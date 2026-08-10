/*
 * This src code, is property of Apache Xerces-J project, and is made
 * available here for code development purposes.
 */
package xml.xpath31.processor.types;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.transform.TransformerException;

import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;

/**
 * An XML Schema data type representation, for the 
 * xs:date data type.
 */
public class XSDate extends XSCalendarType {

    private static final long serialVersionUID = -9204442487368342326L;
    
    private static final String XS_DATE = "xs:date";
    
    private Calendar _calendar;
    
    private boolean _timezoned;
    
    private XSDuration _tz;
    
    /**
     * The value of this class field, stores the fact that whether this XSDate
     * object is constructed via XPath function call fn:current-date().
     */
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

    /**
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
                    throw new TransformerException("XTTE0570 : The supplied string value '" + strVal + "' cannot be parsed to "
                                                                                         		     + "schema type 'date' value."); 
                }
                idx = strVal.indexOf('-', idx + 1);
                if (idx == -1) {
                    throw new TransformerException("XTTE0570 : The supplied string value '" + strVal + "' cannot be parsed to "
                                                                                         		     + "schema type 'date' value.");
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
                result = new XSDate(dateTime.getCalendar(), dateTime.getTimezone());
            }
            else {
                throw new TransformerException("XTTE0570 : The supplied string value '" + strVal + "' cannot be parsed to "
                																				 + "schema type 'date' value."); 
            }
        }
        catch (TransformerException ex) {
           throw ex;  
        }
        catch (Exception ex) {
            throw new TransformerException("XTTE0570 : The supplied string value '" + strVal + "' cannot be parsed to "
            																				 + "schema type 'date' value."); 
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
     * Get the year from the date stored.
     * 
     * @return   the year value of the date stored
     */
    public int year() {
	   int year = _calendar.get(Calendar.YEAR);
	   if (_calendar.get(Calendar.ERA) == GregorianCalendar.BC) {
		  year *= -1;
	   }

	   return year;
	}
    
    /**
     * Get the month from the date stored.
     * 
     * @return   the month value of the date stored
     */
    public int month() {
       return _calendar.get(Calendar.MONTH) + 1;
    }
    
    /**
	 * Get the day from the date stored.
	 * 
	 * @return   the day value of the date stored
	 */
	public int day() {
	   return _calendar.get(Calendar.DAY_OF_MONTH);
	}
    
	/**
     * Method definition, to determine whether this xs:date
     * value has a non-null timezone component.
     * 
     * @return                          Boolean value true or false
     */
    public boolean isDateTimezoned() {
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

        if (isDateTimezoned()) {
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
    
    /**
     * Method definition, to determine whether this xs:date
     * value is equal to the supplied xs:date value instance.
     * 
     * @param xsDate                    The supplied xs:date value instance
     * @return                          Boolean value true or false
     */
    public boolean equals(XSDate xsDate) {
        
    	boolean result = false;
        
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
        
        result = ((year1 == year2) && (month1 == month2) && (date1 == date2)) && 
                                                           isTimezoneEqual(tz1, tz2, isPopulatedFromFnCurrentDate, 
                                                                                     xsDate.isPopulatedFromFnCurrentDate()); 
        
        return result; 
    }
    
    @Override
    public boolean equals(Object obj) {
       boolean isDateEqual = false;
        
       if (obj instanceof XSDate) {
          isDateEqual = this.equals((XSDate)obj);  
       }
       
       return isDateEqual;
    }
    
    @Override
    public int hashCode() {       
       String strVal = stringValue();       
       
       return strVal.hashCode();
    }
    
    /**
     * Method definition, to determine whether this xs:date
     * value is less than the supplied xs:date value instance.
     * 
     * @param xsDate               The supplied xs:date value instance
     * @return                     Boolean value true or false
     */
    public boolean lt(XSDate xsDate) {
        
    	boolean result = false;
        
        Calendar cal1 = getCalendar();
        Calendar cal2 = xsDate.getCalendar();
        
        Date date1 = new Date(cal1.get(Calendar.YEAR), cal1.get(Calendar.MONTH), 
                                                                     cal1.get(Calendar.DATE));
        Date date2 = new Date(cal2.get(Calendar.YEAR), cal2.get(Calendar.MONTH), 
                                                                     cal2.get(Calendar.DATE));
        
        result = date1.before(date2); 
        
        return result;
    }
    
    /**
     * Method definition, to determine whether this xs:date
     * value is greater than the supplied xs:date value instance.
     * 
     * @param xsDate               The supplied xs:date value instance
     * @return                     Boolean value true or false
     */
    public boolean gt(XSDate xsDate) {
        
    	boolean result = false;
        
        Calendar cal1 = getCalendar();
        Calendar cal2 = xsDate.getCalendar();                
        
        Date date1 = new Date(cal1.get(Calendar.YEAR), cal1.get(Calendar.MONTH), 
                                                                      cal1.get(Calendar.DATE));
        Date date2 = new Date(cal2.get(Calendar.YEAR), cal2.get(Calendar.MONTH), 
                                                                      cal2.get(Calendar.DATE));
        
        result = date1.after(date2); 
        
        return result; 
    }
       
    /**
     * Method definition, to add supplied xs:yearMonthDuration, xs:dayTimeDuration
     * value to xs:date value, and get a new xs:date value instance.   
     * 
     * @param xObj								The supplied xs:yearMonthDuration, or 
     *                                          xs:dayTimeDuration value. 
     * @return                                  An xs:date value instance                        
     * @throws TransformerException
     */
    public XObject add(XObject xObj) throws TransformerException {
        
    	XObject result = null;
        
        if (!((xObj instanceof XSYearMonthDuration) || (xObj instanceof XSDayTimeDuration))) {
           throw new TransformerException("XPTY0004 : The values of schema types 'yearMonthDuration', and "
                                                                                 + "'dayTimeDuration' are the only "
                                                                                 + "ones that may be added to schema type 'date' value.");
        }
        
        if (xObj instanceof XSYearMonthDuration) {
           XSYearMonthDuration xsYearMonthDuration = (XSYearMonthDuration)xObj;
           
           Calendar cal1 = (Calendar)((getCalendar()).clone());
           cal1.add(Calendar.MONTH, xsYearMonthDuration.monthValue());
           
           result = new XSDate(cal1, getTimezone());
        }
        else if (xObj instanceof XSDayTimeDuration) {
           XSDayTimeDuration xsDayTimeDuration = (XSDayTimeDuration)xObj;           
           double secsDbl = xsDayTimeDuration.value();
           
           Calendar cal1 = (Calendar)((getCalendar()).clone());
           cal1.setTimeInMillis(cal1.getTimeInMillis() + ((((long)secsDbl * 1000))));
           
           result = new XSDate(cal1, getTimezone());
        }
        
        return result;
    }
    
    /**
     * Method definition, to subtract supplied xs:date, xs:yearMonthDuration, 
     * xs:dayTimeDuration value from an xs:date value.
     * 
     * @param xObj                           The supplied xs:date, xs:yearMonthDuration, or 
     *                                       xs:dayTimeDuration value. 
     * @return                               An xs:dayTimeDuration, or xs:date value
     * @throws TransformerException
     */
    public XObject subtract(XObject xObj) throws TransformerException {
        
    	XObject result = null;
        
        if (!((xObj instanceof XSDate) || (xObj instanceof XSYearMonthDuration)
                                       || (xObj instanceof XSDayTimeDuration))) {
           throw new TransformerException("XPTY0004 : The values of schema types 'date', 'yearMonthDuration' and "
                                                                                 + "'dayTimeDuration' are the only ones that "
                                                                                 + "may be subtracted from schema type 'date' value.");
        }
        
        if (xObj instanceof XSDate) {                   	        	        	        	
        	String str1 = stringValue();
        	
        	String dateTimeStr1 = getXsDateTimeStrFromXsDateStr(str1);
        	XSDateTime xsDateTime1 = XSDateTime.parseDateTime(dateTimeStr1);
        	
        	XSDate xsDate = (XSDate)xObj;
        	String str2 = xsDate.stringValue();
        	String dateTimeStr2 = getXsDateTimeStrFromXsDateStr(str2);
        	XSDateTime xsDateTime2 = XSDateTime.parseDateTime(dateTimeStr2);
        	
        	result = xsDateTime1.subtract(xsDateTime2);
        }
        else if (xObj instanceof XSYearMonthDuration) {
           XSYearMonthDuration xsYearMonthDuration = (XSYearMonthDuration)xObj;           
           
           Calendar cal1 = (Calendar)((getCalendar()).clone());
           cal1.add(Calendar.MONTH, xsYearMonthDuration.monthValue() * -1);
           
           result = new XSDate(cal1, getTimezone());
        }
        else if (xObj instanceof XSDayTimeDuration) {
           XSDayTimeDuration xsDayTimeDuration = (XSDayTimeDuration)xObj;
           double secsValue = xsDayTimeDuration.value();
           
           Calendar cal1 = (Calendar)((getCalendar()).clone());
           cal1.setTimeInMillis(cal1.getTimeInMillis() + ((((long)secsValue * 1000)) * -1));
           
           result = new XSDate(cal1, getTimezone());
        }
        
        return result;
    }
    
    public int getType() {
        return CLASS_XS_DATE;
    }
    
    /**
     * Method definition, to cast the supplied XSAnyType object value
     * to xs:date value. 
     * 
     * @param xsAnyType                         The supplied XSAnyType object value
     * @return                                  An xs:date value
     * @throws TransformerException
     */
    private XSDate castToDate(XSAnyType xsAnyType) throws TransformerException {
        
    	if (xsAnyType instanceof XSDate) {
            XSDate xsDate = (XSDate)xsAnyType;
            
            return new XSDate(xsDate.getCalendar(), xsDate.getTimezone());
        }

        if (xsAnyType instanceof XSDateTime) {
            XSDateTime xsDateTime = (XSDateTime)xsAnyType;
            
            return new XSDate(xsDateTime.getCalendar(), xsDateTime.getTimezone());
        }

        return parseDate(xsAnyType.stringValue());
    }
    
    public boolean isPopulatedFromFnCurrentDate() {
        return isPopulatedFromFnCurrentDate;
    }

    public void setPopulatedFromFnCurrentDate(boolean isPopulatedFromFnCurrentDate) {
        this.isPopulatedFromFnCurrentDate = isPopulatedFromFnCurrentDate;
    }
    
    /**
     * Method definition, to get xs:dateTime string, from
     * supplied xs:date string, by having a cosmetic time infix 
     * string T00:00:00 within the returned string value. 
     * 
     * @param xsDateStr                 The supplied xs:date string
     *                                  value.
     * @return                          The xs:dateTime string
     */
    private String getXsDateTimeStrFromXsDateStr(String xsDateStr) {
		
		String result = null;
		
		if (xsDateStr.contains("+")) {
		   int idx = xsDateStr.indexOf('+');
		   String dateStr1 = xsDateStr.substring(0, idx);
		   
		   result = dateStr1 + "T00:00:00" + xsDateStr.substring(idx);  
		}
		else if (xsDateStr.endsWith("Z")) {
		   String dateStr1 = xsDateStr.substring(0, xsDateStr.length() - 1);
		   
		   result = dateStr1 + "T00:00:00Z";
		}
		else {
		   String[] strArray = xsDateStr.split("\\-");
		   
		   int length1 = strArray.length;
		   
		   if (length1 == 3) {
			  result = xsDateStr + "T00:00:00"; 
		   }
		   else {
			  result = (strArray[0] + "-" + strArray[1] + "-" + strArray[2]  + "T00:00:00" + "-" + strArray[3]);  
		   }
		}
		
		return result;
	}

}
