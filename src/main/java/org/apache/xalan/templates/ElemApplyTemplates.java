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

import java.util.List;
import java.util.Vector;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.transformer.StackGuard;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.serializer.SerializationHandler;
import org.apache.xml.utils.IntStack;
import org.apache.xml.utils.QName;
import org.apache.xpath.VariableStack;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.composite.SequenceTypeSupport;
import org.apache.xpath.composite.XPathSequenceConstructor;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XBooleanStatic;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XNodeSetForDOM;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XRTreeFrag;
import org.apache.xpath.objects.XString;
import org.apache.xpath.operations.Variable;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import xml.xpath31.processor.types.XSAnyAtomicType;

/**
 * Implementation of XSLT xsl:apply-templates element.
 * 
 * Ref : https://www.w3.org/TR/xslt-30/#element-apply-templates
 * 
 * @xsl.usage advanced
 */
public class ElemApplyTemplates extends ElemCallTemplate
{
  
  static final long serialVersionUID = 2903125371542621004L;

  /**
   * mode %qname; #IMPLIED
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
   * 
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
   * This function is called after everything else has been
   * recomposed, and allows the template to set remaining
   * values that may be based on some other property that
   * depends on recomposition.
   */
  public void compose(StylesheetRoot sroot) throws TransformerException
  {
    super.compose(sroot);
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
   *
   * @throws TransformerException
   */
  public void execute(TransformerImpl transformer) throws TransformerException
  {

    transformer.pushCurrentTemplateRuleIsNull(false);

    boolean pushMode = false;

    try
    {
      QName mode = transformer.getMode();

      if (!m_isDefaultTemplate)
      {
        if (((null == mode) && (null != m_mode))
                || ((null != mode) &&!mode.equals(m_mode)))
        {
          pushMode = true;

          transformer.pushMode(m_mode);
        }
      }
      if (transformer.getDebug())
        transformer.getTraceManager().emitTraceEvent(this);

      transformSelectedNodes(transformer);
    }
    finally
    {
      if (transformer.getDebug())
        transformer.getTraceManager().emitTraceEndEvent(this);

      if (pushMode)
        transformer.popMode();

      transformer.popCurrentTemplateRuleIsNull();
    }
  }

  
  /**
   * Perform a query if needed, and call transformNode for each child.
   *
   * @param transformer non-null reference to the the current transform-time state.
   *
   * @throws TransformerException Thrown in a variety of circumstances.
   * @xsl.usage advanced
   */
  public void transformSelectedNodes(TransformerImpl transformer) throws TransformerException
  {

    final XPathContext xctxt = transformer.getXPathContext();
    final int sourceNode = xctxt.getCurrentNode();
    
    DTMIterator sourceNodes = null;
    
    SourceLocator srcLocator = xctxt.getSAXLocator();
    
    XObject varEvalResult = null;
    
    ResultSequence resultSeq = null;
    
    if (m_selectExpression instanceof XPathSequenceConstructor) {
    	resultSeq = (ResultSequence)(((XPathSequenceConstructor)m_selectExpression).execute(xctxt));
    	
    	if (isAllSeqItemsXdmAtomicValues(resultSeq)) {
    		// Added for XSLT 3.0    		
    		executeXslTransformAtomicValueSeq(transformer, xctxt, resultSeq);
    		
    		return;
    	}
    	else {
    		XNodeSet nodeSet = XslTransformEvaluationHelper.getXNodeSetFromResultSequence(resultSeq, xctxt);
    		if (nodeSet != null) {
    			sourceNodes = nodeSet.asIterator(xctxt, sourceNode);   
    		}
    		else {
    			return;
    		}
    	}
    }    
    else if (m_selectExpression instanceof Variable) {
       varEvalResult = ((Variable)m_selectExpression).execute(xctxt);
       
       if (varEvalResult instanceof ResultSequence) {
    	   resultSeq = (ResultSequence)varEvalResult;
    	   
    	   if (isAllSeqItemsXdmAtomicValues(resultSeq)) {
    		   executeXslTransformAtomicValueSeq(transformer, xctxt, resultSeq);

    		   return;
    	   }
    	   else {
    		   XNodeSet nodeSet = XslTransformEvaluationHelper.getXNodeSetFromResultSequence(resultSeq, xctxt);
    		   if (nodeSet != null) {
    			   sourceNodes = nodeSet.asIterator(xctxt, sourceNode);   
    		   }
    		   else {
    			   return;
    		   }
    	   }
       }
       else if (varEvalResult instanceof XNodeSet) {
    	   sourceNodes = ((XNodeSet)varEvalResult).asIterator(xctxt, sourceNode);
       }
       else {
    	   throw new TransformerException("XTTE0505 : xsl:apply-templates 'select' expression evaluation "
    	   		                                 + "resulted in a value that is not supported to be processed.", srcLocator);
       }
    }
    else {
       varEvalResult = m_selectExpression.execute(xctxt);
       
       if (varEvalResult instanceof ResultSequence) {
    	   resultSeq = (ResultSequence)varEvalResult;
    	   
    	   if (isAllSeqItemsXdmAtomicValues(resultSeq)) {
    		   executeXslTransformAtomicValueSeq(transformer, xctxt, resultSeq);

    		   return;
    	   }
    	   else {
    		   XNodeSet nodeSet = XslTransformEvaluationHelper.getXNodeSetFromResultSequence(resultSeq, xctxt);    		   
    		   if (nodeSet != null) {
    			   sourceNodes = nodeSet.asIterator(xctxt, sourceNode);   
    		   }
    		   else {
    			   return;
    		   }
    	   } 
       }
       else if (varEvalResult instanceof XNodeSet) {
    	   sourceNodes = ((XNodeSet)varEvalResult).asIterator(xctxt, sourceNode);
       }
       else if (isXdmItemAtomicValue(varEvalResult)) {
    	   // Added for XSLT 3.0    	   
    	   final StylesheetRoot sroot1 = transformer.getStylesheet();
    	   final TemplateList tlOne = sroot1.getTemplateListComposed();
    	   ElemTemplate template = tlOne.getTemplate(".");
    	   
    	   xctxt.setXPath3ContextItem(varEvalResult);
    	   
    	   for (ElemTemplateElement t = template.m_firstChild; t != null; t = t.m_nextSibling) {
    		   xctxt.setSAXLocator(t);
    		   transformer.pushElemTemplateElement(t);    		   
    		   t.execute(transformer);
    		   transformer.popElemTemplateElement();
    	   }

    	   return;
       }
       else {
    	   throw new TransformerException("XTTE0505 : xsl:apply-templates 'select' expression evaluation "
    	   		                                 											+ "resulted in a value that is not "
    	   		                                 											+ "supported to be processed.", srcLocator);
       }
    }
    
    VariableStack vars = xctxt.getVarStack();
    int nParams = getParamElemCount();
    int thisframe = vars.getStackFrame();
    StackGuard guard = transformer.getStackGuard();
    boolean check = (guard.getRecursionLimit() > -1) ? true : false;
    
    boolean pushContextNodeListFlag = false;
      
    try
    {
      xctxt.pushCurrentNode(DTM.NULL);
      xctxt.pushCurrentExpressionNode(DTM.NULL);
      xctxt.pushSAXLocatorNull();
      transformer.pushElemTemplateElement(null);
      final Vector keys = (m_sortElems == null)
                          ? null
                          : transformer.processSortKeys(this, sourceNode);

      // Sort if we need to
      if (null != keys)
        sourceNodes = sortNodes(xctxt, keys, sourceNodes);
            
      if (transformer.getDebug())
      {
        transformer.getTraceManager().emitSelectedEvent(sourceNode, this,
                "select", new XPath(m_selectExpression),
                new org.apache.xpath.objects.XNodeSet(sourceNodes));
      }

      final SerializationHandler rth = transformer.getSerializationHandler();
      final StylesheetRoot sroot = transformer.getStylesheet();
      final TemplateList tl = sroot.getTemplateListComposed();
      
      final boolean quiet = transformer.getQuietConflictWarnings();
      
      // Should be able to get this from the iterator 
      // but there might be a codebase issue.
      DTM dtm = xctxt.getDTM(sourceNode);
      
      int argsFrame = -1;
      if(nParams > 0)
      {
        // This code will create a section on the stack that is all the 
        // evaluated arguments. These will be copied into the real params 
        // section of each called template.
        argsFrame = vars.link(nParams);
        vars.setStackFrame(thisframe);
        
        int maxParamStackFrameIndex = -1;
        
        for (int i = 0; i < nParams; i++) 
        {
          ElemWithParam ewp = m_paramElems[i];
          if (transformer.getDebug())
            transformer.getTraceManager().emitTraceEvent(ewp);
          
          XObject obj = null;
          
          try {
             obj = ewp.getValue(transformer, sourceNode);
          }
          catch (TransformerException ex) {
             throw new TransformerException(ex.getMessage(), srcLocator);   
          }
          
          if (transformer.getDebug())
            transformer.getTraceManager().emitTraceEndEvent(ewp);
          
          String tunnelStrVal = ewp.getTunnel();
          obj.setTunnel(tunnelStrVal);
          QName qName = ewp.getName();
          obj.setQName(qName);
          
          vars.setLocalVariable(i, obj, argsFrame);
          maxParamStackFrameIndex = i;
        }
        
        for (int idx = 0; idx < MAX_PARAM_LIMIT; idx++) {
        	XObject obj = vars.getLocalVariable(idx, thisframe);
        	if (obj != null) {
        		String tunnelValStr = obj.getTunnel();
        		if ((obj.getTunnel() != null) && XslTransformEvaluationHelper.isTunnelAttributeYes(tunnelValStr)) {
        			vars.setLocalVariable(++maxParamStackFrameIndex, obj, argsFrame);
        		}
        	}
        	else {
        		break; 
        	}
        }

        ElemTemplateElement parentElem = getParentElem();
        while (!(parentElem instanceof ElemTemplate)) {
        	parentElem = parentElem.getParentElem(); 
        }

        List<XObject> tunnelParamObjList = parentElem.getTunnelParamObjList();
        for (int idx = 0; idx < tunnelParamObjList.size(); idx++) {
        	XObject var = tunnelParamObjList.get(idx);
        	vars.setLocalVariable(++maxParamStackFrameIndex, var, argsFrame);
        }
        
        vars.setStackFrame(argsFrame);
      }
      
      xctxt.pushContextNodeList(sourceNodes);
      pushContextNodeListFlag = true;
      
      IntStack currentNodes = xctxt.getCurrentNodeStack();
      
      IntStack currentExpressionNodes = xctxt.getCurrentExpressionNodeStack();
      
      int child;
      while (DTM.NULL != (child = sourceNodes.nextNode()))
      {
        currentNodes.setTop(child);
        currentExpressionNodes.setTop(child);

        if(xctxt.getDTM(child) != dtm)
        {
          dtm = xctxt.getDTM(child);
        }
        
        final int exNodeType = dtm.getExpandedTypeID(child);

        final int nodeType = dtm.getNodeType(child);

        final QName mode = transformer.getMode();

        ElemTemplate template = tl.getTemplateFast(xctxt, child, exNodeType, mode, -1, quiet, dtm);

        // If that didn't locate a node, fall back to a default template rule.
        // See http://www.w3.org/TR/xslt#built-in-rule.
        if (template == null)
        {
          switch (nodeType)
          {
          case DTM.DOCUMENT_FRAGMENT_NODE :
          case DTM.ELEMENT_NODE :
            template = sroot.getDefaultRule();
            break;
          case DTM.ATTRIBUTE_NODE :
          case DTM.CDATA_SECTION_NODE :
          case DTM.TEXT_NODE :
            transformer.pushPairCurrentMatched(sroot.getDefaultTextRule(), child);
            transformer.setCurrentElement(sroot.getDefaultTextRule());
            dtm.dispatchCharactersEvents(child, rth, false);
            transformer.popCurrentMatched();
            continue;
          case DTM.DOCUMENT_NODE :
            template = sroot.getDefaultRootRule();
            break;
          default :
            continue;
          }
        }
        else
        {
        	transformer.setCurrentElement(template);
        }
                
        transformer.pushPairCurrentMatched(template, child);
        if (check)
	        guard.checkForInfiniteLoop();

        int currentFrameBottom;
        if(template.m_frameSize > 0)
        {
          xctxt.pushRTFContext();
          currentFrameBottom = vars.getStackFrame();
          vars.link(template.m_frameSize);
          
          if (template.m_inArgsSize > 0)
          {
            int paramIndex = 0;
            for (ElemTemplateElement elem = template.getFirstChildElem(); 
                 null != elem; elem = elem.getNextSiblingElem()) 
            {
              if(Constants.ELEMNAME_PARAMVARIABLE == elem.getXSLToken())
              {
                ElemParam ep = (ElemParam)elem;
                
                int i;
                for (i = 0; i < nParams; i++) 
                {
                  ElemWithParam ewp = m_paramElems[i];
                  if (ewp.m_qnameID == ep.m_qnameID)                  
                  {
                      XObject obj = vars.getLocalVariable(i, argsFrame);
                      vars.setLocalVariable(paramIndex, obj);
                      break;
                  }
                }
                
                // The below code before commenting has been causing, issues 
                // implementing XSLT tunnel parameters. Commenting this seems 
                // to be working fine for non-tunnel parameter use cases as 
                // well. Pls review.
                /*if (i == nParams) {
                  vars.setLocalVariable(paramIndex, null);*/                
              }
              else
                break;
              paramIndex++;
            }                        
          }                    
        }
        else {
           currentFrameBottom = 0;
        }

        if (transformer.getDebug()) {
           transformer.getTraceManager().emitTraceEvent(template);
        }

        String templateAsAttrVal = template.getAs();
        
        if (templateAsAttrVal != null) {         
            try {
               // Check whether an xsl:template element's result contents conform to the 
               // SequenceType expression specified as value of xsl:template element's 
               // 'as' attribute.
                 
               int dtmNodeHandle = transformer.transformToGlobalRTF(template);
                
               NodeList nodeList = (new XRTreeFrag(dtmNodeHandle, xctxt, template)).convertToNodeset();             
               XObject templateEvalResultForAsAttr = new XNodeSetForDOM(nodeList, xctxt);
               
               templateEvalResultForAsAttr = SequenceTypeSupport.castXdmValueToAnotherType(templateEvalResultForAsAttr, templateAsAttrVal, 
                                                                                                                                          null, xctxt);
               if (templateEvalResultForAsAttr != null) {
                   SerializationHandler handler = transformer.getSerializationHandler();        
                   
                   try {
                        if (templateEvalResultForAsAttr instanceof XNodeSet) {
                           ElemCopyOf.copyOfActionOnNodeSet((XNodeSet)templateEvalResultForAsAttr, transformer, 
                                                                                                            handler, xctxt);
                        }
                        else {
                           ElemCopyOf.copyOfActionOnResultSequence((ResultSequence)templateEvalResultForAsAttr, 
                                                                                                            transformer, handler, xctxt, false); 
                        }
                    } 
                    catch (TransformerException ex) {
                        throw new TransformerException(ex.getMessage(), srcLocator); 
                    } 
                    catch (SAXException ex) {
                        transformer.getErrorListener().fatalError(new TransformerException(ex)); 
                    }   
               }
               else {
                   QName m_name = template.getName();
                   String m_matchPatternStr = (template.getMatch()).getPatternString();
                   String errTemplateStr = (m_name != null) ? m_name.toString() : m_matchPatternStr; 
                   throw new TransformerException("XTTE0505 : The required result type of template " + errTemplateStr 
                                                                                                 + " is " + templateAsAttrVal + ". But the template "
                                                                                                 + "result doesn't conform to this required type.", srcLocator);  
               }
            }
            catch (TransformerException ex) {
               String trfExpMessage = ex.getMessage();
               if ((trfExpMessage != null) && trfExpMessage.startsWith("XTTE0590")) {
            	   throw ex;   
               }
               else {
	               QName m_name = template.getName();
	               String m_matchPatternStr = (template.getMatch()).getPatternString();
	               String errTemplateStr = (m_name != null) ? m_name.toString() : m_matchPatternStr; 
	               throw new TransformerException("XTTE0505 : The required result type of template " + errTemplateStr 
	                                                                                                 + " is " + templateAsAttrVal + ". But the template "
	                                                                                                 + "result doesn't conform to this required type.", srcLocator);
               }
            }  
        }
        else {
            // Evaluate the child templates.
            // Loop through the children of the template, evaluating 
            // each of them.
            for (ElemTemplateElement t = template.m_firstChild; 
                                                        t != null; t = t.m_nextSibling) {
              xctxt.setSAXLocator(t);
              
              try
              {
              	  transformer.pushElemTemplateElement(t);
              	  t.execute(transformer);
              }
              finally
              {
              	  transformer.popElemTemplateElement();
              }
            }
        }
        
        if (transformer.getDebug()) {
	      transformer.getTraceManager().emitTraceEndEvent(template);
        }
	    
        if(template.m_frameSize > 0)
        {
          vars.unlink(currentFrameBottom);
          xctxt.popRTFContext();
        }
          
        transformer.popCurrentMatched();
        
      } // end while (DTM.NULL != (child = sourceNodes.nextNode()))
    }
    catch (SAXException se)
    {
      transformer.getErrorListener().fatalError(new TransformerException(se));
    }
    finally
    {
      if (transformer.getDebug())
        transformer.getTraceManager().emitSelectedEndEvent(sourceNode, this,
                "select", new XPath(m_selectExpression),
                new org.apache.xpath.objects.XNodeSet(sourceNodes));
      
      // Unlink to the original stack frame  
      if(nParams > 0)
        vars.unlink(thisframe);
      xctxt.popSAXLocator();
      if (pushContextNodeListFlag) xctxt.popContextNodeList();
      transformer.popElemTemplateElement();
      xctxt.popCurrentExpressionNode();
      xctxt.popCurrentNode();
      sourceNodes.detach();
    }
  }

  /**
   * This method definition does, an XSL transformation of a 
   * sequence of XDM atomic values.
   */
  private void executeXslTransformAtomicValueSeq(TransformerImpl transformer, final XPathContext xctxt,
		  														ResultSequence resultSeq) throws TransformerException {
	  
	  final StylesheetRoot sroot = transformer.getStylesheet();
	  final TemplateList tl = sroot.getTemplateListComposed();
	  ElemTemplate template = tl.getTemplate(".");    		    		

	  for (int idx = 0; idx < resultSeq.size(); idx++) {
		  XObject xObj = resultSeq.item(idx);
		  xctxt.setXPath3ContextItem(xObj);
		  for (ElemTemplateElement t = template.m_firstChild; 
				                                         t != null; t = t.m_nextSibling) {
			  xctxt.setSAXLocator(t);
			  transformer.pushElemTemplateElement(t);    		   
			  t.execute(transformer);
			  transformer.popElemTemplateElement();
		  }
	  }
  }

  /**
   * Check whether all XDM items within the supplied ResultSequence object 
   * are atomic values. 
   */
  private boolean isAllSeqItemsXdmAtomicValues(ResultSequence resultSeq) {
	  boolean result = true;
	
	  for (int idx = 0; idx < resultSeq.size(); idx++) {
		 XObject xObj = resultSeq.item(idx);
		 if (!isXdmItemAtomicValue(xObj)) {
			result = false;
			break;
		 }
	  }
	  
	  return result;
  }
  
  /**
   * Check whether an supplied XDM item is an atomic value.
   */
  private boolean isXdmItemAtomicValue(XObject xObj) {
	  boolean result = false;
	  
	  if ((xObj instanceof XNumber) || (xObj instanceof XBoolean) || 
		  (xObj instanceof XBooleanStatic) || (xObj instanceof XString) || (xObj instanceof XSAnyAtomicType)) {
	      result = true;
	  }
	  
	  return result;
  }

}
