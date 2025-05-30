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
package org.apache.xml.serializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Class definition to support, evaluation of xsl:character-map
 * instruction.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class CharacterMapConfig {
	
	// An java.util.HashMap object storing character's code point 
	// to replacement string value mapping. 
	Map<Integer, String> m_xslCharMap = new HashMap<Integer, String>();
	
	/**
	 * Class constructor.
	 */
	public CharacterMapConfig() {
		// NO OP
	}

	public void put(Integer codePoint, String strValue) {
		this.m_xslCharMap.put(codePoint, strValue);
	}

	public String get(Integer codePoint) {
		return this.m_xslCharMap.get(codePoint);
	}
	
	public void setCharMap(Map<Integer, String> xslCharMap) {
		this.m_xslCharMap = xslCharMap;
	}
	
	public Map<Integer, String> getCharMap() {
		return m_xslCharMap;
	}

}
