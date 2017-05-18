package org.camokatuk.madcomposer.engine.control;

import org.camokatuk.madcomposer.engine.MadEngine;

public class StateMonitor
{
	private MadEngine engine;

	public StateMonitor(MadEngine engine)
	{
		this.engine = engine;
	}

	// might involve some realtime multithread stuff
	public int getBpm()
	{
		return engine.getBpm();
	}
}
