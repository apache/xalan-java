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

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xml.utils.QName;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.composite.SequenceTypeSupport;
import org.apache.xpath.objects.XNodeSetForDOM;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XRTreeFrag;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Implementation of the XSLT 3.0 xsl:next-iteration instruction.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class ElemIterateNextIteration extends ElemTemplateElement implements ExpressionOwner
{
    
	private static final long serialVersionUID = -582877657433106548L;

	public static List<XslIterateParamWithparamData> fWithparamList = new ArrayList<XslIterateParamWithparamData>();

	/**
	 * Construct an element representing xsl:next-iteration.
	 */
	public ElemIterateNextIteration() {}

	/**
	 * The "select" expression.
	 */
	protected Expression m_selectExpression = null;

	public void setSelect(XPath xpath)
	{
		m_selectExpression = xpath.getExpression();   
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
	 * @see ExpressionOwner#setExpression(Expression)
	 */
	public void setExpression(Expression exp)
	{
		exp.exprSetParent(this);
		m_selectExpression = exp;
	}

	/**
	 * @see ExpressionOwner#getExpression()
	 */
	public Expression getExpression()
	{
		return m_selectExpression;
	}

	/**
	 * This function is called after everything else has been recomposed, 
	 * and allows the template to set remaining values that may be based 
	 * on some other property that depends on recomposition.
	 *
	 * @throws TransformerException
	 */
	public void compose(StylesheetRoot sroot) throws TransformerException {
		super.compose(sroot);

		java.util.Vector vnames = sroot.getComposeState().getVariableNames();

		if (m_selectExpression != null) {
			m_selectExpression.fixupVariables(vnames, sroot.getComposeState().getGlobalsSize());
		}
		else {
			m_selectExpression = getStylesheetRoot().m_selectDefault.getExpression();
		}
	}

	/**
	 * This after the template's children have been composed.
	 */
	public void endCompose(StylesheetRoot sroot) throws TransformerException
	{    
		super.endCompose(sroot);
	}

	/**
	 * Get an int constant identifying the type of element.
	 * @see org.apache.xalan.templates.Constants
	 *
	 * @return The token ID for this element
	 */
	public int getXSLToken()
	{
		return Constants.ELEMNAME_ITERATE_NEXTITERATION;
	}

	/**
	 * Return the node name.
	 *
	 * @return The element's name
	 */
	public String getNodeName()
	{
		return Constants.ELEMNAME_ITERATE_NEXTITERATION_STRING;
	}

	/**
	 * Execute the xsl:next-iteration transformation.
	 *
	 * @param transformer non-null reference to the the current transform-time state.
	 *
	 * @throws TransformerException
	 */
	public void execute(TransformerImpl transformer) throws TransformerException
	{

		XPathContext xpathContext = transformer.getXPathContext();
		
		SourceLocator srcLocator = xpathContext.getSAXLocator(); 

		if (!isXslNextIterationDescendantOfXslIterate(this)) {
			throw new TransformerException("XTSE0010 : An xsl:next-iteration instruction doesn't have xsl:iterate instruction as an ancestor.", srcLocator); 
		}

		elemIterateNextIterationProcessing(transformer);            
	}

	/**
	 * Method definition, to process xsl:iterate instruction's xsl:next-iteration element. 
	 * 
	 * @param transformer              non-null reference to the the current transform-time state.
	 *
	 * @throws TransformerException    Thrown in a variety of circumstances.
	 * 
	 * @xsl.usage advanced
	 */
	private void elemIterateNextIterationProcessing(TransformerImpl transformer) throws TransformerException {

		XPathContext xctxt = transformer.getXPathContext();

		SourceLocator srcLocator = xctxt.getSAXLocator();

		int contextNode = xctxt.getContextNode();

		// Clear the, xsl:next-iteration->xsl:with-param* list storage before
		// evaluating this xsl:next-iteration element.
		fWithparamList.clear();

		NodeList nextIterationChildNodes = this.getChildNodes();                        

		for (int idx = 0; idx < nextIterationChildNodes.getLength(); idx++) {
			Node nextIterChild = nextIterationChildNodes.item(idx);
			if (nextIterChild instanceof ElemWithParam) {
				ElemWithParam withParamElem = (ElemWithParam)nextIterChild;
				QName withParamNameVal = withParamElem.getName();
				XPath withParamSelectXPath = withParamElem.getSelect();                              
				XslIterateParamWithparamData paramWithparamDataObj = new XslIterateParamWithparamData();
				paramWithparamDataObj.setName(withParamNameVal);
				paramWithparamDataObj.setSelect(withParamSelectXPath);
				if (fWithparamList.contains(paramWithparamDataObj)) {
					throw new TransformerException("XTSE0670 : Duplicate xsl:with-param parameter name '" + withParamNameVal + "'", srcLocator);   
				}
				else {
					fWithparamList.add(paramWithparamDataObj);  
				}
			}
		}

		Map<QName, XObject> xpathVarMap = xctxt.getXPathVarMap();

		int paramCount = (ElemIterate.fXslIterateParamWithparamDataList).size();
		int withParamCount = fWithparamList.size();
	      
		if (paramCount != withParamCount) {			
			for (int idx2 = 0; idx2 < withParamCount; idx2++) {
				XslIterateParamWithparamData withParamData = fWithparamList.get(idx2);
				QName withParamQName = withParamData.getName();
				boolean paramExists = false;
				for (int idx = 0; idx < paramCount; idx++) {
					XslIterateParamWithparamData paramData = (ElemIterate.fXslIterateParamWithparamDataList).get(idx);
					QName paramQName = paramData.getName();
					if (withParamQName.equals(paramQName)) {
						paramExists = true;
					}
				}

				if (!paramExists) {
					throw new TransformerException("XTSE3130 : An xsl:iterate's parameter '" + withParamQName.toString() + "' doesn't exist "
							                                                                 + "corresponding to xsl:next-iteration's xsl:with-param element.", srcLocator); 
				}
			}

			for (int idx = 0; idx < paramCount; idx++) {
				XslIterateParamWithparamData paramData = (ElemIterate.fXslIterateParamWithparamDataList).get(idx);
				QName paramQName = paramData.getName();
				boolean paramExists = false;
				XslIterateParamWithparamData withParamData = null;
				for (int idx2 = 0; idx2 < withParamCount; idx2++) {
					withParamData = fWithparamList.get(idx2);
					QName withParamQName = withParamData.getName();
					if (withParamQName.equals(paramQName)) {
						paramExists = true;

						break;
					}
				}

				if (paramExists) {
					QName xslParamQName = withParamData.getName();

					ElemWithParam elemWithParam = getElemWithParam(xslParamQName);

					XPath withParamSelectVal = withParamData.getSelect();
					XObject withParamVal = null;
					if (withParamSelectVal != null) {
						// Evaluate xsl:with-param's value, from its "select" attribute
						withParamVal = withParamSelectVal.execute(xctxt, contextNode, this);
					}
					else {
						// Evaluate xsl:with-param's value, from the sequence constructor
						// provided as content within xsl:with-param element.                   
						int rootNodeHandleOfRtf = transformer.transformToRTF(elemWithParam);
						NodeList nodeList = (new XRTreeFrag(rootNodeHandleOfRtf, xctxt, 
								elemWithParam)).convertToNodeset();                
						withParamVal = new XNodeSetForDOM(nodeList, xctxt);
					}

					String withParamAsAttr = elemWithParam.getAs();
					if (withParamAsAttr != null) {
						try {
							withParamVal = SequenceTypeSupport.castXdmValueToAnotherType(withParamVal, withParamAsAttr, null, xctxt);
						}
						catch (TransformerException ex) {
							if (withParamSelectVal != null) {
								throw ex;  
							}
							else {
								throw new TransformerException("XTTE0570 : The value of sequence constructor, supplied within xsl:with-param element cannot "
										                                                                                    + "be cast to the type " + withParamAsAttr, srcLocator);  
							}
						}
					}

					xpathVarMap.put(xslParamQName, withParamVal);
				}
			}
		}
		else {			
			for (int idx = 0; idx < paramCount; idx ++) {
				XslIterateParamWithparamData paramData = (ElemIterate.fXslIterateParamWithparamDataList).get(idx);
				QName paramQname = paramData.getName();
				boolean xslMatchingWithParamDeclared = false;
				for (int idx2 = 0; idx2 < withParamCount; idx2++) {
					XslIterateParamWithparamData withParamData = fWithparamList.get(idx2);                                                
					QName xslWithParamQName = withParamData.getName();
					if (xslWithParamQName.equals(paramQname)) {
						xslMatchingWithParamDeclared = true;
						XObject withParamVal = null;
						
						ElemWithParam elemWithParam = getElemWithParam(xslWithParamQName);

						XPath withParamSelectVal = withParamData.getSelect();                
						if (withParamSelectVal != null) {
							// Evaluate xsl:with-param's value, from its "select" attribute
							withParamVal = withParamSelectVal.execute(xctxt, contextNode, this);
						}
						else {
							// Evaluate xsl:with-param's value, from the sequence constructor
							// provided as content within xsl:with-param element.                   
							int rootNodeHandleOfRtf = transformer.transformToRTF(elemWithParam);
							NodeList nodeList = (new XRTreeFrag(rootNodeHandleOfRtf, xctxt, 
									elemWithParam)).convertToNodeset();                
							withParamVal = new XNodeSetForDOM(nodeList, xctxt);
						}

						String withParamAsAttr = elemWithParam.getAs();
						if (withParamAsAttr != null) {
							try {
								withParamVal = SequenceTypeSupport.castXdmValueToAnotherType(withParamVal, withParamAsAttr, null, xctxt);
							}
							catch (TransformerException ex) {
								if (withParamSelectVal != null) {
									throw ex;  
								}
								else {
									throw new TransformerException("XTTE0570 : The value of sequence constructor, supplied within xsl:with-param element cannot "
											                                                                                    + "be cast to the type " + withParamAsAttr, srcLocator);  
								}
							}
						}

						xpathVarMap.put(xslWithParamQName, withParamVal);
						
						break;
					}
				}
				
				if (!xslMatchingWithParamDeclared) {
					throw new TransformerException("XTSE3130 : xsl:iterate parameter " + paramQname.toString() + " is not declared "
							                                                           + "within the containing xsl:iterate instruction.", srcLocator);
				}
			}
		}
	}

	/**
	 * Method definition, to get xsl:next-iteration instruction's xsl:with-param 
	 * element reference for the supplied xsl:iterate parameter name.
	 * 
	 * @param qName							    Supplied xsl:iterate instruction
	 *                                          parameter name.
	 * @return                                  An xsl:with-param element reference 
	 */
	private ElemWithParam getElemWithParam(QName qName) {
		
		ElemWithParam elemWithParam = null;

		ElemTemplateElement elemTemplateElement = getFirstChildElem();

		while (elemTemplateElement != null) {
			ElemWithParam elemWithParamTemp = (ElemWithParam)elemTemplateElement;
			if ((elemWithParamTemp.getName()).equals(qName)) {
				elemWithParam = elemWithParamTemp;
				break;
			}
			else {
				elemTemplateElement = elemTemplateElement.getNextSiblingElem(); 
			}
		}

		return elemWithParam;
	}

	/**
	 * Method definition, to determine whether an xsl:next-iteration instruction has 
	 * xsl:iterate instruction as ancestor.
	 * 
	 * @param xslNextInstr                     An xsl:next-iteration instruction element reference
	 * @return                                 Boolean value true, if the supplied xsl:next-iteration 
	 *                                         instruction has xsl:iterate instruction as ancestor, 
	 *                                         or false. 
	 */
	private boolean isXslNextIterationDescendantOfXslIterate(ElemIterateNextIteration xslNextInstr) {
		
		boolean result = false;

		ElemTemplateElement xslParentElement = xslNextInstr.m_parentNode;

		while (xslParentElement != null) {
			if (xslParentElement instanceof ElemIterate) {
				result = true;
				break;
			}
			else {
				xslParentElement = xslParentElement.m_parentNode;    
			}
		}

		return  result;   
	}
      
}
