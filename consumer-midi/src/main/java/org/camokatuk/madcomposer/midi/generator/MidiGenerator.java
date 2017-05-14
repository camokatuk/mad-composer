package org.camokatuk.madcomposer.midi.generator;

import java.util.Map;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.camokatuk.madcomposer.midi.MidiNote;
import org.camokatuk.madcomposer.music.Bar;
import org.camokatuk.madcomposer.music.Duration;
import org.camokatuk.madcomposer.music.Note;

public class MidiGenerator
{
	private static final Logger LOGGER = LogManager.getLogger(MidiGenerator.class);

	private MidiDevice currentDevice = null;
	private Receiver receiver = null;

	private MidiGeneratorRunnable activeRunnable;
	private Thread activeThread;

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
		if (receiver == null)
		{
			throw new RuntimeException("Not initialized");
		}

		this.stopGenerating();

		activeRunnable = new MidiGeneratorRunnable(receiver, bpm);
		activeThread = new Thread(activeRunnable);
		activeThread.start();
	}

	public synchronized void stopGenerating()
	{
		if (activeThread != null)
		{
			activeRunnable.stop();

			try
			{
				activeThread.join();
			}
			catch (InterruptedException e)
			{
				LOGGER.warn("Generating thread was interrupted, but I don't give a fuck :P");
			}
		}
	}

	public void scheduleBar(Bar bar, int trackId)
	{
		try
		{
			for (Map.Entry<Duration, Note> noteEntry : bar.getNotesByStart().entrySet())
			{
				Note note = noteEntry.getValue();
				LOGGER.info("Scheduling " + note.getPitch() + " at " + durationInTicks(note.getDuration()));
				MidiNote midiNote = new MidiNote(note.getPitch(), 127, durationInTicks(note.getDuration()));
				activeRunnable.pushNoteToNextBar(midiNote, trackId, noteEntry.getKey());
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Unable to schedule bar", e);
		}
	}

	private long durationInTicks(Duration duration)
	{
		return (MidiGeneratorRunnable.TICKS_PER_BAR / duration.getBase() * duration.getQuant());
	}

	public MidiGeneratorRunnable getRunnable()
	{
		return activeRunnable;
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

	public static int getPPQ()
	{
		return MidiGeneratorRunnable.PPQ;
	}
}

/***
 * MidiGenerator.java
 ***/