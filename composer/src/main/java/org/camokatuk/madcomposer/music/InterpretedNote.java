package org.camokatuk.madcomposer.music;

public class InterpretedNote
{
	private final Note note;
	private final int velocity; // let's measure it as midi does (0-127), but can be anything in theory
	private final Shuffle shuffle;

	// TODO add duration mistake

	public InterpretedNote(Note note, int velocity, Shuffle shuffle)
	{
		this.note = note;
		this.velocity = velocity;
		this.shuffle = shuffle;
	}

	public Note getNote()
	{
		return note;
	}

	public int getVelocity()
	{
		return velocity;
	}

	public Shuffle getShuffle()
	{
		return shuffle;
	}
}
