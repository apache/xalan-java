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

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xml.dtm.DTM;
import org.apache.xml.utils.QName;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;
import org.w3c.dom.Node;

/**
 * Implementation of an XSLT 3.0 xsl:context-item instruction.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class ElemContextItem extends ElemTemplateElement {

	private static final long serialVersionUID = 8951289086136373312L;
	
	/**
	 * The value of the "as" attribute.
	 */
	private String m_asAttr;

	/**
	 * Set the "as" attribute.
	 */
	public void setAs(String val) 
	{
		m_asAttr = val;
	}

	/**
	 * Get the "as" attribute.
	 */
	public String getAs() 
	{
		return m_asAttr;
	}
	
	/**
	 * The value of the "use" attribute.
	 */
	private String m_useAttr;
	
	/**
	 * Set the "use" attribute.
	 */
	public void setUse(String use) 
	{
		m_useAttr = use;
	}

	/**
	 * Get the "use" attribute.
	 */
	public String getUse() 
	{
		return m_useAttr;
	}
	
	/**
	 * Get an integer representation of the element type.
	 *
	 * @return An integer representation of the element, defined in the
	 *         Constants class.
	 * @see org.apache.xalan.templates.Constants
	 */
	public int getXSLToken()
	{
		return Constants.ELEMNAME_CONTEXT_ITEM;
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
	}
	
	/**
	 * Return the node name.
	 *
	 * @return The node name
	 */
	public String getNodeName()
	{
		return Constants.ELEMNAME_CONTEXT_ITEM_STRING;
	}
	
	/**
	 * Execute the xsl:context-item transformation.
	 *
	 * @param transformer non-null reference to the the current transform-time state.
	 *
	 * @throws TransformerException
	 */
	public void execute(TransformerImpl transformer) throws TransformerException
	{
		XPathContext xctxt = transformer.getXPathContext();
		
		final int contextNode = xctxt.getCurrentNode(); 
		
		SourceLocator srcLocator = xctxt.getSAXLocator();
		
        ElemTemplateElement parentTemplateElem = getParentElem();
        
        Node prevSiblingNode = getPreviousSibling();
        
        QName enclosingXslTemplateName = null;
        
        if ((parentTemplateElem instanceof ElemTemplate) && (prevSiblingNode == null)) {        	
        	ElemTemplate elemTemplate = (ElemTemplate)parentTemplateElem;
        	enclosingXslTemplateName = elemTemplate.getName();
        }
        else if (prevSiblingNode != null) {
        	String prevSiblingNodeName = prevSiblingNode.getNodeName();
        	throw new TransformerException("XTSE0010 : An xsl:context-item instruction can only occur as first "
		                                                                             + "child element of xsl:template instruction. An element '" + 
		        			                                                         prevSiblingNodeName + "' occured as previous sibling to xsl:context-item "
		        			                                                         + "instruction.", srcLocator);
        }
        else {
        	throw new TransformerException("XTSE0010 : An xsl:context-item instruction can only occur as first "
        			                                                                 + "child element of xsl:template instruction.", srcLocator);
        }
        
        if (m_useAttr == null) {
        	/**
        	 * An xsl:context-item instruction's "use" attribute is not specified
        	 * within an XSL stylesheet. Setting xsl:context-item instruction attribute 
        	 * "use"'s run-time value to 'optional' which is default value of "use" 
        	 * attribute.
        	 */
        	m_useAttr = Constants.ELEMNAME_CONTEXT_ITEM_OPTIONAL_STRING; 
        }
        
        if (!(m_useAttr.equals(Constants.ELEMNAME_CONTEXT_ITEM_REQUIRED_STRING) || 
        	  m_useAttr.equals(Constants.ELEMNAME_CONTEXT_ITEM_OPTIONAL_STRING) || 
        	  m_useAttr.equals(Constants.ELEMNAME_CONTEXT_ITEM_ABSENT_STRING))) {
        	throw new TransformerException("XTSE0010 : An xsl:context-item instruction's attribute \"use\" can have possible values "
        			                                                              + "'required', 'optional', 'absent'. Value occured in "
        			                                                              + "stylesheet: '" + m_useAttr + "'.", srcLocator);
        }        
        else if ((enclosingXslTemplateName == null) && (m_useAttr.equals(Constants.ELEMNAME_CONTEXT_ITEM_OPTIONAL_STRING) || 
        	                                         m_useAttr.equals(Constants.ELEMNAME_CONTEXT_ITEM_ABSENT_STRING))) {
        	throw new TransformerException("XTSE0020 : An xsl:context-item instruction appearing in an xsl:template declaration "
        			                                    + "with no \"name\" attribute must specify value of \"use\" attribute as 'required'.", srcLocator);
        }        
        else if (m_useAttr.equals(Constants.ELEMNAME_CONTEXT_ITEM_ABSENT_STRING) && (contextNode != DTM.NULL)) {
            throw new TransformerException("XTSE0020 : An xsl:context-item instruction has its attribute \"use\" value as 'absent', but "
            		                                                                                                 + "the context item exists.", srcLocator);
        }
        else if (m_useAttr.equals(Constants.ELEMNAME_CONTEXT_ITEM_REQUIRED_STRING) && (contextNode == DTM.NULL)) {
            throw new TransformerException("XTSE0020 : An xsl:context-item instruction has its attribute \"use\" value as 'required', but "
            		                                                                                                 + "the context item is absent.", srcLocator);
        }
        
        String xpathExprStr = ". instance of " + m_asAttr;
        
        XPath xpathObj = new XPath(xpathExprStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
        
        XObject evalResult = xpathObj.execute(xctxt, contextNode, xctxt.getNamespaceContext());
        if (!evalResult.bool()) {
        	String nodeName = null;
            if (contextNode != DTM.NULL) {
            	DTM dtm = xctxt.getDTM(contextNode);
            	Node node = dtm.getNode(contextNode);
            	nodeName = node.getNodeName();
            }
        	String errMesgSuffix = (nodeName != null) ? " The supplied node '" + nodeName + "' doesn't conform." : "";
        	throw new TransformerException("XTTE0590 : The required item type of the context item for the template "
        			                                                                                    + "rule is " + m_asAttr + "." + errMesgSuffix, srcLocator);
        }
        
	}

}
