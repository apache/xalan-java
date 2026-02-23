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
package org.apache.xalan.processor;

import org.apache.xalan.templates.ElemGlobalContextItem;
import org.apache.xalan.templates.ElemTemplateElement;

/**
 * This class processes parse events for an xsl:global-context-item 
 * instruction.
 *
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class ProcessorGlobalContextItemDecl extends ProcessorTemplateElem
{
    
   private static final long serialVersionUID = -7859018545629928282L;

  /**
   * Append the current template element to the current
   * template element, and then push it onto the current template
   * element stack.
   *
   * @param handler non-null reference to current StylesheetHandler that is constructing the Templates.
   * @param elem The non-null reference to the ElemVariable element.
   *
   * @throws org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   */
  protected void appendAndPush(
          StylesheetHandler handler, ElemTemplateElement elem)
            throws org.xml.sax.SAXException
  {

	  handler.pushElemTemplateElement(elem);
  }

  /**
   * Receive notification of the end of an element.
   *
   * @param name The element type name.
   * @param attributes The specified or defaulted attributes.
   *
   * @param handler non-null reference to current StylesheetHandler that is constructing the Templates.
   * @param uri The Namespace URI, or an empty string.
   * @param localName The local name (without prefix), or empty string if not namespace processing.
   * @param rawName The qualified name (with prefix).
   */
  public void endElement(
          StylesheetHandler handler, String uri, String localName, String rawName)
            throws org.xml.sax.SAXException
  {

	  ElemGlobalContextItem elemGlobalContextItem = (ElemGlobalContextItem)(handler.getElemTemplateElement());

	  handler.getStylesheet().appendChild(elemGlobalContextItem);
	  handler.getStylesheet().setGlobalContextItem(elemGlobalContextItem);
	  super.endElement(handler, uri, localName, rawName);
  }
}
