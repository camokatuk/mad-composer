package org.camokatuk.funkydrummer;

import org.camokatuk.funkydrummer.control.EngineControlRoom;
import org.camokatuk.funkydrummer.engine.Engine;
import org.camokatuk.funkydrummer.ui.View;

public class Application
{
    public static void main(String[] sd)
    {
        Engine engine = new Engine();
        engine.start();

        EngineControlRoom controlRoom = new EngineControlRoom(engine);

        View view = new View(controlRoom);
        view.initialize();
    }
}
