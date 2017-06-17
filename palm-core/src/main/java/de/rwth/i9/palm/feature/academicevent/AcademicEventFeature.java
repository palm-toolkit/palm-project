package de.rwth.i9.palm.feature.academicevent;

/**
 * Factory interface for features Venue/AcademicEvent
 * 
 * @author sigit
 *
 */
public interface AcademicEventFeature
{
	public EventBasicStatistic getEventBasicStatistic();

	public EventMining getEventMining();

	public EventInterest getEventInterest();

	public EventPublication getEventPublication();

	public EventSearch getEventSearch();

	public EventTopicModeling getEventTopicModeling();

	public EventTopResearcher getEventResearcher();

}
