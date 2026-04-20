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
import org.apache.xalan.xslt.util.StringUtil;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionNode;
import org.apache.xpath.XPathContext;
import org.apache.xpath.axes.SelfIteratorNoPredicate;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.patterns.NodeTest;

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
	 * Evaluate the function. The function must return a valid object.
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
		
		if (arg0 instanceof NodeTest) {
  		   if (XslTransformEvaluationHelper.isNodeTestExpressionFuntionType((NodeTest)arg0)) {
  			  throw new javax.xml.transform.TransformerException("FOTY0013 : An atomic value is required for the first argument of XPath function format-integer(), "
  			  		                                                                  + "but the supplied type is a function type, which cannot be atomized.", srcLocator); 
  		   }
  	    }
		
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
		String pictureStr1 = XslTransformEvaluationHelper.getStrVal(arg1Obj);
		
		String str2 = null;		
		if (arg2 != null) {
		   XObject arg2Obj = arg2.execute(xctxt);
		   str2 = XslTransformEvaluationHelper.getStrVal(arg2Obj); 
		}
		
		ElemNumber elemNumber = new ElemNumber();

		Locale locale = null;
		if (str2 != null) {
			locale = new Locale(str2.toUpperCase(), "");
		}
		else {
			locale = Locale.getDefault();
		}

		elemNumber.setLocale(locale);
		
		String formattedNumberResultStr = null;
				
		int picStrLength = pictureStr1.length();
		if (picStrLength == 0) {
			throw new javax.xml.transform.TransformerException("FODF1310 : An XPath function fn:format-integer's "
																				                    + "second argument specifies an "
																				                    + "invalid picture string. Picture "
																				                    + "string has length zero.", srcLocator);	
		}
		
		int idx = pictureStr1.lastIndexOf(';');
		
		String primaryFormatToken = null;
		String formatModifier = null;		
		if (idx == -1) {
		   primaryFormatToken = pictureStr1;
		   
		   // The format modifier is absent
		}
		else if (idx == 0) {
		   // Picture string ';' is invalid
			
		   throw new javax.xml.transform.TransformerException("FODF1310 : An XPath function fn:format-integer's "
																				                    + "second argument specifies an "
																				                    + "invalid picture string '" + pictureStr1 + "'.", srcLocator);
		}
		else if (idx == (picStrLength - 1)) {
		   // Picture string primary_format_token; is invalid, for e.g w;
			
		   throw new javax.xml.transform.TransformerException("FODF1310 : An XPath function fn:format-integer's "
																				                    + "second argument specifies an "
																				                    + "invalid picture string '" + pictureStr1 + "'.", srcLocator);
		}
		else {
		   primaryFormatToken = pictureStr1.substring(0, idx);
		   formatModifier = pictureStr1.substring(idx + 1);
		}
		
		elemNumber.setFormatRawValue(primaryFormatToken);
		
		// Cardinal numbering is default
		boolean isCardinalNumbering = true;
		
		if (formatModifier != null) {		   	
		   int idx2 = formatModifier.indexOf('(');
		   int idx3 = formatModifier.lastIndexOf(')');
		   if ((idx2 != -1) && (idx3 != -1) && (idx2 < idx3)) {
			   String str1 = formatModifier.substring(idx2, idx3 + 1); 
			   if (!StringUtil.isStrHasBalancedParentheses(str1, '(', ')')) {
				   throw new javax.xml.transform.TransformerException("FODF1310 : An XPath function fn:format-integer's "
																				                       + "second argument specifies an "
																				                       + "invalid picture string '" + pictureStr1 + "'.", srcLocator); 
			   }
			   else {
				   /**
				    * Xalan-J ignores any substring like "(...)" wrt this, although
				    * XPath 3.1 F&O spec allows this syntax and XPath function 
				    * implementation for fn:format-integer may ascribe implementation 
				    * specific meaning to this.
				    */
			   }
		   }
		   else if ((idx2 != -1) || (idx3 != -1)) {
			   throw new javax.xml.transform.TransformerException("FODF1310 : An XPath function fn:format-integer's "
																				                       + "second argument specifies an "
																				                       + "invalid picture string '" + pictureStr1 + "'.", srcLocator); 
		   }
		   
		   if ((idx2 != -1) && (idx3 != -1)) {
			   String fmPrefix = formatModifier.substring(0, idx2);
			   String fmSuffix = formatModifier.substring(idx3 + 1);
			   formatModifier = fmPrefix + fmSuffix;
		   }
			
		   int formatModifierLength = formatModifier.length();
		   if (formatModifierLength == 1) {
			   char c1 = formatModifier.charAt(0);
			   if (c1 == 'c') {
				   // Cardinal numbering is been followed
				   isCardinalNumbering = true; 
			   }
			   else if (c1 == 'o') {
				   // Ordinal numbering is been followed
				   isCardinalNumbering = false; 
			   }
			   else {
				   throw new javax.xml.transform.TransformerException("FODF1310 : An XPath function fn:format-integer's "
																					                           + "second argument specifies an "
																					                           + "invalid picture string '" + pictureStr1 + "'.", srcLocator); 
			   }			   
		   }
		   else if (formatModifierLength == 2) {
			   char c1 = formatModifier.charAt(0);
			   if (c1 == 'c') {
				   // Cardinal numbering is been followed
				   isCardinalNumbering = true; 
			   }
			   else if (c1 == 'o') {
				   // Ordinal numbering is been followed
				   isCardinalNumbering = false; 
			   }
			   else {
				   throw new javax.xml.transform.TransformerException("FODF1310 : An XPath function fn:format-integer's "
																					                           + "second argument specifies an "
																					                           + "invalid picture string '" + pictureStr1 + "'.", srcLocator); 
			   }			   
			   
			   char c2 = formatModifier.charAt(1);
			   
			   if (!((c2 == 'a') || (c2 == 't'))) {
				   throw new javax.xml.transform.TransformerException("FODF1310 : An XPath function fn:format-integer's "
																				                             + "second argument specifies an "
																				                             + "invalid picture string '" + pictureStr1 + "'.", srcLocator); 
			   }
		   }
		   else {
			   throw new javax.xml.transform.TransformerException("FODF1310 : An XPath function fn:format-integer's "
																				                              + "second argument specifies an "
																				                              + "invalid picture string '" + pictureStr1 + "'.", srcLocator); 
		   }
		}
		
		if ("1".equals(primaryFormatToken) || "w".equals(primaryFormatToken) || "W".equals(primaryFormatToken) || "Ww".equals(primaryFormatToken) 
				                                                                                      || "a".equals(primaryFormatToken) || 
				                                                                                      "A".equals(primaryFormatToken) 
				                                                                                      || "i".equals(primaryFormatToken) 
				                                                                                      || "I".equals(primaryFormatToken)) {
			ExpressionNode exprOwnerNode = getExpressionOwner();
			TransformerImpl transformerImpl = null;
			if (exprOwnerNode instanceof ElemTemplateElement) {
				ElemTemplateElement elemTemplateElement = (ElemTemplateElement)exprOwnerNode;
				StylesheetRoot stylesheetRoot = XslTransformEvaluationHelper.getXslStylesheetRootFromXslElementRef(elemTemplateElement);
				transformerImpl = stylesheetRoot.getTransformerImpl();		   		   		   		   
			}

			formattedNumberResultStr = elemNumber.formatNumberList(transformerImpl, new long[] { arg0Long }, DTM.NULL, !isCardinalNumbering);
		}
		else {
			NumberFormat nf = NumberFormat.getIntegerInstance(locale);
			DecimalFormat df = (DecimalFormat)nf;
			try {
			   df.applyPattern(pictureStr1);
			}
			catch (IllegalArgumentException ex) {
			   throw new javax.xml.transform.TransformerException("FODF1310 : An XPath function fn:format-integer's "
			   		                                                                                          + "second argument specifies an "
			   		                                                                                          + "invalid picture string '" + pictureStr1 + "'.", srcLocator);
			}
			
			formattedNumberResultStr = df.format(arg0Long);
		}
		   
		result = new XSString(formattedNumberResultStr);
		
		return result;
		
	}

}
