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

import java.util.ArrayList;
import java.util.List;

import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.KeyDeclaration;
import org.apache.xalan.templates.Stylesheet;
import org.apache.xml.utils.QName;
import org.apache.xml.utils.SAXSourceLocator;
import org.xml.sax.Attributes;

/**
 * TransformerFactory for xsl:key markup.
 * <pre>
 * <!ELEMENT xsl:key EMPTY>
 * <!ATTLIST xsl:key
 *   name %qname; #REQUIRED
 *   match %pattern; #REQUIRED
 *   use %expr; #REQUIRED
 * >
 * </pre>
 * @see <a href="http://www.w3.org/TR/xslt#dtd">XSLT DTD</a>
 * @see <a href="http://www.w3.org/TR/xslt#key">key in XSLT Specification</a>
 */
class ProcessorKey extends XSLTElementProcessor
{
   static final long serialVersionUID = 4285205417566822979L;

  /**
   * Receive notification of the start of an xsl:key element.
   *
   * @param handler The calling StylesheetHandler/TemplatesBuilder.
   * @param uri The Namespace URI, or the empty string if the
   *        element has no Namespace URI or if Namespace
   *        processing is not being performed.
   * @param localName The local name (without prefix), or the
   *        empty string if Namespace processing is not being
   *        performed.
   * @param rawName The raw XML 1.0 name (with prefix), or the
   *        empty string if raw names are not available.
   * @param attributes The attributes attached to the element.  If
   *        there are no attributes, it shall be an empty
   *        Attributes object.
   */
  public void startElement(
          StylesheetHandler handler, String uri, String localName, String rawName, Attributes attributes)
            throws org.xml.sax.SAXException
  {

	  KeyDeclaration kd = new KeyDeclaration(handler.getStylesheet(), handler.nextUid());

	  kd.setDOMBackPointer(handler.getOriginatingNode());
	  kd.setLocaterInfo(handler.getLocator());
	  setPropertiesFromAttributes(handler, rawName, attributes, kd);

	  Stylesheet stylesheet = handler.getStylesheet();
	  int xslKeyCount = stylesheet.getKeyCount();
	  for (int idx = 0; idx < xslKeyCount; idx++) {
		  KeyDeclaration keyDecl2 = stylesheet.getKey(idx);
		  QName keyName = keyDecl2.getName();       
		  if (keyName.equals(kd.getName())) {
			  if (kd.getComposite() != keyDecl2.getComposite()) {
				  SAXSourceLocator srcLocator = handler.getLocator();
				  int lineNo = srcLocator.getLineNumber();
				  int colNo = srcLocator.getColumnNumber();
				  String errLocationStr = "[" + lineNo + ", " + colNo + "]";
				  String keyNameStr = keyName.toString();    		  
				  throw new org.xml.sax.SAXException("XTSE1222 : " + errLocationStr + " Within an XSL stylesheet, there are more "
																											   + "than one key declarations with name '" 
																											   + keyNameStr + "', whose attribute 'composite' "
																											   + "value are not consistent.");   
			  }
		  }
	  }

	  appendAndPush(handler, kd);

	  stylesheet.setKey(kd);
  }

  /**
   * Set the properties of an object from the given attribute list.
   * @param handler The stylesheet's Content handler, needed for
   *                error reporting.
   * @param rawName The raw name of the owner element, needed for
   *                error reporting.
   * @param attributes The list of attributes.
   * @param target The target element where the properties will be set.
   */
  void setPropertiesFromAttributes(
          StylesheetHandler handler, String rawName, Attributes attributes, 
          org.apache.xalan.templates.ElemTemplateElement target)
            throws org.xml.sax.SAXException
  {

    XSLTElementDef def = getElemDef();

    // Keep track of which XSLTAttributeDefs have been processed, so 
    // I can see which default values need to be set.
    List processedDefs = new ArrayList();
    int nAttrs = attributes.getLength();

    for (int i = 0; i < nAttrs; i++)
    {
      String attrUri = attributes.getURI(i);
      String attrLocalName = attributes.getLocalName(i);
      XSLTAttributeDef attrDef = def.getAttributeDef(attrUri, attrLocalName);

      if (null == attrDef)
      {

        // Then barf, because this element does not allow this attribute.
        handler.error(attributes.getQName(i)
                      + "attribute is not allowed within an XML element " + rawName + ".", null);
      }
      else
      {
        String valueString = attributes.getValue(i);
        
        int colonIdx = valueString.indexOf(':');
        boolean valueStrOk = false;
        int idx2 = valueString.indexOf(org.apache.xpath.compiler.Keywords.FUNC_KEY_STRING + "(");
        if ((colonIdx != -1) && (idx2 >= 0) && ((colonIdx + 1) == idx2)) {
        	valueStrOk = true;
        }

        if (!valueStrOk && (idx2 >= 0)) {
		   handler.error(XSLMessages.createMessage(
				                                  XSLTErrorResources.ER_INVALID_KEY_CALL, null), null);
        }

        processedDefs.add(attrDef);
        attrDef.setAttrValue(handler, attrUri, attrLocalName,
                             attributes.getQName(i), attributes.getValue(i),
                             target);
      }
    }

    XSLTAttributeDef[] attrDefs = def.getAttributes();
    int nAttrDefs = attrDefs.length;

    for (int i = 0; i < nAttrDefs; i++)
    {
      XSLTAttributeDef attrDef = attrDefs[i];
      String defVal = attrDef.getDefault();

      if (null != defVal)
      {
        if (!processedDefs.contains(attrDef))
        {
          attrDef.setDefAttrValue(handler, target);
        }
      }

      if (attrDef.getRequired())
      {
        if (!processedDefs.contains(attrDef))
          handler.error(
            XSLMessages.createMessage(
              XSLTErrorResources.ER_REQUIRES_ATTRIB, new Object[]{ rawName,
                                                                   attrDef.getName() }), null);
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
	  if (null != parent)
	  {		  
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
