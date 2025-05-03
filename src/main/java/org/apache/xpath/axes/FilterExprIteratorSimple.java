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
/*
 * $Id$
 */
package org.apache.xpath.axes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.Axis;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xml.dtm.DTMManager;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.VariableStack;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathArray;
import org.apache.xpath.objects.XPathInlineFunction;
import org.apache.xpath.objects.XPathMap;
import org.apache.xpath.types.ForEachGroupCompositeGroupingKey;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import xml.xpath31.processor.types.XSAnyAtomicType;

/**
 * Class to use for one-step iteration that doesn't have a predicate, and 
 * doesn't need to set the context.
 */
public class FilterExprIteratorSimple extends LocPathIterator
{
  static final long serialVersionUID = -6978977187025375579L;
  
  /** 
   * The contained expression. Should be non-null.
   */
  private Expression m_expr;

  /** The result of executing m_expr. Needs to be deep cloned on clone op. */
  transient private XMLNodeCursorImpl m_exprObj;

  private boolean m_mustHardReset = false;
  private boolean m_canDetachNodeset = true;

  /**
   * Create a FilterExprIteratorSimple object.
   *
   */
  public FilterExprIteratorSimple()
  {
    super(null);
  }
  
  /**
   * Create a FilterExprIteratorSimple object.
   *
   */
  public FilterExprIteratorSimple(Expression expr)
  {
    super(null);
    m_expr = expr;
  }
  
  /**
   * Initialize the context values for this expression
   * after it is cloned.
   *
   * @param context The XPath runtime context for this
   * transformation.
   */
  public void setRoot(int context, Object environment)
  {
  	super.setRoot(context, environment);
  	m_exprObj = executeFilterExpr(context, m_execContext, getPrefixResolver(), 
  	                  getIsTopLevel(), m_stackFrame, m_expr);
  }

  /**
   * Execute the expression. Meant for reuse by other FilterExpr iterators 
   * that are not derived from this object.
   */
  public static XMLNodeCursorImpl executeFilterExpr(int context, XPathContext xctxt, 
	  												PrefixResolver prefixResolver,
	  												boolean isTopLevel,
	  												int stackFrame,
	  												Expression expr)
    throws org.apache.xml.utils.WrappedRuntimeException
  {
    PrefixResolver savedResolver = xctxt.getNamespaceContext();
    XMLNodeCursorImpl result = null;

    try
    {
      xctxt.pushCurrentNode(context);
      xctxt.setNamespaceContext(prefixResolver);

      // The setRoot operation can take place with a reset operation, 
      // and so we may not be in the context of LocPathIterator#nextNode, 
      // so we have to set up the variable context, execute the expression, 
      // and then restore the variable context.

      if (isTopLevel)
      {
        VariableStack vars = xctxt.getVarStack();

        // These three statements need to be combined into one operation.
        int savedStart = vars.getStackFrame();
        vars.setStackFrame(stackFrame);
        
        XObject xObj = expr.execute(xctxt);
        Object obj1 = xObj.object();
        if (!(obj1 instanceof ForEachGroupCompositeGroupingKey)) {            
            result = (org.apache.xpath.objects.XMLNodeCursorImpl)expr.execute(xctxt);
        }
        else {
        	ForEachGroupCompositeGroupingKey forEachGroupCompositeGroupingKeyObj = (ForEachGroupCompositeGroupingKey)obj1;
            ResultSequence groupingKeySeq = forEachGroupCompositeGroupingKeyObj.getValue();
            result = getCompositeGroupingKeyNodeset(groupingKeySeq, xctxt);
        }
        
        
        result.setShouldCacheNodes(true);

        // These two statements need to be combined into one operation.
        vars.setStackFrame(savedStart);
      }
      else
        result = (org.apache.xpath.objects.XMLNodeCursorImpl) expr.execute(xctxt);

    }
    catch (javax.xml.transform.TransformerException se)
    {
      throw new org.apache.xml.utils.WrappedRuntimeException(se);
    }
    finally
    {
      xctxt.popCurrentNode();
      xctxt.setNamespaceContext(savedResolver);
    }
    return result;
  }
  
  /**
   *  Returns the next node in the set and advances the position of the
   * iterator in the set. After a NodeIterator is created, the first call
   * to nextNode() returns the first node in the set.
   *
   * @return  The next <code>Node</code> in the set being iterated over, or
   *   <code>null</code> if there are no more members in that set.
   */
  public int nextNode()
  {
  	if(m_foundLast)
  		return DTM.NULL;

    int next;

    if (null != m_exprObj)
    {
      m_lastFetched = next = m_exprObj.nextNode();
    }
    else
      m_lastFetched = next = DTM.NULL;

    // m_lastFetched = next;
    if (DTM.NULL != next)
    {
      m_pos++;
      return next;
    }
    else
    {
      m_foundLast = true;

      return DTM.NULL;
    }
  }
  
  /**
   * Detaches the walker from the set which it iterated over, releasing
   * any computational resources and placing the iterator in the INVALID
   * state.
   */
  public void detach()
  {  
    if(m_allowDetach)
    {
  		super.detach();
  		m_exprObj.detach();
  		m_exprObj = null;
    }
  }

  /**
   * This function is used to fixup variables from QNames to stack frame 
   * indexes at stylesheet build time.
   * @param vars List of QNames that correspond to variables.  This list 
   * should be searched backwards for the first qualified name that 
   * corresponds to the variable reference qname.  The position of the 
   * QName in the vector from the start of the vector will be its position 
   * in the stack frame (but variables above the globalsTop value will need 
   * to be offset to the current stack frame).
   */
  public void fixupVariables(java.util.Vector vars, int globalsSize)
  {
    super.fixupVariables(vars, globalsSize);
    m_expr.fixupVariables(vars, globalsSize);
  }

  /**
   * Get the inner contained expression of this filter.
   */
  public Expression getInnerExpression()
  {
    return m_expr;
  }

  /**
   * Set the inner contained expression of this filter.
   */
  public void setInnerExpression(Expression expr)
  {
    expr.exprSetParent(this);
    m_expr = expr;
  }

  /** 
   * Get the analysis bits for this walker, as defined in the WalkerFactory.
   * @return One of WalkerFactory#BIT_DESCENDANT, etc.
   */
  public int getAnalysisBits()
  {
    if (null != m_expr && m_expr instanceof PathComponent)
    {
      return ((PathComponent) m_expr).getAnalysisBits();
    }
    return WalkerFactory.BIT_FILTER;
  }

  /**
   * Returns true if all the nodes in the iteration well be returned in document 
   * order.
   * Warning: This can only be called after setRoot has been called!
   * 
   * @return true as a default.
   */
  public boolean isDocOrdered()
  {
    return m_exprObj.isDocOrdered();
  }

  class filterExprOwner implements ExpressionOwner
  {
    /**
    * @see ExpressionOwner#getExpression()
    */
    public Expression getExpression()
    {
      return m_expr;
    }

    /**
     * @see ExpressionOwner#setExpression(Expression)
     */
    public void setExpression(Expression exp)
    {
      exp.exprSetParent(FilterExprIteratorSimple.this);
      m_expr = exp;
    }

  }

  /**
   * This will traverse the heararchy, calling the visitor for 
   * each member.  If the called visitor method returns 
   * false, the subtree should not be called.
   * 
   * @param visitor The visitor whose appropriate method will be called.
   */
  public void callPredicateVisitors(XPathVisitor visitor)
  {
    m_expr.callVisitors(new filterExprOwner(), visitor);

    super.callPredicateVisitors(visitor);
  }

  /**
   * @see Expression#deepEquals(Expression)
   */
  public boolean deepEquals(Expression expr)
  {
    if (!super.deepEquals(expr))
      return false;

    FilterExprIteratorSimple fet = (FilterExprIteratorSimple) expr;
    if (!m_expr.deepEquals(fet.m_expr))
      return false;

    return true;
  }
  
  /**
   * Returns the axis being iterated, if it is known.
   * 
   * @return Axis.CHILD, etc., or -1 if the axis is not known or is of multiple 
   * types.
   */
  public int getAxis()
  {
  	if(null != m_exprObj)
    	return m_exprObj.getAxis();
    else
    	return Axis.FILTEREDLIST;
  }

  /**
   * Method definition to convert xsl:for-each-group's composite grouping key 
   * sequence to a node set.
   */
  private static XMLNodeCursorImpl getCompositeGroupingKeyNodeset(ResultSequence groupingKeySeq, DTMManager dtmMgr) 
  		                                                                                                        throws TransformerException {
      
	  XMLNodeCursorImpl nodeSet = null;

	  System.setProperty(org.apache.xml.utils.Constants.XML_DOCUMENT_BUILDER_FACTORY_KEY, 
			  																		org.apache.xml.utils.Constants.XML_DOCUMENT_BUILDER_FACTORY_VALUE);

	  try {
		  DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		  dbf.setNamespaceAware(true);

		  DocumentBuilder dBuilder = dbf.newDocumentBuilder();
		  Document document = dBuilder.newDocument();

		  // Generate random XML element name strings, using class java.util.UUID.
		  // Prefixing with an English letter, to make the string a valid XML name.
		  String docElemNameTemp = "a_" + ((UUID.randomUUID()).toString());
		  String elemNameTemp1 = "a_" + (UUID.randomUUID()).toString();
		  String elemNameTemp2 = "a_" + (UUID.randomUUID()).toString();
		  
		  Element docElem = document.createElement(docElemNameTemp);		  		  

		  for (int idx = 0; idx < groupingKeySeq.size(); idx++) {
			  XObject seqItem = groupingKeySeq.item(idx);
			  if (seqItem instanceof XMLNodeCursorImpl) {
				  DTMCursorIterator iter = seqItem.iter();
				  int nodeHandle = iter.nextNode();
				  DTM dtm = dtmMgr.getDTM(nodeHandle);
				  Node node = dtm.getNode(nodeHandle);
				  if (dtm.getNodeType(nodeHandle) == DTM.ELEMENT_NODE) {        				   
					  String nodeName = node.getNodeName();
					  String nodeTxtContent = node.getTextContent();
					  Element elem = document.createElement(nodeName);					  
					  Text text = document.createTextNode(nodeTxtContent);
					  elem.appendChild(text);
					  docElem.appendChild(elem);
				  }
				  if (dtm.getNodeType(nodeHandle) == DTM.ATTRIBUTE_NODE) { 
					  String attrName = node.getNodeName();
					  String attrValue = node.getNodeValue();
					  Element elem = document.createElement(elemNameTemp1);
					  elem.setAttribute(attrName, attrValue);
					  docElem.appendChild(elem);
				  }
				  else if (dtm.getNodeType(nodeHandle) == DTM.TEXT_NODE) {
					  String nodeTxtContent = node.getTextContent();
					  Text text = document.createTextNode(nodeTxtContent);
					  docElem.appendChild(text);
				  }
			  }
			  else if (seqItem instanceof XSAnyAtomicType) {
				  Element elem = document.createElement(elemNameTemp2);
				  String nodeTxtContent = XslTransformEvaluationHelper.getStrVal(seqItem);
				  Text text = document.createTextNode(nodeTxtContent);
				  elem.appendChild(text);
				  docElem.appendChild(elem);
			  }
			  else if ((seqItem instanceof XPathInlineFunction) || (seqItem instanceof XPathMap) || (seqItem instanceof XPathArray)) {
				  throw new TransformerException("XPTY0004 : Cannot convert a sequence to node set, because sequence has one or more function "
																												  		   + "item, map or an array instances.");
			  }
		  }

		  document.appendChild(docElem);
		  
		  DTM dtm = dtmMgr.getDTM(new DOMSource(document), true, null, false, false);
		  int docNodeHandle = dtm.getDocument();			    	        			  

		  int docElemHandle = dtm.getFirstChild(docNodeHandle);
		  int elemNodeHandle = dtm.getFirstChild(docElemHandle);
		  List<Integer> nodeHandleList = new ArrayList<Integer>();
		  while (elemNodeHandle != DTM.NULL) {			  
			  Element elemNode = (Element)(dtm.getNode(elemNodeHandle));
			  String elemName = elemNode.getNodeName();
			  if (elemNameTemp1.equals(elemName)) {
				  NamedNodeMap namedNodeMap = elemNode.getAttributes();
				  int attrLength = namedNodeMap.getLength();
				  for (int idx = 0; idx < attrLength; idx++) {
					  Attr attr = (Attr)(namedNodeMap.item(idx));
					  int attrNodeHandle = dtm.getAttributeNode(elemNodeHandle, null, attr.getNodeName());
					  nodeHandleList.add(attrNodeHandle);
				  }
			  }
			  else if (elemNameTemp2.equals(elemName)) {
				  int nodeHandle = dtm.getFirstChild(elemNodeHandle);
				  nodeHandleList.add(nodeHandle);
				  
			  }
			  else {				  
				  nodeHandleList.add(elemNodeHandle);				  
			  }

			  elemNodeHandle = dtm.getNextSibling(elemNodeHandle);
		  }

		  nodeSet = new XMLNodeCursorImpl(nodeHandleList, dtmMgr);
	  }
	  catch (Exception ex) {
		  throw new TransformerException("XPTY0004 : An error occured while converting a sequence to node set, "
				  																						+ "with following error trace : " + ex.getMessage() + ".");
	  }

	  return nodeSet; 
  }
  
}

