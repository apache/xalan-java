/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * $Id$
 */
package org.apache.xpath.xs.types;

import org.apache.xpath.objects.ResultSequence;

/**
 * An XML Schema data type representation, of the xs:dayTimeDuration 
 * datatype.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XSDayTimeDuration extends XSDuration {

    private static final long serialVersionUID = 4194060959383397526L;
    
    private static final String XS_DAY_TIME_DURATION = "xs:dayTimeDuration";

	/**
	 * Initializes an XSDayTimeDuration object, with the supplied parameters. If more than 24 
	 * hours is supplied, the number of days is adjusted accordingly. The same occurs for
	 * minutes and seconds.
	 * 
	 * @param days       number of days in this duration of time
	 * @param hours      number of hours in this duration of time
	 * @param minutes    number of minutes in this duration of time
	 * @param seconds    number of seconds in this duration of time
	 * @param negative   true if this duration of time represents a backwards passage
	 *                   through time. false otherwise.
	 */
	public XSDayTimeDuration(int days, int hours, int minutes, double seconds, 
	                                                               boolean negative) {
		super(0, 0, days, hours, minutes, seconds, negative);
	}

	/**
	 * Initializes an XSDayTimeDuration object, to the given number of seconds.
	 * 
	 * @param secs    number of seconds in the duration of time
	 */
	public XSDayTimeDuration(double secs) {
		super(0, 0, 0, 0, 0, Math.abs(secs), secs < 0);
	}

	/**
	 * Initializes an XSDayTimeDuration object, to a duration of no time 
	 * (i.e, 0 days, 0 hours, 0 minutes, 0 seconds).
	 */
	public XSDayTimeDuration() {
		super(0, 0, 0, 0, 0, 0.0, false);
	}

	public ResultSequence constructor(ResultSequence arg) {
	    // TO DO
	    return null;	
	}

	
	/**
	 * Creates a new XSDuration object, by parsing the supplied String
	 * representation of XSDuration.
	 * 
	 * @param str      String representation of XSDuration
	 * 
	 * @return         new XSDuration object, representing the supplied string
	 */
	public static XSDuration parseDTDuration(String str) {
		boolean negative = false;
		int days = 0;
		int hours = 0;
		int minutes = 0;
		double seconds = 0;

		String pstr = null;
		String tstr = null;

		if (str.startsWith("-P")) {
			negative = true;
			pstr = str.substring(2, str.length());
		} else if (str.startsWith("P")) {
			negative = false;
			pstr = str.substring(1, str.length());
		} else {
			return null;
		}

		try {
			int index = pstr.indexOf('D');
			boolean did_something = false;

			if (index == -1) {
				if (pstr.startsWith("T")) {
					tstr = pstr.substring(1, pstr.length());
				} else {
					return null;
				}
			} else {
				String digit = pstr.substring(0, index);
				days = Integer.parseInt(digit);
				tstr = pstr.substring(index + 1, pstr.length());

				if (tstr.startsWith("T")) {
					tstr = tstr.substring(1, tstr.length());
				} else {
					if (tstr.length() > 0) {
						return null;
					}
					tstr = "";
					did_something = true;
				}
			}

			index = tstr.indexOf('H');
			if (index != -1) {
				String digit = tstr.substring(0, index);
				hours = Integer.parseInt(digit);
				tstr = tstr.substring(index + 1, tstr.length());
				did_something = true;
			}

			index = tstr.indexOf('M');
			if (index != -1) {
				String digit = tstr.substring(0, index);
				minutes = Integer.parseInt(digit);
				tstr = tstr.substring(index + 1, tstr.length());
				did_something = true;
			}

			index = tstr.indexOf('S');
			if (index != -1) {
				String digit = tstr.substring(0, index);
				seconds = Double.parseDouble(digit);
				tstr = tstr.substring(index + 1, tstr.length());
				did_something = true;
			}
			if (did_something) {
				if (tstr.length() != 0) {
					return null;
				}
			} else {
				return null;
			}

		} catch (NumberFormatException err) {
			return null;
		}

		return new XSDayTimeDuration(days, hours, minutes, seconds, negative);
	}

	/**
	 * Get the datatype's name.
	 * 
	 * @return   'dayTimeDuration', which is this datatype's name
	 */
	public String typeName() {
		return "dayTimeDuration";
	}

	/**
	 * Get the datatype's name.
	 * 
	 * @return  'xs:dayTimeDuration', which is this datatype's name
	 */
	public String stringType() {
		return XS_DAY_TIME_DURATION;
	}
	
}