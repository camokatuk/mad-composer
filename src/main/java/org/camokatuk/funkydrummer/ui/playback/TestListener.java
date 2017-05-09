package org.camokatuk.funkydrummer.ui.playback;

import org.camokatuk.funkydrummer.control.EngineControlRoom;
import org.camokatuk.funkydrummer.control.playback.PlaybackController;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TestListener implements ActionListener
{
    private final PlaybackController playbackController;

    public TestListener(EngineControlRoom controlRoom)
    {
        this.playbackController = controlRoom.getPlaybackController();
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        this.playbackController.test();
    }
}
