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

//import org.w3c.dom.Node;
//import org.w3c.dom.traversal.NodeFilter;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;

import org.apache.xpath.patterns.NodeTestFilter;

/**
 * This class extends ChildWalkerMultiStep to handle the root step of
 * patterns such as "/foo/baz" where the first step is the root, and the
 * rest of the steps are simple child steps.
 */
public class RootWalkerMultiStep extends ChildWalkerMultiStep
{

  /**
   * Construct an ChildWalkerMultiStep using a LocPathIterator.
   *
   * @param locPathIterator
   */
  public RootWalkerMultiStep(LocPathIterator locPathIterator)
  {
    super(locPathIterator);
  }

  /**
   *  Set the root node of the TreeWalker.
   *
   * @param root The context node of this step.
   */
  public void setRoot(int root)
  {

    super.setRoot(root);

    m_processedRoot = false;
  }

  /**
   * Get the next node in document order on the axes.
   *
   * @return the next valid child node.
   */
  protected int getNextNode()
  {

    if (m_isFresh)
      m_isFresh = false;

    int current = this.getCurrentNode();

    // %NODETESTFILTER%
//    if (current.isSupported(FEATURE_NODETESTFILTER, "1.0"))
//      ((NodeTestFilter) current).setNodeTest(this);

    int next;

    if (!m_processedRoot)
    {
      m_processedRoot = true;
      
      next = getDTM(m_currentNode).getDocument();
    }
    else
      next = DTM.NULL;

    if (DTM.NULL != next)
    {
      m_currentNode = next;

      // doesn't seem like we need to do this!
      if (acceptNode(next) != DTMIterator.FILTER_ACCEPT)
      {
        next = DTM.NULL;
      }

      if (DTM.NULL == next)
      {
        m_currentNode = current;  // don't advance the current node.
        this.m_isDone = true;
      }
    }
    else
      this.m_isDone = true;

    return next;
  }

  /** True if the root node has been processed. */
  transient boolean m_processedRoot = false;
}
