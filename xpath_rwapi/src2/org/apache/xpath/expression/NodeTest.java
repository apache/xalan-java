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
package org.apache.xpath.expression;

import org.apache.xml.QName;
import org.apache.xpath.XPath20Exception;

/**
 * Represents an item test. An item test includes both node tests 
 * and the context item test (dot).  
 * <p>
 * A node test is either a name test or a kind test. For the former, use
 * the method {@link #getNameTest()} to get the {@link QName} involved in the test. 
 * </p>
 * <p>In the case of kind test, the method {@link #getKindTest()} return the kind
 * test type as following:
 * <ul>
 * <li><b>{@link #TEXT_TEST}</b> for text()</li>
 * <li><b>{@link #PROCESSING_INSTRUCTION_TEST}</b> for processing-instruction()</b>. 
 * Use {@link #getNameTest()}.getLocalPart() to get the target.</li>
 * <li><b>{@link #COMMENT_TEST}</b> for comment()</li>
 * <li><b>{@link #ANY_KIND_TEST}</b> for node()</li>
 * <li><b> {@link #CONTEXT_ITEM_TEST}</b> for '.'</li>
 * </ul> 
 * 
 * @see <a href="http://www.w3.org/TR/xpath20/#doc-NodeTest">Node test specification</a>
 * @see <a href="http://www.w3.org/TR/xpath20/#abbrev">Context item specification</a>
 */
public interface NodeTest {
    
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
	 * The node test is a context item test (belong to the kind test group)
	 */
	static final short CONTEXT_ITEM_TEST = 4;
    
    /**
     * Full name of kind tests. 
     * This array is synchronized with the kind test constants
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
     * Gets the kind test code.
     * @return short One of the kind test constant value
     * @throws XPath20Exception whenever this node test isn't a kind test
     */
    short getKindTest() throws XPath20Exception; 
    
    /**
     * Gets the qualified name of the name test or the name of the 'PITarget'
     * when the node test is pi kind test. For the later, only the local
     * part of the qualified name is relevant.
     * @return QName The name test
     * @throws XPath20Exception whenever this node test isn't a name test
     * or a pi kind test
     */
    QName getNameTest() throws XPath20Exception;
    
   
}


