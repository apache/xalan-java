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
package org.apache.xpath.functions.math;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.Function2Args;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathInlineFunction;
import org.apache.xpath.patterns.NodeTest;

import xml.xpath31.processor.types.XSDouble;
import xml.xpath31.processor.types.XSFloat;
import xml.xpath31.processor.types.XSNumericType;

/**
 * Implementation of XPath 3.1 function math:atan2.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncMathAtan2 extends Function2Args {

    private static final long serialVersionUID = 1342863964649663483L;
    
    /**
	 * Class constructor.
	 */
	public FuncMathAtan2() {
		m_defined_arity = new Short[] { 2 };
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
        
        if ((m_arg0 == null) || (m_arg1 == null)) {
            throw new TransformerException("XPST0017 : An XPath 3.1 function call math 'atan2' requires two arguments.", srcLocator);
        } 
        
        if (m_arg0 instanceof NodeTest) {
        	if (XslTransformEvaluationHelper.isNodeTestExpressionFuntionType((NodeTest)m_arg0)) {
        		throw new javax.xml.transform.TransformerException("FOTY0013 : An xdm atomic value is required for the first argument of XPath 3.1 function math 'atan2', "
																							        				+ "but the supplied type is a "
																							        				+ "function type, which cannot be atomized.", srcLocator); 
        	}
        }
        else if (m_arg0 instanceof XPathInlineFunction) {
        	if (XslTransformEvaluationHelper.isNodeTestExpressionFuntionType((NodeTest)m_arg0)) {
        		throw new javax.xml.transform.TransformerException("FOTY0013 : An xdm atomic value is required for the first argument of XPath 3.1 function math 'atan2', "
																							        				+ "but the supplied type is a "
																							        				+ "function type, which cannot be atomized.", srcLocator); 
        	}
        }

        if (m_arg1 instanceof NodeTest) {
        	if (XslTransformEvaluationHelper.isNodeTestExpressionFuntionType((NodeTest)m_arg1)) {
        		throw new javax.xml.transform.TransformerException("FOTY0013 : An xdm atomic value is required for the second argument of XPath 3.1 function math 'atan2', "
																							        				+ "but the supplied type is a "
																							        				+ "function type, which cannot be atomized.", srcLocator); 
        	}
        }
        else if (m_arg1 instanceof XPathInlineFunction) {
        	if (XslTransformEvaluationHelper.isNodeTestExpressionFuntionType((NodeTest)m_arg1)) {
        		throw new javax.xml.transform.TransformerException("FOTY0013 : An xdm atomic value is required for the second argument of XPath 3.1 function math 'atan2', "
																							        				+ "but the supplied type is a "
																							        				+ "function type, which cannot be atomized.", srcLocator); 
        	}
        }
        
        XObject arg0Obj = null;

    	try {
    		arg0Obj = getFunctionEffectiveArgValue(m_arg0, xctxt);
    	}
    	catch (Exception ex) {		   
    		throw new TransformerException("XPTY0004 : An XPath 3.1 function call math 'atan2' first argument is not an XML Schema type double.", srcLocator);
    	}    	
    	
    	double lDbl = getDoubleValue(arg0Obj, srcLocator, "first");
    	
    	if (Double.isNaN(lDbl)) {
    	   result = new XSDouble(Double.NaN);
    	   
    	   return result;
    	}
    	
    	XObject arg1Obj = null;

        try {
        	arg1Obj = getFunctionEffectiveArgValue(m_arg1, xctxt);
        }
        catch (Exception ex) {		   
        	throw new TransformerException("XPTY0004 : An XPath 3.1 function call math 'atan2' second argument is not numeric.", srcLocator);
        }        
    	
    	double rDbl = getDoubleValue(arg1Obj, srcLocator, "second");
    	
    	if (Double.isNaN(rDbl)) {
     	   result = new XSDouble(Double.NaN);
     	   
     	   return result;
     	}
    	    	
    	result = new XSDouble(Math.atan2(lDbl, rDbl));
        
        return result;
    }
    
    /**
     * Method definition, to get a primitive double value, from 
     * an XObject object instance.
     * 
     * @param xObject                        The supplied XObject, object instance 
     * @param srcLocator                     An XSL transformation source locator object
     * @param argNumStr                      An function argument number description
     * @return                               A primitive double, value
     * @throws javax.xml.transform.TransformerException
     */
    private double getDoubleValue(XObject xObject, SourceLocator srcLocator, String argNumStr) 
                                                                                 throws javax.xml.transform.TransformerException {
        
        double result = 0.0;
        
        if (xObject instanceof XNumber) {        	
           XNumber xNumber = (XNumber)xObject;
           
           if (xNumber.getXsDecimal() != null) {
        	   String strVal = (xNumber.getXsDecimal()).stringValue();
        	   
        	   result = Double.valueOf(strVal);
           }
           else if (xNumber.getXsInteger() != null) {
        	   String strVal = (xNumber.getXsInteger()).stringValue();
        	   
        	   result = Double.valueOf(strVal);
           }
           else if (xNumber.getXsDouble() != null) {
        	   result = (xNumber.getXsDouble()).doubleValue(); 
           }
           else {
        	   result = xNumber.num(); 
           }
        }
        else if (xObject instanceof XSDouble) {
           XSDouble xsDouble = (XSDouble)xObject;
           
           result = xsDouble.doubleValue();  
        }
        else if (xObject instanceof XSFloat) {
           XSFloat xsFloat = (XSFloat)xObject;
           
           result = xsFloat.floatValue(); 
        }
        else if (xObject instanceof XSNumericType) {
           String strVal = ((XSNumericType)xObject).stringValue();
                                 
           result = Double.valueOf(strVal);
        }
        else if (xObject instanceof XMLNodeCursorImpl) {
        	XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)xObject;

        	if (xmlNodeCursorImpl.getLength() > 0) {
        		String strVal = xmlNodeCursorImpl.str();                          
        		
        		try {
        			result = Double.valueOf(strVal);
        		}
        		catch (NumberFormatException ex) {
        			throw new TransformerException("XPTY0004 : An XPath 3.1 function call math 'atan2' " + argNumStr + " argument is not an XML Schema type double.", srcLocator);
        		} 
        	}          
        }
        else if (xObject instanceof ResultSequence) {
            ResultSequence rSeq = (ResultSequence)xObject;
            
            if (rSeq.size() == 1) {
               XObject xObj = rSeq.item(0); 
               
               String strVal = XslTransformEvaluationHelper.getStrVal(xObj);              
               
               try {
             	  result = Double.valueOf(strVal);
               }
               catch (NumberFormatException ex) {
            	  throw new TransformerException("XPTY0004 : An XPath 3.1 function call math 'atan2' " + argNumStr + " argument is not an XML Schema type double.", srcLocator);
               }
            }
            else if (rSeq.size() > 1) {
         	   throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath 3.1 function math 'atan2', requires xdm sequence arguments of "
         	   		                                                                                               + "size one. An XPath 3.1 function math 'atan2' " + argNumStr 
         	   		                                                                                               + " argument is a sequence of size " + rSeq.size() + ".", srcLocator); 
            }
        }
        else {
        	throw new javax.xml.transform.TransformerException("XPTY0004 : An xdm item type of " + argNumStr + " argument to XPath 3.1 function call math 'atan2' is not "
																														  + "an XML Schema type double.", srcLocator); 
        }
        
        return result;
        
    }

}
