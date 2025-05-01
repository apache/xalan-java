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
package org.apache.xpath.functions.array;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.XMLNSDecl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.utils.QName;
import org.apache.xpath.Expression;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.FunctionMultiArgs;
import org.apache.xpath.functions.WrongNumberArgsException;
import org.apache.xpath.objects.InlineFunctionParameter;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathArray;
import org.apache.xpath.objects.XPathInlineFunction;
import org.apache.xpath.operations.Variable;
import org.apache.xpath.res.XPATHErrorResources;

/**
 * Implementation of the array:sort() function.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncArraySort extends FunctionMultiArgs
{
    
	private static final long serialVersionUID = 4848739188248412283L;
	
	/**
     * The number of arguments passed to the array:sort function 
     * call.
     */
    private int numOfArgs = 0;

    /**
     * Evaluate array:sort function call. The function must return a valid object.
     * 
     * @param xctxt The current execution context.
     * @return A valid XObject.
     *
     * @throws javax.xml.transform.TransformerException
     */
    public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
    {
        XPathArray evalResult = new XPathArray();
        
        SourceLocator srcLocator = xctxt.getSAXLocator();
        
        Expression arg0 = m_arg0;        
        Expression arg1 = null;        
        Expression arg2 = null;
        
        List<XdmItemWithSortKeyValue> inpList = new ArrayList<XdmItemWithSortKeyValue>();
        
        String collationUri = xctxt.getDefaultCollation();
        
        if ((numOfArgs == 2) || (numOfArgs == 3)) {
           arg1 = m_arg1;
           
           XObject XObjArg1 = arg1.execute(xctxt);
            
           if ((XObjArg1 instanceof ResultSequence) && 
                                                (((ResultSequence)XObjArg1).size() == 0)) {
        	  // The 2nd argument to function call array:sort, is an empty sequence
              collationUri = xctxt.getDefaultCollation();   
           }
           else {
              // Collation uri was, explicitly provided during the function call array:sort               
              collationUri = XslTransformEvaluationHelper.getStrVal(XObjArg1); 
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
 
        if (xObjArg0 instanceof XPathArray) {
           XPathArray arg0Arr = (XPathArray)xObjArg0;
           
           Map<QName, XObject> inlineFunctionVarMap = xctxt.getXPathVarMap();
           
           ElemTemplateElement elemTemplateElement = (ElemTemplateElement)xctxt.getNamespaceContext();
           List<XMLNSDecl> prefixTable = null;
           if (elemTemplateElement != null) {
               prefixTable = (List<XMLNSDecl>)elemTemplateElement.getPrefixTable();
           }
           
           for (int idx = 0; idx < arg0Arr.size(); idx++) {
              XObject inpArrItem = arg0Arr.get(idx);
              if (numOfArgs == 3) {                                   
                 XPathInlineFunction arg2InlineFunc = (XPathInlineFunction)arg2;
                 
                 List<InlineFunctionParameter> funcParamList = arg2InlineFunc.getFuncParamList();
                 inlineFunctionVarMap.put(new QName((funcParamList.get(0)).getParamName()), inpArrItem);
                 
                 String sortKeyXPathStr = arg2InlineFunc.getFuncBodyXPathExprStr();
                 
                 if (prefixTable != null) {
                     sortKeyXPathStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(sortKeyXPathStr, 
                                                                                                         prefixTable);
                 }
                 
                 XPath sortKeyXPathExpr = new XPath(sortKeyXPathStr, srcLocator, xctxt.getNamespaceContext(), 
                                                                                                     XPath.SELECT, null);
                 XObject sortKeyVal = sortKeyXPathExpr.execute(xctxt, xctxt.getContextNode(), xctxt.getNamespaceContext());
                 
                 // Reset the function item argument reference value
                 inlineFunctionVarMap.put(new QName((funcParamList.get(0)).getParamName()), null);
                 
                 inpList.add(new XdmItemWithSortKeyValue(inpArrItem, sortKeyVal));
              }
              else if ((numOfArgs == 1) || (numOfArgs == 2)) {
                 inpList.add(new XdmItemWithSortKeyValue(inpArrItem, inpArrItem));  
              }
           }
           
           SortComparator sortComparator = new SortComparator(collationUri);
           
           // Input list is mutated with sorted result, with this function call
           inpList.sort(sortComparator);
           
           if (sortComparator.getErrMessage() != null) {
              throw new javax.xml.transform.TransformerException(sortComparator.getErrMessage(), srcLocator); 
           }
               
           for (int idx = 0; idx < inpList.size(); idx++) {
              XdmItemWithSortKeyValue listItem = inpList.get(idx);
              evalResult.add(listItem.getItem()); 
           }           
        }
        else {
           throw new TransformerException("FORG0006 : The 1st argument provided with function call "
           		                                        + "array:sort, is not an array.", srcLocator);
        }
        
        return evalResult;
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
     * xdm item and its corresponding sort key value.
     */
    class XdmItemWithSortKeyValue {
        
       private XObject item;
       
       private XObject sortKeyVal;
       
       public XdmItemWithSortKeyValue(XObject item, XObject sortKeyVal) {
          this.item = item;
          this.sortKeyVal = sortKeyVal; 
       }

       public XObject getItem() {
          return item;
       }

       public void setItem(XObject item) {
          this.item = item;
       }

       public XObject getSortKeyVal() {
          return sortKeyVal;
       }

       public void setSortKeyVal(XObject sortKeyVal) {
          this.sortKeyVal = sortKeyVal;
       }

    }
    
    /*
     * An object of this class, supports sorting of an xdm input 
     * array, when using array:sort function call. 
     */
    private class SortComparator implements Comparator<XdmItemWithSortKeyValue> {
        
        private String collationUri;
        
        private String errMesg = null;
        
        /*
         * The class constructor.
         */
        public SortComparator(String collationUri) {
           this.collationUri = collationUri;
        }

        @Override
        public int compare(XdmItemWithSortKeyValue obj1, XdmItemWithSortKeyValue obj2) {           
           int comparisonResult = 0;
           
           XObject sortKeyVal1 = obj1.getSortKeyVal();
           XObject sortKeyVal2 = obj2.getSortKeyVal();
           
           try {
              if (sortKeyVal1.vcLessThan(sortKeyVal2, null, collationUri, true)) {
                 comparisonResult = -1;  
              }
              else if (sortKeyVal1.vcGreaterThan(sortKeyVal2, null, collationUri, true)) {
                 comparisonResult = 1; 
              }
           }
           catch (javax.xml.transform.TransformerException ex) {
              errMesg = ex.getMessage();
           }
           
           return comparisonResult;           
        }
        
        public String getErrMessage() {
           return errMesg; 
        }
    }
    
}
