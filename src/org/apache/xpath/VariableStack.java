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
 * template arguments and variables.  The VariableStack extends 
 * Stack, and each element in the stack is a stack frame, i.e. a 
 * Stack itself.  The zero element is the global stack frame.
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
  
//  /**
//   * Pushes an item onto the top of this stack. This has exactly 
//   * the same effect as:
//   * <blockquote><pre>
//   * addElement(item)</pre></blockquote>
//   *
//   * @param   item   the item to be pushed onto this stack.
//   * @return  the <code>item</code> argument.
//   * @see     java.util.Vector#addElement
//   */
//  public Object push(Object item) 
//  {
//      if(!(item instanceof Stack))
//        throw new RuntimeException("You can only push a Stack on the variable stack!");
//
//      return super.push(item);
//  }
//  
//  public synchronized void addElement(Object obj) {
//    if(!(obj instanceof Stack))
//      throw new RuntimeException("You can only push a Stack on the variable stack!");
//      
//    super.addElement(obj);
//  }
//  
//  public synchronized void insertElementAt(Object obj, int index) {
//  if(!(obj instanceof Stack))
//    throw new RuntimeException("You can only push a Stack on the variable stack!");
//    
//  super.insertElementAt(obj, index);
//  }
//
//  public synchronized void setElementAt(Object obj, int index) {
//  if(!(obj instanceof Stack))
//    throw new RuntimeException("You can only push a Stack on the variable stack!");
//    
//  super.insertElementAt(obj, index);
// }
  
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
    return (-1 == m_searchStart) ? this.size()-1 : m_searchStart;
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
    // m_contextPositions.push(pos);
  }
  
  /**
   * Pop the current context position onto the contextPositions. This operation 
   * usually corresponds to the ending of a template.
   */
  public void popContextPosition()
  {
    // m_contextPositions.pop();
  }
  
  /**
   * Get the current context position.
   * 
   * @return The context marker into the stack to let us know when
   * to stop searching for a var.
   */
  public int getContextPos()
  {
    // return m_contextPositions.peek();
    return this.size()-1;
  }
  
  /**
   * Push the current top of the stack as 
   * a context marker into the variables stack to let us know when
   * to stop searching for a var.  This operation 
   * usually corresponds to the start of a template.
   */
  public void pushContextMarker()
  {
    // m_contextPositions.push(this.size());
    push(m_emptyStackFrame);
  }

  /**
   * Pop the current context from the current context stack.
   */
  public void popCurrentContext()
  {

    // int newSize = m_contextPositions.pop();

    // setSize(newSize);
    pop();
  }
  
  /**
   * Get the current stack frame.
   * 
   * @return non-null reference to current stack frame, which may be 
   * the global stack.
   */
   private Stack getCurrentFrame()
   {
      int stackFrameIndex = (-1 == m_searchStart) ? this.size()-1 : m_searchStart;
      // System.out.println("what: "+this.elementAt(stackFrameIndex));
      return (Stack)elementAt(stackFrameIndex);
   }
   
  /**
   * Allocate variables frame.
   */
   private Stack allocateCurrentFrame()
   {
      int stackFrameIndex = (-1 == m_searchStart) ? this.size()-1 : m_searchStart;
      Stack newFrame = new Stack();
      this.setElementAt(newFrame, stackFrameIndex);
      return newFrame;
   }

  
  /**
   * Push a parameter onto the stack, or replace it 
   * if it already exists.  Don't forget
   * to call startContext before pushing a series of
   * arguments for a given macro call.
   *
   * @param qname The qualified name of the variable.
   * @param val The wrapped value of the variable.
   */
  public void pushOrReplaceParameter(QName qname, XObject xval)
  {
    Stack frame = getCurrentFrame();
    if(frame == m_emptyStackFrame)
    {
      frame = allocateCurrentFrame();
    }
    for (int i = (frame.size() - 1); i >= 0; i--)
    {
      Arg arg = (Arg)frame.elementAt(i);
      if(arg.getQName().equals(qname) && arg.isFromWithParam())
      {
        frame.setElementAt(new Arg(qname, xval, true), i);
        return;
      }
    }
    frame.push(new Arg(qname, xval, true));
  }
  
  /**
   * Re-mark the variables in the current frame as all 
   * being parameters.
   */
  public void remarkParams()
  {
    Stack frame = getCurrentFrame();
    
    for (int i = (frame.size() - 1); i >= 0; i--)
    {
      Arg arg = (Arg)frame.elementAt(i);
      if(null != arg)
        arg.setIsVisible(false);
    }
  }


  /**
   * Push an argument onto the stack.  Don't forget
   * to call startContext before pushing a series of
   * arguments for a given macro call.
   *
   * @param qname The qualified name of the variable.
   * @param val The wrapped value of the variable.
   */
  // Note that this method will push an Arg onto the Frame even
  // if an Arg for this qname already exists, effectively hiding that 
  // previous stack entry.  That entry is never reclaimed.  This could lead to
  // objects in the Frame that will never be used.  Hopefully, this situation
  // will be short lived since the frame is released after the node-set is
  // in the apply-templates is completed.  However, for large node-sets, we
  // will be putting a lot of unusable entries in the Frame.  If this becomes
  // a problem, we should replace those matching entries provided that 
  // isFromWithParam is false.  I'm not sure that the overhead of searching
  // for matching entries is worth it at this point.  GLP
  public void pushVariable(QName qname, XObject val)
  {
    Stack frame = getCurrentFrame();
    if(frame == m_emptyStackFrame)
      frame = allocateCurrentFrame();
    frame.push(new Arg(qname, val, false));
  }
  
  /**
   * Push an argument onto the stack.  Don't forget
   * to call startContext before pushing a series of
   * arguments for a given macro call.
   *
   * @param arg The variable argument.
   */
  public void pushVariableArg(Arg arg)
  {
    Stack frame = getCurrentFrame();
    if(frame == m_emptyStackFrame)
      frame = allocateCurrentFrame();
    frame.push(arg);
  }

  
  /**
   * Returns a variable or parameter that is already declared, 
   * either in the current context or in the global space.
   *
   * @param qname The qualified name of the variable.
   *
   * @return the Arg if the variable is already declared, otherwise <code>null</code>.
   *
   * @throws TransformerException
   */
  public Arg getDeclaredVariable(QName qname) throws TransformerException
  {

    Stack frame = getCurrentFrame();
    
    for (int i = (frame.size() - 1); i >= 0; i--)
    {
      Object obj = frame.elementAt(i);

      if (((Arg) obj).getQName().equals(qname))
      {
        return (Arg) obj;
      }
    }
          
    Stack gframe = (Stack)this.elementAt(0);
    if(gframe == frame)
      return null;
    
    for (int i = (gframe.size() - 1); i >= 0; i--)
    {
      Object obj = gframe.elementAt(i);

      if (((Arg) obj).getQName().equals(qname))
      {
        return (Arg) obj;
      }
    }

    return null;
  }


  /**
   * Get the variable argument.
   *
   * @param qname The qualified name of the variable.
   *
   * @return the argument object.
   *
   * @throws TransformerException
   */
  public Arg getParamArg(QName qname) throws TransformerException
  {

    XObject val = null;
    Stack frame = getCurrentFrame();
    
    for (int i = (frame.size() - 1); i >= 0; i--)
    {
      Arg arg = (Arg)frame.elementAt(i);

      if (arg.getQName().equals(qname) && arg.isFromWithParam())
      {
        return arg;
      }
    }

    return null;
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
  public XObject getVariable(XPathContext xctxt, QName name) throws TransformerException
  {

    Stack frame = getCurrentFrame();
    Stack gframe = (Stack)this.elementAt(0);
    
    if(frame != gframe)
    {
      for (int i = (frame.size() - 1); i >= 0; i--)
      {
        Arg arg = (Arg)frame.elementAt(i);
  
        if (arg.getQName().equals(name) && arg.isVisible())
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
    }
     
    for (int i = (gframe.size() - 1); i >= 0; i--)
    {
      Arg arg = (Arg)gframe.elementAt(i);

      if (arg.getQName().equals(name) && arg.isVisible())
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
    Stack frame = getCurrentFrame();

    m_elemFramePos.push(frame.size());
  }

  /**
   * Pop the current context from the current context stack.
   */
  public void popElemFrame()
  {

    Stack frame = getCurrentFrame();
    int newSize = m_elemFramePos.pop();

    frame.setSize(newSize);
  }
  
  
  /**
   * Hold the position of the start of the current element frame.
   * @serial
   */
  private IntStack m_elemFramePos = new IntStack();
  
  /**
   * Hold the position of the start of the current element contexts.
   */
  private static final Stack m_emptyStackFrame = new Stack();

  /** The top of the globals space.
   *  @serial     */
  private int m_globalStackFrameIndex = -1;
  
  /** Where to start the current search for a variable.
   * If this is -1, the search should start at the top 
   * of the stack.
   * @serial */
  private int m_searchStart = -1;

}  // end XSLArgStack

