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
package org.apache.xpath.datamodel;

import org.apache.xpath.XPath20Exception;
import org.apache.xpath.expression.KindTest;

/**
 * Represents a <em>sequence type</em>.
 * A sequence type is a {@link KindTest} with an {@link #getOccurrenceIndicator() occurence indicator}
 * and the following {@link KindTest#getKindTestType() kindtest type} additions:
 * <ul>
 * <li>{@link #EMPTY_SEQ} for <code>empty()</code> sequence.</li>
 * <li>{@link #ITEM_TYPE} for <code>item()</code> type.</li>
 * <li>{@link #ATOMIC_TYPE} for QName type. Use {@link org.apache.xpath.expression.KindTest#getTypeName()} to get it.</li>
 * </ul>
 * 
 * @author <a href="mailto:villard@us.ibm.com">Lionel Villard</a>
 * @version $Id$
 * @see <a href="http://www.w3.org/TR/xpath20#id-sequencetype">XPath 2.0 Specification</a>
 */
public interface SequenceType extends KindTest
{
	/**
	 * Denote the empty sequence type
	 */
	final short EMPTY_SEQ = -1;

	/**
	 * Item type constants
	 */
	final short ITEM_TYPE = 7;
	final short ATOMIC_TYPE = 8;

	/**
	 * Occurrence indicator constants
	 */
	final short ZERO_OR_MORE = 0;
	final short ONE_OR_MORE = 1;
	final short ZERO_OR_ONE = 2;
	final short ONE = 3;

	/**
	 * Gets the type of xpath values in the sequence
	 * @return short One of the item type (including kindtest type) constants or {@link #EMPTY_SEQ}.
	 */
	short getItemType();

	/**
	 * Gets the occurrence indicator of the item type 
	 * @return short One of the occurrence indicator constants or -1 in case of empty sequence type
	 */
	short getOccurrenceIndicator();

	/**
	 * Sets the occurrence indicator of the item type. 
	 * @param short One of the occurrence indicator constants 
	 * @throws XPath20Exception whenever this expression is {@link #EMPTY_SEQ}.
	 */
	void setOccurrenceIndicator(short occ) throws XPath20Exception;

}
