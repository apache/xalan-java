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

import java.math.BigInteger;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xml.dtm.DTMManager;
import org.apache.xpath.Expression;
import org.apache.xpath.XPathCollationSupport;
import org.apache.xpath.XPathContext;
import org.apache.xpath.axes.LocPathIterator;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XObject;

import xml.xpath31.processor.types.XSDouble;
import xml.xpath31.processor.types.XSFloat;
import xml.xpath31.processor.types.XSInteger;
import xml.xpath31.processor.types.XSUntyped;
import xml.xpath31.processor.types.XSUntypedAtomic;

/**
 * Implementation of an XPath 3.1 function fn:index-of.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncIndexOf extends FunctionMultiArgs {

   private static final long serialVersionUID = 2912594883291006421L;
   
   /**
    * Class constructor.
    */
   public FuncIndexOf() {
	   m_defined_arity = new Short[] { 2, 3 };
   }

  /**
   * Evaluate the function. The function must return a valid object.
   * 
   * @param xctxt                     An XPath context object
   * 
   * @return A valid XObject
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
  {      
	  
	    XObject result = null;
                        
        SourceLocator srcLocator = xctxt.getSAXLocator();
        
        final int contextNode = xctxt.getCurrentNode();
        
        Expression arg0 = getArg0();
        Expression arg1 = getArg1();
        Expression arg2 = getArg2();                
        
  	    if ((arg0 == null) || (arg1 == null)) {
 		   throw new javax.xml.transform.TransformerException("XPST0017 : An XPath 3.1 function 'index-of' has been called with wrong "
 		   		                                                                                      + "number of arguments. Expected function 'index-of' "
 		   		                                                                                      + "argument count is either two or three.", srcLocator);  
 	    }
  	    
  	    Expression[] fourthAndAboveArgs = getArgs();
  	    if ((fourthAndAboveArgs != null) && (fourthAndAboveArgs.length > 0)) {
  	       throw new javax.xml.transform.TransformerException("XPST0017 : An XPath 3.1 function 'index-of' has been called with wrong "
																				                      + "number of arguments. Expected function 'index-of' "
																				                      + "argument count is either two or three.", srcLocator);
  	    }
  	    
  	    XPathCollationSupport xpathCollationSupport = xctxt.getXPathCollationSupport();
  	    
  	    String collationUri = null;
      
		if (arg2 != null) {
		   // A collation uri was, explicitly provided during the function call fn:index-of
			
		   XObject collationXObj = arg2.execute(xctxt);
		   
		   if (!((collationXObj instanceof ResultSequence) && (((ResultSequence)collationXObj).size() == 0))) {			   
			   collationUri = XslTransformEvaluationHelper.getStrVal(collationXObj);
		   }
		   else {
			   throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath 3.1 function 'index-of' cannot have an empty "
                                                                                                      + "sequence as its third argument.", srcLocator);
		   }
		}
		else {
		   collationUri = xctxt.getDefaultCollation(); 
		}
        
        DTMManager dtmMgr = (DTMManager)xctxt;
        
        ResultSequence arg0ResultSeq = null;
        
        if (arg0 instanceof LocPathIterator) {
            arg0ResultSeq = new ResultSequence();
                                
            DTMCursorIterator arg0DtmIterator = arg0.asIterator(xctxt, contextNode);        
            
            int nextNodeDtmHandle;
            
            while ((nextNodeDtmHandle = arg0DtmIterator.nextNode()) != DTM.NULL) {
                XMLNodeCursorImpl xNodeSetItem = new XMLNodeCursorImpl(nextNodeDtmHandle, dtmMgr);                
                String nodeStrValue = xNodeSetItem.str();
                
                DTM dtm = dtmMgr.getDTM(nextNodeDtmHandle);
                
                if (dtm.getNodeType(nextNodeDtmHandle) == DTM.ELEMENT_NODE) {
                   XSUntyped xsUntyped = new XSUntyped(nodeStrValue);
                   arg0ResultSeq.add(xsUntyped);
                }
                else if (dtm.getNodeType(nextNodeDtmHandle) == DTM.ATTRIBUTE_NODE) {
                   XSUntypedAtomic xsUntypedAtomic = new XSUntypedAtomic(nodeStrValue);
                   arg0ResultSeq.add(xsUntypedAtomic);
                }
                else {
                   XSUntypedAtomic xsUntypedAtomic = new XSUntypedAtomic(nodeStrValue);
                   arg0ResultSeq.add(xsUntypedAtomic);
                }                        
            } 
        }
        else {
            XObject arg0Obj = arg0.execute(xctxt);
            
            if (arg0Obj instanceof ResultSequence) {
               arg0ResultSeq = (ResultSequence)arg0Obj;     
            }           
            else {
               arg0ResultSeq = new ResultSequence();
               arg0ResultSeq.add(arg0Obj);
            }
        }
        
        XObject arg1Obj = arg1.execute(xctxt);
        
        if (arg1Obj instanceof ResultSequence) {
           ResultSequence rSeq = (ResultSequence)arg1Obj;
           
           if (rSeq.size() == 1) {
        	  arg1Obj = rSeq.item(0); 
           }
           else {
        	  throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath 3.1 function 'index-of' second argument should be "
        	  		                                                                                                              + "an xdm atomic value.", srcLocator); 
           }
        }
        else if (arg1Obj instanceof XMLNodeCursorImpl) {
           XMLNodeCursorImpl xNodeSet = (XMLNodeCursorImpl)arg1Obj;
           
           if (xNodeSet.getLength() == 1) {
              String nodeStrValue = xNodeSet.str();
               
              DTMCursorIterator sourceNodes = arg0.asIterator(xctxt, contextNode);
              int dtmNodeHandle = sourceNodes.nextNode();
              
              DTM dtm = dtmMgr.getDTM(dtmNodeHandle);
               
              if (dtm.getNodeType(dtmNodeHandle) == DTM.ELEMENT_NODE) {
                 arg1Obj = new XSUntyped(nodeStrValue);
              }
              else if (dtm.getNodeType(dtmNodeHandle) == DTM.ATTRIBUTE_NODE) {
                 arg1Obj = new XSUntypedAtomic(nodeStrValue);
              }
              else {
                 arg1Obj = new XSUntypedAtomic(nodeStrValue);
              }    
           }
           else {                            
              throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath 3.1 function 'index-of' second argument should be "
              		                                                                                                              + "an xdm atomic value.", srcLocator); 
           }
        }
        
                
        ResultSequence resultSeq1 = new ResultSequence();
        
        boolean isNanSearch = false;
        if (arg1Obj instanceof XSDouble) {
           isNanSearch = ((XSDouble)arg1Obj).nan();	
        }
        else if (arg1Obj instanceof XSFloat) {
           isNanSearch = ((XSFloat)arg1Obj).nan();
        }
        
        if (!isNanSearch) {
        	int size1 = arg0ResultSeq.size();
        	
        	for (int idx = 0; idx < size1; idx++) {
        		ResultSequence rSeq1 = new ResultSequence();

        		XObject xObj = arg0ResultSeq.item(idx);
        		boolean isNanSearch1 = false;
        		if (xObj instanceof XSDouble) {
        			isNanSearch1 = ((XSDouble)xObj).nan();	
        		}
        		else if (xObj instanceof XSFloat) {
        			isNanSearch1 = ((XSFloat)xObj).nan();
        		}

        		if (!isNanSearch1) {
        			rSeq1.add(xObj);	
        		}
        		else {
        			continue;
        		}
                
        		try {
        		   if (XslTransformEvaluationHelper.contains(rSeq1, arg1Obj, collationUri, xpathCollationSupport)) {
        			  resultSeq1.add(new XSInteger(BigInteger.valueOf(idx + 1)));    
        		   }
        		}
        		catch (TransformerException ex) {
        		   throw new TransformerException(ex.getMessage(), srcLocator);
        		}
        	}
        }
        
        result = resultSeq1;
            
        return result;
   }

}
