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
import org.apache.xalan.xslt.util.XslTransformData;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XObject;
import org.w3c.dom.Node;

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
   * Send a message to diagnostics.
   * The xsl:message instruction sends a message in a way that
   * is dependent on the XSLT transformer. The content of the xsl:message
   * instruction is a template. The xsl:message is instantiated by
   * instantiating the content to create an XML fragment. This XML
   * fragment is the content of the message.
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
	  
	  if (m_selectExpression != null) {
		  XObject xObj = m_selectExpression.execute(xctxt, contextNode, xctxt.getNamespaceContext());		  
		  if (xObj instanceof XMLNodeCursorImpl) {
			 XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)xObj;
			 int nodeHandle = xmlNodeCursorImpl.asNode(xctxt);
			 DTM dtm = xctxt.getDTM(nodeHandle); 
			 if (dtm.getNodeType(nodeHandle) == DTM.ATTRIBUTE_NODE) {
				strBuff.append(dtm.getNodeValue(nodeHandle));
			 }
			 else {
				 Node node = dtm.getNode(nodeHandle);
				 try {
					String xmlNodeStr = XslTransformEvaluationHelper.serializeXmlDomElementNode(node);
					strBuff.append(xmlNodeStr);
				 } 
				 catch (Exception ex) {
				    throw new TransformerException(ex.getMessage());
				 } 
			 }
		  }
		  else if (xObj instanceof ResultSequence) {
			 // TO DO 
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
				strBuff.append(xmlNodeStr);
			 } 
			 catch (Exception ex) {
			    throw new TransformerException(ex.getMessage());
			 }
			 
			 childNode = dtm.getNextSibling(childNode); 
		  }		  
	  }
	  
	  if (strBuff.length() > 0) {
		  XslTransformData.m_xsl_message_rSeq.add(new XSString(strBuff.toString())); 
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
			 String errCodeStr = "XTMM9000";
			 if (m_errorCode != null) {
				errCodeStr = m_errorCode.evaluate(xctxt, contextNode, xctxt.getNamespaceContext()); 
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
					   int idx2 = xdmItemStr1.indexOf("?>");
					   xdmItemStr1 = xdmItemStr1.substring(idx2 + 2);
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
			 
			 throw new TransformerException(errCodeStr + " : An XSL stylesheet processing has been terminated by 'message' "
							 		                                                                      + "instruction, with following message trace : '" 
									                                                                      + strBuff2.toString() + "'.", srcLocator);
		 }
	  }

	  if (transformer.getDebug())
		  transformer.getTraceManager().emitTraceEndEvent(this); 
  }
}
