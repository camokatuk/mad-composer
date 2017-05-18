package org.camokatuk.madcomposer.music.composer;

import java.util.HashMap;
import java.util.Map;

import org.camokatuk.madcomposer.music.Bar;
import org.camokatuk.madcomposer.music.Composer;
import org.camokatuk.madcomposer.music.MusicalDuration;
import org.camokatuk.madcomposer.music.Pitch;
import org.camokatuk.madcomposer.music.PlayableNote;

public class Paradox implements Composer
{
	@Override
	public Map<Integer, Bar<MusicalDuration, PlayableNote>> writeNextBar()
	{
		Map<Integer, Bar<MusicalDuration, PlayableNote>> trackIdToBar = new HashMap<>();

		Bar<MusicalDuration, PlayableNote> bar = new Bar<>();
		bar.addEvent(new PlayableNote(new Pitch("C", 1), MusicalDuration.quarter(1)), MusicalDuration.quarter(0));
		bar.addEvent(new PlayableNote(new Pitch("D", 1), MusicalDuration.quarter(1)), MusicalDuration.quarter(0));
		bar.addEvent(new PlayableNote(new Pitch("C", 1), MusicalDuration.quarter(1)), MusicalDuration.quarter(1));
		bar.addEvent(new PlayableNote(new Pitch("C", 1), MusicalDuration.quarter(1)), MusicalDuration.quarter(2));
		bar.addEvent(new PlayableNote(new Pitch("C", 1), MusicalDuration.quarter(1)), MusicalDuration.quarter(3));
		trackIdToBar.put(0, bar);

		return trackIdToBar;
	}
}
