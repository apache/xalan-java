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
import org.apache.xml.utils.QName;
import org.apache.xpath.XPathContext;

/**
 * Implementation of XSLT 3.0 xsl:expose element.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage internal
 */
public class ElemExpose extends ElemTemplateElement
{
  
  private static final long serialVersionUID = 1560924627242710382L;

  /**
   * Get an int constant identifying the type of element.
   * @see org.apache.xalan.templates.Constants
   *
   * @return The token id for the element
   */
  public int getXSLToken()
  {
	  return Constants.ELEMNAME_USE_PACKAGE;
  }

  /**
   * Return the node name.
   *
   * @return The element's name
   */
  public String getNodeName()
  {
	  return Constants.ELEMNAME_USE_PACKEGE_STRING;
  }
  
  /**
   * An xsl:package instruction that, this xsl:use-package 
   * instruction refers to.
   */
  private Stylesheet m_stylesheet;
  
  public void setStylesheet(Stylesheet stylesheet) {
	  m_stylesheet = stylesheet;  
  }
  
  public Stylesheet getStylesheet() {
	 return m_stylesheet; 
  }
  
  /**
   * Class field, to refer to xsl:expose instruction's attribute 
   * "component".
   */
  private String m_component;
  
  /**
   * Class field, to refer to xsl:expose instruction's attribute 
   * "names".
   */
  private Vector m_names;
  
  /**
   * Class field, to refer to xsl:expose instruction's attribute 
   * "visibility".
   */
  private String m_visibility;
  
  public String getComponent() {
	  return m_component;
  }

  public void setComponent(String component) {
	  this.m_component = component;
  }

  public Vector getNames() {
	  return m_names;
  }

  public void setNames(Vector names) {
	  this.m_names = names;
  }

  public String getVisibility() {
	  return m_visibility;
  }

  public void setVisibility(String visibility) {
	  this.m_visibility = visibility;
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
   * Evaluate an XSL xsl:use-package transformation.
   *
   * @param transformer non-null reference to the the current transform-time state.
   *
   * @throws TransformerException
   */
  public void execute(TransformerImpl transformer) throws TransformerException {
    
	  XPathContext xctxt = transformer.getXPathContext();

	  SourceLocator srcLocator = xctxt.getSAXLocator();

	  if (transformer.getDebug()) {
		  transformer.getTraceManager().emitTraceEvent(this);		  
	  }      

	  // to do

	  if (transformer.getDebug()) {
		  transformer.getTraceManager().emitTraceEndEvent(this);
	  }
    
  }

}
