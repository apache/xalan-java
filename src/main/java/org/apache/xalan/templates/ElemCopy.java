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

import org.apache.xalan.serialize.SerializerUtils;
import org.apache.xalan.transformer.ClonerToResultTree;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xalan.xslt.util.XslTransformSharedDatastore;
import org.apache.xerces.impl.xs.XSElementDecl;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xml.serializer.CharacterMapConfig;
import org.apache.xml.serializer.SerializationHandler;
import org.apache.xml.serializer.SerializerBase;
import org.apache.xml.utils.QName;
import org.apache.xpath.Expression;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.composite.SequenceTypeSupport;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XRTreeFrag;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Implementation of XSLT xsl:copy instruction.
 * 
 * @xsl.usage advanced
 */
public class ElemCopy extends ElemUse
{
   static final long serialVersionUID = 5478580783896941384L;

  /**
   * Get an int constant identifying the type of element.
   * @see org.apache.xalan.templates.Constants
   *
   * @return The token ID for this element 
   */
  public int getXSLToken()
  {
    return Constants.ELEMNAME_COPY;
  }

  /**
   * Return the node name.
   *
   * @return This element's name
   */
  public String getNodeName()
  {
    return Constants.ELEMNAME_COPY_STRING;
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
  
  public void compose(StylesheetRoot sroot) throws TransformerException
  {
	  super.compose(sroot);

	  StylesheetRoot.ComposeState cstate = sroot.getComposeState();
	  java.util.Vector vnames = cstate.getVariableNames();
	  int golbalsSize = cstate.getGlobalsSize();

	  m_vars = (java.util.Vector)(vnames.clone());
	  m_globals_size = golbalsSize; 
  }

  /**
   * The xsl:copy element provides an easy way of copying the current node.
   * Executing this function creates a copy of the current node into the
   * result tree.
   * <p>The namespace nodes of the current node are automatically
   * copied as well, but the attributes and children of the node are not
   * automatically copied. The content of the xsl:copy element is a
   * template for the attributes and children of the created node;
   * the content is instantiated only for nodes of types that can have
   * attributes or children (i.e. root nodes and element nodes).</p>
   * <p>The root node is treated specially because the root node of the
   * result tree is created implicitly. When the current node is the
   * root node, xsl:copy will not create a root node, but will just use
   * the content template.</p>
   *
   * @param transformer non-null reference to the the current transform-time state.
   *
   * @throws TransformerException
   */
  public void execute(TransformerImpl transformer) throws TransformerException
  {
	  XPathContext xctxt = transformer.getXPathContext();

	  SourceLocator srcLocator = xctxt.getSAXLocator();
	  
	  try
	  {
		  int sourceNode = xctxt.getCurrentNode();
		  xctxt.pushCurrentNode(sourceNode);
		  DTM dtm = xctxt.getDTM(sourceNode);
		  short nodeType = dtm.getNodeType(sourceNode);		  

		  QName type = getType();
		  String validation = getValidation();

		  if ((type != null) && (validation != null)) {
			  throw new TransformerException("XTTE1540 : An xsl:copy instruction cannot have both the attributes "
					  																	   + "'type' and 'validation'.", srcLocator); 
		  }

		  if ((DTM.DOCUMENT_NODE != nodeType) && (DTM.DOCUMENT_FRAGMENT_NODE != nodeType))
		  {
			  SerializationHandler rthandler = transformer.getSerializationHandler();

			  if (transformer.getDebug())
				  transformer.getTraceManager().emitTraceEvent(this);
			  
			  if (nodeType == DTM.TEXT_NODE) {				  				  
				  XMLNodeCursorImpl xdmNode = new XMLNodeCursorImpl(sourceNode, xctxt);
				  String nodeStrValue = xdmNode.str();				  
				  if (rthandler instanceof SerializerBase) {					  					  
			    	  SerializerBase serializerBase = (SerializerBase)rthandler;
			    	  CharacterMapConfig charMapConfig = serializerBase.getCharMapConfig();
			    	  if (charMapConfig != null) {
			    		 // xsl:character-map transformation
			    	     nodeStrValue = XslTransformEvaluationHelper.characterMapTransformation(nodeStrValue, charMapConfig);
			    	  }
			    	  
			    	  rthandler.characters(nodeStrValue.toCharArray(), 0, nodeStrValue.length());			    	  
			      }
				  else {
					  ClonerToResultTree.cloneToResultTree(sourceNode, nodeType, dtm, rthandler, false); 
				  }				  
			  }
			  else {
			      ClonerToResultTree.cloneToResultTree(sourceNode, nodeType, dtm, rthandler, false);
			  }

			  if (DTM.ELEMENT_NODE == nodeType)
			  {
				  super.execute(transformer);
				  SerializerUtils.processNSDecls(rthandler, sourceNode, nodeType, dtm);

				  if (type != null) {
					  StylesheetRoot stylesheetRoot = XslTransformSharedDatastore.stylesheetRoot;
					  XSModel xsModel = stylesheetRoot.getXsModel();        			          			          			  
					  if (xsModel != null) {
						  XSTypeDefinition xsTypeDefn = xsModel.getTypeDefinition(type.getLocalName(), type.getNamespace());
						  if (xsTypeDefn != null) {					  
							  validateWithSchemaTypeAndEmitElement(sourceNode, dtm, xsTypeDefn, transformer, xctxt);
						  }
						  else {
							  throw new TransformerException("FODC0005 : An xsl:copy instruction has 'type' attribute with "
																				   + "value '" + type.getLocalName() + "' to request "
																				   + "validation of xsl:copy's result, but the schema referred via "
																				   + "xsl:import-schema instruction does'nt have a global type definition "
																				   + "with name '" + type.getLocalName() + "'.", srcLocator); 
						  }
					  }
					  else {
						  throw new TransformerException("FODC0005 : An xsl:copy instruction has 'type' attribute to request "
																				   + "validation of xsl:copy's result, but an XML input document has not "
																				   + "been validated using schema supplied via xsl:import-schema instruction.", 
																				   srcLocator); 
					  }
				  }
				  else if (validation != null) {
					  if (!isValidationStrOk(validation)) {
						  throw new TransformerException("XTTE1540 : An xsl:copy instruction's attribute 'validation' can only have one of following "
								  												   + "values : strict, lax, preserve, strip.", srcLocator);  
					  }
					  else if ((Constants.XS_VALIDATION_STRICT_STRING).equals(validation)) {
						  StylesheetRoot stylesheetRoot = XslTransformSharedDatastore.stylesheetRoot;
						  XSModel xsModel = stylesheetRoot.getXsModel();        			          			          			  
						  if (xsModel != null) {
							  Node srcNode = dtm.getNode(sourceNode);
							  String nodeLocalName = srcNode.getLocalName();
							  String nodeNamespace = srcNode.getNamespaceURI();							  
							  XSElementDecl schemaElemDecl = (XSElementDecl)(xsModel.getElementDeclaration(nodeLocalName, nodeNamespace));							  
							  if (schemaElemDecl != null) {
								  validateWithElemDeclAndEmitElement(sourceNode, nodeLocalName, schemaElemDecl, transformer, xctxt);								  
							  }
							  else {
								  throw new TransformerException("FODC0005 : An xsl:copy instruction has 'validation' attribute with value '" + 
																				 			    Constants.XS_VALIDATION_STRICT_STRING + "' to request validation of "
																							    + "xsl:copy's result, but the schema used to validate "
																							    + "an XML input document doesn't have global element declaration for "
																							    + "an element node produced by xsl:copy instruction.", srcLocator);
							  }
						  }
						  else {
							  throw new TransformerException("FODC0005 : An xsl:copy instruction has 'validation' attribute to request "
																						       + "validation of xsl:copy's result, but an XML input "
																						       + "document has not been validated using schema supplied "
																						       + "via xsl:import-schema instruction.", srcLocator); 
						  }
					  }
					  else if ((Constants.XS_VALIDATION_LAX_STRING).equals(validation)) {
						  StylesheetRoot stylesheetRoot = XslTransformSharedDatastore.stylesheetRoot;
						  XSModel xsModel = stylesheetRoot.getXsModel();        			          			          			  
						  if (xsModel != null) {
							  Node srcNode = dtm.getNode(sourceNode);
							  String nodeLocalName = srcNode.getLocalName();
							  String nodeNamespace = srcNode.getNamespaceURI();							  
							  XSElementDecl schemaElemDecl = (XSElementDecl)(xsModel.getElementDeclaration(nodeLocalName, nodeNamespace));							  
							  if (schemaElemDecl != null) {
								  validateWithElemDeclAndEmitElement(sourceNode, nodeLocalName, schemaElemDecl, transformer, xctxt);								  
							  }							  
						  }						  
					  }
					  
					  // The validation value 'strip' requires no validation

					  // The validation value 'preserve' is currently not implemented
				  }
				  else {
					  transformer.executeChildTemplates(this, true);
				  }

				  String ns = dtm.getNamespaceURI(sourceNode);
				  String localName = dtm.getLocalName(sourceNode);
				  transformer.getResultTreeHandler().endElement(ns, localName, dtm.getNodeName(sourceNode));
			  }
			  if (transformer.getDebug())
				  transformer.getTraceManager().emitTraceEndEvent(this);         
		  }
		  else
		  {
			  if (transformer.getDebug())
				  transformer.getTraceManager().emitTraceEvent(this);

			  super.execute(transformer);
			  transformer.executeChildTemplates(this, true);

			  if (transformer.getDebug())
				  transformer.getTraceManager().emitTraceEndEvent(this);
		  }
	  }
	  catch(org.xml.sax.SAXException se) {
		  throw new TransformerException(se);
	  }
	  catch (TransformerException ex) {
		  throw new TransformerException(ex.getMessage(), srcLocator); 
	  }
	  catch (Exception ex) {
		  String errMesg = ex.getMessage();
		  throw new TransformerException("XTTE1540 : An error occured while evaluating an XSL stylesheet "
																				  + "xsl:copy instruction." 
																				  + ((errMesg != null) ? " " + errMesg : ""), srcLocator);
	  }
	  finally
	  {
		  xctxt.popCurrentNode();
	  }
  }

  /**
   * This method first constructs an XML string information, from which an XML 
   * element node is formed. The method then validates the formed XML element node 
   * with a schema type, and emits element to XSL transform's output if 
   * validation succeeds.
   */
  private void validateWithSchemaTypeAndEmitElement(int contextNode, DTM dtm, XSTypeDefinition xsTypeDefn, 
		                                            TransformerImpl transformer, XPathContext xctxt) throws TransformerException {		   

	  Node srcNode = dtm.getNode(contextNode);
	  String nodeLocalName = srcNode.getLocalName();
	  
	  SourceLocator srcLocator = xctxt.getSAXLocator();

	  StringBuffer strBuff = getAttrInformationStrBuff(nodeLocalName, contextNode, transformer, xctxt);

	  String xmlStrValue = (strBuff.toString()).trim() + ">";

	  int nodeHandle = transformer.transformToRTF(this);
	  NodeList nodeList = (new XRTreeFrag(nodeHandle, xctxt, this)).convertToNodeset();
	  if (nodeList != null) {
		  Node node = nodeList.item(0);
		  String xmlStr = null;    		 
		  try {
			  xmlStr = XslTransformEvaluationHelper.serializeXmlDomElementNode(node);
			  xmlStrValue = xmlStrValue + xmlStr.substring(xmlStr.indexOf("?>") + 2);
			  xmlStrValue += "</" + nodeLocalName + ">";
			  xmlStrValue = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + xmlStrValue;
			  if (SequenceTypeSupport.isXmlStrValid(xmlStrValue, null, xsTypeDefn)) {
				  transformer.executeChildTemplates(this, true); 
			  }									  
		  } 
		  catch (Exception ex) {
			  String errMesg = ex.getMessage();
			  throw new TransformerException("XTTE1540 : An error occured while evaluating an XSL stylesheet "
																					  + "xsl:copy instruction." 
																					  + ((errMesg != null) ? " " + errMesg : ""), srcLocator);
		  }
	  }
  }

  /**
   * This method first constructs an XML string information, from which an XML 
   * element node is formed. The method then validates the formed XML element node 
   * with a schema element declaration, and emits element to XSL transform's output 
   * if validation succeeds.
   */
  private void validateWithElemDeclAndEmitElement(int contextNode, String nodeLocalName, XSElementDecl schemaElemDecl, 
		                                          TransformerImpl transformer, XPathContext xctxt) throws TransformerException {	  	  
	  
	  StringBuffer strBuff = getAttrInformationStrBuff(nodeLocalName, contextNode, transformer, xctxt);

	  String xmlStrValue = (strBuff.toString()).trim() + ">";
	  
	  SourceLocator srcLocator = xctxt.getSAXLocator();

	  int nodeHandle = transformer.transformToRTF(this);
	  NodeList nodeList = (new XRTreeFrag(nodeHandle, xctxt, this)).convertToNodeset();
	  if (nodeList != null) {
		  Node node = nodeList.item(0);
		  String xmlStr = null;    		 
		  try {
			  xmlStr = XslTransformEvaluationHelper.serializeXmlDomElementNode(node);
			  xmlStrValue = xmlStrValue + xmlStr.substring(xmlStr.indexOf("?>") + 2);
			  xmlStrValue += "</" + nodeLocalName + ">";
			  xmlStrValue = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + xmlStrValue;
			  if (SequenceTypeSupport.isXmlStrValid(xmlStrValue, schemaElemDecl, null)) {
				  transformer.executeChildTemplates(this, true); 
			  }									  
		  } 
		  catch (Exception ex) {			  
			  String errMesg = ex.getMessage();			  
			  throw new TransformerException("XTTE1540 : An error occured while evaluating an XSL stylesheet "
																						  + "xsl:copy instruction." 
																						  + ((errMesg != null) ? " " + errMesg : ""), srcLocator);
		  }
	  }
   }
  
   /**
   * This method analyzes xsl:copy instruction's child nodes that are adjacent 
   * xsl:copy-of (that emits attributes) and xsl:attribute instructions, and string 
   * information serialization of all such attributes is returned by this method.
   */
   private StringBuffer getAttrInformationStrBuff(String nodeLocalName, int sourceNode, 
		                                          TransformerImpl transformer, XPathContext xctxt) throws TransformerException {
		
	   StringBuffer strBuff = new StringBuffer();

	   strBuff.append("<" + nodeLocalName + " ");

	   Node childElem = getFirstChild();
	   int attrCount = 0;
	   while (childElem instanceof ElemAttribute) {
		   // xsl:copy's first child instruction is xsl:attribute  
		   attrCount++;
		   ElemAttribute elemAttr = (ElemAttribute)childElem;
		   AVT attrAvtName = elemAttr.getName();
		   String attrName = attrAvtName.evaluate(xctxt, sourceNode, xctxt.getNamespaceContext());
		   String attrValue = null;
		   Expression attrSelectExpr = elemAttr.getSelect();
		   if (attrSelectExpr != null) {					            	  
			   if (m_vars != null) {
				   attrSelectExpr.fixupVariables(m_vars, m_globals_size);   
			   }

			   XObject xpathEvalResult = attrSelectExpr.execute(xctxt);
			   attrValue = XslTransformEvaluationHelper.getStrVal(xpathEvalResult);
		   }
		   else {					                            
			   attrValue = transformer.transformToString(elemAttr);
		   }

		   strBuff.append(attrName + "=\"" + attrValue + "\" ");

		   childElem = childElem.getNextSibling();		   
		   while (childElem instanceof ElemCopyOf) {
			   ElemCopyOf elemCopyOf = (ElemCopyOf)childElem;
			   XPath copyOfSelectXPath = elemCopyOf.getSelect();
			   Expression xpathExpr = copyOfSelectXPath.getExpression();
			   XObject xObject = xpathExpr.execute(xctxt);
			   if (xObject instanceof XMLNodeCursorImpl) {
				   XMLNodeCursorImpl xNodeSet = (XMLNodeCursorImpl)xObject;
				   DTMCursorIterator dtmIter = xNodeSet.iter();
				   int nodeHandle;
				   while ((nodeHandle = dtmIter.nextNode()) != DTM.NULL) {
					   DTM dtm1 = dtmIter.getDTM(nodeHandle);
					   Node node = dtm1.getNode(nodeHandle);
					   if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
						   attrCount++;
						   attrName = node.getLocalName();
						   attrValue = node.getNodeValue();
						   strBuff.append(attrName + "=\"" + attrValue + "\" ");
					   }
				   }
			   }
			   
			   childElem = childElem.getNextSibling();
		   }
	   }

	   if (attrCount == 0) {
		   childElem = getFirstChild();
		   String attrName = null;
		   String attrValue = null;
		   while (childElem instanceof ElemCopyOf) {
			   // xsl:copy's first child instruction is xsl:copy-of
			   ElemCopyOf elemCopyOf = (ElemCopyOf)childElem;
			   XPath copyOfSelectXPath = elemCopyOf.getSelect();
			   Expression xpathExpr = copyOfSelectXPath.getExpression();
			   XObject xObject = xpathExpr.execute(xctxt);
			   if (xObject instanceof XMLNodeCursorImpl) {				   				   
				   XMLNodeCursorImpl xNodeSet = (XMLNodeCursorImpl)xObject;
				   DTMCursorIterator dtmIter = xNodeSet.iter();
				   int nodeHandle;
				   while ((nodeHandle = dtmIter.nextNode()) != DTM.NULL) {
					   DTM dtm1 = dtmIter.getDTM(nodeHandle);
					   Node node = dtm1.getNode(nodeHandle);
					   if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
						   attrName = node.getLocalName();
						   attrValue = node.getNodeValue();
						   strBuff.append(attrName + "=\"" + attrValue + "\" ");
					   }
				   }
			   }

			   childElem = childElem.getNextSibling();			   
			   while (childElem instanceof ElemAttribute) {
				   ElemAttribute elemAttr = (ElemAttribute)childElem;
				   AVT attrAvtName = elemAttr.getName();
				   attrName = attrAvtName.evaluate(xctxt, sourceNode, xctxt.getNamespaceContext());
				   attrValue = null;
				   Expression attrSelectExpr = elemAttr.getSelect();
				   if (attrSelectExpr != null) {					            	  
					   if (m_vars != null) {
						   attrSelectExpr.fixupVariables(m_vars, m_globals_size);   
					   }

					   XObject xpathEvalResult = attrSelectExpr.execute(xctxt);
					   attrValue = XslTransformEvaluationHelper.getStrVal(xpathEvalResult);
				   }
				   else {					                            
					   attrValue = transformer.transformToString(elemAttr);
				   }

				   strBuff.append(attrName + "=\"" + attrValue + "\" ");
				   
				   childElem = childElem.getNextSibling();
			   }
		   }
	   }

	   return strBuff;
	}

}
