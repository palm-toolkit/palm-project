package de.rwth.i9.palm.interestmining.service;

import java.util.regex.Pattern;

import org.junit.Ignore;
import org.junit.Test;

import de.rwth.i9.palm.utils.Inflector;

public class InflectorTest
{

	@Test
	public void inflectorTest()
	{
		String text = "Semantic Mashups";

		Inflector inflector = new Inflector();
		System.out.println( inflector.singularize( text ) );
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
	@Ignore
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
