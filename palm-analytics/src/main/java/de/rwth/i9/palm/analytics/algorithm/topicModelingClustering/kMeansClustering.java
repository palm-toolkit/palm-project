package de.rwth.i9.palm.analytics.algorithm.topicModelingClustering;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.junit.Test;

import de.rwth.i9.palm.analytics.algorithm.ngram.Ngram;

public class kMeansClustering
{
	public int numClusters; // number of centroids is the same as this one
	public int numDimenstions; // equivalent to the number of topics
	public double[][] dataset; // holds all the data points (N. docs x N.
								// topics)
	public double[][] centroids; // holds informations regarding centroids
									// (N.clusters x N. topics)
	public double[][] newcentroids;
	public int[][] clusters; // holds values 0/1 to show the cluster assignment
								// for each doc (N. clusters x N. docs)
	public double[][] distanceCentroids;

	public Ngram ngrams = new Ngram();
	public int numIterations = 500;

	@Test
	public void test() throws Exception
	{

		try
		{	
			kMeans( 10, 5 );

		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
	}

	// Convert the results of topic modeling maps into two dimensional array
	public double[][] addInstances( HashMap<String, List<Double>> documentsTopicDistribution, int numDocuments, int numTopics )
	{
		double[][] dataset = new double[numDocuments][numTopics];
		int i = 0;
		for ( Entry<String, List<Double>> document : documentsTopicDistribution.entrySet() )
		{
			int j = 0;
			for ( double distribution : document.getValue() )
			{
				dataset[i][j++] = distribution;
			}
			i++;
		}
		return dataset;
	}


	// Run the topic modeling in order to get the topic proportions for each of
	// the entities
	public double[][] getTopicModelingResults( Ngram ngrams )
	{
		// ngrams.createModel( path, purpose, specify, 20 );
		double[][] topicModelingResults = addInstances( ngrams.getDoumentTopicProportion(), ngrams.getNumInstances(), ngrams.getNumTopics() );
		return topicModelingResults;
	}

	// Pick randomly the initial k centroids ( k x N.Topics)
	public double[][] assignRandomCentroids( double[][] data, int numCentroids )
	{
		double initialCentroids[][] = new double[numCentroids][data[0].length];
		ArrayList<Integer> list = new ArrayList<Integer>();
		for ( int i = 0; i < data.length; i++ )
		{
			list.add( new Integer( i ) );
		}
		Collections.shuffle( list );
		for ( int i = 0; i < numCentroids; i++ )
		{
			initialCentroids[i] = data[list.get( i )];
		}
		return initialCentroids;
	}

	// Returns an array of distances of a datapoint from each of the centroids (
	// 1 x k)
	public double[] distancePointCentroids( double[] point, double[][] centroids )
	{
		double[] distanceFromCentroids = new double[centroids.length];
		for ( int i = 0; i < centroids.length; i++ )
		{
			similarityMeasures similarity = new similarityMeasures( point, centroids[i] );
			distanceFromCentroids[i] = similarity.sqrtEuclidianSimilarity( point, centroids[i] );
		}
		return distanceFromCentroids;
	}

	// Returns a 2 dimensional array holding distance of each datapoint from all
	// other centroids (N.Docs x k)
	public double[][] distancePointsfromCentroids( double[][] centroids, double[][] dataset )
	{
		double[][] distanceCentroids = new double[dataset.length][centroids.length];
		for ( int i = 0; i < dataset.length; i++ )
		{
			distanceCentroids[i] = distancePointCentroids( dataset[i], centroids );
		}
		return distanceCentroids;
	}

	// Calculates the nearest centroid to a given datapoint and assigns it to
	// the specific cluster ( k x N.docs)
	public int[][] createClusters( double[][] distanceCentroids )
	{
		int[][] clusters = new int[distanceCentroids[0].length][distanceCentroids.length];
		for ( int i = 0; i < distanceCentroids.length; i++ )
		{
			int temp = findNearestCentroid( distanceCentroids[i] );
			clusters[temp][i] = 1;
		}
		return clusters;
	}

	// Finds the minimal distance of a point to other centroids (index of the
	// minimal element - which represents the nearest centroid)
	public int findNearestCentroid( double[] centroids )
	{
		int nearestCentroid = -1;
		double min = centroids[0];
		for ( int i = 0; i < centroids.length; i++ )
		{
			if ( centroids[i] <= min )
			{
				min = centroids[i];
				nearestCentroid = i;
			}
		}
		return nearestCentroid;
	}

	// Perform the centroid shiffting to the new centroids of all clusters ( k x
	// N.Topics)
	public double[][] assignNewCentroids( double[][] data, int[][] actualClusters, int numClusters )
	{
		double[][] newCentroids = new double[numClusters][data[0].length];

		for ( int i = 0; i < actualClusters.length; i++ )
		{
			int countPoints = 0;
			double[] temporalSum = new double[data[0].length];
			for ( int j = 0; j < actualClusters[i].length; j++ )
			{
				if ( actualClusters[i][j] == 1 )
				{
					countPoints++;
					temporalSum = sumElementwiseofTopics( temporalSum, data[j] );
				}
			}
			newCentroids[i] = calculateTheMean( temporalSum, countPoints );
		}
		return newCentroids;
	}

	// This method is used to calculate the elementwise sum of two arrays used
	// to find the mean for centroids (datapoints from the same cluster)
	public double[] sumElementwiseofTopics( double[] document1, double[] document2 )
	{
		double[] sumTopicProportion = new double[document1.length > document2.length ? document1.length : document2.length];
		for ( int i = 0; i < sumTopicProportion.length; i++ )
		{
			sumTopicProportion[i] = document1[i] + document2[i];
		}
		return sumTopicProportion;
	}

	// This method is used to calculate the mean for centroids
	public double[] calculateTheMean( double[] temporalSum, int numElements )
	{
		double[] mean = new double[temporalSum.length];
		int i = 0;
		for ( double element : temporalSum )
		{
			if ( numElements != 0 )
			{
				mean[i++] = element / numElements;
			}
			else
			{
				mean[i++] = element;
			}
		}
		return mean;
	}

	// The criteria used to stop the clustering unless the maximal number of
	// iterations is not yet reached. If we have on stable condition 90%> of
	// centroids then the calculation of new centroids stops.
	public boolean stopCriteria( double[][] oldCentroids, double[][] newCentroids )
	{
		boolean stop = false;
		int countBooleans = 0;
		boolean[] nonrealocated = new boolean[oldCentroids.length > newCentroids.length ? oldCentroids.length : newCentroids.length];
		for ( int i = 0; i < nonrealocated.length; i++ )
		{
			if ( centroidRealocation( oldCentroids[i], newCentroids[i] ) )
			{
				nonrealocated[i] = true;
			}
		}

		for ( boolean bool : nonrealocated )
		{
			if ( bool == true )
				countBooleans++;
		}

		if ( countBooleans / nonrealocated.length >= 1 )
			stop = true;

		return stop;
	}

	// the condition to decide if the old and new centroid are significant
	// dissimilar to each other (dissimilarity rate is to scale 1e-6
	public boolean centroidRealocation( double[] firstcentroid, double[] secondcentroid )
	{
		boolean isSimilar = false;
		similarityMeasures similarity = new similarityMeasures( firstcentroid, secondcentroid );
		if ( similarity.sqrtEuclidianSimilarity( firstcentroid, secondcentroid ) < 0.000001 )
		{
			isSimilar = true;
		}
		return isSimilar;
	}

	// Jagota Measurement - Measuring Clusters Quality
	// Q = Sum (1.k) 1/ |Ci| * Sum (x) d(x, Mean)
	public double interclusterSimilarity( double[][] centroids, double[][] dataset, int[][] actualClusters )
	{
		double interclusterSimilarity = 0;
		similarityMeasures similarity = new similarityMeasures();
		for ( int i = 0; i < actualClusters.length; i++ )
		{
			int countPoints = 0;
			double temporalSum = 0;
			for ( int j = 0; j < actualClusters[i].length; j++ )
			{
				if ( actualClusters[i][j] == 1 )
				{
					countPoints++;
					temporalSum += similarity.sqrtEuclidianSimilarity( centroids[i], dataset[j] );
				}
				interclusterSimilarity += 1 / countPoints * temporalSum;
			}
		}
		return interclusterSimilarity;
	}

	// Copies the actual results of centroids to be kept for the later
	// calculations. ( k x N.Topics)
	public double[][] copyofOldCentroids( double[][] actualCentroids )
	{
		if ( actualCentroids == null )
			return null;
		double[][] result = new double[actualCentroids.length][];
		for ( int i = 0; i < actualCentroids.length; i++ )
		{
			result[i] = actualCentroids[i].clone();
		}
		return result;
	}

	// map the created clusters with the actual documents
	// TO DO * Create hashmaps for the results
	public void showClusteredData( int[][] clusters, Ngram ngrams )
	{
		for ( int i = 0; i < clusters.length; i++ )
		{
			System.out.println( "Cluster: " + i );

			for ( int j = 0; j < clusters[i].length; j++ )
			{
				if ( clusters[i][j] == 1 )
				{
					System.out.println( ngrams.tng.ilist.get( j ).getSource().toString().replace( "\\", ";" ).split( ";" )[6].replace( ".txt", "" ) );
				}
			}
		}
	}

	// returns an 2d-array (k x number of elements clustered together)
	public String[][] getClusteredInstances( int[][] clusters, Ngram ngrams )
	{
		String[][] clusteredInstances = new String[clusters.length][];
		for ( int i = 0; i < clusters.length; i++ )
		{
			int k = 0;
			for ( int j = 0; j < clusters[i].length; j++ )
			{
				if ( clusters[i][j] == 1 )
				{
					clusteredInstances[i][k++] = ngrams.tng.ilist.get( j ).getSource().toString().replace( "\\", ";" ).split( ";" )[6].replace( ".txt", "" );
				}
			}
		}
		return clusteredInstances;
	}


	// for each cluster find the most representative topics based on final
	// centroids
	public ArrayList<ArrayList<String>> getDominantclusterTopics( double[][] centroids, Ngram ngrams )
	{
		ArrayList<ArrayList<String>> clusterTopics = new ArrayList<ArrayList<String>>( centroids.length );
		List<String> topics = ngrams.getListTopicsNgrams( ngrams.tng, 10, false );
		for (int i=0; i < centroids.length; i++){
			int k=0;
			ArrayList<String> temp = new ArrayList<String>( centroids[i].length );
			for ( int j = 0; j < centroids[i].length; j++ )
			{
				if ( centroids[i][j] >= 0.05 )
				{
					temp.add( topics.get( j ) );
				}
			}
			clusterTopics.add( temp );
		}

		return clusterTopics;
	}

	// The main running method which performs the K-Means clustering
	public void kMeans( int numIterations, int numClusters )
	{
		// get the dataset from the topic distribution
		dataset = getTopicModelingResults( ngrams );
		for ( int i = 0; i < dataset.length; i++ )
		{
			for ( int j = 0; j < dataset[i].length; j++ )
			{
				System.out.print( dataset[i][j] + " " );
			}
			System.out.println();
		}
		// pick random points to start the clustering
		centroids = assignRandomCentroids( dataset, numClusters );
		System.out.println( "_____________________________" );
		for ( int i = 0; i < centroids.length; i++ )
		{
			for ( int j = 0; j < centroids[i].length; j++ )
			{
				System.out.print( centroids[i][j] + " " );
			}
			System.out.println();
		}
		newcentroids = new double[centroids.length][centroids[0].length];

		// one of the stopping criteria (finishing the number of the number of
		// iterations planned to converge)
		// continue as long as the stopping criteria is not fulfilled
		while ( numIterations > 0 || !stopCriteria( centroids, newcentroids ) )
		{
				System.out.println( "iteration: " + numIterations );

				// create a copy of the newcentroids
				centroids = copyofOldCentroids( newcentroids );
				System.out.println( "_____________________________" );
				for ( int i = 0; i < centroids.length; i++ )
				{
					for ( int j = 0; j < centroids[i].length; j++ )
					{
						System.out.print( centroids[i][j] + " " );
					}
					System.out.println();
				}

				// find the distance of each point from each of the centroids
				distanceCentroids = distancePointsfromCentroids( centroids, dataset );
				System.out.println( "_____________________________" );
				for ( int i = 0; i < distanceCentroids.length; i++ )
				{
					for ( int j = 0; j < distanceCentroids[i].length; j++ )
					{
						System.out.print( distanceCentroids[i][j] + " " );
					}
					System.out.println();
				}

				// assign each of the points to the nearest cluster center
				clusters = createClusters( distanceCentroids );
				System.out.println( "_____________________________" );
				for ( int i = 0; i < clusters.length; i++ )
				{
					for ( int j = 0; j < clusters[i].length; j++ )
					{
						System.out.print( clusters[i][j] + " " );
					}
					System.out.println();
				}

				// calculate the new cluster centers
				newcentroids = assignNewCentroids( dataset, clusters, numClusters );
				System.out.println( "_____________________________" );
				for ( int i = 0; i < newcentroids.length; i++ )
				{
					for ( int j = 0; j < newcentroids[i].length; j++ )
					{
						System.out.print( newcentroids[i][j] + " " );
					}
					System.out.println();
				}
			numIterations--;
			}
		showClusteredData( clusters, ngrams );

		ArrayList<ArrayList<String>> instancedata = getDominantclusterTopics( newcentroids, ngrams );
		for ( int i = 0; i < instancedata.size(); i++ )
		{
			System.out.println( "Topics representing Cluster " + i );
			for ( int j = 0; j < instancedata.get( i ).size(); j++ )
			{
				System.out.println( instancedata.get( i ).get( j ) );
			}
		}

	}
}
