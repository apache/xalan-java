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

import org.apache.xalan.trace.TraceManager;

import org.apache.xalan.utils.DOMBuilder;
import org.apache.xalan.utils.NodeVector;
import org.apache.xalan.utils.BoolStack;
import org.apache.xalan.utils.QName;

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
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.Serializer;
import org.apache.xml.serialize.SerializerFactory;
import org.apache.xml.serialize.Method;

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

// TRaX Imports
import trax.Result;
import trax.Transformer;
import trax.TransformException;
import trax.URIResolver;

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
public class TransformerImpl extends XMLFilterImpl implements Transformer
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
        
        // Get the input content handler, which will handle the 
        // parse events and create the source tree.
        ContentHandler inputHandler = getInputContentHandler();
        reader.setContentHandler( inputHandler );
        reader.setProperty("http://xml.org/sax/properties/lexical-handler", inputHandler);
        
        getXPathContext().setPrimaryReader(reader);
        
        // Kick off the parse.  When the ContentHandler gets 
        // the startDocument event, it will call transformNode( node ).
        reader.parse( xmlSource );
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
  ContentHandler createResultContentHandler(Result outputTarget)
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
      // Get the output format that was set by the user, otherwise get the 
      // output format from the stylesheet.
      OutputFormat format = (null == m_outputFormat) 
                            ? getStylesheet().getOutputComposed() :
                              m_outputFormat;
      
      String method = format.getMethod();
      if(null == method)
        method = Method.XML;
      SerializerFactory sfactory = SerializerFactory.getSerializerFactory(method);
      
      try
      {
        // System.out.println("createResultContentHandler -- format.getIndenting: "
        //                   +format.getIndenting());
        Serializer serializer 
          = (null != outputTarget.getCharacterStream()) ?
            sfactory.makeSerializer(outputTarget.getCharacterStream(), format) :
            sfactory.makeSerializer(outputTarget.getByteStream(), format);
        handler = serializer.asContentHandler();
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
      this.transformNode(null, null, node, null);
      if((null != m_resultTreeHandler) && !m_resultTreeHandler.getFoundEndDoc())
      {
        m_resultTreeHandler.endDocument();
        this.m_resultTreeHandler.flushPending();
      }
    }
    catch(SAXException se)
    {
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
   */
  public DocumentFragment transformToRTF(
                                        Stylesheet stylesheetTree,
                                        ElemTemplateElement templateParent,
                                        Node sourceNode, QName mode)
    throws SAXException
  {
    XPathContext xctxt = getXPathContext();
    Document docFactory = xctxt.getDOMHelper().getDOMFactory();

    // Create a ResultTreeFrag object.
    ResultTreeFrag resultFragment 
      = new ResultTreeFrag(docFactory, xctxt);

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
      SerializerFactory sfactory = SerializerFactory.getSerializerFactory("text");
      sw = new StringWriter();
      OutputFormat format = new OutputFormat();
      format.setPreserveSpace(true);
      Serializer serializer = sfactory.makeSerializer(sw, format);
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
        m_selectDefault = new XPath("node()", template, template, XPath.SELECT);

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
        }
        sorter.sort(nodeList, keys, xctxt);
        nodeList.setCurrentPos(0);
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

    // To find templates, use the the root of the import tree if 
    // this element is not an xsl:apply-imports or starting from the root, 
    // otherwise use the root of the stylesheet tree.
    // TODO: Not sure apply-import handling is correct right now.  -sb
    StylesheetComposed stylesheetTree = (null == template) 
                                        ? getStylesheet() 
                                          : template.getStylesheetComposed();

    XPathContext xctxt = getXPathContext();
    boolean isDefaultTextRule = false;
    
    if(null == template)
    {
      // Find the XSL template that is the best match for the 
      // element.        
      template = stylesheetTree.getTemplateComposed(xctxt, 
                                                    child, mode,
                                                    getQuietConflictWarnings());
      
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
      // Fire a trace event for the template.
      if(TransformerImpl.S_DEBUG)
        getTraceManager().fireTraceEvent(child, mode, template);
      
      // And execute the child templates.
      executeChildTemplates(template, child, mode);
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
        t.execute(this, sourceNode, mode);
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
        boolean treatAsNumbers 
          = ((null != dataTypeString)&& 
             dataTypeString.equals(Constants.ATTRVAL_DATATYPE_NUMBER)) ? 
            true : false;
        String orderString 
          = sort.getOrder().evaluate(xctxt, sourceNodeContext, 
                                     xslInstruction, 
                                     new StringBuffer());
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

  

} // end TransformerImpl class
