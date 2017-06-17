package de.rwth.i9.palm.feature.researcher;

/**
 * Factory interface for features on Researcher
 * 
 * @author sigit
 *
 */
public interface ResearcherFeature
{
	public ResearcherAcademicEventTree getResearcherAcademicEventTree();

	public ResearcherApi getResearcherApi();

	public ResearcherBasicInformation getResearcherBasicInformation();

	public ResearcherCoauthor getResearcherCoauthor();

	// public ResearcherRecommededauthor getResearcherRecommendedauthor();

	public ResearcherSimilarauthor getResearcherSimilarauthor();

	public ResearcherTopicEvolutionTest getResearcherDynamicTopicModellingauthorTest();

	public ResearcherTopicCompositionCloud getResearcherTopicModelingCloud();

	public ResearcherInterest getResearcherInterest();
	
	public ResearcherInterestEvolution getResearcherInterestEvolution();

	public ResearcherMining getResearcherMining();

	public ResearcherPublication getResearcherPublication();

	public ResearcherSearch getResearcherSearch();

	public ResearcherTopicModeling getResearcherTopicModeling();

	public ResearcherTopPublication getResearcherTopPublication();
	
	public ResearcherTopicModelingLDA getResearcherTopicModelingLDA();
	
	public ResearcherTopicModelingNGram getResearcherTopicModelingNGram();
	
	public ResearcherTopicModelingTOT getResearcherTopicModelingTOT();
	
	public ResearcherTopicModelingDiscreteLDA getResearcherTopicModelingDiscreteLDA();
	
	public ResearcherTopicModelingLabeledLDA getResearcherTopicModelingLabeledLDA();
	

}
