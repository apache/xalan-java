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
/*
 * $Id$
 */
package org.apache.xpath.operations;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.XSSimpleType;
import org.apache.xerces.impl.dv.xs.XSSimpleTypeDecl;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xpath.composite.SequenceTypeData;
import org.apache.xpath.composite.SequenceTypeSupport;
import org.apache.xpath.objects.XObject;

import xml.xpath31.processor.types.XSBoolean;

/**
 * The XPath 3.1 "castable as" operation.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class CastableAs extends Operation
{

  private static final long serialVersionUID = 7722658885378043019L;

  /**
   * Apply the operation to two operands, and return the result.
   *
   * @param left non-null reference to the evaluated left operand.
   * @param right non-null reference to the evaluated right operand.
   *
   * @return non-null reference to the XObject that represents the result of the operation.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject operate(XObject left, XObject right) 
                                                 throws javax.xml.transform.TransformerException
  {
	  XObject result = null;
      
      SequenceTypeData seqTypedData = (SequenceTypeData)right;
      
      try {
         result = SequenceTypeSupport.castXdmValueToAnotherType(left, seqTypedData, true);
         if (result != null) {
        	result = new XSBoolean(true); 
         }
         else {
        	XSTypeDefinition typeDefn = seqTypedData.getXsTypeDefinition();
        	if ((typeDefn != null) && (typeDefn instanceof XSSimpleType)) {
        	    XSSimpleTypeDecl simpleTypeDecl = (XSSimpleTypeDecl)typeDefn;        	    
        	    java.lang.String inpValue = XslTransformEvaluationHelper.getStrVal(left);
        	    try {
					simpleTypeDecl.validate(inpValue, null, null);
					result = new XSBoolean(true); 
				} 
        	    catch (InvalidDatatypeValueException ex) {
					result = new XSBoolean(false); 
				}
        	}
         }
      }
      catch (javax.xml.transform.TransformerException ex) {
    	 result = new XSBoolean(false); 
      }
      
      return result;
  }
  
}
