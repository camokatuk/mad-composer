package org.camokatuk.madcomposer.music;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Consumer;

public class Bar<OffsetDuration extends Comparable<OffsetDuration>, Event>
{
	public class TimedEvent implements Comparable<TimedEvent>
	{
		private final OffsetDuration offsetDuration;
		private final Event event;

		public TimedEvent(OffsetDuration offsetDuration, Event event)
		{
			this.offsetDuration = offsetDuration;
			this.event = event;
		}

		public OffsetDuration getOffset()
		{
			return offsetDuration;
		}

		public Event getEvent()
		{
			return event;
		}

		@Override
		public int compareTo(TimedEvent o)
		{
			int offsetComparison = offsetDuration.compareTo(o.offsetDuration);
			return offsetComparison != 0 ? offsetComparison : Integer.compare(event.hashCode(), o.event.hashCode());
		}

		@Override
		public boolean equals(Object o)
		{
			if (this == o)
			{ return true; }
			if (o == null || getClass() != o.getClass())
			{ return false; }

			TimedEvent that = (TimedEvent) o;

			if (!offsetDuration.equals(that.offsetDuration))
			{ return false; }
			return event.equals(that.event);
		}

		@Override
		public int hashCode()
		{
			int result = offsetDuration.hashCode();
			result = 31 * result + event.hashCode();
			return result;
		}
	}

	private final SortedSet<TimedEvent> timedEvents = new TreeSet<>();

	public void addEvent(Event event, OffsetDuration start)
	{
		timedEvents.add(new TimedEvent(start, event));
	}

	public void traverse(Consumer<TimedEvent> consumer)
	{
		timedEvents.forEach(consumer);
	}
}
