package org.camokatuk.madcomposer.engine.sound.midi;

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
import org.camokatuk.madcomposer.engine.sound.SoundEngine;

public class MidiSoundEngine implements SoundEngine<MidiSoundEngineEvent>
{
	private static final Logger LOGGER = LogManager.getLogger(MidiSoundEngine.class);

	private MidiTransport midiTransport;

	// logic
	private Sequence sequence = null;
	private Thread sequencerThread = null;
	private AtomicBoolean running = new AtomicBoolean(false);
	private AtomicLong currentBar = new AtomicLong(0);
	private AtomicLong lastPopulatedBar = new AtomicLong(0);

	public MidiSoundEngine(MidiTransport midiTransport)
	{
		this.midiTransport = midiTransport;
	}

	@Override
	public void initialize()
	{
		LOGGER.info("Initializing midi engine...");
		this.midiTransport.initialize();
	}

	@Override
	public void shutdown()
	{
		LOGGER.info("Shutting down midi engine...");
		this.midiTransport.shutdown();
	}

	@Override
	public void notifyAllInstrumentsPlayed()
	{
		LOGGER.info(lastPopulatedBar.incrementAndGet() + " bar is populated");
	}

	@Override
	public boolean isReadyToConsume()
	{
		return lastPopulatedBar.get() == currentBar.get();
	}

	@Override
	public synchronized void consumeEvent(MidiSoundEngineEvent event)
	{
		MidiNote midiNote = event.getEvent();
		Long nextBarOffsetTicks = event.getStart();
		Integer trackId = event.getInstrumentId();

		try
		{
			ShortMessage onMessage = new ShortMessage();
			onMessage.setMessage(ShortMessage.NOTE_ON, 0, midiNote.getPitch().getMidiIndex(), midiNote.getVelocity());

			ShortMessage offMessage = new ShortMessage();
			offMessage.setMessage(ShortMessage.NOTE_OFF, 0, midiNote.getPitch().getMidiIndex(), 0);

			long nextBarStartTick = currentBar.get() * MidiConstants.TICKS_PER_BAR;
			long absoluteStartInTicks = nextBarStartTick + nextBarOffsetTicks;

			LOGGER.debug("Scheduling note at " + absoluteStartInTicks + " - " + (absoluteStartInTicks + midiNote.getDurationTicks()));
			sequence.getTracks()[trackId].add(new MidiEvent(onMessage, absoluteStartInTicks));
			sequence.getTracks()[trackId].add(new MidiEvent(offMessage, absoluteStartInTicks + midiNote.getDurationTicks()));
		}
		catch (InvalidMidiDataException e)
		{
			LOGGER.error("Can't play note: " + midiNote.getPitch() + " at " + trackId + " track", e);
		}
	}

	@Override
	public synchronized void start(int numberOfTracks, int bpm)
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
			sequencer = initializeSequencer(sequence, bpm);
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
					long actualCurrentBar = sequencer.getTickPosition() / MidiConstants.TICKS_PER_BAR + 1;
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
		Sequence sequence = new Sequence(Sequence.PPQ, MidiConstants.PPQ);
		// this fucker actually stops after playing a sequence, so let's have a stopSequencer event at Long.MAX_VALUE ticks
		Track newTrack = sequence.createTrack();
		ShortMessage endOfTrackMessage = new ShortMessage();
		endOfTrackMessage.setMessage(ShortMessage.END_OF_EXCLUSIVE);
		newTrack.add(new MidiEvent(endOfTrackMessage, Long.MAX_VALUE));
		// initialize the rest of the tracks
		IntStream.range(1, numberOfTracks).forEach(i -> sequence.createTrack());
		return sequence;
	}

	private Sequencer initializeSequencer(Sequence sequence, int bpm) throws MidiUnavailableException, InvalidMidiDataException
	{
		Sequencer sequencer = MidiSystem.getSequencer(false); // true triggers piano
		sequencer.setSequence(sequence);
		sequencer.getTransmitter().setReceiver(midiTransport.getReceiver());
		sequencer.open();
		sequencer.setTempoInBPM(bpm);
		sequencer.setMasterSyncMode(Sequencer.SyncMode.MIDI_SYNC);
		LOGGER.info("Initialized sequencer at " + bpm + " bpm, " + MidiConstants.PPQ + " ppq");
		return sequencer;
	}

	@Override
	public synchronized void pause()
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

}
