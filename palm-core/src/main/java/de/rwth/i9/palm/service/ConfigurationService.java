package de.rwth.i9.palm.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 
 * @author sigit
 *
 */
@Service
public class ConfigurationService
{
	private final Logger log = LoggerFactory.getLogger( ConfigurationService.class );

	// properties
	@Value( "${virtuoso.url}" )
	private String virtuosoUrl;

	@Value( "${virtuoso.user}" )
	private String virtuosoUser;

	@Value( "${virtuoso.password}" )
	private String virtuosoPassword;

	@Value( "${virtuoso.graph}" )
	private String virtuosoGraph;

	@Value( "${sparql.endpoint}" )
	private String sparqlEndpoint;

	@Value( "${sparql.engine}" )
	private String sparqlEngine;

	@Value( "${sparql.proxy}" )
	private String sparqlProxy;

	public String getVirtuosoUrl()
	{
		return virtuosoUrl;
	}

	public void setVirtuosoUrl( String virtuosoUrl )
	{
		this.virtuosoUrl = virtuosoUrl;
	}

	public String getVirtuosoUser()
	{
		return virtuosoUser;
	}

	public void setVirtuosoUser( String virtuosoUser )
	{
		this.virtuosoUser = virtuosoUser;
	}

	public String getVirtuosoPassword()
	{
		return virtuosoPassword;
	}

	public void setVirtuosoPassword( String virtuosoPassword )
	{
		this.virtuosoPassword = virtuosoPassword;
	}

	public String getVirtuosoGraph()
	{
		return virtuosoGraph;
	}

	public void setVirtuosoGraph( String virtuosoGraph )
	{
		this.virtuosoGraph = virtuosoGraph;
	}

	public String getSparqlEndpoint()
	{
		return sparqlEndpoint;
	}

	public void setSparqlEndpoint( String sparqlEndpoint )
	{
		this.sparqlEndpoint = sparqlEndpoint;
	}

	public String getSparqlEngine()
	{
		return sparqlEngine;
	}

	public void setSparqlEngine( String sparqlEngine )
	{
		this.sparqlEngine = sparqlEngine;
	}

	public String getSparqlProxy()
	{
		return sparqlProxy;
	}

	public void setSparqlProxy( String sparqlProxy )
	{
		this.sparqlProxy = sparqlProxy;
	}
}
