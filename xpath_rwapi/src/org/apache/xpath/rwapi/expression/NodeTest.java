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
 * originally based on software copyright (c) 1999, Lotus
 * Development Corporation., http://www.lotus.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.xpath.rwapi.expression;

import org.apache.xpath.rwapi.XPathException;

/**
 * Represents item test. An item test includes both node tests and the context item
 * selection.  
 * <pre>
 * [28]   NodeTest   ::=   KindTest |  NameTest 
 * [29]   NameTest   ::=   QName |  Wildcard 
 * [30]   Wildcard   ::=   "*" |  ":"? NCName ":" "*" |  "*" ":" NCName 
 * [31]   KindTest   ::=   ProcessingInstructionTest 
 *                          |  CommentTest
 *                          |  TextTest
 *                          |  AnyKindTest 
 * [32]   ProcessingInstructionTest   ::=   "processing-instruction" "(" StringLiteral? ")" 
 * [33]   CommentTest   ::=   "comment" "(" ")" 
 * [34]   TextTest   ::=   "text" "(" ")" 
 * [35]   AnyKindTest   ::=   "node" "(" ")"
 * </pre>
 * @see <a href="http://www.w3.org/TR/xpath20/#doc-NodeTest">Node test specification</a>
 * @see <a href="http://www.w3.org/TR/xpath20/#abbrev">Context item specification</a>
 */
public interface NodeTest {
    
    /**
     * The node test is a wildcard
     */
    static final String WILDCARD = "*";
    
    /**
     * The item test is a processing instruction kind test 
     */
	static final short PROCESSING_INSTRUCTION_TEST = 0;
	
	/**
	 * The item test is a comment kind test
	 */
	static final short COMMENT_TEST = 1;
	
	/**	 
	 * The item test is any kind of test (except context item test)
	 */
	static final short ANY_KIND_TEST = 2;
	
	/**
	 * The item test is a text kind test
	 */
	static final short TEXT_TEST = 3;
	
	/**
	 * The node test is a context item test
	 */
	static final short CONTEXT_ITEM_TEST = 4;
    
    /**
     * Full name of kind tests. 
     * This array is kept in synchronization with kind test constants
     */
	static final String[] KIND_TEST_NAME = { "processing-instruction()", "comment()", "node()", "text()", "." };
    
    /**
     * Return true whenever this node test is a name test
     * @return boolean
     */
    boolean isNameTest();
    
    /**
     * Return true whenever this node test is a kind test
     * @return boolean
     */
    boolean isKindTest();
    
    /**
     * Gets the kind test
     * @return short One of the kind test constant value
     * @throws XPathException whenever this node test isn't a kind test
     */
    short getKindTest() throws XPathException; 
    
    /**
     * Gets the local part of the name test
     * @return String The local part of the name test or {@link #WILDCARD}
     * @throws XPathException whenever this node test isn't name test
     */
    String getLocalNameTest() throws XPathException;
    
    /**
     * Gets the prefix of the name test
     * @return String The prefix part of the name test or {@link #WILDCARD}
     * @throws XPathException whenever this node test isn't a name test
     */
    String getPrefix() throws XPathException;
}


