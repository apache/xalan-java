/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights 
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:  
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Xalan" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 1999, Lotus
 * Development Corporation., http://www.lotus.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.xalan.templates;

import org.w3c.dom.Node;
import org.w3c.dom.DOMException;

import org.xml.sax.SAXException;

import org.apache.xalan.utils.QName;
import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.transformer.ResultTreeHandler;
import org.apache.xpath.XPathContext;

/**
 * <meta name="usage" content="advanced"/>
 * Implement xsl:attribute.
 * <pre>
 * &amp;!ELEMENT xsl:attribute %char-template;>
 * &amp;!ATTLIST xsl:attribute
 *   name %avt; #REQUIRED
 *   namespace %avt; #IMPLIED
 *   %space-att;
 * &amp;
 * </pre>
 * @see <a href="http://www.w3.org/TR/xslt#creating-attributes">creating-attributes in XSLT Specification</a>
 */
public class ElemAttribute extends ElemTemplateElement
{

  /**
   * The local name which should be used.
   */
  public AVT m_name_avt = null;

  /**
   * The namespace which should be used.
   */
  public AVT m_namespace_avt = null;

  /**
   * Set the "name" attribute.
   *
   * NEEDSDOC @param name
   */
  public void setName(AVT name)
  {
    m_name_avt = name;
  }

  /**
   * Get the "name" attribute.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public AVT getName()
  {
    return m_name_avt;
  }

  /**
   * Set the "namespace" attribute.
   *
   * NEEDSDOC @param name
   */
  public void setNamespace(AVT name)
  {
    m_namespace_avt = name;
  }

  /**
   * Get the "namespace" attribute.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public AVT getNamespace()
  {
    return m_namespace_avt;
  }

  /**
   * Get an int constant identifying the type of element.
   * @see org.apache.xalan.templates.Constants
   *
   * NEEDSDOC ($objectName$) @return
   */
  public int getXSLToken()
  {
    return Constants.ELEMNAME_ATTRIBUTE;
  }

  /**
   * Return the node name.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public String getNodeName()
  {
    return Constants.ELEMNAME_ATTRIBUTE_STRING;
  }

  /**
   * Create an attribute in the result tree.
   * @see <a href="http://www.w3.org/TR/xslt#creating-attributes">creating-attributes in XSLT Specification</a>
   *
   * NEEDSDOC @param transformer
   * NEEDSDOC @param sourceNode
   * NEEDSDOC @param mode
   *
   * @throws SAXException
   */
  public void execute(
          TransformerImpl transformer, Node sourceNode, QName mode)
            throws SAXException
  {

    if (TransformerImpl.S_DEBUG)
      transformer.getTraceManager().fireTraceEvent(sourceNode, mode, this);

    ResultTreeHandler rhandler = transformer.getResultTreeHandler();
    XPathContext xctxt = transformer.getXPathContext();

    // The attribute name has to be evaluated as an AVT.
    String attrName = m_name_avt.evaluate(xctxt, sourceNode, this);
    String origAttrName = attrName;  // save original attribute name

    // Get the children of the xsl:attribute element as the string value.
    String val = transformer.transformToString(this, sourceNode, mode);

    // If they are trying to add an attribute when there isn't an 
    // element pending, it is an error.
    if (!rhandler.isElementPending())
    {
      transformer.getMsgMgr().warn(
        XSLTErrorResources.WG_ILLEGAL_ATTRIBUTE_NAME,
        new Object[]{ origAttrName });

      return;

      // warn(templateChild, sourceNode, "Trying to add attribute after element child has been added, ignoring...");
    }

    if (null == attrName)
      return;

    String attrNameSpace = null;  // by default

    // Did they declare a namespace attribute?
    if (null != m_namespace_avt)
    {

      // The namespace attribute is an AVT also.
      attrNameSpace = m_namespace_avt.evaluate(xctxt, sourceNode, this);

      if (null != attrNameSpace && attrNameSpace.length() > 0)
      {

        // Get the prefix for that attribute in the result namespace.
        String prefix = rhandler.getPrefix(attrNameSpace);

        // If we didn't find the prefix mapping, make up a prefix 
        // and have it declared in the result tree.
        if (null == prefix)
        {
          prefix = rhandler.getNewUniqueNSPrefix();

          rhandler.startPrefixMapping(prefix, attrNameSpace, false);
        }

        // add the prefix to the attribute name.
        attrName = (prefix + ":" + QName.getLocalPart(attrName));
      }
    }

    // Is the attribute xmlns type?
    else if (QName.isXMLNSDecl(origAttrName))
    {

      // Then just declare the namespace prefix and get out.
      String prefix = QName.getPrefixFromXMLNSDecl(origAttrName);
      String ns = rhandler.getURI(prefix);

      if (null == ns)
        rhandler.startPrefixMapping(prefix, val, false);

      return;
    }

    // Note we are using original attribute name for these tests. 
    else
    {

      // Does the attribute name have a prefix?
      String nsprefix = QName.getPrefixPart(origAttrName);

      if (null == nsprefix)
        nsprefix = "";

      // We're going to claim that this must be resolved in 
      // the result tree namespace.
      try
      {
        attrNameSpace = getNamespaceForPrefix(nsprefix);

        if ((null == attrNameSpace) && (nsprefix.length() > 0))
        {
          transformer.getMsgMgr().warn(
            XSLTErrorResources.WG_COULD_NOT_RESOLVE_PREFIX,
            new Object[]{ nsprefix });

          return;
        }
      }
      catch (Exception ex)
      {

        // Could not resolve prefix
        attrNameSpace = null;

        transformer.getMsgMgr().warn(
          XSLTErrorResources.WG_COULD_NOT_RESOLVE_PREFIX,
          new Object[]{ nsprefix });

        return;
      }
    }

    String localName = QName.getLocalPart(attrName);

    rhandler.addAttribute(attrNameSpace, localName, attrName, "CDATA", val);
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
   * NEEDSDOC @param newChild
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws DOMException
   */
  public Node appendChild(Node newChild) throws DOMException
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
}
