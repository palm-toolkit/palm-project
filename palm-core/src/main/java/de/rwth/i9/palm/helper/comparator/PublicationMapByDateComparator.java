package de.rwth.i9.palm.helper.comparator;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;

public class PublicationMapByDateComparator implements Comparator<Map<String, Object>>
{

	@Override
	public int compare( final Map<String, Object> publication1, final Map<String, Object> publication2 )
	{
		if ( publication1 == null && publication2 == null )
			return 0;

		if ( publication1 == null )
			return 1;

		if ( publication2 == null )
			return -1;

		Date publicationDate1 = null;
		Date publicationDate2 = null;
		try
		{
			SimpleDateFormat sdf1 = new SimpleDateFormat( (String) publication1.get( "dateFormat" ) );
			publicationDate1 = sdf1.parse( (String) publication1.get( "date" ) );
		}
		catch ( Exception e )
		{
		}
		try
		{
			SimpleDateFormat sdf2 = new SimpleDateFormat( (String) publication2.get( "dateFormat" ) );
			publicationDate2 = sdf2.parse( (String) publication2.get( "date" ) );
		}
		catch ( Exception e )
		{
		}

		if ( publicationDate1 == null && publicationDate2 == null )
			return 0;

		if ( publicationDate1 == null )
			return 1;

		if ( publicationDate2 == null )
			return -1;

		if ( publicationDate1.before( publicationDate2 ) )
			return 1;
		else if ( publicationDate1.after( publicationDate2 ) )
			return -1;
		else
			return 0;
	}

}