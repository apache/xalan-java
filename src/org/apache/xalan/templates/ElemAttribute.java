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

import java.util.Vector;

import javax.xml.transform.TransformerException;

import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.transformer.TransformerImpl;
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
 * XSLT 3.0 xsl:attribute element.
 * 
 * <xsl:attribute
           name = { qname }
           namespace? = { uri }
           select? = expression
           separator? = { string }
           type? = eqname
           validation? = "strict" | "lax" | "preserve" | "strip" >
        <!-- Content: sequence-constructor -->
   </xsl:attribute>

 * @xsl.usage advanced
 */

/* 
   Implementation of the XSLT 3.0 xsl:attribute instruction.
*/
public class ElemAttribute extends ElemElement
{
    static final long serialVersionUID = 8817220961566919187L;
    
    /**
     * The "select" expression.
     */
    protected Expression m_selectExpression = null;
    
    // The following two variables are used, for performing fixupVariables
    // action on certain XPath expression objects, within an object of this
    // class.
    private Vector fVars;    
    private int fGlobalsSize;

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
    
    fVars = (java.util.Vector)(vnames.clone());
    fGlobalsSize = golbalsSize; 
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
    
        if (null != nodeName && nodeName.length() > 0) {
              SerializationHandler rhandler = transformer.getSerializationHandler();
              
              // XSLT 3.0 spec notes : the string value of the new attribute node may be defined either 
              // by using the select attribute, or by the sequence constructor that forms the content of 
              // the xsl:attribute element. These are mutually exclusive: if the select attribute is present 
              // then the sequence constructor must be empty, and if the sequence constructor is non-empty  
              // then the select attribute must be absent.              
              if (m_selectExpression != null && this.m_firstChild != null) {
                  throw new TransformerException("XTSE0840 : An xsl:attribute element with a select attribute must "
                                                     + "be empty.", xctxt.getSAXLocator());   
              }
        
              String val = null;
                            
              if (m_selectExpression != null) {
                  // evaluate the value of xsl:attribute's "select" attribute
                  
                  if (fVars != null) {
                     m_selectExpression.fixupVariables(fVars, fGlobalsSize);   
                  }
                  
                  XObject xpathEvalResult = m_selectExpression.execute(xctxt);              
                  val = xpathEvalResult.str();
              }
              else {
                  // evaluate the value of xsl:attribute element's child contents          
                  val = transformer.transformToString(this);
              }
              
              try {
                    // let the result tree handler add the attribute and its String value.
                    String localName = QName.getLocalPart(nodeName);
                    if (prefix != null && prefix.length() > 0) {
                        rhandler.addAttribute(nodeNamespace, localName, nodeName, "CDATA", val, true);
                    }
                    else {
                        rhandler.addAttribute("", localName, nodeName, "CDATA", val, true);
                    }
              }
              catch (SAXException e) {
                   // no op
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

}
