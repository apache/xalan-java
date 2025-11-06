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

import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.xs.AnyURIDV;
import org.apache.xml.utils.XML11Char;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;
import org.w3c.dom.DOMException;

/**
 * Implementation of XSLT 3.0 xsl:namespace instruction.
 *
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class ElemNamespace extends ElemTemplateElement
{

  private static final long serialVersionUID = -4462559949130957853L;

 /**
   * The xsl:namespace element has a required name attribute 
   * that specifies the name of an XML namespace node. The 
   * value of the name attribute is interpreted as an attribute 
   * value template.
   */
  private AVT m_name_atv = null;

  /**
   * Set the value of "name" attribute.
   *
   * @param v Value for the name attribute
   */
  public void setName(AVT v)
  {
	  m_name_atv = v;
  }

  /**
   * Get the value of "name" attribute.
   *
   * @return The value of the "name" attribute 
   */
  public AVT getName()
  {
	  return m_name_atv;
  }
  
  /**
   * Class field to, represent the value of "xpath-default-namespace" 
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
   * is declared on xsl:namespace instruction.
   */
  private boolean m_expand_text_declared;
  
  /**
   * This class field, represents the value of "expand-text" 
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
   * The optional select attribute contains an expression.
   */
  public XPath m_selectExpression = null;

  /**
   * Set the "select" attribute.
   *
   * @param expr Expression for select attribute 
   */
  public void setSelect(XPath expr)
  {      
	  m_selectExpression = expr;
  }

  /**
   * Get the "select" attribute.
   *
   * @return Expression for select attribute 
   */
  public XPath getSelect()
  {
	  return m_selectExpression;
  }
  
  private Vector m_vars;
  
  private int m_globals_size;
  
  /**
   * This function is called after everything else has been
   * recomposed, and allows the template to set remaining
   * values that may be based on some other property that
   * depends on recomposition.
   */
  public void compose(StylesheetRoot sroot) throws TransformerException
  {
	  super.compose(sroot);
	  
	  java.util.Vector vnames = sroot.getComposeState().getVariableNames();
	  if (m_name_atv != null)
		  m_name_atv.fixupVariables(vnames, sroot.getComposeState().getGlobalsSize());

	  StylesheetRoot.ComposeState cstate = sroot.getComposeState();

	  m_vars = (Vector)vnames.clone();
	  m_globals_size = cstate.getGlobalsSize();
  }

  /**
   * Get an int constant identifying the type of element.
   * @see org.apache.xalan.templates.Constants
   *
   * @return The token ID for the element
   */
  public int getXSLToken()
  {
	  return Constants.ELEMNAME_NAMESPACE;
  }

  /**
   * Return the node name.
   *
   * @return The element's name
   */
  public String getNodeName()
  {
	  return Constants.ELEMNAME_NAMESPACE_STRING;
  }

  /**
   * Create an XML namespace node within an XSL transformation 
   * result tree. The content of the xsl:namespace element is a
   * template for the string value of the namespace instruction node.
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

	  final int contextNode = xctxt.getCurrentNode();
	  
	  SourceLocator srcLocator = xctxt.getSAXLocator();
	  
	  if ((m_selectExpression != null) && (m_xpath_default_namespace != null)) {    		
    	  m_selectExpression = new XPath(m_selectExpression.getPatternString(), srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
      }	  
	  
	  String data = null;		  
	  if (m_selectExpression != null) {
		  XObject xObj = m_selectExpression.execute(xctxt, contextNode, xctxt.getNamespaceContext());			  
		  data = XslTransformEvaluationHelper.getStrVal(xObj);
	  }
	  else {
		  data = transformer.transformToString(this);
	  }
	  
	  if ((data == null) || (data.length() == 0)) {
		  throw new TransformerException("XTDE0930 : An XSL namespace instruction, produced a zero-length string value for "
		  		                                                                           + "an XML namespace.", srcLocator); 
	  }
	  else if ("http://www.w3.org/2000/xmlns/".equals(data)) {
		  throw new TransformerException("XTDE0905 : An XSL namespace instruction, produced a disallowed URI value "
		  		                                                                           + "http://www.w3.org/2000/xmlns/.", srcLocator); 
	  }
	  
	  AnyURIDV xsAnyUriDv = new AnyURIDV();
	  try {		  		  		  		  
		  Object xsAnyUriObj1 = xsAnyUriDv.getActualValue(data, null);
	  } 
	  catch (InvalidDatatypeValueException ex) {
		  throw new TransformerException("XTDE0905 : An XSL namespace instruction, produced an URI value "
								  		                                                   + "that is not valid in the lexical space of "
								  		                                                   + "the datatype xs:anyURI.", srcLocator); 
	  }
	  
	  if (m_selectExpression != null) {
		  ElemTemplateElement elemTemplateElement = getFirstChildElem();
		  while (elemTemplateElement != null) {
			 if (!(elemTemplateElement instanceof ElemFallback)) {
				 throw new TransformerException("XTSE0910 : An XSL namespace instruction cannot have, "
												                                          + "both 'select' attribute and non-empty content "
												                                          + "with XSL instructions other than XSL fallback.", srcLocator); 
			 }
			 
			 elemTemplateElement = elemTemplateElement.getNextSiblingElem();
		  }
	  }

	  String xslNamespaceName = (m_name_atv == null) ? null : m_name_atv.evaluate(xctxt, contextNode, this);
	  if ((xslNamespaceName == null) || (xslNamespaceName.length() == 0) || !XML11Char.isXML11ValidNCName(xslNamespaceName) 
					                                                                                     || xslNamespaceName.equals("xmlns")) {
		   throw new TransformerException("XTDE0920 : An XSL namespace instruction cannot produce a name, that is "
		   		                                                                          + "zero-length string, not an XML NCName, or "
		   		                                                                          + "has a string value \"xmlns\".", srcLocator);   
	  }
	  
	  if (xslNamespaceName.equalsIgnoreCase("xml") && !xslNamespaceName.equals("http://www.w3.org/XML/1998/namespace")) {
		  throw new TransformerException("XTDE0925 : An XSL namespace instruction produced a name 'xml' and a value not equal "
		  		                                                                          + "to 'http://www.w3.org/XML/1998/namespace'", srcLocator); 
	  }
	  
      if (!xslNamespaceName.equalsIgnoreCase("xml") && xslNamespaceName.equals("http://www.w3.org/XML/1998/namespace")) {
    	  throw new TransformerException("XTDE0925 : An XSL namespace instruction produced a name other than 'xml' and a value "
    	  		                                                                          + "equal to 'http://www.w3.org/XML/1998/namespace'", srcLocator); 
	  }

	  boolean isExpandText = false;
	  if (m_expand_text_declared) {
		  isExpandText = m_expand_text;  
	  }
	  else {
		  ElemTemplateElement elemTemplateElem = getParentElem();      
		  isExpandText = getExpandTextValue(elemTemplateElem);
	  }

	  if (isExpandText) {
		  data = getStrValueAfterExpandTextProcessing(data, transformer, m_vars, m_globals_size);
	  }

	  try
	  {		  
		  transformer.getResultTreeHandler().namespaceAfterStartElement(xslNamespaceName, data);
	  }
	  catch(org.xml.sax.SAXException se)
	  {
		  throw new TransformerException(se);
	  }

	  if (transformer.getDebug())
		  transformer.getTraceManager().emitTraceEndEvent(this);
  }

  /**
   * Add a child to the child list.
   *
   * @param newChild Child to add to child list
   *
   * @return The child just added to the child list
   *
   * @throws DOMException
   */
  public ElemTemplateElement appendChild(ElemTemplateElement newChild)
  {

	  int type = ((ElemTemplateElement) newChild).getXSLToken();

	  switch (type)
	  {
	  // char-instructions 
	  case Constants.ELEMNAME_TEXTLITERALRESULT :
	  case Constants.ELEMNAME_APPLY_TEMPLATES :
	  case Constants.ELEMNAME_APPLY_IMPORTS :
	  case Constants.ELEMNAME_CALLTEMPLATE :
	  case Constants.ELEMNAME_FOREACH :
	  case Constants.ELEMNAME_VALUEOF :
	  case Constants.ELEMNAME_COPY_OF :
	  case Constants.ELEMNAME_NUMBER :
	  case Constants.ELEMNAME_CHOOSE :
	  case Constants.ELEMNAME_IF :
	  case Constants.ELEMNAME_TEXT :
	  case Constants.ELEMNAME_COPY :
	  case Constants.ELEMNAME_VARIABLE :
	  case Constants.ELEMNAME_MESSAGE :

		  // instructions 
		  // case Constants.ELEMNAME_PI:
		  // case Constants.ELEMNAME_COMMENT:
		  // case Constants.ELEMNAME_ELEMENT:
		  // case Constants.ELEMNAME_ATTRIBUTE:
		  break;
	  default :
		  error(XSLTErrorResources.ER_CANNOT_ADD,
				  new Object[]{ newChild.getNodeName(),
						  this.getNodeName() });  //"Can not add " +((ElemTemplateElement)newChild).m_elemName +

		  //" to " + this.m_elemName);
	  }

	  return super.appendChild(newChild);
  }
  
  /**
   * This after the template's children have been composed.
   */
  public void endCompose(StylesheetRoot sroot) throws TransformerException
  {
	  super.endCompose(sroot);
  }
}
