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
import java.util.List;
import java.util.Vector;

import javax.xml.XMLConstants;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.xslt.util.XslTransformData;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xml.serializer.SerializationHandler;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathCollationSupport;
import org.apache.xpath.XPathContext;
import org.apache.xpath.compiler.Keywords;
import org.apache.xpath.functions.FuncNumber;
import org.apache.xpath.functions.XSL3ConstructorOrExtensionFunction;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XdmAttributeItem;
import org.apache.xpath.types.XSLanguage;
import org.xml.sax.SAXException;

import xml.xpath31.processor.types.XSString;

/**
 * Implementation of an XSLT 3.0 instruction xsl:perform-sort.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class ElemPerformSort extends ElemTemplateElement implements ExpressionOwner {

	private static final long serialVersionUID = 724739011831529300L;
	
	/**
	 * The "select" expression.
	 */
	private Expression m_selectExpression = null;
	
	/**
	 * Class field to refer to, XPath expression for subsequent 
	 * processing.
	 */
	private XPath m_xpath = null;

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
	 * Set the "select" attribute.
	 *
	 * @param xpath The XPath expression for the "select" attribute.
	 */
	public void setSelect(XPath xpath)
	{
		m_selectExpression = xpath.getExpression();
		m_xpath = xpath;    
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
	 * is declared on xsl:perform-sort instruction.
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
	
	private Vector m_vars;
	  
	private int m_globals_size;
	
	/**
	 * An XPath expression for XSL attribute "use-when". 
	 */
	private XPath m_useWhen = null;

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
	 * @return			     XPath expression for attribute "use-when"
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
	 * @param sroot                             StylesheetRoot object
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

		m_vars = vnames;
		m_globals_size = sroot.getComposeState().getGlobalsSize(); 
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
	 * Vector containing the xsl:sort elements associated with this element.
	 */
	protected Vector m_sortElems = null;

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
		if (m_sortElems == null) {
		   m_sortElems = new Vector();
		}

		m_sortElems.addElement(sortElem);
	}
	
	/**
	 * Get an int constant identifying the type of element.
	 * @see org.apache.xalan.templates.Constants
	 *
	 * @return           The token id for this element
	 */
	public int getXSLToken()
	{
		return Constants.ELEMNAME_PERFORMSORT;
	}

	/**
	 * Return the node name.
	 *
	 * @return The element's name
	 */
	public String getNodeName()
	{
		return Constants.ELEMNAME_PERFORMSORT_STRING;
	}
	
	/**
	 * Evaluate an XSL instruction xsl:perform-sort.
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
		
		XPathContext xctxt = transformer.getXPathContext();
		
		final int sourceNode = xctxt.getCurrentNode();
		
		SourceLocator srcLocator = xctxt.getSAXLocator();				
		
		if (m_xpath_default_namespace != null) {    		
	    	m_xpath = new XPath(m_xpath.getPatternString(), srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null, false, m_xpath_default_namespace);
	    	
	    	m_selectExpression = m_xpath.getExpression();
	  	}
		
		ElemTemplateElement elemTemplateElement = getFirstChildElem();
		
		boolean isXslSort = false;
		while (elemTemplateElement != null) {
			if (elemTemplateElement instanceof ElemSort) {
				isXslSort = true;
				
				break;
			}
			
			elemTemplateElement = elemTemplateElement.getNextSiblingElem();
		}
		
		if (!isXslSort) {
			throw new TransformerException("XTSE0010 : An XSL perform-sort instruction must have one or more, XSL sort "
					                                                                            + "instructions at begining of perform-sort "
					                                                                            + "instruction.", srcLocator);
		}
		
		int sortElemCount = m_sortElems.size();
		
		for (int idx = 0; idx < sortElemCount; idx++) {
    	    ElemSort elemSort = (ElemSort)m_sortElems.get(idx);
    	    
    	    if (idx > 0) {
    	       if (elemSort.isStableDeclared()) {
    	    	  throw new TransformerException("XTSE1017 : Only the first XSL 'sort' element within a sequence of "
					    	    	  		                                                                            + "'sort' elements can have an attribute named "
					    	    	  		                                                                            + "'stable'.", elemSort);	
    	       }
    	    }
    	    
    	    XPath xslSortSelect = elemSort.getSelect();
    	    
    	    if (xslSortSelect != null) {
    	    	Expression xpathExpr = xslSortSelect.getExpression();
    	    	
    	    	if (xpathExpr instanceof XSL3ConstructorOrExtensionFunction) {
    	    		XSL3ConstructorOrExtensionFunction xsl3ConstructorOrExtFunc = (XSL3ConstructorOrExtensionFunction)xpathExpr;
    	    		String funcName = xsl3ConstructorOrExtFunc.getFunctionName();
    	    		String namespace = xsl3ConstructorOrExtFunc.getNamespace();
    	    		
    	    		if ((XMLConstants.W3C_XML_SCHEMA_NS_URI).equals(namespace) && (Keywords.XS_DURATION).equals(funcName)) {
    	    			throw new TransformerException("XTDE1030 : An XSL instruction perform-sort's sort key cannot be with XML Schema type 'duration'.", elemSort);
    	    		}
    	    	}
    	    }
    	    else if (elemSort.getFirstChildElem() == null) {
    	    	xslSortSelect = new XPath(".", srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
    	    	
    	    	elemSort.setSelect(xslSortSelect);    	    	    	    	    	    	
    	    }
    	    
            AVT langAvt = elemSort.getLang();
    	    
    	    if (langAvt != null) {
    	       String langStr = langAvt.evaluate(xctxt, sourceNode, elemSort);
    	       
    	       if (!"".equals(langStr)) {
    	    	   // Construction XSLanguage object instance, does 
    	    	   // xs:language value space type check.
    	    	   
    	    	   XSLanguage xsLanguage = new XSLanguage(langStr); 
    	       }
    	       else {
    	    	  elemSort.setLang(null); 
    	       }
    	    }
	    	
    	    if (elemSort.getLang() != null) {
    	    	AVT dataTypeAvt = elemSort.getDataType();
    	    	String dataTypeStr = dataTypeAvt.evaluate(xctxt, sourceNode, xctxt.getNamespaceContext());
    	    	    	    	
    	    	if ((Constants.ELEMNAME_NUMBER_STRING).equals(dataTypeStr) || (xslSortSelect.getExpression() instanceof FuncNumber)) {
    	    	   // xsl:sort lang has no effect on numeric data
    	    		
    	    	   elemSort.setLang(null);
    	    	}
    	    }
    	    
    	    AVT collationAvt = elemSort.getCollation();
    	    
    	    if (collationAvt != null) {
    	    	String collationUri = collationAvt.evaluate(xctxt, sourceNode, elemSort);

    	    	if (!XPathCollationSupport.isCollationSupported(collationUri)) {
    	    		throw new TransformerException("XTDE1035 : An XSL sort, collation '" + collationUri + "' is not supported.", elemSort);
    	    	}
    	    }
    	}

		try {												
			SerializationHandler handler = transformer.getSerializationHandler();
			
			if (m_selectExpression != null) {			   			   
				XObject xObj = m_selectExpression.execute(xctxt);
				
				ElemTemplateElement xslPerformSortParentElem = getParentElem();
				
				boolean isXslNamedTemplateChild = false;
				
				if ((xslPerformSortParentElem instanceof ElemTemplate) && !(xslPerformSortParentElem instanceof ElemFunction)) {
				   ElemTemplate elemTemplate = (ElemTemplate)xslPerformSortParentElem;
				   
				   if ((elemTemplate.getMatch() == null) && (elemTemplate.getName() != null)) {
					   isXslNamedTemplateChild = true;
				   }
				}
				
				if (xObj instanceof XMLNodeCursorImpl) {
					XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)xObj;
					
					DTMCursorIterator xdmNodeSetIter = xmlNodeCursorImpl.asIterator(xctxt, sourceNode);
										 																																		
					ElemForEach elemForEach = new ElemForEach();

					final Vector sortKeys = transformer.processSortKeys(this, sourceNode);
										
					xdmNodeSetIter = elemForEach.sortNodes(xctxt, sortKeys, xdmNodeSetIter);
					
					int nextNode = DTM.NULL;
					boolean isXdmNsNode = false; 
					
					if ((xslPerformSortParentElem instanceof ElemVariable) || isXslNamedTemplateChild 
							                                               || (xslPerformSortParentElem instanceof ElemFunction)) {
						ResultSequence rSeq = new ResultSequence();

						int count = 0;

						while ((nextNode = xdmNodeSetIter.nextNode()) != DTM.NULL) {
							count++;

							if (count == 1) {
								/**
								 * If the first node within an xdm sequence is an 
								 * XML namespace node, then other XML sibling nodes 
								 * are also assumed to be XML namespace nodes.
								 */
								
								DTM dtm = xctxt.getDTM(nextNode); 
								if (dtm.getNodeType(nextNode) == DTM.NAMESPACE_NODE) {
									isXdmNsNode = true;
								}
							}
							
							XMLNodeCursorImpl xmlNodeCursorImpl2 = new XMLNodeCursorImpl(nextNode, xctxt);

							if (!isXdmNsNode) {								
								rSeq.add(xmlNodeCursorImpl2);
							}
							else {
								String str1 = xmlNodeCursorImpl2.str();
								rSeq.add(new XSString(str1));
							}
						}

						XslTransformData.m_xsl_perform_sort_rSeq = rSeq;
					}
					else {						
						int count = 0;
						
						while ((nextNode = xdmNodeSetIter.nextNode()) != DTM.NULL) {
							count++;

							if (count == 1) {
								/**
								 * If the first node within an xdm sequence is an 
								 * XML namespace node, then other XML sibling nodes 
								 * are also assumed to be XML namespace nodes.
								 */
								
								DTM dtm = xctxt.getDTM(nextNode); 
								if (dtm.getNodeType(nextNode) == DTM.NAMESPACE_NODE) {
									isXdmNsNode = true;
								}
							}
							
							XMLNodeCursorImpl xmlNodeCursorImpl2 = new XMLNodeCursorImpl(nextNode, xctxt);
							
							if (!isXdmNsNode) {	
							   ElemCopyOf.copyOfActionOnNodeSet(xmlNodeCursorImpl2, transformer, handler, xctxt);
							}
							else {
							   String str1 = xmlNodeCursorImpl2.str();
							   XSString xsString = new XSString(str1);
							   
							   ResultSequence rSeq = new ResultSequence();
							   rSeq.add(xsString);
							   
							   ElemCopyOf.copyOfActionOnResultSequence(rSeq, transformer, handler, xctxt, false, this);
							}
						}
					}
				}
				else if (xObj instanceof ResultSequence) {
					ElemForEach elemForEach = new ElemForEach();
					
					final Vector sortKeys = transformer.processSortKeys(this, sourceNode);
					ResultSequence rSeq = (ResultSequence)xObj;
					
					rSeq = elemForEach.sortXdmSequence(xctxt, sortKeys, rSeq);										
					
					if (((xslPerformSortParentElem instanceof ElemVariable) || (xslPerformSortParentElem instanceof ElemFunction) 
							                                                || isXslNamedTemplateChild) && !(rSeq.item(0) instanceof XdmAttributeItem)) {												
						XslTransformData.m_xsl_perform_sort_rSeq = rSeq;
					}
					else {
					    ElemCopyOf.copyOfActionOnResultSequence(rSeq, transformer, handler, xctxt, false, this);
					}
				}
			}
			else {
				int rootNodeHandleRtf = transformer.transformToRTF(this);
				
				DTM dtm = xctxt.getDTM(rootNodeHandleRtf);
				
				int nextNode = dtm.getFirstChild(rootNodeHandleRtf);
				List<Integer> nodeHandleList = new ArrayList<Integer>();
				
				while (nextNode != DTM.NULL) {
					nodeHandleList.add(Integer.valueOf(nextNode));
					
					nextNode = dtm.getNextSibling(nextNode); 
				}

				XMLNodeCursorImpl xmlNodeCursorImpl = new XMLNodeCursorImpl(nodeHandleList, xctxt);
				DTMCursorIterator xdmNodeSetIter = xmlNodeCursorImpl.asIterator(xctxt, sourceNode);

				ElemForEach elemForEach = new ElemForEach();

				final Vector sortKeys = transformer.processSortKeys(this, sourceNode);

				xdmNodeSetIter = elemForEach.sortNodes(xctxt, sortKeys, xdmNodeSetIter);
				
				ElemCopyOf.copyOfActionOnNodeSet((XMLNodeCursorImpl)xdmNodeSetIter, transformer, handler, xctxt);
			}
		}
		catch (SAXException ex) {
			throw new TransformerException(ex.getMessage(), srcLocator);
		}
		catch (TransformerException ex) {
			throw new TransformerException(ex.getMessage(), srcLocator);
		}
		finally
		{
			if (transformer.getDebug()) {
				transformer.getTraceManager().emitTraceEndEvent(this);
			}

			transformer.popCurrentTemplateRuleIsNull();
		}

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
		
		switch (type)
	    {
	    case Constants.ELEMNAME_SORT :	    	
	    	setSortElem((ElemSort)newChild);
	    	
	    	break;
	    case Constants.ELEMNAME_FALLBACK :
	    	break;
	    default :
	    	if (m_selectExpression != null) {
	    		String lineNo = String.valueOf(newChild.getLineNumber());
	    		String columnNo = String.valueOf(newChild.getColumnNumber());

	    		error(XSLTErrorResources.ER_PERFORM_SORT_CANNOT_ADD,
								    				  new Object[]{ newChild.getNodeName(),
								    						   this.getNodeName(), lineNo, columnNo });
	    	}	    	
	    }
		
		return super.appendChild(newChild);
	}
	
	/**
	 * Call the children visitors.
	 * 
	 * @param visitor                The visitor whose appropriate method 
	 *                               will be called.
	 */
	public void callChildVisitors(XSLTVisitor visitor, boolean callAttributes)
	{		
		if (callAttributes && (m_selectExpression != null))
			m_selectExpression.callVisitors(this, visitor);

		int length = getSortElemCount();

		for (int i = 0; i < length; i++)
		{
			getSortElem(i).callVisitors(visitor);
		}

		super.callChildVisitors(visitor, callAttributes);
	}

	@Override
	public Expression getExpression() {
		return m_selectExpression;
	}

	@Override
	public void setExpression(Expression exp) {
		exp.exprSetParent(this);
	  	m_selectExpression = exp;
	}		

}
