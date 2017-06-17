package de.rwth.i9.palm.persistence.relational;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.Source;
import de.rwth.i9.palm.model.SourceType;
import de.rwth.i9.palm.persistence.SourceDAO;

public class SourceDAOHibernate extends GenericDAOHibernate<Source> implements SourceDAO
{

	public SourceDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

	@Override
	public List<Source> getAllSource()
	{
		Query query = getCurrentSession().createQuery( "FROM Source" );

		@SuppressWarnings( "unchecked" )
		List<Source> sources = query.list();

		if ( sources == null )
			return Collections.emptyList();

		return sources;
	}

	@Override
	public Map<SourceType, Boolean> getActiveSourceMap()
	{
		Query query = getCurrentSession().createQuery( "FROM Source" );
		
		@SuppressWarnings( "unchecked" )
		List<Source> sources = query.list();
		
		if( sources == null )
			return Collections.emptyMap();
		
		Map<SourceType, Boolean> activeSourceMap = new HashMap<SourceType, Boolean>();
		for( Source source : sources){
			activeSourceMap.put( source.getSourceType(), source.isActive() );
		}

		if ( activeSourceMap.isEmpty() )
			return Collections.emptyMap();

		return activeSourceMap;
	}

	@Override
	public Map<String, Source> getSourceMap()
	{

		Query query = getCurrentSession().createQuery( "FROM Source" );

		@SuppressWarnings( "unchecked" )
		List<Source> sources = query.list();

		if ( sources == null )
			return Collections.emptyMap();

		Map<String, Source> sourceMap = new HashMap<String, Source>();
		for ( Source source : sources )
		{
			sourceMap.put( source.getSourceType().toString(), source );
		}

		return sourceMap;
	}

}
