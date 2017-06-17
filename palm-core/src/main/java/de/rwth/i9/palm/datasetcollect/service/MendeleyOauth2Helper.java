package de.rwth.i9.palm.datasetcollect.service;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Map;

import org.apache.http.ParseException;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.rwth.i9.palm.helper.DateTimeHelper;
import de.rwth.i9.palm.model.Source;
import de.rwth.i9.palm.model.SourceProperty;
import de.rwth.i9.palm.oauth2.Oauth2Client;
import de.rwth.i9.palm.persistence.PersistenceStrategy;
import de.rwth.i9.palm.service.ApplicationService;

@Service
public class MendeleyOauth2Helper
{
	private final static Logger log = LoggerFactory.getLogger( MendeleyOauth2Helper.class );

	@Autowired
	private PersistenceStrategy persistenceStrategy;

	@Autowired
	private ApplicationService applicationService;

	public void checkAndUpdateMendeleyToken( Source source ) throws ParseException, IOException, OAuthSystemException, OAuthProblemException
	{
		// check or update token here
		SourceProperty mendeleyTokenProperty = source.getSourcePropertyByIdentifiers( "oauth2", "TOKEN" );

		String token = null;

		// get current timestamp
		java.util.Date date = new java.util.Date();

		if ( mendeleyTokenProperty == null )
		{
			//
			log.info( "requesting new token" );
			// update and persist token
			Map<String, String> oauth2PropertiesMap = source.getValidSourcePropertyListByMainIdentifierMap( "oauth2" );
			token = Oauth2Client.Oauth2ClientRequestToken( oauth2PropertiesMap.get( "TOKEN_URL" ), oauth2PropertiesMap.get( "TRUSTED_CLIENT_ID" ), oauth2PropertiesMap.get( "TRUSTED_SECRET" ) );

			// get current timestamp
			Timestamp currentTimestamp = new Timestamp( date.getTime() );

			SourceProperty newSourceProperty = new SourceProperty();
			newSourceProperty.setMainIdentifier( "oauth2" );
			newSourceProperty.setSecondaryIdentifier( "TOKEN" );
			newSourceProperty.setValue( token );
			newSourceProperty.setValid( true );
			newSourceProperty.setLastModified( currentTimestamp );
			newSourceProperty.setExpiredEvery( "10 MINUTE" );
			newSourceProperty.setSource( source );
			persistenceStrategy.getSourcePropertyDAO().persist( newSourceProperty );

			source.addSourceProperty( newSourceProperty );
			persistenceStrategy.getSourceDAO().persist( source );

			// update source map
			applicationService.updateAcademicNetworkSourcesCache();

		}
		else
		{
			// check if token has already expired > (10 MINUTE)
			//if ( mendeleyTokenProperty.getExpiredEvery().equals( "10 MINUTE" ) )
			{
				// get current timestamp
				Timestamp currentTimestamp = new Timestamp( date.getTime() );

				if ( DateTimeHelper.substractTimeStampToMinutes( currentTimestamp, mendeleyTokenProperty.getLastModified() ) > 10 )
				{
					// update token
					log.info( "updating token" );
					// update and persist token
					Map<String, String> oauth2PropertiesMap = source.getValidSourcePropertyListByMainIdentifierMap( "oauth2" );
					token = Oauth2Client.Oauth2ClientRequestToken( oauth2PropertiesMap.get( "TOKEN_URL" ), oauth2PropertiesMap.get( "TRUSTED_CLIENT_ID" ), oauth2PropertiesMap.get( "TRUSTED_SECRET" ) );

					mendeleyTokenProperty.setValue( token );
					mendeleyTokenProperty.setLastModified( currentTimestamp );
					persistenceStrategy.getSourcePropertyDAO().persist( mendeleyTokenProperty );

					// update source map
					applicationService.updateAcademicNetworkSourcesCache();
				}
			}
		}
	}
}
