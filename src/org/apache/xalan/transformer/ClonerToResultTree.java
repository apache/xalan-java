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

import org.apache.xalan.templates.Stylesheet;

import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.CDATASection;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.EntityReference;

import javax.xml.transform.TransformerException;
import org.xml.sax.Attributes;

import org.apache.xpath.XPathContext;
import org.apache.xpath.DOMHelper;
import org.apache.xalan.res.XSLTErrorResources;

/**
 * <meta name="usage" content="internal"/>
 * Class used to clone a node, possibly including its children to 
 * a result tree.
 */
public class ClonerToResultTree
{

  /** Result tree handler for the cloned tree           */
  private ResultTreeHandler m_rth;

  /** Transformer instance to use for cloning          */
  private TransformerImpl m_transformer;

  /**
   * Constructor ClonerToResultTree
   *
   *
   * @param transformer non-null transformer instance to use for the cloning
   * @param rth non-null result tree handler for the cloned tree
   */
  public ClonerToResultTree(TransformerImpl transformer,
                            ResultTreeHandler rth)
  {
    m_rth = rth;
    m_transformer = transformer;
  }

  /**
   * Clone an element with or without children.
   * TODO: Fix or figure out node clone failure!
   * the error condition is severe enough to halt processing.
   *
   * @param node The node to clone
   * @param shouldCloneAttributes Flag indicating whether to 
   * clone children attributes
   * 
   * @throws TransformerException
   */
  public void cloneToResultTree(Node node, boolean shouldCloneAttributes)
    throws TransformerException
  {

    try
    {
      boolean stripWhiteSpace = false;
      XPathContext xctxt = m_transformer.getXPathContext();
      DOMHelper dhelper = xctxt.getDOMHelper();

      switch (node.getNodeType())
      {
      case Node.TEXT_NODE :
        {
          Text tx = (Text) node;
          String data = null;

          // System.out.println("stripWhiteSpace = "+stripWhiteSpace+", "+tx.getData());
          if (stripWhiteSpace)
          {
            if (!dhelper.isIgnorableWhitespace(tx))
            {
              data = tx.getData();

              if ((null != data) && (0 == data.trim().length()))
              {
                data = null;
              }
            }
          }
          else
          {
            Node parent = node.getParentNode();

            if (null != parent)
            {
              if (Node.DOCUMENT_NODE != parent.getNodeType())
              {
                data = tx.getData();

                if ((null != data) && (0 == data.length()))
                {
                  data = null;
                }
              }
            }
            else
            {
              data = tx.getData();

              if ((null != data) && (0 == data.length()))
              {
                data = null;
              }
            }
          }

          if (null != data)
          {

            // TODO: Hack around the issue of comments next to literals.
            // This would be, when a comment is present, the whitespace
            // after the comment must be added to the literal.  The
            // parser should do this, but XML4J doesn't seem to.
            // <foo>some lit text
            //     <!-- comment -->
            //     </foo>
            // Loop through next siblings while they are comments, then,
            // if the node after that is a ignorable text node, append
            // it to the text node just added.
            if (dhelper.isIgnorableWhitespace(tx))
            {
              m_rth.ignorableWhitespace(data.toCharArray(), 0, data.length());
            }
            else
            {
              m_rth.characters(data.toCharArray(), 0, data.length());
            }
          }
        }
        break;
      case Node.DOCUMENT_FRAGMENT_NODE :
      case Node.DOCUMENT_NODE :

        // Can't clone a document, but refrain from throwing an error
        // so that copy-of will work
        break;
      case Node.ELEMENT_NODE :
        {
          Attributes atts;

          if (shouldCloneAttributes)
          {
            m_rth.addAttributes(node);
            m_rth.processNSDecls(node);
          }

          String ns = dhelper.getNamespaceOfNode(node);
          String localName = dhelper.getLocalNameOfNode(node);

          m_rth.startElement(ns, localName, node.getNodeName(), null);
        }
        break;
      case Node.CDATA_SECTION_NODE :
        {
          m_rth.startCDATA();

          String data = ((CDATASection) node).getData();

          m_rth.characters(data.toCharArray(), 0, data.length());
          m_rth.endCDATA();
        }
        break;
      case Node.ATTRIBUTE_NODE :
        {
          if (m_rth.isDefinedNSDecl((Attr) node))
            break;

          String ns = dhelper.getNamespaceOfNode(node);
          String localName = dhelper.getLocalNameOfNode(node);

          m_rth.addAttribute(ns, localName, node.getNodeName(), "CDATA",
                             ((Attr) node).getValue());
        }
        break;
      case Node.COMMENT_NODE :
        {
          m_rth.comment(((Comment) node).getData());
        }
        break;
      case Node.ENTITY_REFERENCE_NODE :
        {
          EntityReference er = (EntityReference) node;

          m_rth.entityReference(er.getNodeName());
        }
        break;
      case Node.PROCESSING_INSTRUCTION_NODE :
        {
          ProcessingInstruction pi = (ProcessingInstruction) node;

          m_rth.processingInstruction(pi.getTarget(), pi.getData());
        }
        break;
      default :
        m_transformer.getMsgMgr().error(null, XSLTErrorResources.ER_CANT_CREATE_ITEM,
                                        new Object[]{ node.getNodeName() });  //"Can not create item in result tree: "+node.getNodeName());
      }
    }
    catch(org.xml.sax.SAXException se)
    {
      throw new TransformerException(se);
    }
  }  // end cloneToResultTree function
}
