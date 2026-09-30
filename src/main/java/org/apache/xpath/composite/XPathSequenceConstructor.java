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
package org.apache.xpath.composite;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.templates.XMLNSDecl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xml.dtm.DTMManager;
import org.apache.xml.utils.QName;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.axes.LocPathIterator;
import org.apache.xpath.compiler.Keywords;
import org.apache.xpath.functions.XSL3ConstructorOrExtensionFunction;
import org.apache.xpath.functions.XSL3FunctionService;
import org.apache.xpath.functions.XSLFunctionBuilder;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XBooleanStatic;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import xml.xpath31.processor.types.XSAnyAtomicType;
import xml.xpath31.processor.types.XSBoolean;
import xml.xpath31.processor.types.XSNumericType;
import xml.xpath31.processor.types.XSString;

/**
 * Class definition, to implement XPath 3.1 'literal sequence', 
 * constructor expression.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XPathSequenceConstructor extends Expression {

    private static final long serialVersionUID = -5141131877741250613L;
    
    /**
     * List containing, XPath expression strings for the sequence constructor.
     */
    private List<String> m_sequenceConstructorXPathParts = new ArrayList<String>();
    
    /**
     * An optional XPath expression string, to represent a 
     * predicate following an XPath literal sequence constructor.
     * 
     * For e.g, within an XPath expression, (a, b)[p] this is p.
     */
    private String m_xpathPredicateStr = null;
    
    private String m_xpathPrefixStr = null;
    
    /**
     * An optional XPath expression string, to represent a path 
     * suffix following an XPath literal sequence constructor.
     * 
     * For e.g, within an XPath expression, (a, b)/m/n this is m/n.
     */
    private String m_xpathSuffixStr = null;
    
    /**
     * An XPath 3.1 literal sequence constructor, may be following by an
     * XPath operator with, subsequence XPath operand. For e.g,
     * ('a b c', 'd a')!tokenize(.,' ') => distinct-values() 
     */
    private String m_xpath_op = null;
    
    /**
     * This class field is used during, XPath.fixupVariables(..) action 
     * as performed within object of this class.  
     */    
    private Vector m_vars;
    
    /**
     * This class field is used during, XPath.fixupVariables(..) action 
     * as performed within object of this class.  
     */
    private int m_globals_size;
    
    private XSL3FunctionService m_xsl3FunctionService = XSLFunctionBuilder.getXSLFunctionService();
    
    // List used to check inf recursion error
    // REVISIT
    private static List<String> m_list_temp1 = new ArrayList<String>();
    

    @Override
    public XObject execute(XPathContext xctxt) throws TransformerException {
        
    	XObject result = null;
        
        SourceLocator srcLocator = xctxt.getSAXLocator();
        
        int currentNode = xctxt.getContextNode();        
        
        List<XMLNSDecl> prefixTable = XslTransformEvaluationHelper.getXSLNsPrefixTable(xctxt);        
        
        /**
         * XPath expression evaluation, within the list 'm_sequenceConstructorXPathParts', 
         * and concatenating xdm sequences resulting from each of them, to get final result
         * for an XPath sequence constructor evaluation,
         * 
         * This XPath evaluation result, is further processed by optional prefix, predicate,
         * suffix information, if they're available. 
         */
        
        ResultSequence resultSeq = new ResultSequence();
                
        /**
         * Count of the, XPath 3.1 sequence constructor, XPath expression parts.
         * The computed xdm sequence as, returned by this method, may contain
         * number of xdm items equal or more than this integer value.
         */
        int xpathSeqConsPartCount = m_sequenceConstructorXPathParts.size();
        
        boolean isSourceKind1 = false;
        boolean isSourceKind2 = false;
        
        for (int idx = 0; idx < xpathSeqConsPartCount; idx++) {
           String xpathExprStr = m_sequenceConstructorXPathParts.get(idx);
           
           if (prefixTable != null) {
              xpathExprStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(xpathExprStr, 
                                                                                                     prefixTable);
           }
           
           XPath xpathObj = null;
           
           try {
              xpathObj = new XPath(xpathExprStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
           }
           catch (TransformerException ex) {        	  
        	  if (m_list_temp1.contains(xpathExprStr)) {
        		 throw ex;  
        	  }
        	  
        	  m_list_temp1.add(xpathExprStr);
        	   
        	  if (!xpathExprStr.startsWith("(") && !xpathExprStr.endsWith(")")) {        		         		 
        	     xpathObj = new XPath("(" + xpathExprStr + ")", srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
        	  }
        	  
        	  if (xpathObj == null) {
        		 xpathObj = new XPath("(" + xpathExprStr + ")", srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
        	  }
           }
           
           if (m_vars != null) {
              xpathObj.fixupVariables(m_vars, m_globals_size);
           }
           
           boolean isXpathCtxtActive = false;
           
           try {
        	   if (currentNode == DTM.NULL) {
        		   String[] strArr1 = xpathExprStr.split("/");
        		   
        		   if ((strArr1.length > 0) && strArr1[0].startsWith("$")) {
        			   String varName = (strArr1[0]).substring(1);
        			   varName = varName.trim();
        			   
        			   Map<QName, XObject> map1 = xctxt.getXPathVarMap();
        			   XObject xObj = map1.get(new QName(varName));
        			   
        			   if (xObj instanceof XMLNodeCursorImpl) {
        				   currentNode = ((XMLNodeCursorImpl)xObj).asNode(xctxt);            			  
        				   xctxt.pushCurrentNode(currentNode);

        				   isXpathCtxtActive = true;
        			   }
        		   }
        	   }

        	   Expression xpathExpr = xpathObj.getExpression();        	           	   

        	   if (xpathExpr instanceof LocPathIterator) {        		           		           		   
        		   isSourceKind1 = true;
        		   LocPathIterator locPathIterator = (LocPathIterator)xpathExpr;                              

        		   DTMCursorIterator dtmIter = null;                     
        		   
        		   try {
        			   dtmIter = locPathIterator.asIterator(xctxt, currentNode);
        		   }
        		   catch (ClassCastException ex) {
        			   // No op
        		   }

        		   if (dtmIter != null) {        			           			   
        			   boolean isEmptyNodeSet = true;
        			   
        			   int nextNode = DTM.NULL;
        			   
        			   while ((nextNode = dtmIter.nextNode()) != DTM.NULL)
        			   {
        				   isEmptyNodeSet = false;
        				   XMLNodeCursorImpl xNodeSetItem = new XMLNodeCursorImpl(nextNode, xctxt);
        				   resultSeq.add(xNodeSetItem);                                            
        			   }

        			   if (isEmptyNodeSet) {
        				   // Possible XML namespace string handling 
        				   xpathExprStr = xpathExprStr.replace(" : ", ":");
        				   
        				   if (prefixTable != null) {
    						   xpathExprStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(xpathExprStr, prefixTable);
    					   }

    					   XPath xpathObj2 = new XPath(xpathExprStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
    					   
    					   if (m_vars != null) {
    						   xpathObj2.fixupVariables(m_vars, m_globals_size);
    					   }

    					   Expression xpathExpr2 = xpathObj2.getExpression();        				      					   
    					   
    					   if (xpathExpr2 instanceof XPathNamedFunctionReference) {
    						  resultSeq.add((XPathNamedFunctionReference)xpathExpr2); 
    					   }
        			   }
        		   }
        		   else if (xpathExprStr.startsWith("$") && xpathExprStr.contains("[") && xpathExprStr.endsWith("]")) {
        			   String varRefXPathExprStr = "$" + xpathExprStr.substring(1, xpathExprStr.indexOf('['));
        			   String xpathIndexExprStr = xpathExprStr.substring(xpathExprStr.indexOf('[') + 1, xpathExprStr.indexOf(']'));

        			   // Evaluate the, variable reference XPath expression
        			   if (prefixTable != null) {
        				   varRefXPathExprStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(
        						   																			varRefXPathExprStr, prefixTable);
        			   }

        			   XPath varXPathObj = new XPath(varRefXPathExprStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
        			   
        			   if (m_vars != null) {
        				   varXPathObj.fixupVariables(m_vars, m_globals_size);
        			   }

        			   XObject varEvalResult = varXPathObj.execute(xctxt, xctxt.getCurrentNode(), xctxt.getNamespaceContext());

        			   // Evaluate the, xdm sequence index XPath expression
        			   if (prefixTable != null) {
        				   xpathIndexExprStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(
																			        						   xpathIndexExprStr, 
																			        						   prefixTable);
        			   }

        			   XPath xpathIndexObj = new XPath(xpathIndexExprStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
        			   
        			   if (m_vars != null) {
        				   xpathIndexObj.fixupVariables(m_vars, m_globals_size);
        			   }

        			   XObject seqIndexEvalResult = xpathIndexObj.execute(xctxt, xctxt.getCurrentNode(), xctxt.getNamespaceContext());

        			   if (varEvalResult instanceof ResultSequence) {
        				   ResultSequence varEvalResultSeq = (ResultSequence)varEvalResult; 

        				   if (seqIndexEvalResult instanceof XNumber) {
        					   double dValIndex = ((XNumber)seqIndexEvalResult).num();
        					   
        					   if (dValIndex == (int)dValIndex) {
        						   XObject evalResult = varEvalResultSeq.item((int)dValIndex - 1);
        						   resultSeq.add(evalResult);
        					   }
        					   else {
        						   throw new javax.xml.transform.TransformerException("XPTY0004 : An index value used with a sequence reference, is not an integer.", srcLocator);  
        					   }
        				   }
        				   else if (seqIndexEvalResult instanceof XSNumericType) {
        					   String indexStrVal = ((XSNumericType)seqIndexEvalResult).stringValue();
        					   double dValIndex = (Double.valueOf(indexStrVal)).doubleValue();
        					   
        					   if (dValIndex == (int)dValIndex) {
        						   XObject evalResult = varEvalResultSeq.item((int)dValIndex - 1);
        						   resultSeq.add(evalResult);
        					   }
        					   else {
        						   throw new javax.xml.transform.TransformerException("XPTY0004 : An index value used with a sequence reference, is not an integer.", srcLocator);  
        					   }
        				   }
        				   else {
        					   throw new javax.xml.transform.TransformerException("XPTY0004 : An index value used with a sequence reference, is not numeric.", srcLocator);  
        				   }
        			   }
        		   }
        	   }
        	   else if (xpathExpr instanceof XSL3ConstructorOrExtensionFunction) {
        		   XSL3ConstructorOrExtensionFunction xsl3ConstructorOrExtensionFunction = (XSL3ConstructorOrExtensionFunction)xpathExpr;        	           	   
        		   XObject funcEvalResult = xsl3ConstructorOrExtensionFunction.execute(xctxt);        	           	           	          	   
        		   
        		   resultSeq.add(funcEvalResult);
        	   }
        	   else if (xpathExpr instanceof XPathNamedFunctionReference) {
        		   resultSeq.add((XPathNamedFunctionReference)xpathExpr);
        	   }
        	   else {        		   
        		   if (xpathExpr instanceof XPathForExpr) {
        			   isSourceKind2 = true; 
        		   }

        		   XObject xPathExprPartResult = xpathObj.execute(xctxt, currentNode, xctxt.getNamespaceContext());                              

        		   if (xPathExprPartResult instanceof XMLNodeCursorImpl) {
        			   DTMManager dtmMgr = (DTMManager)xctxt;

        			   XMLNodeCursorImpl xNodeSet = (XMLNodeCursorImpl)xPathExprPartResult;
        			   DTMCursorIterator sourceNodes = xNodeSet.iter();

        			   int nextNode = DTM.NULL;

        			   while ((nextNode = sourceNodes.nextNode()) != DTM.NULL) {
        				   XMLNodeCursorImpl xNodeSetItem = new XMLNodeCursorImpl(nextNode, dtmMgr);        				   
        				   resultSeq.add(xNodeSetItem);
        			   }               
        		   }
        		   else if (xPathExprPartResult instanceof ResultSequence) {
        			   ResultSequence inpResultSeq = (ResultSequence)xPathExprPartResult;
        			   int rSeqLength = inpResultSeq.size();
        			   
        			   for (int idx1 = 0; idx1 < rSeqLength; idx1++) {
        				   XObject xObj = inpResultSeq.item(idx1);
        				   resultSeq.add(xObj);                 
        			   }
        		   }
        		   else {
        			   // We're assuming here that, an input value is an xdm sequence 
        			   // with cardinality one.
        			   
        			   resultSeq.add(xPathExprPartResult);               
        		   }
        	   }
           }
           finally {
        	  if (isXpathCtxtActive) {
        		 xctxt.popCurrentNode(); 
        	  }
           }
        }
        
        if (m_xpathPredicateStr != null) {
        	if (prefixTable != null) {
        		m_xpathPredicateStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(m_xpathPredicateStr, prefixTable);
        	}

        	XPath xpathObj = new XPath(m_xpathPredicateStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
        	
        	if (m_vars != null) {
        		xpathObj.fixupVariables(m_vars, m_globals_size);
            }
        	
            XObject xpath3ContextItem = xctxt.getXPath3ContextItem();
            int xpath3ContextPos = xctxt.getXPath3ContextPosition();
            int xpath3ContextSize = xctxt.getXPath3ContextSize();
            
            try {
            	result = getSequenceValueByIndex(xctxt, resultSeq, xpathObj);
            }
            catch (TransformerException ex) {
            	throw new javax.xml.transform.TransformerException("XPTY0004 : An error occured while evaluating an XPath predicate "
            			                                                                          + "following a literal sequence constructor expression. Exception "
            			                                                                          + "trace : " + ex.getMessage() + ".", srcLocator);
            }
            finally {
            	xctxt.setXPath3ContextItem(xpath3ContextItem);
            	xctxt.setXPath3ContextPosition(xpath3ContextPos);
            	xctxt.setXPath3ContextSize(xpath3ContextSize);
            }
            
            if (result == null) {
            	try {
            		ResultSequence newResultSeq = getResultSequenceByPredicateEvaluation(xctxt, srcLocator, resultSeq, xpathObj);
            		
            		result = newResultSeq; 
            	}
            	catch (TransformerException ex) {
            		throw new javax.xml.transform.TransformerException("XPTY0004 : An error occured while evaluating an XPath predicate "
                            															          + "following a literal sequence constructor expression. Exception "
                            															          + "trace : " + ex.getMessage() + ".", srcLocator);
            	}
            	finally {
            		xctxt.setXPath3ContextItem(xpath3ContextItem);
                	xctxt.setXPath3ContextPosition(xpath3ContextPos);
                	xctxt.setXPath3ContextSize(xpath3ContextSize);
            	}
            }
        }
        else if ((m_xpathPrefixStr != null) && (m_xpathSuffixStr != null)) {
        	XPath xpathPrefixObj = new XPath(m_xpathPrefixStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
        	
        	if (m_vars != null) {
        		xpathPrefixObj.fixupVariables(m_vars, m_globals_size);
            }
        	
            XPath xpathSuffixObj = new XPath(m_xpathSuffixStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
        	
        	if (m_vars != null) {
        		xpathSuffixObj.fixupVariables(m_vars, m_globals_size);
            }
        	
        	int sourceNode = xctxt.getCurrentNode(); 
        	
        	XObject evalResult = xpathPrefixObj.execute(xctxt, sourceNode, xctxt.getNamespaceContext());
        	
        	ResultSequence newResultSeq = new ResultSequence();
        	
        	boolean isProcessedAsNodeSet = false;
        	
        	if (evalResult instanceof XMLNodeCursorImpl) {
        		XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)evalResult;
        		DTMCursorIterator dtmCursorIterator = xmlNodeCursorImpl.iter();
        		
        		int nextNode = DTM.NULL;        	           	   
        		
        		while ((nextNode = dtmCursorIterator.nextNode()) != DTM.NULL) {
        			try {
        				xctxt.pushCurrentNode(nextNode);
        				XObject xObj = xpathSuffixObj.execute(xctxt, nextNode, xctxt.getNamespaceContext());
        				newResultSeq.add(xObj);
        			}
        			finally {
        				xctxt.popCurrentNode();
        			}
        		}

        		result = newResultSeq;

        		isProcessedAsNodeSet = true;
        	}
        	else if (evalResult instanceof ResultSequence) {
        		ResultSequence rSeq = (ResultSequence)evalResult;
        		int size1 = rSeq.size();
        		
        		for (int idx = 0; idx < size1; idx++) {
        			XObject xObj = rSeq.item(idx);
        			
        			if (xObj instanceof XMLNodeCursorImpl) {
        				int nextNode = ((XMLNodeCursorImpl)xObj).asNode(xctxt);
        				
        				try {
        					xctxt.pushCurrentNode(nextNode);
        					XObject xObj2 = xpathSuffixObj.execute(xctxt, nextNode, xctxt.getNamespaceContext());
        					newResultSeq.add(xObj2);
        				}
        				finally {
        					xctxt.popCurrentNode();
        				}
        				
        				isProcessedAsNodeSet = true;
        			}
        			else {
        				break;
        			}
        		}
        		
        		if (isProcessedAsNodeSet) {
        		   result = newResultSeq;
        		}
        	}
        	
        	if (!isProcessedAsNodeSet) {
        	   evalResult = evalResult.getFresh();
        	   
        	   result = evalResult;
        	}
        	
        	return result;
        }
        else if ((Keywords.SIMPLE_MAP_OP).equals(m_xpath_op) && (m_xpathSuffixStr != null)) {        	        	
        	XPath xpathObj = new XPath(m_xpathSuffixStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
        	
        	if (m_vars != null) {
        	   xpathObj.fixupVariables(m_vars, m_globals_size);
            }
        	        	      	        	
        	XObject prevCtxtItem = xctxt.getXPath3ContextItem();
        	int prevCtxtPos = xctxt.getXPath3ContextPosition();
        	int prevCtxtSize = xctxt.getXPath3ContextSize();
        	        	        	
        	boolean isXdmSeqNumeric = true;
        	
        	boolean isXdmSeqContainsFuncItem = false;
        	
        	int size1 = resultSeq.size();
        	
        	for (int idx = 0; idx < size1; idx++) {
        	   XObject xObj = resultSeq.item(idx);
        	   
        	   if (!((xObj instanceof XNumber) || (xObj instanceof XSNumericType))) {
        		  isXdmSeqNumeric = false;
        		  
        		  if (xObj instanceof XPathNamedFunctionReference) {
        			 isXdmSeqContainsFuncItem = true;  
        		  }
        	   }
        	}

        	if (isXdmSeqNumeric && !isXdmSeqContainsFuncItem) {
        		ResultSequence resultSeq1 = new ResultSequence();

        		for (int idx = 0; idx < size1; idx++) {
        			XObject xObj = resultSeq.item(idx);

        			xctxt.setXPath3ContextItem(xObj);
        			xctxt.setXPath3ContextPosition(idx + 1);
        			xctxt.setXPath3ContextSize(size1);

        			try {
        				XObject xObj1 = xpathObj.execute(xctxt, currentNode, xctxt.getNamespaceContext());

        				resultSeq1.add(xObj1);
        			}
        			finally {
        				xctxt.setXPath3ContextItem(prevCtxtItem);        			
        				xctxt.setXPath3ContextPosition(prevCtxtPos);
        				xctxt.setXPath3ContextSize(prevCtxtSize); 
        			}
        		}

        		return resultSeq1;
        	}
        	else if (isXdmSeqContainsFuncItem) {
        		ResultSequence resultSeq1 = new ResultSequence();

        		for (int idx = 0; idx < size1; idx++) {
        			XObject xObj = resultSeq.item(idx);

        			if (xObj instanceof XPathNamedFunctionReference) {
        			   XObject xObj1 = xpathObj.execute(xctxt, currentNode, xctxt.getNamespaceContext());
        			   
        			   ResultSequence argSeq1 = new ResultSequence();
        			   
        			   if (xObj1 instanceof ResultSequence) {
        				  argSeq1 = (ResultSequence)xObj1;  
        			   }
        			   else {
        			      argSeq1.add(xObj1);
        			   }
        			   
        			   XObject evalResult = m_xsl3FunctionService.evaluateXPathNamedFunctionReference((XPathNamedFunctionReference)xObj, null, argSeq1, 
        					                                                                              prefixTable, m_vars, m_globals_size, getExpressionOwner(), xctxt);
        			   resultSeq1.add(evalResult);
        			}
        			else {
        				xctxt.setXPath3ContextItem(xObj);
        				xctxt.setXPath3ContextPosition(idx + 1);
        				xctxt.setXPath3ContextSize(size1);

        				try {
        					XObject xObj1 = xpathObj.execute(xctxt, currentNode, xctxt.getNamespaceContext());

        					resultSeq1.add(xObj1);
        				}
        				finally {
        					xctxt.setXPath3ContextItem(prevCtxtItem);        			
        					xctxt.setXPath3ContextPosition(prevCtxtPos);
        					xctxt.setXPath3ContextSize(prevCtxtSize); 
        				}
        			}
        		}
        		
        		return resultSeq1;
        	}
        	else {
        		xctxt.setXPath3ContextItem(new XSString(resultSeq.str()));
        		xctxt.setXPath3ContextPosition(1);
    			xctxt.setXPath3ContextSize(1);

        		try {
        			XObject xObj1 = xpathObj.execute(xctxt, currentNode, xctxt.getNamespaceContext());

        			if (!(xObj1 instanceof ResultSequence)) {        			
        				result = xObj1;

        				return result;
        			}
        			else {
        				ResultSequence resultSeq1 = new ResultSequence();

        				ResultSequence rSeq2 = (ResultSequence)xObj1;
        				int size2 = rSeq2.size();

        				for (int idx2 = 0; idx2 < size2; idx2++) {
        					resultSeq1.add(rSeq2.item(idx2)); 
        				}

        				result = resultSeq1;

        				return result;
        			}
        		}
        		finally {
        			xctxt.setXPath3ContextItem(prevCtxtItem);        			
        			xctxt.setXPath3ContextPosition(prevCtxtPos);
        			xctxt.setXPath3ContextSize(prevCtxtSize);
        		}
        	 }
        }
        else if (m_xpathSuffixStr != null) {
        	if (prefixTable != null) {
        		m_xpathSuffixStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(m_xpathSuffixStr, prefixTable);
        	}
        	
        	boolean isSuffixPredicate = (m_xpathSuffixStr.startsWith("[") && m_xpathSuffixStr.endsWith("]"));
        	String xpathStrEffective = null;
        	
        	if (isSuffixPredicate) {
        	   xpathStrEffective = m_xpathSuffixStr.substring(1, m_xpathSuffixStr.length() - 1);
        	}
        	else {
        	   xpathStrEffective = m_xpathSuffixStr;
        	}

        	XPath xpathObj = new XPath(xpathStrEffective, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
        	
        	if (m_vars != null) {
        		xpathObj.fixupVariables(m_vars, m_globals_size);
            }
        	
        	ResultSequence newResultSeq = new ResultSequence();
        	
        	int rSeqLength = resultSeq.size();
        	
        	for (int idx = 0; idx < rSeqLength; idx++) {
        	   XObject xObj = resultSeq.item(idx);
        	   
        	   if (xObj instanceof XMLNodeCursorImpl) {
        		  XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)xObj;
        		  DTMCursorIterator iter = xmlNodeCursorImpl.iterRaw();
        		  
        		  int newContextNode = iter.nextNode();
        		  
        		  XObject evalResult = null;
        		  
        		  xctxt.pushCurrentNode(newContextNode);
        		  
        		  try {
        		      evalResult = xpathObj.execute(xctxt, newContextNode, xctxt.getNamespaceContext());
        		  }
        		  finally {
        			  xctxt.popCurrentNode(); 
        		  }
        		  
        		  if (!isSuffixPredicate) {        		  
        			  XMLNodeCursorImpl newNodeSet = (XMLNodeCursorImpl)evalResult;
        			  DTMCursorIterator iter2 = newNodeSet.iterRaw();

        			  int nextNode = DTM.NULL;

        			  while ((nextNode = iter2.nextNode()) != DTM.NULL) {
        				  XMLNodeCursorImpl node = new XMLNodeCursorImpl(nextNode, xctxt);
        				  newResultSeq.add(node);
        			  }
        		  }
        		  else if (evalResult.bool()) {
        			  newResultSeq.add(xObj);
        		  }
        	   }
        	   else if (xObj instanceof XSAnyAtomicType) {
        		   if (isSuffixPredicate) { 
        			   XObject prevCtxtItem = xctxt.getXPath3ContextItem();

        			   XObject evalResult = null;

        			   xctxt.setXPath3ContextItem(xObj);

        			   try {
        				   evalResult = xpathObj.execute(xctxt, DTM.NULL, xctxt.getNamespaceContext());
        				   
        				   if (evalResult.bool()) {
        				      newResultSeq.add(xObj);
        				   }
        			   }
        			   finally {
        				   xctxt.setXPath3ContextItem(prevCtxtItem);  
        			   }
        		   }
        		   else {
                       // REVISIT
        		   }
        	   }
        	}
        	
        	result = newResultSeq;
        }
        else {
        	result = resultSeq;
        }
        
        if (result instanceof ResultSequence) {
        	ResultSequence rSeq = (ResultSequence)result;
        	int rSeqLength = rSeq.size();
        	
        	for (int idx = 0; idx < rSeqLength; idx++) {
        		XObject xdmItem = rSeq.item(idx);
        		
        		if (xdmItem instanceof XMLNodeCursorImpl) {
        			XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)xdmItem;
        			int nodeHandle = xmlNodeCursorImpl.asNode(xctxt);
        			
        			DTM dtm = xctxt.getDTM(nodeHandle);
        			boolean flg1 = false;
        			
        			if (dtm.getNodeType(nodeHandle) == DTM.ELEMENT_NODE) {
        				Node node = dtm.getNode(nodeHandle);
        				String nodeName = node.getNodeName();
        				
        				try {
        					if (nodeName.startsWith("b_")) {
        						Element elemNode = (Element)node;
        						
        						xdmItem = new XString(elemNode.getTextContent());
        					}
        					else {
        						flg1 = true;
        					}
        				}
        				catch (Exception ex) {
        					flg1 = true;
        				}        		         		 
        			}

        			if (flg1) {
        				xdmItem = rSeq.item(idx);
        				xdmItem = xdmItem.getFresh();
        			}
        		}

        		rSeq.set(idx, xdmItem);
        	}
        	
        	result = rSeq; 
        }
        
        if (isSourceKind1 && isSourceKind2) {
           result.setDataHomogeneousSource(true);
        }
        
        return result;
    }

	/**
	 * Given a supplied sequence which is the result of XPath literal sequence constructor 
	 * evaluation, and an XPath predicate expression following the literal sequence 
	 * constructor, attempt to return a result as an index accessor for the supplied sequence.
	 */
	private XObject getSequenceValueByIndex(XPathContext xctxt, ResultSequence resultSeq, XPath xpathObj)
																									throws TransformerException {
		
		XObject result = null;
		
		List<Integer> intList = new ArrayList<Integer>();
		
		int size1 = resultSeq.size();
		
		for (int idx = 0; idx < size1; idx++) {
			XObject xObj = resultSeq.item(idx);
			
			xctxt.setXPath3ContextItem(xObj);
			xctxt.setXPath3ContextPosition(idx + 1);
			xctxt.setXPath3ContextSize(resultSeq.size());
			
			XObject seqEvalResult = xpathObj.execute(xctxt, DTM.NULL, xctxt.getNamespaceContext());
			
			if (seqEvalResult instanceof XNumber) {
		 	   double dbl1 = ((XNumber)seqEvalResult).num();
		 	   
		 	   if (dbl1 == (int)dbl1) {
		 		  intList.add((int)dbl1); 
		 	   }                 	   
		 	}
			else if (seqEvalResult instanceof XSNumericType) {
			   XSNumericType xsNumericType = (XSNumericType)seqEvalResult;
		 	   String strValue = xsNumericType.stringValue();
		 	   
		 	   double dbl1 = Double.valueOf(strValue);
		 	   
		 	   if (dbl1 == (int)dbl1) {
		 		  intList.add((int)dbl1); 
		 	   } 
			}
		}
		
		int size2 = intList.size();
		
		if ((size1 > 0) && (size2 == size1)) {
			Integer intValue = intList.get(0);
			boolean allValSame = true;			
						
			for (int idx = 1; idx < size2; idx++) {
				int iVal = intList.get(idx);
				
				if (iVal != intValue) {
				   allValSame = false;
				   
				   break;
				}
			}
			
			if (allValSame && ((intValue >= 1) && (intValue <= size1))) {
			    result = resultSeq.item(intValue - 1); 
			}
			else {
				result = new ResultSequence();
			}
		}
		
		return result;
	}
	
	/**
	 * Given a supplied sequence which is the result of XPath literal sequence constructor 
	 * evaluation, and an XPath predicate expression following the literal sequence 
	 * constructor, evaluate the predicate in turn for each sequence item as a context item 
	 * and return the supplied sequence values for which the predicate evaluates to true.
	 */
	private ResultSequence getResultSequenceByPredicateEvaluation(XPathContext xctxt, SourceLocator srcLocator,
														          ResultSequence resultSeq, XPath xpathObj) throws TransformerException {
		
		ResultSequence newResultSeq = new ResultSequence();
		
		for (int idx = 0; idx < resultSeq.size(); idx++) {
			XObject xObj = resultSeq.item(idx);
			
			xctxt.setXPath3ContextItem(xObj);
			xctxt.setXPath3ContextPosition(idx + 1);
			xctxt.setXPath3ContextSize(resultSeq.size());
			
			XObject seqEvalResult = xpathObj.execute(xctxt, DTM.NULL, xctxt.getNamespaceContext());
			
			boolean boolValue = false;
			
			if (seqEvalResult instanceof XBooleanStatic) {
				boolValue = ((XBooleanStatic)seqEvalResult).bool();
			}
			else if (seqEvalResult instanceof XBoolean) {
				boolValue = ((XBoolean)seqEvalResult).bool();
			}
			else if (seqEvalResult instanceof XSBoolean) {
				boolValue = ((XSBoolean)seqEvalResult).bool();
			}
			else {
				throw new javax.xml.transform.TransformerException("XPTY0004 : An error occured while evaluating an XPath predicate "
						                                                              + "following a literal sequence constructor expression. "
						                                                              + "The predicate didn't evaluate to a boolean value.", srcLocator);
			}
			
			if (boolValue) {
				newResultSeq.add(xObj);
			}
		}
		
		return newResultSeq;
	}
	
	@Override
    public void callVisitors(ExpressionOwner owner, XPathVisitor visitor) {
       // No op
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

    public List<String> getSequenceConstructorXPathParts() {
        return m_sequenceConstructorXPathParts;
    }

    public void setSequenceConstructorXPathParts(List<String> 
                                                        sequenceConstructorXpathParts) {
        this.m_sequenceConstructorXPathParts = sequenceConstructorXpathParts;
    }

    public String getPredicateExpr() {
	    return m_xpathPredicateStr;
	}
    
	public void setPredicateExpr(String sequencePredicateExpr) {
		this.m_xpathPredicateStr = sequencePredicateExpr; 		
	}
	
	public String getXPathPrefixStr() {
		return m_xpathPrefixStr;
	}

	public void setXPathPrefixStr(String prefixStr) {
		this.m_xpathPrefixStr = prefixStr;		
	}
	
	public String getXPathSuffixStr() {
		return m_xpathSuffixStr;
	}

	public void setXPathSuffixStr(String xpathSuffixStr) {
		m_xpathSuffixStr = xpathSuffixStr; 		
	}

	public String getXPathOp() {
		return m_xpath_op;
	}

	public void setXPathOp(String xpathOp) {
		this.m_xpath_op = xpathOp;
	}		

}
