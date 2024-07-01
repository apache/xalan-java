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
package org.apache.xpath;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.XMLNSDecl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xml.utils.SAXSourceLocator;
import org.apache.xpath.axes.LocPathIterator;
import org.apache.xpath.compiler.Compiler;
import org.apache.xpath.compiler.FunctionTable;
import org.apache.xpath.compiler.XPathParser;
import org.apache.xpath.functions.Function;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.operations.ArrowOp;
import org.apache.xpath.res.XPATHErrorResources;

import xml.xpath31.processor.types.XSNumericType;

/**
 * The XPath class wraps an expression object and provides general services 
 * for execution of that expression.
 * @xsl.usage advanced
 */
public class XPath implements Serializable, ExpressionOwner
{
  static final long serialVersionUID = 3976493477939110553L;

  /** The top of the expression tree. 
   *  @serial */
  private Expression m_mainExp;
  
  /**
   * The function table for xpath built-in functions
   */
  private transient FunctionTable m_funcTable = null;
  
  // The following two fields of this class, are used during 
  // XPath.fixupVariables(..) action as performed within object of 
  // this class.    
  private Vector fVars;    
  private int fGlobalsSize;
  
  /**
   * This class field, is used when evaluating an 
   * XPath arrow operator, "=>".
   */
  private String fArrowOpRemainingXPathExprStr = null;

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
    fVars = (Vector)(vars.clone());
    fGlobalsSize = globalsSize;
    
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
      fArrowOpRemainingXPathExprStr = parser.getArrowOpRemainingXPathExprStr();
    }
    else if (MATCH == type)
      parser.initMatchPattern(compiler, exprString, prefixResolver);
    else
      throw new RuntimeException(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_CANNOT_DEAL_XPATH_TYPE, new Object[]{Integer.toString(type)})); //"Can not deal with XPath type: " + type);

    Expression expr = compiler.compile(0);
    if (expr instanceof ArrowOp) {
       ((ArrowOp)expr).setArrowOpRemainingXPathExprStr(fArrowOpRemainingXPathExprStr);
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
          ErrorListener errorListener, FunctionTable aTable)
            throws javax.xml.transform.TransformerException
  { 
    m_funcTable = aTable;     
    if(null == errorListener)
      errorListener = new org.apache.xml.utils.DefaultErrorHandler();
    
    m_patternString = exprString;

    XPathParser parser = new XPathParser(errorListener, locator);
    Compiler compiler = new Compiler(errorListener, locator, m_funcTable);    
    
    if (SELECT == type) {
      parser.initXPath(compiler, exprString, prefixResolver, false);
      fArrowOpRemainingXPathExprStr = parser.getArrowOpRemainingXPathExprStr();
    }
    else if (MATCH == type)
      parser.initMatchPattern(compiler, exprString, prefixResolver);
    else
      throw new RuntimeException(XSLMessages.createXPATHMessage(
            XPATHErrorResources.ER_CANNOT_DEAL_XPATH_TYPE, 
            new Object[]{Integer.toString(type)})); 

    Expression expr = compiler.compile(0);
    if (expr instanceof ArrowOp) {
      ((ArrowOp)expr).setArrowOpRemainingXPathExprStr(fArrowOpRemainingXPathExprStr);
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

      if (SELECT == type)
        parser.initXPath(compiler, exprString, prefixResolver, isSequenceTypeXPathExpr);
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

    xctxt.pushNamespaceContext(namespaceContext);

    xctxt.pushCurrentNodeAndExpression(contextNode, contextNode);
    
    SourceLocator srcLocator = xctxt.getSAXLocator(); 

    XObject xobj = null;
    
    boolean isProcessAsNodeset = true;
    
    if (m_mainExp instanceof LocPathIterator) {
        LocPathIterator locPathIterator = (LocPathIterator)m_mainExp;
        
        DTMIterator dtmIter = null;                     
        try {
           dtmIter = locPathIterator.asIterator(xctxt, contextNode);
        }
        catch (ClassCastException ex) {
           isProcessAsNodeset = false;
        }
    }

    try {
       if (isProcessAsNodeset) {
          if (m_mainExp instanceof Function) {
             xobj = ((Function)m_mainExp).execute(xctxt); 
          }
          else {
             xobj = m_mainExp.execute(xctxt);
          }
       }
       else {
          String xpathPatternStr = getPatternString();
               
          if (xpathPatternStr.startsWith("$") && xpathPatternStr.contains("[") && 
                                                                            xpathPatternStr.endsWith("]")) {              
             // Within this 'if' clause, we handle the case, where the XPath expression is
             // syntactically of type $varName[expr], for example $varName[1], $varName[$idx],
             // $varName[funcCall(arg)] etc, and $varName resolves to a 'ResultSequence' object.
                   
             String varRefXPathExprStr = "$" + xpathPatternStr.substring(1, xpathPatternStr.indexOf('['));
             String xpathIndexExprStr = xpathPatternStr.substring(xpathPatternStr.indexOf('[') + 1, 
                                                                                          xpathPatternStr.indexOf(']'));
             ElemTemplateElement elemTemplateElement = (ElemTemplateElement)xctxt.getNamespaceContext();
             List<XMLNSDecl> prefixTable = null;
             if (elemTemplateElement != null) {
                prefixTable = (List<XMLNSDecl>)elemTemplateElement.getPrefixTable();
             }
                  
             // Evaluate the, variable reference XPath expression
             if (prefixTable != null) {
                varRefXPathExprStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(
                                                                                                 varRefXPathExprStr, 
                                                                                                 prefixTable);
             }
                  
             XPath xpathObj = new XPath(varRefXPathExprStr, srcLocator, 
                                                                   xctxt.getNamespaceContext(), XPath.SELECT, null);            
             if (fVars != null) {
                xpathObj.fixupVariables(fVars, fGlobalsSize);  
             }
                  
             XObject varEvalResult = xpathObj.execute(xctxt, xctxt.getCurrentNode(), xctxt.getNamespaceContext());
                  
             // Evaluate the, xdm sequence index XPath expression
             if (prefixTable != null) {
                xpathIndexExprStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(
                                                                                                xpathIndexExprStr, 
                                                                                                prefixTable);
             }
                  
             xpathObj = new XPath(xpathIndexExprStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
                  
             if (fVars != null) {
                xpathObj.fixupVariables(fVars, fGlobalsSize);  
             }
                  
             XObject seqIndexEvalResult = xpathObj.execute(xctxt, xctxt.getCurrentNode(), 
                                                                                    xctxt.getNamespaceContext());
             if (varEvalResult instanceof ResultSequence) {
                ResultSequence resultSeq = (ResultSequence)varEvalResult;
                     
                if (seqIndexEvalResult instanceof XNumber) {
                   double dValIndex = ((XNumber)seqIndexEvalResult).num();
                   if (dValIndex == (int)dValIndex) {
                      xobj = resultSeq.item((int)dValIndex - 1);
                   }
                   else {
                      throw new javax.xml.transform.TransformerException("XPTY0004 : an index value used with an xdm "
                                                                                                      + "sequence reference, is not an integer.", 
                                                                                                            srcLocator); 
                   }  
                }
                else if (seqIndexEvalResult instanceof XSNumericType) {
                        String indexStrVal = ((XSNumericType)seqIndexEvalResult).stringValue();
                        double dValIndex = (Double.valueOf(indexStrVal)).doubleValue();
                        if (dValIndex == (int)dValIndex) {
                           xobj = resultSeq.item((int)dValIndex - 1);                                  
                        }
                        else {
                           throw new javax.xml.transform.TransformerException("XPTY0004 : an index value used with an xdm "
                                                                                                + "sequence reference, is not an integer.", 
                                                                                                     srcLocator); 
                        } 
                }
                else {
                        throw new javax.xml.transform.TransformerException("XPTY0004 : an index value used with an xdm sequence "
                                                                                               + "reference, is not numeric.", srcLocator);  
                }
              }          
            }  
          }
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

    return xobj;
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
	 return fArrowOpRemainingXPathExprStr;
  }

  public void setArrowOpRemainingXPathExprStr(String arrowOpRemainingXPathExprStr) {
	 this.fArrowOpRemainingXPathExprStr = arrowOpRemainingXPathExprStr;
  }

}
