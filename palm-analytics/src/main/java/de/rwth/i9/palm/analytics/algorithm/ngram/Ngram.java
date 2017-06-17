package de.rwth.i9.palm.analytics.algorithm.ngram;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import cc.mallet.types.InstanceList;
import cc.mallet.util.Randoms;
import de.rwth.i9.palm.analytics.util.TopicMiningConstants;

public class Ngram implements NGrams
{
	public String path = TopicMiningConstants.USER_DESKTOP_PATH;
	public TopicalNGrams tng;

	public InstanceList getInstanceDataDirectoryLevel( String path, String purpose, String entityId ) throws IOException
	{
		// Get the data from a directory and convert it into mallet format
		// Use importData Class to make input traverse through the following
		// pipes
		// 1. Input2CharSequence
		// 2. CharSequence2TokenSequence
		// 3. TokenSequenceLowercase
		// 4. TokenSequenceRemoveStopwords
		// 5. TokenSequence2FeatureSequenceBigrams

		if ( entityId.isEmpty() )
		{
			String processBuilder_command = TopicMiningConstants.USER_PROCESS_COMMAND_INPUT + purpose + "\\" + purpose + TopicMiningConstants.USER_PROCESS_COMMAND_OUTPUT + purpose + "\\MALLET\\" + purpose + "-N-" + purpose + ".mallet";

			// Windows ProcessBuilder
			ProcessBuilder builder = new ProcessBuilder( "cmd.exe", "/c", processBuilder_command );

			// MAC Process
			// Process p = Runtime.getRuntime().exec(new String []{"/bin/bash",
			// "-c", processBuilder_command });
			// try {
			// p.waitFor();
			// } catch (InterruptedException e) {
			// e.printStackTrace();
			// }
			// ------------------

			// Win
			// ProcessBuilder builder = new ProcessBuilder( "cmd.exe", "/c", "cd
			// \"C:\\mallet\"&& bin\\mallet import-dir --input
			// C:\\Users\\Albi\\Desktop\\" + purpose + "\\" + purpose + "
			// --keep-sequence-bigrams --remove-stopwords --extra-stopwords
			// C:\\mallet\\stoplists\\extra-stoplist.txt" + " --output
			// C:\\Users\\Albi\\Desktop\\" + purpose + "\\MALLET\\" + purpose +
			// "-N-" + purpose + ".mallet" );
			// Mac ProcessBuilder
			// Process p = Runtime.getRuntime().exec(new String
			// []{"/bin/bash","-c", "/Users/pirolena/Documents/mallet/bin/mallet
			// import-dir --input " + path + purpose + "/" + purpose + "
			// --keep-sequence-bigrams --remove-stopwords --extra-stopwords
			// /Users/pirolena/Documents/mallet/stoplists/extra-stoplist.txt
			// --output /Users/pirolena/Desktop/" + purpose + "/MALLET/" +
			// purpose + "-N-" + purpose + ".mallet"});

			builder.redirectErrorStream( true );
			Process p = builder.start();
			BufferedReader r = new BufferedReader( new InputStreamReader( p.getInputStream() ) );
			String line;

			while ( true )
			{
				line = r.readLine();
				if ( line == null )
				{
					break;
				}
			}
			InstanceList training = InstanceList.load( new File( path + purpose + "/MALLET/" + purpose + "-N-" + purpose + ".mallet" ) );
			return training;
		}
		else
		{
			String processBuilder_command = TopicMiningConstants.USER_PROCESS_COMMAND_INPUT + purpose + "\\" + entityId + TopicMiningConstants.USER_PROCESS_COMMAND_OUTPUT + purpose + "\\MALLET\\" + purpose + "-N-" + entityId + ".mallet";

			// ProcessBuilder Windows
			ProcessBuilder builder = new ProcessBuilder( "cmd.exe", "/c", processBuilder_command );

			// ProcessBuilder Mac
			// Process p = Runtime.getRuntime().exec( new String[] {
			// "/bin/bash", "-c", processBuilder_command});
			// try
			// {
			// p.waitFor();
			// }
			// catch ( InterruptedException e )
			// {
			// e.printStackTrace();
			// }
			// -----------------

			builder.redirectErrorStream( true );
			Process p = builder.start();
			BufferedReader r = new BufferedReader( new InputStreamReader( p.getInputStream() ) );
			String line;

			while ( true )
			{
				line = r.readLine();
				if ( line == null )
				{
					break;
				}
			}
			File entityFile = new File( path + purpose + "\\\\MALLET\\\\" + purpose + "-N-" + entityId + ".mallet" );

			InstanceList training = InstanceList.load( entityFile );
			return training;
		}
	}

	// create instanceList for each of the authors in order to run the Topical
	// ngrams for each of them
	// generate the unique set of topics for each of the authors
	/**
	 * 
	 * @param path
	 * @param purpose
	 * @param entityId
	 * @return list of instances
	 * @throws IOException
	 */
	public InstanceList getInstanceDataFileLevel( String path, String purpose, String entityId ) throws IOException
	{
		// Get the data from a directory and convert it into mallet format
		// Use importData Class to make input traverse through the following
		// pipes
		// 1. Input2CharSequence
		// 2. CharSequence2TokenSequence
		// 3. TokenSequenceLowercase
		// 4. TokenSequenceRemoveStopwords
		// 5. TokenSequence2FeatureSequenceBigrams

		// builder on windows
		String processBuilder_command = TopicMiningConstants.USER_PROCESS_COMMAND_INPUT + purpose + "\\" + purpose + "\\" + entityId + ".txt" + TopicMiningConstants.USER_PROCESS_COMMAND_WITHOUT_STOPWORDS_OUTPUT + purpose + "\\MALLET\\" + purpose + "-N-grams" + purpose + ".mallet";

		ProcessBuilder builder = new ProcessBuilder( "cmd.exe", "/c", processBuilder_command );

		// builder on mac
		// Process p = Runtime.getRuntime().exec(new String []{"/bin/bash",
		// "-c", processBuilder_command} );
		// try {
		// p.waitFor();
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// builder.redirectErrorStream( true );
		// Process p = builder.start();
		// BufferedReader r = new BufferedReader( new InputStreamReader(
		// p.getInputStream() ) );
		// String line;
		//
		// while ( true )
		// {
		// line = r.readLine();
		// if ( line == null )
		// {
		// break;
		// }
		// }
		InstanceList training = InstanceList.load( new File( path + purpose + "/" + purpose + "-N-Grams" + entityId + ".mallet" ) );
		return training;
	}

	/**
	 * Set the number of topics covering our corpora
	 */
	public TopicalNGrams setNumberTopics( int numTopics )
	{
		TopicalNGrams m = new TopicalNGrams( numTopics, 50.0, 0.01, 0.01, 0.03, 0.2, 1000 );
		return m;
	}

	/**
	 * Get the number of topics from an already created model
	 */
	public int getNumTopics()
	{
		return tng.numTopics;
	}

	/**
	 * Get the number of instances of a model
	 */
	public int getNumInstances()
	{
		return tng.ilist.size();
	}

	/**
	 * Create a model under a specific path
	 */
	public TopicalNGrams createModel( String path, String purpose, String entityId, int numTopics ) throws NullPointerException
	{
		int numFiles;

		// get the number of files available for each level of hierarchy, it
		// influences the number of topics
		if ( entityId.isEmpty() )
		{
			numFiles = new File( path + purpose + "/" + purpose ).listFiles().length;
			if ( numFiles == 0 )
			{
				return null;
			}
		}
		else
		{
			numFiles = new File( path + purpose + "/" + entityId ).listFiles().length;
			if ( numFiles == 0 )
			{
				return null;
			}
		}

		// by heuristics decide on maximal number of Topics the model will
		// contain dependent on number of files
		if ( numFiles > 100 )
		{
			numTopics = 100;
		}
		else if ( numFiles < 10 && numFiles > 1 )
		{
			numTopics = numFiles - 1;
		}
		else if ( numFiles <= 1 )
		{
			numTopics = 1;
		}
		else
		{
			// numTopics = 5;

		}

		TopicalNGrams ngram = new TopicalNGrams( numTopics, 50.0, 0.01, 0.01, 0.03, 0.2, 1000 );
		InstanceList trained;
		try
		{
			trained = getInstanceDataDirectoryLevel( path, purpose, entityId );
			ngram.estimate( trained, 100, 1, 0, null, new Randoms() );
		}
		catch ( IOException e )
		{
			e.printStackTrace();
		}

		return ngram;
	}

	/**
	 * Create a model with variable number of topics Number of topics depending
	 * on number of files (instances)
	 */
	public TopicalNGrams createModelRevised( String path, String purpose, String entityId, int numTopics )
	{
		int numFiles;

		// get the number of files available for each level of hierarchy, it
		// influences the number of topics
		if ( entityId.isEmpty() )
		{
			numFiles = new File( path + purpose + "/" + purpose ).listFiles().length;
			if ( numFiles == 0 )
			{
				return null;
			}
		}
		else
		{
			numFiles = new File( path + purpose + "/" + entityId ).listFiles().length;
			if ( numFiles == 0 )
			{
				return null;
			}
		}

		// by heuristics decide on maximal number of Topics the model will
		// contain dependent on number of files
		if ( numFiles > 100 )
		{
			numTopics = 100;
		}
		else if ( numFiles < 10 )
		{
			numTopics = numFiles;
		}
		else
		{
			// default number of topics

		}

		TopicalNGrams ngram = new TopicalNGrams( numTopics, 50.0, 0.01, 0.01, 0.03, 0.2, 1000 );
		InstanceList trained;
		try
		{
			trained = getInstanceDataDirectoryLevel( path, purpose, entityId );
			ngram.estimate( trained, 50, 1, 0, null, new Randoms() );
		}
		catch ( IOException e )
		{
			e.printStackTrace();
		}

		return ngram;
	}

	/**
	 * Use the already existing models to start topic estimation
	 */
	public TopicalNGrams useTrainedData( String path, String purpose, String entityId, int numTopics )
	{
		TopicalNGrams ngram = new TopicalNGrams( numTopics, 50.0, 0.01, 0.01, 0.03, 0.2, 1000 );
		InstanceList trained;
		if ( entityId == "" )
		{
			trained = InstanceList.load( new File( path + purpose + "/MALLET/" + purpose + "-N-" + purpose + ".mallet" ) );
		}
		else
			trained = InstanceList.load( new File( path + purpose + "\\\\MALLET\\\\" + purpose + "-N-" + entityId + ".mallet" ) );

		if ( !trained.isEmpty() )
			ngram.estimate( trained, 100, 1, 0, null, new Randoms() );
		else
			return null;

		return ngram;
	}

	/**
	 * Not used method
	 */
	public TopicalNGrams useTrainedDataRevised( String path, String purpose, String entityId, int numTopics )
	{
		TopicalNGrams ngram = new TopicalNGrams( numTopics, 50.0, 0.01, 0.01, 0.03, 0.2, 1000 );
		InstanceList trained;
		if ( entityId == "" )
		{
			trained = InstanceList.load( new File( path + purpose + "/MALLET/" + purpose + "-N-" + purpose + ".mallet" ) );
		}
		else
			trained = InstanceList.load( new File( path + purpose + "/MALLET/" + entityId + ".mallet" ) );

		if ( !trained.isEmpty() )
			ngram.estimate( trained, 50, 1, 0, path + "Model.txt", new Randoms() );
		else
			return null;

		return ngram;
	}

	/**
	 * Not used method
	 */
	public void printDocTopicprobs( TopicalNGrams m, String path, String purpose, String specify )
	{
		try
		{
			PrintWriter out = new PrintWriter( new File( path + purpose + "/DocTopic-NGram-" + purpose + "-" + specify + ".txt" ) );
			m.printDocumentTopics( out, 0.0, -1 );
			out.close();
		}
		catch ( IOException e )
		{
			e.printStackTrace();
		}
	}

	/**
	 * Returns a string of topics and proportions
	 */
	public String[] getStringDocumentTopicIndex( TopicalNGrams m, double threshold, int max, boolean weight )
	{
		String[] document = m.documentTopics( threshold, max, weight ).split( "\n" );
		return document;
	}

	// returns a List of Strings with each element a topic followed by its bag
	// of ngrams
	public List<String> getListDocumentTopicIndex( TopicalNGrams m, double threshold, int max, boolean weight )
	{
		String[] document = m.documentTopics( threshold, max, weight ).split( "\n" );
		List<String> topics = new ArrayList<String>();
		for ( String d : document )
			topics.add( d );
		return topics;
	}

	// returns the list of all the topic proportions for all the documents (they
	// are not ordered so it can serve as an input to document similarity
	public HashMap<String, List<Double>> getDoumentTopicProportion()
	{
		HashMap<String, List<Double>> h = new HashMap<String, List<Double>>();
		h = tng.documentAllTopicsasMap();
		return h;
	}

	// returns the list of all the topic proportions for all the documents (they
	// are not ordered so it can serve as an input to document similarity
	public HashMap<String, List<Double>> getDoumentTopicProportion( TopicalNGrams model )
	{
		HashMap<String, List<Double>> h = new HashMap<String, List<Double>>();
		h = model.documentAllTopicsasMap();
		return h;
	}

	// Method used for topic composition on Entity Level
	// if unigram is true then we return topics as unigrams otherwise we go for
	// ngrams
	public List<String> getTopicProportionEntityLevel( TopicalNGrams model, boolean unigram, int nwords, boolean weight )
	{
		List<String> topiconEntity = new ArrayList<String>();
		HashMap<String, List<Double>> unorderedDistributions = getDoumentTopicProportion( model );
		String[] topics;
		// decide on having the unigrams or ngrams
		if ( unigram )
		{
			topics = getStringTopicsUnigrams( model, nwords, weight );
		}
		else
		{
			topics = getStringTopicsNgrams( model, nwords, weight );
		}

		double[][] overallTopics = new double[unorderedDistributions.size()][unorderedDistributions.get( unorderedDistributions.keySet().toArray()[0] ).size()];

		int i = 0;
		// get the topics of publications
		for ( Entry<String, List<Double>> distributions : unorderedDistributions.entrySet() )
		{
			overallTopics[i] = getdouble( distributions.getValue() );
			i++;
		}

		// calculate the average for each topic distribution overall
		// publications of the author (entity)
		int j = 0;
		double[] overallavg = new double[overallTopics[0].length];
		while ( j < overallTopics[0].length )
		{
			for ( int k = 0; k < overallTopics.length; k++ )
			{
				overallavg[j] += overallTopics[k][j];
			}
			overallavg[j] /= overallTopics.length;
			topiconEntity.add( topics[j] + "_-_" + overallavg[j] );
			j++;
		}
		return topiconEntity;
	}

	// returns an array of Strings with each element a topic followed by its bag
	// of unigrams
	public String[] getStringTopicsUnigrams( TopicalNGrams m, int nwords, boolean weight )
	{

		String[] topics = m.printUnigrams( nwords, weight ).split( "\n" );

		return topics;
	}

	// returns list of strings of topics where each element is topic followed by
	// its bag of unigrams
	public List<String> getListTopicsUnigrams( TopicalNGrams m, int nwords, boolean weight )
	{

		List<String> listtopics = new ArrayList<String>();
		String[] topics = m.printUnigrams( nwords, weight ).split( "\n" );
		for ( String topic : topics )
		{
			listtopics.add( topic );
		}
		return listtopics;
	}

	// returns an array of Strings with each element a topic followed by its bag
	// of Ngrams
	public String[] getStringTopicsNgrams( TopicalNGrams m, int nwords, boolean weight )
	{
		String[] topics = m.printNgrams( nwords, weight ).split( "\n" );
		return topics;
	}

	// returns list of strings of topics where each element is topic followed by
	// its bag of unigrams
	public List<String> getListTopicsNgrams( TopicalNGrams m, int nwords, boolean weight )
	{
		List<String> listtopics = new ArrayList<String>();
		String[] topics = m.printNgrams( nwords, weight ).split( "\n" );
		for ( String topic : topics )
		{
			listtopics.add( topic );
		}
		return listtopics;
	}

	// gets some random files from path/purpose and pastes them on
	// path/purpose/specify specify = Trainer
	/**
	 * Not used method
	 */
	public void getRandomTrainerFiles( String path, String purpose )
	{
		int count = 20;
		String[] trainer = new File( path + purpose + "/" + purpose ).list();
		while ( count != 0 )
		{
			int random = new Random().nextInt( trainer.length - 1 );
			try
			{
				Files.copy( new File( path + purpose + "/" + purpose, trainer[random] ).toPath(), new File( path + purpose + "/Trainer", trainer[random] ).toPath(), StandardCopyOption.REPLACE_EXISTING );// Here
																																																			// can
																																																			// be
																																																			// also
																																																			// used
																																																			// ATOMIC_MOVE
			}
			catch ( IOException e )
			{
				e.printStackTrace();
			}
			count--;
		}
	}

	// Returns a map <DocumentID, Top Ngrams Topic Assigned to it> which shows
	// the topic assigned to a specific document with given ID
	// When calling max = -1, threshold = 0.05,
	public HashMap<String, List<String>> getTopicNgramsDocument( TopicalNGrams model, int docID, int max, double threshold, int numTopics, int numWords, boolean weight )
	{
		LinkedHashMap<String, List<String>> h = new LinkedHashMap<String, List<String>>();
		List<String> topdoc = new ArrayList<String>();// getListDocumentTopic(m,threshold,max,weight);
		List<String> topics = getListTopicsNgrams( model, numWords, false );
		int docLen;
		double topicDist[] = new double[model.numTopics];
		if ( docID != -1 )
		{
			docLen = model.topics[docID].length;
			for ( int ti = 0; ti < numTopics; ti++ )
				topicDist[ti] = ( ( (float) model.docTopicCounts[docID][ti] ) / docLen );
			if ( max < 0 )
				max = numTopics;
			for ( int tp = 0; tp < max; tp++ )
			{
				double maxvalue = 0;
				int maxindex = -1;
				for ( int ti = 0; ti < numTopics; ti++ )
					if ( topicDist[ti] > maxvalue )
					{
						maxvalue = topicDist[ti];
						maxindex = ti;
					}
				if ( maxindex == -1 || topicDist[maxindex] < threshold )
					break;
				if ( weight )
				{
					// (maxindex+" "+topicDist[maxindex]+" ");

					topdoc.add( topics.get( maxindex ) + " _-_ " + topicDist[maxindex] );
				}
				else
				{
					topdoc.add( topics.get( maxindex ) );
				}
				topicDist[maxindex] = 0;

			}
			// Windows
			h.put( model.ilist.get( docID ).getSource().toString().replace( "\\", ";" ).split( ";" )[6].replace( ".txt", "" ), topdoc );

			// Mac
			// h.put( model.ilist.get( docID ).getSource().toString().replace(
			// "/",
			// ";" ).split( ";" )[6].replace( ".txt", "" ), topdoc );
		}
		return h;
	}

	// Returns a map <DocumentID, Top Ngrams Topic Assigned to it> which shows
	// the topic assigned to a specific document with given ID
	// When calling max = -1, threshold = 0.05,
	public HashMap<String, List<String>> getTopicNgramsDocumentWordWeight( TopicalNGrams model, int docID, int max, double threshold, int numTopics, int numWords, boolean weight )
	{
		LinkedHashMap<String, List<String>> h = new LinkedHashMap<String, List<String>>();
		List<String> topdoc = new ArrayList<String>();// getListDocumentTopic(m,threshold,max,weight);
		List<String> topics = getListTopicsNgrams( model, numWords, true );
		int docLen;
		double topicDist[] = new double[model.numTopics];
		if ( docID > -1 )
		{
			docLen = model.topics[docID].length;
			for ( int ti = 0; ti < numTopics; ti++ )
				topicDist[ti] = ( ( (float) model.docTopicCounts[docID][ti] ) / docLen );
			if ( max < 0 )
				max = numTopics;
			for ( int tp = 0; tp < max; tp++ )
			{
				double maxvalue = 0;
				int maxindex = -1;
				for ( int ti = 0; ti < numTopics; ti++ )
					if ( topicDist[ti] > maxvalue )
					{
						maxvalue = topicDist[ti];
						maxindex = ti;
					}
				if ( maxindex == -1 || topicDist[maxindex] < threshold )
					break;
				if ( weight )
				{
					// (maxindex+" "+topicDist[maxindex]+" ");

					topdoc.add( topics.get( maxindex ) + " _-_ " + topicDist[maxindex] );
				}
				else
				{
					topdoc.add( topics.get( maxindex ) );
				}
				topicDist[maxindex] = 0;

			}
			// Windows
			h.put( model.ilist.get( docID ).getSource().toString().replace( "\\", ";" ).split( ";" )[6].replace( ".txt", "" ), topdoc );

			// Mac
			// h.put( model.ilist.get( docID ).getSource().toString().replace(
			// "/",
			// ";" ).split( ";" )[6].replace( ".txt", "" ), topdoc );

		}

		return h;
	}

	// get the top x toics for a given entitity docID NGrams
	public HashMap<String, List<String>> getTopTopicNgramsDocument( TopicalNGrams model, int docID, int max, double threshold, int numTopics, int numWords, boolean weight )
	{

		HashMap<String, List<String>> temp = getTopicNgramsDocument( model, docID, -1, 0.0, model.numTopics, numWords, true );
		LinkedHashMap<String, List<String>> result = new LinkedHashMap<String, List<String>>();
		List<String> topics = new ArrayList<String>( numTopics );
		if ( numTopics > model.numTopics )
		{
			numTopics = model.numTopics;
		}
		int count = 0;
		for ( Entry<String, List<String>> entry : temp.entrySet() )
		{
			for ( String topic : entry.getValue() )
			{
				if ( count < numTopics )
				{
					topics.add( topic );
					count++;
				}
			}
			result.put( entry.getKey(), topics );
		}

		return result;
	}

	public HashMap<String, List<String>> getTopicNGramsAllDocuments( TopicalNGrams model, int max, double threshold, int numTopics, int numWords, boolean weight )
	{
		HashMap<String, List<String>> alldocumentTopics = new HashMap<>();
		for ( int i = 0; i < model.topics.length; i++ )
		{
			for ( Entry<String, List<String>> topicsOneDoc : getTopicNgramsDocument( model, i, max, threshold, numTopics, numWords, weight ).entrySet() )
			{
				alldocumentTopics.put( topicsOneDoc.getKey(), topicsOneDoc.getValue() );
			}
		}
		return alldocumentTopics;
	}

	// Returns a map <DocumentID, Top Unigrams Topic Assigned to it> which shows
	// the topic assigned to a specific document with given ID
	// When calling max = -1, threshold = 0.05,
	public HashMap<String, List<String>> getTopicUnigramsDocument( TopicalNGrams model, int docID, int max, double threshold, int numTopics, int numWords, boolean weight )
	{
		HashMap<String, List<String>> h = new HashMap<String, List<String>>();
		List<String> topdoc = new ArrayList<String>();// getListDocumentTopic(m,threshold,max,weight);
		List<String> topics = getListTopicsUnigrams( model, numWords, false );

		int docLen;
		double topicDist[] = new double[model.numTopics];
		if ( docID != -1 )
		{
			docLen = model.topics[docID].length;
			for ( int ti = 0; ti < numTopics; ti++ )
				topicDist[ti] = ( ( (float) model.docTopicCounts[docID][ti] ) / docLen );
			if ( max < 0 )
				max = numTopics;
			for ( int tp = 0; tp < max; tp++ )
			{
				double maxvalue = 0;
				int maxindex = -1;
				for ( int ti = 0; ti < numTopics; ti++ )
					if ( topicDist[ti] > maxvalue )
					{
						maxvalue = topicDist[ti];
						maxindex = ti;
					}
				if ( maxindex == -1 || topicDist[maxindex] < threshold )
					break;
				// (maxindex+" "+topicDist[maxindex]+" ");
				if ( weight )
				{
					topdoc.add( topics.get( maxindex ) + " _-_ " + topicDist[maxindex] );
				}
				else
				{
					topdoc.add( topics.get( maxindex ) );
				}
				topicDist[maxindex] = 0;
			}

			// Windows
			h.put( model.ilist.get( docID ).getSource().toString().replace( "\\", ";" ).split( ";" )[6].replace( ".txt", "" ), topdoc );

			// Mac
			// h.put( model.ilist.get( docID ).getSource().toString().replace(
			// "/",
			// ";" ).split( ";" )[6].replace( ".txt", "" ), topdoc );
		}
		return h;
	}

	// Returns a map <DocumentID, Top Unigrams Topic Assigned to it> which shows
	// the topic assigned to a specific document with given ID
	// When calling max = -1, threshold = 0.05,
	public HashMap<String, List<String>> getTopicUnigramsDocumentWordWeigth( TopicalNGrams model, int docID, int max, double threshold, int numTopics, int numWords, boolean weight )
	{
		HashMap<String, List<String>> h = new HashMap<String, List<String>>();
		List<String> topdoc = new ArrayList<String>();// getListDocumentTopic(m,threshold,max,weight);
		List<String> topics = getListTopicsUnigrams( model, numWords, true );

		int docLen;
		double topicDist[] = new double[model.numTopics];
		docLen = model.topics[docID].length;
		for ( int ti = 0; ti < numTopics; ti++ )
			topicDist[ti] = ( ( (float) model.docTopicCounts[docID][ti] ) / docLen );
		if ( max < 0 )
			max = numTopics;
		for ( int tp = 0; tp < max; tp++ )
		{
			double maxvalue = 0;
			int maxindex = -1;
			for ( int ti = 0; ti < numTopics; ti++ )
				if ( topicDist[ti] > maxvalue )
				{
					maxvalue = topicDist[ti];
					maxindex = ti;
				}
			if ( maxindex == -1 || topicDist[maxindex] < threshold )
				break;
			// (maxindex+" "+topicDist[maxindex]+" ");
			if ( weight )
			{
				topdoc.add( topics.get( maxindex ) + " _-_ " + topicDist[maxindex] );
			}
			else
			{
				topdoc.add( topics.get( maxindex ) );
			}
			topicDist[maxindex] = 0;
		}

		// Windows
		h.put( model.ilist.get( docID ).getSource().toString().replace( "\\", ";" ).split( ";" )[6].replace( ".txt", "" ), topdoc );

		// Mac
		// h.put( model.ilist.get( docID ).getSource().toString().replace( "/",
		// ";" ).split( ";" )[6].replace( ".txt", "" ), topdoc );

		return h;
	}

	// get the top x topics for a given entity docID Unigrams
	public HashMap<String, List<String>> getTopTopicUnigramsDocument( TopicalNGrams model, int docID, int max, double threshold, int numTopics, int numWords, boolean weight )
	{

		HashMap<String, List<String>> temp = getTopicUnigramsDocument( model, docID, -1, 0.0, model.numTopics, numWords, true );
		LinkedHashMap<String, List<String>> result = new LinkedHashMap<String, List<String>>();
		List<String> topics = new ArrayList<String>( numTopics );
		if ( numTopics > model.numTopics )
		{
			numTopics = model.numTopics;
		}
		int count = 0;
		for ( Entry<String, List<String>> entry : temp.entrySet() )
		{
			for ( String topic : entry.getValue() )
			{
				if ( count < numTopics )
				{
					topics.add( topic );
					count++;
				}
			}
			result.put( entry.getKey(), topics );
		}

		return result;
	}

	//
	public HashMap<String, List<String>> getTopicUnigramsAllDocuments( TopicalNGrams model, int max, double threshold, int numTopics, int numWords, boolean weight )
	{
		HashMap<String, List<String>> alldocumentTopics = new HashMap<>();
		for ( int i = 0; i < model.topics.length; i++ )
		{
			for ( Entry<String, List<String>> topicsOneDoc : getTopicUnigramsDocument( model, i, max, threshold, numTopics, numWords, weight ).entrySet() )
			{
				alldocumentTopics.put( topicsOneDoc.getKey(), topicsOneDoc.getValue() );
			}
		}
		return alldocumentTopics;
	}

	// method to visualize the topic evolution
	// note this method is only in cases when tng.createModel("Years") = true
	public HashMap<String, List<String>> getEvolutionofTopicOverTime( TopicalNGrams model, int docID, int numWords, boolean weight )
	{
		LinkedHashMap<String, List<String>> topicevolution = new LinkedHashMap<String, List<String>>();
		HashMap<String, List<Double>> doctopicsAll = model.documentTransposedAllTopicsasMap();
		List<String> topics = getListTopicsNgrams( model, numWords, weight );

		for ( Entry<String, List<Double>> topicMapping : doctopicsAll.entrySet() )
		{
			List<String> topdoc = new ArrayList<String>();
			int i = 0;
			for ( Double topic : topicMapping.getValue() )
			{
				// Windows
				topdoc.add( ( model.ilist.get( i ) ).getSource().toString().replace( "\\", ";" ).split( ";" )[6].replace( ".txt", "" ) + "_-_" + topic );

				// Mac
				// topdoc.add( ( model.ilist.get( i )
				// ).getSource().toString().replace( "/", ";" ).split( ";"
				// )[6].replace( ".txt", "" ) + "_-_" + topic );

				i++;
			}
			topicevolution.put( topics.get( Integer.parseInt( topicMapping.getKey().toString() ) ), topdoc );
		}

		return topicevolution;
	}

	// creates a hashmap <String, List<Double>> holding for each document, its
	// distance with other documents (can be used later on for publications and
	// authors)
	// the second parameter is used to specify which similarity measurement will
	// be used among Euclidian(0), Cosine(1), Pearson(2), KL(3)
	public HashMap<String, List<String>> calculateSimilarity( int similarityMeasure, int maxresult )
	{
		HashMap<String, List<String>> distance = new HashMap<String, List<String>>();
		HashMap<String, List<Double>> topicProportions = new HashMap<String, List<Double>>();
		topicProportions = getDoumentTopicProportion();

		similarityMeasures similarity = new similarityMeasures();
		double[][] similarityMatrix = new double[tng.ilist.size()][tng.ilist.size()];

		// create the matrix which will hold the distances of each document from
		// all the other documents
		int k = 0;
		for ( Entry<String, List<Double>> entry : topicProportions.entrySet() )
		{
			int i = 0;
			double[] similarityperElement = new double[tng.ilist.size()];
			for ( Entry<String, List<Double>> entry1 : topicProportions.entrySet() )
			{
				switch ( similarityMeasure ) {
				case 0: {
					similarityperElement[i] = similarity.sqrtEuclidianSimilarity( entry.getValue(), entry1.getValue() );
					break;
				}
				case 1: {
					similarityperElement[i] = similarity.cosineSimilarity( entry.getValue(), entry1.getValue() );
					break;
				}
				case 2: {
					similarityperElement[i] = similarity.pearsonCorrelation( entry.getValue(), entry1.getValue() );
					break;
				}
				case 3: {
					similarityperElement[i] = similarity.divergenceJennsenShannon( getdouble( entry.getValue() ), getdouble( entry1.getValue() ) );
					break;
				}
				}

				i++;
			}
			similarityMatrix[k] = similarityperElement;
			k++;
		}

		if ( maxresult > tng.ilist.size() )
			maxresult = tng.ilist.size();

		// for each document find the top similar elements, take their id, and
		// put them to

		for ( int i = 0; i < similarityMatrix.length; i++ )
		{
			// the list of similar elements
			List<String> similarIds = new ArrayList<String>();
			// number of maximums you will have to find
			int N = 0;

			int index = -1;
			while ( N < maxresult )
			{
				double max = similarityMatrix[i][0];
				// find the maximum in array
				for ( int j = 0; j < similarityMatrix[i].length; j++ )
				{
					if ( similarityMeasure != 3 )
					{
						if ( similarityMatrix[i][j] >= max )
						{
							max = similarityMatrix[i][j];
							index = j;
						}
					}
					else
					{
						if ( similarityMatrix[i][j] <= max )
						{
							max = similarityMatrix[i][j];
							index = j;
						}
					}
				}
				// Windows
				similarIds.add( tng.ilist.get( index ).getSource().toString().replace( "\\", ";" ).split( ";" )[6].replace( ".txt", "" ) + "->" + max );

				// Mac
				// similarIds.add( tng.ilist.get( index
				// ).getSource().toString().replace( "/", ";" ).split( ";"
				// )[6].replace( ".txt", "" ) + "->" + max );

				if ( similarityMeasure != 3 )
				{
					similarityMatrix[i][index] = -1;
				}
				else
				{
					similarityMatrix[i][index] = +2;
				}

				N++;
			}
			// Windows
			distance.put( tng.ilist.get( i ).getSource().toString().replace( "\\", ";" ).split( ";" )[6].replace( ".txt", "" ), similarIds );

			// Mac
			// distance.put( tng.ilist.get( i ).getSource().toString().replace(
			// "/", ";" ).split( ";" )[6].replace( ".txt", "" ), similarIds );

		}
		return distance;
	}

	// creates a hashmap <String, List<Double>> holding for each document, its
	// distance with other documents (can be used later on for publications and
	// authors)
	// the second parameter is used to specify which similarity measurement will
	// be used among Euclidian(0), Cosine(1), Pearson(2), KL(3)
	public HashMap<String, HashMap<String, List<String>>> calculateSimilarityContributors( TopicalNGrams model, int similarityMeasure, int maxresult )
	{
		// result map
		HashMap<String, HashMap<String, List<String>>> resultMap = new HashMap<String, HashMap<String, List<String>>>();
		HashMap<String, List<String>> topicContributor = new LinkedHashMap<String, List<String>>();
		List<String> topics = getListTopicsNgrams( model, 10, false );
		HashMap<String, List<Double>> topicProportions = new HashMap<String, List<Double>>();
		topicProportions = getDoumentTopicProportion( model );

		similarityMeasures similarity = new similarityMeasures();
		double[][] similarityMatrix = new double[model.ilist.size()][model.ilist.size()];

		// keep track of similarity on the topic level
		double[][] trackTopicSimilarity = new double[model.ilist.size()][model.numTopics];

		int m = 0;
		for ( Entry<String, List<Double>> entry : topicProportions.entrySet() )
		{
			int j = 0;
			for ( Entry<String, List<Double>> entry1 : topicProportions.entrySet() )
			{
				while ( j < entry1.getValue().size() )
				{
					trackTopicSimilarity[m][j] = Math.abs( entry.getValue().get( j ) - entry1.getValue().get( j ) );
					j++;
				}
			}
			m++;
		}

		// create the matrix which will hold the distances of each document from
		// all the other documents
		int k = 0;
		for ( Entry<String, List<Double>> entry : topicProportions.entrySet() )
		{
			int i = 0;
			double[] similarityperElement = new double[model.ilist.size()];
			for ( Entry<String, List<Double>> entry1 : topicProportions.entrySet() )
			{
				if ( entry.getKey() != entry1.getKey() )
				{
					switch ( similarityMeasure ) {
					case 0: {
						similarityperElement[i] = similarity.sqrtEuclidianSimilarity( entry.getValue(), entry1.getValue() );
						break;
					}
					case 1: {
						similarityperElement[i] = similarity.cosineSimilarity( entry.getValue(), entry1.getValue() );
						break;
					}
					case 2: {
						similarityperElement[i] = similarity.pearsonCorrelation( entry.getValue(), entry1.getValue() );
						break;
					}
					case 3: {
						similarityperElement[i] = similarity.divergenceJennsenShannon( getdouble( entry.getValue() ), getdouble( entry1.getValue() ) );
						break;
					}
					}
				}

				i++;
			}
			similarityMatrix[k] = similarityperElement;
			k++;
		}

		if ( maxresult > model.ilist.size() )
			maxresult = model.ilist.size();

		// for each document find the top similar elements, take their id

		for ( int i = 0; i < similarityMatrix.length; i++ )
		{
			// number of maximums you will have to find
			int N = 0;

			int index = -1;
			while ( N < maxresult )
			{
				// the list of similar elements
				List<String> similarIds = new ArrayList<String>();
				double max = similarityMatrix[i][0];

				// find the maximum in array
				for ( int j = 0; j < similarityMatrix[i].length; j++ )
				{
					if ( similarityMeasure != 3 )
					{
						if ( similarityMatrix[i][j] >= max )
						{
							max = similarityMatrix[i][j];
							index = j;
						}
					}

					// find the minimum in an array
					else
					{
						if ( similarityMatrix[i][j] <= max )
						{
							max = similarityMatrix[i][j];
							index = j;
						}
					}
				}

				// add the similarity with author level
				similarIds.add( max + "" );

				// add the topic level similarity
				for ( int t = 0; t < topics.size(); t++ )
				{
					similarIds.add( topics.get( t ) + "_-_" + trackTopicSimilarity[i][t] );
				}

				if ( index > -1 )
				{
				// Windows
				topicContributor.put( model.ilist.get( index ).getSource().toString().replace( "\\", ";" ).split( ";" )[6].replace( ".txt", "" ), similarIds );

				// Mac
				// topicContributor.put( model.ilist.get( index
				// ).getSource().toString().replace( "/", ";" ).split( ";"
				// )[6].replace( ".txt", "" ), similarIds );

				if ( similarityMeasure != 3 )
				{
					similarityMatrix[i][index] = -1;
				}
				else
				{
					similarityMatrix[i][index] = +2;
				}
				}

				N++;
			}

			// Windows
			resultMap.put( model.ilist.get( i ).getSource().toString().replace( "\\", ";" ).split( ";" )[6].replace( ".txt", "" ), topicContributor );

			// Mac
			// resultMap.put( model.ilist.get( i
			// ).getSource().toString().replace( "/", ";" ).split( ";"
			// )[6].replace( ".txt", "" ), topicContributor );
		}
		return resultMap;
	}

	public HashMap<String, List<String>> calculateSimilarity( TopicalNGrams model, int similarityMeasure, int maxresult )
	{
		HashMap<String, List<String>> distance = new HashMap<String, List<String>>();
		HashMap<String, List<Double>> topicProportions = new HashMap<String, List<Double>>();
		topicProportions = getDoumentTopicProportion( model );

		similarityMeasures similarity = new similarityMeasures();
		double[][] similarityMatrix = new double[model.ilist.size()][model.ilist.size()];

		// create the matrix which will hold the distances of each document from
		// all the other documents
		int k = 0;
		for ( Entry<String, List<Double>> entry : topicProportions.entrySet() )
		{
			int i = 0;
			double[] similarityperElement = new double[model.ilist.size()];
			for ( Entry<String, List<Double>> entry1 : topicProportions.entrySet() )
			{
				switch ( similarityMeasure ) {
				case 0: {
					similarityperElement[i] = similarity.sqrtEuclidianSimilarity( entry.getValue(), entry1.getValue() );
					break;
				}
				case 1: {
					similarityperElement[i] = similarity.cosineSimilarity( entry.getValue(), entry1.getValue() );
					break;
				}
				case 2: {
					similarityperElement[i] = similarity.pearsonCorrelation( entry.getValue(), entry1.getValue() );
					break;
				}
				case 3: {
					similarityperElement[i] = similarity.divergenceJennsenShannon( getdouble( entry.getValue() ), getdouble( entry1.getValue() ) );
					break;
				}
				}

				i++;
			}
			similarityMatrix[k] = similarityperElement;
			k++;
		}

		if ( maxresult > model.ilist.size() )
			maxresult = model.ilist.size();

		// for each document find the top similar elements, take their id, and
		// put them to the resultMap

		for ( int i = 0; i < similarityMatrix.length; i++ )
		{
			// the list of similar elements
			List<String> similarIds = new ArrayList<String>();
			// number of maximums you will have to find
			int N = 0;

			int index = -1;
			while ( N < maxresult )
			{
				double max = similarityMatrix[i][0];
				// find the maximum in array
				for ( int j = 0; j < similarityMatrix[i].length; j++ )
				{
					if ( similarityMeasure != 3 )
					{
						if ( similarityMatrix[i][j] >= max )
						{
							max = similarityMatrix[i][j];
							index = j;
						}
					}
					else
					{
						if ( similarityMatrix[i][j] <= max )
						{
							max = similarityMatrix[i][j];
							index = j;
						}
					}
				}
				// Windows
				if ( index != -1 )
				{
					similarIds.add( model.ilist.get( index ).getSource().toString().replace( "\\", ";" ).split( ";" )[6].replace( ".txt", "" ) + "->" + max );

				// Mac
				// similarIds.add( model.ilist.get( index
				// ).getSource().toString().replace( "/", ";" ).split( ";"
				// )[6].replace( ".txt", "" ) + "->" + max );

					if ( similarityMeasure != 3 )
					{
						similarityMatrix[i][index] = -1;
					}
					else
					{
						similarityMatrix[i][index] = +2;
					}
				}
				N++;
			}
			// Windows
			distance.put( model.ilist.get( i ).getSource().toString().replace( "\\", ";" ).split( ";" )[6].replace( ".txt", "" ), similarIds );

			// Mac
			// distance.put( model.ilist.get( i
			// ).getSource().toString().replace( "/", ";" ).split( ";"
			// )[6].replace( ".txt", "" ), similarIds );
		}
		return distance;
	}

	// returns the List of top similar entities (author, publication etc)
	public List<String> similarEntities( String id, int maxresult, int similarityMeasure )
	{
		List<String> result = new ArrayList<String>();
		HashMap<String, List<String>> similarMap = new HashMap<String, List<String>>();
		similarMap = calculateSimilarity( similarityMeasure, maxresult );
		if ( similarMap.containsKey( id ) )
		{
			result = similarMap.get( id );
		}
		else
		{
			result.add( "Element not found on our Results!" );
		}
		return result;
	}

	public List<String> similarEntities( TopicalNGrams model, String id, int maxresult, int similarityMeasure )
	{
		List<String> result = new ArrayList<String>();
		HashMap<String, List<String>> similarMap = new HashMap<String, List<String>>();
		similarMap = calculateSimilarity( model, similarityMeasure, maxresult );
		if ( similarMap.containsKey( id ) )
		{
			result = similarMap.get( id );
		}
		else
		{
			result.add( "Element not found on our Results!" );
		}
		return result;
	}

	public HashMap<String, List<String>> getTopicLevelSimilarity( TopicalNGrams model, String id, int maxresult, int simialrityMeasure, int numTopics )
	{
		HashMap<String, List<String>> resultMap = new HashMap<String, List<String>>();

		// will hold the list of authors followed by the % of similarity
		List<String> similarAuthors = new ArrayList<String>();
		similarAuthors = similarEntities( model, id, maxresult, 3 );

		// will hold the author-ids similar to the specified by id, and the list
		// of topics_-_%
		HashMap<String, List<Double>> authorsTopic = new HashMap<String, List<Double>>();
		authorsTopic = getDoumentTopicProportion( model );

		// list of topics string
		List<String> topics = new ArrayList<String>();
		topics = getListTopicsNgrams( model, 10, false );

		// declare an array to track the topic similarity for each author
		double[][] topicSimilarity = new double[maxresult][model.numTopics];

		// calculate the topicSimilarity for each of the similar authors
		int i = 0;
		for ( String similarAuthor : similarAuthors )
		{
			topicSimilarity[i] = new similarityMeasures().manhattanDistance( authorsTopic.get( similarAuthor.split( "->" )[0] ), authorsTopic.get( id ) );
			i++;
		}

		double[] topicID = new double[authorsTopic.get( id ).size()];
		i = 0;
		// get the array of the topic distributions
		for ( double d : authorsTopic.get( id ) )
		{
			topicID[i] = d;
			i++;
		}

		for ( int m = 0; m < similarAuthors.size(); m++ )
		{
			for ( int j = 0; j < topicID.length; j++ )
			{
				topicSimilarity[m][j] = topicSimilarity[m][j] / topicID[j];
			}
		}

		// get the top 10 topics of for each of the authors

		for ( int j = 0; j < topicSimilarity.length; j++ )
		{
			List<String> topicAuthor = new ArrayList<String>();
			int N = 0;
			while ( N < 5 )
			{
				double min = topicSimilarity[j][0];
				int index = -1;
				for ( int k = 0; k < topicSimilarity[j].length; k++ )
				{
					if ( topicSimilarity[j][k] <= min )
					{
						min = topicSimilarity[j][k];
						index = k;
					}
					topicSimilarity[j][index] = +2;
				}
				topicAuthor.add( topics.get( index ) + "_-_" + min );
				N++;
			}
			List<String> topicLevelSimilarity = new ArrayList<String>( topicAuthor.size() );
			// topicLevelSimilarity.add( similarAuthors.get( j ).split( "->"
			// )[1] );
			for ( String entity : topicAuthor )
				topicLevelSimilarity.add( entity );

			resultMap.put( similarAuthors.get( j ), topicLevelSimilarity );
		}

		return resultMap;

	}

	public HashMap<String, List<String>> getTopicLevelSimilarityTopMinDelta( TopicalNGrams model, String id, int maxresult, int similarityMeasure, int numTopics )
	{
		LinkedHashMap<String, List<String>> resultMap = new LinkedHashMap<String, List<String>>();

		// will hold the list of authors followed by the % of similarity
		List<String> similarAuthors = new ArrayList<String>();
		similarAuthors = similarEntities( model, id, maxresult, similarityMeasure );

		// will hold the author-ids similar to the specified by id, and the list
		// of topics_-_%
		HashMap<String, List<Double>> authorsTopic = new HashMap<String, List<Double>>();
		authorsTopic = getDoumentTopicProportion( model );

		// list of topics string
		List<String> topics = new ArrayList<String>( numTopics );
		topics = getListTopicsNgrams( model, 10, false );

		// get the list of topic proportions for author id
		List<Double> topicAuthorId = new ArrayList<Double>();
		topicAuthorId = authorsTopic.get( id );

		int N = 0;
		int[] toptopics = new int[numTopics];
		for ( int i = 0; i < topicAuthorId.size(); i++ )
		{
			if ( topicAuthorId.get( i ) >= 0.015 )
			{
				toptopics[i] = 1;
				N++;
			}
		}

		// calculate the topicSimilarity for each of the similar authors
		for ( String similarAuthor : similarAuthors )
		{

			int Nvs = 0;
			int[] toptopicsVs = new int[numTopics];

			for ( int i = 0; i < numTopics; i++ )
			{
				if ( authorsTopic.get( similarAuthor.split( "->" )[0] ).get( i ) >= 0.015 )
				{
					toptopicsVs[i] = 1;
					Nvs++;
				}
			}

			// compare author with similarAuthors
			List<String> topicAuthor = new ArrayList<String>();
			for ( int j = 0; j < toptopics.length; j++ )
			{
				if ( ( toptopics[j] == 1 ) && ( toptopics[j] == toptopicsVs[j] ) )
				{
					topicAuthor.add( topics.get( j ) + "_-_" + Nvs / N );
				}
			}

			List<String> topicLevelSimilarity = new ArrayList<String>( topicAuthor.size() );
			// topicLevelSimilarity.add( similarAuthors.get( j ).split( "->"
			// )[1] );
			for ( String entity : topicAuthor )
				topicLevelSimilarity.add( entity );

			resultMap.put( similarAuthor.split( "->" )[0] + "->" + Double.parseDouble( similarAuthor.split( "->" )[1] ) * Nvs / N, topicLevelSimilarity );

		}

		return resultMap;

	}

	// topic level similarity
	// result HashMap<entityId, List<String>>
	// List<String>- %similarity with entity, t1 _-_ %, t2 _-_% ...
	public HashMap<String, List<String>> similarEntitiesTopicLevel( TopicalNGrams model, String id, int maxresult, int similarityMeasure )
	{
		HashMap<String, List<String>> result = new HashMap<String, List<String>>();
		HashMap<String, HashMap<String, List<String>>> similarMap = new HashMap<String, HashMap<String, List<String>>>();
		similarMap = calculateSimilarityContributors( model, similarityMeasure, maxresult );
		if ( similarMap.containsKey( id ) )
		{
			result = similarMap.get( id );
		}

		return result;
	}

	// topics which contribute to high similarity
	public LinkedHashMap<String, List<String>> similarTopics( String id, int maxresult )
	{
		LinkedHashMap<String, List<String>> result = new LinkedHashMap<String, List<String>>();

		return result;
	}

	// creates a hashmap <String, List<Double>> holding for each document, its
	// distance with other documents (can be used later on for publications and
	// authors)
	// the second parameter is used to specify which similarity measurement will
	// be used among Euclidian(0), Cosine(1), Pearson(2), KL(3)
	public HashMap<String, LinkedHashMap<String, Double>> calculateSimilarityMap( int similarityMeasure, int maxresult )
	{
		HashMap<String, LinkedHashMap<String, Double>> distance = new HashMap<String, LinkedHashMap<String, Double>>();

		HashMap<String, List<Double>> topicProportions = new HashMap<String, List<Double>>();
		topicProportions = getDoumentTopicProportion();

		similarityMeasures similarity = new similarityMeasures();
		double[][] similarityMatrix = new double[tng.ilist.size()][tng.ilist.size()];

		// create the matrix which will hold the distances of each document from
		// all the other documents
		int k = 0;
		for ( Entry<String, List<Double>> entry : topicProportions.entrySet() )
		{
			int i = 0;
			double[] similarityperElement = new double[tng.ilist.size()];
			for ( Entry<String, List<Double>> entry1 : topicProportions.entrySet() )
			{
				switch ( similarityMeasure ) {
				case 0: {
					similarityperElement[i] = similarity.sqrtEuclidianSimilarity( entry.getValue(), entry1.getValue() );
					break;
				}
				case 1: {
					similarityperElement[i] = similarity.cosineSimilarity( entry.getValue(), entry1.getValue() );
					break;
				}
				case 2: {
					similarityperElement[i] = similarity.pearsonCorrelation( entry.getValue(), entry1.getValue() );
					break;
				}
				case 3: {
					similarityperElement[i] = similarity.divergenceJennsenShannon( getdouble( entry.getValue() ), getdouble( entry1.getValue() ) );
					break;
				}
				}
				i++;
			}
			similarityMatrix[k] = similarityperElement;
			k++;
		}

		// for each document find the top similar elements, take their id, and
		// put them to

		for ( int i = 0; i < similarityMatrix.length; i++ )
		{
			// the list of similar elements
			LinkedHashMap<String, Double> similarIds = new LinkedHashMap<String, Double>();
			// number of maximums you will have to find
			int N = 0;

			int index = -1;
			while ( N < maxresult )
			{
				double max = similarityMatrix[i][0];
				// find the maximum in array
				for ( int j = 0; j < similarityMatrix[i].length; j++ )
				{
					if ( similarityMatrix[i][j] >= max )
					{
						max = similarityMatrix[i][j];
						index = j;
					}
				}

				// Windows
				similarIds.put( tng.ilist.get( index ).getSource().toString().replace( "\\", ";" ).split( ";" )[6].replace( ".txt", "" ), max );

				// Mac
				// similarIds.put( tng.ilist.get( index
				// ).getSource().toString().replace( "/", ";" ).split( ";"
				// )[6].replace( ".txt", "" ), max );

				similarityMatrix[i][index] = -1;
				N++;
			}
			// Windows
			distance.put( tng.ilist.get( i ).getSource().toString().replace( "\\", ";" ).split( ";" )[6].replace( ".txt", "" ), similarIds );

			// Mac
			// distance.put( tng.ilist.get( i ).getSource().toString().replace(
			// "/", ";" ).split( ";" )[6].replace( ".txt", "" ), similarIds );
		}
		return distance;
	}

	// returns the List of top similar entities (author, publication etc)
	public List<String> similarEntitiesMap( String id, int maxresult, int similarityMeasure )
	{
		List<String> result = new ArrayList<String>();
		HashMap<String, LinkedHashMap<String, Double>> similarMap = new HashMap<String, LinkedHashMap<String, Double>>();
		similarMap = calculateSimilarityMap( similarityMeasure, maxresult );
		if ( similarMap.containsKey( id ) )
		{
			for ( Entry<String, Double> value : similarMap.get( id ).entrySet() )
				result.add( value.getKey() );
		}
		else
		{
			result.add( "Element not found in our list" );
		}
		return result;
	}

	// based on similarity results, we recommend most similar
	// publications/authors
	// criteria for this is that a pub. is considered "similar" by at least two
	// similarity measures
	public HashMap<String, List<String>> recommendSimilar( int maxresult )
	{
		HashMap<String, List<String>> result = new HashMap<String, List<String>>();
		similarityMeasures similarity = new similarityMeasures();
		for ( Entry<String, List<Double>> entry : getDoumentTopicProportion().entrySet() )
		{
			List<String> recommend = new ArrayList<String>();
			double[] cos = new double[tng.ilist.size()];
			double[] euc = new double[tng.ilist.size()];
			double[] pea = new double[tng.ilist.size()];
			int i = 0;
			int count = maxresult;
			for ( Entry<String, List<Double>> entry1 : getDoumentTopicProportion().entrySet() )
			{
				cos[i] = similarity.cosineSimilarity( entry.getValue(), entry1.getValue() );
				euc[i] = similarity.sqrtEuclidianSimilarity( entry.getValue(), entry1.getValue() );
				pea[i] = similarity.pearsonCorrelation( entry.getValue(), entry1.getValue() );
				boolean c = false, e = false, p = false;
				if ( cos[i] > 0.5 && cos[i] < 1.0 )
				{
					c = true;
				}
				if ( euc[i] < 0.6 && euc[i] >= 0 )
				{
					e = true;
				}
				if ( pea[i] > 0.5 && pea[i] < 1.0 )
				{
					p = true;
				}

				if ( ( count > 0 ) && ( ( c && e ) || ( e && p ) || ( c && p ) ) )
				{
					recommend.add( entry1.getKey() );
					count--;
				}
				i++;
			}
			result.put( entry.getKey(), recommend );

		}
		return result;
	}

	// returns the List of recommended entities (author publication etc)
	public List<String> recommendedEntity( String id, int maxresult )
	{
		List<String> result = new ArrayList<String>();
		HashMap<String, List<String>> similarMap = new HashMap<String, List<String>>();
		similarMap = recommendSimilar( maxresult );

		if ( similarMap.containsKey( id ) )
		{
			result = similarMap.get( id );
		}
		else
		{
			result.add( "Element not found in our list" );
		}
		return result;
	}

	// documentAllTopicsasMap <String, List<Double>> &&
	// documentAllTopicsasMap<String, List<String>>
	// this method has to match the String (ID) with List of topics and list of
	// topic proportions
	public LinkedHashMap<String, LinkedHashMap<String, Double>> getDocumentTopicDetailMap( TopicalNGrams m, int max, double threshold, int numTopics, int numWords, boolean weight )
	{
		LinkedHashMap<String, LinkedHashMap<String, Double>> topicDetails = new LinkedHashMap<>();
		HashMap<String, List<String>> topicWords = new HashMap<>();
		HashMap<String, List<Double>> topicDistribution = new HashMap<>();
		LinkedHashMap<String, Double> topicWordDistributionMatch = new LinkedHashMap<>();
		// loop over all the documents to populate the two input maps

		topicDistribution = m.documentAllTopicsasMap();
		topicWords = getTopicNGramsAllDocuments( m, max, threshold, numTopics, numWords, weight );
		for ( Entry<String, List<String>> topic : topicWords.entrySet() )
		{
			for ( Entry<String, List<Double>> distribution : topicDistribution.entrySet() )
			{
				for ( int i = 0; i < topic.getValue().size(); i++ )
				{
					if ( topic.getKey() == distribution.getKey() )
					{
						Collections.sort( distribution.getValue() );
						Collections.reverse( distribution.getValue() );
						topicWordDistributionMatch.put( topic.getValue().get( i ), distribution.getValue().get( i ) );
					}
				}
				topicDetails.put( topic.getKey(), topicWordDistributionMatch );
			}
		}

		return topicDetails;
	}

	// this method is used to return an integer which corresponds to the
	// Author/Conference/Years/Publication ID from db
	public int maptoRealDatabaseID( String id, TopicalNGrams model ) throws Exception
	{
		int docID = -1;
		for ( int i = 0; i < model.ilist.size(); i++ )
		{
			// Windows
			String trial = model.ilist.get( i ).getSource().toString().replace( "\\", ";" ).split( ";" )[6].replace( ".txt", "" );

			// Mac
			// String trial = model.ilist.get( i
			// ).getSource().toString().replace( "/", ";" ).split( ";"
			// )[6].replace( ".txt", "" );

			if ( id.equals( trial ) )
			{
				docID = i;
				break;
			}
			else
			{
				continue;
			}
		}
		return docID;
	}

	public double[] TopicInferencer()
	{
		double[] hi = new double[10];
		return hi;
	}

	/*
	 * Convert Double[] to double[]
	 */
	public double[] getdouble( List<Double> array )
	{
		double[] d = new double[array.size()];
		int i = 0;
		for ( Double arr : array )
			d[i++] = arr;
		return d;
	}

	/*
	 * Method called by the controller to create a specific model, use model
	 * generate topics, their proportions, final hash-map
	 */
	public HashMap<String, List<String>> runTopicComposition( String id, String path, String purpose, int numTopics, int maxnumberTopics, int numWords, boolean weight, boolean createmodel, boolean unigram )
	{
		LinkedHashMap<String, List<String>> topicComposition = new LinkedHashMap<String, List<String>>();
		List<String> topicList = new ArrayList<String>();
		TopicalNGrams model;

		// check if you need to create a model based on trained data or new ones
		if ( createmodel )
		{
			model = createModel( path, purpose, id, numTopics );
		}
		else
		{
			model = useTrainedData( path, purpose, id, numTopics );
		}

		// pick to run the Ngrams or Unigrams
		topicList = getTopicProportionEntityLevel( model, unigram, numWords, weight );
		double[] distributions = new double[topicList.size()];
		int i = 0;

		List<String> addtopics = new ArrayList<String>();
		// calculate the maxnumber of top topics
		for ( String proportion : topicList )
		{
			distributions[i] = Double.parseDouble( proportion.split( "_-_" )[1] );

			i++;
		}

		// find the top N maxtopics
		if ( maxnumberTopics > model.numTopics )
		{
			maxnumberTopics = model.numTopics;
		}

		int N = 0;
		int index = -1;
		while ( N < maxnumberTopics )
		{
			double max = distributions[0];
			// find the maximum in array
			for ( int j = 0; j < distributions.length; j++ )
			{
				if ( distributions[j] >= max )
				{
					max = distributions[j];
					index = j;
				}
			}
			addtopics.add( topicList.get( index ).split( "_-_" )[0] + "_-_" + max );
			distributions[index] = -1;
			N++;
		}

		topicComposition.put( id, addtopics );
		return topicComposition;
	}

	// run Topic Modeling on overall level. Get the topic weight for each of the
	public HashMap<String, List<String>> runTopicCompositionHighLevel( String id, String path, String purpose, int numTopics, int maxnumberTopics, int numWords, boolean weight, boolean createmodel, boolean unigram ) throws Exception
	{
		LinkedHashMap<String, List<String>> topicComposition = new LinkedHashMap<String, List<String>>();
		List<String> topicList = new ArrayList<String>();
		TopicalNGrams model;

		// check if you need to create a model based on trained data or new ones
		if ( createmodel )
		{
			model = createModel( path, purpose, "", numTopics );
		}
		else
		{
			model = useTrainedData( path, purpose, "", maxnumberTopics );
		}

		// pick to run the Ngrams or Unigrams
		if ( unigram )
		{
			topicList = getTopicUnigramsDocument( model, maptoRealDatabaseID( id, model ), 10, 0.0, model.numTopics, numWords, weight ).get( id );
		}
		else
		{
			topicList = getTopicNgramsDocument( model, maptoRealDatabaseID( id, model ), 10, 0.0, model.numTopics, numWords, weight ).get( id );
		}

		topicComposition.put( id, topicList );
		return topicComposition;
	}

	// method used to calculate the similarity measure between authors
	// the general view is taken into consideration for this calculation instead
	// of individual one
	public List<String> runSimilarEntities( String id, String path, String purpose, int numTopics, int maxResult, int similarityMeasure, boolean createModel )
	{
		List<String> similarEntities = new ArrayList<String>();
		TopicalNGrams model;

		// check if we need to create an existing model or not
		if ( createModel )
		{
			// create a new model
			model = createModelRevised( path, purpose, "", numTopics );
		}
		else
		{
			model = useTrainedDataRevised( path, purpose, "", numTopics );
		}

		// call the method to calculate the similarities
		similarEntities = similarEntities( model, id, maxResult, similarityMeasure );

		return similarEntities;
	}

	// method used to calculate the similarity measure between authors
	// the general view is taken into consideration for this calculation instead
	// of individual one
	public HashMap<String, List<String>> runSimilarEntitiesTopicLevel( String id, String path, String purpose, int numTopics, int maxResult, int similarityMeasure, boolean createModel )
	{
		HashMap<String, List<String>> similarEntities = new HashMap<String, List<String>>();
		TopicalNGrams model;

		// check if we need to create an existing model or not
		if ( createModel )
		{
			// create a new model
			model = createModel( path, purpose, "", numTopics );
		}
		else
		{
			model = useTrainedData( path, purpose, "", numTopics );
		}

		// call the method to calculate the similarities
		similarEntities = getTopicLevelSimilarityTopMinDelta( model, id, maxResult, similarityMeasure, numTopics );

		return similarEntities;
	}

	// method used to calculate the topic Evolution based based on entity level
	public LinkedHashMap<String, List<String>> runDiscreteTopicEvolution( String path, String purpose, String id, int numWords, int numTopics, int maxnumTopics, boolean createmodel, boolean weight, boolean unigrams )
	{
		LinkedHashMap<String, List<String>> topicEvolution = new LinkedHashMap<String, List<String>>();
		List<String> topics;
		// create model based on the per year documents of each author
		TopicalNGrams model;
		if ( createmodel )
		{
			model = createModel( path, purpose, id, numTopics );
		}
		else
		{
			model = useTrainedData( path, purpose, id, numTopics );
		}

		// get the model and provide the transposed matrix for years
		HashMap<String, List<Double>> doctopicsAll = model.documentTransposedAllTopicsasMap();

		if ( unigrams )
		{
			topics = getListTopicsUnigrams( model, numWords, weight );
		}
		else
		{
			topics = getListTopicsNgrams( model, numWords, weight );
		}

		for ( Entry<String, List<Double>> topicMapping : doctopicsAll.entrySet() )
		{
			List<String> topdoc = new ArrayList<String>();
			int i = 0;
			for ( Double topic : topicMapping.getValue() )
			{
				// Windows
				topdoc.add( ( model.ilist.get( i ) ).getSource().toString().replace( "\\", ";" ).split( ";" )[6].replace( ".txt", "" ) + "_-_" + topic );

				// Mac
				// topdoc.add( ( model.ilist.get( i )
				// ).getSource().toString().replace( "/", ";" ).split( ";"
				// )[6].replace( ".txt", "" ) + "_-_" + topic );

				i++;
			}
			topicEvolution.put( topics.get( Integer.parseInt( topicMapping.getKey().toString() ) ), topdoc );
		}

		return topicEvolution;
	}

	// method used to get the topic composition as weighted words/phrases Tag
	// Cloud
	public HashMap<String, Double> runweightedTopicComposition( String path, String purpose, String id, int numTopics, int maxnumTopics, int numWords, boolean createmodel, boolean unigrams )
	{
		LinkedHashMap<String, Double> weightedWords = new LinkedHashMap<String, Double>();
		List<String> topicProportions = new ArrayList<String>();
		TopicalNGrams model;

		// check if needed to create a new model or use an existing one
		if ( createmodel )
		{
			model = createModel( path, purpose, id, numTopics );
		}
		else
		{
			model = useTrainedData( path, purpose, id, numTopics );
		}

		// use the model to get the list of topics and the weighted
		topicProportions = getTopicProportionEntityLevel( model, unigrams, numWords, true );

		// do the split _-_ to get the topic weight and topic
		// do the split of the topic " " to get the words and weight
		// multiply the topic weight and word weight
		// double[] proportion = new double[topicProportions.size()];
		// int i = 0;
		// for ( String topic : topicProportions )
		// {
		// proportion[i] = Double.parseDouble( topic.split( "_-_" )[1] );
		// i++;
		// }

		for ( String topic : topicProportions )
		{

			String topicWordsWeights = topic.split( "_-_" )[0];

			for ( String wordweight : topicWordsWeights.split( " " ) )
			{
				// differentiate between unigrams and n-grams
				if ( unigrams )
				{
					if ( !wordweight.split( "-" )[0].isEmpty() )
						weightedWords.put( wordweight.split( "-" )[0], (double) ( Double.parseDouble( wordweight.split( "-" )[1] ) * Math.pow( Double.parseDouble( topic.split( "_-_" )[1] ), 2 ) ) * 1000000 );
				}
				else
				{
					if ( !wordweight.split( "-" )[0].isEmpty() )
						weightedWords.put( wordweight.split( "-" )[0].replace( "_", " " ), (double) ( Double.parseDouble( wordweight.split( "-" )[1] ) * Math.pow( Double.parseDouble( topic.split( "_-_" )[1] ), 2 ) ) * 1000000 );
				}
			}
		}

		weightedWords = (LinkedHashMap<String, Double>) sortByValue( weightedWords );
		// get the top 10 * numTopics words
		int N = 0;
		if ( numTopics < 6 )
		{
		}
		else
		{
		}

		LinkedHashMap<String, Double> results = new LinkedHashMap<String, Double>();
		for ( Entry<String, Double> element : weightedWords.entrySet() )
		{
			results.put( element.getKey(), element.getValue() );
			N++;

			if ( N >= 30 )
			{
				break;
			}
		}
		return results;
	}

	// method used to get the topic composition as weighted words/phrases Tag
	// Cloud
	public HashMap<String, Double> runweightedTopicCompositionforPublications( String path, String purpose, String id, List<String> authorIds, int numTopics, int maxnumTopics, int numWords, boolean createmodel, boolean unigrams )
	{
		LinkedHashMap<String, Double> weightedWords = new LinkedHashMap<String, Double>();
		List<String> topicProportions = new ArrayList<String>();

		// use the model to get the list of topics and the weighted
		topicProportions = runTopicsFromListofEntities( path, purpose, authorIds, id, numTopics, maxnumTopics, numWords, createmodel, unigrams, true ).get( id );

		for ( String topic : topicProportions )
		{

			String topicWordsWeights = topic.split( "_-_" )[0];
			// add the weight factor for each element

			for ( String wordweight : topicWordsWeights.split( " " ) )
			{
				// differentiate between unigrams and n-grams
				if ( unigrams )
				{
					if ( !wordweight.split( "-" )[0].isEmpty() )
						weightedWords.put( wordweight.split( "-" )[0], (double) ( Double.parseDouble( wordweight.split( "-" )[1] ) * Math.pow( Double.parseDouble( topic.split( "_-_" )[1] ), 2 ) ) * 100000 );
				}
				else
				{
					if ( !wordweight.split( "-" )[0].isEmpty() )
						weightedWords.put( wordweight.split( "-" )[0].replace( "_", " " ), (double) ( Double.parseDouble( wordweight.split( "-" )[1] ) * Math.pow( Double.parseDouble( topic.split( "_-_" )[1] ), 2 ) ) * 100000 );
				}
			}
		}
		weightedWords = (LinkedHashMap<String, Double>) sortByValue( weightedWords );
		int N = 0;
		LinkedHashMap<String, Double> results = new LinkedHashMap<String, Double>();
		for ( Entry<String, Double> element : weightedWords.entrySet() )
		{
			if ( N < 10 )
			{
				results.put( element.getKey(), element.getValue() );
				N++;
			}
			else
			{
				break;
			}
		}

		return results;
	}

	// get the list of topics followed by their proportions from a list of
	// authorIds
	public HashMap<String, List<String>> runTopicsFromListofEntities( String path, String purpose, List<String> authorIds, String publicationId, int numTopics, int maxnumTopics, int numWords, boolean createmodel, boolean unigrams, boolean wordweight )
	{

		HashMap<String, List<String>> topics = new HashMap<String, List<String>>();
		List<String> mergedResults = new ArrayList<String>();
		TopicalNGrams model;
		// create model for each of the authors that we have
		for ( String author : authorIds )
		{

			// decide to create model or not
			if ( createmodel )
			{
				model = createModel( path, purpose, author, numTopics );
			}
			else
			{
				model = useTrainedData( path, purpose, author, numTopics );
			}

			try
			{
				// for the created model get the specific topic distribution for
				// the given publicationId
				if ( unigrams )
				{
					if ( wordweight )
					{
						List<String> temporalResult = new ArrayList<String>();
						for ( Entry<String, List<String>> element : getTopicUnigramsDocumentWordWeigth( model, maptoRealDatabaseID( publicationId, model ), -1, 0.0, model.numTopics, numWords, true ).entrySet() )
						{
							List<String> partialresult = new ArrayList<String>( element.getValue() );
							temporalResult.addAll( partialresult );
						}
						mergedResults.addAll( temporalResult );
						// topics.put( publicationId, temporalResult );
					}
					else
					{
						List<String> temporalResult = new ArrayList<String>();
						for ( Entry<String, List<String>> element : getTopicUnigramsDocument( model, maptoRealDatabaseID( publicationId, model ), -1, 0.0, model.numTopics, numWords, true ).entrySet() )
						{
							List<String> partialresult = new ArrayList<String>( element.getValue() );
							temporalResult.addAll( partialresult );
						}
						mergedResults.addAll( temporalResult );
						// topics.put( author, temporalResult );
					}

					topics.put( publicationId, mergedResults );
				}
				else
				{
					if ( wordweight )
					{
						List<String> temporalResult = new ArrayList<String>();
						for ( Entry<String, List<String>> element : getTopicNgramsDocumentWordWeight( model, maptoRealDatabaseID( publicationId, model ), -1, 0.0, model.numTopics, numWords, true ).entrySet() )
						{
							List<String> partialresult = new ArrayList<String>( element.getValue() );
							temporalResult.addAll( partialresult );
						}
						mergedResults.addAll( temporalResult );
						// topics.put( publicationId, temporalResult );
					}
					else
					{
						List<String> temporalResult = new ArrayList<String>();
						for ( Entry<String, List<String>> element : getTopicNgramsDocument( model, maptoRealDatabaseID( publicationId, model ), -1, 0.0, model.numTopics, numWords, true ).entrySet() )
						{
							List<String> partialresult = new ArrayList<String>( element.getValue() );
							temporalResult.addAll( partialresult );
						}
						mergedResults.addAll( temporalResult );
					}
					topics.put( publicationId, mergedResults );
				}
			}
			catch ( Exception e )
			{
				e.printStackTrace();
			}
		}
		return topics;
	}

	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue( Map<K, V> map )
	{
		List<Map.Entry<K, V>> list = new LinkedList<>( map.entrySet() );
		Collections.sort( list, new Comparator<Map.Entry<K, V>>()
		{
			@Override
			public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 )
			{
				return ( o2.getValue() ).compareTo( o1.getValue() );
			}
		} );
		Map<K, V> result = new LinkedHashMap<>();
		for ( Map.Entry<K, V> entry : list )
		{
			result.put( entry.getKey(), entry.getValue() );
		}
		return result;
	}

	public Boolean dateCheckCriteria( String path, String purpose, String Id ) throws IOException
	{
		fileDateCheck create = new fileDateCheck();

		return create.createNewModel( path + purpose + "/MALLET/" + Id + ".mallet" );
	}

	// method used to calculate the similarity measure between authors
	// the general view is taken into consideration for this calculation instead
	// of individual one
	public HashMap<String, List<String>> runSimilarEntitiesContributorsTopicLevel( String id, String path, String purpose, int numTopics, int maxResult, int similarityMeasure, boolean createModel )
	{
		HashMap<String, List<String>> similarEntities = new HashMap<String, List<String>>();
		TopicalNGrams model;

		// check if we need to create an existing model or not
		if ( createModel )
		{
			// create a new model
			model = createModelRevised( path, purpose, "", numTopics );
		}
		else
		{
			model = useTrainedDataRevised( path, purpose, "", numTopics );
		}

		// call the method to calculate the similarities
		similarEntities = similarEntitiesTopicLevel( model, id, maxResult, similarityMeasure );

		return similarEntities;
	}
}