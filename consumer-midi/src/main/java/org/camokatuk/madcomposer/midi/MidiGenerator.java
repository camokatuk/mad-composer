package org.camokatuk.madcomposer.midi;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MidiGenerator
{
	private static final Logger LOGGER = LogManager.getLogger(MidiGenerator.class);

	private MidiDevice currentDevice = null;
	private Receiver receiver = null;

	private int bpm = 167;

	private AtomicBoolean stillRunning = new AtomicBoolean(true);

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
		this.stopWorkingWithCurrentDevice();
	}

	public void test()
	{
		stillRunning.set(true);
		try
		{
			if (this.receiver == null)
			{
				throw new RuntimeException("Not initialized");
			}

			Sequencer sequencer = MidiSystem.getSequencer(false); // true triggers piano

			int END_OF_TRACK_MESSAGE = 47;
			sequencer.addMetaEventListener(meta -> {
				if (meta.getType() == END_OF_TRACK_MESSAGE)
				{
					stillRunning.set(false);
				}
			});

			sequencer.setSequence(MidiSystem.getSequence(MidiGenerator.class.getResourceAsStream("/X2.mid")));
			sequencer.getTransmitter().setReceiver(this.receiver);
			sequencer.open();
			sequencer.setTempoInBPM(bpm);
			sequencer.setLoopStartPoint(0);
			sequencer.setLoopEndPoint(-1);
			sequencer.setLoopCount(0);
			sequencer.setMasterSyncMode(Sequencer.SyncMode.MIDI_SYNC);
			sequencer.start();

			//            playNote(receiver, new MidiNote(new Note("C", 1), 127, 1000));
			//            playNote(receiver, new MidiNote(new Note("C", 1), 127, 1000));
			//            playNote(receiver, new MidiNote(new Note("C", 1), 127, 1000));

			while (stillRunning.get())
			{

			}
			sequencer.stop();
			//            receiver.close();
			sequencer.close();
		}
		catch (MidiUnavailableException e)
		{
			throw new RuntimeException(e);
		}
		catch (InvalidMidiDataException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	public void playNote(MidiNote midiNote)
	{
		int nChannel = 1;

		int nKey = midiNote.getNote().getMidiIndex();
		int nVelocity = midiNote.getVelocity();
		int nDuration = midiNote.getDurationMillis();

		out("Receiver: " + receiver);
		/*	Here, we prepare the MIDI messages to send.
		    Obviously, one is for turning the key on and
			one for turning it off.
		*/
		ShortMessage onMessage = null;
		ShortMessage offMessage = null;
		try
		{
			onMessage = new ShortMessage();
			offMessage = new ShortMessage();
			onMessage.setMessage(ShortMessage.NOTE_ON, nChannel, nKey, nVelocity);
			offMessage.setMessage(ShortMessage.NOTE_OFF, nChannel, nKey, 0);

			out("On Msg: " + onMessage.getStatus() + " " + onMessage.getData1() + " " + onMessage.getData2());
			out("Off Msg: " + offMessage.getStatus() + " " + offMessage.getData1() + " " + offMessage.getData2());
		}
		catch (InvalidMidiDataException e)
		{
			e.printStackTrace();
		}

		/*
		 *	Turn the note on
		 */
		out("sending on message...");
		receiver.send(onMessage, -1);
		out("...sent");

		/*
		 *	Wait for the specified amount of time
		 *	(the duration of the note).
		 */
		try
		{
			Thread.sleep(nDuration);
		}
		catch (InterruptedException e)
		{
			throw new RuntimeException(e);
		}

		/*
		 *	Turn the note off.
		 */
		out("sending off message...");
		receiver.send(offMessage, -1);
		out("...sent");
	}

	public int getBpm()
	{
		return bpm;
	}

	public void setBpm(int bpm)
	{
		this.bpm = bpm;
	}

	private static void out(String strMessage)
	{
		System.out.println(strMessage);
	}

	public MidiDevice getCurrentDevice()
	{
		return currentDevice;
	}
}

/***
 * MidiGenerator.java
 ***/