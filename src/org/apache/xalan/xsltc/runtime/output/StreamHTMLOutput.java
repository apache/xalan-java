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

package org.apache.xalan.xsltc.runtime.output;

import java.util.Vector;

import java.io.Writer;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import org.apache.xalan.xsltc.*;
import org.apache.xalan.xsltc.runtime.*;
import org.apache.xalan.xsltc.runtime.Hashtable;

public class StreamHTMLOutput extends StreamOutput {

    private static final String HREF_STR = "href";
    private static final String CITE_STR = "cite";
    private static final String SRC_STR  = "src";

    private static final Hashtable _emptyElements = new Hashtable();
    private static final String[] tags = { "area", "base", "basefont", "br",
					   "col", "frame", "hr", "img", "input",
					   "isindex", "link", "meta", "param" };
    static {
        for (int i = 0; i < tags.length; i++) {
            _emptyElements.put(tags[i], "");
	}
    }

    private boolean _headTagOpen = false;
    private boolean _inStyleScript = false;
    private String  _mediaType     = "text/html";

    public StreamHTMLOutput(StreamOutput output) {
	super(output);
	setIndent(true);  // default for HTML
// System.out.println("StreamHTMLOutput.<init> this = " + this);
    }

    public StreamHTMLOutput(Writer writer, String encoding) {
	super(writer, encoding);
	setIndent(true);  // default for HTML
//System.out.println("StreamHTMLOutput.<init> this = " + this);
    }

    public StreamHTMLOutput(OutputStream out, String encoding) 
	throws IOException
    {
	super(out, encoding);
	setIndent(true);  // default for HTML
//System.out.println("StreamHTMLOutput.<init> this = " + this);
    }

    public void startDocument() throws TransletException { 
	// empty
    }

    public void endDocument() throws TransletException { 
	// Finally, output buffer to writer
	outputBuffer();
    }

    public void startElement(String elementName) throws TransletException { 
	if (_startTagOpen) {
	    closeStartTag();
	}

	// Handle document type declaration (for first element only)
	if (_firstElement) {
	    if (_doctypeSystem != null || _doctypePublic != null) {
		appendDTD(elementName);
	    }
	    _firstElement = false;
	}

	if (_indent) {
	    if (!_emptyElements.containsKey(elementName.toLowerCase())) {
		indent(_lineFeedNextStartTag);
		_lineFeedNextStartTag = true;
		_indentNextEndTag = false;
	    }
	    _indentLevel++;
	}

	_buffer.append('<').append(elementName);
	_startTagOpen = true;
	_indentNextEndTag = false;

	if (elementName.equalsIgnoreCase("head")) {
	    _headTagOpen = true;
	}
	else if (elementName.equalsIgnoreCase("style") || 
		 elementName.equalsIgnoreCase("script")) 
	{
	    _inStyleScript = true;
	}
    }

    public void endElement(String elementName) 
	throws TransletException 
    { 
	if (_inStyleScript && 
	    (elementName.equalsIgnoreCase("style") || 
	     elementName.equalsIgnoreCase("script"))) 
	{
	    _inStyleScript = false;
	}

	if (_startTagOpen) {
	    appendAttributes();
	    if (_emptyElements.containsKey(elementName.toLowerCase())) {
		_buffer.append('>');
	    }
	    else {
		closeStartTag();
		_buffer.append("</").append(elementName).append('>');
	    }
	    _startTagOpen = false;

	    if (_indent) {
		_indentLevel--;
		_indentNextEndTag = true;
	    }
	}
	else {
	    if (_indent) {
		_indentLevel--;

		if (_indentNextEndTag) {
		    indent(_indentNextEndTag);
		    _indentNextEndTag = true;
		    _lineFeedNextStartTag = true;
		}
	    }
	    _buffer.append("</").append(elementName).append('>');
	    _indentNextEndTag = true;
	}
    }

    public void characters(String characters)
	throws TransletException 
    { 
	if (_startTagOpen) {
	    closeStartTag();
	}

	if (_escaping && !_inStyleScript) {
	    escapeCharacters(characters.toCharArray(), 0, characters.length());
	}
	else {
	    _buffer.append(characters);
	}
    }

    public void characters(char[] characters, int offset, int length)
	throws TransletException 
    { 
	if (_startTagOpen) {
	    closeStartTag();
	}

	if (_escaping && !_inStyleScript) {
	    escapeCharacters(characters, offset, length);
	}
	else {
	    _buffer.append(characters, offset, length);
	}
    }

    public void attribute(String name, String value)
	throws TransletException 
    { 
// System.out.println("attribute = " + name + " " + value);
	if (_startTagOpen) {
	    int k;
	    Attribute attr;

	    if (name.equalsIgnoreCase(HREF_STR) || 
		name.equalsIgnoreCase(SRC_STR)  || 
		name.equals(CITE_STR)) 
	    {
		attr = new Attribute(name, escapeURL(value));
	    }
	    else {
		attr = new Attribute(name, escapeNonURL(value));
	    }

	    if ((k = _attributes.indexOf(attr)) >= 0) {
		_attributes.setElementAt(attr, k);
	    }
	    else {
		_attributes.add(attr);
	    }
	}
    }

    public void comment(String comment) throws TransletException { 
	if (_startTagOpen) {
	    closeStartTag();
	}
	appendComment(comment);
    }

    public void processingInstruction(String target, String data)
	throws TransletException 
    { 
	if (_startTagOpen) {
	    closeStartTag();
	}

	// Handle document type declaration 
	if (_firstElement) {
	    if (_doctypeSystem != null || _doctypePublic != null) {
		appendDTD("html");
	    }
	    _firstElement = false;
	}

	// A PI in HTML ends with ">" instead of "?>"
	_buffer.append("<?").append(target).append(' ')
	    .append(data).append('>');
    }

    public boolean setEscaping(boolean escape) throws TransletException 
    { 
	final boolean temp = _escaping;
	_escaping = escape;
	return temp; 
    }

    public void close() { 
	try {
	    _writer.close();
	}
	catch (Exception e) {
	    // ignore
	}
    }

    public void namespace(String prefix, String uri) throws TransletException 
    { 
	// ignore when method type is HTML
    }

    public void setCdataElements(Hashtable elements) { 
	// ignore when method type is HTML
    }

    public void setType(int type) { 
	// ignore: default is HTML
    }

    /**
     * Set the output media type - only relevant for HTML output
     */
    public void setMediaType(String mediaType) {
	_mediaType = mediaType;
    }

    /**
     * Escape non ASCII characters (> u007F) as &#XXX; entities.
     */
    private String escapeNonURL(String base) {
	final int length = base.length();
	final StringBuffer result = new StringBuffer();

        for (int i = 0; i < length; i++){
	    final char ch = base.charAt(i);

	    if ((ch >= '\u007F' && ch < '\u00A0') ||
		(_is8859Encoded && ch > '\u00FF'))
	    {
	        result.append(CHAR_ESC_START)
		      .append(Integer.toString((int) ch))
		      .append(';');
	    }
	    else {
	        result.append(ch); 
	    } 
  	}
	return result.toString();
    }

    /**
     * This method escapes special characters used in HTML attribute values
     */
    private String escapeURL(String base) {
	final char[] chs = base.toCharArray();
	final StringBuffer result = new StringBuffer();

	final int length = chs.length;
        for (int i = 0; i < length; i++) {
	    final char ch = chs[i];

	    if (ch <= 0x20) {
		result.append('%').append(makeHHString(ch));
	    } 
	    else if (ch > '\u007F') {
		result.append('%')
		      .append(makeHHString((ch >> 6) | 0xC0))
		      .append('%')
		      .append(makeHHString((ch & 0x3F) | 0x80));
	    }
	    else {
		// These chars are reserved or unsafe in URLs
	        switch (ch) {
		    case '\u007F' :
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
		    case '\u0020' :
		        result.append('%')
		              .append(Integer.toHexString((int) ch));
		        break;
		    case '"':
			result.append("%22");
			break;
		    default:	
		        result.append(ch); 
			break;
	        }
	    } 
  	}
	return result.toString();
    }

    private String makeHHString(int i) {
	final String s = Integer.toHexString(i).toUpperCase();
	return (s.length() == 1) ? "0" + s : s;
    }

    /**
     * Emit HTML meta info
     */
    private void appendHeader() {
	_buffer.append("<meta http-equiv=\"Content-Type\" content=\"")
	       .append(_mediaType).append("; charset=")
	       .append(_encoding).append("\">");
    }

    protected void closeStartTag() throws TransletException {
	super.closeStartTag();

	// Insert <META> tag directly after <HEAD> element in HTML output
	if (_headTagOpen) {
	    appendHeader();
	    _headTagOpen = false;
	}
    } 

    /**
     * This method escapes special characters used in text nodes
     */
    protected void escapeCharacters(char[] ch, int off, int len) {
	int limit = off + len;
	int offset = off;

	if (limit > ch.length) {
	    limit = ch.length;
	}

	// Step through characters and escape all special characters
	for (int i = off; i < limit; i++) {
	    final char current = ch[i];

	    switch (current) {
	    case '&':
		_buffer.append(ch, offset, i - offset).append(AMP);
		offset = i + 1;
		break;
	    case '<':
		_buffer.append(ch, offset, i - offset).append(LT);
		offset = i + 1;
		break;
	    case '>':
		_buffer.append(ch, offset, i - offset).append(GT);
		offset = i + 1;
		break;
	    case '\u00A0':
		_buffer.append(ch, offset, i - offset).append(NBSP);
		offset = i + 1;
		break;
	    default:
		if ((current >= '\u007F' && current < '\u00A0') ||
		    (_is8859Encoded && current > '\u00FF'))
		{
		    _buffer.append(ch, offset, i - offset)
			   .append(CHAR_ESC_START)
			   .append(Integer.toString((int)ch[i]))
			   .append(';');
		    offset = i + 1;
		}
	    }
	}
	// Output remaining characters (that do not need escaping).
	if (offset < limit) {
	    _buffer.append(ch, offset, limit - offset);
	}
    }
}
