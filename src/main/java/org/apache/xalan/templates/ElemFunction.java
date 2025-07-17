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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xalan.xslt.util.XslTransformSharedDatastore;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xml.dtm.ref.DTMNodeList;
import org.apache.xml.dtm.ref.DTMNodeProxy;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xml.utils.QName;
import org.apache.xpath.VariableStack;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.compiler.FunctionTable;
import org.apache.xpath.compiler.Keywords;
import org.apache.xpath.composite.SequenceTypeData;
import org.apache.xpath.composite.SequenceTypeFunctionTest;
import org.apache.xpath.composite.SequenceTypeKindTest;
import org.apache.xpath.composite.SequenceTypeSupport;
import org.apache.xpath.composite.XPathNamedFunctionReference;
import org.apache.xpath.functions.Function;
import org.apache.xpath.objects.ElemFunctionItem;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XNodeSetForDOM;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathArray;
import org.apache.xpath.objects.XPathInlineFunction;
import org.apache.xpath.objects.XPathMap;
import org.apache.xpath.objects.XRTreeFrag;
import org.apache.xpath.patterns.NodeTest;
import org.apache.xpath.types.XMLAttribute;
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

import xml.xpath31.processor.types.XSAnyURI;
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
 * Implementation of XSLT 3.0 xsl:function instruction.
 *
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class ElemFunction extends ElemTemplate
{

  private static final long serialVersionUID = 4973132678982467288L;
  
  /**
   * This class field, represents the value of "xpath-default-namespace" 
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
   * This class field, represents the value of "override" 
   * attribute.
   */
  private boolean m_override;
  
  
  /**
   * This class field, indicates that xsl:function instruction 
   * has attribute "override" declared with either of the 
   * values 'true' or 'false'. 
   */
  private boolean m_override_attr_declared;

  /**
   * Set the value of "override" attribute.
   *
   * @param v   Value of the "override" attribute
   */
  public void setOverride(boolean bool)
  {
	  m_override = bool;	  
	  m_override_attr_declared = true;
  }

  /**
   * Get the value of "override" attribute.
   *  
   * @return		  The value of "override" attribute 
   */
  public boolean getOverride() {
	  return m_override;
  }
  
  /**
   * This class field, represents the value of "override-extension-function" 
   * attribute. An xsl:function's attributes "override-extension-function" and 
   * "override" are synonym. An xsl:function attribute "override" is deprecated 
   * with XSLT 3.0. 
   */
  private boolean m_override_extension_function;
  
  /**
   * This class field, indicates that xsl:function instruction has attribute 
   * "override-extension-function" declared with either of the values 
   * 'true' or 'false'. 
   */
  private boolean m_override_extension_function_attr_declared;

  /**
   * Set the value of "override-extension-function" attribute.
   *
   * @param v   Value of the "override-extension-function" attribute
   */
  public void setOverrideExtensionFunction(boolean bool)
  {
	  m_override_extension_function = bool;	  
	  m_override_extension_function_attr_declared = true;
  }

  /**
   * Get the value of "override-extension-function" attribute.
   *  
   * @return		  The value of "override-extension-function" attribute 
   */
  public boolean getOverrideExtensionFunction() {
	  return m_override_extension_function;
  }

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
                   throw new TransformerException("XPST0017 : An xsl:function instruction {" + funcNameSpaceUri + "}" + funcLocalName + " "
                   		                                                                     + "has more than one xsl:param '" + 
                		                                                                       xslParamName + "' declaration.", srcLocator); 
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
            	 throw new TransformerException("XPST0017 : An xsl:function instruction {" + funcNameSpaceUri + "}" + funcLocalName + " "
                                                                                           + "has more than one xsl:param '" + elemParamQName.toString() 
                                                                                           + "' declaration.", srcLocator); 
             } 
         }
         
         idx++;
      }
      
      if (xslParamMap.size() != argSequence.size()) {
         throw new TransformerException("XPST0017 : For the xsl:function " + "{" + funcNameSpaceUri + "}" + funcLocalName + 
                                                                       ", the number of arguments provided with function call is not equal "
                                                                       + "to number of function's xsl:param declarations.", srcLocator);  
      }
      
      Collection<Integer> xslParamIdxs = xslParamMap.values();
      Object[] idxArr = xslParamIdxs.toArray();
      if (idxArr.length > 0) {
         Arrays.sort(idxArr);
         int currVal = ((Integer)idxArr[0]).intValue();
         if (currVal != 0) {
            throw new TransformerException("XPST0017 : For the xsl:function " + "{" + funcNameSpaceUri + "}" + funcLocalName + 
                                                                          ", there's a non xsl:param declaration as first child "
                                                                          + "element of xsl:function.", srcLocator); 
         }
         
         for (int idx1 = 1; idx1 < idxArr.length; idx1++) {
            int nextVal = ((Integer)idxArr[idx1]).intValue();
            if (nextVal != (currVal + 1)) {
               throw new TransformerException("XPST0017 : For the xsl:function " + "{" + funcNameSpaceUri + "}" + funcLocalName + 
                                                                             ", there's a non xsl:param declaration between two "
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
                 XObject argConvertedVal = null;
                 String paramAsAttrStrVal = ((ElemParam)elem).getAs();
                 
                 if (paramAsAttrStrVal != null) {
                    argConvertedVal = getParamValueAsAttributeProcessing(argValue, funcLocalName, funcNameSpaceUri, paramIdx, 
                    		                                             elem.getPrefixTable(), paramAsAttrStrVal, transformer, xctxt);
                 }
                 else {
                	argConvertedVal = argValue;  
                 }
                 
                 if (argConvertedVal instanceof ResultSequence) {                
                    XMLNodeCursorImpl nodeSet = XslTransformEvaluationHelper.getXNodeSetFromResultSequence((ResultSequence)argConvertedVal, 
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
            	SequenceTypeData seqExpectedTypeData = getSequenceTypeDataFromSeqTypeStr(funcAsAttrStrVal, xctxt, srcLocator);
            	if (seqExpectedTypeData.getSequenceTypeFunctionTest() != null) {
            	   return funcResultConvertedVal;
            	}
            	else {
            	   throw new TransformerException("XPTY0004 : The function call result for function {" + funcNameSpaceUri + "}" + funcLocalName + 
										                                                                         ", doesn't match the declared function result type " + 
										                                                                         funcAsAttrStrVal + ".", srcLocator); 
            	}
             }
             else if (funcResultConvertedVal instanceof XPathMap) {            	
             	SequenceTypeData seqExpectedTypeData = getSequenceTypeDataFromSeqTypeStr(funcAsAttrStrVal, xctxt, srcLocator);
             	if (seqExpectedTypeData.getSequenceTypeMapTest() != null) {
             	   return funcResultConvertedVal;
             	}
             	else {
             	   throw new TransformerException("XPTY0004 : The function call result for function {" + funcNameSpaceUri + "}" + funcLocalName + 
										                                                                         ", doesn't match the declared function result type " + 
										                                                                         funcAsAttrStrVal + ".", srcLocator); 
             	}
             }
             else if (funcResultConvertedVal instanceof XPathArray) {            	
              	SequenceTypeData seqExpectedTypeData = getSequenceTypeDataFromSeqTypeStr(funcAsAttrStrVal, xctxt, srcLocator);
              	if (seqExpectedTypeData.getSequenceTypeArrayTest() != null) {
              	   return funcResultConvertedVal;
              	}
              	else {
              	   throw new TransformerException("XPTY0004 : The function call result for function {" + funcNameSpaceUri + "}" + funcLocalName + 
									                                                                            ", doesn't match the declared function result type " + 
									                                                                            funcAsAttrStrVal + ".", srcLocator); 
              	}
             }
             else if (((XslTransformSharedDatastore.m_xpathNamedFunctionRefSequence).size() > 0) && !ElemVariable.m_isXPathNamedFunctionRefSequenceVar) {
            	SequenceTypeData seqExpectedTypeData = getSequenceTypeDataFromSeqTypeStr(funcAsAttrStrVal, xctxt, srcLocator);
            	
            	int funcItemSeqSize = (XslTransformSharedDatastore.m_xpathNamedFunctionRefSequence).size();
        		
        		SequenceTypeFunctionTest sequenceTypeFunctionTest = seqExpectedTypeData.getSequenceTypeFunctionTest();
        		int seqTypeItemOccurenceIndicator = seqExpectedTypeData.getItemTypeOccurrenceIndicator();
        		boolean isSeqCardinalityOk = false;
        		if ((funcItemSeqSize == 0) && ((seqTypeItemOccurenceIndicator == SequenceTypeSupport.OccurrenceIndicator.ZERO_OR_MANY) || 
        				                       (seqTypeItemOccurenceIndicator == SequenceTypeSupport.OccurrenceIndicator.ZERO_OR_ONE))) {
        		   isSeqCardinalityOk = true;
        		}
        		else if ((funcItemSeqSize == 1) && ((seqTypeItemOccurenceIndicator == SequenceTypeSupport.OccurrenceIndicator.ABSENT) || 
        				                            (seqTypeItemOccurenceIndicator == SequenceTypeSupport.OccurrenceIndicator.ZERO_OR_MANY) || 
        				                            (seqTypeItemOccurenceIndicator == SequenceTypeSupport.OccurrenceIndicator.ONE_OR_MANY) ||
        				                            (seqTypeItemOccurenceIndicator == SequenceTypeSupport.OccurrenceIndicator.ZERO_OR_ONE))) {
        		   // An XPath sequence type occurrence indicator with value absent, or any 
        		   // other occurrence indicator is ok.
        		   isSeqCardinalityOk = true;
        		}
        		else if ((seqTypeItemOccurenceIndicator == SequenceTypeSupport.OccurrenceIndicator.ZERO_OR_MANY) ||
        				 (seqTypeItemOccurenceIndicator == SequenceTypeSupport.OccurrenceIndicator.ONE_OR_MANY)) {
        		   // funcItemSeqSize > 1
        		   isSeqCardinalityOk = true;
        		}
        		
        		if (!isSeqCardinalityOk) {
        			throw new TransformerException("XPTY0004 : The function call result for function {" + funcNameSpaceUri + "}" + funcLocalName + 
													                                                    ", doesn't match the declared function result type " + funcAsAttrStrVal + ". The cardinality " + 
													        					                        funcItemSeqSize + " of function's result, doesn't conform to what is specified in xsl:function "
													        					                        + "result's 'as' attribute value " + funcAsAttrStrVal + ".", srcLocator);
        		}
            	
        		if (sequenceTypeFunctionTest != null) {
        			if (sequenceTypeFunctionTest.isAnyFunctionTest()) {
        				if ((XslTransformSharedDatastore.m_xpathNamedFunctionRefSequence).size() == 1) {
        					funcResultConvertedVal = (XslTransformSharedDatastore.m_xpathNamedFunctionRefSequence).item(0);
        					(XslTransformSharedDatastore.m_xpathNamedFunctionRefSequence).remove(0);
        				}
        				else {
        					funcResultConvertedVal = XslTransformSharedDatastore.m_xpathNamedFunctionRefSequence;
        				}

        				return funcResultConvertedVal;
        			}
        			else {
        				if ((XslTransformSharedDatastore.m_xpathNamedFunctionRefSequence).size() == 1) {
        					funcResultConvertedVal = (XslTransformSharedDatastore.m_xpathNamedFunctionRefSequence).item(0);        					
        					if (funcResultConvertedVal instanceof XPathNamedFunctionReference) {
        					   XPathNamedFunctionReference xpathNamedFunctionReference = (XPathNamedFunctionReference)funcResultConvertedVal;
        					   if (xpathNamedFunctionReference.getXslStylesheetFunction() == null) {
        						   int actualFuncArity = xpathNamedFunctionReference.getArity();
        						   List<String> expectedParamList = sequenceTypeFunctionTest.getTypedFunctionTestParamSpecList();
        						   if (actualFuncArity != expectedParamList.size()) {
        							   throw new TransformerException("XPTY0004 : The function call result for function {" + funcNameSpaceUri + "}" + funcLocalName + 
                                                                                                                             ", doesn't match the declared function result type " + funcAsAttrStrVal + 
                                                                                                                             ". The function arity specification doesn't match.", srcLocator); 
        						   }        						   
        					   }
        					   else {
        						   // REVISIT
        					   }
        					}
        					
        					(XslTransformSharedDatastore.m_xpathNamedFunctionRefSequence).remove(0);
        				}
        				else {
        					// REVISIT
        					funcResultConvertedVal = XslTransformSharedDatastore.m_xpathNamedFunctionRefSequence;
        					(XslTransformSharedDatastore.m_xpathNamedFunctionRefSequence).clear();
        				}

        				return funcResultConvertedVal;
        			}
        		}
             }
             
             funcResultConvertedVal = preprocessXslFunctionOrAVariableResult(result, funcAsAttrStrVal, xctxt, null);
            
             if (funcResultConvertedVal == null) {
                funcResultConvertedVal = SequenceTypeSupport.castXdmValueToAnotherType(result, funcAsAttrStrVal, null, xctxt);
                
                if (funcResultConvertedVal == null) {
                   throw new TransformerException("XPTY0004 : The function call result for function {" + funcNameSpaceUri + "}" + funcLocalName + 
                                                                                                                          ", doesn't match the declared function result type " + 
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
     
     final int contextNode = xctxt.getContextNode();
     
     SourceLocator srcLocator = xctxt.getSAXLocator();
     
     XPath seqTypeXPath = new XPath(sequenceTypeXPathExprStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null, true);

     XObject seqTypeExpressionEvalResult = seqTypeXPath.execute(xctxt, contextNode, xctxt.getNamespaceContext());

     SequenceTypeData seqExpectedTypeData = (SequenceTypeData)seqTypeExpressionEvalResult;
     
     SequenceTypeKindTest seqTypeKindTest = seqExpectedTypeData.getSequenceTypeKindTest();
     
     if (initialEvalResult instanceof XPathNamedFunctionReference) {
    	 SequenceTypeFunctionTest seqTypeFunctionTest = seqExpectedTypeData.getSequenceTypeFunctionTest();
    	 if (seqTypeFunctionTest != null) {
    		 XPathNamedFunctionReference xpathNamedFunctionReference = (XPathNamedFunctionReference)initialEvalResult;    		 
    		 if (seqTypeFunctionTest.isAnyFunctionTest()) {
    			 resultSequence = new ResultSequence();
    			 resultSequence.add(xpathNamedFunctionReference);

    			 return resultSequence;
    		 }
    		 else {
    			 // REVISIT        		     		 
    			 List<String> funcParamSpecList = seqTypeFunctionTest.getTypedFunctionTestParamSpecList();
    			 String funcReturnSeqType = seqTypeFunctionTest.getTypedFunctionTestReturnType();
    			 
    			 String funcName = xpathNamedFunctionReference.getFuncName();
    			 int funcArity = xpathNamedFunctionReference.getArity();
    			 FunctionTable funcTable = xctxt.getFunctionTable();
    			 Object funcIdInFuncTable = funcTable.getFunctionId(funcName);
    			 Function function = funcTable.getFunction((int)funcIdInFuncTable);
    			 if (function != null) {
    				 resultSequence = new ResultSequence();
    				 resultSequence.add(xpathNamedFunctionReference);

    				 return resultSequence; 
    			 }
        	 }
    	 }    	 
     }
     else if (initialEvalResult instanceof XMLAttribute) {
    	 XMLAttribute xmlAttribute = (XMLAttribute)initialEvalResult;
    	 String localName = xmlAttribute.getLocalName();
    	 String nsUri = xmlAttribute.getNamespaceUri();
    	 QName attrQName = new QName(nsUri, localName);
    	 if (seqTypeKindTest.getKindVal() == SequenceTypeSupport.ATTRIBUTE_KIND) {
    		 String expectedLocalName = seqTypeKindTest.getNodeLocalName();    		 
    		 if ((expectedLocalName != null) && !"".equals(expectedLocalName)) {
    			 String expectedNsUri = seqTypeKindTest.getNodeNsUri();
    			 QName expectedQName = new QName(expectedNsUri, expectedLocalName);
    			 if (!attrQName.equals(expectedQName)) {
    				 throw new TransformerException("XTTE0570 : An attribute '" + attrQName.toString() + "' produced during "
    				 		                                                    + "XSL transformation doesn't match the expected type " + 
    						                                                      sequenceTypeXPathExprStr + ".", srcLocator);
    			 }
    		 }
    	 }
    	 
    	 resultSequence = new ResultSequence();
		 resultSequence.add(initialEvalResult);

		 return resultSequence;
     }
     
     XNodeSetForDOM xNodeSetForDOM = (XNodeSetForDOM)initialEvalResult;
     
     DTMNodeList dtmNodeList = (DTMNodeList)(xNodeSetForDOM.object());

     Node localRootNode = dtmNodeList.item(0);
     NodeList nodeList = localRootNode.getChildNodes();
     int nodeSetLen = nodeList.getLength();          
     
     if (nodeSetLen == 1) {
        Node node = nodeList.item(0);
        short nodeType = node.getNodeType();
        if (nodeType == Node.TEXT_NODE) {
             String strVal = ((Text)node).getNodeValue();             
             if (seqTypeKindTest == null) {               
                resultSequence = new ResultSequence();                  
                if (strVal.contains(ElemSequence.STRING_VAL_SERIALIZATION_SUFFIX)) {
                   String[] strParts = strVal.split(ElemSequence.STRING_VAL_SERIALIZATION_SUFFIX);
                   for (int idx = 0; idx < strParts.length; idx++) {
                      String seqItemStrVal = strParts[idx];
                      XObject xObject = getXSTypedAtomicValue(seqItemStrVal, seqExpectedTypeData.getBuiltInSequenceType());
                      if (xObject != null) {
                         resultSequence.add(xObject);
                      }
                   }
                }
                else {
                   XObject xObject = getXSTypedAtomicValue(strVal, seqExpectedTypeData.getBuiltInSequenceType());
                   if (xObject != null) {
                      resultSequence.add(xObject);
                   }
                }
             }
         }
     }
     
     if ((nodeSetLen == 1) && (seqTypeKindTest != null)) {
    	 Node node = nodeList.item(0);
         short nodeType = node.getNodeType();
         if ((nodeType == Node.ELEMENT_NODE) && (seqTypeKindTest.getKindVal() == SequenceTypeSupport.ELEMENT_KIND)) {        	 
        	 String typeNodeLocalName = seqTypeKindTest.getNodeLocalName();
        	 String typeNodeNsUri = seqTypeKindTest.getNodeNsUri();
        	 String typeDataTypeLocalName = seqTypeKindTest.getDataTypeLocalName();
        	 String typeDataTypeUri = seqTypeKindTest.getDataTypeUri();
        	 
        	 String nodeLocalName = node.getLocalName();
        	 String nodeNsUri = node.getNamespaceURI();
        	 
        	 XSTypeDefinition xsTypeDefn = xNodeSetForDOM.getXsTypeDefinition();
        	 if (xsTypeDefn == null) {
        		if (typeDataTypeLocalName == null) {
        			if (typeNodeLocalName == null) {        				
        				resultSequence = getResultSequenceFromNode(node, seqTypeKindTest, xctxt);
        				
        				return resultSequence;
        			}        			
        		}
        		else if ((XMLConstants.W3C_XML_SCHEMA_NS_URI).equals(typeDataTypeUri) && (Keywords.XS_UNTYPED).equals(typeDataTypeLocalName)) {
    				if ((nodeNsUri == null) && (typeNodeNsUri == null) && typeNodeLocalName.equals(nodeLocalName)) {
    					resultSequence = getResultSequenceFromNode(node, seqTypeKindTest, xctxt);

        				return resultSequence;
    				}
    				else if ((nodeNsUri != null) && (nodeNsUri.equals(typeNodeNsUri)) && typeNodeLocalName.equals(nodeLocalName)) {
    					resultSequence = getResultSequenceFromNode(node, seqTypeKindTest, xctxt);

        				return resultSequence;
    				}
    			}
        	 }
        	 else {
        		// REVISIT
        		// We need to do XML Schema validation of node 'node' with the type xsTypeDefn 
        	 }
         }
     }
     else if ((seqExpectedTypeData.getItemTypeOccurrenceIndicator() == 0) || (seqExpectedTypeData.getItemTypeOccurrenceIndicator() == 
                                                                                                               SequenceTypeSupport.OccurrenceIndicator.ZERO_OR_ONE)) {
    	 if ((resultSequence != null) && (resultSequence.size() > 1)) {
    		 String errMesg = null;
    		 if (m_name != null) {
    			 errMesg = "XTTE0780 : A sequence of more than one item, is not allowed as a result of call to function '" + m_name.toString() + "'. "
    					 																		+ "The expected result type of this function is " + sequenceTypeXPathExprStr + "."; 
    		 }
    		 else if (varQName != null) {
    			 errMesg = "XTTE0570 : A sequence of more than one item, is not allowed as the value of variable '$" + varQName.toString() + "'. "
    					 																		+ "This variable has expected type " + sequenceTypeXPathExprStr + "."; 
    		 }
    		 else {
    			 errMesg = "XTTE0570 : A sequence of more than one item, is not allowed as the result of xsl:evaluate instruction's evaluation. "
    					 																		+ "An xsl:evaluate instruction's expected type is " + sequenceTypeXPathExprStr + ".";
    		 }

    		 throw new TransformerException(errMesg); 
    	 }
     }
     
     return resultSequence;
  }
  
  /**
   * Method definition, to return an object instance of this class.
   */
  public static ElemFunction getXSLFunctionService() {
     return new ElemFunction();  
  }
  
  /**
   * Method definition, to return xsl:function definition's arity.
   */
  public short getArity() {
	 short paramCount = 0;
	 
	 for (ElemTemplateElement elem = getFirstChildElem(); elem != null; elem = elem.getNextSiblingElem()) {
		 if (elem instanceof ElemParam) {
			 paramCount++; 
		 }
	 }
	 
	 return paramCount;
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
   * @param transformer				An XSL TransformerImpl object
   * @param xctxt           		An XPath context object
   * @return                		Result of xsl:function's evaluation  
   * @throws TransformerException
   */
  private XObject getXslFunctionResult(TransformerImpl transformer, XPathContext xctxt) throws TransformerException {		
	  
	  XObject result = null;
	  
	  ElemTemplateElement firstChildElem = getFirstChildElem();
	  ElemTemplateElement secondChildElem = null;
	  if (firstChildElem != null) {
		 secondChildElem = firstChildElem.getNextSiblingElem();  
	  }
	  
	  if ((firstChildElem instanceof ElemAttribute) && (secondChildElem == null)) {
		  // An XSL function is emitting only an attribute node via xsl:attribute 
		  // instruction.		  
		  ElemAttribute elemAttr = (ElemAttribute)firstChildElem;
		  AVT attrAvtName = elemAttr.getName();
		  String prefix = null;
		  String localName = attrAvtName.evaluate(xctxt, xctxt.getCurrentNode(), xctxt.getNamespaceContext());
		  String namespaceUri = null;
		  int indexColon = localName.indexOf(':');
		  if (indexColon > 0) {
			 prefix = localName.substring(0, indexColon);
			 localName = localName.substring(indexColon + 1); 
			 List<XMLNSDecl> prefixTable = firstChildElem.getPrefixTable();
			 for (int idx = 0; idx < prefixTable.size(); idx++) {
				 XMLNSDecl xmlNSDecl = prefixTable.get(idx);
				 String prefix1 = xmlNSDecl.getPrefix();
				 if (prefix1.equals(prefix)) {
					namespaceUri = xmlNSDecl.getURI();
					break;
				 }
			 }
		  }

		  String attrValue = null;
		  
		  try {
			 elemAttr.setIsSerialize(false);
		     elemAttr.constructNode(localName, prefix, namespaceUri, transformer);
		     attrValue = elemAttr.getAttrVal();
		  }
		  catch (TransformerException ex) {
			 throw ex; 
		  }
		  finally {
			 elemAttr.setIsSerialize(true);
			 elemAttr.setAttrVal(null);
		  }
		  
		  result = new XMLAttribute(localName, prefix, namespaceUri, attrValue);
	  }
	  else {
		  Object xslFunctionResult = transformer.transformToGlobalRTFXslFunctionOrTemplate(this);

		  if (XslTransformSharedDatastore.m_xpathInlineFunction != null) {
			  result = XslTransformSharedDatastore.m_xpathInlineFunction;			  
			  XslTransformSharedDatastore.m_xpathInlineFunction = null;
		  }
		  else if (XslTransformSharedDatastore.m_xpathMap != null) {
			  result = XslTransformSharedDatastore.m_xpathMap;			  
			  XslTransformSharedDatastore.m_xpathMap = null;
		  }
		  else if (XslTransformSharedDatastore.m_xpathArray != null) {
			  result = XslTransformSharedDatastore.m_xpathArray;			  
			  XslTransformSharedDatastore.m_xpathArray = null;
		  }

		  if (result == null) {		  
			  int nodeDtmHandle = Integer.valueOf(xslFunctionResult.toString());
			  NodeList nodeList = (new XRTreeFrag(nodeDtmHandle, xctxt, this)).convertToNodeset();
			  result = new XNodeSetForDOM(nodeList, xctxt);		  
		  }
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
         boolean boolVal = ("true".equals(strVal) || "1".equals(strVal)) ? true : false;
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
      else if (sequenceType == SequenceTypeSupport.XS_ANY_URI) {
         result = new XSAnyURI(strVal); 
      }
      else {
    	 result = new XSString(strVal); 
      }
     
      return result;
  }
  
  /**
   * Check whether two XSTypeDefinition objects, represent the same schema type. 
   */
  private boolean isTypeXsDefinitionEqual(XSTypeDefinition typeDef1, XSTypeDefinition typeDef2) {	  
	  boolean result = false;

	  String typeName1 = typeDef1.getName();
	  String typeNamespace1 = typeDef1.getNamespace();

	  String typeName2 = typeDef2.getName();
	  String typeNamespace2 = typeDef2.getNamespace();
	  
	  if ((typeNamespace1 == null) && (typeNamespace2 == null)) {
		 result = typeName1.equals(typeName2);   
	  }
	  else if (((typeNamespace1 == null) && (typeNamespace2 != null)) || ((typeNamespace1 != null) && (typeNamespace2 == null))) {
		 result = false; 
	  }
	  else if (typeNamespace1.equals(typeNamespace2) && typeName1.equals(typeName2)) {
		 result = true; 
	  }

	  return result;
  }
  
  /**
   * Given XPath sequence type string value, produce compiled SequenceTypeData object. 
   */
  private SequenceTypeData getSequenceTypeDataFromSeqTypeStr(String seqTypeStr, XPathContext xctxt, 
                                                             SourceLocator srcLocator) throws TransformerException {
	  SequenceTypeData seqTypeData = null;

	  XPath seqTypeXPath = new XPath(seqTypeStr, srcLocator, xctxt.getNamespaceContext(), 
			                                                                             XPath.SELECT, null, true);
	  XObject seqTypeExpressionEvalResult = seqTypeXPath.execute(xctxt, xctxt.getContextNode(), xctxt.getNamespaceContext());
	  seqTypeData = (SequenceTypeData)seqTypeExpressionEvalResult;

	  return seqTypeData;
  }
  
  /**
   * Method definition to do type checking and required modification of xsl:function's xsl:param
   * value using the XPath sequence type expression from xsl:param's 'as' attribute.
   * 
   * @param srcValue								      An XDM value on which type checking and 
   *                                                      value conversion is required.
   * @param funcLocalName								  An xsl:function's local name value from it's QName value
   * @param funcNameSpaceUri                              An xsl:function's QName name's namespace uri                              	                                                      
   * @param paramIdx									  An xsl:param's relative index value within sibling 
   *                                                      xsl:param elements.
   * @param prefixTable									  An XSL transformation's run-time XML namespace
   *                                                      prefix table list.                                                       
   * @param paramAsAttrStrVal							  String value of xsl:param's 'as' attribute
   * @param transformer                                   An XSL transformation run-time TransformerImpl object
   * @param xctxt										  An XPath context object
   * @return											  An XDM value produced after conversion using xsl:param's 
   *                                                      XPath sequence type 'as' attribute.
   * @throws TransformerException
   */
  private XObject getParamValueAsAttributeProcessing(XObject srcValue, String funcLocalName, String funcNameSpaceUri, 
		                                             int paramIdx, List prefixTable, String paramAsAttrStrVal, 
		                                             TransformerImpl transformer, XPathContext xctxt) throws TransformerException {
		
	  XObject argConvertedVal = null;

	  SourceLocator srcLocator = xctxt.getSAXLocator();

	  try {
		  XPath seqTypeXPath = new XPath(paramAsAttrStrVal, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null, true);            
		  XObject seqTypeExpressionEvalResult = seqTypeXPath.execute(xctxt, xctxt.getContextNode(), xctxt.getNamespaceContext());
		  SequenceTypeData seqExpectedTypeData = (SequenceTypeData)seqTypeExpressionEvalResult;          

		  XMLNodeCursorImpl nodeSet = SequenceTypeSupport.getNodeReference(srcValue);
		  if (nodeSet != null) {
			  XSTypeDefinition typeDef = nodeSet.getXsTypeDefinition();                    	                      	                      	  
			  if (typeDef != null) {                    		  
				  XSTypeDefinition typeDef2 = seqExpectedTypeData.getXsTypeDefinition();
				  if (typeDef2 != null && isTypeXsDefinitionEqual(typeDef, typeDef2)) {                            	  
					  argConvertedVal = nodeSet;                      			
				  }
				  else {
					  throw new TransformerException("XPTY0004 : An xsl:function call argument at position " + (paramIdx + 1) + " for "
																								  + "function {" + funcNameSpaceUri + "}" + funcLocalName + ", doesn't "
																								  + "match the declared parameter type " + paramAsAttrStrVal + ".", srcLocator);
				  }
			  }                    	  
			  else if (seqExpectedTypeData.getSequenceTypeFunctionTest() != null) {                    		  
				  DTMCursorIterator dtmIter = nodeSet.getContainedIter();                    		  
				  if (dtmIter instanceof NodeTest) {
					  try {
						  ElemFunction elemFunction = XslTransformEvaluationHelper.getElemFunctionFromNodeTestExpression(
								                                                                               (NodeTest)dtmIter, transformer, srcLocator);
						  if (elemFunction != null) {
							  // REVISIT : To check for elemFunction object's conformance with details in 
							  // the object seqExpectedTypeData.getSequenceTypeFunctionTest()  
							  argConvertedVal = new ElemFunctionItem(elemFunction);
						  }
					  }
					  catch (TransformerException ex) {
						  // NO OP
					  }
				  }
			  }
		  }
		  else if ((seqExpectedTypeData.getSequenceTypeFunctionTest() != null) && (srcValue instanceof XPathInlineFunction)) {
			  // REVISIT : To check for argValue's conformance with details in 
			  // the object seqExpectedTypeData.getSequenceTypeFunctionTest()
			  argConvertedVal = srcValue; 
		  }

		  if (argConvertedVal == null) {
			  argConvertedVal = SequenceTypeSupport.castXDMValueToAnotherType(srcValue, paramAsAttrStrVal, null, xctxt, prefixTable);
		  }

		  if (argConvertedVal == null) {
			  throw new TransformerException("XPTY0004 : An xsl:function call argument at position " + (paramIdx + 1) + " for "
																					  + "function {" + funcNameSpaceUri + "}" + funcLocalName + ", doesn't "
																					  + "match the declared parameter type " + paramAsAttrStrVal + ".", srcLocator); 
		  }
	  }
	  catch (TransformerException ex) {
		  if ((SequenceTypeSupport.INLINE_FUNCTION_PARAM_TYPECHECK_COUNT_ERROR).equals(ex.getMessage())) {
			  throw new TransformerException("XPTY0004 : The number of XPath inline function parameters, is not equal to "
					  																  + "the number of expected type specifications for them.", srcLocator);   
		  }
		  else {
			  throw new TransformerException("XPTY0004 : An xsl:function call argument at position " + (paramIdx + 1) + " for "
																					  + "function {" + funcNameSpaceUri + "}" + funcLocalName + ", doesn't "
																					  + "match the declared parameter type " + paramAsAttrStrVal + ".", srcLocator);
		  }
	  }

	  return argConvertedVal;	  
	}
  
   /**
    * Method definition to get an XML node instance as a ResultSequence object. 
    * 
    * @param node					An XML node instance
    * @param seqTypeKindTest        SequenceTypeKindTest object with which the node has 
    * 								been verified.
    * @param xctxt					An XPath context object
    * @return						ResultSequence object encapsulating the node
    */
    private ResultSequence getResultSequenceFromNode(Node node, SequenceTypeKindTest seqTypeKindTest, XPathContext xctxt) {
		ResultSequence resultSequence = new ResultSequence();
		
		DTMNodeProxy dtmNodeProxy = (DTMNodeProxy)node;
		int nodeHandle = dtmNodeProxy.getDTMNodeNumber();
		XMLNodeCursorImpl xmlNodeCursorImpl = new XMLNodeCursorImpl(nodeHandle, xctxt);
		xmlNodeCursorImpl.setSeqTypeKindTest(seqTypeKindTest);
		resultSequence.add(xmlNodeCursorImpl);
		
		return resultSequence;
	}
    
    public boolean isOverrideAttrDeclared() {
    	return m_override_attr_declared;
    }
    
    public boolean isOverrideExtensionFunctionAttrDeclared() {
    	return m_override_extension_function_attr_declared;
    }

}
