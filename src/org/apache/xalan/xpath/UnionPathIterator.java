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
package org.apache.xalan.xpath;

// DOM Imports
import org.w3c.dom.traversal.NodeIterator;
import org.w3c.dom.Node;
import org.w3c.dom.DOMException;

// Xalan Imports
import org.apache.xalan.xpath.NodeSet;
import org.apache.xalan.xpath.XPath;
import org.apache.xalan.xpath.XPathContext;

/**
 * <meta name="usage" content="advanced"/>
 * This class extends NodeSet, which implements NodeIterator, 
 * and fetches nodes one at a time in document order based on a XPath
 * <a href="http://www.w3.org/TR/xpath#NT-UnionExpr">UnionExpr</a>.
 * As each node is iterated via nextNode(), the node is also stored 
 * in the NodeVector, so that previousNode() can easily be done.
 */
public class UnionPathIterator extends NodeSet implements Cloneable
{
  /**
   * Constructor to create an instance which you can add location paths to.
   */
  public UnionPathIterator()
  {
    super();
    m_mutable = false;
    m_cacheNodes = false;
    m_iterators = null;
  }
  
  /**
   * Add an iterator to the union list.
   */
  public void addIterator(LocPathIterator iter)
  {
    // Increase array size by only 1 at a time.  Fix this
    // if it looks to be a problem.
    if(null == m_iterators)
    {
      m_iterators = new LocPathIterator[1];
      m_iterators[0] = iter;
    }
    else
    {
      LocPathIterator[] iters = m_iterators;
      int len = m_iterators.length;
      m_iterators = new LocPathIterator[len+1];
      System.arraycopy(iters, 0, m_iterators, 0, len);
      m_iterators[len] = iter;
    }
  
  }

  /**
   * Package-private constructor, which takes the 
   * same arguments as XLocator's union() function, plus the 
   * locator reference.
   */
  UnionPathIterator(XPath xpath, XPathContext execContext, 
                    Node context, int opPos,
                    SimpleNodeLocator locator)
  {
    super();
    m_mutable = false;
    m_cacheNodes = false;

    this.m_xpath = xpath;
    this.m_execContext = execContext;
    this.m_currentContextNode = execContext.getCurrentNode();
    this.m_context = context;
    this.m_locator = locator;
    opPos = xpath.getFirstChildPos(opPos); 
    loadLocationPaths(opPos, 0);
    
    // Swoop through all iterators, asking them to find
    // their first node.
    int n = m_iterators.length;
    for(int i = 0; i < n; i++)
    {
      m_iterators[i].nextNode();
    }
  }
  
  /**
   * Get a cloned LocPathIterator.
   */
  public Object clone()
    throws CloneNotSupportedException
  {
    UnionPathIterator clone = (UnionPathIterator)super.clone();
    int n = m_iterators.length;
    clone.m_iterators = new LocPathIterator[n];
    for(int i = 0; i < n; i++)
    {
      clone.m_iterators[i] = (LocPathIterator)m_iterators[i].clone();
    }
    return clone;
  }
  
  /**
   * Reset the iterator.
   */
  public void reset()
  {
    super.reset();
    m_foundLast = false;
    int n = m_iterators.length;
    for(int i = 0; i < n; i++)
    {
      m_iterators[i].reset();
    }
  }
  
  /**
   * Initialize the location path iterators.
   */
  protected void loadLocationPaths(int opPos, int count)
  {    
    // TODO: Handle unwrapped FilterExpr
    int steptype = m_xpath.m_opMap[opPos];
    if((steptype & OpCodes.LOCATIONPATHEX_MASK) == OpCodes.OP_LOCATIONPATH)
    {
      loadLocationPaths(m_xpath.getNextOpPos(opPos), count+1);
      m_iterators[count] = createLocPathIterator(opPos);
    }
    else
    {
      // Have to check for unwrapped functions, which the LocPathIterator
      // doesn't handle. 
      switch(steptype)
      {
      case OpCodes.OP_VARIABLE:
      case OpCodes.OP_EXTFUNCTION:
      case OpCodes.OP_FUNCTION:
      case OpCodes.OP_GROUP:
        loadLocationPaths(m_xpath.getNextOpPos(opPos), count+1);
        LocPathIterator iter = new LocPathIterator(m_execContext,
                                                   m_execContext.getNamespaceContext(),
                                                   m_context);
        iter.m_xpath = m_xpath;
        iter.m_opPos = opPos;
        iter.m_firstWalker = new org.apache.xalan.xpath.axes.FilterExprWalker(iter);
        iter.m_firstWalker.setOpPos(opPos);
        m_iterators[count] = iter;
        break;
      default:
        m_iterators = new LocPathIterator[count];
      }

      
    }
  }
  
  /**
   * Create a new location path iterator.
   */
  protected LocPathIterator createLocPathIterator(int opPos)
  {
    return new LocPathIterator(m_xpath, m_execContext,
                                        m_context, opPos, m_locator);
  }

  /**
   *  Returns the next node in the set and advances the position of the 
   * iterator in the set. After a NodeIterator is created, the first call 
   * to nextNode() returns the first node in the set.
   * @return  The next <code>Node</code> in the set being iterated over, or
   *   <code>null</code> if there are no more members in that set.
   * @exception DOMException
   *    INVALID_STATE_ERR: Raised if this method is called after the
   *   <code>detach</code> method was invoked.
   */
  public Node nextNode()
    throws DOMException
  {    
    // If the cache is on, and the node has already been found, then 
    // just return from the list.
    if(this.m_cacheNodes && (m_next < (this.size() -1)))
      return super.nextNode();

    if(m_foundLast)
      return null;
    
    // Loop through the iterators getting the current fetched 
    // node, and get the earliest occuring in document order
    Node earliestNode = null;
    int n = m_iterators.length;
    int iteratorUsed = -1;
    for(int i = 0; i < n; i++)
    {
      Node node = m_iterators[i].getCurrentNode();
      if(null == node)
        continue;
      else if(null == earliestNode)
      {
        iteratorUsed = i;
        earliestNode = node;
      }
      else
      {
        if(node.equals(earliestNode))
        {
          // Found a duplicate, so skip past it.
          m_iterators[i].nextNode();
        }
        else
        {
          DOMHelper dh = m_execContext.getDOMHelper();
          if(dh.isNodeAfter(node, earliestNode))
          {
            iteratorUsed = i;
            earliestNode = node;
          }
        }
      }
    }
    
    if(null != earliestNode)
    {
      m_iterators[iteratorUsed].nextNode();
      if(this.m_cacheNodes)
        this.addElement(earliestNode);
      m_next++;
    }
    else
      m_foundLast = true;
    
    return earliestNode;
  }
  
  /**
   * If an index is requested, NodeSet will call this method 
   * to run the iterator to the index.  By default this sets 
   * m_next to the index.  If the index argument is -1, this 
   * signals that the iterator should be run to the end.
   */
  public void runTo(int index)
  {
    if(m_foundLast || ((index >= 0) && (index <= m_next)))
      return;
    
    Node n;
    while(null == (n = nextNode()))
    {
      if(m_next >= index)
        break;
    }
  }
  
  /**
   * Tells if we've found the last node yet.
   */
  protected boolean m_foundLast = false;
  
  /**
   * The XPath that contains the union expression.
   */
  protected XPath m_xpath;
  
  /**
   * The execution context for the expression.
   */
  protected XPathContext m_execContext;
  
  /**
   * The node context for the expression.
   */
  protected Node m_context;
  
  /**
   * The node context from where the Location Path is being 
   * executed from (i.e. for current() support).
   */
  protected Node m_currentContextNode;
  public Node getCurrentContextNode() { return m_currentContextNode; }
    
  /**
   * Reference to the SimpleNodeLocator that created 
   * this instance.
   */
  protected SimpleNodeLocator m_locator;
  
  /**
   * The location path iterators, one for each 
   * <a href="http://www.w3.org/TR/xpath#NT-LocationPath">location 
   * path</a> contained in the union expression.
   */
  protected LocPathIterator[] m_iterators;
  
}
