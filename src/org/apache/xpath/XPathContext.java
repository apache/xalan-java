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
package org.apache.xpath;

// Java lib imports
import java.io.File;
import java.io.IOException;

import java.util.Stack;

// Xalan imports
import org.apache.xalan.utils.IntStack;
import org.apache.xalan.utils.NSInfo;
import org.apache.xalan.utils.PrefixResolver;
import org.apache.xalan.utils.QName;
import org.apache.xalan.utils.NodeVector;
import org.apache.xalan.res.XSLMessages;
import org.apache.xpath.res.XPATHErrorResources;
import org.apache.xpath.axes.ContextNodeList;
import org.apache.xpath.axes.SubContextList;

import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XNodeSet;


// DOM Imports
import org.w3c.dom.traversal.NodeIterator;
import org.w3c.dom.traversal.TreeWalker;
import org.w3c.dom.Node;

import org.apache.xalan.extensions.ExpressionContext;

// SAX2 imports
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.Locator;

// TRaX imports
import org.apache.trax.URIResolver;
import org.apache.trax.TransformException;

// Temporary!!!
import org.apache.xalan.extensions.ExtensionsTable;

/**
 * <meta name="usage" content="advanced"/>
 * Default class for the runtime execution context for XPath.
 */
public class XPathContext implements ExpressionContext
{
  /**
   * Create an XPathContext instance.
   */
  public XPathContext()
  {
  }

  /**
   * Create an XPathContext instance.
   * @param owner Value that can be retreaved via the getOwnerObject() method.
   * @see getOwnerObject
   */
  public XPathContext(Object owner)
  {
    m_owner = owner;
  }

  /**
   * Copy attributes from another liaison.
   */
  public void copyFromOtherLiaison(XPathContext from)
    throws SAXException
  {
  }

  /**
   * Reset for new run.
   */
  public void reset()
  {
  }
  
  Locator m_saxLocation;
  
  public void setSAXLocator(Locator location)
  {
    m_saxLocation = location;
  }
  
  public Locator getSAXLocator()
  {
    return m_saxLocation;
  }
  
  private Object m_owner;
  
  /**
   * Get the "owner" context of this context, which should be, 
   * in the case of XSLT, the Transformer object.  This is needed 
   * so that XSLT functions can get the Transformer.
   * @return The owner object passed into the constructor, or null.
   */
  public Object getOwnerObject()
  {
    return m_owner;
  }

  // ================ extensionsTable ===================
  
  /**
   * The table of Extension Handlers.
   */
  private ExtensionsTable m_extensionsTable = new ExtensionsTable();
  
  /**
   * Get the extensions table object.
   */
  public ExtensionsTable getExtensionsTable()
  {
    return m_extensionsTable;
  }
  
  void setExtensionsTable(ExtensionsTable table)
  {
    m_extensionsTable = table;
  }
  
  // ================ VarStack ===================

  /**
   * The stack of Variable stacks.  A VariableStack will be
   * pushed onto this stack for each template invocation.
   */
  private VariableStack m_variableStacks = new VariableStack();
  
  /**
   * Get the variable stack, which is in charge of variables and
   * parameters.
   */
  public VariableStack getVarStack() { return m_variableStacks; }

  /**
   * Get the variable stack, which is in charge of variables and
   * parameters.
   */
  public void setVarStack(VariableStack varStack) { m_variableStacks = varStack; }

  /**
   * Given a name, locate a variable in the current context, and return
   * the Object.
   */
  public XObject getVariable(QName qname)
    throws org.xml.sax.SAXException
  {
    Object obj = getVarStack().getVariable(qname);
    if((null != obj) && !(obj instanceof XObject))
    {
      obj = new XObject(obj);
    }
    return (XObject)obj;
  }
  
  
  // ================ DOMHelper ===================

  private DOMHelper m_domHelper;
  
  /**
   * Get the DOMHelper associated with this execution context.
   */
  public final DOMHelper getDOMHelper()
  {
    if(null == m_domHelper)
      m_domHelper = new DOM2Helper();
    return m_domHelper;
  }
  
  /**
   * Set the DOMHelper associated with this execution context.
   */
  public void setDOMHelper(DOMHelper helper)
  {
    m_domHelper = helper;
  }
                                                
  // ================ SourceTreeManager ===================

  private SourceTreeManager m_sourceTreeManager = new SourceTreeManager();
  
  /**
   * Get the DOMHelper associated with this execution context.
   */
  public final SourceTreeManager getSourceTreeManager()
  {
    return m_sourceTreeManager;
  }
  
  /**
   * Set the DOMHelper associated with this execution context.
   */
  public void setSourceTreeManager(SourceTreeManager mgr)
  {
    m_sourceTreeManager = mgr;
  }
  
  // =================================================

  private URIResolver m_uriResolver;
  
  /**
   * Get the URIResolver associated with this execution context.
   */
  public final URIResolver getURIResolver()
  {
    return m_uriResolver;
  }
  
  /**
   * Set the URIResolver associated with this execution context.
   */
  public void setURIResolver(URIResolver resolver)
  {
    m_uriResolver = resolver;
  }
  
  // =================================================
   
  public XMLReader m_primaryReader;
  
  /**
   * Get primary XMLReader associated with this execution context.
   */
  public final XMLReader getPrimaryReader()
  {
    return m_primaryReader;
  }
  
  /**
   * Set primary XMLReader associated with this execution context.
   */
  public void setPrimaryReader(XMLReader reader)
  {
    m_primaryReader = reader;
  }

  // =================================================
  
  /**
   * Take a user string (system ID) return the url.
   * @exception XSLProcessorException thrown if the active ProblemListener and XPathContext decide 
   * the error condition is severe enough to halt processing.
   */
  public final String getAbsoluteURI(String urlString, String base)
    throws SAXException 
  {
    InputSource inputSource;
    try
    {
      inputSource = getSourceTreeManager().resolveURI(base, urlString, getSAXLocator());
    }
    catch(IOException ioe)
    {
      inputSource = null; // shutup compiler.
      throw new SAXException(ioe);
    }
    // System.out.println("url: "+url.toString());
    return inputSource.getSystemId();
  }
  
  private static XSLMessages m_XSLMessages = new XSLMessages();

  /**
   * Tell the user of an assertion error, and probably throw an 
   * exception.
   */
  private void assert(boolean b, String msg)
    throws org.xml.sax.SAXException
  {
    ErrorHandler errorHandler = getPrimaryReader().getErrorHandler();
    if (errorHandler != null) {
      errorHandler.fatalError(new TransformException(m_XSLMessages.createMessage(XPATHErrorResources.ER_INCORRECT_PROGRAMMER_ASSERTION, new Object[] {msg})));
    }
  }
  
  //==========================================================
  // SECTION: Execution context state tracking
  //==========================================================
           
  /**
   * The current context node list.
   */
  private Stack m_contextNodeLists = new Stack();
        
  /**
   * Get the current context node list.
   */
  public final ContextNodeList getContextNodeList()
  {
    if (m_contextNodeLists.size()>0)
      return (ContextNodeList)m_contextNodeLists.peek();
    else 
      return null;
  }
 
  /**
   * <meta name="usage" content="internal"/>
   * Set the current context node list.
   * @param A nodelist that represents the current context 
   * list as defined by XPath.
   */
  public final void pushContextNodeList(ContextNodeList nl)
  {
    m_contextNodeLists.push(nl);
  }

  /**
   * <meta name="usage" content="internal"/>
   * Pop the current context node list.
   */
  public final void popContextNodeList()
  {
    m_contextNodeLists.pop();
  }
  
  /**
   * The current prefixResolver for the execution context (not
   * the source tree context).
   * (Is this really needed?)
   */
  private PrefixResolver m_currentPrefixResolver = null;
  
  private NodeVector m_currentNodes = new NodeVector();
  
  /**
   * Get the current context node.
   */
  public final Node getCurrentNode()
  {
    return m_currentNodes.peepOrNull();
  }

  /**
   * Set the current context node.
   */
  public final void pushCurrentNode(Node n)
  {
    m_currentNodes.push(n);
  }
  
  /**
   * Pop the current context node.
   */
  public final void popCurrentNode()
  {
    m_currentNodes.popQuick();
  }
  
  private Stack m_currentExpressionNodes = new Stack();
  
  /**
   * Get the current node that is the expression's context (i.e. for current() support).
   */
  public final Node getCurrentExpressionNode()
  {
    try
    {
      return (Node)m_currentExpressionNodes.peek();
    }
    catch(java.util.EmptyStackException ese)
    {
    }
    return null;
  }

  /**
   * Set the current node that is the expression's context (i.e. for current() support).
   */
  public final void pushCurrentExpressionNode(Node n)
  {
    m_currentExpressionNodes.push(n);
  }
  
  /**
   * Pop the current node that is the expression's context (i.e. for current() support).
   */
  public final void popCurrentExpressionNode()
  {
    m_currentExpressionNodes.pop();
  }

  
  /**
   * Get the current namespace context for the xpath.
   */
  public final PrefixResolver getNamespaceContext()
  {
    return m_currentPrefixResolver;
  }

  /**
   * Get the current namespace context for the xpath.
   */
  public final void setNamespaceContext(PrefixResolver pr)
  {
    m_currentPrefixResolver = pr;
  } 
  
  //==========================================================
  // SECTION: Current TreeWalker contexts (for internal use)
  //==========================================================

  /**
   * Stack of AxesIterators.
   */
  private Stack m_axesIteratorStack = new Stack();
  
  /**
   * <meta name="usage" content="internal"/>
   * Push a TreeWalker on the stack.
   */
  public final void pushSubContextList(SubContextList iter)
  {
    m_axesIteratorStack.push(iter);
  }
  
  /**
   * <meta name="usage" content="internal"/>
   * Pop the last pushed axes iterator.
   */
  public final void popSubContextList()
  {
    m_axesIteratorStack.pop();
  }
  
  /**
   * <meta name="usage" content="internal"/>
   * Get the current axes iterator, or return null if none.
   */
  public SubContextList getSubContextList()
  {
    return m_axesIteratorStack.isEmpty() 
           ? null : (SubContextList)m_axesIteratorStack.peek();
                                           
  }

  //==========================================================
  // SECTION: Implementation of ExpressionContext interface
  //==========================================================

  /**
   * Get the current context node.
   * @return The current context node.
   */
  public final Node getContextNode()
  {
    return this.getCurrentNode();
  }
  
  /**
   * Get the current context node list.
   * @return An iterator for the current context list, as 
   * defined in XSLT.
   */
  public final NodeIterator getContextNodes()
  {
    try
    {
    ContextNodeList cnl = getContextNodeList();
    if(null != cnl)
      return cnl.cloneWithReset();
    else
      return null; // for now... this might ought to be an empty iterator.
    }
    catch(CloneNotSupportedException cnse)
    {
      return null; // error reporting?
    }
  }

  /**
   * Get the value of a node as a number.
   * @param n Node to be converted to a number.  May be null.
   * @return value of n as a number.
   */
  public final double toNumber(Node n)
  {
    return XNodeSet.getNumberFromNode(n);
  }

  /**
   * Get the value of a node as a string.
   * @param n Node to be converted to a string.  May be null.
   * @return value of n as a string, or an empty string if n is null.
   */
  public final String toString(Node n)
  {
    return XNodeSet.getStringFromNode(n);
  }


}
