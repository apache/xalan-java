/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the  "License");
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
/*
 * $Id$
 */
package org.apache.xpath.xs.types;

import org.apache.xpath.objects.ResultSequence;

/**
 * A representation of the XML Schema xs:duration data type.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XSDuration extends XSCtrType {

	private static final String XS_DURATION = "xs:duration";
	
	protected int _year;
	protected int _month;
	protected int _days;
	protected int _hours;
	protected int _minutes;
	protected double _seconds;
	protected boolean _negative;

	/**
	 * Initializes to a XSDuration object with the supplied parameters. If more 
	 * than 24 hours is supplied, the number of days is adjusted accordingly.
	 * The same occurs for minutes and seconds.
	 * 
	 * @param years
	 *            number of years in this duration of time.
	 * @param months
	 *            number of months in this duration of time.
	 * @param days
	 *            number of days in this duration of time
	 * @param hours
	 *            number of hours in this duration of time
	 * @param minutes
	 *            number of minutes in this duration of time
	 * @param seconds
	 *            number of seconds in this duration of time
	 * @param negative
	 *            true if this duration of time represents a backwards passage
	 *            through time. false otherwise.
	 */
	public XSDuration(int years, int months, int days, int hours, int minutes,
			                                       double seconds, boolean negative) {
		_year = years;
		_month = months;
		_days = days;
		_hours = hours;
		_minutes = minutes;
		_seconds = seconds;
		_negative = negative;

		if (_month >= 12) {
			_year += _month / 12;
			_month = _month % 12;
		}

		if (_seconds >= 60) {
			int isec = (int) _seconds;
			double rem = _seconds - (isec);

			_minutes += isec / 60;
			_seconds = isec % 60;
			_seconds += rem;
		}
		
		if (_minutes >= 60) {
			_hours += _minutes / 60;
			_minutes = _minutes % 60;
		}
		
		if (_hours >= 24) {
			_days += _hours / 24;
			_hours = _hours % 24;
		}

	}

	/**
	 * Initializes to the given number of seconds.
	 * 
	 * @param secs  number of seconds in the duration of time
	 */
	public XSDuration(double secs) {
		this(0, 0, 0, 0, 0, Math.abs(secs), secs < 0);
	}

	/**
	 * Initializes to a duration of no time (0days, 0hours, 0minutes, 0seconds).
	 */
	public XSDuration() {
		this(0, 0, 0, 0, 0, 0.0, false);
	}

	public String typeName() {
		return "duration";
	}

	public String stringType() {
		return XS_DURATION;
	}

	/**
	 * Get a string representation of the duration stored.
	 * 
	 * @return   string representation of the duration stored
	 */
	public String stringValue() {
		String returnVal = "";
		
		boolean didSomething = false;
		
		String tret = "";

		if (negative() && !(days() == 0 && hours() == 0 && seconds() == 0))
			returnVal += "-";

		returnVal += "P";

		int years = year();
		if (years != 0)
			returnVal += years + "Y";

		int months = month();
		if (months != 0) {
			returnVal += months + "M";
		}

		if (days() != 0) {
			returnVal += days() + "D";
			didSomething = true;
		}

		// do the "time" bit
		int hours = hours();
		int minutes = minutes();
		double seconds = seconds();
		
		if (hours != 0) {
			tret += hours + "H";
			didSomething = true;
		}
		if (minutes != 0) {
			tret += minutes + "M";
			didSomething = true;
		}
		if (seconds != 0) {
			String doubStr = (new Double(seconds).toString());
			if (doubStr.endsWith(".0")) {
				// string value of x.0 seconds is xS. e.g, 7.0S is converted to
				// 7S.
				tret += doubStr.substring(0, doubStr.indexOf(".0")) + "S";
			} else {
				tret += seconds + "S";
			}
			didSomething = true;
		} else if (!didSomething) {
		    tret += "0S";
		}
		
		if ((year() == 0 && month() == 0) || (hours > 0 || minutes > 0 || seconds > 0)) {
			if (tret.length() > 0) {
				returnVal += "T" + tret;
			}
		}

		return returnVal;
	}

	/**
	 * Get the number of days within the duration of time stored.
	 * 
	 * @return   number of days within the duration of time stored
	 */
	public int days() {
		return _days;
	}

	/**
	 * Get the number of minutes within the duration of time
	 * stored.
	 * 
	 * @return   number of minutes within the duration of time stored
	 */
	public int minutes() {
		return _minutes;
	}

	/**
	 * Get the number of hours within the duration of time stored.
	 * 
	 * @return  number of hours within the duration of time stored
	 */
	public int hours() {
		return _hours;
	}

	/**
	 * Get the number of seconds within the duration of time
	 * stored.
	 * 
	 * @return  number of seconds within the duration of time stored
	 */
	public double seconds() {
		return _seconds;
	}

	/**
	 * Check whether this duration represents a backward passage through
	 * time.
	 * 
	 * @return true   if this duration represents a backward passage through time.
	 *                false otherwise.
	 */
	public boolean negative() {
		return _negative;
	}

	/**
	 * Get the duration of time stored as the number of seconds within it.
	 * 
	 * @return number of seconds making up this duration of time
	 */
	public double value() {
		double ret = days() * 24 * 60 * 60;

		ret += hours() * 60 * 60;
		ret += minutes() * 60;
		ret += seconds();

		if (negative()) {
			ret *= -1;
		}
		
		return ret;
	}
	
	public double time_value() {
		double ret = 0;
		ret += hours() * 60 * 60;
		ret += minutes() * 60;
		ret += seconds();

		if (negative())
			ret *= -1;
		return ret;
	}

	/**
	 * TO DO
	 */
	public ResultSequence constructor(ResultSequence arg) {
		ResultSequence rs = null;		

		return rs;
	}

	/**
	 * Construct a new XSDuration object, by parsing the 
	 * supplied string.
	 * 
	 * @param str   string to be parsed
	 * 
	 * @return      XSDuration object representing the duration of time supplied
	 */
	public static XSDuration parse(String str) {
		boolean negative = false;
		int years = 0;
		int months = 0;
		int days = 0;
		int hours = 0;
		int minutes = 0;
		double seconds = 0;

		// string following the P
		String pstr = "";
		String tstr = "";

		// get the negative and pstr
		if (str.startsWith("-P")) {
			negative = true;
			pstr = str.substring(2, str.length());
		} else if (str.startsWith("P")) {
			negative = false;
			pstr = str.substring(1, str.length());
		} else
			return null;

		try {
			int index = pstr.indexOf('Y');
			boolean did_something = false;

			if (index != -1) {
				String digit = pstr.substring(0, index);
				years = Integer.parseInt(digit);
				pstr = pstr.substring(index + 1, pstr.length());
				did_something = true;
			}

			index = pstr.indexOf('M');
			if (index != -1) {
				String digit = pstr.substring(0, index);
				months = Integer.parseInt(digit);
				pstr = pstr.substring(index + 1, pstr.length());
				did_something = true;
			}

			// get the days
			index = pstr.indexOf('D');

			if (index == -1) {
				if (pstr.startsWith("T")) {
					tstr = pstr.substring(1, pstr.length());
				}
			} else {
				String digit = pstr.substring(0, index);
				days = Integer.parseInt(digit);
				tstr = pstr.substring(index + 1, pstr.length());

				if (tstr.startsWith("T")) {
					tstr = tstr.substring(1, tstr.length());
				} else {
					tstr = "";
					did_something = true;
				}
			}

			// hour
			index = tstr.indexOf('H');
			if (index != -1) {
				String digit = tstr.substring(0, index);
				hours = Integer.parseInt(digit);
				tstr = tstr.substring(index + 1, tstr.length());
				did_something = true;
			}
			// minute
			index = tstr.indexOf('M');
			if (index != -1) {
				String digit = tstr.substring(0, index);
				minutes = Integer.parseInt(digit);
				tstr = tstr.substring(index + 1, tstr.length());
				did_something = true;
			}
			// seconds
			index = tstr.indexOf('S');
			if (index != -1) {
				String digit = tstr.substring(0, index);
				seconds = Double.parseDouble(digit);
				tstr = tstr.substring(index + 1, tstr.length());
				did_something = true;
			}
			if (!did_something) {
				return null;
			}

		} catch (NumberFormatException ex) {
			return null;
		}

		return new XSDuration(years, months, days, hours, minutes, seconds, negative);
	}

	/**
	 * Get the number of years within the duration of time stored.
	 * 
	 * @return number of years within the duration of time stored
	 */
	public int year() {
		return _year;
	}

	/**
	 * Get the number of months within the duration of time stored.
	 * 
	 * @return number of months within the duration of time stored
	 */
	public int month() {
		return _month;
	}

}
