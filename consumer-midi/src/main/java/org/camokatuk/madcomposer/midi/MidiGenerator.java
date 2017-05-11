package org.camokatuk.madcomposer.midi;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

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

import com.sun.media.sound.MidiUtils;

public class MidiGenerator
{
	private class Runnable implements java.lang.Runnable
	{
		private static final int PPQ = 96;
		private final Receiver receiver;
		private final int bpm;

		public Runnable(Receiver receiver, int bpm)
		{
			this.receiver = receiver;
			this.bpm = bpm;
		}

		@Override
		public void run()
		{
			try
			{

				LOGGER.info("Starting thread at " + bpm + " bpm");

				Sequencer sequencer = MidiSystem.getSequencer(false); // true triggers piano

				Sequence sequence = new Sequence(Sequence.PPQ, PPQ);

				Sequence fileSequence = MidiSystem.getSequence(MidiGenerator.class.getResourceAsStream("/X2.mid"));

				Track track0 = fileSequence.getTracks()[0];
				Track newTrack = sequence.createTrack();
				for (int i = 0; i < track0.size(); i++)
				{
					MidiEvent event = track0.get(i);
					LOGGER.info(event.getMessage());

					if (!MidiUtils.isMetaEndOfTrack(event.getMessage()))
					{
						newTrack.add(event);
					}

				}

				sequencer.setSequence(sequence);
				sequencer.getTransmitter().setReceiver(receiver);
				sequencer.open();
				sequencer.setTempoInBPM(bpm);
				sequencer.setMasterSyncMode(Sequencer.SyncMode.MIDI_SYNC);
				sequencer.start();

				while (running.get())
				{
					while (!noteQueue.isEmpty())
					{
						try
						{
							MidiNote note = noteQueue.take();
							LOGGER.info("New note " + note);
							injectNoteNow(sequencer, note);
						}
						catch (InvalidMidiDataException e)
						{
							e.printStackTrace();
						}
					}
				}

				LOGGER.info("Stopping thread");

				sequencer.stop();
				sequencer.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				// TODO ... release
			}
		}

		private void injectNoteNow(Sequencer sequencer, MidiNote note) throws InvalidMidiDataException
		{
			ShortMessage onMessage = new ShortMessage();
			onMessage.setMessage(ShortMessage.NOTE_ON, 0, note.getNote().getMidiIndex(), note.getVelocity());

			ShortMessage offMessage = new ShortMessage();
			offMessage.setMessage(ShortMessage.NOTE_OFF, 0, note.getNote().getMidiIndex(), 0);

			long cur = sequencer.getTickPosition();
			sequencer.getSequence().getTracks()[0].add(new MidiEvent(onMessage, cur + delay));
			sequencer.getSequence().getTracks()[0].add(new MidiEvent(offMessage, cur + delay + ticksPerMilliseconds(note.getDurationMillis())));
		}

		private long ticksPerMilliseconds(int millis)
		{
			long ticksPerMinute = PPQ * bpm;
			LOGGER.info((ticksPerMinute * millis / 60000));
			return (ticksPerMinute * millis / 60000);
		}
	}

	private static final Logger LOGGER = LogManager.getLogger(MidiGenerator.class);

	private final AtomicBoolean running = new AtomicBoolean(false);
	private volatile int delay = 0;

	private MidiDevice currentDevice = null;
	private Receiver receiver = null;

	private Thread activeThread;
	private BlockingQueue<MidiNote> noteQueue = new LinkedBlockingQueue<>();
	private int bpm = 167;

	public MidiGenerator()
	{

	}

	public void initialize(MidiDevice... devices)
	{
		for (MidiDevice device : devices)
		{
			if (trySwitchingDeviceTo(device))
			{
				break;
			}
		}
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

		LOGGER.info("Checking device: " + newDevice.getDeviceInfo().getName() + "...");
		try
		{
			newDevice.open();
			Receiver newReceiver = newDevice.getReceiver();

			// if we didn't crash yet, new device works, and we can discard current device and reassign fields
			this.stopWorkingWithCurrentDevice();
			this.currentDevice = newDevice;
			this.receiver = newReceiver;
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

	private void stopWorkingWithCurrentDevice()
	{
		if (this.receiver != null)
		{
			this.receiver.close();
			this.receiver = null;
		}
		if (this.currentDevice != null)
		{
			this.currentDevice.close();
			this.currentDevice = null;
		}
	}

	public void destroy()
	{
		this.stopGenerating();
		this.stopWorkingWithCurrentDevice();
	}

	public synchronized void startGenerating()
	{
		if (running.get())
		{
			running.set(false);
			try
			{
				activeThread.wait();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		if (receiver == null)
		{
			throw new RuntimeException("Not initialized");
		}

		activeThread = new Thread(new Runnable(receiver, bpm));
		running.set(true);
		activeThread.start();
	}

	public synchronized void stopGenerating()
	{
		if (activeThread != null)
		{
			running.set(false);
		}
	}

	public void triggerNote(MidiNote note)
	{
		try
		{
			noteQueue.put(note);
		}
		catch (InterruptedException e)
		{
			LOGGER.error("Fuck");
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

	public MidiDevice getCurrentDevice()
	{
		return currentDevice;
	}

	public synchronized void setDelay(int delay)
	{
		this.delay = delay;
	}
}

/***
 * MidiGenerator.java
 ***/