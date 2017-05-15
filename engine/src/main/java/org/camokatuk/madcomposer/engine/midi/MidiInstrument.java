package org.camokatuk.madcomposer.engine.midi;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.camokatuk.madcomposer.music.Duration;
import org.camokatuk.madcomposer.music.TimeKeeper;

public class MidiInstrument implements TimeKeeper
{
	public static final int PPQ = 96;
	public static final int TICKS_PER_BAR = PPQ * 4;

	private static final Logger LOGGER = LogManager.getLogger(MidiInstrument.class);

	private static final String FALLBACK_DEVICE = "Microsoft GS Wavetable Synth";
	private static final String DEFAULT_DEVICE = "LoopBe Internal MIDI";

	// hardware
	private final MidiDeviceManager deviceManager = new MidiDeviceManager();
	private MidiDevice currentDevice = null;
	private Receiver receiver = null;

	// logic
	private Sequence sequence = null;
	private Thread sequencerThread = null;
	private AtomicBoolean running = new AtomicBoolean(false);
	private AtomicLong currentBar = new AtomicLong(0);
	private AtomicLong lastPopulatedBar = new AtomicLong(0);

	private int bpm;

	public MidiInstrument()
	{
		this.setBpm(167);
	}

	public void initialize() throws MidiUnavailableException
	{
		deviceManager.rescanDevices();
		this.initialize(deviceManager.getOutputDevice(DEFAULT_DEVICE), deviceManager.getOutputDevice(FALLBACK_DEVICE));
	}

	private void initialize(MidiDevice... devices)
	{
		for (MidiDevice device : devices)
		{
			if (trySwitchingDeviceTo(device))
			{
				break;
			}
		}
	}

	@Override
	public boolean isTimeToPlayNextBar()
	{
		return lastPopulatedBar.get() == currentBar.get();
	}

	public void notifyNextBarIsPopulated()
	{
		LOGGER.info(lastPopulatedBar.incrementAndGet() + " bar is populated");
	}

	public void scheduleNoteNextBar(MidiNote midiNote, int trackId, Duration relativeStart)
	{
		try
		{
			ShortMessage onMessage = new ShortMessage();
			onMessage.setMessage(ShortMessage.NOTE_ON, 0, midiNote.getPitch().getMidiIndex(), midiNote.getVelocity());

			ShortMessage offMessage = new ShortMessage();
			offMessage.setMessage(ShortMessage.NOTE_OFF, 0, midiNote.getPitch().getMidiIndex(), 0);

			long nextBarStartTick = currentBar.get() * TICKS_PER_BAR;

			long absoluteStartInTicks = nextBarStartTick + (TICKS_PER_BAR / relativeStart.getBase() * relativeStart.getQuant());

			LOGGER.info("Scheduling note at " + absoluteStartInTicks + " - " + (absoluteStartInTicks + midiNote.getDurationTicks()));
			sequence.getTracks()[trackId].add(new MidiEvent(onMessage, absoluteStartInTicks));
			sequence.getTracks()[trackId].add(new MidiEvent(offMessage, absoluteStartInTicks + midiNote.getDurationTicks()));
		}
		catch (InvalidMidiDataException e)
		{
			LOGGER.error("Can't play note: " + midiNote.getPitch() + " at " + trackId + " track", e);
		}
	}

	public synchronized void createAndStartSequencer(int numberOfTracks)
	{
		if (receiver == null)
		{
			throw new RuntimeException("Not initialized");
		}

		if (this.running.getAndSet(true))
		{
			throw new IllegalStateException("Midi instrument is already running");
		}

		final Sequencer sequencer;
		try
		{
			this.sequence = this.initializeSequence(numberOfTracks);
			sequencer = initializeSequencer(sequence);
		}
		catch (Exception e)
		{
			LOGGER.error("Failed to initialize sequencer", e);
			throw new RuntimeException(e);
		}

		this.currentBar = new AtomicLong(0);
		this.lastPopulatedBar = new AtomicLong(0);

		sequencerThread = new Thread(() -> {
			try
			{
				while (lastPopulatedBar.get() == 0)
				{
					// waiting for the first bar to get populated
				}

				LOGGER.info("Starting sequencer updater...");
				sequencer.start(); // instantiates another thread

				while (running.get())
				{
					long actualCurrentBar = sequencer.getTickPosition() / TICKS_PER_BAR + 1;
					this.currentBar.set(actualCurrentBar);
				}

				LOGGER.info("Stopping sequencer updater");
			}
			catch (Exception e)
			{
				LOGGER.error(e);
			}
			finally
			{
				if (sequencer != null)
				{
					LOGGER.info("Closing sequencer");
					sequencer.stop();
					sequencer.close();
				}
			}
		});
		sequencerThread.start();
	}

	private Sequence initializeSequence(int numberOfTracks) throws InvalidMidiDataException
	{
		Sequence sequence = new Sequence(Sequence.PPQ, PPQ);
		// this fucker actually stops after playing a sequence, so let's have a stopSequencer event at Long.MAX_VALUE ticks
		Track newTrack = sequence.createTrack();
		ShortMessage endOfTrackMessage = new ShortMessage();
		endOfTrackMessage.setMessage(ShortMessage.END_OF_EXCLUSIVE);
		newTrack.add(new MidiEvent(endOfTrackMessage, Long.MAX_VALUE));
		// initialize the rest of the tracks
		IntStream.range(1, numberOfTracks).forEach(i -> sequence.createTrack());
		return sequence;
	}

	private Sequencer initializeSequencer(Sequence sequence) throws MidiUnavailableException, InvalidMidiDataException
	{
		Sequencer sequencer = MidiSystem.getSequencer(false); // true triggers piano
		sequencer.setSequence(sequence);
		sequencer.getTransmitter().setReceiver(receiver);
		sequencer.open();
		sequencer.setTempoInBPM(bpm);
		sequencer.setMasterSyncMode(Sequencer.SyncMode.MIDI_SYNC);
		LOGGER.info("Initialized sequencer at " + bpm + " bpm, " + PPQ + " ppq");
		return sequencer;
	}

	public synchronized void stopSequencer()
	{
		if (sequencerThread != null)
		{
			if (!this.running.getAndSet(false))
			{
				LOGGER.info("Midi instrument is stopped already");
				return;
			}

			try
			{
				sequencerThread.join();
			}
			catch (InterruptedException e)
			{
				LOGGER.warn("Generating thread was interrupted, but I don't give a fuck :P");
			}
		}
	}

	private void releaseDevice()
	{
		if (this.currentDevice != null)
		{
			this.stopSequencer();

			this.currentDevice.close();
			this.currentDevice = null;

			if (this.receiver != null)
			{
				this.receiver.close();
				this.receiver = null;
			}
		}
	}

	public void shutdown()
	{
		LOGGER.info("Releasing device...");
		this.releaseDevice();
	}

	public MidiDeviceManager getDeviceManager()
	{
		return deviceManager;
	}

	public boolean trySwitchingDeviceTo(MidiDevice newDevice)
	{
		if (newDevice == null)
		{
			return false;
		}

		if (currentDevice == newDevice)
		{
			// just keep using it
			return true;
		}

		this.releaseDevice();

		try
		{
			LOGGER.info("Opening device: " + newDevice.getDeviceInfo().getName() + "...");
			newDevice.open();
			this.currentDevice = newDevice;
			this.receiver = newDevice.getReceiver();
			LOGGER.info(newDevice.getDeviceInfo().getName() + " works!");
		}
		catch (MidiUnavailableException e)
		{
			newDevice.close(); // in case we died after opening new device
			LOGGER.warn("Skipping " + newDevice.getDeviceInfo().getName() + "(" + e.getMessage() + ")");
			return false;
		}

		return true;
	}

	public MidiDevice getCurrentDevice()
	{
		return currentDevice;
	}

	public int getBpm()
	{
		return bpm;
	}

	public void setBpm(int bpm)
	{
		this.bpm = bpm;
	}
}
