package de.rwth.i9.palm.analytics.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author sigit
 *
 */
@Service
public class AppService
{
	private final Logger log = LoggerFactory.getLogger( AppService.class );

	// properties
	@Value( "${opennlp.sentence}" )
	private String openNLPSentence;

	@Value( "${opennlp.tokenizer}" )
	private String openNLPTokenizer;

	@Value( "${opennlp.pos}" )
	private String openNLPPos;

	@Value( "${opennlp.namefinder.format}" )
	private String openNLPNamefinderFormat;

	@Value( "${opennlp.parser}" )
	private String openNLPParser;

	@Value( "${opennlp.coref.dir}" )
	private String openNLPCorefDir;

	@Value( "${opennlp.chunker}" )
	private String openNLPChunker;

	public String getOpenNLPSentence()
	{
		return openNLPSentence;
	}

	public void setOpenNLPSentence( String openNLPSentence )
	{
		this.openNLPSentence = openNLPSentence;
	}

	public String getOpenNLPTokenizer()
	{
		return openNLPTokenizer;
	}

	public void setOpenNLPTokenizer( String openNLPTokenizer )
	{
		this.openNLPTokenizer = openNLPTokenizer;
	}

	public String getOpenNLPPos()
	{
		return openNLPPos;
	}

	public void setOpenNLPPos( String openNLPPos )
	{
		this.openNLPPos = openNLPPos;
	}

	public String getOpenNLPNamefinderFormat()
	{
		return openNLPNamefinderFormat;
	}

	public void setOpenNLPNamefinderFormat( String openNLPNamefinderFormat )
	{
		this.openNLPNamefinderFormat = openNLPNamefinderFormat;
	}

	public String getOpenNLPParser()
	{
		return openNLPParser;
	}

	public void setOpenNLPParser( String openNLPParser )
	{
		this.openNLPParser = openNLPParser;
	}

	public String getOpenNLPCorefDir()
	{
		return openNLPCorefDir;
	}

	public void setOpenNLPCorefDir( String openNLPCorefDir )
	{
		this.openNLPCorefDir = openNLPCorefDir;
	}

	public String getOpenNLPChunker()
	{
		return openNLPChunker;
	}

	public void setOpenNLPChunker( String openNLPChunker )
	{
		this.openNLPChunker = openNLPChunker;
	}
}
