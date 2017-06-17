package de.rwth.i9.palm.analytics.textcompare;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import de.rwth.i9.palm.analytics.api.PalmAnalytics;
import de.rwth.i9.palm.analytics.config.AppConfig;


@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( classes = AppConfig.class, loader = AnnotationConfigContextLoader.class )
public class TextCompareTest
{
	@Autowired
	PalmAnalytics palmAnalytics;

	@Before
	public void init()
	{
		assertThat( palmAnalytics, is( not( ( nullValue() ) ) ) );
	}

	@Test
	public void testCandidateTermSubstring()
	{
		String text1 = "The future of e-learning: a shift to knowledge networking and social software";
		String text2 = "The future of e-learning: a shift to knowledge networking and social software lala";
		System.out.println( palmAnalytics.getTextCompare().getDistanceByLuceneLevenshteinDistance( text1, text2 ) );
	}
}
