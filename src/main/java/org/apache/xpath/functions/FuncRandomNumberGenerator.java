/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.xpath.functions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.compiler.Keywords;
import org.apache.xpath.functions.context.FuncCurrentDateTime;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XBooleanStatic;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathInlineFunction;
import org.apache.xpath.objects.XPathMap;
import org.apache.xpath.objects.XString;

import xml.xpath31.processor.types.XSAnyAtomicType;
import xml.xpath31.processor.types.XSDateTime;
import xml.xpath31.processor.types.XSDouble;
import xml.xpath31.processor.types.XSNumericType;
import xml.xpath31.processor.types.XSString;

/**
 * Implementation of an XPath 3.1 function fn:random-number-generator.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncRandomNumberGenerator extends FunctionMultiArgs {

	private static final long serialVersionUID = -7202234011428135803L;
	
	/**
	 * Class constructor.
	 */
	public FuncRandomNumberGenerator() {
		m_arity = new Short[] { 0, 1 };
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
		
		XObject result = null;
		
		SourceLocator srcLocator = xctxt.getSAXLocator();
		
		final int sourceNode = xctxt.getCurrentNode(); 
		
		XPathMap xpathMap = new XPathMap();
		
		double randomNumValue = 0.0;		
		
		if (m_arg1 != null) {
			throw new javax.xml.transform.TransformerException("XPST0017 : An XPath 3.1 function 'random-number-generator' can "
					                                                                                                      + "have arity zero or one.", srcLocator); 
		}
		else if (m_arg0 != null) {
			XObject arg0Obj = getFunctionArgEffectiveValue(m_arg0, xctxt);
			
			if ((arg0Obj instanceof ResultSequence) && (((ResultSequence)arg0Obj).size() == 0)) {
				Random random = new Random();
				
				randomNumValue = random.nextDouble();
			}
			else {
				double rngSeedValue = 0.0;

				if (arg0Obj instanceof XNumber) {
					rngSeedValue = ((XNumber)arg0Obj).num();
				}
				else if (arg0Obj instanceof XSNumericType) {
					XSNumericType xsNumericType = (XSNumericType)arg0Obj;
					String numericStrValue = xsNumericType.stringValue();
					
					rngSeedValue = (Double.valueOf(numericStrValue)).doubleValue();
				}
				else if (m_arg0 instanceof FuncCurrentDateTime) {
					XSDateTime xsDateTime = (XSDateTime)(((FuncCurrentDateTime)m_arg0).execute(xctxt));
					Calendar calendar = xsDateTime.getCalendar();
					
					rngSeedValue = (double)(calendar.getTimeInMillis());
				}
				else if (arg0Obj instanceof XSDateTime) {
					XSDateTime xsDateTime = (XSDateTime)arg0Obj;
					Calendar calendar = xsDateTime.getCalendar();
					
					rngSeedValue = (double)(calendar.getTimeInMillis());
				}
				else if ((arg0Obj instanceof XSAnyAtomicType) || (arg0Obj instanceof XString) 
															  || (arg0Obj instanceof XBoolean) 
															  || (arg0Obj instanceof XBooleanStatic)) {
					String arg0Str = XslTransformEvaluationHelper.getStrVal(arg0Obj);
					int rngSeedValue1 = 0;
					int strLength = arg0Str.length();

					for (int idx = 0; idx < strLength; idx++) {
						int chrIntValue = arg0Str.charAt(idx);
						rngSeedValue1 += chrIntValue;
					}

					rngSeedValue = (double)rngSeedValue1;
				}
				else {
					throw new javax.xml.transform.TransformerException("XPST0017 : An XPath 3.1 function 'random-number-generator' "
																										 + "'seed' argument is not of schema "
																										 + "type 'anyAtomicType'.", srcLocator);
				}

				Random random = new Random();
				random.setSeed((long)rngSeedValue);

				randomNumValue = random.nextDouble();
			}
		}
		else {
			Random random = new Random();
			
			randomNumValue = random.nextDouble();
		}
		
		xpathMap.put(new XSString(Keywords.NUMBER), new XSDouble(randomNumValue));
		
		String funcNextXPathStr = "function () { " + Keywords.FUNC_RANDOM_NUMBER_GENERATOR + "() }";
		
		XPath nextExprXPath = new XPath(funcNextXPathStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
		XObject xObj = nextExprXPath.execute(xctxt, DTM.NULL, xctxt.getNamespaceContext());
		
		xpathMap.put(new XSString(Keywords.NEXT), xObj);
		
        String funcPermuteXPathStr = "function ($seq) { " + Keywords.FUNC_RANDOM_NUMBER_GENERATOR + "()?permute($seq) }";
		
		XPath funcPermuteXPath = new XPath(funcPermuteXPathStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
		XObject xObj2 = funcPermuteXPath.execute(xctxt, DTM.NULL, xctxt.getNamespaceContext());
		
		xpathMap.put(new XSString(Keywords.PERMUTE), xObj2);
		
		String funcLookupArg = getFuncLookupArg();
		
		if (funcLookupArg == null) {
		   result = xpathMap; 
		}
		else if ((Keywords.NUMBER).equals(funcLookupArg)) {
		   result = xpathMap.get(new XSString(Keywords.NUMBER));
		}
		else if ("next()".equals(funcLookupArg)) {
		   XPathInlineFunction xpathInlineFunc = (XPathInlineFunction)(xpathMap.get(new XSString(Keywords.NEXT)));
		   String xpathStr1 = xpathInlineFunc.getFuncBodyXPathExprStr();
		   
		   XPath xpath1 = new XPath(xpathStr1, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
		   
		   result = xpath1.execute(xctxt, DTM.NULL, xctxt.getNamespaceContext());
		}
		else if ("next".equals(funcLookupArg)) {
		   result = (XPathInlineFunction)(xpathMap.get(new XSString(Keywords.NEXT)));
		}
		else if ("next()?number".equals(funcLookupArg)) {
			XPathInlineFunction xpathInlineFunc = (XPathInlineFunction)(xpathMap.get(new XSString(Keywords.NEXT)));
			String xpathStr1 = xpathInlineFunc.getFuncBodyXPathExprStr();

			XPath xpath1 = new XPath(xpathStr1, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);

			XPathMap xpathMap2 = (XPathMap)(xpath1.execute(xctxt, DTM.NULL, xctxt.getNamespaceContext()));
			
			result = xpathMap2.get(new XSString(Keywords.NUMBER)); 
		}
		else if (funcLookupArg.startsWith("permute(")) {
			int idx1 = funcLookupArg.indexOf('(') + 1;
			int idx2 = funcLookupArg.lastIndexOf(')');
			
			String xpathStr1 = (funcLookupArg.substring(idx1, idx2)).trim();
			XPath xpath1 = new XPath(xpathStr1, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
			
			XObject xObj1 = xpath1.execute(xctxt, sourceNode, xctxt.getNamespaceContext());
			
			if (xObj1 instanceof ResultSequence) {
			   result = permuteXdmSequence((ResultSequence)xObj1); 
			}
			else if (xObj1 instanceof XMLNodeCursorImpl) {			   								
			   result = permuteXdmNodeSet((XMLNodeCursorImpl)xObj1, xctxt); 
			}
			else {
			   result = xObj1; 
			}
		}
		else if (funcLookupArg.startsWith("next()?permute(")) {
			int idx1 = "next()?permute(".length();
			
			String xpathStr1 = (funcLookupArg.substring(idx1, funcLookupArg.length() - 1)).trim();
            XPath xpath1 = new XPath(xpathStr1, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
			
			XObject xObj1 = xpath1.execute(xctxt, sourceNode, xctxt.getNamespaceContext());
			
			if (xObj1 instanceof ResultSequence) {
			   result = permuteXdmSequence((ResultSequence)xObj1); 
			}
			else if (xObj1 instanceof XMLNodeCursorImpl) {			   								
			   result = permuteXdmNodeSet((XMLNodeCursorImpl)xObj1, xctxt); 
			}
			else {
			   result = xObj1;	
			}
		}
		
		return result;
	}

	/**
	 * Method definition, to produce a random permutation
	 * of the supplied xdm sequence.
	 * 
	 * @param rSeq                         The supplied xdm sequence
	 *                                     object instance.
	 * @return                             Random permutation of the supplied, 
	 *                                     xdm sequence.
	 */
	private ResultSequence permuteXdmSequence(ResultSequence rSeq) {
		
		ResultSequence result = null;

		List<XObject> list1 = rSeq.getResultSequenceItems();

		Collections.shuffle(list1);

		ResultSequence rSeq2 = new ResultSequence();

		int size1 = list1.size();

		for (int idx = 0; idx < size1; idx++) {
			rSeq2.add(list1.get(idx));
		}

		result = rSeq2;

		return result;
	}
	
	/**
	 * Method definition, to produce a random permutation
	 * of the supplied xdm node set.
	 * 
	 * @param xObj1                        The supplied xdm node set
	 *                                     object instance.
	 * @return                             Random permutation of the supplied, 
	 *                                     xdm node set.
	 */
	private XObject permuteXdmNodeSet(XMLNodeCursorImpl xObj1, XPathContext xctxt) throws TransformerException {
		
		XMLNodeCursorImpl result = null;

		DTMCursorIterator dtmCursorIter = xObj1.iter();

		List<XObject> list1 = new ArrayList<XObject>(); 

		int nextNode = DTM.NULL;

		while ((nextNode = dtmCursorIter.nextNode()) != DTM.NULL) {
			XMLNodeCursorImpl xmlNodeCursorImpl1 = new XMLNodeCursorImpl(nextNode, xctxt);

			list1.add(xmlNodeCursorImpl1);
		}

		Collections.shuffle(list1);

		int size1 = list1.size();

		List<Integer> dtmList1 = new ArrayList<Integer>();

		for (int idx = 0; idx < size1; idx++) {
			XObject xObj3 = list1.get(idx);				  
			XMLNodeCursorImpl xmlNodeCursorImpl1 = (XMLNodeCursorImpl)xObj3;

			int dtmNodeId = xmlNodeCursorImpl1.asNode(xctxt);
			dtmList1.add(dtmNodeId);
		}

		result = new XMLNodeCursorImpl(dtmList1, xctxt);

		return result;
	}

}
