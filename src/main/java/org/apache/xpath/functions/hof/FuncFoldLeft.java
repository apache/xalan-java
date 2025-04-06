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
package org.apache.xpath.functions.hof;

import java.util.List;
import java.util.Map;

import javax.xml.transform.SourceLocator;

import org.apache.xalan.templates.ElemFunction;
import org.apache.xalan.templates.ElemTemplate;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xalan.templates.TemplateList;
import org.apache.xalan.templates.XMLNSDecl;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.utils.QName;
import org.apache.xpath.ExpressionNode;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.XSL3FunctionService;
import org.apache.xpath.functions.XSLFunctionBuilder;
import org.apache.xpath.objects.InlineFunctionParameter;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathInlineFunction;
import org.apache.xpath.operations.Variable;
import org.apache.xpath.patterns.NodeTest;

/**
 * Implementation of XPath 3.1 function fn:fold-left.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncFoldLeft extends XPathHigherOrderBuiltinFunction {
    
    private static final long serialVersionUID = -3772850377799360556L;
    
    private static XSL3FunctionService xslFunctionService = XSLFunctionBuilder.getXSLFunctionService(); 

    public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
    {
        XObject evalResult = new ResultSequence();
        
        SourceLocator srcLocator = xctxt.getSAXLocator();
        
        int contextNode = xctxt.getContextNode();
        
        ResultSequence foldLeftFirstArgSeq = constructSequenceFromXPathExpression(m_arg0, xctxt);
        
        XObject foldLeftBaseVal = m_arg1.execute(xctxt);
        
        XPathInlineFunction foldLeftInlineFuncArg = null;
        
        ElemFunction elemFunction = null;        
        TransformerImpl transformerImpl = null;
        
        if (m_arg2 instanceof Variable) {
           XObject arg2XObj = m_arg2.execute(xctxt);
           if (arg2XObj instanceof XPathInlineFunction) {
              foldLeftInlineFuncArg = (XPathInlineFunction)arg2XObj;
           }
           else {
               QName varQname = (((Variable)m_arg2).getElemVariable()).getName();
               throw new javax.xml.transform.TransformerException("FORG0006 : The third argument to function call "
                                                                        + "fn:fold-left is a variable reference '" + 
                                                                           varQname.getLocalName() + "', that cannot be "
                                                                        + "evaluated to a function item.", srcLocator);  
           }
        }        
        else if (m_arg2 instanceof XPathInlineFunction) {
           foldLeftInlineFuncArg = (XPathInlineFunction)m_arg2;                                           
        }
        else if (m_arg2 instanceof NodeTest) {
           NodeTest nodeTest = (NodeTest)m_arg2;
           String funcNameRef = nodeTest.getLocalName();
           String funcNamespace = nodeTest.getNamespace();
           
           ExpressionNode expressionNode = m_arg2.getExpressionOwner();
           ExpressionNode stylesheetRootNode = null;
           while (expressionNode != null) {
        	   stylesheetRootNode = expressionNode;
        	   expressionNode = expressionNode.exprGetParent();                     
           }

           StylesheetRoot stylesheetRoot = (StylesheetRoot)stylesheetRootNode;
           
           if (stylesheetRoot != null) {
        	   transformerImpl = stylesheetRoot.getTransformerImpl();  
        	   TemplateList templateList = stylesheetRoot.getTemplateListComposed();        	   
        	   if (xslFunctionService.isFuncArityWellFormed(funcNameRef)) {        	   
        		   int hashCharIdx = funcNameRef.indexOf('#');
        		   String funcNameRef2 = funcNameRef.substring(0, hashCharIdx);
        		   ElemTemplate elemTemplate = templateList.getTemplate(new QName(funcNamespace, funcNameRef2));
        		   if ((elemTemplate != null) && (elemTemplate instanceof ElemFunction)) {
        			   elemFunction = (ElemFunction)elemTemplate;
        			   int xslFuncDefnParamCount = elemFunction.getParamCount();                      
        			   String str = funcNameRef.substring(hashCharIdx + 1);
        			   int funcRefParamCount = (Integer.valueOf(str)).intValue();
        			   if (funcRefParamCount != xslFuncDefnParamCount) {
        				   throw new javax.xml.transform.TransformerException("FORG0006 : An XSL named function reference " + funcNameRef 
        						   																			+ " cannot resolve to a function "
        						   																			+ "definition.", srcLocator); 
        			   }
        		   }
        	   }
        	   else {
        		   throw new javax.xml.transform.TransformerException("FORG0006 : An XSL named function reference " + funcNameRef 
																											+ " cannot resolve to a function "
																											+ "definition.", srcLocator);
        	   }
           }           
        }
        else {
           throw new javax.xml.transform.TransformerException("FORG0006 : The third argument to function call "
                                                                                 						+ "fn:fold-left is not an XPath function reference.", srcLocator);
        }
        
        if (foldLeftInlineFuncArg != null) {
        	String inlineFnXPathStr = foldLeftInlineFuncArg.getFuncBodyXPathExprStr();
        	List<InlineFunctionParameter> funcParamList = foldLeftInlineFuncArg.getFuncParamList();

        	if (funcParamList.size() == 2) {              
        		String funcItemFirstArgName = (funcParamList.get(0)).getParamName();
        		String funcItemSecondArgName = (funcParamList.get(1)).getParamName();

        		ElemTemplateElement elemTemplateElement = (ElemTemplateElement)xctxt.getNamespaceContext();
        		List<XMLNSDecl> prefixTable = null;
        		if (elemTemplateElement != null) {
        			prefixTable = (List<XMLNSDecl>)elemTemplateElement.getPrefixTable();
        		}

        		if (prefixTable != null) {
        			inlineFnXPathStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(
        																								inlineFnXPathStr, prefixTable);
        		}

        		XPath inlineFuncXPath = new XPath(inlineFnXPathStr, srcLocator, xctxt.getNamespaceContext(), 
        																									XPath.SELECT, null);              
        		for (int idx = 0; idx < foldLeftFirstArgSeq.size(); idx++) {
        			Map<QName, XObject> inlineFunctionVarMap = xctxt.getXPathVarMap();

        			inlineFunctionVarMap.put(new QName(funcItemSecondArgName), foldLeftFirstArgSeq.item(idx));

        			if (idx == 0) {                    
        				inlineFunctionVarMap.put(new QName(funcItemFirstArgName), foldLeftBaseVal);
        			}
        			else {
        				inlineFunctionVarMap.put(new QName(funcItemFirstArgName), evalResult);                   
        			}

        			evalResult = inlineFuncXPath.execute(xctxt, contextNode, xctxt.getNamespaceContext());

        			// Reset the function item argument variables
        			inlineFunctionVarMap.put(new QName(funcItemFirstArgName), null);
        			inlineFunctionVarMap.put(new QName(funcItemSecondArgName), null);
        		}
        	}
        	else {
        		throw new javax.xml.transform.TransformerException("XPTY0004 : An inline function definition argument to "
																					        				+ "function fn:fold-left has " + funcParamList.size() + " "
																					        				+ "parameters. Expected 2.", srcLocator); 
        	}
        }
        else if ((elemFunction != null) && (transformerImpl != null)) {        						
        	for (int idx = 0; idx < foldLeftFirstArgSeq.size(); idx++) {
        		ResultSequence argSequence = new ResultSequence();

        		argSequence.add(foldLeftFirstArgSeq.item(idx));
        		if (idx == 0) {                        				    				    				
        			argSequence.add(foldLeftBaseVal);
        		}
        		else {
        			argSequence.add(evalResult);    				                   
        		}

        		evalResult = elemFunction.evaluateXslFunction(transformerImpl, argSequence);
        	}			
        }
        
        return evalResult;
    }

}
