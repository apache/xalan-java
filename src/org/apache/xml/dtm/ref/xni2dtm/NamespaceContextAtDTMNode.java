package org.apache.xml.dtm.ref.xni2dtm;

import org.apache.xerces.xni.NamespaceContext;
import org.apache.xml.dtm.*;


/** Perform XNIstyle lookup of namespace information in the context of a
 * DTM node. Written as part of the glue for XNI2DTM and XPath2Type, but
 * may be useful elsewhere. Unclear which package it should live in.
 * */
public class NamespaceContextAtDTMNode implements NamespaceContext {
	DTM m_dtm;
	int m_nodeHandle;

	/**
	 * Constructor for NamespaceContextAtDTMNode
	 */
	public NamespaceContextAtDTMNode(DTM dtm,int nodeHandle) {
		m_dtm=dtm;
		m_nodeHandle=nodeHandle;
	}
	
	/** Relocate this context object to a new node 
	 * It is the caller's responsibility -- as with getParentContext() --
	 * to save any status info, or reset the node selection. */
	public void setCurrentNode(DTM dtm,int nodeHandle) {
		m_dtm=dtm;
		m_nodeHandle=nodeHandle;
	}

    /**
     * Return a count of locally declared prefixes, including
     * the default prefix if bound.
     * %REVIEW% INEFFICIENT since all DTM provides is low-level iterator.
     * %OPT% We could cache into a list; would that be better or worse?
     */
    public int getDeclaredPrefixCount()
    {
    	int nshandle=m_dtm.getFirstNamespaceNode(m_nodeHandle,false);
    	int count=0;
    	while(nshandle!=m_dtm.NULL)
    	{
    		++count;
    		nshandle=m_dtm.getNextNamespaceNode(m_nodeHandle,nshandle,false);
    	}
    	return count;    	
    }

    /** 
     * Returns the prefix at the specified index in the current context.
     * %REVIEW% INEFFICIENT since all DTM provides is low-level iterator.
     * %OPT% We could cache into a list; would that be better or worse?
     */
    public String getDeclaredPrefixAt(int index)
    {
    	int nshandle=m_dtm.getFirstNamespaceNode(m_nodeHandle,false);
    	while(index-- >= 0 && nshandle!=m_dtm.NULL)
    	{
    		nshandle=m_dtm.getNextNamespaceNode(m_nodeHandle,nshandle,false);
    	}
    	
    	// Should this be getLocalName or getPrefix?
    	// %BUG% %REVIEW%
		return m_dtm.getLocalName(nshandle);
    }

    /**
     * Returns the parent namespace context or null if there is no
     * parent context. The total depth of the namespace contexts 
     * matches the element depth in the document.
     * <p>
     * <strong>Note:</strong> This method <em>may</em> return the same 
     * NamespaceContext object reference. The caller is responsible for
     * saving the declared prefix mappings before calling this method.
     */
    public NamespaceContext getParentContext()
    {
    	int parenthandle=m_dtm.getParent(m_nodeHandle);
    	if(parenthandle==m_dtm.NULL)
    		return null;
    	
   		m_nodeHandle=parenthandle;
   		return this;
    }    
    
    
    
    /**
     * Return one of the prefixes mapped to a Namespace URI
     * in the local context.
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
			m_nodeHandle,false);
		while(nshandle!=DTM.NULL)
		{
			if(uri.equals(m_dtm.getNamespaceURI(nshandle)))
			{
				String prefix=m_dtm.getLocalName(nshandle);
				if(prefix!=null && !prefix.equals(""))
					return prefix;
			}
    		nshandle=m_dtm.getNextNamespaceNode(
				m_nodeHandle,nshandle,false);
		}
		return null;
    }
    
    /**
     * Look up a prefix and get the currently-mapped Namespace URI.
     * in the local context.
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
			m_nodeHandle,false);
		while(nshandle!=DTM.NULL)
		{
			if(prefix.equals(m_dtm.getLocalName(nshandle))) // getPrefix?
				//return m_dtm.getNamespaceURI(nshandle);
				return m_dtm.getNodeValue(nshandle);
    		nshandle=m_dtm.getNextNamespaceNode(
				m_nodeHandle,nshandle,false);
		}
		return null;
    }
    
}

