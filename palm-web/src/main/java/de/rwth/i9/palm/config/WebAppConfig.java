package de.rwth.i9.palm.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

import de.rwth.i9.palm.analytics.api.PalmAnalyticsImpl;
import de.rwth.i9.palm.feature.academicevent.AcademicEventFeature;
import de.rwth.i9.palm.feature.academicevent.AcademicEventFeatureImpl;
import de.rwth.i9.palm.feature.circle.CircleFeature;
import de.rwth.i9.palm.feature.circle.CircleFeatureImpl;
import de.rwth.i9.palm.feature.publication.PublicationFeature;
import de.rwth.i9.palm.feature.publication.PublicationFeatureImpl;
import de.rwth.i9.palm.feature.researcher.ResearcherFeature;
import de.rwth.i9.palm.feature.researcher.ResearcherFeatureImpl;
import de.rwth.i9.palm.feature.user.UserFeature;
import de.rwth.i9.palm.feature.user.UserFeatureImpl;
import de.rwth.i9.palm.graph.feature.GraphFeature;
import de.rwth.i9.palm.graph.feature.GraphFeatureImpl;
import de.rwth.i9.palm.service.ApplicationService;
import de.rwth.i9.palm.service.SecurityService;
import de.rwth.i9.palm.service.TemplateService;

//import de.rwth.i9.palm.analytics.api.PalmAnalyticsImpl;

@Configuration
@EnableWebMvc
@ComponentScan( { "de.rwth.i9.palm" } )
@PropertySource( "classpath:application.properties" )
@EnableAsync
@Lazy( true )
public class WebAppConfig extends WebMvcConfigurerAdapter implements AsyncConfigurer
{
	private static final String PROPERTY_NAME_TASKEXECUTOR_MAXPOOLSIZE = "taskexecutor.maxpoolsize";
	private static final String PROPERTY_NAME_TASKEXECUTOR_COREPOOLSIZE = "taskexecutor.corepoolsize";
	private static final String PROPERTY_NAME_TASKEXECUTOR_QUEUECAPACITY = "taskexecutor.queuecapacity";
	private static final String PROPERTY_NAME_TASKEXECUTOR_THREADNAMEPREFIX = "taskexecutor.threadnameprefix";

	@Autowired
	private Environment env;

	/* freemarker */

	@Bean
	public FreeMarkerViewResolver setupFreeMarkerViewResolver()
	{
		FreeMarkerViewResolver freeMarkerViewResolver = new FreeMarkerViewResolver();
		freeMarkerViewResolver.setCache( false );
		freeMarkerViewResolver.setContentType( "text/html;charset=UTF-8" );
		freeMarkerViewResolver.setSuffix( ".ftl" );
		freeMarkerViewResolver.setExposeSessionAttributes( true );
		freeMarkerViewResolver.setExposeSpringMacroHelpers( true );
		freeMarkerViewResolver.setAllowSessionOverride( true );
		freeMarkerViewResolver.setRequestContextAttribute( "rc" );

		return freeMarkerViewResolver;
	}

	@Bean
	public FreeMarkerConfigurer setupFreeMarkerConfigurer()
	{
		FreeMarkerConfigurer freeMarkerConfigurer = new FreeMarkerConfigurer();
		freeMarkerConfigurer.setTemplateLoaderPaths( 
				"/WEB-INF/ftl/",
				"/WEB-INF/ftl/administration",
				"/WEB-INF/ftl/administration/api",
				"/WEB-INF/ftl/administration/conference",
				"/WEB-INF/ftl/administration/config",
				"/WEB-INF/ftl/administration/datamanagement",
				"/WEB-INF/ftl/administration/dataset",
				"/WEB-INF/ftl/administration/developer",
				"/WEB-INF/ftl/administration/publication",
				"/WEB-INF/ftl/administration/pdfhtmlextract",
				"/WEB-INF/ftl/administration/researcher",
				"/WEB-INF/ftl/administration/source",
				"/WEB-INF/ftl/administration/statistic",
				"/WEB-INF/ftl/administration/system",
				"/WEB-INF/ftl/administration/termextraction",
				"/WEB-INF/ftl/administration/topicmodeling",
				"/WEB-INF/ftl/administration/termweighting",
				"/WEB-INF/ftl/administration/test",
				"/WEB-INF/ftl/administration/user",
				"/WEB-INF/ftl/administration/widget",
				"/WEB-INF/ftl/analytics",
				"/WEB-INF/ftl/circle",
				"/WEB-INF/ftl/circle/widget",
				"/WEB-INF/ftl/conference",
				"/WEB-INF/ftl/conference/widget",
				"/WEB-INF/ftl/dataset", "/WEB-INF/ftl/dialog",
				"/WEB-INF/ftl/error",
				"/WEB-INF/ftl/explore",
				"/WEB-INF/ftl/explore/widget",
				"/WEB-INF/ftl/menu",
				"/WEB-INF/ftl/publication",
				"/WEB-INF/ftl/publication/widget",
				"/WEB-INF/ftl/researcher",
				"/WEB-INF/ftl/researcher/widget",
				"/WEB-INF/ftl/sparqlview", 
				"/WEB-INF/ftl/template",
				"/WEB-INF/ftl/template/form",
				"/WEB-INF/ftl/template/layout",
				"/WEB-INF/ftl/template/widget",
				"/WEB-INF/ftl/user", 
				"/WEB-INF/ftl/user/profile",
				"/WEB-INF/ftl/user/research", 
				"/WEB-INF/ftl/user/publcation", 
				"/WEB-INF/ftl/user/conference",
				"/WEB-INF/ftl/user/circle",
				"/WEB-INF/ftl/user/widget"
			);

		Properties prop = new Properties();
		prop.put( "default_encoding", "UTF-8" );
		prop.put( "auto_import", 
				"template/layout/layoutMacros.ftl as layout, " +
				"template/layout/contentMacros.ftl as content, " +
				"template/widget/widgetMacros.ftl as widget, " +
				"spring.ftl as spring, " +
				"template/layout/dialogLayoutMacros.ftl as dialoglayout" );

		freeMarkerConfigurer.setFreemarkerSettings( prop );

		Map<String, Object> freemakerVariables = new HashMap<String, Object>();
		freemakerVariables.put( "foo", "foo" );

		freeMarkerConfigurer.setFreemarkerVariables( freemakerVariables );
		return freeMarkerConfigurer;
	}

	/* resource */
	// Maps resources path to webapp/resources
	public void addResourceHandlers( ResourceHandlerRegistry registry )
	{
		registry.addResourceHandler( "/resources/**" ).addResourceLocations( "/resources/" );
	}

	// <mvc:default-servlet-handler/>
	@Override
	public void configureDefaultServletHandling( DefaultServletHandlerConfigurer configurer )
	{
		configurer.enable();
	}

	// Provides internationalization of messages
	@Bean
	public ResourceBundleMessageSource messageSource()
	{
		ResourceBundleMessageSource source = new ResourceBundleMessageSource();
		source.setBasename( "messages" );
		return source;
	}

	/* fileupload */
	@Bean( name = "multipartResolver" )
	public CommonsMultipartResolver createMultipartResolver()
	{
		CommonsMultipartResolver resolver = new CommonsMultipartResolver();
		resolver.setDefaultEncoding( "utf-8" );
		resolver.setMaxUploadSize( 10000000 );
		return resolver;
	}


	/* palm analytics */
	@Bean
	@Scope( "singleton" )
	public PalmAnalyticsImpl configAnalyticsImpl()
	{
		return new PalmAnalyticsImpl();
	}
	
	/* palm academic event feature */
	@Bean
	@Scope( "singleton" )
	public AcademicEventFeature academicEventFeature()
	{
		return new AcademicEventFeatureImpl();
	}

	/* palm researcher feature */
	@Bean
	@Scope( "singleton" )
	public ResearcherFeature researcherFeature()
	{
		return new ResearcherFeatureImpl();
	}

	/* palm publication feature */
	@Bean
	@Scope( "singleton" )
	public PublicationFeature publicationFeature()
	{
		return new PublicationFeatureImpl();
	}

	/* palm circle feature */
	@Bean
	@Scope( "singleton" )
	public CircleFeature CircleFeature()
	{
		return new CircleFeatureImpl();
	}

	/* palm user feature */
	@Bean
	@Scope( "singleton" )
	public UserFeature userFeature()
	{
		return new UserFeatureImpl();
	}

	/* palm graph feature */
	@Bean
	@Scope( "singleton" )
	public GraphFeature graphFeature()
	{
		return new GraphFeatureImpl();
	}

	/* Scheduling and ThreadPool */

	@Override
	public Executor getAsyncExecutor()
	{
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setMaxPoolSize( Integer.parseInt( env.getRequiredProperty( PROPERTY_NAME_TASKEXECUTOR_MAXPOOLSIZE ) ) );
		taskExecutor.setCorePoolSize( Integer.parseInt( env.getRequiredProperty( PROPERTY_NAME_TASKEXECUTOR_COREPOOLSIZE ) ) );
		taskExecutor.setQueueCapacity( Integer.parseInt( env.getRequiredProperty( PROPERTY_NAME_TASKEXECUTOR_QUEUECAPACITY ) ) );
		taskExecutor.setThreadNamePrefix( env.getRequiredProperty( PROPERTY_NAME_TASKEXECUTOR_THREADNAMEPREFIX ) );
		taskExecutor.initialize();
		return taskExecutor;
	}

	@Override
	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler()
	{
		return new SimpleAsyncUncaughtExceptionHandler();
	}

	@Bean
	@DependsOn( { "transactionManager" } )
	public ApplicationService applicationService()
	{
		return new ApplicationService();
	}

	@Bean( name = "securityService" )
	@DependsOn( { "sessionFactory" } )
	public SecurityService securityService()
	{
		return new SecurityService();
	}

	@Bean( name = "templateService" )
	@DependsOn( { "transactionManager" } )
	public TemplateService templateService()
	{
		return new TemplateService();
	}
}
