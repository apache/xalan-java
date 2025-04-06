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

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.SourceLocator;

import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.utils.XMLString;
import org.apache.xml.utils.XMLStringFactory;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.Function3Args;
import org.apache.xpath.functions.RegexEvaluationSupport;
import org.apache.xpath.functions.WrongNumberArgsException;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLStringFactoryImpl;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;
import org.apache.xpath.regex.Matcher;
import org.apache.xpath.regex.PatternSyntaxException;
import org.apache.xpath.res.XPATHErrorResources;

import xml.xpath31.processor.types.XSString;

/**
 * Implementation of the fn:tokenize function.
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
        
        String arg0Str = XslTransformEvaluationHelper.getStrVal(m_arg0.execute(xctxt));
        
        XMLString inputStr = new XString(arg0Str);
        
        if (m_arg1 == null && m_arg2 == null) {
        	// The function has been called with only the first argument supplied.
        	
        	/**
        	 * While calling this function, if only first argument is supplied then this 
             * function splits the supplied string at whitespace boundaries. i.e, fn:tokenize($input) is 
             * equivalent to calling fn:tokenize(fn:normalize-space($input), ' ')) where the second 
             * argument is a single space character (x20).
        	 */
            
            XMLString effectiveInpStr = inputStr.fixWhiteSpace(true, true, false);            
            char[] buf = new char[1];
            buf[0] = ' ';
            XMLStringFactory xsf = XMLStringFactoryImpl.getFactory();
            XMLString pattern = xsf.newstr(new String(buf, 0, 1));
            
            List<String> tokenList = null;
            
            try {
               tokenList = tokenize(effectiveInpStr.toString(), pattern.toString(), null);
            }
            catch (PatternSyntaxException ex) {
                throw new javax.xml.transform.TransformerException(XSLMessages.createXPATHMessage(XPATHErrorResources.
                                                          									ER_INVALID_REGEX, new Object[]{ FUNCTION_NAME }), srcLocator);   
            }
            catch (Exception ex) {
                throw new javax.xml.transform.TransformerException(ex.getMessage(), srcLocator); 
            }
            
            for (int idx = 0; idx < tokenList.size(); idx++) {
                resultSeq.add(new XSString(tokenList.get(idx)));    
            }
        }
        else {
        	String patternStr = null;

        	if (m_arg1 != null) {
        	    patternStr = XslTransformEvaluationHelper.getStrVal(m_arg1.execute(xctxt));
        	}

        	String flagsStr = null;

        	if (m_arg2 != null) {
        		flagsStr = XslTransformEvaluationHelper.getStrVal(m_arg2.execute(xctxt));
        		if (!RegexEvaluationSupport.isFlagStrValid(flagsStr)) {               
        			throw new javax.xml.transform.TransformerException(XSLMessages.createXPATHMessage(XPATHErrorResources.
        																						ER_INVALID_REGEX_FLAGS, new Object[]{ FUNCTION_NAME }), srcLocator); 
        		}
        	}

        	List<String> tokenList = null;

        	try {
        		tokenList = tokenize(inputStr.toString(), patternStr, flagsStr);
        	}
        	catch (PatternSyntaxException ex) {
        		throw new javax.xml.transform.TransformerException(XSLMessages.createXPATHMessage(XPATHErrorResources.
        				ER_INVALID_REGEX, new Object[]{ FUNCTION_NAME }), srcLocator);  
        	}
        	catch (Exception ex) {
        		throw new javax.xml.transform.TransformerException(ex.getMessage(), srcLocator);   
        	}

        	for (int idx = 0; idx < tokenList.size(); idx++) {
        		resultSeq.add(new XSString(tokenList.get(idx)));    
        	}
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
  
  /**
   * Tokenize a given input string, using the regex pattern and flags strings.
   */
  private List<String> tokenize(String inputStr, String pattern, String flags) throws 
                                                                           PatternSyntaxException, Exception {

      List<String> tokens = new ArrayList<String>();

      Matcher regexMatcher = null;

      try {
          regexMatcher = RegexEvaluationSupport.regex(RegexEvaluationSupport.transformRegexStrForSubtractionOp(
                                                                                            pattern.toString()), flags != null ? 
                                                                                            flags.toString() : null, inputStr.toString());
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
