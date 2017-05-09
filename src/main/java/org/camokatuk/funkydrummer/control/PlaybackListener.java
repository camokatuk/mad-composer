package org.camokatuk.funkydrummer.control;

import org.camokatuk.funkydrummer.model.midi.MidiScheisse;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PlaybackListener implements ActionListener
{
    private final MidiScheisse midiScheisse;

    public PlaybackListener()
    {
        this.midiScheisse = new MidiScheisse();
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        this.midiScheisse.test();
    }
}
