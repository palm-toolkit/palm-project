package de.rwth.i9.palm.analytics.api;

import de.rwth.i9.palm.analytics.algorithm.corephrase.CorePhrase;
import de.rwth.i9.palm.analytics.algorithm.cvalue.CValue;
import de.rwth.i9.palm.analytics.algorithm.dynamicLDA.DynamicTopicModel;
import de.rwth.i9.palm.analytics.algorithm.lda.Lda;
import de.rwth.i9.palm.analytics.algorithm.ngram.NGrams;
import de.rwth.i9.palm.analytics.opennlp.OpenNLP;
import de.rwth.i9.palm.analytics.textcompare.TextCompare;

public interface PalmAnalytics
{
	public CorePhrase getCorePhraseAlgorithm();

	public CValue getCValueAlgorithm();

	public DynamicTopicModel getDynamicTopicModel();

	public OpenNLP getOpenNLPTool();
	
	public NGrams getNGrams();

	public Lda getLda();

	public TextCompare getTextCompare();

}