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
// Transformations for XML (TRaX)
// Copyright ©2000 Lotus Development Corporation, Exoffice Technologies,
// Oracle Corporation, Michael Kay of International Computers Limited, Apache
// Software Foundation.  All rights reserved.
package org.apache.trax;

import org.xml.sax.SAXParseException;
import org.xml.sax.SAXException;
import org.xml.sax.Locator;
import org.xml.sax.helpers.LocatorImpl;

/**
 * This simply subclasses the TransformException for the purposes 
 * of being able to be caught in a catch clause.
 *
 * <h3>Open issues:</h3>
 * <dl>
 *    <dt><h4>No open issues are known for this class</h4></dt>
 *    <dd></dd>
 * </dl>
 * 
 * @version Alpha
 * @author <a href="mailto:scott_boag@lotus.com">Scott Boag</a>
 */
public class TransformException extends SAXParseException
{
  /**
   * Create a new TransformException.
   *
   * @param message The error or warning message.
   * @see org.xml.sax.SAXException
   */
  public TransformException (String message) 
  {
    super(message, new LocatorImpl());
  }

  /**
   * Create a new TransformException wrapping an existing exception.
   *
   * @param e The exception to be wrapped in a SAXException.
   * @see org.xml.sax.SAXException
   */
  public TransformException (Exception e)
  {
    super(e.getMessage(), new LocatorImpl(), e);
  }
  
  /**
   * Create a new TransformException wrapping an existing exception.
   *
   * @param e The exception to be wrapped in a SAXException.
   * @see org.xml.sax.SAXException
   */
  public TransformException (Exception e, Locator locator)
  {
    super(e.getMessage(), locator, e);
  }

  /**
   * Wrap an existing exception in a TransformException.
   *
   * <p>This is used for throwing processor exceptions before 
   * the processing has started.</p>
   *
   * @param message The error or warning message, or null to
   *                use the message from the embedded exception.
   * @param e Any exception
   * @see org.xml.sax.Locator
   * @see org.xml.sax.Parser#setLocale
   */
  public TransformException (String message, Exception e) 
  {
    super( message, new LocatorImpl(), e);
  }
  
  /**
   * Create a new TransformException from a message and a Locator.
   *
   * <p>This constructor is especially useful when an application is
   * creating its own exception from within a DocumentHandler
   * callback.</p>
   *
   * @param message The error or warning message.
   * @param locator The locator object for the error or warning.
   * @see org.xml.sax.Locator
   * @see org.xml.sax.Parser#setLocale 
   */
  public TransformException (String message, Locator locator) 
  {
    super(message, locator);
  }
  
  /**
   * Wrap an existing exception in a TransformException.
   *
   * <p>This constructor is especially useful when an application is
   * creating its own exception from within a DocumentHandler
   * callback, and needs to wrap an existing exception that is not a
   * subclass of SAXException.</p>
   *
   * @param message The error or warning message, or null to
   *                use the message from the embedded exception.
   * @param locator The locator object for the error or warning.
   * @param e Any exception
   * @see org.xml.sax.Locator
   * @see org.xml.sax.Parser#setLocale
   */
  public TransformException (String message, Locator locator,
                             Exception e) 
  {
    super( message, locator, e);
  }
  
  /**
   * Create a new TransformException.
   *
   * <p>This constructor is most useful for parser writers.</p>
   *
   * <p>If the system identifier is a URL, the parser must resolve it
   * fully before creating the exception.</p>
   *
   * @param message The error or warning message.
   * @param publicId The public identifer of the entity that generated
   *                 the error or warning.
   * @param systemId The system identifer of the entity that generated
   *                 the error or warning.
   * @param lineNumber The line number of the end of the text that
   *                   caused the error or warning.
   * @param columnNumber The column number of the end of the text that
   *                     cause the error or warning.
   * @see org.xml.sax.Parser#setLocale
   */
  public TransformException (String message, 
                             String publicId, String systemId,
                             int lineNumber, int columnNumber)
  {
    super(message, publicId, systemId, lineNumber, columnNumber);
  }
  
  
  /**
   * Create a new TransformException with an embedded exception.
   *
   * <p>This constructor is most useful for parser writers who
   * need to wrap an exception that is not a subclass of
   * SAXException.</p>
   *
   * <p>If the system identifier is a URL, the parser must resolve it
   * fully before creating the exception.</p>
   *
   * @param message The error or warning message, or null to use
   *                the message from the embedded exception.
   * @param publicId The public identifer of the entity that generated
   *                 the error or warning.
   * @param systemId The system identifer of the entity that generated
   *                 the error or warning.
   * @param lineNumber The line number of the end of the text that
   *                   caused the error or warning.
   * @param columnNumber The column number of the end of the text that
   *                     cause the error or warning.
   * @param e Another exception to embed in this one.
   * @see org.xml.sax.Parser#setLocale
   */
  public TransformException (String message, String publicId, String systemId,
                             int lineNumber, int columnNumber, Exception e)
  {
    super(message, publicId, systemId, lineNumber, columnNumber, e);
  }
  
  
  /**
   * Print the the trace of methods from where the error 
   * originated.  This will trace all nested exception 
   * objects, as well as this object.
   * @param s The stream where the dump will be sent to.
   */
  public void printStackTrace(java.io.PrintStream s) 
  {
    if(s == null)
      s = System.err;
    try
    {
      super.printStackTrace(s);
    }
    catch(Exception e){}
    Exception exception = getException();
    for(int i = 0; (i < 10) && (null != exception); i++)
    {
      s.println("---------");
      exception.printStackTrace(s);
      if(exception instanceof SAXException)
      {
        SAXException se = (SAXException)exception;
        Exception prev = exception;
        exception = se.getException();
        if(prev == exception)
          break;
      }
      else
      {
        exception = null;
      }
    }
  }
  
  private boolean isSimilar(Exception e1, Exception e2)
  {
    boolean isSimilar = false;
    if((e1 instanceof SAXParseException) && 
       (e2 instanceof SAXParseException))
    {
      // If the file and line number are the same, then only 
      // report the top-level error.
      SAXParseException spe1 = (SAXParseException)e1;
      String oldSystemID = spe1.getSystemId();
      int oldLine = spe1.getLineNumber();
      int oldColumn = spe1.getColumnNumber();

      SAXParseException spe2 = (SAXParseException)e2;
      String newSystemID = spe2.getSystemId();
      int newLine = spe2.getLineNumber();
      int newColumn = spe2.getColumnNumber();
      
      if(oldSystemID == null)
        oldSystemID = "";
      if(newSystemID == null)
        newSystemID = "";

      isSimilar = (oldSystemID.equals(newSystemID) 
                   && (oldLine == newLine)
                   && (oldColumn == newColumn));  
    }
    return isSimilar;
  }
  
  private void appendMessageAndInfo(StringBuffer sbuffer)
  {
    String message = super.getMessage();
    String systemID = getSystemId();
    int line = getLineNumber();
    int column = getColumnNumber();
    
    if(null != message)
    {
      sbuffer.append(message);
    }
    if(null != systemID)
    {
      sbuffer.append("; SystemID: ");
      sbuffer.append(systemID);
    }
    if(0 != line)
    {
      sbuffer.append("; Line#: ");
      sbuffer.append(line);
    }
    if(0 != column)
    {
      sbuffer.append("; Column#: ");
      sbuffer.append(column);
    }
  }
  
  /**
   * Find the most contained message.
   * @returns The error message of the originating exception.
   */
  public String getMessage() 
  {
    StringBuffer sbuffer = new StringBuffer();
    
    appendMessageAndInfo(sbuffer);
    
    Exception prev = this;

    Exception exception = getException();
    while(null != exception)
    {      
      if((!((exception instanceof TransformException) || 
           (exception instanceof ProcessorException))) &&
         (exception instanceof SAXException))
      {
        if(exception instanceof SAXParseException)
        {          
          if(!isSimilar(prev, exception))
          {
            SAXParseException spe = (SAXParseException)exception;
            String message = spe.getMessage();
            if(null != message)
            {
              sbuffer.append("\n (");
              sbuffer.append( spe.getClass().getName());
              sbuffer.append( "): ");
              sbuffer.append(message);
            }

            if(null != spe.getSystemId())
            {
              sbuffer.append("; SystemID: ");
              sbuffer.append(spe.getSystemId());
            }
            if(0 != spe.getLineNumber())
            {
              sbuffer.append("; Line#: ");
              sbuffer.append(spe.getLineNumber());
            }
            if(0 != spe.getColumnNumber())
            {
              sbuffer.append("; Column#: ");
              sbuffer.append(spe.getColumnNumber());
            }
          }
        }
        else if(!isSimilar(prev, exception))
        {
          String message = exception.getMessage();
          if(null != message)
          {
            sbuffer.append("\n (");
            sbuffer.append( exception.getClass().getName());
            sbuffer.append( "): ");
            sbuffer.append(message);
          }
        }
        
        prev = exception;
        exception = ((SAXException)exception).getException();
      }
      else if(!isSimilar(prev, exception))
      {
        String message = exception.getMessage();
        if(null != message)
        {
          sbuffer.append("\n (");
          sbuffer.append( exception.getClass().getName());
          sbuffer.append( "): ");
          sbuffer.append(message);
        }
        exception = null;
      }
    }
    return sbuffer.toString();
  }

  /**
   * Print the the trace of methods from where the error 
   * originated.  This will trace all nested exception 
   * objects, as well as this object.
   * @param s The writer where the dump will be sent to.
   */
  public void printStackTrace(java.io.PrintWriter s) 
  {
    if(s == null)
      s = new java.io.PrintWriter(System.err);
    try
    {
      super.printStackTrace(s);
    }
    catch(Exception e){}
    Exception exception = getException();
    
    for(int i = 0; (i < 10) && (null != exception); i++)
    {
      s.println("---------");
      try
      {
        exception.printStackTrace(s);
      }
      catch(Exception e)
      {
        s.println("Could not print stack trace...");
      }
      if(exception instanceof SAXException)
      {
        SAXException se = (SAXException)exception;
        Exception prev = exception;
        exception = se.getException();
        if(prev == exception)
        {
          exception = null;
          break;
        }
      }
      else
      {
        exception = null;
      }
    }
  }


}
