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

import javax.xml.transform.TransformerException;

import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMManager;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;

import xml.xpath31.processor.types.XSAnyType;

/**
 * The XSLT xsl:if element, is used to perform conditional processing,
 * within an XSLT stylesheet.
 * 
 * <xsl:if
        test = expression>
       <!-- Content: sequence-constructor -->
   </xsl:if>

 * @xsl.usage advanced
 */
public class ElemIf extends ElemTemplateElement
{
  static final long serialVersionUID = 2158774632427453022L;

  /**
   * The xsl:if element must have a test attribute, which specifies an expression.
   * @serial
   */
  private XPath m_test = null;

  /**
   * Set the "test" attribute.
   * The xsl:if element must have a test attribute, which specifies an expression.
   *
   * @param v test attribute to set
   */
  public void setTest(XPath v)
  {
    m_test = v;
  }

  /**
   * Get the "test" attribute.
   * The xsl:if element must have a test attribute, which specifies an expression.
   *
   * @return the "test" attribute for this element.
   */
  public XPath getTest()
  {
    return m_test;
  }
  
  /**
   * Class field to, represent the value of "xpath-default-namespace" 
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
   * is declared on xsl:if instruction.
   */
  private boolean m_expand_text_declared;
  
  /**
   * This class field, represents the value of "expand-text" 
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
   * This function is called after everything else has been
   * recomposed, and allows the template to set remaining
   * values that may be based on some other property that
   * depends on recomposition.
   *
   * @param sroot The root stylesheet.
   *
   * @throws TransformerException
   */
  public void compose(StylesheetRoot sroot) throws TransformerException
  {

    super.compose(sroot);

    java.util.Vector vnames = sroot.getComposeState().getVariableNames();

    if (null != m_test)
      m_test.fixupVariables(vnames, sroot.getComposeState().getGlobalsSize());
  }

  /**
   * Get an int constant identifying the type of element.
   * @see org.apache.xalan.templates.Constants
   *
   * @return The token ID for this element
   */
  public int getXSLToken()
  {
    return Constants.ELEMNAME_IF;
  }

  /**
   * Return the node name.
   *
   * @return the element's name
   */
  public String getNodeName()
  {
    return Constants.ELEMNAME_IF_STRING;
  }

  /**
   * Conditionally execute a sub-template.
   * The expression is evaluated and the resulting object is converted
   * to a boolean as if by a call to the boolean function. If the result
   * is true, then the content template is instantiated; otherwise, nothing
   * is created.
   *
   * @param transformer non-null reference to the the current transform-time state.
   *
   * @throws TransformerException
   */
  public void execute(TransformerImpl transformer) throws TransformerException
  {

    XPathContext xctxt = transformer.getXPathContext();
    int sourceNode = xctxt.getCurrentNode();
    
    if (m_xpath_default_namespace != null) {
       m_test = new XPath(m_test.getPatternString(), xctxt.getSAXLocator(), xctxt.getNamespaceContext(), XPath.SELECT, null);
    }

    if (transformer.getDebug())
    {
      XObject test = m_test.execute(xctxt, sourceNode, this);

      if (transformer.getDebug())
        transformer.getTraceManager().emitSelectedEvent(sourceNode, this,
                "test", m_test, test);
    
      // xsl:for-each now emits one trace event + one for every
      // iteration; changing xsl:if to emit one regardless of true/false

      if (transformer.getDebug())
        transformer.getTraceManager().emitTraceEvent(this);

      if (test.bool())
      {
        transformer.executeChildTemplates(this, true);        
      }

      if (transformer.getDebug())
        transformer.getTraceManager().emitTraceEndEvent(this);

      // I don't think we want this.  -sb
      //  if (transformer.getDebug())
      //    transformer.getTraceManager().emitSelectedEvent(sourceNode, this,
      //            "endTest", m_test, test);
    }
    /*else if (m_test.bool(xctxt, sourceNode, this))
    {
      transformer.executeChildTemplates(this, true);
    }*/
    else {
        XObject xpath3ContextItem = xctxt.getXPath3ContextItem();        
        if (xpath3ContextItem != null) {
            XPathContext xctxtNew = new XPathContext(false);            
            xctxtNew.setVarStack(xctxt.getVarStack());
            
            xctxtNew.setXPath3ContextPosition(xctxt.getXPath3ContextPosition());
            xctxtNew.setXPath3ContextSize(xctxt.getXPath3ContextSize());
                              
            DTMManager dtmMgr = xctxtNew.getDTMManager();
            String strVal = "";
            if (xpath3ContextItem instanceof XSAnyType) {
                strVal = ((XSAnyType)xpath3ContextItem).stringValue();     
            }
            else {
                strVal = xpath3ContextItem.str(); 
            }
            DTM docFragDtm = dtmMgr.createDTMForSimpleXMLDocument(strVal);
      
            int contextNode = docFragDtm.getFirstChild(docFragDtm.getDocument());            
            xctxtNew.pushCurrentNode(contextNode);
            xctxtNew.setSAXLocator(this);
            if (m_test.bool(xctxtNew, contextNode, this)) {
                transformer.executeChildTemplates(this, true);    
            }  
        }        
        else if (m_test.bool(xctxt, sourceNode, this)) {
           transformer.executeChildTemplates(this, true);
        }
    }
    
  }
  
  /**
   * Call the children visitors.
   * @param visitor The visitor whose appropriate method will be called.
   */
  protected void callChildVisitors(XSLTVisitor visitor, boolean callAttrs)
  {
  	if(callAttrs)
  		m_test.getExpression().callVisitors(m_test, visitor);
    super.callChildVisitors(visitor, callAttrs);
  }

}
