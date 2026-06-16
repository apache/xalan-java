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

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.Locale;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xpath.Expression;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.FunctionOneArg;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;

import xml.xpath31.processor.types.XSDateTime;

/**
 * Implementation of fn:parse-ietf-date function.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncParseIetfDate extends FunctionOneArg {

	private static final long serialVersionUID = 193162856497501251L;

	/**
	 * Class constructor.
	 */
	public FuncParseIetfDate() {
		m_defined_arity = new Short[] { 1 };	
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

		Expression arg0Expr = getArg0();

		XObject xObj0 = arg0Expr.execute(xctxt);

		if (xObj0 instanceof ResultSequence) {
			result = new ResultSequence();

			return result;
		}

		String arg0Str = XslTransformEvaluationHelper.getStrVal(xObj0);
				
		ZonedDateTime zdt = getZonedDateTime(arg0Str);

		int day = zdt.getDayOfMonth();
		String dayStr = (day < 10) ? ("0" + day + "") : (day + "");
		int month = zdt.getMonthValue();
		String monthStr = (month < 10) ? ("0" + month + "") : (month + "");
		int year = zdt.getYear();
		String yearStr = (year + "");

		int hour = zdt.getHour();
		String hourStr = (hour < 10) ? ("0" + hour + "") : (hour + "");
		int min = zdt.getMinute();
		String minStr = (min < 10) ? ("0" + min + "") : (min + "");
		int sec = zdt.getSecond();
		String secStr = (sec < 10) ? ("0" + sec + "") : (sec + "");

		ZoneOffset zoneOffset = zdt.getOffset();
		String zoneOffsetStr = zoneOffset.getId();
		
		String str1 = (yearStr + "-" + monthStr + "-" + dayStr +"T" + hourStr + ":" + minStr + ":" + secStr + zoneOffsetStr);
		
		result = XSDateTime.parseDateTime(str1);

		return result;
	}

	/**
	 * Method definition, to get ZonedDateTime parsed 
	 * value for the supplied IETF formatted date string.
	 * 
	 * The ZonedDateTime object components may be extracted,
	 * to construct xs:dateTime typed value.
	 * 
	 * @param ietfDateStr                  The supplied IETF formatted date string
	 * @return                             An ZonedDateTime parsed object instance 
	 */
	private ZonedDateTime getZonedDateTime(String ietfDateStr) {
		
		ZonedDateTime result = null;
		
		DateTimeFormatter dtf = null;
        boolean isDateValueParseErr = false;
		
		try {
		   result = ZonedDateTime.parse(ietfDateStr, DateTimeFormatter.RFC_1123_DATE_TIME);
		}
		catch (DateTimeParseException ex) {
			isDateValueParseErr = true;
		}
		
		if (isDateValueParseErr) {
			dtf = (new DateTimeFormatterBuilder())
                                             .appendPattern("EEE, d MMM ")
                                             .appendValueReduced(ChronoField.YEAR, 2, 2, 1950)
                                             .appendPattern(" HH:mm:ss z")
					                         .toFormatter(Locale.ENGLISH);
			result = ZonedDateTime.parse(ietfDateStr, dtf);
		}
		
		return result;
	}
	
}
