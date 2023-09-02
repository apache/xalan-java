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

import org.apache.xpath.objects.ResultSequence;

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
    public ResultSequence constructor(ResultSequence arg) {
        // TO DO
        return null;
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
    public Calendar calendar() {
        return _calendar;
    }
    
    /**
     * Get the seconds value as an integer stored within this 
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
    public boolean isXsTimeObjectTimezoned() {
        return _timezoned;
    }

    @Override
    public String stringValue() {
        String returnVal = "";
        
        Calendar calendarVal = calendar();
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

        if (isXsTimeObjectTimezoned()) {
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
    
    public int getType() {
        return CLASS_XS_TIME;
    }

}
