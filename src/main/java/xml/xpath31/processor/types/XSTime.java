/*
 * This src code, is property of Apache Xerces-J project, and is made
 * available here for code development purposes.
 */
package xml.xpath31.processor.types;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.xml.transform.TransformerException;

import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.WrongNumberArgsException;
import org.apache.xpath.functions.datetime.FuncAdjustTimeToTimezone;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;

/**
 * An XML Schema data type representation, for the 
 * xs:time data type.
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
                                                                                   + "cannot be parsed to schema type 'time' value.");
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
     * Method definition, to determine whether this xs:time
     * value has a non-null timezone component.
     * 
     * @return                          Boolean value true or false
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
     * Method definition, to determine whether this xs:time
     * value is equal to the supplied xs:time value instance.
     * 
     * @param xsDate                    The supplied xs:time value instance
     * @return                          Boolean value true or false
     */
    public boolean equals(XSTime xsTime) {
        
    	boolean result = false;
        
        int hour1 = hour();
        int mins1 = minute();
        double secs1 = second();
        
        int hour2 = xsTime.hour();
        int mins2 = xsTime.minute();
        double secs2 = xsTime.second();
        XSDuration tz1 = getTimezone();
        XSDuration tz2 = xsTime.getTimezone();
        
        result = ((hour1 == hour2) && (mins1 == mins2) && (secs1 == secs2)) && 
                                                                            isTimezoneEqual(tz1, tz2, isPopulatedFromFnCurrentTime, 
                                                                                                     xsTime.isPopulatedFromFnCurrentTime());
        
        return result;
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
     * Method definition, to determine whether this xs:time
     * value is less than the supplied xs:time value instance.
     * 
     * @param xsDate               The supplied xs:time value instance
     * @return                     Boolean value true or false
     */
    public boolean lt(XSTime xsTime) {
       
    	boolean result = false;
       
       int hour1 = hour();
       int mins1 = minute();
       double secs1 = second();
       
       int hour2 = xsTime.hour();
       int mins2 = xsTime.minute();
       double secs2 = xsTime.second();
       
       if (hour1 < hour2) {
    	  result = true; 
       }
       else if (hour1 == hour2) {
          if (mins1 < mins2) {
        	 result = true;  
          }
          else if (mins1 == mins2) {
             if (secs1 < secs2) {
            	result = true;  
             }
          }
       }
    
       return result;
    }
    
    /**
     * Method definition, to determine whether this xs:time
     * value is greater than the supplied xs:time value instance.
     * 
     * @param xsDate               The supplied xs:time value instance
     * @return                     Boolean value true or false
     */
    public boolean gt(XSTime xsTime) {
    	
    	boolean result = false;
        
        int hour1 = hour();
        int mins1 = minute();
        double secs1 = second();
        
        int hour2 = xsTime.hour();
        int mins2 = xsTime.minute();
        double secs2 = xsTime.second();
        
        if (hour1 > hour2) {
        	result = true; 
        }
        else if (hour1 == hour2) {
           if (mins1 > mins2) {
        	   result = true;  
           }
           else if (mins1 == mins2) {
              if (secs1 > secs2) {
            	  result = true;  
              }
           }
        }
     
        return result;
    }
    
    /**
     * Method definition, to add supplied xs:dayTimeDuration
     * value to xs:time value, and get a new xs:time value instance.   
     * 
     * @param xObj								The supplied xs:dayTimeDuration value 
     * @return                                  An xs:time value instance                        
     * @throws TransformerException
     */
    public XObject add(XObject xObject) throws TransformerException {
         
    	 XObject result = null;
         
         if (!(xObject instanceof XSDayTimeDuration)) {
            throw new TransformerException("XPTY0004 : The value of schema type 'dayTimeDuration' is the only "
            		                                                                             + "one that may be added "
            		                                                                             + "to schema type 'time' value.");
         }
         
         XSDayTimeDuration xsDayTimeDuration = (XSDayTimeDuration)xObject;
         double secsVal = xsDayTimeDuration.value();
         
         Calendar cal1 = (Calendar)((getCalendar()).clone());
         cal1.setTimeInMillis(cal1.getTimeInMillis() + ((((long)secsVal * 1000))));
         
         result = new XSTime(cal1, getTimezone());
         
         return result;
    }
    
    /**
     * Method definition, to subtract supplied xs:time, xs:dayTimeDuration 
     * value from an xs:time value.
     * 
     * @param xObj                            The supplied xs:time, xs:dayTimeDuration
     *                                        value. 
     * @return                                An xs:dayTimeDuration, or xs:time value
     * @throws TransformerException
     */
     public XObject subtract(XObject xObj) throws TransformerException {
          
    	 XObject result = null;
          
          if (!((xObj instanceof XSTime) || (xObj instanceof XSDayTimeDuration))) {
             throw new TransformerException("XPTY0004 : The values of schema type 'time' and 'dayTimeDuration' "
             		                                                                         + "are the only ones that may be subtracted "
             		                                                                         + "from schema type 'time' value.");
          }
          
          if (xObj instanceof XSTime) {       	          	          	 
        	 XPathContext xctxt = new XPathContext();
        	 
        	 XSDuration tz = null;        	 
        	 boolean tzEqual = false;
        	 
        	 if (_timezoned) {
        		tz = _tz;
        		
        		XSTime xsTime = (XSTime)xObj;
        		XSDuration tz2 = xsTime.getTimezone();
        		
        		if (tz.equals(tz2)) {
        		   tzEqual = true;
        		}
        	 }
        	 else {
        		tz = xctxt.getTimezone();  
        	 }        	         		         		 

        	 try {
        		 if (!tzEqual) {
        			 FuncAdjustTimeToTimezone funcAdjustTimeToTimezone = new FuncAdjustTimeToTimezone();

        			 funcAdjustTimeToTimezone.setArg(xObj, 0);
        			 funcAdjustTimeToTimezone.setArg(tz, 1);

        			 xObj = funcAdjustTimeToTimezone.execute(xctxt);
        		 }

        		 Calendar cal1 = getCalendar();
        		 Calendar cal2 = ((XSTime)xObj).getCalendar();
        		 long diffDurationMilliSecs = cal1.getTimeInMillis() - cal2.getTimeInMillis();

        		 result = new XSDayTimeDuration(diffDurationMilliSecs / 1000);
        	 }
        	 catch (WrongNumberArgsException ex) {
        		 // No op 
        	 }        	 
          }          
          else if (xObj instanceof XSDayTimeDuration) {
             XSDayTimeDuration xsDayTimeDuration = (XSDayTimeDuration)xObj;
             double secsVal = xsDayTimeDuration.value();
             
             Calendar cal1 = (Calendar)((getCalendar()).clone());
             cal1.setTimeInMillis(cal1.getTimeInMillis() + ((((long)secsVal * 1000)) * -1));
             
             result = new XSTime(cal1, getTimezone());
          }
          
          return result;
    }
    
    public int getType() {
        return CLASS_XS_TIME;
    }

    /**
     * Method definition, to cast the supplied XSAnyType object value
     * to xs:time value. 
     * 
     * @param xsAnyType                         The supplied XSAnyType object value
     * @return                                  An xs:time value
     * @throws TransformerException
     */
    private XSTime castToTime(XSAnyType xsAnyType) throws TransformerException {        
        
    	XSTime result = null;
        
        if (xsAnyType instanceof XSTime) {
           result = (XSTime)xsAnyType;
        }        
        else if (xsAnyType instanceof XSDateTime) {
           XSDateTime xsDateTime = (XSDateTime)xsAnyType;
           result = new XSTime(xsDateTime.getCalendar(), xsDateTime.getTimezone());
        }
        else {
           result = parseTime(xsAnyType.stringValue());
        }
        
        return result;
    }
    
    public boolean isPopulatedFromFnCurrentTime() {
		return isPopulatedFromFnCurrentTime;
	}

	public void setPopulatedFromFnCurrentTime(boolean isPopulatedFromFnCurrentTime) {
		this.isPopulatedFromFnCurrentTime = isPopulatedFromFnCurrentTime;
	}

}
