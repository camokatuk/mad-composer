package org.camokatuk.madcomposer.engine.control;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.camokatuk.madcomposer.engine.Engine;

public class EngineControlRoom
{
	private static final Logger LOGGER = LogManager.getLogger(EngineControlRoom.class);

	private final Engine engine;

	public EngineControlRoom(Engine engine)
	{
		this.engine = engine;
	}

	public MidiTransportPlayerController getMidiSpammerController()
	{
		return new MidiTransportPlayerController(engine.getMidiTransport());
	}

	public PlaybackController getPlaybackController()
	{
		return new PlaybackController(engine);
	}

	public StateMonitor getStateMonitor()
	{
		return new StateMonitor(engine);
	}

	public void startEngine()
	{
		this.engine.initialize();
	}

	public void stopEngine()
	{
		engine.shutdown();
	}
}
