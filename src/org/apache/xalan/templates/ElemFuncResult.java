/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights 
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

//import org.w3c.dom.*;
import org.apache.xml.dtm.DTM;

import org.xml.sax.*;

import org.apache.xpath.*;
import org.apache.xpath.Expression;
import org.apache.xpath.objects.XObjectFactory;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;
import org.apache.xpath.objects.XRTreeFrag;
import org.apache.xpath.objects.XRTreeFragSelectWrapper;
import org.apache.xml.utils.QName;
import org.apache.xalan.trace.SelectionEvent;
import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.transformer.TransformerImpl;

import javax.xml.transform.TransformerException;

/**
 * Handles the xsl:result element within an xsl:function element.
 */
public class ElemFuncResult extends ElemVariable
{
 
  /**
   * Generate the xsl:function return value, and assign it to the variable
   * index slot assigned for it in ElemFunction compose().
   * 
   */
  public void execute(TransformerImpl transformer) throws TransformerException
  {    
    XPathContext context = transformer.getXPathContext();
    VariableStack varStack = context.getVarStack();
    // ElemFunc result should always be the last child of an ElemFunction.
    ElemFunction owner = this.getParentElem() instanceof ElemFunction ? 
                         (ElemFunction)this.getParentElem() : null;
    if (owner != null)
    {
      int resultIndex = owner.getResultIndex();
      int sourceNode = context.getCurrentNode();
      // Set the return value;
      XObject var = getValue(transformer, sourceNode);   
      varStack.setLocalVariable(resultIndex, var);
    }    
  }

  /**
   * Get an integer representation of the element type.
   *
   * @return An integer representation of the element, defined in the
   *     Constants class.
   * @see org.apache.xalan.templates.Constants
   */
  public int getXSLToken()
  {
    return Constants.ELEMNAME_FUNCRESULT;
  }
  
  /**
   * Return the node name, defined in the
   *     Constants class.
   * @see org.apache.xalan.templates.Constants.
   * @return The node name
   * 
   */
   public String getNodeName()
  {
    return Constants.ELEMNAME_FUNCRESULT_STRING;
  }

  /**
   * @see org.apache.xalan.templates.ElemVariable#addVariableName(ComposeState)
   */
  protected void addVariableName(StylesheetRoot.ComposeState cstate)
  {
    // We don't want to add this to the list.
    // super.addVariableName(cstate);
  }

}
