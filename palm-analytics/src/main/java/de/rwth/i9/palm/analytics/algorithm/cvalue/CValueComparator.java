package de.rwth.i9.palm.analytics.algorithm.cvalue;

import java.util.Comparator;

/**
 * Sort CValue calculation from larget to smaller
 *
 */
public class CValueComparator implements Comparator<TermCandidate>
{

	@Override
	public int compare( TermCandidate o1, TermCandidate o2 )
	{
		double c1 = o1.getCValue();
		double c2 = o2.getCValue();
		if ( c1 > c2 )
			return -1;
		else if ( c2 == c1 )
			return 0;
		else
			return 1;
	}

}