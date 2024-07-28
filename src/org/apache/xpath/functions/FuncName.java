/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the  "License");
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

import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xpath.Expression;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XObject;
import org.w3c.dom.Node;

import xml.xpath31.processor.types.XSString;

/**
 * Implementation of XPath 3.1 fn:name() function.
 * 
 * @author : Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncName extends FunctionMultiArgs {

  private static final long serialVersionUID = -3263417937216412681L;

  /**
   * Implementation of the function. The function must return
   * a valid object.
   * 
   * @param xctxt The current execution context.
   * @return A valid XObject.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
  {

	  XObject result = null;
    
      SourceLocator srcLocator = xctxt.getSAXLocator();
	  
	  Expression arg0 = getArg0();
	  String nodeNameStr = null;
	  
	  if (arg0 == null) {
		 int contextNode = xctxt.getCurrentNode();
		 DTM dtm = xctxt.getDTM(contextNode);
		 Node node = dtm.getNode(contextNode);
		 nodeNameStr = node.getNodeName();
	  }
	  else {
		 XObject nodeArg = arg0.execute(xctxt);
		 if (nodeArg instanceof XNodeSet) {
			XNodeSet nodeSet = (XNodeSet)nodeArg;
			DTMIterator dtmIter = nodeSet.iterRaw();
			int nodeHandle = dtmIter.nextNode();
			DTM dtm = xctxt.getDTM(nodeHandle);
			Node node = dtm.getNode(nodeHandle);
			nodeNameStr = node.getNodeName();
		 }
		 else {
			throw new javax.xml.transform.TransformerException("XPTY0004: The 1st argument of function fn:name is not a node.", srcLocator);  
		 }		 
	  }
	  
	  result = new XSString(nodeNameStr);

      return result;
  }
}
