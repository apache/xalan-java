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

import java.io.PrintWriter;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLConnection;
import java.net.MalformedURLException;
import java.util.Hashtable;
import java.util.Date;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.XMLReader;
import org.xml.sax.SAXException;

import org.apache.xalan.xsltc.DOM;
import org.apache.xalan.xsltc.DOMCache;
import org.apache.xalan.xsltc.Translet;
import org.apache.xalan.xsltc.dom.DOMImpl;
import org.apache.xalan.xsltc.dom.DTDMonitor;
import org.apache.xalan.xsltc.runtime.AbstractTranslet;
import org.apache.xalan.xsltc.runtime.Constants;

public final class DocumentCache implements DOMCache {

    private int       _size;
    private Hashtable _references;
    private String[]  _URIs;
    private int       _count;
    private int       _current;
    private SAXParser _parser;
    private XMLReader _reader;

    private static final int REFRESH_INTERVAL = 1000;

    /*
     * Inner class containing a DOMImpl object and DTD handler
     */
    public final class CachedDocument {
	
	// Statistics data
	private long _firstReferenced;
	private long _lastReferenced;
	private long _accessCount;
	private long _lastModified;
	private long _lastChecked;
	private long _buildTime;

	// DOM and DTD handler references
	private DOMImpl    _dom = null;
	private DTDMonitor _dtdMonitor = null;
	
	/**
	 * Constructor - load document and initialise statistics
	 */
	public CachedDocument(String uri) {
	    // Initialise statistics variables
	    final long stamp = System.currentTimeMillis();
	    _firstReferenced = stamp;
	    _lastReferenced  = stamp;
	    _accessCount     = 0;
	    loadDocument(uri);

	    _buildTime = System.currentTimeMillis() - stamp;
	}

	/**
	 * Loads the document and updates build-time (latency) statistics
	 */
	public void loadDocument(String uri) {

	    _dom = new DOMImpl();
	    _dtdMonitor = new DTDMonitor();

	    try {
		final long stamp = System.currentTimeMillis();

		_reader.setContentHandler(_dom.getBuilder());
		_dtdMonitor.handleDTD(_reader);
		_reader.parse(uri);
		_dom.setDocumentURI(uri);

		// The build time can be used for statistics for a better
		// priority algorithm (currently round robin).
		final long thisTime = System.currentTimeMillis() - stamp;
		if (_buildTime > 0)
		    _buildTime = (_buildTime + thisTime) >>> 1;
		else
		    _buildTime = thisTime;
	    }
	    catch (Exception e) {
		_dom = null;
		_dtdMonitor = null;
	    }
	}

	public DOMImpl getDocument()       { return(_dom); }

	public DTDMonitor getDTDMonitor()  { return(_dtdMonitor); }

	public long getFirstReferenced()   { return(_firstReferenced); }

	public long getLastReferenced()    { return(_lastReferenced); }

	public long getAccessCount()       { return(_accessCount); }

	public void incAccessCount()       { _accessCount++; }

	public long getLastModified()      { return(_lastModified); }

	public void setLastModified(long t){ _lastModified = t; }

	public long getLatency()           { return(_buildTime); }

	public long getLastChecked()       { return(_lastChecked); }
	
	public void setLastChecked(long t) { _lastChecked = t; }

	public long getEstimatedSize() {
	    if (_dom != null)
		return(_dom.getSize() << 5); // ???
	    else
		return(0);
	}

    }

    /**
     * DocumentCache constructor
     */
    public DocumentCache(int size) throws SAXException {
	_count = 0;
	_current = 0;
	_size  = size;
	_references = new Hashtable(_size+2);
	_URIs = new String[_size];

	try {
	    // Create a SAX parser and get the XMLReader object it uses
	    final SAXParserFactory factory = SAXParserFactory.newInstance();
	    try {
		factory.setFeature(Constants.NAMESPACE_FEATURE,true);
	    }
	    catch (Exception e) {
		factory.setNamespaceAware(true);
	    }
	    _parser = factory.newSAXParser();
	    _reader = _parser.getXMLReader();
	}
	catch (ParserConfigurationException e) {
	    System.err.println("Your SAX parser is not configured correctly.");
	    System.exit(-1);
	}
    }

    /**
     * Returns the time-stamp for a document's last update
     */
    private final long getLastModified(String uri) {
	try {
	    URL url = new URL(uri);
	    URLConnection connection = url.openConnection();
	    long timestamp = connection.getLastModified();
	    // Check for a "file:" URI (courtesy of Brian Ewins)
	    if (timestamp == 0){ // get 0 for local URI
	        if ("file".equals(url.getProtocol())){
	            File localfile = new File(URLDecoder.decode(url.getFile()));
	            timestamp = localfile.lastModified();
	        }
	    }
	    return(timestamp);
	}
	// Brutal handling of all exceptions
	catch (Exception e) {
	    return(System.currentTimeMillis());
	}
    }

    /**
     *
     */
    private CachedDocument lookupDocument(String uri) {
	return((CachedDocument)_references.get(uri));
    }

    /**
     *
     */
    private synchronized void insertDocument(String uri, CachedDocument doc) {
	if (_count < _size) {
	    // Insert out URI in circular buffer
	    _URIs[_count++] = uri;
	    _current = 0;
	}
	else {
	    // Remove oldest URI from reference Hashtable
	    _references.remove(_URIs[_current]);
	    // Insert our URI in circular buffer
	    _URIs[_current] = uri;
	    if (++_current >= _size) _current = 0;
	}
	_references.put(uri, doc);
    }

    /**
     *
     */
    private synchronized void replaceDocument(String uri, CachedDocument doc) {
	CachedDocument old = (CachedDocument)_references.get(uri);
	if (doc == null)
	    insertDocument(uri, doc);
	else
	    _references.put(uri, doc);
    }

    /**
     * Returns a document either by finding it in the cache or
     * downloading it and putting it in the cache.
     */
    public final DOMImpl retrieveDocument(String uri, int mask, Translet trs) {
	CachedDocument doc;

	// Try to get the document from the cache first
	if ((doc = lookupDocument(uri)) == null) {
	    doc = new CachedDocument(uri);
	    if (doc == null) return null; // better error handling needed!!!
	    doc.setLastModified(getLastModified(uri));
	    insertDocument(uri, doc);
	}
	// If the document is in the cache we must check if it is still valid
	else {
	    long now = System.currentTimeMillis();
	    long chk = doc.getLastChecked();
	    doc.setLastChecked(now);
	    // Has the modification time for this file been checked lately?
	    if (now > (chk + REFRESH_INTERVAL)) {
		doc.setLastChecked(now);
		long last = getLastModified(uri);
		// Reload document if it has been modified since last download
		if (last > doc.getLastModified()) {
		    doc = new CachedDocument(uri);
		    if (doc == null) return null;
		    doc.setLastModified(getLastModified(uri));
		    replaceDocument(uri, doc);
		}
	    }
	    
	}

	// Get the references to the actual DOM and DTD handler
	final DOMImpl    dom = doc.getDocument();
	final DTDMonitor dtd = doc.getDTDMonitor();

	// The dom reference may be null if the URL pointed to a
	// non-existing document
	if (dom == null) return null;

	doc.incAccessCount(); // For statistics

	final AbstractTranslet translet = (AbstractTranslet)trs;

	// Set minimum needed size for key/id indices in the translet
	translet.setIndexSize(dom.getSize());
	// Create index for any ID attributes defined in the document DTD
	dtd.buildIdIndex(dom, mask, translet);
	// Pass all unparsed entities to the translet
	translet.setUnparsedEntityURIs(dtd.getUnparsedEntityURIs());

	return(doc.getDocument());
    }

    /**
     * Outputs the cache statistics
     */
    public void getStatistics(PrintWriter out) {
	out.println("<h2>DOM cache statistics</h2><center><table border=\"2\">"+
		    "<tr><td><b>Document URI</b></td>"+
		    "<td><center><b>Build time</b></center></td>"+
		    "<td><center><b>Access count</b></center></td>"+
		    "<td><center><b>Last accessed</b></center></td>"+
		    "<td><center><b>Last modified</b></center></td></tr>");

	for (int i=0; i<_count; i++) {
	    CachedDocument doc = (CachedDocument)_references.get(_URIs[i]);
	    out.print("<tr><td><a href=\""+_URIs[i]+"\">"+
		      "<font size=-1>"+_URIs[i]+"</font></a></td>");
	    out.print("<td><center>"+doc.getLatency()+"ms</center></td>");
	    out.print("<td><center>"+doc.getAccessCount()+"</center></td>");
	    out.print("<td><center>"+(new Date(doc.getLastReferenced()))+
		      "</center></td>");
	    out.print("<td><center>"+(new Date(doc.getLastModified()))+
		      "</center></td>");
	    out.println("</tr>");
	}

	out.println("</table></center>");
    }
}
