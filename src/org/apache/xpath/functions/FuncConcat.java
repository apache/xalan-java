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

import javax.xml.transform.TransformerException;

import org.apache.xalan.res.XSLMessages;
import org.apache.xpath.Expression;
import org.apache.xpath.XPathContext;
import org.apache.xpath.axes.SelfIteratorNoPredicate;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;

import xml.xpath31.processor.types.XSAnyType;

/**
 * Execute the concat() function.
 * 
 * @xsl.usage advanced
 */
public class FuncConcat extends FunctionMultiArgs
{
    static final long serialVersionUID = 1737228885202314413L;

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

    StringBuffer sb = new StringBuffer();        

    // Compiler says we must have at least two arguments
    sb.append(inspectXPathSelfAxesExpression(m_arg0, xctxt));
    sb.append(inspectXPathSelfAxesExpression(m_arg1, xctxt));

    if (m_arg2 != null) {
       sb.append(inspectXPathSelfAxesExpression(m_arg2, xctxt));
    }

    if (m_args != null) {
       for (int i = 0; i < m_args.length; i++) {
         sb.append(inspectXPathSelfAxesExpression(m_args[i], xctxt));
       }
    }

    return new XString(sb.toString());
  }

  /**
   * Check that the number of arguments passed to this function is correct.
   *
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
      throw new WrongNumberArgsException(XSLMessages.createXPATHMessage("gtone", null));
  }
  
  /*
   * If the XPath expression's pattern string is ".", the evaluation result of 
   * XPath expression, is the string value of XPath context item.
   * 
   * Other than, handling an XPath expression that has a pattern string as ".", 
   * this method does few of other things as well. 
   */
  private String inspectXPathSelfAxesExpression(Expression expr, XPathContext xctxt) 
                                                                   throws TransformerException {
      String resultStr = null;
      
      XObject xpath3ContextItem = xctxt.getXPath3ContextItem();
      if (expr instanceof SelfIteratorNoPredicate) {
          if (xpath3ContextItem != null) {
              resultStr = xpath3ContextItem.str();
          }
          else {
              resultStr = expr.execute(xctxt).str();   
          }
      }
      else if (expr instanceof Function) {
          XObject evalResult = ((Function)expr).execute(xctxt);
          if (evalResult instanceof XSAnyType) {
              resultStr = ((XSAnyType)evalResult).stringValue();    
          }
          else {
              resultStr = evalResult.str();  
          }
      }
      else {
          resultStr = expr.execute(xctxt).str();
      }
      
      return resultStr;      
  }
  
}
