package de.rwth.i9.palm.feature.publication;

import org.springframework.beans.factory.annotation.Autowired;

public class PublicationFeatureImpl implements PublicationFeature
{
	@Autowired( required = false )
	private PublicationApi publicationApi;

	@Autowired( required = false )
	private PublicationBasicStatistic publicationBasicStatistic;

	@Autowired( required = false )
	private PublicationDetail publicationDetail;

	@Autowired( required = false )
	private PublicationDelete publicationDelete;

	@Autowired( required = false )
	private PublicationManage publicationManage;

	@Autowired( required = false )
	private PublicationMining publicationMining;

	@Autowired( required = false )
	private PublicationSearch publicationSearch;

	@Autowired( required = false )
	private PublicationSimilar publicationSimilar;
	
	@Autowired( required = false )
	private PublicationTopicModeling publicationTopicModeling;

	@Override
	public PublicationApi getPublicationApi()
	{
		if ( this.publicationApi == null )
			this.publicationApi = new PublicationApiImpl();

		return this.publicationApi;
	}

	@Override
	public PublicationBasicStatistic getPublicationBasicStatistic()
	{
		if ( this.publicationBasicStatistic == null )
			this.publicationBasicStatistic = new PublicationBasicStatisticImpl();

		return this.publicationBasicStatistic;
	}

	@Override
	public PublicationDetail getPublicationDetail()
	{
		if ( this.publicationDetail == null )
			this.publicationDetail = new PublicationDetailImpl();

		return this.publicationDetail;
	}

	@Override
	public PublicationDelete doDeletePublication()
	{
		if ( this.publicationDelete == null )
			this.publicationDelete = new PublicationDeleteImpl();

		return this.publicationDelete;
	}

	@Override
	public PublicationManage getPublicationManage()
	{
		if ( this.publicationManage == null )
			this.publicationManage = new PublicationManageImpl();

		return this.publicationManage;
	}

	@Override
	public PublicationMining getPublicationMining()
	{
		if ( this.publicationMining == null )
			this.publicationMining = new PublicationMiningImpl();

		return this.publicationMining;
	}

	@Override
	public PublicationSearch getPublicationSearch()
	{
		if ( this.publicationSearch == null )
			this.publicationSearch = new PublicationSearchImpl();

		return this.publicationSearch;
	}

	@Override
	public PublicationSimilar getPublicationSimilar()
	{
		if ( this.publicationSimilar == null )
			this.publicationSimilar = new PublicationSimilarImpl();

		return this.publicationSimilar;
	}

	@Override
	public PublicationTopicModeling getPublicationTopicModeling() {
		if ( this.publicationTopicModeling == null )
			this.publicationTopicModeling = new PublicationTopicModelingImpl();

		return this.publicationTopicModeling;
	}
}
