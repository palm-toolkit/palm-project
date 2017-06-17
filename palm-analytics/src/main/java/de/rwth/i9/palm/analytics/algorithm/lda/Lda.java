package de.rwth.i9.palm.analytics.algorithm.lda;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.types.IDSorter;
import cc.mallet.types.InstanceList;

/**
 * An implementation of the LDA Algorithm. 
 * For the details regarding this algorithm check: https://www.cs.princeton.edu/~blei/papers/BleiNgJordan2003.pdf
 * <i> Automatic Bag of Words extraction from a text corpus </i>
 * 
 */

public interface Lda
{
	/**
	 * 
	 * @param path - Specifies where the list of documents is 
	 * @param purpose - Specifies one of the following: {Authors, Publications, Conferences, Years}
	 * @param specify - Define {Trainer, Infer}
	 * @return List of instances in the format: purpose-specify.mallet
	 */
	public InstanceList getInstanceData(String path, String purpose, String specify);
	
	/**
	 * 
	 * @param numTopics - Number of topics from which a documents is composed
	 * @return	Number of topics specified as a parameter
	 */
	public ParallelTopicModel setNumberTopics( int numTopics);
	
	/**
	 * 
	 * @param m - Parallel Topic Model already estimated (created)
	 * @return - Number of topics that were predefined on LDA implementation (50)
	 */
	public int getNumTopics(ParallelTopicModel m);
	
	/**
	 * 
	 * @param path
	 * @param purpose
	 * @param specify
	 * @param numTopics - Number of topics from which a document is composed
	 * @param numWords - Number of Words shown in a topic (from the implementation its one lower)
	 * @return - Topic Model trained with the specific parameters 
	 * 			 alphaSum = 50.0 (alpha = alphaSum/numTopics) 
	 * 			 beta = 0.01  ( beta = 1/numTopics
	 */
	public ParallelTopicModel createModel(String path, String purpose, String specify, int numTopics, int numWords);
	
	/**
	 * 
	 * @param m 
	 * @param nwords
	 * @param path
	 * @param purpose
	 * @param specify
	 * @return A file on the above specified path. This one contains the list of bag of words for each topic
	 */
	public void printTopWords(ParallelTopicModel m, int nwords,String path, String purpose, String specify);
	
	/**
	 * 
	 * @param m
	 * @param path
	 * @param purpose
	 * @param specify
	 * @return A file containing the topic distribution for each instance
	 */
	public void printDocTopicprobs(ParallelTopicModel m,String path, String purpose, String specify);
	
	/**
	 * 
	 * @param m
	 * @param nwords
	 * @return Array of Strings containing topicsalpha topic bag of words 
	 *		   Similar to the above method but the result is on Array format
	 */
	public String[] getStringTopics (ParallelTopicModel m, int nwords);
	
	/**
	 * 
	 * @param m
	 * @param nwords
	 * @return Similar to getStringTopics but in this case the result is of the form ArrayList
	 */
	public List<String> getListTopics (ParallelTopicModel m, int nwords);
	
	/**
	 * 
	 * @param m
	 * @param path
	 * @param purpose
	 * @param specify
	 * @return This method returns the perplexity calculation for the already trained model m
	 * 		   Refer to: <i> https://en.wikipedia.org/wiki/Perplexity </i>
	 */
	public void evaluateModel(ParallelTopicModel m, String path, String purpose, String specify);

	
	// /**
	// *
	// * @param path
	// * @param purpose
	// * @param specify
	// * @throws IOException
	// * Used when the results were on file format. Mapping list of topics
	// * with topic distribution for each instance.
	// */
	// public void DocTopicMapper(String path, String purpose, String specify)
	// throws IOException;
	
	/**
	 * 
	 * @param trainedModel
	 * @param path
	 * @param purpose
	 * @param specify
	 * @param docID - specifies the id of document in the format found on db. 
	 * @return	Array of doubles specifying the percentage of "appearance" of that topic[i] on this document
	 * 			The document is entered as a new instance on the existing vocabulary and uses the trained model
	 * @throws IOException
	 */
	public double[] TopicInferencer(ParallelTopicModel trainedModel, String path, String purpose, String specify, String docID) throws IOException;
	
	/**
	 * 
	 * @param m
	 * @param docID - integer referring to instance (from InstanceList) responsible for that document
	 * @param max - specifies the maximal number of topics generated for a corpus
	 * @param threshold - minimal topic distribution allowed to be considered significant of a document
	 * @param numTopics
	 * @param numWords
	 * @return - A HashMap<String, String> where key: TopicID as String (used later to be mapped to db-version id)
	 * 											 value: Bag of words for that topic
	 */
	public HashMap<String, String> getTopicDocument( ParallelTopicModel m, int docID, int max, double threshold, int numTopics, int numWords );
	
	
	/**
	 * 
	 * @param m
	 * @param docID
	 * @param max
	 * @param threshold
	 * @param numTopics
	 * @param numWords
	 * @return A HashMap<String, Double> where key: TopicID as String (used later to be mapped to db-version id)
	 * 											 value: Double specified the % of assignment of that topic to specific document
	 */
	public HashMap<String, Double> getTopicWeight( ParallelTopicModel m, int docID, int max, double threshold, int numTopics, int numWords );
	
	/**
	 * 
	 * @param m
	 * @param docID
	 * @param max
	 * @param threshold
	 * @param numTopics
	 * @return	A HashMap<String, List<String> where key: TopicID as String (used later to be mapped to db-version id)
	 * 											 value: List of Strings specifying topic ID "-"  % of assignment of that topic to specific document
	 *			Idea: Get all the significant topics for a specific document		
	 */
	public HashMap<String, List<String>> getAllDocumentTopics( ParallelTopicModel m, int docID, int max, double threshold, int numTopics );
	
	/**
	 * 
	 * @param m
	 * @param numTopics
	 * @return This method is part of mallet. Used to sort the words per topic accordingly to their significance 
	 */
	public ArrayList<TreeSet<IDSorter>> getSortedWords( ParallelTopicModel m, int numTopics );
}
