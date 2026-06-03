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
public class XPathSequenceTypeData extends XObject {
    
    private static final long serialVersionUID = -8207360998434418776L;

    private int builtInSequenceType;
    
    private XSTypeDefinition xsTypeDefinition;
    
    private int itemTypeOccurrenceIndicator;
    
    private XPathSequenceTypeKindTest sequenceTypeKindTest;
    
    private XPathSequenceTypeFunctionTest sequenceTypeFunctionTest;
    
    private XPathSequenceTypeMapTest sequenceTypeMapTest;
    
    private XPathSequenceTypeArrayTest sequenceTypeArrayTest;

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

    public XPathSequenceTypeKindTest getSequenceTypeKindTest() {
        return sequenceTypeKindTest;
    }

    public void setSequenceTypeKindTest(XPathSequenceTypeKindTest sequenceTypeKindTest) {
        this.sequenceTypeKindTest = sequenceTypeKindTest;
    }

	public XPathSequenceTypeFunctionTest getSequenceTypeFunctionTest() {
		return sequenceTypeFunctionTest;
	}

	public void setSequenceTypeFunctionTest(XPathSequenceTypeFunctionTest sequenceTypeFunctionTest) {
		this.sequenceTypeFunctionTest = sequenceTypeFunctionTest;
	}
	
	public XPathSequenceTypeMapTest getSequenceTypeMapTest() {
		return sequenceTypeMapTest;
	}

	public void setSequenceTypeMapTest(XPathSequenceTypeMapTest sequenceTypeMapTest) {
		this.sequenceTypeMapTest = sequenceTypeMapTest;
	}
	
	public XPathSequenceTypeArrayTest getSequenceTypeArrayTest() {
		return sequenceTypeArrayTest;
	}

	public void setSequenceTypeArrayTest(XPathSequenceTypeArrayTest sequenceTypeArrayTest) {
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
	public boolean equal(XPathSequenceTypeData sequenceTypeData2) {
		
		boolean result = false;
	    
	    int builtInseqType2 = sequenceTypeData2.getBuiltInSequenceType();	    
	    XPathSequenceTypeKindTest sequenceTypeKindTest2 = sequenceTypeData2.getSequenceTypeKindTest();
	    
	    int occrInd2 = sequenceTypeData2.getItemTypeOccurrenceIndicator();
	    
	    boolean dataTypeCompatible = false;
	    
	    if ((this.builtInSequenceType != 0) && (builtInseqType2 != 0)) {
	       if ((this.builtInSequenceType == XPathSequenceTypeSupport.XS_ANY_ATOMIC_TYPE) || 
	    		                                                                (builtInseqType2 == XPathSequenceTypeSupport.XS_ANY_ATOMIC_TYPE)) {
	    	   dataTypeCompatible = true; 
	       }
	       
	       if (!dataTypeCompatible) {
	    	  if (this.builtInSequenceType == XPathSequenceTypeSupport.STRING) {
	    		 if ((builtInseqType2 == XPathSequenceTypeSupport.STRING) || (builtInseqType2 == XPathSequenceTypeSupport.XS_NORMALIZED_STRING) ||
												    		     (builtInseqType2 == XPathSequenceTypeSupport.XS_TOKEN) || 
												    			 (builtInseqType2 == XPathSequenceTypeSupport.XS_NMTOKEN) || 
												    			 (builtInseqType2 == XPathSequenceTypeSupport.XS_LANGUAGE) ||
												    			 (builtInseqType2 == XPathSequenceTypeSupport.XS_NAME) ||
												    			 (builtInseqType2 == XPathSequenceTypeSupport.XS_NCNAME) ||
												    			 (builtInseqType2 == XPathSequenceTypeSupport.XS_ID) ||
												    			 (builtInseqType2 == XPathSequenceTypeSupport.XS_IDREF)) {
	    			 dataTypeCompatible = true; 
	    		 }
	    	  }
	    	  else if (this.builtInSequenceType == XPathSequenceTypeSupport.XS_NORMALIZED_STRING) {
	    		  if ((builtInseqType2 == XPathSequenceTypeSupport.XS_NORMALIZED_STRING) ||
												    		     (builtInseqType2 == XPathSequenceTypeSupport.XS_TOKEN) || 
												    			 (builtInseqType2 == XPathSequenceTypeSupport.XS_NMTOKEN) || 
												    			 (builtInseqType2 == XPathSequenceTypeSupport.XS_LANGUAGE) ||
												    			 (builtInseqType2 == XPathSequenceTypeSupport.XS_NAME) ||
												    			 (builtInseqType2 == XPathSequenceTypeSupport.XS_NCNAME) ||
												    			 (builtInseqType2 == XPathSequenceTypeSupport.XS_ID) ||
												    			 (builtInseqType2 == XPathSequenceTypeSupport.XS_IDREF)) {
                     dataTypeCompatible = true; 
                  } 
	    	  }
              else if (this.builtInSequenceType == XPathSequenceTypeSupport.XS_TOKEN) {
            	  if ((builtInseqType2 == XPathSequenceTypeSupport.XS_TOKEN) || 
												    			 (builtInseqType2 == XPathSequenceTypeSupport.XS_NMTOKEN) || 
												    			 (builtInseqType2 == XPathSequenceTypeSupport.XS_LANGUAGE) ||
												    			 (builtInseqType2 == XPathSequenceTypeSupport.XS_NAME) ||
												    			 (builtInseqType2 == XPathSequenceTypeSupport.XS_NCNAME) ||
												    			 (builtInseqType2 == XPathSequenceTypeSupport.XS_ID) ||
												    			 (builtInseqType2 == XPathSequenceTypeSupport.XS_IDREF)) {
                     dataTypeCompatible = true; 
                  }
	    	  }
              else if (this.builtInSequenceType == XPathSequenceTypeSupport.XS_NAME) {
            	  if ((builtInseqType2 == XPathSequenceTypeSupport.XS_NAME) ||
												    			 (builtInseqType2 == XPathSequenceTypeSupport.XS_NCNAME) ||
												    			 (builtInseqType2 == XPathSequenceTypeSupport.XS_ID) ||
												    			 (builtInseqType2 == XPathSequenceTypeSupport.XS_IDREF)) {
                     dataTypeCompatible = true; 
                 }
	    	  }
              else if (this.builtInSequenceType == XPathSequenceTypeSupport.XS_NCNAME) {
            	  if ((builtInseqType2 == XPathSequenceTypeSupport.XS_NCNAME) ||
												    			 (builtInseqType2 == XPathSequenceTypeSupport.XS_ID) ||
												    			 (builtInseqType2 == XPathSequenceTypeSupport.XS_IDREF)) {
                     dataTypeCompatible = true; 
                  } 
	    	  }
	       }
	       
	       if (!dataTypeCompatible) {
	    	   if (this.builtInSequenceType == XPathSequenceTypeSupport.XS_DECIMAL) {
	    		   if ((builtInseqType2 == XPathSequenceTypeSupport.XS_DECIMAL) || (builtInseqType2 == XPathSequenceTypeSupport.XS_INTEGER) ||
												    		     (builtInseqType2 == XPathSequenceTypeSupport.XS_LONG) || 
												    			 (builtInseqType2 == XPathSequenceTypeSupport.XS_NON_NEGATIVE_INTEGER) || 
												    			 (builtInseqType2 == XPathSequenceTypeSupport.XS_NON_POSITIVE_INTEGER) ||
												    			 (builtInseqType2 == XPathSequenceTypeSupport.XS_INT) ||
												    			 (builtInseqType2 == XPathSequenceTypeSupport.XS_SHORT) ||
												    			 (builtInseqType2 == XPathSequenceTypeSupport.XS_BYTE) ||
												    			 (builtInseqType2 == XPathSequenceTypeSupport.XS_POSITIVE_INTEGER) ||
												    			 (builtInseqType2 == XPathSequenceTypeSupport.XS_UNSIGNED_LONG) ||
												    			 (builtInseqType2 == XPathSequenceTypeSupport.XS_UNSIGNED_INT) ||
												    			 (builtInseqType2 == XPathSequenceTypeSupport.XS_UNSIGNED_SHORT) ||
												    			 (builtInseqType2 == XPathSequenceTypeSupport.XS_UNSIGNED_BYTE)) {	    			   
                       dataTypeCompatible = true; 
                   } 
	    	   }
	    	   else if (this.builtInSequenceType == XPathSequenceTypeSupport.XS_INTEGER) {
	    		   if ((builtInseqType2 == XPathSequenceTypeSupport.XS_INTEGER) ||
												    		     (builtInseqType2 == XPathSequenceTypeSupport.XS_LONG) || 
												    			 (builtInseqType2 == XPathSequenceTypeSupport.XS_NON_NEGATIVE_INTEGER) || 
												    			 (builtInseqType2 == XPathSequenceTypeSupport.XS_NON_POSITIVE_INTEGER) ||
												    			 (builtInseqType2 == XPathSequenceTypeSupport.XS_INT) ||
												    			 (builtInseqType2 == XPathSequenceTypeSupport.XS_SHORT) ||
												    			 (builtInseqType2 == XPathSequenceTypeSupport.XS_BYTE) ||
												    			 (builtInseqType2 == XPathSequenceTypeSupport.XS_POSITIVE_INTEGER) ||
												    			 (builtInseqType2 == XPathSequenceTypeSupport.XS_UNSIGNED_LONG) ||
												    			 (builtInseqType2 == XPathSequenceTypeSupport.XS_UNSIGNED_INT) ||
												    			 (builtInseqType2 == XPathSequenceTypeSupport.XS_UNSIGNED_SHORT) ||
												    			 (builtInseqType2 == XPathSequenceTypeSupport.XS_UNSIGNED_BYTE)) {	    			   
                       dataTypeCompatible = true; 
                   } 
	    	   }
	    	   else if (this.builtInSequenceType == XPathSequenceTypeSupport.XS_LONG) {
	    		   if ((builtInseqType2 == XPathSequenceTypeSupport.XS_LONG) || (builtInseqType2 == XPathSequenceTypeSupport.XS_INT) ||
														    			   (builtInseqType2 == XPathSequenceTypeSupport.XS_SHORT) ||
														    			   (builtInseqType2 == XPathSequenceTypeSupport.XS_BYTE)) {	    			   
                       dataTypeCompatible = true; 
                   } 
	    	   }
	    	   else if (this.builtInSequenceType == XPathSequenceTypeSupport.XS_INT) {
	    		   if ((builtInseqType2 == XPathSequenceTypeSupport.XS_INT) || (builtInseqType2 == XPathSequenceTypeSupport.XS_SHORT) ||
												    			          (builtInseqType2 == XPathSequenceTypeSupport.XS_BYTE)) {	    			   
                       dataTypeCompatible = true; 
                   } 
	    	   }
	    	   else if (this.builtInSequenceType == XPathSequenceTypeSupport.XS_SHORT) {
	    		   if ((builtInseqType2 == XPathSequenceTypeSupport.XS_SHORT) || (builtInseqType2 == XPathSequenceTypeSupport.XS_BYTE)) {	    			   
                       dataTypeCompatible = true; 
                   } 
	    	   }
	    	   else if (this.builtInSequenceType == XPathSequenceTypeSupport.XS_NON_NEGATIVE_INTEGER) {
	    		   if ((builtInseqType2 == XPathSequenceTypeSupport.XS_NON_NEGATIVE_INTEGER) ||												    		     
														    			 (builtInseqType2 == XPathSequenceTypeSupport.XS_POSITIVE_INTEGER) ||
														    			 (builtInseqType2 == XPathSequenceTypeSupport.XS_UNSIGNED_LONG) ||
														    			 (builtInseqType2 == XPathSequenceTypeSupport.XS_UNSIGNED_INT) ||
														    			 (builtInseqType2 == XPathSequenceTypeSupport.XS_UNSIGNED_SHORT) ||
														    			 (builtInseqType2 == XPathSequenceTypeSupport.XS_UNSIGNED_BYTE)) {	    			   
                       dataTypeCompatible = true; 
                   } 
	    	   }
	    	   else if (this.builtInSequenceType == XPathSequenceTypeSupport.XS_UNSIGNED_LONG) {
	    		   if ((builtInseqType2 == XPathSequenceTypeSupport.XS_UNSIGNED_LONG) ||												    		     												    			 
														    			 (builtInseqType2 == XPathSequenceTypeSupport.XS_UNSIGNED_INT) ||
														    			 (builtInseqType2 == XPathSequenceTypeSupport.XS_UNSIGNED_SHORT) ||
														    			 (builtInseqType2 == XPathSequenceTypeSupport.XS_UNSIGNED_BYTE)) {	    			   
                       dataTypeCompatible = true; 
                   } 
	    	   }
	    	   else if (this.builtInSequenceType == XPathSequenceTypeSupport.XS_UNSIGNED_INT) {
	    		   if ((builtInseqType2 == XPathSequenceTypeSupport.XS_UNSIGNED_INT) ||
												    			         (builtInseqType2 == XPathSequenceTypeSupport.XS_UNSIGNED_SHORT) ||
												    			         (builtInseqType2 == XPathSequenceTypeSupport.XS_UNSIGNED_BYTE)) {	    			   
                       dataTypeCompatible = true; 
                   } 
	    	   }
	    	   else if (this.builtInSequenceType == XPathSequenceTypeSupport.XS_UNSIGNED_SHORT) {
	    		   if ((builtInseqType2 == XPathSequenceTypeSupport.XS_UNSIGNED_SHORT) || 
	    				                                                 (builtInseqType2 == XPathSequenceTypeSupport.XS_UNSIGNED_BYTE)) {	    			   
                       dataTypeCompatible = true; 
                   } 
	    	   }
	    	   else if (this.builtInSequenceType == XPathSequenceTypeSupport.XS_NON_POSITIVE_INTEGER) {
	    		   if ((builtInseqType2 == XPathSequenceTypeSupport.XS_NON_POSITIVE_INTEGER) ||												    		     
												    			         (builtInseqType2 == XPathSequenceTypeSupport.XS_NEGATIVE_INTEGER)) {	    			   
                       dataTypeCompatible = true; 
                   } 
	    	   }
	       }
	       
	       if (!dataTypeCompatible) {
	    	   if (this.builtInSequenceType == XPathSequenceTypeSupport.XS_DURATION) {
	    		   if ((builtInseqType2 == XPathSequenceTypeSupport.XS_DAYTIME_DURATION) || (builtInseqType2 == XPathSequenceTypeSupport.XS_YEARMONTH_DURATION)) {	    			   
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
	    	if ((this.sequenceTypeKindTest.getKindVal() == XPathSequenceTypeSupport.ITEM_KIND) && isOccurenceIndicatorCompatible(occrInd2)) {	    		
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
		
		if ((this.itemTypeOccurrenceIndicator == XPathSequenceTypeSupport.OccurrenceIndicator.ABSENT) && 
				                                                                     (occrInd == XPathSequenceTypeSupport.OccurrenceIndicator.ABSENT)) {
			result = true; 
		}
		else if ((this.itemTypeOccurrenceIndicator == XPathSequenceTypeSupport.OccurrenceIndicator.ABSENT) && 
				                                                                     ((occrInd == XPathSequenceTypeSupport.OccurrenceIndicator.ZERO_OR_MANY) || 
						                                                              (occrInd == XPathSequenceTypeSupport.OccurrenceIndicator.ZERO_OR_ONE))) {
			result = true;
		}
		else if ((occrInd == XPathSequenceTypeSupport.OccurrenceIndicator.ABSENT) && 
				                                                           ((this.itemTypeOccurrenceIndicator == XPathSequenceTypeSupport.OccurrenceIndicator.ZERO_OR_MANY) || 
						                                                   (this.itemTypeOccurrenceIndicator == XPathSequenceTypeSupport.OccurrenceIndicator.ZERO_OR_ONE))) {
			result = true;
		}
		else if (this.itemTypeOccurrenceIndicator == occrInd) {
			result = true;
		}
		
		return result;
	}

}
