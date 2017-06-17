package de.rwth.i9.palm.feature.publication;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.rwth.i9.palm.model.Author;
import de.rwth.i9.palm.model.Circle;
import de.rwth.i9.palm.model.Publication;
import de.rwth.i9.palm.model.PublicationAuthor;
import de.rwth.i9.palm.persistence.PersistenceStrategy;

@Component
public class PublicationDeleteImpl implements PublicationDelete
{
	@Autowired
	private PersistenceStrategy persistenceStrategy;

	@Override
	public void deletePublication( Publication publication )
	{
		// remove publication connection

		// check with circle
		// get list of circle that contain this publication
		// remove links
		Set<Circle> circles = publication.getCircles();

		for ( Circle circle : circles )
		{
			circle.removePublication( publication );
		}
		publication.getCircles().clear();

		// remove bookmark
		publication.getUserPublicationBookmarks().clear();
		// remove publication author
		for ( PublicationAuthor pa : publication.getPublicationAuthors() )
		{
			Author author = pa.getAuthor();
			author.removePublicationAuthor( pa );

			// calculate publication and citation
			author.reCalculateNumberOfPublicationAndCitation();

			pa.setAuthor( null );
			pa.setPublication( null );
		}
		publication.getPublicationAuthors().clear();
		publication.setEvent( null );

		persistenceStrategy.getPublicationDAO().delete( publication );
	}

	@Override
	public void deletePublications( Set<Publication> publications )
	{
		if ( publications == null || publications.isEmpty() )
			return;

		for ( Publication publication : publications )
			this.deletePublication( publication );

	}

}
