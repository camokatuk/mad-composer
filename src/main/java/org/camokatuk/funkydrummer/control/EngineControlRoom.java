package org.camokatuk.funkydrummer.control;

import org.camokatuk.funkydrummer.control.playback.PlaybackController;
import org.camokatuk.funkydrummer.control.state.StateMonitor;
import org.camokatuk.funkydrummer.engine.Engine;

public class EngineControlRoom
{
    private final Engine engine;

    public EngineControlRoom(Engine engine)
    {
        this.engine = engine;
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
        engine.start();
    }

    public void stopEngine()
    {
        engine.stop();
    }
}
