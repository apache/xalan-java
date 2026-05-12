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

import javax.xml.transform.TransformerException;

import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xpath.XPathCollationSupport;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.Function3Args;
import org.apache.xpath.functions.WrongNumberArgsException;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.res.XPATHErrorResources;

import xml.xpath31.processor.types.XSNumericType;
import xml.xpath31.processor.types.XSString;

/**
 * Implementation of XPath 3.1 function fn:substring.
 * 
 * @xsl.usage advanced
 */
public class FuncSubstring extends Function3Args
{
   static final long serialVersionUID = -5996676095024715502L;
    
   /**
	* Class constructor.
	*/
   public FuncSubstring() {
	   m_defined_arity = new Short[] { 2, 3 };
   }

   /**
    * Evaluate the function. The function must return a valid object.
    * 
    * @param xctxt 							The current execution context
    * @return 								A valid XObject
    *
    * @throws javax.xml.transform.TransformerException
    */
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
  {
	  XObject result = null;
	  
	  // An XPath 3.1 function fn:substring index within the 
	  // string starts at position 1 and not 0.

	  try {
		  XObject arg0Obj = m_arg0.execute(xctxt);

		  if ((arg0Obj instanceof ResultSequence) && (((ResultSequence)arg0Obj).size() == 0)) {
			  result = new XSString("");
		  }
		  else {
			  String inpStr = XslTransformEvaluationHelper.getStrVal(arg0Obj);

			  XPathCollationSupport xPathCollationSupport = xctxt.getXPathCollationSupport();

			  int[] codePointsArr = xPathCollationSupport.getCodepointsFromString(inpStr);

			  int start = 0;

			  XObject arg1Obj = m_arg1.execute(xctxt);

			  boolean is2ndArgNegInf = false;
			  boolean isStartComputed = false;

			  if (arg1Obj instanceof XNumber) {
				  double dbl = arg1Obj.num();
				  Double dblObj = Double.valueOf(dbl);
				  if (dblObj.isNaN()) {
					  result = new XSString("");

					  return result; 
				  }

				  if (dblObj == dblObj.NEGATIVE_INFINITY) {
					  is2ndArgNegInf = true;  
				  }
				  else {
					  start = getNormalizedInt(dbl);			     
					  isStartComputed = true;
				  }
			  }
			  else if (arg1Obj instanceof XSNumericType) {
				  String arg1StrVal = ((XSNumericType)arg1Obj).stringValue();
				  double dbl = Double.valueOf(arg1StrVal);
				  Double dblObj = Double.valueOf(dbl);
				  if (dblObj.isNaN()) {
					  result = new XSString("");

					  return result; 
				  }

				  if (dblObj == dblObj.NEGATIVE_INFINITY) {
					  is2ndArgNegInf = true;  
				  }
				  else {
					  start = getNormalizedInt(dbl);
					  isStartComputed = true;
				  }
			  }
			  else {
				  double dbl = arg1Obj.num();
				  Double dblObj = Double.valueOf(dbl);
				  if (dblObj.isNaN()) {
					  result = new XSString("");

					  return result; 
				  }

				  if (dblObj == dblObj.NEGATIVE_INFINITY) {
					  is2ndArgNegInf = true;  
				  }
				  else {
					  start = getNormalizedInt(dbl);
					  isStartComputed = true;
				  }
			  }

			  int length = 0;

			  boolean is3rdArgPosInf = false;
			  boolean isLengthComputed = false;

			  if (m_arg2 != null) {			  
				  XObject arg2Obj = m_arg2.execute(xctxt);			  

				  if (arg2Obj instanceof XNumber) {
					  double dbl = arg2Obj.num();
					  Double dblObj = Double.valueOf(dbl);
					  if (dblObj.isNaN()) {
						  result = new XSString("");

						  return result; 
					  }

					  if (dblObj == dblObj.POSITIVE_INFINITY) {
						  is3rdArgPosInf = true;  
					  }
					  else {
						  length = getNormalizedInt(dbl);
						  isLengthComputed = true;
					  }
				  }
				  else if (arg2Obj instanceof XSNumericType) {					  
					  String arg2StrVal = ((XSNumericType)arg2Obj).stringValue();
					  double dbl = Double.valueOf(arg2StrVal);
					  Double dblObj = Double.valueOf(dbl);
					  if (dblObj.isNaN()) {
						  result = new XSString("");

						  return result; 
					  }

					  if (dblObj == dblObj.POSITIVE_INFINITY) {
						  is3rdArgPosInf = true;  
					  }
					  else {
						  length = getNormalizedInt(dbl);
						  isLengthComputed = true;
					  }
				  }
				  else {
					  double dbl = arg2Obj.num();
					  Double dblObj = Double.valueOf(dbl);
					  if (dblObj.isNaN()) {
						  result = new XSString("");

						  return result; 
					  }

					  if (dblObj == dblObj.POSITIVE_INFINITY) {
						  is3rdArgPosInf = true;  
					  }
					  else {
						  length = getNormalizedInt(dbl);
						  isLengthComputed = true;
					  }
				  }
			  }
			  else {
				  StringBuffer strBuff = new StringBuffer();
				  int strtIndex = --start;
				  for (int idx = strtIndex; idx < codePointsArr.length; idx++) {
					  if ((idx >= 0) && (idx < codePointsArr.length)) {
						  char[] charArr = Character.toChars(codePointsArr[idx]);
						  strBuff.append(String.valueOf(charArr));
					  }
				  }

				  result = new XSString(strBuff.toString());

				  return result;
			  }

			  if (is2ndArgNegInf && is3rdArgPosInf) {
				  // Since the value of -INF + INF is NaN, no characters are selected			  
				  result = new XSString("");

				  return result;  
			  }

			  if (isStartComputed && isLengthComputed) {
				  StringBuffer strBuff = new StringBuffer();
				  int strtIndex = --start;
				  int count = 0;
				  for (int idx = strtIndex; idx < codePointsArr.length; idx++) {
					  count++;
					  if ((idx >= 0) && (idx < codePointsArr.length) && (count <= length)) {
						  char[] charArr = Character.toChars(codePointsArr[idx]);
						  strBuff.append(String.valueOf(charArr));
					  }
				  }

				  result = new XSString(strBuff.toString());

				  return result;
			  }

			  if (!is2ndArgNegInf && is3rdArgPosInf) {
				  // Characters at positions greater than or equal to start and less than 
				  // INF are selected.
				  StringBuffer strBuff = new StringBuffer();
				  int strtIndex = --start;
				  for (int idx = strtIndex; idx < codePointsArr.length; idx++) {
					  if ((idx >= 0) && (idx < codePointsArr.length)) {
						  char[] charArr = Character.toChars(codePointsArr[idx]);
						  strBuff.append(String.valueOf(charArr));
					  }
				  }

				  result = new XSString(strBuff.toString());

				  return result;
			  }		  		  
		  }
      }
      catch (TransformerException ex) {
          throw ex;
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
    if (argNum < 2)
      reportWrongNumberArgs();
  }

  /**
   * Constructs and throws a WrongNumberArgException with the appropriate
   * message for this function object.
   *
   * @throws WrongNumberArgsException
   */
  protected void reportWrongNumberArgs() throws WrongNumberArgsException {
      throw new WrongNumberArgsException(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_TWO_OR_THREE, null)); //"2 or 3");
  }
  
  /**
   * Method definition, to get the normalized int value for 
   * the supplied double value, for the purpose of using 
   * as an XPath 3.1 function fn:substring arguments.
   * 
   * @param dbl				  The supplied double value
   * @return                  The normalized int value
   */
  private int getNormalizedInt(double dbl) {
	  
	  int result;
	  
	  int int1 = (int)dbl;
	  if (dbl == int1) {
		  result = int1;  
	  }
	  else {
		  double diff = (dbl - int1);
		  if (diff >= 0.5) {
			  result = ++int1;	 
		  }
		  else {
			  result = --int1; 
		  }
	  }

	  return result;
   }
}
