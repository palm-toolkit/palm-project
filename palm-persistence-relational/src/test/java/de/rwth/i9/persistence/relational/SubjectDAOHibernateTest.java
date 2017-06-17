package de.rwth.i9.persistence.relational;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;

import de.rwth.i9.palm.config.DatabaseConfigTest;
import de.rwth.i9.palm.model.Subject;
import de.rwth.i9.palm.persistence.PersistenceStrategy;
import de.rwth.i9.palm.persistence.SubjectDAO;

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( classes = DatabaseConfigTest.class, loader = AnnotationConfigContextLoader.class )
@Transactional
public class SubjectDAOHibernateTest
{
	@Autowired
	private PersistenceStrategy persistenceStrategy;

	private SubjectDAO subjectDAO;

	@Before
	public void init()
	{
		subjectDAO = persistenceStrategy.getSubjectDAO();
		assertNotNull( subjectDAO );
	}

	@Test
	public void test()
	{
		Subject subject = persistenceStrategy.getSubjectDAO().getSubjectByLabel( "something" );

	}

}
