/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights 
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:  
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Xalan" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 1999, Lotus
 * Development Corporation., http://www.lotus.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.xpath.operations;

import javax.xml.transform.TransformerException;

import org.apache.xml.dtm.XType;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XInteger;
import org.apache.xpath.objects.XObject;

/**
 * The 'div' operation expression executer.
 */
public class Idiv extends OperationNormalized
{
  static GenericOpFunc IDivObj = new GenericOpFunc()
  {
    /**
     * @see org.apache.xpath.operations.opfuncs.GenericOpFunc#operate(XPathContext, XObject, XObject)
     */
    public XObject operate(XPathContext xctxt, XObject lhs, XObject rhs)
      throws TransformerException
    {
      return new XInteger(lhs.integer() / rhs.integer());
    }

  };

  static OpFuncLookupTable m_funcs;
  {
    m_funcs = new OpFuncLookupTable();
    m_funcs.setFunc(XType.DOUBLE, IDivObj);
    m_funcs.setFunc(XType.FLOAT, IDivObj);
    m_funcs.setFunc(XType.INTEGER, IDivObj);
    m_funcs.setFunc(XType.DECIMAL, IDivObj);

    m_funcs.setFunc(XType.DURATION, NOTSUPPORTED);
    m_funcs.setFunc(XType.DAYTIMEDURATION, NOTSUPPORTED);
    m_funcs.setFunc(XType.YEARMONTHDURATION, NOTSUPPORTED);
    
    m_funcs.setFunc(XType.DATETIME, NOTSUPPORTED);
    m_funcs.setFunc(XType.TIME, NOTSUPPORTED);
    m_funcs.setFunc(XType.DATE, NOTSUPPORTED);
    m_funcs.setFunc(XType.GYEARMONTH, NOTSUPPORTED);
    m_funcs.setFunc(XType.GYEAR, NOTSUPPORTED);
    m_funcs.setFunc(XType.GMONTHDAY, NOTSUPPORTED);
    m_funcs.setFunc(XType.GDAY, NOTSUPPORTED);
    m_funcs.setFunc(XType.GMONTH, NOTSUPPORTED);

    m_funcs.setFunc(XType.BOOLEAN, NOTSUPPORTED);
    m_funcs.setFunc(XType.STRING, NOTSUPPORTED);
    m_funcs.setFunc(XType.BASE64BINARY, NOTSUPPORTED);
    m_funcs.setFunc(XType.HEXBINARY, NOTSUPPORTED);
    m_funcs.setFunc(XType.ANYURI, NOTSUPPORTED);
    m_funcs.setFunc(XType.QNAME, NOTSUPPORTED);
    m_funcs.setFunc(XType.NOTATION, NOTSUPPORTED);
  }
  
  /**
   * @see org.apache.xpath.operations.Operation#getLookupTable()
   */
  public OpFuncLookupTable getLookupTable()
  {
    return m_funcs;
  }

}
