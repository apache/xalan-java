/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the "License");
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

import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xml.utils.QName;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.composite.SequenceTypeSupport;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XNodeSetForDOM;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XRTreeFrag;
import org.apache.xpath.objects.XString;
import org.w3c.dom.NodeList;

/**
 * Implementation of XSLT xsl:with-param element.
 * 
 * Ref : https://www.w3.org/TR/xslt-30/#element-with-param
 * 
 * @xsl.usage advanced
 */
public class ElemWithParam extends ElemTemplateElement
{
  
  static final long serialVersionUID = -1070355175864326257L;
  
  /**
   * This is the index to the stack frame being called, <emph>not</emph> the 
   * stack frame that contains this element.
   */
  int m_index;

  /**
   * The "select" attribute, which specifies the value of the
   * argument, if element content is not specified.
   * @serial
   */
  private XPath m_selectPattern = null;
  
  /**
   * True if the pattern is a simple ".".
   */
  private boolean m_isDot = false;

  /**
   * Set the "select" attribute.
   * The "select" attribute specifies the value of the
   * argument, if element content is not specified.
   *
   * @param v Value to set for the "select" attribute. 
   */
  public void setSelect(XPath v)
  {
      m_selectPattern = v;
    
      if (v != null) {
          String s = v.getPatternString();

          m_isDot = (null != s) && s.equals(".");
      }
  }

  /**
   * Get the "select" attribute.
   * The "select" attribute specifies the value of the
   * argument, if element content is not specified.
   *
   * @return Value of the "select" attribute. 
   */
  public XPath getSelect()
  {
    return m_selectPattern;
  }

  /**
   * The required name attribute specifies the name of the
   * parameter (the variable the value of whose binding is
   * to be replaced). The value of the name attribute is a QName,
   * which is expanded as described in [2.4 Qualified Names].
   * @serial
   */
  private QName m_qname = null;
  
  int m_qnameID;

  /**
   * Set the "name" attribute.
   * DJD
   *
   * @param v Value to set for the "name" attribute.
   */
  public void setName(QName v)
  {
    m_qname = v;
  }

  /**
   * Get the "name" attribute.
   * DJD
   *
   * @return Value of the "name" attribute.
   */
  public QName getName()
  {
    return m_qname;
  }
  
  /**
   * The value of the "as" attribute.
   */
  private String m_asAttr;
  
  /**
   * Set the "as" attribute.
   */
  public void setAs(String val) {
     m_asAttr = val;
  }
  
  /**
   * Get the "as" attribute.
   */
  public String getAs()
  {
     return m_asAttr;
  }
  
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
   * Get an integer representation of the element type.
   *
   * @return An integer representation of the element, defined in the
   *     Constants class.
   * @see org.apache.xalan.templates.Constants
   */
  public int getXSLToken()
  {
    return Constants.ELEMNAME_WITHPARAM;
  }


  /**
   * Return the node name.
   *
   * @return the node name.
   */
  public String getNodeName()
  {
    return Constants.ELEMNAME_WITHPARAM_STRING;
  }
  
  /**
   * This function is called after everything else has been
   * recomposed, and allows the template to set remaining
   * values that may be based on some other property that
   * depends on recomposition.
   */
  public void compose(StylesheetRoot sroot) throws TransformerException
  {
    // See if we can reduce an RTF to a select with a string expression.
    if(null == m_selectPattern  
       && sroot.getOptimizer())
    {
      XPath newSelect = ElemVariable.rewriteChildToExpression(this);
      if(null != newSelect)
        m_selectPattern = newSelect;
    }
    m_qnameID = sroot.getComposeState().getQNameID(m_qname);
    super.compose(sroot);
    
    java.util.Vector vnames = sroot.getComposeState().getVariableNames();
    if(null != m_selectPattern)
      m_selectPattern.fixupVariables(vnames, sroot.getComposeState().getGlobalsSize());
      
    // m_index must be resolved by ElemApplyTemplates and ElemCallTemplate!
  }
  
  /**
   * Set the parent as an ElemTemplateElement.
   *
   * @param p This node's parent as an ElemTemplateElement
   */
  public void setParentElem(ElemTemplateElement p)
  {
    super.setParentElem(p);
    p.m_hasVariableDecl = true;
  }
  
  /**
   * Get the XObject representation of the variable.
   *
   * @param transformer non-null reference to the the current transform-time state.
   * @param sourceNode non-null reference to the current source node.
   *
   * @return the XObject representation of the variable.
   *
   * @throws TransformerException
   */
  public XObject getValue(TransformerImpl transformer, int sourceNode) throws TransformerException
  {

    XObject var;
    XPathContext xctxt = transformer.getXPathContext();
    
    SourceLocator srcLocator = xctxt.getSAXLocator();
    
    if (m_tunnelAttr != null && !isValidTunnelParamValue(m_tunnelAttr)) {
       throw new TransformerException("XTTE0590 : Allowed values for xsl:with-param's tunnel "
       		                                           + "attribute are : yes, true, 1, no, false, 0. The "
       		                                           + "supplied value is " + m_tunnelAttr + ".", srcLocator); 
    }

    xctxt.pushCurrentNode(sourceNode);

    try
    {
      if (null != m_selectPattern)
      {
        XObject xpath3ContextItem = xctxt.getXPath3ContextItem();
        
        if (m_isDot && xpath3ContextItem != null) {
           var = xpath3ContextItem;   
        }
        else {
           var = m_selectPattern.execute(xctxt, sourceNode, this);
        }

        var.allowDetachToRelease(false);

        if (transformer.getDebug())
          transformer.getTraceManager().emitSelectedEvent(sourceNode, this,
                  "select", m_selectPattern, var);
      }
      else if (null == getFirstChildElem())
      {
        var = XString.EMPTYSTRING;
      }
      else
      {
          // Use result tree fragment
          int df = transformer.transformToRTF(this);

          var = new XRTreeFrag(df, xctxt, this);
        
          NodeList nodeList = (new XRTreeFrag(df, xctxt, this)).convertToNodeset();       
        
          var = new XNodeSetForDOM(nodeList, xctxt); 
      }
    }
    finally
    {
      xctxt.popCurrentNode();
    }
    
    if (m_asAttr != null) {
       var = SequenceTypeSupport.castXdmValueToAnotherType(var, m_asAttr, null, 
                                                                          transformer.getXPathContext());
       if (var == null) {
          throw new TransformerException("XTTE0590 : The required item type of the value of argument used for xsl:with-param " + 
                                                                     m_qname.toString() + " is " + m_asAttr + ". The supplied value "
                                                                     + "doesn't match the expected item type.", srcLocator);  
       }
       else {
          if ((var instanceof ResultSequence) && (((ResultSequence)var).size() == 1)) {
       	     var = (((ResultSequence)var)).item(0); 
          }
       }
    }

    return var;
  }
  
  /**
   * Call the children visitors.
   * @param visitor The visitor whose appropriate method will be called.
   */
  protected void callChildVisitors(XSLTVisitor visitor, boolean callAttrs)
  {
  	if(callAttrs && (null != m_selectPattern))
  		m_selectPattern.getExpression().callVisitors(m_selectPattern, visitor);
    super.callChildVisitors(visitor, callAttrs);
  }
  
  /**
   * Add a child to the child list. If the select attribute
   * is present, an error will be raised.
   *
   * @param elem New element to append to this element's children list
   *
   * @return null if the select attribute was present, otherwise the 
   * child just added to the child list 
   */
  public ElemTemplateElement appendChild(ElemTemplateElement elem)
  {
    // cannot have content and select
    if (m_selectPattern != null)
    {
      error(XSLTErrorResources.ER_CANT_HAVE_CONTENT_AND_SELECT, 
          new Object[]{"xsl:" + this.getNodeName()});
      return null;
    }
    return super.appendChild(elem);
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
