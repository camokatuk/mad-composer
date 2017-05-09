package org.camokatuk.funkydrummer.ui;

import org.camokatuk.funkydrummer.control.EngineControlRoom;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class View
{
    private EngineControlRoom engineControlRoom;

    public View(EngineControlRoom engineControlRoom)
    {
        this.engineControlRoom = engineControlRoom;
    }

    public void initialize()
    {
        javax.swing.SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame(engineControlRoom);
            mainFrame.setLocationRelativeTo(null);
            mainFrame.setVisible(true);
            mainFrame.addWindowListener(new WindowAdapter()
            {
                @Override
                public void windowClosing(WindowEvent e)
                {
                    System.out.println("Closed");
                    engineControlRoom.stopEngine();
                    mainFrame.dispose();
                }
            });
        });
    }
}
