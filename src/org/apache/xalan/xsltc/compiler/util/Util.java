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
 *
 */

package org.apache.xalan.xsltc.compiler.util;

import org.apache.bcel.generic.Type;
import org.apache.bcel.generic.*;
import org.apache.xalan.xsltc.compiler.Parser;

public final class Util {
    static public char filesep;

    static {
	String temp = System.getProperty("file.separator", "/");
	filesep = temp.charAt(0);
    }

    public static String noExtName(String name) {
	final int index = name.lastIndexOf('.');
	return name.substring(0, index >= 0 ? index : name.length());
    }

    /**
     * Search for both slashes in order to support URLs and 
     * files.
     */
    public static String baseName(String name) {
	int index = name.lastIndexOf('/');
	if (index < 0) {
	    index = name.lastIndexOf('\\');
	}
	return name.substring(index + 1);
    }

    /**
     * Search for both slashes in order to support URLs and 
     * files.
     */
    public static String pathName(String name) {
	int index = name.lastIndexOf('/');
	if (index < 0) {
	    index = name.lastIndexOf('\\');
	}
	return name.substring(0, index + 1);
    }

    /**
     * Replace all illegal Java chars by '_'.
     */
    public static String toJavaName(String name) {
	if (name.length() > 0) {
	    final StringBuffer result = new StringBuffer();

	    char ch = name.charAt(0);
	    result.append(Character.isJavaIdentifierStart(ch) ? ch : '_');

	    final int n = name.length();
	    for (int i = 1; i < n; i++) {
		ch = name.charAt(i);
		result.append(Character.isJavaIdentifierPart(ch)  ? ch : '_');
	    }
	    return result.toString();
	}
	return name;
    }

    public static Type getJCRefType(String signature) {
	return Type.getType(signature);
    }

    public static String internalName(String cname) {
	return cname.replace('.', filesep);
    }

    public static void println(String s) {
	if (false) {
	    System.out.println(s);
	}
    }

    public static void println(char ch) {
	if (false) {
	    System.out.println(ch);
	}
    }

    public static void TRACE1() {
	System.out.println("TRACE1");
    }

    public static void TRACE2() {
	System.out.println("TRACE2");
    }

    public static void TRACE3() {
	System.out.println("TRACE3");
    }

    /**
     * Replace a certain character in a string with a new substring.
     */
    public static String replace(String base, char c, String str) {
	final int len = base.length() - 1;
	int pos;
	while ((pos = base.indexOf(c)) > -1) {
	    if (pos == 0) {
		final String after = base.substring(1);
		base = str + after;
	    }
	    else if (pos == len) {
		final String before = base.substring(0, pos);
		base = before + str;
	    }
	    else {
		final String before = base.substring(0, pos);
		final String after = base.substring(pos+1);
		base = before + str + after;
	    }
	}
	return base;
    }

    /**
     * Replace occurances of '.' with '$dot$' and '-' with '$dash$'
     */
    public static String escape(String input) {
	input = replace(input, '.', "$dot$");
	input = replace(input, '-', "$dash$");
	return input;
    }

}

