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

import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xpath.objects.XObject;

/**
 * An object of this class, stores information about use of one
 * sequence type XPath expression, while doing an XSLT stylesheet 
 * transformation.
 * 
 * For e.g, an object of this class can store XSLT transformation
 * run-time data for sequence type expressions like xs:string, 
 * xs:string+, xs:integer, xs:integer*, empty-sequence() etc. 
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class SequenceTypeData extends XObject {
    
    private static final long serialVersionUID = -8207360998434418776L;

    private int builtInSequenceType;
    
    private XSTypeDefinition xsTypeDefinition;
    
    private int itemTypeOccurrenceIndicator;
    
    private SequenceTypeKindTest sequenceTypeKindTest;
    
    private SequenceTypeFunctionTest sequenceTypeFunctionTest;
    
    private SequenceTypeMapTest sequenceTypeMapTest;
    
    private SequenceTypeArrayTest sequenceTypeArrayTest;

    public int getBuiltInSequenceType() {
        return builtInSequenceType;
    }

    public void setBuiltInSequenceType(int sequenceType) {
        this.builtInSequenceType = sequenceType;
    }

    public XSTypeDefinition getXsTypeDefinition() {
		return xsTypeDefinition;
	}

	public void setXsTypeDefinition(XSTypeDefinition xsTypeDefinition) {
		this.xsTypeDefinition = xsTypeDefinition;
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
	
    /**
     * Check whether, one SequenceTypeData object is functionally
     * equal to another SequenceTypeData object.  
     */
	public boolean equal(SequenceTypeData sequenceTypeData) {
	    
		boolean result = true;
	    
	    int seqType2 = sequenceTypeData.getBuiltInSequenceType();
	    int occrInd2 = sequenceTypeData.getItemTypeOccurrenceIndicator();
	    SequenceTypeKindTest sequenceTypeKindTest2 = sequenceTypeData.getSequenceTypeKindTest();
	    
	    if ((this.builtInSequenceType != 0) && (this.builtInSequenceType == seqType2) 
	    		                           && (this.itemTypeOccurrenceIndicator == occrInd2)) {
	       result = true;
	    }
	    else if (this.itemTypeOccurrenceIndicator != occrInd2) {
	       result = false;
	    }
	    else if (!(this.sequenceTypeKindTest).equal(sequenceTypeKindTest2)) {
	       result = false;
	    }
	    
	    return result;
	}

}
