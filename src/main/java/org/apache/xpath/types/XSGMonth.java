package org.apache.xpath.types;

import javax.xml.transform.TransformerException;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.regex.Matcher;
import org.apache.xpath.regex.Pattern;

import xml.xpath31.processor.types.XSAnyAtomicType;

/**
 * Implementation of XML Schema data type xs:gMonth.
 * 
 * @author : Mukul Gandhi <mukulg@apache.org>
 */
public class XSGMonth extends XSAnyAtomicType {

	private static final long serialVersionUID = -3607305468895616720L;

	/**
	 * Class fields to represent components of XML Schema type xs:gMonth.  
	 */
	
	private int m_month;
	
    private boolean m_isTimeZoneNegative;
	
	private boolean m_isTimeZoneUtc;
	
	private int m_timezone_hrs;
	
	private int m_timezone_min;
	
	private boolean m_isTimeZoned;
	
	private String m_gMonthStrValue;
	
	private static final String XS_GMONTH = "xs:gMonth";
	
	/**
	 * Default constructor.
	 */
	public XSGMonth() {
		// NO OP
	}
	
	/**
	 * Class constructor.
	 */
	public XSGMonth(String gMonthStrValue) throws TransformerException {
		parse(gMonthStrValue);
		m_gMonthStrValue = gMonthStrValue; 
	}

	@Override
	public String stringType() {
		return XS_GMONTH;
	}
	
	public ResultSequence constructor(ResultSequence seq) {
		ResultSequence result = new ResultSequence();
		
		XObject xObj = seq.item(0);
		String strVal = XslTransformEvaluationHelper.getStrVal(xObj);
		try {
			XSGMonth xsGMonth = new XSGMonth(strVal);
			result.add(xsGMonth);
		} catch (TransformerException ex) {
			// NO OP
		}
		
		return result;
	}

	@Override
	public String stringValue() {
		return m_gMonthStrValue;
	}
	
	public int getType() {
		return CLASS_GMONTH;
	}
	
	/**
	 * Implementation of operation equals, for the type xs:gMonth.
	 */
	public boolean eq(XSGMonth obj2) {
		boolean result = false;

		result = ((m_month == obj2.getMonth()) && (m_isTimeZoneNegative == obj2.isTimeZoneNegative()) && 
				  (m_isTimeZoneUtc == obj2.isTimeZoneUtc()) && (m_timezone_hrs == obj2.getTimezoneHrs()) && 
				  (m_timezone_min == obj2.getTimezoneMin()) && (m_isTimeZoned == obj2.isTimeZoned()));

		return result;
	}
	
	/**
	 * Implementation of operation not equals, for the type xs:gMonth.
	 */
	public boolean ne(XSGMonth obj2) {
        boolean result = false;
		
		result = !eq(obj2);
		
		return result;
	}

	@Override
	public String typeName() {
		return "gMonth";
	}
	
	/**
	 * Getter method definitions for various components of an XML Schema type xs:gMonth. 
	 */
	
	public int getMonth() {
		return m_month;
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
	 * xs:gMonth's component parts. 
	 * 
	 * @param gMonthStrValue						A string value that needs to be parsed 
	 *                                              to a xs:gMonth value. 
	 * @throws TransformerException
	 */
	private void parse(String gMonthStrValue) throws TransformerException {		
		try {
			if (gMonthStrValue.startsWith("--")) {
				String suffixValue = gMonthStrValue.substring(2);
				if (suffixValue.endsWith("Z")) {											
					int zIdx = suffixValue.indexOf('Z');
					String str2 = suffixValue.substring(0, zIdx);
					m_month = Integer.valueOf(str2);
					m_isTimeZoneUtc = true;
					m_isTimeZoned = true;
				}
				else {
					int plusIdx = suffixValue.indexOf('+');
					if (plusIdx > -1) {
						String str2 = suffixValue.substring(0, plusIdx);
						m_month = Integer.valueOf(str2);
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
							throw new TransformerException("FORG0001 : A string value " + gMonthStrValue + " cannot be parsed to a "
                                                                                                         + "value of type xs:gMonth.");
						}
					}
					else {
						int minusIdx = suffixValue.indexOf('-');
						if (minusIdx > -1) {
							String str2 = suffixValue.substring(0, minusIdx);
							m_month = Integer.valueOf(str2);
							String timeZoneStr = suffixValue.substring(minusIdx + 1);
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
								throw new TransformerException("FORG0001 : A string value " + gMonthStrValue + " cannot be parsed to a "
                                        																	 + "value of type xs:gMonth.");
							}
						}
						else {
							m_month = Integer.valueOf(suffixValue);
						}
					}
				}
			}
			else {
				throw new TransformerException("FORG0001 : A string value " + gMonthStrValue + " cannot be parsed to a "
					                                                                         + "value of type xs:gMonth.");
			}
		}
		catch (Exception ex) {
			throw new TransformerException("FORG0001 : A string value " + gMonthStrValue + " cannot be parsed to a "
                    																     + "value of type xs:gMonth.");
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
		
		Pattern pattern = Pattern.compile("((0[0-9]|1[0-3]):[0-5][0-9]|14:00)");
		Matcher matcher = pattern.matcher(strValue);
		result = matcher.matches();  
		
		return result;
	}

}
