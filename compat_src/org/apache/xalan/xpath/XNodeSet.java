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

import org.w3c.dom.*;
import org.w3c.dom.traversal.NodeIterator;
import org.w3c.dom.traversal.NodeFilter;
import java.text.*;

/**
 * <meta name="usage" content="general"/>
 * This class represents an XPath nodeset object, and is capable of 
 * converting the nodeset to other types, such as a string.
 */
public class XNodeSet extends org.apache.xpath.objects.XNodeSet
{  
  /**
   * Construct a XNodeSet object.
   */
  public XNodeSet(NodeList val)
  {
    super(new NodeIteratorWrapper(val));
  }
  
  /**
   * Construct an empty XNodeSet object.
   */
  public XNodeSet()
  {
    super();
  }

  /**
   * Construct a XNodeSet object for one node.
   */
  public XNodeSet(Node n)
  {
    super(n);    
  }
  
 
 static class NodeIteratorWrapper implements NodeIterator
  {

    /** Position of next node          */
    private int m_pos = 0;

    /** Document fragment instance this will wrap         */
    private NodeList m_list;

    /**
     * Constructor NodeIteratorWrapper
     *
     *
     * @param df Document fragment instance this will wrap
     */
    NodeIteratorWrapper(NodeList list)
    {
      m_list = list;
    }

    /**
     *  The root node of the Iterator, as specified when it was created.
     *
     * @return null
     */
    public Node getRoot()
    {
      return null;
    }

    /**
     *  This attribute determines which node types are presented via the
     * iterator. The available set of constants is defined in the
     * <code>NodeFilter</code> interface.
     *
     * @return All node types
     */
    public int getWhatToShow()
    {
      return NodeFilter.SHOW_ALL;
    }

    /**
     *  The filter used to screen nodes.
     *
     * @return null
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
     * @return true
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
     * @throws DOMException
     *    INVALID_STATE_ERR: Raised if this method is called after the
     *   <code>detach</code> method was invoked.
     */
    public Node nextNode() throws DOMException
    {

      if (m_pos < m_list.getLength())
      {
        return m_list.item(m_pos++);
      }
      else
        return null;
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

      if (m_pos >0)
      {
        return m_list.item(m_pos-1);
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
  }
  

}
