/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights 
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:  
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Xalan" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 1999, Lotus
 * Development Corporation., http://www.lotus.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.xpath;

import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.traversal.NodeIterator;
import org.w3c.dom.DocumentFragment;

import java.util.Vector;

import java.io.Serializable;
import java.io.ObjectInputStream;
import java.io.IOException;

import org.apache.xalan.utils.PrefixResolver;
import org.apache.xalan.utils.QName;
import org.apache.xpath.res.XPATHErrorResources;
import org.apache.xpath.functions.Function;

// import org.apache.xpath.functions.FuncLoader;
import org.apache.xpath.compiler.Compiler;
import org.apache.xpath.compiler.XPathParser;
import org.apache.xpath.compiler.OpMap;  // temp
import org.apache.xpath.compiler.OpCodes;  // temp
import org.apache.xpath.compiler.PsuedoNames;  // temp
import org.apache.xpath.compiler.FunctionTable;  // temp
import org.apache.xpath.objects.XObject;
import org.apache.xalan.res.XSLMessages;
import org.apache.xpath.objects.*;

import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;

import org.apache.trax.TransformException;
import org.apache.xpath.patterns.NodeTest;

/**
 * <meta name="usage" content="general"/>
 * The XPath class represents the semantic parse tree of the XPath pattern.
 * It is the representation of the grammar which filters out
 * the choice for replacement order of the production rules.
 * In order to conserve memory and reduce object creation, the
 * tree is represented as an array of integers:
 *    [op code][length][...]
 * where strings are represented within the array as
 * indexes into the token tree.
 */
public class XPath implements Serializable
{

  /** NEEDSDOC Field m_mainExp          */
  private Expression m_mainExp;

  /**
   * NEEDSDOC Method getExpression 
   *
   *
   * NEEDSDOC (getExpression) @return
   */
  public Expression getExpression()
  {
    return m_mainExp;
  }

  /**
   * NEEDSDOC Method setExpression 
   *
   *
   * NEEDSDOC @param exp
   */
  public void setExpression(Expression exp)
  {
    m_mainExp = exp;
  }

  /** NEEDSDOC Field m_locator          */
  private Locator m_locator;

  /**
   * NEEDSDOC Method getLocator 
   *
   *
   * NEEDSDOC (getLocator) @return
   */
  public Locator getLocator()
  {
    return m_locator;
  }

  /**
   * NEEDSDOC Method setLocator 
   *
   *
   * NEEDSDOC @param l
   */
  public void setLocator(Locator l)
  {
	// Note potential hazards -- l may not be serializable, or may be changed
	  // after being assigned here.
	m_locator = l;
  }

  /** NEEDSDOC Field m_patternString          */
  String m_patternString;

  /**
   * NEEDSDOC Method getPatternString 
   *
   *
   * NEEDSDOC (getPatternString) @return
   */
  public String getPatternString()
  {
    return m_patternString;
  }

  /** NEEDSDOC Field SELECT          */
  public static final int SELECT = 0;

  /** NEEDSDOC Field MATCH          */
  public static final int MATCH = 1;

  /**
   * Construct an XPath object.  The object must be initialized by the
   * XPathParser.initXPath method.
   *
   * NEEDSDOC @param exprString
   * NEEDSDOC @param locator
   * NEEDSDOC @param prefixResolver
   * NEEDSDOC @param type
   *
   * @throws org.xml.sax.SAXException
   */
  public XPath(
          String exprString, Locator locator, PrefixResolver prefixResolver, int type)
            throws org.xml.sax.SAXException
  {

    // TODO: would like not to clone the locator...
	// but it may not be serializable, and it may be changed after being
	// passed in.
    if(null != locator)
      m_locator = new org.apache.xalan.utils.SerializableLocatorImpl(locator); 
      
    m_patternString = exprString;

    XPathParser parser = new XPathParser();
    Compiler compiler = new Compiler(null, locator);

    if (SELECT == type)
      parser.initXPath(compiler, exprString, prefixResolver);
    else if (MATCH == type)
      parser.initMatchPattern(compiler, exprString, prefixResolver);
    else
      throw new RuntimeException("Can not deal with XPath type: " + type);

    // System.out.println("----------------");
    Expression expr = compiler.compile(0);

    // System.out.println("expr: "+expr);
    this.setExpression(expr);

    if (MATCH == type)
      calcTargetStrings(compiler);
  }

  /**
   * <meta name="usage" content="experimental"/>
   * Given an expression and a context, evaluate the XPath
   * and call the callback as nodes are found.  Only some simple
   * types of expresions right now can call back, so if this
   * method returns null, then the callbacks have been called, otherwise
   * a valid XObject will be returned.
   * @param xctxt The execution context.
   * @param contextNode The node that "." expresses.
   * @param namespaceContext The context in which namespaces in the
   * XPath are supposed to be expanded.
   * @exception SAXException thrown if the active ProblemListener decides
   * the error condition is severe enough to halt processing.
   * @param callback Interface that implements the processLocatedNode method.
   * @param callbackInfo Object that will be passed to the processLocatedNode method.
   * @param stopAtFirst True if the search should stop once the first node in document
   * order is found.
   * @return The result of the XPath or null if callbacks are used.
   * @exception SAXException thrown if
   * the error condition is severe enough to halt processing.
   *
   * @throws org.xml.sax.SAXException
   */
  public XObject execute(
          XPathContext xctxt, Node contextNode, PrefixResolver namespaceContext)
            throws org.xml.sax.SAXException
  {

    PrefixResolver savedPrefixResolver = xctxt.getNamespaceContext();

    xctxt.m_currentPrefixResolver = namespaceContext;

    xctxt.pushCurrentNodeAndExpression(contextNode, contextNode);

    XObject xobj = null;

    try
    {
      xobj = m_mainExp.execute(xctxt);
    }
    catch (Exception e)
    {
      if (e instanceof org.apache.trax.TransformException)
        throw (org.apache.trax.TransformException) e;
      else
      {
        while (e instanceof org.apache.xalan.utils.WrappedRuntimeException)
        {
          e = ((org.apache.xalan.utils.WrappedRuntimeException) e).getException();
        }

        throw new org.apache.trax.TransformException("Error in XPath",
                m_locator, e);
      }
    }
    finally
    {
      xctxt.m_currentPrefixResolver = savedPrefixResolver;

      xctxt.popCurrentNodeAndExpression();
    }

    return xobj;
  }

  /** NEEDSDOC Field DEBUG_MATCHES          */
  private static final boolean DEBUG_MATCHES = false;

  /**
   * Get the match score of the given node.
   *
   * NEEDSDOC @param xctxt
   * @param context The current source tree context node.
   * @returns score, one of MATCH_SCORE_NODETEST,
   * MATCH_SCORE_NONE, MATCH_SCORE_OTHER, MATCH_SCORE_QNAME.
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws org.xml.sax.SAXException
   */
  public double getMatchScore(XPathContext xctxt, Node context)
          throws org.xml.sax.SAXException
  {

    xctxt.pushCurrentNode(context);
    xctxt.pushCurrentExpressionNode(context);

    try
    {
      XObject score = m_mainExp.execute(xctxt);

      if (DEBUG_MATCHES)
        System.out.println("score: " + score.num() + " for "
                           + context.getNodeName() + " for xpath "
                           + this.getPatternString());

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
   * Install a built-in function.
   * @param name The unqualified name of the function.
   * @param funcIndex The index of the function in the table.
   * @param func A Implementation of an XPath Function object.
   * @return the position of the function in the internal index.
   */
  public void installFunction(String name, int funcIndex, Function func)
  {
    FunctionTable.installFunction(func, funcIndex);
  }

  /** NEEDSDOC Field m_targetStrings          */
  private Vector m_targetStrings;

  /**
   * NEEDSDOC Method calcTargetStrings 
   *
   *
   * NEEDSDOC @param compiler
   */
  private void calcTargetStrings(Compiler compiler)
  {

    Vector targetStrings = new Vector();
    int opPos = 2;

    while (compiler.getOp(opPos) == OpCodes.OP_LOCATIONPATHPATTERN)
    {
      int nextOpPos = compiler.getNextOpPos(opPos);

      opPos = compiler.getFirstChildPos(opPos);

      while (compiler.getOp(opPos) != OpCodes.ENDOP)
      {
        int nextStepPos = compiler.getNextOpPos(opPos);
        int nextOp = compiler.getOp(nextStepPos);

        if ((nextOp == OpCodes.OP_PREDICATE) || (nextOp == OpCodes.ENDOP))
        {
          int stepType = compiler.getOp(opPos);

          opPos += 3;

          switch (stepType)
          {
          case OpCodes.OP_FUNCTION :
            targetStrings.addElement(PsuedoNames.PSEUDONAME_ANY);
            break;
          case OpCodes.FROM_ROOT :
            targetStrings.addElement(PsuedoNames.PSEUDONAME_ROOT);
            break;
          case OpCodes.MATCH_ATTRIBUTE :
          case OpCodes.MATCH_ANY_ANCESTOR :
          case OpCodes.MATCH_IMMEDIATE_ANCESTOR :
            int tok = compiler.getOp(opPos);

            opPos++;

            switch (tok)
            {
            case OpCodes.NODETYPE_COMMENT :
              targetStrings.addElement(PsuedoNames.PSEUDONAME_COMMENT);
              break;
            case OpCodes.NODETYPE_TEXT :
              targetStrings.addElement(PsuedoNames.PSEUDONAME_TEXT);
              break;
            case OpCodes.NODETYPE_NODE :
              targetStrings.addElement(PsuedoNames.PSEUDONAME_ANY);
              break;
            case OpCodes.NODETYPE_ROOT :
              targetStrings.addElement(PsuedoNames.PSEUDONAME_ROOT);
              break;
            case OpCodes.NODETYPE_ANYELEMENT :
              targetStrings.addElement(PsuedoNames.PSEUDONAME_ANY);
              break;
            case OpCodes.NODETYPE_PI :
              targetStrings.addElement(PsuedoNames.PSEUDONAME_ANY);
              break;
            case OpCodes.NODENAME :

              // Skip the namespace
              int tokenIndex = compiler.getOp(opPos + 1);

              if (tokenIndex >= 0)
              {
                String targetName =
                  (String) compiler.m_tokenQueue[tokenIndex];

                if (targetName.equals("*"))
                {
                  targetStrings.addElement(PsuedoNames.PSEUDONAME_ANY);
                }
                else
                {
                  targetStrings.addElement(targetName);
                }
              }
              else
              {
                targetStrings.addElement(PsuedoNames.PSEUDONAME_ANY);
              }
              break;
            default :
              targetStrings.addElement(PsuedoNames.PSEUDONAME_ANY);
              break;
            }
            break;
          }
        }

        opPos = nextStepPos;
      }

      opPos = nextOpPos;
    }

    m_targetStrings = targetStrings;

    // for(int i = 0; i < m_targetStrings.size(); i++)
    //  System.out.println("targetStrings["+i+"]="+m_targetStrings.elementAt(i));
  }

  /**
   * <meta name="usage" content="advanced"/>
   * This method is for building indexes of match patterns for fast lookup.
   * This allows a caller to get the name or type of a node, and quickly
   * find the likely candidates that may match.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public Vector getTargetElementStrings()
  {
    return m_targetStrings;
  }

  /**
   * Warn the user of an problem.
   *
   * NEEDSDOC @param xctxt
   * NEEDSDOC @param sourceNode
   * NEEDSDOC @param msg
   * NEEDSDOC @param args
   *
   * @throws org.xml.sax.SAXException
   */
  public void warn(
          XPathContext xctxt, Node sourceNode, int msg, Object[] args)
            throws org.xml.sax.SAXException
  {

    String fmsg = XSLMessages.createXPATHWarning(msg, args);
    ErrorHandler ehandler = xctxt.getPrimaryReader().getErrorHandler();

    if (null != ehandler)
    {

      // TO DO: Need to get stylesheet Locator from here.
      ehandler.warning(new TransformException(fmsg));
    }
  }

  /**
   * Tell the user of an assertion error, and probably throw an
   * exception.
   *
   * NEEDSDOC @param b
   * NEEDSDOC @param msg
   *
   * @throws org.xml.sax.SAXException
   */
  public void assert(boolean b, String msg) throws org.xml.sax.SAXException
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
   * NEEDSDOC @param xctxt
   * NEEDSDOC @param sourceNode
   * NEEDSDOC @param msg
   * NEEDSDOC @param args
   *
   * @throws org.xml.sax.SAXException
   */
  public void error(
          XPathContext xctxt, Node sourceNode, int msg, Object[] args)
            throws org.xml.sax.SAXException
  {

    String fmsg = XSLMessages.createXPATHMessage(msg, args);
    ErrorHandler ehandler = xctxt.getPrimaryReader().getErrorHandler();
    TransformException te = new TransformException(fmsg,
                              xctxt.getSAXLocator());

    if (null != ehandler)
      ehandler.fatalError(te);
    else
    {
      System.out.println(te.getMessage() + "; file " + te.getSystemId()
                         + "; line " + te.getLineNumber() + "; column "
                         + te.getColumnNumber());
    }
  }

  /**
   * <meta name="usage" content="advanced"/>
   * The match score if no match is made.
   */
  public static final double MATCH_SCORE_NONE = Double.NEGATIVE_INFINITY;

  /**
   * <meta name="usage" content="advanced"/>
   * The match score if the pattern has the form
   * of a QName optionally preceded by an @ character.
   */
  public static final double MATCH_SCORE_QNAME = 0.0;

  /**
   * <meta name="usage" content="advanced"/>
   * The match score if the pattern pattern has the form NCName:*.
   */
  public static final double MATCH_SCORE_NSWILD = -0.25;

  /**
   * <meta name="usage" content="advanced"/>
   * The match score if the pattern consists of just a NodeTest.
   */
  public static final double MATCH_SCORE_NODETEST = -0.5;

  /**
   * <meta name="usage" content="advanced"/>
   * The match score if the pattern consists of something
   * other than just a NodeTest or just a qname.
   */
  public static final double MATCH_SCORE_OTHER = 0.5;
}
