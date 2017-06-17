package de.rwth.i9.palm.analytics.util;

public class TopicMiningConstants
{

	// path Windows
	public static final String USER_DESKTOP_PATH = "C:\\Users\\Ligi\\Desktop\\";
	public static final String USER_YEARS_FOLDER_PATH = USER_DESKTOP_PATH + "Years\\";
	public static final String USER_MALLET_PATH = "C:\\Mallet\\";
	// public static final String USER_MALLET_PATH =
	// "C:\\Users\\Ligi\\git\\Mallet\\";
	public static final String USER_PROCESS_COMMAND_INPUT = "cd \"" + USER_MALLET_PATH + "\"&& bin\\mallet import-dir --input " + USER_DESKTOP_PATH;
	public static final String USER_PROCESS_COMMAND_OUTPUT = " --keep-sequence-bigrams --remove-stopwords --extra-stopwords " + USER_MALLET_PATH + "stoplists\\extra-stoplist.txt" + " --output " + USER_DESKTOP_PATH;
	public static final String USER_PROCESS_COMMAND_WITHOUT_STOPWORDS_OUTPUT = " --keep-sequence-bigrams --remove-stopwords --output " + USER_DESKTOP_PATH ;

	// path MAC
	/*  
	 	public static final String USER_DESKTOP_PATH = String path = "/Users/Ligi/Desktop/";
		public static final String USER_PROCESS_COMMAND_INPUT = USER_MALLET_PATH + "\\bin\\mallet import-dir --input " + USER_DESKTOP_PATH ;
	 	public static final String USER_PROCESS_COMMAND_OUTPUT = " --keep-sequence-bigrams --remove-stopwords --extra-stopwords " +  USER_MALLET_PATH + "\\stoplists\\extra-stoplist.txt" + " --output " + USER_DESKTOP_PATH ;
	 	public static final String USER_PROCESS_COMMAND_WITHOUT_STOPWORDS_OUTPUT = " --keep-sequence-bigrams --remove-stopwords --output " + USER_DESKTOP_PATH ;
	 	
	 */


}
