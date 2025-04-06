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
import org.apache.xpath.functions.Function3Args;
import org.apache.xpath.objects.XObject;

import xml.xpath31.processor.types.XSString;

/**
 * Implementation of XPath 3.1 fn:translate() function.
 * 
 * @xsl.usage advanced
 */
public class FuncTranslate extends Function3Args
{
  
   static final long serialVersionUID = -1672834340026116482L;

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
	  
	  String theFirstString = XslTransformEvaluationHelper.getStrVal(m_arg0.execute(xctxt));
	  String theSecondString = XslTransformEvaluationHelper.getStrVal(m_arg1.execute(xctxt));
	  String theThirdString = XslTransformEvaluationHelper.getStrVal(m_arg2.execute(xctxt));
	  
	  int theFirstStringLength = theFirstString.length();
	  int theThirdStringLength = theThirdString.length();

	  // A StringBuffer object to contain the new characters. We'll use it to construct
	  // the result string.
	  StringBuffer strBuf = new StringBuffer();

	  for (int i = 0; i < theFirstStringLength; i++)
	  {
		  char theCurrentChar = theFirstString.charAt(i);
		  int theIndex = theSecondString.indexOf(theCurrentChar);

		  if (theIndex < 0)
		  {
			  // We didn't find the character in the second string, therefore 
			  // it's not translated.
			  strBuf.append(theCurrentChar);
		  }
		  else if (theIndex < theThirdStringLength)
		  {
			  // There's a corresponding character in the third string, 
			  // therefore we do the translation.
			  strBuf.append(theThirdString.charAt(theIndex));
		  }
		  else
		  {
			  // There's no corresponding character in the
			  // third string, since it's shorter than the
			  // second string. In this case, the character
			  // is removed from the output string, so don't
			  // do anything.
		  }
	  }

	  return new XSString(strBuf.toString());
	  
  }
}
