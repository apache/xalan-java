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

import java.util.Vector;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.xslt.util.XslTransformData;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xpath.Expression;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.axes.SelfIteratorNoPredicate;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XNodeSetForDOM;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathMap;
import org.apache.xpath.objects.XRTreeFrag;
import org.apache.xpath.objects.XString;
import org.w3c.dom.NodeList;

/**
 * Class definition, to implement XSLT 3.0 instruction xsl:map-entry 
 * implementation.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class ElemMapEntry extends ElemTemplateElement {

	private static final long serialVersionUID = 268470193387805997L;
	
	/**
	 * The required key attribute contains an expression.
	 */
	public XPath m_keyExpression = null;
	
	/**
	 * An optional select attribute contains an expression.
	 */
	public XPath m_selectExpression = null;

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
	 * This class field is used during, XPath.fixupVariables(..) action 
	 * as performed within object of this class.  
	 */    
	private Vector m_vars;
	
	/**
	 * This class field is used during, XPath.fixupVariables(..) action 
	 * as performed within object of this class.  
	 */
	private int m_globals_size;
	
	/**
	 * Set the "key" attribute.
	 * 
	 * The required select attribute contains an expression.
	 *
	 * @param expr Expression for select attribute 
	 */
	public void setKey(XPath expr)
	{	    	      
		m_keyExpression = expr;
	}
	
	/**
	 * Get the "key" attribute.
	 * 
	 * The required select attribute contains an expression.
	 *
	 * @return Expression for select attribute 
	 */
	public XPath getKey()
	{
		return m_keyExpression;
	}
	
	/**
	 * Set the "select" attribute.
	 * 
	 * The required select attribute contains an expression.
	 *
	 * @param expr Expression for select attribute 
	 */
	public void setSelect(XPath expr)
	{	    	      
		m_selectExpression = expr;
	}
	
	/**
	 * Get the "select" attribute.
	 * 
	 * The required select attribute contains an expression.
	 *
	 * @return Expression for select attribute 
	 */
	public XPath getSelect()
	{
		return m_selectExpression;
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
	 * Variable to indicate whether, an attribute 'expand-text'
	 * is declared on xsl:copy-of instruction.
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
	 * This function is called after everything else has been
	 * recomposed, and allows the template to set remaining
	 * values that may be based on some other property that
	 * depends on recomposition.
	 */
	public void compose(StylesheetRoot sroot) throws TransformerException
	{
		super.compose(sroot);

		java.util.Vector vnames = (sroot.getComposeState()).getVariableNames();

		m_vars = (Vector)(vnames.clone()); 
		m_globals_size = (sroot.getComposeState()).getGlobalsSize();
		
		if (m_keyExpression != null) {
	        m_keyExpression.fixupVariables(vnames, m_globals_size);
	    }
		
		if (m_selectExpression != null) {
	        m_selectExpression.fixupVariables(vnames, m_globals_size);
	    }
	}
	
	/**
	 * Get an int constant identifying the type of element.
	 * @see org.apache.xalan.templates.Constants
	 *
	 * @return           The token id for this element
	 */
	public int getXSLToken()
	{
		return Constants.ELEMNAME_MAP_ENTRY;
	}

	/**
	 * Return the node name.
	 *
	 * @return The element's name
	 */
	public String getNodeName()
	{
		return Constants.ELEMNAME_MAP_ENTRY_STRING;
	}
	
	/**
	 * Evaluate the xsl:map-entry instruction.
	 *
	 * @param transformer non-null reference to the the current transform-time state.
	 *
	 * @throws TransformerException
	 */
	public void execute(TransformerImpl transformer) throws TransformerException
	{
		if (transformer.getDebug())
			transformer.getTraceManager().emitTraceEvent(this);
		
		XPathContext xctxt = transformer.getXPathContext();
	    setXPathContext(xctxt);
	    
	    SourceLocator srcLocator = xctxt.getSAXLocator();
	    
	    final int sourceNode = xctxt.getCurrentNode();	    	    
	    
	    if (m_xpath_default_namespace != null) {    		
	    	m_keyExpression = new XPath(m_keyExpression.getPatternString(), srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
	    	
	    	if (m_selectExpression != null) {    		
		    	m_selectExpression = new XPath(m_selectExpression.getPatternString(), srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
		    }
	    }
	    
	    if ((m_selectExpression != null) && (getFirstChildElem() != null)) {
	       throw new TransformerException("XTSE3280 : An XSL 'map-entry' instruction cannot have both the \"select\" attribute "
	       		                                                                                                 + "and a contained sequence constructor.", srcLocator);
	    }
	    
	    XObject keyObj1 = null;
	    
	    Expression expr = m_keyExpression.getExpression();
	    if (expr instanceof SelfIteratorNoPredicate) {
	       if (xctxt.getXPath3ContextItem() != null) {
	    	  keyObj1 = xctxt.getXPath3ContextItem();  
	       }
	       else {
	    	  keyObj1 = new XMLNodeCursorImpl(sourceNode, xctxt); 
	       }
	    }
	    else {
	       keyObj1 = m_keyExpression.execute(xctxt, sourceNode, xctxt.getNamespaceContext());
	    }
	    
	    ResultSequence keyrSeq = null;
	    
	    if (keyObj1 instanceof ResultSequence) {
	       keyrSeq = (ResultSequence)keyObj1;
	    }
	    else if (keyObj1 instanceof XMLNodeCursorImpl) {
	       keyrSeq = XslTransformEvaluationHelper.getResultSequenceFromXObject(keyObj1, xctxt);
	    }
	    
	    if ((keyrSeq != null) && (keyrSeq.size() == 0)) {
	       throw new TransformerException("XPTY0004 : An XSL 'map-entry' instruction cannot evaluate to a key that is "
	       		                                                                                                  + "an empty sequence.", srcLocator);
	    }
	    
	    XObject valueObj1 = null;
	    
	    if (m_selectExpression != null) {
	       valueObj1 = m_selectExpression.execute(xctxt, sourceNode, xctxt.getNamespaceContext());
	    }
	    else if (getFirstChildElem() == null) {
	       valueObj1 = XString.EMPTYSTRING;
	    }
	    else {
	    	int rootNodeHandleOfRtf = transformer.transformToRTF(this);

	    	if (XslTransformData.m_xpathInlineFunction != null) {
	    		valueObj1 = XslTransformData.m_xpathInlineFunction;
	    		XslTransformData.m_xpathInlineFunction = null;
	    	}
	    	else if (XslTransformData.m_xpathArray != null) {
	    		valueObj1 = XslTransformData.m_xpathArray;
	    		XslTransformData.m_xpathArray = null;
	    	}
	    	else if (XslTransformData.m_xpathMap != null) {
	    		valueObj1 = XslTransformData.m_xpathMap;
	    		XslTransformData.m_xpathMap = null;
	    	}
	    	else if ((XslTransformData.m_xpathNamedFunctionRefSequence).size() > 0) {
	    		if ((XslTransformData.m_xpathNamedFunctionRefSequence).size() == 1) {
	    			valueObj1 = (XslTransformData.m_xpathNamedFunctionRefSequence).item(0);  
	    		}
	    		else {
	    			valueObj1 = XslTransformData.m_xpathNamedFunctionRefSequence;
	    		}
	    	}
	    	else if (XslTransformData.m_xsl_perform_sort_resultSeq != null) {
	    		valueObj1 = XslTransformData.m_xsl_perform_sort_resultSeq; 
	    	}
	    	else {
	    		NodeList nodeList = (new XRTreeFrag(rootNodeHandleOfRtf, xctxt, this)).convertToNodeset();    	  
	    		valueObj1 = new XNodeSetForDOM(nodeList, xctxt);    	     
	    	}
	    }
	    
	    ElemTemplateElement elemTemplateParentElem = getParentElem();
	    
	    if (elemTemplateParentElem instanceof ElemTemplate) {
	    	if (ElemTemplateElement.m_xpath_map_seq != null) {	    	  
	    	   (ElemTemplateElement.m_xpath_map).put(keyObj1, valueObj1);
	    	}
	    	else {
	    	   ElemTemplateElement.m_xpath_map_seq = new ResultSequence();
	    	   ElemTemplateElement.m_xpath_map = new XPathMap();
	    	   
	    	   (ElemTemplateElement.m_xpath_map).put(keyObj1, valueObj1);
	    	   
	    	   (ElemTemplateElement.m_xpath_map_seq).add(ElemTemplateElement.m_xpath_map);
	    	}
	    	
	    	return;
	    }
	    
	    
	    boolean isXslMapAncestor = false;
	    while (elemTemplateParentElem != null) {
	       if (elemTemplateParentElem instanceof ElemMap) {
	    	  ElemMap elemMap = (ElemMap)elemTemplateParentElem;	    	  
	    	  elemMap.put(keyObj1, valueObj1);
	    	  
	    	  isXslMapAncestor = true;
	    	  
	    	  break;
	       }
	       
	       elemTemplateParentElem = elemTemplateParentElem.getParentElem();
	    }
	    
	    // An XSL 3 instruction xsl:map-entry, without an ancestor 
	    // XSL xs:map instruction, constructs an xdm map with a single entry.
	    
	    if (!isXslMapAncestor) {
	    	if (ElemMap.m_xpath_map_seq == null) {
	    		ElemMap.m_xpath_map_seq = new ResultSequence();	    	
	    		ElemMap.m_xpath_map = new XPathMap();
	    		
	    		(ElemMap.m_xpath_map).put(keyObj1, valueObj1);
	    		(ElemMap.m_xpath_map_seq).add(ElemMap.m_xpath_map);
	    	}
	    	else {
	    		(ElemMap.m_xpath_map).put(keyObj1, valueObj1);
	    	}
	    }
		
	}
	
	/**
	 * Add a child to the child list.
	 *
	 * @param newChild Child to add to this node's child list
	 *
	 * @return Child just added to child list
	 */
	public ElemTemplateElement appendChild(ElemTemplateElement newChild)
	{
        return super.appendChild(newChild);
	}
	
	/**
	 * Call the children visitors.
	 * 
	 * @param visitor The visitor whose appropriate method will be called.
	 */
	protected void callChildVisitors(XSLTVisitor visitor, boolean callAttrs)
	{	  	
		super.callChildVisitors(visitor, callAttrs);
	}

}
