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

import org.apache.xalan.utils.QName;
import org.apache.xpath.objects.XObject;

/**
 * <meta name="usage" content="internal"/>
 * This class holds an instance of an argument on 
 * the stack.
 */
public class Arg
{
  private QName m_qname;
  
  /**
   * Get the qualified name for this argument.
   */
  public QName getQName()
  {
	  return m_qname;
  }

  /**
   * Set the qualified name for this argument.
   */
  public void setQName(QName name)
  {
	  m_qname = name;
  }
  
  private XObject m_val;
  
  /**
   * Get the value for this argument.
   */
  public XObject getVal()
  {
	  return m_val;
  }

  /**
   * Set the value for this argument.
   */
  public void setVal(XObject val)
  {
	  m_val = val;
  }
  
  private String m_expression;
  
  /**
   * Get the value expression for this argument.
   */
  public String getExpression()
  {
	  return m_expression;
  }

  /**
   * Set the value expression for this argument.
   */
  public void setExpression(String expr)
  {
	  m_expression = expr;
  }

  private boolean m_isParamVar;
  
  /**
   * Construct a dummy parameter argument.
   */
  public Arg()
  {
    m_qname = new QName("");; // so that string compares can be done.
    m_val = null;
    m_expression = null;
    m_isParamVar = false;
  }

  /**
   * Construct a parameter argument.
   */
  public Arg(QName qname, String expression, boolean isParamVar)
  {
    m_qname = qname;
    m_val = null;
    m_expression = expression;
    m_isParamVar = isParamVar;
  }

  /**
   * Construct a parameter argument.
   */
  public Arg(QName qname, XObject val)
  {
    m_qname = qname;
    m_val = val;
    m_isParamVar = false;
    m_expression = null;
  }


  /**
   * Construct a parameter argument.
   */
  public Arg(QName qname, XObject val, boolean isParamVar)
  {
    m_qname = qname;
    m_val = val;
    m_isParamVar = isParamVar;
    m_expression = null;
  }

  /**
   * Override equals and agree that we're equal if 
   * the passed object is a string and it matches 
   * the name of the arg.
   */
  public boolean equals(Object obj)
  {
    try
    {
      if(m_qname != null)
      {
        QName qname = (QName)obj;
        return m_qname.equals(qname.getLocalPart()) 
               && ((null != m_qname.getNamespace()) && (null != qname.getNamespace())) 
               ? m_qname.getNamespace().equals(qname.getNamespace())
                 : ((null == m_qname.getNamespace()) && (null == qname.getNamespace()));
      }
    }
    catch(ClassCastException cce)
    {
    }
    
    return false;
  }

  /**
   * Override equals and agree that we're equal if 
   * the passed object is a QName and it matches 
   * the name of the arg.
   */
  public boolean equals(QName qname)
  {
    if(m_qname != null)
    {
      return m_qname.getLocalPart().equals(qname.getLocalPart()) 
             && (((null != m_qname.getNamespace()) && (null != qname.getNamespace())) 
                 ? m_qname.getNamespace().equals(qname.getNamespace())
                   : ((null == m_qname.getNamespace()) && (null == qname.getNamespace())));
    }
    return false;
  }

}