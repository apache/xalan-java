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
package org.apache.xpath;

import org.apache.xml.utils.QName;

/**
 * This is a rather low lever interface that is passed to the 
 * fixupVariables method on expressions.
 * 
 * Created Jul 19, 2002
 * @author sboag
 */
public interface VariableComposeState
{
  /**
   * Given a qualified name, return an integer ID that can be 
   * quickly compared.
   *
   * @param qname a qualified name object, must not be null.
   *
   * @return the expanded-name id of the qualified name.
   */
  public int getQNameID(QName qname);

  /**
   * Add the name of a qualified name within the template.  The position in 
   * the vector is its ID.
   * @param qname A qualified name of a param or variable, should be non-null.
   * @return the index where the variable was added.
   */
  int addVariableName(final org.apache.xml.utils.QName qname);

  /**
   * Reset the stack frame size to zero.
   */
  void resetStackFrameSize();

  /**
   * Get the current size of the stack frame.
   * @return int The size of the stack frame.
   */
  int getFrameSize();

  /**
   * Get the current size of the stack frame.  Use this to record the position 
   * in a template element at startElement, so that it can be popped 
   * at endElement.
   */
  int getCurrentStackFrameSize();

  /**
   * Set the current size of the stack frame.
   */
  void setCurrentStackFrameSize(int sz);

  /**
   * Get the size of the globals area.
   * @return int
   */
  int getGlobalsSize();

  /**
   * Push a "mark" on the stack that will delimit a scope.
   */
  void pushStackMark();

  /**
   * Pop the variable scope to the last mark pushed.
   */
  void popStackMark();

  /**
   * Get the a of QNames that correspond to variables.  This list
   * should be searched backwards for the first qualified name that
   * corresponds to the variable reference qname.  The position of the
   * QName in the vector from the start of the vector will be its position
   * in the stack frame (but variables above the globalsTop value will need
   * to be offset to the current stack frame).
   * 
   * @return A reference to the vector of variable names.  The reference 
   * returned is owned by the implementor of this interface, 
   * and so should not really be mutated, or stored anywhere.
   */
  java.util.Vector getVariableNames();

}
