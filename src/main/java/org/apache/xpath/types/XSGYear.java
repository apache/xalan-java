package org.apache.xpath.types;

import javax.xml.transform.TransformerException;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;

import xml.xpath31.processor.types.XSAnyAtomicType;

/**
 * Implementation of XML Schema data type xs:gYear.
 * 
 * @author : Mukul Gandhi <mukulg@apache.org>
 */
public class XSGYear extends XSAnyAtomicType {
	
	private static final long serialVersionUID = 4500652479945465195L;

	/**
	 * Class fields to represent components of XML Schema type xs:gYear.  
	 */
	
    private int m_year;
	
	private boolean m_isNegative;
	
	private boolean m_isTimeZoneNegative;
	
	private boolean m_isTimeZoneUtc;
	
	private int m_timezone_hrs;
	
	private int m_timezone_min;
	
	private boolean m_isTimeZoned;
	
	private String m_gYearStrValue;
	
	private static final String XS_GYEAR = "xs:gYear";
	
	/**
	 * Default constructor.
	 */
	public XSGYear() {
		// NO OP
	}
	
	/**
	 * Class constructor.
	 */
	public XSGYear(String gYearStrValue) throws TransformerException {
		parse(gYearStrValue);
		m_gYearStrValue = gYearStrValue; 
	}

	@Override
	public String stringType() {
		return XS_GYEAR;
	}
	
	public ResultSequence constructor(ResultSequence seq) {
		ResultSequence result = new ResultSequence();
		
		XObject xObj = seq.item(0);
		String strVal = XslTransformEvaluationHelper.getStrVal(xObj);
		try {
			XSGYear xsGYear = new XSGYear(strVal);
			result.add(xsGYear);
		} catch (TransformerException ex) {
			// NO OP
		}
		
		return result;
	}

	@Override
	public String stringValue() {
		return m_gYearStrValue;
	}
	
	public int getType() {
		return CLASS_GYEAR;
	}
	
	/**
	 * Implementation of operation equals, for the type xs:gYear.
	 */
	public boolean eq(XSGYear obj2) {
		boolean result = false;

		result = ((m_year == obj2.getYear()) && (m_isNegative == obj2.isNegative()) && 
				  (m_isTimeZoneNegative == obj2.isTimeZoneNegative()) && (m_isTimeZoneUtc == obj2.isTimeZoneUtc()) && 
				  (m_timezone_hrs == obj2.getTimezoneHrs()) && (m_timezone_min == obj2.getTimezoneMin()) && 
				  (m_isTimeZoned == obj2.isTimeZoned()));

		return result;
	}
	
	/**
	 * Implementation of operation not equals, for the type xs:gYear.
	 */
	public boolean ne(XSGYear obj2) {
        boolean result = false;
		
		result = !eq(obj2);
		
		return result;
	}

	@Override
	public String typeName() {
		return "gYear";
	}
	
	/**
	 * Method definition to parse a string value into the type xs:gYear's 
	 * component parts. 
	 * 
	 * @param gYearMonthStrValue						A string value that needs to be parsed 
	 *                                                  to a xs:gYear value. 
	 * @throws TransformerException
	 */
	private void parse(String gYearStrValue) throws TransformerException {
		
		try {
			int strLength = gYearStrValue.length();			
			if (strLength > 0) {
				if (gYearStrValue.charAt(0) == '-') {
					m_isNegative = true;
					String suffixValue = gYearStrValue.substring(1);
					if (suffixValue.endsWith("Z")) {
						m_isTimeZoneUtc = true;						
						int zIdx = suffixValue.indexOf('Z');
						String str2 = suffixValue.substring(0, zIdx);
						m_year = Integer.valueOf(str2); 
					}
					else {
						int plusIdx = suffixValue.indexOf('+');
						if (plusIdx > -1) {
							String str2 = suffixValue.substring(0, plusIdx);
							m_year = Integer.valueOf(str2);
							String timeZoneStr = suffixValue.substring(plusIdx + 1);
							String[] timeZoneStrParts = timeZoneStr.split(":");
							m_timezone_hrs = Integer.valueOf(timeZoneStrParts[0]); 
							m_timezone_min = Integer.valueOf(timeZoneStrParts[1]);
							if ((m_timezone_hrs == 0) && (m_timezone_min == 0)) {
								m_isTimeZoneUtc = true;  
							}
							m_isTimeZoned = true;
						}
						else {
							String[] strParts = suffixValue.split("-");
							int strArrLength = strParts.length;
							if (strArrLength == 1) {
								m_year = Integer.valueOf(strParts[0]);
							}
							else if (strArrLength == 2) {
								m_year = Integer.valueOf(strParts[0]);
								String[] timeZoneStrParts = (strParts[1]).split(":");
								m_timezone_hrs = Integer.valueOf(timeZoneStrParts[0]); 
								m_timezone_min = Integer.valueOf(timeZoneStrParts[1]);
								if ((m_timezone_hrs == 0) && (m_timezone_min == 0)) {
									m_isTimeZoneUtc = true;  
								}
								m_isTimeZoned = true;
								m_isTimeZoneNegative = true;
							}
						}
					}
				}
				else if (gYearStrValue.endsWith("Z")) {
					m_isTimeZoneUtc = true;						
					int zIdx = gYearStrValue.indexOf('Z');
					String str2 = gYearStrValue.substring(0, zIdx);
					m_year = Integer.valueOf(str2);
					m_isTimeZoned = true;
				}
				else {
					int plusIdx = gYearStrValue.indexOf('+');
					if (plusIdx > -1) {
						String str2 = gYearStrValue.substring(0, plusIdx);
						m_year = Integer.valueOf(str2);
						String timeZoneStr = gYearStrValue.substring(plusIdx + 1);
						String[] timeZoneStrParts = timeZoneStr.split(":");
						m_timezone_hrs = Integer.valueOf(timeZoneStrParts[0]); 
						m_timezone_min = Integer.valueOf(timeZoneStrParts[1]);
						if ((m_timezone_hrs == 0) && (m_timezone_min == 0)) {
							m_isTimeZoneUtc = true;  
						}
						m_isTimeZoned = true;
					}
					else {
						String[] strParts = gYearStrValue.split("-"); 
						int strArrLength = strParts.length;
						if (strArrLength == 1) {
							m_year = Integer.valueOf(strParts[0]);
						}
						else if (strArrLength == 2) {
							m_year = Integer.valueOf(strParts[0]);
							String[] timeZoneStrParts = (strParts[1]).split(":");
							m_timezone_hrs = Integer.valueOf(timeZoneStrParts[0]); 
							m_timezone_min = Integer.valueOf(timeZoneStrParts[1]);
							if ((m_timezone_hrs == 0) && (m_timezone_min == 0)) {
								m_isTimeZoneUtc = true;  
							}
							m_isTimeZoned = true;
							m_isTimeZoneNegative = true;
						}
					}
				}
			}
			else {
				throw new TransformerException("FORG0001 : A zero length string was supplied to construct a value of type xs:gYear.");	
			}
	    }
	    catch (Exception ex) {
	    	throw new TransformerException("FORG0001 : A string value " + gYearStrValue + " cannot be parsed to a value of type xs:gYear.");	
	    }
	}

	/**
	 * Getter method definitions for various components of an XML Schema type xs:gYear. 
	 */
	
	public int getYear() {
		return m_year;
	}
	
	public boolean isNegative() {
		return m_isNegative;
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

}
