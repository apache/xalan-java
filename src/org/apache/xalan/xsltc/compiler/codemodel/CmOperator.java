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
 * @author Santiago Pericas-Geertsen
 *
 */

package org.apache.xalan.xsltc.compiler.codemodel;

public class CmOperator {

    private static final String[] stringRep = {
	"+",	// PLUS
	"-",	// MINUS
	"*",	// STAR
	"/",	// DIV
	"%",	// MOD
	"<",	// LT
	"<=",	// LE
	">",	// GT
	">=",	// GE
	"==",	// EQ
	"!=",	// NE
	"&&",	// AND
	"||", 	// OR
	"<<", 	// SL
	">>", 	// SR
	"&",	// LAND
	"|",	// LOR
	"^",	// LXOR
	"!",	// NOT
	"~",	// LNOT
	"++",	// PPLUS
	"--", 	// MMINUS
	"=",	// ASGN
	"*=",	// STAR_ASGN
	"/=",	// DIV_ASGN
	"%=",	// MOD_ASGN
	"+=",	// PLUS_ASGN
	"-=",	// MINUS_ASGN
	"<<=",	// SL_ASGN
	">>=",	// SR_ASGN
	"&=",  	// LAND_ASGN
	"|=",	// LOR_ASGN
	"^=",	// LXOR_ASGN
    };

    public static final CmOperator PLUS       = new CmOperator(0);
    public static final CmOperator MINUS      = new CmOperator(1);
    public static final CmOperator STAR       = new CmOperator(2);
    public static final CmOperator DIV        = new CmOperator(3);
    public static final CmOperator MOD        = new CmOperator(4);
    public static final CmOperator LT         = new CmOperator(5);
    public static final CmOperator LE         = new CmOperator(6);
    public static final CmOperator GT         = new CmOperator(7);
    public static final CmOperator GE         = new CmOperator(8);
    public static final CmOperator EQ         = new CmOperator(9);
    public static final CmOperator NE 	    = new CmOperator(10);
    public static final CmOperator AND 	    = new CmOperator(11);
    public static final CmOperator OR 	    = new CmOperator(12);
    public static final CmOperator SL 	    = new CmOperator(13);
    public static final CmOperator SR	    = new CmOperator(14);
    public static final CmOperator LAND 	    = new CmOperator(15);
    public static final CmOperator LOR 	    = new CmOperator(16);
    public static final CmOperator LXOR 	    = new CmOperator(17);
    public static final CmOperator NOT 	    = new CmOperator(18);
    public static final CmOperator LNOT 	    = new CmOperator(19);
    public static final CmOperator PPLUS 	    = new CmOperator(20);
    public static final CmOperator MMINUS     = new CmOperator(21);
    public static final CmOperator ASGN 	    = new CmOperator(22);
    public static final CmOperator STAR_ASGN  = new CmOperator(23);
    public static final CmOperator DIV_ASGN   = new CmOperator(24);
    public static final CmOperator MOD_ASGN   = new CmOperator(25);
    public static final CmOperator PLUS_ASGN  = new CmOperator(26);
    public static final CmOperator MINUS_ASGN = new CmOperator(27);
    public static final CmOperator SL_ASGN    = new CmOperator(28);
    public static final CmOperator SR_ASGN    = new CmOperator(29);
    public static final CmOperator LAND_ASGN  = new CmOperator(30);
    public static final CmOperator LOR_ASGN   = new CmOperator(31);
    public static final CmOperator LXOR_ASGN  = new CmOperator(32);

    private int _code;

    private CmOperator(int code) {
	_code = code;
    }

    public String toString() {
	return stringRep[_code];
    }
}
