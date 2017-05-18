package org.camokatuk.madcomposer.engine.sound;

// can be based on chunks like midi, or always be ready to consume
public interface SoundEngine<Event extends SoundEngineEvent>
{
	void initialize();

	void start(int numberOfPerformers, int bpm);

	void notifyAllInstrumentsPlayed();

	boolean isReadyToConsume();

	void consumeEvent(Event event);

	void pause();

	void shutdown();
}
