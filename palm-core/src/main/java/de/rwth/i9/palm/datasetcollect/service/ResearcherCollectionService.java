package de.rwth.i9.palm.datasetcollect.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.http.ParseException;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.rwth.i9.palm.helper.NameNormalizationHelper;
import de.rwth.i9.palm.model.Author;
import de.rwth.i9.palm.model.AuthorAlias;
import de.rwth.i9.palm.model.AuthorSource;
import de.rwth.i9.palm.model.Institution;
import de.rwth.i9.palm.model.Source;
import de.rwth.i9.palm.model.SourceType;
import de.rwth.i9.palm.persistence.PersistenceStrategy;
import de.rwth.i9.palm.service.ApplicationService;

@Service
public class ResearcherCollectionService
{
	private final static Logger log = LoggerFactory.getLogger( ResearcherCollectionService.class );

	@Autowired
	private AsynchronousAuthorCollectionService asynchronousAuthorCollectionService;

	@Autowired
	private PersistenceStrategy persistenceStrategy;

	@Autowired
	private ApplicationService applicationService;

	@Autowired
	private MendeleyOauth2Helper mendeleyOauth2Helper;

	/**
	 * Gather researcher from academic networks
	 * 
	 * @param query
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws OAuthProblemException
	 * @throws OAuthSystemException
	 * @throws ParseException
	 */
	@Transactional
	public List<Author> collectAuthorInformationFromNetwork( String query, boolean stored ) throws IOException, InterruptedException, ExecutionException, ParseException, OAuthSystemException, OAuthProblemException
	{
		// authors container
		List<Author> authors = new ArrayList<Author>();

		// container
		List<Future<List<Map<String, String>>>> authorFutureLists = new ArrayList<Future<List<Map<String, String>>>>();

		// getSourceMap
		Map<String, Source> sourceMap = applicationService.getAcademicNetworkSources();

		// get from configuration
		boolean isUseGoogleScholar = true;
		boolean isUseCiteseerX = true;
		boolean isUseDblp = true;
		boolean isUseMendeley = true;

		// alter value from configuration
		String useGoogleScholar = applicationService.getConfigValue( "researcher", "source", "google scholar" );
		if ( useGoogleScholar != null && useGoogleScholar.equals( "no" ) )
			isUseGoogleScholar = false;
		String useCiteseerX = applicationService.getConfigValue( "researcher", "source", "citeserx" );
		if ( useCiteseerX != null && useCiteseerX.equals( "no" ) )
			isUseCiteseerX = false;
		String useDblp = applicationService.getConfigValue( "researcher", "source", "dblp" );
		if ( useDblp != null && useDblp.equals( "no" ) )
			isUseDblp = false;
		String useMendeley = applicationService.getConfigValue( "researcher", "source", "mendeley" );
		if ( useMendeley != null && useMendeley.equals( "no" ) )
			isUseMendeley = false;

		// loop through all source which is active
		for ( Map.Entry<String, Source> sourceEntry : sourceMap.entrySet() )
		{
			Source source = sourceEntry.getValue();
			if ( source.getSourceType().equals( SourceType.GOOGLESCHOLAR ) && source.isActive() && isUseGoogleScholar )
				authorFutureLists.add( asynchronousAuthorCollectionService.getListOfAuthorsGoogleScholar( query, source ) );
			else if ( source.getSourceType().equals( SourceType.CITESEERX ) && source.isActive() && isUseCiteseerX )
				authorFutureLists.add( asynchronousAuthorCollectionService.getListOfAuthorsCiteseerX( query, source ) );
			else if ( source.getSourceType().equals( SourceType.DBLP ) && source.isActive() && isUseDblp )
				authorFutureLists.add( asynchronousAuthorCollectionService.getListOfAuthorsDblp( query, source ) );
			else if ( source.getSourceType().equals( SourceType.MENDELEY ) && source.isActive() && isUseMendeley )
			{
				// check for token validity
				mendeleyOauth2Helper.checkAndUpdateMendeleyToken( source );
				authorFutureLists.add( asynchronousAuthorCollectionService.getListOfAuthorsMendeley( query, source ) );
			}
		}

		// merge the result
		this.mergeAuthorInformation( authorFutureLists, authors, stored, sourceMap );

		return authors;
	}

	/**
	 * Merging author information from multiple resources, check whether author
	 * is already exist on database if exist then just merge if there is missing
	 * information, if not exist create new, merge information and persist.
	 * 
	 * @param authorFutureLists
	 * @param authors2
	 * @param stored
	 * @param sourceMap
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	@Transactional
	private void mergeAuthorInformation( List<Future<List<Map<String, String>>>> authorFutureLists, List<Author> authors2, boolean stored, Map<String, Source> sourceMap ) throws InterruptedException, ExecutionException
	{
		if ( authorFutureLists.size() > 0 )
		{
			// get number of active sources
			int numberOfActiveSources = 0;
			for( Map.Entry<String, Source> sourceEntry : sourceMap.entrySet() ){
				if( sourceEntry.getValue().isActive())
					numberOfActiveSources++;
			}
			
			// Merge gathered author information
			List<Map<String, String>> mergedAuthorList = new ArrayList<Map<String, String>>();
			Map<String, Integer> indexHelper = new HashMap<String, Integer>();
			for ( Future<List<Map<String, String>>> authorFutureList : authorFutureLists )
			{
				List<Map<String, String>> authorListMap = authorFutureList.get();
				for ( Map<String, String> authorMap : authorListMap )
				{
					if ( authorMap.get( "name" ) == null )
						continue;

					String authorName = NameNormalizationHelper.normalizeName( authorMap.get( "name" ).toLowerCase().replace( ".", "" ).trim() );

					// check if author already on array list
					Integer authorIndex = indexHelper.get( authorName );
					if ( authorIndex == null )
					{
						// if not exist on map
						mergedAuthorList.add( authorMap );
						indexHelper.put( authorName, mergedAuthorList.size() - 1 );
					}
					else
					{
						Map<String, String> mapFromMergerList = mergedAuthorList.get( authorIndex );
						for ( Map.Entry<String, String> entry : authorMap.entrySet() )
						{// merge everything else
							if ( mapFromMergerList.get( entry.getKey() ) == null )
							{
								mapFromMergerList.put( entry.getKey(), entry.getValue() );
							}
							else
							{
								if ( entry.getKey().equals( "source" ) || entry.getKey().equals( "url" ) )
									mapFromMergerList.put( entry.getKey(), mapFromMergerList.get( entry.getKey() ) + " " + entry.getValue() );
							}
						}
					}
				}
			}

			// remove author if its only from mendeley
			// since mendeley also put non researcher on its api result
			// the source URL of mendeley will be "MENDELEY"
			// which are less then 10 character in length
			if ( numberOfActiveSources > 1 )
			{
				for ( Iterator<Map<String, String>> iteratorAuthor = mergedAuthorList.iterator(); iteratorAuthor.hasNext(); )
				{
					Map<String, String> authorMap = iteratorAuthor.next();
	
					if ( authorMap.get( "source" ).equals( "MENDELEY" ) )
						iteratorAuthor.remove();
				}
			}

			// merger data
			for ( Map<String, String> mergedAuthor : mergedAuthorList )
			{
				String name = mergedAuthor.get( "name" ).toLowerCase().replace( ".", "" ).trim();
				String institution = "";
				String academicStatus = "";
				String otherDetail = "";
				String photo = mergedAuthor.get( "photo" );
				String citedby = mergedAuthor.get( "citedBy" );
				String discipline = mergedAuthor.get( "discipline" );
				String hindex = mergedAuthor.get( "hindex" );
				String aliases = mergedAuthor.get( "aliases" );
				String source = mergedAuthor.get( "source" );
				String url = mergedAuthor.get( "url" );

				String affliliation = mergedAuthor.get( "affiliation" );
				// looking for university
				if ( affliliation != null )
				{
					String[] authorDetails = affliliation.split( "," );
					for ( int i = 0; i < authorDetails.length; i++ )
					{
						if ( authorDetails[i].contains( "rof" ) || authorDetails[i].contains( "esearch" ) || authorDetails[i].contains( "octor" ) )
							academicStatus = authorDetails[i].trim().toLowerCase();
						// from word U"nivers"ity, institut, collage, state, school, technology, faculdade, education, hochschule, rieure						
						else if ( authorDetails[i].contains( "nivers" ) || authorDetails[i].contains( "nstit" ) ||
							 authorDetails[i].contains( "ollag" ) || authorDetails[i].contains( "tate" ) ||
							 authorDetails[i].contains( "echn" ) || authorDetails[i].contains( "choo" ) ||
							 authorDetails[i].contains( "acul" ) || authorDetails[i].contains( "ochs" ) ||
							 authorDetails[i].contains( "duca" ) || authorDetails[i].contains( "ieur" ))
							institution = authorDetails[i].trim();
						else
						{
							if ( authorDetails[i].length() > 16 )
							{
								if ( !otherDetail.equals( "" ) )
									otherDetail += ", ";
								otherDetail += authorDetails[i];
							}
						}
					}
				}

				// check source academic status from mendeley
				if ( academicStatus.equals( "" ) && mergedAuthor.get( "academicStatus" ) != null )
					academicStatus = mergedAuthor.get( "academicStatus" );

				List<Author> authors = persistenceStrategy.getAuthorDAO().getByName( name );
				Author author = null;

				if ( authors.isEmpty() )
				{
					author = new Author();
					author.setName( name );

					String[] splitName = name.split( " " );
					String lastName = splitName[splitName.length - 1];
					author.setLastName( lastName );
					String firstName = name.substring( 0, name.length() - lastName.length() ).replace( ".", "" ).trim();
					if ( !firstName.equals( "" ) )
						author.setFirstName( firstName );
				}
				else
				{
					author = authors.get( 0 );
				}

				// set academic status and affiliation
				if ( !institution.equals( "" ) && ( author.getInstitution() == null ) )
				{
					String institutionName = institution.toLowerCase().replace( "university", "" ).replace( "college", "" ).replace( "state", "" ).replace( "institute", "" ).replace( "school", "" ).replace( "academy", "" );
					Institution institutionObject = null;
					// find institution on database
					List<Institution> institutionObjects = persistenceStrategy.getInstitutionDAO().getWithFullTextSearch( institutionName );
					if ( !institutionObjects.isEmpty() )
					{
						// get the first one which is more likely correct
						institutionObject = institutionObjects.get( 0 );
					}
					else
					{
						institutionObject = new Institution();
						institutionObject.setName( institution );
						institutionObject.setURI( institution.replace( " ", "-" ) );
					}

					author.setInstitution( institutionObject );
				}

				if ( !academicStatus.equals( "" ) && author.getAcademicStatus() == null )
				{
					author.setAcademicStatus( academicStatus );
				}

				// author alias if exist
				if ( aliases != null )
				{
					// remove '[...]' sign at start and end.
					aliases = aliases.substring( 1, aliases.length() - 1 );
					for ( String authorAliasString : aliases.split( "," ) )
					{
						authorAliasString = authorAliasString.toLowerCase().replace( ".", "" ).trim();
						if ( !name.equals( authorAliasString ) )
						{
							AuthorAlias authorAlias = new AuthorAlias();
							authorAlias.setCompleteName( authorAliasString );
							authorAlias.setAuthor( author );
							author.addAlias( authorAlias );
						}
					}
				}

				if ( photo != null )
					author.setPhotoUrl( photo );

				if ( !otherDetail.equals( "" ) )
					author.setOtherDetail( otherDetail );
				// Number of citations
				if ( citedby != null )
					author.setCitedBy( Integer.parseInt( citedby ) );

				// TODO, specific to mendeley add adacemic_status and
				// discipline/field

				if ( discipline != null )
					author.setAcademicStatus( discipline );

				// H-index
				if ( hindex != null )
					author.setHindex( Integer.parseInt( hindex ) );

				// insert source
				Set<AuthorSource> authorSources = new LinkedHashSet<AuthorSource>();

				if ( source != null && url != null )
				{
					String[] sources = source.split( " " );
					String[] sourceUrls = url.split( " " );
					// checking for duplication
					Set<String> registeredSourceUlr = new HashSet<String>();
					//log.info( "\nRESEARCHER COLLECTION SERVICE" );
					for ( int i = 0; i < sources.length; i++ )
					{
						// prevent empty string and duplicated source
						if ( !sources[i].equals( "" ) && !registeredSourceUlr.contains( sourceUrls[i] ) )
						{
							AuthorSource as = new AuthorSource();
							as.setName( author.getName() );
							as.setSourceUrl( sourceUrls[i] );
							as.setSourceType( SourceType.valueOf( sources[i].toUpperCase() ) );
							as.setAuthor( author );

							registeredSourceUlr.add( sourceUrls[i] );

							authorSources.add( as );
							// author sources
							//log.info( author.getId() + "-" + author.getName() + " - " + as.getSourceType() + " -> " + as.getSourceUrl() );

							// add author sources
							author.addAuthorSource( as );
						}
					}
				}

				// in case of duplication
				if ( !authors2.contains( author ) )
					authors2.add( author );

				// if stored, then save author to database
				if ( stored )
					persistenceStrategy.getAuthorDAO().persist( author );
			}

		}

	}

}
