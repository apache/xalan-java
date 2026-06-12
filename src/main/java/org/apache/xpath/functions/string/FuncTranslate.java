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
import javax.xml.transform.TransformerException;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.Function3Args;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;

import xml.xpath31.processor.types.XSAnyURI;
import xml.xpath31.processor.types.XSString;
import xml.xpath31.processor.types.XSUntypedAtomic;

/**
 * Implementation of XPath 3.1 function fn:translate.
 * 
 * @xsl.usage advanced
 */
public class FuncTranslate extends Function3Args
{
  
   static final long serialVersionUID = -1672834340026116482L;
   
   /**
    * Class constructor.
    */
   public FuncTranslate() {
	   m_defined_arity = new Short[] { 3 };
   }

  /**
   * Evaluate the function. The function must return a valid object.
   * 
   * @param xctxt                         An XPath context object
   * @return                              A valid XObject
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
  {
	  
	  XObject result = null;

	  SourceLocator srcLocator = xctxt.getSAXLocator();

	  XObject xObj0 = null;
	  XObject xObj1 = null;
	  XObject xObj2 = null;

	  xObj0 = m_arg0.execute(xctxt);
	  
	  if (xObj0 instanceof ResultSequence) {
		  if (((ResultSequence)xObj0).size() > 1) {
		     throw new TransformerException("XPTY0004 : The supplied value to XPath function 'translate' for the first argument, "
                                                                                                                   + "cannot be converted to a string.", srcLocator);
		  }
		  else if (((ResultSequence)xObj0).size() == 1) {
			 xObj0 = ((ResultSequence)xObj0).item(0);
			 
			 if (!((xObj0 instanceof XSString) || (xObj0 instanceof XString) || (xObj0 instanceof XSAnyURI) 
					                                                         || (xObj0 instanceof XSUntypedAtomic) || (xObj0 instanceof XMLNodeCursorImpl))) {
				  throw new TransformerException("XPTY0004 : The supplied value to XPath function 'translate' for the first argument, "
				  		                                                                                           + "cannot be converted to a string.", srcLocator); 
			 }			 
		  }
	  }	  
	  else if (!((xObj0 instanceof XSString) || (xObj0 instanceof XString) || (xObj0 instanceof XSAnyURI) 
			                                                               || (xObj0 instanceof XSUntypedAtomic) || (xObj0 instanceof XMLNodeCursorImpl))) {
		  throw new TransformerException("XPTY0004 : The supplied value to XPath function 'translate' for the first argument, "
		  		                                                                                                   + "cannot be converted to a string.", srcLocator); 
	  }	  	  

	  xObj1 = m_arg1.execute(xctxt);

	  if (xObj1 instanceof ResultSequence) {
		  if (((ResultSequence)xObj1).size() > 1) {
			  throw new TransformerException("XPTY0004 : The supplied value to XPath function 'translate' for the second argument, "
					  																							   + "cannot be converted to a string.", srcLocator);
		  }
		  else if (((ResultSequence)xObj1).size() == 1) {
			  xObj1 = ((ResultSequence)xObj1).item(0);

			  if (!((xObj1 instanceof XSString) || (xObj1 instanceof XString) || (xObj1 instanceof XSAnyURI) 
					                                                          || (xObj1 instanceof XSUntypedAtomic) || (xObj1 instanceof XMLNodeCursorImpl))) {
				  throw new TransformerException("XPTY0004 : The supplied value to XPath function 'translate' for the second argument, "
						                                                                                           + "cannot be converted to a string.", srcLocator); 
			  }			 
		  }
		  else {
			  throw new TransformerException("XPTY0004 : An XPath function 'translate' second argument cannot be an empty sequence.", srcLocator); 
		  }
	  }	  
	  else if (!((xObj1 instanceof XSString) || (xObj1 instanceof XString) || (xObj1 instanceof XSAnyURI) 
			                                                               || (xObj1 instanceof XSUntypedAtomic) || (xObj1 instanceof XMLNodeCursorImpl))) {
		  throw new TransformerException("XPTY0004 : The supplied value to XPath function 'translate' for the second argument, "
				  																								   + "cannot be converted to a string.", srcLocator); 
	  }
	  
	  if ((xObj1 instanceof XMLNodeCursorImpl) && ((XMLNodeCursorImpl)xObj1).getLength() == 0) {
		  throw new TransformerException("XPTY0004 : An XPath function 'translate' second argument cannot be an empty sequence.", srcLocator); 
	  }

	  xObj2 = m_arg2.execute(xctxt);
	  
	  if (xObj2 instanceof ResultSequence) {
		  if (((ResultSequence)xObj2).size() > 1) {
			  throw new TransformerException("XPTY0004 : The supplied value to XPath function 'translate' for the third argument, "
					  																							   + "cannot be converted to a string.", srcLocator);
		  }
		  else if (((ResultSequence)xObj2).size() == 1) {
			  xObj2 = ((ResultSequence)xObj2).item(0);

			  if (!((xObj2 instanceof XSString) || (xObj2 instanceof XString) || (xObj2 instanceof XSAnyURI) 
					                                                          || (xObj2 instanceof XSUntypedAtomic) || (xObj2 instanceof XMLNodeCursorImpl))) {
				  throw new TransformerException("XPTY0004 : The supplied value to XPath function 'translate' for the third argument, "
						  																						   + "cannot be converted to a string.", srcLocator); 
			  }			 
		  }
		  else {
			  throw new TransformerException("XPTY0004 : An XPath function 'translate' third argument cannot be an empty sequence.", srcLocator); 
		  }
	  }	  
	  else if (!((xObj2 instanceof XSString) || (xObj2 instanceof XString) || (xObj2 instanceof XSAnyURI) 
			                                                               || (xObj2 instanceof XSUntypedAtomic) || (xObj2 instanceof XMLNodeCursorImpl))) {
		  throw new TransformerException("XPTY0004 : The supplied value to XPath function 'translate' for the third argument, "
				  																								   + "cannot be converted to a string.", srcLocator); 
	  }
	  
	  if ((xObj2 instanceof XMLNodeCursorImpl) && ((XMLNodeCursorImpl)xObj2).getLength() == 0) {
		  throw new TransformerException("XPTY0004 : An XPath function 'translate' third argument cannot be an empty sequence.", srcLocator); 
	  }
	  
	  if ((xObj0 instanceof ResultSequence) && (((ResultSequence)xObj0).size() == 0)) {
		  result = new XSString("");

		  return result;
	  }

	  if ((xObj0 instanceof XMLNodeCursorImpl) && (((XMLNodeCursorImpl)xObj0).getLength() == 0)) {
		  result = new XSString("");

		  return result; 
	  }

	  String theFirstString = XslTransformEvaluationHelper.getStrVal(xObj0);
	  String theSecondString = XslTransformEvaluationHelper.getStrVal(xObj1);
	  String theThirdString = XslTransformEvaluationHelper.getStrVal(xObj2);
	  
	  /**
	   * To allow for non-BMP characters as well to be considered,
	   * we do string translation for code point values, and then
	   * convert result to string.
	   */
	  
	  int[] codePointArrStr1 = (theFirstString.codePoints()).toArray();
	  int[] codePointArrStr2 = (theSecondString.codePoints()).toArray();
	  int[] codePointArrStr3 = (theThirdString.codePoints()).toArray();
	  
	  int theFirstStringLength = codePointArrStr1.length;
	  int theThirdStringLength = codePointArrStr3.length;
	  
	  int[] result2 = null;

	  for (int i = 0; i < theFirstStringLength; i++)
	  {		  
		  int theCurrentCharCodePoint = codePointArrStr1[i];
		  int theIndex = getCodePointIndex(codePointArrStr2, theCurrentCharCodePoint);

		  if (theIndex < 0)
		  {
			  // We didn't find the character in the second string, therefore 
			  // it's not translated.
			  
			  if (result2 == null) {
				 result2 = new int[1];
				 result2[0] = theCurrentCharCodePoint; 
			  }
			  else {
				  int[] tempArr = new int[result2.length + 1];
				  System.arraycopy(result2, 0, tempArr, 0, result2.length);
				  tempArr[result2.length] = theCurrentCharCodePoint;

				  result2 = tempArr; 
			  }
		  }
		  else if (theIndex < theThirdStringLength)
		  {
			  // There's a corresponding character in the third string, 
			  // therefore we do the translation.
			  
			  if (result2 == null) {
				 result2 = new int[1];
				 result2[0] = codePointArrStr3[theIndex]; 
			  }
			  else {
				 int[] tempArr = new int[result2.length + 1];
				 System.arraycopy(result2, 0, tempArr, 0, result2.length);
				 tempArr[result2.length] = codePointArrStr3[theIndex];
				 
				 result2 = tempArr;
			  }
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

	  String str1 = null;
	  
	  if (result2 != null) {
		 str1 = new String(result2, 0, result2.length);  
	  }
	  else {
		 str1 = ""; 
	  }
	  
	  result = new XSString(str1);

	  return result;
	  
  }
  
  /**
   * Method definition, to find an index within the supplied 
   * code point array for a specified supplied code point value.
   * 
   * @param srcArr                   The supplied code point array
   * @param search                   The code point to be searched
   *                                 within the supplied array.
   * @return                         An array index value, or -1 if
   *                                 the code point value to be searched
   *                                 is not found.                                 
   */
  private int getCodePointIndex(int[] srcArr, int search) {
	  
	  int result = -1;
	  
	  int length1 = srcArr.length;
	  for (int idx = 0; idx < length1; idx++) {
		 if (srcArr[idx] == search) {
			result = idx;
			
			break;
		 }
	  }
	  
	  return result;
  }
}
