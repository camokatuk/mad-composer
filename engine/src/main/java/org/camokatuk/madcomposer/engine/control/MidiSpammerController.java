package org.camokatuk.madcomposer.engine.control;

import java.util.List;
import java.util.stream.Collectors;

import javax.sound.midi.MidiDevice;

import org.camokatuk.madcomposer.midi.MidiSpammer;

public class MidiSpammerController
{
	private final MidiSpammer midiSpammer;

	public MidiSpammerController(MidiSpammer midiSpammer)
	{
		this.midiSpammer = midiSpammer;
	}

	public List<MidiDevice> listMidiDevices()
	{
		return midiSpammer.getDeviceManager().getOutputDevices().values().stream()
			.sorted((d1, d2) -> d1.getDeviceInfo().getName().compareTo(d2.getDeviceInfo().getName())).collect(Collectors.toList());
	}

	public MidiDevice getCurrentDevice()
	{
		return midiSpammer.getMidiGenerator().getCurrentDevice();
	}

	public boolean trySwitchingDeviceTo(MidiDevice selectedItem)
	{
		return midiSpammer.trySwitchingDeviceTo(selectedItem);
	}
}
