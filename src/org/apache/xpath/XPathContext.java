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
import org.apache.xml.utils.IntStack;
import org.apache.xml.utils.NSInfo;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xml.utils.QName;
import org.apache.xml.utils.NodeVector;
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
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
// import org.xml.sax.Locator;

// TRaX imports
import javax.xml.transform.URIResolver;
import javax.xml.transform.TransformerException;
import org.apache.xml.utils.SAXSourceLocator;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.dom.DOMSource;

// Temporary!!!
import org.apache.xalan.extensions.ExtensionsTable;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.Source;
import javax.xml.transform.ErrorListener;

/**
 * <meta name="usage" content="advanced"/>
 * Default class for the runtime execution context for XPath.
 */
public class XPathContext implements ExpressionContext
{

  /**
   * Create an XPathContext instance.
   */
  public XPathContext(){}

  /**
   * Create an XPathContext instance.
   * @param owner Value that can be retrieved via the getOwnerObject() method.
   * @see getOwnerObject
   */
  public XPathContext(Object owner)
  {
    m_owner = owner;
  }

  /**
   * Copy attributes from another liaison.
   *
   * NEEDSDOC @param from
   *
   * @throws TransformerException
   */
  public void copyFromOtherLiaison(XPathContext from) throws TransformerException{}

  /**
   * Reset for new run.
   */
  public void reset(){}

  /** NEEDSDOC Field m_saxLocation          */
  SourceLocator m_saxLocation;

  /**
   * NEEDSDOC Method setSAXLocator 
   *
   *
   * NEEDSDOC @param location
   */
  public void setSAXLocator(SourceLocator location)
  {
    m_saxLocation = location;
  }

  /**
   * NEEDSDOC Method getSAXLocator 
   *
   *
   * NEEDSDOC (getSAXLocator) @return
   */
  public SourceLocator getSAXLocator()
  {
    return m_saxLocation;
  }

  /** NEEDSDOC Field m_owner          */
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
   *
   * NEEDSDOC ($objectName$) @return
   */
  public ExtensionsTable getExtensionsTable()
  {
    return m_extensionsTable;
  }

  /**
   * NEEDSDOC Method setExtensionsTable 
   *
   *
   * NEEDSDOC @param table
   */
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
   *
   * NEEDSDOC ($objectName$) @return
   */
  public VariableStack getVarStack()
  {
    return m_variableStacks;
  }

  /**
   * Get the variable stack, which is in charge of variables and
   * parameters.
   *
   * NEEDSDOC @param varStack
   */
  public void setVarStack(VariableStack varStack)
  {
    m_variableStacks = varStack;
  }

  /**
   * Given a name, locate a variable in the current context, and return
   * the Object.
   *
   * NEEDSDOC @param qname
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject getVariable(QName qname) throws javax.xml.transform.TransformerException
  {

    Object obj = getVarStack().getVariable(this, qname);

    if ((null != obj) &&!(obj instanceof XObject))
    {
      obj = new XObject(obj);
    }

    return (XObject) obj;
  }

  // ================ DOMHelper ===================

  /** NEEDSDOC Field m_domHelper          */
  private DOMHelper m_domHelper;

  /**
   * Get the DOMHelper associated with this execution context.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public final DOMHelper getDOMHelper()
  {

    if (null == m_domHelper)
      m_domHelper = new DOM2Helper();

    return m_domHelper;
  }

  /**
   * Set the DOMHelper associated with this execution context.
   *
   * NEEDSDOC @param helper
   */
  public void setDOMHelper(DOMHelper helper)
  {
    m_domHelper = helper;
  }

  // ================ SourceTreeManager ===================

  /** NEEDSDOC Field m_sourceTreeManager          */
  private SourceTreeManager m_sourceTreeManager = new SourceTreeManager();

  /**
   * Get the DOMHelper associated with this execution context.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public final SourceTreeManager getSourceTreeManager()
  {
    return m_sourceTreeManager;
  }

  /**
   * Set the DOMHelper associated with this execution context.
   *
   * NEEDSDOC @param mgr
   */
  public void setSourceTreeManager(SourceTreeManager mgr)
  {
    m_sourceTreeManager = mgr;
  }
  
  // =================================================

  /** The ErrorListener where errors and warnings are to be reported.   */
  private ErrorListener m_errorListener;

  /**
   * Get the ErrorListener where errors and warnings are to be reported.
   *
   * @return A non-null ErrorListener reference.
   */
  public final ErrorListener getErrorListener()
  {
    return m_errorListener;
  }

  /**
   * Set the ErrorListener where errors and warnings are to be reported.
   *
   * @param listener A non-null ErrorListener reference.
   */
  public void setErrorListener(ErrorListener listener)
  {
    m_errorListener = listener;
  }


  // =================================================

  /** NEEDSDOC Field m_uriResolver          */
  private URIResolver m_uriResolver;

  /**
   * Get the URIResolver associated with this execution context.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public final URIResolver getURIResolver()
  {
    return m_uriResolver;
  }

  /**
   * Set the URIResolver associated with this execution context.
   *
   * NEEDSDOC @param resolver
   */
  public void setURIResolver(URIResolver resolver)
  {
    m_uriResolver = resolver;
  }

  // =================================================

  /** NEEDSDOC Field m_primaryReader          */
  public XMLReader m_primaryReader;

  /**
   * Get primary XMLReader associated with this execution context.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public final XMLReader getPrimaryReader()
  {
    return m_primaryReader;
  }

  /**
   * Set primary XMLReader associated with this execution context.
   *
   * NEEDSDOC @param reader
   */
  public void setPrimaryReader(XMLReader reader)
  {
    m_primaryReader = reader;
  }

  // =================================================

  /**
   * Take a user string (system ID) return the url.
   *
   * NEEDSDOC @param urlString
   * NEEDSDOC @param base
   *
   * NEEDSDOC ($objectName$) @return
   * @exception XSLProcessorException thrown if the active ProblemListener and XPathContext decide
   * the error condition is severe enough to halt processing.
   *
   * @throws TransformerException
   */
  public final String getAbsoluteURI(String urlString, String base)
          throws TransformerException
  {
    try
    {
      Source source = getSourceTreeManager().resolveURI(base, urlString,
                                                        getSAXLocator());
      return source.getSystemId();
    }
    catch (TransformerException te)
    {
      throw new TransformerException(te);
    }
    catch (IOException ioe)
    {
      throw new TransformerException(ioe);
    }
  }

  /** NEEDSDOC Field m_XSLMessages          */
  private static XSLMessages m_XSLMessages = new XSLMessages();

  /**
   * Tell the user of an assertion error, and probably throw an
   * exception.
   *
   * NEEDSDOC @param b
   * NEEDSDOC @param msg
   *
   * @throws javax.xml.transform.TransformerException
   */
  private void assert(boolean b, String msg) throws javax.xml.transform.TransformerException
  {

    ErrorListener errorHandler = getErrorListener();

    if (errorHandler != null)
    {
      errorHandler.fatalError(
        new TransformerException(
          m_XSLMessages.createMessage(
            XPATHErrorResources.ER_INCORRECT_PROGRAMMER_ASSERTION,
            new Object[]{ msg }), (SAXSourceLocator)this.getSAXLocator()));
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
   *
   * NEEDSDOC ($objectName$) @return
   */
  public final ContextNodeList getContextNodeList()
  {

    if (m_contextNodeLists.size() > 0)
      return (ContextNodeList) m_contextNodeLists.peek();
    else
      return null;
  }

  /**
   * <meta name="usage" content="internal"/>
   * Set the current context node list.
   * @param A nodelist that represents the current context
   * list as defined by XPath.
   *
   * NEEDSDOC @param nl
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
  PrefixResolver m_currentPrefixResolver = null;

  /** NEEDSDOC Field m_currentNodes          */
  private NodeVector m_currentNodes = new NodeVector();

  /**
   * Get the current context node.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public final Node getCurrentNode()
  {
    return m_currentNodes.peepOrNull();
  }

  /**
   * Set the current context node.
   *
   * NEEDSDOC @param cn
   * NEEDSDOC @param en
   */
  public final void pushCurrentNodeAndExpression(Node cn, Node en)
  {
    m_currentNodes.push(cn);
    m_currentExpressionNodes.push(en);
  }

  /**
   * Set the current context node.
   */
  public final void popCurrentNodeAndExpression()
  {
    m_currentNodes.popQuick();
    m_currentExpressionNodes.popQuick();
  }

  /**
   * Set the current context node.
   *
   * NEEDSDOC @param n
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

  /** NEEDSDOC Field m_currentExpressionNodes          */
  private NodeVector m_currentExpressionNodes = new NodeVector();

  /**
   * Get the current node that is the expression's context (i.e. for current() support).
   *
   * NEEDSDOC ($objectName$) @return
   */
  public final Node getCurrentExpressionNode()
  {
    return m_currentExpressionNodes.peepOrNull();
  }

  /**
   * Set the current node that is the expression's context (i.e. for current() support).
   *
   * NEEDSDOC @param n
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
    m_currentExpressionNodes.popQuick();
  }

  /**
   * Get the current namespace context for the xpath.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public final PrefixResolver getNamespaceContext()
  {
    return m_currentPrefixResolver;
  }

  /**
   * Get the current namespace context for the xpath.
   *
   * NEEDSDOC @param pr
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
   *
   * NEEDSDOC @param iter
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
   *
   * NEEDSDOC ($objectName$) @return
   */
  public SubContextList getSubContextList()
  {
    return m_axesIteratorStack.isEmpty()
           ? null : (SubContextList) m_axesIteratorStack.peek();
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

      if (null != cnl)
        return cnl.cloneWithReset();
      else
        return null;  // for now... this might ought to be an empty iterator.
    }
    catch (CloneNotSupportedException cnse)
    {
      return null;  // error reporting?
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
