package de.rwth.i9.palm.analytics.algorithm.ngram;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import cc.mallet.types.InstanceList;

public interface NGrams
{
	/**
	 * 
	 * @param path
	 * @param purpose
	 * @param specify
	 * @return
	 * @throws IOException
	 */
	public InstanceList getInstanceDataDirectoryLevel( String path, String purpose, String specify ) throws IOException;
	
	/**
	 * 
	 * @param docID
	 * @param numWords
	 * @param weight
	 * @return
	 */
	public HashMap<String, List<String>> getEvolutionofTopicOverTime( TopicalNGrams model, int docID, int numWords, boolean weight );

	/**
	 * 
	 * @param numTopics
	 * @return
	 */
	public TopicalNGrams setNumberTopics( int numTopics );
	
	/**
	 * 
	 * @return number of topics existing in the model
	 */
	public int getNumTopics();

	/**
	 * 
	 * @return the number of instances of the model 1 instance created for each
	 *         document
	 */
	public int getNumInstances();

	/**
	 * 
	 * @param path
	 * @param purpose
	 * @param specify
	 * @param numTopics
	 * @return
	 * @throws IOException
	 */
	public TopicalNGrams createModel( String path, String purpose, String specify, int numTopics ) throws IOException;
	
	/**
	 * 
	 * @param path
	 * @param purpose
	 * @param entityId
	 * @param numTopics
	 * @return
	 */
	public TopicalNGrams createModelRevised( String path, String purpose, String entityId, int numTopics );
	
	/**
	 * 
	 * @param path
	 * @param purpose
	 * @param entityId
	 * @param numTopics
	 * @return
	 */
	public TopicalNGrams useTrainedData( String path, String purpose, String entityId, int numTopics );
	
	/**
	 * 
	 * @param path
	 * @param purpose
	 * @param entityId
	 * @param numTopics
	 * @return
	 */
	public TopicalNGrams useTrainedDataRevised( String path, String purpose, String entityId, int numTopics );
	
	/**
	 * 
	 * @param m
	 * @param path
	 * @param purpose
	 * @param specify
	 */
	public void printDocTopicprobs(TopicalNGrams m,String path, String purpose, String specify);
		
	/**
	 *  
	 * @param m
	 * @param threshold
	 * @param max
	 * @param weight
	 * @return
	 */
	public String[] getStringDocumentTopicIndex (TopicalNGrams m, double threshold, int max, boolean weight);
	
	/**
	 *  
	 * @param m
	 * @param threshold
	 * @param max
	 * @param weight
	 * @return
	 */
	public List<String> getListDocumentTopicIndex (TopicalNGrams m, double threshold, int max, boolean weight);
	
	/**
	 * 
	 * @param m
	 * @return
	 */
	public HashMap<String, List<Double>> getDoumentTopicProportion();
	
	/**
	 * 
	 * @param m
	 * @param nwords
	 * @param weight
	 * @return
	 */
	public String[] getStringTopicsUnigrams (TopicalNGrams m, int nwords, boolean weight);

	/**
	 * 
	 * @param m
	 * @param nwords
	 * @param weight
	 * @return
	 */
	public List<String> getListTopicsUnigrams (TopicalNGrams m, int nwords, boolean weight);
	
	/**
	 *  
	 * @param m
	 * @param nwords
	 * @param weight
	 * @return
	 */
	public String[] getStringTopicsNgrams (TopicalNGrams m, int nwords, boolean weight);

	/**
	 * 
	 * @param m
	 * @param nwords
	 * @param weight
	 * @return
	 */
	public List<String> getListTopicsNgrams (TopicalNGrams m, int nwords, boolean weight);	
		

	/**
	 * 
	 * @param path
	 * @param purpose
	 */
	public void getRandomTrainerFiles(String path, String purpose);

		
	/**
	 *  
	 * @param m
	 * @param docID
	 * @param max
	 * @param threshold
	 * @param numTopics
	 * @param numWords
	 * @param weight
	 * @return
	 */
	public HashMap<String, List<String>> getTopicNgramsDocument( TopicalNGrams model, int docID, int max, double threshold, int numTopics, int numWords, boolean weight );

	/**
	 * 
	 * @param docID
	 * @param max
	 * @param threshold
	 * @param numTopics
	 * @param numWords
	 * @param weight
	 * @return
	 */
	public HashMap<String, List<String>> getTopTopicUnigramsDocument( TopicalNGrams model, int docID, int max, double threshold, int numTopics, int numWords, boolean weight );

	/**
	 * 
	 * @param docID
	 * @param max
	 * @param threshold
	 * @param numTopics
	 * @param numWords
	 * @param weight
	 * @return
	 */
	public HashMap<String, List<String>> getTopTopicNgramsDocument( TopicalNGrams model, int docID, int max, double threshold, int numTopics, int numWords, boolean weight );

	/**
	 * 
	 * @param m
	 * @param docID
	 * @param max
	 * @param threshold
	 * @param numTopics
	 * @param numWords
	 * @param weight
	 * @return
	 */
	public HashMap<String, List<String>> getTopicUnigramsDocument( TopicalNGrams model, int docID, int max, double threshold, int numTopics, int numWords, boolean weight );
	
	/**
	 * 
	 * @param tng
	 * @param choise
	 * @return
	 */
	public HashMap<String, List<String>> calculateSimilarity( int choise, int maxresult );
	
	/**
	 * 
	 * @param id
	 * @param maxresult
	 * @return
	 */
	public List<String> similarEntities( String id, int maxresult, int similarityMeasure );

	/**
	 *  
	 * @param tng
	 * @return
	 */
	public HashMap<String, List<String>> recommendSimilar( int maxresult );

	/**
	 * 
	 * @param similarityMeasure
	 * @param maxresult
	 * @return
	 */
	public HashMap<String, LinkedHashMap<String, Double>> calculateSimilarityMap( int similarityMeasure, int maxresult );

	/**
	 * 
	 * @param id
	 * @param maxresult
	 * @param similarityMeasure
	 * @return
	 */
	public List<String> similarEntitiesMap( String id, int maxresult, int similarityMeasure );
	/**
	 * 
	 * @param id
	 * @param maxresult
	 * @return
	 */
	public List<String> recommendedEntity( String id, int maxresult );

	/**
	 * 
	 * @param m
	 * @param max
	 * @param threshold
	 * @param numTopics
	 * @param numWords
	 * @param weight
	 * @return
	 */
	public LinkedHashMap<String, LinkedHashMap<String, Double>> getDocumentTopicDetailMap( TopicalNGrams m, int max, double threshold, int numTopics, int numWords, boolean weight );

	/**
	 * 
	 * @param m
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public int maptoRealDatabaseID( String id, TopicalNGrams model ) throws Exception;

	/**
	 * 
	 * @param model
	 * @param id
	 * @param maxresult
	 * @param simialrityMeasure
	 * @param numTopics
	 * @return
	 */
	public HashMap<String, List<String>> getTopicLevelSimilarity( TopicalNGrams model, String id, int maxresult, int simialrityMeasure, int numTopics );

	/**
	 * 
	 * @param id
	 * @param path
	 * @param purpose
	 * @param maxnumTopics
	 * @param numTopics
	 * @param numWords
	 * @param weight
	 * @param createmodel
	 * @param unigram
	 * @return
	 */
	public HashMap<String, List<String>> runTopicComposition( String id, String path, String purpose, int numTopics, int maxnumberTopics, int numWords, boolean weight, boolean createmodel, boolean unigram );

	/**
	 * 
	 * @param path
	 * @param purpose
	 * @param id
	 * @param numWords
	 * @param numTopics
	 * @param maxnumTopics
	 * @param createmodel
	 * @param weight
	 * @param unigrams
	 * @return
	 */
	public LinkedHashMap<String, List<String>> runDiscreteTopicEvolution( String path, String purpose, String id, int numWords, int numTopics, int maxnumTopics, boolean createmodel, boolean weight, boolean unigrams );

	/**
	 * 
	 * @param id
	 * @param path
	 * @param purpose
	 * @param numTopics
	 * @param maxResult
	 * @param similarityMeasure
	 * @param createModel
	 * @return
	 */
	public List<String> runSimilarEntities( String id, String path, String purpose, int numTopics, int maxResult, int similarityMeasure, boolean createModel );

	public HashMap<String, List<String>> runSimilarEntitiesContributorsTopicLevel( String id, String path, String purpose, int numTopics, int maxresult, int similarityMeasure, boolean createModel );

	/**
	 * 
	 * @param id
	 * @param path
	 * @param purpose
	 * @param numTopics
	 * @param maxResult
	 * @param similarityMeasure
	 * @param createModel
	 * @return
	 */
	public HashMap<String, List<String>> runSimilarEntitiesTopicLevel( String id, String path, String purpose, int numTopics, int maxResult, int similarityMeasure, boolean createModel );

	/**
	 * 
	 * @param path
	 * @param purpose
	 * @param id
	 * @param numTopics
	 * @param maxnumTopics
	 * @param numWords
	 * @param createmodel
	 * @param unigrams
	 * @return
	 */
	public HashMap<String, Double> runweightedTopicComposition( String path, String purpose, String id, int numTopics, int maxnumTopics, int numWords, boolean createmodel, boolean unigrams );

	/**
	 * 
	 * @param id
	 * @param path
	 * @param purpose
	 * @param numTopics
	 * @param maxnumberTopics
	 * @param numWords
	 * @param weight
	 * @param createmodel
	 * @param unigram
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, List<String>> runTopicCompositionHighLevel( String id, String path, String purpose, int numTopics, int maxnumberTopics, int numWords, boolean weight, boolean createmodel, boolean unigram ) throws Exception;

	/**
	 * 
	 * @param path
	 * @param purpose
	 * @param authorIds
	 * @param publicationId
	 * @param numTopics
	 * @param maxnumTopics
	 * @param numWords
	 * @param createmodel
	 * @param unigrams
	 * @return
	 */
	public HashMap<String, List<String>> runTopicsFromListofEntities( String path, String purpose, List<String> authorIds, String publicationId, int numTopics, int maxnumTopics, int numWords, boolean createmodel, boolean unigrams, boolean wordweight );

	/**
	 * 
	 * @param model
	 * @param docID
	 * @param max
	 * @param threshold
	 * @param numTopics
	 * @param numWords
	 * @param weight
	 * @return
	 */
	public HashMap<String, List<String>> getTopicNgramsDocumentWordWeight( TopicalNGrams model, int docID, int max, double threshold, int numTopics, int numWords, boolean weight );

	/**
	 * 
	 * @param model
	 * @param docID
	 * @param max
	 * @param threshold
	 * @param numTopics
	 * @param numWords
	 * @param weight
	 * @return
	 */
	public HashMap<String, List<String>> getTopicUnigramsDocumentWordWeigth( TopicalNGrams model, int docID, int max, double threshold, int numTopics, int numWords, boolean weight );

	/**
	 * 
	 * @param path
	 * @param purpose
	 * @param id
	 * @param numTopics
	 * @param maxnumTopics
	 * @param numWords
	 * @param createmodel
	 * @param unigrams
	 * @return
	 */
	public HashMap<String, Double> runweightedTopicCompositionforPublications( String path, String purpose, String id, List<String> authorIds, int numTopics, int maxnumTopics, int numWords, boolean createmodel, boolean unigrams );

	/**
	 * 
	 * @param model
	 * @param id
	 * @param maxresult
	 * @param simialrityMeasure
	 * @param numTopics
	 * @return
	 */
	public HashMap<String, List<String>> getTopicLevelSimilarityTopMinDelta( TopicalNGrams model, String id, int maxresult, int simialrityMeasure, int numTopics );

	/**
	 * 
	 * @param path
	 * @param purpose
	 * @return
	 * @throws IOException
	 */
	public Boolean dateCheckCriteria(String path, String purpose, String Id) throws IOException;

}
