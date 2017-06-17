package de.rwth.i9.palm.feature.academicevent;

import java.util.Map;

public interface EventBasicStatistic
{
	public Map<String, Object> getEventBasicStatisticById( String eventId );

	public Map<String, Object> getEventGroupBasicStatisticById( String eventGroupId );
}
