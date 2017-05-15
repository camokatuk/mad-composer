package org.camokatuk.madcomposer.engine.midi;

import java.util.Map;
import java.util.Random;

import org.camokatuk.madcomposer.music.Bar;
import org.camokatuk.madcomposer.music.Duration;
import org.camokatuk.madcomposer.music.Instrument;
import org.camokatuk.madcomposer.music.InterpretedNote;
import org.camokatuk.madcomposer.music.Shuffle;
import org.camokatuk.madcomposer.music.ShuffleDegree;

import static org.camokatuk.madcomposer.engine.midi.MidiMonster.TICKS_PER_BAR;

public class MidiInstrument implements Instrument
{
	private static final Random random = new Random();
	private final int trackId;
	private final MidiMonster midiMonster;

	public MidiInstrument(int trackId, MidiMonster midiMonster)
	{
		this.trackId = trackId;
		this.midiMonster = midiMonster;
	}

	@Override
	public void playBar(Bar<InterpretedNote> interpretedBar)
	{
		// TODO avoid clashes after shuffle
		for (Map.Entry<Duration, InterpretedNote> noteEntry : interpretedBar.getNotesByStart().entrySet())
		{
			InterpretedNote interpretedNote = noteEntry.getValue();

			long durationTicks = durationInTicks(interpretedNote.getNote().getDuration());
			long durationTicksAfterShuffle = durationTicks - 16; //applyShuffle(durationTicks, interpretedNote.getShuffle());

			long noteStartTimeInTicks = (TICKS_PER_BAR / noteEntry.getKey().getBase() * noteEntry.getKey().getQuant());
			long startTicksAfterShuffle = applyShuffle(noteStartTimeInTicks, interpretedNote.getShuffle());

			MidiNote midiNote = new MidiNote(interpretedNote.getNote().getPitch(), interpretedNote.getVelocity(), durationTicksAfterShuffle);

			midiMonster.scheduleNoteNextBar(this.trackId, midiNote, startTicksAfterShuffle);
		}
	}

	private static long applyShuffle(long noteStartTimeInTicks, Shuffle shuffle)
	{
		long shiftValue = 0;
		// PPQ 96 TODO
		if (shuffle.getDegree() == ShuffleDegree.A_BIT)
		{
			shiftValue = random.nextInt(3);
		}
		else if (shuffle.getDegree() == ShuffleDegree.SLIGHTLY)
		{
			shiftValue = 3 + random.nextInt(4);
		}
		else if (shuffle.getDegree() == ShuffleDegree.NOTICEABLY)
		{
			shiftValue = 7 + random.nextInt(5);
		}

		return noteStartTimeInTicks + shiftValue * shuffle.getShiftDirection();
	}

	private long durationInTicks(Duration duration)
	{
		return (TICKS_PER_BAR / duration.getBase() * duration.getQuant());
	}

}
