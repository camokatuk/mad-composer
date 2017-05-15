package org.camokatuk.madcomposer.music.composer;

import java.util.HashMap;
import java.util.Map;

import org.camokatuk.madcomposer.music.Bar;
import org.camokatuk.madcomposer.music.Composer;
import org.camokatuk.madcomposer.music.Duration;
import org.camokatuk.madcomposer.music.Note;
import org.camokatuk.madcomposer.music.Pitch;

public class Paradox implements Composer
{
	@Override
	public Map<Integer, Bar<Note>> writeNextBar()
	{
		Map<Integer, Bar<Note>> trackIdToBar = new HashMap<>();

		Bar<Note> bar = new Bar<>();
		bar.addNote(new Note(new Pitch("C", 1), Duration.quarter(1)), Duration.quarter(0));
		bar.addNote(new Note(new Pitch("C", 1), Duration.quarter(1)), Duration.quarter(1));
		bar.addNote(new Note(new Pitch("C", 1), Duration.quarter(1)), Duration.quarter(2));
		bar.addNote(new Note(new Pitch("C", 1), Duration.quarter(1)), Duration.quarter(3));
		trackIdToBar.put(0, bar);

		return trackIdToBar;
	}
}
