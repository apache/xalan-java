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
package org.apache.xpath.functions;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.extensions.ExpressionContext;
import org.apache.xalan.templates.ElemFunction;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xalan.templates.XMLNSDecl;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.xs.XSSimpleTypeDecl;
import org.apache.xml.utils.QName;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionNode;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.composite.XPathNamedFunctionReference;
import org.apache.xpath.objects.ElemFunctionItem;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathArray;
import org.apache.xpath.objects.XPathInlineFunction;
import org.apache.xpath.objects.XPathMap;
import org.apache.xpath.objects.XString;

import xml.xpath31.processor.types.XSString;

/**
 * This class implements an XPath 3.1 dynamic function call, and
 * XDM map & array information lookup using function call syntax and unary 
 * lookup syntax respectively.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XPathDynamicFunctionCall extends Expression {
    
    private static final long serialVersionUID = -4177034386870890029L;

    /**
     * There are following three types of XPath expression evaluations, that 
     * this class implementation solves:
     * 
     * $funcVar(arg1, ...)   This is a call to an XDM inline function definition, 
     *                       where variable $funcVar is a function item.
     *                                
     * $mapVar(key)          This is an XDM map entry's value lookup via function 
     *                       call syntax.         
     * 
     * $mapVar?key           This is an XDM map entry's value lookup via unary 
     *                       lookup operator.
     *                  
     * $arrayVar?index       This is an XDM array item lookup for a given array 
     *                       variable reference and index value, for e.g $arrayVar?3.                  
     */
    
    private String m_funcRefVarName;
    
    private boolean m_isUnaryLookup;
    
    private List<String> m_argList;
    
    private List<String> m_trailingArgList;
    
    private String[] m_xpathChainedArgListArr;
    
    /**
     * The class fields m_vars & m_globals_size declared below are used during 
     * XPath.fixupVariables(..) action as performed within object of this class.
     */
    
    private Vector m_vars;
    
    private int m_globals_size;
    
    private XSL3FunctionService m_xsl3FunctionService = XSLFunctionBuilder.getXSLFunctionService();
    
    /**
     * Evaluate an XPath dynamic function call expression.
     */
    @Override
    public XObject execute(XPathContext xctxt) throws TransformerException {
        
       XObject evalResult = null;
       
       SourceLocator srcLocator = xctxt.getSAXLocator();
       
       int contextNode = xctxt.getContextNode();
       
       Map<QName, XObject> inlineFunctionVarMap = xctxt.getXPathVarMap();
       
       // We find below, reference of an XPath inline function, that this dynamic
       // function call refers to.       
       XObject functionRef = inlineFunctionVarMap.get(new QName(m_funcRefVarName));
       
       if (functionRef == null) {
           ExpressionContext exprContext = xctxt.getExpressionContext();
          
           try {
              functionRef = exprContext.getVariableOrParam(new QName(m_funcRefVarName));
           }
           catch (TransformerException ex) {
              // Trying to get an XPath inline function reference, from within 
              // stylesheet's global variable scope. 
              ExpressionNode expressionNode = getExpressionOwner();
              ExpressionNode stylesheetRootNode = null;
              while (expressionNode != null) {
                 stylesheetRootNode = expressionNode;
                 expressionNode = expressionNode.exprGetParent();                     
              }
              StylesheetRoot stylesheetRoot = (StylesheetRoot)stylesheetRootNode;
              Map<QName, XPathInlineFunction> globalInlineFunctionVarMap = stylesheetRoot.
                                                                            getInlineFunctionVarMap();
              functionRef = globalInlineFunctionVarMap.get(new QName(m_funcRefVarName)); 
           }           
       }
       
       List<XMLNSDecl> prefixTable = null;
       
       if (functionRef != null) {
    	    ElemTemplateElement elemTemplateElement = (ElemTemplateElement)xctxt.getNamespaceContext();
            
            if (elemTemplateElement != null) {
               prefixTable = (List<XMLNSDecl>)elemTemplateElement.getPrefixTable();
            }
                	    
    	    if (functionRef instanceof XPathInlineFunction) {    	    		    	    	
	           evalResult = m_xsl3FunctionService.evaluateXPathInlineFunction((XPathInlineFunction)functionRef, m_argList, 
	        		                                                           xctxt, prefixTable, m_vars, m_globals_size, 
	        		                                                           m_xpathVarList, m_funcRefVarName);
               
	           if ((evalResult instanceof XPathNamedFunctionReference) && (m_trailingArgList != null)) {
	        	  evalResult = m_xsl3FunctionService.evaluateXPathNamedFunctionReference((XPathNamedFunctionReference)evalResult, m_trailingArgList, 
	        			                                                                  prefixTable, m_vars, m_globals_size, getExpressionOwner(), 
	        			                                                                  xctxt); 
	           }
	        }
    	    else if (functionRef instanceof XPathMap) {
     		   XPathMap xpathMap = (XPathMap)functionRef;
     		   
     		   if (m_xpathChainedArgListArr != null) {
     			  for (int idx = 0; idx < m_xpathChainedArgListArr.length; idx++) {
     				 String argXPathStr = m_xpathChainedArgListArr[idx];     				 
     				 if (idx == 0) {
     				    evalResult = getXPathMapEntryValueByKey(xpathMap, argXPathStr, prefixTable, xctxt);
     				 }
     				 else if (evalResult instanceof XPathMap) {
     					evalResult = getXPathMapEntryValueByKey((XPathMap)evalResult, argXPathStr, prefixTable, xctxt); 
     				 }
     				 else if (evalResult instanceof XPathArray) {
     					 if (prefixTable != null) {
     						 argXPathStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(argXPathStr, prefixTable);
     					 }

     					 XPath argXPath = new XPath(argXPathStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
     					 if (m_vars != null) {
     						 argXPath.fixupVariables(m_vars, m_globals_size);
     					 }

     					 XObject argValue = argXPath.execute(xctxt, contextNode, xctxt.getNamespaceContext());
     					 
     					 evalResult = getXPathArrayResultByIndex((XPathArray)evalResult, argValue, xctxt); 
     				 }
     			  }
     		   }
     		   else {
     			   if (m_argList.size() != 1) {
     				   throw new javax.xml.transform.TransformerException("XPTY0004 : Function call reference for map information lookup, needs to have "
																								     				    + "one argument which should be one of map's key "
																								     				    + "name.", srcLocator); 
     			   }
     			   else {
     				   String argXPathStr = m_argList.get(0);

     				   if ("*".equals(argXPathStr)) {
     					   // This is XDM map's wild-card key specifier. To return 
     					   // all the map entry values as typed sequence.     				  
     					   Map<XObject, XObject> nativeMap = xpathMap.getNativeMap();
     					   ResultSequence rSeq = new ResultSequence();
     					   Set<XObject> keySet = nativeMap.keySet();
     					   Iterator<XObject> iter = keySet.iterator();
     					   while (iter.hasNext()) {
     						   XObject xObj = iter.next();
     						   rSeq.add(nativeMap.get(xObj));
     					   }

     					   evalResult = rSeq;
     				   }
     				   else if (".".equals(argXPathStr)) {
     					   XObject contextItem = xctxt.getXPath3ContextItem();
     					   evalResult = xpathMap.get(contextItem);
     				   }
     				   else {
     					   evalResult = getXPathMapEntryValueByKey(xpathMap, argXPathStr, prefixTable, xctxt);

     					   if ((evalResult instanceof XPathMap) && (m_trailingArgList != null)) {
     						   if (m_trailingArgList.size() == 1) {
     							   argXPathStr = m_trailingArgList.get(0); 
     							   evalResult = getXPathMapEntryValueByKey((XPathMap)evalResult, argXPathStr, prefixTable, xctxt);
     						   }
     						   else {
     							   // Return an empty sequence
     							   evalResult = new ResultSequence(); 
     						   }
     					   }
     				   }
     			   }
    	       }
     	    }
    	    else if (functionRef instanceof XPathArray) {
    	       XPathArray xpathArr = (XPathArray)functionRef;
    	       
    	       if (m_xpathChainedArgListArr != null) { 
      			  for (int idx = 0; idx < m_xpathChainedArgListArr.length; idx++) {
      				 String argXPathStr = m_xpathChainedArgListArr[idx];     				 
      				 if (idx == 0) {
      					 if (prefixTable != null) {
      						 argXPathStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(argXPathStr, prefixTable);
      					 }

      					 XPath argXPath = new XPath(argXPathStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
      					 if (m_vars != null) {
      						 argXPath.fixupVariables(m_vars, m_globals_size);
      					 }

      					 XObject argValue = argXPath.execute(xctxt, contextNode, xctxt.getNamespaceContext());      					 
      					 evalResult = getXPathArrayResultByIndex(xpathArr, argValue, xctxt);
      				 }
      				 else if (evalResult instanceof XPathMap) {
      					evalResult = getXPathMapEntryValueByKey((XPathMap)evalResult, argXPathStr, prefixTable, xctxt); 
      				 }
      				 else if (evalResult instanceof XPathArray) {
      					 if (prefixTable != null) {
      						 argXPathStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(argXPathStr, prefixTable);
      					 }

      					 XPath argXPath = new XPath(argXPathStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
      					 if (m_vars != null) {
      						 argXPath.fixupVariables(m_vars, m_globals_size);
      					 }

      					 XObject argValue = argXPath.execute(xctxt, contextNode, xctxt.getNamespaceContext());
      					 
      					 evalResult = getXPathArrayResultByIndex((XPathArray)evalResult, argValue, xctxt); 
      				 }
      			  }
      		   }
    	       else {
    	    	   if (m_argList.size() != 1) {
    	    		   throw new javax.xml.transform.TransformerException("XPTY0004 : Function call syntax for array information lookup, needs to have "
    	    				   																							 + "one argument which should be position "
    	    				   																							 + "within an array.", srcLocator); 
    	    	   }
    	    	   else {
    	    		   String argXPathStr = m_argList.get(0);
    	    		   if ("*".equals(argXPathStr)) {
    	    			   // This is XDM array's wild-card key specifier. To return 
    	    			   // all the array values as typed sequence. 
    	    			   ResultSequence rSeq = new ResultSequence();
    	    			   for (int idx = 0; idx < xpathArr.size(); idx++) {
    	    				   rSeq.add(xpathArr.get(idx));
    	    			   }    				 
    	    			   evalResult = rSeq;
    	    		   }
    	    		   else if (".".equals(argXPathStr)) {
    	    			   XObject contextItem = xctxt.getXPath3ContextItem();     				  
    	    			   evalResult = getXPathArrayResultByIndex(xpathArr, contextItem, xctxt);
    	    		   }
    	    		   else {
    	    			   if (prefixTable != null) {
    	    				   argXPathStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(argXPathStr, prefixTable);
    	    			   }

    	    			   XPath argXPath = new XPath(argXPathStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
    	    			   if (m_vars != null) {
    	    				   argXPath.fixupVariables(m_vars, m_globals_size);
    	    			   }

    	    			   XObject argValue = argXPath.execute(xctxt, contextNode, xctxt.getNamespaceContext());	                  
    	    			   if (argValue instanceof ResultSequence) {
    	    				   ResultSequence rSeqArg = (ResultSequence)argValue;
    	    				   ResultSequence rSeqAnswer = new ResultSequence(); 
    	    				   for (int idx = 0; idx < rSeqArg.size(); idx++) {
    	    					   XObject oneArgValue = rSeqArg.item(idx);
    	    					   String strVal = XslTransformEvaluationHelper.getStrVal(oneArgValue);
    	    					   int arrQueryIndex = Integer.valueOf(strVal); 
    	    					   rSeqAnswer.add(xpathArr.get(arrQueryIndex - 1));
    	    				   }

    	    				   evalResult = rSeqAnswer;
    	    			   }
    	    			   else {
    	    				   evalResult = getXPathArrayResultByIndex(xpathArr, argValue, xctxt);
    	    			   }
    	    		   }
    	    	   }
    	       }
    	    }
    	    else if (functionRef instanceof ElemFunctionItem) {
    	    	ElemFunction elemFunction = ((ElemFunctionItem)functionRef).getElemFunction();

    	    	ResultSequence argSequence = new ResultSequence(); 
    	    	for (int idx = 0; idx < m_argList.size(); idx++) {
    	    		String argXPathStr = m_argList.get(idx);
    	    		if (prefixTable != null) {
    	    			argXPathStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(argXPathStr, prefixTable);
    	    		}

    	    		XPath argXPath = new XPath(argXPathStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
    	    		if (m_vars != null) {
    	    			argXPath.fixupVariables(m_vars, m_globals_size);
    	    		}

    	    		XObject argValue = argXPath.execute(xctxt, contextNode, xctxt.getNamespaceContext());
    	    		argSequence.add(argValue);
    	    	}

    	    	ExpressionNode expressionNode = getExpressionOwner();
    	    	ExpressionNode stylesheetRootNode = null;
    	    	while (expressionNode != null) {
    	    		stylesheetRootNode = expressionNode;
    	    		expressionNode = expressionNode.exprGetParent();                     
    	    	}
    	    	StylesheetRoot stylesheetRoot = (StylesheetRoot)stylesheetRootNode;

    	    	TransformerImpl transformerImpl = stylesheetRoot.getTransformerImpl();

    	    	evalResult = elemFunction.evaluateXslFunction(transformerImpl, argSequence);
    	    }
    	    else if (functionRef instanceof XPathNamedFunctionReference) {
    	    	ElemFunction elemFunction = ((XPathNamedFunctionReference)functionRef).getXslStylesheetFunction();
    	    	if (elemFunction != null) {
    	    		ResultSequence argSequence = new ResultSequence(); 
        	    	for (int idx = 0; idx < m_argList.size(); idx++) {
        	    		String argXPathStr = m_argList.get(idx);
        	    		if (prefixTable != null) {
        	    			argXPathStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(argXPathStr, prefixTable);
        	    		}

        	    		XPath argXPath = new XPath(argXPathStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
        	    		if (m_vars != null) {
        	    			argXPath.fixupVariables(m_vars, m_globals_size);
        	    		}

        	    		XObject argValue = argXPath.execute(xctxt, contextNode, xctxt.getNamespaceContext());
        	    		argSequence.add(argValue);
        	    	}

        	    	ExpressionNode expressionNode = getExpressionOwner();
        	    	ExpressionNode stylesheetRootNode = null;
        	    	while (expressionNode != null) {
        	    		stylesheetRootNode = expressionNode;
        	    		expressionNode = expressionNode.exprGetParent();                     
        	    	}
        	    	StylesheetRoot stylesheetRoot = (StylesheetRoot)stylesheetRootNode;

        	    	TransformerImpl transformerImpl = stylesheetRoot.getTransformerImpl();

        	    	evalResult = elemFunction.evaluateXslFunction(transformerImpl, argSequence);
    	    	}
    	    	else {
    	    	   evalResult = m_xsl3FunctionService.evaluateXPathNamedFunctionReference((XPathNamedFunctionReference)functionRef, m_argList, 
																						   prefixTable, m_vars, m_globals_size, getExpressionOwner(), 
																						   xctxt);
    	    	}
    	    }
    	    else {
    	       Object obj1 = functionRef.object();
    	       if (obj1 instanceof Function) {
    	    	  Function function = (Function)obj1;    	    	  
    	    	  
    	    	  String funcNamespace = function.getNamespace();
 	    		  String funcLocalName = function.getLocalName();
 	    		  
 	    		  Short[] funcDefinedArity = function.getDefinedArity();
 	    		  List<Short> arityList = Arrays.asList(funcDefinedArity);
 	    		  
 	    		  final short runTimeArityValue = (short)(m_argList.size());
 	    		  
 	    		  String expandedFuncName = "{" + funcNamespace + ":" + funcLocalName + "}#" + runTimeArityValue;
 	    		  
    	    	  if (arityList.contains(runTimeArityValue)) {    	    		 
    	    		 try {
    	    			 for (int idx = 0; idx < m_argList.size(); idx++) {
    	    				 String argXPathStr = m_argList.get(idx);
    	    				 if (prefixTable != null) {
    	    					 argXPathStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(argXPathStr, prefixTable);
    	    				 }

    	    				 XPath argXPath = new XPath(argXPathStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
    	    				 if (m_vars != null) {
    	    					 argXPath.fixupVariables(m_vars, m_globals_size);
    	    				 }

    	    				 XObject argValue = argXPath.execute(xctxt, contextNode, xctxt.getNamespaceContext());
    	    				 function.setArg(argValue, idx);
    	    			 }
    	    		 }
    	    		 catch (WrongNumberArgsException ex) {
    	    			 throw new javax.xml.transform.TransformerException("XPTY0004 : Wrong number of arguments provided, "
                                 																	+ "during function call " + expandedFuncName + ".", srcLocator); 
    	    		 }
    	    		 
    	    		 evalResult = function.execute(xctxt);
    	    	  }
    	    	  else {
    	    		 throw new javax.xml.transform.TransformerException("XPTY0004 : The number of arguments provided for "
    	    		 		                                                                        + "function call " + expandedFuncName + " is "
    	    		 		                                                                        + "incorrect. Required " + runTimeArityValue + ", supplied " 
    	    		 		                                                                        + m_argList.size() + ".", srcLocator); 
    	    	  }
    	       }
    	       else if (obj1 instanceof ElemFunction) {
    	    	  ElemFunction elemFunction = (ElemFunction)obj1;
    	    	  StylesheetRoot stylesheetRoot = functionRef.getXslStylesheetRoot();
	    		  TransformerImpl transformerImpl = stylesheetRoot.getTransformerImpl();	    		  
    	    	  
	    		  int funcArity = elemFunction.getArity();    	    	  
    	    	  
    	    	  if ((m_argList != null) && (m_argList.size() == funcArity)) {    	    		  
    	    		  ResultSequence argSequence = new ResultSequence(); 
    	    		  for (int idx = 0; idx < m_argList.size(); idx++) {
    	    			  String argXPathStr = m_argList.get(idx);
    	    			  if (prefixTable != null) {
    	    				  argXPathStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(argXPathStr, prefixTable);
    	    			  }

    	    			  XPath argXPath = new XPath(argXPathStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
    	    			  if (m_vars != null) {
    	    				  argXPath.fixupVariables(m_vars, m_globals_size);
    	    			  }

    	    			  XObject argValue = argXPath.execute(xctxt, contextNode, xctxt.getNamespaceContext());
    	    			  argSequence.add(argValue);
    	    		  }
    	    		  
    	    		  evalResult = elemFunction.evaluateXslFunction(transformerImpl, argSequence);
    	    	  }
    	    	  else if (funcArity == 0) {
    	    		  ResultSequence argSequence = new ResultSequence();
    	    		  evalResult = elemFunction.evaluateXslFunction(transformerImpl, argSequence);
    	    	  }
    	    	  else {
    	    		  QName funcName = elemFunction.getName();
    	    		  
    	    		  throw new javax.xml.transform.TransformerException("XPTY0004 : The number of arguments provided for "
																	                              + "stylesheet function call " + funcName.toString() + " is "
																	                              + "incorrect. Required " + funcArity + ", supplied " 
																	                              + m_argList.size() + ".", srcLocator);  
    	    	  }
    	       }
    	       else if (isFunctionRefXsSimpleTypeDefinition(functionRef)) {
    	    	   XObject xObj = (((ResultSequence)functionRef)).item(0);
    	    	   XSSimpleTypeDecl xsSimpleTypeDecl = (XSSimpleTypeDecl)(xObj.getXsTypeDefinition());
    	    	   int argCount = m_argList.size();
    	    	   if (argCount == 1) {
    	    		   String argXPathStr = m_argList.get(0);
    	    		   if (prefixTable != null) {
    	    			   argXPathStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(argXPathStr, prefixTable);
    	    		   }

    	    		   XPath argXPath = new XPath(argXPathStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
    	    		   if (m_vars != null) {
    	    			   argXPath.fixupVariables(m_vars, m_globals_size);
    	    		   }

    	    		   XObject argValue = argXPath.execute(xctxt, contextNode, xctxt.getNamespaceContext());
    	    		   String argStrValue = XslTransformEvaluationHelper.getStrVal(argValue);
    	    		   try {
						   xsSimpleTypeDecl.validate(argStrValue, null, null);
						   evalResult = argValue; 
					   } 
    	    		   catch (InvalidDatatypeValueException ex) {
    	    			   throw new javax.xml.transform.TransformerException("XPTY0004 : XPath dynamic function call $" + 
    	    		                                                                                          m_funcRefVarName + "(" + argXPathStr + ") evaluation failed "
    	    		                                                                                          + "with a type error : " + ex.getMessage(), srcLocator); 
					   }
    	    	   }
    	    	   else {
    	    		   throw new javax.xml.transform.TransformerException("XPTY0004 : XPath dynamic function call $" + m_funcRefVarName + "(..) has " + argCount + " "
    	    		   		                                                                                  + "arguments. Expected 1.", srcLocator); 
    	    	   }
    	       }
    	       else {
    	    	   evalResult = XObject.create(new XSString("")); 
    	       }
    	    }
      }
      else {
         throw new javax.xml.transform.TransformerException("XPST0008 : Variable '" + m_funcRefVarName + "' has "
                                                                                                       + "not been declared, or its declaration is not in scope.", 
                                                                                                                                              xctxt.getSAXLocator());    
      }
       
      if ((evalResult instanceof XPathInlineFunction) && (m_trailingArgList != null)) {
    	 evalResult = m_xsl3FunctionService.evaluateXPathInlineFunction((XPathInlineFunction)evalResult, m_trailingArgList, 
    			                                                        xctxt, prefixTable, m_vars, m_globals_size, 
    			                                                        m_xpathVarList, m_funcRefVarName); 
      }
               
      return evalResult;
      
    }

	@Override
    public void fixupVariables(Vector vars, int globalsSize) {
        m_vars = (Vector)(vars.clone());
        m_globals_size = globalsSize; 
    }
    
    @Override
    public void callVisitors(ExpressionOwner owner, XPathVisitor visitor) {
        // NO OP
    }

    @Override
    public boolean deepEquals(Expression expr) {
        return false;
    }
    
	/**
	 * Method definition to get an XDM array item value at a specified index.
	 * 
	 * @param xpathArr							An XDM array object instance
	 * @param indexVal							An array index value
	 * @param xctxt							    An XPath context object
	 * @return									An XDM value retrieved from the specified array index
	 * @throws TransformerException
	 */
    private XObject getXPathArrayResultByIndex(XPathArray xpathArr, XObject indexVal, XPathContext xctxt) 
    		                                                                                           throws TransformerException {
    	XObject evalResult;
    	
    	SourceLocator srcLocator = xctxt.getSAXLocator();
    	
    	String argValStr = XslTransformEvaluationHelper.getStrVal(indexVal);     				  

    	Integer intVal = null;     				  
    	try {
    		intVal = Integer.valueOf(argValStr);
    		if (!(intVal > 0 && (intVal <= xpathArr.size()))) {
	    			throw new javax.xml.transform.TransformerException("XPTY0004 : Function call syntax for array information lookup, "
							    					                                                           + "needs to have one numeric argument with value greater or equal to one "
							    					                                                           + "specifying an index value for an array.", srcLocator); 
    		}
    	}
    	catch (NumberFormatException ex) {
    		throw new javax.xml.transform.TransformerException("XPTY0004 : Function call syntax for array information lookup, "
								    				                                                           + "needs to have one numeric argument with value greater or equal to one "
								    				                                                           + "specifying an index value for an array.", srcLocator); 
    	}

    	evalResult = xpathArr.get(intVal - 1);
    	
    	return evalResult;
	}
    
    /**
     * Method definition to lookup an XDM map entry's value for the specified key.
     * 
     * @param xpathMap								An XDM map object instance
     * @param xpathKeyStr							An XPath expression string for map's key
     * @param prefixTable							List of values from XSL transformation prefix
     *                                              table.
     * @param xctxt                                 An XPath context object
     * @return										Map entry's value for the supplied key	
     * @throws TransformerException
     */
    private XObject getXPathMapEntryValueByKey(XPathMap xpathMap, String xpathKeyStr, 
    									       List<XMLNSDecl> prefixTable, XPathContext xctxt) throws TransformerException {

    	XObject evalResult = null;

    	SourceLocator srcLocator = xctxt.getSAXLocator(); 

    	final int contextNode = xctxt.getCurrentNode();

    	if (prefixTable != null) {
    		xpathKeyStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(xpathKeyStr, prefixTable);
    	}

    	XPath argXPath = new XPath(xpathKeyStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
    	if (m_vars != null) {
    		argXPath.fixupVariables(m_vars, m_globals_size);
    	}

    	XObject argValue = argXPath.execute(xctxt, contextNode, xctxt.getNamespaceContext());
    	
    	if (argValue instanceof XString) {
    		argValue = new XSString(((XString)argValue).str());
    		evalResult = xpathMap.get(argValue);     					 
    		if (evalResult == null) {
    			throw new javax.xml.transform.TransformerException("XPTY0004 : An XDM map doesn't have an entry with key name '" + 
																			    					XslTransformEvaluationHelper.getStrVal(argValue) + "'.",  
																			    					srcLocator); 
    		}
    	}
    	else if (argValue instanceof XSString) {
    		evalResult = xpathMap.get(argValue);     					 
    		if (evalResult == null) {
    			throw new javax.xml.transform.TransformerException("XPTY0004 : An XDM map doesn't have an entry with key name '" + 
																			    					XslTransformEvaluationHelper.getStrVal(argValue) + "'.",  
																			    					srcLocator); 
    		} 
    	}
    	else if (argValue instanceof ResultSequence) {
    		ResultSequence argSeq = (ResultSequence)argValue;
    		ResultSequence evalResultSeq = new ResultSequence(); 
    		for (int idx = 0; idx < argSeq.size(); idx++) {
    			XObject argSeqItem = argSeq.item(idx);
    			evalResultSeq.add(xpathMap.get(argSeqItem));
    		}

    		evalResult = evalResultSeq;
    	}
    	else if (argValue instanceof XMLNodeCursorImpl) {    	        		
    		if (m_isUnaryLookup) {
    			evalResult = xpathMap.get(new XSString(xpathKeyStr));
    		}
    		else {
    			ResultSequence argSeq = XslTransformEvaluationHelper.getResultSequenceFromXObject(argValue, xctxt);
        	    ResultSequence evalResultSeq = new ResultSequence(); 
        		for (int idx = 0; idx < argSeq.size(); idx++) {
        			XObject argSeqItem = argSeq.item(idx);
        			String strValue = XslTransformEvaluationHelper.getStrVal(argSeqItem);
        			evalResultSeq.add(xpathMap.get(new XSString(strValue)));
        		}

        		if (evalResultSeq.size() == 1) {
        		   evalResult = evalResultSeq.item(0);
        		}
        		else {
        		   evalResult = evalResultSeq;
        		}
    		}
    	}    	
    	else {
    		throw new javax.xml.transform.TransformerException("XPTY0004 : An XDM map lookup is not done via a "
    				                                                                              + "string valued key.",  srcLocator);
    	}

    	return evalResult;		  
    }
    
    /**
     * Method definition to check whether, an XPath dynamic function call
     * resolves to an XPath simple type constructor function call.
     */
    private boolean isFunctionRefXsSimpleTypeDefinition(XObject functionRef) {
		
    	boolean result = false;
		
		if ((functionRef instanceof ResultSequence) && (((ResultSequence)functionRef).size() == 1)) {
			XObject xObj = (((ResultSequence)functionRef)).item(0);
			if (xObj instanceof XSString) {
			   XSString xsString = (XSString)xObj;
			   if ((Expression.XS_SIMPLE_TYPE_NAME).equals(xsString.stringValue()) && (xsString.getXsTypeDefinition() != null)) {
				   result = true; 
			   }
			}
		}
		
		return result;
	}
    
    public String getFuncRefVarName() {
        return m_funcRefVarName;
    }

    public void setFuncRefVarName(String funcRefVarName) {
        this.m_funcRefVarName = funcRefVarName;
    }

    public List<String> getArgList() {
        return m_argList;
    }

    public void setArgList(List<String> argList) {
        this.m_argList = argList;
    }
    
    public void setIsFromUnaryLookupEvaluation(boolean isUnaryLookup) {
		m_isUnaryLookup = isUnaryLookup; 		
	}
	
	public boolean getIsFromUnaryLookupEvaluation() {
		return m_isUnaryLookup;
	}

	public void setTrailingArgList(List<String> strList) {
		m_trailingArgList = strList; 		
	}
	
	public List<String> getTrailingArgList() {
		return m_trailingArgList;
	}
	
	public void setChainedArgXPathStrArray(String[] strArray) {	
		m_xpathChainedArgListArr = strArray; 
	}
	
	public String[] getChainedArgXPathStrArray() {
		return m_xpathChainedArgListArr;
	}

}
