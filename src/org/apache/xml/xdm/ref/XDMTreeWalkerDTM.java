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

import org.apache.xml.xdm.XDMCursor;
import org.apache.xml.xdm.XDMNodeVisitor;
import org.apache.xml.xdm.XDMTreeWalker;
import org.apache.xml.dtm.DTM;

/** Implementation of the XDMTreeWalker for use with DTM.
 * 
 * As an implementation shortcut, this extends the Self Cursor
 * so it can pass itself to the Walker as the current location.
 * 
 * @author keshlam
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class XDMTreeWalkerDTM 
extends XDMSelfCursorDTM  
implements XDMTreeWalker 
{
	
	/** Construct an XDMTreeWalker wrapped around a DTM.
	 * @param rootNode Node to start at, expressed as a Cursor.
	 * */
	public XDMTreeWalkerDTM(XDMCursorDTM rootNode)
	{
		super(rootNode);
	}

	/** Reset the walker to a new root node.
	 * 
	 * %REVIEW% Unclear this method will ever be used.
	 * 
	 * @param rootNode Node to start at, expressed as an
	 * XDMCursorDTM.
	 * @return this
	 * @throw ClassCastException if another flavor of XDMCursor
	 * is passed in. (Should never happen.)
	 * @see org.apache.xml.xdm.XDMTreeWalker#setRootNode(XDMCursor)
	 */
	public XDMTreeWalker setRootNode(XDMCursor rootNode) {
		XDMCursorDTM rn=(XDMCursorDTM)rootNode;
		m_currentDTM=rn.getCurrentDTM();
		m_currentHandle=rn.getCurrentHandle();
		return this;
	}

	/** Basic iterative tree-walk-and-invoke-visitor, applied
	 * against the DTM APIs.
	 * 
	 * %REVIEW% Is it worth making this Synchronized, just
	 * for paranoia's sake? We don't _intend_ it to be invoked
	 * reentrantly, but...
	 * 
	 * @see org.apache.xml.xdm.XDMTreeWalker#walk(XDMNodeVisitor)
	 */
	public void walk(XDMNodeVisitor behavior) {
		int top=m_currentHandle;

	    while (DTM.NULL != m_currentHandle)
   		{
			startNode(behavior);
   	   		int nextNode = m_currentDTM.getFirstChild(m_currentHandle);
			while (DTM.NULL == nextNode)
      		{
        		endNode(behavior);

        		if (top == m_currentHandle)
          		break;

        		nextNode = m_currentDTM.getNextSibling(m_currentHandle);

        		if (DTM.NULL == nextNode)
        		{
          			m_currentHandle = m_currentDTM.getParent(m_currentHandle);

          			if ((DTM.NULL == m_currentHandle) || (top == m_currentHandle))
          			{
            			if (DTM.NULL != m_currentHandle)
              			endNode(behavior);

            			nextNode = DTM.NULL;

            			break;
          			}
        		}
      		}

      		m_currentHandle = nextNode;
    	}
    	
    	// Reset, permitting reuse:
    	m_currentHandle = top;
	}
	
	/** %REVIEW% Hmmm. We could push this out and make it 
	 * behavior.startNode(this)... but testing type is a tiny
	 * bit more efficient here, and arguably more convenient.
	 * On the other other hand, this makes sharing behaviors in 
	 * the handler more difficult.
	 * */
	void startNode(XDMNodeVisitor behavior)
	{
	    switch (m_currentDTM.getNodeType(m_currentHandle))
    	{
	    case DTM.COMMENT_NODE :
	    	behavior.comment(this);
	    	break;
	    case DTM.DOCUMENT_NODE :
	    case DTM.DOCUMENT_FRAGMENT_NODE :
	    	// Should these be separated? Walker can test...
	    	behavior.startDocument( this);
	    	break;

	    case DTM.ELEMENT_NODE :
	    	behavior.startElement(this);
	    	
	    	// %REVIEW% NS/NSdecl nodes *not* passed...?
	    	// (See attrs for illustration of one approach.)
	    	
	    	// %REVIEW% Do we need this feature?
	    	// %OPT% Even if so, we don't need to test every time...
	    	if(behavior.wantsAttributeEvents())
	    	{
	    		int element=m_currentHandle;
	    		for(m_currentHandle=m_currentDTM.getFirstAttribute( m_currentHandle);
	    			m_currentHandle!=DTM.NULL;
					m_currentHandle=m_currentDTM.getNextAttribute( m_currentHandle))
				{
					startNode(behavior);
		    	}
		    	m_currentHandle=element;
	    	}
	    	break;

	    case DTM.ATTRIBUTE_NODE :
	    	behavior.attribute(this);
	    	break;
    	case DTM.PROCESSING_INSTRUCTION_NODE :
    		behavior.processingInstruction( this);
    		break;
	    case DTM.TEXT_NODE :
	    	behavior.text( this);
	    	break;
	    case DTM.CDATA_SECTION_NODE : // Don't actually exist in DTM
	    case DTM.ENTITY_REFERENCE_NODE :
	    default :
	    	break;
	    }
    }
    
	/** %REVIEW% Hmmm. We could push this out and make it 
	 * behavior.endNode(this)... but testing type is a tiny
	 * bit more efficient here, and arguably more convenient.
	 * On the other other hand, this makes sharing behaviors in 
	 * the handler more difficult.
	 * */
	void endNode(XDMNodeVisitor behavior)
	{
	    switch (m_currentDTM.getNodeType(m_currentHandle))
    	{
	    case DTM.DOCUMENT_NODE :
	    case DTM.DOCUMENT_FRAGMENT_NODE :
	    	// Should these be separated? Walker can test...
	    	behavior.endDocument( this);
	    	break;

	    case DTM.ELEMENT_NODE :
	    	behavior.endElement(this);
	    	break;

		case DTM.ATTRIBUTE_NODE : 				// No end event
    	case DTM.PROCESSING_INSTRUCTION_NODE :	// No end event
	    case DTM.TEXT_NODE :					// No end event
	    case DTM.CDATA_SECTION_NODE :			// Don't actually exist in DTM
	    case DTM.ENTITY_REFERENCE_NODE :		// Don't actually exist in DTM
	    default :
	    	break;
	    }
    }	
    
}
