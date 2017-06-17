package de.rwth.i9.palm.helper;

import java.util.List;

public class OpenNlpResultHelper
{
	private String sentence;
	private List<String> tokenizedSentence;
	private List<String> posTaggedSentence;
	private List<String> nounPhraseChunked;
	private List<String> cValueResult;

	public List<String> getTokenizedSentence()
	{
		return tokenizedSentence;
	}

	public void setTokenizedSentence( List<String> tokenizedSentence )
	{
		this.tokenizedSentence = tokenizedSentence;
	}

	public List<String> getPosTaggedSentence()
	{
		return posTaggedSentence;
	}

	public void setPosTaggedSentence( List<String> posTaggedSentence )
	{
		this.posTaggedSentence = posTaggedSentence;
	}

	public List<String> getNounPhraseChunked()
	{
		return nounPhraseChunked;
	}

	public void setNounPhraseChunked( List<String> nounPhraseChunked )
	{
		this.nounPhraseChunked = nounPhraseChunked;
	}

	public List<String> getcValueResult()
	{
		return cValueResult;
	}

	public void setcValueResult( List<String> cValueResult )
	{
		this.cValueResult = cValueResult;
	}

	public String getSentence()
	{
		return sentence;
	}

	public void setSentence( String sentence )
	{
		this.sentence = sentence;
	}
}
