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
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.traversal.NodeIterator;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.DOMException;

import org.apache.xalan.utils.NodeVector;
import org.apache.xpath.axes.ContextNodeList;

/**
 * <meta name="usage" content="advanced"/>
 * The NodeSet class can act as either a NodeVector,
 * NodeList, or NodeIterator.  However, in order for it to
 * act as a NodeVector or NodeList, it's required that
 * setShouldCacheNodes(true) be called before the first
 * nextNode() is called, in order that nodes can be added
 * as they are fetched.  Derived classes that implement iterators
 * must override runTo(int index), in order that they may
 * run the iteration to the given index.
 */
public class NodeSet extends NodeVector
        implements NodeList, NodeIterator, Cloneable, ContextNodeList
{

  /**
   * Create an empty nodelist.
   */
  public NodeSet()
  {
    super();
  }

  /**
   * Create an empty nodelist.
   *
   * NEEDSDOC @param blocksize
   */
  public NodeSet(int blocksize)
  {
    super(blocksize);
  }

  /**
   * Create a NodeSet, and copy the members of the
   * given nodelist into it.
   *
   * NEEDSDOC @param nodelist
   */
  public NodeSet(NodeList nodelist)
  {

    super();

    addNodes(nodelist);
  }

  /**
   * Create a NodeSet, and copy the members of the
   * given NodeSet into it.
   *
   * NEEDSDOC @param nodelist
   */
  public NodeSet(NodeSet nodelist)
  {

    super();

    addNodes((NodeIterator) nodelist);
  }

  /**
   * Create a NodeSet, and copy the members of the
   * given nodelist into it.
   *
   * NEEDSDOC @param ni
   */
  public NodeSet(NodeIterator ni)
  {

    super();

    addNodes(ni);
  }

  /**
   * Create a NodeSet, and copy the members of the
   * given nodelist into it.
   *
   * NEEDSDOC @param node
   */
  public NodeSet(Node node)
  {

    super();

    addNode(node);
  }

  /**
   *  The root node of the Iterator, as specified when it was created.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public Node getRoot()
  {
    return null;
  }

  /**
   * Get a cloned LocPathIterator.
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws CloneNotSupportedException
   */
  public Object clone() throws CloneNotSupportedException
  {

    NodeSet clone = (NodeSet) super.clone();

    return clone;
  }

  /**
   * Get a cloned Iterator.
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws CloneNotSupportedException
   */
  public NodeIterator cloneWithReset() throws CloneNotSupportedException
  {

    NodeSet clone = (NodeSet) clone();

    clone.reset();

    return clone;
  }

  /**
   * Reset the iterator.
   */
  public void reset()
  {
    m_next = 0;
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

    if ((m_next) < this.size())
    {
      Node next = this.elementAt(m_next);

      m_next++;

      return next;
    }
    else
      return null;
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

    if (!m_cacheNodes)
      throw new RuntimeException(
        "This NodeSet can not iterate to a previous node!");

    if ((m_next - 1) > 0)
    {
      m_next--;

      return this.elementAt(m_next);
    }
    else
      return null;
  }

  /**
   *  Detaches the iterator from the set which it iterated over, releasing
   * any computational resources and placing the iterator in the INVALID
   * state. After<code>detach</code> has been invoked, calls to
   * <code>nextNode</code> or<code>previousNode</code> will raise the
   * exception INVALID_STATE_ERR.
   */
  public void detach(){}

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
   * If an index is requested, NodeSet will call this method
   * to run the iterator to the index.  By default this sets
   * m_next to the index.  If the index argument is -1, this
   * signals that the iterator should be run to the end.
   *
   * NEEDSDOC @param index
   */
  public void runTo(int index)
  {

    if (!m_cacheNodes)
      throw new RuntimeException(
        "This NodeSet can not do indexing or counting functions!");

    if ((index >= 0) && (m_next < m_firstFree))
      m_next = index;
    else
      m_next = m_firstFree - 1;
  }

  /**
   * Returns the <code>index</code>th item in the collection. If
   * <code>index</code> is greater than or equal to the number of nodes in
   * the list, this returns <code>null</code>.
   * @param index Index into the collection.
   * @return The node at the <code>index</code>th position in the
   *   <code>NodeList</code>, or <code>null</code> if that is not a valid
   *   index.
   */
  public Node item(int index)
  {

    runTo(index);

    return (Node) this.elementAt(index);
  }

  /**
   * The number of nodes in the list. The range of valid child node indices is
   * 0 to <code>length-1</code> inclusive.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public int getLength()
  {

    runTo(-1);

    return this.size();
  }

  /**
   * Add a node.
   *
   * NEEDSDOC @param n
   */
  public void addNode(Node n)
  {

    if (!m_mutable)
      throw new RuntimeException("This NodeSet is not mutable!");

    this.addElement(n);
  }

  /**
   * Insert a node at a given position.
   *
   * NEEDSDOC @param n
   * NEEDSDOC @param pos
   */
  public void insertNode(Node n, int pos)
  {

    if (!m_mutable)
      throw new RuntimeException("This NodeSet is not mutable!");

    insertElementAt(n, pos);
  }

  /**
   * Remove a node.
   *
   * NEEDSDOC @param n
   */
  public void removeNode(Node n)
  {

    if (!m_mutable)
      throw new RuntimeException("This NodeSet is not mutable!");

    this.removeElement(n);
  }

  /**
   * Copy NodeList members into this nodelist, adding in
   * document order.  If a node is null, don't add it.
   *
   * NEEDSDOC @param nodelist
   */
  public void addNodes(NodeList nodelist)
  {

    if (!m_mutable)
      throw new RuntimeException("This NodeSet is not mutable!");

    if (null != nodelist)  // defensive to fix a bug that Sanjiva reported.
    {
      int nChildren = nodelist.getLength();

      for (int i = 0; i < nChildren; i++)
      {
        Node obj = nodelist.item(i);

        if (null != obj)
        {
          addElement(obj);
        }
      }
    }

    // checkDups();
  }

  /**
   * Copy NodeList members into this nodelist, adding in
   * document order.  If a node is null, don't add it.
   *
   * NEEDSDOC @param ns
   */
  public void addNodes(NodeSet ns)
  {

    if (!m_mutable)
      throw new RuntimeException("This NodeSet is not mutable!");

    addNodes((NodeIterator) ns);
  }

  /**
   * Copy NodeList members into this nodelist, adding in
   * document order.  If a node is null, don't add it.
   *
   * NEEDSDOC @param iterator
   */
  public void addNodes(NodeIterator iterator)
  {

    if (!m_mutable)
      throw new RuntimeException("This NodeSet is not mutable!");

    if (null != iterator)  // defensive to fix a bug that Sanjiva reported.
    {
      Node obj;

      while (null != (obj = iterator.nextNode()))
      {
        addElement(obj);
      }
    }

    // checkDups();
  }

  /**
   * Copy NodeList members into this nodelist, adding in
   * document order.  If a node is null, don't add it.
   *
   * NEEDSDOC @param nodelist
   * NEEDSDOC @param support
   */
  public void addNodesInDocOrder(NodeList nodelist, XPathContext support)
  {

    if (!m_mutable)
      throw new RuntimeException("This NodeSet is not mutable!");

    int nChildren = nodelist.getLength();

    for (int i = 0; i < nChildren; i++)
    {
      Node node = nodelist.item(i);

      if (null != node)
      {
        addNodeInDocOrder(node, support);
      }
    }
  }

  /**
   * Copy NodeList members into this nodelist, adding in
   * document order.  If a node is null, don't add it.
   *
   * NEEDSDOC @param iterator
   * NEEDSDOC @param support
   */
  public void addNodesInDocOrder(NodeIterator iterator, XPathContext support)
  {

    if (!m_mutable)
      throw new RuntimeException("This NodeSet is not mutable!");

    Node node;

    while (null != (node = iterator.nextNode()))
    {
      addNodeInDocOrder(node, support);
    }
  }

  /**
   * Not yet ready for prime time.
   * I can't use recursion in this.
   *
   * NEEDSDOC @param start
   * NEEDSDOC @param end
   * NEEDSDOC @param testIndex
   * NEEDSDOC @param nodelist
   * NEEDSDOC @param support
   *
   * NEEDSDOC ($objectName$) @return
   */
  private boolean addNodesInDocOrder(int start, int end, int testIndex,
                                     NodeList nodelist, XPathContext support)
  {

    if (!m_mutable)
      throw new RuntimeException("This NodeSet is not mutable!");

    boolean foundit = false;
    int i;
    Node node = nodelist.item(testIndex);

    for (i = end; i >= start; i--)
    {
      Node child = (Node) elementAt(i);

      if (child == node)
      {
        i = -2;  // Duplicate, suppress insert

        break;
      }

      if (!support.getDOMHelper().isNodeAfter(node, child))
      {
        insertElementAt(node, i + 1);

        testIndex--;

        if (testIndex > 0)
        {
          boolean foundPrev = addNodesInDocOrder(0, i, testIndex, nodelist,
                                                 support);

          if (!foundPrev)
          {
            addNodesInDocOrder(i, size() - 1, testIndex, nodelist, support);
          }
        }

        break;
      }
    }

    if (i == -1)
    {
      insertElementAt(node, 0);
    }

    return foundit;
  }

  /**
   * Add the node into a vector of nodes where it should occur in
   * document order.
   * @param v Vector of nodes, presumably containing Nodes
   * @param obj Node object.
   *
   * NEEDSDOC @param node
   * @param test true if we should test for doc order
   * NEEDSDOC @param support
   * @return insertIndex.
   */
  public int addNodeInDocOrder(Node node, boolean test, XPathContext support)
  {

    if (!m_mutable)
      throw new RuntimeException("This NodeSet is not mutable!");

    int insertIndex = -1;

    if (test)
    {

      // This needs to do a binary search, but a binary search 
      // is somewhat tough because the sequence test involves 
      // two nodes.
      int size = size(), i;

      for (i = size - 1; i >= 0; i--)
      {
        Node child = (Node) elementAt(i);

        if (child == node)
        {
          i = -2;  // Duplicate, suppress insert

          break;
        }

        if (!support.getDOMHelper().isNodeAfter(node, child))
        {
          break;
        }
      }

      if (i != -2)
      {
        insertIndex = i + 1;

        insertElementAt(node, insertIndex);
      }
    }
    else
    {
      insertIndex = this.size();

      boolean foundit = false;

      for (int i = 0; i < insertIndex; i++)
      {
        if (this.item(i).equals(node))
        {
          foundit = true;

          break;
        }
      }

      if (!foundit)
        addElement(node);
    }

    // checkDups();
    return insertIndex;
  }  // end addNodeInDocOrder(Vector v, Object obj)

  /**
   * Add the node into a vector of nodes where it should occur in
   * document order.
   * @param v Vector of nodes, presumably containing Nodes
   * @param obj Node object.
   *
   * NEEDSDOC @param node
   * NEEDSDOC @param support
   *
   * NEEDSDOC ($objectName$) @return
   */
  public int addNodeInDocOrder(Node node, XPathContext support)
  {

    if (!m_mutable)
      throw new RuntimeException("This NodeSet is not mutable!");

    return addNodeInDocOrder(node, true, support);
  }  // end addNodeInDocOrder(Vector v, Object obj)

  /**
   * Get the length of the list.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public int size()
  {
    return super.size();
  }

  /**
   * Append a Node onto the vector.
   *
   * NEEDSDOC @param value
   */
  public void addElement(Node value)
  {

    if (!m_mutable)
      throw new RuntimeException("This NodeSet is not mutable!");

    super.addElement(value);
  }

  /**
   * Inserts the specified node in this vector at the specified index.
   * Each component in this vector with an index greater or equal to
   * the specified index is shifted upward to have an index one greater
   * than the value it had previously.
   *
   * NEEDSDOC @param value
   * NEEDSDOC @param at
   */
  public void insertElementAt(Node value, int at)
  {

    if (!m_mutable)
      throw new RuntimeException("This NodeSet is not mutable!");

    super.insertElementAt(value, at);
  }

  /**
   * Append the nodes to the list.
   *
   * NEEDSDOC @param nodes
   */
  public void appendNodes(NodeVector nodes)
  {

    if (!m_mutable)
      throw new RuntimeException("This NodeSet is not mutable!");

    super.appendNodes(nodes);
  }

  /**
   * Inserts the specified node in this vector at the specified index.
   * Each component in this vector with an index greater or equal to
   * the specified index is shifted upward to have an index one greater
   * than the value it had previously.
   */
  public void removeAllElements()
  {

    if (!m_mutable)
      throw new RuntimeException("This NodeSet is not mutable!");

    super.removeAllElements();
  }

  /**
   * Removes the first occurrence of the argument from this vector.
   * If the object is found in this vector, each component in the vector
   * with an index greater or equal to the object's index is shifted
   * downward to have an index one smaller than the value it had
   * previously.
   *
   * NEEDSDOC @param s
   *
   * NEEDSDOC ($objectName$) @return
   */
  public boolean removeElement(Node s)
  {

    if (!m_mutable)
      throw new RuntimeException("This NodeSet is not mutable!");

    return super.removeElement(s);
  }

  /**
   * Deletes the component at the specified index. Each component in
   * this vector with an index greater or equal to the specified
   * index is shifted downward to have an index one smaller than
   * the value it had previously.
   *
   * NEEDSDOC @param i
   */
  public void removeElementAt(int i)
  {

    if (!m_mutable)
      throw new RuntimeException("This NodeSet is not mutable!");

    super.removeElementAt(i);
  }

  /**
   * Sets the component at the specified index of this vector to be the
   * specified object. The previous component at that position is discarded.
   *
   * The index must be a value greater than or equal to 0 and less
   * than the current size of the vector.
   *
   * NEEDSDOC @param node
   * NEEDSDOC @param index
   */
  public void setElementAt(Node node, int index)
  {

    if (!m_mutable)
      throw new RuntimeException("This NodeSet is not mutable!");

    super.setElementAt(node, index);
  }

  /**
   * Get the nth element.
   *
   * NEEDSDOC @param i
   *
   * NEEDSDOC ($objectName$) @return
   */
  public Node elementAt(int i)
  {

    runTo(i);

    return super.elementAt(i);
  }

  /**
   * Tell if the table contains the given node.
   *
   * NEEDSDOC @param s
   *
   * NEEDSDOC ($objectName$) @return
   */
  public boolean contains(Node s)
  {

    runTo(-1);

    return super.contains(s);
  }

  /**
   * Searches for the first occurence of the given argument,
   * beginning the search at index, and testing for equality
   * using the equals method.
   *
   * NEEDSDOC @param elem
   * NEEDSDOC @param index
   * @return the index of the first occurrence of the object
   * argument in this vector at position index or later in the
   * vector; returns -1 if the object is not found.
   */
  public int indexOf(Node elem, int index)
  {

    runTo(-1);

    return super.indexOf(elem, index);
  }

  /**
   * Searches for the first occurence of the given argument,
   * beginning the search at index, and testing for equality
   * using the equals method.
   *
   * NEEDSDOC @param elem
   * @return the index of the first occurrence of the object
   * argument in this vector at position index or later in the
   * vector; returns -1 if the object is not found.
   */
  public int indexOf(Node elem)
  {

    runTo(-1);

    return super.indexOf(elem);
  }

  /** NEEDSDOC Field m_next          */
  protected int m_next = 0;

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
   * Set the current position in the node set.
   * @param i Must be a valid index.
   */
  public void setCurrentPos(int i)
  {

    if (!m_cacheNodes)
      throw new RuntimeException(
        "This NodeSet can not do indexing or counting functions!");

    m_next = i;
  }

  /**
   * Return the last fetched node.  Needed to support the UnionPathIterator.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public Node getCurrentNode()
  {

    if (!m_cacheNodes)
      throw new RuntimeException(
        "This NodeSet can not do indexing or counting functions!");

    int saved = m_next;
    Node n = elementAt(m_next-1);
    m_next = saved; // HACK: I think this is a bit of a hack.  -sb
    return n;
  }

  /** NEEDSDOC Field m_mutable          */
  protected boolean m_mutable = true;

  /** NEEDSDOC Field m_cacheNodes          */
  protected boolean m_cacheNodes = true;

  /**
   * NEEDSDOC Method getShouldCacheNodes 
   *
   *
   * NEEDSDOC (getShouldCacheNodes) @return
   */
  public boolean getShouldCacheNodes()
  {
    return m_cacheNodes;
  }

  /**
   * If setShouldCacheNodes(true) is called, then nodes will
   * be cached.  They are not cached by default.
   *
   * NEEDSDOC @param b
   */
  public void setShouldCacheNodes(boolean b)
  {

    if (!isFresh())
      throw new RuntimeException(
        "Can not call setShouldCacheNodes after nextNode has been called!");

    m_cacheNodes = b;
    m_mutable = true;
  }
}
