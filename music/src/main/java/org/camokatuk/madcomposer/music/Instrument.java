package org.camokatuk.madcomposer.music;

public interface Instrument
{
	void playBar(Bar<MusicalDuration, InterpretedNote> interpretedBar);
}
