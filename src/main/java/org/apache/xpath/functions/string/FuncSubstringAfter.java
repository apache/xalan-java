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
package org.apache.xpath.functions.string;

import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xpath.XPathCollationSupport;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.FunctionMultiArgs;
import org.apache.xpath.functions.WrongNumberArgsException;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.res.XPATHErrorResources;

import xml.xpath31.processor.types.XSString;

/**
 * Implementation of XPath 3.1 function fn:substring-after.
 * 
 * @xsl.usage advanced
 */
public class FuncSubstringAfter extends FunctionMultiArgs
{
   
	static final long serialVersionUID = -8119731889862512194L;
    
    /**
     * The number of arguments passed to the fn:substring-after function 
     * call.
     */
    private int numOfArgs = 0;

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
	  
	  XPathCollationSupport xPathCollationSupport = xctxt.getXPathCollationSupport();
	    
	  if (numOfArgs == 2) {
		 int index = str1.indexOf(str2);
		 
		 result = ((index == -1) ? new XSString("") : new XSString(str1.substring(index + str2.length()))); 
	  }
	  else {
		  // A collation uri was, explicitly provided during the function 
		  // call fn:substring-before.

		  String collationUri = XslTransformEvaluationHelper.getStrVal(m_arg2.execute(xctxt));

		  int str1Length = str1.length();
		  boolean subsMatchFound = false;
		  for (int idx = 0; idx < str1Length; idx++) {
			  int temp = idx;			  
			  for (int idx2 = temp; idx2 < str1Length; idx2++) {
				  String tempStr = str1.substring(temp, idx2 + 1);
				  int comparisonResult = xPathCollationSupport.compareStringsUsingCollation(tempStr, str2, collationUri);
				  if (comparisonResult == 0) {
					  subsMatchFound = true;        		 
					  break;
				  }
			  }

			  if (subsMatchFound) {
				  result = new XSString(str1.substring((temp + 1) + str2.length())); 
				  break;
			  }
		  }
		  
		  if (!subsMatchFound) {
			 result = new XSString("");  
		  }
	  }
	  
	  return result;
	  
  }
  
  /**
   * Check that the number of arguments passed to this function is correct.
   *
   * @param argNum The number of arguments that is being passed to the function.
   *
   * @throws WrongNumberArgsException
   */
  public void checkNumberArgs(int argNum) throws WrongNumberArgsException
  {
	  if (!(argNum > 1 && argNum <= 3)) {
		  reportWrongNumberArgs();
	  }
	  else {
		  numOfArgs = argNum;   
	  }
  }
  
  /**
   * Constructs and throws a WrongNumberArgException with the appropriate
   * message for this function object.
   *
   * @throws WrongNumberArgsException
   */
  protected void reportWrongNumberArgs() throws WrongNumberArgsException {
      throw new WrongNumberArgsException(XSLMessages.createXPATHMessage(
                                                                   XPATHErrorResources.ER_TWO_OR_THREE, 
                                                                   null));
  }
  
}
