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
 * @author Jacek Ambroziak
 *
 */

package org.apache.xalan.xsltc.demo.servlet;

import java.io.*;
import java.text.*;
import java.util.*;
import java.net.URL;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.xalan.xsltc.compiler.*;
import org.apache.xalan.xsltc.compiler.util.*;
import org.apache.xalan.xsltc.util.getopt.*;

public class CompileServlet extends HttpServlet {

    /**
     * Main servlet entry point. The servlet reads a stylesheet from the
     * URI specified by the "sheet" parameter. The compiled Java class
     * ends up in the CWD of the web server (a better solution would be
     * to have an environment variable point to a translet directory).
     */
    public void doGet(HttpServletRequest request,
		      HttpServletResponse response)
	throws IOException, ServletException {

	response.setContentType("text/html");
	PrintWriter out = response.getWriter();
		
	String stylesheetName = request.getParameter("sheet");
	
	out.println("<html><head>");
	out.println("<title>Servlet Stylesheet Compilation</title>");
	out.println("</head><body>");

	if (stylesheetName == null) {
	    out.println("<h1>Compilation error</h1>");
	    out.println("The parameter <b><tt>sheet</tt></b> "+
			"must be specified");
	}
	else {
	    XSLTC xsltc = new XSLTC();

	    xsltc.init();
	    xsltc.compile(new URL(stylesheetName));
	    out.println("<h1>Compilation successful</h1>");
	    out.println("The stylesheet was compiled into the translet "+
			"class "+xsltc.getClassName() + " and is now "+
			"available for transformations on this server.");
	}
	out.println("</body></html>");
    }
}
