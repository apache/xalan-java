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
package org.apache.xalan.dtm;
 
import org.w3c.dom.*;
import java.util.Vector;
import java.util.Hashtable;
import org.xml.sax.HandlerBase;
import org.xml.sax.AttributeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import java.io.IOException;

import org.apache.xalan.utils.StringToStringTable;
import org.apache.xalan.utils.StringVector;
import org.apache.xalan.utils.StringToStringTableVector;
import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.xpath.res.XPATHErrorResources;

import org.apache.xerces.readers.XMLEntityHandler;
import org.apache.xerces.dom.DocumentTypeImpl;
import org.apache.xerces.dom.ElementDefinitionImpl;
import org.apache.xerces.dom.EntityImpl;
import org.apache.xerces.framework.XMLDocumentHandler;
import org.apache.xerces.framework.XMLContentSpec;
import org.apache.xerces.utils.QName;
import org.apache.xerces.framework.XMLAttrList;

/**
 * <meta name="usage" content="internal"/>
 * <code>DTM</code> is an XML document model expressed as a table rather than
 * an object tree. It attempts to be very compact, and to support very
 * specifically limited multitasking: users can start reading the document
 * while it's still being generated.  (A work in progress...)
 * <p>
 * (***** The SAX handler calls, and the string-based XMLContentHandler
 * methods, are known to be bad; they're included as hooks for the future.)</p>
 * <p>
 * DTM does _not_ directly support the W3C's Document Object Model. However,
 * it attempts to come close enough that a subset of DOM behavior can be
 * implemented as proxy objects referencing the DTM.</p>
 * @see DTMProxy
 */
public class DTM 
  extends org.apache.xerces.framework.XMLParser implements Runnable,
  XMLDocumentHandler, XMLDocumentHandler.DTDHandler
{
  // COMPILATION CONTROL: Debugging features
  final boolean DISABLE = false;
  final boolean DEBUG = false;
  final boolean DEBUG_WAITS = false;
  final boolean TRACE = false;

  private boolean fCreateEntityReferenceNodes = true;
  
  // DTM state information
  // org.xml.sax.Parser parser;
  // org.apache.xalan.dtm.HookedXMLParser ibmparser;
  // org.apache.xerces.utils.StringPool fStringPool;
  ChunkedIntArray nodes = new ChunkedIntArray(4);
  
  /**
   * Make the string public.
   */
  public org.apache.xerces.utils.StringPool getStringPool() { return fStringPool; }

  // Context for parse-and-append operations
  int currentParent = 0;
  int previousSibling = 0;
  private boolean XML4J=false;
  private boolean processingIgnorableWhitespace = false;
  private boolean processingCDATASection = false;
  private boolean previousSiblingWasParent = false;
  // Local cache for record-at-a-time fetch
  int gotslot[] = new int[4];

  // Unique-string-to-integer conversions, for use with SAX.
  // (The XMLContentHandler interface patches back into XML4J's
  // symbol tables instead */
  Hashtable symbolTable = new Hashtable();
  Vector symbolList=new Vector();

  // MANEFEST CONSTANTS
  // Status bits, ORed with node type (assumed to be <256, should be safe)
  final int TEXT_IGNORABLE = 2 << 8;
  final int TEXT_CDATA = 4 << 8;
  
  // Impossible prefix to look up the default namespace
  public static final String DEFAULT_PREFIX_STR = "#:::";

  // Handshaking for updates of simul read/write
  final int UPDATE_FREQUENCY=10;
  int update_counter=UPDATE_FREQUENCY;
  // endDocument recieved?
  private boolean done = false;
  
  DTMProxy document;
  
  boolean m_isError = false;
  
  protected int fAmpIndex;
  protected int fLtIndex;
  protected int fGtIndex;
  protected int fAposIndex;
  protected int fQuotIndex;
  
  ChunkedIntArray m_entityNodes = null;
  IntMap m_entities = new IntMap();
  
  IntToObjectMap m_elementDecls = new IntToObjectMap();
  
  IntMap m_idMap = new IntMap();
  
  /**
   * Namespace lookup. This is actually a stack of StringToStringTables
   */
  StringToStringTableVector namespaceTable = new StringToStringTableVector(64);
  
  /**
   * Default empty namespace
   */
  private StringToStringTable m_emptyNamespace = new StringToStringTable();
  
  // This needs to be set before calling run()
  private InputSource m_inputSource = null;
  
  public void setInputSource(InputSource inputSource)
  {
    m_inputSource = inputSource;
  }
  
  /**
   * Construct a DTM.
   * This is the only constructor currently working.
   * @param parser HookedXMLParser Input event-stream source.
   */
  public DTM()
  {
    super();
    
    initHandlers(false, this, this);
  }

  /** Returns the XML Schema validator. */
  /*
  protected org.apache.xerces.validators.schema.XSchemaValidator getSchemaValidator() 
  {
    if (fSchemaValidator == null)
      fSchemaValidator = new NullSchemaValidator(fStringPool, fErrorReporter, fEntityHandler);
    return fSchemaValidator;
  }
  */
  
  boolean m_throwNewError = true;

  
  /**
   * Run the parse thread.
   */
  public void run()
  {
    try
    {
      m_throwNewError = true;
      parse(m_inputSource);
    }
    catch(Exception e)
    {
      ; // should have already been reported via the error handler?
    }
  }
  
  
  // For now, just have an array of references.  This can 
  // later be changed to some sort of pool... maybe.
  DTMNodeVector m_proxies = new DTMNodeVector();
  
  /**
   * Return a Node object that represents the index.
   */
  public final DTMProxy getNode(int pos)
  {
    if(true)
    {
      // Create a new node every time...
      if(0 == pos)
      {
        if(null == document)
          document = new DTMProxy(this, pos, Node.DOCUMENT_NODE);
        return document;
      }
      return new DTMProxy(this, pos);
    }
    else
    {
      // cache the objects in an array...
      DTMProxy proxy = m_proxies.get(pos);
      if(null == proxy)
      {
        proxy = new DTMProxy(this, pos);
        m_proxies.put(pos, proxy);
      }
      
      // For now...
      return proxy;
    }
  }

  /**
   * Wrapper for ChunkedIntArray.append, to automatically update the
   * previous sibling's "next" reference (if necessary) and periodically
   * wake a reader who may have encountered incomplete data and entered
   * a wait state.
   * @param w0 int As in ChunkedIntArray.append
   * @param w1 int As in ChunkedIntArray.append
   * @param w2 int As in ChunkedIntArray.append
   * @param w3 int As in ChunkedIntArray.append
   * @return int As in ChunkedIntArray.append
   * @see ChunkedIntArray.append
   */
  private final int appendNode(int w0, int w1, int w2, int w3)
  {
    // A decent compiler will probably inline this.
    int slotnumber = nodes.appendSlot(w0, w1, w2, w3);

    if(DEBUG) System.out.println(slotnumber+": "+w0+" "+w1+" "+w2+" "+w3);
    
    if(previousSiblingWasParent)
      nodes.writeEntry(previousSibling,2,slotnumber);

    previousSiblingWasParent = false; // Set the default; endElement overrides

    if (--update_counter == 0)
      synchronized (this)
      {
        update_counter=UPDATE_FREQUENCY;
        notifyAll();
      }
    
    return slotnumber;
  }
  
  //==========================================================
  // SECTION: ContentHandler
  //==========================================================
  private final void ____ContentHandler____(){}

  /**
   * XMLContentHandler API: Start-of-document recieved.
   * Much like the SAX startDocument() and setDocumentLocator() calls,
   * but also has the side effect of switching DTM into its XML4J-aware
   * mode.
   */
  public final void startDocument() 
  {
    XML4J = true;
    // Initialize the doc -- no parent, no next-sib
    nodes.writeSlot(0,Node.DOCUMENT_NODE,-1,-1,0);
    document = getNode(0);
    // Make sure nobody is still waiting
    synchronized (this)
    {
      if(DEBUG_WAITS)
        System.out.println("startDocument(1): "+document);
      notify();
    }
  }
  
  /**
   ** SAX API: End of Document reached. 
   * Finalize the DTM: close out the sibling chain, mark the document as
   * no-more-nodes-expected, and wake up anyone who is waiting.
   * @exception org.xml.sax.SAXException Not used.
   */
  public final void endDocument() 
    throws org.xml.sax.SAXException
  {
    if(DISABLE)return;

    done=true;
    
    // Fix up "next" of last Element, if needed
    if(previousSiblingWasParent)
      nodes.writeEntry(previousSibling,2,-1);

    // Make sure nobody is still waiting
    synchronized (this)
    {
      // if(DEBUG_WAITS)
      //  System.out.println("endDocument");
      notifyAll();
    }
  }

  /**
   * XMLContentHandler API: Process element start-tag and its attributes.
   * This includes pushing a new namespace context (with any
   * namespaces declared on this element), creating the Element
   * node, making it the new parent, and creating child Attribute
   * nodes as needed. Namespace declarations _do_ appear as
   * attributes. Attributes are expressed as the leading children
   * of the Element, and are separated out later, which is different
   * from DOM's behavior.
   * <p>
   * The mapping from namespace prefixes to namespace URIs is also performed
   * at this time.
   * @param elementNameIndex int Index of element's qualified name in
   * symbol table.
   * @param attrListIndex int Starting index of this element's attributes
   * in the parser's attribute table, or -1 to indicate no attributes.
   */
  public final void startElement(QName qname,
                           XMLAttrList xmlAttrList, 
                           int attrListIndex) 
  {
    if(DISABLE)return;    
    // Need to retrive the attrList...
        
    String attrname, attrvalue;

    // Push a new namespace context

    StringToStringTable pushNS=m_emptyNamespace;
    
    // Process any new namespace declarations
    if(attrListIndex!=-1)
    {
      for (int index = xmlAttrList.getFirstAttr(attrListIndex);
           index != -1;
           index = xmlAttrList.getNextAttr(index))
      {
        attrname = fStringPool.toString(xmlAttrList.getAttrName(index));
        if (attrname.startsWith("xmlns:"))
        {
          // XML4J very politely offers to stringify this for us
          attrvalue = fStringPool.toString(xmlAttrList.getAttValue(index));
          String nsprefix = attrname.substring(6);
          if(m_emptyNamespace == pushNS)
            pushNS = new StringToStringTable();
          pushNS.put(nsprefix,attrvalue);
        }
        else if(attrname.equals("xmlns"))
        {
          attrvalue = fStringPool.toString(xmlAttrList.getAttValue(index));
          if(m_emptyNamespace == pushNS)
            pushNS = new StringToStringTable();
          pushNS.put(DEFAULT_PREFIX_STR,attrvalue);
        }
      }
    }
    namespaceTable.addElement(pushNS);
    
    // Scope some stuff...
    int ourslot;
    {
      // W0 Low: Node Type.
      // W0 High: Namespace
      int w0 = org.w3c.dom.Node.ELEMENT_NODE | (qname.uri << 16);
      // W1: Parent
      int w1 = currentParent;
      // W2: Next. Initialize as 0 (unresolved)
      int w2 = 0;
      // W3: Tagname
      int w3 = qname.rawname;

      // Add this element to the document
      ourslot = appendNode(w0, w1, w2, w3);
      
    }
    // Change append context
    currentParent = ourslot;
    
    previousSibling = 0;
    
    IntMap elemMap = (IntMap)m_elementDecls.get(qname.rawname);

    // Append the attributes
    if(attrListIndex!=-1)
    {
      for (int index = xmlAttrList.getFirstAttr(attrListIndex);
           index != -1;
           index = xmlAttrList.getNextAttr(index))
      {
        // W0 Low: Node Type.
        // W0 High: Namespace
        int attrNameIndex = xmlAttrList.getAttrName(index);
        attrname=fStringPool.toString(attrNameIndex);
        
        if(null != elemMap)
        {
          int attrDecl = elemMap.get(attrNameIndex);
          if((attrDecl >> 16) == fStringPool.addSymbol("ID"))
          {
            // Apparently, attribute lists have their own string pool.
            int attrValIndex = xmlAttrList.getAttValue(index);
            // But then, I don't understand why this works.
            String valStr=fStringPool.toString(attrValIndex);
            int valIndex = fStringPool.addSymbol(valStr);
            m_idMap.put(valIndex, ourslot);
          }
        }

        int w0;
        int colonpos = attrname.indexOf(':');
        if(colonpos > 0)
        {
          String prefix = attrname.substring(0, colonpos);
          w0 = org.w3c.dom.Node.ATTRIBUTE_NODE | (stringToInt(resolveNamespace(prefix)) << 16);
        }
        else
        {
          w0 = org.w3c.dom.Node.ATTRIBUTE_NODE;
        }
        // W1: Parent
        int w1 = currentParent;
        // W2: Next (not yet resolved)
        int w2 = 0;
        // W3: Tagname
        int w3 = xmlAttrList.getAttrName(index);

        // Add this element to the document
        ourslot = appendNode(w0, w1, w2, w3);
        previousSibling=ourslot;
        
        // Create attribute substructure. 
        // ***** Current XML4J will _only_ yield a single text,
        //   rather than attempting to retain EntityReference nodes
        //   within Attribute values.
        // ***** DTMProxy currently assumes this behavior!
        // W0 Low: Node Type, with flag if ignorable whitespace
        // W0 High: Buffer index (in SAX mode) or 0 (XML4J mode)
        w0=org.w3c.dom.Node.TEXT_NODE;
        // W1: Parent
        w1 = ourslot;
        // W2: Start position within buffer (SAX), or text index (XML4J)
        w2 = xmlAttrList.getAttValue(index);
        // W3: Length of this text (SAX), or 0 (XML4J)
        w3 = 0;
        
        // *************************************************
        // ***********FIX BUG SOMEWHERE IN HERE*************
        // *************************************************
        
        // Do a "weak push" to make this first child
        // of the Attr node rather than its next-sib.
        previousSibling=0;
        appendNode(w0,w1,w2,w3);
        // Restore attr as the sib
        previousSibling=ourslot;		    
        
        // Attrs are Parents
        previousSiblingWasParent=true;
      }
    }
  }
  
  /**
   * XMLDocumentHandler API: End-tag reached. Pop the parentage context, along
   * with any namespaces this element defined. Make sure that the sibling chain
   * has been properly terminated.
   * @param name int Index of element name in XML4J's symbol table
   */
  public final void endElement(QName name)
  {
    if(DISABLE)return;    
    int thisElement = currentParent;
    
    // If last node appended before we pop has a next-sib reference,
    // (true of nodes which function as parents)
    // we need to switch that from 0 (unknown) to -1 (null)
    if (previousSibling != 0 & previousSiblingWasParent)
      nodes.writeEntry(previousSibling,2, -1);

    // Pop parentage
    nodes.readSlot(currentParent, gotslot);
    currentParent = gotslot[1];

    // The element just being finished will be
    // the previous sibling for the next operation
    previousSibling = thisElement;
    previousSiblingWasParent = true;

    // Pop a level of namespace table
    namespaceTable.removeLastElem();
  }


  /**
   * SAX API: Accept a chunk of characters for normalization into a Text
   * node. Note that since SAX may reuse its input buffers, we may need to
   * either extract the string now or cache a copy of the buffer. It is unclear
   * which approach is preferable
   * @param ch char[] Input buffer in which string can be found.
   * @param start int Offset to start of string, 0-based.
   * @param length int Length of string
   * @exception org.xml.sax.SAXException Required by API, not thrown as far
   * as I know.
   */
  public final void characters(char ch[], int start, int length) 
    throws org.xml.sax.SAXException
  {
    throw new SAXException(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_SAX_API_NOT_HANDLED, null)); //"SAX API characters(char ch[]... not handled by the DTM!");
  }

  /**
   * XMLContentHandler API: Accept a chunk of characters for normalization 
   * into a Text node. This is an "enhanced SAX" flavor of the call, adding the
   * flag for CDATA sections, and is not currently used.
   * <p>
   * Note that since XML4J may reuse its input buffers, we may need to
   * either extract the string now or cache a copy of the buffer. It is unclear
   * which approach is preferable
   * @param ch char[] Input buffer in which string can be found.
   * @param start int Offset to start of string, 0-based.
   * @param length int Length of string
   * @param cdataSection boolean True iff text was enclosed in <![CDATA[ ]]>
   * @exception org.xml.sax.SAXException Required by API, not thrown as far
   * as I know.
   */
  public final void characters(char ch[], int start, int length, boolean cdataSection) 
    throws org.xml.sax.SAXException
  {
    // XML4J, currently not used
    // ***** Currently not recording distinction between CDATA
    // and text. Probably should, for model consistancy...?
    characters(ch,start,length);
  }

  /**
   * XMLContentHandler API: Accept a chunk of characters for normalization 
   * into a Text node. This is the "late binding" version of the call, which
   * we currently rely upon.
   * @param dataIndex int Index of this string in XML4J's symbol tables
   * @param cdataSection boolean True iff text was enclosed in <![CDATA[ ]]>
   * @exception org.xml.sax.SAXException Required by API, not thrown as far
   * as I know.
   */
  public final void characters(int dataIndex) 
    throws org.xml.sax.SAXException
  {
    if(DISABLE)return;
    general_characters(dataIndex);
  }
  
  /** Start CDATA section. */
  public final void startCDATA() throws Exception 
  {
  }

  /** End CDATA section. */
  public final void endCDATA() throws Exception 
  {
  }
  
  /** Ignorable whitespace. */
  public final void ignorableWhitespace(int dataIndex) 
    throws Exception 
  {
    general_characters(dataIndex);
  }
  
  /**
   * SAX API: Accept a chunk of characters for normalization into an
   * ignorable-whitespace Text
   * node. Note that since SAX may reuse its input buffers, we may need to
   * either extract the string now or cache a copy of the buffer. It is unclear
   * which approach is preferable
   * @param ch char[] Input buffer in which string can be found.
   * @param start int Offset to start of string, 0-based.
   * @param length int Length of string
   * @exception org.xml.sax.SAXException Required by API, not thrown as far
   * as I know.
   */
  public final void ignorableWhitespace(char ch[], int start, int length) 
    throws org.xml.sax.SAXException
  {
    throw new SAXException(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_IGNORABLE_WHITESPACE_NOT_HANDLED, null)); //"ignorableWhitespace(char ch[]... not handled by the DTM!");
  }
  
  /**
   * XMLContentHandler API: Accept a chunk of characters for normalization 
   * into an ignorable-whitespace Text node. 
   * This is an "enhanced SAX" flavor of the call, adding the
   * flag for CDATA sections, and is not currently used.
   * <p>
   * Note that since XML4J may reuse its input buffers, we may need to
   * either extract the string now or cache a copy of the buffer. It is unclear
   * which approach is preferable
   * @param ch char[] Input buffer in which string can be found.
   * @param start int Offset to start of string, 0-based.
   * @param length int Length of string
   * @param cdataSection boolean True iff text was enclosed in <![CDATA[ ]]>
   * @exception org.xml.sax.SAXException Required by API, not thrown as far
   * as I know.
   */
  public final void ignorableWhitespace(char ch[], int start, int length, boolean cdataSection) 
    throws org.xml.sax.SAXException
  {
    throw new SAXException(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_IGNORABLE_WHITESPACE_NOT_HANDLED, null)); //"ignorableWhitespace(char ch[]... not handled by the DTM!");
  }
  
  /**
   * XMLContentHandler API: Accept a chunk of characters for normalization 
   * into an ignorable-whitespace Text node. 
   * This is the "late binding" version of the call, which
   * we currently rely upon.
   * @param dataIndex int Index of this string in XML4J's symbol tables
   * @param cdataSection boolean True iff text was enclosed in <![CDATA[ ]]>
   * @exception org.xml.sax.SAXException Required by API, not thrown as far
   * as I know.
   */
  public final void ignorableWhitespace(int dataIndex, boolean cdataSection) 
    throws org.xml.sax.SAXException
  {
    processingIgnorableWhitespace = true;
    general_characters(dataIndex);
  }

  /** Text-accumulator operation for the integer-index version of
   * characters(). Obviously far simpler, since we are assured that
   * (unlike the parse buffers) the XML4J symbol table will persist.
   * @param index int Index of this string in XML4J's symbol tables.
   *<p>
   * KNOWN LIMITATION: DOESN'T PRESERVE CDATA FLAG.
   */
  public final void general_characters(int index) 
  {
    // Add this element to the document
    int w0 = Node.TEXT_NODE;
    // W1: Parent
    int w1 = currentParent;
    // W2: Start position within buffer (SAX), or text index (XML4J)
    int w2 = index;
    // W3: Length of this text (SAX), or 0 (XML4J)
    int w3 = gotslot[2];
    int ourslot = appendNode(w0, w1, w2, w3);
    previousSibling = ourslot;
  }

  /**
   * XMLContentHandler API: Create a Comment node. Available only in the
   * late-binding form, and not supported in SAX 1.0 at all.
   * @param dataIndex int Index of comment's contents in XML4J's symbol table
   */
  public final void comment(int dataIndex) 
  {
    if(DISABLE)return;
    
    // Short Form, XML4J mode
    int w0, w1, w2, w3;
    
    // W0 Low: Node Type, with flags
    // W0 High: Not used
    w0 = Node.COMMENT_NODE;
    // W1: Parent
    w1 = currentParent;
    // W2: Text number
    w2 = dataIndex;
    // W3: Length of this text TBD
    w3 = -1;

    // Add this element to the document
    int ourslot = appendNode(w0, w1, w2, w3);
    previousSibling = ourslot;
  }
  
  /**
   * XMLContentHandler API: Create a PI node.
   * @param target int target processor name index
   * @param data int Index of PI parameters to be passed to that processor
   */
  public final void processingInstruction(int target, int data) 
  {
    if(DISABLE)return;
    
    // W0 Low: Node Type.
    int w0 = org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE;
    // W1: Parent
    int w1 = currentParent;
    // W2: data
    int w2 = data;
    // W3: Target
    int w3 = target;

    // Add this element to the document
    int ourslot = appendNode(w0, w1, w2, w3);

    previousSibling = ourslot;
  }

  
  /** Report the start of the scope of a namespace declaration. */
  public final void startNamespaceDeclScope(int prefix, int uri) throws Exception 
  {
  }

  /** Report the end of the scope of a namespace declaration. */
  public final void endNamespaceDeclScope(int prefix) throws Exception 
  {
  }
  
  /** Report when the internal subset is completely scanned. */
  public final void internalSubset(int internalSubset)
  {
  }
  
  /** Start entity reference. */
  public final void startEntityReference(int entityName, int entityType,
                                   int entityContext) throws Exception 
  {
    /*
    if(null == m_entityNodes)
      initDefaultEntities();

    // are we ignoring entity reference nodes?
    if ((entityName == fAmpIndex) ||
        (entityName == fGtIndex) ||
        (entityName == fLtIndex) ||
        (entityName == fAposIndex) ||
        (entityName == fQuotIndex)) 
      // if(entityName > 0)
    {
      String str = fStringPool.toString(entityName);

      int entRef = m_entities.get(entityName);
      if(entRef >= 0)
      {
        int val = m_entityNodes.readEntry(entRef, 3);
        general_characters(val);
      }
      return;
    }
    */

  } // startEntityReference(int,int,int)

  /** End entity reference. */
  public final void endEntityReference(int entityName, int entityType,
                                 int entityContext) throws Exception 
  {
  } // endEntityReference(int,int,int)
  
  /**
     * Set the state of any feature in a SAX2 parser.  The parser
     * might not recognize the feature, and if it does recognize
     * it, it might not be able to fulfill the request.
     *
     * @param featureId The unique identifier (URI) of the feature.
     * @param state The requested state of the feature (true or false).
     *
     * @exception org.xml.sax.SAXNotRecognizedException If the
     *            requested feature is not known.
     * @exception org.xml.sax.SAXNotSupportedException If the
     *            requested feature is known, but the requested
     *            state is not supported.
     * @exception org.xml.sax.SAXException If there is any other
     *            problem fulfilling the request.
     */
    public void setFeature(String featureId, boolean state)
        throws SAXNotRecognizedException, SAXNotSupportedException

    {
       if (featureId.startsWith(XERCES_FEATURES_PREFIX)) 
       {
         String feature = featureId.substring(XERCES_FEATURES_PREFIX.length());
            
         //
         // http://apache.org/xml/features/dom/create-entity-ref-nodes
         //   This feature determines whether entity references within
         //   the document are included in the document tree as
         //   EntityReference nodes.
         //   Note: The children of the entity reference are always
         //         added to the document. This feature only affects
         //         whether an EntityReference node is also included
         //         as the parent of the entity reference children.
         //
         if (feature.equals("dom/create-entity-ref-nodes")) {
             setCreateEntityReferenceNodes(state);
           return;
         }
       }
       //
       // Pass request off to XMLParser for the common cases.
       //
       super.setFeature(featureId, state);
    }  

  //==========================================================
  // SECTION: DTD Handling
  //==========================================================
  private final void ____DTDHandling____(){}
  
  private final void initDefaultEntities()
  {
    fAmpIndex = fStringPool.addSymbol("amp");
    fLtIndex = fStringPool.addSymbol("lt");
    fGtIndex = fStringPool.addSymbol("gt");
    fAposIndex = fStringPool.addSymbol("apos");
    fQuotIndex = fStringPool.addSymbol("quot");
    
    try
    {
      m_entityNodes = new ChunkedIntArray(4);
      declareEntity(fAmpIndex, fStringPool.addSymbol("&"));
      declareEntity(fLtIndex, fStringPool.addSymbol("<"));
      declareEntity(fGtIndex, fStringPool.addSymbol(">"));
      declareEntity(fAposIndex, fStringPool.addSymbol("'"));
      declareEntity(fQuotIndex, fStringPool.addSymbol("\""));
    }
    catch(Exception e)
    {
    }
  }

  //
  // DTDValidator.EventHandler methods
  //

  /**
   *  This function will be called when a &lt;!DOCTYPE...&gt; declaration is
   *  encountered.
   */
  public final void startDTD(int rootElementType, int publicId, int systemId)
    throws Exception 
  {
    // String qualifiedName = fStringPool.toString(rootElementType);
    // String publicID = fStringPool.toString(publicId);
    // String systemID = fStringPool.toString(systemId);
    
    // fDocumentType = new org.apache.xerces.dom.DocumentTypeImpl(null, qualifiedName, publicID, systemID);

  } // startDTD(int,int,int)

  /**
   *  This function will be called at the end of the DTD.
   */
  public final void endDTD() throws Exception {}

  /**
   * callback for an element declaration. 
   *
   * @param elementType element handle of the element being declared
   * @param contentSpec contentSpec for the element being declared
   * @see org.apache.xerces.framework.XMLContentSpec
   * @exception java.lang.Exception
   */
  public void elementDecl(QName elementDecl, XMLContentSpec contentSpec) throws Exception
  {
  }

  /**
   * callback for an attribute list declaration. 
   *
   * @param elementType element handle for the attribute's element
   * @param attrName string pool index of the attribute name
   * @param attType type of attribute
   * @param enumString String representing the values of the enumeration,
   *        if the attribute is of enumerated type, or null if it is not.
   * @param attDefaultType an integer value denoting the DefaultDecl value
   * @param attDefaultValue string pool index of this attribute's default value 
   *        or -1 if there is no defaultvalue 
   * @exception java.lang.Exception
   */
  public void attlistDecl(QName elementDecl, QName attributeDecl,
                          int attType, String enumString,
                          int attDefaultType,
                          int attDefaultValue) throws Exception
  {
  }
  
  /**
   * callback for the start of the DTD
   * This function will be called when a &lt;!DOCTYPE...&gt; declaration is
   * encountered.
   *
   * @param rootElementType element handle for the root element of the document
   * @param publicId string pool index of the DTD's public ID
   * @param systemId string pool index of the DTD's system ID
   * @exception java.lang.Exception
   */
  public void startDTD(QName rootElement, int publicId, int systemId) throws Exception
  {
  }

  /**
   * &lt;!ELEMENT Name contentspec&gt;
   */
  public void elementDecl(QName elementDecl, 
                          int contentSpecType, 
                          int contentSpecIndex,
                          XMLContentSpec.Provider contentSpecProvider) 
    throws Exception
  {
  }
  
  /**
   * &lt;!ATTLIST Name AttDef&gt;
   */
  public void attlistDecl(QName elementDecl, QName attributeDecl,
                          int attType, boolean attList,
                          String enumString,
                          int attDefaultType,
                          int attDefaultValue) throws Exception
  {
  } // attlistDecl(int,int,int,String,int,int)
  
  /**
   * Get the element identified by the ID.
   */
  public final Element getIdentifier(String id)
  {
    int strIndex = fStringPool.addSymbol(id);
    int elemNum = m_idMap.get(strIndex);
    if(elemNum <= 0)
    {
      // Then we have to keep trying until it has arrived, or the end of 
      // the document is found.
      while((elemNum <= 0) && !done && !m_isError)
      {
        synchronized (this)
        {
          try
          {
            if(DEBUG_WAITS)
              System.out.println("Waiting... getIdentifier");
            wait();
          }
          catch (InterruptedException e)
          {
            // That's OK, it's as good a time as any to check again
          }
          elemNum = m_idMap.get(strIndex);
        }
      }
    }
    return (elemNum > 0) ? getNode(elemNum) : null;
  }

  /**
   * &lt;!ENTITY % Name EntityValue&gt; (internal)
   */
  public final void internalPEDecl(int entityName, int entityValue) throws Exception {}

  /**
   * &lt;!ENTITY % Name ExternalID>                (external)
   */
  public final void externalPEDecl(int entityName, int publicId, int systemId) throws Exception {}
  
  /**
   * Declare an entity.
   */
  private final void declareEntity(int entityName, int entityValue)
  {
    //if (!fCreateEntityReferenceNodes)
    //  return;
    
    int w0=org.w3c.dom.Node.ENTITY_NODE;
    // W1: Parent
    int w1 = 0;
    // W2: name index
    int w2 =  entityName;
    // W3: Not used
    int w3 = entityValue;

    int slotnumber = m_entityNodes.appendSlot(w0, w1, w2, w3);
    
    m_entities.put(entityName, slotnumber);

    // if (fDocumentType == null) return; //revist: should never happen. Exception?

    //revist: how to check if entity was already declared.
    // XML spec says that 1st Entity decl is binding.

    // Entity entity = fDocumentImpl.createEntity(fStringPool.toString(entityName));
    // fDocumentType.getEntities().setNamedItem(entity);

  } // internalEntityDecl(int,int)

  /**
   * &lt;!ENTITY Name EntityValue&gt; (internal)
   */
  public final void internalEntityDecl(int entityName, int entityValue)
    throws Exception 
  {
    if(null == m_entityNodes)
      initDefaultEntities();

    declareEntity(entityName, entityValue);

  } // internalEntityDecl(int,int)
  

  /**
   * &lt;!ENTITY Name ExternalID>                (external)
   */
  public final void externalEntityDecl(int entityName, int publicId, int systemId)
    throws Exception 
  {
    if(null == m_entityNodes)
      initDefaultEntities();

    int w0=org.w3c.dom.Node.ENTITY_NODE;
    // W1: Parent
    int w1 = 0;
    // W2: public id
    int w2 =  publicId;
    // W3: system id
    int w3 = systemId;

    int slotnumber = m_entityNodes.appendSlot(w0, w1, w2, w3);
    
    m_entities.put(entityName, slotnumber);

    //revist: how to check if entity was already declared.
    // XML spec says that 1st Entity decl is binding.

    // EntityImpl entity = (EntityImpl)fDocumentImpl.createEntity(fStringPool.toString(entityName));
    // entity.setPublicId(fStringPool.toString(publicId));
    // entity.setSystemId(fStringPool.toString(systemId));
    // fDocumentType.getEntities().setNamedItem(entity);

  } // externalEntityDecl(int,int,int)

  /**
   * &lt;!ENTITY Name ExternalID NDataDecl>      (unparsed)
   */
  public final void unparsedEntityDecl(int entityName,
                                 int publicId, int systemId,
                                 int notationName) throws Exception 
  {
    if(null == m_entityNodes)
      initDefaultEntities();

    int w0=org.w3c.dom.Node.ENTITY_NODE;
    // W1: Parent
    int w1 = 0;
    // W2: name index
    int w2 =  publicId;
    // W3: Hope we have enough bits...
    int w3 = systemId | (notationName << 16);

    int slotnumber = m_entityNodes.appendSlot(w0, w1, w2, w3);
    
    m_entities.put(entityName, slotnumber);

    //revist: how to check if entity was already declared.
    // XML spec says that 1st Entity decl is binding.

    // EntityImpl entity = (EntityImpl)fDocumentImpl.createEntity(fStringPool.toString(entityName));
    // entity.setPublicId(fStringPool.toString(publicId));
    // entity.setSystemId(fStringPool.toString(systemId));
    // entity.setNotationName(fStringPool.toString(notationName));
    // fDocumentType.getEntities().setNamedItem(entity);

  } // unparsedEntityDecl(int,int,int,int)

  /**
   * &lt;!NOTATION Name ExternalId>
   */
  public final void notationDecl(int notationName, int publicId, int systemId)
    throws Exception 
  {

    //revist: how to check if entity was already declared.
    // XML spec says that 1st Entity decl is binding.

    // NotationImpl notation = (NotationImpl)fDocumentImpl.createNotation(fStringPool.toString(notationName));
    // notation.setPublicId(fStringPool.toString(publicId));
    // notation.setSystemId(fStringPool.toString(systemId));

    // fDocumentType.getNotations().setNamedItem(notation);

  } // notationDecl(int,int,int)
  
  //==========================================================
  // SECTION: DTM Read API
  //==========================================================
  private final void ____DTMReadAPI____(){}

  /**
   * Return the document proxy object.
   */
  public final Document getDocument()
  {
    if(TRACE)
      System.out.println("DTM: getDocument");
    
    if(null != document)
      return document;
    
    synchronized (this)
    {
      while(null == document)
      {
        try
        {
          if(m_isError)
            break;
          if(DEBUG_WAITS)
            System.out.println("Waiting... getDocument");
          wait();
          if(DEBUG_WAITS)
            System.out.println("Out of waiting... getDocument: "+document);
        }
        catch (InterruptedException e)
        {
          // That's OK, it's as good a time as any to check again
        }
      }
    }
    if(DEBUG_WAITS)
      System.out.println("getDocument returning: "+document);
    return document;
  }

  /**
   * DTM read API: Given a node index, get the index of the node's first child.
   * If not yet resolved, waits for more nodes to be added to the document and
   * tries again
   * @param postition int Index of this node's record.
   * @return int DTM node-number of first child, or -1 to indicate none exists.
   */
  public final int getFirstChild(int position)
  {
    if(TRACE)
      System.out.println("DTM: getFirstChild");
        
    // Examine the node in question	
    nodes.readSlot(position, gotslot);

    // If not an element or Attribute or EntRef, child is null
    int type = (gotslot[0] & 0xFFFF);
    if ((type != Node.ELEMENT_NODE) &&
        (type != Node.ENTITY_REFERENCE_NODE) &&
        (type != Node.DOCUMENT_NODE))
      return -1;
    
    // 0 is Document; first child is node 1.
    int parent = (position == 0) ? 1 : gotslot[1];
    
    // Advance to first non-Attr child
    // (First child of any kind is at position+1, thereafter walk sibs)
    int kid=position+1;
    while((kid > nodes.lastUsed) && !done)
    {
      synchronized (this)
      {
        try
        {
          if(m_isError)
            break;
          if(DEBUG_WAITS)
            System.out.println("Waiting... getFirstChild");
          wait();
        }
        catch (InterruptedException e)
        {
          // That's OK, it's as good a time as any to check again
        }
      }
    }

    // ***** MIGHT GET MORE SPEED BY INLINING THE NEXT-SIB CODE.
    while (kid != -1)
    {
      nodes.readSlot(kid,gotslot);
      boolean isAttr = ((gotslot[0]&0xff) == Node.ATTRIBUTE_NODE);
      if(isAttr)
      {
        // kid=getNextSibling(kid); // Since attrs have 2 nodes, advance by 2
        // if(kid == -1)
        //  break;
      }
      else if(gotslot[1] == position)
        return kid;
      else if(gotslot[1] == parent)
        break; // already to next sibling.
      
      // Else it's an Attr. Advance to next (Attrs have Next in DTM)
      // and try again.
      kid=getNextSibling(kid);
    }
    return -1;
  }
  
  /**
   * DTM read API: Given a node index, get the index of the node's first child.
   * If not yet resolved, waits for more nodes to be added to the document and
   * tries again
   * @param postition int Index of this node's record.
   * @return int DTM node-number of first child, or -1 to indicate none exists.
   */
  public final int getFirstAttribute(int position)
  {
    if(TRACE)
      System.out.println("DTM: getFirstAttribute");
        
    // Examine the node in question	
    nodes.readSlot(position, gotslot);

    // If not an element or Attribute or EntRef, child is null
    int type = (gotslot[0] & 0xFFFF);
    if (type != Node.ELEMENT_NODE)
      return -1;
    
    // 0 is Document; first child is node 1.
    // int parent = (position == 0) ? 1 : gotslot[1];
    
    // Advance to first non-Attr child
    // (First child of any kind is at position+1, thereafter walk sibs)
    int kid=position+1;
    while((kid > nodes.lastUsed) && !done)
    {
      synchronized (this)
      {
        try
        {
          if(m_isError)
            break;
          if(DEBUG_WAITS)
            System.out.println("Waiting... getFirstChild");
          wait();
        }
        catch (InterruptedException e)
        {
          // That's OK, it's as good a time as any to check again
        }
      }
    }

    nodes.readSlot(kid,gotslot);
    boolean isAttr = ((gotslot[0]&0xff) == Node.ATTRIBUTE_NODE);
    if(isAttr)
    {
      return kid;
    }
    return -1;
  }


  /**
   * DTM read API: Given a node index, advance to the next attribute. If an
   * element, we advance to its first attribute; if an attr, we advance to
   * the next attr on the same node.
   * If not yet resolved, waits for more nodes to be added to the document and
   * tries again.
   * @param postition int Index of this node's record.
   * @return int DTM node-number of the resolved attr, 
   * or -1 to indicate none exists.
   */
  public final int getNextAttribute(int position)
  {
    if(TRACE)
      System.out.println("DTM: getNextAttribute: "+position);

    while (!m_isError)
    {
      // Next after last in document is Null.
      if (done && position >= nodes.lastUsed)
        return -1;

      // Examine the node in question	
      nodes.readSlot(position, gotslot);
      
      // If not starting from element or attribute, 
      // this operation doesn't apply
      int type=gotslot[0] & 0xFFFF;
      if (type != Node.ATTRIBUTE_NODE && 
          type!= Node.ELEMENT_NODE)
        return -1;

      else
      {
        if (position < nodes.lastUsed) 
        {
          // If starting from an element,
          // First attr is following slot, or doesn't exist.
          if(type==Node.ELEMENT_NODE)
          {
            if((nodes.readEntry(position + 1, 0)& 0xFFFF) == 
               Node.ATTRIBUTE_NODE)
              return position + 1;
            else
              return -1;
          }
          
          // If starting from an attribute,
          // next attr is via reference
          if(gotslot[2] != 0)
          {
            if((gotslot[2] != -1) &&
               ((nodes.readEntry(gotslot[2], 0) & 0xFFFF) == 
               Node.ATTRIBUTE_NODE))
              return gotslot[2];
            else
              return -1;
          }
        }
      }
      
      //Otherwise we need to wait for more nodes and try again
      synchronized (this)
      {
        try
        {
          if(DEBUG_WAITS)
            System.out.println("Waiting... getNextAttribute");
          wait();
        }
        catch (InterruptedException e)
        {
          // That's OK, it's as good a time as any to check again
        }
      }
    }
    throw new RuntimeException(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_ERROR_OCCURED, null)); //"Error occured!");
  }

  /**
   * DTM read API: Given a node index, advance to its last child. 
   * If not yet resolved, waits for more nodes to be added to the document and
   * tries again.
   * WARNING: DTM is asymmetric; this operation is resolved by search, and is
   * relatively expensive.
   * @param postition int Index of this node's record.
   * @return int Node-number of last child,
   * or -1 to indicate none exists.
   */
  public final int getLastChild(int position)
  {
    if(TRACE)
      System.out.println("DTM: getLastChild");

    // NOT OPTIMIZED -- it's slow in this model anyway.
    int nt = getNodeType(position);
    if((nt != Node.ELEMENT_NODE) &&
       (nt != Node.DOCUMENT_NODE))
    {
      // System.out.println("early exit - Node type: "+nt);
      return -1;
    }
    
    int lastChild = -1;

    // Walk across the kids until all have been accounted for
    for (int nextkid = getFirstChild(position); 
         nextkid != -1; 
         nextkid = getNextSibling(nextkid))
    {
      lastChild = nextkid;
    }
    return lastChild;
  }

  /**
   * DTM read API: Given a node index, advance to its next sibling.
   * If not yet resolved, waits for more nodes to be added to the document and
   * tries again.
   * @param postition int Index of this node's record.
   * @return int Node-number of next sibling,
   * or -1 to indicate none exists.
   */
  public final int getNextSibling(int position)
  {
    if(TRACE)
      System.out.println("DTM: getNextSibling");

    // 0 is Document; no next-sib.
    if (position == 0)
      return -1;
    while (!m_isError)
    {
      // Next after last in document is Null.
      if (done && (position >= nodes.lastUsed))
        return -1;

      // If an element or attribute, next is as pointed to, IF resolved.
      // (Attributes don't have NextSib in the DOM, but it's a useful concept
      // here)
      int type = nodes.readEntry(position, 0) & 0xFF;
      if (type == Node.ELEMENT_NODE ||
          type == Node.ATTRIBUTE_NODE ||
          type == Node.ENTITY_REFERENCE_NODE)
      { 
        int nextSib = nodes.readEntry(position, 2);
        if(nextSib != 0)
          return nextSib;
      }
      // If not an element, next node is following, IF it
      // has the same parent as this one
      else
      {
        if (position < nodes.lastUsed) 
        {
          int thisParent = nodes.readEntry(position, 1);
          if(nodes.readEntry(position+1, 1) == thisParent)
            return position + 1;
          else
            return -1; // No next kid.
        }
      }
      
      //Otherwise we need to wait for more nodes and try again
      synchronized (this)
      {
        try
        {
          if(DEBUG_WAITS)
            System.out.println("Waiting... getNextSibling");
          wait();
        }
        catch (InterruptedException e)
        {
          // That's OK, it's as good a time as any to check again
        }
      }
    }
    
    ErrorHandler ehandler = this.getErrorHandler();
    
    try
    {
      if(null != ehandler)
        ehandler.fatalError(new SAXParseException(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_PROBLEM_IN_DTM_NEXTSIBLING, null), null));
    }
    catch(SAXException se)
    {
      if(m_throwNewError)
        throw new RuntimeException(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_ERROR_OCCURED, null)); //"Error occured!");
    }
    // m_problemListener.message(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_PROBLEM_IN_DTM_NEXTSIBLING, null)); //"Problem occured in DTM in getNextSibling... trying to recover");

    return -1;
  }
  
  
  /**
   * DTM read API: Given a node index, advance to its next descendant.
   * If not yet resolved, waits for more nodes to be added to the document and
   * tries again.
   * @param postition int Index of this node's record.
   * @return int Node-number of next descendant,
   * or -1 to indicate none exists.
   */
  public final int getNextDescendant(int parentPos, int position)
  {
    if(TRACE)
      System.out.println("DTM: getNextDescendant");

    // 0 is Document; no next-sib.
    if (position == 0)
      return -1;
    
    // System.out.println("nodes.lastUsed: "+nodes.lastUsed);
    // System.out.println("position: "+position);
        
    while (!m_isError)
    {
      // Next after last in document is Null.
      if (done && (position >= nodes.lastUsed))
        return -1;
      
      if (position > parentPos) 
      {
        nodes.readSlot(position+1, gotslot);
        
        // System.out.println("["+Integer.toHexString(gotslot[0])
        //                   +"]["+Integer.toHexString(gotslot[1])
        //                   +"]["+Integer.toHexString(gotslot[2])
        //                   +"]["+Integer.toHexString(gotslot[3])+"]");
        
        if(gotslot[2] != 0)
        {
          // Examine the node in question	
          int type = (gotslot[0] & 0xFFFF);
          if (type == Node.ATTRIBUTE_NODE)
          {
            position+=2; // skip this one and the text value
          }
          else 
          {
            int nextParentPos = gotslot[1];
            if(nextParentPos >= parentPos)
              return position+1;
            else
            {
              if(DEBUG_WAITS)
                System.out.println("Not descendent...");
              return -1; // Not descendent.
            }
          }
        }
        else if(!done)
        {
          //Otherwise we need to wait for more nodes and try again
          synchronized (this)
          {
            try
            {
              if(DEBUG_WAITS)
                System.out.println("Waiting... getNextDescendant");
              wait();
            }
            catch (InterruptedException e)
            {
              // That's OK, it's as good a time as any to check again
            }
          }
        }
        else
        {
          System.out.println("Strange case...");

          // No idea if this is right...
          return -1; // Not descendent.
        }
      }
      else
      {
        position++;
      }
    }
    throw new RuntimeException(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_ERROR_OCCURED, null)); //"Error occured!");
  }

  /**
   * DTM read API: Given a node index, advance to its next sibling.
   * If not yet resolved, waits for more nodes to be added to the document and
   * tries again.
   * @param postition int Index of this node's record.
   * @return int Node-number of next sibling,
   * or -1 to indicate none exists.
   */
  public final int getNextNode(int parentPos, int position)
  {    
    if(TRACE && false)
      System.out.println("DTM: getNextNode");

    while (!m_isError)
    {
      // Next after last in document is Null.
      if (done && (position >= nodes.lastUsed))
        return -1;
      
      int nextOffset = ((nodes.readEntry(position, 1) & 0xFF) == Node.ATTRIBUTE_NODE)? 2 : 1;
      
      if (position >= parentPos) 
      {
        nodes.readSlot(position+nextOffset, gotslot);
        
        if(gotslot[2] != 0)
        {
          // Examine the node in question	
          int nextParentPos = gotslot[1];
          if(nextParentPos >= parentPos)
          {
            return position+nextOffset;
          }
          else
            return -1; // Not descendent.
        }
        else if(!done)
        {
          //Otherwise we need to wait for more nodes and try again
          synchronized (this)
          {
            try
            {
              if(DEBUG_WAITS)
                System.out.println("Waiting... getNextNode");
              wait();
            }
            catch (InterruptedException e)
            {
              // That's OK, it's as good a time as any to check again
            }
          }
        }
        else
        {
          // ??
          return -1;
        }
      }
      else
      {
        position+=nextOffset;
      }
    }
    throw new RuntimeException(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_ERROR_OCCURED, null)); //"Error occured!");
  }
  
  /**
   * DTM read API: Given a node index, find its parent node.
   * @param postition int Index of this node's record.
   * @return int Node-number of parent,
   * or -1 to indicate none exists.
   */
  public final int getParent(int position)
  {
    if(TRACE)
      System.out.println("DTM: getParent");

    // 0 is Document; no parent
    if (position == 0)
      return -1;
    // Off end of array is undefined.
    if (position > nodes.lastUsed)
      return 0;
    else
      return nodes.readEntry(position, 1);
  }
  
  /**
   * DTM read API: Given a node index, find its preceeding sibling.
   * WARNING: DTM is asymmetric; this operation is resolved by search, and is
   * relatively expensive.
   * @param postition int Index of this node's record.
   * @return int Node-number of the previous sib,
   * or -1 to indicate none exists.
   */
  public final int getPreviousSibling(int position)
  {
    if(TRACE)
      System.out.println("DTM: getPreviousSibling");

    // NOT OPTIMIZED -- it's slow in this model anyway.

    // 0 is Document, 1 is first child of doc
    if (position <= 1)
      return -1;

    int parent = nodes.readEntry(position, 1);
    int kid, nextkid;
    if (parent == 0)
      kid = 1;
    else
      kid = getFirstChild(parent);
    while ((nextkid = getNextSibling(kid)) != position)
    {
      kid = nextkid;
      if(kid == -1)
        break;
    }
    return kid;
  }
  
  /**
   * DTM read API: Given a node index, advance to the preceding node.  
   * The preceding axis contains all nodes in the same document as the 
   * context node that are before the context node in document order, 
   * excluding any ancestors and excluding attribute nodes and namespace nodes.
   * @param startPos  The position from where the axes is relative to.
   * @param postition int Index of this node's record.
   * @return int Node-number of preceding sibling,
   * or -1 to indicate none exists.
   */
  public final int getNextPreceding(int startPos, int position)
  {    
    if(TRACE)
      System.out.println("DTM: getNextPreceding");

    while (position > 1) // Preceding never returns the root
    {
      // Decrement position
      position--;
      
      int type = (nodes.readEntry(position, 0) & 0xFFFF);
      if (type == Node.ATTRIBUTE_NODE)
        continue;
	  
	  return nodes.specialFind(startPos, position);
/*
      // We have to look all the way up the ancestor chain
      // to make sure we don't have an ancestor.
      int ancestor = startPos;
      while(ancestor > 0)
      {
        ancestor = nodes.readEntry(ancestor, 1);
        if(ancestor == position)
          break;
      }

      if (ancestor <= 0) 
      {
        return position;
      }
*/	  
    }
    return -1;
  }

  /**
   * DTM read API: Given a node index, advance to the preceding node.  
   * The preceding axis contains all nodes in the same document as the 
   * context node that are before the context node in document order, 
   * excluding any ancestors and excluding attribute nodes and namespace nodes.
   * @param startPos  The position from where the axes is relative to.
   * @param postition int Index of this node's record.
   * @return int Node-number of preceding sibling,
   * or -1 to indicate none exists.
   */
  public final int getPrecedingOrAncestorOrSelf(int position)
  {    
    if(TRACE)
      System.out.println("DTM: getPrecedingOrAncestorOrSelf");

    // Decrement position
    position--;
    
    if(position > 1)
    {
      int type = (nodes.readEntry(position-1, 0) & 0xFFFF);
      if (type == Node.ATTRIBUTE_NODE)
        position--;
    }
    return position;
  }
  
  static final String[] fixednames=
  {
    null,null,              // nothing, Element
    null,"#text",           // Attr, Text
    "#cdata_section",null,  // CDATA, EntityReference
    null,null,              // Entity, PI
    "#comment","#document", // Comment, Document
    null,"#document-fragment", // Doctype, DocumentFragment
    null};                  // Notation

  /**
   * DTM read API: Given a node index, return its DOM-style node name.
   * @param postition int Index of this node's record.
   * @return String Name of this node.
   */
  public final String getNodeName(int position)
  {
    // int strIndex = nodes.readEntry(position, 3);
    // return fStringPool.toString( fStringPool.getFullNameForQName(strIndex) );
    if(position==0)
      return fixednames[Node.DOCUMENT_NODE];

    nodes.readSlot(position, gotslot);
    String name=fixednames[gotslot[0]&0xff];
    if(name==null)
      name=intToString(gotslot[3]);
    return name;
  }
  
  /**
   * DTM read API: Given a node index, return its DOM-style localname.
   * (As defined in Namespaces, this is the portion of the name after any
   * colon character)
   * @param postition int Index of this node's record.
   * @return String Local name of this node.
   */
  public final String getLocalName(int position)
  {
    // return  fStringPool.toString( fStringPool.getLocalPartForQName(nodes.readEntry(position, 3)) );
    String name=getNodeName(position);
    if(null != name)
    {
      int colonpos = name.indexOf(':');
      return (colonpos < 0) ? name : name.substring(colonpos+1);
    }
    else
    {
      return null;
    }
  }
  
  /**
   * DTM read API: Given a node index, return its DOM-style name prefix.
   * (As defined in Namespaces, this is the portion of the name before any
   * colon character)
   * @param postition int Index of this node's record.
   * @return String prefix of this node's name, or null if no explicit
   * namespace prefix was given.
   */
  public final String getPrefix(int position)
  {
    // return  fStringPool.toString( fStringPool.getFullNameForQName(nodes.readEntry(position, 3)) );
    String name=getNodeName(position);
    int colonpos = name.indexOf(':');
    return (colonpos <= 0) ? null : name.substring(0, colonpos);
  }
  
  /**
   * DTM read API: Given a node index, return its DOM-style namespace URI
   * (As defined in Namespaces, this is the declared URI which this node's
   * prefix -- or default in lieu thereof -- was mapped to.)
   * @param postition int Index of this node's record.
   * @return String URI value of this node's namespace, or null if no
   * namespace was resolved.
   */
  public final String getNamespaceURI(int position)
  {
    if(position==0)
      return null;
    int w0=nodes.readEntry(position, 0);
    int type = (w0&0xff);
    if((type != Node.ELEMENT_NODE) && (type != Node.ATTRIBUTE_NODE))
      return null;
    else 
      return intToString(w0>>16);
  }

  /**
   * DTM read API: Given a node index, return its node value. This is mostly
   * as defined by the DOM, but may ignore some conveniences.
   * <p>
   * @param postition int Index of this node's record.
   * @return String Value of this node, or null if not 
   * meaningful for this node type.
   */
  public final String getNodeValue(int position)
  {
    nodes.readSlot(position, gotslot);
    int nodetype=gotslot[0] & 0xFF;
    String value=null;
    
    switch(nodetype)
    {
    case Node.TEXT_NODE:
    case Node.CDATA_SECTION_NODE: // We handle as flagged Text...
      value=intToString(gotslot[2]);
      break;
    case Node.PROCESSING_INSTRUCTION_NODE:
    case Node.COMMENT_NODE:
      value=intToString(gotslot[2]);
      break;
    case Node.ATTRIBUTE_NODE: // Value is in kid(s)
    case Node.ELEMENT_NODE:
    case Node.ENTITY_REFERENCE_NODE:
    default:
      break;
    }
    return value; // DO SOMETHING FOR SAX?
  }

  /**
   * DTM read API: Given a node index, return its DOM-style node type.
   * @param postition int Index of this node's record.
   * @return int Node type, as per the DOM's Node._NODE constants.
   */
  public final int getNodeType(int position)
  {
    return nodes.readEntry(position,0)&0xff;
  }
  
  /**
   * DTM read API: Given a node index, indicate whether the parser marked
   * it as an ignorable-whitespace text node.
   * @param postition int Index of this node's record.
   * @return boolean true iff the node was created via an ignorableWhitespace
   * event.
   */
  public final boolean isIgnorableText(int position)
  {
    return (position == 0) 
           ? false
             : ( (nodes.readEntry(position,0)&0xffff) ==
                 (Node.TEXT_NODE | TEXT_IGNORABLE) );
  }

  /**
   * DTM development API: Given a node index, write the node's contents
   * in extremely terse form onto Standard Output.
   * @param postition int Index of this node's record.
   */
  public final void display(int position)
  {
    if(position == 0)
      System.out.println("0:\tDocument (implicit first child = 1)");
    else
    {
      System.out.println(position+":\t"+getNodeName(position)+
                         ", parent="+getParent(position)+
                         ", nextsib="+getNextSibling(position) );
      String v=getNodeValue(position);
      if(v!=null)
        System.out.println("\tvalue="+v);
    }
  }
  /**
   * DTM development API: display() all the nodes in the DTM.
   * @see display
   *
   */
  public final void dump()
  {
    for(int i=0;i<=nodes.lastUsed;++i)
      display(i);
  }

  /**
   * Internal routine: Add a string to the symbol table. Attempts to work
   * in both SAX and XML4J-specific modes.
   * @param s String to be added
   * @return int Index number assigned to this string
   */
  private final int stringToInt(String s)
  {
    // Used only for SAX-based input
    // When recieving from XML4J, numbers were precalculated
    if(fStringPool==null)
    {
      Integer iobj=(Integer)symbolTable.get(s);
      if (iobj==null)
      {
        symbolList.addElement(s);
        iobj=new Integer(symbolList.size());
        symbolTable.put(s,iobj);
      }
      return iobj.intValue();
    }
    else
      return fStringPool.addSymbol(s);
  }
  /**
   * Internal routine: Retrieve a string from the symbol table. Attempts to work
   * in both SAX and XML4J-specific modes.
   * @param int Index number of desired string
   * @return String String represented by that number.
   */
  private final String intToString(int i)
  {
    if(fStringPool==null)
      return (String)symbolList.elementAt(i);
    else
      return fStringPool.toString(i);
  }
  
  /**
   * Internal routine: Look up a namespace in the current parent-element's
   * context, by consulting the stacked hashtables.
   * @param prefix String prefix to be resolved
   * @return String Namespace URI which that prefix currently points to.
   */
  private final String resolveNamespace(String prefix)
  {
    if(prefix==null || prefix.length() == 0)
      prefix=DEFAULT_PREFIX_STR;
    
    String nsuri = namespaceTable.get((prefix==null || prefix.length() == 0) ? 
                                      DEFAULT_PREFIX_STR : prefix);
    return (null == nsuri) ? "" : nsuri;
  }
  
  /**
   * Internal routine: Look up a namespace in the current parent-element's
   * context, by consulting the stacked hashtables.
   * @param prefix String prefix to be resolved
   * @return String Namespace URI which that prefix currently points to.
   */
  private final boolean isDefaultNamespaceInEffect()
  {
    return namespaceTable.containsKey(DEFAULT_PREFIX_STR);
  }

  /**
   * Signal the XML declaration of a document
   *
   * @param version the handle in the string pool for the version number
   * @param encoding the handle in the string pool for the encoding
   * @param standalong the handle in the string pool for the standalone value
   * @exception java.lang.Exception
   */
  public void xmlDecl(int version, int encoding, int standalone) 
    throws Exception
  {
  }
  
  /**
   * Signal the Text declaration of an external entity.
   *
   * @exception java.lang.Exception
   */
  public void textDecl(int version, int encoding) throws Exception
  {
  }

  /**
   * This feature determines whether entity references within
   * the document are included in the document tree as
   * EntityReference nodes.
   * <p>
   * Note: The children of the entity reference are always
   * added to the document. This feature only affects
   * whether an EntityReference node is also included
   * as the parent of the entity reference children.
   *
   * @param create True to create entity reference nodes; false
   *               to only insert the entity reference children.
   *
   * @see #getCreateEntityReferenceNodes
   */
  protected void setCreateEntityReferenceNodes(boolean create) 
  {
    fCreateEntityReferenceNodes = create;
  }

  /**
   * @see #setCreateEntityReferenceNodes
   */
  public final boolean getCreateEntityReferenceNodes() 
  {
    return fCreateEntityReferenceNodes;
  }
  
  /*
  class NullSchemaValidator extends org.apache.xerces.validators.schema.XSchemaValidator
  {
    public NullSchemaValidator(org.apache.xerces.utils.StringPool stringPool, 
                               org.apache.xerces.framework.XMLErrorReporter errorReporter, 
                               XMLEntityHandler entityHandler) 
    {
      super(stringPool, errorReporter, entityHandler);
    }
    
    public final void loadSchema(String uri) {}
    public final void loadSchema(InputSource is) {}
  }
  */

  //==========================================================
  // SECTION: Diagnostics
  //==========================================================
  private final void ____Diagnostics____(){}
  
  public void dumpDTM()
  {
    try
    {
      // java.io.File file = new java.io.File("dtmdump.txt");
      java.io.FileOutputStream os = new java.io.FileOutputStream("dtmdump.txt");
      java.io.PrintWriter pw = new java.io.PrintWriter(os);
      for(int i = 0;i < nodes.lastUsed;i++)
      {
        nodes.readSlot(i, gotslot);
        pw.print(i+") ");
        pw.print("[type: "+gotslot[0]+" (");
        int type = (gotslot[0] & 0x00FF);
        switch(type)
        {
        case Node.ATTRIBUTE_NODE: pw.print("ATTRIBUTE_NODE"); break;
        case Node.CDATA_SECTION_NODE: pw.print("CDATA_SECTION_NODE"); break;
        case Node.COMMENT_NODE: pw.print("COMMENT_NODE"); break;
        case Node.DOCUMENT_FRAGMENT_NODE: pw.print("DOCUMENT_FRAGMENT_NODE"); break;
        case Node.DOCUMENT_NODE: pw.print("DOCUMENT_NODE"); break;
        case Node.DOCUMENT_TYPE_NODE: pw.print("DOCUMENT_TYPE_NODE"); break;
        case Node.ELEMENT_NODE: pw.print("ELEMENT_NODE"); break;
        case Node.ENTITY_NODE: pw.print("ENTITY_NODE"); break;
        case Node.ENTITY_REFERENCE_NODE: pw.print("ENTITY_REFERENCE_NODE"); break;
        case Node.NOTATION_NODE: pw.print("NOTATION_NODE"); break;
        case Node.PROCESSING_INSTRUCTION_NODE: pw.print("PROCESSING_INSTRUCTION_NODE"); break;
        case Node.TEXT_NODE: pw.print("TEXT_NODE"); break;
        default: pw.print("???");
        }
        pw.print(")]");
        pw.print("[parent: "+gotslot[1]+"]");
        if(Node.TEXT_NODE == type)
        {
          pw.print("[data: "+gotslot[2]+"]");
          // pw.print("[2]: "+gotslot[2]+" (char data: "+intToString(gotslot[2])+")");
          // pw.print("[3]: "+gotslot[3]+" (empty)");
        }
        else
        {
          pw.print("[next: "+gotslot[2]+"]");
          pw.print("[name: "+gotslot[3]+", "+intToString(gotslot[3])+"]");
        }
        pw.println("");
      }
      pw.close();
    }
    catch(java.io.IOException ioe)
    {
      System.out.println("Could not dump DTM");
    }

  }

}