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

import org.apache.xml.dtm.DTM;
import org.apache.xml.utils.XMLString;
import org.apache.xpath.XPathCollationSupport;
import org.apache.xpath.XPathContext;
import org.apache.xpath.composite.XPathNamedFunctionReference;
import org.apache.xpath.functions.FunctionDef1Arg;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathInlineFunction;

import xml.xpath31.processor.types.XSInteger;

/**
 * Implementation of an XPath 3.1 function fn:string-length.
 * 
 * @xsl.usage advanced
 */
public class FuncStringLength extends FunctionDef1Arg
{
  static final long serialVersionUID = -159616417996519839L;
   
  /**
   * Class constructor.
   */
  public FuncStringLength() {
	 m_arity = new Short[] { 0, 1 };
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
	  
	  SourceLocator scrLocator = xctxt.getSAXLocator();
	  
	  final int sourceNode = xctxt.getContextNode();
	  
	  if (m_arg0 == null) {
		 if ((xctxt.getXPath3ContextItem() == null) && (sourceNode == DTM.NULL)) {
			throw new TransformerException("XPDY0002 : An XPath 3.1 function 'string-length' is called with "
					                                                                     + "no arguments, and XPath context "
					                                                                     + "item is absent.", scrLocator); 
		 }
	  }
	  
	  if ((m_arg0 instanceof XPathInlineFunction) || (m_arg0 instanceof XPathNamedFunctionReference)) {
		  throw new TransformerException("FOTY0013 : An XPath 3.1 function 'string-length' is supplied "
		  		                                                                         + "with an argument which is "
		  		                                                                         + "a function item.", scrLocator); 
	  }

	  try {
		  if (m_arg0 != null) {
			  XObject xObj0 = getFunctionArgEffectiveValue(m_arg0, xctxt);

			  if (xObj0 instanceof ResultSequence) {
				  ResultSequence rSeq = (ResultSequence)xObj0;
				  if (rSeq.size() > 1) {
					  throw new TransformerException("XPTY0004 : An XPath 3.1 function 'string-length' is supplied "
																									  + "an argument which is a sequence of "
																									  + "size greater than one.", scrLocator); 
				  }
			  }

			  if (xObj0 instanceof XMLNodeCursorImpl) {
				  int size1 = ((XMLNodeCursorImpl)xObj0).getLength();
				  if (size1 > 1) {
					  throw new TransformerException("XPTY0004 : An XPath 3.1 function 'string-length' is supplied "
																									  + "an argument which is a sequence of "
																									  + "size greater than one.", scrLocator); 
				  }
			  }
		  }
		  
		  XMLString xmlStr1 = getArg0AsString(xctxt);
		  String inpStr = xmlStr1.toString();

		  XPathCollationSupport xPathCollationSupport = xctxt.getXPathCollationSupport();

		  int[] codePointsArr = xPathCollationSupport.getCodepointsFromString(inpStr);

		  result = new XSInteger(codePointsArr.length + "");
	  }
	  catch (TransformerException ex) {
		  throw ex; 
	  }

	  return result;
   }
}
