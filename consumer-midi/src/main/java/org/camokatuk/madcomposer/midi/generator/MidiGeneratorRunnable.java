package org.camokatuk.madcomposer.midi.generator;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.camokatuk.madcomposer.midi.MidiNote;
import org.camokatuk.madcomposer.music.Duration;

/**/ class MidiGeneratorRunnable implements java.lang.Runnable
{
	public static final int PPQ = 96;
	public static final int TICKS_PER_BAR = PPQ * 4;
	private static final Logger LOGGER = LogManager.getLogger(MidiGenerator.class);

	private final Receiver receiver;
	private final int bpm;

	private final AtomicBoolean running = new AtomicBoolean(false);
	private volatile int delay = 0;

	private Sequencer sequencer = null;

	public MidiGeneratorRunnable(Receiver receiver, int bpm)
	{
		this.receiver = receiver;
		this.bpm = bpm;
	}

	@Override
	public void run()
	{
		this.start();
		try
		{
			LOGGER.info("Starting thread at " + bpm + " bpm");

			sequencer = MidiSystem.getSequencer(false); // true triggers piano

			Sequence sequence = new Sequence(Sequence.PPQ, PPQ);

			Track newTrack = sequence.createTrack();
			ShortMessage endOfTrackMessage = new ShortMessage();
			endOfTrackMessage.setMessage(ShortMessage.END_OF_EXCLUSIVE);
			// this fucker actually stops after playing a sequence, so let's have a stop event at Long.MAX_VALUE ticks
			newTrack.add(new MidiEvent(endOfTrackMessage, Long.MAX_VALUE));

			sequencer.setSequence(sequence);
			sequencer.getTransmitter().setReceiver(receiver);
			sequencer.open();
			sequencer.setTempoInBPM(bpm);
			sequencer.setMasterSyncMode(Sequencer.SyncMode.MIDI_SYNC);
			sequencer.start(); // instantiates another thread

			while (running.get())
			{
				//				while (!eventQueue.isEmpty())
				//				{
				//					try
				//					{
				//						MidiEvent note = eventQueue.take();
				//						LOGGER.info("New note " + note);
				//						injectEventNow(sequencer, note, 0); // TODO not now
				//					}
				//					catch (InvalidMidiDataException e)
				//					{
				//						LOGGER.error(e);
				//					}
				//				}
			}

			LOGGER.info("Stopping thread");
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
	}

	public void pushNoteToNextBar(MidiNote midiNote, int trackId, Duration relativeStart) throws InvalidMidiDataException
	{
		ShortMessage onMessage = new ShortMessage();
		onMessage.setMessage(ShortMessage.NOTE_ON, 0, midiNote.getPitch().getMidiIndex(), midiNote.getVelocity());

		ShortMessage offMessage = new ShortMessage();
		offMessage.setMessage(ShortMessage.NOTE_OFF, 0, midiNote.getPitch().getMidiIndex(), 0);

		long currentBar = sequencer.getTickPosition() / TICKS_PER_BAR + 1;
		long nextBarStartTick = currentBar * TICKS_PER_BAR;

		LOGGER.info("Current bar: " + currentBar);
		long absoluteStartInTicks = nextBarStartTick + delay + (TICKS_PER_BAR / relativeStart.getBase() * relativeStart.getQuant());

		LOGGER.info("Scheduling note at " + absoluteStartInTicks + " - " + (absoluteStartInTicks + midiNote.getDurationTicks()));
		sequencer.getSequence().getTracks()[trackId].add(new MidiEvent(onMessage, absoluteStartInTicks));
		sequencer.getSequence().getTracks()[trackId].add(new MidiEvent(offMessage, absoluteStartInTicks + midiNote.getDurationTicks()));
	}

	public boolean isRunning()
	{
		return running.get();
	}

	public void start()
	{
		this.running.set(true);
	}

	public void stop()
	{
		this.running.set(false);
	}
}
