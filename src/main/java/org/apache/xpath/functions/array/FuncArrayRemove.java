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
package org.apache.xpath.functions.array;

import javax.xml.transform.SourceLocator;

import org.apache.xpath.Expression;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.Function2Args;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathArray;
import org.apache.xpath.operations.Variable;

import xml.xpath31.processor.types.XSInteger;
import xml.xpath31.processor.types.XSNumericType;

/**
 * Implementation of the array:remove function.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncArrayRemove extends Function2Args {

	private static final long serialVersionUID = 7890619596202668602L;
	
	/**
	 * Class constructor.
	 */
	public FuncArrayRemove() {
		m_defined_arity = new Short[] { 2 };
	}

	public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException {
		
	    XObject result = null;
	       
	    SourceLocator srcLocator = xctxt.getSAXLocator();
	       
	    Expression arg0Expr = getArg0();	    
	    Expression arg1Expr = getArg1();	    	    
	    
	    XPathArray arg0Arr = null;
	    if (arg0Expr instanceof Variable) {
	       XObject arg0Value = ((Variable)arg0Expr).execute(xctxt);
	       if (arg0Value instanceof XPathArray) {
	    	  arg0Arr = (XPathArray)arg0Value;
	       }
	       else {
	    	  throw new javax.xml.transform.TransformerException("FORG0006 : The 1st argument of array:remove function call, "
	    	  		                                                                       + "needs to be an xdm array.", srcLocator);  
	       }
	    }
	    else {
	    	XObject arg0Value = arg0Expr.execute(xctxt);
	    	if (arg0Value instanceof XPathArray) {
	    	   arg0Arr = (XPathArray)arg0Value;
		    }
		    else {
		       throw new javax.xml.transform.TransformerException("FORG0006 : The 1st argument of array:remove function call, "
		       		                                                                      + "needs to be an xdm array.", srcLocator);   
		    }
	    }
	    	    
	    XPathArray resultArr = new XPathArray();
	    
	    XObject arg1 = null;
	    if (arg1Expr instanceof Variable) {
	       arg1 = ((Variable)arg1Expr).execute(xctxt);
	       if (isXdmValueAnIntegerSeq(arg1)) {
	    	  ResultSequence seqInt = getIntegerSeq(arg1); 
	    	  for (int idx = 0; idx < arg0Arr.size(); idx++) {
	    		 if (!isIntegerSeqContainsAnInteger(seqInt, idx + 1)) {
	    			resultArr.add(arg0Arr.get(idx));  
	    		 }
	    	  }
	       }
	       else {
	    	  throw new javax.xml.transform.TransformerException("FOAY0001 : The 2nd argument of array:remove function "
	    	  		                                                    + "call, needs to be an xs:integer sequence.", srcLocator); 
	       }
	    }
	    else {
	       arg1 = arg1Expr.execute(xctxt);
	       if (isXdmValueAnIntegerSeq(arg1)) {
	    	  ResultSequence seqInt = getIntegerSeq(arg1); 
		      for (int idx = 0; idx < arg0Arr.size(); idx++) {
		         if (!isIntegerSeqContainsAnInteger(seqInt, idx + 1)) {
		    	    resultArr.add(arg0Arr.get(idx));  
		    	 }
		      }   
		   }
		   else {
			  throw new javax.xml.transform.TransformerException("FOAY0001 : The 2nd argument of array:remove function "
                                                                         + "call, needs to be an xs:integer sequence.", srcLocator);	   
		   }
	    }
	    
	    result = resultArr;
	    
	    return result;
	}

	/**
	 * Get an integer sequence, for an xdm value which is known 
	 * to be a sequence of integer (or, which is castable to an 
	 * integer) values.
	 */
	private ResultSequence getIntegerSeq(XObject xdmVal) {
		ResultSequence result = new ResultSequence();
		
		if (xdmVal instanceof ResultSequence) {
			   ResultSequence seq = (ResultSequence)xdmVal;
			   for (int idx = 0; idx < seq.size(); idx++) {
				  XObject item = seq.item(idx);
				  if (item instanceof XSNumericType) {
					 XSNumericType xsNumericVal = (XSNumericType)item;
					 String strVal = xsNumericVal.stringValue();
					 XSInteger xsInteger = new XSInteger(strVal);
					 result.add(xsInteger);
				  }
				  else if (item instanceof XNumber) {
					 XNumber xNumberVal = (XNumber)item;
					 double dblVal = xNumberVal.num();
					 String strVal = (Double.valueOf(dblVal)).toString();
					 XSInteger xsInteger = new XSInteger(strVal);
					 result.add(xsInteger);
				  }
			   }
			} 
			else if (xdmVal instanceof XSNumericType) {
			   XSNumericType xsNumericVal = (XSNumericType)xdmVal;
			   String strVal = xsNumericVal.stringValue();
			   XSInteger xsInteger = new XSInteger(strVal);
			   result.add(xsInteger);
			}
			else if (xdmVal instanceof XNumber) {
			   XNumber xNumberVal = (XNumber)xdmVal;
			   double dblVal = xNumberVal.num();
			   // String strVal = (Double.valueOf(dblVal)).toString();
				 XSInteger xsInteger = new XSInteger();
				 xsInteger.setDouble(dblVal);
				 result.add(xsInteger);
			}
		
		return result;
	}

	/**
	 * Find whether an, xdm value is an integer sequence.
	 */
	private boolean isXdmValueAnIntegerSeq(XObject xdmVal) {		
		boolean isIntegerSeq = true;
		
		if (xdmVal instanceof ResultSequence) {
		   ResultSequence seq = (ResultSequence)xdmVal;
		   for (int idx = 0; idx < seq.size(); idx++) {
			  XObject item = seq.item(idx);
			  if (item instanceof XSNumericType) {
				 XSNumericType xsNumericVal = (XSNumericType)item;
				 String strVal = xsNumericVal.stringValue();
				 try {
				    Integer integer = Integer.valueOf(strVal);
				 }
				 catch (NumberFormatException ex) {
					isIntegerSeq = false;
					break;
				 }
			  }
			  else if (item instanceof XNumber) {
				 XNumber xNumberVal = (XNumber)item;
				 double dblVal = xNumberVal.num();
				 if (!((int)dblVal == dblVal)) {
					isIntegerSeq = false;
				    break; 
				 }
			  }
			  else {
				 isIntegerSeq = false;
				 break;
			  }
		   }
		} 
		else if (xdmVal instanceof XSNumericType) {
		   XSNumericType xsNumericVal = (XSNumericType)xdmVal;
		   String strVal = xsNumericVal.stringValue();
		   try {
		      Integer integer = Integer.valueOf(strVal);
		   }
		   catch (NumberFormatException ex) {
			  isIntegerSeq = false;
		   }
		}
		else if (xdmVal instanceof XNumber) {
		   XNumber xNumberVal = (XNumber)xdmVal;
		   double dblVal = xNumberVal.num();
		   if (!((int)dblVal == dblVal)) {
			  isIntegerSeq = false; 
		   } 
		}
		else {
		   isIntegerSeq = false; 
		}
		
		return isIntegerSeq; 
	}
	
	/**
	 * Find whether an, integer sequence contains a specific integer value.
	 */
	private boolean isIntegerSeqContainsAnInteger(ResultSequence seq, int value) {
		boolean result = false;
		
		for (int idx = 0; idx < seq.size(); idx++) {
		   XSInteger item = (XSInteger)(seq.item(idx));
		   double dblVal = item.doubleValue();
		   if ((int)dblVal == value) {
			  result = true;
			  break;
		   }
		}
		
		return result;
    }

}
