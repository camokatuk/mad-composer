package org.camokatuk.madcomposer.music.performer;

import java.util.Map;

import org.camokatuk.madcomposer.music.Bar;
import org.camokatuk.madcomposer.music.Duration;
import org.camokatuk.madcomposer.music.InterpretedNote;
import org.camokatuk.madcomposer.music.Note;
import org.camokatuk.madcomposer.music.Personality;
import org.camokatuk.madcomposer.music.Shuffle;

public class Robot implements Personality
{
	@Override
	public Bar<InterpretedNote> interpret(Bar<Note> writtenBar)
	{
		Bar<InterpretedNote> interpretedBar = new Bar<>();
		for (Map.Entry<Duration, Note> barEntry : writtenBar.getNotesByStart().entrySet())
		{
			interpretedBar.addNote(new InterpretedNote(barEntry.getValue(), 127, Shuffle.ON_TIME), barEntry.getKey());
		}
		return interpretedBar;
	}
}
