package org.camokatuk.madcomposer.controlroom.playback;

import org.camokatuk.madcomposer.midi.Engine;

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
