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

import org.apache.xalan.templates.XMLNSDecl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMCursorIterator;
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
import org.apache.xpath.objects.XPathArray;

import xml.xpath31.processor.types.XSNumericType;

/**
 * Class definition, to evaluate an XPath 3.1 literal 
 * array constructor expression.
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
        
        XPathArray result = new XPathArray();
        
        if (m_IsEmptyArray) {
           return result;  	
        }
        
        SourceLocator srcLocator = xctxt.getSAXLocator();
        
        final int sourceNode = xctxt.getContextNode();
        
        List<XMLNSDecl> prefixTable = XslTransformEvaluationHelper.getXSLNsPrefixTable(xctxt);
        
        int size1 = m_arrayConstructorXPathParts.size();
        
        for (int idx = 0; idx < size1; idx++) {
           String xpathExprStr = m_arrayConstructorXPathParts.get(idx);
           
           if (prefixTable != null) {
              xpathExprStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(xpathExprStr, prefixTable);
           }
           
           XPath xpathObj = new XPath(xpathExprStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
           
           if (m_vars != null) {
              xpathObj.fixupVariables(m_vars, m_globals_size);
           }
           
           Expression expr = xpathObj.getExpression();
           
           if (expr instanceof LocPathIterator) {
               LocPathIterator locPathIterator = (LocPathIterator)expr;
               
               DTMCursorIterator dtmIter = null;                     
               try {
                   dtmIter = locPathIterator.asIterator(xctxt, sourceNode);
               }
               catch (ClassCastException ex) {
                   // No op
               }
               
               if (dtmIter != null) {
                  int nextNode = DTM.NULL;
                  
                  while ((nextNode = dtmIter.nextNode()) != DTM.NULL)
                  {
                      XMLNodeCursorImpl xNodeSetItem = new XMLNodeCursorImpl(nextNode, xctxt);
                      
                      result.add(xNodeSetItem);
                  }
               }
               else if (xpathExprStr.startsWith("$") && xpathExprStr.contains("[") && xpathExprStr.endsWith("]")) {
                   String varRefXPathExprStr = "$" + xpathExprStr.substring(1, xpathExprStr.indexOf('['));
                   String xpathIndexExprStr = xpathExprStr.substring(xpathExprStr.indexOf('[') + 1, xpathExprStr.indexOf(']'));
                   
                   if (prefixTable != null) {
                      varRefXPathExprStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(varRefXPathExprStr, 
                    		   																								  prefixTable);
                   }
                   
                   XPath varXPathObj = new XPath(varRefXPathExprStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
                   
                   XObject varEvalResult = varXPathObj.execute(xctxt, sourceNode, xctxt.getNamespaceContext());
                   
                   // Evaluate an, xdm sequence XPath index expression
                   
                   if (prefixTable != null) {
                      xpathIndexExprStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(xpathIndexExprStr, prefixTable);
                   }
                   
                   XPath xpathIndexObj = new XPath(xpathIndexExprStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
                   
                   if (m_vars != null) {
                      xpathIndexObj.fixupVariables(m_vars, m_globals_size);
                   }
                   
                   XObject arrIndexEvalResult = xpathIndexObj.execute(xctxt, sourceNode, xctxt.getNamespaceContext());
                   
                   if (varEvalResult instanceof ResultSequence) {
                       ResultSequence varEvalResultSeq = (ResultSequence)varEvalResult; 
                       
                       if (arrIndexEvalResult instanceof XNumber) {
                          double dValIndex = ((XNumber)arrIndexEvalResult).num();
                          if (dValIndex == (int)dValIndex) {
                             XObject evalResult = varEvalResultSeq.item((int)dValIndex - 1);
                             
                             result.add(evalResult);
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
                             
                             result.add(evalResult);
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
               XObject xObj = xpathObj.execute(xctxt, sourceNode, xctxt.getNamespaceContext());
               
               if (xObj instanceof XMLNodeCursorImpl) {                   
                  XMLNodeCursorImpl XMLNodeCursorImpl = (XMLNodeCursorImpl)xObj;
                  DTMCursorIterator dtmCursorIterator = XMLNodeCursorImpl.iter();
                   
                  int nextNode = DTM.NULL;
                   
                  while ((nextNode = dtmCursorIterator.nextNode()) != DTM.NULL) {
                     XMLNodeCursorImpl xmlNodeCursorImpl = new XMLNodeCursorImpl(nextNode, xctxt);
                     
                     result.add(xmlNodeCursorImpl);
                  }               
               }
               else if (xObj instanceof ResultSequence) {                  
                  result.add(xObj);                 
               }
               else if (xObj instanceof XPathArray) {
            	  result.add(xObj);
               }
               else {            	   
            	  result.add(xObj);               
               }
           }
        }
        
        /*int size2 = result.size();
        int xdmEmptyArrCount = 0;
        for (int idx = 0; idx < size2; idx++) {
        	XObject xObj = result.get(idx);        	
        	if ((xObj instanceof XPathArray) && (((XPathArray)xObj).size() == 0)) {
        		xdmEmptyArrCount++; 
        	}           
        }
        
        if (xdmEmptyArrCount == 1) {
        	XPathArray xpathArr2 = new XPathArray(); 
        	for (int idx = 0; idx < size2; idx++) {
               XObject xObj = result.get(idx);
               if (xObj instanceof XPathArray) {
            	   if (((XPathArray)xObj).size() > 0) {
            	      xpathArr2.add(xObj);
            	   }
               }
               else {
            	   xpathArr2.add(xObj);
               }
        	}
        	
        	result = xpathArr2; 
        }*/
        
        return result;
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
