/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.xalan.transformer;

import java.text.CollationKey;
import java.util.Vector;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XObject;

/**
 * This class can sort vectors of xdm nodes according 
 * to an XPath select pattern.
 * 
 * @author Scott Boag <scott_boag@us.ibm.com>
 * @author Joseph Kesselman <keshlam@alum.mit.edu>
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage internal
 */
public class NodeSorter
{

  /** Current XPath context           */
  XPathContext m_xctxt;

  /** Vector of NodeSortKeys          */
  Vector m_keys;

  /**
   * Construct a NodeSorter, passing in the XSL TransformerFactory
   * so it can know how to get the node data according to
   * the proper whitespace rules.
   *
   * @param xctxt               An XPath context object
   */
  public NodeSorter(XPathContext xctxt)
  {
      m_xctxt = xctxt;
  }

  /**
   * Given a vector of nodes, sort each node according to
   * the criteria in the keys.
   * 
   * @param v                          An vector of Nodes
   * @param keys                       A vector of NodeSortKeys
   * @param xctxt                      XPath context to use
   *
   * @throws javax.xml.transform.TransformerException
   */
  public void sort(DTMCursorIterator v, Vector keys, XPathContext xctxt) 
		                                                                throws javax.xml.transform.TransformerException {

	  m_keys = keys;

	  int n = v.getLength();

	  // Create a vector of node compare elements, according 
	  // to an input vector of nodes
	  Vector nodes = new Vector();

	  for (int idx = 0; idx < n; idx++)
	  {
		  NodeCompareElem elem = new NodeCompareElem(v.item(idx));

		  nodes.addElement(elem);
	  }

	  Vector scratchVector = new Vector();

	  mergesort(nodes, scratchVector, 0, n - 1, xctxt);

	  // Return sorted vector of nodes
	  for (int idx = 0; idx < n; idx++)
	  {
		  v.setItem(((NodeCompareElem)(nodes.elementAt(idx))).m_node, idx);
	  }

	  v.setCurrentPos(0);
  }
  
  /**
   * Given an xdm sequence of items, sort each item according to
   * the criteria in the keys.
   * 
   * @param rSeq                  An xdm sequence of items
   * @param keys                  A vector of NodeSortKeys
   * @param xctxt                 XPath context to use
   *
   * @throws javax.xml.transform.TransformerException
   */
  public ResultSequence sort(ResultSequence rSeq, Vector keys, XPathContext xctxt) 
                                                                             throws javax.xml.transform.TransformerException {

	  ResultSequence result = new ResultSequence();
	  
	  m_keys = keys;

	  int n = rSeq.size();

	  // Create a vector of xdm compare items, according 
	  // to an input vector of xdm items.
	  Vector xdmItemVector = new Vector();

	  for (int idx = 0; idx < n; idx++)
	  {
		  NodeCompareElem elem = new NodeCompareElem(rSeq.item(idx));

		  xdmItemVector.addElement(elem);
	  }

	  Vector scratchVector = new Vector();

	  mergesort(xdmItemVector, scratchVector, 0, n - 1, xctxt);

	  // Return sorted vector of xdm items
	  for (int idx = 0; idx < n; idx++)
	  {		  
		  result.add(((NodeCompareElem)(xdmItemVector.elementAt(idx))).m_xobj);
	  }
	  
	  return result;
  }

  /**
   * Return the results of a compare of two nodes.
   *
   * @param n1 First node to use in compare
   * @param n2 Second node to use in compare
   * @param kIndex Index of NodeSortKey to use for sort
   * @param xctxt XPath context to use
   *
   * @return The results of the compare of the two nodes.
   *
   * @throws javax.xml.transform.TransformerException
   */
  int compare(NodeCompareElem n1, NodeCompareElem n2, int kIndex, XPathContext xctxt) 
		                                                                             throws javax.xml.transform.TransformerException
  {

	  int result = 0;
	  
	  NodeSortKey k = (NodeSortKey) m_keys.elementAt(kIndex);

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
		  else
		  {
			  // Get values dynamically
			  XObject r1 = k.m_selectPat.execute(m_xctxt, n1.m_node,
					                             k.m_namespaceContext);
			  XObject r2 = k.m_selectPat.execute(m_xctxt, n2.m_node,
					                             k.m_namespaceContext);
			  n1Num = r1.num();

			  // Can't use NaN for compare. They are never equal. Use zero instead.
			  // That way we can keep elements in document order. 
			  n2Num = r2.num();
		  }

		  if ((n1Num == n2Num) && ((kIndex + 1) < m_keys.size()))
		  {
			  result = compare(n1, n2, kIndex + 1, xctxt);
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
		  else
		  {
			  // Get values dynamically
			  XObject r1 = k.m_selectPat.execute(m_xctxt, n1.m_node,
					                             k.m_namespaceContext);
			  XObject r2 = k.m_selectPat.execute(m_xctxt, n2.m_node,
					                             k.m_namespaceContext);

			  String str1 = XslTransformEvaluationHelper.getStrVal(r1);
			  String str2 = XslTransformEvaluationHelper.getStrVal(r2);
			  
			  n1String = k.m_col.getCollationKey(str1);
			  n2String = k.m_col.getCollationKey(str2);
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
				  //java defaults to upper case is greater
				  result = result == 0 ? 0 : -result;
			  }
		  }

		  //Process order parameter
		  if (k.m_descending)
		  {
			  result = -result;
		  }
	  }  //end else

	  if (result == 0)
	  {
		  if ((kIndex + 1) < m_keys.size())
		  {
			  result = compare(n1, n2, kIndex + 1, xctxt);
		  }
	  }

	  if (result == 0)
	  {      
		  if (n1.m_xobj == null) {
		     DTM dtm = xctxt.getDTM(n1.m_node);		  
		     result = dtm.isNodeAfter(n1.m_node, n2.m_node) ? -1 : 1;
		  }
		  else {
			 boolean result2 = n1.m_xobj.vcGreaterThan(n2.m_xobj, null, null, true);
			 
			 result = (result2 ? 1 : -1); 
		  }
	  }

	  return result;
  }

  /**
   * This implements a standard Mergesort, as described in
   * Robert Sedgewick's Algorithms book.  This is a better
   * sort for our purpose than the Quicksort because it
   * maintains the original document order of the input if
   * the order isn't changed by the sort.
   *
   * @param a First vector of nodes to compare
   * @param b Second vector of  nodes to compare 
   * @param l Left boundary of  partition
   * @param r Right boundary of  partition
   * @param xctxt XPath context to use
   *
   * @throws javax.xml.transform.TransformerException
   */
  void mergesort(Vector a, Vector b, int l, int r, XPathContext xctxt) 
		                                                              throws javax.xml.transform.TransformerException
  {

	  if ((r - l) > 0)
	  {
		  int m = (r + l) / 2;

		  mergesort(a, b, l, m, xctxt);
		  mergesort(a, b, m + 1, r, xctxt);

		  int i, j, k;

		  for (i = m; i >= l; i--)
		  {
			  // Use insert if we need to increment vector size
			  if (i >= b.size())
				  b.insertElementAt(a.elementAt(i), i);
			  else
				  b.setElementAt(a.elementAt(i), i);
		  }

		  i = l;

		  for (j = (m + 1); j <= r; j++)
		  {
			  if (r + m + 1 - j >= b.size())
				  b.insertElementAt(a.elementAt(j), r + m + 1 - j);
			  else
				  b.setElementAt(a.elementAt(j), r + m + 1 - j);
		  }

		  j = r;

		  int compVal;

		  for (k = l; k <= r; k++)
		  {
			  if (i == j)
				  compVal = -1;
			  else
				  compVal = compare((NodeCompareElem) b.elementAt(i), (NodeCompareElem) b.elementAt(j), 0, xctxt);

			  if (compVal < 0)
			  {
				  a.setElementAt(b.elementAt(i), k);

				  i++;
			  }
			  else if (compVal > 0)
			  { 
				  a.setElementAt(b.elementAt(j), k);

				  j--;
			  }
		  }
	  }
  }

  /**
   * This class holds the value(s) from executing the given
   * node against the sort key(s). 
   * @xsl.usage internal
   */
  class NodeCompareElem
  {

	/**
	 * Only one of the class fields, m_node or m_xobj 
	 * shall be available for use.
	 * 
	 * The field m_node is used, when there is an xdm nodeset 
	 * to be sorted, and the field m_xobj is used when there 
	 * is an xdm sequence of non-node items have to be sorted. 
	 */
	  
    /** Current node              */
    int m_node;
    
    /** Current xdm item          */
    XObject m_xobj;

    /** This maxkey value was chosen arbitrarily. We are assuming that the    
    // maxkey + 1 keys will only hit fairly rarely and therefore, we
    // will get the node values for those keys dynamically.
    */
    int maxkey = 2;

    // Keep this in case we decide to use an array. Right now
    // using two variables is cheaper.
    //Object[] m_KeyValue = new Object[2];

    /** Value from first sort key           */
    Object m_key1Value;

    /** Value from second sort key            */
    Object m_key2Value;

    /**
     * Constructor NodeCompareElem.
     *
     * @param node Current node
     *
     * @throws javax.xml.transform.TransformerException
     */
    NodeCompareElem(int node) throws javax.xml.transform.TransformerException
    {
    	m_node = node;

    	if (!m_keys.isEmpty())
    	{
    		NodeSortKey k1 = (NodeSortKey) m_keys.elementAt(0);
    		XObject r = k1.m_selectPat.execute(m_xctxt, node, k1.m_namespaceContext);

    		double d;

    		if (k1.m_treatAsNumbers)
    		{
    			d = r.num();

    			// Can't use NaN for compare. They are never equal. Use zero instead.  
    			m_key1Value = new Double(d);
    		}
    		else
    		{
    			m_key1Value = k1.m_col.getCollationKey(XslTransformEvaluationHelper.getStrVal(r));
    		}

    		if (r.getType() == XObject.CLASS_NODESET)
    		{
    			// %REVIEW%
    			DTMCursorIterator ni = ((XMLNodeCursorImpl)r).iterRaw();
    			int current = ni.getCurrentNode();
    			if(DTM.NULL == current)
    				current = ni.nextNode();

    			// if (ni instanceof ContextNodeList) // %REVIEW%
    			// tryNextKey = (DTM.NULL != current);

    			// else abdicate... should never happen, but... -sb
    		}

    		if (m_keys.size() > 1)
    		{
    			NodeSortKey k2 = (NodeSortKey) m_keys.elementAt(1);

    			XObject r2 = k2.m_selectPat.execute(m_xctxt, node,
    					k2.m_namespaceContext);

    			if (k2.m_treatAsNumbers) {
    				d = r2.num();
    				m_key2Value = new Double(d);
    			} else {
    				String str2 = XslTransformEvaluationHelper.getStrVal(r2);
    				m_key2Value = k2.m_col.getCollationKey(str2);
    			}
    		}        
    	}  // end if not empty    
    }
    
    /**
     * Constructor NodeCompareElem.
     * 
     * @param xObj              Current xdm item
     * 
     * @throws javax.xml.transform.TransformerException
     */
    NodeCompareElem(XObject xObj) throws javax.xml.transform.TransformerException
    {
    	m_xobj = xObj;

    	if (!m_keys.isEmpty())
    	{
    		NodeSortKey k1 = (NodeSortKey) m_keys.elementAt(0);
    		
    		XObject prevXctxtItem = m_xctxt.getXPath3ContextItem();    		
    		XObject r = null;
    		
    		try {
    			m_xctxt.setXPath3ContextItem(xObj);    		
    			r = k1.m_selectPat.execute(m_xctxt, DTM.NULL, k1.m_namespaceContext);
    		}
    		finally {    		
    			m_xctxt.setXPath3ContextItem(prevXctxtItem);
    		}

    		double d = 0.0;

    		if (k1.m_treatAsNumbers)
    		{    			
    			String str1 = XslTransformEvaluationHelper.getStrVal(r);
    			try {
    			   d = (Double.valueOf(str1)).doubleValue();
    			}
    			catch (NumberFormatException ex) {
    			   throw new javax.xml.transform.TransformerException("XPTY0004 : The sort key value " + str1 + " is not numeric.");
    			}

    			// Can't use NaN for compare. They are never equal. Use zero instead.  
    			m_key1Value = new Double(d);
    		}
    		else
    		{
    			m_key1Value = k1.m_col.getCollationKey(XslTransformEvaluationHelper.getStrVal(r));
    		}

    		if (r.getType() == XObject.CLASS_NODESET)
    		{
    			DTMCursorIterator ni = ((XMLNodeCursorImpl)r).iterRaw();
    			int current = ni.getCurrentNode();
    			if(DTM.NULL == current)
    				current = ni.nextNode();
    		}

    		if (m_keys.size() > 1)
    		{
    			NodeSortKey k2 = (NodeSortKey) m_keys.elementAt(1);

    			m_xctxt.setXPath3ContextItem(xObj);
    			XObject r2 = null;
    			
    			try {
    				r2 = k2.m_selectPat.execute(m_xctxt, DTM.NULL, k2.m_namespaceContext);
    			}
    			finally {
    				m_xctxt.setXPath3ContextItem(prevXctxtItem);
    			}

    			if (k2.m_treatAsNumbers) {    				
    				String str1 = XslTransformEvaluationHelper.getStrVal(r2);

    				try {
    					d = (Double.valueOf(str1)).doubleValue();
    				}
    				catch (NumberFormatException ex) {
    					throw new javax.xml.transform.TransformerException("XPTY0004 : The sort key value " + str1 + " is not numeric.");
    				}

    				m_key2Value = new Double(d);
    			} else {
    				String str2 = XslTransformEvaluationHelper.getStrVal(r2);
    				m_key2Value = k2.m_col.getCollationKey(str2);
    			}
    		}        
    	}  // end if not empty
    }
    
  }  // end NodeCompareElem class
}
