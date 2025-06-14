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

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xml.serializer.SerializationHandler;
import org.apache.xml.utils.QName;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.composite.SequenceTypeData;
import org.apache.xpath.composite.SequenceTypeSupport;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XNodeSetForDOM;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathInlineFunction;
import org.apache.xpath.objects.XRTreeFrag;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Implementation of XSLT 3.0 xsl:template element.
 * 
 * @xsl.usage advanced
 */
public class ElemTemplate extends ElemTemplateElement
{
  
  static final long serialVersionUID = -5283056789965384058L;
  
  /** 
   * The public identifier for the current document event.
   */
  private String m_publicId;

  /** 
   * The system identifier for the current document event.
   */
  private String m_systemId;

  /**
   * Return the public identifier for the current document event.
   * <p>This will be the public identifier
   * @return A string containing the public identifier, or
   *         null if none is available.
   * @see #getSystemId
   */
  public String getPublicId()
  {
	  return m_publicId;
  }

  /**
   * Return the system identifier for the current document event.
   *
   * <p>If the system identifier is a URL, the parser must resolve it
   * fully before passing it to the application.</p>
   *
   * @return A string containing the system identifier, or null
   *         if none is available.
   * @see #getPublicId
   */
  public String getSystemId()
  {
	  return m_systemId;
  }

  /**
   * Set the location information for this element.
   *
   * @param locator SourceLocator holding location information 
   */
  public void setLocaterInfo(SourceLocator locator)
  {
	  m_publicId = locator.getPublicId();
	  m_systemId = locator.getSystemId();

	  super.setLocaterInfo(locator);
  }

  /**
   * The owning stylesheet.
   */
  private Stylesheet m_stylesheet;

  /**
   * Get the stylesheet composed (resolves includes and
   * imports and has methods on it that return "composed" properties.
   * 
   * @return The stylesheet composed.
   */
  public StylesheetComposed getStylesheetComposed()
  {
	  return m_stylesheet.getStylesheetComposed();
  }

  /**
   * Get the owning stylesheet.
   *
   * @return The owning stylesheet.
   */
  public Stylesheet getStylesheet()
  {
	  return m_stylesheet;
  }

  /**
   * Set the owning stylesheet.
   *
   * @param sheet The owning stylesheet for this element
   */
  public void setStylesheet(Stylesheet sheet)
  {
	  m_stylesheet = sheet;
  }

  /**
   * Get the root stylesheet.
   *
   * @return The root stylesheet for this element
   */
  public StylesheetRoot getStylesheetRoot()
  {
	  return m_stylesheet.getStylesheetRoot();
  }

  /**
   * The match attribute is a Pattern that identifies the source
   * node or nodes to which the rule applies.
   */
  private XPath m_matchPattern = null;

  /**
   * Set the "match" attribute.
   * The match attribute is a Pattern that identifies the source
   * node or nodes to which the rule applies. The match attribute
   * is required unless the xsl:template element has a name
   * attribute. It is an error for the value of the match attribute 
   * to contain a VariableReference.
   *
   * @param v Value to set for the "match" attribute
   */
  public void setMatch(XPath v)
  {
	  m_matchPattern = v;
  }

  /**
   * Get the "match" attribute.
   * The match attribute is a Pattern that identifies the source
   * node or nodes to which the rule applies. The match attribute
   * is required unless the xsl:template element has a name
   * attribute. It is an error for the value of the match attribute 
   * to contain a VariableReference.
   *
   * @return Value of the "match" attribute 
   */
  public XPath getMatch()
  {
	  return m_matchPattern;
  }

  /**
   * An xsl:template element with a name attribute specifies a named template.
   */
  protected QName m_name = null;

  /**
   * Set the "name" attribute.
   * An xsl:template element with a name attribute specifies a named template.
   * If an xsl:template element has a name attribute, it may, but need not,
   * also have a match attribute.
   *
   * @param v Value to set the "name" attribute
   */
  public void setName(QName v)
  {
	  m_name = v;
  }

  /**
   * Get the "name" attribute.
   * An xsl:template element with a name attribute specifies a named template.
   * If an xsl:template element has a name attribute, it may, but need not,
   * also have a match attribute.
   *
   * @return Value of the "name" attribute
   */
  public QName getName()
  {
	  return m_name;
  }

  /**
   * Modes allow an element to be processed multiple times,
   * each time producing a different result.
   */
  private QName m_mode;

  /**
   * Set the "mode" attribute.
   * Modes allow an element to be processed multiple times,
   * each time producing a different result. If xsl:template
   * does not have a match attribute, it must not have a mode attribute.
   *
   * @param v Value to set the "mode" attribute
   */
  public void setMode(QName v)
  {
	  m_mode = v;
  }

  /**
   * Get the "mode" attribute.
   * Modes allow an element to be processed multiple times,
   * each time producing a different result.  If xsl:template
   * does not have a match attribute, it must not have a mode attribute.
   *
   * @return Value of the "mode" attribute
   */
  public QName getMode()
  {
	  return m_mode;
  }

  /**
   * The value of the "as" attribute.
   */
  private String m_asAttr;

  /**
   * Set the "as" attribute.
   */
  public void setAs(String val) {
	  m_asAttr = val;
  }

  /**
   * Get the "as" attribute.
   */
  public String getAs()
  {
	  return m_asAttr;
  }

  /**
   * The priority of a template rule is specified by the priority
   * attribute on the template rule.
   */
  private double m_priority = XPath.MATCH_SCORE_NONE;

  /**
   * Set the "priority" attribute.
   * The priority of a template rule is specified by the priority
   * attribute on the template rule. The value of this must be a
   * real number (positive or negative), matching the production
   * Number with an optional leading minus sign (-).
   *
   * @param v The value to set for the "priority" attribute
   */
  public void setPriority(double v)
  {
	  m_priority = v;
  }

  /**
   * Get the "priority" attribute.
   * The priority of a template rule is specified by the priority
   * attribute on the template rule. The value of this must be a
   * real number (positive or negative), matching the production
   * Number with an optional leading minus sign (-).
   *
   * @return The value of the "priority" attribute
   */
  public double getPriority()
  {
	  return m_priority;
  }

  /**
   * Get an int constant identifying the type of element.
   * @see org.apache.xalan.templates.Constants
   *
   * @return The token ID for the element
   */
  public int getXSLToken()
  {
	  return Constants.ELEMNAME_TEMPLATE;
  }

  /**
   * Return the node name.
   *
   * @return The element's name
   */
  public String getNodeName()
  {
	  return Constants.ELEMNAME_TEMPLATE_STRING;
  }
  
  /**
   * The stack frame size for this template, which is equal to the maximum number 
   * of params and variables that can be declared in the template at one time.
   */
  public int m_frameSize;
  
  /**
   * The size of the portion of the stack frame that can hold parameter 
   * arguments.
   */
  int m_inArgsSize;
  
  /**
   * List of namespace/local-name pairs, DTM style, that are unique 
   * qname identifiers for the arguments.  The position of a given qname 
   * in the list is the argument ID, and thus the position in the stack
   * frame.
   */
  private int[] m_argsQNameIDs;
  
  /**
   * This function is called after everything else has been
   * recomposed, and allows the template to set remaining
   * values that may be based on some other property that
   * depends on recomposition.
   */
  public void compose(StylesheetRoot sroot) throws TransformerException
  {
	  super.compose(sroot);
	  
	  StylesheetRoot.ComposeState cstate = sroot.getComposeState();
	  java.util.Vector vnames = cstate.getVariableNames();
	  if (null != m_matchPattern)
		  m_matchPattern.fixupVariables(vnames, sroot.getComposeState().getGlobalsSize());

	  cstate.resetStackFrameSize();
	  m_inArgsSize = 0;
  }
  
  /**
   * This method is called after the template's children have been composed.
   */
  public void endCompose(StylesheetRoot sroot) throws TransformerException
  {
	  StylesheetRoot.ComposeState cstate = sroot.getComposeState();
	  super.endCompose(sroot);
	  m_frameSize = cstate.getFrameSize();

	  cstate.resetStackFrameSize();
  }

  /**
   * Copy the template contents into the result tree.
   * The content of the xsl:template element is the template
   * that is instantiated when the template rule is applied.
   *
   * @param transformer non-null reference to the the current transform-time state.
   *
   * @throws TransformerException
   */
  public void execute(TransformerImpl transformer) throws TransformerException {
    
	  XPathContext xctxt = transformer.getXPathContext();

	  SourceLocator srcLocator = xctxt.getSAXLocator();

	  transformer.getStackGuard().checkForInfiniteLoop();

	  xctxt.pushRTFContext();

	  if (transformer.getDebug()) {
		  transformer.getTraceManager().emitTraceEvent(this);
	  }

	  if (m_asAttr == null) {
		  transformer.executeChildTemplates(this, true);
	  }
	  else {         
		  try {                      
			  XObject xslTemplateEvalResult = getXslTemplateResult(transformer, xctxt);
			  
			  if (xslTemplateEvalResult instanceof XPathInlineFunction) {
				  XPath seqTypeXPath = new XPath(m_asAttr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null, true);
				  XObject seqTypeExpressionEvalResult = seqTypeXPath.execute(xctxt, xctxt.getContextNode(), xctxt.getNamespaceContext());
				  SequenceTypeData seqExpectedTypeData = (SequenceTypeData)seqTypeExpressionEvalResult;
				  if (seqExpectedTypeData.getSequenceTypeFunctionTest() != null) {            	  
					  return;
				  }
				  else {
					  String errTemplateStr = (m_name != null) ? m_name.toString() : m_matchPattern.getPatternString();
					  throw new TransformerException("XTTE0505 : The required result type of template " + errTemplateStr 
																								  + " is " + m_asAttr + ". But the template result "
																								  + "doesn't conform to this required type.", srcLocator); 
				  }
			  }
			  else {
				  xslTemplateEvalResult = SequenceTypeSupport.castXdmValueToAnotherType(xslTemplateEvalResult, m_asAttr, null, xctxt);
				  
				  if (xslTemplateEvalResult != null) {
					  SerializationHandler handler = transformer.getSerializationHandler();

					  try {
						  if (xslTemplateEvalResult instanceof XMLNodeCursorImpl) {
							  ElemCopyOf.copyOfActionOnNodeSet((XMLNodeCursorImpl)xslTemplateEvalResult, transformer, handler, xctxt);
						  }
						  else {
							  ResultSequence rSeq = new ResultSequence();
							  if (xslTemplateEvalResult instanceof ResultSequence) {
								 rSeq = (ResultSequence)xslTemplateEvalResult;   
							  }
							  else {
								  rSeq.add(xslTemplateEvalResult); 
							  }
							  
							  ElemCopyOf.copyOfActionOnResultSequence(rSeq, transformer, handler, xctxt, false); 
						  }
					  } 
					  catch (TransformerException ex) {
						  throw new TransformerException(ex.getMessage(), srcLocator); 
					  } 
					  catch (SAXException ex) {
						  transformer.getErrorListener().fatalError(new TransformerException(ex)); 
					  }        		           		     
				  }
				  else {        
					  String errTemplateStr = (m_name != null) ? m_name.toString() : m_matchPattern.getPatternString();
					  throw new TransformerException("XTTE0505 : The required result type of template " + errTemplateStr 
																								        + " is " + m_asAttr + ". But the template result "
																								        + "doesn't conform to this required type.", srcLocator);  
				  }
			  }
		  }
		  catch (TransformerException ex) {
			  String errMesg = ex.getMessage();			  
			  if ((errMesg != null) && (errMesg.startsWith("XTTE0505") || errMesg.startsWith("XTTE0590") || 
					                                                      errMesg.startsWith("XTDE0700"))) {
				  throw ex;   
			  }
			  else {
				  String errTemplateStr = (m_name != null) ? m_name.toString() : m_matchPattern.getPatternString(); 
				  throw new TransformerException("XTTE0505 : The required result type of template " + errTemplateStr 
																								  + " is " + m_asAttr + ". But the template result "
																								  + "doesn't conform to this required type.", srcLocator);
			  }
		  }
	  }        

	  if (transformer.getDebug()) {
		  transformer.getTraceManager().emitTraceEndEvent(this);
	  }

	  xctxt.popRTFContext();
    
  }
  
  /**
   * Method definition to return, the result of xsl:template element 
   * processing for an xsl:template element that has "as" attribute.
   * 
   * @param transformer					An XSL TransformerImpl object instance
   * @param xctxt						XPath evaluation context
   * @return 							An XObject instance representing the result of xsl:template processing.
   * @throws TransformerException
   */
  private XObject getXslTemplateResult(TransformerImpl transformer, XPathContext xctxt) throws TransformerException {			  
	  
	  XObject result = null;
	  
	  Object xslFunctionResult = transformer.transformToGlobalRTFXslFunctionOrTemplate(this);
	  
	  Integer nodeDtmHandle = null;
	  
	  try {
		 nodeDtmHandle = Integer.valueOf(xslFunctionResult.toString());
	     NodeList nodeList = (new XRTreeFrag(nodeDtmHandle.intValue(), xctxt, this)).convertToNodeset();
	     result = new XNodeSetForDOM(nodeList, xctxt);
	  }
	  catch (NumberFormatException ex) {
		 result = (XPathInlineFunction)xslFunctionResult;
	  }	  

	  return result;
  }

  /**
   * This function is called during recomposition to
   * control how this element is composed.
   * @param root The root stylesheet for this transformation.
   */
  public void recompose(StylesheetRoot root)
  {
	  root.recomposeTemplates(this);
  }

}
