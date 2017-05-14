package org.camokatuk.madcomposer.music;

public class Duration implements Comparable<Duration>
{
	private final int quant;
	private final int base; // 64 or 48

	public Duration(int quant, int base)
	{
		this.quant = quant;
		this.base = base;
	}

	public static Duration whole(int quant)
	{
		return new Duration(quant, 1);
	}

	public static Duration half(int quant)
	{
		return new Duration(quant, 1);
	}

	public static Duration quarter(int quant)
	{
		return new Duration(quant, 4);
	}

	public static Duration eighth(int quant)
	{
		return new Duration(quant, 8);
	}

	public static Duration sixteenth(int quant)
	{
		return new Duration(quant, 16);
	}

	public static Duration half3(int quant)
	{
		return new Duration(quant, 3);
	}

	public static Duration quarter3(int quant)
	{
		return new Duration(quant, 6);
	}

	public static Duration eighth3(int quant)
	{
		return new Duration(quant, 12);
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
	public int compareTo(Duration o)
	{
		return Integer.compare(quant * o.base, base * o.quant);
	}
}
