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
 *     the documentation and/or other materials provided with the
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

  private static final Integer contextMarker = new Integer(0);
  private static final Arg m_elemFrameBoundry = new Arg();
  private int m_globalStackFrameIndex = -1;

  /**
   * This is the top of the stack frame from where a search 
   * for a variable or param should take place.  It may not 
   * be the real stack top.
   */
  private int m_currentStackFrameIndex = 0;

  /**
   * Mark the top of the global stack frame.
   */
  public void markGlobalStackFrame()
  {
    m_globalStackFrameIndex = this.size();
    push(contextMarker);
  }
    
  /**
   * Set the top of the stack frame from where a search 
   * for a variable or param should take place.
   */
  public void setCurrentStackFrameIndex(int currentStackFrameIndex)
  {
    m_currentStackFrameIndex = currentStackFrameIndex;
  }
  
  /**
   * Get the top of the stack frame from where a search 
   * for a variable or param should take place.
   */
  public int getCurrentStackFrameIndex()
  {
    return m_currentStackFrameIndex;
  }
  
  // Push a context marker onto the stack to let us know when
  // to stop searching for a var.
  public void pushContextMarker()
  {
    push(contextMarker);
  }
  
  /**
   * Override the push in order to track the 
   * m_currentStackFrameIndex correctly.
   */
  public Object push(Object o)
  {
    if(m_currentStackFrameIndex == this.size())
      m_currentStackFrameIndex+=1;
    return super.push(o);
  }

  /**
   * Override the pop in order to track the 
   * m_currentStackFrameIndex correctly.
   */
  public Object pop()
  {
    if(m_currentStackFrameIndex == this.size())
      m_currentStackFrameIndex-=1;
    return super.pop();
  }

  /**
   * Override the setSize in order to track the 
   * m_currentStackFrameIndex correctly.
   */
  public void setStackSize(int sz)
  {
    boolean b = (m_currentStackFrameIndex == this.size());
    setSize(sz);
    if(b)
      m_currentStackFrameIndex = this.size();
  }

  /**
   * Pop the current context from the current context stack.
   */
  public void popCurrentContext()
  {
    int nElems = size();
    // Sub 1 extra for the context marker.
    for(int i = (nElems - 1); i >= 0; i--)
    {
      if(this.elementAt(i) == contextMarker)
      {
        this.setStackSize(i);
        break;
      }
    }
  }
  
  /**
   * Push an argument onto the stack.  Don't forget
   * to call startContext before pushing a series of
   * arguments for a given macro call.
   */
  public void pushVariable(QName qname, XObject val)
  {
    push(new Arg(qname, val, false));
  }

  /**
   * Same as getVariable, except don't look in the
   * global space.
   */
  public XObject getParamVariable(QName qname)
    throws SAXException
  {
    XObject val = null;
    int nElems = getCurrentStackFrameIndex();

    for(int i = (nElems - 1); i >= 0; i--)
    {
      Object obj = elementAt(i);
      
      if(obj == contextMarker)
      {
        break;
      }
      else if(((Arg)obj).equals(qname))
      {
        val = ((Arg)obj).getVal();
        break;
      }
    }
    return val;
  }


  /**
   * Given a name, return a string representing
   * the value.
   */
  public Object getVariable(QName name)
    throws SAXException
  {
    int nElems = getCurrentStackFrameIndex();
    
    // Look in the current frame
    int nSize = size();
    for(int i = (nSize - 1); i >= nElems; i--)
    {
      Object obj = this.elementAt(i);
      if(obj == m_elemFrameBoundry)
      {
        break;
      }
      else if(((Arg)obj).equals(name))
      {
        return ((Arg)obj).getVal();
      }
    }  
    
    // Sub 1 extra for the context marker.
    for(int i = (nElems - 1); i >= 0; i--)
    {
      Object obj = elementAt(i);
      if(obj == contextMarker)
      {
        break;
      }
      else if(((Arg)obj).equals(name))
      {
        return ((Arg)obj).getVal();
      }
    }

    // Look in the global space
    for(int i = (m_globalStackFrameIndex-1); i >= 1; i--)
    {
      Object obj = elementAt(i);
      if(obj == contextMarker)
        break;
      else if(((Arg)obj).equals(name))
        return ((Arg)obj).getVal();
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
    push(m_elemFrameBoundry);
  }
  
  /**
   * Pop the current context from the current context stack.
   */
  public void popElemFrame()
  {
    int nElems = size();
    // Sub 1 extra for the context marker.
    for(int i = (nElems - 1); i >= 0; i--)
    {
      Object obj = this.elementAt(i);
      if(obj == contextMarker)
      {
        break;
      }
      else if(obj == m_elemFrameBoundry)
      {
        this.setStackSize(i);
        break;
      }
    }
  }


  
} // end XSLArgStack