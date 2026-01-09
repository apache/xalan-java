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
package org.apache.xpath.composite;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xml.dtm.DTMManager;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XObject;

/**
 * Method definition, to support XPath parse of expressions like
 * /info/text()[. = 'there']/following-sibling::* etc. These are
 * newly introduced after XPath 1.0.
 * 
 * An implementation of this class, is motivated by W3C XSLT 3.0
 * test suite cases iterate-028 & iterate-030.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XPathTextAndNodeExpr extends Expression {
	
	private static final long serialVersionUID = 410225432741175797L;

	/**
	 * XPath expression string fragment, representing optional 
	 * xpath prefix string before /text() ... or /node() ...
	 */
	private String m_xpathPrefixStr = null;
	
	/**
	 * Run-time non null string value, that is either text() 
	 * or node().
	 */
	private String m_nodeStr = null;
	
	/**
	 * XPath expression string fragment, representing optional 
	 * predicate.
	 */
	private String m_xpathPredicateValStr = null;
	
	/**
	 * XPath expression string fragment, representing optional 
	 * XPath fragment string after predicate.
	 */
	private String m_xpathSuffixValStr = null;
	
	/**
	 * Class field used to represent XSL variables, available
	 * within XSL transformation run-time context.
	 */
    private Vector m_vars;
    
    /**
     * Class field used to represent XSL variables, available
     * within XSL transformation run-time context.
     */
    private int m_globals_size;

	@Override
	public XObject execute(XPathContext xctxt) throws TransformerException {
		
		XObject result = null;
		
		final int sourceNode = xctxt.getCurrentNode();
		SourceLocator srcLocator = xctxt.getSAXLocator();
		
		XPath xpathObj = null;
		XObject xObjResult = null;
		
		if ((m_xpathPrefixStr == null) && (m_xpathPredicateValStr == null) 
				                                                 && (m_xpathSuffixValStr == null)) {
			xpathObj = new XPath(m_nodeStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
		    if (m_vars != null) {
				xpathObj.fixupVariables(m_vars, m_globals_size);
			}
		    
		    result = xpathObj.execute(xctxt, sourceNode, xctxt.getNamespaceContext());
		    
		    return result;
		}
		
		List<Integer> nodeHandleSeq = new ArrayList<Integer>();
		if (m_xpathPrefixStr != null) {
		    xpathObj = new XPath(m_xpathPrefixStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
		    if (m_vars != null) {
				xpathObj.fixupVariables(m_vars, m_globals_size);
			}
		    
		    xObjResult = xpathObj.execute(xctxt, sourceNode, xctxt.getNamespaceContext());
		    
		    XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)xObjResult;
		    if (xmlNodeCursorImpl.getLength() == 1) {		    	
		    	nodeHandleSeq.add(Integer.valueOf(xmlNodeCursorImpl.asNode(xctxt)));
		    }
		    else {
		    	DTMCursorIterator nodeIter1 = xmlNodeCursorImpl.iterRaw();
		    	int nextNode;	    
		    	while ((nextNode = nodeIter1.nextNode()) != DTM.NULL) {
		    		nodeHandleSeq.add(Integer.valueOf(nextNode));
		    	}
		    }
		}
		else {
			nodeHandleSeq.add(Integer.valueOf(sourceNode));
		}
		
				
		ResultSequence rSeq = new ResultSequence();
		
		int nodeSetLength = nodeHandleSeq.size();
		for (int idx = 0; idx < nodeSetLength; idx++) {
			int localContextNode = (nodeHandleSeq.get(idx)).intValue();
			xpathObj = new XPath(m_nodeStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
			if (m_vars != null) {
				xpathObj.fixupVariables(m_vars, m_globals_size);
			}

			xObjResult = xpathObj.execute(xctxt, localContextNode, xctxt.getNamespaceContext());

			XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)xObjResult;
			DTMCursorIterator nodeIter1 = xmlNodeCursorImpl.iterRaw();
			int nextNode;
			DTMManager dtmManager = xctxt.getDTMManager();			 
			while ((nextNode = nodeIter1.nextNode()) != DTM.NULL) {
				XMLNodeCursorImpl nodeRef = new XMLNodeCursorImpl(nextNode, dtmManager);
				rSeq.add(nodeRef);
			}
		}
		
		if (m_xpathPredicateValStr != null) {
		   ResultSequence rSeq2 = new ResultSequence();
		   
		   Integer predicateIntVal = null;
		   try {
			   predicateIntVal = Integer.valueOf(m_xpathPredicateValStr);
		   }
		   catch (NumberFormatException ex) {
			   // no op 
		   }
		   
		   int nodeCount = rSeq.size();
		   if (predicateIntVal != null) {
			  if (!((predicateIntVal.intValue() < 1) || (predicateIntVal.intValue() > nodeCount))) {
				 XObject xObj = rSeq.item(predicateIntVal - 1);
				 rSeq2.add(xObj);
			  }
			  else {
				 // Error handling 
			  }
		   }
		   else {			   
			   for (int idx = 0; idx < nodeCount; idx++) {
				   XMLNodeCursorImpl nodeRef = (XMLNodeCursorImpl)(rSeq.item(idx));
				   int contextNode1 = (nodeRef.iterRaw()).nextNode();
				   xpathObj = new XPath(m_xpathPredicateValStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
				   if (m_vars != null) {
					   xpathObj.fixupVariables(m_vars, m_globals_size);
				   }
				   
				   xObjResult = xpathObj.execute(xctxt, contextNode1, xctxt.getNamespaceContext());
				   if (xObjResult.bool()) {
					   rSeq2.add(nodeRef);  
				   }
			   } 
		   }
		   
		   if (m_xpathSuffixValStr != null) {
			   nodeCount = rSeq2.size();
			   ResultSequence rSeq3 = new ResultSequence();
			   for (int idx = 0; idx < nodeCount; idx++) {
				   XMLNodeCursorImpl nodeRef = (XMLNodeCursorImpl)(rSeq2.item(idx));
				   int contextNode1 = (nodeRef.iterRaw()).nextNode();
				   xpathObj = new XPath(m_xpathSuffixValStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
				   if (m_vars != null) {
					   xpathObj.fixupVariables(m_vars, m_globals_size);
				   }

				   xObjResult = xpathObj.execute(xctxt, contextNode1, xctxt.getNamespaceContext());
				   if (xObjResult instanceof XMLNodeCursorImpl) {
					   XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)xObjResult;					   
					   int length1 = xmlNodeCursorImpl.getLength();
					   DTMCursorIterator nodeIter1 = null;
					   if (length1 > 0) {
						   nodeIter1 = xmlNodeCursorImpl.iter();						  
					   }
					   else {
						   nodeIter1 = xmlNodeCursorImpl.iterRaw();
					   }
					   
					   int nextNode;
					   DTMManager dtmManager = xctxt.getDTMManager();			 
					   while ((nextNode = nodeIter1.nextNode()) != DTM.NULL) {
						   XMLNodeCursorImpl nodeRef2 = new XMLNodeCursorImpl(nextNode, dtmManager);
						   rSeq3.add(nodeRef2);
					   }
				   }
			   }
			   
			   result = rSeq3;
		   }
		   else {
			  result = rSeq2;
		   }
		}
		else {
			result = rSeq;
		}
		
		return result;
	}

	@Override
	public void fixupVariables(Vector vars, int globalsSize) {
		// TO DO
	}
	
	@Override
	public void callVisitors(ExpressionOwner owner, XPathVisitor visitor) {
		// no op
	}

	@Override
	public boolean deepEquals(Expression expr) {
		// no op
		return false;
	}
	
	public String getXpathPrefixStr() {
		return m_xpathPrefixStr;
	}
	
	public void setXpathPrefixStr(String xpathPrefixStr) {
		this.m_xpathPrefixStr = xpathPrefixStr;
	}

	public String getNodeStr() {
		return m_nodeStr;
	}

	public void setNodeStr(String nodeStr) {
		this.m_nodeStr = nodeStr;
	}

	public String getXpathPredicateValStr() {
		return m_xpathPredicateValStr;
	}

	public void setXpathPredicateValStr(String xpathPredicateValStr) {
		this.m_xpathPredicateValStr = xpathPredicateValStr;
	}

	public String getXpathSuffixValStr() {
		return m_xpathSuffixValStr;
	}

	public void setXpathSuffixValStr(String xpathSuffixValStr) {
		this.m_xpathSuffixValStr = xpathSuffixValStr;
	}

}
