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
import org.apache.xml.utils.IntStack;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xml.utils.ObjectPool;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.axes.AxesWalker;
import org.apache.xpath.VariableStack;

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
public class LocPathIterator extends PredicatedNodeTest
        implements Cloneable, NodeIterator, ContextNodeList, NodeList,
                   java.io.Serializable
{

  /**
   * Get the waiting walker at the given index.
   *
   *
   * @param i The walker index.
   *
   * @return non-null reference to an AxesWalker.
   */
  AxesWalker getWaiting(int i)
  {
    return (AxesWalker) m_waiting.elementAt(i);
  }

  /**
   * Get the number of waiters waiting in the current expression execution.
   * Note that this may not be the same as the total number of waiters in
   * the waiting list.
   *
   *
   * @return the number of waiters waiting in the current expression execution.
   */
  int getWaitingCount()
  {
    if(null == m_waiting)
      return 0;
    else
      return m_waiting.size() - m_waitingBottom;
  }

  /**
   * Create a LocPathIterator object.
   *
   * @param nscontext The namespace context for this iterator,
   * should be OK if null.
   */
  public LocPathIterator(PrefixResolver nscontext)
  {

    setLocPathIterator(this);

    this.m_prefixResolver = nscontext;
  }

  /**
   * Create a LocPathIterator object, including creation
   * of step walkers from the opcode list, and call back
   * into the Compiler to create predicate expressions.
   *
   * @param compiler The Compiler which is creating
   * this expression.
   * @param opPos The position of this iterator in the
   * opcode list from the compiler.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public LocPathIterator(Compiler compiler, int opPos, int analysis)
          throws javax.xml.transform.TransformerException
  {
    this(compiler, opPos, analysis, true);
  }

  /**
   * Create a LocPathIterator object, including creation
   * of step walkers from the opcode list, and call back
   * into the Compiler to create predicate expressions.
   *
   * @param compiler The Compiler which is creating
   * this expression.
   * @param opPos The position of this iterator in the
   * opcode list from the compiler.
   * @param shouldLoadWalkers True if walkers should be
   * loaded, or false if this is a derived iterator and
   * it doesn't wish to load child walkers.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public LocPathIterator(
          Compiler compiler, int opPos, int analysis, boolean shouldLoadWalkers)
            throws javax.xml.transform.TransformerException
  {
    m_analysis = analysis;

    setLocPathIterator(this);

    int firstStepPos = compiler.getFirstChildPos(opPos);

    if (shouldLoadWalkers)
    {
      m_firstWalker = WalkerFactory.loadWalkers(this, compiler, firstStepPos,
                                                0);
      m_lastUsedWalker = m_firstWalker;
    }
  }

  /**
   * Execute this iterator, meaning create a clone that can
   * store state, and initialize it for fast execution from
   * the current runtime state.  When this is called, no actual
   * query from the current context node is performed.
   *
   * @param xctxt The XPath execution context.
   *
   * @return An XNodeSet reference that holds this iterator.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt)
          throws javax.xml.transform.TransformerException
  {

    try
    {

      // LocPathIterator clone = (LocPathIterator) m_pool.getInstanceIfFree();
      // if (null == clone)
      LocPathIterator clone = (LocPathIterator) this.clone();

      clone.initContext(xctxt);

      return new XNodeSet(clone);
    }
    catch (CloneNotSupportedException ncse)
    {
      throw new javax.xml.transform.TransformerException(ncse);
    }
  }

  /**
   * <meta name="usage" content="advanced"/>
   * Set if this is an iterator at the upper level of
   * the XPath.
   *
   * @param b true if this location path is at the top level of the
   *          expression.
   */
  public void setIsTopLevel(boolean b)
  {
    m_isTopLevel = b;
  }

  /**
   * <meta name="usage" content="advanced"/>
   * Get if this is an iterator at the upper level of
   * the XPath.
   *
   * @return true if this location path is at the top level of the
   *          expression.
   */
  public boolean getIsTopLevel()
  {
    return m_isTopLevel;
  }

  /**
   * Initialize the context values for this expression
   * after it is cloned.
   *
   * @param execContext The XPath runtime context for this
   * transformation.
   */
  public void initContext(XPathContext execContext)
  {

    this.m_context = execContext.getCurrentNode();
    this.m_currentContextNode = execContext.getCurrentExpressionNode();
    this.m_execContext = execContext;
    this.m_prefixResolver = execContext.getNamespaceContext();
    this.m_dhelper = execContext.getDOMHelper();

    if (m_isTopLevel)
    {
      VariableStack vars = execContext.getVarStack();

      this.m_varStackPos = vars.getSearchStartOrTop();
      this.m_varStackContext = vars.getContextPos();
    }
  }

  /**
   * Set the next position index of this iterator.
   *
   * @param next A value greater than or equal to zero that indicates the next
   * node position to fetch.
   */
  protected void setNextPosition(int next)
  {
    m_next = next;
  }

  /**
   * Get the current position, which is one less than
   * the next nextNode() call will retrieve.  i.e. if
   * you call getCurrentPos() and the return is 0, the next
   * fetch will take place at index 1.
   *
   * @return A value greater than or equal to zero that indicates the next
   * node position to fetch.
   */
  public final int getCurrentPos()
  {
    return m_next;
  }

  /**
   * Add one to the current node index.
   */
  void incrementNextPosition()
  {
    m_next++;
  }

  /**
   * If setShouldCacheNodes(true) is called, then nodes will
   * be cached.  They are not cached by default.
   *
   * @param b True if this iterator should cache nodes.
   */
  public void setShouldCacheNodes(boolean b)
  {

    if (b)
      m_cachedNodes = new NodeSet();
    else
      m_cachedNodes = null;
  }

  /**
   * Get cached nodes.
   *
   * @return Cached nodes.
   */
  public NodeSet getCachedNodes()
  {
    return m_cachedNodes;
  }

  /**
   * Set the current position in the node set.
   *
   * @param i Must be a valid index greater
   * than or equal to zero and less than m_cachedNodes.size().
   */
  public void setCurrentPos(int i)
  {

    // System.out.println("setCurrentPos: "+i);
    if (null == m_cachedNodes)
      throw new RuntimeException(
        "This NodeSet can not do indexing or counting functions!");

    setNextPosition(i);
    m_cachedNodes.setCurrentPos(i);

    // throw new RuntimeException("Who's resetting this thing?");
  }

  /**
   * Get the length of the cached nodes.
   *
   * <p>Note: for the moment at least, this only returns
   * the size of the nodes that have been fetched to date,
   * it doesn't attempt to run to the end to make sure we
   * have found everything.  This should be reviewed.</p>
   *
   * @return The size of the current cache list.
   */
  public int size()
  {

    if (null == m_cachedNodes)
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
   *
   * @return The number of nodes in the list, always greater or equal to zero.
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

    if ((null == m_cachedNodes) || (pos != 0))
      this.setShouldCacheNodes(true);

    runTo(-1);
    this.setCurrentPos(pos);
  }

  /**
   * Tells if this NodeSet is "fresh", in other words, if
   * the first nextNode() that is called will return the
   * first node in the set.
   *
   * @return true of nextNode has not been called.
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
   * @throws DOMException
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
   * This attribute determines which node types are presented via the
   * iterator. The available set of constants is defined in the
   * <code>NodeFilter</code> interface.
   *
   * <p>This is somewhat useless at this time, since it doesn't
   * really return information that tells what this iterator will
   * show.  It is here only to fullfill the DOM NodeIterator
   * interface.</p>
   *
   * @return For now, always NodeFilter.SHOW_ALL & ~NodeFilter.SHOW_ENTITY_REFERENCE.
   * @see org.w3c.dom.traversal.NodeIterator
   */
  public int getWhatToShow()
  {

    // TODO: ??
    return NodeFilter.SHOW_ALL & ~NodeFilter.SHOW_ENTITY_REFERENCE;
  }

  /**
   *  The filter used to screen nodes.  Not used at this time,
   * this is here only to fullfill the DOM NodeIterator
   * interface.
   *
   * @return Always null.
   * @see org.w3c.dom.traversal.NodeIterator
   */
  public NodeFilter getFilter()
  {
    return null;
  }

  /**
   * The root node of the Iterator, as specified when it was created.
   *
   * @return The "root" of this iterator, which, in XPath terms,
   * is the node context for this iterator.
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
   * @return Always true, since entity reference nodes are not
   * visible in the XPath model.
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
    this.m_prefixResolver = null;
    this.m_dhelper = null;
    this.m_varStackPos = -1;
    this.m_varStackContext = 0;

    // m_pool.freeInstance(this);
  }

  /**
   * Get a cloned Iterator that is reset to the beginning
   * of the query.
   *
   * @return A cloned NodeIterator set of the start of the query.
   *
   * @throws CloneNotSupportedException
   */
  public NodeIterator cloneWithReset() throws CloneNotSupportedException
  {

    LocPathIterator clone = (LocPathIterator) clone();

    clone.reset();

    return clone;
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

    LocPathIterator clone = (LocPathIterator) super.clone();

    //    clone.m_varStackPos = this.m_varStackPos;
    //    clone.m_varStackContext = this.m_varStackContext;
    if (null != m_firstWalker)
    {
      // If we have waiting walkers, we have to check for duplicates.
      Vector clones = (null != m_waiting) ? new Vector() : null;

      clone.m_firstWalker = m_firstWalker.cloneDeep(clone, clones);

      if (null != m_waiting)
      {
        clone.m_waiting = (Vector) m_waiting.clone();  // or is new Vector faster?

        int n = m_waiting.size();

        for (int i = 0; i < n; i++)
        {
          AxesWalker waiting = (AxesWalker) m_waiting.elementAt(i);

          clone.m_waiting.setElementAt(waiting.cloneDeep(clone, clones), i);
        }
      }
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
    m_next = 0;
    m_last = 0;
    m_waitingBottom = 0;

    if (null != m_firstWalker)
    {
      m_lastUsedWalker = m_firstWalker;

      m_firstWalker.setRoot(m_context);
      if(null != m_waiting)
        m_waiting.removeAllElements();
    }
  }

  /**
   *  Returns the next node in the set and advances the position of the
   * iterator in the set. After a NodeIterator is created, the first call
   * to nextNode() returns the first node in the set.
   * @return  The next <code>Node</code> in the set being iterated over, or
   *   <code>null</code> if there are no more members in that set.
   * @throws DOMException
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
      Node next = m_cachedNodes.nextNode();

      this.setCurrentPos(m_cachedNodes.getCurrentPos());

      return next;
    }

    // If the variable stack position is not -1, we'll have to 
    // set our position in the variable stack, so our variable access 
    // will be correct.  Iterators that are at the top level of the 
    // expression need to reset the variable stack, while iterators 
    // in predicates do not need to, and should not, since their execution
    // may be much later than top-level iterators.  
    // m_varStackPos is set in initContext, which is called 
    // from the execute method.
    if (-1 == m_varStackPos)
    {
      if (null == m_firstWalker.getRoot())
      {
        this.setNextPosition(0);
        m_firstWalker.setRoot(m_context);

        m_lastUsedWalker = m_firstWalker;
      }

      return returnNextNode(m_firstWalker.nextNode());
    }
    else
    {
      VariableStack vars = m_execContext.getVarStack();

      // These three statements need to be combined into one operation.
      int savedStart = vars.getSearchStart();

      vars.setSearchStart(m_varStackPos);
      vars.pushContextPosition(m_varStackContext);

      if (null == m_firstWalker.getRoot())
      {
        this.setNextPosition(0);
        m_firstWalker.setRoot(m_context);

        m_lastUsedWalker = m_firstWalker;
      }

      Node n = returnNextNode(m_firstWalker.nextNode());

      // These two statements need to be combined into one operation.
      vars.setSearchStart(savedStart);
      vars.popContextPosition();

      return n;
    }
  }

  /**
   * Bottleneck the return of a next node, to make returns
   * easier from nextNode().
   *
   * @param nextNode The next node found, may be null.
   *
   * @return The same node that was passed as an argument.
   */
  protected Node returnNextNode(Node nextNode)
  {

    if (null != nextNode)
    {
      if (null != m_cachedNodes)
        m_cachedNodes.addElement(nextNode);

      this.incrementNextPosition();
    }

    m_lastFetched = nextNode;

    if (null == nextNode)
      m_foundLast = true;

    return nextNode;
  }

  /**
   * Return the last fetched node.  Needed to support the UnionPathIterator.
   *
   * @return The last fetched node, or null if the last fetch was null.
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
   *
   * @param index The index to run to, or -1 if the iterator
   * should run to the end.
   */
  public void runTo(int index)
  {

    if (m_foundLast || ((index >= 0) && (index <= getCurrentPos())))
      return;

    Node n;

    if (-1 == index)
    {
      while (null != (n = nextNode()));
    }
    else
    {
      while (null != (n = nextNode()))
      {
        if (getCurrentPos() >= index)
          break;
      }
    }
  }

  /**
   * <meta name="usage" content="advanced"/>
   * Get the head of the walker list.
   *
   * @return The head of the walker list, or null
   * if this iterator does not implement walkers.
   */
  public final AxesWalker getFirstWalker()
  {
    return m_firstWalker;
  }

  /**
   * <meta name="usage" content="advanced"/>
   * Set the last used walker.
   *
   * @param walker The last used walker, or null.
   */
  public final void setLastUsedWalker(AxesWalker walker)
  {
    m_lastUsedWalker = walker;
  }

  /**
   * <meta name="usage" content="advanced"/>
   * Get the last used walker.
   *
   * @return The last used walker, or null.
   */
  public final AxesWalker getLastUsedWalker()
  {
    return m_lastUsedWalker;
  }

  /**
   * <meta name="usage" content="advanced"/>
   * Add a walker to the waiting list.
   *
   * @param walker A walker that is waiting for
   * other step walkers to complete, before it can
   * continue.
   *
   * @see org.apache.xpath.axes.AxesWalker
   */
  public final void addToWaitList(AxesWalker walker)
  {
    if (null == m_waiting)
    {
      m_waiting = new Vector();
    }
    
    m_waiting.addElement(walker);
  }

  /**
   * <meta name="usage" content="advanced"/>
   * Remove a walker from the waiting list.
   *
   * @param walker A walker that is no longer waiting.
   *
   * @see org.apache.xpath.axes.AxesWalker
   */
  public final void removeFromWaitList(AxesWalker walker)
  {
    if(null != m_waiting) // defensive check.
      m_waiting.removeElement(walker);
  }

  /**
   * Tells if we've found the last node yet.
   *
   * @return true if the last nextNode returned null.
   */
  public final boolean getFoundLast()
  {
    return m_foundLast;
  }

  /**
   * The XPath execution context we are operating on.
   *
   * @return XPath execution context this iterator is operating on,
   * or null if initContext has not been called.
   */
  public final XPathContext getXPathContext()
  {
    return m_execContext;
  }

  /**
   * The DOM helper for the given context;
   *
   * @return The DOMHelper that should be used,
   * or null if initContext has not been called.
   */
  public final DOMHelper getDOMHelper()
  {
    return m_dhelper;
  }

  /**
   * The node context for the iterator.
   *
   * @return The node context, same as getRoot().
   */
  public final Node getContext()
  {
    return m_context;
  }

  /**
   * The node context from where the expression is being
   * executed from (i.e. for current() support).
   *
   * @return The top-level node context of the entire expression.
   */
  public final Node getCurrentContextNode()
  {
    return m_currentContextNode;
  }

  /**
   * Set the current context node for this iterator.
   *
   * @param n Must be a non-null reference to the node context.
   */
  public final void setCurrentContextNode(Node n)
  {
    m_currentContextNode = n;
  }

  /**
   * Return the saved reference to the prefix resolver that
   * was in effect when this iterator was created.
   *
   * @return The prefix resolver or this iterator, which may be null.
   */
  public final PrefixResolver getPrefixResolver()
  {
    return m_prefixResolver;
  }

  /**
   * Get the index of the last node in the iteration.
   *
   *
   * @return the index of the last node in the iteration.
   */
  public int getLast()
  {
    return m_last;
  }

  /**
   * Set the index of the last node in the iteration.
   *
   *
   * @param last the index of the last node in the iteration.
   */
  public void setLast(int last)
  {
    m_last = last;
  }

  /**
   * Get the index of the last node that can be itterated to.
   * This probably will need to be overridded by derived classes.
   *
   * @param xctxt XPath runtime context.
   *
   * @return the index of the last node that can be itterated to.
   */
  public int getLastPos(XPathContext xctxt)
  {
    int pos = getProximityPosition();
    LocPathIterator clone;

    try
    {
      clone = (LocPathIterator) clone();
    }
    catch (CloneNotSupportedException cnse)
    {
      return -1;
    }

    clone.setPredicateCount(clone.getPredicateCount() - 1);

    Node next;

    while (null != (next = clone.nextNode()))
    {
      pos++;
    }

    // System.out.println("pos: "+pos);
    return pos;
  }
  
  /**
   * Get the analysis pattern built by the WalkerFactory.
   *
   * @return The analysis pattern built by the WalkerFactory.
   */
  int getAnalysis()
  {
    return m_analysis;
  }

  /**
   * Set the analysis pattern built by the WalkerFactory.
   *
   * @param a The analysis pattern built by the WalkerFactory.
   */
  void setAnalysis(int a)
  {
    m_analysis = a;
  }
  
  /**
   * Tell if this expression or it's subexpressions can traverse outside 
   * the current subtree.
   * 
   * @return true if traversal outside the context node's subtree can occur.
   */
   public boolean canTraverseOutsideSubtree()
   {
    if((m_analysis & WalkerFactory.BITMASK_TRAVERSES_OUTSIDE_SUBTREE) != 0)
    {
      return true;
    }
    // We have to ask subwalkers about their predicates.
    if(null != m_firstWalker)
    {
      if(m_firstWalker.canTraverseOutsideSubtree())
        return true;
    }
    return super.canTraverseOutsideSubtree();
   }

  
  //============= State Data =============
  
  /** The starting point in m_waiting where the waiting step walkers are. */
  transient int m_waitingBottom = 0;

  /**
   * An index to the point in the variable stack where we should
   * begin variable searches for this iterator.
   * This is -1 if m_isTopLevel is false.
   */
  transient int m_varStackPos = -1;

  /**
   * An index into the variable stack where the variable context
   * ends, i.e. at the point we should terminate the search and
   * go looking for global variables.
   */
  transient int m_varStackContext;

  /**
   * Value determined at compile time, indicates that this is an
   * iterator at the top level of the expression, rather than inside
   * a predicate.
   * @serial
   */
  private boolean m_isTopLevel = false;

  /** The index of the last node in the iteration. */
  transient private int m_last = 0;
  
  /* The pool for cloned iterators.  Iterators need to be cloned
   * because the hold running state, and thus the original iterator
   * expression from the stylesheet pool can not be used.          */

  // ObjectPool m_pool = new ObjectPool(this.getClass());

  /** The last node that was fetched, usually by nextNode. */
  transient public Node m_lastFetched;

  /**
   * If this iterator needs to cache nodes that are fetched, they
   * are stored here.
   */
  transient NodeSet m_cachedNodes;

  /** The last used step walker in the walker list.
   *  @serial */
  protected AxesWalker m_lastUsedWalker;

  /** The head of the step walker list.
   *  @serial */
  protected AxesWalker m_firstWalker;

  /** This is true if nextNode returns null. */
  transient protected boolean m_foundLast = false;

  /**
   * Quicker access to the DOM helper than going through the
   * XPathContext object.
   */
  transient protected DOMHelper m_dhelper;

  /**
   * The context node for this iterator, which doesn't change through
   * the course of the iteration.
   */
  transient protected Node m_context;

  /**
   * The node context from where the expression is being
   * executed from (i.e. for current() support).  Different
   * from m_context in that this is the context for the entire
   * expression, rather than the context for the subexpression.
   */
  transient protected Node m_currentContextNode;

  /**
   * Fast access to the current prefix resolver.  It isn't really
   * clear that this is needed.
   * @serial
   */
  protected PrefixResolver m_prefixResolver;

  /**
   * The XPathContext reference, needed for execution of many
   * operations.
   */
  transient protected XPathContext m_execContext;

  /**
   * The index of the next node to be fetched.  Useful if this
   * is a cached iterator, and is being used as random access
   * NodeList.
   */
  transient protected int m_next = 0;

  /**
   * The list of "waiting" step walkers.
   * @see org.apache.xpath.axes.AxesWalker
   */
  transient private Vector m_waiting = null;
  
  /**
   * The analysis pattern built by the WalkerFactory.
   * TODO: Move to LocPathIterator.
   * @see org.apache.xpath.axes.WalkerFactory
   * @serial
   */
  protected int m_analysis = 0x00000000;
}
