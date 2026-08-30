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
package org.apache.xpath.operations;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.xml.transform.SourceLocator;

import org.apache.xalan.templates.XMLNSDecl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XObject;

/**
 * Class definition, supporting implementation for XPath 3.1 
 * operator 'except'. 
 * 
 * This class, evaluates XPath operator 'except', by using 
 * XPath expression string values for operator 'except' first 
 * and second operands.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XPathExcept extends Expression
{

  private static final long serialVersionUID = 878464622772924593L;

  /**
   * Class field, representing XPath expression string,
   * which is XPath operator 'except' first operand.
   */
  private java.lang.String m_lstr = null;
  
  /**
   * Class field, representing XPath expression string,
   * which is XPath operator 'except' second operand.
   */
  private java.lang.String m_rstr = null;
  
  // Class field, used to resolve variable references 
  // within an XPath expression.
  private Vector m_vars;
  
  // Class field, used to resolve variable references 
  // within an XPath expression.
  private int m_globals_size;
  
  /**
   * Default constructor.
   */
  public XPathExcept() {
	 // no op  
  }
  
  /**
   * Class constructor.
   */
  public XPathExcept(java.lang.String lStr, java.lang.String rStr) {
	  m_lstr = lStr;
	  m_rstr = rStr;
  }

  /**
   * Method definition, to get XPath operator 'except' 
   * evaluation result.
   *  
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException 
  {
	  XObject result = null;
	  
	  final int sourceNode = xctxt.getCurrentNode();

	  SourceLocator srcLocator = xctxt.getSAXLocator();
	  
      m_lstr = normalizeStrBoundaryParens(m_lstr.trim());
	  
	  m_rstr = normalizeStrBoundaryParens(m_rstr.trim());
	  
	  List<XMLNSDecl> prefixTable = XslTransformEvaluationHelper.getXSLNsPrefixTable(xctxt);
	  
	  if (prefixTable != null) {
    	  m_lstr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(m_lstr, prefixTable); 

    	  m_rstr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(m_rstr, prefixTable);
      }
	  
	  XPath xpath1 = new XPath(m_lstr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
	  
	  if (m_vars != null) {
		  xpath1.fixupVariables(m_vars, m_globals_size);
	  }		  

	  XObject xObj1 = xpath1.execute(xctxt, sourceNode, xctxt.getNamespaceContext());
	  
	  List<Integer> nodeHandleLstFirst = new ArrayList<Integer>(); 
	  
	  if (xObj1 instanceof XMLNodeCursorImpl) {
		 XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)xObj1;
		 DTMCursorIterator dtmCursorIterator = xmlNodeCursorImpl.iter();
		 
		 int nextNode = DTM.NULL;
		 while ((nextNode = dtmCursorIterator.nextNode()) != DTM.NULL) {
			nodeHandleLstFirst.add(nextNode); 
		 }		 
	  }	  	  
	  
      XPath xpath2 = new XPath(m_rstr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
	  
	  if (m_vars != null) {
		  xpath2.fixupVariables(m_vars, m_globals_size);
	  }		  

	  XObject xObj2 = xpath2.execute(xctxt, sourceNode, xctxt.getNamespaceContext());
	  
	  List<Integer> nodeHandleLstSecond = new ArrayList<Integer>();
	  
	  if (xObj2 instanceof XMLNodeCursorImpl) {
		 XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)xObj2;
		 DTMCursorIterator dtmCursorIterator = xmlNodeCursorImpl.iter();
		 
		 int nextNode = DTM.NULL;
		 while ((nextNode = dtmCursorIterator.nextNode()) != DTM.NULL) {
			 nodeHandleLstSecond.add(nextNode); 
		 }
	  }
	  	  	  
	  /**
	   * An XPath 3.1 operator 'except' evaluation result,
	   * doesn't contain duplicate nodes and the result nodes are 
	   * sorted in XML document order, as specified by XPath 3.1 
	   * spec.
	   */
	  
	  // A java.util.List object, that shall contain list of
	  // XML node handles for result of XPath 3.1 operator 'except'.
	  
	  List<Integer> nodeHandleResultLst = new ArrayList<Integer>();
	  
	  int size1 = nodeHandleLstFirst.size();
	  for (int idx = 0; idx < size1; idx++) {
		  int nodeHandle1 = nodeHandleLstFirst.get(idx);
		  if (!nodeHandleLstSecond.contains(nodeHandle1)) {
			  nodeHandleResultLst.add(nodeHandle1);
		  }
	  }
	  
	  nodeHandleResultLst.sort(null);
	  
	  if (nodeHandleResultLst.size() > 0) {
		 result = new XMLNodeCursorImpl(nodeHandleResultLst, xctxt);  
	  }
	  else {
		 result = new ResultSequence(); 
	  }

	  return result;
  }

  public java.lang.String getLstr() {
	  return m_lstr;
  }

  public void setLstr(java.lang.String lstr) {
	  this.m_lstr = lstr;
  }

  public java.lang.String getRstr() {
	  return m_rstr;
  }

  public void setRstr(java.lang.String rstr) {
	  this.m_rstr = rstr;
  }

  @Override
  public void callVisitors(ExpressionOwner owner, XPathVisitor visitor) {
	  // no op
  }

  @Override
  public void fixupVariables(Vector vars, int globalsSize) {
	  m_vars = (Vector)(vars.clone());
      m_globals_size = globalsSize;
  }

  @Override
  public boolean deepEquals(Expression expr) {
	  // no op
	  return false;
  }

}
