package de.rwth.i9.palm.feature.circle;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.text.WordUtils;

import de.rwth.i9.palm.model.Circle;

public class CircleBasicInformationImpl implements CircleBasicInformation
{

	@Override
	public Map<String, Object> getCircleBasicInformationMap( Circle circle )
	{
		Map<String, Object> circleMap = new LinkedHashMap<String, Object>();

		// preparing data format
		DateFormat dateFormat = new SimpleDateFormat( "yyyy/M/d", Locale.ENGLISH );

		circleMap.put( "id", circle.getId() );

		circleMap.put( "name", circle.getName() );
		circleMap.put( "dateCreated", dateFormat.format( circle.getCreationDate() ) );

		if ( circle.getCreator() != null )
		{
			Map<String, Object> creatorMap = new LinkedHashMap<String, Object>();
			creatorMap.put( "id", circle.getCreator().getId() );
			creatorMap.put( "name", WordUtils.capitalize( circle.getCreator().getName() ) );
			if ( circle.getCreator().getAuthor() != null )
				creatorMap.put( "authorId", circle.getCreator().getAuthor().getId() );

			circleMap.put( "creator", creatorMap );
		}

		if ( circle.getAuthors() != null )
			circleMap.put( "numberAuthors", circle.getAuthors().size() );

		if ( circle.getPublications() != null )
			circleMap.put( "numberPublications", circle.getPublications().size() );

		if ( circle.getDescription() != null && !circle.getDescription().equals( "" ) )
			circleMap.put( "description", circle.getDescription() );

		// check autowired with security service here
		circleMap.put( "isLock", circle.isLock() );
		circleMap.put( "isValid", circle.isValid() );

		return circleMap;
	}

}
