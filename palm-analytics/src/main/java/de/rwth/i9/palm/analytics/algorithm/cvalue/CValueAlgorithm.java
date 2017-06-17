package de.rwth.i9.palm.analytics.algorithm.cvalue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of the CValue term recognition algorithm. See Frantzi et.
 * al 2000, <i> Automatic recognition of multi-word terms:. the C-value/NC-value
 * method</i>
 * 
 */
public class CValueAlgorithm implements CValue
{
	// logger
	static private final Logger logger = LoggerFactory.getLogger( CValueAlgorithm.class );

	// minimum frequencies of candidate term
	private int frequencyThreshold = 1;

	// list of terms
	private List<String> terms;

	// the map of term candidate
	private HashMap<String, TermCandidate> candidatesMap;

	// constructor
	public CValueAlgorithm()
	{
		candidatesMap = new HashMap<String, TermCandidate>();
	}

	// getter setter
	public int getFrequencyThreshold()
	{
		return frequencyThreshold;
	}

	/**
	 * Set the frequency threshold of candidate terms. Any terms that has
	 * frequency below this threshold will be ignored
	 * 
	 * @param frequencyThreshold
	 *            an integer value of threshold
	 */
	public void setFrequencyThreshold( final int frequencyThreshold )
	{
		if ( frequencyThreshold < 0 )
			logger.info( "Frequency threshold must be larger than 0" );
		else
			this.frequencyThreshold = frequencyThreshold;
	}

	public List<String> getTerms()
	{
		return terms;
	}

	public void setTerms( List<String> terms )
	{
		// reset candidate maps
		this.candidatesMap = new HashMap<String, TermCandidate>();
		this.terms = terms;
	}

	public CValueAlgorithm addTerms( final String term )
	{
		if ( this.terms == null )
			this.terms = new ArrayList<String>();
		this.terms.add( term );
		return this;
	}

	// build the termCandidate
	private void buildTermCandidate( String term )
	{
		TermCandidate candidate;
		if ( ( candidate = candidatesMap.get( term ) ) == null )
		{
			candidate = new TermCandidate( term );
			candidatesMap.put( term, candidate );
		}
		candidate.increaseCandidateFrequencies();
	}

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
	public List<TermCandidate> getTermCandidates()
	{
		List<TermCandidate> candidates = new ArrayList<TermCandidate>( candidatesMap.values() );
		Collections.sort( candidates, new CValueComparator() );
		return candidates;
	}

	/**
	 * Run the actual calculation process. </br> including building the
	 * multi-word term HashMap and assign each candidate term with its
	 * frequencies, nested frequencies and number of nester candidate
	 */
	public void calculateCValue()
	{
		logger.info( "build the multi-word term candidates" );
		// first build the term candidates
		for ( String term : this.terms )
			buildTermCandidate( term );

		logger.info( "check for frequency threshold, eliminate candidates if candidates frequencies < frequency threshold" );
		// check frequency threshold
		if ( this.frequencyThreshold > 1 )
			for ( Iterator<Map.Entry<String, TermCandidate>> candidatesMapEntry = candidatesMap.entrySet().iterator(); candidatesMapEntry.hasNext(); )
			{
				Map.Entry<String, TermCandidate> entry = candidatesMapEntry.next();
				if ( entry.getValue().getCandidateFrequencies() < frequencyThreshold )
				{
					candidatesMapEntry.remove();
				}
			}

		logger.info( "calculating the cValue" );

		List<TermCandidate> candidates = new ArrayList<TermCandidate>( candidatesMap.values() );
		// sort based on term length and frequencies
		Collections.sort( candidates, new CValueTermLengthComparator() );

		// prepare the nested candidate
		TermCandidate nestedCandidate = null;

		// loop the ordered candidats
		for ( TermCandidate candidate : candidates )
		{
			// check nested candidate by checking the current candidate
			// substring with candidates hashmap
			for ( String candidateSubString : candidate.getTermCandidateSubString() )
			{
				// check whether any other candidate nested into current
				// candidate
				if ( ( nestedCandidate = candidatesMap.get( candidateSubString ) ) != null )
				{
					// if nested, do two things
					// increase the number of unique nester by one on nester
					// candidate
					nestedCandidate.increaseNumberOfUniqueNesterCandidates();
					// increase the nested candidate frequency by longer/current
					// candidate
					nestedCandidate.increaseCandidateNestedFrequencies( candidate.getCandidateFrequencies() );

				}
			}
		}

		logger.info( "The calculation process is done!" );
	}
}
