/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights 
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
import org.apache.xpath.ExpressionNode;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.VariableComposeState;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.objects.XObject;

/**
 * The baseclass for a binary operation.
 */
public abstract class Operation extends Expression implements ExpressionOwner
{

  /** The left operand expression.
   *  @serial */
  protected Expression m_left;

  /** The right operand expression.
   *  @serial */
  protected Expression m_right;
  
  static GenericOpFunc NOTSUPPORTED = new GenericOpFunc()
  {
    /**
     * @see org.apache.xpath.operations.opfuncs.GenericOpFunc#operate(XPathContext, XObject, XObject)
     */
    public XObject operate(XPathContext xctxt, XObject lhs, XObject rhs)
    {
      return null;
    }

  };
  
  /**
   * This function is used to fixup variables from QNames to stack frame 
   * indexes at stylesheet build time.
   * @param vars List of QNames that correspond to variables.  This list 
   * should be searched backwards for the first qualified name that 
   * corresponds to the variable reference qname.  The position of the 
   * QName in the vector from the start of the vector will be its position 
   * in the stack frame (but variables above the globalsTop value will need 
   * to be offset to the current stack frame).
   */
  public void fixupVariables(VariableComposeState vcs)
  {
    m_left.fixupVariables(vcs);
    m_right.fixupVariables(vcs);
  }


  /**
   * Tell if this expression or it's subexpressions can traverse outside
   * the current subtree.
   *
   * @return true if traversal outside the context node's subtree can occur.
   */
  public boolean canTraverseOutsideSubtree()
  {

    if (null != m_left && m_left.canTraverseOutsideSubtree())
      return true;

    if (null != m_right && m_right.canTraverseOutsideSubtree())
      return true;

    return false;
  }

  /**
   * Set the left and right operand expressions for this operation.
   *
   *
   * @param l The left expression operand.
   * @param r The right expression operand.
   */
  public void setLeftRight(Expression l, Expression r)
  {
    jjtAddChild(l, 0); 
    jjtAddChild(r, 1); 
  }
  
  
  /**
   * Add the left or right node of the operation.
   */
  public void jjtAddChild(org.apache.xpath.parser.Node n, int i) 
  {
  	n = fixupPrimarys(n);  // yuck.
  	if(0 == i)
  	{
    	m_left = (Expression)n;
    	m_left.jjtSetParent(this);
  	}
  	else if(1 == i)
  	{
    	m_right = (Expression)n;
    	m_right.jjtSetParent(this);
  	}
  	else
  	{
  		// assertion... should not be able to occur.
  		throw new RuntimeException("Can't add more than two children to an operation!");
  	}
  }
  
  /** This method returns a child node.  The children are numbered
     from zero, left to right. */
  public ExpressionNode exprGetChild(int i)
  {
  	assertion(i <= 1, "Operation can only have one or two children!");
  	return (0 == i) ? m_left : m_right;
  }

  /** Return the number of children the node has. */
  public int exprGetNumChildren()
  {
  	int count = 1;
   	if(null != m_right)
  		count++;
  	return count;
  }

  /** @return the left operand of binary operation, as an Expression.
   */
  public Expression getLeftOperand(){
    return m_left;
  }

  /** @return the right operand of binary operation, as an Expression.
   */
  public Expression getRightOperand(){
    return m_right;
  }
  
  class LeftExprOwner implements ExpressionOwner
  {
    /**
     * @see ExpressionOwner#getExpression()
     */
    public Expression getExpression()
    {
      return m_left;
    }

    /**
     * @see ExpressionOwner#setExpression(Expression)
     */
    public void setExpression(Expression exp)
    {
    	exp.exprSetParent(Operation.this);
    	m_left = exp;
    }
  }

  /**
   * @see XPathVisitable#callVisitors(ExpressionOwner, XPathVisitor)
   */
  public void callVisitors(ExpressionOwner owner, XPathVisitor visitor)
  {
  	if(visitor.visitBinaryOperation(owner, this))
  	{
  		m_left.callVisitors(new LeftExprOwner(), visitor);
  		m_right.callVisitors(this, visitor);
  	}
  }

  /**
   * @see ExpressionOwner#getExpression()
   */
  public Expression getExpression()
  {
    return m_right;
  }

  /**
   * @see ExpressionOwner#setExpression(Expression)
   */
  public void setExpression(Expression exp)
  {
  	exp.exprSetParent(this);
  	m_right = exp;
  }

  /**
   * @see Expression#deepEquals(Expression)
   */
  public boolean deepEquals(Expression expr)
  {
  	if(!isSameClass(expr))
  		return false;
  		
  	if(!m_left.deepEquals(((Operation)expr).m_left))
  		return false;
  		
  	if(!m_right.deepEquals(((Operation)expr).m_right))
  		return false;
  		
  	return true;
  }
  /**
   * Tell if this node should have it's PathExpr ancestory reduced.
   */
  public boolean isPathExprReduced()
  {
  	return true;
  }

}
