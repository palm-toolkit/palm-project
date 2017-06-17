
package de.rwth.i9.palm.analytics.algorithm.ngram;

import java.util.ArrayList;
import java.util.List;

import cc.mallet.util.Maths;
public class similarityMeasures
{
	double[] a;
	double[] b;
	
	public similarityMeasures(){
		
	}
	
	public similarityMeasures(double[] a, double[] b){
		this.a = a;
		this.b = b;
	}
	
	// cos = a*b/|a|*|b| = sum(a*b)/sqrt(a^2)*sqrt(b^2)
	public double cosineSimilarity(double[] a, double[] b){
		double sum = 0.0, s1 = 0.0, s2 = 0.0;
		for ( int i = 0; i < ( ( a.length >= b.length ) ? a.length : b.length ); i++ )
		{
			s1 += Math.pow( a[i],2 );
			s2 += Math.pow( b[i], 2 );
			sum += a[i] * b[i];
		}
		return sum/(Math.sqrt( s1 ) * Math.sqrt( s2 ));
	}
	
	public double cosineSimilarity(List<Double> a, List<Double> b){
		double sum = 0.0, s1 = 0.0, s2 = 0.0;
		for ( int i = 0; i < ( ( a.size() >= b.size() ) ? a.size() : b.size() ); i++ )
		{
			s1 += Math.pow(a.get( i ),2 );
			s2 += Math.pow( b.get( i ), 2 );
			sum += a.get( i ) * b.get(i);
		}
		return sum/(Math.sqrt( s1 ) * Math.sqrt( s2 ));
	}
	
	// eucl = |a-b| = sqrt ( a-b ) ^2 
	public double sqrtEuclidianSimilarity(double[] a, double[] b){
		double sum = 0.0;
		for ( int i = 0; i < ( ( a.length >= b.length ) ? a.length : b.length ); i++ )
		{
			sum += Math.pow( (a[i] - b[i]), 2 );
		}
		return Math.sqrt(sum);
	}
	
	public double sqrtEuclidianSimilarity(List<Double> a, List<Double> b){
		double sum = 0.0;
		for ( int i = 0; i < ( ( a.size() >= b.size() ) ? a.size() : b.size() ); i++ )
		{
			sum += Math.pow( (a.get( i ) - b.get(i)), 2 );
		}
		return Math.sqrt(sum);
	}
	
	// relEntropy = sum ( a * log (a/b) )
	public double relativeEntropySimilarity(double[] a, double[] b){
		double sum = 0.0;
		for ( int i = 0; i < ( ( a.length >= b.length ) ? a.length : b.length ); i++ )
		{
			if (b[i]!=0) {
				sum += a[i] * Math.log( a[i]/b[i]);
			} else {
				sum += 0;
			}
		}
		return sum;
	}
	
	public double relativeEntropySimilarity(List<Double> a, List<Double> b){
		double sum = 0.0;
		for ( int i = 0; i < ( ( a.size() >= b.size() ) ? a.size() : b.size() ); i++ )
		{
			if (b.get(i)!= 0){
				sum += a.get( i ) * Math.log( a.get( i )/b.get( i ));
			} else {
				sum += 0;
			}
			
		}
		return sum;
	}
	
	// calculates the Pearson correlation between two vectors 
	// https://en.wikipedia.org/wiki/Pearson_product-moment_correlation_coefficient
	public double pearsonCorrelation(double[] a, double[] b){
		double suma = 0.0, sumb = 0.0, suma2 = 0.0, sumb2 = 0.0, prod =0.0;
		double corr = 0.0;
		for ( int i = 0; i < ( ( a.length >= b.length ) ? a.length : b.length ); i++ )
		{
			suma += a[i];
			sumb += b[i];
			suma2 += Math.pow(a[i], 2);
			sumb2 += Math.pow(b[i], 2);
			prod += a[i]*b[i];
		}
		if (a.length > b.length){
			corr = ((a.length * prod - suma*sumb)/ (Math.sqrt( a.length * suma2 - Math.pow( suma, 2 )) * Math.sqrt( a.length * sumb2 - Math.pow( sumb, 2 ))));
		} else {
			corr = ((b.length * prod - suma*sumb)/ (Math.sqrt( b.length * suma2 - Math.pow( suma, 2 )) * Math.sqrt( b.length * sumb2 - Math.pow( sumb, 2 ))));
		}
		return corr;
	}
	
	public double pearsonCorrelation(List<Double> a, List<Double> b){
		double suma = 0.0, sumb = 0.0, suma2 = 0.0, sumb2 = 0.0, prod =0.0;
		double corr = 0.0;
		for ( int i = 0; i < ( ( a.size() >= b.size() ) ? a.size() : b.size() ); i++ )
		{
			suma += a.get( i );
			sumb += b.get( i );
			suma2 += Math.pow(a.get( i ), 2);
			sumb2 += Math.pow(b.get( i ), 2);
			prod += a.get( i )*b.get(i);
		}
		if (a.size() > b.size()){
			corr = ((a.size() * prod - suma*sumb)/ (Math.sqrt( a.size() * suma2 - Math.pow( suma, 2 )) * Math.sqrt( a.size() * sumb2 - Math.pow( sumb, 2 ))));
		} else {
			corr = ((b.size() * prod - suma*sumb)/ (Math.sqrt( b.size() * suma2 - Math.pow( suma, 2 )) * Math.sqrt( b.size() * sumb2 - Math.pow( sumb, 2 ))));
		}
		return corr;
	}

	// get the top x similar indices of elements between two Lists of doubles
	public List<Integer> manhattanTopicLevelDistance(List<Double> a, List<Double> b, int count){
		// return the number of count top different topics
		List<Integer> indexoftopSimilarTopics = new ArrayList<Integer>();
		int index = -1;
		double[] diferenceArray = new double[a.size() >= b.size() ? a.size() : b.size()];
		if ( count <= a.size() && count <= b.size() )
		{
			for ( int i = 0; i < ( ( a.size() >= b.size() ) ? a.size() : b.size() ); i++ )
			{
				diferenceArray[i] = Math.abs( a.get( i ) - b.get( i ) );
			}
		}
		// get the indices of top count elements
		while ( count > 0 )
		{
			double max = diferenceArray[0];
			for ( int i = 0; i < diferenceArray.length; i++ )
			{
				// find the maximum in array
				if ( diferenceArray[i] >= max )
				{
					max = diferenceArray[i];
					index = i;
				}
			}
			indexoftopSimilarTopics.add( index );
			diferenceArray[index] = -1;
			count--;
		}
		return indexoftopSimilarTopics;
	}

	// get the simple difference elements between two Lists of doubles
	public List<Double> manhattanDistanceList( List<Double> a, List<Double> b )
	{
		List<Double> diferenceArray = new ArrayList<Double>( a.size() >= b.size() ? a.size() : b.size() );

		for ( int i = 0; i < ( ( a.size() >= b.size() ) ? a.size() : b.size() ); i++ )
		{
			diferenceArray.add( Math.abs( a.get( i ) - b.get( i ) ) );
		}
		return diferenceArray;
	}

	// get the simple difference elements between two Lists of doubles
	public double[] manhattanDistance( List<Double> a, List<Double> b )
	{
		// return the number of count top different topics
		List<Integer> indexoftopSimilarTopics = new ArrayList<Integer>();
		int index = -1;
		double[] diferenceArray = new double[a.size() >= b.size() ? a.size() : b.size()];

		for ( int i = 0; i < ( ( a.size() >= b.size() ) ? a.size() : b.size() ); i++ )
		{
			diferenceArray[i] = Math.abs( a.get( i ) - b.get( i ) );
		}
		return diferenceArray;
	}

	// get the Jensen-Shannon divergence array version
	// https://en.wikipedia.org/wiki/Jensen%E2%80%93Shannon_divergence
	public double divergenceJennsenShannon( double[] a, double[] b )
	{
		double divergence = 0.0;
		divergence = Maths.jensenShannonDivergence( a, b );
		return divergence;
	}

	// get the Jensen-Shannon divergence list version
	// public double divergenceJennsenShannon( List<Double> a, List<Double> b )
	// {
	// double divergence = 0.0;
	//
	// divergence = Maths.jensenShannonDivergence( a.toArray( new Double[0] ),
	// b.toArray( new Double[0] ) );
	// return divergence;
	// }
}
