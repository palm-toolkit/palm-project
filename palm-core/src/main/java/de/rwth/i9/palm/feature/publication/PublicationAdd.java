package de.rwth.i9.palm.feature.publication;

import java.util.Map;

import org.springframework.web.multipart.MultipartHttpServletRequest;

public interface PublicationAdd
{
	public Map<String, Object> uploadAndExtractPdf( MultipartHttpServletRequest request );
}
