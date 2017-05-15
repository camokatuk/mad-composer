package org.camokatuk.madcomposer.engine.control;

import org.camokatuk.madcomposer.engine.Engine;

public class PlaybackController
{
	private Engine engine;

	public PlaybackController(Engine engine)
	{
		this.engine = engine;
	}

	public void setBpm(int bpm)
	{
		this.engine.getMidiMonster().setBpm(bpm);
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
