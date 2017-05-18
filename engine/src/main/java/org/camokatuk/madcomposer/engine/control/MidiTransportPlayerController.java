package org.camokatuk.madcomposer.engine.control;

import java.util.List;
import java.util.stream.Collectors;

import javax.sound.midi.MidiDevice;

import org.camokatuk.madcomposer.engine.sound.midi.MidiTransport;

public class MidiTransportPlayerController
{
	private final MidiTransport midiTransport;

	public MidiTransportPlayerController(MidiTransport midiTransport)
	{
		this.midiTransport = midiTransport;
	}

	public List<MidiDevice> listMidiDevices()
	{
		return midiTransport.getDeviceManager().getOutputDevices().values().stream()
			.sorted((d1, d2) -> d1.getDeviceInfo().getName().compareTo(d2.getDeviceInfo().getName())).collect(Collectors.toList());
	}

	public MidiDevice getCurrentDevice()
	{
		return midiTransport.getCurrentDevice();
	}

	public boolean trySwitchingDeviceTo(MidiDevice selectedItem)
	{
		return midiTransport.trySwitchingDeviceTo(selectedItem);
	}
}
