package org.camokatuk.madcomposer.music;

public class Note
{
	private final Pitch pitch;
	private final Duration duration;

	public Note(Pitch pitch, Duration duration)
	{
		this.pitch = pitch;
		this.duration = duration;
	}

	public Pitch getPitch()
	{
		return pitch;
	}

	public Duration getDuration()
	{
		return duration;
	}
}
