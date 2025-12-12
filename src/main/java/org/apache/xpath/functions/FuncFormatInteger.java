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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import javax.xml.transform.SourceLocator;

import org.apache.xalan.templates.ElemNumber;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionNode;
import org.apache.xpath.XPathContext;
import org.apache.xpath.axes.SelfIteratorNoPredicate;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XObject;

import xml.xpath31.processor.types.XSString;

/**
 * Implementation of an XPath 3.1 function fn:format-integer.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncFormatInteger extends FunctionMultiArgs {

	private static final long serialVersionUID = -422841478442099777L;
	
	/**
	 * Default constructor.
	 */
	public FuncFormatInteger() {
		m_defined_arity = new Short[] {2, 3}; 
	}
	
	/**
	 * Implementation of the function. The function must return a valid object.
	 * 
	 * @param xctxt                           An XPath expression context, object
	 * @return                                A valid XObject
	 *
	 * @throws javax.xml.transform.TransformerException
	 */
	public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException {

		XObject result = null;

		SourceLocator srcLocator = xctxt.getSAXLocator();

		Expression arg0 = getArg0();
		Expression arg1 = getArg1();
		Expression arg2 = getArg2();

		Expression[] exprArray = getArgs();

		if ((arg1 == null) || ((exprArray != null) && (exprArray.length > 0))) {
			throw new javax.xml.transform.TransformerException("FOAP0001 : An XPath function fn:format-integer can "
																												+ "have either two or three arguments.", srcLocator);
		}
		
		XObject arg0Obj = null;
		
		if ((arg0 instanceof SelfIteratorNoPredicate) && (xctxt.getXPath3ContextItem() != null)) {
		   arg0Obj = xctxt.getXPath3ContextItem(); 
		}
		else {
		   arg0Obj = arg0.execute(xctxt);
		}
		
		if ((arg0Obj instanceof ResultSequence) && (((ResultSequence)arg0Obj).size() == 0)) {
		   result = new XSString("");
		   
		   return result;
		}
		
        if ((arg0Obj instanceof XMLNodeCursorImpl) && (((XMLNodeCursorImpl)arg0Obj).getLength() == 0)) {
           result = new XSString("");
 		   
 		   return result;
		}
        
        String str0 = null;
        if (arg0Obj instanceof ResultSequence) {
        	ResultSequence rSeq = (ResultSequence)arg0Obj;
        	if (rSeq.size() == 1) {
        		arg0Obj = rSeq.item(0);
        	}
        	else {
        		throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath function fn:format-integer's first "
        				                                                                          + "argument, cannot be an xdm sequence "
        				                                                                          + "with length greater than one.", srcLocator); 
        	}
        }
        
        if (arg0Obj instanceof XMLNodeCursorImpl) {
           XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)arg0Obj;
           if (xmlNodeCursorImpl.getLength() > 1) {
        	   throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath function fn:format-integer's first "
																		                         + "argument, cannot be an xdm nodeset "
																		                         + "with length greater than one.", srcLocator); 
           }
           
           str0 = xmlNodeCursorImpl.str();
        }
        else {
		   str0 = XslTransformEvaluationHelper.getStrVal(arg0Obj);
        }

		long arg0Long = 0;
		
		boolean arg0FormatError = false;
		try {
			arg0Long = Long.valueOf(str0);
			if (str0.contains(".")) {
				arg0FormatError = true; 
			}
		}
		catch (NumberFormatException ex) {
			arg0FormatError = true;
		}

		if (arg0FormatError) {
			throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath function fn:format-integer's first argument, "
					                                                                                                  + "is not a valid representation of "
					                                                                                                  + "an xs:integer value.", srcLocator);
		}
		
		XObject arg1Obj = arg1.execute(xctxt);		
		String str1 = XslTransformEvaluationHelper.getStrVal(arg1Obj);
		
		String str2 = null;		
		if (arg2 != null) {
		   XObject arg2Obj = arg2.execute(xctxt);
		   str2 = XslTransformEvaluationHelper.getStrVal(arg2Obj); 
		}
		
		ElemNumber elemNumber = new ElemNumber();
		elemNumber.setFormatRawValue(str1);

		Locale locale = null;
		if (str2 != null) {
			locale = new Locale(str2.toUpperCase(), "");
		}
		else {
			locale = Locale.getDefault();
		}

		elemNumber.setLocale(locale);
		
		String formattedNumberStr = null;
		
		if (str1.contains("w") || str1.contains("W") || str1.contains("a") || 
				                                                  str1.contains("A") 
				                                                              || str1.contains("i") || str1.contains("I")) {
			ExpressionNode exprOwnerNode = getExpressionOwner();
			TransformerImpl transformerImpl = null;
			if (exprOwnerNode instanceof ElemTemplateElement) {
				ElemTemplateElement elemTemplateElement = (ElemTemplateElement)exprOwnerNode;
				StylesheetRoot stylesheetRoot = XslTransformEvaluationHelper.getXslStylesheetRootFromXslElementRef(elemTemplateElement);
				transformerImpl = stylesheetRoot.getTransformerImpl();		   		   		   		   
			}

			formattedNumberStr = elemNumber.formatNumberList(transformerImpl, new long[] { arg0Long }, DTM.NULL, false);
		}
		else {
			NumberFormat nf = NumberFormat.getIntegerInstance(locale);
			DecimalFormat df = (DecimalFormat)nf;
			try {
			   df.applyPattern(str1);
			}
			catch (IllegalArgumentException ex) {
			   throw new javax.xml.transform.TransformerException("FODF1310 : An XPath function fn:format-integer's "
			   		                                                                                          + "second argument, specifies an "
			   		                                                                                          + "XPath invalid picture string '" + str1 + "'.", srcLocator);
			}
			
			formattedNumberStr = df.format(arg0Long);
		}
		   
		result = new XSString(formattedNumberStr);
		
		return result;
		
	}

}
