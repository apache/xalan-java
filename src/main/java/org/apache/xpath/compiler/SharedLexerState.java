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
package org.apache.xpath.compiler;

import java.util.Map;

/**
 * A class definition to support processing of Xalan-J's 
 * lexer.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 */
public class SharedLexerState {
	
	/**
	 * A java.util.Map object, to contain XML namespace bindings from 
	 * included XSL stylesheet's xsl:stylesheet element via xsl:include 
	 * instruction.
	 */
	public static Map<String, String> m_nsMap;
	
	/**
	 * Reset values of variables contained within this class.
	 */
	public static void reset() {
		if (m_nsMap != null) {
		   m_nsMap.clear();
		}
	}

}
