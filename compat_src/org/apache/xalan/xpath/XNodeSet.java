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
import org.w3c.dom.Text;
import org.w3c.dom.traversal.NodeIterator;
import org.w3c.dom.traversal.NodeFilter;
import java.text.*;

import org.apache.xpath.XPathContext;
import org.apache.xpath.NodeSet;
import org.apache.xpath.DOMHelper;
import org.apache.xml.dtm.ref.DTMNodeIterator;
import org.apache.xml.dtm.ref.DTMNodeList;
import org.apache.xml.dtm.ref.DTMManagerDefault;
import org.apache.xml.dtm.DTM;

/**
 * <meta name="usage" content="general"/>
 * This class represents an XPath nodeset object, and is capable of 
 * converting the nodeset to other types, such as a string.
 * @deprecated This compatibility layer will be removed in later releases. 
 */
public class XNodeSet extends XObject  
{
  org.apache.xpath.objects.XNodeSet m_xnodeset;
  DTMManagerDefault dtmMgr = new DTMManagerDefault(); 
  
  /**
   * Construct a XNodeSet object.
   */
  public XNodeSet(NodeList val)
  {
    super();
    int node = dtmMgr.getDTMHandleFromNode(val.item(0));
    m_xnodeset = new org.apache.xpath.objects.XNodeSet(dtmMgr.createDTMIterator(node)) ;
  }
  
  /**
   * Construct an empty XNodeSet object.
   */
  public XNodeSet()
  {
    super();
    m_xnodeset = new org.apache.xpath.objects.XNodeSet(dtmMgr);
  }

  /**
   * Construct a XNodeSet object for one node.
   */
  public XNodeSet(Node n)
  {
    super(n);    
    m_xnodeset = new org.apache.xpath.objects.XNodeSet(dtmMgr.getDTMHandleFromNode(n), dtmMgr);
  }
  
  
  /**
   * Tell that this is a CLASS_NODESET.
   */
  public int getType()
  {
    return m_xnodeset.getType();
  }
  
  /**
   * Given a request type, return the equivalent string. 
   * For diagnostic purposes.
   */
  public String getTypeString() // PR:DMAN4MBJ4D Submitted by:<garyp@firstech.com> change to protected
  {
    return m_xnodeset.getTypeString();
  }
  
  /**
   * Get the string conversion from a single node.
   */
  double getNumberFromNode(Node n)
  {
    return m_xnodeset.getNumberFromNode(dtmMgr.getDTMHandleFromNode(n));
  }

  /**
   * Cast result object to a number.
   */
  public double num()
  {
    return m_xnodeset.num();
  }

  /**
   * Cast result object to a boolean.
   */
  public boolean bool()
  {
    return m_xnodeset.bool();
  }
  

  /**
   * Get the string conversion from a single node.
   */
  static String getStringFromNode(Node n)
  {
    switch (n.getNodeType())
    {
    case Node.ELEMENT_NODE :
    case Node.DOCUMENT_NODE :
      return DOMHelper.getNodeData(n);
    case Node.CDATA_SECTION_NODE :
    case Node.TEXT_NODE :
      return ((Text) n).getData();
    case Node.COMMENT_NODE :
    case Node.PROCESSING_INSTRUCTION_NODE :
    case Node.ATTRIBUTE_NODE :
      return n.getNodeValue();
    default :
      return DOMHelper.getNodeData(n);
    }
  }
  

  /**
   * Cast result object to a string.
   */
  public String str()
  {
    return m_xnodeset.str();
  }
  
  /**
   * Cast result object to a result tree fragment.
   */
  public DocumentFragment rtree(XPathSupport support)
  {    
    return rtree((XPathContext) support);
  }
  
  /**
   * Cast result object to a result tree fragment.
   *
   * @param support The XPath context to use for the conversion 
   *
   * @return the nodeset as a result tree fragment.
   */
  public DocumentFragment rtree(XPathContext support)
  {
    org.apache.xpath.XPathContext context = (org.apache.xpath.XPathContext)support;
    return m_xnodeset.rtree(context);
    //return (DocumentFragment)context.getDTMManager().getDTM(result).getNode(result);    
  }

  /**
   * Cast result object to a nodelist.
   */
  public NodeList nodeset() throws javax.xml.transform.TransformerException
  {
    return new DTMNodeList(m_xnodeset.iter());
  }  

  /**
   * Cast result object to a nodelist.
   */
  public NodeList mutableNodeset()
  {
   return new DTMNodeList(m_xnodeset.mutableNodeset());
  }  
  
  /**
   * Tell if one object is less than the other.
   */
  public boolean lessThan(XObject obj2)
    throws org.xml.sax.SAXException, javax.xml.transform.TransformerException
  {
    return m_xnodeset.lessThan(obj2.m_xObject);
  }
  
  /**
   * Tell if one object is less than or equal to the other.
   */
  public boolean lessThanOrEqual(XObject obj2)
    throws org.xml.sax.SAXException, javax.xml.transform.TransformerException
  {
    return m_xnodeset.lessThanOrEqual(obj2.m_xObject);
  }
  
  /**
   * Tell if one object is greater than the other.
   */
  public boolean greaterThan(XObject obj2)
    throws org.xml.sax.SAXException, javax.xml.transform.TransformerException
  {
    return m_xnodeset.greaterThan(obj2.m_xObject);
  }
  
  /**
   * Tell if one object is greater than the other.
   */
  public boolean greaterThanOrEqual(XObject obj2)
    throws org.xml.sax.SAXException, javax.xml.transform.TransformerException
  {
    return m_xnodeset.greaterThanOrEqual(obj2.m_xObject);
  }   
  
  /**
   * Tell if two objects are functionally equal.
   */
  public boolean equals(XObject obj2)
    throws org.xml.sax.SAXException
  {
    return m_xnodeset.equals(obj2);
  }  
  
  /**
   * Tell if two objects are functionally not equal.
   */
  public boolean notEquals(XObject obj2)
    throws org.xml.sax.SAXException, javax.xml.transform.TransformerException
  {
    return m_xnodeset.notEquals(obj2.m_xObject);
  }  
 
 static class NodeIteratorWrapper extends org.apache.xpath.NodeSetDTM
  {

    /** Position of next node          */
    private int m_pos = 0;

    /** Document fragment instance this will wrap         */
    private NodeList m_list;
    private org.apache.xml.dtm.DTMManager dtmManager; 

    /**
     * Constructor NodeIteratorWrapper
     *
     *
     * @param df Document fragment instance this will wrap
     */
    NodeIteratorWrapper(NodeList list)
    {
      super(new org.apache.xml.dtm.ref.DTMManagerDefault());
      m_list = list;
      dtmManager = getDTMManager(); 
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
    public int nextNode() throws DOMException
    {

      Node n;
      if (m_pos < m_list.getLength())
      {
       n = m_list.item(m_pos++);
       return dtmManager.getDTMHandleFromNode(n);
      }
      
      else
        return DTM.NULL;
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
    public int previousNode() throws DOMException
    {

      Node n;
      if (m_pos >0)
      {
        n = m_list.item(m_pos-1);
        return dtmManager.getDTMHandleFromNode(n);
      }
      else
        return DTM.NULL;
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
