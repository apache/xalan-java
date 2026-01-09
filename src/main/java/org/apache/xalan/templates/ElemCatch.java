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
import org.apache.xml.serializer.SerializationHandler;
import org.apache.xml.utils.QName;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;
import org.xml.sax.SAXException;

/**
 * An implementation of, XSLT 3.0 xsl:catch instruction.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 *  
 * @xsl.usage advanced
 */
public class ElemCatch extends ElemTemplateElement implements ExpressionOwner {

	static final long serialVersionUID = -1806774621639027295L;
	
	/**
	 * The value of the "errors" attribute.
	 */
	private QName m_errorNames[] = null;

	/**
	 * Set the "errors" attribute.
	 */
	public void setErrors(Vector v)
	{
		int n = v.size();
		m_errorNames = new QName[n];

		for (int idx = 0; idx < n; idx++) {
		   m_errorNames[idx] = (QName)v.elementAt(idx);
		}
	}
	
	/**
	 * Get the value of "errors" attribute.
	 */
	public QName[] getErrors()
	{
	    return m_errorNames;
	}
	  
	/**
	 * The "select" expression.
	 */
	private XPath m_selectExpression = null;
	
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
	 * Class field, that represents the value of "xpath-default-namespace" 
	 * attribute.
	 */
	private String m_xpath_default_namespace = null;

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
	 * is declared on xsl:catch instruction.
	 */
	private boolean m_expand_text_declared;

	/**
	 * Class field, that represents the value of "expand-text" 
	 * attribute.
	 */
	private boolean m_expand_text;

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
	 * This class field is used during, XPath.fixupVariables(..) 
	 * evaluation as performed within object of this class.  
	 */    
	private Vector m_vars;

	/**
	 * This class field is used during, XPath.fixupVariables(..) 
	 * evaluation as performed within object of this class.  
	 */
	private int m_globals_size;

	/**
	 * Class constructor.
	 */
	public ElemCatch() {}
	
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
		
        java.util.Vector vnames = (sroot.getComposeState()).getVariableNames();
	    
	    m_vars = (Vector)(vnames.clone()); 
	    m_globals_size = (sroot.getComposeState()).getGlobalsSize();

	    if (m_selectExpression != null) {
	        m_selectExpression.fixupVariables(vnames, m_globals_size);
	    }
	}

	/**
	 * This function is called after the template's children have been composed.
	 */
	public void endCompose(StylesheetRoot sroot) throws TransformerException
	{     
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
		return Constants.ELEMNAME_CATCH;
	}

	/**
	 * Return the node name.
	 *
	 * @return The element's name
	 */
	public String getNodeName()
	{
		return Constants.ELEMNAME_CATCH_STRING;
	}
	
	/**
	 * Run an xsl:catch transformation.
	 *
	 * @param transformer non-null reference to the the current transform-time state.
	 *
	 * @throws TransformerException
	 */
	public void execute(TransformerImpl transformer) throws TransformerException
	{
        XPathContext xctxt = transformer.getXPathContext();
	    
	    SourceLocator srcLocator = xctxt.getSAXLocator();
	    
	    int contextNode = xctxt.getContextNode();
	    
	    ElemTemplateElement parentElem = getParentElem();
	    if (!(parentElem instanceof ElemTry)) {
	    	throw new TransformerException("XTSE3150 : An XSL catch element can occur, only as child of xsl try element.", srcLocator);
	    }
	    
	    if ((m_selectExpression != null) && (m_xpath_default_namespace != null)) {    		
	    	m_selectExpression = new XPath(m_selectExpression.getPatternString(), srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
	    }
	    
	    if ((m_selectExpression != null) && (this.m_firstChild != null)) {
	    	throw new TransformerException("XTSE3150 : An XSL catch element cannot have both 'select' attribute, and a contained "
	    			                                                                                              + "sequence constructor.", srcLocator);
		}
	    
	    if (m_selectExpression != null) {
	    	// An XSL processing specified by xsl:catch element is
	    	// been done by xsl:catch element's 'select' attribute.
	    	
	    	if (m_vars != null) {
    			m_selectExpression.fixupVariables(m_vars, m_globals_size);
    		}

    		m_selectExpression.setIsConcreteExceptionProcessing(true);
    		XObject xpathEvalResult = m_selectExpression.execute(xctxt, contextNode, xctxt.getNamespaceContext());
    		
    		ResultSequence rSeq = new ResultSequence();
			rSeq.add(xpathEvalResult);
			SerializationHandler handler = transformer.getSerializationHandler(); 
			try {
				ElemCopyOf.copyOfActionOnResultSequence(rSeq, transformer, handler, xctxt, false, this);
			} 
			catch (TransformerException ex) {
				throw new javax.xml.transform.TransformerException("XPTY0004 : An error occured while serializing XSL catch's evaluation "
						                                                        + "to an XSL result tree, with following run-time exception trace : " + 
						                                                        ex.getMessage() + ".", srcLocator);	
			} 
			catch (SAXException ex) {
				throw new javax.xml.transform.TransformerException("XPTY0004 : An error occured while serializing XSL catch's evaluation "
                                                                               + "to an XSL result tree, with following exception trace : " + 
                                                                               ex.getMessage() + ".", srcLocator);		
			}
	    }
	    else {
	    	// An XSL processing specified by xsl:catch element is been 
	    	// done by xsl:catch element's contained sequence constructor.
	    	
	    	for (ElemTemplateElement t = this.m_firstChild; t != null;
	    															t = t.m_nextSibling) 
	    	{
	    		xctxt.setSAXLocator(t);
	    		transformer.setCurrentElement(t);
	    		t.execute(transformer);
	    	}
	    }
	}
	
	/**
	 * Add an XSL stylesheet child information.
	 *
	 * @param newChild Child to add to child list
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
	public void callChildVisitors(XSLTVisitor visitor, boolean callAttributes)
	{    
		super.callChildVisitors(visitor, callAttributes);
	}
	
	@Override
	public Expression getExpression() {
		// no op
		return null;
	}

	@Override
	public void setExpression(Expression exp) {
		// no op
	}

}
