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

import javax.xml.transform.SourceLocator;

import org.apache.xalan.templates.ElemFunction;
import org.apache.xalan.templates.ElemTemplate;
import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xalan.templates.TemplateList;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xml.dtm.DTMManager;
import org.apache.xml.utils.QName;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionNode;
import org.apache.xpath.XPathContext;
import org.apache.xpath.axes.LocPathIterator;
import org.apache.xpath.composite.XPathForExpr;
import org.apache.xpath.composite.XPathSequenceConstructor;
import org.apache.xpath.functions.Function3Args;
import org.apache.xpath.functions.XSL3FunctionService;
import org.apache.xpath.functions.XSLFunctionBuilder;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.operations.Range;
import org.apache.xpath.operations.Variable;
import org.apache.xpath.patterns.NodeTest;

import xml.xpath31.processor.types.XSUntyped;
import xml.xpath31.processor.types.XSUntypedAtomic;

/**
 * This class provides few utility methods, to support XPath 3.1 
 * built-in higher order function evaluations. The language higher
 * order functions do one or both of following : accept functions as
 * arguments, or return function as function call result.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XPathHigherOrderBuiltinFunction extends Function3Args {
    
    private static final long serialVersionUID = 5970365027214826130L;
    
    private XSL3FunctionService m_xslFunctionService = XSLFunctionBuilder.getXSLFunctionService();

    /**
     * This method, evaluates an XPath expression to produce an xdm sequence, that
     * can be used as argument to an XPath higher order function call.
     *  
     * @param xpathExpr      an XPath expression, that is evaluated by this method
     * @param xctxt          an XPath context object
     * 
     * @return               an xdm sequence produced by this method.
     * 
     * @throws javax.xml.transform.TransformerException
     */
    protected ResultSequence constructSequenceFromXPathExpression(Expression xpathExpr, XPathContext xctxt) 
                                                                                       throws javax.xml.transform.TransformerException {        
        ResultSequence resultSeq = new ResultSequence();
        
        int contextNode = xctxt.getContextNode();

        if (xpathExpr instanceof Range) {
            resultSeq = (ResultSequence)(((Range)xpathExpr).execute(xctxt));    
        }
        else if (xpathExpr instanceof XPathSequenceConstructor) {
        	resultSeq = (ResultSequence)(((XPathSequenceConstructor)xpathExpr).execute(xctxt));
        }
        else if (xpathExpr instanceof Variable) {
            XObject xObj = ((Variable)xpathExpr).execute(xctxt);

            if (xObj instanceof XMLNodeCursorImpl) {               
                DTMManager dtmMgr = (DTMManager)xctxt;

                XMLNodeCursorImpl xNodeSet = (XMLNodeCursorImpl)xObj;           
                DTMCursorIterator sourceNodes = xNodeSet.iter();

                int nextNodeDtmHandle;

                while ((nextNodeDtmHandle = sourceNodes.nextNode()) != DTM.NULL) {
                    XMLNodeCursorImpl xNodeSetItem = new XMLNodeCursorImpl(nextNodeDtmHandle, dtmMgr);
                    String nodeStrValue = xNodeSetItem.str();

                    DTM dtm = dtmMgr.getDTM(nextNodeDtmHandle);

                    if (dtm.getNodeType(nextNodeDtmHandle) == DTM.ELEMENT_NODE) {
                        XSUntyped xsUntyped = new XSUntyped(nodeStrValue);                 
                        XslTransformEvaluationHelper.addItemToResultSequence(resultSeq, 
                                                                                  xsUntyped, true);
                    }
                    else if (dtm.getNodeType(nextNodeDtmHandle) == DTM.ATTRIBUTE_NODE) {
                        XSUntypedAtomic xsUntypedAtomic = new XSUntypedAtomic(nodeStrValue);
                        XslTransformEvaluationHelper.addItemToResultSequence(resultSeq, 
                                                                                  xsUntypedAtomic, true);
                    }
                    else {
                        XSUntypedAtomic xsUntypedAtomic = new XSUntypedAtomic(nodeStrValue);
                        XslTransformEvaluationHelper.addItemToResultSequence(resultSeq, 
                                                                                  xsUntypedAtomic, true);
                    }
                }       
            }
            else if (xObj instanceof ResultSequence) {
                resultSeq = (ResultSequence)xObj; 
            }
            else {
                resultSeq.add(xObj);
            }
        }
        else if (xpathExpr instanceof LocPathIterator) {            
            DTMManager dtmMgr = (DTMManager)xctxt;        
            DTMCursorIterator arg0DtmIterator = xpathExpr.asIterator(xctxt, contextNode);        

            int nextNodeDtmHandle;

            while ((nextNodeDtmHandle = arg0DtmIterator.nextNode()) != DTM.NULL) {
                XMLNodeCursorImpl xNodeSetItem = new XMLNodeCursorImpl(nextNodeDtmHandle, dtmMgr);            
                String nodeStrValue = xNodeSetItem.str();

                DTM dtm = dtmMgr.getDTM(nextNodeDtmHandle);

                if (dtm.getNodeType(nextNodeDtmHandle) == DTM.ELEMENT_NODE) {
                    XSUntyped xsUntyped = new XSUntyped(nodeStrValue);
                    resultSeq.add(xsUntyped);
                }
                else if (dtm.getNodeType(nextNodeDtmHandle) == DTM.ATTRIBUTE_NODE) {
                    XSUntypedAtomic xsUntypedAtomic = new XSUntypedAtomic(nodeStrValue);
                    resultSeq.add(xsUntypedAtomic);
                }
                else {
                    XSUntypedAtomic xsUntypedAtomic = new XSUntypedAtomic(nodeStrValue);
                    resultSeq.add(xsUntypedAtomic);
                }                        
            }
        }
        else if (xpathExpr instanceof XPathForExpr) { 
           XObject xObj = ((XPathForExpr)xpathExpr).execute(xctxt);
           resultSeq = (ResultSequence)xObj;
        }

        return resultSeq;
   }
   
    /**
     * Function definition to get xsl:function's compiled object ElemFunction, 
     * given a NodeTest expression.  
     * 
     * @param nodeTest							An object instance for example, constructed from XPath 
     *                                          named function references like fn0:abc#1.
     * @param transformerImpl					An XSL transform TransformerImpl object
     * @param srcLocator						SourceLocator object in XPath context
     * @return									An ElemFunction object if available, otherwise null
     * 
     * @throws javax.xml.transform.TransformerException
     */
   protected ElemFunction getElemFunctionFromNodeTestExpression(NodeTest nodeTest, TransformerImpl transformerImpl, 
		                                                        SourceLocator srcLocator) throws javax.xml.transform.TransformerException {

	   ElemFunction result = null;
	   
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
    	   if (m_xslFunctionService.isFuncArityWellFormed(funcNameRef)) {        	   
    		   int hashCharIdx = funcNameRef.indexOf('#');
    		   String funcNameRef2 = funcNameRef.substring(0, hashCharIdx);
    		   int funcArity = Integer.valueOf(funcNameRef.substring(hashCharIdx + 1));        		   
    		   ElemTemplate elemTemplate = templateList.getXslFunction(new QName(funcNamespace, funcNameRef2), funcArity);        		   
    		   if (elemTemplate != null) {
    			   result = (ElemFunction)elemTemplate;
    			   int xslFuncDefnParamCount = result.getParamCount();                      
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
    	
       return result;
   }
   
   /**
    * Function definition to get an XSL TransformerImpl instance, given 
    * a supplied XPath compiled expression. 
    * 
    * @param expr					An XPath compiled expression
    * @return                       TransformerImpl object instance
    */
   protected TransformerImpl getTransformerImplFromXPathExpression(Expression expr) {
	   
	   TransformerImpl transformerImpl = null;

	   ExpressionNode expressionNode = expr.getExpressionOwner();
	   ExpressionNode stylesheetRootNode = null;
	   while (expressionNode != null) {
		   stylesheetRootNode = expressionNode;
		   expressionNode = expressionNode.exprGetParent();                     
	   }

	   StylesheetRoot stylesheetRoot = (StylesheetRoot)stylesheetRootNode;
	   if (stylesheetRoot != null) {
		   transformerImpl = stylesheetRoot.getTransformerImpl();
	   }

	   return transformerImpl; 
   }

}
