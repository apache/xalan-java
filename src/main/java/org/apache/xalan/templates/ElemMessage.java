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

import java.io.StringReader;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.xslt.util.XslTransformData;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xml.utils.QName;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import xml.xpath31.processor.types.XSString;

/**
 * Implementation of XSLT 3.0 xsl:message instruction.
 * 
 * @xsl.usage advanced
 */
public class ElemMessage extends ElemTemplateElement
{
   static final long serialVersionUID = 1530472462155060023L;
   
   /**
    * Class constructor.
    */
   public ElemMessage() {
	   if (XslTransformData.m_xsl_message_rSeq == null) {
		   XslTransformData.m_xsl_message_rSeq = new ResultSequence(); 
	   } 
   }
   
   /**
    * The optional select attribute contains an expression.
    */
   public XPath m_selectExpression = null;

   /**
    * Set the "select" attribute.
    * The optional select attribute contains an expression.
    *
    * @param expr Expression for select attribute 
    */
   public void setSelect(XPath expr)
   {      
	   m_selectExpression = expr;
   }

   /**
    * Get the "select" attribute.
    * The optional select attribute contains an expression.
    *
    * @return Expression for select attribute 
    */
   public XPath getSelect()
   {
	   return m_selectExpression;
   }

  /**
   * If the terminate attribute has the value yes, then the
   * XSLT transformer should terminate processing after sending
   * the message. The default value is no.
   */
  private AVT m_terminate = null; 

  /**
   * Set the "terminate" attribute.
   * If the terminate attribute has the value yes, then the
   * XSLT transformer should terminate processing after sending
   * the message. The default value is no.
   *
   * @param xslTerminate 				Value to set for "terminate" attribute
   */
  public void setTerminate(AVT xslTerminate)
  {
	  m_terminate = xslTerminate;
  }

  /**
   * Get the "terminate" attribute.
   * If the terminate attribute has the value yes, then the
   * XSLT transformer should terminate processing after sending
   * the message. The default value is no.
   *
   * @return value of "terminate" attribute.
   */
  public AVT getTerminate()
  {
	  return m_terminate;
  }
  
  /**
   * The value of the "error-code" attribute.
   */
  protected AVT m_errorCode;

  /**
   * Set the "error-code" attribute.
   * 
   * @param v Value to set for the "error-code" attribute.
   */
  public void setErrorCode(AVT errorCode)
  {
	  m_errorCode = errorCode;
  }

  /**
   * Get the "error-code" attribute.
   * 
   * @return Value of the "error-code" attribute.
   */
  public AVT getErrorCode()
  {
	  return m_errorCode;
  }
  
  /**
   * This class field, represents the value of "xpath-default-namespace" 
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
   * is declared on xsl:message instruction.
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
   * This class field is used during, XPath.fixupVariables(..) action 
   * as performed within object of this class.  
   */    
  private Vector m_vars;
  
  /**
   * This class field is used during, XPath.fixupVariables(..) action 
   * as performed within object of this class.  
   */
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

	  java.util.Vector vnames = (sroot.getComposeState()).getVariableNames();

	  m_vars = (Vector)(vnames.clone()); 
	  m_globals_size = (sroot.getComposeState()).getGlobalsSize();

	  if (m_selectExpression != null) {
		  m_selectExpression.fixupVariables(vnames, m_globals_size);
	  }
  }
  
  public void endCompose(StylesheetRoot sroot) throws TransformerException
  {    
	  super.endCompose(sroot);
  }

  /**
   * Get an int constant identifying the type of element.
   * @see org.apache.xalan.templates.Constants
   *
   * @return The token ID for this element
   */
  public int getXSLToken()
  {
    return Constants.ELEMNAME_MESSAGE;
  }

  /**
   * Return the node name.
   *
   * @return name of the element 
   */
  public String getNodeName()
  {
    return Constants.ELEMNAME_MESSAGE_STRING;
  }

  /**
   * Evaluate an, xsl:message instruction.
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
	  
	  SourceLocator srcLocator = xctxt.getSAXLocator(); 
	  
	  int contextNode = xctxt.getCurrentNode();
	  	  	  
	  StringBuffer strBuff = new StringBuffer();
	  
	  // The results of xsl:message instruction 'select' expression,
	  // and xsl:message's contained sequence constructor are concatenated.
	  
	  StylesheetRoot stylesheetRoot = transformer.getStylesheet();
	  
	  List<XMLNSDecl> prefixTable = getPrefixTable();
	  
	  if ((m_selectExpression != null) && (m_xpath_default_namespace != null)) {    		
   	      m_selectExpression = new XPath(m_selectExpression.getPatternString(), srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
   	  } 
	  
	  try {
		  if (m_selectExpression != null) {
			  XObject xObj = m_selectExpression.execute(xctxt, contextNode, xctxt.getNamespaceContext());		  
			  if (xObj instanceof XMLNodeCursorImpl) {
				  appendNodeStrToStrBuff(xctxt, strBuff, (XMLNodeCursorImpl)xObj, prefixTable, stylesheetRoot);
			  }
			  else if (xObj instanceof ResultSequence) {
				  ResultSequence rSeq = (ResultSequence)xObj;
				  int rSeqLength = rSeq.size();
				  for (int idx = 0; idx < rSeqLength; idx++) {
					  XObject xObj2 = rSeq.item(idx);
					  if (xObj2 instanceof XMLNodeCursorImpl) {
						  appendNodeStrToStrBuff(xctxt, strBuff, (XMLNodeCursorImpl)xObj2, prefixTable, stylesheetRoot);
					  }

					  else {
						  String strValue = XslTransformEvaluationHelper.getStrVal(xObj2);
						  strBuff.append(strValue);
					  }

					  if (idx < (rSeqLength - 1)) {
						  strBuff.append(" "); 
					  }
				  }
			  }
			  else {
				  String strValue = XslTransformEvaluationHelper.getStrVal(xObj);
				  strBuff.append(strValue);
			  } 
		  }

		  boolean isXslMessagePreprocess = false;

		  if (transformer.getOptimize()) {
			  ElemTemplateElement firstChild = this.getFirstChildElem();	  
			  if (firstChild instanceof ElemText) {
				  ElemTemplateElement nextSibling = firstChild.getNextSiblingElem();
				  boolean isXslMessageAllChildNodeText = true;
				  while (nextSibling != null) {
					  if (!((nextSibling instanceof ElemText) || (nextSibling instanceof ElemTextLiteral))) {
						  isXslMessageAllChildNodeText = false;

						  break;
					  }

					  nextSibling = nextSibling.getNextSiblingElem();
				  }

				  if (isXslMessageAllChildNodeText) {
					  isXslMessagePreprocess = true;

					  String strValue = transformer.transformToString(firstChild);
					  if (m_expand_text) {
						  strValue = getStrValueAfterExpandTextProcessing(strValue, transformer, m_vars, m_globals_size);
					  }

					  strBuff.append(strValue);
					  XslTransformData.m_xsl_message_rSeq.add(new XSString(strBuff.toString()));
				  }
			  }

			  if (!isXslMessagePreprocess && this.hasTextLitOnly()) {
				  // An xsl:message instruction, contains a single literal text child

				  isXslMessagePreprocess = true;

				  String strValue = firstChild.getNodeValue();
				  if (m_expand_text) {
					  strValue = getStrValueAfterExpandTextProcessing(strValue, transformer, m_vars, m_globals_size); 
				  }

				  strBuff.append(strValue);

				  XSString xsString = new XSString(strValue);
				  ((XObject)xsString).setUseStrictValue(true);

				  XslTransformData.m_xsl_message_rSeq.add(xsString);
			  }
		  }

		  if (!isXslMessagePreprocess) {
			  int rootNodeHandleOfRtf = transformer.transformToRTF(this);

			  if (rootNodeHandleOfRtf != DTM.NULL) {
				  DTM dtm = xctxt.getDTM(rootNodeHandleOfRtf);
				  int childNode = dtm.getFirstChild(rootNodeHandleOfRtf);
				  while (childNode != DTM.NULL) {			 			 
					  try {
						  short nodeType = dtm.getNodeType(childNode);
						  String xmlNodeStr = null;
						  Node node = dtm.getNode(childNode);
						  if (nodeType == DTM.ELEMENT_NODE) {
							  xmlNodeStr = XslTransformEvaluationHelper.serializeXmlDomElementNode(node);				 
							  int prefixTableSize = prefixTable.size();				  
							  if (prefixTableSize > 1) {
								  System.setProperty(Constants.XML_DOCUMENT_BUILDER_FACTORY_KEY, Constants.XML_DOCUMENT_BUILDER_FACTORY_VALUE);

								  DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
								  dbFactory.setNamespaceAware(true);
								  DocumentBuilder docBuilder = dbFactory.newDocumentBuilder();

								  StringReader strReader = new StringReader(xmlNodeStr);
								  InputSource inpSource = new InputSource(strReader);
								  Document document = docBuilder.parse(inpSource);
								  Element docElement = document.getDocumentElement();

								  for (int idx = 0; idx < prefixTableSize; idx++) {
									  XMLNSDecl xmlNSDecl = prefixTable.get(idx);
									  String prefix = xmlNSDecl.getPrefix();
									  String uri = xmlNSDecl.getURI();
									  if (!((Constants.S_XSLNAMESPACEURL).equals(uri)) && !isXslPrefixToBeExcluded(stylesheetRoot, prefix)) {
										  // Adding an XML namespace declaration to, an XML element node.
										  docElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + prefix, uri);
									  }
								  }

								  xmlNodeStr = XslTransformEvaluationHelper.serializeXmlDomElementNode(docElement);							  
							  }

							  int idx = xmlNodeStr.indexOf("?>");
							  xmlNodeStr = (idx > -1) ? xmlNodeStr.substring(idx + 2) : xmlNodeStr; 
						  }
						  else {
							  xmlNodeStr = node.getNodeValue();
						  }

						  strBuff.append(xmlNodeStr);
					  } 
					  catch (Exception ex) {
						  throw new TransformerException(ex.getMessage());
					  }

					  childNode = dtm.getNextSibling(childNode); 
				  }		  
			  }

			  String strValue = strBuff.toString();	  
			  if (m_expand_text_declared && m_expand_text) {
				  strValue = getStrValueAfterExpandTextProcessing(strValue, transformer, m_vars, m_globals_size); 
			  }

			  XslTransformData.m_xsl_message_rSeq.add(new XSString(strValue));
		  }
      }
	  catch (TransformerException ex) {
		  int lineNumber = srcLocator.getLineNumber();
		  int columnNumber = srcLocator.getColumnNumber();
		  XslTransformData.m_xsl_message_rSeq.add(new XSString("line : " + lineNumber + ", "
		  		                                                                      + "column : " + columnNumber + " :: " + ex.getMessage())); 
	  }
	  
	  if (m_terminate != null) {
		 String xslTerminateStr = m_terminate.evaluate(xctxt, contextNode, xctxt.getNamespaceContext());		 
		 xslTerminateStr = xslTerminateStr.trim();
		 if (!("yes".equals(xslTerminateStr) || "true".equals(xslTerminateStr) || "1".equals(xslTerminateStr) || 
				                                                               "no".equals(xslTerminateStr) || 
				                                                               "false".equals(xslTerminateStr) || 
				                                                               "0".equals(xslTerminateStr))) {
			 throw new TransformerException("XTSE0020 : An XSL instruction message's terminate attribute value " + xslTerminateStr + " "
											 		                                                             + "is not allowed. The values must be "
											 		                                                             + "one of following, (0|1|false|no|true|yes).", srcLocator);
		 }
		 
		 if ("yes".equals(xslTerminateStr) || "true".equals(xslTerminateStr) || "1".equals(xslTerminateStr)) {
			 String errCodeStr = "Q{" + Constants.XSL_ERROR_NAMESACE + "}XTMM9000";
			 if (m_errorCode != null) {
				 String errCodeAvtEvaluatedStr = m_errorCode.evaluate(xctxt, contextNode, xctxt.getNamespaceContext());				 				 
				 String errCodeStr2 = getErrorCodeString(errCodeAvtEvaluatedStr);
				 errCodeStr = (errCodeStr2 != null) ? errCodeStr2 : errCodeStr;   
			 } 

			 StringBuffer strBuff2 = new StringBuffer();
			 ResultSequence rSeq = XslTransformData.m_xsl_message_rSeq;
			 int rSeqlength = rSeq.size();
    		 for (int idx = 0; idx < rSeqlength; idx++) {
    			XObject xObj = rSeq.item(idx);
    			String xdmItemStr1 = null;
    			if (xObj instanceof XMLNodeCursorImpl) {
    			    XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)xObj;
    			    int nodeHandle = xmlNodeCursorImpl.asNode(xctxt);
    			    DTM dtm2 = xctxt.getDTM(nodeHandle);
    			    Node node = dtm2.getNode(nodeHandle);
    			    try {
    			       xdmItemStr1 = XslTransformEvaluationHelper.serializeXmlDomElementNode(node);    			       
    			       int prefixTableSize = prefixTable.size();
    			       if ((dtm2.getNodeType(nodeHandle) == DTM.ELEMENT_NODE) && (prefixTableSize > 1)) {
    			    	   System.setProperty(Constants.XML_DOCUMENT_BUILDER_FACTORY_KEY, Constants.XML_DOCUMENT_BUILDER_FACTORY_VALUE);

    			    	   DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    			    	   dbFactory.setNamespaceAware(true);
    			    	   DocumentBuilder docBuilder = dbFactory.newDocumentBuilder();

    			    	   StringReader strReader = new StringReader(xdmItemStr1);
    			    	   InputSource inpSource = new InputSource(strReader);
    			    	   Document document = docBuilder.parse(inpSource);
    			    	   Element docElement = document.getDocumentElement();

    			    	   for (int idx2 = 0; idx2 < prefixTableSize; idx2++) {
    			    		   XMLNSDecl xmlNSDecl = prefixTable.get(idx2);
    			    		   String prefix = xmlNSDecl.getPrefix();
    			    		   String uri = xmlNSDecl.getURI();
    			    		   if (!((Constants.S_XSLNAMESPACEURL).equals(uri)) && !isXslPrefixToBeExcluded(stylesheetRoot, prefix)) {
    			    			   // Adding an XML namespace declaration to, an XML element node.
    			    			   docElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + prefix, uri);
    			    		   }
    			    	   }

    			    	   xdmItemStr1 = XslTransformEvaluationHelper.serializeXmlDomElementNode(docElement);							  
    			       }
    			       
    			       xdmItemStr1 = xdmItemStr1.trim();
    					 
    			       int idx2 = xdmItemStr1.indexOf("?>");
    			       xdmItemStr1 = (idx2 > -1) ? xdmItemStr1.substring(idx2 + 2) : xdmItemStr1;					   					   
					} 
    			    catch (Exception ex) {
                       throw new TransformerException(ex.getMessage());
					}
    			}
    			else {
    				xdmItemStr1 = XslTransformEvaluationHelper.getStrVal(xObj);
    			}
    			
    			strBuff2.append(xdmItemStr1);
    		 }
			 
			 throw new TransformerException(errCodeStr + " : An XSL stylesheet processing has been aborted by 'message' "
							 		                                                                      + "instruction, with following message trace : '" 
									                                                                      + strBuff2.toString() + "'.", srcLocator);
		 }
	  }

	  if (transformer.getDebug())
		  transformer.getTraceManager().emitTraceEndEvent(this); 
  }
  
  public ElemTemplateElement appendChild(ElemTemplateElement newChild)
  {
	  super.appendChild(newChild);
	  
	  return newChild;
  }

  /**
   * Method definition, to append xdm node contents to the
   * supplied string buffer.
   * 
   * @param xctxt										An XPath context object
   * @param strBuff                                     The supplied StringBuffer
   *                                                    object.
   * @param xmlNodeCursorImpl                           An XMLNodeCursorImpl object instance
   * @throws TransformerException
   */
  private void appendNodeStrToStrBuff(XPathContext xctxt, StringBuffer strBuff, 
		                                                                     XMLNodeCursorImpl xmlNodeCursorImpl,
		                                                                     List<XMLNSDecl> prefixTable,
		                                                                     StylesheetRoot stylesheetRoot)
		  																			throws TransformerException {
	  int nodeHandle = xmlNodeCursorImpl.asNode(xctxt);
	  DTM dtm = xctxt.getDTM(nodeHandle); 
	  if (dtm.getNodeType(nodeHandle) == DTM.ATTRIBUTE_NODE) {
		  strBuff.append(dtm.getNodeValue(nodeHandle));
	  }
	  else {
		  Node node = dtm.getNode(nodeHandle);
		  try {
			  String xmlNodeStr = XslTransformEvaluationHelper.serializeXmlDomElementNode(node);
			  int prefixTableSize = prefixTable.size();			  
			  if ((dtm.getNodeType(nodeHandle) == DTM.ELEMENT_NODE) && (prefixTableSize > 1)) {
				  System.setProperty(Constants.XML_DOCUMENT_BUILDER_FACTORY_KEY, Constants.XML_DOCUMENT_BUILDER_FACTORY_VALUE);

				  DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				  dbFactory.setNamespaceAware(true);
				  DocumentBuilder docBuilder = dbFactory.newDocumentBuilder();

				  StringReader strReader = new StringReader(xmlNodeStr);
				  InputSource inpSource = new InputSource(strReader);
				  Document document = docBuilder.parse(inpSource);
				  Element docElement = document.getDocumentElement();
				  							  
				  for (int idx = 0; idx < prefixTableSize; idx++) {
					 XMLNSDecl xmlNSDecl = prefixTable.get(idx);
					 String prefix = xmlNSDecl.getPrefix();
					 String uri = xmlNSDecl.getURI();
					 if (!((Constants.S_XSLNAMESPACEURL).equals(uri)) && !isXslPrefixToBeExcluded(stylesheetRoot, prefix)) {
						// Adding an XML namespace declaration to, an XML element node.
						docElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + prefix, uri);
					 }
				  }
				  
				  xmlNodeStr = XslTransformEvaluationHelper.serializeXmlDomElementNode(docElement);							  
			  }
			  
			  int idx = xmlNodeStr.indexOf("?>");
			  xmlNodeStr = (idx > -1) ? xmlNodeStr.substring(idx + 2) : xmlNodeStr;
			  strBuff.append(xmlNodeStr);
		  } 
		  catch (Exception ex) {
			  throw new TransformerException(ex.getMessage());
		  } 
	  }
  }
  
  /**
   * Method definition, to check whether XSL stylesheet's 'exclude-result-prefixes'
   * attribute contains the supplied prefix value.
   * 
   * @param stylesheetRoot						An XSL stylesheet's StylesheetRoot 
   *                                            object. 
   * @param prefix                              The supplied prefix value.
   * @return
   */
  private boolean isXslPrefixToBeExcluded(StylesheetRoot stylesheetRoot, String prefix) {
	  
	  boolean result = false;
	  
	  int count = stylesheetRoot.getExcludeResultPrefixCount();
	  for (int idx = 0; idx < count; idx++) {
		 String prefix2 = stylesheetRoot.getExcludeResultPrefix(idx);
		 if (prefix.equals(prefix2)) {
			 result = true;
			 
			 break;
		 }
	  }
	  
	  return result;
  }
  
  /**
   * Method definition, to get XPath 3.1 URIQualifiedName string value,
   * using the supplied xsl:message's attribute error-code's expanded 
   * AVT value. 
   * 
   * @param errCodeAvtEvaluatedStr             The supplied error-code string value
   * 
   * @return                URIQualifiedName string value, or null                  
   */
  private String getErrorCodeString(String errCodeAvtEvaluatedStr) {

	  String result = null;

	  try {
		  if (errCodeAvtEvaluatedStr.startsWith("Q{")) {
			  int idx = errCodeAvtEvaluatedStr.indexOf('}');
			  if (idx > 2) {
				  String nsUri = errCodeAvtEvaluatedStr.substring(2, idx);
				  String localName = errCodeAvtEvaluatedStr.substring(idx + 1);

				  // This validates the QName value
				  QName errorQname = new QName(nsUri, localName, true);
				  
				  result = errCodeAvtEvaluatedStr; 
			  }
		  }
		  else {						 
			  int idx = errCodeAvtEvaluatedStr.indexOf(':');
			  if (idx > -1) {
				  String prefix = errCodeAvtEvaluatedStr.substring(0, idx);
				  String nsUri = getNamespaceForPrefix(prefix);
				  String localName = errCodeAvtEvaluatedStr.substring(idx + 1);

				  // This validates the QName value
				  QName errorQname = new QName(nsUri, localName, true);

				  result = "Q{" + nsUri + "}" + localName;
			  }
			  else {
				  // This validates the QName value
				  QName errorQname = new QName(null, errCodeAvtEvaluatedStr, true);

				  result = "Q{}" + errCodeAvtEvaluatedStr;
			  }
		  }
	  }
	  catch(Exception ex) {
		  // no op
	  }

	  return result;
  }
  
}
