package serialize;


/**
 * Names of the four default output methods.
 * <p>
 * Four default output methods are defined: XML, HTML, XHTML and TEXT.
 * Serializers may support additional output methods. The names of
 * these output methods should be encoded as <tt>namespace:local</tt>.
 *
 * @version Alpha
 * @author <a href="mailto:arkin@exoffice.com">Assaf Arkin</a>
 * @see OutputFormat
 */
public final class Method
{
    
    
    /**
     * The output method for XML documents: <tt>xml</tt>.
     */
    public static final String XML = "xml";
    
    
    /**
     * The output method for HTML documents: <tt>html</tt>.
     */
    public static final String HTML = "html";
    
    
    /**
     * The output method for XHTML documents: <tt>xhtml</tt>.
     */
    public static final String XHTML = "xhtml";
    
    
    /**
     * The output method for text documents: <tt>text</tt>.
     */
    public static final String Text = "text";
    
    
}


