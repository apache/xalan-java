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

// Java library imports
import java.util.Vector;
import java.util.Stack;

// DOM imports
import org.w3c.dom.traversal.NodeIterator;
import org.w3c.dom.traversal.TreeWalker;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.DOMException;
import org.w3c.dom.NodeList;

// Xalan imports
import org.apache.xpath.res.XPATHErrorResources;
import org.apache.xpath.XPath;
import org.apache.xpath.compiler.OpMap;
import org.apache.xpath.compiler.Compiler;
import org.apache.xpath.DOMHelper;
import org.apache.xpath.compiler.OpCodes;
import org.apache.xpath.compiler.PsuedoNames;
import org.apache.xpath.NodeSet;
import org.apache.xpath.Expression;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;
import org.apache.xalan.utils.IntStack;
import org.apache.xalan.utils.PrefixResolver;
import org.apache.xalan.utils.ObjectPool;
import org.apache.xpath.objects.XNodeSet;

import org.apache.xpath.axes.AxesWalker;

/**
 * <meta name="usage" content="advanced"/>
 * This class extends NodeSet, which implements NodeIterator, 
 * and fetches nodes one at a time in document order based on a XPath
 * <a href="http://www.w3.org/TR/xpath#NT-LocationPath>LocationPath</a>.
 * 
 * <p>If setShouldCacheNodes(true) is called, 
 * as each node is iterated via nextNode(), the node is also stored 
 * in the NodeVector, so that previousNode() can easily be done, except in 
 * the case where the LocPathIterator is "owned" by a UnionPathIterator, 
 * in which case the UnionPathIterator will cache the nodes.</p>
 */
public class LocPathIterator extends Expression 
  implements Cloneable, NodeIterator, ContextNodeList, NodeList
{ 
  /**
   * Create a LocPathIterator object.
   */
  public LocPathIterator(PrefixResolver nscontext)
  {    
    this.m_prefixResolver = nscontext;
  }

  /**
   * Create a LocPathIterator object.
   */
  public LocPathIterator(Compiler compiler, int opPos)
    throws org.xml.sax.SAXException
  {
    int firstStepPos = compiler.getFirstChildPos(opPos);
    m_firstWalker = WalkerFactory.loadWalkers(this, compiler, firstStepPos, 0);
    m_lastUsedWalker = m_firstWalker;
  }
  
  /**
   * Create a LocPathIterator object (for match patterns.
   */
  public LocPathIterator(Compiler compiler, int opPos,
                         boolean isMatchPattern)
    throws org.xml.sax.SAXException
  {
    m_firstWalker = WalkerFactory.loadOneWalker(this, compiler, opPos);
  }
  
  ObjectPool m_pool = new ObjectPool();
  
  public XObject execute(XPathContext xctxt)
    throws org.xml.sax.SAXException
  {
    try
    {
      
      LocPathIterator clone = (LocPathIterator)m_pool.getInstanceIfFree();
      if(null == clone)
        clone = (LocPathIterator)this.clone();
      clone.initContext(xctxt);
      return new XNodeSet( clone );
    }
    catch(CloneNotSupportedException ncse)
    {
    }
    return null;
  }
  
  public void initContext(XPathContext execContext)
  {
    this.m_context = execContext.getCurrentNode();
    this.m_currentContextNode = execContext.getCurrentExpressionNode();
    this.m_execContext = execContext;
    this.m_stackFrameIndex = execContext.getVarStack().getCurrentStackFrameIndex();
    this.m_prefixResolver = execContext.getNamespaceContext();
    this.m_dhelper = execContext.getDOMHelper();
  }
  
  NodeSet m_cachedNodes = null;
  private int m_next = 0;
  
  protected void setNextPosition(int next)
  {
    m_next = next;
    // System.out.println("setNextPosition to: "+m_next);
  }
  
  /**
   * Get the current position, which is one less than 
   * the next nextNode() call will retreave.  i.e. if 
   * you call getCurrentPos() and the return is 0, the next 
   * fetch will take place at index 1.
   */
  public int getCurrentPos() { return m_next;  }

  void incrementNextPosition()
  {
    m_next++;
    // System.out.println("incrementNextPosition to: "+m_next);
  }

  /**
   * If setShouldCacheNodes(true) is called, then nodes will 
   * be cached.  They are not cached by default.
   */
  public void setShouldCacheNodes(boolean b)
  {
    if(b)
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
    // System.out.println("setCurrentPos: "+i);
    if(null == m_cachedNodes)
      throw new RuntimeException("This NodeSet can not do indexing or counting functions!");
    setNextPosition(i); 
    m_cachedNodes.setCurrentPos(i);
    // throw new RuntimeException("Who's resetting this thing?");
  }

  /**
   * Get the length of the list.
   */
  public int size()
  {
    if(null == m_cachedNodes)
      return 0;
    return m_cachedNodes.size();
  }
  
  /**
   *  Returns the <code>index</code> th item in the collection. If 
   * <code>index</code> is greater than or equal to the number of nodes in 
   * the list, this returns <code>null</code> .
   * @param index  Index into the collection.
   * @return  The node at the <code>index</code> th position in the 
   *   <code>NodeList</code> , or <code>null</code> if that is not a valid 
   *   index.
   */
  public Node item(int index)
  {
    resetToCachedList();
    return m_cachedNodes.item(index);
  }

  /**
   *  The number of nodes in the list. The range of valid child node indices 
   * is 0 to <code>length-1</code> inclusive. 
   */
  public int getLength()
  {
    resetToCachedList();
    return m_cachedNodes.getLength();
  }
  
  /**
   * In order to implement NodeList (for extensions), try to reset
   * to a cached list for random access.
   */
  private void resetToCachedList()
  {
    int pos = this.getCurrentPos();
    if((null == m_cachedNodes) || (pos != 0))
      this.setShouldCacheNodes(true);
    runTo(-1);
    this.setCurrentPos(pos);
  }

  
  /**
   * Tells if this NodeSet is "fresh", in other words, if 
   * the first nextNode() that is called will return the 
   * first node in the set.
   */
  public boolean isFresh()
  {
    return (getCurrentPos() == 0);
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
  public Node previousNode()
    throws DOMException
  {
    if(null == m_cachedNodes)
      throw new RuntimeException("This NodeSet can not iterate to a previous node!");
    
    return m_cachedNodes.previousNode();
  }
  
  /**
   *  This attribute determines which node types are presented via the 
   * iterator. The available set of constants is defined in the 
   * <code>NodeFilter</code> interface.
   */
  public int getWhatToShow()
  {
    // TODO: ??
    return NodeFilter.SHOW_ALL & ~NodeFilter.SHOW_ENTITY_REFERENCE;
  }

  /**
   *  The filter used to screen nodes.
   */
  public NodeFilter getFilter()
  {
    return null;
  }
  
  /**
   *  The root node of the Iterator, as specified when it was created.
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
   */
  public boolean getExpandEntityReferences()
  {
    return true;
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
    this.m_stackFrameIndex = 0;
    this.m_prefixResolver = null;
    this.m_dhelper = null;
    m_pool.freeInstance(this);
  }
  
  /**
   * Get a cloned Iterator.
   */
  public NodeIterator cloneWithReset()
    throws CloneNotSupportedException
  {
    LocPathIterator clone = (LocPathIterator)clone();
    clone.reset();
    return clone;
  }
  
  /**
   * Get a cloned LocPathIterator.
   */
  public Object clone()
    throws CloneNotSupportedException
  {
    LocPathIterator clone = (LocPathIterator)super.clone();
    AxesWalker walker = m_firstWalker;
    AxesWalker prevClonedWalker = null;
    while(null != walker)
    {
      AxesWalker clonedWalker = (AxesWalker)walker.clone();
      clonedWalker.setLocPathIterator(clone);
      if(clone.m_lastUsedWalker == walker)
        clone.m_lastUsedWalker = clonedWalker;
      if(null == prevClonedWalker)
      {
        clone.m_firstWalker = clonedWalker;
        prevClonedWalker = clonedWalker;
      }
      else
      {
        prevClonedWalker.setNextWalker(clonedWalker);
        clonedWalker.setPrevWalker(prevClonedWalker);
        prevClonedWalker = clonedWalker;
      }
      walker = walker.getNextWalker();
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
    m_lastFetched = null;
    m_lastUsedWalker = m_firstWalker;
    m_firstWalker.setRoot(m_context);
    m_waiting.removeAllElements();
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
    if((null != m_cachedNodes) && (m_cachedNodes.getCurrentPos() < m_cachedNodes.size()))
    {
      Node next = m_cachedNodes.nextNode();
      this.setCurrentPos( m_cachedNodes.getCurrentPos() );
      return next;
    }

    if(null == m_firstWalker.getRoot())
    {
      this.setNextPosition(0);
      m_firstWalker.setRoot(m_context);
      m_lastUsedWalker = m_firstWalker;
    }
    return returnNextNode(m_firstWalker.nextNode());
  }
  
  /**
   * Bottleneck the return of a next node, to make returns 
   * easier from nextNode().
   */
  private Node returnNextNode(Node nextNode)
  {
    if(null != nextNode)
    {
      if(null != m_cachedNodes)
        m_cachedNodes.addElement(nextNode);
      this.incrementNextPosition();
    }
    m_lastFetched = nextNode;
    if(null == nextNode)
      m_foundLast = true;
    return nextNode;
  }
  
  /**
   * Return the last fetched node.  Needed to support the UnionPathIterator.
   */
  public Node getCurrentNode()
  {
    return m_lastFetched;
  }
  
  /**
   * If an index is requested, NodeSet will call this method 
   * to run the iterator to the index.  By default this sets 
   * m_next to the index.  If the index argument is -1, this 
   * signals that the iterator should be run to the end.
   */
  public void runTo(int index)
  {
    if(m_foundLast || ((index >= 0) && (index <= getCurrentPos())))
      return;
    
    Node n;
    if(-1 == index)
    {
      while(null != (n = nextNode()))
        ;
    }
    else
    {
      while(null != (n = nextNode()))
      {
        if(getCurrentPos() >= index)
          break;
      }
    }
  }
        
  /**
   * 
   */
  // protected AxesWalker[] m_axesWalkers;
  protected AxesWalker m_firstWalker;
  
  /**
   * For internal use.
   */
  public AxesWalker getFirstWalker()
  {
    return m_firstWalker;
  }

  /**
   * For use by the AxesWalker.
   */
  private AxesWalker m_lastUsedWalker;

  /**
   * For internal use.
   */
  public void setLastUsedWalker(AxesWalker walker)
  {
    m_lastUsedWalker = walker;
  }

  /**
   * For internal use.
   */
  public AxesWalker getLastUsedWalker()
  {
    return m_lastUsedWalker;
  }
  
  // Temp as vector.  This probably ought to be a heap/priority queue.
  public Vector m_waiting = new Vector();
  
  public void addToWaitList(AxesWalker walker)
  {
    m_waiting.addElement(walker);
  }
  
  public void removeFromWaitList(AxesWalker walker)
  {
    m_waiting.removeElement(walker);
  }
  
  /**
   * The last fetched node.
   */
  Node m_lastFetched;
    
  /**
   * Tells if we've found the last node yet.
   */
  public boolean getFoundLast() { return m_foundLast; }
  protected boolean m_foundLast = false;
  
  /**
   * The XPath execution context we are operating on.
   */
  protected XPathContext m_execContext;
  public XPathContext getXPathContext() { return m_execContext; }
  
  /**
   * The DOM helper for the given context;
   */
  protected DOMHelper m_dhelper;
  public DOMHelper getDOMHelper() { return m_dhelper; }
  
  /**
   * The node context for the expression.
   */
  protected Node m_context;
  public Node getContext() { return m_context; }

  /**
   * The node context for the expression.
   */
  protected int m_stackFrameIndex;
  public int getStackFrameIndex() { return m_stackFrameIndex; }

  /**
   * The node context from where the expression is being 
   * executed from (i.e. for current() support).
   */
  protected Node m_currentContextNode;
  public Node getCurrentContextNode() { return m_currentContextNode; }
  public void setCurrentContextNode(Node n) { m_currentContextNode = n; }

  /**
   * Return the saved reference to the prefix resolver that 
   * was in effect when this iterator was created.
   */
  public PrefixResolver getPrefixResolver() { return m_prefixResolver; }
  protected PrefixResolver m_prefixResolver;
}


