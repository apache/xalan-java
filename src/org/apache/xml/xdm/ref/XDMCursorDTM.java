/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights 
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

package org.apache.xml.xdm.ref;

import javax.xml.transform.SourceLocator;

import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.apache.xml.xdm.XDMCursor;
import org.apache.xml.xdm.Axis;
import org.apache.xml.xdm.XDMSequence;
import org.apache.xml.xdm.XDMTreeWalker;
import org.apache.xml.xdm.XDMException;
import org.apache.xml.utils.NodeVector;
import org.apache.xml.utils.XMLString;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMManager;
import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xpath.res.XPATHErrorResources;
import org.apache.xalan.res.XSLMessages;

/**
 * @author keshlam
 */
public abstract class XDMCursorDTM implements XDMCursor,Cloneable {
	
	// Current-node references
	protected DTM m_currentDTM;
	protected int m_startHandle; // %REVIEW% Long-term, replace with Identity
	protected int m_currentHandle; // %REVIEW% Long-term, replace with Identity

	/**
	 * Temporary Constructor for XDMCursorDTM. Should be phased
	 * out when handles are phased out, in favor of a low-level
	 * DTM Node Identifier.
	 * 
	 * @param dtmManager DTMManager containing the DTM.
	 * @param nodeHandle handle of node. 
	 */
	public XDMCursorDTM(DTMManager dtmManager,int nodeHandle) {
		super();
		m_currentDTM=dtmManager.getDTM(nodeHandle);
		m_startHandle=nodeHandle;
	}

	/**
	 * Temporary Constructor for XDMCursorDTM. Should be phased
	 * out when handles are phased out, in favor of a low-level
	 * DTM Node Identifier.
	 * 
	 * @param dtmManager DTMManager containing the DTM.
	 * @param nodeHandle handle of node. 
	 */
	public XDMCursorDTM(DTM dtm,int nodeHandle) {
		super();
		m_currentDTM=dtm;
		m_startHandle=nodeHandle;
	}

	/**
	 * Internal constructor. Set the new cursor
	 * to start at this cursor's current node.
	 * 
	 * @param dtmManager DTMManager containing the DTM.
	 * @param nodeHandle handle of node. 
	 */
	public XDMCursorDTM(XDMCursorDTM other) {
		super();
		m_currentDTM=other.m_currentDTM;;
		m_startHandle=other.m_currentHandle;
	}
	
	/**
	 * @see org.apache.xml.xdm.XDMCursor#isEmpty()
	 */
	abstract public boolean isEmpty();
	

	/**
	 * @see org.apache.xml.xdm.XDMCursor#getAxisCursor(XDMCursor, int)
	 */
	public XDMCursor getAxisCursor(int axis) {
    	XDMCursorDTM cursor = null;

	    switch (axis)
    	{
	    case Axis.SELF :
    	  cursor = new XDMSelfCursorDTM(this);
	      break;
    	case Axis.CHILD :
	    case Axis.PARENT :
	    case Axis.ANCESTOR :
	    case Axis.ANCESTORORSELF :
	    case Axis.ATTRIBUTE :
	    case Axis.DESCENDANT :
    	case Axis.DESCENDANTORSELF :
	    case Axis.FOLLOWING :
    	case Axis.PRECEDING :
	    case Axis.FOLLOWINGSIBLING :
    	case Axis.PRECEDINGSIBLING :
	    case Axis.NAMESPACE :
    	case Axis.ROOT :
    	  // Extended-type parameter will be ignored
	      cursor = new XDMTraverserCursorDTM(this,axis,0);
    	  break;
	    default :
    	  throw new XDMException(XSLMessages.createMessage(XSLTErrorResources.ER_ITERATOR_AXIS_NOT_IMPLEMENTED, new Object[]{Axis.names[axis]})); //"Error: iterator for axis '" + Axis.names[axis]
	                             //+ "' not implemented");
    	}

	    return (cursor); // May be empty, of course.
	}

	/**
	 * @see org.apache.xml.xdm.XDMCursor#getTypedAxisCursor(XDMCursor, int, int)
	 */
	public XDMCursor getTypedAxisCursor(int axis,int type) {
    	XDMCursorDTM cursor = null;

	    switch (axis)
    	{
	    case Axis.SELF :
	      if(m_currentDTM.getExpandedTypeID(m_currentHandle)==type)
    	  	cursor = new XDMSelfCursorDTM(this);
    	  // Else no node; return null?
	      break;
    	case Axis.CHILD :
	    case Axis.PARENT :
	    case Axis.ANCESTOR :
	    case Axis.ANCESTORORSELF :
	    case Axis.ATTRIBUTE :
	    case Axis.DESCENDANT :
    	case Axis.DESCENDANTORSELF :
	    case Axis.FOLLOWING :
    	case Axis.PRECEDING :
	    case Axis.FOLLOWINGSIBLING :
    	case Axis.PRECEDINGSIBLING :
	    case Axis.NAMESPACE :
    	case Axis.ROOT :
    	  // Extended-type parameter will be ignored
	      cursor = new XDMTraverserCursorDTM(this,axis,type);
    	  break;
	    default :
    	  throw new XDMException(XSLMessages.createMessage(XSLTErrorResources.ER_ITERATOR_AXIS_NOT_IMPLEMENTED, new Object[]{Axis.names[axis]})); //"Error: iterator for axis '" + Axis.names[axis]
	                             //+ "' not implemented");
    	}

	    return (cursor);
	}

	/**
	 * @see org.apache.xml.xdm.XDMCursor#cloneXDMCursor()
	 */
	abstract public XDMCursor cloneXDMCursor();

	/** When subclassing this, remember to deep-clone any 
	 * iterator state information!
	 * 
	 * @see org.apache.xml.xdm.XDMCursor#clone()
	 */
	public Object clone() {
		try
		{
			return super.clone();
		} catch(CloneNotSupportedException e)
		{
			throw new InternalError(e.toString());
		}
	}

	/**
	 * @see org.apache.xml.xdm.XDMCursor#singleNode()
	 */
	public XDMCursor singleNode() {
		return new XDMSelfCursorDTM(m_currentDTM,m_currentHandle);
	}

	/**
	 * @see org.apache.xml.xdm.XDMCursor#hasChildNodes()
	 */
	public boolean hasChildNodes() {
		return m_currentDTM.hasChildNodes(m_currentHandle);
	}

	/**
	 * @see org.apache.xml.xdm.XDMCursor#getAttributeNode(String, String)
	 */
	public XDMCursor getAttributeNode(String namespaceURI, String name) {
		return new XDMSelfCursorDTM(m_currentDTM,
			m_currentDTM.getAttributeNode(m_currentHandle,namespaceURI,name));
	}

	/**
	 * @see org.apache.xml.xdm.XDMCursor#getDocumentRoot()
	 */
	public XDMCursor getDocumentRoot() {
		return new XDMSelfCursorDTM(m_currentDTM,
			m_currentDTM.getDocumentRoot(m_currentHandle));
	}

	/**
	 * @see org.apache.xml.xdm.XDMCursor#getIterationRoot()
	 */
	abstract public XDMCursor getIterationRoot();

	/**
	 * @see org.apache.xml.xdm.XDMCursor#setIterationRoot(XDMCursor, Object)
	 */
	abstract public XDMCursor setIterationRoot(XDMCursor root, Object environment);

	/**
	 * @see org.apache.xml.xdm.XDMCursor#resetIteration()
	 */
	abstract public void resetIteration();

	/**
	 * @see org.apache.xml.xdm.XDMCursor#nextNode()
	 */
	abstract public boolean nextNode();

	/**
	 * @see org.apache.xml.xdm.XDMCursor#previousNode()
	 */
	//abstract public XDMCursor previousNode();

	/** Release any resources used by this cursor, and
	 * -- possibly -- return it to a reuse pool.
	 * 
	 * We used pooling in LocPathIterators. And XRTreeFrag,
	 * which implemented DTMIterator, used this as a hint 
	 * that it was now time to discard the Temporary/ResultFragment
	 * DTM. It appears to have been a no-op for other iterators.
	 * 
	 * @see org.apache.xml.xdm.XDMCursor#detachIteration()
	 * @see org.apache.xml.dtm.DTMIterator#detach()
	 */
	abstract public void detach();

	/** %REVIEW% Are we pooling these?
	 * 
	 * @see org.apache.xml.xdm.XDMCursor#allowDetachToRelease(boolean)
	 */
	abstract public void allowDetachToRelease(boolean allowRelease);

	/**
	 * @see org.apache.xml.xdm.XDMCursor#setShouldCacheNodes(boolean)
	 */
	abstract public void setShouldCacheNodes();

	/** Unsupported in XDMCursorDTM derivatives.
	 * @see org.apache.xml.xdm.XDMCursor#isMutable()
	 */
	public boolean isMutable()
	{ return false; }

	/**
	 * @see org.apache.xml.xdm.XDMCursor#getCurrentPos()
	 */
	abstract public int getCurrentPos();

	/**
	 * @see org.apache.xml.xdm.XDMCursor#setCurrentPos(int)
	 */
	abstract public boolean setCurrentPos(int i);

	/**
	 * @see org.apache.xml.xdm.XDMCursor#item(int)
	 */
	abstract public XDMCursor item(int index);

	/** Unsupported in XDMCursorDTM derivities.
	 * 
	 * %REVIEW% Shouldn't assertion be centrally defined somewhere?
	 * 
	 * @see org.apache.xml.xdm.XDMCursor#setItem(int, int)
	 */
	public void setItem(int node, int index) {
		assertion(false, "setItem not supported by this iterator!");
	}
	
  /**
   * Tell the user of an assertion error, and probably throw an
   * exception.
   * 
   * %REVIEW% Shouldn't this be centrally defined somewhere --
   * eg, in XPATHErrorResources? And do we really want to pay
   * for a call-and-return, just to get the convenience of not
   * having to write an if?
   *
   * @param b  If false, a runtime exception will be thrown.
   * @param msg The assertion message, which should be informative.
   *
   * @throws RuntimeException if the b argument is false.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public void assertion(boolean b, java.lang.String msg)
  {
    if (!b)
    {
      java.lang.String fMsg = XSLMessages.createXPATHMessage(
        XPATHErrorResources.ER_INCORRECT_PROGRAMMER_ASSERTION,
        new Object[]{ msg });

      RuntimeException a = new RuntimeException(fMsg);
      a.printStackTrace();
      throw a;
    }
  }
	

	/**
	 * @see org.apache.xml.xdm.XDMCursor#getLength()
	 */
	abstract public int getLength();

	/**
	 * @see org.apache.xml.xdm.XDMCursor#cloneWithReset()
	 */
	abstract public XDMCursor cloneWithReset() throws CloneNotSupportedException;

	/**
	 * @see org.apache.xml.xdm.XDMCursor#isDocOrdered()
	 */
	abstract public boolean isDocOrdered();

	/**
	 * @see org.apache.xml.xdm.XDMCursor#getAxis()
	 */
	abstract public int getAxis();

	/**
	 * @see org.apache.xml.xdm.XDMCursor#getStringValue()
	 */
	public XMLString getStringValue() {
		return m_currentDTM.getStringValue(m_currentHandle);
	}

	/**
	 * @see org.apache.xml.xdm.XDMCursor#getStringValueChunkCount()
	 */
	public int getStringValueChunkCount() {
		return m_currentDTM.getStringValueChunkCount(m_currentHandle);
	}

	/**
	 * @see org.apache.xml.xdm.XDMCursor#getStringValueChunk(int, int, int[])
	 */
	public char[] getStringValueChunk(
		int chunkIndex,
		int[] startAndLen) {
		return m_currentDTM.getStringValueChunk(m_currentHandle,chunkIndex,startAndLen);
	}

	/**
	 * @see org.apache.xml.xdm.XDMCursor#getExpandedTypeID()
	 */
	public int getExpandedTypeID() {
		return m_currentDTM.getExpandedTypeID(m_currentHandle);
	}

	/**
	 * @see org.apache.xml.xdm.XDMCursor#getExpandedTypeID(String, String, int)
	 */
	public int getExpandedTypeID(
		String namespace,String localName,int type) {
		return m_currentDTM.getExpandedTypeID(namespace,localName,type);
	}

	/**
	 * @see org.apache.xml.xdm.XDMCursor#getLocalNameFromExpandedNameID(int)
	 */
	public String getLocalNameFromExpandedNameID(int expandedNameID) {
		return m_currentDTM.getLocalNameFromExpandedNameID(expandedNameID);
	}

	/**
	 * @see org.apache.xml.xdm.XDMCursor#getNamespaceFromExpandedNameID(int)
	 */
	public String getNamespaceFromExpandedNameID(int expandedNameID) {
		return m_currentDTM.getNamespaceFromExpandedNameID(expandedNameID);
	}

	/**
	 * @see org.apache.xml.xdm.XDMCursor#getNodeName()
	 */
	public String getNodeName() {
		return m_currentDTM.getNodeName(m_currentHandle);
	}

	/**
	 * @see org.apache.xml.xdm.XDMCursor#getNodeNameX()
	 */
	public String getNodeNameX() {
		return m_currentDTM.getNodeNameX(m_currentHandle);
	}

	/**
	 * @see org.apache.xml.xdm.XDMCursor#getLocalName()
	 */
	public String getLocalName() {
		return m_currentDTM.getLocalName(m_currentHandle);
	}

	/**
	 * @see org.apache.xml.xdm.XDMCursor#getPrefix()
	 */
	public String getPrefix() {
		return m_currentDTM.getPrefix(m_currentHandle);
	}

	/**
	 * @see org.apache.xml.xdm.XDMCursor#getNamespaceURI()
	 */
	public String getNamespaceURI() {
		return m_currentDTM.getNamespaceURI(m_currentHandle);
	}

	/**
	 * @see org.apache.xml.xdm.XDMCursor#getNodeValue()
	 */
	public String getNodeValue() {
		return m_currentDTM.getNodeValue(m_currentHandle);
	}

	/**
	 * @see org.apache.xml.xdm.XDMCursor#getNodeType()
	 */
	public int getNodeType() {
		return m_currentDTM.getNodeType(m_currentHandle);
	}

	/**
	 * @see org.apache.xml.xdm.XDMCursor#isSupported(String, String)
	 */
	public boolean isSupported(String feature, String version) {
		return m_currentDTM.isSupported(feature,version);
	}

	/**
	 * @see org.apache.xml.xdm.XDMCursor#getDocumentBaseURI()
	 */
	public String getDocumentBaseURI() {
		return m_currentDTM.getDocumentBaseURI();
	}

	/**
	 * @see org.apache.xml.xdm.XDMCursor#setDocumentBaseURI(String)
	 */
	public void setDocumentBaseURI(String baseURI) {
		m_currentDTM.setDocumentBaseURI(baseURI);
	}

	/**
	 * @see org.apache.xml.xdm.XDMCursor#getDocumentSystemIdentifier()
	 */
	public String getDocumentSystemIdentifier() {
		return m_currentDTM.getDocumentSystemIdentifier(m_currentHandle);
	}

	/**
	 * @see org.apache.xml.xdm.XDMCursor#getDocumentEncoding()
	 */
	public String getDocumentEncoding() {
		return m_currentDTM.getDocumentEncoding(m_currentHandle);
	}

	/**
	 * @see org.apache.xml.xdm.XDMCursor#getDocumentStandalone()
	 */
	public String getDocumentStandalone() {
		return m_currentDTM.getDocumentStandalone(m_currentHandle);
	}

	/**
	 * @see org.apache.xml.xdm.XDMCursor#getDocumentVersion()
	 */
	public String getDocumentVersion() {
		return m_currentDTM.getDocumentVersion(m_currentHandle);
	}

	/**
	 * @see org.apache.xml.xdm.XDMCursor#getDocumentAllDeclarationsProcessed()
	 */
	public boolean getDocumentAllDeclarationsProcessed() {
		return m_currentDTM.getDocumentAllDeclarationsProcessed();
	}

	/**
	 * @see org.apache.xml.xdm.XDMCursor#getDocumentTypeDeclarationSystemIdentifier()
	 */
	public String getDocumentTypeDeclarationSystemIdentifier() {
		return m_currentDTM.getDocumentTypeDeclarationSystemIdentifier();
	}

	/**
	 * @see org.apache.xml.xdm.XDMCursor#getDocumentTypeDeclarationPublicIdentifier()
	 */
	public String getDocumentTypeDeclarationPublicIdentifier() {
		return m_currentDTM.getDocumentTypeDeclarationPublicIdentifier();
	}

	/**
	 * @see org.apache.xml.xdm.XDMCursor#getElementById(String)
	 */
	public XDMCursor getElementById(String elementId) {
		int nodeHandle=m_currentDTM.getElementById(elementId);
		return new XDMSelfCursorDTM(m_currentDTM,nodeHandle);
	}

	/**
	 * @see org.apache.xml.xdm.XDMCursor#getElementByIdref(String)
	 */
	public NodeVector getElementByIdref(String elementIdref) {
		return m_currentDTM.getElementByIdref(elementIdref);
	}

	/**
	 * @see org.apache.xml.xdm.XDMCursor#getUnparsedEntityURI(String)
	 */
	public String getUnparsedEntityURI(String name) {
		return m_currentDTM.getUnparsedEntityURI(name);
	}

	/**
	 * @see org.apache.xml.xdm.XDMCursor#supportsPreStripping()
	 */
	public boolean supportsPreStripping() {
		return m_currentDTM.supportsPreStripping();
	}

	/**
	 * @see org.apache.xml.xdm.XDMCursor#isSameNode(XDMCursor)
	 */
	public boolean isSameNode(XDMCursor otherCursor) {
		return 
			(otherCursor instanceof XDMCursorDTM)
			? (m_currentHandle ==
				((XDMCursorDTM)otherCursor).m_currentHandle)
			: false; // %REVIEW% Is that the unknown state?
	}

	/**
	 * @see org.apache.xml.xdm.XDMCursor#isAfter(XDMCursor)
	 */
	public boolean isAfter(XDMCursor otherCursor) {
		// CHECK PHASE! Do we need to reverse, or want isBefore?
		return 
			(otherCursor instanceof XDMCursorDTM)
			? m_currentDTM.isNodeAfter(m_currentHandle,
				((XDMCursorDTM)otherCursor).getCurrentHandle())
			: false; // %REVIEW% Is that the unknown state?				
	}

	/**
	 * @see org.apache.xml.xdm.XDMCursor#isWhitespaceInElementContent()
	 */
	public boolean isWhitespaceInElementContent() {
		return m_currentDTM.isCharacterElementContentWhitespace(
			m_currentHandle);
	}

	/**
	 * @see org.apache.xml.xdm.XDMCursor#isDocumentAllDeclarationsProcessed()
	 */
	public boolean isDocumentAllDeclarationsProcessed() {
		return m_currentDTM.isDocumentAllDeclarationsProcessed(m_currentHandle);
	}

	/**
	 * @see org.apache.xml.xdm.XDMCursor#isAttributeSpecified()
	 */
	public boolean isAttributeSpecified() {
		return m_currentDTM.isAttributeSpecified(m_currentHandle);
	}

	/**
	 * @see org.apache.xml.xdm.XDMCursor#dispatchCharactersEvents(ContentHandler, boolean)
	 */
	public void dispatchCharactersEvents(ContentHandler ch, boolean normalize)
		throws SAXException {
		m_currentDTM.dispatchCharactersEvents(m_currentHandle,ch,normalize);
	}

	/**
	 * @see org.apache.xml.xdm.XDMCursor#dispatchToEvents(ContentHandler)
	 */
	public void dispatchToEvents(ContentHandler ch) throws SAXException {
		m_currentDTM.dispatchToEvents(m_currentHandle,ch);
	}

	/**
	 * @see org.apache.xml.xdm.XDMCursor#getNode()
	 */
	public Node getNode() {
		return m_currentDTM.getNode(m_currentHandle);
	}

	/**
	 * @see org.apache.xml.xdm.XDMCursor#getXDMCursorFromNode(Node)
	 */
	public XDMCursor getXDMCursorFromNode(Node node) {
		int nodeHandle=m_currentDTM.getDTMHandleFromNode(node);
		return new XDMSelfCursorDTM(m_currentDTM.getManager(),nodeHandle);
	}

	/**
	 * @see org.apache.xml.xdm.XDMCursor#getSourceLocatorFor()
	 */
	public SourceLocator getSourceLocator() {
		return m_currentDTM.getSourceLocatorFor(m_currentHandle);
	}

	/**
	 * @see org.apache.xml.xdm.XDMCursor#getSchemaTypeName()
	 */
	public String getSchemaTypeName() {
		return m_currentDTM.getSchemaTypeName(m_currentHandle);
	}

	/**
	 * @see org.apache.xml.xdm.XDMCursor#getSchemaTypeNamespace()
	 */
	public String getSchemaTypeNamespace() {
		return m_currentDTM.getSchemaTypeNamespace(m_currentHandle);
	}

	/**
	 * @see org.apache.xml.xdm.XDMCursor#getSchemaTypeLocalName()
	 */
	public String getSchemaTypeLocalName() {
		return m_currentDTM.getSchemaTypeLocalName(m_currentHandle);
	}

	/**
	 * @see org.apache.xml.xdm.XDMCursor#isNodeSchemaType(String, String)
	 */
	public boolean isNodeSchemaType(String namespace, String localname) {
		return m_currentDTM.isNodeSchemaType(m_currentHandle,namespace,localname);
	}

	/**
	 * @see org.apache.xml.xdm.XDMCursor#getTypedValue()
	 */
	public XDMSequence getTypedValue() {
		return m_currentDTM.getTypedValue(m_currentHandle);
	}

	/**
	 * @see org.apache.xml.xdm.XDMCursor#getTreeWalker()
	 */
	public XDMTreeWalker getTreeWalker() {
		return new XDMTreeWalkerDTM(this);
	}

	/** Unique to XDTMCursorDTM derivitives, FOR INTERNAL USE ONLY
	 * 
	 * @return the DTM within which the current node is located.
	 * */
	DTM getCurrentDTM()
	{
		return m_currentDTM;
	}

	/** Unique to XDTMCursorDTM derivitives, FOR INTERNAL USE ONLY
	 * 
	 * @return the DTM handle of the current node is located.
	 * %REVIEW% Ought to become node IDENTIFIER ASAP.
	 * */
	public int getCurrentHandle()
	{
		return m_currentHandle;
	}
	
	/** Debugging hook: Display the Current Node. Note that
	 * this is not intended to be used to serialize the node as
	 * XML; it's just so we can see what it represents during
	 * code development.
	 * 
     * %REVIEW% Add namespace info, type info, ... Check what
     * we were doing with DTM node printout, move that here.
	 * */
	public String toString()
	{
		// Formatting hack -- suppress quotes when value is null, to distinguish
        // it from "null".
        String value=getNodeValue();
        String vq = (value==null) ? "" : "\"";
		
    	return new StringBuffer("[")
    		//.append(nodeHandle).append(": ")
    		.append(TYPENAME[getNodeType()]).append(' ')
    		.append(getNodeName()).append(' ')
    		.append("\tValue=").append(vq).append(value).append(vq)
    		.append(']')
    		.toString();
	}
}
