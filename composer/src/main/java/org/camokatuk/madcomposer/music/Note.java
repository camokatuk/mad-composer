package org.camokatuk.madcomposer.music;

import java.util.Arrays;

/**
 * Octave indexOf ranges from -2 to 8, ableton live style
 */
public class Note
{
    private static final String[] nameIndex = new String[]{"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};

    private final int midiIndex;
    private final String name;
    private final int octave;

    public Note(String name, int octave)
    {
        this.midiIndex = indexOf(name, octave);
        this.name = name;
        this.octave = octave;
    }

    public static int indexOf(String name, int octave)
    {
        return indexOf(name.toUpperCase()) + (octave + 2) * 12;
    }

    public Note(int midiIndex)
    {
        this.midiIndex = Math.min(127, Math.max(0, midiIndex));
        this.name = nameIndex[this.midiIndex % 12];
        this.octave = this.midiIndex / 12 - 2;
    }

    private static int indexOf(String name)
    {
        return Arrays.binarySearch(nameIndex, name);
    }

    public int getMidiIndex()
    {
        return midiIndex;
    }

    public String getName()
    {
        return name;
    }

    public int getOctave()
    {
        return octave;
    }

    @Override
    public String toString()
    {
        return name + octave;
    }

    public static void main(String s[])
    {
        for (int i = 0; i < 128; i++)
        {
            System.out.println(new Note(i));
        }
    }
}
