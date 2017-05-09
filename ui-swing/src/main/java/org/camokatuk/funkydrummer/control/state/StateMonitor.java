package org.camokatuk.funkydrummer.control.state;

import org.camokatuk.funkydrummer.engine.Engine;

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
        return engine.getBpm();
    }
}
