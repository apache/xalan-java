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
package org.apache.xalan.processor;

import javax.xml.transform.TransformerException;

import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.templates.Constants;
import org.apache.xalan.templates.ElemTemplateElement;
import org.xml.sax.Attributes;

/**
 * This class processes parse events for an XSLT template element.
 * @see <a href="http://www.w3.org/TR/xslt#dtd">XSLT DTD</a>
 * @see <a href="http://www.w3.org/TR/xslt#section-Creating-the-Result-Tree">section-Creating-the-Result-Tree in XSLT Specification</a>
 */
public class ProcessorTemplateElem extends XSLTElementProcessor
{
    static final long serialVersionUID = 8344994001943407235L;

  /**
   * Receive notification of the start of an element.
   *
   * @param handler non-null reference to current StylesheetHandler that is constructing the Templates.
   * @param uri The Namespace URI, or an empty string.
   * @param localName The local name (without prefix), or empty string if not namespace processing.
   * @param rawName The qualified name (with prefix).
   * @param attributes The specified or defaulted attributes.
   */
  public void startElement(
          StylesheetHandler handler, String uri, String localName, String rawName, Attributes attributes)
            throws org.xml.sax.SAXException
  {
	  
	verifyXSLAllowedAttributes(localName, attributes);

    super.startElement(handler, uri, localName, rawName, attributes);
    try
    {
      // ElemTemplateElement parent = handler.getElemTemplateElement();
      XSLTElementDef def = getElemDef();
      Class classObject = def.getClassObject();
      ElemTemplateElement elem = null;

      try
      {
        elem = (ElemTemplateElement) classObject.newInstance();

        elem.setDOMBackPointer(handler.getOriginatingNode());
        elem.setLocaterInfo(handler.getLocator());
        elem.setPrefixes(handler.getNamespaceSupport());
      }
      catch (InstantiationException ie)
      {
        handler.error(XSLTErrorResources.ER_FAILED_CREATING_ELEMTMPL, null, ie);//"Failed creating ElemTemplateElement instance!", ie);
      }
      catch (IllegalAccessException iae)
      {
        handler.error(XSLTErrorResources.ER_FAILED_CREATING_ELEMTMPL, null, iae);//"Failed creating ElemTemplateElement instance!", iae);
      }

      setPropertiesFromAttributes(handler, rawName, attributes, elem);
      appendAndPush(handler, elem);
    }
    catch(TransformerException te)
    {
      throw new org.xml.sax.SAXException(te);
    }
  }
  
  /**
   * Method definition to verify attributes that can be present on 
   * specific XSLT instructions as per XSLT 3.0 specification.
   * 
   * @param localName					An XSLT instruction's local name for an instruction
   *                                    that is present within the stylesheet.
   * @param attributes					XSLT instruction whose local name is localName, it's 
   *                                    attributes that are present within the stylesheet.	
   */
  private void verifyXSLAllowedAttributes(String localName, Attributes attributes) 
		                                                                      throws org.apache.xml.utils.WrappedRuntimeException {
	  if (Constants.ELEMNAME_FOREACHGROUP_STRING.equals(localName)) {
		  int noOfAttributes = attributes.getLength();
		  if (noOfAttributes > 0) {
			  XSLTElementDef elemDef = getElemDef();
			  for (int idx = 0; idx < noOfAttributes; idx++) {
				  String attrLocalName = attributes.getLocalName(idx);
				  XSLTAttributeDef attrDef = elemDef.getAttributeDef(null, attrLocalName);
				  if (attrDef == null) {
					  TransformerException te = new TransformerException("XTSE0090 : Attribute '" + attrLocalName + "' is not allowed "
							  															          + "to appear on element " + Constants.
							  															          ELEMNAME_FOREACHGROUP_STRING + ".", this);
					  throw new org.apache.xml.utils.WrappedRuntimeException(te);
				  }
			  }
		  }
	  }
  }

  /**
   * Append the current template element to the current
   * template element, and then push it onto the current template
   * element stack.
   *
   * @param handler non-null reference to current StylesheetHandler that is constructing the Templates.
   * @param elem non-null reference to a the current template element.
   *
   * @throws org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   */
  protected void appendAndPush(
          StylesheetHandler handler, ElemTemplateElement elem)
            throws org.xml.sax.SAXException
  {

    ElemTemplateElement parent = handler.getElemTemplateElement();
    if(null != parent)  // defensive, for better multiple error reporting. -sb
    {
      parent.appendChild(elem);
      handler.pushElemTemplateElement(elem);
    }
  }

  /**
   * Receive notification of the end of an element.
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
    super.endElement(handler, uri, localName, rawName);
    handler.popElemTemplateElement().setEndLocaterInfo(handler.getLocator());
  }
  
}
