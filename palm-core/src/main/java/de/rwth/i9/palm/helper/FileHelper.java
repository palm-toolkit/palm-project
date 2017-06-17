package de.rwth.i9.palm.helper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileHelper
{
	public static String getResourceAsString( String filename ) throws Exception
	{
		InputStream in = ClassLoader.getSystemResourceAsStream( filename );
		if ( in == null )
		{
			in = Thread.currentThread().getContextClassLoader().getResourceAsStream( filename );
		}

		InputStreamReader isr = new InputStreamReader( in, "UTF-8" );
		BufferedReader br = new BufferedReader( isr );
		StringBuffer sb = new StringBuffer();
		for ( int c = br.read(); c != -1; c = br.read() )
			sb.append( (char) c );
		return sb.toString();
	}

	public static long t0 = 0;

	public static long showMemTime( String msg )
	{
		long t1 = System.currentTimeMillis();
		long max = Runtime.getRuntime().maxMemory() / 1024 / 1024;
		long fre = Runtime.getRuntime().freeMemory();
		long tot = Runtime.getRuntime().totalMemory();
		long use = ( tot - fre ) / 1024L / 1024L;
		String pattern = "%4dms - %4d / %4d M - %s";
		if ( t0 == 0 )
		{
			System.out.println( String.format( pattern, 0, use, max, msg ) );
			t0 = System.currentTimeMillis();
			return 0L;
		}
		else
		{
			long t = t1 - t0;
			System.out.println( String.format( pattern, t, use, max, msg ) );
			t0 = System.currentTimeMillis();
			return t;
		}

	}
}
