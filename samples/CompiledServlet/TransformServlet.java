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
 * @author Jacek Ambroziak
 *
 */

import java.io.*;
import java.text.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.xml.sax.*;

import org.apache.xalan.xsltc.*;
import org.apache.xalan.xsltc.runtime.*;
import org.apache.xalan.xsltc.dom.*;

/**
 * This servlet demonstrates how XSL transformations can be made available as
 * a web service. See the CompileServlet for an example on how stylesheets
 * can be pre-compiled before this servlet is invoked.
 *
 * Note that the XSLTC transformation engine is invoked through its native
 * interface and not the javax.xml.transform (JAXP) interface. This because
 * XSLTC still does not offer precompiled transformations through JAXP.
 */
public final class TransformServlet extends HttpServlet {

    /*
     * This is a document cache with 32 document slots. This servlet returns
     * cache statistics if the 'stats' parameter is passed with the HTTP
     * request in doGet().
     */
    private static DocumentCache cache = null;

    /**
     * Main servlet entry point
     */
    public void doGet(HttpServletRequest request,
		      HttpServletResponse response)
	throws IOException, ServletException {

	// Initialise the output writer
	response.setContentType("text/html");
	PrintWriter out = response.getWriter();

	// Get the two paramters "class" and "source".
	String className   = request.getParameter("class");
	String documentURI = request.getParameter("source");

	try {
	    // Initialize document cache with 32 DOM slots
	    if (cache == null) cache = new DocumentCache(32);

	    if (request.getParameter("stats") != null) {
		cache.getStatistics(out);
	    }
	    else if ((className == null) || (documentURI == null)) {
	        out.println("<h1>XSL transformation error</h1>");
		out.println("The parameters <b><tt>class</tt></b> and " +
			    "<b><tt>source</tt></b> must be specified");
	    }
	    else {
		// Get a reference to the translet class (not object yet)
	        Class tc = Class.forName(className);
		// Instanciate a translet object (inherits AbstractTranslet)
		AbstractTranslet translet = (AbstractTranslet)tc.newInstance();

		// Set the document cache for the translet. This is needed in
		// case the translet uses the document() function.
		translet.setDOMCache(cache);

		// Read input document from the DOM cache
		DOMImpl dom = cache.retrieveDocument(documentURI, 0, translet);

		// Initialize the output handler
		DefaultSAXOutputHandler saxHandler = 
		    new DefaultSAXOutputHandler(out);

		// Start the transformation
		final long start = System.currentTimeMillis();
		translet.transform(dom, new TextOutput(saxHandler));
		final long done = System.currentTimeMillis() - start;
		out.println("<!-- transformed by XSLTC in "+done+"msecs -->");
	    }
	}
	catch (IOException e) {
	    out.println("<h1>Error</h1>");
	    out.println("Could not locate source document: " + documentURI);
	    out.println(e.toString());
	}
	catch (ClassNotFoundException e) {
	  out.println("<h1>Error</h1>");
	  out.println("Could not locate the translet class: " + className);
	  out.println(e.toString());
	}
	catch (SAXException e) {
	    out.println("<h1>Error</h1>");
	    out.println("Error parsing document " + documentURI);
	}
	catch (Exception e) {
	    out.println("<h1>Error</h1>");
	    out.println(e.toString());
	}
    }
}
