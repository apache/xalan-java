/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the "License");
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
package org.apache.xpath.functions;

import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionNode;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;

/**
 * Base class for functions that accept two arguments.
 * @xsl.usage advanced
 */
public class Function2Args extends FunctionOneArg
{
  static final long serialVersionUID = 5574294996842710641L;

  /** The second argument passed to the function (at index 1).
   *  
   */
  protected Expression m_arg1;

  /**
   * Return the second argument passed to the function (at index 1).
   *
   * @return An expression that represents the second argument passed to the 
   *         function.
   */
  public Expression getArg1()
  {
    return m_arg1;
  }
  
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
  public void fixupVariables(java.util.Vector vars, int globalsSize)
  {
    super.fixupVariables(vars, globalsSize);
    if(null != m_arg1)
      m_arg1.fixupVariables(vars, globalsSize);
  }


  /**
   * Set an argument expression for a function.  This method is called by the 
   * XPath compiler.
   *
   * @param arg non-null expression that represents the argument.
   * @param argNum The argument number index.
   *
   * @throws WrongNumberArgsException If the argNum parameter is greater than 1.
   */
  public void setArg(Expression arg, int argNum)
          throws WrongNumberArgsException
  {
    if (argNum == 0)
      super.setArg(arg, argNum);
    else if (1 == argNum)
    {
      m_arg1 = arg;
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
    if (argNum != 2)
      reportWrongNumberArgs();
  }

  /**
   * Constructs and throws a WrongNumberArgException with the appropriate
   * message for this function object.
   *
   * @throws WrongNumberArgsException
   */
  protected void reportWrongNumberArgs() throws WrongNumberArgsException {
      throw new WrongNumberArgsException(XSLMessages.createXPATHMessage("two", null));
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
    ? true : m_arg1.canTraverseOutsideSubtree();
   }
   
  class Arg1Owner implements ExpressionOwner
  {
    /**
     * @see ExpressionOwner#getExpression()
     */
    public Expression getExpression()
    {
      return m_arg1;
    }


    /**
     * @see ExpressionOwner#setExpression(Expression)
     */
    public void setExpression(Expression exp)
    {
    	exp.exprSetParent(Function2Args.this);
    	m_arg1 = exp;
    }
  }

   
  /**
   * @see org.apache.xpath.XPathVisitable#callVisitors(ExpressionOwner, XPathVisitor)
   */
  public void callArgVisitors(XPathVisitor visitor)
  {
  	super.callArgVisitors(visitor);
  	if(null != m_arg1)
  		m_arg1.callVisitors(new Arg1Owner(), visitor);
  }

  /**
   * @see Expression#deepEquals(Expression)
   */
  public boolean deepEquals(Expression expr)
  {
  	if(!super.deepEquals(expr))
  		return false;
  		
  	if(null != m_arg1)
  	{
  		if(null == ((Function2Args)expr).m_arg1)
  			return false;
  			
  		if(!m_arg1.deepEquals(((Function2Args)expr).m_arg1))
  			return false;
  	}
  	else if(null != ((Function2Args)expr).m_arg1)
  		return false;
  		
  	return true;
  }
 
 /**
  * Function definition to get an XSL TransformerImpl instance, given 
  * a supplied XPath compiled expression. 
  * 
  * @param expr					  An XPath compiled expression
  * @return                       TransformerImpl object instance
  */
 protected TransformerImpl getTransformerImplFromXPathExpression(Expression expr) {

	 TransformerImpl transformerImpl = null;

	 ExpressionNode expressionNode = expr.getExpressionOwner();
	 ExpressionNode stylesheetRootNode = null;
	 while (expressionNode != null) {
		 stylesheetRootNode = expressionNode;
		 expressionNode = expressionNode.exprGetParent();                     
	 }

	 StylesheetRoot stylesheetRoot = (StylesheetRoot)stylesheetRootNode;
	 if (stylesheetRoot != null) {
		 transformerImpl = stylesheetRoot.getTransformerImpl();
	 }

	 return transformerImpl; 
 }
 
 /**
  * Method definition to convert a singleton XDM item to a sequence 
  * containing that one item.
  * 
  * @param xObj					    A singleton item to be cast to a sequence
  * @return							An XObject instance representing the returned sequence						
  */
 protected XObject castSingletonItemToResultSequence(XObject xObj) {	  
	  XObject result = null;
	  
	  ResultSequence rSeq = new ResultSequence();
	  rSeq.add(xObj);
	  
	  result = rSeq;
	  
	  return result;
 }

}
