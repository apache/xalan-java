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

import java.util.Map;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xml.utils.QName;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathEQname;

import xml.xpath31.processor.types.XSBoolean;

/**
 * Implementation of an XPath 3.1 function fn:type-available.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncTypeAvailable extends FunctionOneArg
{
    
    private static final long serialVersionUID = 7413544938433476596L;

	/**
     * Default constructor.
     */
    public FuncTypeAvailable() {
    	m_arity = new Short[] { 1 };	
    }

    /**
     * Evaluate the function. The function must return a valid object.
     * 
     * @param xctxt                        An XPath context object
     * @return                             A valid XObject
     *
     * @throws javax.xml.transform.TransformerException
     */
    public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
    {
    	XObject result = null;
    	
    	XObject xObj0 = getFunctionArgEffectiveValue(m_arg0, xctxt);
    	
    	String str1 = XslTransformEvaluationHelper.getStrVal(xObj0);
    	
    	// Ref: Class XSLTAttributeDef, method processUriQualifiedName()
    	str1 = str1.replaceAll("a1_([0-9]{4}):", "");
    	
    	Map<QName, XSTypeDefinition> xsInScopeSchemaTypes = xctxt.getInScopeSchemaTypes();
    	
    	QName typeQName = XPathEQname.parseEQname(str1, xctxt.getNamespaceContext());
    	
    	if (xsInScopeSchemaTypes.get(typeQName) != null) {
    	   result = new XSBoolean(true);
    	}
    	else {
    	   result = new XSBoolean(false);
    	}

    	return result;    
    }
  
}
