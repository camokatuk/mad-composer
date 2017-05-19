package org.camokatuk.madcomposer.music;

public class MusicalDuration implements Comparable<MusicalDuration>
{
	private final int quant;
	private final int base; // 64 or 48

	public MusicalDuration(int quant, int base)
	{
		int gcd = findGCD(quant, base);
		this.quant = quant / gcd;
		this.base = base / gcd;
	}

	private static int findGCD(int number1, int number2)
	{
		if (number2 == 0) { return number1; }
		return findGCD(number2, number1 % number2);
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

	public MusicalDuration add(int quant, int base)
	{
		return new MusicalDuration(this.base * quant + this.quant * base, this.base * base);
	}

	public MusicalDuration add(MusicalDuration duration)
	{
		return new MusicalDuration(this.base * duration.quant + this.quant * duration.base, this.base * duration.base);
	}

	public MusicalDuration toBarOffset()
	{
		return new MusicalDuration(this.quant % this.base, this.base);
	}

	public boolean notLongerThanBar()
	{
		return quant < base;
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

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{ return true; }
		if (o == null || getClass() != o.getClass())
		{ return false; }

		MusicalDuration that = (MusicalDuration) o;

		if (quant != that.quant)
		{ return false; }
		return base == that.base;

	}

	@Override
	public int hashCode()
	{
		int result = quant;
		result = 31 * result + base;
		return result;
	}
}
