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
package org.apache.xalan.templates;

import java.util.List;
import java.util.Vector;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.serialize.SerializerUtils;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xalan.xslt.util.XslTransformSharedDatastore;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xml.serializer.SerializationHandler;
import org.apache.xpath.Expression;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.axes.LocPathIterator;
import org.apache.xpath.axes.SelfIteratorNoPredicate;
import org.apache.xpath.compiler.XPathParser;
import org.apache.xpath.composite.SequenceTypeData;
import org.apache.xpath.composite.XPathNamedFunctionReference;
import org.apache.xpath.functions.Function;
import org.apache.xpath.functions.XSL3ConstructorOrExtensionFunction;
import org.apache.xpath.functions.XSL3FunctionService;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XNodeSetForDOM;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathInlineFunction;
import org.apache.xpath.objects.XPathMap;
import org.apache.xpath.objects.XRTreeFrag;
import org.apache.xpath.objects.XString;
import org.apache.xpath.operations.Operation;
import org.apache.xpath.operations.Range;
import org.apache.xpath.operations.SimpleMapOperator;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import xml.xpath31.processor.types.XSAnyAtomicType;
import xml.xpath31.processor.types.XSNumericType;

/**
 * Implementation of XSL 3 xsl:sequence element.
 * 
 * Ref : https://www.w3.org/TR/xslt-30/#element-sequence
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 *
 * @xsl.usage advanced
 */
public class ElemSequence extends ElemTemplateElement
{

  private static final long serialVersionUID = 4299774714317634314L;
  
  /**
   * Boolean value to indicate that, whether xsl:sequence instruction 
   * is called from xsl:fork instruction. 
   */
  private boolean m_isCalledFromXslFork;
  
  /**
   * xsl:sequence instruction's evaluation result. This is useful
   * when xsl:sequence instruction is called from xsl:fork 
   * instruction.
   */
  private XObject m_xslSequenceEvalResult = null;

  /**
   * Default constructor.
   */
  public ElemSequence() {
	  // NO OP
  }
  
  /**
   * This class field is used during, XPath.fixupVariables(..) action 
   * as performed within object of this class.  
   */    
  private Vector m_vars;
  
  /**
   * This class field is used during, XPath.fixupVariables(..) action 
   * as performed within object of this class.  
   */
  private int m_globals_size;

  /**
   * The value of the "select" attribute.
   */
  private XPath m_selectPattern;
  
  /**
   * We use this string value constant to add as a suffix to, a string value that
   * is getting serialized by Xalan-J serializer, when XSL serialization is
   * getting done during evaluation of xsl:sequence instruction. This helps us
   * construct later during XSL transformation process, a sequence of xdm atomic
   * values.
   * 
   * We're assuming that, this string value is significantly random, and is unlikely
   * to exist within an input document that is getting transformed by Xalan-J.
   */
  public static String STRING_VAL_SERIALIZATION_SUFFIX = "XSL_SEQ" + ElemCopyOf.SPACE_CHAR + "XSL_SEQ";

  /**
   * Set the value of "select" attribute.
   *
   * @param v  Value to set for the "select" attribute.
   */
  public void setSelect(XPath v) {
      m_selectPattern = v;
  }

  /**
   * Get the value of "select" attribute.
   * 
   * @return Value of the "select" attribute.
   */
  public XPath getSelect() {
     return m_selectPattern;
  }

  /**
   * Get an integer representation of the element type.
   *
   * @return An integer representation of the element, defined in the
   *         Constants class.
   */
  public int getXSLToken() {
      return Constants.ELEMNAME_SEQUENCE;
  }

  /**
   * Return the node name.
   *
   * @return The node name
   */
  public String getNodeName() {
      return Constants.ELEMNAME_SEQUENCE_STRING;
  }

  /**
   * @param transformer non-null reference to the the current transform-time state.
   *
   * @throws TransformerException
   */
  public void execute(TransformerImpl transformer) throws TransformerException {

      int sourceNode = transformer.getXPathContext().getCurrentNode();
  
      processXslSequence(transformer, sourceNode);       
  }

  /**
   * This method, helps to evaluate an xsl:sequence instruction.
   * 
   * @param transformer non-null reference to the the current transform-time state.
   * @param sourceNode non-null reference to the current source node.
   *
   * @throws TransformerException
   */
  public void processXslSequence(TransformerImpl transformer, int sourceNode) 
                                                                           throws TransformerException
  {

    XObject xslSequenceVal = null;
    
    XPathContext xctxt = transformer.getXPathContext();

    xctxt.pushCurrentNode(sourceNode);
    
    SourceLocator srcLocator = xctxt.getSAXLocator();
    
    Expression selectExpression = null;
 
    try {        
      if (m_selectPattern != null) {
    	 ElemTemplateElement xslSeqChildElem = getFirstChildElem();
    	 while (xslSeqChildElem != null) {
    	   if (!(xslSeqChildElem instanceof ElemFallback)) {
    		  throw new javax.xml.transform.TransformerException("XTSE3185 : An xsl:sequence element cannot "
    		  		                                                   + "have both \"select\" attribute, and child content "
    		  		                                                   + "other than xsl:fallback element(s).", srcLocator); 
    	   }
    	   else {
    		  xslSeqChildElem = xslSeqChildElem.getNextSiblingElem(); 
    	   }
    	}
    	  
        selectExpression = m_selectPattern.getExpression();
        
        if (selectExpression instanceof XSL3ConstructorOrExtensionFunction) {
        	XSL3ConstructorOrExtensionFunction xpathFunc = (XSL3ConstructorOrExtensionFunction)selectExpression;
        	XSL3FunctionService xslFunctionService = xctxt.getXSLFunctionService();
            XObject evalResult = xslFunctionService.callFunction(xpathFunc, transformer, xctxt);
            if (evalResult != null) {
               xslSequenceVal = evalResult;    
            }
            else {
               xslSequenceVal = m_selectPattern.execute(xctxt, sourceNode, this);    
            }            
        }
        else if ((selectExpression instanceof Function) || 
                                                     (selectExpression instanceof SimpleMapOperator) || 
                                                     (selectExpression instanceof Range)) {
            xslSequenceVal = selectExpression.execute(xctxt);
        }
        else if (selectExpression instanceof Operation) {
            Operation xpathOperation = (Operation)selectExpression;            
            XObject leftOperand = (xpathOperation.getLeftOperand()).execute(xctxt);
            XObject rightOperand = (xpathOperation.getRightOperand()).execute(xctxt);
            xslSequenceVal = xpathOperation.operate(leftOperand, rightOperand);
        }
        else if (selectExpression instanceof SelfIteratorNoPredicate) {
            xslSequenceVal = xctxt.getXPath3ContextItem();
        }
        else if (selectExpression instanceof LocPathIterator) {                        
            int contextNode = xctxt.getContextNode();
            
            LocPathIterator locPathIterator = (LocPathIterator)selectExpression;
            
            DTMCursorIterator dtmIter = null;                     
            try {
                dtmIter = locPathIterator.asIterator(xctxt, contextNode);
            }
            catch (ClassCastException ex) {
                // no op
            }
            
            if (dtmIter != null) {               
               xslSequenceVal = new XMLNodeCursorImpl(dtmIter);
            }
            else {
               ResultSequence resultSeq = new ResultSequence();
                
               String xpathExprStr = m_selectPattern.getPatternString();
               
               if (xpathExprStr.startsWith("$") && xpathExprStr.contains("[") && 
                                                                       xpathExprStr.endsWith("]")) {
                   ElemTemplateElement elemTemplateElement = (ElemTemplateElement)xctxt.getNamespaceContext();
                   List<XMLNSDecl> prefixTable = null;
                   if (elemTemplateElement != null) {
                       prefixTable = (List<XMLNSDecl>)elemTemplateElement.getPrefixTable();
                   }                                      
                   
                   String varRefXPathExprStr = "$" + xpathExprStr.substring(1, xpathExprStr.indexOf('['));
                   String xpathIndexExprStr = xpathExprStr.substring(xpathExprStr.indexOf('[') + 1, 
                                                                                        xpathExprStr.indexOf(']'));

                   // Evaluate the, variable reference XPath expression
                   if (prefixTable != null) {
                       varRefXPathExprStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(
                                                                                                          varRefXPathExprStr, 
                                                                                                          prefixTable);
                   }                                      

                   XPath varXPathObj = new XPath(varRefXPathExprStr, srcLocator, xctxt.getNamespaceContext(), 
                                                                                                         XPath.SELECT, null);
                   if (m_vars != null) {
                      varXPathObj.fixupVariables(m_vars, m_globals_size);  
                   }
                   
                   XObject varEvalResult = varXPathObj.execute(xctxt, xctxt.getCurrentNode(), xctxt.getNamespaceContext());

                   // Evaluate the, xdm sequence index XPath expression
                   if (prefixTable != null) {
                       xpathIndexExprStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(
                                                                                                         xpathIndexExprStr, 
                                                                                                         prefixTable);
                   }

                   XPath xpathIndexObj = new XPath(xpathIndexExprStr, srcLocator, xctxt.getNamespaceContext(), 
                                                                                                          XPath.SELECT, null);
                   
                   if (m_vars != null) {
                      xpathIndexObj.fixupVariables(m_vars, m_globals_size);  
                   }
                   
                   XObject seqIndexEvalResult = xpathIndexObj.execute(xctxt, xctxt.getCurrentNode(), 
                                                                                                xctxt.getNamespaceContext());

                   if (varEvalResult instanceof ResultSequence) {
                       ResultSequence varEvalResultSeq = (ResultSequence)varEvalResult; 

                       if (seqIndexEvalResult instanceof XNumber) {
                           double dValIndex = ((XNumber)seqIndexEvalResult).num();
                           if (dValIndex == (int)dValIndex) {
                               XObject evalResult = varEvalResultSeq.item((int)dValIndex - 1);
                               resultSeq.add(evalResult);
                           }
                           else {
                               throw new javax.xml.transform.TransformerException("XPTY0004 : an index value used with an xdm "
                                                                                                               + "sequence reference, is not an integer.", srcLocator); 
                           }
                       }
                       else if (seqIndexEvalResult instanceof XSNumericType) {
                           String indexStrVal = ((XSNumericType)seqIndexEvalResult).stringValue();
                           double dValIndex = (Double.valueOf(indexStrVal)).doubleValue();
                           if (dValIndex == (int)dValIndex) {
                               XObject evalResult = varEvalResultSeq.item((int)dValIndex - 1);
                               resultSeq.add(evalResult);
                           }
                           else {
                               throw new javax.xml.transform.TransformerException("XPTY0004 : an index value used with an xdm "
                                                                                                               + "sequence reference, is not an integer.", srcLocator);  
                           }
                       }
                       else {
                           throw new javax.xml.transform.TransformerException("XPTY0004 : an index value used with an xdm sequence "
                                                                                                               + "reference, is not numeric.", srcLocator);   
                       }
                   }
               }
               
               xslSequenceVal = resultSeq;
            }
        }
  
        if (xslSequenceVal == null) {
           xslSequenceVal = m_selectPattern.execute(xctxt, sourceNode, this);
           if (xslSequenceVal instanceof XPathNamedFunctionReference) {
         	  XslTransformSharedDatastore.xpathNamedFunctionReference = (XPathNamedFunctionReference)xslSequenceVal;  
           }
        }        
      }
      else if (getFirstChildElem() == null) {
         xslSequenceVal = XString.EMPTYSTRING;
      }
      else {
   		 int sequenceConstructDtmHandle = transformer.transformToRTF(this);
		 NodeList nodeList = (new XRTreeFrag(sequenceConstructDtmHandle, xctxt, this)).convertToNodeset();		
		 xslSequenceVal = new XNodeSetForDOM(nodeList, xctxt); 
      }
      
      if (xslSequenceVal == null) {
          // The result of evaluation of xsl:sequence instruction here,
          // is an empty sequence.
          XPath emptySeqXPath = new XPath(XPathParser.XPATH_EXPR_STR_EMPTY_SEQUENCE, srcLocator, 
                                                                                  null, XPath.SELECT, null);
          xslSequenceVal = emptySeqXPath.execute(xctxt, DTM.NULL, null);
      }
      
      if (m_isCalledFromXslFork) {
           m_xslSequenceEvalResult = xslSequenceVal;
      }            
      else if ((xslSequenceVal != null) && (XslTransformSharedDatastore.xpathNamedFunctionReference == null)) {
    	  emitXslSequenceResultToSerializer(xctxt, transformer, xslSequenceVal);          
       }
    }
    catch (SAXException se) {
       throw new TransformerException(se);
    }
    finally {      
       xctxt.popCurrentNode();
    }   
  }
  
  /**
   * A method definition to emit contents of xsl:sequence instruction's 
   * evaluation result, to XSL transformation serializer.
   * 
   * @param xctxt							an XPathContext object 
   * @param transformer						an TransformerImpl object instance
   * @param xslSequenceVal					an XObject object instance, representing 
   *                                        result of evaluation of the xsl:sequence instruction
   * @throws TransformerException
   * @throws SAXException
   */
  public void emitXslSequenceResultToSerializer(XPathContext xctxt, TransformerImpl transformer, 
		                                                                                  XObject xslSequenceVal)
		                                                                         throws TransformerException, SAXException {
	  if (xslSequenceVal instanceof XPathInlineFunction) {
		  XslTransformSharedDatastore.xpathInlineFunction = (XPathInlineFunction)xslSequenceVal;   
	  }
	  else if (xslSequenceVal instanceof XPathMap) {
		  XslTransformSharedDatastore.xpathMap = (XPathMap)xslSequenceVal;
	  }
	  else {
		  SerializationHandler handler = transformer.getSerializationHandler();

		  int xObjectType = xslSequenceVal.getType();
		  String strVal = null;

		  switch (xObjectType) {           
		  case XObject.CLASS_NODESET :          
			  ElemCopyOf.copyOfActionOnNodeSet((XMLNodeCursorImpl)xslSequenceVal, transformer, handler, xctxt);          
			  break;
		  case XObject.CLASS_RTREEFRAG :
			  SerializerUtils.outputResultTreeFragment(handler, xslSequenceVal, xctxt);
			  break;
		  case XObject.CLASS_RESULT_SEQUENCE :         
			  ResultSequence resultSequence = (ResultSequence)xslSequenceVal;          
			  ElemCopyOf.copyOfActionOnResultSequence(resultSequence, transformer, handler, xctxt, true);          
			  break;
		  default :
			  // NO OP
		  }

		  boolean isToAddStrValSerializationSuffix = isToAddStrValSerializationSuffix(xctxt); 

		  if ((xslSequenceVal instanceof XBoolean) || (xslSequenceVal instanceof XNumber) || 
				  (xslSequenceVal instanceof XString)) {
			  if (isToAddStrValSerializationSuffix) {
				  strVal = xslSequenceVal.str() + STRING_VAL_SERIALIZATION_SUFFIX;
			  }
			  else {
				  strVal = xslSequenceVal.str();  
			  }

			  handler.characters(strVal.toCharArray(), 0, strVal.length());
		  }
		  else if (xslSequenceVal instanceof XSAnyAtomicType) {
			  if (isToAddStrValSerializationSuffix) {
				  strVal = ((XSAnyAtomicType)xslSequenceVal).stringValue() + STRING_VAL_SERIALIZATION_SUFFIX;
			  }
			  else {
				  strVal = ((XSAnyAtomicType)xslSequenceVal).stringValue();
			  }

			  handler.characters(strVal.toCharArray(), 0, strVal.length());
		  }
	  }
  }

  /**
   * This function is called after everything else has been
   * recomposed, and allows the template to set remaining
   * values that may be based on some other property that
   * depends on recomposition.
   */
  public void compose(StylesheetRoot sroot) throws TransformerException {    
     StylesheetRoot.ComposeState cstate = sroot.getComposeState();
    
     java.util.Vector vnames = cstate.getVariableNames();
        
     m_vars = (Vector)vnames.clone();
     m_globals_size = cstate.getGlobalsSize();
        
     if (m_selectPattern != null) {
        m_selectPattern.fixupVariables(vnames, cstate.getGlobalsSize());
     }
    
     super.compose(sroot);
  }
  
  /**
   * This after the template's children have been composed.
   */
  public void endCompose(StylesheetRoot sroot) throws TransformerException
  {
     super.endCompose(sroot);
  }
  
  /**
   * Set the parent as an ElemTemplateElement.
   *
   * @param p This node's parent as an ElemTemplateElement
   */
  public void setParentElem(ElemTemplateElement p)
  {
    super.setParentElem(p);
  }

  
  /**
   * Call the children visitors.
   * 
   * @param visitor The visitor whose appropriate method will be called.
   */
  protected void callChildVisitors(XSLTVisitor visitor, boolean callAttrs)
  {
  	if (m_selectPattern != null) {
  	   m_selectPattern.getExpression().callVisitors(m_selectPattern, visitor);
  	}
  	
    super.callChildVisitors(visitor, callAttrs);
  }
  
  /**
   * This method determines that, should a Xalan-J serialization suffix be added, when
   * emitting xdm atomic values to an XSLT result sequence.
   */
  public boolean isToAddStrValSerializationSuffix(XPathContext xctxt) throws TransformerException {
     
     boolean isToAddStrValSerializationSuffix = true;
     
     ElemTemplateElement elemTemplateElem = getParentElem();
     
     String asAttrStrVal = null;
     
     while (elemTemplateElem != null) {
        if (elemTemplateElem instanceof ElemFunction) {
           asAttrStrVal = ((ElemFunction)elemTemplateElem).getAs();
           break;
        }
        else if (elemTemplateElem instanceof ElemVariable) {
           asAttrStrVal = ((ElemVariable)elemTemplateElem).getAs();
           break;
        }
        else {
           elemTemplateElem = elemTemplateElem.getParentElem();  
        }
     }
     
     if (asAttrStrVal != null) {
         XPath seqTypeXPath = new XPath(asAttrStrVal, xctxt.getSAXLocator(), 
                                                                        xctxt.getNamespaceContext(), 
                                                                        XPath.SELECT, null, true);
         XObject seqTypeExpressionEvalResult = seqTypeXPath.execute(xctxt, xctxt.getContextNode(), 
                                                                             xctxt.getNamespaceContext());
         SequenceTypeData seqExpectedTypeData = (SequenceTypeData)seqTypeExpressionEvalResult;
         if (seqExpectedTypeData.getSequenceTypeKindTest() != null) {
            isToAddStrValSerializationSuffix = false; 
         }
     }
     else {
         isToAddStrValSerializationSuffix = false;
     }
     
     return isToAddStrValSerializationSuffix; 
  }

  public boolean getIsCalledFromXslFork() {
	  return m_isCalledFromXslFork;
  }

  public void setIsCalledFromXslFork(boolean isCalledFromXslFork) {
	  this.m_isCalledFromXslFork = isCalledFromXslFork;
  }

  public XObject getXslSequenceEvalResult() {
	  return m_xslSequenceEvalResult;
  }

  public void setXslSequenceEvalResult(XObject xslSequenceEvalResult) {
	  this.m_xslSequenceEvalResult = xslSequenceEvalResult;
  }

}
