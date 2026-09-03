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
/*
 * $Id$
 */
package org.apache.xpath.functions;

import org.apache.xalan.res.XSLMessages;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.compiler.Compiler;
import org.apache.xpath.objects.XObject;

/**
 * This is a superclass of all XPath functions. This allows two
 * ways for the class to be called. One method is that the
 * super class processes the arguments and hands the results to
 * the derived class, the other method is that the derived
 * class may process it's own arguments, which is faster since
 * the arguments don't have to be added to an array, but causes
 * a larger code footprint.
 * 
 * @xsl.usage advanced
 */
public abstract class Function extends Expression
{
   static final long serialVersionUID = 6927661240854599768L;
    
   /** 
    * The first argument passed to the function (at index 0).
    */
   protected Expression m_arg0;
   
   /**
    * Class fields to represent, an XPath built-in function's
    * arity which can vary from 0 or greater. These are the
    * function arity values specified by XPath 3.1 F&O spec.
    * 
    * Function's specified arity are set, either to an array 
    * m_arity, or {m_min_arity, m_max_arity}.  
    */
   
   protected Short[] m_arity = null;
   
   protected int m_min_arity = -1;

   protected int m_max_arity = -1;
   
   // This class field represents, function call's 
   // run-time argument count.
   protected int m_arg_count = -1;
   
   
   private String m_localName = null;
   
   private String m_namespace = null;
   
   /**
    * Class field used to implement XPath 3.1 function call, syntax 
    * like, func()?.. , for example, when an XPath 3.1 function call 
    * returns an xdm map.
    */   
   private String m_func_lookup_arg = null;

   /**
   * Set an argument expression for a function.  This method is called by the 
   * XPath compiler.
   *
   * @param arg non-null expression that represents the argument.
   * @param argNum The argument number index.
   *
   * @throws WrongNumberArgsException If the argNum parameter is beyond what 
   * is specified for this function.
   */
  public void setArg(Expression arg, int argNum)
          throws WrongNumberArgsException
  {
      reportWrongNumberArgs();
  }

  /**
   * Check that the number of arguments passed to this function is correct.
   * This method is meant to be overloaded by derived classes, to check for 
   * the number of arguments for a specific function type.  This method is 
   * called by the compiler for static number of arguments checking.
   *
   * @param argNum The number of arguments that is being passed to the function.
   *
   * @throws WrongNumberArgsException
   */
  public void checkNumberArgs(int argNum) throws WrongNumberArgsException
  {
    if (argNum != 0)
      reportWrongNumberArgs();
  }

  /**
   * Constructs and throws a WrongNumberArgException with the appropriate
   * message for this function object.  This method is meant to be overloaded
   * by derived classes so that the message will be as specific as possible.
   *
   * @throws WrongNumberArgsException
   */
  protected void reportWrongNumberArgs() throws WrongNumberArgsException {
      throw new WrongNumberArgsException(XSLMessages.createXPATHMessage("zero", null));
  }

  /**
   * Evaluate the function. The function must return a valid object.
   * 
   * @param xctxt                        An XPath context object
   * @return                             A valid XObject
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
  {

    // Programmer's assert.  (And, no, I don't want the method to be abstract).
    System.out.println("Error! Function.execute should not be called!");

    return null;
  }
  
  /**
   * Call the visitors for the function arguments.
   */
  public void callArgVisitors(XPathVisitor visitor)
  {
  }

  
  /**
   * @see org.apache.xpath.XPathVisitable#callVisitors(ExpressionOwner, XPathVisitor)
   */
  public void callVisitors(ExpressionOwner owner, XPathVisitor visitor)
  {
  	if (visitor.visitFunction(owner, this))
  	{
  		callArgVisitors(visitor);
  	}
  }
  
  /**
   * @see Expression#deepEquals(Expression)
   */
  public boolean deepEquals(Expression expr)
  {
  	if (!isSameClass(expr))
  		return false;
  		
  	return true;
  }

  /**
   * This function is currently only being used by Position()
   * and Last(). See respective functions for more detail.
   */
  public void postCompileStep(Compiler compiler)
  {
    // no default action
  }
  
  public Expression getArg0()
  {
	  return m_arg0;
  }

  public void setArg0(Expression arg0) {
	  this.m_arg0 = arg0; 
  }
  
  public Short[] getArity() {
	 return m_arity; 
  }
  
  public void setArity(Short[] arity) {
	 this.m_arity = arity; 
  }
  
  public int getMinArity() {
	  return m_min_arity;
  }

  public void setMinArity(int minArity) {
	  this.m_min_arity = minArity;
  }

  public int getMaxArity() {
	  return m_max_arity;
  }

  public void setMaxArity(int maxArity) {
	  this.m_max_arity = maxArity;
  }

  public int getRuntimeArgCount() {
	  return m_arg_count; 
  }

  public void setRuntimeArgCount(int argCount) {
	  this.m_arg_count = argCount; 
  } 

  public String getLocalName() {
	  return m_localName;
  }

  public void setLocalName(String localName) {
	  this.m_localName = localName;
  }

  public String getNamespace() {
	  return m_namespace;
  }

  public void setNamespace(String namespace) {
	  this.m_namespace = namespace;
  }
  
  public String getFuncLookupArg() {
	  return m_func_lookup_arg;
  }

  public void setFuncLookupArg(String funcLookupStr) {
	  this.m_func_lookup_arg = funcLookupStr;
  }
  
}
