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
package org.apache.xpath.axes;

import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.VariableComposeState;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.functions.Function;
import org.apache.xpath.operations.Variable;
import org.apache.xpath.parser.Node;
import org.apache.xpath.parser.SimpleNode;
import org.apache.xpath.seqctor.ExprSequence;

/**
 * <meta name="usage" content="advanced"/>
 * This class extends NodeSetDTM, which implements DTMIterator,
 * and fetches nodes one at a time in document order based on a XPath
 * <a href="http://www.w3.org/TR/xpath#NT-UnionExpr">UnionExpr</a>.
 * As each node is iterated via nextNode(), the node is also stored
 * in the NodeVector, so that previousNode() can easily be done.
 */
public class UnionPathIterator
  extends LocPathIterator
  implements Cloneable, DTMIterator, java.io.Serializable, PathComponent
{

  /**
   * Constructor to create an instance which you can add location paths to.
   */
  public UnionPathIterator()
  {

    super();

    // m_mutable = false;
    // m_cacheNodes = false;
    m_iterators = null;
    m_exprs = null;
  }

  /**
   * Initialize the context values for this expression 
   * after it is cloned.
   *
   * @param execContext The XPath runtime context for this 
   * transformation.
   */
  public void setRoot(int context, Object environment)
  {
    super.setRoot(context, environment);
    initIterators(context); 
  }
  
   /**
   * Initialize the context values for this expression 
   * after it is cloned.
   *
   * @param execContext The XPath runtime context for this 
   * transformation.
   */
  public void initIterators(int context)
  {
    try
    {
      if (null != m_exprs)
      {
        int n = m_exprs.length;
        DTMIterator newIters[] = new DTMIterator[n];

        for (int i = 0; i < n; i++)
        {
          DTMIterator iter = m_exprs[i].asIterator(m_execContext, context);
          newIters[i] = iter;
          iter.nextNode();
        }
        m_iterators = newIters;
      }
    }
    catch (Exception e)
    {
      throw new org.apache.xml.utils.WrappedRuntimeException(e);
    }
  }

  /**
   * Add an iterator to the union list.
   *
   * @param iter non-null reference to a location path iterator.
   */
  public void addIterator(DTMIterator expr)
  {

    // Increase array size by only 1 at a time.  Fix this
    // if it looks to be a problem.
    if (null == m_iterators)
    {
      m_iterators = new DTMIterator[1];
      m_iterators[0] = expr;
    }
    else
    {
      DTMIterator[] exprs = m_iterators;
      int len = m_iterators.length;

      m_iterators = new DTMIterator[len + 1];

      System.arraycopy(exprs, 0, m_iterators, 0, len);

      m_iterators[len] = expr;
    }
    expr.nextNode();
    if (expr instanceof Expression)
       ((Expression) expr).exprSetParent(this);
  }

  /**
   *  Detaches the iterator from the set which it iterated over, releasing
   * any computational resources and placing the iterator in the INVALID
   * state. After<code>detach</code> has been invoked, calls to
   * <code>nextNode</code> or<code>previousNode</code> will raise the
   * exception INVALID_STATE_ERR.
   */
  public void detach()
  {
    if (null != m_iterators)
    {
      int n = m_iterators.length;
      for (int i = 0; i < n; i++)
      {
        m_iterators[i].detach();
      }
      m_iterators = null;
    }
  }

  /**
   * @see org.apache.xpath.parser.Node#jjtAddChild(Node, int)
   */
  public void jjtAddChild(Node n, int i)
  {
    if (n instanceof UnionPathIterator)
    {
      int nChildren = n.jjtGetNumChildren();
      for (int j = 0; j < nChildren; j++)
      {
        Node child = n.jjtGetChild(j);
        child.jjtSetParent(this);
        if (j == 0)
          jjtAddChild(child, i);
        else
          jjtAddChild(child, m_exprs.length); // order doesn't matter!
      }
      return;
    }
    if (null == m_exprs)
    {
      m_exprs = new LocPathIterator[i + 1];
    }
    else
      if (i >= m_exprs.length)
      {

        // Slow but space conservative.
        LocPathIterator[] exprs = new LocPathIterator[m_exprs.length + 1];

        System.arraycopy(m_exprs, 0, exprs, 0, m_exprs.length);

        m_exprs = exprs;
      }
    n = fixupPrimarys(n);

    // If a function or variable has been reduced from a path 
    // expression, essentially 
    // we need to turn it back into a path expression here!  There 
    // might be a way to do this a bit earlier.  -sb
    // if(!(((SimpleNode)n).isPathExpr()))
    if(n instanceof Variable || n instanceof Function || n instanceof ExprSequence)
    {
      FilterExprIteratorSimple feis =
        new FilterExprIteratorSimple((Expression) n);
      feis.jjtSetParent(this);
      n = feis;
    }
    m_exprs[i] = (LocPathIterator) n;
  }

  /**
   * @see org.apache.xpath.parser.Node#jjtGetChild(int)
   */
  public Node jjtGetChild(int i)
  {
    return m_exprs[i];
  }

  /**
   * @see org.apache.xpath.parser.Node#jjtGetNumChildren()
   */
  public int jjtGetNumChildren()
  {
    return m_exprs.length;
  }

  /** 
   * Get the analysis bits for this walker, as defined in the WalkerFactory.
   * @return One of WalkerFactory#BIT_DESCENDANT, etc.
   */
  public int getAnalysisBits()
  {
    int bits = 0;

    if (m_exprs != null)
    {
      int n = m_exprs.length;

      for (int i = 0; i < n; i++)
      {
        int bit = m_exprs[i].getAnalysisBits();
        bits |= bit;
      }
    }

    return bits;
  }

  /**
   * Read the object from a serialization stream.
   *
   * @param stream Input stream to read from
   *
   * @throws java.io.IOException
   * @throws javax.xml.transform.TransformerException
   */
  private void readObject(java.io.ObjectInputStream stream)
    throws java.io.IOException, javax.xml.transform.TransformerException
  {
    try
    {
      stream.defaultReadObject();
      m_clones = new IteratorPool(this);
    }
    catch (ClassNotFoundException cnfe)
    {
      throw new javax.xml.transform.TransformerException(cnfe);
    }
  }

  /**
   * Get a cloned LocPathIterator that holds the same 
   * position as this iterator.
   *
   * @return A clone of this iterator that holds the same node position.
   *
   * @throws CloneNotSupportedException
   */
  public Object clone() throws CloneNotSupportedException
  {

    UnionPathIterator clone = (UnionPathIterator) super.clone();

    // %REVIEW% Not 100% sure this clone should be done here! -sb
    if(clone.m_iterators == m_iterators && null != m_iterators)
    {
      DTMIterator[] clonedIters = new DTMIterator[m_iterators.length];
      clone.m_iterators = clonedIters;
      for (int i = 0; i < m_iterators.length; i++)
      {
        clonedIters[i] = (DTMIterator)((Expression)m_iterators[i]).cloneDeep();
      }
    }  
    
    return clone;
  }

  /**
   *  Get a cloned Iterator that is reset to the beginning
   *  of the query.
   * 
   *  @return A cloned NodeIterator set of the start of the query.
   * 
   *  @throws CloneNotSupportedException
   */
  public DTMIterator cloneWithReset() throws CloneNotSupportedException
  {

    UnionPathIterator clone = (UnionPathIterator) super.cloneWithReset();

    clone.resetProximityPositions();

    return clone;
  }

  /**
   *  Returns the next node in the set and advances the position of the
   * iterator in the set. After a DTMIterator is created, the first call
   * to nextNode() returns the first node in the set.
   * 
   * @return  The next <code>Node</code> in the set being iterated over, or
   *   <code>null</code> if there are no more members in that set.
   */
  public int nextNode()
  {
    if (m_foundLast)
      return DTM.NULL;

    if (DTM.NULL == m_lastFetched)
    {
      resetProximityPositions();
    }

    // Loop through the iterators getting the current fetched 
    // node, and get the earliest occuring in document order
    int earliestNode = DTM.NULL;

    if (null != m_iterators)
    {
      int n = m_iterators.length;
      int iteratorUsed = -1;
      int savedIteratorUsed = -1;
      int savedEarliestNode = DTM.NULL;

      while (true)
      {
        for (int i = 0; i < n; i++)
        {
          int node = m_iterators[i].getCurrentNode();

          if (DTM.NULL == node)
            continue;
          else
            if (DTM.NULL == earliestNode)
            {
              iteratorUsed = i;
              earliestNode = node;
            }
            else
            {
              if (node == earliestNode)
              {

                // Found a duplicate, so skip past it.
                // %REVIEW% Make sure this is really what we 
                // want to do for XPath 2.0.
                m_iterators[i].nextNode();
              }
              else
              {
                DTM dtm = getDTM(node);

                if (dtm.isNodeAfter(node, earliestNode))
                {
                  iteratorUsed = i;
                  earliestNode = node;
                }
              }
            }
        }

        if (DTM.NULL == earliestNode)
          break;

        else
          if (getPredicateCount() == 0)
          {
            m_iterators[iteratorUsed].nextNode();
            break;
          }
          else
          {
            m_iterators[iteratorUsed].nextNode();
            int acceptence = acceptNode(earliestNode);

            if (DTMIterator.FILTER_ACCEPT == acceptence)
              break;
              
            earliestNode = DTM.NULL;
          }
      }

      if (DTM.NULL != earliestNode)
      {
        // m_iterators[iteratorUsed].nextNode();

        incrementCurrentPos();
      }
      else
        m_foundLast = true;
    }

    m_lastFetched = earliestNode;

    return earliestNode;
  }

  /**
   * This function is used to fixup variables from QNames to stack frame 
   * indexes at stylesheet build time.
   * 
   * @param vars List of QNames that correspond to variables.  This list 
   * should be searched backwards for the first qualified name that 
   * corresponds to the variable reference qname.  The position of the 
   * QName in the vector from the start of the vector will be its position 
   * in the stack frame (but variables above the globalsTop value will need 
   * to be offset to the current stack frame).
   */
  public void fixupVariables(VariableComposeState vcs)
  {
    for (int i = 0; i < m_exprs.length; i++)
    {
      m_exprs[i].fixupVariables(vcs);
    }

  }

  /**
   * The location path iterators, one for each
   * <a href="http://www.w3.org/TR/xpath#NT-LocationPath">location
   * path</a> contained in the union expression.
   * @serial
   */
  protected LocPathIterator[] m_exprs;

  /**
   * The location path iterators, one for each
   * <a href="http://www.w3.org/TR/xpath#NT-LocationPath">location
   * path</a> contained in the union expression.
   * @serial
   */
  protected DTMIterator[] m_iterators;

  /**
   * Returns the axis being iterated, if it is known.
   * 
   * @return Axis.CHILD, etc., or -1 if the axis is not known or is of multiple 
   * types.
   */
  public int getAxis()
  {
    // Could be smarter.
    return -1;
  }

  class iterOwner implements ExpressionOwner
  {
    int m_index;

    iterOwner(int index)
    {
      m_index = index;
    }

    /**
     * @see ExpressionOwner#getExpression()
     */
    public Expression getExpression()
    {
      return m_exprs[m_index];
    }

    /**
     * @see ExpressionOwner#setExpression(Expression)
     */
    public void setExpression(Expression exp)
    {

      if (!(exp instanceof LocPathIterator))
      {
        // Yuck.  Need FilterExprIter.  Or make it so m_exprs can be just 
        // plain expressions?
        WalkingIterator wi = new WalkingIterator(getPrefixResolver());
        FilterExprWalker few = new FilterExprWalker(wi);
        wi.setFirstWalker(few);
        few.setInnerExpression(exp);
        wi.exprSetParent(UnionPathIterator.this);
        few.exprSetParent(wi);
        exp.exprSetParent(few);
        exp = wi;
      }
      else
        exp.exprSetParent(UnionPathIterator.this);
      m_exprs[m_index] = (LocPathIterator) exp;
    }

  }

  /**
   * @see XPathVisitable#callVisitors(ExpressionOwner, XPathVisitor)
   */
  public void callVisitors(ExpressionOwner owner, XPathVisitor visitor)
  {
    if (visitor.visitUnionPath(owner, this))
    {
      if (null != m_exprs)
      {
        int n = m_exprs.length;
        for (int i = 0; i < n; i++)
        {
          m_exprs[i].callVisitors(new iterOwner(i), visitor);
        }
      }
    }
  }

  /**
   * @see Expression#deepEquals(Expression)
   */
  public boolean deepEquals(Expression expr)
  {
    if (!super.deepEquals(expr))
      return false;

    UnionPathIterator upi = (UnionPathIterator) expr;

    if (null != m_exprs)
    {
      int n = m_exprs.length;

      if ((null == upi.m_exprs) || (upi.m_exprs.length != n))
        return false;

      for (int i = 0; i < n; i++)
      {
        if (!m_exprs[i].deepEquals(upi.m_exprs[i]))
          return false;
      }
    }
    else
      if (null != upi.m_exprs)
      {
        return false;
      }

    return true;
  }

}
