package de.rwth.i9.palm.feature.circle;

import org.springframework.beans.factory.annotation.Autowired;

public class CircleFeatureImpl implements CircleFeature
{

	@Autowired( required = false )
	private CircleAcademicEventTree circleAcademicEventTree;

	@Autowired( required = false )
	private CircleApi circleApi;

	@Autowired( required = false )
	private CircleBasicInformation circleBasicInformation;

	@Autowired( required = false )
	private CircleDetail circleDetail;

	@Autowired( required = false )
	private CircleInterest circleInterest;

	@Autowired( required = false )
	private CircleManage circleManage;

	@Autowired( required = false )
	private CirclePublication circlePublication;

	@Autowired( required = false )
	private CircleResearcher circleResearcher;

	@Autowired( required = false )
	private CircleSearch circleSearch;

	@Autowired( required = false )
	private CircleTopPublication circleTopPublication;

	@Autowired( required = false )
	private CircleTopicModeling circleTopicModeling;

	@Override
	public CircleAcademicEventTree getCircleAcademicEventTree()
	{
		if ( this.circleAcademicEventTree == null )
			this.circleAcademicEventTree = new CircleAcademicEventTreeImpl();

		return this.circleAcademicEventTree;
	}

	@Override
	public CircleApi getCircleApi()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CircleBasicInformation getCircleBasicInformation()
	{
		if ( this.circleBasicInformation == null )
			this.circleBasicInformation = new CircleBasicInformationImpl();

		return this.circleBasicInformation;
	}

	@Override
	public CircleDetail getCircleDetail()
	{
		if ( this.circleDetail == null )
			this.circleDetail = new CircleDetailImpl();

		return this.circleDetail;
	}

	@Override
	public CircleInterest getCircleInterest()
	{
		if ( this.circleInterest == null )
			this.circleInterest = new CircleInterestImpl();

		return this.circleInterest;
	}

	@Override
	public CircleManage getCircleManage()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public CirclePublication getCirclePublication()
	{
		if ( this.circlePublication == null )
			this.circlePublication = new CirclePublicationImpl();

		return this.circlePublication;
	}

	public CircleResearcher getCircleResearcher()
	{
		if ( this.circleResearcher == null )
			this.circleResearcher = new CircleResearcherImpl();

		return this.circleResearcher;
	}

	@Override
	public CircleSearch getCircleSearch()
	{
		if ( this.circleSearch == null )
			this.circleSearch = new CircleSearchImpl();

		return this.circleSearch;
	}

	@Override
	public CircleTopPublication getCircleTopPublication()
	{
		if ( this.circleTopPublication == null )
			this.circleTopPublication = new CircleTopPublicationImpl();

		return this.circleTopPublication;
	}

	@Override
	public CircleTopicModeling getCircleTopicModeling()
	{
		if ( this.circleTopicModeling == null )
			this.circleTopicModeling = new CircleTopicModelingImpl();

		return this.circleTopicModeling;
	}

}
