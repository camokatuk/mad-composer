package org.camokatuk.madcomposer.midi;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;

import org.camokatuk.madcomposer.midi.generator.MidiGenerator;
import org.camokatuk.madcomposer.music.Bar;
import org.camokatuk.madcomposer.music.ComposerListener;

public class MidiPlayer implements ComposerListener
{
	private static final String FALLBACK_DEVICE = "Microsoft GS Wavetable Synth";
	private static final String DEFAULT_DEVICE = "LoopBe Internal MIDI";

	private final MidiDeviceManager deviceManager = new MidiDeviceManager();
	private final MidiGenerator midiGenerator = new MidiGenerator();

	public void initialize() throws MidiUnavailableException
	{
		deviceManager.rescanDevices();
		midiGenerator.initialize(deviceManager.getOutputDevice(DEFAULT_DEVICE), deviceManager.getOutputDevice(FALLBACK_DEVICE));
	}

	public MidiGenerator getMidiGenerator()
	{
		return midiGenerator;
	}

	public MidiDeviceManager getDeviceManager()
	{
		return deviceManager;
	}

	public boolean trySwitchingDeviceTo(MidiDevice device)
	{
		return midiGenerator.trySwitchingDeviceTo(device);
	}

	@Override
	public void consumeBar(Bar bar, int trackId)
	{
		midiGenerator.scheduleBar(bar, trackId);
	}

}
