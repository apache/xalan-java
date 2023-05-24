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

import javax.xml.transform.SourceLocator;

import org.apache.xalan.res.XSLMessages;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;
import org.apache.xpath.operations.Variable;
import org.apache.xpath.res.XPATHErrorResources;

/**
 * Execute the string-join() function.
 * 
 * This function returns a string created by concatenating the items 
 * in a sequence, with a defined separator between adjacent items.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncStringJoin extends Function2Args {

   private static final long serialVersionUID = 4171534319684252331L;

   /**
   * Execute the function. The function must return
   * a valid object.
   * 
   * @param xctxt The current execution context.
   * 
   * @return A valid XObject.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
  {

    SourceLocator srcLocator = xctxt.getSAXLocator();
    
    ResultSequence arg0ResultSeq = null;
    
    if (m_arg0 instanceof Function) {
        // evaluate an xslt/xpath function reference.
        XObject evalResult = ((Function)m_arg0).execute(xctxt);
        if (evalResult instanceof ResultSequence) {
            arg0ResultSeq = (ResultSequence)evalResult;   
        }                
    }
    
    if (m_arg0 instanceof Variable) {
        // evaluate an xslt variable reference.
        XObject evalResult = ((Variable)m_arg0).execute(xctxt);
        if (evalResult instanceof ResultSequence) {
            arg0ResultSeq = (ResultSequence)evalResult;    
        }
    }
    
    if (arg0ResultSeq == null) {
        throw new javax.xml.transform.TransformerException("The first argument of fn:string-join, "
                                                              + "did not evaluate to a sequence.", 
                                                                             srcLocator);    
    }
    
    String separator = null;
    
    if (m_arg1 == null) {
       separator = "";   
    }    
    else if (m_arg1 instanceof XString) {
       separator = ((XString)m_arg1).str();
    }
    else {
       throw new javax.xml.transform.TransformerException("The second argument of fn:string-join must "
                                                               + "be absent, or it must be a string value.", 
                                                                                     srcLocator);
    }
    
    StringBuffer sb = new StringBuffer();
    
    for (int idx = 0; idx < arg0ResultSeq.size(); idx++) {
       if (idx < (arg0ResultSeq.size() - 1)) {
          sb.append(((arg0ResultSeq.item(idx)).str()) + separator);    
       }
       else {
          sb.append((arg0ResultSeq.item(idx)).str());    
       }
    }        

    return new XString(sb.toString());
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
     if (argNum < 1 || argNum > 2) {
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
                                                XPATHErrorResources.ER_ONE_OR_TWO, null)); //"1 or 2"
  }
  
}
