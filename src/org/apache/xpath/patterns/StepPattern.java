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
package org.apache.xpath.patterns;

import org.apache.xpath.Expression;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.XPathContext;
import org.apache.xalan.utils.PrefixResolver;
import org.apache.xpath.axes.SubContextList;
import org.apache.xpath.compiler.PsuedoNames;

import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;

/**
 * <meta name="usage" content="internal"/>
 * NEEDSDOC Class StepPattern <needs-comment/>
 */
public class StepPattern extends NodeTest implements SubContextList
{

  /**
   * Constructor StepPattern
   *
   *
   * NEEDSDOC @param whatToShow
   * NEEDSDOC @param namespace
   * NEEDSDOC @param name
   */
  public StepPattern(int whatToShow, String namespace, String name)
  {
    super(whatToShow, namespace, name);
  }

  /**
   * Constructor StepPattern
   *
   *
   * NEEDSDOC @param whatToShow
   */
  public StepPattern(int whatToShow)
  {
    super(whatToShow);
  }

  /** NEEDSDOC Field m_targetString          */
  String m_targetString;  // only calculate on head

  /**
   * NEEDSDOC Method calcTargetString 
   *
   */
  public void calcTargetString()
  {

    int whatToShow = getWhatToShow();

    switch (whatToShow)
    {
    case NodeFilter.SHOW_COMMENT :
      m_targetString = PsuedoNames.PSEUDONAME_COMMENT;
      break;
    case NodeFilter.SHOW_TEXT :
      m_targetString = PsuedoNames.PSEUDONAME_TEXT;
      break;
    case NodeFilter.SHOW_ALL :
      m_targetString = PsuedoNames.PSEUDONAME_ANY;
      break;
    case NodeFilter.SHOW_DOCUMENT :
    case NodeFilter.SHOW_DOCUMENT | NodeFilter.SHOW_DOCUMENT_FRAGMENT :
      m_targetString = PsuedoNames.PSEUDONAME_ROOT;
      break;
    case NodeFilter.SHOW_ELEMENT :
      if (this.WILD == m_name)
        m_targetString = PsuedoNames.PSEUDONAME_ANY;
      else
        m_targetString = m_name;
      break;
    default :
      m_targetString = PsuedoNames.PSEUDONAME_ANY;
      break;
    }
  }

  /**
   * NEEDSDOC Method getTargetString 
   *
   *
   * NEEDSDOC (getTargetString) @return
   */
  public String getTargetString()
  {
    return m_targetString;
  }

  /**
   * Reference to nodetest and predicate for
   * parent or ancestor.
   */
  StepPattern m_relativePathPattern;

  /**
   * NEEDSDOC Method setRelativePathPattern 
   *
   *
   * NEEDSDOC @param expr
   */
  public void setRelativePathPattern(StepPattern expr)
  {

    m_relativePathPattern = expr;

    calcScore();
  }

  /** NEEDSDOC Field m_predicates          */
  Expression[] m_predicates;

  /**
   * NEEDSDOC Method getPredicate 
   *
   *
   * NEEDSDOC @param i
   *
   * NEEDSDOC (getPredicate) @return
   */
  public Expression getPredicate(int i)
  {
    return m_predicates[i];
  }

  /**
   * NEEDSDOC Method getPredicateCount 
   *
   *
   * NEEDSDOC (getPredicateCount) @return
   */
  public final int getPredicateCount()
  {
    return (null == m_predicates) ? 0 : m_predicates.length;
  }

  /**
   * NEEDSDOC Method setPredicates 
   *
   *
   * NEEDSDOC @param predicates
   */
  public void setPredicates(Expression[] predicates)
  {

    m_predicates = predicates;

    calcScore();
  }

  /**
   * Static calc of match score.
   */
  protected void calcScore()
  {

    if ((getPredicateCount() > 0) || (null != m_relativePathPattern))
      m_score = SCORE_OTHER;
    else
      super.calcScore();

    if (null == m_targetString)
      calcTargetString();
  }

  /**
   * NEEDSDOC Method executeStep 
   *
   *
   * NEEDSDOC @param xctxt
   *
   * NEEDSDOC (executeStep) @return
   *
   * @throws org.xml.sax.SAXException
   */
  public XObject executeStep(XPathContext xctxt)
          throws org.xml.sax.SAXException
  {

    XObject score;
    short nodeType = xctxt.getCurrentNode().getNodeType();

    if (nodeType == Node.ATTRIBUTE_NODE
            && m_whatToShow != NodeFilter.SHOW_ATTRIBUTE)
    {
      score = NodeTest.SCORE_NONE;
    }
    else if ((nodeType == Node.DOCUMENT_NODE || nodeType == Node.DOCUMENT_FRAGMENT_NODE)
             && m_whatToShow != (NodeFilter.SHOW_DOCUMENT | NodeFilter.SHOW_DOCUMENT_FRAGMENT))
    {
      score = NodeTest.SCORE_NONE;
    }
    else
      score = super.execute(xctxt);

    if (score == NodeTest.SCORE_NONE)
    {

      // System.out.println("executeStep: "+this.m_name+" = "+score);
      return score;
    }

    int n = getPredicateCount();

    if (n == 0)
      return score;

    // xctxt.setNamespaceContext(m_lpi.getPrefixResolver());
    // xctxt.pushCurrentNode(context);
    try
    {
      xctxt.pushSubContextList(this);

      for (int i = 0; i < n; i++)
      {
        XObject pred;

        pred = m_predicates[i].execute(xctxt);

        if (XObject.CLASS_NUMBER == pred.getType())
        {
          if (this.getProximityPosition(xctxt) != (int) pred.num())
          {
            score = NodeTest.SCORE_NONE;

            break;
          }
        }
        else if (!pred.bool())
        {
          score = NodeTest.SCORE_NONE;

          break;
        }

        // countProximityPosition(++m_predicateIndex);
      }
    }
    finally
    {
      xctxt.popSubContextList();
    }

    // System.out.println("executeStep: "+this.m_name+" = "+score);
    return score;
  }

  /**
   * NEEDSDOC Method getProximityPosition 
   *
   *
   * NEEDSDOC @param xctxt
   *
   * NEEDSDOC (getProximityPosition) @return
   */
  public int getProximityPosition(XPathContext xctxt)
  {

    Node context = xctxt.getCurrentNode();

    // System.out.println("context: "+context.getNodeName());
    Node parentContext = xctxt.getDOMHelper().getParentOfNode(context);

    // System.out.println("parentContext: "+parentContext.getNodeName());
    try
    {
      xctxt.pushCurrentNode(parentContext);

      int pos = 0;

      for (Node child = parentContext.getFirstChild(); child != null;
              child = child.getNextSibling())
      {
        try
        {
          xctxt.pushCurrentNode(child);

          if (NodeTest.SCORE_NONE != super.execute(xctxt))
          {
            pos++;

            if (child.equals(context))
            {
              return pos;
            }
          }
        }
        finally
        {
          xctxt.popCurrentNode();
        }
      }
    }
    catch (org.xml.sax.SAXException se)
    {

      // TODO: should keep throw sax exception...
      throw new java.lang.RuntimeException(se.getMessage());
    }
    finally
    {
      xctxt.popCurrentNode();

      // xctxt.popContextNodeList();
    }

    return 0;
  }

  /**
   * NEEDSDOC Method getLastPos 
   *
   *
   * NEEDSDOC @param xctxt
   *
   * NEEDSDOC (getLastPos) @return
   */
  public int getLastPos(XPathContext xctxt)
  {

    Node context = xctxt.getCurrentNode();
    Node parentContext = xctxt.getDOMHelper().getParentOfNode(context);

    try
    {
      xctxt.pushCurrentNode(parentContext);

      int count = 0;

      for (Node child = parentContext.getFirstChild(); child != null;
              child = child.getNextSibling())
      {
        try
        {
          xctxt.pushCurrentNode(child);

          if (NodeTest.SCORE_NONE != super.execute(xctxt))
            count++;
        }
        finally
        {
          xctxt.popCurrentNode();
        }
      }

      return count;
    }
    catch (org.xml.sax.SAXException se)
    {

      // TODO: should keep throw sax exception...
      throw new java.lang.RuntimeException(se.getMessage());
    }
    finally
    {
      xctxt.popCurrentNode();

      // xctxt.popContextNodeList();
    }
  }

  /**
   * NEEDSDOC Method executeRelativePathPattern 
   *
   *
   * NEEDSDOC @param xctxt
   *
   * NEEDSDOC (executeRelativePathPattern) @return
   *
   * @throws org.xml.sax.SAXException
   */
  public XObject executeRelativePathPattern(XPathContext xctxt)
          throws org.xml.sax.SAXException
  {

    XObject score;
    Node parent =
      xctxt.getDOMHelper().getParentOfNode(xctxt.getCurrentNode());

    if (null != parent)
    {
      try
      {
        xctxt.pushCurrentNode(parent);

        score = execute(xctxt);

        if (score != NodeTest.SCORE_NONE)
          score = SCORE_OTHER;
      }
      finally
      {
        xctxt.popCurrentNode();
      }
    }
    else
      score = NodeTest.SCORE_NONE;

    return score;
  }

  /**
   * NEEDSDOC Method execute 
   *
   *
   * NEEDSDOC @param xctxt
   *
   * NEEDSDOC (execute) @return
   *
   * @throws org.xml.sax.SAXException
   */
  public XObject execute(XPathContext xctxt) throws org.xml.sax.SAXException
  {

    XObject score;

    // int n = getPredicateCount();
    score = executeStep(xctxt);

    if ((score != NodeTest.SCORE_NONE) && (null != m_relativePathPattern))
    {
      score = m_relativePathPattern.executeRelativePathPattern(xctxt);
    }

    return score;
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
      XObject score = execute(xctxt);

      return score.num();
    }
    finally
    {
      xctxt.popCurrentNode();
      xctxt.popCurrentExpressionNode();
    }

    // return XPath.MATCH_SCORE_NONE;
  }
}
