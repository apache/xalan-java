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

import org.apache.xerces.xs.XSTypeDefinition;
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
    
    private int builtInSequenceType;
    
    private XSTypeDefinition xsTypeDefinition;
    
    private int itemTypeOccurrenceIndicator;
    
    private SequenceTypeKindTest sequenceTypeKindTest;
    
    private SequenceTypeFunctionTest sequenceTypeFunctionTest;
    
    private SequenceTypeMapTest sequenceTypeMapTest;
    
    private SequenceTypeArrayTest sequenceTypeArrayTest;

    @Override
    public XObject execute(XPathContext xctxt) throws TransformerException {
       SequenceTypeData sequenceTypeData = new SequenceTypeData();
       
       sequenceTypeData.setBuiltInSequenceType(builtInSequenceType);
       sequenceTypeData.setXsTypeDefinition(xsTypeDefinition);
       sequenceTypeData.setSequenceTypeKindTest(sequenceTypeKindTest);       
       sequenceTypeData.setItemTypeOccurrenceIndicator(itemTypeOccurrenceIndicator);
       sequenceTypeData.setSequenceTypeFunctionTest(sequenceTypeFunctionTest);
       sequenceTypeData.setSequenceTypeMapTest(sequenceTypeMapTest);
       sequenceTypeData.setSequenceTypeArrayTest(sequenceTypeArrayTest);
       
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

    public int getBuiltInSequenceType() {
        return builtInSequenceType;
    }

    public void setBuiltInSequenceType(int sequenceType) {
        this.builtInSequenceType = sequenceType;
    }

    public XSTypeDefinition getXsSequenceTypeDefinition() {
		return xsTypeDefinition;
	}

	public void setXsSequenceTypeDefinition(XSTypeDefinition xsSequenceTypeDefinition) {
		this.xsTypeDefinition = xsSequenceTypeDefinition;
	}

	public int getItemTypeOccurrenceIndicator() {
        return itemTypeOccurrenceIndicator;
    }

    public void setItemTypeOccurrenceIndicator(int itemTypeOccurrenceIndicator) {
        this.itemTypeOccurrenceIndicator = itemTypeOccurrenceIndicator;
    }
    
    public SequenceTypeKindTest getSequenceTypeKindTest() {
        return sequenceTypeKindTest;
    }

    public void setSequenceTypeKindTest(SequenceTypeKindTest sequenceTypeKindTest) {
        this.sequenceTypeKindTest = sequenceTypeKindTest;
    }

    public SequenceTypeFunctionTest getSequenceTypeFunctionTest() {
		return sequenceTypeFunctionTest;
	}

	public void setSequenceTypeFunctionTest(SequenceTypeFunctionTest sequenceTypeFunctionTest) {
		this.sequenceTypeFunctionTest = sequenceTypeFunctionTest;
	}
	
    public SequenceTypeMapTest getSequenceTypeMapTest() {
		return sequenceTypeMapTest;
	}

	public void setSequenceTypeMapTest(SequenceTypeMapTest sequenceTypeMapTest) {
		this.sequenceTypeMapTest = sequenceTypeMapTest;
	}
	
    public SequenceTypeArrayTest getSequenceTypeArrayTest() {
		return sequenceTypeArrayTest;
	}

	public void setSequenceTypeArrayTest(SequenceTypeArrayTest sequenceTypeArrayTest) {
		this.sequenceTypeArrayTest = sequenceTypeArrayTest;
	}

	@Override
    public void callVisitors(ExpressionOwner owner, XPathVisitor visitor) {
       // NO OP        
    }

}
