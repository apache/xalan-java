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

public class HtmlOutput extends StreamOutput {

    protected static final Hashtable _emptyElements = new Hashtable();
    protected static final String[] tags = { "area", "base", "basefont", "br",
					   "col", "frame", "hr", "img", "input",
					   "isindex", "link", "meta", "param" };
    static {
        for (int i = 0; i < tags.length; i++) {
            _emptyElements.put(tags[i], "");
	}
    }

    public HtmlOutput(Writer writer) {
	_writer = writer;
    }

    public HtmlOutput(OutputStream out, String encoding) 
	throws IOException
    {
	try {
	    _writer = new OutputStreamWriter(out, encoding);
	}
	catch (UnsupportedEncodingException e) {
	    _writer = new OutputStreamWriter(out, "UTF-8");
	}
    }

    public void startDocument() throws TransletException { 
	// empty
    }

    public void endDocument() throws TransletException { 
	if (_startTagOpen) {
	    _buffer.append("/>");
	}

	try {
	    int n = 0;
	    final int length = _buffer.length();
	    final String output = _buffer.toString();

	    // Output buffer in chunks of OUTPUT_BUFFER_SIZE 
	    if (length > OUTPUT_BUFFER_SIZE) {
		do {
		    _writer.write(output, n, OUTPUT_BUFFER_SIZE);
		    n += OUTPUT_BUFFER_SIZE;
		} while (n + OUTPUT_BUFFER_SIZE < length);
	    }
	    _writer.write(output, n, length - n);
	    _writer.flush();
	}
	catch (IOException e) {
	    // ignore
	}
    }

    public void startElement(String elementName) throws TransletException { 
	if (_startTagOpen) {
	    _buffer.append('>');
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
    }

    public void endElement(String elementName) throws TransletException { 
	if (_startTagOpen) {
	    _startTagOpen = false;
	    _buffer.append(">");
	}
	// Empty elements may not have closing tags
	if (_emptyElements.get(elementName.toLowerCase()) == null) {
	    _buffer.append("</").append(elementName).append('>');
	}
    }

    public void characters(String characters)
	throws TransletException 
    { 
	if (_startTagOpen) {
	    _buffer.append('>');
	    _startTagOpen = false;
	}

	if (_escaping) {
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

	if (_escaping) {
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
	    _buffer.append(' ').append(attributeName)
		.append("=\"").append(attributeValue).append('"');
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
	// ignore since method type is HTML
    }

    public void setCdataElements(Hashtable elements) { 
	// ignore since method type is HTML
    }

    public void setType(int type) { 
	// ignore: default is HTML
    }

    public void setIndent(boolean indent) { 
	// ignore: default is off
    }

    public void omitHeader(boolean value) { 
	// ignore: default is on
    }

    private void escapeCharacters(char[] ch, int off, int len) {
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
		_buffer.append(ch, offset, i - offset);
		_buffer.append(AMP);
		offset = i + 1;
		break;
	    case '<':
		_buffer.append(ch, offset, i - offset);
		_buffer.append(LT);
		offset = i + 1;
		break;
	    case '>':
		_buffer.append(ch, offset, i - offset);
		_buffer.append(GT);
		offset = i + 1;
		break;
	    case '\u00a0':
		_buffer.append(ch, offset, i - offset);
		_buffer.append(NBSP);
		offset = i + 1;
		break;
	    default:
		if ( (current >= '\u007F' && current < '\u00A0') ||
		     (_is8859Encoded && (current > '\u00FF')) )
		{
		    _buffer.append(ch, offset, i - offset);
		    _buffer.append(CHAR_ESC_START);
		    _buffer.append(Integer.toString((int)ch[i]));
		    _buffer.append(';');
		    offset = i + 1;
		}
	    }
	}
	// Output remaining characters (that do not need escaping).
	if (offset < limit) {
	    _buffer.append(ch, offset, limit - offset);
	}
    }

    /**
     * Adds a newline in the output stream and indents to correct level
     */
    private void indent(boolean linefeed) {
        if (linefeed) {
            _buffer.append('\n');
	}

	_buffer.append(INDENT, 0, 
	    _indentLevel < MAX_INDENT_LEVEL ? _indentLevel + _indentLevel 
		: MAX_INDENT);
    }
}
