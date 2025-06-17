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

import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xml.dtm.DTMManager;
import org.apache.xml.serializer.SerializationHandler;
import org.apache.xpath.Expression;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.axes.LocPathIterator;
import org.apache.xpath.composite.XPathIfExpr;
import org.apache.xpath.composite.XPathLetExpr;
import org.apache.xpath.functions.FuncRound;
import org.apache.xpath.functions.Function;
import org.apache.xpath.functions.WrongNumberArgsException;
import org.apache.xpath.functions.XPathDynamicFunctionCall;
import org.apache.xpath.functions.XSL3ConstructorOrExtensionFunction;
import org.apache.xpath.functions.XSL3FunctionService;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathArray;
import org.apache.xpath.objects.XPathMap;
import org.apache.xpath.objects.XString;
import org.apache.xpath.operations.Operation;
import org.apache.xpath.operations.Variable;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import xml.xpath31.processor.types.XSAnyType;
import xml.xpath31.processor.types.XSDayTimeDuration;
import xml.xpath31.processor.types.XSDecimal;
import xml.xpath31.processor.types.XSDouble;
import xml.xpath31.processor.types.XSInteger;
import xml.xpath31.processor.types.XSNumericType;
import xml.xpath31.processor.types.XSQName;

/**
 * Implementation of XSLT xsl:value-of instruction.
 * 
 * Ref : https://www.w3.org/TR/xslt-30/#element-value-of
 * 
 * @author Scott Boag <scott_boag@us.ibm.com>
 * @author Ilene Seelemann
 *         Morris Kwan <mkwan@apache.org>, Christine Li <jycli@apache.org>
 *         
 * @author Mukul Gandhi <mukulg@apache.org>
 *         (XSLT 3 specific changes, to this class)
 * 
 * @xsl.usage advanced
 */
public class ElemValueOf extends ElemTemplateElement {
    
  static final long serialVersionUID = 3490728458007586786L;

  /**
   * The select expression to be executed.
   */
  private XPath m_selectExpression = null;

  /**
   * True if the pattern is a simple ".".
   */
  private boolean m_isDot = false;
  
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
   * Set the "select" attribute.
   * The required select attribute is an expression; this expression
   * is evaluated and the resulting object is converted to a
   * string as if by a call to the string function.
   *
   * @param v The value to set for the "select" attribute.
   */
  public void setSelect(XPath v)
  {

    if (null != v)
    {
      String s = v.getPatternString();

      m_isDot = (null != s) && s.equals(".");
    }

    m_selectExpression = v;
  }

  /**
   * Get the "select" attribute.
   * The required select attribute is an expression; this expression
   * is evaluated and the resulting object is converted to a
   * string as if by a call to the string function.
   *
   * @return The value of the "select" attribute.
   */
  public XPath getSelect()
  {
    return m_selectExpression;
  }
  
  /**
   * The separator attribute value.
   */
  private String m_separator = null;
  
  /**
   * Set the value of separator attribute. 
   */
  public void setSeparator(String separator) {
     m_separator = separator; 
  }
  
  /**
   * Get the value of separator attribute. 
   */
  public String getSeparator() {
	 return m_separator; 
  }

  /**
   * Tells if this element should disable escaping.
   * @serial
   */
  private boolean m_disableOutputEscaping = false;

  /**
   * Set the "disable-output-escaping" attribute.
   * Normally, the xml output method escapes & and < (and
   * possibly other characters) when outputting text nodes.
   * This ensures that the output is well-formed XML. However,
   * it is sometimes convenient to be able to produce output
   * that is almost, but not quite well-formed XML; for
   * example, the output may include ill-formed sections
   * which are intended to be transformed into well-formed
   * XML by a subsequent non-XML aware process. For this reason,
   * XSLT provides a mechanism for disabling output escaping.
   * An xsl:value-of or xsl:text element may have a
   * disable-output-escaping attribute; the allowed values
   * are yes or no; the default is no; if the value is yes,
   * then a text node generated by instantiating the xsl:value-of
   * or xsl:text element should be output without any escaping.
   * @see <a href="http://www.w3.org/TR/xslt#disable-output-escaping">disable-output-escaping in XSLT Specification</a>
   *
   * @param v The value to set for the "disable-output-escaping" attribute.
   */
  public void setDisableOutputEscaping(boolean v)
  {
    m_disableOutputEscaping = v;
  }

  /**
   * Get the "disable-output-escaping" attribute.
   * Normally, the xml output method escapes & and < (and
   * possibly other characters) when outputting text nodes.
   * This ensures that the output is well-formed XML. However,
   * it is sometimes convenient to be able to produce output
   * that is almost, but not quite well-formed XML; for
   * example, the output may include ill-formed sections
   * which are intended to be transformed into well-formed
   * XML by a subsequent non-XML aware process. For this reason,
   * XSLT provides a mechanism for disabling output escaping.
   * An xsl:value-of or xsl:text element may have a
   * disable-output-escaping attribute; the allowed values
   * are yes or no; the default is no; if the value is yes,
   * then a text node generated by instantiating the xsl:value-of
   * or xsl:text element should be output without any escaping.
   * @see <a href="http://www.w3.org/TR/xslt#disable-output-escaping">disable-output-escaping in XSLT Specification</a>
   *
   * @return The value of the "disable-output-escaping" attribute.
   */
  public boolean getDisableOutputEscaping()
  {
    return m_disableOutputEscaping;
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
    return Constants.ELEMNAME_VALUEOF;
  }
  
  /**
   * This class field's value denotes the constant string "XSL_SEQ" which 
   * is used during processing of xsl:sequence instruction.
   */
  private static final String XSL_SEQ = "XSL_SEQ";

  /**
   * This function is called after everything else has been
   * recomposed, and allows the template to set remaining
   * values that may be based on some other property that
   * depends on recomposition.
   *
   * NEEDSDOC @param sroot
   *
   * @throws TransformerException
   */
  public void compose(StylesheetRoot sroot) throws TransformerException
  {

    super.compose(sroot);

    java.util.Vector vnames = (sroot.getComposeState()).getVariableNames();
    
    m_vars = (Vector)(vnames.clone()); 
    m_globals_size = (sroot.getComposeState()).getGlobalsSize();

    if (m_selectExpression != null) {
        m_selectExpression.fixupVariables(vnames, m_globals_size);
    }
  }

  /**
   * Return the node name.
   *
   * @return The node name
   */
  public String getNodeName()
  {
    return Constants.ELEMNAME_VALUEOF_STRING;
  }

  /**
   * Execute the string expression and copy the text to the
   * result tree.
   * The required select attribute is an expression; this expression
   * is evaluated and the resulting object is converted to a string
   * as if by a call to the string function. The string specifies
   * the string-value of the created text node. If the string is
   * empty, no text node will be created. The created text node will
   * be merged with any adjacent text nodes.
   * @see <a href="http://www.w3.org/TR/xslt#value-of">value-of in XSLT Specification</a>
   *
   * @param transformer non-null reference to the the current transform-time state.
   *
   * @throws TransformerException
   */
  public void execute(TransformerImpl transformer) throws TransformerException
  {

    XPathContext xctxt = transformer.getXPathContext();
    
    SourceLocator srcLocator = xctxt.getSAXLocator();
    
    SerializationHandler rth = transformer.getResultTreeHandler();

    if (transformer.getDebug())
      transformer.getTraceManager().emitTraceEvent(this);

    try
    {      
        xctxt.pushNamespaceContext(this);

        int current = xctxt.getCurrentNode();

        xctxt.pushCurrentNodeAndExpression(current, current);

        if (m_disableOutputEscaping)
          rth.processingInstruction(
            javax.xml.transform.Result.PI_DISABLE_OUTPUT_ESCAPING, "");

        try
        {
          Expression expr = null;
          
          if (m_selectExpression != null) {        	  
        	 Node childNode = getFirstChild();
        	 if (childNode != null) {
                throw new TransformerException("XTSE0870 : An xsl:value-of instruction cannot have both a "
                		                                    + "'select' attribute and non-empty content.", srcLocator);
        	 }
        	 else {
        		expr = m_selectExpression.getExpression(); 
        	 }
          }          
          else {
        	  evaluateXslValueOfSeqConstructorAndEmitResult(transformer, xctxt, rth);
        	  
        	  return;
          }

          if (transformer.getDebug())
          {
            XObject obj = expr.execute(xctxt);

            transformer.getTraceManager().emitSelectedEvent(current, this,
                                                       "select", m_selectExpression, obj);
            obj.dispatchCharactersEvents(rth);
          }
          else
          {
              XObject xpath3ContextItem = xctxt.getXPath3ContextItem();
              if (m_isDot && xpath3ContextItem != null) {                  
                  String strValue = XslTransformEvaluationHelper.getStrVal(xpath3ContextItem);
                  (new XString(strValue)).dispatchCharactersEvents(rth);
              }
              else {
                  if (expr instanceof XSL3ConstructorOrExtensionFunction) {
                	  XSL3ConstructorOrExtensionFunction xpathFunc = (XSL3ConstructorOrExtensionFunction)expr;
                	  XSL3FunctionService xslFunctionService = xctxt.getXSLFunctionService();
                      XObject evalResult = xslFunctionService.callFunction(xpathFunc, transformer, xctxt);
                      if (evalResult != null) {
                    	  if (evalResult instanceof XSDayTimeDuration) {
                    		  serializeXsDaytimeDurationValue((XSDayTimeDuration)evalResult, 3, xctxt, rth);
                    	  }
                    	  else if (evalResult instanceof XSQName) {
                    		  XSQName xsQName = (XSQName)evalResult;
                    		  String nsPrefix = xsQName.getPrefix();
                    		  String strValue = null;
                    		  if ((nsPrefix != null) && !"".equals(nsPrefix)) {
                    			 strValue = nsPrefix + ":" + xsQName.getLocalPart(); 
                    		  }
                    		  else {
                    			 strValue = ((XSQName) evalResult).stringValue();  
                    		  }
                    		  
                    		  (new XString(strValue)).dispatchCharactersEvents(rth);
                    	  }
                    	  else {
                    		  String strValue = XslTransformEvaluationHelper.getStrVal(evalResult);                                                       
                    		  strValue = preProcessStrBeforeXslSerialization(strValue);                          
                    		  (new XString(strValue)).dispatchCharactersEvents(rth);
                    	  }
                      }
                      else {
                          expr.executeCharsToContentHandler(xctxt, rth);   
                      }
                  }
                  else if (expr instanceof Function) {
                      XObject evalResult = ((Function)expr).execute(xctxt);
                      String strValue = null;
                      
                      if (evalResult instanceof XSAnyType) {
                    	  strValue = ((XSAnyType)evalResult).stringValue();    
                      }                                            
                      else if (evalResult instanceof XMLNodeCursorImpl) {
                    	  XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)evalResult;
                    	  DTMCursorIterator dtmCursorIter = xmlNodeCursorImpl.iterRaw();
                    	  int nextNode;
                    	  StringBuffer strBuffer = new StringBuffer();
                    	  while ((nextNode = dtmCursorIter.nextNode()) != DTM.NULL) {
                    		  XMLNodeCursorImpl node = new XMLNodeCursorImpl(nextNode, xctxt);
                    		  strBuffer.append(node.str() + " ");
                    	  }
                    	  
                    	  strValue = (strBuffer.toString()).trim(); 
                      }
                      else if (evalResult instanceof ResultSequence) {
                    	  strValue = getEffectiveSequenceStrValue((ResultSequence)evalResult);
                      }
                      else if (evalResult instanceof XPathArray) {
                    	  XPathArray xpathArr = (XPathArray)evalResult;
                    	  List<XObject> nativeArr = xpathArr.getNativeArray();

                    	  ResultSequence rSeq = new ResultSequence();
                    	  for (int idx = 0; idx < nativeArr.size(); idx++) {                           
                    		  rSeq.add(nativeArr.get(idx));
                    	  }

                    	  strValue = getEffectiveSequenceStrValue(rSeq);
                      }
                      else if (evalResult instanceof XPathMap) {
                    	  throw new TransformerException("FOTY0013 : Cannot do an XPath atomization of a map "
                    			                                                     + "(ref, https://www.w3.org/TR/xpath-31/#id-atomization).", srcLocator);
                      }
                      else {
                    	  strValue = XslTransformEvaluationHelper.getStrVal(evalResult);
                      }
                      
                      (new XString(strValue)).dispatchCharactersEvents(rth);
                  }
                  else if (expr instanceof Variable) {          	  
                      XObject evalResult = ((Variable)expr).execute(xctxt);
                      
                      String strValue = null;
                      
                      if (evalResult instanceof XSAnyType) {
                          strValue = ((XSAnyType)evalResult).stringValue();    
                      }
                      else if (evalResult instanceof XPathArray) {
                    	 XPathArray xpathArr = (XPathArray)evalResult;
                    	 List<XObject> nativeArr = xpathArr.getNativeArray();                    	                          
                         
                         ResultSequence rSeq = new ResultSequence();
                         for (int idx = 0; idx < nativeArr.size(); idx++) {                           
                        	rSeq.add(nativeArr.get(idx));
                         }
                         
                         strValue = getEffectiveSequenceStrValue(rSeq);
                      }
                      else if (evalResult instanceof XPathMap) {
                    	 throw new TransformerException("FOTY0013 : Cannot do an XPath atomization of a map "
                    	 		                                              + "(ref, https://www.w3.org/TR/xpath-31/#id-atomization).", srcLocator);
                      }
                      else if (evalResult instanceof XNumber) {
                    	 XNumber xNumber = (XNumber)evalResult;
                    	 if (xNumber.isXsInteger()) {
                    		XSInteger xsInteger = xNumber.getXsInteger();
                    		strValue = xsInteger.stringValue(); 
                    	 }
                    	 else if (xNumber.isXsDecimal()) {
                    		XSDecimal xsDecimal = xNumber.getXsDecimal();
                     		strValue = xsDecimal.stringValue(); 
                    	 }
                    	 else if (xNumber.isXsDouble()) {
                    		XSDouble xsDouble = xNumber.getXsDouble();
                      		strValue = xsDouble.stringValue(); 
                    	 }
                    	 else {
                    		strValue = XslTransformEvaluationHelper.getStrVal(evalResult);
                    	 }
                      }
                      else if (evalResult instanceof ResultSequence) {
                    	 strValue = getEffectiveSequenceStrValue((ResultSequence)evalResult); 
                      }
                      else if (evalResult instanceof XMLNodeCursorImpl) {                    	  
                    	  XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)evalResult;
                    	  DTMCursorIterator iter = xmlNodeCursorImpl.iterRaw();                    	  
                    	  StringBuffer strBuff = new StringBuffer();
                    	  int nextNode;
                    	  while ((nextNode = iter.nextNode()) != DTM.NULL) {
                    		  XMLNodeCursorImpl nodeImpl = new XMLNodeCursorImpl(nextNode, xctxt);
                    		  String nodeStrValue = nodeImpl.str();
                    		  strBuff.append(nodeStrValue + " ");
                    	  }

                    	  strValue = strBuff.toString();
                    	  if (strValue.length() > 1) {
                    		 strValue = strValue.substring(0, strValue.length() - 1); 
                    	  }
                      }
                      else {
                    	 strValue = XslTransformEvaluationHelper.getStrVal(evalResult);  
                      }
                      
                      (new XString(strValue)).dispatchCharactersEvents(rth);
                  }
                  else if (expr instanceof Operation) {                     
                     XObject evalResult = expr.execute(xctxt);
                     
                     String strValue = null;
                     
                     if (evalResult instanceof ResultSequence) {                         
                         strValue = getEffectiveSequenceStrValue((ResultSequence)evalResult);
                     }                     
                     else {
                         strValue = XslTransformEvaluationHelper.getStrVal(evalResult);
                     }
                     
                     (new XString(strValue)).dispatchCharactersEvents(rth);
                  }
                  else if (expr instanceof XPathDynamicFunctionCall) {
                     XPathDynamicFunctionCall dfc = (XPathDynamicFunctionCall)expr;
                     
                     XObject evalResult = dfc.execute(xctxt);
                     
                     String strValue = XslTransformEvaluationHelper.getStrVal(evalResult);
                     
                     (new XString(strValue)).dispatchCharactersEvents(rth);
                  }
                  else if (expr instanceof LocPathIterator) {
                     LocPathIterator locPathIterator = (LocPathIterator)expr;
                     
                     Function func = locPathIterator.getFuncExpr();
                     XPathDynamicFunctionCall dfc = locPathIterator.getDynamicFuncCallExpr();
                     
                     DTMCursorIterator dtmIter = null;                     
                     try {
                        dtmIter = locPathIterator.asIterator(xctxt, current);
                     }
                     catch (ClassCastException ex) {
                        // no op
                     }
                     catch (Exception ex) {
                    	// no op 
                     }
                     
                     if (dtmIter != null) {
                        int nextNode;
                        StringBuffer strBuff = new StringBuffer();
                        while ((nextNode = dtmIter.nextNode()) != DTM.NULL)
                        {
                           XMLNodeCursorImpl xdmNodeObj = new XMLNodeCursorImpl(nextNode, xctxt);
                           String resultStr = "";
                           if (func != null) {
                        	  // Evaluate an XPath expression like /a/b/funcCall(..).
                        	  // Find one result item for a sequence of items.
                              XObject evalResult = evaluateXPathSuffixFunction(xctxt, srcLocator, func, xdmNodeObj);
                              resultStr = XslTransformEvaluationHelper.getStrVal(evalResult);                               
                           }
                           else if (dfc != null) {
                        	   // Evaluate an XPath expression like /a/b/$funcCall(..).
                        	   // Find one result item for a sequence of items.
                               XObject evalResult = evaluateXPathSuffixDfc(xctxt, dfc, xdmNodeObj);
                               resultStr = XslTransformEvaluationHelper.getStrVal(evalResult);                               
                           }
                           else {
                              resultStr = xdmNodeObj.str();
                           }
                           strBuff.append(resultStr + " ");
                        }
                         
                        String nodeSetStrValue = strBuff.toString();
                        if (nodeSetStrValue.length() > 1) {
                           nodeSetStrValue = nodeSetStrValue.substring(0, 
                                                                 nodeSetStrValue.length() - 1);
                           (new XString(nodeSetStrValue)).dispatchCharactersEvents(rth);
                        }
                     }
                     else {
                        String xpathPatternStr = m_selectExpression.getPatternString();
                        
                        if (xpathPatternStr.startsWith("$") && xpathPatternStr.contains("[") && 
                                                                                    xpathPatternStr.endsWith("]")) {
                           // Within this 'if' clause, we handle the case, where the XPath expression is
                           // syntactically of type $varName[expr], for example $varName[1], $varName[$idx],
                           // $varName[funcCall(arg)] etc, and $varName resolves to a 'ResultSequence' object.
                            
                           String varRefXPathExprStr = "$" + xpathPatternStr.substring(1, xpathPatternStr.indexOf('['));
                           String xpathIndexExprStr = xpathPatternStr.substring(xpathPatternStr.indexOf('[') + 1, 
                                                                                                   xpathPatternStr.indexOf(']'));
                           
                           ElemTemplateElement elemTemplateElement = (ElemTemplateElement)xctxt.getNamespaceContext();
                           List<XMLNSDecl> prefixTable = null;
                           if (elemTemplateElement != null) {
                              prefixTable = (List<XMLNSDecl>)elemTemplateElement.getPrefixTable();
                           }
                           
                           // Evaluate the, variable reference XPath expression
                           if (prefixTable != null) {
                              varRefXPathExprStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(
                                                                                                             varRefXPathExprStr, 
                                                                                                             prefixTable);
                           }
                           
                           XPath xpathObj = new XPath(varRefXPathExprStr, srcLocator, 
                                                                                 xctxt.getNamespaceContext(), XPath.SELECT, null);
                           if (m_vars != null) {
                              xpathObj.fixupVariables(m_vars, m_globals_size);                            
                           }
                           
                           XObject varEvalResult = xpathObj.execute(xctxt, xctxt.getCurrentNode(), xctxt.getNamespaceContext());
                           
                           // Evaluate the, xdm sequence index XPath expression
                           if (prefixTable != null) {
                              xpathIndexExprStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(
                                                                                                              xpathIndexExprStr, 
                                                                                                              prefixTable);
                           }
                            
                           xpathObj = new XPath(xpathIndexExprStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
                           
                           if (m_vars != null) {
                              xpathObj.fixupVariables(m_vars, m_globals_size);                            
                           }
                           
                           XObject seqIndexEvalResult = xpathObj.execute(xctxt, xctxt.getCurrentNode(), 
                                                                                                  xctxt.getNamespaceContext());
                           if (varEvalResult instanceof ResultSequence) {
                              ResultSequence resultSeq = (ResultSequence)varEvalResult; 
                              
                              if (seqIndexEvalResult instanceof XNumber) {
                                 double dValIndex = ((XNumber)seqIndexEvalResult).num();
                                 if (dValIndex == (int)dValIndex) {
                                    XObject evalResult = resultSeq.item((int)dValIndex - 1);
                                    String strValue = XslTransformEvaluationHelper.getStrVal(evalResult);
                                    (new XString(strValue)).dispatchCharactersEvents(rth);
                                 }
                                 else {
                                    throw new javax.xml.transform.TransformerException("XPTY0004 : an index value used with an xdm "
                                                                                                        + "sequence reference, is not an integer.", 
                                                                                                             srcLocator); 
                                 }
                              }
                              else if (seqIndexEvalResult instanceof XSNumericType) {
                                 String indexStrVal = ((XSNumericType)seqIndexEvalResult).stringValue();
                                 double dValIndex = (Double.valueOf(indexStrVal)).doubleValue();
                                 if (dValIndex == (int)dValIndex) {
                                    XObject evalResult = resultSeq.item((int)dValIndex - 1);
                                    String strValue = XslTransformEvaluationHelper.getStrVal(evalResult);
                                    (new XString(strValue)).dispatchCharactersEvents(rth);                                    
                                 }
                                 else {
                                     throw new javax.xml.transform.TransformerException("XPTY0004 : an index value used with an xdm "
                                                                                                        + "sequence reference, is not an integer.", 
                                                                                                             srcLocator); 
                                 }
                              }
                              else {
                                 throw new javax.xml.transform.TransformerException("XPTY0004 : an index value used with an xdm sequence "
                                                                                                       + "reference, is not numeric.", srcLocator);  
                              }
                           }                           
                        }
                     }
                  }
                  else if (expr instanceof XPathLetExpr) {
                     XPathLetExpr letExpr = (XPathLetExpr)expr;
                      
                     XObject evalResult = letExpr.execute(xctxt);                     
                     String strValue = XslTransformEvaluationHelper.getStrVal(evalResult);
                     
                     (new XString(strValue)).dispatchCharactersEvents(rth);
                  }
                  else if (expr instanceof XPathIfExpr) {
                      XPathIfExpr ifExpr = (XPathIfExpr)expr;
                       
                      XObject evalResult = ifExpr.execute(xctxt);                     
                      String strValue = XslTransformEvaluationHelper.getStrVal(evalResult);
                      
                      (new XString(strValue)).dispatchCharactersEvents(rth);
                  }
                  else if (expr instanceof XString) {
                	  XString xStr = (XString)expr;
                	  String strValue = xStr.str();
                	  String xpathPatternStr = m_selectExpression.getPatternString();
                	  if ("''''".equals(xpathPatternStr) && "".equals(strValue)) {
                		  // An XSL stylesheet has an effective character value ' (which in 
                		  // escaped form is '' and lexically specified with XPath expression 
                		  // string as '''') that needs to be emitted to stylesheet's result.
                		  XString xStr2 = new XString("'");
                		  xStr2.executeCharsToContentHandler(xctxt, rth);
                	  }
                	  else {
                		  xStr.executeCharsToContentHandler(xctxt, rth); 
                	  }
                  }
                  else {
                	  XObject evalResult = expr.execute(xctxt);                     
                      String strValue = XslTransformEvaluationHelper.getStrVal(evalResult);
                      if (m_separator != null) {
                    	 strValue = strValue.replace(" ", m_separator);  
                      }
                      
                      XString xStr = new XString(strValue);

                      xStr.executeCharsToContentHandler(xctxt, rth);
                  }
              }
          }
        }
        finally
        {
          if (m_disableOutputEscaping)
            rth.processingInstruction(
              javax.xml.transform.Result.PI_ENABLE_OUTPUT_ESCAPING, "");

          xctxt.popNamespaceContext();
          xctxt.popCurrentNodeAndExpression();
        }
    }
    catch (SAXException se)
    {
        throw new TransformerException(se);
    }
    catch (Exception ex) {
    	TransformerException te = new TransformerException(ex);
    	te.setLocator(this);
    	throw te;
    }
    finally
    {
      if (transformer.getDebug())
	    transformer.getTraceManager().emitTraceEndEvent(this); 
    }
  }

  /**
   * Add a child to the child list.
   *
   * @param newChild Child to add to children list
   *
   * @return Child just added to children list
   *
   * @throws DOMException
   */
  public ElemTemplateElement appendChild(ElemTemplateElement newChild)
  {
	  return super.appendChild(newChild);
  }
  
  /**
   * Call the children visitors.
   * @param visitor The visitor whose appropriate method will be called.
   */
  protected void callChildVisitors(XSLTVisitor visitor, boolean callAttrs)
  {
  	  if(callAttrs)
  		 m_selectExpression.getExpression().callVisitors(m_selectExpression, visitor);
      super.callChildVisitors(visitor, callAttrs);
  }
  
  /**
   * We might have earlier added a suffix STRING_VAL_SERIALIZATION_SUFFIX to 
   * certain string values for the purpose of correct XSLT transformation
   * serialization to the result. If those suffixes are still present on 
   * those strings, they need to be trimmed to restore original string 
   * values.
   */
  private String preProcessStrBeforeXslSerialization(String str) {
	 String resultStr = str;
	 
	 if (resultStr.contains(ElemSequence.STRING_VAL_SERIALIZATION_SUFFIX)) {
	     resultStr = resultStr.replace(ElemSequence.STRING_VAL_SERIALIZATION_SUFFIX, "");
	 }
	 
	 return resultStr;
  }
  
  /**
   * Get an effective string value of sequence to be emitted,
   * considering xsl:value-of element's separator attribute if 
   * available.
   */
  private String getEffectiveSequenceStrValue(ResultSequence seq) {	
	String strValue = seq.str();
	
	if (m_separator != null) {
	   strValue = strValue.replace(" ", m_separator);
	}
	
	return strValue;
  }
  
  /**
   * This method evaluates xsl:value-of instruction's contained sequence constructor,
   * and emits the result of evaluation to XSL transform's output.
   */
  private void evaluateXslValueOfSeqConstructorAndEmitResult(TransformerImpl transformer, XPathContext xctxt, 
		                                                     SerializationHandler rth) throws TransformerException, SAXException {
	  
	  int rtfNodeHandle = transformer.transformToRTF(this);
	  DTMManager dtmMgr = xctxt.getDTMManager();        	  
	  DTM dtm = dtmMgr.getDTM(rtfNodeHandle);        	  
	  int nodeType = dtm.getNodeType(rtfNodeHandle);

	  StringBuffer nodeStrValBuff = new StringBuffer();

	  if (nodeType == DTM.DOCUMENT_NODE) {
		  Node docNode = dtm.getNode(rtfNodeHandle);
		  NodeList nodeList = docNode.getChildNodes();
		  if ((nodeList != null) && (nodeList.getLength() > 0)) {
			  for (int idx = 0; idx < nodeList.getLength(); idx++) {
				  Node node = nodeList.item(idx);
				  String nodeStrVal = node.getTextContent();
				  if (idx < (nodeList.getLength() - 1)) {
					  if (m_separator != null) {
						  nodeStrValBuff.append(nodeStrVal + m_separator);
					  }
					  else {
						  nodeStrValBuff.append(nodeStrVal); 
					  }
				  }
				  else {
					  nodeStrValBuff.append(nodeStrVal);
				  }
			  }
		  }

		  String nodeStrVal = (nodeStrValBuff.toString()).trim();
		  if (nodeStrVal.contains(XSL_SEQ)) {
			  nodeStrVal = nodeStrVal.replace(XSL_SEQ, "");
			  if (m_separator != null) {        				
				  nodeStrVal = nodeStrVal.replace(" ", m_separator);
				  nodeStrVal = nodeStrVal.substring(0, (nodeStrVal.length() - 1));
				  String rTrimmedSeparator = strRtrim(m_separator);
				  if (!m_separator.equals(rTrimmedSeparator)) {
					  int lIdx = nodeStrVal.lastIndexOf(rTrimmedSeparator);
					  if (lIdx > 0) {
						  nodeStrVal = nodeStrVal.substring(0, lIdx);
					  } 
				  }
			  }
			  else {
				  nodeStrVal = nodeStrVal.trim();
			  }
		  }
		  (new XString(nodeStrVal)).dispatchCharactersEvents(rth);
	  }
	  else {
		  // xsl:value-of's 'separator' attribute is ignored 
		  // in this case. 
		  Node node = dtm.getNode(rtfNodeHandle);
		  String nodeStrVal = node.getTextContent();
		  (new XString(nodeStrVal)).dispatchCharactersEvents(rth);
	  }
   }
  
   /**
    * Method definition to trim whitespace characters from RHS of 
    * an input string, and returning resulting string.
    */
   private String strRtrim(String str) {
	  String resultStr = null;
	  
	  if (str.length() == 0) {
		 resultStr = "";  
	  }
	  else if (str.length() == 1) {
		 if (Character.isWhitespace(str.charAt(0))) {
			resultStr = ""; 
		 }
		 else {
			resultStr = str;
		 }
	  }
	  else {
		 char chr = str.charAt(str.length() - 1);
		 if (Character.isWhitespace(chr)) {
			resultStr = str.substring(0, str.length() - 1);
			resultStr = strRtrim(resultStr); 
		 }
		 else {
			resultStr = str; 
		 }
	  }
	  
	  return resultStr;
   }
   
   /**
    * Method definition to serialize xs:dayTimeDuration value, by serializing
    * xs:dayTimeDuration value's seconds component upto a specified number
    * of decimal places.  
    * 
    * @param xsDayTimeDurationValue					The supplied xs:dayTimeDuration value 
    * @param precision								Precision value for mathematical rounding
    * @param xctxt									XPath context object
    * @param rth									An XSL SerializationHandler run-time object
    * @throws WrongNumberArgsException
    * @throws TransformerException
    * @throws SAXException
    */
   private void serializeXsDaytimeDurationValue(XSDayTimeDuration xsDayTimeDurationValue, int precision, 
		   										XPathContext xctxt, SerializationHandler rth)
		   																				throws WrongNumberArgsException, TransformerException, SAXException {
	   double dbl = xsDayTimeDurationValue.seconds();
	   
	   if (dbl != 0) {
		   FuncRound funcRound = new FuncRound();
		   funcRound.setArg0(new XSDouble(dbl));
		   funcRound.setArg(new XSInteger(String.valueOf(precision)), 1);
		   String roundedDblResult = (funcRound.execute(xctxt)).str();
		   String xsDayTimeDurationStrValue = xsDayTimeDurationValue.stringValue();
		   String secsStrValue = (Double.valueOf(dbl)).toString();
		   int idx = xsDayTimeDurationStrValue.indexOf(secsStrValue);
		   String prefixStr = xsDayTimeDurationStrValue.substring(0, idx);
		   xsDayTimeDurationStrValue = prefixStr + roundedDblResult + "S";
		   xsDayTimeDurationStrValue = preProcessStrBeforeXslSerialization(xsDayTimeDurationStrValue);
		   
		   (new XString(xsDayTimeDurationStrValue)).dispatchCharactersEvents(rth);
	   }
	   else {
		   (new XString(xsDayTimeDurationValue.stringValue())).dispatchCharactersEvents(rth);
	   }
   }

}
