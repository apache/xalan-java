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
/*
 * $Id$
 */
package org.apache.xpath.xs.types;

import org.apache.xpath.objects.XObject;

/**
 * Base class for all the XML Schema types.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public abstract class XSAnyType extends XObject {
	
    private static final long serialVersionUID = -3385975335330221518L;

    /**
	 * Get the datatype's name, for e.g "xs:boolean".
	 * 
	 * @return datatype's name
	 */
	public abstract String stringType();

	/**
	 * Get the string representation of the value stored.
	 * 
	 * @return get the string representation of the, value 
	 *         stored adhering to this type.
	 */
	public abstract String stringValue();
	
	public void appendToFsb(org.apache.xml.utils.FastStringBuffer fsb) {
	   fsb.append(stringValue());
	} 
	
}
