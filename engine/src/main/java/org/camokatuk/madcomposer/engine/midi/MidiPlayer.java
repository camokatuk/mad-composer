package org.camokatuk.madcomposer.engine.midi;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.camokatuk.madcomposer.music.Bar;
import org.camokatuk.madcomposer.music.Duration;
import org.camokatuk.madcomposer.music.Note;
import org.camokatuk.madcomposer.music.Player;

public class MidiPlayer implements Player
{
	private static final Logger LOGGER = LogManager.getLogger(MidiPlayer.class);

	private final int trackId;
	private final MidiInstrument midiInstrument;

	public MidiPlayer(int trackId, MidiInstrument midiInstrument)
	{
		this.trackId = trackId;
		this.midiInstrument = midiInstrument;
	}

	@Override
	public void performBar(Bar bar)
	{
		for (Map.Entry<Duration, Note> noteEntry : bar.getNotesByStart().entrySet())
		{
			Note note = noteEntry.getValue();
			MidiNote midiNote = new MidiNote(note.getPitch(), 127, durationInTicks(note.getDuration())); // TODO velocity (character)
			midiInstrument.scheduleNoteNextBar(midiNote, trackId, noteEntry.getKey());
		}
	}

	private long durationInTicks(Duration duration)
	{
		return (MidiInstrument.TICKS_PER_BAR / duration.getBase() * duration.getQuant());
	}
}