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

import org.apache.xpath.compiler.OpCodes;

// DOM Imports
import org.w3c.dom.traversal.NodeIterator;
import org.w3c.dom.Node;
import org.w3c.dom.DOMException;
import org.w3c.dom.traversal.NodeFilter;

// Xalan Imports
import org.apache.xpath.NodeSet;
import org.apache.xpath.Expression;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.DOMHelper;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.compiler.Compiler;
import org.apache.xalan.utils.ObjectPool;

/**
 * <meta name="usage" content="advanced"/>
 * This class extends NodeSet, which implements NodeIterator,
 * and fetches nodes one at a time in document order based on a XPath
 * <a href="http://www.w3.org/TR/xpath#NT-UnionExpr">UnionExpr</a>.
 * As each node is iterated via nextNode(), the node is also stored
 * in the NodeVector, so that previousNode() can easily be done.
 */
public class UnionPathIterator extends Expression
        implements Cloneable, NodeIterator, ContextNodeList
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
  }

  /**
   * NEEDSDOC Method initContext 
   *
   *
   * NEEDSDOC @param execContext
   */
  public void initContext(XPathContext execContext)
  {

    this.m_execContext = execContext;
    this.m_currentContextNode = execContext.getCurrentExpressionNode();
    this.m_context = execContext.getCurrentNode();

    if (null != m_iterators)
    {
      int n = m_iterators.length;

      for (int i = 0; i < n; i++)
      {
        m_iterators[i].initContext(execContext);
        m_iterators[i].nextNode();
      }
    }
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

    this.m_context = null;
    this.m_execContext = null;
    this.m_context = null;

    int n = m_iterators.length;

    for (int i = 0; i < n; i++)
    {
      m_iterators[i].detach();
    }

    m_pool.freeInstance(this);
  }

  /** NEEDSDOC Field m_pool          */
  ObjectPool m_pool = new ObjectPool();

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

    try
    {
      UnionPathIterator clone =
        (UnionPathIterator) m_pool.getInstanceIfFree();

      if (null == clone)
        clone = (UnionPathIterator) this.clone();

      clone.initContext(xctxt);

      return new XNodeSet(clone);
    }
    catch (CloneNotSupportedException ncse){}

    return null;
  }

  /** NEEDSDOC Field m_cachedNodes          */
  NodeSet m_cachedNodes = null;

  /** NEEDSDOC Field m_next          */
  protected int m_next = 0;

  /**
   * If setShouldCacheNodes(true) is called, then nodes will
   * be cached.  They are not cached by default.
   *
   * NEEDSDOC @param b
   */
  public void setShouldCacheNodes(boolean b)
  {

    if (b)
      m_cachedNodes = new NodeSet();
    else
      m_cachedNodes = null;
  }

  /**
   * Set the current position in the node set.
   * @param i Must be a valid index.
   */
  public void setCurrentPos(int i)
  {

    if (null == m_cachedNodes)
      throw new RuntimeException(
        "This NodeSet can not do indexing or counting functions!");

    m_next = i;

    m_cachedNodes.setCurrentPos(i);
  }

  /**
   * Get the length of the list.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public int size()
  {

    if (null == m_cachedNodes)
      return 0;

    return m_cachedNodes.size();
  }

  /**
   * Tells if this NodeSet is "fresh", in other words, if
   * the first nextNode() that is called will return the
   * first node in the set.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public boolean isFresh()
  {
    return (m_next == 0);
  }

  /**
   *  Returns the previous node in the set and moves the position of the
   * iterator backwards in the set.
   * @return  The previous <code>Node</code> in the set being iterated over,
   *   or<code>null</code> if there are no more members in that set.
   * @exception DOMException
   *    INVALID_STATE_ERR: Raised if this method is called after the
   *   <code>detach</code> method was invoked.
   */
  public Node previousNode() throws DOMException
  {

    if (null == m_cachedNodes)
      throw new RuntimeException(
        "This NodeSet can not iterate to a previous node!");

    return m_cachedNodes.previousNode();
  }

  /**
   *  This attribute determines which node types are presented via the
   * iterator. The available set of constants is defined in the
   * <code>NodeFilter</code> interface.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public int getWhatToShow()
  {

    // TODO: ??
    return NodeFilter.SHOW_ALL & ~NodeFilter.SHOW_ENTITY_REFERENCE;
  }

  /**
   *  The filter used to screen nodes.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public NodeFilter getFilter()
  {
    return null;
  }

  /**
   *  The root node of the Iterator, as specified when it was created.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public Node getRoot()
  {
    return m_context;
  }

  /**
   *  The value of this flag determines whether the children of entity
   * reference nodes are visible to the iterator. If false, they will be
   * skipped over.
   * <br> To produce a view of the document that has entity references
   * expanded and does not expose the entity reference node itself, use the
   * whatToShow flags to hide the entity reference node and set
   * expandEntityReferences to true when creating the iterator. To produce
   * a view of the document that has entity reference nodes but no entity
   * expansion, use the whatToShow flags to show the entity reference node
   * and set expandEntityReferences to false.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public boolean getExpandEntityReferences()
  {
    return true;
  }

  /**
   * Add an iterator to the union list.
   *
   * NEEDSDOC @param iter
   */
  public void addIterator(LocPathIterator iter)
  {

    // Increase array size by only 1 at a time.  Fix this
    // if it looks to be a problem.
    if (null == m_iterators)
    {
      m_iterators = new LocPathIterator[1];
      m_iterators[0] = iter;
    }
    else
    {
      LocPathIterator[] iters = m_iterators;
      int len = m_iterators.length;

      m_iterators = new LocPathIterator[len + 1];

      System.arraycopy(iters, 0, m_iterators, 0, len);

      m_iterators[len] = iter;
    }
  }

  /**
   * Constructor.
   *
   * NEEDSDOC @param compiler
   * NEEDSDOC @param opPos
   *
   * @throws org.xml.sax.SAXException
   */
  public UnionPathIterator(Compiler compiler, int opPos)
          throws org.xml.sax.SAXException
  {

    super();

    opPos = compiler.getFirstChildPos(opPos);

    loadLocationPaths(compiler, opPos, 0);
  }

  /**
   * Get a cloned UnionPathIterator.
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws CloneNotSupportedException
   */
  public NodeIterator cloneWithReset() throws CloneNotSupportedException
  {

    UnionPathIterator clone = (UnionPathIterator) clone();

    clone.reset();

    return clone;
  }

  /**
   * Get a cloned UnionPathIterator.
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws CloneNotSupportedException
   */
  public Object clone() throws CloneNotSupportedException
  {

    UnionPathIterator clone = (UnionPathIterator) super.clone();
    int n = m_iterators.length;

    clone.m_iterators = new LocPathIterator[n];

    for (int i = 0; i < n; i++)
    {
      clone.m_iterators[i] = (LocPathIterator) m_iterators[i].clone();
    }

    return clone;
  }

  /**
   * Reset the iterator.
   */
  public void reset()
  {

    // super.reset();
    m_foundLast = false;
    m_next = 0;

    int n = m_iterators.length;

    for (int i = 0; i < n; i++)
    {
      m_iterators[i].reset();
    }
  }

  /**
   * Initialize the location path iterators.
   *
   * NEEDSDOC @param compiler
   * NEEDSDOC @param opPos
   * NEEDSDOC @param count
   *
   * @throws org.xml.sax.SAXException
   */
  protected void loadLocationPaths(Compiler compiler, int opPos, int count)
          throws org.xml.sax.SAXException
  {

    // TODO: Handle unwrapped FilterExpr
    int steptype = compiler.getOpMap()[opPos];

    if (steptype == OpCodes.OP_LOCATIONPATH)
    {
      loadLocationPaths(compiler, compiler.getNextOpPos(opPos), count + 1);

      m_iterators[count] = createLocPathIterator(compiler, opPos);
    }
    else
    {

      // Have to check for unwrapped functions, which the LocPathIterator
      // doesn't handle. 
      switch (steptype)
      {
      case OpCodes.OP_VARIABLE :
      case OpCodes.OP_EXTFUNCTION :
      case OpCodes.OP_FUNCTION :
      case OpCodes.OP_GROUP :
        loadLocationPaths(compiler, compiler.getNextOpPos(opPos), count + 1);

        LocPathIterator iter =
          new LocPathIterator(compiler.getNamespaceContext());

        iter.m_firstWalker = new org.apache.xpath.axes.FilterExprWalker(iter);

        iter.m_firstWalker.init(compiler, opPos, steptype);

        m_iterators[count] = iter;
        break;
      default :
        m_iterators = new LocPathIterator[count];
      }
    }
  }

  /**
   * Create a new location path iterator.
   *
   * NEEDSDOC @param compiler
   * NEEDSDOC @param opPos
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws org.xml.sax.SAXException
   */
  protected LocPathIterator createLocPathIterator(
          Compiler compiler, int opPos) throws org.xml.sax.SAXException
  {
    return WalkerFactory.newLocPathIterator(compiler, opPos);
  }

  /** NEEDSDOC Field m_lastFetched          */
  Node m_lastFetched;

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
  public Node nextNode() throws DOMException
  {

    // If the cache is on, and the node has already been found, then 
    // just return from the list.
    if ((null != m_cachedNodes)
            && (m_cachedNodes.getCurrentPos() < m_cachedNodes.size()))
    {
      return m_cachedNodes.nextNode();
    }

    if (m_foundLast)
      return null;

    // Loop through the iterators getting the current fetched 
    // node, and get the earliest occuring in document order
    Node earliestNode = null;

    if (null != m_iterators)
    {
      int n = m_iterators.length;
      int iteratorUsed = -1;

      for (int i = 0; i < n; i++)
      {
        Node node = m_iterators[i].getCurrentNode();

        if (null == node)
          continue;
        else if (null == earliestNode)
        {
          iteratorUsed = i;
          earliestNode = node;
        }
        else
        {
          if (node.equals(earliestNode))
          {

            // Found a duplicate, so skip past it.
            m_iterators[i].nextNode();
          }
          else
          {
            DOMHelper dh = m_execContext.getDOMHelper();

            if (dh.isNodeAfter(node, earliestNode))
            {
              iteratorUsed = i;
              earliestNode = node;
            }
          }
        }
      }

      if (null != earliestNode)
      {
        m_iterators[iteratorUsed].nextNode();

        if (null != m_cachedNodes)
          m_cachedNodes.addElement(earliestNode);

        m_next++;
      }
      else
        m_foundLast = true;
    }

    m_lastFetched = earliestNode;

    return earliestNode;
  }

  /**
   * If an index is requested, NodeSet will call this method
   * to run the iterator to the index.  By default this sets
   * m_next to the index.  If the index argument is -1, this
   * signals that the iterator should be run to the end.
   *
   * NEEDSDOC @param index
   */
  public void runTo(int index)
  {

    if (m_foundLast || ((index >= 0) && (index <= m_next)))
      return;

    Node n;

    while (null == (n = nextNode()))
    {
      if (m_next >= index)
        break;
    }
  }

  /**
   * Get the current position, which is one less than
   * the next nextNode() call will retreave.  i.e. if
   * you call getCurrentPos() and the return is 0, the next
   * fetch will take place at index 1.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public int getCurrentPos()
  {
    return m_next;
  }

  /**
   * Return the last fetched node.  Needed to support the UnionPathIterator.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public Node getCurrentNode()
  {
    return m_lastFetched;
  }

  /**
   * Tells if we've found the last node yet.
   */
  protected boolean m_foundLast = false;

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

  /**
   * NEEDSDOC Method getCurrentContextNode 
   *
   *
   * NEEDSDOC (getCurrentContextNode) @return
   */
  public Node getCurrentContextNode()
  {
    return m_currentContextNode;
  }

  /**
   * The location path iterators, one for each
   * <a href="http://www.w3.org/TR/xpath#NT-LocationPath">location
   * path</a> contained in the union expression.
   */
  protected LocPathIterator[] m_iterators;
}
