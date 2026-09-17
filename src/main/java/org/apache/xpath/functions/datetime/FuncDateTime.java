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
package org.apache.xpath.functions.datetime;

import java.util.Calendar;

import javax.xml.transform.SourceLocator;

import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.Function2Args;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;

import xml.xpath31.processor.types.XSDate;
import xml.xpath31.processor.types.XSDateTime;
import xml.xpath31.processor.types.XSDuration;
import xml.xpath31.processor.types.XSTime;

/**
 * Implementation of XPath 3.1 function fn:dateTime.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncDateTime extends Function2Args
{

  private static final long serialVersionUID = 4597387349539359750L;
  
  /**
   * Class constructor.
   */
  public FuncDateTime() {
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
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
  {
	  XObject result = null;
	  
	  SourceLocator srcLocator = xctxt.getSAXLocator();
	  
	  XObject arg0Val = getFunctionArgEffectiveValue(m_arg0, xctxt);
	  
	  XObject arg1Val = getFunctionArgEffectiveValue(m_arg1, xctxt);
	  
	  if (arg0Val instanceof ResultSequence) {
		 ResultSequence rSeq1 = (ResultSequence)arg0Val;
		 
		 if (rSeq1.size() == 0) {
		    result = new ResultSequence();
		    
		    return result;
		 }
		 else if (rSeq1.size() == 1) {
			arg0Val = rSeq1.item(0);  
		 }
	  }
	  
	  if ((arg1Val instanceof ResultSequence) && (((ResultSequence)arg1Val).size() == 0)) {
		  ResultSequence rSeq2 = (ResultSequence)arg1Val;

		  if (rSeq2.size() == 0) {
			  result = new ResultSequence();

			  return result;
		  }
		  else if (rSeq2.size() == 1) {
			  arg1Val = rSeq2.item(0);  
		  }
	  }
	  
	  if (!(arg0Val instanceof XSDate)) {
		 throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath 3.1 function 'dateTime' first argument is XML schema type 'date'.", srcLocator);  
	  }	  
	  
	  if (!(arg1Val instanceof XSTime)) {
		 throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath 3.1 function 'dateTime' first argument is XML schema type 'time'.", srcLocator); 
	  }
	  
	  XSDate dateVal = (XSDate)arg0Val;
	  XSTime timeVal = (XSTime)arg1Val;
	  
	  XSDuration timeZone1 = dateVal.getTimezone();
	  XSDuration timeZone2 = timeVal.getTimezone();
	  
	  XSDuration resultTimeZone;
	  
	  if ((timeZone1 == null) && (timeZone2 == null)) {
		 resultTimeZone = null;   
	  }	  
	  else if ((timeZone1 != null) && (timeZone2 == null)) {
		 resultTimeZone = timeZone1; 
	  }	  
	  else if ((timeZone1 == null) && (timeZone2 != null)) {
		 resultTimeZone = timeZone2; 
	  }
	  else if (timeZone1.equals(timeZone2)) {
		 resultTimeZone = timeZone1;		  
	  }
	  else {
		 throw new javax.xml.transform.TransformerException("FORG0008 : An XPath 3.1 function 'dateTime' is called with "
		 		                                                                                           + "XML schema type 'date' and 'time' arguments, "
		 		                                                                                           + "that have different timezones.", srcLocator); 
	  }
	  
	  Calendar calendar = Calendar.getInstance();
	  
	  calendar.set(dateVal.year(), dateVal.month() - 1, dateVal.day());
	  calendar.set(Calendar.HOUR_OF_DAY, timeVal.hour());
	  calendar.set(Calendar.MINUTE, timeVal.minute());
	  
	  double secsValue = timeVal.second();
	  int secs = (int)secsValue;
	  int deltaMilliSecs = 0;
	  
	  if (secsValue > secs) {
	     deltaMilliSecs = (int)((secsValue - secs) * 1000);
	  }
	  
	  calendar.set(Calendar.SECOND, secs);
	  calendar.set(Calendar.MILLISECOND, deltaMilliSecs);
	  
	  result = new XSDateTime(calendar, resultTimeZone);
	  
	  return result;
	  
  }
  
}
