package de.rwth.i9.palm.feature.researcher;

import org.springframework.beans.factory.annotation.Autowired;

public class ResearcherFeatureImpl implements ResearcherFeature
{
	@Autowired( required = false )
	private ResearcherAcademicEventTree researcherAcademicEventTree;

	@Autowired( required = false )
	private ResearcherApi researcherApi;

	@Autowired( required = false )
	private ResearcherBasicInformation researcherBasicInformation;

	@Autowired( required = false )
	private ResearcherCoauthor researcherCoauthor;

	// @Autowired( required = false )
	// private ResearcherRecommededauthor researcherRecommendedauthor;

	@Autowired( required = false )
	private ResearcherSimilarauthor researcherSimilarauthor;

	@Autowired( required = false )
	private ResearcherTopicEvolutionTest researcherTopicEvolutionTest;

	@Autowired( required = false )
	private ResearcherTopicCompositionCloud researcherTopicCloud;

	@Autowired( required = false )
	private ResearcherInterest researcherInterest;
	
	@Autowired( required = false )
	private ResearcherInterestEvolution researcherInterestEvolution;
	
	@Autowired( required = false )
	private ResearcherMining researcherMining;

	@Autowired( required = false )
	private ResearcherPublication researcherPublication;

	@Autowired( required = false )
	private ResearcherSearch researcherSearch;

	@Autowired( required = false )
	private ResearcherTopPublication researcherTopPublication;
	
	@Autowired( required = false )
	private ResearcherTopicModelingLDA researcherTopicModelingLDA;
	
	@Autowired( required = false )
	private ResearcherTopicModelingNGram researcherTopicModelingNGram;
	
	@Autowired( required = false )
	private ResearcherTopicModelingTOT researcherTopicModelingTOT;
	
	@Autowired( required = false )
	private ResearcherTopicModelingDiscreteLDA researcherTopicModelingDiscreteLDA;
	
	@Autowired( required = false )
	private ResearcherTopicModelingLabeledLDA researcherTopicModelingLabeledLDA;

	@Autowired( required = false )
	private ResearcherTopicModeling researcherTopicModeling;

	@Override
	public ResearcherAcademicEventTree getResearcherAcademicEventTree()
	{
		if ( this.researcherAcademicEventTree == null )
			this.researcherAcademicEventTree = new ResearcherAcademicEventTreeImpl();

		return this.researcherAcademicEventTree;
	}

	@Override
	public ResearcherApi getResearcherApi()
	{
		if ( this.researcherApi == null )
			this.researcherApi = new ResearcherApiImpl();

		return this.researcherApi;
	}

	@Override
	public ResearcherBasicInformation getResearcherBasicInformation()
	{
		if ( this.researcherBasicInformation == null )
			this.researcherBasicInformation = new ResearcherBasicInformationImpl();

		return this.researcherBasicInformation;
	}

	@Override
	public ResearcherCoauthor getResearcherCoauthor()
	{
		if ( this.researcherCoauthor == null )
			this.researcherCoauthor = new ResearcherCoauthorImpl();

		return this.researcherCoauthor;
	}

	// @Override
	// public ResearcherRecommededauthor getResearcherRecommendedauthor()
	// {
	// if ( this.researcherRecommendedauthor == null )
	// this.researcherRecommendedauthor = new ResearcherRecommendedauthorImpl();
	//
	// return this.researcherRecommendedauthor;
	// }

	@Override
	public ResearcherSimilarauthor getResearcherSimilarauthor()
	{
		if ( this.researcherSimilarauthor == null )
			this.researcherSimilarauthor = new ResearcherSimilarauthorImpl();

		return this.researcherSimilarauthor;
	}

	@Override
	public ResearcherTopicEvolutionTest getResearcherDynamicTopicModellingauthorTest()
	{
		if ( this.researcherTopicEvolutionTest == null )
			this.researcherTopicEvolutionTest = new ResearcherTopicEvolutionImplTest();

		return this.researcherTopicEvolutionTest;
	}

	@Override
	public ResearcherTopicCompositionCloud getResearcherTopicModelingCloud()
	{
		if ( this.researcherTopicCloud == null )
			this.researcherTopicCloud = new ResearcherTopicCompositionCloudImpl();

		return this.researcherTopicCloud;
	}

	@Override
	public ResearcherInterest getResearcherInterest()
	{
		if( this.researcherInterest == null )
			this.researcherInterest = new ResearcherInterestImpl();

		return this.researcherInterest;
	}

	@Override
	public ResearcherInterestEvolution getResearcherInterestEvolution()
	{
		if( this.researcherInterestEvolution == null )
			this.researcherInterestEvolution = new ResearcherInterestEvolutionImpl();

		return this.researcherInterestEvolution;
	}

	@Override
	public ResearcherMining getResearcherMining()
	{
		if ( this.researcherMining == null )
			this.researcherMining = new ResearcherMiningImpl();

		return this.researcherMining;
	}

	@Override
	public ResearcherPublication getResearcherPublication()
	{
		if ( this.researcherPublication == null )
			this.researcherPublication = new ResearcherPublicationImpl();

		return this.researcherPublication;
	}

	@Override
	public ResearcherSearch getResearcherSearch()
	{
		if ( this.researcherSearch == null )
			this.researcherSearch = new ResearcherSearchImpl();

		return this.researcherSearch;
	}

	@Override
	public ResearcherTopPublication getResearcherTopPublication()
	{
		if ( this.researcherTopPublication == null )
			this.researcherTopPublication = new ResearcherTopPublicationImpl();

		return this.researcherTopPublication;
	}

	@Override
	public ResearcherTopicModeling getResearcherTopicModeling()
	{
		if ( this.researcherTopicModeling == null )
			this.researcherTopicModeling = new ResearcherTopicModelingImpl();

		return this.researcherTopicModeling;
	}

	public ResearcherTopicModelingLDA getResearcherTopicModelingLDA()
	{
		if ( this.researcherTopicModelingLDA == null)
			this.researcherTopicModelingLDA = new ResearcherTopicModelingLDAImpl();
		return this.researcherTopicModelingLDA;
	}

	@Override
	public ResearcherTopicModelingNGram getResearcherTopicModelingNGram()
	{
		if ( this.researcherTopicModelingNGram == null)
			this.researcherTopicModelingNGram = new ResearcherTopicModelingNGramImpl();
		return this.researcherTopicModelingNGram;
	}

	@Override
	public ResearcherTopicModelingTOT getResearcherTopicModelingTOT()
	{
		if ( this.researcherTopicModelingTOT == null)
			this.researcherTopicModelingTOT = new ResearcherTopicModelingTOTImpl();
		return this.researcherTopicModelingTOT;
	}

	@Override
	public ResearcherTopicModelingDiscreteLDA getResearcherTopicModelingDiscreteLDA()
	{
		if ( this.researcherTopicModelingDiscreteLDA == null)
			this.researcherTopicModelingDiscreteLDA = new ResearcherTopicModelingDiscreteLDAImpl();
		return this.researcherTopicModelingDiscreteLDA;
	}

	@Override
	public ResearcherTopicModelingLabeledLDA getResearcherTopicModelingLabeledLDA()
	{
		if ( this.researcherTopicModelingLabeledLDA == null)
			this.researcherTopicModelingLabeledLDA = new ResearcherTopicModelingLabeledLDAImpl();
		return this.researcherTopicModelingLabeledLDA;
	}

}
