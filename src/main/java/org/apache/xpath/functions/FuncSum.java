/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.xpath.functions;

import java.math.BigInteger;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xpath.XPathContext;
import org.apache.xpath.composite.XPathSequenceType;
import org.apache.xpath.composite.XPathSequenceTypeSupport;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XBooleanStatic;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathArray;
import org.apache.xpath.objects.XString;

import xml.xpath31.processor.types.XSAnyAtomicType;
import xml.xpath31.processor.types.XSDayTimeDuration;
import xml.xpath31.processor.types.XSDecimal;
import xml.xpath31.processor.types.XSDouble;
import xml.xpath31.processor.types.XSFloat;
import xml.xpath31.processor.types.XSInteger;
import xml.xpath31.processor.types.XSNumericType;
import xml.xpath31.processor.types.XSUntypedAtomic;
import xml.xpath31.processor.types.XSYearMonthDuration;

/**
 * Implementation of an XPath 3.1 function fn:sum.
 * 
 * @author Scott Boag <scott_boag@us.ibm.com>
 * @author Morris Kwan, Brian James Minchau,
 * @author Christine Li <jycli@apache.org>
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 *         (XPath 3.1 F&O specific changes, to this class)
 * 
 * @xsl.usage advanced
 */
public class FuncSum extends FunctionMultiArgs
{	
	
	static final long serialVersionUID = -2719049259574677519L;

	/**
	 * Class constructor.
	 */
	public FuncSum() {
		m_arity = new Short[] { 1, 2 };  
	}

	/**
	 * Evaluate the function. The function must return a valid object.
	 * 
	 * @param xctxt                        An XPath context object
	 * @return                             A valid XObject
	 *
	 * @throws javax.xml.transform.TransformerException
	 */
	public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
	{    
		
		XObject result = null;

		SourceLocator srcLocator = xctxt.getSAXLocator(); 

		if (m_arg2 != null) {
			throw new TransformerException("XPST0017 : An XPath 3.1 function 'sum' only allows one, or two arguments.", srcLocator); 
		}

		if (m_arg0 == null) {
			throw new TransformerException("XPST0017 : An XPath 3.1 function 'sum' is called without an argument.", srcLocator);
		}
		
		XObject arg0Obj = null;
		
		XObject arg1Obj = null;

		if (m_arg1 == null) {
			// An XPath function sum is called with one argument
			
			arg0Obj = getFunctionArgEffectiveValue(m_arg0, xctxt);						
			
			if (!((arg0Obj instanceof ResultSequence) || (arg0Obj instanceof XMLNodeCursorImpl) || (arg0Obj instanceof XPathArray))) {
				ResultSequence rSeq = new ResultSequence();
				rSeq.add(arg0Obj);

				arg0Obj = rSeq;
			}
			
			if (arg0Obj instanceof XMLNodeCursorImpl) {
				ResultSequence rSeq = getXdmSequenceFromNodeSet((XMLNodeCursorImpl)arg0Obj, xctxt);
				
				arg0Obj = rSeq;
			}
			else if (arg0Obj instanceof XPathArray) {
				arg0Obj = ((XPathArray)arg0Obj).atomize();
			}

			if (arg0Obj instanceof ResultSequence) {
				ResultSequence rSeq = (ResultSequence)arg0Obj;
				
				int size1 = rSeq.size();
				
				if (size1 == 1) {
				   XObject xObj = rSeq.item(0);
				   
				   if ((xObj instanceof XSNumericType) || (xObj instanceof XNumber) || (xObj instanceof XSYearMonthDuration) 
						                                                            || (xObj instanceof XSDayTimeDuration)) {
					   result = xObj;
					   
					   return result;
				   }
				   else if (xObj instanceof XSUntypedAtomic) {
					   String str1 = ((XSUntypedAtomic)xObj).stringValue();
                	   
                	   try {
                		  Double dbl = Double.valueOf(str1);                    		                      		                  		
                		  
                		  double dblValue = dbl.doubleValue();
                		  
                		  if ((Double.valueOf(dblValue)).isNaN()) {
                			  result = new XSDouble(Double.NaN);
                		  }
                		  else {
                			  result = new XSDouble(dblValue);
                		  }
                		  
                		  return result;
                	   }
                	   catch(NumberFormatException ex) {
                		  throw new TransformerException("FORG0006 : An XPath 3.1 function 'sum' is supplied with an xdm sequence, "
                                                                                                                                   + "that contains schema typed value "
                                                                                                                                   + "'untypedAtomic' that could'nt be cast to schema type 'double'.", srcLocator);   
                	   } 
				   }
				   else if (xObj instanceof XMLNodeCursorImpl) {
					   ResultSequence rSeq2 = getXdmSequenceFromNodeSet((XMLNodeCursorImpl)xObj, xctxt);
                	   
                	   FuncSum funcSum = new FuncSum();
                	   funcSum.setArg0(rSeq2);
                	   
                	   result = funcSum.execute(xctxt);
                	   
                	   return result;
				   }
				   else {
					   throw new TransformerException("FORG0006 : An XPath 3.1 function 'sum' is supplied with an argument that "
																						                                        + "contains neither an xdm "
																						                                        + "numeric value or, a duration value.", srcLocator); 
				   }
				}								
				else if (size1 != 0) {					
					BigInteger sum1 = null;
					
					Double sum2 = null;										
					
					XSYearMonthDuration xsYearMonthDurationSum = null;
					XSDayTimeDuration xsDayTimeDurationSum = null;
					
					boolean isSeqContainsNumber = false;
					boolean isSeqContainsXsYearMonthDuration = false;
					boolean isSeqContainsXsDayTimeDuration = false;
					
					boolean resultTypeXsDouble = false;
					
					int xsIntegerCount = 0;
					int xsDecimalCount = 0;
					int xsFloatCount = 0;
					int xsDoubleCount = 0;
															
					for (int idx = 0; idx < size1; idx++) {
					   XObject xObj = rSeq.item(idx);
					   
					   if (xObj instanceof XSNumericType) {
						   isSeqContainsNumber = true;
						   
						   if (isSeqContainsXsDayTimeDuration || isSeqContainsXsYearMonthDuration) {
							  throw new TransformerException("FORG0006 : An XPath 3.1 function 'sum' is supplied with an xdm sequence, "
													  		                                                                          + "that contains values of incompatible "
													  		                                                                          + "types.", srcLocator);  
						   }
						   
						   if (xObj instanceof XSDouble) {
							   XSDouble xsDouble = (XSDouble)xObj;
							   if (xsDouble.nan()) {
								   result = new XSDouble(Double.NaN); 
							   }
							   
							   xsDoubleCount++;
						   }
						   else if (xObj instanceof XSFloat) {
							   XSFloat xsFloat = (XSFloat)xObj;
							   if (xsFloat.nan()) {
								   result = new XSFloat(Float.NaN);
								   
								   for (int idx2 = (idx + 1); idx2 < size1; idx2++) {
									   /**
									    * Check whether there is any subsequent, xs:double
									    * 'NaN' exists within the supplied xdm sequence, 
									    * that takes precedence over xs:float 'NaN'.
									    */
									   
									   XObject xObj2 = rSeq.item(idx2);

									   if (xObj2 instanceof XSDouble) {
										   XSDouble xsDouble = (XSDouble)xObj2;
										   if (xsDouble.nan()) {
											   result = new XSDouble(Double.NaN);
											   
											   return result;
										   } 
									   }
								   }
							   }
							   
							   xsFloatCount++;
						   }
						   
						   if (result != null) {
							  return result;  
						   }
						   
						   if (xObj instanceof XSInteger) {							   
							  if (sum1 == null) {
								 sum1 = ((XSInteger)xObj).intValue();   
							  }
							  else {
								 BigInteger temp1 = ((XSInteger)xObj).intValue();								 
								 sum1 = sum1.add(temp1);
							  }
							  
							  xsIntegerCount++;
						   }						   
						   else {
							  resultTypeXsDouble = true;
							  
							  if (xObj instanceof XSDecimal) {
								 xsDecimalCount++;  
							  }
							  
							  String str1 = ((XSNumericType)xObj).stringValue();  
							  if (sum2 == null) {
								 sum2 = Double.valueOf(str1);  
							  }
							  else {
								 sum2 = (sum2 + Double.valueOf(str1));   
							  }
						   }
					   }
					   else if (xObj instanceof XNumber) {
						   isSeqContainsNumber = true;
						   
						   if (isSeqContainsXsDayTimeDuration || isSeqContainsXsYearMonthDuration) {
							   throw new TransformerException("FORG0006 : An XPath 3.1 function 'sum' is supplied with an xdm sequence, "
														   		                                                                        + "that contains values of incompatible "
														   		                                                                        + "types.", srcLocator);  
						   }
						   
						   resultTypeXsDouble = true;
						   
						   double dblValue = ((XNumber)xObj).num();
						   
						   if ((Double.valueOf(dblValue)).isNaN()) {
							  result = new XSDouble(Double.NaN);
							  
							  return result;
						   }
						   
						   if (sum2 == null) {
							   sum2 = dblValue;  
						   }
						   else {
							   sum2 = (sum2 + dblValue);   
						   }
					   }
					   else if (xObj instanceof XSYearMonthDuration) {
						   isSeqContainsXsYearMonthDuration = true;
						   
						   if (isSeqContainsNumber || isSeqContainsXsDayTimeDuration) {
							   throw new TransformerException("FORG0006 : An XPath 3.1 function 'sum' is supplied with an xdm sequence, "
														   		                                                                        + "that contains values of incompatible "
														   		                                                                        + "types.", srcLocator);  
						   }
						   
						   if (xsYearMonthDurationSum == null) {
							   xsYearMonthDurationSum = (XSYearMonthDuration)xObj;  
						   }
						   else {
							   xsYearMonthDurationSum = xsYearMonthDurationSum.add((XSYearMonthDuration)xObj);  
						   }
					   }
                       else if (xObj instanceof XSDayTimeDuration) {
                    	   isSeqContainsXsDayTimeDuration = true;
                    	   
                    	   if (isSeqContainsNumber || isSeqContainsXsYearMonthDuration) {
                    		   throw new TransformerException("FORG0006 : An XPath 3.1 function 'sum' is supplied with an xdm sequence, "
							                    		   		                                                                        + "that contains values of incompatible "
							                    		   		                                                                        + "types.", srcLocator); 
                    	   }
                    	   
                    	   if (xsDayTimeDurationSum == null) {
                    		   xsDayTimeDurationSum = (XSDayTimeDuration)xObj;  
                    	   }
                    	   else {
                    		   xsDayTimeDurationSum = xsDayTimeDurationSum.add((XSDayTimeDuration)xObj);  
                    	   }
					   }
                       else if (xObj instanceof XSUntypedAtomic) {
                    	   String str1 = ((XSUntypedAtomic)xObj).stringValue();
                    	   
                    	   try {
                    		  Double dbl = Double.valueOf(str1);                    		                      		  
                    		  
                    		  isSeqContainsNumber = true;
                    		  resultTypeXsDouble = true;
                    		  
                    		  if (isSeqContainsXsDayTimeDuration || isSeqContainsXsYearMonthDuration) {
   							     throw new TransformerException("FORG0006 : An XPath 3.1 function 'sum' is supplied with an xdm sequence, "
   														   		                                                                          + "that contains values of incompatible "
   														   		                                                                          + "types.", srcLocator);  
   						      }
                    		  
                    		  double dblValue = dbl.doubleValue();
                    		  
                    		  if ((Double.valueOf(dblValue)).isNaN()) {
                    			  result = new XSDouble(Double.NaN);

                    			  return result;
                    		  }

                    		  if (sum2 == null) {
                    			  sum2 = dblValue;  
                    		  }
                    		  else {
                    			  sum2 = (sum2 + dblValue);   
                    		  }
                    	   }
                    	   catch(NumberFormatException ex) {
                    		   throw new TransformerException("FORG0006 : An XPath 3.1 function 'sum' is supplied with an xdm sequence, "
                                                                                                                                        + "that contains schema typed value "
                                                                                                                                        + "'untypedAtomic' that could'nt be cast to schema type 'double'.", srcLocator);   
                    	   }
                       }
                       else if (xObj instanceof XMLNodeCursorImpl) {
                    	   ResultSequence rSeq2 = getXdmSequenceFromNodeSet((XMLNodeCursorImpl)xObj, xctxt);
                    	   
                    	   FuncSum funcSum = new FuncSum();
                    	   funcSum.setArg0(rSeq2);
                    	   
                    	   XObject xObj2 = funcSum.execute(xctxt);
                    	   XSDouble xsDouble = (XSDouble)xObj2;
                    	   
                    	   if (xsDouble.nan()) {
							  result = new XSDouble(Double.NaN);
							  
							  return result;
						   }
						   
						   xsDoubleCount++;
						   
						   resultTypeXsDouble = true;

						   if (sum2 == null) {
							   sum2 = xsDouble.doubleValue();  
						   }
						   else {
							   sum2 = (sum2 + xsDouble.doubleValue());   
						   }
                       }
                       else {
                    	   throw new TransformerException("FORG0006 : An XPath 3.1 function 'sum' is supplied with an argument that "
																								                                    + "contains neither an xdm "
																								                                    + "numeric value or, a duration value.", srcLocator);
                       }
					}
					
					if (resultTypeXsDouble) {
					   result = new XSDouble(sum2);
					}
					else if ((xsYearMonthDurationSum == null) && (xsDayTimeDurationSum == null)) {
					   result = new XSInteger(sum1);
					}
					else if (xsYearMonthDurationSum != null) {
					   result = xsYearMonthDurationSum; 
					}
					else if (xsDayTimeDurationSum != null) {
					   result = xsDayTimeDurationSum; 
					}
					
					XPathSequenceType xpathSeqTypeResultData = null;
					
					if (xsIntegerCount == size1) {
						xpathSeqTypeResultData = new XPathSequenceType();
						xpathSeqTypeResultData.setBuiltInSequenceType(XPathSequenceTypeSupport.XS_INTEGER);
					}
					else if (xsDecimalCount == size1) {
						xpathSeqTypeResultData = new XPathSequenceType();
						xpathSeqTypeResultData.setBuiltInSequenceType(XPathSequenceTypeSupport.XS_DECIMAL);
					}
					else if (xsFloatCount == size1) {												
						XSDouble xsDouble = (XSDouble)result;
						Double dbl1 = xsDouble.doubleValue();
						
						if (dbl1 < Double.valueOf(-3.4028235E38)) {						
						   result = new XSDouble(Double.NEGATIVE_INFINITY);
						}						
						else if (dbl1 > Double.valueOf(3.4028235E38)) {
						   result = new XSDouble(Double.POSITIVE_INFINITY);
						}
						
						xpathSeqTypeResultData = new XPathSequenceType();
						xpathSeqTypeResultData.setBuiltInSequenceType(XPathSequenceTypeSupport.XS_FLOAT);
					}
					else if (xsDoubleCount == size1) {
						xpathSeqTypeResultData = new XPathSequenceType();
						xpathSeqTypeResultData.setBuiltInSequenceType(XPathSequenceTypeSupport.XS_DOUBLE);
					}
					
					if (xpathSeqTypeResultData != null) {
					   result.setCastAsType(xpathSeqTypeResultData);
					}
				}				
				else {					
					result = new XSInteger("0");

					return result;
				}
			}			
			else {
				throw new TransformerException("FORG0006 : An XPath 3.1 function 'sum' is supplied with an argument that "
						                                                                                                 + "contains neither an xdm "
						                                                                                                 + "numeric value or, a duration value.", srcLocator);
			}
		}
		else {
			// An XPath function sum is called with two arguments

			arg0Obj = getFunctionArgEffectiveValue(m_arg0, xctxt);
			
			arg1Obj = getFunctionArgEffectiveValue(m_arg1, xctxt);						
			
			ResultSequence rSeq1 = null;
			
			if (arg0Obj instanceof ResultSequence) {
				rSeq1 = (ResultSequence)arg0Obj; 
			}
			else if (arg0Obj instanceof XMLNodeCursorImpl) {
				rSeq1 = getXdmSequenceFromNodeSet((XMLNodeCursorImpl)arg0Obj, xctxt); 
			}
			else {
				rSeq1 = new ResultSequence();
				rSeq1.add(arg0Obj);
			}
			
			if (rSeq1.size() > 0) {			   			   
				FuncSum funcSum = new FuncSum();
				funcSum.setArg0(rSeq1);

				result = funcSum.execute(xctxt);
			}
			else {
				/**
				 * An XPath function 'sum' first argument is an empty sequence.
				 * Therefore, XPath function 'sum' second argument shall be 
				 * the result.
				 */
				
				ResultSequence rSeqArg1 = new ResultSequence(); 

				if (arg1Obj instanceof XMLNodeCursorImpl) {								
					if (((XMLNodeCursorImpl)arg1Obj).getLength() > 0) {
						rSeqArg1 = getXdmSequenceFromNodeSet((XMLNodeCursorImpl)arg1Obj, xctxt); 
					}
					else {
						rSeqArg1 = null;
					}
				}
				else if (arg1Obj instanceof XPathArray) {
					ResultSequence rSeq = ((XPathArray)arg1Obj).atomize();

					if (rSeq.size() > 0) {
						rSeqArg1 = rSeq;
					}
					else {
						rSeqArg1 = null;
					}
				}
				else if (arg1Obj instanceof ResultSequence) {				
					ResultSequence rSeq = (ResultSequence)arg1Obj;

					if (rSeq.size() > 0) {
						rSeqArg1 = rSeq;
					}
					else {
						rSeqArg1 = null;
					}
				}
				else {
					rSeqArg1.add(arg1Obj);
				}

				if (rSeqArg1 != null) {
					if (rSeqArg1.size() == 1) {				   
						arg1Obj = rSeqArg1.item(0);
					}
					else if (rSeqArg1.size() == 0) {
						arg1Obj = null; 
					}
					else {
						throw new TransformerException("XPTY0004 : An XPath 3.1 function 'sum', second argument cardinality "
								                                                                                            + "should be zero or one.", srcLocator); 
					}
				}
				else {
					arg1Obj = null;
				}

				if ((arg1Obj != null) && !((arg1Obj instanceof XSAnyAtomicType) || (arg1Obj instanceof XNumber) 
																			    || (arg1Obj instanceof XString)
						                                                        || (arg1Obj instanceof XBoolean)
						                                                        || (arg1Obj instanceof XBooleanStatic))) {
					throw new TransformerException("FORG0006 : An XPath 3.1 function 'sum', second argument doesn't conform with XML Schema "
							                                                                                               + "simple type.", srcLocator);
				}

				if (arg1Obj != null) {
					result = arg1Obj;
				}
				else {
					result = new ResultSequence(); 
				} 
			}
		}
		
		return result;
	}

	/**
	 * Method definition, to get an xdm sequence from nodeset.
	 * 
	 * @param xmlNodeCursorImpl                    The supplied XMLNodeCursorImpl, 
	 *                                             nodeset object.         
	 * @param xctxt                                An XPath context object
	 * @return                                     An xdm sequence object 
	 */
	private ResultSequence getXdmSequenceFromNodeSet(XMLNodeCursorImpl xmlNodeCursorImpl, XPathContext xctxt) {
		
		ResultSequence rSeq = new ResultSequence();
		
		int nextNode = DTM.NULL;
		
		DTMCursorIterator dtmCursorIter = xmlNodeCursorImpl.iter();	
		
		while ((nextNode = dtmCursorIter.nextNode()) != DTM.NULL) {
		   XMLNodeCursorImpl xmlNodeCursorImpl2 = new XMLNodeCursorImpl(nextNode, xctxt);
		   String str1 = xmlNodeCursorImpl2.str();
		   
		   XSDouble xsDouble = null;
		   XSYearMonthDuration xsYearMonthDuration = null;
		   XSDayTimeDuration xsDayTimeDuration = null;

		   try {
			   xsDouble = XSDouble.parseDouble(str1);					   
			   rSeq.add(xsDouble);
		   }
		   catch (TransformerException ex) {
		       // No op
		   }

		   if (xsDouble == null) {
			   try {
				   xsYearMonthDuration = (XSYearMonthDuration)(XSYearMonthDuration.parseYearMonthDuration(str1));
				   rSeq.add(xsYearMonthDuration);
			   }
			   catch (TransformerException ex) {
				   // No op
			   }
		   }

		   if ((xsDouble == null) && (xsYearMonthDuration == null)) {
			   try {
				   xsDayTimeDuration = (XSDayTimeDuration)(XSDayTimeDuration.parseDayTimeDuration(str1));
				   rSeq.add(xsDayTimeDuration);
			   }
			   catch (TransformerException ex) {
				   // No op
			   }
		   }				   				  
		}
		
		return rSeq;
	}
}
