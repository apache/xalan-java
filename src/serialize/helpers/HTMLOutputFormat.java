package serialize.helpers;


import serialize.OutputFormat;
import serialize.Method;


/**
 * Output format for HTML documents.
 * <p>
 * The output format affects the manner in which a document is
 * serialized. The output format determines the output method,
 * encoding, indentation, document type, and various other properties
 * that affect the manner in which a document is serialized.
 *
 * @version Alpha
 * @author <a href="mailto:arkin@exoffice.com">Assaf Arkin</a>
 */
public class HTMLOutputFormat
    extends OutputFormat
{


    public HTMLOutputFormat()
    {
        setMethod( Method.HTML );
        setMediaType( "text/html" );
        setOmitXMLDeclaration( true );
        setPreserveSpace( false );
        setDoctypePublicId( "-//W3C//DTD HTML 4.0//EN" );
        setDoctypeSystemId( "http://www.w3.org/TR/WD-html-in-xml/DTD/xhtml1-strict.dtd" );
    }


    public HTMLOutputFormat( String encoding )
    {
        this();
        setEncoding( encoding );
    }


    public HTMLOutputFormat( boolean indenting )
    {
        this();
        setIndenting( indenting );
    }


}

