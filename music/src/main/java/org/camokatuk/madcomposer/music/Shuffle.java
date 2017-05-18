package org.camokatuk.madcomposer.music;

public class Shuffle
{
	public static final Shuffle ON_TIME = new Shuffle((byte) 0, ShuffleDegree.NO);

	public static final Shuffle A_BIT_AHEAD = new Shuffle((byte) 1, ShuffleDegree.A_BIT);
	public static final Shuffle SLIGHTLY_AHEAD = new Shuffle((byte) 1, ShuffleDegree.SLIGHTLY);
	public static final Shuffle NOTICEABLY_AHEAD = new Shuffle((byte) 1, ShuffleDegree.NOTICEABLY);

	public static final Shuffle A_BIT_BEHIND = new Shuffle((byte) -1, ShuffleDegree.A_BIT);
	public static final Shuffle SLIGHTLY_BEHIND = new Shuffle((byte) -1, ShuffleDegree.SLIGHTLY);
	public static final Shuffle NOTICEABLY_BEHIND = new Shuffle((byte) -1, ShuffleDegree.NOTICEABLY);

	private final byte shiftDirection;
	private final ShuffleDegree degree;

	private Shuffle(byte shiftDirection, ShuffleDegree degree)
	{
		this.shiftDirection = shiftDirection;
		this.degree = degree;
	}

	public ShuffleDegree getDegree()
	{
		return degree;
	}

	public byte getShiftDirection()
	{
		return this.shiftDirection;
	}
}
