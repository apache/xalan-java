package org.apache.xpath.types;

import javax.xml.transform.TransformerException;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;

import xml.xpath31.processor.types.XSAnyAtomicType;

/**
 * Implementation of XML Schema data type xs:gYearMonth.
 * 
 * @author : Mukul Gandhi <mukulg@apache.org>
 */
public class XSGYearMonth extends XSAnyAtomicType {

	private static final long serialVersionUID = 7570071303286900072L;
	
	/**
	 * Class fields to represent components of XML Schema type xs:gYearMonth.  
	 */
	private int m_year;
	
	private int m_month;
	
	private boolean m_isNegative;
	
	private boolean m_isTimeZoneNegative;
	
	private boolean m_isTimeZoneUtc;
	
	private int m_timezone_hrs;
	
	private int m_timezone_min;
	
	private boolean m_isTimeZoned;
	
	private String m_gYearMonthStrValue;
	
	private static final String XS_GYEAR_MONTH = "xs:gYearMonth";
	
	/**
	 * Default constructor.
	 */
	public XSGYearMonth() {
		// NO OP
	}
	
	/**
	 * Class constructor.
	 */
	public XSGYearMonth(String gYearMonthStrValue) throws TransformerException {		
		parse(gYearMonthStrValue);
		m_gYearMonthStrValue = gYearMonthStrValue;
	}
	
	@Override
	public String stringType() {
		return XS_GYEAR_MONTH;
	}
	
	public ResultSequence constructor(ResultSequence seq) {
		ResultSequence result = new ResultSequence();
		
		XObject xObj = seq.item(0);
		String strVal = XslTransformEvaluationHelper.getStrVal(xObj);
		try {
			XSGYearMonth xsGYearMonth = new XSGYearMonth(strVal);
			result.add(xsGYearMonth);
		} catch (TransformerException ex) {
			// NO OP
		}
		
		return result;
	}

	@Override
	public String stringValue() {
		return m_gYearMonthStrValue;
	}
	
	public int getType() {
		return CLASS_GYEAR_MONTH;
	}
	
	/**
	 * Implementation of operation equals, for the type xs:gYearMonth.
	 */
	public boolean eq(XSGYearMonth obj2) {		
		boolean result = false;
		
		result = ((m_year == obj2.getYear()) && (m_month == obj2.getMonth()) && 
				  (m_isNegative == obj2.isNegative()) && (m_isTimeZoneNegative == obj2.isTimeZoneNegative()) && 
				  (m_isTimeZoneUtc == obj2.isTimeZoneUtc()) && (m_timezone_hrs == obj2.getTimezoneHrs()) && 
				  (m_timezone_min == obj2.getTimezoneMin()) && (m_isTimeZoned == obj2.isTimeZoned()));
		
		return result;
	}
	
	/**
	 * Implementation of operation not equals, for the type xs:gYearMonth.
	 */
	public boolean ne(XSGYearMonth obj2) {
		boolean result = false;
		
		result = !eq(obj2);
		
		return result;
	}

	@Override
	public String typeName() {
		return "gYearMonth";
	}
	
	/**
	 * Method definition to parse a string value into the type xs:gYearMonth's 
	 * component parts. 
	 * 
	 * @param gYearMonthStrValue						A string value that needs to be parsed 
	 *                                                  to a xs:gYearMonth value. 
	 * @throws TransformerException
	 */
	private void parse(String gYearMonthStrValue) throws TransformerException {
		
		try {
			int strLength = gYearMonthStrValue.length();			
			if (strLength > 0) {
				if (gYearMonthStrValue.charAt(0) == '-') {
					m_isNegative = true;
					String suffixValue = gYearMonthStrValue.substring(1);
					if (suffixValue.endsWith("Z")) {
						m_isTimeZoneUtc = true;						
						int zIdx = suffixValue.indexOf('Z');
						String str2 = suffixValue.substring(0, zIdx);
						int minusIdx = str2.indexOf('-');
						m_year = Integer.valueOf(str2.substring(0, minusIdx));
						m_month = Integer.valueOf(str2.substring(minusIdx + 1)); 
					}
					else {
						int plusIdx = suffixValue.indexOf('+');
						if (plusIdx > -1) {
							String str2 = suffixValue.substring(0, plusIdx);
							int minusIdx = str2.indexOf('-');
							m_year = Integer.valueOf(str2.substring(0, minusIdx));
							m_month = Integer.valueOf(str2.substring(minusIdx + 1));
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
							if (strArrLength == 2) {
								m_year = Integer.valueOf(strParts[0]);
								m_month = Integer.valueOf(strParts[1]);  
							}
							else if (strArrLength == 3) {
								m_year = Integer.valueOf(strParts[0]);
								m_month = Integer.valueOf(strParts[1]);
								String[] timeZoneStrParts = (strParts[2]).split(":");
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
				else if (gYearMonthStrValue.endsWith("Z")) {
					m_isTimeZoneUtc = true;						
					int zIdx = gYearMonthStrValue.indexOf('Z');
					String str2 = gYearMonthStrValue.substring(0, zIdx);
					int minusIdx = str2.indexOf('-');
					m_year = Integer.valueOf(str2.substring(0, minusIdx));
					m_month = Integer.valueOf(str2.substring(minusIdx + 1));
					m_isTimeZoned = true;
				}
				else {
					int plusIdx = gYearMonthStrValue.indexOf('+');
					if (plusIdx > -1) {
						String str2 = gYearMonthStrValue.substring(0, plusIdx);
						int minusIdx = str2.indexOf('-');
						m_year = Integer.valueOf(str2.substring(0, minusIdx));
						m_month = Integer.valueOf(str2.substring(minusIdx + 1));
						String timeZoneStr = gYearMonthStrValue.substring(plusIdx + 1);
						String[] timeZoneStrParts = timeZoneStr.split(":");
						m_timezone_hrs = Integer.valueOf(timeZoneStrParts[0]); 
						m_timezone_min = Integer.valueOf(timeZoneStrParts[1]);
						if ((m_timezone_hrs == 0) && (m_timezone_min == 0)) {
							m_isTimeZoneUtc = true;  
						}
						m_isTimeZoned = true;
					}
					else {
						String[] strParts = gYearMonthStrValue.split("-"); 
						int strArrLength = strParts.length;
						if (strArrLength == 2) {
							m_year = Integer.valueOf(strParts[0]);
							m_month = Integer.valueOf(strParts[1]);  
						}
						else if (strArrLength == 3) {
							m_year = Integer.valueOf(strParts[0]);
							m_month = Integer.valueOf(strParts[1]);
							String[] timeZoneStrParts = (strParts[2]).split(":");
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
				throw new TransformerException("FORG0001 : A zero length string was supplied to construct a value of type xs:gYearMonth.");
			}
		}
		catch (Exception ex) {
		    throw new TransformerException("FORG0001 : A string value " + gYearMonthStrValue + " cannot be parsed to a "
		    		                                                                         + "value of type xs:gYearMonth.");	
		}
	}

	/**
	 * Getter method definitions for various components of an XML Schema type xs:gYearMonth. 
	 */
	
	public int getYear() {
		return m_year;
	}
	
	public int getMonth() {
		return m_month;
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
