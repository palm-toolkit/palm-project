package de.rwth.i9.palm.controller;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import de.rwth.i9.palm.analytics.algorithm.dynamicLDA.ParallelTopicModel;
import de.rwth.i9.palm.analytics.api.PalmAnalytics;
import de.rwth.i9.palm.analytics.util.TopicMiningConstants;
import de.rwth.i9.palm.persistence.PersistenceStrategy;

@Controller
@RequestMapping( value = "/topicmodeling" )
public class ManageTopicModelingController
{
	@Autowired
	private PersistenceStrategy persistenceStrategy;

	@Autowired
	private PalmAnalytics palmAnalytics;

	@Transactional
	@RequestMapping( method = RequestMethod.GET )
	public @ResponseBody String allReindex()									 
	{
		ParallelTopicModel ptm = palmAnalytics.getDynamicTopicModel().createModel( TopicMiningConstants.USER_YEARS_FOLDER_PATH, "Years", 11, 10 );

		List<String> topics = palmAnalytics.getDynamicTopicModel().getListTopics( 10 );
		
		LinkedHashMap<String, List<Double>> distribution = palmAnalytics.getDynamicTopicModel().getTopicDistributionforDocuments( ptm, 0.0, 10, ptm.numTopics );

		Map<String, Object> ldaObjectResults = new HashMap<>();
		ldaObjectResults.put( "algorithm", "lda" );
		ldaObjectResults.put( "termvalues", "lda" );
		return "success";
	}
}
