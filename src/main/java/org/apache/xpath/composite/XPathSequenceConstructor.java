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
import java.util.Vector;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.XMLNSDecl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xml.dtm.DTMManager;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.axes.LocPathIterator;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;

import xml.xpath31.processor.types.XSNumericType;

/**
 * This class implements and evaluates XPath 3.1 literal 
 * sequence constructor expressions.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XPathSequenceConstructor extends Expression {

    private static final long serialVersionUID = -5141131877741250613L;
    
    /**
     * These are the main XPath expression strings for the sequence constructor.
     */
    private List<String> m_sequenceConstructorXPathParts = new ArrayList<String>();
    
    /**
     * An optional XPath expression string, to represent an index 
     * suffix of an XPath expression.
     * 
     * For e.g, in the XPath expression (a, b)[1] this is 1.
     */
    private String m_xpathIndexExpr = null;
    
    /**
     * An optional XPath expression string, to represent a suffix of an 
     * XPath expression.
     * 
     * For e.g, in the XPath expression (a, b)/m/n this is m/n.
     */
    private String m_xpathSuffixStr = null;
    
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
    
    @Override
    public void callVisitors(ExpressionOwner owner, XPathVisitor visitor) {
       // no op
    }

    @Override
    public XObject execute(XPathContext xctxt) throws TransformerException {
        
    	XObject result = null;
        
        SourceLocator srcLocator = xctxt.getSAXLocator();
        
        int contextNode = xctxt.getContextNode();
        
        ElemTemplateElement elemTemplateElement = (ElemTemplateElement)xctxt.getNamespaceContext();
        List<XMLNSDecl> prefixTable = null;
        if (elemTemplateElement != null) {
            prefixTable = (List<XMLNSDecl>)elemTemplateElement.getPrefixTable();
        }
        
        /**
         * We evaluate all the XPath expression parts within the list 'm_sequenceConstructorXPathParts', 
         * and concatenate the sequences resulting from each of them, to get the final result 
         * sequence that is returned by this method.
         */
        
        ResultSequence resultSeq = new ResultSequence();
        
        for (int idx = 0; idx < m_sequenceConstructorXPathParts.size(); idx++) {
           String xpathExprStr = m_sequenceConstructorXPathParts.get(idx);
           
           if (prefixTable != null) {
              xpathExprStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(xpathExprStr, 
                                                                                                     prefixTable);
           }
           
           XPath xpathObj = new XPath(xpathExprStr, srcLocator, xctxt.getNamespaceContext(), 
                                                                                      XPath.SELECT, null);           
           
           Expression xpathExpr = xpathObj.getExpression();
           
           if (xpathExpr instanceof LocPathIterator) {
               LocPathIterator locPathIterator = (LocPathIterator)xpathExpr;
               
               DTMCursorIterator dtmIter = null;                     
               try {
                   dtmIter = locPathIterator.asIterator(xctxt, contextNode);
               }
               catch (ClassCastException ex) {
                   // no op
               }
               
               if (dtmIter != null) {
                  int nextNode;
                  while ((nextNode = dtmIter.nextNode()) != DTM.NULL)
                  {
                      XMLNodeCursorImpl xNodeSetItem = new XMLNodeCursorImpl(nextNode, xctxt);
                      resultSeq.add(xNodeSetItem);
                  }
               }
               else if (xpathExprStr.startsWith("$") && xpathExprStr.contains("[") && 
                                                                           xpathExprStr.endsWith("]")) {
                   String varRefXPathExprStr = "$" + xpathExprStr.substring(1, xpathExprStr.indexOf('['));
                   String xpathIndexExprStr = xpathExprStr.substring(xpathExprStr.indexOf('[') + 1, 
                                                                                       xpathExprStr.indexOf(']'));
                   
                   // Evaluate the, variable reference XPath expression
                   if (prefixTable != null) {
                      varRefXPathExprStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(
                                                                                               varRefXPathExprStr, prefixTable);
                   }
                   
                   XPath varXPathObj = new XPath(varRefXPathExprStr, srcLocator, xctxt.getNamespaceContext(), 
                                                                                                      XPath.SELECT, null);
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
                   
                   XPath xpathIndexObj = new XPath(xpathIndexExprStr, srcLocator, xctxt.getNamespaceContext(), 
                                                                                                     XPath.SELECT, null);
                   if (m_vars != null) {
                      xpathIndexObj.fixupVariables(m_vars, m_globals_size);
                   }
                   
                   XObject seqIndexEvalResult = xpathIndexObj.execute(xctxt, xctxt.getCurrentNode(), 
                                                                                                xctxt.getNamespaceContext());
                   
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
           else {
        	   if (m_vars != null) {
        		   xpathObj.fixupVariables(m_vars, m_globals_size);
               }
        	   
               XObject xPathExprPartResult = xpathObj.execute(xctxt, contextNode, 
                                                                              xctxt.getNamespaceContext());                              
               
               if (xPathExprPartResult instanceof XMLNodeCursorImpl) {
                  DTMManager dtmMgr = (DTMManager)xctxt;
                   
                  XMLNodeCursorImpl xNodeSet = (XMLNodeCursorImpl)xPathExprPartResult;
                  DTMCursorIterator sourceNodes = xNodeSet.iter();
                   
                  int nextNodeDtmHandle;
                   
                  while ((nextNodeDtmHandle = sourceNodes.nextNode()) != DTM.NULL) {
                     XMLNodeCursorImpl xNodeSetItem = new XMLNodeCursorImpl(nextNodeDtmHandle, dtmMgr);
                     resultSeq.add(xNodeSetItem);
                  }               
               }
               else if (xPathExprPartResult instanceof ResultSequence) {
                  ResultSequence inpResultSeq = (ResultSequence)xPathExprPartResult; 
                  for (int idx1 = 0; idx1 < inpResultSeq.size(); idx1++) {
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
        
        if (m_xpathIndexExpr != null) {
        	if (prefixTable != null) {
        		m_xpathIndexExpr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(m_xpathIndexExpr, prefixTable);
        	}

        	XPath xpathObj = new XPath(m_xpathIndexExpr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
        	
        	if (m_vars != null) {
        		xpathObj.fixupVariables(m_vars, m_globals_size);
            }
        	
        	XObject seqIndexEvalResult = xpathObj.execute(xctxt, xctxt.getCurrentNode(), xctxt.getNamespaceContext());
        	
        	if (seqIndexEvalResult instanceof XNumber) {
        	   double dbl1 = ((XNumber)seqIndexEvalResult).num();
        	   if (dbl1 == (int)dbl1) {
        		  result = resultSeq.item((int)dbl1 - 1); 
        	   }
        	   else {
        		  throw new javax.xml.transform.TransformerException("XPTY0004 : An index value used with a sequence reference, is not an integer.", srcLocator);
        	   }
        	}
        	else if (seqIndexEvalResult instanceof XSNumericType) {
        	   XSNumericType xsNumericType = (XSNumericType)seqIndexEvalResult;
        	   String strValue = xsNumericType.stringValue();
        	   double dbl1 = Double.valueOf(strValue);
               if (dbl1 == (int)dbl1) {
            	  result = resultSeq.item((int)dbl1 - 1);   
        	   }
        	   else {
        		  throw new javax.xml.transform.TransformerException("XPTY0004 : An index value used with a sequence reference, is not an integer.", srcLocator);
        	   }
        	}
        	else {
        	   throw new javax.xml.transform.TransformerException("XPTY0004 : An index value used with a sequence reference, is not numeric.", srcLocator);
        	}
        }
        else if (m_xpathSuffixStr != null) {
        	if (prefixTable != null) {
        		m_xpathSuffixStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(m_xpathSuffixStr, prefixTable);
        	}

        	XPath xpathObj = new XPath(m_xpathSuffixStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
        	
        	if (m_vars != null) {
        		xpathObj.fixupVariables(m_vars, m_globals_size);
            }
        	
        	ResultSequence newResultSeq = new ResultSequence();
        	
        	for (int idx = 0; idx < resultSeq.size(); idx++) {
        	   XObject xObj = resultSeq.item(idx);
        	   if (xObj instanceof XMLNodeCursorImpl) {
        		  XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)xObj;
        		  DTMCursorIterator iter = xmlNodeCursorImpl.iterRaw();
        		  int newContextNode = iter.nextNode();
        		  XObject evalResult = xpathObj.execute(xctxt, newContextNode, xctxt.getNamespaceContext());
        		  XMLNodeCursorImpl newNodeSet = (XMLNodeCursorImpl)evalResult;
        		  DTMCursorIterator iter2 = newNodeSet.iterRaw();
        		  int nextNode;
        		  while ((nextNode = iter2.nextNode()) != DTM.NULL) {
        			  XMLNodeCursorImpl node = new XMLNodeCursorImpl(nextNode, xctxt);
        			  newResultSeq.add(node);
        		  }
        	   }
        	}
        	
        	result = newResultSeq;
        }
        else {
           result = resultSeq; 	
        }
        
        return result;
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

	public void setIndexExpr(String sequenceIndexExpr) {
		this.m_xpathIndexExpr = sequenceIndexExpr; 		
	}
	
	public String getIndexExpr() {
	    return m_xpathIndexExpr;
	}

	public void setXPathSuffixStr(String xpathSuffixStr) {
		m_xpathSuffixStr = xpathSuffixStr; 		
	}
	
	public String getXPathSuffixStr() {
		return m_xpathSuffixStr;
	}

}
