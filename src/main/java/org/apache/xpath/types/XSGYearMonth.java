package org.apache.xpath.types;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.xml.transform.TransformerException;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;

import xml.xpath31.processor.types.XSAnySimpleType;
import xml.xpath31.processor.types.XSBoolean;
import xml.xpath31.processor.types.XSInteger;

/**
 * Implementation of XML Schema data type xs:gYearMonth.
 * 
 * @author : Mukul Gandhi <mukulg@apache.org>
 */
public class XSGYearMonth extends XSAnySimpleType {

	private static final long serialVersionUID = 7570071303286900072L;
	
	private static XSInteger year;
	
	private static XSInteger month;
	
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
		String monthStr = month.stringValue();
		
		resultStr = yearStr + "-" + monthStr;
		
		return resultStr;
	}
	
	/**
	 * Get the year component from this xs:gYearMonth object instance. 
	 */
	public XSInteger getYear() {
		XSInteger yearValue = null;
		
		BigDecimal bigDecimal = year.getValue();
		yearValue = new XSInteger(BigInteger.valueOf(bigDecimal.longValue()));
		
		return yearValue;
	}
	
	/**
	 * Get the month component from this xs:gYearMonth object instance. 
	 */
	public XSInteger getMonth() {
		XSInteger monthValue = null;
		
		BigDecimal bigDecimal = month.getValue();
		monthValue = new XSInteger(BigInteger.valueOf(bigDecimal.longValue()));
		
		return monthValue;
	}
	
	public int getType() {
		return CLASS_GYEAR_MONTH;
	}
	
	/**
	 * Implementation of operation equals, for the type xs:gYearMonth.
	 */
	public XSBoolean eq(XSGYearMonth value) {
		XSBoolean result = null;
		
		XSInteger thisYear = this.getYear();
		XSInteger argYear = value.getYear();
		
		XSInteger thisMonth = this.getMonth();
		XSInteger argMonth = value.getMonth();
		
		boolean boolResult = (thisYear.intValue() == argYear.intValue()) && 
				                              (thisMonth.intValue() == argMonth.intValue());
		
		result = new XSBoolean(boolResult);
		
		return result;
	}
	
	/**
	 * Implementation of operation not equals, for the type xs:gYearMonth.
	 */
	public XSBoolean ne(XSGYearMonth value) {
		XSBoolean result = null;
		
		XSInteger thisYear = this.getYear();
		XSInteger argYear = value.getYear();
		
		XSInteger thisMonth = this.getMonth();
		XSInteger argMonth = value.getMonth();
		
		boolean boolResult = (thisYear.intValue() != argYear.intValue()) || 
				                              (thisMonth.intValue() != argMonth.intValue());
		
		result = new XSBoolean(boolResult);
		
		return result;
	}

}
