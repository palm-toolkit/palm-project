package de.rwth.i9.palm.interestmining.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CorePhraseAndWordFreqHelper
{
	public static Map<String, Double> calculateYearFactor( Map<String, PublicationClusterHelper> publicationClustersMap, Double weightingRange )
	{
		Map<String, Double> yearFactorMaps = new HashMap<String, Double>();

		// initial weight
		double initialWeight = 1.0;

		// == calculate year weighting

		// First, get construct Map based on language, with year list as the
		// value
		Map<String, List<Integer>> languageYearListMap = new HashMap<String, List<Integer>>();

		for ( Map.Entry<String, PublicationClusterHelper> publicationClusterEntry : publicationClustersMap.entrySet() )
		{
			PublicationClusterHelper publicationCluster = publicationClusterEntry.getValue();
			String language = publicationCluster.getLanguage();
			int year = publicationCluster.getYear();

			if ( languageYearListMap.get( language ) != null )
			{
				languageYearListMap.get( language ).add( year );
			}
			else
			{
				List<Integer> yearList = new ArrayList<Integer>();
				yearList.add( year );

				languageYearListMap.put( language, yearList );
			}
		}

		// Second, sort the year list on each language map and calculate the
		// weight
		for ( Map.Entry<String, List<Integer>> languageYearListEntry : languageYearListMap.entrySet() )
		{
			String language = languageYearListEntry.getKey();
			List<Integer> yearList = languageYearListEntry.getValue();
			// sort yearlist
			Collections.sort( yearList );

			// assign weighting value
			for ( int i = 0; i < yearList.size(); i++ )
			{
				double weighting = initialWeight + ( ( i + 1 ) / (double) yearList.size() ) * weightingRange;
				yearFactorMaps.put( language + yearList.get( i ), weighting );
			}

		}

		return yearFactorMaps;
	}

	public static Map<String, Double> calculateTotalYearsFactor( Map<String, PublicationClusterHelper> publicationClustersMap )
	{
		Map<String, Double> totalYearsFactorMaps = new HashMap<String, Double>();

		// initial weight
		// TODO consultation with supervisor
		double initialWeight = 1.0;

		for ( Map.Entry<String, PublicationClusterHelper> publicationClusterEntry : publicationClustersMap.entrySet() )
		{
			PublicationClusterHelper publicationCluster = publicationClusterEntry.getValue();
			String language = publicationCluster.getLanguage();

			if ( totalYearsFactorMaps.get( language ) == null )
			{
				totalYearsFactorMaps.put( language, initialWeight );
			}
		}

		return totalYearsFactorMaps;
	}

}
