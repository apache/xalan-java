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

import org.apache.xpath.compiler.Compiler;
import org.apache.xpath.patterns.NodeTest;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.compiler.OpCodes;

import org.w3c.dom.traversal.NodeIterator;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.DOMException;
import org.w3c.dom.traversal.NodeFilter;

/**
 * <meta name="usage" content="advanced"/>
 * This class implements an optimized iterator for
 * descendant, descendant-or-self, or "//foo" patterns.
 * @see org.apache.xpath.axes.WalkerFactory#newLocPathIterator.
 */
public class DescendantIterator extends LocPathIterator
{

  /**
   * Create a DescendantIterator object.
   *
   * @param compiler A reference to the Compiler that contains the op map.
   * @param opPos The position within the op map, which contains the
   * location path expression for this itterator.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public DescendantIterator(Compiler compiler, int opPos)
          throws javax.xml.transform.TransformerException
  {

    super(compiler, opPos, false);

    int ops[] = compiler.getOpMap();
    int firstStepPos = compiler.getFirstChildPos(opPos);
    int stepType = ops[firstStepPos];

    if (OpCodes.FROM_DESCENDANTS_OR_SELF == stepType)
      m_orSelf = true;
    else if(OpCodes.FROM_ROOT == stepType)
    {
      m_fromRoot = true;
      m_orSelf = true;
      firstStepPos += 8;
    }
    else
      m_orSelf = false;

    m_nodeTest = new NodeTest();

    int whatToShow = compiler.getWhatToShow(firstStepPos);

    if ((0 == (whatToShow
               & (NodeFilter.SHOW_ATTRIBUTE | NodeFilter.SHOW_ELEMENT
                  | NodeFilter.SHOW_PROCESSING_INSTRUCTION))) || (whatToShow == NodeFilter.SHOW_ALL))
      m_nodeTest.initNodeTest(whatToShow);
    else
    {
      m_nodeTest.initNodeTest(whatToShow, compiler.getStepNS(firstStepPos),
                              compiler.getStepLocalName(firstStepPos));
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
   * @exception DOMException
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

    Node pos;  // our main itteration node.  
    boolean getSelf;

    // Figure out what the start context should be.
    // If the m_lastFetched is null at this point we're at the start 
    // of a fresh iteration.
    if (null == m_lastFetched)
    {
      getSelf = m_orSelf; // true if descendants-or-self.
      
      // The start context can either be the location path context node, 
      // or the root node.
      if (getSelf && m_fromRoot)
      {
        if(m_context.getNodeType() == Node.DOCUMENT_NODE)
          pos = m_context;
        else
          pos = m_context.getOwnerDocument();
      }
      else
        pos = m_context;
      m_startContext = pos;
    }
    else
    {
      // if the iterator is not fresh...
      pos = m_lastFetched;
      getSelf = false;  // never process the start node at this point.
    }
    
    Node top = m_startContext; // tells us when to stop.
    Node next = null;

    // non-recursive depth-first traversal.
    while (null != pos)
    {
      if(getSelf)
      {
        try
        {
          XObject score = m_nodeTest.execute(m_execContext, pos);
          if (NodeTest.SCORE_NONE != score)
          {
            next = pos;
            break;
          }
        }
        catch (TransformerException te)
        {
          throw new org.apache.xml.utils.WrappedRuntimeException(te);
        }
      }
      else
        getSelf = true;
       
      Node nextNode = pos.getFirstChild();

      while (null == nextNode)
      {
        if (top.equals(pos))
          break;

        nextNode = pos.getNextSibling();

        if (null == nextNode)
        {
          pos = pos.getParentNode();

          if ((null == pos) || (top.equals(pos)))
          {
            nextNode = null;

            break;
          }
        }
      }

      pos = nextNode;
    }
    
    m_lastFetched = next;

    if (null != next)
    {
      if (null != m_cachedNodes)
        m_cachedNodes.addElement(next);

      m_next++;

      return next;
    }
    else
    {
      m_foundLast = true;
      m_startContext = null;

      return null;
    }
  }
  
  /** The top of the subtree, may not be the same as m_context if "//foo" pattern. */ 
  private Node m_startContext;

  /** The NodeTest for this iterator. */
  private NodeTest m_nodeTest;

  /** True if this is a descendants-or-self axes. */
  private boolean m_orSelf;
  
  /** True if this is a descendants-or-self axes. */
  private boolean m_fromRoot;
}
