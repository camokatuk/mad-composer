package org.camokatuk.madcomposer.engine.midi;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.camokatuk.madcomposer.engine.TimeKeeper;

public class MidiMonster implements TimeKeeper
{
	public static final int PPQ = 96;
	public static final int TICKS_PER_BAR = PPQ * 4;

	private static final Logger LOGGER = LogManager.getLogger(MidiMonster.class);

	private MidiTransport midiTransport;

	// logic
	private Sequence sequence = null;
	private Thread sequencerThread = null;
	private AtomicBoolean running = new AtomicBoolean(false);
	private AtomicLong currentBar = new AtomicLong(0);
	private AtomicLong lastPopulatedBar = new AtomicLong(0);

	private int bpm;

	public MidiMonster(MidiTransport midiTransport)
	{
		this.midiTransport = midiTransport;
		this.setBpm(167);
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

	public synchronized void scheduleNoteNextBar(int trackId, MidiNote midiNote, long relativeStartTicks)
	{
		try
		{
			ShortMessage onMessage = new ShortMessage();
			onMessage.setMessage(ShortMessage.NOTE_ON, 0, midiNote.getPitch().getMidiIndex(), midiNote.getVelocity());

			ShortMessage offMessage = new ShortMessage();
			offMessage.setMessage(ShortMessage.NOTE_OFF, 0, midiNote.getPitch().getMidiIndex(), 0);

			long nextBarStartTick = currentBar.get() * TICKS_PER_BAR;
			long absoluteStartInTicks = nextBarStartTick + relativeStartTicks;

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
		if (midiTransport.getReceiver() == null)
		{
			throw new RuntimeException("Transport not initialized");
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
				while (running.get() && lastPopulatedBar.get() == 0)
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
		sequencer.getTransmitter().setReceiver(midiTransport.getReceiver());
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

	public int getBpm()
	{
		return bpm;
	}

	public void setBpm(int bpm)
	{
		this.bpm = bpm;
	}
}
