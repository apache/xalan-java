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

import javax.xml.transform.TransformerException;

import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.transformer.TransformerImpl;
import org.w3c.dom.DOMException;

/**
 * Implement xsl:comment.
 * <pre>
 * <!ELEMENT xsl:comment %char-template;>
 * <!ATTLIST xsl:comment %space-att;>
 * </pre>
 * @see <a href="http://www.w3.org/TR/xslt#section-Creating-Comments">section-Creating-Comments in XSLT Specification</a>
 * @xsl.usage advanced
 */
public class ElemComment extends ElemTemplateElement
{
  static final long serialVersionUID = -8813199122875770142L;
      
  private Vector m_vars;
  
  private int m_globals_size;
  
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
   * is declared on xsl:comment instruction.
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
   */
  public void compose(StylesheetRoot sroot) throws TransformerException {	  
	  StylesheetRoot.ComposeState cstate = sroot.getComposeState();

	  java.util.Vector vnames = cstate.getVariableNames();

	  m_vars = (Vector)vnames.clone();
	  m_globals_size = cstate.getGlobalsSize();

	  super.compose(sroot);
  }

  /**
   * Get an int constant identifying the type of element.
   * @see org.apache.xalan.templates.Constants
   *
   * @return The token ID for this element
   */
  public int getXSLToken()
  {
    return Constants.ELEMNAME_COMMENT;
  }

  /**
   * Return the node name.
   *
   * @return This element's name
   */
  public String getNodeName()
  {
    return Constants.ELEMNAME_COMMENT_STRING;
  }

  /**
   * Execute the xsl:comment transformation 
   *
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
      transformer.getTraceManager().emitTraceEvent(this);
    try
    {
      // Note the content model is:
      // <!ENTITY % instructions "
      // %char-instructions;
      // | xsl:processing-instruction
      // | xsl:comment
      // | xsl:element
      // | xsl:attribute
      // ">
      String data = transformer.transformToString(this);
      
      boolean isExpandText = false;
      if (m_expand_text_declared) {
    	 isExpandText = m_expand_text;  
      }
      else {
         ElemTemplateElement elemTemplateElem = getParentElem();      
         isExpandText = getExpandTextValue(elemTemplateElem);
      }
      
      if (isExpandText) {
    	 data = getStrValueAfterExpandTextProcessing(data, transformer, m_vars, m_globals_size);
      }

      transformer.getResultTreeHandler().comment(data);
    }
    catch(org.xml.sax.SAXException se)
    {
      throw new TransformerException(se);
    }
    finally
    {
      if (transformer.getDebug())
        transformer.getTraceManager().emitTraceEndEvent(this);
    }
  }

  /**
   * Add a child to the child list.
   *
   * @param newChild Child to add to this node's child list
   *
   * @return Child that was just added to child list
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
   * This after the template's children have been composed.
   */
  public void endCompose(StylesheetRoot sroot) throws TransformerException
  {
     super.endCompose(sroot);
  }
}
