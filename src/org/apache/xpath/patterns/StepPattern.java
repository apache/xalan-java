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
import org.apache.xml.utils.PrefixResolver;
import org.apache.xpath.axes.SubContextList;
import org.apache.xpath.compiler.PsuedoNames;

import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;

/**
 * <meta name="usage" content="advanced"/>
 * This class represents a single pattern match step.
 */
public class StepPattern extends NodeTest implements SubContextList
{

  /**
   * Construct a StepPattern that tests for namespaces and node names.
   *
   *
   * @param whatToShow Bit set defined mainly by {@link org.w3c.dom.traversal.NodeFilter}.
   * @param namespace The namespace to be tested.
   * @param name The local name to be tested.
   */
  public StepPattern(int whatToShow, String namespace, String name)
  {
    
    super(whatToShow, namespace, name);
  }

  /**
   * Construct a StepPattern that doesn't test for node names.
   *
   *
   * @param whatToShow Bit set defined mainly by {@link org.w3c.dom.traversal.NodeFilter}.
   */
  public StepPattern(int whatToShow)
  {
    super(whatToShow);
  }

  /** The target local name or psuedo name, for hash table lookup optimization.
   *  @serial  */
  String m_targetString;  // only calculate on head
  
  /**
   * Calculate the local name or psuedo name of the node that this pattern will test, 
   * for hash table lookup optimization.
   *
   * @see org.apache.xpath.compiler.PsuedoNames
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
    case NodeFilter.SHOW_CDATA_SECTION :
    case (NodeFilter.SHOW_TEXT | NodeFilter.SHOW_CDATA_SECTION):
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
   * Get the local name or psuedo name of the node that this pattern will test, 
   * for hash table lookup optimization.
   *
   *
   * @return local name or psuedo name of the node.
   * @see org.apache.xpath.compiler.PsuedoNames
   */
  public String getTargetString()
  {
    return m_targetString;
  }

  /**
   * Reference to nodetest and predicate for
   * parent or ancestor.
   * @serial
   */
  StepPattern m_relativePathPattern;

  /**
   * Set the reference to nodetest and predicate for
   * parent or ancestor.
   *
   *
   * @param expr The relative pattern expression.
   */
  public void setRelativePathPattern(StepPattern expr)
  {

    m_relativePathPattern = expr;

    calcScore();
  }

  /** The list of predicate expressions for this pattern step.
   *  @serial   */
  Expression[] m_predicates;
  
  /**
   * Tell if this expression or it's subexpressions can traverse outside 
   * the current subtree.
   * 
   * NOTE: Ancestors tests with predicates are problematic, and will require 
   * special treatment.
   * 
   * @return true if traversal outside the context node's subtree can occur.
   */
   public boolean canTraverseOutsideSubtree()
   {
    int n = getPredicateCount();
    for (int i = 0; i < n; i++) 
    {
      if(getPredicate(i).canTraverseOutsideSubtree())
        return true;
    }
    
    return false;
   }

  /**
   * Get a predicate expression.
   *
   *
   * @param i The index of the predicate.
   *
   * @return A predicate expression.
   */
  public Expression getPredicate(int i)
  {
    return m_predicates[i];
  }

  /**
   * Get the number of predicates for this match pattern step.
   *
   *
   * @return the number of predicates for this match pattern step.
   */
  public final int getPredicateCount()
  {
    return (null == m_predicates) ? 0 : m_predicates.length;
  }

  /**
   * Set the predicates for this match pattern step.
   *
   *
   * @param predicates An array of expressions that define predicates 
   *                   for this step.
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
   * Execute this pattern step, including predicates.
   *
   *
   * @param xctxt XPath runtime context.
   *
   * @return {@link org.apache.xpath.patterns.NodeTest#SCORE_NODETEST}, 
   *         {@link org.apache.xpath.patterns.NodeTest#SCORE_NONE}, 
   *         {@link org.apache.xpath.patterns.NodeTest#SCORE_NSWILD}, 
   *         {@link org.apache.xpath.patterns.NodeTest#SCORE_QNAME}, or
   *         {@link org.apache.xpath.patterns.NodeTest#SCORE_OTHER}.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject executeStep(XPathContext xctxt)
          throws javax.xml.transform.TransformerException
  {

    XObject score;

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
   * Get the proximity position index of the current node based on this 
   * node test.
   *
   *
   * @param xctxt XPath runtime context.
   *
   * @return the proximity position index of the current node based on the 
   *         node test.
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
    catch (javax.xml.transform.TransformerException se)
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
   * Get the count of the nodes that match the test, which is the proximity 
   * position of the last node that can pass this test in the sub context 
   * selection.  In XSLT 1-based indexing, this count is the index of the last 
   * node.
   *
   *
   * @param xctxt XPath runtime context.
   *
   * @return the count of the nodes that match the test.
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
    catch (javax.xml.transform.TransformerException se)
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
   * Execute the match pattern step relative to another step.
   *
   *
   * @param xctxt The XPath runtime context.
   *
   * @return {@link org.apache.xpath.patterns.NodeTest#SCORE_NODETEST}, 
   *         {@link org.apache.xpath.patterns.NodeTest#SCORE_NONE}, 
   *         {@link org.apache.xpath.patterns.NodeTest#SCORE_NSWILD}, 
   *         {@link org.apache.xpath.patterns.NodeTest#SCORE_QNAME}, or
   *         {@link org.apache.xpath.patterns.NodeTest#SCORE_OTHER}.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject executeRelativePathPattern(XPathContext xctxt)
          throws javax.xml.transform.TransformerException
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
   * Test the current node to see if it matches the given node test, and if 
   * it does, and there is a relative path pattern, execute that to see if it 
   * matches also.
   *
   * @param xctxt XPath runtime context.
   *
   * @return {@link org.apache.xpath.patterns.NodeTest#SCORE_NODETEST},
   *         {@link org.apache.xpath.patterns.NodeTest#SCORE_NONE},
   *         {@link org.apache.xpath.patterns.NodeTest#SCORE_NSWILD},
   *         {@link org.apache.xpath.patterns.NodeTest#SCORE_QNAME}, or
   *         {@link org.apache.xpath.patterns.NodeTest#SCORE_OTHER}.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
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

  /** Set to true to send diagnostics about pattern matches to the consol.  */
  private static final boolean DEBUG_MATCHES = false;

  /**
   * Get the match score of the given node.
   *
   * @param xctxt The XPath runtime context.
   * @param context The node to be tested.
   *
   * @return {@link org.apache.xpath.patterns.NodeTest#SCORE_NODETEST},
   *         {@link org.apache.xpath.patterns.NodeTest#SCORE_NONE},
   *         {@link org.apache.xpath.patterns.NodeTest#SCORE_NSWILD},
   *         {@link org.apache.xpath.patterns.NodeTest#SCORE_QNAME}, or
   *         {@link org.apache.xpath.patterns.NodeTest#SCORE_OTHER}.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public double getMatchScore(XPathContext xctxt, Node context)
          throws javax.xml.transform.TransformerException
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
