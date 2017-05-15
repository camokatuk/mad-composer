package org.camokatuk.madcomposer.engine.midi;

import org.camokatuk.madcomposer.music.Pitch;

public class MidiNote
{
	private final Pitch pitch;
	private final int velocity;
	        /*
	     *	TimeKeeper between pitch on and pitch off event in
		 *	milliseconds. Pitch that on most systems, the
		 *	best resolution you can expect are 10 ms.
		 */

	private final long durationTicks;

	public MidiNote(Pitch pitch, int velocity, long durationTicks)
	{
		this.pitch = pitch;
		this.velocity = Math.min(127, Math.max(0, velocity));
		this.durationTicks = Math.max(0, durationTicks);
	}

	public Pitch getPitch()
	{
		return pitch;
	}

	public int getVelocity()
	{
		return velocity;
	}

	public long getDurationTicks()
	{
		return durationTicks;
	}

	@Override
	public String toString()
	{
		return "MidiNote{" + "pitch=" + pitch + ", velocity=" + velocity + ", durationTicks=" + durationTicks +
			'}';
	}
}
