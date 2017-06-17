package de.rwth.i9.palm.feature.researcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.rwth.i9.palm.analytics.api.PalmAnalytics;
import de.rwth.i9.palm.analytics.util.TopicMiningConstants;
import de.rwth.i9.palm.model.Author;

@Component
public class ResearcherTopicEvolutionImplTest implements ResearcherTopicEvolutionTest
{
	@Autowired
	private PalmAnalytics palmAnalytics;
	private String path = TopicMiningConstants.USER_DESKTOP_PATH;
	
	@Override
	public Map<String, Object> getResearcherTopicEvolutionTest( Author author ) throws IOException
	{
		// researchers list container
		Map<String, Object> responseMap = new LinkedHashMap<String, Object>();

		HashMap<String, List<String>> topicevolution = new LinkedHashMap<String, List<String>>();

		// getEvolutionofTopicOverTime( 0, 5, false );
		topicevolution = (HashMap<String, List<String>>) palmAnalytics.getNGrams().runDiscreteTopicEvolution( path, "Author-Year", author.getId().toString(), 5, 10, 10, palmAnalytics.getNGrams().dateCheckCriteria(path, "Author-Year", author.getId().toString()), false, false );
		// Prepare set of similarAuthor HashSet;
		List<LinkedHashMap<String, Object>> topicList = new ArrayList<LinkedHashMap<String, Object>>();
		String[] colors = { "0efff8", "ff7f0e", "0eff7f", "ffa70e", "ff7f5a", "d4991c", "ad937c", "ff430e", "ff0e8e", "0e8eff" };
		int col = 0;
		for ( Entry<String, List<String>> topic : topicevolution.entrySet() )
		{
			LinkedHashMap<String, Object> evolutionMap = new LinkedHashMap<String, Object>();
			LinkedHashMap<String, String> yearproportion = new LinkedHashMap<String, String>();
			for ( String proportions : topic.getValue() )
				yearproportion.put( proportions.split( "_-_" )[0], proportions.split( "_-_" )[1] );
			evolutionMap.put( "values", yearproportion );
			evolutionMap.put( "key", topic.getKey() );
			evolutionMap.put( "color", colors[col] );
			if ( col < colors.length )
			{
				col++;
			}
			else
			{
				col = 0;
			}

			topicList.add( evolutionMap );
		}

		// prepare list of object map containing similarAuthor details
		List<Map<String, Object>> idk = new ArrayList<Map<String, Object>>();

		for ( Map<String, Object> individaltopics : topicList )
		{
			idk.add( individaltopics );
		}

		// put similarAuthor to responseMap
		responseMap.put( "termvalues", idk );

		return responseMap;
	}

}
