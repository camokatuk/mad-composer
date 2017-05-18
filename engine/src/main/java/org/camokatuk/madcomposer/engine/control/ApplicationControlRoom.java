package org.camokatuk.madcomposer.engine.control;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.camokatuk.madcomposer.engine.MadEngine;
import org.camokatuk.madcomposer.engine.sound.midi.MidiTransport;

public class ApplicationControlRoom
{
	private static final Logger LOGGER = LogManager.getLogger(ApplicationControlRoom.class);

	private final MadEngine engine;
	private MidiTransport midiTransport;

	public ApplicationControlRoom(MadEngine engine, MidiTransport midiTransport)
	{
		this.engine = engine;
		this.midiTransport = midiTransport;
	}

	public MidiTransportPlayerController getMidiSpammerController()
	{
		return new MidiTransportPlayerController(midiTransport);
	}

	public PlaybackController getPlaybackController()
	{
		return new PlaybackController(engine);
	}

	public StateMonitor getStateMonitor()
	{
		return new StateMonitor(engine);
	}

	public void stopEngine()
	{
		engine.shutdown();
	}
}
