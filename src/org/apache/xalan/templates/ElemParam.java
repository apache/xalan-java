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

import java.util.Arrays;
import java.util.List;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.utils.QName;
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
   * Constructor ElemParam.
   *
   */
  public ElemParam(){}
  
  /**
   * The value of the "tunnel" attribute.
   */
  private String m_tunnelAttr;
  
  /**
   * Set the "tunnel" attribute.
   */
  public void setTunnel(String val) {
	 m_tunnelAttr = val;
  }
  
  /**
   * Get the "tunnel" attribute.
   */
  public String getTunnel()
  {
     return m_tunnelAttr;
  }

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
    
    if (m_tunnelAttr != null && !isValidTunnelParamValue(m_tunnelAttr)) {
       throw new TransformerException("XTTE0590 : Allowed values for xsl:param's tunnel "
        		                                              + "attribute are : yes, true, 1, no, false, 0. The "
        		                                              + "supplied value is " + m_tunnelAttr + ".", srcLocator); 
    }
    
    if (vars.isLocalSet(m_index)) {
       XObject var = vars.getLocalVariable(xctx, m_index);
       String tunnelValStr = var.getTunnel();
 	   QName tunnelParamQName = var.getQName(); 	  
 	   if (m_qname.equals(tunnelParamQName) && !XslTransformEvaluationHelper.isTunnelAttributeYes(m_tunnelAttr) && 
 			                                               XslTransformEvaluationHelper.isTunnelAttributeYes(tunnelValStr)) {
 		  vars.setLocalVariable(m_index, null);
 		  ElemTemplateElement elemTemplateElement = getParentElem();
 		  elemTemplateElement.setTunnelParamObj(var);
 	   }
    }
            
    if (!vars.isLocalSet(m_index) || (getParentElem() instanceof ElemIterate)) {
        int sourceNode = transformer.getXPathContext().getCurrentNode();
        
        try {
           XObject var = getValue(transformer, sourceNode);
           if (var != null) {        	  
              transformer.getXPathContext().getVarStack().setLocalVariable(m_index, var);        	  
           }
           else {              
              throw new TransformerException("XTTE0590 : The value to parameter " + m_qname.toString() + " cannot be assigned. "
                                                                             + "Either an input content for parameter is not available within "
                                                                             + "stylesheet context, or parameter's value cannot be cast to an expected type.", srcLocator);
           }
        }
        catch (TransformerException ex) {
            throw new TransformerException("XTTE0590 : The value to parameter " + m_qname.toString() + " cannot be assigned. "
                                                                           + "Either an input content for parameter is not available within "
                                                                           + "stylesheet context, or parameter's value cannot be cast to an expected type.", srcLocator);   
        }	
    }
    else if (!(getParentElem() instanceof ElemFunction)) {
    	String asAttrVal = getAs();

    	if (asAttrVal != null) {
    		try {
    			XObject var = transformer.getXPathContext().getVarStack().getLocalVariable(xctx, m_index);
    			var = SequenceTypeSupport.convertXdmValueToAnotherType(var, asAttrVal, null, transformer.getXPathContext());
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
  
  /**
   * Check whether a string value is a valid, XSLT tunnel attribute value.
   */
  private boolean isValidTunnelParamValue(String val) {
	 boolean result = false;
	 
	 String[] allowedValuesStr = new String [] {"yes", "true", "1", "no", "false", "0"};
	 List<String> strList = Arrays.asList(allowedValuesStr);
	 result = strList.contains(val);
	 
	 return result;
  }
  
}
