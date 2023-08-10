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

import java.util.List;
import java.util.Map;

import javax.xml.transform.SourceLocator;

import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.XMLNSDecl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.dtm.DTMManager;
import org.apache.xml.utils.QName;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.axes.LocPathIterator;
import org.apache.xpath.objects.InlineFunction;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.operations.Range;
import org.apache.xpath.operations.Variable;
import org.apache.xpath.xs.types.XSUntyped;
import org.apache.xpath.xs.types.XSUntypedAtomic;

/**
 * Implementation of the fold-left() function.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
/*
 * fn:fold-left is one of XPath 3.1's higher-order function
 * (ref, https://www.w3.org/TR/xpath-functions-31/#higher-order-functions).
 * 
 * The XPath function fn:fold-left has following signature, and definition,
 * 
 * fn:fold-left($seq as item()*,
                $zero as item()*,
                $f as function(item()*, item()) as item()*) as item()*
   
 * The fn:fold-left function, processes the supplied sequence from left to right, 
 * applying the supplied function repeatedly to each item in turn, together
 * with an accumulated result value.
 */
public class FuncFoldLeft extends Function3Args {
    
    private static final long serialVersionUID = -3772850377799360556L;

    public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
    {
        XObject result = null;
        
        SourceLocator srcLocator = xctxt.getSAXLocator();
        
        int contextNode = xctxt.getContextNode();
        
        ResultSequence foldLeftFirstArgResultSeq = null;
        
        if (m_arg0 instanceof Range) {
           foldLeftFirstArgResultSeq = (ResultSequence)(((Range)m_arg0).execute(xctxt));    
        }
        else if (m_arg0 instanceof Variable) {
           XObject xObj = ((Variable)m_arg0).execute(xctxt);
           
           if (xObj instanceof XNodeSet) {
               foldLeftFirstArgResultSeq = new ResultSequence();
               
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
                     XslTransformEvaluationHelper.addItemToResultSequence(foldLeftFirstArgResultSeq, 
                                                                                            xsUntyped, true);
                  }
                  else if (dtm.getNodeType(nextNodeDtmHandle) == DTM.ATTRIBUTE_NODE) {
                     XSUntypedAtomic xsUntypedAtomic = new XSUntypedAtomic(nodeStrValue);
                     XslTransformEvaluationHelper.addItemToResultSequence(foldLeftFirstArgResultSeq, 
                                                                                            xsUntypedAtomic, true);
                  }
                  else {
                     XSUntypedAtomic xsUntypedAtomic = new XSUntypedAtomic(nodeStrValue);
                     XslTransformEvaluationHelper.addItemToResultSequence(foldLeftFirstArgResultSeq, 
                                                                                            xsUntypedAtomic, true);
                  }
               }       
           }
           else if (xObj instanceof ResultSequence) {
              foldLeftFirstArgResultSeq = (ResultSequence)xObj; 
           }
           else {
              // REVISIT
              foldLeftFirstArgResultSeq = new ResultSequence();
              foldLeftFirstArgResultSeq.add(xObj);
           }
        }
        else if (m_arg0 instanceof LocPathIterator) {
            foldLeftFirstArgResultSeq = new ResultSequence();
            
            DTMManager dtmMgr = (DTMManager)xctxt;        
            DTMIterator arg0DtmIterator = m_arg0.asIterator(xctxt, contextNode);        
            
            int nextNodeDtmHandle;
            
            while ((nextNodeDtmHandle = arg0DtmIterator.nextNode()) != DTM.NULL) {
                XNodeSet xNodeSetItem = new XNodeSet(nextNodeDtmHandle, dtmMgr);            
                String nodeStrValue = xNodeSetItem.str();
                
                DTM dtm = dtmMgr.getDTM(nextNodeDtmHandle);
                
                if (dtm.getNodeType(nextNodeDtmHandle) == DTM.ELEMENT_NODE) {
                   XSUntyped xsUntyped = new XSUntyped(nodeStrValue);
                   foldLeftFirstArgResultSeq.add(xsUntyped);
                }
                else if (dtm.getNodeType(nextNodeDtmHandle) == DTM.ATTRIBUTE_NODE) {
                   XSUntypedAtomic xsUntypedAtomic = new XSUntypedAtomic(nodeStrValue);
                   foldLeftFirstArgResultSeq.add(xsUntypedAtomic);
                }
                else {
                   XSUntypedAtomic xsUntypedAtomic = new XSUntypedAtomic(nodeStrValue);
                   foldLeftFirstArgResultSeq.add(xsUntypedAtomic);
                }                        
            }
        }
        
        XObject foldLeftBaseVal = m_arg1.execute(xctxt);
        
        InlineFunction foldLeftThirdArg = null;
        
        if (m_arg2 instanceof InlineFunction) {
           foldLeftThirdArg = (InlineFunction)m_arg2;
           
           String inlineFnXPathStr = foldLeftThirdArg.getFuncBodyXPathExprStr();
           List<String> funcParamNameList = foldLeftThirdArg.getFuncParamNameList();
           
           if (funcParamNameList.size() == 2) {              
              String funcItemFirstArgName = funcParamNameList.get(0);
              String funcItemSecondArgName = funcParamNameList.get(1);
              
              ElemTemplateElement elemTemplateElement = (ElemTemplateElement)xctxt.getNamespaceContext();
              List<XMLNSDecl> prefixTable = null;
              if (elemTemplateElement != null) {
                  prefixTable = (List<XMLNSDecl>)elemTemplateElement.getPrefixTable();
              }
              
              if (prefixTable != null) {
                 inlineFnXPathStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(
                                                                                        inlineFnXPathStr, prefixTable);
              }
              
              XPath inlineFuncXPath = new XPath(inlineFnXPathStr, srcLocator, xctxt.getNamespaceContext(), 
                                                                                              XPath.SELECT, null);              
              for (int idx = 0; idx < foldLeftFirstArgResultSeq.size(); idx++) {
                 Map<QName, XObject> inlineFunctionVarMap = xctxt.getXPathVarMap();
                 
                 inlineFunctionVarMap.put(new QName(funcItemSecondArgName), foldLeftFirstArgResultSeq.item(idx));
              
                 if (idx == 0) {                    
                    inlineFunctionVarMap.put(new QName(funcItemFirstArgName), foldLeftBaseVal);
                 }
                 else {
                    inlineFunctionVarMap.put(new QName(funcItemFirstArgName), result);                   
                 }
                 
                 result = inlineFuncXPath.execute(xctxt, contextNode, xctxt.getNamespaceContext());
                 
                 // Reset the function item argument variables
                 inlineFunctionVarMap.put(new QName(funcItemFirstArgName), null);
                 inlineFunctionVarMap.put(new QName(funcItemSecondArgName), null);
              }
           }
           else {
              result = new ResultSequence();  
           }
        }
        else {
           result = new ResultSequence(); 
        }
        
        return result;
    }

}
