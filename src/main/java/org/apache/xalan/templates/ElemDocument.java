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

import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xml.serializer.SerializationHandler;
import org.apache.xml.utils.QName;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.composite.SequenceTypeData;
import org.apache.xpath.composite.SequenceTypeKindTest;
import org.apache.xpath.composite.SequenceTypeSupport;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XObject;

/**
 * Implementation of XSLT 3.0 xsl:document instruction.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class ElemDocument extends ElemTemplateElement {
  
  private static final long serialVersionUID = -4941523610958927295L;
  
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
  }

  /**
   * This after the template's children have been composed.
   */
  public void endCompose(StylesheetRoot sroot) throws TransformerException
  {
	  super.endCompose(sroot);
  }

  /**
   * Get an int constant identifying the type of element.
   * @see org.apache.xalan.templates.Constants
   *
   * @return The token id for this element
   */
  public int getXSLToken()
  {
	  return Constants.ELEMNAME_DOCUMENT;
  }

  /**
   * Return the node name.
   *
   * @return The element's name
   */
  public String getNodeName()
  {
	  return Constants.ELEMNAME_DOCUMENT_STRING;
  }

  /**
   * Method definition, to evaluate xsl:document instruction.
   *
   * @param transformer non-null reference to the the current transform-time state.
   *
   * @throws TransformerException
   */
  public void execute(TransformerImpl transformer) throws TransformerException
  {
    
	  XPathContext xctxt = transformer.getXPathContext();

	  SourceLocator srcLocator = xctxt.getSAXLocator();
	  
	  int contextNode = xctxt.getCurrentNode(); 

	  QName type = getType();

	  String validationStr = getValidation();

	  if ((type != null) && (validationStr != null)) {
		  throw new TransformerException("XTTE1540 : An XSL 'document' instruction cannot have both the attributes "
				                                                                                      + "'type' and 'validation'.", srcLocator); 
	  }
	  
	  SerializationHandler handler = transformer.getSerializationHandler();	  	  	  	  
	  
	  boolean isCharMarkerToEmit = false;
	  
	  ElemTemplateElement elemTemplateElement = getParentElem();
	  if (elemTemplateElement instanceof ElemVariable) {
		 ElemVariable elemVariable = (ElemVariable)elemTemplateElement;
		 String asAttrValue = elemVariable.getAs();
		 if (asAttrValue != null) { 			 
			 XPath seqTypeXPath = new XPath(asAttrValue, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null, true);            
			 XObject seqTypeExpressionEvalResult = seqTypeXPath.execute(xctxt, contextNode, xctxt.getNamespaceContext());            
			 SequenceTypeData seqExpectedTypeData = (SequenceTypeData)seqTypeExpressionEvalResult;
			 
			 SequenceTypeKindTest seqTypeKindTest = seqExpectedTypeData.getSequenceTypeKindTest();
			 if ((seqTypeKindTest != null) && !(seqTypeKindTest.getKindVal() == SequenceTypeSupport.DOCUMENT_KIND)) {
				isCharMarkerToEmit = true;
			 }
		 }
	  }
	  else if (elemTemplateElement instanceof ElemTemplate) {
		 ElemTemplate elemTemplate = (ElemTemplate)elemTemplateElement;
		 String asAttrValue = elemTemplate.getAs();
		 if ((asAttrValue != null) && ((elemTemplate.getMatch() == null) && (elemTemplate.getName() != null)) 
				                                                                                 && !(elemTemplateElement instanceof ElemFunction)) { 			 
			 XPath seqTypeXPath = new XPath(asAttrValue, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null, true);            
			 XObject seqTypeExpressionEvalResult = seqTypeXPath.execute(xctxt, contextNode, xctxt.getNamespaceContext());            
			 SequenceTypeData seqExpectedTypeData = (SequenceTypeData)seqTypeExpressionEvalResult;
			 
			 SequenceTypeKindTest seqTypeKindTest = seqExpectedTypeData.getSequenceTypeKindTest();
			 int itemTypeOccurenceIndicator = seqExpectedTypeData.getItemTypeOccurrenceIndicator();
			 if ((seqTypeKindTest != null) && !((seqTypeKindTest.getKindVal() == SequenceTypeSupport.DOCUMENT_KIND) || 
					                            (itemTypeOccurenceIndicator == SequenceTypeSupport.OccurrenceIndicator.ABSENT))) {
				isCharMarkerToEmit = true;
			 }
		 }
	  }
	  
	  try {
		  if (isCharMarkerToEmit) {
			 char[] charArray = (Constants.XSL_DOCUMENT_INSTRUCTION_MARKER).toCharArray();
			 
		     handler.characters(charArray, 0, charArray.length);
		  }

		  int rootNodeHandleOfRtf = transformer.transformToRTF(this);

		  XMLNodeCursorImpl xmlNodeCursorImpl = new XMLNodeCursorImpl(rootNodeHandleOfRtf, xctxt);	  	  

		  ElemCopyOf.copyOfActionOnNodeSet(xmlNodeCursorImpl, transformer, handler, xctxt);
	  } 
	  catch (Exception ex) {
		  // no op
	  }
  }

  /**
   * Add a child to the child list.
   *
   * @param newChild Child to add to this node's child list
   *
   * @return Child just added to child list
   */
  public ElemTemplateElement appendChild(ElemTemplateElement newChild)
  {	 	  
	  return super.appendChild(newChild);
  }

  /**
   * Call the children visitors.
   * @param visitor The visitor whose appropriate method will be called.
   */
  protected void callChildVisitors(XSLTVisitor visitor, boolean callAttrs)
  {  	  	
	  super.callChildVisitors(visitor, callAttrs);
  }

}
