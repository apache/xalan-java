/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights 
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
import org.apache.xml.utils.DateTimeObj;
import org.apache.xml.utils.Duration;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XDate;
import org.apache.xpath.objects.XDateTime;
import org.apache.xpath.objects.XDouble;
import org.apache.xpath.objects.XDuration;
import org.apache.xpath.objects.XDTDuration;
import org.apache.xpath.objects.XYMDuration;
import org.apache.xpath.objects.XFloat;
import org.apache.xpath.objects.XInteger;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XTime;

/**
 * The binary '-' operation expression executer.
 */
public class Subtract extends OperationNormalized
{
  static OpFuncLookupTable m_funcs;
  {
    m_funcs = new OpFuncLookupTable();
    m_funcs.setFunc(XType.DOUBLE, new GenericOpFunc()
    {
      public XObject operate(XPathContext xctxt, XObject lhs, XObject rhs)
        throws TransformerException
      {
        return new XDouble(lhs.num() - rhs.num());
      }
    });
    m_funcs.setFunc(XType.FLOAT, new GenericOpFunc()
    {
      public XObject operate(XPathContext xctxt, XObject lhs, XObject rhs)
        throws TransformerException
      {
        return new XFloat(lhs.floatVal() - rhs.floatVal());
      }
    });
    m_funcs.setFunc(XType.INTEGER, new GenericOpFunc()
    {
      public XObject operate(XPathContext xctxt, XObject lhs, XObject rhs)
        throws TransformerException
      {
        return new XInteger(lhs.integer() - rhs.integer());
      }
    });
    m_funcs.setFunc(XType.DECIMAL, NOTSUPPORTED);

    m_funcs.setFunc(XType.DURATION, new GenericOpFunc()
    {
      public XObject operate(XPathContext xctxt, XObject lhs, XObject rhs)
        throws TransformerException
      {
        Duration duration1 = lhs.duration();
        // The normalizer should make sure the second argument is always 
        // a duration!
        Duration duration2 = rhs.duration();
        Duration du = duration1.subtractDTDuration(duration2);
        return new XDuration(du);
      }
    });
    m_funcs.setFunc(XType.DAYTIMEDURATION, new GenericOpFunc()
    {
      public XObject operate(XPathContext xctxt, XObject lhs, XObject rhs)
        throws TransformerException
      {
        // for now...
        int rhsVT = rhs.getValueType();
        if(XType.DURATION == rhsVT 
          || XType.DAYTIMEDURATION == rhsVT)
          {
        Duration duration1 = lhs.duration();
        // The normalizer should make sure the second argument is always 
        // a duration!
        Duration duration2 = rhs.duration();
        Duration du = duration1.subtractDTDuration(duration2);
        return new XDTDuration(du);
      }
        else
        {
        	throw new TransformerException("Type Exception!");
        }
      }
    });
    m_funcs.setFunc(XType.YEARMONTHDURATION, new GenericOpFunc()
    {
      public XObject operate(XPathContext xctxt, XObject lhs, XObject rhs)
        throws TransformerException
      {
        // for now...
        int rhsVT = rhs.getValueType();
        if(XType.DURATION == rhsVT 
          || XType.YEARMONTHDURATION == rhsVT)
          {
        Duration duration1 = lhs.duration();
        // The normalizer should make sure the second argument is always 
        // a duration!
        Duration duration2 = rhs.duration();
        Duration du = duration1.subtractYMDuration(duration2);
        return new XYMDuration(du);
      }
        else
        {
         throw new TransformerException("Type Exception!");
        }
      }
    });
    
    m_funcs.setFunc(XType.DATETIME, new GenericOpFunc()
    {
      public XObject operate(XPathContext xctxt, XObject lhs, XObject rhs)
        throws TransformerException
      {
        DateTimeObj dt = lhs.datetime();
        // The normalizer should
        int rhsVT = rhs.getValueType();
        if(XType.DURATION == rhsVT 
          || XType.DAYTIMEDURATION == rhsVT)
        {
          Duration duration2 = rhs.duration();
          DateTimeObj du = dt.subtractDTDurationFromDateTime(duration2);
         return new XDateTime(du);
        }
        else //if(XType.YEARMONTHDURATION == rhsVT)
        {
          Duration duration2 = rhs.duration();
          DateTimeObj du = dt.subtractYMDurationFromDateTime(duration2);
         return new XDateTime(du);
        }
      }
    });
    m_funcs.setFunc(XType.TIME, new GenericOpFunc()
    {
      public XObject operate(XPathContext xctxt, XObject lhs, XObject rhs)
        throws TransformerException
      {
        DateTimeObj oldTime = lhs.time();
        // The normalizer should
        Duration duration2 = rhs.duration();
        
        DateTimeObj newTime = oldTime.subtractDTDurationFromTime(duration2);
        return new XTime(newTime);
      }
    });
    m_funcs.setFunc(XType.DATE, new GenericOpFunc()
    {
      public XObject operate(XPathContext xctxt, XObject lhs, XObject rhs)
        throws TransformerException
      {
        DateTimeObj dt = lhs.date();
        // The normalizer should
        int rhsVT = rhs.getValueType();
        if(XType.DURATION == rhsVT 
          || XType.DAYTIMEDURATION == rhsVT)
        {
          Duration duration2 = rhs.duration();
          DateTimeObj newDate = dt.subtractDTDurationFromDate(duration2);
          return new XDate(newDate);
        }
       else if(XType.YEARMONTHDURATION == rhsVT)
        {
          Duration duration2 = rhs.duration();
          DateTimeObj newDate = dt.subtractYMDurationFromDate(duration2);
          return new XDate(newDate);
        }
        else
        {
          DateTimeObj dt2 = rhs.date();
          Duration duration = dt.subtractDateFromDate(dt2);
          return new XDuration(duration);
        }
      }
    });
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
