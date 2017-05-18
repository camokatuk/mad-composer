package org.camokatuk.madcomposer.engine.control;

import org.camokatuk.madcomposer.engine.MadEngine;

public class PlaybackController
{
	private MadEngine engine;

	public PlaybackController(MadEngine engine)
	{
		this.engine = engine;
	}

	public void setBpm(int bpm)
	{
		this.engine.setBpm(bpm);
	}

	public void start()
	{
		this.engine.start();
	}

	public void stop()
	{
		this.engine.pause();
	}

	public void trigger()
	{
		System.out.println("I'm disabled");
	}
}
