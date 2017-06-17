package de.rwth.i9.palm.helper.comparator;

import java.util.Comparator;

import de.rwth.i9.palm.helper.TreeHelper;

public class TreeDirectChildByNaturalOrderComparator implements Comparator<TreeHelper>
{

	@Override
	public int compare( final TreeHelper treeHelper1, final TreeHelper treeHelper2 )
	{
		if ( treeHelper1 == null && treeHelper2 == null )
			return 0;

		if ( treeHelper1 == null )
			return -1;

		if ( treeHelper2 == null )
			return 1;

		return treeHelper1.getTitle().compareTo( treeHelper2.getTitle() );
	}

}