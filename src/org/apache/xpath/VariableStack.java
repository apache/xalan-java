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

import java.util.Stack;
import java.util.Vector;

import org.apache.xalan.utils.QName;
import org.apache.xalan.utils.IntStack;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XRTreeFrag;

import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.xml.sax.SAXException;

/**
 * <meta name="usage" content="internal"/>
 * Defines a class to keep track of a stack for
 * template arguments and variables, since we can't
 * simply bind the variables to templates and walk
 * the preceding children and ancestors.  The stack
 * is delimited by context markers which bound call
 * frames, and which you can't search past for a variable,
 * and by element frames, which are Arg objects with
 * the given ElemTemplateElement instead of a qname. You
 * can search past element frames, and they accumulate
 * until they are popped.
 *
 * Note: Someone recently made the suggestion that the
 * globals should not be kept at the bottom of the stack,
 * but should be implemented in a hash table.
 */
public class VariableStack extends Stack
{

  /**
   * Constructor for a variable stack.
   */
  public VariableStack()
  {
    pushContextMarker();
  }

  /**
   * Hold the position of the start of the current element contexts.
   */
  private IntStack m_contextPositions = new IntStack();

  /** NEEDSDOC Field m_globalStackFrameIndex          */
  private int m_globalStackFrameIndex = -1;

  /**
   * Hold the position of the start of the current element frame.
   */
  private IntStack m_elemFramePos = new IntStack();

  /**
   * Mark the top of the global stack frame.
   */
  public void markGlobalStackFrame()
  {
    m_globalStackFrameIndex = this.size();
  }

  // Push a context marker onto the stack to let us know when
  // to stop searching for a var.

  /**
   * NEEDSDOC Method pushContextMarker 
   *
   */
  public void pushContextMarker()
  {
    m_contextPositions.push(this.size());
  }

  /**
   * Pop the current context from the current context stack.
   */
  public void popCurrentContext()
  {

    int newSize = m_contextPositions.pop();

    setSize(newSize);
  }

  /**
   * Push an argument onto the stack.  Don't forget
   * to call startContext before pushing a series of
   * arguments for a given macro call.
   *
   * NEEDSDOC @param qname
   * NEEDSDOC @param val
   */
  public void pushVariable(QName qname, XObject val)
  {
    push(new Arg(qname, val, false));
  }

  /**
   * Same as getVariable, except don't look in the
   * global space.
   *
   * NEEDSDOC @param qname
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws SAXException
   */
  public XObject getParamVariable(QName qname) throws SAXException
  {

    XObject val = null;
    int nElems = this.size();
    int endContextPos = m_contextPositions.peek();

    for (int i = (nElems - 1); i >= endContextPos; i--)
    {
      Object obj = elementAt(i);

      if (((Arg) obj).equals(qname))
      {
        val = ((Arg) obj).getVal();

        break;
      }
    }

    return val;
  }

  /**
   * Given a name, return a string representing
   * the value.
   *
   * NEEDSDOC @param name
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws SAXException
   */
  public Object getVariable(QName name) throws SAXException
  {

    int nElems = this.size();
    int endContextPos = m_contextPositions.peek();

    for (int i = (nElems - 1); i >= endContextPos; i--)
    {
      Arg arg = (Arg) elementAt(i);

      if (arg.equals(name))
        return arg.getVal();
    }

    // Look in the global space
    for (int i = (m_globalStackFrameIndex - 1); i >= 0; i--)
    {
      Object obj = elementAt(i);

      if (((Arg) obj).equals(name))
        return ((Arg) obj).getVal();
    }

    return null;
  }

  /**
   * Push an argument onto the stack.  Don't forget
   * to call startContext before pushing a series of
   * arguments for a given macro call.
   */
  public void pushElemFrame()
  {
    m_elemFramePos.push(this.size());
  }

  /**
   * Pop the current context from the current context stack.
   */
  public void popElemFrame()
  {

    int newSize = m_elemFramePos.pop();

    setSize(newSize);
  }
}  // end XSLArgStack

