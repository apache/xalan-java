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

import org.apache.xml.dtm.*;
import org.w3c.dom.*;

import org.apache.xerces.xni.*;
import org.apache.xerces.xni.psvi.*;
import org.apache.xerces.xni.grammars.XMLGrammarPool;


/** This is a vague -- very vague -- attempt to encapsulate the XPath2
 * "in-scope schema definitions". Our only available implementation at this
 * time is highly Xerces-dependent, but I'd really rather not expose that
 * more widely than I have to. 
 * 
 * This is supposed to be a set of (QName, type definition) pairs, defining the set
 * of types available for reference within XPath expressions, including
 * the validate() operation. PLEASE NOTE that these make absolutely no
 * reference to the types referenced by the instance document, though the
 * two sets are expected/hoped to overlap.
 * 
 * The built-in schema types should always be available. Additional schema
 * documents can be loaded into the mix.
 * 
 * %REVIEW% Many issues -- base URI, you-name-it.
 * */
public class AbstractSchema {
	org.apache.xerces.impl.xs.XMLSchemaLoader xsload=
		new org.apache.xerces.impl.xs.XMLSchemaLoader();
	org.apache.xerces.util.XMLGrammarPoolImpl pool=
		new org.apache.xerces.util.XMLGrammarPoolImpl();
		
	public AbstractSchema()
	{
		// Apparently required to bind the two together, per Neil G.
		// This implies pool.putGrammar will be called after loading.
		xsload.setProperty(DOMValidationConfigurationSwipedFromXerces.GRAMMAR_POOL,pool);
	}
		
	public void appendSchema(String publicId, String systemId,  
                          String baseSystemId, java.io.InputStream byteStream,
                          String encoding)
	{
		if(publicId==null) publicId="";
		if(systemId==null) systemId="";
		if(baseSystemId==null) baseSystemId="";
		try
		{
			org.apache.xerces.xni.grammars.Grammar gmr=
				xsload.loadGrammar(
					new org.apache.xerces.xni.parser.XMLInputSource(
					publicId, systemId, baseSystemId, byteStream, encoding));
		}
		catch (java.io.IOException e)
		{
			// DO SOMETHING WITH THIS!
			System.err.println(e);
		}
	}
	public void appendSchema(String publicId, String systemId,  
                          String baseSystemId, java.io.Reader charStream,
                          String encoding)
	{
		if(publicId==null) publicId="";
		if(systemId==null) systemId="";
		if(baseSystemId==null) baseSystemId="";
		try
		{
			org.apache.xerces.xni.grammars.Grammar gmr=
				xsload.loadGrammar(
					new org.apache.xerces.xni.parser.XMLInputSource(
					publicId, systemId, baseSystemId, charStream, encoding));
		}
		catch (java.io.IOException e)
		{
			// DO SOMETHING WITH THIS!
			System.err.println(e);
		}
	}
	public void appendSchema(String publicId, String systemId,  
                          String baseSystemId)
	{
		if(publicId==null) publicId="";
		if(systemId==null) systemId="";
		if(baseSystemId==null) baseSystemId="";
		try
		{
			// %REVIEW% Need more education on loading grammars; this is a kluge
			// Need to set an error handler on this.
			// Should we really reset every time?
			xsload.reset();
			
			org.apache.xerces.xni.grammars.Grammar gmr=
				xsload.loadGrammar(
					new org.apache.xerces.xni.parser.XMLInputSource(
					publicId, systemId, baseSystemId));
		}
		catch (java.io.IOException e)
		{
			// DO SOMETHING WITH THIS!
			System.err.println(e);
		}
	}
	
	public Object getGrammarPool()
	{
		return pool;
	}
	
	/** This relies on unpublished Xerces APIs. It's even more vulnerable
	 * to future change than the rest of the PSVI interfaces.
	 * Caveat hackitor!
	 * 
	 * Outline courtesy of Sandy Gao -- thanks again!
	 * */
	public XPath2Type lookUpType(String namespace,String typename)
	{
		XPath2Type ret=null;	
		
		org.apache.xerces.impl.xs.XSDDescription descr=
			new org.apache.xerces.impl.xs.XSDDescription();
		descr.setContextType(descr.CONTEXT_PREPARSE);
		if(namespace==null) namespace=""; // I *think* this is right; not sure
		descr.setTargetNamespace(namespace);

		org.apache.xerces.xni.grammars.Grammar g=pool.getGrammar(descr);
		if(g!=null)		
			ret=new XPath2Type( ((org.apache.xerces.impl.xs.SchemaGrammar)g).getGlobalTypeDecl(typename),
				namespace,typename);
			
		return ret;
	}

}

