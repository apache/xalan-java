package org.apache.xpath;

import org.w3c.dom.Node;

import org.apache.xpath.objects.XObject;
import org.apache.xpath.res.XPATHErrorResources;
import org.apache.xalan.res.XSLMessages;
import org.xml.sax.ErrorHandler;
import trax.ProcessorException;

public abstract class Expression
{
  protected XPath m_xpath;
  
  public abstract XObject execute(XPathContext xctxt)
    throws org.xml.sax.SAXException;
  
  /**
   * Warn the user of an problem.
   */
  public void warn(XPathContext xctxt, int msg, Object[] args)
    throws org.xml.sax.SAXException
  {
    java.lang.String fmsg = XSLMessages.createXPATHWarning(msg, args); 
    
    ErrorHandler eh = xctxt.getPrimaryReader().getErrorHandler();
    if(null != eh)
    {
      // TO DO: Need to get stylesheet Locator from here.
      eh.warning(new ProcessorException(fmsg));
    }
  }

  /**
   * Tell the user of an assertion error, and probably throw an 
   * exception.
   */
  public void assert(boolean b, java.lang.String msg)
    throws org.xml.sax.SAXException
  {
    if(!b)
    {
      java.lang.String fMsg 
        = XSLMessages.createXPATHMessage(XPATHErrorResources.ER_INCORRECT_PROGRAMMER_ASSERTION, 
                                         new Object[] {msg}); 
      throw new RuntimeException(fMsg);
    }
  }

  /**
   * Tell the user of an error, and probably throw an 
   * exception.
   */
  public void error(XPathContext xctxt, int msg, Object[] args)
    throws org.xml.sax.SAXException
  {
    java.lang.String fmsg = XSLMessages.createXPATHMessage(msg, args); 
    
    ProcessorException te = new ProcessorException(fmsg, m_xpath.getLocator());
    
    ErrorHandler eh = xctxt.getPrimaryReader().getErrorHandler();
    if(null != eh)
      eh.fatalError(te);
    else
    {
      System.out.println(te.getMessage()
                         +"; file "+te.getSystemId()
                         +"; line "+te.getLineNumber()
                         +"; column "+te.getColumnNumber());
    }
  }
}
