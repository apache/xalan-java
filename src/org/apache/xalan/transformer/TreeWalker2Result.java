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
package org.apache.xalan.transformer;

import org.w3c.dom.*;

import org.xml.sax.*;

import org.apache.xml.utils.TreeWalker;
import org.apache.xml.utils.MutableAttrListImpl;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xpath.DOMHelper;

/**
 * <meta name="usage" content="internal"/>
 * Handle a walk of a tree, but screen out attributes for
 * the result tree.
 */
public class TreeWalker2Result extends TreeWalker
{

  /** The transformer instance          */
  TransformerImpl m_transformer;

  /** The result tree handler          */
  ResultTreeHandler m_handler;

  /** Node where to start the tree walk           */
  Node m_startNode;

  /**
   * Constructor.
   *
   * @param transformer Non-null transformer instance
   * @param handler The Result tree handler to use
   */
  public TreeWalker2Result(TransformerImpl transformer,
                           ResultTreeHandler handler)
  {

    super(handler, transformer.getXPathContext().getDOMHelper());

    m_transformer = transformer;
    m_handler = handler;
  }

  /**
   * Perform a pre-order traversal non-recursive style.
   *
   * @param pos Start node for traversal
   *
   * @throws TransformerException
   */
  public void traverse(Node pos) throws org.xml.sax.SAXException
  {

    m_startNode = pos;

    super.traverse(pos);
  }

  /**
   * Start traversal of the tree at the given node
   *
   *
   * @param node Starting node for traversal
   *
   * @throws TransformerException
   */
  protected void startNode(Node node) throws org.xml.sax.SAXException
  {

    try
    {
      if ((Node.ELEMENT_NODE == node.getNodeType()) && (m_startNode == node))
      {
        DOMHelper dhelper = m_transformer.getXPathContext().getDOMHelper();
        String elemName = node.getNodeName();
        String localName = dhelper.getLocalNameOfNode(node);
        String namespace = dhelper.getNamespaceOfNode(node);

        m_handler.startElement(namespace, localName, elemName, null);

        for (Node parent = node; parent != null;
             parent = parent.getParentNode())
        {
          if (Node.ELEMENT_NODE != parent.getNodeType())
            continue;

          NamedNodeMap atts = ((Element) parent).getAttributes();
          int n = atts.getLength();

          for (int i = 0; i < n; i++)
          {
            String nsDeclPrefix = null;
            Attr attr = (Attr) atts.item(i);
            String name = attr.getName();
            String value = attr.getValue();

            if (name.startsWith("xmlns:"))
            {

              // get the namespace prefix 
              nsDeclPrefix = name.substring(name.indexOf(":") + 1);
            }
            else if (name.equals("xmlns"))
            {
              nsDeclPrefix = "";
            }

            if ((nsDeclPrefix == null) && (node != parent))
              continue;

            /*
            else if(nsDeclPrefix != null)
            {
            String desturi = m_processor.getURI(nsDeclPrefix);
            // Look for an alias for this URI. If one is found, use it as the result URI
            String aliasURI = m_elem.m_stylesheet.lookForAlias(value);
            if(aliasURI.equals(desturi)) // TODO: Check for extension namespaces
            {
            continue;
            }
            }
            */
            m_handler.addAttribute(dhelper.getNamespaceOfNode(attr),
                                   dhelper.getLocalNameOfNode(attr), name,
                                   "CDATA", value);

            // Make sure namespace is not in the excluded list then
            // add to result tree

            /*
            if(!m_handler.getPendingAttributes().contains(name))
            {
            if(nsDeclPrefix == null)
            {
            m_handler.addAttribute(name, "CDATA", value);
            }
            else
            {
            String desturi
            = m_handler.getURI(nsDeclPrefix);
            if(null == desturi)
            {
            m_handler.addAttribute(name, "CDATA", value);
            }
            else if(!desturi.equals(value))
            {
            m_handler.addAttribute(name, "CDATA", value);
            }
            }
            }
            */
          }
        }

        // m_handler.processResultNS(m_elem);           
      }
      else
      {
        super.startNode(node);
      }
    }
    catch(javax.xml.transform.TransformerException te)
    {
      throw new org.xml.sax.SAXException(te);
    }
  }
}
