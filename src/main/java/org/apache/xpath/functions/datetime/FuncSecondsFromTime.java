/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the  "License");
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

import javax.xml.transform.SourceLocator;

import org.apache.xpath.Expression;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.FunctionOneArg;
import org.apache.xpath.objects.XObject;

import xml.xpath31.processor.types.XSDecimal;
import xml.xpath31.processor.types.XSTime;

/**
 * Implementation of fn:seconds-from-time function.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncSecondsFromTime extends FunctionOneArg
{
  private static final long serialVersionUID = -5441092496752221630L;

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
	  
	  XObject arg0Val = arg0.execute(xctxt);
	  
	  if (!(arg0Val instanceof XSTime)) {
		 throw new javax.xml.transform.TransformerException("XPTY0004 : The required item type of the first argument of "
		 		                                                   + "fn:seconds-from-time() is xs:time, whereas the supplied "
		 		                                                   + "argument is not conformant.", srcLocator);   
	  }
	  else {
		 XSTime xsTimeVal = (XSTime)arg0Val;
		 result = new XSDecimal(xsTimeVal.second() + "");
	  }
	  
	  return result;
  }
  
}
