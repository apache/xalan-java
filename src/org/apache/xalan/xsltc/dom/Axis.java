/*
 * @(#)$Id$
 *
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
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
 * originally based on software copyright (c) 2001, Sun
 * Microsystems., http://www.sun.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 * @author Jacek Ambroziak
 * @author Santiago Pericas-Geertsen
 * @author Morten Jorgensen
 *
 */

package org.apache.xalan.xsltc.dom;

/*
 * IMPORTANT NOTE - this interface will probably be replaced by
 * org.apache.xml.dtm.Axis (very similar)
 */

public interface Axis {

    public static final int ANCESTOR         =  0;
    public static final int ANCESTORORSELF   =  1;
    public static final int ATTRIBUTE        =  2;
    public static final int CHILD            =  3;
    public static final int DESCENDANT       =  4;
    public static final int DESCENDANTORSELF =  5;
    public static final int FOLLOWING        =  6;
    public static final int FOLLOWINGSIBLING =  7;
    public static final int NAMESPACEDECLS   =  8;
    public static final int NAMESPACE        =  9;
    public static final int PARENT           = 10;
    public static final int PRECEDING        = 11;
    public static final int PRECEDINGSIBLING = 12;
    public static final int SELF             = 13;
	
    public static final String[] names = {
	"ancestor",
	"ancestor-or-self",
	"attribute",
	"child",
	"descendant",
	"descendant-or-self",
	"following",
	"following-sibling",
	"namespace",
	"namespace-decls",
	"parent",
	"preceding",
	"preceding-sibling",
	"self"
    };

    public static final boolean[] isReverse = {
	true,  // ancestor
	true,  // ancestor-or-self
	false, // attribute
	false, // child
	false, // descendant
	false, // descendant-or-self
	false, // following
	false, // following-sibling
	false, // namespace
	false, // namespace-declarations
	false, // parent (one node, has no order)
	true,  // preceding
	true,  // preceding-sibling
	false  // self (one node, has no order)
    };
}
