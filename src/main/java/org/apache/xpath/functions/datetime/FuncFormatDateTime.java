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

import java.util.Calendar;
import java.util.List;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.xslt.util.NumberUtil;
import org.apache.xalan.xslt.util.RegexMatchInfo;
import org.apache.xalan.xslt.util.RegexUtil;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xml.utils.XMLString;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.FunctionMultiArgs;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;

import xml.xpath31.processor.types.XSDateTime;
import xml.xpath31.processor.types.XSDuration;
import xml.xpath31.processor.types.XSString;

/**
 * Implementation of an XPath 3.1 function fn:format-dateTime.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncFormatDateTime extends FunctionMultiArgs {

	private static final long serialVersionUID = 2988932941569397210L;

	/**
     * Class constructor.
     */
    public FuncFormatDateTime() {
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
    	     	     	 
    	 String arg0Str = null;
    	 XSDateTime arg0DateTime = null;
    	     	 
    	 if (m_arg0 != null) {    		     		 
    		 XObject xObj0 = getFunctionArgEffectiveValue(m_arg0, xctxt);
    		 
    		 if ((xObj0 instanceof ResultSequence) && (((ResultSequence)xObj0).size() == 0)) {
    			result = new ResultSequence();
    			
    			return result;
    		 }
    		 
    		 arg0Str = XslTransformEvaluationHelper.getStrVal(xObj0);
    		 
    		 try {
    			arg0DateTime = XSDateTime.parseDateTime(arg0Str);
    		 }
    		 catch (TransformerException ex) {
    			throw new javax.xml.transform.TransformerException("FOAP0001 : An XPath 3.1 function call 'format-dateTime' doesn't have a well "
																														   + "formatted dateTime value as its "
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
    			 throw new javax.xml.transform.TransformerException("FOAP0001 : An XPath 3.1 function call 'format-dateTime' doesn't "
																								    					 + "have a dateTime input argument, because the "
																								    					 + "context node is absent.", srcLocator);
    		 }

    		 if (arg0Str != null) {
    			 try {
    				arg0DateTime = XSDateTime.parseDateTime(arg0Str);
    			 }
    			 catch (TransformerException ex) {
    				 throw new javax.xml.transform.TransformerException("FOAP0001 : An XPath 3.1 function call 'format-dateTime' doesn't have a well "
																							    						 + "formatted dateTime value as its "
																							    						 + "first argument.", srcLocator); 
    			 }
    		 }
    		 else {
    			 throw new javax.xml.transform.TransformerException("FOAP0001 : An XPath 3.1 function call 'format-dateTime' has a null value for "
    					 																								 + "its first dateTime value argument.", srcLocator);
    		 } 
    	 }
    	     	     	 
    	 String arg1Str = null;
    	 
    	 if (m_arg1 != null) {     		
    		 XObject xObj1 = getFunctionArgEffectiveValue(m_arg1, xctxt); 
    		 
    		 arg1Str = XslTransformEvaluationHelper.getStrVal(xObj1);
    	 }
    	 else {    	 
    		 throw new javax.xml.transform.TransformerException("FOAP0001 : An XPath 3.1 function call 'format-dateTime' has a "
    				 																									+ "missing picture argument.", srcLocator);
    	 }    	     	
     	
     	 String resultStr1 = replaceFormatTemplate(arg0DateTime, arg1Str);
     	
     	 result = new XSString(resultStr1);
     	
    	 return result;
     }

     /**
      * Method definition, to replace [..] placeholders within the
      * supplied picture string, with xs:dateTime value components that is
      * supplied as an argument to this method.
      * 
      * The picture string comprises of format placeholders and/or literal
      * text.
      * 
      * @param xsDate                     The supplied xs:dateTime value object
      * @param picString                  The supplied picture string
      * @return                           The result of picture string transformation
      */
	 private String replaceFormatTemplate(XSDateTime xsDateTime, String picString) {
		 
		 String result = null;

		 List<RegexMatchInfo> regexMatchInfoList = RegexUtil.getRegexMatchInfoList("\\[(.*?)\\]", picString);

		 Calendar calendar = xsDateTime.getCalendar();

		 int year = calendar.get(Calendar.YEAR);
		 int month = calendar.get(Calendar.MONTH) + 1;
		 int dayInYear = calendar.get(Calendar.DAY_OF_YEAR);
		 int dayInMonth = calendar.get(Calendar.DATE);
		 int weekInYear = calendar.get(Calendar.WEEK_OF_YEAR);
		 int weekInMonth = calendar.get(Calendar.WEEK_OF_MONTH);
		 int dayInWeek = calendar.get(Calendar.DAY_OF_WEEK);

		 int hour = xsDateTime.hour();
		 int min = xsDateTime.minute();
		 int sec = xsDateTime.second();

		 String amPmMarkerInfoStr = xsDateTime.getAmPmMarkerInfo();

		 XSDuration timezone = xsDateTime.getTimezone();
		 String tzOffset = null;
		 if (timezone != null) {
			 tzOffset = getTimezoneOffset(timezone);
		 }

		 String[] monthNameArray = new String[] {"January", "February", "March", "April", "May", "June", "July", "August", 
				 																		"September", "October", "November", "December"};

		 String[] dayOfWeekNameArray = new String[] {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

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
			 if ("Y".equals(str1) || "Y0001".equals(str1)) {
				 // Year numeric value, or padded with leading zeros
				 replacedStr1 = year+""; 
			 }     	   
			 else if ("M".equals(str1) || "M1".equals(str1)) {
				 // Month numeric value
				 replacedStr1 = month+""; 
			 }
			 else if ("M01".equals(str1)) {
				 // Month numeric value padded with leading zeros
				 if (month < 10) {
					 replacedStr1 = "0"+month+""; 
				 }
				 else {
					 replacedStr1 = month+""; 
				 }
			 }
			 else if ("MNn".equals(str1)) {
				 // Month name, title case
				 replacedStr1 = monthNameArray[month - 1];
			 }
			 else if ("Mn".equals(str1)) {
				 // Month name, lowercase
				 replacedStr1 = (monthNameArray[month - 1]+"").toLowerCase();
			 }
			 else if ("MN".equals(str1)) {
				 // Month name, uppercase
				 replacedStr1 = (monthNameArray[month - 1]+"").toUpperCase(); 
			 }
			 else if ("M1o".equals(str1)) {
				 // Month numeric value with ordinal form
				 replacedStr1 = NumberUtil.getOrdinalNumber(month);
			 }
			 else if ("Mi".equals(str1)) {
				 // Month numeric value with roman numeral in smallcase
				 replacedStr1 = NumberUtil.getRomanNumeral(month, true);
			 }
			 else if ("MI".equals(str1)) {
				 // Month numeric value with roman numeral in uppercase
				 replacedStr1 = NumberUtil.getRomanNumeral(month, false);
			 }
			 else if ("D".equals(str1) || "D1".equals(str1)) {
				 // Day in month numeric value
				 replacedStr1 = dayInMonth+""; 
			 }
			 else if ("D01".equals(str1)) {
				 // Day in month numeric value padded with leading zeros
				 if (dayInMonth < 10) {
					 replacedStr1 = "0"+dayInMonth+"";
				 }
				 else {
					 replacedStr1 = dayInMonth+""; 
				 }
			 }     	   
			 else if ("D1o".equals(str1)) {
				 // Day in month numeric value with ordinal form
				 replacedStr1 = NumberUtil.getOrdinalNumber(dayInMonth); 
			 }
			 else if ("Di".equals(str1)) {
				 // Day in month numeric value with roman numeral in smallcase
				 replacedStr1 = NumberUtil.getRomanNumeral(dayInMonth, true);
			 }
			 else if ("DI".equals(str1)) {
				 // Day in month numeric value with roman numeral in uppercase
				 replacedStr1 = NumberUtil.getRomanNumeral(dayInMonth, false);
			 }
			 else if ("d".equals(str1) || "d1".equals(str1)) {
				 // Day in year numeric value
				 replacedStr1 = dayInYear+""; 
			 }
			 else if ("d01".equals(str1)) {
				 // Day in year numeric value padded with leading zeros
				 if (dayInYear < 10) {
					 replacedStr1 = "0"+dayInYear+"";
				 }
				 else {
					 replacedStr1 = dayInYear+""; 
				 }
			 }     	   
			 else if ("d1o".equals(str1)) {
				 // Day in year numeric value with ordinal form
				 replacedStr1 = NumberUtil.getOrdinalNumber(dayInYear); 
			 }
			 else if ("di".equals(str1)) {
				 // Day in year numeric value with roman numeral in smallcase
				 replacedStr1 = NumberUtil.getRomanNumeral(dayInYear, true);
			 }
			 else if ("dI".equals(str1)) {
				 // Day in year numeric value with roman numeral in uppercase
				 replacedStr1 = NumberUtil.getRomanNumeral(dayInYear, false);
			 }
			 else if ("W".equals(str1) || "W1".equals(str1)) {
				 // Week in the year numeric value 
				 replacedStr1 = weekInYear+""; 
			 }
			 else if ("W01".equals(str1)) {
				 // Week in the year numeric value padded with leading zeros
				 if (weekInYear < 10) {
					 replacedStr1 = "0"+weekInYear+""; 
				 }
				 else {
					 replacedStr1 = weekInYear+""; 
				 }
			 }     	   
			 else if ("W1o".equals(str1)) {
				 // Week in the year numeric value with ordinal form
				 replacedStr1 = NumberUtil.getOrdinalNumber(weekInYear); 
			 }
			 else if ("Wi".equals(str1)) {
				 // Week in the year numeric value with roman numeral in smallcase
				 replacedStr1 = NumberUtil.getRomanNumeral(weekInYear, true);
			 }
			 else if ("WI".equals(str1)) {
				 // Week in the year numeric value with roman numeral in uppercase
				 replacedStr1 = NumberUtil.getRomanNumeral(weekInYear, false);
			 }
			 else if ("w".equals(str1) || "w1".equals(str1)) {
				 // Week in the month numeric value 
				 replacedStr1 = weekInMonth+""; 
			 }
			 else if ("w01".equals(str1)) {
				 // Week in the month numeric value padded with leading zeros
				 if (weekInMonth < 10) {
					 replacedStr1 = "0"+weekInMonth+""; 
				 }
				 else {
					 replacedStr1 = weekInMonth+""; 
				 }
			 }     	   
			 else if ("w1o".equals(str1)) {
				 // Week in the month numeric value with ordinal form
				 replacedStr1 = NumberUtil.getOrdinalNumber(weekInMonth); 
			 }
			 else if ("wi".equals(str1)) {
				 // Week in the month numeric value with roman numeral in smallcase
				 replacedStr1 = NumberUtil.getRomanNumeral(weekInMonth, true);
			 }
			 else if ("wI".equals(str1)) {
				 // Week in the month numeric value with roman numeral in uppercase
				 replacedStr1 = NumberUtil.getRomanNumeral(weekInMonth, false);
			 }
			 else if ("F".equals(str1)) {
				 // Day in the week numeric value
				 replacedStr1 = dayInWeek+""; 
			 }     	   
			 else if ("FNn".equals(str1)) {
				 // Day in the week, title case
				 replacedStr1 = dayOfWeekNameArray[dayInWeek - 1]; 
			 }
			 else if ("Fn".equals(str1)) {
				 // Day in the week, lowercase
				 replacedStr1 = (dayOfWeekNameArray[dayInWeek - 1]+"").toLowerCase(); 
			 }
			 else if ("FN".equals(str1)) {
				 // Day in the week, uppercase
				 replacedStr1 = (dayOfWeekNameArray[dayInWeek - 1]+"").toUpperCase();
			 }
			 else if ("F1o".equals(str1)) {
				 // Day in the week numeric value with ordinal form
				 replacedStr1 = NumberUtil.getOrdinalNumber(dayInWeek);
			 }
			 else if ("Fi".equals(str1)) {
				 // Day in the week numeric value with roman numeral in smallcase
				 replacedStr1 = NumberUtil.getRomanNumeral(dayInWeek, true);
			 }
			 else if ("FI".equals(str1)) {
				 // Day in the week numeric value with roman numeral in uppercase
				 replacedStr1 = NumberUtil.getRomanNumeral(dayInWeek, false);
			 }
			 else if ("H".equals(str1)) {     			 
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
