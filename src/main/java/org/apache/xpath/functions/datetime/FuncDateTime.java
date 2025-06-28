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

import org.apache.xpath.Expression;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.Function2Args;
import org.apache.xpath.objects.XObject;

import xml.xpath31.processor.types.XSDate;
import xml.xpath31.processor.types.XSDateTime;
import xml.xpath31.processor.types.XSDuration;
import xml.xpath31.processor.types.XSTime;

/**
 * Implementation of fn:dateTime function.
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
  	 m_defined_arity = new Short[] { 2 };	
  }

  /**
   * Execute the function. The function must return
   * a valid object.
   * @param xctxt The current execution context.
   * @return A valid XObject.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
  {
	  XObject result = null;
	  
	  SourceLocator srcLocator = xctxt.getSAXLocator();
	  
	  Expression arg0 = getArg0();
	  Expression arg1 = getArg1();
	  
	  XObject arg0Val = arg0.execute(xctxt);
	  XObject arg1Val = arg1.execute(xctxt);
	  
	  if (!(arg0Val instanceof XSDate)) {
		 throw new javax.xml.transform.TransformerException("XPTY0004 : The required item type of the first argument of fn:dateTime() is xs:date, "
		 		                                                                  + "whereas the supplied argument is not conformant.", srcLocator);  
	  }	  
	  
	  if (!(arg1Val instanceof XSTime)) {
		 throw new javax.xml.transform.TransformerException("XPTY0004 : The required item type of the second argument of fn:dateTime() is xs:time, "
                                                                                  + "whereas the supplied argument is not conformant.", srcLocator); 
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
		 throw new javax.xml.transform.TransformerException("FORG0008 : The two arguments to fn:dateTime have inconsistent timezones.", srcLocator); 
	  }
	  
	  Calendar cal = Calendar.getInstance();
	  cal.set(dateVal.year(), dateVal.month() - 1, dateVal.day());
	  cal.set(Calendar.HOUR_OF_DAY, timeVal.hour());
	  cal.set(Calendar.MINUTE, timeVal.minute());
	  cal.set(Calendar.SECOND, (new Double(Math.floor(timeVal.second())).intValue()));
	  cal.set(Calendar.MILLISECOND, 0);
	  
	  result = new XSDateTime(cal, resultTimeZone);
	  
	  return result;
  }
  
}
