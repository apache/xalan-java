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
 *
 */

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import javax.ejb.SessionBean;
import javax.ejb.SessionContext;

public class TransformBean implements SessionBean {

    private SessionContext m_context = null;
    
    private final static String nullErrorMsg =
	"<h1>XSL transformation error</h1>"+
	"<p>'null' parameters sent to the XSL transformation bean's "+
	"<tt>transform(String document, String translet)</tt> method.</p>";

    private static final String NAMESPACE_FEATURE =
	"http://xml.org/sax/features/namespaces";

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
                TransformerFactory tf = TransformerFactory.newInstance();
                try {
                    tf.setAttribute("use-classpath", Boolean.TRUE);
                } catch (IllegalArgumentException iae) {
                    System.err.println(
                        "Could not set XSLTC-specific TransformerFactory "
                      + "attributes.  Transformation failed.");
                }

                Transformer t =
                    tf.newTransformer(new StreamSource(transletName));

                // Do the actual transformation
                final long start = System.currentTimeMillis();
                t.transform(new StreamSource(document),
                            new StreamResult(out));
                final long done = System.currentTimeMillis() - start;
                out.println("<!-- transformed by XSLTC in "+done+"msecs -->");
	    }
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
	m_context = context;
    }

    // General EJB entry points
    public void ejbCreate() { }
    public void ejbRemove() { }
    public void ejbActivate() { }
    public void ejbPassivate() { }
    public void ejbLoad() { }
    public void ejbStore() { }
}
