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
package org.apache.xalan.extensions;

import org.w3c.dom.Node;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.traversal.NodeIterator;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.transformer.ResultTreeHandler;
import org.apache.xalan.templates.Stylesheet;
import org.apache.xalan.utils.QName;
import org.xml.sax.SAXException;
import org.apache.xalan.xpath.XObject;
import org.apache.xalan.xpath.XString;
import org.apache.xalan.xpath.XBoolean;
import org.apache.xalan.xpath.XNumber;
import org.apache.xalan.xpath.XRTreeFrag;
import org.apache.xalan.xpath.XNodeSet;
import org.apache.xalan.xpath.XPathContext;

// import org.apache.xalan.xslt.*;

/**
 * <meta name="usage" content="general"/>
 * Provides transformer context to be passed to an extension element.
 *
 * @author Sanjiva Weerawarana (sanjiva@watson.ibm.com)
 */
public class XSLProcessorContext {
  
  /**
   * Create a processor context to be passed to an extension.
   * (Notice it is a package-only constructor).
   */
  XSLProcessorContext (TransformerImpl transformer,
                       Stylesheet stylesheetTree,
                       Node sourceTree, Node sourceNode, QName mode) 
  {
    this.transformer = transformer;
    this.stylesheetTree = stylesheetTree;
    this.mode = mode;
    this.sourceTree = sourceTree;
    this.sourceNode = sourceNode;
  }
  
  private TransformerImpl transformer;
  
  /**
   * Get the transformer.
   */
  public TransformerImpl getTransformer()
  {
    return transformer;
  }
  
  private Stylesheet stylesheetTree;
  
  /**
   * Get the Stylesheet being executed.
   */
  public Stylesheet getStylesheet()
  {
    return stylesheetTree;
  }
  
  private Node sourceTree;
  
  /**
   * Get the root of the source tree being executed.
   */
  public Node getSourceTree()
  {
    return sourceTree;
  }
  
  private Node sourceNode;
  
  /**
   * Get the current context node.
   */
  public Node getContextNode()
  {
    return sourceNode;
  }
  
  private QName mode;
  
  /**
   * Get the current mode being executed.
   */
  public QName getMode()
  {
    return mode;
  }

  /**
   * Output an object to the result tree by doing the right conversions.
   * This is public for access by extensions.
   *
   * @param obj the Java object to output. If its of an X<something> type
   *        then that conversion is done first and then sent out.
   */
  public void outputToResultTree (Stylesheet stylesheetTree, Object obj)
    throws SAXException,
    java.net.MalformedURLException,
    java.io.FileNotFoundException,
    java.io.IOException
  {
    ResultTreeHandler rtreeHandler = transformer.getResultTreeHandler();
    
    XObject value;
    // Make the return object into an XObject because it
    // will be easier below.  One of the reasons to do this
    // is to keep all the conversion functionality in the
    // XObject classes.
    if(obj instanceof XObject)
    {
      value = (XObject)obj;
    }
    else if(obj instanceof String)
    {
      value = new XString((String)obj);
    }
    else if(obj instanceof Boolean)
    {
      value = new XBoolean(((Boolean)obj).booleanValue());
    }
    else if(obj instanceof Double)
    {
      value = new XNumber(((Double)obj).doubleValue());
    }
    else if(obj instanceof DocumentFragment)
    {
      value = new XRTreeFrag((DocumentFragment)obj);
    }
    else if(obj instanceof Node)
    {
      value = new XNodeSet((Node)obj);
    }
    else if(obj instanceof NodeIterator)
    {
      value = new XNodeSet((NodeIterator)obj);
    }
    else
    {
      value = new XString(obj.toString());
    }

    int type = value.getType();
    String s;
    switch(type)
    {
    case XObject.CLASS_BOOLEAN:        case XObject.CLASS_NUMBER:        case XObject.CLASS_STRING:
      s = value.str();
      rtreeHandler.characters(s.toCharArray(), 0, s.length());
      break;
    case XObject.CLASS_NODESET:          // System.out.println(value);
      NodeIterator nl = value.nodeset();
      Node pos;
      while(null != (pos = nl.nextNode()))
      {
        Node top = pos;
        while(null != pos)
        {
          rtreeHandler.flushPending();
          rtreeHandler.cloneToResultTree(stylesheetTree, pos, false, false,
                            true);
          Node nextNode = pos.getFirstChild();
          while(null == nextNode)
          {
            if(Node.ELEMENT_NODE == pos.getNodeType())
            {
              rtreeHandler.endElement("", "", pos.getNodeName());
            }
            if(top == pos)
              break;
            nextNode = pos.getNextSibling();
            if(null == nextNode)
            {
              pos = pos.getParentNode();
              if(top == pos)
              {
                if(Node.ELEMENT_NODE == pos.getNodeType())
                {
                  rtreeHandler.endElement("", "", pos.getNodeName());
                }
                nextNode = null;
                break;
              }
            }
          }
          pos = nextNode;
        }
      }
      break;

    case XObject.CLASS_RTREEFRAG:
      rtreeHandler.outputResultTreeFragment(value, 
                                            transformer.getXPathContext());
      break;
    }
  }


  /**
   * I need a "Node transformNode (Node)" method somewhere that the
   * user can call to process the transformation of a node but not
   * serialize out automatically. ????????????????
   *
   * Does ElemTemplateElement.executeChildTemplates() cut it? It sends
   * results out to the stream directly, so that could be a problem.
   */
}
