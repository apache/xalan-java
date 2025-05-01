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

/**
 *  Implementation of the XSLT 3.0 xsl:import-schema instruction.
 *  
 *  @author Mukul Gandhi <mukulg@apache.org>
 *  
 *  @xsl.usage advanced
 */
public class ElemImportSchema extends ElemTemplateElement
{
   static final long serialVersionUID = -3070117361903102033L;

  /**
   * Get an int constant identifying the type of element.
   * @see org.apache.xalan.templates.Constants
   *
   * @return The token ID for this element
   */
  public int getXSLToken()
  {
    return Constants.ELEMNAME_IMPORT_SCHEMA;
  }

  /**
   * Return the node name.
   *
   * @return The element's name
   */
  public String getNodeName()
  {
    return Constants.ELEMNAME_IMPORT_SCHEMA_STRING;
  }

  /**
   * Constructor ElemImportSchema
   */
  public ElemImportSchema(){}

  /**
   * Execute the xsl:import-schema transformation.
   *
   * @param transformer non-null reference to the the current transform-time state.
   *
   * @throws TransformerException
   */
  public void execute(TransformerImpl transformer) throws TransformerException
  {
     // NO OP
  }

  /**
   * Tell if this element can accept variable declarations.
   * @return true if the element can accept and process variable declarations.
   */
  public boolean canAcceptVariables()
  {
  	return false;
  }
  
  public void compose(StylesheetRoot sroot) throws TransformerException
  {
	 // NO OP  
  }
  
  public void endCompose(StylesheetRoot sroot) throws TransformerException
  {
	 // NO OP  
  }
  
  public void setParentElem(ElemTemplateElement p)
  {
    super.setParentElem(p);
  }
  
  protected void callChildVisitors(XSLTVisitor visitor, boolean callAttrs)
  {
    super.callChildVisitors(visitor, callAttrs);
  }
  
  public ElemTemplateElement appendChild(ElemTemplateElement elem)
  {
    return super.appendChild(elem);
  }

}
