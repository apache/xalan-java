/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Xalan" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 1999, Lotus
 * Development Corporation., http://www.lotus.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.xml.utils;


import java.util.Date;
import java.util.TimeZone;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.text.ParseException;
import javax.xml.transform.TransformerException;
import org.apache.xpath.objects.XString;
import org.apache.xpath.objects.XDouble;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XObject;

/**
 * <meta name="usage" content="general"/>
 * This class contains a duration object.
 */

public class Duration
{
        
    // Duration format
    public static final String du = "PnYnMnDTnHnMn.nS";
    
    private int m_year;
    private int m_month;
    private int m_day;
    private static int m_T;
    private int m_hour;
    private int m_minute;
    private double m_second;
    private boolean m_signed;
    private String m_sign = "-";
    private String m_duration;
    private int m_size;
    
    /**
     * The duration string must be a string in the format defined as the 
     * lexical representation of xs:duration in 
     * <a href="http://www.w3.org/TR/xmlschema-2/#duration">[3.2.6 duration]</a> of
     * <a href="http://www.w3.org/TR/xmlschema-2/">[XML Schema Part 2: Datatypes]</a>.
     * The duration format is basically PnYnMnD, although implementers should consult
     * <a href="http://www.w3.org/TR/xmlschema-2/">[XML Schema Part 2: Datatypes]</a> and 
     * <a href="http://www.iso.ch/markete/8601.pdf">[ISO 8601]</a> for details.
     */
    public Duration(String duration) throws TransformerException
    {
    	m_duration = duration;
    	int i;
    	m_size = duration.length();
    	m_signed = duration.startsWith("-");
    	int offset;
    	int start = offset = (m_signed ? 2 : 1); // Bypass the P
    	// Remember our Time separator
    	m_T = (i = duration.indexOf("T")) != -1 ? i : m_size ;
    	// boundary check??
    	// Maybe I can avoid doing string operations here if I will not need
    	// the individual values?? ie: int nY = ( (i = duration.indexOf("Y")) != -1) ? i - start : 0; 
    	String nY = ( (i = duration.indexOf("Y")) != -1) ? duration.substring(start, i) :"";
    	start = (i > 0) ? (i + 1) : start ;
        String nM = ((i = duration.substring(0,m_T).indexOf("M")) != -1 ) ? duration.substring(start, i) :"";
        start = (i > 0) ? (i + 1) : start ;
        String nD = ( (i = duration.indexOf("D")) != -1) ? duration.substring(start, i) :"";
                 
       try{
        m_year = nY.length() >0 ? Integer.parseInt(nY) :0;
        m_month = nM.length() >0 ? Integer.parseInt(nM) :0;
        m_day = nD.length() >0 ? Integer.parseInt(nD) :0;
        
       if (m_T < m_size)
        {
        start = (m_T < m_size) ? m_T + 1 : start;
        String nH = ( (i = duration.indexOf("H")) != -1) ? duration.substring(start, i) :"";
        start = (i > 0) ? (i + 1) : start ;
        String nMt = ( (i = duration.substring(m_T, m_size).indexOf("M")) != -1) ? duration.substring(start, i+=m_T) :"";
        start = (i > 0) ? (i + 1) : start ;
        String nS = ( (i = duration.indexOf("S")) != -1) ? duration.substring(start, i) :"";
        
        m_hour = nH.length() >0 ? Integer.parseInt(nH) :0;
        m_minute = nMt.length() >0 ? Integer.parseInt(nMt) :0;
        m_second = nS.length() >0 ? Double.valueOf(nS).doubleValue() :0;
        }
        }
        catch (NumberFormatException nfe)
        {
        throw new TransformerException(nfe);
        }
      
    }
    
	/** Added by JJK to support Xerces' stopgap internal representation
	 * as delivered through DTMSequence. We should work with them to
	 * converge on a shared time/date/duration object compatable with
	 * the schema spec.
	 * */
    public Duration(int[] xercesduration)
    {
 		// I'm not sure how they're representing negative durations!?
        m_year = Math.abs(xercesduration[0]);
        m_month = xercesduration[1];
        m_day = xercesduration[2];
        m_hour = xercesduration[3];
        m_minute = xercesduration[4];
        m_second = xercesduration[5]+(xercesduration[6]/1000);
        // xercesduration[8] is timezone related; ignore it.    	
    }

    public Duration(int year, int month, int day, int hour, int minute, double second)
    {
    // I'm not sure how they're representing negative durations!?
        m_year = Math.abs(year);
        m_month = month;
        m_day = day;
        m_hour = hour;
        m_minute = minute;
        m_second = second;
        
        if(year < 0)
          m_signed = true;
        // xercesduration[8] is timezone related; ignore it.      
    }
    
    
    /**
     * The duration function returns the duration specified in the duration 
     * string given as the argument.. 
     * The duration string that's returned must be a string in the format defined as the 
     * lexical representation of xs:duration in 
     * <a href="http://www.w3.org/TR/xmlschema-2/#duration">[3.2.6 duration]</a> of
     * <a href="http://www.w3.org/TR/xmlschema-2/">[XML Schema Part 2: Datatypes]</a>.
     * The duration format is basically PnYnMnDTnHnMnS, although implementers should consult
     * <a href="http://www.w3.org/TR/xmlschema-2/">[XML Schema Part 2: Datatypes]</a> and 
     * <a href="http://www.iso.ch/markete/8601.pdf">[ISO 8601]</a> for details.
     */
    public static Duration duration(Duration du)
    {
    	//Duration du = new Duration (duration);
    	int len = du.m_size;
              
      if (!du.m_duration.startsWith("P", (du.m_signed ? 1 : 0)) ||
          (du.m_year == 0 && du.m_month == 0 && du.m_day == 0 && du.m_hour == 0 && du.m_minute == 0 && du.m_second == 0) ||
          (m_T == len && (du.m_hour != 0 || du.m_minute != 0 || du.m_second != 0)) ||
          m_T == len -1) 
        return null;
                    
      return du;
    }
    
    /**
     * The duration function returns the duration specified in the duration 
     * string given as the argument.. 
     * The duration string that's returned must be a string in the format defined as the 
     * lexical representation of xs:duration in 
     * <a href="http://www.w3.org/TR/xmlschema-2/#duration">[3.2.6 duration]</a> of
     * <a href="http://www.w3.org/TR/xmlschema-2/">[XML Schema Part 2: Datatypes]</a>.
     * The duration format is basically PnYnMn, although implementers should consult
     * <a href="http://www.w3.org/TR/xmlschema-2/">[XML Schema Part 2: Datatypes]</a> and 
     * <a href="http://www.iso.ch/markete/8601.pdf">[ISO 8601]</a> for details.
     */
    public static Duration YMDuration(String duration) throws TransformerException
    {
    	Duration du = duration(new Duration (duration));
    	if (du == null)
    	return null;
              
      if (!duration.startsWith("P", (du.m_signed ? 1 : 0)) ||
           (du.m_day != 0 || du.m_hour != 0 || du.m_minute != 0 || du.m_second != 0) ||
          (du.m_year == 0 && du.m_month == 0) ) 
        return null;
                    
      return du;
    }
    
    /**
     * The duration function returns the duration specified in the duration 
     * string given as the argument.. 
     * The duration string that's returned must be a string in the format defined as the 
     * lexical representation of xs:duration in 
     * <a href="http://www.w3.org/TR/xmlschema-2/#duration">[3.2.6 duration]</a> of
     * <a href="http://www.w3.org/TR/xmlschema-2/">[XML Schema Part 2: Datatypes]</a>.
     * The duration format is basically PnYnMn, although implementers should consult
     * <a href="http://www.w3.org/TR/xmlschema-2/">[XML Schema Part 2: Datatypes]</a> and 
     * <a href="http://www.iso.ch/markete/8601.pdf">[ISO 8601]</a> for details.
     */
    public static Duration YMDurationFromMonths(int months) throws TransformerException
    {
    	String sign = (months > 0) ? "" : "-";
    	months = Math.abs(months);
    	int monthDu = months%12;
       int yearDu = months/12;  
    	return new Duration ( sign + "P" + (yearDu != 0 ? (Integer.toString(yearDu) + "Y") : "")  + (monthDu != 0 ? (Integer.toString(monthDu) + "M") : "")); 
      
    }
    
    public static Duration DTDuration(String duration) throws TransformerException
    {
    	Duration du = duration(new Duration (duration));
              
      if (!duration.startsWith("P", (du.m_signed ? 1 : 0)) ||
          (du.m_year != 0 || du.m_month != 0) ||
          (du.m_day == 0 && du.m_hour == 0 && du.m_minute == 0 && du.m_second == 0) ) 
        return null;
                    
      return du;
    }
    
    /**
     * The duration function returns the duration specified in the duration 
     * string given as the argument.. 
     * The duration string that's returned must be a string in the format defined as the 
     * lexical representation of xs:duration in 
     * <a href="http://www.w3.org/TR/xmlschema-2/#duration">[3.2.6 duration]</a> of
     * <a href="http://www.w3.org/TR/xmlschema-2/">[XML Schema Part 2: Datatypes]</a>.
     * The duration format is basically PnYnMn, although implementers should consult
     * <a href="http://www.w3.org/TR/xmlschema-2/">[XML Schema Part 2: Datatypes]</a> and 
     * <a href="http://www.iso.ch/markete/8601.pdf">[ISO 8601]</a> for details.
     */
    public static Duration DTDurationFromSecs(double secs) throws TransformerException
    {
    	String sign = (secs > 0) ? "" : "-"; 
    	secs = Math.abs(secs);
    	double secDu = secs%60;
       int mins = (new Double(secs)).intValue()/60;
       int mnDu = mins%60;
       int hours = mins/60;
       int hourDu = hours%24;
       int dayDu = hours/24;
    	return new Duration ( sign + "P" 
    	+ (dayDu != 0 ? (Integer.toString(dayDu) + "D") : "")
        + ((hourDu != 0 || mnDu != 0 || secDu != 0) ? "T" : "")  
        + (hourDu != 0 ? (Integer.toString(hourDu) + "H") : "")         
        + (mnDu != 0 ? (Integer.toString(mnDu) + "M") : "")
        + (secDu != 0 ? (Double.toString(secDu) + "S") : "")  ); 
    }
    
    /**
     * The duration function returns the duration specified in the duration 
     * string given as the argument.. 
     * The duration string that's returned must be a string in the format defined as the 
     * lexical representation of xs:duration in 
     * <a href="http://www.w3.org/TR/xmlschema-2/#duration">[3.2.6 duration]</a> of
     * <a href="http://www.w3.org/TR/xmlschema-2/">[XML Schema Part 2: Datatypes]</a>.
     * The duration format is basically PnYnMnDTnHnMnS, although implementers should consult
     * <a href="http://www.w3.org/TR/xmlschema-2/">[XML Schema Part 2: Datatypes]</a> and 
     * <a href="http://www.iso.ch/markete/8601.pdf">[ISO 8601]</a> for details.
     */
    public boolean DTEqual(Duration du)
    {
    	if ((m_signed == du.m_signed) &&
    	m_day == du.m_day &&
    	m_hour == du.m_hour &&
    	m_minute == du.m_minute &&
    	m_second == du.m_second)
    	return true;
    	else 
    	return false;
    }
    
    /**
     * The duration function returns the duration specified in the duration 
     * string given as the argument.. 
     * The duration string that's returned must be a string in the format defined as the 
     * lexical representation of xs:duration in 
     * <a href="http://www.w3.org/TR/xmlschema-2/#duration">[3.2.6 duration]</a> of
     * <a href="http://www.w3.org/TR/xmlschema-2/">[XML Schema Part 2: Datatypes]</a>.
     * The duration format is basically PnYnMnDTnHnMnS, although implementers should consult
     * <a href="http://www.w3.org/TR/xmlschema-2/">[XML Schema Part 2: Datatypes]</a> and 
     * <a href="http://www.iso.ch/markete/8601.pdf">[ISO 8601]</a> for details.
     */
    public boolean DTLessThan(Duration du)
    {
    	int sign = (m_signed) ? -1 : 1;
    	int hrs = m_hour + (m_day * 24);
    	int mns = m_minute + (hrs * 60);
       double secs = (m_second + (mns * 60)) * sign;
        
        sign = (du.m_signed) ? -1 : 1;
    	hrs = du.m_hour + (du.m_day * 24);
    	mns = du.m_minute + (hrs * 60);
       double secs2 = (du.m_second + (mns * 60)) * sign;
        
    	
    	if (secs < secs2) 
    	return true;
    	else 
    	return false;
    }
    
    /**
     * The duration function returns the duration specified in the duration 
     * string given as the argument.. 
     * The duration string that's returned must be a string in the format defined as the 
     * lexical representation of xs:duration in 
     * <a href="http://www.w3.org/TR/xmlschema-2/#duration">[3.2.6 duration]</a> of
     * <a href="http://www.w3.org/TR/xmlschema-2/">[XML Schema Part 2: Datatypes]</a>.
     * The duration format is basically PnYnMnDTnHnMnS, although implementers should consult
     * <a href="http://www.w3.org/TR/xmlschema-2/">[XML Schema Part 2: Datatypes]</a> and 
     * <a href="http://www.iso.ch/markete/8601.pdf">[ISO 8601]</a> for details.
     */
    public boolean DTLessThanOrEqual(Duration du)
    {
    	 
    	return (DTLessThan(du) || DTEqual(du));
    }
    
    /**
     * The duration function returns the duration specified in the duration 
     * string given as the argument.. 
     * The duration string that's returned must be a string in the format defined as the 
     * lexical representation of xs:duration in 
     * <a href="http://www.w3.org/TR/xmlschema-2/#duration">[3.2.6 duration]</a> of
     * <a href="http://www.w3.org/TR/xmlschema-2/">[XML Schema Part 2: Datatypes]</a>.
     * The duration format is basically PnYnMnDTnHnMnS, although implementers should consult
     * <a href="http://www.w3.org/TR/xmlschema-2/">[XML Schema Part 2: Datatypes]</a> and 
     * <a href="http://www.iso.ch/markete/8601.pdf">[ISO 8601]</a> for details.
     */
    public boolean DTGreaterThan(Duration du)
    {
    	return du.DTLessThan(this);
    }
    
    /**
     * The duration function returns the duration specified in the duration 
     * string given as the argument.. 
     * The duration string that's returned must be a string in the format defined as the 
     * lexical representation of xs:duration in 
     * <a href="http://www.w3.org/TR/xmlschema-2/#duration">[3.2.6 duration]</a> of
     * <a href="http://www.w3.org/TR/xmlschema-2/">[XML Schema Part 2: Datatypes]</a>.
     * The duration format is basically PnYnMnDTnHnMnS, although implementers should consult
     * <a href="http://www.w3.org/TR/xmlschema-2/">[XML Schema Part 2: Datatypes]</a> and 
     * <a href="http://www.iso.ch/markete/8601.pdf">[ISO 8601]</a> for details.
     */
    public boolean DTGreaterThanOrEqual(Duration du)
    {
    	 
    	return (DTGreaterThan(du) || DTEqual(du));
    }
    
    /**
     * The duration function returns the duration specified in the duration 
     * string given as the argument.. 
     * The duration string that's returned must be a string in the format defined as the 
     * lexical representation of xs:duration in 
     * <a href="http://www.w3.org/TR/xmlschema-2/#duration">[3.2.6 duration]</a> of
     * <a href="http://www.w3.org/TR/xmlschema-2/">[XML Schema Part 2: Datatypes]</a>.
     * The duration format is basically PnYnMnDTnHnMnS, although implementers should consult
     * <a href="http://www.w3.org/TR/xmlschema-2/">[XML Schema Part 2: Datatypes]</a> and 
     * <a href="http://www.iso.ch/markete/8601.pdf">[ISO 8601]</a> for details.
     */
    public boolean YMEqual(Duration du)
    {
    	if ((m_signed == du.m_signed) &&
    	m_year == du.m_year &&
    	m_month == du.m_month)
    	return true;
    	else 
    	return false;
    }
    
     /**
     * The duration function returns the duration specified in the duration 
     * string given as the argument.. 
     * The duration string that's returned must be a string in the format defined as the 
     * lexical representation of xs:duration in 
     * <a href="http://www.w3.org/TR/xmlschema-2/#duration">[3.2.6 duration]</a> of
     * <a href="http://www.w3.org/TR/xmlschema-2/">[XML Schema Part 2: Datatypes]</a>.
     * The duration format is basically PnYnMnDTnHnMnS, although implementers should consult
     * <a href="http://www.w3.org/TR/xmlschema-2/">[XML Schema Part 2: Datatypes]</a> and 
     * <a href="http://www.iso.ch/markete/8601.pdf">[ISO 8601]</a> for details.
     */
    public boolean YMLessThan(Duration du)
    {
    	int sign = (m_signed) ? -1 : 1;
       int months = (m_month + (m_year * 12)) * sign;
       
        sign = (du.m_signed) ? -1 : 1;
       int months2 = (du.m_month + (du.m_year * 12)) * sign;
       
        if (months < months2)
    	return true;
    	else 
    	return false;
    }
    
    /**
     * The duration function returns the duration specified in the duration 
     * string given as the argument.. 
     * The duration string that's returned must be a string in the format defined as the 
     * lexical representation of xs:duration in 
     * <a href="http://www.w3.org/TR/xmlschema-2/#duration">[3.2.6 duration]</a> of
     * <a href="http://www.w3.org/TR/xmlschema-2/">[XML Schema Part 2: Datatypes]</a>.
     * The duration format is basically PnYnMnDTnHnMnS, although implementers should consult
     * <a href="http://www.w3.org/TR/xmlschema-2/">[XML Schema Part 2: Datatypes]</a> and 
     * <a href="http://www.iso.ch/markete/8601.pdf">[ISO 8601]</a> for details.
     */
    public boolean YMLessThanOrEqual(Duration du)
    {
    	 
    	return (YMLessThan(du) || YMEqual(du));
    }
    
    /**
     * The duration function returns the duration specified in the duration 
     * string given as the argument.. 
     * The duration string that's returned must be a string in the format defined as the 
     * lexical representation of xs:duration in 
     * <a href="http://www.w3.org/TR/xmlschema-2/#duration">[3.2.6 duration]</a> of
     * <a href="http://www.w3.org/TR/xmlschema-2/">[XML Schema Part 2: Datatypes]</a>.
     * The duration format is basically PnYnMnDTnHnMnS, although implementers should consult
     * <a href="http://www.w3.org/TR/xmlschema-2/">[XML Schema Part 2: Datatypes]</a> and 
     * <a href="http://www.iso.ch/markete/8601.pdf">[ISO 8601]</a> for details.
     */
    public boolean YMGreaterThan(Duration du)
    {
    	return du.YMLessThan(this);
    }
    
    /**
     * The duration function returns the duration specified in the duration 
     * string given as the argument.. 
     * The duration string that's returned must be a string in the format defined as the 
     * lexical representation of xs:duration in 
     * <a href="http://www.w3.org/TR/xmlschema-2/#duration">[3.2.6 duration]</a> of
     * <a href="http://www.w3.org/TR/xmlschema-2/">[XML Schema Part 2: Datatypes]</a>.
     * The duration format is basically PnYnMnDTnHnMnS, although implementers should consult
     * <a href="http://www.w3.org/TR/xmlschema-2/">[XML Schema Part 2: Datatypes]</a> and 
     * <a href="http://www.iso.ch/markete/8601.pdf">[ISO 8601]</a> for details.
     */
    public boolean YMGreaterThanOrEqual(Duration du)
    {
    	 
    	return (YMGreaterThan(du) || YMEqual(du));
    }
    
    /**
     * The duration function returns the duration specified in the duration 
     * string given as the argument.. 
     * The duration string that's returned must be a string in the format defined as the 
     * lexical representation of xs:duration in 
     * <a href="http://www.w3.org/TR/xmlschema-2/#duration">[3.2.6 duration]</a> of
     * <a href="http://www.w3.org/TR/xmlschema-2/">[XML Schema Part 2: Datatypes]</a>.
     * The duration format is basically PnYnMnDTnHnMnS, although implementers should consult
     * <a href="http://www.w3.org/TR/xmlschema-2/">[XML Schema Part 2: Datatypes]</a> and 
     * <a href="http://www.iso.ch/markete/8601.pdf">[ISO 8601]</a> for details.
     */
    public boolean equals(Duration du)
    {
    	if ((m_signed == du.m_signed) &&
    	m_year == du.m_year &&
    	m_month == du.m_month &&
    	m_day == du.m_day &&
    	m_hour == du.m_hour &&
    	m_minute == du.m_minute &&
    	m_second == du.m_second)
    	return true;
    	else 
    	return false;
    }
    
    
    
    public Duration addYMDuration(Duration d)
    throws TransformerException
    {
    	
        String sign;
        int monthDu, yearDu;
        if (m_signed == d.m_signed) //!signed1 && !signed1 || signed1 && signed2)
        {
        	sign = (m_signed) ? m_sign : "";
           int months = m_month + d.m_month;
            monthDu = months%12;
            yearDu = months/12 + m_year + d.m_year;  
        }
        else
        {
        	int y1 = m_year;
        	int y2 = d.m_year;
        	int y = (m_signed) ? y2 - y1 : y1 - y2;
        	
        	int m1 = m_month;
        	int m2 = d.m_month;
        	int months = (m_signed) ? m2 - m1 + (y*12)  : m1 - m2 + (y*12);
        	sign = months > 0 ? "" : "-";
        	 
        	months = Math.abs(months);
        	monthDu = months%12;
        	yearDu = months/12;  
        	
        }
        
        return new Duration(sign + "P" + (yearDu != 0 ? (Integer.toString(yearDu) + "Y") : "")  + (monthDu != 0 ? (Integer.toString(monthDu) + "M") : "")); 
        
    }
    
    public Duration subtractYMDuration(Duration d)
    throws TransformerException
    {
    	
        String sign;
        int monthDu, yearDu;
        
       if (m_signed == d.m_signed)
        {
        	int y1 = m_year;
        	int y2 = d.m_year;
        	int y = (m_signed) ? y2 - y1 : y1 - y2;
        	
        	int m1 = m_month;
        	int m2 = d.m_month;
        	int months = (m_signed) ? m2 - m1 + (y*12)  : m1 - m2 + (y*12);
        	sign = months > 0 ? "" : m_sign;
        	
        	months = Math.abs(months); 
        	monthDu = months%12;
        	yearDu = months/12;  
        	
        }
        else
        {
        	sign = (m_signed) ? m_sign : "";
           int months = m_month + d.m_month;
            monthDu = months%12;
            yearDu = months/12 + m_year + d.m_year;  
        }
        
        return new Duration(sign + "P" + (yearDu != 0 ? (Integer.toString(yearDu) + "Y") : "")  + (monthDu != 0 ? (Integer.toString(monthDu) + "M") : "")); 
        
    }
    
    public Duration multiplyYMDuration(double dec)
    throws TransformerException
    {
    	int sign = (m_signed) ? -1 : 1;
       double months = (m_month + (m_year * 12)) * dec * sign;
        months = Math.floor(months + .5);
        
        //months = (new Double(Math.floor(new Integer(months).doubleValue()))).intValue()
        
       double ms = Math.abs(months);   
       int monthDu = (new Double(ms%12)).intValue();
       int yearDu = (new Double(ms/12)).intValue();
       //new Double(Math.floor(new Integer(months/12).doubleValue())).intValue();
       
        return new Duration((months > 0 ? "" : m_sign) + "P" + (yearDu != 0 ? (Integer.toString(yearDu) + "Y") : "")  + (monthDu != 0 ? (Integer.toString(monthDu) + "M") : "")); 
        
    	}
    
    public Duration divideYMDuration(double dec)
    throws TransformerException
    {int sign = (m_signed) ? -1 : 1;
       double months = (m_month + (m_year * 12)) / dec * sign;
        months = Math.floor(months + .5);
        
      double ms = Math.abs(months);    
       int monthDu = (new Double(ms%12)).intValue();
       int yearDu = (new Double(ms/12)).intValue();
       //new Double(Math.floor(new Integer(months/12).doubleValue())).intValue();
       
       return new Duration((months > 0 ? "" : m_sign) + "P" + (yearDu != 0 ? (Integer.toString(yearDu) + "Y") : "")  + (monthDu != 0 ? (Integer.toString(monthDu) + "M") : "")); 
        }
    
    public Duration addDTDuration(Duration du)
    throws TransformerException
    {
    	String sign;
        int dayDu, hourDu, mnDu;
        double secDu;
        if (m_signed == du.m_signed) //!signed1 && !signed1 || signed1 && signed2)
        {
        	sign = (m_signed) ? m_sign : "";
        	double secs = m_second + du.m_second;
        	secDu = secs%60;
        	int mins = (new Double(secs)).intValue()/60 + m_minute + du.m_minute;
        	mnDu = mins%60;
        	int hours = mins/60 + m_hour + du.m_hour;
        	hourDu = hours%24;
            dayDu = hours/24 + m_day + du.m_day;
        }
        else
        {
        	int d1 = m_day;
        	int d2 = du.m_day;
        	int d = (m_signed) ? d2 - d1 : d1 - d2;
        	
        	int h1 = m_hour;
        	int h2 = du.m_hour;
        	int h = (m_signed) ? h2 - h1 + (d*24)  : h1 - h2 + (d*24);
        	
        	int m1 = m_minute;
        	int m2 = du.m_minute;
        	int m = (m_signed) ? m2 - m1 + (h*60)  : m1 - m2 + (h*60);
        	
        	double s1 = m_second;
        	double s2 = du.m_second;
        	double s = (m_signed) ? s2 - s1 + (m*60)  : s1 - s2 + (m*60);
        	sign = s > 0 ? "" : m_sign;
        	
        	s = Math.abs(s); 
        	secDu = s%60;
        	m = (new Double(s)).intValue()/60;
        	mnDu = m%60;
        	h = m/60;
        	hourDu = h%24;
        	dayDu = h/24;  
        	
        }
        
        return new Duration(sign + "P" + (dayDu != 0 ? (Integer.toString(dayDu) + "D") : "")
        + ((hourDu != 0 || mnDu != 0 || secDu != 0) ? "T" : "")  
        + (hourDu != 0 ? (Integer.toString(hourDu) + "H") : "")         
        + (mnDu != 0 ? (Integer.toString(mnDu) + "M") : "")
        + (secDu != 0 ? (Double.toString(secDu) + "S") : "")  ); 
        }
    
    public Duration subtractDTDuration(Duration du)
    throws TransformerException
    {
    	String sign;
        int dayDu, hourDu, mnDu;
         double secDu;
        if (m_signed == du.m_signed) 
        {
        	int d1 = m_day;
        	int d2 = du.m_day;
        	int d = (m_signed) ? d2 - d1 : d1 - d2;
        	
        	int h1 = m_hour;
        	int h2 = du.m_hour;
        	int h = (m_signed) ? h2 - h1 + (d*24)  : h1 - h2 + (d*24);
        	
        	int m1 = m_minute;
        	int m2 = du.m_minute;
        	int m = (m_signed) ? m2 - m1 + (h*60)  : m1 - m2 + (h*60);
        	
        	double s1 = m_second;
        	double s2 = du.m_second;
        	double s = (m_signed) ? s2 - s1 + (m*60)  : s1 - s2 + (m*60);
        	sign = s > 0 ? "" : m_sign;
        	
        	s = Math.abs(s); 
        	secDu = s%60;
        	m = (new Double(s)).intValue()/60;
        	mnDu = m%60;
        	h = m/60;
        	hourDu = h%24;
        	dayDu = h/24;  
        	
        }
        else
        {
        	sign = (m_signed) ? m_sign : "";
        	double secs = m_second + du.m_second;
        	secDu = secs%60;
        	int mins = (new Double(secs)).intValue()/60 + m_minute + du.m_minute;
        	mnDu = mins%60;
        	int hours = mins/60 + m_hour + du.m_hour;
        	hourDu = hours%24;
            dayDu = hours/24 + m_day + du.m_day;
        }
        
        
        return new Duration(sign + "P" + (dayDu != 0 ? (Integer.toString(dayDu) + "D") : "")
        + ((hourDu != 0 || mnDu != 0 || secDu != 0) ? "T" : "")  
        + (hourDu != 0 ? (Integer.toString(hourDu) + "H") : "")         
        + (mnDu != 0 ? (Integer.toString(mnDu) + "M") : "")
        + (secDu != 0 ? (Double.toString(secDu) + "S") : "")  ); 
    	}
    
    public Duration multiplyDTDuration(double dec)
    throws TransformerException
    {
    	int sign = (m_signed) ? -1 : 1;
    	int hrs = m_hour + (m_day * 24);
    	int mns = m_minute + (hrs * 60);
       double secs = (m_second + (mns * 60)) * dec * sign;
        
        double s = Math.abs(secs);   
       double secDu = s%60;
        mns = (new Double(s/60)).intValue();
       int mnDu = mns%60;
        hrs = mns/60;
       int hrDu = hrs%24;
       int dayDu = hrs/24;
       
       return new Duration((secs > 0 ? "" : m_sign) + "P" + (dayDu != 0 ? (Integer.toString(dayDu) + "D") : "")  
       + ((hrDu != 0 || mnDu != 0 || secDu != 0) ? "T" : "") 
       + (hrDu != 0 ? (Integer.toString(hrDu) + "H") : "")
       + (mnDu != 0 ? (Integer.toString(mnDu) + "M") : "")
       + (secDu != 0 ? (Double.toString(secDu) + "S") : "")); 
        
    	}
    
    public Duration divideDTDuration(double dec)
    throws TransformerException
    {
    	int sign = (m_signed) ? -1 : 1;       
    	int hrs = m_hour + (m_day * 24);
    	int mns = m_minute + (hrs * 60);
       double secs = Math.floor(((m_second + (mns * 60)) / dec) + .5) * sign;
        secs = Math.floor(secs);
       
       double s = Math.abs(secs);    
       double secDu = s%60;
        mns = (new Double(s/60)).intValue();
       int mnDu = mns%60;
        hrs = mns/60;
       int hrDu = hrs%24;
       int dayDu = hrs/24;
       return new Duration((secs > 0 ? "" : m_sign) + "P" + (dayDu != 0 ? (Integer.toString(dayDu) + "D") : "")  
       + ((hrDu != 0 || mnDu != 0 || secDu != 0) ? "T" : "") 
       + (hrDu != 0 ? (Integer.toString(hrDu) + "H") : "")
       + (mnDu != 0 ? (Integer.toString(mnDu) + "M") : "")
       + (secDu != 0 ? (Double.toString(secDu) + "S") : "")); 
             }
    
    public void setYears(int years)
    {
    	m_year = years;
    }
    
    public int getYears()
    {
    	return m_year;
    }
    
    public void setMonths(int months)
    {
    	m_month = months;
    }
    
    public int getMonths()
    {
    	return m_month;
    }
    
    public void setDays(int days)
    {
    	m_day = days;
    }
    
    public int getDays()
    {
    	return m_day;
    }
    
    public void setHours(int hours)
    {
    	m_hour = hours;
    }
    
    public int getHours()
    {
    	return m_hour;
    }
    
    public void setSeconds(double secs)
    {
    	m_second = secs;
    }
    
    public double getSeconds()
    {
    	return m_second;
    }
    
    public void setMinutes(int mts)
    {
    	m_minute = mts;
    }
    
    public int getMinutes()
    {
    	return m_minute;
    }
    
    public boolean getSigned()
    {
    	return m_signed;
    }
    
    public String toString()
    {
      if (null == m_duration)
      {
        m_duration =
          (m_signed ? m_sign : "")
            + "P"
            + ((m_year > 0) ? m_year + "Y" : "")
            + ((m_month > 0) ? m_month + "M" : "")
            + ((m_day > 0) ? m_day +"D": "")
            + ((m_hour != 0 || m_minute != 0 || m_second != 0) ? "T" : "")
            + ((m_hour != 0) ? m_hour + "H" : "")
            + ((m_minute != 0) ? m_minute + "M" : "")
            + ((m_second != 0) ? m_second + "S" : "");
      }
        
      return m_duration;
    	// return m_duration;
    }
    
    

}