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
package org.apache.xalan.templates;

import org.apache.xpath.*;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.XPathContext;
import org.apache.xpath.compiler.XPathParser;
import org.apache.xalan.utils.FastStringBuffer;

import org.w3c.dom.*;

/**
 * <meta name="usage" content="internal"/>
 * Simple string part of a complex AVT.
 */
public class AVTPartXPath extends AVTPart
{

  /**
   * Simple string value;
   */
  private XPath m_xpath;

  /**
   * Construct a simple AVT part.
   * @param val A pure string section of an AVT.
   *
   * NEEDSDOC @param xpath
   */
  public AVTPartXPath(XPath xpath)
  {
    m_xpath = xpath;
  }

  /**
   * Construct a simple AVT part.
   * @param val A pure string section of an AVT.
   * NEEDSDOC @param nsNode
   * NEEDSDOC @param xpathProcessor
   * NEEDSDOC @param factory
   * NEEDSDOC @param liaison
   *
   * @throws org.xml.sax.SAXException
   */
  public AVTPartXPath(
          String val, org.apache.xalan.utils.PrefixResolver nsNode, XPathParser xpathProcessor, XPathFactory factory, XPathContext liaison)
            throws org.xml.sax.SAXException
  {
    m_xpath = new XPath(val, null, nsNode, XPath.SELECT);
  }

  /**
   * Get the AVT part as the original string.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public String getSimpleString()
  {
    return "{" + m_xpath.getPatternString() + "}";
  }

  /**
   * Write the value into the buffer.
   *
   * NEEDSDOC @param xctxt
   * @param buf Buffer to write into.
   * @param context The current source tree context.
   * @param nsNode The current namespace context (stylesheet tree context).
   * @param NodeList The current Context Node List.
   *
   * @throws org.xml.sax.SAXException
   */
  public void evaluate(
          XPathContext xctxt, FastStringBuffer buf, Node context, org.apache.xalan.utils.PrefixResolver nsNode)
            throws org.xml.sax.SAXException
  {

    XObject xobj = m_xpath.execute(xctxt, context, nsNode);

    if (null != xobj)
    {
      buf.append(xobj.str());
    }
  }
}
