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
/*
 * $Id$
 */
package org.apache.xpath.functions;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.xml.transform.SourceLocator;

import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.XMLNSDecl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.dtm.DTMManager;
import org.apache.xml.utils.QName;
import org.apache.xpath.Expression;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.InlineFunction;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.operations.Variable;
import org.apache.xpath.res.XPATHErrorResources;

/**
 * Implementation of the sort() function.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
/*
 * fn:sort is one of XPath 3.1's higher-order function
 * (ref, https://www.w3.org/TR/xpath-functions-31/#higher-order-functions).
 *  
 * The fn:sort function, sorts a supplied sequence, based on the value 
 * of a sort key supplied as a function.
 */
public class FuncSort extends FunctionMultiArgs
{

    private static final long serialVersionUID = -7037457505611880167L;
    
    /**
     * The number of arguments passed to the fn:sort function 
     * call.
     */
    private int numOfArgs = 0;

    /**
     * Execute the function. The function must return a valid object.
     * 
     * @param xctxt The current execution context.
     * @return A valid XObject.
     *
     * @throws javax.xml.transform.TransformerException
     */
    public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
    {
        ResultSequence evalResultSeq = new ResultSequence();
        
        SourceLocator srcLocator = xctxt.getSAXLocator();
        
        Expression arg0 = m_arg0;        
        Expression arg1 = null;        
        Expression arg2 = null;
        
        List<InpSeqItemWithSortKeyValue> inpSeqItemWithSortKeyValueList = new 
                                                                   ArrayList<InpSeqItemWithSortKeyValue>();
        
        if ((numOfArgs == 3) || (numOfArgs == 2)) {
           arg1 = m_arg1;           
           XObject XObjArg1 = arg1.execute(xctxt);
            
           if (!((XObjArg1 instanceof ResultSequence) && 
                                                (((ResultSequence)XObjArg1).size() == 0))) {
               throw new javax.xml.transform.TransformerException("FORG0006 : The collation corresponding to supplied value of "
                                                                                     + "second argument to function call fn:sort, is "
                                                                                     + "not supported.", srcLocator);  
           }
           
           arg2 = m_arg2;
        }
        
        XObject xObjArg0 = null;
        
        if (arg0 instanceof Variable) {
           xObjArg0 = ((Variable)arg0).execute(xctxt);           
        }
        else {
           xObjArg0 = arg0.execute(xctxt); 
        }                
        
        if (xObjArg0 instanceof XNodeSet) {
           DTMManager dtmMgr = (DTMManager)xctxt;
            
           XNodeSet xNodeSet = (XNodeSet)xObjArg0;           
           DTMIterator sourceNodes = xNodeSet.iter();
           
           int nextNodeDtmHandle;
           
           Map<QName, XObject> inlineFunctionVarMap = xctxt.getXPathVarMap();
           
           ElemTemplateElement elemTemplateElement = (ElemTemplateElement)xctxt.getNamespaceContext();
           List<XMLNSDecl> prefixTable = null;
           if (elemTemplateElement != null) {
               prefixTable = (List<XMLNSDecl>)elemTemplateElement.getPrefixTable();
           }

           while ((nextNodeDtmHandle = sourceNodes.nextNode()) != DTM.NULL) {
              XNodeSet xNodeSetItem = new XNodeSet(nextNodeDtmHandle, dtmMgr);
              
              if (numOfArgs == 3) {                                   
                  InlineFunction arg2InlineFunc = (InlineFunction)arg2;
                  
                  List<String> funcParamNameList = arg2InlineFunc.getFuncParamNameList();
                  inlineFunctionVarMap.put(new QName(funcParamNameList.get(0)), xNodeSetItem);
                  
                  String sortKeyXPathStr = arg2InlineFunc.getFuncBodyXPathExprStr();
                  
                  if (prefixTable != null) {
                      sortKeyXPathStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(sortKeyXPathStr, 
                                                                                                                   prefixTable);
                  }
                  
                  XPath sortKeyXPathExpr = new XPath(sortKeyXPathStr, srcLocator, xctxt.getNamespaceContext(), 
                                                                                       XPath.SELECT, null);
                  XObject sortKeyVal = sortKeyXPathExpr.execute(xctxt, nextNodeDtmHandle, xctxt.getNamespaceContext());
                  
                  // reset the function item argument reference value
                  inlineFunctionVarMap.put(new QName(funcParamNameList.get(0)), null);
                  
                  inpSeqItemWithSortKeyValueList.add(new InpSeqItemWithSortKeyValue(xNodeSetItem, sortKeyVal));
               }
               else if ((numOfArgs == 2) || (numOfArgs == 1)) {
                  inpSeqItemWithSortKeyValueList.add(new InpSeqItemWithSortKeyValue(xNodeSetItem, xNodeSetItem));
               }
           }
           
           inpSeqItemWithSortKeyValueList.sort(new FnSortComparator(srcLocator));
           
           for (int idx = 0; idx < inpSeqItemWithSortKeyValueList.size(); idx++) {
              InpSeqItemWithSortKeyValue inpSeqItemWithSortKeyValue = inpSeqItemWithSortKeyValueList.get(idx);
              evalResultSeq.add(inpSeqItemWithSortKeyValue.getInpSeqItem()); 
           }
           
        } 
        else if (xObjArg0 instanceof ResultSequence) {
           ResultSequence arg0ResultSeq = (ResultSequence)xObjArg0;
           
           Map<QName, XObject> inlineFunctionVarMap = xctxt.getXPathVarMap();
           
           ElemTemplateElement elemTemplateElement = (ElemTemplateElement)xctxt.getNamespaceContext();
           List<XMLNSDecl> prefixTable = null;
           if (elemTemplateElement != null) {
               prefixTable = (List<XMLNSDecl>)elemTemplateElement.getPrefixTable();
           }
           
           for (int idx = 0; idx < arg0ResultSeq.size(); idx++) {
              XObject inputSeqItem = arg0ResultSeq.item(idx);
              if (numOfArgs == 3) {                                   
                 InlineFunction arg2InlineFunc = (InlineFunction)arg2;
                 
                 List<String> funcParamNameList = arg2InlineFunc.getFuncParamNameList();
                 inlineFunctionVarMap.put(new QName(funcParamNameList.get(0)), inputSeqItem);
                 
                 String sortKeyXPathStr = arg2InlineFunc.getFuncBodyXPathExprStr();
                 
                 if (prefixTable != null) {
                     sortKeyXPathStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(sortKeyXPathStr, 
                                                                                                         prefixTable);
                 }
                 
                 XPath sortKeyXPathExpr = new XPath(sortKeyXPathStr, srcLocator, xctxt.getNamespaceContext(), 
                                                                                                     XPath.SELECT, null);
                 XObject sortKeyVal = sortKeyXPathExpr.execute(xctxt, xctxt.getContextNode(), xctxt.getNamespaceContext());
                 
                 // reset the function item argument reference value
                 inlineFunctionVarMap.put(new QName(funcParamNameList.get(0)), null);
                 
                 inpSeqItemWithSortKeyValueList.add(new InpSeqItemWithSortKeyValue(inputSeqItem, sortKeyVal));
              }
              else if ((numOfArgs == 2) || (numOfArgs == 1)) {
                 inpSeqItemWithSortKeyValueList.add(new InpSeqItemWithSortKeyValue(inputSeqItem, inputSeqItem));  
              }
           }
           
           inpSeqItemWithSortKeyValueList.sort(new FnSortComparator(srcLocator));
               
           for (int idx = 0; idx < inpSeqItemWithSortKeyValueList.size(); idx++) {
              InpSeqItemWithSortKeyValue inpSeqItemWithSortKeyValue = inpSeqItemWithSortKeyValueList.get(idx);
              evalResultSeq.add(inpSeqItemWithSortKeyValue.getInpSeqItem()); 
           }           
        }
        else {           
           evalResultSeq.add(xObjArg0); 
        }
        
        return evalResultSeq;
    }
    
    /**
     * Check that the number of arguments passed to this function is correct.
     *
     * @param argNum The number of arguments that is being passed to the function.
     *
     * @throws WrongNumberArgsException
     */
    public void checkNumberArgs(int argNum) throws WrongNumberArgsException
    {
       if (!(argNum > 0 && argNum <= 3)) {
          reportWrongNumberArgs();
       }
       else {
          numOfArgs = argNum;   
       }
    }

    /**
     * Constructs and throws a WrongNumberArgException with the appropriate
     * message for this function object.
     *
     * @throws WrongNumberArgsException
     */
    protected void reportWrongNumberArgs() throws WrongNumberArgsException {
        throw new WrongNumberArgsException(XSLMessages.createXPATHMessage(
                                                                     XPATHErrorResources.ER_ONE_TWO_OR_THREE, 
                                                                     null));
    }
    
    /*
     * An object of this class, contains a pair of an input 
     * sequence xdm item and its corresponding sort key value.
     */
    class InpSeqItemWithSortKeyValue {
        
       private XObject inpSeqItem;
       
       private XObject inpSeqItemSortKeyVal;
       
       public InpSeqItemWithSortKeyValue(XObject inpSeqItem, 
                                                      XObject inpSeqItemSortKeyVal) {
          this.inpSeqItem = inpSeqItem;
          this.inpSeqItemSortKeyVal = inpSeqItemSortKeyVal; 
       }

       public XObject getInpSeqItem() {
          return inpSeqItem;
       }

       public void setInpSeqItem(XObject inpSeqItem) {
          this.inpSeqItem = inpSeqItem;
       }

       public XObject getInpSeqItemSortKeyVal() {
          return inpSeqItemSortKeyVal;
       }

       public void setInpSeqItemSortKeyVal(XObject inpSeqItemSortKeyVal) {
          this.inpSeqItemSortKeyVal = inpSeqItemSortKeyVal;
       }

    }
    
    /*
     * An object of this class, is used to support sorting of an 
     * xdm input sequence. 
     */
    class FnSortComparator implements Comparator {
        
        private SourceLocator srcLocator;
        
        /*
         * The class constructor.
         */
        public FnSortComparator(SourceLocator srcLocator) {
           this.srcLocator = srcLocator;    
        }

        @Override
        public int compare(Object obj1, Object obj2) {
           int comparisonResult = 0;
           
           XObject sortKeyVal1 = ((InpSeqItemWithSortKeyValue)obj1).getInpSeqItemSortKeyVal();
           XObject sortKeyVal2 = ((InpSeqItemWithSortKeyValue)obj2).getInpSeqItemSortKeyVal();
           
           try {
              if (sortKeyVal1.vcGreaterThan(sortKeyVal2, null, true)) {
                 comparisonResult = 1;  
              }
              else if (sortKeyVal1.vcLessThan(sortKeyVal2, null, false)) {
                 comparisonResult = -1; 
              }
           }
           catch (javax.xml.transform.TransformerException ex) {
              // NO OP
              // REVISIT
           }
           
           return comparisonResult;           
        }        
    }
    
}
