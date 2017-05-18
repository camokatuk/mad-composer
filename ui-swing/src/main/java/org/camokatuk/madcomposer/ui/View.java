package org.camokatuk.madcomposer.ui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import org.camokatuk.madcomposer.engine.control.ApplicationControlRoom;

public class View
{
	private final ApplicationControlRoom applicationControlRoom;
	private final MainFrame mainFrame;

	public View(ApplicationControlRoom applicationControlRoom)
	{
		this.applicationControlRoom = applicationControlRoom;
		this.mainFrame = new MainFrame(applicationControlRoom);
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
		applicationControlRoom.stopEngine();
		mainFrame.dispose();
	}

}
