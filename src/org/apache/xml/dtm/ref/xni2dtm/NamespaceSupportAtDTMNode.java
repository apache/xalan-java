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

import org.xml.sax.helpers.NamespaceSupport;
import java.util.Enumeration;
import org.apache.xml.dtm.*;


/** Perform SAX-style lookup of namespace information in the context of a
 * DTM node. Written as part of the glue for XNI2DTM and XPath2Type, but
 * may be useful elsewhere. Unclear which package it should live in.
 * */
public class NamespaceSupportAtDTMNode extends NamespaceSupport {
	DTM m_dtm;
	int m_nodeHandle;

	/**
	 * Constructor for NamespaceContextAtDTMNode
	 */
	public NamespaceSupportAtDTMNode() {
		throw new UnsupportedOperationException("not supported in this class");
	}
	/**
	 * Constructor for NamespaceContextAtDTMNode
	 */
	public NamespaceSupportAtDTMNode(DTM dtm,int nodeHandle) {
		m_dtm=dtm;
		m_nodeHandle=nodeHandle;
	}

    public boolean declarePrefix (String prefix, String uri) {
		throw new UnsupportedOperationException("not supported in this class");
	}
	/* No-op in this instantiation */
    public void pushContext ()
    {
    }
	/* No-op in this instantiation */
    public void popContext ()
    {
    }
	/* No-op in this instantiation */
    public void reset ()    
    {
    }
    
    /**
     * Return an enumeration of all prefixes declared in this context.
     *
     * <p>The empty (default) prefix will be included in this 
     * enumeration; note that this behaviour differs from that of
     * {@link #getPrefix} and {@link #getPrefixes}.</p>
     *
     * @return An enumeration of all prefixes declared in this
     *         context.
     * @see #getPrefixes
     * @see #getURI
     */
    public Enumeration getDeclaredPrefixes ()
    {
    	return new Enumeration()
    	{
    		int nshandle=m_dtm.getFirstNamespaceNode(
				m_nodeHandle,true);

			public boolean hasMoreElements()
			{
				return nshandle!=DTM.NULL;
			}
			public Object nextElement()
			{
				if(nshandle==DTM.NULL)
					return null;
				String prefix=m_dtm.getLocalName(nshandle);
	    		nshandle=m_dtm.getNextNamespaceNode(
					m_nodeHandle,nshandle,true);
				return prefix;
			}
		};
    }
    
    
    /**
     * Return one of the prefixes mapped to a Namespace URI.
     *
     * <p>If more than one prefix is currently mapped to the same
     * URI, this method will make an arbitrary selection; if you
     * want all of the prefixes, use the {@link #getPrefixes}
     * method instead.</p>
     *
     * <p><strong>Note:</strong> this will never return the empty (default) prefix;
     * to check for a default prefix, use the {@link #getURI getURI}
     * method with an argument of "".</p>
     *
     * @param uri The Namespace URI.
     * @param isAttribute true if this prefix is for an attribute
     *        (and the default Namespace is not allowed).
     * @return One of the prefixes currently mapped to the URI supplied,
     *         or null if none is mapped or if the URI is assigned to
     *         the default Namespace.
     * @see #getPrefixes(java.lang.String)
     * @see #getURI
     */
    public String getPrefix (String uri)
    {
    	if(uri==null)
    		uri="";
    		
   		int nshandle=m_dtm.getFirstNamespaceNode(
			m_nodeHandle,true);
		while(nshandle!=DTM.NULL)
		{
			if(uri.equals(m_dtm.getNamespaceURI(nshandle)))
			{
				String prefix=m_dtm.getLocalName(nshandle);
				if(prefix!=null && !prefix.equals(""))
					return prefix;
			}
    		nshandle=m_dtm.getNextNamespaceNode(
				m_nodeHandle,nshandle,true);
		}
		return null;
    }
    
    /**
     * Look up a prefix and get the currently-mapped Namespace URI.
     *
     * <p>This method looks up the prefix in the current context.
     * Use the empty string ("") for the default Namespace.</p>
     *
     * @param prefix The prefix to look up.
     * @return The associated Namespace URI, or null if the prefix
     *         is undeclared in this context.
     * @see #getPrefix
     * @see #getPrefixes
     */
    public String getURI (String prefix)
    {
    	if(prefix==null)
    		prefix="";
    		
   		int nshandle=m_dtm.getFirstNamespaceNode(
			m_nodeHandle,true);
		while(nshandle!=DTM.NULL)
		{
			if(prefix.equals(m_dtm.getLocalName(nshandle))) // getPrefix?
				//return m_dtm.getNamespaceURI(nshandle);
				return m_dtm.getNodeValue(nshandle);
    		nshandle=m_dtm.getNextNamespaceNode(
				m_nodeHandle,nshandle,true);
		}
		return null;
    }

    /**
     * Return an enumeration of all prefixes currently declared.
     *
     * <p><strong>Note:</strong> if there is a default prefix, it will not be
     * returned in this enumeration; check for the default prefix
     * using the {@link #getURI getURI} with an argument of "".</p>
     *
     * @return An enumeration of all prefixes declared in the
     *         current context except for the empty (default)
     *         prefix.
     * @see #getDeclaredPrefixes
     * @see #getURI
     */
    public Enumeration getPrefixes ()
    {
    	return new Enumeration()
    	{    		
    		int nshandle=m_dtm.getFirstNamespaceNode(
				m_nodeHandle,true);

			public boolean hasMoreElements()
			{
				skipdefault();
				return nshandle!=DTM.NULL;
			}
			public Object nextElement()
			{
				skipdefault();
				if(nshandle==DTM.NULL)
					return null;
				String prefix=m_dtm.getLocalName(nshandle);
	    		nshandle=m_dtm.getNextNamespaceNode(
					m_nodeHandle,nshandle,true);
				return prefix;
			}
			
			private void skipdefault()
			{
				if(nshandle!=DTM.NULL &&
					"".equals(m_dtm.getLocalName(nshandle)))
		    		nshandle=m_dtm.getNextNamespaceNode(
						m_nodeHandle,nshandle,true);
			}
		};
    }    

    /**
     * Return an enumeration of all prefixes currently declared for a URI.
     *
     * <p>This method returns prefixes mapped to a specific Namespace
     * URI.  The xml: prefix will be included.  If you want only one
     * prefix that's mapped to the Namespace URI, and you don't care 
     * which one you get, use the {@link #getPrefix getPrefix}
     *  method instead.</p>
     *
     * <p><strong>Note:</strong> the empty (default) prefix is <em>never</em> included
     * in this enumeration; to check for the presence of a default
     * Namespace, use the {@link #getURI getURI} method with an
     * argument of "".</p>
     *
     * @param uri The Namespace URI.
     * @return An enumeration of all prefixes declared in the
     *         current context.
     * @see #getPrefix
     * @see #getDeclaredPrefixes
     * @see #getURI
     */
    public Enumeration getPrefixes (String uri)
    {
    	return new Enumeration()
    	{    		
    		String e_uri;
    		int nshandle=m_dtm.getFirstNamespaceNode(
				m_nodeHandle,true);

			// Kluge to pass in the URI
			public Enumeration setURI(String uri)
			{
				e_uri=uri;
				return this;
			}

			public boolean hasMoreElements()
			{
				skipwronguri();
				return nshandle!=DTM.NULL;
			}
			public Object nextElement()
			{
				skipwronguri();
				if(nshandle==DTM.NULL)
					return null;
				String prefix=m_dtm.getLocalName(nshandle);
	    		nshandle=m_dtm.getNextNamespaceNode(
					m_nodeHandle,nshandle,true);
				return prefix;
			}
			
			private void skipwronguri()
			{
				while(nshandle!=DTM.NULL &&
					! (e_uri.equals(m_dtm.getLocalName(nshandle))) )
		    		nshandle=m_dtm.getNextNamespaceNode(
						m_nodeHandle,nshandle,true);
			}
		}.setURI(uri);
    }
    
    
    /**
     * Process a raw XML 1.0 name, after all declarations in the current
     * context have been handled by {@link #declarePrefix declarePrefix()}.
     *
     * <p>This method processes a raw XML 1.0 name in the current
     * context by removing the prefix and looking it up among the
     * prefixes currently declared.  The return value will be the
     * array supplied by the caller, filled in as follows:</p>
     *
     * <dl>
     * <dt>parts[0]</dt>
     * <dd>The Namespace URI, or an empty string if none is
     *  in use.</dd>
     * <dt>parts[1]</dt>
     * <dd>The local name (without prefix).</dd>
     * <dt>parts[2]</dt>
     * <dd>The original raw name.</dd>
     * </dl>
     *
     * <p>All of the strings in the array will be internalized.  If
     * the raw name has a prefix that has not been declared, then
     * the return value will be null.</p>
     *
     * <p>Note that attribute names are processed differently than
     * element names: an unprefixed element name will received the
     * default Namespace (if any), while an unprefixed attribute name
     * will not.</p>
     *
     * @param qName The raw XML 1.0 name to be processed.
     * @param parts An array supplied by the caller, capable of
     *        holding at least three members.
     * @param isAttribute A flag indicating whether this is an
     *        attribute name (true) or an element name (false).
     * @return The supplied array holding three internalized strings 
     *        representing the Namespace URI (or empty string), the
     *        local name, and the raw XML 1.0 name; or null if there
     *        is an undeclared prefix.
     * @see #declarePrefix
     * @see java.lang.String#intern */
    public String [] processName (String qName, String parts[],
				  boolean isAttribute)
    {
   		parts[2]=qName;
    	
    	int colon=qName.indexOf(':');
   		parts[1]=(colon<0) ? qName : qName.substring(colon+1);
    	
    	String pfx;
    	if(colon==-1)
    		pfx=(isAttribute) ? null : "";
    	else
	    	pfx=qName.substring(0,colon);
    	parts[0] = (pfx==null) ? null : getURI(pfx);
	    	
	    return parts;
    }
    
}

