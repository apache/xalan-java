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
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xml.utils.NodeVector;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathCollationSupport;
import org.apache.xpath.XPathContext;
import org.apache.xpath.axes.NodeCursor;
import org.apache.xpath.composite.XPathForExpr;
import org.apache.xpath.composite.XPathSequenceConstructor;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;
import org.apache.xpath.operations.Variable;
import org.apache.xpath.types.StringWithCollation;

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
   * The "collation" attribute value.
   */
  private String m_collationUri = null;
  
  /**
   * An XPathCollationSupport object value. 
   */
  private XPathCollationSupport m_xpathCollationSupport = null;
  
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
   * Set the "collation" attribute.
   *
   * @param collation   String value for the "collation" attribute.
   */
  public void setCollation(String collationUri)
  {
	  m_collationUri = collationUri;   
  }
  
  /**
   * Get the "collation" attribute.
   *
   * @return   String value of the "collation" attribute.
   */
  public String getCollation()
  {
      return m_collationUri;
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
           transformer.getTraceManager().emitTraceEvent(this);
        }
    
        try {
            transformSelectedNodes(transformer);
        }
        finally {
            if (transformer.getDebug()) {
    	       transformer.getTraceManager().emitTraceEndEvent(this);
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
  public List<GroupingKeyAndGroupPairForXslSort> sortGroups(XPathContext xctxt, Vector sortKeys, Object forEachGroups)
                                                            throws TransformerException {
        List<GroupingKeyAndGroupPairForXslSort> sortedGroups = null;
      
        ForEachGroupXslSortSorter sorter = new ForEachGroupXslSortSorter(xctxt, this);
        
        sortedGroups = sorter.sort(forEachGroups, sortKeys, xctxt);
    
        return sortedGroups;
  }

  /**
   * Add a child to the child list.
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
        
        DTMCursorIterator sourceNodes = null;
        
        if (m_selectExpression instanceof Variable) {
            XObject xObj = ((Variable)m_selectExpression).execute(xctxt);
            
            if (xObj instanceof ResultSequence) {                              
               ResultSequence resultSeq = (ResultSequence)xObj;
               sourceNodes = getSourceNodesFromResultSequence(resultSeq, xctxt);                
            }
        }
        else if ((m_selectExpression instanceof XPathSequenceConstructor) ||
                                                    (m_selectExpression instanceof XPathForExpr)) {
            XObject xObj = m_selectExpression.execute(xctxt);
            
            ResultSequence resultSeq = (ResultSequence)xObj;
            sourceNodes = getSourceNodesFromResultSequence(resultSeq, xctxt);
        }
        
        m_xpathCollationSupport = xctxt.getXPathCollationSupport();
        
        if (sourceNodes == null) {        
           sourceNodes = m_selectExpression.asIterator(xctxt, sourceNode);
        }
        
        /**
         * A java.util.Map object to store groups formed for, either 'group-by' or
         * 'group-adjacent' attributes. This map stores mappings between, grouping key
         * value and contents of the group corresponding to a grouping keys.
         */
        Map<Object, List<Integer>> xslForEachGroupMap = new HashMap<Object, List<Integer>>();
        
        /**
         * List to store groups formed for, either 'group-starting-with' or 'group-ending-with' 
         * attributes. The grouping keys are not available for, these kind of groups. 
         */
        List<List<Integer>> xslForEachGroupStartingWithEndingWith = new ArrayList<List<Integer>>();
        
        if (m_GroupByExpression != null) {
            int nextNode;
            
            while (DTM.NULL != (nextNode = sourceNodes.nextNode())) {
                xctxt.pushCurrentNode(nextNode);
                            
                XObject xpathEvalResult = m_GroupByExpression.execute(xctxt);                
                Object groupingKeyValue = getNormalizedGroupingKeyValue(xpathEvalResult);
                                            
                if (xslForEachGroupMap.get(groupingKeyValue) != null) {
                    List<Integer> group = xslForEachGroupMap.get(groupingKeyValue);
                    group.add(nextNode);
                } 
                else {
                    List<Integer> group = new ArrayList<Integer>();
                    group.add(nextNode);
                    xslForEachGroupMap.put(groupingKeyValue, group);
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
                 Object groupingKeyValue = getNormalizedGroupingKeyValue(xpathEvalResult);                 
                 Object currValue = groupingKeyValue;
                 
                 if (idx == 0) {
                     // This is the first XDM node being iterated, within this loop
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
                       List<Integer> group = xslForEachGroupStartingWithEndingWith.get(groupsCountSoFar - 1);
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
                        List<Integer> group = xslForEachGroupStartingWithEndingWith.get(groupsCountSoFar - 1);
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
            	/**
            	 * There are xsl:sort elements within xsl:for-each-group. Sort the groups,
            	 * as per these xsl:sort element details within an XSLT stylesheet.
            	 */
            	
                Object forEachGroups = null;
                
                if (xslForEachGroupMap.size() > 0) {
                    forEachGroups = xslForEachGroupMap;     
                }
                else {
                    forEachGroups = xslForEachGroupStartingWithEndingWith;   
                }
                                
                List<GroupingKeyAndGroupPairForXslSort> groupingKeyAndGroupPairList = sortGroups(xctxt, sortKeys, 
                                                                                          forEachGroups);
                
                for (int idx = 0; idx < groupingKeyAndGroupPairList.size(); idx++) {
                    GroupingKeyAndGroupPairForXslSort groupingKeyAndGroupPair = groupingKeyAndGroupPairList.get(idx);
                    Object groupingKey = groupingKeyAndGroupPair.getGroupingKey();
                    List<Integer> groupNodesDtmHandles = groupingKeyAndGroupPair.getGroupNodesDtmHandles();
                    
                    xctxt.setGroupPosition(idx + 1);    							// Set value of fn:position() function within xsl:for-each-group
                    xctxt.setGroupCount(groupingKeyAndGroupPairList.size());        // Set value of the number of groups formed
                    
                    for (ElemTemplateElement templateElem = this.m_firstChild; templateElem != null; 
                                                                      templateElem = templateElem.m_nextSibling) {
                    	/**
                    	 * Set context item for whole of group contents evaluation, to the initial
                    	 * item of the group.
                    	 */
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
                	/**
                	 * Process the groups, when xsl:for-each-group attributes 'group-by' or
                	 * 'group-adjacent' are used.
                	 */
                    List<GroupingKeyAndNodeHandlePair> groupingKeyAndNodeHandlePairList = new ArrayList
                                                                                 <GroupingKeyAndNodeHandlePair>();
                    
                    Set<Object> groupingKeys = xslForEachGroupMap.keySet();
                    for (Iterator<Object> groupingKeysIter = groupingKeys.iterator(); 
                                                                    groupingKeysIter.hasNext(); ) {
                        Object groupingKey = groupingKeysIter.next();
                        List<Integer> groupNodesDtmHandles = xslForEachGroupMap.get(groupingKey);
                        
                        /**
                         * In the absence of xsl:for-each-group->xsl:sort instruction,
                         * groups need to be sorted according to default sorted order as 
                         * specified by XSLT 3.0 spec. For example, if we've two groups,
                         * the group that comes earlier within XSL transformation output,
                         * has its first item present before the first item of the other 
                         * group in document order. 
                         */
                        Integer groupContentsFirstItemNodeHandle =  groupNodesDtmHandles.get(0);
                        
                        GroupingKeyAndNodeHandlePair groupingKeyNodeHandlePair = new GroupingKeyAndNodeHandlePair(
                                                                        									groupingKey, groupContentsFirstItemNodeHandle);
                        groupingKeyAndNodeHandlePairList.add(groupingKeyNodeHandlePair);
                     }
                    
                     // Sort the xsl:for-each-group's groups, using default sorted order
                     Collections.sort(groupingKeyAndNodeHandlePairList);
                     
                     /**
                      * Loop through these groups formed by xsl:for-each-group instruction, and process
                      * the XSL contents of each group.
                      */
                     for (int idx = 0; idx < groupingKeyAndNodeHandlePairList.size(); idx++) {
                        GroupingKeyAndNodeHandlePair groupingKeyNodeHandlePair = groupingKeyAndNodeHandlePairList.get(idx);
                        
                        Object groupingKey = groupingKeyNodeHandlePair.getGroupingKey();           // current-grouping-key() value, for this group
                        List<Integer> groupNodesDtmHandles = xslForEachGroupMap.get(groupingKey);  // current-group() contents, for this group
                        
                        xctxt.setGroupPosition(idx + 1);										   // Set value of fn:position() function within xsl:for-each-group
                        xctxt.setGroupCount(groupingKeyAndNodeHandlePairList.size());		       // Set value of the number of groups formed                        
                        
                        for (ElemTemplateElement templateElem = this.m_firstChild; templateElem != null; 
                                                         											templateElem = templateElem.m_nextSibling) {
                        	/**
                        	 * Set context item for whole of group contents evaluation, to the
                        	 * initial item of the group.
                        	 */
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
                	/**
                	 * Process the groups, when xsl:for-each-group attributes 'group-starting-with' or
                	 * 'group-ending-with' are used. Loop through these groups formed by xsl:for-each-group
                	 * instruction, and process the XSL contents of each group.
                	 */
                	
                    for (int idx = 0; idx < xslForEachGroupStartingWithEndingWith.size(); idx++) {
                       List<Integer> groupNodesDtmHandles = xslForEachGroupStartingWithEndingWith.get(idx);
                       
                       xctxt.setGroupPosition(idx + 1);											// Set value of fn:position() function within xsl:for-each-group
                       xctxt.setGroupCount(xslForEachGroupStartingWithEndingWith.size());	    // Set value of the number of groups formed                       
                       
                       for (ElemTemplateElement templateElem = this.m_firstChild; templateElem != null; 
                                                                          templateElem = templateElem.m_nextSibling) {                   
                    	   /**
                    	    * Set context item for whole of group contents evaluation, to the initial
                    	    * item of the group.
                    	    */
                           xctxt.pushCurrentNode((groupNodesDtmHandles.get(0)).intValue());
                           
                           /**
                            * The grouping key is absent when, attributes 'group-starting-with' or
                            * 'group-ending-with' are used.
                            */
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
                  transformer.getTraceManager().emitSelectedEndEvent(sourceNode, this,
								                                      "select", new XPath(m_selectExpression),
								                                      new org.apache.xpath.objects.XMLNodeCursorImpl(sourceNodes));
              }
        
              xctxt.popSAXLocator();
              xctxt.popContextNodeList();
              transformer.popElemTemplateElement();
              xctxt.popCurrentExpressionNode();
              xctxt.popCurrentNode();
              xctxt.setGroupPosition(0);
              xctxt.setGroupCount(0);
              sourceNodes.detach();
         }
        
         /**
          * When a particular xsl:for-each-group's evaluation has completed, set the XPath evaluation 
          * context node to the node which was the context node before xsl:for-each-group evaluation 
          * was started.
         */         
         xctxt.pushCurrentNode(sourceNode);
  }
  
  /**
   * Class definition to implement, sorting of groups formed by xsl:for-each-group 
   * instruction as per default sorted order as specified by XSLT 3.0 spec.
  */
  private class GroupingKeyAndNodeHandlePair implements Comparable<GroupingKeyAndNodeHandlePair> {
     
	 /**
	  * Variable storing value of a grouping key, for a particular 
	  * group. All the members of a group have same value for the 
	  * grouping key.
	  */
     private Object groupingKey;
     
     /**
      * XML document node handle for the first member of a group.
      */
     private Integer nodeHandle;
     
     /**
      * Class constructor.
      * 
      * @param groupingKey
      * @param nodeHandle
      */
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

     /**
      * This method compares, two XML node integer handles using relational 
      * <, > or = semantics. An XML node with numerically lesser node handle 
      * value is earlier (this is an intrinsic property of a DTM object 
      * instance for an XML document) in document order than a node with 
      * numerically greater node handle.
      */
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
   * Method definition, to transform xsl:for-each-group instruction's grouping 
   * key value (which is the result of evaluating xsl:for-each-group's 'group-by' or 
   * 'group-adjacent' XPath expressions), into a normalized value of type 
   * java.lang.Object (which is the data type of underlying java.util.Map 
   * object's key definition).
   */
  private Object getNormalizedGroupingKeyValue(XObject xpathEvalResult) {
      
	  Object xpathRawResult = null;
      
      if (xpathEvalResult instanceof XString) {
    	  if (m_collationUri == null) {
    		 xpathRawResult = xpathEvalResult.str();   
    	  }
    	  else {
    	     xpathRawResult = new StringWithCollation(xpathEvalResult.str(), m_collationUri, m_xpathCollationSupport);
    	  }
      }
      else if (xpathEvalResult instanceof XSQName) {
    	  XSQName qName = (XSQName)xpathEvalResult;
    	  String localPart = qName.getLocalPart();
    	  String namespaceUri = qName.getNamespaceUri();
    	  xpathRawResult = localPart + ((namespaceUri == null) ? "" : ":" + namespaceUri); 
      }
      else if (xpathEvalResult instanceof XSString) {
    	  if (m_collationUri == null) {
    		 xpathRawResult = ((XSString)xpathEvalResult).stringValue();   
    	  }
    	  else {
             xpathRawResult = new StringWithCollation(((XSString)xpathEvalResult).stringValue(), m_collationUri, m_xpathCollationSupport);
    	  }
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
  private DTMCursorIterator getSourceNodesFromResultSequence(ResultSequence resultSeq, XPathContext xctxt) 
                                                                                          throws TransformerException {
     DTMCursorIterator sourceNodes = null;
     
     NodeVector nodeVector = new NodeVector();
     
     for (int idx = 0; idx < resultSeq.size(); idx++) {
         XObject xObject = resultSeq.item(idx);
         xObject = ((XMLNodeCursorImpl)xObject).getFresh();
         DTMCursorIterator dtmIter = xObject.iter();
         nodeVector.addElement(dtmIter.nextNode()); 
     }
   
     NodeCursor nodeSequence = new NodeCursor(nodeVector);
     
     try {
        sourceNodes = nodeSequence.cloneWithReset();
     } 
     catch (CloneNotSupportedException ex) {
        throw new TransformerException("An error occured during XSL grouping with xsl:for-each-group "
                                                                                            + "instruction.", xctxt.getSAXLocator());
     }
     
     return sourceNodes; 
  }

}
