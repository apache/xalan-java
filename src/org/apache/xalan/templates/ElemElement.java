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

import org.w3c.dom.*;

import org.xml.sax.*;

import org.apache.xpath.*;
import org.apache.xml.utils.QName;
import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.transformer.ResultTreeHandler;

import javax.xml.transform.TransformerException;

/**
 * <meta name="usage" content="advanced"/>
 * Implement xsl:element
 * <pre>
 * <!ELEMENT xsl:element %template;>
 * <!ATTLIST xsl:element
 *   name %avt; #REQUIRED
 *   namespace %avt; #IMPLIED
 *   use-attribute-sets %qnames; #IMPLIED
 *   %space-att;
 * >
 * </pre>
 * @see <a href="http://www.w3.org/TR/xslt#section-Creating-Elements-with-xsl:element">XXX in XSLT Specification</a>
 */
public class ElemElement extends ElemUse
{

  /**
   * The name attribute is interpreted as an attribute value template.
   * It is an error if the string that results from instantiating the
   * attribute value template is not a QName.
   * @serial
   */
  protected AVT m_name_avt = null;

  /**
   * Set the "name" attribute.
   * The name attribute is interpreted as an attribute value template.
   * It is an error if the string that results from instantiating the
   * attribute value template is not a QName.
   *
   * @param v Name attribute to set for this element
   */
  public void setName(AVT v)
  {
    m_name_avt = v;
  }

  /**
   * Get the "name" attribute.
   * The name attribute is interpreted as an attribute value template.
   * It is an error if the string that results from instantiating the
   * attribute value template is not a QName.
   *
   * @return Name attribute for this element
   */
  public AVT getName()
  {
    return m_name_avt;
  }

  /**
   * If the namespace attribute is present, then it also is interpreted
   * as an attribute value template. The string that results from
   * instantiating the attribute value template should be a URI reference.
   * It is not an error if the string is not a syntactically legal URI reference.
   * @serial
   */
  protected AVT m_namespace_avt = null;

  /**
   * Set the "namespace" attribute.
   * If the namespace attribute is present, then it also is interpreted
   * as an attribute value template. The string that results from
   * instantiating the attribute value template should be a URI reference.
   * It is not an error if the string is not a syntactically legal URI reference.
   *
   * @param v NameSpace attribute to set for this element
   */
  public void setNamespace(AVT v)
  {
    m_namespace_avt = v;
  }

  /**
   * Get the "namespace" attribute.
   * If the namespace attribute is present, then it also is interpreted
   * as an attribute value template. The string that results from
   * instantiating the attribute value template should be a URI reference.
   * It is not an error if the string is not a syntactically legal URI reference.
   *
   * @return Namespace attribute for this element
   */
  public AVT getNamespace()
  {
    return m_namespace_avt;
  }

  /**
   * Get an int constant identifying the type of element.
   * @see org.apache.xalan.templates.Constants
   *
   * @return The token ID for this element
   */
  public int getXSLToken()
  {
    return Constants.ELEMNAME_ELEMENT;
  }

  /**
   * Return the node name.
   *
   * @return This element's name 
   */
  public String getNodeName()
  {
    return Constants.ELEMNAME_ELEMENT_STRING;
  }
  
  /**
   * Validate that the node name is good.
   * 
   * @param nodeName Name of the node being constructed, which may be null.
   * 
   * @return true if the node name is valid, false otherwise.
   */
   protected boolean validateNodeName(String nodeName)
   {
    if(nodeName == null)
      return false;
      
    int indexOfNSSep = nodeName.indexOf(':');
    int len = nodeName.length();
    
    if(len == 0)
      return false;
      
    if(indexOfNSSep + 1 == len)
      return false;
      
    if(indexOfNSSep == 0)
      return false;
      
    String localName = QName.getLocalPart(nodeName);
      
    if(isValidNCName(localName))
    {
      String prefix = QName.getPrefixPart(nodeName);
      if(prefix.length() == 0)
        return true;
      if(isValidNCName(prefix))
        return true;
    }

    return false;
   }
   
  /**
   * Resolve the namespace into a prefix.  Meant to be
   * overidded by elemAttribute if this class is derived.
   *
   * @param rhandler The current result tree handler.
   * @param prefix The probable prefix if already known.
   * @param nodeNamespace  The namespace.
   *
   * @return The prefix to be used.
   */
  protected String resolvePrefix(ResultTreeHandler rhandler,
                                 String prefix, String nodeNamespace)
    throws TransformerException
  {

//    if (null != prefix && prefix.length() == 0)
//    {
//      String foundPrefix = rhandler.getPrefix(nodeNamespace);
//
//      // System.out.println("nsPrefix: "+nsPrefix);           
//      if (null == foundPrefix)
//        foundPrefix = "";
//    }
    return prefix;
  }
    
  /**
   * Create an element in the result tree.
   * The xsl:element element allows an element to be created with a
   * computed name. The expanded-name of the element to be created
   * is specified by a required name attribute and an optional namespace
   * attribute. The content of the xsl:element element is a template
   * for the attributes and children of the created element.
   *
   * @param transformer non-null reference to the the current transform-time state.
   * @param sourceNode non-null reference to the <a href="http://www.w3.org/TR/xslt#dt-current-node">current source node</a>.
   * @param mode reference, which may be null, to the <a href="http://www.w3.org/TR/xslt#modes">current mode</a>.
   *
   * @throws TransformerException
   */
  public void execute(
          TransformerImpl transformer, Node sourceNode, QName mode)
            throws TransformerException
  {

    ResultTreeHandler rhandler = transformer.getResultTreeHandler();
    XPathContext xctxt = transformer.getXPathContext();
    String nodeName = m_name_avt.evaluate(xctxt, sourceNode, this);

    // make sure that if a prefix is specified on the attribute name, it is valid
    int indexOfNSSep = nodeName.indexOf(':');
    String prefix = null;
    String nodeNamespace = null;

    if (nodeName.length() == 0)
      nodeName = null;

    if ((null != nodeName) /* && (indexOfNSSep >= 0) */)
    {
      prefix = (indexOfNSSep > 0) ? nodeName.substring(0, indexOfNSSep) : "";

      // Catch the exception this may cause. We don't want to stop processing.
      try
      {
        nodeNamespace = getNamespaceForPrefix(prefix);

        if (null == nodeNamespace && indexOfNSSep <= 0)
          nodeNamespace = "";

        // Check if valid QName. Assuming that if the prefix is defined,
        // it is valid.
        if (!validateNodeName(nodeName))
        {
          transformer.getMsgMgr().warn(
            this, XSLTErrorResources.WG_ILLEGAL_ATTRIBUTE_NAME,
            new Object[]{ nodeName });

          nodeName = null;
        }
      }
      catch (Exception ex)
      {
        transformer.getMsgMgr().warn(
          this, XSLTErrorResources.WG_COULD_NOT_RESOLVE_PREFIX,
          new Object[]{ prefix });

        nodeName = null;
      }
    }

    // Check if valid QName
    else if (null == nodeName)
    {
      transformer.getMsgMgr().warn(
        this, XSLTErrorResources.WG_ILLEGAL_ATTRIBUTE_NAME,
        new Object[]{ nodeName });

      nodeName = null;
    }
    else if (nodeName.length() == 0 ||!isValidNCName(nodeName))
    {
      transformer.getMsgMgr().warn(
        this, XSLTErrorResources.WG_COULD_NOT_RESOLVE_PREFIX,
        new Object[]{ nodeName });

      nodeName = null;
    }

    if (null != nodeName)
    {
      if (null != m_namespace_avt)
      {
        nodeNamespace = m_namespace_avt.evaluate(xctxt, sourceNode, this);

        // System.out.println("nodeNamespace: "+nodeNamespace);
        if (null == nodeNamespace)
          nodeNamespace = "";

        prefix = resolvePrefix(rhandler, prefix, nodeNamespace);
        
        if (null == prefix)
          prefix = "";

        if (prefix.length() > 0)
          nodeName = (prefix + ":" + QName.getLocalPart(nodeName));
      }
      else if (null != prefix && null == nodeNamespace)
      {
        transformer.getMsgMgr().warn(
          this, XSLTErrorResources.WG_COULD_NOT_RESOLVE_PREFIX,
          new Object[]{ prefix });

        nodeName = null;
      }
    }

    constructNode(nodeName, prefix, nodeNamespace, transformer, sourceNode,
                  mode);
  }
  
  /**
   * Construct a node in the result tree.  This method is overloaded by 
   * xsl:attribute. At this class level, this method creates an element.
   *
   * @param nodeName The name of the node, which may be null.
   * @param prefix The prefix for the namespace, which may be null.
   * @param nodeNamespace The namespace of the node, which may be null.
   * @param transformer non-null reference to the the current transform-time state.
   * @param sourceNode non-null reference to the <a href="http://www.w3.org/TR/xslt#dt-current-node">current source node</a>.
   * @param mode reference, which may be null, to the <a href="http://www.w3.org/TR/xslt#modes">current mode</a>.
   *
   * @throws TransformerException
   */
  void constructNode(
          String nodeName, String prefix, String nodeNamespace, TransformerImpl transformer, Node sourceNode, QName mode)
            throws TransformerException
  {

    try
    {
      ResultTreeHandler rhandler = transformer.getResultTreeHandler();

      if (null != nodeName)
      {

        // Add namespace declarations.
        executeNSDecls(transformer);

        if (null != prefix)
        {
          rhandler.startPrefixMapping(prefix, nodeNamespace, true);
        }

        rhandler.startElement(nodeNamespace, QName.getLocalPart(nodeName),
                              nodeName, null);
      }

      boolean shouldAddAttrs = (null != nodeName);

      if (shouldAddAttrs)
        super.execute(transformer, sourceNode, mode);

      transformer.executeChildTemplates(this, sourceNode, mode,
                                        shouldAddAttrs);

      // Now end the element if name was valid
      if (null != nodeName)
      {
        rhandler.endElement(nodeNamespace, QName.getLocalPart(nodeName),
                            nodeName);
        unexecuteNSDecls(transformer);
      }
    }
    catch (SAXException se)
    {
      throw new TransformerException(se);
    }
  }
}
