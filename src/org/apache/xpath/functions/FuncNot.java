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

import org.apache.xalan.templates.XSConstructorFunctionUtil;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.operations.Operation;
import org.xml.sax.SAXException;

/**
 * Execute the not() function.
 * 
 * @xsl.usage advanced
 */
public class FuncNot extends FunctionOneArg
{
    static final long serialVersionUID = 7299699961076329790L;

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
      if (m_arg0 instanceof Operation) {
          try {
             XObject result = XSConstructorFunctionUtil.processFuncExtFunctionOrXPathOpn(
                                                                                    xctxt, m_arg0, null);
             return result.bool() ? XBoolean.S_FALSE : XBoolean.S_TRUE; 
          }
          catch(SAXException ex) {
             throw new javax.xml.transform.TransformerException(ex.getMessage());    
          }
      }
      else {
          return m_arg0.execute(xctxt).bool() ? XBoolean.S_FALSE : XBoolean.S_TRUE;
      }
  }
  
}
