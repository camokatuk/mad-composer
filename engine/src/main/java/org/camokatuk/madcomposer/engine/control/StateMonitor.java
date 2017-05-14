package org.camokatuk.madcomposer.engine.control;

import org.camokatuk.madcomposer.engine.Engine;

public class StateMonitor
{
	private Engine engine;

	public StateMonitor(Engine engine)
	{
		this.engine = engine;
	}

	// might involve some realtime multithread stuff
	public int getBpm()
	{
		return engine.getMidiPlayer().getMidiGenerator().getBpm();
	}
}
