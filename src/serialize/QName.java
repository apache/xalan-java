/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights 
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:  
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Xalan" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 1999, Lotus
 * Development Corporation., http://www.lotus.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
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
