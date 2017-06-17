package de.rwth.i9.palm.feature.researcher;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

//import de.rwth.i9.palm.analytics.algorithm.dynamicLDA.ParallelTopicModel;
import de.rwth.i9.palm.analytics.api.PalmAnalytics;

public class ResearcherTopicModelingLDAImpl implements ResearcherTopicModelingLDA 
{	
	@Autowired 
	private PalmAnalytics palmAnalytics;
	
	
	public Map<String, Object> researcherTopicDistribution(){
		Map<String, Object> topicdist = new HashMap<String, Object>();
		//List<Object> topics = palmAnalytics.getDynamicTopicModel().getListTopics( palmAnalytics.getDynamicTopicModel().createModel( "C:\\Users\\Albi\\Desktop\\Years\\", "Years", 11, 10 ), 10 );
		
		return topicdist;
	}
	
	
	

	
	
	
	//LinkedHashMap<String, List<Double>> distribution = palmAnalytics.getDynamicTopicModel().getTopicDistributionforDocuments( ptm, 0.0, 10, ptm.numTopics );
}
