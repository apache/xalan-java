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
 * An XML Schema data type representation, of the xs:date datatype.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XSDate extends XSCalendarType {

    private static final long serialVersionUID = -9204442487368342326L;
    
    private static final String XS_DATE = "xs:date";
    
    private Calendar _calendar;
    private boolean _timezoned;
    private XSDuration _tz;
    
    /**
     * Class constructor.
     * 
     * Creates a new 'XSDate' instance, corresponding to the supplied 
     * date and time.
     * 
     * @param cal     the java.util.Calendar representation of the date to be stored
     * 
     * @param tz      the time zone of the date to be stored
     */
    public XSDate(Calendar cal, XSDuration tz) {
        _calendar = cal;
        _tz = tz;
        
        if (tz == null) {
           _timezoned = false;
        }
        else {
           _timezoned = true;
        }
    }

    /*
     * Class constructor. 
     */
    public XSDate() {
        this(new GregorianCalendar(TimeZone.getTimeZone("GMT")), null);
    }

    @Override
    public ResultSequence constructor(ResultSequence arg) {
        // TO DO
        return null;
    }

    @Override
    public String typeName() {
        return "date";
    }

    @Override
    public String stringType() {
        return XS_DATE;
    }
    
    /**
     * Get the Calendar representation of the date stored.
     * 
     * @return    Calendar representation of the date stored
     */
    public Calendar calendar() {
        return _calendar;
    }
    
    /**
     * Get the month from the date stored.
     * 
     * @return    the month value of the date stored
     */
    public int month() {
        return _calendar.get(Calendar.MONTH) + 1;
    }
    
    /**
     * Check whether this date has an optional, timezone associated with it.
     * 
     * @return true    if there is a timezone associated with this date. false
     *                 otherwise.
     */
    public boolean timezoned() {
        return _timezoned;
    }

    @Override
    public String stringValue() {
        String ret = "";

        Calendar adjustFortimezone = calendar();

        if (adjustFortimezone.get(Calendar.ERA) == GregorianCalendar.BC) {
            ret += "-";
        }

        ret += XSDateTime.pad_int(adjustFortimezone.get(Calendar.YEAR), 4);

        ret += "-";
        ret += XSDateTime.pad_int(month(), 2);

        ret += "-";
        ret += XSDateTime.pad_int(adjustFortimezone.get(Calendar.DAY_OF_MONTH), 2);

        if (timezoned()) {
            int hrs = _tz.hours();
            int min = _tz.minutes();
            double secs = _tz.seconds();
            if (hrs == 0 && min == 0 && secs == 0) {
                ret += "Z";
            } else {
                String tZoneStr = "";
                if (_tz.negative()) {
                    tZoneStr += "-";
                } else {
                    tZoneStr += "+";
                }
                tZoneStr += XSDateTime.pad_int(hrs, 2);
                tZoneStr += ":";
                tZoneStr += XSDateTime.pad_int(min, 2);

                ret += tZoneStr;
            }
        }

        return ret;
    }

}
