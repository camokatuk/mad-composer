package org.camokatuk.madcomposer.engine.control;

import java.util.Collection;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;

import org.camokatuk.madcomposer.midi.MidiSpammer;

public class MidiSpammerController
{
	private final MidiSpammer midiSpammer;

	public MidiSpammerController(MidiSpammer midiSpammer)
	{
		this.midiSpammer = midiSpammer;
	}

	//    public List<String> listMidiDevices()
	//    {
	//        return midiSpammer.getDeviceManager().getOutputDevices().keySet().stream().sorted().collect(Collectors.toList());
	//    }

	public Collection<MidiDevice> listMidiDevices()
	{
		return midiSpammer.getDeviceManager().getOutputDevices().values();
	}

	public MidiDevice getDefaultDevice()
	{
		return midiSpammer.getMidiGenerator().getCurrentDevice();
	}

	public void switchDeviceTo(MidiDevice selectedItem)
	{
		try
		{
			midiSpammer.switchDeviceTo(selectedItem);
		}
		catch (MidiUnavailableException e)
		{
			// TODO logger / user message
			e.printStackTrace();
		}
	}
}
