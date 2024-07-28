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
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.dtm.DTMManager;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.axes.LocPathIterator;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathArray;

import xml.xpath31.processor.types.XSNumericType;

/**
 * This class implements and evaluates XPath 3.1 literal 
 * array constructor expressions.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XPathArrayConstructor extends Expression {
    
	private static final long serialVersionUID = 6480756741454381402L;

	private List<String> m_arrayConstructorXPathParts = new ArrayList<String>();
	
	private boolean m_IsEmptyArray = false;
    
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
    public XObject execute(XPathContext xctxt) throws TransformerException {
        
        XPathArray xpathArrResult = new XPathArray();
        
        if (m_IsEmptyArray) {
           return xpathArrResult;  	
        }
        
        SourceLocator srcLocator = xctxt.getSAXLocator();
        
        int contextNode = xctxt.getContextNode();
        
        ElemTemplateElement elemTemplateElement = (ElemTemplateElement)xctxt.getNamespaceContext();
        List<XMLNSDecl> prefixTable = null;
        if (elemTemplateElement != null) {
            prefixTable = (List<XMLNSDecl>)elemTemplateElement.getPrefixTable();
        }
        
        // We evaluate below all the, XPath expression parts within the list 
        // 'sqrArrayConstructorXPathParts', and concatenate the sequences resulting
        // from each of them, to get the final result sequence that is returned by
        // this method.
        for (int idx = 0; idx < m_arrayConstructorXPathParts.size(); idx++) {
           String xpathExprStr = m_arrayConstructorXPathParts.get(idx);
           
           if (prefixTable != null) {
              xpathExprStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(xpathExprStr, 
                                                                                                     prefixTable);
           }
           
           XPath xpathObj = new XPath(xpathExprStr, srcLocator, xctxt.getNamespaceContext(), 
                                                                                      XPath.SELECT, null);
           if (m_vars != null) {
              xpathObj.fixupVariables(m_vars, m_globals_size);
           }
           
           Expression xpathExpr = xpathObj.getExpression();
           
           if (xpathExpr instanceof LocPathIterator) {
               LocPathIterator locPathIterator = (LocPathIterator)xpathExpr;
               
               DTMIterator dtmIter = null;                     
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
                      XNodeSet xNodeSetItem = new XNodeSet(nextNode, xctxt);
                      xpathArrResult.add(xNodeSetItem);
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
                   
                   XObject arrIndexEvalResult = xpathIndexObj.execute(xctxt, xctxt.getCurrentNode(), 
                                                                                                xctxt.getNamespaceContext());
                   
                   if (varEvalResult instanceof ResultSequence) {
                       ResultSequence varEvalResultSeq = (ResultSequence)varEvalResult; 
                       
                       if (arrIndexEvalResult instanceof XNumber) {
                          double dValIndex = ((XNumber)arrIndexEvalResult).num();
                          if (dValIndex == (int)dValIndex) {
                             XObject evalResult = varEvalResultSeq.item((int)dValIndex - 1);
                             xpathArrResult.add(evalResult);
                          }
                          else {
                              throw new javax.xml.transform.TransformerException("XPTY0004 : an index value used with an xdm "
                                                                                       + "array reference, is not an integer.", 
                                                                                              srcLocator);  
                          }
                       }
                       else if (arrIndexEvalResult instanceof XSNumericType) {
                          String indexStrVal = ((XSNumericType)arrIndexEvalResult).stringValue();
                          double dValIndex = (Double.valueOf(indexStrVal)).doubleValue();
                          if (dValIndex == (int)dValIndex) {
                             XObject evalResult = varEvalResultSeq.item((int)dValIndex - 1);
                             xpathArrResult.add(evalResult);
                          }
                          else {
                              throw new javax.xml.transform.TransformerException("XPTY0004 : an index value used with an xdm "
                                                                                       + "array reference, is not an integer.", 
                                                                                              srcLocator);  
                          }
                       }
                       else {
                           throw new javax.xml.transform.TransformerException("XPTY0004 : an index value used with an xdm array "
                                                                                    + "reference, is not numeric.", srcLocator);  
                       }
                   }
               }
           }
           else {
               XObject xPathExprPartResult = xpathObj.execute(xctxt, contextNode, 
                                                                              xctxt.getNamespaceContext());
               
               if (xPathExprPartResult instanceof XNodeSet) {
                  DTMManager dtmMgr = (DTMManager)xctxt;
                   
                  XNodeSet xNodeSet = (XNodeSet)xPathExprPartResult;
                  DTMIterator sourceNodes = xNodeSet.iter();
                   
                  int nextNodeDtmHandle;
                   
                  while ((nextNodeDtmHandle = sourceNodes.nextNode()) != DTM.NULL) {
                     XNodeSet xNodeSetItem = new XNodeSet(nextNodeDtmHandle, dtmMgr);
                     xpathArrResult.add(xNodeSetItem);
                  }               
               }
               else if (xPathExprPartResult instanceof ResultSequence) {
                  ResultSequence inpResultSeq = (ResultSequence)xPathExprPartResult; 
                  for (int idx1 = 0; idx1 < inpResultSeq.size(); idx1++) {
                     XObject xObj = inpResultSeq.item(idx1);
                     xpathArrResult.add(xObj);                 
                  }
               }
               else {
                  // We're assuming here that, an input value is an xdm sequence 
                  // with cardinality one.
            	   xpathArrResult.add(xPathExprPartResult);               
               }
           }
        }
        
        return xpathArrResult;
    }

    @Override
    public void fixupVariables(Vector vars, int globalsSize) {
        m_vars = (Vector)(vars.clone());
        m_globals_size = globalsSize;
    }
    
    @Override
    public void callVisitors(ExpressionOwner owner, XPathVisitor visitor) {
        // no op
    }

    @Override
    public boolean deepEquals(Expression expr) {
        return false;
    }

    public List<String> getArrayConstructorXPathParts() {
        return m_arrayConstructorXPathParts;
    }

    public void setArrayConstructorXPathParts(List<String> arrayConstructorXPathParts) {
        this.m_arrayConstructorXPathParts = arrayConstructorXPathParts;
    }

	public boolean isEmptyArray() {
		return m_IsEmptyArray;
	}

	public void setIsEmptyArray(boolean isEmptyArray) {
		this.m_IsEmptyArray = isEmptyArray;
	}

}
