/*
 * The Apache Software License, Version 1.1
 *
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
package org.apache.xml.dtm.ref.xni2dtm;

import org.w3c.dom.Node;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMManager;
import org.apache.xml.dtm.ref.DTMDefaultBase;
import org.apache.xml.dtm.ref.DTMNodeProxy;
import org.apache.xml.dtm.ref.xni2dtm.XNI2DTM;
import org.apache.xml.dtm.ref.xni2dtm.DTM2XNI;
import org.apache.xpath.XPathContext;
import javax.xml.transform.URIResolver;

/** Temporary extension function, prototyping proposed XSLT2
 * &lt;xsl:import-schema [namespace="uri-reference"] [schema-location="uri-reference"]/&gt;
 * 
 * Per xslt20.30apr02.html#import-schema:
 * 
 *  The xsl:import-schema declaration is used to identify schema components (that is,
 *  definitions of types) which need to be available statically, that is, before any source
 *  document is available. Every type name used statically within the stylesheet (including
 *  type names used within XPath expressions) must be defined in an imported schema.
 *  The declaration imports the element and attribute declarations and type definitions
 *  from the schema, and maps them to types in the XPath data model according to rules
 *  defined in [].
 *
 *      Ed. Note: Whether or not the locally declared elements and attributes of a
 *      schema are imported is an open issue.
 *
 *  The namespace and schema-location attributes are both optional. At least one of them
 *  must be present, and it is permissible to supply both.
 *
 *  The namespace attribute indicates that a schema for the given namespace is required
 *  by the stylesheet. This information may be enough on its own to enable an
 *  implementation to locate the required schema components.
 *
 *  The schema-location attribute gives the URI of a location where a schema document
 *  or other resource containing the required definitions may be found. An XSLT
 *  processor must have the capability to process an XML Schema that exists at this
 *  location in the form of a source XML document; implementations may also be able to
 *  access equivalent information held in other forms, for example a compiled XML
 *  Schema, or type information expressed using some other schema language.
 *
 *  [ERR016] It is a static error if the processor is not able to locate a schema using the
 *  namespace and/or schema-location attributes , or if the document that it locates is
 *  neither a valid XML Schema nor any other resource that the implementation can
 *  process.
 *
 *  [ERR017] It is a static error if two xsl:import-schema declarations yield multiple
 *  definitions for the same named type, even if the definitions are consistent with each
 *  other.
 *
 *  The use of a namespace in an xsl:import-schema declaration does not by itself make
 *  the namespace available for use in the stylesheet. If names from the namespace are
 *  used within the stylesheet, a prefix must be associated with the namespace by means
 *  of a namespace declaration, in the normal way.
 * 
 *  The precise way in which an implementation uses the namespace and/or
 *  schema-location attributes to locate schema definitions is implementation-defined.
 * 
 * */
public class ElemImportSchema {
	private static final boolean JJK_DISABLE_VALIDATOR=false; // debugging hook
	
	public static String eval(org.apache.xalan.extensions.ExpressionContext expressionContext,
		String schemaNamespace, String schemaLocation) 
	{
		// This happens to work in current code. It isn't really
		// documented. Future versions expect to expose it more elegantly,
		// according to Don Leslie. But since this extension is just
		// temporary, let's use the cheat... We know it's going to be a
		// particular inner class, which has an accessor to retrieve its
		// associated XPathContext, so we reach in and ask it to reach back.
		XPathContext xctxt = ((XPathContext.XPathExpressionContext)expressionContext).getXPathContext();

		// Looking up a schema by namespace is still an ill-defined operation
		// at best, pending the Semantic Web (which as far as I know is still
		// naught but a vision held by Tim Berners-Lee). It will probably have
		// to be handled by a plug-in resolver of some kind until that settles.
		// For now, the simplest answer seems to be to make whoever does entity
		// resolution deal with it, calling the namespace a public ID.
		// %REVIEW%
		xctxt.addInScopeSchemaDefinitions(schemaNamespace,schemaLocation);
		
		return ""; // Bogus, just required because this is temporarily a function
	}
}

