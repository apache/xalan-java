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
package org.apache.xpath;

import org.w3c.dom.Node;

import org.apache.xpath.objects.XObject;
import org.apache.xpath.res.XPATHErrorResources;
import org.apache.xalan.res.XSLMessages;

import org.xml.sax.XMLReader;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.apache.xalan.utils.SAXSourceLocator;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.ErrorListener;

/**
 * <meta name="usage" content="internal"/>
 * NEEDSDOC Class Expression <needs-comment/>
 */
public abstract class Expression
	implements java.io.Serializable
{

  /** NEEDSDOC Field m_xpath          */
  protected SourceLocator m_slocator;
  
  public void setSourceLocator(SourceLocator locator)
  {
    m_slocator = locator;
  }

  /**
   * NEEDSDOC Method execute 
   *
   *
   * NEEDSDOC @param xctxt
   *
   * NEEDSDOC (execute) @return
   *
   * @throws javax.xml.transform.TransformerException
   */
  public abstract XObject execute(XPathContext xctxt)
    throws javax.xml.transform.TransformerException;

  /**
   * Warn the user of an problem.
   *
   * NEEDSDOC @param xctxt
   * NEEDSDOC @param msg
   * NEEDSDOC @param args
   *
   * @throws javax.xml.transform.TransformerException
   */
  public void warn(XPathContext xctxt, int msg, Object[] args)
    throws javax.xml.transform.TransformerException
  {

    java.lang.String fmsg = XSLMessages.createXPATHWarning(msg, args);
    if(null != xctxt)
    {
      ErrorListener eh = xctxt.getErrorListener();

      // TO DO: Need to get stylesheet Locator from here.
      eh.warning(new TransformerException(fmsg, xctxt.getSAXLocator()));
    }
  }

  /**
   * Tell the user of an assertion error, and probably throw an
   * exception.
   *
   * NEEDSDOC @param b
   * NEEDSDOC @param msg
   *
   * @throws javax.xml.transform.TransformerException
   */
  public void assert(boolean b, java.lang.String msg)
          throws javax.xml.transform.TransformerException
  {

    if (!b)
    {
      java.lang.String fMsg = XSLMessages.createXPATHMessage(
        XPATHErrorResources.ER_INCORRECT_PROGRAMMER_ASSERTION,
        new Object[]{ msg });

      throw new RuntimeException(fMsg);
    }
  }

  /**
   * Tell the user of an error, and probably throw an
   * exception.
   *
   * NEEDSDOC @param xctxt
   * NEEDSDOC @param msg
   * NEEDSDOC @param args
   *
   * @throws javax.xml.transform.TransformerException
   */
  public void error(XPathContext xctxt, int msg, Object[] args)
          throws javax.xml.transform.TransformerException
  {

    java.lang.String fmsg = XSLMessages.createXPATHMessage(msg, args);
    if(null != xctxt)
    {
      ErrorListener eh = xctxt.getErrorListener();

      TransformerException te = new TransformerException(fmsg, m_slocator);
      eh.fatalError(te);
    }
    
  }
}
