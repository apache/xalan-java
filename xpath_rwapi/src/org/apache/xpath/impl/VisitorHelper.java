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
package org.apache.xpath.impl;

import org.apache.xpath.datamodel.SingleType;
import org.apache.xpath.expression.KindTest;
import org.apache.xpath.expression.NameTest;
import org.apache.xpath.expression.TreatExpr;
import org.apache.xpath.expression.ConditionalExpr;
import org.apache.xpath.expression.Expr;
import org.apache.xpath.expression.ForAndQuantifiedExpr;
import org.apache.xpath.expression.InstanceOfExpr;
import org.apache.xpath.expression.Literal;
import org.apache.xpath.expression.OperatorExpr;
import org.apache.xpath.expression.PathExpr;
import org.apache.xpath.expression.StepExpr;
import org.apache.xpath.expression.Variable;
import org.apache.xpath.expression.Visitor;

/**
 * Helper for implementing expression visitor.
 * @author <a href="mailto:villard@us.ibm.com">Lionel Villard</a>
 * @version $Id$
 */
public class VisitorHelper implements Visitor
{

	public boolean visitCastableAs(TreatExpr expr)
	{
		return true;
	}

	public boolean visitConditional(ConditionalExpr condition)
	{
		return true;
	}

	public boolean visitForOrQuantifiedExpr(ForAndQuantifiedExpr expr)
	{
		return true;
	}

	public boolean visitInstanceOf(InstanceOfExpr expr)
	{
		return true;
	}

	public boolean visitLiteral(Literal literal)
	{
		return true;
	}

	public boolean visitOperator(OperatorExpr operator)
	{
		return true;
	}

	public boolean visitPath(PathExpr path)
	{
		return true;
	}

	public boolean visitStep(StepExpr step)
	{
		return true;
	}

	public boolean visitVariable(Variable var)
	{
		return true;
	}

	public boolean visitContextItem(Expr expr)
	{
		return true;
	}

	public boolean visitKindTest(KindTest expr)
	{
		return true;
	}

	public boolean visitNameTest(NameTest expr)
	{
		return true;
	}

	public boolean visitSingleType(SingleType impl)
	{
		return true;
	}

	public boolean visitTreatAs(TreatExpr impl)
	{
		return true;
	}

}
