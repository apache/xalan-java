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
package org.apache.xpath.objects;

import org.w3c.dom.*;
import org.w3c.dom.traversal.NodeIterator;
import org.w3c.dom.traversal.NodeFilter;
import org.apache.xpath.DOMHelper;

/**
 * <meta name="usage" content="general"/>
 * This class represents an XPath result tree fragment object, and is capable of 
 * converting the RTF to other types, such as a string.
 */
public class XRTreeFrag extends XObject
{  
  /**
   * Create an XObject.
   */
  public XRTreeFrag(DocumentFragment frag)
  {
    super(frag);
  }
  
  /**
   * Tell what kind of class this is.
   */
  public int getType()
  {
    return CLASS_RTREEFRAG;
  }

  /**
   * Given a request type, return the equivalent string. 
   * For diagnostic purposes.
   */
  protected String getTypeString()
  {
    return "#RTREEFRAG";
  }
  
  /**
   * Cast result object to a number.
   */
  public double num()
  {
    java.text.NumberFormat m_formatter = java.text.NumberFormat.getNumberInstance();
    double result;
    String s = DOMHelper.getNodeData((DocumentFragment)m_obj);
    if(null != s)
    {
      try
      {
        // result = Double.valueOf(s).doubleValue();
        Number n = m_formatter.parse(s.trim());
        result = n.doubleValue();
      }
      // catch(NumberFormatException nfe)
      catch(java.text.ParseException nfe)
      {
        result = Double.NaN;
      }
    }
    else
    {
      result = Double.NaN;
    }

    return result;
  }

  /**
   * Cast result object to a boolean.
   */
  public boolean bool()
  {
    boolean result = false;
    NodeList nl = ((DocumentFragment)m_obj).getChildNodes();
    int nChildren = nl.getLength();
    for(int i = 0; i < nChildren; i++)
    {
      Node n = nl.item(i);
      if((Node.TEXT_NODE == n.getNodeType()) &&
          (((Text)n).getData().trim().length() == 0))
      {
        continue;
      }
      
      result = true;
      break;
    }

    return result;
  }

  /**
   * Cast result object to a string.
   */
  public String str()
  {
    String str = DOMHelper.getNodeData((DocumentFragment)m_obj);
    return (null == str) ? "" : str;
  }
  
  /**
   * Cast result object to a result tree fragment.
   */
  public DocumentFragment rtree()
  {
    return (DocumentFragment)m_obj;
  }
  
  /**
   * Cast result object to a nodelist.
   */
  public NodeIterator nodeset()
  {
    System.out.println("**m_obj: "+((Object)m_obj).getClass().getName());
    if(m_obj instanceof NodeIterator)
      return (NodeIterator)m_obj;
    else 
      return new NodeIteratorWrapper(rtree());
  }
  
  /**
   * Cast result object to a nodelist. (special function).
   */
  public NodeList convertToNodeset()
  {
    return ((DocumentFragment)m_obj).getChildNodes();
  }  
  
  /**
   * Tell if two objects are functionally equal.
   */
  public boolean equals(XObject obj2)
    throws org.xml.sax.SAXException
  {
    if(XObject.CLASS_NODESET == obj2.getType())
    {
      // In order to handle the 'all' semantics of 
      // nodeset comparisons, we always call the 
      // nodeset function.
      return obj2.equals(this);
    }
    else if(XObject.CLASS_BOOLEAN == obj2.getType())
    {
      return bool() == obj2.bool();
    }
    else if(XObject.CLASS_NUMBER == obj2.getType())
    {
      return num() == obj2.num();
    }
    else if(XObject.CLASS_NODESET == obj2.getType())
    {
      return str().equals(obj2.str());
    }
    else if(XObject.CLASS_STRING == obj2.getType())
    {
      return str().equals(obj2.str());
    }
    else if(XObject.CLASS_RTREEFRAG == obj2.getType())
    {
      // Probably not so good.  Think about this.
      return str().equals(obj2.str());
    }
    else
    {
      return super.equals(obj2);
    }
  }
  
  class NodeIteratorWrapper implements NodeIterator, NodeList
  {
    int m_pos = -1;
    DocumentFragment m_docFrag;
    NodeList m_nl;
    
    NodeIteratorWrapper(DocumentFragment df)
    {
      m_docFrag = df;
      m_nl = df.getChildNodes();
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
      return m_nl.item(index);
    }

    /**
     *  The number of nodes in the list. The range of valid child node indices 
     * is 0 to <code>length-1</code> inclusive. 
     */
    public int getLength()
    {
      return m_nl.getLength();
    }
    
    /**
     *  The root node of the Iterator, as specified when it was created.
     */
    public Node getRoot()
    {
      return null;
    }

    /**
     *  This attribute determines which node types are presented via the 
     * iterator. The available set of constants is defined in the 
     * <code>NodeFilter</code> interface.
     */
    public int getWhatToShow()
    {
      return NodeFilter.SHOW_ALL;
    }

    /**
     *  The filter used to screen nodes.
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
    public Node nextNode()
                         throws DOMException
    {
      int next = m_pos+1;
      if((next >= 0) && (next < m_nl.getLength()))  
      {
        m_pos++;
        return m_nl.item(next);
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
    public Node previousNode()
                             throws DOMException
    {
      int prev = m_pos-1;
      if((prev >= 0) && (prev < m_nl.getLength()))  
      {
        m_pos--;
        return m_nl.item(prev);
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
    public void detach()
    {
    }
  }

}
