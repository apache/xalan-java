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
 * operator 'intersect'. 
 * 
 * This class, evaluates XPath operator 'intersect', by using 
 * XPath expression string values for operator 'intersect' first 
 * and second operands.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XPath3Intersect extends Expression
{
  
  private static final long serialVersionUID = 8981837059372769069L;

  /**
   * Class field, representing XPath expression string,
   * which is XPath operator 'intersect' first operand.
   */
  private java.lang.String m_lstr = null;
  
  /**
   * Class field, representing XPath expression string,
   * which is XPath operator 'intersect' second operand.
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
  public XPath3Intersect() {
	 // no op  
  }
  
  /**
   * Class constructor.
   */
  public XPath3Intersect(java.lang.String lStr, java.lang.String rStr) {
	  m_lstr = lStr;
	  m_rstr = rStr;
  }

  /**
   * Method definition, to get XPath operator 'intersect' 
   * evaluation result.
   *  
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException 
  {
	  XObject result = null;
	  
	  final int sourceNode = xctxt.getCurrentNode();

	  SourceLocator srcLocator = xctxt.getSAXLocator();	  	  
	  
	  XPath lxpath = new XPath(m_lstr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
	  
	  if (m_vars != null) {
		  lxpath.fixupVariables(m_vars, m_globals_size);
	  }		  

	  XObject lxObj = lxpath.execute(xctxt, sourceNode, xctxt.getNamespaceContext());
	  
	  List<Integer> nodeHandleLstFirst = new ArrayList<Integer>(); 
	  
	  if (lxObj instanceof XMLNodeCursorImpl) {
		 XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)lxObj;
		 DTMCursorIterator dtmCursorIterator = xmlNodeCursorImpl.iter();
		 
		 int nextNode = DTM.NULL;
		 while ((nextNode = dtmCursorIterator.nextNode()) != DTM.NULL) {
			nodeHandleLstFirst.add(nextNode); 
		 }		 
	  }
	  
      XPath rxpath = new XPath(m_rstr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
	  
	  if (m_vars != null) {
		  rxpath.fixupVariables(m_vars, m_globals_size);
	  }		  

	  XObject rxObj = rxpath.execute(xctxt, sourceNode, xctxt.getNamespaceContext());
	  
	  List<Integer> nodeHandleLstSecond = new ArrayList<Integer>();
	  
	  if (rxObj instanceof XMLNodeCursorImpl) {
		 XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)rxObj;
		 DTMCursorIterator dtmCursorIterator = xmlNodeCursorImpl.iter();
		 
		 int nextNode = DTM.NULL;
		 while ((nextNode = dtmCursorIterator.nextNode()) != DTM.NULL) {
			 nodeHandleLstSecond.add(nextNode); 
		 }
	  }
	  	  	  
	  /**
	   * An XPath 3.1 operator 'intersect' evaluation result,
	   * doesn't contain duplicate nodes and the result nodes are 
	   * sorted in XML document order, as specified by XPath 3.1 
	   * spec.
	   */
	  
	  // A java.util.List object, that shall contain list of
	  // XML node handles for result of XPath 3.1 operator 'intersect'.
	  
	  List<Integer> nodeHandleResultLst = new ArrayList<Integer>();
	  
	  int size1 = nodeHandleLstFirst.size();
	  for (int idx = 0; idx < size1; idx++) {
		 int nodeHandle1 = nodeHandleLstFirst.get(idx);
		 int size2 = nodeHandleLstSecond.size();
		 for (int idx2 = 0; idx2 < size2; idx2++) {
			int nodeHandle2 = nodeHandleLstSecond.get(idx2);
			if ((nodeHandle1 == nodeHandle2) && !nodeHandleResultLst.contains(nodeHandle1)) {
				nodeHandleResultLst.add(nodeHandle1);
			}
		 }
	  }
	  
	  int size2 = nodeHandleLstSecond.size();
	  for (int idx2 = 0; idx2 < size2; idx2++) {
		 int nodeHandle2 = nodeHandleLstSecond.get(idx2);
		 size1 = nodeHandleLstFirst.size();
		 for (int idx = 0; idx < size1; idx++) {
			int nodeHandle1 = nodeHandleLstFirst.get(idx);
			if ((nodeHandle1 == nodeHandle2) && !nodeHandleResultLst.contains(nodeHandle1)) {
				nodeHandleResultLst.add(nodeHandle1);
			}
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
