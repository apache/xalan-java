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
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMFilter;
import org.apache.xml.dtm.DTMAxisTraverser;
import org.apache.xml.dtm.Axis;

/**
 * <meta name="usage" content="advanced"/>
 * This class represents a single pattern match step.
 */
public class StepPattern extends NodeTest implements SubContextList
{

  /** NEEDSDOC Field m_axisForPredicate */
  protected int m_axisForPredicate;

  /** NEEDSDOC Field m_axis */
  protected int m_axis;
  
  /**
   * Construct a StepPattern that tests for namespaces and node names.
   *
   *
   * @param whatToShow Bit set defined mainly by {@link org.w3c.dom.traversal.NodeFilter}.
   * @param namespace The namespace to be tested.
   * @param name The local name to be tested.
   */
  public StepPattern(int whatToShow, String namespace, String name,
                     int axis, int axisForPredicate)
  {

    super(whatToShow, namespace, name);

    m_axis = axis;
    m_axisForPredicate = axisForPredicate;
  }

  /**
   * Construct a StepPattern that doesn't test for node names.
   *
   *
   * @param whatToShow Bit set defined mainly by {@link org.w3c.dom.traversal.NodeFilter}.
   */
  public StepPattern(int whatToShow,
                     int axis, int axisForPredicate)
  {

    super(whatToShow);
    
    m_axis = axis;
    m_axisForPredicate = axisForPredicate;
  }

  /**
   * The target local name or psuedo name, for hash table lookup optimization.
   *  @serial
   */
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
    case DTMFilter.SHOW_COMMENT :
      m_targetString = PsuedoNames.PSEUDONAME_COMMENT;
      break;
    case DTMFilter.SHOW_TEXT :
    case DTMFilter.SHOW_CDATA_SECTION :
    case (DTMFilter.SHOW_TEXT | DTMFilter.SHOW_CDATA_SECTION) :
      m_targetString = PsuedoNames.PSEUDONAME_TEXT;
      break;
    case DTMFilter.SHOW_ALL :
      m_targetString = PsuedoNames.PSEUDONAME_ANY;
      break;
    case DTMFilter.SHOW_DOCUMENT :
    case DTMFilter.SHOW_DOCUMENT | DTMFilter.SHOW_DOCUMENT_FRAGMENT :
      m_targetString = PsuedoNames.PSEUDONAME_ROOT;
      break;
    case DTMFilter.SHOW_ELEMENT :
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
    super.fixupVariables(vars, globalsSize);
    if(null != m_predicates)
    {
      for (int i = 0; i < m_predicates.length; i++) 
      {
        m_predicates[i].fixupVariables(vars, globalsSize);
      }
    }
    if(null != m_relativePathPattern)
    {
      m_relativePathPattern.fixupVariables(vars, globalsSize);
    }
  }


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
  
  /**
   * Get the reference to nodetest and predicate for
   * parent or ancestor.
   *
   *
   * @return The relative pattern expression.
   */
  public StepPattern getRelativePathPattern()
  {

    return m_relativePathPattern;
  }
  
//  /**
//   * Set the list of predicate expressions for this pattern step.
//   * @param predicates List of expression objects.
//   */
//  public void setPredicates(Expression[] predicates)
//  {
//    m_predicates = predicates;
//  }
  
  /**
   * Set the list of predicate expressions for this pattern step.
   * @return List of expression objects.
   */
  public Expression[] getPredicates()
  {
    return m_predicates;
  }


  /**
   * The list of predicate expressions for this pattern step.
   *  @serial
   */
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
      if (getPredicate(i).canTraverseOutsideSubtree())
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
  public void calcScore()
  {

    if ((getPredicateCount() > 0) || (null != m_relativePathPattern))
    {
      m_score = SCORE_OTHER;
    }
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
  public XObject execute(XPathContext xctxt, int currentNode)
          throws javax.xml.transform.TransformerException
  {

    if (m_whatToShow == NodeTest.SHOW_BYFUNCTION)
    {
      if (null != m_relativePathPattern)
      {
        return m_relativePathPattern.execute(xctxt, currentNode);
      }
      else
        return NodeTest.SCORE_NONE;
    }

    if (null == m_relativePathPattern)
    {
      return super.execute(xctxt, currentNode);
    }
    else
    {
      if (super.execute(xctxt, currentNode) == NodeTest.SCORE_NONE)
        return NodeTest.SCORE_NONE;
      return m_relativePathPattern.executeRelativePathPattern(xctxt, this);
    }
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
  public XObject execute(XPathContext xctxt)
          throws javax.xml.transform.TransformerException
  {

    if (m_whatToShow == NodeTest.SHOW_BYFUNCTION)
    {
      if (null != m_relativePathPattern)
      {
        return m_relativePathPattern.execute(xctxt);
      }
      else
        return NodeTest.SCORE_NONE;
    }

    XObject score = super.execute(xctxt, xctxt.getCurrentNode());

    if (score == NodeTest.SCORE_NONE)
      return score;

    else if (null != m_relativePathPattern)
    {
      return m_relativePathPattern.executeRelativePathPattern(xctxt, this);
    }
    else
    {
      return score;
    }
  }
  
  /**
   * Execute an expression in the XPath runtime context, and return the 
   * result of the expression.
   *
   *
   * @param xctxt The XPath runtime context.
   * @param currentNode The currentNode.
   * @param dtm The DTM of the current node.
   * @param expType The expanded type ID of the current node.
   *
   * @return The result of the expression in the form of a <code>XObject</code>.
   *
   * @throws javax.xml.transform.TransformerException if a runtime exception 
   *         occurs.
   */
  public XObject execute(XPathContext xctxt, int currentNode, 
                         DTM dtm, int expType)
    throws javax.xml.transform.TransformerException
  {
    if (m_whatToShow == NodeTest.SHOW_BYFUNCTION)
    {
      if (null != m_relativePathPattern)
      {
        return m_relativePathPattern.execute(xctxt);
      }
      else
        return NodeTest.SCORE_NONE;
    }

    XObject score = super.execute(xctxt, currentNode, dtm, expType);

    if (score == NodeTest.SCORE_NONE)
      return score;

    else if (null != m_relativePathPattern)
    {
      return m_relativePathPattern.executeRelativePathPattern(xctxt, this);
    }
    else
    {
      return score;
    }
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
  public int getProximityPosition(XPathContext xctxt, int predPos)
  {

    int context = xctxt.getCurrentNode();
    DTM dtm = xctxt.getDTM(context);
    int pos = 0;

    int parentContext = xctxt.getPredicateRoot();    
      
    try
    {
      xctxt.pushCurrentNode(parentContext);

      DTMAxisTraverser traverser = dtm.getAxisTraverser(m_axisForPredicate);

      for (int child = traverser.first(parentContext); DTM.NULL != child;
              child = traverser.next(parentContext, child))
      {
        try
        {
          xctxt.pushCurrentNode(child);

          if (NodeTest.SCORE_NONE != super.execute(xctxt, child))
          {
            boolean pass = true;
            
            try
            {
              xctxt.pushSubContextList(this);
              xctxt.pushPredicateRoot(parentContext);
      
              for (int i = 0; i < predPos; i++)
              {
                XObject pred = m_predicates[i].execute(xctxt);

                if (XObject.CLASS_NUMBER == pred.getType())
                {
                  if ((pos+1) != (int) pred.num())
                  {
                    pass = false;
                    break;
                  }
                }
                else if (!pred.bool())
                {
                  pass = false;
                  break;
                }
              }
            }
            finally
            {
              xctxt.popSubContextList();
              xctxt.popPredicateRoot();
            }              
            
            if(pass)
              pos++;

            if (child == context)
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

    return pos;
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

    return getProximityPosition(xctxt, xctxt.getPredicatePos());
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

    int context = xctxt.getCurrentNode();
    DTM dtm = xctxt.getDTM(context);

    int parentContext = xctxt.getPredicateRoot();    
    {

      // System.out.println("parentContext: "+parentContext.getNodeName());
      try
      {
        xctxt.pushCurrentNode(parentContext);

        int count = 0;
        DTMAxisTraverser traverser = dtm.getAxisTraverser(m_axisForPredicate);

        for (int child = traverser.first(parentContext); DTM.NULL != child;
                child = traverser.next(parentContext, child))
        {
          try
          {
            xctxt.pushCurrentNode(child);

            if (NodeTest.SCORE_NONE != super.execute(xctxt, child))
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

    // return 0;
  }

  /**
   * Execute the match pattern step relative to another step.
   *
   *
   * @param xctxt The XPath runtime context.
   * NEEDSDOC @param prevStep
   *
   * @return {@link org.apache.xpath.patterns.NodeTest#SCORE_NODETEST},
   *         {@link org.apache.xpath.patterns.NodeTest#SCORE_NONE},
   *         {@link org.apache.xpath.patterns.NodeTest#SCORE_NSWILD},
   *         {@link org.apache.xpath.patterns.NodeTest#SCORE_QNAME}, or
   *         {@link org.apache.xpath.patterns.NodeTest#SCORE_OTHER}.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject executeRelativePathPattern(
          XPathContext xctxt, StepPattern prevStep)
            throws javax.xml.transform.TransformerException
  {

    XObject score = NodeTest.SCORE_NONE;
    int context = xctxt.getCurrentNode();
    DTM dtm = xctxt.getDTM(context);

    if (null != dtm)
    {
      int predContext = xctxt.getCurrentNode();
      DTMAxisTraverser traverser;
      
      int axis = m_axis;
      
      traverser = dtm.getAxisTraverser(axis);

      for (int relative = traverser.first(context); DTM.NULL != relative;
              relative = traverser.next(context, relative))
      {
        try
        {
          xctxt.pushCurrentNode(relative);

          score = execute(xctxt);
          
          if (score != NodeTest.SCORE_NONE)
          {
            score = executePredicates( xctxt, prevStep, SCORE_OTHER, 
                       predContext, relative);

            if (score != NodeTest.SCORE_NONE)
              break;
          }
        }
        finally
        {
          xctxt.popCurrentNode();
        }
      }
    }

    return score;
  }

  /**
   * NEEDSDOC Method executePredicates 
   *
   *
   * NEEDSDOC @param xctxt
   * NEEDSDOC @param prevStep
   * NEEDSDOC @param score
   *
   * NEEDSDOC (executePredicates) @return
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected static XObject executePredicates(
          XPathContext xctxt, StepPattern prevStep, XObject score, 
          int context, int predicateRootContext)
            throws javax.xml.transform.TransformerException
  {

    int n = prevStep.getPredicateCount();

    if (n != 0)
    {
      try
      {
        xctxt.pushCurrentNode(context);
        xctxt.pushSubContextList(prevStep);
        xctxt.pushPredicateRoot(predicateRootContext);

        for (int i = 0; i < n; i++)
        {
          xctxt.pushPredicatePos(i);
          try
          {
            XObject pred = prevStep.m_predicates[i].execute(xctxt);
  
            if (XObject.CLASS_NUMBER == pred.getType())
            {
              int pos = (int) pred.num();
              if (prevStep.getProximityPosition(xctxt, i) != pos)
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
          }
          finally
          {
            xctxt.popPredicatePos();
          }
        }
      }
      finally
      {
        xctxt.popCurrentNode();
        xctxt.popSubContextList();
        xctxt.popPredicateRoot();
      }
    }

    return score;
  }
  
  public String toString()
  {
    StringBuffer buf = new StringBuffer();
    for(StepPattern pat = this; pat != null; pat = pat.m_relativePathPattern)
    {
      if(pat != this)
        buf.append("/");
      buf.append(Axis.names[pat.m_axis]);
      buf.append("::");
      if(0x000005000 == pat.m_whatToShow)
      {
        buf.append("doc()");
      }
      else if(DTMFilter.SHOW_BYFUNCTION == pat.m_whatToShow)
      {
        buf.append("function()");
      }
      else if(DTMFilter.SHOW_ALL == pat.m_whatToShow)
      {
        buf.append("node()");
      }
      else if(DTMFilter.SHOW_TEXT == pat.m_whatToShow)
      {
        buf.append("text()");
      }
      else if(DTMFilter.SHOW_PROCESSING_INSTRUCTION == pat.m_whatToShow)
      {
        buf.append("processing-instruction(");
        if(null != pat.m_name)
        {
          buf.append(pat.m_name);
        }
        buf.append(")");
      }
      else if(DTMFilter.SHOW_COMMENT == pat.m_whatToShow)
      {
        buf.append("comment()");
      }
      else if(null != pat.m_name)
      {
        if(DTMFilter.SHOW_ATTRIBUTE == pat.m_whatToShow)
        {
          buf.append("@");
        }
        if(null != pat.m_namespace)
        {
          buf.append("{");
          buf.append(pat.m_namespace);
          buf.append("}");
        }
        buf.append(pat.m_name);
      }
      else if(DTMFilter.SHOW_ATTRIBUTE == pat.m_whatToShow)
      {
        buf.append("@");
      }
      else if((DTMFilter.SHOW_DOCUMENT | DTMFilter.SHOW_DOCUMENT_FRAGMENT) 
              == pat.m_whatToShow)
      {
        buf.append("doc-root()");
      }
      else
      {
        buf.append("?"+Integer.toHexString(pat.m_whatToShow));
      }
      if(null != pat.m_predicates)
      {
        for (int i = 0; i < pat.m_predicates.length; i++) 
        {
          buf.append("[");
          buf.append(pat.m_predicates[i]);
          buf.append("]");
        }
        
      }
    }
    return buf.toString();
  }

  /** Set to true to send diagnostics about pattern matches to the consol. */
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
  public double getMatchScore(XPathContext xctxt, int context)
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
  
  public void setAxis(int axis)
  {
    m_axis = axis;
  }
  
  public int getAxis()
  {
    return m_axis;
  }
  
  public void setPredicateAxis(int axisForPredicate)
  {
    m_axisForPredicate = axisForPredicate;
  }
  
  public int getPredicateAxis()
  {
    return m_axisForPredicate;
  }

}
