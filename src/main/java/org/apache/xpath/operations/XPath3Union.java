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
 * Class definition, providing implementation for XPath 3.1 
 * operators '|' & 'union', using XPath expression string 
 * values which are, XPath expression string values for 
 * union operator's first and second operands.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XPath3Union extends Expression
{
    
  private static final long serialVersionUID = 5559048164177617210L;
  
  /**
   * Class field, representing XPath expression string,
   * which is XPath operator 'union' first operand.
   */
  private java.lang.String m_lstr = null;
  
  /**
   * Class field, representing XPath expression string,
   * which is XPath operator 'union' second operand.
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
  public XPath3Union() {
	 // no op  
  }
  
  /**
   * Class constructor.
   */
  public XPath3Union(java.lang.String lStr, java.lang.String rStr) {
	  m_lstr = lStr;
	  m_rstr = rStr;
  }

  /**
   * Method definition, to get XPath operator '|' & 
   * 'union' evaluation result.
   *  
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException 
  {
	  XObject result = null;
	  
	  final int sourceNode = xctxt.getCurrentNode();

	  SourceLocator srcLocator = xctxt.getSAXLocator();
	  
	  boolean isSuffixFuncPattern = false;
	  
	  int idx = m_lstr.lastIndexOf("/");
	  
	  java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(".*\\(\\s*\\)");
	  
	  // A java.util.List object, that shall contain list of
	  // XML node handles for result of XPath 3.1 operator 'union'.		  
	  List<Integer> nodeHandleList = new ArrayList<Integer>();
	  
	  XMLNodeCursorImpl xmlNodeCursorImpl = null;
	  
	  if (idx > 0) {
		  java.lang.String str1 = (m_lstr.substring(0, idx)).trim();
		  java.lang.String str2 = (m_lstr.substring(idx + 1)).trim();

		  java.util.regex.Matcher matcher = pattern.matcher(str2);	  
		  if (matcher.matches()) {
			  // The supplied XPath expression string is like,
			  // /m/n/text() etc.			  
			  isSuffixFuncPattern = true;  
		  }

		  XPath lxpath = null;

		  if (isSuffixFuncPattern) {
			  lxpath = new XPath(str1, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
		  }
		  else {
			  lxpath = new XPath(m_lstr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);  
		  }

		  if (m_vars != null) {
			  lxpath.fixupVariables(m_vars, m_globals_size);
		  }		  

		  XObject lXObj = lxpath.execute(xctxt, sourceNode, xctxt.getNamespaceContext());
		  
		  if (lXObj instanceof XMLNodeCursorImpl) {
			  xmlNodeCursorImpl = (XMLNodeCursorImpl)lXObj; 
			  if (isSuffixFuncPattern) {
				  java.lang.String a1 = str2.replace(" ", "");
				  if ("text()".equals(a1)) {
					  DTMCursorIterator dtmCursorIterator = xmlNodeCursorImpl.iter(); 
					  int nextNode = DTM.NULL;
					  while ((nextNode = dtmCursorIterator.nextNode()) != DTM.NULL) {
						  DTM dtm = xctxt.getDTM(nextNode);
						  // This is assumed to be an xdm text node
						  int child = dtm.getFirstChild(nextNode);				 
						  nodeHandleList.add(child);
					  } 
				  }
			  }
			  else {
				  DTMCursorIterator dtmCursorIterator = xmlNodeCursorImpl.iter(); 
				  int nextNode = DTM.NULL;
				  while ((nextNode = dtmCursorIterator.nextNode()) != DTM.NULL) {
					  nodeHandleList.add(nextNode); 
				  }
			  }
	      }
		  
		  isSuffixFuncPattern = false;

		  int idx2 = m_rstr.lastIndexOf("/");
		  java.lang.String str3 = null;
		  java.lang.String str4 = null;
		  if (idx2 != -1) {
			  str3 = (m_rstr.substring(0, idx2)).trim();
			  str4 = (m_rstr.substring(idx2 + 1)).trim();

			  matcher = pattern.matcher(str4);	  
			  if (matcher.matches()) {
				 // The supplied XPath expression string is like,
				 // /m/n/text() etc.
				 isSuffixFuncPattern = true;  
			  }
		  }

		  XPath rxpath = null;

		  if (isSuffixFuncPattern) {
			  rxpath = new XPath(str3, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
		  }
		  else {
			  rxpath = new XPath(m_rstr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);  
		  }

		  if (m_vars != null) {
			  rxpath.fixupVariables(m_vars, m_globals_size);
		  }

		  XObject rXObj = rxpath.execute(xctxt, sourceNode, xctxt.getNamespaceContext());
		  
		  if (rXObj instanceof XMLNodeCursorImpl) {
			  xmlNodeCursorImpl = (XMLNodeCursorImpl)rXObj;
			  if (isSuffixFuncPattern) {
				  java.lang.String a1 = str4.replace(" ", "");
				  if ("text()".equals(a1)) {
					  DTMCursorIterator dtmCursorIterator = xmlNodeCursorImpl.iter(); 
					  int nextNode = DTM.NULL;
					  while ((nextNode = dtmCursorIterator.nextNode()) != DTM.NULL) {
						  DTM dtm = xctxt.getDTM(nextNode);
						  // This is assumed to be an xdm text node
						  int child = dtm.getFirstChild(nextNode);				 
						  nodeHandleList.add(child);
					  } 
				  }
			  }
			  else {
				  DTMCursorIterator dtmCursorIterator = xmlNodeCursorImpl.iter(); 
				  int nextNode = DTM.NULL;
				  while ((nextNode = dtmCursorIterator.nextNode()) != DTM.NULL) {
					  nodeHandleList.add(nextNode); 
				  }
			  }
		  }
	  }
	  else if (idx == 0) {
		  /**
		   * There's a character '/' (there's, only one character '/') 
		   * at, beginning of an XPath expression string.
		   */
		  		  
		  m_lstr = m_lstr.replaceAll("\\s*", "");		  
		  XPath lxpath = new XPath(m_lstr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
		  XObject lXObj = lxpath.execute(xctxt, sourceNode, xctxt.getNamespaceContext());
		  
		  DTMCursorIterator dtmCursorIterator = null;
		  int nextNode = DTM.NULL;
		  
		  if (lXObj instanceof XMLNodeCursorImpl) {
			  xmlNodeCursorImpl = (XMLNodeCursorImpl)lXObj;
			  dtmCursorIterator = xmlNodeCursorImpl.iter(); 
			  while ((nextNode = dtmCursorIterator.nextNode()) != DTM.NULL) {
				  nodeHandleList.add(nextNode); 
			  }
	      }
		  
		  int idx2 = m_rstr.lastIndexOf("/");
		  if (idx2 > 0) {
			  java.lang.String str1 = (m_rstr.substring(0, idx)).trim();
			  java.lang.String str2 = (m_rstr.substring(idx + 1)).trim();

			  java.util.regex.Matcher matcher = pattern.matcher(str2);	  
			  if (matcher.matches()) {
				  // The supplied XPath expression string is like,
				  // /m/n/text() etc.			  
				  isSuffixFuncPattern = true;  
			  }

			  XPath rxpath = null;

			  if (isSuffixFuncPattern) {
				  rxpath = new XPath(str1, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
			  }
			  else {
				  rxpath = new XPath(m_rstr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);  
			  }

			  if (m_vars != null) {
				  rxpath.fixupVariables(m_vars, m_globals_size);
			  }
			  
			  XObject rXObj = rxpath.execute(xctxt, sourceNode, xctxt.getNamespaceContext());
			  
			  if (rXObj instanceof XMLNodeCursorImpl) {
				  xmlNodeCursorImpl = (XMLNodeCursorImpl)rXObj; 
				  if (isSuffixFuncPattern) {
					  java.lang.String a1 = str2.replace(" ", "");
					  if ("text()".equals(a1)) {
						  dtmCursorIterator = xmlNodeCursorImpl.iter(); 
						  nextNode = DTM.NULL;
						  while ((nextNode = dtmCursorIterator.nextNode()) != DTM.NULL) {
							  DTM dtm = xctxt.getDTM(nextNode);
							  // This is assumed to be an xdm text node
							  int child = dtm.getFirstChild(nextNode);				 
							  nodeHandleList.add(child);
						  } 
					  }
				  }
				  else {
					  dtmCursorIterator = xmlNodeCursorImpl.iter(); 
					  nextNode = DTM.NULL;
					  while ((nextNode = dtmCursorIterator.nextNode()) != DTM.NULL) {
						  nodeHandleList.add(nextNode); 
					  }
				  }
		      }
		  }
		  else if (idx2 == 0) {
			  m_rstr = m_rstr.replaceAll("\\s*", "");		  
			  XPath rxpath = new XPath(m_rstr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
			  XObject rXObj = rxpath.execute(xctxt, sourceNode, xctxt.getNamespaceContext());
			  
			  if (rXObj instanceof XMLNodeCursorImpl) {
				  xmlNodeCursorImpl = (XMLNodeCursorImpl)rXObj;
				  dtmCursorIterator = xmlNodeCursorImpl.iter(); 
				  nextNode = DTM.NULL;
				  while ((nextNode = dtmCursorIterator.nextNode()) != DTM.NULL) {
					  nodeHandleList.add(nextNode); 
				  }
		      }
		  }
	  }
	  
	  /**
	   * Remove duplicates from nodeHandleList, and sort resulting 
	   * node handles in XML document order, as specified by 
	   * XPath 3.1 spec.
	   */
	  
	  List<Integer> list2 = new ArrayList<Integer>();
	  int size2 = nodeHandleList.size();
	  if (size2 > 0) {
		  for (int a1 = 0; a1 < size2; a1++) {
			  Integer x1 = nodeHandleList.get(a1);
			  if (list2.isEmpty()) {
				  list2.add(x1);
			  }
			  else if (!list2.contains(x1)) {
				  list2.add(x1);
			  }
		  }

		  list2.sort(null);

		  result = new XMLNodeCursorImpl(list2, xctxt);
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
