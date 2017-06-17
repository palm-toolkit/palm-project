//package de.rwth.i9.palm.analytics.algorithm.lda;
//
//import java.io.IOException;
//import java.nio.file.Paths;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.apache.lucene.analysis.de.GermanAnalyzer;
//import org.apache.lucene.analysis.en.EnglishAnalyzer;
//import org.apache.lucene.document.Document;
//import org.apache.lucene.document.Field;
//import org.apache.lucene.document.FieldType;
//import org.apache.lucene.index.DirectoryReader;
//import org.apache.lucene.index.FieldInfo.IndexOptions;
//import org.apache.lucene.index.IndexReader;
//import org.apache.lucene.index.IndexWriter;
//import org.apache.lucene.index.IndexWriterConfig;
//import org.apache.lucene.index.Terms;
//import org.apache.lucene.index.TermsEnum;
//import org.apache.lucene.search.IndexSearcher;
//import org.apache.lucene.search.PhraseQuery;
//import org.apache.lucene.search.ScoreDoc;
//import org.apache.lucene.search.TopDocs;
//import org.apache.lucene.store.Directory;
//import org.apache.lucene.store.FSDirectory;
//import org.apache.lucene.util.BytesRef;
//
//public class LuceneTest {
//
//	private IndexWriter writer;
//
//	public LuceneTest() {
//
//		Directory indexDirectory;
//		try {
// // indexDirectory = FSDirectory.open(
// "C:/Users/Administrator/Desktop/Years/Years/2014.txt" );
//
//			EnglishAnalyzer germanAnalyzer = new EnglishAnalyzer(null);
//
//			IndexWriterConfig writerConfig = new IndexWriterConfig(null, germanAnalyzer);
//			writerConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
////			writer = new IndexWriter(indexDirectory, writerConfig);
////		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//	// Takes as input the string of the document and indexes it
//	public void CreateDocument(String content) {
//		Document document = new Document();
//
//		FieldType fieldType = new FieldType();
//		fieldType.setStoreTermVectors(true);
//		fieldType.setStored(true);
//		fieldType.setTokenized(true);
//		fieldType.setStoreTermVectors(true);
//		fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
//
//		Field field = new Field("content", content, fieldType);
//
//		document.add(field);
//		
//		try {
//			writer.addDocument(document);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}
//	
//	
//	public void readIndex(int numberDoc){
//		try {
//			IndexReader reader = DirectoryReader.open(writer, true);
//			IndexSearcher searcher = new IndexSearcher(reader);
//			
//			PhraseQuery phrase = new PhraseQuery();
//			
//			TopDocs topDocs = searcher.search(phrase, numberDoc);
//			
//			ScoreDoc [] docs = topDocs.scoreDocs;
//			
//			for(int i= 1; i < docs.length ;i++){
//				//get the terms for every document
//				Terms terms = reader.getTermVector(docs[i].doc, "content");
//				TermsEnum termsIterator = terms.iterator( null );
//				BytesRef term ;
//				//loop on all terms of the document
//				while((term = termsIterator.next()) != null){
//					String text = term.utf8ToString();
//					long freq = termsIterator.totalTermFreq();
//				}
//				
//				
//			}
//			
//			
//			
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//	}
//	
//	public void finish(){
//		try {
//			writer.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//	public static void main(String[] args) {
//
//		LuceneTest  test = new LuceneTest();
//		int i = 10;
//		//loop to add the contents to your document
//		while(i < 10){
//			test.CreateDocument("ADD CONTENT HERE");
//			
//		}
//		
//		//This reads the index and specifies the term and the freq just use the loop to add the stuff
//		//to the matrix and create the matrix
//		test.readIndex(10);
//		
//		//don't forget to close the writer otherwise it will never close and u can't use it again
//		test.finish();
//		
//		
//	}
//}