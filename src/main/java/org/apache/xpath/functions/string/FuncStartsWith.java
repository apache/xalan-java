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
import org.apache.xpath.axes.SelfIteratorNoPredicate;
import org.apache.xpath.functions.Function2Args;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XObject;

/**
 * Implementation of XPath fn:starts-with function.
 * 
 * @xsl.usage advanced
 */
public class FuncStartsWith extends Function2Args
{
    static final long serialVersionUID = 2194585774699567928L;

  /**
   * Execute the function. The function must return
   * a valid object.
   * 
   * @param xctxt The current execution context.
   * @return A valid XObject.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
  {
	  XObject result = null;
    
	  XObject xpath3ContextItem = xctxt.getXPath3ContextItem();
	  
	  XObject arg1XObj = m_arg1.execute(xctxt);
	  String arg1StrValue = XslTransformEvaluationHelper.getStrVal(arg1XObj);

	  if ((m_arg0 instanceof SelfIteratorNoPredicate) && (xpath3ContextItem != null)) {
		  String arg0StrValue = XslTransformEvaluationHelper.getStrVal(xpath3ContextItem);		  
		  result = arg0StrValue.startsWith(arg1StrValue) ? XBoolean.S_TRUE : XBoolean.S_FALSE;   
	  }
	  else {
		  XObject xpathEvalResult = m_arg0.execute(xctxt);
		  String arg0StrValue = XslTransformEvaluationHelper.getStrVal(xpathEvalResult);
		  result = arg0StrValue.startsWith(arg1StrValue) ? XBoolean.S_TRUE : XBoolean.S_FALSE;
	  }
    
      return result;
  }
}
