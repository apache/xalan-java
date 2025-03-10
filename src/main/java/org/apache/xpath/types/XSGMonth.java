package org.apache.xpath.types;

import java.math.BigInteger;

import javax.xml.transform.TransformerException;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;

import xml.xpath31.processor.types.XSAnyAtomicType;
import xml.xpath31.processor.types.XSInteger;

/**
 * Implementation of XML Schema data type xs:gMonth.
 * 
 * @author : Mukul Gandhi <mukulg@apache.org>
 */
public class XSGMonth extends XSAnyAtomicType {

	private static final long serialVersionUID = -3607305468895616720L;

	private XSInteger month;
	
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
		if (gMonthStrValue.length() != 4) {
		    throw new TransformerException("FOCA0003 : An xs:gMonth value cannot be "
		    		                                                   + "constructed from string value " + gMonthStrValue);
		}
		else {
			XSInteger xsInteger = new XSInteger(gMonthStrValue.substring(2));
			BigInteger bigInt = xsInteger.intValue();
			int int1 = bigInt.compareTo(BigInteger.valueOf(1));
			int int2 = bigInt.compareTo(BigInteger.valueOf(12));
			if ((int1 == -1) || (int2 == 1)) {
				throw new TransformerException("FOCA0003 : An xs:gMonth value cannot be "
                                                                      + "constructed from string value " + gMonthStrValue);
			}
			
			this.month = xsInteger;
		}
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
		String resultStr = null;
		
		String monthStr = month.stringValue();
		monthStr = (monthStr.length() == 1) ? "0"+monthStr : monthStr;
		
		resultStr = "--" + monthStr;
		
		return resultStr;
	}
	
	/**
	 * Get the month component from this xs:gMonth object instance. 
	 */
	public XSInteger getMonth() {
		return month;
	}
	
	public int getType() {
		return CLASS_GMONTH;
	}
	
	/**
	 * Implementation of operation equals, for the type xs:gMonth.
	 */
	public boolean eq(XSGMonth value) {
		boolean result = false;
		
		XSInteger thisMonth = this.getMonth();
		XSInteger argMonth = value.getMonth();
		
		result = (thisMonth.intValue()).equals(argMonth.intValue());
		
		return result;
	}
	
	/**
	 * Implementation of operation not equals, for the type xs:gMonth.
	 */
	public boolean ne(XSGMonth value) {
        boolean result = false;
		
        XSInteger thisMonth = this.getMonth();
		XSInteger argMonth = value.getMonth();
		
		result = !(thisMonth.intValue()).equals(argMonth.intValue());
		
		return result;
	}

	@Override
	public String typeName() {
		return "gMonth";
	}

}
