package org.apache.xpath.types;

import javax.xml.transform.TransformerException;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;

import xml.xpath31.processor.types.XSAnySimpleType;
import xml.xpath31.processor.types.XSInteger;

/**
 * Implementation of XML Schema data type xs:gYearMonth.
 * 
 * @author : Mukul Gandhi <mukulg@apache.org>
 */
public class XSGYearMonth extends XSAnySimpleType {

	private static final long serialVersionUID = 7570071303286900072L;
	
	private XSInteger year;
	
	private XSInteger month;
	
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
		if ((gYearMonthStrValue.length() != 7) && (gYearMonthStrValue.charAt(4) != '-')) {
		    throw new TransformerException("FOCA0003 : An xs:gYearMonth value cannot be "
		    		                                                 + "constructed from string value " + gYearMonthStrValue);
		}
		else {
			String yearStr = gYearMonthStrValue.substring(0, 4);
			this.year = new XSInteger(yearStr);		
			String monthStr = gYearMonthStrValue.substring(5);
			this.month = new XSInteger(monthStr);
		}
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
		String resultStr = null;
		
		String yearStr = year.stringValue();
		yearStr = (yearStr.length() == 1) ? "0"+yearStr : yearStr; 
		String monthStr = month.stringValue();
		monthStr = (monthStr.length() == 1) ? "0"+monthStr : monthStr;
		
		resultStr = yearStr + "-" + monthStr;
		
		return resultStr;
	}
	
	/**
	 * Get the year component from this xs:gYearMonth object instance. 
	 */
	public XSInteger getYear() {
		return year;
	}
	
	/**
	 * Get the month component from this xs:gYearMonth object instance. 
	 */
	public XSInteger getMonth() {
		return month;
	}
	
	public int getType() {
		return CLASS_GYEAR_MONTH;
	}
	
	/**
	 * Implementation of operation equals, for the type xs:gYearMonth.
	 */
	public boolean eq(XSGYearMonth value) {
		boolean result = false;
		
		XSInteger thisYear = this.getYear();
		XSInteger argYear = value.getYear();
		
		XSInteger thisMonth = this.getMonth();
		XSInteger argMonth = value.getMonth();
		
		result = ((thisYear.intValue()).equals(argYear.intValue()) && (thisMonth.intValue()).equals(argMonth.intValue()));
		
		return result;
	}
	
	/**
	 * Implementation of operation not equals, for the type xs:gYearMonth.
	 */
	public boolean ne(XSGYearMonth value) {
		boolean result = false;
		
		XSInteger thisYear = this.getYear();
		XSInteger argYear = value.getYear();
		
		XSInteger thisMonth = this.getMonth();
		XSInteger argMonth = value.getMonth();
		
		result = (!(thisYear.intValue()).equals(argYear.intValue()) || !(thisMonth.intValue()).equals(argMonth.intValue()));
		
		return result;
	}

}
