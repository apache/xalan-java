/*
 * The Apache Software License, Version 1.1 
 *
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights 
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
public class ExceptPathIterator
  extends UnionPathIterator
{

  /**
   * Constructor to create an instance which you can add location paths to.
   */
  public ExceptPathIterator()
  {

    super();
  }
  
  /**
   * Initialize the context values for this expression 
   * after it is cloned.
   *
   * @param execContext The XPath runtime context for this 
   * transformation.
   */
  public void initIterators(int context)
  {
    try
    {
      if (null != m_exprs)
      {
        int n = m_exprs.length;
        DTMIterator newIters[] = new DTMIterator[n];

        for (int i = 0; i < n; i++)
        {
          DTMIterator iter = m_exprs[i].asIterator(m_execContext, context);
          newIters[i] = iter;
          iter.setShouldCacheNodes(true);
          iter.nextNode();
        }
        m_iterators = newIters;
      }
    }
    catch (Exception e)
    {
      throw new org.apache.xml.utils.WrappedRuntimeException(e);
    }
  }


  /**
   *  Returns the next node in the set and advances the position of the
   * iterator in the set. After a DTMIterator is created, the first call
   * to nextNode() returns the first node in the set.
   * 
   * @return  The next <code>Node</code> in the set being iterated over, or
   *   <code>null</code> if there are no more members in that set.
   */
  public int nextNode()
  {
    if (m_foundLast)
      return DTM.NULL;

    if (DTM.NULL == m_lastFetched)
    {
      resetProximityPositions();
    }

    // Loop through the iterators getting the current fetched 
    // node, and get the earliest occuring in document order
    int earliestNode = DTM.NULL;

    if (null != m_iterators)
    {
      //int n = m_iterators.length;
     // int iteratorUsed = -1;
    //  int savedIteratorUsed = -1;
    //  int savedEarliestNode = DTM.NULL;

      while (true)
      {
        //for (int i = 0; i < n; i++)
       // {
          int node = m_iterators[0].getCurrentNode();

          if (DTM.NULL == node)
            break;
          else
            if (DTM.NULL == earliestNode)
            {
              //iteratorUsed = i;
              earliestNode = node;
            }
            else
            {
              if (node == earliestNode)
              {

                // Found a duplicate, so skip past it.
                // %REVIEW% Make sure this is really what we 
                // want to do for XPath 2.0.
                m_iterators[0].nextNode();
              }
              else
              {
                DTM dtm = getDTM(node);

                if (dtm.isNodeAfter(node, earliestNode))
                {
                  //iteratorUsed = i;
                  earliestNode = node;
                }
              }
            }
        //}

        if (DTM.NULL == earliestNode)
          break;

        else         
          {
            m_iterators[0].nextNode();
            int acceptence = acceptNode(earliestNode);

            if (DTMIterator.FILTER_ACCEPT == acceptence)
              break;
              
            earliestNode = DTM.NULL;
          }
      }

      if (DTM.NULL != earliestNode)
      {
        // m_iterators[iteratorUsed].nextNode();

        incrementCurrentPos();
      }
      else
        m_foundLast = true;
    }

    m_lastFetched = earliestNode;

    return earliestNode;
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
  	    DTMIterator iterator = (DTMIterator)m_iterators[1];
  	    int node;
  	    if (iterator.isFresh())
  	      node = iterator.nextNode();
  	    else
  		  node = iterator.getCurrentNode();
  		while (node != DTM.NULL)
  		{
  			if (n ==  node)
  			{
  			  iterator.reset();
  			  return DTMIterator.FILTER_SKIP;
  			}
  			node = iterator.nextNode();
  		}
  		iterator.reset();
  	}
  	
  	return accept;
  }

}
