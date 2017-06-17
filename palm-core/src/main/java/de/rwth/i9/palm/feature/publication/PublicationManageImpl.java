package de.rwth.i9.palm.feature.publication;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.rwth.i9.palm.model.Publication;
import de.rwth.i9.palm.pdfextraction.service.PdfExtractionService;
import de.rwth.i9.palm.persistence.PersistenceStrategy;

@Component
public class PublicationManageImpl implements PublicationManage
{

	@Autowired
	private PersistenceStrategy persistenceStrategy;

	@Autowired
	private PdfExtractionService pdfExtractionService;

	@Override
	public Map<String, Object> extractPublicationFromPdf( String publicationId ) throws IOException, InterruptedException, ExecutionException
	{
		// create JSON mapper for response
		Map<String, Object> responseMap = new LinkedHashMap<String, Object>();

		// get publication
		Publication publication = persistenceStrategy.getPublicationDAO().getById( publicationId );
		if ( publication == null )
		{
			responseMap.put( "status", "Error - publication not found" );
			return responseMap;
		}

		pdfExtractionService.extractPdfFromSpecificPublication( publication, responseMap );

		return responseMap;
	}

	@Override
	public Map<String, Object> editPublication( ObjectMapper publicationJson )
	{
		// TODO Auto-generated method stub
		return null;
	}

}
