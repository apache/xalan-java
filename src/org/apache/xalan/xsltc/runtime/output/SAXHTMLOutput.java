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

import org.xml.sax.SAXException;
import org.xml.sax.ContentHandler;
import org.xml.sax.ext.LexicalHandler;
import org.apache.xalan.xsltc.TransletException;
import org.apache.xalan.xsltc.runtime.BasisLibrary;
import org.apache.xalan.xsltc.runtime.AttributeList;


public class SAXHTMLOutput extends SAXOutput  { 
    private boolean   _headTagOpen = false;
    private String _mediaType = "text/html";

    public SAXHTMLOutput(ContentHandler handler, String encoding) {
	super(handler, encoding);
    }

    public SAXHTMLOutput(ContentHandler handler, LexicalHandler lex, 
	String encoding)
    {
	super(handler, lex, encoding);
    }


    public void attribute(String name, final String value) 
	throws TransletException
    {
	final String patchedName = patchQName(name);
	final String localName = getLocalName(patchedName);
	final int index = _attributes.getIndex(name); 

	if (!_startTagOpen) {
            BasisLibrary.runTimeError(BasisLibrary.STRAY_ATTRIBUTE_ERR,name);
        }
        if (index >= 0) {
            _attributes.setAttribute(index, EMPTYSTRING, EMPTYSTRING,
                    name, "CDATA", value);
        }
        else {
            _attributes.addAttribute(EMPTYSTRING, EMPTYSTRING,
                name, "CDATA", value);
        }
    }

    /**
    * Send characters to the output document
    */
    public void characters(char[] ch, int off, int len)
        throws TransletException 
    {
	try {
            // Close any open start tag
            if (_startTagOpen) closeStartTag();
            _saxHandler.characters(ch, off, len);
        }
        catch (SAXException e) {
            throw new TransletException(e);
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
            _saxHandler.startElement(null, _elementName, _elementName, 
		_attributes);

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
     * Emit header through the SAX handler
     */
    private void emitHeader() throws SAXException {
        AttributeList attrs = new AttributeList();
        attrs.add("http-equiv", "Content-Type");
        attrs.add("content", _mediaType+"; charset="+_encoding);
        _saxHandler.startElement(EMPTYSTRING, EMPTYSTRING, "meta", attrs);
        _saxHandler.endElement(EMPTYSTRING, EMPTYSTRING, "meta");
    }

    /**
     * Start an element in the output document. This might be an XML
     * element (<elem>data</elem> type) or a CDATA section.
     */
    public void startElement(String elementName) throws TransletException {
    	try {
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
            if (elementName.toLowerCase().equals("head")) {
                _headTagOpen = true;
	    }
        } catch (SAXException e) {
            throw new TransletException(e);
        }
    }



   
    /**
     * End an element or CDATA section in the output document
     */
    public void endElement(String elementName) throws TransletException {
        try {
            // Close any open element
            if (_startTagOpen) closeStartTag();
            _saxHandler.endElement(EMPTYSTRING, EMPTYSTRING,
                (String)(_qnameStack.pop()));
        } catch (SAXException e) {
            throw new TransletException(e);
        }

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
     * Ends the document output.
     */
    public void endDocument() throws TransletException {
        try {
            // Close any open start tag
            if (_startTagOpen) closeStartTag();

            // Close output document
            _saxHandler.endDocument();
        } catch (SAXException e) {
            throw new TransletException(e);
        }
    }


    private void initNamespaces() { 
	//empty 
    }

    private String lookupNamespace(String prefix) { 
	return null;	// no-op	
    }

    private void popNamespace(String prefix) throws SAXException {
	//empty
    }

    private String getNamespaceURI(String qname, boolean isElement)
        throws TransletException
    {
	return null;	// no-op	
    } 
}
