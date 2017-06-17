package de.rwth.i9.palm.analytics.opennlp;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import opennlp.tools.coref.DiscourseEntity;
import opennlp.tools.parser.Parse;
import opennlp.tools.util.Span;

public interface OpenNLP
{
	/**
	 * Read the content from the specified file and return a list of detected
	 * sentences.
	 * 
	 * @param file
	 *            the file to read
	 * @param cs
	 *            the file charset
	 * @return the detected sentences
	 * 
	 * @throws IOException
	 *             If an error occurs while loading the file
	 */
	public String[] detectSentences( final File file, final Charset cs ) throws IOException;

	/**
	 * Break the given content/corpus into sentences.
	 * <p>
	 * The sentence detector is lazily initialized on first use.
	 * </p>
	 * 
	 * @param content
	 *            the text corpus
	 * @return the detected sentences in string array
	 */
	public String[] detectSentences( final String content );

	/**
	 * Tokenize the given sentence.
	 * <p>
	 * The tokenizer is lazily initialized on first use.
	 * </p>
	 * 
	 * @param sentence
	 *            a sentence to tokenize
	 * @return the individual tokens
	 */
	public String[] tokenize( final String sentence );

	/**
	 * Detect the part of speech tags for the given tokens in a sentence.
	 * <p>
	 * The tagger is lazily initialized on first use.
	 * </p>
	 * 
	 * @param tokens
	 *            an array of sentence tokens to tag
	 * @return the individual part-of-speech tags
	 */
	public String[] tagPartOfSpeech( final String[] tokens );

	/**
	 * Find named entities in a tokenized sentence.
	 * <p>
	 * Must call {@link #clearNamedEntityAdaptiveData()} after finding all named
	 * entities in a single document.
	 * </p>
	 *
	 *
	 * @param sentence
	 *            the sentence text
	 * @param tokens
	 *            the sentence tokens
	 * @return a collection of named entity references
	 */
	public List<Span> findNamedEntities( final String sentence, final String[] tokens );

	/**
	 * Find Discourse entities (entity mentions) in a document.
	 * 
	 * @param sentences
	 *            the document sentences
	 * @return the recognized discourse entities.
	 */
	public DiscourseEntity[] findEntityMentions( final String[] sentences );

	/**
	 * Convert the provided sentence and corresponding tokens into a parse tree.
	 * 
	 * @param text
	 *            the sentence text
	 * @return the parse tree
	 */
	public Parse parseSentence( final String text );

	/**
	 * 
	 * @param sentence
	 * @return
	 */
	public List<String> nounPhraseExtractor( String[] tokenizeSentence, String[] posTaggerSentence );
}
