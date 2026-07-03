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

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathInlineFunction;
import org.apache.xpath.objects.XString;
import org.apache.xpath.patterns.NodeTest;

import xml.xpath31.processor.types.XSDouble;
import xml.xpath31.processor.types.XSNumericType;
import xml.xpath31.processor.types.XSString;

/**
 * Implementation of an XPath 3.1 function fn:number.
 * 
 * @xsl.usage advanced
 */
public class FuncNumber extends FunctionDef1Arg
{
	static final long serialVersionUID = 7266745342264153076L;

	/**
	 * Class constructor.
	 */
	public FuncNumber() {
		m_arity = new Short[] {0, 1};  
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

		if (m_arg0 instanceof NodeTest) {
			if (XslTransformEvaluationHelper.isNodeTestExpressionFuntionType((NodeTest)m_arg0)) {
				throw new javax.xml.transform.TransformerException("FOTY0013 : An xdm atomic value is required for the first argument of XPath 3.1 function 'number', but the "
																																+ "supplied type is a function type, which cannot be atomized.", srcLocator); 
			}
		}
		else if (m_arg0 instanceof XPathInlineFunction) {
			throw new javax.xml.transform.TransformerException("FOTY0013 : An xdm atomic value is required for the first argument of XPath 3.1 function 'number', but the "
																																+ "supplied type is a function type, which cannot be atomized.", srcLocator); 
		}

		if (m_arg0 != null) {     	  
			XObject xObj0 = getFunctionArgEffectiveValue(m_arg0, xctxt);

			if (xObj0 instanceof XSNumericType) {
				String str1 = ((XSNumericType)xObj0).stringValue();

				result = new XSDouble(Double.valueOf(str1));
			}
			else if (xObj0 instanceof XNumber) {
				XNumber xNumber = (XNumber)xObj0;

				if (xNumber.getXsDecimal() != null) {
					String str1 = XslTransformEvaluationHelper.getStrVal(xNumber.getXsDecimal());

					result = new XSDouble(str1);
				}
				else if (xNumber.getXsInteger() != null) {
					String str1 = XslTransformEvaluationHelper.getStrVal(xNumber.getXsInteger());

					result = new XSDouble(str1);
				}
				else if (xNumber.getXsDouble() != null) {
					result = xNumber.getXsDouble(); 
				}
				else {
					result = new XSDouble(xNumber.num());
				}
			}
			else if (xObj0 instanceof ResultSequence) {
				if ((((ResultSequence)xObj0).size() == 0) || (((ResultSequence)xObj0).size() > 1)) {
					result = new XSDouble(Double.NaN);
				}
				else if (((ResultSequence)xObj0).size() == 1) {    		   
					FuncNumber funcNumber = new FuncNumber();
					funcNumber.setArg0(((ResultSequence)xObj0).item(0));

					try {
						result = funcNumber.execute(xctxt);
					}
					catch (TransformerException ex) {
						result = new XSDouble(Double.NaN); 
					}
				}
			}
			else if (xObj0 instanceof XMLNodeCursorImpl) {    		    		
				try {
					result = new XNumber(getArg0AsNumber(xctxt));
				}
				catch (TransformerException ex) {
					result = new XSDouble(Double.NaN); 
				}
			}
			else if ((xObj0 instanceof XSString) || (xObj0 instanceof XString)) {
				try {
					result = new XSDouble(getArg0AsNumber(xctxt));
				}
				catch (TransformerException ex) {
					result = new XSDouble(Double.NaN); 
				}
			}
			else {
				result = new XSDouble(Double.NaN); 
			}
		}           
		else {
			if (!((xctxt.getXPath3ContextItem() == null) && (xctxt.getContextNode() == DTM.NULL))) {    		  
				try {
					result = new XNumber(getArg0AsNumber(xctxt));
				}
				catch (TransformerException ex) {
					result = new XSDouble(Double.NaN); 
				}
			}
			else {
				throw new TransformerException("XPDY0002 : An XPath 3.1 function 'number' is called without "
																									+ "an argument, and XPath context "
																									+ "item is absent.", srcLocator);
			}
		}

		return result;
	}
  
}
