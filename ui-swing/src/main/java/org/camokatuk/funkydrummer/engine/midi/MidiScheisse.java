package org.camokatuk.funkydrummer.engine.midi;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;

public class MidiScheisse
{
	private static final String FALLBACK_DEVICE = "Microsoft GS Wavetable Synth";
	private static final String LOOPBE_DEVICE = "LoopBe Internal MIDI";
	private MidiDevice outputDevice;
	private Receiver receiver = null;

    private AtomicBoolean stillRunning = new AtomicBoolean(true);

    public MidiScheisse()
    {

    }

    public void initialize()
    {
        try
        {
	        this.outputDevice = getDevice(LOOPBE_DEVICE);
	        this.outputDevice.open();
	        this.receiver = outputDevice.getReceiver();
        }
        catch (MidiUnavailableException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void test(int bpm)
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

            sequencer.setSequence(MidiSystem.getSequence(MidiScheisse.class.getResourceAsStream("/X2.mid")));
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

    public void destroy()
    {
        this.receiver.close();
        this.outputDevice.close();
    }

    private static MidiDevice getDevice(String name)
    {
        MidiDevice.Info info = DeviceManager.getMidiDeviceInfo(name, true);

	    if (info == null)
	    {
		    // falling back to default ms midi synth
		    info = DeviceManager.getMidiDeviceInfo(FALLBACK_DEVICE, true);
	    }

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