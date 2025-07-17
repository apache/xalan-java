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
package org.apache.xalan.templates;

import org.apache.xml.utils.QName;

/**
 * A class definition, whose object instance uniquely identifies 
 * an xsl:function definition.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XslFunctionDefinitionKey {
	
	/**
	 * An xsl:function definition's name.
	 */
	private QName m_name = null;
	
	/**
	 * An xsl:function definition's arity.
	 */
	private int m_arity;
	
	private boolean m_isOverride;
	
	/**
	 * Class constructor.
	 * 
	 * @param name				   An xsl:function definition's name
	 * @param arity				   An xsl:function definition's arity
	 * @param isOverride		   An xsl:function definition's override value
	 */
	public XslFunctionDefinitionKey(QName name, int arity, boolean isOverride) {
		this.m_name = name;
		this.m_arity = arity;
		this.m_isOverride = isOverride; 
	}

	public QName getName() {
		return m_name;
	}

	public void setName(QName name) {
		this.m_name = name;
	}

	public int getArity() {
		return m_arity;
	}

	public void setArity(int arity) {
		this.m_arity = arity;
	}
	
	public boolean getOverride() {
		return m_isOverride;
	}

	public void setOverride(boolean bool) {
		this.m_isOverride = bool;
	}
	
	public boolean equals(Object obj2) {
		boolean result = false;
		
		if (obj2 instanceof XslFunctionDefinitionKey) {
		    XslFunctionDefinitionKey funcDefnKey = (XslFunctionDefinitionKey)obj2;
		    QName funcName = funcDefnKey.getName();
		    int arity = funcDefnKey.getArity();
		    boolean isOverride = funcDefnKey.getOverride(); 
		    if (funcName.equals(this.m_name) && (arity == this.m_arity) && (isOverride == this.m_isOverride)) {
		       result = true;
		    }
		}
		
		return result;
	}
	
	public int hashCode() {
		int result;
		
		int qNameHashCode = (this.m_name).hashCode();
		int arityHashCode = (Integer.valueOf(this.m_arity)).hashCode();
		int isOverrideHashCode = (Boolean.valueOf(this.m_isOverride)).hashCode();
				
		long sum1 = (long)(qNameHashCode + arityHashCode + isOverrideHashCode);
		if (sum1 <= Integer.MAX_VALUE) {
			result = (int)sum1; 
		}
		else {
			result = ((int)sum1 / 2); 
		}  
		
		return result;
	}

}
