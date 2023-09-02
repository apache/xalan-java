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

import javax.xml.transform.TransformerException;

import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.objects.XObject;

/**
 * An object of this class is, populated by an XPath parser
 * to represent a particular XPath sequence type expression.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XPathSequenceTypeExpr extends Expression {

    private static final long serialVersionUID = -7060682923984508436L;
    
    private int fSequenceType;
    
    private int fItemTypeOccurrenceIndicator;

    @Override
    public XObject execute(XPathContext xctxt) throws TransformerException {
       SequenceTypeData sequenceTypeData = new SequenceTypeData();
       
       sequenceTypeData.setSequenceType(fSequenceType);
       sequenceTypeData.setItemTypeOccurrenceIndicator(fItemTypeOccurrenceIndicator);
       
       return sequenceTypeData;
    }

    @Override
    public void fixupVariables(Vector vars, int globalsSize) {
       // NO OP  
    }

    @Override
    public boolean deepEquals(Expression expr) {
       // NO OP
       return false;
    }

    public int getSequenceType() {
        return fSequenceType;
    }

    public void setSequenceType(int sequenceType) {
        this.fSequenceType = sequenceType;
    }

    public int getItemTypeOccurrenceIndicator() {
        return fItemTypeOccurrenceIndicator;
    }

    public void setItemTypeOccurrenceIndicator(int itemTypeOccurrenceIndicator) {
        this.fItemTypeOccurrenceIndicator = itemTypeOccurrenceIndicator;
    }
    
    @Override
    public void callVisitors(ExpressionOwner owner, XPathVisitor visitor) {
       // NO OP        
    }

}
