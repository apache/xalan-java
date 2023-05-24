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
     * Initializes this object, to the supplied time and timezone.
     * 
     * @param cal   Calendar representation of the time to be stored
     * @param tz    the timezone (possibly null) associated with this time
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
     * Initialises to the current time
     */
    public XSTime() {
        this (new GregorianCalendar(TimeZone.getTimeZone("GMT")), null);
    }

    @Override
    public ResultSequence constructor(ResultSequence arg) {
        // TO DO
        return null;
    }
    
    /**
     * Get the datatype's name.
     * 
     * @return   "time" which is the datatype's name
     */
    @Override
    public String typeName() {
        return "time";
    }

    /**
     * Get the datatype's full name.
     * 
     * @return   "xs:time" which is the datatype's full name
     */
    @Override
    public String stringType() {
        return XS_TIME;
    }
    
    /**
     * Get a java.util.Calendar representation of time stored, 
     * within this object.
     * 
     * @return    Calendar representation of the time stored
     */
    public Calendar calendar() {
        return _calendar;
    }
    
    /**
     * Get the seconds stored as an integer, within this object.
     * 
     * @return    the second stored
     */
    public double second() {
        double s = _calendar.get(Calendar.SECOND);
        double ms = _calendar.get(Calendar.MILLISECOND);

        ms /= 1000;
        s += ms;
        
        return s;
    }
    
    /**
     * Check whether the time component stored within this object, 
     * has a timezone associated with it.
     * 
     * @return    true if the time has a timezone associated. false otherwise.
     */
    public boolean timezoned() {
        return _timezoned;
    }

    /**
     * Get a String representation of the time stored.
     * 
     * @return   String representation of the time stored
     */
    @Override
    public String stringValue() {
        String returnVal = "";
        
        Calendar adjustFortimezone = calendar();
        returnVal += XSDateTime.pad_int(adjustFortimezone.get(Calendar.HOUR_OF_DAY), 2);
        
        returnVal += ":";
        returnVal += XSDateTime.pad_int(adjustFortimezone.get(Calendar.MINUTE), 2);
        

        returnVal += ":";
        int isecond = (int) second();
        double sec = second();

        if ((sec - (isecond)) == 0.0) {
            returnVal += XSDateTime.pad_int(isecond, 2);
        }
        else {
            if (sec < 10.0) {
               returnVal += "0" + sec;
            }
            else {
               returnVal += sec;
            }
        }

        if (timezoned()) {
            int hrs = _tz.hours();
            int min = _tz.minutes();
            double secs = _tz.seconds();
            if (hrs == 0 && min == 0 && secs == 0) {
               returnVal += "Z";
            }
            else {
               String tZoneStr = "";
               if (_tz.negative()) {
                  tZoneStr += "-";  
               }
               else {
                  tZoneStr += "+"; 
               }
               tZoneStr += XSDateTime.pad_int(hrs, 2);  
               tZoneStr += ":";
               tZoneStr += XSDateTime.pad_int(min, 2);
              
               returnVal += tZoneStr;
            }
         }

         return returnVal;
    }

}
