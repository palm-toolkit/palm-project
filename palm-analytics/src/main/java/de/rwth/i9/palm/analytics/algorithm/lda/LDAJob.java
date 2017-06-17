package de.rwth.i9.palm.analytics.algorithm.lda;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import org.junit.Test;

import cc.mallet.topics.MarginalProbEstimator;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.topics.TopicInferencer;
import cc.mallet.types.IDSorter;
import cc.mallet.types.InstanceList;
import cc.mallet.types.LabelSequence;
import de.rwth.i9.palm.analytics.util.TopicMiningConstants;

public class LDAJob implements Lda
{
	@Test
	public void test() throws Exception
	{

		try
		{
			// // create the respective models for authors, publications
			ParallelTopicModel authors = createModel( TopicMiningConstants.USER_DESKTOP_PATH, "Authors", "Authors", 50, 10 );
			//
			// // print the Top x Words for each Topic
			// printTopWords( authors, 10, path, "Authors", "Trainer" );
			// // printTopWords(publications, 10, path, "Publications",
			// "Trainer");
			// List<String> author = getListTopics( authors, 10 );
			//
			// for ( int i = 0; i < authors.data.size(); i++ )
			// {
			// for ( Entry<String, String> entry : getTopicDocument( authors, i,
			// -1, 0.00, authors.getNumTopics(), 11 ).entrySet() )
			// {
			// System.out.println( ( entry.getKey().split( "/" )[7]
			// ).replaceAll( ".txt", "" ) + " ->-> " +
			// entry.getValue().replaceAll( "[^a-z\\s]", "" ) );
			// }
			// }
			//
			// for ( int i = 0; i < authors.data.size(); i++ )
			// {
			// for ( Entry<String, Double> entry : getTopicWeight( authors, i,
			// -1, 0.00, authors.getNumTopics(), 11 ).entrySet() )
			// {
			// System.out.println( ( entry.getKey().split( "/" )[7]
			// ).replaceAll( ".txt", "" ) + " ->-> " + entry.getValue() );
			// }
			// }

			// For the visualization (update it later on)

			// System.out.println();
			// System.out.println( "_________________________________" );
			// System.out.println( "" );
			// for ( int i = 0; i < authors.data.size(); i++ )
			// {
			// for ( Entry<String, List<String>> entry : getAllDocumentTopics(
			// authors, i, -1, 0.005, authors.getNumTopics() ).entrySet() )
			// {
			// System.out.println( ( entry.getKey().split( "/" )[7]
			// ).replaceAll( ".txt", "" ) + " ->-> " );
			// for ( String a : entry.getValue() )
			// {
			// System.out.println( author.get( Integer.parseInt( a.split( "-"
			// )[0] ) ) );
			// }
			// }
			// }
			// // print the Topic proportions for each document
			// printDocTopicprobs( authors, path, "Authors", "Trainer" );
			//// printDocTopicprobs(publications, path,
			// "Publications","Trainer");
			//
			// // Alternative way to get the topics without using the files
			// TopWords per topic"
			// List<String> topics = new ArrayList<>();
			// topics.add( authors.displayTopWords( 10, true ) );

			for ( double d : TopicInferencer( authors, TopicMiningConstants.USER_DESKTOP_PATH, "Authors", "Authors", "c442983a-0099-4d6d-89b1-6cfc57fa6138" ) )
			{
				System.out.println( d );
			}

		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
	}

	// filename specifies the list of files that will come from db
	// purpose specifies the instances of analysis (author, publication,
	// conference, yearly)
	// returns the list of instances that will serve as input to LDA for
	// analysis

	// purpose - {Authors, Publications, Conferences, Years}
	// specify - {Trainer, Infer}
	public InstanceList getInstanceData( String path, String purpose, String specify )
	{

		// Get the data from a directory and convert it into mallet format
		// Use importData Class to make input traverse through the following
		// pipes
		// 1. Input2CharSequence
		// 2. CharSequence2TokenSequence
		// 3. TokenSequenceLowercase
		// 4. TokenSequenceRemoveStopwords
		// 5. TokenSequence2FeatureSequence
		importData importer = new importData();
		InstanceList instances = importer.readDirectory( new File( path + "/" + purpose + "/" + purpose ) ); // +
																												// "/"+
																												// specify
																												// ));
		instances.save( new File( path + purpose + "/" + purpose + "-" + specify + "-LDA" + ".mallet" ) );
		InstanceList training = InstanceList.load( new File( path + purpose + "/" + purpose + "-" + specify + "-LDA" + ".mallet" ) );

		return training;
	}

	// set the number of topics
	public ParallelTopicModel setNumberTopics( int numTopics )
	{
		if ( numTopics <= 0 )
		{
			System.out.print( "Wrong input" );
			return null;
		}
		else
		{
			return new ParallelTopicModel( numTopics );
		}
	}

	// return the default LDA number of topics
	public int getNumTopics( ParallelTopicModel m )
	{
		return m.getNumTopics();
	}

	// create a model of reference in a training corpora
	public ParallelTopicModel createModel( String path, String purpose, String specify, int numTopics, int numWords )
	{

		ParallelTopicModel lda = new ParallelTopicModel( numTopics, 50.0, 0.01 );
		lda.setNumThreads( 1 );
		lda.optimizeInterval = 20;
		lda.printLogLikelihood = true;
		lda.setTopicDisplay( numTopics, numWords + 1 );
		InstanceList trained = getInstanceData( path, purpose, specify );
		lda.addInstances( trained );
		try
		{
			lda.estimate();
		}
		catch ( IOException e )
		{
			e.printStackTrace();
		}
		return lda;
	}

	// purpose - {Authors, Publications, Conferences, Years} ; specify -
	// {Trainer, Infer}
	public void printTopWords( ParallelTopicModel m, int nwords, String path, String purpose, String specify )
	{
		try
		{
			m.printTopWords( new File( path + purpose + "/TopWords-" + purpose + "-" + specify + ".txt" ), nwords + 1, false );
		}
		catch ( IOException e )
		{
			e.printStackTrace();
		}
	}

	// purpose - {Authors, Publications, Conferences, Years} ; specify -
	// {Trainer, Infer}
	public void printDocTopicprobs( ParallelTopicModel m, String path, String purpose, String specify )
	{
		try
		{
			m.printDocumentTopics( new File( path + purpose + "/DocTopic-" + purpose + "-" + specify + ".txt" ) );
		}
		catch ( IOException e )
		{
			e.printStackTrace();
		}
	}

	// returns another version of topics (not as a File) but as vector of
	// strings
	public String[] getStringTopics( ParallelTopicModel m, int nwords )
	{

		String[] topics = m.displayTopWords( nwords, false ).split( "\n" );

		return topics;
	}

	// returns list of strings of topics (alternative to another method
	// implemented above
	public List<String> getListTopics( ParallelTopicModel m, int nwords )
	{

		List<String> listtopics = new ArrayList<String>();
		String[] topics = m.displayTopWords( nwords, false ).split( "\n" );
		for ( String topic : topics )
		{
			listtopics.add( topic );
		}
		return listtopics;
	}

	// Some minor problems need to be fixed here
	public void evaluateModel( ParallelTopicModel m, String path, String purpose, String specify )
	{

		InstanceList training = InstanceList.load( new File( path + purpose + "/" + purpose + "-" + specify + ".mallet" ) );
		MarginalProbEstimator evaluator = m.getProbEstimator();
		double logLikelyhood = evaluator.evaluateLeftToRight( training, 10, false, null );
		System.out.println( logLikelyhood );
	}

	// this methods maps the best topic with the suitable document
	// used only for initial version - files as output
	// public void DocTopicMapper(String path, String purpose, String specify)
	// throws IOException{
	// @SuppressWarnings( "resource" )
	// BufferedReader docs = new BufferedReader(new FileReader(path + purpose +
	// "/DocTopic-" + purpose + "-" + specify + ".txt"));
	// @SuppressWarnings( "resource" )
	// BufferedReader tops = new BufferedReader(new FileReader(path + purpose +
	// "/TopWords-" + purpose + "-" + specify + ".txt"));
	// String document, topic;
	//
	// // get Line by line the bag of words for each of the topics
	// List<String> listtopic = new ArrayList<String>();
	// while(( topic = tops.readLine())!=null){
	// listtopic.add( topic );
	// }
	//
	// // get Line by line the topic distribution for each of the documents
	// List<String> listdoc = new ArrayList<String>();
	// while((document=docs.readLine())!=null){
	// listdoc.add( document );
	// }
	//
	// // map documents to topic's bag-of-words
	// for (int i=1; i<listdoc.size();i++){
	// int numTopics = 50;
	// String[] docsplit = listdoc.get( i ).split( "\\s+" );
	// for(int j =0;j<numTopics;j++){
	// if (listtopic.get( j ).startsWith( docsplit[2]) == true){
	// System.out.println(docsplit[1] +" -> " + listtopic.get( j ).substring( 10
	// ));
	// break;
	//
	// }
	// }
	// }
	// }

	// This is used to assign a topic distribution to new Documents
	public double[] TopicInferencer( ParallelTopicModel trainedModel, String path, String purpose, String specify, String docID ) throws IOException
	{
		double[] topicProbs;
		TopicInferencer infere = trainedModel.getInferencer();
		InstanceList instance = getInstanceData( path, purpose, specify );
		topicProbs = infere.getSampledDistribution( instance.get( 0 ), 100, 10, 10 );

		return topicProbs;
	}

	// Returns a map <DocumentID, Top Topic Assigned to it> which shows the
	// topic assigned to a specific document with given ID
	// When calling max = -1, threshold = 0.05,
	public HashMap<String, String> getTopicDocument( ParallelTopicModel m, int docID, int max, double threshold, int numTopics, int numWords )
	{

		HashMap<String, String> h = new HashMap<String, String>();
		int[] topicCounts = new int[numTopics];
		String[] topicNames;
		int docLen = 0;
		int topicID = 0;
		IDSorter[] sortedTopics = new IDSorter[numTopics];
		for ( int topic = 0; topic < numTopics; topic++ )
		{
			// Initialize the sorters with dummy values
			sortedTopics[topic] = new IDSorter( topic, topic );
		}

		if ( max < 0 || max > numTopics )
		{
			max = numTopics;
		}

		LabelSequence topicSequence = (LabelSequence) m.data.get( docID ).topicSequence;
		int[] currentDocTopics = topicSequence.getFeatures();

		docLen = currentDocTopics.length;

		// Count up the tokens
		for ( int token = 0; token < docLen; token++ )
		{
			topicCounts[currentDocTopics[token]]++;
		}

		// And normalize
		for ( int topic = 0; topic < numTopics; topic++ )
		{
			sortedTopics[topic].set( topic, ( m.alpha[topic] + topicCounts[topic] ) / ( docLen + m.alphaSum ) );
		}

		Arrays.sort( sortedTopics );

		// m.data.get(docID).instance.getName(); // can be also
		// model.data.get(doc).instance.getID(); or whatever :))))

		for ( int i = 0; i < max; i++ )
			if ( sortedTopics[0].getWeight() > threshold )
			{
				topicID = sortedTopics[0].getID();
			}
		topicNames = getStringTopics( m, numWords );
		h.put( m.data.get( docID ).instance.getName() + "", topicNames[topicID] );
		Arrays.fill( topicCounts, 0 );
		return h;
	}

	// get the weight of the highest probable topic for a specific document
	// (docID)
	public HashMap<String, Double> getTopicWeight( ParallelTopicModel m, int docID, int max, double threshold, int numTopics, int numWords )
	{

		HashMap<String, Double> h = new HashMap<String, Double>();
		int[] topicCounts = new int[numTopics];
		int docLen = 0;
		double topicWeight = 0;
		IDSorter[] sortedTopics = new IDSorter[numTopics];
		for ( int topic = 0; topic < numTopics; topic++ )
		{
			// Initialize the sorters with dummy values
			sortedTopics[topic] = new IDSorter( topic, topic );
		}

		if ( max < 0 || max > numTopics )
		{
			max = numTopics;
		}

		LabelSequence topicSequence = (LabelSequence) m.data.get( docID ).topicSequence;
		int[] currentDocTopics = topicSequence.getFeatures();

		docLen = currentDocTopics.length;

		// Count up the tokens
		for ( int token = 0; token < docLen; token++ )
		{
			topicCounts[currentDocTopics[token]]++;
		}

		// And normalize
		for ( int topic = 0; topic < numTopics; topic++ )
		{
			sortedTopics[topic].set( topic, ( m.alpha[topic] + topicCounts[topic] ) / ( docLen + m.alphaSum ) );
		}

		Arrays.sort( sortedTopics );

		// m.data.get(docID).instance.getName(); // can be also
		// model.data.get(doc).instance.getID(); or whatever :))))

		for ( int i = 0; i < max; i++ )
			if ( sortedTopics[0].getWeight() > threshold )
			{
				topicWeight = sortedTopics[0].getWeight();
			}
		h.put( m.data.get( docID ).instance.getName() + "", topicWeight );// in
																			// case
																			// weight
																			// is
																			// needed
																			// just
																			// add:
																			// topicWeight
		Arrays.fill( topicCounts, 0 );
		return h;
	}

	// produce a Map wish holds <DocumentId, List<TopicWeight, String of Words>>
	public HashMap<String, List<String>> getAllDocumentTopics( ParallelTopicModel m, int docID, int max, double threshold, int numTopics )
	{

		HashMap<String, List<String>> h = new HashMap<String, List<String>>();
		List<String> topics = new ArrayList<String>();
		int[] topicCounts = new int[numTopics];
		int docLen = 0;
		int topicID = 0;
		double topicWeight = 0;
		IDSorter[] sortedTopics = new IDSorter[numTopics];
		for ( int topic = 0; topic < numTopics; topic++ )
		{
			// Initialize the sorters with dummy values
			sortedTopics[topic] = new IDSorter( topic, topic );
		}

		if ( max < 0 || max > numTopics )
		{
			max = numTopics;
		}

		LabelSequence topicSequence = (LabelSequence) m.data.get( docID ).topicSequence;
		int[] currentDocTopics = topicSequence.getFeatures();

		docLen = currentDocTopics.length;

		// Count up the tokens
		for ( int token = 0; token < docLen; token++ )
		{
			topicCounts[currentDocTopics[token]]++;
		}

		// And normalize
		for ( int topic = 0; topic < numTopics; topic++ )
		{
			sortedTopics[topic].set( topic, ( m.alpha[topic] + topicCounts[topic] ) / ( docLen + m.alphaSum ) );
		}

		Arrays.sort( sortedTopics );

		// m.data.get(docID).instance.getName(); // can be also
		// model.data.get(doc).instance.getID(); or whatever :))))
		for ( int i = 0; i < max; i++ )
		{
			if ( sortedTopics[i].getWeight() < threshold )
			{
				break;
			}

			topicID = sortedTopics[i].getID();
			topicWeight = sortedTopics[i].getWeight();
			topics.add( topicID + "-" + topicWeight );
		}
		h.put( m.data.get( docID ).instance.getName() + "", topics );
		Arrays.fill( topicCounts, 0 );
		return h;
	}

	// function used to get the sorted words per topic
	public ArrayList<TreeSet<IDSorter>> getSortedWords( ParallelTopicModel m, int numTopics )
	{

		ArrayList<TreeSet<IDSorter>> topicSortedWords = new ArrayList<TreeSet<IDSorter>>( numTopics );

		// Initialize the tree sets
		for ( int topic = 0; topic < numTopics; topic++ )
		{
			topicSortedWords.add( new TreeSet<IDSorter>() );
		}

		// Collect counts
		for ( int type = 0; type < m.numTypes; type++ )
		{

			int[] topicCounts = m.typeTopicCounts[type];

			int index = 0;
			while ( index < topicCounts.length && topicCounts[index] > 0 )
			{

				int topic = topicCounts[index] & m.topicMask;
				int count = topicCounts[index] >> m.topicBits;

				topicSortedWords.get( topic ).add( new IDSorter( type, count ) );

				index++;
			}
		}

		return topicSortedWords;
	}
}