package de.rwth.i9.palm.analytics.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;

import de.rwth.i9.palm.analytics.api.PalmAnalyticsImpl;

@Configuration
@ComponentScan( { "de.rwth.i9.palm" } )
@PropertySource( "classpath:opennlp.properties" )
@Lazy( true )
public class AppConfig
{

	@Autowired
	private Environment env;

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer()
	{
		return new PropertySourcesPlaceholderConfigurer();
	}
	
	/* palm analytics */
	@Bean
	@Scope( "singleton" )
	public PalmAnalyticsImpl configAnalyticsImpl()
	{
		PalmAnalyticsImpl palmAnalyticsImpl = new PalmAnalyticsImpl();
		return palmAnalyticsImpl;
	}
}
