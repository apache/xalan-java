package servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.net.URL;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;

/*
 * This sample applies the todo.xsl stylesheet to the
 * todo.xml XML document, and returns the transformation
 * output (HTML) to the client browser.
 *
 * IMPORTANT: For this to work, you must place todo.xsl and todo.xml 
 * in the servlet root directory for documents.
 *
 */

public class SimpleXSLTServlet extends HttpServlet {

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
    // The servlet returns HTML.
    response.setContentType("text/html");    
    try
    {	
      TransformerFactory tFactory = TransformerFactory.newInstance();
      // Get the XML input document and the stylesheet.
      Source xmlSource = new StreamSource(new URL("file:todo.xml").openStream());
      Source xslSource = new StreamSource(new URL("file:todo.xsl").openStream());
      // Generate the transformer.
      Transformer transformer = tFactory.newTransformer(xslSource);
      // Perform the transformation, sending the output to the response.
      transformer.transform(xmlSource, new StreamResult(out));
    }
    catch (Exception e)
    {
      out.write(e.getMessage());
      e.printStackTrace(out);    
    }
    out.close();
  }
  
}
