package org.camokatuk.funkydrummer.ui;

import org.camokatuk.funkydrummer.control.EngineControlRoom;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class View
{
    private final EngineControlRoom engineControlRoom;
    private final MainFrame mainFrame;

    public View(EngineControlRoom engineControlRoom)
    {
        this.engineControlRoom = engineControlRoom;
        this.mainFrame = new MainFrame(engineControlRoom);
    }

    public void initialize()
    {
        javax.swing.SwingUtilities.invokeLater(() -> {
            mainFrame.setLocationRelativeTo(null);
            mainFrame.setVisible(true);
            mainFrame.addWindowListener(new WindowAdapter()
            {
                @Override
                public void windowClosing(WindowEvent e)
                {
                    destroy();
                }
            });
        });
    }

    public void destroy()
    {
        engineControlRoom.stopEngine();
        mainFrame.dispose();
    }

}
