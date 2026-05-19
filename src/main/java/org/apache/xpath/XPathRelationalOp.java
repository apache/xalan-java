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
package org.apache.xpath;

import java.util.Arrays;
import java.util.List;

import org.apache.xpath.operations.Operation;

public class XPathRelationalOp extends Operation {

	private static final long serialVersionUID = 8090025880595978756L;
	
	/**
	 * Method definition, to check whether an XML Schema 1.0, 
	 * supplied built-in type name is numeric.
	 * 
	 * @param typeName					  The supplied XML Schema type 
	 *                                    name string.
	 * @return                            Boolean value true or false
	 */
	protected boolean isXsBuiltInTypeNumeric(java.lang.String typeName) {

		boolean result = false;

		java.lang.String[] built_in_xs1_numeric_type_arr = new java.lang.String[] { "decimal", "double", "float", "integer", "long", 
																					"int", "short", "byte", "nonNegativeInteger", "unsignedLong",
																					"unsignedInt", "unsignedShort", "unsignedByte", "positiveInteger",
																					"nonPositiveInteger", "negativeInteger"};
		List<java.lang.String> strList = Arrays.asList(built_in_xs1_numeric_type_arr);
		if (strList.contains(typeName)) {
			result = true; 
		}

		return result;
	}

}
