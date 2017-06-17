package de.rwth.i9.palm.interestmining.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.rwth.i9.palm.analytics.algorithm.cvalue.CValue;
import de.rwth.i9.palm.analytics.algorithm.cvalue.TermCandidate;
import de.rwth.i9.palm.analytics.api.PalmAnalytics;
import de.rwth.i9.palm.interestmining.service.PublicationClusterHelper.TermDetail;
import de.rwth.i9.palm.model.CircleInterest;
import de.rwth.i9.palm.model.Interest;
import de.rwth.i9.palm.persistence.PersistenceStrategy;

@Service
public class CValueCircleInterestProfile
{
	private final static Logger log = LoggerFactory.getLogger( CValueCircleInterestProfile.class );

	@Autowired
	private PersistenceStrategy persistenceStrategy;

	@Autowired
	private PalmAnalytics palmAnalytics;

	public void doCValueCalculation( CircleInterest circleInterest, PublicationClusterHelper publicationCluster, int numberOfExtractionService )
	{
		// assign circleInterest properties
		circleInterest.setLanguage( publicationCluster.getLanguage() );

		DateFormat dateFormat = new SimpleDateFormat( "yyyy", Locale.ENGLISH );
		try
		{
			circleInterest.setYear( dateFormat.parse( Integer.toString( publicationCluster.getYear() ) ) );
		}
		catch ( ParseException e )
		{
			e.printStackTrace();
		}

		
		Map<String, TermDetail> termDetailsMap = publicationCluster.getTermMap();
		List<String> terms = new ArrayList<>();
		
		for ( Map.Entry<String, TermDetail> termDetailEntryMap : termDetailsMap.entrySet() )
		{
			String term = termDetailEntryMap.getKey();

			// just skip term which are too long
			if ( term.length() > 50 )
				continue;

			TermDetail termDetail = termDetailEntryMap.getValue();
			
			//only proceed for term that intersect with other topic extractor
			if ( ( termDetail.getExtractionServiceTypes().size() >= numberOfExtractionService - 1 ) || numberOfExtractionService == 1 )
			{
			
				for ( int i = 0; i < termDetail.getFrequencyOnTitle(); i++ )
				{
					// if there is weighting add here
					terms.add( term );
				}

				for ( int i = 0; i < termDetail.getFrequencyOnKeyword(); i++ )
				{
					// if there is weighting add here
					terms.add( term );
				}

				for ( int i = 0; i < termDetail.getFrequencyOnAbstract(); i++ )
				{
					terms.add( term );
				}
			}
		}
		
		// calculate c-value
		CValue cValue = palmAnalytics.getCValueAlgorithm();
		cValue.setTerms( terms );
		cValue.calculateCValue();
		
		List<TermCandidate> termCandidates = cValue.getTermCandidates();
		
		// ordered map as helper
		Map<String, Double> termWeightHelperMap = new HashMap<String, Double>();
		double maxWeightValue = 0.0;

		// put calculated term value to container
		for ( TermCandidate termCandidate : termCandidates )
		{
			if ( termCandidate.getCValue() >= 2 )
			{
				double cValueWeight = termCandidate.getCValue();
				termWeightHelperMap.put( termCandidate.getCandidateTerm(), cValueWeight );

				if ( cValueWeight > maxWeightValue )
					maxWeightValue = cValueWeight;
			}
		}

		// normalize value between 0 - 1
		for ( Map.Entry<String, Double> termWeightHelperEntry : termWeightHelperMap.entrySet() )
		{
			String term = termWeightHelperEntry.getKey();

			if ( maxWeightValue == 0 )
				continue;

			double normalizedWeighting = termWeightHelperEntry.getValue() / maxWeightValue;

			// proceed to interest object
			Interest interest = persistenceStrategy.getInterestDAO().getInterestByTerm( term );

			if ( interest == null )
			{
				interest = new Interest();
				interest.setTerm( term );
				persistenceStrategy.getInterestDAO().persist( interest );
			}
			circleInterest.addTermWeight( interest, normalizedWeighting );
		}
		
	}
}
