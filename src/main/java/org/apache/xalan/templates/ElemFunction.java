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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.serialize.SerializerUtils;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.xslt.util.XslTransformData;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xml.dtm.ref.DTMNodeList;
import org.apache.xml.dtm.ref.DTMNodeProxy;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xml.utils.QName;
import org.apache.xml.utils.XMLString;
import org.apache.xpath.VariableStack;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathStaticContext;
import org.apache.xpath.compiler.FunctionTable;
import org.apache.xpath.compiler.Keywords;
import org.apache.xpath.composite.SequenceTypeData;
import org.apache.xpath.composite.SequenceTypeFunctionTest;
import org.apache.xpath.composite.SequenceTypeKindTest;
import org.apache.xpath.composite.SequenceTypeSupport;
import org.apache.xpath.composite.SequenceTypeSupport.OccurrenceIndicator;
import org.apache.xpath.composite.XPathNamedFunctionReference;
import org.apache.xpath.functions.Function;
import org.apache.xpath.functions.XslFunctionMemoization;
import org.apache.xpath.objects.ElemFunctionItem;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XNodeSetForDOM;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathArray;
import org.apache.xpath.objects.XPathInlineFunction;
import org.apache.xpath.objects.XPathMap;
import org.apache.xpath.objects.XRTreeFrag;
import org.apache.xpath.operations.CastAs;
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
   * Class field, that represents the value of "xpath-default-namespace" 
   * attribute.
   */
  private String m_xpath_default_namespace = null;

  /**
   * Set the value of "xpath-default-namespace" attribute.
   *
   * @param xpathDefaultNamespace              Value of the "xpath-default-namespace" 
   *                                           attribute.
   */
  public void setXpathDefaultNamespace(String xpathDefaultNamespace)
  {
	  m_xpath_default_namespace = xpathDefaultNamespace; 
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
   * Class field, that represents the value of "override" 
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
   * @param isOverride                    Value of the "override" attribute
   */
  public void setOverride(boolean isOverride)
  {
	  m_override = isOverride;	  
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
   * Class field, that represents the value of "override-extension-function" 
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
   * @param isOverrideExtensionFunc           Value of the "override-extension-function" 
   *                                          attribute.
   */
  public void setOverrideExtensionFunction(boolean isOverrideExtensionFunc)
  {
	  m_override_extension_function = isOverrideExtensionFunc;	  
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
   * Class field, that represents the value of "new-each-time" 
   * attribute.
   */
  private String m_newEachTime = null;

  /**
   * Set the value of "new-each-time" attribute.
   *
   * @param newEachTime   Value of the "new-each-time" attribute
   */
  public void setNewEachTime(String newEachTime)
  {
	  m_newEachTime = newEachTime; 
  }

  /**
   * Get the value of "new-each-time" attribute.
   *  
   * @return		  The value of "new-each-time" attribute 
   */
  public String getNewEachTime() {
	  return m_newEachTime;
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
   * Variable, to indicate whether, an attribute 'expand-text'
   * is declared on xsl:function instruction.
   */
  private boolean m_expand_text_declared;
  
  /**
   * Variable, to indicate whether, XSL transformation
   * should retain the results of previous function calls. 
   */
  private boolean m_cache;
  
  /**
   * Class field, that represents the value of "expand-text" 
   * attribute.
   */
  private boolean m_expand_text;

  /**
   * Set the value of "expand-text" attribute.
   *
   * @param isExpandText               Value of the "expand-text" 
   *                                   attribute.
   */
  public void setExpandText(boolean isExpandText)
  {
	  m_expand_text = isExpandText;
	  m_expand_text_declared = true;
  }

  /**
   * Get the value of "expand-text" attribute.
   *  
   * @return		  The value of "expand-text" attribute 
   */
  public boolean getExpandText() {
	  return m_expand_text;
  }
  
  /**
   * Get a boolean value indicating whether, an "expand-text" 
   * attribute has been declared. 
   */
  public boolean getExpandTextDeclared() {
	  return m_expand_text_declared;
  }
  
  /**
   * Class field, to refer to xsl:function instruction's 
   * attribute "visibility".
   */
  private String m_visibility = "private";
  
  public void setVisibility(String v)
  {
	  m_visibility = v;
  }
  
  public String getVisibility()
  {
	  return m_visibility;
  }
  
  /**
   * Set the value of "cache" attribute.
   *
   * @param isCacheResult              Value of the "cache" attribute
   */
  public void setCache(boolean isCacheResult)
  {
	  m_cache = isCacheResult;
  }

  /**
   * Get the value of "cache" attribute.
   *  
   * @return		  The value of "cache" attribute 
   */
  public boolean getCache() {
	  return m_cache;
  }
  
  /**
   * An XPath expression for 'use-when' attribute. 
   */
  private XPath m_useWhen = null;

  /**
   * Method definition, to set the value of XSL attribute 
   * "use-when".
   * 
   * @param xpath            XPath expression for attribute "use-when"
   */
  public void setUseWhen(XPath xpath)
  {
	  m_useWhen = xpath;  
  }

  /**
   * Method definition, to get the value of XSL attribute 
   * "use-when".
   * 
   * @return			XPath expression for attribute "use-when"
   */
  public XPath getUseWhen()
  {
	  return m_useWhen;
  }

  /**
   * Get an integer representation of the element type.
   *
   * @return         An integer representation of the element, defined in the
   *                 Constants class.
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
   * Method definition, to evaluate result of xsl:function call. 
   * 
   * @param transformer						An XSL TransformerImpl object 
   *                                        instance.
   * @param argSequence						An xdm sequence, representing arguments
   *                                        to xsl:function call.
   * @return                                Result of xsl:function call
   * @throws TransformerException
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
      boolean isOnlyElemTextLiteral = false;
      boolean isOtherElem = false;
      for (ElemTemplateElement elem = getFirstChildElem(); elem != null; elem = elem.getNextSiblingElem()) {
    	 if ((elem instanceof ElemTextLiteral) && !isOtherElem) {
    		isOnlyElemTextLiteral = true; 
         }
    	 else if (!(elem instanceof ElemParam)) {
    		isOtherElem = true;
    		isOnlyElemTextLiteral = false;
    	 }
    	 
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
                   throw new TransformerException("XPST0017 : An XSL function declaration {" + funcNameSpaceUri + "}" + funcLocalName + " "
                   		                                                                     + "has more than one param '" + 
                		                                                                       xslParamName + "' declaration.", srcLocator); 
                }
            }
         }
         else if (elem instanceof ElemParam) {
             ElemParam elemParam = (ElemParam)elem;
             QName elemParamQName = elemParam.getName();
             if (xslParamMap.get(elemParamQName) == null) {
                if (!elemParam.getRequired()) {
                	throw new TransformerException("XTSE0020 : An XSL function declaration {" + funcNameSpaceUri + "}" + funcLocalName + " "
                                                                                              + "param attribute 'required''s value "
                                                                                              + "cannot be 'no' with lexical forms no, false, 0.", srcLocator); 
                }
            	 
                xslParamMap.put(elemParamQName, Integer.valueOf(idx)); 
             }
             else {
            	 throw new TransformerException("XPST0017 : An XSL function declaration {" + funcNameSpaceUri + "}" + funcLocalName + " "
                                                                                           + "has more than one param '" + elemParamQName.toString() 
                                                                                           + "' declaration.", srcLocator); 
             } 
         }
         
         idx++;
      }
      
      if (xslParamMap.size() != argSequence.size()) {
         throw new TransformerException("XPST0017 : An XSL function " + "{" + funcNameSpaceUri + "}" + funcLocalName + 
					                                                                       " call's argument count is not equal "
					                                                                       + "to number of function's param declarations.", srcLocator);  
      }
      
      Collection<Integer> xslParamIdxs = xslParamMap.values();
      Object[] idxArr = xslParamIdxs.toArray();
      if (idxArr.length > 0) {
         Arrays.sort(idxArr);
         int currVal = ((Integer)idxArr[0]).intValue();
         if (currVal != 0) {
            throw new TransformerException("XPST0017 : An XSL function " + "{" + funcNameSpaceUri + "}" + funcLocalName + 
				                                                                           " declaration has a non 'param' declaration as first child "
				                                                                           + "element of 'function'.", srcLocator); 
         }
         
         for (int idx1 = 1; idx1 < idxArr.length; idx1++) {
            int nextVal = ((Integer)idxArr[idx1]).intValue();
            if (nextVal != (currVal + 1)) {
               throw new TransformerException("XPST0017 : An XSL function " + "{" + funcNameSpaceUri + "}" + funcLocalName + 
			                                                                               " declaration has a non 'param' declaration between two "
			                                                                               + "'param' declarations.", srcLocator); 
            }
            else {
               currVal = nextVal;  
            }
         }
      }
      
      if (m_newEachTime != null) {
    	 String xslFuncNewEachTimeNormalizedValue = m_newEachTime.trim();
    	 if (!("yes".equals(xslFuncNewEachTimeNormalizedValue) || "true".equals(xslFuncNewEachTimeNormalizedValue) 
    			                                               || "1".equals(xslFuncNewEachTimeNormalizedValue) 
    			                                               || "no".equals(xslFuncNewEachTimeNormalizedValue) 
    			                                               || "false".equals(xslFuncNewEachTimeNormalizedValue) 
    			                                               || "0".equals(xslFuncNewEachTimeNormalizedValue) 
    			                                               || "maybe".equals(xslFuncNewEachTimeNormalizedValue))) {
    		 throw new TransformerException("XTSE0020 : An XSL function declaration " + "{" + funcNameSpaceUri + "}" + funcLocalName + " "
    		 		                                                                  + "specifies a disallowed value " + m_newEachTime + " for attribute 'new-each-time'. "
							                                                          + "An XSL function declaration's attribute 'new-each-time' may have one of following "
							                                                          + "values : yes, true, 1, no, false, 0, maybe.", srcLocator);
    		 
    	 }
    	 else {
    		// We may try to implement this feature
    	 }    	 
      }
      
      int paramIdx = 0;
      List<XObject> argList = new ArrayList<XObject>();      
      for (ElemTemplateElement elem = getFirstChildElem(); elem != null; 
    		  													elem = elem.getNextSiblingElem()) {
    	  if (elem.getXSLToken() == Constants.ELEMNAME_PARAMVARIABLE) {
    		  XObject argValue = argSequence.item(paramIdx);
    		  argList.add(argValue);
    	  }
      }
      
      if (m_cache) {
    	  XslFunctionMemoization funcMemoization = new XslFunctionMemoization(m_name, argList);
    	  int funcResultCacheSize = (funcMemoization.func_result_cache).size();
    	  for (int idx2 = 0; idx2 < funcResultCacheSize; idx2++) {
    		  XslFunctionMemoization obj2 = (funcMemoization.func_result_cache).get(idx2);
    		  if (obj2.equals(funcMemoization)) {
    			  result = obj2.getResult();

    			  return result;
    		  }
    	  }
      }
      
      VariableStack varStack = xctxt.getVarStack();            
      int argsFrame = 0;
      
      try {
    	  if (xslParamMap.size() > 0) {
    		  /**
    		   * Add all of the xsl:function parameter value mappings to XPath context's
    		   * variable stack, after which the XSL instructions following xsl:param 
    		   * declarations can dereference those parameters.
    		   */
    		  argsFrame = varStack.link(xslParamMap.size());

    		  paramIdx = 0;
    		  for (ElemTemplateElement elem = getFirstChildElem(); elem != null; 
    				                                                   elem = elem.getNextSiblingElem()) {
    			  if (elem.getXSLToken() == Constants.ELEMNAME_PARAMVARIABLE) {
    				  XObject argValue = argSequence.item(paramIdx);    				  
    				  XObject argConvertedVal = null;
    				  String paramAsAttrStrVal = ((ElemParam)elem).getAs();
    				  
    				  if (paramAsAttrStrVal != null) {
    					  List prefixTable = elem.getPrefixTable();    					  
    					  argConvertedVal = getParamValueAsAttributeProcessing(argValue, funcLocalName, funcNameSpaceUri, paramIdx, 
    							                                               prefixTable, paramAsAttrStrVal, transformer, xctxt);
    				  }
    				  else {
    					  argConvertedVal = argValue;  
    				  }

    				  if (argConvertedVal instanceof ResultSequence) {                
    					  XMLNodeCursorImpl nodeSet = XslTransformEvaluationHelper.getXNodeSetFromResultSequence(
    							                                                                              (ResultSequence)argConvertedVal, xctxt);
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

          // Evaluate xsl:function's result, before processing with "as" attribute    	  
    	  ElemTemplateElement elemTemplateElement = getFirstChildElem();
    	  if ((elemTemplateElement instanceof ElemValueOf) && (elemTemplateElement.getNextSiblingElem() == null)) {
    		  // An xsl:function has single xsl:value-of child instruction
    		  ElemValueOf elemValueOf = (ElemValueOf)elemTemplateElement;
    		  XPath selectAttrXPath = elemValueOf.getSelect();
    		  String strValue = null;
    		  if (selectAttrXPath != null) {
    			  int sourceNode = xctxt.getCurrentNode();
    			  XObject xObj = selectAttrXPath.execute(xctxt, sourceNode, prefixResolver);
    			  strValue = XslTransformEvaluationHelper.getStrVal(xObj); 
    		  }
    		  else {		  
    			  strValue = transformer.transformToString(elemTemplateElement);    		     
    		  }

    		  result = new XSString(strValue);    		      		  
    	  }
    	  else {
    		  result = getXslFunctionResult(transformer, xctxt); 
    	  }
      }
      catch (TransformerException ex) {
    	  throw ex;
      }
      finally {
    	  if (xslParamMap.size() > 0) {
    		  varStack.unlink(); 
    	  }    	  
      }
                        
      XObject funcResultConvertedVal = result;
      
      String funcAsAttrStrVal = getAs();
      
      if (funcAsAttrStrVal == null) {
    	 funcAsAttrStrVal = "item()*"; 
      }
      
      if (funcAsAttrStrVal != null) {    	  
    	  try {
    		SequenceTypeData seqExpectedTypeData = SequenceTypeSupport.getSequenceTypeDataFromSeqTypeStr(funcAsAttrStrVal, xctxt, srcLocator);
    		
    		SequenceTypeKindTest seqTypeKindTest = seqExpectedTypeData.getSequenceTypeKindTest();
    		int seqTypeItemOccrIndicator = seqExpectedTypeData.getItemTypeOccurrenceIndicator();
    		
    	    if (isOnlyElemTextLiteral) {    	    	
    		    funcResultConvertedVal = new XSString((XslTransformEvaluationHelper.getStrVal(funcResultConvertedVal)).trim());
    		    
    		    CastAs castAs = new CastAs();
    		    funcResultConvertedVal = castAs.operate(funcResultConvertedVal, seqExpectedTypeData);
    		    
    		    if (m_cache) {
    		       XslFunctionMemoization funcMemoization = new XslFunctionMemoization(m_name, argList, funcResultConvertedVal, this);
    		       (funcMemoization.func_result_cache).add(funcMemoization);
    		    }
    		    
    		    if (SerializerUtils.m_xdmAttrList.size() > 0) {
    		       SerializerUtils.m_xdmAttrList.clear();
    		    }
    		    
    		    return funcResultConvertedVal;
    	    }    	          	         	 
        	 
             if (funcResultConvertedVal instanceof XPathInlineFunction) {
            	if ((seqTypeKindTest != null) && (seqTypeKindTest.getKindVal() == SequenceTypeSupport.ITEM_KIND)) {
            		if (m_cache) {
         		       XslFunctionMemoization funcMemoization = new XslFunctionMemoization(m_name, argList, funcResultConvertedVal, this);
         		       (funcMemoization.func_result_cache).add(funcMemoization);
         		    }
            		
            		if (SerializerUtils.m_xdmAttrList.size() > 0) {
         		       SerializerUtils.m_xdmAttrList.clear();
         		    }
            		
            	    return funcResultConvertedVal;
            	}
            	
            	if (seqExpectedTypeData.getSequenceTypeFunctionTest() != null) {
            		if (m_cache) {
            			XslFunctionMemoization funcMemoization = new XslFunctionMemoization(m_name, argList, funcResultConvertedVal, this);
            			(funcMemoization.func_result_cache).add(funcMemoization);
            		}
            		
            		if (SerializerUtils.m_xdmAttrList.size() > 0) {
         		       SerializerUtils.m_xdmAttrList.clear();
         		    }

            		return funcResultConvertedVal;
            	}
            	else {
            	   throw new TransformerException("XPTY0004 : An XSL function call result for function {" + funcNameSpaceUri + "}" + funcLocalName + 
										                                                                         ", doesn't match the declared function result type " + 
										                                                                         funcAsAttrStrVal + ".", srcLocator); 
            	}
             }
             else if ((funcResultConvertedVal instanceof XPathMap) || (funcResultConvertedVal instanceof XPathArray)) {
            	 if ((seqTypeKindTest != null) && (seqTypeKindTest.getKindVal() == SequenceTypeSupport.ITEM_KIND)) {
            		 if (m_cache) {
            			 XslFunctionMemoization funcMemoization = new XslFunctionMemoization(m_name, argList, funcResultConvertedVal, this);
            			 (funcMemoization.func_result_cache).add(funcMemoization);
            		 }
            		 
            		 if (SerializerUtils.m_xdmAttrList.size() > 0) {
          		        SerializerUtils.m_xdmAttrList.clear();
          		     }

            		 return funcResultConvertedVal;
            	 }
            	 
            	 try {
            		 funcResultConvertedVal = SequenceTypeSupport.castXdmValueToAnotherType(funcResultConvertedVal, funcAsAttrStrVal, null, xctxt);
            		 
            		 if (m_cache) {
            			 XslFunctionMemoization funcMemoization = new XslFunctionMemoization(m_name, argList, funcResultConvertedVal, this);
            			 (funcMemoization.func_result_cache).add(funcMemoization);
            		 }
            		 
            		 if (SerializerUtils.m_xdmAttrList.size() > 0) {
          		        SerializerUtils.m_xdmAttrList.clear();
          		     }

            		 return funcResultConvertedVal;
            	 }
            	 catch (TransformerException ex) {
            		 throw new TransformerException("XPTY0004 : An XSL function call result for function {" + funcNameSpaceUri + "}" + funcLocalName + 
																			            				 ", doesn't match the declared function result type " + 
																			            				 funcAsAttrStrVal + ".", srcLocator);
            	 }             	            	              	
             }
             else if (((XslTransformData.m_xpathNamedFunctionRefSequence).size() > 0) && !ElemVariable.m_isXPathNamedFunctionRefSequenceVar) {            
            	int funcItemSeqSize = (XslTransformData.m_xpathNamedFunctionRefSequence).size();
        		
        		SequenceTypeFunctionTest sequenceTypeFunctionTest = seqExpectedTypeData.getSequenceTypeFunctionTest();        		
        		boolean isSeqCardinalityOk = false;
        		if ((funcItemSeqSize == 0) && ((seqTypeItemOccrIndicator == SequenceTypeSupport.OccurrenceIndicator.ZERO_OR_MANY) || 
        				                       (seqTypeItemOccrIndicator == SequenceTypeSupport.OccurrenceIndicator.ZERO_OR_ONE))) {
        		   isSeqCardinalityOk = true;
        		}
        		else if ((funcItemSeqSize == 1) && ((seqTypeItemOccrIndicator == SequenceTypeSupport.OccurrenceIndicator.ABSENT) || 
        				                            (seqTypeItemOccrIndicator == SequenceTypeSupport.OccurrenceIndicator.ZERO_OR_MANY) || 
        				                            (seqTypeItemOccrIndicator == SequenceTypeSupport.OccurrenceIndicator.ONE_OR_MANY) ||
        				                            (seqTypeItemOccrIndicator == SequenceTypeSupport.OccurrenceIndicator.ZERO_OR_ONE))) {
        		   // An XPath sequence type occurrence indicator with value absent, or any 
        		   // other occurrence indicator is ok.
        		   isSeqCardinalityOk = true;
        		}
        		else if ((seqTypeItemOccrIndicator == SequenceTypeSupport.OccurrenceIndicator.ZERO_OR_MANY) ||
        				 (seqTypeItemOccrIndicator == SequenceTypeSupport.OccurrenceIndicator.ONE_OR_MANY)) {
        		   // here, funcItemSeqSize > 1
        		   isSeqCardinalityOk = true;
        		}
        		
        		if (!isSeqCardinalityOk) {
        			throw new TransformerException("XPTY0004 : An XSL function call result for function {" + funcNameSpaceUri + "}" + funcLocalName + 
													                                                    ", doesn't match the declared function result type " + funcAsAttrStrVal + ". The cardinality " + 
													        					                        funcItemSeqSize + " of function's result, doesn't conform to what is specified in XSL function "
													        					                        + "result's 'as' attribute value " + funcAsAttrStrVal + ".", srcLocator);
        		}
            	
        		if (sequenceTypeFunctionTest != null) {
        			if (sequenceTypeFunctionTest.isAnyFunctionTest()) {
        				if ((XslTransformData.m_xpathNamedFunctionRefSequence).size() == 1) {
        					funcResultConvertedVal = (XslTransformData.m_xpathNamedFunctionRefSequence).item(0);
        					(XslTransformData.m_xpathNamedFunctionRefSequence).remove(0);
        				}
        				else {
        					funcResultConvertedVal = XslTransformData.m_xpathNamedFunctionRefSequence;
        				}
        				
        				if (m_cache) {
        					XslFunctionMemoization funcMemoization = new XslFunctionMemoization(m_name, argList, funcResultConvertedVal, this);
        					(funcMemoization.func_result_cache).add(funcMemoization);
        				}
        				
        				if (SerializerUtils.m_xdmAttrList.size() > 0) {
        	    		   SerializerUtils.m_xdmAttrList.clear();
        	    		}

        				return funcResultConvertedVal;
        			}
        			else {
        				if ((XslTransformData.m_xpathNamedFunctionRefSequence).size() == 1) {
        					funcResultConvertedVal = (XslTransformData.m_xpathNamedFunctionRefSequence).item(0);        					
        					if (funcResultConvertedVal instanceof XPathNamedFunctionReference) {
        					   XPathNamedFunctionReference xpathNamedFunctionReference = (XPathNamedFunctionReference)funcResultConvertedVal;
        					   if (xpathNamedFunctionReference.getXslStylesheetFunction() == null) {
        						   String funcNamespace = xpathNamedFunctionReference.getFuncNamespace();
        						   int actualFuncArity = 0;           
        						   if ((XPathStaticContext.XPATH_BUILT_IN_FUNCS_NS_URI).equals(funcNamespace) && 
        								   																     (Keywords.FUNC_CONCAT_STRING).equals(funcLocalName)) {
        							   actualFuncArity = xpathNamedFunctionReference.getConcatArity();
        						   }
        						   else {
        							   actualFuncArity = xpathNamedFunctionReference.getArity(); 
        						   }
        						   
        						   List<String> expectedParamList = sequenceTypeFunctionTest.getTypedFunctionTestParamSpecList();
        						   if (actualFuncArity != expectedParamList.size()) {
        							   throw new TransformerException("XPTY0004 : An XSL function call result for function {" + funcNameSpaceUri + "}" + funcLocalName + 
                                                                                                                             ", doesn't match the declared function result type " + funcAsAttrStrVal + 
                                                                                                                             ". The function arity specification doesn't match.", srcLocator); 
        						   }        						   
        					   }        					   
        					}
        					
        					(XslTransformData.m_xpathNamedFunctionRefSequence).remove(0);
        				}
        				else {
        					funcResultConvertedVal = XslTransformData.m_xpathNamedFunctionRefSequence;
        					(XslTransformData.m_xpathNamedFunctionRefSequence).clear();
        				}
        				
        				if (m_cache) {
        					XslFunctionMemoization funcMemoization = new XslFunctionMemoization(m_name, argList, funcResultConvertedVal, this);
        					(funcMemoization.func_result_cache).add(funcMemoization);
        				}
        				
        				if (SerializerUtils.m_xdmAttrList.size() > 0) {
        	    		   SerializerUtils.m_xdmAttrList.clear();
        	    		}

        				return funcResultConvertedVal;
        			}
        		}
             }
             
             if (ElemPerformSort.m_namespace_result_seq.size() > 0) {
            	 result = ElemPerformSort.m_namespace_result_seq;            	 
            	 funcResultConvertedVal = preprocessXslFunctionOrAVariableResult(result, funcAsAttrStrVal, xctxt, null);            	 
            	 (ElemPerformSort.m_namespace_result_seq).clear();
             }
             else if (result instanceof XSString) {
            	 funcResultConvertedVal = SequenceTypeSupport.castXdmValueToAnotherType(result, funcAsAttrStrVal, null, xctxt);
             }
             else {
                 funcResultConvertedVal = preprocessXslFunctionOrAVariableResult(result, funcAsAttrStrVal, xctxt, null);
             }
            
             if (funcResultConvertedVal == null) {
                funcResultConvertedVal = SequenceTypeSupport.castXdmValueToAnotherType(result, funcAsAttrStrVal, null, xctxt);                                
                if (funcResultConvertedVal == null) {
                   if ((seqTypeItemOccrIndicator == SequenceTypeSupport.OccurrenceIndicator.ZERO_OR_MANY) || 
                	   (seqTypeItemOccrIndicator == SequenceTypeSupport.OccurrenceIndicator.ZERO_OR_ONE)) {
                	   funcResultConvertedVal = new ResultSequence();
                   }
                   else {
                       throw new TransformerException("XPTY0004 : An XSL function call result for function {" + funcNameSpaceUri + "}" + funcLocalName + 
                                                                                                                          ", doesn't match the declared function result type " + 
                		                                                                                                  funcAsAttrStrVal + ".", srcLocator);
                   }
                }
                else if (funcResultConvertedVal instanceof XMLNodeCursorImpl) {
                   funcResultConvertedVal = xslSequenceTransformTextnode(xctxt, (XMLNodeCursorImpl)funcResultConvertedVal);
                }
             }
         }
         catch (TransformerException ex) {
            throw new TransformerException(ex.getMessage(), srcLocator); 
         }
    	 finally {
    		ElemVariable.m_isXPathNamedFunctionRefSequenceVar = false;    		 
    	 }
      }
      
      if (funcResultConvertedVal instanceof ResultSequence) {
         ResultSequence resultSeq = (ResultSequence)funcResultConvertedVal;
         if (resultSeq.size() == 1) {
            funcResultConvertedVal = resultSeq.item(0);   
         }
      }
      
      if (m_cache) {
    	  XslFunctionMemoization funcMemoization = new XslFunctionMemoization(m_name, argList, funcResultConvertedVal, this);
    	  (funcMemoization.func_result_cache).add(funcMemoization);
      }
      
      if (SerializerUtils.m_xdmAttrList.size() > 0) {
	     SerializerUtils.m_xdmAttrList.clear();
	  }
      
      if (funcResultConvertedVal instanceof XMLNodeCursorImpl) {
    	  funcResultConvertedVal = xslSequenceTransformTextnode(xctxt, (XMLNodeCursorImpl)funcResultConvertedVal);
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
   * @throws TransformerException 
   */
  public void recompose(StylesheetRoot root) throws TransformerException
  {
      super.recompose(root);
  }
  
  /**
   * Method definition, to do data type cast of the supplied xdm value, from
   * an initial result of evaluation of xsl:function/xsl:variable instructions.  
   * 
   * @param xdmValue									The supplied xdm value
   * @param sequenceTypeXPathExprStr                    An XPath sequence type expression 
   *                                                    string.
   * @param xctxt                                       An xpath context object instance
   * @param varQName                                    Variable's Qname value used for
   *                                                    error reporting.
   * @return                                            An xdm sequence object
   * @throws TransformerException
   */
  
  public ResultSequence preprocessXslFunctionOrAVariableResult(XObject xdmValue,
                                                               String sequenceTypeXPathExprStr,
                                                               XPathContext xctxt, QName varQName) throws TransformerException {
     ResultSequence result = null;
     
     final int contextNode = xctxt.getContextNode();
     
     SourceLocator srcLocator = xctxt.getSAXLocator();
     
     XPath seqTypeXPath = new XPath(sequenceTypeXPathExprStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null, true);
     XObject seqTypeExpressionEvalResult = seqTypeXPath.execute(xctxt, contextNode, xctxt.getNamespaceContext());

     SequenceTypeData seqExpectedTypeData = (SequenceTypeData)seqTypeExpressionEvalResult;     
     SequenceTypeKindTest seqTypeKindTest = seqExpectedTypeData.getSequenceTypeKindTest();
     
     int itemTypeOccurenceIndicator = seqExpectedTypeData.getItemTypeOccurrenceIndicator();
     
     if (xdmValue instanceof XPathNamedFunctionReference) {
    	 SequenceTypeFunctionTest seqTypeFunctionTest = seqExpectedTypeData.getSequenceTypeFunctionTest();
    	 if (seqTypeFunctionTest != null) {
    		 XPathNamedFunctionReference xpathNamedFunctionReference = (XPathNamedFunctionReference)xdmValue;    		 
    		 if (seqTypeFunctionTest.isAnyFunctionTest()) {
    			 result = new ResultSequence();
    			 result.add(xpathNamedFunctionReference);

    			 return result;
    		 }
    		 else {    			 
    			 String funcName = xpathNamedFunctionReference.getFuncName();
    			 FunctionTable funcTable = xctxt.getFunctionTable();
    			 Object funcIdInFuncTable = funcTable.getFunctionId(funcName);
    			 Function function = funcTable.getFunction((int)funcIdInFuncTable);
    			 if (function != null) {
    				 result = new ResultSequence();
    				 result.add(xpathNamedFunctionReference);

    				 return result; 
    			 }
        	 }
    	 }    	 
     }
     else if (xdmValue instanceof XMLAttribute) {
    	 XMLAttribute xmlAttribute = (XMLAttribute)xdmValue;
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
    	 
    	 result = new ResultSequence();
		 result.add(xdmValue);

		 return result;
     }
     
     if (xdmValue instanceof ResultSequence) {
    	 ResultSequence rSeq = (ResultSequence)xdmValue;
    	 
    	 int rSeqLength = rSeq.size();
    	 
    	 if (seqTypeKindTest.getKindVal() == SequenceTypeSupport.ITEM_KIND) {
    		 boolean isSeqTypeOccrIndicatorOk = false;
    		 if (itemTypeOccurenceIndicator == OccurrenceIndicator.ZERO_OR_MANY) {
    			 isSeqTypeOccrIndicatorOk = true;
    		 }
    		 else if ((itemTypeOccurenceIndicator == OccurrenceIndicator.ONE_OR_MANY) && (rSeqLength > 0)) {
    			 isSeqTypeOccrIndicatorOk = true;
    		 }
    		 else if ((itemTypeOccurenceIndicator == OccurrenceIndicator.ZERO_OR_ONE) && (rSeqLength <= 1)) {
    			 isSeqTypeOccrIndicatorOk = true;
    		 }
    		 else if ((itemTypeOccurenceIndicator == OccurrenceIndicator.ABSENT) && (rSeqLength == 1)) {
    			 isSeqTypeOccrIndicatorOk = true;
    		 }

    		 if (isSeqTypeOccrIndicatorOk) {
    			 result = new ResultSequence();
    			 for (int idx = 0; idx < rSeqLength; idx++) {
    				 result.add(rSeq.item(idx)); 
    			 }

    			 return result;
    		 }
    	 }
     }
     
     XNodeSetForDOM xNodeSetForDom = (XNodeSetForDOM)xdmValue;
     
     DTMNodeList dtmNodeList = (DTMNodeList)(xNodeSetForDom.object());

     Node localRootNode = dtmNodeList.item(0);
     NodeList nodeList = localRootNode.getChildNodes();
     int nodeSetLen = nodeList.getLength();          
     
     if (nodeSetLen == 1) {
        Node node = nodeList.item(0);
        short nodeType = node.getNodeType();
        if (nodeType == Node.TEXT_NODE) {
             String strVal = ((Text)node).getNodeValue();             
             if (seqTypeKindTest == null) {                                 
                if (strVal.contains(ElemSequence.STRING_VAL_SER_SUFFIX)) {
                   String[] strParts = strVal.split(ElemSequence.STRING_VAL_SER_SUFFIX);
                   for (int idx = 0; idx < strParts.length; idx++) {
                      String seqItemStrVal = strParts[idx];
                      XObject xObject = getXSTypedAtomicValue(seqItemStrVal, seqExpectedTypeData.getBuiltInSequenceType());
                      if (xObject != null) {
                    	 if (result == null) {
                    		result = new ResultSequence(); 
                    	 }
                    	 
                         result.add(xObject);
                      }
                   }
                }
                else if (strVal.contains(ElemSequence.STRING_VAL_SER_INTEGER_SUFFIX)) {
                    String[] strParts = strVal.split(ElemSequence.STRING_VAL_SER_INTEGER_SUFFIX);
                    for (int idx = 0; idx < strParts.length; idx++) {
                       String seqItemStrVal = strParts[idx];
                       int xsBuiltInSeqType = seqExpectedTypeData.getBuiltInSequenceType();                       
                       XObject xObject = getXSTypedAtomicValue(seqItemStrVal, xsBuiltInSeqType);
                       if (xObject instanceof XSInteger) {
                    	   if (result == null) {
                    		   result = new ResultSequence(); 
                    	   }

                    	   result.add(xObject);
                       }
                    }
                }
                else if (strVal.contains(ElemSequence.STRING_VAL_SER_DECIMAL_SUFFIX)) {
                    String[] strParts = strVal.split(ElemSequence.STRING_VAL_SER_DECIMAL_SUFFIX);
                    for (int idx = 0; idx < strParts.length; idx++) {
                       String seqItemStrVal = strParts[idx];
                       XObject xObject = getXSTypedAtomicValue(seqItemStrVal, seqExpectedTypeData.getBuiltInSequenceType());
                       if (xObject instanceof XSDecimal) {
                    	   if (result == null) {
                    		   result = new ResultSequence(); 
                    	   }

                    	   result.add(xObject);
                       }
                    }
                }
                else if (strVal.contains(ElemSequence.STRING_VAL_SER_DOUBLE_SUFFIX)) {
                    String[] strParts = strVal.split(ElemSequence.STRING_VAL_SER_DOUBLE_SUFFIX);
                    for (int idx = 0; idx < strParts.length; idx++) {
                       String seqItemStrVal = strParts[idx];
                       XObject xObject = getXSTypedAtomicValue(seqItemStrVal, seqExpectedTypeData.getBuiltInSequenceType());
                       if ((xObject instanceof XSDouble) || (xObject instanceof XSInteger)) {
                    	   if (result == null) {
                    		   result = new ResultSequence(); 
                    	   }

                    	   result.add(xObject);
                       }
                    }
                }
                else if (strVal.contains(ElemSequence.STRING_VAL_SER_FLOAT_SUFFIX)) {
                    String[] strParts = strVal.split(ElemSequence.STRING_VAL_SER_FLOAT_SUFFIX);
                    for (int idx = 0; idx < strParts.length; idx++) {
                       String seqItemStrVal = strParts[idx];
                       XObject xObject = getXSTypedAtomicValue(seqItemStrVal, seqExpectedTypeData.getBuiltInSequenceType());
                       if (xObject instanceof XSFloat) {
                    	   if (result == null) {
                    		   result = new ResultSequence(); 
                    	   }

                    	   result.add(xObject);
                       }
                    }
                }
                else {
                   XObject xObject = getXSTypedAtomicValue(strVal, seqExpectedTypeData.getBuiltInSequenceType());
                   if (xObject != null) {
                	   if (result == null) {
                		   result = new ResultSequence(); 
                	   }
                	   
                	   result.add(xObject);
                   }
                }
             }
             else if (seqTypeKindTest.getKindVal() == SequenceTypeSupport.ITEM_KIND) {
            	 if (strVal.contains(ElemSequence.STRING_VAL_SER_SUFFIX)) {
            		 String[] strParts = strVal.split(ElemSequence.STRING_VAL_SER_SUFFIX);
            		 for (int idx = 0; idx < strParts.length; idx++) {
            			 String seqItemStrVal = strParts[idx];
            			 if (result == null) {
            				 result = new ResultSequence(); 
            			 }
            			 
            			 result.add(new XSString(seqItemStrVal));
            		 }
            	 }
            	 else if (strVal.contains(ElemSequence.STRING_VAL_SER_INTEGER_SUFFIX)) {
            		 String[] strParts = strVal.split(ElemSequence.STRING_VAL_SER_INTEGER_SUFFIX);
            		 for (int idx = 0; idx < strParts.length; idx++) {
            			 String seqItemStrVal = strParts[idx];
            			 if (result == null) {
            				 result = new ResultSequence(); 
            			 }
            			 
            			 try {
            				Integer intValue1 = Integer.valueOf(seqItemStrVal);
            				result.add(new XSInteger(seqItemStrVal));
            			 }
            			 catch(NumberFormatException ex) {
            				// no op
            			 }            			             			 
            		 }
            	 }
            	 else if (strVal.contains(ElemSequence.STRING_VAL_SER_DECIMAL_SUFFIX)) {
            		 String[] strParts = strVal.split(ElemSequence.STRING_VAL_SER_DECIMAL_SUFFIX);
            		 for (int idx = 0; idx < strParts.length; idx++) {
            			 String seqItemStrVal = strParts[idx];
            			 if (result == null) {
            				 result = new ResultSequence(); 
            			 }
            			 
            			 try {
            				Double dblValue1 = Double.valueOf(seqItemStrVal);
            				result.add(new XSDecimal(seqItemStrVal));
            			 }
            			 catch(NumberFormatException ex) {
            				// no op
            			 }
            		 }
            	 }
            	 else if (strVal.contains(ElemSequence.STRING_VAL_SER_DOUBLE_SUFFIX)) {
            		 String[] strParts = strVal.split(ElemSequence.STRING_VAL_SER_DOUBLE_SUFFIX);
            		 for (int idx = 0; idx < strParts.length; idx++) {
            			 String seqItemStrVal = strParts[idx];
            			 if (result == null) {
            				 result = new ResultSequence(); 
            			 }
            			 
            			 try {
            				Double dblValue1 = Double.valueOf(seqItemStrVal);
            				result.add(new XSDouble(seqItemStrVal));
            			 }
            			 catch(NumberFormatException ex) {
            				// no op 
            			 }
            		 }
            	 }
            	 else if (strVal.contains(ElemSequence.STRING_VAL_SER_FLOAT_SUFFIX)) {
            		 String[] strParts = strVal.split(ElemSequence.STRING_VAL_SER_FLOAT_SUFFIX);
            		 for (int idx = 0; idx < strParts.length; idx++) {
            			 String seqItemStrVal = strParts[idx];
            			 if (result == null) {
            				 result = new ResultSequence(); 
            			 }
            			 
            			 try {
            				Float fltValue1 = Float.valueOf(seqItemStrVal);
            				result.add(new XSFloat(seqItemStrVal));
            			 }
            			 catch(NumberFormatException ex) {
            				// no op 
            			 }
            		 }
            	 }
            	 else {
            		 if (result == null) {
        				 result = new ResultSequence(); 
        			 }
        			 
        			 result.add(new XSString(strVal));
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
        	 
        	 XSTypeDefinition xsTypeDefn = xNodeSetForDom.getXsTypeDefinition();
        	 if (xsTypeDefn == null) {
        		if (typeDataTypeLocalName == null) {
        			if (typeNodeLocalName == null) {        				
        				result = getResultSequenceFromNode(node, seqTypeKindTest, xctxt);
        				
        				return result;
        			}        			
        		}
        		else if ((XMLConstants.W3C_XML_SCHEMA_NS_URI).equals(typeDataTypeUri) && (Keywords.XS_UNTYPED).equals(typeDataTypeLocalName)) {
    				if ((nodeNsUri == null) && (typeNodeNsUri == null) && typeNodeLocalName.equals(nodeLocalName)) {
    					result = getResultSequenceFromNode(node, seqTypeKindTest, xctxt);

        				return result;
    				}
    				else if ((nodeNsUri != null) && (nodeNsUri.equals(typeNodeNsUri)) && typeNodeLocalName.equals(nodeLocalName)) {
    					result = getResultSequenceFromNode(node, seqTypeKindTest, xctxt);

        				return result;
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
    	 if ((result != null) && (result.size() > 1)) {
    		 String errMesg = null;
    		 if (m_name != null) {
    			 errMesg = "XTTE0780 : A sequence of more than one item, is not allowed as a result of call to function '" + m_name.toString() + "'. "
    					 																		+ "The expected result type of this function is " 
    					                                                                        + sequenceTypeXPathExprStr + "."; 
    		 }
    		 else if (varQName != null) {
    			 errMesg = "XTTE0570 : A sequence of more than one item, is not allowed as the value of variable '$" + varQName.toString() + "'. "
    					 																		+ "This variable has expected type " 
    					                                                                        + sequenceTypeXPathExprStr + "."; 
    		 }
    		 else {
    			 errMesg = "XTTE0570 : A sequence of more than one item, is not allowed as the result of XSL 'evaluate' instruction's evaluation. "
    					 																		+ "An XSL 'evaluate' instruction's expected type is " 
    					                                                                        + sequenceTypeXPathExprStr + ".";
    		 }

    		 throw new TransformerException(errMesg); 
    	 }
     }
     
     return result;
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
  
  public boolean isOverrideAttrDeclared() {
	  return m_override_attr_declared;
  }

  public boolean isOverrideExtensionFunctionAttrDeclared() {
	  return m_override_extension_function_attr_declared;
  }
  
  /**
   * Method definition, to get xsl:function's parameter information.
   * 
   * @return                   List of ElemParam objects, or null.
   */
  public List<ElemParam> getFuncParamList() {
	  List<ElemParam> funcParamList = new ArrayList<ElemParam>();

	  ElemTemplateElement elemTemplateElem = this.getFirstChildElem();
	  if (elemTemplateElem instanceof ElemParam) {
		  funcParamList.add((ElemParam)elemTemplateElem);
	  }

	  return funcParamList; 
  }
  
  /**
   * Method definition, to evaluate xsl:function instruction's contained 
   * sequence constructor to produce xsl:function's result, before xsl:function's 
   * result is processed with xsl:function element's "as" attribute.
   * 
   * @param transformer							An XSL TransformerImpl object
   * @param xctxt           					An XPath context object
   * @return                					Result of xsl:function's evaluation  
   * @throws TransformerException
   */
  private XObject getXslFunctionResult(TransformerImpl transformer, XPathContext xctxt) 
		                                                                             throws TransformerException {		
	  
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

		  if (XslTransformData.m_xpathInlineFunction != null) {
			  result = XslTransformData.m_xpathInlineFunction;			  
			  XslTransformData.m_xpathInlineFunction = null;
		  }
		  else if (XslTransformData.m_xpathMap != null) {
			  result = XslTransformData.m_xpathMap;			  
			  XslTransformData.m_xpathMap = null;
		  }
		  else if (XslTransformData.m_xpathArray != null) {
			  result = XslTransformData.m_xpathArray;			  
			  XslTransformData.m_xpathArray = null;
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
   * Method definition, to construct xdm typed value, for the supplied string
   * value and Xalan's XML Schema built-in type's integer code value.
   * 
   * @param strVal									The supplied string value
   * @param xsBuiltInTypeId							Xalan's XML Schema built-in type's 
   *                                                integer code value. 
   * @return                                        Constructed xdm value
   * @throws TransformerException
   */
  private XObject getXSTypedAtomicValue(String strVal, int xsBuiltInTypeId) throws TransformerException {
      
      XObject result = null;
     
      if (xsBuiltInTypeId == SequenceTypeSupport.BOOLEAN) {
         boolean boolVal = ("true".equals(strVal) || "1".equals(strVal)) ? true : false;
         result = new XSBoolean(boolVal);
      }
      else if (xsBuiltInTypeId == SequenceTypeSupport.STRING) {
         result = new XSString(strVal);
      }
      else if (xsBuiltInTypeId == SequenceTypeSupport.XS_DATE) {
         result = XSDate.parseDate(strVal);
      }
      else if (xsBuiltInTypeId == SequenceTypeSupport.XS_DATETIME) {
         result = XSDateTime.parseDateTime(strVal);
      }
      else if (xsBuiltInTypeId == SequenceTypeSupport.XS_TIME) {
    	 result = XSTime.parseTime(strVal);
      }
      else if (xsBuiltInTypeId == SequenceTypeSupport.XS_DURATION) {
         result = XSDuration.parseDuration(strVal);
      }
      else if (xsBuiltInTypeId == SequenceTypeSupport.XS_DAYTIME_DURATION) {
         result = XSDayTimeDuration.parseDayTimeDuration(strVal);
      }
      else if (xsBuiltInTypeId == SequenceTypeSupport.XS_YEARMONTH_DURATION) {
         result = XSYearMonthDuration.parseYearMonthDuration(strVal); 
      }
      else if (xsBuiltInTypeId == SequenceTypeSupport.XS_DECIMAL) {
         result = new XSDecimal(strVal); 
      }
      else if (xsBuiltInTypeId == SequenceTypeSupport.XS_INTEGER) {
         result = new XSInteger(strVal); 
      }
      else if (xsBuiltInTypeId == SequenceTypeSupport.XS_LONG) {
         result = new XSLong(strVal);
      }
      else if (xsBuiltInTypeId == SequenceTypeSupport.XS_INT) {
         result = new XSInt(strVal); 
      }
      else if (xsBuiltInTypeId == SequenceTypeSupport.XS_SHORT) {
         result = new XSShort(strVal); 
      }
      else if (xsBuiltInTypeId == SequenceTypeSupport.XS_BYTE) {
         result = new XSByte(strVal); 
      }
      else if (xsBuiltInTypeId == SequenceTypeSupport.XS_UNSIGNED_LONG) {
         result = new XSUnsignedLong(strVal); 
      }
      else if (xsBuiltInTypeId == SequenceTypeSupport.XS_UNSIGNED_INT) {
         result = new XSUnsignedInt(strVal); 
      }
      else if (xsBuiltInTypeId == SequenceTypeSupport.XS_UNSIGNED_SHORT) {
         result = new XSUnsignedShort(strVal); 
      }
      else if (xsBuiltInTypeId == SequenceTypeSupport.XS_UNSIGNED_BYTE) {
          result = new XSUnsignedByte(strVal); 
      }
      else if (xsBuiltInTypeId == SequenceTypeSupport.XS_NON_NEGATIVE_INTEGER) {
         result = new XSNonNegativeInteger(strVal); 
      }
      else if (xsBuiltInTypeId == SequenceTypeSupport.XS_POSITIVE_INTEGER) {
         result = new XSPositiveInteger(strVal); 
      }
      else if (xsBuiltInTypeId == SequenceTypeSupport.XS_NON_POSITIVE_INTEGER) {
         result = new XSNonPositiveInteger(strVal); 
      }
      else if (xsBuiltInTypeId == SequenceTypeSupport.XS_NEGATIVE_INTEGER) {
         result = new XSNegativeInteger(strVal); 
      }
      else if (xsBuiltInTypeId == SequenceTypeSupport.XS_DOUBLE) {
         result = new XSDouble(strVal); 
      }
      else if (xsBuiltInTypeId == SequenceTypeSupport.XS_FLOAT) {
         result = new XSFloat(strVal); 
      }
      else if (xsBuiltInTypeId == SequenceTypeSupport.XS_ANY_URI) {
         result = new XSAnyURI(strVal); 
      }
      else {
    	 result = new XSString(strVal); 
      }
     
      return result;
  }
  
  /**
   * Method definition, to check whether two XSTypeDefinition objects, 
   * represent the same schema type. 
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
   * Method definition, to do type checking and required modification of xsl:function's xsl:param
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
					  throw new TransformerException("XPTY0004 : An XSL function call argument at position " + (paramIdx + 1) + " for "
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
							  argConvertedVal = new ElemFunctionItem(elemFunction);
						  }
					  }
					  catch (TransformerException ex) {
						  // no op
					  }
				  }
			  }
			  else if (seqExpectedTypeData.getBuiltInSequenceType() != 0) {
				  // This should work, if node doesn't have attributes,
				  // and only has text node child. revisit
				  int nodeHandle = nodeSet.asNode(xctxt);
				  DTM dtm = xctxt.getDTM(nodeHandle);
				  XMLString xmlStr1 = dtm.getStringValue(nodeHandle);
				  String str1 = xmlStr1.toString();
				  XObject xObj = SequenceTypeSupport.castXdmValueToAnotherType(new XSString(str1), seqExpectedTypeData, false);
				  if (xObj != null) {
					  argConvertedVal = xObj;  
				  }
				  else {
					  throw new TransformerException("XPTY0004 : An XSL function call argument at position " + (paramIdx + 1) + " for "
																								  + "function {" + funcNameSpaceUri + "}" + funcLocalName + ", doesn't "
																								  + "match the declared parameter type " + paramAsAttrStrVal + ".", srcLocator); 
				  }
			  }
		  }
		  		  		  		  
		  if (argConvertedVal == null) {
			  boolean isSrcValueTypeOk = true;			  
			  if (srcValue instanceof ResultSequence) {
				  ResultSequence rSeq = (ResultSequence)srcValue;
				  int rSeqLength = rSeq.size();
				  int seqTypeOccrIndicator = seqExpectedTypeData.getItemTypeOccurrenceIndicator();
				  boolean isOccrIndicatorOk = false;
				  if ((seqTypeOccrIndicator == SequenceTypeSupport.OccurrenceIndicator.ABSENT) && (rSeqLength == 1)) {
					  isOccrIndicatorOk = true;  
				  }
				  else if ((seqTypeOccrIndicator == SequenceTypeSupport.OccurrenceIndicator.ONE_OR_MANY) && (rSeqLength >= 1)) {
					  isOccrIndicatorOk = true; 
				  }
				  else if ((seqTypeOccrIndicator == SequenceTypeSupport.OccurrenceIndicator.ZERO_OR_MANY) && (rSeqLength >= 0)) {
					  isOccrIndicatorOk = true;
				  }
				  else if ((seqTypeOccrIndicator == SequenceTypeSupport.OccurrenceIndicator.ZERO_OR_ONE) && 
						                                                                                   ((rSeqLength == 0) || (rSeqLength == 1))) {
					  isOccrIndicatorOk = true;
				  }

				  if (isOccrIndicatorOk) {
					  for (int idx = 0; idx < rSeqLength; idx++) {
						  XObject xObj = rSeq.item(idx);
						  if (seqExpectedTypeData.getSequenceTypeFunctionTest() != null) {
							  if (!((xObj instanceof XPathNamedFunctionReference) || (xObj instanceof XPathInlineFunction))) {
								  isSrcValueTypeOk = false;
								  // REVISIT : To check for xObj value's conformance with details in 
								  // the object seqExpectedTypeData.getSequenceTypeFunctionTest()
							  }
						  }
					  }
				  }
				  else {
					  isSrcValueTypeOk = false;
				  }

				  if (isSrcValueTypeOk) {
					  argConvertedVal = srcValue;  
				  }
			  }
	      }
		  
		  if ((argConvertedVal == null) && (seqExpectedTypeData.getSequenceTypeFunctionTest() != null) 
				                                                                            && (srcValue instanceof XPathInlineFunction)) {
			  // REVISIT : To check for argValue value's conformance with details in 
			  // the object seqExpectedTypeData.getSequenceTypeFunctionTest()
			  argConvertedVal = srcValue; 
		  }

		  if (argConvertedVal == null) {
			  argConvertedVal = SequenceTypeSupport.castXdmValueToAnotherType(srcValue, paramAsAttrStrVal, null, xctxt, prefixTable); 
		  }

		  if (argConvertedVal == null) {
			  throw new TransformerException("XPTY0004 : An XSL function call argument at position " + (paramIdx + 1) + " for "
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
			  throw new TransformerException("XPTY0004 : An XSL function call argument at position " + (paramIdx + 1) + " for "
																					  + "function {" + funcNameSpaceUri + "}" + funcLocalName + ", doesn't "
																					  + "match the declared parameter type " + paramAsAttrStrVal + ".", srcLocator);
		  }
	  }

	  return argConvertedVal;	  
	}
  
   /**
    * Method definition, to get an XML node instance as a ResultSequence 
    * object. 
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
    
    /**
     * Method definition, to do possible XSL transformation of an 
     * xdm text node produced by xsl:sequence instruction.
     * 
     * @param xctxt							    An XPathContext object instance
     * @param nodeRef							An XMLNodeCursorImpl node reference
     * @return									An xdm item, produced by this method
     */
    private XObject xslSequenceTransformTextnode(XPathContext xctxt, XMLNodeCursorImpl nodeRef) {
    	
    	XObject result = null;

    	XMLNodeCursorImpl clonedXdmNode = null;
    	
    	try {
    		clonedXdmNode = (XMLNodeCursorImpl)nodeRef.clone();
    		
    		DTMCursorIterator dtmIter = nodeRef.iterRaw();
    		int nodeHandle = dtmIter.nextNode();
    		if ((nodeHandle != DTM.NULL) && (dtmIter.nextNode() == DTM.NULL)) {
    			// An xdm nodeset, is of size one    			
    			DTM dtm = xctxt.getDTM(nodeHandle);
    			if (dtm.getNodeType(nodeHandle) == DTM.TEXT_NODE) {
    				Node node = dtm.getNode(nodeHandle);
    				String nodeStrValue = node.getNodeValue();
    				if ((nodeStrValue != null) && nodeStrValue.contains(ElemSequence.SER_SUFFIX_ID)) {
    					// An xdm text node has been produced, by xsl:sequence instruction. 
    					// Transform text node value, to an appropriate xdm sequence.
    					ResultSequence rSeq = new ResultSequence(); 
    					String[] strArray1 = nodeStrValue.split(ElemSequence.SER_SUFFIX_ID);
    					int arrLength = strArray1.length;
    					for (int idx2 = 0; idx2 < arrLength; idx2++) {
    						String arrItemStr1 = strArray1[idx2];
    						if ((idx2 % 2 == 1) && " ".equals(arrItemStr1)) {
    						   continue;	
    						}
    						
    						try {
    							Double dbl1 = Double.valueOf(arrItemStr1);
    							rSeq.add(new XSDouble(dbl1.doubleValue()));
    						}
    						catch (NumberFormatException ex) {
    							rSeq.add(new XSString(arrItemStr1)); 
    						}
    					}

    					result =  rSeq;
    				}
    				else {
    					result = clonedXdmNode;
    				}
    			}
    			else {
    				result = clonedXdmNode;
    			}
    		}
    		else {
    			result = clonedXdmNode;
    		}
    	}
    	catch (CloneNotSupportedException ex) {
    		result = clonedXdmNode; 
    	}

    	return result;
    }

}
