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

/** Abstract superclass for generated template classes produced
 * by the CompilingStylesheetProcessor/CompilingStylesheetHandler.
 * (This had been an interface, but I want to factor out some 
 * "boilerplate" code rather than regenerating it each time.)
 */
package org.apache.xalan.processor;
import org.apache.xalan.templates.ElemTemplate;

import javax.xml.transform.SourceLocator;
import org.apache.xml.utils.SAXSourceLocator;

public abstract class CompiledTemplate
extends ElemTemplate 
implements java.io.Serializable
{
  /**
   * Object[] m_interpretArray is used to bind to nodes we don't yet
  * know how to compile. Set at construction.
  * This array resembles the DOM's getChildren().item(), but includes
  * some things that aren't children, and is our primary access
  * point for its contents even when they are kids.
  * @serial
  */
  protected java.lang.Object[] m_interpretArray;

  // Namespace context tracking. Note that this is dynamic state
  // during execution, _NOT_ the static state tied to a single 
  // ElemTemplateElement during parsing. Also note that it needs to
  // be set in execute() but testable in getNamespaceForPrefix --
  // and the latter, most unfortunately, is not passed the xctxt so
  // making that threadsafe is a bit ugly. 
  transient protected java.util.Hashtable m_nsThreadContexts=new java.util.Hashtable();

  /**
   * Tell if this template is a compiled template.
   */
  public boolean isCompiledTemplate()
  {
    return true;
  }

  /** Accessor to let interpretive children query current namespace state.
   * Note that unlike the interpreted version, the namespace state of this
   * code changes over time... and that we need to maintain a unique state
   * for each thread if we're to support multitasking.
   */
  public String getNamespaceForPrefix(String nsPrefix)
  {
    String nsuri="";
    org.xml.sax.helpers.NamespaceSupport nsSupport=
      (org.xml.sax.helpers.NamespaceSupport)
      m_nsThreadContexts.get(Thread.currentThread());
    if(null!=nsSupport)
      nsuri=nsSupport.getURI(nsPrefix);
    if(null==nsuri || nsuri.length()==0)
      nsuri=m_parentNode.getNamespaceForPrefix(nsPrefix);
    return nsuri;
  }


  /** public constructor: Copy values from original
   * template object, pick up "uncompiled children"
   * array from compilation/instantiation process,
   * and set the Locator information explicitly.
   * (I want the locator to be visible as literals
   * in the generated code, for debugging purposes.)
   */
  public CompiledTemplate(ElemTemplate original,
                          int lineNumber, int columnNumber,
                          String publicId,String systemId,
                          java.lang.Object[] interpretArray)
  {
    SAXSourceLocator locator = new SAXSourceLocator();
    locator.setLineNumber(lineNumber);
    locator.setColumnNumber(columnNumber);
    locator.setPublicId(publicId);
    locator.setSystemId(systemId);
    setLocaterInfo(locator); // yes, there's an or/er clash
    
    // uncompiled descendents/children
    this.m_interpretArray=interpretArray;

    // other context values which seem to be needed
    setMatch(original.getMatch());
    setMode(original.getMode());
    setName(original.getName());
    setPriority(original.getPriority());
    setStylesheet(original.getStylesheet());

    // Reparent the interpreted ElemTemplateElements,
    // to break their dependency on interpretive code.
    // AVTs in this array don't need proceessing, as they are
    // apparently not directly aware of their Elements.

    for(int i=0;i<m_interpretArray.length;++i)
      {
        if(m_interpretArray[i] instanceof org.apache.xalan.templates.ElemTemplateElement)
          {
            org.apache.xalan.templates.ElemTemplateElement ete=
              (org.apache.xalan.templates.ElemTemplateElement)
              m_interpretArray[i];
            
            // Append alone is not enough; it's lightweight, and assumes
            // the child had no previous parent. Need to remove first.
            // (We know that there _was_ a previous parent, of course!)
            appendChild(ete.getParentElem().removeChild(ete));
          }
      }
  } // Constructor initialization ends
  
  
  /** Main entry point for the Template transformation.
   * It's abstract in CompiledTemplate, but is filled in
   * when the actual template code is synthesized.
   */
  public abstract void execute(
                          org.apache.xalan.transformer.TransformerImpl transformer,
                      org.w3c.dom.Node sourceNode,
                      org.apache.xml.utils.QName mode)
       throws javax.xml.transform.TransformerException;
  
  /** During deserialization, reinstantiate the transient thread-table
   */
  private void readObject(java.io.ObjectInputStream in)
     throws java.io.IOException, ClassNotFoundException
  {
        in.defaultReadObject();   

        m_nsThreadContexts=new java.util.Hashtable();
  }
  
}
