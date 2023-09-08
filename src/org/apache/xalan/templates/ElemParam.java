/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the  "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * $Id$
 */
package org.apache.xalan.templates;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xpath.VariableStack;
import org.apache.xpath.XPathContext;
import org.apache.xpath.composite.SequenceTypeSupport;
import org.apache.xpath.objects.XObject;

/**
 * Implementation of XSLT xsl:param element.
 * 
 * Ref : https://www.w3.org/TR/xslt-30/#element-param
 * 
 * @xsl.usage advanced
 */
public class ElemParam extends ElemVariable
{
  
  static final long serialVersionUID = -1131781475589006431L;
  
  int m_qnameID;

  /**
   * Constructor ElemParam
   *
   */
  public ElemParam(){}

  /**
   * Get an int constant identifying the type of element.
   * @see org.apache.xalan.templates.Constants
   *
   * @return The token ID of the element
   */
  public int getXSLToken()
  {
    return Constants.ELEMNAME_PARAMVARIABLE;
  }

  /**
   * Return the node name.
   *
   * @return The element's name
   */
  public String getNodeName()
  {
    return Constants.ELEMNAME_PARAMVARIABLE_STRING;
  }

  /**
   * Copy constructor.
   *
   * @param param Element from an xsl:param
   *
   * @throws TransformerException
   */
  public ElemParam(ElemParam param) throws TransformerException
  {
    super(param);
  }

  /**
   * This function is called after everything else has been
   * recomposed, and allows the template to set remaining
   * values that may be based on some other property that
   * depends on recomposition.
   */
  public void compose(StylesheetRoot sroot) throws TransformerException
  {
    super.compose(sroot);
    m_qnameID = sroot.getComposeState().getQNameID(m_qname);
    int parentToken = m_parentNode.getXSLToken();
    if (parentToken == Constants.ELEMNAME_TEMPLATE
        || parentToken == Constants.EXSLT_ELEMNAME_FUNCTION)
      ((ElemTemplate)m_parentNode).m_inArgsSize++;
  }
  
  /**
   * Execute a variable declaration and push it onto the variable stack.
   * @see <a href="http://www.w3.org/TR/xslt#variables">variables in XSLT Specification</a>
   *
   * @param transformer non-null reference to the the current transform-time state.
   *
   * @throws TransformerException
   */
  public void execute(TransformerImpl transformer) throws TransformerException
  {
    if (transformer.getDebug())
      transformer.getTraceManager().fireTraceEvent(this);
      
    VariableStack vars = transformer.getXPathContext().getVarStack();
    
    XPathContext xctx = transformer.getXPathContext();
    
    SourceLocator srcLocator = xctx.getSAXLocator();
    
    String asAttrVal = getAs();
    
    if(!vars.isLocalSet(m_index)) {
        // The caller of this stylesheet callable component, didn't provide the 
        // parameter value via xsl:with-param instruction.
        
        // We'll determine the parameter's value by evaluating either the parameter's
        // 'select' attribute, or the contained sequence constructor within xsl:param 
        // element.
        
        int sourceNode = transformer.getXPathContext().getCurrentNode();
        
        try {
           XObject var = getValue(transformer, sourceNode);
           if (var == null) {
              throw new TransformerException("XTTE0590 : The value to parameter " + m_qname.toString() + " cannot be assigned. "
                                                                             + "Either an input content for parameter is not available within "
                                                                             + "stylesheet context, or parameter's value cannot be cast to an expected type.", srcLocator);  
           }
           
           transformer.getXPathContext().getVarStack().setLocalVariable(m_index, var);
        }
        catch (TransformerException ex) {
            throw new TransformerException("XTTE0590 : The value to parameter " + m_qname.toString() + " cannot be assigned. "
                                                                           + "Either an input content for parameter is not available within "
                                                                           + "stylesheet context, or parameter's value cannot be cast to an expected type.", srcLocator);   
        }
    }
    else {
        // The caller of this stylesheet callable component, has provided the 
        // parameter's value via xsl:with-param instruction.
        
        // If the xsl:param instruction has 'as' attribute, we'll check below
        // whether the parameter's value conforms to the sequence type specified by
        // xsl:param's 'as' attribute.
        
        if (asAttrVal != null) {
          try {
             XObject var = transformer.getXPathContext().getVarStack().getLocalVariable(xctx, m_index);
             var = SequenceTypeSupport.convertXDMValueToAnotherType(var, asAttrVal, null, 
                                                                               transformer.getXPathContext());
             if (var == null) {
                throw new TransformerException("XTTE0590 : The required item type of the value of parameter " + 
                                                                               m_qname.toString() + " is " + asAttrVal + ". The supplied value "
                                                                               + "doesn't match the expected item type.", srcLocator);  
             }
          }
          catch (TransformerException ex) {
             throw new TransformerException("XTTE0590 : The required item type of the value of parameter " + 
                                                                               m_qname.toString() + " is " + asAttrVal + ". The supplied value "
                                                                               + "doesn't match the expected item type.", srcLocator); 
          }
       }
    }
    
    
    if (transformer.getDebug())
      transformer.getTraceManager().fireTraceEndEvent(this);
  }
  
}
