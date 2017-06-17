package de.rwth.i9.palm.helper.comparator;

import java.util.Comparator;

import de.rwth.i9.palm.model.ConfigProperty;

public class ConfigPropertyByPositionComparator implements Comparator<ConfigProperty>
{

	@Override
	public int compare( final ConfigProperty configProperty1, final ConfigProperty configProperty2 )
	{
		if ( configProperty1 == null && configProperty2 == null )
			return 0;

		if ( configProperty1 == null )
			return 1;

		if ( configProperty2 == null )
			return -1;

		int position1 = configProperty1.getPosition();
		int position2 = configProperty2.getPosition();

		if ( position1 < position2 )
			return -1;

		if ( position1 > position2 )
			return 1;

		return 0;
	}

}