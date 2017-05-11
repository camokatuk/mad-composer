package org.camokatuk.madcomposer.engine.control;

import org.camokatuk.madcomposer.engine.Engine;
import org.camokatuk.madcomposer.midi.MidiNote;
import org.camokatuk.madcomposer.music.Note;

public class PlaybackController
{
	private Engine engine;

	public PlaybackController(Engine engine)
	{
		this.engine = engine;
	}

	public void setBpm(int bpm)
	{
		this.engine.getMidiSpammer().getMidiGenerator().setBpm(bpm);
	}

	public void start()
	{
		this.engine.getMidiSpammer().getMidiGenerator().startGenerating();
	}

	public void stop()
	{
		this.engine.getMidiSpammer().getMidiGenerator().stopGenerating();
	}

	public void trigger()
	{
		this.engine.getMidiSpammer().getMidiGenerator().triggerNote(new MidiNote(new Note("C", 1), 127, 1000));
	}

	public void setDelay(int delay)
	{
		this.engine.getMidiSpammer().getMidiGenerator().setDelay(delay);
	}
}
