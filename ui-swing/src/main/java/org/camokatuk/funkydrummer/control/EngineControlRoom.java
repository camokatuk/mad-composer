package org.camokatuk.funkydrummer.control;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.camokatuk.funkydrummer.control.playback.PlaybackController;
import org.camokatuk.funkydrummer.control.state.StateMonitor;
import org.camokatuk.funkydrummer.engine.Engine;

public class EngineControlRoom
{
    private static final Logger LOGGER = LogManager.getLogger(EngineControlRoom.class);

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
        LOGGER.info("Starting engine...");
        engine.start();
        LOGGER.info("Engine is started");
    }

    public void stopEngine()
    {
        LOGGER.info("Stopping engine...");
        engine.stop();
        LOGGER.info("Engine is stopped");
    }
}
