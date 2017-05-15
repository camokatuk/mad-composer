package org.camokatuk.madcomposer.engine;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.camokatuk.madcomposer.engine.midi.MidiInstrument;
import org.camokatuk.madcomposer.engine.midi.MidiMonster;
import org.camokatuk.madcomposer.engine.midi.MidiTransport;
import org.camokatuk.madcomposer.music.Bar;
import org.camokatuk.madcomposer.music.Composer;
import org.camokatuk.madcomposer.music.Performer;
import org.camokatuk.madcomposer.music.composer.Paradox;
import org.camokatuk.madcomposer.music.performer.Robot;

public class Engine
{
	private static final Logger LOGGER = LogManager.getLogger(Engine.class);

	private Composer composer = new Paradox();
	private Map<Integer, Performer> players = new HashMap<>();
	private MidiMonster midiMonster;
	private MidiTransport midiTransport = new MidiTransport();

	private TimeKeeper timeKeeper;
	private AtomicBoolean running = new AtomicBoolean(false);
	private Thread processThread = null;

	public Engine()
	{
		this.midiMonster = new MidiMonster(midiTransport);
		this.timeKeeper = midiMonster;
		this.players.put(0, new Performer(new Robot(), new MidiInstrument(0, midiMonster)));
	}

	public void initialize()
	{
		LOGGER.info("Initializing engine...");
		midiTransport.initialize();
		LOGGER.info("Engine initialized");
	}

	public void start()
	{
		if (this.running.getAndSet(true))
		{
			LOGGER.info("Engine already running!");
			return;
		}

		midiMonster.createAndStartSequencer(this.players.size());
		LOGGER.info("Starting engine...");
		processThread = new Thread(() -> {
			while (running.get())
			{
				if (timeKeeper.isTimeToPlayNextBar())
				{
					LOGGER.info("Populating next bar...");
					Map<Integer, Bar> score = composer.writeNextBar();
					for (Map.Entry<Integer, Bar> barPerTrack : score.entrySet())
					{
						players.get(barPerTrack.getKey()).performBar(barPerTrack.getValue());
					}
					midiMonster.notifyNextBarIsPopulated();
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
		midiMonster.stopSequencer();
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
			midiTransport.shutdown();
		}
	}

	public MidiMonster getMidiMonster()
	{
		return midiMonster;
	}

	public MidiTransport getMidiTransport()
	{
		return midiTransport;
	}
}
