package de.rwth.i9.palm.analytics.algorithm.cvalue;

import java.util.List;

/**
 * An implementation of the CValue term recognition algorithm. See Frantzi et.
 * al 2000, <i> Automatic recognition of multi-word terms:. the C-value/NC-value
 * method</i>
 * 
 */

public interface CValue
{
	/**
	 * Set the frequency threshold of candidate terms. Any terms that has
	 * frequency below this threshold will be ignored
	 * 
	 * @param frequencyThreshold
	 *            an integer value of threshold
	 */
	public void setFrequencyThreshold( int frequencyThreshold );

	/**
	 * Add a term into list of term phrases
	 * Terms are extracted using <i>part of speech Penn Treebank</i>
	 * and usually in these following form:
	 * 	<i>1. Noun + Noun,</i></br>
	 *	<i>2.(Adj|Noun) + Noun,</i></br>
	 *	<i>3.((Adj|Noun) +|((Adj|Noun)* (NounPrep)?)(Adj|Noun)*)Noun</i></br>
	 * 
	 * @param term
	 *            the term phrase
	 * @return the CValueAlgorithm class itself
	 */
	public CValueAlgorithm addTerms( final String term );
	
	/**
	 * set list of term phrases
	 * Terms are extracted using <i>part of speech Penn Treebank</i>
	 * and usually in these following form:
	 * <i>1. Noun + Noun,</i></br> <i>2.(Adj|Noun) + Noun,</i></br>
	 * <i>3.((Adj|Noun) +|((Adj|Noun)* (NounPrep)?)(Adj|Noun)*)Noun</i></br>
	 * 
	 * @param terms
	 *            Linkedlist of multi-word terms
	 */
	public void setTerms( List<String> terms );
	
	/**
	 * Run the actual calculation process. </br> including building the
	 * multi-word term HashMap and assign each candidate term with its
	 * frequencies, nested frequencies and number of nester candidate
	 */
	public void calculateCValue();
	
	/**
	 * Get the list of constructed multi-word terms candidates
	 * Call the following methods first to avoid calling empty list.
	 * <i> set the list of terms</i></br>
	 * <pre>setTerms( terms );</pre></br>
	 * <i> set the frequency threshold (optional, to boost precision)</i></br>
	 * <pre>setFrequencyThreshold( integer );</pre></br>
	 * <i> build candidate list and calculate the cValue</i></br>
	 * <pre>calculateCValue();</pre></br>
	 * @return List<TermCandidate>
	 * 		list of term candidate based on input multi-word terms
	 */
	public List<TermCandidate> getTermCandidates();
}
