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

import java.util.HashMap;
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
import org.apache.xml.utils.QName;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionNode;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.composite.SequenceTypeData;
import org.apache.xpath.composite.SequenceTypeSupport;
import org.apache.xpath.objects.InlineFunctionParameter;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathArray;
import org.apache.xpath.objects.XPathInlineFunction;
import org.apache.xpath.objects.XPathMap;
import org.apache.xpath.objects.XString;

import xml.xpath31.processor.types.XSString;

/**
 * This class implements XPath 3.1 dynamic function calls and,
 * map/array information lookup using function call and unary 
 * lookup syntax.
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
    
    private boolean m_IsUnaryLookup;
    
    private List<String> m_argList;
    
    // The following two fields of this class, are used during 
    // XPath.fixupVariables(..) action as performed within object of 
    // this class.    
    private Vector m_vars;    
    private int m_globals_size;

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

    @Override
    public void callVisitors(ExpressionOwner owner, XPathVisitor visitor) {
        // no op
    }

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
              // Try to get an XPath inline function reference, from within 
              // stylesheet's global scope. 
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
       
       if (functionRef != null) {
    	    ElemTemplateElement elemTemplateElement = (ElemTemplateElement)xctxt.getNamespaceContext();
            
    	    List<XMLNSDecl> prefixTable = null;
            if (elemTemplateElement != null) {
               prefixTable = (List<XMLNSDecl>)elemTemplateElement.getPrefixTable();
            }
                	    
    	    if (functionRef instanceof XPathInlineFunction) {
	           XPathInlineFunction inlineFunction = (XPathInlineFunction)functionRef;
	           
	           String inlineFnXPathStr = inlineFunction.getFuncBodyXPathExprStr();
	           List<InlineFunctionParameter> funcParamList = inlineFunction.getFuncParamList();           
	           
	           if (m_argList.size() != funcParamList.size()) {
	               throw new javax.xml.transform.TransformerException("XPTY0004 : Number of arguments required for "
	                                                                                  + "dynamic call to function is " + funcParamList.size() + ". "
	                                                                                  + "Number of arguments provided " + m_argList.size() + ".", xctxt.getSAXLocator());    
	           }	           	           
	           
	           Map<QName, XObject> functionParamAndArgMap = new HashMap<QName, XObject>();
	           
	           for (int idx = 0; idx < funcParamList.size(); idx++) {
	              InlineFunctionParameter funcParam = funcParamList.get(idx);                                                         
	              
	              String argXPathStr = m_argList.get(idx);
	              
	              if (prefixTable != null) {
	                  argXPathStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(argXPathStr, 
	                                                                                                      prefixTable);
	              }
	              
	              XPath argXPath = new XPath(argXPathStr, srcLocator, xctxt.getNamespaceContext(), 
	                                                                                        XPath.SELECT, null);
	              if (m_vars != null) {
	                 argXPath.fixupVariables(m_vars, m_globals_size);
	              }
	              
	              XObject argValue = argXPath.execute(xctxt, contextNode, xctxt.getNamespaceContext());
	              
	              String funcParamName = funcParam.getParamName();
	              SequenceTypeData paramType = funcParam.getParamType();
	              
	              if (paramType != null) {
	                  try {
	                     argValue = SequenceTypeSupport.castXdmValueToAnotherType(argValue, null, paramType, null);                     
	                     if (argValue == null) {
	                        throw new TransformerException("XTTE0505 : The item type of argument at position " + (idx + 1) + " of dynamic function call "
	                                                                                                           + "$" + m_funcRefVarName + ", doesn't match "
	                                                                                                           + "an expected type.", srcLocator);  
	                     }
	                  }
	                  catch (TransformerException ex) {
	                     throw new TransformerException("XTTE0505 : The item type of argument at position " + (idx + 1) + " of dynamic function call "
	                                                                                                        + "$" + m_funcRefVarName + ", doesn't match "
	                                                                                                        + "an expected type.", srcLocator); 
	                  }
	              }
	              
	              m_xpathVarList.add(new QName(funcParamName));
	              
	              functionParamAndArgMap.put(new QName(funcParamName), argValue);
	           }
	           
	           inlineFunctionVarMap.putAll(functionParamAndArgMap);
	           
	           if (prefixTable != null) {
	              inlineFnXPathStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(inlineFnXPathStr, 
	                                                                                                           prefixTable);
	           }
	           
	           XPath inlineFnXPath = new XPath(inlineFnXPathStr, srcLocator, xctxt.getNamespaceContext(), 
	                                                                                       XPath.SELECT, null);
	           if (m_vars != null) {
	              inlineFnXPath.fixupVariables(m_vars, m_globals_size);
	           }
	                      
	           evalResult = inlineFnXPath.execute(xctxt, contextNode, xctxt.getNamespaceContext());
	           
	           SequenceTypeData funcReturnType = inlineFunction.getReturnType();
	           if (funcReturnType != null) {
	              try {
	                 evalResult = SequenceTypeSupport.castXdmValueToAnotherType(evalResult, null, funcReturnType, null);
	                 if (evalResult == null) {
	                    throw new TransformerException("XTTE0505 : The item type of result of dynamic function call $"+ m_funcRefVarName + ", doesn't match an "
	                                                                                                                                   + "expected type.", srcLocator);  
	                 }
	              }
	              catch (TransformerException ex) {
	                  throw new TransformerException("XTTE0505 : The item type of result of dynamic function call $"+ m_funcRefVarName + ", doesn't match an "
	                                                                                                                                 + "expected type.", srcLocator);  
	              }
	           }
	           
	           Set<QName> keysOfArgVariables = functionParamAndArgMap.keySet();
	           Iterator<QName> iter = keysOfArgVariables.iterator();
	           while (iter.hasNext()) {
	        	  QName key = iter.next();
	        	  inlineFunctionVarMap.remove(key);
	           }
	        }
    	    else if (functionRef instanceof XPathMap) {
     		   XPathMap xpathMap = (XPathMap)functionRef;
     		   if (m_argList.size() != 1) {
     			   throw new javax.xml.transform.TransformerException("XPTY0004 : Function call syntax for map information lookup, needs to have "
     			   		                                                     + "1 argument which should be one of map's key name.", 
     			   		                                                     xctxt.getSAXLocator()); 
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
     				  if (prefixTable != null) {
     					  argXPathStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(argXPathStr, 
     							  prefixTable);
     				  }

     				  XPath argXPath = new XPath(argXPathStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
     				  if (m_vars != null) {
     					  argXPath.fixupVariables(m_vars, m_globals_size);
     				  }

     				  XObject argValue = argXPath.execute(xctxt, contextNode, xctxt.getNamespaceContext());
     				  if (argValue instanceof XString) {
     					  argValue = new XSString(((XString)argValue).str());
     					  evalResult = xpathMap.get(argValue);     					 
     					  if (evalResult == null) {
     						  throw new javax.xml.transform.TransformerException("XPTY0004 : An XDM map doesn't have an entry with key name '" + 
     								                                                              XslTransformEvaluationHelper.getStrVal(argValue) + "'.",  xctxt.getSAXLocator()); 
     					  }
     				  }
     				  else if (argValue instanceof XSString) {
     					  evalResult = xpathMap.get(argValue);     					 
     					  if (evalResult == null) {
     						  throw new javax.xml.transform.TransformerException("XPTY0004 : An XDM map doesn't have an entry with key name '" + 
     								                                                             XslTransformEvaluationHelper.getStrVal(argValue) + "'.",  xctxt.getSAXLocator()); 
     					  } 
     				  }
     				  else if (argValue instanceof ResultSequence) {
     					 ResultSequence rSeqArg = (ResultSequence)argValue;
     					 ResultSequence rSeqAnswer = new ResultSequence(); 
     					 for (int idx = 0; idx < rSeqArg.size(); idx++) {
     						XObject oneArgValue = rSeqArg.item(idx);
     						rSeqAnswer.add(xpathMap.get(oneArgValue));
     					 }
     					 
     					 evalResult = rSeqAnswer;
     				  }
     				  else if (m_IsUnaryLookup && (argValue instanceof XMLNodeCursorImpl)) {
     					 evalResult = xpathMap.get(new XSString(argXPathStr));
     				  }
     				  else {
     					 throw new javax.xml.transform.TransformerException("XPTY0004 : An XDM map lookup is not done via a "
     							                                                                + "string valued key.",  xctxt.getSAXLocator());
     				  }
     			  }
     		   }    		   
     	    }
    	    else if (functionRef instanceof XPathArray) {
    	       XPathArray xpathArr = (XPathArray)functionRef;    	       
    	       if (m_argList.size() != 1) {
    	    	   throw new javax.xml.transform.TransformerException("XPTY0004 : Function call syntax for array information lookup, needs to have "
                                                                             + "1 argument which should be position within an array.", 
                                                                             xctxt.getSAXLocator()); 
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
     				  evalResult = getArrayLookupResult(xctxt, xpathArr, contextItem);
     			  }
     			  else {
	     			  if (prefixTable != null) {
	  	                 argXPathStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(argXPathStr, 
	  	                                                                                                      prefixTable);
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
	                     evalResult = getArrayLookupResult(xctxt, xpathArr, argValue);
	                  }
     			  }
     		   }
    	    }
    	    else {
    	       Object obj1 = functionRef.object();
    	       if (obj1 instanceof Function) {
    	    	  Function function = (Function)obj1;    	    	  
    	    	  
    	    	  String funcNamespace = function.getNamespace();
 	    		  String funcLocalName = function.getLocalName();
 	    		  int funcArity = function.getFuncArity();
 	    		  
 	    		  String expandedFuncName = "{" + funcNamespace + ":" + funcLocalName + "}#" + funcArity;
 	    		  
    	    	  if (m_argList.size() == funcArity) {    	    		     	    		 
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
    	    		 		                                                                     + "incorrect. Required " + funcArity + ", supplied " 
    	    		 		                                                                     + m_argList.size() + ".", srcLocator); 
    	    	  }
    	       }
    	       else if (obj1 instanceof ElemFunction) {
    	    	  ElemFunction elemFunction = (ElemFunction)obj1;    	    	      	    	  
    	    	  int funcArity = elemFunction.getParamCount();    	    	  
    	    	  if (m_argList.size() == funcArity) {
    	    		  StylesheetRoot stylesheetRoot = functionRef.getXslStylesheetRoot();
    	    		  TransformerImpl transformerImpl = stylesheetRoot.getTransformerImpl();
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
    	    	  else {
    	    		  QName funcName = elemFunction.getName();
    	    		  throw new javax.xml.transform.TransformerException("XPTY0004 : The number of arguments provided for "
																	                              + "stylesheet function call " + funcName.toString() + " is "
																	                              + "incorrect. Required " + funcArity + ", supplied " 
																	                              + m_argList.size() + ".", srcLocator);  
    	    	  }
    	       }
    	    }
      }
      else {
         throw new javax.xml.transform.TransformerException("XPST0008 : Variable '" + m_funcRefVarName + "' has "
                                                                                                       + "not been declared, or its declaration is not in scope.", 
                                                                                                                                              xctxt.getSAXLocator());    
      }
               
      return evalResult;
    }

    @Override
    public void fixupVariables(Vector vars, int globalsSize) {
        m_vars = (Vector)(vars.clone());
        m_globals_size = globalsSize; 
    }

    @Override
    public boolean deepEquals(Expression expr) {
        return false;
    }
    
    /**
     * Given an xdm array object, get array item at a specified index position.
     */
    private XObject getArrayLookupResult(XPathContext xctxt, XPathArray xpathArr, XObject indexVal)
			                                                                                      throws TransformerException {
    	XObject evalResult;
    	
    	String argValStr = XslTransformEvaluationHelper.getStrVal(indexVal);     				  

    	Integer intVal = null;     				  
    	try {
    		intVal = Integer.valueOf(argValStr);
    		if (!(intVal > 0 && (intVal <= xpathArr.size()))) {
    			throw new javax.xml.transform.TransformerException("XPTY0004 : Function call syntax for array information lookup, "
    					                                                  + "needs to have 1 numeric argument >= 1 specifying position "
    					                                                  + "within an array.", xctxt.getSAXLocator()); 
    		}
    	}
    	catch (NumberFormatException ex) {
    		throw new javax.xml.transform.TransformerException("XPTY0004 : Function call syntax for array information lookup, "
    				                                                  + "needs to have 1 numeric argument >= 1 specifying position "
    				                                                  + "within an array.", xctxt.getSAXLocator()); 
    	}

    	evalResult = xpathArr.get(intVal - 1);
    	
    	return evalResult;
	}

	public void setIsFromUnaryLookupEvaluation(boolean isUnaryLookup) {
		m_IsUnaryLookup = isUnaryLookup; 		
	}
	
	public boolean getIsFromUnaryLookupEvaluation() {
		return m_IsUnaryLookup;
	}

}
