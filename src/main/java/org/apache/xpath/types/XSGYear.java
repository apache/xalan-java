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
 * Implementation of XML Schema data type xs:gYear.
 * 
 * @author : Mukul Gandhi <mukulg@apache.org>
 */
public class XSGYear extends XSAnySimpleType {
	
	private static final long serialVersionUID = 4500652479945465195L;

	private static XSInteger year;
	
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
		if (gYearStrValue.length() != 4) {
		    throw new TransformerException("FOCA0003 : An xs:gYear value cannot be "
		    		                                                   + "constructed from string value " + gYearStrValue);
		}
		else {
			this.year = new XSInteger(gYearStrValue);		
		}
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
		String resultStr = null;
		
		String yearStr = year.stringValue();
		
		resultStr = yearStr;
		
		return resultStr;
	}
	
	/**
	 * Get the year component from this xs:gYear object instance. 
	 */
	public XSInteger getYear() {
		XSInteger yearValue = null;
		
		BigDecimal bigDecimal = year.getValue();
		yearValue = new XSInteger(BigInteger.valueOf(bigDecimal.longValue()));
		
		return yearValue;
	}
	
	public int getType() {
		return CLASS_GYEAR;
	}
	
	/**
	 * Implementation of operation equals, for the type xs:gYear.
	 */
	public XSBoolean eq(XSGYear value) {
		XSBoolean result = null;
		
		XSInteger thisYear = this.getYear();
		XSInteger argYear = value.getYear();
		
		boolean boolResult = (thisYear.intValue() == argYear.intValue());
		
		result = new XSBoolean(boolResult);
		
		return result;
	}
	
	/**
	 * Implementation of operation not equals, for the type xs:gYear.
	 */
	public XSBoolean ne(XSGYear value) {
        XSBoolean result = null;
		
		XSInteger thisYear = this.getYear();
		XSInteger argYear = value.getYear();
		
		boolean boolResult = (thisYear.intValue() != argYear.intValue());
		
		result = new XSBoolean(boolResult);
		
		return result;
	}

}
