/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights reserved.
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
 * originally based on software copyright (c) 2003, International Business
 * Machines, Inc., http://www.ibm.com.  For more information on the Apache
 * Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.xml.serializer;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;

/**
 * This class keeps track of the currently defined namespaces. Conceptually the
 * prefix/uri/depth triplets are pushed on a stack pushed on a stack. The depth
 * indicates the nesting depth of the element for which the mapping was made.
 * 
 * <p>For example:
 * <pre>
 * <chapter xmlns:p1="def">
 *   <paragraph xmlns:p2="ghi">
 *      <sentance xmlns:p3="jkl">
 *      </sentance>
 *    </paragraph>
 *    <paragraph xlmns:p4="mno">
 *    </paragraph>
 * </chapter>
 * </pre>
 * 
 * When the <chapter> element is encounted the prefix "p1" associated with uri
 * "def" is pushed on the stack with depth 1.
 * When the first <paragraph> is encountered "p2" and "ghi" are pushed with
 * depth 2.
 * When the <sentance> is encountered "p3" and "jkl" are pushed with depth 3.
 * When </sentance> occurs the popNamespaces(3) will pop "p3"/"jkl" off the
 * stack.  Of course popNamespaces(2) would pop anything with depth 2 or
 * greater.
 * 
 * So prefix/uri pairs are pushed and poped off the stack as elements are
 * processed.  At any given moment of processing the currently visible prefixes
 * are on the stack and a prefix can be found given a uri, or a uri can be found
 * given a prefix.
 */
public class NamespaceMappings
{
    /**
     * This member is continually incremented when new prefixes need to be
     * generated. ("ns0"  "ns1" ...)
     */
    private int count = 0;

    /**
     * Stack of prefixes that have mappings
     * The top of this stack is the prefix that was last mapped to an URI
     * 
     * For every prefix pushed on this stack a corresponding integer is pushed
     * on the m_nodeStack. That way all prefixes pushed at the current depth can
     * be removed at the same time.      
     */
    private java.util.Stack m_prefixStack = new Stack();

    /**
     * Each entry (prefix) in this hashtable points to a Stack of URIs
     */
    private Hashtable m_namespaces = new Hashtable();

    /** 
     * The top of this stack contains the nested element depth
     * of the last declared a namespace.
     * Used to know how many prefix mappings to pop when leaving
     * the current element depth.
     * For every prefix mapping the current element depth is 
     * pushed on this stack, as well as the prefix on the m_prefixStack
     * That way all prefixes pushed at the current depth can be 
     * removed at the same time.
     * Used to ensure prefix/uri map scopes are closed correctly
     *
     */
    private Stack m_nodeStack = new Stack();

    private static final String EMPTYSTRING = "";
    private static final String XML_PREFIX = "xml"; // was "xmlns"

    /**
     * Default constructor
     * @see java.lang.Object#Object()
     */
    public NamespaceMappings()
    {
        initNamespaces();
    }

    /**
     * This method initializes the namespace object with appropriate stacks
     * and predefines a few prefix/uri pairs which always exist.
     */
    private void initNamespaces()
    {
 

        // Define the default namespace (initially maps to "" uri)
        Stack stack;
        m_namespaces.put(EMPTYSTRING, stack = new Stack());
        stack.push(EMPTYSTRING);
        m_prefixStack.push(EMPTYSTRING);

        m_namespaces.put(XML_PREFIX, stack = new Stack());
        stack.push("http://www.w3.org/XML/1998/namespace");
        m_prefixStack.push(XML_PREFIX);

        m_nodeStack.push(new Integer(-1));

    }

    /**
     * Use a namespace prefix to lookup a namespace URI.
     * 
     * @param prefix String the prefix of the namespace
     * @return the URI corresponding to the prefix
     */
    public String lookupNamespace(String prefix)
    {
        final Stack stack = (Stack) m_namespaces.get(prefix);
        return stack != null && !stack.isEmpty() ? (String) stack.peek() : null;
    }

    /**
     * Given a namespace uri, and the namespaces mappings for the 
     * current element, return the current prefix for that uri.
     * 
     * @param uri the namespace URI to be search for
     * @return an existing prefix that maps to the given URI, null if no prefix
     * maps to the given namespace URI.
     */
    public String lookupPrefix(String uri)
    {
        String foundPrefix = null;
        Enumeration prefixes = m_namespaces.keys();
        while (prefixes.hasMoreElements())
        {
            String prefix = (String) prefixes.nextElement();
            String uri2 = lookupNamespace(prefix);
            if (uri2 != null && uri2.equals(uri))
            {
                foundPrefix = prefix;
                break;
            }
        }
        return foundPrefix;
    }

    /**
     * Undeclare the namespace that is currently pointed to by a given prefix
     */
    public boolean popNamespace(String prefix)
    {
        // Prefixes "xml" and "xmlns" cannot be redefined
        if (prefix.startsWith(XML_PREFIX))
        {
            return false;
        }

        Stack stack;
        if ((stack = (Stack) m_namespaces.get(prefix)) != null)
        {
            stack.pop();
            return true;
        }
        return false;
    }

    /**
     * Declare a prefix to point to a namespace URI
     * @param prefix a String with the prefix for a qualified name
     * @param uri a String with the uri to which the prefix is to map
     * @param elemDepth the depth of current declaration
     */
    public boolean pushNamespace(String prefix, String uri, int elemDepth)
    {
        // Prefixes "xml" and "xmlns" cannot be redefined
        if (prefix.startsWith(XML_PREFIX))
        {
            return false;
        }

        Stack stack;
        // Get the stack that contains URIs for the specified prefix
        if ((stack = (Stack) m_namespaces.get(prefix)) == null)
        {
            m_namespaces.put(prefix, stack = new Stack());
        }

        if (!stack.empty() && uri.equals(stack.peek()))
        {
            return false;
        }

        stack.push(uri);
        m_prefixStack.push(prefix);
        m_nodeStack.push(new Integer(elemDepth));
        return true;
    }

    /**
     * Pop, or undeclare all namespace definitions that are currently
     * declared at the given element depth, or deepter.
     * @param elemDepth the element depth for which mappings declared at this
     * depth or deeper will no longer be valid
     */
    public void popNamespaces(int elemDepth)
    {
        while (true)
        {
            if (m_nodeStack.isEmpty())
                return;
            Integer i = (Integer) (m_nodeStack.peek());
            if (i.intValue() < elemDepth)
                return;
            /* the depth of the declared mapping is elemDepth or deeper
             * so get rid of it
             */

            m_nodeStack.pop();
            popNamespace((String) m_prefixStack.pop());
        }
    }

    /**
     * Generate a new namespace prefix ( ns0, ns1 ...) not used before
     * @return String a new namespace prefix ( ns0, ns1, ns2 ...)
     */
    public String generateNextPrefix()
    {
        return "ns" + (count++);
    }

 
    /**
     * This method makes a clone of this object.
     * @see java.lang.Object#clone()
     */
    public Object clone() throws CloneNotSupportedException {
        NamespaceMappings clone = new NamespaceMappings();
        clone.m_prefixStack = (Stack)m_prefixStack.clone();
        clone.m_nodeStack = (Stack) m_nodeStack.clone();
        clone.m_namespaces = (Hashtable) m_namespaces.clone();
        
        clone.count = count;
        return clone;
        
    }
    
    public final void reset()
    {
    	this.count = 0;
    	this.m_namespaces.clear();
    	this.m_nodeStack.clear();
    	this.m_prefixStack.clear();
    	
    	initNamespaces();
    }

}
