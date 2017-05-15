package org.camokatuk.madcomposer.music;

public class Player
{
	private final Personality personality;
	private final Instrument instrument;

	public Player(Personality personality, Instrument instrument)
	{
		this.personality = personality;
		this.instrument = instrument;
	}

	public void performBar(Bar<Note> bar)
	{
		instrument.performBar(personality.interpret(bar));
	}
}

