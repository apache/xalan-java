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

class StreamOutput extends OutputBase {

    protected static final String AMP      = "&amp;";
    protected static final String LT       = "&lt;";
    protected static final String GT       = "&gt;";
    protected static final String CRLF     = "&#xA;";
    protected static final String QUOTE    = "&quot;";
    protected static final String NBSP     = "&nbsp;";

    protected static final String CHAR_ESC_START  = "&#";

    protected static final char[] INDENT = "                    ".toCharArray();
    protected static final int MAX_INDENT_LEVEL = (INDENT.length >> 1);
    protected static final int MAX_INDENT       = INDENT.length;

    protected static final int BUFFER_SIZE = 32 * 1024;
    protected static final int OUTPUT_BUFFER_SIZE = 4 * 1024;

    protected Writer  _writer = null;
    protected StringBuffer _buffer = new StringBuffer(BUFFER_SIZE);

    protected boolean _startTagOpen = false;
    protected boolean _is8859Encoded = false;

    protected boolean _indent = false;
    protected boolean _omitHeader = false;

    protected boolean _lineFeedNextStartTag = false;
    protected boolean _linefeedNextEndTag = false;
    protected boolean _indentNextEndTag = false;
    protected int     _indentLevel = 0;

    protected boolean _escaping = true;
    protected boolean _firstElement = true;

    protected String  _encoding;

    protected String  _doctypeSystem = null;
    protected String  _doctypePublic = null;

    /**
     * Set the output document system/public identifiers
     */
    public void setDoctype(String system, String pub) {
	_doctypeSystem = system;
	_doctypePublic = pub;
    }

    public void setIndent(boolean indent) { 
	_indent = indent;
    }

    public void omitHeader(boolean value) {
        _omitHeader = value;
    }

    protected void appendDTD(String name) {
	_buffer.append(name);
	if (_doctypePublic == null) {
	    _buffer.append(" SYSTEM");
	}
	else {
	    _buffer.append(" PUBLIC \"").append(_doctypePublic).append("\"");
	}
	if (_doctypeSystem != null) {
	    _buffer.append(" \"").append(_doctypeSystem).append("\">\n");
	}
	else {
	    _buffer.append(">\n");
	}
    }

    /**
     * Adds a newline in the output stream and indents to correct level
     */
    protected void indent(boolean linefeed) {
        if (linefeed) {
            _buffer.append('\n');
	}

	_buffer.append(INDENT, 0, 
	    _indentLevel < MAX_INDENT_LEVEL ? _indentLevel + _indentLevel 
		: MAX_INDENT);
    }

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
		if ((current >= '\u007F' && current < '\u00A0') ||
		    (_is8859Encoded && current > '\u00FF'))
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
}