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
package org.apache.xpath.composite;

import javax.xml.transform.TransformerException;

import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;

/**
 * An object of this class is, constructed by an XPath parser
 * to represent a named function reference expression.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XPathNamedFunctionReference extends XObject {

	private static final long serialVersionUID = -2448144139309343329L;
	
	private String funcNamespace = null;	
	private String funcName = null;	
	private int funcArity = -1;

	@Override
    public XObject execute(XPathContext xctxt) throws TransformerException {
		// The run-time execution of this function, shall 
		// not reach this method invocation. The following implementation
		// of this method is only notional.
		XObject result = null;		
		result = new ResultSequence();		
		return result;
    }

	public String getFuncNamespace() {
		return funcNamespace;
	}

	public void setFuncNamespace(String funcNamespace) {
		this.funcNamespace = funcNamespace;
	}

	public String getFuncName() {
		return funcName;
	}

	public void setFuncName(String funcName) {
		this.funcName = funcName;
	}

	public int getFuncArity() {
		return funcArity;
	}

	public void setFuncArity(int funcArity) {
		this.funcArity = funcArity;
	}
	
	public void callVisitors(ExpressionOwner owner, XPathVisitor visitor) {
        // no op
    }
    
    public int getType()
    {
      return CLASS_FUNCTION_ITEM;
    }

}
