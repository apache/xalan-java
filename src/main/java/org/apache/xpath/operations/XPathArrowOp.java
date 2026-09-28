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
package org.apache.xpath.operations;

import java.util.List;
import java.util.Vector;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.templates.XMLNSDecl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xpath.Expression;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.composite.XPathNamedFunctionReference;
import org.apache.xpath.composite.XPathSequenceConstructor;
import org.apache.xpath.functions.Function;
import org.apache.xpath.functions.Function2Args;
import org.apache.xpath.functions.Function3Args;
import org.apache.xpath.functions.FunctionOneArg;
import org.apache.xpath.functions.WrongNumberArgsException;
import org.apache.xpath.functions.XPathDynamicFunctionCall;
import org.apache.xpath.functions.XSL3ConstructorOrExtensionFunction;
import org.apache.xpath.functions.XSL3FunctionService;
import org.apache.xpath.functions.XSLFunctionBuilder;
import org.apache.xpath.functions.json.FuncJsonDoc;
import org.apache.xpath.functions.json.FuncJsonToXml;
import org.apache.xpath.functions.json.FuncParseJson;
import org.apache.xpath.functions.json.FuncXmlToJson;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathArray;
import org.apache.xpath.objects.XPathMap;

import xml.xpath31.processor.types.XSDouble;
import xml.xpath31.processor.types.XSNumericType;

/**
 * An XPath 3.1 expression arrow operator, '=>' implementation. 
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XPathArrowOp extends XPathOperator
{

	private static final long serialVersionUID = 4577709194385888770L;
	
	/**
	 * Class field, to represent the suffix of a chained XPath 3.1 
	 * arrow expression. For example, with an XPath expression 
	 * a=>func1()=>func2() the value of this class field shall be =>func2().
	 */
	private java.lang.String m_xpath_arrowOpRemainingExprStr;
	
	// Class field, used for Xalan-J fixupVariables action
    private Vector m_vars;
    
    // Class field, used for Xalan-J fixupVariables action
    private int m_globals_size;
    
    private XSL3FunctionService m_xsl3FunctionService = XSLFunctionBuilder.getXSLFunctionService();

	/**
	 * Evaluate an XPath arrow operator, and return the result.
	 *
	 * @param left non-null reference to the evaluated left operand
	 * @param right non-null reference to the evaluated right operand
	 *
	 * @return non-null reference to the XObject that represents the result of the operation
	 *
	 * @throws javax.xml.transform.TransformerException
	 */
    public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException {
        
      XObject result = null;
      
      SourceLocator srcLocator = xctxt.getSAXLocator();
      
      List<XMLNSDecl> prefixTable = XslTransformEvaluationHelper.getXSLNsPrefixTable(xctxt);
      
      if (m_right instanceof XPathDynamicFunctionCall) {
    	 XPathDynamicFunctionCall dfc = (XPathDynamicFunctionCall)m_right;    	     	 
    	 
    	 Expression lArg = m_left;
    	 
    	 XObject lArgObj = lArg.execute(xctxt);
    	 
    	 dfc.setArg0(lArgObj);
    	 
    	 result = dfc.execute(xctxt);
      }
      else if (m_right instanceof XSL3ConstructorOrExtensionFunction) {
    	 XSL3ConstructorOrExtensionFunction xsl3ConstructorOrExtensionFunction = (XSL3ConstructorOrExtensionFunction)m_right;
    	 Vector argVector = xsl3ConstructorOrExtensionFunction.getArgVector();
    	 
    	 Expression lArg = m_left;
    	 
    	 XObject lArgObj = lArg.execute(xctxt);
    	 
    	 argVector.add(0, lArgObj);
    	 
    	 result = xsl3ConstructorOrExtensionFunction.execute(xctxt);
      }
      else if (m_right instanceof XPathNamedFunctionReference) {
    	  if ((m_xpath_arrowOpRemainingExprStr != null) && m_xpath_arrowOpRemainingExprStr.startsWith("(") 
    			                                        && m_xpath_arrowOpRemainingExprStr.endsWith(")")) {    				     		  
    		  int size2 = m_xpath_arrowOpRemainingExprStr.length();
    		  java.lang.String str1 = null;

    		  if (size2 > 2) {
    			  str1 = m_xpath_arrowOpRemainingExprStr.substring(1, size2 - 1);
    			  str1 = str1.trim();
    		  }
    		  else {
    			  str1 = "";  
    		  }

    		  Expression lArg = m_left;

    		  XObject lArgObj = lArg.execute(xctxt);

    		  if (lArgObj instanceof ResultSequence) {
    			  // to do 
    		  }
    		  else if (lArgObj instanceof XMLNodeCursorImpl) {
    			  // to do 
    		  }
    		  else if ("".equals(str1)) {
    			  // An XPathNamedFunctionReference object needs to be called, 
    			  // with an empty argument list.

    			  ResultSequence argSeq = new ResultSequence();
    			  argSeq.add(lArgObj);

    			  result = m_xsl3FunctionService.evaluateXPathNamedFunctionReference((XPathNamedFunctionReference)m_right, null, argSeq, 
    					                                                                                                              prefixTable, m_vars, m_globals_size, getExpressionOwner(), xctxt);

    			  return result;
    		  }
    	  }
    	  else {
    		  throw new TransformerException("XPST0003 : An XPath 3.1 expression syntax error while evaluating, an XPath operator =>. An expected token '(' is absent.", srcLocator); 
    	  }
      }
      else if (m_right instanceof XPathSequenceConstructor) {
    	 XPathSequenceConstructor xpathSeqCons = (XPathSequenceConstructor)m_right;
    	 
    	 XObject xObj = xpathSeqCons.execute(xctxt);
    	 
    	 if (xObj instanceof ResultSequence) {
    		 ResultSequence rSeq = (ResultSequence)xObj;
    		 int size1 = rSeq.size();

    		 if (size1 == 1) {
    			 xObj = rSeq.item(0); 
    		 }
    		 else {
    			 throw new TransformerException("XPTY0004 : While evaluating an XPath 3.1 operator '=>' the right operand "
    			 		                                                                                                  + "is an xdm empty sequence, or an xdm sequence with "
    			 		                                                                                                  + "size greater than one. An XPath required operand type is a function item.", srcLocator);	
    		 }

    		 if (xObj instanceof XPathNamedFunctionReference) {
    			 if ((m_xpath_arrowOpRemainingExprStr != null) && m_xpath_arrowOpRemainingExprStr.startsWith("(") 
    					                                       && m_xpath_arrowOpRemainingExprStr.endsWith(")")) {    				 
    				 int size2 = m_xpath_arrowOpRemainingExprStr.length();
    				 java.lang.String str1 = null;
    				 
    				 if (size2 > 2) {
    					str1 = m_xpath_arrowOpRemainingExprStr.substring(1, size2 - 1);
    					str1 = str1.trim();
    				 }
    				 else {
    					str1 = "";  
    				 }
    				 
    				 Expression lArg = m_left;
    		    	 
    		    	 XObject lArgObj = lArg.execute(xctxt);
    		    	 
    		    	 if (lArgObj instanceof ResultSequence) {
    		    		 // to do 
    		    	 }
    		    	 else if (lArgObj instanceof XMLNodeCursorImpl) {
    		    		 // to do 
    		    	 }
    		    	 else if ("".equals(str1)) {
    		    		 // An XPathNamedFunctionReference object needs to be called, 
    		    		 // with an empty argument list.

    		    		 ResultSequence argSeq = new ResultSequence();
    		    		 argSeq.add(lArgObj);

    		    		 result = m_xsl3FunctionService.evaluateXPathNamedFunctionReference((XPathNamedFunctionReference)xObj, null, argSeq, 
    		    				                                                                prefixTable, m_vars, m_globals_size, getExpressionOwner(), xctxt);

    		    		 return result;
    		    	 }
    			 }
    			 else {
    				 throw new TransformerException("XPST0003 : An XPath 3.1 expression syntax error while evaluating, an XPath operator =>. An expected token '(' is absent.", srcLocator); 
    			 }
    		 }
    		 else if ((xObj instanceof XPathMap) || (xObj instanceof XPathArray)) {
    			 if ((m_xpath_arrowOpRemainingExprStr != null) && m_xpath_arrowOpRemainingExprStr.startsWith("(") 
    					                                       && m_xpath_arrowOpRemainingExprStr.endsWith(")")) {    				 
    				 int size2 = m_xpath_arrowOpRemainingExprStr.length();
    				 java.lang.String str1 = null;

    				 if (size2 > 2) {
    					 str1 = m_xpath_arrowOpRemainingExprStr.substring(1, size2 - 1);
    					 str1 = str1.trim();
    				 }
    				 else {
    					 str1 = "";  
    				 }

    				 XObject xObj0 = m_left.execute(xctxt);    				     				 

    				 if (xObj0 instanceof ResultSequence) {
    					 // to do 
    				 }
    				 else if (xObj0 instanceof XMLNodeCursorImpl) {
    					 // to do 
    				 }
    				 else if ("".equals(str1)) {
    					 if (xObj instanceof XPathMap) {
    						 XPathMap xpathMap = (XPathMap)xObj;
    						 
    						 result = xpathMap.get(xObj0);
    						 
    						 if (result == null) {
    							result = new ResultSequence();
    						 }
    						 
    						 return result;
    					 }
    					 else {
    						 XPathArray xpathArr = (XPathArray)xObj;
    						     						 
    						 XSNumericType xObj1 = null;
    						 
    						 if (xObj0 instanceof XNumber) {
    							XNumber xNumber = (XNumber)xObj0;
    							
    							if (xNumber.getXsDecimal() != null) {
    							   xObj1 = xNumber.getXsDecimal(); 
    							}
    							else if (xNumber.getXsDouble() != null) {
    							   xObj1 = xNumber.getXsDouble(); 
    							}
    							else if (xNumber.getXsInteger() != null) {
    							   xObj1 = xNumber.getXsInteger(); 
    							}
    							else {
    							   xObj1 = new XSDouble(xNumber.num()); 
    							}
    						 }
    						 else if (xObj0 instanceof XSNumericType) {
    							xObj1 = (XSNumericType)xObj0; 
    						 }
    						 else {
    							throw new TransformerException("XPTY0004 : While evaluating an XPath 3.1 operator '=>', an XPath "
    									                                                                                         + "first operand is not numeric required for "
    									                                                                                         + "an xdm array index.", srcLocator);
    						 }
    						 
    						 java.lang.String str2 = XslTransformEvaluationHelper.getStrVal(xObj1);
    						 
    						 int idx = 0;
    						 
    						 try {
    							double dbl = Double.valueOf(str2);
    							
    							if ((int)dbl == dbl) {
    							   idx = (int)dbl; 
    							}
    							else {
    							   throw new TransformerException("XPTY0004 : While evaluating an XPath 3.1 operator '=>', an XPath "
                                                                                                                                    + "first operand is not an integer or "
                                                                                                                                    + "couldn't be cast to an integer, required for an xdm array index.", srcLocator);
    							}
    						 }
    						 catch (NumberFormatException ex) {
    							// to do 
    						 }
    						 
    						 int size3 = xpathArr.size();
    						 
    						 if ((idx >= 1) && (idx <= size3)) {
    						    result = xpathArr.get(idx - 1);
    						 }
    						 else {
    							 throw new TransformerException("XPTY0004 : While evaluating an XPath 3.1 operator '=>', an XPath "
                                                                                                                                  + "first operand is an integer, not within an xdm array's size. "
                                                                                                                                  + "The supplied xdm array index value is " + idx + ", while "
                                                                                                                                  + "an xdm array size is " + size3 + ".", srcLocator);
    						 }
    					 }
    				 }
    			 }
    			 else {
    				 throw new TransformerException("XPST0003 : An XPath 3.1 expression syntax error while evaluating, an XPath operator =>. An expected token '(' is absent.", srcLocator); 
    			 }
    		 }
    	 }
    	 else {
    		 // to do
    	 }
      }      
      else {
    	  Function function = (Function)m_right;
    	  
    	  if (function instanceof FuncParseJson) {
    		  FuncParseJson fnParseJson = (FuncParseJson)function; 
    		  
    		  fnParseJson.setArg0(null);
    		  
    		  try {
				 fnParseJson.setArg(null, 1);
			  } 
    		  catch (WrongNumberArgsException ex) {
    			 throw new TransformerException("FORX0003 : An XPath 3.1 operator => evaluation has, an XPath dynamic error.", srcLocator);
			  }
    		  
              Function3Args funcThreeArgs = (Function3Args)function;
    		  
    		  Expression arg0 = funcThreeArgs.getArg0();
    		  
    		  if (!(m_left instanceof Function)) {
    			  fnParseJson.setArg0(m_left);  
    		  }
    		  else {
    			  XObject funcEvalResult = m_left.execute(xctxt);
    			  fnParseJson.setArg0(funcEvalResult);
    		  }
    		  
    		  try {
    			  if (arg0 != null) {
    				 fnParseJson.setArg(arg0, 1);
    			  }
    		  }
    		  catch (WrongNumberArgsException ex) {
    			  throw new TransformerException("FORX0003 : An XPath 3.1 operator => evaluation has, an XPath dynamic error.", srcLocator);
    		  }
    		  
    		  result = fnParseJson.execute(xctxt); 
    	  }
    	  else if (function instanceof FuncJsonDoc) {
    		  FuncJsonDoc fnJsonDoc = (FuncJsonDoc)function; 
    		  
    		  fnJsonDoc.setArg0(null);
    		  
    		  try {
				 fnJsonDoc.setArg(null, 1);
			  } 
    		  catch (WrongNumberArgsException ex) {
    			 throw new TransformerException("FORX0003 : An XPath 3.1 operator => evaluation has, an XPath dynamic error.", srcLocator);
			  }
    		  
              Function3Args funcThreeArgs = (Function3Args)function;
    		  
    		  Expression arg0 = funcThreeArgs.getArg0();
    		  
    		  if (!(m_left instanceof Function)) {
    			  fnJsonDoc.setArg0(m_left);  
    		  }
    		  else {
    			  XObject funcEvalResult = m_left.execute(xctxt);
    			  fnJsonDoc.setArg0(funcEvalResult);
    		  }
    		  
    		  try {
    			  if (arg0 != null) {
    				 fnJsonDoc.setArg(arg0, 1);
    			  }
    		  }
    		  catch (WrongNumberArgsException ex) {
    			  throw new TransformerException("FORX0003 : An XPath 3.1 operator => evaluation has, an XPath dynamic error.", srcLocator);
    		  }
    		  
    		  result = fnJsonDoc.execute(xctxt); 
    	  }
    	  else if (function instanceof FuncJsonToXml) {
    		  FuncJsonToXml fnJsonToXml = (FuncJsonToXml)function; 
    		  
    		  fnJsonToXml.setArg0(null);
    		  
    		  try {
				 fnJsonToXml.setArg(null, 1);
			  } 
    		  catch (WrongNumberArgsException ex) {
    			 throw new TransformerException("FORX0003 : An XPath 3.1 operator => evaluation has, an XPath dynamic error.", srcLocator);
			  }
    		  
              Function3Args funcThreeArgs = (Function3Args)function;
    		  
    		  Expression arg0 = funcThreeArgs.getArg0();
    		  
    		  if (!(m_left instanceof Function)) {
    			  fnJsonToXml.setArg0(m_left);  
    		  }
    		  else {
    			  XObject funcEvalResult = m_left.execute(xctxt);
    			  fnJsonToXml.setArg0(funcEvalResult);
    		  }
    		  
    		  try {
    			  if (arg0 != null) {
    				 fnJsonToXml.setArg(arg0, 1);
    			  }
    		  }
    		  catch (WrongNumberArgsException ex) {
    			  throw new TransformerException("FORX0003 : An XPath 3.1 operator => evaluation has, an XPath dynamic error.", srcLocator);
    		  }
    		  
    		  result = fnJsonToXml.execute(xctxt);
    	  }
    	  else if (function instanceof FuncXmlToJson) {
    		  FuncXmlToJson fnXmlToJson = (FuncXmlToJson)function; 
    		  
    		  fnXmlToJson.setArg0(null);
    		  
    		  try {
				 fnXmlToJson.setArg(null, 1);
			  } 
    		  catch (WrongNumberArgsException ex) {
    			 throw new TransformerException("FORX0003 : An XPath 3.1 operator => evaluation has, an XPath dynamic error.", srcLocator);
			  }
    		  
              Function3Args funcThreeArgs = (Function3Args)function;
    		  
    		  Expression arg0 = funcThreeArgs.getArg0();
    		  
    		  if (!(m_left instanceof Function)) {
    			  fnXmlToJson.setArg0(m_left);  
    		  }
    		  else {
    			  XObject funcEvalResult = m_left.execute(xctxt);
    			  fnXmlToJson.setArg0(funcEvalResult);
    		  } 
    		  
    		  try {
    			  if (arg0 != null) {
    				 fnXmlToJson.setArg(arg0, 1);
    			  }
    		  }
    		  catch (WrongNumberArgsException ex) {
    			  throw new TransformerException("FORX0003 : An XPath 3.1 operator => evaluation has, an XPath dynamic error.", srcLocator);
    		  }
    		  
    		  result = fnXmlToJson.execute(xctxt);
    	  }
    	  else if (function instanceof Function3Args) {
    		  Function3Args funcThreeArgs = (Function3Args)function;
    		  
    		  Expression arg0 = funcThreeArgs.getArg0();
    		  Expression arg1 = funcThreeArgs.getArg1();
    		  
    		  if (!(m_left instanceof Function)) {
    			 funcThreeArgs.setArg0(m_left);  
    		  }
    		  else {
    			 XObject funcEvalResult = m_left.execute(xctxt);
    			 funcThreeArgs.setArg0(funcEvalResult);
    		  }    		  
    		  
    		  try {
    			  if (arg0 != null) {
    				 funcThreeArgs.setArg(arg0, 1);
    			  }
    			  
    			  if (arg1 != null) {
    				 funcThreeArgs.setArg(arg1, 2);
    			  }
    		  }
    		  catch (WrongNumberArgsException ex) {
    			  throw new TransformerException("FORX0003 : An XPath 3.1 operator => evaluation has, an XPath dynamic error.", srcLocator);
    		  }

    		  result = funcThreeArgs.execute(xctxt);
    	  }
    	  else if (function instanceof Function2Args) {
    		  Function2Args funcTwoArgs = (Function2Args)function;
    		  Expression expr1 = funcTwoArgs.getArg1();

    		  if (expr1 != null) {
    			  throw new TransformerException("FORX0003 : While evaluating an XPath 3.1 operator '=>', the second function "
    			  		                                                                                                      + "item argument with arity two, "
    			  		                                                                                                      + "cannot be supplied with a second argument.", srcLocator); 
    		  }
    		  else {     		
    			  Expression arg0 = funcTwoArgs.getArg0();
    			  
    			  if (!(m_left instanceof Function)) {
    				  funcTwoArgs.setArg0(m_left);  
    			  }
    			  else {
    				  XObject funcEvalResult = m_left.execute(xctxt);
    				  funcTwoArgs.setArg0(funcEvalResult);
    			  }
    			  
    			  try {     		   
    				  funcTwoArgs.setArg(arg0, 1);
    			  } 
    			  catch (WrongNumberArgsException ex) {
    				  throw new TransformerException("FORX0003 : An XPath 3.1 operator => evaluation has, an XPath dynamic error.", srcLocator);
    			  }
    			  
    			  result = funcTwoArgs.execute(xctxt);
    		  }
    	  }
    	  else if (function instanceof FunctionOneArg) {
    		  FunctionOneArg funcOneArg = (FunctionOneArg)function;
    		  Expression arg0 = funcOneArg.getArg0();

    		  if (arg0 != null) {
    			  throw new TransformerException("FORX0003 : While evaluating an XPath 3.1 operator '=>', the second function "
																								                              + "item argument with arity one, "
																								                              + "cannot be supplied with a function argument.", srcLocator); 
    		  }
    		  else {    			  
    			  if (!(m_left instanceof Function)) {
    				  funcOneArg.setArg0(m_left);  
    			  }
    			  else {
    				  XObject funcEvalResult = m_left.execute(xctxt);
    				  funcOneArg.setArg0(funcEvalResult);
    			  }
    			  
    			  result = funcOneArg.execute(xctxt);
    		  }
    	  }    	  
      }
      
      if ((m_xpath_arrowOpRemainingExprStr != null) && m_xpath_arrowOpRemainingExprStr.contains("=>")) {         
		  result = getXPathArrowOpFinalResult(result, m_xpath_arrowOpRemainingExprStr, xctxt);
	  }
      
      return result;
      
    }
    
    @Override
    public void fixupVariables(Vector vars, int globalsSize) {
        m_vars = (Vector)(vars.clone());
        m_globals_size = globalsSize; 
    }
    
    public java.lang.String getArrowOpRemainingXPathExprStr() {
    	return m_xpath_arrowOpRemainingExprStr;
    }

    public void setArrowOpRemainingXPathExprStr(java.lang.String arrowOpRemainingXPathExprStr) {
    	this.m_xpath_arrowOpRemainingExprStr = arrowOpRemainingXPathExprStr;
    }
    
    /**
     * Method definition, to handle more than one occurrence of an XPath 
     * arrow operator, "=>" within an XPath expression.  
     * 
     * @param prevResult                               Partial previous result of
     *                                                 evaluation
     * @param arrowOpRemainingXPathExprStr             An XPath remaining expression string
     *                                                 of the form =>...
     * @param xctxt                                    An XPath context object
     * @return                                         The result of XPath expression evaluation
     * @throws TransformerException
     */
    private XObject getXPathArrowOpFinalResult(XObject prevResult, java.lang.String arrowOpRemainingXPathExprStr, 
    		                                                                          XPathContext xctxt) throws TransformerException {
       XObject result = null;
       
       SourceLocator srcLocator = xctxt.getSAXLocator(); 
       
       int idx = arrowOpRemainingXPathExprStr.indexOf("=>");
       java.lang.String arrowOpNextStr = arrowOpRemainingXPathExprStr.substring(idx + 2);
       
       int idx3 = arrowOpNextStr.indexOf("=>");
       java.lang.String arrowOpRemainingXPathExprStr2 = null;
       if (idx3 != -1) {
    	  arrowOpRemainingXPathExprStr2 = arrowOpNextStr.substring(idx3);  
       }
       
       int idx2 = arrowOpNextStr.indexOf('(');
       java.lang.String str1 = arrowOpNextStr.substring(0, idx2 + 1) + "''";
       java.lang.String a1 = arrowOpNextStr.substring(idx2 + 1);
       if (a1.startsWith(")")) {
    	  str1 = str1 + ")";
       }
       else {
    	  str1 = str1 + "," + arrowOpNextStr.substring(idx2 + 1);
       }
       
       arrowOpNextStr = str1;
       
       List<XMLNSDecl> prefixTable = XslTransformEvaluationHelper.getXSLNsPrefixTable(xctxt);
       
       if (prefixTable != null) {
          arrowOpNextStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(arrowOpNextStr, prefixTable);
       }
       
       XPath xpathObj = new XPath(arrowOpNextStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
       Expression expr1 = xpathObj.getExpression();
       
       if (expr1 instanceof XPathDynamicFunctionCall) {
    	   XPathDynamicFunctionCall dfc = (XPathDynamicFunctionCall)expr1;    	     	 
    	   dfc.setArg0(prevResult);
    	   
    	   result = dfc.execute(xctxt);
       }
       else if (expr1 instanceof XSL3ConstructorOrExtensionFunction) {
    	   XSL3ConstructorOrExtensionFunction xsl3ConstructorOrExtensionFunction = (XSL3ConstructorOrExtensionFunction)expr1;
    	   Vector argVector = xsl3ConstructorOrExtensionFunction.getArgVector();

    	   argVector.add(0, prevResult);

    	   result = xsl3ConstructorOrExtensionFunction.execute(xctxt);
       }
       else {
    	   Function function = (Function)expr1;

    	   if (function instanceof Function3Args) {
    		   Function3Args funcThreeArgs = (Function3Args)function;    		   
    		   funcThreeArgs.setArg0(prevResult);    		   
    		   result = funcThreeArgs.execute(xctxt);
    	   }
    	   else if (function instanceof Function2Args) {
    		   Function2Args funcTwoArgs = (Function2Args)function;    		   
    		   funcTwoArgs.setArg0(prevResult);
    			   
    		   result = funcTwoArgs.execute(xctxt);
    	   }
    	   else if (function instanceof FunctionOneArg) {
    		   FunctionOneArg funcOneArg = (FunctionOneArg)function;
    		   funcOneArg.setArg0(prevResult);
    		   
    		   result = funcOneArg.execute(xctxt);
    	   }

    	   if (arrowOpRemainingXPathExprStr2 != null) {         
    		   result = getXPathArrowOpFinalResult(result, arrowOpRemainingXPathExprStr2, xctxt);
    	   }
       }
       
       return result;
       
    }
    
}
