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
package org.apache.xpath.operations;

import java.util.Iterator;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.transform.SourceLocator;

import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xalan.templates.XMLNSDecl;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xalan.xslt.util.XslTransformSharedDatastore;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xpath.ArithmeticOperation;
import org.apache.xpath.ExpressionNode;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.compiler.OpCodes;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.w3c.dom.Node;

import xml.xpath31.processor.types.XSDecimal;
import xml.xpath31.processor.types.XSDouble;
import xml.xpath31.processor.types.XSFloat;
import xml.xpath31.processor.types.XSInteger;

/**
 * An XPath 'idiv' operation implementation.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 */
public class IDiv extends ArithmeticOperation
{

  private static final long serialVersionUID = 5138215729063791579L;

  /**
   * Apply the operation to two operands, and return the result.
   *
   * @param left non-null reference to the evaluated left operand.
   * @param right non-null reference to the evaluated right operand.
   *
   * @return non-null reference to the XObject that represents the result of the operation.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject operate(XObject left, XObject right) throws javax.xml.transform.TransformerException
  {
	  XObject result = null;
	  
	  java.lang.String arg0Str = null;
	  
	  if ((left instanceof XSInteger) || (left instanceof XSDecimal) || 
			                                                      (left instanceof XSFloat) || 
			                                                      (left instanceof XSDouble) || 
	                                                              (left instanceof XNumber)) {
		 arg0Str = XslTransformEvaluationHelper.getStrVal(left);		 
	  }
	  
	  java.lang.String arg1Str = XslTransformEvaluationHelper.getStrVal(right);
	  
	  java.lang.String xpathCastAsStr = "(" + arg0Str + " div " + arg1Str + ") cast as xs:integer";
	  
	  ExpressionNode expressionNode = getExpressionOwner();
	  if (expressionNode instanceof ElemTemplateElement) {
		  ElemTemplateElement elemTemplateElement = (ElemTemplateElement)expressionNode;
		  
		  ExpressionNode stylesheetRootNode = null; 
		  while (expressionNode != null) {
			 stylesheetRootNode = expressionNode;
			 expressionNode = expressionNode.exprGetParent();                     
		  }

		  StylesheetRoot stylesheetRoot = (StylesheetRoot)stylesheetRootNode;
		  TransformerImpl transformerImpl = stylesheetRoot.getTransformerImpl();
		  
		  XPathContext xctxt = transformerImpl.getXPathContext();		  		  
		  SourceLocator srcLocator = xctxt.getSAXLocator();
		  
  	      List<XMLNSDecl> prefixTable = (List<XMLNSDecl>)elemTemplateElement.getPrefixTable();
  	      Iterator<XMLNSDecl> iter = prefixTable.iterator();
  	      
  	      boolean isXsNsDeclAvailable = false;  	      
  	      while (iter.hasNext()) {
  	    	 XMLNSDecl xmlNSDecl = iter.next();
  	    	 java.lang.String uri = xmlNSDecl.getURI();
  	    	 if ((XMLConstants.W3C_XML_SCHEMA_NS_URI).equals(uri)) {
  	    		isXsNsDeclAvailable = true;
  	    		
  	    		break;
  	    	 }  	    	 
  	      }
  	     
  	      try {
  	    	  // Add XML Schema namespace binding to Xalan-J namespace prefix table, 
  			  // if this namespace binding is currently not there in prefix table.
  	    	  if (!isXsNsDeclAvailable) {
  	    		  prefixTable.add(new XMLNSDecl("xs", XMLConstants.W3C_XML_SCHEMA_NS_URI, false));  
  	    	  }

  	    	  IDivEvaluatorPrefixResolver iDivOpPrefixResolver = new IDivEvaluatorPrefixResolver(prefixTable);

  	    	  XPath xpath = new XPath(xpathCastAsStr, srcLocator, iDivOpPrefixResolver, XPath.SELECT, null);

  	    	  XslTransformSharedDatastore.xpathCallingOpCode = OpCodes.OP_IDIV;

  	    	  // Get the result of XPath 'idiv' operator evaluation
  	    	  result = xpath.execute(xctxt, xctxt.getCurrentNode(), xctxt.getNamespaceContext());
  	      }
  	      finally {
  	    	  // Remove XML Schema namespace binding from Xalan-J namespace prefix 
  	    	  // table, that was previously added to evaluate XPath 'idiv' operation.
  	    	  
  	    	  if (!isXsNsDeclAvailable) {
  	    		  iter = prefixTable.iterator();
  	    		  while (iter.hasNext()) {
  	    			  XMLNSDecl xmlNSDecl = iter.next();
  	    			  java.lang.String uri = xmlNSDecl.getURI();
  	    			  if ((XMLConstants.W3C_XML_SCHEMA_NS_URI).equals(uri)) {
  	    				  prefixTable.remove(xmlNSDecl);

  	    				  break;
  	    			  }  	    	 
  	    		  }
  	    	  }
  	      }
	  }

	  return result;
  }
  
  /**
   * An PrefixResolver class definition for XPath 'idiv' operation 
   * implementation.
   */
  class IDivEvaluatorPrefixResolver implements PrefixResolver {

	List<XMLNSDecl> prefixTable = null;
	  
	public IDivEvaluatorPrefixResolver(List<XMLNSDecl> prefixTable) {
		this.prefixTable = prefixTable;	
	}
	
	@Override
	public java.lang.String getNamespaceForPrefix(java.lang.String prefix) {
		
		java.lang.String result = null;

		Iterator<XMLNSDecl> iter = prefixTable.iterator();
		while (iter.hasNext()) {
			XMLNSDecl xmlNSDecl = iter.next();
			java.lang.String uri = xmlNSDecl.getURI();
			if ((XMLConstants.W3C_XML_SCHEMA_NS_URI).equals(uri)) {
				result = uri;
			}  	    	 
		}
		
		return result;
	}

	@Override
	public java.lang.String getNamespaceForPrefix(java.lang.String prefix, Node context) {
		// no op
		return null;
	}

	@Override
	public java.lang.String getBaseIdentifier() {
		// no op
		return null;
	}

	@Override
	public boolean handlesNullPrefixes() {
		// no op
		return false;
	}
	  
  }

}
