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
package org.apache.xpath.functions.string;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.Function2Args;
import org.apache.xpath.objects.XObject;

import xml.xpath31.processor.types.XSBoolean;

/**
 * Implementation of fn:Contains function.
 * 
 * @xsl.usage advanced
 */
public class FuncContains extends Function2Args
{
    static final long serialVersionUID = 5084753781887919723L;

  /**
   * Execute the function. The function must return a valid object.
   * 
   * @param xctxt The current execution context.
   * @return A valid XObject.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
  {    
	    XObject result = null;
	    
	    String str1 = XslTransformEvaluationHelper.getStrVal(m_arg0.execute(xctxt));
	    String str2 = XslTransformEvaluationHelper.getStrVal(m_arg1.execute(xctxt));
	
	    // Add this check for JDK consistency for empty strings
	    if ((str1.length() == 0) && (str2.length() == 0)) {
	        result = new XSBoolean(true);
	    }
	    else {	
	       int index = str1.indexOf(str2);
	       result = ((index > -1) ? new XSBoolean(true) : new XSBoolean(false));
	    }
	
	    return result;
  }
}
