package de.rwth.i9.palm.helper;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.sax.BodyContentHandler;

public class PdfDetail
{
	private BodyContentHandler handler;
	private Metadata metadata;

	public BodyContentHandler getHandler()
	{
		return handler;
	}

	public void setHandler( BodyContentHandler handler )
	{
		this.handler = handler;
	}

	public Metadata getMetadata()
	{
		return metadata;
	}

	public void setMetadata( Metadata metadata )
	{
		this.metadata = metadata;
	}
}
