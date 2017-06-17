package de.rwth.i9.palm.feature.publication;

import java.util.Set;

import de.rwth.i9.palm.model.Publication;

public interface PublicationDelete
{
	public void deletePublication( Publication publication );

	public void deletePublications( Set<Publication> publications );
}
