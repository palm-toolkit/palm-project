package de.rwth.i9.palm.persistence.relational;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import de.rwth.i9.palm.persistence.AuthorDAO;
import de.rwth.i9.palm.persistence.AuthorInterestDAO;
import de.rwth.i9.palm.persistence.AuthorInterestProfileDAO;
import de.rwth.i9.palm.persistence.AuthorSourceDAO;
import de.rwth.i9.palm.persistence.AuthorTopicModelingDAO;
import de.rwth.i9.palm.persistence.AuthorTopicModelingProfileDAO;
import de.rwth.i9.palm.persistence.CircleDAO;
import de.rwth.i9.palm.persistence.CircleInterestDAO;
import de.rwth.i9.palm.persistence.CircleInterestProfileDAO;
import de.rwth.i9.palm.persistence.CircleTopicModelingDAO;
import de.rwth.i9.palm.persistence.CircleTopicModelingProfileDAO;
import de.rwth.i9.palm.persistence.CircleWidgetDAO;
import de.rwth.i9.palm.persistence.ConfigDAO;
import de.rwth.i9.palm.persistence.ConfigPropertyDAO;
import de.rwth.i9.palm.persistence.CountryDAO;
import de.rwth.i9.palm.persistence.EventDAO;
import de.rwth.i9.palm.persistence.EventGroupDAO;
import de.rwth.i9.palm.persistence.EventInterestDAO;
import de.rwth.i9.palm.persistence.EventInterestProfileDAO;
import de.rwth.i9.palm.persistence.ExtractionServiceDAO;
import de.rwth.i9.palm.persistence.ExtractionServicePropertyDAO;
import de.rwth.i9.palm.persistence.FunctionDAO;
import de.rwth.i9.palm.persistence.InstantiableDAO;
import de.rwth.i9.palm.persistence.InstitutionDAO;
import de.rwth.i9.palm.persistence.InterestDAO;
import de.rwth.i9.palm.persistence.InterestProfileCircleDAO;
import de.rwth.i9.palm.persistence.InterestProfileDAO;
import de.rwth.i9.palm.persistence.InterestProfileEventDAO;
import de.rwth.i9.palm.persistence.InterestProfilePropertyDAO;
import de.rwth.i9.palm.persistence.LocationDAO;
import de.rwth.i9.palm.persistence.PersistenceStrategy;
import de.rwth.i9.palm.persistence.PublicationAuthorDAO;
import de.rwth.i9.palm.persistence.PublicationDAO;
import de.rwth.i9.palm.persistence.PublicationFileDAO;
import de.rwth.i9.palm.persistence.PublicationHistoryDAO;
import de.rwth.i9.palm.persistence.PublicationSourceDAO;
import de.rwth.i9.palm.persistence.PublicationTopicDAO;
import de.rwth.i9.palm.persistence.RoleDAO;
import de.rwth.i9.palm.persistence.SessionDataSetDAO;
import de.rwth.i9.palm.persistence.SourceDAO;
import de.rwth.i9.palm.persistence.SourcePropertyDAO;
import de.rwth.i9.palm.persistence.SubjectDAO;
import de.rwth.i9.palm.persistence.TopicModelingAlgorithmAuthorDAO;
import de.rwth.i9.palm.persistence.TopicModelingAlgorithmCircleDAO;
import de.rwth.i9.palm.persistence.UserAuthorBookmarkDAO;
import de.rwth.i9.palm.persistence.UserCircleBookmarkDAO;
import de.rwth.i9.palm.persistence.UserDAO;
import de.rwth.i9.palm.persistence.UserEventGroupBookmarkDAO;
import de.rwth.i9.palm.persistence.UserPublicationBookmarkDAO;
import de.rwth.i9.palm.persistence.UserRequestDAO;
import de.rwth.i9.palm.persistence.UserWidgetDAO;
import de.rwth.i9.palm.persistence.WidgetDAO;

/**
 * {@inheritDoc}
 *
 */
public class PersistenceStrategyImpl implements PersistenceStrategy
{
	@Autowired
	private SessionFactory sessionFactory;

	private final Map<String, InstantiableDAO> daoMap = new HashMap<String, InstantiableDAO>();

	public void setSessionFactory( SessionFactory sessionFactory )
	{
		this.sessionFactory = sessionFactory;
	}

	public void registerDAO( String name, InstantiableDAO dao )
	{
		if ( name == null || name.trim().equals( "" ) )
			return;
		if ( dao == null )
		{
			return;
		}
		else
		{
			daoMap.put( name, dao );
			return;
		}
	}

	@Autowired( required = false )
	private AuthorDAO authorDAO;

	@Autowired( required = false )
	private AuthorInterestDAO authorInterestDAO;

	@Autowired( required = false )
	private AuthorInterestProfileDAO authorInterestProfileDAO;

	@Autowired( required = false )
	private AuthorSourceDAO authorSourceDAO;

	@Autowired( required = false )
	private AuthorTopicModelingDAO authorTopicModelingDAO;

	@Autowired( required = false )
	private AuthorTopicModelingProfileDAO authorTopicModelingProfileDAO;

	@Autowired( required = false )
	private CircleDAO circleDAO;

	@Autowired( required = false )
	private CircleInterestDAO circleInterestDAO;

	@Autowired( required = false )
	private CircleInterestProfileDAO circleInterestProfileDAO;

	@Autowired( required = false )
	private CircleTopicModelingDAO circleTopicModelingDAO;

	@Autowired( required = false )
	private CircleTopicModelingProfileDAO circleTopicModelingProfileDAO;

	@Autowired( required = false )
	private CircleWidgetDAO circleWidgetDAO;

	@Autowired( required = false )
	private CountryDAO countryDAO;

	@Autowired( required = false )
	private ConfigDAO configDAO;

	@Autowired( required = false )
	private ConfigPropertyDAO configPropertyDAO;

	@Autowired( required = false )
	private EventDAO eventDAO;

	@Autowired( required = false )
	private EventInterestDAO eventInterestDAO;

	@Autowired( required = false )
	private EventInterestProfileDAO eventInterestProfileDAO;

	@Autowired( required = false )
	private EventGroupDAO eventGroupDAO;

	@Autowired( required = false )
	private ExtractionServiceDAO extractionServiceDAO;

	@Autowired( required = false )
	private ExtractionServicePropertyDAO extractionServicePropertyDAO;

	@Autowired( required = false )
	private FunctionDAO functionDAO;

	@Autowired( required = false )
	private InstitutionDAO institutionDAO;

	@Autowired( required = false )
	private InterestDAO interestDAO;

	@Autowired( required = false )
	private InterestProfileDAO interestProfileDAO;

	@Autowired( required = false )
	private InterestProfileCircleDAO interestProfileCircleDAO;

	@Autowired( required = false )
	private InterestProfileEventDAO interestProfileEventDAO;

	@Autowired( required = false )
	private InterestProfilePropertyDAO interestProfilePropertyDAO;

	@Autowired( required = false )
	private LocationDAO locationDAO;

	@Autowired( required = false )
	private PublicationDAO publicationDAO;

	@Autowired( required = false )
	private PublicationAuthorDAO publicationAuthorDAO;

	@Autowired( required = false )
	private PublicationFileDAO publicationFileDAO;

	@Autowired( required = false )
	private PublicationHistoryDAO publicationHistoryDAO;

	@Autowired( required = false )
	private PublicationSourceDAO publicationSourceDAO;

	@Autowired( required = false )
	private PublicationTopicDAO publicationTopicDAO;

	@Autowired( required = false )
	private RoleDAO roleDAO;

	@Autowired( required = false )
	private SessionDataSetDAO sessionDataSetDAO;
	
	@Autowired( required = false )
	private SourceDAO sourceDAO;

	@Autowired( required = false )
	private SourcePropertyDAO sourcePropertyDAO;

	@Autowired( required = false )
	private SubjectDAO subjectDAO;

	@Autowired( required = false )
	private TopicModelingAlgorithmCircleDAO topicModelingAlgorithmCircleDAO;

	@Autowired( required = false )
	private TopicModelingAlgorithmAuthorDAO topicModelingAlgorithmAuthorDAO;

	@Autowired( required = false )
	private UserAuthorBookmarkDAO userAuthorBookmarkDAO;

	@Autowired( required = false )
	private UserCircleBookmarkDAO userCircleBookmarkDAO;

	@Autowired( required = false )
	private UserDAO userDAO;

	@Autowired( required = false )
	private UserEventGroupBookmarkDAO userEventGroupBookmarkDAO;

	@Autowired( required = false )
	private UserPublicationBookmarkDAO userPublicationBookmarkDAO;

	@Autowired( required = false )
	private UserRequestDAO userRequestDAO;

	@Autowired( required = false )
	private UserWidgetDAO userWidgetDAO;

	@Autowired( required = false )
	private WidgetDAO widgetDAO;

	@Override
	public AuthorDAO getAuthorDAO()
	{
		if ( this.authorDAO == null )
			this.authorDAO = new AuthorDAOHibernate( this.sessionFactory );

		return this.authorDAO;
	}

	@Override
	public AuthorInterestDAO getAuthorInterestDAO()
	{
		if ( this.authorInterestDAO == null )
			this.authorInterestDAO = new AuthorInterestDAOHibernate( this.sessionFactory );

		return this.authorInterestDAO;
	}

	@Override
	public AuthorInterestProfileDAO getAuthorInterestProfileDAO()
	{
		if ( this.authorInterestProfileDAO == null )
			this.authorInterestProfileDAO = new AuthorInterestProfileDAOHibernate( this.sessionFactory );

		return this.authorInterestProfileDAO;
	}

	@Override
	public AuthorSourceDAO getAuthorSourceDAO()
	{
		if ( this.authorSourceDAO == null )
			this.authorSourceDAO = new AuthorSourceDAOHibernate( this.sessionFactory );

		return this.authorSourceDAO;
	}

	@Override
	public AuthorTopicModelingDAO getAuthorTopicModelingDAO()
	{
		if ( this.authorTopicModelingDAO == null )
			this.authorTopicModelingDAO = new AuthorTopicModelingDAOHibernate( this.sessionFactory );

		return this.authorTopicModelingDAO;
	}

	@Override
	public AuthorTopicModelingProfileDAO getAuthorTopicModelingProfileDAO()
	{
		if ( this.authorTopicModelingProfileDAO == null )
			this.authorTopicModelingProfileDAO = new AuthorTopicModelingProfileDAOHibernate( this.sessionFactory );

		return this.authorTopicModelingProfileDAO;
	}

	@Override
	public CircleDAO getCircleDAO()
	{
		if ( this.circleDAO == null )
			this.circleDAO = new CircleDAOHibernate( this.sessionFactory );

		return this.circleDAO;
	}

	@Override
	public CircleInterestDAO getCircleInterestDAO()
	{
		if ( this.circleInterestDAO == null )
			this.circleInterestDAO = new CircleInterestDAOHibernate( this.sessionFactory );

		return this.circleInterestDAO;
	}

	@Override
	public CircleInterestProfileDAO getCircleInterestProfileDAO()
	{
		if ( this.circleInterestProfileDAO == null )
			this.circleInterestProfileDAO = new CircleInterestProfileDAOHibernate( this.sessionFactory );

		return this.circleInterestProfileDAO;
	}

	@Override
	public CircleTopicModelingDAO getCircleTopicModelingDAO()
	{
		if ( this.circleTopicModelingDAO == null )
			this.circleTopicModelingDAO = new CircleTopicModelingDAOHibernate( this.sessionFactory );

		return this.circleTopicModelingDAO;
	}

	@Override
	public CircleTopicModelingProfileDAO getCircleTopicModelingProfileDAO()
	{
		if ( this.circleTopicModelingProfileDAO == null )
			this.circleTopicModelingProfileDAO = new CircleTopicModelingProfileDAOHibernate( this.sessionFactory );

		return this.circleTopicModelingProfileDAO;
	}

	@Override
	public CircleWidgetDAO getCircleWidgetDAO()
	{
		if ( this.circleWidgetDAO == null )
			this.circleWidgetDAO = new CircleWidgetDAOHibernate( this.sessionFactory );

		return this.circleWidgetDAO;
	}

	@Override
	public ConfigDAO getConfigDAO()
	{
		if ( this.configDAO == null )
			this.configDAO = new ConfigDAOHibernate( this.sessionFactory );

		return this.configDAO;
	}

	@Override
	public ConfigPropertyDAO getConfigPropertyDAO()
	{
		if ( this.configPropertyDAO == null )
			this.configPropertyDAO = new ConfigPropertyDAOHibernate( this.sessionFactory );

		return this.configPropertyDAO;
	}

	@Override
	public CountryDAO getCountryDAO()
	{
		if ( this.countryDAO == null )
			this.countryDAO = new CountryDAOHibernate( this.sessionFactory );

		return this.countryDAO;
	}

	@Override
	public EventDAO getEventDAO()
	{
		if ( this.eventDAO == null )
			this.eventDAO = new EventDAOHibernate( this.sessionFactory );

		return this.eventDAO;
	}

	@Override
	public EventInterestDAO getEventInterestDAO()
	{
		if ( this.eventInterestDAO == null )
			this.eventInterestDAO = new EventInterestDAOHibernate( this.sessionFactory );

		return this.eventInterestDAO;
	}

	@Override
	public EventInterestProfileDAO getEventInterestProfileDAO()
	{
		if ( this.eventInterestProfileDAO == null )
			this.eventInterestProfileDAO = new EventInterestProfileDAOHibernate( this.sessionFactory );

		return this.eventInterestProfileDAO;
	}

	@Override
	public EventGroupDAO getEventGroupDAO()
	{
		if ( this.eventGroupDAO == null )
			this.eventGroupDAO = new EventGroupDAOHibernate( this.sessionFactory );

		return this.eventGroupDAO;
	}

	@Override
	public ExtractionServiceDAO getExtractionServiceDAO()
	{
		if ( this.extractionServiceDAO == null )
			this.extractionServiceDAO = new ExtractionServiceDAOHibernate( this.sessionFactory );

		return this.extractionServiceDAO;
	}

	@Override
	public ExtractionServicePropertyDAO getExtractionServicePropertyDAO()
	{
		if ( this.extractionServicePropertyDAO == null )
			this.extractionServicePropertyDAO = new ExtractionServicePropertyDAOHibernate( this.sessionFactory );

		return this.extractionServicePropertyDAO;
	}

	@Override
	public FunctionDAO getFunctionDAO()
	{
		if ( this.functionDAO == null )
			this.functionDAO = new FunctionDAOHibernate( this.sessionFactory );

		return this.functionDAO;
	}

	@Override
	public InstitutionDAO getInstitutionDAO()
	{
		if ( this.institutionDAO == null )
			this.institutionDAO = new InstitutionDAOHibernate( this.sessionFactory );

		return this.institutionDAO;
	}

	@Override
	public InterestDAO getInterestDAO()
	{
		if ( this.interestDAO == null )
			this.interestDAO = new InterestDAOHibernate( this.sessionFactory );

		return this.interestDAO;
	}

	@Override
	public InterestProfileDAO getInterestProfileDAO()
	{
		if ( this.interestProfileDAO == null )
			this.interestProfileDAO = new InterestProfileDAOHibernate( this.sessionFactory );

		return this.interestProfileDAO;
	}

	@Override
	public InterestProfileCircleDAO getInterestProfileCircleDAO()
	{
		if ( this.interestProfileCircleDAO == null )
			this.interestProfileCircleDAO = new InterestProfileCircleDAOHibernate( this.sessionFactory );

		return this.interestProfileCircleDAO;
	}

	@Override
	public InterestProfileEventDAO getInterestProfileEventDAO()
	{
		if ( this.interestProfileEventDAO == null )
			this.interestProfileEventDAO = new InterestProfileEventDAOHibernate( this.sessionFactory );

		return this.interestProfileEventDAO;
	}

	@Override
	public InterestProfilePropertyDAO getInterestProfilePropertyDAO()
	{
		if ( this.interestProfilePropertyDAO == null )
			this.interestProfilePropertyDAO = new InterestProfilePropertyDAOHibernate( this.sessionFactory );

		return this.interestProfilePropertyDAO;
	}

	@Override
	public LocationDAO getLocationDAO()
	{
		if ( this.locationDAO == null )
			this.locationDAO = new LocationDAOHibernate( this.sessionFactory );

		return this.locationDAO;
	}

	@Override
	public PublicationDAO getPublicationDAO()
	{
		if ( this.publicationDAO == null )
			this.publicationDAO = new PublicationDAOHibernate( this.sessionFactory );

		return this.publicationDAO;
	}

	@Override
	public PublicationAuthorDAO getPublicationAuthorDAO()
	{
		if ( this.publicationAuthorDAO == null )
			this.publicationAuthorDAO = new PublicationAuthorDAOHibernate( this.sessionFactory );

		return this.publicationAuthorDAO;
	}

	@Override
	public PublicationFileDAO getPublicationFileDAO()
	{
		if ( this.publicationFileDAO == null )
			this.publicationFileDAO = new PublicationFileDAOHibernate( this.sessionFactory );

		return this.publicationFileDAO;
	}

	@Override
	public PublicationSourceDAO getPublicationSourceDAO()
	{
		if ( this.publicationSourceDAO == null )
			this.publicationSourceDAO = new PublicationSourceDAOHibernate( this.sessionFactory );

		return this.publicationSourceDAO;
	}

	@Override
	public PublicationHistoryDAO getPublicationHistoryDAO()
	{
		if ( this.publicationHistoryDAO == null )
			this.publicationHistoryDAO = new PublicationHistoryDAOHibernate( this.sessionFactory );

		return this.publicationHistoryDAO;
	}

	@Override
	public PublicationTopicDAO getPublicationTopicDAO()
	{
		if ( this.publicationTopicDAO == null )
			this.publicationTopicDAO = new PublicationTopicDAOHibernate( this.sessionFactory );

		return this.publicationTopicDAO;
	}

	@Override
	public RoleDAO getRoleDAO()
	{
		if ( this.roleDAO == null )
			this.roleDAO = new RoleDAOHibernate( this.sessionFactory );

		return this.roleDAO;
	}
	
	@Override
	public SessionDataSetDAO getSessionDataSetDAO()
	{
		if ( this.sessionDataSetDAO == null )
			this.sessionDataSetDAO = new SessionDataSetDAOHibernate( this.sessionFactory );

		return this.sessionDataSetDAO;
	}

	@Override
	public SourceDAO getSourceDAO()
	{
		if ( this.sourceDAO == null )
			this.sourceDAO = new SourceDAOHibernate( this.sessionFactory );

		return this.sourceDAO;
	}

	@Override
	public SourcePropertyDAO getSourcePropertyDAO()
	{
		if ( this.sourcePropertyDAO == null )
			this.sourcePropertyDAO = new SourcePropertyDAOHibernate( this.sessionFactory );

		return this.sourcePropertyDAO;
	}

	@Override
	public SubjectDAO getSubjectDAO()
	{
		if ( this.subjectDAO == null )
			this.subjectDAO = new SubjectDAOHibernate( this.sessionFactory );

		return this.subjectDAO;
	}

	@Override
	public TopicModelingAlgorithmAuthorDAO getTopicModelingAlgorithmAuthorDAO()
	{
		if ( this.topicModelingAlgorithmAuthorDAO == null )
			this.topicModelingAlgorithmAuthorDAO = new TopicModelingAlgorithmAuthorDAOHibernate( this.sessionFactory );

		return this.topicModelingAlgorithmAuthorDAO;
	}

	@Override
	public TopicModelingAlgorithmCircleDAO getTopicModelingAlgorithmCircleDAO()
	{
		if ( this.topicModelingAlgorithmCircleDAO == null )
			this.topicModelingAlgorithmCircleDAO = new TopicModelingAlgorithmCircleDAOHibernate( this.sessionFactory );

		return this.topicModelingAlgorithmCircleDAO;
	}

	@Override
	public UserAuthorBookmarkDAO getUserAuthorBookmarkDAO()
	{
		if ( this.userAuthorBookmarkDAO == null )
			this.userAuthorBookmarkDAO = new UserAuthorBookmarkDAOHibernate( this.sessionFactory );

		return this.userAuthorBookmarkDAO;
	}

	@Override
	public UserCircleBookmarkDAO getUserCircleBookmarkDAO()
	{
		if ( this.userCircleBookmarkDAO == null )
			this.userCircleBookmarkDAO = new UserCircleBookmarkDAOHibernate( this.sessionFactory );

		return this.userCircleBookmarkDAO;
	}

	@Override
	public UserDAO getUserDAO()
	{
		if ( this.userDAO == null )
			this.userDAO = new UserDAOHibernate( this.sessionFactory );

		return this.userDAO;
	}

	@Override
	public UserEventGroupBookmarkDAO getUserEventGroupBookmarkDAO()
	{
		if ( this.userEventGroupBookmarkDAO == null )
			this.userEventGroupBookmarkDAO = new UserEventGroupBookmarkDAOHibernate( this.sessionFactory );

		return this.userEventGroupBookmarkDAO;
	}

	@Override
	public UserPublicationBookmarkDAO getUserPublicationBookmarkDAO()
	{
		if ( this.userPublicationBookmarkDAO == null )
			this.userPublicationBookmarkDAO = new UserPublicationBookmarkDAOHibernate( this.sessionFactory );

		return this.userPublicationBookmarkDAO;
	}

	@Override
	public UserRequestDAO getUserRequestDAO()
	{
		if ( this.userRequestDAO == null )
			this.userRequestDAO = new UserRequestDAOHibernate( this.sessionFactory );

		return this.userRequestDAO;
	}

	@Override
	public UserWidgetDAO getUserWidgetDAO()
	{
		if ( this.userWidgetDAO == null )
			this.userWidgetDAO = new UserWidgetDAOHibernate( this.sessionFactory );

		return this.userWidgetDAO;
	}

	@Override
	public WidgetDAO getWidgetDAO()
	{
		if ( this.widgetDAO == null )
			this.widgetDAO = new WidgetDAOHibernate( this.sessionFactory );

		return this.widgetDAO;
	}

}