package org.camokatuk.madcomposer.engine.control;

import java.util.List;
import java.util.stream.Collectors;

import javax.sound.midi.MidiDevice;

import org.camokatuk.madcomposer.engine.midi.MidiInstrument;

public class MidiPlayerController
{
	private final MidiInstrument midiInstrument;

	public MidiPlayerController(MidiInstrument midiInstrument)
	{
		this.midiInstrument = midiInstrument;
	}

	public List<MidiDevice> listMidiDevices()
	{
		return midiInstrument.getDeviceManager().getOutputDevices().values().stream()
			.sorted((d1, d2) -> d1.getDeviceInfo().getName().compareTo(d2.getDeviceInfo().getName())).collect(Collectors.toList());
	}

	public MidiDevice getCurrentDevice()
	{
		return midiInstrument.getCurrentDevice();
	}

	public boolean trySwitchingDeviceTo(MidiDevice selectedItem)
	{
		return midiInstrument.trySwitchingDeviceTo(selectedItem);
	}
}
