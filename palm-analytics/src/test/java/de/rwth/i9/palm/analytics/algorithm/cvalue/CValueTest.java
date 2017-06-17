package de.rwth.i9.palm.analytics.algorithm.cvalue;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import de.rwth.i9.palm.analytics.api.PalmAnalytics;
import de.rwth.i9.palm.analytics.config.AppConfig;

/**
 * Test all functionalities of CValue algorithm through interface and classes
 * 
 */

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( classes = AppConfig.class, loader = AnnotationConfigContextLoader.class )
public class CValueTest
{
	@Autowired
	PalmAnalytics palmAnalytics;

	@Before
	public void init()
	{
		assertThat( palmAnalytics, is( not( ( nullValue() ) ) ) );
	}

	@Test
	@Ignore
	public void testCandidateTermSubstring()
	{
		TermCandidate tc = new TermCandidate( "RECURRENT BASAL CELL CARCINOMA" );
		List<String> tcSubTerm = tc.getTermCandidateSubString();

		List<String> expectedSubTerm = new ArrayList<String>();
		expectedSubTerm.add( "RECURRENT BASAL CELL" );
		expectedSubTerm.add( "BASAL CELL CARCINOMA" );
		expectedSubTerm.add( "RECURRENT BASAL" );
		expectedSubTerm.add( "BASAL CELL" );
		expectedSubTerm.add( "CELL CARCINOMA" );
		expectedSubTerm.add( "RECURRENT" );
		expectedSubTerm.add( "BASAL" );
		expectedSubTerm.add( "CELL" );
		expectedSubTerm.add( "CARCINOMA" );

		assertThat( tcSubTerm, is( expectedSubTerm ) );
	}

	@Test
	public void testCValueCalculation()
	{
		CValueAlgorithm cva = new CValueAlgorithm();

		List<String> terms = new LinkedList<String>();

		for ( int i = 0; i < 5; i++ )
			terms.add( "ADENOID CYSTIC BASAL CELL CARCINOMA" );

		for ( int i = 0; i < 11; i++ )
			terms.add( "CYSTIC BASAL CELL CARCINOMA" );

		for ( int i = 0; i < 7; i++ )
			terms.add( "ULCERATED BASAL CELL CARCINOMA" );

		for ( int i = 0; i < 5; i++ )
			terms.add( "RECURRENT BASAL CELL CARCINOMA" );

		for ( int i = 0; i < 3; i++ )
			terms.add( "CIRCUMSCRIBED BASAL CELL CARCINOMA" );

		for ( int i = 0; i < 984; i++ )
			terms.add( "BASAL CELL CARCINOMA" );

		terms.add( "CELL CARCINOMA" );

		cva.setTerms( terms );
		cva.setFrequencyThreshold( 2 );
		cva.calculateCValue();

		// because the threshold set to 2
		// the "CELL CARCINOMA" should be ignored
		// thus, there must be 6 types of multi-word terms
		assertThat( "multi-word term size", cva.getTermCandidates().size(), is( 6 ) );

		// the result should be
		// length freq freqnested numbernester molti-word-term
		// calculation-result
		// 3 984 5 31 BASAL CELL CARCINOMA 1549.7763332051466
		// 4 7 0 0 ULCERATED BASAL CELL CARCINOMA 14.0
		// 4 11 1 5 CYSTIC BASAL CELL CARCINOMA 12.0
		// 5 5 0 0 ADENOID CYSTIC BASAL CELL CARCINOMA 11.60964047443681
		// 4 5 0 0 RECURRENT BASAL CELL CARCINOMA 10.0
		// 4 3 0 0 CIRCUMSCRIBED BASAL CELL CARCINOMA 6.0
		TermCandidate highestCandidate = null;
		for ( TermCandidate cand : cva.getTermCandidates() )
		{
			highestCandidate = cand;
			if ( highestCandidate != null )
				break;
		}
		assertThat( "largest candidate value ", highestCandidate.getCValue(), is( greaterThan( 1500.0 )) );
	}
}
