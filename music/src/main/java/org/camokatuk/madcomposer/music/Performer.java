package org.camokatuk.madcomposer.music;

public class Performer
{
	private final Personality personality;
	private final Instrument instrument;

	public Performer(Personality personality, Instrument instrument)
	{
		this.personality = personality;
		this.instrument = instrument;
	}

	public void performBar(Bar<MusicalDuration, PlayableNote> bar)
	{
		instrument.playBar(personality.interpret(bar));
	}
}

