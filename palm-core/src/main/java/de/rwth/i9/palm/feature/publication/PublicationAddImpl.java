package de.rwth.i9.palm.feature.publication;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import de.rwth.i9.palm.pdfextraction.service.PdfExtractionService;

@Component
public class PublicationAddImpl implements PublicationAdd
{
	@Autowired
	private PdfExtractionService pdfExtractionService;

	@Override
	public Map<String, Object> uploadAndExtractPdf( MultipartHttpServletRequest request )
	{
		// first upload pdf into system
		uploadPdf( request );

		return null;
	}

	private void uploadPdf( MultipartHttpServletRequest request )
	{

	}

}
