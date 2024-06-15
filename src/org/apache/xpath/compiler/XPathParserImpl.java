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
package org.apache.xpath.compiler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.xml.XMLConstants;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

import org.apache.xalan.res.XSLMessages;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xpath.XPathProcessorException;
import org.apache.xpath.composite.ForExpr;
import org.apache.xpath.composite.ForQuantifiedExprVarBinding;
import org.apache.xpath.composite.IfExpr;
import org.apache.xpath.composite.LetExpr;
import org.apache.xpath.composite.LetExprVarBinding;
import org.apache.xpath.composite.XPathMapConstructor;
import org.apache.xpath.composite.QuantifiedExpr;
import org.apache.xpath.composite.SequenceTypeArrayTest;
import org.apache.xpath.composite.SequenceTypeData;
import org.apache.xpath.composite.SequenceTypeFunctionTest;
import org.apache.xpath.composite.SequenceTypeKindTest;
import org.apache.xpath.composite.SequenceTypeMapTest;
import org.apache.xpath.composite.SequenceTypeSupport;
import org.apache.xpath.composite.XPathArrayConstructor;
import org.apache.xpath.composite.XPathSequenceConstructor;
import org.apache.xpath.composite.XPathSequenceTypeExpr;
import org.apache.xpath.domapi.XPathStylesheetDOM3Exception;
import org.apache.xpath.functions.DynamicFunctionCall;
import org.apache.xpath.objects.InlineFunction;
import org.apache.xpath.objects.InlineFunctionParameter;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XString;
import org.apache.xpath.res.XPATHErrorResources;

/**
 * Tokenizes and parses XPath expressions.
 * 
 * Ref: https://www.w3.org/TR/xpath-31/
 *      (please also refer to section 'A XPath 3.1 Grammar', within
 *       this document)
 * 
 * @author Scott Boag <scott_boag@us.ibm.com>
 * @author Myriam Midy <mmidy@apache.org>
 * @author Joseph Kesselman <jkesselm@apache.org>, Ilene Seelemann, Yash Talwar, 
 *         Henry Zongaro <zongaro@ca.ibm.com>, Christine Li <jycli@apache.org>
 *         
 * @author Mukul Gandhi <mukulg@apache.org>
 *         (XPath 3 specific changes, to this class)
 * 
 * @xsl.usage general
 */
public class XPathParserImpl
{

  /**
   * The XPath to be processed.
   */
  private OpMap m_ops;

  /**
   * The next token in the pattern.
   */
  transient String m_token;

  /**
   * The first char in m_token, the theory being that this
   * is an optimization because we won't have to do charAt(0) as
   * often.
   */
  transient char m_tokenChar = 0;

  /**
   * The position in the token queue is tracked by m_queueMark.
   */
  int m_queueMark = 0;
  
  private static final String CONTINUE_AFTER_FATAL_ERROR = "CONTINUE_AFTER_FATAL_ERROR";

  /**
   * Results from checking FilterExpr syntax
   */
  protected final static int FILTER_MATCH_FAILED     = 0;
  protected final static int FILTER_MATCH_PRIMARY    = 1;
  protected final static int FILTER_MATCH_PREDICATES = 2;
  
  /*
   * While parsing certain XPath 3.1 expressions, we use this constant string 
   * array to make parse decisions. The elements of this array are certain 
   * XPath language key words and symbols that need this support.
   */
  private static final String[] XPATH_OP_ARR = new String[] 
                                                     { "div", "or", "and", "mod", "to", 
                                                      "eq", "ne", "lt", "gt", "le", "ge", 
                                                      "for", "in", "return", "if", "then", 
                                                      "else", "some", "every", "satisfies", 
                                                      "let", ":=", "-", "||", "=>", "instance", 
                                                      "of", "as" };
  
  // If an XPath expression is () (i.e, representing an xdm empty sequence),
  // we translate that within this XPath parser implementation, to an XPath
  // range "to" expression using this class field (this equivalently produces
  // an xdm empty sequence).
  public static final String XPATH_EXPR_STR_EMPTY_SEQUENCE = "1 to 0";
  
  private static final List<String> fXpathOpArrTokensList = Arrays.asList(XPATH_OP_ARR);
  
  private boolean fDynamicFunctionCallArgumentMarker = false;
  
  private boolean fIsXPathPredicateParsingActive = false;
  
  private boolean fIsXPathExprBeginParse = false;
  
  private boolean fIsSequenceTypeXPathExpr = false;
  
  static InlineFunction fInlineFunction = null;
  
  static DynamicFunctionCall fDynamicFunctionCall = null;
  
  static ForExpr fForExpr = null;
  
  static LetExpr fLetExpr = null;
  
  static QuantifiedExpr fQuantifiedExpr = null;
  
  static IfExpr fIfExpr = null;
  
  static XPathSequenceConstructor fXPathSequenceConstructor = null;
  
  static XPathArrayConstructor fXPathArrayConstructor = null;
  
  /**
   * This class supports, implementation of literal array as XPath 
   * function call arguments.
   */
  static class XPathArrayConsFuncArgs {	 
	  
	 public List<Boolean> isFuncArgUsedArr = new ArrayList<Boolean>();
	 
	 public List<XPathArrayConstructor> arrayFuncArgList = new ArrayList<XPathArrayConstructor>();

	 public List<Boolean> getIsFuncArgUsedArr() {
		return isFuncArgUsedArr;
	 }

	 public void setIsFuncArgUsedArr(List<Boolean> isArgUsedArr) {
		this.isFuncArgUsedArr = isArgUsedArr;
	 }

	 public List<XPathArrayConstructor> getArrayFuncArgList() {
		return arrayFuncArgList;
	 }

	 public void setArrayFuncArgList(List<XPathArrayConstructor> arrayFuncArgList) {
		this.arrayFuncArgList = arrayFuncArgList;
	 } 
  }
  
  static XPathArrayConsFuncArgs fXPathArrayConsFuncArgs = null;
  
  /**
   * This class supports, implementation of literal sequence as XPath 
   * function call arguments.
   */
  static class XPathSequenceConsFuncArgs {	 
	  
	 public List<Boolean> isFuncArgUsedList = new ArrayList<Boolean>();
	 
	 public List<XPathSequenceConstructor> seqFuncArgList = new ArrayList<XPathSequenceConstructor>();

	 public List<Boolean> getIsFuncArgUsedList() {
		return isFuncArgUsedList;
	 }

	 public void setIsFuncArgUsedList(List<Boolean> isArgUsedList) {
		this.isFuncArgUsedList = isArgUsedList;
	 }

	 public List<XPathSequenceConstructor> getSeqFuncArgList() {
		return seqFuncArgList;
	 }

	 public void setSeqFuncArgList(List<XPathSequenceConstructor> seqFuncArgList) {
		this.seqFuncArgList = seqFuncArgList;
	 } 
  }
  
  static XPathSequenceConsFuncArgs fXPathSequenceConsFuncArgs = null;
  
  static XPathMapConstructor fXPathMapConstructor = null;
  
  static XPathSequenceTypeExpr fXPathSequenceTypeExpr = null;
  
  private String fArrowOpRemainingXPathExprStr = null;
  
  /**
   * The parser constructor.
   */
  public XPathParserImpl(ErrorListener errorListener, javax.xml.transform.SourceLocator sourceLocator)
  {
    m_errorListener = errorListener;
    m_sourceLocator = sourceLocator;
  }

  /**
   * The prefix resolver to map prefixes to namespaces in the OpMap.
   */
  PrefixResolver m_namespaceContext;

  /**
   * Given an string, init an XPath object for selections,
   * in order that a parse doesn't
   * have to be done each time the expression is evaluated.
   * 
   * @param compiler The compiler object.
   * @param expression A string conforming to the XPath grammar.
   * @param namespaceContext An object that is able to resolve prefixes in
   * the XPath to namespaces.
   * @param isSequenceTypeXPathExpr When this method is called, with this parameter
   *                                set to boolean value 'true', then an XPath parser object 
   *                                instance, instantiated from this class shall parse the XPath
   *                                expression string assuming that it represents an XPath 3.1
   *                                sequence type expression (for e.g, as a value of "as" 
   *                                attribute of xsl:variable instruction). 
   *
   * @throws javax.xml.transform.TransformerException
   */
  public void initXPath(
          Compiler compiler, String expression, PrefixResolver namespaceContext, boolean isSequenceTypeXPathExpr)
            throws javax.xml.transform.TransformerException
  {

    m_ops = compiler;
    m_namespaceContext = namespaceContext;
    m_functionTable = compiler.getFunctionTable();
    
    fIsSequenceTypeXPathExpr = isSequenceTypeXPathExpr;
    
    fXPathArrayConsFuncArgs = new XPathArrayConsFuncArgs();
    
    fXPathSequenceConsFuncArgs = new XPathSequenceConsFuncArgs(); 

    Lexer lexer = new Lexer(compiler, namespaceContext, this);

    lexer.tokenize(expression);

    m_ops.setOp(0,OpCodes.OP_XPATH);
    m_ops.setOp(OpMap.MAPINDEX_LENGTH,2);
    
    
	// Patch for Christine's gripe. She wants her errorHandler to return from
	// a fatal error and continue trying to parse, rather than throwing an exception.
	// Without the patch, that put us into an endless loop.
	//
	// %REVIEW% Is there a better way of doing this?
	// %REVIEW% Are there any other cases which need the safety net?
	// 	(and if so do we care right now, or should we rewrite the XPath
	//	grammar engine and can fix it at that time?)
	try {

      nextToken();
      
      fIsXPathExprBeginParse = true;
      
      Expr();

      if (null != m_token)
      {
        String extraTokens = "";

        while (null != m_token)
        {
          extraTokens += "'" + m_token + "'";

          nextToken();

          if (null != m_token)
            extraTokens += ", ";
        }

        error(XPATHErrorResources.ER_EXTRA_ILLEGAL_TOKENS,
              new Object[]{ extraTokens });  //"Extra illegal tokens: "+extraTokens);
      }

    } 
    catch (org.apache.xpath.XPathProcessorException e)
    {
	  if(CONTINUE_AFTER_FATAL_ERROR.equals(e.getMessage()))
	  {
		// What I _want_ to do is null out this XPath.
		// I doubt this has the desired effect, but I'm not sure what else to do.
		// %REVIEW%!!!
		initXPath(compiler, "/..",  namespaceContext, false);
	  }
	  else
		throw e;
    }

    compiler.shrink();
  }

  /**
   * Given an string, init an XPath object for pattern matches,
   * in order that a parse doesn't
   * have to be done each time the expression is evaluated.
   * @param compiler The XPath object to be initialized.
   * @param expression A String representing the XPath.
   * @param namespaceContext An object that is able to resolve prefixes in
   * the XPath to namespaces.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public void initMatchPattern(
          Compiler compiler, String expression, PrefixResolver namespaceContext)
            throws javax.xml.transform.TransformerException
  {

    m_ops = compiler;
    m_namespaceContext = namespaceContext;
    m_functionTable = compiler.getFunctionTable();

    Lexer lexer = new Lexer(compiler, namespaceContext, this);

    lexer.tokenize(expression);

    m_ops.setOp(0, OpCodes.OP_MATCHPATTERN);
    m_ops.setOp(OpMap.MAPINDEX_LENGTH, 2);

    nextToken();
    Pattern();

    if (null != m_token)
    {
      String extraTokens = "";

      while (null != m_token)
      {
        extraTokens += "'" + m_token + "'";

        nextToken();

        if (null != m_token)
          extraTokens += ", ";
      }

      error(XPATHErrorResources.ER_EXTRA_ILLEGAL_TOKENS,
            new Object[]{ extraTokens });  //"Extra illegal tokens: "+extraTokens);
    }

    m_ops.setOp(m_ops.getOp(OpMap.MAPINDEX_LENGTH), OpCodes.ENDOP);
    m_ops.setOp(OpMap.MAPINDEX_LENGTH, m_ops.getOp(OpMap.MAPINDEX_LENGTH)+1);

    m_ops.shrink();
  }

  /** The error listener where syntax errors are to be sent.
   */
  private ErrorListener m_errorListener;
  
  /** The source location of the XPath. */
  javax.xml.transform.SourceLocator m_sourceLocator;
  
  /** The table contains build-in functions and customized functions */
  private FunctionTable m_functionTable;

  /**
   * Allow an application to register an error event handler, where syntax 
   * errors will be sent.  If the error listener is not set, syntax errors 
   * will be sent to System.err.
   * 
   * @param handler Reference to error listener where syntax errors will be 
   *                sent.
   */
  public void setErrorHandler(ErrorListener handler)
  {
    m_errorListener = handler;
  }

  /**
   * Return the current error listener.
   *
   * @return The error listener, which should not normally be null, but may be.
   */
  public ErrorListener getErrorListener()
  {
    return m_errorListener;
  }

  /**
   * Check whether m_token matches the target string. 
   *
   * @param s A string reference or null.
   *
   * @return If m_token is null, returns false (or true if s is also null), or 
   * return true if the current token matches the string, else false.
   */
  final boolean tokenIs(String s)
  {
    return (m_token != null) ? (m_token.equals(s)) : (s == null);
  }

  /**
   * Check whether m_tokenChar==c. 
   *
   * @param c A character to be tested.
   *
   * @return If m_token is null, returns false, or return true if c matches 
   *         the current token.
   */
  final boolean tokenIs(char c)
  {
    return (m_token != null) ? (m_tokenChar == c) : false;
  }

  /**
   * Look ahead of the current token in order to
   * make a branching decision.
   *
   * @param c the character to be tested for.
   * @param n number of tokens to look ahead.  Must be
   * greater than 1.
   *
   * @return true if the next token matches the character argument.
   */
  final boolean lookahead(char c, int n)
  {

    int pos = (m_queueMark + n);
    boolean b;

    if ((pos <= m_ops.getTokenQueueSize()) && (pos > 0)
            && (m_ops.getTokenQueueSize() != 0))
    {
      String tok = ((String) m_ops.m_tokenQueue.elementAt(pos - 1));

      b = (tok.length() == 1) ? (tok.charAt(0) == c) : false;
    }
    else
    {
      b = false;
    }

    return b;
  }

  /**
   * Look behind the first character of the current token in order to
   * make a branching decision.
   * 
   * @param c the character to compare it to.
   * @param n number of tokens to look behind.  Must be
   * greater than 1.  Note that the look behind terminates
   * at either the beginning of the string or on a '|'
   * character.  Because of this, this method should only
   * be used for pattern matching.
   *
   * @return true if the token behind the current token matches the character 
   *         argument.
   */
  private final boolean lookbehind(char c, int n)
  {

    boolean isToken;
    int lookBehindPos = m_queueMark - (n + 1);

    if (lookBehindPos >= 0)
    {
      String lookbehind = (String) m_ops.m_tokenQueue.elementAt(lookBehindPos);

      if (lookbehind.length() == 1)
      {
        char c0 = (lookbehind == null) ? '|' : lookbehind.charAt(0);

        isToken = (c0 == '|') ? false : (c0 == c);
      }
      else
      {
        isToken = false;
      }
    }
    else
    {
      isToken = false;
    }

    return isToken;
  }

  /**
   * look behind the current token in order to
   * see if there is a useable token.
   * 
   * @param n number of tokens to look behind.  Must be
   * greater than 1.  Note that the look behind terminates
   * at either the beginning of the string or on a '|'
   * character.  Because of this, this method should only
   * be used for pattern matching.
   * 
   * @return true if look behind has a token, false otherwise.
   */
  private final boolean lookbehindHasToken(int n)
  {

    boolean hasToken;

    if ((m_queueMark - n) > 0)
    {
      String lookbehind = (String) m_ops.m_tokenQueue.elementAt(m_queueMark - (n - 1));
      char c0 = (lookbehind == null) ? '|' : lookbehind.charAt(0);

      hasToken = (c0 == '|') ? false : true;
    }
    else
    {
      hasToken = false;
    }

    return hasToken;
  }

  /**
   * Look ahead of the current token in order to
   * make a branching decision.
   * 
   * @param s the string to compare it to.
   * @param n number of tokens to lookahead.  Must be
   * greater than 1.
   *
   * @return true if the token behind the current token matches the string 
   *         argument.
   */
  private final boolean lookahead(String s, int n)
  {

    boolean isToken;

    if ((m_queueMark + n) <= m_ops.getTokenQueueSize())
    {
      String lookahead = (String) m_ops.m_tokenQueue.elementAt(m_queueMark + (n - 1));

      isToken = (lookahead != null) ? lookahead.equals(s) : (s == null);
    }
    else
    {
      isToken = (null == s);
    }

    return isToken;
  }

  /**
   * Retrieve the next token from the command and
   * store it in m_token string.
   */
  private final void nextToken()
  {

    if (m_queueMark < m_ops.getTokenQueueSize())
    {
      m_token = (String) m_ops.m_tokenQueue.elementAt(m_queueMark++);
      m_tokenChar = m_token.charAt(0);
    }
    else
    {
      m_token = null;
      m_tokenChar = 0;
    }
  }

  /**
   * Retrieve a token relative to the current token.
   * 
   * @param i Position relative to current token.
   *
   * @return The string at the given index, or null if the index is out 
   *         of range.
   */
  private final String getTokenRelative(int i)
  {

    String tok;
    int relative = m_queueMark + i;

    if ((relative > 0) && (relative < m_ops.getTokenQueueSize()))
    {
      tok = (String) m_ops.m_tokenQueue.elementAt(relative);
    }
    else
    {
      tok = null;
    }

    return tok;
  }

  /**
   * Retrieve the previous token from the command and
   * store it in m_token string.
   */
  private final void prevToken()
  {

    if (m_queueMark > 0)
    {
      m_queueMark--;

      m_token = (String) m_ops.m_tokenQueue.elementAt(m_queueMark);
      m_tokenChar = m_token.charAt(0);
    }
    else
    {
      m_token = null;
      m_tokenChar = 0;
    }
  }

  /**
   * Consume an expected token, throwing an exception if it
   * isn't there.
   *
   * @param expected The string to be expected.
   *
   * @throws javax.xml.transform.TransformerException
   */
  private final void consumeExpected(String expected)
          throws javax.xml.transform.TransformerException
  {

    if (tokenIs(expected))
    {
      nextToken();
    }
    else
    {
      error(XPATHErrorResources.ER_EXPECTED_BUT_FOUND, new Object[]{ expected,
                                                                     m_token });  //"Expected "+expected+", but found: "+m_token);

	  // Patch for Christina's gripe. She wants her errorHandler to return from
	  // this error and continue trying to parse, rather than throwing an exception.
	  // Without the patch, that put us into an endless loop.
		throw new XPathProcessorException(CONTINUE_AFTER_FATAL_ERROR);
	}
  }

  /**
   * Consume an expected token, throwing an exception if it
   * isn't there.
   *
   * @param expected the character to be expected.
   *
   * @throws javax.xml.transform.TransformerException
   */
  private final void consumeExpected(char expected)
          throws javax.xml.transform.TransformerException
  {

    if (tokenIs(expected))
    {
      nextToken();
    }
    else
    {
      error(XPATHErrorResources.ER_EXPECTED_BUT_FOUND,
            new Object[]{ String.valueOf(expected),
                          m_token });  //"Expected "+expected+", but found: "+m_token);

	  // Patch for Christina's gripe. She wants her errorHandler to return from
	  // this error and continue trying to parse, rather than throwing an exception.
	  // Without the patch, that put us into an endless loop.
		throw new XPathProcessorException(CONTINUE_AFTER_FATAL_ERROR);
    }
  }

  /**
   * Warn the user of a problem.
   *
   * @param msg An error msgkey that corresponds to one of the constants found 
   *            in {@link org.apache.xpath.res.XPATHErrorResources}, which is 
   *            a key for a format string.
   * @param args An array of arguments represented in the format string, which 
   *             may be null.
   *
   * @throws TransformerException if the current ErrorListoner determines to 
   *                              throw an exception.
   */
  void warn(String msg, Object[] args) throws TransformerException
  {

    String fmsg = XSLMessages.createXPATHWarning(msg, args);
    ErrorListener ehandler = this.getErrorListener();

    if (null != ehandler)
    {
      // TO DO: Need to get stylesheet Locator from here.
      ehandler.warning(new TransformerException(fmsg, m_sourceLocator));
    }
    else
    {
      // Should never happen.
      System.err.println(fmsg);
    }
  }
  
  private String getXPathStrFromComponentParts(List<String> xpathExprStrPartList) {
      StringBuffer funcBodyXPathExprStrBuff = new StringBuffer();
      Object[] funcBodyXPathExprStrPartsArr = xpathExprStrPartList.toArray();
      
      for (int idx = 0; idx < funcBodyXPathExprStrPartsArr.length; idx++) {
          String xpathExprStrPart = null;
          
          boolean isXpathInlineFunctionOpToken = false;
          
          if ("$".equals(funcBodyXPathExprStrPartsArr[idx]) && (idx < 
                                                                  (funcBodyXPathExprStrPartsArr.length - 1))) {
              // This handles, variable references within XPath expression string
              // that's been formed within this method.
              xpathExprStrPart = "$" + funcBodyXPathExprStrPartsArr[idx + 1];
              idx += 1;
          }
          else {              
              xpathExprStrPart = (String)funcBodyXPathExprStrPartsArr[idx];
              if (fXpathOpArrTokensList.contains(xpathExprStrPart)) {
                 isXpathInlineFunctionOpToken = true;   
              }
          }
          
          funcBodyXPathExprStrBuff.append(isXpathInlineFunctionOpToken ? " " + xpathExprStrPart + 
                                                                                        " " : xpathExprStrPart);
      }
      
      return (funcBodyXPathExprStrBuff.toString()).trim();
  }
  
  /**
   * While traversing XPath expression's token list, we determine within this method,
   * whether the parse of dynamic function call argument details should continue or
   * not. We simultaneously accumulate function call argument details within a list 
   * 'argDetailsStrPartsList' (that has been passed as an argument to this method) 
   * whose contents are accessible to the caller of this method.
   */
  private boolean isXPathDynamicFuncCallParseAhead(List<String> argDetailsStrPartsList, 
                                                                                  String delim) {
      boolean isXpathDynamicFuncCallParseAhead = false;

      if (lookahead('(', 1)) {
         // The function call argument is itself another function call
         fDynamicFunctionCallArgumentMarker = true;
         argDetailsStrPartsList.add(m_token);
         nextToken();
         isXpathDynamicFuncCallParseAhead = true;
      }
      else if (tokenIs(')') && lookahead(',', 1)) {
         argDetailsStrPartsList.add(m_token);          
         nextToken();
         argDetailsStrPartsList.add(delim);
         nextToken();
         fDynamicFunctionCallArgumentMarker = false;
         isXpathDynamicFuncCallParseAhead = true;
      }
      else if (lookahead(',', 1)) {
         argDetailsStrPartsList.add(m_token);
         nextToken();
         if (!fDynamicFunctionCallArgumentMarker) {
            argDetailsStrPartsList.add(delim);
            nextToken();
         }
         else {
            argDetailsStrPartsList.add(m_token);
            nextToken();
         }
         isXpathDynamicFuncCallParseAhead = true; 
      }
      else if (!tokenIs(')')) {
         argDetailsStrPartsList.add(m_token);
         nextToken();
         isXpathDynamicFuncCallParseAhead = true; 
      }
      else {
         String argDetailsAccumulatedStrSoFar = "(" + getStrValueFromStrList(
                                                                  argDetailsStrPartsList) + ")";         
         if (!isStrHasBalancedParentheses(argDetailsAccumulatedStrSoFar, '(', ')')) {
            argDetailsStrPartsList.add(m_token);
            nextToken();
            isXpathDynamicFuncCallParseAhead = true;
         }         
      }

      return isXpathDynamicFuncCallParseAhead; 
  }
  
  /**
   * Given a list of strings, concatenate list items to form
   * a string and return the string formed to the caller of 
   * this method.
   */
  private String getStrValueFromStrList(List<String> strList) {
      String concatenatedStrInfo = "";
      
      for (int idx = 0; idx < strList.size(); idx++) {
         concatenatedStrInfo = concatenatedStrInfo + strList.get(idx);   
      }
      
      return concatenatedStrInfo; 
  }
  
  /**
   * Check whether a, string has balanced parentheses pairs.
   */
  private boolean isStrHasBalancedParentheses(String str, char lParentType, char rParenType) {
     
     boolean isStrHasBalancedParentheses = true;
     
     Stack<Character> charStack = new Stack<Character>();
     
     int strLen = str.length();
     
     for(int idx = 0; idx < strLen; idx++) {
         char ch = str.charAt(idx);
         if (ch == lParentType) {
            charStack.push(ch); 
         }
         else if (ch == rParenType){
            if (charStack.isEmpty() || (charStack.pop() != lParentType)) {
               // unbalanced parentheses
               isStrHasBalancedParentheses = false;
               break;
            }   
         }
     }
     
     if (!charStack.isEmpty()) {
        isStrHasBalancedParentheses = false;
     }
     
     return isStrHasBalancedParentheses; 
  }
  
  /**
   * This method helps to implement, XPath 3.1 sequence type expressions.
   */
  private void setSequenceTypeOccurenceIndicator(XPathSequenceTypeExpr xpathSequenceTypeExpr, 
                                                                                   boolean isInlineFunction) throws TransformerException {
      if (tokenIs(SequenceTypeSupport.Q_MARK)) {
         xpathSequenceTypeExpr.setItemTypeOccurrenceIndicator(SequenceTypeSupport.
                                                                                 OccurenceIndicator.ZERO_OR_ONE);
         nextToken();
      }
      else if (tokenIs(SequenceTypeSupport.STAR)) {
         xpathSequenceTypeExpr.setItemTypeOccurrenceIndicator(SequenceTypeSupport.
                                                                                 OccurenceIndicator.ZERO_OR_MANY);
         nextToken();
      }
      else if (tokenIs(SequenceTypeSupport.PLUS)) {
         xpathSequenceTypeExpr.setItemTypeOccurrenceIndicator(SequenceTypeSupport.
                                                                                 OccurenceIndicator.ONE_OR_MANY);
         nextToken();
      }
      else if (!isInlineFunction) {
         throw new javax.xml.transform.TransformerException("XPST0051 A sequence type occurence indicator '" + m_token + "', is not recognized."); 
      }
   }
  
  /**
   * Given an XML node's raw name like abc:pqr, this method returns
   * a String array having the XML name components 'abc' and 'pqr'
   * within individual array elements.
   */
   private String[] getXmlNamespaceStrComponents(String xmlRawName) {     
      String[] xmlNamespaceStrParts = new String[2];
      
      int idx = xmlRawName.lastIndexOf(':');             
      String localName = null;
      String nsUri = null;
      if (idx != -1) {
         nsUri = xmlRawName.substring(0, idx);
         localName = xmlRawName.substring(idx + 1);                
      }
      else {
         localName = xmlRawName;  
      }
      
      xmlNamespaceStrParts[0] = localName;
      xmlNamespaceStrParts[1] = nsUri;
      
      return xmlNamespaceStrParts; 
   }
   
   /**
    * This method helps to implement, XPath 3.1 sequence type expressions.
    */
   private SequenceTypeKindTest constructSequenceTypeKindTestForXDMNodes(XPathSequenceTypeExpr 
                                                                                     xpathSequenceTypeExpr, 
                                                                                     int nodeType, 
                                                                                     boolean isInlineFunction) throws TransformerException {

       SequenceTypeKindTest sequenceTypeKindTest = new SequenceTypeKindTest();

       sequenceTypeKindTest.setKindVal(nodeType);          
       nextToken();
       consumeExpected('(');
       String nodeKindTestStr = "";
       while (!tokenIs(")") && m_token != null) {
           nodeKindTestStr += m_token;
           nextToken();
       }
       if (tokenIs(')')) {
           if (isInlineFunction) {
               nextToken();                
               setSequenceTypeOccurenceIndicator(xpathSequenceTypeExpr, isInlineFunction);  
           }
           else {
               if (getTokenRelative(0) == null) {
                   nextToken();                
               }
               else if (getTokenRelative(1) == null) {
                   nextToken();                
                   setSequenceTypeOccurenceIndicator(xpathSequenceTypeExpr, isInlineFunction);
               }
               else {
                   throw new javax.xml.transform.TransformerException("XPST0051 : The sequence type expression is "
                                                                                                          + "not well-formed."); 
               }
           }
       }
       else {
           throw new javax.xml.transform.TransformerException("XPST0051 : The sequence type expression is "
                                                                                                  + "not well-formed. An expected token ')' within "
                                                                                                  + "a sequence type expression is not present.");  
       }

       String[] seqTypeSubParts = nodeKindTestStr.split(",");
       if (seqTypeSubParts.length == 1) {
           String[] nsParts = getXmlNamespaceStrComponents(seqTypeSubParts[0]);
           sequenceTypeKindTest.setNodeLocalName(nsParts[0]);
           sequenceTypeKindTest.setNodeNsUri(nsParts[1]);
       }
       else if (seqTypeSubParts.length == 2) {
           String[] nodeNsParts = getXmlNamespaceStrComponents(seqTypeSubParts[0]);
           String[] nodeDataTypeParts = getXmlNamespaceStrComponents(seqTypeSubParts[1]);
           sequenceTypeKindTest.setNodeLocalName(nodeNsParts[0]);
           sequenceTypeKindTest.setNodeNsUri(nodeNsParts[1]);
           sequenceTypeKindTest.setDataTypeName(nodeDataTypeParts[0]);
           sequenceTypeKindTest.setDataTypeUri(nodeDataTypeParts[1]);
       }
       else if (seqTypeSubParts.length > 2) {
           throw new javax.xml.transform.TransformerException("XPST0051 : The sequence type expression is "
                                                                                                    + "not well-formed."); 
       }

       return sequenceTypeKindTest;
  }

  /**
   * Notify the user of an error, and probably throw an
   * exception.
   *
   * @param msg An error msgkey that corresponds to one of the constants found 
   *            in {@link org.apache.xpath.res.XPATHErrorResources}, which is 
   *            a key for a format string.
   * @param args An array of arguments represented in the format string, which 
   *             may be null.
   *
   * @throws TransformerException if the current ErrorListoner determines to 
   *                              throw an exception.
   */
  void error(String msg, Object[] args) throws TransformerException
  {

    String fmsg = XSLMessages.createXPATHMessage(msg, args);
    ErrorListener ehandler = this.getErrorListener();
    
    TransformerException te = new TransformerException(fmsg, m_sourceLocator);
    if (null != ehandler)
    {
      // TO DO: Need to get stylesheet Locator from here.
      ehandler.fatalError(te);
    }
    else
    {
      // System.err.println(fmsg);
      throw te;
    }
  }

  /**
   * This method is added to support DOM 3 XPath API.
   * <p>
   * This method is exactly like error(String, Object[]); except that
   * the underlying TransformerException is 
   * XpathStylesheetDOM3Exception (which extends TransformerException).
   * <p>
   * So older XPath code in Xalan is not affected by this. To older XPath code
   * the behavior of whether error() or errorForDOM3() is called because it is
   * always catching TransformerException objects and is oblivious to
   * the new subclass of XPathStylesheetDOM3Exception. Older XPath code 
   * runs as before.
   * <p>
   * However, newer DOM3 XPath code upon catching a TransformerException can
   * can check if the exception is an instance of XPathStylesheetDOM3Exception
   * and take appropriate action.
   * 
   * @param msg An error msgkey that corresponds to one of the constants found 
   *            in {@link org.apache.xpath.res.XPATHErrorResources}, which is 
   *            a key for a format string.
   * @param args An array of arguments represented in the format string, which 
   *             may be null.
   *
   * @throws TransformerException if the current ErrorListoner determines to 
   *                              throw an exception.
   */
  void errorForDOM3(String msg, Object[] args) throws TransformerException
  {

	String fmsg = XSLMessages.createXPATHMessage(msg, args);
	ErrorListener ehandler = this.getErrorListener();

	TransformerException te = new XPathStylesheetDOM3Exception(fmsg, m_sourceLocator);
	if (null != ehandler)
	{
	  // TO DO: Need to get stylesheet Locator from here.
	  ehandler.fatalError(te);
	}
	else
	{
	  // System.err.println(fmsg);
	  throw te;
	}
  }
  /**
   * Dump the remaining token queue.
   * Thanks to Craig for this.
   *
   * @return A dump of the remaining token queue, which may be appended to 
   *         an error message.
   */
  protected String dumpRemainingTokenQueue()
  {

    int q = m_queueMark;
    String returnMsg;

    if (q < m_ops.getTokenQueueSize())
    {
      String msg = "\n Remaining tokens: (";

      while (q < m_ops.getTokenQueueSize())
      {
        String t = (String) m_ops.m_tokenQueue.elementAt(q++);

        msg += (" '" + t + "'");
      }

      returnMsg = msg + ")";
    }
    else
    {
      returnMsg = "";
    }

    return returnMsg;
  }

  /**
   * Given a string, return the corresponding function token.
   *
   * @param key A local name of a function.
   *
   * @return   The function ID, which may correspond to one of the FUNC_XXX 
   *    values found in {@link org.apache.xpath.compiler.FunctionTable}, but may 
   *    be a value installed by an external module.
   */
  final int getFunctionToken(String key, String nsUri)
  {

    int tok;
    
    Object id;

    try
    {
      // These are nodetests, xpathparser treats them as functions when parsing
      // a FilterExpr. 
      id = Keywords.lookupNodeTest(key);
      if (id == null) {
    	if ((FunctionTable.XPATH_BUILT_IN_FUNCS_NS_URI).equals(nsUri)) {
    	   // This check is there, to avoid XPath parse conflicts with map & array 
    	   // functions with same local name as functions from namespace http://www.w3.org/2005/xpath-functions. 
    	   id = getFunctionTokenForXPathBuiltinFuncs(key);
    	}    	
    	else if ((FunctionTable.XPATH_BUILT_IN_MAP_FUNCS_NS_URI).equals(nsUri)) {    	       	   
    	   id = getFunctionTokenForXPathBuiltinMapFuncs(key);
    	}
    	else if ((FunctionTable.XPATH_BUILT_IN_ARRAY_FUNCS_NS_URI).equals(nsUri)) {     	   
     	  id = getFunctionTokenForXPathBuiltinArrayFuncs(key);
     	}
    	else {
    	  id = m_functionTable.getFunctionID(key);
    	}
      }
      tok = ((Integer) id).intValue();
    }
    catch (NullPointerException npe)
    {
      tok = -1;
    }
    catch (ClassCastException cce)
    {
      tok = -1;
    }

    return tok;
  }

  /**
   * Get function token, for XPath built-in functions from xpath namespace 
   * http://www.w3.org/2005/xpath-functions. 
   */
  private Object getFunctionTokenForXPathBuiltinFuncs(String key) {	
		Object id = null;
		
		switch (key) {
		      case "contains":
			     id = FunctionTable.FUNC_CONTAINS;
			     break;
		      case "remove":
			     id = FunctionTable.FUNC_REMOVE;
			     break;		      
		      case "insert-before":
			     id = FunctionTable.FUNC_INSERT_BEFORE;
			     break;
		      case "head":
			     id = FunctionTable.FUNC_HEAD;
			     break;
		      case "tail":
			     id = FunctionTable.FUNC_TAIL;
			     break;
		      case "reverse":
				 id = FunctionTable.FUNC_REVERSE;
				 break;
		      case "for-each":
				 id = FunctionTable.FUNC_FOR_EACH;
				 break;
		      case "filter":
				 id = FunctionTable.FUNC_FILTER;
				 break;
		      case "for-each-pair":
				 id = FunctionTable.FUNC_FOR_EACH_PAIR;
				 break;
		      case "fold-left":
				 id = FunctionTable.FUNC_FOLD_LEFT;
				 break;
		      case "fold-right":
			     id = FunctionTable.FUNC_FOLD_RIGHT;
			     break;
		      case "sort":
				 id = FunctionTable.FUNC_SORT;
				 break;
			  default:
				 id = m_functionTable.getFunctionID(key); 
		}
		
		return id;	
  }
  
  /**
   * Get function token, for XPath built-in map functions from xpath namespace
   * http://www.w3.org/2005/xpath-functions/map. 
   */
  private Object getFunctionTokenForXPathBuiltinMapFuncs(String key) {		
		Object id = null;
		
		switch (key) {
		      case "merge":
		        id = FunctionTable.FUNC_MAP_MERGE;
		        break;
		      case "size":
			     id = FunctionTable.FUNC_MAP_SIZE;
			     break;
		      case "keys":
				 id = FunctionTable.FUNC_MAP_KEYS;
				 break;
		      case "contains":
				 id = FunctionTable.FUNC_MAP_CONTAINS;
				 break;
		      case "get":
			     id = FunctionTable.FUNC_MAP_GET;
			     break;
		      case "put":
			     id = FunctionTable.FUNC_MAP_PUT;
			     break;		      		      
		      case "entry":
			     id = FunctionTable.FUNC_MAP_ENTRY;
			     break;
		      case "remove":
				 id = FunctionTable.FUNC_MAP_REMOVE;
				 break;
		      case "for-each":
			     id = FunctionTable.FUNC_MAP_FOREACH;
			     break;
			  default:
				 // NO OP 
		}
		
		return id;	
  }
  
  /**
   * Get function token, for XPath built-in array functions from xpath namespace
   * http://www.w3.org/2005/xpath-functions/array. 
   */
  private Object getFunctionTokenForXPathBuiltinArrayFuncs(String key) {	  
		Object id = null;
		
		switch (key) {
		      case "size":
			     id = FunctionTable.FUNC_ARRAY_SIZE;
			     break;
		      case "get":
			     id = FunctionTable.FUNC_ARRAY_GET;
			     break;
		      case "put":
			     id = FunctionTable.FUNC_ARRAY_PUT;
			     break;
		      case "append":
			     id = FunctionTable.FUNC_ARRAY_APPEND;
			     break;
		      case "subarray":
			     id = FunctionTable.FUNC_ARRAY_SUBARRAY;
			     break;
		      case "remove":
			     id = FunctionTable.FUNC_ARRAY_REMOVE;
			     break;
		      case "insert-before":
			     id = FunctionTable.FUNC_ARRAY_INSERT_BEFORE;
			     break;
		      case "head":
			     id = FunctionTable.FUNC_ARRAY_HEAD;
			     break;
		      case "tail":
			     id = FunctionTable.FUNC_ARRAY_TAIL;
			     break;
		      case "reverse":
				 id = FunctionTable.FUNC_ARRAY_REVERSE;
				 break;
		      case "join":
			     id = FunctionTable.FUNC_ARRAY_JOIN;
				 break;
		      case "for-each":
				 id = FunctionTable.FUNC_ARRAY_FOR_EACH;
				 break;
		      case "filter":
			     id = FunctionTable.FUNC_ARRAY_FILTER;
			     break;
		      case "for-each-pair":
			     id = FunctionTable.FUNC_ARRAY_FOR_EACH_PAIR;
				 break;
		      case "fold-left":
				 id = FunctionTable.FUNC_ARRAY_FOLD_LEFT;
			     break;
		      case "fold-right":
			     id = FunctionTable.FUNC_ARRAY_FOLD_RIGHT;
				 break;
		      case "sort":
				 id = FunctionTable.FUNC_ARRAY_SORT;
			     break;
			  default:
				 // NO OP 
		}
		
		return id;		
   }

  /**
   * Insert room for operation.  This will NOT set
   * the length value of the operation, but will update
   * the length value for the total expression.
   *
   * @param pos The position where the op is to be inserted.
   * @param length The length of the operation space in the op map.
   * @param op The op code to the inserted.
   */
  void insertOp(int pos, int length, int op)
  {

    int totalLen = m_ops.getOp(OpMap.MAPINDEX_LENGTH);

    for (int i = totalLen - 1; i >= pos; i--)
    {
      m_ops.setOp(i + length, m_ops.getOp(i));
    }

    m_ops.setOp(pos,op);
    m_ops.setOp(OpMap.MAPINDEX_LENGTH,totalLen + length);
  }

  /**
   * Insert room for operation.  This WILL set
   * the length value of the operation, and will update
   * the length value for the total expression.
   *
   * @param length The length of the operation.
   * @param op The op code to the inserted.
   */
  void appendOp(int length, int op)
  {

    int totalLen = m_ops.getOp(OpMap.MAPINDEX_LENGTH);

    m_ops.setOp(totalLen, op);
    m_ops.setOp(totalLen + OpMap.MAPINDEX_LENGTH, length);
    m_ops.setOp(OpMap.MAPINDEX_LENGTH, totalLen + length);
  }

  // ============= EXPRESSIONS FUNCTIONS =================
  
  /**
   * Expr   ::=   ExprSingle ("," ExprSingle)*
   * 
   * @throws javax.xml.transform.TransformerException
   */
  protected void Expr() throws javax.xml.transform.TransformerException
  {	  
	  if (fIsXPathExprBeginParse && tokenIs("(") && (lookahead("to", 2) || lookahead("to", 3))) {
		  // To parse an XPath expression like '(a to b) => function()', it seems simpler
		  // to handle its parse here.
		  
		  int opPos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);
		  
		  nextToken();
	      appendOp(2, OpCodes.OP_GROUP);     
	      Expr();
	      consumeExpected(')');
	      
	      m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH,
	    	                                     m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);
	      
	      if (tokenIs("=>")) {	    	  
	          consumeExpected("=>");

	          insertOp(opPos, 2, OpCodes.OP_ARROW);
	          
	          FunctionCall();
	          
	          if (m_token != null) {
	        	 fArrowOpRemainingXPathExprStr = "";  
	          }
	          
	          while (m_token != null) {
	        	 fArrowOpRemainingXPathExprStr += m_token;
	        	 nextToken();
	          }
	      }
	  }
	  else if (fIsXPathExprBeginParse && isSequenceOrArrayBegin()) {
    	  // We consider XPath parse of sequence and array, in similar 
    	  // way. These are different lexically only, by virtue of 
    	  // sequence/array expression's delimiter characters.
    	  
    	  boolean isSequenceConstructor = false;    	  
    	  if (tokenIs("(")) {
    		 isSequenceConstructor = true; 
    	  }
    	  
    	  boolean isSquareArrayConstructor = false;    	  
    	  if (tokenIs("[")) {
    		 isSquareArrayConstructor = true; 
    	  }
    	  
    	  boolean isCurlyArrayConstructor = false;
    	  if (tokenIs("array")) {
    		 isCurlyArrayConstructor = true; 
    	  }
    	  
          nextToken();
          
          if (isCurlyArrayConstructor) {
        	 consumeExpected('{'); 
          }
          
          List<String> seqOrArrayXPathItems = new ArrayList<String>();
          
          if (isSequenceConstructor && tokenIs(')') && (getTokenRelative(0) == null)) {
              // The XPath expression is ()
              
              int opPos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);
              
              nextToken();                            
              
              insertOp(opPos, 2, OpCodes.OP_SEQUENCE_CONSTRUCTOR_EXPR);
              
              seqOrArrayXPathItems.add(XPATH_EXPR_STR_EMPTY_SEQUENCE);
              
              fXPathSequenceConstructor = new XPathSequenceConstructor();              
              fXPathSequenceConstructor.setSequenceConstructorXPathParts(
                                                                    seqOrArrayXPathItems);
              
              m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH, 
            		                                 m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);
              
              return;
          }          
          else if (((isSquareArrayConstructor && tokenIs(']')) || (isCurlyArrayConstructor && tokenIs('}'))) && 
        		                                                                        (getTokenRelative(0) == null)) {
             // The XPath expression is [], or {}
        	 
             int opPos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);
              
             nextToken();                            
              
             insertOp(opPos, 2, OpCodes.OP_ARRAY_CONSTRUCTOR_EXPR);
             
             fXPathArrayConstructor = new XPathArrayConstructor();             
             fXPathArrayConstructor.setIsEmptyArray(true);
             
             m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH, 
                                                    m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);

             return;             
          }
          
          while (m_token != null) {                            
              if (tokenIs("function")) {
                 // An XPath expression parse of 'inline function expression'
                 List<String> inlineFuncBodyXPathStrPartsList = new ArrayList<String>();
                 
                 while (!tokenIs('}') && m_token != null) {
                    if (getTokenRelative(0) != null) {
                       inlineFuncBodyXPathStrPartsList.add(m_token);
                    }
                    nextToken();
                 }
                 
                 if (tokenIs('}')) {
                    inlineFuncBodyXPathStrPartsList.add(m_token);
                    nextToken();
                    if (tokenIs(',')) {
                       nextToken();    
                    }
                 }
                 
                 if (inlineFuncBodyXPathStrPartsList.size() > 0) {
                    String xpathExprStr = getXPathStrFromComponentParts(inlineFuncBodyXPathStrPartsList);                 
                    seqOrArrayXPathItems.add(xpathExprStr);
                 }
              }
              else {
                 List<String> xpathExprTokens = new ArrayList<String>();                 
                 
                 while (!tokenIs(",") && (m_token != null)) {
                    if (!lookahead(null, 1)) {
                       xpathExprTokens.add(m_token);
                    }
                    nextToken();
                 }
                 
                 if (xpathExprTokens.size() > 0) {
                    String seqOrArrayItemXPath = getXPathStrFromComponentParts(xpathExprTokens);
                    if (isSequenceConstructor && seqOrArrayItemXPath.endsWith(")")) {
                        seqOrArrayItemXPath = seqOrArrayItemXPath.substring(0, seqOrArrayItemXPath.length());    
                    }
                    else {
	                    boolean xpathExprCompletesLiteralArray = (isSquareArrayConstructor && seqOrArrayItemXPath.endsWith("]")) || 
	                    		                                 (isCurlyArrayConstructor && seqOrArrayItemXPath.endsWith("}"));
	                    if (xpathExprCompletesLiteralArray) {
	                        seqOrArrayItemXPath = seqOrArrayItemXPath.substring(0, seqOrArrayItemXPath.length());    
	                    }
                    }
                    
                    seqOrArrayXPathItems.add(seqOrArrayItemXPath);
                 }
                 
                 if (m_token != null) {
                    nextToken();   
                 }
              }                            
          }
          
          boolean isXPathParseOkToProceed = true;
          
          // We verify here that, each XPath string within the list 
          // 'xpathExprParts' has balanced parentheses pairs.
          for (int idx = 0; idx < seqOrArrayXPathItems.size(); idx++) {
             String seqOrArrayMemberXPathExprStr = seqOrArrayXPathItems.get(idx);
             char lParenChar = '(';
             char rParenChar = ')';
             if (isSequenceConstructor) {
            	 lParenChar = '(';
            	 rParenChar = ')';
             }
             else if (isSquareArrayConstructor) {
            	 lParenChar = '[';
            	 rParenChar = ']'; 
             }
             else if (isCurlyArrayConstructor) {
            	 lParenChar = '{';
            	 rParenChar = '}'; 
             }
             
             boolean isStrHasBalancedParentheses = isStrHasBalancedParentheses(
            		                                                    seqOrArrayMemberXPathExprStr, 
            		                                                    lParenChar, rParenChar);
             if (!isStrHasBalancedParentheses) {
                isXPathParseOkToProceed = false;
                break;
             }
          }
          
          // Verifying, whether sequence constructor expression, can build 
          // at-least two xdm items.          
	      // (Sequence constructor expressions having single '(' & ')' parentheses, 
          // are evaluated with Xalan-J parser op code OpCodes.OP_GROUP from 
          // within the Xalan-J parse method call ExprSingle())
	      if (isXPathParseOkToProceed && isSequenceConstructor) {
	    	 if (!(seqOrArrayXPathItems.size() > 1)) {
	    		isXPathParseOkToProceed = false; 
	    	 }
	      }
                             
          if (isXPathParseOkToProceed) {
              int opPos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);
              
              nextToken();
              
              if (isSquareArrayConstructor || isCurlyArrayConstructor) {
            	 insertOp(opPos, 2, OpCodes.OP_ARRAY_CONSTRUCTOR_EXPR);
            	 fXPathArrayConstructor = new XPathArrayConstructor();            	 
            	 fXPathArrayConstructor.setArrayConstructorXPathParts(seqOrArrayXPathItems);
              }
              else {
                 insertOp(opPos, 2, OpCodes.OP_SEQUENCE_CONSTRUCTOR_EXPR);
                 fXPathSequenceConstructor = new XPathSequenceConstructor();                 
                 fXPathSequenceConstructor.setSequenceConstructorXPathParts(seqOrArrayXPathItems);
              }
              
              m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH,
                                             m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);
             
          }
          else {
              // Re-start XPath parse, reusing the same token queue             
              m_queueMark = 0;
              nextToken();
              ExprSingle();
          }
      }      
      else if (fIsXPathExprBeginParse && tokenIs("map")) {
    	  // An XPath parse of 'map' expression
    	  
    	  int opPos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);
    	  
    	  if (fIsSequenceTypeXPathExpr) {
             fXPathSequenceTypeExpr = SequenceTypeExpr(false);
             return;
          }
          
          nextToken();

          insertOp(opPos, 2, OpCodes.OP_MAP_CONSTRUCTOR_EXPR);
          
          fXPathMapConstructor = new XPathMapConstructor();
          Map<String, String> nativeMapVar = new HashMap<String, String>();                    
          
          consumeExpected('{');
          
          if (!(tokenIs('}') && (getTokenRelative(1) == null))) {
        	  while (m_token != null) {        	 
 	        	 String mapEntryKeyXPathExprStr = m_token;
 	        	 nextToken();
 	        	 consumeExpected(':');
 	        	 String mapEntryValueXPathExprStr = null;
 	        	 if (tokenIs("map")) {
 	        		// There's an XPath map constructor here, within this map.
 	        		// i.e, map entry's 'value' here is map.
 	        		mapEntryValueXPathExprStr = getMapConstructorStrValue();
 	        	 }
 	        	 else if (tokenIs('[')) {
 	        		// There's an XPath square array constructor here, within this map.
 	        		// i.e, map entry's 'value' here is an array (specified with XPath 
 	        		// syntax, [ ... ] ).
  	        		mapEntryValueXPathExprStr = getXPathArrayConstructorStrValue(true); 
 	        	 }
 	        	 else if (tokenIs("array") && lookahead('{', 1)) {
 	        		// There's an XPath curly array constructor here, within this map.
 	        		// i.e, map entry's 'value' here is an array (specified with XPath 
 	        		// syntax, array { ... } ).
  	        		mapEntryValueXPathExprStr = getXPathArrayConstructorStrValue(false); 
 	        	 }
 	        	 else {
 	        		// The map entry's 'value' here, is any xdm value other than 
 	        		// 'map', or array.
 	        		mapEntryValueXPathExprStr = m_token;
 	        		while (!(tokenIs(',') || tokenIs('}'))) {
 	        		   nextToken();
 	        		   if (!(tokenIs(',') || tokenIs('}'))) {
 	        		      mapEntryValueXPathExprStr += m_token;
 	        		   }
 	        		}
 	        	 }
 	        	 
 	        	 nativeMapVar.put(mapEntryKeyXPathExprStr, mapEntryValueXPathExprStr);
 	        	 
 	        	 if (tokenIs(',')) {
 	        		consumeExpected(','); 
 	        	 }
 	        	 else if (tokenIs(']')) {
 	        	    consumeExpected(']');
 	        	    if (tokenIs(',')) {
 	 	        	   consumeExpected(','); 
 	 	        	}
 	        	    else if (tokenIs('}')) {
 	        	       consumeExpected('}');
 	 	        	   break;
 	        	    }
 	        	 }
 	        	 else if (tokenIs('}')) {
 	        		consumeExpected('}');
 	        		break;
 	        	 }
 	          }    
          }
          else {
        	  consumeExpected('}'); 
          }
          
          fXPathMapConstructor.setNativeMap(nativeMapVar);
          
          m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH,
                                                 m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);  
      }
      else if (fIsSequenceTypeXPathExpr) {
         fXPathSequenceTypeExpr = SequenceTypeExpr(false); 
      }
      else {
         ExprSingle();
      }
  }

  /**
   * Check whether, there's beginning of a literal sequence constructor, 
   * or literal array constructor.
   */
  private boolean isSequenceOrArrayBegin() {
	  return tokenIs("(") || tokenIs("[") || (tokenIs("array") && lookahead('{', 1));
  }

  /**
   *
   * ExprSingle  ::=  ForExpr
   *       | LetExpr
   *       | QuantifiedExpr
   *       | IfExpr 
   *       | OrExpr
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected void ExprSingle() throws javax.xml.transform.TransformerException
  {
      fIsXPathExprBeginParse = false;
      
      if (tokenIs("for")) {
         // To check, whether XPath 'for' expression is a sub expression of another 
         // XPath expression (for e.g, a 'for' expression could be a function 
         // argument).
         String prevTokenStr = getTokenRelative(-2);
         
         fForExpr = ForExpr(prevTokenStr);
      }
      else if (tokenIs("let")) {
         // To check, whether XPath 'let' expression is a sub expression of another 
         // XPath expression (for e.g, a 'let' expression could be a function 
         // argument).
         String prevTokenStr = getTokenRelative(-2);
          
         fLetExpr = LetExpr(prevTokenStr);
      }
      else if (tokenIs("some")) {
         // To check, whether XPath quantified 'some' expression is a sub expression 
         // of another XPath expression (for e.g, the 'some' expression could be a 
         // function argument, or may be written within an XPath predicate).
         String prevTokenStr = getTokenRelative(-2);
         
         fQuantifiedExpr = QuantifiedExpr(prevTokenStr, QuantifiedExpr.SOME);
      }
      else if (tokenIs("every")) {
         // To check, whether XPath quantified 'every' expression is a sub expression 
         // of another XPath expression (for e.g, an 'every' expression could be a 
         // function argument, or may be written within an XPath predicate).
         String prevTokenStr = getTokenRelative(-2);
         
         fQuantifiedExpr = QuantifiedExpr(prevTokenStr, QuantifiedExpr.EVERY);
      }
      else if (tokenIs("if")) {         
         fIfExpr = IfExpr();
      }
      else {
         OrExpr();
      }
  }
  
  /**
   *  Get XPath map constructor's string value.
   */
  private String getMapConstructorStrValue() throws TransformerException {
	 StringBuffer mapStrBuf = new StringBuffer();
	 
	 mapStrBuf.append(m_token);
	 
	 consumeExpected("map");
	 while (m_token != null) {
		if (tokenIs('}')) {
		   mapStrBuf.append(m_token);
		   consumeExpected('}');
		   if (getCharCount(mapStrBuf.toString(), '{') == 
				                 getCharCount(mapStrBuf.toString(), '}')) {
			   break; 
		   }
		   else {
			   mapStrBuf.append(m_token);
			   nextToken(); 
		   }
		}
		else {
		   mapStrBuf.append(m_token);
		   nextToken();
		}			 
     }
	 
	 return mapStrBuf.toString(); 
  }
  
  /**
   *  Get XPath array constructor's string value.
   */
  private String getXPathArrayConstructorStrValue(boolean isSquareArrayCons) throws TransformerException {
	 StringBuffer arrayStrBuf = new StringBuffer();
	 
	 arrayStrBuf.append(m_token);
	 
	 if (isSquareArrayCons) {
	    consumeExpected('[');
	 }
	 else {		 
		consumeExpected("array");
		arrayStrBuf.append(m_token);
		consumeExpected('{');
	 }
	 
	 while (m_token != null) {
		if (tokenIs(']') || tokenIs('}')) {
		   arrayStrBuf.append(m_token);
		   break;
		}
		else {
		   arrayStrBuf.append(m_token);
		   nextToken();
		}			 
     }
	 
	 return arrayStrBuf.toString(); 
  }
  
  /**
   * Get the count of specified character within a string.
   */
  private int getCharCount(String str, char c) {
	 int count = 0;
	 
	 for (int idx = 0; idx < str.length(); idx++) {
		if (str.charAt(idx) == c) {
		   count++;	
		}
	 }
	 
	 return count;
  }
  
  protected ForExpr ForExpr(String prevTokenStrBeforeFor) throws javax.xml.transform.TransformerException
  {
      int opPos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);
      
      nextToken();
      
      insertOp(opPos, 2, OpCodes.OP_FOR_EXPR);
      
      ForExpr forExpr = new ForExpr();
      
      List<ForQuantifiedExprVarBinding> forExprVarBindingList = new  
                                                           ArrayList<ForQuantifiedExprVarBinding>();
      
      while (!tokenIs("return") && m_token != null)
      {
          String bindingVarName = null;                    
          
          if ((forExprVarBindingList.size() > 0) && tokenIs(',') && 
                                                          lookahead('$', 1)) {
             nextToken();
             nextToken();              
             bindingVarName = m_token;              
             nextToken();              
             consumeExpected("in");
          }
          else if (tokenIs('$')) {
             nextToken();
             bindingVarName = m_token;              
             nextToken();              
             consumeExpected("in");
          }
          
          List<String> bindingXPathExprStrPartsList = new ArrayList<String>();
          
          while (!((tokenIs('$') && lookahead("in", 2)) || tokenIs("return")) && 
                                                                         m_token != null) {
             bindingXPathExprStrPartsList.add(m_token);
             nextToken();
          }
          
          if (",".equals(bindingXPathExprStrPartsList.get(bindingXPathExprStrPartsList.
                                                                                   size() - 1))) {
             bindingXPathExprStrPartsList = bindingXPathExprStrPartsList.subList(0, 
                                                                   bindingXPathExprStrPartsList.size() - 1); 
          }
          
          String varBindingXpathStr = getXPathStrFromComponentParts(
                                                                bindingXPathExprStrPartsList);
          
          ForQuantifiedExprVarBinding forExprVarBinding = new ForQuantifiedExprVarBinding();
          forExprVarBinding.setVarName(bindingVarName);
          forExprVarBinding.setXPathExprStr(varBindingXpathStr);
          
          forExprVarBindingList.add(forExprVarBinding);
          
          if (tokenIs("return")) {
             break; 
          }
      }      
      
      consumeExpected("return");
      
      List<String> xPathReturnExprStrPartsList = new ArrayList<String>();
      
      while (m_token != null) {
         if (tokenIs(')')) {            
            if ((getTokenRelative(0) == null) && "(".equals(prevTokenStrBeforeFor)) {
               break;    
            }
            else {
               xPathReturnExprStrPartsList.add(m_token);
               nextToken();
            }
         }
         else {
            xPathReturnExprStrPartsList.add(m_token);
            nextToken();
         }
      }
      
      String xPathReturnExprStr = getXPathStrFromComponentParts(xPathReturnExprStrPartsList);
      
      forExpr.setForExprVarBindingList(forExprVarBindingList);
      forExpr.setReturnExprXPathStr(xPathReturnExprStr);
      
      m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH,
                                            m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);
      
      return forExpr;
  }
  
  protected LetExpr LetExpr(String prevTokenStrBeforeLet) throws javax.xml.transform.TransformerException
  {
      int opPos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);
      
      nextToken();
      
      insertOp(opPos, 2, OpCodes.OP_LET_EXPR);
      
      LetExpr letExpr = new LetExpr();
      
      List<LetExprVarBinding> letExprVarBindingList = new ArrayList<LetExprVarBinding>();
      
      while (!tokenIs("return") && m_token != null)
      {
          String bindingVarName = null;
          
          if ((letExprVarBindingList.size() > 0) && tokenIs(',') && 
                                                         lookahead('$', 1)) {
              nextToken();
              nextToken();              
              bindingVarName = m_token;              
              nextToken();              
              consumeExpected(':');
              consumeExpected('=');
          }
          else if (tokenIs('$')) {
              nextToken();
              bindingVarName = m_token;              
              nextToken();              
              consumeExpected(':');
              consumeExpected('=');
          }
          
          List<String> bindingXPathExprStrPartsList = new ArrayList<String>();
          
          while (!((tokenIs('$') && lookahead(':', 2) && lookahead('=', 3)) || 
                                                               tokenIs("return")) && m_token != null) {
              bindingXPathExprStrPartsList.add(m_token);
              nextToken();
          }
           
          if (",".equals(bindingXPathExprStrPartsList.get(bindingXPathExprStrPartsList.
                                                                                    size() - 1))) {
              bindingXPathExprStrPartsList = bindingXPathExprStrPartsList.subList(0, 
                                                                    bindingXPathExprStrPartsList.size() - 1); 
          }
          
          String varBindingXpathStr = getXPathStrFromComponentParts(bindingXPathExprStrPartsList);

          LetExprVarBinding letExprVarBinding = new LetExprVarBinding();
          letExprVarBinding.setVarName(bindingVarName);
          letExprVarBinding.setXpathExprStr(varBindingXpathStr);

          letExprVarBindingList.add(letExprVarBinding);

          if (tokenIs("return")) {
             break; 
          }          
      }
      
      consumeExpected("return");
      
      List<String> xPathReturnExprStrPartsList = new ArrayList<String>();
      
      while (m_token != null) {
         if (tokenIs(')')) {            
            if ((getTokenRelative(0) == null) && "(".equals(prevTokenStrBeforeLet)) {
               break;    
            }
            else {
               xPathReturnExprStrPartsList.add(m_token);
               nextToken();
            }
         }
         else {
            xPathReturnExprStrPartsList.add(m_token);
            nextToken();
         }
      }
      
      String xPathReturnExprStr = getXPathStrFromComponentParts(xPathReturnExprStrPartsList);
      
      letExpr.setLetExprVarBindingList(letExprVarBindingList);
      letExpr.setReturnExprXPathStr(xPathReturnExprStr);
      
      m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH,
                                            m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);
      
      return letExpr;
  }
  
  protected QuantifiedExpr QuantifiedExpr(String prevTokenStrBeforeQuantifier, int quantifierExprType) 
                                                              throws javax.xml.transform.TransformerException
  {
      int opPos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);
      
      nextToken();
      
      insertOp(opPos, 2, OpCodes.OP_QUANTIFIED_EXPR);
      
      QuantifiedExpr quantifiedExpr = new QuantifiedExpr();
      quantifiedExpr.setCurrentXPathQuantifier(quantifierExprType);
      
      List<ForQuantifiedExprVarBinding> quantifiedExprVarBindingList = new 
                                                           ArrayList<ForQuantifiedExprVarBinding>();
      
      while (!tokenIs("satisfies") && m_token != null)
      {
          String bindingVarName = null;
          
          if ((quantifiedExprVarBindingList.size() > 0) && tokenIs(',') && lookahead('$', 1)) {
             nextToken();
             nextToken();              
             bindingVarName = m_token;              
             nextToken();              
             consumeExpected("in");
          }
          else if (tokenIs('$')) {
             nextToken();
             bindingVarName = m_token;              
             nextToken();              
             consumeExpected("in");
          }
          
          List<String> bindingXPathExprStrPartsList = new ArrayList<String>();
          
          while (!((tokenIs('$') && lookahead("in", 2)) || tokenIs("satisfies")) && 
                                                                          m_token != null) {
             bindingXPathExprStrPartsList.add(m_token);
             nextToken();
          }
          
          if (",".equals(bindingXPathExprStrPartsList.get(bindingXPathExprStrPartsList.
                                                                                   size() - 1))) {
             bindingXPathExprStrPartsList = bindingXPathExprStrPartsList.subList(0, 
                                                                   bindingXPathExprStrPartsList.size() - 1); 
          }
          
          String varBindingXpathStr = getXPathStrFromComponentParts(
                                                                bindingXPathExprStrPartsList);
          
          ForQuantifiedExprVarBinding quantifiedExprVarBinding = new ForQuantifiedExprVarBinding();
          quantifiedExprVarBinding.setVarName(bindingVarName);
          quantifiedExprVarBinding.setXPathExprStr(varBindingXpathStr);
          
          quantifiedExprVarBindingList.add(quantifiedExprVarBinding);
          
          if (tokenIs("satisfies")) {
             break; 
          }
      }      
      
      consumeExpected("satisfies");
      
      List<String> xPathTestExprStrPartsList = new ArrayList<String>();
      
      while (m_token != null) {
         if (tokenIs(')')) {            
            if ((getTokenRelative(0) == null) && "(".equals(prevTokenStrBeforeQuantifier)) {
               break;    
            }
            else {
               xPathTestExprStrPartsList.add(m_token);
               nextToken();
            }
         }
         else if (fIsXPathPredicateParsingActive && tokenIs(']')) {
            break;    
         }
         else {
            xPathTestExprStrPartsList.add(m_token);
            nextToken();
         }
      }
      
      String xPathTestExprStr = getXPathStrFromComponentParts(xPathTestExprStrPartsList);
      
      quantifiedExpr.setQuantifiedExprVarBindingList(quantifiedExprVarBindingList);
      quantifiedExpr.setQuantifierTestXPathStr(xPathTestExprStr);
      
      m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH,
                                            m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);
      
      return quantifiedExpr;
  }
  
  protected IfExpr IfExpr() throws javax.xml.transform.TransformerException
  {
      int opPos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);
      
      nextToken();
      
      insertOp(opPos, 2, OpCodes.OP_IF_EXPR);
      
      IfExpr ifExpr = new IfExpr();            
      
      consumeExpected('(');
      
      List<String> conditionalExprXPathStrPartsList = new ArrayList<String>();
      
      while (!tokenIs("then") && m_token != null)
      {
          conditionalExprXPathStrPartsList.add(m_token);
          nextToken();
      }
      
      consumeExpected("then");
      
      conditionalExprXPathStrPartsList = conditionalExprXPathStrPartsList.subList(0, 
                                                             conditionalExprXPathStrPartsList.size() - 1);
      
      String conditionalXPathExprStr = getXPathStrFromComponentParts(conditionalExprXPathStrPartsList);
      
      List<String> thenExprXPathStrPartsList = new ArrayList<String>();
      
      while (!tokenIs("else") && m_token != null)
      {
          thenExprXPathStrPartsList.add(m_token);
          nextToken();
      }
      
      consumeExpected("else");
      
      String thenXPathExprStr = getXPathStrFromComponentParts(thenExprXPathStrPartsList);            
      
      List<String> elseExprXPathStrPartsList = new ArrayList<String>();
      
      while (m_token != null)
      {
          if (fIsXPathPredicateParsingActive && lookahead(']', 1)) {
             elseExprXPathStrPartsList.add(m_token);
             elseExprXPathStrPartsList.subList(0, elseExprXPathStrPartsList.size() - 1);
             nextToken();
             break;
          }
          else {
             elseExprXPathStrPartsList.add(m_token);
             nextToken();
          }          
      }
      
      String elseXPathExprStr = getXPathStrFromComponentParts(elseExprXPathStrPartsList);
      
      ifExpr.setConditionalExprXPathStr(conditionalXPathExprStr);
      ifExpr.setThenExprXPathStr(thenXPathExprStr);
      ifExpr.setElseExprXPathStr(elseXPathExprStr);
      
      m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH,
                                    m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);
      
      return ifExpr;      
  }

  /**
   *
   *
   * OrExpr  ::=  AndExpr
   * | OrExpr 'or' AndExpr
   *
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected void OrExpr() throws javax.xml.transform.TransformerException
  {

    int opPos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);

    AndExpr();

    if ((null != m_token) && tokenIs("or"))
    {
      nextToken();
      insertOp(opPos, 2, OpCodes.OP_OR);
      OrExpr();

      m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH,
        m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);
    }
  }

  /**
   *
   *
   * AndExpr  ::=  EqualityExpr
   * | AndExpr 'and' EqualityExpr
   *
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected void AndExpr() throws javax.xml.transform.TransformerException
  {

    int opPos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);

    EqualityExpr(-1);

    if ((null != m_token) && tokenIs("and"))
    {
      nextToken();
      insertOp(opPos, 2, OpCodes.OP_AND);
      AndExpr();

      m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH,
        m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);
    }
  }

  /**
   *
   * @returns an Object which is either a String, a Number, a Boolean, or a vector
   * of nodes.
   *
   * EqualityExpr  ::=  RelationalExpr
   * | EqualityExpr '=' RelationalExpr
   * | EqualityExpr 'eq' RelationalExpr
   * | EqualityExpr 'ne' RelationalExpr
   *
   *
   * @param addPos Position where expression is to be added, or -1 for append.
   *
   * @return the position at the end of the equality expression.
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected int EqualityExpr(int addPos) throws javax.xml.transform.TransformerException
  {
      
    int opPos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);

    if (-1 == addPos)
      addPos = opPos;

    RelationalExpr(-1);

    if (null != m_token)
    {
      if (tokenIs('!'))
      {
        if (lookahead('=', 1)) 
        {
           nextToken();
           nextToken();
           insertOp(addPos, 2, OpCodes.OP_NOTEQUALS);

           int opPlusLeftHandLen = m_ops.getOp(OpMap.MAPINDEX_LENGTH) - addPos;

           addPos = EqualityExpr(addPos);
           m_ops.setOp(addPos + OpMap.MAPINDEX_LENGTH,
             m_ops.getOp(addPos + opPlusLeftHandLen + 1) + opPlusLeftHandLen);
           addPos += 2;
        }
        else 
        {
           // Support for XPath 3.1 simple map operator '!'.
        	
           // An XPath expression SEQ!Expr evaluates an expression Expr once for every 
           // item in the sequence obtained by evaluating SEQ. An XPath language operator "!" 
           // may be applied to any sequence, irrespective of types of its items. An evaluation 
           // for XPath operator '!', may also result in mixed sequence of XPath nodes, atomic 
           // values, and function items.
        	
           // Ref : https://www.w3.org/TR/xpath-31/#id-map-operator
            
           nextToken();
           insertOp(addPos, 2, OpCodes.OP_SIMPLE_MAP_OPERATOR);

           int opPlusLeftHandLen = m_ops.getOp(OpMap.MAPINDEX_LENGTH) - addPos;

           addPos = EqualityExpr(addPos);
           m_ops.setOp(addPos + OpMap.MAPINDEX_LENGTH,
              m_ops.getOp(addPos + opPlusLeftHandLen + 1) + opPlusLeftHandLen);
           addPos += 2;  
        }
      }
      else if (tokenIs('='))
      {
        nextToken();
        insertOp(addPos, 2, OpCodes.OP_EQUALS);

        int opPlusLeftHandLen = m_ops.getOp(OpMap.MAPINDEX_LENGTH) - addPos;

        addPos = EqualityExpr(addPos);
        m_ops.setOp(addPos + OpMap.MAPINDEX_LENGTH,
          m_ops.getOp(addPos + opPlusLeftHandLen + 1) + opPlusLeftHandLen);
        addPos += 2;
      }
      else if (tokenIs("eq"))
      {
        // Support for XPath 3.1 value comparison operator "eq"
          
        nextToken();
        insertOp(addPos, 2, OpCodes.OP_VC_EQUALS);

        int op1 = m_ops.getOp(OpMap.MAPINDEX_LENGTH) - addPos;

        addPos = EqualityExpr(addPos);
        m_ops.setOp(addPos + OpMap.MAPINDEX_LENGTH,
          m_ops.getOp(addPos + op1 + 1) + op1);
        addPos += 2;
      }
      else if (tokenIs("ne"))
      {
        // Support for XPath 3.1 value comparison operator "ne"
          
        nextToken();
        insertOp(addPos, 2, OpCodes.OP_VC_NOT_EQUALS);

        int op1 = m_ops.getOp(OpMap.MAPINDEX_LENGTH) - addPos;

        addPos = EqualityExpr(addPos);
        m_ops.setOp(addPos + OpMap.MAPINDEX_LENGTH,
          m_ops.getOp(addPos + op1 + 1) + op1);
        addPos += 2;
      }
    }

    return addPos;
  }

  /**
   * .
   * @returns an Object which is either a String, a Number, a Boolean, or a vector
   * of nodes.
   *
   * RelationalExpr  ::=  AdditiveExpr
   * | RelationalExpr '<' AdditiveExpr
   * | RelationalExpr '>' AdditiveExpr
   * | RelationalExpr '<=' AdditiveExpr
   * | RelationalExpr '>=' AdditiveExpr
   * | RelationalExpr 'to' AdditiveExpr
   * | RelationalExpr '||' AdditiveExpr
   * | RelationalExpr 'lt' AdditiveExpr
   * | RelationalExpr 'le' AdditiveExpr
   * | RelationalExpr 'gt' AdditiveExpr
   * | RelationalExpr 'ge' AdditiveExpr
   * | RelationalExpr 'is' AdditiveExpr
   * | RelationalExpr '<<' AdditiveExpr
   * | RelationalExpr '>>' AdditiveExpr
   * | RelationalExpr 'instance of' SequenceType
   *
   * @param addPos Position where expression is to be added, or -1 for append.
   *
   * @return the position at the end of the relational expression.
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected int RelationalExpr(int addPos) throws javax.xml.transform.TransformerException
  {

    int opPos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);

    if (-1 == addPos)
      addPos = opPos;

    AdditiveExpr(-1);

    if (null != m_token)
    {
      if (tokenIs('<'))
      {
        nextToken();

        if (tokenIs('='))
        {
          nextToken();
          insertOp(addPos, 2, OpCodes.OP_LTE);
        }
        else if (tokenIs('<'))
        {
          // Support for XPath 3.1 node comparison operator "<<"
          
          nextToken();
          insertOp(addPos, 2, OpCodes.OP_NC_PRECEDE); 
        }
        else
        {
          insertOp(addPos, 2, OpCodes.OP_LT);
        }

        int opPlusLeftHandLen = m_ops.getOp(OpMap.MAPINDEX_LENGTH) - addPos;

        addPos = RelationalExpr(addPos);
        m_ops.setOp(addPos + OpMap.MAPINDEX_LENGTH, 
          m_ops.getOp(addPos + opPlusLeftHandLen + 1) + opPlusLeftHandLen);
        addPos += 2;
      }
      else if (tokenIs('>'))
      {
        nextToken();

        if (tokenIs('='))
        {
          nextToken();
          insertOp(addPos, 2, OpCodes.OP_GTE);
        }
        else if (tokenIs('>'))
        {
          // Support for XPath 3.1 node comparison operator ">>"
          
          nextToken();
          insertOp(addPos, 2, OpCodes.OP_NC_FOLLOWS);
        }
        else
        {
          insertOp(addPos, 2, OpCodes.OP_GT);
        }

        int opPlusLeftHandLen = m_ops.getOp(OpMap.MAPINDEX_LENGTH) - addPos;

        addPos = RelationalExpr(addPos);
        m_ops.setOp(addPos + OpMap.MAPINDEX_LENGTH,
          m_ops.getOp(addPos + opPlusLeftHandLen + 1) + opPlusLeftHandLen);
        addPos += 2;
      }
      else if (tokenIs("to"))
      {
          // Support for XPath 3.1 range expression (i.e, an XPath operator "to").
    	  
    	  // An XPath language range expressions are used to construct a 
    	  // sequence of consecutive integers. For e.g, an XPath language range expression
    	  // "1 to 10" shall return following XPath result sequence (1, 2, 3, 4, 5, 6, 7, 8, 9, 10). 
    	  
    	  // Ref: https://www.w3.org/TR/xpath-31/#construct_seq    	  
          
          nextToken();
          
          insertOp(addPos, 2, OpCodes.OP_TO);
          
          int op1 = m_ops.getOp(OpMap.MAPINDEX_LENGTH) - addPos;
          
          addPos = AdditiveExpr(addPos);
          m_ops.setOp(addPos + OpMap.MAPINDEX_LENGTH, 
            m_ops.getOp(addPos + op1 + 1) + op1);
          addPos += 2; 
      }
      else if (tokenIs("||"))
      {
          // Support for XPath 3.1 string concatenation operator "||".
    	  
    	  // An XPath language string concatenation operator "||" may be used as an
    	  // alternative means, for XPath string concatenation function 'concat'.
          
          nextToken();
          
          insertOp(addPos, 2, OpCodes.OP_STR_CONCAT);
          
          int op1 = m_ops.getOp(OpMap.MAPINDEX_LENGTH) - addPos;

          addPos = RelationalExpr(addPos);
          m_ops.setOp(addPos + OpMap.MAPINDEX_LENGTH, 
            m_ops.getOp(addPos + op1 + 1) + op1);
          addPos += 2; 
      }
      else if (tokenIs("=>"))
      {
          // Support for XPath 3.1 arrow operator "=>"
    	  
          consumeExpected("=>");

          insertOp(addPos, 2, OpCodes.OP_ARROW);
          
          FunctionCall();
          
          if (m_token != null) {
        	 fArrowOpRemainingXPathExprStr = "";  
          }
          
          while (m_token != null) {
        	 fArrowOpRemainingXPathExprStr += m_token;
        	 nextToken();
          }
      }
      else if (tokenIs("lt"))
      {
          // Support for XPath 3.1 value comparison operator "lt"
          
          nextToken();
        
          insertOp(addPos, 2, OpCodes.OP_VC_LT);

          int op1 = m_ops.getOp(OpMap.MAPINDEX_LENGTH) - addPos;

          addPos = RelationalExpr(addPos);
          m_ops.setOp(addPos + OpMap.MAPINDEX_LENGTH,
             m_ops.getOp(addPos + op1 + 1) + op1);
          addPos += 2;
      }
      else if (tokenIs("gt"))
      {
          // Support for XPath 3.1 value comparison operator "gt"
          
          nextToken();
        
          insertOp(addPos, 2, OpCodes.OP_VC_GT);

          int op1 = m_ops.getOp(OpMap.MAPINDEX_LENGTH) - addPos;

          addPos = RelationalExpr(addPos);
          m_ops.setOp(addPos + OpMap.MAPINDEX_LENGTH,
             m_ops.getOp(addPos + op1 + 1) + op1);
          addPos += 2;
      }
      else if (tokenIs("le"))
      {
          // Support for XPath 3.1 value comparison operator "le"
          
          nextToken();
        
          insertOp(addPos, 2, OpCodes.OP_VC_LE);

          int op1 = m_ops.getOp(OpMap.MAPINDEX_LENGTH) - addPos;

          addPos = RelationalExpr(addPos);
          m_ops.setOp(addPos + OpMap.MAPINDEX_LENGTH,
             m_ops.getOp(addPos + op1 + 1) + op1);
          addPos += 2;
      }
      else if (tokenIs("ge"))
      {
          // Support for XPath 3.1 value comparison operator "ge"
          
          nextToken();
        
          insertOp(addPos, 2, OpCodes.OP_VC_GE);

          int op1 = m_ops.getOp(OpMap.MAPINDEX_LENGTH) - addPos;

          addPos = RelationalExpr(addPos);
          m_ops.setOp(addPos + OpMap.MAPINDEX_LENGTH,
             m_ops.getOp(addPos + op1 + 1) + op1);
          addPos += 2;
      }
      else if (tokenIs("is"))
      {
          // Support for XPath 3.1 node comparison operator "is"
          
          nextToken();
        
          insertOp(addPos, 2, OpCodes.OP_IS);

          int op1 = m_ops.getOp(OpMap.MAPINDEX_LENGTH) - addPos;

          addPos = RelationalExpr(addPos);
          m_ops.setOp(addPos + OpMap.MAPINDEX_LENGTH,
             m_ops.getOp(addPos + op1 + 1) + op1);
          addPos += 2;
      }
      else if (tokenIs("instance") && lookahead("of", 1)) {
          // Support for XPath 3.1 "instance of" expression.
    	  
    	  // An XPath language, "instance of" operator requires two operands. An 
    	  // XPath evaluation for "instance of" expression returns value 'true',
    	  // if the value of its first operand is of the type of its second operand.
    	  
    	  // Ref : https://www.w3.org/TR/xpath-31/#id-instance-of
          
          consumeExpected("instance");
          consumeExpected("of");
          
          insertOp(addPos, 2, OpCodes.OP_INSTANCE_OF);
          
          int op1 = m_ops.getOp(OpMap.MAPINDEX_LENGTH) - addPos;
          
          fXPathSequenceTypeExpr = SequenceTypeExpr(false);          
          m_ops.setOp(addPos + OpMap.MAPINDEX_LENGTH, 
                                                  m_ops.getOp(addPos + op1 + 1) + op1);
          addPos += 2;
      }      
      else if (tokenIs("cast") && lookahead("as", 1)) {
          // Support for XPath 3.1 "cast as" expression.
    	  
    	  // An XPath expression "cast as" converts an input value, 
    	  // to another value of a specific datatype.
    	  
    	  // Ref : https://www.w3.org/TR/xpath-31/#id-cast
          
          consumeExpected("cast");
          consumeExpected("as");
          
          insertOp(addPos, 2, OpCodes.OP_CAST_AS);
          
          int op1 = m_ops.getOp(OpMap.MAPINDEX_LENGTH) - addPos;
          
          fXPathSequenceTypeExpr = SequenceTypeExpr(false);
          
          m_ops.setOp(addPos + OpMap.MAPINDEX_LENGTH, 
                                                  m_ops.getOp(addPos + op1 + 1) + op1);
          addPos += 2;
      }
      else if (tokenIs("castable") && lookahead("as", 1)) {
          // Support for XPath 3.1 "castable as" expression.
    	  
    	  // An XPath expression "castable as" tests (i.e, returns true, or false) 
    	  // whether a given value is castable into another datatype.
    	  
    	  // Ref : https://www.w3.org/TR/xpath-31/#id-castable
          
          consumeExpected("castable");
          consumeExpected("as");
          
          insertOp(addPos, 2, OpCodes.OP_CASTABLE_AS);
          
          int op1 = m_ops.getOp(OpMap.MAPINDEX_LENGTH) - addPos;
          
          fXPathSequenceTypeExpr = SequenceTypeExpr(false);
          
          m_ops.setOp(addPos + OpMap.MAPINDEX_LENGTH, 
                                                  m_ops.getOp(addPos + op1 + 1) + op1);
          addPos += 2;
      }
    }

    return addPos;
  }

  /**
   * This has to handle construction of the operations so that they are evaluated
   * in pre-fix order.  So, for 9+7-6, instead of |+|9|-|7|6|, this needs to be
   * evaluated as |-|+|9|7|6|.
   *
   * AdditiveExpr  ::=  MultiplicativeExpr
   * | AdditiveExpr '+' MultiplicativeExpr
   * | AdditiveExpr '-' MultiplicativeExpr
   *
   *
   * @param addPos Position where expression is to be added, or -1 for append.
   *
   * @return the position at the end of the equality expression.
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected int AdditiveExpr(int addPos) throws javax.xml.transform.TransformerException
  {

    int opPos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);

    if (-1 == addPos)
      addPos = opPos;

    MultiplicativeExpr(-1);

    if (null != m_token)
    {
      if (tokenIs('+'))
      {
        nextToken();
        insertOp(addPos, 2, OpCodes.OP_PLUS);

        int opPlusLeftHandLen = m_ops.getOp(OpMap.MAPINDEX_LENGTH) - addPos;

        addPos = AdditiveExpr(addPos);
        m_ops.setOp(addPos + OpMap.MAPINDEX_LENGTH,
          m_ops.getOp(addPos + opPlusLeftHandLen + 1) + opPlusLeftHandLen);
        addPos += 2;
      }
      else if (tokenIs('-'))
      {
        nextToken();
        insertOp(addPos, 2, OpCodes.OP_MINUS);

        int opPlusLeftHandLen = m_ops.getOp(OpMap.MAPINDEX_LENGTH) - addPos;

        addPos = AdditiveExpr(addPos);
        m_ops.setOp(addPos + OpMap.MAPINDEX_LENGTH, 
          m_ops.getOp(addPos + opPlusLeftHandLen + 1) + opPlusLeftHandLen);
        addPos += 2;
      }
    }

    return addPos;
  }

  /**
   * This has to handle construction of the operations so that they are evaluated
   * in pre-fix order.  So, for 9+7-6, instead of |+|9|-|7|6|, this needs to be
   * evaluated as |-|+|9|7|6|.
   *
   * MultiplicativeExpr  ::=  UnaryExpr
   * | MultiplicativeExpr MultiplyOperator UnaryExpr
   * | MultiplicativeExpr 'div' UnaryExpr
   * | MultiplicativeExpr 'mod' UnaryExpr
   * | MultiplicativeExpr 'quo' UnaryExpr
   *
   * @param addPos Position where expression is to be added, or -1 for append.
   *
   * @return the position at the end of the equality expression.
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected int MultiplicativeExpr(int addPos) throws javax.xml.transform.TransformerException
  {

    int opPos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);

    if (-1 == addPos)
      addPos = opPos;

    UnaryExpr();

    if (null != m_token)
    {
      if (tokenIs('*'))
      {
        nextToken();
        insertOp(addPos, 2, OpCodes.OP_MULT);

        int opPlusLeftHandLen = m_ops.getOp(OpMap.MAPINDEX_LENGTH) - addPos;

        addPos = MultiplicativeExpr(addPos);
        m_ops.setOp(addPos + OpMap.MAPINDEX_LENGTH,
          m_ops.getOp(addPos + opPlusLeftHandLen + 1) + opPlusLeftHandLen);
        addPos += 2;
      }
      else if (tokenIs("div"))
      {
        nextToken();
        insertOp(addPos, 2, OpCodes.OP_DIV);

        int opPlusLeftHandLen = m_ops.getOp(OpMap.MAPINDEX_LENGTH) - addPos;

        addPos = MultiplicativeExpr(addPos);
        m_ops.setOp(addPos + OpMap.MAPINDEX_LENGTH,
          m_ops.getOp(addPos + opPlusLeftHandLen + 1) + opPlusLeftHandLen);
        addPos += 2;
      }
      else if (tokenIs("mod"))
      {
        nextToken();
        insertOp(addPos, 2, OpCodes.OP_MOD);

        int opPlusLeftHandLen = m_ops.getOp(OpMap.MAPINDEX_LENGTH) - addPos;

        addPos = MultiplicativeExpr(addPos);
        m_ops.setOp(addPos + OpMap.MAPINDEX_LENGTH,
          m_ops.getOp(addPos + opPlusLeftHandLen + 1) + opPlusLeftHandLen);
        addPos += 2;
      }
      else if (tokenIs("quo"))
      {
        nextToken();
        insertOp(addPos, 2, OpCodes.OP_QUO);

        int opPlusLeftHandLen = m_ops.getOp(OpMap.MAPINDEX_LENGTH) - addPos;

        addPos = MultiplicativeExpr(addPos);
        m_ops.setOp(addPos + OpMap.MAPINDEX_LENGTH,
          m_ops.getOp(addPos + opPlusLeftHandLen + 1) + opPlusLeftHandLen);
        addPos += 2;
      }
    }

    return addPos;
  }

  /**
   *
   * UnaryExpr  ::=  UnionExpr
   * | '-' UnaryExpr
   *
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected void UnaryExpr() throws javax.xml.transform.TransformerException
  {

    int opPos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);
    boolean isNeg = false;

    if (m_tokenChar == '-')
    {
      nextToken();
      appendOp(2, OpCodes.OP_NEG);

      isNeg = true;
    }

    UnionExpr();

    if (isNeg)
      m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH,
        m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);
  }

  /**
   *
   * StringExpr  ::=  Expr
   *
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected void StringExpr() throws javax.xml.transform.TransformerException
  {

    int opPos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);

    appendOp(2, OpCodes.OP_STRING);
    Expr();

    m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH,
      m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);
  }

  /**
   *
   *
   * StringExpr  ::=  Expr
   *
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected void BooleanExpr() throws javax.xml.transform.TransformerException
  {

    int opPos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);

    appendOp(2, OpCodes.OP_BOOL);
    Expr();

    int opLen = m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos;

    if (opLen == 2)
    {
      error(XPATHErrorResources.ER_BOOLEAN_ARG_NO_LONGER_OPTIONAL, null);  //"boolean(...) argument is no longer optional with 19990709 XPath draft.");
    }

    m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH, opLen);
  }

  /**
   *
   *
   * NumberExpr  ::=  Expr
   *
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected void NumberExpr() throws javax.xml.transform.TransformerException
  {

    int opPos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);

    appendOp(2, OpCodes.OP_NUMBER);
    Expr();

    m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH,
      m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);
  }

  /**
   * The context of the right hand side expressions is the context of the
   * left hand side expression. The results of the right hand side expressions
   * are node sets. The result of the left hand side UnionExpr is the union
   * of the results of the right hand side expressions.
   *
   *
   * UnionExpr    ::=    PathExpr
   * | UnionExpr '|' PathExpr
   *
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected void UnionExpr() throws javax.xml.transform.TransformerException
  {

    int opPos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);
    boolean continueOrLoop = true;
    boolean foundUnion = false;

    do
    {
      PathExpr();

      if (tokenIs('|') && !tokenIs("||"))
      {
        if (false == foundUnion)
        {
          foundUnion = true;

          insertOp(opPos, 2, OpCodes.OP_UNION);
        }

        nextToken();
      }
      else
      {
        break;
      }

      // this.m_testForDocOrder = true;
    }
    while (continueOrLoop);

    m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH,
          m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);
  }

  /**
   * PathExpr  ::=  LocationPath
   * | FilterExpr
   * | FilterExpr '/' RelativeLocationPath
   * | FilterExpr '//' RelativeLocationPath
   *
   * @throws XSLProcessorException thrown if the active ProblemListener and XPathContext decide
   * the error condition is severe enough to halt processing.
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected void PathExpr() throws javax.xml.transform.TransformerException
  {

    int opPos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);

    int filterExprMatch = FilterExpr();

    if (filterExprMatch != FILTER_MATCH_FAILED)
    {
      // If FilterExpr had Predicates, a OP_LOCATIONPATH opcode would already
      // have been inserted.
      boolean locationPathStarted = (filterExprMatch==FILTER_MATCH_PREDICATES);

      if (tokenIs('/'))
      {
        nextToken();

        if (!locationPathStarted)
        {
          // int locationPathOpPos = opPos;
          insertOp(opPos, 2, OpCodes.OP_LOCATIONPATH);

          locationPathStarted = true;
        }

        if (!RelativeLocationPath())
        {
          // "Relative location path expected following '/' or '//'"
          error(XPATHErrorResources.ER_EXPECTED_REL_LOC_PATH, null);
        }

      }

      if (locationPathStarted)
      {
        m_ops.setOp(m_ops.getOp(OpMap.MAPINDEX_LENGTH), OpCodes.ENDOP);
        m_ops.setOp(OpMap.MAPINDEX_LENGTH, m_ops.getOp(OpMap.MAPINDEX_LENGTH) + 1);
        m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH,
          m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);
      }
    }
    else
    {
      LocationPath();
    }
  }

  /**
   *
   *
   * FilterExpr  ::=  PrimaryExpr
   * | FilterExpr Predicate
   *
   * @throws XSLProcessorException thrown if the active ProblemListener and XPathContext decide
   * the error condition is severe enough to halt processing.
   *
   * @return  FILTER_MATCH_PREDICATES, if this method successfully matched a
   *          FilterExpr with one or more Predicates;
   *          FILTER_MATCH_PRIMARY, if this method successfully matched a
   *          FilterExpr that was just a PrimaryExpr; or
   *          FILTER_MATCH_FAILED, if this method did not match a FilterExpr
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected int FilterExpr() throws javax.xml.transform.TransformerException
  {

    int opPos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);

    int filterMatch;

    if (PrimaryExpr())
    {
      if (tokenIs('['))
      {

        // int locationPathOpPos = opPos;
        insertOp(opPos, 2, OpCodes.OP_LOCATIONPATH);

        while (tokenIs('['))
        {
          Predicate();
        }

        filterMatch = FILTER_MATCH_PREDICATES;
      }
      else
      {
        filterMatch = FILTER_MATCH_PRIMARY;
      }
    }
    else
    {
      filterMatch = FILTER_MATCH_FAILED;
    }

    return filterMatch;

    /*
     * if(tokenIs('['))
     * {
     *   Predicate();
     *   m_ops.m_opMap[opPos + OpMap.MAPINDEX_LENGTH] = m_ops.m_opMap[OpMap.MAPINDEX_LENGTH] - opPos;
     * }
     */
  }

  /**
   *
   * PrimaryExpr  ::=  VariableReference
   * | '(' Expr ')'
   * | Literal
   * | VarRef ArgumentList
   * | Number
   * | FunctionCall
   * | FunctionItemExpr
   *
   * @return true if this method successfully matched a PrimaryExpr
   *
   * @throws javax.xml.transform.TransformerException
   *
   */
  protected boolean PrimaryExpr() throws javax.xml.transform.TransformerException
  {

    boolean matchFound = false;
    
    int opPos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);

    if ((m_tokenChar == '\'') || (m_tokenChar == '"'))
    {
      appendOp(2, OpCodes.OP_LITERAL);
      Literal();

      m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH, 
        m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);

      matchFound = true;
    }
    else if ((m_tokenChar == '$') && (lookahead('(', 2))) {
       // Support for XPath 3.1 'dynamic function call' expression.
    	
       // For example, if an XPath inline function expression is bound
       // to an XSLT variable as follows : <xsl:variable name="sqr" select="function($val) { $val * $val }"/>,
       // then an XPath expression $sqr(5) shall return a value 25. Within this example, 
       // an XPath expression $sqr(5) is referred to as an XPath dynamic function call.
    	
       // Ref : https://www.w3.org/TR/xpath-31/#id-dynamic-function-invocation
                     
       appendOp(2, OpCodes.OP_DYNAMIC_FUNCTION_CALL);
       
       nextToken();  // consume '$'
       
       String funcRefVarName = m_token;       
       nextToken();
       consumeExpected('(');
       
       // While parsing the arguments detail of a dynamic function call,
       // we use the code as specified below, and store the resulting
       // information within an object of class 'DynamicFunctionCall'.
       
       List<String> argList = new ArrayList<>();
       
       List<String> argDetailsStrPartsList = new ArrayList<String>();
       
       // We create here a temporary function call argument delimiter 
       // string, for this processing.
       long currentTimeMills = System.currentTimeMillis();
       String delimSuffix = (Long.valueOf(currentTimeMills)).toString();
       String delim = "t0_" + delimSuffix;
       
       while (m_token != null && isXPathDynamicFuncCallParseAhead(
                                                      argDetailsStrPartsList, delim)) {
          // NO OP
       }
       
       fDynamicFunctionCallArgumentMarker = false;
       
       int startIdx = 0;
       int idxDelim;       
       while (argDetailsStrPartsList.contains(delim) && 
                         (idxDelim = argDetailsStrPartsList.indexOf(delim)) != -1) {
          List<String> lst1 = argDetailsStrPartsList.subList(startIdx, idxDelim);
          
          String xpathStr = getXPathStrFromComponentParts(lst1);
          
          argList.add(xpathStr);
          
          List<String> lst2 = argDetailsStrPartsList.subList(idxDelim + 1, 
                                                                           argDetailsStrPartsList.size());
          
          argDetailsStrPartsList = lst2; 
       }
       
       if (argDetailsStrPartsList.size() > 0) {
           String xpathStr = getXPathStrFromComponentParts(argDetailsStrPartsList);
           argList.add(xpathStr);
       }

       consumeExpected(')');
       
       fDynamicFunctionCall = new DynamicFunctionCall();
       fDynamicFunctionCall.setFuncRefVarName(funcRefVarName);
       fDynamicFunctionCall.setArgList(argList);
       
       m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH,
                               m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);
       
       matchFound = true;
    }
    else if (m_tokenChar == '$')
    {
      nextToken();  // consume '$'
      appendOp(2, OpCodes.OP_VARIABLE);
      QName();
      
      m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH,
        m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);

      matchFound = true;
    }
    else if (m_tokenChar == '(')
    {
      nextToken();
      appendOp(2, OpCodes.OP_GROUP);     
      Expr();
      consumeExpected(')');      
      
      m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH,
        m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);

      matchFound = true;
    }
    else if ((null != m_token) && ((('.' == m_tokenChar) && (m_token.length() > 1) && Character.isDigit(
            m_token.charAt(1))) || Character.isDigit(m_tokenChar)))
    {
      appendOp(2, OpCodes.OP_NUMBERLIT);
      Number();

      m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH,
        m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);

      matchFound = true;
    }
    else if (tokenIs("function") && lookahead('(', 1)) {
      // Support for XPath 3.1 "inline function" expression
    	
      // Ref : https://www.w3.org/TR/xpath-31/#id-inline-func
      
      appendOp(2, OpCodes.OP_INLINE_FUNCTION);
      
      fInlineFunction = InlineFunctionExpr();
      
      m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH,
              m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);
      
      matchFound = true;
         
    }
    else if (lookahead('(', 1) || (lookahead(':', 1) && lookahead('(', 3)))
    {
      matchFound = FunctionCall();
    }
    else
    {
      matchFound = false;
    }

    return matchFound;
  }
  
  protected InlineFunction InlineFunctionExpr() throws javax.xml.transform.TransformerException {
      InlineFunction inlineFunction = new InlineFunction();
      
      List<InlineFunctionParameter> funcParamList = new ArrayList<InlineFunctionParameter>();      
      String funcBodyXPathExprStr = null;
      
      nextToken();
      
      consumeExpected('(');

      if (!tokenIs(')')) {
          while (!tokenIs(')') && m_token != null)
          {
              if (tokenIs(','))
              {
                  error(XPATHErrorResources.ER_FOUND_COMMA_BUT_NO_PRECEDING_PARAM, null);
              }
    
              if (m_tokenChar == '$')
              {
                  nextToken();
                  
                  InlineFunctionParameter inlineFunctionParameter = new InlineFunctionParameter();                   
                  if (lookahead("as", 1)) {
                     inlineFunctionParameter.setParamName(m_token);                     
                     nextToken();
                     nextToken();                     
                     SequenceTypeData paramType = new SequenceTypeData();
                     XPathSequenceTypeExpr seqTypeExpr = SequenceTypeExpr(true);
                     paramType.setSequenceType(seqTypeExpr.getSequenceType());
                     paramType.setItemTypeOccurrenceIndicator(seqTypeExpr.getItemTypeOccurrenceIndicator());
                     paramType.setSequenceTypeKindTest(seqTypeExpr.getSequenceTypeKindTest());
                     paramType.setSequenceTypeFunctionTest(seqTypeExpr.getSequenceTypeFunctionTest());
                     inlineFunctionParameter.setParamType(paramType);                     
                  }
                  else {
                     inlineFunctionParameter.setParamName(m_token);
                     nextToken();
                  }
                  
                  funcParamList.add(inlineFunctionParameter);
              }
    
              if (!tokenIs(')'))
              {
                  consumeExpected(',');
    
                  if (tokenIs(')'))
                  {
                      error(XPATHErrorResources.ER_FOUND_COMMA_BUT_NO_FOLLOWING_PARAM, null);                    
                  }
              }
          }    
      }
      
      inlineFunction.setFuncParamList(funcParamList);
      
      consumeExpected(')');
      
      if (tokenIs("as")) {
         nextToken();
         XPathSequenceTypeExpr seqTypeExpr = SequenceTypeExpr(true);
         SequenceTypeData returnType = new SequenceTypeData();
         returnType.setSequenceType(seqTypeExpr.getSequenceType());
         returnType.setItemTypeOccurrenceIndicator(seqTypeExpr.getItemTypeOccurrenceIndicator());
         returnType.setSequenceTypeKindTest(seqTypeExpr.getSequenceTypeKindTest());
         
         inlineFunction.setReturnType(returnType);
      }
      
      consumeExpected('{');
      
      // While parsing the XPath expression string that forms body of inline
      // function expression, we only get this XPath expression's string value,
      // as determined below and store that within an object of class 
      // 'InlineFunction'.
      
      List<String> funcBodyXPathExprStrPartsList = new ArrayList<String>();
      
      if (tokenIs('}')) {
          consumeExpected('}');    
      }
      else {
         while (!tokenIs('}') && m_token != null)
         {
             funcBodyXPathExprStrPartsList.add(m_token);
             nextToken();
         }         
         consumeExpected('}');         
      }
      
      funcBodyXPathExprStr = getXPathStrFromComponentParts(funcBodyXPathExprStrPartsList);
      
      if (funcBodyXPathExprStr.length() > 0) {
         inlineFunction.setFuncBodyXPathExprStr(funcBodyXPathExprStr);
      }
      
      return inlineFunction;
  }

  /**
   *
   * Argument    ::=    Expr
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected void Argument() throws javax.xml.transform.TransformerException
  {
	
    int opPos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);
    
    appendOp(2, OpCodes.OP_ARGUMENT);
    
    if (tokenIs('(')) {
    	// XPath literal sequence as, function argument.
    	// XPath 3.1 spec : 3.4.1 Constructing Sequences
    	
    	int prevQueueMark = m_queueMark;
    	
    	if (lookahead(')', 1)) {
	        // an XPath function argument is () (i.e, an empty sequence)                
	        
	        consumeExpected('(');
	        consumeExpected(')');                            
	        
	        insertOp(opPos, 2, OpCodes.OP_SEQUENCE_CONSTRUCTOR_EXPR);
	        
	        List<String> seqConstructorXPathParts = new ArrayList<String>();
	        seqConstructorXPathParts.add(XPATH_EXPR_STR_EMPTY_SEQUENCE);
	        
	        List<XPathSequenceConstructor> seqConsList = fXPathSequenceConsFuncArgs.getSeqFuncArgList();
	        XPathSequenceConstructor xPathSeqConstructor = new XPathSequenceConstructor();                 
	        xPathSeqConstructor.setSequenceConstructorXPathParts(seqConstructorXPathParts);    	
	        seqConsList.add(xPathSeqConstructor);
	      	List<Boolean> funcArgUsedSeq = fXPathSequenceConsFuncArgs.getIsFuncArgUsedList();
	      	funcArgUsedSeq.add(Boolean.valueOf(false));
	        
	        m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH, 
	                                             m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);	
    	}
    	else {
	       consumeExpected('(');    		
	 	    		
	       List<String> seqConstructorXPathParts = new ArrayList<String>();
	       parseSequenceOrArrayConstructorFuncArg(seqConstructorXPathParts, '(', ')');
	       consumeExpected(')'); 
	            
	       if (seqConstructorXPathParts.size() > 1) {
	          insertOp(opPos, 2, OpCodes.OP_SEQUENCE_CONSTRUCTOR_EXPR);
	          
	          List<XPathSequenceConstructor> seqConsList = fXPathSequenceConsFuncArgs.getSeqFuncArgList();
	          XPathSequenceConstructor xPathSeqConstructor = new XPathSequenceConstructor();                 
	          xPathSeqConstructor.setSequenceConstructorXPathParts(seqConstructorXPathParts);    	
	          seqConsList.add(xPathSeqConstructor);
	      	  List<Boolean> funcArgUsedSeq = fXPathSequenceConsFuncArgs.getIsFuncArgUsedList();
	      	  funcArgUsedSeq.add(Boolean.valueOf(false));
	      	
	          m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH, 
	                                                 m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);	          
	       }
	       else {
	          m_queueMark = prevQueueMark;
	          m_tokenChar = '(';
	          Expr();
	       }
    	}
    }
    else if (tokenIs('[')) {
    	// XPath literal square array as, function argument.
    	// XPath 3.1 spec : 3.11.2.1 Array Constructors
    	
    	consumeExpected('[');    		

    	List<String> arrConstructorXPathParts = new ArrayList<String>();
    	parseSequenceOrArrayConstructorFuncArg(arrConstructorXPathParts, '[', ']');
    	consumeExpected(']'); 

    	insertOp(opPos, 2, OpCodes.OP_ARRAY_CONSTRUCTOR_EXPR);
    	
    	List<XPathArrayConstructor> arrConsList = fXPathArrayConsFuncArgs.getArrayFuncArgList();
    	XPathArrayConstructor xPathArrayConstructor = new XPathArrayConstructor();                 
    	xPathArrayConstructor.setArrayConstructorXPathParts(arrConstructorXPathParts);    	
    	arrConsList.add(xPathArrayConstructor);
    	List<Boolean> funcArgUsedArr = fXPathArrayConsFuncArgs.getIsFuncArgUsedArr();
    	funcArgUsedArr.add(Boolean.valueOf(false));

    	m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH, 
    			                               m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);	       
    }
    else if (tokenIs("array") && lookahead('{', 1)) {
    	// XPath literal curly array as, function argument.
    	// XPath 3.1 spec : 3.11.2.1 Array Constructors
    	
    	consumeExpected("array");
    	consumeExpected('{');

    	List<String> arrConstructorXPathParts = new ArrayList<String>();
    	parseSequenceOrArrayConstructorFuncArg(arrConstructorXPathParts, '{', '}');
    	consumeExpected('}'); 

    	insertOp(opPos, 2, OpCodes.OP_ARRAY_CONSTRUCTOR_EXPR);
    	
    	List<XPathArrayConstructor> arrConsList = fXPathArrayConsFuncArgs.getArrayFuncArgList();
    	XPathArrayConstructor xPathArrayConstructor = new XPathArrayConstructor();                 
    	xPathArrayConstructor.setArrayConstructorXPathParts(arrConstructorXPathParts);    	    	
    	arrConsList.add(xPathArrayConstructor);
    	List<Boolean> funcArgUsedArr = fXPathArrayConsFuncArgs.getIsFuncArgUsedArr();
    	funcArgUsedArr.add(Boolean.valueOf(false));

    	m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH, 
    			                               m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);	       
    }
    else if (tokenIs("map")) {
       // XPath literal map expression as, function argument
  	   mapFuncArg();	
    }
    else {       
       Expr();       
    }
    
    m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH,
                                           m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);
    
  }
  
  /**
   * This method is used to parse any of the following XPath literals : 
   * 'sequence constructor', 'square array constructor', 'curly array 
   * constructor'.
   * 
   * A valid pair of <lParen, rParen> characters, determine whether the
   * token queue from current position incrementally shall be interpreted as
   * one of the following XPath literals : 'sequence constructor', 'square array 
   * constructor', or 'curly array constructor'.
   * 
   * @xpathExprPartList : String array, that'll be populated with xpath expression
   *                      strings of the sequence/array items in order.
   * @lParen            : This shall be one of following : '(', '[', '{'.
   * @rParen            : This shall be one of following : ')', ']', '}'.                        
   */
  private void parseSequenceOrArrayConstructorFuncArg(List<String> xpathExprPartList, char lParen, 
		                                                               char rParen) throws TransformerException {	  
	  String xpathExpr = "";	  
	  boolean fl1 = true;
	  
	  while (!tokenIs(rParen)) {
		  if (!(tokenIs(',') || tokenIs(rParen))) {
			  xpathExpr += m_token;
			  nextToken();
			  if (tokenIs(rParen) && !isStrHasBalancedParentheses(lParen + xpathExpr, lParen, rParen)) {
				  if (lookahead(null, 1)) {
					  xpathExprPartList.add(xpathExpr);
					  break;
				  }
				  else if (lookahead(',', 1)) {
					  xpathExprPartList.add(xpathExpr);
					  xpathExpr = "";
					  break;
				  }
				  else if (!xpathExpr.contains(lParen+"") && tokenIs(rParen)) {
					  xpathExprPartList.add(xpathExpr);
					  xpathExpr = "";
					  break;
				  }
				  else {
					  xpathExpr += m_token;
				  }
				  nextToken();
				  if (tokenIs(rParen)) {
					  if (isStrHasBalancedParentheses(lParen + xpathExpr + rParen, lParen, rParen) 
							                                                    && !lookahead(rParen, 1)) {
						  xpathExpr = (lParen + xpathExpr + m_token); 
						  nextToken();
						  fl1 = false;
					  }
				  }
				  else {
					  xpathExpr += m_token;
					  nextToken();
				  }
			  }
			  else if (fl1){		       	    
				  if (tokenIs(',')) {
					  xpathExprPartList.add(xpathExpr);
					  xpathExpr = "";
					  consumeExpected(',');
				  }
				  continue;
			  }
			  else {
				  fl1 = true;
				  xpathExpr += m_token;
				  nextToken();
				  break;
			  }
		  }
		  else if (tokenIs(',')) {
			  xpathExprPartList.add(xpathExpr);
			  xpathExpr = "";
			  consumeExpected(',');
		  }
	  } 
  }

  /**
   * This method is used to parse, an XPath literal 'map' as 
   * function argument.
   */
  private void mapFuncArg() throws TransformerException {	  

	  int opPos1 = m_ops.getOp(OpMap.MAPINDEX_LENGTH);
	  
	  nextToken();
	  
	  insertOp(opPos1, 2, OpCodes.OP_MAP_CONSTRUCTOR_EXPR);

	  fXPathMapConstructor = new XPathMapConstructor();
	  
	  Map<String, String> nativeMapObj = new HashMap<String, String>();                    

	  consumeExpected('{');

	  while (!tokenIs('}')) {        	 
		  String mapEntryKeyXPathExprStr = m_token;
		  nextToken();
		  consumeExpected(':');
		  String mapEntryValueXPathExprStr = null;
		  if (tokenIs("map")) {
			  // There's likely an XPath map constructor here, 
			  // within this map. 	        		
			  mapEntryValueXPathExprStr = getMapConstructorStrValue();
		  }
		  else if (tokenIs('[')) {
			  // There's likely an XPath square array constructor here, 
			  // within this map. 	        		
			  mapEntryValueXPathExprStr = getXPathArrayConstructorStrValue(true); 
		  }
		  else if (tokenIs("array") && lookahead('{', 1)) {
			  // There's likely an XPath curly array constructor here, 
			  // within this map. 	        		
			  mapEntryValueXPathExprStr = getXPathArrayConstructorStrValue(false); 
		  }
		  else {
			  // The map's key value here is a simple value (i.e, not an array, or 
			  // map).
			  mapEntryValueXPathExprStr = m_token;
			  while (!(tokenIs(',') || tokenIs('}'))) {
				  nextToken();
				  if (!(tokenIs(',') || tokenIs('}'))) {
					  mapEntryValueXPathExprStr += m_token;
				  }
			  }
		  }
		  nativeMapObj.put(mapEntryKeyXPathExprStr, mapEntryValueXPathExprStr);
		  if (tokenIs(',')) {
			  consumeExpected(','); 
		  }
		  else if (tokenIs(']')) {
			  consumeExpected(']');
			  if (tokenIs(',')) {
				  consumeExpected(','); 
			  }
			  else if (tokenIs('}')) {
				  consumeExpected('}');
				  break;
			  }
		  }
		  else if (tokenIs('}')) {
			  consumeExpected('}');
			  break;
		  }
	  }

	  fXPathMapConstructor.setNativeMap(nativeMapObj);
	  
	  m_ops.setOp(opPos1 + OpMap.MAPINDEX_LENGTH,
			                            m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos1);
  }

  /**
   *
   * FunctionCall    ::=    FunctionName '(' ( Argument ( ',' Argument)*)? ')'
   *
   * @return true if, and only if, a FunctionCall was matched
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected boolean FunctionCall() throws javax.xml.transform.TransformerException
  {

    int opPos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);

    if (lookahead(':', 1))
    {
      if (tokenIs(FunctionTable.XPATH_BUILT_IN_FUNCS_NS_URI)) 
      {
         nextToken();
         consumeExpected(':');
         
         int funcTok = getFunctionToken(m_token, FunctionTable.XPATH_BUILT_IN_FUNCS_NS_URI);

         if (-1 == funcTok)
         {
           error(XPATHErrorResources.ER_COULDNOT_FIND_FUNCTION,
                 new Object[] {"{" + FunctionTable.XPATH_BUILT_IN_FUNCS_NS_URI + "}" + m_token + "()"});
         }
         else if ((FunctionTable.XPATH_MATH_FUNC_IDS_ARR).contains(Integer.valueOf(funcTok))) {
             funcTok = -1;
             error(XPATHErrorResources.ER_COULDNOT_FIND_FUNCTION,
                   new Object[] {"{" + FunctionTable.XPATH_BUILT_IN_FUNCS_NS_URI + "}" + m_token + "()"});  
         }
         else if ((FunctionTable.XPATH_ARRAY_FUNC_IDS_ARR).contains(Integer.valueOf(funcTok))) {
             funcTok = -1;
             error(XPATHErrorResources.ER_COULDNOT_FIND_FUNCTION,
                   new Object[] {"{" + FunctionTable.XPATH_BUILT_IN_FUNCS_NS_URI + "}" + m_token + "()"});  
         }
         else if ((FunctionTable.XPATH_MAP_FUNC_IDS_ARR).contains(Integer.valueOf(funcTok))) {
             funcTok = -1;
             error(XPATHErrorResources.ER_COULDNOT_FIND_FUNCTION,
                   new Object[] {"{" + FunctionTable.XPATH_BUILT_IN_FUNCS_NS_URI + "}" + m_token + "()"});  
         }

         switch (funcTok)
         {
            case OpCodes.NODETYPE_PI :
            case OpCodes.NODETYPE_COMMENT :
            case OpCodes.NODETYPE_TEXT :
            case OpCodes.NODETYPE_NODE :
              // Node type tests look like function calls, but they're not
              return false;
            default :
              appendOp(3, OpCodes.OP_FUNCTION);

            m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH + 1, funcTok);
         }

         nextToken();
      }
      else if (tokenIs(FunctionTable.XPATH_BUILT_IN_MATH_FUNCS_NS_URI)) 
      {
         nextToken();
         consumeExpected(':');
         
         int funcTok = getFunctionToken(m_token, FunctionTable.XPATH_BUILT_IN_MATH_FUNCS_NS_URI);

         if (-1 == funcTok)
         {
           error(XPATHErrorResources.ER_COULDNOT_FIND_FUNCTION,
                 new Object[] {"{" + FunctionTable.XPATH_BUILT_IN_MATH_FUNCS_NS_URI + "}" + m_token + "()"});
         }
         else if (!(FunctionTable.XPATH_MATH_FUNC_IDS_ARR).contains(Integer.valueOf(funcTok))) {
             funcTok = -1;
             error(XPATHErrorResources.ER_COULDNOT_FIND_FUNCTION,
                   new Object[] {"{" + FunctionTable.XPATH_BUILT_IN_MATH_FUNCS_NS_URI + "}" + m_token + "()"});  
         }

         switch (funcTok)
         {
            case OpCodes.NODETYPE_PI :
            case OpCodes.NODETYPE_COMMENT :
            case OpCodes.NODETYPE_TEXT :
            case OpCodes.NODETYPE_NODE :
              // Node type tests look like function calls, but they're not
              return false;
            default :
              appendOp(3, OpCodes.OP_FUNCTION);

            m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH + 1, funcTok);
         }

         nextToken();
      }
      else if (tokenIs(FunctionTable.XPATH_BUILT_IN_MAP_FUNCS_NS_URI)) 
      {
         nextToken();
         consumeExpected(':');
         
         int funcTok = getFunctionToken(m_token, FunctionTable.XPATH_BUILT_IN_MAP_FUNCS_NS_URI);

         if (-1 == funcTok)
         {
             error(XPATHErrorResources.ER_COULDNOT_FIND_FUNCTION,
                 new Object[] {"{" + FunctionTable.XPATH_BUILT_IN_MAP_FUNCS_NS_URI + "}" + m_token + "()"});
         }
         else if (!(FunctionTable.XPATH_MAP_FUNC_IDS_ARR).contains(Integer.valueOf(funcTok))) {
             funcTok = -1;
             error(XPATHErrorResources.ER_COULDNOT_FIND_FUNCTION,
                   new Object[] {"{" + FunctionTable.XPATH_BUILT_IN_ARRAY_FUNCS_NS_URI + "}" + m_token + "()"});  
         }

         switch (funcTok)
         {
            case OpCodes.NODETYPE_PI :
            case OpCodes.NODETYPE_COMMENT :
            case OpCodes.NODETYPE_TEXT :
            case OpCodes.NODETYPE_NODE :
              // Node type tests look like function calls, but they're not
              return false;
            default :
              appendOp(3, OpCodes.OP_FUNCTION);

            m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH + 1, funcTok);
         }

         nextToken();
      }
      else if (tokenIs(FunctionTable.XPATH_BUILT_IN_ARRAY_FUNCS_NS_URI)) 
      {
         nextToken();
         consumeExpected(':');
         
         int funcTok = getFunctionToken(m_token, FunctionTable.XPATH_BUILT_IN_ARRAY_FUNCS_NS_URI);

         if (-1 == funcTok)
         {
           error(XPATHErrorResources.ER_COULDNOT_FIND_FUNCTION,
                 new Object[] {"{" + FunctionTable.XPATH_BUILT_IN_ARRAY_FUNCS_NS_URI + "}" + m_token + "()"});
         }
         else if (!(FunctionTable.XPATH_ARRAY_FUNC_IDS_ARR).contains(Integer.valueOf(funcTok))) {
             funcTok = -1;
             error(XPATHErrorResources.ER_COULDNOT_FIND_FUNCTION,
                   new Object[] {"{" + FunctionTable.XPATH_BUILT_IN_ARRAY_FUNCS_NS_URI + "}" + m_token + "()"});  
         }

         switch (funcTok)
         {
            case OpCodes.NODETYPE_PI :
            case OpCodes.NODETYPE_COMMENT :
            case OpCodes.NODETYPE_TEXT :
            case OpCodes.NODETYPE_NODE :
              // Node type tests look like function calls, but they're not
              return false;
            default :
              appendOp(3, OpCodes.OP_FUNCTION);

            m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH + 1, funcTok);
         }

         nextToken();
      }
      else {  
        appendOp(4, OpCodes.OP_EXTFUNCTION);

        m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH + 1, m_queueMark - 1);

        nextToken();
        consumeExpected(':');

        m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH + 2, m_queueMark - 1);

        nextToken();
      }
    }
    else
    {
      int funcTok = getFunctionToken(m_token, FunctionTable.XPATH_BUILT_IN_FUNCS_NS_URI);

      if (funcTok == -1)
      {
          error(XPATHErrorResources.ER_COULDNOT_FIND_FUNCTION,
              new Object[]{"{" + FunctionTable.XPATH_BUILT_IN_FUNCS_NS_URI + "}" + m_token + "()"});
      }
      else if ((FunctionTable.XPATH_MATH_FUNC_IDS_ARR).contains(Integer.valueOf(funcTok))) {
          funcTok = -1;
          error(XPATHErrorResources.ER_COULDNOT_FIND_FUNCTION,
                new Object[] {"{" + FunctionTable.XPATH_BUILT_IN_FUNCS_NS_URI + "}" + m_token + "()"});  
      }      
      else if ((FunctionTable.XPATH_MAP_FUNC_IDS_ARR).contains(Integer.valueOf(funcTok))) {
          funcTok = -1;
          error(XPATHErrorResources.ER_COULDNOT_FIND_FUNCTION,
                new Object[] {"{" + FunctionTable.XPATH_BUILT_IN_FUNCS_NS_URI + "}" + m_token + "()"});  
      }
      else if ((FunctionTable.XPATH_ARRAY_FUNC_IDS_ARR).contains(Integer.valueOf(funcTok))) {
          funcTok = -1;
          error(XPATHErrorResources.ER_COULDNOT_FIND_FUNCTION,
                new Object[] {"{" + FunctionTable.XPATH_BUILT_IN_FUNCS_NS_URI + "}" + m_token + "()"});  
      }

      switch (funcTok)
      {
         case OpCodes.NODETYPE_PI :
         case OpCodes.NODETYPE_COMMENT :
         case OpCodes.NODETYPE_TEXT :
         case OpCodes.NODETYPE_NODE :
           // Node type tests look like function calls, but they're not
           return false;
         default :
           appendOp(3, OpCodes.OP_FUNCTION);

         m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH + 1, funcTok);
      }

      nextToken();
    }

    consumeExpected('(');

    while (!tokenIs(')') && m_token != null)
    {
      if (tokenIs(','))
      {
        error(XPATHErrorResources.ER_FOUND_COMMA_BUT_NO_PRECEDING_ARG, null);  //"Found ',' but no preceding argument!");
      }

      Argument();

      if (!tokenIs(')'))
      {
        consumeExpected(',');

        if (tokenIs(')'))
        {
          error(XPATHErrorResources.ER_FOUND_COMMA_BUT_NO_FOLLOWING_ARG,
                null);  //"Found ',' but no following argument!");
        }
      }
    }

    consumeExpected(')');

    m_ops.setOp(m_ops.getOp(OpMap.MAPINDEX_LENGTH), OpCodes.ENDOP);
    m_ops.setOp(OpMap.MAPINDEX_LENGTH,m_ops.getOp(OpMap.MAPINDEX_LENGTH) + 1);
    m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH, 
      m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);

    return true;
  }

  // ============= GRAMMAR FUNCTIONS =================

  /**
   *
   * LocationPath ::= RelativeLocationPath
   * | AbsoluteLocationPath
   *
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected void LocationPath() throws javax.xml.transform.TransformerException
  {

    int opPos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);

    // int locationPathOpPos = opPos;
    appendOp(2, OpCodes.OP_LOCATIONPATH);

    boolean seenSlash = tokenIs('/');

    if (seenSlash)
    {
      appendOp(4, OpCodes.FROM_ROOT);

      // Tell how long the step is without the predicate
      m_ops.setOp(m_ops.getOp(OpMap.MAPINDEX_LENGTH) - 2, 4);
      m_ops.setOp(m_ops.getOp(OpMap.MAPINDEX_LENGTH) - 1, OpCodes.NODETYPE_ROOT);

      nextToken();
    } else if (m_token == null) {
      error(XPATHErrorResources.ER_EXPECTED_LOC_PATH_AT_END_EXPR, null);
    }

    if (m_token != null)
    {
      if (!RelativeLocationPath() && !seenSlash)
      {
        // Neither a '/' nor a RelativeLocationPath - i.e., matched nothing
        // "Location path expected, but found "+m_token+" was encountered."
        error(XPATHErrorResources.ER_EXPECTED_LOC_PATH, 
              new Object [] {m_token});
      }
    }

    m_ops.setOp(m_ops.getOp(OpMap.MAPINDEX_LENGTH), OpCodes.ENDOP);
    m_ops.setOp(OpMap.MAPINDEX_LENGTH,m_ops.getOp(OpMap.MAPINDEX_LENGTH) + 1);
    m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH,
      m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);
  }

  /**
   *
   * RelativeLocationPath ::= Step
   * | RelativeLocationPath '/' Step
   * | AbbreviatedRelativeLocationPath
   *
   * @returns true if, and only if, a RelativeLocationPath was matched
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected boolean RelativeLocationPath()
               throws javax.xml.transform.TransformerException
  {
    if (!Step())
    {
      return false;
    }

    while (tokenIs('/'))
    {
      nextToken();

      if (!Step())
      {
        // RelativeLocationPath can't end with a trailing '/'
        // "Location step expected following '/' or '//'"
        error(XPATHErrorResources.ER_EXPECTED_LOC_STEP, null);
      }
    }

    return true;
  }

  /**
   *
   * Step    ::=    Basis Predicate
   * | AbbreviatedStep
   *
   * @returns false if step was empty (or only a '/'); true, otherwise
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected boolean Step() throws javax.xml.transform.TransformerException
  {
    int opPos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);

    boolean doubleSlash = tokenIs('/');

    // At most a single '/' before each Step is consumed by caller; if the
    // first thing is a '/', that means we had '//' and the Step must not
    // be empty.
    if (doubleSlash)
    {
      nextToken();

      appendOp(2, OpCodes.FROM_DESCENDANTS_OR_SELF);

      // Have to fix up for patterns such as '//@foo' or '//attribute::foo',
      // which translate to 'descendant-or-self::node()/attribute::foo'.
      // notice I leave the '/' on the queue, so the next will be processed
      // by a regular step pattern.

      // Make room for telling how long the step is without the predicate
      m_ops.setOp(OpMap.MAPINDEX_LENGTH,m_ops.getOp(OpMap.MAPINDEX_LENGTH) + 1);
      m_ops.setOp(m_ops.getOp(OpMap.MAPINDEX_LENGTH), OpCodes.NODETYPE_NODE);
      m_ops.setOp(OpMap.MAPINDEX_LENGTH,m_ops.getOp(OpMap.MAPINDEX_LENGTH) + 1);

      // Tell how long the step is without the predicate
      m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH + 1,
          m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);

      // Tell how long the step is with the predicate
      m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH,
          m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);

      opPos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);
    }

    if (tokenIs("."))
    {
      nextToken();

      if (tokenIs('['))
      {
        error(XPATHErrorResources.ER_PREDICATE_ILLEGAL_SYNTAX, null);  //"'..[predicate]' or '.[predicate]' is illegal syntax.  Use 'self::node()[predicate]' instead.");
      }

      appendOp(4, OpCodes.FROM_SELF);

      // Tell how long the step is without the predicate
      m_ops.setOp(m_ops.getOp(OpMap.MAPINDEX_LENGTH) - 2,4);
      m_ops.setOp(m_ops.getOp(OpMap.MAPINDEX_LENGTH) - 1, OpCodes.NODETYPE_NODE);
    }
    else if (tokenIs(".."))
    {
      nextToken();
      appendOp(4, OpCodes.FROM_PARENT);

      // Tell how long the step is without the predicate
      m_ops.setOp(m_ops.getOp(OpMap.MAPINDEX_LENGTH) - 2,4);
      m_ops.setOp(m_ops.getOp(OpMap.MAPINDEX_LENGTH) - 1, OpCodes.NODETYPE_NODE);
    }

    // There is probably a better way to test for this 
    // transition... but it gets real hairy if you try 
    // to do it in basis().
    else if (tokenIs('*') || tokenIs('@') || tokenIs('_')
             || (m_token!= null && Character.isLetter(m_token.charAt(0))))
    {
      Basis();

      while (tokenIs('['))
      {
        Predicate();
      }

      // Tell how long the entire step is.
      m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH,
        m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos); 
    }
    else
    {
      // No Step matched - that's an error if previous thing was a '//'
      if (doubleSlash)
      {
        // "Location step expected following '/' or '//'"
        error(XPATHErrorResources.ER_EXPECTED_LOC_STEP, null);
      }

      return false;
    }

    return true;
  }

  /**
   *
   * Basis    ::=    AxisName '::' NodeTest
   * | AbbreviatedBasis
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected void Basis() throws javax.xml.transform.TransformerException
  {

    int opPos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);
    int axesType;

    // The next blocks guarantee that a FROM_XXX will be added.
    if (lookahead("::", 1))
    {
      axesType = AxisName();

      nextToken();
      nextToken();
    }
    else if (tokenIs('@'))
    {
      axesType = OpCodes.FROM_ATTRIBUTES;

      appendOp(2, axesType);
      nextToken();
    }
    else
    {
      axesType = OpCodes.FROM_CHILDREN;

      appendOp(2, axesType);
    }

    // Make room for telling how long the step is without the predicate
    m_ops.setOp(OpMap.MAPINDEX_LENGTH, m_ops.getOp(OpMap.MAPINDEX_LENGTH) + 1);

    NodeTest(axesType);

    // Tell how long the step is without the predicate
    m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH + 1,
      m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);
   }

  /**
   *
   * Basis    ::=    AxisName '::' NodeTest
   * | AbbreviatedBasis
   *
   * @return FROM_XXX axes type, found in {@link org.apache.xpath.compiler.Keywords}.
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected int AxisName() throws javax.xml.transform.TransformerException
  {

    Object val = Keywords.getAxisName(m_token);

    if (null == val)
    {
      error(XPATHErrorResources.ER_ILLEGAL_AXIS_NAME,
            new Object[]{ m_token });  //"illegal axis name: "+m_token);
    }

    int axesType = ((Integer) val).intValue();

    appendOp(2, axesType);

    return axesType;
  }

  /**
   *
   * NodeTest    ::=    WildcardName
   * | NodeType '(' ')'
   * | 'processing-instruction' '(' Literal ')'
   *
   * @param axesType FROM_XXX axes type, found in {@link org.apache.xpath.compiler.Keywords}.
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected void NodeTest(int axesType) throws javax.xml.transform.TransformerException
  {

    if (lookahead('(', 1))
    {
      Object nodeTestOp = Keywords.getNodeType(m_token);

      if (null == nodeTestOp)
      {
        error(XPATHErrorResources.ER_UNKNOWN_NODETYPE,
              new Object[]{ m_token });  //"Unknown nodetype: "+m_token);
      }
      else
      {
        nextToken();

        int nt = ((Integer) nodeTestOp).intValue();

        m_ops.setOp(m_ops.getOp(OpMap.MAPINDEX_LENGTH), nt);
        m_ops.setOp(OpMap.MAPINDEX_LENGTH, m_ops.getOp(OpMap.MAPINDEX_LENGTH) + 1);

        consumeExpected('(');

        if (OpCodes.NODETYPE_PI == nt)
        {
          if (!tokenIs(')'))
          {
            Literal();
          }
        }

        consumeExpected(')');
      }
    }
    else
    {

      // Assume name of attribute or element.
      m_ops.setOp(m_ops.getOp(OpMap.MAPINDEX_LENGTH), OpCodes.NODENAME);
      m_ops.setOp(OpMap.MAPINDEX_LENGTH, m_ops.getOp(OpMap.MAPINDEX_LENGTH) + 1);

      if (lookahead(':', 1))
      {
        if (tokenIs('*'))
        {
          m_ops.setOp(m_ops.getOp(OpMap.MAPINDEX_LENGTH), OpCodes.ELEMWILDCARD);
        }
        else
        {
          m_ops.setOp(m_ops.getOp(OpMap.MAPINDEX_LENGTH), m_queueMark - 1);

          // Minimalist check for an NCName - just check first character
          // to distinguish from other possible tokens
          if (!Character.isLetter(m_tokenChar) && !tokenIs('_'))
          {
            // "Node test that matches either NCName:* or QName was expected."
            error(XPATHErrorResources.ER_EXPECTED_NODE_TEST, null);
          }
        }

        nextToken();
        consumeExpected(':');
      }
      else
      {
        m_ops.setOp(m_ops.getOp(OpMap.MAPINDEX_LENGTH), OpCodes.EMPTY);
      }

      m_ops.setOp(OpMap.MAPINDEX_LENGTH, m_ops.getOp(OpMap.MAPINDEX_LENGTH) + 1);

      if (tokenIs('*'))
      {
        m_ops.setOp(m_ops.getOp(OpMap.MAPINDEX_LENGTH), OpCodes.ELEMWILDCARD);
      }
      else
      {
        m_ops.setOp(m_ops.getOp(OpMap.MAPINDEX_LENGTH), m_queueMark - 1);

        // Minimalist check for an NCName - just check first character
        // to distinguish from other possible tokens
        if (!Character.isLetter(m_tokenChar) && !tokenIs('_'))
        {
          // "Node test that matches either NCName:* or QName was expected."
          error(XPATHErrorResources.ER_EXPECTED_NODE_TEST, null);
        }
      }

      m_ops.setOp(OpMap.MAPINDEX_LENGTH, m_ops.getOp(OpMap.MAPINDEX_LENGTH) + 1);

      nextToken();
    }
  }

  /**
   *
   * Predicate ::= '[' PredicateExpr ']'
   *
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected void Predicate() throws javax.xml.transform.TransformerException
  {

    if (tokenIs('['))
    {
      fIsXPathPredicateParsingActive = true;      
      nextToken();
      PredicateExpr();
      consumeExpected(']');      
      fIsXPathPredicateParsingActive = false;
    }
  }

  /**
   *
   * PredicateExpr ::= Expr
   *
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected void PredicateExpr() throws javax.xml.transform.TransformerException
  {

    int opPos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);

    appendOp(2, OpCodes.OP_PREDICATE);
    Expr();

    m_ops.setOp(m_ops.getOp(OpMap.MAPINDEX_LENGTH), OpCodes.ENDOP);
    m_ops.setOp(OpMap.MAPINDEX_LENGTH, m_ops.getOp(OpMap.MAPINDEX_LENGTH) + 1);
    m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH,
      m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);
  }

  /**
   * QName ::=  (Prefix ':')? LocalPart
   * Prefix ::=  NCName
   * LocalPart ::=  NCName
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected void QName() throws javax.xml.transform.TransformerException
  {
    // Namespace
    if(lookahead(':', 1))
    {
      m_ops.setOp(m_ops.getOp(OpMap.MAPINDEX_LENGTH), m_queueMark - 1);
      m_ops.setOp(OpMap.MAPINDEX_LENGTH, m_ops.getOp(OpMap.MAPINDEX_LENGTH) + 1);

      nextToken();
      consumeExpected(':');
    }
    else
    {
      m_ops.setOp(m_ops.getOp(OpMap.MAPINDEX_LENGTH), OpCodes.EMPTY);
      m_ops.setOp(OpMap.MAPINDEX_LENGTH, m_ops.getOp(OpMap.MAPINDEX_LENGTH) + 1);
    }
    
    // Local name
    m_ops.setOp(m_ops.getOp(OpMap.MAPINDEX_LENGTH), m_queueMark - 1);
    m_ops.setOp(OpMap.MAPINDEX_LENGTH, m_ops.getOp(OpMap.MAPINDEX_LENGTH) + 1);

    nextToken();
  }

  /**
   * NCName ::=  (Letter | '_') (NCNameChar)
   * NCNameChar ::=  Letter | Digit | '.' | '-' | '_' | CombiningChar | Extender
   */
  protected void NCName()
  {

    m_ops.setOp(m_ops.getOp(OpMap.MAPINDEX_LENGTH), m_queueMark - 1);
    m_ops.setOp(OpMap.MAPINDEX_LENGTH, m_ops.getOp(OpMap.MAPINDEX_LENGTH) + 1);

    nextToken();
  }

  /**
   * The value of the Literal is the sequence of characters inside
   * the " or ' characters>.
   *
   * Literal  ::=  '"' [^"]* '"'
   * | "'" [^']* "'"
   *
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected void Literal() throws javax.xml.transform.TransformerException
  {

    int last = m_token.length() - 1;
    char c0 = m_tokenChar;
    char cX = m_token.charAt(last);

    if (((c0 == '\"') && (cX == '\"')) || ((c0 == '\'') && (cX == '\'')))
    {

      // Mutate the token to remove the quotes and have the XString object
      // already made.
      int tokenQueuePos = m_queueMark - 1;

      m_ops.m_tokenQueue.setElementAt(null,tokenQueuePos);

      Object obj = new XString(m_token.substring(1, last));

      m_ops.m_tokenQueue.setElementAt(obj,tokenQueuePos);

      // lit = m_token.substring(1, last);
      m_ops.setOp(m_ops.getOp(OpMap.MAPINDEX_LENGTH), tokenQueuePos);
      m_ops.setOp(OpMap.MAPINDEX_LENGTH, m_ops.getOp(OpMap.MAPINDEX_LENGTH) + 1);

      nextToken();
    }
    else
    {
      error(XPATHErrorResources.ER_PATTERN_LITERAL_NEEDS_BE_QUOTED,
            new Object[]{ m_token });  //"Pattern literal ("+m_token+") needs to be quoted!");
    }
  }

  /**
   *
   * Number ::= [0-9]+('.'[0-9]+)? | '.'[0-9]+
   *
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected void Number() throws javax.xml.transform.TransformerException
  {

    if (null != m_token)
    {

      // Mutate the token to remove the quotes and have the XNumber object
      // already made.
      double num;

      try
      {
      	// XPath 3.1 requires support for numbers in exp notation
      	/*if ((m_token.indexOf('e') > -1)||(m_token.indexOf('E') > -1))
      		throw new NumberFormatException();*/
        num = Double.valueOf(m_token).doubleValue();
      }
      catch (NumberFormatException nfe)
      {
        num = 0.0;  // to shut up compiler.

        error(XPATHErrorResources.ER_COULDNOT_BE_FORMATTED_TO_NUMBER,
              new Object[]{ m_token });  //m_token+" could not be formatted to a number!");
      }

      m_ops.m_tokenQueue.setElementAt(new XNumber(num),m_queueMark - 1);
      m_ops.setOp(m_ops.getOp(OpMap.MAPINDEX_LENGTH), m_queueMark - 1);
      m_ops.setOp(OpMap.MAPINDEX_LENGTH, m_ops.getOp(OpMap.MAPINDEX_LENGTH) + 1);

      nextToken();
    }
  }

  // ============= PATTERN FUNCTIONS =================

  /**
   *
   * Pattern  ::=  LocationPathPattern
   * | Pattern '|' LocationPathPattern
   *
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected void Pattern() throws javax.xml.transform.TransformerException
  {

    while (true)
    {
      LocationPathPattern();

      if (tokenIs('|'))
      {
        nextToken();
      }
      else
      {
        break;
      }
    }
  }

  /**
   *
   *
   * LocationPathPattern  ::=  '/' RelativePathPattern?
   * | IdKeyPattern (('/' | '//') RelativePathPattern)?
   * | '//'? RelativePathPattern
   *
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected void LocationPathPattern() throws javax.xml.transform.TransformerException
  {

    int opPos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);

    final int RELATIVE_PATH_NOT_PERMITTED = 0;
    final int RELATIVE_PATH_PERMITTED     = 1;
    final int RELATIVE_PATH_REQUIRED      = 2;

    int relativePathStatus = RELATIVE_PATH_NOT_PERMITTED;

    appendOp(2, OpCodes.OP_LOCATIONPATHPATTERN);

    if (lookahead('(', 1)
            && (tokenIs(Keywords.FUNC_ID_STRING)
                || tokenIs(Keywords.FUNC_KEY_STRING)))
    {
      IdKeyPattern();

      if (tokenIs('/'))
      {
        nextToken();

        if (tokenIs('/'))
        {
          appendOp(4, OpCodes.MATCH_ANY_ANCESTOR);

          nextToken();
        }
        else
        {
          appendOp(4, OpCodes.MATCH_IMMEDIATE_ANCESTOR);
        }

        // Tell how long the step is without the predicate
        m_ops.setOp(m_ops.getOp(OpMap.MAPINDEX_LENGTH) - 2, 4);
        m_ops.setOp(m_ops.getOp(OpMap.MAPINDEX_LENGTH) - 1, OpCodes.NODETYPE_FUNCTEST);

        relativePathStatus = RELATIVE_PATH_REQUIRED;
      }
    }
    else if (tokenIs('/'))
    {
      if (lookahead('/', 1))
      {
        appendOp(4, OpCodes.MATCH_ANY_ANCESTOR);
        
        // Added this to fix bug reported by Myriam for match="//x/a"
        // patterns.  If you don't do this, the 'x' step will think it's part
        // of a '//' pattern, and so will cause 'a' to be matched when it has
        // any ancestor that is 'x'.
        nextToken();

        relativePathStatus = RELATIVE_PATH_REQUIRED;
      }
      else
      {
        appendOp(4, OpCodes.FROM_ROOT);

        relativePathStatus = RELATIVE_PATH_PERMITTED;
      }


      // Tell how long the step is without the predicate
      m_ops.setOp(m_ops.getOp(OpMap.MAPINDEX_LENGTH) - 2, 4);
      m_ops.setOp(m_ops.getOp(OpMap.MAPINDEX_LENGTH) - 1, OpCodes.NODETYPE_ROOT);

      nextToken();
    }
    else
    {
      relativePathStatus = RELATIVE_PATH_REQUIRED;
    }

    if (relativePathStatus != RELATIVE_PATH_NOT_PERMITTED)
    {
      if (!tokenIs('|') && (null != m_token))
      {
        RelativePathPattern();
      }
      else if (relativePathStatus == RELATIVE_PATH_REQUIRED)
      {
        // "A relative path pattern was expected."
        error(XPATHErrorResources.ER_EXPECTED_REL_PATH_PATTERN, null);
      }
    }

    m_ops.setOp(m_ops.getOp(OpMap.MAPINDEX_LENGTH), OpCodes.ENDOP);
    m_ops.setOp(OpMap.MAPINDEX_LENGTH, m_ops.getOp(OpMap.MAPINDEX_LENGTH) + 1);
    m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH,
      m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);
  }

  /**
   *
   * IdKeyPattern  ::=  'id' '(' Literal ')'
   * | 'key' '(' Literal ',' Literal ')'
   * (Also handle doc())
   *
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected void IdKeyPattern() throws javax.xml.transform.TransformerException
  {
    FunctionCall();
  }

  /**
   *
   * RelativePathPattern  ::=  StepPattern
   * | RelativePathPattern '/' StepPattern
   * | RelativePathPattern '//' StepPattern
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected void RelativePathPattern()
              throws javax.xml.transform.TransformerException
  {

    // Caller will have consumed any '/' or '//' preceding the
    // RelativePathPattern, so let StepPattern know it can't begin with a '/'
    boolean trailingSlashConsumed = StepPattern(false);

    while (tokenIs('/'))
    {
      nextToken();

      // StepPattern() may consume first slash of pair in "a//b" while
      // processing StepPattern "a".  On next iteration, let StepPattern know
      // that happened, so it doesn't match ill-formed patterns like "a///b".
      trailingSlashConsumed = StepPattern(!trailingSlashConsumed);
    }
  }

  /**
   *
   * StepPattern  ::=  AbbreviatedNodeTestStep
   *
   * @param isLeadingSlashPermitted a boolean indicating whether a slash can
   *        appear at the start of this step
   *
   * @return boolean indicating whether a slash following the step was consumed
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected boolean StepPattern(boolean isLeadingSlashPermitted)
            throws javax.xml.transform.TransformerException
  {
    return AbbreviatedNodeTestStep(isLeadingSlashPermitted);
  }

  /**
   *
   * AbbreviatedNodeTestStep    ::=    '@'? NodeTest Predicate
   *
   * @param isLeadingSlashPermitted a boolean indicating whether a slash can
   *        appear at the start of this step
   *
   * @return boolean indicating whether a slash following the step was consumed
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected boolean AbbreviatedNodeTestStep(boolean isLeadingSlashPermitted)
            throws javax.xml.transform.TransformerException
  {

    int opPos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);
    int axesType;

    // The next blocks guarantee that a MATCH_XXX will be added.
    int matchTypePos = -1;

    if (tokenIs('@'))
    {
      axesType = OpCodes.MATCH_ATTRIBUTE;

      appendOp(2, axesType);
      nextToken();
    }
    else if (this.lookahead("::", 1))
    {
      if (tokenIs("attribute"))
      {
        axesType = OpCodes.MATCH_ATTRIBUTE;

        appendOp(2, axesType);
      }
      else if (tokenIs("child"))
      {
        matchTypePos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);
        axesType = OpCodes.MATCH_IMMEDIATE_ANCESTOR;

        appendOp(2, axesType);
      }
      else
      {
        axesType = -1;

        this.error(XPATHErrorResources.ER_AXES_NOT_ALLOWED,
                   new Object[]{ this.m_token });
      }

      nextToken();
      nextToken();
    }
    else if (tokenIs('/'))
    {
      if (!isLeadingSlashPermitted)
      {
        // "A step was expected in the pattern, but '/' was encountered."
        error(XPATHErrorResources.ER_EXPECTED_STEP_PATTERN, null);
      }
      axesType = OpCodes.MATCH_ANY_ANCESTOR;

      appendOp(2, axesType);
      nextToken();
    }
    else
    {
      matchTypePos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);
      axesType = OpCodes.MATCH_IMMEDIATE_ANCESTOR;

      appendOp(2, axesType);
    }

    // Make room for telling how long the step is without the predicate
    m_ops.setOp(OpMap.MAPINDEX_LENGTH, m_ops.getOp(OpMap.MAPINDEX_LENGTH) + 1);

    NodeTest(axesType);

    // Tell how long the step is without the predicate
    m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH + 1,
      m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);

    while (tokenIs('['))
    {
      Predicate();
    }

    boolean trailingSlashConsumed;

    // For "a//b", where "a" is current step, we need to mark operation of
    // current step as "MATCH_ANY_ANCESTOR".  Then we'll consume the first
    // slash and subsequent step will be treated as a MATCH_IMMEDIATE_ANCESTOR
    // (unless it too is followed by '//'.)
    //
    // %REVIEW%  Following is what happens today, but I'm not sure that's
    // %REVIEW%  correct behaviour.  Perhaps no valid case could be constructed
    // %REVIEW%  where it would matter?
    //
    // If current step is on the attribute axis (e.g., "@x//b"), we won't
    // change the current step, and let following step be marked as
    // MATCH_ANY_ANCESTOR on next call instead.
    if ((matchTypePos > -1) && tokenIs('/') && lookahead('/', 1))
    {
      m_ops.setOp(matchTypePos, OpCodes.MATCH_ANY_ANCESTOR);

      nextToken();

      trailingSlashConsumed = true;
    }
    else
    {
      trailingSlashConsumed = false;
    }

    // Tell how long the entire step is.
    m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH,
      m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);

    return trailingSlashConsumed;
  }
  
  /**
   * Parse XPath 'sequence type' expressions.
   */
  protected XPathSequenceTypeExpr SequenceTypeExpr(boolean isInlineFunction) 
                                                                          throws javax.xml.transform.TransformerException {
      int opPos = 0;
      
      if (!isInlineFunction) {
         opPos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);                  
         insertOp(opPos, 2, OpCodes.OP_SEQUENCE_TYPE_EXPR);
      }
      
      XPathSequenceTypeExpr xpathSequenceTypeExpr = new XPathSequenceTypeExpr();
      
      SequenceTypeKindTest sequenceTypeKindTest = null;
      
      if (tokenIs("empty-sequence") && lookahead('(', 1) && lookahead(')', 2)) {
         xpathSequenceTypeExpr.setSequenceType(SequenceTypeSupport.EMPTY_SEQUENCE);
         consumeExpected("empty-sequence");
         consumeExpected('(');
         consumeExpected(')');
      }      
      else if (tokenIs("element")) {
          sequenceTypeKindTest = constructSequenceTypeKindTestForXDMNodes(xpathSequenceTypeExpr, 
                                                                                        SequenceTypeSupport.ELEMENT_KIND, isInlineFunction);          
          xpathSequenceTypeExpr.setSequenceTypeKindTest(sequenceTypeKindTest);          
      }
      else if (tokenIs("attribute")) {
          sequenceTypeKindTest = constructSequenceTypeKindTestForXDMNodes(xpathSequenceTypeExpr, 
                                                                                        SequenceTypeSupport.ATTRIBUTE_KIND, isInlineFunction);          
          xpathSequenceTypeExpr.setSequenceTypeKindTest(sequenceTypeKindTest);
      }
      else if (tokenIs("text")) {
          sequenceTypeKindTest = new SequenceTypeKindTest();
          sequenceTypeKindTest.setKindVal(SequenceTypeSupport.TEXT_KIND);          
          nextToken();
          consumeExpected('(');
          consumeExpected(')');
          xpathSequenceTypeExpr.setSequenceTypeKindTest(sequenceTypeKindTest);
          if (m_token != null) {
             setSequenceTypeOccurenceIndicator(xpathSequenceTypeExpr, isInlineFunction); 
          }          
      }
      else if (tokenIs("namespace-node")) {
          sequenceTypeKindTest = new SequenceTypeKindTest();
          sequenceTypeKindTest.setKindVal(SequenceTypeSupport.NAMESPACE_NODE_KIND);          
          nextToken();
          consumeExpected('(');
          consumeExpected(')');
          xpathSequenceTypeExpr.setSequenceTypeKindTest(sequenceTypeKindTest);
          if (m_token != null) {
             setSequenceTypeOccurenceIndicator(xpathSequenceTypeExpr, isInlineFunction); 
          }
      }
      else if (tokenIs("node")) { 
          sequenceTypeKindTest = new SequenceTypeKindTest();
          sequenceTypeKindTest.setKindVal(SequenceTypeSupport.NODE_KIND);          
          nextToken();
          consumeExpected('(');
          consumeExpected(')');
          xpathSequenceTypeExpr.setSequenceTypeKindTest(sequenceTypeKindTest);
          if (m_token != null) {
             setSequenceTypeOccurenceIndicator(xpathSequenceTypeExpr, isInlineFunction);  
          }
      }
      else if (tokenIs("item")) {
          sequenceTypeKindTest = new SequenceTypeKindTest();
          sequenceTypeKindTest.setKindVal(SequenceTypeSupport.ITEM_KIND);          
          nextToken();
          consumeExpected('(');
          consumeExpected(')');
          xpathSequenceTypeExpr.setSequenceTypeKindTest(sequenceTypeKindTest);
          if (m_token != null) {
             setSequenceTypeOccurenceIndicator(xpathSequenceTypeExpr, isInlineFunction);  
          }
      }
      else if (tokenIs("function")) {
    	  parseFunctionItemSequenceType(xpathSequenceTypeExpr);
      }
      else if (tokenIs("map")) {
    	  parseXdmMapSequenceType(xpathSequenceTypeExpr);    	  
      }
      else if (tokenIs("array")) {
          parseXdmArraySequenceType(xpathSequenceTypeExpr);
      }
      else if (tokenIs(XMLConstants.W3C_XML_SCHEMA_NS_URI)) {
          parseXdmBuiltInXmlSchemaSequenceType(isInlineFunction, xpathSequenceTypeExpr);         
      }
      else {
          throw new javax.xml.transform.TransformerException("XPST0051 The sequence type '" + m_token + "' is not "
                                                                                   + "recognized, within the provided sequence type expression."); 
      }
      
      if (!isInlineFunction) {
         m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH,
                                                m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);
      }
      
      return xpathSequenceTypeExpr;
  }

  /**
   * Parse XPath built-in XML Schema sequence type expressions.
   */
  private void parseXdmBuiltInXmlSchemaSequenceType(boolean isInlineFunction, XPathSequenceTypeExpr xpathSequenceTypeExpr)
		                                                         throws TransformerException {	  
	  
	  consumeExpected(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	  consumeExpected(':');
	  
	  switch (m_token) {
	     case Keywords.FUNC_BOOLEAN_STRING :
	        xpathSequenceTypeExpr.setSequenceType(SequenceTypeSupport.BOOLEAN);
	        break;
	     case Keywords.XS_STRING :
	        xpathSequenceTypeExpr.setSequenceType(SequenceTypeSupport.STRING);
	        break; 
	     case Keywords.XS_NORMALIZED_STRING :
	         xpathSequenceTypeExpr.setSequenceType(SequenceTypeSupport.XS_NORMALIZED_STRING);
	         break;
	     case Keywords.XS_TOKEN :
	         xpathSequenceTypeExpr.setSequenceType(SequenceTypeSupport.XS_TOKEN);
	         break;
	     case Keywords.XS_DECIMAL :
	        xpathSequenceTypeExpr.setSequenceType(SequenceTypeSupport.XS_DECIMAL);
	        break; 
	     case Keywords.XS_FLOAT :
	        xpathSequenceTypeExpr.setSequenceType(SequenceTypeSupport.XS_FLOAT);
	        break; 
	     case Keywords.XS_DOUBLE :
	        xpathSequenceTypeExpr.setSequenceType(SequenceTypeSupport.XS_DOUBLE);
	        break;
	     case Keywords.XS_INTEGER :
	        xpathSequenceTypeExpr.setSequenceType(SequenceTypeSupport.XS_INTEGER);
	        break;
	     case Keywords.XS_LONG :
	        xpathSequenceTypeExpr.setSequenceType(SequenceTypeSupport.XS_LONG);
	        break; 
	     case Keywords.XS_INT :
	        xpathSequenceTypeExpr.setSequenceType(SequenceTypeSupport.XS_INT);
	        break;
	     case Keywords.XS_DATE :
	        xpathSequenceTypeExpr.setSequenceType(SequenceTypeSupport.XS_DATE);
	        break;
	     case Keywords.XS_DATETIME :
	         xpathSequenceTypeExpr.setSequenceType(SequenceTypeSupport.XS_DATETIME);
	         break;
	     case Keywords.XS_DURATION :
	        xpathSequenceTypeExpr.setSequenceType(SequenceTypeSupport.XS_DURATION);
	        break;
	     case Keywords.XS_YEAR_MONTH_DURATION :
	        xpathSequenceTypeExpr.setSequenceType(SequenceTypeSupport.XS_YEARMONTH_DURATION);
	        break;
	     case Keywords.XS_DAY_TIME_DURATION :
	        xpathSequenceTypeExpr.setSequenceType(SequenceTypeSupport.XS_DAYTIME_DURATION);
	        break;
	     case Keywords.XS_TIME :
	         xpathSequenceTypeExpr.setSequenceType(SequenceTypeSupport.XS_TIME);
	         break;
	     case Keywords.XS_ANY_URI :
	         xpathSequenceTypeExpr.setSequenceType(SequenceTypeSupport.XS_ANY_URI);
	         break;
	     case Keywords.XS_QNAME :
	         xpathSequenceTypeExpr.setSequenceType(SequenceTypeSupport.XS_QNAME);
	         break;
	     case Keywords.XS_ANY_ATOMIC_TYPE :
	         xpathSequenceTypeExpr.setSequenceType(SequenceTypeSupport.XS_ANY_ATOMIC_TYPE);
	         break;
	     case Keywords.XS_UNTYPED_ATOMIC :
	         xpathSequenceTypeExpr.setSequenceType(SequenceTypeSupport.XS_UNTYPED_ATOMIC);
	         break;
	     case Keywords.XS_UNTYPED :
	         xpathSequenceTypeExpr.setSequenceType(SequenceTypeSupport.XS_UNTYPED);
	         break;
	     default :
	        throw new javax.xml.transform.TransformerException("XPST0051 : An XML Schema type 'xs:" + m_token + "' is not "
	                                                                                   + "recognized, within the provided sequence type expression.");        
	  }
	  
	  nextToken();
	  
	  if ((m_token != null) && (xpathSequenceTypeExpr.getSequenceType() > 0)) {
	     setSequenceTypeOccurenceIndicator(xpathSequenceTypeExpr, isInlineFunction);
	  }
  }

  /**
   * Parse XPath 'array' sequence type expressions.
   */
  private void parseXdmArraySequenceType(XPathSequenceTypeExpr xpathSequenceTypeExpr) throws TransformerException {
	  
	  SequenceTypeArrayTest sequenceTypeArrayTest = null;
	  
	  if (lookahead('(', 1) && lookahead('*', 2) && lookahead(')', 3)) {
		  // handles ArrayTest of variety AnyArrayTest
		  sequenceTypeArrayTest = new SequenceTypeArrayTest();
		  sequenceTypeArrayTest.setIsAnyArrayTest(true);
		  nextToken();
		  consumeExpected('(');
	      consumeExpected('*');
	      consumeExpected(')');
	      xpathSequenceTypeExpr.setSequenceTypeArrayTest(sequenceTypeArrayTest);
	  }
	  else {
		  // handles ArrayTest of variety TypedArrayTest
		  sequenceTypeArrayTest = new SequenceTypeArrayTest();
		  nextToken();
		  consumeExpected('(');
		  
		  SequenceTypeData arrayItemSequenceType = new SequenceTypeData();
		  while ((m_token != null) && !tokenIs(')')) {
			 if (tokenIs(XMLConstants.W3C_XML_SCHEMA_NS_URI)) {
			    consumeExpected(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			    consumeExpected(':');
			    
			    switch (m_token) {
		            case Keywords.FUNC_BOOLEAN_STRING :
		            	arrayItemSequenceType.setSequenceType(SequenceTypeSupport.BOOLEAN);
		               break;
		            case Keywords.XS_STRING :
		            	arrayItemSequenceType.setSequenceType(SequenceTypeSupport.STRING);
		               break; 
		            case Keywords.XS_NORMALIZED_STRING :
		            	arrayItemSequenceType.setSequenceType(SequenceTypeSupport.XS_NORMALIZED_STRING);
		                break;
		            case Keywords.XS_TOKEN :
		            	arrayItemSequenceType.setSequenceType(SequenceTypeSupport.XS_TOKEN);
		                break;
		            case Keywords.XS_DECIMAL :
		            	arrayItemSequenceType.setSequenceType(SequenceTypeSupport.XS_DECIMAL);
		                break; 
		            case Keywords.XS_FLOAT :
		            	arrayItemSequenceType.setSequenceType(SequenceTypeSupport.XS_FLOAT);
		               break; 
		            case Keywords.XS_DOUBLE :
		            	arrayItemSequenceType.setSequenceType(SequenceTypeSupport.XS_DOUBLE);
		               break;
		            case Keywords.XS_INTEGER :
		            	arrayItemSequenceType.setSequenceType(SequenceTypeSupport.XS_INTEGER);
		               break;
		            case Keywords.XS_LONG :
		            	arrayItemSequenceType.setSequenceType(SequenceTypeSupport.XS_LONG);
		               break; 
		            case Keywords.XS_INT :
		            	arrayItemSequenceType.setSequenceType(SequenceTypeSupport.XS_INT);
		               break;
		            case Keywords.XS_DATE :
		            	arrayItemSequenceType.setSequenceType(SequenceTypeSupport.XS_DATE);
		               break;
		            case Keywords.XS_DATETIME :
		            	arrayItemSequenceType.setSequenceType(SequenceTypeSupport.XS_DATETIME);
		               break;
		            case Keywords.XS_DURATION :
		            	arrayItemSequenceType.setSequenceType(SequenceTypeSupport.XS_DURATION);
		               break;
		            case Keywords.XS_YEAR_MONTH_DURATION :
		            	arrayItemSequenceType.setSequenceType(SequenceTypeSupport.XS_YEARMONTH_DURATION);
		               break;
		            case Keywords.XS_DAY_TIME_DURATION :
		            	arrayItemSequenceType.setSequenceType(SequenceTypeSupport.XS_DAYTIME_DURATION);
		               break;
		            case Keywords.XS_TIME :
		            	arrayItemSequenceType.setSequenceType(SequenceTypeSupport.XS_TIME);
		               break;
		            case Keywords.XS_ANY_URI :
		            	arrayItemSequenceType.setSequenceType(SequenceTypeSupport.XS_ANY_URI);
		               break;
		            case Keywords.XS_QNAME :
		            	arrayItemSequenceType.setSequenceType(SequenceTypeSupport.XS_QNAME);
		               break;
		            case Keywords.XS_ANY_ATOMIC_TYPE :
		            	arrayItemSequenceType.setSequenceType(SequenceTypeSupport.XS_ANY_ATOMIC_TYPE);
		               break;
		            case Keywords.XS_UNTYPED_ATOMIC :
		            	arrayItemSequenceType.setSequenceType(SequenceTypeSupport.XS_UNTYPED_ATOMIC);
		               break;
		            case Keywords.XS_UNTYPED :
		            	arrayItemSequenceType.setSequenceType(SequenceTypeSupport.XS_UNTYPED);
		               break;
		            default :
		               throw new javax.xml.transform.TransformerException("XPST0051 : An XML Schema type 'xs:" + m_token + "' is not "
	                                                                                          + "recognized, within the provided sequence type expression.");        
	           }
			 
			   nextToken();
		     }
		 }
		  
		 consumeExpected(')');
		  
		 sequenceTypeArrayTest.setArrayItemTypeInfo(arrayItemSequenceType);  	    	  
		 xpathSequenceTypeExpr.setSequenceTypeArrayTest(sequenceTypeArrayTest); 
	  }
  }

  /**
   * Parse XPath 'map' sequence type expressions.
   */
  private void parseXdmMapSequenceType(XPathSequenceTypeExpr xpathSequenceTypeExpr) throws TransformerException {
	  
	  SequenceTypeMapTest sequenceTypeMapTest = null;
	  
	  if (lookahead('(', 1) && lookahead('*', 2) && lookahead(')', 3)) {
		  // handles MapTest of variety AnyMapTest
		  sequenceTypeMapTest = new SequenceTypeMapTest();
		  sequenceTypeMapTest.setIsAnyMapTest(true);
		  nextToken();
		  consumeExpected('(');
	      consumeExpected('*');
	      consumeExpected(')');
	      xpathSequenceTypeExpr.setSequenceTypeMapTest(sequenceTypeMapTest);
	  }
	  else {
		  // handles MapTest of variety TypedMapTest
		  sequenceTypeMapTest = new SequenceTypeMapTest();
		  nextToken();
		  consumeExpected('(');
		  
		  SequenceTypeData keySequenceTypeData = new SequenceTypeData();    		  
		  SequenceTypeData valueSequenceTypeData = new SequenceTypeData();
		  
		  while ((m_token != null) && !tokenIs(',')) {
			 if (tokenIs(XMLConstants.W3C_XML_SCHEMA_NS_URI)) {
			    consumeExpected(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			    consumeExpected(':');
			    
			    switch (m_token) {
		            case Keywords.FUNC_BOOLEAN_STRING :
		            	keySequenceTypeData.setSequenceType(SequenceTypeSupport.BOOLEAN);
		               break;
		            case Keywords.XS_STRING :
		            	keySequenceTypeData.setSequenceType(SequenceTypeSupport.STRING);
		               break; 
		            case Keywords.XS_NORMALIZED_STRING :
		            	keySequenceTypeData.setSequenceType(SequenceTypeSupport.XS_NORMALIZED_STRING);
		                break;
		            case Keywords.XS_TOKEN :
		            	keySequenceTypeData.setSequenceType(SequenceTypeSupport.XS_TOKEN);
		                break;
		            case Keywords.XS_DECIMAL :
		            	keySequenceTypeData.setSequenceType(SequenceTypeSupport.XS_DECIMAL);
		                break; 
		            case Keywords.XS_FLOAT :
		            	keySequenceTypeData.setSequenceType(SequenceTypeSupport.XS_FLOAT);
		               break; 
		            case Keywords.XS_DOUBLE :
		            	keySequenceTypeData.setSequenceType(SequenceTypeSupport.XS_DOUBLE);
		               break;
		            case Keywords.XS_INTEGER :
		            	keySequenceTypeData.setSequenceType(SequenceTypeSupport.XS_INTEGER);
		               break;
		            case Keywords.XS_LONG :
		            	keySequenceTypeData.setSequenceType(SequenceTypeSupport.XS_LONG);
		               break; 
		            case Keywords.XS_INT :
		            	keySequenceTypeData.setSequenceType(SequenceTypeSupport.XS_INT);
		               break;
		            case Keywords.XS_DATE :
		            	keySequenceTypeData.setSequenceType(SequenceTypeSupport.XS_DATE);
		               break;
		            case Keywords.XS_DATETIME :
		            	keySequenceTypeData.setSequenceType(SequenceTypeSupport.XS_DATETIME);
		               break;
		            case Keywords.XS_DURATION :
		            	keySequenceTypeData.setSequenceType(SequenceTypeSupport.XS_DURATION);
		               break;
		            case Keywords.XS_YEAR_MONTH_DURATION :
		            	keySequenceTypeData.setSequenceType(SequenceTypeSupport.XS_YEARMONTH_DURATION);
		               break;
		            case Keywords.XS_DAY_TIME_DURATION :
		            	keySequenceTypeData.setSequenceType(SequenceTypeSupport.XS_DAYTIME_DURATION);
		               break;
		            case Keywords.XS_TIME :
		            	keySequenceTypeData.setSequenceType(SequenceTypeSupport.XS_TIME);
		               break;
		            case Keywords.XS_ANY_URI :
		            	keySequenceTypeData.setSequenceType(SequenceTypeSupport.XS_ANY_URI);
		               break;
		            case Keywords.XS_QNAME :
		            	keySequenceTypeData.setSequenceType(SequenceTypeSupport.XS_QNAME);
		               break;
		            case Keywords.XS_ANY_ATOMIC_TYPE :
		            	keySequenceTypeData.setSequenceType(SequenceTypeSupport.XS_ANY_ATOMIC_TYPE);
		               break;
		            case Keywords.XS_UNTYPED_ATOMIC :
		            	keySequenceTypeData.setSequenceType(SequenceTypeSupport.XS_UNTYPED_ATOMIC);
		               break;
		            case Keywords.XS_UNTYPED :
		            	keySequenceTypeData.setSequenceType(SequenceTypeSupport.XS_UNTYPED);
		               break;
		            default :
		               throw new javax.xml.transform.TransformerException("XPST0051 : An XML Schema type 'xs:" + m_token + "' is not "
	                                                                                          + "recognized, within the provided sequence type expression.");        
	         }
			 
			 nextToken();
		   }
		}
		  
		consumeExpected(',');
		  
		while ((m_token != null) && !tokenIs(')')) {
			 if (tokenIs(XMLConstants.W3C_XML_SCHEMA_NS_URI)) {
			    consumeExpected(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			    consumeExpected(':');
			    
			    switch (m_token) {
		            case Keywords.FUNC_BOOLEAN_STRING :
		               valueSequenceTypeData.setSequenceType(SequenceTypeSupport.BOOLEAN);
		               break;
		            case Keywords.XS_STRING :
		            	valueSequenceTypeData.setSequenceType(SequenceTypeSupport.STRING);
		               break; 
		            case Keywords.XS_NORMALIZED_STRING :
		            	valueSequenceTypeData.setSequenceType(SequenceTypeSupport.XS_NORMALIZED_STRING);
		                break;
		            case Keywords.XS_TOKEN :
		            	valueSequenceTypeData.setSequenceType(SequenceTypeSupport.XS_TOKEN);
		                break;
		            case Keywords.XS_DECIMAL :
		            	valueSequenceTypeData.setSequenceType(SequenceTypeSupport.XS_DECIMAL);
		                break; 
		            case Keywords.XS_FLOAT :
		            	valueSequenceTypeData.setSequenceType(SequenceTypeSupport.XS_FLOAT);
		               break; 
		            case Keywords.XS_DOUBLE :
		            	valueSequenceTypeData.setSequenceType(SequenceTypeSupport.XS_DOUBLE);
		               break;
		            case Keywords.XS_INTEGER :
		            	valueSequenceTypeData.setSequenceType(SequenceTypeSupport.XS_INTEGER);
		               break;
		            case Keywords.XS_LONG :
		            	valueSequenceTypeData.setSequenceType(SequenceTypeSupport.XS_LONG);
		               break; 
		            case Keywords.XS_INT :
		            	valueSequenceTypeData.setSequenceType(SequenceTypeSupport.XS_INT);
		               break;
		            case Keywords.XS_DATE :
		            	valueSequenceTypeData.setSequenceType(SequenceTypeSupport.XS_DATE);
		               break;
		            case Keywords.XS_DATETIME :
		            	valueSequenceTypeData.setSequenceType(SequenceTypeSupport.XS_DATETIME);
		               break;
		            case Keywords.XS_DURATION :
		            	valueSequenceTypeData.setSequenceType(SequenceTypeSupport.XS_DURATION);
		               break;
		            case Keywords.XS_YEAR_MONTH_DURATION :
		            	valueSequenceTypeData.setSequenceType(SequenceTypeSupport.XS_YEARMONTH_DURATION);
		               break;
		            case Keywords.XS_DAY_TIME_DURATION :
		            	valueSequenceTypeData.setSequenceType(SequenceTypeSupport.XS_DAYTIME_DURATION);
		               break;
		            case Keywords.XS_TIME :
		            	valueSequenceTypeData.setSequenceType(SequenceTypeSupport.XS_TIME);
		               break;
		            case Keywords.XS_ANY_URI :
		            	valueSequenceTypeData.setSequenceType(SequenceTypeSupport.XS_ANY_URI);
		               break;
		            case Keywords.XS_QNAME :
		            	valueSequenceTypeData.setSequenceType(SequenceTypeSupport.XS_QNAME);
		               break;
		            case Keywords.XS_ANY_ATOMIC_TYPE :
		            	valueSequenceTypeData.setSequenceType(SequenceTypeSupport.XS_ANY_ATOMIC_TYPE);
		               break;
		            case Keywords.XS_UNTYPED_ATOMIC :
		            	valueSequenceTypeData.setSequenceType(SequenceTypeSupport.XS_UNTYPED_ATOMIC);
		               break;
		            case Keywords.XS_UNTYPED :
		            	valueSequenceTypeData.setSequenceType(SequenceTypeSupport.XS_UNTYPED);
		               break;
		            default :
		               throw new javax.xml.transform.TransformerException("XPST0051 : An XML Schema type 'xs:" + m_token + "' is not "
	                                                                                          + "recognized, within the provided sequence type expression.");        
	         }    	             	         
	         
	         nextToken();     	         
	         consumeExpected(')');
		   }     			      		   
		}
		
		sequenceTypeMapTest.setKeySequenceTypeData(keySequenceTypeData);
		sequenceTypeMapTest.setValueSequenceTypeData(valueSequenceTypeData);
		  
		xpathSequenceTypeExpr.setSequenceTypeMapTest(sequenceTypeMapTest);
	 }
  }

  /**
   * Parse XPath 'function item' sequence type expressions.
   */
  private void parseFunctionItemSequenceType(XPathSequenceTypeExpr xpathSequenceTypeExpr) throws TransformerException {
	
	  SequenceTypeFunctionTest sequenceTypeFunctionTest = null;
	  
	  if (lookahead('(', 1) && lookahead('*', 2) && lookahead(')', 3)) {
		  // handles FunctionTest of variety AnyFunctionTest
		  sequenceTypeFunctionTest = new SequenceTypeFunctionTest();
		  sequenceTypeFunctionTest.setIsAnyFunctionTest(true);
		  nextToken();
		  consumeExpected('(');
	      consumeExpected('*');
	      consumeExpected(')');
	      xpathSequenceTypeExpr.setSequenceTypeFunctionTest(sequenceTypeFunctionTest);
	  }
	  else {
		  // handles FunctionTest of variety TypedFunctionTest
		  sequenceTypeFunctionTest = new SequenceTypeFunctionTest();
		  nextToken();
		  consumeExpected('(');
		  List<String> typedFunctionTestPrefixList = new ArrayList<String>();    		  
		  if (!lookahead(')', 1)) {
			 // There's at-least one parameter definition, for TypedFunctionTest
			 String typedFunctionTestPrefixPart = "";
			 while (m_token != null) {
				if (!(tokenIs(',') || tokenIs(')'))) {
				   typedFunctionTestPrefixPart = typedFunctionTestPrefixPart + m_token;
				   nextToken();
				}
				else if (tokenIs(',')) {
				   if (!isStrHasBalancedParentheses(typedFunctionTestPrefixPart, '(', ')')) {
				      typedFunctionTestPrefixPart = typedFunctionTestPrefixPart + m_token;
				      nextToken();
				   }
				   else {
					  typedFunctionTestPrefixList.add(typedFunctionTestPrefixPart);
					  typedFunctionTestPrefixPart = "";
					  if (tokenIs(')')) {
						 break;  
					  }
					  else {
					     nextToken();
					  }
				   }
				}
				else if (tokenIs(')')) {
				   if (lookahead(',', 1) || lookahead(')', 1) || lookahead('*', 1) || 
						                    lookahead('+', 1) || lookahead('?', 1)) {
					  typedFunctionTestPrefixPart = typedFunctionTestPrefixPart + m_token;
					  nextToken(); 
				   }
				   else {
					  typedFunctionTestPrefixList.add(typedFunctionTestPrefixPart);
	 				  typedFunctionTestPrefixPart = "";
	 				  if (tokenIs(')')) {
					     break;  
				      }
				      else {
				         nextToken();
				      }
				   }
				}
			 }
		  }
		  
		  sequenceTypeFunctionTest.setTypedFunctionTestPrefixList(typedFunctionTestPrefixList);
		  
		  consumeExpected(')');
		  consumeExpected("as");
		  
		  String typedFunctionTestReturnType = "";
		  while (m_token != null) {
			 typedFunctionTestReturnType = typedFunctionTestReturnType + m_token;
			 nextToken();
		  }
		  
		  if (!"".equals(typedFunctionTestReturnType)) {
			 sequenceTypeFunctionTest.setTypedFunctionTestReturnType(typedFunctionTestReturnType);  
		  }
		  
		  xpathSequenceTypeExpr.setSequenceTypeFunctionTest(sequenceTypeFunctionTest);
	  }	  
   }
  
   public String getArrowOpRemainingXPathExprStr() {
	   return fArrowOpRemainingXPathExprStr; 
   }
  
}
