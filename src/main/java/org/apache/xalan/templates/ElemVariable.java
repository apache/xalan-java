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
import java.util.Map;
import java.util.Vector;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xalan.xslt.util.XslTransformSharedDatastore;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xml.utils.QName;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionNode;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.axes.LocPathIterator;
import org.apache.xpath.axes.SelfIteratorNoPredicate;
import org.apache.xpath.composite.SequenceTypeData;
import org.apache.xpath.composite.SequenceTypeKindTest;
import org.apache.xpath.composite.SequenceTypeSupport;
import org.apache.xpath.functions.Function;
import org.apache.xpath.functions.XPathDynamicFunctionCall;
import org.apache.xpath.functions.XSL3ConstructorOrExtensionFunction;
import org.apache.xpath.functions.XSL3FunctionService;
import org.apache.xpath.functions.XSLFunctionBuilder;
import org.apache.xpath.objects.ElemFunctionItem;
import org.apache.xpath.objects.ResultSequence;
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
import org.apache.xpath.operations.ArrowOp;
import org.apache.xpath.operations.Operation;
import org.apache.xpath.operations.Range;
import org.apache.xpath.operations.SimpleMapOperator;
import org.apache.xpath.patterns.NodeTest;
import org.w3c.dom.NodeList;

import xml.xpath31.processor.types.XSAnyType;
import xml.xpath31.processor.types.XSNumericType;
import xml.xpath31.processor.types.XSString;

/**
 * Implementation of XSLT xsl:variable element.
 * 
 * Ref : https://www.w3.org/TR/xslt-30/#element-variable
 * 
 * @author Scott Boag <scott_boag@us.ibm.com>
 * @author Gary L Peskin <garyp@apache.org>
 * @author Joseph Kesselman <jkesselm@apache.org>, Morris Kwan <mkwan@apache.org>,
 *         Brian James Minchau <minchau@apache.org>, Henry Zongaro <zongaro@ca.ibm.com>, 
 *         Christine Li <jycli@apache.org>
 *         
 * @author Mukul Gandhi <mukulg@apache.org>
 *         (XSLT 3 specific changes, to this class)
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
   * @serial
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
   * @serial
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
   * Tells if this is a top-level variable or param, or not.
   * @serial
   */
  private boolean m_isTopLevel = false;

  /**
   * Set if this is a top-level variable or param, or not.
   * @see <a href="http://www.w3.org/TR/xslt#top-level-variables">top-level-variables in XSLT Specification</a>
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
   * @see <a href="http://www.w3.org/TR/xslt#top-level-variables">top-level-variables in XSLT Specification</a>
   *
   * @return Boolean indicating whether this is a top-level variable
   * or param, or not.
   */
  public boolean getIsTopLevel()
  {
    return m_isTopLevel;
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

    int sourceNode = transformer.getXPathContext().getCurrentNode();
  
    XObject var = getValue(transformer, sourceNode);
    
    if (var instanceof XPathInlineFunction) 
    {
        Map<QName, XObject> xpathVarMap = (transformer.getXPathContext()).getXPathVarMap();
        xpathVarMap.put(m_qname, var);
    }
    else {
        transformer.getXPathContext().getVarStack().setLocalVariable(m_index, var);
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
 
    try {        
      if (m_selectPattern != null) {          
        selectExpression = m_selectPattern.getExpression();
        
        if (selectExpression instanceof XSL3ConstructorOrExtensionFunction) {        	
        	XSL3ConstructorOrExtensionFunction xpathFunc = (XSL3ConstructorOrExtensionFunction)selectExpression;
        	XSL3FunctionService xslFunctionService = xctxt.getXSLFunctionService();
            XObject evalResult = xslFunctionService.callFunction(xpathFunc, transformer, xctxt);
            
            QName asAttrQName = null;
            
            if (m_asAttr != null) {
            	PrefixResolver prefixResolver = xctxt.getNamespaceContext();
            	asAttrQName = new QName(m_asAttr, prefixResolver);
            }
            
            if (evalResult != null) {
            	String funcName = ((XSL3ConstructorOrExtensionFunction)selectExpression).getFunctionName();
            	String funcNamespace = ((XSL3ConstructorOrExtensionFunction)selectExpression).getNamespace();
            	
            	String evalResultStrValue = (evalResult instanceof XSString) ? ((XSString)evalResult).stringValue() : null;            	
            	if (m_asAttr != null && !(XSL3FunctionService.XS_VALID_TRUE).equals(evalResultStrValue)) {           	     
                   evalResult = SequenceTypeSupport.castXdmValueToAnotherType(evalResult, m_asAttr, null, xctxt);
                   if (evalResult == null) {
                	  String xpathPatternStr = m_selectPattern.getPatternString();
                	  throw new TransformerException("XTTE0570 : The supplied value " + xpathPatternStr + ", doesn't "
                	  		                                               + "match the expected sequence type " + m_asAttr + ".", srcLocator); 
                   }
                }
            	else if (m_asAttr != null && (XSL3FunctionService.XS_VALID_TRUE).equals(evalResultStrValue)) {
            	   String typeName = asAttrQName.getLocalName();
            	   String typeNamespace = asAttrQName.getNamespace();
            	   if (funcName.equals(typeName) && typeNamespace.equals(funcNamespace)) {            		  
            	      XObject valToBeValidated = (((XSL3ConstructorOrExtensionFunction)selectExpression).getArg(0)).execute(xctxt);
            	      evalResult = valToBeValidated; 
            	   }
            	   else {
            		  String xpathPatternStr = m_selectPattern.getPatternString();
                 	  throw new TransformerException("XTTE0570 : The supplied value " + xpathPatternStr + ", doesn't "
                 	  		                                               + "match the expected sequence type " + m_asAttr + ".", srcLocator); 
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
                 	  throw new javax.xml.transform.TransformerException("XTTE0570 : variable's computed value is not of "
                 	  		                                                        + "an expected type " + m_asAttr +".", srcLocator); 
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
             	  throw new javax.xml.transform.TransformerException("XTTE0570 : variable's computed value is not of "
             	  		                                                         + "an expected type " + m_asAttr +".", srcLocator); 
               }
            }
             
            return evalResult;
        }
        else if ((selectExpression instanceof Range) || (selectExpression instanceof ArrowOp)) {
            XObject evalResult = selectExpression.execute(xctxt);
            
            if (m_asAttr != null) {
               evalResult = SequenceTypeSupport.castXdmValueToAnotherType(evalResult, m_asAttr, null, xctxt);
               if (evalResult == null) {
              	  throw new javax.xml.transform.TransformerException("XTTE0570 : variable's computed value is not of "
              	  		                                                         + "an expected type " + m_asAttr +".", srcLocator); 
               }
            }
             
            return evalResult; 
        }
        else if (selectExpression instanceof Operation) {
        	Operation xpathOperation = (Operation)selectExpression;            
            XObject leftOperand = (xpathOperation.getLeftOperand()).execute(xctxt);
            XObject rightOperand = (xpathOperation.getRightOperand()).execute(xctxt);
            
            XObject evalResult = null;
            try {
               evalResult = xpathOperation.operate(leftOperand, rightOperand);
            }
            catch (TransformerException ex) {
               throw new TransformerException(ex.getMessage(), srcLocator); 
            }
            
            if (m_asAttr != null) {
               evalResult = SequenceTypeSupport.castXdmValueToAnotherType(evalResult, m_asAttr, null, xctxt);
               if (evalResult == null) {
            	  throw new javax.xml.transform.TransformerException("XTTE0570 : variable's computed value is not of "
            	  		                                                         + "an expected type " + m_asAttr +".", srcLocator); 
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
                               throw new javax.xml.transform.TransformerException("XPTY0004 : an index value used with an xdm "
                                                                                       + "sequence reference, is not an integer.", 
                                                                                                        srcLocator); 
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
                                                                                       + "sequence reference, is not an integer.", 
                                                                                                        srcLocator);  
                           }
                       }
                       else {
                           throw new javax.xml.transform.TransformerException("XPTY0004 : an index value used with an xdm sequence "
                                                                                                + "reference, is not numeric.", 
                                                                                                        srcLocator);   
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
    			
    			ElemFunction elemFunction = null;
    			
    			if (m_xslFunctionService.isFuncArityWellFormed(funcLocalNameRef)) {        	   
    				int hashCharIdx = funcLocalNameRef.indexOf('#');
    				String funcNameRef2 = funcLocalNameRef.substring(0, hashCharIdx);
    				int funcArity = Integer.valueOf(funcLocalNameRef.substring(hashCharIdx + 1));        		   
    				ElemTemplate elemTemplate = templateList.getXslFunction(new QName(funcNamespace, funcNameRef2), funcArity);        		   
    				if (elemTemplate != null) {
    					elemFunction = (ElemFunction)elemTemplate;
    					int xslFuncDefnParamCount = elemFunction.getParamCount();                      
    					String str = funcLocalNameRef.substring(hashCharIdx + 1);
    					int funcRefParamCount = (Integer.valueOf(str)).intValue();
    					if (funcRefParamCount != xslFuncDefnParamCount) {
    						throw new javax.xml.transform.TransformerException("FORG0006 : An XSL named function reference " + funcLocalNameRef 
																					    								     + " cannot resolve to a function "
																					    								     + "definition.", srcLocator); 
    					}
    				}
    			}
    			else {
    				throw new javax.xml.transform.TransformerException("FORG0006 : An XSL named function reference " + funcLocalNameRef 
																							    					 + " cannot resolve to a function "
																							    					 + "definition.", srcLocator);
    			}

    			if (elemFunction != null) {
    				ElemFunctionItem elemFunctionObject = new ElemFunctionItem(elemFunction);

    				return elemFunctionObject; 
    			}
    		}
    		catch (Exception ex) {
    			// NO OP
    		}
        }
  
        if (var == null) {
           var = m_selectPattern.execute(xctxt, sourceNode, this);
        }
        
        if (var != null) {
           var.allowDetachToRelease(false);

           if (transformer.getDebug()) {
               transformer.getTraceManager().emitSelectedEvent(sourceNode, this, "select", 
                                                                        m_selectPattern, var);
           }
        }
      }
      else if (null == getFirstChildElem()) {
          var = XString.EMPTYSTRING;
      }
      else {
    	  int rootNodeHandleOfRtf;
    	  
    	  if (m_parentNode instanceof Stylesheet) {
    		  // Global variable
    		  rootNodeHandleOfRtf = transformer.transformToGlobalRTF(this);
    	  }
    	  else {
    		  rootNodeHandleOfRtf = transformer.transformToRTF(this);
    	  }
    	  
    	  if (XslTransformSharedDatastore.xpathInlineFunction != null) {
    		  // This condition is met after method call transformer.transformToGlobalRTF/transformer.transformToRTF 
    		  // previously.
    		  if (m_asAttr == null) {
    			  var = XslTransformSharedDatastore.xpathInlineFunction;
    			  XslTransformSharedDatastore.xpathInlineFunction = null;
    		  }
    	  }
    	  else if (XslTransformSharedDatastore.xpathArray != null) {
    		  // This condition is met after method call transformer.transformToGlobalRTF/transformer.transformToRTF 
    		  // previously.
    		  if (m_asAttr == null) {
    			  var = XslTransformSharedDatastore.xpathArray;
    			  XslTransformSharedDatastore.xpathArray = null;
    		  }
    	  }
    	  else if (XslTransformSharedDatastore.xpathMap != null) {
    		  // This condition is met after method call transformer.transformToGlobalRTF/transformer.transformToRTF 
    		  // previously.
    		  if (m_asAttr == null) {
    			  var = XslTransformSharedDatastore.xpathMap;
    			  XslTransformSharedDatastore.xpathMap = null;
    		  }
    	  }
    	  else {
    	     NodeList nodeList = (new XRTreeFrag(rootNodeHandleOfRtf, xctxt, this)).convertToNodeset();    	  
    	     var = new XNodeSetForDOM(nodeList, xctxt);
    	  }
      }
    }
    finally {      
       xctxt.popCurrentNode();
    }
    
    if (m_asAttr != null) {       
       if (XslTransformSharedDatastore.xpathInlineFunction != null) {
    	   XPath seqTypeXPath = new XPath(m_asAttr, srcLocator, xctxt.getNamespaceContext(), 
                                                                                       XPath.SELECT, null, true);
           XObject seqTypeExpressionEvalResult = seqTypeXPath.execute(xctxt, xctxt.getContextNode(), xctxt.getNamespaceContext());
           SequenceTypeData seqExpectedTypeData = (SequenceTypeData)seqTypeExpressionEvalResult;
           SequenceTypeKindTest seqTypeKindTest = seqExpectedTypeData.getSequenceTypeKindTest();
           int seqTypeKindVal = 0;
           if (seqTypeKindTest != null) {
              seqTypeKindVal = seqTypeKindTest.getKindVal();
           }
           if ((seqExpectedTypeData.getSequenceTypeFunctionTest() != null) || (seqTypeKindVal == SequenceTypeSupport.ITEM_KIND)) {              	   
    	      var = XslTransformSharedDatastore.xpathInlineFunction;
    	      XslTransformSharedDatastore.xpathInlineFunction = null;
           }
           else {
        	  throw new TransformerException("XTTE0505 : The variable " + m_qname.getLocalName() + "'s value doesn't conform "
        	  		                                                + "to variable's expected type " + m_asAttr + ".", srcLocator); 
           }
       }
       else if (XslTransformSharedDatastore.xpathArray != null) {
    	   XPath seqTypeXPath = new XPath(m_asAttr, srcLocator, xctxt.getNamespaceContext(), 
                                                                                       XPath.SELECT, null, true);
           XObject seqTypeExpressionEvalResult = seqTypeXPath.execute(xctxt, xctxt.getContextNode(), xctxt.getNamespaceContext());
           SequenceTypeData seqExpectedTypeData = (SequenceTypeData)seqTypeExpressionEvalResult;
           SequenceTypeKindTest seqTypeKindTest = seqExpectedTypeData.getSequenceTypeKindTest();
           int seqTypeKindVal = 0;
           if (seqTypeKindTest != null) {
              seqTypeKindVal = seqTypeKindTest.getKindVal();
           }
           if ((seqExpectedTypeData.getSequenceTypeArrayTest() != null) || (seqTypeKindVal == SequenceTypeSupport.ITEM_KIND)) {              	   
    	      var = XslTransformSharedDatastore.xpathArray;
    	      XslTransformSharedDatastore.xpathArray = null;
           }
           else {
        	  throw new TransformerException("XTTE0505 : The variable " + m_qname.getLocalName() + "'s value doesn't conform "
        	  		                                                + "to variable's expected type " + m_asAttr + ".", srcLocator); 
           }
       }
       else if (XslTransformSharedDatastore.xpathMap != null) {
    	   XPath seqTypeXPath = new XPath(m_asAttr, srcLocator, xctxt.getNamespaceContext(), 
                                                                                       XPath.SELECT, null, true);
           XObject seqTypeExpressionEvalResult = seqTypeXPath.execute(xctxt, xctxt.getContextNode(), xctxt.getNamespaceContext());
           SequenceTypeData seqExpectedTypeData = (SequenceTypeData)seqTypeExpressionEvalResult;
           SequenceTypeKindTest seqTypeKindTest = seqExpectedTypeData.getSequenceTypeKindTest();
           int seqTypeKindVal = 0;
           if (seqTypeKindTest != null) {
              seqTypeKindVal = seqTypeKindTest.getKindVal();
           }
           if ((seqExpectedTypeData.getSequenceTypeMapTest() != null) || (seqTypeKindVal == SequenceTypeSupport.ITEM_KIND)) {              	   
    	      var = XslTransformSharedDatastore.xpathMap;
    	      XslTransformSharedDatastore.xpathMap = null;
           }
           else {
        	  throw new TransformerException("XTTE0505 : The variable " + m_qname.getLocalName() + "'s value doesn't conform "
        	  		                                                + "to variable's expected type " + m_asAttr + ".", srcLocator); 
           }
       }
       else if (var instanceof XNodeSetForDOM) {
          XObject variableConvertedVal = null;
          
          try {
             ElemFunction elemFunction = ElemFunction.getXSLFunctionService();
             variableConvertedVal = elemFunction.preprocessXslFunctionOrAVariableResult(var, m_asAttr, xctxt, m_qname);
          }
          catch (TransformerException ex) {
             throw new TransformerException(ex.getMessage(), srcLocator); 
          }
          
          if (variableConvertedVal != null) {
             var = variableConvertedVal;    
          }
          else {
             var = SequenceTypeSupport.castXdmValueToAnotherType(var, m_asAttr, null, xctxt); 
          }
       }
       else if (var instanceof XPathMap) {
    	  try {
    	     var = SequenceTypeSupport.castXdmValueToAnotherType(var, m_asAttr, null, xctxt);
    	  }
    	  catch (TransformerException ex) {
    		 String errMesg = ex.getMessage();
    		 boolean indicatorOne = errMesg.contains("value"); 
    		 boolean indicatorTwo = errMesg.contains("cannot be cast to a type");
    		 if (indicatorOne && indicatorTwo) {
    		    errMesg = errMesg + " An error occured, while evaluating xdm map's key or value [map's sequenceType is '" + m_asAttr + "'].";
    		    throw new TransformerException(errMesg, srcLocator);
    		 }
    		 else {
    		    throw new TransformerException(errMesg, srcLocator);
    		 }
    	  }    	   
       }
       else if (var instanceof XPathArray) {
    	  try {
      	     var = SequenceTypeSupport.castXdmValueToAnotherType(var, m_asAttr, null, xctxt);
      	  }
      	  catch (TransformerException ex) {
      		 String errMesg = ex.getMessage();
   		     boolean indicatorOne = errMesg.contains("value"); 
   		     boolean indicatorTwo = errMesg.contains("cannot be cast to a type");
   		     if (indicatorOne && indicatorTwo) {
   		        errMesg = errMesg + " An error occured, while evaluating an xdm array [array's sequenceType is '" + m_asAttr + "'].";
   		        throw new TransformerException(errMesg, srcLocator);
   		     }
   		     else {
   		        throw new TransformerException(errMesg, srcLocator);
   		     }
      	  }
       }
       else {
    	  try {
             var = SequenceTypeSupport.castXdmValueToAnotherType(var, m_asAttr, null, xctxt);
    	  }
    	  catch (TransformerException ex) {
    		 throw ex; 
    	  }
       }
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
    if(null == m_selectPattern  
       && sroot.getOptimizer())
    {
      XPath newSelect = rewriteChildToExpression(this);
      if(null != newSelect)
        m_selectPattern = newSelect;
    }
    
    StylesheetRoot.ComposeState cstate = sroot.getComposeState();
    
    // This should be done before addVariableName, so we don't have visibility 
    // to the variable now being defined.
    java.util.Vector vnames = cstate.getVariableNames();
    
    m_vars = (Vector)vnames.clone();
    m_globals_size = cstate.getGlobalsSize();
    
    if(null != m_selectPattern)
      m_selectPattern.fixupVariables(vnames, cstate.getGlobalsSize());
      
    // Only add the variable if this is not a global.  If it is a global, 
    // it was already added by stylesheet root.
    if(!(m_parentNode instanceof Stylesheet) && m_qname != null)
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
    if(m_parentNode instanceof Stylesheet)
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
  	if(null != m_selectPattern)
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
  	if((null != ns) && ns.equals(RedundentExprEliminator.PSUEDOVARNAMESPACE))
  	{
  		if(m_qname.getLocalName().startsWith("#"))
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

}
