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
import java.io.Writer;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.lang.IllegalArgumentException;
import java.util.Enumeration;
import java.util.StringTokenizer;

import org.xml.sax.XMLReader;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.xalan.xsltc.Translet;
import org.apache.xalan.xsltc.TransletException;
import org.apache.xalan.xsltc.dom.*;
import org.apache.xalan.xsltc.runtime.*;
import org.apache.xalan.xsltc.compiler.*;

import java.util.Properties;

public final class TransformerImpl extends Transformer {

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
    
    // List all error messages here
    private final static String TRANSLET_ERR_MSG = 
	"The transformer has no encapsulated translet object.";
    private final static String HANDLER_ERR_MSG = 
	"No defined output handler for transformation result.";
    private static final String ERROR_LISTENER_NULL =
	"Attempting to set ErrorListener for Transformer to null";

    /**
     * Implements JAXP's Transformer constructor
     * Our Transformer objects always need a translet to do the actual work
     */
    public TransformerImpl(Translet translet) {
	_translet = (AbstractTranslet)translet;
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

	// Verify the input
	if (_translet == null) throw new TransformerException(TRANSLET_ERR_MSG);
	_handler = getOutputHandler(result);
	if (_handler == null) throw new TransformerException(HANDLER_ERR_MSG);
	
	// Run the transformation
	transform(source, _handler, _encoding);
    }

    /**
     * Create an output handler, and get the requested output encoding
     * from the translet instance
     */
    private ContentHandler getOutputHandler(Result result) 
	throws TransformerException {
	// Try to get the encoding from Translet (may not be set)
	_encoding = _translet._encoding;
	if (_encoding == null) _encoding = "UTF-8";

	StreamResult target   = (StreamResult)result;
	Writer       writer   = target.getWriter();
	OutputStream ostream  = target.getOutputStream();
	String       systemid = target.getSystemId();

	try {
	    if (writer != null) {
		// no constructor that takes encoding yet...
		return (new DefaultSAXOutputHandler(writer));
	    } 
	    else if (ostream != null) {
		return (new DefaultSAXOutputHandler(ostream, _encoding));
	    }
	    else if (systemid != null) {
		String filePrefix = new String("file:///");
		if (systemid.startsWith(filePrefix)) {
		    systemid = systemid.substring(filePrefix.length());
		}
		ostream = (OutputStream)(new FileOutputStream(systemid));
		return(new DefaultSAXOutputHandler(ostream, _encoding));
	    }
	    return null;
	}
	catch (java.io.FileNotFoundException e) {
	    throw new TransformerException(e);
	}
	catch (java.io.IOException e) {
	    throw new TransformerException(e);
	}
    }
 
    /**
     * Internal transformation method - uses the internal APIs of XSLTC
     */
    private void transform(Source source,
			   ContentHandler handler,
			   String encoding) throws TransformerException {
	try {
	    // Create a SAX parser and get the XMLReader object it uses
	    final SAXParserFactory factory = SAXParserFactory.newInstance();
	    final SAXParser parser = factory.newSAXParser();
	    final XMLReader reader = parser.getXMLReader();
 
	    // Set the DOM's DOM builder as the XMLReader's SAX2 content handler
	    final DOMImpl dom = new DOMImpl();
	    reader.setContentHandler(dom.getBuilder());
	    // Create a DTD monitor and pass it to the XMLReader object
	    final DTDMonitor dtdMonitor = new DTDMonitor();
	    dtdMonitor.handleDTD(reader);
 
	    String url = source.getSystemId();
	    if (url != null) {
		dom.setDocumentURI(url);
		if (url.startsWith("file:/")) {   
		    reader.parse(url);
		} else {                                
		    reader.parse("file:"+(new File(url).getAbsolutePath()));
		}
	    }
	    else if (source instanceof StreamSource) {
		InputStream stream = ((StreamSource)source).getInputStream();
		InputSource input = new InputSource(stream);
		reader.parse(input);
	    }
	    else {
		throw new TransformerException("Unsupported input.");
	    }
	    
	    // Set size of key/id indices
	    _translet.setIndexSize(dom.getSize());
	    // If there are any elements with ID attributes, build an index
	    dtdMonitor.buildIdIndex(dom, 0, _translet);
	    // Pass unparsed entity URIs to the translet
	    _translet.setDTDMonitor(dtdMonitor);

	    // Pass output properties to the translet
	    setOutputProperties(_translet, _properties);

	    // Transform the document
	    TextOutput textOutput = new TextOutput(handler, _encoding);
	    _translet.transform(dom, textOutput);
	}
	catch (TransletException e) {
	    if (_errorListener != null)
		postErrorToListener(e.getMessage());
	    throw new TransformerException(e);
	}
	catch (RuntimeException e) {
	    if (_errorListener != null)
		postErrorToListener("Runtime Error: " + e.getMessage());
	    throw new TransformerException(e);
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

	// First check if the property is overridden in this Transformer
	if (_properties != null) value = _properties.getProperty(name);

	// Then check if it is set in the translet
	if ((value == null) && (_translet != null)) {
	    // TODO: get propertie value from translet
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

	// Then return the default values
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

}
