package de.rwth.i9.palm.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import de.rwth.i9.palm.model.Author;
import de.rwth.i9.palm.model.AuthorAlias;
import de.rwth.i9.palm.model.AuthorInterest;
import de.rwth.i9.palm.model.AuthorInterestProfile;
import de.rwth.i9.palm.model.AuthorTopicModeling;
import de.rwth.i9.palm.model.AuthorTopicModelingProfile;
import de.rwth.i9.palm.model.Circle;
import de.rwth.i9.palm.model.CircleInterest;
import de.rwth.i9.palm.model.CircleInterestProfile;
import de.rwth.i9.palm.model.CircleTopicModeling;
import de.rwth.i9.palm.model.CircleTopicModelingProfile;
import de.rwth.i9.palm.model.CircleWidget;
import de.rwth.i9.palm.model.Config;
import de.rwth.i9.palm.model.ConfigProperty;
import de.rwth.i9.palm.model.Country;
import de.rwth.i9.palm.model.Event;
import de.rwth.i9.palm.model.EventGroup;
import de.rwth.i9.palm.model.EventInterest;
import de.rwth.i9.palm.model.EventInterestProfile;
import de.rwth.i9.palm.model.ExtractionService;
import de.rwth.i9.palm.model.ExtractionServiceProperty;
import de.rwth.i9.palm.model.Function;
import de.rwth.i9.palm.model.Institution;
import de.rwth.i9.palm.model.Interest;
import de.rwth.i9.palm.model.InterestAuthor;
import de.rwth.i9.palm.model.InterestProfile;
import de.rwth.i9.palm.model.InterestProfileCircle;
import de.rwth.i9.palm.model.InterestProfileEvent;
import de.rwth.i9.palm.model.InterestProfileProperty;
import de.rwth.i9.palm.model.Location;
import de.rwth.i9.palm.model.Publication;
import de.rwth.i9.palm.model.PublicationAuthor;
import de.rwth.i9.palm.model.PublicationFile;
import de.rwth.i9.palm.model.PublicationHistory;
import de.rwth.i9.palm.model.PublicationSource;
import de.rwth.i9.palm.model.PublicationTopic;
import de.rwth.i9.palm.model.Role;
import de.rwth.i9.palm.model.Source;
import de.rwth.i9.palm.model.SourceProperty;
import de.rwth.i9.palm.model.Subject;
import de.rwth.i9.palm.model.TopicModelingAlgorithmAuthor;
import de.rwth.i9.palm.model.TopicModelingAlgorithmCircle;
import de.rwth.i9.palm.model.User;
import de.rwth.i9.palm.model.UserAuthorBookmark;
import de.rwth.i9.palm.model.UserCircleBookmark;
import de.rwth.i9.palm.model.UserEventGroupBookmark;
import de.rwth.i9.palm.model.UserPublicationBookmark;
import de.rwth.i9.palm.model.UserRequest;
import de.rwth.i9.palm.model.UserWidget;
import de.rwth.i9.palm.model.WeightingAlgorithm;
import de.rwth.i9.palm.model.Widget;
import de.rwth.i9.palm.persistence.relational.PersistenceStrategyImpl;

@Configuration
@EnableTransactionManagement
@ComponentScan( "de.rwth.i9" )
@PropertySource( value = "classpath:database.dev.properties" )
@Lazy( true )
public class DatabaseConfig
{
	private static final String PROPERTY_NAME_DATABASE_DRIVER = "db.driver";
	private static final String PROPERTY_NAME_DATABASE_PASSWORD = "db.password";
	private static final String PROPERTY_NAME_DATABASE_URL = "db.url";
	private static final String PROPERTY_NAME_DATABASE_USERNAME = "db.username";

	private static final String PROPERTY_NAME_HIBERNATE_DIALECT = "hibernate.dialect";
	private static final String PROPERTY_NAME_HIBERNATE_SHOW_SQL = "hibernate.show_sql";
	private static final String PROPERTY_NAME_HIBERNATE_FORMAT_SQL = "hibernate.format_sql";
	private static final String PROPERTY_NAME_HIBERNATE_HBM2DDL_AUTO = "hibernate.hbm2ddl.auto";
	//private static final String PROPERTY_NAME_HIBERNATE_CONNECTION_AUTOCOMMIT = "hibernate.connection.autocommit";
	private static final String PROPERTY_NAME_HIBERNATE_CONNECTION_CHARSET = "hibernate.connection.CharSet";
	private static final String PROPERTY_NAME_HIBERNATE_CONNECTION_CHARACTERENCODING = "hibernate.connection.characterEncoding";
	private static final String PROPERTY_NAME_HIBERNATE_CONNECTION_USEUNICODE = "hibernate.connection.useUnicode";

	private static final String PROPERTY_NAME_ENTITYMANAGER_PACKAGES_TO_SCAN = "entitymanager.packages.to.scan";
	private static final String PROPERTY_NAME_HIBERNATE_SEARCH_DEFAULT_DIRECTORY_PROVIDER = "hibernate.search.default.directory_provider";
	private static final String PROPERTY_NAME_HIBERNATE_SEARCH_DEFAULT_INDEXBASE = "hibernate.search.default.indexBase";
	private static final String PROPERTY_NAME_HIBERNATE_SEARCH_LUCENE_VERSION = "hibernate.search.lucene_version";

	@Autowired
	private Environment env;

	@Bean( destroyMethod = "close" )
	public DataSource dataSource()
	{
		BasicDataSource dataSource = new BasicDataSource();

		dataSource.setDriverClassName( env.getRequiredProperty( PROPERTY_NAME_DATABASE_DRIVER ) );
		dataSource.setUrl( env.getRequiredProperty( PROPERTY_NAME_DATABASE_URL ) );
		dataSource.setUsername( env.getRequiredProperty( PROPERTY_NAME_DATABASE_USERNAME ) );
		dataSource.setPassword( env.getRequiredProperty( PROPERTY_NAME_DATABASE_PASSWORD ) );
		dataSource.setValidationQuery( "select 1" );

		return dataSource;
	}

	@Bean
	public LazyConnectionDataSourceProxy lazyConnectionDataSourceProxy()
	{
		LazyConnectionDataSourceProxy lazyConnectionDataSourceProxy = new LazyConnectionDataSourceProxy();
		lazyConnectionDataSourceProxy.setTargetDataSource( this.dataSource() );
		return lazyConnectionDataSourceProxy;
	}

	private Properties hibProperties()
	{
		Properties properties = new Properties();
		properties.put( PROPERTY_NAME_HIBERNATE_DIALECT, env.getRequiredProperty( PROPERTY_NAME_HIBERNATE_DIALECT ) );
		properties.put( PROPERTY_NAME_HIBERNATE_SHOW_SQL, env.getRequiredProperty( PROPERTY_NAME_HIBERNATE_SHOW_SQL ) );
		properties.put( PROPERTY_NAME_HIBERNATE_FORMAT_SQL, env.getRequiredProperty( PROPERTY_NAME_HIBERNATE_FORMAT_SQL ) );
		properties.put( PROPERTY_NAME_HIBERNATE_HBM2DDL_AUTO, env.getRequiredProperty( PROPERTY_NAME_HIBERNATE_HBM2DDL_AUTO ) );
		// properties.put(PROPERTY_NAME_HIBERNATE_CONNECTION_AUTOCOMMIT,
		// env.getRequiredProperty(PROPERTY_NAME_HIBERNATE_CONNECTION_AUTOCOMMIT));
		properties.put( PROPERTY_NAME_HIBERNATE_CONNECTION_CHARSET, env.getRequiredProperty( PROPERTY_NAME_HIBERNATE_CONNECTION_CHARSET ) );
		properties.put( PROPERTY_NAME_HIBERNATE_CONNECTION_CHARACTERENCODING, env.getRequiredProperty( PROPERTY_NAME_HIBERNATE_CONNECTION_CHARACTERENCODING ) );
		properties.put( PROPERTY_NAME_HIBERNATE_CONNECTION_USEUNICODE, env.getRequiredProperty( PROPERTY_NAME_HIBERNATE_CONNECTION_USEUNICODE ) );

		properties.put( PROPERTY_NAME_HIBERNATE_SEARCH_DEFAULT_DIRECTORY_PROVIDER, env.getRequiredProperty( PROPERTY_NAME_HIBERNATE_SEARCH_DEFAULT_DIRECTORY_PROVIDER ) );
		properties.put( PROPERTY_NAME_HIBERNATE_SEARCH_DEFAULT_INDEXBASE, env.getRequiredProperty( PROPERTY_NAME_HIBERNATE_SEARCH_DEFAULT_INDEXBASE ) );
		properties.put( PROPERTY_NAME_HIBERNATE_SEARCH_LUCENE_VERSION, env.getRequiredProperty( PROPERTY_NAME_HIBERNATE_SEARCH_LUCENE_VERSION ) );
		return properties;
	}

	@Bean
	public HibernateTransactionManager transactionManager()
	{
		HibernateTransactionManager transactionManager = new HibernateTransactionManager();
		transactionManager.setSessionFactory( sessionFactory().getObject() );
		return transactionManager;
	}

	@Bean
	public LocalSessionFactoryBean sessionFactory()
	{
		LocalSessionFactoryBean sessionFactoryBean = new LocalSessionFactoryBean();
		sessionFactoryBean.setDataSource( this.lazyConnectionDataSourceProxy() );
		sessionFactoryBean.setPackagesToScan( env.getRequiredProperty( PROPERTY_NAME_ENTITYMANAGER_PACKAGES_TO_SCAN ) );
		sessionFactoryBean.setHibernateProperties( hibProperties() );
		sessionFactoryBean.setAnnotatedClasses( new Class<?>[] { 
				/* model class here */
				Author.class,
				AuthorAlias.class,
				AuthorInterest.class,
				AuthorInterestProfile.class,
				AuthorTopicModeling.class, 
				AuthorTopicModelingProfile.class,
				Circle.class,
				CircleInterest.class,
				CircleInterestProfile.class,
				CircleTopicModeling.class,
				CircleTopicModelingProfile.class,
				CircleWidget.class,
				Country.class,
				Config.class,
				ConfigProperty.class,
				Event.class,
				EventInterest.class,
				EventInterestProfile.class,
				EventGroup.class,
				ExtractionService.class,
				ExtractionServiceProperty.class,
				Function.class,
				Institution.class,
				Interest.class,
				InterestAuthor.class,
				InterestProfile.class,
				InterestProfileCircle.class,
				InterestProfileEvent.class,
				InterestProfileProperty.class,
				Location.class,
				Publication.class,
				PublicationAuthor.class,
				PublicationFile.class,
				PublicationHistory.class,
				PublicationSource.class,
				PublicationTopic.class,
				Role.class,
 				Source.class,
 				SourceProperty.class,
 				Subject.class,
 				TopicModelingAlgorithmAuthor.class,
 				TopicModelingAlgorithmCircle.class,
				User.class,
				UserAuthorBookmark.class,
				UserCircleBookmark.class,
				UserEventGroupBookmark.class,
				UserPublicationBookmark.class,
				UserRequest.class,
				UserWidget.class,
				Widget.class,
				WeightingAlgorithm.class
		} );
		return sessionFactoryBean;
	}

	@Bean
	@Scope( "singleton" )
	public PersistenceStrategyImpl configPersistenceStrategyImpl()
	{
		PersistenceStrategyImpl persistenceStrategyImpl = new PersistenceStrategyImpl();
		persistenceStrategyImpl.setSessionFactory( sessionFactory().getObject() );
		return persistenceStrategyImpl;
	}

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer()
	{
		return new PropertySourcesPlaceholderConfigurer();
	}
}