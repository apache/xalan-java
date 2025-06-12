package org.apache.xpath.types;

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.TransformerException;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;

import xml.xpath31.processor.types.XSAnyAtomicType;

/**
 * Implementation of XML Schema data type xs:gMonthDay.
 * 
 * @author : Mukul Gandhi <mukulg@apache.org>
 */
public class XSGMonthDay extends XSAnyAtomicType {

	private static final long serialVersionUID = -3463438692413910274L;

	/**
	 * Class fields to represent components of XML Schema type xs:gMonthDay.  
	 */
	
	private int m_month;
	
	private int m_day;
	
    private boolean m_isTimeZoneNegative;
	
	private boolean m_isTimeZoneUtc;
	
	private int m_timezone_hrs;
	
	private int m_timezone_min;
	
	private boolean m_isTimeZoned;
	
	private String m_gMonthDayStrValue;
	
	private static final String XS_GMONTH_DAY = "xs:gMonthDay";
	
	/**
	 * Default constructor.
	 */
	public XSGMonthDay() {
		// NO OP
	}
	
	/**
	 * Class constructor.
	 */
	public XSGMonthDay(String gMonthDayStrValue) throws TransformerException {		
		parse(gMonthDayStrValue);
		m_gMonthDayStrValue = gMonthDayStrValue; 
	}

	@Override
	public String stringType() {
		return XS_GMONTH_DAY;
	}
	
	public ResultSequence constructor(ResultSequence seq) {
		ResultSequence result = new ResultSequence();
		
		XObject xObj = seq.item(0);
		String strVal = XslTransformEvaluationHelper.getStrVal(xObj);
		try {
			XSGMonthDay xsGMonthDay = new XSGMonthDay(strVal);
			result.add(xsGMonthDay);
		} catch (TransformerException ex) {
			// NO OP
		}
		
		return result;
	}

	@Override
	public String stringValue() {
		return m_gMonthDayStrValue;
	}
	
	public int getType() {
		return CLASS_GMONTHDAY;
	}
	
	/**
	 * Implementation of operation equals, for the type xs:gMonthDay.
	 */
	public boolean eq(XSGMonthDay obj2) {
		boolean result = false;
		
		result = ((m_month == obj2.getMonth()) && (m_day == obj2.getDay()) && (m_isTimeZoneNegative == 
				   obj2.isTimeZoneNegative()) && (m_isTimeZoneUtc == obj2.isTimeZoneUtc()) && 
				   (m_timezone_hrs == obj2.getTimezoneHrs()) && (m_timezone_min == obj2.getTimezoneMin()) && 
				   (m_isTimeZoned == obj2.isTimeZoned()));
		
		return result;
	}
	
	/**
	 * Implementation of operation not equals, for the type xs:gMonthDay.
	 */
	public boolean ne(XSGMonthDay obj2) {
        boolean result = false;
		
		result = !eq(obj2);
		
		return result;
	}
	
	/**
	 * Method definition to check whether, the provided string values of month
	 * and day are valid combinations. 
	 */
	private boolean isMonthAndDayValueConsistent(String monthStrValue, String dayStrValue) {
		
		boolean result = false;
		
		Integer monthIntValue = Integer.valueOf(monthStrValue);
		Integer dayIntValue = Integer.valueOf(dayStrValue);
		
		List<MonthAndNoOfDaysValuesPair> listOfValidObjects = getValidMonthAndDaysPairs();
		
		result = listOfValidObjects.stream().filter(obj -> obj.getMonthValue().equals(monthIntValue) && 
				                                           obj.getNoOfDaysInMonth().equals(dayIntValue)).findFirst().isPresent();
		
		return result;
	}
	
	/**
	 * Method definition to construct, a list of 'valid month' and 'no of 
	 * days in that month' pairs.
	 */
	private List<MonthAndNoOfDaysValuesPair> getValidMonthAndDaysPairs() {
		List<MonthAndNoOfDaysValuesPair> listOfValidObjects = new ArrayList<MonthAndNoOfDaysValuesPair>();
		
		for (int idx = 1; idx <= 29; idx++) {
			listOfValidObjects.add(new MonthAndNoOfDaysValuesPair(1, idx));
			listOfValidObjects.add(new MonthAndNoOfDaysValuesPair(2, idx));
			listOfValidObjects.add(new MonthAndNoOfDaysValuesPair(3, idx));
			listOfValidObjects.add(new MonthAndNoOfDaysValuesPair(4, idx));
			listOfValidObjects.add(new MonthAndNoOfDaysValuesPair(5, idx));
			listOfValidObjects.add(new MonthAndNoOfDaysValuesPair(6, idx));
			listOfValidObjects.add(new MonthAndNoOfDaysValuesPair(7, idx));
			listOfValidObjects.add(new MonthAndNoOfDaysValuesPair(8, idx));
			listOfValidObjects.add(new MonthAndNoOfDaysValuesPair(9, idx));
			listOfValidObjects.add(new MonthAndNoOfDaysValuesPair(10, idx));
			listOfValidObjects.add(new MonthAndNoOfDaysValuesPair(11, idx));
			listOfValidObjects.add(new MonthAndNoOfDaysValuesPair(12, idx));
		}


		listOfValidObjects.add(new MonthAndNoOfDaysValuesPair(1, 30));
		listOfValidObjects.add(new MonthAndNoOfDaysValuesPair(3, 30));
		listOfValidObjects.add(new MonthAndNoOfDaysValuesPair(4, 30));
		listOfValidObjects.add(new MonthAndNoOfDaysValuesPair(5, 30));
		listOfValidObjects.add(new MonthAndNoOfDaysValuesPair(6, 30));
		listOfValidObjects.add(new MonthAndNoOfDaysValuesPair(7, 30));
		listOfValidObjects.add(new MonthAndNoOfDaysValuesPair(8, 30));
		listOfValidObjects.add(new MonthAndNoOfDaysValuesPair(9, 30));
		listOfValidObjects.add(new MonthAndNoOfDaysValuesPair(10, 30));
		listOfValidObjects.add(new MonthAndNoOfDaysValuesPair(11, 30));
		listOfValidObjects.add(new MonthAndNoOfDaysValuesPair(12, 30));
		
		listOfValidObjects.add(new MonthAndNoOfDaysValuesPair(1, 31));
		listOfValidObjects.add(new MonthAndNoOfDaysValuesPair(3, 31));
		listOfValidObjects.add(new MonthAndNoOfDaysValuesPair(5, 31));
		listOfValidObjects.add(new MonthAndNoOfDaysValuesPair(7, 31));
		listOfValidObjects.add(new MonthAndNoOfDaysValuesPair(8, 31));
		listOfValidObjects.add(new MonthAndNoOfDaysValuesPair(10, 31));
		listOfValidObjects.add(new MonthAndNoOfDaysValuesPair(12, 31));
		
		return listOfValidObjects;
	}
	
	/**
	 * An object of this class, contains a pair of month and days 
	 * values for checking existence of a (month + days) object within 
	 * a list;
	 */
	class MonthAndNoOfDaysValuesPair {
		
		private Integer monthValue;
		
		private Integer noOfDaysInMonth;
		
		public MonthAndNoOfDaysValuesPair(Integer monthValue, Integer noOfDaysInMonth) {
			this.monthValue = monthValue;
			this.noOfDaysInMonth = noOfDaysInMonth;
		}

		public Integer getMonthValue() {
			return monthValue;
		}

		public void setMonthValue(Integer monthValue) {
			this.monthValue = monthValue;
		}

		public Integer getNoOfDaysInMonth() {
			return noOfDaysInMonth;
		}

		public void setNoOfDaysInMonth(Integer noOfDaysInMonth) {
			this.noOfDaysInMonth = noOfDaysInMonth;
		}
	}

	@Override
	public String typeName() {
		return "gMonthDay";
	}
	
	/**
	 * Getter method definitions for various components of an XML Schema type xs:gMonthDay. 
	 */
	
	public int getMonth() {
		return m_month;
	}
	
	public int getDay() {
		return m_day;
	}

	public boolean isTimeZoneNegative() {
		return m_isTimeZoneNegative;
	}

	public boolean isTimeZoneUtc() {
		return m_isTimeZoneUtc;
	}

	public int getTimezoneHrs() {
		return m_timezone_hrs;
	}

	public int getTimezoneMin() {
		return m_timezone_min;
	}

	public boolean isTimeZoned() {
		return m_isTimeZoned;
	}
	
	/**
	 * Method definition to parse a string value to an XML Schema type 
	 * xs:gMonthDay's component parts. 
	 * 
	 * @param gMonthDayStrValue						    A string value that needs to be parsed 
	 *                                                  to a xs:gMonthDay value. 
	 * @throws TransformerException
	 */
	private void parse(String gMonthDayStrValue) throws TransformerException {		
		try {
			if (gMonthDayStrValue.startsWith("--")) {
				String suffixValue = gMonthDayStrValue.substring(2);
				if (suffixValue.endsWith("Z") && (suffixValue.charAt(2) == '-')) {
					String monthStrValue = suffixValue.substring(0,2);
					String dayStrValue = suffixValue.substring(3);
					if (isMonthAndDayValueConsistent(monthStrValue, dayStrValue)) {
						m_month = Integer.valueOf(monthStrValue);					
						m_day = Integer.valueOf(dayStrValue);
					}
					else {
						throw new TransformerException("FORG0001 : A string value " + gMonthDayStrValue + " cannot be parsed to a "
                                                                                                        + "value of type xs:gMonthDay.");
					}
					m_isTimeZoneUtc = true;
					m_isTimeZoned = true;
				}
				else {
					int plusIdx = suffixValue.indexOf('+');
					if (plusIdx > -1) {
						String str2 = suffixValue.substring(0, plusIdx);
						if (str2.charAt(2) == '-') {
							String monthStrValue = str2.substring(0,2);
							String dayStrValue = str2.substring(3);
							if (isMonthAndDayValueConsistent(monthStrValue, dayStrValue)) {
								m_month = Integer.valueOf(monthStrValue);					
								m_day = Integer.valueOf(dayStrValue);
							}
							else {
								throw new TransformerException("FORG0001 : A string value " + gMonthDayStrValue + " cannot be parsed to a "
                                                                                                                + "value of type xs:gMonthDay.");
							}
							String timeZoneStr = suffixValue.substring(plusIdx + 1);
							if (isTimeZoneStrCorrectlyFormatted(timeZoneStr)) {
								String[] timeZoneStrParts = timeZoneStr.split(":");
								m_timezone_hrs = Integer.valueOf(timeZoneStrParts[0]); 
								m_timezone_min = Integer.valueOf(timeZoneStrParts[1]);
								if ((m_timezone_hrs == 0) && (m_timezone_min == 0)) {
									m_isTimeZoneUtc = true;  
								}
								m_isTimeZoned = true;
							}
							else {
								throw new TransformerException("FORG0001 : A string value " + gMonthDayStrValue + " cannot be parsed to a "
                                                                                                                + "value of type xs:gMonthDay.");
							}
						}
						else {
							throw new TransformerException("FORG0001 : A string value " + gMonthDayStrValue + " cannot be parsed to a "
                                                                                                            + "value of type xs:gMonthDay.");
						}
					}
					else {
						String[] strParts = suffixValue.split("-");
						int strLength = strParts.length; 
						if ((strLength == 2) && isMonthAndDayValueConsistent(strParts[0], strParts[1])) {							
							m_month = Integer.valueOf(strParts[0]);					
							m_day = Integer.valueOf(strParts[1]); 
						}
						else if ((strLength == 3) && isMonthAndDayValueConsistent(strParts[0], strParts[1])) {
							m_month = Integer.valueOf(strParts[0]);					
							m_day = Integer.valueOf(strParts[1]);
							String timeZoneStr = strParts[2];
							if (isTimeZoneStrCorrectlyFormatted(timeZoneStr)) {
								String[] timeZoneStrParts = timeZoneStr.split(":");
								m_timezone_hrs = Integer.valueOf(timeZoneStrParts[0]); 
								m_timezone_min = Integer.valueOf(timeZoneStrParts[1]);
								if ((m_timezone_hrs == 0) && (m_timezone_min == 0)) {
									m_isTimeZoneUtc = true;  
								}
								m_isTimeZoned = true;
								m_isTimeZoneNegative = true;
							}
							else {
								throw new TransformerException("FORG0001 : A string value " + gMonthDayStrValue + " cannot be parsed to a "
                                                                                                                + "value of type xs:gMonthDay.");
							}
						}
						else {
							throw new TransformerException("FORG0001 : A string value " + gMonthDayStrValue + " cannot be parsed to a "
                                                                                                            + "value of type xs:gMonthDay.");
						}
					}
				}
			}
			else {
				throw new TransformerException("FORG0001 : A string value " + gMonthDayStrValue + " cannot be parsed to a "
                                                                                                + "value of type xs:gMonthDay.");
			}
		}
		catch (Exception ex) {
			throw new TransformerException("FORG0001 : A string value " + gMonthDayStrValue + " cannot be parsed to a "
                                                                                            + "value of type xs:gMonthDay.");
		}
	}
	
	/**
	 * Method definition to check the string format of a timezone string.
	 * 
	 * (The regex for timezone's string value is specified within XML Schema 
	 *  datatypes specification)
	 * 
	 * @param strValue					The supplied string value of timezone
	 * @return							Boolean true or false
	 */
	private boolean isTimeZoneStrCorrectlyFormatted(String strValue) {
		boolean result = false;
		
		java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("((0[0-9]|1[0-3]):[0-5][0-9]|14:00)");
		java.util.regex.Matcher matcher = pattern.matcher(strValue);
		result = matcher.matches(); 
		
		return result;
	}

}
