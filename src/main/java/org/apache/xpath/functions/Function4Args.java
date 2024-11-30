/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the  "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * $Id$
 */
package org.apache.xpath.functions;

import org.apache.xalan.res.XSLMessages;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPathVisitor;

/**
 * Base class for functions that accept four arguments.
 * @xsl.usage advanced
 */
public class Function4Args extends Function3Args
{
  private static final long serialVersionUID = 553916218361619933L;
    
  /** The third argument passed to the function (at index 3).
   *
   */
  Expression m_arg3;

  /**
   * Return the third argument passed to the function (at index 3).
   *
   * @return An expression that represents the fourth argument passed to the 
   *         function.
   */
  public Expression getArg3()
  {
    return m_arg3;
  }
  
  /**
   * This function is used to fixup variables from QNames to stack frame 
   * indexes at stylesheet build time.
   * @param vars List of QNames that correspond to variables. This list 
   * should be searched backwards for the first qualified name that 
   * corresponds to the variable reference qname. The position of the 
   * QName in the vector from the start of the vector will be its position 
   * in the stack frame (but variables above the globalsTop value will need 
   * to be offset to the current stack frame).
   */
  public void fixupVariables(java.util.Vector vars, int globalsSize)
  {
    super.fixupVariables(vars, globalsSize);
    if (null != m_arg3)
       m_arg3.fixupVariables(vars, globalsSize);
  }

  /**
   * Set an argument expression for a function. This method is called by the 
   * XPath compiler.
   *
   * @param arg non-null expression that represents the argument.
   * @param argNum The argument number index.
   *
   * @throws WrongNumberArgsException If the argNum parameter is greater than 3.
   */
  public void setArg(Expression arg, int argNum)
          throws WrongNumberArgsException
  {
    if (argNum < 3)
      super.setArg(arg, argNum);
    else if (3 == argNum)
    {
       m_arg3 = arg;
       arg.exprSetParent(this);
    }
    else
	   reportWrongNumberArgs();
  }

  /**
   * Check that the number of arguments passed to this function is correct. 
   *
   *
   * @param argNum The number of arguments that is being passed to the function.
   *
   * @throws WrongNumberArgsException
   */
  public void checkNumberArgs(int argNum) throws WrongNumberArgsException
  {
    if (argNum != 4)
      reportWrongNumberArgs();
  }

  /**
   * Constructs and throws a WrongNumberArgException with the appropriate
   * message for this function object.
   *
   * @throws WrongNumberArgsException
   */
  protected void reportWrongNumberArgs() throws WrongNumberArgsException {
      throw new WrongNumberArgsException(XSLMessages.createXPATHMessage("four", null));
  }
  
  /**
   * Tell if this expression or it's subexpressions can traverse outside 
   * the current subtree.
   * 
   * @return true if traversal outside the context node's subtree can occur.
   */
   public boolean canTraverseOutsideSubtree()
   {
    return super.canTraverseOutsideSubtree() 
    ? true : m_arg3.canTraverseOutsideSubtree();
   }
   
  class Arg3Owner implements ExpressionOwner
  {
    /**
     * @see ExpressionOwner#getExpression()
     */
    public Expression getExpression()
    {
      return m_arg3;
    }


    /**
     * @see ExpressionOwner#setExpression(Expression)
     */
    public void setExpression(Expression exp)
    {
    	exp.exprSetParent(Function4Args.this);
    	m_arg3 = exp;
    }
  }

   
  /**
   * @see org.apache.xpath.XPathVisitable#callVisitors(ExpressionOwner, XPathVisitor)
   */
  public void callArgVisitors(XPathVisitor visitor)
  {
  	super.callArgVisitors(visitor);
  	if(null != m_arg3)
  		m_arg3.callVisitors(new Arg3Owner(), visitor);
  }

  /**
   * @see Expression#deepEquals(Expression)
   */
  public boolean deepEquals(Expression expr)
  {
  	if(!super.deepEquals(expr))
  		return false;
  		
  	if(null != m_arg3)
  	{
  		if(null == ((Function4Args)expr).m_arg3)
  			return false;

  		if(!m_arg3.deepEquals(((Function4Args)expr).m_arg3))
  			return false;
  	}
  	else if (null != ((Function4Args)expr).m_arg3)
  		return false;
  		
  	return true;
  }

}
