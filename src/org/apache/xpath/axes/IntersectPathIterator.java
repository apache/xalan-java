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

import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.VariableComposeState;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.functions.Function;
import org.apache.xpath.operations.Variable;
import org.apache.xpath.parser.Node;
import org.apache.xpath.parser.SimpleNode;

/**
 * <meta name="usage" content="advanced"/>
 * This class extends NodeSetDTM, which implements DTMIterator,
 * and fetches nodes one at a time in document order based on a XPath
 * <a href="http://www.w3.org/TR/xpath#NT-UnionExpr">UnionExpr</a>.
 * As each node is iterated via nextNode(), the node is also stored
 * in the NodeVector, so that previousNode() can easily be done.
 */
public class IntersectPathIterator
  extends ExceptPathIterator
{

  /**
   * Constructor to create an instance which you can add location paths to.
   */
  public IntersectPathIterator()
  {

    super();
  }
  
  /**
   *  Test whether a specified node is visible in the logical view of a
   * TreeWalker or NodeIterator. This function will be called by the
   * implementation of TreeWalker and NodeIterator; it is not intended to
   * be called directly from user code.
   * @param n  The node to check to see if it passes the filter or not.
   * @return  a constant to determine whether the node is accepted,
   *   rejected, or skipped, as defined  above .
   */
  public short acceptNode(int n)
  {
  	short accept = DTMIterator.FILTER_ACCEPT;
  	if (getPredicateCount() != 0)
  	 accept = super.acceptNode(n);
  	if (accept == DTMIterator.FILTER_ACCEPT)
  	{
  	  accept = DTMIterator.FILTER_SKIP;
  	  try
  	  {
  	    DTMIterator iterator = (DTMIterator)m_iterators[1].clone();
  		int node = iterator.getCurrentNode();
  		while (node != DTM.NULL)
  		{
  			if (n ==  node)
  			{
  			  accept = DTMIterator.FILTER_ACCEPT;
  			  break;
  			}
  			node = iterator.nextNode();
  		}
  	  }
  	  catch(CloneNotSupportedException ex) {} 
  	}
  	return accept;
  }

}
