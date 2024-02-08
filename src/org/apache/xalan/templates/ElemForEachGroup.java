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

import org.apache.xalan.transformer.ForEachGroupXslSortSorter;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.utils.NodeVector;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.axes.NodeSequence;
import org.apache.xpath.composite.ForExpr;
import org.apache.xpath.composite.SimpleSequenceConstructor;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;
import org.apache.xpath.operations.Variable;

import xml.xpath31.processor.types.XSAnyURI;
import xml.xpath31.processor.types.XSBoolean;
import xml.xpath31.processor.types.XSDate;
import xml.xpath31.processor.types.XSDateTime;
import xml.xpath31.processor.types.XSNumericType;
import xml.xpath31.processor.types.XSQName;
import xml.xpath31.processor.types.XSString;
import xml.xpath31.processor.types.XSTime;

/**
 * Implementation of the XSLT 3.0 xsl:for-each-group instruction.
 * 
 * Ref : https://www.w3.org/TR/xslt-30/#grouping
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 *  
 * @xsl.usage advanced
 */
public class ElemForEachGroup extends ElemTemplateElement 
                                                implements ExpressionOwner
{
  
  private static final long serialVersionUID = -6682554013978812260L;

  /**
   * The "select" expression.
   */
  private Expression m_selectExpression = null;
  
  /**
   * The "group-by" expression.
   */
  private Expression m_GroupByExpression = null;
  
  /**
   * The "group-adjacent" expression.
   */
  private Expression m_GroupAdjacentExpression = null;
  
  /**
   * The "group-starting-with" expression.
   */
  private Expression m_GroupStartingWithExpression = null;
  
  /**
   * The "group-ending-with" expression.
   */
  private Expression m_GroupEndingWithExpression = null;
  
  /**
   * Vector containing the xsl:sort elements associated with this element.
   */
  private Vector m_sortElems = null;
  
  /**
   * The class constructor.
   */
  public ElemForEachGroup() {}
  
  /**
   * Get the count of xsl:sort elements associated with this element.
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
   * @param i index of xsl:sort element to get
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
      if (m_sortElems == null)
         m_sortElems = new Vector();
    
      m_sortElems.addElement(sortElem);
  }

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
  
  /**
   * Set the "group-by" attribute.
   *
   * @param xpath The XPath expression for the "group-by" attribute.
   */
  public void setGroupBy(XPath xpath)
  {
      m_GroupByExpression = xpath.getExpression();   
  }
  
  /**
   * Get the "group-by" attribute.
   *
   * @return The XPath expression for the "group-by" attribute.
   */
  public Expression getGroupBy()
  {
      return m_GroupByExpression;
  }
  
  /**
   * Set the "group-adjacent" attribute.
   *
   * @param xpath The XPath expression for the "group-adjacent" attribute.
   */
  public void setGroupAdjacent(XPath xpath)
  {
      m_GroupAdjacentExpression = xpath.getExpression();   
  }
  
  /**
   * Get the "group-adjacent" attribute.
   *
   * @return The XPath expression for the "group-adjacent" attribute.
   */
  public Expression getGroupAdjacent()
  {
      return m_GroupAdjacentExpression;
  }
  
  /**
   * Set the "group-starting-with" attribute.
   *
   * @param xpath The XPath expression for the "group-starting-with" attribute.
   */
  public void setGroupStartingWith(XPath xpath)
  {
      m_GroupStartingWithExpression = xpath.getExpression();   
  }
  
  /**
   * Get the "group-starting-with" attribute.
   *
   * @return The XPath expression for the "group-starting-with" attribute.
   */
  public Expression getGroupStartingWith()
  {
      return m_GroupStartingWithExpression;
  }
  
  /**
   * Set the "group-ending-with" attribute.
   *
   * @param xpath The XPath expression for the "group-ending-with" attribute.
   */
  public void setGroupEndingWith(XPath xpath)
  {
      m_GroupEndingWithExpression = xpath.getExpression();   
  }
  
  /**
   * Get the "group-ending-with" attribute.
   *
   * @return The XPath expression for the "group-ending-with" attribute.
   */
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
   * This function is called after the template's children have been composed.
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
   * Execute the xsl:for-each-group transformation.
   *
   * @param transformer non-null reference to the the current transform-time state.
   *
   * @throws TransformerException
   */
  public void execute(TransformerImpl transformer) throws TransformerException
  {
        transformer.pushCurrentTemplateRuleIsNull(true);
        
        if (transformer.getDebug()) {
           transformer.getTraceManager().fireTraceEvent(this);
        }
    
        try {
            transformSelectedNodes(transformer);
        }
        finally {
            if (transformer.getDebug()) {
    	       transformer.getTraceManager().fireTraceEndEvent(this);
            }
            
            transformer.popCurrentTemplateRuleIsNull();
        }
  }
  
  /**
   * Sort the provided xsl:for-each-group groups.
   *
   *
   * @param xctxt             the XPath runtime state for the sort
   * @param sortKeys          vector of sort keys
   * @param forEachGroups     an object representing, groups to be sorted
   *
   * @return a list of sorted groups
   *
   * @throws TransformerException
   */
  public List<GroupingKeyAndGroupPair> sortGroups(XPathContext xctxt, Vector sortKeys, Object forEachGroups)
                                                            throws TransformerException {
        List<GroupingKeyAndGroupPair> sortedGroups = null;
      
        ForEachGroupXslSortSorter sorter = new ForEachGroupXslSortSorter(xctxt, this);
        
        sortedGroups = sorter.sort(forEachGroups, sortKeys, xctxt);
    
        return sortedGroups;
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
   * 
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
  
  /**
   * This method performs the actual XSLT transformation logic, on XSL contents of 
   * xsl:for-each-group element.
   *
   * @param transformer              non-null reference to the the current transform-time state.
   *
   * @throws TransformerException    thrown in a variety of circumstances.
   * 
   * @xsl.usage advanced
   */
  private void transformSelectedNodes(TransformerImpl transformer)
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
        
        DTMIterator sourceNodes = null;
        
        if (m_selectExpression instanceof Variable) {
            XObject xObj = ((Variable)m_selectExpression).execute(xctxt);
            
            if (xObj instanceof ResultSequence) {                              
               ResultSequence resultSeq = (ResultSequence)xObj;
               sourceNodes = getSourceNodesFromResultSequence(resultSeq, xctxt);                
            }
        }
        else if ((m_selectExpression instanceof SimpleSequenceConstructor) ||
                                                    (m_selectExpression instanceof ForExpr)) {
            XObject xObj = m_selectExpression.execute(xctxt);
            
            ResultSequence resultSeq = (ResultSequence)xObj;
            sourceNodes = getSourceNodesFromResultSequence(resultSeq, xctxt);
        }
        
        if (sourceNodes == null) {        
           sourceNodes = m_selectExpression.asIterator(xctxt, sourceNode);
        }
                      
        // A java.util.Map object to store groups formed for, either 'group-by' or 
        // 'group-adjacent' attributes. This map stores mappings between, grouping key
        // value and contents of the group corresponding to a grouping key value. 
        Map<Object, List<Integer>> xslForEachGroupMap = new HashMap<Object, List<Integer>>();
        
        // List to store groups formed for, either 'group-starting-with' or 'group-ending-with' 
        // attributes. The grouping keys are not available for, these kind of groups.
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
                     // First xdm node within the nodes been traversed, by this loop
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
        
         try {
            xctxt.pushCurrentNode(DTM.NULL);
            xctxt.pushCurrentExpressionNode(DTM.NULL);
      
            xctxt.pushSAXLocatorNull();
            xctxt.pushContextNodeList(sourceNodes);
            transformer.pushElemTemplateElement(null);
            
            final Vector sortKeys = (m_sortElems == null)
                                            ? null : transformer.processSortKeysForEachGroup(
                                                                                    this, sourceNode);            
            if (sortKeys != null) {
                // There are xsl:sort elements within xsl:for-each-group. Sort the groups,
                // as per these xsl:sort element details within an XSLT stylesheet. 
                Object forEachGroups = null;
                
                if (xslForEachGroupMap.size() > 0) {
                    forEachGroups = xslForEachGroupMap;     
                }
                else {
                    forEachGroups = xslForEachGroupStartingWithEndingWith;   
                }
                                
                List<GroupingKeyAndGroupPair> groupingKeyAndGroupPairList = sortGroups(xctxt, sortKeys, 
                                                                                          forEachGroups);
                
                for (int idx = 0; idx < groupingKeyAndGroupPairList.size(); idx++) {
                    GroupingKeyAndGroupPair groupingKeyAndGroupPair = groupingKeyAndGroupPairList.get(idx);
                    Object groupingKey = groupingKeyAndGroupPair.getGroupingKey();
                    List<Integer> groupNodesDtmHandles = groupingKeyAndGroupPair.getGroupNodesDtmHandles();
                    
                    for (ElemTemplateElement templateElem = this.m_firstChild; templateElem != null; 
                                                                      templateElem = templateElem.m_nextSibling) {
                        // Set context item for whole of group contents evaluation, to the initial 
                        // item of the group.
                        xctxt.pushCurrentNode((groupNodesDtmHandles.get(0)).intValue());
                        
                        templateElem.setGroupingKey(groupingKey);
                        templateElem.setGroupNodesDtmHandles(groupNodesDtmHandles);
                        xctxt.setSAXLocator(templateElem);
                        transformer.setCurrentElement(templateElem);                   
                        templateElem.execute(transformer);
                     }
                }
            }
            else {
                // xsl:sort elements are not present within xsl:for-each-group element               
                if (xslForEachGroupMap.size() > 0) {
                    // Process the groups, when xsl:for-each-group attributes 'group-by' 
                    // or 'group-adjacent' are used. 
                    List<GroupingKeyAndNodeHandlePair> groupingKeyAndNodeHandlePairList = new ArrayList
                                                                                 <GroupingKeyAndNodeHandlePair>();
                    
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
                    
                     // Sort the xsl:for-each-group's groups, using default sorted order
                     Collections.sort(groupingKeyAndNodeHandlePairList);
                     
                     // Loop through these groups formed by xsl:for-each-group instruction, and process 
                     // the XSL contents of each group.
                     for (int idx = 0; idx < groupingKeyAndNodeHandlePairList.size(); idx++) {
                        GroupingKeyAndNodeHandlePair groupingKeyNodeHandlePair = groupingKeyAndNodeHandlePairList.get(idx);
                        
                        Object groupingKey = groupingKeyNodeHandlePair.getGroupingKey();  // current-grouping-key() value, for this group
                        List<Integer> groupNodesDtmHandles = xslForEachGroupMap.get(groupingKey);  // current-group() contents, for this group                              
                        
                        for (ElemTemplateElement templateElem = this.m_firstChild; templateElem != null; 
                                                         templateElem = templateElem.m_nextSibling) {
                            // Set context item for whole of group contents evaluation, to the initial item of the group
                            xctxt.pushCurrentNode((groupNodesDtmHandles.get(0)).intValue());
                            
                            templateElem.setGroupingKey(groupingKey);
                            templateElem.setGroupNodesDtmHandles(groupNodesDtmHandles);
                            xctxt.setSAXLocator(templateElem);
                            transformer.setCurrentElement(templateElem);                   
                            templateElem.execute(transformer);
                        }
                     }
                }
                else {
                    // Process the groups, when xsl:for-each-group attributes 'group-starting-with' 
                    // or 'group-ending-with' are used.
                    // Loop through these groups formed by xsl:for-each-group instruction, and process 
                    // the XSL contents of each group.
                    for (int idx = 0; idx < xslForEachGroupStartingWithEndingWith.size(); idx++) {
                       List<Integer> groupNodesDtmHandles = xslForEachGroupStartingWithEndingWith.get(idx);
                       for (ElemTemplateElement templateElem = this.m_firstChild; templateElem != null; 
                                                                          templateElem = templateElem.m_nextSibling) {                   
                           // Set context item for whole of group contents evaluation, to the initial item of the group
                           xctxt.pushCurrentNode((groupNodesDtmHandles.get(0)).intValue());
                           
                           // The grouping key is absent when, attributes 'group-starting-with' or 
                           // 'group-ending-with' are used.                   
                           templateElem.setGroupNodesDtmHandles(groupNodesDtmHandles);
                           xctxt.setSAXLocator(templateElem);
                           transformer.setCurrentElement(templateElem);                   
                           templateElem.execute(transformer);
                        }
                    }
                }
            }
         }
         finally {
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
        
         // When a particular xsl:for-each-group's evaluation has completed, set the XPath evaluation 
         // context node to the node which was the context node before xsl:for-each-group evaluation 
         // was started. 
         xctxt.pushCurrentNode(sourceNode);
  }
  
  /**
   * A class to support, reordering the xsl:for-each-group's groups as per definition of default 
   * sorted order (i.e, order of first appearance) when xsl:sort elements are not present within 
   * xsl:for-each-group.
  */
  private class GroupingKeyAndNodeHandlePair implements Comparable<GroupingKeyAndNodeHandlePair> {
     
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
  
  /**
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
  
  /**
   * This method, converts xsl:for-each-group grouping key's initially computed value, 
   * into a normalized data typed value of type java.lang.Object.
   * 
   * For the purpose of, evaluating grouping key XPath expressions for xsl:for-each-group, 
   * the following data types are currently supported : string (xs:string and xalanj's XString), 
   * number (XML Schema numeric types, and xalanj's XNumber), boolean (xs:boolean and xalanj's 
   * XBoolean), xs:date, xs:dateTime, xs:time, xs:QName, xs:anyURI. Any other data type for 
   * grouping key is converted to a string value.
   */
  private Object getXPathEvaluationRawResult(XObject xpathEvalResult) {
      Object xpathRawResult = null;
      
      if (xpathEvalResult instanceof XString) {
          xpathRawResult = xpathEvalResult.str();    
      }
      else if (xpathEvalResult instanceof XSQName) {
    	  XSQName qName = (XSQName)xpathEvalResult;
    	  String localName = qName.getLocalName();
    	  String namespaceUri = qName.getNamespaceUri();
    	  xpathRawResult = localName + ((namespaceUri == null) ? "" : ":" + namespaceUri); 
      }
      else if (xpathEvalResult instanceof XSString) {
          xpathRawResult = ((XSString)xpathEvalResult).stringValue();  
      }
      else if (xpathEvalResult instanceof XNumber) {
          xpathRawResult = Double.valueOf(((XNumber)xpathEvalResult).num());  
      }
      else if (xpathEvalResult instanceof XSNumericType) {
          String strVal = ((XSNumericType)xpathEvalResult).stringValue();
          xpathRawResult = Double.valueOf(strVal);
      }
      else if (xpathEvalResult instanceof XBoolean) {
          xpathRawResult =  Boolean.valueOf(((XBoolean)xpathEvalResult).bool());     
      }
      else if (xpathEvalResult instanceof XSBoolean) {
          xpathRawResult = Boolean.valueOf(((XSBoolean)xpathEvalResult).value());
      }
      else if ((xpathEvalResult instanceof XSDate) || (xpathEvalResult instanceof XSDateTime) || 
    		                                          (xpathEvalResult instanceof XSTime)) {
          xpathRawResult = xpathEvalResult;
      }
      else if (xpathEvalResult instanceof XSAnyURI) {
    	  xpathRawResult = xpathEvalResult; 
      }
      else {
          // Any other data type for grouping key, is treated as string
          xpathRawResult = XslTransformEvaluationHelper.getStrVal(xpathEvalResult);  
      }
      
      return xpathRawResult;
  }
  
  /**
   * Get XML document source nodes (represented as an 'DTMIterator' object), from
   * a list of XNodeSet objects contained within a 'ResultSequence' object.    
   */
  private DTMIterator getSourceNodesFromResultSequence(ResultSequence resultSeq, XPathContext xctxt) 
                                                                                          throws TransformerException {
     DTMIterator sourceNodes = null;
     
     NodeVector nodeVector = new NodeVector();
     
     for (int idx = 0; idx < resultSeq.size(); idx++) {
         XObject xObject = resultSeq.item(idx);
         xObject = ((XNodeSet)xObject).getFresh();
         DTMIterator dtmIter = xObject.iter();
         nodeVector.addElement(dtmIter.nextNode()); 
     }
   
     NodeSequence nodeSequence = new NodeSequence(nodeVector);
     
     try {
        sourceNodes = nodeSequence.cloneWithReset();
     } catch (CloneNotSupportedException ex) {
        throw new TransformerException("An error occured during XSL grouping with xsl:for-each-group "
                                                                                            + "instruction.", xctxt.getSAXLocator());
     }
     
     return sourceNodes; 
  }

}
