/*
 * @(#)$Id$
 *
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
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
 *    Alternately, this acknowledgment may appear in the software itself
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
 * originally based on software copyright (c) 2001, Sun
 * Microsystems., http://www.sun.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 * @author Jacek Ambroziak
 * @author Santiago Pericas-Geertsen
 * @author Morten Jorgensen
 * @author Seth Ford <seth.ford@fmr.com> 
 *
 */

package org.apache.xalan.xsltc.runtime;

import java.io.*;
import java.util.Stack;
import java.util.Enumeration;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;

import org.apache.xalan.xsltc.*;

public final class TextOutput implements TransletOutputHandler, Constants {

    // These are the various output types we handle
    public static final int UNKNOWN = 0; // determine type from output contents
    public static final int XML     = 1;
    public static final int HTML    = 2;
    public static final int TEXT    = 3;
    public static final int QNAME   = 4;  // no special handling

    // These parameters are set by the <xsl:output> element, or by the
    // get/setOutputProperty() methods in TrAX
    private int	   _outputType = UNKNOWN;
    private String _encoding;
    private String _mediaType = "text/html";
    private String _doctypeSystem = null;
    private String _doctypePublic = null;

    private boolean   _escapeChars = false;
    private boolean   _startTagOpen = false;
    private boolean   _headTagOpen = false;
    private boolean   _cdataTagOpen = false;
    private boolean   _is8859Encoded = false;

    // Contains all elements that should be output as CDATA sections
    private Hashtable _cdata = null;

    private static final char[] AMP      = "&amp;".toCharArray();
    private static final char[] LT       = "&lt;".toCharArray();
    private static final char[] GT       = "&gt;".toCharArray();
    private static final char[] CRLF     = "&#xA;".toCharArray();
    private static final char[] QUOTE    = "&quot;".toCharArray();
    private static final char[] NBSP     = "&nbsp;".toCharArray();

    private static final char[] BEGCDATA = "<![CDATA[".toCharArray();
    private static final char[] ENDCDATA = "]]>".toCharArray();
    private static final char[] CNTCDATA = "]]]]><![CDATA[>".toCharArray();
    private static final char[] BEGCOMM  = "<!--".toCharArray();
    private static final char[] ENDCOMM  = "-->".toCharArray();

    private static final int AMP_length   = AMP.length;
    private static final int LT_length    = LT.length;
    private static final int GT_length    = GT.length;
    private static final int CRLF_length  = CRLF.length;
    private static final int QUOTE_length = QUOTE.length;
    private static final int NBSP_length  = NBSP.length;
    private static final int BEGCOMM_length = BEGCOMM.length;
    private static final int ENDCOMM_length = ENDCOMM.length;

    private static final String HREF_STR        = "href";
    private static final String CITE_STR        = "cite";
    private static final String SRC_STR         = "src";
    private static final String CHAR_ESC_START  = "&#";
    private static final String CDATA_ESC_START = "]]>&#";
    private static final String CDATA_ESC_END   = ";<![CDATA[";

    private AttributesImpl _attributes = new AttributesImpl();
    private String         _elementName = null;

    // Each entry (prefix) in this hashtable points to a Stack of URIs
    private Hashtable _namespaces;
    // The top of this stack contains an id of the element that last declared
    // a namespace. Used to ensure prefix/uri map scopes are closed correctly
    private Stack     _nodeStack;
    // The top of this stack is the prefix that was last mapped to an URI
    private Stack     _prefixStack;

    // The top of this stack contains the QName of the currently open element
    private Stack     _qnameStack;

    // The top of this stack contains the element id of the last element whose
    // contents should be output as CDATA sections.
    private Stack     _cdataStack;

    // Holds the current tree depth (see startElement() and endElement()).
    private int _depth = 0;

    // Reference to the SAX2 handlers that consume this handler's output
    private ContentHandler _saxHandler;
    private LexicalHandler _lexHandler;

    /**
     * Creates a new translet output post-processor
     *
     * @param handler A SAX2 handler to consume the generated SAX events 
     * @throws IOException
     */
    public TextOutput(ContentHandler handler) throws IOException {
        _saxHandler = handler;
        init();
    }

    /**
     * Creates a new translet output post-processor
     *
     * @param handler A SAX2 handler to consume the generated SAX events 
     * @param encoding The default encoding to use (set in <xsl:output>)
     * @throws IOException
     */
    public TextOutput(ContentHandler handler, String encoding)
	throws IOException {
        _saxHandler = handler;
        init();
	_encoding = encoding;
	_is8859Encoded = _encoding.equalsIgnoreCase("iso-8859-1");	
    }

    /**
     * Creates a new translet output post-processor
     *
     * @param handler A SAX2 handler to consume the generated SAX events 
     * @param encoding The default encoding to use (set in <xsl:output>)
     * @throws IOException
     */
    public TextOutput(ContentHandler sax, LexicalHandler lex, String encoding)
	throws IOException {
        _saxHandler = sax;
	_lexHandler = lex;
        init();
	_encoding = encoding;
	_is8859Encoded = _encoding.equalsIgnoreCase("iso-8859-1");	
    }

    /**
     * Initialise global variables
     */
    private void init() throws IOException {
	// Reset all output configuration from <xsl:output>
	_outputType = UNKNOWN;
	_encoding = "UTF-8";
	_mediaType = "text/html";

	// Reset all internal variables and tables
	_escapeChars  = false;
	_startTagOpen = false;
	_cdataTagOpen = false;
	_qnameStack = new Stack();
	_cdataStack = new Stack();
	_cdataStack.push(new Integer(-1)); // push dummy value

	// Reset our internal namespace handling
	initNamespaces();
    }

    /**
     * Closes the output stream
     */
    public void close() {
	if ((_saxHandler != null) &&
	    (_saxHandler instanceof DefaultSAXOutputHandler)) {
	    ((DefaultSAXOutputHandler)_saxHandler).close();
	}
    }

    /**
     * This method is used internally when the output type was initially
     * undefined and the type is set (by this handler) based on the contents
     * of the output. Set the default values for some output paramters.
     */
    private void setTypeInternal(int type) {
	if (type == XML) {
	    _escapeChars = true;
	}
	else if (type == HTML) {
	    setIndent(true);
	    _escapeChars = true;
	}
	setType(type);
    }

    /**
     * Emit header through the SAX handler
     */
    private void emitHeader() throws SAXException {
	// Output HTML header as META element
	if (_outputType == HTML) {
	    AttributeList attrs = new AttributeList();
	    attrs.add("http-equiv", "Content-Type");
	    attrs.add("content", _mediaType+"; charset="+_encoding);
	    _saxHandler.startElement(EMPTYSTRING, EMPTYSTRING, "meta", attrs);
	    _saxHandler.endElement(EMPTYSTRING, EMPTYSTRING, "meta");
	}
    }

    /**
     * This method is called when all the data needed for a call to the
     * SAX handler's startElement() method has been gathered.
     */
    public void closeStartTag() throws TransletException {
	try {
	    _startTagOpen = false;

	    // Now is time to send the startElement event
	    _saxHandler.startElement(getNamespaceURI(_elementName, true),
		getLocalName(_elementName), _elementName, _attributes);

	    // Insert <META> tag directly after <HEAD> element in HTML output
	    if (_headTagOpen) {
		emitHeader();
		_headTagOpen = false;
	    }
	}
	catch (SAXException e) {
	    throw new TransletException(e);
	}
    } 

    /**
     * Turns special character escaping on/off. Note that characters will
     * never, even if this option is set to 'true', be escaped within
     * CDATA sections in output XML documents.
     */
    public boolean setEscaping(boolean escape) throws TransletException {

	// Set output type to XML (the default) if still unknown.
	if (_outputType == UNKNOWN) setTypeInternal(XML);

	// Save old escaping setting (for the return value) and set new value
	boolean oldSetting = _escapeChars;
	_escapeChars = escape;

	// Characters are never escaped in output mode 'text'
	if (_outputType == TEXT) _escapeChars = false; 

	return(oldSetting);
    }

    /**
     * Starts the output document. Outputs the document header if the
     * output type is set to XML.
     */
    public void startDocument() throws TransletException {
        try {
            _saxHandler.startDocument();
	    // Output escaping is _ALWAYS_ enabled for XML output
            if (_outputType == XML) _escapeChars = true;
        } catch (SAXException e) {
            throw new TransletException(e);
        }
    }

    /**
     * Ends the document output.
     */
    public void endDocument() throws TransletException {
        try {
            // Set output type to XML (the default) if still unknown.
            if (_outputType == UNKNOWN) setTypeInternal(XML);

	    // Close any open start tag
	    if (_startTagOpen) closeStartTag();
	    if (_cdataTagOpen) closeCDATA();

	    // Close output document
            _saxHandler.endDocument();
        } catch (SAXException e) {
            throw new TransletException(e);
        }
    }

    public void characters(String str) throws TransletException {
	try {
	    characters(str.toCharArray(), 0, str.length());
	}
	catch (SAXException e) {
            throw new TransletException(e);
	}
    }

    /**
     * Utility method - pass a whole character array to the SAX handler
     */
    private void characters(char[] ch) throws SAXException {
	characters(ch, 0, ch.length);
    }

    /**
     * Utility method - pass a whole charactes as CDATA to SAX handler
     */
    private void startCDATA(char[] ch, int off, int len) throws SAXException {
	
	final int limit = off + len;
	int offset = off;

	// Output start bracket - "<![CDATA["
	_saxHandler.characters(BEGCDATA, 0, BEGCDATA.length);

	// Detect any occurence of "]]>" in the character array
	for (int i = offset; i < limit-2; i++) {
	    if (ch[i] == ']' && ch[i+1] == ']' && ch[i+2] == '>') {
		_saxHandler.characters(ch, offset, i - offset);
		_saxHandler.characters(CNTCDATA, 0, CNTCDATA.length);
		offset = i+3;
		i=i+2; // Skip next chars ']' and '>'.
	    }
	}

	// Output the remaining characters
	if (offset < limit) _saxHandler.characters(ch, offset, limit - offset);

	_cdataTagOpen = true;
    }

    private void closeCDATA() throws SAXException {
	// Output closing bracket - "]]>"
	_saxHandler.characters(ENDCDATA, 0, ENDCDATA.length);
	_cdataTagOpen = false;
    }

    public void startCDATA() throws TransletException {
	// This class is deprecated - use runtime.output package
    }

    public void endCDATA() throws TransletException {
	// This class is deprecated - use runtime.output package
    }

    /**
     * Send characters to the output document
     */
    public void characters(char[] ch, int off, int len)	
	throws TransletException {
        try {
	    switch(_outputType) {
	    case UNKNOWN: // Set type to XML and fall through
		setTypeInternal(XML);
	    case XML:
		// Close any open start tag
		if (_startTagOpen) closeStartTag();

		// Take special precautions if within a CDATA section. If we
		// encounter the sequence ']]>' within the CDATA, we need to
		// break the section in two and leave the ']]' at the end of
		// the first CDATA and '>' at the beginning of the next. Other
		// special characters/sequences are _NOT_ escaped within CDATA.
		Integer I = (Integer)_cdataStack.peek();
		if ((I.intValue() == _depth) && (!_cdataTagOpen)) {
		    startCDATA(ch, off, len);
		} 
		else if (_escapeChars) {
		    if (_cdataTagOpen) {
			escapeCDATA(ch, off, len);
		    } else {
			escapeCharacters(ch, off, len);
		    }
		} 
		else {
		    _saxHandler.characters(ch, off, len);
		}
		return;

	    case HTML:
		// Close any open start tag
		if (_startTagOpen) closeStartTag();

		// Output escaped characters if required. Non-ASCII characters
		// within HTML attributes should _NOT_ be escaped.
		if (_escapeChars) {
		    if (!_qnameStack.isEmpty()) {
			String qname = (String)_qnameStack.peek();
			qname = qname.toLowerCase();
			if ((qname.equals("style"))||(qname.equals("script"))) {
			    _saxHandler.characters(ch, off, len);
			    return;
			}
		    }
		    escapeCharacters(ch, off, len);
		}
		// Output the chracters as the are
		else {
		    _saxHandler.characters(ch, off, len);
		}
		return;

	    case TEXT:
		_saxHandler.characters(ch, off, len);
		return;
	    }
        }
	catch (SAXException e) {
            throw new TransletException(e);
        }
    }

    /**
     * Start an element in the output document. This might be an XML
     * element (<elem>data</elem> type) or a CDATA section.
     */
    public void startElement(String elementName) throws TransletException {
	try {
	    switch(_outputType) {

	    case UNKNOWN:
		// If we don't know the output type yet we need to examine
		// the very first element to see if it is "html".
		if (elementName.toLowerCase().equals("html"))
		    setTypeInternal(HTML);
		else
		    setTypeInternal(XML);
		startElement(elementName);
		return;

	    case XML:
		// Close any open start tag
		if (_startTagOpen) closeStartTag();
		if (_cdataTagOpen) closeCDATA();

		// Handle document type declaration (for first element only)
		if (_lexHandler != null) {
		    if (_doctypeSystem != null)
			_lexHandler.startDTD(elementName,
					     _doctypePublic,_doctypeSystem);
		    _lexHandler = null;
		}

		_depth++;
		_elementName = elementName;
		_attributes.clear();
		_startTagOpen = true;
		_qnameStack.push(elementName);
		
		if ((_cdata != null) && (_cdata.get(elementName) != null))
		    _cdataStack.push(new Integer(_depth));
		
		return;

	    case HTML:
		// Close any open start tag
		if (_startTagOpen) closeStartTag();

		// Handle document type declaration (for first element only)
		if (_lexHandler != null) {
		    if ((_doctypeSystem != null) || (_doctypePublic != null))
			_lexHandler.startDTD(elementName,
					     _doctypePublic,_doctypeSystem);
		    _lexHandler = null;
		}

		_depth++;
		_elementName = elementName;
		_attributes.clear();
		_startTagOpen = true;
		_qnameStack.push(elementName);

		// Insert <META> tag directly after <HEAD> element in HTML doc
		if (elementName.toLowerCase().equals("head"))
		    _headTagOpen = true;
		return;

	    case TEXT:
		// Do not output element tags if output mode is 'text'
		return;
		
	    }
	}
	catch (SAXException e) {
            throw new TransletException(e);
        }
    }

    /**
     * Utility method - escape special characters and pass to SAX handler
     */
    private void escapeCharacters(char[] ch, int off, int len)
	throws SAXException {

	int limit = off + len;
	int offset = off;

	if (limit > ch.length) limit = ch.length;;


	// Step through characters and escape all special characters
	for (int i = off; i < limit; i++) {
	    char current = ch[i];
	    switch (current) {
	    case '&':
		_saxHandler.characters(ch, offset, i - offset);
		_saxHandler.characters(AMP, 0, AMP_length);
		offset = i + 1;
		break;
	    case '<':
		_saxHandler.characters(ch, offset, i - offset);
		_saxHandler.characters(LT, 0, LT_length);
		offset = i + 1;
		break;
	    case '>':
		_saxHandler.characters(ch, offset, i - offset);
		_saxHandler.characters(GT, 0, GT_length);
		offset = i + 1;
		break;
	    case '\u00a0':
		_saxHandler.characters(ch, offset, i - offset);
		_saxHandler.characters(NBSP, 0, NBSP_length);
		offset = i + 1;
		break;
	    default:
		if ( (current >= '\u007F' && current < '\u00A0') ||
		     (_is8859Encoded && (current > '\u00FF')) )
		{
		    StringBuffer buf = new StringBuffer(CHAR_ESC_START);
		    buf.append(Integer.toString((int)ch[i]));
		    buf.append(';');
		    final String esc = buf.toString();
		    final char[] chars = esc.toCharArray();
		    final int    strlen = esc.length();
		    _saxHandler.characters(ch, offset, i - offset);
		    _saxHandler.characters(chars, 0, strlen);
		    offset = i + 1;
		}
	    }
	}
	// Output remaining characters (that do not need escaping).
	if (offset < limit) _saxHandler.characters(ch, offset, limit - offset);
    }

    /**
     * Utility method - escape special characters and pass to SAX handler
     */
    private void escapeCDATA(char[] ch, int off, int len)
	throws SAXException {

	int limit = off + len;
	int offset = off;

	if (limit > ch.length) limit = ch.length;;

	// Step through characters and escape all special characters
	for (int i = off; i < limit; i++) {
	    if (ch[i] > '\u00ff') {
		StringBuffer buf = new StringBuffer(CDATA_ESC_START);
		buf.append(Integer.toString((int)ch[i]));
		buf.append(CDATA_ESC_END);
		final String esc = buf.toString();
		final char[] chars = esc.toCharArray();
		final int    strlen = esc.length();
		_saxHandler.characters(ch, offset, i - offset);
		_saxHandler.characters(chars, 0, strlen);
		offset = i + 1;
	    }
	}
	// Output remaining characters (that do not need escaping).
	if (offset < limit) _saxHandler.characters(ch, offset, limit - offset);
    }

    /**
     * This method escapes special characters used in attribute values
     */
    private String escapeString(String value) {

	int i;
	char[] ch = value.toCharArray();
	int limit = ch.length;
	int offset = 0;
	StringBuffer buf = new StringBuffer();
	
	for (i = 0; i < limit; i++) {
	    switch (ch[i]) {
	    case '&':
		buf.append(ch, offset, i - offset);
		buf.append(AMP);
		offset = i + 1;
		break;
	    case '"':
		buf.append(ch, offset, i - offset);
		buf.append(QUOTE);
		offset = i + 1;
		break;
	    case '<':
		buf.append(ch, offset, i - offset);
		buf.append(LT);
		offset = i + 1;
		break;
	    case '>':
		buf.append(ch, offset, i - offset);
		buf.append(GT);
		offset = i + 1;
		break;
	    case '\n':
		buf.append(ch, offset, i - offset);
		buf.append(CRLF);
		offset = i + 1;
		break;
	    }
	}
	if (offset < limit) {
	    buf.append(ch, offset, limit - offset);
	}
	return(buf.toString());
    }

    private String makeHHString(int i) {
	String s = Integer.toHexString(i).toUpperCase();
	if (s.length() == 1) {
	    s = "0"+s;
	}
	return s;
    }

    /**
     * This method escapes special characters used in HTML attribute values
     */
    private String escapeAttr(String base) {
	final int len = base.length() - 1;
	final String str = "&quot;";
	int pos;

	char[] ch = base.toCharArray();
	StringBuffer buf = new StringBuffer();
        for(int i=0; i<base.length(); i++){
	    if (ch[i] <= 0x20) {
		buf.append('%');
		buf.append(makeHHString(ch[i]));
	    } 
	    else if (ch[i] > '\u007F') {
		int high = (ch[i] >> 6) | 0xC0;
		int low  = (ch[i] & 0x3F) | 0x80;  // First 6 bits + high bit
		buf.append('%');
		buf.append(makeHHString(high));
		buf.append('%');
		buf.append(makeHHString(low));
	    }
	    else {
		// These chars are reserved or unsafe in URLs
		// pg 196, 'HTML, The Definitive Guide" C. Musciano, et. al
	        switch (ch[i]) {
		    case '\u007F' :
		    case '\u0022' :
		    case '\u007B' :
		    case '\u007D' :
		    case '\u007C' :
		    case '\\'     :
		    case '\t'     :
		    case '\u005E' :
		    case '\u007E' :
		    case '\u005B' :
		    case '\u005D' :
		    case '\u0060' :
		        buf.append('%');
		        buf.append(Integer.toHexString((int)ch[i]));
		        break;
		    case '\u0026' :
			//bug fix for customer/murphy3: buf.append("&amp;");
			buf.append("&");
			break;
		    default:	
		        buf.append(ch[i]); break;
	        }
	    } 
  	}
	base = buf.toString();

	while ((pos = base.indexOf('"')) > -1) {
	    if (pos == 0) {
		final String after = base.substring(1);
		base = str + after;
	    }
	    else if (pos == len) {
		final String before = base.substring(0, pos);
		base = before + str;
	    }
	    else {
		final String before = base.substring(0, pos);
		final String after = base.substring(pos+1);
		base = before + str + after;
	    }
	}
	return base;
    }

    /**
     * Replaces whitespaces in a URL with '%20'
     */
    private String quickAndDirtyUrlEncode(String base) {
	final String pst20 = "%20";
	final int len = base.length() - 1;
	int pos;
	// Slow, very slow indeed
	while ((pos = base.indexOf(' ')) > -1) {
	    if (pos == 0) {
		final String after = base.substring(1);
		base = pst20 + after;
	    }
	    else if (pos == len) {
		final String before = base.substring(0, pos);
		base = before + pst20;
	    }
	    else {
		final String before = base.substring(0, pos);
		final String after = base.substring(pos+1);
		base = before + pst20 + after;
	    }
	}
	return base;
    }

    /**
     * Returns the URI of an element or attribute. Note that default namespaces 
     * do not apply directly to attributes.
     */
    private String getNamespaceURI(String qname, boolean isElement) 
	throws TransletException 
    {
	String uri = EMPTYSTRING;
	int col = qname.lastIndexOf(':');
	final String prefix = (col > 0) ? qname.substring(0, col) : EMPTYSTRING;

	if (prefix != EMPTYSTRING || isElement) { 
	    uri = lookupNamespace(prefix);
	    if (uri == null && !prefix.equals(XMLNS_PREFIX)) {
		BasisLibrary.runTimeError(BasisLibrary.NAMESPACE_PREFIX_ERR,
					  qname.substring(0, col));
	    }
	}
	return uri;
    }

    /**
     * Returns the local name of a qualified name. If the name has no prefix
     * then return null. 
     */
    private static String getLocalName(String qname) throws TransletException {
	final int col = qname.lastIndexOf(':');
	return (col > 0) ? qname.substring(col + 1) : null;
    }

    /**
     * TODO: This method is a HACK! Since XSLTC does not have access to the
     * XML file, it sometimes generates a NS prefix of the form "ns?" for
     * an attribute. If at runtime, when the qname of the attribute is
     * known, another prefix is specified for the attribute, then we can get 
     * a qname of the form "ns?:otherprefix:name". This function patches the 
     * qname by simply ignoring "otherprefix".
     */
    private static String patchQName(String qname) throws TransletException {
	final int lastColon = qname.lastIndexOf(':');
	if (lastColon > 0) {
	    final int firstColon = qname.indexOf(':');
	    if (firstColon != lastColon) {
		return qname.substring(0, firstColon) + qname.substring(lastColon);
	    }
	}
	return qname;
    }

    /**
     * Put an attribute and its value in the start tag of an element.
     * Signal an exception if this is attempted done outside a start tag.
     */
    public void attribute(String name, final String value)
	throws TransletException {

	if (_outputType == TEXT) return;

	final String patchedName = patchQName(name);
	final String localName = getLocalName(patchedName);
	final String uri = getNamespaceURI(patchedName, false);
	final int index = (localName == null) ?
				_attributes.getIndex(name) :	/* don't use patchedName */
				_attributes.getIndex(uri, localName);

	switch(_outputType) {
	case XML:
	    if (!_startTagOpen) {
		BasisLibrary.runTimeError(BasisLibrary.STRAY_ATTRIBUTE_ERR, patchedName);
	    }

	    // Output as namespace declaration
	    if (name.startsWith(XMLNS_PREFIX)) {
		namespace(name.length() > 6 ? name.substring(6) : EMPTYSTRING, value);
	    }
	    else {
		if (index >= 0) {	// Duplicate attribute?
		    _attributes.setAttribute(index, uri, localName, patchedName, "CDATA", 
			escapeString(value));	
		}
		else {
		    _attributes.addAttribute(uri, localName, patchedName, "CDATA", 
			escapeString(value));
		}
	    }
	    break;
	case HTML:
	    if (!_startTagOpen) {
		BasisLibrary.runTimeError(BasisLibrary.STRAY_ATTRIBUTE_ERR,name);
	    }

	    /* 
	     * The following is an attempt to escape an URL stored in a href
	     * attribute of HTML output. Normally URLs should be encoded at
	     * the time they are created, since escaping or unescaping a
	     * completed URI might change its semantics. We limit or escaping
	     * to include space characters only - and nothing else. This is for
	     * two reasons: (1) performance and (2) we want to make sure that
	     * we do not change the meaning of the URL.
	     */
	    final String tmp = name.toLowerCase();
	    if (tmp.equals(HREF_STR) || tmp.equals(SRC_STR) || tmp.equals(CITE_STR)) {
		if (index >= 0) {
		    _attributes.setAttribute(index, EMPTYSTRING, EMPTYSTRING, name, 
			"CDATA", quickAndDirtyUrlEncode(escapeAttr(value)));
		}
		else {
		    _attributes.addAttribute(EMPTYSTRING, EMPTYSTRING, name, "CDATA",
			quickAndDirtyUrlEncode(escapeAttr(value)));
		}
	    }
	    else {
		if (index >= 0) {
		    _attributes.setAttribute(index, EMPTYSTRING, EMPTYSTRING, 
			name, "CDATA", escapeNonURLAttr(value));
		}
		else {
		    _attributes.addAttribute(EMPTYSTRING, EMPTYSTRING, 
			name, "CDATA", escapeNonURLAttr(value));
		}
	    }
	    break;
	}
    }

    /**
     * Escape non ASCII characters (> u007F) as &#XXX; entities.
     */
    private String escapeNonURLAttr(String base) {
	final int len = base.length() - 1;

	char[] ch = base.toCharArray();
	StringBuffer buf = new StringBuffer();
        for(int i=0; i<base.length(); i++){
	    if (ch[i] > '\u007F') {
	        buf.append(CHAR_ESC_START);
		buf.append(Integer.toString((int)ch[i]));
	        buf.append(';');
	    }
	    else {
	        buf.append(ch[i]); 
	    } 
  	}
	base = buf.toString();
	return base;
    }


    /**
     * End an element or CDATA section in the output document
     */
    public void endElement(String elementName) throws TransletException {
	try {
	    switch(_outputType) {
	    case TEXT:
		// Do not output element tags if output mode is 'text'
		return;
	    case XML:
		// Close any open element
		if (_startTagOpen) closeStartTag();
		if (_cdataTagOpen) closeCDATA();

		final String qname = (String) _qnameStack.pop();
		_saxHandler.endElement(getNamespaceURI(qname, true), 
		    getLocalName(qname), qname);

		popNamespaces();
		if (((Integer)_cdataStack.peek()).intValue() == _depth)
		    _cdataStack.pop();
		_depth--;
		return;
	    case HTML:
		// Close any open element
		if (_startTagOpen) closeStartTag();
		_saxHandler.endElement(EMPTYSTRING, EMPTYSTRING, 
		    (String)(_qnameStack.pop()));
		popNamespaces();
		_depth--;		
		return;
	    }

        } catch (SAXException e) {
            throw new TransletException(e);
        }
    }

    /**
     * Send a HTML-style comment to the output document
     */
    public void comment(String comment) throws TransletException {
	try {
	    // Close any open element before emitting comment
            if (_startTagOpen) closeStartTag();
	    if (_cdataTagOpen) closeCDATA();

            // Set output type to XML (the default) if still unknown.
            if (_outputType == UNKNOWN) setTypeInternal(XML);

            // ...and then output the comment.
            _saxHandler.characters(BEGCOMM, 0, BEGCOMM_length);
            characters(comment);
            _saxHandler.characters(ENDCOMM, 0, ENDCOMM_length);
	}
	catch (SAXException e) {
	    throw new TransletException(e);
	}
    }
    
    /**
     * Send a processing instruction to the output document
     */
    public void processingInstruction(String target, String data)
	throws TransletException {
        try {
	    // Close any open element
	    if (_startTagOpen) closeStartTag();
	    if (_cdataTagOpen) closeCDATA();

	    // Handle document type declaration (only HTML at this point)
	    if ((_lexHandler != null) && (_outputType == HTML)) {
		if ((_doctypeSystem != null) || (_doctypePublic != null))
		    _lexHandler.startDTD("HTML",_doctypePublic,_doctypeSystem);
		_lexHandler = null;
	    }

	    // Pass the processing instruction to the SAX handler
            _saxHandler.processingInstruction(target, data);
        }
	catch (SAXException e) {
            throw new TransletException(e);
        }
    }

    /**
     * Initialize namespace stacks
     */
    private void initNamespaces() {
	_namespaces = new Hashtable();
	_nodeStack = new Stack();
	_prefixStack = new Stack();

	// Define the default namespace (initially maps to "" uri)
	Stack stack;
	_namespaces.put(EMPTYSTRING, stack = new Stack());
	stack.push(EMPTYSTRING);
	_prefixStack.push(EMPTYSTRING);

	_namespaces.put(XML_PREFIX, stack = new Stack());
	stack.push("http://www.w3.org/XML/1998/namespace");
	_prefixStack.push(XML_PREFIX);

	_nodeStack.push(new Integer(-1));
	_depth = 0;
    }

    /**
     * Declare a prefix to point to a namespace URI
     */
    private void pushNamespace(String prefix, String uri) throws SAXException {
	// Prefixes "xml" and "xmlns" cannot be redefined
	if (prefix.equals(XML_PREFIX)) return;
	
	Stack stack;
	// Get the stack that contains URIs for the specified prefix
	if ((stack = (Stack)_namespaces.get(prefix)) == null) {
	    stack = new Stack();
	    _namespaces.put(prefix, stack);
	}
	// Quit now if the URI the prefix currently maps to is the same as this
	if (!stack.empty() && uri.equals(stack.peek())) return;
	// Put this URI on top of the stack for this prefix
	stack.push(uri);

	_prefixStack.push(prefix);
	_nodeStack.push(new Integer(_depth));

	// Inform the SAX handler
	_saxHandler.startPrefixMapping(prefix, escapeString(uri));
    }

    /**
     * Undeclare the namespace that is currently pointed to by a given prefix
     */
    private void popNamespace(String prefix) throws SAXException {
	// Prefixes "xml" and "xmlns" cannot be redefined
	if (prefix.equals(XML_PREFIX)) return;

	Stack stack;
	if ((stack = (Stack)_namespaces.get(prefix)) != null) {
	    stack.pop();
	    _saxHandler.endPrefixMapping(prefix);
	}
    }

    /**
     * Pop all namespace definitions that were delcared by the current element
     */
    private void popNamespaces() throws TransletException {
	try {
	    while (true) {
		if (_nodeStack.isEmpty()) return;
		Integer i = (Integer)(_nodeStack.peek());
		if (i.intValue() != _depth) return;
		_nodeStack.pop();
		popNamespace((String)_prefixStack.pop());
	    }
	}
	catch (SAXException e) {
	    throw new TransletException(e);
	}
    }

    /**
     * Use a namespace prefix to lookup a namespace URI
     */
    private String lookupNamespace(String prefix) {
	final Stack stack = (Stack)_namespaces.get(prefix);
	return stack != null && !stack.isEmpty() ? (String)stack.peek() : null;
    }

    /**
     * Send a namespace declaration in the output document. The namespace
     * declaration will not be include if the namespace is already in scope
     * with the same prefix.
     */
    public void namespace(final String prefix, final String uri)
	throws TransletException {
	try {
	    if (_startTagOpen)
		pushNamespace(prefix, uri);
	    else {
		if ((prefix == EMPTYSTRING) && (uri == EMPTYSTRING)) return;
		BasisLibrary.runTimeError(BasisLibrary.STRAY_NAMESPACE_ERR,
					  prefix, uri);
	    }
	}
	catch (SAXException e) {
	    throw new TransletException(e);
	}
    }

    /************************************************************************
     * The following are all methods for configuring the output settings
     ************************************************************************/
    /**
     * Set the output type. The type must be wither TEXT, XML or HTML.
     */
    public void setType(int type)  {
	try {
	    _outputType = type;
	    if ((_outputType == HTML) || (_outputType == XML))
		_escapeChars = true;
	    if (_encoding == null)
	    	_encoding = "UTF-8";
	    if (_saxHandler instanceof DefaultSAXOutputHandler)
		((DefaultSAXOutputHandler)_saxHandler).setOutputType(type);
	}
	catch (SAXException e) { }
    }

    /**
     * Turns output indentation on/off. Should only be set to on
     * if the output type is XML or HTML.
     */
    public void setIndent(boolean indent) {
	if (_saxHandler instanceof DefaultSAXOutputHandler) {
            ((DefaultSAXOutputHandler)_saxHandler).setIndent(indent);
	}
    } 

    /**
     * Directive to turn xml header declaration  on/off. 
     */
    public void omitHeader(boolean value) {
	if (_saxHandler instanceof DefaultSAXOutputHandler) {
            ((DefaultSAXOutputHandler)_saxHandler).omitHeader(value);
	}
    }

    /**
     * Set the XML output document version - should be 1.0
     */
    public void setVersion(String version) {
	if (_saxHandler instanceof DefaultSAXOutputHandler) {
            ((DefaultSAXOutputHandler)_saxHandler).setVersion(version);
	}
    }

    /**
     * Set the XML standalone attribute - must be "yes" or "no"
     */
    public void setStandalone(String standalone) {
	if (_saxHandler instanceof DefaultSAXOutputHandler) {
            ((DefaultSAXOutputHandler)_saxHandler).setStandalone(standalone);
	}
    }

    /**
     * Set the output document system/public identifiers
     */
    public void setDoctype(String system, String pub) {
	_doctypeSystem = system;
	_doctypePublic = pub;
    }

    /**
     * Set the output media type - only relevant for HTML output
     */
    public void setMediaType(String mediaType) {
	// This value does not have to be passed to the SAX handler. This
	// handler creates the HTML <meta> tag in which the media-type
	// (MIME-type) will be used.
	_mediaType = mediaType;
    }

    /**
     * The <xsl:output method="xml"/> instruction can specify that certain
     * XML elements should be output as CDATA sections. This methods allows
     * the translet to insert these elements into a hashtable of strings.
     * Every output element is looked up in this hashtable before it is
     * output.
     */ 
    public void setCdataElements(Hashtable elements) {
	_cdata = elements;
    }

}
