package org.camokatuk.funkydrummer.ui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class FunkyWindowAdapter extends WindowAdapter
{

    @Override
    public void windowClosing(WindowEvent e)
    {
        System.out.println("Closed");
        e.getWindow().dispose();
    }
}
