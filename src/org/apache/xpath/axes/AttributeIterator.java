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

import javax.xml.transform.TransformerException;

import org.apache.xpath.XPathContext;
import org.apache.xpath.compiler.Compiler;
import org.apache.xpath.patterns.NodeTest;
import org.apache.xpath.objects.XObject;

import org.w3c.dom.traversal.NodeIterator;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.DOMException;
import org.w3c.dom.traversal.NodeFilter;

/**
 * <meta name="usage" content="advanced"/>
 * This class implements an optimized iterator for
 * attribute axes patterns.
 * @see org.apache.xpath.axes.WalkerFactory#newLocPathIterator
 */
public class AttributeIterator extends LocPathIterator
{

  /**
   * Create a AttributeIterator object.
   *
   * @param compiler A reference to the Compiler that contains the op map.
   * @param opPos The position within the op map, which contains the
   * location path expression for this itterator.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public AttributeIterator(Compiler compiler, int opPos, int analysis)
          throws javax.xml.transform.TransformerException
  {

    super(compiler, opPos, analysis, false);

    int firstStepPos = compiler.getFirstChildPos(opPos);
    int whatToShow = compiler.getWhatToShow(firstStepPos);

    if ((0 == (whatToShow
               & (NodeFilter.SHOW_ATTRIBUTE | NodeFilter.SHOW_ELEMENT
                  | NodeFilter.SHOW_PROCESSING_INSTRUCTION))) || (whatToShow == NodeFilter.SHOW_ALL))
      initNodeTest(whatToShow);
    else
    {
      initNodeTest(whatToShow, compiler.getStepNS(firstStepPos),
                              compiler.getStepLocalName(firstStepPos));
    }
    initPredicateInfo(compiler, firstStepPos);
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
    return clone;
  }

  /**
   *  Get a cloned Iterator that is reset to the beginning
   *  of the query.
   * 
   *  @return A cloned NodeIterator set of the start of the query.
   * 
   *  @throws CloneNotSupportedException
   */
  public NodeIterator cloneWithReset() throws CloneNotSupportedException
  {

    AttributeIterator clone = (AttributeIterator) super.cloneWithReset();

    clone.m_attrListPos = 0;
    
    clone.resetProximityPositions();

    return clone;
  }

  /**
   * Initialize the attribute list.
   */
  private void initAttrList()
  {

    if (m_context.getNodeType() == Node.ELEMENT_NODE)
    {
      m_attrListPos = 0;
      m_attributeList = m_context.getAttributes();

      if (null != m_attributeList)
      {
        m_nAttrs = m_attributeList.getLength();
      }
      else
        m_nAttrs = 0;

      if (0 == m_nAttrs)
      {
        m_foundLast = true;
      }
    }
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
    // If the cache is on, and the node has already been found, then 
    // just return from the list.
    if ((null != m_cachedNodes)
            && (m_cachedNodes.getCurrentPos() < m_cachedNodes.size()))
    {
      Node next = m_cachedNodes.nextNode();

      this.setCurrentPos(m_cachedNodes.getCurrentPos());

      return next;
    }

    if (m_foundLast)
      return null;

    if (null == m_attributeList)
    {
      initAttrList();
      resetProximityPositions();
    }

    org.apache.xpath.VariableStack vars;
    int savedStart;
    if (-1 != m_varStackPos)
    {
      vars = m_execContext.getVarStack();

      // These three statements need to be combined into one operation.
      savedStart = vars.getSearchStart();

      vars.setSearchStart(m_varStackPos);
      vars.pushContextPosition(m_varStackContext);
    }
    else
    {
      // Yuck.  Just to shut up the compiler!
      vars = null;
      savedStart = 0;
    }
    
    try
    {
      Node next;
  
      do
      {
        m_lastFetched = next =
          (null == m_attributeList || m_attrListPos >= m_nAttrs)
          ? null : m_attributeList.item(m_attrListPos++);
  
        if (null != next)
        {
          if(NodeFilter.FILTER_ACCEPT == acceptNode(next))
            break;
          else
            continue;
        }
        else
          break;
      }
      while (next != null);
  
      if (null != next)
      {
        if (null != m_cachedNodes)
          m_cachedNodes.addElement(m_lastFetched);
  
        m_next++;
  
        return next;
      }
      else
      {
        m_foundLast = true;
  
        return null;
      }
    }
    finally
    {
      if (-1 != m_varStackPos)
      {
        // These two statements need to be combined into one operation.
        vars.setSearchStart(savedStart);
        vars.popContextPosition();
      }
    }
  }
  
  /**
   * Get the index of the last node that can be itterated to.
   *
   * @param xctxt XPath runtime context.
   *
   * @return the index of the last node that can be itterated to.
   */
  public int getLastPos(XPathContext xctxt)
  {
    return m_nAttrs;
  }


  /** The attribute list for the given context. */
  transient private NamedNodeMap m_attributeList;

  /** The position within the attribute list. */
  transient private int m_attrListPos = 0;

  /** The number of attributes within the list. */
  transient private int m_nAttrs = 0;
}
