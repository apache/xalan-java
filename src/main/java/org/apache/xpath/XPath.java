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
package org.apache.xpath;

import java.io.Serializable;
import java.io.StringReader;
import java.net.URI;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.processor.StylesheetHandler;
import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.templates.Constants;
import org.apache.xalan.templates.ElemCopyOf;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xalan.templates.XMLNSDecl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xalan.xslt.util.XslTransformSharedDatastore;
import org.apache.xerces.dom.DOMInputImpl;
import org.apache.xerces.impl.dv.xs.XSSimpleTypeDecl;
import org.apache.xerces.impl.xs.XSLoaderImpl;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xml.utils.SAXSourceLocator;
import org.apache.xpath.axes.LocPathIterator;
import org.apache.xpath.compiler.Compiler;
import org.apache.xpath.compiler.FunctionTable;
import org.apache.xpath.compiler.XPathParser;
import org.apache.xpath.composite.XPathExprFunctionSuffix;
import org.apache.xpath.functions.Function;
import org.apache.xpath.functions.XPathDynamicFunctionCall;
import org.apache.xpath.functions.XSL3FunctionService;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.operations.ArrowOp;
import org.apache.xpath.patterns.NodeTest;
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

import xml.xpath31.processor.types.XSNumericType;
import xml.xpath31.processor.types.XSString;

/**
 * This class wraps an XPath expression object and provides 
 * common services for evaluation of that expression.
 * 
 * @xsl.usage advanced
 */
public class XPath implements Serializable, ExpressionOwner
{
  static final long serialVersionUID = 3976493477939110553L;

  /** 
   * The top of XPath expression tree. 
   */
  private Expression m_mainExp;
  
  /**
   * The function table for xpath built-in functions
   */
  private transient FunctionTable m_funcTable = null;
  
  /**
   * This class field is used during, XPath.fixupVariables(..) action 
   * as performed within object of this class.  
   */    
  private Vector m_vars;
  
  /**
   * This class field is used during, XPath.fixupVariables(..) action 
   * as performed within object of this class.  
   */
  private int m_globals_size;
  
  /**
   * This class field, is used while evaluating an 
   * XPath arrow operator, "=>".
   */
  private String m_arrowop_remaining_xpath_expr_str = null;
  
  /**
   * This class field with boolean value 'true' denotes, that 
   * this XPath expression corresponds to quantified expression's 
   * satisfies clause. 
   */
  private boolean m_is_quantified_expr;
  
  /**
   * This class field with boolean value 'true' denotes, that 
   * this XPath expression corresponds to xsl:try element 
   * processing. 
   */
  private boolean m_is_xsltry_processing;

  /**
   * initial the function table
   */
  private void initFunctionTable() {
  	 m_funcTable = new FunctionTable();
  }

  /**
   * Get the raw Expression object that this class wraps.
   *
   *
   * @return the raw Expression object, which should not normally be null.
   */
  public Expression getExpression()
  {
    return m_mainExp;
  }
  
  /**
   * This function is used to fixup variables from QNames to stack frame 
   * indexes at stylesheet build time.
   * @param vars List of QNames that correspond to variables.  This list 
   * should be searched backwards for the first qualified name that 
   * corresponds to the variable reference qname.  The position of the 
   * QName in the vector from the start of the vector will be its position 
   * in the stack frame (but variables above the globalsTop value will need 
   * to be offset to the current stack frame).
   */
  public void fixupVariables(java.util.Vector vars, int globalsSize)
  {
    m_vars = (Vector)(vars.clone());
    m_globals_size = globalsSize;
    
    m_mainExp.fixupVariables(vars, globalsSize);
  }

  /**
   * Set the raw expression object for this object.
   *
   *
   * @param exp the raw Expression object, which should not normally be null.
   */
  public void setExpression(Expression exp)
  {
  	if(null != m_mainExp)
    	exp.exprSetParent(m_mainExp.exprGetParent()); // a bit bogus
    m_mainExp = exp;
  }

  /**
   * Get the SourceLocator on the expression object.
   *
   *
   * @return the SourceLocator on the expression object, which may be null.
   */
  public SourceLocator getLocator()
  {
    return m_mainExp;
  }

  /** The pattern string, mainly kept around for diagnostic purposes.
   *  @serial  */
  String m_patternString;

  /**
   * Return the XPath string associated with this object.
   *
   *
   * @return the XPath string associated with this object.
   */
  public String getPatternString()
  {
    return m_patternString;
  }

  /** Represents a select type expression. */
  public static final int SELECT = 0;

  /** Represents a match type expression.  */
  public static final int MATCH = 1;

  /**
   * Construct an XPath object.  
   *
   * (Needs review -sc) This method initializes an XPathParser/
   * Compiler and compiles the expression.
   * @param exprString The XPath expression.
   * @param locator The location of the expression, may be null.
   * @param prefixResolver A prefix resolver to use to resolve prefixes to 
   *                       namespace URIs.
   * @param type one of {@link #SELECT} or {@link #MATCH}.
   * @param errorListener The error listener, or null if default should be used.
   *
   * @throws javax.xml.transform.TransformerException if syntax or other error.
   */
  public XPath(
          String exprString, SourceLocator locator, PrefixResolver prefixResolver, int type,
          ErrorListener errorListener)
            throws javax.xml.transform.TransformerException
  { 
    
	initFunctionTable();
    
    if(null == errorListener)
      errorListener = new org.apache.xml.utils.DefaultErrorHandler();
    
    m_patternString = exprString;

    XPathParser parser = new XPathParser(errorListener, locator);
    Compiler compiler = new Compiler(errorListener, locator, m_funcTable);    
    
    if (SELECT == type) {
      parser.initXPath(compiler, exprString, prefixResolver, false);
      m_arrowop_remaining_xpath_expr_str = parser.getArrowOpRemainingXPathExprStr();
    }
    else if (MATCH == type)
      parser.initMatchPattern(compiler, exprString, prefixResolver);
    else
      throw new RuntimeException(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_CANNOT_DEAL_XPATH_TYPE, new Object[]{Integer.toString(type)})); //"Can not deal with XPath type: " + type);

    Expression expr = compiler.compile(0);
    if (expr instanceof ArrowOp) {
       ((ArrowOp)expr).setArrowOpRemainingXPathExprStr(m_arrowop_remaining_xpath_expr_str);
    }
    
    this.setExpression(expr);
    
    if((null != locator) && locator instanceof ExpressionNode)
    {
    	expr.exprSetParent((ExpressionNode)locator);
    }

  }

  /**
   * Construct an XPath object.  
   *
   * (Needs review -sc) This method initializes an XPathParser/
   * Compiler and compiles the expression.
   * @param exprString The XPath expression.
   * @param locator The location of the expression, may be null.
   * @param prefixResolver A prefix resolver to use to resolve prefixes to 
   *                       namespace URIs.
   * @param type one of {@link #SELECT} or {@link #MATCH}.
   * @param errorListener The error listener, or null if default should be used.
   *
   * @throws javax.xml.transform.TransformerException if syntax or other error.
   */
  public XPath(
          String exprString, SourceLocator locator, 
          PrefixResolver prefixResolver, int type,
          ErrorListener errorListener, FunctionTable funcTable)
            throws javax.xml.transform.TransformerException
  { 
    m_funcTable = funcTable;     
    if(null == errorListener)
      errorListener = new org.apache.xml.utils.DefaultErrorHandler();
    
    m_patternString = exprString;

    XPathParser parser = new XPathParser(errorListener, locator);
    Compiler compiler = new Compiler(errorListener, locator, m_funcTable);
    
    Expression expr = null;
    
    if (SELECT == type) {
      parser.initXPath(compiler, exprString, prefixResolver, false);
      m_arrowop_remaining_xpath_expr_str = parser.getArrowOpRemainingXPathExprStr();
      
      XPathExprFunctionSuffix xpathExprFunctionSuffix = parser.getXPathExprFunctionSuffix();
      if (xpathExprFunctionSuffix != null) {
    	 parser = new XPathParser(errorListener, locator);
    	 compiler = new Compiler(errorListener, locator, m_funcTable);
    	 String xpathOneExprStr = xpathExprFunctionSuffix.getXPathOneStr();    	     	 
    	 parser.initXPath(compiler, xpathOneExprStr, prefixResolver, false);
    	 Expression expr1 = compiler.compile(0);
    	 
    	 parser = new XPathParser(errorListener, locator);
    	 compiler = new Compiler(errorListener, locator, m_funcTable);
    	 String xpathTwoExprStr = xpathExprFunctionSuffix.getXPathTwoStr();    	 
    	 if (xpathTwoExprStr.contains(":")) {  		 
    		 StylesheetHandler stylesheetHandler = (StylesheetHandler)prefixResolver;    		 
    		 Hashtable nsUriTable = stylesheetHandler.getNamespaceUriTable();
    		 Enumeration nsUriTableKeys = nsUriTable.keys();
    		 while (nsUriTableKeys.hasMoreElements()) {
    			String key = (nsUriTableKeys.nextElement()).toString();
    			String value = (nsUriTable.get(key)).toString();
    			xpathTwoExprStr = xpathTwoExprStr.replace(key, value);
    		 }
    	 }
    	 parser.initXPath(compiler, xpathTwoExprStr, prefixResolver, false);
    	 Expression expr2 = compiler.compile(0);
    	 
    	 parser.setXPathExprFunctionSuffix(null);
    	 
    	 expr = expr1;
    	 
    	 if ((expr instanceof LocPathIterator) && (expr2 instanceof Function)) {
    		LocPathIterator locPathIter = (LocPathIterator)expr;
    		expr2.exprSetParent((ExpressionNode)locator);
    		locPathIter.setFuncExpr((Function)expr2);
    	 }
    	 else if ((expr instanceof LocPathIterator) && (expr2 instanceof XPathDynamicFunctionCall)) {
     		LocPathIterator locPathIter = (LocPathIterator)expr;
     		expr2.exprSetParent((ExpressionNode)locator);
     		locPathIter.setDynamicFuncCallExpr((XPathDynamicFunctionCall)expr2);
     	 }
      }
    }
    else if (MATCH == type)
      parser.initMatchPattern(compiler, exprString, prefixResolver);    
    else
      throw new RuntimeException(XSLMessages.createXPATHMessage(
            XPATHErrorResources.ER_CANNOT_DEAL_XPATH_TYPE, 
            new Object[]{Integer.toString(type)}));
    
    if (expr == null) {
       expr = compiler.compile(0);
    }
    
    if (expr instanceof ArrowOp) {
       ((ArrowOp)expr).setArrowOpRemainingXPathExprStr(m_arrowop_remaining_xpath_expr_str);
    }

    this.setExpression(expr);
    
    if((null != locator) && locator instanceof ExpressionNode)
    {
    	expr.exprSetParent((ExpressionNode)locator);
    }

  }
  
  /**
   * Construct an XPath object.  
   *
   * (Needs review -sc) This method initializes an XPathParser/
   * Compiler and compiles the expression.
   * @param exprString The XPath expression.
   * @param locator The location of the expression, may be null.
   * @param prefixResolver A prefix resolver to use to resolve prefixes to 
   *                       namespace URIs.
   * @param type one of {@link #SELECT} or {@link #MATCH}.
   *
   * @throws javax.xml.transform.TransformerException if syntax or other error.
   */
  public XPath(
          String exprString, SourceLocator locator, PrefixResolver prefixResolver, int type)
            throws javax.xml.transform.TransformerException
  {  
    this(exprString, locator, prefixResolver, type, null);    
  }
  
  /**
   * Construct an XPath object. This method has an additional parameter
   * 'isSequenceTypeXPathExpr', to handle XPath 3.1 expressions that 
   * represent sequence type declarations. 
   *
   * @throws javax.xml.transform.TransformerException if syntax or other error.
   */
  public XPath(
          String exprString, SourceLocator locator, PrefixResolver prefixResolver, int type,
          ErrorListener errorListener, boolean isSequenceTypeXPathExpr)
            throws javax.xml.transform.TransformerException {
      
	  initFunctionTable();
      
      if(null == errorListener)
        errorListener = new org.apache.xml.utils.DefaultErrorHandler();
      
      m_patternString = exprString;

      XPathParser parser = new XPathParser(errorListener, locator);
      Compiler compiler = new Compiler(errorListener, locator, m_funcTable);

      if (SELECT == type) {
        parser.initXPath(compiler, exprString, prefixResolver, isSequenceTypeXPathExpr);
      }
      else if (MATCH == type)
        parser.initMatchPattern(compiler, exprString, prefixResolver);
      else
        throw new RuntimeException(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_CANNOT_DEAL_XPATH_TYPE, new Object[]{Integer.toString(type)}));

      Expression expr = compiler.compile(0);

      this.setExpression(expr);
      
      if((null != locator) && locator instanceof ExpressionNode)
      {
          expr.exprSetParent((ExpressionNode)locator);
      } 
  }

  /**
   * Construct an XPath object.
   *
   * @param expr The Expression object.
   *
   * @throws javax.xml.transform.TransformerException if syntax or other error.
   */
  public XPath(Expression expr)
  {  
    this.setExpression(expr);
    
    initFunctionTable();   
  }
  
  /**
   * Given an expression and a context, evaluate the XPath
   * and return the result.
   * 
   * @param xctxt The execution context.
   * @param contextNode The node that "." expresses.
   * @param namespaceContext The context in which namespaces in the
   * XPath are supposed to be expanded.
   *
   * @return The result of the XPath or null if callbacks are used.
   * @throws TransformerException thrown if
   * the error condition is severe enough to halt processing.
   *
   * @throws javax.xml.transform.TransformerException
   * @xsl.usage experimental
   */
  public XObject execute(
          XPathContext xctxt, org.w3c.dom.Node contextNode, 
          PrefixResolver namespaceContext)
            throws javax.xml.transform.TransformerException
  {
    return execute(
          xctxt, xctxt.getDTMHandleFromNode(contextNode), 
          namespaceContext);
  }
  

  /**
   * Given an expression and a context, evaluate the XPath
   * and return the result.
   * 
   * @param xctxt The execution context.
   * @param contextNode The node that "." expresses.
   * @param namespaceContext The context in which namespaces in the
   * XPath are supposed to be expanded.
   * 
   * @throws TransformerException thrown if the active ProblemListener decides
   * the error condition is severe enough to halt processing.
   *
   * @throws javax.xml.transform.TransformerException
   * @xsl.usage experimental
   */
  public XObject execute(
          XPathContext xctxt, int contextNode, PrefixResolver namespaceContext)
            throws javax.xml.transform.TransformerException
  {

	  XObject result = null;
	  
	  xctxt.pushNamespaceContext(namespaceContext);

	  xctxt.pushCurrentNodeAndExpression(contextNode, contextNode);

	  result = evaluateXPathExpr(xctxt, contextNode, false);

	  return result;
  }
  
  /**
   * Evaluate an XPath "instance of" expression.
   */
  public XObject executeInstanceOf(XPathContext xctxt, int contextNode, PrefixResolver namespaceContext)
                                                                                  throws javax.xml.transform.TransformerException {

	  XObject result = null;
	  
	  xctxt.pushNamespaceContext(namespaceContext);

	  xctxt.pushCurrentNodeAndExpression(contextNode, contextNode);

	  result = evaluateXPathExpr(xctxt, contextNode, true);

	  return result;
  }
  
  /**
   * Given an expression and a context, evaluate the XPath
   * and return the result.
   * 
   * @param xctxt The execution context.
   * @param contextNode The node that "." expresses.
   * @param namespaceContext The context in which namespaces in the
   * XPath are supposed to be expanded.
   * 
   * @throws TransformerException thrown if the active ProblemListener decides
   * the error condition is severe enough to halt processing.
   *
   * @throws javax.xml.transform.TransformerException
   * @xsl.usage experimental
   */
  public boolean bool(
          XPathContext xctxt, int contextNode, PrefixResolver namespaceContext)
            throws javax.xml.transform.TransformerException
  {

    xctxt.pushNamespaceContext(namespaceContext);

    xctxt.pushCurrentNodeAndExpression(contextNode, contextNode);

    try
    {
      return m_mainExp.bool(xctxt);
    }
    catch (TransformerException te)
    {
      te.setLocator(this.getLocator());
      ErrorListener el = xctxt.getErrorListener();
      if(null != el)
      {
        el.error(te);
      }
      else
        throw te;
    }
    catch (Exception e)
    {
      while (e instanceof org.apache.xml.utils.WrappedRuntimeException)
      {
        e = ((org.apache.xml.utils.WrappedRuntimeException) e).getException();
      }

      String msg = e.getMessage();
      
      if (msg == null || msg.length() == 0) {
           msg = XSLMessages.createXPATHMessage(
               XPATHErrorResources.ER_XPATH_ERROR, null);
     
      }        
      
      TransformerException te = new TransformerException(msg,
              getLocator(), e);
      ErrorListener el = xctxt.getErrorListener();
      if(null != el)
      {
        el.fatalError(te);
      }
      else
        throw te;
    }
    finally
    {
      xctxt.popNamespaceContext();

      xctxt.popCurrentNodeAndExpression();
    }

    return false;
  }

  /** Set to true to get diagnostic messages about the result of 
   *  match pattern testing.  */
  private static final boolean DEBUG_MATCHES = false;

  /**
   * Get the match score of the given node.
   *
   * @param xctxt XPath runtime context.
   * @param context The current source tree context node.
   * 
   * @return score, one of {@link #MATCH_SCORE_NODETEST},
   * {@link #MATCH_SCORE_NONE}, {@link #MATCH_SCORE_OTHER}, 
   * or {@link #MATCH_SCORE_QNAME}.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public double getMatchScore(XPathContext xctxt, int context)
          throws javax.xml.transform.TransformerException
  {

    xctxt.pushCurrentNode(context);
    xctxt.pushCurrentExpressionNode(context);

    try
    {
      XObject score = m_mainExp.execute(xctxt);

      if (DEBUG_MATCHES)
      {
        DTM dtm = xctxt.getDTM(context);
        System.out.println("score: " + score.num() + " for "
                           + dtm.getNodeName(context) + " for xpath "
                           + this.getPatternString());
      }

      return score.num();
    }
    finally
    {
      xctxt.popCurrentNode();
      xctxt.popCurrentExpressionNode();
    }

    // return XPath.MATCH_SCORE_NONE;
  }


  /**
   * Warn the user of an problem.
   *
   * @param xctxt The XPath runtime context.
   * @param sourceNode Not used.
   * @param msg An error msgkey that corresponds to one of the constants found 
   *            in {@link org.apache.xpath.res.XPATHErrorResources}, which is 
   *            a key for a format string.
   * @param args An array of arguments represented in the format string, which 
   *             may be null.
   *
   * @throws TransformerException if the current ErrorListoner determines to 
   *                              throw an exception.
   */
  public void warn(
          XPathContext xctxt, int sourceNode, String msg, Object[] args)
            throws javax.xml.transform.TransformerException
  {

    String fmsg = XSLMessages.createXPATHWarning(msg, args);
    ErrorListener ehandler = xctxt.getErrorListener();

    if (null != ehandler)
    {
      ehandler.warning(new TransformerException(fmsg, (SAXSourceLocator)xctxt.getSAXLocator()));
    }
  }

  /**
   * Tell the user of an assertion error, and probably throw an
   * exception.
   *
   * @param b  If false, a runtime exception will be thrown.
   * @param msg The assertion message, which should be informative.
   * 
   * @throws RuntimeException if the b argument is false.
   */
  public void assertion(boolean b, String msg)
  {

    if (!b)
    {
      String fMsg = XSLMessages.createXPATHMessage(
        XPATHErrorResources.ER_INCORRECT_PROGRAMMER_ASSERTION,
        new Object[]{ msg });

      throw new RuntimeException(fMsg);
    }
  }

  /**
   * Tell the user of an error, and probably throw an
   * exception.
   *
   * @param xctxt The XPath runtime context.
   * @param sourceNode Not used.
   * @param msg An error msgkey that corresponds to one of the constants found 
   *            in {@link org.apache.xpath.res.XPATHErrorResources}, which is 
   *            a key for a format string.
   * @param args An array of arguments represented in the format string, which 
   *             may be null.
   *
   * @throws TransformerException if the current ErrorListoner determines to 
   *                              throw an exception.
   */
  public void error(
          XPathContext xctxt, int sourceNode, String msg, Object[] args)
            throws javax.xml.transform.TransformerException
  {

    String fmsg = XSLMessages.createXPATHMessage(msg, args);
    ErrorListener ehandler = xctxt.getErrorListener();

    if (null != ehandler)
    {
      ehandler.fatalError(new TransformerException(fmsg,
                              (SAXSourceLocator)xctxt.getSAXLocator()));
    }
    else
    {
      SourceLocator slocator = xctxt.getSAXLocator();
      System.out.println(fmsg + "; file " + slocator.getSystemId()
                         + "; line " + slocator.getLineNumber() + "; column "
                         + slocator.getColumnNumber());
    }
  }
  
  /**
   * This will traverse the heararchy, calling the visitor for 
   * each member.  If the called visitor method returns 
   * false, the subtree should not be called.
   * 
   * @param owner The owner of the visitor, where that path may be 
   *              rewritten if needed.
   * @param visitor The visitor whose appropriate method will be called.
   */
  public void callVisitors(ExpressionOwner owner, XPathVisitor visitor)
  {
  	m_mainExp.callVisitors(this, visitor);
  }

  /**
   * The match score if no match is made.
   * @xsl.usage advanced
   */
  public static final double MATCH_SCORE_NONE = Double.NEGATIVE_INFINITY;

  /**
   * The match score if the pattern has the form
   * of a QName optionally preceded by an @ character.
   * @xsl.usage advanced
   */
  public static final double MATCH_SCORE_QNAME = 0.0;

  /**
   * The match score if the pattern pattern has the form NCName:*.
   * @xsl.usage advanced
   */
  public static final double MATCH_SCORE_NSWILD = -0.25;

  /**
   * The match score if the pattern consists of just a NodeTest.
   * @xsl.usage advanced
   */
  public static final double MATCH_SCORE_NODETEST = -0.5;

  /**
   * The match score if the pattern consists of something
   * other than just a NodeTest or just a qname.
   * @xsl.usage advanced
   */
  public static final double MATCH_SCORE_OTHER = 0.5;

  public String getArrowOpRemainingXPathExprStr() {
	 return m_arrowop_remaining_xpath_expr_str;
  }

  public void setArrowOpRemainingXPathExprStr(String arrowOpRemainingXPathExprStr) {
	 this.m_arrowop_remaining_xpath_expr_str = arrowOpRemainingXPathExprStr;
  }

  public void setIsQuantifiedExpr(boolean isQuantifiedExpr) {	
	 this.m_is_quantified_expr = isQuantifiedExpr;
  }
  
  /**
   * Evaluate an XPath expression, to produce an xdm result item.
   */
  private XObject evaluateXPathExpr(XPathContext xctxt, int contextNode, boolean instanceOfCheck)
		                                                              throws TransformerException {

	  XObject result = null;

	  boolean isProcessAsNodeset = true;

	  if (m_mainExp instanceof LocPathIterator) {
		  LocPathIterator locPathIterator = (LocPathIterator)m_mainExp;

		  DTMCursorIterator dtmIter = null;
		  String xpathExprStr = null;
		  
		  try {			  
			  ExpressionNode exprNode = m_mainExp.getExpressionOwner();
			  if (exprNode instanceof ElemCopyOf) {
				  ElemCopyOf elemCopyOf = (ElemCopyOf)exprNode;
				  boolean isXmlSourceAbsent = elemCopyOf.getXMLSourceAbsent();
				  XPath elemCopyOfSelect = elemCopyOf.getSelect();
				  xpathExprStr = elemCopyOfSelect.getPatternString();
				  
				  // An XPath expression string, starting with character '$'
				  // means that an XPath expression is a variable reference.
				  
				  if (isXmlSourceAbsent && !xpathExprStr.startsWith("$")) {
					  contextNode = DTM.NULL;  
				  }
			  }
			  else {
			     // REVISIT : Other XSL template elements needs to be handled
			  }
			  
			  dtmIter = locPathIterator.asIterator(xctxt, contextNode);
		  }
		  catch (ClassCastException ex) {
			 isProcessAsNodeset = false;
		  }
		  catch (Exception ex) {
			 isProcessAsNodeset = false;
		  }
	  }

	  try {    	
		  if (isProcessAsNodeset) {
			  if (m_mainExp instanceof NodeTest) {
				 // Check for the possibility of XPath named function reference				  
				 result = evaluateXPathNamedFunctionReference((NodeTest)m_mainExp, xctxt);				 
			  }
			  else {
			     result = m_mainExp.execute(xctxt);
			  }
		  }
		  else {
			  String xpathPatternStr = getPatternString();
			  if (xpathPatternStr.startsWith("$") && xpathPatternStr.contains("[") && xpathPatternStr.endsWith("]")) {
				  result = evaluateXPathExprWithPredicateSimple(xpathPatternStr, xctxt);          
			  }  
		  }
	  }
	  catch (TransformerException te)
	  {
		  if (instanceOfCheck) {
			  throw te; 
		  }
		  te.setLocator(this.getLocator());
		  ErrorListener el = xctxt.getErrorListener();
		  if(null != el && !m_is_quantified_expr && !m_is_xsltry_processing)
		  {
			  el.error(te);
		  }
		  else
			  throw te;
	  }
	  catch (Exception e)
	  {
		  while (e instanceof org.apache.xml.utils.WrappedRuntimeException)
		  {
			  e = ((org.apache.xml.utils.WrappedRuntimeException) e).getException();
		  }
		  
		  if (instanceOfCheck) {
			  String msg = XSLMessages.createXPATHMessage(XPATHErrorResources.ER_XPATH_ERROR, null);
			  TransformerException te = new TransformerException(msg, getLocator(), e);
			  throw te;
		  }

		  String msg = e.getMessage();

		  if (msg == null || msg.length() == 0) {
			  msg = XSLMessages.createXPATHMessage(XPATHErrorResources.ER_XPATH_ERROR, null);

		  }  
		  TransformerException te = new TransformerException(msg, getLocator(), e);
		  ErrorListener el = xctxt.getErrorListener();
		  if(null != el)
		  {
			  el.fatalError(te);
		  }
		  else
			  throw te;
	  }
	  finally
	  {
		  xctxt.popNamespaceContext();

		  xctxt.popCurrentNodeAndExpression();
	  }

	  return result;
  }

  public void setIsXslTryProcessing(boolean bool) {
	  m_is_xsltry_processing = bool;
  }
  
  public boolean getIsXslTryProcessing() {
	  return m_is_xsltry_processing; 
  }
  
  /**
   * Method definition to evaluate an XPath expression, specified as named function reference.
   */
  private XObject evaluateXPathNamedFunctionReference(NodeTest nodeTest, XPathContext xctxt) throws TransformerException {
		
	  XObject result = null;

	  boolean isSchemaTypeRefFailed = false;
	  
	  try {				  			  
		  String localName = nodeTest.getLocalName();		  
		  int idx = localName.indexOf('#');
		  String xsSimpleTypeName = localName.substring(0, idx);
		  int arity = Integer.valueOf(localName.substring(idx + 1));				  
		  String xsSimpleTypeNamespace = nodeTest.getNamespace();

		  StylesheetRoot stylesheetRoot = XslTransformSharedDatastore.m_stylesheetRoot;				  				  

		  Node elemTemplateElem = stylesheetRoot.getFirstChildElem();				  				  
		  while (elemTemplateElem != null && !(Constants.ELEMNAME_IMPORT_SCHEMA_STRING).equals(elemTemplateElem.getLocalName())) {   
			  elemTemplateElem = elemTemplateElem.getNextSibling();
		  }

		  XSModel xsModel = null;

		  if ((Constants.ELEMNAME_IMPORT_SCHEMA_STRING).equals(elemTemplateElem.getLocalName())) {
			  NodeList nodeList = elemTemplateElem.getChildNodes();
			  Node xsSchemaTopMostNode = nodeList.item(0);		   

			  if (xsSchemaTopMostNode != null) {
				  // An xsl:import-schema instruction's child contents specifies a literal XML Schema document

				  DOMImplementationLS domImplLS = (DOMImplementationLS)((DOMImplementationRegistry.newInstance()).getDOMImplementation("LS"));
				  LSSerializer lsSerializer = domImplLS.createLSSerializer();
				  DOMConfiguration domConfig = lsSerializer.getDomConfig();
				  domConfig.setParameter(XSL3FunctionService.XML_DOM_FORMAT_PRETTY_PRINT, Boolean.TRUE);
				  String xmlSchemaDocumentStr = lsSerializer.writeToString((Document)xsSchemaTopMostNode);
				  xmlSchemaDocumentStr = xmlSchemaDocumentStr.replaceFirst(XSL3FunctionService.UTF_16, XSL3FunctionService.UTF_8);
				  xmlSchemaDocumentStr = xmlSchemaDocumentStr.replaceFirst("schema", "schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"");

				  DOMInputImpl lsInput = new DOMInputImpl();
				  lsInput.setCharacterStream(new StringReader(xmlSchemaDocumentStr));
				  XSLoaderImpl xsLoader = new XSLoaderImpl();

				  xsModel = xsLoader.load(lsInput);
			  }
			  else {
				  // An XML Schema document is available, referenced by uri from xsl:import-schema 
				  // element's attribute 'schema-location'.				

				  NamedNodeMap importSchemaNodeAttributes = ((Element)elemTemplateElem).getAttributes();

				  if (importSchemaNodeAttributes != null) {
					  String xslSystemId = XslTransformSharedDatastore.m_xslSystemId;
					  URL url = null;

					  Node attrNode1 = importSchemaNodeAttributes.item(0);
					  Node attrNode2 = importSchemaNodeAttributes.item(1);

					  if (attrNode1 != null) {
						  URI inpUri = new URI(attrNode1.getNodeValue());
						  String stylesheetSystemId = xslSystemId;
						  if (!inpUri.isAbsolute() && (stylesheetSystemId != null)) {
							  URI resolvedUri = (new URI(stylesheetSystemId)).resolve(inpUri);
							  url = resolvedUri.toURL(); 
							  if (!"namespace".equals(attrNode1.getNodeName())) {																								
								  XSLoaderImpl xsLoader = new XSLoaderImpl();

								  xsModel = xsLoader.loadURI(url.toString());
							  }
						  }
					  }
					  else if (attrNode2 != null) {
						  URI inpUri = new URI(attrNode2.getNodeValue());
						  String stylesheetSystemId = xslSystemId;

						  if (!inpUri.isAbsolute() && (stylesheetSystemId != null)) {
							  URI resolvedUri = (new URI(stylesheetSystemId)).resolve(inpUri);
							  url = resolvedUri.toURL();
							  if ("schema-location".equals(attrNode2.getNodeName())) {
								  XSLoaderImpl xsLoader = new XSLoaderImpl();

								  xsModel = xsLoader.loadURI(url.toString());
							  }
						  }
					  }
				  }
			  }
		  }

		  if (xsModel != null) {
			  XSTypeDefinition xsTypeDefinition = xsModel.getTypeDefinition(xsSimpleTypeName, xsSimpleTypeNamespace);
			  XSSimpleTypeDecl xsSimpleTypeDecl = (XSSimpleTypeDecl)xsTypeDefinition;
			  if (!((xsSimpleTypeDecl != null) && (arity != 1))) {
				  XSString xsString = new XSString((Expression.XS_SIMPLE_TYPE_NAME));
				  XObject xObj = XObject.create(xsString);
				  xObj.setXsTypeDefinition(xsTypeDefinition);

				  result = xObj;
			  }
			  else {
				  result = nodeTest.execute(xctxt);
			  }
		  }
		  else {
			  result = nodeTest.execute(xctxt); 
		  }
	  }
	  catch (Exception ex) {					  
		  isSchemaTypeRefFailed = true;
	  }

	  if (isSchemaTypeRefFailed) {
		  result = nodeTest.execute(xctxt); 
	  }
	  
	  return result;
	  
	}
  
    /**
     * Method definition to evaluate an XPath expression of the form $varName[..].
     * 
     * @param xpathPatternStr					  An XPath expression string
     * @param xctxt								  An XPath context object
     * @return                                    The result of XPath expression evaluation
     * @throws TransformerException
     */
    private XObject evaluateXPathExprWithPredicateSimple(String xpathPatternStr, XPathContext xctxt) throws TransformerException {
		
    	XObject result = null;

    	SourceLocator srcLocator = xctxt.getSAXLocator(); 

    	/**
    	 * Here we handle XPath expression evaluation, that have syntax of type $varName[expr], 
    	 * for example $varName[1], $varName[$idx], $varName[funcCall(arg)] etc, and $varName 
    	 * resolves to a 'ResultSequence' object.
    	 */
    	String varRefXPathExprStr = "$" + xpathPatternStr.substring(1, xpathPatternStr.indexOf('['));
    	String xpathIndexExprStr = xpathPatternStr.substring(xpathPatternStr.indexOf('[') + 1, xpathPatternStr.indexOf(']'));
    	ElemTemplateElement elemTemplateElement = (ElemTemplateElement)xctxt.getNamespaceContext();
    	List<XMLNSDecl> prefixTable = null;
    	if (elemTemplateElement != null) {
    		prefixTable = (List<XMLNSDecl>)elemTemplateElement.getPrefixTable();
    	}

    	// Evaluate the, variable reference XPath expression
    	if (prefixTable != null) {
    		varRefXPathExprStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(varRefXPathExprStr, prefixTable);
    	}

    	XPath xpathObj = new XPath(varRefXPathExprStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);            
    	if (m_vars != null) {
    		xpathObj.fixupVariables(m_vars, m_globals_size);  
    	}

    	XObject varEvalResult = xpathObj.execute(xctxt, xctxt.getCurrentNode(), xctxt.getNamespaceContext());

    	// Evaluate the, xdm sequence index XPath expression
    	if (prefixTable != null) {
    		xpathIndexExprStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(xpathIndexExprStr, prefixTable);
    	}

    	xpathObj = new XPath(xpathIndexExprStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);

    	if (m_vars != null) {
    		xpathObj.fixupVariables(m_vars, m_globals_size);  
    	}

    	XObject seqIndexEvalResult = xpathObj.execute(xctxt, xctxt.getCurrentNode(), xctxt.getNamespaceContext());
    	if (varEvalResult instanceof ResultSequence) {
    		ResultSequence resultSeq = (ResultSequence)varEvalResult;

    		if (seqIndexEvalResult instanceof XNumber) {
    			double dValIndex = ((XNumber)seqIndexEvalResult).num();
    			if (dValIndex == (int)dValIndex) {
    				result = resultSeq.item((int)dValIndex - 1);
    			}
    			else {
    				throw new javax.xml.transform.TransformerException("XPTY0004 : An index value used with an XDM sequence reference, is not an integer.", srcLocator); 
    			}  
    		}
    		else if (seqIndexEvalResult instanceof XSNumericType) {
    			String indexStrVal = ((XSNumericType)seqIndexEvalResult).stringValue();
    			double dValIndex = (Double.valueOf(indexStrVal)).doubleValue();
    			if (dValIndex == (int)dValIndex) {
    				result = resultSeq.item((int)dValIndex - 1);                                  
    			}
    			else {
    				throw new javax.xml.transform.TransformerException("XPTY0004 : An index value used with an XDM sequence reference, is not an integer.", srcLocator); 
    			} 
    		}
    		else {
    			throw new javax.xml.transform.TransformerException("XPTY0004 : An index value used with an XDM sequence reference, is not numeric.", srcLocator);  
    		}
    	}

    	return result;
  }

}
