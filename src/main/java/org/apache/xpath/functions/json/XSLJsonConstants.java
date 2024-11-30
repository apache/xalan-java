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
package org.apache.xpath.functions.json;

/**
 * A class, specifying constant values needed by Xalan-J's
 * XSL JSON support.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XSLJsonConstants {		
	
	public static final String MAP = "map";
	
	public static final String ARRAY = "array";
	
	public static final String STRING = "string";
	
	public static final String NUMBER = "number";
	
	public static final String BOOLEAN = "boolean";
	
	public static final String NULL = "null";
	
	public static final String KEY = "key";
	
	public static final String LIBERAL = "liberal";
	
	public static final String DUPLICATES = "duplicates";
	
	public static final String DUPLICATES_REJECT = "reject";
	
	public static final String DUPLICATES_USE_FIRST = "use-first";
	
	public static final String DUPLICATES_USE_LAST = "use-last";
	
	public static final String DUPLICATES_RETAIN = "retain";
		
	// This refers to an XML Schema document provided within Xalan-J 
	// XSL 3 codebase, as specified within XPath and XQuery F&O 3.1 spec.
	public static final String XML_JSON_SCHEMA_FILE_NAME = "schema-for-json.xsd";
	
	public static final int JSON_INDENT_FACTOR = 1;
	
}
