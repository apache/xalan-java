/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights 
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
package org.apache.xpath.axes;

import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.functions.FuncLast;
import org.apache.xpath.functions.FuncPosition;
import org.apache.xpath.functions.Function;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.operations.Div;
import org.apache.xpath.operations.Minus;
import org.apache.xpath.operations.Mod;
import org.apache.xpath.operations.Mult;
import org.apache.xpath.operations.Plus;
import org.apache.xpath.operations.Quo;
import org.apache.xpath.operations.Variable;

public class HasPositionalPredChecker extends XPathVisitor
{
	private boolean m_hasPositionalPred = false;
	private int m_predDepth = 0;
	
	/**
	 * Process the LocPathIterator to see if it contains variables 
	 * or functions that may make it context dependent.
	 * @param path LocPathIterator that is assumed to be absolute, but needs checking.
	 * @return true if the path is confirmed to be absolute, false if it 
	 * may contain context dependencies.
	 */
	public static boolean check(LocPathIterator path)
	{
		HasPositionalPredChecker hppc = new HasPositionalPredChecker();
		path.callVisitors(null, hppc);
		return hppc.m_hasPositionalPred;
	}
	
	/**
	 * Visit a function.
	 * @param owner The owner of the expression, to which the expression can 
	 *              be reset if rewriting takes place.
	 * @param func The function reference object.
	 * @return true if the sub expressions should be traversed.
	 */
	public boolean visitFunction(ExpressionOwner owner, Function func)
	{
		if((func instanceof FuncPosition) ||
		   (func instanceof FuncLast))
			m_hasPositionalPred = true;
		return true;
	}
	
//	/**
//	 * Visit a variable reference.
//	 * @param owner The owner of the expression, to which the expression can 
//	 *              be reset if rewriting takes place.
//	 * @param var The variable reference object.
//	 * @return true if the sub expressions should be traversed.
//	 */
//	public boolean visitVariableRef(ExpressionOwner owner, Variable var)
//	{
//		m_hasPositionalPred = true;
//		return true;
//	}
	
  /**
   * Visit a predicate within a location path.  Note that there isn't a 
   * proper unique component for predicates, and that the expression will 
   * be called also for whatever type Expression is.
   * 
   * @param owner The owner of the expression, to which the expression can 
   *              be reset if rewriting takes place.
   * @param pred The predicate object.
   * @return true if the sub expressions should be traversed.
   */
  public boolean visitPredicate(ExpressionOwner owner, Expression pred)
  {
    m_predDepth++;

    if(m_predDepth == 1)
    {
      if((pred instanceof Variable) || 
         (pred instanceof XNumber) ||
         (pred instanceof Div) ||
         (pred instanceof Plus) ||
         (pred instanceof Minus) ||
         (pred instanceof Mod) ||
         (pred instanceof Quo) ||
         (pred instanceof Mult) ||
         (pred instanceof org.apache.xpath.operations.Number) ||
         (pred instanceof Function))
          m_hasPositionalPred = true;
      else
      	pred.callVisitors(owner, this);
    }

    m_predDepth--;

    // Don't go have the caller go any further down the subtree.
    return false;
  }


}

