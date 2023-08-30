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

import java.math.BigDecimal;

import javax.xml.transform.TransformerException;

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

	/**
	 * A method to construct an xdm sequence comprising a
	 * xs:dayTimeDuration value, given input data as argument
	 * to this method.
	 */
	public ResultSequence constructor(ResultSequence arg) {
        ResultSequence resultSeq = new ResultSequence();
        
        if (arg.size() == 0) {
           return resultSeq;     
        }
        
        XSAnyType xsAnyType = (XSAnyType)arg.item(0);
        
        XSDuration xsDuration = castToDayTimeDuration(xsAnyType);
        
        resultSeq.add(xsDuration);

        return resultSeq;	
	}

	
	/**
	 * Creates a new XSDuration object, by parsing the supplied String
	 * representation of XSDuration.
	 * 
	 * @param    strVal      String representation of XSDuration value
	 * 
	 * @return               new XSDuration object, representing the 
	 *                       supplied string.
	 */
	public static XSDuration parseDayTimeDuration(String strVal) {
		boolean negative = false;
		int days = 0;
		int hours = 0;
		int minutes = 0;
		double seconds = 0;

		String pstr = null;
		String tstr = null;

		if (strVal.startsWith("-P")) {
			negative = true;
			pstr = strVal.substring(2, strVal.length());
		} else if (strVal.startsWith("P")) {
			negative = false;
			pstr = strVal.substring(1, strVal.length());
		} else {
			return null;
		}

		try {
			int index = pstr.indexOf('D');
			boolean actionStatus = false;

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
					actionStatus = true;
				}
			}

			index = tstr.indexOf('H');
			if (index != -1) {
				String digit = tstr.substring(0, index);
				hours = Integer.parseInt(digit);
				tstr = tstr.substring(index + 1, tstr.length());
				actionStatus = true;
			}

			index = tstr.indexOf('M');
			if (index != -1) {
				String digit = tstr.substring(0, index);
				minutes = Integer.parseInt(digit);
				tstr = tstr.substring(index + 1, tstr.length());
				actionStatus = true;
			}

			index = tstr.indexOf('S');
			if (index != -1) {
				String digit = tstr.substring(0, index);
				seconds = Double.parseDouble(digit);
				tstr = tstr.substring(index + 1, tstr.length());
				actionStatus = true;
			}
			if (actionStatus) {
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
	
	/**
	 * Method to add an XSDayTimeDuration value, to this 
	 * XSDayTimeDuration value.
	 */
	public XSDayTimeDuration add(XSDayTimeDuration xsDayTimeDuration) {       
        double sum = value() + xsDayTimeDuration.value();

        return new XSDayTimeDuration(sum);
    }
	
    /**
     * Method to subtract an XSDayTimeDuration value, from this 
     * XSDayTimeDuration value.
     */
    public XSDayTimeDuration subtract(XSDayTimeDuration xsDayTimeDuration) {       
        double diff = value() - xsDayTimeDuration.value();

        return new XSDayTimeDuration(diff);
    }
    
    /**
     * Method to multiply an XSDayTimeDuration value represented by this
     * object, with a numeric value represented by an argument passed to
     * this method.
     * 
     * @throws TransformerException 
     */
    public XSDayTimeDuration mult(XSAnyType xsAnyType) throws TransformerException {
        
        XSDayTimeDuration result = null;
        
        if (xsAnyType instanceof XSNumericType) {
           String argStrVal = ((XSNumericType)xsAnyType).stringValue();
           XSDouble argDoubleVal = new XSDouble(argStrVal);
           if (argDoubleVal.nan()) {
              throw new TransformerException("FOCA0005 : Cannot multiply an XSDayTimeDuration value with NaN.");  
           }
           else {
              result = new XSDayTimeDuration(value() * argDoubleVal.doubleValue()); 
           }
        }
        else {
           throw new TransformerException("FOCA0005 : Cannot multiply an XSDayTimeDuration value with a "
                                                                                                   + "non-numeric value"); 
        }
        
        return result;
    }
    
    /**
     * Method to divide this XSDayTimeDuration value, by a value (that needs to be
     * either a numeric value or a XSDayTimeDuration value) that is passed as an
     * argument to this method.
     * 
     * @throws TransformerException 
     */
    public XSDayTimeDuration div(XSAnyType xsAnyType) throws TransformerException {
        
        XSDayTimeDuration result = null;
        
        if (xsAnyType instanceof XSNumericType) {
           String argStrVal = ((XSNumericType)xsAnyType).stringValue();
           XSDouble argDoubleVal = new XSDouble(argStrVal);
           if (argDoubleVal.nan()) {
              throw new TransformerException("FOCA0005 : Cannot divide an XSDayTimeDuration value with NaN.");  
           }
           else if (argDoubleVal.zero()) {
              throw new TransformerException("FODT0001 : Cannot divide an XSDayTimeDuration value with zero."); 
           }
           else if (argDoubleVal.infinite()) {
              double doubleResultVal = value() / argDoubleVal.doubleValue();
              result = new XSDayTimeDuration(doubleResultVal);
           }
           else {
              BigDecimal bigDecimal1 = new BigDecimal(value());
              BigDecimal bigDecimal2 = new BigDecimal(argDoubleVal.doubleValue());
              BigDecimal bigDecimalResult = bigDecimal1.divide(new BigDecimal(bigDecimal2.doubleValue()), 
                                                                                                 18, BigDecimal.ROUND_HALF_EVEN);
              result = new XSDayTimeDuration(bigDecimalResult.doubleValue());
           }
        }
        else if (xsAnyType instanceof XSDayTimeDuration) {
           double dbl2 = ((XSDayTimeDuration)xsAnyType).seconds();
           
           if (dbl2 != 0) {
               BigDecimal bigDecimal1 = new BigDecimal(value());
               BigDecimal bigDecimal2 = new BigDecimal(dbl2);
               BigDecimal bigDecimalResult = bigDecimal1.divide(new BigDecimal(bigDecimal2.doubleValue()), 
                                                                                                  18, BigDecimal.ROUND_HALF_EVEN);
               result = new XSDayTimeDuration(bigDecimalResult.doubleValue());  
           }
           else {
              throw new TransformerException("FODT0001 : Cannot divide an XSDayTimeDuration value, with a XSDayTimeDuration "
                                                                                               + "value that represents zero seconds."); 
           }
        }
        else {
           throw new TransformerException("FORG0006 : Cannot divide an XSDayTimeDuration value, with a value that is of "
                                                                                   + "a type other than numeric or XSDayTimeDuration.");
        }
        
        return result;
    }
	
	/**
     * Do a data type cast, of a XSAnyType value to an XSDuration
     * value. 
     */
	private XSDuration castToDayTimeDuration(XSAnyType xsAnyType) {
        
	    if (xsAnyType instanceof XSDuration) {
            XSDuration xsDuration = (XSDuration) xsAnyType;
            
            return new XSDayTimeDuration(xsDuration.days(), xsDuration.hours(), 
                                         xsDuration.minutes(), xsDuration.seconds(), 
                                         xsDuration.negative());
        }
        
        return parseDayTimeDuration(xsAnyType.stringValue());
    }
	
}
