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
package org.apache.xml.utils;

import java.io.Serializable;

import org.w3c.dom.Node;

/**
 * <meta name="usage" content="internal"/>
 * A very simple table that stores a list of Nodes.
 */
public class NodeVector implements Serializable, Cloneable
{

  /** Size of blocks to allocate.
   *  @serial          */
  private int m_blocksize;

  /** Array of nodes this points to.
   *  @serial          */
  private Node m_map[];

  /** Number of nodes in this NodeVector.
   *  @serial          */
  protected int m_firstFree = 0;

  /** Size of the array this points to.
   *  @serial           */
  private int m_mapSize;  // lazy initialization

  /**
   * Default constructor.
   */
  public NodeVector()
  {
    m_blocksize = 32;
    m_mapSize = 0;
  }

  /**
   * Construct a NodeVector, using the given block size.
   *
   * @param blocksize Size of blocks to allocate 
   */
  public NodeVector(int blocksize)
  {
    m_blocksize = blocksize;
    m_mapSize = 0;
  }

  /**
   * Get a cloned LocPathIterator.
   *
   * @return A clone of this
   *
   * @throws CloneNotSupportedException
   */
  public Object clone() throws CloneNotSupportedException
  {

    NodeVector clone = (NodeVector) super.clone();

    if ((null != this.m_map) && (this.m_map == clone.m_map))
    {
      clone.m_map = new Node[this.m_map.length];

      System.arraycopy(this.m_map, 0, clone.m_map, 0, this.m_map.length);
    }

    return clone;
  }

  /**
   * Get the length of the list.
   *
   * @return Number of nodes in this NodeVector
   */
  public int size()
  {
    return m_firstFree;
  }

  /**
   * Append a Node onto the vector.
   *
   * @param value Node to add to the vector
   */
  public void addElement(Node value)
  {

    if ((m_firstFree + 1) >= m_mapSize)
    {
      if (null == m_map)
      {
        m_map = new Node[m_blocksize];
        m_mapSize = m_blocksize;
      }
      else
      {
        m_mapSize += m_blocksize;

        Node newMap[] = new Node[m_mapSize];

        System.arraycopy(m_map, 0, newMap, 0, m_firstFree + 1);

        m_map = newMap;
      }
    }

    m_map[m_firstFree] = value;

    m_firstFree++;
  }

  /**
   * Append a Node onto the vector.
   *
   * @param value Node to add to the vector
   */
  public final void push(Node value)
  {

    int ff = m_firstFree;

    if ((ff + 1) >= m_mapSize)
    {
      if (null == m_map)
      {
        m_map = new Node[m_blocksize];
        m_mapSize = m_blocksize;
      }
      else
      {
        m_mapSize += m_blocksize;

        Node newMap[] = new Node[m_mapSize];

        System.arraycopy(m_map, 0, newMap, 0, ff + 1);

        m_map = newMap;
      }
    }

    m_map[ff] = value;

    ff++;

    m_firstFree = ff;
  }

  /**
   * Pop a node from the tail of the vector and return the result.
   *
   * @return the node at the tail of the vector
   */
  public final Node pop()
  {

    m_firstFree--;

    Node n = m_map[m_firstFree];

    m_map[m_firstFree] = null;

    return n;
  }

  /**
   * Pop a node from the tail of the vector and return the
   * top of the stack after the pop.
   *
   * @return The top of the stack after it's been popped 
   */
  public final Node popAndTop()
  {

    m_firstFree--;

    m_map[m_firstFree] = null;

    return (m_firstFree == 0) ? null : m_map[m_firstFree - 1];
  }

  /**
   * Pop a node from the tail of the vector.
   */
  public final void popQuick()
  {

    m_firstFree--;

    m_map[m_firstFree] = null;
  }

  /**
   * Return the node at the top of the stack without popping the stack.
   * Special purpose method for TransformerImpl, pushElemTemplateElement.
   * Performance critical.
   *
   * @return Node at the top of the stack or null if stack is empty.  
   */
  public final Node peepOrNull()
  {
    return ((null != m_map) && (m_firstFree > 0))
           ? m_map[m_firstFree - 1] : null;
  }

  /**
   * Push a pair of nodes into the stack.  
   * Special purpose method for TransformerImpl, pushElemTemplateElement.
   * Performance critical.
   *
   * @param v1 First node to add to vector
   * @param v2 Second node to add to vector
   */
  public final void pushPair(Node v1, Node v2)
  {

    if (null == m_map)
    {
      m_map = new Node[m_blocksize];
      m_mapSize = m_blocksize;
    }
    else
    {
      if ((m_firstFree + 2) >= m_mapSize)
      {
        m_mapSize += m_blocksize;

        Node newMap[] = new Node[m_mapSize];

        System.arraycopy(m_map, 0, newMap, 0, m_firstFree);

        m_map = newMap;
      }
    }

    m_map[m_firstFree] = v1;
    m_map[m_firstFree + 1] = v2;
    m_firstFree += 2;
  }

  /**
   * Pop a pair of nodes from the tail of the stack. 
   * Special purpose method for TransformerImpl, pushElemTemplateElement.
   * Performance critical.
   */
  public final void popPair()
  {

    m_firstFree -= 2;
    m_map[m_firstFree] = null;
    m_map[m_firstFree + 1] = null;
  }

  /**
   * Set the tail of the stack to the given node.
   * Special purpose method for TransformerImpl, pushElemTemplateElement.
   * Performance critical.
   *
   * @param n Node to set at the tail of vector
   */
  public final void setTail(Node n)
  {
    m_map[m_firstFree - 1] = n;
  }

  /**
   * Set the given node one position from the tail.
   * Special purpose method for TransformerImpl, pushElemTemplateElement.
   * Performance critical.
   *
   * @param n Node to set
   */
  public final void setTailSub1(Node n)
  {
    m_map[m_firstFree - 2] = n;
  }

  /**
   * Return the node at the tail of the vector without popping
   * Special purpose method for TransformerImpl, pushElemTemplateElement.
   * Performance critical.
   *
   * @return Node at the tail of the vector
   */
  public final Node peepTail()
  {
    return m_map[m_firstFree - 1];
  }

  /**
   * Return the node one position from the tail without popping.
   * Special purpose method for TransformerImpl, pushElemTemplateElement.
   * Performance critical.
   *
   * @return Node one away from the tail
   */
  public final Node peepTailSub1()
  {
    return m_map[m_firstFree - 2];
  }

  /**
   * Inserts the specified node in this vector at the specified index.
   * Each component in this vector with an index greater or equal to
   * the specified index is shifted upward to have an index one greater
   * than the value it had previously.
   *
   * @param value Node to insert
   * @param at Position where to insert
   */
  public void insertElementAt(Node value, int at)
  {

    if (null == m_map)
    {
      m_map = new Node[m_blocksize];
      m_mapSize = m_blocksize;
    }
    else if ((m_firstFree + 1) >= m_mapSize)
    {
      m_mapSize += m_blocksize;

      Node newMap[] = new Node[m_mapSize];

      System.arraycopy(m_map, 0, newMap, 0, m_firstFree + 1);

      m_map = newMap;
    }

    if (at <= (m_firstFree - 1))
    {
      System.arraycopy(m_map, at, m_map, at + 1, m_firstFree - at);
    }

    m_map[at] = value;

    m_firstFree++;
  }

  /**
   * Append the nodes to the list.
   *
   * @param nodes NodeVector to append to this list
   */
  public void appendNodes(NodeVector nodes)
  {

    int nNodes = nodes.size();

    if (null == m_map)
    {
      m_mapSize = nNodes + m_blocksize;
      m_map = new Node[m_mapSize];
    }
    else if ((m_firstFree + nNodes) >= m_mapSize)
    {
      m_mapSize += (nNodes + m_blocksize);

      Node newMap[] = new Node[m_mapSize];

      System.arraycopy(m_map, 0, newMap, 0, m_firstFree + nNodes);

      m_map = newMap;
    }

    System.arraycopy(nodes.m_map, 0, m_map, m_firstFree, nNodes);

    m_firstFree += nNodes;
  }

  /**
   * Inserts the specified node in this vector at the specified index.
   * Each component in this vector with an index greater or equal to
   * the specified index is shifted upward to have an index one greater
   * than the value it had previously.
   */
  public void removeAllElements()
  {

    if (null == m_map)
      return;

    for (int i = 0; i < m_firstFree; i++)
    {
      m_map[i] = null;
    }

    m_firstFree = 0;
  }

  /**
   * Removes the first occurrence of the argument from this vector.
   * If the object is found in this vector, each component in the vector
   * with an index greater or equal to the object's index is shifted
   * downward to have an index one smaller than the value it had
   * previously.
   *
   * @param s Node to remove from the list
   *
   * @return True if the node was successfully removed
   */
  public boolean removeElement(Node s)
  {

    if (null == m_map)
      return false;

    for (int i = 0; i < m_firstFree; i++)
    {
      Node node = m_map[i];

      if ((null != node) && node.equals(s))
      {
        if (i > m_firstFree)
          System.arraycopy(m_map, i + 1, m_map, i - 1, m_firstFree - i);
        else
          m_map[i] = null;

        m_firstFree--;

        return true;
      }
    }

    return false;
  }

  /**
   * Deletes the component at the specified index. Each component in
   * this vector with an index greater or equal to the specified
   * index is shifted downward to have an index one smaller than
   * the value it had previously.
   *
   * @param i Index of node to remove
   */
  public void removeElementAt(int i)
  {

    if (null == m_map)
      return;

    if (i > m_firstFree)
      System.arraycopy(m_map, i + 1, m_map, i - 1, m_firstFree - i);
    else
      m_map[i] = null;
  }

  /**
   * Sets the component at the specified index of this vector to be the
   * specified object. The previous component at that position is discarded.
   *
   * The index must be a value greater than or equal to 0 and less
   * than the current size of the vector.
   *
   * @param node Node to set
   * @param index Index of where to set the node
   */
  public void setElementAt(Node node, int index)
  {

    if (null == m_map)
    {
      m_map = new Node[m_blocksize];
      m_mapSize = m_blocksize;
    }

    m_map[index] = node;
  }

  /**
   * Get the nth element.
   *
   * @param i Index of node to get
   *
   * @return Node at specified index
   */
  public Node elementAt(int i)
  {

    if (null == m_map)
      return null;

    return m_map[i];
  }

  /**
   * Tell if the table contains the given node.
   *
   * @param s Node to look for
   *
   * @return True if the given node was found.
   */
  public boolean contains(Node s)
  {

    if (null == m_map)
      return false;

    for (int i = 0; i < m_firstFree; i++)
    {
      Node node = m_map[i];

      if ((null != node) && node.equals(s))
        return true;
    }

    return false;
  }

  /**
   * Searches for the first occurence of the given argument,
   * beginning the search at index, and testing for equality
   * using the equals method.
   *
   * @param elem Node to look for
   * @param index Index of where to start the search
   * @return the index of the first occurrence of the object
   * argument in this vector at position index or later in the
   * vector; returns -1 if the object is not found.
   */
  public int indexOf(Node elem, int index)
  {

    if (null == m_map)
      return -1;

    for (int i = index; i < m_firstFree; i++)
    {
      Node node = m_map[i];

      if ((null != node) && node.equals(elem))
        return i;
    }

    return -1;
  }

  /**
   * Searches for the first occurence of the given argument,
   * beginning the search at index, and testing for equality
   * using the equals method.
   *
   * @param elem Node to look for 
   * @return the index of the first occurrence of the object
   * argument in this vector at position index or later in the
   * vector; returns -1 if the object is not found.
   */
  public int indexOf(Node elem)
  {

    if (null == m_map)
      return -1;

    for (int i = 0; i < m_firstFree; i++)
    {
      Node node = m_map[i];

      if ((null != node) && node.equals(elem))
        return i;
    }

    return -1;
  }
}
