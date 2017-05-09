package org.camokatuk.madcomposer.engine;

import javax.sound.midi.MidiUnavailableException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.camokatuk.madcomposer.midi.MidiSpammer;

public class Engine
{
	private static final Logger LOGGER = LogManager.getLogger(Engine.class);

	private MidiSpammer midiSpammer = new MidiSpammer();

	public void start()
	{
		LOGGER.info("Starting engine...");
		try
		{
			midiSpammer.initialize();
		}
		catch (MidiUnavailableException e)
		{
			throw new EngineFatalException("Failed to start midi spammer even with default device", e);
		}
		LOGGER.info("Engine started");
	}

	public void stop()
	{
		LOGGER.info("Stopping engine...");
		midiSpammer.getMidiGenerator().destroy();
	}

	public MidiSpammer getMidiSpammer()
	{
		return midiSpammer;
	}
}
