package de.rwth.i9.palm.controller.topicmodeling;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

//import de.rwth.i9.palm.analytics.algorithm.dynamicLDA.ParallelTopicModel;
import de.rwth.i9.palm.analytics.api.PalmAnalytics;
import de.rwth.i9.palm.persistence.PersistenceStrategy;

@Controller
@RequestMapping( value = "/circle" )
public class TopicModelingCircleController
{
	@Autowired
	private PersistenceStrategy persistenceStrategy;

	@Autowired
	private PalmAnalytics palmAnalytics;

	@Transactional
	@RequestMapping( value = "/dynamictopic", method = RequestMethod.GET )
	public @ResponseBody String allReindex()
	{
//		ParallelTopicModel ptm = palmAnalytics.getDynamicTopicModel().createModel( "C:\\Users\\nifry\\Desktop\\Years\\", "Years", 11, 10 );
//
//		List<String> topics = palmAnalytics.getDynamicTopicModel().getListTopics( ptm, 10 );
//		LinkedHashMap<String, List<Double>> distribution = palmAnalytics.getDynamicTopicModel().getTopicDistributionforDocuments( ptm, 0.0, 10, ptm.numTopics );

		return "success";
	}
}
