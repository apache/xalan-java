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
    // Output goes in the response stream.
    PrintWriter out = new PrintWriter (response.getOutputStream());
    // This servlet is intended to return HTML.
    response.setContentType("text/html");    
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
          transformer.transform(xmlSource, new StreamResult(out)); // Perform the transformation.
        }
        else
          out.write("No Stylesheet!");
      }
      else
        out.write("No XML Input Document!");
    }
    catch (Exception e)
    {
      out.write(e.getMessage());
      e.printStackTrace(out);    
    }
    out.close();
  }
  
  // Get parameters from the request URL.
  String getRequestParam(HttpServletRequest request, String param)
  {
	  if (request != null) 
    { 
	    String[] paramVals = request.getParameterValues(param); 
	    if (paramVals != null) 
		return paramVals[0];
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
        String[] paramVals = request.getParameterValues(paramName);
        if (paramVals != null)
            transformer.setParameter(paramName, paramVals[0]);                                            
      }
      catch (Exception e)
      {
      }
    }
  }
  
}
