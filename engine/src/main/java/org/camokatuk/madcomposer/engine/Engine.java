package org.camokatuk.madcomposer.engine;

import javax.sound.midi.MidiUnavailableException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.camokatuk.madcomposer.midi.MidiPlayer;

public class Engine
{
	private static final Logger LOGGER = LogManager.getLogger(Engine.class);

	private MidiPlayer midiPlayer = new MidiPlayer();

	public void start()
	{
		LOGGER.info("Starting engine...");
		try
		{
			midiPlayer.initialize();
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
		midiPlayer.getMidiGenerator().destroy();
	}

	public MidiPlayer getMidiPlayer()
	{
		return midiPlayer;
	}
}
