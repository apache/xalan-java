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
package org.apache.xalan.transformer;

import java.util.Vector;

import org.apache.xpath.axes.LocPathIterator;
import org.apache.xml.utils.QName;
import org.apache.xalan.templates.KeyDeclaration;
import org.apache.xpath.NodeSet;

import org.w3c.dom.Node;
import org.w3c.dom.DOMException;
import org.w3c.dom.traversal.NodeIterator;

/**
 * <meta name="usage" content="internal"/>
 * This class implements an optimized iterator for 
 * "key()" patterns. It uses a KeyIterator to walk the 
 * source tree and incrementally build a list of nodes that match
 * a given key name, match pattern and value.  
 */
public class KeyRefIterator extends LocPathIterator
{

  /** Key name.
   *  @serial         */
  private QName m_name;    
  
  /** Use field of key function.
   *  @serial         */
  private String m_lookupKey;  
  
  /** Main Key iterator for this iterator.
   *  @serial    */
  private KeyIterator m_ki;    
  
  /**
   * Get key name
   *
   *
   * @return Key name
   */
  public QName getName()
  {
    return m_name;
  }
  
  

  /**
   * Constructor KeyRefIterator
   *
   *
   * @param ref Key value to match
   * @param ki The main key iterator used to walk the source tree 
   */
  public KeyRefIterator(String ref, KeyIterator ki)
  {

    super(ki.getPrefixResolver());
    setShouldCacheNodes(true);
    m_ki = ki;
    m_name = ki.getName();
    m_lookupKey = ref;
    this.m_execContext = ki.getXPathContext();
    this.m_dhelper = ki.getDOMHelper();
  }

  /**
   *  Returns the next node in the set and advances the position of the
   * iterator in the set. After a NodeIterator is created, the first call
   * to nextNode() returns the first node in the set.
   * 
   * @return  The next <code>Node</code> in the set being iterated over, or
   *   <code>null</code> if there are no more members in that set.
   * 
   * @throws DOMException
   *    INVALID_STATE_ERR: Raised if this method is called after the
   *   <code>detach</code> method was invoked.
   */
  public Node nextNode() throws DOMException
  {

   if (m_foundLast)
      return null;
    
    // If the cache is on, and the node has already been found, then 
    // just return from the list.
    NodeSet m_cachedNodes = getCachedNodes();
    
    // We are not using the NodeSet methods getCurrentPos() and nextNode()
    // in this case because the nodeset is not cloned and therefore
    // the positions it indicates may not be associated with the 
    // current iterator.
    if ((null != m_cachedNodes)
            && (m_next < m_cachedNodes.size()))        
    {
      Node next = m_cachedNodes.elementAt(m_next); 
      this.setCurrentPos(++m_next); 
      m_lastFetched = next;
      
      return next;
    }    

    Node next = null;       
    if ( m_ki.getLookForMoreNodes()) 
    {
      ((KeyWalker)m_ki.getFirstWalker()).m_lookupKey = m_lookupKey;
      next = m_ki.nextNode();        
    }
    
    if (null != next)
    {  
      m_lastFetched = next;
      this.setCurrentPos(++m_next);
      return next;
    }
    else
      m_foundLast = true;                      
    
    m_lastFetched = null;
    return null;
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
    KeyRefIterator clone = (KeyRefIterator)super.clone();
    // clone.m_ki = (KeyIterator)m_ki.clone();

    return clone;
  }
  
//  /**
//   * Get a cloned Iterator that is reset to the beginning 
//   * of the query.
//   *
//   * @return A cloned NodeIterator set of the start of the query.
//   *
//   * @throws CloneNotSupportedException
//   */
//  public NodeIterator cloneWithReset() throws CloneNotSupportedException
//  {
//    KeyRefIterator clone = (KeyRefIterator)super.cloneWithReset();
//
//    return clone;
//  }
  
  /**
   * Reset the iterator.
   */
  public void reset()
  {
    super.reset();
    
    setCurrentPos(0);
  }

  
  /**
   * Add a node matching this ref to the cached nodes for this iterator 
   *
   *
   * @param node Node to add to cached nodes
   */
  public void addNode(Node node) 
  {
    NodeSet m_cachedNodes = getCachedNodes();
    if (null != m_cachedNodes)
    {
      if(!m_cachedNodes.contains(node))
        m_cachedNodes.addElement(node);
    }
  }  
       
}
