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
package org.apache.xalan.transformer;

// Java imports
import java.util.Stack;
import java.util.Vector;
import java.util.Enumeration;

import java.io.StringWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

// Xalan imports
import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.stree.SourceTreeHandler;
import org.apache.xalan.templates.Constants;
import org.apache.xalan.templates.ElemAttributeSet;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.StylesheetComposed;
import org.apache.xalan.templates.ElemForEach;
import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xalan.templates.Stylesheet;
import org.apache.xalan.templates.ElemWithParam;
import org.apache.xalan.templates.ElemSort;
import org.apache.xalan.templates.AVT;
import org.apache.xalan.templates.ElemVariable;
import org.apache.xalan.templates.ElemParam;
import org.apache.xalan.templates.ElemCallTemplate;
import org.apache.xalan.templates.ElemTemplate;
import org.apache.xalan.templates.TemplateList;
import org.apache.xalan.trace.TraceManager;
import org.apache.xalan.utils.DOMBuilder;
import org.apache.xalan.utils.NodeVector;
import org.apache.xalan.utils.BoolStack;
import org.apache.xalan.utils.QName;
import org.apache.xalan.utils.PrefixResolver;
import org.apache.xalan.utils.ObjectPool;
import org.apache.xpath.XPathContext;
import org.apache.xpath.NodeSet;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.XPath;
import org.apache.xpath.objects.XString;
import org.apache.xpath.objects.XRTreeFrag;
import org.apache.xpath.Arg;
import org.apache.xpath.XPathAPI;
import org.apache.xpath.VariableStack;
import org.apache.xpath.compiler.XPathParser;
import org.apache.xpath.axes.ContextNodeList;

// Back support for liaisons
import org.apache.xpath.DOM2Helper;

// Serializer Imports
import org.apache.serialize.OutputFormat;
import org.apache.serialize.Serializer;
import org.apache.serialize.SerializerFactory;
import org.apache.serialize.Method;

// DOM Imports
import org.w3c.dom.Attr;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Document;
import org.w3c.dom.traversal.NodeIterator;
import org.w3c.dom.Node;

// SAX2 Imports
import org.xml.sax.ContentHandler;
import org.xml.sax.helpers.XMLFilterImpl;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.Locator;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

// TRaX Imports
import org.apache.trax.Result;
import org.apache.trax.Transformer;
import org.apache.trax.TransformException;
import org.apache.trax.URIResolver;

// Imported JAVA API for XML Parsing 1.0 classes
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * <meta name="usage" content="advanced"/>
 * The Xalan workhorse -- Collaborates with the XPath xcontext, the DOM,
 * and the XPath engine, to transform a source tree of nodes into a result tree
 * according to instructions and templates specified by a stylesheet tree.
 * We suggest you use one of the
 * static XSLTProcessorFactory getProcessor() methods to instantiate the processor
 * and return an interface that greatly simplifies the process of manipulating
 * TransformerImpl.
 *
 * <p>The methods <code>process(...)</code> are the primary public entry points.
 * The best way to perform transformations is to use the
 * {@link XSLTProcessor#process(XSLTInputSource, XSLTInputSource, XSLTResultTarget)} method,
 * but you may use any of process methods defined in TransformerImpl.</p>
 *
 * <p>Please note that this class is not safe per instance over multiple
 * threads.  If you are in a multithreaded environment, you should
 * keep a pool of these objects, or create a new one each time.  In a
 * multithreaded environment, the right way to do things is to create a
 * StylesheetRoot via processStylesheet, and then reuse this object
 * over multiple threads.</p>
 *
 * <p>If you reuse the processor instance, you should call reset() between transformations.</p>
 * @see XSLTProcessorFactory
 * @see XSLTProcessor
 */
public class TransformerImpl extends XMLFilterImpl
        implements Transformer, Runnable, TransformState
{
  /**
   * True if the parser events should be on the main thread,
   * false if not.  Experemental.  Can not be set right now.
   */
  private boolean m_parserEventsOnMain = true;

  /** The thread that the transformer is running on.          */
  private Thread m_transformThread;

  /** The base URL of the source tree.          */
  private String m_urlOfSource = null;

  /**
   * The output format object set by the user.  May be null.
   */
  private OutputFormat m_outputFormat;

  /** The output serializer         */
  private Serializer m_serializer;
  
  /**
   * The content handler for the source input tree.
   */
  ContentHandler m_inputContentHandler;

  /**
   * Use member variable to store param variables as they're
   * being created, use member variable so we don't
   * have to create a new vector every time.
   */
  private Vector m_newVars = new Vector();

  /** The JAXP Document Builder, mainly to create Result Tree Fragments. */
  DocumentBuilder m_docBuilder = null;

  /** A pool of ResultTreeHandlers, for serialization of a subtree to text.
   *  Please note that each of these also holds onto a Text Serializer.  */
  private ObjectPool m_textResultHandlerObjectPool =
    new ObjectPool(org.apache.xalan.transformer.ResultTreeHandler.class);

  /** Related to m_textResultHandlerObjectPool, this is a pool of 
   * StringWriters, which are passed to the Text Serializers.
   * (I'm not sure if this is really needed any more.  -sb)      */
  private ObjectPool m_stringWriterObjectPool =
    new ObjectPool(java.io.StringWriter.class);

  /** A static text format object, which can be used over and 
   * over to create the text serializers.    */
  private static OutputFormat m_textformat;

  static
  {
    // Synchronize??
    m_textformat = new OutputFormat();

    m_textformat.setMethod("text");
    m_textformat.setPreserveSpace(true);
  }
  
  /** A node vector used as a stack to track the current 
   * ElemTemplateElement.  Needed for the 
   * org.apache.xalan.transformer.TransformState interface,  
   * so a tool can discover the calling template. */
  private NodeVector m_currentTemplateElements = new NodeVector(64);

  /** A node vector used as a stack to track the current 
   * ElemTemplate that was matched, as well as the node that 
   * was matched.  Needed for the 
   * org.apache.xalan.transformer.TransformState interface,  
   * so a tool can discover the matched template, and matched 
   * node. */
  private NodeVector m_currentMatchTemplates = new NodeVector();

  /**
   * The root of a linked set of stylesheets.
   */
  private StylesheetRoot m_stylesheetRoot = null;

  /**
   * If this is set to true, do not warn about pattern
   * match conflicts.
   */
  private boolean m_quietConflictWarnings = true;

  /**
   * The liason to the XML parser, so the XSL processor
   * can handle included files, and the like, and do the
   * initial parse of the XSL document.
   */
  private XPathContext m_xcontext;

  /**
   * Object to guard agains infinite recursion when
   * doing queries.
   */
  private StackGuard m_stackGuard = new StackGuard();

  /**
   * Output handler to bottleneck SAX events.
   */
  private ResultTreeHandler m_resultTreeHandler;

  /** The key manager, which manages xsl:keys.   */
  private KeyManager m_keyManager = new KeyManager();

  /**
   * Stack for the purposes of flagging infinite recursion with
   * attribute sets.
   */
  private Stack m_attrSetStack = null;

  /**
   * The table of counters for xsl:number support.
   * @see ElemNumber
   */
  private CountersTable m_countersTable = null;

  /**
   * Is > 0 when we're processing a for-each.
   */
  private BoolStack m_currentTemplateRuleIsNull = new BoolStack();

  /** The message manager, which manages error messages, warning 
   * messages, and other types of message events.   */
  private MsgMgr m_msgMgr;

  /**
   * This is a compile-time flag to turn off calling
   * of trace listeners. Set this to false for optimization purposes.
   */
  public static boolean S_DEBUG = false;

  /**
   * The trace manager.
   */
  private TraceManager m_traceManager = new TraceManager(this);

  /** If the transform thread throws an exception, the exception needs to 
   * be stashed away so that the main thread can pass it on to the 
   * client. */
  private Exception m_exceptionThrown = null;

  /** The InputSource for the source tree, which is needed if the 
   * parse thread is not the main thread, in order for the parse 
   * thread's run method to get to the input source.   
   * (Delete this if reversing threads is outlawed. -sb)    */
  private InputSource m_xmlSource;

  /** This is needed for support of setSourceTreeDocForThread(Node doc),
   * which must be called in order for the transform thread's run 
   * method to obtain the root of the source tree to be transformed.     */
  private Node m_doc;

  /**
   * If the the transform is on the secondary thread, we
   * need to know when it is done, so we can return.
   */
  boolean m_isTransformDone = false;

  //==========================================================
  // SECTION: Constructors
  //==========================================================

  /**
   * Construct a TransformerImpl.
   *
   * @param stylesheet The root of the stylesheet tree.
   */
  public TransformerImpl(StylesheetRoot stylesheet)
  {

    setStylesheet(stylesheet);
    setXPathContext(new XPathContext(this));
    getXPathContext().setNamespaceContext(stylesheet);
  }

  /**
   * Reset the state.  This needs to be called after a process() call
   * is invoked, if the processor is to be used again.
   */
  public void reset()
  {

    // I need to look more carefully at which of these really
    // needs to be reset.
    m_countersTable = null;
    m_stackGuard = new StackGuard();

    getXPathContext().reset();
    m_currentTemplateElements.removeAllElements();
    m_currentMatchTemplates.removeAllElements();

    m_resultTreeHandler = null;
    m_keyManager = new KeyManager();
    m_attrSetStack = null;
    m_countersTable = null;
    m_currentTemplateRuleIsNull = new BoolStack();
    m_xmlSource = null;
    m_doc = null;
    m_isTransformDone = false;
    m_inputContentHandler = null;
  }

  // ========= Transformer Interface Implementation ==========

  /**
   * Transform a document.
   *
   * @param input The input source for the document entity.
   *
   * @param xmlSource A SAX InputSource object, must not be null.
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @exception java.io.IOException An IO exception from the parser,
   *            possibly from a byte stream or character stream
   *            supplied by the application.
   * @see org.xml.sax.XMLReader#parse(org.xml.sax.InputSource)
   */
  public void parse(InputSource xmlSource) throws SAXException, IOException
  {
    transform(xmlSource);
  }

  /**
   * <meta name="usage" content="experimental"/>
   * Get true if the parser events should be on the main thread,
   * false if not.  Experimental.  Can not be set right now.
   *
   * @return true if the parser events should be on the main thread,
   * false if not.
   */
  public boolean isParserEventsOnMain()
  {
    return m_parserEventsOnMain;
  }

  /**
   * <meta name="usage" content="internal"/>
   * Get the thread that the transform process is on.
   *
   * @return The thread that the transform process is on, or null.
   */
  public Thread getTransformThread()
  {
    return m_transformThread;
  }

  /**
   * <meta name="usage" content="internal"/>
   * Get the thread that the transform process is on.
   *
   * @param t The transform thread, may be null.
   */
  public void setTransformThread(Thread t)
  {
    m_transformThread = t;
  }

  /**
   * Process the source tree to SAX parse events.
   * @param xmlSource  The input for the source tree.
   *
   * @throws TransformException
   */
  public void transform(InputSource xmlSource) throws TransformException
  {

    if (null != xmlSource.getSystemId())
      m_urlOfSource = xmlSource.getSystemId();

    try
    {

      // Get an already set XMLReader, or create one.
      XMLReader reader = this.getParent();

      if (null == reader)
      {
        reader = XMLReaderFactory.createXMLReader();
      }

      try
      {
        reader.setFeature("http://xml.org/sax/features/namespace-prefixes",
                          true);
        reader.setFeature("http://apache.org/xml/features/validation/dynamic",
                          true);
      }
      catch (SAXException se)
      {

        // What can we do?
        // TODO: User diagnostics.
      }

      // Get the input content handler, which will handle the 
      // parse events and create the source tree. 
      ContentHandler inputHandler = getInputContentHandler();

      reader.setContentHandler(inputHandler);
      reader.setProperty("http://xml.org/sax/properties/lexical-handler",
                         inputHandler);

      // Set the reader for cloning purposes.
      getXPathContext().setPrimaryReader(reader);

      this.m_exceptionThrown = null;

      if (inputHandler instanceof SourceTreeHandler)
      {
        SourceTreeHandler sth = (SourceTreeHandler) inputHandler;

        sth.setInputSource(xmlSource);
        sth.setUseMultiThreading(true);

        Node doc = sth.getRoot();

        if (null != doc)
        {
          getXPathContext().getSourceTreeManager().putDocumentInCache(doc,
                  xmlSource);

          m_xmlSource = xmlSource;
          m_doc = doc;

          if (isParserEventsOnMain())
          {
            m_isTransformDone = false;

            getXPathContext().getPrimaryReader().parse(xmlSource);
          }
          else
          {
            Thread t = new Thread(this);

            t.start();
            transformNode(doc);
          }
        }
      }
      else
      {

        // ??
        reader.parse(xmlSource);
      }

      // Kick off the parse.  When the ContentHandler gets 
      // the startDocument event, it will call transformNode( node ).
      // reader.parse( xmlSource );
      // This has to be done to catch exceptions thrown from 
      // the transform thread spawned by the STree handler.
      Exception e = getExceptionThrown();

      if (null != e)
      {
        if (e instanceof org.apache.trax.TransformException)
          throw (org.apache.trax.TransformException) e;
        else if (e instanceof org.apache.xalan.utils.WrappedRuntimeException)
          throw new org.apache.trax.TransformException(
            ((org.apache.xalan.utils.WrappedRuntimeException) e).getException());
        else
        {
          throw new org.apache.trax.TransformException(e);
        }
      }
      else if (null != m_resultTreeHandler)
      {
        m_resultTreeHandler.endDocument();
      }
    }
    catch (org.apache.xalan.utils.WrappedRuntimeException wre)
    {
      Throwable throwable = wre.getException();

      while (throwable
             instanceof org.apache.xalan.utils.WrappedRuntimeException)
      {
        throwable =
          ((org.apache.xalan.utils.WrappedRuntimeException) throwable).getException();
      }

      throw new TransformException(wre.getException());
    }
    catch (SAXException se)
    {
      se.printStackTrace();

      if (se instanceof TransformException)
        throw (TransformException) se;
      else
        throw new TransformException(se);
    }
    catch (IOException ioe)
    {
      throw new TransformException(ioe);
    }
    finally
    {
      reset();
    }
  }

  /**
   * Get the base URL of the source.
   *
   * @return The base URL of the source tree, or null. 
   */
  public String getBaseURLOfSource()
  {
    return m_urlOfSource;
  }

  /**
   * Get the value of a property.  Recognized properties are:
   *
   * <p>"http://xml.apache.org/xslt/sourcebase" - the base URL for the
   * source, which is needed when pure SAX ContentHandler transformation
   * is to be done.</p>
   *
   * @param name The property name, which is a fully-qualified URI.
   *
   * @return The value of the property, or null if not found.
   * @exception org.xml.sax.SAXNotRecognizedException When the
   *            XMLReader does not recognize the property name.
   * @exception org.xml.sax.SAXNotSupportedException When the
   *            XMLReader recognizes the property name but
   *            cannot set the requested value.
   *
   * @throws SAXNotRecognizedException
   * @throws SAXNotSupportedException
   */
  public Object getProperty(String name)
          throws SAXNotRecognizedException, SAXNotSupportedException
  {

    if (name.equals("http://xml.apache.org/xslt/sourcebase"))
      return name;
    else
      return super.getProperty(name);
  }

  /**
   * Set the value of a property.  Recognized properties are:
   *
   * <p>"http://xml.apache.org/xslt/sourcebase" - the base URL for the
   * source, which is needed when pure SAX ContentHandler transformation
   * is to be done.</p>
   *
   * @param name The property name, which is a fully-qualified URI.
   * @param value The requested value for the property.
   * @exception org.xml.sax.SAXNotRecognizedException When the
   *            XMLReader does not recognize the property name.
   * @exception org.xml.sax.SAXNotSupportedException When the
   *            XMLReader recognizes the property name but
   *            cannot set the requested value.
   *
   * @throws SAXNotRecognizedException
   * @throws SAXNotSupportedException
   */
  public void setProperty(String name, Object value)
          throws SAXNotRecognizedException, SAXNotSupportedException
  {

    if (name.equals("http://xml.apache.org/xslt/sourcebase"))
      m_urlOfSource = (String) value;
    else
      super.getProperty(name);
  }

  /**
   * <meta name="usage" content="internal"/>
   * Process the an input source to a DOM node.  FOR INTERNAL USE ONLY.
   * 
   * @param xmlSource  The input for the source tree.
   *
   * @return The Node result of the parse, never null.
   *
   * @throws TransformException
   */
  public Node parseToNode(InputSource xmlSource) throws TransformException
  {

    // Duplicate code from above... but slightly different.  
    // TODO: Work on this...
    if (null != xmlSource.getSystemId())
      m_urlOfSource = xmlSource.getSystemId();

    Node doc = null;
      try
      {
        // Get an already set XMLReader, or create one.
        XMLReader reader = this.getParent();

        if (null == reader)
        {
          reader = XMLReaderFactory.createXMLReader();
        }

        try
        {
          reader.setFeature("http://xml.org/sax/features/namespace-prefixes",
                            true);
          reader.setFeature(
                            "http://apache.org/xml/features/validation/dynamic", true);
        }
        catch (SAXException se)
        {

          // What can we do?
          // TODO: User diagnostics.
        }

        // TODO: Handle Xerces DOM parser.
        // Get the input content handler, which will handle the 
        // parse events and create the source tree.
        ContentHandler inputHandler = getInputContentHandler();
        Class inputHandlerClass = ((Object) inputHandler).getClass();

        inputHandler = (ContentHandler) inputHandlerClass.newInstance();

        reader.setContentHandler(inputHandler);
        reader.setProperty("http://xml.org/sax/properties/lexical-handler",
                           inputHandler);
        getXPathContext().setPrimaryReader(reader);

        // ...and of course I need a standard way to get a node...
        if (inputHandler instanceof org.apache.xalan.stree.SourceTreeHandler)
        {

          // Kick off the parse.  When the ContentHandler gets 
          // the startDocument event, it will call transformNode( node ).
          reader.parse(xmlSource);

          doc =
               ((org.apache.xalan.stree.SourceTreeHandler) inputHandler).getRoot();
        }
      }
      catch (java.lang.IllegalAccessException iae)
      {
        throw new TransformException(iae);
      }
      catch (InstantiationException ie)
      {
        throw new TransformException(ie);
      }
      catch (SAXException se)
      {
        throw new TransformException(se);
      }
      catch (IOException ioe)
      {
        throw new TransformException(ioe);
      }

    return doc;
  }

  /**
   * Create a result ContentHandler from a Result object, based 
   * on the current OutputFormat.
   *
   * @param outputTarget Where the transform result should go, 
   * should not be null.
   *
   * @return A valid ContentHandler that will create the 
   * result tree when it is fed SAX events.
   *
   * @throws TransformException
   */
  public ContentHandler createResultContentHandler(Result outputTarget)
          throws TransformException
  {
    return createResultContentHandler(outputTarget, getOutputFormat());
  }

  /**
   * Create a ContentHandler from a Result object and an OutputFormat.
   *
   * @param outputTarget Where the transform result should go, 
   * should not be null.
   * @param format The OutputFormat object that will contain 
   * instructions on how to serialize the output.
   *
   * @return A valid ContentHandler that will create the 
   * result tree when it is fed SAX events.
   *
   * @throws TransformException
   */
  public ContentHandler createResultContentHandler(
          Result outputTarget, OutputFormat format) throws TransformException
  {

    ContentHandler handler;

    // If the Result object contains a Node, then create 
    // a ContentHandler that will add nodes to the input node.
    Node outputNode = outputTarget.getNode();

    if (null != outputNode)
    {
      short type = outputNode.getNodeType();
      Document doc = (Node.DOCUMENT_NODE == type)
                     ? (Document) outputNode : outputNode.getOwnerDocument();

      handler = (Node.DOCUMENT_FRAGMENT_NODE == type)
                ? new DOMBuilder(doc, (DocumentFragment) outputNode)
                : new DOMBuilder(doc, outputNode);
    }

    // Otherwise, create a ContentHandler that will serialize the 
    // result tree to either a stream or a writer.
    else
    {
      String method = format.getMethod();

      if (null == method)
        method = Method.XML;

      try
      {
        Serializer serializer = SerializerFactory.getSerializer(format);

        if (null != outputTarget.getCharacterStream())
          serializer.setWriter(outputTarget.getCharacterStream());
        else
          serializer.setOutputStream(outputTarget.getByteStream());

        handler = serializer.asContentHandler();

        this.setSerializer(serializer);
      }
      catch (UnsupportedEncodingException uee)
      {
        throw new TransformException(uee);
      }
      catch (IOException ioe)
      {
        throw new TransformException(ioe);
      }
    }

    return handler;
  }

  /**
   * Process the source tree to the output result.
   * @param xmlSource  The input for the source tree.
   * @param outputTarget The output source target.
   *
   * @throws TransformException
   */
  public void transform(InputSource xmlSource, Result outputTarget)
          throws TransformException
  {

    ContentHandler handler = createResultContentHandler(outputTarget);

    this.setContentHandler(handler);
    transform(xmlSource);
  }

  /**
   * Process the source node to the output result, if the
   * processor supports the "http://xml.org/trax/features/dom/input"
   * feature.
   * @param node  The input source node, which can be any valid DOM node.
   * @param outputTarget The output source target.
   *
   * @throws TransformException
   */
  public void transformNode(Node node, Result outputTarget)
          throws TransformException
  {

    ContentHandler handler = createResultContentHandler(outputTarget);

    this.setContentHandler(handler);
    transformNode(node);
  }

  /**
   * Process the source node to the output result, if the
   * processor supports the "http://xml.org/trax/features/dom/input"
   * feature.
   * @param node  The input source node, which can be any valid DOM node.
   * @param outputTarget The output source target.
   *
   * @throws TransformException
   */
  public void transformNode(Node node) throws TransformException
  {

    try
    {
      pushGlobalVars(node);

      // ==========
      // Give the top-level templates a chance to pass information into 
      // the context (this is mainly for setting up tables for extensions).
      StylesheetRoot stylesheet = this.getStylesheet();
      int n = stylesheet.getGlobalImportCount();

      for (int i = 0; i < n; i++)
      {
        StylesheetComposed imported = stylesheet.getGlobalImport(i);

        imported.runtimeInit(this);

        for (ElemTemplateElement child = imported.getFirstChildElem();
                child != null; child = child.getNextSiblingElem())
        {
          child.runtimeInit(this);
        }

        int includedCount = imported.getIncludeCountComposed();

        for (int j = 0; j < includedCount; j++)
        {
          Stylesheet included = imported.getIncludeComposed(j);

          included.runtimeInit(this);

          for (ElemTemplateElement child = included.getFirstChildElem();
                  child != null; child = child.getNextSiblingElem())
          {
            child.runtimeInit(this);
          }
        }
      }

      // ===========
      this.applyTemplateToNode(null, null, node, null);

      if (null != m_resultTreeHandler)
      {
        m_resultTreeHandler.endDocument();
      }
    }
    catch (SAXException se)
    {
      if (se instanceof org.apache.trax.TransformException)
        throw (org.apache.trax.TransformException) se;
      else
        throw new TransformException(se);
    }
  }

  /**
   * Get a SAX2 ContentHandler for the input.
   * @return A valid ContentHandler, which should never be null, as
   * long as getFeature("http://xml.org/trax/features/sax/input")
   * returns true.
   */
  public ContentHandler getInputContentHandler()
  {

    if (null == m_inputContentHandler)
      m_inputContentHandler = new SourceTreeHandler(this);

    return m_inputContentHandler;
  }

  /**
   * Get a SAX2 DeclHandler for the input.
   * @return A valid DeclHandler, which should never be null, as
   * long as getFeature("http://xml.org/trax/features/sax/input")
   * returns true.
   */
  public DeclHandler getInputDeclHandler()
  {

    if (m_inputContentHandler instanceof DeclHandler)
      return (DeclHandler) m_inputContentHandler;
    else
      return null;
  }

  /**
   * Get a SAX2 LexicalHandler for the input.
   * @return A valid LexicalHandler, which should never be null, as
   * long as getFeature("http://xml.org/trax/features/sax/input")
   * returns true.
   */
  public LexicalHandler getInputLexicalHandler()
  {

    if (m_inputContentHandler instanceof LexicalHandler)
      return (LexicalHandler) m_inputContentHandler;
    else
      return null;
  }

  /**
   * Set the output properties for the transformation.  These
   * properties will override properties set in the templates
   * with xsl:output.
   *
   * @see org.xml.org.apache.serialize.OutputFormat
   *
   * @param oformat A valid OutputFormat object (which will 
   * not be mutated), or null.
   */
  public void setOutputFormat(OutputFormat oformat)
  {
    m_outputFormat = oformat;
  }

  /**
   * Get the output properties used for the transformation.
   *
   * @see org.xml.org.apache.serialize.OutputFormat
   *
   * @return the output format that was set by the user, 
   * otherwise the output format from the stylesheet.
   */
  public OutputFormat getOutputFormat()
  {

    // Get the output format that was set by the user, otherwise get the 
    // output format from the stylesheet.
    OutputFormat format = (null == m_outputFormat)
                          ? getStylesheet().getOutputComposed()
                          : m_outputFormat;

    return format;
  }

  /**
   * <meta name="usage" content="internal"/>
   * Get the current serializer in use, which may well not
   * be the main serializer (for instance, this may well be 
   * a text serializer for string creation from templates).
   *
   * @return The current serializer, or null if there is none.
   */
  public Serializer getSerializer()
  {
    return m_serializer;
  }

  /**
   * <meta name="usage" content="internal"/>
   * Set the current serializer.
   *
   * @param s The current serializer, or null.
   */
  public void setSerializer(Serializer s)
  {
    m_serializer = s;
    ;
  }

  /**
   * Set a parameter for the templates.
   * 
   * @param name The name of the parameter.
   * @param namespace The namespace of the parameter.
   * @param value The value object.  This can be any valid Java object
   * -- it's up to the processor to provide the proper
   * coersion to the object, or simply pass it on for use
   * in extensions.
   */
  public void setParameter(String name, String namespace, Object value)
  {

    VariableStack varstack = getXPathContext().getVarStack();
    QName qname = new QName(namespace, name);
    XObject xobject = XObject.create(value);

    varstack.pushVariable(qname, xobject);
  }

  /**
   * Reset the parameters to a null list.
   */
  public void resetParameters(){}

  /**
   * Given a template, search for
   * the arguments and push them on the stack.  Also,
   * push default arguments on the stack.
   * You <em>must</em> call popContext() when you are
   * done with the arguments.
   *
   * @param xctxt The XPath runtime state for this transformation.
   * @param xslCallTemplateElement The xsl:call-template element.
   * @param sourceNode The Current source tree node.
   * @param mode The current xslt mode.
   *
   * @throws SAXException
   */
  public void pushParams(
          XPathContext xctxt, ElemCallTemplate xslCallTemplateElement, Node sourceNode, QName mode)
            throws SAXException
  {

    // The trick here is, variables need to be executed outside the context 
    // of the current stack frame.
    VariableStack vars = xctxt.getVarStack();
    int n = xslCallTemplateElement.getParamElemCount();

    for (int i = 0; i < n; i++)
    {
      ElemWithParam xslParamElement = xslCallTemplateElement.getParamElem(i);

      // Get the argument value as either an expression or 
      // a result tree fragment.
      XObject var;
      XPath param = xslParamElement.getSelect();

      if (null != param)
      {
        var = param.execute(getXPathContext(), sourceNode, xslParamElement);
      }
      else if (null == xslParamElement.getFirstChild())
      {
        var = XString.EMPTYSTRING;
      }
      else
      {

        // Use result tree fragment
        DocumentFragment df = transformToRTF(xslParamElement, sourceNode,
                                             mode);

        var = new XRTreeFrag(df);
      }

      m_newVars.addElement(new Arg(xslParamElement.getName(), var, true));
    }

    vars.pushContextMarker();

    int nNew = m_newVars.size();

    if (nNew > 0)
    {
      for (int i = 0; i < nNew; i++)
      {
        vars.push((Arg) m_newVars.elementAt(i));
      }

      // Dragons check: make sure this is nulling the refs.
      m_newVars.removeAllElements();
    }
  }  // end pushParams method

  /**
   * Internal -- push the global variables onto
   * the context's variable stack.
   *
   * @param contextNode The root of the source tree, can't be null.
   *
   * @throws SAXException
   */
  protected void pushGlobalVars(Node contextNode) throws SAXException
  {

    // I'm a little unhappy with this, as it seems like 
    // this will make all the variables for all stylesheets 
    // in scope, when really only the current stylesheet's 
    // global variables should be in scope.  Have to think on 
    // this more...
    XPathContext xctxt = getXPathContext();
    VariableStack vs = xctxt.getVarStack();
    StylesheetRoot sr = getStylesheet();
    Enumeration vars = sr.getVariablesComposed();

    while (vars.hasMoreElements())
    {
      ElemVariable v = (ElemVariable) vars.nextElement();
      Object val = vs.getVariable(v.getName());

      if (null != val)
        continue;

      XObject xobj = v.getValue(this, contextNode);

      vs.pushVariable(v.getName(), xobj);
    }

    vars = sr.getParamsComposed();

    while (vars.hasMoreElements())
    {
      ElemParam v = (ElemParam) vars.nextElement();
      Object val = vs.getVariable(v.getName());

      if (null != val)
        continue;

      XObject xobj = v.getValue(this, contextNode);

      vs.pushVariable(v.getName(), xobj);
    }

    vs.markGlobalStackFrame();
  }

  /**
   * Set an object that will be used to resolve URIs used in
   * document(), etc.
   * @param resolver An object that implements the URIResolver interface,
   * or null.
   */
  public void setURIResolver(URIResolver resolver)
  {
    getXPathContext().getSourceTreeManager().setURIResolver(resolver);
  }

  /**
   * Set the entity resolver, which will be passed on to any 
   * XMLReaders that are created.
   *
   * @param resolver The entity resolver, or null.
   * 
   * @see org.xml.sax.EntityResolver
   */
  public void setEntityResolver(org.xml.sax.EntityResolver resolver)
  {
    super.setEntityResolver(resolver);
    getXPathContext().getSourceTreeManager().setEntityResolver(resolver);
  }

  // ======== End Transformer Implementation ========  


  /**
   * <meta name="usage" content="advanced"/>
   * Given a stylesheet element, create a result tree fragment from it's
   * contents.
   * @param stylesheetTree The stylesheet object that holds the fragment.
   * @param templateParent The template element that holds the fragment.
   * @param sourceNode The current source context node.
   * @param mode The mode under which the template is operating.
   * @return An object that represents the result tree fragment.
   *
   * @throws SAXException
   */
  public DocumentFragment transformToRTF(
          ElemTemplateElement templateParent, Node sourceNode, QName mode)
            throws SAXException
  {

    // XPathContext xctxt = getXPathContext();
    // Document docFactory = xctxt.getDOMHelper().getDOMFactory();
    if (null == m_docBuilder)
    {
      try
      {
        DocumentBuilderFactory dfactory =
          DocumentBuilderFactory.newInstance();

        dfactory.setNamespaceAware(true);
        dfactory.setValidating(true);

        m_docBuilder = dfactory.newDocumentBuilder();
      }
      catch (ParserConfigurationException pce)
      {
        throw new SAXException(pce);  //"createDocument() not supported in XPathContext!");

        // return null;
      }
    }

    Document docFactory = m_docBuilder.newDocument();

    // Create a ResultTreeFrag object.
    DocumentFragment resultFragment = docFactory.createDocumentFragment();

    // Create a DOMBuilder object that will handle the SAX events 
    // and build the ResultTreeFrag nodes.
    ContentHandler rtfHandler = new DOMBuilder(docFactory, resultFragment);

    // Save the current result tree handler.
    ResultTreeHandler savedRTreeHandler = this.m_resultTreeHandler;

    // And make a new handler for the RTF.
    this.m_resultTreeHandler = new ResultTreeHandler(this, rtfHandler);

    // Do the transformation of the child elements.
    executeChildTemplates(templateParent, sourceNode, mode);

    // Make sure everything is flushed!
    this.m_resultTreeHandler.flushPending();

    // Restore the previous result tree handler.
    this.m_resultTreeHandler = savedRTreeHandler;

    return resultFragment;
  }


  /**
   * <meta name="usage" content="internal"/>
   * Get the StringWriter pool, so that StringWriter 
   * objects may be reused.
   *
   * @return The string writer pool, not null.
   */
  public ObjectPool getStringWriterPool()
  {
    return m_stringWriterObjectPool;
  }

  /**
   * <meta name="usage" content="advanced"/>
   * Take the contents of a template element, process it, and
   * convert it to a string.
   *
   * @param elem The parent element whose children will be output 
   * as a string.
   * @param transformer The XSLT transformer instance.
   * @param sourceNode The current source node context.
   * @param mode The current xslt mode.
   * 
   * @return The stringized result of executing the elements children.
   * 
   * @exception SAXException
   */
  public String transformToString(
          ElemTemplateElement elem, Node sourceNode, QName mode)
            throws SAXException
  {

    // Save the current result tree handler.
    ResultTreeHandler savedRTreeHandler = this.m_resultTreeHandler;

    // Create a Serializer object that will handle the SAX events 
    // and build the ResultTreeFrag nodes.
    StringWriter sw = (StringWriter) m_stringWriterObjectPool.getInstance();

    m_resultTreeHandler =
      (ResultTreeHandler) m_textResultHandlerObjectPool.getInstance();

    Serializer serializer = m_resultTreeHandler.getSerializer();

    try
    {
      if (null == serializer)
      {
        serializer = SerializerFactory.getSerializer(m_textformat);

        m_resultTreeHandler.setSerializer(serializer);
        serializer.setWriter(sw);

        ContentHandler shandler = serializer.asContentHandler();

        m_resultTreeHandler.init(this, shandler);
      }
      else
      {

        // serializer.setWriter(sw);
        // serializer.setOutputFormat(m_textformat);
        // ContentHandler shandler = serializer.asContentHandler();
        // m_resultTreeHandler.setContentHandler(shandler);
      }
    }
    catch (IOException ioe)
    {
      throw new SAXException(ioe);
    }

    String result;

    try
    {
      this.m_resultTreeHandler.startDocument();

      // Do the transformation of the child elements.
      executeChildTemplates(elem, sourceNode, mode);
      this.m_resultTreeHandler.endDocument();

      result = sw.toString();
    }
    finally
    {
      sw.getBuffer().setLength(0);

      try
      {
        sw.close();
      }
      catch (Exception ioe){}

      m_stringWriterObjectPool.freeInstance(sw);
      m_textResultHandlerObjectPool.freeInstance(m_resultTreeHandler);
      m_resultTreeHandler.reset();

      // Restore the previous result tree handler.
      m_resultTreeHandler = savedRTreeHandler;
    }

    return result;
  }

  /**
   * <meta name="usage" content="advanced"/>
   * Given an element and mode, find the corresponding
   * template and process the contents.
   *
   * @param stylesheetTree The current Stylesheet object.
   * @param xslInstruction The calling element.
   * @param template The template to use if xsl:for-each, or null.
   * @param selectContext The selection context.
   * @param child The source context node.
   * @param mode The current mode, may be null.
   * @exception SAXException
   * @return true if applied a template, false if not.
   */
  public boolean applyTemplateToNode(ElemTemplateElement xslInstruction,  // xsl:apply-templates or xsl:for-each
                                     ElemTemplateElement template,  // may be null
                                             Node child, QName mode)
                                                     throws SAXException
  {

    short nodeType = child.getNodeType();
    boolean isApplyImports = ((xslInstruction == null)
                              ? false
                              : xslInstruction.getXSLToken()
                                == Constants.ELEMNAME_APPLY_IMPORTS);

    // To find templates, use the the root of the import tree if 
    // this element is not an xsl:apply-imports or starting from the root, 
    // otherwise use the root of the stylesheet tree.
    // TODO: Not sure apply-import handling is correct right now.  -sb
    StylesheetComposed stylesheetTree;

    if (isApplyImports)
    {
      StylesheetComposed stylesheet = xslInstruction.getStylesheetComposed();
      StylesheetRoot sroot = stylesheet.getStylesheetRoot();
      int importNumber = sroot.getImportNumber(stylesheet);
      int nImports = sroot.getGlobalImportCount();

      if (importNumber < (nImports - 1))
        stylesheetTree = sroot.getGlobalImport(importNumber + 1);
      else
        return false;
    }
    else
    {
      stylesheetTree = (null == template)
                       ? getStylesheet() : template.getStylesheetComposed();
    }

    XPathContext xctxt = getXPathContext();
    boolean isDefaultTextRule = false;

    if (null == template)
    {

      // Find the XSL template that is the best match for the 
      // element.        
      PrefixResolver savedPrefixResolver = xctxt.getNamespaceContext();

      try
      {
        xctxt.setNamespaceContext(xslInstruction);

        TemplateList tl = stylesheetTree.getTemplateListComposed();

        template = tl.getTemplate(xctxt, child, mode,
                                  m_quietConflictWarnings);
      }
      finally
      {
        xctxt.setNamespaceContext(savedPrefixResolver);
      }

      // If that didn't locate a node, fall back to a default template rule.
      // See http://www.w3.org/TR/xslt#built-in-rule.
      if (null == template)
      {
        switch (nodeType)
        {
        case Node.DOCUMENT_FRAGMENT_NODE :
        case Node.ELEMENT_NODE :
          template = m_stylesheetRoot.getDefaultRule();
          break;
        case Node.CDATA_SECTION_NODE :
        case Node.TEXT_NODE :
        case Node.ATTRIBUTE_NODE :
          template = m_stylesheetRoot.getDefaultTextRule();
          isDefaultTextRule = true;
          break;
        case Node.DOCUMENT_NODE :
          template = m_stylesheetRoot.getDefaultRootRule();
          break;
        default :

          // No default rules for processing instructions and the like.
          return false;
        }
      }
    }

    // If we are processing the default text rule, then just clone 
    // the value directly to the result tree.
    try
    {
      pushPairCurrentMatched(template, child);

      if (isDefaultTextRule)
      {
        switch (nodeType)
        {
        case Node.CDATA_SECTION_NODE :
        case Node.TEXT_NODE :
          m_resultTreeHandler.m_cloner.cloneToResultTree(child, false);
          break;
        case Node.ATTRIBUTE_NODE :
        {
          String val = ((Attr) child).getValue();

          getResultTreeHandler().characters(val.toCharArray(), 0,
                                            val.length());
        }
        break;
        }
      }
      else
      {

        // 9/11/00: If template has been compiled, hand off to it
        // since much (most? all?) of the processing has been inlined.
        // (It would be nice if there was a single entry point that
        // worked for both... but the interpretive system works by
        // having the Tranformer execute the children, while the
        // compiled obviously has to run its own code. It's
        // also unclear that "execute" is really the right name for
        // that entry point.)
        // Fire a trace event for the template.
        if (TransformerImpl.S_DEBUG)
          getTraceManager().fireTraceEvent(child, mode, template);

        // And execute the child templates.
        if (template.isCompiledTemplate())
          template.execute(this, child, mode);
        else
          executeChildTemplates(template, child, mode);
      }
    }
    finally
    {
      popCurrentMatched();
    }

    return true;
  }

  /**
   * <meta name="usage" content="advanced"/>
   * Execute each of the children of a template element.
   *
   * @param elem The ElemTemplateElement that contains the children 
   * that should execute.
   * @param sourceNode The current context node.
   * @param mode The current mode.
   * @param handler The ContentHandler to where the result events 
   * should be fed.
   * 
   * @exception SAXException
   */
  public void executeChildTemplates(
          ElemTemplateElement elem, Node sourceNode, QName mode, ContentHandler handler)
            throws SAXException
  {

    ContentHandler savedHandler = this.getContentHandler();

    try
    {
      getResultTreeHandler().flushPending();
      this.setContentHandler(handler);
      executeChildTemplates(elem, sourceNode, mode);
    }
    finally
    {
      this.setContentHandler(savedHandler);
    }
  }

  /**
   * <meta name="usage" content="advanced"/>
   * Execute each of the children of a template element.
   *
   * @param transformer The XSLT transformer instance.
   *
   * @param elem The ElemTemplateElement that contains the children 
   * that should execute.
   * @param sourceNode The current context node.
   * @param mode The current mode.
   * 
   * @exception SAXException
   */
  public void executeChildTemplates(
          ElemTemplateElement elem, Node sourceNode, QName mode)
            throws SAXException
  {

    // Does this element have any children?
    ElemTemplateElement firstChild = elem.getFirstChildElem();

    if (null == firstChild)
      return;

    XPathContext xctxt = getXPathContext();

    // Check for infinite loops if we have to.
    boolean check = (m_stackGuard.m_recursionLimit > -1);

    if (check)
      getStackGuard().push(elem, sourceNode);

    // We need to push an element frame in the variables stack, 
    // so all the variables can be popped at once when we're done.
    VariableStack varstack = getXPathContext().getVarStack();

    varstack.pushElemFrame();

    Locator savedLocator = xctxt.getSAXLocator();

    try
    {
      pushElemTemplateElement(null);

      // Loop through the children of the template, calling execute on 
      // each of them.
      for (ElemTemplateElement t = firstChild; t != null;
              t = t.getNextSiblingElem())
      {
        xctxt.setSAXLocator(t);
        m_currentTemplateElements.setTail(t);
        t.execute(this, sourceNode, mode);
      }
    }
    finally
    {
      popElemTemplateElement();
      xctxt.setSAXLocator(savedLocator);

      // Pop all the variables in this element frame.
      varstack.popElemFrame();
    }

    // Check for infinite loops if we have to
    if (check)
      getStackGuard().pop();
  }

  /**
   * <meta name="usage" content="advanced"/>
   * Get the keys for the xsl:sort elements.
   * Note: Should this go into ElemForEach?
   *
   * @param foreach Valid ElemForEach element, not null.
   * @param sourceNodeContext The current node context in the source tree, 
   * needed to evaluate the Attribute Value Templates.
   *
   * @return A Vector of NodeSortKeys, or null.
   *
   * @throws SAXException
   */
  public Vector processSortKeys(
                                ElemForEach foreach, Node sourceNodeContext)
    throws SAXException
  {

    Vector keys = null;
    XPathContext xctxt = getXPathContext();
    int nElems = foreach.getSortElemCount();

    if (nElems > 0)
      keys = new Vector();

    // March backwards, collecting the sort keys.
    for (int i = 0; i < nElems; i++)
    {
      ElemSort sort = foreach.getSortElem(i);
      String langString =
                         (null != sort.getLang())
                         ? sort.getLang().evaluate(xctxt, sourceNodeContext, foreach)
                           : null;
      String dataTypeString = sort.getDataType().evaluate(xctxt,
                                                          sourceNodeContext, foreach);

      if (dataTypeString.indexOf(":") >= 0)
        System.out.println(
                           "TODO: Need to write the hooks for QNAME sort data type");
      else if (!(dataTypeString.equalsIgnoreCase(Constants.ATTRVAL_DATATYPE_TEXT))
               &&!(dataTypeString.equalsIgnoreCase(
                                                   Constants.ATTRVAL_DATATYPE_NUMBER)))
        foreach.error(XSLTErrorResources.ER_ILLEGAL_ATTRIBUTE_VALUE,
                             new Object[]{ Constants.ATTRNAME_DATATYPE,
                             dataTypeString });

      boolean treatAsNumbers =
                              ((null != dataTypeString) && dataTypeString.equals(
                                                                                 Constants.ATTRVAL_DATATYPE_NUMBER)) ? true : false;
      String orderString = sort.getOrder().evaluate(xctxt,
                                                    sourceNodeContext, foreach);

      if (!(orderString.equalsIgnoreCase(Constants.ATTRVAL_ORDER_ASCENDING))
          &&!(orderString.equalsIgnoreCase(
                                           Constants.ATTRVAL_ORDER_DESCENDING)))
        foreach.error(XSLTErrorResources.ER_ILLEGAL_ATTRIBUTE_VALUE,
                             new Object[]{ Constants.ATTRNAME_ORDER,
                             orderString });

      boolean descending =
                          ((null != orderString) && orderString.equals(
                                                                       Constants.ATTRVAL_ORDER_DESCENDING)) ? true : false;
      AVT caseOrder = sort.getCaseOrder();
      boolean caseOrderUpper;

      if (null != caseOrder)
      {
        String caseOrderString = caseOrder.evaluate(xctxt,
                                                    sourceNodeContext,
                                                    foreach);

        if (!(caseOrderString.equalsIgnoreCase(Constants.ATTRVAL_CASEORDER_UPPER))
            &&!(caseOrderString.equalsIgnoreCase(
                                                 Constants.ATTRVAL_CASEORDER_LOWER)))
          foreach.error(
                               XSLTErrorResources.ER_ILLEGAL_ATTRIBUTE_VALUE,
                               new Object[]{ Constants.ATTRNAME_CASEORDER,
                               caseOrderString });

        caseOrderUpper =
                        ((null != caseOrderString) && caseOrderString.equals(
                                                                             Constants.ATTRVAL_CASEORDER_UPPER)) ? true : false;
      }
      else
      {
        caseOrderUpper = false;
      }

      keys.addElement(new NodeSortKey(this, sort.getSelect(),
                                      treatAsNumbers, descending,
                                      langString, caseOrderUpper,
                                      foreach));
    }

    return keys;
  }

  //==========================================================
  // SECTION: TransformState implementation
  //==========================================================

  /**
   * Push the current template element.
   *
   * @param elem The current ElemTemplateElement (may be null, and then
   * set via setCurrentElement).
   */
  public void pushElemTemplateElement(ElemTemplateElement elem)
  {
    m_currentTemplateElements.push(elem);
  }

  /**
   * Pop the current template element.
   */
  public void popElemTemplateElement()
  {
    m_currentTemplateElements.pop();
  }

  /**
   * Set the top of the current template elements 
   * stack.
   *
   * @param e The current ElemTemplateElement about to 
   * be executed.
   */
  public void setCurrentElement(ElemTemplateElement e)
  {
    m_currentTemplateElements.setTail(e);
  }

  /**
   * Retrieves the current ElemTemplateElement that is 
   * being executed.
   *
   * @return The current ElemTemplateElement that is executing, 
   * should not normally be null.
   */
  public ElemTemplateElement getCurrentElement()
  {
    return (ElemTemplateElement) m_currentTemplateElements.peepTail();
  }

  /**
   * This method retrieves the current context node
   * in the source tree.
   *
   * @return The current context node (should never be null?).
   */
  public Node getCurrentNode()
  {
    return m_xcontext.getCurrentNode();
  }

  /**
   * This method retrieves the xsl:template
   * that is in effect, which may be a matched template
   * or a named template.
   *
   * <p>Please note that the ElemTemplate returned may
   * be a default template, and thus may not have a template
   * defined in the stylesheet.</p>
   *
   * @return The current xsl:template, should not be null.
   */
  public ElemTemplate getCurrentTemplate()
  {
    ElemTemplateElement elem = getCurrentElement();

    while ((null != elem)
           && (elem.getXSLToken() != Constants.ELEMNAME_TEMPLATE))
    {
      elem = elem.getParentElem();
    }

    return (ElemTemplate) elem;
  }

  /**
   * Push both the current xsl:template or xsl:for-each onto the 
   * stack, along with the child node that was matched.
   * (Note: should this only be used for xsl:templates?? -sb)
   *
   * @param template xsl:template or xsl:for-each.
   * @param child The child that was matched.
   */
  public void pushPairCurrentMatched(ElemTemplateElement template, Node child)
  {
    m_currentMatchTemplates.pushPair(template, child);
  }

  /**
   * Pop the elements that were pushed via pushPairCurrentMatched.
   */
  public void popCurrentMatched()
  {
    m_currentMatchTemplates.popPair();
  }

  /**
   * This method retrieves the xsl:template
   * that was matched.  Note that this may not be
   * the same thing as the current template (which
   * may be from getCurrentElement()), since a named
   * template may be in effect.
   *
   * @return The pushed template that was pushed via pushPairCurrentMatched.
   */
  public ElemTemplate getMatchedTemplate()
  {
    return (ElemTemplate) m_currentMatchTemplates.peepTailSub1();
  }

  /**
   * Retrieves the node in the source tree that matched
   * the template obtained via getMatchedTemplate().
   *
   * @return The matched node that corresponds to the 
   * match attribute of the current xsl:template.
   */
  public Node getMatchedNode()
  {
    return m_currentMatchTemplates.peepTail();
  }

  /**
   * Get the current context node list.
   *
   * @return A reset clone of the context node list.
   */
  public NodeIterator getContextNodeList()
  {

    try
    {
      return getXPathContext().getContextNodeList().cloneWithReset();
    }
    catch (CloneNotSupportedException cnse)
    {

      // should never happen.
      return null;
    }
  }

  /**
   * Get the TrAX Transformer object in effect.
   *
   * @return This object.
   */
  public Transformer getTransformer()
  {
    return this;
  }

  //==========================================================
  // SECTION: Accessor Functions
  //==========================================================

  /**
   * Set the stylesheet for this processor.  If this is set, then the
   * process calls that take only the input .xml will use
   * this instead of looking for a stylesheet PI.  Also,
   * setting the stylesheet is needed if you are going
   * to use the processor as a SAX ContentHandler.
   *
   * @param stylesheetRoot A non-null StylesheetRoot object,
   * or null if you wish to clear the stylesheet reference.
   */
  public void setStylesheet(StylesheetRoot stylesheetRoot)
  {
    m_stylesheetRoot = stylesheetRoot;
  }

  /**
   * Get the current stylesheet for this processor.
   *
   * @return The stylesheet that is associated with this 
   * transformer.
   */
  public StylesheetRoot getStylesheet()
  {
    return m_stylesheetRoot;
  }

  /**
   * Get quietConflictWarnings property. If the quietConflictWarnings 
   * property is set to true, warnings about pattern conflicts won't be
   * printed to the diagnostics stream.
   *
   * @return True if this transformer should not report 
   * template match conflicts.
   */
  public boolean getQuietConflictWarnings()
  {
    return m_quietConflictWarnings;
  }

  /**
   * If the quietConflictWarnings property is set to
   * true, warnings about pattern conflicts won't be
   * printed to the diagnostics stream.
   * False by default.
   * (Currently setting this property will have no effect.)
   * 
   * @param b true if conflict warnings should be suppressed.
   */
  public void setQuietConflictWarnings(boolean b)
  {
    m_quietConflictWarnings = b;
  }

  /**
   * <meta name="usage" content="internal"/>
   * Set the execution context for XPath.
   *
   * @param xcontext A non-null reference to the XPathContext 
   * associated with this transformer.
   */
  public void setXPathContext(XPathContext xcontext)
  {
    m_xcontext = xcontext;
  }

  /**
   * Get the XPath context associated with this transformer.
   *
   * @return The XPathContext reference, never null.
   */
  public XPathContext getXPathContext()
  {
    return m_xcontext;
  }

  /**
   * <meta name="usage" content="internal"/>
   * Get the object used to guard the stack from
   * recursion.
   *
   * @return The StackGuard object, which should never be null.
   */
  public StackGuard getStackGuard()
  {
    return m_stackGuard;
  }

  /**
   * Get the recursion limit.
   * Used for infinite loop check. If the value is -1, do not
   * check for infinite loops. Anyone who wants to enable that
   * check should change the value of this variable to be the
   * level of recursion that they want to check. Be careful setting
   * this variable, if the number is too low, it may report an
   * infinite loop situation, when there is none.
   * Post version 1.0.0, we'll make this a runtime feature.
   *
   * @return The limit on recursion, or -1 if no check is to be made.
   */
  public int getRecursionLimit()
  {
    return m_stackGuard.getRecursionLimit();
  }

  /**
   * Get the recursion limit.
   * Used for infinite loop check. If the value is -1, do not
   * check for infinite loops. Anyone who wants to enable that
   * check should change the value of this variable to be the
   * level of recursion that they want to check. Be careful setting
   * this variable, if the number is too low, it may report an
   * infinite loop situation, when there is none.
   * Post version 1.0.0, we'll make this a runtime feature.
   *
   * @param limit A number that represents the limit of recursion, 
   * or -1 if no checking is to be done.
   */
  public void setRecursionLimit(int limit)
  {
    m_stackGuard.setRecursionLimit(limit);
  }

  /**
   * Get the ResultTreeHandler object.
   *
   * @return The current ResultTreeHandler, which may not 
   * be the main result tree manager.
   */
  public ResultTreeHandler getResultTreeHandler()
  {
    return m_resultTreeHandler;
  }

  /**
   * Get the KeyManager object.
   *
   * @return A reference to the KeyManager object, which should
   * never be null.
   */
  public KeyManager getKeyManager()
  {
    return m_keyManager;
  }

  /**
   * Check to see if this is a recursive attribute definition.
   *
   * @param attrSet A non-null ElemAttributeSet reference.
   *
   * @return true if the attribute set is recursive.
   */
  public boolean isRecursiveAttrSet(ElemAttributeSet attrSet)
  {

    if (null == m_attrSetStack)
    {
      m_attrSetStack = new Stack();
    }

    if (!m_attrSetStack.empty())
    {
      int loc = m_attrSetStack.search(attrSet);

      if (loc > -1)
      {
        return true;
      }
    }

    return false;
  }

  /**
   * Push an executing attribute set, so we can check for
   * recursive attribute definitions.
   *
   * @param attrSet A non-null ElemAttributeSet reference.
   */
  public void pushElemAttributeSet(ElemAttributeSet attrSet)
  {
    m_attrSetStack.push(attrSet);
  }

  /**
   * Pop the current executing attribute set.
   */
  public void popElemAttributeSet()
  {
    m_attrSetStack.pop();
  }

  /**
   * Get the table of counters, for optimized xsl:number support.
   *
   * @return The CountersTable, never null.
   */
  public CountersTable getCountersTable()
  {

    if (null == m_countersTable)
      m_countersTable = new CountersTable();

    return m_countersTable;
  }

  /**
   * Tell if the current template rule is null, i.e. if we are 
   * directly within an apply-templates.  Used for xsl:apply-imports.
   *
   * @return True if the current template rule is null.
   */
  public boolean currentTemplateRuleIsNull()
  {
    return ((!m_currentTemplateRuleIsNull.isEmpty())
            && (m_currentTemplateRuleIsNull.peek() == true));
  }

  /**
   * Push true if the current template rule is null, false
   * otherwise.
   *
   * @param b True if the we are executing an xsl:for-each 
   * (or xsl:call-template?).
   */
  public void pushCurrentTemplateRuleIsNull(boolean b)
  {
    m_currentTemplateRuleIsNull.push(b);
  }

  /**
   * Push true if the current template rule is null, false
   * otherwise.
   */
  public void popCurrentTemplateRuleIsNull()
  {
    m_currentTemplateRuleIsNull.pop();
  }

  /**
   * Return the message manager.
   *
   * @return The message manager, never null.
   */
  public MsgMgr getMsgMgr()
  {

    if (null == m_msgMgr)
      m_msgMgr = new MsgMgr(this);

    return m_msgMgr;
  }

  /**
   * Get an instance of the trace manager for this transformation.
   * This object can be used to set trace listeners on various
   * events during the transformation.
   *
   * @return A reference to the TraceManager, never null.
   */
  public TraceManager getTraceManager()
  {
    return m_traceManager;
  }

  /**
   * Set the parent reader.
   *
   * <p>This is the {@link org.xml.sax.XMLReader XMLReader} from which
   * this filter will obtain its events and to which it will pass its
   * configuration requests.  The parent may itself be another filter.</p>
   *
   * <p>If there is no parent reader set, any attempt to parse
   * or to set or get a feature or property will fail.</p>
   *
   * @param parent The parent XML reader.
   * @exception java.lang.NullPointerException If the parent is null.
   */
  public void setParent(XMLReader parent)
  {

    super.setParent(parent);

    // the setting of the parent's content handler directly works 
    // because parse (InputSource input) is overridden, and 
    // setupParse(); in XMLFilterImpl is never called.
    parent.setContentHandler(getInputContentHandler());
  }

  /**
   * Allow an application to register a content event handler.
   *
   * <p>If the application does not register a content handler, all
   * content events reported by the SAX parser will be silently
   * ignored.</p>
   *
   * <p>Applications may register a new or different handler in the
   * middle of a parse, and the SAX parser must begin using the new
   * handler immediately.</p>
   *
   * @param handler The content handler.
   * @exception java.lang.NullPointerException If the handler
   *            argument is null.
   * @see #getContentHandler
   */
  public void setContentHandler(ContentHandler handler)
  {

    super.setContentHandler(handler);

    if (null == m_resultTreeHandler)
      m_resultTreeHandler = new ResultTreeHandler(this, handler);
    else
      m_resultTreeHandler.setContentHandler(handler);
  }

  /**
   * Look up the value of a feature.
   *
   * <p>The feature name is any fully-qualified URI.  It is
   * possible for an Processor to recognize a feature name but
   * to be unable to return its value; this is especially true
   * in the case of an adapter for a SAX1 Parser, which has
   * no way of knowing whether the underlying parser is
   * validating, for example.</p>
   *
   * <h3>Open issues:</h3>
   * <dl>
   *    <dt><h4>Should getFeature be changed to hasFeature?</h4></dt>
   *    <dd>Keith Visco writes: Should getFeature be changed to hasFeature?
   *        It returns a boolean which indicated whether the "state"
   *        of feature is "true or false". I assume this means whether
   *        or not a feature is supported? I know SAX is using "getFeature",
   *        but to me "hasFeature" is cleaner.</dd>
   * </dl>
   *
   * @param name The feature name, which is a fully-qualified
   *        URI.
   * @return The current state of the feature (true or false).
   * @exception org.xml.sax.SAXNotRecognizedException When the
   *            Processor does not recognize the feature name.
   * @exception org.xml.sax.SAXNotSupportedException When the
   *            Processor recognizes the feature name but
   *            cannot determine its value at this time.
   *
   * @throws SAXNotRecognizedException
   * @throws SAXNotSupportedException
   */
  public boolean getFeature(String name)
          throws SAXNotRecognizedException, SAXNotSupportedException
  {

    if ("http://xml.org/trax/features/sax/input".equals(name))
      return true;
    else if ("http://xml.org/trax/features/dom/input".equals(name))
      return true;

    throw new SAXNotRecognizedException(name);
  }

  ////////////////////////
  // Implement Runnable //  
  ////////////////////////

  /**
   * Get the exception thrown by the secondary thread (normally 
   * the transform thread).
   *
   * @return The thrown exception, or null if no exception was 
   * thrown.
   */
  public Exception getExceptionThrown()
  {
    return m_exceptionThrown;
  }

  /**
   * This is just a way to set the document for run().
   *
   * @param doc A non-null reference to the root of the 
   * tree to be transformed.
   */
  public void setSourceTreeDocForThread(Node doc)
  {
    m_doc = doc;
  }

  /**
   * Tell if the transform method is completed.
   *
   * @return True if transformNode has completed, or 
   * an exception was thrown.
   */
  public boolean isTransformDone()
  {
    return m_isTransformDone;
  }

  /**
   * From a secondary thread, post the exception, so that
   * it can be picked up from the main thread.
   *
   * @param e The exception that was thrown.
   */
  private void postExceptionFromThread(Exception e)
  {

    if (m_inputContentHandler instanceof SourceTreeHandler)
    {
      SourceTreeHandler sth = (SourceTreeHandler) m_inputContentHandler;

      sth.setExceptionThrown(e);
    }

    m_isTransformDone = true;
    m_exceptionThrown = e;
    ;  // should have already been reported via the error handler?

    synchronized (this)
    {
      String msg = e.getMessage();

      // System.out.println(e.getMessage());
      notifyAll();

      if (null == msg)
      {

        // m_throwNewError = false;
        e.printStackTrace();
      }

      // throw new org.apache.xalan.utils.WrappedRuntimeException(e);
    }
  }

  /**
   * Run the transform thread.
   */
  public void run()
  {

    try
    {

      // Node n = ((SourceTreeHandler)getInputContentHandler()).getRoot();
      // transformNode(n);
      if (isParserEventsOnMain())
      {
        try
        {
          m_isTransformDone = false;

          transformNode(m_doc);
        }
        catch (Exception e)
        {

          // Strange that the other catch won't catch this...
          postExceptionFromThread(e);
        }
        finally
        {
          m_isTransformDone = true;

          synchronized (this)
          {
            notifyAll();
          }
        }
      }
      else
      {
        getXPathContext().getPrimaryReader().parse(m_xmlSource);
      }
    }
    catch (Exception e)
    {
      postExceptionFromThread(e);
    }
  }
}  // end TransformerImpl class

