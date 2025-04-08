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
import org.apache.xpath.functions.WrongNumberArgsException;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.res.XPATHErrorResources;

import xml.xpath31.processor.types.XSBoolean;

/**
 * Implementation of XPath 3.1 function fn:contains.
 * 
 * @xsl.usage advanced
 */
public class FuncContains extends XSL3StringCollationAwareFunction
{
  
  static final long serialVersionUID = 5084753781887919723L;
  
  /**
   * The number of arguments passed to the fn:contains function 
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
	    
	    String arg0StrValue = getArgStringValue(xctxt, m_arg0);	    
	    String arg1StrValue = getArgStringValue(xctxt, m_arg1);
	    
	    if (numOfArgs == 2) {
	    	if ((arg0StrValue.length() == 0) && (arg1StrValue.length() == 0)) {
	    		result = new XSBoolean(true);
	    	}
	    	else {	
	    		int index = arg0StrValue.indexOf(arg1StrValue);
	    		result = ((index > -1) ? new XSBoolean(true) : new XSBoolean(false));
	    	}
	    }
	    else {
	    	// A collation uri was, explicitly provided during the function 
	    	// call fn:contains.

	    	XPathCollationSupport xPathCollationSupport = xctxt.getXPathCollationSupport();	    	
	    	String collationUri = XslTransformEvaluationHelper.getStrVal(m_arg2.execute(xctxt));

	    	result = new XSBoolean(false);

	    	int arg0StrLength = arg0StrValue.length();
	    	for (int idx = 0; idx < arg0StrLength; idx++) {
	    		int temp = idx;
	    		for (int idx2 = temp; idx2 < arg0StrLength; idx2++) {
	    			String tempStr = arg0StrValue.substring(temp, idx2 + 1);
	    			int comparisonResult = xPathCollationSupport.compareStringsUsingCollation(tempStr, arg1StrValue, collationUri);
	    			if (comparisonResult == 0) {
	    				result = new XSBoolean(true);        		 
	           		    break;
	    			}
	    		}
	    		
	    		if (((XSBoolean)result).bool()) {
	    		   break;
	    		}
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
