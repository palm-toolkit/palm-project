package de.rwth.i9.palm.pattern;

import java.util.regex.Pattern;

import org.junit.Ignore;
import org.junit.Test;

public class SimplePatternTest
{

	@Test
	@Ignore
	public void test()
	{
		String text = "This is the text to be searched " + "for occurrences of the pattern.";

		String pattern = ".*is.*";

		boolean matches = Pattern.matches( pattern, text );

		System.out.println( "matches = " + matches );
	}

	@Test
	@Ignore
	public void test2()
	{
		String text = "A sep Text sep With sep Many sep Separators";

		String patternString = "sep";
		Pattern pattern = Pattern.compile( patternString );

		String[] split = pattern.split( text );

		System.out.println( "split.length = " + split.length );

		for ( String element : split )
		{
			System.out.println( "element = " + element );
		}
	}

	@Test
	public void test3()
	{
		String text = "A sep Text sep With sep Many sep Separators";

		String patternString = "sep";
		Pattern pattern = Pattern.compile( patternString );

		String[] split = pattern.split( text );

		System.out.println( "split.length = " + split.length );

		for ( String element : split )
		{
			System.out.println( "element = " + element );
		}
	}

}
