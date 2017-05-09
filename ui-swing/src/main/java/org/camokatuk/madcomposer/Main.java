package org.camokatuk.madcomposer;

import org.camokatuk.madcomposer.engine.Engine;
import org.camokatuk.madcomposer.engine.control.EngineControlRoom;
import org.camokatuk.madcomposer.ui.View;

public class Main
{
	public static void main(String[] sd)
	{
		Engine engine = new Engine();
		EngineControlRoom controlRoom = new EngineControlRoom(engine);

		controlRoom.startEngine();

		View view = new View(controlRoom);
		view.initialize();
	}
}
