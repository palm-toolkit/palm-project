package de.rwth.i9.palm.analytics.algorithm.cvalue;

import java.util.Comparator;

/**
 * 
 * Sort candidates based on their term length and frequencies
 *
 */
public class CValueTermLengthComparator implements Comparator<TermCandidate>
{

	@Override
	public int compare( TermCandidate o1, TermCandidate o2 )
	{
		if ( o1.getCandidateLength() > o2.getCandidateLength() )
			return -1;
		// on term with the same length compare with its occurrence in corpus(
		// frequency )
		else if ( o1.getCandidateLength() == o2.getCandidateLength() )
		{
			if ( o1.getCandidateFrequencies() > o2.getCandidateFrequencies() )
				return -1;
			else if ( o1.getCandidateFrequencies() == o2.getCandidateFrequencies() )
				return 0;
			else
				return 1;
		}
		else
			return 1;

	}

}