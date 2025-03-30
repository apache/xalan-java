/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the  "License");
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
/*
 * $Id$
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
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XObject;

/**
 * An instance of this class, shall sort xsl:for-each-group groups, according to 
 * xsl:sort element(s) within xsl:for-each-group element.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class ForEachGroupXslSortSorter
{

  /** Reference to the current xsl:for-each-group element */
  ElemForEachGroup m_elemForEachGroup;
  
  /** Current XPath context */
  XPathContext m_execContext;

  /** Vector of NodeSortKeys */
  Vector m_keys;

  /**
   * Constructor for this class.
   *
   * @param xpathContext       XPath context to use
   * 
   * @param elemForEachGroup   reference to current xsl:for-each-group element
   */
  public ForEachGroupXslSortSorter(XPathContext xpathContext, ElemForEachGroup elemForEachGroup)
  {
      m_execContext = xpathContext;
      m_elemForEachGroup = elemForEachGroup; 
  }

  /**
   * Given an object representing xsl:for-each-group groups to be sorted, sort these groups 
   * according to the criteria represented by the xsl:sort element(s).
   * 
   * @param forEachGroups     groups to be sorted.
   * @param sortKeys          a vector of NodeSortKeys.
   * @param xpathContext      XPath context to use
   *
   * @throws javax.xml.transform.TransformerException
   */
  public List<GroupingKeyAndGroupPair> sort(Object forEachGroups, Vector sortKeys, XPathContext xpathContext)
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
            
            for (int idx = 0; idx < xslForEachGroupStartingWithEndingWith.size(); idx++) {
                Object groupingKey = null;
                List<Integer> groupNodesDtmHandles = xslForEachGroupStartingWithEndingWith.get(idx);
                                
                GroupingKeyAndGroupPair groupingKeyAndGroupPair = new GroupingKeyAndGroupPair(
                                                                                 groupingKey, groupNodesDtmHandles);
                groupingKeyAndGroupPairList.add(groupingKeyAndGroupPair);
            }
        }
    
        int numberOfGroups = groupingKeyAndGroupPairList.size();
        
        // populate a vector of node compare elements, based on an input list of 
        // xsl:for-each-group groups.
        Vector nodeCompareElements = new Vector();
    
        for (int idx = 0; idx < numberOfGroups; idx++) {
            NodeCompareElem elem = new NodeCompareElem(groupingKeyAndGroupPairList.get(idx));
    
            nodeCompareElements.addElement(elem);
        }
    
        Vector scratchVector = new Vector();
    
        mergesort(nodeCompareElements, scratchVector, 0, numberOfGroups - 1, xpathContext);
        
        groupingKeyAndGroupPairList.clear();
        
        for (int idx = 0; idx < nodeCompareElements.size(); idx++)
        {
            GroupingKeyAndGroupPair groupingKeyAndGroupPair = ((NodeCompareElem)nodeCompareElements.get(idx)).
                                                                        getGroupingKeyAndGroupPair();
            groupingKeyAndGroupPairList.add(groupingKeyAndGroupPair);
        }
        
        return groupingKeyAndGroupPairList;
        
  }

  /**
   * Return the results of a compare of two xsl:for-each-group groups.
   *
   * @param n1              first node to use for compare
   * @param n2              second node to use for compare
   * @param kIndex          index of NodeSortKey to use for sort
   * @param xpathContext    XPath context to use
   *
   * @return    The result of comparing two xsl:for-each-group groups, for the purpose of sorting 
   *            with xsl:sort element(s).
   *
   * @throws TransformerException
   */
  int compare(NodeCompareElem n1, NodeCompareElem n2, int kIndex, 
                                        XPathContext xpathContext) throws TransformerException {

    int result = 0;
    
    int n1ContextNode = ((n1.getGroupingKeyAndGroupPair()).getGroupNodesDtmHandles()).get(0);
    
    int n2ContextNode = ((n2.getGroupingKeyAndGroupPair()).getGroupNodesDtmHandles()).get(0);
    
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
        XObject r1 = k.m_selectPat.execute(m_execContext, n1ContextNode,
                                           k.m_namespaceContext);
        XObject r2 = k.m_selectPat.execute(m_execContext, n2ContextNode,
                                           k.m_namespaceContext);
        n1Num = r1.num();
        
        n2Num = r2.num();
      }

      if ((n1Num == n2Num) && ((kIndex + 1) < m_keys.size()))
      {
        result = compare(n1, n2, kIndex + 1, xpathContext);
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
        XObject r1 = k.m_selectPat.execute(m_execContext, n1ContextNode,
                                           k.m_namespaceContext);
        XObject r2 = k.m_selectPat.execute(m_execContext, n2ContextNode,
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
        result = compare(n1, n2, kIndex + 1, xpathContext);
      }
    }

    if (0 == result) {
        DTM dtm = xpathContext.getDTM(n1ContextNode);
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
  void mergesort(Vector a, Vector b, int l, int r, XPathContext xpathContext)
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
                     compVal = compare((NodeCompareElem) b.elementAt(i),
                                           (NodeCompareElem) b.elementAt(j), 0, xpathContext);
        
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

  /**
   * This class stores the value(s) produced by evaluating the given
   * xsl:for-each-group group with the sort key(s).
   *  
   * @xsl.usage internal
   */
  class NodeCompareElem {
    
        /** This maxkey value was chosen arbitrarily. We're assuming that the    
            maxkey + 1 keys will only occur fairly rarely and therefore, we will 
            get the node values for those keys dynamically.
        */
        int maxkey = 2;
    
        /** Value from first sort key */
        Object m_key1Value;
    
        /** Value from second sort key */
        Object m_key2Value;
        
        GroupingKeyAndGroupPair m_groupingKeyAndGroupPair;

        /**
         * Constructor function, of this class.
         *
         * @param groupingKeyAndGroupPair    current GroupingKeyAndGroupPair object
         *
         * @throws javax.xml.transform.TransformerException
         */
        NodeCompareElem(GroupingKeyAndGroupPair groupingKeyAndGroupPair) throws 
                                                               javax.xml.transform.TransformerException {
            
              this.m_groupingKeyAndGroupPair = groupingKeyAndGroupPair; 
            
              int contextNodeDtmHandle = ((groupingKeyAndGroupPair.getGroupNodesDtmHandles()).
                                                                                      get(0)).intValue();
              
              TransformerImpl transformer = (TransformerImpl) m_execContext.getOwnerObject();
              m_elemForEachGroup.setGroupingKey(groupingKeyAndGroupPair.getGroupingKey());
              m_elemForEachGroup.setGroupNodesDtmHandles(groupingKeyAndGroupPair.getGroupNodesDtmHandles());
              transformer.setCurrentElement(m_elemForEachGroup);
              
              // for the xsl:sort element select expression's evaluation, the context item is the 
              // initial item of the group.
              m_execContext.pushCurrentNode(contextNodeDtmHandle);
              
              if (!m_keys.isEmpty()) {
                    NodeSortKey k1 = (NodeSortKey) m_keys.elementAt(0);
                    XObject r = (k1.m_selectPat).execute(m_execContext, contextNodeDtmHandle,
                                                                                k1.m_namespaceContext);
            
                    double d;
            
                    if (k1.m_treatAsNumbers) {
                          d = r.num();
                          m_key1Value = new Double(d);
                    }
                    else {
                          m_key1Value = k1.m_col.getCollationKey(r.str());
                    }
            
                    if (r.getType() == XObject.CLASS_NODESET) {
                          DTMCursorIterator ni = ((XMLNodeCursorImpl)r).iterRaw();
                          int current = ni.getCurrentNode();
                          if(DTM.NULL == current) {
                             current = ni.nextNode();
                          }
                    }
            
                    if (m_keys.size() > 1) {
                          NodeSortKey k2 = (NodeSortKey) m_keys.elementAt(1);
                
                          XObject r2 = (k2.m_selectPat).execute(m_execContext, contextNodeDtmHandle,
                                                              k2.m_namespaceContext);
                
                          if (k2.m_treatAsNumbers) {
                              d = r2.num();
                              m_key2Value = new Double(d);
                          } else {
                              m_key2Value = k2.m_col.getCollationKey(r2.str());
                          }
                    }
              }   
        }

        public GroupingKeyAndGroupPair getGroupingKeyAndGroupPair() {
            return m_groupingKeyAndGroupPair;
        }

        public void setGroupingKeyAndGroupPair(GroupingKeyAndGroupPair 
                                                             groupingKeyAndGroupPair) {
            this.m_groupingKeyAndGroupPair = groupingKeyAndGroupPair;
        }
    
  }
  
}
