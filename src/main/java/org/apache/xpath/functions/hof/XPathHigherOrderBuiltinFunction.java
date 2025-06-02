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
package org.apache.xpath.functions.hof;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xml.dtm.DTMManager;
import org.apache.xpath.Expression;
import org.apache.xpath.XPathContext;
import org.apache.xpath.axes.LocPathIterator;
import org.apache.xpath.composite.XPathForExpr;
import org.apache.xpath.composite.XPathSequenceConstructor;
import org.apache.xpath.functions.Function3Args;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.operations.Range;
import org.apache.xpath.operations.Variable;

import xml.xpath31.processor.types.XSUntyped;
import xml.xpath31.processor.types.XSUntypedAtomic;

/**
 * This class provides few utility methods, to support XPath 3.1 
 * built-in higher order function call evaluation. The language higher
 * order functions do one or both of following : accept functions as
 * arguments, or return function as function call result.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XPathHigherOrderBuiltinFunction extends Function3Args {
    
    private static final long serialVersionUID = 5970365027214826130L;

    /**
     * Method definition to evaluate an XPath expression to produce a sequence,
     * that can be used as argument to an XPath higher order function call.
     *  
     * @param xpathExpr      An XPath compiled expression
     * @param xctxt          An XPath expression context object
     * 
     * @return               A sequence returned by this method
     * 
     * @throws javax.xml.transform.TransformerException
     */
    protected ResultSequence constructSequenceFromXPathExpression(Expression xpathExpr, XPathContext xctxt) 
                                                                                       throws javax.xml.transform.TransformerException {        
        ResultSequence resultSeq = new ResultSequence();
        
        int contextNode = xctxt.getContextNode();

        if (xpathExpr instanceof Range) {
            resultSeq = (ResultSequence)(((Range)xpathExpr).execute(xctxt));    
        }
        else if (xpathExpr instanceof XPathSequenceConstructor) {
        	resultSeq = (ResultSequence)(((XPathSequenceConstructor)xpathExpr).execute(xctxt));
        }
        else if (xpathExpr instanceof Variable) {
            XObject xObj = ((Variable)xpathExpr).execute(xctxt);

            if (xObj instanceof XMLNodeCursorImpl) {               
                DTMManager dtmMgr = (DTMManager)xctxt;

                XMLNodeCursorImpl xNodeSet = (XMLNodeCursorImpl)xObj;           
                DTMCursorIterator sourceNodes = xNodeSet.iter();

                int nextNode;

                while ((nextNode = sourceNodes.nextNode()) != DTM.NULL) {
                    XMLNodeCursorImpl xNodeSetItem = new XMLNodeCursorImpl(nextNode, dtmMgr);
                    String nodeStrValue = xNodeSetItem.str();

                    DTM dtm = dtmMgr.getDTM(nextNode);

                    if (dtm.getNodeType(nextNode) == DTM.ELEMENT_NODE) {
                        XSUntyped xsUntyped = new XSUntyped(nodeStrValue);                 
                        XslTransformEvaluationHelper.addItemToResultSequence(resultSeq, 
                                                                                  xsUntyped, true);
                    }
                    else if (dtm.getNodeType(nextNode) == DTM.ATTRIBUTE_NODE) {
                        XSUntypedAtomic xsUntypedAtomic = new XSUntypedAtomic(nodeStrValue);
                        XslTransformEvaluationHelper.addItemToResultSequence(resultSeq, 
                                                                                  xsUntypedAtomic, true);
                    }
                    else {
                        XSUntypedAtomic xsUntypedAtomic = new XSUntypedAtomic(nodeStrValue);
                        XslTransformEvaluationHelper.addItemToResultSequence(resultSeq, 
                                                                                  xsUntypedAtomic, true);
                    }
                }       
            }
            else if (xObj instanceof ResultSequence) {
                resultSeq = (ResultSequence)xObj; 
            }
            else {
                resultSeq.add(xObj);
            }
        }
        else if (xpathExpr instanceof LocPathIterator) {            
            DTMManager dtmMgr = (DTMManager)xctxt;        
            DTMCursorIterator arg0DtmIterator = xpathExpr.asIterator(xctxt, contextNode);        

            int nextNode;

            while ((nextNode = arg0DtmIterator.nextNode()) != DTM.NULL) {
                XMLNodeCursorImpl xNodeSetItem = new XMLNodeCursorImpl(nextNode, dtmMgr);            
                String nodeStrValue = xNodeSetItem.str();

                DTM dtm = dtmMgr.getDTM(nextNode);

                if (dtm.getNodeType(nextNode) == DTM.ELEMENT_NODE) {
                    XSUntyped xsUntyped = new XSUntyped(nodeStrValue);
                    resultSeq.add(xsUntyped);
                }
                else if (dtm.getNodeType(nextNode) == DTM.ATTRIBUTE_NODE) {
                    XSUntypedAtomic xsUntypedAtomic = new XSUntypedAtomic(nodeStrValue);
                    resultSeq.add(xsUntypedAtomic);
                }
                else {
                    XSUntypedAtomic xsUntypedAtomic = new XSUntypedAtomic(nodeStrValue);
                    resultSeq.add(xsUntypedAtomic);
                }                        
            }
        }
        else if (xpathExpr instanceof XPathForExpr) { 
           XObject xObj = ((XPathForExpr)xpathExpr).execute(xctxt);
           resultSeq = (ResultSequence)xObj;
        }
        else {
        	XObject xObj = xpathExpr.execute(xctxt);
        	resultSeq.add(xObj);
        }

        return resultSeq;
   }

}
