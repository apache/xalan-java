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
package org.apache.xalan.utils;

import java.io.Serializable;
import org.w3c.dom.Node;

/**
 * <meta name="usage" content="internal"/>
 * A very simple table that stores a list of Nodes.
 */
public class NodeVector implements Serializable, Cloneable
{
  private int m_blocksize;
  private Node m_map[];
  protected int m_firstFree = 0;
  private int m_mapSize; // lazy initialization

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
   */
  public NodeVector(int blocksize)
  {
    m_blocksize = blocksize;
    m_mapSize = 0;
  }
  
  /**
   * Get a cloned LocPathIterator.
   */
  public Object clone()
    throws CloneNotSupportedException
  {
    NodeVector clone = (NodeVector)super.clone();
    if((null != this.m_map) && (this.m_map == clone.m_map))
    {
      clone.m_map = new Node[this.m_map.length];
      System.arraycopy(this.m_map, 0, clone.m_map, 0, this.m_map.length);
    }
    return clone;
  }
  
  /**
   * Get the length of the list.
   */
  public int size()
  {
    return m_firstFree;
  }

  /**
   * Append a Node onto the vector.
   */
  public void addElement(Node value)
  {
    if(null == m_map)
    {
      m_map = new Node[m_blocksize];
      m_mapSize = m_blocksize;
    }
    else if((m_firstFree+1) >= m_mapSize)
    {
      m_mapSize+=m_blocksize;
      Node newMap[] = new Node[m_mapSize];
      System.arraycopy(m_map, 0, newMap, 0, m_firstFree+1);
      m_map = newMap;
    }
    m_map[m_firstFree] = value;
    m_firstFree++;
  }

  /**
   * Inserts the specified node in this vector at the specified index. 
   * Each component in this vector with an index greater or equal to 
   * the specified index is shifted upward to have an index one greater 
   * than the value it had previously. 
   */
  public void insertElementAt(Node value, int at)
  {
    if(null == m_map)
    {
      m_map = new Node[m_blocksize];
      m_mapSize = m_blocksize;
    }
    else if((m_firstFree+1) >= m_mapSize)
    {
      m_mapSize+=m_blocksize;
      Node newMap[] = new Node[m_mapSize];
      System.arraycopy(m_map, 0, newMap, 0, m_firstFree+1);
      m_map = newMap;
    }
    if(at <= (m_firstFree-1))
    {
      System.arraycopy(m_map, at, m_map, at+1, m_firstFree-at);
    }
    m_map[at] = value;
    m_firstFree++;
  }
  
  /**
   * Append the nodes to the list. 
   */
  public void appendNodes(NodeVector nodes)
  {
    int nNodes = nodes.size();
    if(null == m_map)
    {
      m_mapSize = nNodes+m_blocksize;
      m_map = new Node[m_mapSize];
    }
    else if((m_firstFree+nNodes) >= m_mapSize)
    {
      m_mapSize+=(nNodes+m_blocksize);
      Node newMap[] = new Node[m_mapSize];
      System.arraycopy(m_map, 0, newMap, 0, m_firstFree+nNodes);
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
    if(null == m_map)
      return;
    for(int i = 0; i < m_firstFree; i++)
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
   */
  public boolean removeElement(Node s)
  {
    if(null == m_map)
      return false;
    
    for(int i = 0; i < m_firstFree; i++)
    {
      Node node = m_map[i];
      if((null != node) && node.equals(s))
      {
        if(i > m_firstFree)
          System.arraycopy(m_map, i+1, m_map, i-1, m_firstFree-i);
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
   */
  public void removeElementAt(int i)
  {
    if(null == m_map)
      return;
    
    if(i > m_firstFree)
      System.arraycopy(m_map, i+1, m_map, i-1, m_firstFree-i);
    else
      m_map[i] = null;
  }
  
  /**
   * Sets the component at the specified index of this vector to be the 
   * specified object. The previous component at that position is discarded. 
   * 
   * The index must be a value greater than or equal to 0 and less 
   * than the current size of the vector. 
   */
  public void setElementAt(Node node, int index)
  {
    if(null == m_map)
    {
      m_map = new Node[m_blocksize];
      m_mapSize = m_blocksize;
    }
    m_map[index] = node;
  }

  /**
   * Get the nth element.
   */
  public Node elementAt(int i)
  {
    if(null == m_map)
      return null;
    
    return m_map[i];
  }
  
  /**
   * Tell if the table contains the given node.
   */
  public boolean contains(Node s)
  {
    if(null == m_map)
      return false;
    for(int i = 0; i < m_firstFree; i++)
    {
      Node node = m_map[i];
      if((null != node) && node.equals(s))
        return true;
    }
    return false;
  }
  
  /**
   * Searches for the first occurence of the given argument, 
   * beginning the search at index, and testing for equality 
   * using the equals method. 
   * @return the index of the first occurrence of the object 
   * argument in this vector at position index or later in the 
   * vector; returns -1 if the object is not found. 
   */
  public int indexOf(Node elem, int index)
  {
    if(null == m_map)
      return -1;
    
    for(int i = index; i < m_firstFree; i++)
    {
      Node node = m_map[i];
      if((null != node) && node.equals(elem))
        return i;
    }
    return -1;
  }

  /**
   * Searches for the first occurence of the given argument, 
   * beginning the search at index, and testing for equality 
   * using the equals method. 
   * @return the index of the first occurrence of the object 
   * argument in this vector at position index or later in the 
   * vector; returns -1 if the object is not found. 
   */
  public int indexOf(Node elem)
  {
    if(null == m_map)
      return -1;
    
    for(int i = 0; i < m_firstFree; i++)
    {
      Node node = m_map[i];
      if((null != node) && node.equals(elem))
        return i;
    }
    return -1;
  }


}
