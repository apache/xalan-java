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

// Xalan imports
import org.apache.xalan.xpath.res.XPATHErrorResources;
import org.apache.xalan.xpath.XPath;
import org.apache.xalan.xpath.OpCodes;
import org.apache.xalan.xpath.PsuedoNames;
import org.apache.xalan.xpath.NodeSet;
import org.apache.xalan.xpath.XPathContext;
import org.apache.xalan.xpath.XObject;
import org.apache.xalan.utils.IntStack;
import org.apache.xalan.utils.PrefixResolver;

import org.apache.xalan.xpath.axes.AxesWalker;

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
public class LocPathIterator extends NodeSet implements Cloneable
{
  /**
   * Create a LocPathIterator object.
   */
  public LocPathIterator(XPathContext execContext,
                         PrefixResolver nscontext,
                         Node context)
  {    
    this.m_execContext = execContext;
    this.m_stackFrameIndex = execContext.getVarStack().getCurrentStackFrameIndex();
    this.m_dhelper = execContext.getDOMHelper();
    this.m_currentContextNode = execContext.getCurrentNode();
    this.m_prefixResolver = nscontext;
    this.m_context = context; // 'tis the root node
  }

  /**
   * Create a LocPathIterator object.
   */
  public LocPathIterator(XPath xpath, XPathContext execContext, 
                         Node context, int opPos,
                         SimpleNodeLocator locator)
  {
    m_mutable = false;
    m_cacheNodes = false;
    this.m_context = context;
    this.m_execContext = execContext;
    this.m_stackFrameIndex = execContext.getVarStack().getCurrentStackFrameIndex();
    this.m_prefixResolver = execContext.getNamespaceContext();
    this.m_currentContextNode = execContext.getCurrentNode();
    this.m_dhelper = execContext.getDOMHelper();
    this.m_locator = locator;
    this.m_xpath = xpath;
    this.m_opPos = opPos;
    
    m_firstStepPos = xpath.getFirstChildPos(opPos);
    this.loadIterators(m_firstStepPos, 0);
    // allocateWalkerStacks(m_firstStepPos);
  }
  
  /**
   * Create a LocPathIterator object (for match patterns.
   */
  public LocPathIterator(XPath xpath, XPathContext execContext, 
                         Node context, int opPos,
                         SimpleNodeLocator locator,
                         boolean isMatchPattern)
  {
    m_mutable = false;
    m_cacheNodes = false;
    this.m_context = context;
    this.m_execContext = execContext;
    this.m_stackFrameIndex = execContext.getVarStack().getCurrentStackFrameIndex();
    this.m_prefixResolver = execContext.getNamespaceContext();
    this.m_dhelper = execContext.getDOMHelper();
    this.m_locator = locator;
    this.m_xpath = xpath;
    this.m_opPos = opPos;
    
    loadOneIterator(opPos);
    // m_firstStepPos = xpath.getFirstChildPos(opPos);
    // allocateWalkerStacks(firstStepPos);
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
    super.reset();
    m_foundLast = false;
    m_lastFetched = null;
    m_lastUsedWalker = m_firstWalker;
    m_firstWalker.setRoot(m_context);
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
    if(this.m_cacheNodes && (m_next < this.size()))
    {
      return super.nextNode();
    }

    if(null == m_firstWalker.getRoot())
    {
      m_next = 0;
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
      if(this.m_cacheNodes)
        this.addElement(nextNode);
      m_next++;
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
    if(m_foundLast || ((index >= 0) && (index <= m_next)))
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
        if(m_next >= index)
          break;
      }
    }
  }
    
  /**
   * Create the proper iterator from the axes type.
   */
  public AxesWalker createIterator(int stepType, LocPathIterator lpi)
  {
    return AxesWalker.createDefaultWalker(stepType, lpi);
  }
              
  /**
   * List of AxesIterators.
   */
  private Stack m_savedAxesWalkers = new Stack();
  
  /**
   * <meta name="usage" content="advanced"/>
   * This method is for building an array of possible levels
   * where the target element(s) could be found for a match.
   * @param xpath The xpath that is executing.
   * @param context The current source tree context node.
   */
  protected void loadOneIterator(int stepOpCodePos)
  {
    int stepType = m_xpath.getOpMap()[stepOpCodePos];
    if( stepType != OpCodes.ENDOP )
    {
      // m_axesWalkers = new AxesWalker[1];
      
      // As we unwind from the recursion, create the iterators.
      AxesWalker ai = createIterator(stepType, this);
      ai.init(stepOpCodePos, stepType);
      m_firstWalker = ai;
    }
  }
          
  /**
   * <meta name="usage" content="advanced"/>
   * This method is for building an array of possible levels
   * where the target element(s) could be found for a match.
   * @param xpath The xpath that is executing.
   * @param context The current source tree context node.
   */
  protected void loadIterators(int stepOpCodePos, int stepIndex)
  {
    int stepType;
    AxesWalker walker, prevWalker = null;
    int ops[] = m_xpath.getOpMap();
    while( OpCodes.ENDOP != (stepType = ops[stepOpCodePos]) )
    {      
      // As we unwind from the recursion, create the iterators.
      walker = createIterator(stepType, this);
      walker.init(stepOpCodePos, stepType);
      if(null == m_firstWalker)
      {
        m_firstWalker = walker;
        m_lastUsedWalker = walker;
      }
      else
      {
        prevWalker.setNextWalker(walker);
        walker.setPrevWalker(prevWalker);
      }
      prevWalker = walker;
      stepOpCodePos = m_xpath.getNextStepPos(stepOpCodePos);
      if(stepOpCodePos < 0)
        break;
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
   * The XPath we are operating on.
   */
  protected XPath m_xpath;
  public XPath getXPath() { return m_xpath; }
  
  /**
   * The op position of this iterator.
   */
  protected int m_opPos;

  /**
   * The first step position of this iterator.
   */
  protected int m_firstStepPos;

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

  /**
   * Reference to the SimpleNodeLocator that created 
   * this instance.
   */
  public SimpleNodeLocator getLocator() { return m_locator; }
  protected SimpleNodeLocator m_locator;

  /**
   * Return the saved reference to the prefix resolver that 
   * was in effect when this iterator was created.
   */
  public PrefixResolver getPrefixResolver() { return m_prefixResolver; }
  protected PrefixResolver m_prefixResolver;
}


