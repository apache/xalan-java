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

import javax.xml.transform.SourceLocator;

import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.FunctionMultiArgs;
import org.apache.xpath.functions.WrongNumberArgsException;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;

import xml.xpath31.processor.types.XSDateTime;
import xml.xpath31.processor.types.XSDuration;
import xml.xpath31.processor.types.XSTime;

/**
 * Implementation of an XPath 3.1 function fn:adjust-time-to-timezone.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncAdjustTimeToTimezone extends FunctionMultiArgs {

	private static final long serialVersionUID = 1834834446315499352L;

	/**
	 * Class constructor.
	 */
	public FuncAdjustTimeToTimezone() {
		m_defined_arity = new Short[] {1, 2}; 
	}
	
	/**
	 * Evaluate the function. The function must return a valid object.
	 * 
	 * @param xctxt						An XPath context object
	 * @return 							A valid XObject
	 *
	 * @throws javax.xml.transform.TransformerException
	 */
	public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException {

		XObject result = null;

		SourceLocator srcLocator = xctxt.getSAXLocator();
		
		XSTime arg0XsTime = null;
		XSDuration arg1Timezone = null;
		
		if (m_arg0 == null) {
		   result = new ResultSequence();
		   
		   return result;
		}
		else {
			XObject xObj0 = m_arg0.execute(xctxt);

			if (xObj0 instanceof XSTime) {
				arg0XsTime = (XSTime)xObj0; 
			}
			else if ((xObj0 instanceof ResultSequence) && (((ResultSequence)xObj0).size() == 0)) {
				result = new ResultSequence();

				return result;
			}
			else {
				throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath function adjust-time-to-timezone's first "
						                                                                                       + "argument is not an XML Schema time value.", srcLocator);
			}
		}
				
		boolean isFnTimeZoneArgEmptySeq = false;
		
		if (m_arg1 == null) {
			// Get the value of implicit timezone, from XPath context
			arg1Timezone = xctxt.getTimezone();
		}
		else {
			XObject xObj1 = m_arg1.execute(xctxt);

			if (xObj1 instanceof XSDuration) {
				arg1Timezone = (XSDuration)xObj1; 
			}
			else if ((xObj1 instanceof ResultSequence) && (((ResultSequence)xObj1).size() == 0)) {
				isFnTimeZoneArgEmptySeq = true;
			}
			else {
				throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath function adjust-time-to-timezone's second "
																											  + "argument is not an XML Schema dayTimeDuration "
																											  + "value for timezone.", srcLocator);  
			}
		}
		
		if (arg1Timezone != null) {
		   int tzHrs = arg1Timezone.hours();
		   int tzMins = arg1Timezone.minutes();
		   int totalTzHrs = (tzHrs + (tzMins / 60));
		   totalTzHrs = (arg1Timezone.negative() ? (-1 * totalTzHrs) : totalTzHrs);
		   if ((totalTzHrs < -14) || (totalTzHrs > 14)) {
			   throw new javax.xml.transform.TransformerException("FODT0003 : An XPath function adjust-time-to-timezone's "
							  		                                                                         + "second argument doesn't represent a timezone "
							  		                                                                         + "value within valid duration range. Timezone value "
							  		                                                                         + "can be within the range -PT14H and PT14H.", srcLocator);
		   } 
		}
				
		XSDuration arg0XsTimezone = arg0XsTime.getTimezone();
		if ((arg0XsTimezone == null) && isFnTimeZoneArgEmptySeq) {			
			result = arg0XsTime;
		}
		else if ((arg0XsTimezone == null) && !isFnTimeZoneArgEmptySeq) {		
			String arg0StrValue = arg0XsTime.stringValue();
			
			int timeZoneHours = arg1Timezone.hours();
			int timeZoneMins = arg1Timezone.minutes();
			
			String timeZoneStrValue = null;
			if ((timeZoneHours == 0) && (timeZoneMins == 0)) {
			   timeZoneStrValue = "00:00";
			}
			else {
			   String hrs1 = (timeZoneHours < 10) ? ("0" + timeZoneHours) : (timeZoneHours + "");
			   String mins1 = (timeZoneMins < 10) ? ("0" + timeZoneMins) : (timeZoneMins + "");
			   
			   timeZoneStrValue = (hrs1 + ":" + mins1);
			   
			   boolean isTimeZoneNegative = arg1Timezone.negative();
			   timeZoneStrValue = isTimeZoneNegative ? ("-" + timeZoneStrValue) : ("+" + timeZoneStrValue); 
			}
			
			String resultStrValue = arg0StrValue + timeZoneStrValue;  
			
			result = XSTime.parseTime(resultStrValue);
		}
		else if ((arg0XsTimezone != null) && isFnTimeZoneArgEmptySeq) {			
			String arg0StrValue = arg0XsTime.stringValue();														
			
			String resultStrValue = null;
			if (arg0StrValue.endsWith("Z")) {
			   int idx = arg0StrValue.indexOf('Z');
			   resultStrValue = arg0StrValue.substring(0, idx); 
			}
			else if (arg0StrValue.contains("+")) {
			   int idx = arg0StrValue.indexOf('+');
			   resultStrValue = arg0StrValue.substring(0, idx);
			}
            else if (arg0StrValue.contains("-")) {
               int idx = arg0StrValue.indexOf('-');
 			   resultStrValue = arg0StrValue.substring(0, idx);
			}
			
			result = XSTime.parseTime(resultStrValue);
		}
		else if ((arg0XsTimezone != null) && !isFnTimeZoneArgEmptySeq) {						
			int timeZoneHours = arg1Timezone.hours();
			int timeZoneMins = arg1Timezone.minutes();						
			
			String timeZoneStrValue = null;
			if ((timeZoneHours == 0) && (timeZoneMins == 0)) {
			   timeZoneStrValue = "00:00";			   			     
			}
			else {
			   String hrs1 = (timeZoneHours < 10) ? ("0" + timeZoneHours) : (timeZoneHours + "");
			   String mins1 = (timeZoneMins < 10) ? ("0" + timeZoneMins) : (timeZoneMins + "");
			   
			   timeZoneStrValue = (hrs1 + ":" + mins1);
			   
			   boolean isTimeZoneNegative = arg1Timezone.negative();
			   timeZoneStrValue = isTimeZoneNegative ? ("-" + timeZoneStrValue) : ("+" + timeZoneStrValue);
			}
			
			String arg0StrValue = arg0XsTime.stringValue();	
			
			// An algorithm for this logic is specified within XPath F&O spec
			
			XSDateTime xsDateTime = XSDateTime.parseDateTime("1972-12-31T" + arg0StrValue);
			
			FuncAdjustDateTimeToTimezone funcAdjustDateTimeToTimezone = new FuncAdjustDateTimeToTimezone();
            try {
				funcAdjustDateTimeToTimezone.setArg(xsDateTime, 0);
				funcAdjustDateTimeToTimezone.setArg(arg1Timezone, 1);
			} 
            catch (WrongNumberArgsException ex) {
                // no op
			}            
            
            XSDateTime xsDateTime2 = (XSDateTime)(funcAdjustDateTimeToTimezone.execute(xctxt));
            
            int hours = xsDateTime2.hour();
            String hrsStr = ((hours < 10) ? "0" + hours : hours + "");
            int mins = xsDateTime2.minute();
            String minsStr = ((mins < 10) ? "0" + mins : mins + "");
            int secs = xsDateTime2.second();
            String secsStr = ((secs < 10) ? "0" + secs : secs + "");
            
            String resultStrValue = (hrsStr + ":" + minsStr + ":" + secsStr + timeZoneStrValue);   
			
			result = XSTime.parseTime(resultStrValue);
		}

		return result;

	}

}
