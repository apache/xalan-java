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

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.apache.xpath.objects.ResultSequence;

/**
 * An XML Schema data type representation, of the xs:dateTime 
 * datatype.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XSDateTime extends XSCalendarType {
    
    private static final long serialVersionUID = -2163029573822424868L;
    
    private static final String XS_DATE_TIME = "xs:dateTime";
    
    private Calendar _calendar;
    private boolean _timezoned;
    private XSDuration _tz;
    
    /**
     * Class constructor.
     * 
     * Creates a new 'XSDateTime' instance, corresponding to the supplied 
     * date and time.
     * 
     * @param cal     the java.util.Calendar representation of the date and 
     *                time to be stored
     * 
     * @param tz      the timezone of the date to be stored
     */
    public XSDateTime(Calendar cal, XSDuration tz) {
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
    public XSDateTime(Calendar cal) {
        _calendar = cal;

        if (_tz == null) {
            _timezoned = false;
        }
        else {
            _timezoned = true;
        }
    }
    
    /*
     * Class constructor. 
     */
    public XSDateTime() {
        this(new GregorianCalendar(), null);
    }

    @Override
    public ResultSequence constructor(ResultSequence arg) {
        // TO DO
        return null;
    }

    @Override
    public String typeName() {
        return "dateTime";
    }
    
    /**
     * Check to see if a character is numeric.
     * 
     * @param x    character to be tested
     * 
     * @return     true if the character is numeric. false otherwise.
     */
    public static boolean is_digit(char x) {
        if ('0' <= x && x <= '9') {
           return true;
        }
        
        return false;
    }
    
    /**
     * Parse a string representation of a date and time, and retrieve the year,
     * month and day components from this string.
     * 
     * @param  str    the String representation of the date (with optional timezone)
     * 
     * @return        an integer array of size 3. first element is the year, second 
     *                element is the month, and third element is the day.
     */
    public static int[] parse_date(String str) {
        int state = 0;

        int[] ret = new int[3];

        for (int i = 0; i < ret.length; i++) {
            ret[i] = 0;
        }

        String token = "";
        for (int i = 0; i < str.length(); i++) {
            char x = str.charAt(i);

            switch (state) {
            case 0:
                if (is_digit(x)) {
                    token += x;
                } else if (x == '-') {
                    token += x;
                } else {
                    return null;
                }
                state = 1;
                break;
            case 1:
                if (x == '-') {
                    String uy = token;
                    if (uy.startsWith("-")) {
                        uy = uy.substring(1, uy.length());
                    }
                    int uyl = uy.length();

                    if (uyl < 4) {
                        return null;
                    }

                    if (uyl == 4) {
                        if (uy.compareTo("0000") == 0) {
                           return null;
                        }                        
                    } 
                    else if (uy.charAt(0) == '0') {
                        return null;
                    }

                    ret[0] = Integer.parseInt(token);
                    token = "";
                    state = 2;
                } 
                else if (is_digit(x)) {
                    token += x;
                }
                else {
                    return null;
                }
                break;
            case 2:
                if (x == '-') {
                    if (token.length() != 2) {
                        return null;
                    }

                    ret[1] = Integer.parseInt(token);
                    token = "";
                    state = 3;
                } else if (is_digit(x)) {
                    token += x;
                }
                else {
                    return null;
                }
                break;
            case 3:
                if (is_digit(x)) {
                    token += x;
                }
                else {
                    return null;
                }
                break;
            default:
                return ret;
            }
        }
        
        if (state != 3) {
            return null;
        }

        if (token.length() != 2) {
            return null;
        }

        ret[2] = Integer.parseInt(token);

        return ret;
    }
    
    /**
     * Parse a string representation of a date and time, and retrieve the hour,
     * minute and seconds components from this string.
     * 
     * @param   str    the String representation of the date (with optional timezone)
     * 
     * @return         an integer array of size 3. first element is the hour, second 
     *                 element is the minute, and third element is the seconds.
     */
    public static double[] parse_time(String str) {
        int state = 0; 

        double[] ret = new double[3];

        String token = "";

        for (int i = 0; i < str.length(); i++) {
            char x = str.charAt(i);

            switch (state) {
            case 0:
            case 1:
                if (x == ':') {
                    if (token.length() != 2) {
                        return null;
                    }
                    ret[state] = Integer.parseInt(token);
                    state++;
                    token = "";
                } else if (is_digit(x)) {
                    token += x;
                }
                else {
                    return null;
                }
                break;
            case 2:
                if (is_digit(x)) {
                    token += x;
                    if (token.length() > 2) {
                        return null;
                    }
                } else if (x == '.') {
                    token += x;
                    state = 3;
                } else {
                    return null;
                }
                break;
            case 3:
                if (is_digit(x)) {
                    token += x;
                }
                else {
                    return null;
                }
                break;
            default:
                return null;
            }
        }
        
        if (!(state == 3 || state == 2)) {
            return null;
        }
        
        if (token.length() == 3) {
            return null;
        }

        ret[2] = Double.parseDouble(token);

        if (ret[0] == 24.0) {
            ret[0] = 00.0;
        }

        return ret;
    }
    
    /**
     * Parse a String representation of a date and time, and retrieve the
     * timezone component from this string.
     * 
     * @param  str   the String representation of the date (with optional timezone)
     * 
     * @return       an integer array of size 3. first element represents whether the
     *               timezone is ahead or behind GMT, second element is the hour
     *               displacement, and third element is the minute displacement.
     */
    public static int[] parse_timezone(String str) {
        int[] ret = new int[3];

        for (int i = 0; i < ret.length; i++) {
            ret[i] = 0;
        }
        
        ret[0] = 1;

        if (str.equals("Z")) {
            return ret;
        }

        if (str.startsWith("+")) {
            ret[0] = 1;
        }
        else if (str.startsWith("-")) {
            ret[0] = -1;
        }
        else {
            return null;
        }

        str = str.substring(1, str.length());

        if (str.length() != (2 + 1 + 2)) {
            return null;
        }

        try {
            ret[1] = Integer.parseInt(str.substring(0, 2));
            ret[2] = Integer.parseInt(str.substring(3, 5));

            if (ret[1] > 14) {
                return null;
            }
            
            if (ret[2] > 59) {
                return null;
            }

            return ret;
        } catch (NumberFormatException ex) {
            return null;
        }
    }
    
    /**
     * Parse a String representation of a date and time, and construct a new
     * XSDateTime object using that information.
     * 
     * @param str    the String representation of the date (with optional timezone)
     * 
     * @return       the XSDateTime representation of the date and time (with optional
     *               timezone)
     */
    public static XSDateTime parseDateTime(String str) {

        int index = str.indexOf('T');
        if (index == -1) {
            return null;
        }

        String date = str.substring(0, index);
        String time = str.substring(index + 1, str.length());
        String timezone = null;

        index = time.indexOf('+');
        if (index == -1) {
            index = time.indexOf('-');
        }
        if (index == -1) {
            index = time.indexOf('Z');
        }
        if (index != -1) {
            timezone = time.substring(index, time.length());
            time = time.substring(0, index);
        }

        int d[] = parse_date(date);
        if (d == null) {
            return null;
        }

        TimeZone UTC = TimeZone.getTimeZone("UTC");
        GregorianCalendar cal = new GregorianCalendar(UTC);

        int year = d[0];
        if (year < 0) {
            year *= -1;
            cal.set(Calendar.ERA, GregorianCalendar.BC);
        } else {
            cal.set(Calendar.ERA, GregorianCalendar.AD);
        }

        cal.set(Calendar.DAY_OF_MONTH, 2);
        cal.set(Calendar.MONTH, 2);

        if (!set_item(cal, Calendar.YEAR, year)) {
            return null;
        }

        if (!set_item(cal, Calendar.MONTH, d[1] - 1)) {
            return null;
        }

        if (!set_item(cal, Calendar.DAY_OF_MONTH, d[2])) {
            return null;
        }

        double t[] = parse_time(time);
        if (t == null) {
            return null;
        }

        if (!set_item(cal, Calendar.HOUR_OF_DAY, (int) t[0])) {
            return null;
        }

        if (!set_item(cal, Calendar.MINUTE, (int) t[1])) {
            return null;
        }

        if (!set_item(cal, Calendar.SECOND, (int) t[2])) {
            return null;
        }

        double ms = t[2] - ((int) t[2]);
        ms *= 1000;
        if (!set_item(cal, Calendar.MILLISECOND, (int) ms)) {
            return null;
        }

        int tz[] = null;
        XSDuration tzd = null;
        if (timezone != null) {
            tz = parse_timezone(timezone);

            if (tz == null) {
                return null;
            }

            tzd = new XSDayTimeDuration(0, tz[1], tz[2], 0.0, tz[0] < 0);

        }

        return new XSDateTime(cal, tzd);
    }

    @Override
    public String stringType() {
        return XS_DATE_TIME;
    }
    
    public Calendar calendar() {
        return _calendar;
    }
    
    public static String pad_int(int num, int len) {
        String ret = "";
        String snum = "" + num;

        int pad = len - snum.length();

        // sort out the negative
        if (num < 0) {
            ret += "-";
            snum = snum.substring(1, snum.length());
            pad++;
        }

        StringBuffer buf = new StringBuffer(ret);
        for (int i = 0; i < pad; i++) {
            buf.append("0");
        }
        buf.append(snum);
        ret = buf.toString();
        return ret;
    }
    
    public double second() {
        double s = _calendar.get(Calendar.SECOND);
        double ms = _calendar.get(Calendar.MILLISECOND);

        ms /= 1000;
        s += ms;
        
        return s;
    }
    
    public int month() {
        return _calendar.get(Calendar.MONTH) + 1;
    }
    
    public boolean timezoned() {
        return _timezoned;
    }

    @Override
    public String stringValue() {
        String returnVal = "";

        Calendar adjustFortimezone = calendar();

        if (adjustFortimezone.get(Calendar.ERA) == GregorianCalendar.BC) {
            returnVal += "-";
        }

        returnVal += pad_int(adjustFortimezone.get(Calendar.YEAR), 4);

        returnVal += "-";
        returnVal += pad_int(month(), 2);

        returnVal += "-";
        returnVal += pad_int(adjustFortimezone.get(Calendar.DAY_OF_MONTH), 2);

        // time
        returnVal += "T";

        returnVal += pad_int(adjustFortimezone.get(Calendar.HOUR_OF_DAY), 2);

        returnVal += ":";
        returnVal += pad_int(adjustFortimezone.get(Calendar.MINUTE), 2);

        returnVal += ":";
        int isecond = (int) second();
        double sec = second();

        if ((sec - (isecond)) == 0.0)
            returnVal += pad_int(isecond, 2);
        else {
            if (sec < 10.0)
                returnVal += "0" + sec;
            else
                returnVal += sec;
        }

        if (timezoned()) {
            int hrs = _tz.hours();
            int min = _tz.minutes();
            double secs = _tz.seconds();
            if (hrs == 0 && min == 0 && secs == 0) {
                returnVal += "Z";
            } else {
                String tZoneStr = "";
                if (_tz.negative()) {
                    tZoneStr += "-";
                } else {
                    tZoneStr += "+";
                }
                tZoneStr += pad_int(hrs, 2);
                tZoneStr += ":";
                tZoneStr += pad_int(min, 2);

                returnVal += tZoneStr;
            }
        }

        return returnVal;
    }
    
    /**
     * Set a particular field within the Calendar.
     * 
     * @param cal     the Calendar object to set the field in
     * @param item    the field to set
     * @param val     the value to set the field to
     * 
     * @return        true if successfully set. false otherwise
     */
    private static boolean set_item(Calendar cal, int item, int val) {
        int min = cal.getActualMinimum(item);

        if (val < min)
            return false;

        int max = cal.getActualMaximum(item);

        if (val > max)
            return false;

        cal.set(item, val);
        return true;
    }

}
