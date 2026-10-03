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
package org.apache.xpath.functions.string;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.FunctionOneArg;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;

import xml.xpath31.processor.types.XSNumericType;

/**
 * Implementation of an XPath 3.1 function fn:codepoints-to-string.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncCodePointsToString extends FunctionOneArg {

    private static final long serialVersionUID = -4531182517520672452L;
    
    /**
	 * Class constructor.
	 */
	public FuncCodePointsToString() {
	    m_arity = new Short[] { 1 };
	}

	/**
	 * Evaluate the function. The function must return a valid object.
	 * 
	 * @param xctxt 					    An XPath context object
	 * @return 								A valid XObject
	 *
	 * @throws javax.xml.transform.TransformerException
	 */
    public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
    {
        XObject result = null;
        
        SourceLocator srcLocator = xctxt.getSAXLocator(); 
        
        XObject xObj = getFunctionArgEffectiveValue(m_arg0, xctxt);
        
        boolean isXml11Support = xctxt.isXML11Support();
        
        if (!isXml11Support) {
        	if (xObj instanceof ResultSequence) {
        		ResultSequence rSeq = (ResultSequence)xObj;

        		int size1 = rSeq.size();
        		
        		/**
        		 * The codepoint values 1 upto 31, refer to the control
        		 * characters. The following code, checks for control 
        		 * character validation for the supplied function argument.
        		 * 
        		 * XML 1.0 doesn't allow control characters within 
        		 * string values.
        		 */

        		for (int idx = 0; idx < size1; idx++) {
        			XObject xObj2 = rSeq.item(idx);

        			if (xObj2 instanceof XNumber) {
        				XNumber xNumber = (XNumber)xObj2;

        				xObj2 = XslTransformEvaluationHelper.getXNumberNormalizedValue(xNumber);
        			}

        			int codePoint1 = 0;

        			if (xObj2 instanceof XNumber) {
        				double dbl = ((XNumber)xObj2).num();        		 

        				codePoint1 = (int)dbl;

        				if (codePoint1 == dbl) {
                           if ((codePoint1 >= 1) && (codePoint1 <= 31)) {
                        	  throw new TransformerException("FOCH0001 : An XPath 3.1 function 'codepoints-to-string' refers, to a codepoint "
                        	  		                                                                                                         + "value " + codePoint1 + " "
                        	  		                                                                                                         + "that is not allowed by XML 1.0.", srcLocator);
                           }
        				}
        				else {
        				   throw new TransformerException("XPTY0004 : An XPath 3.1 function 'codepoints-to-string' refers, to a non-integer code point value.", srcLocator); 
        				}
        			}
        			else if (xObj2 instanceof XSNumericType) {
        				String str1 = XslTransformEvaluationHelper.getStrVal(xObj2);
        				
        				double dbl = Double.valueOf(str1);
        				
        				codePoint1 = (int)dbl;

        				if (codePoint1 == dbl) {
        					if ((codePoint1 >= 1) && (codePoint1 <= 31)) {
        						throw new TransformerException("FOCH0001 : An XPath 3.1 function 'codepoints-to-string' refers, to a codepoint "
																									        								+ "value " + codePoint1 + " "
																									        								+ "that is not allowed by XML 1.0.", srcLocator);
        					}
        				}
        				else {
        					throw new TransformerException("XPTY0004 : An XPath 3.1 function 'codepoints-to-string' refers, to a non-integer code point value.", srcLocator); 
        				}
        			}
        			else {
        				throw new TransformerException("XPTY0004 : An XPath 3.1 function 'codepoints-to-string' refers, to a non-numeric code point value.", srcLocator);
        			}
        		}
        	}
        }
        
        String resultStr = getStringFromXObject(xObj, xctxt);
        
        result = new XString(resultStr);
        
        return result;
    }
    
    /**
     * Method definition, to convert the supplied XObject object instance,
     * to a string value, to be returned as function fn:codepoints-to-string's
     * result.  
     * 
     * @param xObj                            The supplied XObject object
     *                                        instance. 
     * @param xctxt                           An XPath context object
     * @return                                The string value
     * @throws javax.xml.transform.TransformerException
     */
    private String getStringFromXObject(XObject xObj, XPathContext xctxt) 
    		                                                            throws javax.xml.transform.TransformerException {
       
       String result = null;
       
       StringBuffer strBuff = new StringBuffer();
       
       SourceLocator srcLocator = xctxt.getSAXLocator();
       
       ResultSequence rSeq = null;
       
       if (xObj instanceof ResultSequence) {
           rSeq = (ResultSequence)xObj;
           
           int size1 = rSeq.size();
           
           for (int idx = 0; idx < size1; idx++) {
              XObject xobj0 = rSeq.item(idx);
              
              if (xobj0 instanceof XNumber) {
                 XNumber xNum = (XNumber)xobj0;
                 double dblVal = xNum.num();
                 
                 if (dblVal == (int)dblVal) {
                    char[] charArr = Character.toChars((int)dblVal);                    
                    strBuff.append(String.valueOf(charArr));
                 }
                 else {
                    throw new TransformerException("FORG0006 : An XPath 3.1 function 'codepoints-to-string' is supplied, with a non-integer codepoint value.", srcLocator);    
                 }
              }
              else if (xobj0 instanceof XSNumericType) {
                 String str1 = ((XSNumericType)xobj0).stringValue();
                 double dbl = (Double.valueOf(str1)).doubleValue();
                 
                 if (dbl == (int)dbl) {
                    char[] charArr = Character.toChars((int)dbl);
                    strBuff.append(String.valueOf(charArr));
                 }
                 else {
                	throw new TransformerException("FORG0006 : An XPath 3.1 function 'codepoints-to-string' is supplied, with a non-integer codepoint value.", srcLocator);   
                 }
              }
              else if (xobj0 instanceof XMLNodeCursorImpl) {
                 XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)xobj0;
                 
                 if (xmlNodeCursorImpl.getLength() == 1) {
                    String str1 = xmlNodeCursorImpl.str();
                    double dblVal = (Double.valueOf(str1)).doubleValue();
                    
                    if (dblVal == (int)dblVal) {
                       char[] charArr = Character.toChars((int)dblVal);
                       strBuff.append(String.valueOf(charArr)); 
                    }
                    else {
                       throw new TransformerException("FORG0006 : An XPath 3.1 function 'codepoints-to-string' is supplied, with a non-integer codepoint value.", srcLocator);   
                    }
                 }
                 else {
                	throw new TransformerException("FORG0006 : An XPath 3.1 function 'codepoints-to-string' is supplied, with a sequence that has an xdm node with size not equal to one.", srcLocator); 
                 }
              }
              else {
                 String itemStrVal = xobj0.str();
                 try {
                    double dblVal = (Double.valueOf(itemStrVal)).doubleValue();
                    
                    if (dblVal == (int)dblVal) {
                       char[] charArr = Character.toChars((int)dblVal);
                       strBuff.append(String.valueOf(charArr)); 
                    }
                    else {
                       throw new TransformerException("FORG0006 : An XPath 3.1 function 'codepoints-to-string' is supplied, with a non-integer codepoint value.", srcLocator);   
                    }
                 }
                 catch (NumberFormatException ex) {
                	throw new TransformerException("FORG0006 : An XPath 3.1 function 'codepoints-to-string' is supplied, with a non-numeric codepoint value.", srcLocator);
                 }
              }
           }
        }
        else if (xObj instanceof XMLNodeCursorImpl) {            
           XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)xObj;           
           DTMCursorIterator dtmCursorIterator = xmlNodeCursorImpl.iter();
            
           int nextNode = DTM.NULL;
           
           while ((nextNode = dtmCursorIterator.nextNode()) != DTM.NULL) {
               XMLNodeCursorImpl xNodeSetItem = new XMLNodeCursorImpl(nextNode, xctxt);
               String nodeStrValue = xNodeSetItem.str();
               
               double dblVal = (Double.valueOf(nodeStrValue)).doubleValue();
               
               if (dblVal == (int)dblVal) {
                  char[] charArr = Character.toChars((int)dblVal);
                  strBuff.append(String.valueOf(charArr));  
               }
               else {
            	  throw new TransformerException("FORG0006 : An XPath 3.1 function 'codepoints-to-string' is supplied, with a non-integer codepoint value.", srcLocator);   
               }
           }
        }
        else {
           String str1 = xObj.str();
           
           double dbl = (Double.valueOf(str1)).doubleValue();
           
           if (dbl == (int)dbl) {
              char[] charArr = Character.toChars((int)dbl);
              strBuff.append(String.valueOf(charArr));
           }
           else {
        	  throw new TransformerException("FORG0006 : An XPath 3.1 function 'codepoints-to-string' is supplied, with a non-integer codepoint value.", srcLocator);   
           }
        }
       
        result = strBuff.toString(); 
       
        return result; 
    }

}
