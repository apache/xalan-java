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

import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.traversal.NodeFilter;

import org.apache.xpath.patterns.NodeTestFilter;

/**
 * <meta name="usage" content="advanced"/>
 * This walker should be used when the walker is at the end of a location 
 * path, or is the only step.  It has a much simplified nextNode() method.
 */
public class AttributeWalkerOneStep extends AxesWalker
{

  /** The attribute list from the context node.  */
  transient NamedNodeMap m_attributeList;

  /** The current index into m_attributeList.  -1 to start. */
  transient int m_attrListPos;

  /** The number of attributes in m_attributeList, or -2 if no attributes. */
  transient int m_nAttrs;

  /**
   *  The root node of the TreeWalker.
   *
   * @param root The context node of the node step.
   */
  public void setRoot(Node root)
  {

    super.setRoot(root);

    if (root.getNodeType() == Node.ELEMENT_NODE)
    {
      m_attrListPos = -1;
      m_attributeList = m_currentNode.getAttributes();

      if (null != m_attributeList)
        m_nAttrs = m_attributeList.getLength();
      else
        m_nAttrs = -2;
    }
  }

  /**
   * Construct an AxesWalker using a LocPathIterator.
   *
   * @param locPathIterator The location path iterator that 'owns' this walker.
   */
  public AttributeWalkerOneStep(LocPathIterator locPathIterator)
  {
    super(locPathIterator);
  }

  /**
   * Get the next node in document order on the axes.
   *
   * @return The next node in the itteration, or null.
   */
  public Node nextNode()
  {

    if (m_isFresh)
      m_isFresh = false;

    Node current = this.getCurrentNode();

    if (current.isSupported(FEATURE_NODETESTFILTER, "1.0"))
      ((NodeTestFilter) current).setNodeTest(this);

    Node next = null;

    while (null != m_attributeList)
    {
      m_attrListPos++;

      if (m_attrListPos < m_nAttrs)
      {
        next = m_attributeList.item(m_attrListPos);

        if (null != next)
          m_currentNode = next;

        if (acceptNode(next) == NodeFilter.FILTER_ACCEPT)
          break;
      }
      else
      {
        next = null;
        m_attributeList = null;
      }
    }

    if (null == next)
      this.m_isDone = true;

    return next;
  }
}
