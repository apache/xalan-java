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
package org.apache.xpath.functions.string;

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
 * Implementation of the fn:replace function.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncReplace extends Function4Args {
    
   static final long serialVersionUID = 400116356230813776L;
   
   private static final String FUNCTION_NAME = "replace()";

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
	    
	    XObject xpath3ContextItem = xctxt.getXPath3ContextItem();
	    String inputStr = null;
	    if ((m_arg0 instanceof SelfIteratorNoPredicate) && (xpath3ContextItem != null)) {
	       inputStr = XslTransformEvaluationHelper.getStrVal(xpath3ContextItem); 
	    }
	    else {
           inputStr = XslTransformEvaluationHelper.getStrVal(m_arg0.execute(xctxt));
	    }
        String patternStr = XslTransformEvaluationHelper.getStrVal(m_arg1.execute(xctxt));
        String replacementStr = XslTransformEvaluationHelper.getStrVal(m_arg2.execute(xctxt));
        
        String flagStr = null;
        
        if (m_arg3 != null) {
           flagStr = XslTransformEvaluationHelper.getStrVal(m_arg3.execute(xctxt));
           if (!RegexEvaluationSupport.isFlagStrValid(flagStr)) {
               throw new javax.xml.transform.TransformerException(XSLMessages.createXPATHMessage(XPATHErrorResources.
                                                                                                                ER_INVALID_REGEX_FLAGS, new Object[]{ FUNCTION_NAME }), 
            		   																						    srcLocator);     
           }
        }
        
        try {
            Matcher matcher = RegexEvaluationSupport.regex(RegexEvaluationSupport.transformRegexStrForSubtractionOp(
            		                                                                                             patternStr), flagStr, inputStr);
            String resultStr = matcher.replaceAll(replacementStr);
            result = new XSString(resultStr);
        }
        catch (PatternSyntaxException ex) {
            throw new javax.xml.transform.TransformerException(XSLMessages.createXPATHMessage(XPATHErrorResources.
                                                                                                                ER_INVALID_REGEX, new Object[]{ FUNCTION_NAME }), 
                                                                                                                srcLocator);   
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
  
}
