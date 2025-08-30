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
package org.apache.xpath.functions.string;

import javax.xml.transform.SourceLocator;

import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathCollationSupport;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.FuncArgPlaceholder;
import org.apache.xpath.functions.WrongNumberArgsException;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.operations.Variable;
import org.apache.xpath.res.XPATHErrorResources;

/**
 * Implementation of XPath 3.1 function fn:ends-with.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncEndsWith extends XSL3StringCollationAwareFunction
{
	
  static final long serialVersionUID = -4632201923330818748L;
  
  /**
   * Class constructor.
   */
  public FuncEndsWith() {
	 m_defined_arity = new Short[] { 2, 3 };
  }
  
  /**
   * The number of arguments passed to the fn:ends-with function 
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
	  
	  SourceLocator srcLocator = xctxt.getSAXLocator();
	  
	  /**
	   * An XPath expression FuncArgPlaceholder if not null, for one or more of
	   * the function arguments, signifies that the corresponding function argument
	   * has been specified as ? i.e the function call is a partial function
	   * application. In this case, fn:ends-with function returns its result
	   * as an instance of XPathInlineFunction object.
	   */

	  String arg0StrValue = null;	        
	  if ((m_arg0 != null) && !(m_arg0 instanceof FuncArgPlaceholder)) {
		  if (m_arg0 instanceof Variable) {
			  XObject obj1 = m_arg0.execute(xctxt);
			  arg0StrValue = XslTransformEvaluationHelper.getStrVal(obj1);
		  }
		  else {
			  arg0StrValue = getArgStringValue(xctxt, m_arg0);
		  }
	  }

	  String arg1StrValue = null;	        
	  if ((m_arg1 != null) && !(m_arg1 instanceof FuncArgPlaceholder)) {
		  if (m_arg1 instanceof Variable) {
			  XObject obj1 = m_arg1.execute(xctxt);
			  Object object1 = obj1.object();
			  if (!(object1 instanceof FuncArgPlaceholder)) {
				  arg1StrValue = XslTransformEvaluationHelper.getStrVal(obj1);
			  }	    		
		  }
		  else {
			  arg1StrValue = getArgStringValue(xctxt, m_arg1);
		  }
	  }

	  String collationUri = null;	        
	  if ((m_arg2 != null) && !(m_arg2 instanceof FuncArgPlaceholder)) {
		  if (m_arg2 instanceof Variable) {
			  XObject obj1 = m_arg2.execute(xctxt);
			  Object object1 = obj1.object();
			  if (!(object1 instanceof FuncArgPlaceholder)) {
				  collationUri = XslTransformEvaluationHelper.getStrVal(obj1);
			  }	    		
		  }
		  else {
			  collationUri = getArgStringValue(xctxt, m_arg2);
		  }
	  }
	        
	  XPathCollationSupport xPathCollationSupport = xctxt.getXPathCollationSupport();
	    
	  if ((arg0StrValue != null) && (arg1StrValue != null)) {	    	
		  if (numOfArgs == 2) {
			  result = arg0StrValue.endsWith(arg1StrValue) ? XBoolean.S_TRUE : XBoolean.S_FALSE;
		  }
		  else if (collationUri != null) {
			  result = XBoolean.S_FALSE;
			  
			  int str0Length = arg0StrValue.length();
			  for (int idx = 0; idx < str0Length; idx++) {
				  String str0Suffix = arg0StrValue.substring(str0Length - (idx + 1), str0Length);
				  int comparisonResult = xPathCollationSupport.compareStringsUsingCollation(str0Suffix, arg1StrValue, collationUri);
				  if (comparisonResult == 0) {
					  result = XBoolean.S_TRUE;
					  
					  break;
				  }        			 
			  } 	
		  }
		  else {
			  String xpathInlineFuncExprStr = "function($collation) { ends-with('" + arg0StrValue + "', '" + arg1StrValue + "', $collation) }";

			  XPath xpathObj = new XPath(xpathInlineFuncExprStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);

			  result = xpathObj.execute(xctxt, DTM.NULL, null);
		  }
	  }
	  else if ((arg0StrValue == null) && (arg1StrValue != null)) {
		  if (numOfArgs == 2) {
			  String xpathInlineFuncExprStr = "function($arg0) { ends-with($arg0, '" + arg1StrValue + "') }";

			  XPath xpathObj = new XPath(xpathInlineFuncExprStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);

			  result = xpathObj.execute(xctxt, DTM.NULL, null);
		  }
		  else if (collationUri != null) {
			  String xpathInlineFuncExprStr = "function($arg0) { ends-with($arg0, '" + arg1StrValue + "', '" + collationUri + "') }";

			  XPath xpathObj = new XPath(xpathInlineFuncExprStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);

			  result = xpathObj.execute(xctxt, DTM.NULL, null);
		  }
		  else {
			  String xpathInlineFuncExprStr = "function($arg0, $collation) { ends-with($arg0, '" + arg1StrValue + "', $collation) }";

			  XPath xpathObj = new XPath(xpathInlineFuncExprStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);

			  result = xpathObj.execute(xctxt, DTM.NULL, null);
		  }
	  }
	  else if ((arg0StrValue != null) && (arg1StrValue == null)) {
		  if (numOfArgs == 2) {
			  String xpathInlineFuncExprStr = "function($arg1) { ends-with('" + arg0StrValue + "', $arg1) }";

			  XPath xpathObj = new XPath(xpathInlineFuncExprStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);

			  result = xpathObj.execute(xctxt, DTM.NULL, null);
		  }
		  else if (collationUri != null) {
			  String xpathInlineFuncExprStr = "function($arg1) { ends-with('" + arg0StrValue + "', $arg1, '" + collationUri + "') }";

			  XPath xpathObj = new XPath(xpathInlineFuncExprStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);

			  result = xpathObj.execute(xctxt, DTM.NULL, null);
		  }
		  else {
			  String xpathInlineFuncExprStr = "function($arg1, $collation) { ends-with('" + arg0StrValue + "', $arg1, $collation) }";

			  XPath xpathObj = new XPath(xpathInlineFuncExprStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);

			  result = xpathObj.execute(xctxt, DTM.NULL, null);
		  }
	  }
	  else if ((arg0StrValue == null) && (arg1StrValue == null)) {
		  if (numOfArgs == 2) {
			  String xpathInlineFuncExprStr = "function($arg0, $arg1) { ends-with($arg0, $arg1) }";

			  XPath xpathObj = new XPath(xpathInlineFuncExprStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);

			  result = xpathObj.execute(xctxt, DTM.NULL, null);
		  }
		  else if (collationUri != null) {
			  String xpathInlineFuncExprStr = "function($arg0, $arg1) { ends-with($arg0, $arg1, '" + collationUri + "') }";

			  XPath xpathObj = new XPath(xpathInlineFuncExprStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);

			  result = xpathObj.execute(xctxt, DTM.NULL, null);
		  }
		  else {
			  String xpathInlineFuncExprStr = "function($arg0, $arg1, $collation) { ends-with($arg0, $arg1, $collation) }";

			  XPath xpathObj = new XPath(xpathInlineFuncExprStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);

			  result = xpathObj.execute(xctxt, DTM.NULL, null);
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
