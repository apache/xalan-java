package serialize.helpers;


import serialize.OutputFormat;
import serialize.Method;


/**
 * Output format for text documents.
 * <p>
 * The output format affects the manner in which a document is
 * serialized. The output format determines the output method,
 * encoding, indentation, document type, and various other properties
 * that affect the manner in which a document is serialized.
 *
 * @version Alpha
 * @author <a href="mailto:arkin@exoffice.com">Assaf Arkin</a>
 */
public class TextOutputFormat
    extends OutputFormat
{


    public TextOutputFormat()
    {
        setMethod( Method.Text );
        setMediaType( "text/plain" );
        setPreserveSpace( true );
    }


    public TextOutputFormat( String encoding )
    {
        this();
        setEncoding( encoding );
    }


    public TextOutputFormat( boolean indenting )
    {
        this();
        setIndenting( indenting );
        setPreserveSpace( false );
    }


}

