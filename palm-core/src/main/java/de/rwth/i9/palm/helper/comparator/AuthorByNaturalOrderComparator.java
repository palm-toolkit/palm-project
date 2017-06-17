package de.rwth.i9.palm.helper.comparator;

import java.util.Comparator;

import de.rwth.i9.palm.model.Author;

public class AuthorByNaturalOrderComparator implements Comparator<Author>
{

	@Override
	public int compare( final Author author1, final Author author2 )
	{
		String name1 = author1.getName();
		String name2 = author2.getName();

		return name1.compareTo( name2 );
	}

}