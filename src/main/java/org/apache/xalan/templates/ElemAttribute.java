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
/*
 * $Id$
 */
package org.apache.xalan.templates;

import java.util.Vector;

import javax.xml.XMLConstants;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xalan.xslt.util.XslTransformSharedDatastore;
import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.XSSimpleType;
import org.apache.xerces.impl.dv.xs.XSSimpleTypeDecl;
import org.apache.xerces.xs.XSAttributeDeclaration;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xml.serializer.NamespaceMappings;
import org.apache.xml.serializer.SerializationHandler;
import org.apache.xml.utils.QName;
import org.apache.xml.utils.XML11Char;
import org.apache.xpath.Expression;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;
import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;

/**
 * Implementation of XSL stylesheet instruction xsl:attribute.
 * 
 * @see <a href="https://www.w3.org/TR/xslt-30/#creating-attributes">xsl:attribute in XSLT 3.0 Specification</a>
 * 
 * @xsl.usage advanced
 */
public class ElemAttribute extends ElemElement
{
    static final long serialVersionUID = 8817220961566919187L;
    
    /**
     * The "select" expression.
     */
    protected Expression m_selectExpression = null;
    
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
   * Get an int constant identifying the type of element.
   * @see org.apache.xalan.templates.Constants
   *
   * @return The token ID for this element
   */
  public int getXSLToken()
  {
    return Constants.ELEMNAME_ATTRIBUTE;
  }

  /**
   * Return the node name.
   *
   * @return The element name 
   */
  public String getNodeName()
  {
    return Constants.ELEMNAME_ATTRIBUTE_STRING;
  }
  
  /**
   * Set the "select" attribute.
   *
   * @param xpath The XPath expression for the "select" attribute.
   */
  public void setSelect(XPath xpath)
  {
      m_selectExpression = xpath.getExpression();  
  }

  /**
   * Get the "select" attribute.
   *
   * @return The XPath expression for the "select" attribute.
   */
  public Expression getSelect()
  {
      return m_selectExpression;
  }
  
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
   * Resolve the namespace into a prefix.  At this level, if no prefix exists, 
   * then return a manufactured prefix.
   *
   * @param rhandler The current result tree handler.
   * @param prefix The probable prefix if already known.
   * @param nodeNamespace  The namespace, which should not be null.
   *
   * @return The prefix to be used.
   */
  protected String resolvePrefix(SerializationHandler rhandler,
                                 String prefix, String nodeNamespace)
    throws TransformerException
  {

    if (null != prefix && (prefix.length() == 0 || prefix.equals("xmlns")))
    {
      // Since we can't use default namespace, in this case we try and 
      // see if a prefix has already been defined or this namespace.
      prefix = rhandler.getPrefix(nodeNamespace);

      // System.out.println("nsPrefix: "+nsPrefix);           
      if (null == prefix || prefix.length() == 0 || prefix.equals("xmlns"))
      {
        if(nodeNamespace.length() > 0)
        {
            NamespaceMappings prefixMapping = rhandler.getNamespaceMappings();
            prefix = prefixMapping.generateNextPrefix();
        }
        else
          prefix = "";
      }
    }
    return prefix;
  }
  
  /**
   * Validate that the node name is good.
   * 
   * @param nodeName Name of the node being constructed, which may be null.
   * 
   * @return true if the node name is valid, false otherwise.
   */
   protected boolean validateNodeName(String nodeName)
   {
      if(null == nodeName)
        return false;
      if(nodeName.equals("xmlns"))
        return false;
      return XML11Char.isXML11ValidQName(nodeName);
   }
  
  /**
   * Construct an xsl:attribute node within the XSLT transformation's result tree.
   *
   * @param nodeName              The name of the node, which may be null.
   * @param prefix                The prefix for the namespace, which may be null.
   * @param nodeNamespace         The namespace of the node, which may be null.
   * @param transformer           non-null reference to the current transform-time state.
   *
   * @throws TransformerException
   */
  void constructNode(String nodeName, String prefix, String nodeNamespace, 
                                                                 TransformerImpl transformer)
                                                                            throws TransformerException {
    
        XPathContext xctxt = transformer.getXPathContext();
        
        SourceLocator srcLocator = xctxt.getSAXLocator();
    
        if (null != nodeName && nodeName.length() > 0) {
              SerializationHandler rhandler = transformer.getSerializationHandler();
              
              // XSLT 3.0 spec changes wrt XSLT 1.0 : The string value of the new attribute node may be
              // defined either by using the select attribute, or by the sequence constructor that forms 
              // the content of the xsl:attribute element. These are mutually exclusive. i.e, if the
              // select attribute is present then the sequence constructor must be empty, and if the 
              // sequence constructor is non-empty then the select attribute must be absent.              
              if (m_selectExpression != null && this.m_firstChild != null) {
                  throw new TransformerException("XTSE0840 : An xsl:attribute element with a select attribute must "
                                                                  + "have an empty child sequence constructor.", srcLocator);   
              }
        
              String attrVal = null;
                            
              if (m_selectExpression != null) {
                  // Evaluate an xsl:attribute 'select' attribute's XPath expression,
            	  // to determine the value of an attribute for emitting to XSLT
            	  // transformation's output.
            	  
                  if (m_vars != null) {
                     m_selectExpression.fixupVariables(m_vars, m_globals_size);   
                  }
                  
                  XObject xpathEvalResult = m_selectExpression.execute(xctxt);
                  attrVal = XslTransformEvaluationHelper.getStrVal(xpathEvalResult);
              }
              else {
                  // Evaluate an xsl:attribute's child sequence constructor, to determine 
            	  // the value of an attribute for emitting to XSLT transformation's 
            	  // output.          
                  attrVal = transformer.transformToString(this);
              }
              
              QName type = getType();
              String validation = getValidation();
              
              if ((type != null) && (validation != null)) {
            	  throw new TransformerException("XTTE1540 : An xsl:attribute instruction cannot have both the attributes "
            	  																						+ "'type' and 'validation'.", srcLocator); 
              }
              
              if (type != null) {
            	  // An xsl:attribute instruction has an attribute "type".
            	  
            	  // An attribute node constructed needs to be validated
            	  // by this type.
            	  
            	  if ((XMLConstants.W3C_XML_SCHEMA_NS_URI).equals(type.getNamespace())) {
            		 // An attribute value needs to be validated with an XML Schema built-in type.    			 
          			 validateXslElementAttributeResultWithBuiltInSchemaType(attrVal, type, xctxt, ATTRIBUTE); 
            	  }
            	  else {
            		 XSModel xsModel = (XslTransformSharedDatastore.stylesheetRoot).getXsModel();
         			 
         			 if (xsModel != null) {
         				// An XML input document has been validated with a schema.
         				 
         				// An attribute value needs to be validated with an XML Schema 
         				// user-defined type.
         				 
         				XSTypeDefinition typeDefn = xsModel.getTypeDefinition(type.getLocalName(), type.getNamespace());
         				if ((typeDefn != null) && (typeDefn instanceof XSSimpleType)) {
         					XSSimpleTypeDecl xsSimpleTypeDecl = (XSSimpleTypeDecl)typeDefn;
         					try {
								xsSimpleTypeDecl.validate(attrVal, null, null);
							} 
         					catch (InvalidDatatypeValueException ex) {
								throw new TransformerException("XTTE1540 : An attribute value produced by the stylesheet, is not "
										                                              + "valid with the schema type " + type.getLocalName() + ".", srcLocator);								
							}
         				}
         				else {
         				    throw new TransformerException("XTTE1540 : Validation was requested of an XML attribute produced by instruction "
                                                                              + "xsl:attribute, but the schema available doesn't have a simple type "
                                                                              + "definition referred by xsl:attribute's 'type' attribute.", srcLocator);
         				}
         			 }
         			 else {
         				 throw new TransformerException("XTTE1540 : Validation was requested of an XML attribute produced by instruction "
     		    				 		                                            + "xsl:attribute, but XML input document was not validated with a "
     		    				 		                                            + "schema.", srcLocator);
         			 } 
            	  }
              }
              else if (validation != null) {
             	 // An xsl:attribute instruction has an attribute "validation".
            	  
             	 // An attribute node constructed needs to be validated
             	 // by an attribute declaration available in the schema.
             	  
             	 if (!isValidationStrOk(validation)) {
             		 throw new TransformerException("XTTE1540 : An xsl:attribute instruction's attribute 'validation' can only have one of following "
             		 		                                                                        + "values : strict, lax, preserve, strip.", srcLocator);
             	 }
             	 else if ((Constants.XS_VALIDATION_STRICT_STRING).equals(validation)) {
             		 XSModel xsModel = (XslTransformSharedDatastore.stylesheetRoot).getXsModel();
             		 if (xsModel != null) {
             			 String localName = QName.getLocalPart(nodeName);
             			 XSAttributeDeclaration attrDecl = xsModel.getAttributeDeclaration(localName, nodeNamespace);
             			 
             			 if (attrDecl != null) {    				 
             				 XSSimpleTypeDecl xsSimpleTypeDecl = (XSSimpleTypeDecl)(attrDecl.getTypeDefinition());
             				 try {
								xsSimpleTypeDecl.validate(attrVal, null, null);
							 } 
         					 catch (InvalidDatatypeValueException ex) {
								throw new TransformerException("XTTE1540 : An attribute value produced by the stylesheet, is not "
										                                                             + "valid with attribute's declaration available "
										                                                             + "in the schema. " + ex.getMessage(), srcLocator);								
							 }
             			 }
             			 else {
             				 throw new TransformerException("XTTE1540 : An xsl:attribute instruction's attribute \"validation\" has value 'strict', but "
																             						 + "the schema available doesn't have a corresponding attribute "
																             						 + "declaration.", srcLocator); 
             			 }
             		 }
             		 else {
             			 throw new TransformerException("XTTE1540 : An xsl:attribute instruction's attribute \"validation\" has value 'strict', but "
																	             					 + "an XML input document has not been validated with "
																	             					 + "a schema.", srcLocator);
             		 }
           	     }
             	 else if ((Constants.XS_VALIDATION_LAX_STRING).equals(validation)) {
             		 // An attribute "validation"'s value 'lax' has the same effect as the value strict, except that 
             		 // whereas strict validation fails if there is no available top-level attribute declaration in 
             		 // the schema.

             		 XSModel xsModel = (XslTransformSharedDatastore.stylesheetRoot).getXsModel();
             		 if (xsModel != null) {
             			 String localName = QName.getLocalPart(nodeName);             			 
            			 XSAttributeDeclaration attrDecl = xsModel.getAttributeDeclaration(localName, nodeNamespace);
            			 
             			 if (attrDecl != null) {    				 
             				 XSSimpleTypeDecl xsSimpleTypeDecl = (XSSimpleTypeDecl)(attrDecl.getTypeDefinition());
             				 try {
             					 xsSimpleTypeDecl.validate(attrVal, null, null);
             				 } 
             				 catch (InvalidDatatypeValueException ex) {
             					 throw new TransformerException("XTTE1540 : An attribute value produced by the stylesheet, is not "
															             							 + "valid with attribute's declaration available "
															             							 + "in the schema. " + ex.getMessage(), srcLocator);								
             				 }
             			 }    			     			  
             		 }
           	     }
             	 
             	 // The validation value 'strip' requires no validation.
            	 
            	 // The validation value 'preserve' is currently not implemented.
              }
              
              try {
                    // Let an XSL result tree handler add the attribute and its 
            	    // string value.
                    String localName = QName.getLocalPart(nodeName);
                    if (prefix != null && prefix.length() > 0) {
                        rhandler.addAttribute(nodeNamespace, localName, nodeName, "CDATA", attrVal, true);
                    }
                    else {
                        rhandler.addAttribute("", localName, nodeName, "CDATA", attrVal, true);
                    }
              }
              catch (SAXException ex) {
            	  throw new TransformerException("XTSE0840 : An error occured while processing XSL instruction xsl:attribute.", srcLocator);
              }
        }
    
  }


  /**
   * Add a child to the child list.
   * <!ELEMENT xsl:attribute %char-template;>
   * <!ATTLIST xsl:attribute
   *   name %avt; #REQUIRED
   *   namespace %avt; #IMPLIED
   *   %space-att;
   * >
   *
   * @param newChild Child to append to the list of this node's children
   *
   * @return The node we just appended to the children list 
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
	 * @see ElemElement#setName(AVT)
	 */
	public void setName(AVT v) {
        if (v.isSimple())
        {
            if (v.getSimpleString().equals("xmlns"))
            {
                throw new IllegalArgumentException();
            }
        }
		super.setName(v);
	}
	
	public void fixupVariables() {
		if ((m_selectExpression != null) && (m_vars != null)) {
			m_selectExpression.fixupVariables(m_vars, m_globals_size);
		}
	}

}
