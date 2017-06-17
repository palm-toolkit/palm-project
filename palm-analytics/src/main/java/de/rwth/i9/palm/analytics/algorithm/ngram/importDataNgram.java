package de.rwth.i9.palm.analytics.algorithm.ngram;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.Input2CharSequence;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.TokenSequence2FeatureSequenceWithBigrams;
import cc.mallet.pipe.TokenSequenceLowercase;
import cc.mallet.pipe.TokenSequenceRemoveNonAlpha;
import cc.mallet.pipe.TokenSequenceRemoveStopwords;
import cc.mallet.pipe.iterator.FileIterator;
import cc.mallet.types.InstanceList;


public class importDataNgram
{
	 Pipe pipe;

	    public importDataNgram() {
	        pipe = buildPipe();
	    }

	    public Pipe buildPipe() {
		ArrayList<Pipe> pipeList = new ArrayList<Pipe>();

	        // Read data from File objects
	        pipeList.add(new Input2CharSequence("UTF-8"));

	        // Tokenize raw strings
	        pipeList.add(new CharSequence2TokenSequence());

	        // Normalize all tokens to all lowercase
	        pipeList.add(new TokenSequenceLowercase());

	        // Remove stopwords from a standard English stoplist.
	        //  options: [case sensitive] [mark deletions]
	        pipeList.add(new TokenSequenceRemoveStopwords(false, true));

	        // Rather than storing tokens as strings, convert 
	        //  them to integers by looking them up in an alphabet.
	        pipeList.add(new TokenSequenceRemoveNonAlpha(false));
	        pipeList.add(new TokenSequence2FeatureSequenceWithBigrams());
	        
	        
	        return new SerialPipes(pipeList);
	    }

	    public InstanceList readDirectory(File directory) {
	        return readDirectories(new File[] {directory});
	    }

	    public InstanceList readDirectories(File[] directories) {
	        
	        // Construct a file iterator, starting with the 
	        //  specified directories, and recursing through subdirectories.
	        // The second argument specifies a FileFilter to use to select
	        //  files within a directory.
	        // The third argument is a Pattern that is applied to the 
	        //   filename to produce a class label. In this case, I've 
	        //   asked it to use the last directory name in the path.
	        FileIterator iterator =
	            new FileIterator(directories,
	                             new TxtFilter(),
	                             FileIterator.LAST_DIRECTORY);

	        // Construct a new instance list, passing it the pipe
	        //  we want to use to process instances.
	        InstanceList instances = new InstanceList(pipe);

	        // Now process each instance provided by the iterator.
	        instances.addThruPipe(iterator);

	        return instances;
	    }

	    /** This class illustrates how to build a simple file filter */
	    class TxtFilter implements FileFilter {

	        /** Test whether the string representation of the file 
	         *   ends with the correct extension. Note that {@ref FileIterator}
	         *   will only call this filter if the file is not a directory,
	         *   so we do not need to test that it is a file.
	         */
	        public boolean accept(File file) {
	            return file.toString().endsWith(".txt");
	        }
	    }
}
