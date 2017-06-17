package de.rwth.i9.palm.feature.researcher;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.rwth.i9.palm.helper.comparator.AuthorInterestProfileByProfileNameLengthComparator;
import de.rwth.i9.palm.helper.comparator.CoAuthorByNumberOfCollaborationsAndNameComparator;
import de.rwth.i9.palm.helper.comparator.PublicationMapByDateComparator;
import de.rwth.i9.palm.model.Author;
import de.rwth.i9.palm.model.AuthorInterest;
import de.rwth.i9.palm.model.AuthorInterestProfile;
import de.rwth.i9.palm.model.Institution;
import de.rwth.i9.palm.model.Interest;
import de.rwth.i9.palm.model.Publication;
import de.rwth.i9.palm.model.PublicationTopic;
import de.rwth.i9.palm.persistence.PersistenceStrategy;

@Component
public class ResearcherCoauthorImpl implements ResearcherCoauthor
{
	@Autowired
	private PersistenceStrategy persistenceStrategy;

	@Override
	public Map<String, Object> getResearcherCoAuthorMap( Author author, int startPage, int maxCoauthorsResult, int maxauthorInterestResult )
	{
		// researchers list container
		Map<String, Object> responseMap = new LinkedHashMap<String, Object>();

		if ( author.getPublications() == null || author.getPublications().isEmpty() )
		{
			responseMap.put( "count", 0 );
			return responseMap;
		}
		//prepare a list of map object containing coauthor common publications
		Map<String, List<Map<String, Object>>> coAuthorCollaborationPublicationMap = new HashMap<String, List<Map<String, Object>>>();
		
		// prepare a list of map object containing coauthor properties and
		Map<String, Integer> coAuthorCollaborationCountMap = new HashMap<String, Integer>();		

		// Prepare set of coauthor HashSet;
		Set<Author> coauthorSet = new HashSet<Author>();

		for ( Publication publication : author.getPublications() )
		{
			for ( Author coAuthor : publication.getAuthors() )
			{
				// just skip if its himself
				if ( coAuthor.equals( author ) )
					continue;

				coauthorSet.add( coAuthor );

				// nr collaborations
				if ( coAuthorCollaborationCountMap.get( coAuthor.getId() ) == null )
				{
					coAuthorCollaborationCountMap.put( coAuthor.getId(), 1 );
				}
				else
					coAuthorCollaborationCountMap.put( coAuthor.getId(), coAuthorCollaborationCountMap.get( coAuthor.getId() ) + 1 );
			
				// common publications
				List<Map<String, Object>> publications = coAuthorCollaborationPublicationMap.get( coAuthor.getId() );

				if ( publications == null )
					publications = new ArrayList<Map<String, Object>>();

				publications.add( this.getPublicationDetails( publication ) );
				coAuthorCollaborationPublicationMap.put( coAuthor.getId(), publications );
			}
		}

		// prepare list of object map containing coAuthor details
		List<Map<String, Object>> coAuthorList = new ArrayList<Map<String, Object>>();

		for ( Author coAuthor : coauthorSet )
		{
			// only copy necessary attributes
			Map<String, Object> coAuthorMap = new LinkedHashMap<String, Object>();
			coAuthorMap.put( "id", coAuthor.getId() );
			coAuthorMap.put( "name", coAuthor.getName() );
			coAuthorMap.put( "hindex", coAuthor.getHindex() );
			if ( coAuthor.getInstitution() != null ){
				Map<String, String> affiliationData = new HashMap<String, String>();
				
				affiliationData.put("institution", coAuthor.getInstitution().getName());
				
				if (coAuthor.getInstitution().getLocation() != null){
					affiliationData.put("country", coAuthor.getInstitution().getLocation().getCountry().getName());
					affiliationData.put( "url", coAuthor.getInstitution().getUrl() );
				}
				else
				{
					String institution_name = coAuthor.getInstitution().getName().replaceAll( " (?i)university", "" );
					institution_name = institution_name.replaceAll( "(?i)university ", "" );

					List<Institution> institutions = persistenceStrategy.getInstitutionDAO().getByName( institution_name );

					for ( int i = 0; i < institutions.size(); i++ )
						if ( institutions.get( i ).getLocation() != null )
							if ( institutions.get( i ).getLocation().getCountry().getName() != null )
							{
								affiliationData.put( "country", institutions.get( i ).getLocation().getCountry().getName() );
								affiliationData.put( "url", institutions.get( i ).getUrl() );
								break;
							}
				}
							
				coAuthorMap.put( "aff", affiliationData );
			}
			
			if( coAuthor.getPhotoUrl() != null )
				coAuthorMap.put( "photo", coAuthor.getPhotoUrl() );
			coAuthorMap.put( "isAdded", coAuthor.isAdded() );
			coAuthorMap.put( "status", coAuthor.getAcademicStatus() );
			coAuthorMap.put( "coauthorTimes", coAuthorCollaborationCountMap.get( coAuthor.getId() ) );
			coAuthorMap.put( "commonInterests", this.getCommonInterests( author, coAuthor ) );

			if ( !coAuthorCollaborationPublicationMap.get( coAuthor.getId() ).isEmpty() )
			{
				Collections.sort( coAuthorCollaborationPublicationMap.get( coAuthor.getId() ), new PublicationMapByDateComparator() );
				coAuthorMap.put( "commonPublications", coAuthorCollaborationPublicationMap.get( coAuthor.getId() ) );
			}

			// add into list
			coAuthorList.add( coAuthorMap );
		}
		
		Collections.sort( coAuthorList, new CoAuthorByNumberOfCollaborationsAndNameComparator() );

		// prepare list of object map containing coAuthor details
		List<Map<String, Object>> coAuthorListPaging = new ArrayList<Map<String, Object>>();

		int position = 0;
		for ( Map<String, Object> coAuthor : coAuthorList )
		{
			if ( position >= startPage && coAuthorListPaging.size() < maxauthorInterestResult )
			{
				coAuthorListPaging.add( coAuthor );
			}
		}
		// put coauthor to responseMap
		responseMap.put( "countTotal", coAuthorList.size() );
		responseMap.put( "count", coAuthorListPaging.size() );

		if ( maxCoauthorsResult < coAuthorList.size() )
			responseMap.put( "coAuthors", coAuthorList.subList( 0, maxCoauthorsResult ) );
		else
			responseMap.put( "coAuthors", coAuthorList );

		return responseMap;
	}

	public List<Map<String, Object>> getCommonInterests(Author author, Author coAuthor){
		List<Map<String, Object>> interestsListAuthor   = getInterests( author ) ;
		List<Map<String, Object>> interestsListCoauthor = getInterests( coAuthor );		
		
		return intersectCoauthorInterests(interestsListAuthor, interestsListCoauthor);
	}
	
	public List< Map <String, Object> > getInterests(Author author){
		List<AuthorInterestProfile> authorInterestProfiles = new ArrayList<AuthorInterestProfile>();
		authorInterestProfiles.addAll( author.getAuthorInterestProfiles() );
		// sort based on profile length ( currently there is no attribute to
		// store position)
		Collections.sort( authorInterestProfiles, new AuthorInterestProfileByProfileNameLengthComparator() );

		// the whole authorInterestResult related to interest
		List<Map<String, Object>> authorInterestResult = new ArrayList<Map<String, Object>>();
		
		for ( AuthorInterestProfile authorInterestProfile : authorInterestProfiles )
		{
			// get authorInterest set on profile
			Set<AuthorInterest> authorInterests = authorInterestProfile.getAuthorInterests();

			// if profile contain no authorInterest just skip
			if ( authorInterests == null || authorInterests.isEmpty() )
				continue;

			for ( AuthorInterest authorInterest : authorInterests ){
				for ( Map.Entry<Interest, Double> termWeightMap : authorInterest.getTermWeights().entrySet() )
				{
					// just remove not significant value
					if ( termWeightMap.getValue() < 0.4 )
						continue;
						
					Map<String, Object> termWeightObject = new HashMap<String, Object>();
					termWeightObject.put( "id", termWeightMap.getKey().getId() );
					termWeightObject.put( "term", termWeightMap.getKey().getTerm() );
					termWeightObject.put( "value", termWeightMap.getValue() );

					if ( !listContains(termWeightObject, authorInterestResult) )
						authorInterestResult.add(termWeightObject);
				}
			}
		}
		return authorInterestResult;
	}
	
	public Map<String, Object> getPublicationDetails( Publication publication )
	{
		Map<String, Object> publicationDetails = new HashMap<String, Object>();
		if ( publication.getTitle() != null )
			publicationDetails.put( "title", publication.getTitle() );

		if ( publication.getPublicationDate() != null )
		{
			SimpleDateFormat sdf = new SimpleDateFormat( publication.getPublicationDateFormat() );
			publicationDetails.put( "date", sdf.format( publication.getPublicationDate() ) );
			publicationDetails.put( "dateFormat", publication.getPublicationDateFormat() );
		}

		publicationDetails.put( "id", publication.getId() );
		publicationDetails.put( "type", publication.getPublicationType() );
		publicationDetails.put( "abstract", publication.getAbstractText() );
		publicationDetails.put( "cited", publication.getCitedBy() );

		if ( publication.getAbstractText() != null || publication.getKeywordText() != null )
			publicationDetails.put( "contentExist", true );
		else
			publicationDetails.put( "contentExist", false );

		// publication coauthors
		List<Map<String, Object>> coauthors = new ArrayList<Map<String, Object>>();
		if ( publication.getCoAuthors() != null )
			for ( Author coauthor : publication.getCoAuthors() )
			{
				Map<String, Object> coauthorMap = new HashMap<String, Object>();
				coauthorMap.put( "name", coauthor.getName() );
				coauthors.add( coauthorMap );
			}
		publicationDetails.put( "coauthor", coauthors );

		// publication topics
		List<Map<String, Object>> topics = new ArrayList<Map<String, Object>>();
		if ( publication.getPublicationTopics() != null )
			for ( PublicationTopic publicationTopic : publication.getPublicationTopics() )
			{
				Map<String, Object> publicationTopicMap = new HashMap<String, Object>();
				publicationTopicMap.put( "termstring", publicationTopic.getTermString() );
				publicationTopicMap.put( "termvalues", publicationTopic.getTermValues() );
				topics.add( publicationTopicMap );
			}
		publicationDetails.put( "topics", topics );

		return publicationDetails;
	}

	private boolean listContains(Map<String, Object> element, List<Map<String, Object>> list){
		for ( Map<String, Object> elem : list){
			if (elem.containsValue(element.get("id")))
				return true;
		}
		return false;
	}
	public List<Map<String, Object>> intersectCoauthorInterests(List<Map<String, Object>> interestsListAuthor, List<Map<String, Object>> interestsListCoauthor){
		List<Map<String, Object>> commonInterest = new ArrayList<Map<String, Object>>();
			
		for (Map<String, Object> interestCoauthor : interestsListCoauthor){	
			String interestCoauthorID = interestCoauthor.get("id").toString();
			boolean found = false; int i = 0;

			while( found == false && i < interestsListAuthor.size()){
				if (interestsListAuthor.get(i).containsValue(interestCoauthorID))
					found = true;
				i++;
			}
			if (found){
				commonInterest.add(interestCoauthor);
			}		
		}
			
		return commonInterest;
	}
}
