/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES{} LOSS OF
 * USE, DATA, OR PROFITS{} OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 2001, Sun
 * Microsystems., http://www.sun.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 * @author Santiago Pericas-Geertsen
 * @author G. Todd Miller 
 *
 */
package org.apache.xml.serializer;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.Transformer;

import org.apache.xml.res.XMLErrorResources;
import org.apache.xml.res.XMLMessages;
import org.apache.xml.utils.BoolStack;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


/**
 * This class acts as a base class for the XML "serializers"
 * and the stream serializers.
 * It contains a number of common fields and methods.
 */
abstract public class SerializerBase
    implements SerializationHandler, SerializerConstants, org.apache.xml.dtm.ref.dom2dtm.DOM2DTM.CharacterNodeHandler
{
    

    /**
     * To fire off the end element trace event
     * @param name Name of element
     */
    protected void fireEndElem(String name)
        throws org.xml.sax.SAXException
    {
        flushMyWriter();
        if (m_tracer != null)
            m_tracer.fireGenerateEvent(SerializerTrace.EVENTTYPE_ENDELEMENT,name, (Attributes)null);     	        	    	
    }

    /**
     * Report the characters trace event
     * @param chars  content of characters
     * @param start  starting index of characters to output
     * @param length  number of characters to output
     */
    protected void fireCharEvent(char[] chars, int start, int length)
        throws org.xml.sax.SAXException
    {
        flushMyWriter();
        if (m_tracer != null)
            m_tracer.fireGenerateEvent(SerializerTrace.EVENTTYPE_CHARACTERS, chars, start,length);     	        	    	
    }

    /**
     * true if we still need to call startDocumentInternal() 
	 */
    protected boolean m_needToCallStartDocument = true; 
    
    /**
     * Set to true when a start tag is started, or open, but not all the
     * attributes or namespace information is yet collected.
     */
    protected boolean m_startTagOpen = false;

    /**
     * Keeps track of the nesting depth of elements inside each other.
     * This is the nesting depth of the current element being processed.
     */
    int m_currentElemDepth = 0;

    /**
     * The cached value of the QName of the current element
     */
    protected String m_elementName = null;
    
    /**
     * The cached value of the local name of the current element
     */
    protected String m_elementLocalName = null;
    
    /**
     * The cached value of the URI of the current element
     */
    protected String m_elementURI = null;


    /** True if a trailing "]]>" still needs to be written to be
     * written out. Used to merge adjacent CDATA sections
     */
    protected boolean m_cdataTagOpen = false;

    /**
     * All the attributes of the current element, collected from
     * startPrefixMapping() calls, or addAddtribute() calls, or 
     * from the SAX attributes in a startElement() call.
     */
    protected AttributesImplSerializer m_attributes = new AttributesImplSerializer();

    /**
     * Tells if we're in an EntityRef event.
     */
    protected boolean m_inEntityRef = false;

    /** This flag is set while receiving events from the external DTD */
    protected boolean m_inExternalDTD = false;

    /** When an element starts, if its name is in the cdata-section-names list
     * then push a True on the stack, otherwise push a False.
     * at the end of an element the value is poped.
     * It is True if the text of the element should be in CDATA section blocks. 
     */
    protected BoolStack m_cdataSectionStates = new BoolStack();

    /**
     * The System ID for the doc type.
     */
    private String m_doctypeSystem;

    /**
     * The public ID for the doc type.
     */
    private String m_doctypePublic;

    /**
     * Flag to tell that we need to add the doctype decl, which we can't do
     * until the first element is encountered.
     */
    boolean m_needToOutputDocTypeDecl = true;

    /**
     * The character encoding.  Must match the encoding used for the
     * printWriter.
     */
    private String m_encoding = null;

    /** 
     * The top of this stack contains an id of the element that last declared
     * a namespace. Used to ensure prefix/uri map scopes are closed correctly
     */
    protected Stack m_nodeStack;

    /** 
     * The top of this stack is the prefix that was last mapped to an URI
     */
    protected Stack m_prefixStack;

    /**
     * Tells if we should write the XML declaration.
     */
    private boolean m_shouldNotWriteXMLHeader = false;

    /**
     * The standalone value for the doctype.
     */
    private String m_standalone;

    /**
     * True if standalone was specified.
     */
    protected boolean m_standaloneWasSpecified = false;

    /**
     * Flag to tell if indenting (pretty-printing) is on.
     */
    protected boolean m_doIndent = false;
    /**
     * Amount to indent.
     */
    protected int m_indentAmount = 0;

    /**
     * Tells the XML version, for writing out to the XML decl.
     */
    private String m_version = null;

    /**
     * The mediatype.  Not used right now.
     */
    private String m_mediatype;

    /**
     * The transformer that was around when this output handler was created (if
     * any).
     */
    private Transformer m_transformer;

    /** 
     * Pairs of local names and corresponding URIs of CDATA sections. This list
     * comes from the cdata-section-elements attribute. Every second one is a
     * local name, and every other second one is the URI for the local name. 
     */
    protected Vector m_cdataSectionElements = null;

    /**
     * Namespace support, that keeps track of currently defined 
     * prefix/uri mappings. As processed elements come and go, so do
     * the associated mappings for that element.
     */
    protected NamespaceMappings m_prefixMap;
    
    /**
     * Handle for firing generate events.  This interface may be implemented
     * by the referenced transformer object.
     */
    protected SerializerTrace m_tracer;
    
    protected SourceLocator m_sourceLocator;
    

    /**
     * The writer to send output to. This field is only used in the ToStream
     * serializers, but exists here just so that the fireStartDoc() and
     * other fire... methods can flush this writer when tracing.
     */
    protected java.io.Writer m_writer = null;    

    /**
     * Receive notification of a comment.
     * 
     * @see org.apache.xml.serializer.ExtendedLexicalHandler#comment(String)
     */
    public void comment(String data) throws SAXException
    {
        this.comment(data.toCharArray(), 0, data.length());
    }

    /**
      * TODO: This method is a HACK! Since XSLTC does not have access to the
      * XML file, it sometimes generates a NS prefix of the form "ns?" for
      * an attribute. If at runtime, when the qname of the attribute is
      * known, another prefix is specified for the attribute, then we can get
      * a qname of the form "ns?:otherprefix:name". This function patches the
      * qname by simply ignoring "otherprefix".
      */
    protected String patchName(String qname)
    {

        
        final int lastColon = qname.lastIndexOf(':');

        if (lastColon > 0) {
            final int firstColon = qname.indexOf(':');
            final String prefix = qname.substring(0, firstColon);
            final String localName = qname.substring(lastColon + 1);

        // If uri is "" then ignore prefix
            final String uri = m_prefixMap.lookupNamespace(prefix);
            if (uri != null && uri.length() == 0) {
                return localName;
            }
            else if (firstColon != lastColon) {
                return prefix + ':' + localName;
            }
        }
        return qname;        
    }

    /**
     * Returns the local name of a qualified name. If the name has no prefix,
     * then it works as the identity (SAX2).
     * @param qname the qualified name 
     * @return the name, but excluding any prefix and colon.
     */
    protected static String getLocalName(String qname)
    {
        final int col = qname.lastIndexOf(':');
        return (col > 0) ? qname.substring(col + 1) : qname;
    }

    /**
     * Receive an object for locating the origin of SAX document events.
     *
     * @param locator An object that can return the location of any SAX document
     * event.
     * 
     * Receive an object for locating the origin of SAX document events.
     *
     * <p>SAX parsers are strongly encouraged (though not absolutely
     * required) to supply a locator: if it does so, it must supply
     * the locator to the application by invoking this method before
     * invoking any of the other methods in the DocumentHandler
     * interface.</p>
     *
     * <p>The locator allows the application to determine the end
     * position of any document-related event, even if the parser is
     * not reporting an error.  Typically, the application will
     * use this information for reporting its own errors (such as
     * character content that does not match an application's
     * business rules).  The information returned by the locator
     * is probably not sufficient for use with a search engine.</p>
     *
     * <p>Note that the locator will return correct information only
     * during the invocation of the events in this interface.  The
     * application should not attempt to use it at any other time.</p>
     */
    public void setDocumentLocator(Locator locator)
    {
        return;

        // I don't do anything with this yet.
    }

    /**
     * Adds the given attribute to the set of collected attributes , but only if
     * there is a currently open element.
     * 
     * An element is currently open if a startElement() notification has
     * occured but the start of the element has not yet been written to the
     * output.  In the stream case this means that we have not yet been forced
     * to close the elements opening tag by another notification, such as a
     * character notification.
     * 
     * @param uri the URI of the attribute
     * @param localName the local name of the attribute
     * @param rawName    the qualified name of the attribute
     * @param type the type of the attribute (probably CDATA)
     * @param value the value of the attribute
     * @see org.apache.xml.serializer.ExtendedContentHandler#addAttribute(String, String, String, String, String)
     */
    public void addAttribute(
        String uri,
        String localName,
        String rawName,
        String type,
        String value)
        throws SAXException
    {
        if (m_startTagOpen)
        {
            addAttributeAlways(uri, localName, rawName, type, value);
        }

    }
    
    /**
     * Adds the given attribute to the set of attributes, even if there is
     * no currently open element. This is useful if a SAX startPrefixMapping()
     * should need to add an attribute before the element name is seen.
     * 
     * @param uri the URI of the attribute
     * @param localName the local name of the attribute
     * @param rawName   the qualified name of the attribute
     * @param type the type of the attribute (probably CDATA)
     * @param value the value of the attribute
     */
    public void addAttributeAlways(
        String uri,
        String localName,
        String rawName,
        String type,
        String value)
    {
//            final int index =
//                (localName == null || uri == null) ?
//                m_attributes.getIndex(rawName):m_attributes.getIndex(uri, localName);        
            int index;
//            if (localName == null || uri == null){
//                index = m_attributes.getIndex(rawName);
//            }
//            else {
//                index = m_attributes.getIndex(uri, localName);
//            }
            index = m_attributes.getIndex(rawName);
            if (index >= 0)
            {
                /* We've seen the attribute before.
                 * We may have a null uri or localName, but all
                 * we really want to re-set is the value anyway.
                 */
                m_attributes.setValue(index,value);
            }
            else
            {
                // the attribute doesn't exist yet, create it
                m_attributes.addAttribute(uri, localName, rawName, type, value);
            }
        
    }
  

    /**
     *  Adds  the given attribute to the set of collected attributes, 
     * but only if there is a currently open element.  This method is only
     * called by XSLTC.
     *
     * @param name the attribute's qualified name
     * @param value the value of the attribute
     */
    public void addAttribute(String name, final String value)
    {
        if (m_startTagOpen)
        {
            final String patchedName = patchName(name);
            final String localName = getLocalName(patchedName);
            final String uri = getNamespaceURI(patchedName, false);

            addAttributeAlways(uri,localName, patchedName, "CDATA", value);
         }
    }    


    /**
     * Add the given attributes to the currently collected ones. These
     * attributes are always added, regardless of whether on not an element
     * is currently open.
     * @param atts List of attributes to add to this list
     */
    public void addAttributes(Attributes atts) throws SAXException
    {

        int nAtts = atts.getLength();

        for (int i = 0; i < nAtts; i++)
        {
            String uri = atts.getURI(i);

            if (null == uri)
                uri = "";

            addAttributeAlways(
                uri,
                atts.getLocalName(i),
                atts.getQName(i),
                atts.getType(i),
                atts.getValue(i));

        }
    }

    /**
     * Return a {@link ContentHandler} interface into this serializer.
     * If the serializer does not support the {@link ContentHandler}
     * interface, it should return null.
     *
     * @return A {@link ContentHandler} interface into this serializer,
     *  or null if the serializer is not SAX 2 capable
     * @throws IOException An I/O exception occured
     */
    public ContentHandler asContentHandler() throws IOException
    {
        return this;
    }

    /**
     * Report the end of an entity.
     *
     * @param name The name of the entity that is ending.
     * @throws org.xml.sax.SAXException The application may raise an exception.
     * @see #startEntity
     */
    public void endEntity(String name) throws org.xml.sax.SAXException
    {
        if (name.equals("[dtd]"))
            m_inExternalDTD = false;
        m_inEntityRef = false;

        this.fireEndEntity(name);        
    }

    /**
     * Flush and close the underlying java.io.Writer. This method applies to
     * ToStream serializers, not ToSAXHandler serializers.
     * @see org.apache.xml.serializer.ToStream
     */
    public void close()
    {
        // do nothing (base behavior)
    }

    /**
     * Initialize global variables
     */
    protected void initCDATA()
    {
        // CDATA stack
        //        _cdataStack = new Stack();
        //        _cdataStack.push(new Integer(-1)); // push dummy value
    }

    /**
     * Returns the character encoding to be used in the output document.
     * @return the character encoding to be used in the output document.
     */
    public String getEncoding()
    {
        return m_encoding;
    }

   /**
     * Sets the character encoding coming from the xsl:output encoding stylesheet attribute.
     * @param encoding the character encoding
     */
    public void setEncoding(String m_encoding)
    {
        this.m_encoding = m_encoding;
    }

    /**
     * Sets the value coming from the xsl:output omit-xml-declaration stylesheet attribute
     * @param b true if the XML declaration is to be omitted from the output
     * document.
     */
    public void setOmitXMLDeclaration(boolean b)
    {
        this.m_shouldNotWriteXMLHeader = b;
    }


    /**
     * @return true if the XML declaration is to be omitted from the output
     * document.
     */
    public boolean getOmitXMLDeclaration()
    {
        return m_shouldNotWriteXMLHeader;
    }

    /**
     * Returns the previously set value of the value to be used as the public
     * identifier in the document type declaration (DTD).
     * 
     *@return the public identifier to be used in the DOCTYPE declaration in the
     * output document.
     */    
    public String getDoctypePublic()
    {
        return m_doctypePublic;
    }

    /** Set the value coming from the xsl:output doctype-public stylesheet attribute.
      * @param doctypePublic the public identifier to be used in the DOCTYPE
      * declaration in the output document.
      */
    public void setDoctypePublic(String doctypePublic)
    {
        this.m_doctypePublic = doctypePublic;
    }


    /**
     * Returns the previously set value of the value to be used
     * as the system identifier in the document type declaration (DTD).
	 * @return the system identifier to be used in the DOCTYPE declaration in
	 * the output document.
     *
     */    
    public String getDoctypeSystem()
    {
        return m_doctypeSystem;
    }

    /** Set the value coming from the xsl:output doctype-system stylesheet attribute.
      * @param doctypeSystem the system identifier to be used in the DOCTYPE
      * declaration in the output document.
      */
    public void setDoctypeSystem(String doctypeSystem)
    {
        this.m_doctypeSystem = doctypeSystem;
    }

    /** Set the value coming from the xsl:output doctype-public and doctype-system stylesheet properties
     * @param doctypeSystem the system identifier to be used in the DOCTYPE
     * declaration in the output document.
     * @param doctypePublic the public identifier to be used in the DOCTYPE
     * declaration in the output document.
     */
    public void setDoctype(String doctypeSystem, String doctypePublic)
    {
        this.m_doctypeSystem = doctypeSystem;
        this.m_doctypePublic = doctypePublic;
    }

    /**
     * Sets the value coming from the xsl:output standalone stylesheet attribute.
     * @param standalone a value of "yes" indicates that the
     * <code>standalone</code> delaration is to be included in the output
     * document. This method remembers if the value was explicitly set using
     * this method, verses if the value is the default value.
     */
    public void setStandalone(String standalone)
    {
        if (standalone != null)
        {
            m_standaloneWasSpecified = true;
            setStandaloneInternal(standalone);
        }
    }
    /**
     * Sets the XSL standalone attribute, but does not remember if this is a
     * default or explicite setting.
     * @param standalone "yes" | "no"
     */    
    protected void setStandaloneInternal(String standalone)
    {
        if ("yes".equals(standalone))
            m_standalone = "yes";
        else
            m_standalone = "no";
        
    }

    /**
     * Gets the XSL standalone attribute
     * @return a value of "yes" if the <code>standalone</code> delaration is to
     * be included in the output document.
     *  @see   org.apache.xml.serializer.XSLOutputAttributes#getStandalone()
     */
    public String getStandalone()
    {
        return m_standalone;
    }

    /**
     * @return true if the output document should be indented to visually
     * indicate its structure.
     */
    public boolean getIndent()
    {
        return m_doIndent;
    }
    /**
     * Gets the mediatype the media-type or MIME type associated with the output
     * document.
     * @return the mediatype the media-type or MIME type associated with the
     * output document.
     */
    public String getMediaType()
    {
        return m_mediatype;
    }

    /**
     * Gets the version of the output format.
     * @return the version of the output format.
     */
    public String getVersion()
    {
        return m_version;
    }

    /**
     * Sets the value coming from the xsl:output version attribute.
     * @param version the version of the output format.
     * @see org.apache.xml.serializer.SerializationHandler#setVersion(String)
     */
    public void setVersion(String version)
    {
        m_version = version;
    }

    /**
     * Sets the value coming from the xsl:output media-type stylesheet attribute.
     * @param mediaType the non-null media-type or MIME type associated with the
     * output document.
     * @see javax.xml.transform.OutputKeys#MEDIA_TYPE
     * @see org.apache.xml.serializer.SerializationHandler#setMediaType(String)
     */
    public void setMediaType(String mediaType)
    {
        m_mediatype = mediaType;
    }

    /**
     * @return the number of spaces to indent for each indentation level.
     */
    public int getIndentAmount()
    {
        return m_indentAmount;
    }

    /**
     * Sets the indentation amount.
     * @param m_indentAmount The m_indentAmount to set
     */
    public void setIndentAmount(int m_indentAmount)
    {
        this.m_indentAmount = m_indentAmount;
    }

    /**
     * Sets the value coming from the xsl:output indent stylesheet
     * attribute.
     * @param doIndent true if the output document should be indented to
     * visually indicate its structure.
     * @see org.apache.xml.serializer.XSLOutputAttributes#setIndent(boolean)
     */
    public void setIndent(boolean doIndent)
    {
        m_doIndent = doIndent;
    }

    /**
     * This method is used when a prefix/uri namespace mapping
     * is indicated after the element was started with a 
     * startElement() and before and endElement().
     * startPrefixMapping(prefix,uri) would be used before the
     * startElement() call.
     * @param uri the URI of the namespace
     * @param prefix the prefix associated with the given URI.
     * 
     * @see org.apache.xml.serializer.ExtendedContentHandler#namespaceAfterStartElement(String, String)
     */
    public void namespaceAfterStartElement(String uri, String prefix)
        throws SAXException
    {
        // default behavior is to do nothing
    }

    /**
     * Return a {@link DOMSerializer} interface into this serializer. If the
     * serializer does not support the {@link DOMSerializer} interface, it should
     * return null.
     *
     * @return A {@link DOMSerializer} interface into this serializer,  or null
     * if the serializer is not DOM capable
     * @throws IOException An I/O exception occured
     * @see org.apache.xml.serializer.Serializer#asDOMSerializer()
     */
    public DOMSerializer asDOMSerializer() throws IOException
    { 
        return null;
    }

    /**
     * Push a boolean state based on if the name of the element
     * is found in the list of qnames.  A state is always pushed,
     * one way or the other.
     *
     * @param namespaceURI Should be a non-null reference to the namespace URL
     *        of the element that owns the state, or empty string.
     * @param localName Should be a non-null reference to the local name
     *        of the element that owns the state.
     *
     * Hidden parameters are the vector of qualified elements specified in
     * cdata-section-names attribute, and the m_cdataSectionStates stack
     * onto which whether the current element is in the list is pushed (true or
     * false). Other hidden parameters are the current elements namespaceURI,
     * localName and qName
     */
    protected void pushCdataSectionState()
    {

        boolean b;

        if (null != m_cdataSectionElements)
        {
            b = false;
            if (m_elementLocalName == null)
                m_elementLocalName = getLocalName(m_elementName);
            if (m_elementURI == null)
            {
                String prefix = getPrefixPart(m_elementName);
                if (prefix != null)
                    m_elementURI = m_prefixMap.lookupNamespace(prefix);

            }

            if ((null != m_elementURI) && m_elementURI.length() == 0)
                m_elementURI = null;

            int nElems = m_cdataSectionElements.size();

            // loop through 2 at a time, as these are pairs of URI and localName
            for (int i = 0; i < nElems; i += 2)
            {
                String uri = (String) m_cdataSectionElements.elementAt(i);
                String loc = (String) m_cdataSectionElements.elementAt(i + 1);
                if (loc.equals(m_elementLocalName)
                    && subPartMatch(m_elementURI, uri))
                {
                    b = true;

                    break;
                }
            }
        }
        else
        {
            b = m_cdataSectionStates.peekOrFalse();
        }

        m_cdataSectionStates.push(b);
    }

    /**
     * Tell if two strings are equal, without worry if the first string is null.
     *
     * @param p String reference, which may be null.
     * @param t String reference, which may be null.
     *
     * @return true if strings are equal.
     */
    private static final boolean subPartMatch(String p, String t)
    {
        return (p == t) || ((null != p) && (p.equals(t)));
    }

    /**
     * Returns the local name of a qualified name. 
     * If the name has no prefix,
     * then it works as the identity (SAX2). 
     * 
     * @param qname a qualified name
     * @return returns the prefix of the qualified name,
     * or null if there is no prefix.
     */
    protected static final String getPrefixPart(String qname)
    {
        final int col = qname.indexOf(':');
        return (col > 0) ? qname.substring(0, col) : null;
        //return (col > 0) ? qname.substring(0,col) : "";
    }

    /**
     * Some users of the serializer may need the current namespace mappings
     * @return the current namespace mappings (prefix/uri)
     * @see org.apache.xml.serializer.ExtendedContentHandler#getNamespaceMappings()
     */
    public NamespaceMappings getNamespaceMappings()
    {
        return m_prefixMap;
    }

    /**
     * Returns the prefix currently pointing to the given URI (if any).
     * @param namespaceURI the uri of the namespace in question
     * @return a prefix pointing to the given URI (if any).
     * @see org.apache.xml.serializer.ExtendedContentHandler#getPrefix(String)
     */
    public String getPrefix(String namespaceURI)
    {
        String prefix = m_prefixMap.lookupPrefix(namespaceURI);
        return prefix;
    }

    /**
     * Returns the URI of an element or attribute. Note that default namespaces
     * do not apply directly to attributes.
     * @param qname a qualified name
     * @param isElement true if the qualified name is the name of 
     * an element.
     * @return returns the namespace URI associated with the qualified name.
     */
    public String getNamespaceURI(String qname, boolean isElement)
    {
        String uri = EMPTYSTRING;
        int col = qname.lastIndexOf(':');
        final String prefix = (col > 0) ? qname.substring(0, col) : EMPTYSTRING;

        if (!EMPTYSTRING.equals(prefix) || isElement)
        {
            if (m_prefixMap != null)
            {
                uri = m_prefixMap.lookupNamespace(prefix);
                if (uri == null && !prefix.equals(XMLNS_PREFIX))
                {
                    throw new RuntimeException(
                        XMLMessages.createXMLMessage(
                            XMLErrorResources.ER_NAMESPACE_PREFIX,
                            new Object[] { qname.substring(0, col) }  ));
                }
            }
        }
        return uri;
    }

    /**
     * Returns the URI of prefix (if any)
     * 
	 * @param prefix the prefix whose URI is searched for
     * @return the namespace URI currently associated with the
     * prefix, null if the prefix is undefined.
     */
    public String getNamespaceURIFromPrefix(String prefix)
    {
        String uri = null;
        if (m_prefixMap != null)
            uri = m_prefixMap.lookupNamespace(prefix);
        return uri;
    }

    /**
     * Entity reference event.
     *
     * @param name Name of entity
     *
     * @throws org.xml.sax.SAXException
     */
    public void entityReference(String name) throws org.xml.sax.SAXException
    {

        flushPending();

        startEntity(name);
        endEntity(name);

		fireEntityReference(name);
    }

    /**
     * Sets the transformer associated with this serializer
     * @param t the transformer associated with this serializer.
     * @see org.apache.xml.serializer.SerializationHandler#setTransformer(Transformer)
     */
    public void setTransformer(Transformer t)
    {
        m_transformer = t;
        
        // If this transformer object implements the SerializerTrace interface
        // then assign m_tracer to the transformer object so it can be used
        // to fire trace events.
        if ((m_transformer instanceof SerializerTrace) &&
            (((SerializerTrace) m_transformer).hasTraceListeners())) {
           m_tracer = (SerializerTrace) m_transformer;
        } else {
           m_tracer = null;
        }
    }
    /**
     * Gets the transformer associated with this serializer
     * @return returns the transformer associated with this serializer.
     * @see org.apache.xml.serializer.SerializationHandler#getTransformer()
     */
    public Transformer getTransformer()
    {
        return m_transformer;
    }
    
    /**
     * This method gets the nodes value as a String and uses that String as if
     * it were an input character notification.
     * @param node the Node to serialize
     * @throws org.xml.sax.SAXException
     */
    public void characters(org.w3c.dom.Node node)
        throws org.xml.sax.SAXException
    {
        flushPending();

        String data = node.getNodeValue();
        char[] ch = null;
        int length = 0;
        if (data != null)
        {
            ch = data.toCharArray();
            length = data.length();
             characters(ch, 0, length);

        }
    }
    

    /**
     * @see org.xml.sax.ErrorHandler#error(SAXParseException)
     */
    public void error(SAXParseException exc) throws SAXException {
    }

    /**
     * @see org.xml.sax.ErrorHandler#fatalError(SAXParseException)
     */
    public void fatalError(SAXParseException exc) throws SAXException {
        
      m_startTagOpen = false;

    }

    /**
     * @see org.xml.sax.ErrorHandler#warning(SAXParseException)
     */
    public void warning(SAXParseException exc) throws SAXException 
    {
    }

    /**
     * To fire off start entity trace event
     * @param name Name of entity
     */
    protected void fireStartEntity(String name)
        throws org.xml.sax.SAXException
    {
        flushMyWriter();
        if (m_tracer != null)
            m_tracer.fireGenerateEvent(SerializerTrace.EVENTTYPE_ENTITYREF, name);     	        	    	
    }

    /**
     * Report the characters event
     * @param chars  content of characters
     * @param start  starting index of characters to output
     * @param length  number of characters to output
     */
//    protected void fireCharEvent(char[] chars, int start, int length)
//        throws org.xml.sax.SAXException
//    {
//        if (m_tracer != null)
//            m_tracer.fireGenerateEvent(SerializerTrace.EVENTTYPE_CHARACTERS, chars, start,length);     	        	    	
//    }
//        

    /**
     * This method is only used internally when flushing the writer from the
     * various fire...() trace events.  Due to the writer being wrapped with 
     * SerializerTraceWriter it may cause the flush of these trace events:
     * EVENTTYPE_OUTPUT_PSEUDO_CHARACTERS 
     * EVENTTYPE_OUTPUT_CHARACTERS
     * which trace the output written to the output stream.
     * 
     */
    private void flushMyWriter()
    {
        if (m_writer != null)
        {
            try
            {
                m_writer.flush();
            }
            catch(IOException ioe)
            {
            
            }
        }
    }
    /**
     * Report the CDATA trace event
     * @param chars  content of CDATA
     * @param start  starting index of characters to output
     * @param length  number of characters to output
     */
    protected void fireCDATAEvent(char[] chars, int start, int length)
        throws org.xml.sax.SAXException
    {
        flushMyWriter();
		if (m_tracer != null)
			m_tracer.fireGenerateEvent(SerializerTrace.EVENTTYPE_CDATA, chars, start,length);     	        	    	
    }

    /**
     * Report the comment trace event
     * @param chars  content of comment
     * @param start  starting index of comment to output
     * @param length  number of characters to output
     */
    protected void fireCommentEvent(char[] chars, int start, int length)
        throws org.xml.sax.SAXException
    {
        flushMyWriter();
		if (m_tracer != null)
			m_tracer.fireGenerateEvent(SerializerTrace.EVENTTYPE_COMMENT, new String(chars, start, length));     	        	    	
    }


    /**
     * To fire off end entity trace event
     * @param name Name of entity
     */
    public void fireEndEntity(String name)
        throws org.xml.sax.SAXException
    {
        flushMyWriter();
    	// we do not need to handle this.
    }    

    /**
     * To fire off start document trace  event
     */
     protected void fireStartDoc()
        throws org.xml.sax.SAXException
    {
        flushMyWriter();
        if (m_tracer != null)
            m_tracer.fireGenerateEvent(SerializerTrace.EVENTTYPE_STARTDOCUMENT);     	    
    }    


    /**
     * To fire off end document trace event
     */
    protected void fireEndDoc()
        throws org.xml.sax.SAXException
    {
        flushMyWriter();
        if (m_tracer != null)
        m_tracer.fireGenerateEvent(SerializerTrace.EVENTTYPE_ENDDOCUMENT);     	        	
    }    
    
    /**
     * Report the start element trace event. This trace method needs to be
     * called just before the attributes are cleared.
     * 
     * @param elemName the qualified name of the element
     * 
     */
    protected void fireStartElem(String elemName)
        throws org.xml.sax.SAXException
    {
        flushMyWriter();
        if (m_tracer != null)
            m_tracer.fireGenerateEvent(SerializerTrace.EVENTTYPE_STARTELEMENT,
                elemName, m_attributes);     	        	
    }    


    /**
     * To fire off the end element event
     * @param name Name of element
     */
//    protected void fireEndElem(String name)
//        throws org.xml.sax.SAXException
//    {
//        if (m_tracer != null)
//            m_tracer.fireGenerateEvent(SerializerTrace.EVENTTYPE_ENDELEMENT,name, (Attributes)null);     	        	    	
//    }    


    /**
     * To fire off the PI trace event
     * @param name Name of PI
     */
    protected void fireEscapingEvent(String name, String data)
        throws org.xml.sax.SAXException
    {
        flushMyWriter();
        if (m_tracer != null)
            m_tracer.fireGenerateEvent(SerializerTrace.EVENTTYPE_PI,name, data);     	        	    	
    }    


    /**
     * To fire off the entity reference trace event
     * @param name Name of entity reference
     */
    protected void fireEntityReference(String name)
        throws org.xml.sax.SAXException
    {
        flushMyWriter();
        if (m_tracer != null)
            m_tracer.fireGenerateEvent(SerializerTrace.EVENTTYPE_ENTITYREF,name, (Attributes)null);     	        	    	
    }    

    /**
     * Receive notification of the beginning of a document.
     * This method is never a self generated call, 
     * but only called externally.
     *
     * <p>The SAX parser will invoke this method only once, before any
     * other methods in this interface or in DTDHandler (except for
     * setDocumentLocator).</p>
     *
     * @throws org.xml.sax.SAXException Any SAX exception, possibly
     *            wrapping another exception.
     *
     * @throws org.xml.sax.SAXException
     */
    public void startDocument() throws org.xml.sax.SAXException
    {

        // if we do get called with startDocument(), handle it right away       
        startDocumentInternal();
        m_needToCallStartDocument = false;
        return;
    }   
    
    /**
     * This method handles what needs to be done at a startDocument() call,
     * whether from an external caller, or internally called in the 
     * serializer.  Historically Xalan has not always called a startDocument()
     * although it always calls endDocument() on the serializer.
     * So the serializer must be flexible for that. Even if no external call is
     * made into startDocument() this method will always be called as a self
     * generated internal startDocument, it handles what needs to be done at a
     * startDocument() call.
     * 
     * This method exists just to make sure that startDocument() is only ever
     * called from an external caller, which in principle is just a matter of
     * style.
     * 
     * @throws SAXException
     */
    protected void startDocumentInternal() throws org.xml.sax.SAXException
    {
    	this.fireStartDoc();
    } 
    /**
     * This method is used to set the source locator, which might be used to
     * generated an error message.
     * @param locator the source locator
     *
     * @see org.apache.xml.serializer.ExtendedContentHandler#setSourceLocator(javax.xml.transform.SourceLocator)
     */
    public void setSourceLocator(SourceLocator locator)
    {
        m_sourceLocator = locator;    
    }

    
    /** 
     * Used only by TransformerSnapshotImpl to restore the serialization 
     * to a previous state. 
     * 
     * @param NamespaceMappings
     */
    public void setNamespaceMappings(NamespaceMappings mappings) {
        m_prefixMap = mappings;
    }

}
