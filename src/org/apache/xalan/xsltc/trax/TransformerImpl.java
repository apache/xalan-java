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
import org.apache.xalan.xsltc.compiler.util.ErrorMsg;

import java.util.Properties;

public final class TransformerImpl extends Transformer
    implements DOMCache, ErrorListener {

    private AbstractTranslet _translet = null;
    private String           _encoding = null;
    private ContentHandler   _handler = null;

    private ErrorListener _errorListener = this;
    private URIResolver   _uriResolver = null;
    private Properties    _properties = null;

    // Used for default output property settings
    private final static String EMPTY_STRING = "";
    private final static String NO_STRING    = "no";
    private final static String YES_STRING   = "yes";
    private final static String XML_STRING   = "xml";

    // Pre-set DOMImpl to use as input (used only with TransformerHandlerImpl)
    private DOMImpl _dom = null;

    private final static String LEXICAL_HANDLER_PROPERTY =
	"http://xml.org/sax/properties/lexical-handler";
    private static final String NAMESPACE_FEATURE =
	"http://xml.org/sax/features/namespaces";
    
    /**
     * Implements JAXP's Transformer constructor
     * Our Transformer objects always need a translet to do the actual work
     */
    protected TransformerImpl(Translet translet) {
	_translet = (AbstractTranslet)translet;
	_properties = createOutputProperties();
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
	    ErrorMsg err = new ErrorMsg(ErrorMsg.JAXP_NO_TRANSLET_ERR);
	    throw new TransformerException(err.toString());
	}

	_handler = getOutputHandler(result);
	if (_handler == null) {
	    ErrorMsg err = new ErrorMsg(ErrorMsg.JAXP_NO_HANDLER_ERR);
	    throw new TransformerException(err.toString());
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
     * the transform() method. 
     */
    private ContentHandler getOutputHandler(Result result) throws 
 	TransformerException 
    {
	// Try to get the encoding from the translet (may not be set)
	if (_translet._encoding != null) {
            _encoding = _translet._encoding;
        }
        else {
            _encoding = "UTF-8"; // default output encoding
        }

	// Return the content handler for this Result object
	try {
	    // Result object could be SAXResult, DOMResult, or StreamResult 
	    if (result instanceof SAXResult) {
                final SAXResult target = (SAXResult)result;
                final ContentHandler handler = target.getHandler();
                // Simple as feck, just pass the SAX handler back...
                if (handler != null) return handler;
            }
	    else if (result instanceof DOMResult) {
                return new SAX2DOM(((DOMResult) result).getNode());
            }
	    else if (result instanceof StreamResult) {
		// Get StreamResult
		final StreamResult target = (StreamResult)result;	

		// StreamResult may have been created with a java.io.File,
		// java.io.Writer, java.io.OutputStream or just a String
		// systemId. 

		// try to get a Writer from Result object
		final Writer writer = target.getWriter();
		if (writer != null) {
		    return (new DefaultSAXOutputHandler(writer, _encoding));
		}

		// or try to get an OutputStream from Result object
		final OutputStream ostream = target.getOutputStream();
		if (ostream != null) {
		    return (new DefaultSAXOutputHandler(ostream, _encoding));
		}

		// or try to get just a systemId string from Result object
		String systemId = result.getSystemId();
		if (systemId == null) {
		    ErrorMsg err = new ErrorMsg(ErrorMsg.JAXP_NO_RESULT_ERR);
                    throw new TransformerException(err.toString());
		}

		// System Id may be in one of several forms, (1) a uri
		// that starts with 'file:', (2) uri that starts with 'http:'
		// or (3) just a filename on the local system.
	        OutputStream os = null;
		URL url = null;
		if (systemId.startsWith("file:")) {
                    url = new URL(systemId);
                    os = new FileOutputStream(url.getFile());
		    return (new DefaultSAXOutputHandler(os, _encoding));
                }
                else if (systemId.startsWith("http:")) {
                    url = new URL(systemId);
                    URLConnection connection = url.openConnection();
                    os = connection.getOutputStream();
		    return (new DefaultSAXOutputHandler(os, _encoding));
                }
                else {
                    // system id is just a filename
                    File tmp = new File(systemId);
                    url = tmp.toURL();
                    os = new FileOutputStream(url.getFile());
		    return (new DefaultSAXOutputHandler(os, _encoding));
                }
	    }
	}
        // If we cannot write to the location specified by the SystemId
        catch (UnknownServiceException e) {
            throw new TransformerException(e);
        }
        // If we cannot create a SAX2DOM adapter
        catch (ParserConfigurationException e) {
            ErrorMsg err = new ErrorMsg(ErrorMsg.SAX2DOM_ADAPTER_ERR);
            throw new TransformerException(err.toString());
        }
        // If we cannot create the file specified by the SystemId
        catch (IOException e) {
            throw new TransformerException(e);
        }
	return null;
    }


/*************
    private ContentHandler getOutputHandler(Result result) 
	throws TransformerException {
	// Try to get the encoding from Translet (may not be set)
	if (_translet._encoding != null) {
	    _encoding = _translet._encoding;
	}
	else {
	    _encoding = "UTF-8"; // default output encoding
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
		if ((new File(systemId)).exists()) systemId = "file:"+systemId;
		final URL url = new URL(systemId);
		final URLConnection connection = url.openConnection();
		final OutputStream ostream = connection.getOutputStream();
		return(new DefaultSAXOutputHandler(ostream, _encoding));
	    }
	    else {
		ErrorMsg err = new ErrorMsg(ErrorMsg.JAXP_NO_RESULT_ERR);
		throw new TransformerException(err.toString());
	    }
	}
	// If we cannot write to the location specified by the SystemId
	catch (UnknownServiceException e) {
	    throw new TransformerException(e);
	}
	// If we cannot create a SAX2DOM adapter
	catch (ParserConfigurationException e) {
	    ErrorMsg err = new ErrorMsg(ErrorMsg.SAX2DOM_ADAPTER_ERR);
	    throw new TransformerException(err.toString());
	}
	// If we cannot create the file specified by the SystemId
	catch (IOException e) {
	    throw new TransformerException(e);
	}
    }

**********************/

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
		try {
		    reader.setProperty(LEXICAL_HANDLER_PROPERTY, builder);
		}
		catch (SAXException e) {
		    // quitely ignored
		}
		reader.setContentHandler(builder);

		// Parse the input and build the internal DOM
		reader.parse(input);
		dom.setDocumentURI(systemId);
	    }
	    // Handle DOMSource input
	    else if (source instanceof DOMSource) {
		final DOMSource   domsrc = (DOMSource)source;
		final org.w3c.dom.Node node = domsrc.getNode();

		boolean isComplete = true;
		if (node.getNodeType() != org.w3c.dom.Node.DOCUMENT_NODE) {
		    isComplete = false;
		}

		final DOM2SAX     dom2sax = new DOM2SAX(node);
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
		if (!isComplete) {
		    builder.startDocument();
		}
		dom2sax.parse(input); // need this parameter?
		if (!isComplete) {
		    builder.endDocument();
		}
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
		try {
		    factory.setFeature(NAMESPACE_FEATURE,true);
		}
		catch (Exception e) {
		    factory.setNamespaceAware(true);
		}

		final SAXParser parser = factory.newSAXParser();
		final XMLReader reader = parser.getXMLReader();

		// Create a DTD monitor to trap all DTD/declarative events
		dtd = new DTDMonitor();
		dtd.handleDTD(reader);

		// Create a new internal DOM and set up its builder to trap
		// all content/lexical events
		dom = new DOMImpl();
		final DOMBuilder builder = dom.getBuilder();
		try {
		    reader.setProperty(LEXICAL_HANDLER_PROPERTY, builder);
		}
		catch (SAXException e) {
		    // quitely ignored
		}
		reader.setContentHandler(builder);

		InputSource input;
		if (streamInput != null)
		    input = new InputSource(streamInput);
		else if (streamReader != null)
		    input = new InputSource(streamReader);
		else if (systemId != null)
		    input = new InputSource(systemId);
		else {
		    ErrorMsg err = new ErrorMsg(ErrorMsg.JAXP_NO_SOURCE_ERR);
		    throw new TransformerException(err.toString());
		}

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
	    if (_errorListener != null)	postErrorToListener(e.getMessage());
	    throw new TransformerException(e);
	}
	catch (MalformedURLException e) {
	    if (_errorListener != null)	postErrorToListener(e.getMessage());
	    throw new TransformerException(e);
	}
	catch (UnknownHostException e) {
	    if (_errorListener != null)	postErrorToListener(e.getMessage());
	    throw new TransformerException(e);
	}
	catch (Exception e) {
	    if (_errorListener != null)	postErrorToListener(e.getMessage());
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
	    if (_errorListener != null)	postErrorToListener(e.getMessage());
	    throw new TransformerException(e);
	}
	catch (RuntimeException e) {
	    if (_errorListener != null)	postErrorToListener(e.getMessage());
	    throw new TransformerException(e);
	}
	catch (Exception e) {
	    if (_errorListener != null)	postErrorToListener(e.getMessage());
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
        if (listener == null) {
	    ErrorMsg err = new ErrorMsg(ErrorMsg.ERROR_LISTENER_NULL_ERR,
					"Transformer");
            throw new IllegalArgumentException(err.toString());
	}
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
     * Implements JAXP's Transformer.getOutputProperties().
     * Returns a copy of the output properties for the transformation. This is
     * a set of layered properties. The first layer contains properties set by
     * calls to setOutputProperty() and setOutputProperties() on this class,
     * and the output settings defined in the stylesheet's <xsl:output>
     * element makes up the second level, while the default XSLT output
     * settings are returned on the third level.
     *
     * @return Properties in effect for this Transformer
     */
    public Properties getOutputProperties() {
	return(_properties);
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
	if (!validOutputProperty(name)) {
	    ErrorMsg err = new ErrorMsg(ErrorMsg.JAXP_UNKNOWN_PROP_ERR, name);
	    throw new IllegalArgumentException(err.toString());
	}
	return(_properties.getProperty(name));
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
	_properties.putAll(properties);
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
	if (!validOutputProperty(name)) {
	    ErrorMsg err = new ErrorMsg(ErrorMsg.JAXP_UNKNOWN_PROP_ERR, name);
	    throw new IllegalArgumentException(err.toString());
	}
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
	    String name  = (String)names.nextElement();
	    // bug fix # 6636- contributed by Tim Elcott
	    String value = (String)properties.getProperty(name);

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
		if ((value != null) && (value.toLowerCase().equals("yes")))
		    translet._omitHeader = true;
		else
		    translet._omitHeader = false;
	    }
	    else if (name.equals(OutputKeys.INDENT)) {
		if ((value != null) && (value.toLowerCase().equals("yes")))
		    translet._indent = true;
		else
		    translet._indent = false;
	    }
	    else if (name.equals(OutputKeys.CDATA_SECTION_ELEMENTS)) {
		if (value != null) {
		    translet._cdata = null; // clear previous setting
		    StringTokenizer e = new StringTokenizer(value);
		    while (e.hasMoreTokens()) {
			translet.addCdataElement(e.nextToken());
		    }
		}
	    }
	}
    }

    /**
     * Internal method to pass any properties to the translet prior to
     * initiating the transformation
     */
    private Properties createOutputProperties() {
	
	// Level3: Return the default property value
 	// bug # 6751 fixed by removing setProperty lines for 
  	//  OutputKeys.(DOCTYPE_PUBLIC|DOCTYPE_SYSTEM|CDATA_SECTION_ELEMENTS)
  	//  instead of setting them to "" (EMPTY_STRING). Fix contributed
  	//  by Derek Sayeau.   
	Properties third = new Properties();
	third.setProperty(OutputKeys.ENCODING, "UTF-8");
	third.setProperty(OutputKeys.METHOD, XML_STRING);
	third.setProperty(OutputKeys.INDENT, NO_STRING);
	third.setProperty(OutputKeys.MEDIA_TYPE, "text/xml");
	third.setProperty(OutputKeys.OMIT_XML_DECLARATION, NO_STRING);
	third.setProperty(OutputKeys.STANDALONE, NO_STRING);
	third.setProperty(OutputKeys.VERSION, "1.0");

	// Level2: Return the property value is set in the translet
	// Creating these properties with the third-level properties as default
	Properties second = new Properties(third);
	if (_translet != null) {
	    String value = _translet._encoding;
	    if (value != null) second.setProperty(OutputKeys.ENCODING, value);

	    value = _translet._method;
	    if (value != null) second.setProperty(OutputKeys.METHOD, value);

	    if (_translet._indent)
		second.setProperty(OutputKeys.INDENT, "yes");
	    else
		second.setProperty(OutputKeys.INDENT, "no");

	    value = _translet._doctypePublic;
	    if (value != null) 
		second.setProperty(OutputKeys.DOCTYPE_PUBLIC, value);

	    value = _translet._doctypeSystem;
	    if (value != null) 
		second.setProperty(OutputKeys.DOCTYPE_SYSTEM, value);

	    value = makeCDATAString(_translet._cdata);
	    if (value != null) 
		second.setProperty(OutputKeys.CDATA_SECTION_ELEMENTS,value);

	    value = _translet._mediaType;
	    if (value != null) second.setProperty(OutputKeys.MEDIA_TYPE, value);

	    if (_translet._omitHeader)
		second.setProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	    else
		second.setProperty(OutputKeys.OMIT_XML_DECLARATION, "no");

	    value = _translet._standalone;
	    if (value != null) second.setProperty(OutputKeys.STANDALONE, value);

	    value = _translet._version;
	    if (value != null) second.setProperty(OutputKeys.VERSION, value);
	}

	// Creating the properties with the second-level properties as default
	return(new Properties(second));
    }

    /**
     * Verifies if a given output property name is a property defined in
     * the JAXP 1.1 / TrAX spec
     */
    private boolean validOutputProperty(String name) {
	if (name.equals(OutputKeys.ENCODING)) return true;
	if (name.equals(OutputKeys.METHOD)) return true;
	if (name.equals(OutputKeys.INDENT)) return true;
	if (name.equals(OutputKeys.DOCTYPE_PUBLIC)) return true;
	if (name.equals(OutputKeys.DOCTYPE_SYSTEM)) return true;
	if (name.equals(OutputKeys.CDATA_SECTION_ELEMENTS)) return true;
	if (name.equals(OutputKeys.MEDIA_TYPE)) return true;
	if (name.equals(OutputKeys.OMIT_XML_DECLARATION)) return true;
	if (name.equals(OutputKeys.STANDALONE)) return true;
	if (name.equals(OutputKeys.VERSION)) return true;
	if (name.charAt(0) == '{') return true;
	return false;
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

    /**
     * Receive notification of a recoverable error. 
     * The transformer must continue to provide normal parsing events after
     * invoking this method. It should still be possible for the application
     * to process the document through to the end.
     *
     * @param exception The warning information encapsulated in a transformer 
     * exception.
     * @throws TransformerException if the application chooses to discontinue
     * the transformation (always does in our case).
     */
    public void error(TransformerException e)
	throws TransformerException {
	System.err.println("ERROR: "+e.getMessageAndLocation());
	throw(e); 	
    }

    /**
     * Receive notification of a non-recoverable error. 
     * The application must assume that the transformation cannot continue
     * after the Transformer has invoked this method, and should continue
     * (if at all) only to collect addition error messages. In fact,
     * Transformers are free to stop reporting events once this method has
     * been invoked.
     *
     * @param exception The warning information encapsulated in a transformer
     * exception.
     * @throws TransformerException if the application chooses to discontinue
     * the transformation (always does in our case).
     */
    public void fatalError(TransformerException e)
	throws TransformerException {
	System.err.println("FATAL: "+e.getMessageAndLocation());
	Throwable wrapped = e.getException();
	if (wrapped != null)
	    System.err.println("     : "+wrapped.getMessage());
	throw(e);
    }

    /**
     * Receive notification of a warning.
     * Transformers can use this method to report conditions that are not
     * errors or fatal errors. The default behaviour is to take no action.
     * After invoking this method, the Transformer must continue with the
     * transformation. It should still be possible for the application to
     * process the document through to the end.
     *
     * @param exception The warning information encapsulated in a transformer
     * exception.
     * @throws TransformerException if the application chooses to discontinue
     * the transformation (never does in our case).
     */
    public void warning(TransformerException e)
	throws TransformerException {
	System.err.println("WARNING: "+e.getMessageAndLocation());
	Throwable wrapped = e.getException();
	if (wrapped != null)
	    System.err.println("       : "+wrapped.getMessage());
    }

}
