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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
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
import org.apache.xpath.objects.XPathMap;
import org.apache.xpath.objects.XString;

import xml.xpath31.processor.types.XSDouble;
import xml.xpath31.processor.types.XSNumericType;
import xml.xpath31.processor.types.XSString;

/**
 * Xalan-J's XPath parser, constructs an object of this class, 
 * to produce run-time representation of an XPath map expression.
 * 
 * Ref: https://www.w3.org/TR/xpath-31/#id-maps
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XPathMapConstructor extends Expression {
    
	private static final long serialVersionUID = 2664427126053000318L;
	
	// Variable reference, denoting an native run-time map
	private Map<String, String> fMapVar = new HashMap<String, String>();
	
	// The following two fields of this class, are used during 
    // XPath.fixupVariables(..) action as performed within object of 
    // this class.    
    private Vector fVars;    
    private int fGlobalsSize;
    
    private String KEY = "key";
    private String VALUE = "value";
    
    @Override
    public void callVisitors(ExpressionOwner owner, XPathVisitor visitor) {
       // no op
    }

    @Override
    public XObject execute(XPathContext xctxt) throws TransformerException {        
    	XPathMap xpathResultMap = new XPathMap();                                
        
        ElemTemplateElement elemTemplateElement = (ElemTemplateElement)xctxt.getNamespaceContext();
        List<XMLNSDecl> prefixTable = null;
        if (elemTemplateElement != null) {
            prefixTable = (List<XMLNSDecl>)elemTemplateElement.getPrefixTable();
        }
        
        Set<Entry<String, String>> mapEntrySet = fMapVar.entrySet();
        Iterator<Entry<String, String>> mapIter = mapEntrySet.iterator();
        while (mapIter.hasNext()) {
           Entry<String, String> mapEntry = mapIter.next();
           String xpathKeyStr = mapEntry.getKey();
           String xpathValueStr = mapEntry.getValue();
           
           if (prefixTable != null) {
        	  xpathKeyStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(xpathKeyStr, 
                                                                                                      prefixTable);        	  
           }
           
           XObject mapEntryKey = evaluateXPathExpression(xpathKeyStr, xctxt, KEY);
           if (mapEntryKey instanceof XString) {
        	  mapEntryKey = new XSString(((XString)mapEntryKey).str()); 
           }
           else if (mapEntryKey instanceof XNumber) {
        	  mapEntryKey = new XSDouble(((XNumber)mapEntryKey).num());  
           }
           
           if (prefixTable != null) {
        	  xpathValueStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(xpathValueStr, 
                                                                                                       prefixTable);        	  
           }
           
           XObject mapEntryValue = evaluateXPathExpression(xpathValueStr, xctxt, VALUE);           
           xpathResultMap.put(mapEntryKey, mapEntryValue);
        }
        
        return xpathResultMap;
    }

    @Override
    public void fixupVariables(Vector vars, int globalsSize) {
        fVars = (Vector)(vars.clone());
        fGlobalsSize = globalsSize;
    }

	public Map<String, String> getNativeMap() {
		return fMapVar;
	}

	public void setNativeMap(Map<String, String> map) {
		this.fMapVar = map;
	}
	
    @Override
    public boolean deepEquals(Expression expr) {
        return false;
    }
    
    /**
     * 
     * @param xpathExprStr   XPath expression string to be evaluated
     * @param xctxt          XPath context
     * @param mapComponent   is either of strings "key", or "value"
     * @return               Result of XPath expression evaluation
     * 
     * @throws TransformerException
     */
    private XObject evaluateXPathExpression(String xpathExprStr, XPathContext xctxt, String mapComponentName) throws TransformerException {
    	XObject result = null;
    	
    	int contextNode = xctxt.getContextNode();
    	
    	SourceLocator srcLocator = xctxt.getSAXLocator();
    	
    	XPath xpathObj = new XPath(xpathExprStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
        if (fVars != null) {
           xpathObj.fixupVariables(fVars, fGlobalsSize);
        }

        Expression xpathExpr = xpathObj.getExpression();
    	
    	if (xpathExpr instanceof LocPathIterator) {    		
            LocPathIterator locPathIterator = (LocPathIterator)xpathExpr;
            
            DTMIterator dtmIter = null;                     
            try {
                dtmIter = locPathIterator.asIterator(xctxt, contextNode);
            }
            catch (ClassCastException ex) {
                // NO OP
            }
            
            if (dtmIter != null) {
               int nextNode;
               int nodeCount = 0;
               XPathArray mapEntryValue = new XPathArray();
               while ((nextNode = dtmIter.nextNode()) != DTM.NULL) {
            	   nodeCount++;
            	   if (KEY.equals(mapComponentName)) {
            	      if (nodeCount > 1) {
            		     throw new javax.xml.transform.TransformerException("XPTY0004 : Key of a map cannot be a sequence of size "
            		  		                                                                   + "greater than one.", srcLocator); 
            	      }
            	      else {
            	    	 result = new XNodeSet(nextNode, xctxt);
            	    	 return result;
            	      }
            	   }            	   
                   XNodeSet xNodeSetItem = new XNodeSet(nextNode, xctxt);
                   mapEntryValue.add(xNodeSetItem);
               }
               
               return mapEntryValue; 
            }
            else if (xpathExprStr.startsWith("$") && xpathExprStr.contains("[") && 
                                                                        xpathExprStr.endsWith("]")) {
                String varRefXPathExprStr = "$" + xpathExprStr.substring(1, xpathExprStr.indexOf('['));
                String xpathIndexExprStr = xpathExprStr.substring(xpathExprStr.indexOf('[') + 1, 
                                                                                    xpathExprStr.indexOf(']'));                
                XPath varXPathObj = new XPath(varRefXPathExprStr, srcLocator, xctxt.getNamespaceContext(), 
                                                                                                   XPath.SELECT, null);
                XObject varEvalResult = varXPathObj.execute(xctxt, xctxt.getCurrentNode(), xctxt.getNamespaceContext());
                
                XPath xpathIndexObj = new XPath(xpathIndexExprStr, srcLocator, xctxt.getNamespaceContext(), 
                                                                                                  XPath.SELECT, null);
                if (fVars != null) {
                   xpathIndexObj.fixupVariables(fVars, fGlobalsSize);
                }
                
                XObject arrIndexEvalResult = xpathIndexObj.execute(xctxt, xctxt.getCurrentNode(), 
                                                                                             xctxt.getNamespaceContext());
                
                if (varEvalResult instanceof ResultSequence) {
                    ResultSequence varEvalResultSeq = (ResultSequence)varEvalResult; 
                    
                    if (arrIndexEvalResult instanceof XNumber) {
                       double dValIndex = ((XNumber)arrIndexEvalResult).num();
                       if (dValIndex == (int)dValIndex) {
                          XObject evalResult = varEvalResultSeq.item((int)dValIndex - 1);
                          result = evalResult;                          
                          return result;
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
                          result = evalResult;                          
                          return result;
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
    	else if (xpathExpr instanceof XPathSequenceConstructor) {
    		ResultSequence sequence = (ResultSequence)xpathExpr.execute(xctxt);
    		return sequence;
    	}
        else {
        	xpathObj = new XPath(xpathExprStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
            if (fVars != null) {
               xpathObj.fixupVariables(fVars, fGlobalsSize);
            }
            
            XObject xPathExprPartResult = xpathObj.execute(xctxt, contextNode, 
                                                                           xctxt.getNamespaceContext());
            
            if (xPathExprPartResult instanceof XNodeSet) {
               DTMManager dtmMgr = (DTMManager)xctxt;
                
               XNodeSet xNodeSet = (XNodeSet)xPathExprPartResult;
               DTMIterator sourceNodes = xNodeSet.iter();
                
               int nextNode;
               int nodeCount = 0;               
               while ((nextNode = sourceNodes.nextNode()) != DTM.NULL) {
            	  nodeCount++;
                  if (KEY.equals(mapComponentName)) {
            	     if (nodeCount > 1) {
            		    throw new javax.xml.transform.TransformerException("XPTY0004 : Key of a map cannot be a sequence of size "
            		  		                                                                   + "greater than one.", srcLocator); 
            	     }
            	     else {
            	    	result = new XNodeSet(nextNode, xctxt);
            	    	return result;
            	     }
            	  } 
               }               
            }
            else if (xPathExprPartResult instanceof ResultSequence) {
               ResultSequence inpResultSeq = (ResultSequence)xPathExprPartResult;
               XPathArray mapEntryValue = new XPathArray();
               for (int idx1 = 0; idx1 < inpResultSeq.size(); idx1++) {
            	  if (KEY.equals(mapComponentName)) {
            		 if (idx1 > 0) {
            	        throw new javax.xml.transform.TransformerException("XPTY0004 : Key of a map cannot be a sequence of size "
            	     		                                                                        + "greater than one.", srcLocator);
            		 }
            		 else {
            			result = inpResultSeq.item(idx1);
            			return result;
            		 }
            	  }
                  XObject xObj = inpResultSeq.item(idx1);
                  mapEntryValue.add(xObj);                 
               }
               
               return mapEntryValue;
            }
            else {
               return xPathExprPartResult;               
            }
        }
    	
    	return result;
    }

}
