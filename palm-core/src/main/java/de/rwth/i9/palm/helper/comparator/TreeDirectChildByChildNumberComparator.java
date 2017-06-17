package de.rwth.i9.palm.helper.comparator;

import java.util.Comparator;

import de.rwth.i9.palm.helper.TreeHelper;

public class TreeDirectChildByChildNumberComparator implements Comparator<TreeHelper>
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

		// the tree depth is always 2
		int cildrenSize1 = 0;
		for ( TreeHelper child : treeHelper1.getChildren() )
		{
			cildrenSize1 += child.getChildren().size();
		}
		int cildrenSize2 = 0;
		for ( TreeHelper child : treeHelper2.getChildren() )
		{
			cildrenSize2 += child.getChildren().size();
		}

		if ( cildrenSize1 < cildrenSize2 )
			return 1;

		if ( cildrenSize1 > cildrenSize2 )
			return -1;

		return 0;
	}

}