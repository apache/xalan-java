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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.xml.transform.SourceLocator;

import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xpath.Expression;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XObject;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import xml.xpath31.processor.types.XSString;

/**
 * Implementation of XPath 3.1 fn:in-scope-prefixes() function.
 * 
 * @author : Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncInScopePrefixes extends FunctionOneArg {

   private static final long serialVersionUID = 2372823852330912332L;
   
   /**
    * Class constructor.
    */
   public FuncInScopePrefixes() {
	   m_defined_arity = new Short[] { 1 };
   }

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

	  ResultSequence result = null;
    
      SourceLocator srcLocator = xctxt.getSAXLocator();
	  
	  Expression arg0 = getArg0();

	  XObject nodeArg = arg0.execute(xctxt);
	  if (nodeArg instanceof XMLNodeCursorImpl) {
		 XMLNodeCursorImpl nodeSet = (XMLNodeCursorImpl)nodeArg;
		 DTMCursorIterator dtmIter = nodeSet.iterRaw();
		 int nodeHandle = dtmIter.nextNode();
		 DTM dtm = xctxt.getDTM(nodeHandle);
		 Node node = dtm.getNode(nodeHandle);
		 if (node.getNodeType() == Node.ELEMENT_NODE) {
			 Set<String> inScopePrefixesSet = new HashSet<String>();
			 getInScopePrefixes(node, inScopePrefixesSet);
			 Iterator<String> inscopePrefixesIterator = inScopePrefixesSet.iterator();
			 ResultSequence resultSequence = new ResultSequence(); 
			 while (inscopePrefixesIterator.hasNext()) {
				resultSequence.add(new XSString(inscopePrefixesIterator.next())); 
			 }			 
			 result = resultSequence;
		 }
		 else {
			throw new javax.xml.transform.TransformerException("XPTY0004: The argument of fn:in-scope-prefixes function "
					                                                         + "is not an element node", srcLocator);	 
		 }
      }
	  else {
		 throw new javax.xml.transform.TransformerException("XPTY0004: The argument of fn:in-scope-prefixes function is not "
		 		                                                         + "an element.", srcLocator);  
	  }

      return result;
  }

  /**
   * Get the in-scope-prefixes of an element node.
   */
  private void getInScopePrefixes(Node node, Set<String> inScopePrefixesSet) {	  
	  NamedNodeMap attrMap = node.getAttributes();
	  int attrCount = attrMap.getLength();
	  for (int idx = 0; idx < attrCount; idx++) {
		 Node attrNode = attrMap.item(idx);
		 String attrName = attrNode.getNodeName();
		 if ("xmlns".equals(attrName)) {
			inScopePrefixesSet.add("");
		 }
		 else if (attrName.startsWith("xmlns:")) {
		    String prefixStr = attrName.substring(6);
		    inScopePrefixesSet.add(prefixStr);
		 }
	  }
	  
	  Node parentNode = node.getParentNode();	  
	  if (parentNode.getNodeType() == Node.ELEMENT_NODE) {
		 getInScopePrefixes(node.getParentNode(), inScopePrefixesSet); 
	  }
  }
}
