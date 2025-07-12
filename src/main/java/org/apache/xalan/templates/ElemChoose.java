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

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMManager;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;
import org.w3c.dom.DOMException;

import xml.xpath31.processor.types.XSAnyType;

/**
 *  Implementation of the XSLT 3.0 xsl:choose instruction.
 *  
 *  @xsl.usage advanced
 */
public class ElemChoose extends ElemTemplateElement
{
  static final long serialVersionUID = -3070117361903102033L;
  
  /**
   * Class field to, represent the value of "xpath-default-namespace" 
   * attribute.
   */
  private String m_xpath_default_namespace = null;

  /**
   * Set the value of "xpath-default-namespace" attribute.
   *
   * @param v   Value of the "xpath-default-namespace" attribute
   */
  public void setXpathDefaultNamespace(String v)
  {
	  m_xpath_default_namespace = v; 
  }

  /**
   * Get the value of "xpath-default-namespace" attribute.
   *  
   * @return		  The value of "xpath-default-namespace" attribute 
   */
  public String getXpathDefaultNamespace() {
	  return m_xpath_default_namespace;
  }

  /**
   * Get an int constant identifying the type of element.
   * @see org.apache.xalan.templates.Constants
   *
   * @return The token ID for this element
   */
  public int getXSLToken()
  {
    return Constants.ELEMNAME_CHOOSE;
  }

  /**
   * Return the node name.
   *
   * @return The element's name
   */
  public String getNodeName()
  {
    return Constants.ELEMNAME_CHOOSE_STRING;
  }

  /**
   * Constructor ElemChoose
   *
   */
  public ElemChoose(){}

  /**
   * Execute the xsl:choose transformation.
   *
   *
   * @param transformer non-null reference to the the current transform-time state.
   *
   * @throws TransformerException
   */
  public void execute(TransformerImpl transformer) throws TransformerException
  {

    if (transformer.getDebug())
      transformer.getTraceManager().emitTraceEvent(this);

    boolean found = false;

    for (ElemTemplateElement childElem = getFirstChildElem();
            childElem != null; childElem = childElem.getNextSiblingElem())
    {
      int type = childElem.getXSLToken();

      if (Constants.ELEMNAME_WHEN == type)
      {
        found = true;

        ElemWhen when = (ElemWhen) childElem;                

        // must be xsl:when
        XPathContext xctxt = transformer.getXPathContext();
        SourceLocator srcLocator = xctxt.getSAXLocator(); 
        XPath whenTest = when.getTest();
        if (when.getXpathDefaultNamespace() != null) {
           whenTest = new XPath(whenTest.getPatternString(), srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
        }
        
        int sourceNode = xctxt.getCurrentNode();

        if (transformer.getDebug())
        {
            XObject test = whenTest.execute(xctxt, sourceNode, when);

            if (transformer.getDebug())
               transformer.getTraceManager().emitSelectedEvent(sourceNode, when,
                                                                        "test", whenTest, test);
            if (test.bool())
            {
               transformer.getTraceManager().emitTraceEvent(when);
            
               transformer.executeChildTemplates(when, true);

	           transformer.getTraceManager().emitTraceEndEvent(when); 
	                  
               return;
            }
        }
        else {
            XObject xpath3ContextItem = xctxt.getXPath3ContextItem();        
            if (xpath3ContextItem != null) {
                XPathContext xctxtNew = new XPathContext(false);            
                xctxtNew.setVarStack(xctxt.getVarStack());
                
                xctxtNew.setXPath3ContextPosition(xctxt.getXPath3ContextPosition());
                xctxtNew.setXPath3ContextSize(xctxt.getXPath3ContextSize());
                                  
                DTMManager dtmMgr = xctxtNew.getDTMManager();
                String strVal = "";
                if (xpath3ContextItem instanceof XSAnyType) {
                    strVal = ((XSAnyType)xpath3ContextItem).stringValue();     
                }
                else {
                    strVal = xpath3ContextItem.str(); 
                }
                DTM docFragDtm = dtmMgr.createDTMForSimpleXMLDocument(strVal);
          
                int contextNode = docFragDtm.getFirstChild(docFragDtm.getDocument());            
                xctxtNew.pushCurrentNode(contextNode);
                xctxtNew.setSAXLocator(this);
                if (whenTest.bool(xctxtNew, contextNode, this)) {
                    transformer.executeChildTemplates(when, true);
                    return;
                }  
            }        
            else if (whenTest.bool(xctxt, sourceNode, this)) {
               transformer.executeChildTemplates(when, true);
               return;
            }  
        }        
      }
      else if (Constants.ELEMNAME_OTHERWISE == type)
      {
        found = true;

        if (transformer.getDebug())
          transformer.getTraceManager().emitTraceEvent(childElem);

        // xsl:otherwise                
        transformer.executeChildTemplates(childElem, true);

        if (transformer.getDebug())
	      transformer.getTraceManager().emitTraceEndEvent(childElem); 
        return;
      }
    }

    if (!found)
      transformer.getMsgMgr().error(
        this, XSLTErrorResources.ER_CHOOSE_REQUIRES_WHEN);
        
    if (transformer.getDebug())
	  transformer.getTraceManager().emitTraceEndEvent(this);         
  }

  /**
   * Add a child to the child list.
   *
   * @param newChild Child to add to this node's child list
   *
   * @return The child that was just added to the child list
   *
   * @throws DOMException
   */
  public ElemTemplateElement appendChild(ElemTemplateElement newChild)
  {

    int type = ((ElemTemplateElement) newChild).getXSLToken();

    switch (type)
    {
        case Constants.ELEMNAME_WHEN :
        case Constants.ELEMNAME_OTHERWISE :
          break;
        default :
          error(XSLTErrorResources.ER_CANNOT_ADD,
                new Object[]{ newChild.getNodeName(),
                              this.getNodeName() });
    }

    return super.appendChild(newChild);
  }
  
  /**
   * Tell if this element can accept variable declarations.
   * @return true if the element can accept and process variable declarations.
   */
  public boolean canAcceptVariables()
  {
  	return false;
  }

}
