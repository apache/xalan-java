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
 * @author Santiago Pericas-Geertsen
 *
 */

package org.apache.xalan.xsltc.trax;

import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.TemplatesHandler;

import org.apache.xalan.xsltc.compiler.CompilerException;
import org.apache.xalan.xsltc.compiler.Parser;
import org.apache.xalan.xsltc.compiler.SourceLoader;
import org.apache.xalan.xsltc.compiler.Stylesheet;
import org.apache.xalan.xsltc.compiler.SyntaxTreeNode;
import org.apache.xalan.xsltc.compiler.XSLTC;

import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;

/**
 * Implementation of a JAXP1.1 TemplatesHandler
 */
public class TemplatesHandlerImpl 
    implements ContentHandler, TemplatesHandler, SourceLoader
{
    /**
     * System ID for this stylesheet.
     */
    private String _systemId;

    /**
     * Number of spaces to add for output indentation.
     */
    private int _indentNumber;

    /**
     * This URIResolver is passed to all Transformers.
     */
    private URIResolver _uriResolver = null;

    /**
     * A reference to the transformer factory that this templates
     * object belongs to.
     */
    private TransformerFactoryImpl _tfactory = null;
    
    /**
     * A reference to XSLTC's parser object.
     */
    private Parser _parser = null;

    /**
     * Default constructor
     */
    protected TemplatesHandlerImpl(int indentNumber,
	TransformerFactoryImpl tfactory)
    {
	_indentNumber = indentNumber;
	_tfactory = tfactory;
    
        // Initialize a parser object
        XSLTC xsltc = new XSLTC();
        xsltc.init();
        xsltc.setOutputType(XSLTC.BYTEARRAY_OUTPUT);
        _parser = xsltc.getParser();
    }

    /**
     * Implements javax.xml.transform.sax.TemplatesHandler.getSystemId()
     * Get the base ID (URI or system ID) from where relative URLs will be
     * resolved.
     * @return The systemID that was set with setSystemId(String id)
     */
    public String getSystemId() {
	return _systemId;
    }

    /**
     * Implements javax.xml.transform.sax.TemplatesHandler.setSystemId()
     * Get the base ID (URI or system ID) from where relative URLs will be
     * resolved.
     * @param id Base URI for this stylesheet
     */
    public void setSystemId(String id) {
	_systemId = id;
    }

    /**
     * Store URIResolver needed for Transformers.
     */
    public void setURIResolver(URIResolver resolver) {
	_uriResolver = resolver;
    }

    /**
     * Implements javax.xml.transform.sax.TemplatesHandler.getTemplates()
     * When a TemplatesHandler object is used as a ContentHandler or
     * DocumentHandler for the parsing of transformation instructions, it
     * creates a Templates object, which the caller can get once the SAX
     * events have been completed.
     * @return The Templates object that was created during the SAX event
     *         process, or null if no Templates object has been created.
     */
    public Templates getTemplates() {
	try {
	    XSLTC xsltc = _parser.getXSLTC();

	    // Set a document loader (for xsl:include/import) if defined
	    if (_uriResolver != null) {
		xsltc.setSourceLoader(this);
	    }

	    // Set the translet class name if not already set
	    String transletName = null;
	    if (_systemId != null) {
		transletName = Util.baseName(_systemId);
	    }
	    else {
	    	transletName = (String)_tfactory.getAttribute("translet-name");
	    }
	    xsltc.setClassName(transletName);

	    // Get java-legal class name from XSLTC module
	    transletName = xsltc.getClassName();

	    Stylesheet stylesheet = null;
	    SyntaxTreeNode root = _parser.getDocumentRoot();

	    // Compile the translet - this is where the work is done!
	    if (!_parser.errorsFound() && root != null) {
		// Create a Stylesheet element from the root node
		stylesheet = _parser.makeStylesheet(root);
		stylesheet.setSystemId(_systemId);
		stylesheet.setParentStylesheet(null);
		_parser.setCurrentStylesheet(stylesheet);

		// Set it as top-level in the XSLTC object
		xsltc.setStylesheet(stylesheet);

		// Create AST under the Stylesheet element
		_parser.createAST(stylesheet);
	    }

	    // Generate the bytecodes and output the translet class(es)
	    if (!_parser.errorsFound() && stylesheet != null) {
		stylesheet.setMultiDocument(xsltc.isMultiDocument());

                // Class synchronization is needed for BCEL
                synchronized (xsltc.getClass()) {
                    stylesheet.translate();
                }
	    }

	    if (!_parser.errorsFound()) {
		// Check that the transformation went well before returning
		final byte[][] bytecodes = xsltc.getBytecodes();
		if (bytecodes != null) {
		    final TemplatesImpl templates =
			new TemplatesImpl(xsltc.getBytecodes(), transletName,
			    _parser.getOutputProperties(), _indentNumber, _tfactory);

		    // Set URIResolver on templates object
		    if (_uriResolver != null) {
			templates.setURIResolver(_uriResolver);
		    }
		    return templates;
		}
	    }
	}
	catch (CompilerException e) {
	    // falls through
	}
	return null;
    }

    /**
     * This method implements XSLTC's SourceLoader interface. It is used to
     * glue a TrAX URIResolver to the XSLTC compiler's Input and Import classes.
     *
     * @param href The URI of the document to load
     * @param context The URI of the currently loaded document
     * @param xsltc The compiler that resuests the document
     * @return An InputSource with the loaded document
     */
    public InputSource loadSource(String href, String context, XSLTC xsltc) {
	try {
	    // A _uriResolver must be set if this method is called
	    final Source source = _uriResolver.resolve(href, context);
	    if (source != null) {
		return Util.getInputSource(xsltc, source);
	    }
	}
	catch (TransformerException e) {
	    // Falls through
	}
	return null;
    }
    
    // -- ContentHandler --------------------------------------------------
    
    /**
     * Re-initialize parser and forward SAX2 event.
     */
    public void startDocument() {
        _parser.init();
        _parser.startDocument();
    }

    /**
     * Just forward SAX2 event to parser object.
     */
    public void endDocument() { 
        _parser.endDocument();
    }

    /**
     * Just forward SAX2 event to parser object.
     */
    public void startPrefixMapping(String prefix, String uri) {
        _parser.startPrefixMapping(prefix, uri);
    }

    /**
     * Just forward SAX2 event to parser object.
     */
    public void endPrefixMapping(String prefix) { 
        _parser.endPrefixMapping(prefix);
    }

    /**
     * Just forward SAX2 event to parser object.
     */
    public void startElement(String uri, String localname, String qname, 
        Attributes attributes) throws SAXException 
    {
        _parser.startElement(uri, localname, qname, attributes);
    }
    
    /**
     * Just forward SAX2 event to parser object.
     */
    public void endElement(String uri, String localname, String qname) {
        _parser.endElement(uri, localname, qname);
    }

    /**
     * Just forward SAX2 event to parser object.
     */
    public void characters(char[] ch, int start, int length) {
        _parser.characters(ch, start, length);
    }
    
    /**
     * Just forward SAX2 event to parser object.
     */
    public void processingInstruction(String name, String value) {
        _parser.processingInstruction(name, value);
    }
    
    /**
     * Just forward SAX2 event to parser object.
     */
    public void ignorableWhitespace(char[] ch, int start, int length) { 
        _parser.ignorableWhitespace(ch, start, length);
    }

    /**
     * Just forward SAX2 event to parser object.
     */
    public void skippedEntity(String name) { 
        _parser.skippedEntity(name);
    }

    /**
     * Set internal system Id and forward SAX2 event to parser object.
     */
    public void setDocumentLocator(Locator locator) {
        setSystemId(locator.getSystemId());
        _parser.setDocumentLocator(locator);
    }
}


