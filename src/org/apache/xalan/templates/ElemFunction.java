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
package org.apache.xalan.templates;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.utils.QName;
import org.apache.xpath.VariableStack;
import org.apache.xpath.XPathContext;
import org.apache.xpath.composite.SequenceTypeSupport;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XNodeSetForDOM;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XRTreeFrag;
import org.w3c.dom.NodeList;

/**
 * Implementation of XSLT xsl:function element.
 * 
 * Ref : https://www.w3.org/TR/xslt-30/#stylesheet-functions
 *
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class ElemFunction extends ElemTemplate
{

  private static final long serialVersionUID = 4973132678982467288L;

  /**
   * Class constructor.
   */
  public ElemFunction() {}

  /**
   * The value of the "name" attribute.
   */
  protected QName m_qname;

  public void setName(QName qName)
  {
      m_qname = qName;
  }

  public QName getName()
  {
      return m_qname;
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
     return Constants.ELEMNAME_FUNCTION;
  }

  /**
   * Return the node name.
   *
   * @return The node name
   */
  public String getNodeName()
  {
     return Constants.ELEMNAME_FUNCTION_STRING;
  }
  
  /**
   * The stack frame size for this xsl:function definition, which is equal
   * to the maximum number of params and variables that can be declared 
   * in the function at one time.
   */
  public int m_frameSize;
  
  /**
   * The size of the portion of the stack frame that can hold parameter 
   * arguments.
   */
  int m_inArgsSize;

  /**
   * This method evaluates the xsl:function call, and returns the result
   * to the caller of this function.
   */
  public XObject executeXslFunction(TransformerImpl transformer, 
                                                             ResultSequence argSequence) throws TransformerException {
      XObject result = null;
      
      XPathContext xctxt = transformer.getXPathContext();
      
      SourceLocator srcLocator = xctxt.getSAXLocator(); 
      
      VariableStack varStack = xctxt.getVarStack();
      
      int thisframe = varStack.getStackFrame();
      int nextFrame = varStack.link(m_frameSize);
      
      varStack.setStackFrame(thisframe);
      
      String funcLocalName = m_qname.getLocalName();
      String funcNameSpaceUri = m_qname.getNamespaceURI(); 
      
      int paramCount = 0;
      for (ElemTemplateElement elem = getFirstChildElem(); elem != null; 
                                                              elem = elem.getNextSiblingElem()) {
          if(elem.getXSLToken() == Constants.ELEMNAME_PARAMVARIABLE) {
             if (argSequence.size() < (paramCount + 1)) {
                 throw new TransformerException("XPST0017 : Cannot find a " + argSequence.size() + " argument function named "
                                                                            + "{" + funcNameSpaceUri + "}" + funcLocalName + "() within a stylesheet scope. "
                                                                            + "The function name was recognized, but number of arguments is wrong.", srcLocator); 
             }
             XObject argValue = argSequence.item(paramCount);
             XObject argConvertedVal = argValue;
             String paramAsAttrStrVal = ((ElemParam)elem).getAs();              
             if (paramAsAttrStrVal != null) {
                try {
                   argConvertedVal = SequenceTypeSupport.convertXDMValueToAnotherType(argValue, paramAsAttrStrVal, null, xctxt);
                   if (argConvertedVal == null) {
                      throw new TransformerException("XPTY0004 : Function call argument at position " + (paramCount + 1) + " for "
                                                                        + "function {" + funcNameSpaceUri + "}" + funcLocalName + "(), doesn't "
                                                                        + "match the declared parameter type " + paramAsAttrStrVal + ".", srcLocator); 
                   }
                }
                catch (TransformerException ex) {
                   throw new TransformerException("XPTY0004 : Function call argument at position " + (paramCount + 1) + " for "
                                                                     + "function {" + funcNameSpaceUri + "}" + funcLocalName + "(), doesn't "
                                                                     + "match the declared parameter type " + paramAsAttrStrVal + ".", srcLocator); 
                }
             }
             
             if (argConvertedVal instanceof ResultSequence) {                
                XNodeSet nodeSet = XslTransformEvaluationHelper.getXNodeSetFromResultSequence((ResultSequence)argConvertedVal, 
                                                                                                                            xctxt);
                if (nodeSet != null) {
                   argConvertedVal = nodeSet;  
                }
             }
             
             varStack.setLocalVariable(paramCount, argConvertedVal, nextFrame);
             paramCount++;
          }
          else {
             break; 
          }
      }
      
      varStack.setStackFrame(nextFrame);
      
      int df = transformer.transformToGlobalRTF(this);
      
      NodeList nodeList = (new XRTreeFrag(df, xctxt, this)).convertToNodeset();     
      
      result = new XNodeSetForDOM(nodeList, xctxt);
            
      XObject funcResultConvertedVal = result;
      
      String funcAsAttrStrVal = getAs();
      
      if (funcAsAttrStrVal != null) {
         try {
            funcResultConvertedVal = SequenceTypeSupport.convertXDMValueToAnotherType(result, funcAsAttrStrVal, null, xctxt);
            if (funcResultConvertedVal == null) {
               throw new TransformerException("XPTY0004 : The function call result for function {" + funcNameSpaceUri + "}" + funcLocalName + 
                                                                                "(), doesn't match the declared function result type " + 
                                                                                                                       funcAsAttrStrVal + ".", srcLocator);   
            }
         }
         catch (TransformerException ex) {
            throw new TransformerException("XPTY0004 : The function call result for function {" + funcNameSpaceUri + "}" + funcLocalName + 
                                                                                           "(), doesn't match the declared function result type " + 
                                                                                                                       funcAsAttrStrVal + ".", srcLocator); 
         }
      }
      
      if (funcResultConvertedVal instanceof ResultSequence) {
         ResultSequence resultSeq = (ResultSequence)funcResultConvertedVal;
         int resultSeqSize = resultSeq.size();
         if ((resultSeqSize == 1) && (resultSeq.item(0) instanceof XNodeSet)) {
            funcResultConvertedVal = resultSeq.item(0);   
         }
      }
      
      return funcResultConvertedVal;
  }
  
  /**
   * This function is called after everything else has been
   * recomposed, and allows an xsl:function to set remaining
   * values that may be based on some other property that
   * depends on recomposition.
   */
  public void compose(StylesheetRoot sroot) throws TransformerException
  {
      super.compose(sroot);
      StylesheetRoot.ComposeState cstate = sroot.getComposeState();
      java.util.Vector vnames = cstate.getVariableNames();        
      cstate.resetStackFrameSize();
      m_inArgsSize = 0;
  }
  
  /**
   * This function is called, after xsl:function's children have been composed. 
   * We have to get the count of how many variables have been declared, so we
   * can do a link and unlink.
   */
  public void endCompose(StylesheetRoot sroot) throws TransformerException
  {
      StylesheetRoot.ComposeState cstate = sroot.getComposeState();
      super.endCompose(sroot);
      m_frameSize = cstate.getFrameSize();
      
      cstate.resetStackFrameSize();
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
  
  public void recompose(StylesheetRoot root)
  {
      root.recomposeTemplates(this);
  }

}
