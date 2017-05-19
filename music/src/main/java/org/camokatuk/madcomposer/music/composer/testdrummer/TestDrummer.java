package org.camokatuk.madcomposer.music.composer.testdrummer;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

		final String phrase1 = "K..K..S..K..";
		final String phrase2 = "K....K..S..";
		final String phrase3 = "K...S...K..";
		final String phrase4 = "S...K...S..";
		final String phrase5 = "S.S..S.";

		List<String> phrases = new LinkedList<>(Arrays.asList(phrase1, phrase2, phrase3, phrase4, phrase5));
		Collections.shuffle(phrases);
		Phrase phrase = Phrase.parse(phrases.stream().collect(Collectors.joining()));
		trackIdToBar.put(0, phrase.toOneBar(drumHitMap));

		return trackIdToBar;
	}
}
