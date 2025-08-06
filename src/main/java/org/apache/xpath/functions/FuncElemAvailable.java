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
/*
 * $Id$
 */
package org.apache.xpath.functions;

import org.apache.xalan.templates.Constants;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xml.utils.QName;
import org.apache.xpath.ExtensionsProvider;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XObject;

/**
 * Implementation of XSLT 3.0 function fn:element-available.
 * 
 * An fn:element-available function can check for, both
 * stylesheet elements in XSLT namespace or in other extension 
 * namespaces.
 * 
 * @xsl.usage advanced
 */
public class FuncElemAvailable extends FunctionOneArg
{
   static final long serialVersionUID = -472533699257968546L;

  /**
   * Implementation of the function. The function must return
   * a valid object.
   * 
   * @param xctxt The current execution context.
   * @return A valid XObject.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
  {
    
    String namespace = null;
    String methName = null;

    String fullName = m_arg0.execute(xctxt).str();
    
    if (fullName.startsWith("Q{")) {
    	 // Support for XPath 3.1 URI qualified names 
		 int i = fullName.indexOf('}');
		 if (i > 2) {
		    namespace = fullName.substring(2, i);
		    methName = fullName.substring(i + 1);
		 }		 		 
	}
    else {
    	int indexOfNSSep = fullName.indexOf(':');
    	String prefix = null;    	
    	if (indexOfNSSep < 0)
    	{
    		prefix = "";
    		namespace = Constants.S_XSLNAMESPACEURL;
    		methName = fullName;
    	}
    	else
    	{
    		prefix = fullName.substring(0, indexOfNSSep);
    		namespace = xctxt.getNamespaceContext().getNamespaceForPrefix(prefix);
    		if (null == namespace)
    		   return XBoolean.S_FALSE;
    		methName = fullName.substring(indexOfNSSep + 1);
    	}
    }

    if ((Constants.S_XSLNAMESPACEURL).equals(namespace) || 
    	(Constants.S_BUILTIN_EXTENSIONS_URL).equals(namespace))
    {
    	try
    	{
    		TransformerImpl transformer = (TransformerImpl) xctxt.getOwnerObject();
    		return transformer.getStylesheet().getAvailableElements().containsKey(
															    				new QName(namespace, methName))
															    				? XBoolean.S_TRUE : XBoolean.S_FALSE;
    	}
    	catch (Exception e)
    	{
    		return XBoolean.S_FALSE;
    	}
    }
    else
    {
    	//dml
    	ExtensionsProvider extProvider = (ExtensionsProvider)xctxt.getOwnerObject();

    	return extProvider.elementAvailable(namespace, methName) ? XBoolean.S_TRUE : XBoolean.S_FALSE;
    }
  }
}
