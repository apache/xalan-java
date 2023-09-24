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

import javax.xml.transform.TransformerException;

import org.apache.xpath.objects.ResultSequence;

/**
 * A representation of the XML Schema xs:duration data type.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XSDuration extends XSCtrType {

    private static final long serialVersionUID = -8460416911698841833L;

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
	
	public double timeValue() {
		double ret = 0;
		ret += hours() * 60 * 60;
		ret += minutes() * 60;
		ret += seconds();

		if (negative()) {
		   ret *= -1;
		}
		
		return ret;
	}

	/**
     * A method to construct an xdm sequence comprising a
     * xs:duration value, given input data as argument to 
     * this method.
     * 
	 * @throws TransformerException 
     */
	public ResultSequence constructor(ResultSequence arg) throws TransformerException {
	    ResultSequence resultSeq = new ResultSequence();
	    
	    if (arg.size() == 0) {
	       return resultSeq;     
	    }
	    
	    XSAnyType xsAnyType = (XSAnyType)arg.item(0);
	    
        XSDuration xsDuration = castToDuration(xsAnyType);
        
        resultSeq.add(xsDuration);

		return resultSeq;
	}

	/**
	 * Construct a new XSDuration object, by parsing the supplied string.
	 * 
	 * @param strVal   string to be parsed
	 * 
	 * @return      XSDuration object representing the duration of time supplied
	 */
	public static XSDuration parseDuration(String strVal) throws TransformerException {
		
	    boolean isDurationNegative = false;
		
		int years = 0;
		int months = 0;
		int days = 0;
		int hours = 0;
		int minutes = 0;
		double seconds = 0;

		String pstr = "";
		String tstr = "";

		if (strVal.startsWith("-P")) {
			isDurationNegative = true;
			pstr = strVal.substring(2, strVal.length());
		} else if (strVal.startsWith("P")) {
			isDurationNegative = false;
			pstr = strVal.substring(1, strVal.length());
		} else {
		    throw new TransformerException("XTTE0570 : The supplied string value '" + strVal + "' "
		                                                                                         + "cannot be parsed to a xs:duration value.");
		}

		try {
			int index = pstr.indexOf('Y');
			boolean isAction = false;

			if (index != -1) {
				String digit = pstr.substring(0, index);
				years = Integer.parseInt(digit);
				pstr = pstr.substring(index + 1, pstr.length());
				isAction = true;
			}

			index = pstr.indexOf('M');
			if (index != -1) {
				String digit = pstr.substring(0, index);
				months = Integer.parseInt(digit);
				pstr = pstr.substring(index + 1, pstr.length());
				isAction = true;
			}

			// days
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
					isAction = true;
				}
			}

			// hours
			index = tstr.indexOf('H');
			if (index != -1) {
				String digit = tstr.substring(0, index);
				hours = Integer.parseInt(digit);
				tstr = tstr.substring(index + 1, tstr.length());
				isAction = true;
			}
			// minutes
			index = tstr.indexOf('M');
			if (index != -1) {
				String digit = tstr.substring(0, index);
				minutes = Integer.parseInt(digit);
				tstr = tstr.substring(index + 1, tstr.length());
				isAction = true;
			}
			// seconds
			index = tstr.indexOf('S');
			if (index != -1) {
				String digit = tstr.substring(0, index);
				seconds = Double.parseDouble(digit);
				tstr = tstr.substring(index + 1, tstr.length());
				isAction = true;
			}
			if (!isAction) {
			    throw new TransformerException("XTTE0570 : The supplied string value '" + strVal + "' "
                                                                                               + "cannot be parsed to a xs:duration value.");
			}

		} 
		catch (TransformerException ex) {
	        throw ex;  
	    }
		catch (Exception ex) {
		    throw new TransformerException("XTTE0570 : The supplied string value '" + strVal + "' "
                                                                                              + "cannot be parsed to a xs:duration value."); 
		}

		return new XSDuration(years, months, days, hours, minutes, seconds, isDurationNegative);
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
	
	/**
	 * This method does an equality comparison between, this and
	 * another XSDuration value. 
	 */
	public boolean equals(XSDuration xsDuration) {
       double val1 = value();
       double val2 = xsDuration.value();
       
       return val1 == val2;
    }
	
	/**
     * This method checks whether, this XSDuration value is less
     * than another one.  
     */
    public boolean lt(XSDuration xsDuration) {
       double val1 = value();
       double val2 = xsDuration.value();
       
       return val1 < val2;
    }
    
    /**
     * This method checks whether, this XSDuration value is
     * greater than another one.  
     */
    public boolean gt(XSDuration xsDuration) {
       double val1 = value();
       double val2 = xsDuration.value();
       
       return val1 > val2;
    }
    
    public int getType() {
       return CLASS_XS_DURATION;
    }
    
    /**
     * Do a data type cast, of a XSAnyType value to an XSDuration
     * value.
     *  
     * @throws TransformerException 
     */
    private XSDuration castToDuration(XSAnyType xsAnyType) throws TransformerException {
        
        if (xsAnyType instanceof XSDuration) {
            XSDuration duration = (XSDuration) xsAnyType;
            
            return new XSDuration(duration.year(), duration.month(), duration.days(), duration.hours(), 
                                                              duration.minutes(), duration.seconds(), duration.negative());
        }
        
        return parseDuration(xsAnyType.stringValue());
    }

}
