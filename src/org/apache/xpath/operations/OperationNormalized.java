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
import org.apache.xml.utils.XMLString;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XSequence;
import org.apache.xpath.objects.XString;

/**
 * 
 * 
 * Created Jul 31, 2002
 * @author sboag
 */
public abstract class OperationNormalized extends Operation
{
  public abstract OpFuncLookupTable getLookupTable();
  
  
  /**
   * Get the default value type that this operation expects.
   * @return int one of XType.XXX being one of the simple types.
   */
  public int getDefaultType()
  {
    return XType.DOUBLE;
  }
  
  /**
   * Execute a binary operation by calling execute on each of the operands,
   * and then calling the operate method on the derived class.
   *
   *
   * @param xctxt The runtime execution context.
   *
   * @return The XObject result of the operation.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt)
          throws javax.xml.transform.TransformerException
  {

    XObject left = m_left.execute(xctxt, true);
    XObject right = m_right.execute(xctxt, true);

    if(XSequence.EMPTY == left || XSequence.EMPTY == right)
      return XSequence.EMPTY;
    
//    if(!left.isSingletonOrEmpty())
//    {
//      XSequence xseq = left.xseq();
//      left = xseq.getCurrent();
//      if(null == left)
//        return XSequence.EMPTY;
//      // Node?
//    }
//    if(!right.isSingletonOrEmpty())
//    {
//      XSequence xseq = right.xseq();
//      right = xseq.getCurrent();
//      if(null == right)
//        return XSequence.EMPTY;
//      // Node?
//    }
    
    int type = left.getValueType();
    int defaultType = getDefaultType();
    if(type == XType.ANYTYPE || type == XType.ANYSIMPLETYPE)
    {
      type = getDefaultType();
    }
    else if(type != defaultType)
    {
      type = findFallbackType(type, defaultType);
     /* if(XType.DURATION == type)
      {
        XObject savedLeft = left;
        left = right;
        right = savedLeft;
        type = left.getValueType();
        type = findFallbackType(type, defaultType);
      }*/
    }
    
    GenericOpFunc opfunc = getLookupTable().getFunc(type);
    if(null == opfunc)
      throw new TransformerException("Type Exception!", this);
    return opfunc.operate(xctxt, left, right);
  }
  
  /**
   * Find the fallback type for this object.  
   * Assumptions: 1) the valType and the requiredType do not match, 
   * 2) the valType has already been checked for ANYTYPE (this is 
   * not officially part of fallback), and 3) the valType is one of 
   * the 19 simple types.
   * @param valType The value type.
   * @param requiredType  The required type.
   * @return int Either the valType, or the required type.
   */
  public int findFallbackType(int valType, int requiredType)
  {
    switch (requiredType)
    {
      case XType.DOUBLE :
      case XType.INTEGER :
      case XType.FLOAT :
        switch (valType)
        {
          case XType.ANYTYPE :
          case XType.ANYSIMPLETYPE :
            return requiredType;

          case XType.STRING :
          case XType.BOOLEAN :
            return requiredType;
            
          case XType.DATE :
          case XType.DATETIME :
          case XType.TIME :
          case XType.GYEARMONTH :
          case XType.GYEAR :
          case XType.GMONTHDAY :
          case XType.GDAY :
          case XType.GMONTH :
            return valType;

          case XType.DURATION :
          case XType.YEARMONTHDURATION :
          case XType.DAYTIMEDURATION :
            // Normalize this for duration so that a quick 
            // switch can be made in execute(XPathContext xctxt) 
            // to make the duration the second argument.
            return valType;//XType.DURATION;
            }
        break;

      case XType.BOOLEAN :
      case XType.STRING :
      case XType.DECIMAL :
        return requiredType;
    }
    return valType;
  }
  

}
