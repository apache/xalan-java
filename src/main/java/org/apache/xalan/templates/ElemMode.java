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

import javax.xml.transform.TransformerException;

import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xml.utils.QName;

/**
 * A class definition, supporting an implementation of 
 * XSLT 3.0 xsl:mode instruction.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class ElemMode extends ElemTemplateElement
{
  
  private static final long serialVersionUID = 5605801317717210156L;

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
   * @param stylesheet The owning stylesheet for this element
   */
  public void setStylesheet(Stylesheet stylesheet)
  {
	  m_stylesheet = stylesheet;
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
   * An xsl:mode element's 'name' attribute.
   */
  private QName m_name = null;
  
  /**
   * An xsl:mode element's 'on-no-match' attribute.
   * 
   * This has default value "text-only-copy", which may be 
   * overridden by XSL stylesheet's xsl:mode declaration. 
   */
  private String m_onNoMatch = Constants.ATTRVAL_TEXT_ONLY_COPY;
  
  /**
   * An xsl:mode element's 'on-multiple-match' attribute.
   * 
   * This has default value "use-last", which may be overridden 
   * by XSL stylesheet's xsl:mode declaration.
   */
  private String m_onMultipleMatch = Constants.ATTRVAL_USE_LAST;
  
  /**
   * An xsl:mode element's 'warning-on-no-match' attribute.
   * 
   * Xalan's default value for this attribute is false.
   */
  private boolean m_warningOnNoMatch = false;
  
  /**
   * This has boolean value true, if xsl:mode instruction's attribute 
   * 'warning-on-no-match' is declared within an XSL stylesheet. 
   */
  private boolean m_warningOnNoMatchDeclared = false;
  
  /**
   * An xsl:mode element's 'warning-on-multiple-match' attribute.
   * 
   * Xalan's default value for this attribute is false.
   */
  private boolean m_warningOnMultipleMatch = false;
  
  /**
   * This has boolean value true, if xsl:mode instruction's attribute 
   * 'warning-on-multiple-match' is declared within an XSL stylesheet. 
   */
  private boolean m_warningOnMultipleMatchDeclared = false;

  /**
   * Set the "name" attribute.
   *
   * @param qname Value to set the "name" attribute
   */
  public void setName(QName qname)
  {
	  m_name = qname;
  }

  /**
   * Get the "name" attribute.
   *
   * @return Value of the "name" attribute
   */
  public QName getName()
  {
	  return m_name;
  }
  
  /**
   * Get the "on-no-match" attribute.
   *
   * @return Value of the "on-no-match" attribute
   */
  public String getOnNoMatch()
  {
	  return m_onNoMatch;
  }
  
  /**
   * Set the "on-no-match" attribute.
   *
   * @param onNoMatch Value to set the "on-no-match" attribute
   */
  public void setOnNoMatch(String onNoMatch)
  {
	  m_onNoMatch = onNoMatch;
  }
  
  /**
   * Get the "on-multiple-match" attribute.
   *
   * @return Value of the "on-multiple-match" attribute
   */
  public String getOnMultipleMatch() {
	  return m_onMultipleMatch;
  }

  /**
   * Set the "on-multiple-match" attribute.
   *
   * @param onNoMatch Value to set the "on-multiple-match" attribute
   */
  public void setOnMultipleMatch(String onMultipleMatch) {
	  this.m_onMultipleMatch = onMultipleMatch;
  }

  /**
   * Get the "warning-on-no-match" attribute.
   *
   * @return Value of the "warning-on-no-match" attribute
   */
  public boolean isWarningOnNoMatch() {
	  return m_warningOnNoMatch;
  }

  /**
   * Set the "warning-on-no-match" attribute.
   *
   * @param onNoMatch Value to set the "warning-on-no-match" attribute
   */
  public void setWarningOnNoMatch(boolean warningOnNoMatch) {
	  this.m_warningOnNoMatch = warningOnNoMatch;
	  
	  m_warningOnNoMatchDeclared = true;
  }
  
  public boolean isWarningOnNoMatchDeclared() {
	  return m_warningOnNoMatchDeclared;
  }

  /**
   * Get the "warning-on-multiple-match" attribute.
   *
   * @return Value of the "warning-on-multiple-match" attribute
   */
  public boolean isWarningOnMultipleMatch() {
	  return m_warningOnMultipleMatch;
  }

  /**
   * Set the "warning-on-multiple-match" attribute.
   *
   * @param onNoMatch Value to set the "warning-on-multiple-match" attribute
   */
  public void setWarningOnMultipleMatch(boolean warningOnMultipleMatch) {
	  this.m_warningOnMultipleMatch = warningOnMultipleMatch;
	  
	  m_warningOnMultipleMatchDeclared = true;
  }
  
  public boolean isWarningOnMultipleMatchDeclared() {
	  return m_warningOnMultipleMatchDeclared;
  }

/**
   * Get an int constant identifying the type of element.
   * @see org.apache.xalan.templates.Constants
   *
   * @return The token id for the element
   */
  public int getXSLToken()
  {
	  return Constants.ELEMNAME_MODE;
  }

  /**
   * Return the node name.
   *
   * @return The element's name
   */
  public String getNodeName()
  {
	  return Constants.ATTRNAME_MODE;
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
  }
  
  /**
   * This method is called after the template's children have been composed.
   */
  public void endCompose(StylesheetRoot sroot) throws TransformerException
  {
	  super.endCompose(sroot);
  }

  /**
   * A placeholder method definition, that doesn't do evaluation.
   *
   * @param transformer non-null reference to the the current transform-time state.
   *
   * @throws TransformerException
   */
  public void execute(TransformerImpl transformer) throws TransformerException {    
	  // no op    
  }

}
