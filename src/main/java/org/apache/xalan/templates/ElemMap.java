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

import javax.xml.transform.TransformerException;

import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathMap;

/**
 * Class definition, to implement XSLT 3.0 instruction xsl:map 
 * implementation.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class ElemMap extends ElemTemplateElement {

	private static final long serialVersionUID = 8285815931283478710L;
	
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
	 * Class field, that stores a sequence of xdm maps. Evaluation result
	 * of sibling xsl:map instructions are stored within this class field.
	 */
	public static ResultSequence m_xpath_map_seq = null;
	
	/**
	 * Class field, that stores an xdm map, that is the result of evaluation
	 * of an xsl:map instruction.
	 */
	public static XPathMap m_xpath_map = null;
	
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
	}
	
	/**
	 * Get an int constant identifying the type of element.
	 * @see org.apache.xalan.templates.Constants
	 *
	 * @return           The token id for this element
	 */
	public int getXSLToken()
	{
		return Constants.ELEMNAME_MAP;
	}

	/**
	 * Return the node name.
	 *
	 * @return The element's name
	 */
	public String getNodeName()
	{
		return Constants.ELEMNAME_MAP_STRING;
	}
	
	/**
	 * Evaluate the xsl:map instruction.
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
		
		ElemTemplateElement elemTemplateParentElem = getParentElem();		
		if ((elemTemplateParentElem instanceof ElemLiteralResult) || (elemTemplateParentElem instanceof ElemElement)) {
		   throw new TransformerException("XTDE0450 : An xdm map cannot be added as child of an XML constructed element.", this); 
		}
		
		if ((elemTemplateParentElem instanceof ElemTemplate) && !(elemTemplateParentElem instanceof ElemFunction)) {
			ElemTemplate elemTemplate = (ElemTemplate)elemTemplateParentElem;
			if (elemTemplate.getName() == null) {
				// Trying to evaluate, <xsl:template match="..."> having xsl:map child element
				if (ElemTemplateElement.m_xpath_map_seq == null) {
					ElemTemplateElement.m_xpath_map_seq = new ResultSequence();
					ElemTemplateElement.m_xpath_map = new XPathMap();
				}				

				ElemTemplateElement t = getFirstChildElem();

				if (t instanceof ElemApplyTemplates) {
					// There's a possibility of constructing an xdm map
					// successfully for this case.				
					xctxt.setSAXLocator(t);
					transformer.setCurrentElement(t);
					t.execute(transformer);

					(ElemTemplateElement.m_xpath_map_seq).add(ElemTemplateElement.m_xpath_map);

					return;
				}
				else {
					// XSL transformation behavior is undefined for this case
					ElemTemplateElement.m_xpath_map_seq = null;
					ElemTemplateElement.m_xpath_map = null;

					return;
				}
			}
		}
		
		if (m_xpath_map_seq == null) {
		   m_xpath_map_seq = new ResultSequence();
		}
	    
	    m_xpath_map = new XPathMap();
	    
	    for (ElemTemplateElement t = this.m_firstChild; t != null; t = t.m_nextSibling)
	    {
	    	boolean xslStylesheetError = false;	    	
	    	if (!(t instanceof ElemMapEntry)) {
	    		xslStylesheetError = true;
	    		ElemTemplateElement elem1 = t.getFirstChildElem();
	    		while (elem1 != null) {	    		  
	    			if (elem1 instanceof ElemMapEntry) {
	    				xslStylesheetError = false;

	    				break; 
	    			}

	    			ElemTemplateElement elem2 = elem1.getNextSiblingElem();	    		  
	    			if (elem2 == null) {
	    				elem1 = elem1.getFirstChildElem();  
	    			}
	    			else {	    			  
	    				elem1 = elem2;
	    			}
	    		}
	    	}
	    	
	    	if (xslStylesheetError) {
	    	   throw new TransformerException("XTTE3375 : An XSL 'map' instruction doesn't have an XSL 'map-entry' descendant instruction.", this);
	    	}
	    	
	    	xctxt.setSAXLocator(t);
	    	transformer.setCurrentElement(t);
	    	t.execute(transformer);
	    }
	    
	    m_xpath_map_seq.add(m_xpath_map);
	}
	
	/**
	 * Method definition, to add key, value pair to an xdm map.
	 * 
	 *  This method is called by, ElemMapEntry class implementation
	 *  which is an implementation of xsl:map-entry instruction.
	 * 
	 * @param key				Value of map entry's key
	 * @param value             Value of map entry's value
	 */
	public void put(XObject key, XObject value) {
		m_xpath_map.put(key, value);  
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
