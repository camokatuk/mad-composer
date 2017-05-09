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
		this.engine.getMidiSpammer().getMidiGenerator().setBpm(bpm);
	}

	public void test()
	{
		this.engine.getMidiSpammer().getMidiGenerator().test();
	}
}
