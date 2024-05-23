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

    private int fSequenceType;
    
    private int fItemTypeOccurrenceIndicator;
    
    private SequenceTypeKindTest sequenceTypeKindTest;
    
    private SequenceTypeFunctionTest fSequenceTypeFunctionTest;
    
    private SequenceTypeMapTest fSequenceTypeMapTest;

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

    public SequenceTypeKindTest getSequenceTypeKindTest() {
        return sequenceTypeKindTest;
    }

    public void setSequenceTypeKindTest(SequenceTypeKindTest sequenceTypeKindTest) {
        this.sequenceTypeKindTest = sequenceTypeKindTest;
    }

	public SequenceTypeFunctionTest getSequenceTypeFunctionTest() {
		return fSequenceTypeFunctionTest;
	}

	public void setSequenceTypeFunctionTest(SequenceTypeFunctionTest sequenceTypeFunctionTest) {
		this.fSequenceTypeFunctionTest = sequenceTypeFunctionTest;
	}
	
	public SequenceTypeMapTest getSequenceTypeMapTest() {
		return fSequenceTypeMapTest;
	}

	public void setSequenceTypeMapTest(SequenceTypeMapTest sequenceTypeMapTest) {
		this.fSequenceTypeMapTest = sequenceTypeMapTest;
	}
	
    /**
     * Check whether, one SequenceTypeData object is functionally
     * equal to another SequenceTypeData object.  
     */
	public boolean equal(SequenceTypeData sequenceTypeData) {
	    boolean isEqual = true;
	    
	    int seqType2 = sequenceTypeData.getSequenceType();
	    int occrInd2 = sequenceTypeData.getItemTypeOccurrenceIndicator();
	    SequenceTypeKindTest sequenceTypeKindTest2 = sequenceTypeData.getSequenceTypeKindTest();
	    
	    if ((this.fSequenceType != 0) && (this.fSequenceType == seqType2) 
	    		                           && (this.fItemTypeOccurrenceIndicator == occrInd2)) {
	       isEqual = true;
	    }
	    else if (this.fItemTypeOccurrenceIndicator != occrInd2) {
	       isEqual = false;
	    }
	    else if (!(this.sequenceTypeKindTest).equal(sequenceTypeKindTest2)) {
	       isEqual = false;
	    }
	    
	    return isEqual;
	}

}
