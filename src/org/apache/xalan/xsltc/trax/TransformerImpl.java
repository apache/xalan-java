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
 *
 */

package org.apache.xalan.xsltc.trax;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Writer;
import java.io.Reader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.net.UnknownServiceException;

import java.lang.IllegalArgumentException;
import java.util.Enumeration;
import java.util.StringTokenizer;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.ext.LexicalHandler;

import org.w3c.dom.Document;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.sax.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

import org.apache.xalan.xsltc.Translet;
import org.apache.xalan.xsltc.TransletException;
import org.apache.xalan.xsltc.DOMCache;
import org.apache.xalan.xsltc.dom.*;
import org.apache.xalan.xsltc.runtime.*;
import org.apache.xalan.xsltc.compiler.*;

import java.util.Properties;

public final class TransformerImpl extends Transformer implements DOMCache {

    private AbstractTranslet _translet = null;
    private String           _encoding = null;
    private ContentHandler   _handler = null;

    private ErrorListener _errorListener = null;
    private URIResolver   _uriResolver = null;
    private Properties    _properties = null;

    // Used for default output property settings
    private final static String EMPTY_STRING = "";
    private final static String NO_STRING    = "no";
    private final static String YES_STRING   = "yes";
    private final static String XML_STRING   = "xml";

    // Pre-set DOMImpl to use as input (used only with TransformerHandlerImpl)
    private DOMImpl _dom = null;
    
    // List all error messages here
    private final static String TRANSLET_ERR_MSG = 
	"The transformer has no encapsulated translet object.";
    private final static String HANDLER_ERR_MSG = 
	"No defined output handler for transformation result.";
    private static final String ERROR_LISTENER_NULL =
	"Attempting to set ErrorListener for Transformer to null";
    private static final String INPUT_SOURCE_EMPTY =
	"The Source object passed to transform() has no contents.";
    private static final String OUTPUT_RESULT_EMPTY =
	"The Result object passed to transform() is invalid.";

    private final static String LEXICAL_HANDLER_PROPERTY =
	"http://xml.org/sax/properties/lexical-handler";

    /**
     * Implements JAXP's Transformer constructor
     * Our Transformer objects always need a translet to do the actual work
     */
    protected TransformerImpl(Translet translet) {
	_translet = (AbstractTranslet)translet;
    }

    /**
     * Returns the translet wrapped inside this Transformer
     */
    protected AbstractTranslet getTranslet() {
	return(_translet);
    }

    /**
     * Implements JAXP's Transformer.transform()
     *
     * @param source Contains the input XML document
     * @param result Will contain the output from the transformation
     * @throws TransformerException
     */
    public void transform(Source source, Result result)
	throws TransformerException {

	if (_translet == null) {
	    throw new TransformerException(TRANSLET_ERR_MSG);
	}

	_handler = getOutputHandler(result);
	if (_handler == null) { 
	    throw new TransformerException(HANDLER_ERR_MSG);
	}

	if (_uriResolver != null) {
	    _translet.setDOMCache(this);
	}

	// Run the transformation
	transform(source, (ContentHandler)_handler, _encoding);

	// If a DOMResult, then we must set the DOM Tree so it can
	// be retrieved later 
	if (result instanceof DOMResult) {
	    ((DOMResult)result).setNode(((SAX2DOM)_handler).getDOM());
	}
    }

    /**
     * Create an output handler (SAX2 handler) for the transformation output
     * based on the type and contents of the TrAX Result object passed to
     * the transform() method. Only StreamResult and SAXResult are currently
     * handled.
     */
    private ContentHandler getOutputHandler(Result result) 
	throws TransformerException {
	// Try to get the encoding from Translet (may not be set)
	if (_translet._encoding != null) {
	    _encoding = _translet._encoding;
	}
	else {
	    _encoding = "utf-8"; // default output encoding
	}

	try {
	    String systemId = result.getSystemId();

	    // Handle SAXResult output handler
	    if (result instanceof SAXResult) {
		final SAXResult target = (SAXResult)result;
		final ContentHandler handler = target.getHandler();
		// Simple as feck, just pass the SAX handler back...
		if (handler != null) return handler;
	    }
	    // Handle StreamResult output handler
	    else if (result instanceof StreamResult) {
		final StreamResult target = (StreamResult)result;
		final OutputStream ostream = target.getOutputStream();
		final Writer writer = target.getWriter();

		if (ostream != null)
		    return (new DefaultSAXOutputHandler(ostream, _encoding));
		else if (writer != null)
		    return (new DefaultSAXOutputHandler(writer, _encoding));
		else if ((systemId != null) && systemId.startsWith("file:")) {
		    final URL url = new URL(systemId);
		    final OutputStream os = new FileOutputStream(url.getFile());
		    return (new DefaultSAXOutputHandler(os, _encoding));
		}
	    }
	    // Handle DOMResult output handler
	    else if (result instanceof DOMResult) {
		return (new SAX2DOM());
	    }

	    // Common, final handling of all input sources, only used if the
	    // other contents of the Result object could not be used
	    if (systemId != null) {
		if ((new File(systemId)).exists())
		    systemId = "file:"+systemId;
		final URL url = new URL(systemId);
		final URLConnection connection = url.openConnection();
		final OutputStream ostream = connection.getOutputStream();
		return(new DefaultSAXOutputHandler(ostream, _encoding));
	    }
	    else {
		throw new TransformerException(OUTPUT_RESULT_EMPTY);
	    }
	}
	// If we cannot write to the location specified by the SystemId
	catch (UnknownServiceException e) {
	    throw new TransformerException(e);
	}
	// If we cannot create a SAX2DOM adapter
	catch (ParserConfigurationException e) {
	    throw new TransformerException(
		"SAX2DOM adapter could not be created, " + e.getMessage());
	}
	// If we cannot create the file specified by the SystemId
	catch (IOException e) {
	    throw new TransformerException(e);
	}
    }

    /**
     * Set the internal DOMImpl that will be used for the next transformation
     */
    protected void setDOM(DOMImpl dom) {
	_dom = dom;
    }

    /**
     * Builds an internal DOM from a TrAX Source object
     */
    private DOMImpl getDOM(Source source, int mask)
	throws TransformerException {
	try {
	    // Use the pre-defined DOM if present
	    if (_dom != null) {
		DOMImpl dom = _dom;
		_dom = null; // use only once, so reset to 'null'
		return(dom);
	    }

	    DOMImpl dom = null;
	    DTDMonitor dtd = null;

	    // Handle SAXSource input
	    if (source instanceof SAXSource) {
		// Get all info from the input SAXSource object
		final SAXSource   sax    = (SAXSource)source;
		final XMLReader   reader = sax.getXMLReader();
		final InputSource input  = sax.getInputSource();
		final String      systemId = sax.getSystemId();

		// Create a DTD monitor to trap all DTD/declarative events
		dtd = new DTDMonitor();
		dtd.handleDTD(reader);

		// Create a new internal DOM and set up its builder to trap
		// all content/lexical events
		dom = new DOMImpl();
		final DOMBuilder builder = dom.getBuilder();
		reader.setContentHandler(builder);
		try {
		    reader.setProperty(LEXICAL_HANDLER_PROPERTY, builder);
		}
		catch (SAXException e) {
		    // quitely ignored
		}

		// Parse the input and build the internal DOM
		reader.parse(input);
		dom.setDocumentURI(systemId);
	    }
	    // Handle DOMSource input
	    else if (source instanceof DOMSource) {
		final DOMSource   domsrc = (DOMSource)source;
		final Document    tree = (Document)domsrc.getNode();
		final DOM2SAX     dom2sax = new DOM2SAX(tree);
		final InputSource input = null; 
		final String      systemId = domsrc.getSystemId(); 

		// Create a DTD monitor to trap all DTD/declarative events
		dtd = new DTDMonitor();
		dtd.handleDTD(dom2sax);

		// Create a new internal DOM and set up its builder to trap
		// all content/lexical events
		dom = new DOMImpl();
		final DOMBuilder builder = dom.getBuilder();
		dom2sax.setContentHandler(builder);

		// Parse the input and build the internal DOM
		dom2sax.parse(input); // need this parameter?
		dom.setDocumentURI(systemId);
	    }
	    // Handle StreamSource input
	    else if (source instanceof StreamSource) {
		// Get all info from the input StreamSource object
		final StreamSource stream = (StreamSource)source;
		final InputStream  streamInput = stream.getInputStream();
		final Reader streamReader = stream.getReader();
		final String systemId = stream.getSystemId();

		// With a StreamSource we need to create our own parser
		final SAXParserFactory factory = SAXParserFactory.newInstance();
		final SAXParser parser = factory.newSAXParser();
		final XMLReader reader = parser.getXMLReader();

		// Create a DTD monitor to trap all DTD/declarative events
		dtd = new DTDMonitor();
		dtd.handleDTD(reader);

		// Create a new internal DOM and set up its builder to trap
		// all content/lexical events
		dom = new DOMImpl();
		final DOMBuilder builder = dom.getBuilder();
		reader.setContentHandler(builder);
		try {
		    reader.setProperty(LEXICAL_HANDLER_PROPERTY, builder);
		}
		catch (SAXException e) {
		    // quitely ignored
		}

		InputSource input;
		if (streamInput != null)
		    input = new InputSource(streamInput);
		else if (streamReader != null)
		    input = new InputSource(streamReader);
		else if (systemId != null)
		    input = new InputSource(systemId);
		else
		    throw new TransformerException(INPUT_SOURCE_EMPTY);

		// Parse the input and build the internal DOM
		reader.parse(input);
		dom.setDocumentURI(systemId);
	    }
	    // Handle XSLTC-internal Source input
	    else if (source instanceof XSLTCSource) {
		final XSLTCSource xsltcsrc = (XSLTCSource)source;
		dtd = xsltcsrc.getDTD();
		dom = xsltcsrc.getDOM();
	    }
	    else {
		return null;
	    }

	    // Set size of key/id indices
	    _translet.setIndexSize(dom.getSize());
	    // If there are any elements with ID attributes, build an index
	    dtd.buildIdIndex(dom, mask, _translet);
	    // Pass unparsed entity URIs to the translet
	    _translet.setDTDMonitor(dtd);
	    return dom;
	}
	catch (FileNotFoundException e) {
	    if (_errorListener != null)
		postErrorToListener("File not found: " + e.getMessage());
	    throw new TransformerException(e);
	}
	catch (MalformedURLException e) {
	    if (_errorListener != null)
		postErrorToListener("Malformed URL: " + e.getMessage());
	    throw new TransformerException(e);
	}
	catch (UnknownHostException e) {
	    if (_errorListener != null)
		postErrorToListener("Cannot resolve URI: " + e.getMessage());
	    throw new TransformerException(e);
	}
	catch (Exception e) {
	    if (_errorListener != null)
		postErrorToListener("Internal error: " + e.getMessage()); 
	    throw new TransformerException(e);
	}
    }
 
    /**
     * Internal transformation method - uses the internal APIs of XSLTC
     */
    private void transform(Source src, ContentHandler sax, String encoding)
	throws TransformerException {
	try {
	    // Build an iternal DOMImpl from the TrAX Source
	    DOMImpl dom = getDOM(src, 0);

	    // Pass output properties to the translet
	    setOutputProperties(_translet, _properties);
	    
	    // This handler will post-process the translet output
	    TextOutput handler;

	    // Check if the ContentHandler also implements LexicalHandler
	    if (sax instanceof LexicalHandler)
		handler = new TextOutput(sax, (LexicalHandler)sax, encoding);
	    else
		handler = new TextOutput(sax, encoding);
	    _translet.transform(dom, handler);
	}
	catch (TransletException e) {
	    e.printStackTrace();
	    if (_errorListener != null)
		postErrorToListener(e.getMessage());
	    throw new TransformerException(e);
	}
	catch (RuntimeException e) {
	    if (_errorListener != null)
		postErrorToListener("Runtime Error: " + e.getMessage());
	    System.err.println("Error: "+e.getMessage());
	    e.printStackTrace();
	    throw new TransformerException(e);
	}
	catch (Exception e) {
	    if (_errorListener != null)
		postErrorToListener("Internal error: " + e.getMessage()); 
	    throw new TransformerException(e);
	}
    }

    /**
     * Implements JAXP's Transformer.getErrorListener()
     * Get the error event handler in effect for the transformation.
     *
     * @return The error event handler currently in effect
     */
    public ErrorListener getErrorListener() {  
	return _errorListener; 
    }

    /**
     * Implements JAXP's Transformer.setErrorListener()
     * Set the error event listener in effect for the transformation.
     *
     * @param listener The error event listener to use
     * @throws IllegalArgumentException
     */
    public void setErrorListener(ErrorListener listener)
	throws IllegalArgumentException {
        if (listener == null)
            throw new IllegalArgumentException(ERROR_LISTENER_NULL);
        _errorListener = listener;
    }

    /**
     * Inform TrAX error listener of an error
     */
    private void postErrorToListener(String message) {
        try {
            _errorListener.error(new TransformerException(message));
	}
	catch (TransformerException e) {
            // ignored - transformation cannot be continued
        }
    }

    /**
     * Inform TrAX error listener of a warning
     */
    private void postWarningToListener(String message) {
        try {
            _errorListener.warning(new TransformerException(message));
        }
	catch (TransformerException e) {
            // ignored - transformation cannot be continued
        }
    }

    /**
     * Implements JAXP's Transformer.getOutputProperties().
     * Returns a copy of the output properties for the transformation. Note that
     * this method will only return properties that were set in this class.
     * The output settings defined in the stylesheet's <xsl:output> element
     * and default XSLT output settings will not be returned by this method.
     *
     * @return Properties explicitly set for this Transformer
     */
    public Properties getOutputProperties() {
	return(_properties);
    }

    /**
     * The translet stores all CDATA sections set in the <xsl:output> element
     * in a Hashtable. This method will re-construct the whitespace separated
     * list of elements given in the <xsl:output> element.
     */
    private String makeCDATAString(Hashtable cdata) {
	// Return a 'null' string if no CDATA section elements were specified
	if (cdata == null) return null;

	StringBuffer result = new StringBuffer();

	// Get an enumeration of all the elements in the hashtable
	Enumeration elements = cdata.keys();
	if (elements.hasMoreElements()) {
	    result.append((String)elements.nextElement());
	    while (elements.hasMoreElements()) {
		String element = (String)elements.nextElement();
		result.append(' ');
		result.append(element);
	    }
	}
	
	return(result.toString());
    }

    /**
     * Implements JAXP's Transformer.getOutputProperty().
     * Get an output property that is in effect for the transformation. The
     * property specified may be a property that was set with setOutputProperty,
     * or it may be a property specified in the stylesheet.
     *
     * @param name A non-null string that contains the name of the property
     * @throws IllegalArgumentException if the property name is not known
     */
    public String getOutputProperty(String name)
	throws IllegalArgumentException {

	String value = null;

	// Level1: Check if the property is overridden in this Transformer
	if (_properties != null) value = _properties.getProperty(name);

	// Level2: Check if the property value is set in the translet
	if ((value == null) && (_translet != null)) {
	    if (name.equals(OutputKeys.ENCODING))
		value = _translet._encoding;
	    else if (name.equals(OutputKeys.METHOD))
		value = _translet._method;
	    else if (name.equals(OutputKeys.INDENT))
		value = (new Boolean(_translet._indent)).toString();
	    else if (name.equals(OutputKeys.DOCTYPE_PUBLIC))
		value = _translet._doctypePublic;
	    else if (name.equals(OutputKeys.DOCTYPE_SYSTEM))
		value = _translet._doctypeSystem;
	    else if (name.equals(OutputKeys.CDATA_SECTION_ELEMENTS))
		value = makeCDATAString(_translet._cdata);
	    else if (name.equals(OutputKeys.MEDIA_TYPE))
		value = _translet._mediaType;
	    else if (name.equals(OutputKeys.OMIT_XML_DECLARATION))
		value = (new Boolean(_translet._omitHeader)).toString();
	    else if (name.equals(OutputKeys.STANDALONE))
		value = _translet._standalone;
	    else if (name.equals(OutputKeys.VERSION))
		value = _translet._version;
	}

	// Level3: Return the default property value
	if (value == null) {
	    if (name.equals(OutputKeys.ENCODING))
		value = "utf-8";
	    else if (name.equals(OutputKeys.METHOD))
		value = XML_STRING;
	    else if (name.equals(OutputKeys.INDENT))
		value = NO_STRING;
	    else if (name.equals(OutputKeys.DOCTYPE_PUBLIC))
		value = EMPTY_STRING;
	    else if (name.equals(OutputKeys.DOCTYPE_SYSTEM))
		value = EMPTY_STRING;
	    else if (name.equals(OutputKeys.CDATA_SECTION_ELEMENTS))
		value = EMPTY_STRING;
	    else if (name.equals(OutputKeys.MEDIA_TYPE))
		value = "text/xml";
	    else if (name.equals(OutputKeys.OMIT_XML_DECLARATION))
		value = NO_STRING;
	    else if (name.equals(OutputKeys.STANDALONE))
		value = NO_STRING;
	    else if (name.equals(OutputKeys.VERSION))
		value = "1.0";
	}

	return value;
    }

    /**
     * Implements JAXP's Transformer.setOutputProperties().
     * Set the output properties for the transformation. These properties
     * will override properties set in the Templates with xsl:output.
     * Unrecognised properties will be quitely ignored.
     *
     * @param properties The properties to use for the Transformer
     * @throws IllegalArgumentException Never, errors are ignored
     */
    public void setOutputProperties(Properties properties)
	throws IllegalArgumentException {
	_properties = properties;
    }

    /**
     * Implements JAXP's Transformer.setOutputProperty().
     * Get an output property that is in effect for the transformation. The
     * property specified may be a property that was set with 
     * setOutputProperty(), or it may be a property specified in the stylesheet.
     *
     * @param name The name of the property to set
     * @param value The value to assign to the property
     * @throws IllegalArgumentException Never, errors are ignored
     */
    public void setOutputProperty(String name, String value)
	throws IllegalArgumentException {
	if (_properties == null) _properties = new Properties();
	_properties.setProperty(name, value);
    }

    /**
     * Internal method to pass any properties to the translet prior to
     * initiating the transformation
     */
    private void setOutputProperties(AbstractTranslet translet,
				     Properties properties) {
	// Return right now if no properties are set
	if (properties == null) return;

	// Get a list of all the defined properties
	Enumeration names = properties.propertyNames();
	while (names.hasMoreElements()) {
	    // Get the next property name and value
	    String name = (String)names.nextElement();
	    String value = properties.getProperty(name);

	    // Pass property value to translet - override previous setting
	    if (name.equals(OutputKeys.ENCODING))
		translet._encoding = value;
	    else if (name.equals(OutputKeys.METHOD))
		translet._method = value;
	    else if (name.equals(OutputKeys.DOCTYPE_PUBLIC))
		translet._doctypePublic = value;
	    else if (name.equals(OutputKeys.DOCTYPE_SYSTEM))
		translet._doctypeSystem = value;
	    else if (name.equals(OutputKeys.MEDIA_TYPE))
		translet._mediaType = value;
	    else if (name.equals(OutputKeys.STANDALONE))
		translet._standalone = value;
	    else if (name.equals(OutputKeys.VERSION))
		translet._version = value;
	    else if (name.equals(OutputKeys.OMIT_XML_DECLARATION)) {
		if ((value != null) == (value.toLowerCase().equals("true"))) {
		    translet._omitHeader = true;
		}
	    }
	    else if (name.equals(OutputKeys.INDENT)) {
		if ((value != null) == (value.toLowerCase().equals("true"))) {
		    translet._indent = true;
		}
	    }
	    else if (name.equals(OutputKeys.CDATA_SECTION_ELEMENTS)) {
		translet._cdata = null; // Important - clear previous setting
		StringTokenizer e = new StringTokenizer(value);
		while (e.hasMoreTokens()) {
		    translet.addCdataElement(e.nextToken());
		}
	    }

	}
    }

    /**
     * Implements JAXP's Transformer.setParameter()
     * Add a parameter for the transformation. The parameter is simply passed
     * on to the translet - no validation is performed - so any unused
     * parameters are quitely ignored by the translet.
     *
     * @param name The name of the parameter
     * @param value The value to assign to the parameter
     */
    public void setParameter(String name, Object value) { 
	_translet.addParameter(name, value, false);
    }

    /**
     * Implements JAXP's Transformer.clearParameters()
     * Clear all parameters set with setParameter. Clears the translet's
     * parameter stack.
     */
    public void clearParameters() {  
	_translet.clearParameters();
    }

    /**
     * Implements JAXP's Transformer.getParameter()
     * Returns the value of a given parameter. Note that the translet will not
     * keep values for parameters that were not defined in the stylesheet.
     *
     * @param name The name of the parameter
     * @return An object that contains the value assigned to the parameter
     */
    public final Object getParameter(String name) {
	return(_translet.getParameter(name));
    }

    /**
     * Implements JAXP's Transformer.getURIResolver()
     * Set the object currently used to resolve URIs used in document().
     *
     * @returns The URLResolver object currently in use
     */
    public URIResolver getURIResolver() {
	return _uriResolver;
    }

    /**
     * Implements JAXP's Transformer.setURIResolver()
     * Set an object that will be used to resolve URIs used in document().
     *
     * @param resolver The URIResolver to use in document()
     */
    public void setURIResolver(URIResolver resolver) { 
	_uriResolver = resolver;
    }

    /**
     * This class should only be used as a DOMCache for the translet if the
     * URIResolver has been set.
     *
     * The method implements XSLTC's DOMCache interface, which is used to
     * plug in an external document loader into a translet. This method acts
     * as an adapter between TrAX's URIResolver interface and XSLTC's
     * DOMCache interface. This approach is simple, but removes the
     * possibility of using external document caches with XSLTC.
     *
     * @param uri  An URI pointing to the document location
     * @param mask Contains a document ID (passed from the translet)
     * @param translet A reference to the translet requesting the document
     */
    public DOMImpl retrieveDocument(String uri, int mask, Translet translet) {
	try {
	    return(getDOM(_uriResolver.resolve(uri, ""), mask));
	}
	catch (TransformerException e) {
	    if (_errorListener != null)
		postErrorToListener("File not found: " + e.getMessage());
	    return(null);
	}
    }
}
