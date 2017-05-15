package org.camokatuk.madcomposer.engine.midi;

import java.util.Map;

import org.camokatuk.madcomposer.music.Bar;
import org.camokatuk.madcomposer.music.Duration;
import org.camokatuk.madcomposer.music.Instrument;
import org.camokatuk.madcomposer.music.InterpretedNote;
import org.camokatuk.madcomposer.music.Shuffle;

import static org.camokatuk.madcomposer.engine.midi.MidiMonster.TICKS_PER_BAR;

public class MidiInstrument implements Instrument
{
	private final int trackId;
	private final MidiMonster midiMonster;

	public MidiInstrument(int trackId, MidiMonster midiMonster)
	{
		this.trackId = trackId;
		this.midiMonster = midiMonster;
	}

	@Override
	public void performBar(Bar<InterpretedNote> interpretedBar)
	{
		for (Map.Entry<Duration, InterpretedNote> noteEntry : interpretedBar.getNotesByStart().entrySet())
		{
			InterpretedNote interpretedNote = noteEntry.getValue();

			long durationTicks = durationInTicks(interpretedNote.getNote().getDuration());
			long durationTicksAfterShuffle = applyShuffle(durationTicks, interpretedNote.getShuffle());

			long noteStartTimeInTicks = (TICKS_PER_BAR / noteEntry.getKey().getBase() * noteEntry.getKey().getQuant());
			long startTicksAfterShuffle = applyShuffle(noteStartTimeInTicks, interpretedNote.getShuffle());

			MidiNote midiNote = new MidiNote(interpretedNote.getNote().getPitch(), interpretedNote.getVelocity(), durationTicksAfterShuffle);

			midiMonster.scheduleNoteNextBar(this.trackId, midiNote, startTicksAfterShuffle);
		}
	}

	private static long applyShuffle(long noteStartTimeInTicks, Shuffle shuffle)
	{
		// TODO apply it!
		return noteStartTimeInTicks;
	}

	private long durationInTicks(Duration duration)
	{
		return (TICKS_PER_BAR / duration.getBase() * duration.getQuant());
	}

}
