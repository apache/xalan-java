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
package org.apache.xalan.stree;

// DOM Imports
import org.w3c.dom.traversal.NodeIterator;
import org.w3c.dom.Node;
import org.w3c.dom.DOMException;

// Xalan Imports
import org.apache.xalan.xpath.NodeSet;
import org.apache.xalan.xpath.XPath;
import org.apache.xalan.xpath.XPathContext;

/**
 * <meta name="usage" content="advanced"/>
 * This class extends NodeSet, which implements NodeIterator, 
 * and fetches nodes one at a time in document order based on a XPath
 * <a href="http://www.w3.org/TR/xpath#NT-UnionExpr">UnionExpr</a>.
 * As each node is iterated via nextNode(), the node is also stored 
 * in the NodeVector, so that previousNode() can easily be done.
 */
public class IndexedUnionPathIterator extends NodeSet
{
  /**
   * Package-private constructor, which takes the 
   * same arguments as XLocator's union() function, plus the 
   * locator reference.
   */
  IndexedUnionPathIterator(XPath xpath, XPathContext execContext, 
                    Node context, int opPos,
                    StreeLocator locator)
  {
    super();
    
    this.m_xpath = xpath;
    this.m_execContext = execContext;
    this.m_context = context;
    this.m_unionOpPos = opPos;
    this.m_locator = locator;
    init();
  }
  
  /**
   * Initialize the location path iterators.
   */
  protected void init()
  {
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
    return null;
  }
  
  /**
   * The XPath that contains the union expression.
   */
  protected XPath m_xpath;
  
  /**
   * The execution context for the expression.
   */
  protected XPathContext m_execContext;
  
  /**
   * The node context for the expression.
   */
  protected Node m_context;
  
  /**
   * The op code position of the union path.
   */
  protected int m_unionOpPos;
  
  /**
   * Reference to the FastNodeLocator that created 
   * this instance.
   */
  protected StreeLocator m_locator;
  
  /**
   * The location path iterators, one for each 
   * <a href="http://www.w3.org/TR/xpath#NT-LocationPath">location 
   * path</a> contained in the union expression.
   */
  protected IndexedLocPathIterator[] m_iterators;

}
