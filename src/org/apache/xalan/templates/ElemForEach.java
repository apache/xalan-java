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
import org.w3c.dom.traversal.NodeIterator;

import org.xml.sax.*;

import org.apache.xpath.*;
import org.apache.xpath.axes.ContextNodeList;
import org.apache.xpath.objects.XObject;

import java.util.Vector;

import org.apache.xml.utils.QName;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.transformer.NodeSorter;
import org.apache.xalan.transformer.ResultTreeHandler;
import org.apache.xalan.transformer.StackGuard;
import org.apache.xalan.stree.SaxEventDispatch;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

/**
 * <meta name="usage" content="advanced"/>
 * Implement xsl:for-each.
 * <pre>
 * <!ELEMENT xsl:for-each
 *  (#PCDATA
 *   %instructions;
 *   %result-elements;
 *   | xsl:sort)
 * >
 *
 * <!ATTLIST xsl:for-each
 *   select %expr; #REQUIRED
 *   %space-att;
 * >
 * </pre>
 * @see <a href="http://www.w3.org/TR/xslt#for-each">for-each in XSLT Specification</a>
 */
public class ElemForEach extends ElemTemplateElement
{

  /**
   * Construct a element representing xsl:for-each.
   */
  public ElemForEach(){}

  /**
   * The "select" expression.
   * @serial
   */
  private XPath m_selectExpression = null;

  /**
   * Set the "select" attribute.
   *
   * @param xpath The XPath expression for the "select" attribute.
   */
  public void setSelect(XPath xpath)
  {
    m_selectExpression = xpath;
  }

  /**
   * Get the "select" attribute.
   *
   * @return The XPath expression for the "select" attribute.
   */
  public XPath getSelect()
  {
    return m_selectExpression;
  }

  /**
   * Get the "select" attribute or default selection pattern.
   *
   * @return the "select" attribute or if null, the 
   * default selection pattern ("node()")
   */
  public XPath getSelectOrDefault()
  {
    return (null == m_selectExpression)
           ? getStylesheetRoot().m_selectDefault : m_selectExpression;
  }

  /** Vector containing the xsl:sort elements associated with this element.
   *  @serial         */
  protected Vector m_sortElems = null;

  /**
   * Get the count xsl:sort elements associated with this element.
   * @return The number of xsl:sort elements.
   */
  public int getSortElemCount()
  {
    return (m_sortElems == null) ? 0 : m_sortElems.size();
  }

  /**
   * Get a xsl:sort element associated with this element.
   *
   * @param i Index of xsl:sort element to get
   *
   * @return xsl:sort element at given index
   */
  public ElemSort getSortElem(int i)
  {
    return (ElemSort) m_sortElems.elementAt(i);
  }

  /**
   * Set a xsl:sort element associated with this element.
   *
   * @param sortElem xsl:sort element to set
   */
  public void setSortElem(ElemSort sortElem)
  {

    if (null == m_sortElems)
      m_sortElems = new Vector();

    m_sortElems.addElement(sortElem);
  }

  /**
   * Get an int constant identifying the type of element.
   * @see org.apache.xalan.templates.Constants
   *
   * @return The token ID for this element
   */
  public int getXSLToken()
  {
    return Constants.ELEMNAME_FOREACH;
  }

  /**
   * Return the node name.
   *
   * @return The element's name
   */
  public String getNodeName()
  {
    return Constants.ELEMNAME_FOREACH_STRING;
  }

  /**
   * Execute the xsl:for-each transformation 
   *
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

    transformer.pushCurrentTemplateRuleIsNull(true);

    try
    {
      if (TransformerImpl.S_DEBUG)
        transformer.getTraceManager().fireTraceEvent(sourceNode, mode, this);

      transformSelectedNodes(transformer, sourceNode, this, mode);
    }
    finally
    {
      transformer.popCurrentTemplateRuleIsNull();
    }
  }

  /**
   * Get template element associated with this
   *
   *
   * @return template element associated with this (itself)
   */
  protected ElemTemplateElement getTemplateMatch()
  {
    return this;
  }

  /**
   * Sort given nodes
   *
   *
   * @param xctxt The XPath runtime state for the sort. 
   * @param keys Vector of sort keyx
   * @param sourceNodes Iterator of nodes to sort
   *
   * @return iterator of sorted nodes
   *
   * @throws TransformerException
   */
  protected NodeIterator sortNodes(
          XPathContext xctxt, Vector keys, NodeIterator sourceNodes)
            throws TransformerException
  {

    NodeSorter sorter = new NodeSorter(xctxt);
    NodeSet nodeList;

    if (sourceNodes instanceof NodeSet)
    {
      nodeList = ((NodeSet) sourceNodes);

      nodeList.setShouldCacheNodes(true);
      nodeList.runTo(-1);
    }
    else
    {
      nodeList = new NodeSet(sourceNodes);
      sourceNodes = nodeList;

      ((ContextNodeList) sourceNodes).setCurrentPos(0);
    }

    xctxt.pushContextNodeList((ContextNodeList) sourceNodes);

    try
    {
      sorter.sort(nodeList, keys, xctxt);
      nodeList.setCurrentPos(0);
    }
    finally
    {
      xctxt.popContextNodeList();
    }

    return sourceNodes;
  }

  /**
   * Return whether or not default parameters need to be pushed into stack   
   *
   *
   * @return False, no need to push parameters here.
   */
  boolean needToPushParams()
  {
    return false;
  }

  /**
   * Push default parameters into the stack 
   *
   *
   * @param transformer non-null reference to the the current transform-time state.
   * @param xctxt The XPath runtime state.
   * @param sourceNode non-null reference to the <a href="http://www.w3.org/TR/xslt#dt-current-node">current source node</a>.
   * @param mode reference, which may be null, to the <a href="http://www.w3.org/TR/xslt#modes">current mode</a>.
   *
   * @return -1, this return will not be used by ElemForEach. 
   * It is just there because ElemApplyTemplates which derives
   * from this needs to that value to be saved.
   *     
   * @throws TransformerException
   */
  int pushParams(
          TransformerImpl transformer, XPathContext xctxt, Node sourceNode, QName mode)
            throws TransformerException
  {

    VariableStack vars = xctxt.getVarStack();

    vars.pushElemFrame();
    return -1;
  }
  
  /**
   * Re-mark the params as params.
   */
  void reMarkParams(XPathContext xctxt)
  {
  }

  /**
   * Pop Default parameters from the stack 
   *
   *
   * @param xctxt The XPath runtime state.
   * @param savedSearchStart This param will not be used by ElemForEach. 
   * It is just there because ElemApplyTemplates which derives
   * from this needs to restore that value. 
   */
  void popParams(XPathContext xctxt, int savedSearchStart)
  {

    VariableStack vars = xctxt.getVarStack();

    vars.popElemFrame();
  }

  /**
   * <meta name="usage" content="advanced"/>
   * Perform a query if needed, and call transformNode for each child.
   *
   *
   * @param transformer non-null reference to the the current transform-time state.
   * @param sourceNode non-null reference to the <a href="http://www.w3.org/TR/xslt#dt-current-node">current source node</a>.
   * @throws TransformerException Thrown in a variety of circumstances.
   * @param stylesheetTree The owning stylesheet tree.
   * @param xslInstruction The stylesheet element context (depricated -- I do
   *      not think we need this).
   * @param template The owning template context.
   * @param sourceNodeContext The current source node context.
   * @param mode The current mode.
   * @param selectPattern The XPath with which to perform the selection.
   * @param xslToken The current XSLT instruction (depricated -- I do not
   *     think we want this).
   * @param tcontext The TransformerImpl context.
   * @param selectStackFrameIndex The stack frame context for executing the
   *                              select statement.
   */
  public void transformSelectedNodes(
          TransformerImpl transformer, Node sourceNode, ElemTemplateElement template, QName mode)
            throws TransformerException
  {
    try
    {
      boolean rdebug = TransformerImpl.S_DEBUG;
      XPathContext xctxt = transformer.getXPathContext();
      XPath selectPattern = getSelectOrDefault();
      XObject selectResult = selectPattern.execute(xctxt, sourceNode, this);
      
      if (rdebug)
        transformer.getTraceManager().fireSelectedEvent(sourceNode, this,
                                                        "test", selectPattern, selectResult);
      
      Vector keys = transformer.processSortKeys(this, sourceNode);
      NodeIterator sourceNodes = selectResult.nodeset();

      // Sort if we need to.
      if (null != keys)
        sourceNodes = sortNodes(xctxt, keys, sourceNodes);

      // The returned value is only relevant for ElemApplyTemplates.
      // This value needs to be passed to popParams which will
      // restore it in the variable stack.
      int savedSearchStart = pushParams(transformer, xctxt, sourceNode, mode);

      // Push the ContextNodeList on a stack, so that select="position()"
      // and the like will work.
      // System.out.println("pushing context node list...");
      SourceLocator savedLocator = xctxt.getSAXLocator();

      xctxt.pushContextNodeList((ContextNodeList) sourceNodes);
      transformer.pushElemTemplateElement(null);

      ResultTreeHandler rth = transformer.getResultTreeHandler();
      StylesheetRoot sroot = getStylesheetRoot();
      TemplateList tl = sroot.getTemplateListComposed();

      // StylesheetComposed stylesheet = getStylesheetComposed();
      StackGuard guard = transformer.getStackGuard();
      boolean check = (guard.m_recursionLimit > -1);
      boolean quiet = transformer.getQuietConflictWarnings();
      boolean needToFindTemplate = (null == template);
      
      try
      {
        Node child;

        while (null != (child = sourceNodes.nextNode()))
        {
          if (needToFindTemplate)
          {
            template = tl.getTemplate(xctxt, child, mode, -1, quiet);

            // If that didn't locate a node, fall back to a default template rule.
            // See http://www.w3.org/TR/xslt#built-in-rule.
            if (null == template)
            {
              switch (child.getNodeType())
              {
              case Node.DOCUMENT_FRAGMENT_NODE :
              case Node.ELEMENT_NODE :
                template = sroot.getDefaultRule();
                break;
              case Node.ATTRIBUTE_NODE :
              case Node.CDATA_SECTION_NODE :
              case Node.TEXT_NODE :
                if (child.isSupported(SaxEventDispatch.SUPPORTSINTERFACE, "1.0"))
                {
                  ((SaxEventDispatch) child).dispatchCharactersEvent(rth);
                }
                else
                {
                  String data = child.getNodeValue();
                  rth.characters(data.toCharArray(), 0, data.length());
                }
                continue;
              case Node.DOCUMENT_NODE :
                template = sroot.getDefaultRootRule();
                break;
              default :

                // No default rules for processing instructions and the like.
                continue;
              }
            }
          }

          ElemTemplateElement t = template.m_firstChild;
            
          // If we are processing the default text rule, then just clone 
          // the value directly to the result tree.
          try
          {              
            xctxt.pushCurrentNode(child);
            if(needToFindTemplate)
              transformer.pushPairCurrentMatched(template, child);

            if (check)
              guard.push(this, child);

            // Fire a trace event for the template.
            if (rdebug)
              transformer.getTraceManager().fireTraceEvent(child, mode,
                                                           template);

            // And execute the child templates.
            if (template.isCompiledTemplate())
              template.execute(transformer, child, mode);
            else
            {

              // Loop through the children of the template, calling execute on 
              // each of them.
              for (; t != null;
                   t = t.m_nextSibling)
              {
                xctxt.setSAXLocator(t);
                transformer.setCurrentElement(t);
                t.execute(transformer, child, mode);
              }
            }
            reMarkParams(xctxt);
          }
          finally
          {
            xctxt.popCurrentNode();
            if(needToFindTemplate)
              transformer.popCurrentMatched();

            if (check)
              guard.pop();
          }
        }
      }
      finally
      {
        xctxt.setSAXLocator(savedLocator);
        xctxt.popContextNodeList();
        transformer.popElemTemplateElement();
        popParams(xctxt, savedSearchStart);
        // if(null != sourceNodes)
        //  sourceNodes.detach();                
      }
    }
    catch(SAXException se)
    {
      transformer.getErrorListener().fatalError(new TransformerException(se));
    }
  }

  /**
   * Add a child to the child list.
   * <!ELEMENT xsl:apply-templates (xsl:sort|xsl:with-param)*>
   * <!ATTLIST xsl:apply-templates
   *   select %expr; "node()"
   *   mode %qname; #IMPLIED
   * >
   *
   * @param newChild Child to add to child list
   *
   * @return Child just added to child list
   *
   * @throws DOMException
   */
  public Node appendChild(Node newChild) throws DOMException
  {

    int type = ((ElemTemplateElement) newChild).getXSLToken();

    if (Constants.ELEMNAME_SORT == type)
    {
      setSortElem((ElemSort) newChild);

      return newChild;
    }
    else
      return super.appendChild(newChild);
  }
}
