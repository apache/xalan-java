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

import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.transformer.TransformerImpl;

/**
 * Implementation of XSLT 3.0 xsl:next-match instruction.
 * 
 * @see <a href="https://www.w3.org/TR/xslt-30/#element-next-match">xsl:next-match XSLT 3.0 specification</a>
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class ElemNextMatch extends ElemTemplateElement
{

  private static final long serialVersionUID = -8730317226202081634L;

  /**
   * Get an int constant identifying the type of element.
   * @see org.apache.xalan.templates.Constants
   *
   * @return Token ID for xsl:next-match element types
   */
  public int getXSLToken()
  {
      return Constants.ELEMNAME_NEXT_MATCH;
  }

  /**
   * Return the node name.
   *
   * @return Element name
   */
  public String getNodeName()
  {
      return Constants.ELEMNAME_NEXT_MATCH_STRING;
  }

  /**
   * Execute the xsl:next-match transformation.
   *
   * @param transformer non-null reference to the the current transform-time state.
   *
   * @throws TransformerException
   */
  public void execute(TransformerImpl transformer) throws TransformerException
  {
     // no op
  }

  /**
   * Add a child to the child list.
   * <!ELEMENT xsl:next-match EMPTY>
   *
   * @param newChild New element to append to this element's children list
   *
   * @return null, xsl:next-match cannot have children 
   */
  public ElemTemplateElement appendChild(ElemTemplateElement newChild)
  {
	  super.appendChild(newChild);
    
	  String lineNo = String.valueOf(newChild.getLineNumber());
	  String columnNo = String.valueOf(newChild.getColumnNumber());

	  if (!(newChild instanceof ElemWithParam)) {
	     error(XSLTErrorResources.ER_CANNOT_ADD,
										  new Object[]{ newChild.getNodeName(),
												  this.getNodeName(), lineNo, columnNo });
	  }

	  return null;
  }
}
