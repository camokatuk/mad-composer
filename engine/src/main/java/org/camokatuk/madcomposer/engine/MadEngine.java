package org.camokatuk.madcomposer.engine;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.camokatuk.madcomposer.engine.sound.SoundEngine;
import org.camokatuk.madcomposer.music.Bar;
import org.camokatuk.madcomposer.music.Composer;
import org.camokatuk.madcomposer.music.MusicalDuration;
import org.camokatuk.madcomposer.music.Performer;
import org.camokatuk.madcomposer.music.PlayableNote;

public class MadEngine
{
	private static final Logger LOGGER = LogManager.getLogger(MadEngine.class);

	private final Composer composer;
	private final List<Performer> performers;
	private final SoundEngine soundEngine;

	private AtomicBoolean running = new AtomicBoolean(false);
	private Thread processThread = null;
	private int bpm = 167;

	public MadEngine(SoundEngine soundEngine, Composer composer, List<Performer> performers)
	{
		this.soundEngine = soundEngine;
		this.composer = composer;
		this.performers = performers;
	}

	public void initialize()
	{
		LOGGER.info("Initializing engine...");
		soundEngine.initialize();
		LOGGER.info("MadEngine initialized");
	}

	public void start()
	{
		if (this.running.getAndSet(true))
		{
			LOGGER.info("MadEngine already running!");
			return;
		}

		soundEngine.start(this.performers.size(), this.bpm);
		LOGGER.info("Starting engine...");
		processThread = new Thread(() -> {
			while (running.get())
			{
				if (soundEngine.isReadyToConsume())
				{
					LOGGER.info("Populating next bar...");
					Map<Integer, Bar<MusicalDuration, PlayableNote>> score = composer.writeNextBar();
					for (Map.Entry<Integer, Bar<MusicalDuration, PlayableNote>> barPerTrack : score.entrySet())
					{
						performers.get(barPerTrack.getKey()).performBar(barPerTrack.getValue());
					}
					soundEngine.notifyAllInstrumentsPlayed();
					LOGGER.info("Done populating");
				}
			}
		});
		processThread.start();
	}

	public void pause()
	{
		if (!this.running.getAndSet(false)) // wasn't running
		{
			return;
		}

		LOGGER.info("Pausing engine...");
		soundEngine.pause();
		try
		{
			processThread.join();
		}
		catch (InterruptedException e)
		{
			LOGGER.info("Couldn't pause engine properly, but who cares");
		}
	}

	public void shutdown()
	{
		LOGGER.info("Stopping engine...");
		try
		{
			this.pause();
		}
		finally
		{
			soundEngine.shutdown();
		}
	}

	public int getBpm()
	{
		return bpm;
	}

	public void setBpm(int bpm)
	{
		this.bpm = bpm;
	}
}
