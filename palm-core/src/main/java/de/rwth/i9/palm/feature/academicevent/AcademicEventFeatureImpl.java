package de.rwth.i9.palm.feature.academicevent;

import org.springframework.beans.factory.annotation.Autowired;

public class AcademicEventFeatureImpl implements AcademicEventFeature
{
	@Autowired( required = false )
	private EventBasicStatistic eventBasicStatistic;

	@Autowired( required = false )
	private EventInterest eventInterest;

	@Autowired( required = false )
	private EventTopicModeling eventTopicModeling;

	@Autowired( required = false )
	private EventMining eventMining;

	@Autowired( required = false )
	private EventPublication eventPublication;

	@Autowired( required = false )
	private EventSearch eventSearch;

	@Autowired( required = false )
	private EventTopResearcher eventTopResearcher;

	@Override
	public EventBasicStatistic getEventBasicStatistic()
	{
		if ( this.eventBasicStatistic == null )
			this.eventBasicStatistic = new EventBasicStatisticImpl();

		return this.eventBasicStatistic;
	}

	@Override
	public EventInterest getEventInterest()
	{
		if ( this.eventInterest == null )
			this.eventInterest = new EventInterestImpl();

		return this.eventInterest;
	}

	@Override
	public EventTopicModeling getEventTopicModeling()
	{
		if ( this.eventTopicModeling == null )
			this.eventTopicModeling = new EventTopicModelingImpl();

		return this.eventTopicModeling;
	}

	@Override
	public EventMining getEventMining()
	{
		if ( this.eventMining == null )
			this.eventMining = new EventMiningImpl();

		return this.eventMining;
	}

	@Override
	public EventPublication getEventPublication()
	{
		if ( this.eventPublication == null )
			this.eventPublication = new EventPublicationImpl();

		return this.eventPublication;
	}

	@Override
	public EventSearch getEventSearch()
	{
		if ( this.eventSearch == null )
			this.eventSearch = new EventSearchImpl();

		return this.eventSearch;
	}

	@Override
	public EventTopResearcher getEventResearcher()
	{
		if ( this.eventTopResearcher == null )
			this.eventTopResearcher = new EventTopResearcherImpl();
		return this.eventTopResearcher;
	}
}
