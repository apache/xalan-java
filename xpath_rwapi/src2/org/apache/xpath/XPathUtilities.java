/*
 * Created on Jul 14, 2003
 */
/*
 * The Apache Software License, Version 1.1
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
 * originally based on software copyright (c) 2002, International
 * Business Machines Corporation., http://www.ibm.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.xpath;

import org.apache.xpath.expression.Expr;
import org.apache.xpath.expression.NodeTest;
import org.apache.xpath.expression.OperatorExpr;
import org.apache.xpath.expression.PathExpr;
import org.apache.xpath.expression.StepExpr;

/**
 * A collection of utility methods for XPath.
 * @author <a href="mailto:villard@us.ibm.com">Lionel Villard</a>
 * @version $Id$
 */
public class XPathUtilities
{

	/**
	 * Return true whether the specified expression matches document node.
	 * The test is performed statically, therefore there is no guarantee
	 * that the expression will indeed match the document node at runtime.
	 * (example: node()).
	 * @param Expr
	 * @return boolean 		
	 */
	static public boolean isMatchDocumentNode(Expr expr)
	{
		// TODO: patterns are not supported by the XPath API yet
		// it means that pattern may be embedded in sequence
		boolean result;
		switch (expr.getExprType())
		{
			case Expr.SEQUENCE_EXPR :
				OperatorExpr op = (OperatorExpr) expr;
				if (op.getOperandCount() == 1)
				{
					result = isMatchDocumentNode(op.getOperand(0));
				} else
				{
					result = false;
				}
				break;
			case Expr.PATH_EXPR :
				PathExpr p = (PathExpr) expr;
				// TODO: revisit after implementation of fn:root(...)=> operandCount == 1
					result =
						(p.isAbsolute() && p.getOperandCount() == 0) // '/'
	|| (p.getOperandCount() == 1 && isMatchDocumentNode(p.getOperand(0)));
				
				break;
			case Expr.STEP :
				StepExpr s = (StepExpr) expr;
				try
				{
					result = s.getNodeTest().isKindTest()
						&& s.getNodeTest().getKindTest() == NodeTest.ANY_KIND_TEST;
				} catch (XPathException e)
				{
					// impossible
					result = false;
				}
				break;
			case Expr.COMBINE_EXPR : // Union, intersect, except
				OperatorExpr c = (OperatorExpr) expr;
				// Only union is valid in pattern 

				if (c.getOperatorType() == OperatorExpr.UNION_COMBINE)
				{
					result = false;
					for (int i = c.getOperandCount() - 1; i >= 0; i--)
					{
						if (isMatchDocumentNode(c.getOperand(i)))
						{
							result = true;
							break;
						}
					}
				} else
				{
					result = false;
				}
			default :
				result = false;
		}
		return result;
	}

}
