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

import java.util.Vector;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.objects.XObject;

/*
 * The XalanJ xpath parser, creates and populates an object of this class, 
 * as a representation of XPath 3.1 "if" conditional expression.
 * 
 * The XPath 3.1 spec, defines "if" expression with following grammar,
 * 
 * IfExpr   ::=   "if" "(" Expr ")" "then" ExprSingle "else" ExprSingle
 * 
 * Ref : https://www.w3.org/TR/xpath-31/#id-conditionals
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class IfExpr extends Expression {
    
    private static final long serialVersionUID = 4057572946055830336L;

    private String conditionalExprXPathStr;
    
    private String thenExprXPathStr;
    
    private String elseExprXPathStr;
    
    // the following two fields of this class, are used during 
    // XPath.fixupVariables(..) action as performed within object of 
    // this class.    
    private Vector fVars;    
    private int fGlobalsSize;

    public String getConditionalExprXPathStr() {
        return conditionalExprXPathStr;
    }

    public void setConditionalExprXPathStr(String conditionalExprXPathStr) {
        this.conditionalExprXPathStr = conditionalExprXPathStr;
    }

    public String getThenExprXPathStr() {
        return thenExprXPathStr;
    }

    public void setThenExprXPathStr(String thenExprXPathStr) {
        this.thenExprXPathStr = thenExprXPathStr;
    }

    public String getElseExprXPathStr() {
        return elseExprXPathStr;
    }

    public void setElseExprXPathStr(String elseExprXPathStr) {
        this.elseExprXPathStr = elseExprXPathStr;
    }

    @Override
    public void callVisitors(ExpressionOwner owner, XPathVisitor visitor) {
       // no op       
    }

    @Override
    public XObject execute(XPathContext xctxt) throws TransformerException {
       XObject evalResult = null;
       
       SourceLocator srcLocator = xctxt.getSAXLocator();
       
       int contextNode = xctxt.getContextNode();
       
       XPath conditionlExprXpath = new XPath(conditionalExprXPathStr, srcLocator, null, 
                                                                            XPath.SELECT, null);
       if (fVars != null) {
          conditionlExprXpath.fixupVariables(fVars, fGlobalsSize);
       }
       
       XObject conditionalXpathExprResult = conditionlExprXpath.execute(xctxt, contextNode, null);
       
       if (conditionalXpathExprResult.bool()) {
           XPath thenExprXpath = new XPath(thenExprXPathStr, srcLocator, null, XPath.SELECT, null);
           if (fVars != null) {
              thenExprXpath.fixupVariables(fVars, fGlobalsSize);
           }
           
           evalResult = thenExprXpath.execute(xctxt, contextNode, null);
       }
       else {
           XPath elseExprXpath = new XPath(elseExprXPathStr, srcLocator, null, XPath.SELECT, null);
           if (fVars != null) {
              elseExprXpath.fixupVariables(fVars, fGlobalsSize);
           }
           
           evalResult = elseExprXpath.execute(xctxt, contextNode, null);
       }
       
       return evalResult;
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

}
