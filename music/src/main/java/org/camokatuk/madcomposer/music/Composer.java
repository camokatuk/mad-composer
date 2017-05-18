package org.camokatuk.madcomposer.music;

import java.util.Map;

public interface Composer
{
	Map<Integer, Bar<MusicalDuration, PlayableNote>> writeNextBar(); // key is playerId
}
