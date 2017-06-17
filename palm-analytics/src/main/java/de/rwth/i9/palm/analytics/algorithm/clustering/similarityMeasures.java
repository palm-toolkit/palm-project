
package de.rwth.i9.palm.analytics.algorithm.clustering;

public class similarityMeasures
{
	double[] a;
	double[] b;
	
	public similarityMeasures(double[] a, double[] b){
		this.a = a;
		this.b = b;
	}
	
	// cos = a*b/|a|*|b| = sum(a*b)/sqrt(a^2)*sqrt(b^2)
	public double cosineSimilarity(double[] a, double[] b){
		double sum = 0.0, s1 = 0.0, s2 = 0.0;
		for (int i=0; i < ((a.length > b.length) ? a.length:b.length); i++){
			s1 += Math.pow( a[i],2 );
			s2 += Math.pow( b[i], 2 );
			sum += a[i] * b[i];
		}
		return sum/(Math.sqrt( s1 ) * Math.sqrt( s2 ));
	}
	
	// eucl = |a-b| = sqrt ( a-b ) ^2 
	public double sqrtEuclidianSimilarity(double[] a, double[] b){
		double sum = 0.0;
		for (int i=0; i < ((a.length>b.length) ? a.length : b.length);i++ ){
			sum += Math.pow( (a[i] - b[i]), 2 );
		}
		return Math.sqrt(sum);
	}
	
	// relEntropy = sum ( a * log (a/b) )
	public double relativeEntropySimilarity(double[] a, double[] b, int numTopics){
		double sum = 0.0;
		for (int i = 0; i < ((a.length > b.length) ? a.length : b.length); i++){
			sum += a[i] * Math.log( a[i]/b[i]);
		}
		return sum;
	}
}
