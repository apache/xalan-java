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

import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XObject;

/**
 * Implementation of an XPath 3.1 function fn:exactly-one.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncExactlyOne extends FunctionDef1Arg {

	private static final long serialVersionUID = 1979788427069492766L;

	/**
	 * Class constructor.
	 */
	public FuncExactlyOne() {
		m_defined_arity = new Short[] { 1 };
	}
	
	/**
	 * Evaluate the function. The function must return a valid object.
	 * 
	 * @param xctxt                         An XPath context object
	 * @return                              A valid XObject
	 *
	 * @throws javax.xml.transform.TransformerException
	 */
	public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
	{
		
		XObject result = null;
		
		SourceLocator srcLocator = xctxt.getSAXLocator();
		
		XObject arg0 = getFunctionEffectiveArgValue(m_arg0, xctxt);
		
		if (arg0 instanceof ResultSequence) {
		   ResultSequence rSeq = (ResultSequence)arg0;
		   
		   int size = rSeq.size();
		   if (size == 1) {
			  result = arg0; 
		   }
		   else {
			  throw new TransformerException("FORG0005 : An XPath 3.1 function call 'exactly-one' has an argument "
			  		                                                                               + "that is empty or contains more than one item.", srcLocator);  
		   }
		}
		else if (arg0 instanceof XMLNodeCursorImpl) {
		   XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)arg0;
		   
		   int size = xmlNodeCursorImpl.getLength();
		   if (size == 1) {
			   result = arg0; 
		   }
		   else {
			   throw new TransformerException("FORG0005 : An XPath 3.1 function call 'exactly-one' has an argument "
                                                                                                   + "that is empty or contains more than one item.", srcLocator);   
		   }
		}
		else {
		   result = arg0;
		}
		
		return result;
		
	}

}
