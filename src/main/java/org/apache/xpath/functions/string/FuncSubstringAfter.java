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

import java.text.CollationElementIterator;
import java.text.RuleBasedCollator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.transform.SourceLocator;

import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathCollationSupport;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.FuncArgPlaceholder;
import org.apache.xpath.functions.WrongNumberArgsException;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.operations.Variable;
import org.apache.xpath.res.XPATHErrorResources;

import xml.xpath31.processor.types.XSString;

/**
 * Implementation of XPath 3.1 function fn:substring-after.
 * 
 * @xsl.usage advanced
 */
public class FuncSubstringAfter extends XSL3StringCollationAwareFunction
{
   
  static final long serialVersionUID = -8119731889862512194L;
  
  /**
   * Class constructor.
   */
  public FuncSubstringAfter() {
	 m_defined_arity = new Short[] { 2, 3 };
  }
    
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
	  
	  SourceLocator srcLocator = xctxt.getSAXLocator();
	  
	  /**
	   * An XPath expression FuncArgPlaceholder if not null, for one or more of
	   * the function arguments, signifies that the corresponding function argument
	   * has been specified as ? i.e the function call is a partial function
	   * application. In this case, fn:substring-after function returns its result
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
			  int index = arg0StrValue.indexOf(arg1StrValue);
				 
			  result = ((index == -1) ? new XSString("") : new XSString(arg0StrValue.substring(index + arg1StrValue.length())));
		  }
		  else if (collationUri != null) {
			  if (collationUri.equals(xctxt.getDefaultCollation())) {
				  int index = arg0StrValue.indexOf(arg1StrValue);

				  result = ((index == -1) ? new XSString("") : new XSString(arg0StrValue.substring(index + arg1StrValue.length())));
			  }
			  else {
				  RuleBasedCollator textCollator = (RuleBasedCollator)(xPathCollationSupport.getUCACollatorFromCollationUri(collationUri));
				  CollationElementIterator collElemIter = textCollator.getCollationElementIterator(arg0StrValue);
				  int collationElem = 0;
				  List<Integer> collationElemList0 = new ArrayList<Integer>();
				  while ((collationElem = collElemIter.next()) != CollationElementIterator.NULLORDER) {
					  collationElemList0.add(collationElem); 
				  }

				  collElemIter = textCollator.getCollationElementIterator(arg1StrValue);
				  List<Integer> collationElemList1 = new ArrayList<Integer>();
				  while ((collationElem = collElemIter.next()) != CollationElementIterator.NULLORDER) {
					  collationElemList1.add(collationElem); 
				  }

				  int str0Length = collationElemList0.size();			  

				  boolean subsMatchFound = false;
				  for (int idx = 0; idx < str0Length; idx++) {
					  int i = idx;
					  Integer[] intArr1 = null;
					  Integer[] intArr2 = null;
					  for (int idx2 = i; idx2 < (str0Length - 1); idx2++) {
						  List<Integer> tempIntList = collationElemList0.subList(i, idx2 + 1); 
						  intArr1 = tempIntList.toArray(new Integer[0]);
						  intArr2 = collationElemList1.toArray(new Integer[0]);
						  if (Arrays.equals(intArr1, intArr2)) {
							  subsMatchFound = true;						  

							  break;
						  }
					  }

					  if (subsMatchFound) {					  					  
						  String tempStr1 = arg0StrValue.substring(i);
						  List<Integer> intList = new ArrayList<Integer>();
						  for (int j1 = 0; j1 < tempStr1.length(); j1++) {
							  String str2 = tempStr1.substring(j1, j1 + 1);
							  collElemIter = textCollator.getCollationElementIterator(str2);
							  int iValue = 0;
							  while ((iValue = collElemIter.next()) != CollationElementIterator.NULLORDER) {
								  intList.add(Integer.valueOf(iValue));	 
							  }						 
							  Integer[] intArr3 = intList.toArray(new Integer[0]);
							  if (Arrays.equals(intArr3, intArr2)) {
								  String resultStr = tempStr1.substring(j1 + 1);
								  result = new XSString(resultStr);

								  break;
							  }
						  }

						  break;
					  }
				  }

				  if (!subsMatchFound) {
					  result = new XSString("");  
				  }
		      }
		  }
		  else {
			  String xpathInlineFuncExprStr = "function($collation) { substring-after('" + arg0StrValue + "', '" + arg1StrValue + "', $collation) }";

			  XPath xpathObj = new XPath(xpathInlineFuncExprStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);

			  result = xpathObj.execute(xctxt, DTM.NULL, null);
		  }
	  }
	  else if ((arg0StrValue == null) && (arg1StrValue != null)) {
		  if (numOfArgs == 2) {
			  String xpathInlineFuncExprStr = "function($arg0) { substring-after($arg0, '" + arg1StrValue + "') }";

			  XPath xpathObj = new XPath(xpathInlineFuncExprStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);

			  result = xpathObj.execute(xctxt, DTM.NULL, null);
		  }
		  else if (collationUri != null) {
			  String xpathInlineFuncExprStr = "function($arg0) { substring-after($arg0, '" + arg1StrValue + "', '" + collationUri + "') }";

			  XPath xpathObj = new XPath(xpathInlineFuncExprStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);

			  result = xpathObj.execute(xctxt, DTM.NULL, null);
		  }
		  else {
			  String xpathInlineFuncExprStr = "function($arg0, $collation) { substring-after($arg0, '" + arg1StrValue + "', $collation) }";

			  XPath xpathObj = new XPath(xpathInlineFuncExprStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);

			  result = xpathObj.execute(xctxt, DTM.NULL, null);
		  }
	  }
	  else if ((arg0StrValue != null) && (arg1StrValue == null)) {
		  if (numOfArgs == 2) {
			  String xpathInlineFuncExprStr = "function($arg1) { substring-after('" + arg0StrValue + "', $arg1) }";

			  XPath xpathObj = new XPath(xpathInlineFuncExprStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);

			  result = xpathObj.execute(xctxt, DTM.NULL, null);
		  }
		  else if (collationUri != null) {
			  String xpathInlineFuncExprStr = "function($arg1) { substring-after('" + arg0StrValue + "', $arg1, '" + collationUri + "') }";

			  XPath xpathObj = new XPath(xpathInlineFuncExprStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);

			  result = xpathObj.execute(xctxt, DTM.NULL, null);
		  }
		  else {
			  String xpathInlineFuncExprStr = "function($arg1, $collation) { substring-after('" + arg0StrValue + "', $arg1, $collation) }";

			  XPath xpathObj = new XPath(xpathInlineFuncExprStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);

			  result = xpathObj.execute(xctxt, DTM.NULL, null);
		  }
	  }
	  else if ((arg0StrValue == null) && (arg1StrValue == null)) {
		  if (numOfArgs == 2) {
			  String xpathInlineFuncExprStr = "function($arg0, $arg1) { substring-after($arg0, $arg1) }";

			  XPath xpathObj = new XPath(xpathInlineFuncExprStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);

			  result = xpathObj.execute(xctxt, DTM.NULL, null);
		  }
		  else if (collationUri != null) {
			  String xpathInlineFuncExprStr = "function($arg0, $arg1) { substring-after($arg0, $arg1, '" + collationUri + "') }";

			  XPath xpathObj = new XPath(xpathInlineFuncExprStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);

			  result = xpathObj.execute(xctxt, DTM.NULL, null);
		  }
		  else {
			  String xpathInlineFuncExprStr = "function($arg0, $arg1, $collation) { substring-after($arg0, $arg1, $collation) }";

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
