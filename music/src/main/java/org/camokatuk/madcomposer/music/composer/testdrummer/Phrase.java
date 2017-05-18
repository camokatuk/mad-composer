package org.camokatuk.madcomposer.music.composer.testdrummer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.camokatuk.madcomposer.music.Bar;
import org.camokatuk.madcomposer.music.MusicalDuration;

public class Phrase
{
	private static Map<Character, MusicalDuration> durationCodes = new HashMap<>();

	static
	{
		durationCodes.put('_', MusicalDuration.whole(0)); // fake space to simplify logic
		durationCodes.put('.', MusicalDuration.sixteenth(1));
		durationCodes.put('-', MusicalDuration.eighth(1));
	}

	private Map<Character, List<MusicalDuration>> events = new HashMap<>();

	public static Phrase parse(String string)
	{
		Phrase phrase = new Phrase();
		MusicalDuration currentDuration = new MusicalDuration(0, 16);
		Set<Character> simultaneousTokens = new HashSet<>();
		string = string + '_';
		for (char character : string.toCharArray())
		{
			MusicalDuration space = durationCodes.get(character);
			if (space != null) // is space
			{
				for (Character token : simultaneousTokens) // add current offset for all tokens
				{
					List<MusicalDuration> durations = phrase.events.getOrDefault(token, new LinkedList<>());
					durations.add(currentDuration);
					phrase.events.put(token, durations);
				}
				simultaneousTokens.clear();
				currentDuration = currentDuration.add(space);
			}
			else if ('/' != character) // not a space or division => token
			{
				simultaneousTokens.add(character);
			}
		}
		return phrase;
	}

	public <E> Bar<MusicalDuration, E> toBar(Map<Character, E> tokenToEvent)
	{
		Bar<MusicalDuration, E> bar = new Bar<>();
		for (Map.Entry<Character, List<MusicalDuration>> eventsEntry : events.entrySet())
		{
			E event = tokenToEvent.get(eventsEntry.getKey());
			eventsEntry.getValue().forEach(duration -> bar.addEvent(event, duration));
		}
		return bar;
	}
}
