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

import org.apache.xml.utils.QName;
import org.apache.xml.utils.IntStack;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XRTreeFrag;

import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.transform.TransformerException;

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

  /** The top of the globals space.     */
  private int m_globalStackFrameIndex = -1;
  
  /** Where to start the current search for a variable.
   * If this is -1, the search should start at the top 
   * of the stack. */
  private int m_searchStart = -1;
  
  /**
   * Set where to start the current search for a variable.
   * If this is -1, the search should start at the top 
   * of the stack.
   * 
   * @param startPos The position to start the search, or -1
   * if the search should start from the top.
   */
  public void setSearchStart(int startPos)
  {
    m_searchStart = startPos;
  }
  
  /**
   * Get the position from where the search should start, 
   * which is either the searchStart property, or the top
   * of the stack if that value is -1.
   * 
   * @return The position from where the search should start, which
   * is always greater than or equal to zero.
   */
  public int getSearchStartOrTop()
  {
    return (-1 == m_searchStart) ? this.size() : m_searchStart;
  }
  
  /**
   * Get the position to start the search, or -1
   * if the search should start from the top.
   * 
   * @return The position to start the search, or -1
   * if the search should start from the top.
   */
  public int getSearchStart()
  {
    return m_searchStart;
  }

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

  /**
   * Push a context marker onto the contextPositions stack to let us know when
   * to stop searching for a var.  This operation 
   * usually corresponds to the start of a template.
   */
  public void pushContextPosition(int pos)
  {
    m_contextPositions.push(pos);
  }
  
  /**
   * Pop the current context position onto the contextPositions. This operation 
   * usually corresponds to the ending of a template.
   */
  public void popContextPosition()
  {
    m_contextPositions.pop();
  }
  
  /**
   * Get the current context position.
   * 
   * @return The context marker into the stack to let us know when
   * to stop searching for a var.
   */
  public int getContextPos()
  {
    return m_contextPositions.peek();
  }
  
  /**
   * Push the current top of the stack as 
   * a context marker into the variables stack to let us know when
   * to stop searching for a var.  This operation 
   * usually corresponds to the start of a template.
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
   * Push an argument onto the stack, or replace it 
   * if it already exists.  Don't forget
   * to call startContext before pushing a series of
   * arguments for a given macro call.
   *
   * @param qname The qualified name of the variable.
   * @param val The wrapped value of the variable.
   */
  public void pushOrReplaceVariable(QName qname, XObject xval)
  {
    int n = this.size();
    for(int i = n-1; i >= 0; i--)
    {
      Arg arg = (Arg)this.elementAt(i);
      if(arg.getQName().equals(qname))
      {
        this.setElementAt(new Arg(qname, xval), i);
        return;
      }
    }
    push(new Arg(qname, xval, false));
  }


  /**
   * Push an argument onto the stack.  Don't forget
   * to call startContext before pushing a series of
   * arguments for a given macro call.
   *
   * @param qname The qualified name of the variable.
   * @param val The wrapped value of the variable.
   */
  public void pushVariable(QName qname, XObject val)
  {
    push(new Arg(qname, val, false));
  }
  
  /**
   * Tell if a variable or parameter is already declared, 
   * either in the current context or in the global space.
   *
   * @param qname The qualified name of the variable.
   *
   * @return true if the variable is already declared.
   *
   * @throws TransformerException
   */
  public boolean variableIsDeclared(QName qname) throws TransformerException
  {

    int nElems = (-1 == m_searchStart) ? this.size() : m_searchStart;
    int endContextPos = m_contextPositions.peek();

    for (int i = (nElems - 1); i >= endContextPos; i--)
    {
      Object obj = elementAt(i);

      if (((Arg) obj).getQName().equals(qname))
      {
        return true;
      }
    }
    
    if(endContextPos < m_globalStackFrameIndex)
      return false;
    
    // Look in the global space
    for (int i = (m_globalStackFrameIndex - 1); i >= 0; i--)
    {
      Object obj = elementAt(i);

      if (((Arg) obj).getQName().equals(qname))
      {
        return true;
      }
    }

    return false;
  }


  /**
   * Same as getVariable, except don't look in the
   * global space.
   *
   * @param qname The qualified name of the variable.
   *
   * @return The wrapped value of the variable.
   *
   * @throws TransformerException
   */
  public XObject getParamVariable(XPathContext xctxt, QName qname) throws TransformerException
  {

    XObject val = null;
    int nElems = (-1 == m_searchStart) ? this.size() : m_searchStart;
    int endContextPos = m_contextPositions.peek();

    for (int i = (nElems - 1); i >= endContextPos; i--)
    {
      Arg arg = (Arg)elementAt(i);

      if (arg.getQName().equals(qname))
      {
        val = arg.getVal();
        
        if(val.getType() == XObject.CLASS_UNRESOLVEDVARIABLE)
        {
          val = val.execute(xctxt);
          arg.setVal(val);
        }

        break;
      }
    }

    return val;
  }

  /**
   * Given a name, return an object representing
   * the value.
   *
   * @param qname The qualified name of the variable.
   *
   * @return The wrapped value of the variable.
   *
   * @throws TransformerException
   */
  public Object getVariable(XPathContext xctxt, QName name) throws TransformerException
  {

    int nElems = (-1 == m_searchStart) ? this.size() : m_searchStart;
    int endContextPos = m_contextPositions.peek();

    for (int i = (nElems - 1); i >= endContextPos; i--)
    {
      Arg arg = (Arg) elementAt(i);

      if (arg.getQName().equals(name))
      {
        XObject val = arg.getVal();
        if(val.getType() == XObject.CLASS_UNRESOLVEDVARIABLE)
        {
          val = val.execute(xctxt);
          arg.setVal(val);
        }
        return val;
      }
    }
    
    if(endContextPos < m_globalStackFrameIndex)
      return null;

    // Look in the global space
    for (int i = (m_globalStackFrameIndex - 1); i >= 0; i--)
    {
      Arg arg = (Arg)elementAt(i);

      if (arg.getQName().equals(name))
      {
        XObject val = arg.getVal();
        if(val.getType() == XObject.CLASS_UNRESOLVEDVARIABLE)
        {
          val = val.execute(xctxt);
          arg.setVal(val);
        }
        return val;
      }
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

