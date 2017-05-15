package org.camokatuk.madcomposer.engine.midi;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MidiDeviceManager
{
	private static final Logger LOGGER = LogManager.getLogger(MidiDeviceManager.class);

	private Map<String, MidiDevice> inputDevices = new LinkedHashMap<>();
	private Map<String, MidiDevice> outputDevices = new LinkedHashMap<>();

	public void rescanDevices()
	{
		LOGGER.debug("Rescanning devices...");

		inputDevices.clear();
		outputDevices.clear();
		MidiDevice.Info[] deviceInfos = MidiSystem.getMidiDeviceInfo();
		for (int i = 0; i < deviceInfos.length; i++)
		{
			MidiDevice.Info deviceInfo = deviceInfos[i];
			MidiDevice device;
			try
			{
				device = MidiSystem.getMidiDevice(deviceInfo);
			}
			catch (MidiUnavailableException e)
			{
				// skipping for now, maybe add some debug logging later
				continue;
			}

			// allows input
			if (device.getMaxTransmitters() != 0)
			{
				inputDevices.put(deviceInfo.getName(), device);
			}

			// allows output
			if (device.getMaxReceivers() != 0)
			{
				outputDevices.put(deviceInfo.getName(), device);
			}
		}
	}

	public Map<String, MidiDevice> getInputDevices()
	{
		return Collections.unmodifiableMap(inputDevices);
	}

	public Map<String, MidiDevice> getOutputDevices()
	{
		return Collections.unmodifiableMap(outputDevices);
	}

	public MidiDevice getOutputDevice(String name)
	{
		return outputDevices.get(name);
	}
}
