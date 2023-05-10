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
package org.apache.xalan.templates;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.xml.transform.TransformerException;

import org.apache.xalan.transformer.NodeSorter;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;

/**
 *  XSLT 3.0 for-each-group element.
 *    
 *  <xsl:for-each-group
              select = expression
              group-by? = expression
              group-adjacent? = expression
              group-starting-with? = pattern
              group-ending-with? = pattern
              composite? = boolean
              collation? = { uri } >
        <!-- Content: (xsl:sort*, sequence-constructor) -->
    </xsl:for-each-group>
    
    <xsl:sort
           select? = expression
           lang? = { language }
           order? = { "ascending" | "descending" }
           collation? = { uri }
           stable? = { boolean }
           case-order? = { "upper-first" | "lower-first" }
           data-type? = { "text" | "number" | eqname } >
        <!-- Content: sequence-constructor -->
     </xsl:sort>
     
     There are following two XSLT 3.0 grouping functions,
     1) fn:current-group()     
     2) fn:current-grouping-key()     
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */

/* 
   Implementation of the XSLT 3.0 xsl:for-each-group instruction.
 
   The xsl:for-each-group's grouping attributes are as described below (and are further defined
   within XSLT 3.0 spec),
   
   The xsl:for-each-group element can have following grouping attributes : "group-by", "group-adjacent", 
   "group-starting-with", "group-ending-with".  
   
   These xsl:for-each-group attributes are mutually exclusive. i.e one of these is required and two 
   or more of these is an error within the XSLT stylesheet. If the xsl:for-each-group element violates
   this condition, an error XTSE1080 is raised.  
*/
public class ElemForEachGroup extends ElemTemplateElement 
                                                implements ExpressionOwner
{
  
  private static final long serialVersionUID = -6682554013978812260L;
  
  /**
   * Construct a element representing xsl:for-each-group.
   */
  public ElemForEachGroup(){}

  /**
   * The "select" expression.
   * @serial
   */
  protected Expression m_selectExpression = null;
  
  /**
   * The "group-by" expression.
   */
  protected Expression m_GroupByExpression = null;
  
  /**
   * The "group-adjacent" expression.
   */
  protected Expression m_GroupAdjacentExpression = null;
  
  /**
   * The "group-starting-with" expression.
   */
  protected Expression m_GroupStartingWithExpression = null;
  
  /**
   * The "group-ending-with" expression.
   */
  protected Expression m_GroupEndingWithExpression = null;
  
  /**
   * Vector containing the xsl:sort elements associated with this element.
   */
  protected Vector m_sortElems = null;

  /**
   * Set the "select" attribute.
   *
   * @param xpath The XPath expression for the "select" attribute.
   */
  public void setSelect(XPath xpath)
  {
      m_selectExpression = xpath.getExpression();  
  }

  /**
   * Get the "select" attribute.
   *
   * @return The XPath expression for the "select" attribute.
   */
  public Expression getSelect()
  {
      return m_selectExpression;
  }
  
  public void setGroupBy(XPath xpath)
  {
      m_GroupByExpression = xpath.getExpression();   
  }
  
  public Expression getGroupBy()
  {
      return m_GroupByExpression;
  }
  
  public void setGroupAdjacent(XPath xpath)
  {
      m_GroupAdjacentExpression = xpath.getExpression();   
  }
  
  public Expression getGroupAdjacent()
  {
      return m_GroupAdjacentExpression;
  }
  
  public void setGroupStartingWith(XPath xpath)
  {
      m_GroupStartingWithExpression = xpath.getExpression();   
  }
  
  public Expression getGroupStartingWith()
  {
      return m_GroupStartingWithExpression;
  }
  
  public void setGroupEndingWith(XPath xpath)
  {
      m_GroupEndingWithExpression = xpath.getExpression();   
  }
  
  public Expression getGroupEndingWith()
  {
      return m_GroupEndingWithExpression;
  }

  /**
   * This function is called after everything else has been
   * recomposed, and allows the template to set remaining
   * values that may be based on some other property that
   * depends on recomposition.
   *
   * @param sroot
   *
   * @throws TransformerException
   */
  public void compose(StylesheetRoot sroot) throws TransformerException
  {
        super.compose(sroot);
    
        int length = getSortElemCount();
    
        for (int i = 0; i < length; i++)
        {
            getSortElem(i).compose(sroot);
        }
    
        java.util.Vector vnames = sroot.getComposeState().getVariableNames();
    
        if (m_selectExpression != null) {
            m_selectExpression.fixupVariables(vnames, sroot.getComposeState().getGlobalsSize());
        } else {
            m_selectExpression = getStylesheetRoot().m_selectDefault.getExpression();
        }
  }
  
  /**
   * This after the template's children have been composed.
   */
  public void endCompose(StylesheetRoot sroot) throws TransformerException
  {
        int length = getSortElemCount();
    
        for (int i = 0; i < length; i++)
        {
            getSortElem(i).endCompose(sroot);
        }
        
        super.endCompose(sroot);
  }

  /**
   * Get the count xsl:sort elements associated with this element.
   * 
   * @return The number of xsl:sort elements.
   */
  public int getSortElemCount()
  {
      return (m_sortElems == null) ? 0 : m_sortElems.size();
  }

  /**
   * Get a xsl:sort element associated with this element.
   *
   * @param i Index of xsl:sort element to get
   *
   * @return xsl:sort element at given index
   */
  public ElemSort getSortElem(int i)
  {
      return (ElemSort) m_sortElems.elementAt(i);
  }

  /**
   * Set a xsl:sort element associated with this element.
   *
   * @param sortElem xsl:sort element to set
   */
  public void setSortElem(ElemSort sortElem)
  {
      if (null == m_sortElems)
         m_sortElems = new Vector();
    
      m_sortElems.addElement(sortElem);
  }

  /**
   * Get an int constant identifying the type of element.
   * @see org.apache.xalan.templates.Constants
   *
   * @return The token ID for this element
   */
  public int getXSLToken()
  {
      return Constants.ELEMNAME_FOREACHGROUP;
  }

  /**
   * Return the node name.
   *
   * @return The element's name
   */
  public String getNodeName()
  {
      return Constants.ELEMNAME_FOREACHGROUP_STRING;
  }

  /**
   * Execute the xsl:for-each-group transformation
   *
   * @param transformer non-null reference to the the current transform-time state.
   *
   * @throws TransformerException
   */
  public void execute(TransformerImpl transformer) throws TransformerException
  {
        transformer.pushCurrentTemplateRuleIsNull(true);    
        if (transformer.getDebug())
           transformer.getTraceManager().fireTraceEvent(this);
    
        try
        {
            transformSelectedNodes(transformer);
        }
        finally
        {
            if (transformer.getDebug())
    	       transformer.getTraceManager().fireTraceEndEvent(this); 
            transformer.popCurrentTemplateRuleIsNull();
        }
  }

  /**
   * Get template element associated with this
   *
   *
   * @return template element associated with this (itself)
   */
  protected ElemTemplateElement getTemplateMatch()
  {
      return this;
  }

  /**
   * Sort given nodes
   *
   *
   * @param xctxt The XPath runtime state for the sort.
   * @param keys Vector of sort keys
   * @param sourceNodes Iterator of nodes to sort
   *
   * @return iterator of sorted nodes
   *
   * @throws TransformerException
   */
  public DTMIterator sortNodes(XPathContext xctxt, Vector keys, DTMIterator sourceNodes)
                                                                     throws TransformerException {
        NodeSorter sorter = new NodeSorter(xctxt);
        sourceNodes.setShouldCacheNodes(true);
        sourceNodes.runTo(-1);
        xctxt.pushContextNodeList(sourceNodes);
    
        try
        {
            sorter.sort(sourceNodes, keys, xctxt);
            sourceNodes.setCurrentPos(0);
        }
        finally
        {
            xctxt.popContextNodeList();
        }
    
        return sourceNodes;
  }

  /**
   * Perform the actual XSLT transformation logic, on run-time contents of 
   * xsl:for-each-group element.
   *
   * @param transformer non-null reference to the the current transform-time state.
   *
   * @throws TransformerException Thrown in a variety of circumstances.
   * 
   * @xsl.usage advanced
   */
  public void transformSelectedNodes(TransformerImpl transformer)
                                              throws TransformerException {
      
        XPathContext xctxt = transformer.getXPathContext();
        
        int forEachGroupGroupingAttributesCount = getForEachGroupGroupingAttributesCount();
        
        if (forEachGroupGroupingAttributesCount == 0) {
            throw new TransformerException("XTSE1080 : None of the attributes 'group-by', 'group-adjacent', "
                                              + "'group-starting-with', 'group-ending-with' is present on "
                                              + "xsl:for-each-group element.", xctxt.getSAXLocator());     
        }
         
        if (forEachGroupGroupingAttributesCount > 1) {
           throw new TransformerException("XTSE1080 : Only one of the attributes 'group-by', 'group-adjacent', "
                                             + "'group-starting-with', 'group-ending-with' is allowed to be "
                                             + "present on xsl:for-each-group element.", xctxt.getSAXLocator());     
        }
        
        final int sourceNode = xctxt.getCurrentNode();
        DTMIterator sourceNodes = m_selectExpression.asIterator(xctxt, sourceNode);
        
        // start : form xsl:for-each-group raw groups data from the 'sourceNodes' iterator
        
        // grouping keys (only available when attributes 'group-by' or 'group-adjacent' are used),
        // can have data types as defined by XPath data model.
        
        // groups data is formed, within a hashmap (when attributes 'group-by' or 
        // 'group-adjacent' are used). 
        // hashmap item's key is the grouping key, and value of that hashmap item 
        // is a list of nodes dtm integer handles representing the group for that 
        // grouping key.        
        Map<Object, List<Integer>> xslForEachGroupMap = new HashMap<Object, List<Integer>>();
        
        // list to store groups formed for, either 'group-starting-with' or 'group-ending-with'
        List<List<Integer>> xslForEachGroupStartingWithEndingWith = new ArrayList<List<Integer>>();
        
        if (m_GroupByExpression != null) {
            int nextNode;
            
            while (DTM.NULL != (nextNode = sourceNodes.nextNode())) {
                xctxt.pushCurrentNode(nextNode);
                            
                XObject xpathEvalResult = m_GroupByExpression.execute(xctxt);                
                Object xpathRawResult = getXPathEvaluationRawResult(xpathEvalResult);
                                            
                if (xslForEachGroupMap.get(xpathRawResult) != null) {
                    List<Integer> group = xslForEachGroupMap.get(xpathRawResult);
                    group.add(nextNode);
                } else {
                    List<Integer> group = new ArrayList<Integer>();
                    group.add(nextNode);
                    xslForEachGroupMap.put(xpathRawResult, group);
                }            
             }
         }
         else if (m_GroupAdjacentExpression != null) {
             Object prevValue = null;
             int idx = 0;
             int nextNode;
             
             while (DTM.NULL != (nextNode = sourceNodes.nextNode())) {
                 xctxt.pushCurrentNode(nextNode);
                 
                 XObject xpathEvalResult = m_GroupAdjacentExpression.execute(xctxt);                 
                 Object xpathRawResult = getXPathEvaluationRawResult(xpathEvalResult);                 
                 Object currValue = xpathRawResult;
                 
                 if (idx == 0) {
                     // first node within the nodes been traversed, by this loop
                     List<Integer> group = new ArrayList<Integer>();
                     group.add(nextNode);
                     xslForEachGroupMap.put(currValue, group);
                     prevValue = currValue; 
                 }
                 else {
                    if (currValue.equals(prevValue)) {
                        List<Integer> group = xslForEachGroupMap.get(prevValue);
                        group.add(nextNode);
                        prevValue = currValue;
                    }
                    else {
                        List<Integer> group = new ArrayList<Integer>();
                        group.add(nextNode);
                        xslForEachGroupMap.put(currValue, group);
                        prevValue = currValue;
                    }
                 }
                 
                 idx++;
             }
         }
         else if (m_GroupStartingWithExpression != null) {
             int nextNode;
             
             while (DTM.NULL != (nextNode = sourceNodes.nextNode())) {
                xctxt.pushCurrentNode(nextNode);
                
                XObject xpathEvalResult = m_GroupStartingWithExpression.execute(xctxt);
                boolean patternEvalResult = xpathEvalResult.bool();
                if (patternEvalResult) {
                    List<Integer> group = new ArrayList<Integer>();
                    group.add(nextNode);
                    xslForEachGroupStartingWithEndingWith.add(group);
                }
                else {
                    int groupsCountSoFar = xslForEachGroupStartingWithEndingWith.size();
                    if (groupsCountSoFar > 0) {
                       List<Integer> group = xslForEachGroupStartingWithEndingWith.get(
                                                                            groupsCountSoFar - 1);
                       group.add(nextNode);
                    }
                }                
             }
         }
         else if (m_GroupEndingWithExpression != null) {                          
             int nextNode;
             
             List<Integer> nodesList = new ArrayList<Integer>();
             while (DTM.NULL != (nextNode = sourceNodes.nextNode())) {
                nodesList.add(Integer.valueOf(nextNode));   
             }
             
             Collections.reverse(nodesList);
             
             for (int idx = 0; idx < nodesList.size(); idx++) {
                 xctxt.pushCurrentNode((nodesList.get(idx)).intValue());
                 
                 XObject xpathEvalResult = m_GroupEndingWithExpression.execute(xctxt);
                 boolean patternEvalResult = xpathEvalResult.bool();
                 if (patternEvalResult) {
                     List<Integer> group = new ArrayList<Integer>();
                     group.add((nodesList.get(idx)).intValue());
                     xslForEachGroupStartingWithEndingWith.add(group);
                 }
                 else {
                     int groupsCountSoFar = xslForEachGroupStartingWithEndingWith.size();
                     if (groupsCountSoFar > 0) {
                        List<Integer> group = xslForEachGroupStartingWithEndingWith.get(
                                                                             groupsCountSoFar - 1);
                        group.add((nodesList.get(idx)).intValue());
                     }
                 }
              }
             
              Collections.reverse(xslForEachGroupStartingWithEndingWith);
              
              for (int idx = 0; idx < xslForEachGroupStartingWithEndingWith.size(); idx++) {
                  List<Integer> group = xslForEachGroupStartingWithEndingWith.get(idx);
                  Collections.reverse(group);
              }
         }
        
         // end : form xsl:for-each-group raw groups data from the 'sourceNodes' iterator
        
         // Reorder xsl:for-each-group raw groups content known as 'order of first appearance' 
         // as defined by XSLT 3.0 spec (this is the default XSLT transformation xsl:for-each-group 
         // element's sorted output order in absence of xsl:for-each-group's xsl:sort elements).         
         // The groups reordering logic performed below, is only applicable when attributes 
         // 'group-by' or 'group-adjacent' are used.
         List<GroupingKeyAndNodeHandlePair> groupingKeyAndNodeHandlePairList = new ArrayList
                                                                   <GroupingKeyAndNodeHandlePair>();        
         if (xslForEachGroupMap.size() > 0) {                                                                    
            Set<Object> groupingKeys = xslForEachGroupMap.keySet();
            for (Iterator<Object> groupingKeysIter = groupingKeys.iterator(); 
                                                            groupingKeysIter.hasNext(); ) {
                Object groupingKey = groupingKeysIter.next();
                List<Integer> groupNodesDtmHandles = xslForEachGroupMap.get(groupingKey);
                Integer groupContentsFirstItemNodeHandle =  groupNodesDtmHandles.get(0);
                GroupingKeyAndNodeHandlePair groupingKeyNodeHandlePair = new GroupingKeyAndNodeHandlePair(
                                                                groupingKey, groupContentsFirstItemNodeHandle);
                groupingKeyAndNodeHandlePairList.add(groupingKeyNodeHandlePair);
             }            
         }
        
         try {            
            xctxt.pushCurrentNode(DTM.NULL);
            xctxt.pushCurrentExpressionNode(DTM.NULL);
      
            xctxt.pushSAXLocatorNull();
            xctxt.pushContextNodeList(sourceNodes);
            transformer.pushElemTemplateElement(null);
                        
            // sort the xsl:for-each-group's groups. This is only applicable when, xsl:for-each-group 
            // attributes 'group-by' or 'group-adjacent' are used. 
            Collections.sort(groupingKeyAndNodeHandlePairList);
            
            // loop through all the, groups formed by xsl:for-each-group instruction,
            // and process the XSLT contents of each group. This is done when, 
            // attributes 'group-by' or 'group-adjacent' are used.
            for (int idx = 0; idx < groupingKeyAndNodeHandlePairList.size(); idx++) {
               GroupingKeyAndNodeHandlePair groupingKeyNodeHandlePair = groupingKeyAndNodeHandlePairList.get(idx);
               
               Object groupingKey = groupingKeyNodeHandlePair.getGroupingKey();  // current-grouping-key() value, for this group
               List<Integer> groupNodesDtmHandles = xslForEachGroupMap.get(groupingKey);  // current-group() contents, for this group                              
               
               for (ElemTemplateElement templateElem = this.m_firstChild; templateElem != null; 
                                                templateElem = templateElem.m_nextSibling) {
                   // set context item for whole of group contents evaluation, to the initial item of the group
                   xctxt.pushCurrentNode((groupNodesDtmHandles.get(0)).intValue());
                   
                   templateElem.setGroupingKey(groupingKey);
                   templateElem.setGroupNodesDtmHandles(groupNodesDtmHandles);
                   xctxt.setSAXLocator(templateElem);
                   transformer.setCurrentElement(templateElem);                   
                   templateElem.execute(transformer);
               }
            }
            
            // loop through all the, groups formed by xsl:for-each-group instruction,
            // and process the XSLT contents of each group.
            // This is done when, attributes 'group-starting-with' or 'group-ending-with' 
            // are used.            
            for (int idx = 0; idx < xslForEachGroupStartingWithEndingWith.size(); idx++) {
               List<Integer> groupNodesDtmHandles = xslForEachGroupStartingWithEndingWith.get(idx);
               for (ElemTemplateElement templateElem = this.m_firstChild; templateElem != null; 
                                                                  templateElem = templateElem.m_nextSibling) {                   
                   // set context item for whole of group contents evaluation, to the initial item of the group
                   xctxt.pushCurrentNode((groupNodesDtmHandles.get(0)).intValue());
                   
                   // grouping key is absent when, attributes 'group-starting-with' or 'group-ending-with' 
                   // are used.                   
                   templateElem.setGroupNodesDtmHandles(groupNodesDtmHandles);
                   xctxt.setSAXLocator(templateElem);
                   transformer.setCurrentElement(templateElem);                   
                   templateElem.execute(transformer);
                }
            }
         }
         finally
         {
              if (transformer.getDebug()) {
                  transformer.getTraceManager().fireSelectedEndEvent(sourceNode, this,
                                   "select", new XPath(m_selectExpression),
                                    new org.apache.xpath.objects.XNodeSet(sourceNodes));
              }
        
              xctxt.popSAXLocator();
              xctxt.popContextNodeList();
              transformer.popElemTemplateElement();
              xctxt.popCurrentExpressionNode();
              xctxt.popCurrentNode();
              sourceNodes.detach();
         }
        
         // when a particular xsl:for-each-group's evaluation has completed, set the XPath evaluation 
         // context node to the node which was the context node before xsl:for-each-group evaluation 
         // was started. 
         xctxt.pushCurrentNode(sourceNode);
  }

  /**
   * Add a child to the child list.
   * <!ELEMENT xsl:apply-templates (xsl:sort|xsl:with-param)*>
   * <!ATTLIST xsl:apply-templates
   *   select %expr; "node()"
   *   mode %qname; #IMPLIED
   * >
   *
   * @param newChild Child to add to child list
   *
   * @return Child just added to child list
   */
  public ElemTemplateElement appendChild(ElemTemplateElement newChild)
  {
        int type = ((ElemTemplateElement) newChild).getXSLToken();
    
        if (type == Constants.ELEMNAME_SORT)
        {
            setSortElem((ElemSort) newChild);
        
            return newChild;
        }
        else {
            return super.appendChild(newChild);
        }
  }
  
  /**
   * Call the children visitors.
   * @param visitor The visitor whose appropriate method will be called.
   */
  public void callChildVisitors(XSLTVisitor visitor, boolean callAttributes)
  {
      	if(callAttributes && (null != m_selectExpression))
      		m_selectExpression.callVisitors(this, visitor);
      		
        int length = getSortElemCount();
    
        for (int i = 0; i < length; i++)
        {
           getSortElem(i).callVisitors(visitor);
        }
    
        super.callChildVisitors(visitor, callAttributes);
  }

  /**
   * @see ExpressionOwner#getExpression()
   */
  public Expression getExpression()
  {
      return m_selectExpression;
  }

  /**
   * @see ExpressionOwner#setExpression(Expression)
   */
  public void setExpression(Expression exp)
  {
      exp.exprSetParent(this);
      m_selectExpression = exp;
  }
  
  /*
   * Method to support, validating the presence and count of xsl:for-each-group
   * attributes "group-by", "group-adjacent", "group-starting-with", 
   * "group-ending-with". 
   */
  private int getForEachGroupGroupingAttributesCount() 
                                          throws TransformerException {

        int forEachGroupGroupingAttributesCount = 0;
        
        if (m_GroupByExpression != null) {
           forEachGroupGroupingAttributesCount++;    
        }
        
        if (m_GroupAdjacentExpression != null) {
           forEachGroupGroupingAttributesCount++;    
        }
        
        if (m_GroupStartingWithExpression != null) {
           forEachGroupGroupingAttributesCount++;    
        }
        
        if (m_GroupEndingWithExpression != null) {
           forEachGroupGroupingAttributesCount++;    
        }
        
        return forEachGroupGroupingAttributesCount;
  }
  
  /* 
   * For the purpose of, evaluating grouping key XPath expressions for xsl:for-each-group, 
   * the grouping keys are treated as of type String, Number or Boolean. Any other data 
   * type for grouping key is converted to a String value.
   */
  private Object getXPathEvaluationRawResult(XObject xpathEvalResult) {
      Object xpathRawResult = null;
      
      if (xpathEvalResult instanceof XString) {
          xpathRawResult = xpathEvalResult.str();    
      }
      else if (xpathEvalResult instanceof XNumber) {
          xpathRawResult = Double.valueOf(((XNumber)xpathEvalResult).num());  
      }
      else if (xpathEvalResult instanceof XBoolean) {
          xpathRawResult =  Boolean.valueOf(((XBoolean)xpathEvalResult).bool());     
      }
      else {
          // any other data type for grouping key, is treated as string
          xpathRawResult = xpathEvalResult.str();   
      }
      
      return xpathRawResult;
  }
  
  /* 
   * Class to support, reordering the xsl:for-each-group's groups as per definition of default 
   * sorted order (i.e, order of first appearance) when xsl:sort elements are not present within 
   * xsl:for-each-group.
  */
  class GroupingKeyAndNodeHandlePair implements Comparable<GroupingKeyAndNodeHandlePair> {
     
     private Object groupingKey;
     
     private Integer nodeHandle;
     
     public GroupingKeyAndNodeHandlePair(Object groupingKey, Integer nodeHandle) {
        this.groupingKey = groupingKey;
        this.nodeHandle = nodeHandle; 
     }
     
     public Object getGroupingKey() {
        return groupingKey;
     }

     public void setGroupingKey(Object groupingKey) {
        this.groupingKey = groupingKey;
     }

     public Integer getNodeHandle() {
        return nodeHandle;
     }

     public void setNodeHandle(Integer nodeHandle) {
        this.nodeHandle = nodeHandle;
     }

     @Override
     public int compareTo(GroupingKeyAndNodeHandlePair obj) {
        return this.getNodeHandle().compareTo(obj.getNodeHandle());
     }
    
  }

}
