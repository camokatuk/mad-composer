package org.camokatuk.madcomposer.music;

public interface Personality
{
	Bar<InterpretedNote> interpret(Bar<Note> writtenBar);
}
