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
package org.apache.xml.dtm.ref.xni2dtm;

import org.apache.xml.dtm.*;
import org.w3c.dom.*;

import org.apache.xml.utils.NodeConsumer;
import org.apache.xml.utils.XMLString;

import org.apache.xerces.xni.*;
import org.apache.xerces.xni.psvi.*;

/**
 * <meta name="usage" content="advanced"/>
 * This class does a pre-order walk of the DTM tree, calling an XNI
 * XMLDocumentHandler interface as it goes. As such, it's more like 
 * the Visitor design pattern than like the DOM's TreeWalker.
 */
public class DTM2XNI 
implements org.apache.xerces.xni.parser.XMLDocumentScanner
{
  /** Local reference to a XMLDocumentHandler          */
  private XMLDocumentHandler m_XMLDocumentHandler = null;

  /** Source for this TreeWalker          */
  protected DTM m_dtm;
  
  /** Source for this TreeWalker          */
  protected int m_root;
  
  /** Context to wrap around this TreeWalker */
  protected java.util.Vector m_context;
	
  /** Buffer for chars going out to the handler. Reusable. 
   * WARNING:  o..a..xerces.xni.XMLString and o..a..xml.utils.XNIString
   * are *NOT* the same thing! */
  protected org.apache.xerces.xni.XMLString m_xnistring=new org.apache.xerces.xni.XMLString();
  
  /** Buffer for augmentations. Reusable.
   * WARNING: XNI _will_ add values to this; be prepared to
   * clear and reinstantiate it every time.
   * */
  protected org.apache.xerces.xni.Augmentations m_augs=
  	new org.apache.xerces.util.AugmentationsImpl();
  
  /** Manafest constant: Augmentation flag for structure we added, which
   * may need to be stripped out again after validation */
  public static final String DTM2XNI_ADDED_STRUCTURE="Synthetic-Node-added-by-org.apache.xml.dtm.ref.xni2dtm.DTM2XNI";
  
  /** Manafest constant: XML Schema Instance namespace */
  static final String XSI_NAMESPACE="http://www.w3.org/2001/XMLSchema-instance";
  
  /**
   * Set the DTM to be traversed.
   * 
   * @param dtm  Document Table Model to be used.
   */
  public void setDTM(DTM dtm)
  {
    m_dtm = dtm;
  }

  /**
   * Get the XMLDocumentHandler used for the tree walk.
   *
   * @return the XMLDocumentHandler used for the tree walk
   */
  public XMLDocumentHandler getXMLDocumentHandler()
  {
    return m_XMLDocumentHandler;
  }
  
  /**
   * Set the XMLDocumentHandler used for the tree walk.
   *
   * @param ch the XMLDocumentHandler to be the result of the tree walk.
   */
  public void setXMLDocumentHandler(XMLDocumentHandler ch)
  {
    m_XMLDocumentHandler = ch;
  }

  
  /**
   * Constructor.
   * @param   XMLDocumentHandler The implemention of the
   * XMLDocumentHandler operation (toXMLString, digest, ...)
   */
  public DTM2XNI()
  {
  }
  
  /**
   * Constructor.
   * @param   XMLDocumentHandler The implemention of the
   * XMLDocumentHandler operation (toXMLString, digest, ...)
   */
  public DTM2XNI(XMLDocumentHandler handler, DTM dtm,int nodeHandle)
  {
  	setDocumentHandler(handler);
    setSource(dtm,nodeHandle);
  }
  
  /**
   * Constructor.
   * @param   XMLDocumentHandler The implemention of the
   * XMLDocumentHandler operation (toXMLString, digest, ...)
   */
  public DTM2XNI(DTM dtm,int nodeHandle)
  {
    setSource(dtm,nodeHandle);
  }
  /** Set XNI listener */
  public void setDocumentHandler(XMLDocumentHandler handler)
  {
    m_XMLDocumentHandler = handler;
  }
  
  /** Set source to read from */
  public void setSource(DTM dtm,int nodeHandle)
  {
  	m_dtm=dtm;
  	m_root=nodeHandle;
  }

  /** Set wrapper context -- a vector of QNames, outermost first */
  public void setContext(java.util.Vector context)
  {
  	m_context=context;
  }
  
  /** Perform a non-recursive pre-order/post-order traversal,
   * operating as a Visitor. startNode (preorder) and endNode
   * (postorder) are invoked for each node as we traverse over them,
   * with the result that the node is written out to m_XMLDocumentHandler.
   *
   * @param pos Nodehandle in the tree at which to start (and end) traversal --
   * in other words, the root of the subtree to traverse over.
   * @param context Vector of QNames to be synthesized as "wrapper" elements
   * around this tree. It is hoped that we can phase this out when Xerces
   * adds a context parameter to their validator.
   * These will be tagged with an XNI annotation indicating that status, so
   * downstream processing can strip them out again if desired.
   *
   * @throws TransformerException */
  public boolean scanDocument(boolean complete) throws org.apache.xerces.xni.XNIException
  {
  	try
  	{
  		
  	int pos=m_root;
  	int top=pos;

	// Document node wants to be marked as synthesized iff root wasn't
	// a Doc.
	Augmentations docaugs=null;
	if(m_dtm.getNodeType(top)==DTM.DOCUMENT_NODE)
	{
		docaugs=m_augs;	
		docaugs.clear();
		docaugs.putItem(DTM2XNI_ADDED_STRUCTURE,DTM2XNI_ADDED_STRUCTURE);
	}

    m_XMLDocumentHandler.startDocument(null, m_dtm.getDocumentEncoding(pos),
    	docaugs);

    // Generate the synthesized context
    if(m_context!=null)
    	for(int i=0, count=m_context.size();i<count;++i)
    	{
    		// Need to convert from Xalan QName to XNI QName.
			org.apache.xml.utils.QName q=(org.apache.xml.utils.QName) m_context.elementAt(i);
			org.apache.xerces.xni.QName qq=new org.apache.xerces.xni.QName(
				q.getPrefix(), q.getLocalPart(),q.toString(),q.getNamespaceURI());
			// Mark as synthesized ancestor nodes
			// NOTE: Must reassert every time; could have been altered!
			m_augs.clear();
			m_augs.putItem(DTM2XNI_ADDED_STRUCTURE,DTM2XNI_ADDED_STRUCTURE);
		    m_XMLDocumentHandler.startElement(qq,null,m_augs);
    	}

	// %REVIEW% This can undoubtedly be simplified.
    while (DTM.NULL != pos)
    {
      startNode(pos);
      int nextNode = m_dtm.getFirstChild(pos);
      while (DTM.NULL == nextNode)
      {
        endNode(pos);

        if ((DTM.NULL != top) && top == pos)
          break;

        nextNode = m_dtm.getNextSibling(pos);

        if (DTM.NULL == nextNode)
        {
          pos = m_dtm.getParent(pos);

          if ((DTM.NULL == pos) || ((DTM.NULL != top) && (top == pos)))
          {
            nextNode = DTM.NULL;

            break;
          }
        }
      }

      pos = nextNode;
    }
    
    // Generate the context
    if(m_context!=null)
    	for(int i=m_context.size()-1;i>=0;--i)
    	{
    		// Need to convert from Xalan QName to XNI QName.
			org.apache.xml.utils.QName q=(org.apache.xml.utils.QName) m_context.elementAt(i);
			org.apache.xerces.xni.QName qq=new org.apache.xerces.xni.QName(
				q.getPrefix(), q.getLocalPart(),q.toString(),q.getNamespaceURI());
			// Mark as synthesized ancestor nodes
			// NOTE: Must reassert every time; could have been altered!
			m_augs.clear();
			m_augs.putItem(DTM2XNI_ADDED_STRUCTURE,DTM2XNI_ADDED_STRUCTURE);
		    m_XMLDocumentHandler.endElement(qq,m_augs);
    	}

	// Document node needs to be marked as synthesized iff root wasn't
	// a Doc. Note that docaugs will be left null if this test didn't 
	// succeed earlier, which is what we want.
	if(m_dtm.getNodeType(top)==DTM.DOCUMENT_NODE)
	{
		docaugs.clear();
		docaugs.putItem(DTM2XNI_ADDED_STRUCTURE,DTM2XNI_ADDED_STRUCTURE);
	}
    m_XMLDocumentHandler.endDocument(docaugs);
   
    return true; 
    
  	}
  	catch(org.xml.sax.SAXException e)
  	{
  		throw new org.apache.xerces.xni.XNIException(e);
  	}
  }

  /** Flag indicating whether following text to be processed is raw text          */
  boolean nextIsRaw = false;
  
  /**
   * Partly optimized dispatch of characters from Text-style nodes.
   * 
   * I really didn't want to propigate XNI deep into our data structures.
   * That means pulling the data back here to process it.
   * Best I could do was look for lighter-weight ways to retrive it...
   * but I'm not really delighted with this solution.
   */
  private final void dispatchChars(int node)
     throws org.xml.sax.SAXException
  {
  	// Warning: getStringValue includes children, if this isn't a
  	// Text-type node. 
  	XMLString value=m_dtm.getStringValue(node);
  	if(value instanceof org.apache.xpath.objects.XString)
  	{
  		// Should this be moved up to the XMLString API?
  		// I don't think we _have_ any XMLStrings other than XStrings...
  		org.apache.xml.utils.CharacterBlockEnumeration e=
  			((org.apache.xpath.objects.XString)value).enumerateCharacterBlocks();
  			
  		do
  		{
			m_xnistring.setValues(
  					e.getChars(), e.getStart(), e.getLength());
  			m_XMLDocumentHandler.characters(m_xnistring,null);
  		}
  		while(e.nextElement()!=null);
  	}
  	else
  	{
  		// Fallback in case someone else implemented XMLString
  		// and might not support the enumeration call.
  		// Shouldn't arise, but I'm feeling paranoid this week.
  		//
  		// This may involve a lot of data recopying, which is why
  		// we prefer the enumeration -- with luck, that may be able
  		// to yield data directly from the underlying structures.

  		String s=value.toString();
  		char[] ch=s.toCharArray();
		m_xnistring.setValues(ch, 0, ch.length);
		m_XMLDocumentHandler.characters(m_xnistring,null);
  	}
  }

  /**
   * Start processing given node
   *
   *
   * @param node Node to process
   *
   * @throws org.xml.sax.SAXException
   */
  protected void startNode(int node) throws org.xml.sax.SAXException
  {
    switch (m_dtm.getNodeType(node))
    {
    case DTM.COMMENT_NODE :
    {
      XMLString data = m_dtm.getStringValue(node);

  		// This may involve a lot of data recopying, which is why
  		// we prefer the enumeration for character content...
  		// unfortunately, a single comment does need to be a single
  		// event. Might still want to try the enum first and only fall
  		// back on concatenation if that says multiple blocks exist...?
  		// %REVIEW% %OPT%
  		String s=data.toString();
  		char[] ch=s.toCharArray();
  		m_xnistring.setValues(ch, 0, ch.length);
		m_XMLDocumentHandler.comment(m_xnistring,null);
    }
    break;
    case DTM.DOCUMENT_FRAGMENT_NODE :
      // ??;
      break;
    case DTM.DOCUMENT_NODE :
      // already dealt with in traverse().
      break;
    case DTM.ELEMENT_NODE :
      org.apache.xerces.xni.XMLAttributes attrs = 
                            new org.apache.xerces.util.XMLAttributesImpl();

	  // Note that this needs to scan the namespaces declared ON THIS NODE,
	  // *not* the ones inherited.
      for (int nsn = m_dtm.getFirstNamespaceNode(node, false); DTM.NULL != nsn;
           nsn = m_dtm.getNextNamespaceNode(node, nsn, false))
      {
        String nsprefix = m_dtm.getLocalName(nsn); //xmlns:whatever
        	
        m_XMLDocumentHandler.startPrefixMapping(nsprefix, m_dtm.getNodeValue(nsn),null);

		// Also generate as an attr?
	    String ans = m_dtm.getNamespaceURI(nsn); // %REVIEW% Should always be NS NS.
        org.apache.xerces.xni.QName aqq=new org.apache.xerces.xni.QName(
				m_dtm.getPrefix(nsn), nsprefix,
				m_dtm.getNodeName(nsn),ans);
        int index=attrs.addAttribute(aqq, 
                           "CDATA", 
                           m_dtm.getNodeValue(nsn));
      }

      boolean xniTypeSeen=false;   
      for (int i = m_dtm.getFirstAttribute(node); 
           i != m_dtm.NULL; 
           i = m_dtm.getNextAttribute(i)) 
      {
        String aln=m_dtm.getLocalName(i);
	    String ans = m_dtm.getNamespaceURI(i);
        if(null == ans)
          ans = ""; // Safety net
        else if(XSI_NAMESPACE.equals(ans) && aln.equals("type"))
          xniTypeSeen=true; // No need to use pass-through assertion below
        org.apache.xerces.xni.QName aqq=new org.apache.xerces.xni.QName(
				m_dtm.getPrefix(i), aln,
				m_dtm.getNodeName(i),ans);
        int index=attrs.addAttribute(aqq, 
                           "CDATA", 
                           m_dtm.getNodeValue(i));
      }
      
      // The business of trying to force xni:type attributes
      // into the validation stream has been taken out of the
      // XSLT2 spec, at least for now. I'm happy; it was a mess,
      // even with the use of annotations to strip these out again.

      String ns = m_dtm.getNamespaceURI(node);
      if(null == ns)
        ns = "";
      org.apache.xerces.xni.QName qq=new org.apache.xerces.xni.QName(
				m_dtm.getPrefix(node), m_dtm.getLocalName(node),
				m_dtm.getNodeName(node),ns);
      m_XMLDocumentHandler.startElement(qq,attrs,null);
      break;
    case DTM.PROCESSING_INSTRUCTION_NODE :
    {
      String name = m_dtm.getNodeName(node);

      // String data = pi.getData();

   	  // No special handling needed here; we can let the xslt-next-is-raw
   	  // pass through directly rather than having to convert it. 
   	  // See discussion under TEXT_NODE.
   	  //
      // if (name.equals("xslt-next-is-raw"))
      // {
      //   nextIsRaw = true;
      // }
      // else
      {
  		// This may involve a lot of data recopying, which is why
  		// we prefer the enumeration for character content...
  		// unfortunately, a single comment does need to be a single
  		// event. Might still want to try the enum first and only fall
  		// back on concatenation if that says multiple blocks exist.
  		// %REVIEW% %OPT%
  		String s=m_dtm.getNodeValue(node);
  		char[] ch=s.toCharArray();
  		m_xnistring.setValues(ch, 0, ch.length);
        m_XMLDocumentHandler.processingInstruction(name,m_xnistring,null);
      }
    }
    break;
    case DTM.CDATA_SECTION_NODE :
    {
      m_XMLDocumentHandler.startCDATA(null);
      dispatchChars(node);
      m_XMLDocumentHandler.endCDATA(null);
    }
    break;
    case DTM.TEXT_NODE :
    {
   	  // No special handling needed here; we can let the xslt-next-is-raw
   	  // pass through directly rather than having to convert it to a
   	  // javax.xml.transform.Result.PI_DISABLE_OUTPUT_ESCAPING and
   	  // javax.xml.transform.Result.PI_ENABLE_OUTPUT_ESCAPING pair, since
   	  // (at this time) we do not intend to drive a serializer directly
   	  // from an XNI stream. That may change in the future, though.
      // if (nextIsRaw)
      // {
      //  nextIsRaw = false;
	  //  m_XMLDocumentHandler.processingInstruction(javax.xml.transform.Result.PI_DISABLE_OUTPUT_ESCAPING, "");
      //  dispatachChars(node);
      //  m_XMLDocumentHandler.processingInstruction(javax.xml.transform.Result.PI_ENABLE_OUTPUT_ESCAPING, "");
      // }
      // else
      {
        dispatchChars(node);
      }
    }
    break;
    case DTM.ENTITY_REFERENCE_NODE :
		break;      // Should never exist in a DTM.	
    default :
    }
  }

  /**
   * End processing of given node 
   *
   *
   * @param node Node we just finished processing
   *
   * @throws org.xml.sax.SAXException
   */
  protected void endNode(int node) throws org.xml.sax.SAXException
  {

    switch (m_dtm.getNodeType(node))
    {
    case DTM.DOCUMENT_NODE :
		// Dealt with in traverse()
      break;
    case DTM.ELEMENT_NODE :
      String ns = m_dtm.getNamespaceURI(node);
      if(null == ns)
        ns = "";
      org.apache.xerces.xni.QName qq=new org.apache.xerces.xni.QName(
				m_dtm.getPrefix(node), m_dtm.getLocalName(node),
				m_dtm.getNodeName(node),ns);
      m_XMLDocumentHandler.endElement(qq,null);

      for (int nsn = m_dtm.getFirstNamespaceNode(node, false); DTM.NULL != nsn;
           nsn = m_dtm.getNextNamespaceNode(node, nsn, false))
      {
        // String prefix = m_dtm.getPrefix(nsn);
        String prefix = m_dtm.getNodeNameX(nsn);
        m_augs.clear();
        m_augs.putItem(DTM2XNI_ADDED_STRUCTURE,DTM2XNI_ADDED_STRUCTURE);
        m_XMLDocumentHandler.endPrefixMapping(prefix,m_augs);
      }
      break;
    case DTM.CDATA_SECTION_NODE :
      break;
    case DTM.ENTITY_REFERENCE_NODE :
		break;      // Should never exist in a DTM.	
    default :
	    break;
    }
  }
  

	// Required for compliance with XNI XMLDocumentScanner API,
	// ignored here. I suppose we could use it to set systemID and such...
	public void setInputSource(org.apache.xerces.xni.parser.XMLInputSource inputSource)
                    throws java.io.IOException
	{
		// No-op.
	}
}  //DTM2XNI

