/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.xpath.util;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xpath.Expression;
import org.apache.xpath.axes.SelfIteratorNoPredicate;
import org.apache.xpath.functions.Function;
import org.apache.xpath.functions.Function2Args;
import org.apache.xpath.functions.Function3Args;
import org.apache.xpath.functions.FunctionDef1Arg;
import org.apache.xpath.functions.FunctionMultiArgs;
import org.apache.xpath.functions.FunctionOneArg;
import org.apache.xpath.operations.Operation;
import org.apache.xpath.operations.XPath3UnaryOperation;

/**
 * A class definition, specifying few method definitions for
 * XPath 3.1 expression processing.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XPath3ExpressionUtil {
	
	/**
	 * Method definition, to verify whether an XPath 3.1 inline 
	 * function expression body accesses an XPath context item.
	 * 
	 * @param xpathInlineFuncBodyExr              An XPath 3.1 inline function expression 
	 *                                            function body's compiled, expression object.
	 * @param funcBodyStr                         XPath function body's user supplied 
	 *                                            string value.
	 * @param srcLocator                          An XSL transform source locator object
	 * @throws TransformerException
	 */
	 public static void verifyXPathInlineFuncContextItemAccess(Expression xpathInlineFuncBodyExr, String funcBodyStr, 
			  																						SourceLocator srcLocator) throws TransformerException {

		  if (xpathInlineFuncBodyExr instanceof SelfIteratorNoPredicate) {
			  throw new TransformerException("XPDY0002 : An XPath 3.1 inline function expression body, cannot "
																									  + "refer to an XPath context item. An XPath "
																									  + "inline function expression, erroneous function body is '" 
																									  + funcBodyStr + "'.", srcLocator);			  			  
		  }
		  else if (xpathInlineFuncBodyExr instanceof Operation) {
			  Operation opn1 = (Operation)xpathInlineFuncBodyExr;
			  Expression exprL = opn1.getLeftOperand();
			  Expression exprR = opn1.getRightOperand();

			  if ((exprL instanceof SelfIteratorNoPredicate) || (exprR instanceof SelfIteratorNoPredicate)) {
				  throw new TransformerException("XPDY0002 : An XPath 3.1 inline function expression body, cannot "
																									  + "refer to an XPath context item. An XPath "
																									  + "inline function expression, erroneous function body is '" 
																									  + funcBodyStr + "'.", srcLocator); 
			  }
		  }
		  else if (xpathInlineFuncBodyExr instanceof XPath3UnaryOperation) {
			  XPath3UnaryOperation opn1 = (XPath3UnaryOperation)xpathInlineFuncBodyExr;
			  Expression exprR = opn1.getOperand();

			  if (exprR instanceof SelfIteratorNoPredicate) {
				  throw new TransformerException("XPDY0002 : An XPath 3.1 inline function expression body, cannot "
																									  + "refer to an XPath context item. An XPath "
																									  + "inline function expression, erroneous function body is '" 
																									  + funcBodyStr + "'.", srcLocator);	
			  }
		  }
		  else if (xpathInlineFuncBodyExr instanceof Function) {
			  Function func1 = (Function)xpathInlineFuncBodyExr;		  

			  if (func1 instanceof FunctionOneArg) {
				  FunctionOneArg fObj = (FunctionOneArg)func1;				  
				  Expression expr1 = fObj.getArg0();
				  
				  if (expr1 != null) {
					  verifyXPathInlineFuncContextItemAccess(expr1, funcBodyStr, srcLocator);  
				  }
				  else {
					  throw new TransformerException("XPDY0002 : An XPath 3.1 inline function expression body, cannot "
																									  + "refer to an XPath context item. An XPath "
																									  + "inline function expression, erroneous function body is '" 
																									  + funcBodyStr + "'.", srcLocator);
				  }
			  }
			  else if (func1 instanceof FunctionDef1Arg) {
				  FunctionOneArg fObj = (FunctionOneArg)func1;				  
				  Expression expr1 = fObj.getArg0();
				  
				  if (expr1 != null) {
					  verifyXPathInlineFuncContextItemAccess(expr1, funcBodyStr, srcLocator);  
				  }
				  else {
					  throw new TransformerException("XPDY0002 : An XPath 3.1 inline function expression body, cannot "
																									  + "refer to an XPath context item. An XPath "
																									  + "inline function expression, erroneous function body is '" 
																									  + funcBodyStr + "'.", srcLocator);
				  }
			  }
			  else if (func1 instanceof Function2Args) {
				  Function2Args fObj = (Function2Args)func1;				  
				  Expression expr1 = fObj.getArg0();
				  verifyXPathInlineFuncContextItemAccess(expr1, funcBodyStr, srcLocator);

				  expr1 = fObj.getArg1();				  				  
				  verifyXPathInlineFuncContextItemAccess(expr1, funcBodyStr, srcLocator);				  
			  }
			  else if (func1 instanceof Function3Args) {
				  Function3Args fObj = (Function3Args)func1;

				  Expression expr1 = fObj.getArg0();				  				  
				  verifyXPathInlineFuncContextItemAccess(expr1, funcBodyStr, srcLocator);

				  expr1 = fObj.getArg1();				  				  
				  verifyXPathInlineFuncContextItemAccess(expr1, funcBodyStr, srcLocator);

				  expr1 = fObj.getArg2();				  				  
				  verifyXPathInlineFuncContextItemAccess(expr1, funcBodyStr, srcLocator);
			  }
			  else if (func1 instanceof FunctionMultiArgs) {
				  FunctionMultiArgs fObj = (FunctionMultiArgs)func1;

				  Expression expr1 = fObj.getArg0();				  				  
				  verifyXPathInlineFuncContextItemAccess(expr1, funcBodyStr, srcLocator);

				  expr1 = fObj.getArg1();				  				  
				  verifyXPathInlineFuncContextItemAccess(expr1, funcBodyStr, srcLocator);

				  expr1 = fObj.getArg2();				  				  
				  verifyXPathInlineFuncContextItemAccess(expr1, funcBodyStr, srcLocator);

				  Expression[] exprArr1 = fObj.getArgs();
				  if (exprArr1 != null) {
					  int size1 = exprArr1.length;
					  for (int idx = 0; idx < size1; idx++) {
						  expr1 = exprArr1[idx];						  						  
						  verifyXPathInlineFuncContextItemAccess(expr1, funcBodyStr, srcLocator);
					  }
				  }
			  }
		  }
	  }

}
