package de.rwth.i9.palm.analytics.algorithm.dynamicLDA;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Stack;

import org.apache.commons.math3.special.Beta;

public class TemporalTopicModel
{


	/**
	 * Topics over Time
	 * 
	 * For more information on the algorithm see: X. Wang and A. McCallum.
	 * Topics over time: a non-Markov continuous-time model of topical trends.
	 * KDD 2006.
	 * 
	 * Notes on the implementations as at
	 * http://comments.gmane.org/gmane.comp.ai.mallet.devel/1669
	 * 
	 * Author: Arnim Bleir
	 * Author: Piro Lena - updated "Estimate Beta parameters", Phi, Theta 
	 */

	public int[][] countTerm_Topic, countDoc_Topic;
	public int K, V;
	public int[] numOfWordsByTopic;
	public int[][] documents;
	public double alpha = 1.5, beta = 0.1;
	public int[][] z; // topic assigned to the ith token/term in the document d 
	public double[][] betaDistrByTopic;
	private double[] timeStamps;
	//public double[][] psmth;

	// get the number of topics by using mallet ( getTopic number)
	// get the length of documents by using mallet -> DocumentLengths
	// size of vocabulary by mallet -> getAlphabet.size()
	// number of documents InstanceList.data.size()
	// get the tokens for each of the documents data.get(docID).instance.getData()
		// if needed the length of document is data.get(docID).instance.getData().size()
	
	// Initialization phase point 1. of algorithm
	public void addInstances( int[][] documentsInput, double[] timeStamps, int sizeOfVocabulary, int numOfTopics )
	{
		documents = documentsInput;	
		K = numOfTopics;		// number of topics that need to be discovered in a corpora
		V = sizeOfVocabulary; 		// number of unique words
		z = new int[documents.length][];		// topic assigned to the ith token/term in the document d 
		this.timeStamps = timeStamps;		// normalized time interval chosen to analyze the data
		countTerm_Topic = new int[sizeOfVocabulary][K];		// countTerm_Topic (VxK) - terms in each of the topics 
		countDoc_Topic = new int[documents.length][K];		// countDoc_Topic ( N.docs x K) - topic appearance in all documents 
		numOfWordsByTopic = new int[K];		//  number of words for each of the K topics 
		//psmth = new double[documentsInput.length][numOfTopics];
		for ( int m = 0; m < documents.length; m++ )		// 
		{
			z[m] = new int[documents[m].length];
			for ( int n = 0; n < documents[m].length; n++ )
			{
				// random topic assignment for all tokens
				z[m][n] = (int) ( Math.random() * K );
				countTerm_Topic[documents[m][n]][z[m][n]]++;
				countDoc_Topic[m][z[m][n]]++;
				numOfWordsByTopic[z[m][n]]++;
			}
		}
		betaDistrByTopic = new double[K][];
		for ( int k = 0; k < K; k++ )
			// draw a timestamp t_di from beta distr.
			betaDistrByTopic[k] = estimateBetaParams( getTimeStamps( k ) );
	}

	public void run( int maxIter )
	{
		for ( int iter = 0; iter < maxIter; iter++ )
		{
			System.out.format( "iter: %04d\n", iter );
			double[] p;
			double pSum = 0.0, u;
			int topic, i, k;
			double[] tProb = new double[K]; // vector of topic probabilities for each timestamp 
			for ( int d = 0; d < documents.length; d++ )
			{
				for ( k = 0; k < K; k++ ){
					tProb[k] = ( ( Math.pow( 1 - timeStamps[d], betaDistrByTopic[k][0] - 1 ) * Math.pow( timeStamps[d], betaDistrByTopic[k][1] - 1 ) ) / beta( betaDistrByTopic[k][0], betaDistrByTopic[k][1] ) );
				}	
					for ( i = 0; i < documents[d].length; i++ )
				{
					p = new double[K];
					pSum = 0.0;
					topic = z[d][i];
					countTerm_Topic[documents[d][i]][topic]--;
					countDoc_Topic[d][topic]--;
					numOfWordsByTopic[topic]--;
					for ( k = 0; k < K; k++ )
					{
						pSum += ( countDoc_Topic[d][k] + alpha ) * ( ( countTerm_Topic[documents[d][i]][k] + beta ) / ( numOfWordsByTopic[k] + V * beta ) ) * tProb[k];
						p[k] = pSum;
					}
					u = Math.random() * pSum;
					for ( topic = 0; topic < K - 1; topic++ )
						if ( u < p[topic] )
							break;

					countTerm_Topic[documents[d][i]][topic]++;
					countDoc_Topic[d][topic]++;
					numOfWordsByTopic[topic]++;
					z[d][i] = topic;
				}
					
					
//					for ( k = 0; k < K; k++ ){
//						psmth[d][k] = pcopy[k];
//					}
			}
			
			for ( k = 0; k < K; k++ )
				betaDistrByTopic[k] = estimateBetaParams( getTimeStamps( k ) );
		}
	}

	public double[] getTimeStamps( int k )
	{
		Stack<Double> kStack = new Stack<Double>();
		for ( int m = 0; m < documents.length; m++ )
			for ( int n = 0; n < documents[m].length; n++ )
				if ( z[m][n] == k )
					kStack.push( timeStamps[m] );
		int stack_size = kStack.size();
		double[] _x = new double[stack_size];
		for ( int i = 0; i < stack_size; i++ )
			_x[i] = kStack.pop();
		return _x;
	}

	/*
	 * Method of Moments
	 * Modified as discussed in
	 * http://comments.gmane.org/gmane.comp.ai.mallet.devel/1669
	 */
	static double[] estimateBetaParams( double[] _x )
	{
		double[] shapes = new double[2];
		double sum = .0, _var = .0;
		int _n = _x.length;
		for ( double i : _x )
			sum = sum + i;
		double mean = sum / _n;
		for ( double i : _x )
			_var += Math.pow( mean - i, 2 );
		double variance = ( _var / _n ) + 0.001;
		double commonTerm = Math.abs( ( ( mean * ( 1.0 - mean ) ) / variance ) - 1.0 );
		shapes[0] = mean * commonTerm;
		shapes[1] = ( 1 - mean ) * commonTerm;
		return shapes;
	}

	static double beta( double a, double b )
	{
		return Math.exp( Beta.logBeta( a, b ) );
	}

	/*
	 * Calculate the theta parameter for the model 
	 * It represents the multinomial topic distribution for each document  
	 */
	public double[][] getTheta() {
        double[][] theta = new double[documents.length][K];
        for (int m = 0; m < documents.length; m++) {
            for (int k = 0; k < K; k++) {
                theta[m][k] = (countDoc_Topic[m][k] + alpha) / (documents[m].length + K * alpha);
            }
        }
        return theta;
   }
	
	
	/*
	 * Calculate the phi parameter for the model 
	 */
    public double[][] getPhi() {
        double[][] phi = new double[K][V];
        for (int k = 0; k < K; k++) {
            for (int w = 0; w < V; w++) {
                phi[k][w] = (countTerm_Topic[w][k] + beta) / (numOfWordsByTopic[k] + V * beta);
            }
        }
        return phi;
    }
    
	/*
	 * return indicies of the words for each topic ordered by their probability 
	 * of appearance in that toppic
	 */
	public Integer[][] sortedIndicesperTopic( double[][] phi )
	{
		Integer[][] indexes  = new Integer[phi.length][phi[0].length];
		for (int i=0; i < phi.length; i++){
			arrayIndexComparator comparator = new arrayIndexComparator(getDouble(phi[i]));
			indexes[i] = comparator.createIndexArray();
			Arrays.sort(indexes[i], comparator);
		}
		return indexes;
	}

	/*
	 * Convert from double to Double
	 */
	public  Double[] getDouble(double[] array){
		Double[] d = new Double[array.length];
		int i = 0;
		for (double arr : array)
			d[i++] = arr;
		return d;
	}
	

	/*
	 * Convert from integer to Integer
	 */
	public  Integer[] getInteger(int[] array){
		Integer[] d = new Integer[array.length];
		int i = 0;
		for (int arr : array)
			d[i++] = arr;
		return d;
	}
	
	/*
	 * Eliminate duplicates to from an array
	 */
	public static String[] removeDuplicate(String[] words) {
        Set<String> wordSet = new LinkedHashSet<String>();
        for (String word : words) {
            wordSet.add(word);
        }
        return wordSet.toArray(new String[wordSet.size()]);
    }
}
