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

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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
import org.apache.xml.dtm.DTMManager;
import org.apache.xml.utils.NodeVector;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathCollationSupport;
import org.apache.xpath.XPathContext;
import org.apache.xpath.axes.NodeCursor;
import org.apache.xpath.functions.FuncReverse;
import org.apache.xpath.functions.context.FuncPosition;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XBooleanStatic;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;
import org.apache.xpath.operations.Variable;
import org.apache.xpath.types.XslForEachGroupCompositeGroupingKey;
import org.apache.xpath.types.StringWithCollation;
import org.w3c.dom.Node;

import xml.xpath31.processor.types.XSAnyURI;
import xml.xpath31.processor.types.XSBoolean;
import xml.xpath31.processor.types.XSDate;
import xml.xpath31.processor.types.XSDateTime;
import xml.xpath31.processor.types.XSDecimal;
import xml.xpath31.processor.types.XSDouble;
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
   * An XPath expression for 'select' attribute. 
   */
  private XPath m_selectExpression = null;
  
  /**
   * An XPath expression for 'group-by' attribute.
   */
  private XPath m_groupByExpression = null;  
  
  /**
   * An XPath expression for 'group-starting-with' attribute.
   */  
  private XPath m_groupStartingWithExpression = null;
  
  /**
   * An XPath expression for 'group-ending-with' attribute.
   */
  private XPath m_groupEndingWithExpression = null;
  
  /**
   * An XPath expression for 'group-adjacent' attribute.
   */
  private XPath m_groupAdjacentExpression = null;
  
  /**
   * An attribute 'composite''s boolean value.
   */
  private boolean m_composite;
  
  /**
   * The "collation" attribute value.
   */
  private AVT m_collationUri = null;
  
  /**
   * An XPathCollationSupport object value. 
   */
  private XPathCollationSupport m_xpathCollationSupport = null;
  
  /**
   * Vector containing the xsl:sort elements associated with this element.
   */
  private Vector m_sortElems = null;
  
  /**
   * Class field, that represents the value of "xpath-default-namespace" 
   * attribute.
   */
  private String m_xpath_default_namespace = null;
  
  /**
   * Class field, that represents the value of "expand-text" 
   * attribute.
   */
  private boolean m_expand_text;
  
  /**
   * An XPath expression for 'use-when' attribute. 
   */
  private XPath m_useWhen = null;
  
  /**
   * Class field, that represents xsl:for-each-group instruction's
   * sorted list of groups.
   */
  private List<GroupingKeyAndGroupPair> m_sortedGroups = null;
  
  private String m_collation_uri_from_context = null;
  
  /**
   * Class constructor.
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
   * Set the value of "xpath-default-namespace" attribute.
   *
   * @param v   Value of the "xpath-default-namespace" attribute
   */
  public void setXpathDefaultNamespace(String v)
  {
	  m_xpath_default_namespace = v; 
  }

  /**
   * Get the value of "xpath-default-namespace" attribute.
   *  
   * @return		  The value of "xpath-default-namespace" attribute 
   */
  public String getXpathDefaultNamespace() {
	  return m_xpath_default_namespace;
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
      m_groupByExpression = xpath;
  }
  
  /**
   * Get the "group-by" attribute.
   *
   * @return The XPath expression for the "group-by" attribute.
   */
  public XPath getGroupBy()
  {
      return m_groupByExpression;
  }
  
  /**
   * Set the "group-starting-with" attribute.
   *
   * @param xpath The XPath expression for the "group-starting-with" attribute.
   */
  public void setGroupStartingWith(XPath xpath)
  {
      m_groupStartingWithExpression = xpath;   
  }
  
  /**
   * Get the "group-starting-with" attribute.
   *
   * @return The XPath expression for the "group-starting-with" attribute.
   */
  public XPath getGroupStartingWith()
  {
      return m_groupStartingWithExpression;
  }
  
  /**
   * Set the "group-ending-with" attribute.
   *
   * @param xpath The XPath expression for the "group-ending-with" attribute.
   */
  public void setGroupEndingWith(XPath xpath)
  {
      m_groupEndingWithExpression = xpath;   
  }
  
  /**
   * Get the "group-ending-with" attribute.
   *
   * @return The XPath expression for the "group-ending-with" attribute.
   */
  public XPath getGroupEndingWith()
  {
      return m_groupEndingWithExpression;
  }
  
  /**
   * Set the "group-adjacent" attribute.
   *
   * @param xpath The XPath expression for the "group-adjacent" attribute.
   */
  public void setGroupAdjacent(XPath xpath)
  {
      m_groupAdjacentExpression = xpath;   
  }
  
  /**
   * Get the "group-adjacent" attribute.
   *
   * @return The XPath expression for the "group-adjacent" attribute.
   */
  public XPath getGroupAdjacent()
  {
      return m_groupAdjacentExpression;
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
  public void setCollation(AVT collationUri)
  {
	  m_collationUri = collationUri;   
  }
  
  /**
   * Get the "collation" attribute.
   *
   * @return   String value of the "collation" attribute.
   */
  public AVT getCollation()
  {
      return m_collationUri;
  }
  
  /**
   * Variable to indicate whether, an attribute 'expand-text'
   * is declared on xsl:for-each-group instruction.
   */
  private boolean m_expand_text_declared;

  /**
   * Set the value of "expand-text" attribute.
   *
   * @param v   Value of the "expand-text" attribute
   */
  public void setExpandText(boolean v)
  {
	  m_expand_text = v;
	  m_expand_text_declared = true;
  }

  /**
   * Get the value of "expand-text" attribute.
   *  
   * @return		  The value of "expand-text" attribute 
   */
  public boolean getExpandText() {
	  return m_expand_text;
  }
  
  /**
   * Get a boolean value indicating whether, an "expand-text" 
   * attribute has been declared. 
   */
  public boolean getExpandTextDeclared() {
	  return m_expand_text_declared;
  }
  
  /**
   * Method definition, to set the value of XSL attribute 
   * "use-when".
   * 
   * @param xpath            XPath expression for attribute "use-when"
   */
  public void setUseWhen(XPath xpath)
  {
	  m_useWhen = xpath;  
  }

  /**
   * Method definition, to get the value of XSL attribute 
   * "use-when".
   * 
   * @return			XPath expression for attribute "use-when"
   */
  public XPath getUseWhen()
  {
	  return m_useWhen;
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
        }
        
        if (m_groupByExpression != null) {
        	m_groupByExpression.fixupVariables(vnames, sroot.getComposeState().getGlobalsSize());
        }
        
        if (m_groupStartingWithExpression != null) {
        	m_groupStartingWithExpression.fixupVariables(vnames, sroot.getComposeState().getGlobalsSize());
        }
        
        if (m_groupEndingWithExpression != null) {
        	m_groupEndingWithExpression.fixupVariables(vnames, sroot.getComposeState().getGlobalsSize());
        }
        
        if (m_groupAdjacentExpression != null) {
        	m_groupAdjacentExpression.fixupVariables(vnames, sroot.getComposeState().getGlobalsSize());
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
   * @return           The token id for this element
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
        	XPathContext xctxt = transformer.getXPathContext();
        	
        	SourceLocator srcLocator = xctxt.getSAXLocator();
        	
        	final int sourceNode = xctxt.getCurrentNode();
        	
        	StylesheetRoot stylesheetRoot = transformer.getStylesheet();
        	m_collation_uri_from_context = stylesheetRoot.getCollationUri();
        	
        	if (m_useWhen != null) {
        		boolean result1 = isXPathExpressionStatic(m_useWhen.getExpression());
            	if (result1) {
            		XObject useWhenResult = m_useWhen.execute(xctxt, sourceNode, xctxt.getNamespaceContext());
            		if (useWhenResult.bool()) {
            			transformSelectedNodes(transformer);
            		}
            	}
            	else {
            		throw new TransformerException("XPST0008 : XSL variables other than XSLT static variables/parameters, cannot be "
                            																									+ "used within XPath static expression.", srcLocator);
            	}
        	}
        	else {
                transformSelectedNodes(transformer);
        	}
            
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
   * @param xctxt             An XPath context object
   * @param sortKeys          Vector of sort keys
   * @param forEachGroups     An object instance representing, groups 
   *                          to be sorted.
   *
   * @return                  void
   *
   * @throws TransformerException
   */
  public void sortGroups(XPathContext xctxt, Vector sortKeys, Object forEachGroups)
                                                                                throws TransformerException {      
        ForEachGroupXslSortSorter sorter = new ForEachGroupXslSortSorter(xctxt, this);
        
        m_sortedGroups = sorter.sort(forEachGroups, sortKeys, xctxt);
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
        int type = ((ElemTemplateElement)newChild).getXSLToken();
        
        if (type == Constants.ELEMNAME_SORT) {
        	ElemSort elemSort = (ElemSort)newChild;        
        	setSortElem(elemSort);
        }
        
        return super.appendChild(newChild);
  }
  
  /**
   * Call the children visitors.
   * 
   * @param visitor The visitor whose appropriate method will be called.
   */
  public void callChildVisitors(XSLTVisitor visitor, boolean callAttributes)
  {
      	if (callAttributes && (m_selectExpression != null))
      		m_selectExpression.callVisitors(this, visitor);
      		
        int length = getSortElemCount();
    
        for (int idx = 0; idx < length; idx++)
        {
           (getSortElem(idx)).callVisitors(visitor);
        }
    
        super.callChildVisitors(visitor, callAttributes);
  }

  /**
   * @see ExpressionOwner#getExpression()
   */
  public Expression getExpression()
  {
	  return m_selectExpression.getExpression();
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
   * Method definition, to do xsl:for-each-group instruction's XSL transformation.
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
        
        SourceLocator srcLocator = xctxt.getSAXLocator();
        
        int forEachGroupGroupingAttributesCount = getForEachGroupGroupingAttributesCount();
        
        if (forEachGroupGroupingAttributesCount == 0) {
            throw new TransformerException("XTSE1080 : None of the attributes 'group-by', 'group-adjacent', "
										                                              + "'group-starting-with', 'group-ending-with' is present on "
										                                              + "XSL for-each-group instruction.", srcLocator);     
        }
         
        if (forEachGroupGroupingAttributesCount > 1) {
           throw new TransformerException("XTSE1080 : Only one of the attributes 'group-by', 'group-adjacent', "
									                                                 + "'group-starting-with', 'group-ending-with' can be "
									                                                 + "present on XSL for-each-group instruction.", srcLocator);     
        }
        
        if (m_groupStartingWithExpression != null) {
        	try {
        	   // Verify that, xsl:for-each-group instruction's attribute group-starting-with's 
        	   // value has a syntax conforming to an XPath pattern. 
        	   XPath groupStartingWithXPath = new XPath(m_groupStartingWithExpression.getPatternString(), srcLocator, 
        			                                                                                    xctxt.getNamespaceContext(), XPath.MATCH, null);
        	}
        	catch (TransformerException ex) {
        		String errMessageStr = "XTSE0340 : An XSL for-each-group instruction attribute group-starting-with's "
        				                                                                   + "value has syntax error. " + ex.getMessage() 
        				                                                                   + ". This value must conform to an XPath pattern.";
        		throw new TransformerException(errMessageStr, srcLocator); 
        	}
        	
        	if ((m_collationUri != null) || m_composite) {
        	    throw new TransformerException("XTSE1090 : An XSL for-each-group instruction cannot specify "
			        	   		                                                           + "attributes 'collation' or 'composite' "
			        	   		                                                           + "except with group-by/adjacent.", srcLocator);
        	}
        }
        
        if (m_groupEndingWithExpression != null) {
        	try {
        	   // Verify that, xsl:for-each-group instruction's attribute group-ending-with's 
         	   // value has a syntax conforming to an XPath pattern.
        	   XPath groupEndingWithXPath = new XPath(m_groupEndingWithExpression.getPatternString(), srcLocator, 
        			                                                                                xctxt.getNamespaceContext(), XPath.MATCH, null);
        	}
        	catch (TransformerException ex) {
        		String errMessageStr = "XTSE0340 : An XSL for-each-group instruction's attribute group-ending-with's "
																                          + "value has syntax error. " + ex.getMessage() 
																                          + ". This value must conform to an XPath pattern.";
        		
        		throw new TransformerException(errMessageStr, srcLocator); 
        	}
        	
        	if ((m_collationUri != null) || m_composite) {
         	    throw new TransformerException("XTSE1090 : An XSL for-each-group instruction cannot specify "
			         	   		                                                          + "attributes 'collation' or 'composite' "
			         	   		                                                          + "except with group-by/adjacent.", srcLocator);
         	}
        }
        
        if (m_xpath_default_namespace != null) {
        	// Recompile xsl:for-each-group instruction's attributes, to consider 
        	// XSL stylesheet namespace xpath-default-namespace. 
        	
        	m_selectExpression = new XPath(m_selectExpression.getPatternString(), srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);

        	if (m_groupByExpression != null) {
        		m_groupByExpression = new XPath(m_groupByExpression.getPatternString(), srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null); 
        	}

        	if (m_groupStartingWithExpression != null) {
        		m_groupStartingWithExpression = new XPath(m_groupStartingWithExpression.getPatternString(), srcLocator, 
        																								xctxt.getNamespaceContext(), XPath.SELECT, null); 
        	}

        	if (m_groupEndingWithExpression != null) {
        		m_groupEndingWithExpression = new XPath(m_groupEndingWithExpression.getPatternString(), srcLocator, 
        																								xctxt.getNamespaceContext(), XPath.SELECT, null); 
        	}

        	if (m_groupAdjacentExpression != null) {
        		m_groupAdjacentExpression = new XPath(m_groupAdjacentExpression.getPatternString(), srcLocator, 
        																							xctxt.getNamespaceContext(), XPath.SELECT, null); 
        	}
        }
        
        final int currentNode = xctxt.getCurrentNode();
        
        DTMCursorIterator sourceNodes = null;
        
        Expression selectExpr = m_selectExpression.getExpression();                
        
        XObject selectExprResult = null;
        
        if (selectExpr instanceof Variable) {
            selectExprResult = ((Variable)selectExpr).execute(xctxt);                        
        }
        else {
        	selectExprResult = selectExpr.execute(xctxt);
        }
        
        boolean isInpSeqAllAtomicValues = true;
        
        if (selectExprResult instanceof ResultSequence) {
        	ResultSequence resultSeq = (ResultSequence)selectExprResult;
        	
        	if (!XslTransformEvaluationHelper.isSequenceContainsAllXdmAtomicValues(resultSeq)) {
        		// We assume here that, an xsl:for-each-group's input sequence has all 
        		// values as nodes.        		
        		isInpSeqAllAtomicValues = false;
        		
        		sourceNodes = getSourceNodesFromResultSequence(resultSeq, xctxt);
        	}
        	else {
        		DTMManager dtmManager = (DTMManager)xctxt;

        		// Construct an XML DOM wrapper over a sequence of input 
        		// atomic values, for the purpose of grouping
        		DTM dtm = dtmManager.getXmlDTMTreeFromResultSequence(resultSeq);

        		int docNodeHandle = dtm.getDocument();
        		int topMostElemNodeHandle = dtm.getFirstChild(docNodeHandle);
        		int child = dtm.getFirstChild(topMostElemNodeHandle);

        		// This variable shall contain a sequence of text nodes
        		ResultSequence wrapperResultSeq = new ResultSequence();

        		while (child != DTM.NULL) {            	  
        			int textNodeHandle = dtm.getFirstChild(child);
        			XMLNodeCursorImpl xmlNodeCursorImpl = new XMLNodeCursorImpl(textNodeHandle, dtmManager);
        			xmlNodeCursorImpl.m_is_for_each_group = true;
        			wrapperResultSeq.add(xmlNodeCursorImpl);
        			child = dtm.getNextSibling(child); 
        		}

        		sourceNodes = getSourceNodesFromResultSequence(wrapperResultSeq, xctxt);
        	}
        }
        else if (selectExprResult instanceof XMLNodeCursorImpl) {
        	isInpSeqAllAtomicValues = false;
        	
        	XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)selectExprResult;
            
            sourceNodes = xmlNodeCursorImpl.iter();
        }
        else {
        	throw new TransformerException("XPTY0004 : An XSL for-each-group's 'select' expression didn't evaluate "
        			                                                        + "to a sequence or nodeset that could be grouped.", srcLocator);
        }
        
        m_xpathCollationSupport = xctxt.getXPathCollationSupport();
        
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
        
        if (m_groupByExpression != null) {
        	constructGroupsForGroupBy(xctxt, sourceNodes, xslForEachGroupMap);
        }        
        else if (m_groupStartingWithExpression != null) {
        	boolean isReverse = (selectExpr instanceof FuncReverse);
        	constructGroupsForGroupStartingWith(xctxt, sourceNodes, xslForEachGroupStartingWithEndingWith, isInpSeqAllAtomicValues, isReverse);
        }
        else if (m_groupEndingWithExpression != null) {
        	boolean isReverse = (selectExpr instanceof FuncReverse);
        	constructGroupsForGroupEndingWith(xctxt, sourceNodes, xslForEachGroupStartingWithEndingWith, isInpSeqAllAtomicValues, isReverse);
        }
        else if (m_groupAdjacentExpression != null) {
        	constructGroupsForGroupAdjacent(xctxt, sourceNodes, xslForEachGroupAdjacentList, isInpSeqAllAtomicValues);
        }
        
        try {
        	xctxt.pushCurrentNode(DTM.NULL);
        	xctxt.pushCurrentExpressionNode(DTM.NULL);

        	xctxt.pushSAXLocatorNull();
        	xctxt.pushContextNodeList(sourceNodes);
        	transformer.pushElemTemplateElement(null);
        	
        	if (m_sortElems != null) {
        	   int sortElemCount = m_sortElems.size();
        	   for (int idx = 0; idx < sortElemCount; idx++) {
        		  ElemSort elemSort = (ElemSort)(m_sortElems.elementAt(idx));
        		  AVT stableAvt = elemSort.getStable();
        		  if (stableAvt != null) {
        			 String stableValue = stableAvt.evaluate(xctxt, currentNode, xctxt.getNamespaceContext());
        			 stableValue = stableValue.trim();
        			 if (!("yes".equals(stableValue) || "1".equals(stableValue) || "true".equals(stableValue) 
																	        					 || "no".equals(stableValue) || "0".equals(stableValue) 
																	        					 || "false".equals(stableValue))) {
        				 throw new javax.xml.transform.TransformerException("XTSE0020 : An XSL 'sort' instruction attribute 'stable''s value " + 
																			        						 stableValue + " is not valid. The allowed "
																			        						 + "values for attribute 'stable' are yes,1,true,no,0,false.", 
																			        						 elemSort);
        			 }
        			 
        			 if (idx > 0) {
        				 throw new javax.xml.transform.TransformerException("XTSE1017 : An XSL for-each-group instruction can specify, "
        				 		                                                                          + "attribute 'stable' only on for-each-group's "
        				 		                                                                          + "first XSL sort.", elemSort); 
        			 }
        		  }
        	   }
        	}

        	final Vector sortKeys = (m_sortElems == null) ? null : transformer.processSortKeysForEachGroup(
        			                                                                                    this, currentNode);            
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

        		sortGroups(xctxt, sortKeys, forEachGroups);
        		
        		int xslGroupCount = m_sortedGroups.size();

        		for (int idx = 0; idx < xslGroupCount; idx++) {
        			GroupingKeyAndGroupPair groupingKeyAndGroupPair = m_sortedGroups.get(idx);
        			Object groupingKey = groupingKeyAndGroupPair.getGroupingKey();
        			List<Integer> groupNodesDtmHandles = groupingKeyAndGroupPair.getGroupNodeDtmHandles();

        			xctxt.setPos(idx + 1);    							// Set value of fn:position() function within xsl:for-each-group
        			xctxt.setLast(m_sortedGroups.size());               // Set value of the number of groups formed

        			try {
        				/**
        				 * Set context item for whole of group contents evaluation, to the initial
        				 * item of the group.
        				 */
        				xctxt.pushCurrentNode((groupNodesDtmHandles.get(0)).intValue());

        				for (ElemTemplateElement templateElem = this.m_firstChild; templateElem != null; 
        						                                                            templateElem = templateElem.m_nextSibling) {        				
        					templateElem.setGroupingKey(groupingKey);
        					templateElem.setGroupNodesDtmHandles(groupNodesDtmHandles);
        					xctxt.setSAXLocator(templateElem);
        					transformer.setCurrentElement(templateElem);                   
        					templateElem.execute(transformer);
        				}
        			}
        			finally {
        				xctxt.popCurrentNode();
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
        			
        			int size1 = groupingKeyAndNodeHandlePairList.size();        			
        			for (int idx = 0; idx < size1; idx++) {
        				GroupingKeyAndNodeHandlePair groupingKeyNodeHandlePair = groupingKeyAndNodeHandlePairList.get(idx);

        				Object groupingKey = groupingKeyNodeHandlePair.getGroupingKey();              // current-grouping-key() value, for this group
        				List<Integer> groupNodesDtmHandles = xslForEachGroupMap.get(groupingKey);     // current-group() contents, for this group

        				xctxt.setPos(idx + 1);										      // Set value of fn:position() function within xsl:for-each-group
        				xctxt.setLast(size1);		                                      // Set value of the number of groups formed                        

        				try {
        					/**
        					 * Set context item for whole of group contents evaluation, to the
        					 * initial item of the group.
        					 */
        					xctxt.pushCurrentNode((groupNodesDtmHandles.get(0)).intValue());

        					for (ElemTemplateElement templateElem = this.m_firstChild; templateElem != null; 
        							                                                             templateElem = templateElem.m_nextSibling) {        					
        						templateElem.setGroupingKey(groupingKey);
        						templateElem.setGroupNodesDtmHandles(groupNodesDtmHandles);
        						xctxt.setSAXLocator(templateElem);
        						transformer.setCurrentElement(templateElem);                   
        						templateElem.execute(transformer);
        					}
        				}
        				finally {
        					xctxt.popCurrentNode();
        				}
        			}
        		}
        		else if (xslForEachGroupAdjacentList.size() > 0) {                	
        			/**
        			 * Loop through the groups formed by xsl:for-each-group instruction using 
        			 * 'group-adjacent' attribute, and process the XSL contents of each group.
        			 */
        			
        			int size1 = xslForEachGroupAdjacentList.size();
        			for (int idx = 0; idx < size1; idx++) {
        				GroupingKeyAndGroupPair groupingKeyAndGroupPair = xslForEachGroupAdjacentList.get(idx);

        				Object groupingKey = groupingKeyAndGroupPair.getGroupingKey();                             // current-grouping-key() value, for this group
        				List<Integer> groupNodesDtmHandles = groupingKeyAndGroupPair.getGroupNodeDtmHandles();     // current-group() contents, for this group

        				xctxt.setPos(idx + 1);										                   // Set value of fn:position() function within xsl:for-each-group
        				xctxt.setLast(size1);		                                                   // Set value of the number of groups formed                        

        				try {
        					/**
        					 * Set context item for whole of group contents evaluation, to the
        					 * initial item of the group.
        					 */
        					xctxt.pushCurrentNode((groupNodesDtmHandles.get(0)).intValue());

        					for (ElemTemplateElement templateElem = this.m_firstChild; templateElem != null; 
        							                                                            templateElem = templateElem.m_nextSibling) {        					
        						templateElem.setGroupingKey(groupingKey);
        						templateElem.setGroupNodesDtmHandles(groupNodesDtmHandles);
        						setIsGroupingXdmAtomicValues(isInpSeqAllAtomicValues);
        						xctxt.setSAXLocator(templateElem);
        						transformer.setCurrentElement(templateElem);                   
        						templateElem.execute(transformer);
        					}
        				}
        				finally {
        					xctxt.popCurrentNode();
        				}
        			}
        		}
        		else {
        			/**
        			 * Process the groups, when xsl:for-each-group attributes 'group-starting-with' or
        			 * 'group-ending-with' are used. Loop through these groups formed by xsl:for-each-group
        			 * instruction, and process the XSL contents of each group.
        			 */

        			int size1 = xslForEachGroupStartingWithEndingWith.size();
        			for (int idx = 0; idx < size1; idx++) {
        				List<Integer> groupNodesDtmHandles = xslForEachGroupStartingWithEndingWith.get(idx);

        				xctxt.setPos(idx + 1);											// Set value of fn:position() function within xsl:for-each-group
        				xctxt.setLast(size1);	                                        // Set value of the number of groups formed                       

        				try {
        					/**
        					 * Set context item for whole of group contents evaluation, to the initial
        					 * item of the group.
        					 */
        					xctxt.pushCurrentNode((groupNodesDtmHandles.get(0)).intValue());

        					for (ElemTemplateElement templateElem = this.m_firstChild; templateElem != null; 
        							                                                             templateElem = templateElem.m_nextSibling) {                           					
        						/**
        						 * The grouping key is absent when, attributes 'group-starting-with' or
        						 * 'group-ending-with' are used.
        						 */
        						templateElem.setGroupingKey(FuncCurrentGroupingKey.XSL_GROUPING_KEY_ABSENT);

        						templateElem.setGroupNodesDtmHandles(groupNodesDtmHandles);
        						xctxt.setSAXLocator(templateElem);
        						transformer.setCurrentElement(templateElem);                   
        						templateElem.execute(transformer);
        					}
        				}
        				finally {
        					xctxt.popCurrentNode();
        				}
        			}
        		}
        	}
        }
        finally {
        	if (transformer.getDebug()) {
        		transformer.getTraceManager().emitSelectedEndEvent(currentNode, this,
        														   "select", m_selectExpression,
        														   	new org.apache.xpath.objects.XMLNodeCursorImpl(sourceNodes));
        	}

        	xctxt.popSAXLocator();
        	xctxt.popContextNodeList();
        	transformer.popElemTemplateElement();
        	xctxt.popCurrentExpressionNode();
        	xctxt.popCurrentNode();
        	xctxt.setPos(0);
        	xctxt.setLast(0);
        	sourceNodes.detach();
        }
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
	  	  
	  try {
		  int pos = 0;
		  
		  while ((nextNode = sourceNodes.nextNode()) != DTM.NULL) {
			  FuncPosition.m_forEachGroupGroupByPos = ++pos; 
			  XObject xpathEvalResult = m_groupByExpression.execute(xctxt, nextNode, xctxt.getNamespaceContext());
			  Object groupingKeyValue = getNormalizedGroupingKeyValue(xctxt, xpathEvalResult);

			  if (!m_composite) {
				  addXdmNodeHandleToGroup(xslForEachGroupByMap, nextNode, groupingKeyValue);
			  }
			  else {
				  // Processing for xsl:for-each-group's composite grouping key
				  if (groupingKeyValue instanceof XslForEachGroupCompositeGroupingKey) {
					  XslForEachGroupCompositeGroupingKey groupingKeyObj = (XslForEachGroupCompositeGroupingKey)groupingKeyValue;
					  ResultSequence groupingKeySeq = groupingKeyObj.getValue();
					  if (groupingKeySeq.size() >= 1) {
						  addXdmNodeHandleToGroup(xslForEachGroupByMap, nextNode, groupingKeyValue);
					  }
					  else {
						  throw new TransformerException("XTSE1080 : An XSL for-each-group instruction with attribute "
																										  + "'composite=\"yes\"', resulted in a grouping key "
																										  + "sequence that is empty.", srcLocator);
					  }
				  }
				  else {
					  addXdmNodeHandleToGroup(xslForEachGroupByMap, nextNode, groupingKeyValue); 
				  }
			  }
		  }
	  }
	  finally {	  
	      FuncPosition.m_forEachGroupGroupByPos = 0;
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
   * @param isInpSeqAllAtomicValues             Boolean value indicating whether an XDM input
   *                                            sequence to be grouped has all items as atomic
   *                                            values.
   * @param isReverse                           Boolean value indicating, whether nodeset has been
		   *                                    constructed as a result of XPath function fn:reverse.                                            
   * @throws TransformerException
   */
  private void constructGroupsForGroupStartingWith(XPathContext xctxt, DTMCursorIterator sourceNodes,
		                                                                             List<List<Integer>> xslForEachGroupStartingWith, 
		                                                                             boolean isInpSeqAllAtomicValues, boolean isReverse) throws TransformerException {	  
	  
	  List<Integer> allNodeHandleList = new ArrayList<Integer>();	  
	  
	  List<Integer> grpStartNodeHandles = new ArrayList<Integer>();	  	  
	  	  
	  if (!isInpSeqAllAtomicValues) {
		  int idx = 0;
		  int nextNode1;
		  while ((nextNode1 = sourceNodes.nextNode()) != DTM.NULL) {
			  allNodeHandleList.add(Integer.valueOf(nextNode1));
			  DTM dtm = xctxt.getDTM(nextNode1);		  
			  if (idx == 0) {
				  int parentNode = dtm.getParent(nextNode1);			  
				  XObject groupStartingWithEvalResult = m_groupStartingWithExpression.execute(xctxt, parentNode, xctxt.getNamespaceContext());
				  XMLNodeCursorImpl grpStartingWithNodeInit = (XMLNodeCursorImpl)groupStartingWithEvalResult;
				  DTMCursorIterator dtmCursorIter = grpStartingWithNodeInit.getContainedIter();
				  int nextNode2;
				  while ((nextNode2 = dtmCursorIter.nextNode()) != DTM.NULL) {            	 
					  grpStartNodeHandles.add(Integer.valueOf(nextNode2)); 
				  }				  		  			  
			  }
			  
			  idx++;
		  }
		  
		  if (isReverse) {
			  int size1 = grpStartNodeHandles.size();
			  List<Integer> nodeHandleReversed = new ArrayList<Integer>();
			  for (int idx1 = (size1 - 1); idx1 >= 0; idx1--) {
				  nodeHandleReversed.add(grpStartNodeHandles.get(idx1)); 
			  }
			  
			  grpStartNodeHandles = nodeHandleReversed; 
		  }
		  
		  if (grpStartNodeHandles.size() > 0) {
			  /**
			   * Adding the first group which may be present, before the
			   * first XML group-starting-with matching xdm node, to the
			   * result.
			   */
			  int nodeId = grpStartNodeHandles.get(0);
			  int size1 = allNodeHandleList.size();
			  List<Integer> groupNodeHandles = new ArrayList<Integer>();
			  for (int idx1 = 0; idx1 < size1; idx1++) {
				  int nodeHandle = allNodeHandleList.get(idx1);
				  if (nodeHandle != nodeId) {
					  groupNodeHandles.add(nodeHandle);
				  }
				  else {
					  if (groupNodeHandles.size() > 0) {
						  xslForEachGroupStartingWith.add(groupNodeHandles);
					  }

					  break;
				  }
			  }
		  }
	  }
	  else {
		  int nextNode3;
		  while ((nextNode3 = sourceNodes.nextNode()) != DTM.NULL) {
			  allNodeHandleList.add(Integer.valueOf(nextNode3));
			  DTM dtm = xctxt.getDTM(nextNode3);
			  Node node = dtm.getNode(nextNode3);
			  String nodeStrValue = node.getNodeValue();

			  Double dblValue = null;
			  XObject xObj = null;
			  try {
				  dblValue = Double.valueOf(nodeStrValue);
			  }
			  catch (NumberFormatException ex) {
				  // no op 
			  }

			  if (dblValue != null) {
				  xObj = new XSDecimal(BigDecimal.valueOf(dblValue));
			  }
			  else {
				  xObj = new XSString(nodeStrValue);;  
			  }

			  xctxt.setXPath3ContextItem(xObj);				  
			  XObject xObjResult = m_groupStartingWithExpression.execute(xctxt, DTM.NULL, xctxt.getNamespaceContext());
			  if ((xObjResult instanceof XBooleanStatic) || (xObjResult instanceof XBoolean) || (xObjResult instanceof XSBoolean)) {
				  if (xObjResult.bool()) {
					 grpStartNodeHandles.add(Integer.valueOf(nextNode3)); 
				  }
			  }
		  }
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

	  // Getting node handles of the effective last group, for 
	  // xsl:for-each-group's group-starting-with attribute.
	  
	  int noOfGroups = xslForEachGroupStartingWith.size();
	  if (noOfGroups > 0) {
		  List<Integer> lastGroup = xslForEachGroupStartingWith.get(noOfGroups - 1);
		  int lastGroupSize = lastGroup.size();
		  int lastNodeHandleOfLastGroup = lastGroup.get(lastGroupSize - 1);

		  List<Integer> groupNodeHandles = new ArrayList<Integer>();
		  for (int idx2 = 0; idx2 < allNodeHandleList.size(); idx2++) {            	   
			  if (lastNodeHandleOfLastGroup < allNodeHandleList.get(idx2)) {
				  groupNodeHandles.add(allNodeHandleList.get(idx2));  
			  }
		  }
		  
		  if (groupNodeHandles.size() > 0) {
			  xslForEachGroupStartingWith.add(groupNodeHandles);
		  }
	  }	  
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
   * @param isReverse                           Boolean value indicating, whether nodeset has been
   *                                            constructed as a result of XPath function fn:reverse.
   * @throws TransformerException
   */
  private void constructGroupsForGroupEndingWith(XPathContext xctxt, DTMCursorIterator sourceNodes,
		  									                                       List<List<Integer>> xslForEachGroupEndingWith, 
		  									                                                     boolean isInpSeqAllAtomicValues, 
		  									                                                     boolean isReverse) throws TransformerException {
	  
	  List<Integer> allNodeHandleList = new ArrayList<Integer>();
	  
	  List<Integer> grpEndNodeHandles = new ArrayList<Integer>();
	  	  	  
	  if (!isInpSeqAllAtomicValues) {
		  int idx = 0;
		  int nextNode1;
		  while ((nextNode1 = sourceNodes.nextNode()) != DTM.NULL) {
			  allNodeHandleList.add(Integer.valueOf(nextNode1));		  
			  DTM dtm = xctxt.getDTM(nextNode1);
			  if (idx == 0) {
				  int parentNode = dtm.getParent(nextNode1);
				  XObject groupStartingWithEvalResult = m_groupEndingWithExpression.execute(xctxt, parentNode, xctxt.getNamespaceContext());
				  XMLNodeCursorImpl grpStartingWithNodeInit = (XMLNodeCursorImpl)groupStartingWithEvalResult;
				  DTMCursorIterator dtmCursorIter = grpStartingWithNodeInit.getContainedIter();
				  int nextNode2;
				  while ((nextNode2 = dtmCursorIter.nextNode()) != DTM.NULL) {            	 
					  grpEndNodeHandles.add(Integer.valueOf(nextNode2)); 
				  }
			  }

			  idx++;
		  }
		  
		  if (isReverse) {
			  int size1 = grpEndNodeHandles.size();
			  List<Integer> nodeHandleReversed = new ArrayList<Integer>();
			  for (int idx1 = (size1 - 1); idx1 >= 0; idx1--) {
				  nodeHandleReversed.add(grpEndNodeHandles.get(idx1)); 
			  }
			  
			  grpEndNodeHandles = nodeHandleReversed; 
		  }
	  }
	  else {
		  int nextNode2;
		  while ((nextNode2 = sourceNodes.nextNode()) != DTM.NULL) {
			  allNodeHandleList.add(Integer.valueOf(nextNode2));
			  DTM dtm = xctxt.getDTM(nextNode2);			  
			  Node node = dtm.getNode(nextNode2);
			  String nodeStrValue = node.getNodeValue();

			  Double dblValue = null;
			  XObject xObj = null;
			  try {
				  dblValue = Double.valueOf(nodeStrValue);
			  }
			  catch (NumberFormatException ex) {
				  // no op 
			  }

			  if (dblValue != null) {
				  xObj = new XSDecimal(BigDecimal.valueOf(dblValue));
			  }
			  else {
				  xObj = new XSString(nodeStrValue);;  
			  }

			  xctxt.setXPath3ContextItem(xObj);				  
			  XObject xObjResult = m_groupEndingWithExpression.execute(xctxt, DTM.NULL, xctxt.getNamespaceContext());
			  if ((xObjResult instanceof XBooleanStatic) || (xObjResult instanceof XBoolean) || (xObjResult instanceof XSBoolean)) {
				  if (xObjResult.bool()) {
					  grpEndNodeHandles.add(Integer.valueOf(nextNode2)); 
				  }
			  }
		  }
	  }
	  
	  int allNodeHandleSize = allNodeHandleList.size();	  
	  int grpEndNodeHandleSize = grpEndNodeHandles.size();
	  for (int idx = 0; idx < grpEndNodeHandleSize; idx++) {
		  int grpEndNodeHandle = grpEndNodeHandles.get(idx);
		  List<Integer> groupNodeHandles = new ArrayList<Integer>();		  
		  if (idx == 0) {
			  for (int idx2 = 0; idx2 < allNodeHandleSize; idx2++) {
				  int nodeHandle = allNodeHandleList.get(idx2);
				  if (nodeHandle <= grpEndNodeHandle) {
					  groupNodeHandles.add(nodeHandle);
				  }            			 
			  }
		  }
		  else {
			  List<Integer> latestGrpFormed = xslForEachGroupEndingWith.get(idx - 1);
			  int temp = latestGrpFormed.get(latestGrpFormed.size() - 1);
			  for (int idx2 = 0; idx2 < allNodeHandleSize; idx2++) {
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
	  
	  // Getting node handles of the last group, for xsl:for-each-group's 
	  // group-ending-with attribute.
	  
	  if (grpEndNodeHandleSize > 0) {
		  int grpEndNodeHandle = grpEndNodeHandles.get(grpEndNodeHandleSize - 1);
		  List<Integer> groupNodeHandles = new ArrayList<Integer>();
		  for (int idx2 = 0; idx2 < allNodeHandleSize; idx2++) {            	   
			  if (allNodeHandleList.get(idx2) > grpEndNodeHandle) {
				  groupNodeHandles.add(allNodeHandleList.get(idx2));  
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
   * @param isInpSeqAllAtomicValues 
   * @throws TransformerException
   */
  private void constructGroupsForGroupAdjacent(XPathContext xctxt, DTMCursorIterator sourceNodes,
		                                                                         List<GroupingKeyAndGroupPair> xslForEachGroupAdjacentList, 
		                                                                         boolean isInpSeqAllAtomicValues) throws TransformerException {
	 
	 Object prevGroupingKeyValue = null;
	 
	 SourceLocator srcLocator = xctxt.getSAXLocator(); 
	 
	 int idx = 0;
	 int nextNode;
	 
	 while ((nextNode = sourceNodes.nextNode()) != DTM.NULL) {
		 DTM dtm = xctxt.getDTM(nextNode);
		 XSString xsStr1 = null;
		 XObject xpathEvalResult = null;
		 if ((dtm.getNodeType(nextNode) == DTM.TEXT_NODE) && isInpSeqAllAtomicValues) {
			xsStr1 = new XSString(dtm.getNodeValue(nextNode));
			XObject prevCtxtItem = xctxt.getXPath3ContextItem();
			xctxt.setXPath3ContextItem(xsStr1);
			xpathEvalResult = m_groupAdjacentExpression.execute(xctxt, DTM.NULL, xctxt.getNamespaceContext());
			if ((xpathEvalResult instanceof ResultSequence) && (((ResultSequence)xpathEvalResult).size() == 0)) {
	        	throw new TransformerException("XTTE1100 : An XSL for-each-group instruction attribute "
	        			                                                                               + "'group-adjacent''s value is an empty sequence.", srcLocator);
	        }
			xctxt.setXPath3ContextItem(prevCtxtItem);
			if (xpathEvalResult instanceof ResultSequence) {
				ResultSequence rSeq = (ResultSequence)xpathEvalResult;
				int rSeqLength = rSeq.size();
				ResultSequence rSeq2 = new ResultSequence(); 
				for (int idx1 = 0; idx1 < rSeqLength; idx1++) {
					XObject xObj = rSeq.item(idx1);
					if (xObj instanceof XNumber) {
						XNumber xNumber = (XNumber)xObj;
						double dbl = xNumber.num();
						if (!((Double.valueOf(dbl)).isNaN())) {
							rSeq2.add(xObj);
						}
					}
					else if (xObj instanceof XSDouble) {
						XSDouble xsDouble = (XSDouble)xObj;
						double dbl = xsDouble.doubleValue();
						if (!((Double.valueOf(dbl)).isNaN())) {
							rSeq2.add(xObj);
						}
					}
					else {
						rSeq2.add(xObj);
					}
				}
				
				xpathEvalResult = rSeq2; 
			}						
		 }
		 else {
	        xpathEvalResult = m_groupAdjacentExpression.execute(xctxt, nextNode, xctxt.getNamespaceContext());
	        if ((xpathEvalResult instanceof ResultSequence) && (((ResultSequence)xpathEvalResult).size() == 0)) {
	        	throw new TransformerException("XTTE1100 : An XSL for-each-group instruction attribute "
	        			                                                                               + "'group-adjacent''s value is an empty sequence.", srcLocator);
	        }
		 }
	     
	     Object groupingKeyValue = getNormalizedGroupingKeyValue(xctxt, xpathEvalResult);
	     
	     if (m_composite) {
	    	 if (groupingKeyValue instanceof XslForEachGroupCompositeGroupingKey) {
				  XslForEachGroupCompositeGroupingKey groupingKeyObj = (XslForEachGroupCompositeGroupingKey)groupingKeyValue;
				  ResultSequence groupingKeySeq = groupingKeyObj.getValue();
				  if (groupingKeySeq.size() == 0) {
					  throw new TransformerException("XTSE1080 : An XSL for-each-group instruction with attribute "
																	                             + "'composite=\"yes\"', resulted in a grouping key "
																	                             + "sequence that is empty.", srcLocator);
				  }
			  } 
	     }
	     
	     Object currGroupingKeyValue = groupingKeyValue;
	     
	     //List<Integer> group = null;
	     
	     if (idx == 0) {
	         // This is the first XDM node being iterated, within this loop
	    	 List<Integer> group = new ArrayList<Integer>();
	         group.add(nextNode);
	         GroupingKeyAndGroupPair groupingKeyAndGroupPair = new GroupingKeyAndGroupPair(currGroupingKeyValue, group);
	         xslForEachGroupAdjacentList.add(groupingKeyAndGroupPair);
	         prevGroupingKeyValue = currGroupingKeyValue; 
	     }
	     else {
	        if (currGroupingKeyValue.equals(prevGroupingKeyValue)) {
	        	int currResultSize = xslForEachGroupAdjacentList.size();
	        	GroupingKeyAndGroupPair groupingKeyAndGroupPair = xslForEachGroupAdjacentList.get(currResultSize - 1);
	        	List<Integer> group = groupingKeyAndGroupPair.getGroupNodeDtmHandles(); 
	            group.add(nextNode);
	            groupingKeyAndGroupPair.setGroupNodesDtmHandles(group);
	            xslForEachGroupAdjacentList.set(currResultSize - 1, groupingKeyAndGroupPair);
	        }
	        else {
	        	List<Integer> group = new ArrayList<Integer>();
	            group.add(nextNode);
	            GroupingKeyAndGroupPair groupingKeyAndGroupPair = new GroupingKeyAndGroupPair(currGroupingKeyValue, group);
	            xslForEachGroupAdjacentList.add(groupingKeyAndGroupPair);
	        }
	        
	        prevGroupingKeyValue = currGroupingKeyValue;
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
        
        if (m_groupByExpression != null) {
           forEachGroupGroupingAttributesCount++;    
        }
        
        if (m_groupAdjacentExpression != null) {
           forEachGroupGroupingAttributesCount++;    
        }
        
        if (m_groupStartingWithExpression != null) {
           forEachGroupGroupingAttributesCount++;    
        }
        
        if (m_groupEndingWithExpression != null) {
           forEachGroupGroupingAttributesCount++;    
        }
        
        return forEachGroupGroupingAttributesCount;
  }
  
  /**
   * Method definition, to transform xsl:for-each-group instruction's grouping 
   * key value (which is the result of evaluating xsl:for-each-group's 'group-by' or 
   * 'group-adjacent' XPath expressions), into a normalized value of type 
   * java.lang.Object.
   */
  private Object getNormalizedGroupingKeyValue(XPathContext xctxt, XObject groupingKeyValue) throws TransformerException {
      
	  Object normalizedGroupingKeyValue = null;
	  
	  final int contextNode = xctxt.getCurrentNode();
	  
	  String collation = null;	  
	  if (m_collation_uri_from_context != null) {
		  collation = m_collation_uri_from_context;  
	  }
	  else if (m_collationUri != null) {
	      collation = m_collationUri.evaluate(xctxt, contextNode, xctxt.getNamespaceContext());
	  }
      
      if (groupingKeyValue instanceof XString) {
    	  if (collation == null) {
    		 normalizedGroupingKeyValue = groupingKeyValue.str();   
    	  }
    	  else {    		  
    	     normalizedGroupingKeyValue = new StringWithCollation(groupingKeyValue.str(), collation, m_xpathCollationSupport);
    	  }
      }
      else if (groupingKeyValue instanceof XSQName) {
    	  XSQName qName = (XSQName)groupingKeyValue;
    	  String localPart = qName.getLocalPart();
    	  String namespaceUri = qName.getNamespaceUri();
    	  normalizedGroupingKeyValue = localPart + ((namespaceUri == null) ? "" : ":" + namespaceUri); 
      }
      else if (groupingKeyValue instanceof XSString) {
    	  if (collation == null) {
    		 normalizedGroupingKeyValue = ((XSString)groupingKeyValue).stringValue();   
    	  }
    	  else {
             normalizedGroupingKeyValue = new StringWithCollation(((XSString)groupingKeyValue).stringValue(), collation, m_xpathCollationSupport);
    	  }
      }
      else if (groupingKeyValue instanceof XNumber) {
          normalizedGroupingKeyValue = Double.valueOf(((XNumber)groupingKeyValue).num());  
      }
      else if (groupingKeyValue instanceof XSNumericType) {
          String strVal = ((XSNumericType)groupingKeyValue).stringValue();
          normalizedGroupingKeyValue = Double.valueOf(strVal);
      }
      else if (groupingKeyValue instanceof XBooleanStatic) {
    	  normalizedGroupingKeyValue =  Boolean.valueOf(((XBooleanStatic)groupingKeyValue).bool());
      }
      else if (groupingKeyValue instanceof XBoolean) {
          normalizedGroupingKeyValue =  Boolean.valueOf(((XBoolean)groupingKeyValue).bool());     
      }      
      else if (groupingKeyValue instanceof XSBoolean) {
          normalizedGroupingKeyValue = Boolean.valueOf(((XSBoolean)groupingKeyValue).value());
      }
      else if ((groupingKeyValue instanceof XSDate) || (groupingKeyValue instanceof XSTime)) {
          normalizedGroupingKeyValue = groupingKeyValue;
      }      
      else if (groupingKeyValue instanceof XSDateTime) {
    	  XSDateTime dateTimeValue = (XSDateTime)groupingKeyValue;    	  
    	  String dateTimeStr = dateTimeValue.stringValue();
    	  if (dateTimeStr.contains("+")) {
    		  // xs:dateTime string value contains a non-UTC time zone suffix.
    		  // Normalize the xs:dateTime value to UTC time zone.
    		  ZoneId translateToTimeZone = ZoneId.of("UTC");
    		  OffsetDateTime offsetDateTime = OffsetDateTime.parse(dateTimeStr);
    		  ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(offsetDateTime.toInstant(), translateToTimeZone);
    		  DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    		  String formattedDateTimeStr = zonedDateTime.format(dateTimeFormatter);

    		  try {
    			  normalizedGroupingKeyValue = XSDateTime.parseDateTime(formattedDateTimeStr);
    		  } 
    		  catch (TransformerException ex) {
    			  throw ex;
    		  }
    	  }
    	  else {
    		  normalizedGroupingKeyValue = groupingKeyValue;  
    	  }

    	  return normalizedGroupingKeyValue;
      }  		  
      else if (groupingKeyValue instanceof XSAnyURI) {
    	  normalizedGroupingKeyValue = groupingKeyValue; 
      }
      else if (groupingKeyValue instanceof ResultSequence) {
    	  if (collation == null) {
    		  collation = XPathCollationSupport.UNICODE_CODEPOINT_COLLATION_URI;   
    	  }
    	  
    	  ResultSequence rSeq = (ResultSequence)groupingKeyValue;
    	  int rSeqLength = rSeq.size();
    	  ResultSequence rSeq1 = new ResultSequence();
    	  for (int i = 0; i < rSeqLength; i++) {
    		 XObject xObj1 = rSeq.item(i);
    		 if (xObj1 instanceof XMLNodeCursorImpl) {
    		    String strValue1 = XslTransformEvaluationHelper.getStrVal(xObj1);
    		    rSeq1.add(new XSString(strValue1));
    		 }
    		 else {
    			rSeq1.add(xObj1); 
    		 }
    	  }
    	  
    	  normalizedGroupingKeyValue = new XslForEachGroupCompositeGroupingKey(xctxt, rSeq1, collation, m_xpathCollationSupport);
      }
      else {
    	  // Any other data type for grouping key, is treated as string
    	  if (collation == null) {
    		  normalizedGroupingKeyValue = XslTransformEvaluationHelper.getStrVal(groupingKeyValue);   
    	  }
    	  else {
    		  normalizedGroupingKeyValue = new StringWithCollation(XslTransformEvaluationHelper.getStrVal(groupingKeyValue), collation, m_xpathCollationSupport);
    	  } 
      }
      
      return normalizedGroupingKeyValue;      
  }
  
  /**
   * Given a supplied ResultSequence object containing XDM nodes, construct
   * and return a corresponding DTMCursorIterator object instance.     
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
        throw new TransformerException("XTDE0555 : An error occured while performing grouping with XSL 'for-each-group' "
                                                                                            + "instruction.", xctxt.getSAXLocator());
     }
     
     return sourceNodes; 
  }

  public List<GroupingKeyAndGroupPair> getSortedGroups() {
	  return m_sortedGroups;
  }

  public void setSortedGroups(List<GroupingKeyAndGroupPair> sortedGroups) {
	  this.m_sortedGroups = sortedGroups;
  }

}
