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

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import javax.xml.transform.SourceLocator;

import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.FunctionMultiArgs;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;

import xml.xpath31.processor.types.XSDateTime;
import xml.xpath31.processor.types.XSDuration;

/**
 * Implementation of an XPath 3.1 function fn:adjust-dateTime-to-timezone.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncAdjustDateTimeToTimezone extends FunctionMultiArgs {

	private static final long serialVersionUID = -4827800981552403228L;
	
	/**
	 * Class constructor.
	 */
	public FuncAdjustDateTimeToTimezone() {
		m_defined_arity = new Short[] {1, 2}; 
	}
	
	/**
	 * Implementation of the function. The function must return a valid object.
	 * 
	 * @param xctxt						An XPath context object
	 * @return 							A valid XObject
	 *
	 * @throws javax.xml.transform.TransformerException
	 */
	public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException {

		XObject result = null;

		SourceLocator srcLocator = xctxt.getSAXLocator();
		
		XSDateTime arg0XsDateTime = null;
		XSDuration arg1Timezone = null;
		
		if (m_arg0 == null) {
		   result = new ResultSequence();
		   
		   return result;
		}
		else {
			XObject xObj0 = m_arg0.execute(xctxt);

			if (xObj0 instanceof XSDateTime) {
				arg0XsDateTime = (XSDateTime)xObj0; 
			}
			else if ((xObj0 instanceof ResultSequence) && (((ResultSequence)xObj0).size() == 0)) {
				result = new ResultSequence();

				return result;
			}
			else {
				throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath function adjust-dateTime-to-timezone's first "
						                                                                                       + "argument is not an XML Schema dateTime value.", srcLocator);
			}
		}
				
		boolean isFnTimeZoneArgEmptySeq = false;
		
		if (m_arg1 == null) {
			// Get the value of implicit timezone
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
				throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath function adjust-dateTime-to-timezone's second "
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
			   throw new javax.xml.transform.TransformerException("FODT0003 : An XPath function adjust-dateTime-to-timezone's "
							  		                                                                         + "second argument doesn't represent a timezone "
							  		                                                                         + "value within valid duration range. Timezone value "
							  		                                                                         + "can be within the range -PT14H and PT14H.", srcLocator);
		   } 
		}
				
		XSDuration arg0XsTimezone = arg0XsDateTime.getTimezone();
		if ((arg0XsTimezone == null) && isFnTimeZoneArgEmptySeq) {
			result = arg0XsDateTime;
		}
		else if ((arg0XsTimezone == null) && !isFnTimeZoneArgEmptySeq) {
			String arg0StrValue = arg0XsDateTime.stringValue();
			
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
			
			result = XSDateTime.parseDateTime(resultStrValue);
		}
		else if ((arg0XsTimezone != null) && isFnTimeZoneArgEmptySeq) {
			String arg0StrValue = arg0XsDateTime.stringValue();
			int idx = arg0StrValue.indexOf('T');

			String xsDateTimePrefix = arg0StrValue.substring(0, idx);
			String xsDateTimeSuffix = arg0StrValue.substring(idx + 1);
			if (xsDateTimeSuffix.endsWith("Z")) {
				int a1 = xsDateTimeSuffix.indexOf('Z');
				xsDateTimeSuffix = xsDateTimeSuffix.substring(0, a1);
			}
			else if (xsDateTimeSuffix.contains("+")) {
				int a1 = xsDateTimeSuffix.indexOf('-');
				xsDateTimeSuffix = xsDateTimeSuffix.substring(0, a1);
			}
			else if (xsDateTimeSuffix.contains("-")) {
				int a1 = xsDateTimeSuffix.indexOf('-');
				xsDateTimeSuffix = xsDateTimeSuffix.substring(0, a1);
			}
			
			String resultStrValue = xsDateTimePrefix + "T" + xsDateTimeSuffix;
			
			result = XSDateTime.parseDateTime(resultStrValue);
		}
		else if ((arg0XsTimezone != null) && !isFnTimeZoneArgEmptySeq) {						
			int timeZoneHours = arg0XsTimezone.hours();
			int timeZoneMins = arg0XsTimezone.minutes();						
			
			String timeZoneStrValue = null;
			if ((timeZoneHours == 0) && (timeZoneMins == 0)) {
			   timeZoneStrValue = "00:00";			   			     
			}
			else {
			   String hrs1 = (timeZoneHours < 10) ? ("0" + timeZoneHours) : (timeZoneHours + "");
			   String mins1 = (timeZoneMins < 10) ? ("0" + timeZoneMins) : (timeZoneMins + "");
			   
			   timeZoneStrValue = (hrs1 + ":" + mins1);
			   
			   boolean isTimeZoneNegative = arg0XsTimezone.negative();
			   timeZoneStrValue = isTimeZoneNegative ? ("-" + timeZoneStrValue) : ("+" + timeZoneStrValue);
			}
			
			ZoneOffset zoneOffset = ZoneOffset.of(timeZoneStrValue);
			
			OffsetDateTime offsetDateTime = OffsetDateTime.of(arg0XsDateTime.year(), arg0XsDateTime.month(), arg0XsDateTime.day(), 
					                                                                                   arg0XsDateTime.hour(), arg0XsDateTime.minute(), 
					                                                                                   arg0XsDateTime.second(), 0, zoneOffset);			
			
			int hrs2 = arg1Timezone.hours();
			int mins2 = arg1Timezone.minutes();			

			String timeZoneStrValue2 = null;			
			if ((hrs2 == 0) && (mins2 == 0)) {
				timeZoneStrValue2 = "00:00";			   			     
			}
			else {
				String hrsA = (hrs2 < 10) ? ("0" + hrs2) : (hrs2 + "");
				String minsA = (mins2 < 10) ? ("0" + mins2) : (mins2 + "");

				timeZoneStrValue2 = (hrsA + ":" + minsA);
			}
			
			boolean isTimeZoneNegative2 = arg1Timezone.negative();
			timeZoneStrValue2 = isTimeZoneNegative2 ? ("-" + timeZoneStrValue2) : ("+" + timeZoneStrValue2);
			ZoneOffset zoneOffset2 = ZoneOffset.of(timeZoneStrValue2);
			
			OffsetDateTime offsetDateTimeResult = offsetDateTime.withOffsetSameInstant(zoneOffset2);
			String resultDateTimeStrValue = offsetDateTimeResult.toString();
			
			int idx = resultDateTimeStrValue.indexOf('T');
			String prefix1 = resultDateTimeStrValue.substring(0, idx);
			String suffix1 = resultDateTimeStrValue.substring(idx + 1);
			if (suffix1.endsWith("Z")) {
				int a1 = suffix1.indexOf('Z');
				String str1 = suffix1.substring(0, a1);
				String[] strArray1 = str1.split(":");
				if (strArray1.length == 2) {
					str1 = str1 + ":00";
					suffix1 = str1 + "Z";
					resultDateTimeStrValue = prefix1 + "T" + suffix1; 
				}
			}
			else if (suffix1.contains("+")) {
				int a1 = suffix1.indexOf('+');
				String str1 = suffix1.substring(0, a1);
				String str2 = suffix1.substring(a1 + 1);
				String[] strArray1 = str1.split(":");
				if (strArray1.length == 2) {
				   str1 = str1 + ":00";
				   suffix1 = str1 + "+" + str2;
				   resultDateTimeStrValue = prefix1 + "T" + suffix1; 
				}
			}
            else if (suffix1.contains("-")) {
            	int a1 = suffix1.indexOf('-');
				String str1 = suffix1.substring(0, a1);
				String str2 = suffix1.substring(a1 + 1);
				String[] strArray1 = str1.split(":");
				if (strArray1.length == 2) {
				   str1 = str1 + ":00";
				   suffix1 = str1 + "-" + str2;
				   resultDateTimeStrValue = prefix1 + "T" + suffix1; 
				}
			}
			
			result = XSDateTime.parseDateTime(resultDateTimeStrValue);
		}

		return result;

	}

}
