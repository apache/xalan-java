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
 * Implementation of an XPath 3.1 function fn:abs.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncAbs extends FunctionDef1Arg
{

	private static final long serialVersionUID = 7292919650996994647L;
	
	/**
	 * Class constructor.
	 */
	public FuncAbs() {
		m_defined_arity = new Short[] { 1 };
	}

	/**
	 * Evaluate the function. The function must return a valid object.
	 * 
	 * @param xctxt                         An XPath context object
	 * @return                              A valid XObject
	 *
	 * @throws javax.xml.transform.TransformerException
	 */
	public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
	{
		
		XObject result = null;

		SourceLocator srcLocator = xctxt.getSAXLocator();
		
		if (m_arg0 == null) {
		   throw new TransformerException("XPST0017 : An XPath 3.1 function call 'abs' has been called with no argument.", srcLocator);
		}
		else if (m_arg0 instanceof NodeTest) {
			if (XslTransformEvaluationHelper.isNodeTestExpressionFuntionType((NodeTest)m_arg0)) {
				throw new javax.xml.transform.TransformerException("FOTY0013 : An xdm atomic value is required for the first argument of XPath function 'abs', but the "
						                                                                                                       + "supplied type is a function type, "
						                                                                                                       + "which cannot be atomized.", srcLocator); 
			}
		}
		else if (m_arg0 instanceof XPathInlineFunction) {
			throw new javax.xml.transform.TransformerException("FOTY0013 : An xdm atomic value is required for the first argument of XPath function 'abs', but the "
                                                                                                                               + "supplied type is a function type, "
                                                                                                                               + "which cannot be atomized.", srcLocator);
		}
		
		XObject arg0Obj = null;
		
		try {
		   arg0Obj = getFunctionEffectiveArgValue(m_arg0, xctxt);
		}
		catch (Exception ex) {		   
		   throw new TransformerException("FORG0001 : An XPath 3.1 function call 'abs' argument is not numeric.", srcLocator);
		}
		
		if (arg0Obj instanceof XSFloat) {
		   XSFloat xsFloatArg = (XSFloat)arg0Obj;
		   if (xsFloatArg.zero() || xsFloatArg.negativeZero()) {
			  result = new XSFloat(0); 
		   }
		   else if (xsFloatArg.infinite()) {
			  result = new XSFloat(Float.POSITIVE_INFINITY); 
		   }
		   else {
			  float flt1 = xsFloatArg.floatValue();
			  result = new XSFloat(Math.abs(flt1));
		   }
		}
		else if (arg0Obj instanceof XSDouble) {
			XSDouble xsDoubleArg = (XSDouble)arg0Obj;
			if (xsDoubleArg.zero() || xsDoubleArg.negativeZero()) {
				result = new XSDouble(0); 
			}
			else if (xsDoubleArg.infinite()) {
				result = new XSDouble(Double.POSITIVE_INFINITY); 
			}
			else {
				double dbl1 = xsDoubleArg.doubleValue();
				result = new XSDouble(Math.abs(dbl1));
			}
		}									
		else if (arg0Obj instanceof XSByte) {
			XSByte xsByteArg = (XSByte)arg0Obj;
            BigDecimal bigDecimal1 = xsByteArg.getValue();
			
			result = new XSByte((bigDecimal1.abs()).toBigInteger());
		}
		else if (arg0Obj instanceof XSShort) {
			XSShort xsShortArg = (XSShort)arg0Obj;
            BigDecimal bigDecimal1 = xsShortArg.getValue();
			
			result = new XSShort((bigDecimal1.abs()).toBigInteger());
		}
		else if (arg0Obj instanceof XSInt) {
			XSInt xsIntArg = (XSInt)arg0Obj;
            BigDecimal bigDecimal1 = xsIntArg.getValue();
			
			result = new XSInt((bigDecimal1.abs()).toBigInteger());
		}
		else if (arg0Obj instanceof XSLong) {
			XSLong xsLongArg = (XSLong)arg0Obj;
            BigDecimal bigDecimal1 = xsLongArg.getValue();
			
			result = new XSLong((bigDecimal1.abs()).toBigInteger());
		}
		else if (arg0Obj instanceof XSNonNegativeInteger) {			
			result = arg0Obj;
		}
		else if (arg0Obj instanceof XSNegativeInteger) {			
			XSNegativeInteger xsNegativeIntegerArg = (XSNegativeInteger)arg0Obj;
			BigDecimal bigDecimal1 = xsNegativeIntegerArg.getValue();
			
			result = new XSNonNegativeInteger((bigDecimal1.abs()).toBigInteger());
		}
		else if (arg0Obj instanceof XSNonPositiveInteger) {			
			XSNonPositiveInteger xsNonPositiveIntegerArg = (XSNonPositiveInteger)arg0Obj;
			BigDecimal bigDecimal1 = xsNonPositiveIntegerArg.getValue();
			
			result = new XSNonNegativeInteger((bigDecimal1.abs()).toBigInteger());
		}		
		else if (arg0Obj instanceof XSInteger) {
			XSInteger xsIntegerArg = (XSInteger)arg0Obj;			
			BigDecimal bigDecimal1 = xsIntegerArg.getValue();
						
			result = new XSInteger((bigDecimal1.abs()).toBigInteger());
		}
		else if (arg0Obj instanceof XSDecimal) {
			XSDecimal xsDecimalArg = (XSDecimal)arg0Obj;
			BigDecimal bigDecimal1 = xsDecimalArg.getValue();
			
			result = new XSDecimal(bigDecimal1.abs()); 
		}
		else if (arg0Obj instanceof XNumber) {
			XSDouble xsDoubleArg = new XSDouble(((XNumber)arg0Obj).num());
			if (xsDoubleArg.zero() || xsDoubleArg.negativeZero()) {
				result = new XSDouble(0); 
			}
			else if (xsDoubleArg.infinite()) {
				result = new XSDouble(Double.POSITIVE_INFINITY); 
			}
			else {
				double dbl1 = xsDoubleArg.doubleValue();
				result = new XSDouble(Math.abs(dbl1));
			}
		}
		else if ((arg0Obj instanceof ResultSequence) && ((ResultSequence)arg0Obj).size() == 1) {
			XObject arg0 = ((ResultSequence)arg0Obj).item(0);
			
			FuncAbs funcAbs = new FuncAbs();
			funcAbs.setArg0(arg0);
			
			result = funcAbs.execute(xctxt); 
		}
		else if ((arg0Obj instanceof ResultSequence) && ((ResultSequence)arg0Obj).size() == 0) {			
			result = new ResultSequence(); 
		}
		else if ((arg0Obj instanceof XMLNodeCursorImpl) && ((XMLNodeCursorImpl)arg0Obj).getLength() == 0) {			
			result = new ResultSequence(); 
		}
		else {
			if ((arg0Obj instanceof XSString) || (arg0Obj instanceof XString)) {
			   throw new TransformerException("XPTY0004 : An XPath 3.1 function call 'abs' argument is not numeric.", srcLocator);
			}
			
			String strValueOfArg = (getArg0AsString(xctxt)).toString();
			
			try {
			   result = new XSDouble(Math.abs(Double.valueOf(strValueOfArg)));
			}
			catch (NumberFormatException nfe) {
			   throw new TransformerException("XPTY0004 : An XPath 3.1 function call 'abs' argument is not numeric.", srcLocator);
			}
		}

		return result;
	}
}
