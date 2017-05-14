package org.camokatuk.madcomposer.music;

import java.util.Map;
import java.util.TreeMap;

public class Bar
{
	private final Map<Duration, Note> notesByStart = new TreeMap<>();

	public void addNote(Pitch pitch, Duration start, Duration duration)
	{
		this.addNote(new Note(pitch, duration), start);
	}

	public void addNote(Note note, Duration start)
	{
		notesByStart.put(start, note);
	}

	public Map<Duration, Note> getNotesByStart()
	{
		return notesByStart;
	}
}
