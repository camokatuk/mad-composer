package org.camokatuk.madcomposer.music.performer;

import org.camokatuk.madcomposer.music.Bar;
import org.camokatuk.madcomposer.music.InterpretedNote;
import org.camokatuk.madcomposer.music.MusicalDuration;
import org.camokatuk.madcomposer.music.Personality;
import org.camokatuk.madcomposer.music.PlayableNote;
import org.camokatuk.madcomposer.music.Shuffle;

public class Robot implements Personality
{
	@Override
	public Bar<MusicalDuration, InterpretedNote> interpret(Bar<MusicalDuration, PlayableNote> writtenBar)
	{
		Bar<MusicalDuration, InterpretedNote> interpretedBar = new Bar<>();
		writtenBar.traverse(playableNoteEvent ->
		{
			interpretedBar.addEvent(new InterpretedNote(playableNoteEvent.getEvent(), 127, Shuffle.ON_TIME), playableNoteEvent.getOffset());
		});
		return interpretedBar;
	}
}
