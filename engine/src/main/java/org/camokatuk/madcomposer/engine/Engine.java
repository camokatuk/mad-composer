package org.camokatuk.madcomposer.engine;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.sound.midi.MidiUnavailableException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.camokatuk.madcomposer.engine.midi.MidiInstrument;
import org.camokatuk.madcomposer.engine.midi.MidiPlayer;
import org.camokatuk.madcomposer.music.Bar;
import org.camokatuk.madcomposer.music.Composer;
import org.camokatuk.madcomposer.music.Player;
import org.camokatuk.madcomposer.music.TimeKeeper;
import org.camokatuk.madcomposer.music.composer.Paradox;

public class Engine
{
	private static final Logger LOGGER = LogManager.getLogger(Engine.class);

	private Composer composer = new Paradox();
	private Map<Integer, Player> players = new HashMap<>();
	private MidiInstrument midiInstrument;

	private TimeKeeper timeKeeper;
	private AtomicBoolean running = new AtomicBoolean(false);
	private Thread processThread = null;

	public Engine()
	{
		this.midiInstrument = new MidiInstrument();
		this.timeKeeper = midiInstrument;
		this.players.put(0, new MidiPlayer(0, midiInstrument));
	}

	public void initialize()
	{
		LOGGER.info("Initializing engine...");
		try
		{
			midiInstrument.initialize();
		}
		catch (MidiUnavailableException e)
		{
			throw new EngineFatalException("Failed to start midi spammer even with default device", e);
		}
		LOGGER.info("Engine initialized");
	}

	public void start()
	{
		if (this.running.getAndSet(true))
		{
			LOGGER.info("Engine already running!");
			return;
		}

		midiInstrument.createAndStartSequencer(this.players.size());
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
					midiInstrument.notifyNextBarIsPopulated();
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
		midiInstrument.stopSequencer();
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
		this.running.set(false);
		try
		{
			this.pause();
		}
		finally
		{
			midiInstrument.shutdown();
		}
	}

	public MidiInstrument getMidiInstrument()
	{
		return midiInstrument;
	}

}
