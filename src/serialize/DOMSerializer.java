package serialize;


import java.io.IOException;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;


/**
 * Interface for a DOM serializer implementation.
 * <p>
 * The DOM serializer is a facet of a serializer. A serializer may or may
 * not support a DOM serializer.
 * <p>
 * Example:
 * <pre>
 * Document     doc;
 * Serializer   ser;
 * OutputStream os;
 *
 * ser.setOutputStream( os );
 * ser.asDOMSerializer( doc );
 * </pre>
 * 
 *
 * @version Alpha
 * @author <a href="mailto:Scott_Boag/CAM/Lotus@lotus.com">Scott Boag</a>
 * @author <a href="mailto:arkin@exoffice.com">Assaf Arkin</a>
 */
public interface DOMSerializer
{
    
    
    /**
     * Serializes the DOM element. Throws an exception only if an I/O
     * exception occured while serializing.
     *
     * @param elem The element to serialize
     * @throws IOException An I/O exception occured while serializing
     */
    public void serialize( Element elem )
        throws IOException;
    
    
    /**
     * Serializes the DOM document. Throws an exception only if an I/O
     * exception occured while serializing.
     *
     * @param doc The document to serialize
     * @throws IOException An I/O exception occured while serializing
     */
    public void serialize( Document doc )
        throws IOException;
    
    
    /**
     * Serializes the DOM document fragment. Throws an exception only
     * if an I/O exception occured while serializing.
     *
     * @param frag The document fragment to serialize
     * @throws IOException An I/O exception occured while serializing
     */
    public void serialize( DocumentFragment frag )
        throws IOException;
    

}



