package serialize.helpers;


import serialize.OutputFormat;
import serialize.Method;


/**
 * Output format for XHTML documents.
 * <p>
 * The output format affects the manner in which a document is
 * serialized. The output format determines the output method,
 * encoding, indentation, document type, and various other properties
 * that affect the manner in which a document is serialized.
 *
 * @version Alpha
 * @author <a href="mailto:arkin@exoffice.com">Assaf Arkin</a>
 */
public class XHTMLOutputFormat
    extends OutputFormat
{


    public XHTMLOutputFormat()
    {
        setMethod( Method.XHTML );
        setMediaType( "text/html" );
        setOmitXMLDeclaration( true );
        setPreserveSpace( false );
        setDoctypePublicId( "-//W3C//DTD XHTML 1.0 Strict//EN" );
        setDoctypeSystemId( "http://www.w3.org/TR/WD-html-in-xml/DTD/xhtml1-strict.dtd" );
    }


    public XHTMLOutputFormat( String encoding )
    {
        this();
        setEncoding( encoding );
    }


    public XHTMLOutputFormat( boolean indenting )
    {
        this();
        setIndenting( indenting );
    }


}

