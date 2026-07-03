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

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.SourceLocator;

import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xpath.XPathContext;
import org.apache.xpath.axes.SelfIteratorNoPredicate;
import org.apache.xpath.functions.Function4Args;
import org.apache.xpath.functions.RegexEvaluationSupport;
import org.apache.xpath.functions.WrongNumberArgsException;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.regex.Matcher;
import org.apache.xpath.regex.PatternSyntaxException;
import org.apache.xpath.res.XPATHErrorResources;

import xml.xpath31.processor.types.XSString;

/**
 * Implementation of an XPath 3.1 function fn:replace.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncReplace extends Function4Args {
    
   static final long serialVersionUID = 400116356230813776L;
   
   private static final String FUNCTION_NAME = "replace()";
   
   /**
    * Class constructor.
    */
   public FuncReplace() {
 	  m_arity = new Short[] { 3, 4 };
   }

   /**
    * Evaluate the function. The function must return a valid object.
    * 
    * @param xctxt                        An XPath context object
    * @return                             A valid XObject
    *
    * @throws javax.xml.transform.TransformerException
    */
   public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
   {
	   XObject result = null;

	   SourceLocator srcLocator = xctxt.getSAXLocator();

	   XObject xpath3ContextItem = xctxt.getXPath3ContextItem();

	   String inputStr = null;
	   if ((m_arg0 instanceof SelfIteratorNoPredicate) && (xpath3ContextItem != null)) {
		   inputStr = XslTransformEvaluationHelper.getStrVal(xpath3ContextItem); 
	   }
	   else {
		   inputStr = XslTransformEvaluationHelper.getStrVal(getFunctionArgEffectiveValue(m_arg0, xctxt));
	   }

	   String patternStr = XslTransformEvaluationHelper.getStrVal(getFunctionArgEffectiveValue(m_arg1, xctxt));        

	   String replacementStr = XslTransformEvaluationHelper.getStrVal(getFunctionArgEffectiveValue(m_arg2, xctxt));

	   String flagStr = null;

	   if (m_arg3 != null) {
		   flagStr = XslTransformEvaluationHelper.getStrVal(getFunctionArgEffectiveValue(m_arg3, xctxt));

		   if (!RegexEvaluationSupport.isRegexFlagStrValid(flagStr)) {
			   throw new javax.xml.transform.TransformerException(XSLMessages.createXPATHMessage(XPATHErrorResources.
																											   ER_INVALID_REGEX_FLAGS, new Object[]{ FUNCTION_NAME }), 
																											   srcLocator);     
		   }
	   }

	   try {
		   Matcher regexMatcher = null;

		   try {
			   regexMatcher = RegexEvaluationSupport.getRegexMatcher(RegexEvaluationSupport.transformRegexStrForSubtrOp(patternStr), flagStr, inputStr);
		   }
		   catch (Exception ex) {
			   String errMesg = XSLMessages.createXPATHMessage(XPATHErrorResources.ER_INVALID_REGEX, new Object[]{ FUNCTION_NAME });        		

			   String mesg1 = ex.getMessage();
			   errMesg = (mesg1 != null) ? (errMesg + " " + mesg1) : errMesg;  

			   throw new javax.xml.transform.TransformerException(errMesg, srcLocator);
		   }
		   
		   if ((flagStr != null) && flagStr.contains("q")) {			   
			   List<RegexMatchInfo> regexMatchInfoList = new ArrayList<RegexMatchInfo>();

			   while (regexMatcher.find()) {
				   int idx1 = regexMatcher.start();
				   int idx2 = regexMatcher.end();

				   RegexMatchInfo regexMatchInfo = new RegexMatchInfo();
				   regexMatchInfo.setStartIdx(idx1);
				   regexMatchInfo.setEndIdx(idx2);

				   regexMatchInfoList.add(regexMatchInfo);
			   }

			   regexMatcher.reset();

			   int size1 = regexMatchInfoList.size();

			   StringBuffer strBuff = new StringBuffer();

			   if (size1 > 0) {
				   RegexMatchInfo firstRegexMatchInfo = regexMatchInfoList.get(0);
				   int startIdx1 = firstRegexMatchInfo.getStartIdx();
				   if (startIdx1 == 0) {
					   // Regex has matched a substring, which is prefix of an input string         			
					   for (int idx = 0; idx < size1; idx++) {
						   RegexMatchInfo matchInfo = regexMatchInfoList.get(idx);
						   int idx1 = matchInfo.getStartIdx();
						   int idx2 = matchInfo.getEndIdx();
						   //String matchStr = inputStr.substring(idx1, idx2);

						   strBuff.append(replacementStr);

						   if (isXslNonMatchStringAvailable(inputStr, idx2)) {
							   String nonMatchStr = null;
							   if ((idx + 1) == size1) {
								   nonMatchStr = inputStr.substring(idx2);
							   }
							   else {
								   RegexMatchInfo matchInfoNext = regexMatchInfoList.get(idx + 1);
								   nonMatchStr = inputStr.substring(idx2, matchInfoNext.getStartIdx());   
							   }

							   if ((nonMatchStr != null) && (nonMatchStr.length() > 0)) {
								   strBuff.append(nonMatchStr);
							   }
						   }        		
					   }	
				   }
				   else if (startIdx1 > 0) {
					   // An input string's prefix has not been matched by regex        			
					   RegexMatchInfo pof1 = regexMatchInfoList.get(0);
					   String nonMatchStr = inputStr.substring(0, pof1.getStartIdx());

					   if ((nonMatchStr != null) && (nonMatchStr.length() > 0)) {
						   strBuff.append(nonMatchStr);
					   }

					   for (int idx = 0; idx < size1; idx++) {
						   RegexMatchInfo matchInfo = regexMatchInfoList.get(idx);
						   int idx1 = matchInfo.getStartIdx();
						   int idx2 = matchInfo.getEndIdx();

						   strBuff.append(replacementStr);

						   if (isXslNonMatchStringAvailable(inputStr, idx2)) {
							   if ((idx + 1) == size1) {
								   nonMatchStr = inputStr.substring(idx2);
							   }
							   else {
								   RegexMatchInfo matchInfoNext = regexMatchInfoList.get(idx + 1);
								   nonMatchStr = inputStr.substring(idx2, matchInfoNext.getStartIdx());   
							   }

							   if ((nonMatchStr != null) && (nonMatchStr.length() > 0)) {
								   strBuff.append(nonMatchStr);
							   }
						   }        			
					   }
				   }

				   String str1 = strBuff.toString();

				   result = new XSString(str1);
			   }
			   else {
				   result = new XSString(inputStr);
			   }
	       }
		   else {
			   String str1 = regexMatcher.replaceAll(replacementStr);
			   
			   result = new XSString(str1);
		   }
       	
	   }
	   catch (PatternSyntaxException ex) {
		   throw new javax.xml.transform.TransformerException(XSLMessages.createXPATHMessage(XPATHErrorResources.
																										   ER_INVALID_REGEX, new Object[]{ FUNCTION_NAME }), 
																										   srcLocator);   
	   }
	   catch (Exception ex) {
		   String errMesg = ex.getMessage();        	

		   String errCode = "FORX0004";
		   if (errMesg.startsWith("No group")) {
			   errCode = "FORX0003";
		   }

		   errMesg = errCode + " : " + errMesg;  

		   throw new javax.xml.transform.TransformerException(errMesg, srcLocator); 
	   }

	   return result;
   }
   
   /**
    * A class representing, a pair of string index values,
    * for a substring that matched with the fn:replace 
    * function's regex argument.
    */
   class RegexMatchInfo {    	
	   
	   private int startIdx;
	   
	   private int endIdx;

	   /**
	    * Class constructor.
	    */
	   public RegexMatchInfo() {
		   // no op
	   }

	   public int getStartIdx() {
		   return startIdx;
	   }

	   public void setStartIdx(int startIdx) {
		   this.startIdx = startIdx;
	   }

	   public int getEndIdx() {
		   return endIdx;
	   }

	   public void setEndIdx(int endIdx) {
		   this.endIdx = endIdx;
	   }
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
     if (argNum < 3) {
        reportWrongNumberArgs();
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
                                              XPATHErrorResources.ER_THREE_OR_FOUR, null)); //"3 or 4"
  }
  
  private boolean isXslNonMatchStringAvailable(String inpStr, int idx) {

	  boolean result = false;

	  try {
		  result = (inpStr.charAt(idx) != -1);
	  }
	  catch (IndexOutOfBoundsException ex) {
		  // no op
	  }

	  return result;
  }
  
}
