package de.rwth.i9.persistence.relational;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;

import de.rwth.i9.palm.config.DatabaseConfigTest;
import de.rwth.i9.palm.model.Institution;
import de.rwth.i9.palm.persistence.InstitutionDAO;
import de.rwth.i9.palm.persistence.PersistenceStrategy;

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( classes = DatabaseConfigTest.class, loader = AnnotationConfigContextLoader.class )
@Transactional
public class InstitutionDAOHibernateTest
{
	@Autowired
	private PersistenceStrategy persistenceStrategy;

	private InstitutionDAO institutionDAO;

	@Before
	public void init() throws InterruptedException
	{
		institutionDAO = persistenceStrategy.getInstitutionDAO();
		assertNotNull( institutionDAO );
	}

	@Test
	public void getByNameTest() throws InterruptedException
	{
		String name = "RWTH Aachen University";

		System.out.println( "\n===== normal search query = \"" + name + "\"=====\n" );
		// try to search something
		List<Institution> institutions = persistenceStrategy.getInstitutionDAO().getByName( name );

		if ( !institutions.isEmpty() )
		for ( Institution institution : institutions )
		{
			System.out.println( "name : " + institution.getName() + " url : " + institution.getUrl() );
		}
	}

	@Test
	@Ignore
	public void generateAbbrSQLfromInstitutionUrl()
	{
		List<Institution> institutions = persistenceStrategy.getInstitutionDAO().getAll();

		for ( Institution institution : institutions )
		{
			if ( institution.getUrl() == null )
				continue;
			
			String abbr = "";

			String url = institution.getUrl();
			// remove http or www
			url = url.replace( "http://", "" ).replace( "www.", "" );
			
			// split based on dot
			String[] urlArray = url.split( "\\." );
			for( int i=0; i < urlArray.length - 1; i++){
				if( i == 0 )
					abbr = urlArray[ i ].replace( "-", " " );
				else{
					if( !urlArray[ i ].equals( "edu" ) && 
						!urlArray[ i ].equals( "org" ) &&
						!urlArray[ i ].equals( "ac" ) &&
						!urlArray[ i ].equals( "com" )
						){
						abbr += " " + urlArray[i];
					}
				}
			}
			
			System.out.println( "UPDATE institution SET abbr='" + abbr + "' WHERE id='" + institution.getId() + "';" );
		}
	}

	@Test
	@Ignore
	public void getByFullTextTest() throws InterruptedException
	{
		// do reindexing first
		// System.out.println( "\n===== Reindexing - Institution =====\n" );
		// persistenceStrategy.getInstitutionDAO().doReindexing();

		String label = "RWTH Aachen University";
		System.out.println( "\n===== fulltext search query = \"" + label + "\"=====\n" );
		
		List<Institution> institutions = persistenceStrategy.getInstitutionDAO().getWithFullTextSearch( label );
		
		if ( !institutions.isEmpty() )
		for ( Institution institution : institutions )
		{
			System.out.println( "name : " + institution.getName() );
		}

//		Map<String, Object> results2 = persistenceStrategy.getInstitutionDAO().getInstitutionByFUllTextSearchWithPaging( "", 1, 20 );
//		@SuppressWarnings( "unchecked" )
//		List<Institution> institutions2 = (List<Institution>) results2.get( "result" );
//
//		for ( Institution institution : institutions2 )
//		{
//			System.out.println( "title : " + institution.getTitle() );
//		}
//
//		System.out.println( "total record2 " + results2.get( "count" ) );
	}
}
