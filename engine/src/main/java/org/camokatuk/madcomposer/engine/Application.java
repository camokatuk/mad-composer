package org.camokatuk.madcomposer.engine;

import java.util.Collections;

import org.camokatuk.madcomposer.engine.control.ApplicationControlRoom;
import org.camokatuk.madcomposer.engine.sound.SoundEngine;
import org.camokatuk.madcomposer.engine.sound.midi.MidiInstrument;
import org.camokatuk.madcomposer.engine.sound.midi.MidiSoundEngine;
import org.camokatuk.madcomposer.engine.sound.midi.MidiTransport;
import org.camokatuk.madcomposer.music.Composer;
import org.camokatuk.madcomposer.music.Instrument;
import org.camokatuk.madcomposer.music.Performer;
import org.camokatuk.madcomposer.music.composer.testdrummer.TestDrummer;
import org.camokatuk.madcomposer.music.performer.Robot;

/**
 * Created by evlasov on 0018, 5/18.
 */
public class Application
{
	private ApplicationControlRoom controlRoom;

	public void initialize(ApplicationConfiguration applicationConfiguration)
	{
		MidiTransport midiTransport = new MidiTransport();
		//		MidiDebugSoundEngine soundEngine = new MidiDebugSoundEngine();
		SoundEngine soundEngine = new MidiSoundEngine(midiTransport);
		Instrument midiTrack0Instrument = new MidiInstrument(0, soundEngine);

		Composer composer = new TestDrummer();
		Performer robot = new Performer(new Robot(), midiTrack0Instrument);

		MadEngine engine = new MadEngine(soundEngine, composer, Collections.singletonList(robot));
		engine.initialize();

		this.controlRoom = new ApplicationControlRoom(engine, midiTransport);
	}

	public ApplicationControlRoom getApplicationControlRoom()
	{
		return controlRoom;
	}
}
