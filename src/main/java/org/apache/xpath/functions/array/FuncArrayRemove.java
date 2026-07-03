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

import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.Function2Args;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathArray;

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
		m_arity = new Short[] { 2 };
	}

	/**
	 * Evaluate the function. The function must return a valid object.
	 * 
	 * @param xctxt                        An XPath context object
	 * @return                             A valid XObject
	 *
	 * @throws javax.xml.transform.TransformerException
	 */
	public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException {
		
	    XObject result = null;
	       
	    SourceLocator srcLocator = xctxt.getSAXLocator();	    	    
	    
	    XPathArray arg0Arr = null;
	    
	    XObject xObj0 = getFunctionArgEffectiveValue(m_arg0, xctxt);
	    
	    if (xObj0 instanceof XPathArray) {
	    	arg0Arr = (XPathArray)xObj0;
	    }
	    else {
	    	throw new javax.xml.transform.TransformerException("FORG0006 : The first argument of array:remove function call, "
	    																									+ "needs to be an xdm array.", srcLocator);  
	    }
	    	    
	    XPathArray resultArr = new XPathArray();
	    
	    XObject xObj1 = getFunctionArgEffectiveValue(m_arg1, xctxt);
	    
	    if (isXdmValueAnIntegerSeq(xObj1)) {
	    	ResultSequence posSeq1 = getIntegerSeq(xObj1);
	    	int size1 = arg0Arr.size();
	    	for (int idx = 0; idx < size1; idx++) {
	    		if (!isXdmSeqContainsAnInteger(posSeq1, idx + 1)) {
	    			resultArr.add(arg0Arr.get(idx));  
	    		}
	    	}
	    }
	    else {
	    	throw new javax.xml.transform.TransformerException("FOAY0001 : The second argument of array:remove function "
	    																								    + "call, needs to be an xs:integer sequence.", srcLocator); 
	    }
	    
	    result = resultArr;
	    
	    return result;
	}

	/**
	 * Method definition, to get sequence of integer values, for 
	 * an xdm value that is known to be a sequence of integer 
	 * values.
	 */
	private ResultSequence getIntegerSeq(XObject xdmVal) {
		
		ResultSequence result = new ResultSequence();

		if (xdmVal instanceof ResultSequence) {
			ResultSequence seq = (ResultSequence)xdmVal;
			int size1 = seq.size();
			for (int idx = 0; idx < size1; idx++) {
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
			XSInteger xsInteger = new XSInteger();
			xsInteger.setDouble(dblVal);
			result.add(xsInteger);
		}

		return result;
	}

	/**
	 * Method definition, to check whether an, xdm value is a 
	 * sequence of integer values.
	 */
	private boolean isXdmValueAnIntegerSeq(XObject xdmVal) {		
		
		boolean result = true;

		if (xdmVal instanceof ResultSequence) {
			ResultSequence seq = (ResultSequence)xdmVal;
			int size1 = seq.size();
			for (int idx = 0; idx < size1; idx++) {
				XObject item = seq.item(idx);
				if (item instanceof XSNumericType) {
					XSNumericType xsNumericVal = (XSNumericType)item;
					String strVal = xsNumericVal.stringValue();
					try {
						Integer.valueOf(strVal);
					}
					catch (NumberFormatException ex) {
						result = false;

						break;
					}
				}
				else if (item instanceof XNumber) {
					XNumber xNumberVal = (XNumber)item;
					double dblVal = xNumberVal.num();
					if (!((int)dblVal == dblVal)) {
						result = false;

						break; 
					}
				}
				else {
					result = false;

					break;
				}
			}
		} 
		else if (xdmVal instanceof XSNumericType) {
			XSNumericType xsNumericVal = (XSNumericType)xdmVal;
			String strVal = xsNumericVal.stringValue();
			try {
				Integer.valueOf(strVal);
			}
			catch (NumberFormatException ex) {
				result = false;
			}
		}
		else if (xdmVal instanceof XNumber) {
			XNumber xNumberVal = (XNumber)xdmVal;
			double dblVal = xNumberVal.num();
			if (!((int)dblVal == dblVal)) {
				result = false; 
			} 
		}
		else {
			result = false; 
		}

		return result; 
	}
	
	/**
	 * Method definition, to check whether an, integer sequence 
	 * contains a specific integer value.
	 */
	private boolean isXdmSeqContainsAnInteger(ResultSequence posSeq1, int index) {
		
		boolean result = false;
		
		int size1 = posSeq1.size();
		for (int idx = 0; idx < size1; idx++) {
		   XSInteger item = (XSInteger)(posSeq1.item(idx));
		   double dblVal = item.doubleValue();
		   if ((int)dblVal == index) {
			  result = true;
			  
			  break;
		   }
		}
		
		return result;
    }

}
