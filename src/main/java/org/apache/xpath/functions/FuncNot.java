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

import javax.xml.transform.SourceLocator;

import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;

import xml.xpath31.processor.types.XSBoolean;

/**
 * Implementation of XPath 3.1 fn:not function.
 */
public class FuncNot extends FunctionOneArg
{
   static final long serialVersionUID = 7299699961076329790L;
   
   /**
    * Class constructor.
    */
   public FuncNot() {
   	 m_defined_arity = new Short[] { 1 };	
   }

  /**
   * Implementation of the function. The function must return
   * a valid object.
   * 
   * @param xctxt The current execution context.
   * @return A valid XObject.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
  {	  
	  XObject result = null;

	  SourceLocator srcLocator = xctxt.getSAXLocator(); 

	  try {
		  FuncBoolean fnBoolean = new FuncBoolean(m_arg0);
		  result = fnBoolean.execute(xctxt);
	  }
	  catch (javax.xml.transform.TransformerException ex) {
		  throw new javax.xml.transform.TransformerException("FORG0006 : Invalid argument provided "
				                                                            + "to function fn:not.", srcLocator); 
	  }
 
	  /**
	   * Result of function fn:not is, boolean negation of fn:boolean 
	   * function's result on the same function argument. 
	   */
	  
	  if (((XSBoolean)result).value()) {
		  result = new XSBoolean(false);  
	  }
	  else {
		  result = new XSBoolean(true);
	  }

	  return result;
	 
  }
  
}
