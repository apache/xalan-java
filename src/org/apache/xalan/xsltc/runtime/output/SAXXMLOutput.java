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

import java.util.Stack;
import java.io.IOException;
import org.xml.sax.ContentHandler;
import org.xml.sax.ext.LexicalHandler;
import org.apache.xalan.xsltc.TransletException;
import org.apache.xalan.xsltc.runtime.Hashtable;
import org.xml.sax.SAXException;
import org.apache.xalan.xsltc.runtime.BasisLibrary;

public class SAXXMLOutput extends SAXOutput {

    private static final char[] BEGCDATA = "<![CDATA[".toCharArray();
    private static final char[] ENDCDATA = "]]>".toCharArray();
    private static final char[] CNTCDATA = "]]]]><![CDATA[>".toCharArray();

    public SAXXMLOutput(ContentHandler handler, String encoding) 
	throws IOException 
    {
    	super(handler, encoding);
	initCDATA();
	initNamespaces();
    }

    public SAXXMLOutput(ContentHandler handler, LexicalHandler lex, 
        String encoding) throws IOException
    {
        super(handler, lex, encoding);
	initCDATA();
	initNamespaces();
    }

    public void endDocument() throws TransletException {
	try {
	    // Close any open start tag
            if (_startTagOpen) {
		closeStartTag();
	    }
            else if (_cdataTagOpen) {
		closeCDATA();
	    }

            // Close output document
            _saxHandler.endDocument();
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
// System.out.println("SAXXMLOutput.startElement name = " + elementName);
	try {
            // Close any open start tag
            if (_startTagOpen) {
		closeStartTag();
	    }
            else if (_cdataTagOpen) {
		closeCDATA();
	    }

            // Handle document type declaration (for first element only)
            if (_firstElement) {
                if (_doctypeSystem != null) {
                    _lexHandler.startDTD(elementName, _doctypePublic,
			_doctypeSystem);
		}
                _firstElement = false;
            }

            _depth++;
            _elementName = elementName;
            _attributes.clear();
            _startTagOpen = true;
	}
        catch (SAXException e) {
            throw new TransletException(e);
        }
    }

    /**
     * Put an attribute and its value in the start tag of an element.
     * Signal an exception if this is attempted done outside a start tag.
     */
    public void attribute(String name, final String value)
        throws TransletException 
    {
	if (_startTagOpen) {
	    final String patchedName = patchName(name);
	    final String localName = getLocalName(patchedName);
	    final String uri = getNamespaceURI(patchedName, false);

	    final int index = (localName == null) ?
		    _attributes.getIndex(name) :    /* don't use patchedName */
		    _attributes.getIndex(uri, localName);

	    if (index >= 0) {       // Duplicate attribute?
		_attributes.setAttribute(index, uri, localName,
		    patchedName, "CDATA", value);
	    }
	    else {
		_attributes.addAttribute(uri, localName, patchedName,
		    "CDATA", value);
	    }
	}
    }

    public void characters(char[] ch, int off, int len)
        throws TransletException 
    {
// System.out.println("SAXXMLOutput.characters ch = " + new String(ch, off, len));

	try {
            // Close any open start tag
            if (_startTagOpen) {
		closeStartTag();
	    }

            Integer I = (Integer)_cdataStack.peek();
            if ((I.intValue() == _depth) && (!_cdataTagOpen)) {
                startCDATA(ch, off, len);
            }
            else {
                _saxHandler.characters(ch, off, len);
            }
	}
        catch (SAXException e) {
            throw new TransletException(e);
        }
    }

    public void endElement(String elementName) throws TransletException {
// System.out.println("SAXXMLOutput.endElement name = " + elementName);
        try {
            // Close any open element
            if (_startTagOpen) {
		closeStartTag();
	    }
            else if (_cdataTagOpen) {
		closeCDATA();
	    }

            _saxHandler.endElement(getNamespaceURI(elementName, true),
                getLocalName(elementName), elementName);

            popNamespaces();
            if (((Integer)_cdataStack.peek()).intValue() == _depth){
                _cdataStack.pop();
	    }
            _depth--;

    	} 
	catch (SAXException e) {
            throw new TransletException(e);
    	}
    }

    /**
     * Send a namespace declaration in the output document. The namespace
     * declaration will not be include if the namespace is already in scope
     * with the same prefix.
     */
    public void namespace(final String prefix, final String uri)
        throws TransletException 
    {
	if (_startTagOpen) {
	    pushNamespace(prefix, uri);
	}
	else {
	    if ((prefix == EMPTYSTRING) && (uri == EMPTYSTRING)) return;
	    BasisLibrary.runTimeError(BasisLibrary.STRAY_NAMESPACE_ERR,
				      prefix, uri);
	}
    }

    /**
     * Send a processing instruction to the output document
     */
    public void processingInstruction(String target, String data)
        throws TransletException {
        try {
            // Close any open element
            if (_startTagOpen) {
		closeStartTag();
	    }
            else if (_cdataTagOpen) {
		closeCDATA();
	    }

            // Pass the processing instruction to the SAX handler
            _saxHandler.processingInstruction(target, data);
        }
        catch (SAXException e) {
            throw new TransletException(e);
        }
    }

    /**
     * Declare a prefix to point to a namespace URI. Inform SAX handler
     * if this is a new prefix mapping.
     */
    protected boolean pushNamespace(String prefix, String uri) {
	try {
	    if (super.pushNamespace(prefix, uri)) {
		_saxHandler.startPrefixMapping(prefix, uri);
		return true;
	    }
	} 
	catch (SAXException e) {
	    // falls through
	}
	return false;
    }

    /**
     * Undeclare the namespace that is currently pointed to by a given 
     * prefix. Inform SAX handler if prefix was previously mapped.
     */
    protected boolean popNamespace(String prefix) {
	try {
	    if (super.popNamespace(prefix)) {
		_saxHandler.endPrefixMapping(prefix);
		return true;
	    }
	}
	catch (SAXException e) {
	    // falls through
	}
	return false;
    }

    /**
     * This method is called when all the data needed for a call to the
     * SAX handler's startElement() method has been gathered.
     */
    protected void closeStartTag() throws TransletException {
        try {
            _startTagOpen = false;

	    final String localName = getLocalName(_elementName);
	    final String uri = getNamespaceURI(_elementName, true);

            // Now is time to send the startElement event
            _saxHandler.startElement(uri, localName, _elementName, 
		_attributes);

	    if (_cdata != null) {
		final StringBuffer expandedName = (uri == EMPTYSTRING) ? 
		    new StringBuffer(_elementName) :
		    new StringBuffer(uri).append(':').append(localName);

		if (_cdata.containsKey(expandedName.toString())) {
		    _cdataStack.push(new Integer(_depth));
		}
	    }
        }
        catch (SAXException e) {
            throw new TransletException(e);
        }
    }

    public void startCDATA() throws TransletException {
	try {
	    // Output start bracket - "<![CDATA["
	    _saxHandler.characters(BEGCDATA, 0, BEGCDATA.length);
	    _cdataTagOpen = true;
	}
	catch (SAXException e) {
            throw new TransletException(e);
	}
    }

    public void closeCDATA() throws TransletException {
	try {
	    // Output closing bracket - "]]>"
	    _saxHandler.characters(ENDCDATA, 0, ENDCDATA.length);
	    _cdataTagOpen = false;
	}
	catch (SAXException e) {
            throw new TransletException(e);
	}
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
        for (int i = offset; i < limit - 2; i++) {
            if (ch[i] == ']' && ch[i + 1] == ']' && ch[i + 2] == '>') {
                _saxHandler.characters(ch, offset, i - offset);
                _saxHandler.characters(CNTCDATA, 0, CNTCDATA.length);
                offset = i+3;
                i += 2; 	// Skip next chars ']' and '>'
            }
        }

        // Output the remaining characters
        if (offset < limit) {
	    _saxHandler.characters(ch, offset, limit - offset);
	}	    
        _cdataTagOpen = true;
    }
}

