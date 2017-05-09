package org.camokatuk.funkydrummer;

import org.camokatuk.funkydrummer.ui.MainFrame;

public class Application
{
    public static void main(String[] sd)
    {
        javax.swing.SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setLocationRelativeTo(null);
            mainFrame.setVisible(true);
        });
    }
}
