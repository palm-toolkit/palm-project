package de.rwth.i9.palm.analytics.algorithm.dynamicLDA;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class makeTfidfLongvectors {
	public static int numDocuments;
	
	public ArrayList loadUserDocs() throws IOException{
		//nb returns arraylist, each element is an array size 2
		ArrayList userDocs= new ArrayList();
		ArrayList tempUserDoc= new ArrayList();
		
                // one document per line. format: [username**MARK**document content.....]
		String docPath="/path/to/documentsFile";
		BufferedReader br= new BufferedReader(new FileReader(new File(docPath)));
		
		String line;
		String doc;
		String user;
		String[] userAndDoc;
		int countLine=0;
		int parseErrs=0;
		
		while ((line=br.readLine())!=null){
			//System.out.println(line);
			try{
                        //each line contains the user's name, then their document, seperated by "**MARK**"
			userAndDoc=line.split("\\*\\*MARK\\*\\*");
			user=userAndDoc[0];
			doc=userAndDoc[1];
			//System.out.println(user+doc);
			if (doc.length()>3){
				userDocs.add(userAndDoc);
			}
						
			countLine++;
			}catch (Exception e){parseErrs++;}
			
			
		}
		System.out.println(parseErrs);
		
		System.out.println("Num lines: "+countLine);
		this.numDocuments=userDocs.size();
		System.out.println("num docs: "+this.numDocuments);
		
		return userDocs; 
	}
	
	
	public HashMap loadVocabMap() throws IOException{
                //contains each unique word in the corpus, plus the number of documents it's found in.
                //format: [word frequency]
                //returned as a word:frequency map

		String vocabFilePath="/path/to/docFreqs.data";
		
		HashMap<String,Integer> vocabCount=new HashMap();
		String line="";
		BufferedReader br= new BufferedReader(new FileReader(new File(vocabFilePath)));
		String[] thisWordAndFreq;
		String key;
		Integer value;
		while((line=br.readLine())!=null){
			thisWordAndFreq=line.split(" ");
			key=thisWordAndFreq[0];
			value=Integer.parseInt(thisWordAndFreq[1]);
			if (thisWordAndFreq[0].length()>2){ //ie if a word is actually there and not whitespace etc.
				vocabCount.put(key, value);
			}
		}
		return vocabCount;
		
	}
	
	public static void main(String[] args) throws IOException{
		int count=0;
		makeTfidfLongvectors  mtl= new makeTfidfLongvectors ();
		ArrayList vocabList= new ArrayList();
		
		HashMap vocabAndFreq= mtl.loadVocabMap();
		vocabList=mtl.makeVocabList(); //update vocabList defined in class
		System.out.println("vocab list size:  "+vocabList.size());
		ArrayList documents=mtl.loadUserDocs(); //rem that each elem is [[uname][doc]]
		ArrayList<Double> initDocMatrix;
		ArrayList docMatrices;
		ArrayList<Double> tfidfLongMatrix;
		String[] docSplit;
		String docStr;
		
		
		for(int i=0;i<documents.size();i++){
			
			initDocMatrix=mtl.initialiseDocMatrix(vocabList);
			
			String[] thisDocList=(String[]) documents.get(i);
			String user=thisDocList[0];
			String userDoc=thisDocList[1];
			tfidfLongMatrix=makeTfidfMatrix(userDoc, vocabAndFreq, initDocMatrix,vocabList);
			mtl.writeLine(user, tfidfLongMatrix);
			
			if (i%500==0){
				System.out.println(i+" of "+ documents.size()+" written");
			}
			
		}
		
	}


	private void writeLine(String user, ArrayList<Double> tfidfLongMatrix) throws IOException {
                //writes tf-idf weighted vectors to file

		String matrixFilePath="/destinationFolder/tfidfVectors.data";
		FileWriter fw=new FileWriter(matrixFilePath,true);
		fw.write(user+" ");
		DecimalFormat fourDForm = new DecimalFormat("#.#####");
		Iterator iter= tfidfLongMatrix.iterator();
		while (iter.hasNext()){
			fw.write(String.valueOf(fourDForm.format(iter.next()))+" ");
		}
		fw.write("\n");
		fw.close();	
		
	}


	private ArrayList makeVocabList() throws IOException{
                //as well as vocab/frequency hashmap, i need an arraylist, which is used to ensure the placing of tf-idf scores in the same order in the vector.               
 
		String vocabFilePath="C://datasets//twitter_data//sep11//forCossim//docFreqs_790-839.data";
		ArrayList vocab=new ArrayList();
		String line="";
		BufferedReader br= new BufferedReader(new FileReader(new File(vocabFilePath)));
		String[] thisWordAndFreq;
		String word;
		
		
		while((line=br.readLine())!=null){
			thisWordAndFreq=line.split(" ");
			word=thisWordAndFreq[0];
			if (thisWordAndFreq[0].length()>2){ //ie if a word is actually there and not whitespace etc.
				vocab.add(word);
			}
		}
		return vocab;
		
	}


	private static ArrayList<Double> makeTfidfMatrix(String userDoc, HashMap vocabAndFreq, ArrayList<Double> docMatrix,ArrayList vocabList) {
		String[] docSplit=userDoc.split(" ");
		//find unique set of words
		Set<String> wordSet=new HashSet(Arrays.asList(docSplit));

		Iterator setIter= wordSet.iterator();
		int docLen=docSplit.length;
		int errs=0;

		while (setIter.hasNext()){
			String word=(String) setIter.next();
			try{
				Double wordTfidfScore=getWordTfidf(word, docSplit, vocabAndFreq, docLen);
				//find place of that word in vocab
				int place=vocabList.indexOf(word);
				docMatrix.set(place, wordTfidfScore);
				
			}catch(Exception e){errs++;//ie word isn't in vocab. ie was a stop word etc.
			}
			
		}
		//System.out.println(errs);
		return docMatrix;
	}


	private static Double getWordTfidf(String word, String[] docSplit, HashMap vocabAndFreq, int docLen) {
		double tf=getTf(word, docSplit,docLen);
		double idf=getIdf(word, (Integer)vocabAndFreq.get(word));
		double tfidf=tf*idf;
		
		
		return tfidf;
		
	}
	
 

	private static double getIdf(String word, int numDocsContainingWord) {
		
		
		return Math.log(((numDocuments*1.0)/numDocsContainingWord));
	}


	private static double getTf(String word, String[] docSplit, int docLen) {
		//number of occurences of this word in document
		int termFreq=0;
		for(int k=0;k<docSplit.length;k++){
			if (word==docSplit[k]){
				termFreq++;
			}
		}
		return (termFreq/(float)docSplit.length);
	}
	
	


	private ArrayList initialiseDocMatrix(ArrayList vocabList) {
                //set up an initial vector of the correct size (the size of the corpus vocab.) comprised of zeros
			ArrayList initDocMatrix= new ArrayList();
			for (int i=0;i<vocabList.size();i++){
				initDocMatrix.add(0.0);
				
			}
			return initDocMatrix;
		}

	}