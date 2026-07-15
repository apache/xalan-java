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
package org.apache.xpath.functions;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xml.dtm.DTM;
import org.apache.xpath.XPathContext;
import org.apache.xpath.axes.SelfIteratorNoPredicate;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;
import org.apache.xpath.objects.XdmAttributeItem;
import org.apache.xpath.objects.XdmCommentItem;
import org.apache.xpath.objects.XdmProcessingInstructionItem;

import xml.xpath31.processor.types.XSString;

/**
 * Implementation of an XPath 3.1 function fn:generate-id.
 * 
 * @xsl.usage advanced
 */
public class FuncGenerateId extends FunctionDef1Arg
{
	static final long serialVersionUID = 973544842091724273L;

	/**
	 * Class constructor.
	 */
	public FuncGenerateId() {
		m_arity = new Short[] { 0, 1 };
	}

	/**
	 * Evaluate the function. The function must return a valid object.
	 * 
	 * @param xctxt                        An XPath context object
	 * @return                             A valid XObject
	 *
	 * @throws javax.xml.transform.TransformerException
	 */
	public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
	{
		
		XObject result = null;
		
		SourceLocator srcLocator = xctxt.getSAXLocator();

		XObject xpath3CtxtItem = null;
		
		if ((m_arg0 == null) || (m_arg0 instanceof SelfIteratorNoPredicate)) {			
			xpath3CtxtItem = xctxt.getXPath3ContextItem();

			if (xpath3CtxtItem != null) {
				if (xpath3CtxtItem instanceof XdmAttributeItem) {
					result = new XSString(((XdmAttributeItem)xpath3CtxtItem).getIdValue()); 
				}
				else if (xpath3CtxtItem instanceof XdmCommentItem) {
					result = new XSString(((XdmCommentItem)xpath3CtxtItem).getIdValue());
				}
				else if (xpath3CtxtItem instanceof XdmProcessingInstructionItem) {
					result = new XSString(((XdmProcessingInstructionItem)xpath3CtxtItem).getIdValue());
				}

				if (result != null) {
					return result;
				}
			}		 		 
		}		
		
		if ((xpath3CtxtItem == null) && (m_arg0 != null)) {
			XObject xObj0 = getFunctionArgEffectiveValue(m_arg0, xctxt);

			if (!(xObj0 instanceof XMLNodeCursorImpl)) {
				throw new TransformerException("XPTY0004 : An XPath function 'generate-id' first argument "
																								+ "is not an xdm node.", srcLocator);	
			}
		}
		
		int which = getArg0AsNode(xctxt);

		if (DTM.NULL != which)
		{			
			result = new XSString("N" + Integer.toHexString(which).toUpperCase());
		}
		else
			result = new XSString((XString.EMPTYSTRING).str());

		return result;
	}
}
