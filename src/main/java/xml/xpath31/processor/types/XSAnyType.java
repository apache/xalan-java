/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the "License");
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
package xml.xpath31.processor.types;

import org.apache.xml.utils.FastStringBuffer;
import org.apache.xpath.objects.XObject;

/**
 * Base class for all the XML Schema types.
 *
 * <p>(please refer,
 * <a href="https://www.w3.org/TR/xmlschema11-2/#built-in-datatypes">https://www.w3.org/TR/xmlschema11-2/#built-in-datatypes</a>
 * that illustrates the XML Schema 1.1 built-in datatypes hierarchy)</p>
 */
public abstract class XSAnyType extends XObject {
	
    private static final long serialVersionUID = -3385975335330221518L;

    /**
	 * Get the datatype's name. For e.g "xs:boolean", "xs:decimal".
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
	
	public void appendToFsb(FastStringBuffer fsb) {
	   fsb.append(stringValue());
	} 
	
}
