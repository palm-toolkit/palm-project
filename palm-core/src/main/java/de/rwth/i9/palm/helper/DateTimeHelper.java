package de.rwth.i9.palm.helper;

public class DateTimeHelper
{
	public static long substractTimeStampToHours( final java.sql.Timestamp currentTime, final java.sql.Timestamp oldTime )
	{
		long milliseconds1 = oldTime.getTime();
		long milliseconds2 = currentTime.getTime();

		long diff = milliseconds2 - milliseconds1;
		long diffHours = diff / ( 60 * 60 * 1000 );

		return diffHours;
	}

	public static long substractTimeStampToMinutes( final java.sql.Timestamp currentTime, final java.sql.Timestamp oldTime )
	{
		long milliseconds1 = oldTime.getTime();
		long milliseconds2 = currentTime.getTime();

		long diff = milliseconds2 - milliseconds1;
		long diffMinutes = diff / ( 60 * 1000 );

		return diffMinutes;
	}
}
