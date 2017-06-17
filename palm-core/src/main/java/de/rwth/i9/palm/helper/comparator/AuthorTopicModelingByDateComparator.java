package de.rwth.i9.palm.helper.comparator;

import java.util.Comparator;

import de.rwth.i9.palm.model.AuthorTopicModeling;

public class AuthorTopicModelingByDateComparator implements Comparator<AuthorTopicModeling>
{

	@Override
	public int compare( final AuthorTopicModeling authorTopicModeling1, final AuthorTopicModeling authorTopicModeling2 )
	{
		if ( authorTopicModeling1 == null && authorTopicModeling2 == null )
			return 0;

		if ( authorTopicModeling1 == null )
			return -1;

		if ( authorTopicModeling2 == null )
			return 1;

		if ( authorTopicModeling1.getYear().before( authorTopicModeling2.getYear() ) )
			return -1;
		else if ( authorTopicModeling1.getYear().after( authorTopicModeling2.getYear() ) )
			return 1;
		else
			return 0;
	}

}