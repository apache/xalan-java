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

import java.io.*;
import java.text.*;
import java.util.*;

import java.rmi.RemoteException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.XMLReader;
import org.xml.sax.SAXException;

import org.apache.xalan.xsltc.*;
import org.apache.xalan.xsltc.runtime.*;
import org.apache.xalan.xsltc.dom.*;

public class TransformBean implements SessionBean {

    private SessionContext _context = null;
    
    private final static String nullErrorMsg =
	"<h1>XSL transformation error</h1>"+
	"<p>'null' parameters sent to the XSL transformation bean's "+
	"<tt>transform(String document, String translet)</tt> method.</p>";

    private static final String NAMESPACE_FEATURE =
	"http://xml.org/sax/features/namespaces";

    /**
     * Read the input document and build the internal "DOM" tree.
     */
    private DOMImpl getDOM(String url, AbstractTranslet translet)
	throws Exception {

	// Create a SAX parser and get the XMLReader object it uses
	final SAXParserFactory factory = SAXParserFactory.newInstance();
	try {
	    factory.setFeature(NAMESPACE_FEATURE,true);
	}
	catch (Exception e) {
	    factory.setNamespaceAware(true);
	}
	final SAXParser parser = factory.newSAXParser();
	final XMLReader reader = parser.getXMLReader();

	// Set the DOM's builder as the XMLReader's SAX2 content handler
	DOMImpl dom = new DOMImpl();
	reader.setContentHandler(dom.getBuilder());

	// Create a DTD monitor and pass it to the XMLReader object
	final DTDMonitor dtdMonitor = new DTDMonitor();
	dtdMonitor.handleDTD(reader);
	translet.setDTDMonitor(dtdMonitor);

	// Parse the input document
	reader.parse(url);

	return dom;
    }

    /**
     * Generates HTML from a basic error message and an exception
     */
    private void errorMsg(PrintWriter out, Exception e, String msg) {
	out.println("<h1>Error</h1>");
	out.println("<p>"+msg+"</p><br>");
	out.println(e.toString());
    }

    /**
     * Main bean entry point
     */
    public String transform(String document, String transletName) {

	// Initialise the output stream
	final StringWriter sout = new StringWriter();
	final PrintWriter out = new PrintWriter(sout);

	try {
	    if ((document == null) || (transletName == null)) {
		out.println(nullErrorMsg);
	    }
	    else {
		// Instanciate a translet object (inherits AbstractTranslet)
	        Class tc = Class.forName(transletName);
		AbstractTranslet translet = (AbstractTranslet)tc.newInstance();

		// Read input document from the DOM cache
		DOMImpl dom = getDOM(document, translet);

		// Initialize the (default) SAX output handler
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
	    errorMsg(out, e, "Could not locate source document: "+document);
	}
	catch (ClassNotFoundException e) {
	    errorMsg(out, e, "Could not locate the translet class: "+
		     transletName);
	}
	catch (SAXException e) {
	    errorMsg(out, e, "Error parsing document "+document);
	}
	catch (Exception e) {
	    errorMsg(out, e, "Impossible state reached.");
	}

	// Now close up the sink, and return the HTML output in the
	// StringWrite object as a string.
	out.close();
	return sout.toString();
    }

    /**
     *
     */
    public void setSessionContext(SessionContext context) {
	_context = context;
    }

    // General EJB entry points
    public void ejbCreate() { }
    public void ejbRemove() { }
    public void ejbActivate() { }
    public void ejbPassivate() { }
    public void ejbLoad() { }
    public void ejbStore() { }
}
