package org.apache.xpath.types;

import javax.xml.transform.TransformerException;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;

import xml.xpath31.processor.types.XSAnyAtomicType;

/**
 * Implementation of XML Schema data type xs:gDay.
 * 
 * @author : Mukul Gandhi <mukulg@apache.org>
 */
public class XSGDay extends XSAnyAtomicType {

	private static final long serialVersionUID = 5356100798451721396L;

	/**
	 * Class fields to represent components of XML Schema type xs:gDay.  
	 */
	
	private int m_day;
	
    private boolean m_isTimeZoneNegative;
	
	private boolean m_isTimeZoneUtc;
	
	private int m_timezone_hrs;
	
	private int m_timezone_min;
	
	private boolean m_isTimeZoned;
	
	private String m_gDayStrValue;
	
	private static final String XS_GDAY = "xs:gDay";
	
	/**
	 * Default constructor.
	 */
	public XSGDay() {
		// NO OP
	}
	
	/**
	 * Class constructor.
	 */
	public XSGDay(String gDayStrValue) throws TransformerException {
		parse(gDayStrValue);
		m_gDayStrValue = gDayStrValue; 
	}

	@Override
	public String stringType() {
		return XS_GDAY;
	}
	
	public ResultSequence constructor(ResultSequence seq) {
		ResultSequence result = new ResultSequence();
		
		XObject xObj = seq.item(0);
		String strVal = XslTransformEvaluationHelper.getStrVal(xObj);
		try {
			XSGDay xsGDay = new XSGDay(strVal);
			result.add(xsGDay);
		} catch (TransformerException ex) {
			// NO OP
		}
		
		return result;
	}

	@Override
	public String stringValue() {
		return m_gDayStrValue;
	}
	
	public int getType() {
		return CLASS_GDAY;
	}
	
	/**
	 * Implementation of operation equals, for the type xs:gDay.
	 */
	public boolean eq(XSGDay obj2) {
        boolean result = false;
		
		result = ((m_day == obj2.getDay()) && (m_isTimeZoneNegative == obj2.isTimeZoneNegative()) && 
				  (m_isTimeZoneUtc == obj2.isTimeZoneUtc()) && (m_timezone_hrs == obj2.getTimezoneHrs()) && 
				  (m_timezone_min == obj2.getTimezoneMin()) && (m_isTimeZoned == obj2.isTimeZoned()));
		
		return result;
	}
	
	/**
	 * Implementation of operation not equals, for the type xs:gDay.
	 */
	public boolean ne(XSGDay obj2) {
        boolean result = false;
		
		result = !eq(obj2);
		
		return result;
	}

	@Override
	public String typeName() {
		return "gDay";
	}
	
	/**
	 * Getter method definitions for various components of an XML Schema type xs:gDay. 
	 */
	
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
	 * xs:gDay's component parts. 
	 * 
	 * @param gDayStrValue						    A string value that needs to be parsed 
	 *                                              to a xs:gDay value. 
	 * @throws TransformerException
	 */
	private void parse(String gDayStrValue) throws TransformerException {		
		try {
			if (gDayStrValue.startsWith("---")) {
				String suffixValue = gDayStrValue.substring(3);
				if (suffixValue.endsWith("Z")) {
				    m_day = Integer.valueOf(suffixValue.substring(0,2));
				    m_isTimeZoneUtc = true;
					m_isTimeZoned = true;
				}
				else {
					int plusIdx = suffixValue.indexOf('+');
					if (plusIdx > -1) {
						m_day = Integer.valueOf(suffixValue.substring(0, plusIdx));
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
							throw new TransformerException("FORG0001 : A string value " + gDayStrValue + " cannot be parsed to a "
                                                                                                       + "value of type xs:gDay.");
						}
					}
					else {
						int minusIdx = suffixValue.indexOf('-');
						if (minusIdx > -1) {
							m_day = Integer.valueOf(suffixValue.substring(0, minusIdx));
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
								throw new TransformerException("FORG0001 : A string value " + gDayStrValue + " cannot be parsed to a "
                                                                                                           + "value of type xs:gDay.");
							}
						}
						else {
							m_day = Integer.valueOf(suffixValue);
						}
					}
				}
			}
			else {
				throw new TransformerException("FORG0001 : A string value " + gDayStrValue + " cannot be parsed to a "
						                                                                   + "value of type xs:gDay.");
			}
		}
		catch (Exception ex) {
			throw new TransformerException("FORG0001 : A string value " + gDayStrValue + " cannot be parsed to a "
                    																   + "value of type xs:gDay.");
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
