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
import org.apache.xpath.Expression;
import org.apache.xpath.XPathCollationSupport;
import org.apache.xpath.XPathContext;
import org.apache.xpath.axes.SelfIteratorNoPredicate;
import org.apache.xpath.functions.FunctionMultiArgs;
import org.apache.xpath.functions.WrongNumberArgsException;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.res.XPATHErrorResources;

/**
 * Implementation of XPath 3.1 function fn:starts-with.
 * 
 * @xsl.usage advanced
 */
public class FuncStartsWith extends FunctionMultiArgs
{
  
  static final long serialVersionUID = 2194585774699567928L;
  
  /**
   * The number of arguments passed to the fn:starts-with function 
   * call.
   */
  private int numOfArgs = 0;

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
	  
	  Expression arg0 = m_arg0;        
      Expression arg1 = m_arg1;        
      Expression arg2 = null;
      
      XPathCollationSupport xPathCollationSupport = xctxt.getXPathCollationSupport();
	  
      if (numOfArgs == 2) {
    	  XObject arg1XObj = arg1.execute(xctxt);
    	  String arg1StrValue = XslTransformEvaluationHelper.getStrVal(arg1XObj);

    	  if ((arg0 instanceof SelfIteratorNoPredicate) && (xpath3ContextItem != null)) {
    		  String arg0StrValue = XslTransformEvaluationHelper.getStrVal(xpath3ContextItem);		  
    		  result = arg0StrValue.startsWith(arg1StrValue) ? XBoolean.S_TRUE : XBoolean.S_FALSE;   
    	  }
    	  else {
    		  XObject xpathEvalResult = arg0.execute(xctxt);
    		  String arg0StrValue = XslTransformEvaluationHelper.getStrVal(xpathEvalResult);
    		  result = arg0StrValue.startsWith(arg1StrValue) ? XBoolean.S_TRUE : XBoolean.S_FALSE;
    	  }
      }
      else {
    	  // A collation uri was, explicitly provided during the function 
      	  // call fn:starts-with.
          
          arg2 = m_arg2;
          
          XObject xObject0 = arg0.execute(xctxt);
          XObject xObject1 = arg1.execute(xctxt);
          
          XObject xObject2 = arg2.execute(xctxt);
          
          String str0 = XslTransformEvaluationHelper.getStrVal(xObject0);
          String str1 = XslTransformEvaluationHelper.getStrVal(xObject1);
          
          String collationUri = XslTransformEvaluationHelper.getStrVal(xObject2);
          
          result = XBoolean.S_FALSE;
          
          for (int idx = 0; idx < str0.length(); idx++) {
        	 String str0Prefix = str0.substring(0, idx + 1);
        	 int comparisonResult = xPathCollationSupport.compareStringsUsingCollation(str0Prefix, str1, collationUri);
        	 if (comparisonResult == 0) {
        		 result = XBoolean.S_TRUE;        		 
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
