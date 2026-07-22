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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.xml.transform.SourceLocator;

import org.apache.xalan.transformer.KeyManager;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xml.dtm.DTMManager;
import org.apache.xml.utils.QName;
import org.apache.xml.utils.XMLString;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.axes.UnionPathIterator;
import org.apache.xpath.functions.FunctionMultiArgs;
import org.apache.xpath.functions.WrongNumberArgsException;
import org.apache.xpath.functions.datetime.FuncAdjustDateTimeToTimezone;
import org.apache.xpath.functions.datetime.FuncAdjustDateToTimezone;
import org.apache.xpath.functions.datetime.FuncAdjustTimeToTimezone;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;
import org.w3c.dom.Node;

import xml.xpath31.processor.types.XSDate;
import xml.xpath31.processor.types.XSDateTime;
import xml.xpath31.processor.types.XSDouble;
import xml.xpath31.processor.types.XSFloat;
import xml.xpath31.processor.types.XSNumericType;
import xml.xpath31.processor.types.XSTime;

/**
 * Implementation of an XSLT 3.0 function fn:key.
 * 
 * @xsl.usage advanced
 */
public class FuncKey extends FunctionMultiArgs
{
	static final long serialVersionUID = 9089293100115347340L;

	// Boolean value to be used within usedrefs 
	// java.util.Map object.	
	private static final Boolean ISTRUE = Boolean.TRUE;

	/**
	 * Class constructor.
	 */
	public FuncKey() {
		m_arity = new Short[] { 2, 3 };
	}

	/**
	 * Evaluate the function. The function must return 
	 * a valid object.
	 * 
	 * @param xctxt					    An XPath context object
	 * @return                          A valid XObject
	 *
	 * @throws javax.xml.transform.TransformerException
	 */
	public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
	{	

		TransformerImpl transformer = (TransformerImpl)(xctxt.getOwnerObject());

		SourceLocator srcLocator = xctxt.getSAXLocator();
				
		final int sourceNode = xctxt.getCurrentNode();
		
		DTM dtm = xctxt.getDTM(sourceNode);
		int docContext = dtm.getDocumentRoot(sourceNode);
		
		XMLNodeCursorImpl nodes = null;
		
		XObject xObjArg0 = getArg0().execute(xctxt);
		
		String keyNameStr = XslTransformEvaluationHelper.getStrVal(xObjArg0);
		
		QName keyQname = new QName(keyNameStr, xctxt.getNamespaceContext());
		
		ElemTemplateElement template = (ElemTemplateElement)(xctxt.getNamespaceContext());

		boolean isXslKeyComposite = false;

		Vector keyVec1 = (template.getStylesheetRoot()).getKeysComposed();
		int size1 = keyVec1.size();
		for (int idx = 0; idx < size1; idx++) {
			KeyDeclaration keyDecl = (KeyDeclaration)(keyVec1.get(idx));
			QName qName1 = keyDecl.getName();
			if (qName1.equals(keyQname)) {
				isXslKeyComposite = keyDecl.getComposite();

				break;
			}
		}
		
		XObject xObjArg1 = getArg1().execute(xctxt);
		
		if (xObjArg1 instanceof XSNumericType) {
		   XSNumericType xsNumericType = (XSNumericType)xObjArg1;
		   
		   if (xsNumericType instanceof XSDouble) {
			  if (((XSDouble)xsNumericType).nan()) {
				  xObjArg1 = new ResultSequence(); 
			  }
		   }
		   else if (xsNumericType instanceof XSFloat) {
			   if (((XSFloat)xsNumericType).nan()) {
				  xObjArg1 = new ResultSequence();
			   } 
		   }
		   
		   if (!(xObjArg1 instanceof ResultSequence)) {		   
		      String str1 = xsNumericType.stringValue();
		      
		      xObjArg1 = new XNumber(Double.valueOf(str1));
		   }
		}
		else if (xObjArg1 instanceof XSDateTime) {
		   XSDateTime xsDateTime = (XSDateTime)xObjArg1;
		   
		   FuncAdjustDateTimeToTimezone funcAdjustDateTimeToTimezone = new FuncAdjustDateTimeToTimezone();
		   funcAdjustDateTimeToTimezone.setArg0(xsDateTime);
		   XPath xpathObj = new XPath(Constants.XS_DAYTIME_DURATION_UTC, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
		   XObject xObjTz = xpathObj.execute(xctxt, DTM.NULL, xctxt.getNamespaceContext());
		   try {
			  funcAdjustDateTimeToTimezone.setArg(xObjTz, 1);
		   } 
		   catch (WrongNumberArgsException ex) {
              // No op
		   }
		   
		   xObjArg1 = funcAdjustDateTimeToTimezone.execute(xctxt);
		}
		else if (xObjArg1 instanceof XSDate) {
			XSDate xsDate = (XSDate)xObjArg1;

			FuncAdjustDateToTimezone funcAdjustDateToTimezone = new FuncAdjustDateToTimezone();
			funcAdjustDateToTimezone.setArg0(xsDate);
			XPath xpathObj = new XPath(Constants.XS_DAYTIME_DURATION_UTC, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
			XObject xObjTz = xpathObj.execute(xctxt, DTM.NULL, xctxt.getNamespaceContext());
			try {
				funcAdjustDateToTimezone.setArg(xObjTz, 1);
			} 
			catch (WrongNumberArgsException ex) {
				// No op
			}

			xObjArg1 = funcAdjustDateToTimezone.execute(xctxt);
		}
		else if (xObjArg1 instanceof XSTime) {
			XSTime xsTime = (XSTime)xObjArg1;

			FuncAdjustTimeToTimezone funcAdjustTimeToTimezone = new FuncAdjustTimeToTimezone();
			funcAdjustTimeToTimezone.setArg0(xsTime);
			XPath xpathObj = new XPath(Constants.XS_DAYTIME_DURATION_UTC, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
			XObject xObjTz = xpathObj.execute(xctxt, DTM.NULL, xctxt.getNamespaceContext());
			try {
				funcAdjustTimeToTimezone.setArg(xObjTz, 1);
			} 
			catch (WrongNumberArgsException ex) {
				// No op
			}

			xObjArg1 = funcAdjustTimeToTimezone.execute(xctxt);
		}

		XObject xObjArg2 = null;
		
		if (m_arg2 != null) {
			/**
			 * An XPath 3.1 function fn:key's third argument 
			 * if present, becomes an XML document's top most 
			 * node for XSL stylesheet key evaluation.
			 */
			
			xObjArg2 = m_arg2.execute(xctxt);
			int nodeHandle = ((XMLNodeCursorImpl)xObjArg2).asNode(xctxt);
			DTM dtm2 = xctxt.getDTM(nodeHandle);
			Node node = dtm2.getNode(nodeHandle);
			String xmlStr1 = null;
			try {
				xmlStr1 = XslTransformEvaluationHelper.serializeXmlDomElementNode(node);
				DTMManager dtmMgr1 = xctxt.getDTMManager();
				DTM dtm_a = dtmMgr1.getXmlDTMTreeFromString(xmlStr1);
				docContext = dtm_a.getDocument();
			} 
			catch (Exception ex) {
				// no op
			}
		}

		boolean argIsNodeSetDtm = (XObject.CLASS_NODESET == xObjArg1.getType());
		KeyManager kmgr = transformer.getKeyManager();

		// Don't bother with nodeset logic if the thing is only one node.
	    if (argIsNodeSetDtm)
	    {
	    	XMLNodeCursorImpl ns = (XMLNodeCursorImpl)xObjArg1;
	    	ns.setShouldCacheNodes(true);
	    	int len = ns.getLength();	    	
	    	if (len <= 1)
	    		argIsNodeSetDtm = false;
	    }

	    if (argIsNodeSetDtm)
	    {
	    	Map<XMLString, Boolean> usedrefs = null;
	    	
	    	DTMCursorIterator ni = xObjArg1.iter();
	    	int pos;
	    	UnionPathIterator upi = new UnionPathIterator();
	    	upi.exprSetParent(this);

	    	while (DTM.NULL != (pos = ni.nextNode()))
	    	{
	    		dtm = xctxt.getDTM(pos);
	    		XMLString ref = dtm.getStringValue(pos);

	    		if (null == ref)
	    			continue;

	    		if (null == usedrefs)
	    			usedrefs = new HashMap<XMLString, Boolean>();

	    		if (usedrefs.get(ref) != null)
	    		{
	    			continue;  // We already have 'em.
	    		}
	    		else
	    		{
	    			usedrefs.put(ref, ISTRUE);
	    		}

	    		XMLNodeCursorImpl nl = kmgr.getNodeSetDtmByKey(xctxt, docContext, keyQname, 
	    				                                       ref, xctxt.getNamespaceContext());

	    		nl.setRoot(xctxt.getCurrentNode(), xctxt);

	    		upi.addIterator(nl);
	    	}

	    	int current = xctxt.getCurrentNode();
	    	upi.setRoot(current, xctxt);

	    	nodes = new XMLNodeCursorImpl(upi);
	    }
	    else
	    {	      	    		    		    		    	   
	    	if (!isXslKeyComposite) {
	    		if (xObjArg1 instanceof ResultSequence) {
	    			ResultSequence rSeq = (ResultSequence)xObjArg1;
	    			int size2 = rSeq.size();
	    			List<Integer> nodeHandleList = new ArrayList<Integer>();
	    			for (int idx = 0; idx < size2; idx++) {
	    				XObject xObj = rSeq.item(idx);
	    				
	    				String str1 = XslTransformEvaluationHelper.getStrVal(xObj);
		    			XMLString ref = new XString(str1);
		    			XMLNodeCursorImpl xmlNodeCursorImpl = kmgr.getNodeSetDtmByKey(xctxt, docContext, keyQname,
				                                                  										ref, xctxt.getNamespaceContext());
		    			DTMCursorIterator dtmCursorIterator = xmlNodeCursorImpl.iter();
		    			int nextNode = DTM.NULL;
		    			while ((nextNode = dtmCursorIterator.nextNode()) != DTM.NULL) {
		    			   nodeHandleList.add(nextNode);
		    			}
	    			}
	    			
	    			nodes = new XMLNodeCursorImpl(nodeHandleList, xctxt);
	    			nodes.setRoot(xctxt.getCurrentNode(), xctxt);
	    		}
	    		else {	    			
	    			String str1 = XslTransformEvaluationHelper.getStrVal(xObjArg1);	    				    			
	    			XMLString ref = null;
	    			
	    			if ((xObjArg1 instanceof XSNumericType) || (xObjArg1 instanceof XNumber)) {
	    			   XString xStr = new XString(str1);
	    			   XNumber xNum = new XNumber(Double.valueOf(str1));
	    			   
	    			   xStr.setNumber(xNum);
	    			   ref = xStr; 
	    			}
	    			else {
	    			   ref = new XString(str1);
	    			}

	    			nodes = kmgr.getNodeSetDtmByKey(xctxt, docContext, keyQname,
							    					                  ref, xctxt.getNamespaceContext());
	    			nodes.setRoot(xctxt.getCurrentNode(), xctxt);
	    		}
	    	}
	    	else if (xObjArg1 instanceof ResultSequence) {
	    		ResultSequence rSeq = (ResultSequence)xObjArg1;
	    		StringBuffer strBuff = new StringBuffer();
	    		int size2 = rSeq.size();
	    		for (int idx = 0; idx < size2; idx++) {
	    			XObject xObj = rSeq.item(idx);
	    			String str1 = XslTransformEvaluationHelper.getStrVal(xObj);
	    			if (idx < (size2 + 1)) {
	    				strBuff.append(str1 + ","); 
	    			}
	    			else {
	    				strBuff.append(str1);
	    			}
	    		}

	    		String str1 = strBuff.toString();
	    		XMLString ref = new XString(str1);

	    		nodes = kmgr.getNodeSetDtmByKey(xctxt, docContext, keyQname,
											    				ref, xctxt.getNamespaceContext());
	    		nodes.setRoot(xctxt.getCurrentNode(), xctxt);
	    	}               
	    	else {
	    		String str1 = XslTransformEvaluationHelper.getStrVal(xObjArg1);
	    		XMLString ref = new XString(str1);

	    		nodes = kmgr.getNodeSetDtmByKey(xctxt, docContext, keyQname,
											    				ref, xctxt.getNamespaceContext());
	    		nodes.setRoot(xctxt.getCurrentNode(), xctxt);
	    	}
	    }

	    return nodes;
	}
}
