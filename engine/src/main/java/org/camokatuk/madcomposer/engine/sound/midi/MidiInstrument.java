package org.camokatuk.madcomposer.engine.sound.midi;

import java.util.Random;

import org.camokatuk.madcomposer.engine.sound.SoundEngine;
import org.camokatuk.madcomposer.music.Bar;
import org.camokatuk.madcomposer.music.Instrument;
import org.camokatuk.madcomposer.music.InterpretedNote;
import org.camokatuk.madcomposer.music.MusicalDuration;
import org.camokatuk.madcomposer.music.Shuffle;
import org.camokatuk.madcomposer.music.ShuffleDegree;

public class MidiInstrument implements Instrument
{
	private static final Random random = new Random();
	private final int trackId;
	private final SoundEngine<MidiSoundEngineEvent> soundEngine;

	public MidiInstrument(int trackId, SoundEngine<MidiSoundEngineEvent> soundEngine)
	{
		this.trackId = trackId;
		this.soundEngine = soundEngine;
	}

	@Override
	public void playBar(Bar<MusicalDuration, InterpretedNote> interpretedBar)
	{
		interpretedBar.traverse(interpretedNoteEvent -> {
			MusicalDuration offset = interpretedNoteEvent.getOffset();
			InterpretedNote interpretedNote = interpretedNoteEvent.getEvent();

			long durationTicks = durationInTicks(interpretedNote.getNote().getDuration());

			// TODO looks like clashes on the same note are not the problem, but fucking watch out for this shit
			long durationTicksAfterShuffle = durationTicks; //applyShuffle(durationTicks, interpretedNote.getShuffle());

			long noteStartTimeInTicks = (MidiConstants.TICKS_PER_BAR / offset.getBase() * offset.getQuant());
			long startTicksAfterShuffle = applyShuffle(noteStartTimeInTicks, interpretedNote.getShuffle());

			MidiNote midiNote = new MidiNote(interpretedNote.getNote().getPitch(), interpretedNote.getVelocity(), durationTicksAfterShuffle);

			soundEngine.consumeEvent(new MidiSoundEngineEvent(this.trackId, startTicksAfterShuffle, midiNote));
		});
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

	private long durationInTicks(MusicalDuration duration)
	{
		return (MidiConstants.TICKS_PER_BAR / duration.getBase() * duration.getQuant());
	}

}
