/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights
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


import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.xml.transform.TransformerException;
import org.apache.xml.dtm.XType;

/**
 * <meta name="usage" content="general"/>
 * This class contains a duration object.
 */

public class DateTimeObj
{
    // Datetime formats (era and zone handled separately).
    public static final String dt1 = "yyyy-MM-dd'T'HH:mm:ss.ss";
    public static final String dt2 = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String dt3 = "yyyy-MM-dd'T'HH:mm";
    public static final String d = "yyyy-MM-dd";
    public static final String gym = "yyyy-MM";
    public static final String gy = "yyyy";
    public static final String gmd = "MM-dd";
    public static final String gm = "MM";
    public static final String gd = "dd";
    public static final String t1 = "HH:mm:ss.ss";
    public static final String t2 = "HH:mm:ss";
    
    private int m_year;
    private int m_month;
    private int m_day;
    private int m_T;
    private int m_hour;
    private int m_minute;
    private double m_second;
    private String m_zone;
    private Date m_date;
    private String m_dateTime;
    private int m_size;
    private boolean m_signed = false;
    
    /**
     * The duration string must be a string in the format defined as the 
     * lexical representation of xs:duration in 
     * <a href="http://www.w3.org/TR/xmlschema-2/#duration">[3.2.6 duration]</a> of
     * <a href="http://www.w3.org/TR/xmlschema-2/">[XML Schema Part 2: Datatypes]</a>.
     * The duration format is basically PnYnMnD, although implementers should consult
     * <a href="http://www.w3.org/TR/xmlschema-2/">[XML Schema Part 2: Datatypes]</a> and 
     * <a href="http://www.iso.ch/markete/8601.pdf">[ISO 8601]</a> for details.
     */
    public DateTimeObj(String dateTimeIn) throws TransformerException
    {
    	this(dateTimeIn, new String[]{dt1, dt2, dt3});
    }
    
    public DateTimeObj(String dateTimeIn, String[] formatsIn) throws TransformerException
    {
    	try {
    	
    	int i;
    	
    	m_size = dateTimeIn.length();
    	// Remember our Time separator
    	m_T = (i = dateTimeIn.indexOf("T")) != -1 ? i : m_size ;
    	// boundary check??
      String[] edz = getEraDatetimeZone(dateTimeIn);
      m_signed = edz[0].length() >0;
      m_dateTime = edz[1];
      m_zone = edz[2];
      if (m_dateTime == null || m_zone == null) 
        return;
                    
      m_date = testFormats(m_dateTime, formatsIn);
      int index = dateTimeIn.indexOf('.');
    	double dec = (index <0) ? 0 :Double.parseDouble(m_dateTime.substring(index));
    	
      SimpleDateFormat dateFormat = new SimpleDateFormat(dt1);
      dateFormat.setLenient(false);
      Calendar cal = Calendar.getInstance();
      cal.setTime(m_date);
      m_year = cal.get(Calendar.YEAR);
      m_month = cal.get(Calendar.MONTH) + 1; // Calendar returns months starting at 0... 
      m_day = cal.get(Calendar.DATE);
      m_hour = cal.get(Calendar.HOUR);
      m_minute = cal.get(Calendar.MINUTE);
      m_second = (index <0) ? cal.get(Calendar.SECOND) 
                            : cal.get(Calendar.SECOND) + Double.parseDouble(m_dateTime.substring(index)); 
    	}
    	catch (Exception ex)
    	{
    		throw new TransformerException(ex);
    	}  
                  
    }
    
    public DateTimeObj(Date date, String formatOut) throws TransformerException
    {
      //m_zone = date.getTimezoneOffset();
      SimpleDateFormat dateFormat = new SimpleDateFormat(formatOut);
      dateFormat.setLenient(false);
      m_dateTime = dateFormat.format(date);
      m_date = date;
      m_size = m_dateTime.length();
    	// Remember our Time separator
      m_T = (m_T = m_dateTime.indexOf("T")) != -1 ? m_T : m_size ;
    	
      Calendar cal = Calendar.getInstance();
      cal.setLenient(false);
      cal.setTime(date);

      m_signed = (cal.get(Calendar.ERA) == GregorianCalendar.BC);
      m_year = cal.get(Calendar.YEAR);
     // String monthStr = m_dateTime.substring(5, 7);
     // m_month = Integer.valueOf(monthStr).intValue();
     // String dayStr = m_dateTime.substring(8, 10);
     // m_day = Integer.valueOf(dayStr).intValue();
      m_month = cal.get(Calendar.MONTH) + 1;
      m_day = cal.get(Calendar.DATE); 
      m_hour = cal.get(Calendar.HOUR);
      m_minute = cal.get(Calendar.MINUTE);
      m_second = cal.get(Calendar.SECOND);

      //      m_year = date.getYear();
      // m_month = date.getMonth();
      //      m_day = date.getDay();
      //      m_hour = date.getHours();
      //      m_minute = date.getMinutes();
      //      m_second = date.getSeconds(); 
    }

	/** Added by JJK to support Xerces' stopgap internal representation
	 * as delivered through DTMSequence. We should work with them to
	 * converge on a shared time/date/duration object compatable with
	 * the schema spec.
	 * 
	 * %BUG% They return some bogus fields due to normalization
	 * side-effects (they use a pseudodate of 2000/1/15 or something
	 * like that). To fix that we need to check the schema-type
	 * and ignore those fields...
	 * 
	 * %BUG% No idea what format I should be using for m_dateTime!
	 * */
     public DateTimeObj(int[] xercesdate,String typeLocalName)
    {
    	try
    	{
          m_year = Math.abs(xercesdate[0]);
          m_month = xercesdate[1];
          m_day = xercesdate[2];
          m_hour = xercesdate[3];
          m_minute = xercesdate[4];
          m_second = xercesdate[5]+(xercesdate[6]/1000);
          //m_zone = xercesdate[6]; // not sure how encoded
          
          m_signed = (xercesdate[0] < 0);
          
          /* Works, but gives slightly different results. Need to
           * investigate... 
         int type = XType.getTypeFromLocalName(typeLocalName);
         String format;
         switch (type)
         {
         	case XType.DATE:
         	format = d;
         	break;
         	case XType.TIME:
         	format = t1;
         	break;
         	default:
         	format = dt1;
         }
         Calendar cal = Calendar.getInstance();
         cal.set(m_year,(m_month-1),m_day,m_hour,m_minute,(int)m_second);
          
          m_date = cal.getTime();
          SimpleDateFormat dateFormat = new SimpleDateFormat(format);
          dateFormat.setLenient(false);
          m_dateTime = dateFormat.format(m_date);
          */
          
          // Don't particularly like constructing this string,
          // but will do for now. Need to investigate why Date or 
          // Calendar don't work here...
          
         m_dateTime = formatYears(m_year) + "-" + formatDigits(m_month) + "-" + formatDigits(m_day);
         //  m_dateTime = m_year + "-" + m_month + "-" + m_day;
         if (m_hour != 0 && m_minute != 0 && m_second != 0)
            m_dateTime = m_dateTime + "T" + formatDigits(m_hour) + ":" + formatDigits(m_minute) + ":" + m_second; 
          String[] formatsIn = {dt2, d, t2};
          m_date = testFormats(m_dateTime, formatsIn);
          
         if (m_signed)
             m_dateTime = "-" + m_dateTime;
             
        }
    	catch (Exception ex)
    	{
    		throw new WrappedRuntimeException(ex);
    	}  
      

	  // This is wrong but I'm not sure what format I should be using!
      //SimpleDateFormat dateFormat = new SimpleDateFormat();
      //dateFormat.setLenient(false);
      //m_dateTime=dateFormat.format(m_date);
    }
    
    /**
     * Attempt to parse an input string with the allowed formats, returning
     * null if none of the formats work. Input formats are passed in longest to shortest,
     * so if any parse operation fails with a parse error in the string, can
     * immediately return null.
     */
    private static Date testFormats (String in, String[] formats)
      throws ParseException
    {
    	
      for (int i = 0; i <formats.length; i++)
      {
      	try
    	{        
          SimpleDateFormat dateFormat = new SimpleDateFormat(formats[i]);
          dateFormat.setLenient(false);          
          return dateFormat.parse(in);
          }
    	catch (ParseException pe)
    	{
    		if ( i == formats.length -1)
    		throw pe;
    	}
        
      }
      return null;
    	
    }
    
    /**
     * Returns an array with the 3 components that a datetime input string 
     * may contain: - (for BC era), datetime, and zone. If the zone is not
     * valid, return null for that component.
     */
    private  String[] getEraDatetimeZone(String in) throws TransformerException
    {
      String leader = "";
      String datetime = in;
      String zone = "";
      if (in.charAt(0)=='-')
      {
        leader = "-"; //  '+' is implicit , not allowed
        datetime = in.substring(1);
      }
      int z = getZoneStart(datetime);
      if (z > 0)
      {
        zone = datetime.substring(z);
       if(datetime.charAt(z-1) == 'T')
          z--;
        datetime = datetime.substring(0, z);
      }
      else if (z == -2)
        zone = null;
      //System.out.println("'" + leader + "' " + datetime + " " + zone);
      return new String[]{leader, datetime, zone};  
    }
    
    /**
     * Returns an array with the 3 components that a datetime input string 
     * may contain: - (for BC era), datetime, and zone. If the zone is not
     * valid, return null for that component.
     */
    private  Date normalizeDate(Date date, String zone)
    {
      if (zone == null || zone.length()==0 || zone.equals("Z") || zone.equals("00:00") )
      return date;
      else
      {
      	int sign = 1;
      	if (zone.startsWith("-"))
      	{
      		sign = -1;
      	}
      	int index = zone.indexOf(":");
      	int hr = sign * (Integer.parseInt(zone.substring(1, index)));
      	int min = sign * (Integer.parseInt(zone.substring(index+1))); 
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR, hr);
        cal.add(Calendar.MINUTE, min);
        return cal.getTime();
      }  
    }        
    
    /**
     * Get the start of zone information if the input ends
     * with 'Z' or +/-hh:mm. If a zone string is not
     * found, return -1; if the zone string is invalid,
     * return -2.
     */
    private int getZoneStart (String datetime) throws TransformerException
    {
      if (datetime.indexOf("Z") == datetime.length()-1)
        return datetime.indexOf("Z");
      else if (
               (datetime.lastIndexOf("-") == datetime.length()-6 &&
                datetime.charAt(datetime.length()-3) == ':')               
                || 
                (datetime.indexOf("+") == datetime.length() -6)
              )
      {
        try
        {
          SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
          dateFormat.setLenient(false);
          Date d = dateFormat.parse(datetime.substring(datetime.length() -5));
          return datetime.length()-6;
        }
        catch (ParseException pe)
        {
          throw new TransformerException(pe);
          //return -2; // Invalid.
        }

      }
        return -1; // No zone information.
    }
    
    /**
     * See dateTime.
     */
    public static DateTimeObj date() throws TransformerException
    {
      return currentDateTime(d);
    }
    
   
   /**
     * The date:date function returns the date specified in the date/time string given 
     * as the argument. If no argument is given, then the current local date/time, as 
     * returned by date:date-time is used as a default argument. 
     * The date/time string that's returned must be a string in the format defined as the 
     * lexical representation of xs:dateTime in 
     * <a href="http://www.w3.org/TR/xmlschema-2/#dateTime">[3.2.7 dateTime]</a> of
     * <a href="http://www.w3.org/TR/xmlschema-2/">[XML Schema Part 2: Datatypes]</a>.
     * If the argument is not in either of these formats, date:date returns an empty string (''). 
     * The date/time format is basically CCYY-MM-DDThh:mm:ss, although implementers should consult 
     * <a href="http://www.w3.org/TR/xmlschema-2/">[XML Schema Part 2: Datatypes]</a> and 
     * <a href="http://www.iso.ch/markete/8601.pdf">[ISO 8601]</a> for details. 
     * The date is returned as a string with a lexical representation as defined for xs:date in 
     * [3.2.9 date] of [XML Schema Part 2: Datatypes]. The date format is basically CCYY-MM-DD, 
     * although implementers should consult [XML Schema Part 2: Datatypes] and [ISO 8601] for details.
     * If no argument is given or the argument date/time specifies a time zone, then the date string 
     * format must include a time zone, either a Z to indicate Coordinated Universal Time or a + or - 
     * followed by the difference between the difference from UTC represented as hh:mm. If an argument 
     * is specified and it does not specify a time zone, then the date string format must not include 
     * a time zone. 
     */
    public static DateTimeObj date(String dateIn) throws TransformerException
    {
    	DateTimeObj dt = new DateTimeObj (dateIn, new String[]{d});
              
      return dt;
    }
    
    
    public static DateTimeObj date(Date dateIn) throws TransformerException
    {
    	DateTimeObj dt = new DateTimeObj (dateIn, d);
              
      return dt;
    }
    
    
    /**
     * The date:time function returns the time specified in the time string given 
     * as the argument.  
     * The time string that's returned must be a string in the format defined as the 
     * lexical representation of xs:time in 
     * <a href="http://www.w3.org/TR/xmlschema-2/#time">[3.2.7 time]</a> of
     * <a href="http://www.w3.org/TR/xmlschema-2/">[XML Schema Part 2: Datatypes]</a>. 
     * If the argument string is not in this format, date:time returns an empty string (''). 
     * The time format is basically hh:mm:ss, although implementers should consult 
     * If no argument is given or the argument time specifies a time zone, then the time string 
     * format must include a time zone, the difference from UTC represented as hh:mm. If an argument 
     * is specified and it does not specify a time zone, then the time string format must not include 
     * a time zone. 
     */
    public static DateTimeObj time() throws TransformerException
    {
    	return currentDateTime(t1);
    }
    
    
    public static DateTimeObj time(String timeIn) throws TransformerException
    {
    	DateTimeObj dt = new DateTimeObj (timeIn, new String[]{t1, t2});
              
      return dt;
    }
    
    
    public static DateTimeObj time(Date timeIn) throws TransformerException
    {
    	DateTimeObj dt = new DateTimeObj (timeIn, t2);
              
      return dt;
    }
    
    
    public static DateTimeObj gYearMonth(String dateIn) throws TransformerException
    {
    	// Check on java g Date stuff...
    	DateTimeObj dt = new DateTimeObj (dateIn, new String[]{gym});
              
      return dt;
    }
    
    
    public static DateTimeObj gYear(String dateIn) throws TransformerException
    {
    	// Check on java g Date stuff...
    	DateTimeObj dt = new DateTimeObj (dateIn, new String[]{gy});
              
      return dt;
    }
    
    
    public static DateTimeObj gMonth(String dateIn) throws TransformerException
    {
    	// Check on java g Date stuff...
    	DateTimeObj dt = new DateTimeObj (dateIn, new String[]{gm});
              
      return dt;
    }
    
    public static DateTimeObj gMonthDay(String dateIn) throws TransformerException
    {
    	// Check on java g Date stuff...
    	DateTimeObj dt = new DateTimeObj (dateIn, new String[]{gmd});
              
      return dt;
    }
    
    
    public static DateTimeObj gDay(String dateIn) throws TransformerException
    {
    	// Check on java g Date stuff...
    	DateTimeObj dt = new DateTimeObj (dateIn, new String[]{gd});
              
      return dt;
    }
    
    
    /**
     * The date:date-time function returns the current date and time as a date/time string. 
     * The date/time string that's returned must be a string in the format defined as the 
     * lexical representation of xs:dateTime in 
     * <a href="http://www.w3.org/TR/xmlschema-2/#dateTime">[3.2.7 dateTime]</a> of
     * <a href="http://www.w3.org/TR/xmlschema-2/">[XML Schema Part 2: Datatypes]</a>.
     * The date/time format is basically CCYY-MM-DDThh:mm:ss, although implementers should consult
     * <a href="http://www.w3.org/TR/xmlschema-2/">[XML Schema Part 2: Datatypes]</a> and 
     * <a href="http://www.iso.ch/markete/8601.pdf">[ISO 8601]</a> for details.
     * The date/time string format must include a time zone, either a Z to indicate Coordinated 
     * Universal Time or a + or - followed by the difference between the difference from UTC 
     * represented as hh:mm. 
     */   
    public static DateTimeObj currentDateTime(String pattern) throws TransformerException
    {
      Calendar cal = Calendar.getInstance();
      Date datetime = cal.getTime();
      DateTimeObj dt = new DateTimeObj(datetime, pattern);
      
      int offset = cal.get(Calendar.ZONE_OFFSET) + cal.get(Calendar.DST_OFFSET);
      // If there is no offset, we have "Coordinated
      // Universal Time."
      if (offset == 0)
        dt.setZone("Z");
      else
      {
        // Convert milliseconds to hours and minutes
        int hrs = offset/(60*60*1000);
        // In a few cases, the time zone may be +/-hh:30.
        int min = offset%(60*60*1000);
        char posneg = hrs < 0? '-': '+';
        dt.setZone(posneg + formatDigits(hrs) + ':' + formatDigits(min));
      }
      return dt;
    }
    
    
    /**
     * See above
     */
    public static DateTimeObj dateTime() throws TransformerException
    {
    	return dateTime(dt1);
    }
    
    
    /**
     * The date:date-time function returns the date and time specified in the date/time 
     * string given as the argument.. 
     * The date/time string that's returned must be a string in the format defined as the 
     * lexical representation of xs:dateTime in 
     * <a href="http://www.w3.org/TR/xmlschema-2/#dateTime">[3.2.7 dateTime]</a> of
     * <a href="http://www.w3.org/TR/xmlschema-2/">[XML Schema Part 2: Datatypes]</a>.
     * The date/time format is basically CCYY-MM-DDThh:mm:ss, although implementers should consult
     * <a href="http://www.w3.org/TR/xmlschema-2/">[XML Schema Part 2: Datatypes]</a> and 
     * <a href="http://www.iso.ch/markete/8601.pdf">[ISO 8601]</a> for details.
     * The date/time string format must include a time zone, either a Z to indicate Coordinated 
     * Universal Time or a + or - followed by the difference between the difference from UTC 
     * represented as hh:mm. 
     */
    public static DateTimeObj dateTime(String dateTimeIn)
      throws TransformerException
    {
    	return new DateTimeObj(dateTimeIn);
    }
    
    /**
     * Represent the hours and minutes with two-digit strings.
     * @param q hrs or minutes.
     * @return two-digit String representation of hrs or minutes.
     */
    private static String formatDigits(int q)
    {
      String dd = String.valueOf(Math.abs(q));
      return dd.length() == 1 ? "0" + dd : dd;
    }
    
    /**
     * Represent the hours and minutes with two-digit strings.
     * @param q hrs or minutes.
     * @return two-digit String representation of hrs or minutes.
     */
    private static String formatYears(int q)
    {
      String dd = String.valueOf(Math.abs(q));
      int len = dd.length();
      for (int i=len; i<4; i++)
        dd = "0" + dd;
      return dd;
    }
    
    /**
     * Return the hours from a given timezone.
     * @param timezone string
     * @return hours from timezone
     */
    private static int getHrsFromZone(String zone)
    {
      if (zone.equals("Z"))
      return 0;
      int sign = 1;
      if (zone.startsWith("-"))
      {
        sign = -1;
      }
     int index = zone.indexOf(":");
     int hr = sign * (Integer.parseInt(zone.substring(1, index)));
     int min = sign * (Integer.parseInt(zone.substring(index+1))); 
     return hr;
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
    public Duration getYMDuration(DateTimeObj dt) throws TransformerException
    {
    	Duration du = null;
     double secs = new Long((m_date.getTime() - dt.m_date.getTime())/1000).doubleValue(); 
      
      String sign = secs <0 ? "-" : "";
     double days = Math.abs(((secs/60)/60)/24);
     double months = days/31 + (days%31 <15.5 ? 0 : 1);
     int monthDu = new Double(months%12).intValue();
     int yearDu = new Double(months/12).intValue();
     
     return new Duration(sign + "P" + (yearDu != 0 ? (Integer.toString(yearDu) + "Y") : "")
        + (monthDu != 0 ? (Integer.toString(monthDu) + "M") : "") ); 
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
    public Duration getDTDuration(DateTimeObj dt) throws TransformerException
    {
    	Duration du = null;
     double secs = new Long((m_date.getTime() - dt.m_date.getTime())/1000).doubleValue(); 
      
      String sign = secs <0 ? "-" : "";
      secs = Math.abs(secs);
     double secDu = secs%60;
     int mins = (new Double(secs)).intValue()/60;
     int mnDu = mins%60;
     int hours = mins/60;
     int hourDu = hours%24;
     int dayDu = hours/24;
     
     return new Duration(sign + "P" + (dayDu != 0 ? (Integer.toString(dayDu) + "D") : "")
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
    public boolean lessThan(DateTimeObj dt)
    {
    	if (m_zone.equals(dt.m_zone))
    	{
    		if( m_date.before(dt.m_date))
    	/*
    	if ((m_year < dt.m_year || (m_year == 0 && dt.m_year == 0)) &&
    	(m_month < dt.m_month || (m_month == 0 && dt.m_month == 0)) &&
    	(m_day < dt.m_day || (m_day ==0 && dt.m_day == 0)) &&
    	(m_hour < dt.m_hour || (m_hour == 0 && dt.m_hour == 0)) &&
    	(m_minute < m_minute || (m_minute == 0 && m_minute == 0)) &&
    	(m_second < dt.m_second || (m_second == 0 && dt.m_second == 0)) )
    	*/
    	      return true;
    	    else 
    	      return false;
    	}
    	else
    	{
    		Date date1 = normalizeDate(m_date, m_zone);
    		Date date2 = normalizeDate(dt.m_date, dt.m_zone);
    		if (date1.before(date2))
    		return true;
    		else
    		return false;
    	}
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
    public boolean lessThanOrEqual(DateTimeObj dt)
    {
    	return (lessThan(dt) || equals(dt));
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
    public boolean greaterThan(DateTimeObj dt)
    {
    	return dt.lessThan(this);
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
    public boolean greaterThanOrEqual(DateTimeObj dt)
    {
    	return (greaterThan(dt) || equals(dt));
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
    public boolean equals(DateTimeObj dt)
    {
    	if (m_zone.equals(dt.m_zone))
    	{
    	   if (m_year == dt.m_year &&
    	    m_month == dt.m_month &&
    	    m_day == dt.m_day &&
    	    m_hour == dt.m_hour &&
    	    m_minute == dt.m_minute &&
    	    m_second == dt.m_second)
    	return true;
    	else 
    	return false;
    	}
    	else
    	{
    		Date date1 = normalizeDate(m_date, m_zone);
    		Date date2 = normalizeDate(dt.m_date, dt.m_zone);
    	   if (date1.before(date2) || date2.before(date1))
    	    return false;
    	   else 
    	    return true;
    	}
    }
    
    
    public DateTimeObj addDays(double days)
    throws TransformerException
    {
      Calendar cal = Calendar.getInstance();
      cal.setLenient(false);
      cal.setTime(m_date);
      cal.add(Calendar.DATE, new Double(days).intValue());
        
     DateTimeObj dateTime = date(cal.getTime());
     dateTime.setZone(m_zone);  
     return dateTime;
        
    }
    
    
    
    public DateTimeObj addYMDurationToDateTime(Duration d)
    throws TransformerException
    {
      Calendar cal = Calendar.getInstance();
      cal.setLenient(false);
      cal.setTime(m_date);
     int sign = (d.getSigned()) ? -1 : 1;
      cal.add(Calendar.YEAR, sign * (d.getYears()));
      cal.add(Calendar.MONTH, sign * (d.getMonths()));
        
     DateTimeObj dateTime = new DateTimeObj(cal.getTime(), dt2 ); 
     dateTime.setZone(m_zone);  
     return dateTime;
        
    }
    
    public DateTimeObj addYMDurationToDate(Duration d)
    throws TransformerException
    {
      Calendar cal = Calendar.getInstance();
      cal.setLenient(false);
      cal.setTime(m_date);
     int sign = (d.getSigned()) ? -1 : 1;
      cal.add(Calendar.YEAR, sign * (d.getYears()));
      cal.add(Calendar.MONTH, sign * (d.getMonths()));  
     DateTimeObj dt = date(cal.getTime()); 
      dt.setZone(m_zone);  
     return dt;
    }
    
    public DateTimeObj subtractYMDurationFromDateTime(Duration d)
    throws TransformerException
    {
    	Calendar cal = Calendar.getInstance();
      cal.setLenient(false);
      cal.setTime(m_date);
     int sign = (d.getSigned()) ? 1 : -1;
      cal.add(Calendar.MONTH, sign * (d.getMonths() + (d.getYears() * 12)));
        
     DateTimeObj dateTime = new DateTimeObj(cal.getTime(), dt1); 
     dateTime.setZone(m_zone);  
     return dateTime; 
        
    }
    
    public DateTimeObj subtractYMDurationFromDate(Duration d)
    throws TransformerException
    {
    	Calendar cal = Calendar.getInstance();
      cal.setLenient(false);
      cal.setTime(m_date);
     int sign = (d.getSigned()) ? 1 : -1;
      cal.add(Calendar.MONTH, sign * (d.getMonths() + (d.getYears() * 12)));
        
     DateTimeObj dt = date(cal.getTime()); 
      dt.setZone(m_zone);  
     return dt;
    }
    
    
    public DateTimeObj addDTDurationToDateTime(Duration d)
    throws TransformerException
    {
    	Calendar cal = Calendar.getInstance();
      cal.setLenient(false);
      cal.setTime(m_date);
     int sign = (d.getSigned()) ? -1 : 1;
     double secs = d.getSeconds() + (((((d.getDays() * 24) + d.getHours()) * 60) + d.getMinutes()) * 60);
     int intVal = new Double(secs).intValue();
      cal.add(Calendar.SECOND , sign * intVal);
      
      Date date = cal.getTime();
      SimpleDateFormat dateFormat = new SimpleDateFormat(dt2);
      dateFormat.setLenient(false);
      String dateTime = dateFormat.format(date);
      if (secs - intVal != 0)
      {
      String val = Double.toString(secs);
      int index = val.indexOf('.');
      dateTime = (index<0) ? dateTime : dateTime + val.substring(index); 
      }
      //DateTimeObj dt = new DateTimeObj(cal.getTime(), dt2); 
      DateTimeObj dt = new DateTimeObj(dateTime); 
      dt.setZone(m_zone);  
     return dt;
    }
    
    
    
    public DateTimeObj addDTDurationToDate(Duration d)
    throws TransformerException
    {
      Calendar cal = Calendar.getInstance();
      cal.setLenient(false);
      cal.setTime(m_date);
     int sign = (d.getSigned()) ? -1 : 1;
     cal.add(Calendar.MONTH, sign * (d.getMonths()));
      cal.add(Calendar.DATE, sign * (d.getDays()));
       
      DateTimeObj dt = date(cal.getTime()); 
      dt.setZone(m_zone);  
     return dt;
    }
    
        
    public DateTimeObj addDTDurationToTime(Duration d)
    throws TransformerException
    {
      Calendar cal = Calendar.getInstance();
      cal.setLenient(false);
      cal.setTime(m_date);
     int sign = (d.getSigned()) ? -1 : 1;
     double secs = d.getSeconds() + ((((d.getHours()) * 60) + d.getMinutes()) * 60);
     int intVal = new Double(secs).intValue();
      cal.add(Calendar.SECOND , sign * intVal);
      
      Date date = cal.getTime();
      SimpleDateFormat dateFormat = new SimpleDateFormat(t2);
      dateFormat.setLenient(false);
      String dateTime = dateFormat.format(date);
      if (secs - intVal != 0)
      {
      String val = Double.toString(secs);
      int index = val.indexOf('.');
      dateTime = (index<0) ? dateTime : dateTime + val.substring(index); 
      }
      DateTimeObj dt = time(dateTime); 
      dt.setZone(m_zone);  
     return dt;
    }
    
    
    public DateTimeObj subtractDTDurationFromDateTime(Duration d)
    throws TransformerException
    {
      Calendar cal = Calendar.getInstance();
      cal.setLenient(false);
      cal.setTime(m_date);
     int sign = (d.getSigned()) ? 1 : -1;
     double secs = d.getSeconds() + (((((d.getDays() * 24) + d.getHours()) * 60) + d.getMinutes()) * 60);
      cal.add(Calendar.SECOND , sign * (new Double (Math.floor(secs)).intValue()));
       
      DateTimeObj dt = new DateTimeObj(cal.getTime(), dt1); 
      dt.setZone(m_zone);  
     return dt;
    }
    	
    public DateTimeObj subtractDTDurationFromDate(Duration d)
    throws TransformerException
    {
    	
      Calendar cal = Calendar.getInstance();
      cal.setLenient(false);
      cal.setTime(m_date);
     int sign = (d.getSigned()) ? 1 : -1;
     // subtracting 1 from the number of days, because counting
     // from end of given day, so it counts for day 1  
      // cal.add(Calendar.DATE, sign * (d.getDays()-1));
      // "(date('2002-08-05')-duration('P42D'))+duration('P42D') = date('2002-08-05')"
      // doesn't work if you do the -1.  It sure feels like that is wrong.
      // -sb
     double secs = d.getSeconds() + (((((d.getDays() * 24) + d.getHours()) * 60) + d.getMinutes()) * 60);
      cal.add(Calendar.SECOND , sign * (new Double (Math.floor(secs)).intValue()));
       
      DateTimeObj dt = date(cal.getTime()); 
      dt.setZone(m_zone);  
     return dt;
    }
    
    /**
     * Method subtractDateFromDate.
     * @param date2
     * @return Duration
     * @throws TransformerException
     */
    public Duration subtractDateFromDate(DateTimeObj date2)
    throws TransformerException
    {
      
      /*Calendar cal = Calendar.getInstance();
      cal.setLenient(false);
      cal.setTime(m_date);
       
      int year = m_year - date2.m_year;
      int month = m_month - date2.m_month;
      int day = m_day - date2.m_day;
      int hour = m_hour - date2.m_hour;
      int minute = m_minute - date2.m_minute;
      double second = m_second - date2.m_second;
      
      Duration dur = new Duration(year, month, day, hour, minute, second);
       
     return dur;*/
     return getDTDuration(date2);
    }

    	
    	
    public DateTimeObj subtractDTDurationFromTime(Duration d)
    throws TransformerException
    {
    	Calendar cal = Calendar.getInstance();
      cal.setLenient(false);
      cal.setTime(m_date);
     int sign = (d.getSigned()) ? 1 : -1;
     double secs = d.getSeconds() + ((((d.getHours()) * 60) + d.getMinutes()) * 60);
      cal.add(Calendar.SECOND , sign * (new Double(Math.floor(secs)).intValue()));
       
      DateTimeObj dt = time(cal.getTime()); 
      dt.setZone(m_zone);  
     return dt;
    }
    
    public DateTimeObj addTZToDateTime(Duration tz) throws TransformerException
    {
    	if(tz == null)
    	{
    		if (m_zone.equals("z"))
    		return this;
    		Date date;
    		if (m_zone== null || m_zone.length()==0)
    		{
    			Calendar cal = Calendar.getInstance();
    			int offset = TimeZone.getDefault().getOffset(cal.get(Calendar.ERA), cal.get(Calendar.YEAR), 
    			  cal.get(Calendar.MONTH), cal.get(Calendar.DATE), cal.get(Calendar.DAY_OF_WEEK),Math.abs(TimeZone.getDefault().getRawOffset()));
    			int tZone = offset/(60*60*1000);    			
    			// ...but without daylight saving:
    			//int tZone = TimeZone.getDefault().getRawOffset()/(60*60*1000);
    			cal.setTime(m_date);
    			cal.add(Calendar.HOUR,-tZone);
    			//String dateTime = m_dateTime.substring(0,m_dateTime.indexOf("T"));
    			//dateTime = dateTime + formatDigits(hrs) + ":" + formatDigits(m_minute) + ":" + m_second;
    			date = cal.getTime();
    			DateTimeObj datetime = new DateTimeObj(date, dt2);
    			datetime.setZone("Z");
    			return datetime; 
    		}
    		else
    		{    			
    			Calendar cal = Calendar.getInstance();
    			int tZone = getHrsFromZone(m_zone);
    			cal.setTime(m_date);
    			cal.add(Calendar.HOUR,-tZone);
    			date = cal.getTime();
    			DateTimeObj datetime = new DateTimeObj(date, dt2);
    			datetime.setZone("Z");
    			return datetime;
    		}
    	}
    	else
    	{
    		Calendar cal = Calendar.getInstance();
    		int offset = TimeZone.getDefault().getOffset(cal.get(Calendar.ERA), cal.get(Calendar.YEAR), 
    		 cal.get(Calendar.MONTH), cal.get(Calendar.DATE), cal.get(Calendar.DAY_OF_WEEK),Math.abs(TimeZone.getDefault().getRawOffset()));
    		int tZone = tz.getSigned() ? tz.getHours() * -1 : tz.getHours();
    		// need to handle m_zone!!!
    		cal.setTime(m_date);
    		cal.add(Calendar.HOUR,-tZone);
    		if(m_zone != null && m_zone.length()>0)
    		{
    		  tZone = getHrsFromZone(m_zone);    		
    		  tZone = tZone - (offset/(60*60*1000));    		
    		  cal.add(Calendar.HOUR,-tZone);
    		}
    		//String dateTime = m_dateTime.substring(0,m_dateTime.indexOf("T"));
    		//dateTime = dateTime + formatDigits(hrs) + ":" + formatDigits(m_minute) + ":" + m_second;
    		Date date = cal.getTime();
    		DateTimeObj datetime = new DateTimeObj(date, dt2);
    		datetime.setZone("Z");
    		return datetime;
    	}
    }
    
    public DateTimeObj addTZToDate(Duration tz) throws TransformerException
    {
    	if(tz == null)
    	{
    		if (m_zone== null || m_zone.length()==0)
    		{
    			Calendar cal = Calendar.getInstance();
    			int offset = TimeZone.getDefault().getOffset(cal.get(Calendar.ERA), cal.get(Calendar.YEAR), 
    			  cal.get(Calendar.MONTH), cal.get(Calendar.DATE), cal.get(Calendar.DAY_OF_WEEK),Math.abs(TimeZone.getDefault().getRawOffset()));
    			int tZone = offset/(60*60*1000);
    			boolean signed = tZone < 0;
    			DateTimeObj dateTime = date(m_dateTime);
    			dateTime.setZone((signed?"-" : "") + formatDigits(tZone) + ":00");
    			return dateTime; 
    		}
    		else
    		return this;
    	}
    	else
    	{
    		int tZone = tz.getHours();
    		// need to handle m_zone!!!
    		DateTimeObj dateTime = date(m_dateTime);
    		dateTime.setZone((tz.getSigned() ? "-" + formatDigits(tZone) : formatDigits(tZone)) + ":00");
    		return dateTime; 
    	}
    }
    
    public DateTimeObj addTZToTime(Duration tz) throws TransformerException
    {
    	if(tz == null)
    	{
    		if (m_zone.equals("z"))
    		return this;
    		Date date;
    		if (m_zone== null || m_zone.length()==0)
    		{
    			Calendar cal = Calendar.getInstance();
    			int offset = TimeZone.getDefault().getOffset(cal.get(Calendar.ERA), cal.get(Calendar.YEAR), 
    			  cal.get(Calendar.MONTH), cal.get(Calendar.DATE), cal.get(Calendar.DAY_OF_WEEK),Math.abs(TimeZone.getDefault().getRawOffset()));
    			int tZone = offset/(60*60*1000);
    			cal.setTime(m_date);
    			cal.add(Calendar.HOUR,tZone);
    			//String dateTime = m_dateTime.substring(0,m_dateTime.indexOf("T"));
    			//dateTime = dateTime + formatDigits(hrs) + ":" + formatDigits(m_minute) + ":" + m_second;
    			date = cal.getTime();
    			DateTimeObj time = time(date);
    		    time.setZone("Z");
    		    return time; 
    		}
    		else
    		{
    			Calendar cal = Calendar.getInstance();
    			int tZone = getHrsFromZone(m_zone);
    			cal.setTime(m_date);
    			cal.add(Calendar.HOUR,-tZone);
    			date = cal.getTime();
    			DateTimeObj time = time(date);
    		    time.setZone("Z");
    		    return time;
    		}
    	}
    	else
    	{
    		Calendar cal = Calendar.getInstance();
    		int offset = TimeZone.getDefault().getOffset(cal.get(Calendar.ERA), cal.get(Calendar.YEAR), 
    		  cal.get(Calendar.MONTH), cal.get(Calendar.DATE), cal.get(Calendar.DAY_OF_WEEK),Math.abs(TimeZone.getDefault().getRawOffset()));
    		int tZone = tz.getSigned() ? tz.getHours() * -1 : tz.getHours();
    		// need to handle m_zone!!!
    		cal.setTime(m_date);
    		cal.add(Calendar.HOUR,-tZone);
    		if(m_zone != null && m_zone.length()>0)
    		{
    		  tZone = getHrsFromZone(m_zone);
    		  tZone = tZone - (offset/(60*60*1000));    		
    		  cal.add(Calendar.HOUR,-tZone);
    		}
    		//String dateTime = m_dateTime.substring(0,m_dateTime.indexOf("T"));
    		//dateTime = dateTime + formatDigits(hrs) + ":" + formatDigits(m_minute) + ":" + m_second;
    		Date date = cal.getTime();
    		DateTimeObj time = time(date);
    		time.setZone("Z");
    		return time;
    	}
    }
    
    public DateTimeObj removeTZFromDateTime(Duration tz) throws TransformerException
    {
    	if(tz == null)
    	{
    		if (m_zone.equals("z") || m_zone== null || m_zone.length()==0)
    		return this;
    		
    		Date date;
    		Calendar cal = Calendar.getInstance();
    		int offset = TimeZone.getDefault().getOffset(cal.get(Calendar.ERA), cal.get(Calendar.YEAR), 
    		cal.get(Calendar.MONTH), cal.get(Calendar.DATE), cal.get(Calendar.DAY_OF_WEEK),Math.abs(TimeZone.getDefault().getRawOffset()));
    	
    		int tZone = getHrsFromZone(m_zone);
    		tZone = (offset/(60*60*1000)) - tZone;
    		cal.setTime(m_date);
    		cal.add(Calendar.HOUR,tZone);
    		date = cal.getTime();
    		DateTimeObj datetime = new DateTimeObj(date, dt2);
    		datetime.setZone("Z");
    		return datetime;    		
    	}
    	else
    	{
    		Calendar cal = Calendar.getInstance();
    		int offset = TimeZone.getDefault().getOffset(cal.get(Calendar.ERA), cal.get(Calendar.YEAR), 
    			  cal.get(Calendar.MONTH), cal.get(Calendar.DATE), cal.get(Calendar.DAY_OF_WEEK),Math.abs(TimeZone.getDefault().getRawOffset()));
    			
    		int tZone = tz.getSigned() ? tz.getHours() * -1 : tz.getHours();
    		tZone = tZone - (offset/(60*60*1000));
    		// need to handle m_zone!!!
    		cal.setTime(m_date);
    		cal.add(Calendar.HOUR,tZone);
    		if(m_zone != null && m_zone.length()>0)
    		{
    		  tZone = getHrsFromZone(m_zone);
    		  tZone = (offset/(60*60*1000)) - tZone;    		
    		  cal.add(Calendar.HOUR,tZone);
    		}
    		//String dateTime = m_dateTime.substring(0,m_dateTime.indexOf("T"));
    		//dateTime = dateTime + formatDigits(hrs) + ":" + formatDigits(m_minute) + ":" + m_second;
    		Date date = cal.getTime();
    		DateTimeObj datetime = new DateTimeObj(date, dt2);
    		datetime.setZone("Z");
    		return datetime;
    	}
    }
    /* Removed from spec...
    public DateTimeObj removeTZFromDate(Duration tz) throws TransformerException
    {
    	return new DateTimeObj(m_dateTime);
    }
    */
    
    public DateTimeObj removeTZFromTime(Duration tz) throws TransformerException
    {
    	if(tz == null)
    	{
    		if (m_zone.equals("z") || m_zone== null || m_zone.length()==0)
    		return this;
    		
    		Date date;
    		Calendar cal = Calendar.getInstance();
    		int offset = TimeZone.getDefault().getOffset(cal.get(Calendar.ERA), cal.get(Calendar.YEAR), 
    		cal.get(Calendar.MONTH), cal.get(Calendar.DATE), cal.get(Calendar.DAY_OF_WEEK),Math.abs(TimeZone.getDefault().getRawOffset()));
    	
    		int tZone = getHrsFromZone(m_zone);
    		tZone = (offset/(60*60*1000)) - tZone;
    		cal.setTime(m_date);
    		cal.add(Calendar.HOUR,tZone);
    		date = cal.getTime();
    		DateTimeObj time = time(date);
    		time.setZone("Z");
    		return time;    		
    	}
    	else
    	{
    		Calendar cal = Calendar.getInstance();
    		int offset = TimeZone.getDefault().getOffset(cal.get(Calendar.ERA), cal.get(Calendar.YEAR), 
    			  cal.get(Calendar.MONTH), cal.get(Calendar.DATE), cal.get(Calendar.DAY_OF_WEEK),Math.abs(TimeZone.getDefault().getRawOffset()));
    			
    		int tZone = tz.getSigned() ? tz.getHours() * -1 : tz.getHours();
    		tZone = tZone - (offset/(60*60*1000));
    		// need to handle m_zone!!!
    		cal.setTime(m_date);
    		cal.add(Calendar.HOUR,tZone);
    		if(m_zone != null && m_zone.length()>0)
    		{
    		  tZone = getHrsFromZone(m_zone);
    		  tZone = (offset/(60*60*1000)) - tZone;    		
    		  cal.add(Calendar.HOUR,tZone);
    		}
    		//String dateTime = m_dateTime.substring(0,m_dateTime.indexOf("T"));
    		//dateTime = dateTime + formatDigits(hrs) + ":" + formatDigits(m_minute) + ":" + m_second;
    		Date date = cal.getTime();
    		DateTimeObj time = time(date);
    		time.setZone("Z");
    		return time;
    	}
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
    
    public void setZone(String zone)
    {
    	m_zone = zone;
    }
    
    public String getZone()
    {
    	if (m_zone.equals("Z") )
    	return ("00:00");
    	else
    	return m_zone;
    }
    
    public Date getDate()
    {
    	return m_date;
    }
    
    public String toString()
    {
    	return (m_dateTime + 
    	((m_zone != null && m_zone.length()>0 && m_hour == 0) ? "T" : "" )+ 
    	(m_zone == null ? "" : m_zone));
    }
    
    

}