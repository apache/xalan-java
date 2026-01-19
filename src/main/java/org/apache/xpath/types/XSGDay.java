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
package org.apache.xpath.types;

import javax.xml.transform.TransformerException;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.regex.Matcher;
import org.apache.xpath.regex.Pattern;

import xml.xpath31.processor.types.XSAnyAtomicType;
import xml.xpath31.processor.types.XSDate;
import xml.xpath31.processor.types.XSDateTime;

/**
 * Implementation of XML Schema data type xs:gDay.
 * 
 * @author : Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage general
 */
public class XSGDay extends XSAnyAtomicType {

	private static final long serialVersionUID = 5356100798451721396L;

	/**
	 * Class fields to represent components of XML Schema type xs:gDay.  
	 */
	
	private int m_day;
	
    private boolean m_isTimeZoneNegative;
	
	private boolean m_isTimeZoneUtc;
	
	private int m_timezone_hrs;
	
	private int m_timezone_min;
	
	private boolean m_isTimeZoned;
	
	private String m_gDayStrValue;
	
	private static final String XS_GDAY = "xs:gDay";
	
	/**
	 * Default constructor.
	 */
	public XSGDay() {
		// no op
	}
	
	/**
	 * Class constructor.
	 */
	public XSGDay(String gDayStrValue) throws TransformerException {
		
		boolean isXsDate = true;
		
		try {
			// Constructing xs:gDay value, from supplied xs:date lexical value
			XSDate xsDate = XSDate.parseDate(gDayStrValue);
			String timeZoneStr = DateTimeUtil.getTimeZoneStrFromXsDateValue(xsDate);
			
			int day = xsDate.day();
			String str1 = String.valueOf(day);

			if (day < 10) {
				str1 = "0" + str1;  
			}

			if (timeZoneStr != null) {
				str1 += timeZoneStr; 
			}
			
			parse("---" + str1);

			m_gDayStrValue = "---" + str1;
		}
		catch (TransformerException ex) {
			isXsDate = false;
		}
		
		if (!isXsDate) { 
		   try {
			   // Constructing xs:gDay value, from supplied xs:dateTime lexical value
			   XSDateTime xsDateTime = XSDateTime.parseDateTime(gDayStrValue);
			   String timeZoneStr = DateTimeUtil.getTimeZoneStrFromXsDateTimeValue(xsDateTime);

			   int day = xsDateTime.day();
			   String str1 = String.valueOf(day);

			   if (day < 10) {
				   str1 = "0" + str1;  
			   }

			   if (timeZoneStr != null) {
				   str1 += timeZoneStr; 
			   }

			   parse("---" + str1);

			   m_gDayStrValue = "---" + str1;				
		   }
		   catch (TransformerException ex) {
			  // no op  
		   }
		}
		
		if (m_gDayStrValue == null) {
			// Constructing xs:gDay value, from supplied xs:gDay lexical value
			parse(gDayStrValue);

			m_gDayStrValue = gDayStrValue;
		}
	}

	@Override
	public String stringType() {
		return XS_GDAY;
	}
	
	public ResultSequence constructor(ResultSequence seq) {
		ResultSequence result = new ResultSequence();
		
		XObject xObj = seq.item(0);
		String strVal = XslTransformEvaluationHelper.getStrVal(xObj);
		try {
			XSGDay xsGDay = new XSGDay(strVal);
			result.add(xsGDay);
		} catch (TransformerException ex) {
			// no op
		}
		
		return result;
	}

	@Override
	public String stringValue() {
		String result = m_gDayStrValue; 
		if (m_isTimeZoneUtc) {
		   result = result.replace("+00:00", "Z");
		}
		
		return result;
	}
	
	public int getType() {
		return CLASS_GDAY;
	}
	
	/**
	 * Implementation of operation equals, for the type xs:gDay.
	 */
	public boolean eq(XSGDay obj2) {
        boolean result = false;
		
		result = ((m_day == obj2.getDay()) && (m_isTimeZoneNegative == obj2.isTimeZoneNegative()) && 
				  (m_isTimeZoneUtc == obj2.isTimeZoneUtc()) && (m_timezone_hrs == obj2.getTimezoneHrs()) && 
				  (m_timezone_min == obj2.getTimezoneMin()) && (m_isTimeZoned == obj2.isTimeZoned()));
		
		return result;
	}
	
	/**
	 * Implementation of operation not equals, for the type xs:gDay.
	 */
	public boolean ne(XSGDay obj2) {
        boolean result = false;
		
		result = !eq(obj2);
		
		return result;
	}

	@Override
	public String typeName() {
		return "gDay";
	}
	
	/**
	 * Getter method definitions for various components of an XML Schema type xs:gDay. 
	 */
	
	public int getDay() {
		return m_day;
	}

	public boolean isTimeZoneNegative() {
		return m_isTimeZoneNegative;
	}

	public boolean isTimeZoneUtc() {
		return m_isTimeZoneUtc;
	}

	public int getTimezoneHrs() {
		return m_timezone_hrs;
	}

	public int getTimezoneMin() {
		return m_timezone_min;
	}

	public boolean isTimeZoned() {
		return m_isTimeZoned;
	}
	
	/**
	 * Method definition to parse a string value to an XML Schema type 
	 * xs:gDay's component parts. 
	 * 
	 * @param gDayStrValue						    A string value that needs to be parsed 
	 *                                              to a xs:gDay value. 
	 * @throws TransformerException
	 */
	private void parse(String gDayStrValue) throws TransformerException {		
		try {
			if (gDayStrValue.startsWith("---")) {
				String suffixValue = gDayStrValue.substring(3);
				if (suffixValue.endsWith("Z")) {
				    m_day = Integer.valueOf(suffixValue.substring(0,2));
				    m_isTimeZoneUtc = true;
					m_isTimeZoned = true;
				}
				else {
					int plusIdx = suffixValue.indexOf('+');
					if (plusIdx > -1) {
						m_day = Integer.valueOf(suffixValue.substring(0, plusIdx));
						String timeZoneStr = suffixValue.substring(plusIdx + 1);
						if (isTimeZoneStrCorrectlyFormatted(timeZoneStr)) {
							String[] timeZoneStrParts = timeZoneStr.split(":");
							m_timezone_hrs = Integer.valueOf(timeZoneStrParts[0]); 
							m_timezone_min = Integer.valueOf(timeZoneStrParts[1]);
							if ((m_timezone_hrs == 0) && (m_timezone_min == 0)) {
								m_isTimeZoneUtc = true;  
							}
							m_isTimeZoned = true;
						}
						else {
							throw new TransformerException("FORG0001 : A string value " + gDayStrValue + " cannot be parsed to a "
                                                                                                       + "value of type xs:gDay.");
						}
					}
					else {
						int minusIdx = suffixValue.indexOf('-');
						if (minusIdx > -1) {
							m_day = Integer.valueOf(suffixValue.substring(0, minusIdx));
							String timeZoneStr = suffixValue.substring(minusIdx + 1);
							if (isTimeZoneStrCorrectlyFormatted(timeZoneStr)) {
								String[] timeZoneStrParts = timeZoneStr.split(":");
								m_timezone_hrs = Integer.valueOf(timeZoneStrParts[0]); 
								m_timezone_min = Integer.valueOf(timeZoneStrParts[1]);
								if ((m_timezone_hrs == 0) && (m_timezone_min == 0)) {
									m_isTimeZoneUtc = true;  
								}
								m_isTimeZoned = true;
							}
							else {
								throw new TransformerException("FORG0001 : A string value " + gDayStrValue + " cannot be parsed to a "
                                                                                                           + "value of type xs:gDay.");
							}
						}
						else {
							m_day = Integer.valueOf(suffixValue);
						}
					}
				}
			}
			else {
				throw new TransformerException("FORG0001 : A string value " + gDayStrValue + " cannot be parsed to a "
						                                                                   + "value of type xs:gDay.");
			}
		}
		catch (Exception ex) {
			throw new TransformerException("FORG0001 : A string value " + gDayStrValue + " cannot be parsed to a "
                    																   + "value of type xs:gDay.");
		}
	}
	
	/**
	 * Method definition to check the string format of a timezone string.
	 * 
	 * (The regex for timezone's string value is specified within XML Schema 
	 *  datatypes specification)
	 * 
	 * @param strValue					The supplied string value of timezone
	 * @return							Boolean true or false
	 */
	private boolean isTimeZoneStrCorrectlyFormatted(String strValue) {
		boolean result = false;
		
		Pattern pattern = Pattern.compile("((0[0-9]|1[0-3]):[0-5][0-9]|14:00)");
		Matcher matcher = pattern.matcher(strValue);
		result = matcher.matches(); 
		
		return result;
	}

}
