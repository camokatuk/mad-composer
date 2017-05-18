package org.camokatuk.madcomposer.music;

public class PlayableNote
{
	private final Pitch pitch;
	private final MusicalDuration duration;

	public PlayableNote(Pitch pitch, MusicalDuration duration)
	{
		this.pitch = pitch;
		this.duration = duration;
	}

	public Pitch getPitch()
	{
		return pitch;
	}

	public MusicalDuration getDuration()
	{
		return duration;
	}
}
