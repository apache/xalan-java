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

import java.text.Normalizer;

import javax.xml.transform.SourceLocator;

import org.apache.xalan.res.XSLMessages;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.FuncEmpty;
import org.apache.xpath.functions.WrongNumberArgsException;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.res.XPATHErrorResources;

import xml.xpath31.processor.types.XSString;

/**
 * Implementation of XPath 3.1 function fn:normalize-unicode.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncNormalizeUnicode extends XSL3StringCollationAwareFunction
{
  
  private static final long serialVersionUID = 1677438900565315366L;
  
  private static final String NFC = "NFC";
  
  private static final String NFD = "NFD";
  
  private static final String NFKC = "NFKC";
  
  private static final String NFKD = "NFKD";
  
  private static final String FULLY_NORMALIZED = "FULLY-NORMALIZED";

  /**
   * Class constructor.
   */
  public FuncNormalizeUnicode() {
	 m_defined_arity = new Short[] { 1, 2 };
  }

  /**
   * Implementation of the function. The function must return a valid object.
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
	  
	  FuncEmpty funcEmpty = new FuncEmpty();
	  funcEmpty.setArg0(m_arg0);
	  XObject xObj = funcEmpty.execute(xctxt);
	  if (xObj.bool()) {
		  result = new XSString(""); 
	  }
	  else {
		  String arg0StrValue = getArgStringValue(xctxt, m_arg0);

		  String arg1StrValue = null;		  
		  if (m_arg1 != null) {
			  FuncNormalizeSpace funcNormalizeSpace = new FuncNormalizeSpace();
			  funcNormalizeSpace.setArg0(m_arg1);
			  XSString xsString = (XSString)(funcNormalizeSpace.execute(xctxt));
			  String str1 = xsString.stringValue();
			  arg1StrValue = str1.toUpperCase();
	      }
	      else {
	    	  arg1StrValue = NFC;
	      }
		  
		  Normalizer.Form normalizerForm = null;
		  
		  if (NFC.equals(arg1StrValue)) {
			  normalizerForm = Normalizer.Form.NFC;  
		  }
		  else if (NFD.equals(arg1StrValue)) {
			  normalizerForm = Normalizer.Form.NFD;  
		  }
		  else if (NFKC.equals(arg1StrValue)) {
			  normalizerForm = Normalizer.Form.NFKC;  
		  }
		  else if (NFKD.equals(arg1StrValue)) {
			  normalizerForm = Normalizer.Form.NFKD;  
		  }
		  else if (FULLY_NORMALIZED.equals(arg1StrValue)) {
			  throw new javax.xml.transform.TransformerException("FOCH0003 : This implementation doesn't support fn:normalize-unicode string "
			  		                                                                                    + "conversion using FULLY-NORMALIZED normalizer form.", srcLocator);  
		  }
		  else {
			  throw new javax.xml.transform.TransformerException("FOCH0003 : Allowed values for fn:normalize-unicode normalizationForm "
			 		                                                                                    + "argument are NFC, NFD, NFKC, NFKD, FULLY-NORMALIZED. This "
			 		                                                                                    + "implementation doesn't support fn:normalize-unicode string "
			 		                                                                                    + "conversion using FULLY-NORMALIZED normalizer form.", srcLocator);
		  }

		  String resultStr = Normalizer.normalize(arg0StrValue, normalizerForm);
		  
		  result = new XSString(resultStr);
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
	  if (!((argNum == 1) || (argNum == 2))) {
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
                                                                   XPATHErrorResources.ER_ONE_OR_TWO, 
                                                                   null));
  }
  
}
