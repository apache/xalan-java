/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2002-2003 The Apache Software Foundation.  All rights 
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
package org.apache.xpath.parser;

import java.util.Vector;

import javax.xml.transform.TransformerException;

import org.apache.xml.dtm.DTM;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.VariableComposeState;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.objects.XObject;

/**
 * This is an expression node that only exists for construction 
 * purposes. 
 */
public class NonExecutableExpression extends Expression
{
  protected XPath m_parser; // I'm going to leave this for right now only.
  
  public NonExecutableExpression(XPath parser, String value)
  {
  	m_parser = parser;
  	m_value = value;
  }
  
  protected NonExecutableExpression(XPath parser)
  {
  	m_parser = parser;
  }
  
  public XPath getParser()
  {
  	return m_parser;
  }
	
  public Vector m_exprs = new Vector();
	
  public void jjtAddChild(Node n, int i) 
  {
  	n = fixupPrimarys(n);
    if(null == m_exprs)
    	m_exprs  = new Vector();
  	if(i >= m_exprs.size())
  	{
  		m_exprs.setSize(i+1);
  	}
    m_exprs.setElementAt(n, i);
  }
  
  public Node jjtGetChild(int i) 
  {
    if(null == m_exprs)
    	return null;
    else
    	return (Node)m_exprs.elementAt(i);
  }

  public int jjtGetNumChildren() 
  {
    if(null == m_exprs)
    	return 0;
    else
    	return m_exprs.size();
  }
  
  String m_value;
  public void processToken(Token t) { m_value = t.image; }
  
  public String toString() 
  { 
  	return this.getClass().getName()+((null == m_value) ? "" : (" "+m_value)); 
  }



  /**
   * @see Expression#deepEquals(Expression)
   * Dummy stub.
   */
  public boolean deepEquals(Expression expr)
  {
    return false;
  }


  /**
   * @see Expression#fixupVariables(Vector, int)
   * Dummy stub.
   */
  public void fixupVariables(VariableComposeState vcs)
  {
  }


  /**
   * @see Expression#execute(XPathContext)
   * Dummy stub.
   */
  public XObject execute(XPathContext xctxt) throws TransformerException
  {
  	throw new RuntimeException("Can't execute a NonExecutableExpression!");
    // return null;
  }
  
  public XObject execute(XPathContext xctxt, int currentNode)
          throws javax.xml.transform.TransformerException
  {
  	throw new RuntimeException("Can't execute a NonExecutableExpression!");
  }
  
  public XObject execute(
          XPathContext xctxt, int currentNode, DTM dtm, int expType)
            throws javax.xml.transform.TransformerException
  {
  	throw new RuntimeException("Can't execute a NonExecutableExpression!");
  }


  /**
   * @see XPathVisitable#callVisitors(ExpressionOwner, XPathVisitor)
   * Dummy stub.
   */
  public void callVisitors(ExpressionOwner owner, XPathVisitor visitor)
  {
  }


}

