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
    }

    public StreamHTMLOutput(Writer writer, String encoding) {
	super(writer, encoding);
    }

    public StreamHTMLOutput(OutputStream out, String encoding) 
	throws IOException
    {
	super(out, encoding);
    }

    public void startDocument() throws TransletException { 
	// empty
    }

    public void endDocument() throws TransletException { 
	if (_startTagOpen) {
	    _buffer.append("/>");
	}

	// Finally, output buffer to writer
	outputBuffer();
    }

    public void startElement(String elementName) throws TransletException { 
	if (_startTagOpen) {
	    _buffer.append('>');
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

    public void endElement(String elementName) throws TransletException { 
	if (_startTagOpen) {
	    _startTagOpen = false;
	    _buffer.append(">");
	}

	if (_indent) {
	    _indentLevel --;
	    if (_indentNextEndTag) {
		indent(_indentNextEndTag);
		_indentNextEndTag = true;
	    }
	}

	// Empty elements may not have closing tags
	if (!_emptyElements.containsKey(elementName.toLowerCase())) {
	    _buffer.append("</").append(elementName).append('>');
	}
	else if (_headTagOpen) {
	    appendHeader(); 	// Insert <META> tag after <HEAD>
	    _headTagOpen = false;
	}
	else if (_inStyleScript && 
		 (elementName.equalsIgnoreCase("style") || 
		  elementName.equalsIgnoreCase("script"))) 
	{
	    _inStyleScript = false;
	}
    }

    public void characters(String characters)
	throws TransletException 
    { 
	if (_startTagOpen) {
	    _buffer.append('>');
	    _startTagOpen = false;
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
	    _buffer.append('>');
	    _startTagOpen = false;
	}

	if (_escaping && !_inStyleScript) {
	    escapeCharacters(characters, offset, length);
	}
	else {
	    _buffer.append(characters, offset, length);
	}
    }

    public void attribute(String attributeName, String attributeValue)
	throws TransletException 
    { 
	if (_startTagOpen) {
	    _buffer.append(' ').append(attributeName).append("=\"");

	    if (attributeName.equalsIgnoreCase(HREF_STR) || 
		attributeName.equalsIgnoreCase(SRC_STR)  || 
		attributeName.equals(CITE_STR)) 
	    {
		appendEncodedURL(attributeValue).append('"');
	    }
	    else {
		appendNonURL(attributeValue).append('"');
	    }
	}
    }

    public void comment(String comment) throws TransletException { 
	if (_startTagOpen) {
	    _buffer.append('>');
	    _startTagOpen = false;
	}
	_buffer.append("<!--").append(comment).append("-->");
    }

    public void processingInstruction(String target, String data)
	throws TransletException 
    { 
	if (_startTagOpen) {
	    _buffer.append('>');
	    _startTagOpen = false;
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
     * Replaces whitespaces in a URL with '%20'
     */
    private StringBuffer appendEncodedURL(String base) {
	final int length = base.length();

	for (int i = 0; i < length; i++) {
	    final char ch = base.charAt(i);
	    if (ch == ' ') {
		_buffer.append("%20");
	    }
	    else {
		_buffer.append(ch);
	    }
	}
	return _buffer;
    }

    /**
     * Escape non ASCII characters (> u007F) as &#XXX; entities.
     */
    private StringBuffer appendNonURL(String base) {
	final int length = base.length();

        for (int i = 0; i < length; i++){
	    final char ch = base.charAt(i);

	    if ((ch >= '\u007F' && ch < '\u00A0') ||
		(_is8859Encoded && ch > '\u00FF'))
	    {
	        _buffer.append(CHAR_ESC_START)
		       .append(Integer.toString((int) ch))
		       .append(';');
	    }
	    else {
	        _buffer.append(ch); 
	    } 
  	}
	return _buffer;
    }

    /**
     * Emit HTML meta info
     */
    private void appendHeader() {
	_buffer.append("<meta http-equiv=\"Content-Type\" content=\"")
	       .append(_mediaType).append(" charset=\"")
	       .append(_encoding).append("/>");
    }

}
