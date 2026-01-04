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
 * Implementation of XSLT 3.0 xsl:assert instruction.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class ElemAssert extends ElemTemplateElement
{

   private static final long serialVersionUID = 6406258984413843996L;
   
   /**
    * The mandatory test attribute contains an expression.
    */
   public XPath m_testExpression = null;

   /**
    * Set the "test" attribute.
    * The mandatory test attribute contains an expression.
    *
    * @param expr Expression for test attribute 
    */
   public void setTest(XPath expr)
   {      
	   m_testExpression = expr;
   }

   /**
    * Get the "test" attribute.
    * The mandatory test attribute contains an expression.
    *
    * @return Expression for test attribute 
    */
   public XPath getTest()
   {
	   return m_testExpression;
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
   * Class field, that represents the value of "xpath-default-namespace" 
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
   * is declared on xsl:assert instruction.
   */
  private boolean m_expand_text_declared;
  
  /**
   * Class field, that represents the value of "expand-text" 
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

  /**
   * Get an int constant identifying the type of element.
   * @see org.apache.xalan.templates.Constants
   *
   * @return           The token id for this element
   */
  public int getXSLToken()
  {
    return Constants.ELEMNAME_ASSERT;
  }

  /**
   * Return the node name.
   *
   * @return name of the element 
   */
  public String getNodeName()
  {
    return Constants.ELEMNAME_ASSERT_STRING;
  }

  /**
   * Evaluate an, xsl:assert instruction.
   *
   * @param transformer non-null reference to the the current transform-time state.
   *
   * @throws TransformerException
   */
  public void execute(TransformerImpl transformer) throws TransformerException
  {

	  if (transformer.getDebug())
		  transformer.getTraceManager().emitTraceEvent(this);
	  
	  StylesheetRoot stylesheetRoot = transformer.getStylesheet();
	  
	  if (!stylesheetRoot.isAssertEnabled()) {
		 // XSL asserts are disabled by default
		  
		 return; 
	  }
	  
	  XPathContext xctxt = transformer.getXPathContext();
	  
	  SourceLocator srcLocator = xctxt.getSAXLocator(); 
	  
	  int contextNode = xctxt.getCurrentNode();	  	  	  	  
	  
	  try {
		  
		  if (m_xpath_default_namespace != null) {    		
			  m_testExpression = new XPath(m_testExpression.getPatternString(), srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
			  
			  if (m_selectExpression != null) {    		
		   	      m_selectExpression = new XPath(m_selectExpression.getPatternString(), srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
		   	  }
	   	  }
		  
		  XObject xtestResult = m_testExpression.execute(xctxt, contextNode, xctxt.getNamespaceContext());

		  if (!xtestResult.bool()) {
			  // An XSL assertion has failed. The XSL transformation is 
			  // aborted with an XPath dynamic error.
			  
			  StringBuffer strBuff = new StringBuffer();			  
			  
			  List<XMLNSDecl> prefixTable = getPrefixTable();
			  
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
							  appendNodeStrToStrBuff(xctxt, strBuff, (XMLNodeCursorImpl)xObj, prefixTable, stylesheetRoot);
						  }
						  else {
							  String strValue = XslTransformEvaluationHelper.getStrVal(xObj);
							  strBuff.append(strValue);
						  }
					  }
				  }
				  else {
					  String strValue = XslTransformEvaluationHelper.getStrVal(xObj);
					  strBuff.append(strValue);
				  } 
			  }

			  int rootNodeHandleOfRtf = transformer.transformToRTF(this);
			  
			  if (rootNodeHandleOfRtf != DTM.NULL) {
				  DTM dtm = xctxt.getDTM(rootNodeHandleOfRtf);
				  int childNode = dtm.getFirstChild(rootNodeHandleOfRtf);
				  while (childNode != DTM.NULL) {			 
					  Node node = dtm.getNode(childNode);
					  try {						  						  						  						  
						  String xmlNodeStr = XslTransformEvaluationHelper.serializeXmlDomElementNode(node);						  						  						  						  						  
						  int prefixTableSize = prefixTable.size();						  
						  if ((dtm.getNodeType(childNode) == DTM.ELEMENT_NODE) && (prefixTableSize > 1)) {
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

					  childNode = dtm.getNextSibling(childNode); 
				  }		  
			  }

			  String errCodeStr = "XTMM9001";
			  if (m_errorCode != null) {
				  errCodeStr = m_errorCode.evaluate(xctxt, contextNode, xctxt.getNamespaceContext()); 
			  }

	    	  String osName = System.getProperty("os.name");
	    	  String newLineSeq = null;
			  if (osName.startsWith("Windows")) {
				  newLineSeq = "\r\n"; 
			  }
			  else {
				  newLineSeq = "\n";
			  }
			  
			  StringBuffer strBuff2 = new StringBuffer();			  
			  if ((XslTransformData.m_xsl_message_rSeq != null) && ((XslTransformData.m_xsl_message_rSeq).size() > 0)) {    		  
	    		  ResultSequence rSeq = XslTransformData.m_xsl_message_rSeq;
	    		  int rSeqLength = rSeq.size();
	    		  for (int idx = 0; idx < rSeqLength; idx++) {
	    			  XSString xsString = (XSString)(rSeq.item(idx));
	    			  String strValue = ((xsString.stringValue()).trim()); 	    			  
	    			  strBuff2.append(strValue + newLineSeq);
	    		  }
			  }
			  
			  String accumulatedXslTransformResultStr = strBuff2.toString();
			  if (accumulatedXslTransformResultStr.length() > 0) {
				 accumulatedXslTransformResultStr = accumulatedXslTransformResultStr + newLineSeq;  
			  }
			  
			  String strValue = strBuff.toString();	  
			  if (m_expand_text_declared && m_expand_text) {
				 strValue = getStrValueAfterExpandTextProcessing(strValue, transformer, m_vars, m_globals_size); 
			  }

			  throw new TransformerException((accumulatedXslTransformResultStr.length() > 0 ? accumulatedXslTransformResultStr : "") 
					                                                                       + errCodeStr + " : An XSL stylesheet processing has been aborted by 'assert' "
																						   + "instruction, with following message trace : '" 
																						   + strValue + "'.", srcLocator);
		  }
      }
	  catch (Exception ex) {
		  if (ex instanceof TransformerException) {
			 throw ex; 
		  }
		  else {
			 throw new TransformerException("XTTE0505 : An XSL stylesheet processing has failed with an error, while evaluating "
                                                                                                                    + "an xsl:assert instruction.", srcLocator);
		  }
	  }

	  if (transformer.getDebug())
		  transformer.getTraceManager().emitTraceEndEvent(this); 
  }
  
  /**
   * Method definition, to append xdm node's string serialization 
   * to the supplied string buffer.
   * 
   * @param xctxt										An XPath context object
   * @param strBuff                                     The supplied StringBuffer object.
   * @param xmlNodeCursorImpl                           An XMLNodeCursorImpl object instance
   * @param prefixTable                                 An XSL stylesheet's, XML namespace prefix 
   *                                                    table list.
   * @stylesheetRoot                                    An XSL stylesheet's StylesheetRoot object                                                     
   * @throws TransformerException
   */
  private void appendNodeStrToStrBuff(XPathContext xctxt, StringBuffer strBuff, 
		                                                        XMLNodeCursorImpl xmlNodeCursorImpl, 
		                                                        List<XMLNSDecl> prefixTable, StylesheetRoot stylesheetRoot)
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
  
}
