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

import javax.xml.transform.SourceLocator;
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
import org.apache.xpath.objects.XBooleanStatic;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;
import org.apache.xpath.operations.Variable;
import org.apache.xpath.types.ForEachGroupCompositeGroupingKey;
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
   * xsl:for-each-group element's attribute "select" XPath expression.
   */
  private XPath m_selectExpression = null;
  
  /**
   * xsl:for-each-group element's attribute "group-by" XPath expression.
   */
  private XPath m_GroupByExpression = null;  
  
  /**
   * xsl:for-each-group element's attribute "group-starting-with" XPath expression.
   */  
  private XPath m_GroupStartingWithExpression = null;
  
  /**
   * xsl:for-each-group element's attribute "group-ending-with" XPath expression.
   */
  private XPath m_GroupEndingWithExpression = null;
  
  /**
   * xsl:for-each-group element's attribute "group-adjacent" XPath expression.
   */
  private XPath m_GroupAdjacentExpression = null;
  
  /**
   * xsl:for-each-group element attribute "composite"'s value.
   */
  private boolean m_composite;
  
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
      m_selectExpression = xpath;  
  }

  /**
   * Get the "select" attribute.
   *
   * @return The XPath expression for the "select" attribute.
   */
  public XPath getSelect()
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
      m_GroupByExpression = xpath;
  }
  
  /**
   * Get the "group-by" attribute.
   *
   * @return The XPath expression for the "group-by" attribute.
   */
  public XPath getGroupBy()
  {
      return m_GroupByExpression;
  }
  
  /**
   * Set the "group-starting-with" attribute.
   *
   * @param xpath The XPath expression for the "group-starting-with" attribute.
   */
  public void setGroupStartingWith(XPath xpath)
  {
      m_GroupStartingWithExpression = xpath;   
  }
  
  /**
   * Get the "group-starting-with" attribute.
   *
   * @return The XPath expression for the "group-starting-with" attribute.
   */
  public XPath getGroupStartingWith()
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
      m_GroupEndingWithExpression = xpath;   
  }
  
  /**
   * Get the "group-ending-with" attribute.
   *
   * @return The XPath expression for the "group-ending-with" attribute.
   */
  public XPath getGroupEndingWith()
  {
      return m_GroupEndingWithExpression;
  }
  
  /**
   * Set the "group-adjacent" attribute.
   *
   * @param xpath The XPath expression for the "group-adjacent" attribute.
   */
  public void setGroupAdjacent(XPath xpath)
  {
      m_GroupAdjacentExpression = xpath;   
  }
  
  /**
   * Get the "group-adjacent" attribute.
   *
   * @return The XPath expression for the "group-adjacent" attribute.
   */
  public XPath getGroupAdjacent()
  {
      return m_GroupAdjacentExpression;
  }
  
  /**
   * Set the "composite" attribute.
   */
  public void setComposite(boolean composite) {
	  this.m_composite = composite;
  }
  
  /**
   * Get the "composite" attribute.
   */
  public boolean getComposite() {
	  return m_composite;
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
            m_selectExpression = new XPath(getStylesheetRoot().m_selectDefault.getExpression());
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
  public List<GroupingKeyAndGroupPair> sortGroups(XPathContext xctxt, Vector sortKeys, Object forEachGroups)
                                                            throws TransformerException {
        List<GroupingKeyAndGroupPair> sortedGroups = null;
      
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
	  return m_selectExpression.getExpression();
      // return m_selectExpression;
  }

  /**
   * @see ExpressionOwner#setExpression(Expression)
   */
  public void setExpression(Expression exp)
  {
      exp.exprSetParent(this);
      m_selectExpression = new XPath(exp);
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
        
        Expression selectExpr = m_selectExpression.getExpression();
        
        if (selectExpr instanceof Variable) {
            XObject xObj = ((Variable)selectExpr).execute(xctxt);
            
            if (xObj instanceof ResultSequence) {                              
               ResultSequence resultSeq = (ResultSequence)xObj;
               sourceNodes = getSourceNodesFromResultSequence(resultSeq, xctxt);                
            }
        }
        else if ((selectExpr instanceof XPathSequenceConstructor) ||
                                                    (selectExpr instanceof XPathForExpr)) {
            XObject xObj = selectExpr.execute(xctxt);
            
            ResultSequence resultSeq = (ResultSequence)xObj;
            sourceNodes = getSourceNodesFromResultSequence(resultSeq, xctxt);
        }
        
        m_xpathCollationSupport = xctxt.getXPathCollationSupport();
        
        if (sourceNodes == null) {
           XObject value = m_selectExpression.execute(xctxt, sourceNode, xctxt.getNamespaceContext());
           XMLNodeCursorImpl xmlNodeCursorImpl = null;
           if (value instanceof ResultSequence) {
        	   ResultSequence rSeq = (ResultSequence)value;
        	   xmlNodeCursorImpl = XslTransformEvaluationHelper.getXNodeSetFromResultSequence(rSeq, xctxt);
        	   sourceNodes = xmlNodeCursorImpl.iter();
           }
           else {
        	   xmlNodeCursorImpl = (XMLNodeCursorImpl)value;
        	   sourceNodes = xmlNodeCursorImpl.iter();
           }
        }
        
        /**
         * A java.util.Map object to store groups formed by 'group-by' attribute. 
         * This map stores mappings between, grouping key value and contents of 
         * the group corresponding to a grouping keys.
         */
        Map<Object, List<Integer>> xslForEachGroupMap = new HashMap<Object, List<Integer>>();
        
        List<GroupingKeyAndGroupPair> xslForEachGroupAdjacentList = new ArrayList<GroupingKeyAndGroupPair>();
        
        /**
         * List to store groups formed for, either 'group-starting-with' or 'group-ending-with' 
         * attributes. The grouping keys are not available for, these kind of groups. 
         */
        List<List<Integer>> xslForEachGroupStartingWithEndingWith = new ArrayList<List<Integer>>();
        
        if (m_GroupByExpression != null) {
        	constructGroupsForGroupBy(xctxt, sourceNodes, xslForEachGroupMap);
        }        
        else if (m_GroupStartingWithExpression != null) {
        	constructGroupsForGroupStartingWith(xctxt, sourceNodes, xslForEachGroupStartingWithEndingWith);
        }
        else if (m_GroupEndingWithExpression != null) {                          
        	constructGroupsForGroupEndingWith(xctxt, sourceNodes, xslForEachGroupStartingWithEndingWith);
        }
        else if (m_GroupAdjacentExpression != null) {
        	constructGroupsForGroupAdjacent(xctxt, sourceNodes, xslForEachGroupAdjacentList);
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

        		List<GroupingKeyAndGroupPair> groupingKeyAndGroupPairList = sortGroups(xctxt, sortKeys, forEachGroups);

        		for (int idx = 0; idx < groupingKeyAndGroupPairList.size(); idx++) {
        			GroupingKeyAndGroupPair groupingKeyAndGroupPair = groupingKeyAndGroupPairList.get(idx);
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
        			 * Process the groups, when xsl:for-each-group attribute 'group-by' 
        			 * is used.
        			 */
        			List<GroupingKeyAndNodeHandlePair> groupingKeyAndNodeHandlePairList = new ArrayList<GroupingKeyAndNodeHandlePair>();

        			Set<Object> groupingKeys = xslForEachGroupMap.keySet();
        			for (Iterator<Object> groupingKeysIter = groupingKeys.iterator(); groupingKeysIter.hasNext(); ) {
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

        				GroupingKeyAndNodeHandlePair groupingKeyNodeHandlePair = new GroupingKeyAndNodeHandlePair(groupingKey, 
        						                                                                                groupContentsFirstItemNodeHandle);
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

        				Object groupingKey = groupingKeyNodeHandlePair.getGroupingKey();              // current-grouping-key() value, for this group
        				List<Integer> groupNodesDtmHandles = xslForEachGroupMap.get(groupingKey);     // current-group() contents, for this group

        				xctxt.setGroupPosition(idx + 1);										      // Set value of fn:position() function within xsl:for-each-group
        				xctxt.setGroupCount(groupingKeyAndNodeHandlePairList.size());		          // Set value of the number of groups formed                        

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
        		else if (xslForEachGroupAdjacentList.size() > 0) {                	
        			/**
        			 * Loop through the groups formed by xsl:for-each-group instruction using 
        			 * 'group-adjacent' attribute, and process the XSL contents of each group.
        			 */
        			for (int idx = 0; idx < xslForEachGroupAdjacentList.size(); idx++) {
        				GroupingKeyAndGroupPair groupingKeyAndGroupPair = xslForEachGroupAdjacentList.get(idx);

        				Object groupingKey = groupingKeyAndGroupPair.getGroupingKey();                             // current-grouping-key() value, for this group
        				List<Integer> groupNodesDtmHandles = groupingKeyAndGroupPair.getGroupNodesDtmHandles();    // current-group() contents, for this group

        				xctxt.setGroupPosition(idx + 1);										                   // Set value of fn:position() function within xsl:for-each-group
        				xctxt.setGroupCount(xslForEachGroupAdjacentList.size());		                           // Set value of the number of groups formed                        

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
        				xctxt.setGroupCount(xslForEachGroupStartingWithEndingWith.size());	        // Set value of the number of groups formed                       

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
        														   "select", m_selectExpression,
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
   * Construct groups using xsl:for-each-group instruction, when 'group-by' 
   * attribute is present.
   * 
   * @param xctxt                     XPath context object
   * @param sourceNodes               Iterator object for source document nodes that 
   *                                  have to be grouped.
   * @param xslForEachGroupByMap      java.util.Map object that needs to be populated
   *                                  with groups formed.
   * @throws TransformerException
   */
  private void constructGroupsForGroupBy(XPathContext xctxt, DTMCursorIterator sourceNodes,
		  							     Map<Object, List<Integer>> xslForEachGroupByMap) throws TransformerException {
	  int nextNode;
	  
	  SourceLocator srcLocator = xctxt.getSAXLocator();

	  while (DTM.NULL != (nextNode = sourceNodes.nextNode())) {
		  XObject xpathEvalResult = m_GroupByExpression.execute(xctxt, nextNode, xctxt.getNamespaceContext());                
		  Object groupingKeyValue = getNormalizedGroupingKeyValue(xctxt, xpathEvalResult);

		  if (!m_composite) {
			  addXdmNodeHandleToGroup(xslForEachGroupByMap, nextNode, groupingKeyValue);
		  }
		  else {
			  // Processing for xsl:for-each-group's composite grouping key
			  if (groupingKeyValue instanceof ForEachGroupCompositeGroupingKey) {
				  ForEachGroupCompositeGroupingKey groupingKeyObj = (ForEachGroupCompositeGroupingKey)groupingKeyValue;
				  ResultSequence groupingKeySeq = groupingKeyObj.getValue();
				  if (groupingKeySeq.size() >= 1) {
					  addXdmNodeHandleToGroup(xslForEachGroupByMap, nextNode, groupingKeyValue);
				  }
				  else {
					  throw new TransformerException("XTSE1080 : An xsl:for-each-group instruction with attribute "
							  		                             + "value 'composite=\"yes\"', resulted in a grouping key "
							  		                             + "sequence that is empty.", srcLocator);
				  }
			  }
			  else {
				  addXdmNodeHandleToGroup(xslForEachGroupByMap, nextNode, groupingKeyValue); 
			  }
		  }
	  }
  }

  /**
   * When processing xsl:for-each-group instruction having 'group-by' attribute, 
   * resulting in a grouping key having atomic value, add an XDM node handle to the 
   * required group represented by an object of type java.util.Map. 
   * 
   * @param xslForEachGroupByMap		An java.util.Map object representing all the groups 
   *                                    constructed for xsl:for-each-group instruction. 
   * @param nodeHandle                  An XDM node handle that needs to be assigned to an
   *                                    xsl:for-each-group instruction's correct group.
   * @param groupingKeyValue            A grouping key value having an atomic data type, for 
   *                                    xsl:for-each-group instruction. 
   */
  private void addXdmNodeHandleToGroup(Map<Object, List<Integer>> xslForEachGroupByMap, 
		                                                       int nodeHandle, Object groupingKeyValue) {
	  if (xslForEachGroupByMap.get(groupingKeyValue) != null) {
		  List<Integer> group = xslForEachGroupByMap.get(groupingKeyValue);
		  group.add(nodeHandle);
	  } 
	  else {
		  List<Integer> group = new ArrayList<Integer>();
		  group.add(nodeHandle);
		  xslForEachGroupByMap.put(groupingKeyValue, group);
	  }
  }
  
  /**
   * Construct groups using xsl:for-each-group instruction, when 'group-starting-with' 
   * attribute is present.
   * 
   * @param xctxt								XPath context object
   * @param sourceNodes							Iterator object for source document nodes that 
   *                                  			have to be grouped. 
   * @param xslForEachGroupStartingWith         A list that needs to be populated with groups
   *                                            formed.
   * @throws TransformerException
   */
  private void constructGroupsForGroupStartingWith(XPathContext xctxt, DTMCursorIterator sourceNodes,
		                                           List<List<Integer>> xslForEachGroupStartingWith) throws TransformerException {
	  int nextNode;
	  
	  List<Integer> allNodeHandleList = new ArrayList<Integer>();
	  
	  List<Integer> grpStartNodeHandles = new ArrayList<Integer>();
	  
	  int idx3 = 0;
	  while (DTM.NULL != (nextNode = sourceNodes.nextNode())) {
		  allNodeHandleList.add(Integer.valueOf(nextNode));
		  DTM dtm = xctxt.getDTM(nextNode);
		  
		  if (idx3 == 0) {
			  int parentNode = dtm.getParent(nextNode);
			  XObject groupStartingWithEvalResult = m_GroupStartingWithExpression.execute(xctxt, parentNode, xctxt.getNamespaceContext());
			  XMLNodeCursorImpl grpStartingWithNodeInit = (XMLNodeCursorImpl)groupStartingWithEvalResult;
			  DTMCursorIterator dtmCursorIter = grpStartingWithNodeInit.getContainedIter();
			  while (DTM.NULL != (nextNode = dtmCursorIter.nextNode())) {            	 
				  grpStartNodeHandles.add(Integer.valueOf(nextNode)); 
			  }
		  }
		  
		  idx3++;
	  }

	  for (int idx = 0; idx < grpStartNodeHandles.size(); idx++) {
		  int grpStartNodeHandle = grpStartNodeHandles.get(idx);
		  List<Integer> groupNodeHandles = new ArrayList<Integer>();
		  for (int idx2 = 0; idx2 < allNodeHandleList.size(); idx2++) {
			  if (grpStartNodeHandles.size() > 1) {
				  if ((idx < (grpStartNodeHandles.size() - 1)) && ((allNodeHandleList.get(idx2) >= grpStartNodeHandle) && 
						                                           (allNodeHandleList.get(idx2) < grpStartNodeHandles.get(idx + 1)))) {
					  groupNodeHandles.add(allNodeHandleList.get(idx2));  
				  }
			  }
			  else if (allNodeHandleList.get(idx2) >= grpStartNodeHandle) {
				  groupNodeHandles.add(allNodeHandleList.get(idx2)); 
			  }
		  }

		  if (groupNodeHandles.size() > 0) {
			  xslForEachGroupStartingWith.add(groupNodeHandles);
		  }
	  }

	  // Getting node handles of the last group, for xsl:for-each-group's 
	  // group-starting-with attribute.
	  int temp = grpStartNodeHandles.size();             
	  int grpStartNodeHandle = grpStartNodeHandles.get(temp - 1);
	  List<Integer> groupNodeHandles = new ArrayList<Integer>();
	  for (int idx2 = 0; idx2 < allNodeHandleList.size(); idx2++) {            	   
		  if (grpStartNodeHandle <= allNodeHandleList.get(idx2)) {
			  groupNodeHandles.add(allNodeHandleList.get(idx2));  
		  }
	  }

	  xslForEachGroupStartingWith.add(groupNodeHandles);
  }

  /**
   * Construct groups using xsl:for-each-group instruction, when 'group-ending-with' 
   * attribute is present.
   * 
   * @param xctxt								XPath context object
   * @param sourceNodes							Iterator object for source document nodes that 
   *                                  			have to be grouped. 
   * @param xslForEachGroupEndingWith           A list that needs to be populated with groups
   *                                            formed.
   * @throws TransformerException
   */
  private void constructGroupsForGroupEndingWith(XPathContext xctxt, DTMCursorIterator sourceNodes,
		  									     List<List<Integer>> xslForEachGroupEndingWith) throws TransformerException {
	  int nextNode;
	  
	  List<Integer> allNodeHandleList = new ArrayList<Integer>();
	  
	  List<Integer> grpEndNodeHandles = new ArrayList<Integer>();	  
	  
	  int idx3 = 0;
	  while (DTM.NULL != (nextNode = sourceNodes.nextNode())) {
		  allNodeHandleList.add(Integer.valueOf(nextNode));		  
		  DTM dtm = xctxt.getDTM(nextNode);
		  
		  if (idx3 == 0) {
			  int parentNode = dtm.getParent(nextNode);
			  XObject groupStartingWithEvalResult = m_GroupEndingWithExpression.execute(xctxt, parentNode, xctxt.getNamespaceContext());
			  XMLNodeCursorImpl grpStartingWithNodeInit = (XMLNodeCursorImpl)groupStartingWithEvalResult;
			  DTMCursorIterator dtmCursorIter = grpStartingWithNodeInit.getContainedIter();
			  while (DTM.NULL != (nextNode = dtmCursorIter.nextNode())) {            	 
				  grpEndNodeHandles.add(Integer.valueOf(nextNode)); 
			  }
		  }
		  
		  idx3++;
	  }
	  

	  for (int idx = 0; idx < grpEndNodeHandles.size(); idx++) {
		  int grpEndNodeHandle = grpEndNodeHandles.get(idx);
		  List<Integer> groupNodeHandles = new ArrayList<Integer>();
		  if (idx == 0) {
			  for (int idx2 = 0; idx2 < allNodeHandleList.size(); idx2++) {
				  int nodeHandle = allNodeHandleList.get(idx2);
				  if (nodeHandle <= grpEndNodeHandle) {
					  groupNodeHandles.add(nodeHandle);
				  }            			 
			  }
		  }
		  else {
			  List<Integer> latestGrpFormed = xslForEachGroupEndingWith.get(idx - 1);
			  int temp = latestGrpFormed.get(latestGrpFormed.size() - 1);
			  for (int idx2 = 0; idx2 < allNodeHandleList.size(); idx2++) {
				  int nodeHandle = allNodeHandleList.get(idx2); 
				  if ((nodeHandle > temp) && (nodeHandle <= grpEndNodeHandle)) {
					  groupNodeHandles.add(nodeHandle); 
				  }
			  }
		  }

		  if (groupNodeHandles.size() > 0) {
			  xslForEachGroupEndingWith.add(groupNodeHandles);
		  }
	  }
  }

  /**
   * Construct groups using xsl:for-each-group instruction, when 'group-adjacent' 
   * attribute is present.
   * 
   * @param xctxt								XPath context object
   * @param sourceNodes							Iterator object for source document nodes that 
   *                                  			have to be grouped. 
   * @param xslForEachGroupAdjacentList         A list that needs to be populated with groups
   *                                            formed.
   * @throws TransformerException
   */
  private void constructGroupsForGroupAdjacent(XPathContext xctxt, DTMCursorIterator sourceNodes,
		                                       List<GroupingKeyAndGroupPair> xslForEachGroupAdjacentList) throws TransformerException {
	 Object prevValue = null;
	 int idx = 0;
	 int nextNode;
	 
	 while (DTM.NULL != (nextNode = sourceNodes.nextNode())) {	     
	     XObject xpathEvalResult = m_GroupAdjacentExpression.execute(xctxt, nextNode, xctxt.getNamespaceContext());                 
	     Object groupingKeyValue = getNormalizedGroupingKeyValue(xctxt, xpathEvalResult);                 
	     Object currValue = groupingKeyValue;
	     
	     List<Integer> group = null;
	     
	     if (idx == 0) {
	         // This is the first XDM node being iterated, within this loop
	         group = new ArrayList<Integer>();
	         group.add(nextNode);
	         GroupingKeyAndGroupPair groupingKeyAndGroupPair = new GroupingKeyAndGroupPair(currValue, group);
	         xslForEachGroupAdjacentList.add(groupingKeyAndGroupPair);
	         prevValue = currValue; 
	     }
	     else {
	        if (currValue.equals(prevValue)) {
	        	int currResultSize = xslForEachGroupAdjacentList.size();
	        	GroupingKeyAndGroupPair groupingKeyAndGroupPair = xslForEachGroupAdjacentList.get(currResultSize - 1);
	        	group = groupingKeyAndGroupPair.getGroupNodesDtmHandles(); 
	            group.add(nextNode);
	        }
	        else {
	            group = new ArrayList<Integer>();
	            group.add(nextNode);
	            GroupingKeyAndGroupPair groupingKeyAndGroupPair = new GroupingKeyAndGroupPair(currValue, group);
	            xslForEachGroupAdjacentList.add(groupingKeyAndGroupPair);
	        }
	        
	        prevValue = currValue;
	     }
	     
	     idx++;
	 }
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
  private Object getNormalizedGroupingKeyValue(XPathContext xctxt, XObject xpathEvalResult) {
      
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
      else if (xpathEvalResult instanceof XBooleanStatic) {
    	  xpathRawResult =  Boolean.valueOf(((XBooleanStatic)xpathEvalResult).bool());
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
      else if (xpathEvalResult instanceof ResultSequence) {
    	  if (m_collationUri == null) {
    		  m_collationUri = XPathCollationSupport.UNICODE_CODEPOINT_COLLATION_URI;   
    	  }
    	  
    	  xpathRawResult = new ForEachGroupCompositeGroupingKey(xctxt, (ResultSequence)xpathEvalResult, 
    			                                                									m_collationUri, m_xpathCollationSupport);
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
