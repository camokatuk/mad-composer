package org.camokatuk.madcomposer.music.composer.testdrummer;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.camokatuk.madcomposer.music.Bar;
import org.camokatuk.madcomposer.music.Composer;
import org.camokatuk.madcomposer.music.MusicalDuration;
import org.camokatuk.madcomposer.music.Pitch;
import org.camokatuk.madcomposer.music.PlayableNote;

public class TestDrummer implements Composer
{
	private static Map<Character, PlayableNote> drumHitMap = new HashMap<>();
	private final Queue<Phrase> phraseQueue = new LinkedList<>();

	static
	{
		drumHitMap.put('K', new PlayableNote(new Pitch("C", 1), MusicalDuration.sixteenth(1)));
		drumHitMap.put('S', new PlayableNote(new Pitch("D", 1), MusicalDuration.sixteenth(1)));
		drumHitMap.put('H', new PlayableNote(new Pitch("D", 2), MusicalDuration.sixteenth(1)));
	}

	@Override
	public Map<Integer, Bar<MusicalDuration, PlayableNote>> writeNextBar()
	{
		Map<Integer, Bar<MusicalDuration, PlayableNote>> trackIdToBar = new HashMap<>();

		fillQueue();

		trackIdToBar.put(0, phraseQueue.poll().toOneBar(drumHitMap));

		return trackIdToBar;
	}

	private void fillQueue()
	{
		if (phraseQueue.isEmpty())
		{
			final String phrase1 = "K....S..K....S..H.H.H..K....S..K....S..K....";
			final String phrase2 = "K....S..H.HK.S....H.H.H.H...K....S..H.H.H.H.K..S..";
			final String phrase3 = "K...S...K..";
			final String phrase4 = "S...K...S..";
			final String phrase5 = "S.S..S.";

			Phrase.parse(Stream.of(phrase1, phrase2).collect(Collectors.joining())).split().forEach(phraseQueue::offer);
		}
	}
}
