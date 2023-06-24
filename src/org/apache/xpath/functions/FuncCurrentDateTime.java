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
/*
 * $Id$
 */
package org.apache.xpath.functions;

import java.util.Vector;

import org.apache.xalan.res.XSLMessages;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.res.XPATHErrorResources;
import org.apache.xpath.xs.types.XSDateTime;

/**
 * Execute the current-dateTime() function.
 * 
 * This function returns the current date and time (with timezone),
 * stored within the current XPath context.
 * 
 * XPath 3.1 spec mentions following, about this function,
 * 
 * "If the implementation supports data types from XSD 1.1 then the returned 
 * value will be an instance of xs:dateTimeStamp. Otherwise, the only guarantees 
 * are that it will be an instance of xs:dateTime and will have a timezone 
 * component."
 * 
 * The implementation of function fn:current-dateTime(), provided by this class,
 * returns an instance of xs:dateTime. 
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncCurrentDateTime extends Function {

   private static final long serialVersionUID = -2032033071326423919L;

   /**
   * Execute the function. The function must return a valid object.
   * 
   * @param xctxt The current execution context.
   * 
   * @return A valid XObject.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException {
    
    XSDateTime xsDateTime = new XSDateTime(xctxt.getCurrentDateTime(), xctxt.getTimezone());

    return (XObject)xsDateTime;
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
     if (argNum > 0) {
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
                                                XPATHErrorResources.ER_ZERO, null)); //"0"
  }

  @Override
  public void fixupVariables(Vector vars, int globalsSize) {
     // NO OP    
  }
  
}
