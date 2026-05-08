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
import org.apache.xpath.Expression;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.FunctionMultiArgs;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;

import xml.xpath31.processor.types.XSDate;
import xml.xpath31.processor.types.XSDuration;
import xml.xpath31.processor.types.XSString;

/**
 * Implementation of an XPath 3.1 function fn:format-date.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncFormatDate extends FunctionMultiArgs {

	private static final long serialVersionUID = -4170958094059871893L;
	
	/**
     * Class constructor.
     */
    public FuncFormatDate() {
  	   m_defined_arity = new Short[] {2, 5}; 
    }
    
    /**
     * Evaluate the function. The function must return a valid object.
     * 
     * @param xctxt The current execution context
     * @return A valid XObject
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
    	 XSDate arg0Date = null;
    	     	 
    	 if (arg0 != null) {    		     		 
    		 XObject xObj0 = arg0.execute(xctxt);
    		 
    		 if ((xObj0 instanceof ResultSequence) && (((ResultSequence)xObj0).size() == 0)) {
    			result = new ResultSequence();
    			
    			return result;
    		 }
    		 
    		 arg0Str = XslTransformEvaluationHelper.getStrVal(xObj0);
    		 
    		 try {
    			 arg0Date = XSDate.parseDate(arg0Str);
    		 }
    		 catch (TransformerException ex) {
    			throw new javax.xml.transform.TransformerException("FOAP0001 : An XPath 3.1 function call 'format-date' doesn't have a well "
																														   + "formatted date value as its "
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
    			 throw new javax.xml.transform.TransformerException("FOAP0001 : An XPath 3.1 function call 'format-date' doesn't "
																								    					 + "have a date input argument, because the "
																								    					 + "context node is absent.", srcLocator);
    		 }

    		 if (arg0Str != null) {
    			 try {
    				 arg0Date = XSDate.parseDate(arg0Str);
    			 }
    			 catch (TransformerException ex) {
    				 throw new javax.xml.transform.TransformerException("FOAP0001 : An XPath 3.1 function call 'format-date' doesn't have a well "
																							    						 + "formatted date value as its "
																							    						 + "first argument.", srcLocator); 
    			 }
    		 }
    		 else {
    			 throw new javax.xml.transform.TransformerException("FOAP0001 : An XPath 3.1 function call 'format-date' has a null value for "
    					 																								 + "its first date value argument.", srcLocator);
    		 } 
    	 }
    	     	     	 
    	 String arg1Str = null;
    	 
    	 if (arg1 != null) {     		
    		 XObject xObj1 = arg1.execute(xctxt);    	 
    		 arg1Str = XslTransformEvaluationHelper.getStrVal(xObj1);
    	 }
    	 else {    	 
    		 throw new javax.xml.transform.TransformerException("FOAP0001 : An XPath 3.1 function call 'format-date' has a "
    				 																									+ "missing picture argument.", srcLocator);
    	 }    	     	     	
     	
     	 String resultStr1 = replaceFormatTemplate(arg0Date, arg1Str);
     	
     	 result = new XSString(resultStr1);
     	
    	 return result;
     }

     /**
      * Method definition, to replace [..] placeholders within the
      * supplied picture string, with xs:date value components that is
      * supplied as an argument to this method.
      * 
      * The picture string comprises of format placeholders and/or literal
      * text.
      * 
      * @param xsDate                     The supplied xs:date value object
      * @param picString                  The supplied picture string
      * @return                           The result of picture string transformation
      */
	 private String replaceFormatTemplate(XSDate xsDate, String picString) {
		 
		 String result = null;
		 
		 List<RegexMatchInfo> regexMatchInfoList = RegexUtil.getRegexMatchInfoList("\\[(.*?)\\]", picString);
     	
     	 Calendar calendar = xsDate.getCalendar();
     	
     	 int year = calendar.get(Calendar.YEAR);
     	 int month = calendar.get(Calendar.MONTH) + 1;
     	 int dayInYear = calendar.get(Calendar.DAY_OF_YEAR);
     	 int dayInMonth = calendar.get(Calendar.DATE);
     	 int weekInYear = calendar.get(Calendar.WEEK_OF_YEAR);
     	 int weekInMonth = calendar.get(Calendar.WEEK_OF_MONTH);
     	 int dayInWeek = calendar.get(Calendar.DAY_OF_WEEK);
     	 
     	 XSDuration timezone = xsDate.getTimezone();
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
