package org.camokatuk.madcomposer.engine.sound.midi;

import org.camokatuk.madcomposer.engine.sound.SoundEngineEvent;

public class MidiSoundEngineEvent implements SoundEngineEvent<Long, MidiNote>
{
	private final Integer instrumentId;
	private final Long start;
	private final MidiNote midiNote;

	public MidiSoundEngineEvent(Integer instrumentId, Long start, MidiNote midiNote)
	{
		this.start = start;
		this.midiNote = midiNote;
		this.instrumentId = instrumentId;
	}

	public Integer getInstrumentId()
	{
		return instrumentId;
	}

	@Override
	public Long getStart()
	{
		return start;
	}

	@Override
	public MidiNote getEvent()
	{
		return midiNote;
	}
}
