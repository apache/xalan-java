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

import java.util.Stack;

import org.apache.xpath.axes.LocPathIterator;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.DOMHelper;

import org.w3c.dom.Node;

/**
 * Walker for the 'ancestor' axes.
 * @see <a href="http://www.w3.org/TR/xpath#axes">XPath axes descriptions</a>
 */
public class AncestorWalker extends ReverseAxesWalker
{

  /**
   * Construct an AncestorWalker using a LocPathWalker.
   *
   * @param locPathIterator The location path iterator that 'owns' this walker.
   */
  public AncestorWalker(LocPathIterator locPathIterator)
  {
    super(locPathIterator);
  }
  
  /**
   * Get a cloned AncestorWalker.
   *
   * @return A new AncestorWalker that can be used without mutating this one.
   *
   * @throws CloneNotSupportedException
   */
  public Object clone() throws CloneNotSupportedException
  {

    AncestorWalker clone = (AncestorWalker) super.clone();
    // if(null != clone.m_ancestors)
    //  clone.m_ancestorsPos = clone.m_ancestors.size() - 1;
    return clone;
  }

  /**
   * Push the ancestor nodes.
   *
   * @param n
   */
  protected void pushAncestors(Node n)
  {

    m_ancestors = new Stack();

    DOMHelper dh = m_lpi.getDOMHelper();

    while (null != (n = dh.getParentOfNode(n)))
    {
      m_ancestors.push(n);
    }

    m_nextLevelAmount = m_ancestors.isEmpty() ? 0 : 1;
    m_ancestorsPos = m_ancestors.size() - 1;
  }

  /**
   *  The root node of the TreeWalker.
   *
   * @param root The context node of this step.
   */
  public void setRoot(Node root)
  {
    pushAncestors(root);
    super.setRoot(root);
  }

  /**
   *  Moves the <code>TreeWalker</code> to the first visible child of the
   * current node, and returns the new node. If the current node has no
   * visible children, returns <code>null</code> , and retains the current
   * node.
   * @return  The new node, or <code>null</code> if the current node has no
   *   visible children in the TreeWalker's logical view.
   */
  public Node firstChild()
  {

    Node next = (m_ancestorsPos < 0) ? null : (Node) m_ancestors.elementAt(m_ancestorsPos--);

    m_nextLevelAmount = (m_ancestorsPos < 0) ? 0 : 1;

    return setCurrentIfNotNull(next);
  }

  /** Stack of ancestors.  We have to do this instead of 
   *  just using getParent on the fly, because we have to walk the ancestors 
   *  in document order. */
  transient protected Stack m_ancestors;
  
  /** The position within the stack.
   *  @serial */
  transient protected int m_ancestorsPos;

  /**
   * Tell what's the maximum level this axes can descend to.
   *
   * @return An estimation of the maximum level this axes can descend to.
   */
  protected int getLevelMax()
  {

    DOMHelper dh = m_lpi.getDOMHelper();
    Node p = dh.getParentOfNode(m_root);

    return (null == p) ? 1 : dh.getLevel(p);
  }
}
