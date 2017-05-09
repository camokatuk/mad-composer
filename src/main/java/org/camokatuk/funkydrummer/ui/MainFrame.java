package org.camokatuk.funkydrummer.ui;

import org.camokatuk.funkydrummer.control.PlaybackListener;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame
{
    public MainFrame() throws HeadlessException
    {
        this.addWindowListener(new FunkyWindowAdapter());
        this.addPlaybackControls();
        this.pack();
    }

    private void addPlaybackControls()
    {
        JButton button = new JButton("Test");
        button.addActionListener(new PlaybackListener());
        this.getContentPane().add(button);
    }
}
