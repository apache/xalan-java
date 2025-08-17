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

import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.transformer.NodeSortKey;
import org.apache.xalan.transformer.NodeSorter;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xml.dtm.DTMManager;
import org.apache.xml.utils.IntStack;
import org.apache.xml.utils.QName;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.axes.LocPathIterator;
import org.apache.xpath.axes.SelfIteratorNoPredicate;
import org.apache.xpath.composite.SequenceTypeData;
import org.apache.xpath.composite.SequenceTypeSupport;
import org.apache.xpath.composite.XPathForExpr;
import org.apache.xpath.composite.XPathSequenceConstructor;
import org.apache.xpath.functions.Function;
import org.apache.xpath.functions.XPathDynamicFunctionCall;
import org.apache.xpath.functions.XSL3ConstructorOrExtensionFunction;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XBooleanStatic;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathArray;
import org.apache.xpath.objects.XString;
import org.apache.xpath.operations.InstanceOf;
import org.apache.xpath.operations.Operation;
import org.apache.xpath.operations.Variable;

import xml.xpath31.processor.types.XSAnyAtomicType;
import xml.xpath31.processor.types.XSBoolean;
import xml.xpath31.processor.types.XSString;

/**
 * Implementation of the XSLT 3.0 xsl:for-each instruction.
 * 
 * @author Scott Boag <scott_boag@us.ibm.com>
 * @author Joseph Kesselman <jkesselm@apache.org>, Myriam Midy <mmidy@apache.org>,
 *         Ilene Seelemann <ilene@apache.org>
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 *         (XSLT 3.0 specific changes, to this class)
 * 
 * @xsl.usage advanced
 */
public class ElemForEach extends ElemTemplateElement implements ExpressionOwner
{
  static final long serialVersionUID = 6018140636363583690L;
  
  /** Set true to request some basic status reports */
  static final boolean DEBUG = false;
  
  /**
   * This is set by an "xalan-doc-cache-off" pi, or the old "xalan:doc-cache-off" pi.
   * The old form of the PI only works for XML parsers that are not namespace aware.
   * It tells the engine that
   * documents created in the location paths executed by this element
   * will not be reparsed. It's set by StylesheetHandler during
   * construction. Note that this feature applies _only_ to xsl:for-each
   * elements in its current incarnation; a more general cache management
   * solution is desperately needed.
   */
  public boolean m_doc_cache_off=false;
  
  /**
   * Construct a element representing xsl:for-each.
   */
  public ElemForEach(){}

  /**
   * The "select" expression.
   */
  protected Expression m_selectExpression = null;
  
  /**
   * Class field to store, XPath expression for subsequent 
   * processing.
   */
  protected XPath m_xpath = null;
  
  /**
   * This class field, represents the value of "xpath-default-namespace" 
   * attribute.
   */
  private String m_xpath_default_namespace = null;
  
  /**
   * This class field, represents the value of "expand-text" 
   * attribute.
   */
  private boolean m_expand_text;

  /**
   * Set the "select" attribute.
   *
   * @param xpath The XPath expression for the "select" attribute.
   */
  public void setSelect(XPath xpath)
  {
    m_selectExpression = xpath.getExpression();
    m_xpath = xpath;    
  }

  /**
   * Get the "select" attribute.
   *
   * @return The XPath expression for the "select" attribute.
   */
  public Expression getSelect()
  {
    return m_selectExpression;
  }
  
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
   * is declared on xsl:for-each instruction.
   */
  private boolean m_expand_text_declared;

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
  
  private Vector m_vars;
  
  private int m_globals_size;

  /**
   * This function is called after everything else has been
   * recomposed, and allows the template to set remaining
   * values that may be based on some other property that
   * depends on recomposition.
   *
   * @param sroot            StylesheetRoot object
   *
   * @throws TransformerException
   */
  public void compose(StylesheetRoot sroot) throws TransformerException
  {

    super.compose(sroot);

    int length = getSortElemCount();

    for (int i = 0; i < length; i++)
    {
      getSortElem(i).compose(sroot);
    }

    java.util.Vector vnames = sroot.getComposeState().getVariableNames();     

    if (null != m_selectExpression)
      m_selectExpression.fixupVariables(
        vnames, sroot.getComposeState().getGlobalsSize());
    else
    {
      m_selectExpression =
        getStylesheetRoot().m_selectDefault.getExpression();
    }
    
    m_vars = vnames;
    m_globals_size = sroot.getComposeState().getGlobalsSize(); 
    
  }
  
  /**
   * This after the template's children have been composed.
   */
  public void endCompose(StylesheetRoot sroot) throws TransformerException
  {
    int length = getSortElemCount();

    for (int i = 0; i < length; i++)
    {
      getSortElem(i).endCompose(sroot);
    }
    
    super.endCompose(sroot);
  }

  /**
   * Vector containing the xsl:sort elements associated with this element.
   */
  protected Vector m_sortElems = null;

  /**
   * Get the count xsl:sort elements associated with this element.
   * @return The number of xsl:sort elements.
   */
  public int getSortElemCount()
  {
    return (m_sortElems == null) ? 0 : m_sortElems.size();
  }

  /**
   * Get a xsl:sort element associated with this element.
   *
   * @param i Index of xsl:sort element to get
   *
   * @return xsl:sort element at given index
   */
  public ElemSort getSortElem(int i)
  {
    return (ElemSort) m_sortElems.elementAt(i);
  }

  /**
   * Set a xsl:sort element associated with this element.
   *
   * @param sortElem xsl:sort element to set
   */
  public void setSortElem(ElemSort sortElem)
  {

    if (null == m_sortElems)
      m_sortElems = new Vector();

    m_sortElems.addElement(sortElem);
  }

  /**
   * Get an int constant identifying the type of element.
   * @see org.apache.xalan.templates.Constants
   *
   * @return The token ID for this element
   */
  public int getXSLToken()
  {
    return Constants.ELEMNAME_FOREACH;
  }

  /**
   * Return the node name.
   *
   * @return The element's name
   */
  public String getNodeName()
  {
    return Constants.ELEMNAME_FOREACH_STRING;
  }

  /**
   * Execute the xsl:for-each transformation
   *
   * @param transformer non-null reference to the the current transform-time state.
   *
   * @throws TransformerException
   */
  public void execute(TransformerImpl transformer) throws TransformerException
  {

    transformer.pushCurrentTemplateRuleIsNull(true);    
    if (transformer.getDebug()) {
        transformer.getTraceManager().emitTraceEvent(this);   // invoke xsl:for-each element event
    }

    try
    {
        transformSelectedNodes(transformer);
    }
    finally
    {
        if (transformer.getDebug()) {
	       transformer.getTraceManager().emitTraceEndEvent(this);
        }
        
        transformer.popCurrentTemplateRuleIsNull();
    }
    
  }

  /**
   * Get template element associated with this
   *
   *
   * @return template element associated with this (itself)
   */
  protected ElemTemplateElement getTemplateMatch()
  {
    return this;
  }

  /**
   * Sort given nodes
   *
   *
   * @param xctxt The XPath runtime state for the sort.
   * @param keys Vector of sort keyx
   * @param sourceNodes Iterator of nodes to sort
   *
   * @return iterator of sorted nodes
   *
   * @throws TransformerException
   */
  public DTMCursorIterator sortNodes(
          XPathContext xctxt, Vector keys, DTMCursorIterator sourceNodes)
            throws TransformerException
  {

    NodeSorter sorter = new NodeSorter(xctxt);
    sourceNodes.setShouldCacheNodes(true);
    sourceNodes.runTo(-1);
    xctxt.pushContextNodeList(sourceNodes);

    try
    {
      sorter.sort(sourceNodes, keys, xctxt);
      sourceNodes.setCurrentPos(0);
    }
    finally
    {
      xctxt.popContextNodeList();
    }

    return sourceNodes;
  }

  /**
   * @param transformer non-null reference to the the current transform-time state.
   *
   * @throws TransformerException Thrown in a variety of circumstances.
   * 
   * @xsl.usage advanced
   */
  public void transformSelectedNodes(TransformerImpl transformer) throws 
                                                             TransformerException {
    
    XPathContext xctxt = transformer.getXPathContext();
    
    final int sourceNode = xctxt.getCurrentNode();
    
    SourceLocator srcLocator = xctxt.getSAXLocator();
    
    xctxt.setPos(0);
    xctxt.setLast(0);
    
    if (m_xpath_default_namespace != null) {    		
    	m_xpath = new XPath(m_xpath.getPatternString(), srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
    	m_selectExpression = m_xpath.getExpression();
  	}
    
    if (m_selectExpression instanceof Function) {
        XObject evalResult = ((Function)m_selectExpression).execute(xctxt);
        
        if (evalResult instanceof ResultSequence) {            
            processSequenceOrArray(transformer, xctxt, evalResult);
            
            return;
        }
        else if (evalResult instanceof XPathArray) {
        	processSequenceOrArray(transformer, xctxt, evalResult);
        	
            return;
        }
    }
    else if (m_selectExpression instanceof XPathDynamicFunctionCall) {
        XPathDynamicFunctionCall dfc = (XPathDynamicFunctionCall)m_selectExpression;
        XObject evalResult = dfc.execute(xctxt);
        
        if (evalResult instanceof ResultSequence) {            
            processSequenceOrArray(transformer, xctxt, evalResult);
            
            return;
        }
    }
    else if (m_selectExpression instanceof Variable) {
        XObject evalResult = ((Variable)m_selectExpression).execute(xctxt);                
        
        if (evalResult instanceof XSAnyAtomicType) {
        	ResultSequence resultSequence = new ResultSequence();
        	resultSequence.add(evalResult);
        	
        	processSequenceOrArray(transformer, xctxt, resultSequence);
        	
            return;
        }        
        else if (evalResult instanceof ResultSequence) {            
            processSequenceOrArray(transformer, xctxt, evalResult);
            
            return;
        }
        else if (evalResult instanceof XPathArray) {
        	processSequenceOrArray(transformer, xctxt, evalResult);
        	
        	return;
        }
    }    
    else if (m_selectExpression instanceof Operation) {
        XObject  evalResult = m_selectExpression.execute(xctxt);
        
        if (evalResult instanceof ResultSequence) {            
            processSequenceOrArray(transformer, xctxt, evalResult);
            
            return;
        }
    }    
    else if (m_selectExpression instanceof XPathForExpr) {
        XPathForExpr forExpr = (XPathForExpr)m_selectExpression;        
        XObject evalResult = forExpr.execute(xctxt);
        
        processSequenceOrArray(transformer, xctxt, evalResult);
        
        return;
    }    
    else if (m_selectExpression instanceof XPathSequenceConstructor) {
        XPathSequenceConstructor seqCtrExpr = (XPathSequenceConstructor)m_selectExpression;
        XObject evalResult = seqCtrExpr.execute(xctxt);
        
        processSequenceOrArray(transformer, xctxt, evalResult);
        
        return;
    }
    else if (m_selectExpression instanceof LocPathIterator) {    	
        LocPathIterator locPathIterator = (LocPathIterator)m_selectExpression;          
        
        boolean isProcessAsNodeset = true;
        DTMCursorIterator dtmIter = null;                     
        try {
           dtmIter = locPathIterator.asIterator(xctxt, sourceNode);
        }
        catch (ClassCastException ex) {
           isProcessAsNodeset = false;
        }
        
        if (dtmIter != null) {
        	ResultSequence resultSeq = null;
        	
        	Function func = locPathIterator.getFuncExpr();
        	XPathDynamicFunctionCall dfc = locPathIterator.getDynamicFuncCallExpr();
        	
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
        		
        		processSequenceOrArray(transformer, xctxt, resultSeq);
                
                return;
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
        		
        		processSequenceOrArray(transformer, xctxt, resultSeq);
                
                return;
        	}
        }
        
        if (!isProcessAsNodeset) {        	
        	String xpathPatternStr = m_xpath.getPatternString();
            
            if (xpathPatternStr.startsWith("$") && xpathPatternStr.contains("[") && 
                                                                              xpathPatternStr.endsWith("]")) {              
               // Here we handle the case, when an XPath expression has syntax of type $varName[expr], 
               // for example $varName[1], $varName[$idx], $varName[funcCall(arg)] etc, and $varName 
               // resolves to a 'ResultSequence' object.
                     
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
               
               Vector vars = new Vector();
               QName varQName = new QName(xpathPatternStr.substring(1, xpathPatternStr.indexOf('[')));
               vars.add(varQName);
               int globalsSize = 0;               
               xpathObj.fixupVariables(vars, globalsSize);
               
               XObject varEvalResult = xpathObj.execute(xctxt, xctxt.getCurrentNode(), xctxt.getNamespaceContext());                              
                    
               // Evaluate an, xdm sequence index XPath expression
               if (prefixTable != null) {
                  xpathIndexExprStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(
                                                                                                  xpathIndexExprStr, 
                                                                                                  prefixTable);
               }
                                   
               if (varEvalResult instanceof ResultSequence) {
                  ResultSequence inpResultSeq = (ResultSequence)varEvalResult;
                       
                  Integer indexVal = null;
                  try {
                     indexVal = Integer.valueOf(xpathIndexExprStr);
                  }
                  catch (NumberFormatException ex) {
                	 // NO OP
                  }
                  
                  ResultSequence rSeq = new ResultSequence();
                  
                  if (indexVal != null) {
                     XObject xObj = inpResultSeq.item((int)indexVal - 1);
                     rSeq.add(xObj);                       
                  }                  
                  else {
                     for (int idx = 0; idx < inpResultSeq.size(); idx++) {
                    	XObject seqItem = inpResultSeq.item(idx);
                    	
                    	setXPathContextForXslSequenceProcessing(inpResultSeq.size(), idx, seqItem, xctxt);
                    	
                    	xpathObj = new XPath(xpathIndexExprStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);                                                      
                        XObject xObj = xpathObj.execute(xctxt, xctxt.getCurrentNode(), xctxt.getNamespaceContext());
                        if (xObj != null) {
	                        if (xObj instanceof XSBoolean) {
	                        	XSBoolean xsBoolean = (XSBoolean)xObj;
	                        	if (xsBoolean.bool()) {
	                        	   rSeq.add(seqItem);
	                        	}
	                        }
	                        else if (xObj instanceof XBooleanStatic) {
	                        	XBooleanStatic xBool = (XBooleanStatic)xObj;
	                        	if (xBool.bool()) {
	                        	   rSeq.add(seqItem);
	                        	}
	                        }	                        
                        }
                        
                    	resetXPathContextForXslSequenceProcessing(seqItem, xctxt);
                     }
                     
                     processSequenceOrArray(transformer, xctxt, rSeq);
                     
                     return;
                  }
                }          
             }
         }
    }
    
    boolean bool1 = false;
    
    if (m_sortElems != null) {
    	for (int idx = 0; idx < m_sortElems.size(); idx++) {
    	    ElemSort elemSort = (ElemSort)m_sortElems.get(idx);
    	    AVT langAvt = elemSort.getLang();
    	    String collation = elemSort.getCollation();
    	    if ((langAvt != null) || (collation != null)) {
    	    	bool1 = true;
    	    }
    	}
    }
    
    final Vector sortKeys = (m_sortElems == null) ? null 
    		                                  : transformer.processSortKeys(this, sourceNode);
    boolean bool2 = false;
    if (!bool1 && (sortKeys != null)) {
    	int vecSize = sortKeys.size();
    	if (vecSize > 0) {
    	   for (int i = 0; i < vecSize; i++) {
    		  NodeSortKey nodeSortKey = (NodeSortKey)sortKeys.get(i);
    		  XPath selectPatternXPath = nodeSortKey.getSelectPattern();
    		  Expression expression = selectPatternXPath.getExpression();
    		  if (expression instanceof XSL3ConstructorOrExtensionFunction) {
    			  XSL3ConstructorOrExtensionFunction func1 = (XSL3ConstructorOrExtensionFunction)expression;
    			  String namespace = func1.getNamespace();
    			  if (!Constants.S_EXTENSIONS_JAVA_URL.equals(namespace)) {
    				  bool2 = true;
    				  break;
    			  }
    		  }
    	   }
    	}
    }
    
    if (!(bool1 || bool2)) {    	    	
    	DTMCursorIterator sourceNodes = m_selectExpression.asIterator(xctxt, sourceNode);

    	try
    	{
    		// Sort if we need to
    		if (sortKeys != null)
    		    sourceNodes = sortNodes(xctxt, sortKeys, sourceNodes);

    		if (transformer.getDebug())
    		{                
    			Expression expr = m_xpath.getExpression();
    			org.apache.xpath.objects.XObject xObject = expr.execute(xctxt);
    			int current = xctxt.getCurrentNode();
    			transformer.getTraceManager().emitSelectedEvent(current, this, "select", 
    					                                                              m_xpath, xObject);
    		}

    		xctxt.pushCurrentNode(DTM.NULL);

    		IntStack currentNodes = xctxt.getCurrentNodeStack();

    		xctxt.pushCurrentExpressionNode(DTM.NULL);

    		IntStack currentExpressionNodes = xctxt.getCurrentExpressionNodeStack();

    		xctxt.pushSAXLocatorNull();
    		xctxt.pushContextNodeList(sourceNodes);
    		transformer.pushElemTemplateElement(null);

    		// Should be able to get this from the iterator but there must be a bug
    		DTM dtm = xctxt.getDTM(sourceNode);
    		int docID = sourceNode & DTMManager.IDENT_DTM_DEFAULT;
    		int child;

    		while (DTM.NULL != (child = sourceNodes.nextNode()))
    		{
    			currentNodes.setTop(child);
    			currentExpressionNodes.setTop(child);

    			if ((child & DTMManager.IDENT_DTM_DEFAULT) != docID)
    			{
    				dtm = xctxt.getDTM(child);
    				docID = child & DTMManager.IDENT_DTM_DEFAULT;
    			} 

    			// Emit a trace event for the template
    			if (transformer.getDebug())
    			{
    				transformer.getTraceManager().emitTraceEvent(this);
    			}

    			// And execute the child templates.
    			// Loop through the children of the template, calling execute on 
    			// each of them.
    			for (ElemTemplateElement t = this.m_firstChild; t != null;
    					                                               t = t.m_nextSibling)
    			{
    				xctxt.setSAXLocator(t);
    				transformer.setCurrentElement(t);
    				t.execute(transformer);
    			}

    			if (transformer.getDebug())
    			{
    				// We need to make sure an old current element is not 
    				// on the stack. See TransformerImpl#getElementCallstack.
    				transformer.setCurrentElement(null);
    				transformer.getTraceManager().emitTraceEndEvent(this);
    			}


    			// KLUGE: Implement <?xalan:doc_cache_off?> 
    			// ASSUMPTION: This will be set only when the XPath was indeed
    			// a call to the Document() function. Calling it in other
    			// situations is likely to fry Xalan.
    			//
    			// %REVIEW% We need a MUCH cleaner solution -- one that will
    			// handle cleaning up after document() and getDTM() in other
    			// contexts. The whole SourceTreeManager mechanism should probably
    			// be moved into DTMManager rather than being explicitly invoked in
    			// FuncDocument and here.
    			if(m_doc_cache_off)
    			{
    				if(DEBUG)
    					System.out.println("JJK***** CACHE RELEASE *****\n"+
    							"\tdtm="+dtm.getDocumentBaseURI());
    				// NOTE: This will work because this is _NOT_ a shared DTM, and thus has
    				// only a single Document node. If it could ever be an RTF or other
    				// shared DTM, this would require substantial rework.
    				xctxt.getSourceTreeManager().removeDocumentFromCache(dtm.getDocument());
    				xctxt.release(dtm,false);
    			}
    		}
    		
    		return;
    	}
    	finally
    	{
    		if (transformer.getDebug())
    			transformer.getTraceManager().emitSelectedEndEvent(sourceNode, this,
    					"select", new XPath(m_selectExpression),
    					new org.apache.xpath.objects.XMLNodeCursorImpl(sourceNodes));

    		xctxt.popSAXLocator();
    		xctxt.popContextNodeList();
    		transformer.popElemTemplateElement();
    		xctxt.popCurrentExpressionNode();
    		xctxt.popCurrentNode();
    		sourceNodes.detach();
    	}    
    }
    else {
    	DTMCursorIterator sourceNodes = m_selectExpression.asIterator(xctxt, sourceNode);

    	ResultSequence rSeq = new ResultSequence();

    	int nextNode;
    	while (DTM.NULL != (nextNode = sourceNodes.nextNode()))
    	{
    		XMLNodeCursorImpl xdmNode = new XMLNodeCursorImpl(nextNode, xctxt);
    		rSeq.add(xdmNode);
    	}

    	processSequenceOrArray(transformer, xctxt, rSeq);
    }
    
  }

  /**
   * Add a child to the child list.
   * <!ELEMENT xsl:apply-templates (xsl:sort|xsl:with-param)*>
   * <!ATTLIST xsl:apply-templates
   *   select %expr; "node()"
   *   mode %qname; #IMPLIED
   * >
   *
   * @param newChild Child to add to child list
   *
   * @return Child just added to child list
   */
  public ElemTemplateElement appendChild(ElemTemplateElement newChild)
  {

	super.appendChild(newChild);
	  
	int type = ((ElemTemplateElement) newChild).getXSLToken();

    if (Constants.ELEMNAME_SORT == type)
    {
      setSortElem((ElemSort) newChild);
    }
    
    return newChild;
  }
  
  /**
   * Call the children visitors.
   * @param visitor The visitor whose appropriate method will be called.
   */
  public void callChildVisitors(XSLTVisitor visitor, boolean callAttributes)
  {
  	if(callAttributes && (null != m_selectExpression))
  		m_selectExpression.callVisitors(this, visitor);
  		
    int length = getSortElemCount();

    for (int i = 0; i < length; i++)
    {
      getSortElem(i).callVisitors(visitor);
    }

    super.callChildVisitors(visitor, callAttributes);
  }

  /**
   * @see ExpressionOwner#getExpression()
   */
  public Expression getExpression()
  {
    return m_selectExpression;
  }

  /**
   * @see ExpressionOwner#setExpression(Expression)
   */
  public void setExpression(Expression exp)
  {
  	exp.exprSetParent(this);
  	m_selectExpression = exp;
  }
   
   /**
    * Method definition, to process an xdm sequence or an array, to be 
    * used by xsl:for-each's contained sequence constructor.
    */
   private void processSequenceOrArray(TransformerImpl transformer,
                                                              XPathContext xctxt, XObject evalResult) 
                                                              throws TransformerException {       
	   
	   List<XObject> xdmItemList = null;
	   
	   SourceLocator srcLocator = xctxt.getSAXLocator();
	   
	   if (evalResult instanceof ResultSequence) {
		   ResultSequence resultSeq = (ResultSequence)evalResult;
		   xdmItemList = resultSeq.getResultSequenceItems();   
	   }
	   else if (evalResult instanceof XPathArray) {
		   XPathArray xpathArr = (XPathArray)evalResult;
		   ResultSequence resultSeq = xpathArr.atomize();
		   xdmItemList = resultSeq.getResultSequenceItems();
	   }
	   	   
	   	   
	   int itemCount = xdmItemList.size();
	   XObject xObj0 = null;
	   
	   if (itemCount == 0) {
		   return; 
	   }
	   else if (itemCount == 1) {
		   // An input sequence or array is of size one		   
		   xObj0 = xdmItemList.get(0);		   
		   if (xObj0 instanceof XMLNodeCursorImpl) {
			  xObj0 = ((XMLNodeCursorImpl)xObj0).getFresh(); 
		   }

		   setXPathContextForXslSequenceProcessing(1, 0, xObj0, xctxt);

		   for (ElemTemplateElement elemTemplateElem = this.m_firstChild; elemTemplateElem != null; 
				   														elemTemplateElem = elemTemplateElem.m_nextSibling) {
			   xctxt.setSAXLocator(elemTemplateElem);
			   transformer.setCurrentElement(elemTemplateElem);
			   elemTemplateElem.execute(transformer);              
		   }

		   resetXPathContextForXslSequenceProcessing(xObj0, xctxt);

		   return; 
	   }	   
	   else {
		   xObj0 = xdmItemList.get(0);
		   
		   // Sort xdm input items, if specified within an XSL stylesheet 
		   // using xsl:for-each instruction's one or more xsl:sort sibling
		   // elements.		   		   
		   
		   int xslSortElemCount = getSortElemCount();
		   
		   List<SortableItem> sortableItemList = new ArrayList<SortableItem>();					   

		   for (int idx = 0; idx < xdmItemList.size(); idx++) {
			   XObject resultSeqItem = xdmItemList.get(idx);
			   SortableItem sortableItem = null;
			   List<SortKey> sortKeyList = new ArrayList<SortKey>();
			   for (int idx1 = 0; idx1 < xslSortElemCount; idx1++) {				   
				   ElemSort elemSort = getSortElem(idx1);

				   XPath selectXPath = elemSort.getSelect();

				   // This can be absent (which will be default "ascending"), or 
				   // specified as "ascending" | "descending".
				   String sortOrderStr = null;
				   AVT sortOrderAvt = elemSort.getOrder();
				   if (sortOrderAvt != null) {
					   sortOrderStr = sortOrderAvt.evaluate(xctxt, DTM.NULL, xctxt.getNamespaceContext());
				   }

				   // This can be absent, or specified as "upper-first" | "lower-first".
				   // This is used for string content sorting.
				   String caseOrderStr = null;		  
				   AVT caseOrderAvt = elemSort.getCaseOrder();
				   if (caseOrderAvt != null) {
					   caseOrderStr = caseOrderAvt.evaluate(xctxt, DTM.NULL, xctxt.getNamespaceContext()); 
				   }			   			   			   

				   // This can be absent (which will be default "text"), or specified as 
				   // "text" | "number" | eqname.  
				   String dataTypeStr = null;          
				   AVT dataTypeAvt = elemSort.getDataType();
				   if (dataTypeAvt != null) {
					  dataTypeStr = dataTypeAvt.evaluate(xctxt, DTM.NULL, xctxt.getNamespaceContext()); 
				   }
				   
				   String langStr = null;
				   AVT langAvt = elemSort.getLang();
				   if (langAvt != null) {
					  langStr = langAvt.evaluate(xctxt, DTM.NULL, xctxt.getNamespaceContext());  
				   }

				   Class clazz0 = xObj0.getClass();
				   
				   if ((dataTypeStr != null) && !("text".equals(dataTypeStr) || "number".equals(dataTypeStr))) {							  
					   XPath seqTypeXPath = new XPath(dataTypeStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null, true);            
					   XObject seqTypeObj = seqTypeXPath.execute(xctxt, DTM.NULL, xctxt.getNamespaceContext());            
					   SequenceTypeData seqExpectedTypeData = (SequenceTypeData)seqTypeObj;
					   InstanceOf instanceOf = new InstanceOf();
					   XObject xObj = instanceOf.operate(resultSeqItem, seqExpectedTypeData);
					   if (!xObj.bool()) {
						   throw new javax.xml.transform.TransformerException("XPTY0004 : An xdm input sequence processed by xsl:for-each's "
																													   + "xsl:sort instruction has an item that is not of "
																													   + "the type '" + dataTypeStr + "', specified by xsl:sort's "
																													   + "data-type attribute.", srcLocator);  
					   }

					   if (seqExpectedTypeData.getBuiltInSequenceType() == SequenceTypeSupport.STRING) {
						   dataTypeStr = "text"; 
					   }
				   }						  

				   if (idx > 1) {
					   Class clazz1 = resultSeqItem.getClass();
					   if (!clazz1.equals(clazz0)) {
						   // All the sequence items are not of the same type								 
						   throw new javax.xml.transform.TransformerException("XPTY0004 : An xdm input sequence processed by xsl:for-each's "
																											           + "xsl:sort instruction, dosn't have items of "
																											           + "the same type.", srcLocator); 
					   }
				   }

				   XObject sorkKeyObj = null;						  
				   if (selectXPath.getExpression() instanceof SelfIteratorNoPredicate) {
					   sorkKeyObj = resultSeqItem; 
				   }
				   else if (resultSeqItem instanceof XMLNodeCursorImpl) {
					   XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)resultSeqItem;
					   int contextNode = xmlNodeCursorImpl.asNode(xctxt);
					   sorkKeyObj = selectXPath.execute(xctxt, contextNode, xctxt.getNamespaceContext());
				   }
				   else {
					   XObject prevContextItem = xctxt.getXPath3ContextItem();						  
					   xctxt.setXPath3ContextItem(resultSeqItem);
					   sorkKeyObj = selectXPath.execute(xctxt, DTM.NULL, xctxt.getNamespaceContext());
					   xctxt.setXPath3ContextItem(prevContextItem); 
				   }

				   // For variable dataTypeStr's value other than "text" and "number",
				   // SortableItem class's method 'compareTo' takes care of the right 
				   // comparison between sort keys.

				   if ("text".equals(dataTypeStr)) {
					   if ((sorkKeyObj instanceof XString) || (sorkKeyObj instanceof XSString) || (sorkKeyObj instanceof XMLNodeCursorImpl)) { 
						   sorkKeyObj = new XSString(XslTransformEvaluationHelper.getStrVal(sorkKeyObj));
					   }
				   }
				   else if ("number".equals(dataTypeStr)) {
					   try {
						   double dbl = Double.valueOf(XslTransformEvaluationHelper.getStrVal(sorkKeyObj));
						   sorkKeyObj = new XNumber(dbl);
					   }
					   catch (NumberFormatException ex) {
						   throw new javax.xml.transform.TransformerException("XPTY0004 : An xdm input sequence processed by xsl:for-each's "
																													   + "xsl:sort instruction has an item that is not of "
																													   + "the type '" + dataTypeStr + "', specified by xsl:sort's "
																													   + "data-type attribute.", srcLocator); 
					   }
				   }						  

				   String collation = elemSort.getCollation();
				   
				   SortKey sortKey = new SortKey(sorkKeyObj, sortOrderStr, collation, caseOrderStr, langStr);
				   if (collation != null) {
					  sortKey.setCollationDeclared(true); 
				   }
				   
				   sortKeyList.add(sortKey);
			   }
			   
			   sortableItem = new SortableItem(resultSeqItem, sortKeyList);
			   
			   sortableItemList.add(sortableItem);
		   }
		   
		   if (xslSortElemCount > 0) {
		      sortableItemList.sort(null);
		   }
		   			   
		   for (int idx = 0; idx < sortableItemList.size(); idx++) {
			   SortableItem sortableItem = sortableItemList.get(idx);				   
			   XObject resultSeqItem = sortableItem.getInputItem(); 
			   if (resultSeqItem instanceof XMLNodeCursorImpl) {
				   resultSeqItem = ((XMLNodeCursorImpl)resultSeqItem).getFresh(); 
			   }

			   setXPathContextForXslSequenceProcessing(sortableItemList.size(), idx, resultSeqItem, xctxt);

			   for (ElemTemplateElement elemTemplateElem = this.m_firstChild; elemTemplateElem != null; 
					                                                                          elemTemplateElem = elemTemplateElem.m_nextSibling) {
				   xctxt.setSAXLocator(elemTemplateElem);
				   transformer.setCurrentElement(elemTemplateElem);
				   elemTemplateElem.execute(transformer);              
			   }

			   resetXPathContextForXslSequenceProcessing(resultSeqItem, xctxt);
		   }			   
        }
   }
   
   /**
    * Class definition, representing information details available 
    * within one xsl:sort element, along with an xdm sort key value.
    */
   class SortKey {
   	   /**
   	    * A sort key object value associated with an xdm 
   	    * input item.
   	    */
   	   private XObject m_sortKeyObj = null;
   	   
   	   /**
   	    * A sort order denoting 'ascending' or 'descending' 
   	    * sort.
   	    */
   	   private String m_sortOrderStr = null;
   	   
   	   /**
   	    * A collation URI, to be used for text sorting.
   	    */
   	   private String m_collation = null;
   	   
   	   /**
   	    * This class field is true if xsl:sort has a 'collation' 
   	    * attribute, otherwise false.
   	    */
   	   private boolean m_collation_declared = false;
   	   
   	   /**
   	    * Value of xsl:sort instruction's case-order 
   	    * attribute.
   	    */
   	   private String m_caseOrderStr = null;
   	   
   	   /**
   	    * Value of xsl:sort instruction's 'lang' attribute.
   	    */
   	   private String m_langStr = null;
   	   
   	   /**
   	    * Class constructor.
   	    */
   	   public SortKey(XObject sortKeyObj, String sortOrder, String collation,
   			                              String caseOrderStr, String langStr) {
   		   this.m_sortKeyObj = sortKeyObj;
   		   this.m_sortOrderStr = sortOrder;
   		   this.m_collation = collation;
   		   this.m_caseOrderStr = caseOrderStr;
   		   this.m_langStr = langStr; 
   	   }
   	   
   	   public XObject getSortKeyObj() {
   		   return m_sortKeyObj;
   	   }

   	   public void setInpItemSortKeyObj(XObject sortKeyObj) {
   		   this.m_sortKeyObj = sortKeyObj;
   	   }

   	   public String getSortOrderStr() {
   		   return m_sortOrderStr;
   	   }

   	   public void setSortOrderStr(String sortOrderStr) {
   		   this.m_sortOrderStr = sortOrderStr;
   	   }
   	   
   	   private void setCollationDeclared(boolean yesNno) {
   		   this.m_collation_declared = yesNno;
   	   }
   	   
   	   private boolean isCollationDeclared() {
   		   return this.m_collation_declared; 
   	   }
   	   
   	   public String getCollation() {
   		   return this.m_collation;
   	   }
   	   
   	   public void setCollation(String collation) {
   		   this.m_collation = collation; 
   	   }
   	   
   	   public String getCaseOrder() {
   		   return this.m_caseOrderStr;
   	   }
   	   
   	   public void setCaseOrder(String caseOrder) {
   		   this.m_caseOrderStr = caseOrder; 
   	   }
   	   
   	   public String getLangStr() {
   		   return this.m_langStr;
   	   }
   	   
   	   public void setLangStr(String langStr) {
   		   this.m_langStr = langStr;
   	   }
   }
   
   /**
    * A class definition, representing an xdm sortable object,
    * to be used by xsl:for-each's one or more xsl:sort instructions.
    */
   class SortableItem implements Comparable<SortableItem> {
	   
	   /**
	    * An input sequence item that needs to be sorted.
	    */
	   private XObject m_inputItem = null;
	   
	   /**
	    * A list of sequential SortKey objects. 
	    */
	   private List<SortKey> m_sortKeyList = null;
	   
	   /**
	    * Class constructor.
	    */
	   public SortableItem(XObject inpItem, List<SortKey> sortKeyList) {
		   this.m_inputItem = inpItem;
		   this.m_sortKeyList = sortKeyList; 
	   }

	   @Override
	   public int compareTo(SortableItem obj2) {		   		   
		   
		   int result = 0;
		   
		   try {			   
			   for (int idx = 0; idx < m_sortKeyList.size(); idx++) {
				   SortKey sortKey = m_sortKeyList.get(idx);				  
				   List<SortKey> sortKeyList2 = obj2.getSortKeyList();
				   int size2 = sortKeyList2.size();
				   if (size2 > 1) {
					   for (int idx2 = 0; idx2 < size2; idx2++) {
						   if (idx == idx2) {
							   SortKey sortKey2 = sortKeyList2.get(idx2);

							   boolean m_collation_declared = sortKey.isCollationDeclared();
							   String m_collation = sortKey.getCollation();
							   String m_sortOrderStr = sortKey.getSortOrderStr();
							   String m_caseOrderStr = sortKey.getCaseOrder();
							   String m_langStr = sortKey.getLangStr();

							   XObject sortKeyObj = sortKey.getSortKeyObj();
							   XObject sortKeyObj2 = sortKey2.getSortKeyObj();

							   boolean isSortKeyEqual = sortKeyEqual(m_collation_declared, m_collation, m_langStr, sortKeyObj, sortKeyObj2);                       

							   if (!isSortKeyEqual) {
								   boolean lessThan = sortKeyLessThan(m_collation_declared, m_collation, m_sortOrderStr, m_caseOrderStr, 
										                              m_langStr, sortKeyObj, sortKeyObj2);
								   result = (lessThan ? -1 : 1);
							   }
							   else {
								   break;
							   }

							   return result;
						   }
					   }
				   }
				   else {
					   boolean m_collation_declared = sortKey.isCollationDeclared();
					   String m_collation = sortKey.getCollation();
					   String m_sortOrderStr = sortKey.getSortOrderStr();
					   String m_caseOrderStr = sortKey.getCaseOrder();
					   String m_langStr = sortKey.getLangStr();

					   XObject sortKeyObj = sortKey.getSortKeyObj();
					   XObject sortKeyObj2 = (sortKeyList2.get(0)).getSortKeyObj();
					   
					   boolean isSortKeyEqual = sortKeyEqual(m_collation_declared, m_collation, m_langStr, sortKeyObj, sortKeyObj2);                       

					   if (!isSortKeyEqual) {
						   boolean lessThan = sortKeyLessThan(m_collation_declared, m_collation, m_sortOrderStr, m_caseOrderStr, 
								                              m_langStr, sortKeyObj, sortKeyObj2);
						   result = (lessThan ? -1 : 1);
					   }

					   return result;
				   }
			   }			   			   
		   }
		   catch (TransformerException ex) {
			   // NO OP			  
		   }
		   
		   return result;
	   }

	   /**
	    * A method definition, to check whether one sort key value precedes another 
	    * sort key value. If a xsl:sort instruction specifies a collation URI, xsl:sort 
	    * instruction's 'lang' attribute is ignored. When xsl:sort instruction doesn't 
	    * specify both collation URI and 'lang' attributes, the sort keys are compared 
	    * using a lexicographic comparison which is equivalent to using a unicode codepoint 
	    * collation.
	    * 
	    * @param m_collation_declared						Boolean value true, if xsl:sort instruction 
	    *                                                   has declared a 'collation' attribute.
	    * @param m_collation                                An optional collation URI, specified on xsl:sort
	    *                                                   instruction.
	    * @param m_sortOrderStr                             xsl:sort instruction's 'order' attribute value,
	    *                                                   which has default value 'ascending'.
	    * @param m_caseOrderStr                             xsl:sort instructon's 'case-order' attribute value. 
	    * @param m_langStr                                  xsl:sort instructon's 'lang' attribute value.
	    * @param sortKeyObj                                 One of the sort key value
	    * @param sortKeyObj2                                The second sort key value
	    * @return                                           Boolean value true, if the first sort key value
	    *                                                   precedes another, otherwise false.
	    * @throws TransformerException
	    */
	   private boolean sortKeyLessThan(boolean m_collation_declared, String m_collation, String m_sortOrderStr,
			                           String m_caseOrderStr, String m_langStr, XObject sortKeyObj, XObject sortKeyObj2)
					                                                                                                 throws TransformerException {

		   boolean result = false;

		   if ((Constants.ATTRVAL_ORDER_ASCENDING).equals(m_sortOrderStr)) {
			   if (m_collation_declared) {
				   result = sortKeyObj.vcLessThan(sortKeyObj2, null, m_collation, true);				  
			   }
			   else if ((Constants.ATTRVAL_CASEORDER_UPPER).equals(m_caseOrderStr)) {
				   String str1 = XslTransformEvaluationHelper.getStrVal(sortKeyObj);
				   String str2 = XslTransformEvaluationHelper.getStrVal(sortKeyObj2);
				   if (m_langStr == null) {
					   int i = str1.compareToIgnoreCase(str2);
					   if (i == 0) {
						   i = str1.compareTo(str2);
					   }

					   result = (i < 0) ? true : false;
				   }
				   else {
					   Locale locale = new Locale(m_langStr);
					   Collator collator = Collator.getInstance(locale);
					   collator.setStrength(Collator.PRIMARY);
					   int i = collator.compare(str1, str2);
					   if (i == 0) {
						   collator.setStrength(Collator.TERTIARY);
						   i = collator.compare(str1, str2);
					   }

					   result = (i < 0) ? true : false;
				   }
			   }
			   else if ((Constants.ATTRVAL_CASEORDER_LOWER).equals(m_caseOrderStr)) {
				   String str1 = XslTransformEvaluationHelper.getStrVal(sortKeyObj);
				   String str2 = XslTransformEvaluationHelper.getStrVal(sortKeyObj2);
				   if (m_langStr == null) {
					   int i = str1.compareToIgnoreCase(str2);
					   if (i == 0) {
						   i = -1 * str1.compareTo(str2);
					   }

					   result = (i < 0) ? true : false;
				   }
				   else {
					   Locale locale = new Locale(m_langStr);
					   Collator collator = Collator.getInstance(locale);
					   collator.setStrength(Collator.PRIMARY);
					   int i = collator.compare(str1, str2);
					   if (i == 0) {
						   collator.setStrength(Collator.TERTIARY);
						   i = -1 * collator.compare(str1, str2);
					   }

					   result = (i < 0) ? true : false;
				   }
			   }
			   else if (m_langStr != null) {
				   String str1 = XslTransformEvaluationHelper.getStrVal(sortKeyObj);
				   String str2 = XslTransformEvaluationHelper.getStrVal(sortKeyObj2);

				   Locale locale = new Locale(m_langStr);
				   Collator collator = Collator.getInstance(locale);
				   collator.setStrength(Collator.TERTIARY);

				   int i = collator.compare(str1, str2) < 0 ? -1 : 1;

				   result = (i < 0) ? true : false;
			   }
			   else {
				   int i = sortKeyObj.vcLessThan(sortKeyObj2, null, m_collation, true) ? -1 : 1;

				   result = (i < 0) ? true : false;
			   }
		   }
		   else if ((Constants.ATTRVAL_ORDER_DESCENDING).equals(m_sortOrderStr)) {
			   if (m_collation_declared) {
				   result = !sortKeyObj.vcLessThan(sortKeyObj2, null, m_collation, true);
			   }							  
			   else if ((Constants.ATTRVAL_CASEORDER_UPPER).equals(m_caseOrderStr)) {
				   String str1 = XslTransformEvaluationHelper.getStrVal(sortKeyObj);
				   String str2 = XslTransformEvaluationHelper.getStrVal(sortKeyObj2);					  
				   if (m_langStr == null) {
					   int i = str1.compareToIgnoreCase(str2);
					   if (i == 0) {
						   i = -1 * str1.compareTo(str2);
					   }
					   else {
						   i = -i;
					   }

					   result = (i < 0) ? true : false;
				   }
				   else {
					   Locale locale = new Locale(m_langStr);
					   Collator collator = Collator.getInstance(locale);
					   collator.setStrength(Collator.PRIMARY);
					   int i = collator.compare(str1, str2);
					   if (i == 0) {
						   collator.setStrength(Collator.TERTIARY);
						   i = -1 * collator.compare(str1, str2);
					   }
					   else {
						   i = -i;
					   }

					   result = (i < 0) ? true : false;
				   }					   
			   }
			   else if ((Constants.ATTRVAL_CASEORDER_LOWER).equals(m_caseOrderStr)) {
				   String str1 = XslTransformEvaluationHelper.getStrVal(sortKeyObj);
				   String str2 = XslTransformEvaluationHelper.getStrVal(sortKeyObj2);					   
				   if (m_langStr == null) {
					   int i = str1.compareToIgnoreCase(str2);
					   if (i == 0) {
						   i = str1.compareTo(str2);
					   }
					   else {
						   i = -i;
					   }

					   result = (i < 0) ? true : false;
				   }
				   else {
					   Locale locale = new Locale(m_langStr);
					   Collator collator = Collator.getInstance(locale);
					   collator.setStrength(Collator.PRIMARY);
					   int i = collator.compare(str1, str2);
					   if (i == 0) {
						   collator.setStrength(Collator.TERTIARY);
						   i = collator.compare(str1, str2);
					   }
					   else {
						   i = -i;
					   }

					   result = (i < 0) ? true : false;
				   }
			   }
			   else if (m_langStr != null) {
				   String str1 = XslTransformEvaluationHelper.getStrVal(sortKeyObj);
				   String str2 = XslTransformEvaluationHelper.getStrVal(sortKeyObj2);

				   Locale locale = new Locale(m_langStr);
				   Collator collator = Collator.getInstance(locale);
				   collator.setStrength(Collator.TERTIARY);

				   int i = collator.compare(str1, str2) < 0 ? -1 : 1;

				   result = (i < 0) ? false : true;
			   }
			   else {
				   int i = sortKeyObj.vcLessThan(sortKeyObj2, null, m_collation, true) ? 1 : -1;

				   result = (i < 0) ? true : false;
			   }
		   }

		   return result;
	   }

	   /**
	    * A method definition, to compare two sort key values for equality. If a xsl:sort 
	    * instruction specifies a collation URI, xsl:sort instruction's 'lang' attribute is
	    * ignored. When xsl:sort instruction doesn't specify both collation URI and 'lang' 
	    * attributes, the sort keys are compared using a lexicographic comparison which is
	    * equivalent to using a unicode codepoint collation.
	    * 
	    * @param m_collation_declared				   Boolean value true, if xsl:sort instruction has 
	    *                                              specified a 'collation' attribute.
	    * @param m_collation                           An optional collation URI specified on xsl:sort 
	    *                                              instruction, used to compare two sort key values.
	    * @param m_langStr                             Value of xsl:sort instruction's optional 'lang' 
	    *                                              attribute.
	    * @param sortKeyObj                            One of the sort key value
	    * @param sortKeyObj2                           The second sort key value
	    * @return                                      Boolean value true, if the two supplied sort key
	    *                                              values are equal, otherwise false.
	    * @throws TransformerException
	    */
	   private boolean sortKeyEqual(boolean m_collation_declared, String m_collation, String m_langStr,
			                        XObject sortKeyObj, XObject sortKeyObj2) throws TransformerException {

		   boolean result = false;

		   if (m_collation_declared && sortKeyObj.vcEquals(sortKeyObj2, null, m_collation, true)) {							  
			   result = true;
		   }
		   else if (m_langStr != null) {
			   String str1 = XslTransformEvaluationHelper.getStrVal(sortKeyObj);
			   String str2 = XslTransformEvaluationHelper.getStrVal(sortKeyObj2);

			   Locale locale = new Locale(m_langStr);
			   Collator collator = Collator.getInstance(locale);
			   collator.setStrength(Collator.TERTIARY);

			   int result2 = collator.compare(str1, str2);
			   if (result2 == 0) {
				   result = true;
			   }
		   }
		   else if (sortKeyObj.vcEquals(sortKeyObj2, null, m_collation, true)) {
			   result = true;;
		   }

		   return result;
	   }

	   public XObject getInputItem() {
		   return m_inputItem;
	   }

	   public void setInputItem(XObject inputItem) {
		   this.m_inputItem = inputItem;
	   }

	   public List<SortKey> getSortKeyList() {
		   return this.m_sortKeyList;
	   }

	   public void setSortKeyList(List<SortKey> sortKeyList) {
		   this.m_sortKeyList = sortKeyList;  
	   }	   
   }
   
}
