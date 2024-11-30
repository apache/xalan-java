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
package org.apache.xpath.functions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.res.XSLMessages;
import org.apache.xml.utils.QName;
import org.apache.xpath.Expression;
import org.apache.xpath.XPathContext;
import org.apache.xpath.compiler.FunctionTable;
import org.apache.xpath.composite.XPathArrayConstructor;
import org.apache.xpath.composite.XPathNamedFunctionReference;
import org.apache.xpath.objects.InlineFunctionParameter;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathArray;
import org.apache.xpath.objects.XPathInlineFunction;
import org.apache.xpath.operations.Variable;
import org.apache.xpath.res.XPATHErrorResources;

/**
 * Implementation of an XPath 3.1 function, fn:apply.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncApply extends Function2Args {

   private static final long serialVersionUID = 1073550747347273561L;

   /**
   * Implementation of the function. The function must return a valid object.
   * 
   * @param xctxt The current execution context.
   * 
   * @return A valid XObject.
   *
   * @throws javax.xml.transform.TransformerException
   */
   public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
   {                      
      
        XObject result = null;
        
        Expression arg0 = getArg0();
        Expression arg1 = getArg1();
        
        SourceLocator srcLocator = xctxt.getSAXLocator();
 
        if (arg0 instanceof XPathNamedFunctionReference) {
            XPathNamedFunctionReference namedFuncRef = (XPathNamedFunctionReference)arg0;
            result = getFnApplyResult(namedFuncRef, arg1, xctxt);
        }
        else if (arg0 instanceof XPathInlineFunction) {
        	XPathInlineFunction xpathInlineFunction = (XPathInlineFunction)arg0;
        	result = getFnApplyResult(xpathInlineFunction, arg1, xctxt);
        }
        else if (arg0 instanceof Variable) {           
            XObject arg0VarValue = arg0.execute(xctxt);
            if (arg0VarValue instanceof XPathNamedFunctionReference) {
            	XPathNamedFunctionReference namedFuncRef = (XPathNamedFunctionReference)arg0VarValue;
            	result = getFnApplyResult(namedFuncRef, arg1, xctxt);
            }
            else if (arg0VarValue instanceof XPathInlineFunction) {            	
            	XPathInlineFunction xpathInlineFunction = (XPathInlineFunction)arg0VarValue;
            	result = getFnApplyResult(xpathInlineFunction, arg1, xctxt);
            }
            else {
                throw new javax.xml.transform.TransformerException("FORG0006 : The 1st argument provided to function call fn:apply, "
                                                                                               + "is not a function reference.", srcLocator);    
            }
        }
        else {
            throw new javax.xml.transform.TransformerException("FORG0006 : The 1st argument provided to function call fn:apply, "
                                                                                               + "is not a function reference.", srcLocator);               
        }
        
        return result;
  }

  /**
   * Check that the number of arguments passed to this function is correct.
   *
   * @param argNum The number of arguments that is being passed to the function.
   *
   * @throws WrongNumberArgsException
   */
  public void checkNumberArgs(int argNum) throws WrongNumberArgsException
  {
     if (argNum != 2) {
        reportWrongNumberArgs();
     }
  }

  /**
   * Constructs and throws a WrongNumberArgException with the appropriate
   * message for this function object.
   *
   * @throws WrongNumberArgsException
   */
  protected void reportWrongNumberArgs() throws WrongNumberArgsException {
      throw new WrongNumberArgsException(XSLMessages.createXPATHMessage(
                                              XPATHErrorResources.ER_TWO, null)); //"2"
  }
  
  /**
   * Get the result of fn:apply function call, where function call fn:apply's
   * 1st argument is an XPath named function reference.
   */
  private XObject getFnApplyResult(XPathNamedFunctionReference namedFuncRef, 
		                           Expression arg1XpathExpr, XPathContext xctxt) throws TransformerException {
	  XObject result = null;
	  
	  XObject arg1XObj = arg1XpathExpr.execute(xctxt);
	  
	  SourceLocator srcLocator = xctxt.getSAXLocator();
	  
	  if (!(arg1XObj instanceof XPathArray)) {
		 throw new TransformerException("XPTY0004 : The 2nd argument provided to function call fn:apply, "
		 		                                                            + "is not an array reference.", srcLocator);   
	  }
	  
	  String funcNamespace = namedFuncRef.getFuncNamespace();
	  String funcLocalName = namedFuncRef.getFuncName();
	  int funcArity = namedFuncRef.getFuncArity(); 

	  FunctionTable funcTable = xctxt.getFunctionTable();

	  Object funcIdObj = null;
	  if (FunctionTable.XPATH_BUILT_IN_FUNCS_NS_URI.equals(funcNamespace)) {
		  funcIdObj = funcTable.getFunctionId(funcLocalName);
	  }
	  else if (FunctionTable.XPATH_BUILT_IN_MATH_FUNCS_NS_URI.equals(funcNamespace)) {
		  funcIdObj = funcTable.getFunctionIdForXPathBuiltinMathFuncs(funcLocalName);
	  }
	  else if (FunctionTable.XPATH_BUILT_IN_MAP_FUNCS_NS_URI.equals(funcNamespace)) {
		  funcIdObj = funcTable.getFunctionIdForXPathBuiltinMapFuncs(funcLocalName);
	  }
	  else if (FunctionTable.XPATH_BUILT_IN_ARRAY_FUNCS_NS_URI.equals(funcNamespace)) {
		  funcIdObj = funcTable.getFunctionIdForXPathBuiltinArrayFuncs(funcLocalName);
	  }

	  String expandedFuncName = "{" + funcNamespace + ":" + funcLocalName + "}#" + funcArity;	  	  
	  
	  if (funcIdObj != null) {
		  String funcIdStr = funcIdObj.toString();
		  Function function = funcTable.getFunction(Integer.valueOf(funcIdStr));               
		  try {
			 XPathArray xpathArr = (XPathArray)arg1XObj;
			 for (int idx = 0; idx < xpathArr.size(); idx++) {
				XObject arrayItem = xpathArr.get(idx);
				function.setArg(arrayItem, idx);
			 }			 
			 result = function.execute(xctxt);
		  } 
		  catch (WrongNumberArgsException ex) {			    
			 throw new javax.xml.transform.TransformerException("XPTY0004 : Wrong number of arguments provided, "
					                                                   + "during function call " + expandedFuncName + ".", srcLocator); 
		  }               
	  }
	  else {
		  throw new javax.xml.transform.TransformerException("XPTY0004 : There is no function definition "
		  		                                                    + "found, for the function " + expandedFuncName + ".", srcLocator);
	  }

	  return result;
  }
  
  /**
   * Evaluate an XPath function call, to an inline function expression that gets its arguments
   * from an xdm array. This method effectively produces the result of fn:apply function call.
   */
  private XObject getFnApplyResult(XPathInlineFunction xpathInlineFunction, Expression arrXPathExpr, 
		                           XPathContext xctxt) throws TransformerException {
	  
	  XObject result = null;
	  
	  SourceLocator srcLocator = xctxt.getSAXLocator();
	  
	  // We manually construct an XPath dynamic function call expression, 
	  // to evaluate this function call.
	  
	  XPathDynamicFunctionCall xpathDynamicFunctionCall = new XPathDynamicFunctionCall();
	  String funcRefVarName = "dfc_" + (UUID.randomUUID()).toString();
	  xpathDynamicFunctionCall.setFuncRefVarName(funcRefVarName);

	  Map<QName, XObject> inlineFunctionVarMap = xctxt.getXPathVarMap();
	  inlineFunctionVarMap.put(new QName(funcRefVarName), xpathInlineFunction);

	  if (!(arrXPathExpr instanceof XPathArrayConstructor)) {
		  throw new TransformerException("XPTY0004 : The 2nd argument provided to function call fn:apply, "
				                                                             + "is not an array reference.", srcLocator);   
	  }
	  else {
		  XPathArrayConstructor xpathArrConstructor = (XPathArrayConstructor)arrXPathExpr;
		  List<String> arrConsXPathParts = xpathArrConstructor.getArrayConstructorXPathParts();
		  List<InlineFunctionParameter> inlineFuncParamList = xpathInlineFunction.getFuncParamList();
		  if (arrConsXPathParts.size() != inlineFuncParamList.size()) {
			  throw new TransformerException("XPTY0004 : The number of arguments provided with function call fn:apply() "
					                                     + "within its arguments array is " + arrConsXPathParts.size() + 
					                                       ". Required " + inlineFuncParamList.size() + "."); 
		  }
		  else {
			  List<String> dfcArgList = new ArrayList<String>();
			  for (int idx = 0; idx < arrConsXPathParts.size(); idx++) {
				  String arrItemXPathStr = arrConsXPathParts.get(idx);				  
				  dfcArgList.add(arrItemXPathStr);
			  }
			  xpathDynamicFunctionCall.setArgList(dfcArgList);
			  
			  result = xpathDynamicFunctionCall.execute(xctxt);
		  }
	  }

	  return result;
  }

}
