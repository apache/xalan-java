/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002 The Apache Software Foundation.  All rights 
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
package org.apache.xpath.rwapi.expression;

import java.util.Set;

import org.apache.xpath.rwapi.eval.NamespaceContext;

/**
 * Represents the static context of an expression.
 * @see <a href="http://www.w3.org/TR/xpath20/#static_context">XPath 2.0 specification</a>
 */
public interface StaticContext {
    
    short STRICT_POLICY = 0;
    short FLEXIBLE_POLICY = 1;

    /**
     * 
     */
    short getExceptionPolicy();

    /**
     * In-scope namespaces. This is a set of (prefix, URI) pairs. 
     */
    NamespaceContext getNamespaces();

    /**
     * Default namespace for element and type names. 
     */
    String getDefaultNamepaceForElement();

    /**
     * Default namespace for function names. 
     */
    String getDefaultNamepaceforFunction();

    /**
     * In-scope schema definitions. This is a set of (QName, type definition) pairs.
     */
    void getSchemaDefs();

    /**
     * In-scope variables. This is a set of (QName, type) pairs. 
     */
    Set getVariables();
    
    /**
     * In-scope functions. This is a set of (QName, function signature) pairs.
     */
    void getFunctions();
    
    /**
     * In-scope collations. This is a set of (URI, collation) pairs. 
     */
    void getCollations();

    /**
     * Default collation. 
     */
    void getDefaultCollation();
    
    /**
     * Base URI.
     */
    String getBaseURI();
}
    