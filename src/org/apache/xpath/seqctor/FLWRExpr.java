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
package org.apache.xpath.seqctor;

import javax.xml.transform.TransformerException;

import org.apache.xml.utils.QName;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.VariableComposeState;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.operations.Variable;
import org.apache.xpath.parser.Node;

public class FLWRExpr extends Expression implements ExpressionOwner
{
  Binding[] m_bindings;
  Expression m_return;

  /**
   * Constructor for FLWRExpr
   */
  public FLWRExpr()
  {
    super();
  }

  /**
   * @see SimpleNode#shouldReduceIfOneChild()
   */
  public boolean shouldReduceIfOneChild()
  {
    return (jjtGetNumChildren() == 1) ? true : false;
  }

  /**
   * @see org.apache.xpath.XPathVisitable#callVisitors(ExpressionOwner, XPathVisitor)
   */
  public void callVisitors(ExpressionOwner owner, XPathVisitor visitor)
  {
  }

  /**
   * @see org.apache.xpath.Expression#deepEquals(Expression)
   */
  public boolean deepEquals(Expression expr)
  {
    return false;
  }

  /**
   * @see org.apache.xpath.Expression#execute(XPathContext)
   */
  public XObject execute(XPathContext xctxt) throws TransformerException
  {
    return new FLWRIter(m_bindings, m_return, xctxt);
  }

  /**
   * @see org.apache.xpath.Expression#fixupVariables(Vector, int)
   */
  public void fixupVariables(VariableComposeState vcs)
  {
    vcs.pushStackMark();
    Binding[] bindings = m_bindings;
    int globalsSize = vcs.getGlobalsSize();
    for (int i = 0; i < bindings.length; i++)
    {
      Binding binding = bindings[i];
      binding.getExpr().fixupVariables(vcs);
      Variable var = binding.getVar();
      QName varName = var.getQName();
      int index = vcs.addVariableName(varName) - globalsSize;
      var.setIndex(index);
      var.setFixUpWasCalled(true);
      // var.fixupVariables(vcs);
    }    
    m_return.fixupVariables(vcs);
    vcs.popStackMark();
  }

  /**
   * @see org.apache.xpath.ExpressionOwner#getExpression()
   */
  public Expression getExpression()
  {
    return null;
  }

  /**
   * @see org.apache.xpath.ExpressionOwner#setExpression(Expression)
   */
  public void setExpression(Expression exp)
  {
    m_return = exp;
  }

  /**
   * Returns the r.
   * @return Expression
   */
  public Expression getReturn()
  {
    return m_return;
  }

  /**
   * Sets the inExprs.
   * @param inExprs The inExprs to set
   */
  public void setBindings(Binding[] bindings)
  {
    m_bindings = bindings;
  }

  /**
   * Sets the r.
   * @param r The r to set
   */
  public void setReturn(Expression r)
  {
    m_return = r;
  }

  /**
   * @see org.apache.xpath.parser.Node#jjtAddChild(Node, int)
   */
  public void jjtAddChild(Node n, int i)
  {
    if(null == m_bindings && 0 == i)
      m_return = (Expression)fixupPrimarys(n); // Act like a construction node!
    else if(i == 2)
      m_return = (Expression)fixupPrimarys(n);
  }
  
  

  /**
   * @see org.apache.xpath.parser.Node#jjtGetChild(int)
   */
  public Node jjtGetChild(int i)
  {
    if (i == jjtGetNumChildren() - 1)
      return m_return;
    else
      if (null != m_bindings)
      {
        if ((i % 2) == 1)
          return m_bindings[i / 2].getVar();
        else
          return m_bindings[i / 2].getExpr();
      }
      else
        return null;
  }

  /**
   * @see org.apache.xpath.parser.Node#jjtGetNumChildren()
   */
  public int jjtGetNumChildren()
  {
    return (((null != m_bindings) ? m_bindings.length : 0)*2)+
      ((null != m_return) ? 1 : 0);
  }

}

