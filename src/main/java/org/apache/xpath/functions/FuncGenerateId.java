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

import org.apache.xml.dtm.DTM;
import org.apache.xpath.XPathContext;
import org.apache.xpath.axes.SelfIteratorNoPredicate;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;
import org.apache.xpath.objects.XdmAttributeItem;
import org.apache.xpath.objects.XdmCommentItem;
import org.apache.xpath.objects.XdmProcessingInstructionItem;

import xml.xpath31.processor.types.XSString;

/**
 * Implementation of XPath 3.1 fn:generate-id function.
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
	   m_defined_arity = new Short[] { 0, 1 };
	}

  /**
   * Implementation of the function. The function must return
   * a valid object.
   * 
   * @param xctxt                     The current XPath evaluation context
   * @return                          A valid XObject
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
  {
	  XObject result = null;
	  
	  if ((m_arg0 == null) || (m_arg0 instanceof SelfIteratorNoPredicate)) {
		 XObject contextItem = xctxt.getXPath3ContextItem();
		 
		 if (contextItem != null) {
			 if (contextItem instanceof XdmAttributeItem) {
				 result = new XSString(((XdmAttributeItem)contextItem).getIdValue()); 
			 }
			 else if (contextItem instanceof XdmCommentItem) {
				 result = new XSString(((XdmCommentItem)contextItem).getIdValue());
			 }
			 else if (contextItem instanceof XdmProcessingInstructionItem) {
				 result = new XSString(((XdmProcessingInstructionItem)contextItem).getIdValue());
			 }
			 
			 if (result != null) {
				 return result;
			 }
		 }		 		 
	  }

	  int which = getArg0AsNode(xctxt);

	  if (DTM.NULL != which)
	  {
		  // Note that this is a different value than in previous releases
		  // of Xalan. It's sensitive to the exact encoding of the node
		  // handle anyway, so fighting to maintain backward compatability
		  // really didn't make sense; it may change again as we continue
		  // to experiment with balancing document and node numbers within
		  // that value.
		  result = new XSString("N" + Integer.toHexString(which).toUpperCase());
	  }
	  else
		  result = new XSString((XString.EMPTYSTRING).str());
	  
	  return result;
  }
}
