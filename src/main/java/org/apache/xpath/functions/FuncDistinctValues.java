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
package org.apache.xpath.functions;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xpath.XPathCollationSupport;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.string.XSL3StringCollationAwareFunction;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XObject;

import xml.xpath31.processor.types.XSUntypedAtomic;

/**
 * Implementation of an XPath 3.1 function fn:distinct-values.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncDistinctValues extends XSL3StringCollationAwareFunction {
    
   private static final long serialVersionUID = -1637800188441824456L;
   
   /**
    * Default constructor.
    */
   public FuncDistinctValues() {
 	  m_arity = new Short[] { 1, 2 }; 
   }

   /**
    * Evaluate the function. The function must return a valid object.
    * 
    * @param xctxt                        An XPath context object
    * @return                             A valid XObject
    *
    * @throws javax.xml.transform.TransformerException
    */
   public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
   {      
	   
	   ResultSequence resultSeq = new ResultSequence();
	   
	   SourceLocator srcLocator = xctxt.getSAXLocator();

	   if (m_arg0 == null) {
		   throw new TransformerException("XPST0017 : An XPath 3.1 function 'distinct-values', may have one or two arguments.", srcLocator);  
	   }

	   XPathCollationSupport xpathCollationSupport = xctxt.getXPathCollationSupport();

	   String collationUri = null;

	   if (m_arg1 != null) {
		   // A collation uri has been, explicitly provided during the function call fn:distinct-values
		   
		   XObject collationXObj = getFunctionArgEffectiveValue(m_arg1, xctxt);
		   
		   collationUri = XslTransformEvaluationHelper.getStrVal(collationXObj); 			 			 
	   }	   

	   XObject arg0Obj = getFunctionArgEffectiveValue(m_arg0, xctxt);
	   
	   if (arg0Obj instanceof ResultSequence) {
		   ResultSequence inpResultSeq = (ResultSequence)arg0Obj;
		   int size1 = inpResultSeq.size();
		   
		   for (int idx = 0; idx < size1; idx++) {
			   XObject xObj = inpResultSeq.item(idx);
			   
			   XslTransformEvaluationHelper.addItemToResultSequence(resultSeq, xObj, true, collationUri, xpathCollationSupport, xctxt);
		   }
	   }
	   else if (arg0Obj instanceof XMLNodeCursorImpl) {
		   XMLNodeCursorImpl xNodeSet = (XMLNodeCursorImpl)arg0Obj;           
		   DTMCursorIterator dtmCursorIterator = xNodeSet.iter();

		   int nextNode = DTM.NULL;

		   while ((nextNode = dtmCursorIterator.nextNode()) != DTM.NULL) {
			   XMLNodeCursorImpl xmlNodeCursorImpl = new XMLNodeCursorImpl(nextNode, xctxt);
			   String nodeStrValue = xmlNodeCursorImpl.str();

			   XSUntypedAtomic xsUntypedAtomic = new XSUntypedAtomic(nodeStrValue);
			   XslTransformEvaluationHelper.addItemToResultSequence(resultSeq, xsUntypedAtomic, true, collationUri, xpathCollationSupport, xctxt);
		   }
	   }	   
	   else {
		   resultSeq.add(arg0Obj);
	   }
	   
	   ResultSequence rSeq2 = new ResultSequence(); 
	   
	   XslTransformEvaluationHelper.expandResultSequence(resultSeq, rSeq2);
	   
	   resultSeq = rSeq2;

	   return resultSeq;
   }

}
