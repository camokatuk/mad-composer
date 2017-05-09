package org.camokatuk.funkydrummer.control.playback;

import org.camokatuk.funkydrummer.engine.Engine;

public class PlaybackController
{
    private Engine engine;

    public PlaybackController(Engine engine)
    {
        this.engine = engine;
    }

    public void setBpm(int bpm)
    {
        this.engine.setBpm(bpm);
    }

    public void test()
    {
        this.engine.test();
    }
}
