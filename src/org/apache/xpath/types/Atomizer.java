/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2002-2003 The Apache Software Foundation.  All rights 
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
package org.apache.xpath.types;

import org.apache.xml.dtm.XType;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XSequence;

/**
 * Singleton class that handles automization.
 * 
 * Created Jul 30, 2002
 * @author sboag
 */
public class Atomizer
{
  public static XObject makeAtom(XObject xobj)
  {
    if(XSequence.EMPTY == xobj)
      return xobj;
    else if(xobj.isSingletonOrEmpty())
    {
      return xobj;
    }
    else
    {
      XSequence xseq = xobj.xseq();
      XObject first = xseq.next();
      if(null == first)
        return XSequence.EMPTY;
      XObject second = xseq.next();
      if(second != null)
        return xobj;
      // For now, could be node or atom.  Don't do any 
      // type checks here.
      return first;
    }
  }


  public static XObject makeFallbackAtom(XObject xobj, int requiredType)
  {
    switch (requiredType)
    {
      case XType.ANYTYPE :
      case XType.BOOLEAN :
      case XType.DOUBLE :
      case XType.STRING :
      case XType.NODE :
      case XType.RTREEFRAG :

        // Callers should expect the actual values of these constants to 
        // changed... i.e. they should not rely on the value itself.
      case XType.BASE64BINARY :
      case XType.HEXBINARY :
      case XType.FLOAT :
      case XType.DECIMAL :
      case XType.INTEGER :
      case XType.ANYURI :
      case XType.QNAME :
      case XType.NOTATION :
      case XType.DURATION :
      case XType.DATETIME :
      case XType.TIME :
      case XType.DATE :
      case XType.GYEARMONTH :
      case XType.GYEAR :
      case XType.GMONTHDAY :
      case XType.GDAY :
      case XType.GMONTH :

      case XType.YEARMONTHDURATION :
      case XType.DAYTIMEDURATION :

      case XType.ANYSIMPLETYPE :
      case XType.EMPTYSEQ :

        // Answer for a sequence when it hasn't iterated. -sb
      case XType.SEQ :
        break;
      default :
        }
    return null;
  }
}
