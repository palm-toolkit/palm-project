package de.rwth.i9.palm.datasetcollect.service;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( loader = AnnotationConfigContextLoader.class )
public class HtmlPublicationCollectionTest
{

	@Test
	public void getPublicationDetailByPublicationUrlTest() throws IOException
	{
		String input = "http://www.scitepress.org/DigitalLibrary/Link.aspx?doi=10.5220/0004791400090020";
		//input = "http://www.degruyter.com/view/j/icom.2012.11.issue-1/icom.2012.0007/icom.2012.0007.xml";
		//input = "http://www.computer.org/csdl/trans/lt/2013/04/tlt2013040337-abs.html";
		//input = "http://www.emeraldinsight.com/doi/abs/10.1108/13673271211262835";
		//input = "http://www.scitepress.org/DigitalLibrary/Link.aspx?doi=10.5220/0005495501480159";
		//input = "http://link.springer.com/chapter/10.1007%2F978-3-642-04636-0_30";
		//input = "http://www.computer.org/csdl/proceedings/icalt/2006/2632/00/263200912-abs.html";
		//input = "http://ieeexplore.ieee.org/xpl/articleDetails.jsp?arnumber=1508758";
		// input = "http://link.springer.com/chapter/10.1007%2F11575801_18";
		// input = "http://www.computer.org/csdl/proceedings/icalt/2014/4038/00/4038a044-abs.html";
		// input = "http://dx.doi.org/10.1504/IJMLO.2010.029952";
		// input = "http://www.igi-global.com/article/toward-personal-learning-environment-framework/48222";
		// input = "http://ieeexplore.ieee.org/xpl/articleDetails.jsp?arnumber=4032537";
		// input = "http://dl.acm.org/citation.cfm?id=2337376";
		// input = "http://www.emeraldinsight.com/doi/full/10.1108/17440081011034466";
		input = "http://dl.acm.org/citation.cfm?doid=2629597";

		Map<String, String> publicationDetailMaps = HtmlPublicationCollection.getPublicationInformationFromHtmlPage( input );

		for ( Entry<String, String> eachPublicationDetail : publicationDetailMaps.entrySet() )
			System.out.println( eachPublicationDetail.getKey() + " : " + eachPublicationDetail.getValue() );

	}

	@Test
	@Ignore
	public void geteeePdfUrlTest() throws IOException
	{
		String input = "http://ieeexplore.ieee.org/stamp/stamp.jsp?arnumber=6095503";

		System.out.println( HtmlPublicationCollection.getIeeePdfUrl( input ) );

	}
}
