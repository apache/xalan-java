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
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.lang.IllegalArgumentException;

import org.xml.sax.XMLReader;
import org.xml.sax.ContentHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;

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

    public TransformerImpl(Translet translet) {
	_translet = (AbstractTranslet)translet;
    }

    public void transform(Source source, Result result)
	throws TransformerException {

	if (_translet == null) return;
	_handler = getOutputHandler(result);
	if (_handler == null) return;
	
	// finally do the transformation...
	doTransform(source.getSystemId(), _handler, _encoding);
    }

    private ContentHandler getOutputHandler(Result result) {
	// Try to get the encoding from Translet (may not be set)
	_encoding = _translet.getOutputEncoding();
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
	}
	catch (java.io.FileNotFoundException e) {
	    throw new TransformerException(e);
	}
	catch (java.io.IOException e) {
	    throw new TransformerException(e);
	}
	finally {
	    return null;
	}
    }
 
    private void doTransform(String source, ContentHandler handler,
			     String encoding) {
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
 
	    dom.setDocumentURI(source);
	    if (source.startsWith("file:/")) {   
		reader.parse(source);
	    } else {                                
	        reader.parse("file:"+(new File(source).getAbsolutePath()));
	    }

	    // Set size of key/id indices
	    _translet.setIndexSize(dom.getSize());
	    // If there are any elements with ID attributes, build an index
	    dtdMonitor.buildIdIndex(dom, 0, _translet);
	    // Pass unparsed entity URIs to the translet
	    _translet.setDTDMonitor(dtdMonitor);
 
	    // Transform the document
	    TextOutput textOutput = new TextOutput(handler, _encoding);
	    _translet.transform(dom, textOutput);
	}
	catch (TransletException e) {
	    if (_errorListener != null) {
		postErrorToListener(e.getMessage());
	    } else {
	        System.err.println("\nTranslet Error: " + e.getMessage());
	    }
	    System.exit(1);
	}
	catch (RuntimeException e) {
	    if (_errorListener != null) {
		postErrorToListener("Runtime Error: " + e.getMessage());
	    } else {
	        System.err.println("\nRuntime Error: " + e.getMessage());
	    }
	    System.exit(1);
	}
	catch (FileNotFoundException e) {
	    if (_errorListener != null) {
		postErrorToListener("File not found: " + e.getMessage());
	    } else {
		System.err.println("Error: File not found:"+e.getMessage());
	    }
	    System.exit(1);
	}
	catch (MalformedURLException e) {
	    if (_errorListener != null) {
		postErrorToListener("Malformed URL: " + e.getMessage());
	    } else {
	        System.err.println("Error: Malformed URL: "+e.getMessage());
	    }
	    System.exit(1);
	}
	catch (UnknownHostException e) {
	    if (_errorListener != null) {
		postErrorToListener("Cannot resolve URI: " + e.getMessage());
	    } else {
	        System.err.println("Error: Cannot resolve URI: "+
				   e.getMessage());
	    }
	    System.exit(1);
	}
	catch (Exception e) {
	    if (_errorListener != null) {
		postErrorToListener("Internal error: " + e.getMessage()); 
	    } else {
	        System.err.println("Internal error: "+e.getMessage());
	        e.printStackTrace();
	    }
	    System.exit(1);
	}
    }

    // TrAX support methods, get/setErrorListener
    private ErrorListener _errorListener = null;

    /**
     * Get the TrAX error listener
     */
    public ErrorListener getErrorListener() {  
	return _errorListener; 
    }

    /**
     * Set the TrAX error listener
     */
    public void setErrorListener(ErrorListener listener)
	throws IllegalArgumentException {
        if (listener == null) {
            throw new IllegalArgumentException(
	       "Error: setErrorListener() call where ErrorListener is null");
        }
        _errorListener = listener;
    }

    /**
     * Inform TrAX error listener of an error
     */
    private void postErrorToListener(String msg) {
        try {
            _errorListener.error(new TransformerException(
		  "Translet Error: " + msg));
        } catch (TransformerException e) {
            // TBD
        }
    }

    /**
     * Inform TrAX error listener of a warning
     */
    private void postWarningToListener(String msg) {
        try {
            _errorListener.warning(new TransformerException(
		    "Translet Warning: " + msg));
        } catch (TransformerException e) {
            // TBD
        }
    }

    /**
     * Implements JAXP's Transformer.getOutputProperties().
     * Returns a copy of the output properties for the transformation.
     */
    public Properties getOutputProperties() throws IllegalArgumentException { 
	// TODO
	return(null);
    }

    /**
     * Implements JAXP's Transformer.getOutputProperty().
     * Set an output property that will be in effect for the transformation.
     */
    public String getOutputProperty(String name)
	throws IllegalArgumentException { 
	// TODO
	return(null);
    }

    /**
     * Implements JAXP's Transformer.setOutputProperties().
     * Set the output properties for the transformation. These properties
     * will override properties set in the Templates with xsl:output.
     */
    public void setOutputProperties(Properties props)
	throws IllegalArgumentException {
	// TODO
    }

    /**
     * Implements JAXP's Transformer.setOutputProperty().
     * Get an output property that is in effect for the transformation. The
     * property specified may be a property that was set with
     * setOutputProperty(), or it may be a property specified in the stylesheet.
     */
    public void setOutputProperty(String name, String value)
	throws 	IllegalArgumentException  {
	// TODO
    }

    /**
     * Implements JAXP's Transformer.setParameter()
     * Add a parameter for the transformation.
     */
    public void setParameter(String name, Object value) { 
	_translet.addParameter(name, value, false);
    }

    /**
     * Implements JAXP's Transformer.clearParameters()
     * Clears the parameter stack.
     */
    public void clearParameters() {  
	_translet.clearParameters();
    }

    /**
     * Implements JAXP's Transformer.getParameter()
     * Returns the value of a given parameter
     */
    public final Object getParameter(String name) {
	return(_translet.getParameter(name));
    }

    /**
     * These two methods need to pass the URI resolver to the dom/LoadDocument
     * class, which again must use the URI resolver if present.
     */
    public URIResolver getURIResolver() {
	// TODO
	return null;
    }

    public void setURIResolver(URIResolver resolver) { 
	// TODO
    }

}
