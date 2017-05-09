package org.camokatuk.funkydrummer.engine;

import org.camokatuk.funkydrummer.engine.midi.DeviceManager;
import org.camokatuk.funkydrummer.engine.midi.MidiScheisse;

public class Engine
{
    private MidiScheisse midiScheisse = new MidiScheisse();
    private int bpm = 167;

    public void start()
    {
        DeviceManager.listDevicesAndExit(false, true);
        midiScheisse.initialize();
    }

    public void stop()
    {
        midiScheisse.destroy();
    }

    public void setBpm(int bpm)
    {
        this.bpm = bpm;
    }

    public int getBpm()
    {
        return bpm;
    }

    public void test()
    {
        midiScheisse.test(bpm);
    }


}
