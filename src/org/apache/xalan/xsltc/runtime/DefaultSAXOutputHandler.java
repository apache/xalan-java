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
 * @author Morten Jorgensen
 * @author G. Todd Miller 
 * @author Santiago Pericas-Geertsen
 */

package org.apache.xalan.xsltc.runtime;

import java.io.IOException;
import java.io.Writer;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;

import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class DefaultSAXOutputHandler implements ContentHandler {

    // The output writer
    private Writer _writer;

    // Contains end-tags for closing elements (for speeding up output)
    private Hashtable _endTags = new Hashtable();
    // Contains commonly used attributes (for speeding up output)
    private Hashtable _attributeTemplates = new Hashtable();
    // Contains all HTML elements that should not get an end tag
    private Hashtable _emptyElements = new Hashtable();
    // Contains the name of the last opened element (set in startElement())
    private String _element = null;

    // Contains the output document type (XML, HTML or TEXT).
    private int	_outputType = TextOutput.UNKNOWN;
    // Contains the name of the output encoding (default is utf-8).
    private String _encoding = "utf-8";

    // This variable is set to 'true' when a start tag is left open
    private boolean _startTagOpen = false;

    // XML output header (used only for XML output).
    private static final String XML_HEADER_BEG  =
	"<?xml version=\"1.0\" encoding=\"";
    private static final String XML_HEADER_END  = "\" ?>\n";

    // Commonly used strings are stored as char arrays for speed
    private static final char[] BEGPI    = "<?".toCharArray();
    private static final char[] ENDPI    = "?>".toCharArray();
    private static final char[] GT_CR    = ">\n".toCharArray();
    private static final char[] GT_LT_SL = "></".toCharArray();
    private static final char[] SL_GT    = "/>".toCharArray();
    private static final char[] XMLNS    = " xmlns".toCharArray();

    // All of these are used to control/track output indentation
    private static final char[] INDENT =
	"                              ".toCharArray();
    private static final int MAX_INDENT_LEVEL = 10;
    private static final int MAX_INDENT       = 30;

    private static final String EMPTYSTRING = "";

    private boolean   _indent = false;
    private boolean   _omitXmlDecl = false;
    private boolean   _indentNextEndTag = false;
    private boolean   _linefeedNextStartTag = false;
    private int       _indentLevel = 0;

    // This is used for aggregating namespace declarations
    private AttributeList _namespaceDeclarations = new AttributeList();

    /**
     * Constructor - simple, initially for use in servlets
     */
    public DefaultSAXOutputHandler(Writer writer) throws IOException {
	_writer = writer;
	_encoding = "utf-8";
	init();
    }

    /**
     * Constructor - set output-stream & output encoding. 
     */
    public DefaultSAXOutputHandler(OutputStream out, String encoding)
	throws IOException {

        OutputStreamWriter writer;
        try {
            writer = new OutputStreamWriter(out, _encoding = encoding);
        }
        catch (java.io.UnsupportedEncodingException e) {
            writer = new OutputStreamWriter(out, _encoding = "utf-8" );
        }
        _writer = new BufferedWriter(writer);
	init();
    }

    /**
     * Constructor - set output file and output encoding
     */
    public DefaultSAXOutputHandler(String filename, String encoding)
	throws IOException {
	this(new FileOutputStream(filename), encoding);
    }

    /**
     * Utility method: Initialise the output handler 
     */
    private void init() throws IOException {

	// These are HTML tags that can occur as empty elements with
	// no closing tags (such as <br> insteadof XHTML's <br/>).
        final String[] tags = { "area", "base", "basefont", "br",
				"col", "frame", "hr", "img", "input",
				"isindex", "link", "meta", "param" };
        for (int i = 0; i < tags.length; i++)
            _emptyElements.put(tags[i],tags[i]);

	_endTags.clear();
	_outputType = TextOutput.UNKNOWN;
	_indent = false;
	_indentNextEndTag = false;
	_linefeedNextStartTag = false;
	_indentLevel = 0;
	_startTagOpen = false;
    }

    /**
     * Utility method - outputs an XML header
     */
    private void emitHeader() throws SAXException {
	if (_omitXmlDecl) {
	    // if true then stylesheet contained an xsl:output element
	    // with the omit-xml-declaration attribute set to "yes". 
	    return;
	}
	characters(XML_HEADER_BEG);
	characters(_encoding);
	characters(XML_HEADER_END);
    }

    /**
     * Utility method - determine output type; XML or HTML
     */
    private void determineOutputType(String element) throws SAXException {
	// Assume this is an HTML document if first element is <html>
	if ((element != null) && (element.toLowerCase().equals("html"))) {
	    _outputType = TextOutput.HTML;
	}
	// Otherwise we assume this is an XML document
	else {
	    _outputType = TextOutput.XML;
	    emitHeader();
	}
    }

    /**
     * SAX2: Receive notification of the beginning of a document.
     */
    public void startDocument() throws SAXException { }

    /**
     * SAX2: Receive notification of the end of an element.
     */
    public void endDocument() throws SAXException  { 
        try {
	    _writer.flush();
        } catch (IOException e) {
            throw new SAXException(e);
        }
    }

    /**
     * SAX2: Receive notification of the beginning of an element.
     */
    public void startElement(String uri, String localname,
			     String elementName, Attributes attrs) throws
	SAXException {
	try {
	    // Determine the output document type if not already known
	    if (_outputType == TextOutput.UNKNOWN)
		determineOutputType(elementName);

            if (_startTagOpen) closeStartTag(true); // Close any open element.
            _element = elementName; // Save element name

	    // Handle indentation (not a requirement)
            if (_indent) {
                indent(_linefeedNextStartTag);
                _indentLevel++;
                _indentNextEndTag = false;
            }
            _linefeedNextStartTag = true;

	    // Now, finally, output the start tag for the element.
	    _writer.write('<');
	    _writer.write(elementName);
	    _startTagOpen = true;

	    // Output namespace declarations first...
	    int declCount = _namespaceDeclarations.getLength();
	    for (int i=0; i<declCount; i++) {
		final String prefix = _namespaceDeclarations.getQName(i);
		_writer.write(XMLNS);
		if ((prefix != null) && (prefix != EMPTYSTRING)) {
		    _writer.write(':');
		    _writer.write(prefix);
		}
		_writer.write('=');
		_writer.write('\"');
		_writer.write(_namespaceDeclarations.getValue(i));
		_writer.write('\"');
            }
	    _namespaceDeclarations.clear();

	    // ...then output all attributes
	    int attrCount = attrs.getLength();
	    for (int i=0; i<attrCount; i++) {
		_writer.write(' ');
		_writer.write(attrs.getQName(i));
		_writer.write('=');
		_writer.write('\"');
		_writer.write(attrs.getValue(i));
		_writer.write('\"');
            }
	} catch (IOException e) {
            throw new SAXException(e);
        }
    }

    /**
     * SAX2: Receive notification of the end of an element.
     */
    public void endElement(String uri, String localname,
			   String elementName)  throws SAXException {
	try {
            if (_indent) _indentLevel--;

            if (_startTagOpen) {
                closeStartTag(false);
            }
            else {
                if ((_indent) && (_indentNextEndTag)) indent(true);
                char[] endTag = (char[])_endTags.get(elementName);
                if (endTag == null) {
		    // We dont' want to concatenate String objects!!!!
		    // endTag = ("</"+elementName+">\n").toCharArray();
		    final int len = elementName.length();
		    final char[] src = elementName.toCharArray();
		    endTag = new char[len+4];
		    System.arraycopy(src, 0, endTag, 2, len);
		    endTag[0] = '<';
		    endTag[1] = '/';
		    endTag[len+2] = '>';
		    endTag[len+3] = '\n';
                    _endTags.put(elementName,endTag);
                }
                _writer.write(endTag);
            }
            _indentNextEndTag = true;
        } catch (IOException e) {
            throw new SAXException(e);
        }
    }

    /**
     * Utility method - pass a string to the SAX handler's characters() method
     */
    private void characters(String str) throws SAXException{
	final char[] ch = str.toCharArray();
	characters(ch, 0, ch.length);
    }

    /**
     * Utility method - pass a whole character array to the SAX handler
     */
    private void characters(char[] ch) throws SAXException{
	characters(ch, 0, ch.length);
    }

    /**
     * SAX2: Receive notification of character data.
     */
    public void characters(char[] ch, int off, int len) throws SAXException {
        try {
	    // Determine the output document type if not already known
	    if (_outputType == TextOutput.UNKNOWN)
		determineOutputType(null);
	    
            if (len == 0) return;
            _linefeedNextStartTag = false;

            // Close any open start-tags.
            if (_startTagOpen) closeStartTag(true);

            // Output text
	    _writer.write(ch, off, len);
        }
        catch (IOException e) {
            throw new SAXException(e);
        }
    }

    /**
     * SAX2: Receive notification of a processing instruction.
     */
    public void processingInstruction(String target, String data) throws 
	SAXException {
	try {
            if (_startTagOpen) closeStartTag(true);
            _writer.write(BEGPI);
            _writer.write(target);
            _writer.write(' ');
            _writer.write(data);
            if (_outputType == TextOutput.HTML)
                _writer.write('>');
            else
                _writer.write(ENDPI);

        } catch (IOException e) {
            throw new SAXException(e);
        }
    }

    /**
     * SAX2: Receive notification of ignorable whitespace in element content.
     */
    public void ignorableWhitespace(char[] ch, int start, int len) { }

    /**
     * SAX2: Receive an object for locating the origin of SAX document events.
     */
    public void setDocumentLocator(Locator locator) { } 

    /**
     * SAX2: Receive notification of a skipped entity.
     */
    public void skippedEntity(String name) { }

    /**
     * SAX2: Begin the scope of a prefix-URI Namespace mapping.
     *       Namespace declarations are output in startElement()
     */
    public void startPrefixMapping(String prefix, String uri) {
	_namespaceDeclarations.add(prefix,uri);
    }

    /**
     * SAX2: End the scope of a prefix-URI Namespace mapping.
     */
    public void endPrefixMapping(String prefix) {
	// Do nothing
    }

    /**
     * Adds a newline in the output stream and indents to correct level
     */
    private void indent(boolean linefeed) throws IOException {
        if (linefeed)
            _writer.write('\n');
        if (_indentLevel < MAX_INDENT_LEVEL)
            _writer.write(INDENT, 0, (_indentLevel+_indentLevel+_indentLevel));
        else
            _writer.write(INDENT, 0, MAX_INDENT);
    }

    /**
     * Closes a start tag of an element
     */
    private void closeStartTag(boolean content) throws SAXException {
        try {
            // Take special care when outputting empty tags in HTML documents.
            if (!content) {
                if (_outputType == TextOutput.HTML) {
                    // HTML: output empty element as <tag> or <tag></tag>
                    if (!_emptyElements.containsKey(_element.toLowerCase())){
                        _writer.write(GT_LT_SL);
                        _writer.write(_element);
                    }
		    _writer.write(GT_CR);
                }
                else {
                    // XML: output empty element as <tag/>
                    _writer.write(SL_GT);
                }
            }
            else {
              _writer.write('>');
            }
            _startTagOpen = false;
        }
        catch (IOException e) {
            throw new SAXException(e);
        }
    }

    /**
     * Turns output indentation on/off (used with XML and HTML output only)
     * Breaks the SAX HandlerBase interface, but TextOutput will only call
     * this method of the SAX handler is an instance of this class.
     */
    public void setIndent(boolean indent) {
        _indent = indent;
    }

    /**
     * Turns xml declaration generation on/off, dependent on the attribute
     * omit-xml-declaration in any xsl:output element. 
     * Breaks the SAX HandlerBase interface, but TextOutput will only call
     * this method of the SAX handler is an instance of this class.
     */
    public void omitXmlDecl(boolean value) {
        _omitXmlDecl = value;
    }

    /**
     * Set the output type (either TEXT, HTML or XML)
     * Breaks the SAX HandlerBase interface, but TextOutput will only call
     * this method of the SAX handler is an instance of this class.
     */
    public void setOutputType(int type) throws SAXException {
	_outputType = type;
	if (_outputType == TextOutput.XML ) {
	    emitHeader();
	}
    }

}
