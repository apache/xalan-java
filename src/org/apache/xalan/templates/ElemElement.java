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
/*
 * $Id$
 */
package org.apache.xalan.templates;

import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xalan.xslt.util.XslTransformSharedDatastore;
import org.apache.xerces.impl.xs.XSElementDecl;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xml.dtm.DTM;
import org.apache.xml.serializer.SerializationHandler;
import org.apache.xml.utils.QName;
import org.apache.xml.utils.XML11Char;
import org.apache.xpath.Expression;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.composite.SequenceTypeSupport;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XRTreeFrag;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Implement xsl:element
 * <pre>
 * <!ELEMENT xsl:element %template;>
 * <!ATTLIST xsl:element
 *   name %avt; #REQUIRED
 *   namespace %avt; #IMPLIED
 *   use-attribute-sets %qnames; #IMPLIED
 *   %space-att;
 * >
 * </pre>
 * @see <a href="http://www.w3.org/TR/xslt#section-Creating-Elements-with-xsl:element">XXX in XSLT Specification</a>
 * @xsl.usage advanced
 */
public class ElemElement extends ElemUse
{
    static final long serialVersionUID = -324619535592435183L;

  /**
   * The name attribute is interpreted as an attribute value template.
   * It is an error if the string that results from instantiating the
   * attribute value template is not a QName.
   * @serial
   */
  protected AVT m_name_avt = null;

  /**
   * Set the "name" attribute.
   * The name attribute is interpreted as an attribute value template.
   * It is an error if the string that results from instantiating the
   * attribute value template is not a QName.
   *
   * @param v Name attribute to set for this element
   */
  public void setName(AVT v)
  {
    m_name_avt = v;
  }

  /**
   * Get the "name" attribute.
   * The name attribute is interpreted as an attribute value template.
   * It is an error if the string that results from instantiating the
   * attribute value template is not a QName.
   *
   * @return Name attribute for this element
   */
  public AVT getName()
  {
    return m_name_avt;
  }

  /**
   * If the namespace attribute is present, then it also is interpreted
   * as an attribute value template. The string that results from
   * instantiating the attribute value template should be a URI reference.
   * It is not an error if the string is not a syntactically legal URI reference.
   * @serial
   */
  protected AVT m_namespace_avt = null;

  /**
   * Set the "namespace" attribute.
   * If the namespace attribute is present, then it also is interpreted
   * as an attribute value template. The string that results from
   * instantiating the attribute value template should be a URI reference.
   * It is not an error if the string is not a syntactically legal URI reference.
   *
   * @param v NameSpace attribute to set for this element
   */
  public void setNamespace(AVT v)
  {
    m_namespace_avt = v;
  }

  /**
   * Get the "namespace" attribute.
   * If the namespace attribute is present, then it also is interpreted
   * as an attribute value template. The string that results from
   * instantiating the attribute value template should be a URI reference.
   * It is not an error if the string is not a syntactically legal URI reference.
   *
   * @return Namespace attribute for this element
   */
  public AVT getNamespace()
  {
    return m_namespace_avt;
  }
  
  /**
   * An optional attribute "type" may be present on xsl:element 
   * instruction. When this attribute is present, an element node
   * created by xsl:element instruction is validated with this type.
   */
  protected QName m_type = null;
  
  /**
   * Get the value of "type" attribute.
   */
  public QName getType() {
	  return m_type;
  }

  /**
   * Set the value of "type" attribute.
   */
  public void setType(QName type) {
	  this.m_type = type;
  }
  
  /**
   * An optional attribute "validation" may be present on xsl:element 
   * instruction. When this attribute is present, an element node
   * created by xsl:element instruction is validated with this type.
   */
  protected String m_validation = null;
  
  /**
   * Get the value of "validation" attribute.
   */
  public String getValidation() {
	  return m_validation;
  }

  /**
   * Set the value of "validation" attribute.
   */
  public void setValidation(String validation) {
	  this.m_validation = validation;
  }

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
    if(null != m_name_avt)
      m_name_avt.fixupVariables(vnames, cstate.getGlobalsSize());
    if(null != m_namespace_avt)
      m_namespace_avt.fixupVariables(vnames, cstate.getGlobalsSize());
  }


  /**
   * Get an int constant identifying the type of element.
   * @see org.apache.xalan.templates.Constants
   *
   * @return The token ID for this element
   */
  public int getXSLToken()
  {
    return Constants.ELEMNAME_ELEMENT;
  }

  /**
   * Return the node name.
   *
   * @return This element's name 
   */
  public String getNodeName()
  {
    return Constants.ELEMNAME_ELEMENT_STRING;
  }
   
  /**
   * Resolve the namespace into a prefix.  Meant to be
   * overidded by elemAttribute if this class is derived.
   *
   * @param rhandler The current result tree handler.
   * @param prefix The probable prefix if already known.
   * @param nodeNamespace  The namespace.
   *
   * @return The prefix to be used.
   */
  protected String resolvePrefix(SerializationHandler rhandler,
                                 String prefix, String nodeNamespace)
    throws TransformerException
  {

//    if (null != prefix && prefix.length() == 0)
//    {
//      String foundPrefix = rhandler.getPrefix(nodeNamespace);
//
//      // System.out.println("nsPrefix: "+nsPrefix);           
//      if (null == foundPrefix)
//        foundPrefix = "";
//    }
    return prefix;
  }
    
  /**
   * Create an element in the result tree.
   * The xsl:element element allows an element to be created with a
   * computed name. The expanded-name of the element to be created
   * is specified by a required name attribute and an optional namespace
   * attribute. The content of the xsl:element element is a template
   * for the attributes and children of the created element.
   *
   * @param transformer non-null reference to the the current transform-time state.
   *
   * @throws TransformerException
   */
  public void execute(
          TransformerImpl transformer)
            throws TransformerException
  {

       if (transformer.getDebug())
         transformer.getTraceManager().fireTraceEvent(this);

 	SerializationHandler rhandler = transformer.getSerializationHandler();
    XPathContext xctxt = transformer.getXPathContext();
    int sourceNode = xctxt.getCurrentNode();
    
    
    String nodeName = m_name_avt == null ? null : m_name_avt.evaluate(xctxt, sourceNode, this);

    String prefix = null;
    String nodeNamespace = "";

    // Only validate if an AVT was used.
    if ((nodeName != null) && (!m_name_avt.isSimple()) && (!XML11Char.isXML11ValidQName(nodeName)))
    {
      transformer.getMsgMgr().warn(
        this, XSLTErrorResources.WG_ILLEGAL_ATTRIBUTE_VALUE,
        new Object[]{ Constants.ATTRNAME_NAME, nodeName });

      nodeName = null;
    }

    else if (nodeName != null)
    {
      prefix = QName.getPrefixPart(nodeName);

      if (null != m_namespace_avt)
      {
        nodeNamespace = m_namespace_avt.evaluate(xctxt, sourceNode, this);
        if (null == nodeNamespace || 
            (prefix != null && prefix.length()>0 && nodeNamespace.length()== 0) )
          transformer.getMsgMgr().error(
              this, XSLTErrorResources.ER_NULL_URI_NAMESPACE);
        else
        {
        // Determine the actual prefix that we will use for this nodeNamespace

        prefix = resolvePrefix(rhandler, prefix, nodeNamespace);
        if (null == prefix)
          prefix = "";

        if (prefix.length() > 0)
          nodeName = (prefix + ":" + QName.getLocalPart(nodeName));
        else
          nodeName = QName.getLocalPart(nodeName);
        }
      }

      // No namespace attribute was supplied. Use the namespace declarations
      // currently in effect for the xsl:element element.
      else    
      {
        try
        {
          // Maybe temporary, until I get this worked out.  test: axes59
          nodeNamespace = getNamespaceForPrefix(prefix);

          // If we get back a null nodeNamespace, that means that this prefix could
          // not be found in the table.  This is okay only for a default namespace
          // that has never been declared.

          if ( (null == nodeNamespace) && (prefix.length() == 0) )
            nodeNamespace = "";
          else if (null == nodeNamespace)
          {
            transformer.getMsgMgr().warn(
              this, XSLTErrorResources.WG_COULD_NOT_RESOLVE_PREFIX,
              new Object[]{ prefix });

            nodeName = null;
          }

        }
        catch (Exception ex)
        {
          transformer.getMsgMgr().warn(
            this, XSLTErrorResources.WG_COULD_NOT_RESOLVE_PREFIX,
            new Object[]{ prefix });

          nodeName = null;
        }
      }
    }

    constructNode(nodeName, prefix, nodeNamespace, transformer);

    if (transformer.getDebug())
      transformer.getTraceManager().fireTraceEndEvent(this);
  }
  
  /**
   * Construct a node in the result tree.  This method is overloaded by 
   * xsl:attribute. At this class level, this method creates an element.
   * If the node is null, we instantiate only the content of the node in accordance
   * with section 7.1.2 of the XSLT 1.0 Recommendation.
   *
   * @param nodeName The name of the node, which may be <code>null</code>.  If <code>null</code>,
   *                 only the non-attribute children of this node will be processed.
   * @param prefix The prefix for the namespace, which may be <code>null</code>.
   *               If not <code>null</code>, this prefix will be mapped and unmapped.
   * @param nodeNamespace The namespace of the node, which may be not be <code>null</code>.
   * @param transformer non-null reference to the the current transform-time state.
   *
   * @throws TransformerException
   */
  void constructNode(
          String nodeName, String prefix, String nodeNamespace, TransformerImpl transformer)
            throws TransformerException
  {

    boolean shouldAddAttrs;

    try
    {
      SerializationHandler rhandler = transformer.getResultTreeHandler();

      if (null == nodeName)
      {
        shouldAddAttrs = false;
      }
      else
      {
        if (null != prefix)
        {
          rhandler.startPrefixMapping(prefix, nodeNamespace, true);
        }

        rhandler.startElement(nodeNamespace, QName.getLocalPart(nodeName),
                              nodeName);

        super.execute(transformer);

        shouldAddAttrs = true;
      }
      
      XPathContext xctxt = transformer.getXPathContext();
	  SourceLocator srcLocator = xctxt.getSAXLocator();
	  
      if ((m_type != null) && (m_validation != null)) {
    	  throw new TransformerException("XTTE1540 : An xsl:element instruction cannot have both the attributes 'type' and 'validation'.", srcLocator); 
      }

      if (m_type != null) {
    	  // An xsl:element instruction has an attribute "type".
    	  // An element node constructed needs to be validated
    	  // by this type.    	      	  
    	  
    	  int rootNodeHandleOfRtf = transformer.transformToRTF(this);    	      	  
    	  
    	  NodeList nodeList = (new XRTreeFrag(rootNodeHandleOfRtf, xctxt, this)).convertToNodeset();
    	  
    	  if (nodeList != null) {
    		 Node node = nodeList.item(0);    		     		 
    		 
    		 String xmlStr = null;    		 
    		 try {
    			 xmlStr = XslTransformEvaluationHelper.serializeXmlDomElementNode(node);
			 } catch (Exception ex) {
				 throw new TransformerException("XTTE1540 : An error occured while evaluating an XSL stylesheet "
				 		                                          							+ "xsl:element instruction.", srcLocator);
			 }
    		 
    		 if ((XMLConstants.W3C_XML_SCHEMA_NS_URI).equals(m_type.getNamespace())) {
    			// An element content needs to be validated with an XML Schema built-in type.    			 
    			validateXslElementResultWithBuiltInSchemaType(xmlStr, m_type, xctxt);
       	     }
    		 else {    			 
    			 XSModel xsModel = (XslTransformSharedDatastore.stylesheetRoot).getXsModel();
    			 
    			 if (xsModel != null) {
    				 // An XML input document has been validated with a schema.
    				 // An element content needs to be validated with an XML Schema 
    				 // user-defined type.
    				 validateXslElementResultWithUserDefinedSchemaType(nodeName, transformer, xctxt, xmlStr, xsModel);
    			 }
    			 else {
    				 throw new TransformerException("XTTE1540 : Validation was requested of an XML node produced by instruction "
		    				 		                                            + "xsl:element, but XML input document was not validated with a "
		    				 		                                            + "schema.", srcLocator);
    			 }
    	     }
    	  }
      }
      else if (m_validation != null) {
    	 // An xsl:element instruction has an attribute "validation".
    	 // An element node constructed needs to be validated
    	 // by an element declaration available in the schema.
    	  
    	 if (!isValidationStrOk(m_validation)) {
    		 throw new TransformerException("XTTE1540 : An xsl:element instruction's attribute 'validation' can only have one of following "
    		 		                                                                        + "values : strict, lax, preserve, strip.", srcLocator);
    	 }
    	 else if ("strict".equals(m_validation)) {
    		 XSModel xsModel = (XslTransformSharedDatastore.stylesheetRoot).getXsModel();
    		 if (xsModel != null) {    			 
    			 String nodeLocalName = QName.getLocalPart(nodeName);
    			 XSElementDeclaration elemDecl = xsModel.getElementDeclaration(nodeLocalName, nodeNamespace);
    			 if (elemDecl != null) {    				 
    				 validateXslElementResultWithSchemaElemDecl(nodeName, transformer, xctxt, elemDecl);    				     				 
    			 }
    			 else {
    				 throw new TransformerException("XTTE1540 : An xsl:element instruction's attribute \"validation\" has value 'strict', but "
    				 		                                                    + "the schema available doesn't have a corresponding element "
    				 		                                                    + "declaration.", srcLocator); 
    			 }
    		 }
    		 else {
    			 throw new TransformerException("XTTE1540 : An xsl:element instruction's attribute \"validation\" has value 'strict', but "
																	                          + "an XML input document has not been validated with "
																	                          + "a schema.", srcLocator);
    		 }
    	 }
         else if ("lax".equals(m_validation)) {
        	 // An attribute "validation"'s value 'lax' has the same effect as the value strict, except that 
			 // whereas strict validation fails if there is no available top-level element declaration in 
			 // the schema.
        	 
        	 XSModel xsModel = (XslTransformSharedDatastore.stylesheetRoot).getXsModel();
    		 if (xsModel != null) {
    			 String nodeLocalName = QName.getLocalPart(nodeName);
    			 XSElementDeclaration elemDecl = xsModel.getElementDeclaration(nodeLocalName, nodeNamespace);
    			 if (elemDecl != null) {
    				 validateXslElementResultWithSchemaElemDecl(nodeName, transformer, xctxt, elemDecl);
    			 }    			     			  
    		 }
    	 }
    	 
    	 // The validation value 'strip' requires no validation.
    	 
    	 // The validation value 'preserve' is presently not implemented.
      }

      // After any needed element node validation by xsl:element instruction's "type" or 
      // "validation" attribute, emit the element node to XSL 
      // transform's output.    	  
      transformer.executeChildTemplates(this, shouldAddAttrs);  

      // Now end the element if name was valid
      if (null != nodeName)
      {
        rhandler.endElement(nodeNamespace, QName.getLocalPart(nodeName),
                            nodeName);
        if (null != prefix)
        {
          rhandler.endPrefixMapping(prefix);
        }
      }
    }
    catch (SAXException se)
    {
      throw new TransformerException(se);
    }
  }

  /**
   * Call the children visitors.
   * @param visitor The visitor whose appropriate method will be called.
   */
  protected void callChildVisitors(XSLTVisitor visitor, boolean callAttrs)
  {
  	if(callAttrs)
  	{
  	  if(null != m_name_avt)
  		m_name_avt.callVisitors(visitor);
  		
  	  if(null != m_namespace_avt)
  		m_namespace_avt.callVisitors(visitor);
  	}
  		
    super.callChildVisitors(visitor, callAttrs);
  }
  
  /**
   * Validate XSL instruction xsl:element's evaluation result, with an XML Schema built-in type,
   * specified by xsl:element's 'type' attribute. 
   */
  private void validateXslElementResultWithBuiltInSchemaType(String xmlStr, QName typeQname, XPathContext xctxt) throws TransformerException {
		
	  SourceLocator srcLocator = xctxt.getSAXLocator();

	  xmlStr = xmlStr.replaceFirst("<\\?.*\\?>", "");
	  String dataTypeLocalName = typeQname.getLocalName();
	  String xpathConstructorFuncExprStr = "xs:" + dataTypeLocalName + "('" + xmlStr + "')";
	  ElemTemplateElement elemTemplateElement = (ElemTemplateElement)xctxt.getNamespaceContext();
	  List<XMLNSDecl> prefixTable = null;
	  if (elemTemplateElement != null) {
		  prefixTable = (List<XMLNSDecl>)elemTemplateElement.getPrefixTable();
	  }
	  String xmlSchemaNsPrefix = XslTransformEvaluationHelper.getXMLSchemaNsPref(prefixTable);
	  if (xmlSchemaNsPrefix != null) {
		  xpathConstructorFuncExprStr += " instance of " + xmlSchemaNsPrefix + ":" + dataTypeLocalName;
	  }
	  else {
		  xpathConstructorFuncExprStr += " instance of xs:" + dataTypeLocalName;	
	  }

	  XPath xpath = new XPath(xpathConstructorFuncExprStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);

	  XObject xObj = null;
	  boolean isInstanceOf;
	  try {
		  xObj = xpath.executeInstanceOf(xctxt, DTM.NULL, null);
		  isInstanceOf = ((xObj.bool() == true) ? true : false);
	  }
	  catch (TransformerException ex) {
		  isInstanceOf = false;
	  }

	  if (!isInstanceOf) {
		  throw new TransformerException("XTTE1540 : An element node constructed via XSL stylesheet's xsl:element "
																			  + "instruction, is not valid according to type xs:" + dataTypeLocalName + " "
																			  + "specified as value of xsl:element's attribute 'type'.", srcLocator); 
	  }
   }
  
  /**
   * Validate XSL instruction xsl:element's evaluation result, with an XML Schema user-defined type,
   * specified by xsl:element's 'type' attribute. 
   */
   private void validateXslElementResultWithUserDefinedSchemaType(String nodeName, TransformerImpl transformer,
																  XPathContext xctxt, String xmlStr, XSModel xsModel) throws TransformerException {
	   String tempStr = xmlStr.replaceFirst("<\\?.*\\?>", "");

	   xmlStr = "<" + nodeName + " ";

	   SourceLocator srcLocator = xctxt.getSAXLocator();

	   ElemTemplateElement childElem = getFirstChildElem();    		 
	   while (childElem != null && childElem instanceof ElemAttribute) {
		   ElemAttribute elemAttr = (ElemAttribute)childElem;    			 
		   AVT attrAvt = elemAttr.getName();
		   String attrName = attrAvt.evaluate(xctxt, xctxt.getContextNode(), xctxt.getNamespaceContext());    			 
		   Expression attrSelectExpr = elemAttr.getSelect();
		   String attrVal = null;
		   if (attrSelectExpr != null) {
			   elemAttr.fixupVariables();    				 
			   XObject xpathEvalResult = attrSelectExpr.execute(xctxt);
			   attrVal = XslTransformEvaluationHelper.getStrVal(xpathEvalResult);    				 
		   }
		   else {
			   attrVal = transformer.transformToString(elemAttr);
		   }
		   xmlStr = xmlStr + attrName+"=\"" + attrVal + "\" ";
		   childElem = childElem.getNextSiblingElem();
	   }

	   xmlStr = xmlStr.trim() + ">" + tempStr + "</" + nodeName + ">";
	   xmlStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + xmlStr;

	   String dataTypeLocalName = m_type.getLocalName();
	   String dataTypeNamespace = m_type.getNamespace();

	   XSTypeDefinition typeDefn = xsModel.getTypeDefinition(dataTypeLocalName, dataTypeNamespace);

	   if (typeDefn != null) {
		   boolean isXmlStrValid;
		   String errMesg = null;
		   try {
			   isXmlStrValid = SequenceTypeSupport.isXmlStrValid(xmlStr, null, typeDefn);
		   }
		   catch (Exception ex) {
			   isXmlStrValid = false;
			   errMesg = ex.getMessage();
		   }

		   if (!isXmlStrValid) {
			   throw new TransformerException("XTTE1540 : An element node constructed via XSL stylesheet's xsl:element "
																				   + "instruction, is not valid according to type " + dataTypeLocalName + " "
																				   + "specified as value of xsl:element's attribute 'type'. " + errMesg, srcLocator); 
		   }    					 
	   }
  }
  
  /**
   * When an XSL instruction xsl:element has "validation" attribute with values 'strict' or 'lax',
   * validate the result of xsl:element instruction evaluation with the corresponding element
   * declaration available in the schema. 
   */
  private void validateXslElementResultWithSchemaElemDecl(String nodeName, TransformerImpl transformer,
		                                                  XPathContext xctxt, 
		                                                  XSElementDeclaration elemDecl) throws TransformerException {
	  
	  int rootNodeHandleOfRtf = transformer.transformToRTF(this);
	  
	  SourceLocator srcLocator = xctxt.getSAXLocator(); 

	  NodeList nodeList = (new XRTreeFrag(rootNodeHandleOfRtf, xctxt, this)).convertToNodeset();

	  if (nodeList != null) {
		  Node node = nodeList.item(0);    		     		 

		  String xmlStr = null;    		 
		  try {
			  xmlStr = XslTransformEvaluationHelper.serializeXmlDomElementNode(node);    						 
		  } catch (Exception ex) {
			  throw new TransformerException("XTTE1540 : An error occured while evaluating an XSL stylesheet "
					  													+ "xsl:element instruction.", srcLocator);
		  }

		  String tempStr = xmlStr.replaceFirst("<\\?.*\\?>", "");

		  xmlStr = "<" + nodeName + " ";            		 

		  ElemTemplateElement childElem = getFirstChildElem();    		 
		  while (childElem != null && childElem instanceof ElemAttribute) {
			  ElemAttribute elemAttr = (ElemAttribute)childElem;    			 
			  AVT attrAvt = elemAttr.getName();
			  String attrName = attrAvt.evaluate(xctxt, xctxt.getContextNode(), xctxt.getNamespaceContext());    			 
			  Expression attrSelectExpr = elemAttr.getSelect();
			  String attrVal = null;
			  if (attrSelectExpr != null) {
				  elemAttr.fixupVariables();    				 
				  XObject xpathEvalResult = attrSelectExpr.execute(xctxt);
				  attrVal = XslTransformEvaluationHelper.getStrVal(xpathEvalResult);    				 
			  }
			  else {
				  attrVal = transformer.transformToString(elemAttr);
			  }
			  xmlStr = xmlStr + attrName+"=\"" + attrVal + "\" ";
			  childElem = childElem.getNextSiblingElem();
		  }

		  xmlStr = xmlStr.trim() + ">" + tempStr + "</" + nodeName + ">";
		  xmlStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + xmlStr;

		  boolean isXmlStrValid;
		  String errMesg = null;
		  try {
			  isXmlStrValid = SequenceTypeSupport.isXmlStrValid(xmlStr, (XSElementDecl)elemDecl, null);
		  }
		  catch (Exception ex) {
			  isXmlStrValid = false;
			  errMesg = ex.getMessage();
		  }

		  if (!isXmlStrValid) {
			  throw new TransformerException("XTTE1540 : An element node constructed via XSL stylesheet's xsl:element "
																				  + "instruction, is not valid according to the corresponding "
																				  + "element declaration available in the schema. " + errMesg, srcLocator); 
		  }
	  }
  }
  
  /**
   * Check for the allowed values of "validation" attribute.
   */
  private boolean isValidationStrOk(String validationStr) {
	 return ("strict".equals(validationStr) || "lax".equals(validationStr) || 
			 "preserve".equals(validationStr) || "strip".equals(validationStr));  
  }

}
