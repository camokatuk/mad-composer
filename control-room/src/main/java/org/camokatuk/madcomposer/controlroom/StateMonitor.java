package org.camokatuk.madcomposer.controlroom;

import org.camokatuk.madcomposer.midi.Engine;

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
