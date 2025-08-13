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
import org.apache.xpath.functions.FunctionOneArg;
import org.apache.xpath.objects.XObject;

import xml.xpath31.processor.types.XSDateTime;
import xml.xpath31.processor.types.XSDecimal;

/**
 * Implementation of fn:seconds-from-dateTime function.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncSecondsFromDateTime extends FunctionOneArg
{

  private static final long serialVersionUID = -4967761326818696933L;
  
  /**
   * Class constructor.
   */
  public FuncSecondsFromDateTime() {
  	 m_defined_arity = new Short[] { 1 };	
  }

  /**
   * Implementation of the function. The function must return
   * a valid object.
   * 
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
	  
	  XObject arg0Val = arg0.execute(xctxt);
	  
	  if (!(arg0Val instanceof XSDateTime)) {
		 throw new javax.xml.transform.TransformerException("XPTY0004 : The required item type of the first argument of "
		 		                                                   + "fn:seconds-from-dateTime() is xs:dateTime, whereas the supplied "
		 		                                                   + "argument is not conformant.", srcLocator);   
	  }
	  else {
		 XSDateTime xsDateTimeVal = (XSDateTime)arg0Val;
		 
		 Calendar calendar = xsDateTimeVal.getCalendar();
		 int seconds = calendar.get(Calendar.SECOND);
		 int milliSecs = calendar.get(Calendar.MILLISECOND);
		 boolean ms1 = false;
		 if ((milliSecs % 100) == 0) {
			 milliSecs = (calendar.get(Calendar.MILLISECOND)) / 100;
			 ms1 = true;
		 }
		 
		 String secondsStr = null;		 
		 if (milliSecs == 0) {
			 secondsStr = String.valueOf(seconds); 
		 }
		 else {
			 String milliSecStr = null;
			 if (ms1) {
				 milliSecStr = String.valueOf(milliSecs);  
			 }
			 else if (milliSecs < 10) {
				 milliSecStr = "00" + milliSecs;   
			 }
			 else if (milliSecs < 100) {
				 milliSecStr = "0" + milliSecs; 
			 }
			 else if (milliSecs > 100) {
				 milliSecStr = String.valueOf(milliSecs);  
			 }
			 
			 secondsStr = seconds + "." + milliSecStr;  
		 }
		 
		 result = new XSDecimal(secondsStr); 

	  }
	  
	  return result;
  }
  
}
