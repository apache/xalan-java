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
package org.apache.xpath.compiler;

import java.io.StringReader;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.XMLConstants;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.templates.Constants;
import org.apache.xalan.templates.ElemApplyTemplates;
import org.apache.xalan.templates.ElemAttribute;
import org.apache.xalan.templates.ElemChoose;
import org.apache.xalan.templates.ElemComment;
import org.apache.xalan.templates.ElemCopy;
import org.apache.xalan.templates.ElemCopyOf;
import org.apache.xalan.templates.ElemElement;
import org.apache.xalan.templates.ElemForEach;
import org.apache.xalan.templates.ElemForEachGroup;
import org.apache.xalan.templates.ElemFunction;
import org.apache.xalan.templates.ElemIf;
import org.apache.xalan.templates.ElemIterate;
import org.apache.xalan.templates.ElemLiteralResult;
import org.apache.xalan.templates.ElemNumber;
import org.apache.xalan.templates.ElemOtherwise;
import org.apache.xalan.templates.ElemPI;
import org.apache.xalan.templates.ElemSequence;
import org.apache.xalan.templates.ElemTemplate;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.ElemText;
import org.apache.xalan.templates.ElemTextLiteral;
import org.apache.xalan.templates.ElemValueOf;
import org.apache.xalan.templates.ElemVariable;
import org.apache.xalan.templates.ElemWhen;
import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xalan.xslt.util.StringUtil;
import org.apache.xalan.xslt.util.XslTransformData;
import org.apache.xerces.dom.DOMInputImpl;
import org.apache.xerces.impl.xs.XSLoaderImpl;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xml.utils.ObjectVector;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionNode;
import org.apache.xpath.XPathProcessorException;
import org.apache.xpath.composite.ForQuantifiedExprVarBinding;
import org.apache.xpath.composite.LetExprVarBinding;
import org.apache.xpath.composite.SequenceTypeArrayTest;
import org.apache.xpath.composite.SequenceTypeData;
import org.apache.xpath.composite.SequenceTypeFunctionTest;
import org.apache.xpath.composite.SequenceTypeKindTest;
import org.apache.xpath.composite.SequenceTypeMapTest;
import org.apache.xpath.composite.SequenceTypeSupport;
import org.apache.xpath.composite.XPath3ExprSingleComparison;
import org.apache.xpath.composite.XPathArrayComparison;
import org.apache.xpath.composite.XPathArrayConstructor;
import org.apache.xpath.composite.XPathContextItemWithPredicate;
import org.apache.xpath.composite.XPathExprFuncCallExtendedArg;
import org.apache.xpath.composite.XPathExprFunctionCallSuffix;
import org.apache.xpath.composite.XPathExprFunctionSuffix;
import org.apache.xpath.composite.XPathForExpr;
import org.apache.xpath.composite.XPathIfExpr;
import org.apache.xpath.composite.XPathLetExpr;
import org.apache.xpath.composite.XPathMapConstructor;
import org.apache.xpath.composite.XPathNamedFunctionReference;
import org.apache.xpath.composite.XPathQuantifiedExpr;
import org.apache.xpath.composite.XPathSequenceConstructor;
import org.apache.xpath.composite.XPathSequenceTypeExpr;
import org.apache.xpath.domapi.XPathStylesheetDOM3Exception;
import org.apache.xpath.functions.FuncArgPlaceholder;
import org.apache.xpath.functions.XPathDynamicFunctionCall;
import org.apache.xpath.functions.XSL3FunctionService;
import org.apache.xpath.functions.XSLFunctionBuilder;
import org.apache.xpath.objects.InlineFunctionParameter;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XPathInlineFunction;
import org.apache.xpath.objects.XString;
import org.apache.xpath.res.XPATHErrorResources;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

import xml.xpath31.processor.types.XSDecimal;
import xml.xpath31.processor.types.XSDouble;
import xml.xpath31.processor.types.XSInteger;

/**
 * Tokenizes and parses XPath expressions.
 * 
 * Ref: https://www.w3.org/TR/xpath-31/
 *      (please also refer to section 'A XPath 3.1 Grammar', within
 *       XPath 3.1 spec)
 * 
 * @author Scott Boag <scott_boag@us.ibm.com>
 * @author Myriam Midy <mmidy@apache.org>
 * @author Joseph Kesselman <jkesselm@apache.org>, Ilene Seelemann, Yash Talwar, 
 *         Henry Zongaro <zongaro@ca.ibm.com>, Christine Li <jycli@apache.org>
 *         
 * @author Mukul Gandhi <mukulg@apache.org>
 *         (XPath 3.1 specific changes, to this class)
 * 
 * @xsl.usage general
 */
public class XPathParser
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
   * Results from checking FilterExpr syntax.
   */
  protected final static int FILTER_MATCH_FAILED     = 0;
  protected final static int FILTER_MATCH_PRIMARY    = 1;
  protected final static int FILTER_MATCH_PREDICATES = 2;
  
  /**
   * With XPath parse of certain expressions, we use this constant string 
   * array to make parse decisions. The elements of this array are certain 
   * XPath language key words and symbols that need this support.
   */
  private static final String[] XPATH_OP_ARR = new String[] 
                                                     { "div", "or", "and", "mod", "to", 
                                                      "eq", "ne", "lt", "gt", "le", "ge", 
                                                      "for", "in", "return", "if", "then", 
                                                      "else", "some", "every", "satisfies", 
                                                      "let", ":=", "-", "||", "=>", "instance", 
                                                      "of", "as", "idiv" };
  
  /**
   * When an XPath expression is () (i.e, representing an xdm empty sequence),
   * we translate that within this XPath parser implementation, to an XPath
   * range "to" expression using this class field (this equivalently produces
   * an xdm empty sequence). There may be other direct ways of implementing this.
   */
  // REVISIT
  public static final String XPATH_EXPR_STR_EMPTY_SEQUENCE = "1 to 0";
  
  private static final List<String> XPATH_OP_ARR_TOKENS_LIST = Arrays.asList(XPATH_OP_ARR);
  
  private boolean m_dynamicFunctionCallArgumentMarker = false;
  
  private boolean m_isXPathPredicateParsingActive = false;
  
  private boolean m_isXPathExprBeginParse = false;
  
  private boolean m_isSequenceTypeXPathExpr = false;
  
  static XPathInlineFunction m_xpath_inlineFunction = null;
    
  static int m_xpathDynFuncCallProcessedCount = 0;
  static List<XPathDynamicFunctionCall> m_xpathDynamicFunctionCallList = new ArrayList<XPathDynamicFunctionCall>();  
  
  static List<XPathForExpr> m_forExprList = new ArrayList<XPathForExpr>();
  
  static XPathLetExpr m_letExpr = null;
  
  static XPathQuantifiedExpr m_quantifiedExpr = null;
  
  static XPathIfExpr m_ifExpr = null;
  
  static XPathSequenceConstructor m_xpathSequenceConstructor = null;
  
  static XPathArrayConstructor m_xpathArrayConstructor = null;
  
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
  
  static XPathArrayConsFuncArgs m_xpathArrayConsFuncArgs = null;
  
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
  
  static XPathSequenceConsFuncArgs m_xpathSequenceConsFuncArgs = null;
  
  static XPathMapConstructor m_xpathMapConstructor = null;
  
  static XPathSequenceTypeExpr m_xpathSequenceTypeExpr = null;
  
  static XPathNamedFunctionReference m_xpathNamedFunctionReference = null;
  
  static XPathExprFunctionCallSuffix m_xpathExprWithFuncCallSuffix = null;
  
  static XPathContextItemWithPredicate m_xpathContextItemWithPredicate = null;
  
  static XPathExprFuncCallExtendedArg m_xpathExprFuncCallExtendedArg = null;
  
  static XPathArrayComparison m_xpathArrayComparison = null;
  
  static XPath3ExprSingleComparison m_xpath3ExprSingleComparison = null;
  
  static List<FuncArgPlaceholder> m_funcArgPlaceHolderList = new ArrayList<FuncArgPlaceholder>();
  
  private String m_arrowOpRemainingXPathExprStr = null;
  
  private XPathExprFunctionSuffix m_xpathExprFunctionSuffix = null;
  
  private boolean m_isFunctionArgumentParse;
  
  private static XSL3FunctionService xslFunctionService = XSLFunctionBuilder.getXSLFunctionService();
  
  private static final String STRING_CLASSCAST_ERROR_MESSAGE = "org.apache.xpath.objects.XString cannot be cast to java.lang.String";
  
  private boolean m_isParseSequenceTypeExprWithUserDefinedType = false;
  
  private boolean m_isSequenceOperand = false;
  
  private TokenQueueScanPosition m_prevTokQueueScanPosition = null;
  
  private String m_xpathDefaultNamespace = null;
  
  private boolean m_op_group_parse = false;

  
  /**
   * The parser constructor.
   */
  public XPathParser(ErrorListener errorListener, javax.xml.transform.SourceLocator sourceLocator)
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
   *                                expression string assuming that it represents an XPath
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
    
    m_isSequenceTypeXPathExpr = isSequenceTypeXPathExpr;
    
    m_xpathArrayConsFuncArgs = new XPathArrayConsFuncArgs();
    
    m_xpathSequenceConsFuncArgs = new XPathSequenceConsFuncArgs();

    Lexer lexer = new Lexer(compiler, namespaceContext, this);
    
    // Remove XPath expression comments from the supplied expression,
    // before parse of an XPath expression.
    if (StringUtil.isStrHasBalancedXPathCommentDelim(expression)) {
       expression = expression.replaceAll("\\(:.*:\\)", "");
    }
    else {
       error(XPATHErrorResources.ER_UNCLOSED_XPATH_COMMENT, new Object[]{});
    }

    lexer.tokenize(expression);
    
    if (lexer.isNsBindingRequired() && !lexer.isNsBound()) {
       String nsUnboundPrefix = lexer.getNsUnboundPrefix();
       if (nsUnboundPrefix != null) {
          error(XPATHErrorResources.ER_NS_BINDING, new Object[]{ nsUnboundPrefix });
       }
    }

    m_ops.setOp(0,OpCodes.OP_XPATH);
    m_ops.setOp(OpMap.MAPINDEX_LENGTH,2);
        	
	try {

      nextToken();
      
      m_isXPathExprBeginParse = true;
      
      Expr();

      if (null != m_token)
      {    	
    	// Retry XPath expression parse once again by parenthesizing the 
    	// original XPath expression.
    	
        boolean isTrySecondTime = true;
        
        String newExpression = null;
        
        if (!(expression.startsWith("(") && expression.endsWith(")"))) {
        	String[] strArr = expression.split(",");
        	if (strArr.length > 1) {
        	   boolean isXPathExprStrHasBalancedParens = true;
        	   for (int idx = 0; idx < strArr.length; idx++) {
        		  String str1 = strArr[idx];
        		  if (!StringUtil.isStrHasBalancedParentheses(str1, '(', ')')) {
        			  isXPathExprStrHasBalancedParens = false; 
        		  }
        	   }
        	           	   
        	   if (isXPathExprStrHasBalancedParens || tokenIs(',')) {
        		   /**
        		    * We check for the token string "," as well, since
        		    * an XPath parse might have finished without completing
        		    * parse of all sequence items if it was an XPath literal
        		    * sequence constructor parse.
        		    */
        		   newExpression = "(" + expression + ")"; 
        	   }
        	}
        }
  	
        if (newExpression != null) {
        	(m_ops.m_tokenQueue).removeAllElements();    	
        	lexer = new Lexer(compiler, namespaceContext, this);
        	lexer.tokenize(newExpression);

        	m_ops.setOp(0,OpCodes.OP_XPATH);
        	m_ops.setOp(OpMap.MAPINDEX_LENGTH,2);                

        	try {
        		nextToken();

        		m_isXPathExprBeginParse = true;

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
        					new Object[]{ extraTokens });
        		}
        	}
        	catch (org.apache.xpath.XPathProcessorException e)
        	{
        		if(CONTINUE_AFTER_FATAL_ERROR.equals(e.getMessage()))
        		{
        			initXPath(compiler, "/..",  namespaceContext, false);
        		}
        		else
        			throw e;
        	}
        }
        
        String extraTokens = "";

        while (null != m_token)
        {
          extraTokens += "'" + m_token + "'";

          nextToken();

          if (null != m_token)
            extraTokens += ", ";
        }

        if (!isTrySecondTime) {
	        error(XPATHErrorResources.ER_EXTRA_ILLEGAL_TOKENS,
	              new Object[]{ extraTokens });
        }
      }

    } 
    catch (org.apache.xpath.XPathProcessorException e)
    {
	  if(CONTINUE_AFTER_FATAL_ERROR.equals(e.getMessage()))
	  {
		 initXPath(compiler, "/..",  namespaceContext, false);
	  }
	  else
		 throw e;
    }

    compiler.shrink();
  }

  /**
   * Given an string, init an XPath object for pattern matches,
   * in order that a parse doesn't have to be done each time 
   * the expression is evaluated.
   * 
   * @param compiler 							The XPath object to be initialized
   * @param expression 							An XPath expression string
   * @param namespaceContext 					An object that is able to resolve prefixes in
   *                                            the XPath to namespaces.
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
    
    m_expression = expression;

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
            new Object[]{ extraTokens });
    }

    m_ops.setOp(m_ops.getOp(OpMap.MAPINDEX_LENGTH), OpCodes.ENDOP);
    m_ops.setOp(OpMap.MAPINDEX_LENGTH, m_ops.getOp(OpMap.MAPINDEX_LENGTH)+1);

    m_ops.shrink();
  }
  
  /**
   * Given an string, init an XPath object for pattern matches,
   * in order that a parse doesn't have to be done each time 
   * the expression is evaluated.
   * 
   * @param compiler                            The XPath object to be initialized
   * @param expression                          An XPath expression string
   * @param namespaceContext                    An object that is able to resolve prefixes in
   *                                            the XPath to namespaces.
   * @param xpathDefaultNamespace               Non-null value of XSL transformation 
   *                                            xpath-default-namespace.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public void initMatchPattern(Compiler compiler, String expression, PrefixResolver namespaceContext,
			                                                                     String xpathDefaultNamespace) throws javax.xml.transform.TransformerException {
	  m_ops = compiler;
	  m_namespaceContext = namespaceContext;
	  m_xpathDefaultNamespace = xpathDefaultNamespace;
	  
	  m_expression = expression;
	  	  
	  m_functionTable = compiler.getFunctionTable();

	  Lexer lexer = new Lexer(compiler, namespaceContext, this);

	  lexer.tokenize(expression);

	  m_ops.setOp(0, OpCodes.OP_MATCHPATTERN);
	  m_ops.setOp(OpMap.MAPINDEX_LENGTH, 2);

	  nextToken();
	  Pattern();
	  
	  if ((m_xpathDefaultNamespace != null) && (m_expression.startsWith("/")) && (m_queueMark == 2)) {
		  nextToken();
		  nextToken();
		  nextToken();
	  }

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
				  new Object[]{ extraTokens });
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
   * An XPath expression string.
   */
  private String m_expression;

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
              if (XPATH_OP_ARR_TOKENS_LIST.contains(xpathExprStrPart)) {
                 isXpathInlineFunctionOpToken = true;   
              }
          }
          
          funcBodyXPathExprStrBuff.append(isXpathInlineFunctionOpToken ? " " + xpathExprStrPart + 
                                                                                        " " : xpathExprStrPart);
      }
      
      String strResult = funcBodyXPathExprStrBuff.toString();
      
      return strResult.trim();
  }
  
  /**
   * While traversing XPath expression's token list, we determine within this method,
   * whether the parse of dynamic function call argument details should continue or
   * not. We simultaneously accumulate function call argument details within a list 
   * 'argDetailsStrPartsList' (that has been passed as an argument to this method) 
   * whose contents are accessible to the caller of this method.
   */
  private boolean isXPathDynamicFuncCallParseAhead(List<String> argDetailsStrPartsList, 
		                                           String delim) throws TransformerException {
      boolean isXpathDynamicFuncCallParseAhead = false;
      
      String prevToken = getTokenRelative(-2);
      
      if (tokenIs('(') && (prevToken.equals("(") || prevToken.equals(","))) {
     	 // XPath parse of literal sequence
    	  
     	 StringBuffer strBuff = new StringBuffer();
     	 strBuff.append(m_token);    	 
     	 consumeExpected('(');
     	 while (m_token != null) {
     	    if (tokenIs(')')) {
     		   strBuff.append(m_token);
     		   argDetailsStrPartsList.add(strBuff.toString());
     		   consumeExpected(')');
     		   if (tokenIs(',')) {
     			  argDetailsStrPartsList.add(delim);
     			  nextToken();
     		      m_dynamicFunctionCallArgumentMarker = false;
     		      isXpathDynamicFuncCallParseAhead = true;
     		   }
     		   break;
     	    }
     	    else {
     		   strBuff.append(m_token);
     		   nextToken();
     	    }
     	 }
      }
      else if (tokenIs('[') && (prevToken.equals("(") || prevToken.equals(","))) {
     	 // XPath parse of literal array
    	  
     	 StringBuffer strBuff = new StringBuffer();
     	 strBuff.append(m_token);    	 
     	 consumeExpected('[');
     	 while (m_token != null) {
     	    if (tokenIs(']')) {
     		   strBuff.append(m_token);
     		   argDetailsStrPartsList.add(strBuff.toString());
     		   consumeExpected(']');
     		   if (tokenIs(',')) {
     			  argDetailsStrPartsList.add(delim);
     			  nextToken();
     		      m_dynamicFunctionCallArgumentMarker = false;
     		      isXpathDynamicFuncCallParseAhead = true;
     		   }    		   
     		   break;
     	    }
     	    else {
     		   strBuff.append(m_token);
     		   nextToken();
     	    }
     	 }
      }
      else if (lookahead('(', 1)) {
         // The function call argument is itself another function call
         m_dynamicFunctionCallArgumentMarker = true;
         argDetailsStrPartsList.add(m_token);
         nextToken();
         isXpathDynamicFuncCallParseAhead = true;
      }
      else if (tokenIs(')') && lookahead(',', 1)) {
         argDetailsStrPartsList.add(m_token);          
         nextToken();
         argDetailsStrPartsList.add(delim);
         nextToken();
         m_dynamicFunctionCallArgumentMarker = false;
         isXpathDynamicFuncCallParseAhead = true;
      }
      else if (lookahead(',', 1)) {
         argDetailsStrPartsList.add(m_token);
         nextToken();
         if (!m_dynamicFunctionCallArgumentMarker) {
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
         if (!StringUtil.isStrHasBalancedParentheses(argDetailsAccumulatedStrSoFar, '(', ')')) {
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
   * XPath parse of sequence type expressions.
   */
  private void setSequenceTypeOccurenceIndicator(XPathSequenceTypeExpr xpathSequenceTypeExpr, 
                                                 boolean isXPathInlineFunctionParse) throws TransformerException {
      if (tokenIs(SequenceTypeSupport.Q_MARK)) {
         xpathSequenceTypeExpr.setItemTypeOccurrenceIndicator(SequenceTypeSupport.
                                                                                 OccurrenceIndicator.ZERO_OR_ONE);
         nextToken();
      }
      else if (tokenIs(SequenceTypeSupport.STAR)) {
         xpathSequenceTypeExpr.setItemTypeOccurrenceIndicator(SequenceTypeSupport.
                                                                                 OccurrenceIndicator.ZERO_OR_MANY);
         nextToken();
      }
      else if (tokenIs(SequenceTypeSupport.PLUS)) {
         xpathSequenceTypeExpr.setItemTypeOccurrenceIndicator(SequenceTypeSupport.
                                                                                 OccurrenceIndicator.ONE_OR_MANY);
         nextToken();
      }
      else if ((m_token != null) && !isXPathInlineFunctionParse && !m_isFunctionArgumentParse) {
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
    * This method supports XPath parse of sequence type expressions.
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
               if (lookahead(null, 1)) {
                   nextToken();                
               }
               else if (lookahead(null, 2)) {
                   nextToken();
                   if (!(m_isXPathPredicateParsingActive && tokenIs(']'))) {
                      setSequenceTypeOccurenceIndicator(xpathSequenceTypeExpr, isInlineFunction);
                   }                   
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
           sequenceTypeKindTest.setDataTypeLocalName(nodeDataTypeParts[0]);
           sequenceTypeKindTest.setDataTypeUri(nodeDataTypeParts[1]);
           if (nodeDataTypeParts[1] == null) {
        	  // Check for a possibility, of data type being an user-defined schema type
        	  parseSequenceTypeExprWithUserDefinedType(xpathSequenceTypeExpr, nodeDataTypeParts[0]);
           }
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
    	   id = m_functionTable.getFunctionIdForXPathBuiltinFuncs(key);
    	}
    	else if ((FunctionTable.XPATH_BUILT_IN_MATH_FUNCS_NS_URI).equals(nsUri)) {    	       	   
     	   id = m_functionTable.getFunctionIdForXPathBuiltinMathFuncs(key);
     	}
    	else if ((FunctionTable.XPATH_BUILT_IN_MAP_FUNCS_NS_URI).equals(nsUri)) {    	       	   
    	   id = m_functionTable.getFunctionIdForXPathBuiltinMapFuncs(key);
    	}
    	else if ((FunctionTable.XPATH_BUILT_IN_ARRAY_FUNCS_NS_URI).equals(nsUri)) {     	   
     	  id = m_functionTable.getFunctionIdForXPathBuiltinArrayFuncs(key);
     	}
    	else {
    	  id = m_functionTable.getFunctionId(key);
    	}
      }
      tok = ((Integer)id).intValue();
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
	  
	  if ((m_isXPathExprBeginParse || m_isSequenceOperand) && isLiteralSequenceOrArrayBegin()) {
    	  /**
		   * We consider XPath parse of sequence and array, in similar 
    	   * way. These are lexically different only by virtue of sequence/array 
    	   * expression's left-most and right-most delimiter characters.
		   */
    	  
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
          
          if (isSequenceConstructor && tokenIs(')') && lookahead(null, 1)) {
              // The XPath expression is ()        	  
        	  xpathParseLiteralEmptySequence();
        	  
              return;
          }          
          else if (((isSquareArrayConstructor && tokenIs(']')) || (isCurlyArrayConstructor && tokenIs('}'))) && 
        		  																					lookahead(null, 1)) {
             // The XPath expression is [], or {}        	 
             xpathParseLiteralEmptyArray();
             
             return;             
          }
          
          TokenQueueScanPosition prevTokenQueueScanPos = new TokenQueueScanPosition(m_queueMark, m_tokenChar, m_token);
                    
          if (isSquareArrayConstructor) {
        	  /**
        	   * XPath parse of general comparison (i.e, =, !=, <, <=, >, >=)
        	   * between an xdm array on LHS and another appropriate XPath
        	   * operand on RHS.
        	   */        	  
        	  boolean result = xpathParseLiteralArrayCmp(prevTokenQueueScanPos);        	  
        	  if (result) {
        		 return; 
        	  }
          }
          else if (isSequenceConstructor) {
        	  /**
        	   * XPath parse of general comparison (i.e, =, !=, <, <=, >, >=) between
        	   * an XPath expression (which is either 'for', 'let', 'some', 'every', 'if')
        	   * on LHS and another XPath operand on RHS (like a literal array, literal
        	   * sequence or another appropriate XPath expression).
        	   */
        	  if (tokenIs("for") || tokenIs("let") || tokenIs("some") || tokenIs("every") || tokenIs("if")) {
        		  boolean result = xpathParseExprSingleLiteralSeqCmp(prevTokenQueueScanPos);        	  
        		  if (result) {
        			  return; 
        		  }
        	  }
        	  else {
        		  restoreTokenQueueScanPosition(prevTokenQueueScanPos);
        	  }
          }
        	  
          List<String> seqOrArrayXPathItems = new ArrayList<String>();
          
          while (m_token != null) {                            
              if (tokenIs("function")) {
                 // An XPath expression parse of 'inline function expression'
                 List<String> inlineFuncBodyXPathStrPartsList = new ArrayList<String>();
                 
                 while (!tokenIs('}') && m_token != null) {
                    if (!lookahead(null, 1)) {
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
              else if (tokenIs('[')) {
            	 StringBuffer arrStrBuff = new StringBuffer();
            	 arrStrBuff.append(m_token);
            	 nextToken();
            	 while (!((tokenIs(',') && StringUtil.isStrHasBalancedParentheses(arrStrBuff.toString(), '[',']')) || 
            			                                                                                           (isSequenceConstructor && tokenIs(')')) || 
            			                                                                                           (isSquareArrayConstructor && tokenIs(']')) || 
            			                                                                                            (m_token == null))) {           		 
            		arrStrBuff.append(m_token);
            		nextToken();
            	 }
            	 if (tokenIs(']') && !lookahead(null, 1)) {
            		arrStrBuff.append(m_token);
            		consumeExpected(']');
            	 }
            	 String str1 = arrStrBuff.toString();
            	 seqOrArrayXPathItems.add(str1);
            	 if (tokenIs(',') || (isSequenceConstructor && tokenIs(')')) || 
            			             (isSquareArrayConstructor && tokenIs(']'))) {            		
            	    nextToken();            	    
            	 }
              }              
              else {
                 List<String> xpathExprTokens = new ArrayList<String>();                 
                 
                 if (tokenIs("map")) {
                	 xpathExprTokens.add(m_token);
                	 nextToken();
                	 xpathExprTokens.add(m_token);
                	 nextToken();
                	 while (!tokenIs("}") && (m_token != null)) {
                		if (tokenIs(':')) {
                		   xpathExprTokens.add(" " + m_token + " ");
                		}
                		else {
                		   xpathExprTokens.add(m_token);
                		}
                		nextToken();
                	 }
                	 xpathExprTokens.add(m_token);
                	 nextToken();
                 }
                 else {
                	if ((lookahead('(', 1) || (lookahead(':', 1) && lookahead('(', 3))) || isDfcBegin(m_token)) {
                		// The literal sequence constructor item is an XPath conventional 
                		// function call, or an XPath dynamic function call.
                		while (!(isStrListHasBalancedParentheses(xpathExprTokens, '(', ')') && 
                				                                                          tokenIs(",")) && (m_token != null)) {
                		   if (!lookahead(null, 1)) {
                			  xpathExprTokens.add(m_token);
                		   }
                		   nextToken();
                		}
                	}
                	else {
                		while (!tokenIs(",") && (m_token != null)) {
                			if (!lookahead(null, 1)) {
                				xpathExprTokens.add(m_token);
                			}
                			nextToken();
                		}
                	}
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
          
          /**
           * Handle, XPath literal sequence constructor expression
           * like (a,b)[1] i.e, a literal sequence constructor
           * followed by an index accessor. An index accessor here
           * can be a variable reference as well.
           */
          int listSize = seqOrArrayXPathItems.size();
          
          // For e.g, for the XPath expression (a,b)[p] this 
          // string variable will have value "p".
          String sequencePredicateExpr = null; 
        		  
          if (listSize > 1) {
             String listLastItemStr = seqOrArrayXPathItems.get(listSize - 1);
             int idx1 = listLastItemStr.indexOf(')');
             int idx2 = listLastItemStr.indexOf('[');         	          	 
             if ((idx1 >= 0) && (idx2 >= 0) && (idx2 == (idx1 + 1))) {
            	ObjectVector tokenQueue = m_ops.getTokenQueue();
             	int tokenQueueSize = m_ops.getTokenQueueSize();
             	String tokenQueueLastItemStr = (tokenQueue.elementAt(tokenQueueSize - 1)).toString();             	
            	if ("]".equals(tokenQueueLastItemStr)) {
            	   sequencePredicateExpr = listLastItemStr.substring(idx2 + 1);
            	   listLastItemStr = listLastItemStr.substring(0, idx1);
            	   seqOrArrayXPathItems.set(listSize - 1, listLastItemStr);
            	}
             }
          }
          
          boolean isXPathParseOkToProceed = true;
          
          /*if (isSquareArrayConstructor) {
        	 // Compact the array items
             compactArrayItems(seqOrArrayXPathItems);
	      }*/
	  
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
             
             boolean isStrHasBalancedParentheses = StringUtil.isStrHasBalancedParentheses(
								            		                                                    seqOrArrayMemberXPathExprStr, 
								            		                                                    lParenChar, rParenChar);
             if (!isStrHasBalancedParentheses) {
                isXPathParseOkToProceed = false;
                break;
             }
          }
                    
	      if (isXPathParseOkToProceed && isSequenceConstructor) { 
	    	 if (seqOrArrayXPathItems.size() > 1) {
	    		 // An XPath literal sequence constructor expression can 
	    		 // create an XDM sequence having at-least two values.
	    		 isXPathParseOkToProceed = true; 
	    	 }
	    	 else {
	    		 String xpathExprStr = seqOrArrayXPathItems.get(0);
	    		 if (xpathExprStr.startsWith("for") || xpathExprStr.startsWith("let") || 
	    				                               xpathExprStr.startsWith("some") || 
	    				                               xpathExprStr.startsWith("every") || 
	    				                               xpathExprStr.startsWith("if")) {
	    			 // XPath parse of expressions like (for ...). i.e, XPath 'for' 
	    			 // and other such expressions contained within '(' & ')'.
	    			 isXPathParseOkToProceed = true; 
	    		 }
	    		 else if (seqOrArrayXPathItems.size() == 1) {
	    			 String str1 = seqOrArrayXPathItems.get(0);
	    			 if (!(str1.startsWith("[") && str1.endsWith("]"))) {
	    				isXPathParseOkToProceed = false; 
	    			 }
	    		 }
	    		 else {
	    			 isXPathParseOkToProceed = false;
	    		 }
	    	 }
	      }
	      
	      String xpathPrefixStr = null;
	      String xpathSuffixStr = null;	      
	      boolean trailProcessingReq = false;
	      
	      if (!isXPathParseOkToProceed) {
	    	 // XPath parse of expressions like (p,q)/*, (p,q)/*/a etc 
	    	 
	    	 // Construct an XPath original expression string from token queue
	    	 StringBuffer strBuff2 = new StringBuffer();
	    	 ObjectVector tokenQueue = m_ops.getTokenQueue();
          	 int tokenQueueSize = m_ops.getTokenQueueSize();
          	 for (int idx = 0; idx < tokenQueueSize; idx++) {
          		String str1 = (tokenQueue.elementAt(idx)).toString();
          		strBuff2.append(str1);
          	 }
          	 
          	 String xpathExprStr = strBuff2.toString();
          	 int idx = xpathExprStr.indexOf('/');
          	 if (idx > 0) {
          		xpathPrefixStr = xpathExprStr.substring(0, idx);
          		xpathSuffixStr = xpathExprStr.substring(idx + 1);
          		if ((xpathPrefixStr != null) && xpathPrefixStr.startsWith("(") && xpathPrefixStr.endsWith(")")) {
          		   trailProcessingReq = true;
          		}
          	 }
	      }
	      
	      if (trailProcessingReq && (seqOrArrayXPathItems.size() >= 2)) {
	    	 String lastItemStr = seqOrArrayXPathItems.get(listSize - 1);
	    	 String str1 = lastItemStr.substring(0, lastItemStr.indexOf(')'));
	    	 seqOrArrayXPathItems.set(listSize - 1, str1);	    	 
	    	 isXPathParseOkToProceed = true;
	      }
                             
          if (isXPathParseOkToProceed) {
              int opPos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);
              
              nextToken();
              
              if (isSquareArrayConstructor || isCurlyArrayConstructor) {
            	 insertOp(opPos, 2, OpCodes.OP_ARRAY_CONSTRUCTOR_EXPR);
            	 m_xpathArrayConstructor = new XPathArrayConstructor();            	 
            	 m_xpathArrayConstructor.setArrayConstructorXPathParts(seqOrArrayXPathItems);
              }
              else {
                 insertOp(opPos, 2, OpCodes.OP_SEQUENCE_CONSTRUCTOR_EXPR);                 
                 m_xpathSequenceConstructor = new XPathSequenceConstructor();                 
                 m_xpathSequenceConstructor.setSequenceConstructorXPathParts(seqOrArrayXPathItems);  
                 m_xpathSequenceConstructor.setPredicateExpr(sequencePredicateExpr);
                 m_xpathSequenceConstructor.setXPathSuffixStr(xpathSuffixStr);
              }             
          }
          else {
        	  if (m_isSequenceOperand) {
        		  m_isSequenceOperand = false; 
        	  }
        	  else {        		  
        		  // Re-start XPath parse, reusing the same token queue        		                 	  
               	  m_queueMark = 0;
        		  nextToken();       		  
        		  ExprSingle();
        	  }
          }
      }
	  else if (m_isXPathExprBeginParse && tokenIs("(") && (lookahead("to", 2) || lookahead("to", 3))) {
		  // XPath parse of expression like '(a to b) => function()'
		  
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
	        	 m_arrowOpRemainingXPathExprStr = "";  
	          }
	          
	          while (m_token != null) {
	        	 m_arrowOpRemainingXPathExprStr += m_token;
	        	 nextToken();
	          }
	      }
	  }
	  else if (m_isSequenceTypeXPathExpr) {
	      m_xpathSequenceTypeExpr = SequenceTypeExpr(false); 
	  }
      else if (m_isXPathExprBeginParse && tokenIs("map")) {
    	  // XPath parse of map expression string
    	  
    	  int opPos1 = m_ops.getOp(OpMap.MAPINDEX_LENGTH);
     	  
     	  nextToken();
     	  
     	  insertOp(opPos1, 2, OpCodes.OP_MAP_CONSTRUCTOR_EXPR);

     	  m_xpathMapConstructor = new XPathMapConstructor();
     	  
     	  Map<String, String> nativeMapObj = new HashMap<String, String>();                    

     	  consumeExpected('{');
     	  
     	  if (tokenIs('}')) {
     		 nextToken();
     	  }

     	  while (m_token != null) {
     		  String mapEntryKeyXPathExprStr = null;
     		  if (tokenIs('$') && lookahead(':', 2)) {
     			 mapEntryKeyXPathExprStr = m_token;
     			 nextToken();
     			 mapEntryKeyXPathExprStr += m_token;
    		     nextToken();
    		     consumeExpected(':');
     		  }
     		  else {
     		     mapEntryKeyXPathExprStr = m_token;
     		     nextToken();
     		     consumeExpected(':');
     		  }
     		  
     		  String mapEntryValueXPathExprStr = null;
     		  if (tokenIs("map")) {
     			  // There's likely an XPath map constructor here, 
     			  // within this map. 	        		
     			  mapEntryValueXPathExprStr = getXPathMapConstructorStrValue();
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
     		  else if (tokenIs(XMLConstants.W3C_XML_SCHEMA_NS_URI)) {
     			  mapEntryValueXPathExprStr = m_token;
				  nextToken();
     			  while (!(tokenIs(',') || tokenIs('}'))) {     					  
     				  mapEntryValueXPathExprStr += m_token;
     				  nextToken();
     				  
     				  
     			  } 
     		  }
     		  else {
     			  // The map's key value here is a simple value (i.e, not an array, or 
     			  // map).
     			  if (!tokenIs('(')) {
     				  mapEntryValueXPathExprStr = m_token;
     				  nextToken();
     				  while (!(tokenIs(',') || tokenIs('}'))) {     					  
     					  mapEntryValueXPathExprStr += m_token;
     					  nextToken();
     				  }
     			  }
     			  else {
     				 mapEntryValueXPathExprStr = m_token;
    				 nextToken();
    				 while (!tokenIs(')')) {
    					mapEntryValueXPathExprStr += m_token;    					
    					nextToken(); 
    				 }
    				 mapEntryValueXPathExprStr += m_token;
    				 nextToken();
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

     	  m_xpathMapConstructor.setNativeMap(nativeMapObj);
     	  
     	  m_ops.setOp(opPos1 + OpMap.MAPINDEX_LENGTH,
     			                            m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos1);
    	  
      }
      else if (lookahead(':', 1)) {    	  
    	 // XPath parse of named function reference, for XPath built-in 
    	 // functions & stylesheet functions    	  
    	 
    	 int opPos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);
    	 
    	 handleXPathParseNamedFuncRefWithNSQual(opPos);
	  }
	  else if (!m_token.contains(":") && m_token.contains("#") && xslFunctionService.isFuncArityWellFormed(m_token)) {	    	
	     // XPath parse of named function reference, for XPath built-in functions
		  
	     int opPos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);
	     
	     handleXPathParseNamedFuncRefWithoutNSQual(opPos);
	  }
	  else if (m_isXPathExprBeginParse && tokenIs('.') && lookahead('[', 1)) {		 
         int opPos1 = m_ops.getOp(OpMap.MAPINDEX_LENGTH);     	  
     	 
         nextToken();
     	 
     	 insertOp(opPos1, 2, OpCodes.OP_CONTEXT_ITEM_WITH_PREDICATE);
     	 
		 m_xpathContextItemWithPredicate = new XPathContextItemWithPredicate();
		 
		 consumeExpected('[');
		 StringBuffer xpathPredicateSuffixStrBuff = new StringBuffer();
		 boolean fl1 = false;
		 while (m_token != null) {
			 if (!tokenIs(']')) {
			    xpathPredicateSuffixStrBuff.append(m_token + " ");
			    nextToken();
			 }
			 else {
				fl1 = true;
				consumeExpected(']');
				
			    break;	 
			 }
		 }
		 
		 if (!fl1) {
			 error(XPATHErrorResources.ER_EXPECTED_CLOSING_SQUARE_BRACKET, new Object[]{ }); 
		 }
		 		 		 
		 String xpathPredicateSuffixStr = (xpathPredicateSuffixStrBuff.toString()).trim();
		 // TO DO : To handle other appropriate XML namespace uri's as well
		 int idx = xpathPredicateSuffixStr.indexOf(XMLConstants.W3C_XML_SCHEMA_NS_URI + " :");
         if (idx > -1) {
        	 String prefixStr = xpathPredicateSuffixStr.substring(0, idx);
        	 String suffixStr = xpathPredicateSuffixStr.substring(idx);
        	 suffixStr = suffixStr.replace(" ", "");
        	 xpathPredicateSuffixStr = prefixStr + suffixStr;  
         }
		 
		 m_xpathContextItemWithPredicate.setXPathPredicateSuffixExpr(xpathPredicateSuffixStr);		 
		 m_xpathContextItemWithPredicate.setContextItemChar('.');
		 
		 m_ops.setOp(opPos1 + OpMap.MAPINDEX_LENGTH,
                  								 m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos1);
		 
	  }
	  else if (tokenIs("?")) {
		  // A function argument placeholder, for a function call partial 
		  // function application.

		  int opPos1 = m_ops.getOp(OpMap.MAPINDEX_LENGTH);

		  appendOp(2, OpCodes.OP_FUNC_ARG_PLACEHOLDER);

		  consumeExpected('?');

		  FuncArgPlaceholder funcArgPlaceholder = new FuncArgPlaceholder();
		  m_funcArgPlaceHolderList.add(funcArgPlaceholder);

		  m_ops.setOp(opPos1 + OpMap.MAPINDEX_LENGTH,
				                                  m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos1);
	  }
      else {
         ExprSingle();                  
      }
  }

  /**
   * Method definition, for XPath parse of literal array comparison, 
   * with another xdm value using XPath general comparison operators.
   * 
   * This method, supports XPath parse of expressions like: 
   *   
   *   [...] OP xpath_expr
   *   
   *   OP := '=' | != | '<' | <= | '>' | >= 
   * 
   * @param prevTokenQueueScanPos						The current XPath parse, token 
   *                                                    queue scan state.
   * @return											Boolean result true or false
   * @throws TransformerException
   */
  private boolean xpathParseLiteralArrayCmp(TokenQueueScanPosition prevTokenQueueScanPos) 
		                                                                               throws TransformerException {
	
	  boolean result = false;

	  List<String> arrComparisonXpathLhs = new ArrayList<String>();        	          	          	  

	  StringBuffer arrItemXPathStrBuff = new StringBuffer();
	  while (!(tokenIs(']') || (m_token == null))) {
		  while (!(tokenIs(',') || tokenIs(']') || (m_token == null))) {
			  arrItemXPathStrBuff.append(m_token);
			  nextToken();
		  }

		  if (tokenIs(',')) {
			  nextToken();
			  arrComparisonXpathLhs.add(arrItemXPathStrBuff.toString());
			  arrItemXPathStrBuff = new StringBuffer();        			  
			  continue;
		  }

		  if (tokenIs(']')) {
			  nextToken();
			  arrComparisonXpathLhs.add(arrItemXPathStrBuff.toString());        			  
			  break; 
		  }        		  
	  }

	  if (m_token != null) {
		  m_xpathArrayComparison = new XPathArrayComparison();        		         		 

		  boolean isXPathGeneralCmp = isXPathParseRelnGeneralCmpRhs(m_xpathArrayComparison);

		  if (isXPathGeneralCmp) {            		 
			  List<String> xpathExprPartsRhs = xpathParseGeneralComparisonRhs();

			  m_xpathArrayComparison.setArrayConstructorXPathLhs(arrComparisonXpathLhs);
			  m_xpathArrayComparison.setSeqArrConstructorXPathRhs(xpathExprPartsRhs);

			  int opPos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);

			  insertOp(opPos, 2, OpCodes.OP_ARRAY_COMPARISON);

			  m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH, 
					  m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);

			  result = true;
		  }
		  else {            		 
			  restoreTokenQueueScanPosition(prevTokenQueueScanPos);
		  }
	  }
	  else {        		 
		  restoreTokenQueueScanPosition(prevTokenQueueScanPos); 
	  }

	  return result;
  }
  
  /**
   * Method definition, for XPath parse of literal sequence comparison, 
   * with another xdm value using XPath general comparison operators.
   * 
   * This method, supports XPath parse of expressions like: 
   *   
   *   (for ...) OP xpath_expr
   *   (let ...) OP xpath_expr
   *   (some ...) OP xpath_expr
   *   (every ...) OP xpath_expr
   *   (if ...) OP xpath_expr
   *   
   *   OP := '=' | != | '<' | <= | '>' | >= 
   * 
   * @param prevTokenQueueScanPos						The current XPath parse, token 
   *                                                    queue scan state.
   * @return											Boolean result true or false
   * @throws TransformerException
   */
  private boolean xpathParseExprSingleLiteralSeqCmp(TokenQueueScanPosition prevTokenQueueScanPos)
			                                                                             throws TransformerException {
		
	  boolean result = false;

	  StringBuffer strBuff = new StringBuffer();
	  String xpathExprLhs = null;

	  strBuff.append(m_token);

	  boolean xpathLetExpr = false;
	  if (tokenIs("let")) {
		  xpathLetExpr = true; 
	  }

	  nextToken();

	  while (!isXPathGeneralComparison() && (m_token != null)) {
		  if (xpathLetExpr && tokenIs(":") && lookahead('=', 1)) {
			  strBuff.append(m_token);
		  }
		  else {
			  strBuff.append(m_token + " "); 
		  }
		  nextToken(); 
	  }

	  if (tokenIs(')')) {
		  consumeExpected(')');        		    
		  xpathExprLhs = (strBuff.toString()).trim();         		 
	  }
	  else {
		  restoreTokenQueueScanPosition(prevTokenQueueScanPos); 
	  }

	  if (m_token != null) {
		  m_xpath3ExprSingleComparison = new XPath3ExprSingleComparison();        		  
		  m_xpath3ExprSingleComparison.setXPathExprLhs(xpathExprLhs);

		  boolean isXPathGeneralCmp = isXPathParseRelnGeneralCmpRhs(m_xpath3ExprSingleComparison);

		  if (isXPathGeneralCmp) {        			 
			  List<String> xpathExprPartsRhs = xpathParseGeneralComparisonRhs();

			  m_xpath3ExprSingleComparison.setSeqArrConstructorXPathRhs(xpathExprPartsRhs);

			  int opPos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);

			  insertOp(opPos, 2, OpCodes.OP_EXPR_SINGLE_COMPARISON_XPATH3);

			  m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH, 
					                                 m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);

			  result = true;
		  }
		  else {            		 
			  restoreTokenQueueScanPosition(prevTokenQueueScanPos);
		  }  
	  }
	  else {
		  restoreTokenQueueScanPosition(prevTokenQueueScanPos);
	  }

	  return result;
  }
  
  /**
   * Method definition, for XPath parse of literal empty 
   * sequence.
   */
  private void xpathParseLiteralEmptySequence() {	  
	  List<String> seqOrArrayXPathItems = new ArrayList<String>();

	  int opPos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);

	  nextToken();                            

	  insertOp(opPos, 2, OpCodes.OP_SEQUENCE_CONSTRUCTOR_EXPR);

	  seqOrArrayXPathItems.add(XPATH_EXPR_STR_EMPTY_SEQUENCE);

	  m_xpathSequenceConstructor = new XPathSequenceConstructor();              
	  m_xpathSequenceConstructor.setSequenceConstructorXPathParts(seqOrArrayXPathItems);

	  m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH, 
			                                 m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);
  }

  /**
   * Method definition, for XPath parse of literal empty 
   * array.
   */
  private void xpathParseLiteralEmptyArray() {	  
	  int opPos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);

	  nextToken();                            

	  insertOp(opPos, 2, OpCodes.OP_ARRAY_CONSTRUCTOR_EXPR);

	  m_xpathArrayConstructor = new XPathArrayConstructor();             
	  m_xpathArrayConstructor.setIsEmptyArray(true);

	  m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH, 
			                                 m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);
  }

  /**
   * Method definition, to support XPath parse of RHS of general
   * comparison operator (i.e, =, !=, <, <=, >, >=).
   * 
   * @return									  List of XPath expression string
   * @throws TransformerException
   */
  private List<String> xpathParseGeneralComparisonRhs() throws TransformerException {
	
	  List<String> xpathExprPartsRhs = new ArrayList<String>();

	  if (tokenIs('[')) {
		  // An XPath RHS operand is a literal array constructor		  
		  consumeExpected('[');
		  StringBuffer seqArrItemXPathStrBuff = new StringBuffer();
		  while (m_token != null) {
			  if (tokenIs(']')) {
				  if (seqArrItemXPathStrBuff.length() > 0) {
					  xpathExprPartsRhs.add(seqArrItemXPathStrBuff.toString());
				  }
				  consumeExpected(']');            					 
			  }
			  else if (tokenIs(',')) {            					 
				  xpathExprPartsRhs.add(seqArrItemXPathStrBuff.toString());
				  nextToken();
				  seqArrItemXPathStrBuff = new StringBuffer();
			  }
			  else {
				  seqArrItemXPathStrBuff.append(m_token);
				  nextToken(); 
			  }            				 
		  }
	  }
	  else {
		  // An XPath RHS operand XPath expression
		  StringBuffer seqArrItemXPathStrBuff = new StringBuffer();
		  while (m_token != null) {
			  seqArrItemXPathStrBuff.append(m_token);
			  nextToken(); 
		  }
		  xpathExprPartsRhs.add(seqArrItemXPathStrBuff.toString());
	  }

	  return xpathExprPartsRhs;
  }
  
  /**
   * Method definition to check, which XPath general comparison
   * operator occurs, after XPath parse of RHS which can be 
   * XPathArrayComparison or XPath3ExprSingleComparison.
   * 
   * @param expr						 An XPath expression which is either
   *                                     XPathArrayComparison or XPath3ExprSingleComparison.  
   * @return							 Boolean value indicating true or false
   */
  private boolean isXPathParseRelnGeneralCmpRhs(Expression expr) {
	  
	  boolean result = false;
	  
	  boolean isXPathArrayComparison = false;
	  if (expr instanceof XPathArrayComparison) {
		  isXPathArrayComparison = true; 
	  }
	  
	  if (tokenIs('=')) {		  
		  if (isXPathArrayComparison) {
			  m_xpathArrayComparison.setComparisonOpCode(OpCodes.OP_EQUALS); 
		  }
		  else {
			  m_xpath3ExprSingleComparison.setComparisonOpCode(OpCodes.OP_EQUALS);  
		  }
		  
		  nextToken();		  
		  result = true;
	  }
	  else if (tokenIs('!') && lookahead('=', 1)) {		   
		  if (isXPathArrayComparison) {
			  m_xpathArrayComparison.setComparisonOpCode(OpCodes.OP_NOTEQUALS); 
		  }
		  else {
			  m_xpath3ExprSingleComparison.setComparisonOpCode(OpCodes.OP_NOTEQUALS);  
		  }
		  
		  nextToken();
		  nextToken();		  
		  result = true;
	  }
	  else if (tokenIs('<') && lookahead('=', 1)) {		  
		  if (isXPathArrayComparison) {
			  m_xpathArrayComparison.setComparisonOpCode(OpCodes.OP_LTE); 
		  }
		  else {
			  m_xpath3ExprSingleComparison.setComparisonOpCode(OpCodes.OP_LTE);  
		  }
		  
		  nextToken();
		  nextToken();
		  
		  result = true;
	  }
	  else if (tokenIs('<')) {		  
		  if (isXPathArrayComparison) {
			  m_xpathArrayComparison.setComparisonOpCode(OpCodes.OP_LT); 
		  }
		  else {
			  m_xpath3ExprSingleComparison.setComparisonOpCode(OpCodes.OP_LT);  
		  }
		  
		  nextToken();		  
		  result = true;
	  }
	  else if (tokenIs('>') && lookahead('=', 1)) {		  
		  if (isXPathArrayComparison) {
			  m_xpathArrayComparison.setComparisonOpCode(OpCodes.OP_GTE); 
		  }
		  else {
			  m_xpath3ExprSingleComparison.setComparisonOpCode(OpCodes.OP_GTE);  
		  }
		  
		  nextToken();
		  nextToken();
		  
		  result = true;
	  }
	  else if (tokenIs('>')) {		  
		  if (isXPathArrayComparison) {
			  m_xpathArrayComparison.setComparisonOpCode(OpCodes.OP_GT); 
		  }
		  else {
			  m_xpath3ExprSingleComparison.setComparisonOpCode(OpCodes.OP_GT);  
		  }
		  
		  nextToken();		  
		  result = true;
	  }
	  
	  return result;
  }

  /**
   * Method definition, to check whether the current & next parse 
   * token represents an XPath general comparison operator.
   * 
   * @return						Boolean true or false, indicating the
   *                                result of evaluation.
   */
  private boolean isXPathGeneralComparison() {
	  return (tokenIs(')') && lookahead('=', 1)) || 
			                    (tokenIs(')') && lookahead('!', 1) && lookahead('=', 2)) || 
			                    (tokenIs(')') && lookahead('<', 1) && lookahead('=', 2)) ||
			                    (tokenIs(')') && lookahead('<', 1)) ||
			                    (tokenIs(')') && lookahead('>', 1) && lookahead('=', 2)) ||
			                    (tokenIs(')') && lookahead('>', 1));
  }
  
  /**
   * This method definition checks whether, the supplied token 
   * string value starts with an XPath dynamic function call syntax.
   * 
   * @param tokenStr			    The supplied token string value 
   * @return                        Boolean true or false, indicating the 
   *                                result of evaluation.
   */
  private boolean isDfcBegin(String tokenStr) {
	  boolean result = false;
	  
	  if ((tokenStr != null) && (tokenStr.equals("$") && lookahead('(', 2))) {
		  result = true; 
	  }
	  
	  return result;
  }
  
  /**
   * Given a list of strings and left and right parentheses character definitions, 
   * find whether the concatenated string value of the supplied list contains balanced 
   * parenthesis pairs.
   * 
   * @param strList					The supplied list of strings
   * @param lParentType				Left parentheses character
   * @param rParenType              Right parentheses character
   * @return						Boolean true or false, indicating the 
   *                                result of evaluation.
   */
  private boolean isStrListHasBalancedParentheses(List<String> strList, char lParentType, char rParenType) {
	  
	  boolean result = false;
	  
	  StringBuffer strBuff = new StringBuffer();
	  if (strList.size() > 0) {
		 for (int idx = 0; idx < strList.size(); idx++) {
			strBuff.append(strList.get(idx)); 
		 }
		 
		 if (StringUtil.isStrHasBalancedParentheses(strBuff.toString(), '(', ')')) {
			 result = true; 
		 }
	  }
	  
	  return result;
  }
  
  /**
   * Method definition to compact the supplied string valued list,
   * which contains a list of XPath array items. The supplied string
   * valued list is mutated by this method which is the result of 
   * compaction, and is accessible to the caller of this method.
   */
  private void compactArrayItems(List<String> xpathArrayItems) {
	  
	  List<String> strListCopy = new ArrayList<String>();
	  
	  for (int idx = 0; idx < xpathArrayItems.size(); idx++) {
		  strListCopy.add(xpathArrayItems.get(idx)); 
	  }

	  xpathArrayItems.clear();

	  for (int idx = 0; idx < strListCopy.size(); idx++) {
		  String strValue = strListCopy.get(idx);
		  if (!(strValue.charAt(0) == '[')) {
			  xpathArrayItems.add(strValue); 
		  } 
		  else {
			  StringBuffer strBuff = new StringBuffer();
			  strBuff.append(strValue + ",");
			  xpathArrayItems.add(strValue + ",");
			  
			  for (int idx1 = idx + 1; idx1 < strListCopy.size(); idx1++) {
				  String strVal2 = strListCopy.get(idx1);
				  strBuff.append(strVal2);
				  idx = idx1; 
				  if ((strVal2.endsWith("]")) && (getCharCount(strBuff.toString(), '[') == 
						                                                     getCharCount(strBuff.toString(), ']'))) {
					  xpathArrayItems.remove(xpathArrayItems.size() - 1);
					  
					  break;
				  }
				  else {
					  strBuff.append(",");
				  }
			  }
			  
			  xpathArrayItems.add(strBuff.toString());        		 ; 
		  }
	  }
  }

  /**
   * Check whether, there's beginning of a literal sequence constructor, 
   * or literal array constructor.
   */
  private boolean isLiteralSequenceOrArrayBegin() {
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
      m_isXPathExprBeginParse = false;
      
      if (tokenIs("for")) {
         // To check, whether XPath 'for' expression is a sub expression of another 
         // XPath expression (for e.g, a 'for' expression could be a function 
         // argument).
         String prevTokenStr = getTokenRelative(-2);
         
         XPathForExpr forExpr = ForExpr(prevTokenStr);
         m_forExprList.add(forExpr);
      }
      else if (tokenIs("let")) {
         // To check, whether XPath 'let' expression is a sub expression of another 
         // XPath expression (for e.g, a 'let' expression could be a function 
         // argument).
         String prevTokenStr = getTokenRelative(-2);
          
         m_letExpr = LetExpr(prevTokenStr);
      }
      else if (tokenIs("some")) {
         // To check, whether XPath quantified 'some' expression is a sub expression 
         // of another XPath expression (for e.g, the 'some' expression could be a 
         // function argument, or may be written within an XPath predicate).
         String prevTokenStr = getTokenRelative(-2);
         
         m_quantifiedExpr = QuantifiedExpr(prevTokenStr, XPathQuantifiedExpr.SOME);
      }
      else if (tokenIs("every")) {
         // To check, whether XPath quantified 'every' expression is a sub expression 
         // of another XPath expression (for e.g, an 'every' expression could be a 
         // function argument, or may be written within an XPath predicate).
         String prevTokenStr = getTokenRelative(-2);
         
         m_quantifiedExpr = QuantifiedExpr(prevTokenStr, XPathQuantifiedExpr.EVERY);
      }
      else if (tokenIs("if")) {         
         m_ifExpr = IfExpr();
      }
      else {
         OrExpr();
      }
  }
  
  /**
   *  Get XPath map constructor's string value.
   */
  private String getXPathMapConstructorStrValue() throws TransformerException {
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
			   if (tokenIs(':')) {
				   mapStrBuf.append(" " + m_token + " "); 
			   }
			   else {
				   mapStrBuf.append(m_token);   
			   }
			   
			   nextToken(); 
		   }
		}
		else {
		   if (tokenIs(':')) {
			  mapStrBuf.append(" " + m_token + " "); 
		   }
		   else {
			  mapStrBuf.append(m_token);   
		   }
		   
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
		if (tokenIs("map")) {
			String mapStrVal = getXPathMapConstructorStrValue();
			arrayStrBuf.append(mapStrVal);
			arrayStrBuf.append(m_token);
			nextToken();
		}
		else if (isSquareArrayCons && tokenIs(']')) {
		   arrayStrBuf.append(m_token);
		   if (getCharCount(arrayStrBuf.toString(), '[') == getCharCount(arrayStrBuf.toString(), ']')) {
		      break;
		   }
		   else {
			  nextToken(); 
		   }
		}
		else if (isSquareArrayCons && tokenIs(',')) {
		   if (getCharCount(arrayStrBuf.toString(), '[') == getCharCount(arrayStrBuf.toString(), ']')) {
			  break;
		   }
		   else {
			  arrayStrBuf.append(m_token);
			  nextToken();
		   }
		}
		else if (!isSquareArrayCons && tokenIs('}')) {
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
  
  protected XPathForExpr ForExpr(String prevTokenStrBeforeFor) throws javax.xml.transform.TransformerException
  {
      int opPos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);
      
      nextToken();
      
      insertOp(opPos, 2, OpCodes.OP_FOR_EXPR);
      
      XPathForExpr forExpr = new XPathForExpr();
      
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
            if (lookahead(null, 1) && "(".equals(prevTokenStrBeforeFor)) {
               break;    
            }
            else {
               xPathReturnExprStrPartsList.add(m_token);
               nextToken();
            }
         }
         else if (tokenIs(',') && (",".equals(prevTokenStrBeforeFor) || "(".equals(prevTokenStrBeforeFor))) {
        	String lstStrJoin = xPathReturnExprStrPartsList.toString();
        	lstStrJoin = lstStrJoin.substring(1, lstStrJoin.length()-1);
        	if (StringUtil.isStrHasBalancedParentheses(lstStrJoin, '(', ')')) {        	
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
  
  protected XPathLetExpr LetExpr(String prevTokenStrBeforeLet) throws javax.xml.transform.TransformerException
  {
      int opPos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);
      
      nextToken();
      
      insertOp(opPos, 2, OpCodes.OP_LET_EXPR);
      
      XPathLetExpr letExpr = new XPathLetExpr();
      
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
          
          String varBindingXPathStr = getXPathStrFromComponentParts(bindingXPathExprStrPartsList);

          LetExprVarBinding letExprVarBinding = new LetExprVarBinding();
          letExprVarBinding.setVarName(bindingVarName);
          letExprVarBinding.setXPathExprStr(varBindingXPathStr);

          letExprVarBindingList.add(letExprVarBinding);

          if (tokenIs("return")) {
             break; 
          }          
      }
      
      consumeExpected("return");
      
      List<String> xPathReturnExprStrPartsList = new ArrayList<String>();
      
      while (m_token != null) {
         if (tokenIs(')')) {            
            if (lookahead(null, 1) && "(".equals(prevTokenStrBeforeLet)) {
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
  
  protected XPathQuantifiedExpr QuantifiedExpr(String prevTokenStrBeforeQuantifier, int quantifierExprType) 
                                                              throws javax.xml.transform.TransformerException
  {
      int opPos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);
      
      nextToken();
      
      insertOp(opPos, 2, OpCodes.OP_QUANTIFIED_EXPR);
      
      XPathQuantifiedExpr quantifiedExpr = new XPathQuantifiedExpr();
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
            if (lookahead(null, 1) && "(".equals(prevTokenStrBeforeQuantifier)) {
               break;    
            }
            else {
               xPathTestExprStrPartsList.add(m_token);
               nextToken();
            }
         }
         else if (m_isXPathPredicateParsingActive && tokenIs(']')) {
            break;    
         }
         else {
            xPathTestExprStrPartsList.add(m_token);
            nextToken();
         }
      }
      
      String xPathTestExprStr = getXPathStrFromComponentParts(xPathTestExprStrPartsList);
      
      quantifiedExpr.setQuantifiedExprVarBindingList(quantifiedExprVarBindingList);
      quantifiedExpr.setXPathQuantifierTestStr(xPathTestExprStr);
      
      m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH,
                                            m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);
      
      return quantifiedExpr;
  }
  
  protected XPathIfExpr IfExpr() throws javax.xml.transform.TransformerException
  {
      int opPos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);
      
      // XPath parse of, sequence of two or more 'if' expressions
      m_prevTokQueueScanPosition = new TokenQueueScanPosition(m_queueMark, m_tokenChar, m_token);
      
      StringBuffer strBuff = new StringBuffer();      
      while (m_token != null) {
    	 if (tokenIs("and") || tokenIs("or")) {
    		strBuff.append(" " + m_token + " "); 
    	 }
    	 else {
    	    strBuff.append(m_token);
    	 }
    	 
    	 nextToken();
      }
      
      String strValue = strBuff.toString();
      
      String[] strArr = strValue.split(",if");
      if (strArr.length > 1) {
    	 // There are sequence of, at-least two XPath 'if' expressions
    	 for (int i = 1; i < strArr.length; i++) {
    		strArr[i] = "if" + strArr[i]; 
    	 }
    	 
    	 List<String> strList = Arrays.asList(strArr);
    	 
    	 insertOp(opPos, 2, OpCodes.OP_SEQUENCE_CONSTRUCTOR_EXPR);                 
         m_xpathSequenceConstructor = new XPathSequenceConstructor();
         m_xpathSequenceConstructor.setSequenceConstructorXPathParts(strList);
         
         return null;
      }
      else {
    	 restoreTokenQueueScanPosition(m_prevTokQueueScanPosition);
      }
      
      insertOp(opPos, 2, OpCodes.OP_IF_EXPR);
      
      XPathIfExpr ifExpr = new XPathIfExpr();
      
      if (!lookahead('(', 1)) {
    	 error(XPATHErrorResources.ER_IF_EXPR, new Object[] {}); 
      }
      
      int tokenQueueSize = (m_ops.m_tokenQueue).size();
      
      int thenTokenEffectiveComputedIdx = 0;
      
      for (int idx = 0; idx < tokenQueueSize; idx++) {
    	  Object tokenObj = (m_ops.m_tokenQueue).elementAt(idx);
    	  String tokenStrValue = tokenObj.toString();
    	  if ("then".equals(tokenStrValue)) {
    		 thenTokenEffectiveComputedIdx = idx;
    		 break;
    	  }
      }
      
      StringBuffer probableBranchConditionXPathStrBuff = new StringBuffer();
      for (int idx = 1; idx < thenTokenEffectiveComputedIdx; idx++) {
    	  Object tokenObj = (m_ops.m_tokenQueue).elementAt(idx);
    	  String tokenStrValue = tokenObj.toString();
    	  probableBranchConditionXPathStrBuff.append(tokenStrValue);
      }
      
      String probableBranchConditionExprStr = probableBranchConditionXPathStrBuff.toString();
      int thenTokenComputedRevisedIdx = 0;
      while (!StringUtil.isStrHasBalancedParentheses(probableBranchConditionExprStr, '(', ')')) {
    	  boolean isBreak = false;
    	  for (int idx = (thenTokenEffectiveComputedIdx + 1); idx < tokenQueueSize; idx++) {
    		 Object tokenObj = (m_ops.m_tokenQueue).elementAt(idx);
        	 String tokenStrValue = tokenObj.toString();
        	 if (!"then".equals(tokenStrValue)) {
        	    probableBranchConditionExprStr += tokenStrValue;  
        	 }
        	 else if (StringUtil.isStrHasBalancedParentheses(probableBranchConditionExprStr, '(', ')')) {
        		thenTokenComputedRevisedIdx = idx;
        		isBreak = true;
        		break; 
        	 }
    	  }
    	  
    	  if (isBreak) {
    	     break;	  
    	  }
      }
      
      thenTokenEffectiveComputedIdx = (thenTokenComputedRevisedIdx != 0) ? 
    		                                                    thenTokenComputedRevisedIdx : thenTokenEffectiveComputedIdx;
      
      int elseTokenEffectiveComputedIdx = 0;
      
      for (int idx = (tokenQueueSize - 1); idx >= 0; idx--) {
    	  Object tokenObj = (m_ops.m_tokenQueue).elementAt(idx);
    	  String tokenStrValue = tokenObj.toString();
    	  if ("else".equals(tokenStrValue)) {
    		 elseTokenEffectiveComputedIdx = idx;
    		 break;
    	  }
      }
      
      StringBuffer probableElseExprStrBuff = new StringBuffer();
      for (int idx = (elseTokenEffectiveComputedIdx + 1); idx < tokenQueueSize; idx++) {
    	  Object tokenObj = (m_ops.m_tokenQueue).elementAt(idx);
    	  String tokenStrValue = tokenObj.toString();
    	  probableElseExprStrBuff.append(tokenStrValue);
      }
      
      String probableElseExprStr = probableElseExprStrBuff.toString();
      int elseTokenComputedRevisedIdx = 0;
      while (!StringUtil.isStrHasBalancedParentheses(probableElseExprStr, '(', ')')) {
    	  boolean isBreak = false;
    	  for (int idx = (elseTokenEffectiveComputedIdx - 1); idx >= 0; idx--) {
    		  Object tokenObj = (m_ops.m_tokenQueue).elementAt(idx);
        	  String tokenStrValue = tokenObj.toString();
        	  if ("else".equals(tokenStrValue)) {
        		 probableElseExprStr = "";
        		 for (int idx1 = (idx + 1); idx1 < tokenQueueSize; idx1++) {
        			tokenObj = (m_ops.m_tokenQueue).elementAt(idx1);
        			probableElseExprStr += (tokenObj.toString() + " "); 
        		 }
        		 probableElseExprStr = probableElseExprStr.trim();
        		 if (StringUtil.isStrHasBalancedParentheses(probableElseExprStr, '(', ')')) {
        			elseTokenComputedRevisedIdx = idx; 
        			isBreak = true;
        			break; 
        		 }
        	  }
    	  }
    	  
    	  if (isBreak) {
    		 break; 
    	  }
      }
      
      elseTokenEffectiveComputedIdx = ((elseTokenComputedRevisedIdx != 0) ? 
    		                                                     elseTokenComputedRevisedIdx : elseTokenEffectiveComputedIdx); 
      
      List<String> branchConditionXPathStrPartsList = new ArrayList<String>();
      
      int startIdx = (m_isXPathPredicateParsingActive ? 4 : 2);      
      for (int idx = startIdx; idx < thenTokenEffectiveComputedIdx; idx++) {
    	  Object tokenObj = (m_ops.m_tokenQueue).elementAt(idx);
    	  String tokenStrValue = tokenObj.toString();
    	  branchConditionXPathStrPartsList.add(tokenStrValue); 
      }
      
      String thenPrevTokenStr = ((m_ops.m_tokenQueue).elementAt(
    		                                                 thenTokenEffectiveComputedIdx - 1)).toString();      
      if (!")".equals(thenPrevTokenStr)) {
    	  error(XPATHErrorResources.ER_IF_EXPR, new Object[] {}); 
      }
      
      m_queueMark = thenTokenEffectiveComputedIdx;
      m_token = "then";
      m_tokenChar = 't';
      
      consumeExpected("then");
      
      branchConditionXPathStrPartsList = branchConditionXPathStrPartsList.subList(0, 
                                                             branchConditionXPathStrPartsList.size() - 1);
      
      String branchConditionXPathExprStr = getXPathStrFromComponentParts(branchConditionXPathStrPartsList);
      
      List<String> thenExprXPathStrPartsList = new ArrayList<String>();
      
      for (int idx = (thenTokenEffectiveComputedIdx + 1); idx < elseTokenEffectiveComputedIdx; idx++) {
    	  Object tokenObj = (m_ops.m_tokenQueue).elementAt(idx);
    	  String tokenStrValue = tokenObj.toString();
    	  thenExprXPathStrPartsList.add(tokenStrValue); 
      }
      
      m_queueMark = (elseTokenEffectiveComputedIdx + 1);
      m_token = "else";
      m_tokenChar = 'e';
      
      consumeExpected("else");
      
      String thenXPathExprStr = getXPathStrFromComponentParts(thenExprXPathStrPartsList);
      thenXPathExprStr = thenXPathExprStr.trim();
      if (!thenXPathExprStr.equals("()") && thenXPathExprStr.startsWith("(") && thenXPathExprStr.endsWith(")")) {
    	 thenXPathExprStr = thenXPathExprStr.substring(1, thenXPathExprStr.length() - 1);    	 
      }
      
      List<String> elseExprXPathStrPartsList = new ArrayList<String>();
      
      while (m_token != null)
      {
          if (m_isXPathPredicateParsingActive && lookahead(']', 1)) {
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
      
      String elseXPathStr = getXPathStrFromComponentParts(elseExprXPathStrPartsList);
      elseXPathStr = elseXPathStr.trim();
      if (!elseXPathStr.equals("()") && elseXPathStr.startsWith("(") && elseXPathStr.endsWith(")")) {
    	 String probableElseXPathStr = (elseXPathStr.substring(1, elseXPathStr.length() - 1)).trim();
    	 if (probableElseXPathStr.startsWith("if")) {
    		elseXPathStr = probableElseXPathStr;	 
    	 }
      }
      
      ifExpr.setIfConditionXPathStr(branchConditionXPathExprStr);
      ifExpr.setThenExprXPathStr(thenXPathExprStr);
      ifExpr.setElseExprXPathStr(elseXPathStr);
      
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
    
    if (tokenIs('(')) {
      if ("=".equals(getTokenRelative(-2))) {
    	  // Previous token is, '='.
    	  
    	  // XPath parse for RHS of general comparison operators, =, !=. 
    	  // RHS of these operators that're handled here, have syntax of 
    	  // type (a,b,c ...), i.e a literal sequence.
    	  
    	  TokenQueueScanPosition prevTokQueueScanPosition = new TokenQueueScanPosition(
                                                                                   m_queueMark, m_tokenChar, m_token);
    	  nextToken();
    	  if (!tokenIs(')') && !lookahead(null, 1)) {
    		  List<String> seqXPathItems = new ArrayList<String>();
    		      		  
    		  while (m_token != null) {
    			  List<String> xpathExprTokens = new ArrayList<String>();                 

    			  while (!tokenIs(",") && (m_token != null)) {
    				  if (!lookahead(null, 1)) {
    					  xpathExprTokens.add(m_token);
    				  }
    				  nextToken();
    			  }

    			  if (xpathExprTokens.size() > 0) {
    				  String seqItemXPath = getXPathStrFromComponentParts(xpathExprTokens);
    				  if (seqItemXPath.endsWith(")")) {
    					  seqItemXPath = seqItemXPath.substring(0, seqItemXPath.length());    
    				  }    			                   

    				  seqXPathItems.add(seqItemXPath);
    			  }    			      			  

    			  if (m_token != null) {
    				  nextToken();   
    			  }
    		  }    		      		  
    		  
    		  boolean isXPathParseOkToProceed = true;
                            
              for (int idx = 0; idx < seqXPathItems.size(); idx++) {
                 String seqItemXPathExprStr = seqXPathItems.get(idx);
                 
                 if (m_isXPathPredicateParsingActive && (idx == (seqXPathItems.size()-1))) {
                	if (seqItemXPathExprStr.endsWith(")")) {
                	   seqItemXPathExprStr = seqItemXPathExprStr.substring(0, seqItemXPathExprStr.length()-1);
                	   seqXPathItems.set(idx, seqItemXPathExprStr);                	   
   				    } 
                 }
                 
                 char lParenChar = '(';
                 char rParenChar = ')';                 
                 boolean isStrHasBalancedParentheses = StringUtil.isStrHasBalancedParentheses(
								                		                                                    seqItemXPathExprStr, 
								                		                                                    lParenChar, rParenChar);
                 if (!isStrHasBalancedParentheses) {
                    isXPathParseOkToProceed = false;
                    break;
                 }
              }
              
              if (isXPathParseOkToProceed) {
            	  if (m_isXPathPredicateParsingActive) {
            		 // An XPath parse above has gone one token ahead, than 
            		 // what should be. We set these variables, to their expected 
            		 // values.
            		 m_tokenChar = ']';
            		 m_token = "]";
            	  }
            	  else {
                     nextToken();
            	  }
            	  
                  insertOp(opPos, 2, OpCodes.OP_SEQUENCE_CONSTRUCTOR_EXPR);
                  
                  m_xpathSequenceConstructor = new XPathSequenceConstructor();                 
                  m_xpathSequenceConstructor.setSequenceConstructorXPathParts(seqXPathItems);                  
                  
                  m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH,
                                                 m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);
                  
                  return addPos;
              }
              else {
            	  restoreTokenQueueScanPosition(prevTokQueueScanPosition); 
              }
    	   }
        }
    }

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
           // XPath parse of simple map operator '!'        	           
            
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
        // XPath parse of value comparison operator "eq"
          
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
        // XPath parse of value comparison operator "ne"
          
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
    
    if (tokenIs('(')) {
        if (isXPathGeneralRelationalComparisonWithSequence()) {
        	// XPath parse for RHS of general comparison relational 
        	// operators <, <=, >, >=.        	
       	    // RHS for these XPath operators that're handled here, have 
        	// syntax of type (a,b,c), i.e a literal sequence.
        	
        	TokenQueueScanPosition prevTokQueueScanPosition = new 
        			                                     TokenQueueScanPosition(m_queueMark, m_tokenChar, m_token);
        	nextToken();
        	if (!tokenIs(')') && !lookahead(null, 1)) {
        		List<String> seqXPathItems = new ArrayList<String>();

        		while (m_token != null) {
        			List<String> xpathExprTokens = new ArrayList<String>();                 

        			while (!tokenIs(",") && (m_token != null)) {
        				if (!lookahead(null, 1)) {
        					xpathExprTokens.add(m_token);
        				}
        				nextToken();
        			}

        			if (xpathExprTokens.size() > 0) {
        				String seqItemXPath = getXPathStrFromComponentParts(xpathExprTokens);
        				if (seqItemXPath.endsWith(")")) {
        					seqItemXPath = seqItemXPath.substring(0, seqItemXPath.length());    
        				}    			                   

        				seqXPathItems.add(seqItemXPath);
        			}

        			if (m_token != null) {
        				nextToken();   
        			}
        		}

        		boolean isXPathParseOkToProceed = true;

        		for (int idx = 0; idx < seqXPathItems.size(); idx++) {
        			String seqItemXPathExprStr = seqXPathItems.get(idx);
        			
        			if (m_isXPathPredicateParsingActive && (idx == (seqXPathItems.size()-1))) {
                       if (seqItemXPathExprStr.endsWith(")")) {
                    	  seqItemXPathExprStr = seqItemXPathExprStr.substring(0, seqItemXPathExprStr.length()-1);
                    	  seqXPathItems.set(idx, seqItemXPathExprStr);                	   
       				   } 
                    }
        			
        			char lParenChar = '(';
        			char rParenChar = ')';                 
        			boolean isStrHasBalancedParentheses = StringUtil.isStrHasBalancedParentheses(seqItemXPathExprStr, 
        					                                                          												lParenChar, rParenChar);
        			if (!isStrHasBalancedParentheses) {
        				isXPathParseOkToProceed = false;
        				break;
        			}
        		}

        		if (isXPathParseOkToProceed) {                  
        			if (m_isXPathPredicateParsingActive) {
               		   // An XPath parse above has gone one token ahead, than 
               		   // what should be. We set these variables, to their expected 
               		   // values.
               		   m_tokenChar = ']';
               		   m_token = "]";
               	    }
               	    else {
                       nextToken();
               	    }
        			
        			insertOp(opPos, 2, OpCodes.OP_SEQUENCE_CONSTRUCTOR_EXPR);
        			
        			m_xpathSequenceConstructor = new XPathSequenceConstructor();                 
        			m_xpathSequenceConstructor.setSequenceConstructorXPathParts(seqXPathItems);                  

        			m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH,
        					m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);

        			return addPos;
        		}
        		else {
        			restoreTokenQueueScanPosition(prevTokQueueScanPosition); 
        		}
        	}
        }
    }

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
          // XPath parse of node comparison operator "<<"
          
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
          // XPath parse of node comparison operator ">>"
          
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
          // XPath parse of range "to" expression    	     	  
          
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
          // XPath parse of string concatenation operator "||"    	  
          
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
          // XPath parse of arrow operator "=>"
    	  
          consumeExpected("=>");

          insertOp(addPos, 2, OpCodes.OP_ARROW);
          
          FunctionCall();
          
          if (m_token != null) {
        	 m_arrowOpRemainingXPathExprStr = "";  
          }
          
          while (m_token != null) {
        	 m_arrowOpRemainingXPathExprStr += m_token;
        	 nextToken();
          }
      }
      else if (tokenIs("lt"))
      {
          // XPath parse of value comparison operator "lt"
          
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
          // XPath parse of value comparison operator "gt"
          
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
          // XPath parse of value comparison operator "le"
          
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
          // XPath parse of value comparison operator "ge"
          
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
          // XPath parse of node comparison operator "is"
          
          nextToken();
        
          insertOp(addPos, 2, OpCodes.OP_IS);

          int op1 = m_ops.getOp(OpMap.MAPINDEX_LENGTH) - addPos;

          addPos = RelationalExpr(addPos);
          m_ops.setOp(addPos + OpMap.MAPINDEX_LENGTH,
             m_ops.getOp(addPos + op1 + 1) + op1);
          addPos += 2;
      }
      else if (tokenIs("instance") && lookahead("of", 1)) {
          // XPath parse of "instance of" expression    	  
          
          consumeExpected("instance");
          consumeExpected("of");
          
          insertOp(addPos, 2, OpCodes.OP_INSTANCE_OF);
          
          int op1 = m_ops.getOp(OpMap.MAPINDEX_LENGTH) - addPos;
          
          m_xpathSequenceTypeExpr = SequenceTypeExpr(false);          
          m_ops.setOp(addPos + OpMap.MAPINDEX_LENGTH, 
                                                  m_ops.getOp(addPos + op1 + 1) + op1);
          addPos += 2;
      }      
      else if (tokenIs("cast") && lookahead("as", 1)) {
          // XPath parse of "cast as" expression    	  
          
          consumeExpected("cast");
          consumeExpected("as");
          
          insertOp(addPos, 2, OpCodes.OP_CAST_AS);
          
          int op1 = m_ops.getOp(OpMap.MAPINDEX_LENGTH) - addPos;
          
          m_xpathSequenceTypeExpr = SequenceTypeExpr(false);
          
          m_ops.setOp(addPos + OpMap.MAPINDEX_LENGTH, 
                                                  m_ops.getOp(addPos + op1 + 1) + op1);
          addPos += 2;
      }
      else if (tokenIs("castable") && lookahead("as", 1)) {
          // XPath parse of "castable as" expression    	  
          
          consumeExpected("castable");
          consumeExpected("as");
          
          insertOp(addPos, 2, OpCodes.OP_CASTABLE_AS);
          
          int op1 = m_ops.getOp(OpMap.MAPINDEX_LENGTH) - addPos;
          
          m_xpathSequenceTypeExpr = SequenceTypeExpr(false);
          
          m_ops.setOp(addPos + OpMap.MAPINDEX_LENGTH, 
                                                  m_ops.getOp(addPos + op1 + 1) + op1);
          addPos += 2;
      }
      else if (tokenIs("treat") && lookahead("as", 1)) {
          // XPath parse of "treat as" expression    	  
          
          consumeExpected("treat");
          consumeExpected("as");
          
          insertOp(addPos, 2, OpCodes.OP_TREAT_AS);
          
          int op1 = m_ops.getOp(OpMap.MAPINDEX_LENGTH) - addPos;          
          
          m_xpathSequenceTypeExpr = SequenceTypeExpr(false);
          
          m_ops.setOp(addPos + OpMap.MAPINDEX_LENGTH, 
                                                  m_ops.getOp(addPos + op1 + 1) + op1);
          addPos += 2;
      }
    }

    return addPos;
  }

  /**
   * Check whether, there is XPath parse of general relational comparison using
   * operators <, <=, >, >=.
   */
  private boolean isXPathGeneralRelationalComparisonWithSequence() {
	  return ("<".equals(getTokenRelative(-2)) || 
			  ("=".equals(getTokenRelative(-2)) && "<".equals(getTokenRelative(-3)))) ||
			  (">".equals(getTokenRelative(-2)) || 
					  ("=".equals(getTokenRelative(-2)) && ">".equals(getTokenRelative(-3))));
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
        
        if (tokenIs('(')) {
            addPos = handleXPathParseRhsSequenceOperand(addPos, OpCodes.OP_PLUS);
        }
        else {
        	int opPlusLeftHandLen = m_ops.getOp(OpMap.MAPINDEX_LENGTH) - addPos;

        	addPos = AdditiveExpr(addPos);

        	m_ops.setOp(addPos + OpMap.MAPINDEX_LENGTH, 
        	  m_ops.getOp(addPos + opPlusLeftHandLen + 1) + opPlusLeftHandLen);
        	addPos += 2;
        }                
      }
      else if (tokenIs('-'))
      {
        nextToken();
        
        insertOp(addPos, 2, OpCodes.OP_MINUS);
        
        if (tokenIs('(')) {
            addPos = handleXPathParseRhsSequenceOperand(addPos, OpCodes.OP_MINUS);
        }
        else {
        	int opPlusLeftHandLen = m_ops.getOp(OpMap.MAPINDEX_LENGTH) - addPos;

        	addPos = AdditiveExpr(addPos);

        	m_ops.setOp(addPos + OpMap.MAPINDEX_LENGTH, 
        	  m_ops.getOp(addPos + opPlusLeftHandLen + 1) + opPlusLeftHandLen);
        	addPos += 2;
        }
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
   * | MultiplicativeExpr '*' UnaryExpr
   * | MultiplicativeExpr 'div' UnaryExpr
   * | MultiplicativeExpr 'idiv' UnaryExpr
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
        
        if (tokenIs('(')) {
            addPos = handleXPathParseRhsSequenceOperand(addPos, OpCodes.OP_MULT);
        }
        else {
        	int opPlusLeftHandLen = m_ops.getOp(OpMap.MAPINDEX_LENGTH) - addPos;

        	addPos = MultiplicativeExpr(addPos);
            m_ops.setOp(addPos + OpMap.MAPINDEX_LENGTH,
              m_ops.getOp(addPos + opPlusLeftHandLen + 1) + opPlusLeftHandLen);
            addPos += 2;
        }
      }
      else if (tokenIs("div"))
      {
        nextToken();
        
        insertOp(addPos, 2, OpCodes.OP_DIV);
        
        if (tokenIs('(')) {
            addPos = handleXPathParseRhsSequenceOperand(addPos, OpCodes.OP_DIV);
        }
        else {
        	int opPlusLeftHandLen = m_ops.getOp(OpMap.MAPINDEX_LENGTH) - addPos;

        	addPos = MultiplicativeExpr(addPos);
            m_ops.setOp(addPos + OpMap.MAPINDEX_LENGTH,
              m_ops.getOp(addPos + opPlusLeftHandLen + 1) + opPlusLeftHandLen);
            addPos += 2;
        }
      }
      else if (tokenIs("idiv"))
      {
        nextToken();
        
        insertOp(addPos, 2, OpCodes.OP_IDIV);

        if (tokenIs('(')) {
            addPos = handleXPathParseRhsSequenceOperand(addPos, OpCodes.OP_IDIV);
        }
        else {
        	int opPlusLeftHandLen = m_ops.getOp(OpMap.MAPINDEX_LENGTH) - addPos;

        	addPos = MultiplicativeExpr(addPos);
            m_ops.setOp(addPos + OpMap.MAPINDEX_LENGTH,
              m_ops.getOp(addPos + opPlusLeftHandLen + 1) + opPlusLeftHandLen);
            addPos += 2;
        }
      }
      else if (tokenIs("mod"))
      {
        nextToken();
        
        insertOp(addPos, 2, OpCodes.OP_MOD);

        if (tokenIs('(')) {
            addPos = handleXPathParseRhsSequenceOperand(addPos, OpCodes.OP_MOD);
        }
        else {
        	int opPlusLeftHandLen = m_ops.getOp(OpMap.MAPINDEX_LENGTH) - addPos;

        	addPos = MultiplicativeExpr(addPos);
            m_ops.setOp(addPos + OpMap.MAPINDEX_LENGTH,
              m_ops.getOp(addPos + opPlusLeftHandLen + 1) + opPlusLeftHandLen);
            addPos += 2;
        }
      }
      else if (tokenIs("quo"))
      {
        nextToken();
        
        insertOp(addPos, 2, OpCodes.OP_QUO);

        if (tokenIs('(')) {
            addPos = handleXPathParseRhsSequenceOperand(addPos, OpCodes.OP_QUO);
        }
        else {
        	int opPlusLeftHandLen = m_ops.getOp(OpMap.MAPINDEX_LENGTH) - addPos;

        	addPos = MultiplicativeExpr(addPos);
            m_ops.setOp(addPos + OpMap.MAPINDEX_LENGTH,
              m_ops.getOp(addPos + opPlusLeftHandLen + 1) + opPlusLeftHandLen);
            addPos += 2;
        }
      }
    }

    return addPos;
  }

  /**
   *
   * UnaryExpr  ::=  NodeCombiningExpr
   * | '-' UnaryExpr
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

    NodeCombiningExpr();

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
   * are node sets. The result of the left hand side expr is the 
   * union/intersection/difference of the results of the right hand side 
   * expressions.
   *
   * NodeCombiningExpr    ::=    PathExpr
   * |                           NodeCombiningExpr '|' PathExpr
   * |                           NodeCombiningExpr 'union' PathExpr
   * |                           NodeCombiningExpr 'intersect' PathExpr
   * |                           NodeCombiningExpr 'except' PathExpr
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected void NodeCombiningExpr() throws javax.xml.transform.TransformerException
  {

    int opPos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);
    boolean continueOrLoop = true;
    boolean foundCombiningExpr = false;

    do 
    {      
    	String prevToken = getTokenRelative(-2);
    	if (isTokenNodeCombining(prevToken) && tokenIs('(')) {
    		TokenQueueScanPosition prevTokQueueScanPosition = new TokenQueueScanPosition(
    																				m_queueMark, m_tokenChar, m_token);
    		StringBuffer seqExprStrBuff = new StringBuffer();
    		while (m_token != null) {
    			seqExprStrBuff.append(m_token);
    			nextToken();
    		}    	      	  

    		String seqExprStr = seqExprStrBuff.toString();
    		if (seqExprStr.contains(",")) {
    			XslTransformData.m_xpathNodeCombiningExprRhsStrBuff = seqExprStrBuff;

    			int tokenQueueSize = m_ops.m_tokenQueue.size();
    			for (int idx = (prevTokQueueScanPosition.getQueueMark() - 1); idx < tokenQueueSize; idx++) {
    				m_ops.m_tokenQueue.removeElementAt(idx);  
    			}

    			m_queueMark = prevTokQueueScanPosition.getQueueMark();
    			// Converting an XPath expression like (a,b) to a variable reference.
    			// With this, an XPath expression like '* except (a,b)' is evaluated at run-time 
    			// as '* except $varName' where the variable's value at run-time is retrieved
    			// from the class field XslTransformSharedDatastore.xpathNodeCombiningExprRhsStrBuff.
    			m_ops.m_tokenQueue.addElement("$");
    			String tempVarName = "v1_" + System.currentTimeMillis();  // Producing a reliable random variable name string
    			m_ops.m_tokenQueue.addElement(tempVarName);
    			m_token = "$";
    			m_tokenChar = '$';
    		}
    		else {    		
    		    restoreTokenQueueScanPosition(prevTokQueueScanPosition);
    		}
    	}

    	PathExpr(); 

    	if ((tokenIs('|') && !tokenIs("||")) || tokenIs("union")) {
    		if (!foundCombiningExpr) {
    			foundCombiningExpr = true;

    			insertOp(opPos, 2, OpCodes.OP_UNION);
    		}

    		nextToken();
    	}
    	else if (tokenIs("intersect")) {
    		if (!foundCombiningExpr) {
    			foundCombiningExpr = true;

    			insertOp(opPos, 2, OpCodes.OP_INTERSECT);
    		}

    		nextToken();  
    	}
    	else if (tokenIs("except")) {
    		if (!foundCombiningExpr) {
    			foundCombiningExpr = true;

    			insertOp(opPos, 2, OpCodes.OP_EXCEPT);
    		}

    		nextToken();
    	}
    	else {
    		break;
    	}
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
       // XPath parse of dynamic function call, and map &
       // array information lookup using function call syntax.
                     
       appendOp(2, OpCodes.OP_DYNAMIC_FUNCTION_CALL);
       
       nextToken();  // consume '$'
       
       String funcRefVarName = m_token;       
       nextToken();
       consumeExpected('(');
       
       // XPath parse of dynamic function call argument information          
       
       XPathDynamicFunctionCall xpathDynamicFunctionCall = new XPathDynamicFunctionCall();       
       xpathDynamicFunctionCall.setFuncRefVarName(funcRefVarName);
       
       if (!tokenIs(')')) {
    	   // Function call argument list is not empty
    	   
    	   TokenQueueScanPosition prevTokQueueScanPosition = new TokenQueueScanPosition(m_queueMark, m_tokenChar, m_token);
    	   
    	   StringBuffer strBuff = new StringBuffer();
    	   while (m_token != null) {
    		   strBuff.append(m_token);    		   
    		   nextToken();
    	   }
    	   
    	   // XPath parse of chained access of consecutive specification 
    	   // (with chaining of two or more XPath expressions) of XPath 
    	   // dynamic function call, map and array information lookup 
    	   // syntax.
    	   
    	   boolean isDynamicFuncCallChainedAccess = false;
    	   
    	   String strValue = strBuff.toString();
    	   String[] xpathExprStrArr = strValue.split("\\)\\(");    	   
    	   if (xpathExprStrArr.length > 1) {
    		  String arrLastItemStr = xpathExprStrArr[xpathExprStrArr.length - 1];
    		  int arrLastItemStrLength = arrLastItemStr.length();
    		  // Removing ')' if present, from string value of array's last item
    		  if ((arrLastItemStrLength > 1) && (arrLastItemStr.charAt(arrLastItemStrLength - 1) == ')')) {    			  
    			  String lastArgStr = arrLastItemStr.substring(0, arrLastItemStrLength - 1);
    			  xpathExprStrArr[xpathExprStrArr.length - 1] = lastArgStr; 
    		  }
    		  
    		  for (int idx = 0; idx < xpathExprStrArr.length; idx++) {
    			  String str1 = xpathExprStrArr[idx];
    			  if (str1.contains("(") || str1.contains(")")) {
    				 break; 
    			  }
    			  else if (idx == (xpathExprStrArr.length - 1)) {
    				 isDynamicFuncCallChainedAccess = true; 
    			  }
    		  }
    	   }    	   
    	       	   
    	   if (isDynamicFuncCallChainedAccess) {
    		   xpathDynamicFunctionCall.setFuncRefVarName(funcRefVarName);
        	   xpathDynamicFunctionCall.setChainedArgXPathStrArray(xpathExprStrArr); 
    	   }
    	   else {
    		   List<String> argList = new ArrayList<>();
    		   
    		   restoreTokenQueueScanPosition(prevTokQueueScanPosition);
    		   
    		   List<String> argDetailsStrPartsList = new ArrayList<String>();

    		   // We specify here a temporary function call argument delimiter 
    		   // string, for this processing.       
    		   String delim = "t0_" + (UUID.randomUUID()).toString();

    		   while (m_token != null && isXPathDynamicFuncCallParseAhead(argDetailsStrPartsList, delim)) {
    			   // NO OP
    		   }

    		   m_dynamicFunctionCallArgumentMarker = false;

    		   int startIdx = 0;
    		   int idxDelim;       
    		   while (argDetailsStrPartsList.contains(delim) && 
    				   (idxDelim = argDetailsStrPartsList.indexOf(delim)) != -1) {
    			   List<String> lst1 = argDetailsStrPartsList.subList(startIdx, idxDelim);

    			   String xpathStr = getXPathStrFromComponentParts(lst1);

    			   argList.add(xpathStr);

    			   List<String> lst2 = argDetailsStrPartsList.subList(idxDelim + 1, argDetailsStrPartsList.size());

    			   argDetailsStrPartsList = lst2; 
    		   }

    		   if (argDetailsStrPartsList.size() > 0) {
    			   String xpathStr = getXPathStrFromComponentParts(argDetailsStrPartsList);
    			   argList.add(xpathStr);
    		   }
    		   
        	   xpathDynamicFunctionCall.setArgList(argList);

        	   if (argList.size() > 0) {
        		   mutateXPathDynamicFuncCallReference(xpathDynamicFunctionCall, argList);
        	   }
        	   
        	   consumeExpected(')');
    	   }    	       	   
       }
    
       m_xpathDynamicFunctionCallList.add(xpathDynamicFunctionCall);
       
       m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH,
                               m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);
       
       matchFound = true;
    }
    else if ((m_tokenChar == '$') && (lookahead('?', 2))) {
       // XPath parse for unary lookup operator on map & array references       
       
       // e.g, $map1?a (get xdm map's entry value, for key named 'a'), 
       // $array1?3 (get xdm item for array index 3).
    	
       // We translate these XPath expression syntax, to a 
       // compiled form of XPath dynamic function call.
    	
       appendOp(2, OpCodes.OP_DYNAMIC_FUNCTION_CALL);
        
       nextToken();  // consume '$'
        
       String funcRefVarName = m_token;
       List<String> argList = new ArrayList<>();
       
       nextToken();
       consumeExpected('?');
       
       StringBuffer argListNormalizedStrBuff = new StringBuffer();
       while (m_token != null) {
    	  argListNormalizedStrBuff.append(m_token);
    	  nextToken();
       }
       
       argList.add(argListNormalizedStrBuff.toString());          
       nextToken();
       
       XPathDynamicFunctionCall xpathDynamicFunctionCall = new XPathDynamicFunctionCall();
       xpathDynamicFunctionCall.setFuncRefVarName(funcRefVarName);
       xpathDynamicFunctionCall.setIsFromUnaryLookupEvaluation(true);
       xpathDynamicFunctionCall.setArgList(argList);
               
       m_xpathDynamicFunctionCallList.add(xpathDynamicFunctionCall);
       
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
      m_op_group_parse = true;
      Expr();
      m_op_group_parse = false;
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
      // XPath parse of inline function expression    	
      
      appendOp(2, OpCodes.OP_INLINE_FUNCTION);
      
      m_xpath_inlineFunction = InlineFunctionExpr();
      
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
  
  protected XPathInlineFunction InlineFunctionExpr() throws javax.xml.transform.TransformerException {
      XPathInlineFunction inlineFunction = new XPathInlineFunction();
      
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
    
              if (tokenIs('$'))
              {
                  nextToken();
                  
                  InlineFunctionParameter inlineFunctionParameter = new InlineFunctionParameter();                   
                  if (lookahead("as", 1)) {
                     inlineFunctionParameter.setParamName(m_token);                     
                     nextToken();
                     nextToken();                     
                     SequenceTypeData paramType = new SequenceTypeData();
                     XPathSequenceTypeExpr seqTypeExpr = SequenceTypeExpr(true);
                     paramType.setBuiltInSequenceType(seqTypeExpr.getBuiltInSequenceType());
                     paramType.setItemTypeOccurrenceIndicator(seqTypeExpr.getItemTypeOccurrenceIndicator());
                     paramType.setSequenceTypeKindTest(seqTypeExpr.getSequenceTypeKindTest());
                     paramType.setSequenceTypeFunctionTest(seqTypeExpr.getSequenceTypeFunctionTest());
                     paramType.setSequenceTypeMapTest(seqTypeExpr.getSequenceTypeMapTest());
                     paramType.setSequenceTypeArrayTest(seqTypeExpr.getSequenceTypeArrayTest());
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
         returnType.setBuiltInSequenceType(seqTypeExpr.getBuiltInSequenceType());
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
	
    m_isFunctionArgumentParse = true;
    
	int opPos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);
    
    appendOp(2, OpCodes.OP_ARGUMENT);
    
    if (tokenIs('(')) {
    	// XPath literal sequence as, function argument
    	
    	TokenQueueScanPosition prevTokQueueScanPosition = new TokenQueueScanPosition(
    			                                                           m_queueMark, m_tokenChar, m_token);
    	if (lookahead(')', 1)) {
	        // An XPath function argument is () (i.e, an empty sequence)	        
	        consumeExpected('(');
	        consumeExpected(')');                            
	        
	        insertOp(opPos, 2, OpCodes.OP_SEQUENCE_CONSTRUCTOR_EXPR);
	        
	        List<String> seqConstructorXPathParts = new ArrayList<String>();
	        seqConstructorXPathParts.add(XPATH_EXPR_STR_EMPTY_SEQUENCE);
	        
	        List<XPathSequenceConstructor> seqConsList = m_xpathSequenceConsFuncArgs.getSeqFuncArgList();
	        XPathSequenceConstructor xPathSeqConstructor = new XPathSequenceConstructor();                 
	        xPathSeqConstructor.setSequenceConstructorXPathParts(seqConstructorXPathParts);    	
	        seqConsList.add(xPathSeqConstructor);
	      	List<Boolean> funcArgUsedSeq = m_xpathSequenceConsFuncArgs.getIsFuncArgUsedList();
	      	funcArgUsedSeq.add(Boolean.valueOf(false));
	        
	        m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH, 
	                                             m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);	
    	}
    	else {
	       consumeExpected('(');    		
	 	    		
	       List<String> seqConstructorXPathParts = new ArrayList<String>();
	       parseSequenceOrArrayConstructorFuncArg(seqConstructorXPathParts, '(', ')');
	       if (tokenIs(')')) {
	          consumeExpected(')');
	       }
	       else {	    	  
	    	  restoreTokenQueueScanPosition(prevTokQueueScanPosition);
	    	  
	    	  Expr();
	    	  
	    	  m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH, 
                                                     m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);
	    	  return;
	       }
	            
	       if (seqConstructorXPathParts.size() > 1) {
	          insertOp(opPos, 2, OpCodes.OP_SEQUENCE_CONSTRUCTOR_EXPR);
	          
	          List<XPathSequenceConstructor> seqConsList = m_xpathSequenceConsFuncArgs.getSeqFuncArgList();
	          XPathSequenceConstructor xPathSeqConstructor = new XPathSequenceConstructor();                 
	          xPathSeqConstructor.setSequenceConstructorXPathParts(seqConstructorXPathParts);    	
	          seqConsList.add(xPathSeqConstructor);
	      	  List<Boolean> funcArgUsedSeq = m_xpathSequenceConsFuncArgs.getIsFuncArgUsedList();
	      	  funcArgUsedSeq.add(Boolean.valueOf(false));
	      	
	          m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH, 
	                                                 m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);	          
	       }
	       else {
	    	  restoreTokenQueueScanPosition(prevTokQueueScanPosition);
	    	  
	          Expr();
	       }
    	}
    }
    else if (tokenIs('[')) {
    	// XPath literal square array as, function argument
    	
    	consumeExpected('[');    		

    	List<String> arrConstructorXPathParts = new ArrayList<String>();
    	parseSequenceOrArrayConstructorFuncArg(arrConstructorXPathParts, '[', ']');
    	consumeExpected(']'); 

    	insertOp(opPos, 2, OpCodes.OP_ARRAY_CONSTRUCTOR_EXPR);
    	
    	List<XPathArrayConstructor> arrConsList = m_xpathArrayConsFuncArgs.getArrayFuncArgList();
    	XPathArrayConstructor xPathArrayConstructor = new XPathArrayConstructor();                 
    	xPathArrayConstructor.setArrayConstructorXPathParts(arrConstructorXPathParts);    	
    	arrConsList.add(xPathArrayConstructor);
    	List<Boolean> funcArgUsedArr = m_xpathArrayConsFuncArgs.getIsFuncArgUsedArr();
    	funcArgUsedArr.add(Boolean.valueOf(false));

    	m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH, 
    			                               m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);	       
    }
    else if (tokenIs("array") && lookahead('{', 1)) {
    	// XPath literal curly array as, function argument
    	
    	consumeExpected("array");
    	consumeExpected('{');

    	List<String> arrConstructorXPathParts = new ArrayList<String>();
    	parseSequenceOrArrayConstructorFuncArg(arrConstructorXPathParts, '{', '}');
    	consumeExpected('}'); 

    	insertOp(opPos, 2, OpCodes.OP_ARRAY_CONSTRUCTOR_EXPR);
    	
    	List<XPathArrayConstructor> arrConsList = m_xpathArrayConsFuncArgs.getArrayFuncArgList();
    	XPathArrayConstructor xPathArrayConstructor = new XPathArrayConstructor();                 
    	xPathArrayConstructor.setArrayConstructorXPathParts(arrConstructorXPathParts);    	    	
    	arrConsList.add(xPathArrayConstructor);
    	List<Boolean> funcArgUsedArr = m_xpathArrayConsFuncArgs.getIsFuncArgUsedArr();
    	funcArgUsedArr.add(Boolean.valueOf(false));

    	m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH, 
    			                               m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);	       
    }
    else if (tokenIs("map")) {
       // XPath literal map expression as, function argument    	
  	   mapFuncArg();	
    }
    else if (tokenIs("?")) {
       // A function argument placeholder, for a function call partial 
       // function application.    	
       appendOp(2, OpCodes.OP_FUNC_ARG_PLACEHOLDER);
       
       consumeExpected('?');
       
       FuncArgPlaceholder funcArgPlaceholder = new FuncArgPlaceholder();
       m_funcArgPlaceHolderList.add(funcArgPlaceholder);
    }
    else if (lookahead(':', 1)) {
       // XPath parse of named function reference, for XPath built-in functions 
   	   // and stylesheet functions.    	
       handleXPathParseNamedFuncRefWithNSQual(opPos);
    }
    else if (!m_token.contains(":") && m_token.contains("#") && xslFunctionService.isFuncArityWellFormed(m_token)) {
    	// XPath parse of named function reference, for XPath built-in functions    	
    	handleXPathParseNamedFuncRefWithoutNSQual(opPos);
    }
    else if (tokenIs("for")) {
       ExprSingle();
    }
    else if (tokenIs("function")) {
       appendOp(2, OpCodes.OP_INLINE_FUNCTION);
        
       m_xpath_inlineFunction = InlineFunctionExpr();               
    }
    else {
       TokenQueueScanPosition prevTokQueueScanPosition = new TokenQueueScanPosition(m_queueMark, m_tokenChar, m_token);
       StringBuffer strBuff = new StringBuffer();
       while (!tokenIs(',') && !(tokenIs(')') && lookahead(null, 1)) && (m_token != null)) {
    	  strBuff.append(m_token);
    	  nextToken();
       }
       
       String xpathExprStr = strBuff.toString();
       int idx = xpathExprStr.lastIndexOf('/');
       if (idx != -1) {
    	  String xpathLhsStr = xpathExprStr.substring(0, idx);
    	  String xpathRhsStr = xpathExprStr.substring(idx + 1);
    	  if (xpathLhsStr.endsWith(FunctionTable.XPATH_BUILT_IN_FUNCS_NS_URI) || 
								    			  		xpathLhsStr.endsWith(FunctionTable.XPATH_BUILT_IN_MATH_FUNCS_NS_URI) ||
								    			  		xpathLhsStr.endsWith(FunctionTable.XPATH_BUILT_IN_MAP_FUNCS_NS_URI) ||
								    			  		xpathLhsStr.endsWith(FunctionTable.XPATH_BUILT_IN_ARRAY_FUNCS_NS_URI)) {
              restoreTokenQueueScanPosition(prevTokQueueScanPosition);    		  
    		  Expr();  
    	  }    	  
    	  else {    		  
    		  if (!isStrHasXPathAxisNamePrefix(xpathRhsStr) && (xpathRhsStr.endsWith("()") || xpathRhsStr.endsWith("(.)"))) {    		  
    			  insertOp(opPos, 2, OpCodes.OP_XPATH_EXPR_WITH_FUNC_CALL_SUFFIX);    		  

    			  m_xpathExprWithFuncCallSuffix = new XPathExprFunctionCallSuffix();
    			  m_xpathExprWithFuncCallSuffix.setXPathExprStr(xpathExprStr);
    		  }
    		  else {
    			  restoreTokenQueueScanPosition(prevTokQueueScanPosition);    		  
    			  Expr(); 
    		  }
    	  }
       }
       else {
    	   restoreTokenQueueScanPosition(prevTokQueueScanPosition);
    	  
    	   // An XPath function argument is text(). Modify contents of XPath 
    	   // parse token queue by replacing token string text() with ".".
    	   if (tokenIs("text") && lookahead('(', 1) && lookahead(')', 2)) {    		   
    		   mutateTokenQueue(new String[] { "." }, 0, m_queueMark + 2, 1);
    		   
    		   m_token = ".";
    		   m_tokenChar = '.';
    	   }

    	   Expr();
        }                     
    }
    
    m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH,
                                           m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);
    m_isFunctionArgumentParse = false;
    
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
        appendOp(4, OpCodes.OP_CONSTRUCTOR_STYLESHEET_EXT_FUNCTION);

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

      if (-1 == funcTok)
      {
          error(XPATHErrorResources.ER_COULDNOT_FIND_FUNCTION,
              new Object[]{"{" + FunctionTable.XPATH_BUILT_IN_FUNCS_NS_URI + "}" + m_token + "()"});
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
      	  
      if (!m_isParseSequenceTypeExprWithUserDefinedType && !tokenIs(')'))
      {
        consumeExpected(',');

        if (tokenIs(')'))
        {
          error(XPATHErrorResources.ER_FOUND_COMMA_BUT_NO_FOLLOWING_ARG,
                null);  //"Found ',' but no following argument!");
        }
      }
    }

    if (!m_isParseSequenceTypeExprWithUserDefinedType) {
        consumeExpected(')');
    }
    else {
    	m_isParseSequenceTypeExprWithUserDefinedType = false;	
    }
    
    if (tokenIs('(')) {
    	/**
    	 * XPath parse of a possible function argument literal list suffix. 
    	 * This can handle an XPath function call syntax like prefix:func1(args1)(args2), 
    	 * where an XPath expression syntax prefix:func1(args1) returns a function item 
    	 * that can be called with it's required arguments (i.e, args2).
    	 */
    	
    	appendOp(2, OpCodes.OP_XPATH_FUNC_CALL_EXTENDED_ARG);
    	
    	consumeExpected('(');
    	
    	List<String> funcArgrgXPathExprStrList = new ArrayList<String>();
    	
    	while (!tokenIs(')') && m_token != null)
        {
    		if (tokenIs(','))
    		{
    			error(XPATHErrorResources.ER_FOUND_COMMA_BUT_NO_PRECEDING_ARG, null);
    		}

    		StringBuffer strBuff = new StringBuffer();
    		while (!(tokenIs(',') || tokenIs(')')) && m_token != null) {
    			strBuff.append(m_token);
    			nextToken();
    		}

    		String strValue = strBuff.toString();
    		if (strValue.length() > 0) {
    			funcArgrgXPathExprStrList.add(strValue);
    		}

    		if (!tokenIs(')'))
    		{
    			consumeExpected(',');

    			if (tokenIs(')'))
    			{
    				error(XPATHErrorResources.ER_FOUND_COMMA_BUT_NO_FOLLOWING_ARG, null);
    			}
    		}
        }
    	
    	m_xpathExprFuncCallExtendedArg = new XPathExprFuncCallExtendedArg();    	
    	if (funcArgrgXPathExprStrList.size() > 0) {    		
    		m_xpathExprFuncCallExtendedArg.setFunctionArgXPathExprStrList(funcArgrgXPathExprStrList);
    	}
    	
    	consumeExpected(')');
    	
    	m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH,
                       m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);
    }

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
    	if (!isXPathPatternExcludeTrailingNodeFunctions() && ((m_tokenChar == '$') && (lookahead('(', 2)))) {
            // XPath parse of expression like /a/b/$func(..), i.e an XPath path expression 
    		// with a dynamic function call suffix.
    		
    		String xpathOneStr = "";
    		String xpathTwoStr = "";
    		for (int idx = 0; idx < (m_queueMark - 2); idx++) {
    		   xpathOneStr += (String)(m_ops.m_tokenQueue.elementAt(idx)); 
    		}
    		
    		// XPath dynamic function call string accumulation
    		xpathTwoStr += m_token;
    		nextToken();
    		xpathTwoStr += m_token;
    		nextToken();
    		xpathTwoStr += m_token;
    		nextToken();
    		
    		while (m_token != null) {
     		   xpathTwoStr += m_token;
     		   nextToken();     		   
     	    }
    		
    		m_xpathExprFunctionSuffix = new XPathExprFunctionSuffix();    	   
     	    m_xpathExprFunctionSuffix.setXPathOneStr(xpathOneStr);
     	    m_xpathExprFunctionSuffix.setXPathTwoStr(xpathTwoStr);
     	   
     	    int opPos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);
     	    
     	    m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH,
                                             m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);
    	}
    	else {
           // RelativeLocationPath can't end with a trailing '/'
           // "Location step expected following '/' or '//'"
           error(XPATHErrorResources.ER_EXPECTED_LOC_STEP, null);
    	}
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

    // There is probably a better way to test for this transition
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
    int axesType = 0;
    
    boolean funcMatchFound = false;
    
    ExpressionNode exprParent = (ExpressionNode)m_sourceLocator;
    if (exprParent instanceof ElemTemplateElement) {
    	if (exprParent instanceof ElemTextLiteral) {
    	   exprParent = ((ElemTemplateElement)exprParent).getParentElem();	
    	}    	
        m_xpathDefaultNamespace = getXPathDefaultNamespace((ElemTemplateElement)exprParent);
    }

    if (lookahead("::", 1))
    {
    	String axesName = m_token;	
    	axesType = AxisName();

    	nextToken();      
    	nextToken();

    	if ((Keywords.FROM_ANCESTORS_STRING).equals(axesName) || (Keywords.FROM_ANCESTORS_OR_SELF_STRING).equals(axesName) || 
						    			(Keywords.FROM_CHILDREN_STRING).equals(axesName) || (Keywords.FROM_DESCENDANTS_STRING).equals(axesName) || 
						    			(Keywords.FROM_DESCENDANTS_OR_SELF_STRING).equals(axesName) || (Keywords.FROM_FOLLOWING_STRING).equals(axesName) || 
						    			(Keywords.FROM_FOLLOWING_SIBLINGS_STRING).equals(axesName) || (Keywords.FROM_PARENT_STRING).equals(axesName) || 
						    			(Keywords.FROM_PRECEDING_STRING).equals(axesName) || (Keywords.FROM_PRECEDING_SIBLINGS_STRING).equals(axesName) || 
						    			(Keywords.FROM_SELF_STRING).equals(axesName)) {

    		if ((m_xpathDefaultNamespace != null) && !tokenIs(m_xpathDefaultNamespace)) {    			
    			mutateTokenQueue(new String[] { m_xpathDefaultNamespace, ":" }, 0, m_queueMark - 1, 2);
    			
    			m_token = m_xpathDefaultNamespace;
    			m_tokenChar = m_xpathDefaultNamespace.charAt(0);
    		}
    	}
    }
    else if (tokenIs('@'))
    {
    	axesType = OpCodes.FROM_ATTRIBUTES;

    	appendOp(2, axesType);
    	nextToken();            
    }
    else if (!isXPathPatternExcludeTrailingNodeFunctions() && (lookahead('(', 1) || (lookahead(':', 1) && lookahead('(', 3))))
    {
    	TokenQueueScanPosition prevTokQueueScanPosition = new TokenQueueScanPosition(
    			                                                                 m_queueMark, m_tokenChar, m_token);
    	funcMatchFound = FunctionCall();
    	if (funcMatchFound && (m_token != null)) {    		
    	   restoreTokenQueueScanPosition(prevTokQueueScanPosition);
    	   
    	   axesType = OpCodes.FROM_CHILDREN;

           appendOp(2, axesType);
    	}
    	else if (funcMatchFound) {    	   
    	   m_queueMark = 0;
    	   m_tokenChar = 0;
    	   m_token = null;
    	   
    	   nextToken();
    	   
    	   int scanOneEndPos = 0;
    	   int scanTwoStartPos = 0;
    	   if (prevTokQueueScanPosition.getQueueMark() > 2) {
    		   scanOneEndPos = (prevTokQueueScanPosition.getQueueMark() - 2);
    		   scanTwoStartPos = prevTokQueueScanPosition.getQueueMark();
    	   }
    	   else if (prevTokQueueScanPosition.getQueueMark() > 1) {
    		   scanOneEndPos = (prevTokQueueScanPosition.getQueueMark() - 1);
    		   scanTwoStartPos = prevTokQueueScanPosition.getQueueMark();
    	   }
    	   
    	   String xpathOneStr = "";
    	   String xpathTwoStr = "";
    	   xpathOneStr = m_token;
    	   while (m_queueMark != scanOneEndPos) {
    		   try {
    		      nextToken();
    		   }
    		   catch (ClassCastException ex) { 
    			  handleClassCastExceptionOnNextToken(ex);
    		   }
    		   xpathOneStr += m_token; 
    	   }
    	     
    	   for (int idx = scanOneEndPos; idx < scanTwoStartPos; idx++) {
    		  nextToken(); 
    	   }
    	   
    	   while (m_token != null) {
    		   xpathTwoStr += m_token;
    		   // The following is copy of the code from nextToken() 
    		   // function, with minor modification for this need.
    		   if (m_queueMark < m_ops.getTokenQueueSize())
    		   {
    			   Object tokenObj = m_ops.m_tokenQueue.elementAt(m_queueMark++);
    			   if (tokenObj instanceof XString) {
    				   m_token = "'" + ((XString)tokenObj).str() + "'";  
    			   }
    			   else if (tokenObj instanceof XNumber) {
    				   XNumber xNumber = (XNumber)tokenObj;
    				   String strVal = null;
    				   if (xNumber.getXsDecimal() != null) {
    					   strVal = (xNumber.getXsDecimal()).stringValue();  
    				   }
    				   else if (xNumber.getXsDouble() != null) {
    					   strVal = (xNumber.getXsDouble()).stringValue();
    				   }
    				   else if (xNumber.getXsInteger() != null) {
    					   strVal = (xNumber.getXsInteger()).stringValue();
    				   }
    				   else {
    					   strVal = xNumber.str();  
    				   }
    				   m_token = strVal; 
    			   }
    			   else {
    				   m_token = (String)tokenObj; 
    			   }
    			   m_tokenChar = m_token.charAt(0);
    		   }
    		   else
    		   {
    			   m_token = null;
    			   m_tokenChar = 0;
    		   }
    	   }
    	   
    	   m_xpathExprFunctionSuffix = new XPathExprFunctionSuffix();    	   
    	   m_xpathExprFunctionSuffix.setXPathOneStr(xpathOneStr);
    	   m_xpathExprFunctionSuffix.setXPathTwoStr(xpathTwoStr);
    	   
    	   m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH,
                                            m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);
    	   
    	}
    }
    else
    {       
        axesType = OpCodes.FROM_CHILDREN;
        
        boolean isXPathDefaultNsProcessingSkip = false;
        if (tokenIs("node") && lookahead('(', 1) && lookahead(')', 2)) {
        	isXPathDefaultNsProcessingSkip = true;
        }
        
        if (!isXPathDefaultNsProcessingSkip && (m_xpathDefaultNamespace != null) && !tokenIs(m_xpathDefaultNamespace)) {        	
        	mutateTokenQueue(new String[] { m_xpathDefaultNamespace, ":" }, 0, m_queueMark - 1, 2);
        	
			m_token = m_xpathDefaultNamespace;
			m_tokenChar = m_xpathDefaultNamespace.charAt(0);        	
   	    }

        appendOp(2, axesType);
    }

    if (!funcMatchFound) {
	    // Make room for telling how long the step is without the predicate
	    m_ops.setOp(OpMap.MAPINDEX_LENGTH, m_ops.getOp(OpMap.MAPINDEX_LENGTH) + 1);
	
	    NodeTest(axesType);
	
	    // Tell how long the step is without the predicate
	    m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH + 1,
	      m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);
    }
   }

   /**
    * There are few known XPath parse situations within an 
    * object of this class, where during nextToken() method call 
    * a java.lang.ClassCastException needs to be handled.
    */
   protected void handleClassCastExceptionOnNextToken(ClassCastException ex) {
	  String str = ex.getMessage();
	  
	  if (STRING_CLASSCAST_ERROR_MESSAGE.equals(str)) {
		  Object tokenQueueObj1 = m_ops.m_tokenQueue.elementAt(m_queueMark - 1);
		  m_token = "'" + tokenQueueObj1.toString() + "'";      
		  m_tokenChar = m_token.charAt(0);
	  }
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
        error(XPATHErrorResources.ER_UNKNOWN_NODETYPE, new Object[]{ m_token });
      }
      else
      {
        nextToken();

        int nt = ((Integer) nodeTestOp).intValue();                
        
        if (nt == OpCodes.NODETYPE_DOCUMENT) {
           nt = OpCodes.NODETYPE_ROOT;
        }

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
    else if (tokenIs('.')) 
    {
    	// Added for XSLT 3.0
    	
    	// This has been implemented to handle XSL match pattern 
    	// strings, that represent XDM atomic values.
    	Object nodeTestOp = Keywords.getNodeType(m_token);
    	
    	int nt = ((Integer)nodeTestOp).intValue();

        m_ops.setOp(m_ops.getOp(OpMap.MAPINDEX_LENGTH), nt);
        m_ops.setOp(OpMap.MAPINDEX_LENGTH, m_ops.getOp(OpMap.MAPINDEX_LENGTH) + 1);
        
        consumeExpected('.');
        
        if (tokenIs('[')) {
           StringBuffer strBuff = new StringBuffer();
           consumeExpected('[');
           while (m_token != null) {
        	  if (tokenIs(']')) {
        		 consumeExpected(']');  
        	  }
        	  else {
        		 strBuff.append(m_token);
        		 nextToken();
        	  }
           }
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
   * @throws javax.xml.transform.TransformerException
   */
  protected void Predicate() throws javax.xml.transform.TransformerException
  {

    if (tokenIs('['))
    {
    	m_isXPathPredicateParsingActive = true;      
    	nextToken();
    	PredicateExpr();
    	consumeExpected(']');      
    	m_isXPathPredicateParsingActive = false;
    }
  }

  /**
   * 
   * PredicateExpr ::= Expr
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
   * Number := NON_SCIENTIFIC_NUMBER | SCIENTIFIC_NUMBER
   *  
   * NON_SCIENTIFIC_NUMBER ::= [0-9]+('.'[0-9]+)? | '.'[0-9]+
   * 
   * SCIENTIFIC_NUMBER := NON_SCIENTIFIC_NUMBER [e|E] [+|-]? [0-9]+  
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected void Number() throws javax.xml.transform.TransformerException
  {

    if (m_token != null)
    {
    	
      XNumber xNumber = null;
      String numberStrValue = "";
      
      try
      {       	  
    	  if ((m_token.endsWith("e") || m_token.endsWith("E")) && (lookahead('+', 1) || lookahead('-', 1))) {
    		  numberStrValue = m_token;
    		  nextToken();
    		  numberStrValue += m_token;
    		  nextToken();
    		  numberStrValue += m_token;
    	  }
    	  else {
    		  numberStrValue = m_token;
    	  }
    	  
    	  BigDecimal bigDecimal = new BigDecimal(numberStrValue);
    	  xNumber = new XNumber(bigDecimal.doubleValue());
    	  
          if (!(numberStrValue.contains(".") || numberStrValue.contains("e") || numberStrValue.contains("E"))) {
        	  // If a numeric literal doesn't contain ., e and E, then the literal is of type xs:integer        	          	  
        	  xNumber.setXsInteger(new XSInteger(numberStrValue));
          }
          else if (numberStrValue.contains(".") && !(numberStrValue.contains("e") || numberStrValue.contains("E"))) {
        	  // If a numeric literal contains ., but not e and E, then the literal is of type xs:decimal        	  
        	  xNumber.setXsDecimal(new XSDecimal(numberStrValue));
          }
          else if (numberStrValue.contains("e") || numberStrValue.contains("E")) {
        	  // If a numeric literal contains e or E, then the literal is of type xs:double        	  
        	  xNumber.setXsDouble(new XSDouble(numberStrValue));
          }
      }
      catch (Exception ex)
      {
    	  error(XPATHErrorResources.ER_COULDNOT_BE_FORMATTED_TO_NUMBER, new Object[]{ numberStrValue });
      }

      m_ops.m_tokenQueue.setElementAt(xNumber, m_queueMark - 1);
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
   * @throws javax.xml.transform.TransformerException
   */
  protected void Pattern() throws javax.xml.transform.TransformerException
  {
	  
	if (m_xpathDefaultNamespace != null) {
	   mutateTokenQueueXPathMatchPattern();
	}

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
  
   public String getArrowOpRemainingXPathExprStr() {
	   return m_arrowOpRemainingXPathExprStr; 
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
    * @param       xpathExprPartList    String array, that'll be populated with xpath expression
    *                                   strings of the sequence/array items in order.
    * @param       lParen               This shall be one of following : '(', '[', '{'.
    * @param       rParen               This shall be one of following : ')', ']', '}'.                        
    */
   private void parseSequenceOrArrayConstructorFuncArg(List<String> xpathExprPartList, char lParen, 
 		                                                                               char rParen) throws TransformerException {	  
 	  String xpathExpr = "";	  
 	  boolean fl1 = true;
 	  
 	  while (!tokenIs(rParen)) {
 		  if (!(tokenIs(',') || tokenIs(rParen))) {
 			  xpathExpr += m_token;
 			  nextToken();
 			  if (tokenIs(rParen) && !StringUtil.isStrHasBalancedParentheses(lParen + xpathExpr, lParen, rParen)) {
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
 					  if (StringUtil.isStrHasBalancedParentheses(lParen + xpathExpr + rParen, lParen, rParen) 
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
    * XPath parse of literal map as function argument.
    */
   private void mapFuncArg() throws TransformerException {	  

 	  int opPos1 = m_ops.getOp(OpMap.MAPINDEX_LENGTH);
 	  
 	  nextToken();
 	  
 	  insertOp(opPos1, 2, OpCodes.OP_MAP_CONSTRUCTOR_EXPR);

 	  m_xpathMapConstructor = new XPathMapConstructor();
 	  
 	  Map<String, String> nativeMapObj = new HashMap<String, String>();                    

 	  consumeExpected('{');

 	  if (!tokenIs('}')) {
 		  while (!tokenIs('}')) {        	 
 			  String mapEntryKeyXPathExprStr = m_token;
 			  nextToken();
 			  consumeExpected(':');
 			  String mapEntryValueXPathExprStr = null;
 			  if (tokenIs("map")) {
 				  // There's likely an XPath map constructor here, 
 				  // within this map. 	        		
 				  mapEntryValueXPathExprStr = getXPathMapConstructorStrValue();
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
 	  }
 	  else {
 		 consumeExpected('}');
 	  }

 	  m_xpathMapConstructor.setNativeMap(nativeMapObj);
 	  
 	  m_ops.setOp(opPos1 + OpMap.MAPINDEX_LENGTH,
 			                            m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos1);
   }
   
   /**
    * XPath parse of sequence type expressions.
    */
   private XPathSequenceTypeExpr SequenceTypeExpr(boolean isXPathInlineFunctionParse) 
		                                                                         throws javax.xml.transform.TransformerException {
       
	   int opPos = 0;
       
       if (!isXPathInlineFunctionParse) {
          opPos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);                  
          insertOp(opPos, 2, OpCodes.OP_SEQUENCE_TYPE_EXPR);
       }
       
       XPathSequenceTypeExpr xpathSequenceTypeExpr = new XPathSequenceTypeExpr();
       
       SequenceTypeKindTest sequenceTypeKindTest = null;
       
       if (tokenIs("empty-sequence") && lookahead('(', 1) && lookahead(')', 2)) {
           xpathSequenceTypeExpr.setBuiltInSequenceType(SequenceTypeSupport.EMPTY_SEQUENCE);
           consumeExpected("empty-sequence");
           consumeExpected('(');
           consumeExpected(')');
       }      
       else if (tokenIs("element")) {
           sequenceTypeKindTest = constructSequenceTypeKindTestForXDMNodes(xpathSequenceTypeExpr, 
                                                                                         SequenceTypeSupport.ELEMENT_KIND, isXPathInlineFunctionParse);          
           xpathSequenceTypeExpr.setSequenceTypeKindTest(sequenceTypeKindTest);          
       }
       else if (tokenIs("attribute")) {
           sequenceTypeKindTest = constructSequenceTypeKindTestForXDMNodes(xpathSequenceTypeExpr, 
                                                                                         SequenceTypeSupport.ATTRIBUTE_KIND, isXPathInlineFunctionParse);          
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
              setSequenceTypeOccurenceIndicator(xpathSequenceTypeExpr, isXPathInlineFunctionParse); 
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
              setSequenceTypeOccurenceIndicator(xpathSequenceTypeExpr, isXPathInlineFunctionParse); 
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
              setSequenceTypeOccurenceIndicator(xpathSequenceTypeExpr, isXPathInlineFunctionParse);  
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
              setSequenceTypeOccurenceIndicator(xpathSequenceTypeExpr, isXPathInlineFunctionParse);  
           }
       }
       else if (tokenIs("function")) {
     	   parseFunctionItemSequenceType(xpathSequenceTypeExpr);
     	   if (m_token != null) {
              setSequenceTypeOccurenceIndicator(xpathSequenceTypeExpr, isXPathInlineFunctionParse);
              if (m_op_group_parse) {
            	  m_token = ")";
            	  m_tokenChar = ')';
              }
           }
       }
       else if (tokenIs("map")) {
     	   parseXdmMapSequenceType(xpathSequenceTypeExpr, false);
     	   if ((m_token != null) && !tokenIs(')')) {
              setSequenceTypeOccurenceIndicator(xpathSequenceTypeExpr, isXPathInlineFunctionParse);  
           }
       }
       else if (tokenIs("array")) {
           parseXdmArraySequenceType(xpathSequenceTypeExpr, false);
           if ((m_token != null) && !tokenIs(')')) {
              setSequenceTypeOccurenceIndicator(xpathSequenceTypeExpr, isXPathInlineFunctionParse);  
           }
       }
       else if (tokenIs(XMLConstants.W3C_XML_SCHEMA_NS_URI)) {
           parseXdmBuiltInXmlSchemaSequenceType(xpathSequenceTypeExpr, isXPathInlineFunctionParse);         
       }
       else if (tokenIs("schema-element")) {
    	   sequenceTypeKindTest = new SequenceTypeKindTest();
           sequenceTypeKindTest.setKindVal(SequenceTypeSupport.SCHEMA_ELEMENT_KIND);
           nextToken();
           consumeExpected('(');
           if (lookahead(':', 1)) {
        	   sequenceTypeKindTest.setNodeNsUri(m_token);
        	   nextToken();
        	   consumeExpected(':');
        	   sequenceTypeKindTest.setNodeLocalName(m_token);
        	   nextToken();
           }
           else {
        	   sequenceTypeKindTest.setNodeLocalName(m_token);
        	   nextToken();
           }
           xpathSequenceTypeExpr.setSequenceTypeKindTest(sequenceTypeKindTest);
           consumeExpected(')');
       }
       else if (tokenIs("schema-attribute")) {
    	   sequenceTypeKindTest = new SequenceTypeKindTest();
           sequenceTypeKindTest.setKindVal(SequenceTypeSupport.SCHEMA_ATTRIBUTE_KIND);
           nextToken();
           consumeExpected('(');
           if (lookahead(':', 1)) {
        	   sequenceTypeKindTest.setNodeNsUri(m_token);
        	   nextToken();
        	   consumeExpected(':');
        	   sequenceTypeKindTest.setNodeLocalName(m_token);
        	   nextToken();
           }
           else {
        	   sequenceTypeKindTest.setNodeLocalName(m_token);
        	   nextToken();
           }
           xpathSequenceTypeExpr.setSequenceTypeKindTest(sequenceTypeKindTest);
           consumeExpected(')');
       }
       else if (tokenIs("document-node")) {
    	   sequenceTypeKindTest = new SequenceTypeKindTest();
           sequenceTypeKindTest.setKindVal(SequenceTypeSupport.DOCUMENT_KIND);          
           nextToken();
           consumeExpected('(');
           consumeExpected(')');
           xpathSequenceTypeExpr.setSequenceTypeKindTest(sequenceTypeKindTest);
           if (m_token != null) {
              setSequenceTypeOccurenceIndicator(xpathSequenceTypeExpr, isXPathInlineFunctionParse);  
           }
       }
       else {
    	   // Check possibility of user-defined schema type specified within the sequence type expression
    	   parseSequenceTypeExprWithUserDefinedType(xpathSequenceTypeExpr, null);                                                                                    
       }
       
       if (!isXPathInlineFunctionParse) {
          m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH,
                                                 m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);
       }
       
       return xpathSequenceTypeExpr;
   }

   /**
    * While XPath parse of sequence type expression, check for the possibility of
    * user-defined schema type specified within the sequence type expression.
    */
   private void parseSequenceTypeExprWithUserDefinedType(XPathSequenceTypeExpr xpathSequenceTypeExpr, String typeName) 
		                                                                                  throws TransformerException {
	   m_isParseSequenceTypeExprWithUserDefinedType = true;
	   
	   StylesheetRoot stylesheetRoot = XslTransformData.m_stylesheetRoot;
	   String xslSystemId = XslTransformData.m_xslSystemId;

	   String type_namespace = null;
	   String type_name = null;
	   
	   if (typeName == null) {
		   type_namespace = m_token;
		   type_name = null;
		   nextToken();
		   if (":".equals(m_token)) {
			   nextToken();
			   type_name = m_token;    		  
		   }
		   else {
			   type_name = type_namespace;
			   type_namespace = null;			   
		   }
	   }
	   else {
		   type_name = typeName;
		   type_namespace = null;
	   }
	   
	   nextToken();

	   Node elemTemplateElem = stylesheetRoot.getFirstChildElem();

	   while (elemTemplateElem != null && !(Constants.ELEMNAME_IMPORT_SCHEMA_STRING).equals(elemTemplateElem.getLocalName())) {   
		   elemTemplateElem = elemTemplateElem.getNextSibling();
	   }

	   if (elemTemplateElem != null) {
		   NodeList nodeList = elemTemplateElem.getChildNodes();
		   Node xsSchemaTopMostNode = nodeList.item(0);		   

		   if (xsSchemaTopMostNode != null) {
			   // xsl:import-schema instruction's child contents specifies a literal XML Schema document
			   parseImportSchemaWithChildSchemaContents(xpathSequenceTypeExpr, type_namespace, type_name, xsSchemaTopMostNode); 
		   }
		   else {
			   // An XML Schema document is available, at the uri referenced by xsl:import-schema 
			   // element's attribute 'schema-location'.			   
			   parseImportSchemaFromExternalLocation(xpathSequenceTypeExpr, xslSystemId, type_namespace, type_name, elemTemplateElem);
		   }
	   }
   }

   /**
    * XPath parse of xsl:import-schema instruction, where schema details are available as child contents
    * of xsl:import-schema element.
    */
   private void parseImportSchemaWithChildSchemaContents(XPathSequenceTypeExpr xpathSequenceTypeExpr, String typeNamespace,
		                                                 String typeName, Node xsSchemaTopMostNode) throws TransformerException {
	   
	   StylesheetRoot stylesheetRoot = XslTransformData.m_stylesheetRoot;
	   XSModel xsModel = stylesheetRoot.getXsModel();
	   
	   if (xsModel != null) {
		   // A pre-compiled schema is available. Currently this is due to user's request
		   // for an XML input document validation via command-line option -val.
		   setXsTypeDefinitionOnSequenceTypeExpr(xsModel, xpathSequenceTypeExpr, typeNamespace, typeName);
	   }
	   else {
		   String xmlSchemaDocumentStr = null;
		   XSLoaderImpl xsLoader = new XSLoaderImpl();

		   try {
			   DOMImplementationLS domImplLS = (DOMImplementationLS)((DOMImplementationRegistry.newInstance()).getDOMImplementation("LS"));
			   LSSerializer lsSerializer = domImplLS.createLSSerializer();
			   DOMConfiguration domConfig = lsSerializer.getDomConfig();
			   domConfig.setParameter(XSL3FunctionService.XML_DOM_FORMAT_PRETTY_PRINT, Boolean.TRUE);
			   xmlSchemaDocumentStr = lsSerializer.writeToString((Document)xsSchemaTopMostNode);
			   xmlSchemaDocumentStr = xmlSchemaDocumentStr.replaceFirst(XSL3FunctionService.UTF_16, XSL3FunctionService.UTF_8);
			   xmlSchemaDocumentStr = xmlSchemaDocumentStr.replaceFirst("schema", "schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"");

			   DOMInputImpl lsInput = new DOMInputImpl();
			   lsInput.setCharacterStream(new StringReader(xmlSchemaDocumentStr));

			   xsModel = xsLoader.load(lsInput);    						

			   if (xsModel != null) {
				   setXsTypeDefinitionOnSequenceTypeExpr(xsModel, xpathSequenceTypeExpr, typeNamespace, typeName);	    							    						
			   }
			   else {
				   throw new javax.xml.transform.TransformerException("FODC0005 : A valid XML schema could not be built, from child "
						   																	   + "contents of xs:import-schema instruction.");
			   }
		   }
		   catch (Exception ex) {
			   throw new TransformerException(ex.getMessage());
		   }
       }
   }
   
   /**
    * XPath parse of xsl:import-schema instruction, where schema details are available from
    * xsl:import-schema element's attribute schema-location.
    */
   private void parseImportSchemaFromExternalLocation(XPathSequenceTypeExpr xpathSequenceTypeExpr, String xslSystemId,
		                                              String typeNamespace, String typeName, Node elemTemplateElem) throws TransformerException {
	   
	   StylesheetRoot stylesheetRoot = XslTransformData.m_stylesheetRoot;
	   XSModel xsModel = stylesheetRoot.getXsModel();

	   if (xsModel != null) {
		   // A pre-compiled schema is available. Currently this is due to user's request
		   // for an XML input document validation via command-line option -val.
		   setXsTypeDefinitionOnSequenceTypeExpr(xsModel, xpathSequenceTypeExpr, typeNamespace, typeName);
	   }
	   else {
		   XSLoaderImpl xsLoader = new XSLoaderImpl();

		   NamedNodeMap importSchemaNodeAttributes = ((Element)elemTemplateElem).getAttributes();        			   

		   if (importSchemaNodeAttributes != null) {
			   Node attrNode1 = importSchemaNodeAttributes.item(0);
			   Node attrNode2 = importSchemaNodeAttributes.item(1);	        			   	        			   	        			   

			   try {
				   URL url = null;							
				   if (attrNode1 != null) {
					   URI inpUri = new URI(attrNode1.getNodeValue());
					   String stylesheetSystemId = xslSystemId;

					   if (!inpUri.isAbsolute() && (stylesheetSystemId != null)) {
						   URI resolvedUri = (new URI(stylesheetSystemId)).resolve(inpUri);
						   url = resolvedUri.toURL(); 
						   if (!"namespace".equals(attrNode1.getNodeName())) {
							   xsModel = xsLoader.loadURI(url.toString());
						   }
					   }
				   }

				   if (attrNode2 != null && xsModel == null) {
					   URI inpUri = new URI(attrNode2.getNodeValue());
					   String stylesheetSystemId = xslSystemId;

					   if (!inpUri.isAbsolute() && (stylesheetSystemId != null)) {
						   URI resolvedUri = (new URI(stylesheetSystemId)).resolve(inpUri);
						   url = resolvedUri.toURL();
						   if ("schema-location".equals(attrNode2.getNodeName())) {
							   xsModel = xsLoader.loadURI(url.toString());
						   }
					   }	        				  
				   }

				   if (xsModel != null) {				   
					   setXsTypeDefinitionOnSequenceTypeExpr(xsModel, xpathSequenceTypeExpr, typeNamespace, typeName);
				   }
				   else {
					   throw new javax.xml.transform.TransformerException("FODC0005 : A valid XML schema could not be built, "
																						   + "from document available at uri " + url.toString() + " referenced "
																						   + "from xs:import-schema instruction.");
				   }
			   }
			   catch (URISyntaxException ex) {
				   throw new javax.xml.transform.TransformerException("FODC0005 : The schema uri specified with xsl:import-schema instruction "
						   																  + "is not a valid absolute uri, or cannot be resolved to an absolute uri.");   
			   }
			   catch (MalformedURLException ex) {
				   throw new javax.xml.transform.TransformerException("FODC0005 : The schema uri specified with xsl:import-schema instruction "
						   																  + "is not a valid absolute uri, or cannot be resolved to an absolute uri."); 
			   }						
		   }
	   }
   }
   
   /**
    * Set XSTypeDefinition object instance on an XPathSequenceTypeExpr object.  
    */
   private void setXsTypeDefinitionOnSequenceTypeExpr(XSModel xsModel, XPathSequenceTypeExpr xpathSequenceTypeExpr, String typeNamespace,
		   											  String typeName) throws TransformerException {
	   
	   XSTypeDefinition xsTypeDefinition = xsModel.getTypeDefinition(typeName, typeNamespace);
	   
	   if (xsTypeDefinition != null) {
		   if (!m_isFunctionArgumentParse && tokenIs(')')) {
			   consumeExpected(')');
		   }
		   xpathSequenceTypeExpr.setXsSequenceTypeDefinition(xsTypeDefinition);

		   // TO handle occurrence indicator
	   }
	   else {
		   String typeExpandedName = (typeNamespace == null) ? typeName : "{" + typeNamespace + "}:" + typeName;   
		   throw new javax.xml.transform.TransformerException("FODC0005 : The schema built via xs:import-schema instruction doesn't "
		   		                                                             + "contain a type definition with expanded name " + 
				                                                                 typeExpandedName + ".");							
	   }
   }
   
   /**
    * XPath parse of built-in XML Schema sequence type expressions.
    */
   private void parseXdmBuiltInXmlSchemaSequenceType(XPathSequenceTypeExpr xpathSequenceTypeExpr, 
		                                             boolean isXPathInlineFunctionParse) throws TransformerException {	  
 	  
 	  consumeExpected(XMLConstants.W3C_XML_SCHEMA_NS_URI);
 	  consumeExpected(':');
 	  
 	  switch (m_token) {
 	     case Keywords.FUNC_BOOLEAN_STRING :
 	        xpathSequenceTypeExpr.setBuiltInSequenceType(SequenceTypeSupport.BOOLEAN);
 	        break;
 	     case Keywords.XS_STRING :
 	        xpathSequenceTypeExpr.setBuiltInSequenceType(SequenceTypeSupport.STRING);
 	        break; 
 	     case Keywords.XS_NORMALIZED_STRING :
 	         xpathSequenceTypeExpr.setBuiltInSequenceType(SequenceTypeSupport.XS_NORMALIZED_STRING);
 	         break;
 	     case Keywords.XS_TOKEN :
 	         xpathSequenceTypeExpr.setBuiltInSequenceType(SequenceTypeSupport.XS_TOKEN);
 	         break;
 	     case Keywords.XS_DECIMAL :
 	        xpathSequenceTypeExpr.setBuiltInSequenceType(SequenceTypeSupport.XS_DECIMAL);
 	        break; 
 	     case Keywords.XS_FLOAT :
 	        xpathSequenceTypeExpr.setBuiltInSequenceType(SequenceTypeSupport.XS_FLOAT);
 	        break; 
 	     case Keywords.XS_DOUBLE :
 	        xpathSequenceTypeExpr.setBuiltInSequenceType(SequenceTypeSupport.XS_DOUBLE);
 	        break;
 	     case Keywords.XS_INTEGER :
 	        xpathSequenceTypeExpr.setBuiltInSequenceType(SequenceTypeSupport.XS_INTEGER);
 	        break;
 	     case Keywords.XS_NON_POSITIVE_INTEGER :
 		    xpathSequenceTypeExpr.setBuiltInSequenceType(SequenceTypeSupport.XS_NON_POSITIVE_INTEGER);
 		    break;
 	     case Keywords.XS_NEGATIVE_INTEGER :
 			xpathSequenceTypeExpr.setBuiltInSequenceType(SequenceTypeSupport.XS_NEGATIVE_INTEGER);
 			break;
 	     case Keywords.XS_NON_NEGATIVE_INTEGER :
 			xpathSequenceTypeExpr.setBuiltInSequenceType(SequenceTypeSupport.XS_NON_NEGATIVE_INTEGER);
 			break;
 	     case Keywords.XS_POSITIVE_INTEGER :
 			xpathSequenceTypeExpr.setBuiltInSequenceType(SequenceTypeSupport.XS_POSITIVE_INTEGER);
 			break;
 	     case Keywords.XS_LONG :
 	        xpathSequenceTypeExpr.setBuiltInSequenceType(SequenceTypeSupport.XS_LONG);
 	        break; 
 	     case Keywords.XS_INT :
 	        xpathSequenceTypeExpr.setBuiltInSequenceType(SequenceTypeSupport.XS_INT);
 	        break;
 	     case Keywords.XS_SHORT :
 		    xpathSequenceTypeExpr.setBuiltInSequenceType(SequenceTypeSupport.XS_SHORT);
 		    break;
 	     case Keywords.XS_BYTE :
 			xpathSequenceTypeExpr.setBuiltInSequenceType(SequenceTypeSupport.XS_BYTE);
 			break;
 	     case Keywords.XS_UNSIGNED_LONG :
 			xpathSequenceTypeExpr.setBuiltInSequenceType(SequenceTypeSupport.XS_UNSIGNED_LONG);
 			break;
 	     case Keywords.XS_UNSIGNED_INT :
 			xpathSequenceTypeExpr.setBuiltInSequenceType(SequenceTypeSupport.XS_UNSIGNED_INT);
 			break;
 	     case Keywords.XS_UNSIGNED_SHORT :
 			xpathSequenceTypeExpr.setBuiltInSequenceType(SequenceTypeSupport.XS_UNSIGNED_SHORT);
 			break;
 	     case Keywords.XS_UNSIGNED_BYTE :
 			xpathSequenceTypeExpr.setBuiltInSequenceType(SequenceTypeSupport.XS_UNSIGNED_BYTE);
 			break;
 	     case Keywords.XS_DATE :
 	        xpathSequenceTypeExpr.setBuiltInSequenceType(SequenceTypeSupport.XS_DATE);
 	        break;
 	     case Keywords.XS_DATETIME :
 	        xpathSequenceTypeExpr.setBuiltInSequenceType(SequenceTypeSupport.XS_DATETIME);
 	        break;
 	     case Keywords.XS_DURATION :
 	        xpathSequenceTypeExpr.setBuiltInSequenceType(SequenceTypeSupport.XS_DURATION);
 	        break;
 	     case Keywords.XS_YEAR_MONTH_DURATION :
 	        xpathSequenceTypeExpr.setBuiltInSequenceType(SequenceTypeSupport.XS_YEARMONTH_DURATION);
 	        break;
 	     case Keywords.XS_DAY_TIME_DURATION :
 	        xpathSequenceTypeExpr.setBuiltInSequenceType(SequenceTypeSupport.XS_DAYTIME_DURATION);
 	        break;
 	     case Keywords.XS_TIME :
 	         xpathSequenceTypeExpr.setBuiltInSequenceType(SequenceTypeSupport.XS_TIME);
 	         break;
 	     case Keywords.XS_GYEAR_MONTH :
	         xpathSequenceTypeExpr.setBuiltInSequenceType(SequenceTypeSupport.XS_GYEAR_MONTH);
	         break;
 	     case Keywords.XS_GYEAR :
	         xpathSequenceTypeExpr.setBuiltInSequenceType(SequenceTypeSupport.XS_GYEAR);
	         break;
 	     case Keywords.XS_GMONTH_DAY :
	         xpathSequenceTypeExpr.setBuiltInSequenceType(SequenceTypeSupport.XS_GMONTH_DAY);
	         break;
 	     case Keywords.XS_GDAY :
	         xpathSequenceTypeExpr.setBuiltInSequenceType(SequenceTypeSupport.XS_GDAY);
	         break;
 	     case Keywords.XS_GMONTH :
	         xpathSequenceTypeExpr.setBuiltInSequenceType(SequenceTypeSupport.XS_GMONTH);
	         break;
 	     case Keywords.XS_ANY_URI :
 	         xpathSequenceTypeExpr.setBuiltInSequenceType(SequenceTypeSupport.XS_ANY_URI);
 	         break;
 	     case Keywords.XS_QNAME :
 	         xpathSequenceTypeExpr.setBuiltInSequenceType(SequenceTypeSupport.XS_QNAME);
 	         break;
 	     case Keywords.XS_ANY_ATOMIC_TYPE :
 	         xpathSequenceTypeExpr.setBuiltInSequenceType(SequenceTypeSupport.XS_ANY_ATOMIC_TYPE);
 	         break;
 	     case Keywords.XS_UNTYPED_ATOMIC :
 	         xpathSequenceTypeExpr.setBuiltInSequenceType(SequenceTypeSupport.XS_UNTYPED_ATOMIC);
 	         break;
 	     case Keywords.XS_UNTYPED :
 	         xpathSequenceTypeExpr.setBuiltInSequenceType(SequenceTypeSupport.XS_UNTYPED);
 	         break;
 	     case Keywords.XS_BASE64BINARY :
 	        xpathSequenceTypeExpr.setBuiltInSequenceType(SequenceTypeSupport.XS_BASE64BINARY);
 	        break;
 	     case Keywords.XS_HEXBINARY :
 	        xpathSequenceTypeExpr.setBuiltInSequenceType(SequenceTypeSupport.XS_HEXBINARY);
 	        break;
 	     case Keywords.XS_LANGUAGE :
 	        xpathSequenceTypeExpr.setBuiltInSequenceType(SequenceTypeSupport.XS_LANGUAGE);
 	        break;
 	     case Keywords.XS_NAME :
 	        xpathSequenceTypeExpr.setBuiltInSequenceType(SequenceTypeSupport.XS_NAME);
 	        break;
 	     case Keywords.XS_NCNAME :
 	        xpathSequenceTypeExpr.setBuiltInSequenceType(SequenceTypeSupport.XS_NCNAME);
 	        break;
 	     case Keywords.XS_NMTOKEN :
 	    	xpathSequenceTypeExpr.setBuiltInSequenceType(SequenceTypeSupport.XS_NMTOKEN);
	        break;
 	     case Keywords.XS_ID :
 	    	xpathSequenceTypeExpr.setBuiltInSequenceType(SequenceTypeSupport.XS_ID);
	        break;
 	     case Keywords.XS_IDREF :
 	    	xpathSequenceTypeExpr.setBuiltInSequenceType(SequenceTypeSupport.XS_IDREF);
	        break;	        
 	     default :
 	        throw new javax.xml.transform.TransformerException("XPST0051 : An XML Schema type 'xs:" + m_token + "' is not "
 	                                                                                                + "recognized, within the provided sequence type expression.");        
 	  }
 	  
 	  nextToken();
 	  
 	  if ((m_token != null) && (xpathSequenceTypeExpr.getBuiltInSequenceType() > 0)) {
 	     setSequenceTypeOccurenceIndicator(xpathSequenceTypeExpr, isXPathInlineFunctionParse);
 	  }
   }
   
   /**
    * XPath parse of function item sequence type expressions.
    */
   private void parseFunctionItemSequenceType(XPathSequenceTypeExpr xpathSequenceTypeExpr) throws TransformerException {
 	
 	  SequenceTypeFunctionTest sequenceTypeFunctionTest = null;
 	  
 	  if (lookahead('(', 1) && lookahead('*', 2) && lookahead(')', 3)) {
 		  // sequence type FunctionTest of variety AnyFunctionTest
 		  sequenceTypeFunctionTest = new SequenceTypeFunctionTest();
 		  sequenceTypeFunctionTest.setIsAnyFunctionTest(true);
 		  nextToken();
 		  consumeExpected('(');
 	      consumeExpected('*');
 	      consumeExpected(')');
 	      xpathSequenceTypeExpr.setSequenceTypeFunctionTest(sequenceTypeFunctionTest);
 	  }
 	  else {
 		  // sequence type FunctionTest of variety TypedFunctionTest
 		  sequenceTypeFunctionTest = new SequenceTypeFunctionTest();
 		  nextToken();
 		  consumeExpected('(');
 		  List<String> typedFunctionTestParamSpecList = new ArrayList<String>();    		  
 		  if (!lookahead(')', 1)) {
 			 // There's at-least one parameter specification, for TypedFunctionTest
 			 String typedFunctionTestParamSpec = "";
 			 while (m_token != null) {
 				if (!(tokenIs(',') || tokenIs(')'))) {
 				   typedFunctionTestParamSpec = typedFunctionTestParamSpec + m_token;
 				   nextToken();
 				}
 				else if (tokenIs(',')) {
 				   if (!StringUtil.isStrHasBalancedParentheses(typedFunctionTestParamSpec, '(', ')')) {
 				      typedFunctionTestParamSpec = typedFunctionTestParamSpec + m_token;
 				      nextToken();
 				   }
 				   else {
 					  typedFunctionTestParamSpecList.add(typedFunctionTestParamSpec);
 					  typedFunctionTestParamSpec = "";
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
 					  typedFunctionTestParamSpec = typedFunctionTestParamSpec + m_token;
 					  nextToken(); 
 				   }
 				   else {
 					  typedFunctionTestParamSpecList.add(typedFunctionTestParamSpec);
 	 				  typedFunctionTestParamSpec = "";
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
 		  
 		  sequenceTypeFunctionTest.setTypedFunctionTestParamSpecList(typedFunctionTestParamSpecList);
 		  
 		  consumeExpected(')');
 		  consumeExpected("as");
 		  
 		  String typedFunctionTestReturnType = "";
 		  if (m_op_group_parse) {
 			  while (m_token != null && !(lookahead(null, 1) || lookahead(null, 2))) {
 				  typedFunctionTestReturnType = typedFunctionTestReturnType + m_token; 			    
 				  nextToken();
 			  }

 			  consumeExpected(')'); 
 		  }
 		  else {
 			  while (m_token != null) {
 				  typedFunctionTestReturnType = typedFunctionTestReturnType + m_token;
 				  nextToken();
 			  } 			  
 		  }
 		  
 		  if (!"".equals(typedFunctionTestReturnType)) {
 			 sequenceTypeFunctionTest.setTypedFunctionTestReturnType(typedFunctionTestReturnType);  
 		  }
 		  
 		  xpathSequenceTypeExpr.setSequenceTypeFunctionTest(sequenceTypeFunctionTest);
 	  }	  
    }

   /**
    * XPath parse of map sequence type expressions.
    */
   private void parseXdmMapSequenceType(XPathSequenceTypeExpr xpathSequenceTypeExpr, boolean isNested) throws TransformerException {
 	  
 	  SequenceTypeMapTest sequenceTypeMapTest = null;
 	  
 	  if (lookahead('(', 1) && lookahead('*', 2) && lookahead(')', 3)) {
 		  // sequence type MapTest with variety AnyMapTest
 		  sequenceTypeMapTest = new SequenceTypeMapTest();
 		  sequenceTypeMapTest.setIsAnyMapTest(true);
 		  nextToken();
 		  consumeExpected('(');
 	      consumeExpected('*');
 	      consumeExpected(')');
 	      xpathSequenceTypeExpr.setSequenceTypeMapTest(sequenceTypeMapTest);
 	  }
 	  else {
 		  // sequence type MapTest with variety TypedMapTest
 		  sequenceTypeMapTest = new SequenceTypeMapTest();
 		  nextToken();
 		  consumeExpected('(');
 		  
 		  SequenceTypeData keySequenceTypeData = new SequenceTypeData();    		  
 		  SequenceTypeData valueSequenceTypeData = new SequenceTypeData();
 		  
 		  while ((m_token != null) && !tokenIs(',')) {
 			 if (tokenIs(XMLConstants.W3C_XML_SCHEMA_NS_URI)) {
 			    consumeExpected(XMLConstants.W3C_XML_SCHEMA_NS_URI);
 			    consumeExpected(':'); 			    
 			    populateSequenceTypeData(keySequenceTypeData); 			 
 			    nextToken();
 		     }
 		  }
 		  
 		  consumeExpected(',');
 		  
 		  while ((m_token != null) && !tokenIs(')')) {
 			  if (tokenIs(XMLConstants.W3C_XML_SCHEMA_NS_URI)) {
 				  consumeExpected(XMLConstants.W3C_XML_SCHEMA_NS_URI);
 				  consumeExpected(':');
 				  populateSequenceTypeData(valueSequenceTypeData);    	             	         
 				  nextToken();     	         
 				  consumeExpected(')');
 			  }
 			  else if (tokenIs("map")) {
 				  XPathSequenceTypeExpr xpathSequenceTypeExpr2 = new XPathSequenceTypeExpr();  
 				  parseXdmMapSequenceType(xpathSequenceTypeExpr2, true);
 				  SequenceTypeMapTest seqTypeMapTest2 = xpathSequenceTypeExpr2.getSequenceTypeMapTest();
 				  valueSequenceTypeData.setSequenceTypeMapTest(seqTypeMapTest2);
 			  }
 			  else if (tokenIs("array")) {
 				  XPathSequenceTypeExpr xpathSequenceTypeExpr2 = new XPathSequenceTypeExpr();  
 				  parseXdmArraySequenceType(xpathSequenceTypeExpr2, true);
 				  SequenceTypeArrayTest seqTypeArrayTest2 = xpathSequenceTypeExpr2.getSequenceTypeArrayTest();
 				  valueSequenceTypeData.setSequenceTypeArrayTest(seqTypeArrayTest2);
 			  }
 		  }
 		  
 		  if (isNested) {
 		      consumeExpected(')');  
 	      } 		  		  
 		
 		  sequenceTypeMapTest.setKeySequenceTypeData(keySequenceTypeData);
 		  sequenceTypeMapTest.setValueSequenceTypeData(valueSequenceTypeData);
 		  
 		  xpathSequenceTypeExpr.setSequenceTypeMapTest(sequenceTypeMapTest);
 	   }
   }
   
   /**
    * XPath parse of array sequence type expressions.
    */
   private void parseXdmArraySequenceType(XPathSequenceTypeExpr xpathSequenceTypeExpr, boolean isNested) throws TransformerException {
 	  
 	  SequenceTypeArrayTest sequenceTypeArrayTest = null;
 	  
 	  if (lookahead('(', 1) && lookahead('*', 2) && lookahead(')', 3)) {
 		  // sequence type ArrayTest with variety AnyArrayTest
 		  sequenceTypeArrayTest = new SequenceTypeArrayTest();
 		  sequenceTypeArrayTest.setIsAnyArrayTest(true);
 		  nextToken();
 		  consumeExpected('(');
 	      consumeExpected('*');
 	      consumeExpected(')');
 	      xpathSequenceTypeExpr.setSequenceTypeArrayTest(sequenceTypeArrayTest);
 	  }
 	  else {
 		  // sequence type ArrayTest with variety TypedArrayTest
 		  sequenceTypeArrayTest = new SequenceTypeArrayTest();
 		  nextToken();
 		  consumeExpected('(');
 		  
 		  SequenceTypeData arrayItemSequenceType = new SequenceTypeData();
 		  while ((m_token != null) && !tokenIs(')')) {
 			 if (tokenIs(XMLConstants.W3C_XML_SCHEMA_NS_URI)) {
 			    consumeExpected(XMLConstants.W3C_XML_SCHEMA_NS_URI);
 			    consumeExpected(':'); 			     			    
 			    if (lookahead(SequenceTypeSupport.Q_MARK, 1)) {
 			    	populateSequenceTypeData(arrayItemSequenceType); 			    	
 			    	arrayItemSequenceType.setItemTypeOccurrenceIndicator(SequenceTypeSupport.OccurrenceIndicator.ZERO_OR_ONE);
 			    	nextToken();
 			    	nextToken();
 			    }
 			    else if (lookahead(SequenceTypeSupport.STAR, 1)) {
 			    	populateSequenceTypeData(arrayItemSequenceType);
 			    	arrayItemSequenceType.setItemTypeOccurrenceIndicator(SequenceTypeSupport.OccurrenceIndicator.ZERO_OR_MANY);
 			    	nextToken();
 			    	nextToken();
 			    }
 			    else if (lookahead(SequenceTypeSupport.PLUS, 1)) {
 			    	populateSequenceTypeData(arrayItemSequenceType);
 			    	arrayItemSequenceType.setItemTypeOccurrenceIndicator(SequenceTypeSupport.OccurrenceIndicator.ONE_OR_MANY);
 			    	nextToken();
 			    	nextToken();
 			    }
 			    else {
 			    	populateSequenceTypeData(arrayItemSequenceType);
 			    	nextToken();
 			    }
 		     }
 			 else if (tokenIs("map")) {
 				XPathSequenceTypeExpr xpathSequenceTypeExpr2 = new XPathSequenceTypeExpr();  
				parseXdmMapSequenceType(xpathSequenceTypeExpr2, true);
				SequenceTypeMapTest seqTypeMapTest = xpathSequenceTypeExpr2.getSequenceTypeMapTest();
				arrayItemSequenceType.setSequenceTypeMapTest(seqTypeMapTest);
 			 }
 			 else if (tokenIs("array")) {
 				XPathSequenceTypeExpr xpathSequenceTypeExpr2 = new XPathSequenceTypeExpr();  
				parseXdmArraySequenceType(xpathSequenceTypeExpr2, true);
				SequenceTypeArrayTest seqTypeArrTest = xpathSequenceTypeExpr2.getSequenceTypeArrayTest();
				arrayItemSequenceType.setSequenceTypeArrayTest(seqTypeArrTest);
 			 }
 			 else if (tokenIs("item")) {
 				nextToken();
 				SequenceTypeKindTest seqTypeKindTest = new SequenceTypeKindTest();
 				seqTypeKindTest.setKindVal(SequenceTypeSupport.ITEM_KIND);
 				arrayItemSequenceType.setSequenceTypeKindTest(seqTypeKindTest);
 				consumeExpected('(');
 				consumeExpected(')');
 				if (tokenIs(SequenceTypeSupport.Q_MARK)) { 			    	
 			    	arrayItemSequenceType.setItemTypeOccurrenceIndicator(SequenceTypeSupport.OccurrenceIndicator.ZERO_OR_ONE);
 			    	nextToken();
 			    }
 			    else if (tokenIs(SequenceTypeSupport.STAR)) {
 			    	arrayItemSequenceType.setItemTypeOccurrenceIndicator(SequenceTypeSupport.OccurrenceIndicator.ZERO_OR_MANY);
 			    	nextToken();
 			    }
 			    else if (tokenIs(SequenceTypeSupport.PLUS)) {
 			    	arrayItemSequenceType.setItemTypeOccurrenceIndicator(SequenceTypeSupport.OccurrenceIndicator.ONE_OR_MANY);
 			    	nextToken();
 			    }
 			 }
 		  }
 		  
 		  if (!tokenIs(null)) {
 		     consumeExpected(')');
 		  }
 		  
 		  sequenceTypeArrayTest.setArrayItemTypeInfo(arrayItemSequenceType);  	    	  
 		  xpathSequenceTypeExpr.setSequenceTypeArrayTest(sequenceTypeArrayTest); 
 	  }
   }

   /**
    * Method definition, to support XPath parse of map and array sequence
    * type expressions.
    */
   private void populateSequenceTypeData(SequenceTypeData seqTypeData) throws TransformerException {
 	  
 	switch (m_token) {
 	    case Keywords.FUNC_BOOLEAN_STRING :
 	        seqTypeData.setBuiltInSequenceType(SequenceTypeSupport.BOOLEAN);
 	        break;
 	    case Keywords.XS_STRING :
 	    	seqTypeData.setBuiltInSequenceType(SequenceTypeSupport.STRING);
 	        break; 
 	    case Keywords.XS_NORMALIZED_STRING :
 	    	seqTypeData.setBuiltInSequenceType(SequenceTypeSupport.XS_NORMALIZED_STRING);
 	        break;
 	    case Keywords.XS_TOKEN :
 	    	seqTypeData.setBuiltInSequenceType(SequenceTypeSupport.XS_TOKEN);
 	        break;
 	    case Keywords.XS_DECIMAL :
 	    	seqTypeData.setBuiltInSequenceType(SequenceTypeSupport.XS_DECIMAL);
 	        break; 
 	    case Keywords.XS_FLOAT :
 	    	seqTypeData.setBuiltInSequenceType(SequenceTypeSupport.XS_FLOAT);
 	        break; 
 	    case Keywords.XS_DOUBLE :
 	    	seqTypeData.setBuiltInSequenceType(SequenceTypeSupport.XS_DOUBLE);
 	        break;
 	    case Keywords.XS_INTEGER :
 	    	seqTypeData.setBuiltInSequenceType(SequenceTypeSupport.XS_INTEGER);
 	        break;
 	    case Keywords.XS_NON_POSITIVE_INTEGER :
 	    	seqTypeData.setBuiltInSequenceType(SequenceTypeSupport.XS_NON_POSITIVE_INTEGER);
 	        break;
 	    case Keywords.XS_NEGATIVE_INTEGER :
 	    	seqTypeData.setBuiltInSequenceType(SequenceTypeSupport.XS_NEGATIVE_INTEGER);
 	        break;
 	    case Keywords.XS_NON_NEGATIVE_INTEGER :
 	    	seqTypeData.setBuiltInSequenceType(SequenceTypeSupport.XS_NON_NEGATIVE_INTEGER);
 	        break;
 	    case Keywords.XS_POSITIVE_INTEGER :
 	    	seqTypeData.setBuiltInSequenceType(SequenceTypeSupport.XS_POSITIVE_INTEGER);
 	        break;
 	    case Keywords.XS_LONG :
 	    	seqTypeData.setBuiltInSequenceType(SequenceTypeSupport.XS_LONG);
 	        break; 
 	    case Keywords.XS_INT :
 	    	seqTypeData.setBuiltInSequenceType(SequenceTypeSupport.XS_INT);
 	        break;
 	    case Keywords.XS_SHORT :
 	    	seqTypeData.setBuiltInSequenceType(SequenceTypeSupport.XS_SHORT);
 	        break;
 	    case Keywords.XS_BYTE :
 	    	seqTypeData.setBuiltInSequenceType(SequenceTypeSupport.XS_BYTE);
 	        break;
 	    case Keywords.XS_UNSIGNED_LONG :
 	    	seqTypeData.setBuiltInSequenceType(SequenceTypeSupport.XS_UNSIGNED_LONG);
 	        break;
 	    case Keywords.XS_UNSIGNED_INT :
 	    	seqTypeData.setBuiltInSequenceType(SequenceTypeSupport.XS_UNSIGNED_INT);
 	        break;
 	    case Keywords.XS_UNSIGNED_SHORT :
 	    	seqTypeData.setBuiltInSequenceType(SequenceTypeSupport.XS_UNSIGNED_SHORT);
 	        break;
 	    case Keywords.XS_UNSIGNED_BYTE :
 	    	seqTypeData.setBuiltInSequenceType(SequenceTypeSupport.XS_UNSIGNED_BYTE);
 	        break;
 	    case Keywords.XS_DATE :
 	    	seqTypeData.setBuiltInSequenceType(SequenceTypeSupport.XS_DATE);
 	        break;
 	    case Keywords.XS_DATETIME :
 	    	seqTypeData.setBuiltInSequenceType(SequenceTypeSupport.XS_DATETIME);
 	        break;
 	    case Keywords.XS_DURATION :
 	    	seqTypeData.setBuiltInSequenceType(SequenceTypeSupport.XS_DURATION);
 	        break;
 	    case Keywords.XS_YEAR_MONTH_DURATION :
 	    	seqTypeData.setBuiltInSequenceType(SequenceTypeSupport.XS_YEARMONTH_DURATION);
 	        break;
 	    case Keywords.XS_DAY_TIME_DURATION :
 	    	seqTypeData.setBuiltInSequenceType(SequenceTypeSupport.XS_DAYTIME_DURATION);
 	        break;
 	    case Keywords.XS_TIME :
 	    	seqTypeData.setBuiltInSequenceType(SequenceTypeSupport.XS_TIME);
 	        break;
 	    case Keywords.XS_GYEAR_MONTH :
	    	seqTypeData.setBuiltInSequenceType(SequenceTypeSupport.XS_GYEAR_MONTH);
	        break;
 	    case Keywords.XS_GYEAR :
	    	seqTypeData.setBuiltInSequenceType(SequenceTypeSupport.XS_GYEAR);
	        break;
 	    case Keywords.XS_GMONTH_DAY :
	    	seqTypeData.setBuiltInSequenceType(SequenceTypeSupport.XS_GMONTH_DAY);
	        break;
 	    case Keywords.XS_GDAY :
 	    	 seqTypeData.setBuiltInSequenceType(SequenceTypeSupport.XS_GDAY);
	         break;
	     case Keywords.XS_GMONTH :
	    	 seqTypeData.setBuiltInSequenceType(SequenceTypeSupport.XS_GMONTH);
	         break;
 	    case Keywords.XS_ANY_URI :
 	    	seqTypeData.setBuiltInSequenceType(SequenceTypeSupport.XS_ANY_URI);
 	        break;
 	    case Keywords.XS_QNAME :
 	    	seqTypeData.setBuiltInSequenceType(SequenceTypeSupport.XS_QNAME);
 	        break;
 	    case Keywords.XS_ANY_ATOMIC_TYPE :
 	    	seqTypeData.setBuiltInSequenceType(SequenceTypeSupport.XS_ANY_ATOMIC_TYPE);
 	        break;
 	    case Keywords.XS_UNTYPED_ATOMIC :
 	    	seqTypeData.setBuiltInSequenceType(SequenceTypeSupport.XS_UNTYPED_ATOMIC);
 	        break;
 	    case Keywords.XS_UNTYPED :
 	    	seqTypeData.setBuiltInSequenceType(SequenceTypeSupport.XS_UNTYPED);
 	        break;
 	    case Keywords.XS_BASE64BINARY :
	    	seqTypeData.setBuiltInSequenceType(SequenceTypeSupport.XS_BASE64BINARY);
	        break;
 	    case Keywords.XS_HEXBINARY :
	    	seqTypeData.setBuiltInSequenceType(SequenceTypeSupport.XS_HEXBINARY);
	        break;
 	   case Keywords.XS_LANGUAGE :
 		    seqTypeData.setBuiltInSequenceType(SequenceTypeSupport.XS_LANGUAGE);
	        break;
 	   case Keywords.XS_NAME :
 		    seqTypeData.setBuiltInSequenceType(SequenceTypeSupport.XS_NAME);
	        break;
 	   case Keywords.XS_NCNAME :
		    seqTypeData.setBuiltInSequenceType(SequenceTypeSupport.XS_NCNAME);
	        break;
 	   case Keywords.XS_NMTOKEN :
		    seqTypeData.setBuiltInSequenceType(SequenceTypeSupport.XS_NMTOKEN);
	        break;
 	   case Keywords.XS_ID :
		    seqTypeData.setBuiltInSequenceType(SequenceTypeSupport.XS_ID);
	        break;
 	   case Keywords.XS_IDREF :
		    seqTypeData.setBuiltInSequenceType(SequenceTypeSupport.XS_IDREF);
	        break;	        
 	   default :
 	        throw new javax.xml.transform.TransformerException("XPST0051 : An XML Schema type 'xs:" + m_token + "' is not "
 	                                                                                                + "recognized, within the provided sequence type expression.");        
       }
   }
   
   /**
    * At various times during an XPath expression parse, the current parse
    * attempt having occurred upto a point in token queue, needs to be 
    * discarded and another parse choice has to be explored.
    * 
    * An object of this class, saves information about a specific XPath 
    * parse position.
    * 
    * Mukul Gandhi <mukulg@apache.org>
    */
   private class TokenQueueScanPosition {
	   
	  private int queueMark;
	  
	  private char tokenChar;
	  
	  private String token;
	  
	  /**
	   * Class constructor.
	   */
	  public TokenQueueScanPosition(int queueMark, char tokenChar, 
			                        String token) {
		 this.queueMark = queueMark;
		 this.tokenChar = tokenChar;
		 this.token = token;
	  }

	  public int getQueueMark() {
		 return queueMark;
	  }

	  public void setQueueMark(int queueMark) {
		 this.queueMark = queueMark;
	  }

	  public char getTokenChar() {
		 return tokenChar;
	  }

	  public void setTokenChar(char tokenChar) {
		 this.tokenChar = tokenChar;
	  }

	  public String getToken() {
		 return token;
	  }

	  public void setToken(String token) {
		 this.token = token;
	  }
   }
   
   /**
    * Restore XPath parse position to, a particular previous saved state.
    */
   private void restoreTokenQueueScanPosition(TokenQueueScanPosition tokQueueScanPosition) {
	  m_queueMark = tokQueueScanPosition.getQueueMark();
	  m_tokenChar = tokQueueScanPosition.getTokenChar();
	  m_token = tokQueueScanPosition.getToken();	
   }

   public XPathExprFunctionSuffix getXPathExprFunctionSuffix() {
	   return m_xpathExprFunctionSuffix;
   }

   public void setXPathExprFunctionSuffix(XPathExprFunctionSuffix xpathExprFunctionSuffix) {
	   this.m_xpathExprFunctionSuffix = xpathExprFunctionSuffix;
   }
   
   /**
    * Method definition to check, whether the supplied string value has 
    * an XPath axis name prefix.
    * 
    * @param strValue			Supplied string value
    * @return					Boolean true if the supplied string value has an 
    *                           XPath axis name prefix.
    */
   public static boolean isStrHasXPathAxisNamePrefix(String strValue) {
	   
	   boolean result = false;

	   int idx = strValue.indexOf("::");
	   if (idx >= 0) {
		   String xpathAxisStrValue = strValue.substring(0, idx);
		   switch (xpathAxisStrValue) {
		   case Keywords.FROM_ANCESTORS_STRING:
			   result = true;
			   break;
		   case Keywords.FROM_ANCESTORS_OR_SELF_STRING:
			   result = true;
			   break;
		   case Keywords.FROM_ATTRIBUTES_STRING:
			   result = true;
			   break;
		   case Keywords.FROM_CHILDREN_STRING:
			   result = true;
			   break;
		   case Keywords.FROM_DESCENDANTS_STRING:
			   result = true;
			   break;
		   case Keywords.FROM_DESCENDANTS_OR_SELF_STRING:
			   result = true;
			   break;
		   case Keywords.FROM_FOLLOWING_STRING:
			   result = true;
			   break;
		   case Keywords.FROM_FOLLOWING_SIBLINGS_STRING:
			   result = true;
			   break;
		   case Keywords.FROM_PARENT_STRING:
			   result = true;
			   break;
		   case Keywords.FROM_PRECEDING_STRING:
			   result = true;
			   break;
		   case Keywords.FROM_PRECEDING_SIBLINGS_STRING:
			   result = true;
			   break;
		   case Keywords.FROM_SELF_STRING:
			   result = true;
			   break;
		   case Keywords.FROM_NAMESPACE_STRING:
			   result = true;
			   break;
		   default:	   
			   // no op
		   }
	   }

	   return result;	   
   }
   
   /**
    * Method definition to check, whether there's an XPath built-in 
    * node pattern like node(), doument-node(), text(), comment() as 
    * a suffix of XPath expression string. XPath node checks for these 
    * patterns with predicate as suffix are also done.
    */
   private boolean isXPathPatternExcludeTrailingNodeFunctions() {
	   
	  boolean isExclude = false;
	  
	  String currXPathPattern = m_ops.m_currentPattern;
	  if (currXPathPattern.endsWith("node()") || currXPathPattern.endsWith("document-node()") || 
			                                     currXPathPattern.endsWith("text()") || 
			                                     currXPathPattern.endsWith("comment()")) {
		  isExclude = true; 
	  }
	  else {
		  String[] spltResult = currXPathPattern.split("node\\(\\)[\\s]*[\\|][\\s]*");
		  if (spltResult.length > 1) {
			 isExclude = true; 
		  }
		  if (!isExclude) {
			 spltResult = currXPathPattern.split("comment\\(\\)\\[.*\\],");
			 if (spltResult.length > 1) {
				isExclude = true; 
			 }
		  }
	  }
	  
	  return isExclude;
   }
   
   /**
    * Check whether the token string is one of the node combining tokens.
    */
   private boolean isTokenNodeCombining(String tokenStr) {
	   
 	  boolean result = false;	  
 	  
 	  if (tokenStr != null) {
 		  if (((tokenStr.equals("|") && !tokenStr.equals("||")) || tokenStr.equals("union"))
 				  										|| tokenStr.equals("intersect")
 				  										|| tokenStr.equals("except")) {
 			  result = true; 
 		  }
 	  }
 	  
 	  return result;
   }
   
   /**
    * XPath parse of literal sequence constructor, which is an 
    * RHS operand of XPath binary operations like +, -, *, div,
    * idiv, mod, quo.
    */
   private int handleXPathParseRhsSequenceOperand(int addPos, int opCode) throws TransformerException {
	   
 	  int result = 0;
 	  
 	  m_prevTokQueueScanPosition = new TokenQueueScanPosition(m_queueMark, m_tokenChar, m_token);           
 	  m_isSequenceOperand = true;
 	  
 	  Expr();
 	  
 	  if (!m_isSequenceOperand) {
 		  // The method Expr() has set variable m_isSequenceOperand to false
 		  
 		  restoreTokenQueueScanPosition(m_prevTokQueueScanPosition);

 		  int opPlusLeftHandLen = m_ops.getOp(OpMap.MAPINDEX_LENGTH) - addPos;

 		  if ((opCode == OpCodes.OP_PLUS) || (opCode == OpCodes.OP_MINUS)) {
 		     result = AdditiveExpr(addPos);
 		  }
 		  else {
 			 result = MultiplicativeExpr(addPos);
 		  }

 		  m_ops.setOp(addPos + OpMap.MAPINDEX_LENGTH, 
 			m_ops.getOp(addPos + opPlusLeftHandLen + 1) + opPlusLeftHandLen);
 		  result += 2;
 	  }
 	  else {
 		  m_isSequenceOperand = false;
 	  }
 	  
 	  return result;
   }

    /**
    * XPath parse of named function reference with function reference 
    * name being namespace qualified.
    * 
    * @param opPos
    * @throws TransformerException
    */
	private void handleXPathParseNamedFuncRefWithNSQual(int opPos) throws TransformerException {				    	  

		if (tokenIs(FunctionTable.XPATH_BUILT_IN_FUNCS_NS_URI) || tokenIs(FunctionTable.XPATH_BUILT_IN_MATH_FUNCS_NS_URI) ||
																  tokenIs(FunctionTable.XPATH_BUILT_IN_MAP_FUNCS_NS_URI) || 
				                                                  tokenIs(FunctionTable.XPATH_BUILT_IN_ARRAY_FUNCS_NS_URI)) {
			String funcNamespaceUri = m_token;
			String nextTokenToAnalyze = getTokenRelative(1);
			
			TokenQueueScanPosition prevTokQueueScanPosition = new TokenQueueScanPosition(m_queueMark, m_tokenChar, m_token);
			
			if ((nextTokenToAnalyze != null) && (nextTokenToAnalyze.contains("#")) 
											 && xslFunctionService.isFuncArityWellFormed(nextTokenToAnalyze)) {
				nextToken();
				consumeExpected(':');
				String funcName = m_token.substring(0, m_token.indexOf('#'));
				String namedFuncRef = m_token;										
				nextToken();					
				insertOp(opPos, 2, OpCodes.OP_NAMED_FUNCTION_REFERENCE);

				m_xpathNamedFunctionReference = new XPathNamedFunctionReference();
				m_xpathNamedFunctionReference.setFuncName(funcName);
				m_xpathNamedFunctionReference.setFuncNamespace(funcNamespaceUri);					
				String funcArityStr = namedFuncRef.substring(namedFuncRef.indexOf('#') + 1);
				m_xpathNamedFunctionReference.setArity(Short.valueOf(funcArityStr));

				m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH, 
						                    m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);			
			}
			else {
				restoreTokenQueueScanPosition(prevTokQueueScanPosition);

				ExprSingle();
			}
		}
		else if (tokenIs(XMLConstants.W3C_XML_SCHEMA_NS_URI)) {
			String funcNamespaceUri = m_token;
			String nextTokenToAnalyze = getTokenRelative(1);
			
			TokenQueueScanPosition prevTokQueueScanPosition = new TokenQueueScanPosition(m_queueMark, m_tokenChar, m_token);
			
			if ((nextTokenToAnalyze != null) && (nextTokenToAnalyze.contains("#")) 
											 && xslFunctionService.isFuncArityWellFormed(nextTokenToAnalyze)) {
				nextToken();
				consumeExpected(':');
				String funcName = m_token.substring(0, m_token.indexOf('#'));
				String namedFuncRef = m_token;										
				nextToken();					
				insertOp(opPos, 2, OpCodes.OP_NAMED_FUNCTION_REFERENCE);

				m_xpathNamedFunctionReference = new XPathNamedFunctionReference();
				m_xpathNamedFunctionReference.setFuncName(funcName);
				m_xpathNamedFunctionReference.setFuncNamespace(funcNamespaceUri);					
				String funcArityStr = namedFuncRef.substring(namedFuncRef.indexOf('#') + 1);
				m_xpathNamedFunctionReference.setArity(Short.valueOf(funcArityStr));

				m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH, 
						m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);
			}
			else {
				restoreTokenQueueScanPosition(prevTokQueueScanPosition);

				ExprSingle();
			}
		}
		else if (lookahead(':', 1)) {
			TokenQueueScanPosition prevTokQueueScanPosition = new TokenQueueScanPosition(m_queueMark, m_tokenChar, m_token);
			
			StylesheetRoot stylesheetRoot = XslTransformData.m_stylesheetRoot;
			String funcNamespaceUri = m_token;			
			nextToken();
			consumeExpected(':');
			
			try {
				int indexOfHash = m_token.indexOf('#');
				String funcName = m_token.substring(0, indexOfHash);
				int arity = Integer.valueOf(m_token.substring(indexOfHash + 1)); 				
				ElemFunction elemFunction = stylesheetRoot.getXslFunction(new org.apache.xml.utils.QName(funcNamespaceUri, 
						                                                                                               funcName), arity);
				if (elemFunction != null) {
					String namedFuncRef = m_token;										
					nextToken();					
					insertOp(opPos, 2, OpCodes.OP_NAMED_FUNCTION_REFERENCE);

					m_xpathNamedFunctionReference = new XPathNamedFunctionReference();
					m_xpathNamedFunctionReference.setFuncName(funcName);
					m_xpathNamedFunctionReference.setFuncNamespace(funcNamespaceUri);					
					String funcArityStr = namedFuncRef.substring(namedFuncRef.indexOf('#') + 1);
					m_xpathNamedFunctionReference.setArity(Short.valueOf(funcArityStr));
					m_xpathNamedFunctionReference.setXslStylesheetFunction(elemFunction, stylesheetRoot);

					m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH, 
							m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);

				}
				else {
					error("XPath parse of named function reference failed. XSL stylesheet expected an existing user "
							                           + "defined stylesheet function at this stylesheet location.", new Object[]{});
				}
			}
			catch (Exception ex) {
			   restoreTokenQueueScanPosition(prevTokQueueScanPosition);
			   
			   ExprSingle();
			}
		}
		else {
			ExprSingle();
		}
	}
	
	/**
	 * XPath parse of named function reference with function reference 
	 * name not being namespace qualified.
	 * 
	 * @param opPos
	 * @throws TransformerException
	 */
	private void handleXPathParseNamedFuncRefWithoutNSQual(int opPos) throws TransformerException {

		TokenQueueScanPosition prevTokQueueScanPosition = new TokenQueueScanPosition(m_queueMark, m_tokenChar, m_token);

		String funcName = m_token.substring(0, m_token.indexOf('#'));
		int funcTok = getFunctionToken(funcName, FunctionTable.XPATH_BUILT_IN_FUNCS_NS_URI);
		if (funcTok >= 0) {
			String namedFuncRef = m_token;
			nextToken();
			insertOp(opPos, 2, OpCodes.OP_NAMED_FUNCTION_REFERENCE);

			m_xpathNamedFunctionReference = new XPathNamedFunctionReference();
			m_xpathNamedFunctionReference.setFuncName(funcName);
			m_xpathNamedFunctionReference.setFuncNamespace(FunctionTable.XPATH_BUILT_IN_FUNCS_NS_URI);			
			String funcArityStr = namedFuncRef.substring(namedFuncRef.indexOf('#') + 1);
			m_xpathNamedFunctionReference.setArity(Short.valueOf(funcArityStr));

			m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH, 
			        m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);
		}
		else {
			restoreTokenQueueScanPosition(prevTokQueueScanPosition);

			ExprSingle();
		}
	}
	
	/**
	  * Method definition to mutate XPathDynamicFunctionCall object reference,
	  * to handle XPath parse of expressions like $func1(args)(2,3) i.e,
	  * an XPath dynamic function call reference returning a function item
	  * which is subsequence called with function call arguments.
	  * 
	  * @param argList						   A list of XPath string values for the 
	  *                                        argument of the left most XPath dynamic 
	  *                                        function call.
	  * @param xpathDynamicFunctionCall        An XPathDynamicFunctionCall object reference
	  *                                        that needs to be mutated with further information.
	  */
	private void mutateXPathDynamicFuncCallReference(XPathDynamicFunctionCall xpathDynamicFunctionCall, 
			                                         List<String> argList) {
		   
		  List<String> argListBackup = new ArrayList<String>();

		  for (int idx = 0; idx < argList.size(); idx++) {
			  String str1 = argList.get(idx);
			  argListBackup.add(str1);
		  }

		  int noOfArgs = argList.size();
		  String lastArgStr = argList.get(noOfArgs - 1);                     

		  int i = lastArgStr.lastIndexOf(')');       
		  if (i > -1) {
			  boolean isExceptionOccured = false;
			  try {
				  String lastArgEffectiveStr = lastArgStr.substring(0, i);
				  argList.set(noOfArgs - 1, lastArgEffectiveStr);
				  ObjectVector tokenQueue = m_ops.getTokenQueue();
				  int tokenQueueSize = tokenQueue.size();
				  String lastTokenStr = (tokenQueue.elementAt(tokenQueueSize - 1)).toString();
				  if (")".equals(lastTokenStr)) {
					  char c1 = lastArgStr.charAt(i + 1);
					  if (c1 == '(') {
						  String trailingInfoStr = lastArgStr.substring(i + 2);
						  String[] trailingInfoStrParts = trailingInfoStr.split(",");
						  List<String> list1 = Arrays.asList(trailingInfoStrParts);
						  xpathDynamicFunctionCall.setTrailingArgList(list1);
					  }
				  }
			  }
			  catch (StringIndexOutOfBoundsException ex) {
				  isExceptionOccured = true;
			  }

			  if (isExceptionOccured) {
				  xpathDynamicFunctionCall.setArgList(argListBackup);
				  xpathDynamicFunctionCall.setTrailingArgList(null);
			  }
			  else {
				  String str1 = argList.get(noOfArgs - 1);
				  int count1 = 0;
				  int count2 = 0;
				  for (int idx = 0; idx < str1.length(); idx++) {
					  if (str1.charAt(idx) == '(') {
						  count1++; 
					  }
					  else if (str1.charAt(idx) == ')') {
						  count2++; 
					  }
				  }

				  if (count1 != count2) {
					  xpathDynamicFunctionCall.setArgList(argListBackup);
					  xpathDynamicFunctionCall.setTrailingArgList(null); 
				  }
			  }
		  }
	}
	
	/**
     * Method definition to get, XML attribute xpath-default-namespace's effective
     * value for the current XPath expression evaluation, by searching information
     * on an XSL current node and if not found their then information within the
     * ancestor nodes upto StylesheetRoot object.
     * 
     * The search for effective value of attribute xpath-default-namespace completes
     * at the nearest XSL parent node where a non-null value of xpath-default-namespace 
     * is found.  
     * 
     * @param xpathExprXslParentNode			    An XSL supplied non-expression parent node of
     *                                              the XPath expression.
     * @return										An attribute xpath-default-namespace's effective 
     *                                              value for XPath expression.
     */
    public static String getXPathDefaultNamespace(ElemTemplateElement xpathExprXslParentNode) {

    	String result = null;

    	if (xpathExprXslParentNode instanceof StylesheetRoot) {
    		result = ((StylesheetRoot)xpathExprXslParentNode).getXpathDefaultNamespace(); 
    	}
    	else if (xpathExprXslParentNode instanceof ElemVariable) {
    		result = ((ElemVariable)xpathExprXslParentNode).getXpathDefaultNamespace();
    		if (result == null) {
    			result = getXPathDefaultNamespace(((ElemVariable)xpathExprXslParentNode).getParentElem()); 
    		}
    	}
    	else if (xpathExprXslParentNode instanceof ElemFunction) {
    		result = ((ElemFunction)xpathExprXslParentNode).getXpathDefaultNamespace();
    		if (result == null) {
    			result = getXPathDefaultNamespace(((ElemFunction)xpathExprXslParentNode).getParentElem()); 
    		}
    	}
    	else if (xpathExprXslParentNode instanceof ElemTemplate) {
    		result = ((ElemTemplate)xpathExprXslParentNode).getXpathDefaultNamespace();
    		if (result == null) {
    			result = getXPathDefaultNamespace(((ElemTemplate)xpathExprXslParentNode).getParentElem()); 
    		}
    	}
    	else if (xpathExprXslParentNode instanceof ElemApplyTemplates) {
    		result = ((ElemApplyTemplates)xpathExprXslParentNode).getXpathDefaultNamespace();
    		if (result == null) {
    			result = getXPathDefaultNamespace(((ElemApplyTemplates)xpathExprXslParentNode).getParentElem()); 
    		}
    	}
    	else if (xpathExprXslParentNode instanceof ElemForEach) {
    		result = ((ElemForEach)xpathExprXslParentNode).getXpathDefaultNamespace();
    		if (result == null) {
    			result = getXPathDefaultNamespace(((ElemForEach)xpathExprXslParentNode).getParentElem()); 
    		}
    	}
    	else if (xpathExprXslParentNode instanceof ElemForEachGroup) {
    		result = ((ElemForEachGroup)xpathExprXslParentNode).getXpathDefaultNamespace();
    		if (result == null) {
    			result = getXPathDefaultNamespace(((ElemForEachGroup)xpathExprXslParentNode).getParentElem()); 
    		}
    	}
    	else if (xpathExprXslParentNode instanceof ElemIterate) {
    		result = ((ElemIterate)xpathExprXslParentNode).getXpathDefaultNamespace();
    		if (result == null) {
    			result = getXPathDefaultNamespace(((ElemIterate)xpathExprXslParentNode).getParentElem()); 
    		}
    	}
    	else if (xpathExprXslParentNode instanceof ElemValueOf) {
    		result = ((ElemValueOf)xpathExprXslParentNode).getXpathDefaultNamespace();
    		if (result == null) {
    			result = getXPathDefaultNamespace(((ElemValueOf)xpathExprXslParentNode).getParentElem()); 
    		}
    	}
    	else if (xpathExprXslParentNode instanceof ElemCopyOf) {
    		result = ((ElemCopyOf)xpathExprXslParentNode).getXpathDefaultNamespace();
    		if (result == null) {
    			result = getXPathDefaultNamespace(((ElemCopyOf)xpathExprXslParentNode).getParentElem()); 
    		}
    	}
    	else if (xpathExprXslParentNode instanceof ElemCopy) {
    		result = ((ElemCopy)xpathExprXslParentNode).getXpathDefaultNamespace();
    		if (result == null) {
    			result = getXPathDefaultNamespace(((ElemCopy)xpathExprXslParentNode).getParentElem()); 
    		}
    	}
    	else if (xpathExprXslParentNode instanceof ElemLiteralResult) {
    		result = ((ElemLiteralResult)xpathExprXslParentNode).getXpathDefaultNamespace();
    		if (result == null) {
    			result = getXPathDefaultNamespace(((ElemLiteralResult)xpathExprXslParentNode).getParentElem()); 
    		}
    	}
    	else if (xpathExprXslParentNode instanceof ElemChoose) {
    		result = ((ElemChoose)xpathExprXslParentNode).getXpathDefaultNamespace();
    		if (result == null) {
    			result = getXPathDefaultNamespace(((ElemChoose)xpathExprXslParentNode).getParentElem()); 
    		}
    	}
    	else if (xpathExprXslParentNode instanceof ElemWhen) {
    		result = ((ElemWhen)xpathExprXslParentNode).getXpathDefaultNamespace();
    		if (result == null) {
    			result = getXPathDefaultNamespace(((ElemWhen)xpathExprXslParentNode).getParentElem()); 
    		}
    	}
    	else if (xpathExprXslParentNode instanceof ElemOtherwise) {
    		result = ((ElemOtherwise)xpathExprXslParentNode).getXpathDefaultNamespace();
    		if (result == null) {
    			result = getXPathDefaultNamespace(((ElemOtherwise)xpathExprXslParentNode).getParentElem()); 
    		}
    	}
    	else if (xpathExprXslParentNode instanceof ElemIf) {
    		result = ((ElemIf)xpathExprXslParentNode).getXpathDefaultNamespace();
    		if (result == null) {
    			result = getXPathDefaultNamespace(((ElemIf)xpathExprXslParentNode).getParentElem()); 
    		}
    	}
    	else if (xpathExprXslParentNode instanceof ElemSequence) {
    		result = ((ElemSequence)xpathExprXslParentNode).getXpathDefaultNamespace();
    		if (result == null) {
    			result = getXPathDefaultNamespace(((ElemSequence)xpathExprXslParentNode).getParentElem()); 
    		}
    	}
    	else if (xpathExprXslParentNode instanceof ElemNumber) {
    		result = ((ElemNumber)xpathExprXslParentNode).getXpathDefaultNamespace();
    		if (result == null) {
    			result = getXPathDefaultNamespace(((ElemNumber)xpathExprXslParentNode).getParentElem()); 
    		}
    	}
    	else if (xpathExprXslParentNode instanceof ElemText) {
    		result = ((ElemText)xpathExprXslParentNode).getXpathDefaultNamespace();
    		if (result == null) {
    			result = getXPathDefaultNamespace(((ElemText)xpathExprXslParentNode).getParentElem()); 
    		}
    	}
    	else if (xpathExprXslParentNode instanceof ElemAttribute) {
    		result = ((ElemAttribute)xpathExprXslParentNode).getXpathDefaultNamespace();
    		if (result == null) {
    			result = getXPathDefaultNamespace(((ElemAttribute)xpathExprXslParentNode).getParentElem()); 
    		}
    	}
    	else if (xpathExprXslParentNode instanceof ElemElement) {
    		result = ((ElemElement)xpathExprXslParentNode).getXpathDefaultNamespace();
    		if (result == null) {
    			result = getXPathDefaultNamespace(((ElemElement)xpathExprXslParentNode).getParentElem()); 
    		}
    	}
    	else if (xpathExprXslParentNode instanceof ElemComment) {
    		result = ((ElemComment)xpathExprXslParentNode).getXpathDefaultNamespace();
    		if (result == null) {
    			result = getXPathDefaultNamespace(((ElemComment)xpathExprXslParentNode).getParentElem()); 
    		}
    	}
    	else if (xpathExprXslParentNode instanceof ElemPI) {
    		result = ((ElemPI)xpathExprXslParentNode).getXpathDefaultNamespace();
    		if (result == null) {
    			result = getXPathDefaultNamespace(((ElemPI)xpathExprXslParentNode).getParentElem()); 
    		}
    	}

    	return result;
    }
    
    /**
     * Method definition, to mutate XPath expression token queue contents by 
     * adding tokens from the supplied array in order, prior to the current 
     * token.
     * 
     * @param newTokenArr						   An array of token string values, to add at 
     *                                             specified position within token queue.
     * @param start1					           Starting token queue index value for token 
     *                                             prefix list.
     * @param start2                               Starting token queue index value for token 
     *                                             suffix list.
     * @param newTokenQueueSize					   New token queue size to be set on the token 
     *                                             queue object.	
     */
    private void mutateTokenQueue(String[] newTokenArr, int start1, int start2, int newTokenQueueSize) {
    	ObjectVector tokenQueue = m_ops.getTokenQueue();    		      		  
		List<Object> tokenPrefixList = new ArrayList<Object>();
		for (int i = start1; i < m_queueMark - 1; i++) {
			Object obj1 = tokenQueue.elementAt(i);
			tokenPrefixList.add(obj1); 
		}

		List<Object> tokenSuffixList = new ArrayList<Object>();
		int tokenQueueSize = tokenQueue.size();
		for (int i = start2; i < tokenQueueSize; i++) {
			Object obj1 = tokenQueue.elementAt(i);
			tokenSuffixList.add(obj1);
		}    		  

		tokenQueue.removeAllElements();

		for (int j = 0; j < tokenPrefixList.size(); j++) {
			tokenQueue.addElement(tokenPrefixList.get(j));  
		}

		for (int i = 0; i < newTokenArr.length; i++) {
		   tokenQueue.addElement(newTokenArr[i]);
		}

		for (int j = 0; j < tokenSuffixList.size(); j++) {
			tokenQueue.addElement(tokenSuffixList.get(j)); 
		}

		tokenQueue.setSize(tokenPrefixList.size() + tokenSuffixList.size() + newTokenQueueSize);
    }
    
    /**
     * Mutate XPath expression's token queue, for match pattern to consider
     * XSL stylesheet's xpath-default-namespace value.
     */
    private void mutateTokenQueueXPathMatchPattern() {
    	
    	if (!("/".equals(m_expression) || "*".equals(m_expression) || m_expression.contains(":") 
    			                                                   || m_expression.contains("(") 
    			                                                   || m_expression.contains(")") 
    			                                                   || m_expression.contains("@"))) {    		
    		// REVISIT : This implementation supports simple XPath step and union patterns, 
    		// when using XSL attribute xpath-default-namespace.

    		String[] strArr = null;    		
    		if (m_expression.contains("|")) {
    			strArr = m_expression.split("\\|");    			
    			List<String> strList = new ArrayList<String>();    			
    			for (int i = 0; i < strArr.length; i++) {
    				String strValue = (strArr[i]).trim();
    				if (strValue.contains("/")) {
    					String[] strArr1 = strValue.split("/");
    	    			for (int j = 0; j < strArr1.length; j++) {
    	    				String strValue2 = (strArr1[j]).trim();
    	    				strList.add(m_xpathDefaultNamespace + ":" + strValue2);
    	    				if (j < (strArr1.length - 1)) {
    	    					strList.add("/");
    	    				}
    	    			}
    				}
    				else {
    					char chr = strValue.charAt(0);
    					String suffixStr1 = strValue.substring(1);
    					String str1 = ((chr == '/') ? ("/" + m_xpathDefaultNamespace + ":" + suffixStr1) : (m_xpathDefaultNamespace + ":" + strValue)); 
    					strList.add(str1);
    				}
    				
    				if (i < (strArr.length - 1)) {
    					strList.add("|");
    				}
    			}

    			strArr = strList.toArray(strArr);
    		}
    		else if (m_expression.contains("/")) {
    		    strArr = m_expression.split("/");
    		    
    		    int newArrSize = strArr.length + (strArr.length - 1);
    			String[] strArr1 = new String[newArrSize];
    			int j = 0;
    			for (int i = 0; i < strArr.length; i++) {
    				String strValue = (strArr[i]).trim();
    				strArr1[j++] = (m_xpathDefaultNamespace + ":" + strValue);
    				if (i < (strArr.length - 1)) {
    					strArr1[j++] = "/"; 
    				}
    			}

    			strArr = strArr1;
    		}
    		else {
    			char chr = m_expression.charAt(0);
    			String suffixStr1 = m_expression.substring(1);
    			String newStr = (chr == '/') ? ("/" + m_xpathDefaultNamespace + ":" + suffixStr1) : 
    				                                                                      (m_xpathDefaultNamespace + ":" + m_expression); 
    			strArr = new String[] { newStr }; 
    		}

    		ObjectVector tokenQueue = m_ops.getTokenQueue(); 
    		tokenQueue.removeAllElements();

    		int j = 0;
    		for (int i = 0; i < strArr.length; i++) {
    			String strValue = strArr[i];
    			if ("|".equals(strValue)) {
    				tokenQueue.addElement("|");
    				j++;
    			}
    			else if ("/".equals(strValue)) {
    				tokenQueue.addElement("/");
    				j++;
    			}
    			else {
    				if (strValue.startsWith("/")) {
    				    tokenQueue.addElement("/");    				    
    				    String strValueSuffix = strValue.substring(1);
    				    int j1 = strValueSuffix.lastIndexOf(':');
    					tokenQueue.addElement(strValueSuffix.substring(0, j1));
    					tokenQueue.addElement(":");
    					tokenQueue.addElement(strValueSuffix.substring(j1 + 1));
    					j += 4;
    				}
    				else {
    					int j1 = strValue.lastIndexOf(':');
    					tokenQueue.addElement(strValue.substring(0, j1));
    					tokenQueue.addElement(":");
    					tokenQueue.addElement(strValue.substring(j1 + 1));
    					j += 3;
    				}    				    				
    			}    			
    		}

    		tokenQueue.setSize(j);

    		m_token = m_xpathDefaultNamespace;
    		m_tokenChar = m_xpathDefaultNamespace.charAt(0);		  
    	}
    }
  
}
