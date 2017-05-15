package org.camokatuk.madcomposer.engine.midi;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.camokatuk.madcomposer.engine.TransportException;

public class MidiTransport
{
	private static final Logger LOGGER = LogManager.getLogger(MidiTransport.class);

	private static final String FALLBACK_DEVICE = "Microsoft GS Wavetable Synth";
	private static final String DEFAULT_DEVICE = "LoopBe Internal MIDI";

	private final MidiDeviceManager deviceManager = new MidiDeviceManager();
	private MidiDevice currentDevice = null;
	private Receiver receiver = null;

	public void initialize()
	{
		deviceManager.rescanDevices();
		boolean success = this.initialize(deviceManager.getOutputDevice(DEFAULT_DEVICE), deviceManager.getOutputDevice(FALLBACK_DEVICE));
		if (!success)
		{
			throw new TransportException("Failed to initialize midi transport even with default device");
		}
	}

	private boolean initialize(MidiDevice... devices)
	{
		for (MidiDevice device : devices)
		{
			if (trySwitchingDeviceTo(device))
			{
				return true;
			}
		}
		return false;
	}

	private void releaseDevice()
	{
		if (this.currentDevice != null)
		{
			this.currentDevice.close();
			this.currentDevice = null;

			if (this.receiver != null)
			{
				this.receiver.close();
				this.receiver = null;
			}
		}
	}

	public void shutdown()
	{
		LOGGER.info("Releasing device...");
		this.releaseDevice();
		LOGGER.info("Device released");
	}

	public MidiDeviceManager getDeviceManager()
	{
		return deviceManager;
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

		this.releaseDevice();

		try
		{
			LOGGER.info("Opening device: " + newDevice.getDeviceInfo().getName() + "...");
			newDevice.open();
			this.currentDevice = newDevice;
			this.receiver = newDevice.getReceiver();
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

	public MidiDevice getCurrentDevice()
	{
		return currentDevice;
	}

	public Receiver getReceiver()
	{
		return receiver;
	}
}
