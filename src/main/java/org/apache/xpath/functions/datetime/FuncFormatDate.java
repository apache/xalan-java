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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xml.utils.XMLString;
import org.apache.xpath.Expression;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.FunctionMultiArgs;
import org.apache.xpath.objects.XObject;

import xml.xpath31.processor.types.XSDate;
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
    	 
    	 // The 'value' xs:date argument expression 
    	 Expression arg0 = getArg0();
    	 
    	 // The 'picture' xs:string argument expression
    	 Expression arg1 = getArg1();
    	     	     	 
    	 String arg0Str = null;
    	 XSDate arg0Date = null;
    	     	 
    	 if (arg0 != null) {    		     		 
    		 XObject xObj0 = arg0.execute(xctxt);
    		 arg0Str = XslTransformEvaluationHelper.getStrVal(xObj0);
    		 
    		 try {
    			 arg0Date = XSDate.parseDate(arg0Str);
    		 }
    		 catch (TransformerException ex) {
    			throw new javax.xml.transform.TransformerException("FOAP0001 : An XPath 3.1 function call 'format-date' doesn't have a well "
																														   + "formatted XML Schema date value as its "
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
																							    						 + "formatted XML Schema date value as its "
																							    						 + "first argument.", srcLocator); 
    			 }
    		 }
    		 else {
    			 throw new javax.xml.transform.TransformerException("FOAP0001 : An XPath 3.1 function call format-date has a null value for "
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
    	 
    	 Pattern pattern = Pattern.compile("\\[(.*?)\\]");
    	 Matcher regexMatcher = pattern.matcher(arg1Str);
    	 
    	 List<RegexMatchInfo> regexMatchInfoList = new ArrayList<RegexMatchInfo>();
    	 
    	 while (regexMatcher.find()) {
     		int idx1 = regexMatcher.start();
     		int idx2 = regexMatcher.end();
     		RegexMatchInfo regexMatchInfo = new RegexMatchInfo();
     		regexMatchInfo.setStartIdx(idx1);
     		regexMatchInfo.setEndIdx(idx2);
     		regexMatchInfoList.add(regexMatchInfo);
     	}
     	
     	regexMatcher.reset();
     	
     	Calendar calendar = arg0Date.getCalendar();     	     	
     	int year = calendar.get(Calendar.YEAR);
     	int month = calendar.get(Calendar.MONTH) + 1;
     	int dayOfMonth = calendar.get(Calendar.DATE);
     	int weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
     	int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
     	
     	String[] monthNameArray = new String[] {"January", "February", "March", "April", "May", "June", "July", "August", 
     			                                                                                "September", "October", "November", "December"};
     	
     	String[] dayOfWeekNameArray = new String[] {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
     	
     	int size1 = regexMatchInfoList.size();
     	StringBuffer resultStrBuff = new StringBuffer();
     	
     	for (int i = 0; i < size1; i++) {
     	   RegexMatchInfo regexMatchInfo = regexMatchInfoList.get(i);
     	   int m = regexMatchInfo.getStartIdx();
     	   int n = regexMatchInfo.getEndIdx();     	        	   
     	   if ((i == 0) && (m > 0)) {
     		  resultStrBuff.append(arg1Str.substring(0, m)); 
     	   }
     	   else if (i > 0) {
     		  RegexMatchInfo regexMatchInfo2 = regexMatchInfoList.get(i - 1);
     		  int nPrev = regexMatchInfo2.getEndIdx();
     		  resultStrBuff.append(arg1Str.substring(nPrev, m));
     	   }
     	   
     	   String str1 = arg1Str.substring(m + 1, n - 1);
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
     		  replacedStr1 = toOrdinal(month);
 	       }
     	   else if ("Mi".equals(str1)) {
     		  // Month numeric value with roman numeral in smallcase
     		  replacedStr1 = toRoman(month, true);
     	   }
     	   else if ("MI".equals(str1)) {
     		  // Month numeric value with roman numeral in uppercase
     		  replacedStr1 = toRoman(month, false);
     	   }
     	   else if ("D".equals(str1) || "D1".equals(str1)) {
     		  // Day of month numeric value
     		  replacedStr1 = dayOfMonth+""; 
     	   }
     	   else if ("D01".equals(str1)) {
     		   // Day of month numeric value padded with leading zeros
     		   if (dayOfMonth < 10) {
     			  replacedStr1 = "0"+dayOfMonth+"";
     		   }
     		   else {
     			  replacedStr1 = dayOfMonth+""; 
     		   }
     	   }     	   
     	   else if ("D1o".equals(str1)) {
     		  // Day of month numeric value with ordinal form
     		  replacedStr1 = toOrdinal(dayOfMonth); 
     	   }
     	   else if ("Di".equals(str1)) {
     		  // Day of month numeric value with roman numeral in smallcase
     		  replacedStr1 = toRoman(dayOfMonth, true);
     	   }
     	   else if ("DI".equals(str1)) {
     		  // Day of month numeric value with roman numeral in uppercase
     		  replacedStr1 = toRoman(dayOfMonth, false);
     	   }
     	   else if ("W".equals(str1) || "W1".equals(str1)) {
     		  // Week of the year numeric value 
     		  replacedStr1 = weekOfYear+""; 
     	   }
     	   else if ("W01".equals(str1)) {
     		   // Week of the year numeric value padded with leading zeros
     		   if (weekOfYear < 10) {
     			  replacedStr1 = "0"+weekOfYear+""; 
     		   }
     		   else {
     			  replacedStr1 = weekOfYear+""; 
     		   }
     	   }     	   
     	   else if ("W1o".equals(str1)) {
     		  // Week of the year numeric value with ordinal form
     		  replacedStr1 = toOrdinal(weekOfYear); 
     	   }
     	   else if ("Wi".equals(str1)) {
     		  // Week of the year numeric value with roman numeral in smallcase
     		  replacedStr1 = toRoman(weekOfYear, true);
     	   }
     	   else if ("WI".equals(str1)) {
     		  // Week of the year numeric value with roman numeral in uppercase
     		  replacedStr1 = toRoman(weekOfYear, false);
     	   }
     	   else if ("F".equals(str1)) {
     		  // Day of the week numeric value
     		  replacedStr1 = dayOfWeek+""; 
     	   }     	   
     	   else if ("FNn".equals(str1)) {
     		  // Day of the week, title case
     		  replacedStr1 = dayOfWeekNameArray[dayOfWeek - 1]; 
     	   }
     	   else if ("Fn".equals(str1)) {
     		  // Day of the week, lowercase
     		  replacedStr1 = (dayOfWeekNameArray[dayOfWeek - 1]+"").toLowerCase(); 
     	   }
     	   else if ("FN".equals(str1)) {
     		  // Day of the week, uppercase
     		  replacedStr1 = (dayOfWeekNameArray[dayOfWeek - 1]+"").toUpperCase();
     	   }
     	   else if ("F1o".equals(str1)) {
     		  // Day of the week numeric value with ordinal form
     		  replacedStr1 = toOrdinal(dayOfWeek);
     	   }
     	   else if ("Fi".equals(str1)) {
     		  // Day of the week numeric value with roman numeral in smallcase
     		  replacedStr1 = toRoman(dayOfWeek, true);
     	   }
     	   else if ("FI".equals(str1)) {
     		  // Day of the week numeric value with roman numeral in uppercase
     		  replacedStr1 = toRoman(dayOfWeek, false);
     	   }
     	   
     	   resultStrBuff.append(replacedStr1);
     	}
     	
     	result = new XSString(resultStrBuff.toString());
     	
    	return result;
     }
     
     /**
      * A class representing, a pair of string index values,
      * for a substring that matched with the fn:format-date 
      * function first argument's infix strings of form [..].
      */
     class RegexMatchInfo {    	
     	private int startIdx;
     	
     	private int endIdx;
     	
     	/**
     	 * Class constructor.
     	 */
     	public RegexMatchInfo() {
     	    // no op
     	}

 		public int getStartIdx() {
 			return startIdx;
 		}

 		public void setStartIdx(int startIdx) {
 			this.startIdx = startIdx;
 		}

 		public int getEndIdx() {
 			return endIdx;
 		}

 		public void setEndIdx(int endIdx) {
 			this.endIdx = endIdx;
 		}
     }
     
     /**
      * Method definition, to transform a positive integer
      * value to its string ordinal representation.
      * 
      * @param n                   The supplied integer value
      * @return                    The result string ordinal value
      */
     private String toOrdinal(int n) {
    	
    	String result = null;
    	
    	String[] suffixes = new String[] { "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th" };
        switch (n % 100) {
	        case 11:
	        case 12:
	        case 13:
	        	result = (n + "th");
	        default:
	        	result = (n + suffixes[n % 10]);
        }
    	
    	return result;
     }
     
     /**
      * Method definition, to convert a decimal integer
      * to a roman numeral, within the decimal integer range
      * 1 upto 3999.
      * 
      * @param num					  The supplied decimal integer value
      * @param smallcase              Boolean value, indicating whether resulting
      *                               roman numeral should be with small or capital
      *                               case.
      * @return                       The computed roman numeral
      */
     private String toRoman(int num, boolean smallcase) {
         
    	 String result = null;

    	 if ((num < 1) || (num > 3999)) {
    		 result = "invalid_range";
    	 }

    	 int[] values = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};

    	 String[] romanChars = null;
    	 if (smallcase) {
    		 romanChars = new String[] {"m", "cm", "d", "cd", "c", "xc", "l", "xl", "x", "ix", "v", "iv", "i"}; 
    	 }
    	 else {
    		 romanChars = new String[] {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
    	 }

    	 StringBuilder strBuff = new StringBuilder();
    	 for (int i = 0; i < values.length; i++) {
    		 while (num >= values[i]) {
    			 num -= values[i];
    			 strBuff.append(romanChars[i]);
    		 }
    	 }

    	 result = strBuff.toString(); 

    	 return result;
     }

}
