package org.camokatuk.madcomposer.engine.control;

import org.camokatuk.madcomposer.engine.Engine;
import org.camokatuk.madcomposer.music.Bar;
import org.camokatuk.madcomposer.music.Duration;
import org.camokatuk.madcomposer.music.Note;
import org.camokatuk.madcomposer.music.Pitch;

public class PlaybackController
{
	private Engine engine;

	public PlaybackController(Engine engine)
	{
		this.engine = engine;
	}

	public void setBpm(int bpm)
	{
		this.engine.getMidiPlayer().getMidiGenerator().setBpm(bpm);
	}

	public void start()
	{
		this.engine.getMidiPlayer().getMidiGenerator().startGenerating();
	}

	public void stop()
	{
		this.engine.getMidiPlayer().getMidiGenerator().stopGenerating();
	}

	public void trigger()
	{
		int ppq = this.engine.getMidiPlayer().getMidiGenerator().getPPQ();
		Bar bar = new Bar();
		bar.addNote(new Note(new Pitch("C", 1), Duration.quarter(1)), Duration.quarter(0));
		bar.addNote(new Note(new Pitch("C", 1), Duration.quarter(1)), Duration.quarter(1));
		bar.addNote(new Note(new Pitch("C", 1), Duration.quarter(1)), Duration.quarter(2));
		bar.addNote(new Note(new Pitch("C", 1), Duration.quarter(1)), Duration.quarter(3));
		this.engine.getMidiPlayer().getMidiGenerator().scheduleBar(bar, 0);
	}
}
