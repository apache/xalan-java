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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xalan.xslt.util.XslTransformSharedDatastore;
import org.apache.xml.dtm.ref.DTMNodeList;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xml.utils.QName;
import org.apache.xpath.VariableStack;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.composite.SequenceTypeData;
import org.apache.xpath.composite.SequenceTypeSupport;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XNodeSetForDOM;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathInlineFunction;
import org.apache.xpath.objects.XRTreeFrag;
import org.apache.xpath.types.XSByte;
import org.apache.xpath.types.XSNegativeInteger;
import org.apache.xpath.types.XSNonNegativeInteger;
import org.apache.xpath.types.XSNonPositiveInteger;
import org.apache.xpath.types.XSPositiveInteger;
import org.apache.xpath.types.XSShort;
import org.apache.xpath.types.XSUnsignedByte;
import org.apache.xpath.types.XSUnsignedInt;
import org.apache.xpath.types.XSUnsignedLong;
import org.apache.xpath.types.XSUnsignedShort;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import xml.xpath31.processor.types.XSBoolean;
import xml.xpath31.processor.types.XSDate;
import xml.xpath31.processor.types.XSDateTime;
import xml.xpath31.processor.types.XSDayTimeDuration;
import xml.xpath31.processor.types.XSDecimal;
import xml.xpath31.processor.types.XSDouble;
import xml.xpath31.processor.types.XSDuration;
import xml.xpath31.processor.types.XSFloat;
import xml.xpath31.processor.types.XSInt;
import xml.xpath31.processor.types.XSInteger;
import xml.xpath31.processor.types.XSLong;
import xml.xpath31.processor.types.XSString;
import xml.xpath31.processor.types.XSTime;
import xml.xpath31.processor.types.XSYearMonthDuration;

/**
 * Implementation of XSLT 2.0+ xsl:function element.
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
   * Set the value of xsl:function's "name" 
   * attribute.
   */
  public void setName(QName qName)
  {      
      super.setName(qName);
  }

  /**
   * Get the value of xsl:function's "name" 
   * attribute. 
   */
  public QName getName()
  {
      return super.getName();
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
   * This method computes the result of xsl:function call, and returns 
   * result to the caller of this function.
   */
  public XObject evaluateXslFunction(TransformerImpl transformer, ResultSequence argSequence) throws TransformerException {
      
	  XObject result = null;
      
      XPathContext xctxt = transformer.getXPathContext();            
      
      SourceLocator srcLocator = xctxt.getSAXLocator();
      
      String funcLocalName = m_name.getLocalName();
      String funcNameSpaceUri = m_name.getNamespaceURI();
      
      // Validate few of the information of xsl:function's xsl:param declarations      
      Map<QName, Integer> xslParamMap = new HashMap<QName, Integer>();
      int idx = 0;
      PrefixResolver prefixResolver = xctxt.getNamespaceContext();
      for (ElemTemplateElement elem = getFirstChildElem(); elem != null; elem = elem.getNextSiblingElem()) {
         String nodeName = elem.getNodeName();         
         if (nodeName.contains(":")) {
            String nsPrefix = nodeName.substring(0, nodeName.indexOf(':'));
            String namespaceUri = prefixResolver.getNamespaceForPrefix(nsPrefix);
            String nodeLocalName = nodeName.substring(nodeName.indexOf(':') + 1);
            if ((Constants.ELEMNAME_PARAMVARIABLE_STRING).equals(nodeLocalName) && 
                                                                             (Constants.S_XSLNAMESPACEURL).equals(namespaceUri)) {
                String xslParamName = elem.getAttribute(Constants.ATTRNAME_NAME);
                if (xslParamMap.get(new QName(xslParamName)) == null) {
                   xslParamMap.put(new QName(xslParamName), Integer.valueOf(idx)); 
                }
                else {
                   throw new TransformerException("XPST0017 : The xsl:function " + "{" + funcNameSpaceUri + "}" + funcLocalName + 
                                                                             "(), has more than one xsl:param '" + xslParamName + "' declaration.", srcLocator); 
                }
            }
         }
         else if (elem instanceof ElemParam) {
             ElemParam elemParam = (ElemParam)elem;
             QName elemParamQName = elemParam.getName();
             if (xslParamMap.get(elemParamQName) == null) {
                xslParamMap.put(elemParamQName, Integer.valueOf(idx)); 
             }
             else {
                throw new TransformerException("XPST0017 : The xsl:function " + "{" + funcNameSpaceUri + "}" + funcLocalName + 
                                                                          "(), has more than one xsl:param '" + elemParamQName + "' declaration.", srcLocator); 
             } 
         }
         
         idx++;
      }
      
      if (xslParamMap.size() != argSequence.size()) {
         throw new TransformerException("XPST0017 : For the xsl:function " + "{" + funcNameSpaceUri + "}" + funcLocalName + 
                                                                       "(), the number of arguments provided with function call is not equal "
                                                                       + "to number of function's xsl:param declarations.", srcLocator);  
      }
      
      Collection<Integer> xslParamIdxs = xslParamMap.values();
      Object[] idxArr = xslParamIdxs.toArray();
      if (idxArr.length > 0) {
         Arrays.sort(idxArr);
         int currVal = ((Integer)idxArr[0]).intValue();
         if (currVal != 0) {
            throw new TransformerException("XPST0017 : For the xsl:function " + "{" + funcNameSpaceUri + "}" + funcLocalName + 
                                                                          "(), there's a non xsl:param declaration as first child "
                                                                          + "element of xsl:function.", srcLocator); 
         }
         
         for (int idx1 = 1; idx1 < idxArr.length; idx1++) {
            int nextVal = ((Integer)idxArr[idx1]).intValue();
            if (nextVal != (currVal + 1)) {
               throw new TransformerException("XPST0017 : For the xsl:function " + "{" + funcNameSpaceUri + "}" + funcLocalName + 
                                                                             "(), there's a non xsl:param declaration between two "
                                                                             + "xsl:param declarations.", srcLocator); 
            }
            else {
               currVal = nextVal;  
            }
         }
      }            
      
      if (xslParamMap.size() > 0) {
          // Add all of the xsl:function parameter value mappings to xpath context's
          // variable stack, after which the XSL instructions after xsl:param declarations
          // can dereference those parameters.                   
          
    	  VariableStack varStack = xctxt.getVarStack();
    	  
          varStack.unlink();
          int argsFrame = varStack.link(xslParamMap.size());            
                    
          int paramIdx = 0;
          for (ElemTemplateElement elem = getFirstChildElem(); elem != null; 
                                                                  elem = elem.getNextSiblingElem()) {
              if (elem.getXSLToken() == Constants.ELEMNAME_PARAMVARIABLE) {
                 XObject argValue = argSequence.item(paramIdx);
                 XObject argConvertedVal = argValue;
                 String paramAsAttrStrVal = ((ElemParam)elem).getAs();              
                 if (paramAsAttrStrVal != null) {
                    try {
                       argConvertedVal = SequenceTypeSupport.convertXDMValueToAnotherType(argValue, paramAsAttrStrVal, null, 
                    		                                                                                        xctxt, elem.getPrefixTable());
                       if (argConvertedVal == null) {
                          throw new TransformerException("XPTY0004 : Function call argument at position " + (paramIdx + 1) + " for "
                                                                            + "function {" + funcNameSpaceUri + "}" + funcLocalName + "(), doesn't "
                                                                            + "match the declared parameter type " + paramAsAttrStrVal + ".", srcLocator); 
                       }
                    }
                    catch (TransformerException ex) {
                       if ((SequenceTypeSupport.INLINE_FUNCTION_PARAM_TYPECHECK_COUNT_ERROR).equals(ex.getMessage())) {
                    	  throw new TransformerException("XPTY0004 : The number of inline function parameters, is not equal to "
                    	  		                                             + "the number of expected type specifications for them.", srcLocator);   
                       }
                       else {
                          throw new TransformerException("XPTY0004 : Function call argument at position " + (paramIdx + 1) + " for "
                                                                                 + "function {" + funcNameSpaceUri + "}" + funcLocalName + "(), doesn't "
                                                                                 + "match the declared parameter type " + paramAsAttrStrVal + ".", srcLocator);
                       }
                    }
                 }
                 
                 if (argConvertedVal instanceof ResultSequence) {                
                    XNodeSet nodeSet = XslTransformEvaluationHelper.getXNodeSetFromResultSequence((ResultSequence)argConvertedVal, 
                                                                                                                                xctxt);
                    if (nodeSet != null) {
                       argConvertedVal = nodeSet;  
                    }
                 }
                  
                 varStack.setLocalVariable(paramIdx, argConvertedVal, argsFrame);
                 paramIdx++;
              }
              else {
                 break; 
              }
          }                    
      }            
      
      // Get xsl:function's result, before processing with "as" attribute
      result = getXslFunctionResult(transformer, xctxt);
                        
      XObject funcResultConvertedVal = result;
      
      String funcAsAttrStrVal = getAs();
      
      if (funcAsAttrStrVal != null) {
    	 // Process xsl:function's evaluation result with "as" attribute
         try {        	 
             if (funcResultConvertedVal instanceof XPathInlineFunction) {
            	// The xsl:function's evaluation returned a function item. The 
            	// value of xsl:function's "as" attribute if present, needs to 
            	// match accordingly.
            	XPath seqTypeXPath = new XPath(funcAsAttrStrVal, srcLocator, xctxt.getNamespaceContext(), 
                                                                         XPath.SELECT, null, true);
                XObject seqTypeExpressionEvalResult = seqTypeXPath.execute(xctxt, xctxt.getContextNode(), xctxt.getNamespaceContext());
                SequenceTypeData seqExpectedTypeData = (SequenceTypeData)seqTypeExpressionEvalResult;
            	if (seqExpectedTypeData.getSequenceTypeFunctionTest() != null) {
            	   return funcResultConvertedVal;
            	}
            	else {
            	   throw new TransformerException("XPTY0004 : The function call result for function {" + funcNameSpaceUri + "}" + funcLocalName + 
                                                                         "(), doesn't match the declared function result type " + 
                                                                         funcAsAttrStrVal + ".", srcLocator); 
            	}
             }
             
             funcResultConvertedVal = preprocessXslFunctionOrAVariableResult(result, funcAsAttrStrVal, xctxt, null);
            
             if (funcResultConvertedVal == null) {
                funcResultConvertedVal = SequenceTypeSupport.convertXdmValueToAnotherType(result, funcAsAttrStrVal, null, xctxt);
                
                if (funcResultConvertedVal == null) {
                   throw new TransformerException("XPTY0004 : The function call result for function {" + funcNameSpaceUri + "}" + funcLocalName + 
                                                                                    "(), doesn't match the declared function result type " + 
                                                                                                                 funcAsAttrStrVal + ".", srcLocator);   
                }
             }
         }
         catch (TransformerException ex) {
            throw new TransformerException(ex.getMessage(), srcLocator); 
         }
      }
      
      if (funcResultConvertedVal instanceof ResultSequence) {
         ResultSequence resultSeq = (ResultSequence)funcResultConvertedVal;
         if (resultSeq.size() == 1) {
            funcResultConvertedVal = resultSeq.item(0);   
         }
      }
      
      return funcResultConvertedVal;
  }
  
  /**
   * This function is called after everything else has been
   * recomposed, and allows a xsl:function to set remaining
   * values that may be based on some other property that
   * depends on recomposition.
   */
  public void compose(StylesheetRoot sroot) throws TransformerException
  {
      super.compose(sroot);
  }
  
  /**
   * This function is called, after xsl:function's children have been composed. 
   * We have to get the count of how many variables have been declared, so we
   * can do a link and unlink.
   */
  public void endCompose(StylesheetRoot sroot) throws TransformerException
  {
      super.endCompose(sroot);
  }
  
  /**
   * This function is called during recomposition to
   * control how this element is composed.
   * 
   * @param root The root stylesheet for this transformation.
   */
  public void recompose(StylesheetRoot root)
  {
      super.recompose(root);
  }
  
  /**
   * This method supports to implement xsl:function/xsl:variable instructions,
   * when the XSL child contents of xsl:function/xsl:variable instructions contain 
   * xsl:sequence instruction(s).
   * 
   * Given an initial result of computation of, XSL child contents of a xsl:function/xsl:variable
   * instruction, and xsl:function or xsl:variable's expected data type, cast an input data
   * value to the specified expected data type.
   */
  public ResultSequence preprocessXslFunctionOrAVariableResult(XObject initialEvalResult,
                                                               String sequenceTypeXPathExprStr,
                                                               XPathContext xctxt, QName varQName) throws TransformerException {
     ResultSequence resultSequence = null;
     
     XNodeSetForDOM xNodeSetForDOM = (XNodeSetForDOM)initialEvalResult;
     
     DTMNodeList dtmNodeList = (DTMNodeList)(xNodeSetForDOM.object());
     
     final int contextNode = xctxt.getContextNode(); 
     SourceLocator srcLocator = xctxt.getSAXLocator(); 
     
     XPath seqTypeXPath = new XPath(sequenceTypeXPathExprStr, srcLocator, 
                                                                       xctxt.getNamespaceContext(), XPath.SELECT, null, true);

     XObject seqTypeExpressionEvalResult = seqTypeXPath.execute(xctxt, contextNode, xctxt.getNamespaceContext());

     SequenceTypeData seqExpectedTypeData = (SequenceTypeData)seqTypeExpressionEvalResult;

     Node localRootNode = dtmNodeList.item(0);
     NodeList nodeList = localRootNode.getChildNodes();
     int nodeSetLen = nodeList.getLength();
     
     if (nodeSetLen == 1) {
        Node node = nodeList.item(0);
        short nodeType = node.getNodeType();
        if (nodeType == Node.TEXT_NODE) {
             String strVal = ((Text)node).getNodeValue();             
             if (seqExpectedTypeData.getSequenceTypeKindTest() == null) {               
                resultSequence = new ResultSequence();                  
                if (strVal.contains(ElemSequence.STRING_VAL_SERIALIZATION_SUFFIX)) {
                   String[] strParts = strVal.split(ElemSequence.STRING_VAL_SERIALIZATION_SUFFIX);
                   for (int idx = 0; idx < strParts.length; idx++) {
                      String seqItemStrVal = strParts[idx];
                      XObject xObject = getXSTypedAtomicValue(seqItemStrVal, seqExpectedTypeData.getSequenceType());
                      if (xObject != null) {
                         resultSequence.add(xObject);
                      }
                   }
                }
                else {
                   XObject xObject = getXSTypedAtomicValue(strVal, seqExpectedTypeData.getSequenceType());
                   if (xObject != null) {
                      resultSequence.add(xObject);
                   }
                }
             }
         }
     }
     
     if (seqExpectedTypeData.getSequenceTypeKindTest() == null) {
         if ((seqExpectedTypeData.getItemTypeOccurrenceIndicator() == 0) || 
                                                               (seqExpectedTypeData.getItemTypeOccurrenceIndicator() == 
                                                                                                               SequenceTypeSupport.OccurenceIndicator.ZERO_OR_ONE)) {
             if ((resultSequence != null) && (resultSequence.size() > 1)) {
                String errMesg = null;
                if (m_name != null) {
                   errMesg = "XTTE0780 : A sequence of more than one item, is not allowed as a result of call to function '" + m_name.toString() + "'. "
                                                                                + "The expected result type of this function is " + sequenceTypeXPathExprStr + "."; 
                }
                else {
                   errMesg = "XTTE0570 : A sequence of more than one item, is not allowed as the value of variable '$" + varQName.toString() + "'. "
                                                                                + "This variable has expected type " + sequenceTypeXPathExprStr + "."; 
                }
                
                throw new TransformerException(errMesg); 
             }
         }
     }
     
     return resultSequence;
  }
  
  /**
   * A method that returns a default instance of this class.
   */
  public static ElemFunction getXSLFunctionService() {
     return new ElemFunction();  
  }
  
  /**
   * Evaluate xsl:function's child contents to produce xsl:function's result, before 
   * the function's result is processed with function's "as" attribute.
   * 
   * This method is implemented similarly to how, xsl:variable instruction's value
   * is determined from xsl:variable's child contents. Any xsl:param prefix elements 
   * available within xsl:function element, are processed as per xsl:variable 
   * instruction's evaluation semantics (the class ElemParam extends the class 
   * ElemVariable). 
   * 
   * @param transformer				org.apache.xalan.transformer.TransformerImpl object
   * @param xctxt           		xpath context object
   * @return                		result of xsl:function's evaluation  
   * @throws TransformerException
   */
  private XObject getXslFunctionResult(TransformerImpl transformer, XPathContext xctxt) throws TransformerException {		
	  XObject result = null;
	  
	  Object xslFunctionResult = transformer.transformToGlobalRTFXslFunctionOrTemplate(this);
	  
	  Integer nodeDtmHandle = null;	  
	  try {
		 nodeDtmHandle = Integer.valueOf(xslFunctionResult.toString());
	     NodeList nodeList = (new XRTreeFrag(nodeDtmHandle.intValue(), xctxt, this)).convertToNodeset();
	     result = new XNodeSetForDOM(nodeList, xctxt);
	  }
	  catch (NumberFormatException ex) {
		 result = (XPathInlineFunction)xslFunctionResult;
		 XslTransformSharedDatastore.xpathInlineFunction = null;
	  }	  

	  return result;
  }
  
  /**
   * Given Xalan-J's integer code value of, an XML Schema built-in data type and a 
   * string representation of a data value, construct Xalan-J's typed data object 
   * corresponding to the data type's integer code value. 
   */
  private XObject getXSTypedAtomicValue(String strVal, int sequenceType) throws TransformerException {
      
      XObject result = null;
     
      if (sequenceType == SequenceTypeSupport.BOOLEAN) {
         boolean boolVal = ("false".equals(strVal) || "0".equals(strVal)) ? false : true;
         result = new XSBoolean(boolVal);
      }
      else if (sequenceType == SequenceTypeSupport.STRING) {
         result = new XSString(strVal);
      }
      else if (sequenceType == SequenceTypeSupport.XS_DATE) {
         result = XSDate.parseDate(strVal);
      }
      else if (sequenceType == SequenceTypeSupport.XS_DATETIME) {
         result = XSDateTime.parseDateTime(strVal);
      }
      else if (sequenceType == SequenceTypeSupport.XS_TIME) {
    	 result = XSTime.parseTime(strVal);
      }
      else if (sequenceType == SequenceTypeSupport.XS_DURATION) {
         result = XSDuration.parseDuration(strVal);
      }
      else if (sequenceType == SequenceTypeSupport.XS_DAYTIME_DURATION) {
         result = XSDayTimeDuration.parseDayTimeDuration(strVal);
      }
      else if (sequenceType == SequenceTypeSupport.XS_YEARMONTH_DURATION) {
         result = XSYearMonthDuration.parseYearMonthDuration(strVal); 
      }
      else if (sequenceType == SequenceTypeSupport.XS_DECIMAL) {
         result = new XSDecimal(strVal); 
      }
      else if (sequenceType == SequenceTypeSupport.XS_INTEGER) {
         result = new XSInteger(strVal); 
      }
      else if (sequenceType == SequenceTypeSupport.XS_LONG) {
         result = new XSLong(strVal);
      }
      else if (sequenceType == SequenceTypeSupport.XS_INT) {
         result = new XSInt(strVal); 
      }
      else if (sequenceType == SequenceTypeSupport.XS_SHORT) {
         result = new XSShort(strVal); 
      }
      else if (sequenceType == SequenceTypeSupport.XS_BYTE) {
         result = new XSByte(strVal); 
      }
      else if (sequenceType == SequenceTypeSupport.XS_UNSIGNED_LONG) {
         result = new XSUnsignedLong(strVal); 
      }
      else if (sequenceType == SequenceTypeSupport.XS_UNSIGNED_INT) {
         result = new XSUnsignedInt(strVal); 
      }
      else if (sequenceType == SequenceTypeSupport.XS_UNSIGNED_SHORT) {
         result = new XSUnsignedShort(strVal); 
      }
      else if (sequenceType == SequenceTypeSupport.XS_UNSIGNED_BYTE) {
          result = new XSUnsignedByte(strVal); 
      }
      else if (sequenceType == SequenceTypeSupport.XS_NON_NEGATIVE_INTEGER) {
         result = new XSNonNegativeInteger(strVal); 
      }
      else if (sequenceType == SequenceTypeSupport.XS_POSITIVE_INTEGER) {
         result = new XSPositiveInteger(strVal); 
      }
      else if (sequenceType == SequenceTypeSupport.XS_NON_POSITIVE_INTEGER) {
         result = new XSNonPositiveInteger(strVal); 
      }
      else if (sequenceType == SequenceTypeSupport.XS_NEGATIVE_INTEGER) {
         result = new XSNegativeInteger(strVal); 
      }
      else if (sequenceType == SequenceTypeSupport.XS_DOUBLE) {
         result = new XSDouble(strVal); 
      }
      else if (sequenceType == SequenceTypeSupport.XS_FLOAT) {
         result = new XSFloat(strVal); 
      }
      else {
    	 result = new XSString(strVal); 
      }
     
      return result;
  }

}
