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

package org.apache.xalan.xsltc.dom;

import java.io.File;
import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.XMLReader;
import org.xml.sax.SAXException;

import org.apache.xalan.xsltc.DOM;
import org.apache.xalan.xsltc.DOMCache;
import org.apache.xalan.xsltc.Translet;
import org.apache.xalan.xsltc.NodeIterator;
import org.apache.xalan.xsltc.TransletException;
import org.apache.xalan.xsltc.runtime.AbstractTranslet;

public final class LoadDocument {

    /**
     * Returns an iterator containing a set of nodes from an XML document
     * loaded by the document() function.
     */
    public static NodeIterator document(String uri,
					String base,
					AbstractTranslet translet,
					MultiDOM multiplexer)
	throws Exception {

	// Return an empty iterator if the URI is clearly invalid
	// (to prevent some unncessary MalformedURL exceptions).
	if ((uri == null) || (uri.equals("")))
	    return(new SingletonIterator(DOM.NULL,true));

	// Prepend URI base to URI (from context)
	if ((base != null) && (!base.equals(""))) {
	    if ((!uri.startsWith(base)) &&     // unless URI contains base
		(!uri.startsWith("/")) &&      // unless URI is abs. file path
		(!uri.startsWith("http:/")) && // unless URI is abs. http URL
		(!uri.startsWith("file:/"))) { // unless URI is abs. file URL
		uri = base+uri;
	    }
	}

	// Check if this is a local file name
	final File file = new File(uri);
	if (file.exists())
	    uri = "file:" + file.getAbsolutePath();
	
	// Check if this DOM has already been added to the multiplexer
	int mask = multiplexer.getDocumentMask(uri);
	if (mask != -1) {
	    return new SingletonIterator(DOM.ROOTNODE | mask, true);
	}

	// Check if we can get the DOM from a DOMCache
	DOMCache cache = translet.getDOMCache();
	DOMImpl dom;

	mask = multiplexer.nextMask(); // peek

	if (cache != null) {
	    dom = cache.retrieveDocument(uri, mask, translet);
	}
	else {
	    // Parse the input document and construct DOM object
	    // Create a SAX parser and get the XMLReader object it uses
	    final SAXParserFactory factory = SAXParserFactory.newInstance();
	    final SAXParser parser = factory.newSAXParser();
	    final XMLReader reader = parser.getXMLReader();

	    // Set the DOM's DOM builder as the XMLReader's SAX2 content handler
	    dom = new DOMImpl();
	    reader.setContentHandler(dom.getBuilder());
	    // Create a DTD monitor and pass it to the XMLReader object
	    DTDMonitor dtdMonitor = new DTDMonitor();
	    dtdMonitor.handleDTD(reader);

	    dom.setDocumentURI(uri);
	    reader.parse(uri);

	    // Set size of key/id indices
	    translet.setIndexSize(dom.getSize());
	    // Create index for any ID attributes defined in the document DTD
	    dtdMonitor.buildIdIndex(dom, mask, translet);
	    // Pass any unparsed URI elements to the translet
	    translet.setUnparsedEntityURIs(dtdMonitor.getUnparsedEntityURIs());
	}

	// Wrap the DOM object in a DOM adapter and add to multiplexer
	final DOMAdapter domAdapter = translet.makeDOMAdapter(dom);
	mask = multiplexer.addDOMAdapter(domAdapter);

	// Create index for any key elements
	translet.buildKeys((DOM)multiplexer, null, null, DOM.ROOTNODE | mask);

	// Return a singleton iterator containing the root node
	return new SingletonIterator(DOM.ROOTNODE | mask, true);
    }

    /**
     * Interprets the arguments passed from the document() function (see
     * org/apache/xalan/xsltc/compiler/DocumentCall.java) and returns an
     * iterator containing the requested nodes. Builds a union-iterator if
     * several documents are requested.
     */
    public static NodeIterator document(Object arg, String contextURI,
					AbstractTranslet translet,
					MultiDOM multiplexer)
	throws TransletException {
	try {

	    String baseURI = "";

	    // Get the base of the conext URI (if any)
	    if (contextURI != null) {
		final int sep = contextURI.lastIndexOf('/') + 1;
		baseURI = contextURI.substring(0, sep); // could be empty string
	    }

	    // If the argument is just a single string (an URI) we just return
	    // the nodes from the one document this URI points to.
	    if (arg instanceof String) {
		return document((String)arg, baseURI, translet, multiplexer);
	    }
	    // Otherwise we must create a union iterator, add the nodes from
	    // all the DOMs to this iterator, and return the union in the end.
	    else {
		UnionIterator union = new UnionIterator(multiplexer);
		NodeIterator iterator = (NodeIterator)arg;
		int node;

		while ((node = iterator.next()) != DOM.NULL) {
		    String uri = multiplexer.getNodeValue(node);
		    union.addIterator(document(uri, baseURI, 
					       translet, multiplexer));
		}
		return(union);
	    }
	}
	catch (Exception e) {
	    throw new TransletException(e);
	}
    }

}
