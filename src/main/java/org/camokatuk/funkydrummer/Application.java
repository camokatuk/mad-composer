package org.camokatuk.funkydrummer;

import org.camokatuk.funkydrummer.control.EngineControlRoom;
import org.camokatuk.funkydrummer.engine.Engine;
import org.camokatuk.funkydrummer.ui.View;

public class Application
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
