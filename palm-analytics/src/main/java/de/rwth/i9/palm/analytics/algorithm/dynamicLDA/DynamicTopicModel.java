package de.rwth.i9.palm.analytics.algorithm.dynamicLDA;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeSet;

import cc.mallet.types.IDSorter;
import cc.mallet.types.InstanceList;

public interface DynamicTopicModel
{
	
	/**
	 * 
	 * @param path
	 * @param purpose
	 * @return
	 */
	public InstanceList getInstanceData(String path, String purpose);
	
	/**
	 * 
	 * @param numTopics
	 * @return
	 */
	public int setNumberTopics( int numTopics );
	
	/**
	 * 
	 * @param path
	 * @param purpose
	 * @param numTopics
	 * @param numWords
	 * @return
	 */
	public ParallelTopicModel createModel( String path, String purpose, int numTopics, int numWords );
	
	/**
	 * 
	 * @param lda
	 * @return
	 */
	public int getNumTopics();
	
	/**
	 * 
	 * @param m
	 * @param nwords
	 * @param path
	 * @param purpose
	 * @param specify
	 */
	public void printTopWords( int nwords, String path, String purpose, String specify );
	
	/**
	 * 
	 * @param m
	 * @param path
	 * @param purpose
	 * @param specify
	 */
	public void printDocTopicprobs( String path, String purpose, String specify );
	
	/**
	 * 
	 * @param m
	 * @param path
	 * @param purpose
	 * @param specify
	 */
	public void evaluateModel( String path, String purpose, String specify );
	
	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	public File createTempDirectory() throws IOException;
	
	/**
	 * 
	 * @param path
	 * @param purpose
	 */
	public void getRandomTrainerFiles( String path, String purpose );
	
	/**
	 * 
	 * @param path
	 * @param purpose
	 * @param specify
	 * @throws IOException
	 */
	public void DocTopicMapper( String path, String purpose, String specify ) throws IOException;
	
	/**
	 * 
	 * @param trainedModel
	 * @param path
	 * @param purpose
	 * @param specify
	 * @param docID
	 * @return
	 * @throws IOException
	 */
	public double[] TopicInferencer( String path, String purpose, String specify, String docID ) throws IOException;
	
	/**
	 * 
	 * @param m
	 * @param nwords
	 * @return
	 */
	public String[] getStringTopics( int nwords );
	
	/**
	 * 
	 * @param m
	 * @param nwords
	 * @return
	 */
	public List<String> getListTopics( int nwords );
	

	/**
	 * 
	 * @param m
	 * @param threshold
	 * @param docID
	 * @param max
	 * @param numTopics
	 * @return
	 */
	public HashMap<Integer, Double> getTopicProportion(ParallelTopicModel m, double threshold, int docID, int max, int numTopics );
	public List<Double> getTopicProportion2( double threshold, int docID, int max, int numTopics );
	
	/**
	 * 
	 * @param threshold
	 * @param max
	 * @param numTopics
	 * @return
	 */
	public LinkedHashMap<String, List<Double>> getTopicDistributionforDocuments( ParallelTopicModel m, double threshold, int max, int numTopics );
	
	/**
	 * 
	 * @param m
	 * @param numTopics
	 * @return
	 */
	public ArrayList<TreeSet<IDSorter>> getSortedWords( int numTopics );
	
	/**
	 * 
	 * @param m
	 * @param docID
	 * @param max
	 * @param threshold
	 * @param numTopics
	 * @return
	 */
	public LinkedHashMap<String, String> getTopicDocument(int max, double threshold, int numTopics, int numWords );
	
	/**
	 * 
	 * @param m
	 * @param max
	 * @param threshold
	 * @param numTopics
	 * @return
	 */
	public HashMap<String, List<String>> getAllDocumentTopics( int max, double threshold, int numTopics );
	
	/**
	 * 
	 * @param passedMap
	 * @return
	 */
	public LinkedHashMap<Integer,Double> sortHashMapByValues(HashMap<Integer,Double> passedMap);
	
	/**
	 * 
	 * @param m
	 * @return
	 */
	public double[][] getTopicProbabilityAll();
	
	/**
	 * 
	 * @param m
	 * @return
	 */
	public int[][] generateTermFreqMatrix();
	
	/**
	 * 
	 * @param timerange
	 * @param numWords
	 * @param numiterations
	 * @return
	 */
	public TemporalTopicModel createTemporalTopicModel( int timerange, int numWords, int numiterations );
	/**
	 * 
	 * @param ndocs
	 * @param range
	 * @return
	 */
	public double[] generateTimestamps(int ndocs, int range);
	
	/**
	 * 
	 * @param m
	 * @return
	 */
	public int[][] wordIndicesperDocument();
	
	/**
	 * 
	 * @param m
	 * @param t
	 * @return
	 */
	public String[][] getWordsperTemporalTopic ();
	
	/**
	 * 
	 * @param m
	 * @param t
	 * @param nwords
	 * @return
	 */
	public String[][] getTopWordsperTemporalTopic (int nwords);
	
	/**
	 * 
	 * @param m
	 * @param t
	 * @param nwords
	 * @return
	 */
	public LinkedHashMap<String, List<String>> getTopWordperTemporalTopic(int nwords);
	
	/**
	 * 
	 * @param t
	 * @return
	 */
	public LinkedHashMap<Integer, List<Double>> getTemporalTopicDistribution();
	
	/**
	 * 
	 * @param t
	 * @return
	 */
	public LinkedHashMap<String, List<Double>> TemporalTopicEvolution();
	
	/**
	 * 
	 * @param theta
	 * @return
	 */
	public double[][] transposeMatrix( double[][] theta );
}
