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
import org.apache.xalan.templates.XMLNSDecl;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.utils.QName;
import org.apache.xpath.Expression;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathStaticContext;
import org.apache.xpath.compiler.FunctionTable;
import org.apache.xpath.compiler.Keywords;
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
    * Class constructor.
    */
   public FuncApply() {
	   m_arity = new Short[] { 2 };
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

		   elemFunction = XslTransformEvaluationHelper.getElemFunctionFromNodeTestExpression((NodeTest)m_arg0, srcLocator);

		   result = getFnApplyResult(elemFunction, m_arg1, xctxt, transformerImpl);
	   }
	   else if (m_arg0 instanceof Variable) {           
		   XObject arg0VarValue = getFunctionArgEffectiveValue(m_arg0, xctxt);

		   if (arg0VarValue instanceof XPathNamedFunctionReference) {
			   XPathNamedFunctionReference namedFuncRef = (XPathNamedFunctionReference)arg0VarValue;

			   result = getFnApplyResult(namedFuncRef, m_arg1, xctxt);
		   }
		   else if (arg0VarValue instanceof XPathInlineFunction) {            	
			   XPathInlineFunction xpathInlineFunction = (XPathInlineFunction)arg0VarValue;

			   result = getFnApplyResult(xpathInlineFunction, m_arg1, xctxt);
		   }
		   else {
			   throw new javax.xml.transform.TransformerException("FORG0006 : An XPath 3.1 function 'apply' first argument is not a function reference.", srcLocator);    
		   }
	   }
	   else {
		   throw new javax.xml.transform.TransformerException("FORG0006 : An XPath 3.1 function 'apply' first argument is not a function reference.", srcLocator);               
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
   * Method definition, to get the result of XPath 3.1 function call fn:apply.
   * 
   * @param namedFuncRef                         The supplied XPath named function reference 
   *                                             object instance.
   * @param arg1XpathExpr                        An XPath function fn:apply second argument, 
   *                                             XPath expression object.
   * @param xctxt                                An XPath context object
   * @return                                     The result of an XPath function call
   *                                             fn:apply.
   * @throws TransformerException
   */
  private XObject getFnApplyResult(XPathNamedFunctionReference namedFuncRef, 
		                           											Expression arg1XpathExpr, XPathContext xctxt) throws TransformerException {
	  XObject result = null;
	  
	  XObject arg1XObj = getFunctionArgEffectiveValue(arg1XpathExpr, xctxt);
	  
	  SourceLocator srcLocator = xctxt.getSAXLocator();
	  
	  if (!(arg1XObj instanceof XPathArray)) {
		 throw new javax.xml.transform.TransformerException("FORG0006 : An XPath 3.1 function 'apply' second argument is not an xdm array.", srcLocator);   
	  }
	  
	  String funcNamespace = namedFuncRef.getFuncNamespace();
	  String funcLocalName = namedFuncRef.getFuncName();
	  int funcArity = 0;           
	  
	  if ((XPathStaticContext.XPATH_BUILT_IN_FUNCS_NS_URI).equals(funcNamespace) && 
			  															        (Keywords.FUNC_CONCAT_STRING).equals(funcLocalName)) {
		  funcArity = namedFuncRef.getConcatArity();
	  }
	  else {
		  funcArity = namedFuncRef.getArity(); 
	  } 

	  FunctionTable funcTable = xctxt.getFunctionTable();

	  Object funcIdObj = null;
	  
	  if ((funcNamespace == null) || (XPathStaticContext.XPATH_BUILT_IN_FUNCS_NS_URI.equals(funcNamespace))) {
		  funcIdObj = funcTable.getFunctionIdForXSLBuiltinFunc(funcLocalName);
	  }
	  else if (XPathStaticContext.XPATH_BUILT_IN_MATH_FUNCS_NS_URI.equals(funcNamespace)) {
		  funcIdObj = funcTable.getFunctionIdForXPathBuiltinMathFunc(funcLocalName);
	  }
	  else if (XPathStaticContext.XPATH_BUILT_IN_MAP_FUNCS_NS_URI.equals(funcNamespace)) {
		  funcIdObj = funcTable.getFunctionIdForXPathBuiltinMapFunc(funcLocalName);
	  }
	  else if (XPathStaticContext.XPATH_BUILT_IN_ARRAY_FUNCS_NS_URI.equals(funcNamespace)) {
		  funcIdObj = funcTable.getFunctionIdForXPathBuiltinArrayFunc(funcLocalName);
	  }
	  
	  String funcExpandedName = null;
      
	  if (funcNamespace != null) {
	      funcExpandedName = "{" + funcNamespace + ":" + funcLocalName + "}#" + funcArity;
      }
      else {
    	  funcExpandedName = "{" + funcLocalName + "}#" + funcArity;
      }
	  
	  if (funcIdObj != null) {
		  String funcIdStr = funcIdObj.toString();
		  Function function = funcTable.getFunction(Integer.valueOf(funcIdStr));               
		  
		  try {
			 XPathArray xpathArr = (XPathArray)arg1XObj;
			 
			 int size1 = xpathArr.size();
			 
			 for (int idx = 0; idx < size1; idx++) {
				XObject arrayItem = xpathArr.get(idx);
				function.setArg(arrayItem, idx);
			 }
			 
			 result = function.execute(xctxt);
		  } 
		  catch (WrongNumberArgsException ex) {			    
			 throw new javax.xml.transform.TransformerException("XPTY0004 : Wrong number of arguments provided, "
					                                                   									+ "during function call " + funcExpandedName + ".", srcLocator); 
		  }               
	  }
	  else {
		  throw new javax.xml.transform.TransformerException("XPTY0004 : There is no function declaration found, for the function " + funcExpandedName + ".", srcLocator);
	  }

	  return result;
  }
  
  /**
   * Method definition, to get the result of XPath 3.1 function call fn:apply.
   * 
   * @param xpathInlineFunction                  The supplied XPath inline function
   *                                             object reference.
   * @param arrXPathExpr                         An XPath function fn:apply second argument, 
   *                                             XPath expression object.
   * @param xctxt                                An XPath context object
   * @return                                     The result of an XPath function call
   *                                             fn:apply.
   * @throws TransformerException
   */
  private XObject getFnApplyResult(XPathInlineFunction xpathInlineFunction, Expression arrXPathExpr, 
		                           XPathContext xctxt) throws TransformerException {
	  
	  XObject result = null;
	  
	  SourceLocator srcLocator = xctxt.getSAXLocator();
	  
	  // Construct an XPath dynamic function call expression, to evaluate this function call
	  
	  XPathDynamicFunctionCall xpathDynamicFunctionCall = new XPathDynamicFunctionCall();
	  String funcRefVarName = "dfc_" + (UUID.randomUUID()).toString();
	  xpathDynamicFunctionCall.setFuncRefVarName(funcRefVarName);

	  Map<QName, XObject> inlineFunctionVarMap = xctxt.getXPathVarMap();
	  inlineFunctionVarMap.put(new QName(funcRefVarName), xpathInlineFunction);

	  if (!(arrXPathExpr instanceof XPathArrayConstructor)) {
		  throw new javax.xml.transform.TransformerException("FORG0006 : An XPath 3.1 function 'apply' second argument is not an xdm array.", srcLocator);  
	  }
	  else {
		  XPathArrayConstructor xpathArrConstructor = (XPathArrayConstructor)arrXPathExpr;
		  List<String> arrConsXPathParts = xpathArrConstructor.getArrayConstructorXPathParts();
		  List<InlineFunctionParameter> inlineFuncParamList = xpathInlineFunction.getFuncParamList();
		  
		  int size1 = arrConsXPathParts.size();
		  
		  int size2 = inlineFuncParamList.size();
		  
		  if (size1 != size2) {
			  throw new TransformerException("XPTY0004 : An XPath 3.1 function call 'apply' function item's arity, is not equal "
			  		                                                                                                           + "to the supplied xdm array's "
			  		                                                                                                           + "cardinality.", srcLocator); 
		  }
		  else {
			  List<String> dfcArgList = new ArrayList<String>();			  			  
			  
			  for (int idx = 0; idx < size1; idx++) {
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
   * Method definition, to get the result of XPath 3.1 function call fn:apply.
   * 
   * @param elemFunction                         The supplied XSL stylesheet function
   *                                             compiled object instance.
   * @param arrXPathExpr                         An XPath function fn:apply second argument, 
   *                                             XPath expression object.
   * @param xctxt                                An XPath context object
   * @param transformerImpl                      An XSL transformation implementation object
   *                                             instance.
   * @return                                     The result of an XPath function call
   *                                             fn:apply.
   * @throws TransformerException
   */
  private XObject getFnApplyResult(ElemFunction elemFunction, Expression arrXPathExpr, 
		                                                                              XPathContext xctxt, TransformerImpl transformerImpl) throws TransformerException {
	  
	  XObject result = null;
	  
	  SourceLocator srcLocator = xctxt.getSAXLocator();
	  
	  if (!(arrXPathExpr instanceof XPathArrayConstructor)) {
		  throw new javax.xml.transform.TransformerException("FORG0006 : An XPath 3.1 function 'apply' first argument is not a function reference.", srcLocator);
	  }
	  else {
		  XPathArrayConstructor xpathArrConstructor = (XPathArrayConstructor)arrXPathExpr;
		  List<String> arrConsXPathParts = xpathArrConstructor.getArrayConstructorXPathParts();
		  
		  int xslFunctionParamCount = elemFunction.getArity();
		  
		  int size1 = arrConsXPathParts.size();
		  
		  if (size1 != xslFunctionParamCount) {
			  throw new TransformerException("XPTY0004 : An XPath 3.1 function call 'apply' function item's arity, is not equal "
																								                               + "to the supplied xdm array's "
																								                               + "cardinality.", srcLocator);
		  }
		  else {
			  final int contextNode = xctxt.getCurrentNode();

			  List<XMLNSDecl> prefixTable = XslTransformEvaluationHelper.getXSLNsPrefixTable(xctxt);

			  ResultSequence argSequence = new ResultSequence();
			  
			  for (int idx = 0; idx < xslFunctionParamCount; idx++) {
				  String xpathStr = arrConsXPathParts.get(idx);
				  
				  if (prefixTable != null) {
					  xpathStr = XslTransformEvaluationHelper.replaceXmlNsUrisWithPrefixesXPathStr(xpathStr, prefixTable);
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
