package serialize;


/**
 * A qualified name. A qualified name has a local name, a namespace
 * URI and a prefix (if known). A <tt>QName</tt> may also specify
 * a non-qualified name by having a null namespace URI.
 *
 * @version Alpha
 * @author <a href="mailto:arkin@exoffice.com">Assaf Arkin</a>
 */
public class QName
{


    /**
     * The local name.
     */
    private String _localName;


    /**
     * The namespace URI.
     */
    private String _namespaceURI;


    /**
     * The namespace prefix.
     */
    private String _prefix;



    /**
     * Constructs a new QName with the specified namespace URI and
     * local name.
     *
     * @param namespaceURI The namespace URI if known, or null
     * @param localName The local name
     */
    public QName( String namespaceURI, String localName )
    {
        if ( localName == null )
            throw new IllegalArgumentException( "Argument 'localName' is null" );
        _namespaceURI = namespaceURI;
        _localName = localName;
    }


    /**
     * Constructs a new QName with the specified namespace URI, prefix
     * and local name.
     *
     * @param namespaceURI The namespace URI if known, or null
     * @param prefix The namespace prefix is known, or null
     * @param localName The local name
     */
    public QName( String namespaceURI, String prefix, String localName )
    {
        if ( localName == null )
            throw new IllegalArgumentException( "Argument 'localName' is null" );
        _namespaceURI = namespaceURI;
        _prefix = prefix;
        _localName = localName;
    }


    /**
     * Returns the namespace URI. Returns null if the namespace URI
     * is not known.
     *
     * @return The namespace URI, or null
     */
    public String getNamespaceURI()
    {
        return _namespaceURI;
    }


    /**
     * Returns the namespace prefix. Returns null if the namespace
     * prefix is not known.
     *
     * @return The namespace prefix, or null
     */
    public String getPrefix()
    {
        return _prefix;
    }


    /**
     * Returns the local part of the qualified name.
     *
     * @return The local part of the qualified name
     */
    public String getLocalName()
    {
        return _localName;
    }


    public boolean equals( Object object )
    {
        if ( object == this )
            return true;
        if ( object instanceof QName ) {
            return ( ( ( _localName == null && ( (QName) object )._localName == null ) ||
                       ( _localName != null && _localName.equals( ( (QName) object )._localName ) ) ) &&
                     ( ( _namespaceURI == null && ( (QName) object )._namespaceURI == null ) ||
                       ( _namespaceURI != null && _namespaceURI.equals( ( (QName) object )._namespaceURI ) ) ) &&
                     ( ( _prefix == null && ( (QName) object )._prefix == null ) ||
                       ( _prefix != null && _prefix.equals( ( (QName) object )._prefix ) ) ) );
    
        }
        return false;
    }


    public String toString()
    {
        return _prefix != null ? ( _prefix + ":" + _localName ) :
            ( _namespaceURI != null ? ( _namespaceURI + "^" + _localName ) :
              _localName );
    }


}
