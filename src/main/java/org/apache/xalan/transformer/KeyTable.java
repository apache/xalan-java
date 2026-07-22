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
package org.apache.xalan.transformer;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.xml.transform.TransformerException;

import org.apache.xalan.templates.Constants;
import org.apache.xalan.templates.KeyDeclaration;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xml.utils.QName;
import org.apache.xml.utils.WrappedRuntimeException;
import org.apache.xml.utils.XMLString;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.WrongNumberArgsException;
import org.apache.xpath.functions.datetime.FuncAdjustDateTimeToTimezone;
import org.apache.xpath.functions.datetime.FuncAdjustDateToTimezone;
import org.apache.xpath.functions.datetime.FuncAdjustTimeToTimezone;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;

import xml.xpath31.processor.types.XSDate;
import xml.xpath31.processor.types.XSDateTime;
import xml.xpath31.processor.types.XSNumericType;
import xml.xpath31.processor.types.XSTime;

/**
 * Table for XML element keys, keyed by document node. An 
 * object instance of this class is keyed by a Document node 
 * that should be matched with the root of the current context.
 * 
 * @xsl.usage advanced
 */
public class KeyTable
{
  /**
   * The document key.  This table should only be used with contexts
   * whose Document roots match this key.
   */
  private int m_docKey;

  /**
   * Vector of KeyDeclaration instances holding the key declarations.
   */
  private Vector m_keyDeclarations;

  /**
   * Hold a cache of key() function result for each ref.
   * Key is XMLString, the ref value
   * Value is XNodeSet, the key() function result for the given ref value.
   */
  private Map<XMLString, XMLNodeCursorImpl> m_refsTable = null;

  /**
   * Get the document root matching this key.  
   *
   * @return the document root matching this key
   */
  public int getDocKey()
  {
    return m_docKey;
  }

  /** 
   * The main iterator that will walk through the source  
   * tree for this key.
   */
  private XMLNodeCursorImpl m_keyNodes;
  
  KeyIterator getKeyIterator()
  {
  	return (KeyIterator)(m_keyNodes.getContainedIter());
  }

  /**
   * Build a keys table.
   * @param doc The owner document key.
   * @param nscontext The stylesheet's namespace context.
   * @param name The key name
   * @param keyDeclarations The stylesheet's xsl:key declarations.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public KeyTable(
          int doc, PrefixResolver nscontext, QName name, Vector keyDeclarations, XPathContext xctxt)
            throws javax.xml.transform.TransformerException
  {
    m_docKey = doc;
    m_keyDeclarations = keyDeclarations;
    KeyIterator ki = new KeyIterator(name, keyDeclarations);

    m_keyNodes = new XMLNodeCursorImpl(ki);
    m_keyNodes.allowDetachToRelease(false);
    m_keyNodes.setRoot(doc, xctxt);
  }

  /**
   * Given a valid element key, return the corresponding node list.
   * 
   * @param name The name of the key, which must match the 'name' attribute on xsl:key.
   * @param ref The value that must match the value found by the 'match' attribute on xsl:key.
   * @return a set of nodes referenced by the key named <CODE>name</CODE> and the reference <CODE>ref</CODE>. If no node is referenced by this key, an empty node set is returned.
   */
  public XMLNodeCursorImpl getNodeSetDTMByKey(QName name, XMLString ref)

  {
	  XMLNodeCursorImpl refNodes = (XMLNodeCursorImpl)((getRefsTable()).get(ref));
	  
	  // Clone with reset the node set	  
	  try
	  {
		  if (refNodes != null)
		  {
			  refNodes = (XMLNodeCursorImpl) refNodes.cloneWithReset();
		  }
	  }
	  catch (CloneNotSupportedException e)
	  {
		  refNodes = null;
	  }

	  if (refNodes == null) {
		  //  Create an empty XNodeSet		  
		  KeyIterator ki = (KeyIterator) (m_keyNodes).getContainedIter();
		  XPathContext xctxt = ki.getXPathContext();
		  
		  refNodes = new XMLNodeCursorImpl(xctxt.getDTMManager()) {
			  public void setRoot(int nodeHandle, Object environment) {
				  // Root cannot be set on non-iterated node sets. Ignore it.
			  }
		  };
		  
		  refNodes.reset();
	  }

	  return refNodes;
  }

  /**
   * Get Key Name for this KeyTable  
   *
   * @return Key name
   */
  public QName getKeyTableName()
  {
    return getKeyIterator().getName();
  }

  /**
   * @return key declarations for the key associated to this KeyTable
   */
  private Vector getKeyDeclarations() {
    int nDeclarations = m_keyDeclarations.size();
    Vector keyDecls = new Vector(nDeclarations);

    // Walk through each of the declarations made with xsl:key
    for (int i = 0; i < nDeclarations; i++)
    {
      KeyDeclaration kd = (KeyDeclaration) m_keyDeclarations.elementAt(i);

      // Add the declaration if the name on this key declaration
      // matches the name on the iterator for this walker.
      if (kd.getName().equals(getKeyTableName())) {
        keyDecls.add(kd);
      }
    }

    return keyDecls;
  }

  /** 
   * @return lazy initialized refs table associating evaluation of key function
   *         with a XNodeSet
   */
  private Map<XMLString, XMLNodeCursorImpl> getRefsTable()
  {
	  if (m_refsTable == null) {
		  m_refsTable = new HashMap<XMLString, XMLNodeCursorImpl>();

		  KeyIterator keyIter = (KeyIterator) (m_keyNodes).getContainedIter();
		  XPathContext xctxt = keyIter.getXPathContext();

		  Vector keyDecls = getKeyDeclarations();
		  int nKeyDecls = keyDecls.size();

		  int nextNode = DTM.NULL;		  
		  m_keyNodes.reset();
		  
		  while (DTM.NULL != (nextNode = m_keyNodes.nextNode()))
		  {
			  try
			  {
				  for (int keyDeclIdx = 0; keyDeclIdx < nKeyDecls; keyDeclIdx++) {
					  KeyDeclaration keyDeclaration = (KeyDeclaration) keyDecls.elementAt(keyDeclIdx);					  					  
					  
					  XObject xuse = null;					  
					  XPath xpathKeyUse = keyDeclaration.getUse();
					  if (xpathKeyUse != null) {
						  xuse = xpathKeyUse.execute(xctxt, nextNode, keyIter.getPrefixResolver());  
					  }
					  else if (keyDeclaration.getFirstChildElem() == null) {
						  xuse = XString.EMPTYSTRING;
					  }
					  else {
						  xctxt.pushCurrentNode(nextNode);
						  
						  try {
							  TransformerImpl transformer = (TransformerImpl)(xctxt.getOwnerObject());
							  
							  String str1 = transformer.transformToString(keyDeclaration); 
							  
							  xuse = new XString(str1);
						  }
						  finally {
							  xctxt.popCurrentNode();  
						  }
					  }
					  
					  XNumber xNum = null;
					  if ((xuse instanceof XSNumericType) || (xuse instanceof XNumber)) {
						 String str1 = XslTransformEvaluationHelper.getStrVal(xuse);						 
						 xNum = new XNumber(Double.valueOf(str1)); 
					  }					  

					  boolean isXslKeyComosite = keyDeclaration.getComposite();

					  if (xuse.getType() != xuse.CLASS_NODESET) {
						  XMLString exprResult = null;

						  if (isXslKeyComosite && (xuse.getType() == xuse.CLASS_RESULT_SEQUENCE)) {
							  ResultSequence rSeq = (ResultSequence)xuse;
							  StringBuffer strBuff = new StringBuffer();
							  int size1 = rSeq.size();
							  for (int idx = 0; idx < size1; idx++) {
								  XObject xObj = rSeq.item(idx);
								  String str1 = XslTransformEvaluationHelper.getStrVal(xObj);
								  if (idx < (size1 + 1)) {
									  strBuff.append(str1 + ","); 
								  }
								  else {
									  strBuff.append(str1);
								  }
							  }

							  String str1 = strBuff.toString();            	  
							  exprResult = new XString(str1);
						  }
						  else {
							  boolean isXsDateTimeType = false;              
							  if ((xuse instanceof XSDateTime) || (xuse instanceof XSDate) || (xuse instanceof XSTime)) {
								  isXsDateTimeType = true; 
							  }

							  if (!isXsDateTimeType) {
								  String str1 = XslTransformEvaluationHelper.getStrVal(xuse);
								  exprResult = new XString(str1); 
							  }              
							  else if (xuse instanceof XSDateTime) {
								  XSDateTime xsDateTime = (XSDateTime)xuse;

								  FuncAdjustDateTimeToTimezone funcAdjustDateTimeToTimezone = new FuncAdjustDateTimeToTimezone();
								  funcAdjustDateTimeToTimezone.setArg0(xsDateTime);
								  XPath xpathObj = new XPath(Constants.XS_DAYTIME_DURATION_UTC, null, xctxt.getNamespaceContext(), XPath.SELECT, null);
								  XObject xObjTz = xpathObj.execute(xctxt, DTM.NULL, xctxt.getNamespaceContext());
								  try {
									  funcAdjustDateTimeToTimezone.setArg(xObjTz, 1);
								  } 
								  catch (WrongNumberArgsException ex) {
									  // No op
								  }

								  xuse = funcAdjustDateTimeToTimezone.execute(xctxt);
								  String str1 = XslTransformEvaluationHelper.getStrVal(xuse);
								  exprResult = new XString(str1);
							  }
							  else if (xuse instanceof XSDate) {
								  XSDate xsDate = (XSDate)xuse;

								  FuncAdjustDateToTimezone funcAdjustDateToTimezone = new FuncAdjustDateToTimezone();
								  funcAdjustDateToTimezone.setArg0(xsDate);
								  XPath xpathObj = new XPath(Constants.XS_DAYTIME_DURATION_UTC, null, xctxt.getNamespaceContext(), XPath.SELECT, null);
								  XObject xObjTz = xpathObj.execute(xctxt, DTM.NULL, xctxt.getNamespaceContext());
								  try {
									  funcAdjustDateToTimezone.setArg(xObjTz, 1);
								  } 
								  catch (WrongNumberArgsException ex) {
									  // No op
								  }

								  xuse = funcAdjustDateToTimezone.execute(xctxt);
								  String str1 = XslTransformEvaluationHelper.getStrVal(xuse);
								  exprResult = new XString(str1);
							  }
							  else if (xuse instanceof XSTime) {
								  XSTime xsTime = (XSTime)xuse;

								  FuncAdjustTimeToTimezone funcAdjustTimeToTimezone = new FuncAdjustTimeToTimezone();
								  funcAdjustTimeToTimezone.setArg0(xsTime);
								  XPath xpathObj = new XPath(Constants.XS_DAYTIME_DURATION_UTC, null, xctxt.getNamespaceContext(), XPath.SELECT, null);
								  XObject xObjTz = xpathObj.execute(xctxt, DTM.NULL, xctxt.getNamespaceContext());
								  try {
									  funcAdjustTimeToTimezone.setArg(xObjTz, 1);
								  } 
								  catch (WrongNumberArgsException ex) {
									  // No op
								  }

								  xuse = funcAdjustTimeToTimezone.execute(xctxt);
								  String str1 = XslTransformEvaluationHelper.getStrVal(xuse);
								  exprResult = new XString(str1);
							  }              
						  }
						  
						  if (xNum != null) {
							 ((XString)exprResult).setNumber(xNum); 
						  }

						  addValueInRefsTable(xctxt, exprResult, nextNode);
					  } 
					  else {
						  DTMCursorIterator iter1 = ((XMLNodeCursorImpl)xuse).iterRaw();
						  int currentNodeInUseClause;

						  while (DTM.NULL != (currentNodeInUseClause = iter1.nextNode())) {
							  DTM dtm = xctxt.getDTM(currentNodeInUseClause);
							  XMLString exprResult = dtm.getStringValue(currentNodeInUseClause);
							  
							  if (xNum != null) {
								 ((XString)exprResult).setNumber(xNum); 
							  }
							  
							  addValueInRefsTable(xctxt, exprResult, nextNode);
						  }
					  }
				  }
			  } 
			  catch (TransformerException te) {
				  throw new WrappedRuntimeException(te);
			  }
		  }
	  }

	  return m_refsTable;
  }

  /**
   * Add an association between a ref and a node in the m_refsTable.
   * Requires that m_refsTable != null
   * @param xctxt XPath context
   * @param ref the value of the use clause of the current key for the given node
   * @param node the node to reference
   */
  private void addValueInRefsTable(XPathContext xctxt, XMLString ref, int node) {
    
	  XMLNodeCursorImpl nodes = (XMLNodeCursorImpl)(m_refsTable.get(ref));
	  
	  if (nodes == null)
	  {
		  nodes = new XMLNodeCursorImpl(node, xctxt.getDTMManager());
		  nodes.nextNode();
		  m_refsTable.put(ref, nodes);
	  }
	  else
	  {
		  // Nodes are passed to this method in document order.  Since we need to
		  // suppress duplicates, we only need to check against the last entry
		  // in each nodeset.  We use nodes.nextNode after each entry so we can
		  // easily compare node against the current node.
		  if (nodes.getCurrentNode() != node) {
			  nodes.mutableNodeset().addNode(node);
			  nodes.nextNode();
		  }    
	  }
  }
}
