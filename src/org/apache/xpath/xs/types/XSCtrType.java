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

import org.apache.xpath.objects.ResultSequence;

/**
 * A representation of the XSCtrType datatype.
 * 
 * This data type is used for, XML Schema data type constructor functions.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public abstract class XSCtrType extends XSAnyAtomicType {

	/**
	 * Used for constructor functions.
	 * 
	 * @param arg   either an empty sequence, or an XML Schema atomic type
	 * 
	 * @return      the resulting ResultSequence
	 */
	public abstract ResultSequence constructor(ResultSequence arg);

	/**
	 * Get the datatype's name
	 * 
	 * @return String representation of the datatype's name
	 */
	public abstract String typeName();
	
}
