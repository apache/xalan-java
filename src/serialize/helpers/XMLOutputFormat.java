package serialize.helpers;


import serialize.OutputFormat;
import serialize.Method;


/**
 * Output format for XML documents.
 * <p>
 * The output format affects the manner in which a document is
 * serialized. The output format determines the output method,
 * encoding, indentation, document type, and various other properties
 * that affect the manner in which a document is serialized.
 *
 * @version Alpha
 * @author <a href="mailto:arkin@exoffice.com">Assaf Arkin</a>
 */
public class XMLOutputFormat
    extends OutputFormat
{


    public XMLOutputFormat()
    {
        setMethod( Method.XML );
        setMediaType( "text/xml" );
        setPreserveSpace( true );
    }


    public XMLOutputFormat( String encoding )
    {
        this();
        setEncoding( encoding );
    }


    public XMLOutputFormat( boolean indenting )
    {
        this();
        setIndenting( indenting );
        setPreserveSpace( false );
    }


}

