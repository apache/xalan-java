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

import java.lang.reflect.Method;

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
import org.apache.xpath.objects.XString;

// DOM Imports
//import org.w3c.dom.traversal.NodeIterator;
//import org.w3c.dom.traversal.TreeWalker;
//import org.w3c.dom.Node;

import org.apache.xalan.extensions.ExpressionContext;

// SAX2 imports
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
// import org.xml.sax.Locator;

// TRaX imports
import javax.xml.transform.URIResolver;
import javax.xml.transform.TransformerException;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.dom.DOMSource;

// Temporary!!!
import org.apache.xalan.extensions.ExtensionsTable;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.Source;
import javax.xml.transform.ErrorListener;

import org.apache.xml.dtm.DTMManager;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.dtm.DTMFilter;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMWSFilter;

// Utility imports.
import org.apache.xml.utils.SAXSourceLocator;
import org.apache.xml.utils.XMLString;
import org.apache.xml.utils.XMLStringFactory;

import org.apache.xpath.axes.DescendantIterator;

/**
 * <meta name="usage" content="advanced"/>
 * Default class for the runtime execution context for XPath.
 * 
 * <p>This class extends DTMManager but does not directly implement it.</p>
 */
public class XPathContext extends DTMManager // implements ExpressionContext
{
  /**
   * Though XPathContext context extends 
   * the DTMManager, it really is a proxy for this object, which 
   * is the real DTMManager.
   */
  private DTMManager m_dtmManager = DTMManager.newInstance(
                   org.apache.xpath.objects.XMLStringFactoryImpl.getFactory());
  
  /**
   * Return the DTMManager object.  Though XPathContext context extends 
   * the DTMManager, it really is a proxy for the real DTMManager.  If a 
   * caller needs to make a lot of calls to the DTMManager, it is faster 
   * if it gets the real one from this function.
   */
   public DTMManager getDTMManager()
   {
     return m_dtmManager;
   }
  
  /**
   * Get an instance of a DTM, loaded with the content from the
   * specified source.  If the unique flag is true, a new instance will
   * always be returned.  Otherwise it is up to the DTMManager to return a
   * new instance or an instance that it already created and may be being used
   * by someone else.
   * (I think more parameters will need to be added for error handling, and entity
   * resolution).
   *
   * @param source the specification of the source object, which may be null, 
   *               in which case it is assumed that node construction will take 
   *               by some other means.
   * @param unique true if the returned DTM must be unique, probably because it
   * is going to be mutated.
   * @param whiteSpaceFilter Enables filtering of whitespace nodes, and may 
   *                         be null.
   * @param incremental true if the construction should try and be incremental.
   *
   * @return a non-null DTM reference.
   */
  public DTM getDTM(javax.xml.transform.Source source, boolean unique, 
                    DTMWSFilter wsfilter,
                    boolean incremental)
  {
    return m_dtmManager.getDTM(source, unique, wsfilter, 
                               incremental);
  }
                             
  /**
   * Get an instance of a DTM that "owns" a node handle.
   *
   * @param nodeHandle the nodeHandle.
   *
   * @return a non-null DTM reference.
   */
  public DTM getDTM(int nodeHandle)
  {
    return m_dtmManager.getDTM(nodeHandle);
  }
  
  /**
   * Given a W3C DOM node, try and return a DTM handle.
   * Note: calling this may be non-optimal.
   * 
   * @param node Non-null reference to a DOM node.
   * 
   * @return a valid DTM handle.
   */
  public int getDTMHandleFromNode(org.w3c.dom.Node node)
  {
    return m_dtmManager.getDTMHandleFromNode(node);
  }

  
  /**
   * %TBD% Doc
   */
  public int getDTMIdentity(DTM dtm)
  {
    return m_dtmManager.getDTMIdentity(dtm);
  }
  
  /**
   * Creates an empty <code>DocumentFragment</code> object. 
   * @return A new <code>DocumentFragment handle</code>.
   */
  public DTM createDocumentFragment()
  {
    return m_dtmManager.createDocumentFragment();
  }
  
  /**
   * Release a DTM either to a lru pool, or completely remove reference.
   * DTMs without system IDs are always hard deleted.
   * State: experimental.
   * 
   * @param dtm The DTM to be released.
   * @param shouldHardDelete True if the DTM should be removed no matter what.
   * @return true if the DTM was removed, false if it was put back in a lru pool.
   */
  public boolean release(DTM dtm, boolean shouldHardDelete)
  {
    return m_dtmManager.release(dtm, shouldHardDelete);
  }

  /**
   * Create a new <code>DTMIterator</code> based on an XPath
   * <a href="http://www.w3.org/TR/xpath#NT-LocationPath>LocationPath</a> or
   * a <a href="http://www.w3.org/TR/xpath#NT-UnionExpr">UnionExpr</a>.
   *
   * @param xpathCompiler ??? Somehow we need to pass in a subpart of the
   * expression.  I hate to do this with strings, since the larger expression
   * has already been parsed.
   *
   * @param pos The position in the expression.
   * @return The newly created <code>DTMIterator</code>.
   */
  public DTMIterator createDTMIterator(Object xpathCompiler, int pos)
  {
    return m_dtmManager.createDTMIterator(xpathCompiler, pos);
  }

  /**
   * Create a new <code>DTMIterator</code> based on an XPath
   * <a href="http://www.w3.org/TR/xpath#NT-LocationPath>LocationPath</a> or
   * a <a href="http://www.w3.org/TR/xpath#NT-UnionExpr">UnionExpr</a>.
   *
   * @param xpathString Must be a valid string expressing a
   * <a href="http://www.w3.org/TR/xpath#NT-LocationPath>LocationPath</a> or
   * a <a href="http://www.w3.org/TR/xpath#NT-UnionExpr">UnionExpr</a>.
   *
   * @param presolver An object that can resolve prefixes to namespace URLs.
   *
   * @return The newly created <code>DTMIterator</code>.
   */
  public DTMIterator createDTMIterator(String xpathString,
          PrefixResolver presolver)
  {
    return m_dtmManager.createDTMIterator(xpathString, presolver);
  }

  /**
   * Create a new <code>DTMIterator</code> based only on a whatToShow and
   * a DTMFilter.  The traversal semantics are defined as the descendant
   * access.
   *
   * @param whatToShow This flag specifies which node types may appear in
   *   the logical view of the tree presented by the iterator. See the
   *   description of <code>NodeFilter</code> for the set of possible
   *   <code>SHOW_</code> values.These flags can be combined using
   *   <code>OR</code>.
   * @param filter The <code>NodeFilter</code> to be used with this
   *   <code>TreeWalker</code>, or <code>null</code> to indicate no filter.
   * @param entityReferenceExpansion The value of this flag determines
   *   whether entity reference nodes are expanded.
   *
   * @return The newly created <code>NodeIterator</code>.
   */
  public DTMIterator createDTMIterator(int whatToShow,
          DTMFilter filter, boolean entityReferenceExpansion)
  {
    return m_dtmManager.createDTMIterator(whatToShow, filter, entityReferenceExpansion);
  }
  
  /**
   * Create a new <code>DTMIterator</code> that holds exactly one node.
   *
   * @param node The node handle that the DTMIterator will iterate to.
   *
   * @return The newly created <code>DTMIterator</code>.
   */
  public DTMIterator createDTMIterator(int node)
  {
    DescendantIterator iter = new DescendantIterator();
    iter.initContext(this, node);
    return iter;
    // return m_dtmManager.createDTMIterator(node);
  }

  /**
   * Create an XPathContext instance.
   */
  public XPathContext()
  {
  }

  /**
   * Create an XPathContext instance.
   * @param owner Value that can be retrieved via the getOwnerObject() method.
   * @see #getOwnerObject
   */
  public XPathContext(Object owner)
  {
    m_owner = owner;
    try {
      m_ownerGetErrorListener = m_owner.getClass().getMethod("getErrorListener", new Class[] {});
    }
    catch (NoSuchMethodException nsme) {}
  }

  /**
   * Reset for new run.
   */
  public void reset(){}

  /** The current stylesheet locator. */
  SourceLocator m_saxLocation;

  /**
   * Set the current locater in the stylesheet.
   *
   *
   * @param location The location within the stylesheet.
   */
  public void setSAXLocator(SourceLocator location)
  {
    m_saxLocation = location;
  }

  /**
   * Get the current locater in the stylesheet.
   *
   *
   * @return The location within the stylesheet, or null if not known.
   */
  public SourceLocator getSAXLocator()
  {
    return m_saxLocation;
  }

  /** The owner context of this XPathContext.  In the case of XSLT, this will be a
   *  Transformer object.
   */
  private Object m_owner;

  /** The owner context of this XPathContext.  In the case of XSLT, this will be a
   *  Transformer object.
   */
  private Method m_ownerGetErrorListener;

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
   * @return The extensions table.
   */
  public ExtensionsTable getExtensionsTable()
  {
    return m_extensionsTable;
  }

  /**
   * Set the extensions table object.
   *
   *
   * @param table The extensions table object.
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
   * @return the variable stack, which should not be null.
   */
  public VariableStack getVarStack()
  {
    return m_variableStacks;
  }

  /**
   * Get the variable stack, which is in charge of variables and
   * parameters.
   *
   * @param varStack non-null reference to the variable stack.
   */
  public void setVarStack(VariableStack varStack)
  {
    m_variableStacks = varStack;
  }

  /**
   * Given a name, locate a variable in the current context, and return
   * the Object.
   *
   * @param qname The qualified name of a variable.
   *
   * @return reference to variable, or null if not found.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject getVariable(QName qname) throws javax.xml.transform.TransformerException
  {

    return getVarStack().getVariable(this, qname);
  }

  // ================ DOMHelper ===================

  /** The basic DOM helper for the root source tree.
   *  Note that I have some worry about different source tree types 
   *  being mixed, so this may not be a perfect place for this.
   *  Right now, I think all the DOM helpers can handle a DOM that 
   *  they don't know about.  */
  private DOMHelper m_domHelper = new DOM2Helper();

  /**
   * Get the DOMHelper associated with this execution context.
   *
   * @return non-null reference to a DOM helper.
   */
  public final DOMHelper getDOMHelper()
  {
    return m_domHelper;
  }

  /**
   * Set the DOMHelper associated with this execution context.
   *
   * @param helper reference to a dom helper to be associated with this 
   *               execution context.
   */
  public void setDOMHelper(DOMHelper helper)
  {
    m_domHelper = helper;
  }

  // ================ SourceTreeManager ===================

  /** The source tree manager, which associates Source objects to source 
   *  tree nodes. */
  private SourceTreeManager m_sourceTreeManager = new SourceTreeManager();

  /**
   * Get the SourceTreeManager associated with this execution context.
   *
   * @return the SourceTreeManager associated with this execution context.
   */
  public final SourceTreeManager getSourceTreeManager()
  {
    return m_sourceTreeManager;
  }

  /**
   * Set the SourceTreeManager associated with this execution context.
   *
   * @param mgr the SourceTreeManager to be associated with this 
   *        execution context.
   */
  public void setSourceTreeManager(SourceTreeManager mgr)
  {
    m_sourceTreeManager = mgr;
  }
  
  // =================================================

  /** The ErrorListener where errors and warnings are to be reported.   */
  private ErrorListener m_errorListener;

  /** A default ErrorListener in case our m_errorListener was not specified and our
   *  owner either does not have an ErrorListener or has a null one.
   */
  private ErrorListener m_defaultErrorListener;

  /**
   * Get the ErrorListener where errors and warnings are to be reported.
   *
   * @return A non-null ErrorListener reference.
   */
  public final ErrorListener getErrorListener()
  {

    if (null != m_errorListener)
        return m_errorListener;

    ErrorListener retval = null;

    try {
      if (null != m_ownerGetErrorListener)
        retval = (ErrorListener) m_ownerGetErrorListener.invoke(m_owner, new Object[] {});
    }
    catch (Exception e) {}

    if (null == retval)
    {
      if (null == m_defaultErrorListener) 
        m_defaultErrorListener = new org.apache.xml.utils.DefaultErrorHandler();
      retval = m_defaultErrorListener;
    }

    return retval;
  }

  /**
   * Set the ErrorListener where errors and warnings are to be reported.
   *
   * @param listener A non-null ErrorListener reference.
   */
  public void setErrorListener(ErrorListener listener) throws IllegalArgumentException
  {
    if (listener == null) 
      throw new IllegalArgumentException("Null error handler");
    m_errorListener = listener;
  }


  // =================================================

  /** The TrAX URI Resolver for resolving URIs from the document(...)
   *  function to source tree nodes.  */
  private URIResolver m_uriResolver;

  /**
   * Get the URIResolver associated with this execution context.
   *
   * @return a URI resolver, which may be null.
   */
  public final URIResolver getURIResolver()
  {
    return m_uriResolver;
  }

  /**
   * Set the URIResolver associated with this execution context.
   *
   * @param resolver the URIResolver to be associated with this 
   *        execution context, may be null to clear an already set resolver.
   */
  public void setURIResolver(URIResolver resolver)
  {
    m_uriResolver = resolver;
  }

  // =================================================

  /** The reader of the primary source tree.    */
  public XMLReader m_primaryReader;

  /**
   * Get primary XMLReader associated with this execution context.
   *
   * @return The reader of the primary source tree.
   */
  public final XMLReader getPrimaryReader()
  {
    return m_primaryReader;
  }

  /**
   * Set primary XMLReader associated with this execution context.
   *
   * @param reader The reader of the primary source tree.
   */
  public void setPrimaryReader(XMLReader reader)
  {
    m_primaryReader = reader;
  }

  // =================================================


  /** Misnamed string manager for XPath messages.  */
  private static XSLMessages m_XSLMessages = new XSLMessages();

  /**
   * Tell the user of an assertion error, and probably throw an
   * exception.
   *
   * @param b  If false, a TransformerException will be thrown.
   * @param msg The assertion message, which should be informative.
   * 
   * @throws javax.xml.transform.TransformerException if b is false.
   */
  private void assertion(boolean b, String msg) throws javax.xml.transform.TransformerException
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
  
  public Stack getContextNodeListsStack() { return m_contextNodeLists; }
  public void setContextNodeListsStack(Stack s) { m_contextNodeLists = s; }

  /**
   * Get the current context node list.
   *
   * @return  the <a href="http://www.w3.org/TR/xslt#dt-current-node-list">current node list</a>,
   * also refered to here as a <term>context node list</term>.
   */
  public final DTMIterator getContextNodeList()
  {

    if (m_contextNodeLists.size() > 0)
      return (DTMIterator) m_contextNodeLists.peek();
    else
      return null;
  }

  /**
   * <meta name="usage" content="internal"/>
   * Set the current context node list.
   *
   * @param nl the <a href="http://www.w3.org/TR/xslt#dt-current-node-list">current node list</a>,
   * also refered to here as a <term>context node list</term>.
   */
  public final void pushContextNodeList(DTMIterator nl)
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

  /** The stack of <a href="http://www.w3.org/TR/xslt#dt-current-node">current node</a> objects.
   *  Not to be confused with the current node list.  */
  private NodeVector m_currentNodes = new NodeVector();
  
  public NodeVector getCurrentNodeStack() {return m_currentNodes; }
  public void setCurrentNodeStack(NodeVector nv) { m_currentNodes = nv; }

  /**
   * Get the current context node.
   *
   * @return the <a href="http://www.w3.org/TR/xslt#dt-current-node">current node</a>.
   */
  public final int getCurrentNode()
  {
    return m_currentNodes.peepOrNull();
  }
  
  /**
   * Set the current context node and expression node.
   *
   * @param cn the <a href="http://www.w3.org/TR/xslt#dt-current-node">current node</a>.
   * @param en the sub-expression context node.
   */
  public final void pushCurrentNodeAndExpression(int cn, int en)
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
   * @param n the <a href="http://www.w3.org/TR/xslt#dt-current-node">current node</a>.
   */
  public final void pushCurrentNode(int n)
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

  /** A stack of the current sub-expression nodes.  */
  private NodeVector m_currentExpressionNodes = new NodeVector();
  
  public NodeVector getCurrentExpressionNodeStack() { return m_currentExpressionNodes; }
  public void setCurrentExpressionNodeStack(NodeVector nv) { m_currentExpressionNodes = nv; }

  /**
   * Get the current node that is the expression's context (i.e. for current() support).
   *
   * @return The current sub-expression node.
   */
  public final int getCurrentExpressionNode()
  {
    return m_currentExpressionNodes.peepOrNull();
  }

  /**
   * Set the current node that is the expression's context (i.e. for current() support).
   *
   * @param n The sub-expression node to be current.
   */
  public final void pushCurrentExpressionNode(int n)
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
   * @return the current prefix resolver for resolving prefixes to 
   *         namespace URLs.
   */
  public final PrefixResolver getNamespaceContext()
  {
    return m_currentPrefixResolver;
  }

  /**
   * Get the current namespace context for the xpath.
   *
   * @param pr the prefix resolver to be used for resolving prefixes to 
   *         namespace URLs.
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
  
  public Stack getAxesIteratorStackStacks() { return m_axesIteratorStack; }
  public void setAxesIteratorStackStacks(Stack s) { m_axesIteratorStack = s; }

  /**
   * <meta name="usage" content="internal"/>
   * Push a TreeWalker on the stack.
   *
   * @param iter A sub-context AxesWalker.
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
   * @return the sub-context node list.
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
  public final int getContextNode()
  {
    return this.getCurrentNode();
  }

  /**
   * Get the current context node list.
   * @return An iterator for the current context list, as
   * defined in XSLT.
   */
  public final DTMIterator getContextNodes()
  {

    try
    {
      DTMIterator cnl = getContextNodeList();

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
  
  XPathExpressionContext expresionContext = new XPathExpressionContext();
  
  /**
   * The the expression context for extensions for this context.
   * 
   * @return An object that implements the ExpressionContext.
   */
  public ExpressionContext getExpressionContext()
  {
    return expresionContext;
  }
  
  class XPathExpressionContext implements ExpressionContext
  {
    
    /**
     * Get the current context node.
     * @return The current context node.
     */
    public org.w3c.dom.Node getContextNode()
    {
      int context = m_currentNodes.peepOrNull();
      
      return getDTM(context).getNode(context);
    }
  
    /**
     * Get the current context node list.
     * @return An iterator for the current context list, as
     * defined in XSLT.
     */
    public org.w3c.dom.traversal.NodeIterator getContextNodes()
    {
      // %TBD%
      return null;
    }
  
    /**
     * Get the value of a node as a number.
     * @param n Node to be converted to a number.  May be null.
     * @return value of n as a number.
     */
    public double toNumber(org.w3c.dom.Node n)
    {
      // %REVIEW% You can't get much uglier than this...
      int nodeHandle = getDTMHandleFromNode(n);
      DTM dtm = getDTM(nodeHandle);
      XString xobj = (XString)dtm.getStringValue(nodeHandle);
      return xobj.num();
    }
  
    /**
     * Get the value of a node as a string.
     * @param n Node to be converted to a string.  May be null.
     * @return value of n as a string, or an empty string if n is null.
     */
    public String toString(org.w3c.dom.Node n)
    {
      // %REVIEW% You can't get much uglier than this...
      int nodeHandle = getDTMHandleFromNode(n);
      DTM dtm = getDTM(nodeHandle);
      XMLString strVal = dtm.getStringValue(nodeHandle);
      return strVal.toString();
    }

    
  }

}
