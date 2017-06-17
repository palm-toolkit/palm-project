package de.rwth.i9.palm.analytics.opennlp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.coref.DefaultLinker;
import opennlp.tools.coref.DiscourseEntity;
import opennlp.tools.coref.Linker;
import opennlp.tools.coref.LinkerMode;
import opennlp.tools.coref.mention.DefaultParse;
import opennlp.tools.coref.mention.Mention;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinder;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.parser.AbstractBottomUpParser;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTagger;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetector;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.rwth.i9.palm.analytics.service.AppService;
import de.rwth.i9.palm.analytics.util.FileUtilities;

@Service
public class OpenNLPImpl implements OpenNLP
{
	static private final Logger logger = LoggerFactory.getLogger( OpenNLPImpl.class );
	/**
	 * application configuration
	 */
	@Autowired
	private AppService appService;

	// the named entities
	private static final String[] NAME_TYPES = { "person", "organization", "location" };

	// OpenNLP components, lazily initialized.
	private SentenceDetector sentenceDetector = null;
	private Tokenizer tokenizer = null;
	private POSTagger posTagger = null;
	final private Map<String, TokenNameFinder> nameFinderMap = new HashMap<String, TokenNameFinder>();
	private Parser parser = null;
	private Linker linker = null;

	@Override
	public String[] detectSentences( final File file, final Charset cs ) throws IOException
	{
		final List<String> lines = FileUtilities.loadLines( file, cs );
		final ArrayList<String> sentences = new ArrayList<String>();
		for ( final String content : lines )
		{
			final String[] detected = detectSentences( content );
			for ( int idx = 0; idx < detected.length; idx++ )
			{
				final String sentence = detected[idx].trim();
				// check for ending with punctuation
				if ( sentence.matches( ".*\\p{P}$" ) )
				{
					sentences.add( sentence );
				}
				else
				{
					logger.warn( "Sentence #" + idx + " does not end with punctuation: [" + sentence + "]" );
					logger.warn( "Appending a . (period)" );
					sentences.add( sentence + "." );
				}
			}
		}
		return sentences.toArray( new String[0] );
	}

	@Override
	public String[] detectSentences( String content )
	{
		if ( sentenceDetector == null )
		{
			// lazy initialize
			InputStream modelIn = null;
			try
			{
				// sentence detector
				modelIn = new FileInputStream(appService.getOpenNLPSentence());
				//getClass().getResourceAsStream( appService.getOpenNLPSentence() );
				final SentenceModel sentenceModel = new SentenceModel( modelIn );
				modelIn.close();
				sentenceDetector = new SentenceDetectorME( sentenceModel );
				logger.info( "done." );
			}
			catch ( final IOException ioe )
			{
				logger.error( "Error loading sentence detector", ioe );
			}
			finally
			{
				if ( modelIn != null )
				{
					try
					{
						modelIn.close();
					}
					catch ( final IOException e )
					{
					}
				}
			}
		}

		// detect sentences
		return sentenceDetector.sentDetect( content );
	}

	@Override
	public String[] tokenize( final String sentence )
	{
		// tokenize
		return tokenizer().tokenize( sentence );
	}

	/**
	 * @return the lazily-initialized tokenizer
	 */
	private Tokenizer tokenizer()
	{
		if ( tokenizer == null )
		{
			// lazy initialize
			InputStream modelIn = null;
			try
			{
				// tokenizer
				logger.info( "Loading tokenizer model" );
				modelIn = new FileInputStream( appService.getOpenNLPTokenizer() );
				//getClass().getResourceAsStream( appService.getOpenNLPTokenizer() );
				final TokenizerModel tokenModel = new TokenizerModel( modelIn );
				modelIn.close();
				tokenizer = new TokenizerME( tokenModel );
				logger.info( "done." );
			}
			catch ( final IOException ioe )
			{
				logger.error( "Error loading tokenizer", ioe );
			}
			finally
			{
				if ( modelIn != null )
				{
					try
					{
						modelIn.close();
					}
					catch ( final IOException e )
					{
					}
				}
			}
		}
		return tokenizer;
	}

	@Override
	public String[] tagPartOfSpeech( String[] tokens )
	{
		if ( posTagger == null )
		{
			// lazy initialize
			InputStream modelIn = null;
			try
			{
				// tagger
				logger.info( "Loading part-of-speech model" );
				modelIn = new FileInputStream( appService.getOpenNLPPos() );
				//getClass().getResourceAsStream( appService.getOpenNLPPos() );
				final POSModel posModel = new POSModel( modelIn );
				modelIn.close();
				posTagger = new POSTaggerME( posModel );
				logger.info( "done." );
			}
			catch ( final IOException ioe )
			{
				logger.error( "Error loading part-of-speech tagger", ioe );
			}
			finally
			{
				if ( modelIn != null )
				{
					try
					{
						modelIn.close();
					}
					catch ( final IOException e )
					{
					}
				}
			}
		}
		return posTagger.tag( tokens );
	}

	@Override
	public List<Span> findNamedEntities( final String sentence, final String[] tokens )
	{
		final List<Span> entities = new LinkedList<Span>();

		// use each type of finder to identify named entities
		for ( final TokenNameFinder finder : nameFinders() )
		{
			entities.addAll( Arrays.asList( finder.find( tokens ) ) );
		}

		return entities;
	}

	/**
	 * Must be called between documents or can negatively impact detection rate.
	 */
	public void clearNamedEntityAdaptiveData()
	{
		for ( final TokenNameFinder finder : nameFinders() )
		{
			finder.clearAdaptiveData();
		}
	}

	/**
	 * @return the lazily-initialized token name finders
	 */
	private TokenNameFinder[] nameFinders()
	{
		final TokenNameFinder[] finders = new TokenNameFinder[NAME_TYPES.length];
		// one for each name type
		for ( int i = 0; i < NAME_TYPES.length; i++ )
		{
			finders[i] = nameFinder( NAME_TYPES[i] );
		}
		return finders;
	}

	/**
	 * @param type
	 *            the name type recognizer to load
	 * @return the lazily-initialized name token finder
	 */
	private TokenNameFinder nameFinder( final String type )
	{
		if ( !nameFinderMap.containsKey( type ) )
		{
			final TokenNameFinder finder = createNameFinder( type );
			nameFinderMap.put( type, finder );
		}
		return nameFinderMap.get( type );
	}

	/**
	 * @param type
	 *            the name type recognizer to load
	 * @return the lazily-initialized name token finder
	 */
	private TokenNameFinder createNameFinder( final String type )
	{
		InputStream modelIn = null;
		try
		{
			logger.info( "Loading " + type + " named entity model" );
			modelIn = new FileInputStream(  String.format( appService.getOpenNLPNamefinderFormat(), type ));
			//getClass().getResourceAsStream( String.format( appService.getOpenNLPNamefinderFormat(), type ) );
			final TokenNameFinderModel nameFinderModel = new TokenNameFinderModel( modelIn );
			modelIn.close();
			return new NameFinderME( nameFinderModel );
		}
		catch ( final IOException ioe )
		{
			logger.error( "Error loading " + type + " token name finder", ioe );
		}
		finally
		{
			if ( modelIn != null )
			{
				try
				{
					modelIn.close();
				}
				catch ( final IOException e )
				{
				}
			}
		}
		return null;
	}

	public DiscourseEntity[] findEntityMentions( final String[] sentences )
	{

		// list of document mentions
		final List<Mention> document = new ArrayList<Mention>();

		for ( int i = 0; i < sentences.length; i++ )
		{
			// generate the sentence parse tree
			final Parse parse = parseSentence( sentences[i] );

			final DefaultParse parseWrapper = new DefaultParse( parse, i );
			final Mention[] extents = linker().getMentionFinder().getMentions( parseWrapper );

			// Note: taken from TreebankParser source...
			for ( int ei = 0, en = extents.length; ei < en; ei++ )
			{
				// construct new parses for mentions which don't have
				// constituents.
				if ( extents[ei].getParse() == null )
				{
					// not sure how to get head index, but its not used at this
					// point
					final Parse snp = new Parse( parse.getText(), extents[ei].getSpan(), "NML", 1.0, 0 );
					parse.insert( snp );
					logger.debug( "Setting new parse for " + extents[ei] + " to " + snp );
					extents[ei].setParse( new DefaultParse( snp, i ) );
				}
			}
			document.addAll( Arrays.asList( extents ) );
		}

		if ( !document.isEmpty() )
		{
			return linker().getEntities( document.toArray( new Mention[0] ) );
		}
		return new DiscourseEntity[0];
	}

	/**
	 * @return the lazily-initialized linker
	 */
	private Linker linker()
	{
		if ( linker == null )
		{
			try
			{
				// linker
				logger.info( "Loading the linker" );
				System.out.println( ( this.getClass().getClassLoader().getResource( "" ).getPath() ).replace( "test-classes", "classes" ) + ( appService.getOpenNLPCorefDir() ).substring( 1, appService.getOpenNLPCorefDir().length() ) );
				linker = new DefaultLinker(
				// LinkerMode should be TEST
				// Note: I tried EVAL for a long time before realizing that was
				// the problem
						( this.getClass().getClassLoader().getResource( "" ).getPath() ).replace( "test-classes", "classes" ) + ( appService.getOpenNLPCorefDir() ).substring( 1, appService.getOpenNLPCorefDir().length() ), LinkerMode.TEST );

			}
			catch ( final IOException ioe )
			{
				logger.error( "Error loading linker", ioe );
			}
		}
		return linker;
	}

	public Parse parseSentence( final String text )
	{

		final Parse p = new Parse( text,
		// a new span covering the entire text
				new Span( 0, text.length() ),
				// the label for the top if an incomplete node
				AbstractBottomUpParser.INC_NODE,
				// the probability of this parse...uhhh...?
				1,
				// the token index of the head of this parse
				0 );

		final Span[] spans = tokenizer().tokenizePos( text );

		for ( int idx = 0; idx < spans.length; idx++ )
		{
			final Span span = spans[idx];
			// flesh out the parse with token sub-parses
			p.insert( new Parse( text, span, AbstractBottomUpParser.TOK_NODE, 0, idx ) );
		}

		return parse( p );
	}

	/**
	 * Parse the given parse object.
	 * <p>
	 * The parser is lazily initialized on first use.
	 * </p>
	 * 
	 * @param p
	 *            the parse object
	 * @return the parsed parse
	 */
	private Parse parse( final Parse p )
	{
		return parser().parse( p );
	}

	private Parser parser()
	{
		if ( parser == null )
		{
			// lazily initialize the parser
			InputStream modelIn = null;
			try
			{
				// parser
				logger.info( "Loading the parser model" );
				modelIn = new FileInputStream( appService.getOpenNLPParser() );
				//getClass().getResourceAsStream( appService.getOpenNLPParser() );
				final ParserModel parseModel = new ParserModel( modelIn );
				modelIn.close();
				parser = ParserFactory.create( parseModel );
			}
			catch ( final IOException ioe )
			{
				logger.error( "Error loading parser", ioe );
			}
			finally
			{
				if ( modelIn != null )
				{
					try
					{
						modelIn.close();
					}
					catch ( final IOException e )
					{
					}
				}
			}
		}
		// return the parser
		return parser;
	}

	@Override
	public List<String> nounPhraseExtractor( String[] tokenizeSentence, String[] posTaggerSentence )
	{
		ChunkerME chunker = new ChunkerME( this.getChunkerModel() );

		Span[] chunks = chunker.chunkAsSpans( tokenizeSentence, posTaggerSentence );
	    
	  //chunkStrings are the actual chunks
		String[] chunkStrings = Span.spansToStrings( chunks, tokenizeSentence );
		List<String> result = new ArrayList<String>();
		for ( int i = 0; i < chunks.length; i++ )
		{
			if ( chunks[i].getType().equals( "NP" ) )
			{
				result.add( chunkStrings[i] );
			}

		}
		return result;
	}
	
	public static List<String> ngram(List<String> input, int n, String separator) {
	    if (input.size() <= n) {
	      return input;
	    }
	    List<String> outGrams = new ArrayList<String>();
	    for (int i = 0; i < input.size() - (n - 2); i++) {
	      String gram = "";
	      if ((i + n) <= input.size()) {
	        for (int x = i; x < (n + i); x++) {
	          gram += input.get(x) + separator;
	        }
	        gram = gram.substring(0, gram.lastIndexOf(separator));
	        outGrams.add(gram);
	      }
	    }
	    return outGrams;
	  }

	private ChunkerModel getChunkerModel()
	{

		ChunkerModel model = null;
		InputStream modelIn = null;
		try
		{
			modelIn = new FileInputStream( appService.getOpenNLPChunker() );
			//getClass().getResourceAsStream( appService.getOpenNLPChunker() );
			model = new ChunkerModel( modelIn );
		}
		catch ( IOException e )
		{
			// Model loading failed, handle the error
			e.printStackTrace();
		}
		finally
		{
			if ( modelIn != null )
			{
				try
				{
					modelIn.close();
				}
				catch ( IOException e )
				{
				}
			}
		}

		return model;
	}

}
