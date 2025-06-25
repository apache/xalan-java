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
package org.apache.xpath.functions.hof;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.templates.ElemFunction;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.XMLNSDecl;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.utils.QName;
import org.apache.xpath.Expression;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.compiler.FunctionTable;
import org.apache.xpath.composite.XPathArrayConstructor;
import org.apache.xpath.composite.XPathNamedFunctionReference;
import org.apache.xpath.functions.Function;
import org.apache.xpath.functions.Function2Args;
import org.apache.xpath.functions.WrongNumberArgsException;
import org.apache.xpath.functions.XPathDynamicFunctionCall;
import org.apache.xpath.objects.InlineFunctionParameter;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathArray;
import org.apache.xpath.objects.XPathInlineFunction;
import org.apache.xpath.operations.Variable;
import org.apache.xpath.patterns.NodeTest;
import org.apache.xpath.res.XPATHErrorResources;

/**
 * Implementation of an XPath 3.1 function fn:apply.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncApply extends Function2Args {

   private static final long serialVersionUID = 1073550747347273561L;

   /**
    * Evaluate the function call.
    */
   public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
   {                      
	   
        XObject result = null;
        
        SourceLocator srcLocator = xctxt.getSAXLocator();
        
        TransformerImpl transformerImpl = null;
        
        ElemFunction elemFunction = null;
        
        if (m_arg0 instanceof XPathNamedFunctionReference) {
            XPathNamedFunctionReference namedFuncRef = (XPathNamedFunctionReference)m_arg0;
            
            result = getFnApplyResult(namedFuncRef, m_arg1, xctxt);
        }
        else if (m_arg0 instanceof XPathInlineFunction) {
        	XPathInlineFunction xpathInlineFunction = (XPathInlineFunction)m_arg0;
        	
        	result = getFnApplyResult(xpathInlineFunction, m_arg1, xctxt);
        }
        else if (m_arg0 instanceof NodeTest) {
            transformerImpl = getTransformerImplFromXPathExpression(m_arg0);            
            elemFunction = XslTransformEvaluationHelper.getElemFunctionFromNodeTestExpression((NodeTest)m_arg0, transformerImpl, srcLocator);
            result = getFnApplyResult(elemFunction, m_arg1, xctxt, transformerImpl);
        }
        else if (m_arg0 instanceof Variable) {           
            XObject arg0VarValue = m_arg0.execute(xctxt);
            if (arg0VarValue instanceof XPathNamedFunctionReference) {
            	XPathNamedFunctionReference namedFuncRef = (XPathNamedFunctionReference)arg0VarValue;
            	
            	result = getFnApplyResult(namedFuncRef, m_arg1, xctxt);
            }
            else if (arg0VarValue instanceof XPathInlineFunction) {            	
            	XPathInlineFunction xpathInlineFunction = (XPathInlineFunction)arg0VarValue;
            	
            	result = getFnApplyResult(xpathInlineFunction, m_arg1, xctxt);
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
	  
	  // Construct an XPath dynamic function call expression, 
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
  
  /**
   * Get result of fn:apply function call, when fn:apply's function 
   * argument is xsl:function reference.
   */
  private XObject getFnApplyResult(ElemFunction elemFunction, Expression arrXPathExpr, 
		                           XPathContext xctxt, TransformerImpl transformerImpl) throws TransformerException {
	  
	  XObject result = null;
	  
	  SourceLocator srcLocator = xctxt.getSAXLocator();
	  
	  if (!(arrXPathExpr instanceof XPathArrayConstructor)) {
		  throw new TransformerException("XPTY0004 : The 2nd argument provided to function call fn:apply, "
				                                                             							+ "is not an array reference.", srcLocator);   
	  }
	  else {
		  XPathArrayConstructor xpathArrConstructor = (XPathArrayConstructor)arrXPathExpr;
		  List<String> arrConsXPathParts = xpathArrConstructor.getArrayConstructorXPathParts();
		  int xslFunctionParamCount = elemFunction.getArity();
		  if (arrConsXPathParts.size() != xslFunctionParamCount) {
			  throw new TransformerException("XPTY0004 : The number of arguments provided with function call fn:apply() "
																                                     				+ "within its arguments array is " + arrConsXPathParts.size() + 
																                                     				". Required " + xslFunctionParamCount + "."); 
		  }
		  else {
			  final int contextNode = xctxt.getCurrentNode();

			  List<XMLNSDecl> prefixTable = null;
			  ElemTemplateElement elemTemplateElement = (ElemTemplateElement)xctxt.getNamespaceContext();

			  if (elemTemplateElement != null) {
				  prefixTable = (List<XMLNSDecl>)elemTemplateElement.getPrefixTable();
			  }

			  ResultSequence argSequence = new ResultSequence();
			  for (int idx = 0; idx < xslFunctionParamCount; idx++) {
				  String xpathStr = arrConsXPathParts.get(idx);
				  if (prefixTable != null) {
					  xpathStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(xpathStr, prefixTable);
				  }

				  XPath argXPath = new XPath(xpathStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);

				  XObject argVal = argXPath.execute(xctxt, contextNode, xctxt.getNamespaceContext());    					
				  argSequence.add(argVal);
			  }

			  result = elemFunction.evaluateXslFunction(transformerImpl, argSequence);
		  }
	  }

	  return result;
  }

}
