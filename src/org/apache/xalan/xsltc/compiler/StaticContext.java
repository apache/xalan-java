/*
 * @(#)$Id$
 *
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
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

package org.apache.xalan.xsltc.compiler;

import java.text.Collator;
import java.util.ArrayList;

import org.apache.xalan.xsltc.compiler.util.Type;

interface StaticContext {

    /**
     * Returns a reference to the stylesheet object.
     */
    public Stylesheet getStylesheet();

    /**
     * Returns true if XPath 1.0 compatibility is on.
     */
    public boolean getXPath10CompatibleFlag();

    /**
     * Base URI for the stylesheet. This is used by fn:document to
     * resolve relative URIs.
     */
    public String getBaseURI();

    /**
     * Default namespace for elements.
     */
    public String getDefaultElementNamespace();

    /**
     * Default namespace for types (same as for elements).
     */
    public String getDefaultTypeNamespace();

    /**
     * Default namespace for functions.
     */
    public String getDefaultFunctionNamespace();

    /**
     * Returns the in-scope namespace declaration bound to 'prefix'.
     * If prefix is the empty string, the default namespace is returned.
     */
    public String getNamespace(String prefix);

    /**
     * Returns the in-scope type definition bound to 'name'. Return
     * type is implementation dependent.
     */
    public Type getSchemaType(QName name);

    /**
     * Returns an instance of the default collator.
     */
    public Collator getDefaultCollator();

    /**
     * Returns the collator object associated to 'URI'.
     */
    public Collator getCollator(String URI);

    /**
     * Returns the decimal formatting object of a given name. These
     * objects are defined using xsl:decimal-format. The name of
     * a decimal formatting object is used by format-number().
     */
    public DecimalFormatting getDecimalFormatting(QName name);

    /**
     * Returns the template object associated to 'name'. These names
     * are used by xsl:call-template.
     */
    public Template getTemplate(QName name);

    /**
     * Returns a reference to the "current" template.
     */
    public Template getCurrentTemplate();

    /**
     * Returns the variable/param object associated to 'name'.
     */
    public VariableBase getVariable(QName qname);

    /**
     * Returns the attribute set object associated to 'name'. Attribute
     * sets are defined via xsl:attribute-set. Attribute sets can be
     * referenced from xsl:copy and xsl:element.
     */
    public AttributeSet getAttributeSet(QName name);

    /**
     * Returns a list of all primitive operations (i.e. operators
     * and functions) associated to 'name'. Primops can be overloaded;
     * the first element of the list returned is deemed to be the
     * default (note that there can be more than one implementation
     * with the same name and the same arity even though this is not
     * supported in XSLT/XPath).
     */
    public ArrayList getPrimop(String name);

    /**
     * Returns the alias for a namespace prefix. Namespace aliases
     * can be defined using xsl:namespace-alias.
     */
    public String getPrefixAlias(String prefix);

    /**
     * Check if a namespace should not be declared in the output
     * (unless used)
     */
    public boolean getExcludeUri(String uri);
}

