/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the  "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.xpath.xs.types;

import java.math.BigInteger;

import org.apache.xpath.objects.ResultSequence;

/**
 * An XML Schema data type representation, of the xs:int datatype.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XSInt extends XSLong {

    private static final long serialVersionUID = -6853519104620633955L;
    
    private static final String XS_INT = "xs:int";
    
	/*
	 * Class constructor.
	 */
	public XSInt() {
	  this(BigInteger.valueOf(0));
	}
	
	/*
     * Class constructor.
     */
	public XSInt(BigInteger val) {
		super(val);
	}
	
	@Override
    public ResultSequence constructor(ResultSequence arg) {
       // to do
       return null;
    }
	
	public String stringType() {
		return XS_INT;
	}
	
	public String typeName() {
		return "int";
	}
	
}
