package org.apache.xml.utils;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import javax.xml.transform.TransformerException;

import xml.xpath31.processor.types.XSDate;
import xml.xpath31.processor.types.XSDateTime;
import xml.xpath31.processor.types.XSTime;

/**
 * This class definition, specifies few utility methods to support 
 * XPath 3.1 data model xs:dateTime, xs:date and xs:time value 
 * comparisons.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class DateTimeUtil {
	
	/**
	 * Method definition, to get string value of supplied 
	 * xs:dateTime object value.
	 * 
	 * @param xsDateTime			The supplied xs:dateTime object value
	 * @return						String value of supplied xs:dateTime value
	 */
	public static String getXSDateTimeStrValue(XSDateTime xsDateTime) {
	   
	   String result = null;
	                                           
       String strValue = xsDateTime.stringValue();           
       
       Calendar calendar = xsDateTime.getCalendar();
       int milliSecs = calendar.get(Calendar.MILLISECOND);
       boolean ms1 = false;
       if ((milliSecs % 100) == 0) {
          milliSecs = (calendar.get(Calendar.MILLISECOND)) / 100;
          ms1 = true;
       }
       
       if (milliSecs == 0) {
    	   result = strValue; 
       }
       else {
    	   String milliSecStr = null;
    	   if (ms1) {
    		   milliSecStr = String.valueOf(milliSecs);  
    	   }
    	   else if (milliSecs < 10) {
    		   milliSecStr = "00" + milliSecs;   
    	   }
    	   else if (milliSecs < 100) {
    		   milliSecStr = "0" + milliSecs; 
    	   }
    	   else if (milliSecs > 100) {
    		   milliSecStr = String.valueOf(milliSecs);  
    	   }

    	   int idx = strValue.indexOf('T');
    	   String prefixStr = strValue.substring(0, idx);
    	   String suffixStr = strValue.substring(idx);
    	   int timeZoneIdx = suffixStr.indexOf('+');
    	   if (timeZoneIdx == -1) {
    		   timeZoneIdx = suffixStr.indexOf('-'); 
    	   }
    	   if (timeZoneIdx == -1) {
    		   timeZoneIdx = suffixStr.indexOf('Z'); 
    	   }        	  
    	   if (timeZoneIdx > -1) {
    		   String timeStr = suffixStr.substring(0, timeZoneIdx);
    		   timeStr = timeStr + "." + milliSecStr;
    		   String timeZoneStr = suffixStr.substring(timeZoneIdx);
    		   strValue = prefixStr + timeStr + timeZoneStr;  
    	   }
    	   else {
    		   suffixStr = suffixStr + "." + milliSecStr;
    		   strValue = prefixStr + suffixStr;  
    	   }

    	   result = strValue;
       }
	   
	   return result;
	}
	
	/**
	 * Method definition, to check whether two supplied xs:dateTime 
	 * values are equal.
	 * 
	 * @param dt1						One of the supplied xs:dateTime value
	 * @param dt2						The second supplied xs:dateTime value
	 * @return							Boolean value 'true', if the two supplied 
	 *                                  xs:dateTime values are equal, otherwise 'false'.
	 */
	public static boolean isEqual(XSDateTime dt1, XSDateTime dt2) {
	   
		boolean result = false;

		String str1 = dt1.stringValue();
		String str2 = dt2.stringValue();

		int timeZoneIdx = str1.indexOf('T');
		String str3 = str1.substring(timeZoneIdx);
		if (!(str3.contains("+") || str3.contains("-") || str3.endsWith("Z"))) {
		   str1 = str1 + "Z";
		}

		timeZoneIdx = str2.indexOf('T');
		str3 = str2.substring(timeZoneIdx);
		if (!(str3.contains("+") || str3.contains("-") || str3.endsWith("Z"))) {
		   // For comparison purpose, if xs:dateTime value is not in a timezone,
		   // then its assumed to be in UTC timezone.
		   str2 = str2 + "Z"; 
		}				
		
		int yearIdx = str1.indexOf('-');
		int yearValue = Integer.valueOf(str1.substring(0, yearIdx));
		OffsetDateTime odt1 = null;
		if (yearValue > 9999) {
		   String tempStr1 = "9999" + str1.substring(yearIdx);  	
		   odt1 = OffsetDateTime.parse(tempStr1, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
		   odt1 = odt1.plusYears(yearValue + (yearValue - 9999));
		}
		else {
		   odt1 = OffsetDateTime.parse(str1, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
		}
		
		Instant instant1 = odt1.toInstant();				
		
		yearIdx = str2.indexOf('-');
		yearValue = Integer.valueOf(str2.substring(0, yearIdx));
		OffsetDateTime odt2 = null;
		if (yearValue > 9999) {
		   String tempStr1 = "9999" + str2.substring(yearIdx);  	
		   odt2 = OffsetDateTime.parse(tempStr1, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
		   odt2 = odt1.plusYears(yearValue + (yearValue - 9999));
		}
		else {
		   odt2 = OffsetDateTime.parse(str2, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
		}
				
		Instant instant2 = odt2.toInstant();

		result = instant1.equals(instant2);

		return result;
	}

	/**
	 * Method definition, to check whether one supplied xs:dateTime value 
	 * occurs before another supplied xs:dateTime value.
	 * 
	 * @param dt1						One of the supplied xs:dateTime value
	 * @param dt2						The second supplied xs:dateTime value
	 * @return							Boolean value 'true', if the first xs:dateTime value 
	 *                                  occurs before the second xs:dateTime value, otherwise 
	 *                                  'false'.
	 */
	public static boolean isBefore(XSDateTime dt1, XSDateTime dt2) {
		
		boolean result = false;

		String str1 = dt1.stringValue();
		String str2 = dt2.stringValue();

		int timeZoneIdx = str1.indexOf('T');
		String str3 = str1.substring(timeZoneIdx);
		if (!(str3.contains("+") || str3.contains("-") || str3.endsWith("Z"))) {
		   str1 = str1 + "Z";
		}

		timeZoneIdx = str2.indexOf('T');
		str3 = str2.substring(timeZoneIdx);
		if (!(str3.contains("+") || str3.contains("-") || str3.endsWith("Z"))) {
		   // For comparison purpose, if xs:dateTime value is not in a timezone,
		   // then its assumed to be in UTC timezone.
		   str2 = str2 + "Z"; 
		}
		
		int yearIdx = str1.indexOf('-');
		int yearValue = Integer.valueOf(str1.substring(0, yearIdx));
		OffsetDateTime odt1 = null;
		if (yearValue > 9999) {
		   String tempStr1 = "9999" + str1.substring(yearIdx);  	
		   odt1 = OffsetDateTime.parse(tempStr1, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
		   odt1 = odt1.plusYears(yearValue + (yearValue - 9999));
		}
		else {
		   odt1 = OffsetDateTime.parse(str1, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
		}
		
		Instant instant1 = odt1.toInstant();				
		
		yearIdx = str2.indexOf('-');
		yearValue = Integer.valueOf(str2.substring(0, yearIdx));
		OffsetDateTime odt2 = null;
		if (yearValue > 9999) {
		   String tempStr1 = "9999" + str2.substring(yearIdx);  	
		   odt2 = OffsetDateTime.parse(tempStr1, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
		   odt2 = odt1.plusYears(yearValue + (yearValue - 9999));
		}
		else {
		   odt2 = OffsetDateTime.parse(str2, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
		}
				
		Instant instant2 = odt2.toInstant();

		result = instant1.isBefore(instant2);

		return result;
	}
	
	/**
	 * Method definition, to check whether one supplied xs:dateTime value 
	 * occurs after another supplied xs:dateTime value.
	 * 
	 * @param dt1						One of the supplied xs:dateTime value
	 * @param dt2						The second supplied xs:dateTime value
	 * @return							Boolean value 'true', if the first xs:dateTime value 
	 *                                  occurs after the second xs:dateTime value, otherwise 
	 *                                  'false'.
	 */
	public static boolean isAfter(XSDateTime dt1, XSDateTime dt2) {
		
		boolean result = false;

		String str1 = dt1.stringValue();
		String str2 = dt2.stringValue();

		int timeZoneIdx = str1.indexOf('T');
		String str3 = str1.substring(timeZoneIdx);
		if (!(str3.contains("+") || str3.contains("-") || str3.endsWith("Z"))) {
		   str1 = str1 + "Z";
		}

		timeZoneIdx = str2.indexOf('T');
		str3 = str2.substring(timeZoneIdx);
		if (!(str3.contains("+") || str3.contains("-") || str3.endsWith("Z"))) {
		   // For comparison purpose, if xs:dateTime value is not in a timezone,
		   // then its assumed to be in UTC timezone.
		   str2 = str2 + "Z"; 
		}
		
		int yearIdx = str1.indexOf('-');
		int yearValue = Integer.valueOf(str1.substring(0, yearIdx));
		OffsetDateTime odt1 = null;
		if (yearValue > 9999) {
		   String tempStr1 = "9999" + str1.substring(yearIdx);  	
		   odt1 = OffsetDateTime.parse(tempStr1, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
		   odt1 = odt1.plusYears(yearValue + (yearValue - 9999));
		}
		else {
		   odt1 = OffsetDateTime.parse(str1, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
		}
		
		Instant instant1 = odt1.toInstant();				
		
		yearIdx = str2.indexOf('-');
		yearValue = Integer.valueOf(str2.substring(0, yearIdx));
		OffsetDateTime odt2 = null;
		if (yearValue > 9999) {
		   String tempStr1 = "9999" + str2.substring(yearIdx);  	
		   odt2 = OffsetDateTime.parse(tempStr1, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
		   odt2 = odt1.plusYears(yearValue + (yearValue - 9999));
		}
		else {
		   odt2 = OffsetDateTime.parse(str2, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
		}
				
		Instant instant2 = odt2.toInstant();

		result = instant1.isAfter(instant2);

		return result;
	}
	
	/**
	 * Method definition, to check whether one supplied xs:dateTime value 
	 * is equal, or occurs before another supplied xs:dateTime value.
	 * 
	 * @param dt1						One of the supplied xs:dateTime value
	 * @param dt2						The second supplied xs:dateTime value
	 * @return							Boolean value 'true', if the requested
	 *                                  relational comparison is true between 
	 *                                  two supplied xs:dateTime values, otherwise 'false'.
	 */
	public static boolean lessThanOrEqual(XSDateTime dt1, XSDateTime dt2) {
		
		boolean result = false;

		String str1 = dt1.stringValue();
		String str2 = dt2.stringValue();

		int timeZoneIdx = str1.indexOf('T');
		String str3 = str1.substring(timeZoneIdx);
		if (!(str3.contains("+") || str3.contains("-") || str3.endsWith("Z"))) {
		   str1 = str1 + "Z";
		}

		timeZoneIdx = str2.indexOf('T');
		str3 = str2.substring(timeZoneIdx);
		if (!(str3.contains("+") || str3.contains("-") || str3.endsWith("Z"))) {
		   // For comparison purpose, if xs:dateTime value is not in a timezone,
		   // then its assumed to be in UTC timezone.
		   str2 = str2 + "Z"; 
		}
		
		int yearIdx = str1.indexOf('-');
		int yearValue = Integer.valueOf(str1.substring(0, yearIdx));
		OffsetDateTime odt1 = null;
		if (yearValue > 9999) {
		   String tempStr1 = "9999" + str1.substring(yearIdx);  	
		   odt1 = OffsetDateTime.parse(tempStr1, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
		   odt1 = odt1.plusYears(yearValue + (yearValue - 9999));
		}
		else {
		   odt1 = OffsetDateTime.parse(str1, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
		}
		
		Instant instant1 = odt1.toInstant();				
		
		yearIdx = str2.indexOf('-');
		yearValue = Integer.valueOf(str2.substring(0, yearIdx));
		OffsetDateTime odt2 = null;
		if (yearValue > 9999) {
		   String tempStr1 = "9999" + str2.substring(yearIdx);  	
		   odt2 = OffsetDateTime.parse(tempStr1, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
		   odt2 = odt1.plusYears(yearValue + (yearValue - 9999));
		}
		else {
		   odt2 = OffsetDateTime.parse(str2, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
		}
				
		Instant instant2 = odt2.toInstant();

		result = (instant1.isBefore(instant2) || instant1.equals(instant2));

		return result;
	}

	/**
	 * Method definition, to check whether two supplied xs:date 
	 * values are equal.
	 * 
	 * @param date1						One of the supplied xs:date value
	 * @param date2						The second supplied xs:date value
	 * @return							Boolean value 'true', if the two supplied 
	 *                                  xs:date values are equal, otherwise 'false'.
	 */
	public static boolean isEqual(XSDate date1, XSDate date2) throws TransformerException {
		
		boolean result = false;
		
		// For comparison purpose, if xs:date value is not in a timezone,
		// then its assumed to be in UTC timezone.
		
		String str1 = date1.stringValue();
		String[] str1Parts = str1.split("-");
		if (str1Parts.length == 3) {
		   String str3 = str1Parts[2];
		   if (!(str3.contains("+") || str3.endsWith("Z"))) {
			  str1 = str1 + "T00:00:00Z"; 
		   }
		   else {
			  String[] temp = str3.split("\\+|Z");
			  if (str3.contains("+")) {
				 str1 = str1Parts[0] + "-" + str1Parts[1] + "-" + temp[0] + "T00:00:00" + "+" + temp[1];  
			  }
			  else {
				 str1 = str1Parts[0] + "-" + str1Parts[1] + "-" + temp[0] + "T00:00:00Z"; 
			  }
		   }
		}
		else if (str1Parts.length == 4) {
		   String str3 = str1Parts[3];
		   str1 = str1Parts[0] + "-" + str1Parts[1] + "-" + str1Parts[2] + "T00:00:00" + "-"+ str3;
		}
		
		XSDateTime dt1 = XSDateTime.parseDateTime(str1); 
		
		String str2 = date2.stringValue();
		String[] str2Parts = str2.split("-");
		if (str2Parts.length == 3) {
		   String str3 = str2Parts[2];
		   if (!(str3.contains("+") || str3.endsWith("Z"))) {
			   str2 = str2 + "T00:00:00Z"; 
		   }
		   else {
			  String[] temp = str3.split("\\+|Z");
			  if (str3.contains("+")) {
				  str2 = str2Parts[0] + "-" + str2Parts[1] + "-" + temp[0] + "T00:00:00" + "+" + temp[1];  
			  }
			  else {
				  str2 = str2Parts[0] + "-" + str2Parts[1] + "-" + temp[0] + "T00:00:00Z"; 
			  }
		   }
		}
		else if (str2Parts.length == 4) {
		   String str3 = str2Parts[3];
		   str2 = str2Parts[0] + "-" + str2Parts[1] + "-" + str2Parts[2] + "T00:00:00" + "-"+ str3;
		}
		
		XSDateTime dt2 = XSDateTime.parseDateTime(str2);
		
		result = isEqual(dt1, dt2);
		
		return result;
	}
	
	/**
	 * Method definition, to check whether one supplied xs:date value 
	 * occurs before another supplied xs:date value.
	 * 
	 * @param date1						One of the supplied xs:date value
	 * @param date2						The second supplied xs:date value
	 * @return							Boolean value 'true', if the first xs:date value 
	 *                                  occurs before the second xs:date value, otherwise 
	 *                                  'false'.
	 */
    public static boolean isBefore(XSDate date1, XSDate date2) throws TransformerException {
		
		boolean result = false;
		
		// For comparison purpose, if xs:date value is not in a timezone,
		// then its assumed to be in UTC timezone.
		
		String str1 = date1.stringValue();
		String[] str1Parts = str1.split("-");
		if (str1Parts.length == 3) {
		   String str3 = str1Parts[2];
		   if (!(str3.contains("+") || str3.endsWith("Z"))) {
			  str1 = str1 + "T00:00:00Z"; 
		   }
		   else {
			  String[] temp = str3.split("\\+|Z");
			  if (str3.contains("+")) {
				 str1 = str1Parts[0] + "-" + str1Parts[1] + "-" + temp[0] + "T00:00:00" + "+" + temp[1];  
			  }
			  else {
				 str1 = str1Parts[0] + "-" + str1Parts[1] + "-" + temp[0] + "T00:00:00Z"; 
			  }
		   }
		}
		else if (str1Parts.length == 4) {
		   String str3 = str1Parts[3];
		   str1 = str1Parts[0] + "-" + str1Parts[1] + "-" + str1Parts[2] + "T00:00:00" + "-"+ str3;
		}
		
		XSDateTime dt1 = XSDateTime.parseDateTime(str1); 
		
		String str2 = date2.stringValue();
		String[] str2Parts = str2.split("-");
		if (str2Parts.length == 3) {
		   String str3 = str2Parts[2];
		   if (!(str3.contains("+") || str3.endsWith("Z"))) {
			   str2 = str2 + "T00:00:00Z"; 
		   }
		   else {
			  String[] temp = str3.split("\\+|Z");
			  if (str3.contains("+")) {
				  str2 = str2Parts[0] + "-" + str2Parts[1] + "-" + temp[0] + "T00:00:00" + "+" + temp[1];  
			  }
			  else {
				  str2 = str2Parts[0] + "-" + str2Parts[1] + "-" + temp[0] + "T00:00:00Z"; 
			  }
		   }
		}
		else if (str2Parts.length == 4) {
		   String str3 = str2Parts[3];
		   str2 = str2Parts[0] + "-" + str2Parts[1] + "-" + str2Parts[2] + "T00:00:00" + "-"+ str3;
		}
		
		XSDateTime dt2 = XSDateTime.parseDateTime(str2);
		
		result = isBefore(dt1, dt2);
		
		return result;
	}
    
    /**
	 * Method definition, to check whether one supplied xs:date value 
	 * occurs after another supplied xs:date value.
	 * 
	 * @param date1						One of the supplied xs:date value
	 * @param date2						The second supplied xs:date value
	 * @return							Boolean value 'true', if the first xs:date value 
	 *                                  occurs after the second xs:date value, otherwise 
	 *                                  'false'.
	 */
    public static boolean isAfter(XSDate date1, XSDate date2) throws TransformerException {
		
		boolean result = false;
		
		// For comparison purpose, if xs:date value is not in a timezone,
		// then its assumed to be in UTC timezone.
		
		String str1 = date1.stringValue();
		String[] str1Parts = str1.split("-");
		if (str1Parts.length == 3) {
		   String str3 = str1Parts[2];
		   if (!(str3.contains("+") || str3.endsWith("Z"))) {
			  str1 = str1 + "T00:00:00Z"; 
		   }
		   else {
			  String[] temp = str3.split("\\+|Z");
			  if (str3.contains("+")) {
				 str1 = str1Parts[0] + "-" + str1Parts[1] + "-" + temp[0] + "T00:00:00" + "+" + temp[1];  
			  }
			  else {
				 str1 = str1Parts[0] + "-" + str1Parts[1] + "-" + temp[0] + "T00:00:00Z"; 
			  }
		   }
		}
		else if (str1Parts.length == 4) {
		   String str3 = str1Parts[3];
		   str1 = str1Parts[0] + "-" + str1Parts[1] + "-" + str1Parts[2] + "T00:00:00" + "-"+ str3;
		}
		
		XSDateTime dt1 = XSDateTime.parseDateTime(str1); 
		
		String str2 = date2.stringValue();
		String[] str2Parts = str2.split("-");
		if (str2Parts.length == 3) {
		   String str3 = str2Parts[2];
		   if (!(str3.contains("+") || str3.endsWith("Z"))) {
			   str2 = str2 + "T00:00:00Z"; 
		   }
		   else {
			  String[] temp = str3.split("\\+|Z");
			  if (str3.contains("+")) {
				  str2 = str2Parts[0] + "-" + str2Parts[1] + "-" + temp[0] + "T00:00:00" + "+" + temp[1];  
			  }
			  else {
				  str2 = str2Parts[0] + "-" + str2Parts[1] + "-" + temp[0] + "T00:00:00Z"; 
			  }
		   }
		}
		else if (str2Parts.length == 4) {
		   String str3 = str2Parts[3];
		   str2 = str2Parts[0] + "-" + str2Parts[1] + "-" + str2Parts[2] + "T00:00:00" + "-"+ str3;
		}
		
		XSDateTime dt2 = XSDateTime.parseDateTime(str2);
		
		result = isAfter(dt1, dt2);
		
		return result;
	}
    
    /**
	 * Method definition, to check whether one supplied xs:date value 
	 * is equal, or occurs before another supplied xs:date value.
	 * 
	 * @param date1						One of the supplied xs:date value
	 * @param date2						The second supplied xs:date value
	 * @return							Boolean value 'true', if the requested
	 *                                  relational comparison is true between 
	 *                                  two supplied xs:date values, otherwise 'false'.
	 */
    public static boolean lessThanOrEqual(XSDate date1, XSDate date2) throws TransformerException {
        
    	boolean result = false;
    	
    	// For comparison purpose, if xs:date value is not in a timezone,
    	// then its assumed to be in UTC timezone.
		
		String str1 = date1.stringValue();
		String[] str1Parts = str1.split("-");
		if (str1Parts.length == 3) {
		   String str3 = str1Parts[2];
		   if (!(str3.contains("+") || str3.endsWith("Z"))) {
			  str1 = str1 + "T00:00:00Z"; 
		   }
		   else {
			  String[] temp = str3.split("\\+|Z");
			  if (str3.contains("+")) {
				 str1 = str1Parts[0] + "-" + str1Parts[1] + "-" + temp[0] + "T00:00:00" + "+" + temp[1];  
			  }
			  else {
				 str1 = str1Parts[0] + "-" + str1Parts[1] + "-" + temp[0] + "T00:00:00Z"; 
			  }
		   }
		}
		else if (str1Parts.length == 4) {
		   String str3 = str1Parts[3];
		   str1 = str1Parts[0] + "-" + str1Parts[1] + "-" + str1Parts[2] + "T00:00:00" + "-"+ str3;
		}
		
		XSDateTime dt1 = XSDateTime.parseDateTime(str1); 
		
		String str2 = date2.stringValue();
		String[] str2Parts = str2.split("-");
		if (str2Parts.length == 3) {
		   String str3 = str2Parts[2];
		   if (!(str3.contains("+") || str3.endsWith("Z"))) {
			   str2 = str2 + "T00:00:00Z"; 
		   }
		   else {
			  String[] temp = str3.split("\\+|Z");
			  if (str3.contains("+")) {
				  str2 = str2Parts[0] + "-" + str2Parts[1] + "-" + temp[0] + "T00:00:00" + "+" + temp[1];  
			  }
			  else {
				  str2 = str2Parts[0] + "-" + str2Parts[1] + "-" + temp[0] + "T00:00:00Z"; 
			  }
		   }
		}
		else if (str2Parts.length == 4) {
		   String str3 = str2Parts[3];
		   str2 = str2Parts[0] + "-" + str2Parts[1] + "-" + str2Parts[2] + "T00:00:00" + "-"+ str3;
		}
		
		XSDateTime dt2 = XSDateTime.parseDateTime(str2);
		
		result = lessThanOrEqual(dt1, dt2);
		
		return result;
		
	}
    
    /**
	 * Method definition, to check whether two supplied xs:time values 
	 * are equal.
	 * 
	 * @param time1						One of the supplied xs:time value
	 * @param time2						The second supplied xs:time value
	 * @return							Boolean value 'true', if the two supplied 
	 *                                  xs:time values are equal, otherwise 'false'.
	 */
    public static boolean isEqual(XSTime time1, XSTime time2) throws TransformerException {
    	
    	boolean result = false;
    	
    	String str1 = time1.stringValue();
    	if (!(str1.contains("+") || str1.contains("-") || str1.endsWith("Z"))) {
    	   str1 = str1 + "Z"; 
 		}
    	
    	// Prefixing with a fixed implementation chosen literal date
    	str1 = "2000-01-01T" + str1;
    	XSDateTime dt1 = XSDateTime.parseDateTime(str1);
    	
    	String str2 = time2.stringValue();
    	if (!(str2.contains("+") || str2.contains("-") || str2.endsWith("Z"))) {
    	   str2 = str2 + "Z"; 
  		}
    	
    	// Prefixing with a fixed implementation chosen literal date
    	str2 = "2000-01-01T" + str2;
    	XSDateTime dt2 = XSDateTime.parseDateTime(str2);
    	
    	result = isEqual(dt1, dt2);
    	
    	return result;
    }
    
    /**
	 * Method definition, to check whether one supplied xs:time value 
	 * occurs before another supplied xs:time value.
	 * 
	 * @param time1						One of the supplied xs:time value
	 * @param time2						The second supplied xs:time value
	 * @return							Boolean value 'true', if the first xs:time value 
	 *                                  occurs before the second xs:time value, otherwise 
	 *                                  'false'.
	 */
    public static boolean isBefore(XSTime time1, XSTime time2) throws TransformerException {
    	
    	boolean result = false;
    	
    	String str1 = time1.stringValue();
    	if (!(str1.contains("+") || str1.contains("-") || str1.endsWith("Z"))) {
    	   str1 = str1 + "Z"; 
 		}
    	
    	// Prefixing with a fixed implementation chosen literal date
    	str1 = "2000-01-01T" + str1;
    	XSDateTime dt1 = XSDateTime.parseDateTime(str1);
    	
    	String str2 = time2.stringValue();
    	if (!(str2.contains("+") || str2.contains("-") || str2.endsWith("Z"))) {
    	   str2 = str2 + "Z"; 
  		}
    	
    	// Prefixing with a fixed implementation chosen literal date
    	str2 = "2000-01-01T" + str2;
    	XSDateTime dt2 = XSDateTime.parseDateTime(str2);
    	
    	result = isBefore(dt1, dt2);
    	
    	return result;
    }
    
    /**
	 * Method definition, to check whether one supplied xs:time value occurs 
	 * after another supplied xs:time value.
	 * 
	 * @param time1						One of the supplied xs:time value
	 * @param time2						The second supplied xs:time value
	 * @return							Boolean value 'true', if the first xs:time value 
	 *                                  occurs after the second xs:time value, otherwise 
	 *                                  'false'.
	 */
    public static boolean isAfter(XSTime time1, XSTime time2) throws TransformerException {
    	
    	boolean result = false;
    	
    	String str1 = time1.stringValue();
    	if (!(str1.contains("+") || str1.contains("-") || str1.endsWith("Z"))) {
    	   str1 = str1 + "Z"; 
 		}
    	
    	// Prefixing with a fixed implementation chosen literal date
    	str1 = "2000-01-01T" + str1;
    	XSDateTime dt1 = XSDateTime.parseDateTime(str1);
    	
    	String str2 = time2.stringValue();
    	if (!(str2.contains("+") || str2.contains("-") || str2.endsWith("Z"))) {
    	   str2 = str2 + "Z"; 
  		}
    	
    	// Prefixing with a fixed implementation chosen literal date
    	str2 = "2000-01-01T" + str2;
    	XSDateTime dt2 = XSDateTime.parseDateTime(str2);
    	
    	result = isAfter(dt1, dt2);
    	
    	return result;
    }
    
    /**
	 * Method definition, to check whether one supplied xs:time value 
	 * is equal, or occurs before another supplied xs:time value.
	 * 
	 * @param time1						One of the supplied xs:time value
	 * @param time2						The second supplied xs:time value
	 * @return							Boolean value 'true', if the requested
	 *                                  relational comparison is true between 
	 *                                  two supplied xs:time values, otherwise 'false'.
	 */
    public static boolean lessThanOrEqual(XSTime time1, XSTime time2) throws TransformerException {
        
    	boolean result = false;
    	
    	String str1 = time1.stringValue();
    	if (!(str1.contains("+") || str1.contains("-") || str1.endsWith("Z"))) {
    	   str1 = str1 + "Z"; 
 		}
    	
    	// Prefixing with a fixed implementation chosen literal date
    	str1 = "2000-01-01T" + str1;
    	XSDateTime dt1 = XSDateTime.parseDateTime(str1);
    	
    	String str2 = time2.stringValue();
    	if (!(str2.contains("+") || str2.contains("-") || str2.endsWith("Z"))) {
    	   str2 = str2 + "Z"; 
  		}
    	
    	// Prefixing with a fixed implementation chosen literal date
    	str2 = "2000-01-01T" + str2;
    	XSDateTime dt2 = XSDateTime.parseDateTime(str2);
    	
    	result = lessThanOrEqual(dt1, dt2);
    	
    	return result;
    }

}
