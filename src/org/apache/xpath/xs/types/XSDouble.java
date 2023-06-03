/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.xpath.xs.types;

import org.apache.xpath.objects.ResultSequence;

/**
 * An XML Schema data type representation, of the xs:double datatype.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XSDouble extends XSNumericType {

    private static final long serialVersionUID = -2666052244390163961L;

    private static final String XS_DOUBLE = "xs:double";
	
	private Double _value;
	
	private XPath3DecimalFormat xpath3DecimalFormat = new XPath3DecimalFormat(
	                                                              "0.################E0");

	/*
	 * Class constructor.
	 */
	public XSDouble(double val) {
	    _value = new Double(val);
	}

	/*
     * Class constructor.
     */
	public XSDouble() {
	    this(0);
	}

	/*
     * Class constructor.
     */
	public XSDouble(String val) {
		try {
			if (val.equals("-INF")) {
				_value = new Double(Double.NEGATIVE_INFINITY);
			} else if (val.equals("INF")) {
				_value = new Double(Double.POSITIVE_INFINITY);
			} else {
				_value = new Double(val);
			}
		} catch (NumberFormatException ex) {
			// to do
		}
	}

	/**
	 * Get a XSDouble object, corresponding to the string valued 
	 * argument provided.
	 * 
	 * @param str   string value, to be parsed to an XSDouble object
	 * 
	 * @return      an XSDouble object, corresponding to the string
	 *              argument provided.
	 */
	public static XSDouble parseDouble(String str) {	    
		try {
			Double d1 = null;
			
			if (str.equals("INF")) {
				d1 = new Double(Double.POSITIVE_INFINITY);
			} else if (str.equals("-INF")) {
				d1 = new Double(Double.NEGATIVE_INFINITY);
			} else {
				d1 = new Double(str);
			}
			
			return new XSDouble(d1.doubleValue());			
		} catch (NumberFormatException ex) {
			return null;
		}		
	}
	
	@Override
    public ResultSequence constructor(ResultSequence arg) {
        ResultSequence resultSeq = new ResultSequence();
        
        if (arg.size() == 0) {
           return resultSeq;     
        }
        
        XSAnyType xsAnyType = (XSAnyType)arg.item(0);
        
        XSDouble xsAnyTypeConvertedToDouble = null;
        
        if (xsAnyType instanceof XSBoolean) {
            if (xsAnyType.stringValue().equals("true")) {
                xsAnyTypeConvertedToDouble = new XSDouble(1.0E0);
            } else {
                xsAnyTypeConvertedToDouble = new XSDouble(0.0E0);
            }
        }
        else {
            xsAnyTypeConvertedToDouble = parseDouble(xsAnyType.stringValue());
        }
        
        resultSeq.add(xsAnyTypeConvertedToDouble);
        
        return resultSeq;
    }

    @Override
    public String typeName() {
        return "double";
    }

    @Override
    public String stringType() {
        return XS_DOUBLE;
    }

    @Override
    public String stringValue() {
        if (zero()) {
            return "0";
        }

        if (negativeZero()) {
            return "-0";
        }

        if (nan()) {
            return "NaN";
        }

        return xpath3DecimalFormat.performStrFormatting(_value);
    }
    
    /*
     * Check whether this XSDouble object represents -0.
     * 
     * @return    true if this XSDouble object represents -0.
     *            false otherwise.
     */
    public boolean negativeZero() {
        return (Double.compare(_value.doubleValue(), -0.0E0) == 0);
    }

    /**
     * Get the actual double primitive value, corresponding to 
     * this XSDouble object.
     */
    public double doubleValue() {
        return _value.doubleValue();
    }
    
    /**
     * Check whether this XSDouble object represents NaN.
     */
    public boolean nan() {
        return Double.isNaN(_value.doubleValue());
    }

    /**
     * Check whether this XSDouble object represents an 
     * infinite number.
     */
    public boolean infinite() {
        return Double.isInfinite(_value.doubleValue());
    }

    /**
     * Check whether this XSDouble object represents 0.
     */
    public boolean zero() {
        return (Double.compare(_value.doubleValue(), 0.0E0) == 0);
    }
    
    public boolean equals(XSDouble xsDouble) {
        return _value.equals(xsDouble.doubleValue()); 
    }
    
}
