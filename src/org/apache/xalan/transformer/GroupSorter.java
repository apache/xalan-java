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
package org.apache.xalan.transformer;

import java.util.Vector;

import java.text.NumberFormat;
import java.text.CollationKey;

//import org.w3c.dom.Node;
//import org.w3c.dom.traversal.NodeIterator;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xalan.transformer.GroupingIterator;

import org.apache.xpath.axes.ContextNodeList;
import org.apache.xpath.XPathContext;
import org.apache.xpath.NodeSetDTM;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XSequence;
import org.apache.xml.utils.NodeVector;

import javax.xml.transform.TransformerException;

/**
 * <meta name="usage" content="internal"/>
 * This class can sort vectors of DOM nodes according to a select pattern.
 */
public class GroupSorter extends NodeSorter
{

  
  /**
   * Construct a GroupSorter, passing in the XSL TransformerFactory
   * so it can know how to get the node data according to
   * the proper whitespace rules.
   *
   * @param p Xpath context to use
   */
  public GroupSorter(XPathContext p)
  {
    super(p);
  }

  /**
   * Given a vector of nodes, sort each node according to
   * the criteria in the keys.
   * @param v an vector of Nodes.
   * @param keys a vector of NodeSortKeys.
   * @param support XPath context to use
   *
   * @throws javax.xml.transform.TransformerException
   */
  public void sort(DTMIterator v, Vector keys, XPathContext support)
          throws javax.xml.transform.TransformerException
  {

    m_keys = keys;

    // QuickSort2(v, 0, v.size() - 1 );
    int n = v.getLength();

    // %OPT% Change mergesort to just take a DTMIterator?
    // We would also have to adapt DTMIterator to have the function 
    // of GroupCompareElem.
    
    // Create a vector of node compare elements
    // based on the input vector of nodes
    Vector nodes = new Vector();

    for (int i = 0; i < n; i++)
    {
      GroupCompareElem elem = new GroupCompareElem(v.item(i), v);

      nodes.addElement(elem);
    }

    Vector scratchVector = new Vector();

    mergesort(nodes, scratchVector, 0, n - 1, support);

    // return sorted vector of nodes
    for (int i = 0; i < n; i++)
    {
      v.setItem(((GroupCompareElem) nodes.elementAt(i)).m_node, i);
    }
    v.setCurrentPos(0);

    // old code...
    //NodeVector scratchVector = new NodeVector(n);
    //mergesort(v, scratchVector, 0, n - 1, support);
  }

  /**
   * Return the results of a compare of two nodes.
   * TODO: Optimize compare -- cache the getStringExpr results, key by m_selectPat + hash of node.
   *
   * @param n1 First node to use in compare
   * @param n2 Second node to use in compare
   * @param kIndex Index of NodeSortKey to use for sort
   * @param support XPath context to use
   *
   * @return The results of the compare of the two nodes.
   *
   * @throws TransformerException
   */
  int compare(
          NodeCompareElem n1, NodeCompareElem n2, int kIndex, XPathContext support)
            throws TransformerException
  {

    int result = 0;
    SortKey k = (SortKey) m_keys.elementAt(kIndex);

    if (k.m_treatAsNumbers)
    {
      double n1Num, n2Num;

      if (kIndex == 0)
      {
        n1Num = ((Double) n1.m_key1Value).doubleValue();
        n2Num = ((Double) n2.m_key1Value).doubleValue();
      }
      else if (kIndex == 1)
      {
        n1Num = ((Double) n1.m_key2Value).doubleValue();
        n2Num = ((Double) n2.m_key2Value).doubleValue();
      }

      /* Leave this in case we decide to use an array later
      if (kIndex < maxkey)
      {
      double n1Num = (double)n1.m_keyValue[kIndex];
      double n2Num = (double)n2.m_keyValue[kIndex];
      }*/
      else
      {

        // Get values dynamically
        XSequence savedCurrentGroup = support.getCurrentGroup();
      
        try{
          support.setCurrentGroup((XSequence)((GroupCompareElem)n1).m_group.clone());
        }
        catch (CloneNotSupportedException e)
        {}
        XObject r1 = k.m_selectPat.execute(m_execContext, n1.m_node,
                                           k.m_namespaceContext);
        try{
          support.setCurrentGroup((XSequence)((GroupCompareElem)n2).m_group.clone());
        }
        catch (CloneNotSupportedException e)
        {}
        XObject r2 = k.m_selectPat.execute(m_execContext, n2.m_node,
                                           k.m_namespaceContext);
        support.setCurrentGroup(savedCurrentGroup);
        n1Num = r1.num();

        // Can't use NaN for compare. They are never equal. Use zero instead.
        // That way we can keep elements in document order. 
        //n1Num = Double.isNaN(d) ? 0.0 : d;
        n2Num = r2.num();
        //n2Num = Double.isNaN(d) ? 0.0 : d;
      }

      if ((n1Num == n2Num) && ((kIndex + 1) < m_keys.size()))
      {
        result = compare(n1, n2, kIndex + 1, support);
      }
      else
      {
        double diff;
        if (Double.isNaN(n1Num))
        {
          if (Double.isNaN(n2Num))
            diff = 0.0;
          else
            diff = -1;
        }
        else if (Double.isNaN(n2Num))
           diff = 1;
        else
          diff = n1Num - n2Num;

        // process order parameter 
        result = (int) ((diff < 0.0)
                        ? (k.m_descending ? 1 : -1)
                        : (diff > 0.0) ? (k.m_descending ? -1 : 1) : 0);
      }
    }  // end treat as numbers 
    else
    {
      CollationKey n1String, n2String;

      if (kIndex == 0)
      {
        n1String = (CollationKey) n1.m_key1Value;
        n2String = (CollationKey) n2.m_key1Value;
      }
      else if (kIndex == 1)
      {
        n1String = (CollationKey) n1.m_key2Value;
        n2String = (CollationKey) n2.m_key2Value;
      }

      /* Leave this in case we decide to use an array later
      if (kIndex < maxkey)
      {
        String n1String = (String)n1.m_keyValue[kIndex];
        String n2String = (String)n2.m_keyValue[kIndex];
      }*/
      else
      {

        // Get values dynamically
        XSequence savedCurrentGroup = support.getCurrentGroup();
      
        try{
          support.setCurrentGroup((XSequence)((GroupCompareElem)n1).m_group.clone());
        }
        catch (CloneNotSupportedException e)
        {}
        XObject r1 = k.m_selectPat.execute(m_execContext, n1.m_node,
                                           k.m_namespaceContext);
        try{
          support.setCurrentGroup((XSequence)((GroupCompareElem)n2).m_group.clone());
        }
        catch (CloneNotSupportedException e)
        {}
        XObject r2 = k.m_selectPat.execute(m_execContext, n2.m_node,
                                           k.m_namespaceContext);
        support.setCurrentGroup(savedCurrentGroup);

        n1String = k.m_col.getCollationKey(r1.str());
        n2String = k.m_col.getCollationKey(r2.str());
      }

      // Use collation keys for faster compare, but note that whitespaces 
      // etc... are treated differently from if we were comparing Strings.
      result = n1String.compareTo(n2String);

      //Process caseOrder parameter
      if (k.m_caseOrderUpper)
      {
        String tempN1 = n1String.getSourceString().toLowerCase();
        String tempN2 = n2String.getSourceString().toLowerCase();

        if (tempN1.equals(tempN2))
        {

          //java defaults to upper case is greater.
          result = result == 0 ? 0 : -result;
        }
      }

      //Process order parameter
      if (k.m_descending)
      {
        result = -result;
      }
    }  //end else

    if (0 == result)
    {
      if ((kIndex + 1) < m_keys.size())
      {
        result = compare(n1, n2, kIndex + 1, support);
      }
    }

    if (0 == result)
    {

      // I shouldn't have to do this except that there seems to 
      // be a glitch in the mergesort
      // if(r1.getType() == r1.CLASS_NODESET)
      // {
      DTM dtm = support.getDTM(n1.m_node); // %OPT%
      result = dtm.isNodeAfter(n1.m_node, n2.m_node) ? -1 : 1;

      // }
    }

    return result;
  }

  

  /**
   * <meta name="usage" content="internal"/>
   * This class holds the value(s) from executing the given
   * node against the sort key(s). 
   */
  class GroupCompareElem extends NodeSorter.NodeCompareElem
  {

    
    /** Grouped items iterator         */
    GroupingIterator m_groupIterator;
    
    /** Group Iterator          */
    XSequence m_group;

    

    /**
     * Constructor NodeCompareElem
     *
     *
     * @param node Current node
     *
     * @throws javax.xml.transform.TransformerException
     */
    GroupCompareElem(int node, DTMIterator groupIterator) throws javax.xml.transform.TransformerException
    {
      super();
      boolean tryNextKey = true;

      m_node = node;
      m_groupIterator = (GroupingIterator)groupIterator;
      m_group = m_groupIterator.getSequence(node);

      XSequence savedCurrentGroup = m_execContext.getCurrentGroup();
      
      try{
        m_execContext.setCurrentGroup((XSequence)m_group.clone());
      }
      catch (CloneNotSupportedException e)
      {}
      if (!m_keys.isEmpty())
      {
        SortKey k1 = (SortKey) m_keys.elementAt(0);
        XObject r = k1.m_selectPat.execute(m_execContext, node,
                                           k1.m_namespaceContext);

        if (r == null)
          tryNextKey = false;

        double d;

        if (k1.m_treatAsNumbers)
        {
          d = r.num();

          // Can't use NaN for compare. They are never equal. Use zero instead.  
          m_key1Value = new Double(d);
        }
        else
        {
          m_key1Value = k1.m_col.getCollationKey(r.str());
        }

        if (r.getType() == XObject.CLASS_NODESET)
        {
          // %REVIEW%
          DTMIterator ni = ((XNodeSet)r).iterRaw();
          int current = ni.getCurrentNode();
          if(DTM.NULL == current)
            current = ni.nextNode();

          // if (ni instanceof ContextNodeList) // %REVIEW%
          tryNextKey = (DTM.NULL != current);

          // else abdicate... should never happen, but... -sb
        }

        if (m_keys.size() > 1)
        {
          SortKey k2 = (SortKey) m_keys.elementAt(1);

          if (!tryNextKey)
          {
            if (k2.m_treatAsNumbers)
              m_key2Value = new Double(0.0);
            else
              m_key2Value = k2.m_col.getCollationKey("");
          }
          else
          {
            XObject r2 = k2.m_selectPat.execute(m_execContext, node,
                                                k2.m_namespaceContext);

            if (k2.m_treatAsNumbers)
            {
              d = r2.num();
              m_key2Value = new Double(d);
            }
            else
              m_key2Value = k2.m_col.getCollationKey(r2.str());
          }
        }

        /* Leave this in case we decide to use an array later
        while (kIndex <= m_keys.size() && kIndex < maxkey)
        {
          NodeSortKey k = (NodeSortKey)m_keys.elementAt(kIndex);
          XObject r = k.m_selectPat.execute(m_execContext, node, k.m_namespaceContext);
          if(k.m_treatAsNumbers)
            m_KeyValue[kIndex] = r.num();
          else
            m_KeyValue[kIndex] = r.str();
        } */
      }  // end if not empty
      m_execContext.setCurrentGroup(savedCurrentGroup);
    }
  }  // end NodeCompareElem class
}
