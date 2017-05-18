package org.camokatuk.madcomposer.music;

public interface Personality
{
	Bar<MusicalDuration, InterpretedNote> interpret(Bar<MusicalDuration, PlayableNote> writtenBar);
}
