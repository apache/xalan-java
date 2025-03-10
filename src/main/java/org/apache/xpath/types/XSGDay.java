package org.apache.xpath.types;

import java.math.BigInteger;

import javax.xml.transform.TransformerException;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;

import xml.xpath31.processor.types.XSAnyAtomicType;
import xml.xpath31.processor.types.XSInteger;

/**
 * Implementation of XML Schema data type xs:gDay.
 * 
 * @author : Mukul Gandhi <mukulg@apache.org>
 */
public class XSGDay extends XSAnyAtomicType {

	private static final long serialVersionUID = 5356100798451721396L;

	private XSInteger date;
	
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
		if (gDayStrValue.length() != 5) {
		    throw new TransformerException("FOCA0003 : An xs:gDay value cannot be "
		    		                                                   + "constructed from string value " + gDayStrValue);
		}
		else {
			XSInteger xsInteger = new XSInteger(gDayStrValue.substring(3));
			BigInteger bigInt = xsInteger.intValue();
			int int1 = bigInt.compareTo(BigInteger.valueOf(1));
			int int2 = bigInt.compareTo(BigInteger.valueOf(31));
			if ((int1 == -1) || (int2 == 1)) {
				throw new TransformerException("FOCA0003 : An xs:gDay value cannot be "
                                                                      + "constructed from string value " + gDayStrValue);
			}
			
			this.date = xsInteger;
		}
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
		String resultStr = null;
		
		String dateStr = date.stringValue();
		dateStr = (dateStr.length() == 1) ? "0"+dateStr : dateStr;
		
		resultStr = "---" + dateStr;
		
		return resultStr;
	}
	
	/**
	 * Get the days component from this xs:gDay object instance. 
	 */
	public XSInteger getDate() {
		return date;
	}
	
	public int getType() {
		return CLASS_GDAY;
	}
	
	/**
	 * Implementation of operation equals, for the type xs:gDay.
	 */
	public boolean eq(XSGDay value) {
		boolean result = false;
		
		XSInteger thisDate = this.getDate();
		XSInteger argDate = value.getDate();
		
		result = (thisDate.intValue()).equals(argDate.intValue());
		
		return result;
	}
	
	/**
	 * Implementation of operation not equals, for the type xs:gDay.
	 */
	public boolean ne(XSGDay value) {
        boolean result = false;
		
		XSInteger thisDate = this.getDate();
		XSInteger argDate = value.getDate();
		
		result = !(thisDate.intValue()).equals(argDate.intValue());
		
		return result;
	}

	@Override
	public String typeName() {
		return "gDay";
	}

}
