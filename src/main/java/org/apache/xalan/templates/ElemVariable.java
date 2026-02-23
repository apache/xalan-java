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
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.serialize.SerializerUtils;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.xslt.util.XslTransformData;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xml.utils.QName;
import org.apache.xml.utils.XMLString;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionNode;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathStaticContext;
import org.apache.xpath.axes.LocPathIterator;
import org.apache.xpath.axes.SelfIteratorNoPredicate;
import org.apache.xpath.compiler.Keywords;
import org.apache.xpath.composite.SequenceTypeData;
import org.apache.xpath.composite.SequenceTypeFunctionTest;
import org.apache.xpath.composite.SequenceTypeKindTest;
import org.apache.xpath.composite.SequenceTypeSupport;
import org.apache.xpath.composite.SequenceTypeSupport.OccurrenceIndicator;
import org.apache.xpath.composite.XPathArrayConstructor;
import org.apache.xpath.composite.XPathForExpr;
import org.apache.xpath.composite.XPathIfExpr;
import org.apache.xpath.composite.XPathMapConstructor;
import org.apache.xpath.composite.XPathNamedFunctionReference;
import org.apache.xpath.composite.XPathSequenceConstructor;
import org.apache.xpath.composite.XPathTextAndNodeExpr;
import org.apache.xpath.functions.Function;
import org.apache.xpath.functions.Function2Args;
import org.apache.xpath.functions.Function3Args;
import org.apache.xpath.functions.FunctionMultiArgs;
import org.apache.xpath.functions.FunctionOneArg;
import org.apache.xpath.functions.XPathDynamicFunctionCall;
import org.apache.xpath.functions.XSL3ConstructorOrExtensionFunction;
import org.apache.xpath.functions.XSL3FunctionService;
import org.apache.xpath.functions.XSLFunctionBuilder;
import org.apache.xpath.objects.ElemFunctionItem;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XBooleanStatic;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XNodeSetForDOM;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathArray;
import org.apache.xpath.objects.XPathInlineFunction;
import org.apache.xpath.objects.XPathMap;
import org.apache.xpath.objects.XRTreeFrag;
import org.apache.xpath.objects.XRTreeFragSelectWrapper;
import org.apache.xpath.objects.XString;
import org.apache.xpath.objects.XdmAttributeItem;
import org.apache.xpath.objects.XdmCommentItem;
import org.apache.xpath.objects.XdmNamespaceItem;
import org.apache.xpath.objects.XdmProcessingInstructionItem;
import org.apache.xpath.operations.ArrowOp;
import org.apache.xpath.operations.Operation;
import org.apache.xpath.operations.Range;
import org.apache.xpath.operations.SimpleMapOperator;
import org.apache.xpath.operations.UnaryOperation;
import org.apache.xpath.operations.Variable;
import org.apache.xpath.patterns.NodeTest;
import org.w3c.dom.NodeList;

import xml.xpath31.processor.types.XSAnyAtomicType;
import xml.xpath31.processor.types.XSAnyType;
import xml.xpath31.processor.types.XSDecimal;
import xml.xpath31.processor.types.XSDouble;
import xml.xpath31.processor.types.XSFloat;
import xml.xpath31.processor.types.XSInt;
import xml.xpath31.processor.types.XSInteger;
import xml.xpath31.processor.types.XSNumericType;
import xml.xpath31.processor.types.XSQName;
import xml.xpath31.processor.types.XSString;
import xml.xpath31.processor.types.XSUntypedAtomic;

/**
 * Implementation of XSLT xsl:variable element.
 * 
 * @author Scott Boag <scott_boag@us.ibm.com>
 * @author Gary L Peskin <garyp@apache.org>
 * @author Joseph Kesselman <keshlam@alum.mit.edu>, Morris Kwan <mkwan@apache.org>,
 *         Brian James Minchau <minchau@apache.org>, Henry Zongaro <zongaro@ca.ibm.com>, 
 *         Christine Li <jycli@apache.org>
 *         
 * @author Mukul Gandhi <mukulg@apache.org>
 *         (XSLT 3.0 specific changes, to this class)
 *
 * @xsl.usage advanced
 */
public class ElemVariable extends ElemTemplateElement
{
  
  static final long serialVersionUID = 9111131075322790061L;

  /**
   * Constructor ElemVariable
   *
   */
  public ElemVariable(){}

  /**
   * This is the index into the stack frame.
   */
  protected int m_index;
  
  /**
   * The stack frame size for this variable if it is a global variable 
   * that declares an RTF, which is equal to the maximum number 
   * of variables that can be declared in the variable at one time.
   */
  int m_frameSize = -1;
  
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
   * Sets the relative position of this variable within the stack frame (if local)
   * or the global area (if global).  Note that this should be called only for
   * global variables since the local position is computed in the compose() method.
   */
  public void setIndex(int index)
  {
    m_index = index;
  }

  /**
   * If this element is not at the top-level, get the relative position of the
   * variable into the stack frame.  If this variable is at the top-level, get
   * the relative position within the global area.
   */
  public int getIndex()
  {
    return m_index;
  }

  /**
   * The value of the "select" attribute.
   * 
   */
  private XPath m_selectPattern;

  /**
   * Set the "select" attribute.
   * If the variable-binding element has a select attribute,
   * then the value of the attribute must be an expression and
   * the value of the variable is the object that results from
   * evaluating the expression. In this case, the content
   * of the variable must be empty.
   *
   * @param v Value to set for the "select" attribute.
   */
  public void setSelect(XPath v)
  {
    m_selectPattern = v;
  }

  /**
   * Get the "select" attribute.
   * If the variable-binding element has a select attribute,
   * then the value of the attribute must be an expression and
   * the value of the variable is the object that results from
   * evaluating the expression. In this case, the content
   * of the variable must be empty.
   *
   * @return Value of the "select" attribute.
   */
  public XPath getSelect()
  {
    return m_selectPattern;
  }

  /**
   * The value of the "name" attribute.
   * 
   */
  protected QName m_qname;

  /**
   * Set the "name" attribute.
   * Both xsl:variable and xsl:param have a required name
   * attribute, which specifies the name of the variable. The
   * value of the name attribute is a QName, which is expanded
   * as described in [2.4 Qualified Names].
   * @see <a href="http://www.w3.org/TR/xslt#qname">qname in XSLT Specification</a>
   *
   * @param v Value to set for the "name" attribute.
   */
  public void setName(QName v)
  {
    m_qname = v;
  }

  /**
   * Get the "name" attribute.
   * Both xsl:variable and xsl:param have a required name
   * attribute, which specifies the name of the variable. The
   * value of the name attribute is a QName, which is expanded
   * as described in [2.4 Qualified Names].
   * @see <a href="http://www.w3.org/TR/xslt#qname">qname in XSLT Specification</a>
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
  private String m_asAttr = null;
  
  /**
   * The value of this variable 'true' signifies that, an 
   * xsl:variable's value is XPath named function reference.
   */
  static boolean m_isXPathNamedFunctionRefSequenceVar = false;
  
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
   * Tells if this is a top-level variable or param, or not.
   */
  private boolean m_isTopLevel = false;

  /**
   * Set if this is a top-level variable or param, or not.
   *
   * @param v Boolean indicating whether this is a top-level variable
   * or param, or not.
   */
  public void setIsTopLevel(boolean v)
  {
    m_isTopLevel = v;
  }

  /**
   * Get if this is a top-level variable or param, or not.
   *
   * @return Boolean indicating whether this is a top-level variable
   * or param, or not.
   */
  public boolean getIsTopLevel()
  {
    return m_isTopLevel;
  }
  
  /**
   * Class field, that represents the value of "xpath-default-namespace" 
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
   * Variable to indicate whether, an attribute 'expand-text'
   * is declared on xsl:variable instruction.
   */
  private boolean m_expand_text_declared;
  
  /**
   * Class field, that represents the value of "expand-text" 
   * attribute.
   */
  private boolean m_expand_text;

  /**
   * Set the value of "expand-text" attribute.
   *
   * @param v   Value of the "expand-text" attribute
   */
  public void setExpandText(boolean v)
  {
	  m_expand_text = v;
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
   * Class field, representing value of xsl:variable/xsl:param's
   * attribute "static".
   */
  private boolean m_static;
  
  public void setStatic(boolean v) {
	 m_static = v;  
  }
  
  public boolean getStatic() {
	 return m_static; 
  }
  
  /**
   * Class field, to refer to xsl:variable instruction's 
   * attribute "visibility".
   */
  private String m_visibility = "private";
  
  public void setVisibility(String str) {
	 m_visibility = str; 
  }
  
  public String getVisibility() {
	 return m_visibility; 
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
    return Constants.ELEMNAME_VARIABLE;
  }

  /**
   * Return the node name.
   *
   * @return The node name
   */
  public String getNodeName()
  {
    return Constants.ELEMNAME_VARIABLE_STRING;
  }

  /**
   * Copy constructor.
   *
   * @param param An element created from an xsl:variable
   *
   * @throws TransformerException
   */
  public ElemVariable(ElemVariable param) throws TransformerException
  {
    m_selectPattern = param.m_selectPattern;
    m_qname = param.m_qname;   
    m_isTopLevel = param.m_isTopLevel;
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
       transformer.getTraceManager().emitTraceEvent(this);
    
    XPathContext xctxt = transformer.getXPathContext();
    
    SourceLocator srcLocator = xctxt.getSAXLocator();

    final int sourceNode = xctxt.getCurrentNode();
    
    XObject var = null;
    
    if (m_useWhen != null) {              
    	boolean result1 = isXPathExpressionStatic(m_useWhen.getExpression());
    	if (result1) {
    		XObject useWhenResult = m_useWhen.execute(xctxt, sourceNode, xctxt.getNamespaceContext());
    		if (useWhenResult.bool()) {
    			var = getValue(transformer, sourceNode); 
    		}
    		else {
    			return;
    		}
    	}
    	else {
    		throw new TransformerException("XPST0008 : XSL variables other than XSLT static variables/parameters, cannot be "
                    																									+ "used within XPath static expression.", srcLocator);
    	}
    }
    else {
        var = getValue(transformer, sourceNode);
    }
    
    if (var instanceof ResultSequence) {
    	ResultSequence rSeq = (ResultSequence)var;
    	if (rSeq.isXdmParentlessSiblingNodes()) {
    		var = getNodeSetFromSequence(xctxt, rSeq);
    	}
    }
    
    if (var instanceof XPathInlineFunction) 
    {
        Map<QName, XObject> xpathVarMap = xctxt.getXPathVarMap();
        xpathVarMap.put(m_qname, var);
    }
    else {
    	(xctxt.getVarStack()).setLocalVariable(m_index, var);
    }
    
    if (transformer.getDebug())
	    transformer.getTraceManager().emitTraceEndEvent(this);         
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
  public XObject getValue(TransformerImpl transformer, int sourceNode) 
                                                                    throws TransformerException
  {

    XObject var = null;
    
    XPathContext xctxt = transformer.getXPathContext();

    xctxt.pushCurrentNode(sourceNode);
    
    SourceLocator srcLocator = xctxt.getSAXLocator();
    
    Expression selectExpression = null;
    
    if (m_isTopLevel) {
		if (this instanceof ElemParam) {
			if (((ElemParam)this).getRequired() && ((ElemParam)this).getRequiredDeclared()) {
				if ((m_selectPattern == null) && (getFirstChildElem() == null)) {
					throw new TransformerException("XTDE0050 : An XSL stylesheet required top level parameter '" + m_qname.toString() 
					                                                                                            + "''s value is not provided.", srcLocator);
									
				}
			}
		} 
		else if (m_selectPattern != null) {
			/**
			 * This is XSL stylesheet top level xsl:variable declaration
			 * having "select" attribute. We need to check for variable
			 * cyclic dependencies.
			 * 
			 * This has been done to solve, various W3C XSLT 3.0 test cases,
			 * within following code.
			 */
			
			List <QName> qNameList = new ArrayList<QName>();
			List<QName> list1 = XslTransformData.m_xsl_variable_qname_list;
			int size1 = list1.size();
			for (int idx = 0; idx < size1; idx++) {
				qNameList.add(list1.get(idx));
			}
									
			String xpathPatternStr1 = m_selectPattern.getPatternString();
			if (xpathPatternStr1 != null) {
				List<XMLNSDecl> prefixTable = (List<XMLNSDecl>)this.getPrefixTable();
				if (prefixTable != null) {
				   xpathPatternStr1 = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(xpathPatternStr1, prefixTable);					
				}
				
				XPath xpathSelect1 = new XPath(xpathPatternStr1, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
				
				if (m_vars != null) {
					xpathSelect1.fixupVariables(m_vars, m_globals_size);
				}

				Expression expr1 = xpathSelect1.getExpression();
				if (expr1 instanceof XPathInlineFunction) {
					XPathInlineFunction xpathInlineFunc = (XPathInlineFunction)expr1;
					String xpathInlineFuncBody1 = xpathInlineFunc.getFuncBodyXPathExprStr();
					if (prefixTable != null) {
						xpathInlineFuncBody1 = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(xpathInlineFuncBody1, prefixTable);					
					}

					xpathSelect1 = new XPath(xpathInlineFuncBody1, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);

					if (m_vars != null) {
						xpathSelect1.fixupVariables(m_vars, m_globals_size);
					}
					
					expr1 = xpathSelect1.getExpression();
					if (expr1 instanceof XPathIfExpr) {
						XPathIfExpr xpathIfExpr = (XPathIfExpr)expr1;
						String xpathIfCondStr = xpathIfExpr.getIfConditionXPathStr();
						String xpathIfThenStr = xpathIfExpr.getThenExprXPathStr();
						String xpathIfElseStr = xpathIfExpr.getElseExprXPathStr();
						XPath xpathSelect_a = new XPath(xpathIfCondStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
						XPath xpathSelect_b = new XPath(xpathIfThenStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
						XPath xpathSelect_c = new XPath(xpathIfElseStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
						Expression a1 = xpathSelect_a.getExpression();
						Expression a2 = xpathSelect_b.getExpression();
						Expression a3 = xpathSelect_c.getExpression();
						if (a1 instanceof XPathDynamicFunctionCall) {
							XPathDynamicFunctionCall dfc1 = (XPathDynamicFunctionCall)a1;
							String dfcName1 = dfc1.getFuncRefVarName();
							XslTransformData.m_xsl_variable_qname_list.add(new QName(dfcName1));
							List<String> funcArgList1 = dfc1.getArgList();
							int funcArgListSize = funcArgList1.size();
							for (int idx = 0; idx < funcArgListSize; idx++) {
								String argStr1 = funcArgList1.get(idx);
								XslTransformData.m_xsl_variable_qname_list.add(new QName(argStr1));
							}
						}

						if (a2 instanceof XPathDynamicFunctionCall) {
							XPathDynamicFunctionCall dfc2 = (XPathDynamicFunctionCall)a2;
							String dfcName2 = dfc2.getFuncRefVarName();
							XslTransformData.m_xsl_variable_qname_list.add(new QName(dfcName2));
							List<String> funcArgList2 = dfc2.getArgList();
							int funcArgListSize = funcArgList2.size();
							for (int idx = 0; idx < funcArgListSize; idx++) {
								String argStr1 = funcArgList2.get(idx);
								XslTransformData.m_xsl_variable_qname_list.add(new QName(argStr1));
							}
						}

						if (a3 instanceof XPathDynamicFunctionCall) {
							XPathDynamicFunctionCall dfc3 = (XPathDynamicFunctionCall)a3;
							String dfcName3 = dfc3.getFuncRefVarName();
							XslTransformData.m_xsl_variable_qname_list.add(new QName(dfcName3));
							List<String> funcArgList3 = dfc3.getArgList();
							int funcArgListSize = funcArgList3.size();
							for (int idx = 0; idx < funcArgListSize; idx++) {
								String argStr1 = funcArgList3.get(idx);
								XslTransformData.m_xsl_variable_qname_list.add(new QName(argStr1));
							}
						}

						List <QName> qNameList2 = new ArrayList<QName>();
						List<QName> list3 = XslTransformData.m_xsl_variable_qname_list;
						int size3 = list3.size();
						for (int idx = 0; idx < size3; idx++) {
							qNameList2.add(list3.get(idx));
						}

						for (int idx = 0; idx < qNameList.size(); idx++) {
							QName qName1 = qNameList.get(idx);
							qNameList2.remove(qName1);
						}

						if (qNameList2.contains(m_qname)) {
							throw new TransformerException("XPST0008 : An XSL stylesheet top level variable '" + m_qname.toString() 
																											   + "' cannot be resolved to a value due "
																											   + "to cyclic variable dependency.", srcLocator);
						}
					}
					else {
						List <QName> qNameList2 = new ArrayList<QName>();
						List<QName> list3 = XslTransformData.m_xsl_variable_qname_list;
						int size3 = list3.size();
						for (int idx = 0; idx < size3; idx++) {
							qNameList2.add(list3.get(idx));
						}

						for (int idx = 0; idx < qNameList.size(); idx++) {
							QName qName1 = qNameList.get(idx);
							qNameList2.remove(qName1);
						}

						if (qNameList2.contains(m_qname)) {
							throw new TransformerException("XPST0008 : An XSL stylesheet top level variable '" + m_qname.toString() 
																											   + "' cannot be resolved to a value due "
																											   + "to cyclic variable dependency.", srcLocator);
						}
					}
				}			
				else if ((expr1 instanceof Function) || (expr1 instanceof UnaryOperation)) {
					// no op
				}
				else if ((expr1 instanceof XString) || (expr1 instanceof XBoolean) || (expr1 instanceof XBooleanStatic) 
						                                                           || (expr1 instanceof XNumber) || (expr1 instanceof XSAnyAtomicType)) {
					// no op
				}
				else if ((expr1 instanceof Variable) || (expr1 instanceof XPathMapConstructor)) {
					// no op
				}
				else if ((expr1 instanceof LocPathIterator) || (expr1 instanceof XPathSequenceConstructor) 
						                                                           || (expr1 instanceof ResultSequence) || (expr1 instanceof Range)) {
					// no op
				}
				else if (expr1 instanceof Operation) {
					// no op
				}
				else if (expr1 instanceof XPathForExpr) {
					// no op
				}
				else if (qNameList.contains(m_qname)) {
					throw new TransformerException("XPST0008 : An XSL stylesheet top level variable '" + m_qname.toString() 
																									   + "' cannot be resolved to a value due "
																									   + "to cyclic variable dependency.", srcLocator);
				}
		    }
			
			Stylesheet stylesheet = transformer.getStylesheet();              
			Vector globalContextItemList = stylesheet.getGlobalContextItemList();
			Expression xpathSelectExpr = m_selectPattern.getExpression();
			if ((globalContextItemList != null) && !isXPathExpressionStatic(xpathSelectExpr)) {												
					ElemGlobalContextItem elemGlobalContextItem = (ElemGlobalContextItem)(globalContextItemList.get(0));
					String xslGlobalContextItemAsValue = elemGlobalContextItem.getAs();
					String xslGlobalContextItemUseValue = elemGlobalContextItem.getUse();
					if (Constants.ATTRVAL_ABSENT.equals(xslGlobalContextItemUseValue)) {
						throw new TransformerException("XPDY0002 : An XSL top level variable has 'select' expression that requires "
																									   + "a context item. But XSL 'global-context-item' "
																									   + "instruction asserts XPath context item to be absent.", srcLocator);
					}

					if (sourceNode == DTM.NULL) {
						throw new TransformerException("XPDY0002 : An XSL top level variable has 'select' expression that requires "
																									   + "a context item but an context item "
																									   + "is absent.", srcLocator);
					}

					if (xslGlobalContextItemAsValue != null) {
						DTM dtm = xctxt.getDTM(sourceNode);
						short nodeType = dtm.getNodeType(sourceNode);
						String nodeTypeStr = xctxt.getNodeTypeStr(nodeType);
						SequenceTypeData seqExpectedTypeData = SequenceTypeSupport.getSequenceTypeDataFromSeqTypeStr(xslGlobalContextItemAsValue, xctxt, srcLocator);
						SequenceTypeKindTest seqTypeKindTest = seqExpectedTypeData.getSequenceTypeKindTest();
						if (seqTypeKindTest != null) {
							if ((seqTypeKindTest.getKindVal() == SequenceTypeSupport.ELEMENT_KIND) || (seqTypeKindTest.getKindVal() == SequenceTypeSupport.ITEM_KIND)) {
								if (nodeType != DTM.ELEMENT_NODE) {
									throw new TransformerException("XPDY0002 : An xdm " + nodeTypeStr + " node doesn't match with XSL "
																									  + "'global-context-item' instruction's "
																									  + "\"as\" attribute value " + xslGlobalContextItemAsValue + ".", srcLocator);	
								}
							}
							else if ((seqTypeKindTest.getKindVal() == SequenceTypeSupport.ATTRIBUTE_KIND) || (seqTypeKindTest.getKindVal() == SequenceTypeSupport.ITEM_KIND)) {
								if (nodeType != DTM.ATTRIBUTE_NODE) {
									throw new TransformerException("XPDY0002 : An xdm " + nodeTypeStr + " node doesn't match with XSL "
																									  + "'global-context-item' instruction's "
																									  + "\"as\" attribute value " + xslGlobalContextItemAsValue + ".", srcLocator);
								}
							}
							else if ((seqTypeKindTest.getKindVal() == SequenceTypeSupport.DOCUMENT_KIND) || (seqTypeKindTest.getKindVal() == SequenceTypeSupport.ITEM_KIND)) {
								if (nodeType != DTM.DOCUMENT_NODE) {
									throw new TransformerException("XPDY0002 : An xdm " + nodeTypeStr + " node doesn't match with XSL "
																									  + "'global-context-item' instruction's "
																									  + "\"as\" attribute value " + xslGlobalContextItemAsValue + ".", srcLocator);
								}
								
								SequenceTypeKindTest seqTypeSubKindTest = seqTypeKindTest.getSeqTypeSubKindTest();
								if (seqTypeSubKindTest != null) {
									if (seqTypeSubKindTest.getKindVal() == SequenceTypeSupport.ELEMENT_KIND) {
										String dataTypeNodeLocalName = seqTypeSubKindTest.getNodeLocalName();
										String dataTypeNodeNsUri = seqTypeSubKindTest.getNodeNsUri();
										int childNode = dtm.getFirstChild(sourceNode);
										short childNodeType = dtm.getNodeType(childNode);
										String nodeName = dtm.getNodeName(childNode);
										String nsUri = dtm.getNamespaceURI(childNode);										
										boolean isNsEqual = false;
										if ((nsUri == null) && (dataTypeNodeNsUri == null)) {
										   isNsEqual = true;
										}
										else if (nsUri != null) {
										   isNsEqual = nsUri.equals(dataTypeNodeNsUri); 
										}
										else if (dataTypeNodeNsUri != null) {
										   isNsEqual = dataTypeNodeNsUri.equals(nsUri);	
										}
										
										if (!((childNodeType == DTM.ELEMENT_NODE) && nodeName.equals(dataTypeNodeLocalName) && isNsEqual)) {
										   throw new TransformerException("XTTE0590 : An XSL global-context-item instruction's required type is not satisfied.", srcLocator);
										}
									}
									else if (seqTypeSubKindTest.getKindVal() == SequenceTypeSupport.SCHEMA_ELEMENT_KIND) {
										// We may try to implement this functionality
									}
								}
							}
						}
					}
			}
		} 
		
		if (m_static && !"private".equals(m_visibility)) {
			throw new TransformerException("XPST0008 : A top-level static variable " + m_qname.toString() + " is not declared as private.", srcLocator);
		}
		
		if (m_static && (getFirstChildElem() != null)) {
			throw new TransformerException("XTSE0010 : A top-level xsl:variable/xsl:param " + m_qname.toString() + " declared as static, "
					                                                                                      + "has non-empty content.", srcLocator);
		}
		
		if (m_static && !(this instanceof ElemParam) && (m_selectPattern == null)) {
			throw new TransformerException("XTSE0010 : A top-level xsl:variable " + m_qname.toString() + " declared as static doesn't "
					                                                                                   + "have an attribute \"select\".", srcLocator);
		}
    }
    else if (m_static) {
    	throw new TransformerException("XPST0008 : An XSL stylesheet's local variables and parameters cannot be "
    			                                                                                       + "declared with attribute \"static\" = yes. "
    			                                                                                       + "An xsl:variable/xsl:param " + m_qname.toString() 
    			                                                                                       + " that is non-local is declared to be static.", srcLocator);
    }
    
    if ((m_selectPattern != null) && (m_xpath_default_namespace != null)) {    		
       m_selectPattern = new XPath(m_selectPattern.getPatternString(), srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
 	}
 
    try {        
      if (m_selectPattern != null) {          
        selectExpression = m_selectPattern.getExpression();
                
        if (selectExpression instanceof XSL3ConstructorOrExtensionFunction) {        	
        	XSL3ConstructorOrExtensionFunction xpathFunc = (XSL3ConstructorOrExtensionFunction)selectExpression;
        	XSL3FunctionService xslFunctionService = xctxt.getXSLFunctionService();
            XObject evalResult = xslFunctionService.callFunction(xpathFunc, transformer, xctxt);
            
            if (evalResult != null) {
            	if ((m_asAttr != null) && ((evalResult instanceof XPathMap) || (evalResult instanceof XPathArray))) {
            		SequenceTypeData seqExpectedTypeData = SequenceTypeSupport.getSequenceTypeDataFromSeqTypeStr(m_asAttr, xctxt, srcLocator);
            		SequenceTypeKindTest seqTypeKindTest = seqExpectedTypeData.getSequenceTypeKindTest();
            		if ((seqTypeKindTest != null) && (seqTypeKindTest.getKindVal() == SequenceTypeSupport.ITEM_KIND)) {
            			return evalResult;
            		}            		            		
            	}
            	
            	if ((m_asAttr != null) && (evalResult instanceof XSUntypedAtomic)) {
            		SequenceTypeData seqExpectedTypeData = SequenceTypeSupport.getSequenceTypeDataFromSeqTypeStr(m_asAttr, xctxt, srcLocator);
            		int xsBuiltInTypeId = seqExpectedTypeData.getBuiltInSequenceType();
            		if ((xsBuiltInTypeId == SequenceTypeSupport.XS_UNTYPED_ATOMIC) || (xsBuiltInTypeId == SequenceTypeSupport.ITEM_KIND)) {
            		   return evalResult;
            		}
            	}
            	
            	String funcName = ((XSL3ConstructorOrExtensionFunction)selectExpression).getFunctionName();
            	String funcNamespace = ((XSL3ConstructorOrExtensionFunction)selectExpression).getNamespace();
            	
            	String evalResultStrValue = XslTransformEvaluationHelper.getStrVal(evalResult);
            	if (m_asAttr != null && !(XSL3FunctionService.XS_VALID_TRUE).equals(evalResultStrValue)) {           	     
                   evalResult = SequenceTypeSupport.castXdmValueToAnotherType(evalResult, m_asAttr, null, xctxt);
                   if (evalResult == null) {                	  
                	  throw new TransformerException("XTTE0570 : An XSL variable " + m_qname.toString() + "'s evaluation result, doesn't "
                	  		                                                                            + "match an expected xdm sequence type " 
                			                                                                            + m_asAttr + ".", srcLocator); 
                   }
                }
            	else if (m_asAttr != null && (XSL3FunctionService.XS_VALID_TRUE).equals(evalResultStrValue)) {
            	   PrefixResolver prefixResolver = xctxt.getNamespaceContext();
            	   QName asAttrQName = new QName(m_asAttr, prefixResolver);
            	   String typeName = asAttrQName.getLocalName();
            	   String typeNamespace = asAttrQName.getNamespace();
            	   if (funcName.equals(typeName) && typeNamespace.equals(funcNamespace)) {            		  
            	      XObject valToBeValidated = (((XSL3ConstructorOrExtensionFunction)selectExpression).getArg(0)).execute(xctxt);
            	      evalResult = valToBeValidated; 
            	   }
            	   else {
            		  throw new TransformerException("XTTE0570 : An XSL variable " + m_qname.toString() + "'s evaluation result, doesn't "
																		                                + "match an expected xdm sequence type " 
																		                                + m_asAttr + ".", srcLocator); 
            	   }
            	}
            	else if (m_asAttr == null && (XSL3FunctionService.XS_VALID_TRUE).equals(evalResultStrValue)) {
            	   XObject valToBeValidated = (((XSL3ConstructorOrExtensionFunction)selectExpression).getArg(0)).execute(xctxt);
          	       evalResult = valToBeValidated; 	
            	}
                
                return evalResult;    
            }
            else {
                var = m_selectPattern.execute(xctxt, sourceNode, this);    
            }            
        }
        else if (selectExpression instanceof Function) {
            XObject evalResult = ((Function)selectExpression).execute(xctxt);            
            if ((evalResult instanceof ResultSequence) || (evalResult instanceof XPathMap) || 
            		                                      (evalResult instanceof XPathArray) || 
                                                          (evalResult instanceof XSAnyType) ||
                                                          (evalResult instanceof XMLNodeCursorImpl)) {
                if (m_asAttr != null) {
                	evalResult = SequenceTypeSupport.castXdmValueToAnotherType(evalResult, m_asAttr, null, xctxt);
                	if (evalResult == null) {
                		throw new TransformerException("XTTE0570 : An XSL variable " + m_qname.toString() + "'s evaluation result, doesn't "
																		                                  + "match an expected xdm sequence type " 
																		                                  + m_asAttr + ".", srcLocator); 
                	}                	                      
                }
                
                return evalResult; 
            }
        }
        else if (selectExpression instanceof SimpleMapOperator) {
            XObject evalResult = selectExpression.execute(xctxt);
            
            if (m_asAttr != null) {
               evalResult = SequenceTypeSupport.castXdmValueToAnotherType(evalResult, m_asAttr, null, xctxt);
               if (evalResult == null) {
            	   throw new javax.xml.transform.TransformerException("XTTE0570 : An XSL variable's value is not of an expected "
            	   		                                                                           + "XPath sequence type " + m_asAttr +".", srcLocator); 
               }
            }
             
            return evalResult;
        }
        else if ((selectExpression instanceof Range) || (selectExpression instanceof ArrowOp)) {
            XObject evalResult = selectExpression.execute(xctxt);
            
            if (m_asAttr != null) {
               evalResult = SequenceTypeSupport.castXdmValueToAnotherType(evalResult, m_asAttr, null, xctxt);
               if (evalResult == null) {
            	   throw new javax.xml.transform.TransformerException("XTTE0570 : An XSL variable's value is not of an expected "
            	   		                                                                           + "XPath sequence type " + m_asAttr +".", srcLocator); 
               }
            }
             
            return evalResult; 
        }
        else if (selectExpression instanceof Operation) {
        	Operation xpathOp = (Operation)selectExpression;            
            XObject leftOperand = (xpathOp.getLeftOperand()).execute(xctxt);
            XObject rightOperand = (xpathOp.getRightOperand()).execute(xctxt);
            
            XObject evalResult = null;
            try {
               evalResult = xpathOp.operate(leftOperand, rightOperand);
               if (evalResult == null) {
            	  evalResult = xpathOp.execute(xctxt);   
               }
            }
            catch (TransformerException ex) {
               throw new TransformerException(ex.getMessage(), srcLocator); 
            }
            
            if (m_asAttr != null) {
               evalResult = SequenceTypeSupport.castXdmValueToAnotherType(evalResult, m_asAttr, null, xctxt);
               if (evalResult == null) {
            	  throw new javax.xml.transform.TransformerException("XTTE0570 : An XSL variable " + m_qname.toString() + "'s evaluation result, "
            	  		                                                                           + "doesn't match the specified xdm sequence type " 
            			                                                                           + m_asAttr +".", srcLocator); 
               }
            }
             
            return evalResult;
        }
        else if (selectExpression instanceof SelfIteratorNoPredicate) {
            XObject xpath3ContextItem = xctxt.getXPath3ContextItem();
            if (xpath3ContextItem != null) {               
              if (m_asAttr != null) {
                 xpath3ContextItem = SequenceTypeSupport.castXdmValueToAnotherType(xpath3ContextItem, m_asAttr, 
                                                                                                               null, xctxt);  
              }
                
              return xpath3ContextItem;
            }
        }
        else if ((selectExpression instanceof LocPathIterator) && !isTopLevelVariableOrParam(selectExpression)) {        	
            int contextNode = xctxt.getContextNode();
            
            LocPathIterator locPathIterator = (LocPathIterator)selectExpression;
            
            Function func = locPathIterator.getFuncExpr();
            XPathDynamicFunctionCall dfc = locPathIterator.getDynamicFuncCallExpr();
            
            DTMCursorIterator dtmIter = null;                     
            try {
                dtmIter = locPathIterator.asIterator(xctxt, contextNode);
            }
            catch (ClassCastException ex) {
                // no op
            }
            
            if (dtmIter != null) {
               ResultSequence resultSeq = null;
               if (func != null) {            	  
            	  resultSeq = new ResultSequence();
            	  int nextNode;
            	  while ((nextNode = dtmIter.nextNode()) != DTM.NULL)
                  {
                      XMLNodeCursorImpl xdmNodeObj = new XMLNodeCursorImpl(nextNode, xctxt);
                      // Evaluate an XPath expression like /a/b/funcCall(..).
  					  // Find one result item for a sequence of items.                      
                      XObject evalResult = evaluateXPathSuffixFunction(xctxt, srcLocator, func, xdmNodeObj);
                      resultSeq.add(evalResult);
                  }
            	  
            	  var = resultSeq; 
               }
               else if (dfc != null) {            	   
            	   resultSeq = new ResultSequence();
            	   int nextNode;
            	   while ((nextNode = dtmIter.nextNode()) != DTM.NULL)
            	   {
            		   XMLNodeCursorImpl xdmNodeObj = new XMLNodeCursorImpl(nextNode, xctxt);
            		   // Evaluate an XPath expression like /a/b/$funcCall(..).
   					   // Find one result item for a sequence of items.            		   
            		   XObject evalResult = evaluateXPathSuffixDfc(xctxt, dfc, xdmNodeObj);
            		   resultSeq.add(evalResult);
            	   }
            	   
            	   var = resultSeq; 
               }
               else {
            	   var = new XMLNodeCursorImpl(dtmIter);
               }
               
               if (m_asAttr != null) {
                  var = SequenceTypeSupport.castXdmValueToAnotherType(var, m_asAttr, null, xctxt);  
               }
                 
               return var; 
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
                               throw new javax.xml.transform.TransformerException("XPTY0004 : An index value used with an xdm "
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
                               throw new javax.xml.transform.TransformerException("XPTY0004 : An index value used with an xdm "
                               		                                                                              + "sequence reference, is not an integer.", srcLocator);  
                           }
                       }
                       else {
                           throw new javax.xml.transform.TransformerException("XPTY0004 : An index value used with an xdm "
                           		                                                                                  + "sequence reference, is not numeric.", srcLocator);   
                       }
                   }
               }
               
               var = resultSeq;
               
               if (m_asAttr != null) {
                  var = SequenceTypeSupport.castXdmValueToAnotherType(var, m_asAttr, null, xctxt);  
               }
                  
               return var;
            }
        }
        else if (selectExpression instanceof NodeTest) {
        	// Checking for the possibility of a top level xsl:variable or xsl:param 
        	// instruction, referring to a named xsl:function reference.        	        	
        	
        	try {            	
    			ExpressionNode expressionNode = selectExpression.getExpressionOwner();
    			ExpressionNode stylesheetRootNode = null;
    			while (expressionNode != null) {
    				stylesheetRootNode = expressionNode;
    				expressionNode = expressionNode.exprGetParent();                     
    			}

    			StylesheetRoot stylesheetRoot = (StylesheetRoot)stylesheetRootNode;
    			TransformerImpl transformerImpl = stylesheetRoot.getTransformerImpl();  			    			

    			NodeTest nodeTest = (NodeTest)selectExpression; 
    			String funcLocalNameRef = nodeTest.getLocalName();
    			String funcNamespace = nodeTest.getNamespace();
    			    			
    			transformerImpl = stylesheetRoot.getTransformerImpl();  
    			TemplateList templateList = stylesheetRoot.getTemplateListComposed();
    			XSL3FunctionService m_xslFunctionService = XSLFunctionBuilder.getXSLFunctionService();
    			
    			if (!"".equals(funcLocalNameRef) && m_xslFunctionService.isFuncArityWellFormed(funcLocalNameRef)) {        	   
    				int hashCharIdx = funcLocalNameRef.indexOf('#');
    				String funcNameRef2 = funcLocalNameRef.substring(0, hashCharIdx);
    				int funcArity = Integer.valueOf(funcLocalNameRef.substring(hashCharIdx + 1));        		   
    				ElemTemplate elemTemplate = templateList.getXslFunction(new QName(funcNamespace, funcNameRef2), funcArity);
    				ElemFunction elemFunction = null;
    				if (elemTemplate != null) {
    					elemFunction = (ElemFunction)elemTemplate;
    					int xslFuncDefnParamCount = elemFunction.getArity();                      
    					String str = funcLocalNameRef.substring(hashCharIdx + 1);
    					int funcRefParamCount = (Integer.valueOf(str)).intValue();
    					if (funcRefParamCount != xslFuncDefnParamCount) {
    						throw new javax.xml.transform.TransformerException("FORG0006 : An XPath named function reference " + funcLocalNameRef + 
																					    								     " cannot resolve to a function "
																					    								     + "definition.", srcLocator); 
    					}
    					
    					if (elemFunction != null) {
    	    				ElemFunctionItem elemFunctionObject = new ElemFunctionItem(elemFunction);

    	    				return elemFunctionObject; 
    	    			}
    				}
    			}
    		}
    		catch (Exception ex) {
    			// no op
    		}
        }
        else if (selectExpression instanceof XPathArrayConstructor) {
        	var = ((XPathArrayConstructor)selectExpression).execute(xctxt);
        }
        else if (selectExpression instanceof XRTreeFragSelectWrapper) {
        	var = m_selectPattern.execute(xctxt, sourceNode, this);
        	if (var instanceof XString) {
        	   ((XString)var).setXrTreeFragSelectWrapperResult(true);
        	}
        }
        else if (selectExpression instanceof XPathTextAndNodeExpr) {
        	var = ((XPathTextAndNodeExpr)selectExpression).execute(xctxt);
        }
  
        if (var == null) {        	        	
           var = m_selectPattern.execute(xctxt, sourceNode, this);
           
           if (this instanceof ElemParam) {
        	   if (var instanceof XSString) {
        		   var = new XSString((((XSString)var).stringValue()).trim());  
        	   }
        	   else if (var instanceof XString) {
        		   var = new XString((((XString)var).str()).trim());  
        	   }        	   
           }
           
           if ((var == null) && (selectExpression instanceof XPathNamedFunctionReference)) {
        	   var = (XPathNamedFunctionReference)selectExpression; 
           }           
        }
        
        if (var != null) {
           var.allowDetachToRelease(false);

           if (transformer.getDebug()) {
               transformer.getTraceManager().emitSelectedEvent(sourceNode, this, "select", 
                                                                        m_selectPattern, var);
           }
        }
      }
      else if (getFirstChildElem() == null) {
          var = XString.EMPTYSTRING;
      }      
      else {
    	  int rootNodeHandleOfRtf = DTM.NULL;
    	  
    	  if (m_parentNode instanceof Stylesheet) {
    		  // Global variable
    		  rootNodeHandleOfRtf = transformer.transformToGlobalRTF(this);

    		  int attrCount = (SerializerUtils.m_xdmAttrList).size();
    		  if (attrCount > 0) {
    			  ResultSequence rSeq = new ResultSequence();
    			  for (int idx = 0; idx < attrCount; idx++) {
    				  rSeq.add((SerializerUtils.m_xdmAttrList).get(idx)); 
    			  }

    			  (SerializerUtils.m_xdmAttrList).clear();

    			  var = rSeq;

    			  if (m_asAttr != null) {
    				  XPath seqTypeXPath = new XPath(m_asAttr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null, true);
    				  
    				  XObject seqTypeExpressionEvalResult = seqTypeXPath.execute(xctxt, xctxt.getContextNode(), xctxt.getNamespaceContext());
    				  
    				  SequenceTypeData seqExpectedTypeData = (SequenceTypeData)seqTypeExpressionEvalResult;
    				  SequenceTypeKindTest seqTypeKindTest = seqExpectedTypeData.getSequenceTypeKindTest();
    				  int seqTypeKindVal = seqTypeKindTest.getKindVal(); 
    				  
    				  if (seqTypeKindVal == SequenceTypeSupport.ATTRIBUTE_KIND) {    		    		    		
    					  if (var instanceof ResultSequence) {
    						  ResultSequence rSeq2 = (ResultSequence)var;
    						  int rSeqLength = rSeq2.size();
    						  int count = 0;
    						  for (int idx = 0; idx < rSeqLength; idx++) {
    							  XObject xObj = rSeq2.item(idx);
    							  if (xObj instanceof XdmAttributeItem) {
    								  count++;
    							  }
    						  }

    						  if ((rSeqLength >= 1) && (count == rSeqLength)) {
    							  boolean isXdmValueConformsType = false;
    							  int seqTypeOccrIndicator = seqExpectedTypeData.getItemTypeOccurrenceIndicator();
    							  if ((seqTypeOccrIndicator == OccurrenceIndicator.ZERO_OR_MANY) 
    									                                                     || (seqTypeOccrIndicator == OccurrenceIndicator.ONE_OR_MANY)) {
    								  isXdmValueConformsType = true;
    							  }
    							  else if ((seqTypeOccrIndicator == OccurrenceIndicator.ZERO_OR_ONE) && (rSeqLength <= 1)) {
    								  isXdmValueConformsType = true;
    							  }
    							  else if ((seqTypeOccrIndicator == OccurrenceIndicator.ABSENT) && (rSeqLength == 1)) {
    								  isXdmValueConformsType = true;
    							  }

    							  if (isXdmValueConformsType) {
    								  return var;
    							  }
    							  else {    								  
    								  throw new TransformerException("XTTE0570 : An XSL variable " + m_qname.toString() + "'s evaluation "
					    								  		                                                        + "result doesn't match the specified "
					    								  		                                                        + "xdm sequence type " + m_asAttr + ".", srcLocator);
    							  }
    						  }    			    			    			
    					  }
    				  }
    			  }
    		  }
    		  
    		  int nsCount = (SerializerUtils.m_xdmNamespaceList).size();
    		  if (nsCount > 0) {
    			  ResultSequence rSeq = new ResultSequence();
    			  for (int idx = 0; idx < nsCount; idx++) {
    				  rSeq.add((SerializerUtils.m_xdmNamespaceList).get(idx)); 
    			  }

    			  (SerializerUtils.m_xdmNamespaceList).clear();

    			  var = rSeq;

    			  if (m_asAttr != null) {
    				  XPath seqTypeXPath = new XPath(m_asAttr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null, true);
    				  
    				  XObject seqTypeExpressionEvalResult = seqTypeXPath.execute(xctxt, xctxt.getContextNode(), xctxt.getNamespaceContext());
    				  
    				  SequenceTypeData seqExpectedTypeData = (SequenceTypeData)seqTypeExpressionEvalResult;
    				  SequenceTypeKindTest seqTypeKindTest = seqExpectedTypeData.getSequenceTypeKindTest();
    				  int seqTypeKindVal = seqTypeKindTest.getKindVal(); 
    				  
    				  if (seqTypeKindVal == SequenceTypeSupport.NAMESPACE_NODE_KIND) {    		    		    		
    					  if (var instanceof ResultSequence) {
    						  ResultSequence rSeq2 = (ResultSequence)var;
    						  int rSeqLength = rSeq2.size();
    						  int count = 0;
    						  for (int idx = 0; idx < rSeqLength; idx++) {
    							  XObject xObj = rSeq2.item(idx);
    							  if (xObj instanceof XdmNamespaceItem) {
    								  count++;
    							  }
    						  }

    						  if ((rSeqLength >= 1) && (count == rSeqLength)) {
    							  boolean isXdmValueConformsType = false;
    							  int seqTypeOccrIndicator = seqExpectedTypeData.getItemTypeOccurrenceIndicator();
    							  if ((seqTypeOccrIndicator == OccurrenceIndicator.ZERO_OR_MANY) 
    									                                                     || (seqTypeOccrIndicator == OccurrenceIndicator.ONE_OR_MANY)) {
    								  isXdmValueConformsType = true;
    							  }
    							  else if ((seqTypeOccrIndicator == OccurrenceIndicator.ZERO_OR_ONE) && (rSeqLength <= 1)) {
    								  isXdmValueConformsType = true;
    							  }
    							  else if ((seqTypeOccrIndicator == OccurrenceIndicator.ABSENT) && (rSeqLength == 1)) {
    								  isXdmValueConformsType = true;
    							  }

    							  if (isXdmValueConformsType) {
    								  return var;
    							  }
    							  else {    								  
    								  throw new TransformerException("XTTE0570 : An XSL variable " + m_qname.toString() + "'s evaluation "
    								  		                                                                            + "result doesn't match the specified "
    								  		                                                                            + "xdm sequence type " + m_asAttr + ".", srcLocator);
    							  }
    						  }    			    			    			
    					  }
    				  }
    			  }
    		  }
    	  }
    	  else {
    		  ElemTemplateElement elemTemplateElement = getFirstChildElem();
    		  ResultSequence rSeq = new ResultSequence();
    		  
    		  boolean isSeqConstructOk = true;
    		  
    		  if (m_asAttr != null) {    			  
    			  while (elemTemplateElement != null) {
    				 if (elemTemplateElement instanceof ElemText) {
    					 ElemTemplateElement elem1 = (ElemTemplateElement)elemTemplateElement; 
    					 while (elem1 instanceof ElemText) {
    						 elem1 = elem1.getFirstChildElem();
    						 if (elem1 instanceof ElemTextLiteral) {
    							 ElemTextLiteral elemTextLiteral = (ElemTextLiteral)elem1;
    							 char[] chrArray = elemTextLiteral.getChars();
    							 String strValue = String.valueOf(chrArray);
    							 rSeq.add(new XSString(strValue));

    							 elem1 = elem1.getNextSiblingElem();
    						 }
    					 }
    					 
    					 if (!((elem1 instanceof ElemText) || (elem1 instanceof ElemTextLiteral))) {
    					    elemTemplateElement = elem1;
    					 }
    				 }    				 
    				 else if (elemTemplateElement instanceof ElemAttribute) {
    					 ElemAttribute elemAttribute = (ElemAttribute)elemTemplateElement;
    					 elemAttribute.setIsSerialize(false);
    					 AVT attrNameAvt = elemAttribute.getName();
    					 PrefixResolver prefixResolver = xctxt.getNamespaceContext();
    					 String nodeName = attrNameAvt.evaluate(xctxt, sourceNode, prefixResolver);
    					 String prefix = QName.getPrefixPart(nodeName);
    					 String localName = QName.getLocalPart(nodeName); 
    					 String namespace = prefixResolver.getNamespaceForPrefix(prefix);
    					 if (namespace == null) {
    						List<XMLNSDecl> prefixTable = getPrefixTable();
    						namespace = XslTransformEvaluationHelper.getNsUriFromPrefix(prefix, prefixTable);
    					 }
    					 
    					 elemAttribute.constructNode(nodeName, prefix, namespace, transformer);
    					 String attrValue = elemAttribute.getAttrVal();

    					 XdmAttributeItem xdmAttributeItem = new XdmAttributeItem(localName, namespace, attrValue);
    					 rSeq.add(xdmAttributeItem);

    					 elemAttribute.setIsSerialize(true);

    					 elemTemplateElement = elemTemplateElement.getNextSiblingElem();
    				 }
    				 else if (elemTemplateElement instanceof ElemComment) {
    					 ElemComment elemComment = (ElemComment)elemTemplateElement;
    					 elemComment.setIsSerialize(false);
    					 elemComment.execute(transformer);
    					 String commentValue = elemComment.getCommentValue();
    					 
    					 XdmCommentItem xdmCommentItem = new XdmCommentItem(commentValue);    					 
    					 rSeq.add(xdmCommentItem);
    					 
    					 elemComment.setIsSerialize(true);
    					 
    					 elemTemplateElement = elemTemplateElement.getNextSiblingElem();
    				 }
                     else if (elemTemplateElement instanceof ElemPI) {
                    	 ElemPI elemPi = (ElemPI)elemTemplateElement;
                    	 elemPi.setIsSerialize(false);
                    	 elemPi.execute(transformer);
    					 String piName = elemPi.getPiName();
    					 String piValue = elemPi.getPiValue();
    					 
    					 XdmProcessingInstructionItem xdmPiItem = new XdmProcessingInstructionItem(piName, piValue);
    					 rSeq.add(xdmPiItem);
    					 
    					 elemPi.setIsSerialize(true);
    					 
    					 elemTemplateElement = elemTemplateElement.getNextSiblingElem();
    				 }
                     else {
                         isSeqConstructOk = false;
                    	 
                    	 break; 
                     }
    			  }    			      			  
    		  }
    		  
    		  if ((rSeq.size() > 0) && isSeqConstructOk) {
    			  XPath seqTypeXPath = new XPath(m_asAttr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null, true);    	    	
    			  XObject seqTypeExpressionEvalResult = seqTypeXPath.execute(xctxt, xctxt.getContextNode(), xctxt.getNamespaceContext());    	
    			  SequenceTypeData seqExpectedTypeData = (SequenceTypeData)seqTypeExpressionEvalResult;
    			  
    			  if (seqExpectedTypeData.getBuiltInSequenceType() != 0) {
    				 int rSeqLength = rSeq.size();
    				 ResultSequence rSeq2 = new ResultSequence(); 
    				 for (int idx = 0; idx < rSeqLength; idx++) {
    					XObject xObj = rSeq.item(idx);
    					if (xObj instanceof XdmAttributeItem) {
    					   XdmAttributeItem xdmAttributeItem = (XdmAttributeItem)xObj;
    					   String attrStrValue = xdmAttributeItem.getAttrStrValue();
    					   rSeq2.add(new XSString(attrStrValue));
    					}
    				 }
    				 
    				 if (rSeq2.size() == rSeqLength) {
    					var = SequenceTypeSupport.castXdmValueToAnotherType(rSeq2, seqExpectedTypeData, false);

    					return var;
    				 }
    			  }
    			  else {
    				 int rSeqSize = rSeq.size();
    				 ResultSequence rSeqCopy = new ResultSequence(); 
 					 for (int idx = 0; idx < rSeqSize; idx++) {
 						 XObject xObj = rSeq.item(idx);
 						 if (xObj instanceof XdmAttributeItem) {
 							 XdmAttributeItem xdmAttributeItem = (XdmAttributeItem)xObj;
 							 String localName = xdmAttributeItem.getAttrLocalName();
 							 String namespace = xdmAttributeItem.getAttrNodeNs();
 							 QName qName = new QName(namespace, localName);
 							 boolean isSeqItemOk = true;
 							 for (int idx2 = idx + 1; idx2 < rSeqSize; idx2++) {
 								 XObject xObj2 = rSeq.item(idx2);
 								 if (xObj2 instanceof XdmAttributeItem) {
 									 XdmAttributeItem xdmAttributeItem2 = (XdmAttributeItem)xObj2;
 									 String localName2 = xdmAttributeItem2.getAttrLocalName();
 									 String namespace2 = xdmAttributeItem2.getAttrNodeNs();
 									 QName qName2 = new QName(namespace2, localName2);
 									 if (qName2.equals(qName)) {
 										isSeqItemOk = false;
 										
 										break;
 									 }
 								 }
 							 }
 							 
 							 if (isSeqItemOk) {
 								rSeqCopy.add(xObj); 
 							 }
 						 }
 						 else {
 						    rSeqCopy.add(xObj);
 						 }
 					 }    				  
    				  
    			     var = SequenceTypeSupport.castXdmValueToAnotherType(rSeqCopy, seqExpectedTypeData, false);
    			     if (var != null) {
    			    	 return var; 
    			     }
    			     else {
    			    	 throw new TransformerException("XTTE0570 : An XSL variable " + m_qname.toString() + "'s evaluation "
																					    			    			 + "result doesn't match the specified "
																					    			    			 + "xdm sequence type " + m_asAttr + ".", srcLocator);
    			     }
    			  }    			      			  
			  }
    		  else {
    		      rootNodeHandleOfRtf = transformer.transformToRTF(this);
    		  }
    	  }    	      	  
    	  
    	  if (XslTransformData.m_xpathInlineFunction != null) {
    		  // This condition is met after method call transformer.transformToGlobalRTF/transformer.transformToRTF 
    		  // previously.
    		  if (m_asAttr == null) {
    			  var = XslTransformData.m_xpathInlineFunction;
    			  XslTransformData.m_xpathInlineFunction = null;
    		  }
    	  }
    	  else if (XslTransformData.m_xpathArray != null) {
    		  // This condition is met after method call transformer.transformToGlobalRTF/transformer.transformToRTF 
    		  // previously.
    		  if (m_asAttr == null) {
    			  var = XslTransformData.m_xpathArray;
    			  XslTransformData.m_xpathArray = null;
    		  }
    	  }
    	  else if (XslTransformData.m_xpathMap != null) {
    		  // This condition is met after method call transformer.transformToGlobalRTF/transformer.transformToRTF 
    		  // previously.
    		  if (m_asAttr == null) {
    			  var = XslTransformData.m_xpathMap;
    			  XslTransformData.m_xpathMap = null;
    		  }
    	  }
    	  else if ((XslTransformData.m_xpathNamedFunctionRefSequence).size() > 0) {
    		  if (m_asAttr == null) {
    			  if ((XslTransformData.m_xpathNamedFunctionRefSequence).size() == 1) {
    				 var = (XslTransformData.m_xpathNamedFunctionRefSequence).item(0);  
    			  }
    			  else {
    			     var = XslTransformData.m_xpathNamedFunctionRefSequence;
    			  }
    		  }
    	  }
    	  else if (XslTransformData.m_xsl_perform_sort_resultSeq != null) {
    		  var = XslTransformData.m_xsl_perform_sort_resultSeq; 
    	  }
    	  else if (var == null) {
    	     NodeList nodeList = (new XRTreeFrag(rootNodeHandleOfRtf, xctxt, this)).convertToNodeset();    	  
    	     var = new XNodeSetForDOM(nodeList, xctxt);    	     
    	  }
      }
    }
    finally {      
       xctxt.popCurrentNode();
    }
    
    if (m_asAttr != null) {    	
    	var = getXslVariableResultWithAsAttribute(var, xctxt, srcLocator, selectExpression, m_asAttr);
    }
        
    return var;
    
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
    if (null == m_selectPattern  
       && sroot.getOptimizer())
    {
      XPath newSelect = rewriteChildToExpression(this);
      if (null != newSelect)
        m_selectPattern = newSelect;
    }
    
    StylesheetRoot.ComposeState cstate = sroot.getComposeState();
    
    // This should be done before addVariableName, so we don't have visibility 
    // to the variable now being defined.
    java.util.Vector vnames = cstate.getVariableNames();
    
    m_vars = (Vector)vnames.clone();
    m_globals_size = cstate.getGlobalsSize();
    
    if (null != m_selectPattern)
      m_selectPattern.fixupVariables(vnames, cstate.getGlobalsSize());
      
    // Only add the variable if this is not a global.  If it is a global, 
    // it was already added by stylesheet root.
    if (!(m_parentNode instanceof Stylesheet) && m_qname != null)
    {
      m_index = cstate.addVariableName(m_qname) - cstate.getGlobalsSize();
    }
    else if (m_parentNode instanceof Stylesheet)
    {
    	// If this is a global, then we need to treat it as if it's a xsl:template, 
    	// and count the number of variables it contains.  So we set the count to 
    	// zero here.
		cstate.resetStackFrameSize();
    }
    
    // This has to be done after the addVariableName, so that the variable 
    // pushed won't be immediately popped again in endCompose.
    super.compose(sroot);
  }
  
  /**
   * This after the template's children have been composed.  We have to get 
   * the count of how many variables have been declared, so we can do a link 
   * and unlink.
   */
  public void endCompose(StylesheetRoot sroot) throws TransformerException
  {
    super.endCompose(sroot);
    if (m_parentNode instanceof Stylesheet)
    {
    	StylesheetRoot.ComposeState cstate = sroot.getComposeState();
    	m_frameSize = cstate.getFrameSize();
    	cstate.resetStackFrameSize();
    }
  }

  /**
   * If the children of a variable is a single xsl:value-of or text literal, 
   * it is cheaper to evaluate this as an expression, so try and adapt the 
   * child an an expression.
   *
   * @param varElem Should be a ElemParam, ElemVariable, or ElemWithParam.
   *
   * @return An XPath if rewrite is possible, else null.
   *
   * @throws TransformerException
   */
  static XPath rewriteChildToExpression(ElemTemplateElement varElem)
          throws TransformerException
  {

    ElemTemplateElement t = varElem.getFirstChildElem();

    // Down the line this can be done with multiple string objects using 
    // the concat function.
    if (null != t && null == t.getNextSiblingElem())
    {
      int etype = t.getXSLToken();

      if (Constants.ELEMNAME_VALUEOF == etype)
      {
        ElemValueOf valueof = (ElemValueOf) t;

        // %TBD% I'm worried about extended attributes here.
        if (valueof.getDisableOutputEscaping() == false
                && valueof.getDOMBackPointer() == null)
        {
          varElem.m_firstChild = null;

          return new XPath(new XRTreeFragSelectWrapper(valueof.getSelect().getExpression()));
        }
      }
      else if (Constants.ELEMNAME_TEXTLITERALRESULT == etype)
      {
        ElemTextLiteral lit = (ElemTextLiteral) t;

        if (lit.getDisableOutputEscaping() == false
                && lit.getDOMBackPointer() == null)
        {
          String str = lit.getNodeValue();
          XString xstr = new XString(str);

          varElem.m_firstChild = null;

          return new XPath(new XRTreeFragSelectWrapper(xstr));
        }
      }
    }

    return null;
  }

  /**
   * This function is called during recomposition to
   * control how this element is composed.
   * @param root The root stylesheet for this transformation.
   */
  public void recompose(StylesheetRoot root)
  {
    root.recomposeVariables(this);
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
   * Accept a visitor and call the appropriate method 
   * for this class.
   * 
   * @param visitor The visitor whose appropriate method will be called.
   * @return true if the children of the object should be visited.
   */
  protected boolean accept(XSLTVisitor visitor)
  {
  	return visitor.visitVariableOrParamDecl(this);
  }

  
  /**
   * Call the children visitors.
   * @param visitor The visitor whose appropriate method will be called.
   */
  protected void callChildVisitors(XSLTVisitor visitor, boolean callAttrs)
  {
  	if (null != m_selectPattern)
  		m_selectPattern.getExpression().callVisitors(m_selectPattern, visitor);
    super.callChildVisitors(visitor, callAttrs);
  }
  
  /**
   * Tell if this is a psuedo variable reference, declared by Xalan instead 
   * of by the user.
   */
  public boolean isPsuedoVar()
  {
  	java.lang.String ns = m_qname.getNamespaceURI();
  	if ((null != ns) && ns.equals(RedundentExprEliminator.PSUEDOVARNAMESPACE))
  	{
  		if (m_qname.getLocalName().startsWith("#"))
  			return true;
  	}
  	return false;
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
	  // Cannot have content and select both
	  if (m_selectPattern != null)
	  {
		  SourceLocator srcLocator = m_selectPattern.getLocator();	
		  Integer lineNo = srcLocator.getLineNumber();
		  Integer colNo = srcLocator.getColumnNumber();

		  error(XSLTErrorResources.ER_CANT_HAVE_CONTENT_AND_SELECT_LOC_AWARE, 
				                                           new Object[]{lineNo, colNo, "xsl:" + this.getNodeName()});
		  return null;
	  }
	  
	  return super.appendChild(elem);
  }
  
  /**
   * Method definition to check whether, an attribute "select"'s xsl:variable or 
   * xsl:param parent is an XSL stylesheet top level xsl:variable or xsl:param.  
   * 
   * @param selectExpression		 An xsl:variable's or xsl:param's compiled 
   *                                 'select' expression.
   * @return						 Boolean value true or false
   */
  private boolean isTopLevelVariableOrParam(Expression selectExpression) {
	  boolean result = false;

	  ExpressionNode exprNode = selectExpression.getExpressionOwner();
	  if (exprNode != null) {
		  exprNode = exprNode.exprGetParent();
		  if (exprNode instanceof StylesheetRoot) {
			  result = true; 
		  }
	  }

	  return result;
  }
  
  /**
   * Method definition, to convert an xdm sequence with all node 
   * items within it, to a node set object.
   * 
   * @param xctxt						   An XPath context object
   * @param rSeq                           The supplied xdm sequence
   * @return                               Node set object    
   */
  private XObject getNodeSetFromSequence(XPathContext xctxt, ResultSequence rSeq) {
	  
	  XObject result = null;
	  
	  List<Integer> nodeHandleList = new ArrayList<Integer>();
	  int rSeqLength = rSeq.size(); 
	  for (int idx = 0; idx < rSeqLength; idx++) {
		  XObject xObj = rSeq.item(idx);
		  XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)xObj;    			
		  int nextNode = xmlNodeCursorImpl.nextNode();
		  nodeHandleList.add(Integer.valueOf(nextNode));
	  }

	  result = new XMLNodeCursorImpl(nodeHandleList, xctxt);
	  
	  return result;
  }
  
  /**
   * Method definition, to apply xsl:variable instruction's "as" attribute value,
   * to xsl:variable's initially computed result.
   * 
   * @param var										xsl:variable's initially computed result
   * @param xctxt                                   XPathContext object
   * @param srcLocator                              XSL transformation SourceLocator object
   * @param selectExpression                        xsl:variable "select" attribute's compiled
   *                                                XPath expression. This value may be null.
   * @param asAttrString                            xsl:variable "as" attribute's non-null string
   *                                                value.                                                 
   * @return
   * @throws TransformerException
   */
  private XObject getXslVariableResultWithAsAttribute(XObject var, XPathContext xctxt, SourceLocator srcLocator,
			                                                                                     Expression selectExpression,
			                                                                                     String asAttrString) throws TransformerException {
		
		XObject result = null;
		
		XPath seqTypeXPath = new XPath(asAttrString, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null, true);    	    	
		
		XObject seqTypeExpressionEvalResult = seqTypeXPath.execute(xctxt, xctxt.getContextNode(), xctxt.getNamespaceContext());    	
		
		SequenceTypeData seqExpectedTypeData = (SequenceTypeData)seqTypeExpressionEvalResult;
		SequenceTypeKindTest seqTypeKindTest = seqExpectedTypeData.getSequenceTypeKindTest();
		
		int seqTypeOccrIndicator = seqExpectedTypeData.getItemTypeOccurrenceIndicator();

		int seqTypeKindVal = 0;    	
		if (seqTypeKindTest != null) {
			seqTypeKindVal = seqTypeKindTest.getKindVal();    		
			if ((seqTypeKindVal == SequenceTypeSupport.DOCUMENT_KIND) && ((seqTypeOccrIndicator == SequenceTypeSupport.OccurrenceIndicator.ZERO_OR_ONE) || 
				                                                          (seqTypeOccrIndicator == SequenceTypeSupport.OccurrenceIndicator.ZERO_OR_MANY))) {
				if (var.equals(XString.EMPTYSTRING)) {
				   result = var;
					
		           return result;
				}    			
			}    		    		
		}

		if (XslTransformData.m_xpathInlineFunction != null) {
			if ((seqExpectedTypeData.getSequenceTypeFunctionTest() != null) || (seqTypeKindVal == SequenceTypeSupport.ITEM_KIND)) {              	   
				result = XslTransformData.m_xpathInlineFunction;
				XslTransformData.m_xpathInlineFunction = null;
			}
			else {
				throw new TransformerException("XTTE0570 : An XSL variable " + m_qname.toString() + "'s evaluation "
																                                  + "result doesn't match the specified "
																                                  + "xdm sequence type " + asAttrString + ".", srcLocator); 
			}
		}
		else if (XslTransformData.m_xpathArray != null) {
			if ((seqExpectedTypeData.getSequenceTypeArrayTest() != null) || (seqTypeKindVal == SequenceTypeSupport.ITEM_KIND)) {              	   
				result = XslTransformData.m_xpathArray;
				XslTransformData.m_xpathArray = null;
			}
			else {
				throw new TransformerException("XTTE0570 : An XSL variable " + m_qname.toString() + "'s evaluation "
																	                              + "result doesn't match the specified "
																	                              + "xdm sequence type " + asAttrString + ".", srcLocator); 
			}
		}
		else if (XslTransformData.m_xpathMap != null) {
			if ((seqExpectedTypeData.getSequenceTypeMapTest() != null) || (seqTypeKindVal == SequenceTypeSupport.ITEM_KIND)) {              	   
				result = XslTransformData.m_xpathMap;
				XslTransformData.m_xpathMap = null;
			}
			else {
				throw new TransformerException("XTTE0570 : An XSL variable " + m_qname.toString() + "'s evaluation "
																	                              + "result doesn't match the specified "
																	                              + "xdm sequence type " + asAttrString + ".", srcLocator); 
			}
		}
		else if ((XslTransformData.m_xpathNamedFunctionRefSequence).size() > 0) {
			int funcItemSeqSize = (XslTransformData.m_xpathNamedFunctionRefSequence).size();
			
			SequenceTypeFunctionTest sequenceTypeFunctionTest = seqExpectedTypeData.getSequenceTypeFunctionTest();
			boolean isSeqCardinalityOk = false;
			if ((funcItemSeqSize == 0) && ((seqTypeOccrIndicator == SequenceTypeSupport.OccurrenceIndicator.ZERO_OR_MANY) || 
					                       (seqTypeOccrIndicator == SequenceTypeSupport.OccurrenceIndicator.ZERO_OR_ONE))) {
			   isSeqCardinalityOk = true;
			}
			else if ((funcItemSeqSize == 1) && ((seqTypeOccrIndicator == SequenceTypeSupport.OccurrenceIndicator.ABSENT) || 
					                            (seqTypeOccrIndicator == SequenceTypeSupport.OccurrenceIndicator.ZERO_OR_MANY) || 
					                            (seqTypeOccrIndicator == SequenceTypeSupport.OccurrenceIndicator.ONE_OR_MANY) ||
					                            (seqTypeOccrIndicator == SequenceTypeSupport.OccurrenceIndicator.ZERO_OR_ONE))) {
			   // An XPath sequence type occurrence indicator with value absent, or any 
			   // other occurrence indicator is ok for this case.
			   isSeqCardinalityOk = true;
			}
			else if ((seqTypeOccrIndicator == SequenceTypeSupport.OccurrenceIndicator.ZERO_OR_MANY) ||
					 (seqTypeOccrIndicator == SequenceTypeSupport.OccurrenceIndicator.ONE_OR_MANY)) {
			   // funcItemSeqSize > 1
			   isSeqCardinalityOk = true;
			}
			
			if (!isSeqCardinalityOk) {
				throw new TransformerException("XTTE0570 : An XSL variable " + m_qname.toString() + "'s evaluation "
																		                          + "result doesn't match the specified "
																		                          + "xdm sequence type " + asAttrString + ".", srcLocator);
			}
			
			if (sequenceTypeFunctionTest != null) {
			   int funcRefCount = (XslTransformData.m_xpathNamedFunctionRefSequence).size();
			   if (sequenceTypeFunctionTest.isAnyFunctionTest()) {	
				   if (funcRefCount == 1) {
					   result = (XslTransformData.m_xpathNamedFunctionRefSequence).item(0);
				   }
				   else {
					   result = XslTransformData.m_xpathNamedFunctionRefSequence;
				   }
			   }
			   else {
				   List<String> funcParamSpecList = sequenceTypeFunctionTest.getTypedFunctionTestParamSpecList();
				   String funcReturnTypeSpec = sequenceTypeFunctionTest.getTypedFunctionTestReturnType();
				   
				   List<XMLNSDecl> prefixTable = (List<XMLNSDecl>)this.getPrefixTable();
				   
				   for (int idx = 0; idx < funcRefCount; idx++) {
					  XPathNamedFunctionReference funcRef1 = (XPathNamedFunctionReference)((XslTransformData.m_xpathNamedFunctionRefSequence).item(idx));
					  ElemFunction elemFunction = funcRef1.getXslStylesheetFunction();
					  if (elemFunction != null) {    					  
						  String funcLocalName = funcRef1.getFuncName(); 
						  String funcNamespace = funcRef1.getFuncNamespace();    					  
						  int xpathNamedFuncRefArity = 0;           
		            	  if ((XPathStaticContext.XPATH_BUILT_IN_FUNCS_NS_URI).equals(funcNamespace) && 
		            			  															        (Keywords.FUNC_CONCAT_STRING).equals(funcLocalName)) {
		            		  xpathNamedFuncRefArity = funcRef1.getConcatArity();
		            	  }
		            	  else {
		            		  xpathNamedFuncRefArity = funcRef1.getArity(); 
		            	  }
						  
						  int funcTypeSpecArity = funcParamSpecList.size();
						  if (xpathNamedFuncRefArity == funcTypeSpecArity) {
							 List<ElemParam> elemFuncParamList = elemFunction.getFuncParamList();
							 for (int i = 0; i < elemFuncParamList.size(); i++) {
								 boolean ok1 = false;
								 for (int j = 0; j < funcParamSpecList.size(); j++) {
		                            if (i == j) {
		                               ElemParam elemParam1 = elemFuncParamList.get(i);
		                               String paramSpec1 = funcParamSpecList.get(j);
		                               String elemParamAs = elemParam1.getAs();
		                               if (elemParamAs == null) {                                    	  
		                            	  ok1 = true;
		                            	  break;  
		                               }
		                               else {
		                            	  // Check equality of data type objects, corresponding to 
		                            	  // paramSpec1 and elemParamAs at the same positional index.
		                            	   
		                            	  elemParamAs = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(elemParamAs, prefixTable);                                    	   
		                            	  XPath seqTypeXPath1 = new XPath(elemParamAs, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null, true);    	    	                                       	
		                               	  XObject seqTypeExpressionEvalResult1 = seqTypeXPath1.execute(xctxt, xctxt.getContextNode(), xctxt.getNamespaceContext());                                       	
		                               	  SequenceTypeData seqExpectedTypeData1 = (SequenceTypeData)seqTypeExpressionEvalResult1;
		                               	  
		                               	  paramSpec1 = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(paramSpec1, prefixTable);
		                               	  XPath seqTypeXPath2 = new XPath(paramSpec1, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null, true);    	    	                                       	
		                             	  XObject seqTypeExpressionEvalResult2 = seqTypeXPath2.execute(xctxt, xctxt.getContextNode(), xctxt.getNamespaceContext());                                       	
		                             	  SequenceTypeData seqExpectedTypeData2 = (SequenceTypeData)seqTypeExpressionEvalResult2;
		                             	  
		                             	  if (!seqExpectedTypeData1.equal(seqExpectedTypeData2)) {
		                             		 throw new TransformerException("XTTE0570 : An XSL variable " + m_qname.toString() + "'s evaluation "
																								                               + "result doesn't match the specified "
																								                               + "xdm sequence type " + asAttrString + ".", srcLocator);
		                             	  }
		                               }
		                            }
								 }
								 
								 if (ok1) {
									continue; 
								 }
							 }
						  }
						  else {
							  throw new TransformerException("XTTE0570 : An XSL variable " + m_qname.toString() + "'s evaluation "
																				                                + "result doesn't match the specified "
																				                                + "xdm sequence type " + asAttrString + ".", srcLocator);
						  }
						  
						  String elemFuncAs = elemFunction.getAs();
						  if ((elemFuncAs != null) && (funcReturnTypeSpec != null)) {
							  elemFuncAs = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(elemFuncAs, prefixTable);
							  XPath seqTypeXPath1 = new XPath(elemFuncAs, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null, true);    	    	                                       	
							  XObject seqTypeExpressionEvalResult1 = seqTypeXPath1.execute(xctxt, xctxt.getContextNode(), xctxt.getNamespaceContext());                                       	
							  SequenceTypeData seqExpectedTypeData1 = (SequenceTypeData)seqTypeExpressionEvalResult1;

							  funcReturnTypeSpec = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(funcReturnTypeSpec, prefixTable);
							  XPath seqTypeXPath2 = new XPath(funcReturnTypeSpec, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null, true);    	    	                                       	
							  XObject seqTypeExpressionEvalResult2 = seqTypeXPath2.execute(xctxt, xctxt.getContextNode(), xctxt.getNamespaceContext());                                       	
							  SequenceTypeData seqExpectedTypeData2 = (SequenceTypeData)seqTypeExpressionEvalResult2;

							  if (!seqExpectedTypeData1.equal(seqExpectedTypeData2)) {    							      							  
								  throw new TransformerException("XTTE0570 : An XSL variable " + m_qname.toString() + "'s evaluation "
																						                            + "result doesn't match the specified "
																						                            + "xdm sequence type " + asAttrString + ".", srcLocator);
							  }
						  }
					  }
				   }
				   
				   if (funcRefCount == 1) {
					   result = (XslTransformData.m_xpathNamedFunctionRefSequence).item(0);
				   }
				   else {
					   result = XslTransformData.m_xpathNamedFunctionRefSequence;
				   }
			   }
			   
			   m_isXPathNamedFunctionRefSequenceVar = true;
			}
			else {
			   throw new TransformerException("XTTE0570 : An XSL variable " + m_qname.toString() + "'s evaluation "
																	                             + "result doesn't match the specified "
																	                             + "xdm sequence type " + asAttrString + ".", srcLocator); 
			}
		}
		else if (var instanceof XNodeSetForDOM) {
			XObject variableConvertedVal = null;

			if (seqExpectedTypeData.getBuiltInSequenceType() == SequenceTypeSupport.XS_QNAME) {
				String strValue = var.str();
				if (strValue.contains(ElemSequence.STRING_VAL_SER_SUFFIX)) {
					strValue = (strValue.replace(ElemSequence.STRING_VAL_SER_SUFFIX, " ")).trim();    			   
					String regexStr = "\\{.*\\}.*";       // e.g, string value is  {uri}localName 
					java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regexStr);
					java.util.regex.Matcher matcher = pattern.matcher(strValue);
					if (matcher.matches()) {
						int i = strValue.indexOf('}');
						String localName = strValue.substring(i + 1);
						String namespaceUri = strValue.substring(1, i); 
						result = new XSQName(null, localName, namespaceUri);

						return result;
					}
				}
			}
			
			if (seqExpectedTypeData.getBuiltInSequenceType() == SequenceTypeSupport.STRING) {
				String strValue = var.str();
				if (strValue.contains(ElemSequence.STRING_VAL_SER_DOUBLE_SUFFIX) || 
						                                                   strValue.contains(ElemSequence.STRING_VAL_SER_INTEGER_SUFFIX) || 
						                                                   strValue.contains(ElemSequence.STRING_VAL_SER_DECIMAL_SUFFIX) || 
						                                                   strValue.contains(ElemSequence.STRING_VAL_SER_FLOAT_SUFFIX)) {
					throw new TransformerException("XTTE0570 : An XSL variable " + m_qname.toString() + "'s evaluation "
																		                              + "result doesn't match the specified "
																		                              + "xdm sequence type " + asAttrString + ".", srcLocator);	
				}
			}
			
			if (seqExpectedTypeData.getBuiltInSequenceType() == SequenceTypeSupport.XS_INTEGER) {
				String strValue = var.str();
				boolean isResultStaticTypeOk = false;
				if (strValue.contains(ElemSequence.STRING_VAL_SER_DOUBLE_SUFFIX)) {
					strValue = (strValue.replace(ElemSequence.STRING_VAL_SER_DOUBLE_SUFFIX, " ")).trim();
					isResultStaticTypeOk = true;
				}
				else if (strValue.contains(ElemSequence.STRING_VAL_SER_INTEGER_SUFFIX)) {
					strValue = (strValue.replace(ElemSequence.STRING_VAL_SER_INTEGER_SUFFIX, " ")).trim();
					isResultStaticTypeOk = true;
				}
				
				if (isResultStaticTypeOk) {
				   String[] strArray = strValue.split(" ");
				   int arrLength1 = strArray.length;
				   ResultSequence rSeq = new ResultSequence();
				   for (int idx = 0; idx < arrLength1; idx++) {
					  String str1 = strArray[idx];
					  try {
					     Integer intValue = Integer.valueOf(str1);
					     rSeq.add(new XSInteger(str1));
					  }
					  catch (NumberFormatException ex) {
						  throw new javax.xml.transform.TransformerException("XPTY0004 : XSL variable " + m_qname.toString() + "'s value "
																				                                  + "doesn't conform to variable's type "
																				                                  + "declaration " + asAttrString + ".", srcLocator); 
					  }
				   }
				   
				   int rSeqLength = rSeq.size(); 
				   
				   boolean isSeqTypeOccrIndicatorOk = false;
				   if (seqTypeOccrIndicator == OccurrenceIndicator.ZERO_OR_MANY) {
					   isSeqTypeOccrIndicatorOk = true;
				   }
				   else if ((seqTypeOccrIndicator == OccurrenceIndicator.ONE_OR_MANY) && (rSeqLength > 0)) {
					   isSeqTypeOccrIndicatorOk = true;
				   }
				   else if ((seqTypeOccrIndicator == OccurrenceIndicator.ZERO_OR_ONE) && (rSeqLength <= 1)) {
					   isSeqTypeOccrIndicatorOk = true;
				   }
				   else if ((seqTypeOccrIndicator == OccurrenceIndicator.ABSENT) && (rSeqLength == 1)) {
					   isSeqTypeOccrIndicatorOk = true;
				   }

				   if (isSeqTypeOccrIndicatorOk) {   			
					   result = rSeq;

					   return result;
				   }
				   else {
					   var = var.getFresh();

					   throw new TransformerException("XTTE0570 : An XSL variable " + m_qname.toString() + "'s evaluation "
																	    						         + "result doesn't match the specified "
																	    						         + "xdm sequence type " + asAttrString + ".", srcLocator); 
				   }
				}
				else {
					throw new javax.xml.transform.TransformerException("XTTE0570 : XSL variable " + m_qname.toString() + "'s value "
							                                                                                           + "doesn't conform to variable's type "
							                                                                                           + "declaration " + asAttrString + ".", srcLocator);
				}
			}
			
			if ((seqTypeKindTest != null) && (seqTypeKindTest.getKindVal() == SequenceTypeSupport.TEXT_KIND)) {    			
				XNodeSetForDOM xNodeSetForDom = (XNodeSetForDOM)var;
				int nodeHandle = xNodeSetForDom.asNode(xctxt);
				DTM dtm = xctxt.getDTM(nodeHandle);
				int childNode = dtm.getFirstChild(nodeHandle);
				if (childNode == DTM.NULL) {
				   return new ResultSequence();
				}
				else {
				   short nodeType = dtm.getNodeType(childNode);
				   if ((nodeType == DTM.TEXT_NODE) && (dtm.getNextSibling(childNode) == DTM.NULL)) {
					  result = var;
					  
					  return result; 
				   }
				   else {
					  var = var.getFresh(); 
				   }
				}
			}
			
			if ((seqTypeKindTest != null) && ((seqTypeKindTest.getKindVal() == SequenceTypeSupport.NODE_KIND) || 
					                          (seqTypeKindTest.getKindVal() == SequenceTypeSupport.ITEM_KIND))) {    			
				XNodeSetForDOM xNodeSetForDom = (XNodeSetForDOM)var;
				int nodeHandle = xNodeSetForDom.asNode(xctxt);
				DTM dtm = xctxt.getDTM(nodeHandle);
				int childNode = dtm.getFirstChild(nodeHandle);
				ResultSequence rSeq = new ResultSequence();
				while (childNode != DTM.NULL) {    				
					XMLNodeCursorImpl xmlNodeCursorImpl = new XMLNodeCursorImpl(childNode, xctxt);    				
					rSeq.add(xmlNodeCursorImpl);
					childNode = dtm.getNextSibling(childNode); 
				}

				rSeq.setXdmParentlessSiblingNodes(true); 

				int rSeqLength = rSeq.size();    			    			
				if (rSeqLength == 1) {
				   XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)(rSeq.item(0));
				   String nodeStrValue = xmlNodeCursorImpl.str();
				   if (nodeStrValue.contains(ElemSequence.STRING_VAL_SER_SUFFIX)) {
					  String[] strArray = nodeStrValue.split(ElemSequence.STRING_VAL_SER_SUFFIX);
					  int rSeqLengthTmp1 = strArray.length;
					  rSeq.clear();
					  for (int idx = 0; idx < rSeqLengthTmp1; idx++) {
						  String str1 = strArray[idx];
						  if (str1.contains(ElemSequence.STRING_VAL_SER_INTEGER_SUFFIX)) {
							  String[] strArray1 = str1.split(ElemSequence.STRING_VAL_SER_INTEGER_SUFFIX);
							  int arrLength1 = strArray1.length;
							  for (int idx1 = 0; idx1 < arrLength1; idx1++) {
								  String str2 = strArray1[idx1];
								  rSeq.add(new XSInteger(str2));  
							  }
						  }
						  else if (str1.contains(ElemSequence.STRING_VAL_SER_DECIMAL_SUFFIX)) {
							  String[] strArray1 = str1.split(ElemSequence.STRING_VAL_SER_DECIMAL_SUFFIX);
							  int arrLength1 = strArray1.length;
							  for (int idx1 = 0; idx1 < arrLength1; idx1++) {
								  String str2 = strArray1[idx1];
								  rSeq.add(new XSDecimal(str2));  
							  }
						  }
						  else if (str1.contains(ElemSequence.STRING_VAL_SER_DOUBLE_SUFFIX)) {
							  String[] strArray1 = str1.split(ElemSequence.STRING_VAL_SER_DOUBLE_SUFFIX);
							  int arrLength1 = strArray1.length;
							  for (int idx1 = 0; idx1 < arrLength1; idx1++) {
								  String str2 = strArray1[idx1];
								  rSeq.add(new XSDouble(str2));  
							  }
						  }
						  else if (str1.contains(ElemSequence.STRING_VAL_SER_FLOAT_SUFFIX)) {
							  String[] strArray1 = str1.split(ElemSequence.STRING_VAL_SER_FLOAT_SUFFIX);
							  int arrLength1 = strArray1.length;
							  for (int idx1 = 0; idx1 < arrLength1; idx1++) {
								  String str2 = strArray1[idx1];
								  rSeq.add(new XSFloat(str2));  
							  }
						  }
						  else {
							  rSeq.add(new XSString(str1));
						  }
					  }
					  
					  rSeqLength = rSeq.size();
				   }
				   else if (nodeStrValue.contains(ElemSequence.STRING_VAL_SER_INTEGER_SUFFIX)) {    				  
					   String[] strArray = nodeStrValue.split(ElemSequence.STRING_VAL_SER_INTEGER_SUFFIX);
					   int rSeqEffectiveLength = strArray.length;
					   rSeq.clear();
					   for (int idx = 0; idx < rSeqEffectiveLength; idx++) {
						   String str1 = strArray[idx];
						   rSeq.add(new XSInteger(str1));  
					   }

					   rSeqLength = rSeqEffectiveLength; 
				   }
				   else if (nodeStrValue.contains(ElemSequence.STRING_VAL_SER_DECIMAL_SUFFIX)) {    				  
					   String[] strArray = nodeStrValue.split(ElemSequence.STRING_VAL_SER_DECIMAL_SUFFIX);
					   int rSeqEffectiveLength = strArray.length;
					   rSeq.clear();
					   for (int idx = 0; idx < rSeqEffectiveLength; idx++) {
						   String str1 = strArray[idx];
						   rSeq.add(new XSDecimal(str1));  
					   }

					   rSeqLength = rSeqEffectiveLength; 
				   }
				   else if (nodeStrValue.contains(ElemSequence.STRING_VAL_SER_DOUBLE_SUFFIX)) {    				  
					   String[] strArray = nodeStrValue.split(ElemSequence.STRING_VAL_SER_DOUBLE_SUFFIX);
					   int rSeqEffectiveLength = strArray.length;
					   rSeq.clear();
					   for (int idx = 0; idx < rSeqEffectiveLength; idx++) {
						   String str1 = strArray[idx];
						   rSeq.add(new XSDouble(str1));  
					   }

					   rSeqLength = rSeqEffectiveLength; 
				   }
				   else if (nodeStrValue.contains(ElemSequence.STRING_VAL_SER_FLOAT_SUFFIX)) {    				  
					   String[] strArray = nodeStrValue.split(ElemSequence.STRING_VAL_SER_FLOAT_SUFFIX);
					   int rSeqEffectiveLength = strArray.length;
					   rSeq.clear();
					   for (int idx = 0; idx < rSeqEffectiveLength; idx++) {
						   String str1 = strArray[idx];
						   rSeq.add(new XSFloat(str1));  
					   }

					   rSeqLength = rSeqEffectiveLength; 
				   }
				}
				else if ((rSeqLength > 1) && (seqTypeKindTest.getKindVal() == SequenceTypeSupport.ITEM_KIND)) {
				   ResultSequence rSeq2 = new ResultSequence(); 
				   for (int idx = 0; idx < rSeqLength; idx++) {
					   XObject xObj1 = rSeq.item(idx);
					   XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)xObj1;
					   int nodeHandle2 = xmlNodeCursorImpl.asNode(xctxt);
					   DTM dtm2 = xctxt.getDTM(nodeHandle2);
					   short nodeType = dtm2.getNodeType(nodeHandle2);
					   if (nodeType == DTM.TEXT_NODE) {
						  String strValue1 = xmlNodeCursorImpl.str();
						  if (strValue1.contains(ElemSequence.STRING_VAL_SER_SUFFIX)) {
							 String str1 = strValue1.replace(ElemSequence.STRING_VAL_SER_SUFFIX, "");
							 java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\{.{1,}\\}.{1,}");
							 java.util.regex.Matcher matcher = pattern.matcher(str1);
							 if (matcher.matches()) {
								 int idx1 = str1.indexOf('{');
								 int idx2 = str1.indexOf('}');
								 String nsUri = str1.substring(idx1 + 1, idx2);
								 String localName = str1.substring(idx2 + 1);
								 XSQName xsQName = new XSQName(null, localName, nsUri);
								 rSeq2.add(xsQName);
							 }
							 else {
								 rSeq2.add(new XSString(str1));
							 }
						  }
						  else {
							 rSeq2.add(new XSString(strValue1)); 
						  }
					   }
					   else if (nodeType == DTM.ELEMENT_NODE) {   					       					       					   
						   XMLString xmlStr1 = dtm2.getStringValue(nodeHandle2);
						   String strValue1 = xmlStr1.toString();
						   if (strValue1.contains(ElemSequence.STRING_VAL_SER_SUFFIX)) {
							   String str1 = strValue1.replace(ElemSequence.STRING_VAL_SER_SUFFIX, "");
							   java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\{.{1,}\\}.{1,}");
							   java.util.regex.Matcher matcher = pattern.matcher(str1);
							   if (matcher.matches()) {
								   int idx1 = str1.indexOf('{');
								   int idx2 = str1.indexOf('}');
								   String nsUri = str1.substring(idx1 + 1, idx2);
								   String localName = str1.substring(idx2 + 1);
								   XSQName xsQName = new XSQName(null, localName, nsUri);
								   rSeq2.add(xsQName);
							   }
							   else {
								   rSeq2.add(new XSString(str1));
							   }
						   }
						   else if ("".equals(strValue1)) {
							   // This is written, to solve W3C XSLT 3.0 test case sequence-2403b.
							   // This code is not very much correct, and needs to be fixed further.
							   rSeq2.add(xmlNodeCursorImpl);    						   
						   }
						   else {
							   rSeq2.add(new XSString(strValue1));
						   }
					   }
				   }
				   
				   rSeq.clear();    			   
				   rSeq = rSeq2;
				   rSeqLength = rSeq.size(); 
				}

				boolean isSeqTypeOccrIndicatorOk = false;
				if (seqTypeOccrIndicator == OccurrenceIndicator.ZERO_OR_MANY) {
					isSeqTypeOccrIndicatorOk = true;
				}
				else if ((seqTypeOccrIndicator == OccurrenceIndicator.ONE_OR_MANY) && (rSeqLength > 0)) {
					isSeqTypeOccrIndicatorOk = true;
				}
				else if ((seqTypeOccrIndicator == OccurrenceIndicator.ZERO_OR_ONE) && (rSeqLength <= 1)) {
					isSeqTypeOccrIndicatorOk = true;
				}
				else if ((seqTypeOccrIndicator == OccurrenceIndicator.ABSENT) && (rSeqLength == 1)) {
					isSeqTypeOccrIndicatorOk = true;
				}

				if (isSeqTypeOccrIndicatorOk) {   			
					result = rSeq;
					    				
					return result;
				}
				else {
					var = var.getFresh();
					
					throw new TransformerException("XTTE0570 : An XSL variable " + m_qname.toString() + "'s evaluation "
																			                          + "result doesn't match the specified "
																			                          + "xdm sequence type " + asAttrString + ".", srcLocator); 
				}
			}

			try {
				ElemFunction elemFunction = ElemFunction.getXSLFunctionService();
				variableConvertedVal = elemFunction.preprocessXslFunctionOrAVariableResult(var, asAttrString, xctxt, m_qname);
			}
			catch (TransformerException ex) {
				throw new TransformerException(ex.getMessage(), srcLocator); 
			}

			if (variableConvertedVal != null) {
				result = variableConvertedVal;    
			}
			else {    			    			
				result = SequenceTypeSupport.castXdmValueToAnotherType(var, asAttrString, null, xctxt);    			
				if (result == null) {
					throw new TransformerException("XTTE0570 : An XSL variable " + m_qname.toString() + "'s evaluation "
																		                             + "result doesn't match the specified "
																		                             + "xdm sequence type " + asAttrString + ".", srcLocator); 	
				}
			}
		}
		else if (var instanceof XPathMap) {
			try {
				result = SequenceTypeSupport.castXdmValueToAnotherType(var, asAttrString, null, xctxt);
			}
			catch (TransformerException ex) {
				String errMesg = ex.getMessage();
				boolean bool1 = errMesg.contains("value"); 
				boolean bool2 = errMesg.contains("cannot be cast to a type");
				if (bool1 && bool2) {
					errMesg = errMesg + " An error occured, while evaluating an xdm map's key or value. The corresponding "
							                                                                         + "XPath sequence type specified is " + asAttrString + ".";
					throw new TransformerException(errMesg, srcLocator);
				}
				else {
					throw new TransformerException(errMesg, srcLocator);
				}
			}    	   
		}
		else if (var instanceof XPathArray) {
			try {
				result = SequenceTypeSupport.castXdmValueToAnotherType(var, asAttrString, null, xctxt);
			}
			catch (TransformerException ex) {
				String errMesg = ex.getMessage();
				boolean bool1 = errMesg.contains("value"); 
				boolean bool2 = errMesg.contains("cannot be cast to a type");
				if (bool1 && bool2) {
					errMesg = errMesg + " An error occured, while evaluating an xdm array item. The corresponding "
							                                                                        + "XPath sequence type is specified as " + asAttrString + ".";
					throw new TransformerException(errMesg, srcLocator);
				}
				else {
					throw new TransformerException(errMesg, srcLocator);
				}
			}
		}
		else if ((selectExpression != null) && (selectExpression instanceof XRTreeFragSelectWrapper) && 
				                                                                                   ((var instanceof XString) || (var instanceof XSString))) {
			if (seqExpectedTypeData.getBuiltInSequenceType() == SequenceTypeSupport.XS_UNTYPED_ATOMIC) {
				result = new XSUntypedAtomic(XslTransformEvaluationHelper.getStrVal(var)); 
			}
			else {
				result = SequenceTypeSupport.castXdmValueToAnotherType(var, asAttrString, null, xctxt);
				if (result == null) {
					throw new TransformerException("XTTE0570 : An XSL variable " + m_qname.toString() + "'s evaluation "
																	                                  + "result doesn't match the specified "
																	                                  + "xdm sequence type " + asAttrString + ".", srcLocator); 
				}
			}
		}
		else if (var instanceof XPathNamedFunctionReference) {
			SequenceTypeFunctionTest sequenceTypeFunctionTest = seqExpectedTypeData.getSequenceTypeFunctionTest();
			if (sequenceTypeFunctionTest != null) {
			   if (sequenceTypeFunctionTest.isAnyFunctionTest()) {
			      return var;
			   }
			}
			else {
				throw new TransformerException("XTTE0570 : An XSL variable " + m_qname.toString() + "'s evaluation "
																	                              + "result doesn't match the specified "
																	                              + "xdm sequence type " + asAttrString + ".", srcLocator); 
		    }
		}    	    	    	    	    	    	
		else {
			try {
				result = SequenceTypeSupport.castXdmValueToAnotherType(var, asAttrString, null, xctxt);
				if (result == null) {
					throw new TransformerException("XTTE0570 : An XSL variable " + m_qname.toString() + "'s evaluation "
																                                      + "result doesn't match the specified "
																                                      + "xdm sequence type " + asAttrString + ".", srcLocator); 
				}
			}
			catch (TransformerException ex) {
				throw ex; 
			}
		}
		
		if (result == null) {
			if (seqExpectedTypeData.getBuiltInSequenceType() == SequenceTypeSupport.XS_INTEGER) {
				result = new XSInteger("0");
			}
			else if (seqExpectedTypeData.getBuiltInSequenceType() == SequenceTypeSupport.XS_INT) {
				result = new XSInt("0");
			}
		}
		
		return result;
	}
  
    private boolean xpathExpressionAllowable(Expression xpathExpr, QName inspectableVarName) {

    	boolean result = true;

    	if (xpathExpr != null) {
    		if (xpathExpr instanceof Operation) {
    			Operation opn1 = (Operation)xpathExpr;
    			Expression lOperand = opn1.getLeftOperand();
    			Expression rOperand = opn1.getRightOperand();

    			if (lOperand instanceof Variable) {
    				Variable var1 = (Variable)lOperand;
    				if ((var1.getQName()).equals(inspectableVarName)) {
    					result = true;    			       			   
    				}
    			}
    			else if (((lOperand instanceof XSAnyAtomicType) || (lOperand instanceof XNumber) 
                                                                || (lOperand instanceof XString) 
                                                                || (lOperand instanceof XBooleanStatic) 
                                                                || (lOperand instanceof XBoolean))) {
    				result = false;
                }    			

    			if (rOperand instanceof Variable) {
    				Variable var1 = (Variable)rOperand;
    				if ((var1.getQName()).equals(inspectableVarName)) {
    					result = true;
    				}
    			}
    			else if (((rOperand instanceof XSAnyAtomicType) || (rOperand instanceof XNumber) 
									                            || (rOperand instanceof XString) 
									                            || (rOperand instanceof XBooleanStatic) 
									                            || (rOperand instanceof XBoolean))) {
    				result = false;
                }
    		}
    		else if (xpathExpr instanceof FunctionOneArg) {
    			FunctionOneArg function = (FunctionOneArg)xpathExpr;    			
    			Expression arg0 = function.getArg0();

    			if (arg0 instanceof Variable) {
    				Variable var1 = (Variable)arg0;
    				if ((var1.getQName()).equals(inspectableVarName)) {
    					result = true;
    				}
    			}
    			else if (((arg0 instanceof XSAnyAtomicType) || (arg0 instanceof XNumber) 
									    					|| (arg0 instanceof XString) 
									    					|| (arg0 instanceof XBooleanStatic) 
									    					|| (arg0 instanceof XBoolean))) {
    				result = false;
    			}    			
    		}
    		else if (xpathExpr instanceof Function2Args) {
    			Function2Args function = (Function2Args)xpathExpr;
    			Expression arg0 = function.getArg0();
    			Expression arg1 = function.getArg1();
    			
    			result = xpathExpressionAllowable(arg0, inspectableVarName);
    			result = xpathExpressionAllowable(arg1, inspectableVarName);
    		}
            else if (xpathExpr instanceof Function3Args) {
            	Function3Args function = (Function3Args)xpathExpr;
    			Expression arg0 = function.getArg0();
    			Expression arg1 = function.getArg1();
    			Expression arg2 = function.getArg2();
    			
    			result = xpathExpressionAllowable(arg0, inspectableVarName);
    			result = xpathExpressionAllowable(arg1, inspectableVarName);
    			result = xpathExpressionAllowable(arg2, inspectableVarName);
    		}
            else if (xpathExpr instanceof FunctionMultiArgs) {
            	FunctionMultiArgs function = (FunctionMultiArgs)xpathExpr;
    			Expression arg0 = function.getArg0();
    			Expression arg1 = function.getArg1();
    			Expression arg2 = function.getArg2();
    			Expression[] exprArray = function.getArgs();
    			
    			result = xpathExpressionAllowable(arg0, inspectableVarName);
    			result = xpathExpressionAllowable(arg1, inspectableVarName);
    			result = xpathExpressionAllowable(arg2, inspectableVarName);
    		}
    		else {
    			Expression xpathParentExpr = (Expression)(xpathExpr.exprGetParent());

    			result = xpathExpressionAllowable(xpathParentExpr, inspectableVarName);
    		}
    	}

    	return result;
    }

}
