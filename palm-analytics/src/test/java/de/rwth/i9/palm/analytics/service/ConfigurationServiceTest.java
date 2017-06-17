package de.rwth.i9.palm.analytics.service;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import de.rwth.i9.palm.analytics.config.AppConfig;


/**
 * Test whether the properties files successfully loaded
 * 
 * @author sigit
 *
 */

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( classes = AppConfig.class, loader = AnnotationConfigContextLoader.class )
public class ConfigurationServiceTest
{
	@Autowired
	private AppService configurationService;

	@Test
	public void testConfigurationService()
	{
		assertNotNull( configurationService.getOpenNLPSentence() );
	}


}
