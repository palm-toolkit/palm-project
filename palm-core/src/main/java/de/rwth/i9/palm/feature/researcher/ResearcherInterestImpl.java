package de.rwth.i9.palm.feature.researcher;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.rwth.i9.palm.feature.AcademicFeatureImpl;
import de.rwth.i9.palm.interestmining.service.InterestMiningService;
import de.rwth.i9.palm.model.Author;
import de.rwth.i9.palm.persistence.PersistenceStrategy;
import de.rwth.i9.palm.topicextraction.service.TopicExtractionService;

@Component
public class ResearcherInterestImpl extends AcademicFeatureImpl implements ResearcherInterest
{
	@Autowired
	private PersistenceStrategy persistenceStrategy;

	@Autowired
	private TopicExtractionService topicExtractionService;
	
	@Autowired
	private InterestMiningService interestMiningService;

	@Override
	public Map<String, Object> getAuthorInterestById( String authorId, boolean isReplaceExistingResult ) throws InterruptedException, UnsupportedEncodingException, URISyntaxException, ParseException, ExecutionException
	{
		// create JSON mapper for response
		Map<String, Object> responseMap = new LinkedHashMap<String, Object>();

		if ( authorId == null )
		{
			responseMap.put( "status", "error" );
			responseMap.put( "statusMessage", "author id missing" );
			return responseMap;
		}

		// get the author
		Author author = persistenceStrategy.getAuthorDAO().getById( authorId );

		if ( author == null )
		{
			responseMap.put( "status", "error" );
			responseMap.put( "statusMessage", "author not found" );
			return responseMap;
		}
		// put some information
		responseMap.put( "status", "Ok" );

		Map<String, Object> targetAuthorMap = new LinkedHashMap<String, Object>();
		targetAuthorMap.put( "id", author.getId() );
		targetAuthorMap.put( "name", author.getName() );
		responseMap.put( "author", targetAuthorMap );

		// check whether publication has been extracted
		// later add extractionServiceType checking
		topicExtractionService.extractTopicFromPublicationByAuthor( author );
		
		// check if interest need to be recalculate
		if ( !isReplaceExistingResult && author.isUpdateInterest() )
		{
			isReplaceExistingResult = true;

			author.setUpdateInterest( false );
			persistenceStrategy.getAuthorDAO().persist( author );
		}
		// mining the author interest
		interestMiningService.getInterestFromAuthor( responseMap, author, isReplaceExistingResult );


		return responseMap;
	}
}
