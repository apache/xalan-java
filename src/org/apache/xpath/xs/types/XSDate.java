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
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.transform.TransformerException;

import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;

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
    
    // The value of this class field, stores the fact that whether this XSDate
    // object is constructed via XPath function call fn:current-date().
    private boolean isPopulatedFromFnCurrentDate = false;
    
    /**
     * Class constructor.
     * 
     * Creates a new XSDate object instance, corresponding to the provided 
     * date and timezone.
     * 
     * @param cal     the java.util.Calendar representation of the date to be stored
     * 
     * @param tz      the timezone of the date to be stored
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
    public XSDate() {}

    @Override
    public ResultSequence constructor(ResultSequence arg) throws TransformerException {
        ResultSequence resultSeq = new ResultSequence();
        
        if (arg.size() == 0) {
           return resultSeq;     
        }
        
        XSAnyType xsAnyType = (XSAnyType)arg.item(0);
        
        XSDate xsDate = castToDate(xsAnyType);
        
        resultSeq.add(xsDate);

        return resultSeq;        
    }
    
    /**
     * Parse a string representation of a date and construct an new XSDate object.
     * 
     * XML Schema 1.1 datatypes spec, provides following to be the valid string
     * representation (which is an ISO 8601 date format) of xs:date typed value,
     * 
     * dateLexicalRep ::= yearFrag '-' monthFrag '-' dayFrag timezoneFrag? 
     * 
     * @param strVal     the string representation of the date
     * @return           the XSDate representation of the provided string
     */
    public static XSDate parseDate(String strVal) throws TransformerException {
        
        XSDate result = null;
        
        try {
            String dateStr = "";
            String timeStr = "T00:00:00.0";
    
            int idx = strVal.indexOf('+', 1);
            if (idx == -1) {
                idx = strVal.indexOf('-', 1);
                if (idx == -1) {
                    throw new TransformerException("XTTE0570 : The supplied string value '" + 
                                                                         strVal + "' cannot be parsed to a xs:date value."); 
                }
                idx = strVal.indexOf('-', idx + 1);
                if (idx == -1) {
                    throw new TransformerException("XTTE0570 : The supplied string value '" + 
                                                                         strVal + "' cannot be parsed to a xs:date value.");
                }
                idx = strVal.indexOf('-', idx + 1);
            }
            if (idx == -1) {
                idx = strVal.indexOf('Z', 1);
            }
            if (idx != -1) {
                dateStr = strVal.substring(0, idx);
                dateStr += timeStr;
                dateStr += strVal.substring(idx, strVal.length());
            } else {
                dateStr = strVal + timeStr;
            }
    
            XSDateTime dateTime = XSDateTime.parseDateTime(dateStr);
            
            if (dateTime != null) {
                result = new XSDate(dateTime.getCalendar(), dateTime.getTimezone());;
            }
            else {
                throw new TransformerException("XTTE0570 : The supplied string value '" + 
                                                                                  strVal + "' cannot be parsed to a xs:date value."); 
            }
        }
        catch (TransformerException ex) {
           throw ex;  
        }
        catch (Exception ex) {
            throw new TransformerException("XTTE0570 : The supplied string value '" + 
                                                                                  strVal + "' cannot be parsed to a xs:date value."); 
        }
        
        return result;
        
    }
    
    public XSDuration getTimezone() {
        return _tz;
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
     * @return    the java.util.Calendar representation of the date stored
     */
    public Calendar getCalendar() {
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
     * Check whether this XSDate object has an, timezone associated with it.
     * 
     * @return true    if there is a timezone associated with this XSDate object.
     *                 false otherwise.
     */
    public boolean isXsDateObjectTimezoned() {
        return _timezoned;
    }

    @Override
    public String stringValue() {
        String xsDateStrValue = "";

        Calendar calendarObj = getCalendar();

        if (calendarObj.get(Calendar.ERA) == GregorianCalendar.BC) {
            xsDateStrValue += "-";
        }

        xsDateStrValue += XSDateTime.padInt(calendarObj.get(Calendar.YEAR), 4);

        xsDateStrValue += "-";
        xsDateStrValue += XSDateTime.padInt(month(), 2);

        xsDateStrValue += "-";
        xsDateStrValue += XSDateTime.padInt(calendarObj.get(Calendar.
                                                                  DAY_OF_MONTH), 2);

        if (isXsDateObjectTimezoned()) {
            int hrs = _tz.hours();
            int min = _tz.minutes();
            double secs = _tz.seconds();
            if (hrs == 0 && min == 0 && secs == 0) {
                xsDateStrValue += "Z";
            } else {
                String timezoneStr = "";
                if (_tz.negative()) {
                    timezoneStr += "-";
                } else {
                    timezoneStr += "+";
                }
                timezoneStr += XSDateTime.padInt(hrs, 2);
                timezoneStr += ":";
                timezoneStr += XSDateTime.padInt(min, 2);

                xsDateStrValue += timezoneStr;
            }
        }

        return xsDateStrValue;
    }
    
    /*
     * Determine whether, two XSDate objects are equal.
     */
    public boolean equals(XSDate xsDate) {
        boolean isDateEqual = false;
        
        Calendar cal1 = getCalendar();
        Calendar cal2 = xsDate.getCalendar();                
        
        int year1 = cal1.get(Calendar.YEAR);
        int month1 = cal1.get(Calendar.MONTH);
        int date1 = cal1.get(Calendar.DATE);
        
        int year2 = cal2.get(Calendar.YEAR);
        int month2 = cal2.get(Calendar.MONTH);
        int date2 = cal2.get(Calendar.DATE);
        
        XSDuration tz1 = getTimezone();
        XSDuration tz2 = xsDate.getTimezone();
        
        isDateEqual = (((year1 + month1 + date1) == (year2 + month2 + date2)) && 
                                     isTimezoneEqual(tz1, tz2, isPopulatedFromFnCurrentDate, 
                                                                   xsDate.isPopulatedFromFnCurrentDate())); 
        
        return isDateEqual; 
    }
    
    /*
     * Determine whether, this XSDate object is less that, the 
     * XSDate object provided as an argument to this method. 
     */
    public boolean lt(XSDate xsDate) {
        boolean isDateBefore = false;
        
        Calendar cal1 = getCalendar();
        Calendar cal2 = xsDate.getCalendar();
        
        Date date1 = new Date(cal1.get(Calendar.YEAR), cal1.get(Calendar.MONTH), 
                                                                     cal1.get(Calendar.DATE));
        Date date2 = new Date(cal2.get(Calendar.YEAR), cal2.get(Calendar.MONTH), 
                                                                     cal2.get(Calendar.DATE));
        
        isDateBefore = date1.before(date2); 
        
        return isDateBefore;
    }
    
    /*
     * Determine whether, this XSDate object is greater than, the 
     * XSDate object provided as an argument to this method. 
     */
    public boolean gt(XSDate xsDate) {
        boolean isDateAfter = false;
        
        Calendar cal1 = getCalendar();
        Calendar cal2 = xsDate.getCalendar();                
        
        Date date1 = new Date(cal1.get(Calendar.YEAR), cal1.get(Calendar.MONTH), 
                                                                      cal1.get(Calendar.DATE));
        Date date2 = new Date(cal2.get(Calendar.YEAR), cal2.get(Calendar.MONTH), 
                                                                      cal2.get(Calendar.DATE));
        
        isDateAfter = date1.after(date2); 
        
        return isDateAfter; 
    }
    
    /**
    * Implementation of subtraction operation between this XSDate value, and a supplied value
    * (as per XPath 3.1 spec, xs:date, xs:yearMonthDuration and xs:dayTimeDuration are the only
    * permissible data type values, that may be subtracted from an xs:date value).
    */
    public XObject subtract(XObject xObject) throws TransformerException {
        XObject result = null;
        
        if (!((xObject instanceof XSDate) || (xObject instanceof XSYearMonthDuration)
                                          || (xObject instanceof XSDayTimeDuration))) {
           throw new TransformerException("XPTY0004 : The values of types xs:date, xs:yearMonthDuration or "
                                                                                 + "xs:dayTimeDuration are only ones that may be subtracted from an xs:date value.");
        }
        
        if (xObject instanceof XSDate) {
           Calendar cal1 = getCalendar();
           Calendar cal2 = ((XSDate)xObject).getCalendar();
           long diffDurationMilliSecs = cal1.getTimeInMillis() - cal2.getTimeInMillis();
           result = new XSDuration(diffDurationMilliSecs / 1000);
        }
        else if (xObject instanceof XSYearMonthDuration) {
           XSYearMonthDuration argVal = (XSYearMonthDuration)xObject;
           Calendar cal1 = (Calendar)((getCalendar()).clone());
           cal1.add(Calendar.MONTH, argVal.monthValue() * -1);
           result = new XSDate(cal1, getTimezone());
        }
        else if (xObject instanceof XSDayTimeDuration) {
           XSDayTimeDuration argVal = (XSDayTimeDuration)xObject;
           double argValSecs = argVal.value();
           Calendar cal1 = (Calendar)((getCalendar()).clone());
           cal1.setTimeInMillis(cal1.getTimeInMillis() + ((((long)argValSecs * 1000)) * -1));
           result = new XSDate(cal1, getTimezone());
        }
        
        return result;
    }
    
    /**
    * Implementation of addition operation between this XSDate value, and a supplied value
    * (as per XPath 3.1 spec, xs:yearMonthDuration and xs:dayTimeDuration are the only permissible 
    * data type values, that may be added to an xs:date value).
    */
    public XObject add(XObject xObject) throws TransformerException {
        XObject result = null;
        
        if (!((xObject instanceof XSYearMonthDuration) || (xObject instanceof XSDayTimeDuration))) {
           throw new TransformerException("XPTY0004 : The values of types xs:yearMonthDuration or "
                                                                               + "xs:dayTimeDuration are only ones that may be added to an xs:date value.");
        }
        
        if (xObject instanceof XSYearMonthDuration) {
           XSYearMonthDuration argVal = (XSYearMonthDuration)xObject;
           Calendar cal1 = (Calendar)((getCalendar()).clone());
           cal1.add(Calendar.MONTH, argVal.monthValue());
           result = new XSDate(cal1, getTimezone());
        }
        else if (xObject instanceof XSDayTimeDuration) {
           XSDayTimeDuration argVal = (XSDayTimeDuration)xObject;
           double argValSecs = argVal.value();
           Calendar cal1 = (Calendar)((getCalendar()).clone());
           cal1.setTimeInMillis(cal1.getTimeInMillis() + ((((long)argValSecs * 1000))));
           result = new XSDate(cal1, getTimezone());
        }
        
        return result;
    }

    public boolean isPopulatedFromFnCurrentDate() {
        return isPopulatedFromFnCurrentDate;
    }

    public void setPopulatedFromFnCurrentDate(boolean isPopulatedFromFnCurrentDate) {
        this.isPopulatedFromFnCurrentDate = isPopulatedFromFnCurrentDate;
    }
    
    public int getType() {
        return CLASS_XS_DATE;
    }
    
    /*
     * Do a data type cast, of an XSAnyType argument passed to this method, to
     * an XSDate object.
     */
    private XSDate castToDate(XSAnyType xsAnyType) throws TransformerException {
        if (xsAnyType instanceof XSDate) {
            XSDate date = (XSDate) xsAnyType;
            return new XSDate(date.getCalendar(), date.getTimezone());
        }

        if (xsAnyType instanceof XSDateTime) {
            XSDateTime dateTime = (XSDateTime) xsAnyType;
            return new XSDate(dateTime.getCalendar(), dateTime.getTimezone());
        }

        return parseDate(xsAnyType.stringValue());
    }
    
    /*
     * Determine whether, two timezone values (represented as XSDuration objects) 
     * are equal. 
     */
    private boolean isTimezoneEqual(XSDuration tz1, XSDuration tz2, 
                                          boolean isPopulatedFromFnCurrentDate1, 
                                                          boolean isPopulatedFromFnCurrentDate2) {
         
        boolean isTimezoneEqual = false;         
        
        if (tz1 == null && tz2 == null) {
           isTimezoneEqual = true;
        }
        else if (tz1 != null && tz2 != null) {
           isTimezoneEqual = ((tz1.hours() == tz2.hours()) && 
                                                   (tz1.minutes() == tz2.minutes()) && 
                                                           (tz1.negative() == tz2.negative()));
        }
        else if (isPopulatedFromFnCurrentDate1 || isPopulatedFromFnCurrentDate2) {
            isTimezoneEqual = true; 
        }
        
        return isTimezoneEqual;
    }

}
