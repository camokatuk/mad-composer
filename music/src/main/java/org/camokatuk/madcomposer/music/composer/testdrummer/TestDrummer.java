package org.camokatuk.madcomposer.music.composer.testdrummer;

import java.util.HashMap;
import java.util.Map;

import org.camokatuk.madcomposer.music.Bar;
import org.camokatuk.madcomposer.music.Composer;
import org.camokatuk.madcomposer.music.MusicalDuration;
import org.camokatuk.madcomposer.music.Pitch;
import org.camokatuk.madcomposer.music.PlayableNote;

public class TestDrummer implements Composer
{
	private static Map<Character, PlayableNote> drumHitMap = new HashMap<>();

	static
	{
		drumHitMap.put('K', new PlayableNote(new Pitch("C", 1), MusicalDuration.sixteenth(1)));
		drumHitMap.put('S', new PlayableNote(new Pitch("D", 1), MusicalDuration.sixteenth(1)));
	}

	@Override
	public Map<Integer, Bar<MusicalDuration, PlayableNote>> writeNextBar()
	{
		Map<Integer, Bar<MusicalDuration, PlayableNote>> trackIdToBar = new HashMap<>();

		Phrase phrase1 = Phrase.parse("K....K..K/S..K.S...K....");
		trackIdToBar.put(0, phrase1.toBar(drumHitMap));

		return trackIdToBar;
	}
}
