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
package org.apache.xpath.functions.datetime;

import java.util.List;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.xslt.util.RegexMatchInfo;
import org.apache.xalan.xslt.util.RegexUtil;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xml.utils.XMLString;
import org.apache.xpath.Expression;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.FunctionMultiArgs;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;

import xml.xpath31.processor.types.XSDateTime;
import xml.xpath31.processor.types.XSDuration;
import xml.xpath31.processor.types.XSString;

/**
 * Implementation of an XPath 3.1 function fn:format-time.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncFormatTime extends FunctionMultiArgs {

	private static final long serialVersionUID = 308259544111641022L;

	/**
     * Class constructor.
     */
    public FuncFormatTime() {
  	   m_defined_arity = new Short[] {2, 5}; 
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

    	 final int sourceNode = xctxt.getCurrentNode();

    	 Expression arg0 = getArg0();

    	 Expression arg1 = getArg1();

    	 String arg0Str = null;
    	 XSDateTime arg0DateTime = null;

    	 if (arg0 != null) {    		     		 
    		 XObject xObj0 = arg0.execute(xctxt);

    		 if ((xObj0 instanceof ResultSequence) && (((ResultSequence)xObj0).size() == 0)) {
    			 result = new ResultSequence();

    			 return result;
    		 }

    		 arg0Str = XslTransformEvaluationHelper.getStrVal(xObj0);

    		 try {
    			 arg0Str = "2002-12-31T" + arg0Str;
    			 arg0DateTime = XSDateTime.parseDateTime(arg0Str);
    		 }
    		 catch (TransformerException ex) {
    			 throw new javax.xml.transform.TransformerException("FOAP0001 : An XPath 3.1 function call 'format-time' doesn't have a well "
																											    					 + "formatted time value as its "
																											    					 + "first argument.", srcLocator); 
    		 }
    	 }
    	 else {    		 
    		 XObject xObj0 = xctxt.getXPath3ContextItem();
    		 if (xObj0 != null) {
    			 arg0Str = XslTransformEvaluationHelper.getStrVal(xObj0);
    		 }
    		 else if (sourceNode != DTM.NULL) {
    			 DTM dtm = xctxt.getDTM(sourceNode);
    			 XMLString xmlString = dtm.getStringValue(sourceNode);
    			 arg0Str = xmlString.toString();
    		 }
    		 else {
    			 throw new javax.xml.transform.TransformerException("FOAP0001 : An XPath 3.1 function call 'format-time' doesn't "
																								    					     + "have a time input argument, because the "
																								    					     + "context node is absent.", srcLocator);
    		 }

    		 if (arg0Str != null) {
    			 try {
    				 arg0Str = "2002-12-31T" + arg0Str;
    				 arg0DateTime = XSDateTime.parseDateTime(arg0Str);
    			 }
    			 catch (TransformerException ex) {
    				 throw new javax.xml.transform.TransformerException("FOAP0001 : An XPath 3.1 function call 'format-time' doesn't have a well "
																								    						 + "formatted time value as its "
																								    						 + "first argument.", srcLocator); 
    			 }
    		 }
    		 else {
    			 throw new javax.xml.transform.TransformerException("FOAP0001 : An XPath 3.1 function call 'format-time' has a null value for "
    					 																									 + "its first time value argument.", srcLocator);
    		 } 
    	 }

    	 String arg1Str = null;

    	 if (arg1 != null) {     		
    		 XObject xObj1 = arg1.execute(xctxt);    	 
    		 arg1Str = XslTransformEvaluationHelper.getStrVal(xObj1);
    	 }
    	 else {    	 
    		 throw new javax.xml.transform.TransformerException("FOAP0001 : An XPath 3.1 function call 'format-time' has a "
    				 																								+ "missing picture argument.", srcLocator);
    	 }    	     	

    	 String resultStr1 = replaceFormatTemplate(arg0DateTime, arg1Str);

    	 result = new XSString(resultStr1);

    	 return result;
     }

     /**
      * Method definition, to replace [..] placeholders within the
      * supplied picture string, with xs:time value components that is
      * supplied as an argument to this method.
      * 
      * The picture string comprises of format placeholders and/or literal
      * text.
      * 
      * @param xsDate                     The supplied xs:dateTime value object,
      *                                   whose xs:time component is used by this
      *                                   method.
      * @param picString                  The supplied picture string
      * @return                           The result of picture string transformation
      */
	 private String replaceFormatTemplate(XSDateTime xsDateTime, String picString) {
		
		 String result = null;

		 List<RegexMatchInfo> regexMatchInfoList = RegexUtil.getRegexMatchInfoList("\\[(.*?)\\]", picString);

		 int hour = xsDateTime.hour();
		 int min = xsDateTime.minute();
		 int sec = xsDateTime.second();

		 String amPmMarkerInfoStr = xsDateTime.getAmPmMarkerInfo();

		 XSDuration timezone = xsDateTime.getTimezone();
		 String tzOffset = null;
		 if (timezone != null) {
			 tzOffset = getTimezoneOffset(timezone);
		 }

		 int size1 = regexMatchInfoList.size();
		 StringBuffer strBuff = new StringBuffer();

		 for (int idx = 0; idx < size1; idx++) {
			 RegexMatchInfo regexMatchInfo = regexMatchInfoList.get(idx);
			 int m = regexMatchInfo.getStartIdx();
			 int n = regexMatchInfo.getEndIdx();     	        	   
			 if ((idx == 0) && (m > 0)) {
				 strBuff.append(picString.substring(0, m)); 
			 }
			 else if (idx > 0) {
				 RegexMatchInfo regexMatchInfo2 = regexMatchInfoList.get(idx - 1);
				 int nPrev = regexMatchInfo2.getEndIdx();
				 strBuff.append(picString.substring(nPrev, m));
			 }

			 String str1 = picString.substring(m + 1, n - 1);

			 // Whitespace characters within format template are ignored
			 str1 = str1.replaceAll("\\s+", "");

			 String replacedStr1 = null;
			 if ("H".equals(str1)) {     			 
				 replacedStr1 = (hour + "");
			 }
			 else if ("H01".equals(str1)) {
				 String hrStr = (hour + "");
				 if (hour < 10) {
					 hrStr = "0" + hrStr; 
				 }

				 replacedStr1 = hrStr; 
			 }
			 else if ("h".equals(str1)) {
				 if (hour > 12) {
					 int delta = (hour - 12);    			    
					 replacedStr1 = delta + "";
				 }
				 else {
					 replacedStr1 = hour + "";  
				 }
			 }
			 else if ("h01".equals(str1)) {
				 String hrStr = null;
				 if (hour > 12) {
					 int delta = (hour - 12);
					 if (delta < 10) {
						 hrStr = "0" + delta + ""; 
					 }
					 else {
						 hrStr = delta + "";
					 }
				 }
				 else {
					 hrStr = (hour + "");
				 }

				 replacedStr1 = hrStr;
			 }
			 else if ("m".equals(str1)) {
				 replacedStr1 = (min + "");
			 }
			 else if ("m01".equals(str1)) {
				 String minStr = (min + "");
				 if (min < 10) {
					 minStr = "0" + minStr; 
				 }

				 replacedStr1 = minStr; 
			 }
			 else if ("s".equals(str1)) {
				 replacedStr1 = (sec + ""); 
			 }
			 else if ("s01".equals(str1)) {
				 String secStr = (sec + "");
				 if (sec < 10) {
					 secStr = "0" + secStr; 
				 }

				 replacedStr1 = secStr; 
			 }
			 else if ("PN".equals(str1)) {
				 replacedStr1 = amPmMarkerInfoStr.toUpperCase();
			 }
			 else if ("Pn".equals(str1)) {
				 replacedStr1 = amPmMarkerInfoStr;  
			 }
			 else if (("Z".equals(str1) || "z".equals(str1)) && (tzOffset != null)) {
				 // The timezone display string
				 replacedStr1 = getTimeZoneDisplayStr(tzOffset, str1); 
			 }

			 strBuff.append(replacedStr1);
		 }

		 result = strBuff.toString();

		 return result;
	}

}
