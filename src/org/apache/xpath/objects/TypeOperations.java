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
package org.apache.xpath.objects;

/**
 * The responsibility of TypeOperations is to .
 * 
 * Created Jul 18, 2002
 * @author sboag
 */
/**
 * The responsibility of enclosing_type is to .
 * 
 * Created Jul 18, 2002
 * @author sboag
 */
public interface TypeOperations
{
  /**
   * Method add.
   * @param left
   * @param right
   * @return XObject
   */
  public XObject add(XObject left, XObject right);
  
  /**
   * Method and.
   * @param left
   * @param right
   * @return XObject
   */
  public XObject and(XObject left, XObject right);
  
  /**
   * Method bool.
   * @param left
   * @param right
   * @return XObject
   */
  public XObject bool(XObject left, XObject right);
  
  /**
   * Method div.
   * @param left
   * @param right
   * @return XObject
   */
  public XObject div(XObject left, XObject right);
  
  /**
   * Method equals.
   * @param left
   * @param right
   * @return XObject
   */
  public XObject equals(XObject left, XObject right);
  
  /**
   * Method follows.
   * @param left
   * @param right
   * @return XObject
   */
  public XObject follows(XObject left, XObject right);
  
  /**
   * Method fortraneq.
   * @param left
   * @param right
   * @return XObject
   */
  public XObject fortraneq(XObject left, XObject right);
  
  /**
   * Method fortrange.
   * @param left
   * @param right
   * @return XObject
   */
  public XObject fortrange(XObject left, XObject right);
  
  /**
   * Method fortrangt.
   * @param left
   * @param right
   * @return XObject
   */
  public XObject fortrangt(XObject left, XObject right);
  
  /**
   * Method fortranle.
   * @param left
   * @param right
   * @return XObject
   */
  public XObject fortranle(XObject left, XObject right);
  
  /**
   * Method fortranlt.
   * @param left
   * @param right
   * @return XObject
   */
  public XObject fortranlt(XObject left, XObject right);
  /**
   * Method fortranne.
   * @param left
   * @param right
   * @return XObject
   */
  public XObject fortranne(XObject left, XObject right);
  
  /**
   * Method gt.
   * @param left
   * @param right
   * @return XObject
   */
  public XObject gt(XObject left, XObject right);
  
  /**
   * Method gte.
   * @param left
   * @param right
   * @return XObject
   */
  public XObject gte(XObject left, XObject right);
  
  /**
   * Method gtgt.
   * @param left
   * @param right
   * @return XObject
   */
  public XObject gtgt(XObject left, XObject right);
  
  /**
   * Method idiv.
   * @param left
   * @param right
   * @return XObject
   */
  public XObject idiv(XObject left, XObject right);
  
  /**
   * Method is.
   * @param left
   * @param right
   * @return XObject
   */
  public XObject is(XObject left, XObject right);
  
  /**
   * Method isnot.
   * @param left
   * @param right
   * @return XObject
   */
  public XObject isnot(XObject left, XObject right);
  
  /**
   * Method lt.
   * @param left
   * @param right
   * @return XObject
   */
  public XObject lt(XObject left, XObject right);
  
  /**
   * Method lte.
   * @param left
   * @param right
   * @return XObject
   */
  public XObject lte(XObject left, XObject right);
  
  /**
   * Method ltlt.
   * @param left
   * @param right
   * @return XObject
   */
  public XObject ltlt(XObject left, XObject right);
  
  /**
   * Method mod.
   * @param left
   * @param right
   * @return XObject
   */
  public XObject mod(XObject left, XObject right);
  
  /**
   * Method mult.
   * @param left
   * @param right
   * @return XObject
   */
  public XObject mult(XObject left, XObject right);
  
  /**
   * Method neg.
   * @param left
   * @param right
   * @return XObject
   */
  public XObject neg(XObject left, XObject right);
  
  /**
   * Method notequals.
   * @param left
   * @param right
   * @return XObject
   */
  public XObject notequals(XObject left, XObject right);
  
  /**
   * Method or.
   * @param left
   * @param right
   * @return XObject
   */
  public XObject or(XObject left, XObject right);
  
  /**
   * Method pos.
   * @param left
   * @param right
   * @return XObject
   */
  public XObject pos(XObject left, XObject right);
  
  /**
   * Method precedes.
   * @param left
   * @param right
   * @return XObject
   */
  public XObject precedes(XObject left, XObject right);
  
  /**
   * Method quo.
   * @param left
   * @param right
   * @return XObject
   */
  public XObject quo(XObject left, XObject right);
  
  /**
   * Method subtract.
   * @param left
   * @param right
   * @return XObject
   */
  public XObject subtract(XObject left, XObject right);
}
