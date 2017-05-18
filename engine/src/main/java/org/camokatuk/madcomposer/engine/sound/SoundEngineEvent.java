package org.camokatuk.madcomposer.engine.sound;

/**
 * Created by evlasov on 0018, 5/18.
 */
public interface SoundEngineEvent<TimeUnit extends Comparable<TimeUnit>, Event>
{
	TimeUnit getStart();

	Event getEvent();
}
