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
import org.apache.xpath.WhitespaceStrippingElementMatcher;
import org.apache.xml.utils.PrefixResolver;

import org.w3c.dom.Node;
import org.w3c.dom.DOMException;

/**
 * <meta name="usage" content="advanced"/>
 * This class implements an optimized iterator for
 * "node()" patterns, that is, any children of the
 * context node.
 * @see org.apache.xpath.axes.WalkerFactory#newLocPathIterator
 */
public class ChildIterator extends LocPathIterator
{

  /**
   * Create a ChildIterator object.
   *
   * @param compiler A reference to the Compiler that contains the op map.
   * @param opPos The position within the op map, which contains the
   * location path expression for this itterator.
   * NEEDSDOC @param analysis
   *
   * @throws javax.xml.transform.TransformerException
   */
  public ChildIterator(Compiler compiler, int opPos, int analysis)
          throws javax.xml.transform.TransformerException
  {
    super(compiler, opPos, analysis, false);
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

    Node next;

    while (true)
    {
      m_lastFetched = next = (null == m_lastFetched)
                             ? m_context.getFirstChild()
                             : m_lastFetched.getNextSibling();

      // Yuck!  Blech!  -sb
      if (null != next)
      {
        int nt = next.getNodeType();
        if(Node.DOCUMENT_TYPE_NODE == nt) // bug fix, position14, d2d, xerces DOM
          continue;
        else if ((Node.TEXT_NODE == nt)
                &&!next.isSupported(SUPPORTS_PRE_STRIPPING, null))
        {
          Node parent = next.getParentNode();

          if (null != parent && Node.ELEMENT_NODE == parent.getNodeType())
          {
            String data = next.getNodeValue();

            if (org.apache.xml.utils.XMLCharacterRecognizer.isWhiteSpace(
                    data))
            {

              // Ugly trick for now.
              PrefixResolver resolver =
                getXPathContext().getNamespaceContext();

              if (resolver instanceof WhitespaceStrippingElementMatcher)
              {
                WhitespaceStrippingElementMatcher wsem =
                  (WhitespaceStrippingElementMatcher) resolver;

                try
                {
                  if (wsem.shouldStripWhiteSpace(
                          getXPathContext(), (org.w3c.dom.Element) parent))
                  {
                    continue;
                  }
                }
                catch (javax.xml.transform.TransformerException te)
                {
                  throw new org.apache.xml.utils.WrappedRuntimeException(te);
                }
              }
            }
          }
        }
      }

      break;
    }

    // m_lastFetched = next;
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
}
