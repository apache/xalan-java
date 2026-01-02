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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.xml.transform.TransformerException;

import org.apache.xalan.templates.ElemForEachGroup;
import org.apache.xalan.templates.GroupingKeyAndGroupPair;
import org.apache.xml.dtm.DTM;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;

/**
 * An instance of this class, shall sort xsl:for-each-group's groups, 
 * according to xsl:sort elements contained within xsl:for-each-group
 * instruction.
 * 
 *  An implementation of this class, follows design of xsl:for-each 
 *  instruction's xsl:sort element.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class ForEachGroupXslSortSorter
{

  /** Reference to the current xsl:for-each-group element */
  private ElemForEachGroup m_elemForEachGroup;
  
  /** Current XPath context */
  private XPathContext m_xctxt;

  /** Vector of NodeSortKeys */
  private Vector m_keys;

  
  /**
   * Class constructor.
   *
   * @param xctxt                     An XPath context object
   * 
   * @param elemForEachGroup          An xsl:for-each-group instruction's object
   *                                  reference.
   */
  public ForEachGroupXslSortSorter(XPathContext xctxt, ElemForEachGroup elemForEachGroup)
  {
      m_xctxt = xctxt;
      m_elemForEachGroup = elemForEachGroup; 
  }

  /**
   * Given an object representing xsl:for-each-group groups to be sorted, sort these groups 
   * according to the criteria represented by the xsl:sort element(s).
   * 
   * @param forEachGroups                   Reference of groups to be sorted
   * @param sortKeys                        Vector of sort key objects
   * @param xctxt                           An XPath context object
   *
   * @throws javax.xml.transform.TransformerException
   */
  public List<GroupingKeyAndGroupPair> sort(Object forEachGroups, Vector sortKeys, XPathContext xctxt)
                                                    throws javax.xml.transform.TransformerException {
      
        List<GroupingKeyAndGroupPair> groupingKeyAndGroupPairList = new ArrayList<GroupingKeyAndGroupPair>();

        m_keys = sortKeys;
        
        if (forEachGroups instanceof Map) {
            Map<Object, List<Integer>> xslForEachGroupMap = (Map<Object, List<Integer>>)forEachGroups;
            
            Set<Object> groupingKeys = xslForEachGroupMap.keySet();
            for (Iterator<Object> groupingKeysIter = groupingKeys.iterator(); 
                                                            groupingKeysIter.hasNext(); ) {
                Object groupingKey = groupingKeysIter.next();
                List<Integer> groupNodesDtmHandles = xslForEachGroupMap.get(groupingKey);
                GroupingKeyAndGroupPair groupingKeyAndGroupPair = new GroupingKeyAndGroupPair(
                                                                           groupingKey, groupNodesDtmHandles);
                groupingKeyAndGroupPairList.add(groupingKeyAndGroupPair);
             } 
        }
        else {
            List<List<Integer>> xslForEachGroupStartingWithEndingWith = (ArrayList<List<Integer>>)forEachGroups;
            
            int size1 = xslForEachGroupStartingWithEndingWith.size();            
            for (int idx = 0; idx < size1; idx++) {
                Object groupingKey = null;
                List<Integer> groupNodesDtmHandles = xslForEachGroupStartingWithEndingWith.get(idx);
                                
                GroupingKeyAndGroupPair groupingKeyAndGroupPair = new GroupingKeyAndGroupPair(
                                                                                 groupingKey, groupNodesDtmHandles);
                groupingKeyAndGroupPairList.add(groupingKeyAndGroupPair);
            }
        }
    
        int numberOfGroups = groupingKeyAndGroupPairList.size();
        
        // Populate a vector of node compare elements, based on an input list of 
        // xsl:for-each-group groups.
        Vector nodeCompareElements = new Vector();
    
        for (int idx = 0; idx < numberOfGroups; idx++) {
            XslForEachGroupNodeCompareElem elem = new XslForEachGroupNodeCompareElem(m_elemForEachGroup, 
            		                                                                 groupingKeyAndGroupPairList.get(idx),
            		                                                                 m_keys,
            		                                                                 xctxt);
    
            nodeCompareElements.addElement(elem);
        }
    
        Vector scratchVector = new Vector();
    
        mergesort(nodeCompareElements, scratchVector, 0, numberOfGroups - 1, xctxt);
        
        groupingKeyAndGroupPairList.clear();
        
        int size2 = nodeCompareElements.size();
        for (int idx = 0; idx < size2; idx++)
        {
            GroupingKeyAndGroupPair groupingKeyAndGroupPair = ((XslForEachGroupNodeCompareElem)nodeCompareElements.get(idx)).
                                                                        getGroupingKeyAndGroupPair();
            groupingKeyAndGroupPairList.add(groupingKeyAndGroupPair);
        }
        
        return groupingKeyAndGroupPairList;
        
  }

  /**
   * Return the results of a compare of two xsl:for-each-group groups.
   *
   * @param n1                    first node to use for compare
   * @param n2                    second node to use for compare
   * @param kIndex                index of NodeSortKey to use for sort
   * @param xctxt                 An XPath context object
   *
   * @return                      The result of comparing relative sort order between 
   *                              two xsl:for-each-group groups.
   *
   * @throws TransformerException
   */
  private int compare(XslForEachGroupNodeCompareElem n1, XslForEachGroupNodeCompareElem n2, int kIndex, 
                                                                                      XPathContext xctxt) throws TransformerException {

    int result = 0;
    
    int n1ContextNode = ((n1.getGroupingKeyAndGroupPair()).getGroupNodeDtmHandles()).get(0);
    
    int n2ContextNode = ((n2.getGroupingKeyAndGroupPair()).getGroupNodeDtmHandles()).get(0);
    
    NodeSortKey k = (NodeSortKey) m_keys.elementAt(kIndex);

    if (k.m_treatAsNumbers) {
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
        // get values dynamically
        XObject r1 = k.m_selectPat.execute(m_xctxt, n1ContextNode,
                                           k.m_namespaceContext);
        XObject r2 = k.m_selectPat.execute(m_xctxt, n2ContextNode,
                                           k.m_namespaceContext);
        n1Num = r1.num();
        
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
    else {
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
        // get values dynamically
        XObject r1 = k.m_selectPat.execute(m_xctxt, n1ContextNode,
                                           k.m_namespaceContext);
        XObject r2 = k.m_selectPat.execute(m_xctxt, n2ContextNode,
                                           k.m_namespaceContext);

        n1String = k.m_col.getCollationKey(r1.str());
        n2String = k.m_col.getCollationKey(r2.str());
      }

      result = n1String.compareTo(n2String);

      // process caseOrder parameter
      if (k.m_caseOrderUpper)
      {
        String tempN1 = n1String.getSourceString().toLowerCase();
        String tempN2 = n2String.getSourceString().toLowerCase();

        if (tempN1.equals(tempN2))
        {
          // java defaults to upper case is greater.
          result = result == 0 ? 0 : -result;
        }
      }

      // process order parameter
      if (k.m_descending)
      {
        result = -result;
      }
    }  //end else

    if (0 == result) {
      if ((kIndex + 1) < m_keys.size())
      {
        result = compare(n1, n2, kIndex + 1, xctxt);
      }
    }

    if (0 == result) {
        DTM dtm = xctxt.getDTM(n1ContextNode);
        result = dtm.isNodeAfter(n1ContextNode, n2ContextNode) ? -1 : 1;
    }

    return result;
    
  }

  /**
   * This method implements a standard mergesort algorithm.
   *
   * @param   a                  first vector of nodes to compare
   * @param   b                  second vector of  nodes to compare 
   * @param   l                  left boundary of  partition
   * @param   r                  right boundary of  partition
   * @param   xpathContext       XPath context to use
   *
   * @throws TransformerException
   */
  private void mergesort(Vector a, Vector b, int l, int r, XPathContext xpathContext)
                                                         throws TransformerException {

        if ((r - l) > 0) {
              int m = (r + l) / 2;
        
              mergesort(a, b, l, m, xpathContext);
              mergesort(a, b, m + 1, r, xpathContext);
        
              int i, j, k;
        
              for (i = m; i >= l; i--) {
                  // increment vector size if needed
                  if (i >= b.size())
                     b.insertElementAt(a.elementAt(i), i);
                  else
                     b.setElementAt(a.elementAt(i), i);
              }
        
              i = l;
        
              for (j = (m + 1); j <= r; j++) {
                if (r + m + 1 - j >= b.size())
                   b.insertElementAt(a.elementAt(j), r + m + 1 - j);
                else
                   b.setElementAt(a.elementAt(j), r + m + 1 - j);
              }
        
              j = r;
        
              int compVal;
        
              for (k = l; k <= r; k++) {
                  if (i == j)
                     compVal = -1;
                  else
                     compVal = compare((XslForEachGroupNodeCompareElem) b.elementAt(i),
                                           (XslForEachGroupNodeCompareElem) b.elementAt(j), 0, xpathContext);
        
                  if (compVal < 0) {
                     a.setElementAt(b.elementAt(i), k);        
                     i++;
                  }
                  else if (compVal > 0) {
                     a.setElementAt(b.elementAt(j), k);        
                     j--;
                  }
              }
        }
  }
  
}
