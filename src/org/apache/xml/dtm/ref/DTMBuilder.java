/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999,2000 The Apache Software Foundation.  All rights 
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
 * 4. The names "Xerces" and "Apache Software Foundation" must
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
 * originally based on software copyright (c) 1999, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

//package org.apache.xerces.parsers;
package org.apache.xml.dtm.ref;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.Attributes;
import org.xml.sax.ext.LexicalHandler;
import java.io.IOException;
import org.apache.xml.dtm.ref.CoroutineManager;
import org.apache.xml.utils.FastStringBuffer;


/** <p>DTMBuilder is a glue layer which accepts input from the
 * CoroutineParser and issues calls to construct DTM nodes.</p>
 *
 * <p>We're not really delighted with this; it's an extra layer of
 * call-and-return per node created. A better approach, since this is
 * so specific to DTM, might be to give it direct ("friend") access to
 * DTM's storage... or to fold it directly into the DTM class. We've
 * broken it out primarily to allow parallel development of the
 * builder and the DTM model. (Or, in fact, to make it a subclass
 * of CoroutineParser to avoid _that_ layer of call-and-return... though
 * I think tail-call optimization will help us in that case.</p>
 *
 * <p>In its current form, this is basically a SAX-to-DTM call adapter.
 * SAX events coming in are restructured -- primarily by having their strings
 * and text content copied into appropriate storage and re-expressed as
 * index numbers. Note that this requires some negotiation between the DTM
 * and the builder to agree on which string pools this content will be placed
 * into.</p>
 *
 * <p>We also restructure the call sequence somewhat, by merging
 * successive characters() calls into a single text-node creation, and
 * by breaking the attributes out of the SAX createEvent call and generating
 * create-attribute (and create-namespace-declaration) calls.</p>
 *
 * <p>Status: MOSTLY DEAD CODE -- Merged into DTM implementations; this
 * class is being retained just for reference and can probably be discarded
 * some time soon -- Check with Joe.</p>
 * */
public class DTMBuilder
implements ContentHandler, LexicalHandler
{

  //
  // Data
  //

  /** Document being built.
   *
   * <p>%TBD% The DTM API doesn't currently have construction calls,
   * so this is explicitly defined as a DTMDocumentImpl. That needs to
   * be fixed.</p>
   * */
  private DTMDocumentImpl m_dtm;

  private CoroutineManager fCoroutineManager = null;
  private int fAppCoroutine = -1;
  private int fParserCoroutine = -1;

  private CoroutineParser co_parser;

  // Scott suggests sharing pools between DTMs. Note that this will require
  // threadsafety at the pool level.
  private static DTMStringPool commonLocalNames=new DTMSafeStringPool();
  private static DTMStringPool commonNamespaceNames=new DTMSafeStringPool();
  private static DTMStringPool commonPrefixes=new DTMSafeStringPool();

  private DTMStringPool localNames; // For this DTM, may be common
  private DTMStringPool namespaceNames; // For this DTM, may be common
  private DTMStringPool prefixes; 
  private FastStringBuffer content; // Unique per DTM
  int contentStart=0;

  // %TBD% The whole startup sequence has to be resolved --
  // How much is passed builder-to-DTM and how much the other way?
  // Who decides whether we're sharing string pools or creating new ones?
  // When do we hook up to the coroutine parser system?

  //
  // Constructors
  //

  /*
   * @param dtm DTM object to be written into
   * <p>%TBD% The DTM API doesn't currently have construction calls,
   * so this is explicitly defined as a DTMDocumentImpl. That needs to
   * be fixed.</p>
   * @param source SAX InputSource to read the XML document from.
   * @param parser SAX XMLReader to be used to parse source into dtm
   * */
  public DTMBuilder(DTMDocumentImpl dtm, InputSource source, org.xml.sax.XMLReader parser)
  {
    m_dtm=dtm;
    
    // Start with persistant shared pools unless the DTM expresses
    // other preferences
    localNames=m_dtm.getLocalNameTable();
    if(localNames==null)
      m_dtm.setLocalNameTable(localNames=commonLocalNames);

    namespaceNames=m_dtm.getNsNameTable();
    if(namespaceNames==null)
      m_dtm.setNsNameTable(namespaceNames=commonNamespaceNames);

    prefixes=m_dtm.getPrefixNameTable();
    if(prefixes==null)
      m_dtm.setPrefixNameTable(prefixes=commonPrefixes);

    // Unlike the other strings, which may be shared and thus should be
    // reset elsewhere (if at all), content starts empty each time we parse.
    content=m_dtm.getContentBuffer();
    if(content==null)
      m_dtm.setContentBuffer(content=new FastStringBuffer());
    else
      content.reset();
    contentStart=0;

    // Establish incremental parsing hookups
    fCoroutineManager=new CoroutineManager();
    fAppCoroutine = fCoroutineManager.co_joinCoroutineSet(-1);
    // %TBD% parser should be passed in so we can plug in the
    // Xalan version of other specific instances.
    co_parser=new CoroutineSAXParser(fCoroutineManager,fAppCoroutine,parser);
    fParserCoroutine=co_parser.getParserCoroutineID();
    co_parser.setContentHandler(this);
    co_parser.setLexHandler(this); // Needed for comments, I think.
    
    // %TBD% MAJOR CONCERN: Are we sure we'll have reached the
    // startup point before we start the call? CHECK THIS!

    // Begin incremental parsing
    // Note that this doesn't return until the first chunk of parsing
    // has been completed and the parser coroutine yields.
    try
      {
        fCoroutineManager.co_resume(source,fAppCoroutine,fParserCoroutine);
      }
    catch(NoSuchMethodException e)
      {
        // Shouldn't happen unless we've miscoded our coroutine logic
        // "Shut down the garbage smashers on the detention level!"
        e.printStackTrace(System.err);
        fCoroutineManager.co_exit(fAppCoroutine);
      }
  }
  
  //
  // Public methods
  //
  
  // String accumulator support
  private void processAccumulatedText()
  {
    int len=content.length();
    if(len!=contentStart)
      {
        // The FastStringBuffer has been previously agreed upon
        m_dtm.appendTextChild(contentStart,len-contentStart);
        contentStart=len;
      }
  }

  //
  // ContentHandler methods
  // Accept SAX events, reformat as DTM construction calls
  public void characters(char[] ch, int start, int length)
       throws org.xml.sax.SAXException
  {
    // Actually creating the text node is handled by
    // processAccumulatedText(); here we just accumulate the
    // characters into the buffer.
    content.append(ch,start,length);
  }
  public void endDocument() 
       throws org.xml.sax.SAXException
  {
    // May need to tell the low-level builder code to pop up a level.
    // There _should't_ be any significant pending text at this point.
    m_dtm.appendEndDocument();
  }
  public void endElement(java.lang.String namespaceURI, java.lang.String localName,
      java.lang.String qName) 
       throws org.xml.sax.SAXException
  {
    processAccumulatedText();
    // No args but we do need to tell the low-level builder code to
    // pop up a level.
    m_dtm.appendEndElement();
  }
  public void endPrefixMapping(java.lang.String prefix) 
       throws org.xml.sax.SAXException
  {
    // No-op
  }
  public void ignorableWhitespace(char[] ch, int start, int length) 
       throws org.xml.sax.SAXException
  {
    // %TBD% I believe ignorable text isn't part of the DTM model...?
  }
  public void processingInstruction(java.lang.String target, java.lang.String data) 
       throws org.xml.sax.SAXException
  {
    processAccumulatedText();
    // %TBD% Which pools do target and data go into?
  }
  public void setDocumentLocator(Locator locator) 
  {
    // No-op for DTM
  }
  public void skippedEntity(java.lang.String name) 
       throws org.xml.sax.SAXException
  {
    processAccumulatedText();
    //%TBD%
  }
  public void startDocument() 
       throws org.xml.sax.SAXException
  {
    // No-op for DTM?
    //m_dtm.startDocument();
  }
  public void startElement(java.lang.String namespaceURI, java.lang.String localName,
      java.lang.String qName, Attributes atts) 
       throws org.xml.sax.SAXException
  {
    processAccumulatedText();

    // %TBD% Split prefix off qname
    String prefix=null;
    int colon=qName.indexOf(':');
    if(colon>0)
      prefix=qName.substring(0,colon);

    // %TBD% Where do we pool expandedName, or is it just the union, or...
    m_dtm.appendStartElement(namespaceNames.stringToIndex(namespaceURI),
                     localNames.stringToIndex(localName),
                     prefixes.stringToIndex(prefix)); /////// %TBD%

    // %TBD% I'm assuming that DTM will require resequencing of
    // NS decls before other attrs, hence two passes are taken.
    // %TBD% Is there an easier way to test for NSDecl?
    int nAtts=atts.getLength();
    // %TBD% Countdown is more efficient if nobody cares about sequence.
    for(int i=nAtts-1;i>=0;--i)	
      {
        qName=atts.getQName(i);
        if(qName.startsWith("xmlns:") || "xmlns".equals(qName))
          {
            prefix=null;
            colon=qName.indexOf(':');
            if(colon>0)
              {
                prefix=qName.substring(0,colon);
              }
            else
              {
                prefix=""; // Default prefix
              }
            

            m_dtm.appendNSDeclaration(
                                    prefixes.stringToIndex(prefix),
                                    namespaceNames.stringToIndex(atts.getValue(i)),
                                    atts.getType(i).equalsIgnoreCase("ID"));
          }
      }
    
    for(int i=nAtts-1;i>=0;--i)	
      {
        qName=atts.getQName(i);
        if(qName.startsWith("xmlns:") || "xmlns".equals(qName))
          {
            // %TBD% I hate having to extract the prefix into a new
            // string when we may never use it. Consider pooling whole
            // qNames, which are already strings?
            prefix=null;
            colon=qName.indexOf(':');
            if(colon>0)
              {
                prefix=qName.substring(0,colon);
                localName=qName.substring(colon+1);
              }
            else
              {
                prefix=""; // Default prefix
                localName=qName;
              }
            
            
            content.append(atts.getValue(i)); // Single-string value
            int contentEnd=content.length();
            
            if(!("xmlns".equals(prefix) || "xmlns".equals(qName)))
              m_dtm.appendAttribute(namespaceNames.stringToIndex(atts.getURI(i)),
                                  localNames.stringToIndex(localName),
                                  prefixes.stringToIndex(prefix),
                                  atts.getType(i).equalsIgnoreCase("ID"),
                                  contentStart, contentEnd-contentStart);
            contentStart=contentEnd;
          }
      }
  }
  public void startPrefixMapping(java.lang.String prefix, java.lang.String uri) 
       throws org.xml.sax.SAXException
  {
    // No-op in DTM, handled during element/attr processing?
  }

  //
  // LexicalHandler support. Not all SAX2 parsers support these events
  // but we may want to pass them through when they exist...
  //
  public void comment(char[] ch, int start, int length) 
       throws org.xml.sax.SAXException
  {
    processAccumulatedText();

    content.append(ch,start,length); // Single-string value
    m_dtm.appendComment(contentStart,length);
    contentStart+=length;    
  }
  public void endCDATA() 
       throws org.xml.sax.SAXException
  {
    // No-op in DTM
  }
  public void endDTD() 
       throws org.xml.sax.SAXException
  {
    // No-op in DTM
  }
  public void endEntity(java.lang.String name) 
       throws org.xml.sax.SAXException
  {
    // No-op in DTM
  }
  public void startCDATA() 
       throws org.xml.sax.SAXException
  {
    // No-op in DTM
  }
  public void startDTD(java.lang.String name, java.lang.String publicId,
      java.lang.String systemId) 
       throws org.xml.sax.SAXException
  {
    // No-op in DTM
  }
  public void startEntity(java.lang.String name) 
       throws org.xml.sax.SAXException
  {
    // No-op in DTM
  }

  //
  // coroutine support
  //

  public int getAppCoroutine() {
    return fAppCoroutine;
  }
  
  /**
   * getMoreNodes() tells the coroutine parser that we need more nodes.
   *
   * Parameter may be:
   *
   *      null            terminate the parser coroutine.
   *
   *      Boolean.TRUE    indication to continue parsing the current document.
   *			  Resumes normal SAX parsing.
   *
   *      Boolean.FALSE   indication to discontinue parsing and reset.
   *			  Throws UserRequestedStopException
   *			  to return control to the run() loop.
   *
   * We will be resumed with 
   *   co_resume(Boolean.TRUE, ...) on success with more remaining to parse.
   *   co_resume(Boolean.FALSE, ...) on success after endDocument.
   */
  private boolean co_yield(boolean getmore, boolean shutdown)
  {
    Object arg=null;
    if(!shutdown)
      arg= getmore ? Boolean.TRUE : Boolean.FALSE;
    try
      {
        arg = fCoroutineManager.co_resume(arg,fAppCoroutine,fParserCoroutine);
        if (arg instanceof Boolean) {
          return ((Boolean)arg).booleanValue();
          }

        else // Unexpected!
          {
            System.err.println(
                  "Active CoroutineSAXParser: unexpected resume parameter, "
                  +arg.getClass
                  ()+" with value=\""+arg+'"');
            System.err.println("\tStopping parser rather than risk deadlock");
            throw new RuntimeException("Coroutine parameter error ("+arg+')');
          }
      }
    catch(java.lang.NoSuchMethodException e)
      {
        // Shouldn't happen unless we've miscoded our coroutine logic
        // "Shut down the garbage smashers on the detention level!"
        e.printStackTrace(System.err);
        fCoroutineManager.co_exit(fAppCoroutine);
      }

    // Only reached if NoSuchMethodException was thrown (no coroutine)
    return(false); 
  }

} // class DTMBuilder
