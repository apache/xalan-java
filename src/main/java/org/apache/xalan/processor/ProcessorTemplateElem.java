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
package org.apache.xalan.processor;

import java.util.Map;
import java.util.Vector;

import javax.xml.transform.TransformerException;

import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.templates.Constants;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.ElemVariable;
import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xalan.xslt.util.XslTransformData;
import org.apache.xml.dtm.DTM;
import org.apache.xml.utils.QName;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;
import org.xml.sax.Attributes;

/**
 * This class processes parse events for an XSLT template element.
 * @see <a href="http://www.w3.org/TR/xslt#dtd">XSLT DTD</a>
 * @see <a href="http://www.w3.org/TR/xslt#section-Creating-the-Result-Tree">section-Creating-the-Result-Tree in XSLT Specification</a>
 */
public class ProcessorTemplateElem extends XSLTElementProcessor
{
  static final long serialVersionUID = 8344994001943407235L;
  
  /**
   * Class field, to support XSL transformation of stylesheet 
   * attribute "use-when".
   */
  protected boolean m_isUseWhenExclude = false;

  /**
   * Receive notification of the start of an element.
   *
   * @param handler non-null reference to current StylesheetHandler that is constructing the Templates.
   * @param uri The Namespace URI, or an empty string.
   * @param localName The local name (without prefix), or empty string if not namespace processing.
   * @param rawName The qualified name (with prefix).
   * @param attributes The specified or defaulted attributes.
   */
  public void startElement(
          StylesheetHandler handler, String uri, String localName, String rawName, Attributes attributes)
            throws org.xml.sax.SAXException
  {
	  
	  super.startElement(handler, uri, localName, rawName, attributes);
	  
	  try
	  {
		  XSLTElementDef def = getElemDef();
		  Class classObject = def.getClassObject();
		  ElemTemplateElement elem = null;

		  try
		  {
			  elem = (ElemTemplateElement) classObject.newInstance();

			  elem.setDOMBackPointer(handler.getOriginatingNode());
			  elem.setLocaterInfo(handler.getLocator());
			  elem.setPrefixes(handler.getNamespaceSupport());

			  verifyXSLAllowedAttributes(localName, attributes, elem, handler);
		  }
		  catch (InstantiationException ie)
		  {
			  handler.error(XSLTErrorResources.ER_FAILED_CREATING_ELEMTMPL, null, ie);//"Failed creating ElemTemplateElement instance!", ie);
		  }
		  catch (IllegalAccessException iae)
		  {
			  handler.error(XSLTErrorResources.ER_FAILED_CREATING_ELEMTMPL, null, iae);//"Failed creating ElemTemplateElement instance!", iae);
		  }

		  setPropertiesFromAttributes(handler, rawName, attributes, elem);
		   
		  if ((Constants.S_XSLNAMESPACEURL).equals(uri) && (attributes != null)) {
			  String useWhenAttrXPathStr = attributes.getValue("", Constants.ATTRNAME_USE_WHEN);
			  
			  if (useWhenAttrXPathStr != null) {				  
				  XslTransformData.m_use_when = true;
				  XPathContext xctxt = new XPathContext();
				  StylesheetRoot stylesheetRoot = XslTransformData.m_stylesheetRoot;
				  Vector vars = new Vector();
				  ElemTemplateElement elemTemplateElement = stylesheetRoot.getFirstChildElem();
				  
				  while (elemTemplateElement != null) {
					 if (elemTemplateElement instanceof ElemVariable) {
						ElemVariable elemVar = (ElemVariable)elemTemplateElement;						
						if (elemVar.getStatic()) {
						   // Only static variables may be referred, within 
						   // XSL attribute use-when's XPath expression.
						   vars.add(elemVar);	
						}						 
					 }
					 
					 elemTemplateElement = elemTemplateElement.getNextSiblingElem();
				  }
				  
				  int idx = vars.size();				  
				  Map<QName, XObject> varMap = xctxt.getXPathVarMap();				  
				  
				  while (--idx >= 0) {
					  ElemVariable elemVariable = (ElemVariable)(vars.elementAt(idx));					  
					  QName varName = elemVariable.getName();					  
					  XPath xpathObj = elemVariable.getSelect();
					  
					  XObject xObj = xpathObj.execute(xctxt, DTM.NULL, null);
					  
					  varMap.put(varName, xObj);
				  }				  				  
				  
				  XPath useWhenXPath = null;
				  
				  String xpathDefaultNs = null;
				  				  				  
				  if ((Constants.ELEMNAME_TEMPLATE_STRING).equals(localName) && (Constants.S_XSLNAMESPACEURL).equals(uri)) {					 					  

					  int attrLength = attributes.getLength();

					  for (int idx2 = 0; idx2 < attrLength; idx2++) {
						  String attrLocalName = attributes.getLocalName(idx2);              

						  if ((Constants.ATTRNAME_XPATH_DEFAULT_NAMESPACE).equals(attrLocalName)) {
							  xpathDefaultNs = attributes.getValue(idx); 

							  break;
						  }
					  }

					  if (xpathDefaultNs == null) {
						  StylesheetRoot stylesheet = (StylesheetRoot)(handler.getElemTemplateElement());

						  xpathDefaultNs = stylesheet.getXpathDefaultNamespace();
					  }
				  }
				  
				  try {	
					  if (xpathDefaultNs != null) {
					     useWhenXPath = new XPath(useWhenAttrXPathStr, null, handler, XPath.SELECT, null, xpathDefaultNs);
					  }
					  else {
						 useWhenXPath = new XPath(useWhenAttrXPathStr, null, handler, XPath.SELECT, null);
					  }
					  
					  XObject xObj = useWhenXPath.execute(xctxt, DTM.NULL, handler);
					  
					  if (!xObj.bool()) {
						  m_isUseWhenExclude = true; 
					  }
				  }
				  catch (TransformerException ex) {
					  String xpathExprStr = useWhenXPath.getPatternString();
					  
					  throw new org.xml.sax.SAXException("XPST0003 : An XPath evaluation error occured, while evaluating XSL stylesheet attribute 'use-when' " + 
																								                                       xpathExprStr + ". Any variable references within "
																								                                       + "XPath 'use-when' expression must be static.");
				  }
				  finally {
					  XslTransformData.m_use_when = false;
					  varMap.clear();
				  }
			  }
		  }

		  appendAndPush(handler, elem);
	  }
	  catch(TransformerException te)
	  {
		  throw new org.xml.sax.SAXException(te);
	  }
  }

  /**
   * Append the current template element to the current
   * template element, and then push it onto the current template
   * element stack.
   *
   * @param handler non-null reference to current StylesheetHandler that is constructing the Templates.
   * @param elem non-null reference to a the current template element.
   *
   * @throws org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   */
  protected void appendAndPush(
          StylesheetHandler handler, ElemTemplateElement elem)
            throws org.xml.sax.SAXException
  {

	  ElemTemplateElement parent = handler.getElemTemplateElement();
	  if (null != parent)  // defensive, for better multiple error reporting. -sb
	  {
		  try {
			  if (!m_isUseWhenExclude) {
				  parent.appendChild(elem);
			  }
		  }
		  finally {
			  m_isUseWhenExclude = false;
		  }

		  handler.pushElemTemplateElement(elem);
	  }
  }

  /**
   * Receive notification of the end of an element.
   *
   * @param handler non-null reference to current StylesheetHandler that is constructing the Templates.
   * @param uri The Namespace URI, or an empty string.
   * @param localName The local name (without prefix), or empty string if not namespace processing.
   * @param rawName The qualified name (with prefix).
   */
  public void endElement(
          StylesheetHandler handler, String uri, String localName, String rawName)
            throws org.xml.sax.SAXException
  {
	  super.endElement(handler, uri, localName, rawName);
	  handler.popElemTemplateElement().setEndLocaterInfo(handler.getLocator());
  }
  
  /**
   * Method definition to verify attributes that can be present on 
   * specific XSLT instructions as per XSLT 3.0 specification.
   * 
   * @param localName					An XSLT instruction's local name for an instruction
   *                                    that is present within the stylesheet.
   * @param attributes					XSLT instruction whose local name is localName, it's 
   *                                    attributes that are present within the stylesheet.	
   * @param elem 
   * @param handler 
   */
  private void verifyXSLAllowedAttributes(String localName, Attributes attributes, ElemTemplateElement elem, StylesheetHandler handler) 
		                                                                      throws org.xml.sax.SAXException {
	  if ((Constants.ELEMNAME_FOREACHGROUP_STRING).equals(localName) || (Constants.ELEMNAME_COPY_OF_STRING).equals(localName)) {
		  int attrCount = attributes.getLength();
		  if (attrCount > 0) {
			  XSLTElementDef elemDef = getElemDef();
			  for (int idx = 0; idx < attrCount; idx++) {
				  String attrLocalName = attributes.getLocalName(idx);
				  XSLTAttributeDef attrDef = elemDef.getAttributeDef(null, attrLocalName);
				  if (attrDef == null) {
					  TransformerException te = new TransformerException("XTSE0090 : An attribute '" + attrLocalName + "' is not allowed "
							  															          + "to appear on XSL element \"" + localName + "\".", elem);					  
					  handler.error(XSLTErrorResources.ER_FAILED_CREATING_ELEMTMPL, null, te);
				  }
			  }
		  }
	  }
  }
  
}
