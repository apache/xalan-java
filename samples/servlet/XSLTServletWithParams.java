/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights
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
 * originally based on software copyright (c) 1999, Lotus
 * Development Corporation., http://www.lotus.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package servlet;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.Enumeration;
import java.net.URL;

import org.xml.sax.*;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;

/*
 * This sample takes input parameters in the request URL: a URL
 * parameter for the XML input, an xslURL parameter for the stylesheet,
 * and optional stylesheet parameters.
 * To run the equivalent of SimplestXSLServlet (with the documents in the
 * servlet document root directory), the request URL is
 * http://<server/servletpath>servlet.SimpleXSLServlet?URL=file:todo.xml&xslURL=file:todo.xsl
 *
 * Using a stylesheet Processing Instruction:
 * If the XML document includes a stylesheet PI that you want to use, 
 * omit the xslURL parameter.
 *
 * Sending stylesheet parameters: 
 * If, for example, a servlet takes a stylesheet parameter named param1
 * param1 that you want to set to foo, include param1=foo in the URL.
 */

public class XSLTServletWithParams extends HttpServlet {

  public void init(ServletConfig config) throws ServletException
  {
    super.init(config);
  }

  public void doGet (HttpServletRequest request,
                     HttpServletResponse response)
    throws ServletException, IOException
  {
    // The servlet returns HTML; charset is UTF8.
    // See ApplyXSLT.getContentType() to get output properties from <xsl:output>.
    response.setContentType("text/html; charset=UTF-8"); 
    PrintWriter out = response.getWriter();
    try
    {	
      TransformerFactory tFactory = TransformerFactory.newInstance();
      // Get params from URL.
      String xml = getRequestParam(request, "URL");
      String xsl = getRequestParam(request, "xslURL");
      Source xmlSource = null;
      Source xslSource = null;
      Transformer transformer = null;
      // Get the XML input document.
      if (xml != null && xml.length()> 0)
        xmlSource = new StreamSource(new URL(xml).openStream());
      // Get the stylesheet.
      if (xsl != null && xsl.length()> 0)
        xslSource = new StreamSource(new URL(xsl).openStream());
      if (xmlSource != null) // We have an XML input document.
      {
        if (xslSource == null) // If no stylesheet, look for PI in XML input document.
        {
     	    String media= null , title = null, charset = null;
          xslSource = tFactory.getAssociatedStylesheet(xmlSource,media, title, charset);
        }
        if (xslSource != null) // Now do we have a stylesheet?
        {
          transformer = tFactory.newTransformer(xslSource);
          setParameters(transformer, request); // Set stylesheet params.
          // Perform the transformation.
          transformer.transform(xmlSource, new StreamResult(out)); 
        }
        else
          out.write("No Stylesheet!");
      }
      else
        out.write("No XML Input Document!");
    }
    catch (Exception e)
    {
      e.printStackTrace(out);    
    }
    out.close();
  }
  
  // Get parameters from the request URL.
  String getRequestParam(HttpServletRequest request, String param)
  {
	  if (request != null) 
    { 
	    String paramVal = request.getParameter(param); 
		  return paramVal;
	  }
	  return null;
  }
  
  // Set stylesheet parameters from the request URL.
  void setParameters(Transformer transformer, HttpServletRequest request)
  {
    Enumeration paramNames = request.getParameterNames();
    while (paramNames.hasMoreElements())
    {
      String paramName = (String) paramNames.nextElement();
      try
      {
        String paramVal = request.getParameter(paramName);
        if (paramVal != null)
          transformer.setParameter(paramName, paramVal);                                            
      }
      catch (Exception e)
      {
      }
    }
  }  
}
