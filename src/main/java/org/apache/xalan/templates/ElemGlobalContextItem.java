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

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xpath.XPathContext;

/**
 * Implementation of XSLT 3.0 xsl:global-context-item instruction.
 *
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class ElemGlobalContextItem extends ElemTemplateElement {

	private static final long serialVersionUID = -6724404114498581620L;
	
	private String m_asAttr = null;
	
	private String m_useAttr = Constants.ATTRVAL_OPTIONAL;

	public String getAs() {
		return m_asAttr;
	}

	public void setAs(String asAttr) {
		this.m_asAttr = asAttr;
	}

	public String getUse() {
		return m_useAttr;
	}

	public void setUse(String useAttr) {
		this.m_useAttr = useAttr;
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
		return Constants.ELEMNAME_GLOBAL_CONTEXT_ITEM;
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
		return Constants.ELEMNAME_GLOBAL_CONTEXT_ITEM_STRING;
	}
	
	/**
	 * Evaluate the xsl:global-context-item transformation.
	 *
	 * @param transformer non-null reference to the the current transform-time state.
	 *
	 * @throws TransformerException
	 */
	public void execute(TransformerImpl transformer) throws TransformerException
	{
		
		XPathContext xctxt = transformer.getXPathContext();
		
		SourceLocator srcLocator = xctxt.getSAXLocator();
						
		Stylesheet stylesheet = transformer.getStylesheet();
		
		int xslIncludeCount = stylesheet.getIncludeCount();
		List<String> useStrList = new ArrayList<String>();
		List<String> asAttrList = new ArrayList<String>();
		for (int idx = 0; idx < xslIncludeCount; idx++) {
		   Stylesheet stylesheet1 = stylesheet.getInclude(idx);
		   Vector globalContextItemVector = stylesheet1.getGlobalContextItemList();
		   if (globalContextItemVector.size() == 1) {
			  ElemGlobalContextItem elemGlobalContextItem1 = (ElemGlobalContextItem)(globalContextItemVector.get(0));
			  String useStr = elemGlobalContextItem1.getUse();
			  if (useStr != null) {
			     useStrList.add(useStr);
			  }
			  
			  String asAttrStr = elemGlobalContextItem1.getAs();
			  if (asAttrStr != null) {
				 asAttrList.add(asAttrStr); 
			  }
		   }
		}
		
		Vector globalContextItemVector = stylesheet.getGlobalContextItemList();		
		if ((globalContextItemVector != null) && (globalContextItemVector.size() > 1)) {
		   throw new TransformerException("XTSE3087 : An XSL stylesheet may have at most one 'global-context-item' "
		   		                                                                      + "instruction. The stylesheet has " 
				                                                                      + globalContextItemVector.size() 
				                                                                      + " 'global-context-item' instructions.", srcLocator);
		}
		else {
			ElemGlobalContextItem elemGlobalContextItem1 = (ElemGlobalContextItem)(globalContextItemVector.get(0));
			String useStr = elemGlobalContextItem1.getUse();
			if (useStr != null) {
			   useStrList.add(useStr);
			}
			
			String asAttrStr = elemGlobalContextItem1.getAs();
			if (asAttrStr != null) {
				asAttrList.add(asAttrStr); 
			}
		}
		
		int size1 = useStrList.size();
		String useStrValue = null;
		if (size1 > 0) {
		   useStrValue = useStrList.get(0); 
		}
		
		for (int idx = 1; idx < size1; idx++) {
		   String str1 = (useStrList.get(idx)).trim();
		   if (!str1.equals(useStrValue.trim())) {
			   throw new TransformerException("XTSE3087 : An XSL stylesheet contains two 'global-context-item' "
			   		                                                                                + "instructions with conflicting "
			   		                                                                                + "values for attribute 'use'.", srcLocator); 
		   }
		}
		
		size1 = asAttrList.size();
		String asStrValue = null;
		if (size1 > 0) {
			asStrValue = asAttrList.get(0); 
		}
		
		for (int idx = 1; idx < size1; idx++) {
		   String str1 = asAttrList.get(idx);
		   str1 = str1.replaceAll("\\s+", "");
		   asStrValue = asStrValue.replaceAll("\\s+", "");
		   if (!str1.equals(asStrValue)) {
			   throw new TransformerException("XTSE3087 : An XSL stylesheet contains two 'global-context-item' instructions "
			   		                                                                                + "with conflicting item types.", srcLocator); 
		   }
		}
		
		if (!(Constants.ATTRVAL_REQUIRED.equals(m_useAttr) || Constants.ATTRVAL_OPTIONAL.equals(m_useAttr) || Constants.ATTRVAL_ABSENT.equals(m_useAttr))) {
		   throw new TransformerException("XTSE0020 : An XSL instruction 'global-context-item''s attribute \"use\" "
		   		                                                                       + "may have possible value 'required', "
		   		                                                                       + "'optional', 'absent'. The provided value '" 
		   		                                                                       + m_useAttr + "' is invalid.", srcLocator);
		}
		
		if (Constants.ATTRVAL_ABSENT.equals(m_useAttr) && (m_asAttr != null)) {
		   throw new TransformerException("XTSE3089 : An XSL instruction 'global-context-item' cannot have "
		   		                                                                       + "simultaneously \"as\" atribute, and \"use\" "
		   		                                                                       + "attribute with value 'absent'.", srcLocator);
		}
	}

}
