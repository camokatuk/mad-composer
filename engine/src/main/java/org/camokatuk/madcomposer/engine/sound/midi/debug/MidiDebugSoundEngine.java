package org.camokatuk.madcomposer.engine.sound.midi.debug;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.camokatuk.madcomposer.engine.sound.SoundEngine;
import org.camokatuk.madcomposer.engine.sound.midi.MidiSoundEngineEvent;

/**
 * Created by evlasov on 0018, 5/18.
 */
public class MidiDebugSoundEngine implements SoundEngine<MidiSoundEngineEvent>
{
	private static final Logger LOGGER = LogManager.getLogger(MidiDebugSoundEngine.class);

	private Long lastNotified = null;
	private Integer bpm = null;

	@Override
	public void initialize()
	{

	}

	@Override
	public void start(int numberOfPerformers, int bpm)
	{
		this.bpm = bpm;
	}

	@Override
	public void notifyAllInstrumentsPlayed()
	{
		lastNotified = System.currentTimeMillis();
	}

	@Override
	public boolean isReadyToConsume()
	{
		int barTimeMillis = Math.round(240000f / bpm);
		return lastNotified == null || (System.currentTimeMillis() - lastNotified > barTimeMillis);
	}

	@Override
	public void consumeEvent(MidiSoundEngineEvent event)
	{
		LOGGER.info(event.getInstrumentId() + " plays " + event.getEvent());
	}

	@Override
	public void pause()
	{
		lastNotified = null;
	}

	@Override
	public void shutdown()
	{

	}
}
