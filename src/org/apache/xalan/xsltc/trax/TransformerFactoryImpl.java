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
 * @author G. Todd Miller 
 * @author Morten Jorgensen
 *
 */


package org.apache.xalan.xsltc.trax;

import java.io.Reader;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.Vector;

import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import javax.xml.transform.sax.*;

import org.xml.sax.XMLFilter;
import org.xml.sax.InputSource;

import org.apache.xalan.xsltc.Translet;
import org.apache.xalan.xsltc.compiler.XSLTC;
import org.apache.xalan.xsltc.compiler.CompilerException;
import org.apache.xalan.xsltc.compiler.util.Util;
import org.apache.xalan.xsltc.runtime.AbstractTranslet;

/**
 * Implementation of a JAXP1.1 SAXTransformerFactory for Translets.
 */
public class TransformerFactoryImpl extends TransformerFactory {

    // This constant should be removed once all abstract methods are impl'ed.
    private static final String NYI = "Not yet implemented";

    // This error listener is used only for this factory and is not passed to
    // the Templates or Transformer objects that we create!!!
    private ErrorListener _errorListener = null; 

    // This URIResolver is passed to all created Templates and Transformers
    private URIResolver _uriResolver = null;

    // Cache for the newTransformer() method - see method for details
    private Transformer _copyTransformer = null;
    private static final String COPY_TRANSLET_NAME = "GregorSamsa";
    private static final String COPY_TRANSLET_CODE =
	"<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">"+
	"<xsl:template match=\"/\"><xsl:copy-of select=\".\"/></xsl:template>"+
	"</xsl:stylesheet>";

    // All used error messages should be listed here
    private static final String ERROR_LISTENER_NULL =
	"Attempting to set ErrorListener for TransformerFactory to null";
    private static final String UNKNOWN_SOURCE_ERR =
	"Only StreamSource and SAXSource is supported by XSLTC";
    private static final String COMPILE_ERR =
	"Could not compile stylesheet";

    /**
     * javax.xml.transform.sax.TransformerFactory implementation.
     * Contains nothing yet
     */
    public TransformerFactoryImpl() {
	// Don't need anything here so far...
    }

    /**
     * javax.xml.transform.sax.TransformerFactory implementation.
     * Set the error event listener for the TransformerFactory, which is used
     * for the processing of transformation instructions, and not for the
     * transformation itself.
     *
     * @param listener The error listener to use with the TransformerFactory
     * @throws IllegalArgumentException
     */
    public void setErrorListener(ErrorListener listener) 
	throws IllegalArgumentException {
	if (listener == null)
            throw new IllegalArgumentException(ERROR_LISTENER_NULL);
	_errorListener = listener;
    }

    /**
     * javax.xml.transform.sax.TransformerFactory implementation.
     * Get the error event handler for the TransformerFactory.
     *
     * @return The error listener used with the TransformerFactory
     */
    public ErrorListener getErrorListener() { 
	return _errorListener;
    }

    /**
     * javax.xml.transform.sax.TransformerFactory implementation.
     * Returns the value set for a TransformerFactory attribute
     * We are currently not using any attributes with XSLTC. We should try to
     * avoid using attributes as this can make us less flexible.
     *
     * @param name The attribute name
     * @return An object representing the attribute value
     * @throws IllegalArgumentException
     */
    public Object getAttribute(String name) 
	throws IllegalArgumentException { 
        throw new IllegalArgumentException(NYI);
    }

    /**
     * javax.xml.transform.sax.TransformerFactory implementation.
     * Returns the value set for a TransformerFactory attribute
     * We are currently not using any attributes with XSLTC. We should try to
     * avoid using attributes as this can make us less flexible.
     *
     * @param name The attribute name
     * @param value An object representing the attribute value
     * @throws IllegalArgumentException
     */
    public void setAttribute(String name, Object value) 
	throws IllegalArgumentException { 
        throw new IllegalArgumentException(NYI);
    }

    /**
     * javax.xml.transform.sax.TransformerFactory implementation.
     * Look up the value of a feature (to see if it is supported).
     * This method must be updated as the various methods and features of this
     * class are implemented.
     *
     * @param name The feature name
     * @return 'true' if feature is supported, 'false' if not
     */
    public boolean getFeature(String name) { 
	// All supported features should be listed here
	String[] features = {
	    SAXSource.FEATURE,
	    SAXResult.FEATURE,
	    StreamSource.FEATURE,
	    StreamResult.FEATURE
	};

	// Inefficient, but it really does not matter in a function like this
	for (int i=0; i<features.length; i++)
	    if (name.equals(features[i])) return true;

	// Feature not supported
	return false;
    }

    /**
     * javax.xml.transform.sax.TransformerFactory implementation.
     * Get the object that is used by default during the transformation to
     * resolve URIs used in document(), xsl:import, or xsl:include.
     *
     * @return The URLResolver used for this TransformerFactory and all
     * Templates and Transformer objects created using this factory
     */    
    public URIResolver getURIResolver() {
	return(_uriResolver);
    } 

    /**
     * javax.xml.transform.sax.TransformerFactory implementation.
     * Set the object that is used by default during the transformation to
     * resolve URIs used in document(), xsl:import, or xsl:include. Note that
     * this does not affect Templates and Transformers that are already
     * created with this factory.
     *
     * @param resolver The URLResolver used for this TransformerFactory and all
     * Templates and Transformer objects created using this factory
     */    
    public void setURIResolver(URIResolver resolver) {
	_uriResolver = resolver;
    }

    /**
     * javax.xml.transform.sax.TransformerFactory implementation.
     * Get the stylesheet specification(s) associated via the xml-stylesheet
     * processing instruction (see http://www.w3.org/TR/xml-stylesheet/) with
     * the document document specified in the source parameter, and that match
     * the given criteria.
     *
     * @param source The XML source document.
     * @param media The media attribute to be matched. May be null, in which
     * case the prefered templates will be used (i.e. alternate = no).
     * @param title The value of the title attribute to match. May be null.
     * @param charset The value of the charset attribute to match. May be null.
     * @return A Source object suitable for passing to the TransformerFactory.
     * @throws TransformerConfigurationException
     */
    public Source getAssociatedStylesheet(Source source, String media,
					  String title, String charset)
	throws TransformerConfigurationException {
        throw new TransformerConfigurationException(NYI);
    }

    /**
     * javax.xml.transform.sax.TransformerFactory implementation.
     * Create a Transformer object that copies the input document to the result.
     *
     * @return A Transformer object that simply copies the source to the result.
     * @throws TransformerConfigurationException
     */    
    public Transformer newTransformer()
	throws TransformerConfigurationException { 

	if (_copyTransformer != null) return _copyTransformer;

	byte[][] bytecodes = null; // The translet classes go in here

	XSLTC xsltc = new XSLTC();
	xsltc.init();

	// Compile the default copy-stylesheet
	byte[] bytes = COPY_TRANSLET_CODE.getBytes();
	ByteArrayInputStream bytestream = new ByteArrayInputStream(bytes);
	InputSource input = new InputSource(bytestream);
	input.setSystemId(COPY_TRANSLET_NAME);
	bytecodes = xsltc.compile(COPY_TRANSLET_NAME, input);

	// Check that the transformation went well before returning
	if (bytecodes == null) {
	    throw new TransformerConfigurationException(COMPILE_ERR);
	}

	// Create a Transformer object and store for other calls
	Templates templates = new TemplatesImpl(bytecodes, COPY_TRANSLET_NAME);
	_copyTransformer = templates.newTransformer();
	return(_copyTransformer);
    }

    /**
     * javax.xml.transform.sax.TransformerFactory implementation.
     * Process the Source into a Templates object, which is a a compiled
     * representation of the source. Note that this method should not be
     * used with XSLTC, as the time-consuming compilation is done for each
     * and every transformation.
     *
     * @return A Templates object that can be used to create Transformers.
     * @throws TransformerConfigurationException
     */
    public Transformer newTransformer(Source source) throws
	TransformerConfigurationException {
	Templates templates = newTemplates(source);
	return(templates.newTransformer());
    }

    /**
     * Pass warning messages from the compiler to the error listener
     */
    private void passWarningsToListener(Vector messages) {
	try {
	    // Nothing to do if there is no registered error listener
	    if (_errorListener == null) return;
	    // Nothing to do if there are not warning messages
	    if (messages == null) return;
	    // Pass messages to listener, one by one
	    final int count = messages.size();
	    for (int pos=0; pos<count; pos++) {
		String message = messages.elementAt(pos).toString();
		_errorListener.warning(new TransformerException(message));
	    }
	}
	catch (TransformerException e) {
	    // nada
	}
    }

    /**
     * Pass error messages from the compiler to the error listener
     */
    private void passErrorsToListener(Vector messages) {
	try {
	    // Nothing to do if there is no registered error listener
	    if (_errorListener == null) return;
	    // Nothing to do if there are not warning messages
	    if (messages == null) return;
	    // Pass messages to listener, one by one
	    final int count = messages.size();
	    for (int pos=0; pos<count; pos++) {
		String message = messages.elementAt(pos).toString();
		_errorListener.error(new TransformerException(message));
	    }
	}
	catch (TransformerException e) {
	    // nada
	}
    }

    /**
     * javax.xml.transform.sax.TransformerFactory implementation.
     * Process the Source into a Templates object, which is a a compiled
     * representation of the source.
     *
     * @param stylesheet The input stylesheet - DOMSource not supported!!!
     * @return A Templates object that can be used to create Transformers.
     * @throws TransformerConfigurationException
     */
    public Templates newTemplates(Source source)
	throws TransformerConfigurationException {

	// Create a placeholder for the translet bytecodes
	byte[][] bytecodes = null;

	// Create and initialize a stylesheet compiler
	XSLTC xsltc = new XSLTC();
	xsltc.init();

	InputSource input = null;
	final String systemId = source.getSystemId();

	// Try to get InputSource from SAXSource input
	if (source instanceof SAXSource) {
	    final SAXSource sax = (SAXSource)source;
	    input = sax.getInputSource();
	    // Pass the SAX parser to the compiler
	    xsltc.setXMLReader(sax.getXMLReader());
	}
	// Try to get InputStream or Reader from StreamSource
	else if (source instanceof StreamSource) {
	    final StreamSource stream = (StreamSource)source;
	    final InputStream istream = stream.getInputStream();
	    final Reader reader = stream.getReader();
	    // Create InputSource from Reader or InputStream in Source
	    if (istream != null)
		input = new InputSource(istream);
	    else if (reader != null)
		input = new InputSource(reader);
	}
	else {
	    throw new TransformerConfigurationException(UNKNOWN_SOURCE_ERR);
	}
	
	// Try to create an InputStream from the SystemId if no input so far
	if (input == null) input = new InputSource(systemId);

	// Pass system id to InputSource just to be on the safe side
	input.setSystemId(systemId);
	// Compile the stylesheet
	bytecodes = xsltc.compile(null, input);

	final String transletName = xsltc.getClassName();

	// Pass compiler warnings to the error listener
	passWarningsToListener(xsltc.getWarnings());

	// Check that the transformation went well before returning
	if (bytecodes == null) {
	    // Pass compiler errors to the error listener
	    passErrorsToListener(xsltc.getErrors());
	    throw new TransformerConfigurationException(COMPILE_ERR);
	}
	return(new TemplatesImpl(bytecodes, transletName));
    }

    /**
     * javax.xml.transform.sax.SAXTransformerFactory implementation.
     * Get a TemplatesHandler object that can process SAX ContentHandler
     * events into a Templates object.
     *
     * @return A TemplatesHandler object that can handle SAX events
     * @throws TransformerConfigurationException
     */
    public TemplatesHandler newTemplatesHandler() 
	throws TransformerConfigurationException { 
	throw new TransformerConfigurationException(NYI);
    }

    /**
     * javax.xml.transform.sax.SAXTransformerFactory implementation.
     * Get a TransformerHandler object that can process SAX ContentHandler
     * events into a Result.
     *
     * @return A TransformerHandler object that can handle SAX events
     * @throws TransformerConfigurationException
     */
    public TransformerHandler newTransformerHandler() 
	throws TransformerConfigurationException {
	throw new TransformerConfigurationException(NYI);
    }

    /**
     * javax.xml.transform.sax.SAXTransformerFactory implementation.
     * Get a TransformerHandler object that can process SAX ContentHandler
     * events into a Result, based on the transformation instructions
     * specified by the argument.
     *
     * @param src The source of the transformation instructions.
     * @return A TransformerHandler object that can handle SAX events
     * @throws TransformerConfigurationException
     */
    public TransformerHandler newTransformerHandler(Source src) 
	throws TransformerConfigurationException { 
        throw new TransformerConfigurationException(NYI);
    }

    /**
     * javax.xml.transform.sax.SAXTransformerFactory implementation.
     * Get a TransformerHandler object that can process SAX ContentHandler
     * events into a Result, based on the transformation instructions
     * specified by the argument.
     *
     * @param templates Represents a pre-processed stylesheet
     * @return A TransformerHandler object that can handle SAX events
     * @throws TransformerConfigurationException
     */    
    public TransformerHandler newTransformerHandler(Templates templates) 
	throws TransformerConfigurationException  { 
        throw new TransformerConfigurationException(NYI);
    }


    /**
     * javax.xml.transform.sax.SAXTransformerFactory implementation.
     * Create an XMLFilter that uses the given source as the
     * transformation instructions.
     *
     * @param src The source of the transformation instructions.
     * @return An XMLFilter object, or null if this feature is not supported.
     * @throws TransformerConfigurationException
     */
    public XMLFilter newXMLFilter(Source src) 
	throws TransformerConfigurationException {
	Templates templates = newTemplates(src);
	if (templates == null ) return null; 
	return newXMLFilter(templates);
    }

    /**
     * javax.xml.transform.sax.SAXTransformerFactory implementation.
     * Create an XMLFilter that uses the given source as the
     * transformation instructions.
     *
     * @param src The source of the transformation instructions.
     * @return An XMLFilter object, or null if this feature is not supported.
     * @throws TransformerConfigurationException
     */
    public XMLFilter newXMLFilter(Templates templates) 
	throws TransformerConfigurationException {
	try {
      	    return new org.apache.xalan.xsltc.trax.TrAXFilter(templates);
    	}
	catch(TransformerConfigurationException e1) {
      	    if(_errorListener != null) {
                try {
          	    _errorListener.fatalError(e1);
          	    return null;
        	}
		catch( TransformerException e2) {
          	    new TransformerConfigurationException(e2);
        	}
      	    }
      	    throw e1;
    	}
    }

}
