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
import org.apache.xalan.templates.ElemTemplate;

import org.apache.xalan.trace.TraceManager;

import org.apache.xalan.utils.DOMBuilder;
import org.apache.xalan.utils.NodeVector;
import org.apache.xalan.utils.BoolStack;
import org.apache.xalan.utils.QName;
import org.apache.xalan.utils.PrefixResolver;

import org.apache.xpath.XPathContext;
import org.apache.xpath.NodeSet;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.XPath;
import org.apache.xpath.objects.XRTreeFrag;
import org.apache.xpath.Arg;
import org.apache.xpath.XPathAPI;
import org.apache.xpath.VariableStack;
import org.apache.xpath.compiler.XPathParser;
import org.apache.xpath.axes.ContextNodeList;

// Back support for liaisons
import org.apache.xpath.DOM2Helper;

// Serializer Imports
import serialize.OutputFormat;
import serialize.Serializer;
import serialize.SerializerFactory;
import serialize.Method;

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
import trax.Result;
import trax.Transformer;
import trax.TransformException;
import trax.URIResolver;

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
    m_stylesheetRoot = null;
    // m_rootDoc = null;
    // if(null != m_countersTable)
    //  System.out.println("Number counters made: "+m_countersTable.m_countersMade);
    m_countersTable = null;
    // m_resultNameSpaces = new Stack();
    m_stackGuard = new StackGuard();
    getXPathContext().reset();
  }
      
  // ========= Transformer Interface Implementation ==========

  /**
   * Transform a document.
   *
   * @param input The input source for the document entity.
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @exception java.io.IOException An IO exception from the parser,
   *            possibly from a byte stream or character stream
   *            supplied by the application.
   * @see org.xml.sax.XMLReader#parse(org.xml.sax.InputSource)
   */
  public void parse( InputSource xmlSource)
    throws SAXException, IOException
  {
    transform( xmlSource );
  }
  
  /**
   * Process the source tree to SAX parse events.
   * @param xmlSource  The input for the source tree.
   */
  public void transform( InputSource xmlSource)
    throws TransformException
  {
    String liaisonClassName = System.getProperty("org.apache.xalan.source.liaison");

    if(null != liaisonClassName)
    {
      try 
      {
        DOM2Helper liaison =  (DOM2Helper)(Class.forName(liaisonClassName).newInstance());
        liaison.parse(xmlSource);
        getXPathContext().setDOMHelper(liaison);
        transformNode(liaison.getDocument());
      } 
      catch (SAXException se) 
      {
        if(se instanceof trax.TransformException)
          throw (trax.TransformException)se;
        else
          throw new TransformException(se);
      } 
      catch (ClassNotFoundException e1) 
      {
        throw new TransformException("XML Liaison class " + liaisonClassName +
          " specified but not found", e1);
      } 
      catch (IllegalAccessException e2) 
      {
          throw new TransformException("XML Liaison class " + liaisonClassName +
            " found but cannot be loaded", e2);
      } 
      catch (InstantiationException e3) 
      {
          throw new TransformException("XML Liaison class " + liaisonClassName +
            " loaded but cannot be instantiated (no empty public constructor?)",
            e3);
      } 
      catch (ClassCastException e4) 
      {
          throw new TransformException("XML Liaison class " + liaisonClassName +
            " does not implement DOM2Helper", e4);
      }
    }
    else
    {
      try
      {
        // Get an already set XMLReader, or create one.
        XMLReader reader = this.getParent();
        if(null == reader)
        {
          reader = XMLReaderFactory.createXMLReader();
        }
        try
        {
          reader.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
        }
        catch(SAXException se)
        {
          // What can we do?
          // TODO: User diagnostics.
        }
        try
        {
          reader.setFeature("http://apache.org/xml/features/validation/dynamic", true);
        }
        catch(SAXException se)
        {
        }
        
        // Get the input content handler, which will handle the 
        // parse events and create the source tree. 
        ContentHandler inputHandler = getInputContentHandler();
        reader.setContentHandler( inputHandler );
        reader.setProperty("http://xml.org/sax/properties/lexical-handler", inputHandler);
                
        // Set the reader for cloning purposes.
        getXPathContext().setPrimaryReader(reader);
        
        if(inputHandler instanceof org.apache.xalan.stree.SourceTreeHandler)
        {
          ((org.apache.xalan.stree.SourceTreeHandler)inputHandler).setInputSource(xmlSource);
          ((org.apache.xalan.stree.SourceTreeHandler)inputHandler).setUseMultiThreading(true);
          Node doc 
            = ((org.apache.xalan.stree.SourceTreeHandler)inputHandler).getRoot();
          if(null != doc)
          {
            getXPathContext().getSourceTreeManager().putDocumentInCache(doc, xmlSource);
            m_xmlSource = xmlSource;
            Thread t = new Thread(this);
            t.start();
            transformNode(doc);
          }
          
        }
        else
        {
          // ??
          reader.parse( xmlSource );
        }
        
        // Kick off the parse.  When the ContentHandler gets 
        // the startDocument event, it will call transformNode( node ).
        // reader.parse( xmlSource );
        
        // This has to be done to catch exceptions thrown from 
        // the transform thread spawned by the STree handler.
        Exception e = getExceptionThrown();
        if(null != e)
        {
          if(e instanceof trax.TransformException)
            throw (trax.TransformException)e;
          else
            throw new trax.TransformException(e);
        }
      }
      catch(SAXException se)
      {
        // se.printStackTrace();
        if(se instanceof TransformException)
          throw (TransformException)se;
        else
          throw new TransformException(se);
      }
      catch(IOException ioe)
      {
        throw new TransformException(ioe);
      }
    }
  }
  
  /**
   * Process the an input source to a DOM node.  FOR INTERNAL USE ONLY.
   * @param xmlSource  The input for the source tree.
   */
  public Node parseToNode( InputSource xmlSource)
    throws TransformException
  {
    // Duplicate code from above... but slightly different.  
    // TODO: Work on this...
    
    Node doc = null;
    String liaisonClassName = System.getProperty("org.apache.xalan.source.liaison");

    if(null != liaisonClassName)
    {
      try 
      {
        DOM2Helper liaison =  (DOM2Helper)(Class.forName(liaisonClassName).newInstance());
        liaison.parse(xmlSource);
        getXPathContext().setDOMHelper(liaison);
        doc = liaison.getDocument();
      } 
      catch (SAXException se) 
      {
        throw new TransformException(se);
      } 
      catch (ClassNotFoundException e1) 
      {
        throw new TransformException("XML Liaison class " + liaisonClassName +
          " specified but not found", e1);
      } 
      catch (IllegalAccessException e2) 
      {
          throw new TransformException("XML Liaison class " + liaisonClassName +
            " found but cannot be loaded", e2);
      } 
      catch (InstantiationException e3) 
      {
          throw new TransformException("XML Liaison class " + liaisonClassName +
            " loaded but cannot be instantiated (no empty public constructor?)",
            e3);
      } 
      catch (ClassCastException e4) 
      {
          throw new TransformException("XML Liaison class " + liaisonClassName +
            " does not implement DOM2Helper", e4);
      }
    }
    else
    {
      try
      {
        // Get an already set XMLReader, or create one.
        XMLReader reader = this.getParent();
        if(null == reader)
        {
          reader = XMLReaderFactory.createXMLReader();
        }
        try
        {
          reader.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
        }
        catch(SAXException se)
        {
          // What can we do?
          // TODO: User diagnostics.
        }
        
        // TODO: Handle Xerces DOM parser.
        
        // Get the input content handler, which will handle the 
        // parse events and create the source tree.
        ContentHandler inputHandler = getInputContentHandler();
        
        Class inputHandlerClass = ((Object)inputHandler).getClass();
        inputHandler = (ContentHandler)inputHandlerClass.newInstance();
        
        reader.setContentHandler( inputHandler );
        reader.setProperty("http://xml.org/sax/properties/lexical-handler", inputHandler);
        
        getXPathContext().setPrimaryReader(reader);
                
        // ...and of course I need a standard way to get a node...
        if(inputHandler instanceof org.apache.xalan.stree.SourceTreeHandler)
        {
          // Kick off the parse.  When the ContentHandler gets 
          // the startDocument event, it will call transformNode( node ).
          reader.parse( xmlSource );
          
          doc = ((org.apache.xalan.stree.SourceTreeHandler)inputHandler).getRoot();
        }
      }
      catch(java.lang.IllegalAccessException iae)
      {
        throw new TransformException(iae);
      }
      catch(InstantiationException ie)
      {
        throw new TransformException(ie);
      }
      catch(SAXException se)
      {
        throw new TransformException(se);
      }
      catch(IOException ioe)
      {
        throw new TransformException(ioe);
      }
    }
    return doc;
  }
  
    /**
   * Create a ContentHandler from a Result object.
   */
  public ContentHandler createResultContentHandler(Result outputTarget)
    throws TransformException
  {
    return createResultContentHandler(outputTarget, getOutputFormat());
  }
  
  /**
   * Create a ContentHandler from a Result object.
   */
  public ContentHandler createResultContentHandler(Result outputTarget, 
                                                   OutputFormat format)
    throws TransformException
  {
    ContentHandler handler;
    
    // If the Result object contains a Node, then create 
    // a ContentHandler that will add nodes to the input node.
    Node outputNode = outputTarget.getNode();
    if(null != outputNode)
    {
      int type = outputNode.getNodeType();

      Document doc = (Node.DOCUMENT_NODE == type) 
                     ? (Document)outputNode : outputNode.getOwnerDocument();
      
      handler = (Node.DOCUMENT_FRAGMENT_NODE == type) ?
                new DOMBuilder(doc, (DocumentFragment)outputNode) :
                new DOMBuilder(doc, outputNode);
    }
    // Otherwise, create a ContentHandler that will serialize the 
    // result tree to either a stream or a writer.
    else
    {      
      String method = format.getMethod();
      if(null == method)
        method = Method.XML;
      
      try
      {
        Serializer serializer = SerializerFactory.getSerializer(format);
        if(null != outputTarget.getCharacterStream())
          serializer.setWriter(outputTarget.getCharacterStream());
        else
          serializer.setOutputStream(outputTarget.getByteStream());
        handler = serializer.asContentHandler();
        this.setSerializer(serializer);
      }
      catch(UnsupportedEncodingException uee)
      {
        throw new TransformException(uee);
      }
      catch(IOException ioe)
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
   */
  public void transform( InputSource xmlSource, Result outputTarget)
    throws TransformException
  {
    ContentHandler handler = createResultContentHandler(outputTarget);
    this.setContentHandler(handler);
    transform( xmlSource );
  }

  /**
   * Process the source node to the output result, if the 
   * processor supports the "http://xml.org/trax/features/dom/input" 
   * feature.
   * @param node  The input source node, which can be any valid DOM node.
   * @param outputTarget The output source target.
   */
  public void transformNode( Node node, Result outputTarget)
    throws TransformException
  {
    ContentHandler handler = createResultContentHandler(outputTarget);
    this.setContentHandler(handler);
    transformNode( node );
  }
  
  /**
   * Process the source node to the output result, if the 
   * processor supports the "http://xml.org/trax/features/dom/input" 
   * feature.
   * @param node  The input source node, which can be any valid DOM node.
   * @param outputTarget The output source target.
   */
  public void transformNode( Node node )
    throws TransformException
  {
    try
    {
      pushGlobalVars(node);
      
      // ==========
      // Give the top-level templates a chance to pass information into 
      // the context (this is mainly for setting up tables for extensions).
      StylesheetRoot stylesheet = this.getStylesheet();
      int n = stylesheet.getGlobalImportCount();
      for(int i = 0; i < n; i++)
      {
        Stylesheet imported = stylesheet.getGlobalImport(i);
        imported.runtimeInit(this);
        for(ElemTemplateElement child = imported.getFirstChildElem();
            child != null; child = child.getNextSiblingElem())
        {
          child.runtimeInit(this);
        }
      }
      // ===========
      
      this.transformNode(null, null, node, null);
      if((null != m_resultTreeHandler) && !m_resultTreeHandler.getFoundEndDoc())
      {
        m_resultTreeHandler.endDocument();
        this.m_resultTreeHandler.flushPending();
      }
    }
    catch(SAXException se)
    {
      if(se instanceof trax.TransformException)
        throw (trax.TransformException)se;
      else
        throw new TransformException(se);
    }
  }
  
  /**
   * The content handler for the source input tree.
   */
  ContentHandler m_inputContentHandler;
  
  /**
   * Get a SAX2 ContentHandler for the input.
   * @return A valid ContentHandler, which should never be null, as 
   * long as getFeature("http://xml.org/trax/features/sax/input") 
   * returns true.
   */
  public ContentHandler getInputContentHandler()
  {
    if(null == m_inputContentHandler)
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
    if(m_inputContentHandler instanceof DeclHandler)
      return (DeclHandler)m_inputContentHandler;
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
    if(m_inputContentHandler instanceof LexicalHandler)
      return (LexicalHandler)m_inputContentHandler;
    else
      return null;
  }
  
  /**
   * The output format object set by the user.  May be null.
   */
  private OutputFormat m_outputFormat;

  /**
   * Set the output properties for the transformation.  These 
   * properties will override properties set in the templates 
   * with xsl:output.
   * 
   * @see org.xml.serialize.OutputFormat
   */
  public void setOutputFormat(OutputFormat oformat)
  {
    m_outputFormat = oformat;
  }

  /**
   * Get the output properties used for the transformation.
   * 
   * @see org.xml.serialize.OutputFormat
   */
  public OutputFormat getOutputFormat()
  {
    // Get the output format that was set by the user, otherwise get the 
    // output format from the stylesheet.
    OutputFormat format = (null == m_outputFormat) 
                          ? getStylesheet().getOutputComposed() :
                            m_outputFormat;
    return format;
  }
  
  private Serializer m_serializer;
  
  public Serializer getSerializer()
  {
    return m_serializer;
  }
  
  public void setSerializer(Serializer s)
  {
    m_serializer = s;;
  }


  /**
   * Set a parameter for the templates.
   * @param name The name of the parameter.
   * @param namespace The namespace of the parameter.
   * @value The value object.  This can be any valid Java object 
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
  public void resetParameters()
  {
  }
  
  /**
   * Given a template, search for
   * the arguments and push them on the stack.  Also,
   * push default arguments on the stack.
   * You <em>must</em> call popContext() when you are
   * done with the arguments.
   */
  public void pushParams(Stylesheet stylesheetTree,
                  ElemTemplateElement xslCallTemplateElement,
                  Node sourceNode, QName mode)
    throws SAXException
  {
    // The trick here is, variables need to be executed outside the context 
    // of the current stack frame.
    
    XPathContext xctxt = getXPathContext();
    VariableStack vars = xctxt.getVarStack();
    if(1 == vars.getCurrentStackFrameIndex())
    {
      vars.pushElemFrame();
    }

    for(ElemTemplateElement child = xslCallTemplateElement.getFirstChildElem();
        null != child; child = child.getNextSiblingElem())
    {
      // Is it an xsl:with-param element?
      if(Constants.ELEMNAME_WITHPARAM == child.getXSLToken())
      {
        ElemWithParam xslParamElement = (ElemWithParam)child;

        // Get the argument value as either an expression or 
        // a result tree fragment.
        XObject var;
        if(null != xslParamElement.getSelect())
        {
          var = xslParamElement.getSelect().execute(getXPathContext(), sourceNode,
                                                    xslParamElement);
        }
        else
        {
          // Use result tree fragment
          DocumentFragment df = transformToRTF(stylesheetTree, 
                                               xslParamElement,
                                               sourceNode, mode);
          var = new XRTreeFrag(df);
        }
        
        vars.push(new Arg(xslParamElement.getName(), 
                                                         var, true));
      }
    }
    
  } // end pushParams method
  
  /**
   * Internal -- push the global variables onto 
   * the context's variable stack.
   */
  protected void pushGlobalVars(Node contextNode)
    throws SAXException
  {
    // I'm a little unhappy with this, as it seems like 
    // this will make all the variables for all stylesheets 
    // in scope, when really only the current stylesheet's 
    // global variables should be in scope.  Have to think on 
    // this more...
    XPathContext xctxt = getXPathContext();
    VariableStack vs = xctxt.getVarStack();
    if(1 == vs.getCurrentStackFrameIndex())
    {
      vs.pushElemFrame();
    }
    StylesheetRoot sr = getStylesheet();

    Enumeration vars = sr.getVariablesComposed();
    while(vars.hasMoreElements())
    {
      ElemVariable v = (ElemVariable)vars.nextElement();
      Object val = vs.getVariable(v.getName());
      if(null != val)
        continue;
      XObject xobj = v.getValue(this, contextNode);
      vs.pushVariable(v.getName(), xobj);
    }
    vars = sr.getParamsComposed();
    while(vars.hasMoreElements())
    {
      ElemParam v = (ElemParam)vars.nextElement();
      Object val = vs.getVariable(v.getName());
      if(null != val)
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
  
  /*
  * Allow an application to register an entity resolver.
  */
  public void setEntityResolver (org.xml.sax.EntityResolver resolver)
  {
    super.setEntityResolver(resolver);
    getXPathContext().getSourceTreeManager().setEntityResolver(resolver);
  }

    
  // ======== End Transformer Implementation ========  
  
  DocumentBuilder m_docBuilder = null;
    
  /**
   * <meta name="usage" content="advanced"/>
   * Given a stylesheet element, create a result tree fragment from it's
   * contents.
   * @param stylesheetTree The stylesheet object that holds the fragment.
   * @param templateParent The template element that holds the fragment.
   * @param sourceNode The current source context node.
   * @param mode The mode under which the template is operating.
   * @return An object that represents the result tree fragment.
   */
  public DocumentFragment transformToRTF(
                                        Stylesheet stylesheetTree,
                                        ElemTemplateElement templateParent,
                                        Node sourceNode, QName mode)
    throws SAXException
  {
    // XPathContext xctxt = getXPathContext();
    // Document docFactory = xctxt.getDOMHelper().getDOMFactory();
    
    if(null == m_docBuilder)
    {
      try
      {
        DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
        m_docBuilder = dfactory.newDocumentBuilder();
      }
      catch(ParserConfigurationException pce)
      {
        throw new SAXException(pce);//"createDocument() not supported in XPathContext!");
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
   * <meta name="usage" content="advanced"/>
   * Take the contents of a template element, process it, and
   * convert it to a string.
   * 
   * @exception SAXException Might be thrown from the  document() function, or
   *      from xsl:include or xsl:import.
   * @param transformer The XSLT transformer instance.
   * @param sourceNode The current source node context.
   * @param mode The current mode.
   * @return The stringized result of executing the elements children.
   */
  public String transformToString(ElemTemplateElement elem, 
                                 Node sourceNode,
                                 QName mode)
    throws SAXException
  {    
    // Save the current result tree handler.
    ResultTreeHandler savedRTreeHandler = this.m_resultTreeHandler;
    
    // Create a Serializer object that will handle the SAX events 
    // and build the ResultTreeFrag nodes.
    ContentHandler shandler;
    StringWriter sw;
    try
    {
      // SerializerFactory sfactory 
      //  = SerializerFactory.getSerializerFactory("text");
      sw = new StringWriter();
      OutputFormat format = new OutputFormat();
      format.setPreserveSpace(true);
      Serializer serializer = SerializerFactory.getSerializer(format);
      serializer.setWriter(sw);
      shandler = serializer.asContentHandler();
    }
    catch(IOException ioe)
    {
      throw new SAXException(ioe);
    }

    // And make a new handler that will write to the RTF.
    this.m_resultTreeHandler = new ResultTreeHandler(this, shandler);
    
    this.m_resultTreeHandler.startDocument();

    // Do the transformation of the child elements.
    executeChildTemplates(elem, sourceNode, mode);
    
    // Make sure everything is flushed!
    this.m_resultTreeHandler.flushPending();
    
    this.m_resultTreeHandler.endDocument();
    
    // Restore the previous result tree handler.
    this.m_resultTreeHandler = savedRTreeHandler;
    
    return sw.toString();
  }
        
  /** 
   * <meta name="usage" content="advanced"/>
   * Perform a query if needed, and call transformNode for each child.
   * 
   * @exception SAXException Thrown in a variety of circumstances.
   * @param stylesheetTree The owning stylesheet tree.
   * @param xslInstruction The stylesheet element context (depricated -- I do 
   *      not think we need this).
   * @param template The owning template context.
   * @param sourceNodeContext The current source node context.
   * @param mode The current mode.
   * @param selectPattern The XPath with which to perform the selection.
   * @param xslToken The current XSLT instruction (depricated -- I do not     
   *     think we want this).
   * @param tcontext The TransformerImpl context.
   * @param selectStackFrameIndex The stack frame context for executing the
   *                              select statement.
   */
  public void transformSelectedNodes(StylesheetComposed stylesheetTree, 
                                     ElemTemplateElement xslInstruction, // xsl:apply-templates or xsl:for-each
                                     ElemTemplateElement template, // The template to copy to the result tree
                                     Node sourceNodeContext, QName mode, 
                                     XPath selectPattern, 
                                     int selectStackFrameIndex)
    throws SAXException
  {
    // Get the xsl:sort keys, if any.
    Vector keys = processSortKeys(xslInstruction, sourceNodeContext);
    
    XPathContext xctxt = getXPathContext();
      
    NodeIterator sourceNodes;
    VariableStack varstack = xctxt.getVarStack();
    
    // Was a select attribute specified?
    if(null == selectPattern)
    {
      if(null == m_selectDefault)
        m_selectDefault = new XPath("node()", xslInstruction, xslInstruction, XPath.SELECT);
        
      selectPattern = m_selectDefault;
    }
    
    // Save where-ever it is that the stack frame index may be pointing to.
    int savedCurrentStackFrameIndex = varstack.getCurrentStackFrameIndex();

    // Make sure the stack frame index for variables is pointing to the right place.
    varstack.setCurrentStackFrameIndex(selectStackFrameIndex);
    
    try
    {
      XObject result = selectPattern.execute(xctxt, sourceNodeContext, 
                                             xslInstruction);
      sourceNodes = result.nodeset();
      
      if(TransformerImpl.S_DEBUG && m_traceManager.hasTraceListeners())
      {
        XNodeSet xresult = new XNodeSet(new NodeSet(sourceNodes));
        m_traceManager.fireSelectedEvent(sourceNodeContext,
                                         xslInstruction, "select", 
                                         selectPattern, xresult);
        // nodeList.setCurrentPos(0);
      }
    }
    finally
    {
      varstack.setCurrentStackFrameIndex(savedCurrentStackFrameIndex);
    }

    // Of we now have a list of source nodes, sort them if needed, 
    // and then call transformNode on each of them.
    if(null != sourceNodes)
    {
      // Sort if we need to.
      if(null != keys)
      {               
        NodeSorter sorter = new NodeSorter(xctxt);
        NodeSet nodeList;
        
        if(sourceNodes instanceof NodeSet)
        {
          nodeList = ((NodeSet)sourceNodes);
          nodeList.setShouldCacheNodes(true);
          nodeList.runTo(-1);
        }
        else
        {
          nodeList = new NodeSet(sourceNodes);
          sourceNodes = nodeList;
          ((ContextNodeList)sourceNodes).setCurrentPos(0);
        }
        
        xctxt.pushContextNodeList((ContextNodeList)sourceNodes );
        try{
          sorter.sort(nodeList, keys, xctxt);
          nodeList.setCurrentPos(0);
        }
        finally
        {
          xctxt.popContextNodeList();
        }
      }
      
      // Push the ContextNodeList on a stack, so that select="position()"
      // and the like will work.
      // System.out.println("pushing context node list...");
      xctxt.pushContextNodeList((ContextNodeList)sourceNodes );
      try
      {   
        // Do the transformation on each.
        Node context;
        while(null != (context = sourceNodes.nextNode())) 
        {
          transformNode(xslInstruction, template, context, mode);
        }
      }
      finally
      {
        xctxt.popContextNodeList();
      }
    }
    
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
  public boolean transformNode(
                                ElemTemplateElement xslInstruction, // xsl:apply-templates or xsl:for-each
                                ElemTemplateElement template, // may be null
                                Node child,
                                QName mode
                                )
    throws SAXException
  {    
    int nodeType = child.getNodeType();

    boolean isApplyImports = ((xslInstruction == null)? false : 
                               xslInstruction.getXSLToken() == Constants.ELEMNAME_APPLY_IMPORTS);      
    
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
        stylesheetTree = sroot.getGlobalImport(importNumber+1);
      else 
        return false;
    }
    else 
    {  
      stylesheetTree = (null == template) 
                                        ? getStylesheet() 
                                          : template.getStylesheetComposed();
    }  

    XPathContext xctxt = getXPathContext();
    boolean isDefaultTextRule = false;
    
    if(null == template)
    {
      // Find the XSL template that is the best match for the 
      // element.        
      PrefixResolver savedPrefixResolver = xctxt.getNamespaceContext();
      try
      {
        xctxt.setNamespaceContext(xslInstruction);

        template = stylesheetTree.getTemplateComposed(xctxt, 
                                                      child, mode, 
                                                      getQuietConflictWarnings());
      }
      finally
      {
        xctxt.setNamespaceContext(savedPrefixResolver);
      }
      
      // If that didn't locate a node, fall back to a default template rule.
      // See http://www.w3.org/TR/xslt#built-in-rule.
      if(null == template)
      {
        StylesheetRoot root = m_stylesheetRoot;
        switch(nodeType)
        {
        case Node.DOCUMENT_FRAGMENT_NODE:
        case Node.ELEMENT_NODE:
          template = root.getDefaultRule();
          break;
        case Node.CDATA_SECTION_NODE:
        case Node.TEXT_NODE:
        case Node.ATTRIBUTE_NODE:
          template = root.getDefaultTextRule();
          isDefaultTextRule = true;
          break;
        case Node.DOCUMENT_NODE:
          template = root.getDefaultRootRule();
          break;
        default:
          // No default rules for processing instructions and the like.
          return false;
        }   
      }
    }
    
    // If we are processing the default text rule, then just clone 
    // the value directly to the result tree.
    try
    {
      m_currentMatchTemplates.push(template);
      m_currentMatchNodes.push(child);
      if(isDefaultTextRule)
      {
        switch(nodeType)
        {
        case Node.CDATA_SECTION_NODE:
        case Node.TEXT_NODE:
          getResultTreeHandler().cloneToResultTree(stylesheetTree, child, false, false, false);
          break;
        case Node.ATTRIBUTE_NODE:
          {
            String val = ((Attr)child).getValue();
            getResultTreeHandler().characters(val.toCharArray(), 0, val.length());
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
        if(TransformerImpl.S_DEBUG)
        getTraceManager().fireTraceEvent(child, mode, template);
        
        // And execute the child templates.
		if(template instanceof org.apache.xalan.processor.CompiledTemplate)
			template.execute(this,child,mode);
		else
			executeChildTemplates(template, child, mode);
      }
    }
    finally
    {
      m_currentMatchTemplates.pop();
      m_currentMatchNodes.pop();
    }
    return true;
  }
  
  /** 
   * <meta name="usage" content="advanced"/>
   * Execute each of the children of a template element.
   * 
   * @param transformer The XSLT transformer instance.
   * @param sourceNode The current context node.
   * @param mode The current mode.
   * @exception SAXException Might be thrown from the  document() function, or
   *      from xsl:include or xsl:import.
   */
  public void executeChildTemplates(ElemTemplateElement elem, 
                              Node sourceNode,
                              QName mode, ContentHandler handler)
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
   * @param sourceNode The current context node.
   * @param mode The current mode.
   * @exception SAXException Might be thrown from the  document() function, or
   *      from xsl:include or xsl:import.
   */
  public void executeChildTemplates(ElemTemplateElement elem, 
                              Node sourceNode,
                              QName mode)
    throws SAXException
  {    
    // Does this element have any children?
    ElemTemplateElement firstChild = elem.getFirstChildElem();
    if(null == firstChild)    
      return;      
    
    XPathContext xctxt = getXPathContext();

    // Check for infinite loops if we have to.
    boolean check = (getRecursionLimit() > -1);
    if (check)
      getStackGuard().push(elem, sourceNode);
    
    // We need to push an element frame in the variables stack, 
    // so all the variables can be popped at once when we're done.
    VariableStack varstack = getXPathContext().getVarStack();
    varstack.pushElemFrame();
    
    Locator savedLocator = xctxt.getSAXLocator();
    try
    {
      // Loop through the children of the template, calling execute on 
      // each of them.
      for (ElemTemplateElement t = firstChild; t != null; 
           t = t.getNextSiblingElem()) 
      {
        xctxt.setSAXLocator(t);
        try
        {
          pushElemTemplateElement(t, sourceNode);
          t.execute(this, sourceNode, mode);
        }
        finally
        {
          popElemTemplateElement();
        }
      }
    }
    finally
    {
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
   */
  private Vector processSortKeys(ElemTemplateElement xslInstruction,
                                 Node sourceNodeContext)
    throws SAXException
  {
    Vector keys = null;
    int tok = xslInstruction.getXSLToken();
    if((Constants.ELEMNAME_APPLY_TEMPLATES == tok) ||
       (Constants.ELEMNAME_FOREACH == tok))
    {
      XPathContext xctxt = getXPathContext();
      ElemForEach foreach = (ElemForEach)xslInstruction;
      int nElems = foreach.getSortElemCount();
      
      if(nElems > 0)
        keys = new Vector();
      
      // March backwards, collecting the sort keys.
      for(int i = 0; i < nElems; i++)
      {
        ElemSort sort = foreach.getSortElem(i);
        String langString = (null != sort.getLang())
                            ? sort.getLang().evaluate(xctxt, 
                                                      sourceNodeContext, 
                                                      xslInstruction, 
                                                      new StringBuffer())
                              : null;
        String dataTypeString 
          = sort.getDataType().evaluate(xctxt, 
                                        sourceNodeContext, 
                                        xslInstruction, 
                                        new StringBuffer());
        if (dataTypeString.indexOf(":") >= 0 )
          System.out.println("TODO: Need to write the hooks for QNAME sort data type");        
        else if (!(dataTypeString.equalsIgnoreCase(Constants.ATTRVAL_DATATYPE_TEXT)) && 
			      !(dataTypeString.equalsIgnoreCase(Constants.ATTRVAL_DATATYPE_NUMBER)) )
			      xslInstruction.error(XSLTErrorResources.ER_ILLEGAL_ATTRIBUTE_VALUE, new Object[] {Constants.ATTRNAME_DATATYPE, dataTypeString});   
			
        boolean treatAsNumbers 
          = ((null != dataTypeString)&& 
             dataTypeString.equals(Constants.ATTRVAL_DATATYPE_NUMBER)) ? 
            true : false;
        String orderString 
          = sort.getOrder().evaluate(xctxt, sourceNodeContext, 
                                     xslInstruction, 
                                     new StringBuffer());
        if (!(orderString.equalsIgnoreCase(Constants.ATTRVAL_ORDER_ASCENDING)) && 
			      !(orderString.equalsIgnoreCase(Constants.ATTRVAL_ORDER_DESCENDING)))
			      xslInstruction.error(XSLTErrorResources.ER_ILLEGAL_ATTRIBUTE_VALUE, new Object[] {Constants.ATTRNAME_ORDER, orderString});   
			
        boolean descending = ((null != orderString) &&  
                              orderString.equals(Constants.ATTRVAL_ORDER_DESCENDING))? 
                             true : false;

        AVT caseOrder = sort.getCaseOrder();
        boolean caseOrderUpper;
        if(null != caseOrder)
        {
          String caseOrderString 
            = caseOrder.evaluate(xctxt, 
                                 sourceNodeContext, 
                                 xslInstruction, 
                                 new StringBuffer());
          if (!(caseOrderString.equalsIgnoreCase(Constants.ATTRVAL_CASEORDER_UPPER)) && 
			      !(caseOrderString.equalsIgnoreCase(Constants.ATTRVAL_CASEORDER_LOWER)))
			      xslInstruction.error(XSLTErrorResources.ER_ILLEGAL_ATTRIBUTE_VALUE, new Object[] {Constants.ATTRNAME_CASEORDER, caseOrderString});   
			
          caseOrderUpper = ((null != caseOrderString)&& 
                            caseOrderString.equals(Constants.ATTRVAL_CASEORDER_UPPER)) ? 
                           true : false;
        }
        else
        {
          caseOrderUpper = false;
        }

        keys.addElement(new NodeSortKey(this, sort.getSelect(), 
                                        treatAsNumbers, 
                                        descending, langString, 
                                        caseOrderUpper,xslInstruction));
      }
    }
    return keys;
  }
  
  //==========================================================
  // SECTION: TransformState implementation
  //==========================================================
  
  private Stack m_currentTemplateElements = new Stack();
  private Stack m_currentNodes = new Stack();
  
  /**
   * Push the current template element.
   */
  public void pushElemTemplateElement(ElemTemplateElement elem, Node currentNode)
  {
    m_currentTemplateElements.push(elem);
    m_currentNodes.push(currentNode);
  }
  
  /**
   * Pop the current template element.
   */
  public void popElemTemplateElement()
  {
    m_currentTemplateElements.pop();
    m_currentNodes.pop();
  }
  
  /**
   * Retrieves the stylesheet element that produced 
   * the SAX event.
   */
  public ElemTemplateElement getCurrentElement()
  {
    return (ElemTemplateElement)m_currentTemplateElements.peek();
  }

  /**
   * This method retrieves the current context node 
   * in the source tree.
   */
  public Node getCurrentNode()
  {
    return (Node)m_currentNodes.peek();
  }
  
  /**
   * This method retrieves the xsl:template 
   * that is in effect, which may be a matched template 
   * or a named template.
   * 
   * <p>Please note that the ElemTemplate returned may 
   * be a default template, and thus may not have a template 
   * defined in the stylesheet.</p>
   */
  public ElemTemplate getCurrentTemplate()
  {
    ElemTemplateElement elem = getCurrentElement();
    while((null != elem) && (elem.getXSLToken() != Constants.ELEMNAME_TEMPLATE))
      elem = elem.getParentElem();
    return (ElemTemplate)elem;
  }

  private Stack m_currentMatchTemplates = new Stack();
  private Stack m_currentMatchNodes = new Stack();

  /**
   * This method retrieves the xsl:template 
   * that was matched.  Note that this may not be 
   * the same thing as the current template (which 
   * may be from getCurrentElement()), since a named 
   * template may be in effect.
   */
  public ElemTemplate getMatchedTemplate()
  {
    return (ElemTemplate)m_currentMatchTemplates.peek();
  }

  /**
   * Retrieves the node in the source tree that matched 
   * the template obtained via getMatchedTemplate().
   */
  public Node getMatchedNode()
  {
    return (Node)m_currentMatchNodes.peek();
  }
  
  /**
   * Get the current context node list.
   */
  public NodeIterator getContextNodeList()
  {
    try
    {
      return getXPathContext().getContextNodeList().cloneWithReset();
    }
    catch(CloneNotSupportedException cnse)
    {
      // should never happen.
      return null;
    }
  }
  
  /**
   * Get the TrAX Transformer object in effect.
   */
  public Transformer getTransformer()
  {
    return this;
  }
  
  
  //==========================================================
  // SECTION: Member variables
  //==========================================================
  
  /**
   * The root of a linked set of stylesheets.
   */
  private StylesheetRoot m_stylesheetRoot = null;
  
  /**
   * Set the stylesheet for this processor.  If this is set, then the
   * process calls that take only the input .xml will use
   * this instead of looking for a stylesheet PI.  Also,
   * setting the stylesheet is needed if you are going
   * to use the processor as a SAX ContentHandler.
   */
  public void setStylesheet(StylesheetRoot stylesheetRoot)
  {
    m_stylesheetRoot = stylesheetRoot;
  }

  /**
   * Get the current stylesheet for this processor.
   */
  public StylesheetRoot getStylesheet()
  {
    return m_stylesheetRoot;
  }
  
  /**
   * Used for default selection.
   */
  private XPath m_selectDefault;

  /**
   * If this is set to true, do not warn about pattern
   * match conflicts.
   */
  private boolean m_quietConflictWarnings = false;
  
  /**
   * Get quietConflictWarnings property.
   */
  public boolean getQuietConflictWarnings()
  {
    return m_quietConflictWarnings;
  }

  /**
   * If the quietConflictWarnings property is set to
   * true, warnings about pattern conflicts won't be
   * printed to the diagnostics stream.
   * True by default.
   * @param b true if conflict warnings should be suppressed.
   */
  public void setQuietConflictWarnings(boolean b)
  {
    m_quietConflictWarnings = b;
  }

  /**
   * The liason to the XML parser, so the XSL processor
   * can handle included files, and the like, and do the
   * initial parse of the XSL document.
   */
  private XPathContext m_xcontext;
  
  /**
   * Set the execution context for XPath.
   */
  public void setXPathContext(XPathContext xcontext)
  {
    m_xcontext = xcontext;
  }

  /**
   * Get the XML Parser Liaison that this processor uses.
   */
  public XPathContext getXPathContext()
  {
    return m_xcontext;
  }

  /**
   * Object to guard agains infinite recursion when
   * doing queries.
   */
  private StackGuard m_stackGuard = new StackGuard();
  
  /**
   * <meta name="usage" content="internal"/>
   * Get the object used to guard the stack from 
   * recursion.
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
   */
  public void setRecursionLimit(int limit)
  {
    m_stackGuard.setRecursionLimit(limit);
  }


  /**
   * Output handler to bottleneck SAX events.
   */
  private ResultTreeHandler m_resultTreeHandler = new ResultTreeHandler(this);
  
  /**
   * Get the ResultTreeHandler object.
   */
  public ResultTreeHandler getResultTreeHandler()
  {
    return m_resultTreeHandler;
  }
  
  private KeyManager m_keyManager = new KeyManager();
  
  /**
   * Get the KeyManager object.
   */
  public KeyManager getKeyManager()
  {
    return m_keyManager;
  }

  /**
   * Stack for the purposes of flagging infinite recursion with
   * attribute sets.
   */
  private Stack m_attrSetStack = null;
  
  /**
   * Check to see if this is a recursive attribute definition.
   */
  public boolean isRecursiveAttrSet(ElemAttributeSet attrSet)
  {
    if(null == m_attrSetStack)
    {
      m_attrSetStack = new Stack();
    }
    if(!m_attrSetStack.empty())
    {
      int loc = m_attrSetStack.search(this);
      if(loc > -1)
      {
        return true;
      }
    }
    return false;
  }
  
  /**
   * Push an executing attribute set, so we can check for 
   * recursive attribute definitions.
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
   * The table of counters for xsl:number support.
   * @see ElemNumber
   */
  private CountersTable m_countersTable = null;
  
  /**
   * Get the table of counters, for optimized xsl:number support.
   */
  public CountersTable getCountersTable()
  {
    if(null == m_countersTable)
      m_countersTable = new CountersTable();
    return m_countersTable;
  }

  /**
   * Is > 0 when we're processing a for-each
   */
  private BoolStack m_currentTemplateRuleIsNull = new BoolStack();
  
  /**
   * Tell if the current template rule is null.
   */
  public boolean currentTemplateRuleIsNull()
  {
    return ((!m_currentTemplateRuleIsNull.isEmpty()) 
            && (m_currentTemplateRuleIsNull.peek() == true));
  }
  
  /**
   * Push true if the current template rule is null, false 
   * otherwise.
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
  
  private MsgMgr m_msgMgr;
  
  /**
   * Return the message manager.
   */
  public MsgMgr getMsgMgr()
  {
    if(null == m_msgMgr)
      m_msgMgr = new MsgMgr(this);
    return m_msgMgr;
  }
  
  /**
   * This is a compile-time flag to turn off calling 
   * of trace listeners. Set this to false for optimization purposes.
   */
  public static final boolean S_DEBUG = true;
  
  /**
   * The trace manager.
   */
  private TraceManager m_traceManager = new TraceManager(this);
  
  /**
   * Get an instance of the trace manager for this transformation. 
   * This object can be used to set trace listeners on various 
   * events during the transformation.
   */
  public TraceManager getTraceManager()
  {
    return m_traceManager;
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
   */
  public boolean getFeature (String name)
    throws SAXNotRecognizedException, SAXNotSupportedException
  {
    if("http://xml.org/trax/features/sax/input".equals(name))
      return true;
    else if("http://xml.org/trax/features/dom/input".equals(name))
      return true;
    throw new SAXNotRecognizedException(name);
  }

  ////////////////////////
  // Implement Runnable //  
  ////////////////////////
  
  private Exception m_exceptionThrown;
  
  public Exception getExceptionThrown()
  {
    return m_exceptionThrown;
  }
  
  private InputSource m_xmlSource;
  
  /**
   * Run the transform thread.
   */
  public void run()
  {
    try
    {
      // Node n = ((SourceTreeHandler)getInputContentHandler()).getRoot();
      // transformNode(n);
      getXPathContext().getPrimaryReader().parse(m_xmlSource);
    }
    catch(Exception e)
    {
      // e.printStackTrace();
      m_exceptionThrown = e;
      ; // should have already been reported via the error handler?
    }
  }

} // end TransformerImpl class
