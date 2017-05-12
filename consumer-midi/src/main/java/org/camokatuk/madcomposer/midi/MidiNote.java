package org.camokatuk.madcomposer.midi;

import org.camokatuk.madcomposer.music.Note;

public class MidiNote
{
	private final Note note;
	private final int velocity;
	        /*
	     *	Time between note on and note off event in
		 *	milliseconds. Note that on most systems, the
		 *	best resolution you can expect are 10 ms.
		 */

	private final int durationTicks;

	public MidiNote(Note note, int velocity, int durationTicks)
	{
		this.note = note;
		this.velocity = Math.min(127, Math.max(0, velocity));
		this.durationTicks = Math.max(0, durationTicks);
	}

	public Note getNote()
	{
		return note;
	}

	public int getVelocity()
	{
		return velocity;
	}

	public int getDurationTicks()
	{
		return durationTicks;
	}

	@Override
	public String toString()
	{
		return "MidiNote{" + "note=" + note + ", velocity=" + velocity + ", durationTicks=" + durationTicks +
			'}';
	}
}
