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
package org.apache.xpath.xs.types;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.xml.transform.TransformerException;

import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;

/**
 * An XML Schema data type representation, of the xs:time datatype.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XSTime extends XSCalendarType {

    private static final long serialVersionUID = -2086065287703853879L;
    
    private static final String XS_TIME = "xs:time";
    
    private Calendar _calendar;
    
    private boolean _timezoned;
    
    private XSDuration _tz;
    
    /**
     * The value of this class field, stores the fact that whether this
     * XSTime object is constructed via XPath function call fn:current-time().
     */
    private boolean isPopulatedFromFnCurrentTime = false;
    
    /**
     * Class constructor.
     * 
     * Construct an XSTime object, with the provided time and timezone 
     * values.
     * 
     * @param cal   the java.util.Calendar representation of the time to be stored
     * @param tz    the timezone (this could be possibly null) associated with this 
     *              XSTime object.
     */
    public XSTime(Calendar cal, XSDuration tz) {
        _calendar = cal;
        _tz = tz;
        
        if (tz == null) {
           _timezoned = false;
        }
        else {
           _timezoned = true;
        }
    }

    /**
     * Class constructor.
     * 
     * Construct an XSTime object, and initialize it to the current time.
     */
    public XSTime() {
        this (new GregorianCalendar(TimeZone.getDefault()), null);
    }

    @Override
    public ResultSequence constructor(ResultSequence arg) throws TransformerException {
        ResultSequence resultSeq = new ResultSequence();
        
        if (arg.size() == 0) {
           return resultSeq;     
        }
        
        XSAnyType xsAnyType = (XSAnyType)arg.item(0);        
        XSTime xsTime = castToTime(xsAnyType);
        
        resultSeq.add(xsTime);
        
        return resultSeq;
    }
    
    /**
     * Parse a string representation of a time value, and construct an new 
     * XSTime object.
     */
    public static XSTime parseTime(String strVal) throws TransformerException {

        String refDate = "1955-07-12T";
        
        XSDateTime xsDateTime = XSDateTime.parseDateTime(refDate + strVal);
        if (xsDateTime == null) {
           throw new TransformerException("XTTE0570 : The supplied string value '" + strVal + "' "
                                                                       + "cannot be parsed to a xs:time value.");
        }

        return new XSTime(xsDateTime.getCalendar(), xsDateTime.getTimezone());
    }
    
    /**
     * Get the datatype's name.
     * 
     * @return   "time" which is this datatype's name
     */
    @Override
    public String typeName() {
        return "time";
    }

    /**
     * Get the datatype's full name.
     * 
     * @return   "xs:time" which is this datatype's full name
     */
    @Override
    public String stringType() {
        return XS_TIME;
    }
    
    /**
     * Get a java.util.Calendar representation of an time value stored, 
     * within this XSTime object.
     * 
     * @return    Calendar representation of the time stored
     */
    public Calendar getCalendar() {
        return _calendar;
    }
    
    public XSDuration getTimezone() {
        return _tz;
    }
    
    /**
     * Get the hour value stored as an integer within this
     * XSTime object.
     * 
     * @return   the hour value stored
     */
    public int hour() {
        return _calendar.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * Get the minute value stored as an integer within this
     * XSTime object.
     * 
     * @return   the minute value stored
     */
    public int minute() {
        return _calendar.get(Calendar.MINUTE);
    }
    
    /**
     * Get the second value stored as an integer within this
     * XSTime object.
     * 
     * @return    the seconds value stored
     */
    public double second() {
        double secondVal = _calendar.get(Calendar.SECOND);
        double millisecVal = _calendar.get(Calendar.MILLISECOND);

        millisecVal /= 1000;
        secondVal += millisecVal;
        
        return secondVal;
    }
    
    /**
     * Check whether this XSTime object has an, timezone associated with it.
     * 
     * @return true    if there is a timezone associated with this XSTime object.
     *                 false otherwise.
     */
    public boolean isTimetimezoned() {
        return _timezoned;
    }

    @Override
    public String stringValue() {
        String returnVal = "";
        
        Calendar calendarVal = getCalendar();
        returnVal += XSDateTime.padInt(calendarVal.get(Calendar.HOUR_OF_DAY), 2);
        
        returnVal += ":";
        returnVal += XSDateTime.padInt(calendarVal.get(Calendar.MINUTE), 2);
        

        returnVal += ":";
        int intSec = (int) second();
        double doubleSec = second();

        if ((doubleSec - intSec) == 0.0) {
            returnVal += XSDateTime.padInt(intSec, 2);
        }
        else {
            if (doubleSec < 10.0) {
               returnVal += "0" + doubleSec;
            }
            else {
               returnVal += doubleSec;
            }
        }

        if (isTimetimezoned()) {
            int hrs = _tz.hours();
            int min = _tz.minutes();
            double secs = _tz.seconds();
            if (hrs == 0 && min == 0 && secs == 0) {
               returnVal += "Z";
            }
            else {
               String timezoneStr = "";
               if (_tz.negative()) {
                  timezoneStr += "-";  
               }
               else {
                  timezoneStr += "+"; 
               }
               timezoneStr += XSDateTime.padInt(hrs, 2);  
               timezoneStr += ":";
               timezoneStr += XSDateTime.padInt(min, 2);
              
               returnVal += timezoneStr;
            }
         }

         return returnVal;
    }
    
    /**
     * Determine whether, two XSTime objects are equal.
     */
    public boolean equals(XSTime xsTime) {
        boolean isXsTimeEqual = false;
        
        int hour1 = hour();
        int mins1 = minute();
        double secs1 = second();
        
        int hour2 = xsTime.hour();
        int mins2 = xsTime.minute();
        double secs2 = xsTime.second();
        XSDuration tz1 = getTimezone();
        XSDuration tz2 = xsTime.getTimezone();
        
        isXsTimeEqual = ((hour1 == hour2) && (mins1 == mins2) && (secs1 == secs2)) && 
                                                            isTimezoneEqual(tz1, tz2, isPopulatedFromFnCurrentTime, 
                                                                            xsTime.isPopulatedFromFnCurrentTime());
        
        return isXsTimeEqual;
    }
    
    @Override
    public boolean equals(Object obj) {
       boolean isTimeEqual = false;
        
       if (obj instanceof XSTime) {
    	   isTimeEqual = this.equals((XSTime)obj);  
       }
       
       return isTimeEqual;
    }
    
    @Override
    public int hashCode() {       
       String strVal = stringValue();       
       
       return strVal.hashCode();
    }
    
    /**
     * Determine whether, this XSTime object is less than, the XSTime 
     * object provided as an argument to this method. 
     */
    public boolean lt(XSTime xsTime) {
       boolean isTimeBefore = false;
       
       int hour1 = hour();
       int mins1 = minute();
       double secs1 = second();
       
       int hour2 = xsTime.hour();
       int mins2 = xsTime.minute();
       double secs2 = xsTime.second();
       
       if (hour1 < hour2) {
    	  isTimeBefore = true; 
       }
       else if (hour1 == hour2) {
          if (mins1 < mins2) {
        	 isTimeBefore = true;  
          }
          else if (mins1 == mins2) {
             if (secs1 < secs2) {
            	isTimeBefore = true;  
             }
          }
       }
    
       return isTimeBefore;
    }
    
    /**
     * Determine whether, this XSTime object is greater than, the 
     * XSTime object provided as an argument to this method. 
     */
    public boolean gt(XSTime xsTime) {
    	boolean isTimeAfter = false;
        
        int hour1 = hour();
        int mins1 = minute();
        double secs1 = second();
        
        int hour2 = xsTime.hour();
        int mins2 = xsTime.minute();
        double secs2 = xsTime.second();
        
        if (hour1 > hour2) {
        	isTimeAfter = true; 
        }
        else if (hour1 == hour2) {
           if (mins1 > mins2) {
        	   isTimeAfter = true;  
           }
           else if (mins1 == mins2) {
              if (secs1 > secs2) {
            	  isTimeAfter = true;  
              }
           }
        }
     
        return isTimeAfter;
    }
    
    /**
     * Implementation of addition operation between this XSTime value, and a
     * supplied value (as per XPath 3.1 spec, xs:dayTimeDuration is the only 
     * permissible data type value, that may be added to an xs:Time value).
     */
    public XObject add(XObject xObject) throws TransformerException {
         XObject result = null;
         
         if (!(xObject instanceof XSDayTimeDuration)) {
            throw new TransformerException("XPTY0004 : The value of type xs:dayTimeDuration is the only "
            		                                                                      + "one that may be added to an xs:Time value.");
         }
         
         XSDayTimeDuration argVal = (XSDayTimeDuration)xObject;
         double argValSecs = argVal.value();
         Calendar cal1 = (Calendar)((getCalendar()).clone());
         cal1.setTimeInMillis(cal1.getTimeInMillis() + ((((long)argValSecs * 1000))));
         result = new XSTime(cal1, getTimezone());
         
         return result;
    }
     
    /**
     * Implementation of subtraction operation between this XSTime value, and a
     * supplied value (as per XPath 3.1 spec, xs:time and xs:dayTimeDuration are
     * the only permissible data type values, that may be subtracted from an 
     * xs:time value).
     */
     public XObject subtract(XObject xObject) throws TransformerException {
          XObject result = null;
          
          if (!((xObject instanceof XSTime) || (xObject instanceof XSDayTimeDuration))) {
             throw new TransformerException("XPTY0004 : The values of types xs:time and xs:dayTimeDuration "
             		                                                     + "are only ones that may be subtracted "
             		                                                     + "from an xs:time value.");
          }
          
          if (xObject instanceof XSTime) {
             Calendar cal1 = getCalendar();
             Calendar cal2 = ((XSTime)xObject).getCalendar();
             long diffDurationMilliSecs = cal1.getTimeInMillis() - cal2.getTimeInMillis();
             result = new XSDayTimeDuration(diffDurationMilliSecs / 1000);
          }          
          else if (xObject instanceof XSDayTimeDuration) {
             XSDayTimeDuration argVal = (XSDayTimeDuration)xObject;
             double argValSecs = argVal.value();
             Calendar cal1 = (Calendar)((getCalendar()).clone());
             cal1.setTimeInMillis(cal1.getTimeInMillis() + ((((long)argValSecs * 1000)) * -1));
             result = new XSTime(cal1, getTimezone());
          }
          
          return result;
    }
    
    public int getType() {
        return CLASS_XS_TIME;
    }
    
    public boolean isPopulatedFromFnCurrentTime() {
		return isPopulatedFromFnCurrentTime;
	}

	public void setPopulatedFromFnCurrentTime(boolean isPopulatedFromFnCurrentTime) {
		this.isPopulatedFromFnCurrentTime = isPopulatedFromFnCurrentTime;
	}

	/**
     * Do a data type cast, of an XSAnyType argument passed to this method, to
     * an XSTime object.
     */
    private XSTime castToTime(XSAnyType xsAnyType) throws TransformerException {        
        XSTime xsTime = null;
        
        if (xsAnyType instanceof XSTime) {
           xsTime = (XSTime)xsAnyType;
        }        
        else if (xsAnyType instanceof XSDateTime) {
           XSDateTime xsDateTime = (XSDateTime)xsAnyType;
           xsTime = new XSTime(xsDateTime.getCalendar(), xsDateTime.getTimezone());
        }
        else {
           xsTime = parseTime(xsAnyType.stringValue());
        }
        
        return xsTime;
    }

}
