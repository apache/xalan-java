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
package org.apache.xpath.functions.math;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.FunctionOneArg;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathInlineFunction;
import org.apache.xpath.patterns.NodeTest;

import xml.xpath31.processor.types.XSDouble;
import xml.xpath31.processor.types.XSNumericType;

/**
 * Implementation of XPath 3.1 function math:atan.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncMathAtan extends FunctionOneArg
{

    private static final long serialVersionUID = 7279664663962212944L;
    
    /**
	 * Class constructor.
	 */
	public FuncMathAtan() {
		m_defined_arity = new Short[] { 1 };
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

    	if (m_arg0 == null) {
    		throw new TransformerException("XPST0017 : An XPath 3.1 function call math 'atan' has been called with no argument.", srcLocator);
    	}
    	else if (m_arg0 instanceof NodeTest) {
    		if (XslTransformEvaluationHelper.isNodeTestExpressionFuntionType((NodeTest)m_arg0)) {
    			throw new javax.xml.transform.TransformerException("FOTY0013 : An xdm atomic value is required for the first argument of XPath 3.1 function math 'atan', "
																								    					+ "but the supplied type is a "
																								    					+ "function type, which cannot be atomized.", srcLocator); 
    		}
    	}
    	else if (m_arg0 instanceof XPathInlineFunction) {
    		throw new javax.xml.transform.TransformerException("FOTY0013 : An xdm atomic value is required for the first argument of XPath 3.1 function math 'atan', but the "
																									    				+ "supplied type is a function type, "
																									    				+ "which cannot be atomized.", srcLocator); 
    	}
           
        XObject arg0Obj = null;

        try {
        	arg0Obj = getFunctionEffectiveArgValue(m_arg0, xctxt);
        }
        catch (Exception ex) {		   
        	throw new TransformerException("XPTY0004 : An XPath 3.1 function call math 'atan' argument is not an XML Schema type double.", srcLocator);
        }
        
        if (arg0Obj instanceof XNumber) {
        	double dbl = Math.atan(((XNumber)arg0Obj).num());

        	result = new XSDouble(dbl);
        }
        else if (arg0Obj instanceof XSNumericType) {
        	String strVal = ((XSNumericType)arg0Obj).stringValue();

        	double dbl = Math.atan((new XSDouble(strVal)).doubleValue());

        	result = new XSDouble(dbl);
        }
        else if (arg0Obj instanceof XMLNodeCursorImpl) {
        	XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)arg0Obj;

        	if (xmlNodeCursorImpl.getLength() == 0) {
        		result = new ResultSequence(); 
        	}          
        	else {
        		String strVal = xmlNodeCursorImpl.str();                          
        		double dbl = 0.0;

        		try {
        			dbl = Double.valueOf(strVal);
        		}
        		catch (NumberFormatException ex) {
        			throw new TransformerException("XPTY0004 : An XPath 3.1 function call math 'atan' argument is not an XML Schema type double.", srcLocator);
        		}

        		result = new XSDouble(Math.atan(dbl));
        	}
        }
        else if (arg0Obj instanceof ResultSequence) {
            ResultSequence rSeq = (ResultSequence)arg0Obj;
            
            if (rSeq.size() == 0) {
         	  result = new ResultSequence();  
            }           
            else if (rSeq.size() == 1) {
               XObject xObj = rSeq.item(0); 
               
               String strVal = XslTransformEvaluationHelper.getStrVal(xObj);              
               double dbl = 0.0;
               
               try {
             	  dbl = Double.valueOf(strVal);
               }
               catch (NumberFormatException ex) {
             	  throw new TransformerException("XPTY0004 : An XPath 3.1 function call math 'atan' argument is not an XML Schema type double.", srcLocator);
               }
               
               result = new XSDouble(Math.atan(dbl));
            }
            else {
         	   throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath 3.1 function math 'atan', requires an argument which "
         	   		                                                                                             + "should be a sequence of size one.", srcLocator); 
            }
        }
        else {
     	    throw new TransformerException("XPTY0004 : An XPath 3.1 function call math 'atan' argument is not an XML Schema type double.", srcLocator); 
        }
        
        return result;
        
    }
}
