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
 * @author Erwin Bolwidt <ejb@klomp.org>
 * @author John Howard <johnh@schemasoft.com>
 *
 */

package org.apache.xalan.xsltc.runtime;

import java.util.Locale;
import java.util.ResourceBundle;

import java.text.NumberFormat;
import java.text.MessageFormat;
import java.text.FieldPosition;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import org.xml.sax.AttributeList;

import org.apache.xalan.xsltc.*;
import org.apache.xalan.xsltc.DOM;
import org.apache.xalan.xsltc.NodeIterator;
import org.apache.xalan.xsltc.dom.Axis;
import org.apache.xalan.xsltc.dom.DOMAdapter;
import org.apache.xalan.xsltc.dom.MultiDOM;
import org.apache.xalan.xsltc.dom.AbsoluteIterator;
import org.apache.xalan.xsltc.dom.SingletonIterator;

import org.apache.xalan.xsltc.dom.DOMImpl;
import org.apache.xalan.xsltc.dom.DOMBuilder;
import org.apache.xalan.xsltc.dom.StepIterator;
import org.w3c.dom.NodeList;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilderFactory; 
import javax.xml.parsers.DocumentBuilder; 
import org.apache.xalan.xsltc.trax.DOM2SAX;

/**
 * Standard XSLT functions. All standard functions expect the current node 
 * and the DOM as their last two arguments.
 */
public final class BasisLibrary implements Operators {

    private final static String EMPTYSTRING = "";

    /**
     * Standard function count(node-set)
     */
    public static int countF(NodeIterator iterator) {
	return(iterator.getLast());
    }

    /**
     * XSLT Standard function sum(node-set). 
     * stringToDouble is inlined
     */
    public static double sumF(NodeIterator iterator, DOM dom) {
	try {
	    double result = 0.0;
	    int node;
	    while ((node = iterator.next()) != NodeIterator.END) {
		result += Double.parseDouble(dom.getNodeValue(node));
	    }
	    return result;
	}
	catch (NumberFormatException e) {
	    return Double.NaN;
	}
    }

    /**
     * XSLT Standard function string()
     */
    public static String stringF(int node, DOM dom) {
	return dom.getNodeValue(node);
    }

    /**
     * XSLT Standard function string(value)
     */
    public static String stringF(Object obj, DOM dom) {
	if (obj instanceof NodeIterator) {
	    return dom.getNodeValue(((NodeIterator)obj).reset().next());
	}
	else if (obj instanceof Node) {
	    return dom.getNodeValue(((Node)obj).node);
	}
	else if (obj instanceof DOM) {
	    return ((DOM)obj).getStringValue();
	}
	else {
	    return obj.toString();
	}
    }

    /**
     * XSLT Standard function string(value)
     */
    public static String stringF(Object obj, int node, DOM dom) {
	if (obj instanceof NodeIterator) {
	    return dom.getNodeValue(((NodeIterator)obj).reset().next());
	}
	else if (obj instanceof Node) {
	    return dom.getNodeValue(((Node)obj).node);
	}
	else if (obj instanceof DOM) {
	    // When the first argument is a DOM we want the whole fecking
	    // DOM and not just a single node - that would not make sense.
	    //return ((DOM)obj).getNodeValue(node);
	    return ((DOM)obj).getStringValue();
	}
	else if (obj instanceof Double) {
	    Double d = (Double)obj;
	    final String result = d.toString();
	    final int length = result.length();
	    if ((result.charAt(length-2)=='.') &&
		(result.charAt(length-1) == '0'))
		return result.substring(0, length-2);
	    else
		return result;
	}
	else {
	    if (obj != null)
		return obj.toString();
	    else
		return stringF(node, dom);
	}
    }

    /**
     * XSLT Standard function number()
     */
    public static double numberF(int node, DOM dom) {
	return stringToReal(dom.getNodeValue(node));
    }

    /**
     * XSLT Standard function number(value)
     */
    public static double numberF(Object obj, DOM dom) {
	if (obj instanceof Double) {
	    return ((Double) obj).doubleValue();
	}
	else if (obj instanceof Integer) {
	    return ((Integer) obj).doubleValue();
	}
	else if (obj instanceof Boolean) {
	    return  ((Boolean) obj).booleanValue() ? 1.0 : 0.0;
	}
	else if (obj instanceof String) {
	    return stringToReal((String) obj);
	}
	else if (obj instanceof NodeIterator) {
	    NodeIterator iter = (NodeIterator) obj;
	    return stringToReal(dom.getNodeValue(iter.reset().next()));
	}
	else if (obj instanceof Node) {
	    return stringToReal(dom.getNodeValue(((Node) obj).node));
	}
	else if (obj instanceof DOM) {
	    return stringToReal(((DOM) obj).getStringValue());
	}
	else {
	    final String className = obj.getClass().getName();
	    runTimeError(INVALID_ARGUMENT_ERR, className, "number()");
	    return 0.0;
	}
    }

    /**
     * XSLT Standard function boolean()
     */
    public static boolean booleanF(Object obj) {
	if (obj instanceof Double) {
	    final double temp = ((Double) obj).doubleValue();
	    return temp != 0.0 && !Double.isNaN(temp);
	}
	else if (obj instanceof Integer) {
	    return ((Integer) obj).doubleValue() != 0;
	}
	else if (obj instanceof Boolean) {
	    return  ((Boolean) obj).booleanValue();
	}
	else if (obj instanceof String) {
	    return !((String) obj).equals(EMPTYSTRING);
	}
	else if (obj instanceof NodeIterator) {
	    NodeIterator iter = (NodeIterator) obj;
	    return iter.reset().next() != NodeIterator.END;
	}
	else if (obj instanceof Node) {
	    return true;
	}
	else if (obj instanceof DOM) {
	    String temp = ((DOM) obj).getStringValue();
	    return !temp.equals(EMPTYSTRING);
	}
	else {
	    final String className = obj.getClass().getName();
	    runTimeError(INVALID_ARGUMENT_ERR, className, "number()");
	}
	return false;
    }

    /**
     * XSLT Standard function substring(). Must take a double because of
     * conversions resulting into NaNs and rounding.
     */
    public static String substringF(String value, double start) {
	try {
	    final int strlen = value.length();
	    int istart = (int)Math.round(start) - 1;

	    if (Double.isNaN(start)) return(EMPTYSTRING);
	    if (istart > strlen) return(EMPTYSTRING);
 	    if (istart < 1) istart = 0;

	    return value.substring(istart);
	}
	catch (IndexOutOfBoundsException e) {
	    runTimeError(RUN_TIME_INTERNAL_ERR, "substring()");
	    return null;
	}
    }

    /**
     * XSLT Standard function substring(). Must take a double because of
     * conversions resulting into NaNs and rounding.
     */
    public static String substringF(String value, double start, double length) {
	try {
	    final int strlen  = value.length();
	    int istart = (int)Math.round(start) - 1;
	    int isum   = istart + (int)Math.round(length);

	    if (Double.isInfinite(length)) isum = Integer.MAX_VALUE;

	    if (Double.isNaN(start) || Double.isNaN(length))
		return(EMPTYSTRING);
	    if (Double.isInfinite(start)) return(EMPTYSTRING);
	    if (istart > strlen) return(EMPTYSTRING);
	    if (isum < 0) return(EMPTYSTRING);
 	    if (istart < 0) istart = 0;

	    if (isum > strlen)
		return value.substring(istart);
	    else
		return value.substring(istart, isum);
	}
	catch (IndexOutOfBoundsException e) {
	    runTimeError(RUN_TIME_INTERNAL_ERR, "substring()");
	    return null;
	}
    }

    /**
     * XSLT Standard function substring-after(). 
     */
    public static String substring_afterF(String value, String substring) {
	final int index = value.indexOf(substring);
	if (index >= 0)
	    return value.substring(index + substring.length());
	else
	    return EMPTYSTRING;
    }

    /**
     * XSLT Standard function substring-before(). 
     */
    public static String substring_beforeF(String value, String substring) {
	final int index = value.indexOf(substring);
	if (index >= 0)
	    return value.substring(0, index);
	else
	    return EMPTYSTRING;
    }

    /**
     * XSLT Standard function translate(). 
     */
    public static String translateF(String value, String from, String to) {
	final int tol = to.length();
	final int froml = from.length();
	final int valuel = value.length();

	final StringBuffer result = new StringBuffer();
	for (int j, i = 0; i < valuel; i++) {
	    final char ch = value.charAt(i);
	    for (j = 0; j < froml; j++) {
		if (ch == from.charAt(j)) {
		    if (j < tol)
			result.append(to.charAt(j));
		    break;
		}
	    }	
	    if (j == froml)
		result.append(ch);
	}
	return result.toString();
    }

    /**
     * XSLT Standard function normalize-space(). 
     */
    public static String normalize_spaceF(int node, DOM dom) {
	return normalize_spaceF(dom.getNodeValue(node));
    }

    /**
     * XSLT Standard function normalize-space(string). 
     */
    public static String normalize_spaceF(String value) {
	int i = 0, n = value.length();
	StringBuffer result = new StringBuffer();

	while (i < n && isWhiteSpace(value.charAt(i)))
	    i++;

	while (true) {
	    while (i < n && !isWhiteSpace(value.charAt(i))) {
		result.append(value.charAt(i++));
	    }
	    if (i == n)
		break;
	    while (i < n && isWhiteSpace(value.charAt(i))) {
		i++;
	    }
	    if (i < n)
		result.append(' ');
	}
	return result.toString();
    }

    /**
     * XSLT Standard function generate-id(). 
     */
    public static String generate_idF(int node) {
	if (node > 0)
	    // Only generate ID if node exists
	    return "N" + node;
	else
	    // Otherwise return an empty string
	    return EMPTYSTRING;
    }
    
    /**
     * utility function for calls to local-name(). 
     */
    public static String getLocalName(String value) {
	int idx = value.lastIndexOf(':');
	if (idx >= 0) value = value.substring(idx + 1);
	idx = value.lastIndexOf('@');
	if (idx >= 0) value = value.substring(idx + 1);
	return(value);
    }

    /**
     * External functions that cannot be resolved are replaced with a call
     * to this method. This method will generate a runtime errors. A good
     * stylesheet checks whether the function exists using conditional
     * constructs, and never really tries to call it if it doesn't exist.
     * But simple stylesheets may result in a call to this method.
     * The compiler should generate a warning if it encounters a call to
     * an unresolved external function.
     */
    public static void unresolved_externalF(String name) {
	runTimeError(EXTERNAL_FUNC_ERR, name);
    }

    /**
     * XSLT Standard function namespace-uri(node-set).
     */
    public static String namespace_uriF(NodeIterator iter, DOM dom) {
	return namespace_uriF(iter.next(), dom);
    }

    /**
     * XSLT Standard function system-property(name)
     */
    public static String system_propertyF(String name) {
	if (name.equals("xsl:version"))
	    return("1.0");
	if (name.equals("xsl:vendor"))
	    return("Apache Software Foundation (Xalan XSLTC)");
	if (name.equals("xsl:vendor-url"))
	    return("http://xml.apache.org/xalan-j");
	
	runTimeError(INVALID_ARGUMENT_ERR, name, "system-property()");
	return(EMPTYSTRING);
    }

    /**
     * XSLT Standard function namespace-uri(). 
     */
    public static String namespace_uriF(int node, DOM dom) {
	final String value = dom.getNodeName(node);
	final int colon = value.lastIndexOf(':');
	if (colon >= 0)
	    return value.substring(0, colon);
	else
	    return EMPTYSTRING;
    }

    /**
     * Implements the nodeset() extension function. 
     */
    public static NodeIterator nodesetF(Object obj) {
	if (obj instanceof DOM) {
	   final DOMAdapter adapter = (DOMAdapter) obj;
	   return new SingletonIterator(
		  DOM.ROOTNODE | adapter.getMultiDOMMask(), true);
	}
        else if (obj instanceof NodeIterator) {
	   return (NodeIterator) obj;
        }
        else {
	    final String className = obj.getClass().getName();
	    runTimeError(DATA_CONVERSION_ERR, "node-set", className);
	    return null;
        }
    }

    //-- Begin utility functions

    private static boolean isWhiteSpace(char ch) {
	return ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r';
    }

    private static boolean compareStrings(String lstring, String rstring,
					  int op, DOM dom) {
	switch (op) {
	case EQ:
	    return lstring.equals(rstring);

	case NE:
	    return !lstring.equals(rstring);

	case GT:
	    return numberF(lstring, dom) > numberF(rstring, dom);

	case LT:
	    return numberF(lstring, dom) < numberF(rstring, dom);

	case GE:
	    return numberF(lstring, dom) >= numberF(rstring, dom);

	case LE:
	    return numberF(lstring, dom) <= numberF(rstring, dom);

	default:
	    runTimeError(RUN_TIME_INTERNAL_ERR, "compare()");
	    return false;
	}
    }

    /**
     * Utility function: node-set/node-set compare. 
     */
    public static boolean compare(NodeIterator left, NodeIterator right,
				  int op, int node, DOM dom) {
	int lnode;
	left.reset();
	
	while ((lnode = left.next()) != NodeIterator.END) {
	    final String lvalue = dom.getNodeValue(lnode);
	    
	    int rnode;
	    right.reset();
	    while ((rnode = right.next()) != NodeIterator.END) {
		if (compareStrings(lvalue, dom.getNodeValue(rnode), op, dom)) {
		    return true;
		}
	    }
	} 
	return false;
    }

    /**
     * Utility function: node/node-set compare.
     */
    public static boolean compare(int node, NodeIterator nodeSet,
				  int op, DOM dom) {
	final String lvalue = dom.getNodeValue(node);
	int rnode;
	//nodeSet.reset();
	while ((rnode = nodeSet.next()) != NodeIterator.END) {
	    if (compareStrings(lvalue, dom.getNodeValue(rnode), op, dom)) {
		return true;
	    }
	} 
	return false;
    }

    public static boolean compare(int node, NodeIterator iterator,
				  int op, int dummy, DOM dom) {
	//iterator.reset();

	int rnode;
	String value;

	switch(op) {
	case EQ:
	    /* TODO:
	     * This needs figuring out: What sort of comparison is done here?
	     * Are we comparing exact node id's, node types, or node values?
	     * Values is the obvious for attributes, but what about elements?
	     */
	    value = dom.getNodeValue(node);
	    while ((rnode = iterator.next()) != NodeIterator.END)
		if (value.equals(dom.getNodeValue(rnode))) return true;
	    // if (rnode == node) return true; It just ain't that easy!!!
	    break;
	case NE:
	    value = dom.getNodeValue(node);
	    while ((rnode = iterator.next()) != NodeIterator.END)
		if (!value.equals(dom.getNodeValue(rnode))) return true;
	    // if (rnode != node) return true;
	    break;
	case LT:
	    // Assume we're comparing document order here
	    while ((rnode = iterator.next()) != NodeIterator.END)
		if (rnode > node) return true;
	    break;
	case GT:
	    // Assume we're comparing document order here
	    while ((rnode = iterator.next()) != NodeIterator.END)
		if (rnode < node) return true;
	    break;
	} 
	return(false);
    }

    public static boolean compare(NodeIterator left, final double rnumber,
				  final int op, final int node, DOM dom) {
	return(compare(left,rnumber,op,dom));
    }

    /**
     * Utility function: node-set/number compare.
     */
    public static boolean compare(NodeIterator left, final double rnumber,
				  final int op, DOM dom) {
	int node;
	//left.reset();

	switch (op) {
	case EQ:
	    while ((node = left.next()) != NodeIterator.END) {
		if (numberF(dom.getNodeValue(node), dom) == rnumber)
		    return true;
	    }
	    break;

	case NE:
	    while ((node = left.next()) != NodeIterator.END) {
		if (numberF(dom.getNodeValue(node), dom) != rnumber)
		    return true;
	    }
	    break;

	case GT:
	    while ((node = left.next()) != NodeIterator.END) {
		if (numberF(dom.getNodeValue(node), dom) > rnumber)
		    return true;
	    }
	    break;

	case LT:
	    while ((node = left.next()) != NodeIterator.END) {
		if (numberF(dom.getNodeValue(node), dom) < rnumber)
		    return true;
	    }
	    break;

	case GE:
	    while ((node = left.next()) != NodeIterator.END) {
		if (numberF(dom.getNodeValue(node), dom) >= rnumber)
		    return true;
	    }
	    break;

	case LE:
	    while ((node = left.next()) != NodeIterator.END) {
		if (numberF(dom.getNodeValue(node), dom) <= rnumber)
		    return true;
	    }
	    break;

	default:
	    runTimeError(RUN_TIME_INTERNAL_ERR, "compare()");
	}

	return false;
    }

    /**
     * Utility function: node-set/string comparison. 
     */
    public static boolean compare(NodeIterator left, final String rstring,
				  int op, DOM dom) {
	int node;
	//left.reset();
	while ((node = left.next()) != NodeIterator.END) {
	    if (compareStrings(dom.getNodeValue(node), rstring, op, dom)) {
		return true;
	    }
	}
	return false;
    }

    public static boolean compare(NodeIterator left, final String rstring,
				  int op, int node, DOM dom) {
	
	if (compareStrings(dom.getNodeValue(node), rstring, op, dom)) {
	    return true;
	}
	else {
	    return false;
	}
    }

    public static boolean compare(Object left, Object right,
				  int op, int node, DOM dom) { 
	boolean result = false;
	boolean hasSimpleArgs = hasSimpleType(left) && hasSimpleType(right);

	if (op != EQ && op != NE) {
	    // If node-boolean comparison -> convert node to boolean
	    if (left instanceof Node || right instanceof Node) {
		if (left instanceof Boolean) {
		    right = new Boolean(booleanF(right));
		    hasSimpleArgs = true;
		}
		if (right instanceof Boolean) {
		    left = new Boolean(booleanF(left));
		    hasSimpleArgs = true;
		}
	    }

	    if (hasSimpleArgs) {
		switch (op) {
		case GT:
		    return numberF(left, dom) > numberF(right, dom);
		    
		case LT:
		    return numberF(left, dom) < numberF(right, dom);
		    
		case GE:
		    return numberF(left, dom) >= numberF(right, dom);
		    
		case LE:
		    return numberF(left, dom) <= numberF(right, dom);
		    
		default:
		    runTimeError(RUN_TIME_INTERNAL_ERR, "compare()");
		}
	    }
	    // falls through
	}

	if (hasSimpleArgs) {
	    if (left instanceof Boolean || right instanceof Boolean) {
		result = booleanF(left) == booleanF(right);
	    }
	    else if (left instanceof Double || right instanceof Double ||
		     left instanceof Integer || right instanceof Integer) {
		result = numberF(left, dom) == numberF(right, dom);
	    }
	    else { // compare them as strings
		result = stringF(left, dom).equals(stringF(right, dom));
	    }

	    if (op == Operators.NE) {
		result = !result;
	    }
	}
	else {
	    if (left instanceof Node) {
		left = new SingletonIterator(((Node)left).node);
	    }
	    if (right instanceof Node) {
		right = new SingletonIterator(((Node)right).node);
	    }

	    if (hasSimpleType(left) ||
		left instanceof DOM && right instanceof NodeIterator) {
		// swap operands
		final Object temp = right; right = left; left = temp;
	    }

	    if (left instanceof DOM) {
		if (right instanceof Boolean) {
		    result = ((Boolean)right).booleanValue();
		    return result == (op == Operators.EQ);
		}

		final String sleft = ((DOM)left).getStringValue();

		if (right instanceof Number) {
		    result = ((Number)right).doubleValue() ==
			stringToReal(sleft);
		}
		else if (right instanceof String) {
		    result = sleft.equals((String)right);
		}
		else if (right instanceof DOM) {
		    result = sleft.equals(((DOM)right).getStringValue());
		}

		if (op == Operators.NE) {
		    result = !result;
		}
		return result;
	    }

	    // Next, node-set/t for t in {real, string, node-set, result-tree}

	    NodeIterator iter = ((NodeIterator)left).reset();

	    if (right instanceof NodeIterator) {
		result = compare(iter, (NodeIterator)right, op, node, dom);
	    }
	    else if (right instanceof String) {
		//result = compare(iter, (String)right, op, node, dom);
		result = compare(iter, (String)right, op, dom);
	    }	
	    else if (right instanceof Number) {
		final double temp = ((Number)right).doubleValue();
		result = compare(iter, temp, op, dom);
	    }
	    else if (right instanceof Boolean) {
		boolean temp = ((Boolean)right).booleanValue();
		result = (iter.reset().next() != NodeIterator.END) == temp;
	    }
	    else if (right instanceof DOM) {
		result = compare(iter, ((DOM)right).getStringValue(),
				 op, dom);
	    }
	    else if (right == null) {
		return(false);
	    }
	    else {
		final String className = right.getClass().getName();
		runTimeError(INVALID_ARGUMENT_ERR, className, "compare()");
	    }
	}
	return result;
    }

    /**
     * Utility function: used to test context node's language
     */
    public static boolean testLanguage(String testLang, DOM dom, int node) {
	// language for context node (if any)
	String nodeLang = dom.getLanguage(node);
	if (nodeLang == null)
	    return(false);
	else
	    nodeLang = nodeLang.toLowerCase();

	// compare context node's language agains test language
	testLang = testLang.toLowerCase();
	if (testLang.length() == 2) {
	    return(nodeLang.startsWith(testLang));
	}
	else {
	    return(nodeLang.equals(testLang));
	}
    }

    private static boolean hasSimpleType(Object obj) {
	return obj instanceof Boolean || obj instanceof Double ||
	    obj instanceof Integer || obj instanceof String ||
	    obj instanceof Node || obj instanceof DOM; 
    }

    /**
     * Utility function: used in StringType to convert a string to a real.
     */
    public static double stringToReal(String s) {
	try {
	    return Double.valueOf(s).doubleValue();
	}
	catch (NumberFormatException e) {
	    return Double.NaN;
	}
    }

    /**
     * Utility function: used in StringType to convert a string to an int.
     */
    public static int stringToInt(String s) {
	try {
	    return Integer.parseInt(s);
	}
	catch (NumberFormatException e) {
	    return(-1); // ???
	}
    }

    private static double lowerBounds = 0.001;
    private static double upperBounds = 10000000;
    private static DecimalFormat defaultFormatter;
    private static String defaultPattern = "";

    static {
	NumberFormat f = NumberFormat.getInstance(Locale.getDefault());
	// set max fraction digits so that truncation does not occur,
	// see conf test string134
	f.setMaximumFractionDigits(Integer.MAX_VALUE);
	defaultFormatter = (f instanceof DecimalFormat) ?
	    (DecimalFormat) f : new DecimalFormat();
	defaultFormatter.setGroupingUsed(false);
    }

    /**
     * Utility function: used in RealType to convert a real to a string.
     * Removes the decimal if null.
     */
    public static String realToString(double d) {
	final double m = Math.abs(d);
	if ((m >= lowerBounds) && (m < upperBounds)) {
	    final String result = Double.toString(d);
	    final int length = result.length();
	    // Remove leading zeros.
	    if ((result.charAt(length-2) == '.') &&
		(result.charAt(length-1) == '0'))
		return result.substring(0, length-2);
	    else
		return result;
	}
	else {
	    if (Double.isNaN(d) || Double.isInfinite(d))
		return(Double.toString(d));
	    return formatNumber(d, defaultPattern, defaultFormatter);
	}
    }

    /**
     * Utility function: used in RealType to convert a real to an integer
     */
    public static int realToInt(double d) {
	return (int)d;
    }

    /**
     * Utility function: used to format/adjust  a double to a string. The 
     * DecimalFormat object comes from the 'format_symbols' hashtable in 
     * AbstractTranslet.
     */
    private static FieldPosition _fieldPosition = new FieldPosition(0);

    public static String formatNumber(double number, String pattern,
				      DecimalFormat formatter) {
	try {
	    StringBuffer result = new StringBuffer();
	    if (pattern != defaultPattern) {
		formatter.applyLocalizedPattern(pattern);
	    }

	    //------------------------------------------------------
 	    // bug fix # 9179 - make sure localized pattern contains
	    //   a leading zero before decimal, handle cases where  
	    //   decimal is in position zero, and >= to 1. 
	    //   localized pattern is ###.### convert to ##0.###
	    //   localized pattern is .###    convert to 0.###
	    //------------------------------------------------------
	    String localizedPattern = formatter.toPattern();
	    int index = localizedPattern.indexOf('.');
	    if ( index >= 1  && localizedPattern.charAt(index-1) == '#' ) {
		//insert a zero before the decimal point in the pattern
		StringBuffer newpattern = new StringBuffer();
		newpattern.append(localizedPattern.substring(0, index-1));
                newpattern.append("0");
                newpattern.append(localizedPattern.substring(index));
		formatter.applyLocalizedPattern(newpattern.toString());
	    } else if (index == 0) {
                // insert a zero before decimal point in pattern
                StringBuffer newpattern = new StringBuffer();
                newpattern.append("0");
                newpattern.append(localizedPattern);
		formatter.applyLocalizedPattern(newpattern.toString());
            }

	    formatter.format(number, result, _fieldPosition);
	    return(result.toString());
	}
	catch (IllegalArgumentException e) {
	    runTimeError(FORMAT_NUMBER_ERR, Double.toString(number), pattern);
	    return(EMPTYSTRING);
	}
    }
    
    /**
     * Utility function: used to convert references to node-sets. If the
     * obj is an instanceof Node then create a singleton iterator.
     */
    public static NodeIterator referenceToNodeSet(Object obj) {
	// Convert var/param -> node
	if (obj instanceof Node) {
	    return(new SingletonIterator(((Node)obj).node));
	}
	// Convert var/param -> node-set
	else if (obj instanceof NodeIterator) {
	    return(((NodeIterator)obj).cloneIterator());
	}
	else {
	    final String className = obj.getClass().getName();
	    runTimeError(DATA_CONVERSION_ERR, "reference", className);
	    return null;
	}
    }

    /**
     * Utility function used to convert a w3c NodeList into a internal
     * DOM iterator. 
     */
    public static NodeIterator nodeList2Iterator(org.w3c.dom.NodeList nodeList,
	Translet translet, DOM dom) 
    {
	int size = nodeList.getLength();

	// w3c NodeList -> w3c DOM
	DocumentBuilderFactory dfac = DocumentBuilderFactory.newInstance();
	DocumentBuilder docbldr = null;
	try {
	    docbldr = dfac.newDocumentBuilder();
	} catch (javax.xml.parsers.ParserConfigurationException e) {
	    runTimeError(RUN_TIME_INTERNAL_ERR, e.getMessage());
            return null;

	}
	// create new w3c DOM
	Document doc = docbldr.newDocument();	
        org.w3c.dom.Node topElementNode = 
            doc.appendChild(doc.createElementNS("", "__top__"));

	// copy Nodes from NodeList into new w3c DOM
	for (int i=0; i<size; i++){
	    org.w3c.dom.Node curr = nodeList.item(i);
	    int nodeType = curr.getNodeType();
	    if (nodeType == org.w3c.dom.Node.DOCUMENT_NODE) {
		// ignore the root node of node list
		continue;
	    }
	    String value = null;
	    try {
	        value = curr.getNodeValue();
	    } catch (DOMException ex) {
		runTimeError(RUN_TIME_INTERNAL_ERR, ex.getMessage());
                return null;
	    }
	    String namespaceURI = curr.getNamespaceURI();
	    String nodeName = curr.getNodeName();
	    org.w3c.dom.Node newNode = null; 
	    switch (nodeType){
		case org.w3c.dom.Node.ATTRIBUTE_NODE: 
		     newNode = doc.createAttributeNS(namespaceURI,
			nodeName);
                     break;
		case org.w3c.dom.Node.CDATA_SECTION_NODE: 
		     newNode = doc.createCDATASection(value);
                     break;
		case org.w3c.dom.Node.COMMENT_NODE: 
		     newNode = doc.createComment(value);
                     break;
		case org.w3c.dom.Node.DOCUMENT_FRAGMENT_NODE: 
		     newNode = doc.createDocumentFragment();
                     break;
		case org.w3c.dom.Node.DOCUMENT_TYPE_NODE: 
		     // nothing ?
                     break;
		case org.w3c.dom.Node.ELEMENT_NODE: 
		     newNode = doc.createElementNS(namespaceURI, nodeName);
		     break;
		case org.w3c.dom.Node.ENTITY_NODE: 
		     // nothing ? 
                     break;
		case org.w3c.dom.Node.ENTITY_REFERENCE_NODE: 
		     newNode = doc.createEntityReference(nodeName);
		     break;
		case org.w3c.dom.Node.NOTATION_NODE: 
		     // nothing ? 
		     break;
		case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE: 
		     newNode = doc.createProcessingInstruction(nodeName,
			value);
		     break;
		case org.w3c.dom.Node.TEXT_NODE: 
		     newNode = doc.createTextNode(value);
		     break;
	    }
	    try {
	        topElementNode.appendChild(newNode);
	    } catch (DOMException e) {
		runTimeError(RUN_TIME_INTERNAL_ERR, e.getMessage());
		return null;
	    }
	}
	// w3c DOM -> DOM2SAX -> DOMBuilder -> DOMImpl
	DOMImpl idom = new DOMImpl();
	final DOM2SAX dom2sax = new DOM2SAX(doc);
	final DOMBuilder domBuilder = idom.getBuilder();
	dom2sax.setContentHandler(domBuilder);
	try {
	    dom2sax.parse(); 
	} 
        catch (java.io.IOException e){
	    runTimeError(RUN_TIME_INTERNAL_ERR, e.getMessage());
            return null;
	}
        catch (org.xml.sax.SAXException e){
	    runTimeError(RUN_TIME_INTERNAL_ERR, e.getMessage());
            return null;
	}
	

	if (dom instanceof MultiDOM) {
            final MultiDOM multiDOM = (MultiDOM) dom;

	    // Create DOMAdapter and register with MultiDOM
	    DOMAdapter domAdapter = new DOMAdapter(idom, 
                translet.getNamesArray(),
		translet.getNamespaceArray());
            multiDOM.addDOMAdapter(domAdapter);

	    NodeIterator iter1 = multiDOM.getAxisIterator(Axis.CHILD);
	    NodeIterator iter2 = multiDOM.getAxisIterator(Axis.CHILD);
            NodeIterator iter = new AbsoluteIterator(
                new StepIterator(iter1, iter2));

 	    iter.setStartNode(DOM.ROOTNODE | domAdapter.getMultiDOMMask());
	    return iter;
	}
        else {
	    runTimeError(RUN_TIME_INTERNAL_ERR, "nodeList2Iterator()");
	    return null;
        }
    }

    /**
     * Utility function used to convert references to DOMs. 
     */
    public static DOM referenceToResultTree(Object obj) {
	try {
	    return ((DOM) obj);
	}
	catch (IllegalArgumentException e) {
	    final String className = obj.getClass().getName();
	    runTimeError(DATA_CONVERSION_ERR, "reference", className);
	    return null;
	}
    }

    /**
     * Utility function: used with nth position filters to convert a sequence
     * of nodes to just one single node (the one at position n).
     */
    public static NodeIterator getSingleNode(NodeIterator iterator) {
	int node = iterator.next();
	return(new SingletonIterator(node));
    }

    /**
     * Utility function: used in xsl:copy.
     */
    private static char[] _characterArray = new char[32];

    public static void copy(Object obj,
			    TransletOutputHandler handler,
			    int node,
			    DOM dom) {
	try {
	    if (obj instanceof NodeIterator) {
		NodeIterator iter = (NodeIterator) obj;
		dom.copy(iter.reset(), handler);
	    }
	    else if (obj instanceof Node) {
		dom.copy(((Node) obj).node, handler);
	    }
	    else if (obj instanceof DOM) {
		((DOM)obj).copy(1, handler);
	    }
	    else {
		String string = obj.toString();		// or call stringF()
		final int length = string.length();
		if (length > _characterArray.length)
		    _characterArray = new char[length];
		string.getChars(0, length, _characterArray, 0);
		handler.characters(_characterArray, 0, length);
	    }
	}
	catch (TransletException e) {
	    runTimeError(RUN_TIME_COPY_ERR);
	}
    }
    
    /**
     * This function is used in the execution of xsl:element
     */
    public static String getPrefix(String qname) {
	final int index = qname.indexOf(':');
	return (index > 0) ? qname.substring(0, index) : null;
    }

    /**
     * This function is used in the execution of xsl:element
     */
    private static int prefixIndex = 0;
    public static String generatePrefix() {
	return ("ns" + prefixIndex++);
    }

    /**
     * This function is used in the execution of xsl:element
     */
    public static String makeQName(String localName, String prefix) {
	return (new StringBuffer(prefix).append(':').append(localName).toString());
    }

    public static final int RUN_TIME_INTERNAL_ERR   = 0;
    public static final int RUN_TIME_COPY_ERR       = 1;
    public static final int DATA_CONVERSION_ERR     = 2;
    public static final int EXTERNAL_FUNC_ERR       = 3;
    public static final int EQUALITY_EXPR_ERR       = 4;
    public static final int INVALID_ARGUMENT_ERR    = 5;
    public static final int FORMAT_NUMBER_ERR       = 6;
    public static final int ITERATOR_CLONE_ERR      = 7;
    public static final int AXIS_SUPPORT_ERR        = 8;
    public static final int TYPED_AXIS_SUPPORT_ERR  = 9;
    public static final int STRAY_ATTRIBUTE_ERR     = 10; 
    public static final int STRAY_NAMESPACE_ERR     = 11;
    public static final int NAMESPACE_PREFIX_ERR    = 12;
    public static final int DOM_ADAPTER_INIT_ERR    = 13;

    // All error messages are localized and are stored in resource bundles.
    // This array and the following 4 strings are read from that bundle.
    private static String[] _errorMessages;
    
    public final static String ERROR_MESSAGES_KEY = "error-messages";

    static {
	String resource = "org.apache.xalan.xsltc.runtime.ErrorMessages";
	ResourceBundle bundle = ResourceBundle.getBundle(resource);
	_errorMessages  = bundle.getStringArray(ERROR_MESSAGES_KEY);
    }

    /**
     * Print a run-time error message.
     */
    public static void runTimeError(int code) {
	throw new RuntimeException(_errorMessages[code]);
    }

    public static void runTimeError(int code, Object[] args) {
	final String message = MessageFormat.format(_errorMessages[code],args);
	throw new RuntimeException(message);
    }

    public static void runTimeError(int code, Object arg0) {
	runTimeError(code, new Object[]{ arg0 } );
    }

    public static void runTimeError(int code, Object arg0, Object arg1) {
	runTimeError(code, new Object[]{ arg0, arg1 } );
    }

    public static void consoleOutput(String msg) {
	System.out.println(msg);
    }

    /**
     * Replace a certain character in a string with a new substring.
     */
    public static String replace(String base, char ch, String str) {
	return (base.indexOf(ch) < 0) ? base : 
	    replace(base, String.valueOf(ch), new String[] { str });
    }

    public static String replace(String base, String delim, String[] str) {
	final int len = base.length();
	final StringBuffer result = new StringBuffer();

	for (int i = 0; i < len; i++) {
	    final char ch = base.charAt(i);
	    final int k = delim.indexOf(ch);

	    if (k >= 0) {
		result.append(str[k]);
	    }
	    else {
		result.append(ch);
	    }
	}
	return result.toString();
    }

    //-- End utility functions
}
