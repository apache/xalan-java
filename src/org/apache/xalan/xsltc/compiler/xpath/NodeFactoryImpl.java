/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
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
 *
 * @author Santiago Pericas-Geertsen
 */

package org.apache.xalan.xsltc.compiler.xpath;

import org.apache.xpath.rwapi.impl.parser.Axis;
import org.apache.xpath.rwapi.impl.parser.Node;
import org.apache.xpath.rwapi.impl.parser.XPathTreeConstants;

public class NodeFactoryImpl
    implements org.apache.xpath.rwapi.impl.parser.NodeFactory
{
    /**
     * Creates NameTest AST node
     *
     * @return a new AST node or null.
     */
    public org.apache.xpath.rwapi.impl.NameTestImpl createNameTestNode(int id) {
        return new NameTestImpl(id);
    }

    /**
     * Creates KindTest AST node for the given tree node ID.
     *
     * @param id KindTest id of the node to create
     *
     * @return a new AST node or null.
     */
    public org.apache.xpath.rwapi.impl.KindTestImpl createKindTestNode(int id) {
        return new KindTestImpl(id);
    }

    /**
     * Creates Step AST node
     *
     * @return a new AST node or null.
     */
    public org.apache.xpath.rwapi.impl.StepExprImpl createStepNode(int id) {
        return new StepExprImpl(id);
    }

    /**
     * Creates Operator AST node for the given tree node ID.
     *
     * @param id Operator id of the node to create
     *
     * @return a new AST node or null.
     */
    public org.apache.xpath.rwapi.impl.OperatorImpl createOperatorNode(int id) {
        return new OperatorImpl(id);
    }

    /**
     * Creates Literal AST node for the given tree node ID.
     *
     * @param id Literal id of the node to create
     *
     * @return a new AST node or null.
     */
    public org.apache.xpath.rwapi.impl.LiteralImpl createLiteralNode(int id) {
        return new LiteralImpl(id);
    }

    /**
     * Creates Path AST node
     *
     * @return a new Path AST node or null.
     */
    public org.apache.xpath.rwapi.impl.PathExprImpl createPathNode(int id) {
        return new PathExprImpl(id);
    }

    /**
     * Creates FunctionCall AST node
     *
     * @return a new FunctionCall AST node or null.
     */
    public org.apache.xpath.rwapi.impl.FunctionCallImpl
           createFunctionCallNode(int id)
    {
        return new FunctionCallImpl(id);
    }

    /**
     * Creates VarName AST node
     *
     * @return a new VarName AST node or null.
     */
    public org.apache.xpath.rwapi.impl.VariableImpl createVarNameNode(int id) {
        return new VariableImpl(id);
    }

    /**
     * Creates AST node for the given tree node ID.
     *
     * @param id id of the node to create
     *
     * @return a new AST node or null.
     */
    public Node createNode(int id) {
        switch (id) {
            case XPathTreeConstants.JJTCASTEXPR:
                return new CastExprImpl(id);

            case XPathTreeConstants.JJTCASTABLEEXPR:
                return new CastableExprImpl(id);

            case XPathTreeConstants.JJTINSTANCEOFEXPR:
                return new InstanceOfExprImpl(id);

            case XPathTreeConstants.JJTQUANTIFIEDEXPR:
            case XPathTreeConstants.JJTFLWREXPR:
                return new ForAndQuantifiedExprImpl(id);

            case XPathTreeConstants.JJTIFEXPR:
                return new ConditionalExprImpl(id);

            default:
                // Falls through
        }

        return null;
    }
}
