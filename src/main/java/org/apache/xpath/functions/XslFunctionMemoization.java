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
package org.apache.xpath.functions;

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.TransformerException;

import org.apache.xml.utils.QName;
import org.apache.xpath.ExpressionNode;
import org.apache.xpath.objects.XObject;

/**
 * A class definition, to help implement xsl:function instruction's 
 * 'cache' attribute. 
 *
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XslFunctionMemoization {
	
	/**
	 * Class field, to reference xsl:function instruction's 
	 * name.
	 */
	private QName m_name;
	
	/**
	 * Class field, to reference xsl:function call's arguments.
	 */
	private List<XObject> m_argList;
	
	/**
	 * Class field, to reference xsl:function call's result.
	 */
	private XObject m_result;
	
	/**
	 * Class field, to reference XSL stylesheet's xsl:function 
	 * template element.
	 */
	private ExpressionNode m_exprOwner;
	
	/**
	 * Class field, to store cache of xsl:function results.
	 */
	public static List<XslFunctionMemoization> func_result_cache = new ArrayList<XslFunctionMemoization>();
	
	/**
	 * Class constructor.
	 */
	public XslFunctionMemoization(QName name, List<XObject> argList) {
		this.m_name = name;
		this.m_argList = argList;
	}
	
	/**
	 * Class constructor.
	 */
	public XslFunctionMemoization(QName name, List<XObject> argList, 
			                                                   XObject result, ExpressionNode exprOwner) {
	   this.m_name = name;
	   this.m_argList = argList;
	   this.m_result = result;
	   this.m_exprOwner = exprOwner; 
	}		
	
	public boolean equals(Object obj2) {		
		
		boolean result = true;
		
		try {
			if (obj2 instanceof XslFunctionMemoization) {
				XslFunctionMemoization funcMemoize2 = (XslFunctionMemoization)obj2;
				QName qName2 = funcMemoize2.getName();
				List<XObject> argList2 = funcMemoize2.getArgList();
				if (qName2.equals(this.m_name)) {
					int size1 = m_argList.size();
					int size2 = argList2.size();
					for (int idx1 = 0; idx1 < size1; idx1++) {
						XObject xObj1 = m_argList.get(idx1);
						for (int idx2 = 0; idx2 < size2; idx2++) {
							XObject xObj2 = argList2.get(idx2);
							if (!xObj1.vcEquals(xObj2, m_exprOwner, null, true)) {
								result = false;

								break;
							}
						}

						if (!result) {
							break;	
						}
					}
				}
			}
	    }
	    catch (TransformerException ex) {
	    	// no op
	    }
	    
		return result;
	}
	
	public int hashCode() {
		int result = 0;
		
		result = m_name.hashCode() + m_argList.hashCode();  
		
		return result;
	}

	public QName getName() {
		return m_name;
	}

	public void setName(QName name) {
		this.m_name = name;
	}

	public List<XObject> getArgList() {
		return m_argList;
	}

	public void setArgList(List<XObject> argList) {
		this.m_argList = argList;
	}

	public XObject getResult() {
		return m_result;
	}

	public void setResult(XObject result) {
		this.m_result = result;
	}

	public ExpressionNode getExprOwner() {
		return m_exprOwner;
	}

	public void setExprOwner(ExpressionNode exprOwner) {
		this.m_exprOwner = exprOwner;
	}
		
}
