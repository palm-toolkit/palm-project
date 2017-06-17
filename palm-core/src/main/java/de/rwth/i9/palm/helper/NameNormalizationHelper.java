package de.rwth.i9.palm.helper;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class NameNormalizationHelper
{
	public static String normalizeName( String name )
	{
		// check name length after normalization to ASCII
		String nameAscii = name.replaceAll( "[^-a-zA-Z ]", "" );

		if ( nameAscii.length() == name.length() )
		{
			return name;
		}
		else
		{

			@SuppressWarnings( "serial" )
			Map<Character, String> LIGATURES = new HashMap<Character, String>()
			{
				{
					put( 'ä', "ae" );
					put( 'ü', "ue" );
					put( 'ö', "oe" );
					put( 'ß', "ss" );
					put( 'Æ', "AE" );
					put( 'æ', "ae" );
					put( 'œ', "oe" );
					put( 'þ', "th" );
					put( 'ĳ', "ij" );
					put( 'ð', "dh" );
					put( 'Æ', "AE" );
					put( 'Œ', "OE" );
					put( 'Þ', "TH" );
					put( 'Ð', "DH" );
					put( 'Ĳ', "IJ" );
				}
			};

			// name combination 1
			StringBuilder sb = new StringBuilder();
			for ( int i = 0; i < name.length(); i++ )
			{
				char c = name.charAt( i );
				String l = LIGATURES.get( c );
				if ( l != null )
				{
					sb.append( l );
				}
				else if ( c < 0xc0 )
				{
					sb.append( c ); // ASCII and C1 control codes
				}
				else
				{
					// anything else, including diacritics
					l = Normalizer.normalize( Character.toString( c ), Normalizer.Form.NFKD ).replaceAll( "[\\p{InCombiningDiacriticalMarks}]+", "" );
					sb.append( l );
				}
			}

			// name combination 2
			String nfdNormalizedString = Normalizer.normalize( name, Normalizer.Form.NFD );
			Pattern pattern = Pattern.compile( "\\p{InCombiningDiacriticalMarks}+" );
			String mainName = pattern.matcher( nfdNormalizedString ).replaceAll( "" );

			return mainName.replaceAll( "[^a-zA-Z ]", "" );
		}
	}
}
