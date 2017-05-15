package org.camokatuk.madcomposer.music;

import java.util.Map;
import java.util.TreeMap;

public class Bar<N>
{
	private final Map<Duration, N> notesByStart = new TreeMap<>();

	public void addNote(N note, Duration start)
	{
		notesByStart.put(start, note);
	}

	public Map<Duration, N> getNotesByStart()
	{
		return notesByStart;
	}
}
