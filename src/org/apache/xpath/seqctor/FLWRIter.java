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
package org.apache.xpath.seqctor;

import java.util.Vector;

import javax.xml.transform.TransformerException;

import org.apache.xml.utils.WrappedRuntimeException;
import org.apache.xpath.Expression;
import org.apache.xpath.VariableStack;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XNodeSequenceSingleton;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XSequence;
import org.apache.xpath.objects.XSequenceCachedBase;
import org.apache.xpath.objects.XSequenceSingleton;

/**
 * The FLWRIter must iterate over a set of variable bindings to 
 * sequences, call an expression for each tuple, and return the 
 * result.
 * 
 * Created Jul 19, 2002
 * @author sboag
 */
public class FLWRIter extends XSequenceCachedBase implements XSequence
{
  private Binding[] m_bindings;
  private XSequence[] m_evaluations;
  private int m_lastNonNull;
  private Expression m_returnExpr;
  private XSequence m_containedIterator;
  private XObject m_contextItem;
  int m_evalPos = 0;

  /**
   * Constructor for FLWRIter.
   */
  public FLWRIter(Binding[] bindings, Expression returnExpr, XPathContext xctxt)
  {
    super(xctxt);
    setShouldCache(true);
    m_xctxt = xctxt;
    m_returnExpr = returnExpr;
    m_bindings = bindings;
    int len = bindings.length;
    m_evaluations = new XSequence[len];
    
    m_lastNonNull = len - 1; // Might not need this
    
    m_contextItem = xctxt.getCurrentItem();
    boolean foundNonEmpty = false;
        
  }

  /**
   * Bind the next tuple of values to the variables.  The tuples are expressed 
   * as sets within the cartesian product of the 'in' clauses (though note 
   * that I'm not sure 'cartesian' is strictly good terminology, since it 
   * applies to only two sets).
   * @return XObject The last evaluation found, or null if 
   * there's no more to evaluate.
   */
  public XObject evalVariables()
  {
    XSequence[] evals = m_evaluations;
    VariableStack vars = m_xctxt.getVarStack();
    XObject xobj = null;

    for (int bindex = m_evalPos; bindex < evals.length; bindex++)
    {
      XSequence xseq = evals[bindex];
      if (null == xseq)
      {
        try
        {
          Binding binding = m_bindings[bindex];
          XObject result = binding.getExpr().execute(m_xctxt);
          xseq = result.xseq();
          m_evaluations[bindex] = xseq;
        }
        catch (TransformerException e)
        {
          throw new WrappedRuntimeException(e);
        }
      }
      xobj = xseq.next();
      if (null != xobj)
      {
        Binding binding = m_bindings[bindex];
        int vindex = binding.getVar().getIndex();
        vars.setLocalVariable(vindex, xobj);
        m_evalPos = bindex;
      }
      else
        if (bindex != 0)
        {
          // m_evalPos = bindex;
          evals[bindex] = null;
          bindex-=2;
          // xseq.reset();
        }
        else
        {
          m_evalPos = evals.length;
          break;
        }
    }
    return xobj;
  }
    
  /**
   * @see org.apache.xpath.objects.XSequence#getNext()
   */
  protected XObject getNext()
  {
    XObject xobj;
    if (null != m_containedIterator)
    {
      xobj = m_containedIterator.next();
      if (null != xobj)
      {
        return xobj;
      }
      else
        m_containedIterator = null;
    }

    m_xctxt.pushCurrentItem(m_contextItem);
    try
    {
      if (null == evalVariables())
        return null;
      xobj = m_returnExpr.execute(m_xctxt);
      if (xobj.isSequenceProper())
      {
        m_containedIterator = (XSequence) xobj;
        return getNext();
      }
      else
        return xobj;
    }
    catch (TransformerException e)
    {
      throw new WrappedRuntimeException(e);
    }
    finally
    {
      m_xctxt.popCurrentItem();
    }
  }

  /**
   * @see org.apache.xpath.objects.XSequence#getNext()
   */
  protected XObject getPrevious()
  {
    assertion(true, "getPrevious() not supported in the FLWRIter!");
    return null;
  }
  
  /**
   * @return XSequence object.
   * @see java.lang.Object#clone()
   */
  public Object clone() throws CloneNotSupportedException
  {
    return super.clone();
  }

}
