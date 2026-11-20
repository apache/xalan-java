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
/*
 * $Id$
 */
package org.apache.xpath.functions;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XBooleanStatic;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathArray;
import org.apache.xpath.objects.XPathInlineFunction;
import org.apache.xpath.objects.XPathMap;
import org.apache.xpath.objects.XString;
import org.apache.xpath.patterns.NodeTest;

import xml.xpath31.processor.types.XSAnyAtomicType;
import xml.xpath31.processor.types.XSString;
import xml.xpath31.processor.types.XSUntyped;
import xml.xpath31.processor.types.XSUntypedAtomic;

/**
 * Implementation of an XPath 3.1 function fn:data.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncData extends FunctionDef1Arg
{

  private static final long serialVersionUID = 1734069066834829751L;
  
  /**
   * Class constructor.
   */
  public FuncData() {
	  m_arity = new Short[] {0, 1}; 
  }

  /**
   * Evaluate the function. The function must return a valid object.
   * 
   * @param xctxt                           An XPath context object
   * @return                                A valid XObject
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
  {
      XObject result = null;
      
      ResultSequence rSeq = new ResultSequence();
      
      SourceLocator srcLocator = xctxt.getSAXLocator();
      
      XObject argObj = null;
      
      final int sourceNode = xctxt.getCurrentNode();
      
      if (m_arg0 instanceof NodeTest) {
    	  if (XslTransformEvaluationHelper.isNodeTestExpressionFuntionType((NodeTest)m_arg0)) {
    		 throw new TransformerException("FOTY0013 : An XPath 3.1 function call 'data' has argument as function type, "
    		 																										     + "that cannot be atomized.", srcLocator);  
    	  }
      }
      else if (m_arg0 instanceof XPathInlineFunction) {
    	  throw new TransformerException("FOTY0013 : An XPath 3.1 function call 'data' has argument as function type, "
    	  																					                          + "that cannot be atomized.", srcLocator);
      }
      
      if (m_arg0 == null) {
          // An XPath 3.1 function fn:data has been called with no arguments
         
    	  XObject xpath3ContextItem = xctxt.getXPath3ContextItem();
         
         if (xpath3ContextItem != null) {
            argObj = xpath3ContextItem;
            
            if (argObj instanceof XPathInlineFunction) {
      		   throw new TransformerException("FOTY0013 : An XPath 3.1 function call 'data' has an argument from context as function type, "
      		  																													           + "that cannot be atomized.", srcLocator); 
      	    }
         }
         else if (sourceNode != DTM.NULL) {
            argObj = new XMLNodeCursorImpl(sourceNode, xctxt); 
         }
      }
      else {
         // An explicit argument was provided, to the fn:data 
         // function call.
    	 
    	  if (m_arg0 instanceof XMLNodeCursorImpl) {
    		  argObj = (XObject)m_arg0; 
    	  }
    	  else {
    		  argObj = getFunctionArgEffectiveValue(m_arg0, xctxt); 
    	  }
         
         if (argObj instanceof ResultSequence) {
        	ResultSequence rSeq2 = (ResultSequence)argObj;
        	int rSeqLength1 = rSeq2.size();
        	
        	for (int idx = 0; idx < rSeqLength1; idx++) {
        	   XObject xObj1 = rSeq2.item(idx);
        	   
        	   if (xObj1 instanceof XPathInlineFunction) {
        		  throw new TransformerException("FOTY0013 : An XPath 3.1 function call 'data' has argument as function type, "
        		  																											  + "that cannot be atomized.", srcLocator); 
        	   }
        	   else if (xObj1 instanceof XPathMap) {
         		  throw new TransformerException("FOTY0013 : An XPath 3.1 function call 'data' has argument as map type, "
         		  																									     + "that cannot be atomized.", srcLocator); 
         	   }
        	}
         }
      }
      
      if (argObj instanceof XMLNodeCursorImpl) {    	 
         XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)argObj;                           
         
         //XSUntypedAtomic xsUntypedAtomic = new XSUntypedAtomic(xmlNodeCursorImpl.str());
         
         //rSeq.add(xsUntypedAtomic);
         
         DTMCursorIterator dtmCursorIter = xmlNodeCursorImpl.iter();
         
         int nextNode = DTM.NULL;
         
         while ((nextNode = dtmCursorIter.nextNode()) != DTM.NULL) {
        	XMLNodeCursorImpl xmlNodeCursorImpl1 = new XMLNodeCursorImpl(nextNode, xctxt);
        	DTM dtm = xctxt.getDTM(nextNode);
        	short nodeType = dtm.getNodeType(nextNode);
        	
        	String str1 = xmlNodeCursorImpl1.str();
        	
        	if (dtm.getNodeType(nextNode) == DTM.TEXT_NODE || dtm.getNodeType(nextNode) == DTM.DOCUMENT_NODE) {
        	   rSeq.add(new XSUntypedAtomic(str1));
        	}
        	else if ((dtm.getNodeType(nextNode) == DTM.COMMENT_NODE) || (dtm.getNodeType(nextNode) == DTM.NAMESPACE_NODE)
        			                                                 || (dtm.getNodeType(nextNode) == DTM.PROCESSING_INSTRUCTION_NODE)) {        		
         	   rSeq.add(new XSString(str1));
        	}
        	else if (dtm.getNodeType(nextNode) == DTM.ATTRIBUTE_NODE) {
        	   rSeq.add(new XSUntypedAtomic(str1));
        	}
        	else if (dtm.getNodeType(nextNode) == DTM.ELEMENT_NODE) {
        	   rSeq.add(new XSUntypedAtomic(str1));
        	}
         }
      }
      else if (argObj instanceof ResultSequence) {
         ResultSequence rSeq2 = (ResultSequence)argObj;
         
         ResultSequence expandedResultSeq = new ResultSequence();
         XslTransformEvaluationHelper.expandResultSequence(rSeq2, expandedResultSeq);
         
         int size1 = expandedResultSeq.size();
         
         for (int idx = 0; idx < size1; idx++) {
            XObject xdmItem = expandedResultSeq.item(idx);
            
            if ((xdmItem instanceof XSAnyAtomicType) || (xdmItem instanceof XBooleanStatic) || 
            	(xdmItem instanceof XBoolean) || (xdmItem instanceof XNumber) || (xdmItem instanceof XString) ||
                (xdmItem instanceof XSUntypedAtomic) || (xdmItem instanceof XSUntyped)) {
               rSeq.add(xdmItem); 
            }
            else if (xdmItem instanceof XMLNodeCursorImpl) {               
               XMLNodeCursorImpl xmlNodeCursorImpl1 = (XMLNodeCursorImpl)xdmItem;
               
               FuncData funcData = new FuncData();
               funcData.setArg0(xmlNodeCursorImpl1);
               
               XObject xObj = funcData.execute(xctxt);
               rSeq.add(xObj); 
            }
            else if (xdmItem instanceof XPathArray) {
               XPathArray xpathArr = (XPathArray)xdmItem;
               ResultSequence rSeq3 = xpathArr.atomize();
               rSeq.add(rSeq3);
            }
            else if ((xdmItem instanceof XPathInlineFunction) || (xdmItem instanceof XPathMap)) {
               throw new TransformerException("FOTY0013 : An XPath 3.1 function 'data' cannot atomize an xdm function item, or a map.", srcLocator);
            }
         }
      }
      else if (argObj instanceof XPathArray) {
    	  rSeq = ((XPathArray)argObj).atomize();
      }
      else if ((m_arg0 instanceof Function) || (argObj instanceof XPathInlineFunction) 
    		                                || (argObj instanceof XPathMap)) {
    	 throw new TransformerException("FOTY0013 : An XPath 3.1 function 'data' cannot atomize an xdm function item, or a map.", srcLocator); 
      }
      else {
    	  rSeq.add(argObj); 
      }
      
      ResultSequence expandedResultSeq = new ResultSequence();
      XslTransformEvaluationHelper.expandResultSequence(rSeq, expandedResultSeq);
      
      rSeq = expandedResultSeq;
      
      if (rSeq.size() == 1) {
    	 result = rSeq.item(0);  
      }
      else {
    	 result = rSeq;  
      }
      
      return result; 
   }
}
