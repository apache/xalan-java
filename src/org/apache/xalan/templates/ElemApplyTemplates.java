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
import org.apache.xpath.objects.XObject;

import java.util.Vector;

import org.apache.xalan.trace.TracerEvent;
import org.apache.xml.utils.QName;
import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xpath.VariableStack;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.transformer.ResultTreeHandler;
import org.apache.xalan.transformer.ClonerToResultTree;

import javax.xml.transform.TransformerException;

/**
 * <meta name="usage" content="advanced"/>
 * Implement xsl:apply-templates.
 * <pre>
 * &amp;!ELEMENT xsl:apply-templates (xsl:sort|xsl:with-param)*>
 * &amp;!ATTLIST xsl:apply-templates
 *   select %expr; "node()"
 *   mode %qname; #IMPLIED
 * &amp;
 * </pre>
 * @see <a href="http://www.w3.org/TR/xslt#section-Applying-Template-Rules">section-Applying-Template-Rules in XSLT Specification</a>
 */
public class ElemApplyTemplates extends ElemCallTemplate
{

  /**
   * mode %qname; #IMPLIED
   * @serial
   */
  private QName m_mode = null;

  /**
   * Set the mode attribute for this element.
   *
   * @param mode reference, which may be null, to the <a href="http://www.w3.org/TR/xslt#modes">current mode</a>.
   */
  public void setMode(QName mode)
  {
    m_mode = mode;
  }

  /**
   * Get the mode attribute for this element.
   *
   * @return The mode attribute for this element
   */
  public QName getMode()
  {
    return m_mode;
  }

  /**
   * Tells if this belongs to a default template,
   * in which case it will act different with
   * regard to processing modes.
   * @see <a href="http://www.w3.org/TR/xslt#built-in-rule">built-in-rule in XSLT Specification</a>
   * @serial
   */
  private boolean m_isDefaultTemplate = false;

  /**
   * Set if this belongs to a default template,
   * in which case it will act different with
   * regard to processing modes.
   * @see <a href="http://www.w3.org/TR/xslt#built-in-rule">built-in-rule in XSLT Specification</a>
   *
   * @param b boolean value to set. 
   */
  public void setIsDefaultTemplate(boolean b)
  {
    m_isDefaultTemplate = b;
  }

  /**
   * Get an int constant identifying the type of element.
   * @see org.apache.xalan.templates.Constants
   *
   * @return Token ID for this element types
   */
  public int getXSLToken()
  {
    return Constants.ELEMNAME_APPLY_TEMPLATES;
  }

  /**
   * Return the node name.
   *
   * @return Element name
   */
  public String getNodeName()
  {
    return Constants.ELEMNAME_APPLY_TEMPLATES_STRING;
  }

  /**
   * Apply the context node to the matching templates.
   * @see <a href="http://www.w3.org/TR/xslt#section-Applying-Template-Rules">section-Applying-Template-Rules in XSLT Specification</a>
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

    transformer.pushCurrentTemplateRuleIsNull(false);

    try
    {
      if (TransformerImpl.S_DEBUG)
        transformer.getTraceManager().fireTraceEvent(sourceNode, mode, this);

      if (null != sourceNode)
      {

        // boolean needToTurnOffInfiniteLoopCheck = false;
        if (!m_isDefaultTemplate)
        {
          mode = m_mode;
        }

        transformSelectedNodes(transformer, sourceNode, null, mode);
      }
      else  // if(null == sourceNode)
      {
        transformer.getMsgMgr().error(this,
          XSLTErrorResources.ER_NULL_SOURCENODE_HANDLEAPPLYTEMPLATES);  //"sourceNode is null in handleApplyTemplatesInstruction!");
      }
    }
    finally
    {
      transformer.popCurrentTemplateRuleIsNull();
    }
  }

  /**
   * Return whether or not we need to push default arguments on the stack   
   *
   *
   * @return whether or not to push default arguments on the stack
   */
  boolean needToPushParams()
  {
    return true;
  }

  /**
   * Push default arguments on the stack
   *
   *
   * @param transformer non-null reference to the the current transform-time state.
   * @param xctxt The XPath runtime state for this transformation.
   * @param sourceNode non-null reference to the <a href="http://www.w3.org/TR/xslt#dt-current-node">current source node</a>.
   * @param mode reference, which may be null, to the <a href="http://www.w3.org/TR/xslt#modes">current mode</a>.
   *
   * @return The original value of where to start the current search for a variable.
   * This value will be used by popParams to restore the value of
   * VariableStack.m_searchStart.
   * 
   * @throws TransformerException
   */
  int pushParams(
          TransformerImpl transformer, XPathContext xctxt, Node sourceNode, QName mode)
            throws TransformerException
  {

    VariableStack vars = xctxt.getVarStack();
    int savedSearchStart = vars.getSearchStart();      
    
    if (null != m_paramElems)
    {  
      transformer.pushParams(xctxt, this, sourceNode, mode);
     }  
    else
      vars.pushContextMarker();
    
    vars.setSearchStart(-1);
     
    return savedSearchStart;
  }
  
  /**
   * Re-mark the params as params.
   */
  void reMarkParams(XPathContext xctxt)
  {
    VariableStack vars = xctxt.getVarStack();
    vars.remarkParams();
  }


  /**
   * Pop the stack of default arguments after we're done with them 
   *
   *
   * @param xctxt The XPath runtime state for this transformation.
   * @param savedSearchStart Value to restore VariableStack.m_searchStart
   * to. This is used to set where to start the current search for a variable.
   */
  void popParams(XPathContext xctxt, int savedSearchStart)
  {
    VariableStack vars = xctxt.getVarStack();
    vars.popCurrentContext();
    vars.setSearchStart(savedSearchStart);
  }
}
