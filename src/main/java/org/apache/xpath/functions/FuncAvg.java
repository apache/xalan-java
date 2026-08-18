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

import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xpath.XPathContext;
import org.apache.xpath.composite.XPathSequenceTypeSupport;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathArray;

import xml.xpath31.processor.types.XSDayTimeDuration;
import xml.xpath31.processor.types.XSDecimal;
import xml.xpath31.processor.types.XSDouble;
import xml.xpath31.processor.types.XSDuration;
import xml.xpath31.processor.types.XSFloat;
import xml.xpath31.processor.types.XSInteger;
import xml.xpath31.processor.types.XSNumericType;
import xml.xpath31.processor.types.XSUntypedAtomic;
import xml.xpath31.processor.types.XSYearMonthDuration;

/**
 * Implementation of an XPath 3.1 function fn:avg.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncAvg extends FunctionOneArg
{

  private static final long serialVersionUID = 6282866669363344636L;
  
  private static final int DECIMAL_RESULT_SCALE = 10;
  
  /**
   * Class constructor.
   */
  public FuncAvg() {
	  m_arity = new Short[] { 1 };
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
      
      XObject arg0Obj = getFunctionArgEffectiveValue(m_arg0, xctxt);      
      
      if ((arg0Obj instanceof ResultSequence) && (((ResultSequence)arg0Obj).size() == 0)) {
    	 return new ResultSequence();  
      }
      else if (arg0Obj instanceof XMLNodeCursorImpl) {
    	 XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)arg0Obj;
    	 
    	 if (xmlNodeCursorImpl.getLength() == 0) {
    		return new ResultSequence();  
    	 }
      }
      
      if (arg0Obj instanceof ResultSequence) {
    	 ResultSequence rSeq = (ResultSequence)arg0Obj;    	 
    	 int size1 = rSeq.size();
    	 
    	 boolean isXdmNodeSeq = (rSeq.item(0) instanceof XMLNodeCursorImpl);    	     	     	 
    	 boolean isNumericSeq = ((rSeq.item(0) instanceof XSNumericType) || (rSeq.item(0) instanceof XNumber) 
    			                                                         || (rSeq.item(0) instanceof XSUntypedAtomic));
    	 boolean isXsYearMonthDurationSeq = (rSeq.item(0) instanceof XSYearMonthDuration); 
    	 boolean isXsDayTimeDurationSeq = (rSeq.item(0) instanceof XSDayTimeDuration);
    	 
    	 double dblSum1 = 0.0;
    	 float floatSum1 = 0.0f;
    	 BigDecimal decimalSum1 = BigDecimal.valueOf(0);
    	 
    	 XSYearMonthDuration xsYearMonthDurationSum = (XSYearMonthDuration)(XSYearMonthDuration.parseYearMonthDuration("P0M"));
    	 XSDayTimeDuration xsDayTimeDurationSum = (XSDayTimeDuration)(XSDayTimeDuration.parseDayTimeDuration("P0D"));
    	 
    	 String resultTypeStr = null;
    	      	 
    	 for (int idx = 0; idx < size1; idx++) {
    		XObject xObj = rSeq.item(idx);
    		
    		if (isXdmNodeSeq) {
    		   if (xObj instanceof XMLNodeCursorImpl) {
    			  try {
    				 XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)xObj;
    				 String str1 = xmlNodeCursorImpl.str();
    				 
    				 dblSum1 += Double.valueOf(str1);   
    			  }
    			  catch (NumberFormatException ex) {
    				 throw new TransformerException("FORG0006 : An XPath 3.1 function 'avg' has evaluation error, "
    				 		                                                               + "since the supplied xdm nodeset has a node "
    				 		                                                               + "whose string value couldn't be cast to schema type double.", srcLocator);  
    			  }
    		   }
    		   else {
    			  throw new TransformerException("FORG0006 : An XPath 3.1 function 'avg' has evaluation error, since the supplied "
    			  		                                                                               + "function argument sequence contains "
    			  		                                                                               + "a mix of an xdm node, and other xdm types.", srcLocator); 
    		   }
    		   
    		   resultTypeStr = XPathSequenceTypeSupport.DOUBLE;
    		}
    		else if (isNumericSeq) {
    			if ((xObj instanceof XSNumericType) || (xObj instanceof XNumber)) {
    				if (xObj instanceof XSDouble) {
    					if (XPathSequenceTypeSupport.FLOAT.equals(resultTypeStr)) {
    						double dbl = ((XSDouble)xObj).doubleValue();    						
    						
    						dblSum1 = floatSum1;     						
    						dblSum1 += dbl;
    					}
    					else {    						
    						double dbl = ((XSDouble)xObj).doubleValue();
    						dblSum1 += dbl;
    					}
    					
    					resultTypeStr = XPathSequenceTypeSupport.DOUBLE;
    				}
    				else if (xObj instanceof XNumber) {
    					if (XPathSequenceTypeSupport.FLOAT.equals(resultTypeStr)) {
    						double dbl = ((XNumber)xObj).num();    						
    						dblSum1 = floatSum1;
    						
    						dblSum1 += dbl;
    					}
    					else {    						
    						double dbl = ((XNumber)xObj).num();
    						dblSum1 += dbl;
    					}
    					
    					resultTypeStr = XPathSequenceTypeSupport.DOUBLE;
    				}
    				else if (xObj instanceof XSFloat) {
    					if (XPathSequenceTypeSupport.DOUBLE.equals(resultTypeStr)) {
    					    double dbl = ((XSFloat)xObj).floatValue();
    					    dblSum1 += dbl;
    					}
    					else {    						    						
    						float flt = ((XSFloat)xObj).floatValue();    						
    						floatSum1 += flt;
    						
    						resultTypeStr = XPathSequenceTypeSupport.FLOAT;
    					}    					    					
    				}
    				else if (xObj instanceof XSDecimal) {
    					if (XPathSequenceTypeSupport.DOUBLE.equals(resultTypeStr)) {
    					   XSDecimal xsDecimal = (XSDecimal)xObj;
    					   
    					   double dbl = xsDecimal.doubleValue();
    					   dblSum1 += dbl;
    					}
    					else if (XPathSequenceTypeSupport.FLOAT.equals(resultTypeStr)) {
    					   XSDecimal xsDecimal = (XSDecimal)xObj;
     					   
     					   float flt = (float)(xsDecimal.doubleValue());
     					   floatSum1 += flt; 
    					}
    					else if (XPathSequenceTypeSupport.DECIMAL.equals(resultTypeStr)) {
    					   XSDecimal xsDecimal = (XSDecimal)xObj;
    					   
    					   BigDecimal bigDecimal = xsDecimal.getValue();
    					   decimalSum1 = decimalSum1.add(bigDecimal); 
    					}
    					else {    					       					   
                           XSDecimal xsDecimal = (XSDecimal)xObj;
    					   
                           BigDecimal bigDecimal = xsDecimal.getValue();
    					   resultTypeStr = XPathSequenceTypeSupport.DECIMAL;
    					   
    					   decimalSum1 = decimalSum1.add(bigDecimal); 
    					}    					
    				}
    			}
    			else if (xObj instanceof XSUntypedAtomic) {
    				String str1 = ((XSUntypedAtomic)xObj).stringValue();

    				try {
    				    double dbl = Double.valueOf(str1);
    				    
    				    if (XPathSequenceTypeSupport.FLOAT.equals(resultTypeStr)) {   						    						
    						dblSum1 = floatSum1;     						
    						dblSum1 += dbl;
    					}
    					else {    						
    						dblSum1 += dbl;
    					}
    					
    					resultTypeStr = XPathSequenceTypeSupport.DOUBLE;
    				}
    				catch (NumberFormatException ex) {
    					throw new TransformerException("FORG0006 : An XPath 3.1 function 'avg' has evaluation error, since the supplied"
    							                                                                             + " function argument sequence contains "
    							                                                                             + "schema type value 'untypedAtomic' which couldn't be cast to double.", srcLocator); 
    				}
    			}
    			else {
    			    throw new TransformerException("FORG0006 : An XPath 3.1 function 'avg' has evaluation error, since the supplied "
																		                                 + "function argument sequence contains "
																		                                 + "a mix of xdm numeric value, and other xdm types.", srcLocator);
    			}
    		}
    		else if (isXsYearMonthDurationSeq) {
    			if (xObj instanceof XSYearMonthDuration) {
    			   XSYearMonthDuration xsYearMonthDuration = (XSYearMonthDuration)xObj;
    			   
    			   xsYearMonthDurationSum = xsYearMonthDurationSum.add(xsYearMonthDuration);
    			}
    			else {
    			   throw new TransformerException("FORG0006 : An XPath 3.1 function 'avg' has evaluation error, since the supplied "
																			                            + "function argument sequence contains "
																			                            + "a mix of xdm 'yearMonthDuration' value, and other xdm types.", srcLocator);
    			}
    		}
    		else if (isXsDayTimeDurationSeq) {
                if (xObj instanceof XSDayTimeDuration) {
                	XSDayTimeDuration xsDayTimeDuration = (XSDayTimeDuration)xObj;

                	xsDayTimeDurationSum = xsDayTimeDurationSum.add(xsDayTimeDuration);
    			}
    			else {
    			   throw new TransformerException("FORG0006 : An XPath 3.1 function 'avg' has evaluation error, since the supplied "
																			                            + "function argument sequence contains "
																			                            + "a mix of xdm 'dayTimeDuration' value, and other xdm types.", srcLocator);
    			}
    		}
    		else if ((xObj instanceof XSDuration) && !((xObj instanceof XSYearMonthDuration) || (xObj instanceof XSDayTimeDuration))) {
    			throw new TransformerException("FORG0006 : An XPath 3.1 function 'avg' is supplied with "
																				                         + "an argument of schema type 'duration' that "
																				                         + "is neither an XML schema type 'dayTimeDuration' "
																				                         + "nor an 'yearMonthDuration'.", srcLocator);
    		}
    		else {
    		    throw new TransformerException("FORG0006 : An XPath 3.1 function 'avg' is supplied with a sequence argument, "
    		    		                                                                                  + "that contains a value that is "
    		    		                                                                                  + "neither numeric or subtypes of schema type 'duration'.", srcLocator);
    		}
    	 }
    	 
    	 if (isXdmNodeSeq) {
    		result = new XSDouble(dblSum1 / size1); 
    	 }
    	 else if (isNumericSeq) {
    		 if (XPathSequenceTypeSupport.DOUBLE.equals(resultTypeStr)) {
    			result = new XSDouble(dblSum1 / size1); 
    		 }
    		 else if (XPathSequenceTypeSupport.FLOAT.equals(resultTypeStr)) {
    			result = new XSFloat(floatSum1 / size1); 
    		 }
    		 else {
    			BigDecimal bigDecimalResult = decimalSum1.divide(BigDecimal.valueOf(size1), DECIMAL_RESULT_SCALE, RoundingMode.HALF_UP);
    			
    			result = new XSDecimal(bigDecimalResult);
    		 }
    	 }
    	 else if (isXsYearMonthDurationSeq) {
    		 result = xsYearMonthDurationSum.div(new XSInteger(size1 + "")); 
    	 }
    	 else if (isXsDayTimeDurationSeq) {
    		 result = xsDayTimeDurationSum.div(new XSInteger(size1 + ""));;
    	 }
      }
      else if (arg0Obj instanceof XPathArray) {
    	 XPathArray xpathArr = (XPathArray)arg0Obj;
    	 
    	 ResultSequence rSeq1 = xpathArr.atomize();
    	 
    	 FuncAvg funcAvg = new FuncAvg();
    	 funcAvg.setArg0(rSeq1);
    	 
    	 result = funcAvg.execute(xctxt);
      }
      else if (arg0Obj instanceof XMLNodeCursorImpl) {
    	 XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)arg0Obj;
    	 
    	 double dblSum1 = 0.0;
    	 int count = 0;
    	 
    	 DTMCursorIterator dtmCursorIter = xmlNodeCursorImpl.iter();
    	 int nextNode = DTM.NULL;
    	 
    	 while ((nextNode = dtmCursorIter.nextNode()) != DTM.NULL) {
    		XMLNodeCursorImpl node1 = new XMLNodeCursorImpl(nextNode, xctxt);    		
    		String str1 = node1.str();    		
    		count++;
    		
    		try {
    		   dblSum1 += Double.valueOf(str1); 
    		}
    		catch (NumberFormatException ex) {
    		   throw new TransformerException("FORG0006 : An XPath 3.1 function 'avg' has evaluation error, "
    		   		                                                                     + "since one of the supplied xdm "
    		   		                                                                     + "node value could'nt be cast to schema type double.", srcLocator);
    		}
    	 }
    	 
    	 double avg1 = (dblSum1 / count);
    	 
    	 result = new XSDouble(avg1);
      }      
      else if (arg0Obj instanceof XNumber) {
    	 XNumber xNumber = (XNumber)arg0Obj;
    	 
    	 if (xNumber.getXsDecimal() != null) {
    		result = xNumber.getXsDecimal();  
    	 }
    	 else if (xNumber.getXsDouble() != null) {
    		result = xNumber.getXsDouble(); 
    	 }
    	 else if (xNumber.getXsInteger() != null) {
    		result = xNumber.getXsInteger();  
    	 }
    	 else {
    		result = new XSDouble(xNumber.num()); 
    	 }
      }
      else if ((arg0Obj instanceof XSNumericType) || (arg0Obj instanceof XSYearMonthDuration) 
    		                                      || (arg0Obj instanceof XSDayTimeDuration)) {
    	 result = arg0Obj;  
      }
      else if (arg0Obj instanceof XSDuration) {
    	 throw new TransformerException("FORG0006 : An XPath 3.1 function 'avg' is supplied with "
    	 		                                                         + "an argument of schema type 'duration' that "
    	 		                                                         + "is neither an XML schema type 'dayTimeDuration' "
    	 		                                                         + "nor an 'yearMonthDuration'.", srcLocator); 
      }
      else if (arg0Obj instanceof XSUntypedAtomic) {
    	 String str1 = ((XSUntypedAtomic)arg0Obj).stringValue();
    	 
    	 try {
    		result = new XSDouble(Double.valueOf(str1)); 
    	 }
    	 catch (NumberFormatException ex) {
    	    throw new TransformerException("FORG0006 : An XPath 3.1 function 'avg' has evaluation error, since the supplied"
    	    		                                                                                          + " function argument is of schema "
    	    		                                                                                          + "type 'untypedAtomic' which could'nt be cast to double.", srcLocator); 
    	 }
      }
      
      return result;
  }
  
}
