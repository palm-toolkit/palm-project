package de.rwth.i9.palm.analytics.algorithm.LabeledLDA;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import cc.mallet.types.InstanceList;
import de.rwth.i9.palm.analytics.util.TopicMiningConstants;

public class L_LDA
{

	public void test() throws Exception
	{

		try
		{
			LabeledLDA llda = createModel( TopicMiningConstants.USER_DESKTOP_PATH, "labeledLDA", "Label", 10, 10 );

			for ( String a : printTopWords( llda, 10 ) )
				System.out.println( a + "+" );

			for ( Entry<String, String> e : getLabelTopicMap( llda, 10 ).entrySet() )
				System.out.println( e.getKey() + " -> " + e.getValue() );

			System.out.print( getTopDocsperTopic( llda, 3 ) );

			for ( String s : getTopicDistribution( llda, 0.0, 10 ) )
				System.out.println( s );

		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
	}

	// METHODS USED IN LABELED LDA

	// Recognizes the input as ID-LabelSet-DocumentContent and returns
	// instanceList that will be used from alg.
	public InstanceList getInstanceData( String path, String purpose, String docname ) throws IOException
	{
		// Get the data from a directory and convert it into mallet format
		// This version uses the command prompt execution
		// 1. Input2CharSequence
		// 2. CharSequence2To kenSequence
		// 3. TokenSequenceLowercase
		// 4. TokenSequenceRemoveStopwords
		// 5. Define the labels and use them as input files.

		ProcessBuilder builder = new ProcessBuilder( "cmd.exe", "/c", "cd \"C:\\mallet\"&& bin\\mallet import-file --input C:\\Users\\Albi\\Desktop\\" + purpose + "\\" + purpose + "\\" + docname + ".txt" + " --output C:\\Users\\Albi\\Desktop\\" + purpose + "\\" + docname + ".seq" + " --remove-stopwords --label-as-features --keep-sequence " + " --line-regex \"([^\\t]+)\\t([^\\t]+)\\t(.*)\"" );
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
		InstanceList training = InstanceList.load( new File( path + purpose + "/" + purpose + "/" + docname + ".seq" ) );
		return training;
	}

	// create the labeled LDA Model that will be furhter used for later
	// processing (concept extractions)
	public LabeledLDA createModel( String path, String purpose, String docname, int numTopics, int numWords ) throws IOException
	{

		LabeledLDA llda = new LabeledLDA( 0.1, 0.01 );
		InstanceList trained = getInstanceData( path, purpose, docname );
		llda.addInstances( trained );
		try
		{
			llda.estimate();
		}
		catch ( IOException e )
		{
			e.printStackTrace();
		}
		return llda;
	}

	// returns the topWords for each topic Array of Strings
	public String[] printTopWords( LabeledLDA llda, int nwords )
	{
		return llda.modifiedtopWords( nwords ).split( "\n" );
	}

	// returns the topWords for each topic List of Strings
	public List<String> getListTopics( LabeledLDA llda, int nwords )
	{
		List<String> listtopics = new ArrayList<String>();
		for ( String topic : printTopWords( llda, nwords ) )
		{
			listtopics.add( topic );
		}
		return listtopics;
	}

	// returns LinkedHashMap Label - Topic
	public LinkedHashMap<String, String> getLabelTopicMap( LabeledLDA llda, int numWords )
	{
		LinkedHashMap<String, String> topics = new LinkedHashMap<String, String>();
		for ( int i = 0; i < getListTopics( llda, numWords ).size(); i++ )
		{
			String[] temp = getListTopics( llda, numWords ).get( i ).split( "\t" );
			topics.put( temp[0], temp[1] );
		}
		return topics;
	}

	// method used to copy the structure of LabeledLDA created to a
	// ParallelTopicModel in order to proceed with calling the other methods
	public ParallelTopicModel copyLabeledLDAStruct( LabeledLDA llda )
	{
		ParallelTopicModel m = new ParallelTopicModel( llda.topicAlphabet, llda.alpha * llda.numTopics, llda.beta );
		m.data = llda.data;
		m.alphabet = llda.alphabet;
		m.numTypes = llda.numTypes;
		m.betaSum = llda.betaSum;
		m.buildInitialTypeTopicCounts();
		return m;
	}

	// returns the top X documents in which each of the topics are mostly
	// relevant
	// structure #topic #doc-index #doc-name #proportion
	public String getTopDocsperTopic( LabeledLDA llda, int max )
	{
		ParallelTopicModel m = copyLabeledLDAStruct( llda );
		return m.modifiedprintTopicDocuments( max );
	}

	public String[] getTopDocsperTopicString( LabeledLDA llda, int max )
	{
		ParallelTopicModel m = copyLabeledLDAStruct( llda );
		return m.modifiedprintTopicDocuments( max ).split( "\n" );
	}

	public List<String> getTopDocsperTopicList( LabeledLDA llda, int max )
	{
		ParallelTopicModel m = copyLabeledLDAStruct( llda );
		List<String> result = new ArrayList<String>();
		for ( String s : m.modifiedprintTopicDocuments( max ).split( "\n" ) )
			result.add( s );
		return result;
	}

	// get all the topic distributions
	public List<String> getTopicDistribution( LabeledLDA llda, double threshold, int max )
	{
		List<String> topics = new ArrayList<String>();
		ParallelTopicModel m = copyLabeledLDAStruct( llda );
		for ( int i = 0; i < m.data.size(); i++ )
		{
			topics.add( m.printoneDocumentTopics( threshold, max, i ) );
		}
		return topics;
	}

	// // get the document topic proportions in the form of <DocID,
	// List<Double>>
	// public LinkedHashMap <String, List<Double>>
	// getTopicDistributionforDocuments(double threshold, int max, int
	// numTopics){
	// LinkedHashMap<String, List<Double>> topicdist = new LinkedHashMap<String,
	// List<Double>>();
	// for (int i=0; i< years.data.size(); i++){
	// List<Double> proportions = new ArrayList<Double>();
	// for (Entry<Integer, Double> entry : getTopicProportion(threshold, i, max,
	// numTopics).entrySet()){
	// proportions.add( entry.getValue());
	// }
	// topicdist.put( ((years.data.get( i ).instance.getName() + "").split( "/"
	// )[7]).replaceAll(".txt",""), proportions );
	// }
	//
	// return topicdist;
	//
	//
	// }
	//
	// // USe the tree-struct to get the sorted words for each topic
	// public ArrayList<TreeSet<IDSorter>> getSortedWords( int numTopics )
	// {
	//
	// ArrayList<TreeSet<IDSorter>> topicSortedWords = new
	// ArrayList<TreeSet<IDSorter>>( numTopics );
	//
	// // Initialize the tree sets
	// for ( int topic = 0; topic < numTopics; topic++ )
	// {
	// topicSortedWords.add( new TreeSet<IDSorter>() );
	// }
	//
	// // Collect counts
	// for ( int type = 0; type < years.numTypes; type++ )
	// {
	//
	// int[] topicCounts = years.typeTopicCounts[type];
	//
	// int index = 0;
	// while ( index < topicCounts.length && topicCounts[index] > 0 )
	// {
	//
	// int topic = topicCounts[index] & years.topicMask;
	// int count = topicCounts[index] >> years.topicBits;
	//
	// topicSortedWords.get( topic ).add( new IDSorter( type, count ) );
	//
	// index++;
	// }
	// }
	//
	// return topicSortedWords;
	// }
	//
	// // returns the most relevant topic for all the documents
	// // Returns a map which shows the topic assigned to a specific document
	// with
	// // given ID
	// // When calling max = -1, threshold = 0.05,
	// public LinkedHashMap<String, String> getTopicDocument( int max, double
	// threshold, int numTopics, int numWords)
	// {
	// LinkedHashMap<String, String> toptopic = new LinkedHashMap<String,
	// String>();
	// int maxindex = -1;
	// List<String> topics = getListTopics(numWords);
	// for (Entry<String, List<Double>> entry :
	// getTopicDistributionforDocuments(threshold, max, numTopics).entrySet()){
	// Double tempmax = -1.0;
	// for (int i=0; i< entry.getValue().size(); i++){
	// if (entry.getValue().get( i ) > tempmax){
	// tempmax = entry.getValue().get( i );
	// maxindex = i;
	// }
	// }
	// toptopic.put( entry.getKey(), topics.get( maxindex ) );
	// }
	// return toptopic;
	// }
	//
	//
	// public HashMap<String, List<String>> getAllDocumentTopics( int max,
	// double threshold, int numTopics )
	// {
	// HashMap<String, List<String>> h = new HashMap<String, List<String>>();
	// int[] topicCounts = new int[numTopics];
	// int docLen = 0;
	// int topicID = 0;
	// double topicWeight = 0;
	// IDSorter[] sortedTopics = new IDSorter[numTopics];
	// for ( int topic = 0; topic < numTopics; topic++ )
	// {
	// // Initialize the sorters with dummy values
	// sortedTopics[topic] = new IDSorter( topic, topic );
	// }
	//
	// if ( max < 0 || max > numTopics )
	// {
	// max = numTopics;
	// }
	//
	// for ( int docID = 0; docID < years.data.size(); docID++ )
	// {
	// LabelSequence topicSequence = (LabelSequence) years.data.get( docID
	// ).topicSequence;
	// int[] currentDocTopics = topicSequence.getFeatures();
	//
	// docLen = currentDocTopics.length;
	//
	// // Count up the tokens
	// for ( int token = 0; token < docLen; token++ )
	// {
	// topicCounts[currentDocTopics[token]]++;
	// }
	//
	// // And normalize
	// for ( int topic = 0; topic < numTopics; topic++ )
	// {
	// sortedTopics[topic].set( topic, ( years.alpha[topic] + topicCounts[topic]
	// ) / ( docLen + years.alphaSum ) );
	// }
	//
	// Arrays.sort( sortedTopics );
	// List<String> distribution = new ArrayList<String>();
	// // m.data.get(docID).instance.getName(); // can be also
	// // model.data.get(doc).instance.getID(); or whatever :))))
	// for ( int i = 0; i < max; i++ )
	// {
	// if ( sortedTopics[i].getWeight() < threshold )
	// {
	// break;
	// }
	// if ( sortedTopics[0].getWeight() < threshold )
	// {
	// topicID = sortedTopics[0].getID();
	// topicWeight = sortedTopics[0].getWeight();
	// distribution.add( topicID + "-" + topicWeight );
	// }
	// }
	// h.put( (String) years.data.get( docID ).instance.getName(), distribution
	// );
	// Arrays.fill( topicCounts, 0 );
	// }
	// return h;
	// }
	//
	//
	// public LinkedHashMap<Integer,Double>
	// sortHashMapByValues(HashMap<Integer,Double> passedMap) {
	// List<Integer> mapKeys = new ArrayList<Integer>(passedMap.keySet());
	// List<Double> mapValues = new ArrayList<Double>(passedMap.values());
	// Collections.sort(mapValues);
	// Collections.sort(mapKeys);
	//
	// LinkedHashMap<Integer,Double> sortedMap = new
	// LinkedHashMap<Integer,Double>();
	//
	// Iterator<Double> valueIt = mapValues.iterator();
	// while (valueIt.hasNext()) {
	// Object val = valueIt.next();
	// Iterator<Integer> keyIt = mapKeys.iterator();
	//
	// while (keyIt.hasNext()) {
	// Object key = keyIt.next();
	// String comp1 = passedMap.get(key).toString();
	// String comp2 = val.toString();
	//
	// if (comp1.equals(comp2)){
	// passedMap.remove(key);
	// mapKeys.remove(key);
	// sortedMap.put((Integer)key, (Double)val);
	// break;
	// }
	// }
	// }
	// return sortedMap;
	// }
	//
	//
	// // get the topic probabilities and outputs them as a matrix. Rows are
	// documents and columns proportions for each topic
	// public double[][] getTopicProbabilityAll(){
	// double[][] a = new double[years.data.size()][years.numTopics];
	// double[] temp = new double[years.data.size()];
	// for (int i =0; i< years.data.size(); i++){
	// temp = years.getTopicProbabilities( i );
	// for ( int j=0; j < years.numTopics; j++){
	// a[i][j] = temp[j];
	// }
	// }
	// return a;
	// }

	// // This is used to assign a topic distribution to new Documents
	// // Used for AuthorId, DocId uzw
	// public double[] TopicInferencer( String path, String purpose, String
	// specify, String docID ) throws IOException
	// {
	// double[] topicProbs;
	// TopicInferencer infere = years.getInferencer();
	// InstanceList instance = getInstanceData( path, purpose );
	// topicProbs = infere.getSampledDistribution( instance.get( 0 ), 100, 10,
	// 10 );
	// return topicProbs;
	// }

}
