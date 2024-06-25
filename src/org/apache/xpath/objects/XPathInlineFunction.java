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
import org.apache.xpath.composite.SequenceTypeData;

/**
 * An object of this class represents, a run-time representation
 * of an XPath function item "inline function expression".
 * 
 * Ref : https://www.w3.org/TR/xpath-31/#id-inline-func
 *  
 * @author Mukul Gandhi <mukulg@apache.org>
 *   
 * @xsl.usage advanced 
 */
public class XPathInlineFunction extends XObject {

    private static final long serialVersionUID = 9219253671212483045L;
    
    private List<InlineFunctionParameter> funcParamList = new ArrayList<InlineFunctionParameter>();
    
    private String funcBodyXPathExprStr = null;
    
    private SequenceTypeData returnType = null;

    public List<InlineFunctionParameter> getFuncParamList() {
        return funcParamList;
    }

    public void setFuncParamList(List<InlineFunctionParameter> funcParamList) {
        this.funcParamList = funcParamList;
    }

    public String getFuncBodyXPathExprStr() {
        return funcBodyXPathExprStr;
    }

    public void setFuncBodyXPathExprStr(String funcBodyXPathExprStr) {
        this.funcBodyXPathExprStr = funcBodyXPathExprStr;
    }
    
    public SequenceTypeData getReturnType() {
        return returnType;
    }

    public void setReturnType(SequenceTypeData returnType) {
        this.returnType = returnType;
    }

    public void callVisitors(ExpressionOwner owner, XPathVisitor visitor) {
        // no op
    }
    
    public int getType()
    {
      return CLASS_FUNCTION_ITEM;
    }
    
}
