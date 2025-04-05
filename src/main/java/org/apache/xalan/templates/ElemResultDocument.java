/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the  "License");
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;

import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xml.dtm.DTMManager;
import org.apache.xml.utils.QName;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.FuncTransform;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XNodeSetForDOM;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathMap;
import org.apache.xpath.objects.XRTreeFrag;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import xml.xpath31.processor.types.XSString;

/**
 * Implementation of the XSLT 3.0 xsl:result-document instruction.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 *  
 * @xsl.usage advanced
 */
public class ElemResultDocument extends ElemTemplateElement 
                                                implements ExpressionOwner
{
  private static final long serialVersionUID = -4338242968870823693L;

  /**
   * The "href" avt value.
   */
  private AVT m_href = null;
  
  /**
   * The "method" attribute's value.
   */
  private QName m_method = null;
  
  /**
   * The "omit-xml-declaration" boolean value.
   */
  private boolean m_omitXmlDeclaration;
  
  /**
   * This URI string value may be set from fn:transform 
   * function call.
   */
  public static String m_BaseOutputUriStrAbsValue = null;
  
  /**
   * Class field representing reference to, function fn:transform's 
   * return value which is of type XDM map.
   */
  public static XPathMap m_fnTransformResult = null;
  
  /**
   * Class field representing reference to, function fn:transform's 
   * 'delivery-format' parameter specification.
   */
  public static String m_fnTransformDeliveryFormat = null;
  
  /**
   * The class constructor.
   */
  public ElemResultDocument() {}
  
  /**
   * Get the value of "href" attribute.
   *
   * @return 	avt value for the "href" attribute.
   */
  public AVT getHref()
  {
      return m_href;
  }

  /**
   * Set the value of "href" attribute.
   *
   * @param href	avt value for the "href" attribute.
   */
  public void setHref(AVT href)
  {
      this.m_href = href;  
  }

  /**
   * Get the value of "method" attribute.
   *
   * @return 	String value for the "method" attribute.
   */
  public QName getMethod() {
	  return m_method;
  }

  /**
   * Set the value of "method" attribute.
   *
   * @param method	String value for the "method" attribute.
   */
  public void setMethod(QName method) {
	  this.m_method = method;
  }

  /**
   * Get the value of "omit-xml-declaration" attribute.
   *
   * @return 	boolean value for the "omit-xml-declaration" attribute.
   */
  public boolean getOmitXmlDeclaration() {
	  return m_omitXmlDeclaration;
  }

  /**
   * Set the value of "omit-xml-declaration" attribute.
   *
   * @param method	boolean value for the "omit-xml-declaration" attribute.
   */
  public void setOmitXmlDeclaration(boolean omitXmlDeclaration) {
	  this.m_omitXmlDeclaration = omitXmlDeclaration;
  }

  /**
   * This function is called after everything else has been
   * recomposed, and allows the template to set remaining
   * values that may be based on some other property that
   * depends on recomposition.
   *
   * @param sroot
   *
   * @throws TransformerException
   */
  public void compose(StylesheetRoot sroot) throws TransformerException
  {
	  super.compose(sroot);
  }
  
  /**
   * This function is called after the template's children have been composed.
   */
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
      return Constants.ELEMNAME_RESULTDOCUMENT;
  }

  /**
   * Return the node name.
   *
   * @return The element's name
   */
  public String getNodeName()
  {
      return Constants.ELEMNAME_RESULTDOCUMENT_STRING;
  }

  /**
   * Execute the xsl:result-document transformation.
   *
   * @param transformer non-null reference to the the current transform-time state.
   *
   * @throws TransformerException
   */
  public void execute(TransformerImpl transformer) throws TransformerException
  {
	    
	    XPathContext xctxt = transformer.getXPathContext();
	    
	    SourceLocator srcLocator = xctxt.getSAXLocator(); 
	  
	    try {
	    	transformer.pushCurrentTemplateRuleIsNull(true);

	    	if (transformer.getDebug()) {
	    		transformer.getTraceManager().emitTraceEvent(this);
	    	}
	    	
	    	String hrefStrVal = m_href.evaluate(xctxt, xctxt.getContextNode(), this);		// Mandatory
	    	
	    	URL resolvedHrefUrl = null;

			try {
				URI url = new URI(hrefStrVal);
				String stylesheetSystemId = srcLocator.getSystemId();      // base uri of stylesheet, if available

				if (!url.isAbsolute()) {
					if (m_BaseOutputUriStrAbsValue != null) {
						URI resolvedUriArg = (new URI(m_BaseOutputUriStrAbsValue)).resolve(hrefStrVal);
						resolvedHrefUrl = resolvedUriArg.toURL();
					}				
					else if (stylesheetSystemId != null) {
						URI resolvedUriArg = (new URI(stylesheetSystemId)).resolve(hrefStrVal);
						resolvedHrefUrl = resolvedUriArg.toURL(); 
					}
				}

				if (resolvedHrefUrl == null) {
					resolvedHrefUrl = new URL(hrefStrVal);   
				}
			}
			catch (URISyntaxException ex) {
				throw new javax.xml.transform.TransformerException("FODC0005 : An xsl:result-document instruction's href uri '" + hrefStrVal + "' "
																					+ "is not a valid absolute uri, "
																					+ "or cannot be resolved to an absolute uri.", srcLocator);  
			}
			catch (MalformedURLException ex) {
				throw new javax.xml.transform.TransformerException("FODC0005 : An xsl:result-document instruction's href uri '" + hrefStrVal + "' "
																					+ "is not a valid absolute uri, or cannot be resolved to "
																					+ "an absolute uri.", srcLocator);
			}
	    	
	    	QName methodQname = m_method;												// Optional, with default value "xml"
	    	
	    	boolean omitXmlDeclarationAttr = m_omitXmlDeclaration;   					// Optional, with default value "false"
	    	
	    	String resolvedHrefStrVal = resolvedHrefUrl.toString();
	    	
	    	if ((methodQname == null) || ((Constants.ATTRVAL_OUTPUT_METHOD_XML).equals(methodQname.toString()))) {
	    		int xdmNodeHandle = transformer.transformToRTF(this);
	    		
	    		NodeList nodeList = (new XRTreeFrag(xdmNodeHandle, xctxt, this)).convertToNodeset();		
	    		XNodeSetForDOM xNodeSetForDOM = new XNodeSetForDOM(nodeList, xctxt); 
	    		XMLNodeCursorImpl xNodeSet = (XMLNodeCursorImpl)xNodeSetForDOM;
	    		DTMCursorIterator dtmIter = xNodeSet.iterRaw();
	    		int nodeHandle = dtmIter.nextNode();
	    		DTM dtm1 = xctxt.getDTM(nodeHandle);
	    		Node node = dtm1.getNode(nodeHandle);
	    		String xmlStrValue = XslTransformEvaluationHelper.serializeXmlDomElementNode(node);
	    		if (omitXmlDeclarationAttr) {
	    			int xmlPrologEndStrIndex = xmlStrValue.indexOf("?>");
	    			xmlStrValue = xmlStrValue.substring(xmlPrologEndStrIndex + 2); 
	    		}
	    		
	    		if (m_fnTransformResult != null) {
	    			if (m_BaseOutputUriStrAbsValue != null) {
	    				int lastIndexOfSlsh = m_BaseOutputUriStrAbsValue.lastIndexOf('/');
	    				String hrefUriPrefix = m_BaseOutputUriStrAbsValue.substring(0, lastIndexOfSlsh + 1);
	    				URI url = new URI(hrefStrVal);
	    				String effectiveAbsUri = null;
	    				if (!url.isAbsolute()) {
	    					effectiveAbsUri = hrefUriPrefix + url.toString(); 
	    				}
	    				else {
	    					effectiveAbsUri = url.toString(); 
	    				}
	    				
	    				m_fnTransformResult.put(new XSString(effectiveAbsUri), getFnTransformResultComponent(xctxt, xmlStrValue));
	    			}
	    			else {
	    				m_fnTransformResult.put(new XSString(resolvedHrefStrVal), getFnTransformResultComponent(xctxt, xmlStrValue));	
	    			}
	    		}
	    		else {
	    			writeStringContentsToFile(resolvedHrefStrVal, xmlStrValue);
	    		}
	    	}
	    	else if ((Constants.ATTRVAL_OUTPUT_METHOD_TEXT).equals(methodQname.toString())) {
	    		String textStrValue = transformer.transformToString(this);
	    		
	    		if (m_fnTransformResult != null) {
	    			if (m_BaseOutputUriStrAbsValue != null) {
	    				m_fnTransformResult.put(new XSString(m_BaseOutputUriStrAbsValue), getFnTransformResultComponent(xctxt, textStrValue));
	    			}
	    			else {
	    				m_fnTransformResult.put(new XSString(resolvedHrefStrVal), getFnTransformResultComponent(xctxt, textStrValue));	
	    			}
	    		}
	    		else {
	    			writeStringContentsToFile(resolvedHrefStrVal, textStrValue);
	    		}
	    	}
	    	else if ((Constants.ATTRVAL_OUTPUT_METHOD_JSON).equals(methodQname.toString())) {
	    		String jsonStrValue = transformer.transformToString(this);
	    		
	    		try {
	    		   /**
	    		    * Verify that, the string value represents a legible JSON document.
	    		    */
	    		   JSONObject jsonObject = new JSONObject(jsonStrValue);
	    		}
	    		catch (JSONException ex) {
	    		   throw new TransformerException("XPTY0004 : An xsl:result-document instruction within a stylesheet has attribute "
	    		   		                                										+ "\"method\" with value 'json'. There's a JSON syntax error within "
	    		   		                                										+ "an input string provided to the JSON parser, or there was a duplicate "
	    		   		                                										+ "JSON object key. Following run-time error occured : " + 
	    		   		                                										ex.getMessage() + ".", srcLocator);
	    		}
	    		
	    		if (m_fnTransformResult != null) {
	    			if (m_BaseOutputUriStrAbsValue != null) {
	    				m_fnTransformResult.put(new XSString(m_BaseOutputUriStrAbsValue), getFnTransformResultComponent(xctxt, jsonStrValue.trim()));
	    			}
	    			else {
	    				m_fnTransformResult.put(new XSString(resolvedHrefStrVal), getFnTransformResultComponent(xctxt, jsonStrValue.trim()));	
	    			}
	    		}
	    		else {
	    			writeStringContentsToFile(resolvedHrefStrVal, jsonStrValue.trim());
	    		}
	    	}
	    	else if ((Constants.ATTRVAL_OUTPUT_METHOD_HTML).equals(methodQname.toString())) {	    		
	    		ElemTemplateElement t = this.m_firstChild;
	    		if (!(t instanceof ElemLiteralResult)) {
	    		   throw new TransformerException("XPTY0004 : An xsl:result-document instruction's serialization method is 'html', but "
	    		   		                                           + "xsl:result-document's immmediate child content doesn't starts "
	    		   		                                           + "constructing a legible HTML document.", srcLocator);	
	    		}
	    		else {
	    		   StylesheetRoot stylesheetRoot = t.getStylesheetRoot();
	    		   
	    		   /**
	    		    * This object instance is a clone of related internal 
	    		    * value within stylesheetRoot object. Therefore, this object
	    		    * instance can be mutated with new information for this need.
	    		    */
	    		   Properties xslNewOutputProps = stylesheetRoot.getOutputProperties();	    		   
	    		   
	    		   xslNewOutputProps.setProperty(OutputKeys.METHOD, Constants.ATTRVAL_OUTPUT_METHOD_HTML);

	    		   stylesheetRoot.setOutputProps(xslNewOutputProps);

	    		   int xdmNodeHandle = transformer.transformToRTF(this);

	    		   NodeList nodeList = (new XRTreeFrag(xdmNodeHandle, xctxt, this)).convertToNodeset();		
	    		   XNodeSetForDOM xNodeSetForDOM = new XNodeSetForDOM(nodeList, xctxt); 
	    		   XMLNodeCursorImpl xNodeSet = (XMLNodeCursorImpl)xNodeSetForDOM;
	    		   DTMCursorIterator dtmIter = xNodeSet.iterRaw();
	    		   int nodeHandle = dtmIter.nextNode();
	    		   DTM dtm1 = xctxt.getDTM(nodeHandle);
	    		   Node node = dtm1.getNode(nodeHandle);

	    		   String htmlStrValue = XslTransformEvaluationHelper.serializeXmlDomElementNode(node);
	    		   int xmlPrologSuffixIdx = htmlStrValue.indexOf("?>");
	    		   htmlStrValue = htmlStrValue.substring(xmlPrologSuffixIdx + 2);
	    		   
	    		   if (m_fnTransformResult != null) {
	    			   if (m_BaseOutputUriStrAbsValue != null) {
	    				   int lastIndexOfSlsh = m_BaseOutputUriStrAbsValue.lastIndexOf('/');
	    				   String hrefUriPrefix = m_BaseOutputUriStrAbsValue.substring(0, lastIndexOfSlsh + 1);
	    				   URI url = new URI(hrefStrVal);
	    				   String effectiveAbsUri = null;
	    				   if (!url.isAbsolute()) {
	    					   effectiveAbsUri = hrefUriPrefix + url.toString(); 
	    				   }
	    				   else {
	    					   effectiveAbsUri = url.toString(); 
	    				   }

	    				   m_fnTransformResult.put(new XSString(effectiveAbsUri), getFnTransformResultComponent(xctxt, htmlStrValue));
	    			   }
	    			   else {
	    				   m_fnTransformResult.put(new XSString(resolvedHrefStrVal), getFnTransformResultComponent(xctxt, htmlStrValue));	
	    			   }
	    		   }
	    		   else {
	    			   writeStringContentsToFile(resolvedHrefStrVal, htmlStrValue);
	    		   }

	    		   /**
	    		    * This is an original xsl:output information that has to be restored
	    		    * on stylesheetRoot object for any further XSL transformation work, after
	    		    * this xsl:result-document instruction instance has completed its processing.
	    		    */
	    		   Properties prevOutputProperties = stylesheetRoot.getDefaultOutputProps();

	    		   stylesheetRoot.setOutputProps(prevOutputProperties);	    		   
	    		}	    		
	    	}
        }
	    catch (TransformerException ex) {
	        throw ex;	
	    }	    
	    catch (Exception ex) {
	    	throw new TransformerException("XPTY0004 : An exception occured while evaluating an "
	    			                                    								+ "xsl:result-document instruction. Following "
	    			                                    								+ "run-time error occured : " + ex.getMessage() + ".", srcLocator);
	    }
        finally {
            if (transformer.getDebug()) {
    	       transformer.getTraceManager().emitTraceEndEvent(this);
            }
            
            transformer.popCurrentTemplateRuleIsNull();
        }
  }

  /**
   * Add an XSL stylesheet child information.
   *
   * @param newChild Child to add to child list
   *
   * @return Child just added to child list
   */
  public ElemTemplateElement appendChild(ElemTemplateElement newChild)
  {
      return super.appendChild(newChild);
  }
  
  /**
   * Call the children visitors.
   * 
   * @param visitor The visitor whose appropriate method will be called.
   */
  public void callChildVisitors(XSLTVisitor visitor, boolean callAttributes)
  {    
      super.callChildVisitors(visitor, callAttributes);
  }

  @Override
  public Expression getExpression() {
	  return null;
  }

  @Override
  public void setExpression(Expression exp) {
	  // NO OP
  }
  
  /**
   * This method creates a file specified by its name within local file system 
   * store, and writes a string value to the file.
   * 
   * @param hrefStr						local url of the file, with "file:/" scheme.
   *                                    this url may point to any location within 
   *                                    local file system, with write permissions to this JVM 
   *                                    process.  
   * @param strValue					string value that needs to be written to the file
   * @throws FileNotFoundException
   * @throws IOException
   */
  private void writeStringContentsToFile(String hrefStr, String strValue) 
		  													      throws FileNotFoundException, IOException {
	  int localFileUrlPrefixLength = "file:/".length();
	  hrefStr = hrefStr.substring(localFileUrlPrefixLength - 1);
	  
	  FileOutputStream fos = new FileOutputStream(new File(hrefStr));
	  byte[] byteArr = strValue.getBytes();
	  fos.write(byteArr);
	  fos.flush();
	  fos.close();
  }
  
  /**
   * This method definition gets details for the, result of xsl:result-document 
   * instruction.
   */
  private XObject getFnTransformResultComponent(XPathContext xctxt, String resultStrValue) throws Exception {	  
	  
	  XObject result = null;
	  
	  if ((m_fnTransformDeliveryFormat == null) || (FuncTransform.DOCUMENT).equals(m_fnTransformDeliveryFormat)) {
		  System.setProperty(Constants.XML_DOCUMENT_BUILDER_FACTORY_KEY, Constants.XML_DOCUMENT_BUILDER_FACTORY_VALUE);
	      System.setProperty(FuncTransform.XSLT_TRANSFORMER_FACTORY_KEY, FuncTransform.XSLT_TRANSFORMER_FACTORY_VALUE);
		  
	      DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		  DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		  StringReader strReader = new StringReader(resultStrValue);
		  Document document = docBuilder.parse(new InputSource(strReader));
		  
		  DTMManager dtmManager = xctxt.getDTMManager();
		  DTM resultDtm = dtmManager.getDTM(new DOMSource(document), true, null, false, false);
		  int resultDtmHandle = resultDtm.getDocument();			    	
		  
		  XMLNodeCursorImpl resultNode = new XMLNodeCursorImpl(resultDtmHandle, dtmManager);		  
		  resultNode.setDtm(resultDtm);
		  
		  result = resultNode;
	  }
	  else if ((FuncTransform.SERIALIZED).equals(m_fnTransformDeliveryFormat)) {
		  result = new XSString(resultStrValue);
	  }
      else if ((FuncTransform.RAW).equals(m_fnTransformDeliveryFormat)) {
		  // TO DO  
	  }
	  
	  return result;
  }

}
