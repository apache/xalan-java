/*
 * @(#)$Id$
 *
 * The Apache Software License, Version 1.1
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

import java.io.*;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;

import org.xml.sax.*;

import org.apache.xalan.xsltc.*;
import org.apache.xalan.xsltc.runtime.*;
import org.apache.xalan.xsltc.dom.*;

import sunlabs.brazil.server.Handler;
import sunlabs.brazil.server.Request;
import sunlabs.brazil.server.Server;

/**
 * This Brazil handler demonstrates how XSL transformations can be made
 * available as a web service without using a full web server. This class
 * implements the Handler interface from the Brazil project, see:
 * http://www.sun.com/research/brazil/
 *
 * Note that the XSLTC transformation engine is invoked through its native
 * interface and not the javax.xml.transform (JAXP) interface. This because
 * XSLTC still does not offer precompiled transformations through JAXP.
 */
public class TransformHandler implements Handler {

    // A cache for internal DOM structures
    private static DocumentCache cache = null;

    // These two are used while parsing the parameters in the URL
    private final String PARAM_TRANSLET = "translet=";
    private final String PARAM_DOCUMENT = "document=";
    private final String PARAM_STATS = "stats=";

    // All output goes here:
    private PrintWriter _out = null;

    /**
     * Dump an error message to output
     */
    public void errorMessage(String message, Exception e) {
	if (_out == null) return;
	_out.println("<h1>XSL transformation error</h1>"+message);
	_out.println("<br>Exception:</br>"+e.toString());
    }

    public void errorMessage(String message) {
	if (_out == null) return;
	_out.println("<h1>XSL transformation error</h1>"+message);
    }

    /**
     * This method is run when the Brazil proxy is loaded
     */
    public boolean init(Server server, String prefix) {
	return true;
    }

    /**
     * This method is run for every HTTP request sent to the proxy
     */
    public boolean respond(Request request) throws IOException {

	// Initialise the output buffer
	final StringWriter sout = new StringWriter();
	_out = new PrintWriter(sout);

	// These two hold the parameters from the URL 'translet' and 'document'
	String transletName = null;
	String document = null;
	String stats = null;

	// Get the parameters from the URL
	final StringTokenizer params = new StringTokenizer(request.query,"&");
	while (params.hasMoreElements()) {
	    final String param = params.nextToken();
	    if (param.startsWith(PARAM_TRANSLET)) {
		transletName = param.substring(PARAM_TRANSLET.length());
	    }
	    else if (param.startsWith(PARAM_DOCUMENT)) {
		document = param.substring(PARAM_DOCUMENT.length());
	    }
	    else if (param.startsWith(PARAM_STATS)) {
		stats = param.substring(PARAM_STATS.length());
	    }
	}

	try {
	    // Initialize the document cache with 32 DOM slots
	    if (cache == null) cache = new DocumentCache(32);

	    // Output statistics if user looked up "server:port/?stats=xxx"
	    if (stats != null) {
		cache.getStatistics(_out); // get cache statistics (in HTML)
	    }
	    // Make sure that both parameters were specified
	    else if ((transletName == null) || (document == null)) {
		errorMessage("Parameters <b><tt>translet</tt></b> and/or "+
			     "<b><tt>document</tt></b> not specified.");
	    }
	    else {
		// Get a reference to the translet class
	        Class transletClass = Class.forName(transletName);
		// Instanciate the translet object (inherits AbstractTranslet)
		AbstractTranslet translet =
		    (AbstractTranslet)transletClass.newInstance();
		translet.setDOMCache(cache);

		// Get the DOM from the DOM cache
		DOMImpl dom = cache.retrieveDocument(document, 0, translet);

		if (dom == null) {
		    errorMessage("Could not locate: \"<b>"+document+"\"</b>");
		}
		else {
		    // Initialise the translet's output handler
		    DefaultSAXOutputHandler saxHandler = 
			new DefaultSAXOutputHandler(_out);

		    // Do the actual transformation
		    final long start = System.currentTimeMillis();
		    translet.transform(dom, new TextOutput(saxHandler));
		    final long done = System.currentTimeMillis() - start;
		    _out.println("<!-- transformed by XSLTC in "+done+"ms -->");
		}
	    }
	}
	catch (SAXException e) {
	    errorMessage("Error parsing document \""+document+"\"");
	}
	catch (ClassNotFoundException e) {
	    errorMessage("Could not locate the translet class: \""+
			 transletName+"\"<br>Exception:</br>",e);
	}
	catch (Exception e) {
	    errorMessage("Internal error.",e);
	}

	// Pass the transformation output as the HTTP response
	request.sendResponse(sout.toString());
	return true;
    }


}
