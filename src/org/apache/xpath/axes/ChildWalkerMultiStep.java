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
import org.w3c.dom.traversal.NodeFilter;

import org.apache.xpath.patterns.NodeTestFilter;

/**
 * <meta name="usage" content="advanced"/>
 * A child walker multistep is created while only child steps are found.
 *
 * @author sboag@lotus.com
 */
public class ChildWalkerMultiStep extends AxesWalker
{

  /**
   * Construct an ChildWalkerMultiStep using a LocPathIterator.
   *
   * @param locPathIterator
   */
  public ChildWalkerMultiStep(LocPathIterator locPathIterator)
  {
    super(locPathIterator);
  }

  /**
   * Get the next node in document order on the axes.
   *
   * @return the next valid child node.
   */
  protected Node getNextNode()
  {
    if (m_isFresh)
      m_isFresh = false;

    Node current = this.getCurrentNode();
    
    if (current.isSupported(FEATURE_NODETESTFILTER, "1.0"))
      ((NodeTestFilter) current).setNodeTest(this);

    Node next = (m_root == m_currentNode)
                ? m_currentNode.getFirstChild()
                : m_currentNode.getNextSibling();

    if (null != next)
    {
      m_currentNode = next;

      while (acceptNode(next) != NodeFilter.FILTER_ACCEPT)
      {
        next = next.getNextSibling();

        if (null == next)
          break;
        else
          m_currentNode = next;
      }
      
      if(null == next)
        m_currentNode = current; // don't advance the current node.
    }          

    if (null == next)
      this.m_isDone = true;

    return next;
  }

  /**
   * Moves the <code>TreeWalker</code> to the next visible node in document
   * order relative to the current node, and returns the new node. If the
   * current node has no next node,  or if the search for nextNode attempts
   * to step upward from the TreeWalker's root node, returns
   * <code>null</code> , and retains the current node.
   *
   * @return  The new node, or <code>null</code> if the current node has no
   *   next node  in the TreeWalker's logical view.
   */
  public Node nextNode()
  {

    AxesWalker walker = m_lpi.getLastUsedWalker();
    boolean fast = (null != walker) ? walker.isFastWalker() : false;

    while (null != walker)
    {
      Node next;
      if(fast)
      {
        next = walker.getNextNode();
      }
      else
      {
        next = walker.nextNode();
        // In this case, nextNode finished the walk, so we just return.
        if(null != next)
          return next;
      }

      if (null != next)
      {
        if (null != walker.m_nextWalker)
        {
          walker = walker.m_nextWalker;

          walker.setRoot(next);
          m_lpi.setLastUsedWalker(walker);
          fast = walker.isFastWalker();
        }
        else
          return next;
      }
      else
      {
        walker = walker.m_prevWalker;

        if (null != walker)
          fast = walker.isFastWalker();
        m_lpi.setLastUsedWalker(walker);
      }
    }

    return null;
  }
  
  /**
   * Tell if this is a special type of walker compatible with ChildWalkerMultiStep.
   * 
   * @return true this is a special type of walker compatible with ChildWalkerMultiStep.
   */
  protected boolean isFastWalker()
  {
    return true;
  }

}
