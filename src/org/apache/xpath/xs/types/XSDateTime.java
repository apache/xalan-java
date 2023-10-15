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
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.xml.transform.TransformerException;

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
     * The value of this class field, stores the fact that whether this
     * XSDateTime object is constructed via XPath function call 
     * fn:current-dateTime().
     */
    private boolean isPopulatedFromFnCurrentDateTime = false;
    
    /**
     * Class constructor.
     * 
     * Creates a new XSDateTime object instance, corresponding to the provided 
     * date, time and timezone.
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
    public ResultSequence constructor(ResultSequence arg) throws TransformerException {
        ResultSequence resultSeq = new ResultSequence();
        
        if (arg.size() == 0) {
           return resultSeq;     
        }
        
        XSAnyType xsAnyType = (XSAnyType)arg.item(0);
        
        XSDateTime xsDateTime = castToDateTime(xsAnyType);
        
        resultSeq.add(xsDateTime);
        
        return resultSeq;
    }
    
    public Calendar getCalendar() {
        return _calendar;
    }
    
    public XSDuration getTimezone() {
        return _tz;
    }

    @Override
    public String typeName() {
        return "dateTime";
    }
    
    /**
     * Method to check, whether the provided character is numeric.
     * 
     * @param x    the character for which, this check is done
     * 
     * @return     true if the character is numeric. false otherwise.
     */
    public static boolean isDigit(char x) {
        if ('0' <= x && x <= '9') {
           return true;
        }
        
        return false;
    }
    
    /**
     * Parse a string representation of a date and time, and retrieve the year,
     * month and day components from this string.
     * 
     * @param  strVal    the string representation of the date (with an optional  
     *                   timezone value)
     * 
     * @return           an integer array of size 3. first element of this array is the year, 
     *                   second element is the month, and third element is the day.
     */
    public static int[] parseDate(String strVal) {
        
        int[] returnVal = new int[3];
        
        int state = 0;

        for (int i = 0; i < returnVal.length; i++) {
            returnVal[i] = 0;
        }

        String token = "";
        for (int i = 0; i < strVal.length(); i++) {
            char x = strVal.charAt(i);

            switch (state) {
            case 0:
                if (isDigit(x)) {
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

                    returnVal[0] = Integer.parseInt(token);
                    token = "";
                    state = 2;
                } 
                else if (isDigit(x)) {
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

                    returnVal[1] = Integer.parseInt(token);
                    token = "";
                    state = 3;
                } else if (isDigit(x)) {
                    token += x;
                }
                else {
                    return null;
                }
                break;
            case 3:
                if (isDigit(x)) {
                    token += x;
                }
                else {
                    return null;
                }
                break;
            default:
                return returnVal;
            }
        }
        
        if (state != 3) {
            return null;
        }

        if (token.length() != 2) {
            return null;
        }

        returnVal[2] = Integer.parseInt(token);

        return returnVal;
    }
    
    /**
     * Parse a string representation of a date and time, and retrieve the hour,
     * minute and second components from this string.
     * 
     * @param   strVal    the string representation of the date (with an optional 
     *                    timezone value)
     * 
     * @return            an integer array of size 3. first element is the hour, second 
     *                    element is the minute, and third element is the seconds.
     */
    public static double[] parseTime(String strVal) {
        
        double[] returnVal = new double[3];
        
        int state = 0;

        String token = "";

        for (int i = 0; i < strVal.length(); i++) {
            char x = strVal.charAt(i);

            switch (state) {
            case 0:
            case 1:
                if (x == ':') {
                    if (token.length() != 2) {
                        return null;
                    }
                    returnVal[state] = Integer.parseInt(token);
                    state++;
                    token = "";
                } else if (isDigit(x)) {
                    token += x;
                }
                else {
                    return null;
                }
                break;
            case 2:
                if (isDigit(x)) {
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
                if (isDigit(x)) {
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

        returnVal[2] = Double.parseDouble(token);

        if (returnVal[0] == 24.0) {
            returnVal[0] = 00.0;
        }

        return returnVal;
    }
    
    /**
     * Parse a string representation of a date and time, and retrieve the
     * timezone component from this string.
     * 
     * @param  strVal   the string representation of the date (with an optional 
     *                  timezone value)
     * 
     * @return          an integer array of size 3. first element represents whether the
     *                  timezone is ahead or behind GMT, second element is the hour
     *                  displacement, and third element is the minute displacement.
     */
    public static int[] parseTimezone(String strVal) {
        
        int[] returnVal = new int[3];

        for (int i = 0; i < returnVal.length; i++) {
            returnVal[i] = 0;
        }
        
        returnVal[0] = 1;

        if (strVal.equals("Z")) {
            return returnVal;
        }

        if (strVal.startsWith("+")) {
            returnVal[0] = 1;
        }
        else if (strVal.startsWith("-")) {
            returnVal[0] = -1;
        }
        else {
            return null;
        }

        strVal = strVal.substring(1, strVal.length());

        if (strVal.length() != (2 + 1 + 2)) {
            return null;
        }

        try {
            returnVal[1] = Integer.parseInt(strVal.substring(0, 2));
            returnVal[2] = Integer.parseInt(strVal.substring(3, 5));

            if (returnVal[1] > 14) {
                return null;
            }
            
            if (returnVal[2] > 59) {
                return null;
            }

            return returnVal;
        } catch (NumberFormatException ex) {
            return null;
        }
    }
    
    /**
     * Parse a string representation of a date and time, and construct a new
     * XSDateTime object using that information.
     * 
     * @param strVal    the string representation of the date (with an optional 
     *                  timezone value)
     * 
     * @return          the XSDateTime representation of the date and time (with an 
     *                  optional timezone value)
     */
    public static XSDateTime parseDateTime(String strVal) throws TransformerException {
        
        XSDateTime xsDateTime = null;

        try {
            int idx = strVal.indexOf('T');
            if (idx == -1) {
               throw new TransformerException("XTTE0570 : The supplied string value '" + strVal + "' "
                                                                                  + "cannot be parsed to a xs:dateTime value.");
            }
    
            String date = strVal.substring(0, idx);
            String time = strVal.substring(idx + 1, strVal.length());
            String timezone = null;
    
            idx = time.indexOf('+');
            if (idx == -1) {
                idx = time.indexOf('-');
            }
            if (idx == -1) {
                idx = time.indexOf('Z');
            }
            if (idx != -1) {
                timezone = time.substring(idx, time.length());
                time = time.substring(0, idx);
            }
    
            int d[] = parseDate(date);
            if (d == null) {
               throw new TransformerException("XTTE0570 : The supplied string value '" + strVal + "' "
                                                                                             + "cannot be parsed to a xs:dateTime value.");
            }
    
            TimeZone defaultTimezone = TimeZone.getDefault();
            GregorianCalendar gregorianCalendarObj = new GregorianCalendar(
                                                                     defaultTimezone);
    
            int year = d[0];
            if (year < 0) {
                year *= -1;
                gregorianCalendarObj.set(Calendar.ERA, GregorianCalendar.BC);
            } else {
                gregorianCalendarObj.set(Calendar.ERA, GregorianCalendar.AD);
            }
    
            gregorianCalendarObj.set(Calendar.DAY_OF_MONTH, 2);
            gregorianCalendarObj.set(Calendar.MONTH, 2);
    
            if (!setItem(gregorianCalendarObj, Calendar.YEAR, year)) {
                throw new TransformerException("XTTE0570 : The supplied string value '" + strVal + "' "
                                                                                              + "cannot be parsed to a xs:dateTime value.");
            }
    
            if (!setItem(gregorianCalendarObj, Calendar.MONTH, d[1] - 1)) {
                throw new TransformerException("XTTE0570 : The supplied string value '" + strVal + "' "
                                                                                              + "cannot be parsed to a xs:dateTime value.");
            }
    
            if (!setItem(gregorianCalendarObj, Calendar.DAY_OF_MONTH, d[2])) {
                throw new TransformerException("XTTE0570 : The supplied string value '" + strVal + "' "
                                                                                              + "cannot be parsed to a xs:dateTime value.");
            }
    
            double t[] = parseTime(time);
            if (t == null) {
                throw new TransformerException("XTTE0570 : The supplied string value '" + strVal + "' "
                                                                                              + "cannot be parsed to a xs:dateTime value.");
            }
    
            if (!setItem(gregorianCalendarObj, Calendar.HOUR_OF_DAY, (int) t[0])) {
                throw new TransformerException("XTTE0570 : The supplied string value '" + strVal + "' "
                                                                                              + "cannot be parsed to a xs:dateTime value.");
            }
    
            if (!setItem(gregorianCalendarObj, Calendar.MINUTE, (int) t[1])) {
                throw new TransformerException("XTTE0570 : The supplied string value '" + strVal + "' "
                                                                                              + "cannot be parsed to a xs:dateTime value.");
            }
    
            if (!setItem(gregorianCalendarObj, Calendar.SECOND, (int) t[2])) {
                throw new TransformerException("XTTE0570 : The supplied string value '" + strVal + "' "
                                                                                              + "cannot be parsed to a xs:dateTime value.");
            }
    
            double ms = t[2] - ((int) t[2]);
            ms *= 1000;
            if (!setItem(gregorianCalendarObj, Calendar.MILLISECOND, (int) ms)) {
                throw new TransformerException("XTTE0570 : The supplied string value '" + strVal + "' "
                                                                                              + "cannot be parsed to a xs:dateTime value.");
            }
    
            int tz[] = null;
            XSDuration timezoneVal = null;
            if (timezone != null) {
                tz = parseTimezone(timezone);
    
                if (tz == null) {
                   throw new TransformerException("XTTE0570 : The supplied string value '" + strVal + "' "
                                                                                                 + "cannot be parsed to a xs:dateTime value.");
                }
    
                timezoneVal = new XSDayTimeDuration(0, tz[1], tz[2], 0.0, tz[0] < 0);
            }
            
            xsDateTime = new XSDateTime(gregorianCalendarObj, timezoneVal);
        }
        catch (TransformerException ex) {
            throw ex;  
        }
        catch (Exception ex) {
            throw new TransformerException("XTTE0570 : The supplied string value '" + strVal + "' "
                                                                               + "cannot be parsed to a xs:dateTime value."); 
        }

        return xsDateTime;
    }

    @Override
    public String stringType() {
        return XS_DATE_TIME;
    }
    
    public static String padInt(int num, int len) {        
        String returnVal = "";
        
        String numStr = "" + num;

        int pad = len - numStr.length();

        if (num < 0) {
            returnVal += "-";
            numStr = numStr.substring(1, numStr.length());
            pad++;
        }

        StringBuffer strBuf = new StringBuffer(returnVal);
        
        for (int i = 0; i < pad; i++) {
            strBuf.append("0");
        }
        
        strBuf.append(numStr);
        
        returnVal = strBuf.toString();
        
        return returnVal;
    }
    
    public int year() {
        int year = _calendar.get(Calendar.YEAR);
        if (_calendar.get(Calendar.ERA) == GregorianCalendar.BC) {
           year = year * -1;
        }

        return year;
    }
    
    public int month() {
        return _calendar.get(Calendar.MONTH) + 1;
    }
    
    public int day() {
        return _calendar.get(Calendar.DAY_OF_MONTH);
    }
    
    public int hour() {
        return _calendar.get(Calendar.HOUR_OF_DAY);
    }
    
    public int minute() {
        return _calendar.get(Calendar.MINUTE);
    }
    
    public double second() {
        double secondVal = _calendar.get(Calendar.SECOND);
        double millisecVal = _calendar.get(Calendar.MILLISECOND);

        millisecVal /= 1000;
        secondVal += millisecVal;
        
        return secondVal;
    }
    
    /**
     * Check whether this XSDateTime object has an, timezone associated with it.
     * 
     * @return true    if there is a timezone associated with this XSDateTime object.
     *                 false otherwise.
     */
    public boolean isDateTimeTimezoned() {
        return _timezoned;
    }

    @Override
    public String stringValue() {
        String returnVal = "";

        Calendar calendarVal = getCalendar();

        if (calendarVal.get(Calendar.ERA) == GregorianCalendar.BC) {
            returnVal += "-";
        }

        returnVal += padInt(calendarVal.get(Calendar.YEAR), 4);

        returnVal += "-";
        returnVal += padInt(month(), 2);

        returnVal += "-";
        returnVal += padInt(calendarVal.get(Calendar.DAY_OF_MONTH), 2);

        returnVal += "T";

        returnVal += padInt(calendarVal.get(Calendar.HOUR_OF_DAY), 2);

        returnVal += ":";
        returnVal += padInt(calendarVal.get(Calendar.MINUTE), 2);

        returnVal += ":";
        int intSec = (int)second();
        double doubleSec = second();

        if ((doubleSec - intSec) == 0.0) {
           returnVal += padInt(intSec, 2);
        }
        else {
            if (doubleSec < 10.0) {
               returnVal += "0" + doubleSec;
            }
            else {
               returnVal += doubleSec;
            }
        }

        if (isDateTimeTimezoned()) {
            int hrs = _tz.hours();
            int min = _tz.minutes();
            double secs = _tz.seconds();
            if ((hrs == 0) && (min == 0) && (secs == 0)) {
                returnVal += "Z";
            } else {
                String timezoneStr = "";
                if (_tz.negative()) {
                    timezoneStr += "-";
                } else {
                    timezoneStr += "+";
                }
                timezoneStr += padInt(hrs, 2);
                timezoneStr += ":";
                timezoneStr += padInt(min, 2);

                returnVal += timezoneStr;
            }
        }

        return returnVal;
    }
    
    /**
     * Determine whether, two XSDateTime objects are equal.
     */
    public boolean equals(XSDateTime xsDateTime) {
        boolean isDateTimeEqual = false;
        
        Calendar cal1 = getCalendar();
        Calendar cal2 = xsDateTime.getCalendar();
        
        int year1 = cal1.get(Calendar.YEAR);
        int month1 = cal1.get(Calendar.MONTH);
        int date1 = cal1.get(Calendar.DATE);
        int hour1 = hour();
        int mins1 = minute();
        double secs1 = second();
        
        int year2 = cal2.get(Calendar.YEAR);
        int month2 = cal2.get(Calendar.MONTH);
        int date2 = cal2.get(Calendar.DATE);
        int hour2 = xsDateTime.hour();
        int mins2 = xsDateTime.minute();
        double secs2 = xsDateTime.second();
        
        XSDuration tz1 = getTimezone();
        XSDuration tz2 = xsDateTime.getTimezone();
        
        isDateTimeEqual = ((year1 == year2) && (month1 == month2) && (date1 == date2) && 
                           (hour1 == hour2) && (mins1 == mins2) && (secs1 == secs2)) && 
                                                    isTimezoneEqual(tz1, tz2, isPopulatedFromFnCurrentDateTime, 
                                                                    xsDateTime.isPopulatedFromFnCurrentDateTime());
        
        return isDateTimeEqual;
    }
    
    @Override
    public boolean equals(Object obj) {
       boolean isDateTimeEqual = false;
        
       if (obj instanceof XSDateTime) {
           isDateTimeEqual = this.equals((XSDateTime)obj);  
       }
       
       return isDateTimeEqual;
    }
    
    @Override
    public int hashCode() {       
       String strVal = stringValue();       
       
       return strVal.hashCode();
    }
    
    /**
     * Determine whether, this XSDateTime object is less that, the 
     * XSDateTime object provided as an argument to this method. 
     */
    public boolean lt(XSDateTime xsDateTime) {
        boolean isDateTimeBefore = false;
        
        Calendar cal1 = getCalendar();
        Calendar cal2 = xsDateTime.getCalendar();
        
        Date date1 = new Date(cal1.get(Calendar.YEAR), cal1.get(Calendar.MONTH), 
                                                                     cal1.get(Calendar.DATE));
        Date date2 = new Date(cal2.get(Calendar.YEAR), cal2.get(Calendar.MONTH), 
                                                                     cal2.get(Calendar.DATE));
        
        if (date1.before(date2)) {
           isDateTimeBefore = true;  
        }
        else if (date1.equals(date2)) {
            int hour1 = hour();
            int mins1 = minute();
            double secs1 = second();
            
            int hour2 = xsDateTime.hour();
            int mins2 = xsDateTime.minute();
            double secs2 = xsDateTime.second();
            
            if (hour1 < hour2) {
               isDateTimeBefore = true; 
            }
            else if (hour1 == hour2) {
               if (mins1 < mins2) {
                  isDateTimeBefore = true;  
               }
               else if (mins1 == mins2) {
                  if (secs1 < secs2) {
                     isDateTimeBefore = true;  
                  }
               }
            }
        }
        
        return isDateTimeBefore;
    }
    
    /**
     * Determine whether, this XSDateTime object is greater that, the 
     * XSDateTime object provided as an argument to this method. 
     */
    public boolean gt(XSDateTime xsDateTime) {
        boolean isDateTimeAfter = false;
        
        Calendar cal1 = getCalendar();
        Calendar cal2 = xsDateTime.getCalendar();
        
        Date date1 = new Date(cal1.get(Calendar.YEAR), cal1.get(Calendar.MONTH), 
                                                                     cal1.get(Calendar.DATE));
        Date date2 = new Date(cal2.get(Calendar.YEAR), cal2.get(Calendar.MONTH), 
                                                                     cal2.get(Calendar.DATE));
        
        if (date1.after(date2)) {
           isDateTimeAfter = true;  
        }
        else if (date1.equals(date2)) {
            int hour1 = hour();
            int mins1 = minute();
            double secs1 = second();
            
            int hour2 = xsDateTime.hour();
            int mins2 = xsDateTime.minute();
            double secs2 = xsDateTime.second();
            
            if (hour1 > hour2) {
               isDateTimeAfter = true; 
            }
            else if (hour1 == hour2) {
               if (mins1 > mins2) {
                  isDateTimeAfter = true;  
               }
               else if (mins1 == mins2) {
                  if (secs1 > secs2) {
                     isDateTimeAfter = true;  
                  }
               }
            }
        }
        
        return isDateTimeAfter;
    }
    
    public boolean isPopulatedFromFnCurrentDateTime() {
        return isPopulatedFromFnCurrentDateTime;
    }
    
    public void setPopulatedFromFnCurrentDateTime(boolean isPopulatedFromFnCurrentDateTime) {
        this.isPopulatedFromFnCurrentDateTime = isPopulatedFromFnCurrentDateTime;
    }
    
    public int getType() {
        return CLASS_XS_DATETIME;
    }
    
    /**
     * Do a data type cast, of an XSAnyType argument passed to this method, to
     * an XSDateTime object.
     */
    private XSDateTime castToDateTime(XSAnyType xsAnyType) throws TransformerException {
        if (xsAnyType instanceof XSDate) {
            XSDate xsDate = (XSDate) xsAnyType;
            return new XSDateTime(xsDate.getCalendar(), xsDate.getTimezone());
        }

        if (xsAnyType instanceof XSDateTime) {
            XSDateTime xsDateTime = (XSDateTime) xsAnyType;
            return new XSDateTime(xsDateTime.getCalendar(), xsDateTime.getTimezone());
        }

        return parseDateTime(xsAnyType.stringValue()); 
    }
    
    /**
     * Set a particular field within an java.util.Calendar object.
     * 
     * @param cal           the Calendar object to set the field in
     * @param fieldId       the field to set
     * @param fieldval      the value to set the field to
     * 
     * @return              true if successfully set. false otherwise
     */
    private static boolean setItem(Calendar cal, int fieldId, int fieldval) {

        if (fieldval < cal.getActualMinimum(fieldId)) {
            return false;
        }

        if (fieldval > cal.getActualMaximum(fieldId)) {
            return false;
        }

        cal.set(fieldId, fieldval);
        
        return true;
    }

}
