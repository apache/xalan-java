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
package org.apache.xpath.operations;

import org.apache.xpath.Expression;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;

import org.w3c.dom.Node;

/**
 * <meta name="usage" content="internal"/>
 * NEEDSDOC Class Operation <needs-comment/>
 */
public class Operation extends Expression
{

  /** NEEDSDOC Field m_left          */
  protected Expression m_left;

  /** NEEDSDOC Field m_right          */
  protected Expression m_right;

  /**
   * NEEDSDOC Method setLeftRight 
   *
   *
   * NEEDSDOC @param l
   * NEEDSDOC @param r
   */
  public void setLeftRight(Expression l, Expression r)
  {
    m_left = l;
    m_right = r;
  }

  /**
   * NEEDSDOC Method execute 
   *
   *
   * NEEDSDOC @param xctxt
   *
   * NEEDSDOC (execute) @return
   *
   * @throws org.xml.sax.SAXException
   */
  public XObject execute(XPathContext xctxt) throws org.xml.sax.SAXException
  {

    XObject left = m_left.execute(xctxt);
    XObject right = m_right.execute(xctxt);

    return operate(left, right);
  }

  /**
   * NEEDSDOC Method operate 
   *
   *
   * NEEDSDOC @param left
   * NEEDSDOC @param right
   *
   * NEEDSDOC (operate) @return
   *
   * @throws org.xml.sax.SAXException
   */
  public XObject operate(XObject left, XObject right)
          throws org.xml.sax.SAXException
  {
    return null;  // no-op
  }
}
