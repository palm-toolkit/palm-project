package de.rwth.i9.palm.analytics.algorithm.cvalue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TermCandidate
{
	// the term / phrase of candidate
	private String candidateTerm;

	// total frequency of this candidate in the corpus
	private int candidateFrequencies;

	// the length of this candidate
	private int candidateLength;

	// the frequency of this candidate nested on longer candidates
	private int candidateNestedFrequencies;

	// the number of longer candidate terms that contain this candidate
	private int uniqueNesterCandidates;

	// the number of substring combination of term candidate
	private List<String> candidateTermSubStrings;

	// the cvalue value
	private double cValue;

	// constructor set default value
	public TermCandidate( final String term )
	{
		this.candidateTerm = term;
		this.candidateLength = this.candidateTerm.split( " " ).length;
		this.candidateFrequencies = 0;
		this.candidateNestedFrequencies = 0;
		this.uniqueNesterCandidates = 0;
		this.cValue = 0.0;
	}

	// increase candidate frequency every time its found in terms list
	public void increaseCandidateFrequencies()
	{
		this.candidateFrequencies++;
	}

	// increase candidate nested frequency with longer candidate freq every time
	// this candidate found in longer candidate
	public void increaseCandidateNestedFrequencies( final int freq )
	{
		this.candidateNestedFrequencies += freq;
	}

	// increase number of uniq nester candidate candidate found in
	// longer candidate
	public void increaseNumberOfUniqueNesterCandidates()
	{
		this.uniqueNesterCandidates++;
	}

	// get and build the candidate substring
	public List<String> getTermCandidateSubString()
	{
		// candidate only contain a single term
		if ( this.candidateLength == 1 )
			return Collections.emptyList();

		String[] termSubString = this.candidateTerm.split( " " );
		int subTermLength = termSubString.length - 2;
		int frontIndex = 0;
		int rearIndex = frontIndex + subTermLength;
		String subTerm = "";
		do
		{
			// concate the subterm
			for ( int i = frontIndex; i <= rearIndex; i++ )
			{
				if ( i > frontIndex )
					subTerm += " ";
				subTerm += termSubString[i];
			}

			this.addCandidateSubStrings( subTerm );
			subTerm = "";
			// update the index
			frontIndex++;
			rearIndex++;
			// if rear reach the end of string reset indexes
			if ( rearIndex == termSubString.length )
			{
				subTermLength--;
				frontIndex = 0;
				rearIndex = frontIndex + subTermLength;
			}
		} while ( subTermLength > -1 && rearIndex < termSubString.length );

		return this.candidateTermSubStrings;
	}

	// get cvalue of candidate
	public double getCValue()
	{
		if( this.cValue != 0.0)
			return this.cValue;
		
		double log2CandidateLength = ( Math.log( (double) this.candidateLength ) / Math.log( (double) 2 ) );
		double candidateFrequenciesD = (double) this.candidateFrequencies;
		double inverseCandidateNestedFrequencies = 1.0 / (double) this.uniqueNesterCandidates;
		double candidateNestedFrequenciesD = (double) this.candidateNestedFrequencies;

		if ( this.uniqueNesterCandidates == 0 )
			this.cValue = log2CandidateLength * candidateFrequenciesD;
		else
			this.cValue = log2CandidateLength * ( candidateFrequenciesD - inverseCandidateNestedFrequencies * candidateNestedFrequenciesD );
		return this.cValue;
	}

	// getter and setter

	public String getCandidateTerm()
	{
		return candidateTerm;
	}

	public int getCandidateFrequencies()
	{
		return candidateFrequencies;
	}

	public int getCandidateLength()
	{
		return candidateLength;
	}

	public int getCandidateNestedFrequencies()
	{
		return candidateNestedFrequencies;
	}

	public int getUniqueNesterCandidates()
	{
		return uniqueNesterCandidates;
	}

	private TermCandidate addCandidateSubStrings( final String CandidateSubString )
	{
		if ( this.candidateTermSubStrings == null )
			this.candidateTermSubStrings = new ArrayList<String>();
		this.candidateTermSubStrings.add( CandidateSubString );

		return this;
	}


}
