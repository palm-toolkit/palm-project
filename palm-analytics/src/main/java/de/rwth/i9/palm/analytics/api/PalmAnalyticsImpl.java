package de.rwth.i9.palm.analytics.api;

import org.springframework.beans.factory.annotation.Autowired;

import de.rwth.i9.palm.analytics.algorithm.corephrase.CorePhrase;
import de.rwth.i9.palm.analytics.algorithm.corephrase.CorePhraseImpl;
import de.rwth.i9.palm.analytics.algorithm.cvalue.CValue;
import de.rwth.i9.palm.analytics.algorithm.cvalue.CValueAlgorithm;
import de.rwth.i9.palm.analytics.algorithm.dynamicLDA.DynamicLDA;
import de.rwth.i9.palm.analytics.algorithm.dynamicLDA.DynamicTopicModel;
import de.rwth.i9.palm.analytics.algorithm.lda.LDAJob;
import de.rwth.i9.palm.analytics.algorithm.lda.Lda;
import de.rwth.i9.palm.analytics.algorithm.ngram.NGrams;
import de.rwth.i9.palm.analytics.algorithm.ngram.Ngram;
import de.rwth.i9.palm.analytics.opennlp.OpenNLP;
import de.rwth.i9.palm.analytics.opennlp.OpenNLPImpl;
import de.rwth.i9.palm.analytics.textcompare.TextCompare;
import de.rwth.i9.palm.analytics.textcompare.TextCompareImpl;

/**
 * This interface is a Factory-interface for any analytics
 */
public class PalmAnalyticsImpl implements PalmAnalytics
{
	@Autowired( required = false )
	private CorePhrase corePhrase;
	
	@Autowired( required = false )
	private CValue cValue;

	@Autowired( required = false )
	private DynamicTopicModel dynamicTopicModel;

	@Autowired( required = false )
	private OpenNLP openNLP;
	
	@Autowired( required = false )
	private NGrams nGrams;

	@Autowired( required = false )
	private Lda lda;

	@Autowired( required = false )
	private TextCompare textCompare;

	@Override
	public CorePhrase getCorePhraseAlgorithm()
	{
		if ( this.corePhrase == null )
			this.corePhrase = new CorePhraseImpl();

		return this.corePhrase;
	}

	public CValue getCValueAlgorithm()
	{
		if ( this.cValue == null )
			this.cValue = new CValueAlgorithm();

		return this.cValue;
	}

	@Override
	public DynamicTopicModel getDynamicTopicModel()
	{
		if ( this.dynamicTopicModel == null )
			this.dynamicTopicModel = new DynamicLDA();

		return this.dynamicTopicModel;
	}

	@Override
	public OpenNLP getOpenNLPTool()
	{
		if ( this.openNLP == null )
			this.openNLP = new OpenNLPImpl();

		return this.openNLP;
	}

	@Override
	public NGrams getNGrams()
	{
		if ( this.nGrams == null )
			this.nGrams = new Ngram();

		return this.nGrams;
	}

	@Override
	public Lda getLda()
	{
		if ( this.lda == null )
			this.lda = new LDAJob();

		return this.lda;
	}

	@Override
	public TextCompare getTextCompare()
	{
		if( this.textCompare == null )
			this.textCompare = new TextCompareImpl();
		
		return this.textCompare;
	}

}
