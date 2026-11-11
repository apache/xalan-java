/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.xpath.functions;

import java.math.BigDecimal;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.templates.Constants;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xpath.Expression;
import org.apache.xpath.XPathContext;
import org.apache.xpath.axes.SelfIteratorNoPredicate;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XBooleanStatic;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;

import xml.xpath31.processor.types.XSAnyURI;
import xml.xpath31.processor.types.XSBoolean;
import xml.xpath31.processor.types.XSNumericType;
import xml.xpath31.processor.types.XSString;
import xml.xpath31.processor.types.XSUntypedAtomic;

/**
 * Implementation of an XPath 3.1 function fn:boolean.
 */
public class FuncBoolean extends FunctionOneArg
{
    static final long serialVersionUID = 4328660760070034592L;
    
    /**
     * Default constructor.
     */
    public FuncBoolean() {
    	m_arity = new Short[] { 1 };	
    }
    
    /**
     * Class constructor.
     */
    public FuncBoolean(Expression arg) {
       m_arg0 = arg; 
    }

    /**
     * Evaluate the function. The function must return a valid object.
     * 
     * @param xctxt                        An XPath context object
     * @return                             A valid XObject
     *
     * @throws javax.xml.transform.TransformerException
     */
    public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
    {
    	
    	XObject result = null;

    	SourceLocator srcLocator = xctxt.getSAXLocator();

    	XObject xObj = null;
    	
    	if ((m_arg0 instanceof SelfIteratorNoPredicate) && (xctxt.getXPath3ContextItem() == null) 
    			                                        && (xctxt.getContextNode() == DTM.NULL)) {
    	   throw new TransformerException("XPDY0002 : An XPath 3.1 function 'boolean' has been called "
    	   		                                                                                      + "with context item as absent.", srcLocator); 
    	}

    	try {
    		xObj = getFunctionArgEffectiveValue(m_arg0, xctxt);
    	}
    	catch (TransformerException ex) {    	   
    		String errMesg = ex.getMessage();
    		
    		if ((errMesg != null) && (errMesg.startsWith("FOAR0001"))) {
    			// XPath division by zero error    			
    			result = new XSBoolean(true);

    			return result; 
    		}
    		else {
    			throw ex;
    		}
    	}
    	
    	if (xObj instanceof XNumber) {
    	   xObj = XslTransformEvaluationHelper.getXNumberNormalizedValue((XNumber)xObj);
    	}
    	
    	if (xObj instanceof XString) {
    		XString xString = (XString)xObj;
    		
    		if (xString.getXrTreeFragSelectWrapperResult()) {
    			result = new XSBoolean(true);  
    		}
    		else if (xString.length() > 0) {
    			result = new XSBoolean(true);
    		}
    		else {
    			result = new XSBoolean(false);
    		}
    	}    	
    	else if (xObj instanceof ResultSequence) {
    		ResultSequence rSeq = (ResultSequence)xObj;
    		
    		int size1 = rSeq.size();
    		
    		if (size1 == 0) {
    			result = new XSBoolean(false);
    		}
    		else if ((size1 > 1) && ((rSeq.item(0) instanceof XSNumericType) || (rSeq.item(0) instanceof XNumber))) {
    			throw new TransformerException("FORG0006 : An XPath 3.1 function 'boolean' is called with an argument, that "
    					                                                                                                    + "is a sequence with "
    					                                                                                                    + "the first item as numeric.", srcLocator);
    		}
    		else if ((size1 > 1) && ((rSeq.item(0) instanceof XSBoolean) || (rSeq.item(0) instanceof XBoolean) 
    				                                                     || (rSeq.item(0) instanceof XBooleanStatic))) {
    			throw new TransformerException("FORG0006 : An XPath 3.1 function 'boolean' is called with an argument, that "
    					                                                                                                    + "is a sequence with "
    					                                                                                                    + "the first item as boolean.", srcLocator);
    		}
    		else if ((size1 > 1) && ((rSeq.item(0) instanceof XSString) || (rSeq.item(0) instanceof XString))) {
                throw new TransformerException("FORG0006 : An XPath 3.1 function 'boolean' is called with an argument, that "
                                                                                                                            + "is a sequence with "
                                                                                                                            + "the first item as string.", srcLocator);
            }
    		else {
    			XObject xObj1 = rSeq.item(0);    			
    			
    			if (xObj1 instanceof XMLNodeCursorImpl) {
    				XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)xObj1;
    				
    				int size2 = ((XMLNodeCursorImpl)xmlNodeCursorImpl).getLength();
    				
    				if (size2 == 0) {
    	    			result = new XSBoolean(false);
    	    		}
    				else if (size2 == 1) {
    	    			if (xmlNodeCursorImpl.isTransformedAtomicValue()) {
    	    				String strValue = xmlNodeCursorImpl.str();
    	    				
    	    				if ("".equals(strValue)) {
    	    					result = new XSBoolean(false); 
    	    				}
    	    				else {
    	    					result = new XSBoolean(true); 
    	    				}

    	    				xmlNodeCursorImpl.setIsTransformedAtomicValue(false);
    	    			}
    	    			else {
    	    				result = new XSBoolean(true);  
    	    			}
    	    		}
    	    		else {
    	    			result = new XSBoolean(true);
    	    		}
    			}
    			else if ((size1 == 1) && ((xObj1 instanceof XSBoolean) || (xObj1 instanceof XBoolean) 
    					                                               || (xObj1 instanceof XBooleanStatic))) {
    				result = new XSBoolean(xObj1.bool());
    			}
    		}
    	}
    	else if (xObj instanceof XMLNodeCursorImpl) {
    		int size1 = ((XMLNodeCursorImpl)xObj).getLength();
    		
    		if (size1 == 0) {
    			result = new XSBoolean(false);
    		}
    		else if (size1 == 1) {
    			XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)xObj;
    			
    			if (xmlNodeCursorImpl.isTransformedAtomicValue()) {
    				String strValue = xmlNodeCursorImpl.str();
    				
    				if ("".equals(strValue)) {
    					result = new XSBoolean(false); 
    				}
    				else {
    					result = new XSBoolean(true); 
    				}

    				xmlNodeCursorImpl.setIsTransformedAtomicValue(false);
    			}
    			else {
    				result = new XSBoolean(true);  
    			}
    		}
    		else {
    			result = new XSBoolean(true);
    		}
    	}
    	
    	else if ((xObj instanceof XSBoolean) || (xObj instanceof XBoolean) || (xObj instanceof XBooleanStatic)) {
    	    result = new XSBoolean(xObj.bool());
    	}    	
        else if ((xObj instanceof XSString) || (xObj instanceof XSAnyURI) || (xObj instanceof XSUntypedAtomic)) {
    		String str1 = XslTransformEvaluationHelper.getStrVal(xObj);
    		
    		if (str1.length() > 0) {
    			result = new XSBoolean(true);
    		}
    		else {
    			result = new XSBoolean(false);
    		}
    	}    	
        else if (xObj instanceof XSNumericType) {
        	String str1 = XslTransformEvaluationHelper.getStrVal(xObj);        	
        	
        	if ((Constants.ATTRNAME_NAN).equals(str1)) {
        		result = new XSBoolean(false);
        	}			
        	else if ((Constants.POSITIVE_INF_STR).equals(str1) || (Constants.NEGATIVE_INF_STR).equals(str1)) {
        		result = new XSBoolean(true);
        	}
        	else {
        		BigDecimal bigDecimal = new BigDecimal(str1);

        		int bigDecimalScale = bigDecimal.scale();
        		BigDecimal bigDecimalZero = BigDecimal.valueOf(0);
        		bigDecimalZero.setScale(bigDecimalScale);

        		int cmpResult = bigDecimal.compareTo(bigDecimalZero);

        		if (cmpResult != 0) {
        			result = new XSBoolean(true);				
        		}
        		else {
        			result = new XSBoolean(false);
        		}
        	}
		}
        else if (xObj instanceof XNumber) {
        	XNumber xNumber = (XNumber)xObj;        	
        	Double dbl = xNumber.num();

        	if (dbl.isNaN()) {
        		result = new XSBoolean(false);
        	}
        	else if (Double.isInfinite(dbl)) {
        		result = new XSBoolean(true);
        	}
        	else if (dbl == 0) {
        		result = new XSBoolean(false);
        	}
        	else {
        		result = new XSBoolean(true);
        	}
        }    	
        else {
    		throw new TransformerException("FORG0006 : An XPath 3.1 function 'boolean' is called with an argument, "
    				                                                                                               + "that doesn't have effective "
    				                                                                                               + "boolean value defined.", srcLocator);
    	}
    	
    	return result;
    }
  
}
