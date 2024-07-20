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
/*
 * $Id$
 */
package org.apache.xpath.functions;

import java.math.BigDecimal;

import javax.xml.transform.SourceLocator;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xpath.Expression;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;

import xml.xpath31.processor.types.XSAnyURI;
import xml.xpath31.processor.types.XSBoolean;
import xml.xpath31.processor.types.XSNumericType;
import xml.xpath31.processor.types.XSString;
import xml.xpath31.processor.types.XSUntypedAtomic;

/**
 * Implementation of XPath 3.1 fn:boolean function.
 * 
 * @xsl.usage advanced
 */
public class FuncBoolean extends FunctionOneArg
{
    static final long serialVersionUID = 4328660760070034592L;
    
    /**
     * Default constructor.
     */
    public FuncBoolean() {
       // no op	
    }
    
    /**
     * Class constructor.
     */
    public FuncBoolean(Expression arg) {
       m_arg0 = arg; 
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
	
	XObject xObj = m_arg0.execute(xctxt);
	
	if (xObj instanceof ResultSequence) {
	   ResultSequence rSeq = (ResultSequence)xObj;
	   if (rSeq.size() == 0) {
		  result = new XSBoolean(false); 
	   }
	   else {
		  throw new javax.xml.transform.TransformerException("FORG0006 : Invalid argument type, for the "
                                                                               + "function call fn:boolean.", srcLocator); 
	   }
	}
	else if (xObj instanceof XNodeSet) {
	   if (((XNodeSet)xObj).getLength() == 0) {
	      result = new XSBoolean(false);
	   }
	   else {
		  result = new XSBoolean(true); 
	   }
	}
	else if (xObj instanceof XSBoolean) {
	   result = xObj;
	}
	else if (xObj instanceof XBoolean) {
	   XBoolean xBool = (XBoolean)xObj;
	   result = new XSBoolean(xBool.bool());
	}
	else if ((xObj instanceof XSString) || (xObj instanceof XString) || 
			 (xObj instanceof XSAnyURI) || (xObj instanceof XSUntypedAtomic)) {
	   String argStrVal = XslTransformEvaluationHelper.getStrVal(xObj);
	   if ((argStrVal == null) || (argStrVal.length() == 0)) {
		  result = new XSBoolean(false); 
	   }
	   else {
		  result = new XSBoolean(true);
	   }
	}
	else if (xObj instanceof XSNumericType) {
	   XSNumericType xsNumericType = (XSNumericType)xObj;
	   String argStrVal = xsNumericType.stringValue();
	   BigDecimal argBigDecimal = new BigDecimal(argStrVal);	   
	   if ("NaN".equals(argStrVal)) {
		  result = new XSBoolean(false);  
	   }
	   else if (argBigDecimal.compareTo(BigDecimal.valueOf(0)) == 0) {
		  result = new XSBoolean(false); 
	   }
	   else {
		  result = new XSBoolean(true); 
	   }
	}
    else if (xObj instanceof XNumber) {
	   double argDbl = ((XNumber)xObj).num();
	   if (argDbl == 0d) {
		  result = new XSBoolean(false);  
	   }
	   else {
		  result = new XSBoolean(true);
	   }
	}
    else {
       throw new javax.xml.transform.TransformerException("FORG0006 : Invalid argument type, for the "
       		                                                                + "function call fn:boolean.", srcLocator); 
    } 
	
	return result;
  }
  
}
