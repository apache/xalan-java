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
package org.apache.xpath.impl.parser;

import org.apache.xpath.impl.FunctionCallImpl;
import org.apache.xpath.impl.KindTestImpl;
import org.apache.xpath.impl.LiteralImpl;
import org.apache.xpath.impl.NameTestImpl;
import org.apache.xpath.impl.OperatorImpl;
import org.apache.xpath.impl.PathExprImpl;
import org.apache.xpath.impl.SequenceTypeImpl;
import org.apache.xpath.impl.StepExprImpl;
import org.apache.xpath.impl.VariableImpl;

/**
 * Implementation of {@link NodeFactory}. Returns null for each
 * factory method.
 * <p>Provides also a way to create a node factory. By now, it tries
 * to instantiate an user class, by looking at the system property
 * with the key {@link #FACTORY_PROPERTY}. If its value is a valid 
 * class name which implements {@link NodeFactory}, then a new 
 * instance of this class is returned. Otherwise, a singletion of this object 
 * is returned.
 
 * @author <a href="mailto:villard@us.ibm.com">Lionel Villard</a>
 * @version $Id$
 */
public class DefaultNodeFactory implements NodeFactory 
{

	final static public String FACTORY_PROPERTY_KEY = "org.apache.xpath.impl.parser.NodeFactory";

	final static private NodeFactory DEFAULT_NODE_FACTORY = new DefaultNodeFactory();

	static protected NodeFactory createNodeFactory()
	{
		String cn = System.getProperty(FACTORY_PROPERTY_KEY);
		NodeFactory result;
		if (cn != null)
		{
			try
			{
				Class c = Class.forName(cn);
				
				result = (NodeFactory) c.newInstance();
			} catch (ClassNotFoundException e)
			{
				result = DEFAULT_NODE_FACTORY;
			} catch (InstantiationException e)
			{
				result = DEFAULT_NODE_FACTORY;
			} catch (IllegalAccessException e)
			{
				result = DEFAULT_NODE_FACTORY;
			} catch (ClassCastException e)
			{
				result = DEFAULT_NODE_FACTORY;
			}
		}
		else
		{
			result = DEFAULT_NODE_FACTORY;
		}
		
		return result;
	}

	// Implements NodeFactory

	public FunctionCallImpl createFunctionCallNode(int id) {
		return null;
	}

	public KindTestImpl createKindTestNode(int id) {
		return null;
	}

	public LiteralImpl createLiteralNode(int id) {
		return null;
	}

	public NameTestImpl createNameTestNode(int id) {
		return null;
	}

	public Node createNode(int id) {
		return null;
	}

	public OperatorImpl createOperatorNode(int id) {
		return null;
	}

	public PathExprImpl createPathNode(int id) {
		return null;
	}

	public StepExprImpl createStepNode(int id) {
		return null;
	}

	public VariableImpl createVarNameNode(int id) {
		return null;
	}
	
	public SequenceTypeImpl createSequenceTypeNode(int id) {
		return null;

	}

	public SequenceTypeImpl createCastAsNode(int id) {
		return null;
	}

	public SequenceTypeImpl createTreatAsNode(int id) {
		return null;
	}

}
