package de.rwth.i9.palm.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import de.rwth.i9.palm.helper.ProcessLogHelper;
import de.rwth.i9.palm.model.Config;
import de.rwth.i9.palm.model.ExtractionService;
import de.rwth.i9.palm.model.InterestProfile;
import de.rwth.i9.palm.model.Source;
import de.rwth.i9.palm.persistence.PersistenceStrategy;
import freemarker.template.TemplateModelException;

@Service
public class ApplicationService
{
	private final static Logger log = LoggerFactory.getLogger( ApplicationService.class );

	@Autowired
	private PersistenceStrategy persistenceStrategy;

	@Autowired( required = false )
	private FreeMarkerConfigurer freemarkerConfiguration;

	@Autowired
	private SecurityService securityService;

	@Autowired
	private TemplateService templateService;

	// caching the sources
	private Map<String, Source> academicNetworkSourcesCache;

	// caching extraction services
	private Map<String, ExtractionService> extractionServiceCache;

	// caching interest profiles
	private Map<String, InterestProfile> interestProfileCache;

	// caching config profiles
	private Map<String, Config> configCache;

	// user process log
	private Map<String, Map<String, String>> userProcessLogMap;

	// process log with random key
	private Map<String, ProcessLogHelper> processLogMap;

	@PostConstruct
	public void init() throws TemplateModelException
	{
		log.info( "Initializing..." );

		// this.getAcademicNetworkSources();

		if ( freemarkerConfiguration != null )
		{
			// responsible for any security affairs on templates
			exposeFreemarkerSecurityService();
			// responsible for any
			exposeFreemarkerTemplateService();
		}
	}

	private void exposeFreemarkerSecurityService() throws TemplateModelException
	{
		freemarkerConfiguration.getConfiguration().setSharedVariable( "securityService", securityService );
		log.debug( "SecurityService exposed." );
	}

	private void exposeFreemarkerTemplateService() throws TemplateModelException
	{
		freemarkerConfiguration.getConfiguration().setSharedVariable( "templateService", templateService );
		log.debug( "TemplateService exposed." );
	}

	/* Source cache */
	@Transactional
	public Map<String, Source> getAcademicNetworkSources()
	{
		if ( academicNetworkSourcesCache == null || academicNetworkSourcesCache.isEmpty() )
			updateAcademicNetworkSourcesCache();
		return academicNetworkSourcesCache;
	}

	@Transactional
	public void updateAcademicNetworkSourcesCache()
	{
		academicNetworkSourcesCache = persistenceStrategy.getSourceDAO().getSourceMap();
	}

	/* extraction services cache */
	@Transactional
	public Map<String, ExtractionService> getExtractionServices()
	{
		if ( extractionServiceCache == null || extractionServiceCache.isEmpty() )
			updateExtractionServicesCache();
		return extractionServiceCache;
	}

	@Transactional
	public void updateExtractionServicesCache()
	{
		extractionServiceCache = persistenceStrategy.getExtractionServiceDAO().getExtractionServiceMap();
	}

	/* interest profile cache */
	@Transactional
	public Map<String, InterestProfile> getInterestProfiles()
	{
		if ( interestProfileCache == null || interestProfileCache.isEmpty() )
			updateInterestProfilesCache();
		return interestProfileCache;
	}

	@Transactional
	public void updateInterestProfilesCache()
	{
		interestProfileCache = persistenceStrategy.getInterestProfileDAO().getInterestProfileMap();
	}

	/* config cache */
	@Transactional
	public Map<String, Config> getConfig()
	{
		if ( configCache == null || configCache.isEmpty() )
			updateConfigCache();
		return configCache;
	}

	@Transactional
	public String getConfigValue( String configName, String Identifier1, String identifier2 )
	{
		if ( configCache == null || configCache.isEmpty() )
			updateConfigCache();

		Config config = configCache.get( configName );

		if ( config == null )
			return null;

		return config.getConfigPropertyValueByIdentifiers( Identifier1, identifier2 );
	}

	@Transactional
	public void updateConfigCache()
	{
		configCache = persistenceStrategy.getConfigDAO().getConfigMap();
	}

	/* user log map */
	public void putUserProcessLog( String sessionId, String processName, String logMessage )
	{
		if ( this.userProcessLogMap == null )
		{
			this.userProcessLogMap = new HashMap<String, Map<String, String>>();

			// create inner map and put into outer map
			Map<String, String> processLogMap = new HashMap<String, String>();
			processLogMap.put( processName, logMessage );

			this.userProcessLogMap.put( sessionId, processLogMap );

		}
		else
		{
			// check if outer map exist
			if ( this.userProcessLogMap.get( sessionId ) == null )
			{
				// user not exist
				// create inner map and put into outer map
				Map<String, String> processLogMap = new HashMap<String, String>();
				processLogMap.put( processName, logMessage );

				this.userProcessLogMap.put( sessionId, processLogMap );
			}
			else
			{
				// user exist, check for innerMap
				Map<String, String> processLogMap = this.userProcessLogMap.get( sessionId );
				if ( processLogMap.get( processName ) == null )
				{
					// inner map not exist, put new one
					processLogMap.put( processName, logMessage );
				}
				else
				{
					// inner map exist, then update
					processLogMap.put( processName, logMessage );
				}
			}
		}
	}

	/* get user log */
	public String getUserProcessLog( String sessionId, String processName )
	{
		if ( this.userProcessLogMap == null )
		{
			return null;
		}
		else
		{
		if ( this.userProcessLogMap.get( sessionId ) == null )
			return null;
		else
		{
			Map<String, String> processLogMap = this.userProcessLogMap.get( sessionId );
			if ( processLogMap.get( processName ) == null )
			{
				return null;
			}
			else
				return processLogMap.get( processName );
		}
		}
	}

	/* delete user log */
	public void deleteUserProcessLog( String sessionId )
	{
		for ( Iterator<Entry<String, Map<String, String>>> it = this.userProcessLogMap.entrySet().iterator(); it.hasNext(); )
		{
			Entry<String, Map<String, String>> entry = it.next();
			if ( entry.getKey().equals( sessionId ) )
			{
				it.remove();
			}
		}
	}

	/* user log map */
	public void putProcessLog( String processKey, String logMessage, String mode )
	{
		if ( processKey == null )
			return;

		if ( this.processLogMap == null )
		{
			ProcessLogHelper processLogHelper = new ProcessLogHelper();
			processLogHelper.setLogMessge( logMessage );

			this.processLogMap = new HashMap<String, ProcessLogHelper>();
			this.processLogMap.put( processKey, processLogHelper );
		}
		else
		{
			if ( this.processLogMap.get( processKey ) == null )
			{
				ProcessLogHelper processLogHelper = new ProcessLogHelper();
				processLogHelper.setLogMessge( logMessage );

				processLogMap.put( processKey, processLogHelper );
			}
			else
			{
				ProcessLogHelper processLogHelper = this.processLogMap.get( processKey );
				if ( mode.equals( "replace" ) )
					processLogHelper.setLogMessge( logMessage );
				else
					processLogHelper.appendLogMessage( logMessage );
			}
		}
	}

	/* get user log */
	public String getProcessLog( String processKey )
	{
		if ( this.processLogMap == null )
			return "process not found";

		if ( this.processLogMap.get( processKey ) == null )
			return "process not found";
		else
		{
			ProcessLogHelper processLogHelper = this.processLogMap.get( processKey );
			return processLogHelper.getLogMessage();
		}
	}

	/* delete process log */
	public void deleteProcessLog( String processKey )
	{
		for ( Iterator<Entry<String, ProcessLogHelper>> it = this.processLogMap.entrySet().iterator(); it.hasNext(); )
		{
			Entry<String, ProcessLogHelper> entry = it.next();
			if ( entry.getKey().equals( processKey ) )
			{
				it.remove();
			}
		}
	}
}
