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

import javax.xml.transform.SourceLocator;

import org.apache.xalan.templates.Constants;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;

import xml.xpath31.processor.types.XSQName;

/**
 * An implementation of, XPath 3.1 function fn:error.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncError extends FunctionMultiArgs
{
    
	private static final long serialVersionUID = 2926562472111015885L;

	/**
     * Default constructor.
     */
    public FuncError() {
    	m_defined_arity = new Short[] { 0, 1, 2, 3 };	
    }

  /**
   * Evaluate the function. The function must return a valid object.
   * 
   * @param xctxt The current execution context.
   * @return A valid XObject.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
  {      
	  
      XObject errCode = null;
      XObject errDesc = null;
      
      SourceLocator srcLocator = xctxt.getSAXLocator();
      
      if (m_arg0 != null) {
    	  errCode = m_arg0.execute(xctxt); 
      }

      if (m_arg1 != null) {
    	  errDesc = m_arg1.execute(xctxt); 
      }

      if (m_arg2 != null) {
    	  // Presently, an errValue object instance is always an empty sequence,
    	  // and this is ignored by an implementation of this class.
    	  XObject errValue = m_arg2.execute(xctxt);
      }
      
      String errMesgStr = "";
      if (errDesc != null) {
    	 errMesgStr = XslTransformEvaluationHelper.getStrVal(errCode) + " : " + XslTransformEvaluationHelper.getStrVal(errDesc);   
      }
      else if (errCode != null) {
    	 errMesgStr = XslTransformEvaluationHelper.getStrVal(errCode) + " : Raised by XPath 'error' function call."; 
      }
      else {
    	 XObject defErrCode = new XSQName("err", "FOER0000", Constants.XSL_ERROR_NAMESACE);
    	 errMesgStr = XslTransformEvaluationHelper.getStrVal(defErrCode) + " : An XPath dynamic error has occured."; 
      }
      
      // This function always raises an XPath dynamic error
      
      throw new javax.xml.transform.TransformerException(errMesgStr, srcLocator);    
  }
  
}
