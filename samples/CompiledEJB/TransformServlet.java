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

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import javax.naming.*;
import javax.rmi.PortableRemoteObject;

public class TransformServlet extends HttpServlet {

    // Error message used when the XSL transformation bean cannot be created
    private final static String createErrorMsg =
	"<h1>XSL transformation bean error</h1>"+
	"<p>An XSL transformation bean could not be created.</p>";

    // Transformer - "more than meets the eye".
    private TransformHome transformer;

    /**
     * Servlet initializer - look up the bean's home interface
     */
    public void init(ServletConfig config) 
	throws ServletException{
	try{
	    InitialContext context = new InitialContext();
	    Object transformRef = context.lookup("transform");
	    transformer =
		(TransformHome)PortableRemoteObject.narrow(transformRef,
							   TransformHome.class);
	} catch (Exception NamingException) {
	    NamingException.printStackTrace();
	}
    }

    /**
     * Handles "GET" HTTP requests - ie. runs the actual transformation
     */
    public void doGet (HttpServletRequest request, 
		       HttpServletResponse response) 
	throws ServletException, IOException {

	String document = request.getParameter("document");
	String translet = request.getParameter("translet");

	response.setContentType("text/html");

	PrintWriter out = response.getWriter();
	try{
	    // Get the insult from the bean
	    TransformRemote xslt = transformer.create();
	    String result = xslt.transform(document, translet);
	    out.println(result);
	} catch(Exception CreateException){
	    out.println(createErrorMsg);
	}
	out.close();
    }

    public void destroy() {
	System.out.println("Destroy");
    }
}
