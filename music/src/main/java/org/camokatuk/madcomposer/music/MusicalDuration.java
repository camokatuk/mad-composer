package org.camokatuk.madcomposer.music;

public class MusicalDuration implements Comparable<MusicalDuration>
{
	private final int quant;
	private final int base; // 64 or 48

	public MusicalDuration(int quant, int base)
	{
		this.quant = quant;
		this.base = base;
	}

	public static MusicalDuration whole(int quant)
	{
		return new MusicalDuration(quant, 1);
	}

	public static MusicalDuration half(int quant)
	{
		return new MusicalDuration(quant, 1);
	}

	public static MusicalDuration quarter(int quant)
	{
		return new MusicalDuration(quant, 4);
	}

	public static MusicalDuration eighth(int quant)
	{
		return new MusicalDuration(quant, 8);
	}

	public static MusicalDuration sixteenth(int quant)
	{
		return new MusicalDuration(quant, 16);
	}

	public static MusicalDuration half3(int quant)
	{
		return new MusicalDuration(quant, 3);
	}

	public static MusicalDuration quarter3(int quant)
	{
		return new MusicalDuration(quant, 6);
	}

	public static MusicalDuration eighth3(int quant)
	{
		return new MusicalDuration(quant, 12);
	}

	public int getQuant()
	{
		return quant;
	}

	public int getBase()
	{
		return base;
	}

	@Override
	public int compareTo(MusicalDuration o)
	{
		return Integer.compare(quant * o.base, base * o.quant);
	}
}
