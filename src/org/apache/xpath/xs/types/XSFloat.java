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
 * An XML Schema data type representation, of the xs:float datatype.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XSFloat extends XSNumericType {

    private static final long serialVersionUID = 7301295458418107791L;

    private static final String XS_FLOAT = "xs:float";
	
	private Float _value;
	
	private XPath3DecimalFormat xpath3DecimalFormat = new XPath3DecimalFormat(
	                                                                   "0.#######E0");
	
	/*
	 * Class constructor.
	 */
	public XSFloat(float x) {
		_value = new Float(x);
	}

	/*
     * Class constructor.
     */
	public XSFloat() {
		this(0);
	}

	/*
     * Class constructor.
     */
	public XSFloat(String val) {
		try {
	       if (val.equals("-INF")) {
			  _value = new Float(Float.NEGATIVE_INFINITY);
		   } else if (val.equals("INF")) {
			  _value = new Float(Float.POSITIVE_INFINITY);
		   } else {
		      _value = new Float(val);
		   }
		} catch (NumberFormatException ex) {
			// to do
		}
	}
	
	public String stringType() {
		return XS_FLOAT;
	}

	public String typeName() {
		return "float";
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
	
	@Override
    public ResultSequence constructor(ResultSequence arg) {
        ResultSequence resultSeq = new ResultSequence();
        
        if (arg.size() == 0) {
           return resultSeq;     
        }
        
        XSAnyType xsAnyType = (XSAnyType)arg.item(0);
        
        try {
            Float floatVal = null;
            
            if ((xsAnyType.stringValue()).equals("INF")) {
                floatVal = new Float(Float.POSITIVE_INFINITY);
            } 
            else if ((xsAnyType.stringValue()).equals("-INF")) {
                floatVal = new Float(Float.NEGATIVE_INFINITY);
            } 
            else if (xsAnyType instanceof XSBoolean) {
                if ((xsAnyType.stringValue()).equals("true")) {
                    floatVal = new Float("1.0E0");
                } else {
                    floatVal = new Float("0.0E0");
                }
            } 
            else {
                floatVal = new Float(xsAnyType.stringValue());
            }
            
            resultSeq.add(new XSFloat(floatVal.floatValue()));
        } catch (NumberFormatException e) {
            // to do
            return null;
        }
        
        return resultSeq;
    }

	/**
	 * Check whether, value of this numeric float object 
	 * represents NaN.
	 * 
	 * @return     true if this numeric float object represents NaN.
	 *             false otherwise.
	 */
	public boolean nan() {
		return Float.isNaN(_value.floatValue());
	}

	/**
	 * Check whether this float object, represents negative or positive 
	 * infinity.
	 * 
	 * @return    true is this float object represents infinity.
	 *            false otherwise.
	 */
	public boolean infinite() {
		return Float.isInfinite(_value.floatValue());
	}

	/**
	 * Check whether this numeric float object represents 0.
	 * 
	 * @return    true if this numeric float object represents 0.
	 *            false otherwise.
	 */
	public boolean zero() {
	   return (Float.compare(_value.floatValue(), 0) == 0);
	}
	
	/*
	 * Check whether this numeric float object, represents -0.
	 * 
	 * @return   true if this numeric float object represents -0.
	 *           false otherwise.
	 */
	public boolean negativeZero() {
	   return (Float.compare(_value.floatValue(), -0.0f) == 0);
	}
	
	/**
	 * Get the actual numeric float value stored, within this 
	 * object.
	 * 
	 * @return    the actual numeric float value stored
	 */
	public float floatValue() {
		return _value.floatValue();
	}
	
	public boolean equals(XSFloat xsFloat) {
        return _value.equals(xsFloat.floatValue()); 
    }
	
	public boolean lt(XSFloat xsFloat) {
	    return floatValue() < xsFloat.floatValue(); 
    }
	
	public boolean gt(XSFloat xsFloat) {
	    return floatValue() > xsFloat.floatValue(); 
    }
	
    public int getType() {
        return CLASS_XS_FLOAT;
    }
	
}
