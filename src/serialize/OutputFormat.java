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
 * The output format affects the manner in which a document is
 * serialized. The output format determines the output method,
 * encoding, indentation, document type, and various other properties
 * that affect the manner in which a document is serialized.
 * <p>
 * Once an output format has been handed to a serializer or XSLT
 * processor, the application should not attempt to reuse it. The
 * serializer or XSLT processor may modify the properties of the
 * output format object.
 * <p>
 * Implementations may provide classes that extend <tt>OutputFormat</tt>
 * with additional properties, e.g. indentation level, line separation,
 * namespace handlers, etc. An application may use these extra properties
 * by constructing an output format object based on the implementation
 * specified type.
 * <p>
 * <tt>OutputFormat</tt> has been modeled after the XSLT &lt;xsl:output&gt;
 * element declaration. However, it does not assume the existence of an
 * XSLT processor or a particular serializer.
 * <p>
 * Typical usage scenarios supported by <tt>OutputFormat<tt>:
 * <ul>
 * <li>The application constructs an <tt>OutputFormat</tt> object and
 *     passes it to the serializer
 * <li>The application constructs an <tt>OutputFormat</tt> object and
 *     passes it to the XSLT processor, overriding the properties
 *     specified in the stylesheet
 * <li>The XSLT processor constructs an <tt>OutputFormat</tt> object
 *     and passes it to the serializer
 * <li>The XSLT processor constructs an <tt>OutputFormat</tt> object
 *     from the stylesheet and returns it to the applicatio, the
 *     application passes <tt>OutputFormat</tt> to the serializer
 * </ul>
 *
 * @version Alpha
 * @author <a href="mailto:arkin@exoffice.com">Assaf Arkin</a>
 *         <a href="mailto:visco@exoffice.com">Keith Visco</a>
 * @see Method
 */
public class OutputFormat
{


    /**
     * Holds the output method specified for this document,
     * or null if no method was specified.
     *
     * @see Method
     */
    private String _method = Method.XML;


    /**
     * Specifies the version of the output method, null for the
     * default.
     */
    private String _version = null;


    /**
     * True if indentation is requested, false for no indentation.
     */
    private boolean _indent = false;


    /**
     * The encoding to use, if an input stream is used, UTF-8 for
     * the default.
     */
    private String _encoding = "UTF-8";


    /**
     * The specified media type or null.
     */
    private String _mediaType = null;


    /**
     * The specified document type system identifier, or null.
     */
    private String _doctypeSystemId = null;


    /**
     * The specified document type public identifier, or null.
     */
    private String _doctypePublicId = null;


    /**
     * Ture if the XML declaration should be ommited;
     */
    private boolean _omitXmlDeclaration = false;


    /**
     * List of element tag names whose text node children must
     * be output as CDATA.
     */
    private QName[] _cdataElements = new QName[ 0 ];


    /**
     * List of element tag names whose text node children must
     * be output unescaped.
     */
    private QName[] _nonEscapingElements = new QName[ 0 ];


    /**
     * True if spaces should be preserved in elements that do not
     * specify otherwise, or specify the default behavior.
     */
    private boolean _preserve = false;

    /**
     * True if the document type should be marked as standalone.
     */
    private boolean _standalone = false;


    /**
     * Constructs a new output format with the default values.
     */
    public OutputFormat()
    {
    }


    /**
     * Returns the method specified for this output format. See {@link
     * Method} for a list of the default methods. Other methods should
     * be of the format <tt>namespace:local</tt>. The default is
     * {@link Method#XML}.
     *
     * @return The specified output method
     */
    public String getMethod()
    {
        return _method;
    }
    
    
    /**
     * Sets the method for this output format. See {@link Method} for
     * a list of the default methods. Other methods should be of the
     * format <tt>namespace:local</tt>.
     *
     * @param method The output method, or null
     */
    public void setMethod( String method )
    {
        _method = method;
    }


    /**
     * Returns the version for this output method. If no version was
     * specified, will return null and the default version number will
     * be used. If the serializer does not support that particular
     * version, it should default to a supported version.
     *
     * @return The specified method version, or null
     */
    public String getVersion()
    {
        return _version;
    }


    /**
     * Sets the version for this output method.
     *
     * @param version The output method version, or null
     */
    public void setVersion( String version )
    {
        _version = version;
    }


    /**
     * Returns true if indentation was specified. If no indentation
     * was specified, returns false. A derived class may support
     * additional properties, e.g. indentation level, line width to
     * wrap at, tab/spaces, etc.
     *
     * @return True if indentation was specified
     */
    public boolean getIndent()
    {
        return _indent;
    }


    /**
     * Sets the indentation on and off. A derived class may support
     * additional properties, e.g. indentation level, line width to
     * wrap at, tab/spaces, etc. A serializer need not support
     * indentation.
     *
     * @param ident True specifies identiation
     */
    public void setIndent( boolean indent )
    {
        _indent = indent;
    }


    /**
     * Returns the specified encoding. If no encoding was specified,
     * the default is used. For XML and HTML the default would be
     * "UTF-8". For other output methods, the default encoding is
     * unspecified.
     *
     * @return The encoding
     */
    public String getEncoding()
    {
        return _encoding;
    }


    /**
     * Sets the encoding for this output method. Null means the
     * default encoding for the selected output method. For XML and
     * HTML the default would be "UTF-8". For other output methods,
     * the default encoding is unspecified.
     *
     * @param encoding The encoding, or null
     */
    public void setEncoding( String encoding )
    {
        _encoding = encoding;
    }


    /**
     * Returns the specified media type. For each output method a
     * default media type will be used if one was not specified.
     *
     * @return The specified media type, or null
     */
    public String getMediaType()
    {
        return _mediaType;
    }


    /**
     * Sets the media type. For each output method a default media
     * type will be used if one was not specified.
     *
     * @param mediaType The specified media type
     */
    public void setMediaType( String mediaType )
    {
        _mediaType = mediaType;
    }


    /**
     * Sets the document type public identifiers. If not specified the
     * document type will depend on the output method (e.g. HTML, XHTML)
     * or from some other mechanism (e.g. SAX events, DOM DocumentType).
     *
     * @param publicId The public identifier
     */
    public void setDoctypePublicId( String publicId )
    {
        _doctypePublicId = publicId;
    }


    /**
     * Returns the specified document type public identifier,
     * or null.
     */
    public String getDoctypePublicId()
    {
        return _doctypePublicId;
    }
    
    
    /**
     * Sets the document type system identifiers. If not specified the
     * document type will depend on the output method (e.g. HTML, XHTML)
     * or from some other mechanism (e.g. SAX events, DOM DocumentType).
     *
     * @param systemId The system identifier
     */
    public void setDoctypeSystemId( String systemId )
    {
        _doctypeSystemId = systemId;
    }


    /**
     * Returns the specified document type system identifier,
     * or null.
     */
    public String getDoctypeSystemId()
    {
        return _doctypeSystemId;
    }
    
    /**
     * Returns true if the document type is standalone.
     * The default is false.
     */
    public boolean getStandalone()
    {
        return _standalone;
    }


    /**
     * Sets document DTD standalone. The public and system
     * identifiers must be null for the document to be
     * serialized as standalone.
     *
     * @param standalone True if document DTD is standalone
     */
    public void setStandalone( boolean standalone )
    {
        _standalone = standalone;
    }


    /**
     * Returns true if the XML document declaration should
     * be ommited. The default is false.
     */
    public boolean getOmitXMLDeclaration()
    {
        return _omitXmlDeclaration;
    }
    
    
    /**
     * Sets XML declaration omitting on and off.
     *
     * @param omit True if XML declaration should be ommited
     */
    public void setOmitXMLDeclaration( boolean omit )
    {
        _omitXmlDeclaration = omit;
    }
    
    
    /**
     * Returns a list of all the elements whose text node children
     * should be output as CDATA. Returns an empty array if no such
     * elements were specified.
     *
     * @return List of all CDATA elements
     */
    public QName[] getCDataElements()
    {
        return _cdataElements;
    }
    
    
    /**
     * Sets the list of elements for which text node children
     * should be output as CDATA.
     *
     * @param cdataElements List of all CDATA elements
     */
    public void setCDataElements( QName[] cdataElements )
    {
        if ( cdataElements == null )
            _cdataElements = new QName[ 0 ];
        else
            _cdataElements = cdataElements;
    }
    
    
    /**
     * Returns a list of all the elements whose text node children
     * should be output unescaped (no character references). Returns
     * an empty array if no such elements were specified.
     *
     * @return List of all non escaping elements
     */
    public QName[] getNonEscapingElements()
    {
        return _nonEscapingElements;
    }
    
    
    /**
     * Sets the list of elements for which text node children
     * should be output unescaped (no character references).
     *
     * @param nonEscapingElements List of all non-escaping elements
     */
    public void setNonEscapingElements( QName[] nonEscapingElements )
    {
        if ( nonEscapingElements == null )
            _nonEscapingElements = new QName[ 0 ];
        else
            _nonEscapingElements = nonEscapingElements;
    }
    
    
    
    /**
     * Returns true if the default behavior for this format is to
     * preserve spaces. All elements that do not specify otherwise
     * or specify the default behavior will be formatted based on
     * this rule. All elements that specify space preserving will
     * always preserve space.
     */
    public boolean getPreserveSpace()
    {
        return _preserve;
    }
    
    
    /**
     * Sets space preserving as the default behavior. The default is
     * space stripping and all elements that do not specify otherwise
     * or use the default value will not preserve spaces.
     *
     * @param preserve True if spaces should be preserved
     */
    public void setPreserveSpace( boolean preserve )
    {
        _preserve = preserve;
    }


}

