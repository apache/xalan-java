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
 * A class definition, that stores information about use of one
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
     * Method definition, to check whether, one SequenceTypeData object 
     * is functionally equal to another SequenceTypeData object.
     * 
     * @param sequenceTypeData2					    An SequenceTypeData object instance,
     *                                              that needs to be compared with this
     *                                              SequenceTypeData object instance.
     * @return									    Boolean value true or false
     */
	public boolean equal(SequenceTypeData sequenceTypeData2) {
		
		boolean result = false;
	    
	    int builtInseqType2 = sequenceTypeData2.getBuiltInSequenceType();	    
	    SequenceTypeKindTest sequenceTypeKindTest2 = sequenceTypeData2.getSequenceTypeKindTest();
	    
	    int occrInd2 = sequenceTypeData2.getItemTypeOccurrenceIndicator();
	    
	    boolean dataTypeCompatible = false;
	    
	    if ((this.builtInSequenceType != 0) && (builtInseqType2 != 0)) {
	       if ((this.builtInSequenceType == SequenceTypeSupport.XS_ANY_ATOMIC_TYPE) || 
	    		                                                                (builtInseqType2 == SequenceTypeSupport.XS_ANY_ATOMIC_TYPE)) {
	    	   dataTypeCompatible = true; 
	       }
	       
	       if (!dataTypeCompatible) {
	    	  if (this.builtInSequenceType == SequenceTypeSupport.STRING) {
	    		 if ((builtInseqType2 == SequenceTypeSupport.STRING) || (builtInseqType2 == SequenceTypeSupport.XS_NORMALIZED_STRING) ||
												    		     (builtInseqType2 == SequenceTypeSupport.XS_TOKEN) || 
												    			 (builtInseqType2 == SequenceTypeSupport.XS_NMTOKEN) || 
												    			 (builtInseqType2 == SequenceTypeSupport.XS_LANGUAGE) ||
												    			 (builtInseqType2 == SequenceTypeSupport.XS_NAME) ||
												    			 (builtInseqType2 == SequenceTypeSupport.XS_NCNAME) ||
												    			 (builtInseqType2 == SequenceTypeSupport.XS_ID) ||
												    			 (builtInseqType2 == SequenceTypeSupport.XS_IDREF)) {
	    			 dataTypeCompatible = true; 
	    		 }
	    	  }
	    	  else if (this.builtInSequenceType == SequenceTypeSupport.XS_NORMALIZED_STRING) {
	    		  if ((builtInseqType2 == SequenceTypeSupport.XS_NORMALIZED_STRING) ||
												    		     (builtInseqType2 == SequenceTypeSupport.XS_TOKEN) || 
												    			 (builtInseqType2 == SequenceTypeSupport.XS_NMTOKEN) || 
												    			 (builtInseqType2 == SequenceTypeSupport.XS_LANGUAGE) ||
												    			 (builtInseqType2 == SequenceTypeSupport.XS_NAME) ||
												    			 (builtInseqType2 == SequenceTypeSupport.XS_NCNAME) ||
												    			 (builtInseqType2 == SequenceTypeSupport.XS_ID) ||
												    			 (builtInseqType2 == SequenceTypeSupport.XS_IDREF)) {
                     dataTypeCompatible = true; 
                  } 
	    	  }
              else if (this.builtInSequenceType == SequenceTypeSupport.XS_TOKEN) {
            	  if ((builtInseqType2 == SequenceTypeSupport.XS_TOKEN) || 
												    			 (builtInseqType2 == SequenceTypeSupport.XS_NMTOKEN) || 
												    			 (builtInseqType2 == SequenceTypeSupport.XS_LANGUAGE) ||
												    			 (builtInseqType2 == SequenceTypeSupport.XS_NAME) ||
												    			 (builtInseqType2 == SequenceTypeSupport.XS_NCNAME) ||
												    			 (builtInseqType2 == SequenceTypeSupport.XS_ID) ||
												    			 (builtInseqType2 == SequenceTypeSupport.XS_IDREF)) {
                     dataTypeCompatible = true; 
                  }
	    	  }
              else if (this.builtInSequenceType == SequenceTypeSupport.XS_NAME) {
            	  if ((builtInseqType2 == SequenceTypeSupport.XS_NAME) ||
												    			 (builtInseqType2 == SequenceTypeSupport.XS_NCNAME) ||
												    			 (builtInseqType2 == SequenceTypeSupport.XS_ID) ||
												    			 (builtInseqType2 == SequenceTypeSupport.XS_IDREF)) {
                     dataTypeCompatible = true; 
                 }
	    	  }
              else if (this.builtInSequenceType == SequenceTypeSupport.XS_NCNAME) {
            	  if ((builtInseqType2 == SequenceTypeSupport.XS_NCNAME) ||
												    			 (builtInseqType2 == SequenceTypeSupport.XS_ID) ||
												    			 (builtInseqType2 == SequenceTypeSupport.XS_IDREF)) {
                     dataTypeCompatible = true; 
                  } 
	    	  }
	       }
	       
	       if (!dataTypeCompatible) {
	    	   if (this.builtInSequenceType == SequenceTypeSupport.XS_DECIMAL) {
	    		   if ((builtInseqType2 == SequenceTypeSupport.XS_DECIMAL) || (builtInseqType2 == SequenceTypeSupport.XS_INTEGER) ||
												    		     (builtInseqType2 == SequenceTypeSupport.XS_LONG) || 
												    			 (builtInseqType2 == SequenceTypeSupport.XS_NON_NEGATIVE_INTEGER) || 
												    			 (builtInseqType2 == SequenceTypeSupport.XS_NON_POSITIVE_INTEGER) ||
												    			 (builtInseqType2 == SequenceTypeSupport.XS_INT) ||
												    			 (builtInseqType2 == SequenceTypeSupport.XS_SHORT) ||
												    			 (builtInseqType2 == SequenceTypeSupport.XS_BYTE) ||
												    			 (builtInseqType2 == SequenceTypeSupport.XS_POSITIVE_INTEGER) ||
												    			 (builtInseqType2 == SequenceTypeSupport.XS_UNSIGNED_LONG) ||
												    			 (builtInseqType2 == SequenceTypeSupport.XS_UNSIGNED_INT) ||
												    			 (builtInseqType2 == SequenceTypeSupport.XS_UNSIGNED_SHORT) ||
												    			 (builtInseqType2 == SequenceTypeSupport.XS_UNSIGNED_BYTE)) {	    			   
                       dataTypeCompatible = true; 
                   } 
	    	   }
	    	   else if (this.builtInSequenceType == SequenceTypeSupport.XS_INTEGER) {
	    		   if ((builtInseqType2 == SequenceTypeSupport.XS_INTEGER) ||
												    		     (builtInseqType2 == SequenceTypeSupport.XS_LONG) || 
												    			 (builtInseqType2 == SequenceTypeSupport.XS_NON_NEGATIVE_INTEGER) || 
												    			 (builtInseqType2 == SequenceTypeSupport.XS_NON_POSITIVE_INTEGER) ||
												    			 (builtInseqType2 == SequenceTypeSupport.XS_INT) ||
												    			 (builtInseqType2 == SequenceTypeSupport.XS_SHORT) ||
												    			 (builtInseqType2 == SequenceTypeSupport.XS_BYTE) ||
												    			 (builtInseqType2 == SequenceTypeSupport.XS_POSITIVE_INTEGER) ||
												    			 (builtInseqType2 == SequenceTypeSupport.XS_UNSIGNED_LONG) ||
												    			 (builtInseqType2 == SequenceTypeSupport.XS_UNSIGNED_INT) ||
												    			 (builtInseqType2 == SequenceTypeSupport.XS_UNSIGNED_SHORT) ||
												    			 (builtInseqType2 == SequenceTypeSupport.XS_UNSIGNED_BYTE)) {	    			   
                       dataTypeCompatible = true; 
                   } 
	    	   }
	    	   else if (this.builtInSequenceType == SequenceTypeSupport.XS_LONG) {
	    		   if ((builtInseqType2 == SequenceTypeSupport.XS_LONG) || (builtInseqType2 == SequenceTypeSupport.XS_INT) ||
														    			   (builtInseqType2 == SequenceTypeSupport.XS_SHORT) ||
														    			   (builtInseqType2 == SequenceTypeSupport.XS_BYTE)) {	    			   
                       dataTypeCompatible = true; 
                   } 
	    	   }
	    	   else if (this.builtInSequenceType == SequenceTypeSupport.XS_INT) {
	    		   if ((builtInseqType2 == SequenceTypeSupport.XS_INT) || (builtInseqType2 == SequenceTypeSupport.XS_SHORT) ||
												    			          (builtInseqType2 == SequenceTypeSupport.XS_BYTE)) {	    			   
                       dataTypeCompatible = true; 
                   } 
	    	   }
	    	   else if (this.builtInSequenceType == SequenceTypeSupport.XS_SHORT) {
	    		   if ((builtInseqType2 == SequenceTypeSupport.XS_SHORT) || (builtInseqType2 == SequenceTypeSupport.XS_BYTE)) {	    			   
                       dataTypeCompatible = true; 
                   } 
	    	   }
	    	   else if (this.builtInSequenceType == SequenceTypeSupport.XS_NON_NEGATIVE_INTEGER) {
	    		   if ((builtInseqType2 == SequenceTypeSupport.XS_NON_NEGATIVE_INTEGER) ||												    		     
														    			 (builtInseqType2 == SequenceTypeSupport.XS_POSITIVE_INTEGER) ||
														    			 (builtInseqType2 == SequenceTypeSupport.XS_UNSIGNED_LONG) ||
														    			 (builtInseqType2 == SequenceTypeSupport.XS_UNSIGNED_INT) ||
														    			 (builtInseqType2 == SequenceTypeSupport.XS_UNSIGNED_SHORT) ||
														    			 (builtInseqType2 == SequenceTypeSupport.XS_UNSIGNED_BYTE)) {	    			   
                       dataTypeCompatible = true; 
                   } 
	    	   }
	    	   else if (this.builtInSequenceType == SequenceTypeSupport.XS_UNSIGNED_LONG) {
	    		   if ((builtInseqType2 == SequenceTypeSupport.XS_UNSIGNED_LONG) ||												    		     												    			 
														    			 (builtInseqType2 == SequenceTypeSupport.XS_UNSIGNED_INT) ||
														    			 (builtInseqType2 == SequenceTypeSupport.XS_UNSIGNED_SHORT) ||
														    			 (builtInseqType2 == SequenceTypeSupport.XS_UNSIGNED_BYTE)) {	    			   
                       dataTypeCompatible = true; 
                   } 
	    	   }
	    	   else if (this.builtInSequenceType == SequenceTypeSupport.XS_UNSIGNED_INT) {
	    		   if ((builtInseqType2 == SequenceTypeSupport.XS_UNSIGNED_INT) ||
												    			         (builtInseqType2 == SequenceTypeSupport.XS_UNSIGNED_SHORT) ||
												    			         (builtInseqType2 == SequenceTypeSupport.XS_UNSIGNED_BYTE)) {	    			   
                       dataTypeCompatible = true; 
                   } 
	    	   }
	    	   else if (this.builtInSequenceType == SequenceTypeSupport.XS_UNSIGNED_SHORT) {
	    		   if ((builtInseqType2 == SequenceTypeSupport.XS_UNSIGNED_SHORT) || 
	    				                                                 (builtInseqType2 == SequenceTypeSupport.XS_UNSIGNED_BYTE)) {	    			   
                       dataTypeCompatible = true; 
                   } 
	    	   }
	    	   else if (this.builtInSequenceType == SequenceTypeSupport.XS_NON_POSITIVE_INTEGER) {
	    		   if ((builtInseqType2 == SequenceTypeSupport.XS_NON_POSITIVE_INTEGER) ||												    		     
												    			         (builtInseqType2 == SequenceTypeSupport.XS_NEGATIVE_INTEGER)) {	    			   
                       dataTypeCompatible = true; 
                   } 
	    	   }
	       }
	       
	       if (!dataTypeCompatible) {
	    	   if (this.builtInSequenceType == SequenceTypeSupport.XS_DURATION) {
	    		   if ((builtInseqType2 == SequenceTypeSupport.XS_DAYTIME_DURATION) || (builtInseqType2 == SequenceTypeSupport.XS_YEARMONTH_DURATION)) {	    			   
                       dataTypeCompatible = true; 
                   }  
	           }
	       }
	       
	       if (!dataTypeCompatible && (this.builtInSequenceType == builtInseqType2)) {
	    	   dataTypeCompatible = true; 
	       }
	       
	       if (dataTypeCompatible && isOccurenceIndicatorCompatible(occrInd2)) {    	  
	    	   result = true; 
	       }
	    }	    
	    else if ((this.sequenceTypeKindTest != null) && (sequenceTypeKindTest2 != null)) {
	    	dataTypeCompatible = (this.sequenceTypeKindTest).equal(sequenceTypeKindTest2);
	    	if (dataTypeCompatible && isOccurenceIndicatorCompatible(occrInd2)) {
	    	   result = true; 
	    	}
	    }
        else if (this.sequenceTypeKindTest != null) {
	    	if ((this.sequenceTypeKindTest.getKindVal() == SequenceTypeSupport.ITEM_KIND) && isOccurenceIndicatorCompatible(occrInd2)) {	    		
	    	   result = true; 
	    	}
	    }
	    
	    return result;
	}

	/**
	 * Method definition, to check whether, one XPath sequence type 
	 * occurrence indicator value, is compatible with another XPath 
	 * sequence type occurrence indicator value. 
	 * 
	 * @param occrInd									The second XPath sequence type occurrence 
	 *                                                  indicator value. 
	 * @return											Boolean value true or false
	 */
	private boolean isOccurenceIndicatorCompatible(int occrInd) {
		
		boolean result = false;
		
		if ((this.itemTypeOccurrenceIndicator == SequenceTypeSupport.OccurrenceIndicator.ABSENT) && 
				                                                                     (occrInd == SequenceTypeSupport.OccurrenceIndicator.ABSENT)) {
			result = true; 
		}
		else if ((this.itemTypeOccurrenceIndicator == SequenceTypeSupport.OccurrenceIndicator.ABSENT) && 
				                                                                     ((occrInd == SequenceTypeSupport.OccurrenceIndicator.ZERO_OR_MANY) || 
						                                                              (occrInd == SequenceTypeSupport.OccurrenceIndicator.ZERO_OR_ONE))) {
			result = true;
		}
		else if ((occrInd == SequenceTypeSupport.OccurrenceIndicator.ABSENT) && 
				                                                           ((this.itemTypeOccurrenceIndicator == SequenceTypeSupport.OccurrenceIndicator.ZERO_OR_MANY) || 
						                                                   (this.itemTypeOccurrenceIndicator == SequenceTypeSupport.OccurrenceIndicator.ZERO_OR_ONE))) {
			result = true;
		}
		else if (this.itemTypeOccurrenceIndicator == occrInd) {
			result = true;
		}
		
		return result;
	}

}
