package de.rwth.i9.palm.util;

import java.util.UUID;

public class IdentifierFactory
{
	public static String getNextDefaultIdentifier()
	{
		return UUID.randomUUID().toString();
	}
}
