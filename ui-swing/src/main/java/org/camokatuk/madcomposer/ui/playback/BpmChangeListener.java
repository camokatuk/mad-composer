package org.camokatuk.madcomposer.ui.playback;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import org.camokatuk.madcomposer.engine.control.EngineControlRoom;
import org.camokatuk.madcomposer.engine.control.PlaybackController;

public class BpmChangeListener implements DocumentListener
{
	private PlaybackController playbackController;

	public BpmChangeListener(EngineControlRoom controlRoom)
	{
		this.playbackController = controlRoom.getPlaybackController();
	}

	@Override
	public void insertUpdate(DocumentEvent e)
	{
		try
		{
			System.out.println("update");
			playbackController.setBpm(Integer.parseInt(e.getDocument().getText(0, e.getDocument().getLength())));
		}
		catch (BadLocationException e1)
		{
			e1.printStackTrace();
		}
	}

	@Override
	public void removeUpdate(DocumentEvent e)
	{

	}

	@Override
	public void changedUpdate(DocumentEvent e)
	{

	}
}
