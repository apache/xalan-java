
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
 *
 */

package org.apache.xalan.xsltc.trax;

import java.io.File;
import java.io.IOException;

import org.xml.sax.XMLReader;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import org.xml.sax.ext.LexicalHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;

import javax.xml.transform.Source;

import org.apache.xalan.xsltc.*;
import org.apache.xalan.xsltc.dom.DOMImpl;
import org.apache.xalan.xsltc.dom.DOMBuilder;
import org.apache.xalan.xsltc.dom.DTDMonitor;
import org.apache.xalan.xsltc.compiler.util.ErrorMsg;

public final class XSLTCSource implements Source {

    private String     _systemId = null;
    private DOMImpl    _dom      = null;
    private DTDMonitor _dtd      = null;

    private final static String LEXICAL_HANDLER_PROPERTY =
	"http://xml.org/sax/properties/lexical-handler";

    /**
     * Create a new XSLTC-specific DOM source
     * @param size The estimated node-count for this DOM. A good guess here
     * speeds up the DOM build process.
     */
    public XSLTCSource(int size) {
	_dom = new DOMImpl(size);
	_dtd = new DTDMonitor();
    }

    /**
     * Create a new XSLTC-specific DOM source
     */
    public XSLTCSource() {
	_dom = new DOMImpl();
	_dtd = new DTDMonitor();
    }

    /**
     * Implements javax.xml.transform.Source.setSystemId()
     * Set the system identifier for this Source. 
     * This Source can get its input either directly from a file (in this case
     * it will instanciate and use a JAXP parser) or it can receive it through
     * ContentHandler/LexicalHandler interfaces.
     * @param systemId The system Id for this Source
     */
    public void setSystemId(String systemId) {
	if ((new File(systemId)).exists())
	    _systemId = "file:"+systemId;
	else
	    _systemId = systemId;
	_dom.setDocumentURI(_systemId);
    }

    /**
     * Implements javax.xml.transform.Source.getSystemId()
     * Get the system identifier that was set with setSystemId.
     * @return The system identifier that was set with setSystemId,
     *         or null if setSystemId was not called.
     */
    public String getSystemId() {
	return(_systemId);
    }

    /**
     * Build the internal XSLTC-specific DOM.
     * @param reader An XMLReader that will pass the XML contents to the DOM
     * @param systemId Specifies the input file
     * @throws SAXException
     */
    public void build(XMLReader reader, String systemId) throws SAXException {
	try {
	    // Make sure that the system id is set before proceding
	    if ((systemId == null) && (_systemId == null)) {
		ErrorMsg err = new ErrorMsg(ErrorMsg.XSLTC_SOURCE_ERR);
		throw new SAXException(err.toString());
	    }

	    // Use this method in case we need to prepend 'file:' to url
	    if (systemId == null) systemId = _systemId;
	    setSystemId(systemId);

	    // Create an input source for the parser first, just in case the
	    // systemId is invalid. We don't want to waste time creating a SAX
	    // parser before we know that we actually have some valid input.
	    InputSource input = new InputSource(systemId);

	    // Set out DTD monitor up to receive all DTD and declarative
	    // events from the SAX parser. This is necessary to properly
	    // build the index used for the id() function
	    _dtd.handleDTD(reader);

	    DOMBuilder builder = _dom.getBuilder();

	    // Set the DOM builder up to receive content and lexical events
	    reader.setContentHandler(builder);
	    try {
		reader.setProperty(LEXICAL_HANDLER_PROPERTY, builder);
	    }
	    catch (SAXException e) {
		// quitely ignored
	    }

	    // Now, finally - parse the input document
	    reader.parse(input);
	}
	catch (IOException e) {
	    throw new SAXException(e);
	}
    }

    /**
     * Build the internal XSLTC-specific DOM.
     * @param systemId Specifies the input file
     * @throws SAXException
     */
    public void build(String systemId) throws SAXException {
	try {
	    // Create an XMLReader (SAX parser) for processing the input
	    final SAXParserFactory factory = SAXParserFactory.newInstance();
	    final SAXParser parser = factory.newSAXParser();
	    final XMLReader reader = parser.getXMLReader();

	    build(reader, systemId);
	}
	catch (ParserConfigurationException e) {
	    throw new SAXException(e);
	}
    }

    /**
     * Build the internal XSLTC-specific DOM.
     * @param reader An XMLReader that will pass the XML contents to the DOM
     * @throws SAXException
     */
    public void build(XMLReader reader) throws SAXException {
	build(reader, _systemId);
    }

    /**
     * Build the internal XSLTC-specific DOM.
     * The setSystemId() must be called prior to this method.
     * @throws SAXException
     */
    public void build() throws SAXException {
	build(_systemId);
    }    

    /**
     * Returns the internal DOM that is encapsulated in this Source
     */
    protected DOMImpl getDOM() {
	return(_dom);
    }

    /**
     * Returns the internal DTD that is encapsulated in this Source
     */
    protected DTDMonitor getDTD() {
	return(_dtd);
    }

}
