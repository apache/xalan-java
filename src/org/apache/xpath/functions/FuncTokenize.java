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

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.SourceLocator;

import org.apache.xalan.res.XSLMessages;
import org.apache.xml.utils.XMLString;
import org.apache.xml.utils.XMLStringFactory;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLStringFactoryImpl;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;
import org.apache.xpath.regex.Matcher;
import org.apache.xpath.regex.PatternSyntaxException;
import org.apache.xpath.res.XPATHErrorResources;

/**
 * Execute the tokenize() function.
 * 
 * This function returns a sequence of strings constructed by splitting 
 * the input string wherever a separator is found. The separator is 
 * any substring that matches a given regular expression.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncTokenize extends Function3Args {

  private static final long serialVersionUID = -7790625909187393478L;
  
  private static final String FUNCTION_NAME = "tokenize()"; 

  /**
   * Execute the function. The function must return a valid object.
   * 
   * @param xctxt The current execution context.
   * 
   * @return A valid XObject.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
  {      
        
        ResultSequence resultSeq = new ResultSequence();
      
        SourceLocator srcLocator = xctxt.getSAXLocator();
        
        XMLString inputStr = m_arg0.execute(xctxt).xstr();
        
        if (m_arg1 == null && m_arg2 == null) {
            // while calling this function, if only first argument is present, then this 
            // function splits the supplied string at whitespace boundaries. i.e, fn:tokenize($input) is 
            // equivalent to calling fn:tokenize(fn:normalize-space($input), ' ')) where the second 
            // argument is a single space character (x20).
            XMLString effectiveInpStr = inputStr.fixWhiteSpace(true, true, false);            
            char[] buf = new char[1];
            buf[0] = ' ';
            XMLStringFactory xsf = XMLStringFactoryImpl.getFactory();
            XMLString pattern = xsf.newstr(new String(buf, 0, 1));
            
            List<String> tokenList = null;
            
            try {
               tokenList = tokenize(pattern.toString(), null, effectiveInpStr.toString());
            }
            catch (PatternSyntaxException ex) {
                throw new javax.xml.transform.TransformerException(XSLMessages.createXPATHMessage(XPATHErrorResources.
                                                          ER_INVALID_REGEX, new Object[]{ FUNCTION_NAME }), srcLocator);   
            }
            catch (Exception ex) {
                throw new javax.xml.transform.TransformerException(ex.getMessage(), srcLocator); 
            }
            
            for (int idx = 0; idx < tokenList.size(); idx++) {
                resultSeq.add(new XString(tokenList.get(idx)));    
            }
            
            return resultSeq;
        }
        
        XMLString pattern = null;
        
        if (m_arg1 != null) {
            pattern = m_arg1.execute(xctxt).xstr();  
        }
        
        XMLString flags = null;
        
        if (m_arg2 != null) {
           flags = m_arg2.execute(xctxt).xstr();
           if (!RegexEvaluationSupport.isFlagStrValid(flags.toString())) {               
              throw new javax.xml.transform.TransformerException(XSLMessages.createXPATHMessage(XPATHErrorResources.
                                                                       ER_INVALID_REGEX_FLAGS, new Object[]{ FUNCTION_NAME }),
                                                                       srcLocator); 
           }
        }
        
        String flagsStr = (flags != null) ? flags.toString() : null;
        
        List<String> tokenList = null;
        
        try {
           tokenList = tokenize(pattern.toString(), flagsStr, inputStr.toString());
        }
        catch (PatternSyntaxException ex) {
           throw new javax.xml.transform.TransformerException(XSLMessages.createXPATHMessage(XPATHErrorResources.
                                                         ER_INVALID_REGEX, new Object[]{ FUNCTION_NAME }), srcLocator);  
        }
        catch (Exception ex) {
           throw new javax.xml.transform.TransformerException(ex.getMessage(), srcLocator);   
        }
            
        for (int idx = 0; idx < tokenList.size(); idx++) {
            resultSeq.add(new XString(tokenList.get(idx)));    
        }
        
        return resultSeq;
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
     if (!(argNum == 1 || argNum == 2 || argNum == 3)) {
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
                                              XPATHErrorResources.ER_ONE_TWO_OR_THREE, null)); //"1, 2 or 3"
  }
  
  private List<String> tokenize(String pattern, String flags, String inputStr) throws 
                                                                PatternSyntaxException, Exception {

      List<String> tokens = new ArrayList<String>();

      Matcher regexMatcher = null;

      try {
          regexMatcher = RegexEvaluationSupport.regex(RegexEvaluationSupport.trfPatternStrForSubtraction(
                                                  pattern.toString()), flags != null ? flags.toString() : 
                                                                                  null, inputStr.toString());
      }
      catch (PatternSyntaxException ex) {
          throw ex;   
      }

      int startpos = 0;
      int endpos = inputStr.length();

      while (regexMatcher.find()) {
          String delim = regexMatcher.group();
          if (delim.length() == 0) {
              throw new Exception("FORX0003 : The regular expression must not match zero-length string.");
          }
          String token = inputStr.substring(startpos, regexMatcher.start());
          startpos = regexMatcher.end();
          tokens.add(token);
      }

      if (startpos < endpos) {
          String token = inputStr.substring(startpos, endpos);
          tokens.add(token);
      }
      else if (startpos == endpos) {
          tokens.add("");
      }

      return tokens;
  }
  
}
