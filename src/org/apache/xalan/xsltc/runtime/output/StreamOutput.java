/*
 * @(#)$Id$
 *
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

package org.apache.xalan.xsltc.runtime.output;

import java.io.Writer;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import java.util.Vector;
import java.util.Hashtable;

import org.apache.xalan.xsltc.TransletException;

abstract public class StreamOutput extends OutputBase {

    protected static final String AMP      = "&amp;";
    protected static final String LT       = "&lt;";
    protected static final String GT       = "&gt;";
    protected static final String CRLF     = "&#xA;";
    protected static final String APOS     = "&apos;";
    protected static final String QUOT     = "&quot;";
    protected static final String NBSP     = "&nbsp;";

    protected static final String CHAR_ESC_START  = "&#";

    protected static final char[] INDENT = "                    ".toCharArray();
    protected static final int MAX_INDENT_LEVEL = (INDENT.length >> 1);
    protected static final int MAX_INDENT       = INDENT.length;

    protected Writer  _writer;
    protected OutputBuffer _buffer;

    protected boolean _is8859Encoded = false;
    protected boolean _indent     = false;
    protected boolean _omitHeader = false;
    protected String  _standalone = null;
    protected String  _version    = "1.0";

    protected boolean _lineFeedNextStartTag = false;
    protected boolean _linefeedNextEndTag   = false;
    protected boolean _indentNextEndTag     = false;
    protected int     _indentLevel          = 0;

    protected boolean _escaping     = true;
    protected String  _encoding     = "UTF-8";

    protected int     _indentNumber = 2;

    protected Vector _attributes = new Vector();

    static class Attribute {
	public String name, value;

	Attribute(String name, String value) {
	    this.name = name; 
	    this.value = value;
	}

	public int hashCode() {
	    return name.hashCode();
	}

	public boolean equals(Object obj) {
	    try {
		return name.equalsIgnoreCase(((Attribute) obj).name);
	    }
	    catch (ClassCastException e) {
		return false;
	    }
	}
    }

    // Canonical encodings
    private static Hashtable _canonicalEncodings;
    static {
	_canonicalEncodings = new Hashtable();
	_canonicalEncodings.put("ebcdic-cp-us", "Cp037");
	_canonicalEncodings.put("ebcdic-cp-ca", "Cp037"); 
	_canonicalEncodings.put("ebcdic-cp-nl", "Cp037");
	_canonicalEncodings.put("ebcdic-cp-dk", "Cp277"); 
	_canonicalEncodings.put("ebcdic-cp-no", "Cp277"); 
	_canonicalEncodings.put("ebcdic-cp-fi", "Cp278"); 
	_canonicalEncodings.put("ebcdic-cp-se", "Cp278"); 
	_canonicalEncodings.put("ebcdic-cp-it", "Cp280"); 
	_canonicalEncodings.put("ebcdic-cp-es", "Cp284"); 
	_canonicalEncodings.put("ebcdic-cp-gb", "Cp285"); 
	_canonicalEncodings.put("ebcdic-cp-fr", "Cp297"); 
	_canonicalEncodings.put("ebcdic-cp-ar1", "Cp420"); 
	_canonicalEncodings.put("ebcdic-cp-he", "Cp424"); 
	_canonicalEncodings.put("ebcdic-cp-ch", "Cp500"); 
	_canonicalEncodings.put("ebcdic-cp-roece", "Cp870");
	_canonicalEncodings.put("ebcdic-cp-yu", "Cp870"); 
	_canonicalEncodings.put("ebcdic-cp-is", "Cp871"); 
	_canonicalEncodings.put("ebcdic-cp-ar2", "Cp918");   
    }

    public static String getCanonicalEncoding(String encoding) {
	String canonical = 
	    (String)_canonicalEncodings.get(encoding.toLowerCase());
	return (canonical != null) ? canonical : encoding;
    }

    protected StreamOutput(StreamOutput output) {
	_writer = output._writer;
	_encoding = output._encoding;
	_is8859Encoded = output._is8859Encoded;
	_buffer = output._buffer;
	_indentNumber = output._indentNumber;
    }

    protected StreamOutput(Writer writer, String encoding) {
	_writer = writer;
	_encoding = encoding;
	_is8859Encoded = encoding.equalsIgnoreCase("iso-8859-1");
    }

    protected StreamOutput(OutputStream out, String encoding) 
	throws IOException
    {
	try {
	    _writer = new OutputStreamWriter(out, 
					     getCanonicalEncoding(encoding));
	    _encoding = encoding;
	    _is8859Encoded = encoding.equalsIgnoreCase("iso-8859-1");
	}
	catch (UnsupportedEncodingException e) {
	    _writer = new OutputStreamWriter(out, _encoding = "utf-8");
	}
    }

    public void setIndentNumber(int value) {
	_indentNumber = value;
    }

    /**
     * Set the output document system/public identifiers
     */
    public void setDoctype(String system, String pub) {
	_doctypeSystem = system;
	_doctypePublic = pub;
    }

    public void setIndent(boolean indent) { 
// System.out.println("StreamOutput.setIndent() indent = " + indent);
	_indent = indent;
    }

    public void omitHeader(boolean value) {
        _omitHeader = value;
    }

    public void setStandalone(String standalone) {
	_standalone = standalone;
    }

    public void setVersion(String version) { 
	_version = version;
    }

    protected void outputBuffer() {
	try {
	    _writer.write(_buffer.close());
	}
	catch (IOException e) {
	}
    }

    protected void appendDTD(String name) {
	_buffer.append("<!DOCTYPE ").append(name);
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

	    _buffer.append(INDENT, 0, 
		_indentLevel < MAX_INDENT_LEVEL ? 
		    _indentLevel * _indentNumber : MAX_INDENT);
	}
    }

    /**
     * This method escapes special characters used in text nodes. It
     * is overriden for XML and HTML output.
     */
    protected void escapeCharacters(char[] ch, int off, int len) {
    }

    protected void appendAttributes() {
	// Append attributes to output buffer
	if (!_attributes.isEmpty()) {
	    int i = 0;
	    final int length = _attributes.size();

	    do {
		final Attribute attr = (Attribute) _attributes.elementAt(i);
		_buffer.append(' ').append(attr.name).append("=\"")
		       .append(attr.value).append('"');
	    } while (++i < length);

	    _attributes.clear();
	}
    }

    protected void closeStartTag() throws TransletException {
	appendAttributes();
	_buffer.append('>');
	_startTagOpen = false;
    }

    /**
     * Ensure that comments do not include the sequence "--" and
     * that they do not end with "-".
     */
    protected void appendComment(String comment) 
	throws TransletException 
    {
	boolean lastIsDash = false;
	final int n = comment.length();

	_buffer.append("<!--");
	for (int i = 0; i < n; i++) {
	    final char ch = comment.charAt(i);
	    final boolean isDash = (ch == '-');

	    if (lastIsDash && isDash) {
		_buffer.append(" -");
	    }
	    else {
		_buffer.append(ch);
	    }
	    lastIsDash = isDash;
	}
	if (lastIsDash) {
	    _buffer.append(' ');
	}
	_buffer.append("-->");
    }
}
