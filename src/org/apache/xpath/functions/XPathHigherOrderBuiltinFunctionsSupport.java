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

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.dtm.DTMManager;
import org.apache.xpath.Expression;
import org.apache.xpath.XPathContext;
import org.apache.xpath.axes.LocPathIterator;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.operations.Range;
import org.apache.xpath.operations.Variable;
import org.apache.xpath.xs.types.XSUntyped;
import org.apache.xpath.xs.types.XSUntypedAtomic;

/**
 * This class provides few utility methods, to help with XPath 3.1 
 * built-in higher order function evaluations.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XPathHigherOrderBuiltinFunctionsSupport extends Function3Args {
    
    private static final long serialVersionUID = 5970365027214826130L;

    /**
     * This method, evaluates an XPath expression to produce an xdm sequence, that
     * can be used as argument to an XPath higher order function call.
     *  
     * @param xpathExpr      an XPath expression, that is evaluated by this method
     * @param xctxt          an XPath context object
     * 
     * @return               an xdm sequence produced by this method.
     * 
     * @throws javax.xml.transform.TransformerException
     */
    protected ResultSequence constructXDMSequenceFromXPathExpression(Expression xpathExpr, XPathContext xctxt) 
                                                                                       throws javax.xml.transform.TransformerException {        
        ResultSequence resultSeq = new ResultSequence();
        
        int contextNode = xctxt.getContextNode();

        if (xpathExpr instanceof Range) {
            resultSeq = (ResultSequence)(((Range)xpathExpr).execute(xctxt));    
        }
        else if (xpathExpr instanceof Variable) {
            XObject xObj = ((Variable)xpathExpr).execute(xctxt);

            if (xObj instanceof XNodeSet) {               
                DTMManager dtmMgr = (DTMManager)xctxt;

                XNodeSet xNodeSet = (XNodeSet)xObj;           
                DTMIterator sourceNodes = xNodeSet.iter();

                int nextNodeDtmHandle;

                while ((nextNodeDtmHandle = sourceNodes.nextNode()) != DTM.NULL) {
                    XNodeSet xNodeSetItem = new XNodeSet(nextNodeDtmHandle, dtmMgr);
                    String nodeStrValue = xNodeSetItem.str();

                    DTM dtm = dtmMgr.getDTM(nextNodeDtmHandle);

                    if (dtm.getNodeType(nextNodeDtmHandle) == DTM.ELEMENT_NODE) {
                        XSUntyped xsUntyped = new XSUntyped(nodeStrValue);                 
                        XslTransformEvaluationHelper.addItemToResultSequence(resultSeq, 
                                                                                  xsUntyped, true);
                    }
                    else if (dtm.getNodeType(nextNodeDtmHandle) == DTM.ATTRIBUTE_NODE) {
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
            DTMIterator arg0DtmIterator = xpathExpr.asIterator(xctxt, contextNode);        

            int nextNodeDtmHandle;

            while ((nextNodeDtmHandle = arg0DtmIterator.nextNode()) != DTM.NULL) {
                XNodeSet xNodeSetItem = new XNodeSet(nextNodeDtmHandle, dtmMgr);            
                String nodeStrValue = xNodeSetItem.str();

                DTM dtm = dtmMgr.getDTM(nextNodeDtmHandle);

                if (dtm.getNodeType(nextNodeDtmHandle) == DTM.ELEMENT_NODE) {
                    XSUntyped xsUntyped = new XSUntyped(nodeStrValue);
                    resultSeq.add(xsUntyped);
                }
                else if (dtm.getNodeType(nextNodeDtmHandle) == DTM.ATTRIBUTE_NODE) {
                    XSUntypedAtomic xsUntypedAtomic = new XSUntypedAtomic(nodeStrValue);
                    resultSeq.add(xsUntypedAtomic);
                }
                else {
                    XSUntypedAtomic xsUntypedAtomic = new XSUntypedAtomic(nodeStrValue);
                    resultSeq.add(xsUntypedAtomic);
                }                        
            }
        }

        return resultSeq;
   }

}
