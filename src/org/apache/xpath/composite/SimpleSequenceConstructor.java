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
package org.apache.xpath.composite;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.XMLNSDecl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.dtm.DTMManager;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XObject;

/*
 * The XalanJ XPath parser, creates and populates an object of this class, 
 * to help produce an XDM sequence, from an XPath expression using the
 * comma operator.
 * 
 * Ref : https://www.w3.org/TR/xpath-31/#construct_seq
 *       
 *       The XPath grammar fragment, for such a sequence constructor
 *       is following,
 *       
 *       Expr    ::=   ExprSingle ("," ExprSingle)*
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class SimpleSequenceConstructor extends Expression {

    private static final long serialVersionUID = -5141131877741250613L;
    
    private List<String> sequenceConstructorXPathParts = new ArrayList<String>();
    
    // the following two fields of this class, are used during 
    // XPath.fixupVariables(..) action as performed within object of 
    // this class.    
    private Vector fVars;    
    private int fGlobalsSize;
    
    @Override
    public void callVisitors(ExpressionOwner owner, XPathVisitor visitor) {
       // no op
    }

    @Override
    public XObject execute(XPathContext xctxt) throws TransformerException {
        
        ResultSequence resultSeq = new ResultSequence();
        
        SourceLocator srcLocator = xctxt.getSAXLocator();
        
        int contextNode = xctxt.getContextNode();
        
        ElemTemplateElement elemTemplateElement = (ElemTemplateElement)xctxt.getNamespaceContext();
        List<XMLNSDecl> prefixTable = null;
        if (elemTemplateElement != null) {
            prefixTable = (List<XMLNSDecl>)elemTemplateElement.getPrefixTable();
        }
        
        // We evaluate below all the, XPath expression parts within the list 
        // 'sequenceConstructorXPathParts', and concatenate the sequences resulting
        // from each of them, to get the final result sequence that is returned by
        // this method.
        for (int idx = 0; idx < sequenceConstructorXPathParts.size(); idx++) {
           String xpathExprStr = sequenceConstructorXPathParts.get(idx);
           
           if (prefixTable != null) {
              xpathExprStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(xpathExprStr, 
                                                                                                     prefixTable);
           }
           
           XPath xpathObj = new XPath(xpathExprStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
           if (fVars != null) {
              xpathObj.fixupVariables(fVars, fGlobalsSize);
           }

           XObject xPathExprPartResult = xpathObj.execute(xctxt, contextNode, xctxt.getNamespaceContext());
           
           if (xPathExprPartResult instanceof XNodeSet) {
               DTMManager dtmMgr = (DTMManager)xctxt;
               
               XNodeSet xNodeSet = (XNodeSet)xPathExprPartResult;
               DTMIterator sourceNodes = xNodeSet.iter();
               
               int nextNodeDtmHandle;
               
               while ((nextNodeDtmHandle = sourceNodes.nextNode()) != DTM.NULL) {
                  XNodeSet xNodeSetItem = new XNodeSet(nextNodeDtmHandle, dtmMgr);
                  resultSeq.add(xNodeSetItem);
               }               
            }
            else if (xPathExprPartResult instanceof ResultSequence) {
               ResultSequence inpResultSeq = (ResultSequence)xPathExprPartResult; 
               for (int idx1 = 0; idx1 < inpResultSeq.size(); idx1++) {
                  XObject xObj = inpResultSeq.item(idx1);
                  resultSeq.add(xObj);                 
               }
            }
            else {
               // We're assuming here that, an input value is an xdm singleton 
               // item.
               resultSeq.add(xPathExprPartResult);               
            }
        }
        
        return resultSeq;
    }

    @Override
    public void fixupVariables(Vector vars, int globalsSize) {
        fVars = (Vector)(vars.clone());
        fGlobalsSize = globalsSize;
    }

    @Override
    public boolean deepEquals(Expression expr) {
        return false;
    }

    public List<String> getSequenceConstructorXPathParts() {
        return sequenceConstructorXPathParts;
    }

    public void setSequenceConstructorXPathParts(List<String> 
                                                        sequenceConstructorXpathParts) {
        this.sequenceConstructorXPathParts = sequenceConstructorXpathParts;
    }

}
