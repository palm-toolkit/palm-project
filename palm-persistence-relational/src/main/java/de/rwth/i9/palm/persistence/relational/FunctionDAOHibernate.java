package de.rwth.i9.palm.persistence.relational;

import java.util.Collections;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

import de.rwth.i9.palm.model.Function;
import de.rwth.i9.palm.model.FunctionType;
import de.rwth.i9.palm.persistence.FunctionDAO;

public class FunctionDAOHibernate extends GenericDAOHibernate<Function> implements FunctionDAO
{

	public FunctionDAOHibernate( SessionFactory sessionFactory )
	{
		super( sessionFactory );
	}

	@Override
	public List<Function> getFunctionByFunctionTypeAndGrantType( FunctionType functionType, String grantType )
	{
		if ( functionType == null || grantType == null )
			return Collections.emptyList();

		StringBuilder queryString = new StringBuilder();
		queryString.append( "FROM Function f " );
		queryString.append( "WHERE f.functionType = :functionType " );
		queryString.append( "AND f.grantType = :grantType " );

		Query query = getCurrentSession().createQuery( queryString.toString() );
		query.setParameter( "functionType", functionType );
		query.setParameter( "grantType", grantType );

		@SuppressWarnings( "unchecked" )
		List<Function> functions = query.list();

		if ( functions == null || functions.isEmpty() )
			return Collections.emptyList();

		return functions;
	}

}