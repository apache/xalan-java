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
 * This exception serves as a root exception of TRaX exception, and
 * is thrown in raw form when an exceptional condition occurs in the
 * Processor object.
 *
 * <h3>Open issues:</h3>
 * <dl> *    <dt><h4>Abstract exception root?</h4></dt>
 *    <dd>Should the root TRaX exception be abstract?</dd>
 *    <dt><h4>Derive from SAXException?</h4></dt>
 *    <dd>Keith Visco writes: I don't think these exceptions should extend
 *        SAXException, but could nest a SAXException if necessary.</dd>
 * </dl> 
 * @version Alpha
 * @author <a href="mailto:scott_boag@lotus.com">Scott Boag</a>
 */
public class ProcessorException extends SAXParseException
{

  //////////////////////////////////////////////////////////////////////
  // Constructors.
  //////////////////////////////////////////////////////////////////////

  /**
   * Create a new ProcessorException from a message.
   *
   * <p>This constructor is especially useful when an application is
   * creating its own exception from within a DocumentHandler
   * callback.</p>
   *
   * @param message The error or warning message.
   * @see org.xml.sax.Locator
   * @see org.xml.sax.Parser#setLocale
   */
  public ProcessorException(String message)
  {
    super(message, null);
  }

  /**
   * Create a new ProcessorException from a message and a Locator.
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
  public ProcessorException(String message, Locator locator)
  {
    super(message, locator);
  }

  /**
   * Wrap an existing exception in a ProcessorException.
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
  public ProcessorException(String message, Locator locator, Exception e)
  {
    super(message, locator, e);
  }

  /**
   * Wrap an existing exception in a ProcessorException.
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
  public ProcessorException(String message, Exception e)
  {
    super("TRaX Processor Exception", new LocatorImpl(), e);
  }

  /**
   * Create a new ProcessorException.
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
  public ProcessorException(String message, String publicId, String systemId,
                            int lineNumber, int columnNumber)
  {
    super(message, publicId, systemId, lineNumber, columnNumber);
  }

  /**
   * Create a new ProcessorException with an embedded exception.
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
  public ProcessorException(String message, String publicId, String systemId,
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

    if (s == null)
      s = System.err;

    try
    {
      super.printStackTrace(s);
    }
    catch (Exception e){}

    Exception exception = getException();

    for (int i = 0; (i < 10) && (null != exception); i++)
    {
      s.println("---------");
      exception.printStackTrace(s);

      if (exception instanceof SAXException)
      {
        SAXException se = (SAXException) exception;
        Exception prev = exception;

        exception = se.getException();

        if (prev == exception)
          break;
      }
      else
      {
        exception = null;
      }
    }
  }

  /**
   * Find the most contained message.
   * @returns The error message of the originating exception.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public String getMessage()
  {

    StringBuffer sbuffer = new StringBuffer();

    if (null != super.getMessage())
    {
      sbuffer.append(super.getMessage());
    }

    if (null != getSystemId())
    {
      sbuffer.append("; SystemID: ");
      sbuffer.append(getSystemId());
    }

    if (0 != getLineNumber())
    {
      sbuffer.append("; Line#: ");
      sbuffer.append(getLineNumber());
    }

    if (0 != getColumnNumber())
    {
      sbuffer.append("; Column#: ");
      sbuffer.append(getColumnNumber());
    }

    Exception exception = getException();

    while (null != exception)
    {
      if (null != exception.getMessage())
      {
        sbuffer.append("\n (");
        sbuffer.append(exception.getClass().getName());
        sbuffer.append("): ");
        sbuffer.append(exception.getMessage());
      }

      if ((!((exception instanceof TransformException) || (exception instanceof ProcessorException)))
              && (exception instanceof SAXException))
      {
        if (exception instanceof SAXParseException)
        {
          SAXParseException spe = (SAXParseException) exception;

          if (null != spe.getSystemId())
          {
            sbuffer.append("; SystemID: ");
            sbuffer.append(spe.getSystemId());
          }

          if (0 != spe.getLineNumber())
          {
            sbuffer.append("; Line#: ");
            sbuffer.append(spe.getLineNumber());
          }

          if (0 != spe.getColumnNumber())
          {
            sbuffer.append("; Column#: ");
            sbuffer.append(spe.getColumnNumber());
          }
        }

        SAXException se = (SAXException) exception;

        exception = se.getException();
      }
      else
      {
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

    if (s == null)
      s = new java.io.PrintWriter(System.err);

    try
    {
      super.printStackTrace(s);
    }
    catch (Exception e){}

    Exception exception = getException();

    for (int i = 0; (i < 10) && (null != exception); i++)
    {
      s.println("---------");

      try
      {
        exception.printStackTrace(s);
      }
      catch (Exception e)
      {
        s.println("Could not print stack trace...");
      }

      if (exception instanceof SAXException)
      {
        SAXException se = (SAXException) exception;
        Exception prev = exception;

        exception = se.getException();

        if (prev == exception)
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
