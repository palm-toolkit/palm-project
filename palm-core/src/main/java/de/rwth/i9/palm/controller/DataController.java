package de.rwth.i9.palm.controller;


import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import de.rwth.i9.palm.persistence.PersistenceStrategy;

@Controller
@RequestMapping( value = "/data" )
public class DataController
{
	@Autowired
	private PersistenceStrategy persistenceStrategy;

	@Transactional
	@RequestMapping( value = "/reindex/all", method = RequestMethod.GET )
	public @ResponseBody String allReindex()
	{
		try
		{
			persistenceStrategy.getInstitutionDAO().doReindexing();
			persistenceStrategy.getAuthorDAO().doReindexing();
			persistenceStrategy.getPublicationDAO().doReindexing();
			persistenceStrategy.getEventDAO().doReindexing();
			persistenceStrategy.getEventGroupDAO().doReindexing();
			persistenceStrategy.getCircleDAO().doReindexing();
		}
		catch ( InterruptedException e )
		{
			return ( e.getMessage() );
		}

		return "re-indexing institutions, authors, publications, events and circles complete";
	}

	@Transactional
	@RequestMapping( value = "/reindex/institution", method = RequestMethod.GET )
	public @ResponseBody String institutionReindex()
	{
		try
		{
			persistenceStrategy.getInstitutionDAO().doReindexing();
		}
		catch ( InterruptedException e )
		{
			return ( e.getMessage() );
		}
		return "re indexing institution complete";
	}

	@Transactional
	@RequestMapping( value = "/reindex/author", method = RequestMethod.GET )
	public @ResponseBody String authorReindex()
	{
		try
		{
			persistenceStrategy.getAuthorDAO().doReindexing();
		}
		catch ( InterruptedException e )
		{
			return ( e.getMessage() );
		}
		return "re indexing author complete";
	}

	@Transactional
	@RequestMapping( value = "/reindex/publication", method = RequestMethod.GET )
	public @ResponseBody String publicationReindex()
	{
		try
		{
			persistenceStrategy.getPublicationDAO().doReindexing();
		}
		catch ( InterruptedException e )
		{
			return ( e.getMessage() );
		}
		return "re indexing publication complete";
	}

	@Transactional
	@RequestMapping( value = "/reindex/event", method = RequestMethod.GET )
	public @ResponseBody String eventReindex()
	{
		try
		{
			persistenceStrategy.getEventDAO().doReindexing();
			persistenceStrategy.getEventGroupDAO().doReindexing();
		}
		catch ( InterruptedException e )
		{
			return ( e.getMessage() );
		}
		return "re indexing event complete";
	}

	@Transactional
	@RequestMapping( value = "/reindex/circle", method = RequestMethod.GET )
	public @ResponseBody String circleReindex()
	{
		try
		{
			persistenceStrategy.getCircleDAO().doReindexing();
		}
		catch ( InterruptedException e )
		{
			return ( e.getMessage() );
		}
		return "re indexing circle complete";
	}
}
