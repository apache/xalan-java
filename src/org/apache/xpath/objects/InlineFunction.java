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
package org.apache.xpath.objects;

import java.util.ArrayList;
import java.util.List;

import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPathVisitor;

/*
 * The XalanJ xpath parser, creates and populates an object of this 
 * class, as a representation of XPath 3.1 function item "inline 
 * function" definition within XPath expressions.
 * 
 * The XPath 3.1 spec, defines function item "inline function" XPath 
 * expressions with following grammar,

   InlineFunctionExpr        ::=  "function" "(" ParamList? ")" ("as" SequenceType)? FunctionBody

   ParamList                 ::=   Param ("," Param)*

   Param                     ::=   "$" EQName TypeDeclaration?

   FunctionBody              ::=   EnclosedExpr

   EnclosedExpr              ::=   "{" Expr? "}"
   
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 *   
 * @xsl.usage advanced 
 */
public class InlineFunction extends XObject {

    private static final long serialVersionUID = 9219253671212483045L;
    
    private List<String> funcParamNameList = new ArrayList<String>();
    
    private String funcBodyXPathExprStr = null;

    public List<String> getFuncParamNameList() {
        return funcParamNameList;
    }

    public void setFuncParamNameList(List<String> funcParamNameList) {
        this.funcParamNameList = funcParamNameList;
    }

    public String getFuncBodyXPathExprStr() {
        return funcBodyXPathExprStr;
    }

    public void setFuncBodyXPathExprStr(String funcBodyXPathExprStr) {
        this.funcBodyXPathExprStr = funcBodyXPathExprStr;
    }
    
    public void callVisitors(ExpressionOwner owner, XPathVisitor visitor)
    {
        // no op
    }
    
}
