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
package org.apache.xalan.xslt.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.TransformerException;

import org.apache.xalan.templates.XMLNSDecl;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.dtm.DTMManager;
import org.apache.xml.utils.XMLString;
import org.apache.xpath.Expression;
import org.apache.xpath.XPathCollationSupport;
import org.apache.xpath.XPathContext;
import org.apache.xpath.axes.LocPathIterator;
import org.apache.xpath.composite.XPathForExpr;
import org.apache.xpath.composite.XPathSequenceConstructor;
import org.apache.xpath.functions.Function;
import org.apache.xpath.functions.XSL3FunctionService;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;
import org.apache.xpath.operations.Range;
import org.apache.xpath.operations.SimpleMapOperator;
import org.apache.xpath.operations.Variable;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.Node;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

import xml.xpath31.processor.types.XSAnyType;
import xml.xpath31.processor.types.XSDouble;
import xml.xpath31.processor.types.XSNumericType;
import xml.xpath31.processor.types.XSUntyped;
import xml.xpath31.processor.types.XSUntypedAtomic;

/**
 * This class, defines few utility methods, that provide support for
 * XSL 3 transformation processor implementation.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XslTransformEvaluationHelper {
    
    /**
     * Given an xdm input sequence, expand that sequence to produce a new sequence
     * none of whose items are sequence with cardinality greater than one.
     * 
     * The caller of this method, needs to pass an xdm sequence to be expanded
     * as an argument, and another argument reference to get the result from this 
     * method.
     */
    public static void expandResultSequence(ResultSequence seqToBeExpanded, 
                                                                  ResultSequence finalResultSeq) {               
        for (int idx = 0; idx < seqToBeExpanded.size(); idx++) {
          XObject xObject = seqToBeExpanded.item(idx);
          if (xObject instanceof ResultSequence) {
             expandResultSequence((ResultSequence)xObject, finalResultSeq); 
          }
          else {
             finalResultSeq.add(xObject);
          }
       }
    }
    
    /**
     * Given an XPath expression string, replace XML namespace uri references within it,
     * with the corresponding declared XML namespace prefixes, using information from
     * the list object 'nsPrefixTable' passed to this method.
     */
    public static String replaceNsUrisWithPrefixesOnXPathStr(String xpathExprStr, 
                                                                          List<XMLNSDecl> nsPrefixTable) {
       String replacedXPathExprStr = xpathExprStr;
       
       for (int idx = 0; idx < nsPrefixTable.size(); idx++) {
          XMLNSDecl xmlNSDecl = nsPrefixTable.get(idx);
          String prefix = xmlNSDecl.getPrefix();
          String uri = xmlNSDecl.getURI();
          replacedXPathExprStr = replacedXPathExprStr.replace(uri + ":", prefix + ":");
       }
       
       return replacedXPathExprStr; 
    }
    
    /**
     * Get prefix from namespace uri, declared within XSL transformation context. 
     */
    public static String getPrefixFromNsUri(String nsUri, List<XMLNSDecl> nsPrefixTable) {    	
    	String xmlSchemaNsPrefix = null;

    	if (nsUri != null) {
    		for (int idx = 0; idx < nsPrefixTable.size(); idx++) {
    			XMLNSDecl xmlNSDecl = nsPrefixTable.get(idx);
    			if (nsUri.equals(xmlNSDecl.getURI())) {
    				xmlSchemaNsPrefix = xmlNSDecl.getPrefix();
    				break;
    			}
    		}
    	}

    	return xmlSchemaNsPrefix; 
    }
    
    /**
     * Get namespace uri from prefix, declared within XSL transformation context. 
     */
    public static String getNsUriFromPrefix(String prefix, List<XMLNSDecl> nsPrefixTable) {
    	String nsUri = null;
    	
    	if (prefix != null) {
    		for (int idx = 0; idx < nsPrefixTable.size(); idx++) {
    			XMLNSDecl xmlNSDecl = nsPrefixTable.get(idx);
    			if (prefix.equals(xmlNSDecl.getPrefix())) {
    				nsUri = xmlNSDecl.getURI();
    				break;
    			}
    		}
    	}
    	
    	return nsUri; 
    }
    
    /**
     * Given an XObject object reference, return the string value 
     * of the object. 
     */
    public static String getStrVal(XObject xObj) {
       String strVal = null;
       
       if (xObj instanceof XSAnyType) {
          strVal = ((XSAnyType)xObj).stringValue();    
       }
       else {
          strVal = xObj.str();  
       }
       
       return strVal;
    }
    
    /**
     * Add an xdm input item to result sequence, if that already doesn't exist within
     * the result sequence. 
     * @throws TransformerException 
     */
    public static void addItemToResultSequence(ResultSequence resultSeq, XObject inpItem, 
                                                                     boolean cardinalityCheck) throws TransformerException {
        if (cardinalityCheck) {
            if (resultSeq.size() == 0) {                     
                resultSeq.add(inpItem);    
            }
            else if (!contains(resultSeq, inpItem, null, null)) {
                resultSeq.add(inpItem);
            }   
        }
        else {
            resultSeq.add(inpItem);   
        }
    }
    
    /**
     * Add an xdm input item to result sequence, if that already doesn't exist within
     * the result sequence. 
     * @throws TransformerException 
     */
    public static void addItemToResultSequence(ResultSequence resultSeq, XObject inpItem, 
                                                                     boolean cardinalityCheck,
                                                                     String collationUri,
                                                                     XPathCollationSupport xpathCollationSupport) 
                                                                    		                  throws TransformerException {
        if (cardinalityCheck) {
            if (resultSeq.size() == 0) {                     
                resultSeq.add(inpItem);    
            }
            else if (!contains(resultSeq, inpItem, collationUri, xpathCollationSupport)) {
                resultSeq.add(inpItem);
            }   
        }
        else {
            resultSeq.add(inpItem);   
        }
    }
    
    /**
     * Given an XObject object instance, get its contents as a ResultSequence object.  
     */
    public static ResultSequence getResultSequenceFromXObject(XObject xObject, XPathContext xctxt) {        
        
        ResultSequence resultSeq = new ResultSequence();
        
        if (xObject instanceof XNodeSet) {
           XNodeSet nodeSet = (XNodeSet)xObject;
            
           DTMIterator dtmIter = nodeSet.iterRaw();
           int nextNode;
           while ((nextNode = dtmIter.nextNode()) != DTM.NULL) {
              XNodeSet xdmNode = new XNodeSet(nextNode, xctxt);
              resultSeq.add(xdmNode);
           }
        }
        else if (xObject instanceof ResultSequence) {
           ResultSequence rSeq = (ResultSequence)xObject;
           
           for (int idx = 0; idx < rSeq.size(); idx++) {
              resultSeq.add(rSeq.item(idx)); 
           }
        }
        else {
           resultSeq.add(xObject);
        }
        
        return resultSeq;
    }
    
    /**
     * Given a ResultSequence object instance, return a corresponding XNodeSet object if
     * all the items within the supplied ResultSequence are xdm nodes.    
     *
     * If all the items within the supplied ResultSequence object instance are not xdm nodes,
     * then this method returns a null value.  
     */
    public static XNodeSet getXNodeSetFromResultSequence(ResultSequence resultSeq, DTMManager dtmMgr) {
        
        XNodeSet nodeSet = null;
        
        List<Integer> dtmNodeHandleList = new ArrayList<Integer>();
        
        for (int idx = 0; idx < resultSeq.size(); idx++) {
           XObject nodeSetItem = resultSeq.item(idx);
           if (nodeSetItem instanceof XNodeSet) {
              int nodeDtmHandle = (((XNodeSet)nodeSetItem).iter()).nextNode();
              dtmNodeHandleList.add(nodeDtmHandle);
           }
           else {
              break; 
           }
        }
        
        if (dtmNodeHandleList.size() == resultSeq.size()) {
           nodeSet = new XNodeSet(dtmNodeHandleList, dtmMgr);
        }
        
        return nodeSet; 
    }
    
    /**
     * Given a compiled XPath expression and an XPath context object, find
     * the sum of values of xdm items represented by the provided compiled 
     * XPath expression object. 
     */
    public static XNumber getSumOfValues(Expression expr, XPathContext xctxt) throws 
                                                                  javax.xml.transform.TransformerException {        
        double sum = 0.0;    
        
        if (expr instanceof Variable) {
           Variable xslVariable = (Variable)expr;
           XObject resultObj = xslVariable.execute(xctxt);
           if (resultObj instanceof ResultSequence) {
              ResultSequence resultSeq = (ResultSequence)resultObj;
              sum = sumResultSequence(resultSeq);          
           }       
        }
        else if (expr instanceof Function) {
           XObject resultObj = ((Function)expr).execute(xctxt);
           if (resultObj instanceof ResultSequence) {
              ResultSequence resultSeq = (ResultSequence)resultObj;
              sum = sumResultSequence(resultSeq);          
           }  
        }
        else if (expr instanceof XPathForExpr) {
           XPathForExpr forExpr = (XPathForExpr)expr;
           ResultSequence forExprResult = (ResultSequence)(forExpr.execute(xctxt));
           sum = sumResultSequence(forExprResult);
        }
        else if (expr instanceof XPathSequenceConstructor) {
           XPathSequenceConstructor simpleSeqConstructor = (XPathSequenceConstructor)expr;
           ResultSequence seqCtrEvalResult = (ResultSequence)(simpleSeqConstructor.
                                                                                execute(xctxt));
           sum = sumResultSequence(seqCtrEvalResult);
        }
        else if (expr instanceof SimpleMapOperator) {
           SimpleMapOperator simpleMapOperator = (SimpleMapOperator)expr;
           ResultSequence simpleMapOperatorResult = (ResultSequence)(simpleMapOperator.
                                                                                  execute(xctxt));
           sum = sumResultSequence(simpleMapOperatorResult);
        }
        else if (expr instanceof Range) {
        	Range rangeExpr = (Range)expr;
            ResultSequence simpleMapOperatorResult = (ResultSequence)(rangeExpr.execute(xctxt));
            sum = sumResultSequence(simpleMapOperatorResult);
        }
        else if (expr instanceof LocPathIterator) {
           int pos;
            
           DTMIterator nodes = expr.asIterator(xctxt, xctxt.getCurrentNode());

           while ((pos = nodes.nextNode()) != DTM.NULL) {
              DTM dtm = nodes.getDTM(pos);
              XMLString xmlStr = dtm.getStringValue(pos);

              if (xmlStr != null) {
                 sum += xmlStr.toDouble();
              }
           }
           nodes.detach();
        }

        return new XNumber(sum);    
    }
    
    /**
     * Given a compiled XPath expression object and an XPath context object, find
     * the count of xdm items represented by the provided compiled XPath expression 
     * object.  
     */
    public static XNumber getCountOfSequenceItems(Expression expr, XPathContext xctxt) throws 
                                                                                  javax.xml.transform.TransformerException {
        int xdmSequenceSize = 0;
        
        if (expr instanceof Function) {
            XObject evalResult = ((Function)expr).execute(xctxt);
            if (evalResult instanceof XNodeSet) {
                xdmSequenceSize = ((XNodeSet)evalResult).getLength();   
            }
            else if (evalResult instanceof ResultSequence) {
               xdmSequenceSize = ((ResultSequence)evalResult).size();
            }
            else {
               // Here, evalResult is probably of types XSAnyAtomicType, XString, XNumber etc
          	   xdmSequenceSize = 1; 
            }
        }
        else if (expr instanceof Variable) {
           XObject evalResult = ((Variable)expr).execute(xctxt);
           if (evalResult instanceof XNodeSet) {
               xdmSequenceSize = ((XNodeSet)evalResult).getLength();   
           }
           else if (evalResult instanceof ResultSequence) {
              xdmSequenceSize = ((ResultSequence)evalResult).size();
           }
           else {
        	  // Here, evalResult is probably of types XSAnyAtomicType, XString, XNumber etc
        	  xdmSequenceSize = 1; 
           }
        }
        else if (expr instanceof XPathSequenceConstructor) {
           XPathSequenceConstructor simpleSeqConstructor = (XPathSequenceConstructor)expr;
           ResultSequence seqCtrEvalResult = (ResultSequence)(simpleSeqConstructor.
                                                                                execute(xctxt));
           xdmSequenceSize = seqCtrEvalResult.size();
        }
        else if (expr instanceof Expression) {
            if (expr instanceof Range) {
                ResultSequence resultSeq = (ResultSequence)(((Range)expr).execute(xctxt));
                xdmSequenceSize = resultSeq.size();
            }
            else if (expr instanceof XPathForExpr) {
                ResultSequence resultSeq = (ResultSequence)(((XPathForExpr)expr).execute(xctxt));
                xdmSequenceSize = resultSeq.size();   
            }
            else {
                DTMIterator nl = expr.asIterator(xctxt, xctxt.getCurrentNode());
                xdmSequenceSize = nl.getLength(); 
                nl.detach();
            }
        }
    
        return new XNumber((double)xdmSequenceSize);
    }
    
    /**
     * Check whether a 'ResultSequence' object, contains a specific xdm item.
     */
    public static boolean contains(ResultSequence resultSeq, XObject srch, String collationUri,
    		                                                                   XPathCollationSupport xpathCollationSupport) 
    		                                                                		               throws TransformerException {
       
       boolean isSeqContains = false;
       
       for (int idx = 0; idx < resultSeq.size(); idx++) {
          XObject existingItemWithinResultSeq = resultSeq.item(idx);
          if ((existingItemWithinResultSeq instanceof XSUntyped) && 
                                                            (srch instanceof XSUntyped)) {
             if (((XSUntyped)existingItemWithinResultSeq).equals((XSUntyped)srch, collationUri, xpathCollationSupport)) {
                 isSeqContains = true;
                 break;    
             }
          }
          else if ((existingItemWithinResultSeq instanceof XSUntypedAtomic) && 
                                                                  (srch instanceof XSUntypedAtomic)) {
              if (((XSUntypedAtomic)existingItemWithinResultSeq).equals((XSUntypedAtomic)srch, collationUri, xpathCollationSupport)) {
                 isSeqContains = true;
                 break;    
              } 
          }
          else if ((existingItemWithinResultSeq instanceof XSUntyped) && 
                                                                  (srch instanceof XSUntypedAtomic)) {
              if (((XSUntyped)existingItemWithinResultSeq).equals((XSUntypedAtomic)srch, collationUri, xpathCollationSupport)) {
                 isSeqContains = true;
                 break;    
              } 
          }
          else if ((existingItemWithinResultSeq instanceof XSUntypedAtomic) && 
                                                                  (srch instanceof XSUntyped)) {
              if (((XSUntypedAtomic)existingItemWithinResultSeq).equals((XSUntyped)srch, collationUri, xpathCollationSupport)) {
                 isSeqContains = true;
                 break;    
              }
          }
          else if ((existingItemWithinResultSeq instanceof XSNumericType) && (srch instanceof XSNumericType)) {
        	  // When comparing numeric values, collationUri is not used
        	  String lStr = ((XSNumericType)existingItemWithinResultSeq).stringValue();
         	  XSDouble lDouble = new XSDouble(lStr);
         	  
         	  String rStr = ((XSNumericType)srch).stringValue();
        	  XSDouble rDouble = new XSDouble(rStr);
        	  
        	  if (lDouble.equals(rDouble)) {
        		 isSeqContains = true;
                 break;  
        	  }
          }
          else if ((existingItemWithinResultSeq instanceof XSNumericType) && (srch instanceof XNumber)) {
        	  // When comparing numeric values, collationUri is not used
        	  String lStr = ((XSNumericType)existingItemWithinResultSeq).stringValue();
         	  XSDouble lDouble = new XSDouble(lStr);
         	  
         	  double rdbl = ((XNumber)srch).num();
         	  XSDouble rDouble = new XSDouble(rdbl);
         	  
         	 if (lDouble.equals(rDouble)) {
        		isSeqContains = true;
                break;  
        	 }
          }
          else if ((existingItemWithinResultSeq instanceof XNumber) && (srch instanceof XSNumericType)) {
        	 // When comparing numeric values, collationUri is not used
        	 double ldbl = ((XNumber)existingItemWithinResultSeq).num();
          	 XSDouble lDouble = new XSDouble(ldbl);
          	  
          	 String rStr = ((XSNumericType)srch).stringValue();
         	 XSDouble rDouble = new XSDouble(rStr);
          	  
         	 if (lDouble.equals(rDouble)) {
          		isSeqContains = true;
                break;  
          	 } 
         }
         else if ((existingItemWithinResultSeq instanceof XNumber) && (srch instanceof XNumber)) {
        	 // When comparing numeric values, collationUri is not used
        	 double num1 = ((XNumber)existingItemWithinResultSeq).num();
        	 double num2 = ((XNumber)srch).num();
        	 if (num1 == num2) {
        		isSeqContains = true;
        		break; 
        	 }
         }
         else if ((existingItemWithinResultSeq instanceof XSAnyType) && 
                                                                 (srch instanceof XSAnyType)) {
             if (((XSAnyType)existingItemWithinResultSeq).equals((XSAnyType)srch, collationUri, xpathCollationSupport)) {
                isSeqContains = true;
                break;    
             }   
         }
         else if (existingItemWithinResultSeq.equals(srch, collationUri, xpathCollationSupport)) {
             isSeqContains = true;
             break;    
         }
      }
       
      return isSeqContains;       
   }
    
    /*
     * Serialize an XML DOM element node, to XML string value.
     */
    public static String serializeXmlDomElementNode(Node node) throws Exception {
    	String resultStr = null;

    	DOMImplementationLS domImplLS = (DOMImplementationLS)((DOMImplementationRegistry.
    			newInstance()).getDOMImplementation("LS"));
    	LSSerializer lsSerializer = domImplLS.createLSSerializer();
    	DOMConfiguration domConfig = lsSerializer.getDomConfig();
    	domConfig.setParameter(XSL3FunctionService.XML_DOM_FORMAT_PRETTY_PRINT, Boolean.TRUE);
    	resultStr = lsSerializer.writeToString(node);
    	resultStr = resultStr.replaceFirst(XSL3FunctionService.UTF_16, XSL3FunctionService.UTF_8);

    	return resultStr;
    }
    
    /**
     * Get the string contents from an URL.
     */
    public static String getStringContentFromUrl(URL url) throws IOException {
        StringBuilder strBuilder = new StringBuilder();
        
        InputStream inpStream = url.openStream();        
        try {                    
            BufferedReader buffReader = new BufferedReader(new InputStreamReader(inpStream));
            int c;
            while ((c = buffReader.read()) != -1) {
               strBuilder.append((char)c);
            }
        } finally {
            inpStream.close();
        }
     
        return strBuilder.toString();
    }
    
    /**
     * This method checks whether, an XSL xsl:with-param/xsl:param 
     * tunnel attribute's value is true (i.e, having string 
     * values "yes", "true", or "1").
     */
    public static boolean isTunnelAttributeYes(String val) {
       boolean result = false;
       
       if (val != null) {
    	  result = (val.equals("yes") || val.equals("true") || val.equals("1"));
       }
       
       return result;
    }
    
    /**
     * This method produces, numerical sum of xdm sequence items.
     *  
     * @param resultSeq  An xdm sequence object instance, whose items
     *                   need to be numerically added to produce a sum. 
     * @return           The summation value.
     */
    private static double sumResultSequence(ResultSequence resultSeq) {
       
       double sum = 0.0;
       
       for (int idx = 0; idx < resultSeq.size(); idx++) {
          XObject xObj = resultSeq.item(idx);
          String str = null;
          if (xObj instanceof XSAnyType) {
             str = ((XSAnyType)xObj).stringValue();     
          }
          else {
             str = xObj.str();
          }
          if (str != null) {
             XString xStr = new XString(str);
             sum +=  xStr.toDouble();
          }
       }
       
       return sum;
    }

}
