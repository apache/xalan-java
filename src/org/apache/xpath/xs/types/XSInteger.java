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

import java.math.BigDecimal;
import java.math.BigInteger;

import org.apache.xpath.objects.ResultSequence;

/**
 * An XML Schema data type representation, of the xs:integer datatype.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XSInteger extends XSDecimal {

    private static final long serialVersionUID = -4634168510820898744L;

    private static final String XS_INTEGER = "xs:integer";
	
	private BigInteger _value;

	/*
	 * Class constructor.
	 */
	public XSInteger() {
		this(BigInteger.valueOf(0));
	}

	/*
     * Class constructor.
     */
	public XSInteger(BigInteger val) {
		super(new BigDecimal(val));
		_value = val;
	}

	/*
     * Class constructor.
     */
	public XSInteger(String val) {
		super(new BigDecimal(val));
		_value = new BigInteger(val);
	}

	public String stringType() {
		return XS_INTEGER;
	}

	public String typeName() {
		return "integer";
	}

	/**
	 * Get a string representation of an integer value stored, 
	 * within this object.
	 * 
	 * @return   string representation of the integer value stored
	 */
	public String stringValue() {
		return _value.toString();
	}

	/**
     * Check if this XSInteger object represents the value 0.
     * 
     * @return    true if this XSInteger object represents the value 0. 
     *            false otherwise.
     */
	public boolean zero() {
		return (_value.compareTo(BigInteger.ZERO) == 0);
	}

	public ResultSequence constructor(ResultSequence arg) {
	    // to do
        return null;
	}

	/**
     * Get the actual value of an integer number stored within 
     * this object.
     * 
     * @return   the actual value of the number stored
     */
	public BigInteger intValue() {
		return _value;
	}

	/**
     * Set the numeric integer value, within this object.
     * 
     * @param val    number to be stored
     */
	public void setInt(BigInteger val) {
		_value = val;
	}

}
