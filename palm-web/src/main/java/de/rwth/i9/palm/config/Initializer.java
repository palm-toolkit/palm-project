package de.rwth.i9.palm.config;

import javax.servlet.Filter;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

@Order(1)
public class Initializer extends AbstractAnnotationConfigDispatcherServletInitializer {
	
	@Override
	@Profile( value = "dev" )
	protected Class<?>[] getRootConfigClasses() {
		return new Class[] { WebSecurityConfig.class, DatabaseConfig.class, CachingConfig.class };
	}
	
	@Override
	protected Class<?>[] getServletConfigClasses() {
	return new Class<?>[] { WebAppConfig.class };
	}
	
	@Override
	protected String[] getServletMappings() {
	return new String[] { "/" };
	}

	@Override
	protected Filter[] getServletFilters()
	{
		return new Filter[] { new HiddenHttpMethodFilter() };
	}

	@Override
	protected void registerDispatcherServlet( ServletContext servletContext )
	{
		super.registerDispatcherServlet( servletContext );

		servletContext.addListener( new HttpSessionEventPublisher() );

	}

	@Override
	public void onStartup( ServletContext servletContext ) throws ServletException
	{
		super.onStartup( servletContext );
		servletContext.addListener( new SessionListener() );
	}

}