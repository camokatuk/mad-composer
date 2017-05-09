package org.camokatuk.funkydrummer;

import javax.sound.midi.*;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class MidiScheisse
{
    private static final String LOOPBE_DEVICE = "LoopBe Internal MIDI";

    public static void main(String[] args)
    {
        DeviceManager.listDevicesAndExit(false, true);

        try (MidiDevice outputDevice = getDevice(LOOPBE_DEVICE); Receiver receiver = outputDevice.getReceiver())
        {
            outputDevice.open();

            Sequencer sequencer = MidiSystem.getSequencer(true); // true triggers piano

            AtomicBoolean stillRunning = new AtomicBoolean(true);
            int END_OF_TRACK_MESSAGE = 47;
            sequencer.addMetaEventListener(meta -> {
                if (meta.getType() == END_OF_TRACK_MESSAGE)
                {
                    stillRunning.set(false);
                }
            });

            sequencer.setSequence(MidiSystem.getSequence(MidiScheisse.class.getResourceAsStream("/X2.mid")));
            sequencer.getTransmitter().setReceiver(receiver);
            sequencer.open();
            sequencer.setTempoInBPM(167);
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
            receiver.close();
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

    private static MidiDevice getDevice(String name)
    {
        MidiDevice.Info info = DeviceManager.getMidiDeviceInfo(name, true);
        try
        {
            return MidiSystem.getMidiDevice(info);
        }
        catch (MidiUnavailableException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static void playNote(Receiver receiver, MidiNote midiNote)
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


    private static void out(String strMessage)
    {
        System.out.println(strMessage);
    }
}


/***
 * MidiScheisse.java
 ***/