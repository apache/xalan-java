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
import java.util.Vector;

import javax.xml.XMLConstants;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xerces.impl.xs.XSElementDecl;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xml.dtm.DTM;
import org.apache.xml.utils.QName;
import org.apache.xpath.Expression;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.composite.SequenceTypeSupport;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XRTreeFrag;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Implement xsl:use.
 * This acts as a superclass for ElemCopy, ElemAttributeSet,
 * ElemElement, and ElemLiteralResult, on order to implement
 * shared behavior the use-attribute-sets attribute.
 * @see <a href="http://www.w3.org/TR/xslt#attribute-sets">attribute-sets in XSLT Specification</a>
 * @xsl.usage advanced
 */
public class ElemUse extends ElemTemplateElement
{
    static final long serialVersionUID = 5830057200289299736L;

  /**
   * The value of the "use-attribute-sets" attribute.
   * @serial
   */
  private QName m_attributeSetsNames[] = null;
  
  /**
   * Class field to store, an attribute xsl:type's value.
   */
  protected QName m_type = null;
  
  /**
   * Class field to store, an attribute xsl:validation's value.
   */
  protected String m_validation = null;
  
  /**
   * Set value of an attribute xsl:type's value.
   */
  public void setType(QName type)
  {
  	m_type = type;  
  }
  
  /**
   * Get value of an attribute xsl:type's value.
   */
  public QName getType() {
	return m_type;  
  }
  
  /**
   * Set value of an attribute xsl:validation's value.
   */
  public void setValidation(String validation)
  {
  	m_validation = validation;  
  }
  
  /**
   * Get value of an attribute xsl:validation's value.
   */
  public String getValidation() {
	return m_validation;  
  }

  /**
   * Set the "use-attribute-sets" attribute.
   * Attribute sets are used by specifying a use-attribute-sets
   * attribute on xsl:element, xsl:copy (see [7.5 Copying]) or
   * xsl:attribute-set elements. The value of the use-attribute-sets
   * attribute is a whitespace-separated list of names of attribute
   * sets. Each name is specified as a QName, which is expanded as
   * described in [2.4 Qualified Names].
   *
   * @param v The value to set for the "use-attribute-sets" attribute. 
   */
  public void setUseAttributeSets(Vector v)
  {

    int n = v.size();

    m_attributeSetsNames = new QName[n];

    for (int i = 0; i < n; i++)
    {
      m_attributeSetsNames[i] = (QName) v.elementAt(i);
    }
  }

  /**
   * Set the "use-attribute-sets" attribute.
   * Attribute sets are used by specifying a use-attribute-sets
   * attribute on xsl:element, xsl:copy (see [7.5 Copying]) or
   * xsl:attribute-set elements. The value of the use-attribute-sets
   * attribute is a whitespace-separated list of names of attribute
   * sets. Each name is specified as a QName, which is expanded as
   * described in [2.4 Qualified Names].
   *
   * @param v The value to set for the "use-attribute-sets" attribute. 
   */
  public void setUseAttributeSets(QName[] v)
  {
    m_attributeSetsNames = v;
  }

  /**
   * Get the "use-attribute-sets" attribute.
   * Attribute sets are used by specifying a use-attribute-sets
   * attribute on xsl:element, xsl:copy (see [7.5 Copying]) or
   * xsl:attribute-set elements, or a xsl:use-attribute-sets attribute on
   * Literal Result Elements.
   * The value of the use-attribute-sets
   * attribute is a whitespace-separated list of names of attribute
   * sets. Each name is specified as a QName, which is expanded as
   * described in [2.4 Qualified Names].
   *
   * @return The value of the "use-attribute-sets" attribute. 
   */
  public QName[] getUseAttributeSets()
  {
    return m_attributeSetsNames;
  }
  
  /**
   * Add the attributes from the named attribute sets to the attribute list.
   * TODO: Error handling for: "It is an error if there are two attribute sets
   * with the same expanded-name and with equal import precedence and that both
   * contain the same attribute unless there is a definition of the attribute
   * set with higher import precedence that also contains the attribute."
   *
   * @param transformer non-null reference to the the current transform-time state.
   * @param stylesheet The owning root stylesheet
   *
   * @throws TransformerException
   */
  public void applyAttrSets(
          TransformerImpl transformer, StylesheetRoot stylesheet)
            throws TransformerException
  {
    applyAttrSets(transformer, stylesheet, m_attributeSetsNames);
  }

  /**
   * Add the attributes from the named attribute sets to the attribute list.
   * TODO: Error handling for: "It is an error if there are two attribute sets
   * with the same expanded-name and with equal import precedence and that both
   * contain the same attribute unless there is a definition of the attribute
   * set with higher import precedence that also contains the attribute."
   *
   * @param transformer non-null reference to the the current transform-time state.
   * @param stylesheet The owning root stylesheet
   * @param attributeSetsNames List of attribute sets names to apply
   *
   * @throws TransformerException
   */
  private void applyAttrSets(
          TransformerImpl transformer, StylesheetRoot stylesheet, QName attributeSetsNames[])
            throws TransformerException
  {

    if (null != attributeSetsNames)
    {
      int nNames = attributeSetsNames.length;

      for (int i = 0; i < nNames; i++)
      {
        QName qname = attributeSetsNames[i];
        java.util.List attrSets = stylesheet.getAttributeSetComposed(qname);

        if (null != attrSets)
        {
          int nSets = attrSets.size();

          // Highest priority attribute set will be at the top,
          // so process it last.
          for (int k = nSets-1; k >= 0 ; k--)
          {
            ElemAttributeSet attrSet =
              (ElemAttributeSet) attrSets.get(k);

            attrSet.execute(transformer);
          }
        } 
        else 
        {
          throw new TransformerException(
              XSLMessages.createMessage(XSLTErrorResources.ER_NO_ATTRIB_SET, 
                  new Object[] {qname}),this); 
        }
      }
    }
  }

  /**
   * Copy attributes specified by use-attribute-sets to the result tree.
   * Specifying a use-attribute-sets attribute is equivalent to adding
   * xsl:attribute elements for each of the attributes in each of the
   * named attribute sets to the beginning of the content of the element
   * with the use-attribute-sets attribute, in the same order in which
   * the names of the attribute sets are specified in the use-attribute-sets
   * attribute. It is an error if use of use-attribute-sets attributes
   * on xsl:attribute-set elements causes an attribute set to directly
   * or indirectly use itself.
   *
   * @param transformer non-null reference to the the current transform-time state.
   *
   * @throws TransformerException
   */
  public void execute(
          TransformerImpl transformer)
            throws TransformerException
  {

    if (null != m_attributeSetsNames)
    {
      applyAttrSets(transformer, getStylesheetRoot(),
                    m_attributeSetsNames);
    }
 
  }
  
  /**
   * Validate an XSL stylesheet's literal result element content with an XML Schema,
   * built-in type specified by an attribute 'xsl:type'. 
   */
  protected void validateXslElementResultWithBuiltInSchemaType(String xmlStr, QName typeQname, 
  		                                                       XPathContext xctxt) throws TransformerException {
		
  	SourceLocator srcLocator = xctxt.getSAXLocator();

  	xmlStr = xmlStr.replaceFirst("<\\?.*\\?>", "");
  	String dataTypeLocalName = typeQname.getLocalName();
  	String xpathConstructorFuncExprStr = "xs:" + dataTypeLocalName + "('" + xmlStr + "')";

  	ElemTemplateElement elemTemplateElement = (ElemTemplateElement)xctxt.getNamespaceContext();
  	List<XMLNSDecl> prefixTable = null;
  	if (elemTemplateElement != null) {
  		prefixTable = (List<XMLNSDecl>)elemTemplateElement.getPrefixTable();
  	}
  	String xmlSchemaNsPrefix = XslTransformEvaluationHelper.getPrefixFromNsUri(XMLConstants.
  			                                                                            W3C_XML_SCHEMA_NS_URI, prefixTable);
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
  		throw new TransformerException("XTTE1540 : An element node constructed via literal result element,"
															                    + " is not valid according to type xs:" + dataTypeLocalName + " "
															    				+ "specified as value of literal result element's attribute 'xsl:type'.", srcLocator); 
  	}
   }
  
  /**
   * Validate an XSL stylesheet's literal result element content with an XML Schema,
   * user-defined type specified by an attribute 'xsl:type'. 
   */
   protected void validateXslElementResultWithUserDefinedSchemaType(String nodeName, TransformerImpl transformer,
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
			   throw new TransformerException("XTTE1540 : An element node constructed via literal result element,"
																                    + " is not valid according to type " + dataTypeLocalName + " "
																    				+ "specified as value of literal result element's attribute 'xsl:type'. "
																    				+ "" + errMesg, srcLocator);
		   }    					 
	   }
  }
   
  /**
   * When an XSL literal result element has "xsl:validation" attribute with values 
   * 'strict' or 'lax', validate an element node produced by literal result element's 
   * evaluation with the corresponding element declaration available in the schema. 
   */
  protected void validateXslElementResultWithSchemaElemDecl(String nodeName, TransformerImpl transformer,
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
  																						+ "literal result element.", srcLocator);
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
  			throw new TransformerException("XTTE1540 : An element node constructed via XSL stylesheet's literal "
															    					+ "result element, is not valid according to the corresponding "
															    					+ "element declaration available in the schema. " + errMesg, srcLocator); 
  		}
  	}
  } 
   
  /**
   * Check for the allowed values of "validation" attribute.
   */
  protected boolean isValidationStrOk(String validationStr) {
  	return ("strict".equals(validationStr) || "lax".equals(validationStr) || 
  			"preserve".equals(validationStr) || "strip".equals(validationStr));  
  }
}
