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
package org.apache.xalan.stree;

import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;

import org.xml.sax.ContentHandler;

/**
 * <meta name="usage" content="internal"/>
 * Class to hold information about ProcessingInstruction node
 */
public class ProcessingInstructionImpl extends Child
        implements ProcessingInstruction
{

  /** PI's target (see getTarget for more info          */
  private String m_name;

  /** PI's content         */
  private String m_data;

  /**
   * Implement the processingInstruction event.
   *
   * @param doc Document object
   * @param target PI's target
   * @param data PI's content
   */
  ProcessingInstructionImpl(DocumentImpl doc, String target, String data)
  {

    super(doc);

    m_name = target;
    m_data = data;
  }

  /**
   * Returns the local part of the qualified name of this node.
   * <br>For nodes created with a DOM Level 1 method, such as
   * <code>createElement</code> from the <code>Document</code> interface,
   * it is <code>null</code>.
   * @since DOM Level 2
   *
   * @return This PI's target 
   */
  public String getLocalName()
  {
    return m_name;
  }

  /**
   * Get the PI name.
   * Note that getNodeName is aliased to getTarget. 
   *
   * @return This PI's target
   */
  public String getNodeName()
  {
    return m_name;
  }

  /**
   * A short integer indicating what type of node this is. The named
   * constants for this value are defined in the org.w3c.dom.Node interface.
   *
   * @return PROCESSING_INSTRUCTION_NODE node type
   */
  public short getNodeType()
  {
    return Node.PROCESSING_INSTRUCTION_NODE;
  }

  /**
   * A PI's "target" states what processor channel the PI's data
   * should be directed to. It is defined differently in HTML and XML.
   * <p>
   * In XML, a PI's "target" is the first (whitespace-delimited) token
   * following the "<?" token that begins the PI.
   * <p>
   * In HTML, target is always null.
   * <p>
   * Note that getNodeName is aliased to getTarget.
   *
   * @return This PI's target
   */
  public String getTarget()
  {
    return m_name;
  }  // getTarget():String

  /**
   * The content of this processing instruction. This is from the first non
   * white space character after the target to the character immediately
   * preceding the <code>?&gt;</code>.
   *
   * @return This PI's data
   * @throws DOMException
   *   NO_MODIFICATION_ALLOWED_ERR: Raised when the node is readonly.
   */
  public String getData()
  {
    return m_data;
  }

  /**
   * Same as getData 
   *
   *
   * @return This PI's data
   */
  public String getNodeValue()
  {
    return m_data;
  }
  
  /**
   * Handle a Characters event 
   *
   *
   * @param ch Content handler to handle SAX events
   *
   * @throws SAXException if the content handler characters event throws a SAXException.
   */
  public void dispatchCharactersEvent(ContentHandler ch) 
    throws org.xml.sax.SAXException
  {
    ch.characters(m_data.toCharArray(), 0, m_data.length());
  }

}
