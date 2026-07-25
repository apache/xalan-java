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

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathInlineFunction;
import org.apache.xpath.objects.XString;
import org.apache.xpath.patterns.NodeTest;
import org.apache.xpath.types.XSByte;
import org.apache.xpath.types.XSNegativeInteger;
import org.apache.xpath.types.XSNonNegativeInteger;
import org.apache.xpath.types.XSNonPositiveInteger;
import org.apache.xpath.types.XSShort;

import xml.xpath31.processor.types.XSDecimal;
import xml.xpath31.processor.types.XSDouble;
import xml.xpath31.processor.types.XSFloat;
import xml.xpath31.processor.types.XSInt;
import xml.xpath31.processor.types.XSInteger;
import xml.xpath31.processor.types.XSLong;
import xml.xpath31.processor.types.XSString;

/**
 * Implementation of an XPath 3.1 function fn:ceiling.
 * 
 * @xsl.usage advanced
 */
public class FuncCeiling extends FunctionDef1Arg
{
    static final long serialVersionUID = -1275988936390464739L;
    
    /**
	 * Class constructor.
	 */
	public FuncCeiling() {
		m_arity = new Short[] { 1 };
	}

    /**
     * Evaluate the function. The function must return a valid object.
     * 
     * @param xctxt                             An XPath context object
     * @return                                  A valid XObject
     *
     * @throws javax.xml.transform.TransformerException
    */
    public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
    {
    	
    	XObject result = null;

		SourceLocator srcLocator = xctxt.getSAXLocator();
		
		if (m_arg0 == null) {
			throw new TransformerException("XPST0017 : An XPath 3.1 function call 'ceiling' has been called with no argument.", srcLocator);
		}
		else if (m_arg0 instanceof NodeTest) {
			if (XslTransformEvaluationHelper.isNodeTestExpressionFuntionType((NodeTest)m_arg0)) {
				throw new javax.xml.transform.TransformerException("FOTY0013 : An xdm atomic value is required for the first argument of XPath function 'ceiling', but the "
																																	+ "supplied type is a function type, "
																																	+ "which cannot be atomized.", srcLocator); 
			}
		}
		else if (m_arg0 instanceof XPathInlineFunction) {
			throw new javax.xml.transform.TransformerException("FOTY0013 : An xdm atomic value is required for the first argument of XPath function 'ceiling', but the "
																																	+ "supplied type is a function type, "
																																	+ "which cannot be atomized.", srcLocator);
		}
		
        XObject arg0Obj = null;
		
		try {
		   arg0Obj = getFunctionArgEffectiveValue(m_arg0, xctxt);
		}
		catch (Exception ex) {		   
			throw new TransformerException("XPTY0004 : An XPath 3.1 function call 'ceiling' argument is not numeric.", srcLocator);
		}
		
		String arg0Str = null;
		
		try {
		    arg0Str = (getArg0AsString(xctxt)).toString();
		}
		catch (NumberFormatException ex) {
			throw new TransformerException("XPTY0004 : An XPath 3.1 function call 'ceiling' argument is not numeric.", srcLocator);
		}
		
		if (arg0Obj instanceof XSFloat) {
			XSFloat xsFloatArg = (XSFloat)arg0Obj;
			if (xsFloatArg.zero()) {
			    result = new XSFloat(0); 
			}
			else if (xsFloatArg.negativeZero()) {
				result = new XSFloat(-0.0f);
			}
			else if ((xsFloatArg.floatValue() > -1) && (xsFloatArg.floatValue() < 0)) {
			    result = new XSFloat(-0.0f); 
			}
			else {				
				result = new XSFloat((float)(Math.ceil(Float.valueOf(arg0Str))));
			}
		}
		else if (arg0Obj instanceof XSDouble) {
			XSDouble xsDoubleArg = (XSDouble)arg0Obj;
			if (xsDoubleArg.zero()) {
			    result = new XSDouble(0); 
			}
			else if (xsDoubleArg.negativeZero()) {
				result = new XSDouble(-0.0d);
			}
			else if ((xsDoubleArg.doubleValue() > -1) && (xsDoubleArg.doubleValue() < 0)) {
			    result = new XSDouble(-0.0d); 
			}
			else {				
				result = new XSDouble(Math.ceil(Double.valueOf(arg0Str)));
			}
		}
		else if (arg0Obj instanceof XSByte) {                                    
            result = arg0Obj;
		}
		else if (arg0Obj instanceof XSShort) {                        
            result = arg0Obj;
		}
		else if (arg0Obj instanceof XSInt) {                        
            result = arg0Obj;
		}
		else if (arg0Obj instanceof XSLong) {                        
            result = arg0Obj;
		}
		else if (arg0Obj instanceof XSNonNegativeInteger) {			                        
            result = arg0Obj;
		}
		else if (arg0Obj instanceof XSNegativeInteger) {			            
			result = arg0Obj;
		}
		else if (arg0Obj instanceof XSNonPositiveInteger) {			            
			result = arg0Obj;
		}		
		else if (arg0Obj instanceof XSInteger) {
			result = arg0Obj;
		}
		else if (arg0Obj instanceof XSDecimal) {
			if (!arg0Str.contains(".")) {
				result = arg0Obj; 	
			}
			else {
				BigDecimal bigDecimal = BigDecimal.valueOf((long)(Math.ceil(Double.valueOf(arg0Str))));

				result = new XSDecimal(bigDecimal);
			}
		}
		else if (arg0Obj instanceof XNumber) {						
			XNumber xNumber = (XNumber)arg0Obj;
			
			if (xNumber.getXsDecimal() != null) {
				if (!arg0Str.contains(".")) {
					result = xNumber.getXsDecimal(); 	
				}
				else {
					BigDecimal bigDecimal = BigDecimal.valueOf((long)(Math.ceil(Double.valueOf(arg0Str))));

					result = new XSDecimal(bigDecimal);
				} 
			}
			else if (xNumber.getXsDouble() != null) {
				XSDouble xsDoubleArg = xNumber.getXsDouble();
				if (xsDoubleArg.zero()) {
					result = new XSDouble(0); 
				}
				else if (xsDoubleArg.negativeZero()) {
					result = new XSDouble(-0.0d);
				}
				else if ((xsDoubleArg.doubleValue() > -1) && (xsDoubleArg.doubleValue() < 0)) {
					result = new XSDouble(-0.0d); 
				}
				else {				
					result = new XSDouble(Math.ceil(Double.valueOf(arg0Str)));
				}
			}
			else if (xNumber.getXsInteger() != null) {
				result = xNumber.getXsInteger(); 
			}
			else {
				XSDouble xsDoubleArg = new XSDouble(xNumber.num());
				
				if (xsDoubleArg.zero()) {
					result = new XSDouble(0); 
				}
				else if (xsDoubleArg.negativeZero()) {
					result = new XSDouble(-0.0d);
				}
				else if ((xsDoubleArg.doubleValue() > -1) && (xsDoubleArg.doubleValue() < 0)) {
					result = new XSDouble(-0.0d); 
				}
				else {									
					double dbl = Math.ceil(Double.valueOf(arg0Str));
					
					return new XSDecimal(dbl + "");
				}
			}

			return result;
		}
		else if ((arg0Obj instanceof ResultSequence) && ((ResultSequence)arg0Obj).size() == 1) {
			XObject arg0 = ((ResultSequence)arg0Obj).item(0);
			
			FuncCeiling funcCeiling = new FuncCeiling();
			funcCeiling.setArg0(arg0);
			
			result = funcCeiling.execute(xctxt); 
		}
		else if ((arg0Obj instanceof ResultSequence) && ((ResultSequence)arg0Obj).size() == 0) {			
			result = new ResultSequence(); 
		}
		else if ((arg0Obj instanceof XMLNodeCursorImpl) && ((XMLNodeCursorImpl)arg0Obj).getLength() == 0) {			
			result = new ResultSequence(); 
		}
		else {
			if ((arg0Obj instanceof XSString) || (arg0Obj instanceof XString)) {
			   throw new TransformerException("XPTY0004 : An XPath 3.1 function call 'ceiling' argument is not numeric.", srcLocator);
			}

			result = new XSDouble(Math.ceil(Double.valueOf(arg0Str)));			
		}

		return result;
    }
}
