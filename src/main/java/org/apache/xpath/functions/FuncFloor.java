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

import javax.xml.transform.SourceLocator;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathInlineFunction;
import org.apache.xpath.patterns.NodeTest;

/** 
 * Implementation of an XPath 3.1 function fn:floor.
 * 
 * @xsl.usage advanced
 */
public class FuncFloor extends FunctionDef1Arg
{
    static final long serialVersionUID = 2326752233236309265L;
    
    /**
	 * Class constructor.
	 */
	public FuncFloor() {
		m_defined_arity = new Short[] { 1 };
	}

  /**
   * Evaluate the function. The function must return a valid object.
   * 
   * @param xctxt The current execution context
   * @return A valid XObject
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
  {
	  
	  XObject result = null;

	  SourceLocator srcLocator = xctxt.getSAXLocator();

	  if (m_arg0 instanceof NodeTest) {
		  if (XslTransformEvaluationHelper.isNodeTestExpressionFuntionType((NodeTest)m_arg0)) {
			  throw new javax.xml.transform.TransformerException("FOTY0013 : An atomic value is required for the first argument of XPath function floor(), but the "
					  																  + "supplied type is a function type, which cannot be atomized.", srcLocator); 
		  }
	  }
	  else if (m_arg0 instanceof XPathInlineFunction) {
		  throw new javax.xml.transform.TransformerException("FOTY0013 : An atomic value is required for the first argument of XPath function floor(), but the "
                                                                                      + "supplied type is a function type, which cannot be atomized.", srcLocator);
	  }

	  String strValueOfArg = (getArg0AsString(xctxt)).toString();

	  result = new XNumber(Math.floor(Double.valueOf(strValueOfArg))); 

	  return result;
  }
}
