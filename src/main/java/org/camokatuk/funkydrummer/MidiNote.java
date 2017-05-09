package org.camokatuk.funkydrummer;

public class MidiNote
{
    private final Note note;
    private final int velocity;
            /*
         *	Time between note on and note off event in
		 *	milliseconds. Note that on most systems, the
		 *	best resolution you can expect are 10 ms.
		 */

    private final int durationMillis;

    public MidiNote(Note note, int velocity, int durationMillis)
    {
        this.note = note;
        this.velocity = Math.min(127, Math.max(0, velocity));
        this.durationMillis = Math.max(0, durationMillis);
    }

    public Note getNote()
    {
        return note;
    }

    public int getVelocity()
    {
        return velocity;
    }

    public int getDurationMillis()
    {
        return durationMillis;
    }
}
