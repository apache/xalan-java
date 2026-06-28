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

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xml.utils.XMLString;
import org.apache.xpath.XPathContext;
import org.apache.xpath.axes.SelfIteratorNoPredicate;
import org.apache.xpath.composite.XPathNamedFunctionReference;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathArray;
import org.apache.xpath.objects.XPathInlineFunction;
import org.apache.xpath.objects.XPathMap;
import org.apache.xpath.patterns.NodeTest;

import xml.xpath31.processor.types.XSQName;
import xml.xpath31.processor.types.XSString;

/**
 * Implementation of an XPath 3.1 function fn:string.
 * 
 * @xsl.usage advanced
 */
public class FuncString extends FunctionDef1Arg
{
	static final long serialVersionUID = -2206677149497712883L;

	/**
	 * Class constructor.
	 */
	public FuncString() {
		m_defined_arity = new Short[] {0, 1}; 
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
		
		final int sourceNode = xctxt.getContextNode();
		
		XObject xObj0 = null;
		
		if (m_arg0 != null) {
		   if (m_arg0 instanceof SelfIteratorNoPredicate) {
			   if ((xctxt.getXPath3ContextItem() == null) && (sourceNode == DTM.NULL)) {
				   throw new javax.xml.transform.TransformerException("XPDY0002 : An XPath 3.1 function 'string' is called "
																					                            + "with an argument set to \".\", and an "
																					                            + "XPath context item is absent.", srcLocator);  
			   }
		   }
		   else if (m_arg0 instanceof NodeTest) {
				if (XslTransformEvaluationHelper.isNodeTestExpressionFuntionType((NodeTest)m_arg0)) {
					throw new javax.xml.transform.TransformerException("FOTY0014 : An XPath 3.1 function 'string' has argument as "
																												+ "function item, whose string value "
																												+ "is not defined.", srcLocator);  
				} 
		   }
		   else if (m_arg0 instanceof XPathInlineFunction) {
				throw new javax.xml.transform.TransformerException("FOTY0014 : An XPath 3.1 function 'string' has argument as "
																												+ "function item, whose string value "
																												+ "is not defined.", srcLocator);
		   }
		   else if (m_arg0 instanceof XPathNamedFunctionReference) {
				throw new javax.xml.transform.TransformerException("FOTY0014 : An XPath 3.1 function 'string' has argument as "
																												+ "function item, whose string value "
																												+ "is not defined.", srcLocator);
		   }
		   else {	
		        xObj0 = getFunctionArgEffectiveValue(m_arg0, xctxt);
		   }
		}
		else if ((xctxt.getXPath3ContextItem() == null) && (sourceNode == DTM.NULL)) {
		   throw new javax.xml.transform.TransformerException("XPDY0002 : An XPath 3.1 function 'string' is called "
		   		                                                                           + "without an argument, and an "
		   		                                                                           + "XPath context item is absent.", srcLocator);
		}		
						
		if (xObj0 instanceof XSQName) {
		   XSQName xsQName = (XSQName)xObj0;
		   String prefix = xsQName.getPrefix();
		   if ((prefix != null) && !"".equals(prefix)) {
			  String str1 = prefix + ":" + xsQName.getLocalPart();
			  
			  result = new XSString(str1);
			  
			  return result;
		   }
		}
		
		if (xObj0 instanceof XPathMap) {
		   throw new javax.xml.transform.TransformerException("FOTY0014 : An XPath 3.1 function 'string' is called "
                                                                                            + "with an xdm map as argument.", srcLocator);
		}
		else if (xObj0 instanceof XPathArray) {
		   throw new javax.xml.transform.TransformerException("FOTY0014 : An XPath 3.1 function 'string' is called "
                                                                                            + "with an xdm array as argument.", srcLocator);
		}		
		else if ((xObj0 instanceof ResultSequence) && (((ResultSequence)xObj0).size() > 1)) {
		   throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath 3.1 function 'string' is called "
                                                                                            + "with an argument whose cardinality is "
                                                                                            + "greater than one.", srcLocator);
		}

		XMLString xmlStr = getArg0AsString(xctxt);

		result = new XSString(xmlStr.toString()); 

		return result;
	}
  
}
