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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.stream.IntStream;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.templates.ElemFunction;
import org.apache.xalan.templates.ElemTemplate;
import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xalan.templates.TemplateList;
import org.apache.xalan.templates.XMLNSDecl;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xml.dtm.DTMManager;
import org.apache.xml.serializer.CharacterMapConfig;
import org.apache.xml.utils.QName;
import org.apache.xml.utils.XMLString;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionNode;
import org.apache.xpath.XPathCollationSupport;
import org.apache.xpath.XPathContext;
import org.apache.xpath.axes.LocPathIterator;
import org.apache.xpath.composite.XPathForExpr;
import org.apache.xpath.composite.XPathSequenceConstructor;
import org.apache.xpath.functions.Function;
import org.apache.xpath.functions.XSL3FunctionService;
import org.apache.xpath.functions.XSLFunctionBuilder;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XBooleanStatic;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;
import org.apache.xpath.operations.Range;
import org.apache.xpath.operations.SimpleMapOperator;
import org.apache.xpath.operations.Variable;
import org.apache.xpath.patterns.NodeTest;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.Node;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

import xml.xpath31.processor.types.XSAnyAtomicType;
import xml.xpath31.processor.types.XSAnyType;
import xml.xpath31.processor.types.XSBoolean;
import xml.xpath31.processor.types.XSDouble;
import xml.xpath31.processor.types.XSNumericType;
import xml.xpath31.processor.types.XSUntyped;
import xml.xpath31.processor.types.XSUntypedAtomic;

/**
 * This class, defines few utility methods, that provide support for
 * Xalan-J's XSL 3 transformation processor implementation.
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
        
        if (xObject instanceof XMLNodeCursorImpl) {
           XMLNodeCursorImpl nodeSet = (XMLNodeCursorImpl)xObject;
            
           DTMCursorIterator dtmIter = nodeSet.iterRaw();
           int nextNode;
           while ((nextNode = dtmIter.nextNode()) != DTM.NULL) {
              XMLNodeCursorImpl xdmNode = new XMLNodeCursorImpl(nextNode, xctxt);
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
     * Method definition to convert an XDM sequence to a node set.
     */
    public static XMLNodeCursorImpl getXNodeSetFromResultSequence(ResultSequence resultSeq, DTMManager dtmMgr) {
        
        XMLNodeCursorImpl nodeSet = null;
        
        List<Integer> dtmNodeHandleList = new ArrayList<Integer>();
        
        for (int idx = 0; idx < resultSeq.size(); idx++) {
           XObject nodeSetItem = resultSeq.item(idx);
           if (nodeSetItem instanceof XMLNodeCursorImpl) {
              int nodeDtmHandle = (((XMLNodeCursorImpl)nodeSetItem).iter()).nextNode();
              dtmNodeHandleList.add(nodeDtmHandle);
           }
           else {
              break; 
           }
        }
        
        if (dtmNodeHandleList.size() == resultSeq.size()) {
           nodeSet = new XMLNodeCursorImpl(dtmNodeHandleList, dtmMgr);
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
            
           DTMCursorIterator nodes = expr.asIterator(xctxt, xctxt.getCurrentNode());

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
            if (evalResult instanceof XMLNodeCursorImpl) {
                xdmSequenceSize = ((XMLNodeCursorImpl)evalResult).getLength();   
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
           if (evalResult instanceof XMLNodeCursorImpl) {
               xdmSequenceSize = ((XMLNodeCursorImpl)evalResult).getLength();   
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
                DTMCursorIterator nl = expr.asIterator(xctxt, xctxt.getCurrentNode());
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
          XObject resultSeqItem = resultSeq.item(idx);
          if ((resultSeqItem instanceof XSUntyped) && (srch instanceof XSUntyped)) {
             if (((XSUntyped)resultSeqItem).equals((XSUntyped)srch, collationUri, xpathCollationSupport)) {
                 isSeqContains = true;
                 break;    
             }
          }
          else if ((resultSeqItem instanceof XSUntypedAtomic) && (srch instanceof XSUntypedAtomic)) {
              if (((XSUntypedAtomic)resultSeqItem).equals((XSUntypedAtomic)srch, collationUri, xpathCollationSupport)) {
                 isSeqContains = true;
                 break;    
              } 
          }
          else if ((resultSeqItem instanceof XSUntyped) && (srch instanceof XSUntypedAtomic)) {
              if (((XSUntyped)resultSeqItem).equals((XSUntypedAtomic)srch, collationUri, xpathCollationSupport)) {
                 isSeqContains = true;
                 break;    
              } 
          }
          else if ((resultSeqItem instanceof XSUntypedAtomic) && (srch instanceof XSUntyped)) {
              if (((XSUntypedAtomic)resultSeqItem).equals((XSUntyped)srch, collationUri, xpathCollationSupport)) {
                 isSeqContains = true;
                 break;    
              }
          }
          else if ((resultSeqItem instanceof XSNumericType) && (srch instanceof XSNumericType)) {
        	  // When comparing numeric values, collationUri is not used
        	  String lStr = ((XSNumericType)resultSeqItem).stringValue();
         	  XSDouble lDouble = new XSDouble(lStr);
         	  
         	  String rStr = ((XSNumericType)srch).stringValue();
        	  XSDouble rDouble = new XSDouble(rStr);
        	  
        	  if (lDouble.equals(rDouble)) {
        		 isSeqContains = true;
                 break;  
        	  }
          }
          else if ((resultSeqItem instanceof XSNumericType) && (srch instanceof XNumber)) {
        	  // When comparing numeric values, collationUri is not used
        	  String lStr = ((XSNumericType)resultSeqItem).stringValue();
         	  XSDouble lDouble = new XSDouble(lStr);
         	  
         	  double rdbl = ((XNumber)srch).num();
         	  XSDouble rDouble = new XSDouble(rdbl);
         	  
         	 if (lDouble.equals(rDouble)) {
        		isSeqContains = true;
                break;  
        	 }
          }
          else if ((resultSeqItem instanceof XNumber) && (srch instanceof XSNumericType)) {
        	 // When comparing numeric values, collationUri is not used
        	 double ldbl = ((XNumber)resultSeqItem).num();
          	 XSDouble lDouble = new XSDouble(ldbl);
          	  
          	 String rStr = ((XSNumericType)srch).stringValue();
         	 XSDouble rDouble = new XSDouble(rStr);
          	  
         	 if (lDouble.equals(rDouble)) {
          		isSeqContains = true;
                break;  
          	 } 
         }
         else if ((resultSeqItem instanceof XNumber) && (srch instanceof XNumber)) {
        	 // When comparing numeric values, collationUri is not used
        	 double num1 = ((XNumber)resultSeqItem).num();
        	 double num2 = ((XNumber)srch).num();

        	 if ((num1 == num2) || (Double.isNaN(num1) && Double.isNaN(num2))) {
        		isSeqContains = true;
        		break; 
        	 }
         }
         else if ((resultSeqItem instanceof XSAnyType) && (srch instanceof XSAnyType)) {
             if (((XSAnyType)resultSeqItem).equals((XSAnyType)srch, collationUri, xpathCollationSupport)) {
                isSeqContains = true;
                break;    
             }   
         }
         else if (resultSeqItem.equals(srch, collationUri, xpathCollationSupport)) {
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
        } 
        finally {
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
     * This method definition, is used to check whether the supplied string 
     * value has balanced parentheses pairs.
     */
    public static boolean isStrHasBalancedParentheses(String str, char lParentType, char rParenType) {
       
       boolean isStrHasBalancedParentheses = true;
       
       Stack<Character> charStack = new Stack<Character>();
       
       int strLen = str.length();
       
       for(int idx = 0; idx < strLen; idx++) {
           char ch = str.charAt(idx);
           if (ch == lParentType) {
              charStack.push(ch); 
           }
           else if (ch == rParenType){
              if (charStack.isEmpty() || (charStack.pop() != lParentType)) {
                 // unbalanced parentheses
                 isStrHasBalancedParentheses = false;
                 break;
              }   
           }
       }
       
       if (!charStack.isEmpty()) {
          isStrHasBalancedParentheses = false;
       }
       
       return isStrHasBalancedParentheses; 
    }
    
    /**
     * Method definition to check whether, all the values of the 
     * supplied input sequence are XPath atomic values.
     * 
     * @param resultSeq				The supplied XPath sequence object
     * @return						boolean value true or false
     */
    public static boolean isSequenceContainsAllXDMAtomicValues(ResultSequence resultSeq) {
    	
    	boolean result = true;
    	
    	for (int idx = 0; idx < resultSeq.size(); idx++) {
    		XObject seqItem = resultSeq.item(idx);
    		if (!((seqItem instanceof XNumber) || (seqItem instanceof XBooleanStatic) || (seqItem instanceof XBoolean) || 
						        				  (seqItem instanceof XSBoolean) || (seqItem instanceof XString) || 
						        				  (seqItem instanceof XSAnyAtomicType))) {
    			result = false;

    			break;
    		}
    	}
    	
    	return result;
    }
    
    /**
     * This method definition does the xsl:character-map transformation
     * on the supplied string value, using a CharacterMapConfig 
     * run-time object. 
     * 
     * @param strValue					Supplied string value
     * @param charMapConfig				CharacterMapConfig run-time object
     * @return							The modified string value after doing
     *                                  xsl:character-map transformation.
     */
    public static String characterMapTransformation(String strValue, CharacterMapConfig charMapConfig) {    	
    	
    	String result = strValue;    	
    	
    	Map<Integer, String> hashMap1 = charMapConfig.getCharMap();
    	Set<Integer> charSet = hashMap1.keySet();
    	Iterator<Integer> iter = charSet.iterator();    	
    	while (iter.hasNext()) {
    		Integer targetCodePoint = iter.next();
    		String replacementStr = hashMap1.get(targetCodePoint);    	    		    		    		
    		StringBuffer strBuffer = new StringBuffer();
    		IntStream resultCodePointIntStream = result.codePoints();
    		int[] resultCodePointArr = resultCodePointIntStream.toArray();
    		for (int idx = 0; idx < resultCodePointArr.length; idx++) {
    			int codePoint = resultCodePointArr[idx];
    			if (codePoint == targetCodePoint) { 
    				strBuffer.append(replacementStr);
    			} 
    			else {
    				strBuffer.appendCodePoint(codePoint);
    			}
    		}
    		
    		result = strBuffer.toString(); 
    	}

    	return result;
    }
    
    /**
     * Function definition to get xsl:function's compiled ElemFunction object, 
     * given a NodeTest expression.  
     * 
     * @param nodeTest							   A NodeTest object instance constructed from XPath 
     *                                             named function reference like fn0:abc#1.
     * @param transformerImpl					   An XSL transform TransformerImpl object
     * @param srcLocator						   SourceLocator object in XPath context
     * @return									   An ElemFunction object if available, otherwise null
     * 
     * @throws javax.xml.transform.TransformerException
     */
    public static ElemFunction getElemFunctionFromNodeTestExpression(NodeTest nodeTest, TransformerImpl transformerImpl, 
  		                                                             SourceLocator srcLocator) throws javax.xml.transform.TransformerException {

  	  ElemFunction result = null;

  	  String funcNameRef = nodeTest.getLocalName();
  	  String funcNamespace = nodeTest.getNamespace();

  	  ExpressionNode expressionNode = nodeTest.getExpressionOwner();
  	  ExpressionNode stylesheetRootNode = null;
  	  while (expressionNode != null) {
  		  stylesheetRootNode = expressionNode;
  		  expressionNode = expressionNode.exprGetParent();                     
  	  }

  	  StylesheetRoot stylesheetRoot = (StylesheetRoot)stylesheetRootNode;  	    	  

  	  if (stylesheetRoot != null) {
  		  transformerImpl = stylesheetRoot.getTransformerImpl();  
  		  TemplateList templateList = stylesheetRoot.getTemplateListComposed();  		  
  		  XSL3FunctionService xslFunctionService = XSLFunctionBuilder.getXSLFunctionService();  		  
  		  if (xslFunctionService.isFuncArityWellFormed(funcNameRef)) {        	   
  			  int hashCharIdx = funcNameRef.indexOf('#');
  			  String funcNameRef2 = funcNameRef.substring(0, hashCharIdx);
  			  int funcArity = Integer.valueOf(funcNameRef.substring(hashCharIdx + 1));        		   
  			  ElemTemplate elemTemplate = templateList.getXslFunction(new QName(funcNamespace, funcNameRef2), funcArity);        		   
  			  if (elemTemplate != null) {
  				  result = (ElemFunction)elemTemplate;
  				  int xslFuncDefnParamCount = result.getArity();                      
  				  String str = funcNameRef.substring(hashCharIdx + 1);
  				  int funcRefParamCount = (Integer.valueOf(str)).intValue();
  				  if (funcRefParamCount != xslFuncDefnParamCount) {
  					  throw new javax.xml.transform.TransformerException("FORG0006 : An XPath named function reference " + funcNameRef + " cannot resolve to a function "
  																													                 + "definition.", srcLocator); 
  				  }
  			  }
  		  }
  		  else {
  			  throw new javax.xml.transform.TransformerException("FORG0006 : An XPath named function reference " + funcNameRef + " cannot resolve to a function "
  																											                 + "definition.", srcLocator);
  		  }
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
