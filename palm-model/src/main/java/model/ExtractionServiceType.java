package de.rwth.i9.palm.model;

/**
 * @author sigit 
 */
public enum ExtractionServiceType
{
	OPENNLP("OpenNLP"), ALCHEMY("Alchemy"), TEXTWISE("Textwise"), YAHOOCONTENTANALYSIS("Yahoo Content Analysis"), FIVEFILTERS("Five Filters"), OPENCALAIS("OpenCalais"), KEYWORDBASED("Keyword Based");

	private String name;

	private ExtractionServiceType( final String name )
	{
		this.name = name;
	}

	public String getName()
	{
		return this.name;
	}
}
