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
package org.apache.xalan.xslt;

import org.apache.xpath.*;
import org.apache.xpath.compiler.XPathParser;
import org.apache.xpath.compiler.Compiler;
import org.apache.xalan.xpath.XObject;
import org.apache.xalan.xpath.XString;
import org.apache.xalan.xpath.XNodeSet;
import org.apache.xalan.xpath.XBoolean;
import org.apache.xalan.xpath.XNumber;
import org.apache.xalan.xpath.XNull;

import org.w3c.dom.*;
import org.w3c.dom.traversal.NodeIterator;
import org.xml.sax.ContentHandler;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import org.xml.sax.ext.*;
import org.xml.sax.helpers.ParserAdapter;
import org.apache.xml.serialize.XMLSerializer;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.Templates;
import javax.xml.transform.Source;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.ErrorListener;

import java.util.*;
import java.net.*;
import java.io.*;
import java.lang.reflect.*;

import org.apache.xalan.templates.Stylesheet;
//import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xalan.xpath.xml.XMLParserLiaison;
import org.apache.xalan.xpath.xml.ProblemListenerDefault;
import org.apache.xalan.xpath.xml.ProblemListener;
import org.apache.xalan.templates.StylesheetComposed;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.processor.StylesheetHandler;
import org.apache.xalan.processor.TransformerFactoryImpl;
//import org.apache.xalan.processor.ProcessorStylesheet;
//import org.apache.xalan.xslt.StylesheetSpec;
import org.apache.xalan.trace.*;
import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.res.XSLMessages;
import org.apache.xml.utils.PrefixResolverDefault;
import org.apache.xml.utils.TreeWalker;
import org.apache.xml.utils.QName;
import org.apache.xml.utils.DefaultErrorHandler;
import org.apache.xalan.transformer.TransformerHandlerImpl;

// Imported JAVA API for XML Parsing 1.0 classes
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


/**
 * <meta name="usage" content="advanced"/>
 * The Xalan workhorse -- Collaborates with the XML parser liaison, the DOM,
 * and the XPath engine, to transform a source tree of nodes into a result tree
 * according to instructions and templates specified by a stylesheet tree.
 * We suggest you use one of the
 * static XSLTProcessorFactory getProcessor() methods to instantiate the processor
 * and return an interface that greatly simplifies the process of manipulating
 * XSLTEngineImpl.
 *
 * <p>The methods <code>process(...)</code> are the primary public entry points.
 * The best way to perform transformations is to use the
 * {@link org.apache.xalan.xslt.XSLTProcessor#process(org.apache.xalan.xslt.XSLTInputSource, org.apache.xalan.xslt.XSLTInputSource, org.apache.xalan.xslt.XSLTResultTarget)} method,
 * but you may use any of process methods defined in XSLTEngineImpl.</p>
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
 * @deprecated This compatibility layer will be removed in later releases. 
 */
public class XSLTEngineImpl implements  XSLTProcessor
{
  //private Processor m_processor;
  private TransformerFactory m_tfactory;
  private TransformerImpl m_transformerImpl;
  private DOM2Helper m_liaison;
  private String m_outputFileName;
  private DocumentHandler m_documentHandler = null;
  private ProblemListenerDefault m_problemListener;
  private Hashtable m_stylesheetParams;
  StylesheetRoot m_stylesheetRoot = null;
  
  Vector m_evalList = null;
  boolean m_needToEval = false;
  
  /*
  * If this is true, then the diag function will
  * be called.
  */
  private boolean m_traceTemplateChildren = false;

  /*
  * If this is true, then the simple tracing of templates
  * will be performed.
  */
  private boolean m_traceTemplates = false;
  
  /*
  * If this is true, then diagnostics of each select
  * will be performed.
  */
  boolean m_traceSelects = false;

  /*
  * A stream to print diagnostics to.
  */
  java.io.PrintWriter m_diagnosticsPrintWriter = null;

  /* For diagnostics */
  Hashtable m_durationsTable = new Hashtable();
  
  /**
   * A XSLMessages instance capable of producing user messages.
   */
  private static XSLMessages m_XSLMessages = new XSLMessages();
  
    
 /**
   * Construct an XSLT processor that uses the default DTM (Document Table Model) liaison
   * and XML parser. As a general rule, you should use XSLTProcessorFactory to create an
   * instance of this class and provide access to the instance via the XSLTProcessor interface.
   *
   * @see XSLTProcessorFactory
   * @see XSLTProcessor
   */
  protected XSLTEngineImpl()
    throws org.xml.sax.SAXException
  {    
    m_tfactory = TransformerFactory.newInstance();
    m_problemListener = new ProblemListenerDefault();
    //m_liaison =  (DOM2Helper)createLiaison();
  }

  /**
   * Construct an XSLT processor that uses the the given parser liaison.
   * As a general rule, you should use XSLTProcessorFactory to create an
   * instance of this class and provide access to the instance via the XSLTProcessor interface.
   *
   * @see XSLTProcessorFactory
   * @see XSLTProcessor
   */
  public XSLTEngineImpl(String liaisonClassName)
    throws SAXException 
  {   
    m_tfactory = TransformerFactory.newInstance();
    m_problemListener = new ProblemListenerDefault();
    
    try 
      {
        m_liaison =  (DOM2Helper)(Class.forName(liaisonClassName).newInstance());
        //org.apache.xpath.XPathContext xctxt = this.getTransformer().getXPathContext();

        //xctxt.setDOMHelper(m_liaison);        
      } 
      catch (ClassNotFoundException e1) 
      {
        throw new SAXException("XML Liaison class " + liaisonClassName +
          " specified but not found", e1);
      } 
      catch (IllegalAccessException e2) 
      {
          throw new SAXException("XML Liaison class " + liaisonClassName +
            " found but cannot be loaded", e2);
      } 
      catch (InstantiationException e3) 
      {
          throw new SAXException("XML Liaison class " + liaisonClassName +
            " loaded but cannot be instantiated (no empty public constructor?)",
            e3);
      } 
      catch (ClassCastException e4) 
      {
          throw new SAXException("XML Liaison class " + liaisonClassName +
            " does not implement DOM2Helper", e4);
      }
   
  }
    
    

  /**
   * Construct an XSL processor that uses the the given parser liaison.
   * As a general rule, you should use XSLTProcessorFactory to create an
   * instance of this class and provide access to the instance via the XSLTProcessor interface.
   *
   * @param XMLParserLiaison A liaison to an XML parser.
   *
   * @see org.apache.xalan.xpath.xml.XMLParserLiaison
   * @see XSLTProcessorFactory
   * @see XSLTProcessor
   */
  public XSLTEngineImpl(XMLParserLiaison parserLiaison)
    throws org.xml.sax.SAXException
  {
    m_tfactory = TransformerFactory.newInstance();
    m_problemListener = new ProblemListenerDefault();
    
    m_liaison =  (DOM2Helper)parserLiaison;
   // org.apache.xpath.XPathContext xctxt = this.getTransformer().getXPathContext();

    //xctxt.setDOMHelper(m_liaison);    
  }

  /**
   * Construct an XSLT processor that can call back to the XML parser, in order to handle
   * included files and the like.
   *
   * @param XMLParserLiaison A liaison to an XML parser.
   *
   * @see org.apache.xalan.xpath.xml.XMLParserLiaison
   * @see XSLTProcessorFactory
   * @see XSLTProcessor
   */
  XSLTEngineImpl(XMLParserLiaison parserLiaison, XPathFactory xpathFactory)    
    throws SAXException
  {   
    m_tfactory = TransformerFactory.newInstance();
    m_problemListener = new ProblemListenerDefault();
    
    m_liaison =  (DOM2Helper)parserLiaison;
   // org.apache.xpath.XPathContext xctxt = this.getTransformer().getXPathContext();

    //xctxt.setDOMHelper(m_liaison);    
  }
  
  /** 
   * Get a Liaison class
   */
  public XMLParserLiaison createLiaison()
    throws org.xml.sax.SAXException
  {
    return new org.apache.xalan.xpath.xml.XMLParserLiaisonDefault();    
  }
 
  /**
   * Reset the state.  This needs to be called after a process() call
   * is invoked, if the processor is to be used again.
   */
  public void reset()
  {
    if (m_transformerImpl != null)
      m_transformerImpl.reset(); 
    m_stylesheetParams = null;
  }
  
 

  /**
   * Transform the source tree to the output in the given
   * result tree target. As a general rule, we recommend you use the
   * {@link org.apache.xalan.xslt.XSLTProcessor#process(org.apache.xalan.xslt.XSLTInputSource, org.apache.xalan.xslt.XSLTInputSource, org.apache.xalan.xslt.XSLTResultTarget)} method.
   * @param inputSource  The input source.
   * @param stylesheetSource  The stylesheet source.  May be null if source has a xml-stylesheet PI.
   * @param outputTarget The output source tree.
   * @exception XSLProcessorException thrown if the active ProblemListener and XMLParserLiaison decide
   * the error condition is severe enough to halt processing.
   */
  public void process( XSLTInputSource inputSource,
                       XSLTInputSource stylesheetSource,
                       XSLTResultTarget outputTarget)
    throws SAXException
  {
    try
    {
      Boolean totalTimeID = new Boolean(true);
      pushTime(totalTimeID);
      Node sourceTree = null;
      
      Templates templates = null;
      if (m_needToEval)
      {
        Node node = null;
        if (null != stylesheetSource)
        {
          Source ssSource = stylesheetSource.getSourceObject();
          if(ssSource instanceof DOMSource)
          {
            node = ((DOMSource)ssSource).getNode();
          }
        }
        if (null == node)
        {
          node = new DOM2Helper().createDocument() ; 
        }
        for (int i=0; i< m_evalList.size(); i++)
        {
          String name = (String)m_evalList.elementAt(i);
          String expression = (String)m_stylesheetParams.get(name);
          try{
            org.apache.xpath.objects.XObject val = org.apache.xpath.XPathAPI.eval(node, expression);
            
            m_stylesheetParams.put(name, val);
          }
          catch(TransformerException te)
          {
            throw new SAXException(te);
          }
        }
        m_needToEval = false;
        m_evalList = null;
      }
      
      sourceTree = getSourceTreeFromInput(inputSource);
      
      if(null != stylesheetSource)
      {
        try{
        templates = m_tfactory.newTemplates(stylesheetSource.getSourceObject());
        }
        catch (TransformerConfigurationException tce)
        {
          throw new SAXException(tce);
        }  
        
      }      
      else if( null != inputSource)
      { 
        if(null != sourceTree)
        {
          String stylesheetURI = null;
          Stack hrefs = new Stack();
          for(Node child=sourceTree.getFirstChild(); null != child; child=child.getNextSibling())
          {
            if(Node.PROCESSING_INSTRUCTION_NODE == child.getNodeType())
            {
              ProcessingInstruction pi = (ProcessingInstruction)child;
              if(pi.getNodeName().equals("xml-stylesheet")
                 || pi.getNodeName().equals("xml:stylesheet"))
              {
                boolean isOK = true;
                StringTokenizer tokenizer = new StringTokenizer(pi.getNodeValue(), " \t=");
                while(tokenizer.hasMoreTokens())
                {
                  if(tokenizer.nextToken().equals("type"))
                  {
                    String typeVal = tokenizer.nextToken();
                    typeVal = typeVal.substring(1, typeVal.length()-1);
                    if(!typeVal.equals("text/xsl") && !typeVal.equals("text/xml") && !typeVal.equals("application/xml+xslt"))
                    {
                      isOK = false;
                    }
                  }
                }

                if(isOK)
                {
                  tokenizer = new StringTokenizer(pi.getNodeValue(), " \t=");
                  while(tokenizer.hasMoreTokens())
                  {
                    if(tokenizer.nextToken().equals("href"))
                    {
                      stylesheetURI = tokenizer.nextToken();
                      stylesheetURI = stylesheetURI.substring(1, stylesheetURI.length()-1);
                      hrefs.push(stylesheetURI);
                    }
                  }                
                }
              }
            }
          } // end for(int i = 0; i < nNodes; i++)
          boolean isRoot = true;
          Stylesheet prevStylesheet = null;
          while(!hrefs.isEmpty())
          {
            Stylesheet stylesheet = getStylesheetFromPIURL((String)hrefs.pop(), sourceTree,
                                                           (null != inputSource)
                                                           ? inputSource.getSystemId() : null,
                                                           isRoot);
            if(false == isRoot)
            {
              prevStylesheet.setImport((StylesheetComposed)stylesheet);
            }
            prevStylesheet = stylesheet;
            isRoot = false;
          }
        }        
      }
      else
      {
        error(XSLTErrorResources.ER_NO_INPUT_STYLESHEET); //"Stylesheet input was not specified!");
      }
      
     
      if(null == templates)
      {
        if (m_stylesheetRoot != null)
          templates = m_stylesheetRoot.getObject();
        else
        {  
          error(XSLTErrorResources.ER_FAILED_PROCESS_STYLESHEET); //"Failed to process stylesheet!");
          return;
        }  
      }

      if(null != templates)
      {
        try{
          m_transformerImpl = (TransformerImpl)templates.newTransformer(); 
          if (m_problemListener != null)
            m_transformerImpl.setErrorListener(m_problemListener);
        //  if (m_liaison != null)
        //    m_transformerImpl.getXPathContext().setDOMHelper(m_liaison);
   
        }
        catch (TransformerConfigurationException tce)
        {
          throw new SAXException(tce);
        }  
        
        if (m_stylesheetParams != null)
        {
          Enumeration keys = m_stylesheetParams.keys();
          while (keys.hasMoreElements())
          {
            String name = (String)keys.nextElement();
            Object value = m_stylesheetParams.get(name); 
            m_transformerImpl.setParameter(name, null, value);
          } 
        }  
        
        try{
          m_transformerImpl.transform(new DOMSource(sourceTree),
                    outputTarget.getResultObject());
        }
        catch (TransformerException te)
        {
          throw new SAXException(te);
        }  
        if(null != m_diagnosticsPrintWriter)
        {
          displayDuration("Total time", totalTimeID);
        }
      }
    }
    catch(MalformedURLException mue)
    {
      error(XSLTErrorResources.ERROR0000, new Object[] {mue.getMessage()}, mue);
      // throw se;
    }
    catch(FileNotFoundException fnfe)
    {
      error(XSLTErrorResources.ERROR0000, new Object[] {fnfe.getMessage()}, fnfe);
      // throw se;
    }
    catch(IOException ioe)
    {
      error(XSLTErrorResources.ERROR0000, new Object[] {ioe.getMessage()}, ioe);
      // throw se;
    }
    catch(SAXException se)
    {
      error(XSLTErrorResources.ER_SAX_EXCEPTION, se); //"SAX Exception", se);
      // throw se;
    }
  }

  /**
   * Bottleneck the creation of the stylesheet for derivation purposes.
   */
  public StylesheetRoot createStylesheetRoot(String baseIdentifier)
    throws MalformedURLException, FileNotFoundException,
           IOException, SAXException
  {
    try{
      if (baseIdentifier == null)
        return new StylesheetRoot(this, baseIdentifier); 
      
      Source inSource = new XSLTInputSource(baseIdentifier).getSourceObject();
      Templates templates = m_tfactory.newTemplates(inSource);
      StylesheetRoot stylesheet = new StylesheetRoot((org.apache.xalan.templates.StylesheetRoot)templates);      
      return stylesheet;      
    }
    catch (TransformerConfigurationException tce)
    {
      throw new SAXException(tce);
    }
  }
  
  /**
   * Bottleneck the creation of the stylesheet for derivation purposes.
   */
  StylesheetRoot createStylesheetRoot(String baseIdentifier, XSLTInputSource source)
    throws MalformedURLException, FileNotFoundException,
           IOException, SAXException
  {
    try{
      Source inSource = source.getSourceObject();
      Templates templates = m_tfactory.newTemplates(inSource);
      StylesheetRoot stylesheet = new StylesheetRoot((org.apache.xalan.templates.StylesheetRoot)templates);      
      return stylesheet;      
    }
    catch (TransformerConfigurationException tce)
    {
      throw new SAXException(tce);
    }
  }

  /**
   * Given a URI to an XSL stylesheet,
   * Compile the stylesheet into an internal representation.
   * This calls reset() before processing if the stylesheet root has been set
   * to non-null.
   * @param xmldocURLString  The URL to the input XML document.
   * @return The compiled stylesheet object.
   * @exception XSLProcessorException thrown if the active ProblemListener and XMLParserLiaison decide
   * the error condition is severe enough to halt processing.
   */
  public StylesheetRoot processStylesheet(XSLTInputSource stylesheetSource)
    throws SAXException
  {
    try{
      if(null != ((TransformerFactoryImpl)m_tfactory).newTemplatesHandler().getTemplates())
        reset();
    }
    catch (TransformerConfigurationException tce)
    {
      throw new SAXException(tce);
    }

    String xslIdentifier = ((null == stylesheetSource) ||
                            (null == stylesheetSource.getSystemId()))
                           ? "Input XSL" : stylesheetSource.getSystemId();

    // In case we have a fragment identifier, go ahead and
    // try and parse the XML here.
    m_stylesheetRoot = null;
    try
    {
      StylesheetHandler stylesheetProcessor
          = new StylesheetHandler((TransformerFactoryImpl)m_tfactory); //this, m_stylesheetRoot); 
        
      Source ssSource = stylesheetSource.getSourceObject();
      if(ssSource instanceof DOMSource)
      {
        if(((DOMSource)ssSource).getNode() instanceof StylesheetRoot)
        {
          m_stylesheetRoot = (StylesheetRoot)((DOMSource)ssSource).getNode();
        }
        else
        {
          stylesheetProcessor.setSystemId(stylesheetSource.getSystemId());
          TreeWalker tw = new TreeWalker(stylesheetProcessor, new org.apache.xpath.DOM2Helper());
          tw.traverse(((DOMSource)ssSource).getNode());
          m_stylesheetRoot = new StylesheetRoot(stylesheetProcessor.getStylesheetRoot());
        }
      }
      else
      {
				if(null != m_liaison)
        {
         // DOM2Helper liaison =  (DOM2Helper)(Class.forName(liaisonClassName).newInstance());
          m_liaison.parse(SAXSource.sourceToInputSource(ssSource));
					DOMSource source = new DOMSource(m_liaison.getDocument());
					Templates templates = m_tfactory.newTemplates(source);
					m_stylesheetRoot = new StylesheetRoot(
																(org.apache.xalan.templates.StylesheetRoot)templates);																 
        }  
				else
				{
					m_stylesheetRoot = createStylesheetRoot(stylesheetSource.getSystemId(), stylesheetSource);
				}
        addTraceListenersToStylesheet();
        
        
        stylesheetProcessor.pushStylesheet(m_stylesheetRoot.getObject());      
        diag("========= Parsing "+xslIdentifier+" ==========");
        pushTime(xslIdentifier);
        //String liaisonClassName = System.getProperty("org.apache.xalan.source.liaison");

        
        if(null != m_diagnosticsPrintWriter)
          displayDuration("Parse of "+xslIdentifier, xslIdentifier);
      }
    }
    catch(Exception e)
    {
      error(XSLTErrorResources.ER_COULDNT_PARSE_DOC, new Object[] {xslIdentifier}, e); //"Could not parse "+xslIdentifier+" document!", e);
    }
    return m_stylesheetRoot;
  }

  /**
   * Given a URI to an XSL stylesheet,
   * Compile the stylesheet into an internal representation.
   * This calls reset() before processing if the stylesheet root has been set
   * to non-null.
   * @param xmldocURLString  The URL to the input XML document.
   * @return The compiled stylesheet object.
   * @exception XSLProcessorException thrown if the active ProblemListener and XMLParserLiaison decide
   * the error condition is severe enough to halt processing.
   */
  public StylesheetRoot processStylesheet(String xsldocURLString)
    throws SAXException
  {
    try
    {
      XSLTInputSource input = new XSLTInputSource(getURLFromString(xsldocURLString, null).toString());
      return processStylesheet(input);
    }
    catch(SAXException se)
    {
      error(XSLTErrorResources.ER_PROCESSSTYLESHEET_NOT_SUCCESSFUL, se); //"processStylesheet not succesfull!", se);
      return null; // shut up compiler
    }
  }

  /**
   * Set the stylesheet for this processor.  If this is set, then the
   * process calls that take only the input .xml will use
   * this instead of looking for a stylesheet PI.  Also,
   * setting the stylesheet is needed if you are going
   * to use the processor as a SAX DocumentHandler.
   */
  public void setStylesheet(StylesheetRoot stylesheetRoot)
  {
    m_stylesheetRoot = stylesheetRoot;
    org.apache.xalan.templates.StylesheetRoot sr = stylesheetRoot.getObject();
    if (m_transformerImpl == null)
      m_transformerImpl = (TransformerImpl)sr.newTransformer();
    m_transformerImpl.setStylesheet(sr);
    if (m_problemListener != null)
      m_transformerImpl.setErrorListener(m_problemListener);
  }

  /**
   * Get the current stylesheet for this processor.
   */
  public StylesheetRoot getStylesheet()    
  {
    return m_stylesheetRoot;
  }

  /**
   * <meta name="usage" content="internal"/>
   * Get the filename of the output document, if it was set.
   * This is for use by multiple output documents, to determine
   * the base directory for the output document.  It needs to
   * be set by the caller.
   */
  public String getOutputFileName()
  {
    return m_outputFileName;
  }

  /**
   * <meta name="usage" content="internal"/>
   * Set the filename of the output document.
   * This is for use by multiple output documents, to determine
   * the base directory for the output document.  It needs to
   * be set by the caller.
   */
  public void setOutputFileName(String filename)
  {
    m_outputFileName = filename;
  }

  

  /**
   * Given an input source, get the source tree.
   */
  public Node getSourceTreeFromInput(XSLTInputSource inputSource)
    throws org.xml.sax.SAXException
  {
    Node sourceTree = null;
    String xmlIdentifier = ((null == inputSource) ||
                            (null == inputSource.getSystemId()))
                           ? "Input XML" : inputSource.getSystemId();
    
    Source iSource = inputSource.getSourceObject(); 

    if(iSource instanceof DOMSource)
    {
      //if(getXMLProcessorLiaison() instanceof org.apache.xalan.xpath.dtm.DTMLiaison)
       // error(XSLTErrorResources.ER_CANT_USE_DTM_FOR_INPUT);

      sourceTree = ((DOMSource)iSource).getNode();
    }
    else
    {
      // In case we have a fragment identifier, go ahead and
      // try and parse the XML here.
      try
      {
        diag("========= Parsing "+xmlIdentifier+" ==========");
        pushTime(xmlIdentifier);
        
        //String liaisonClassName = System.getProperty("org.apache.xalan.source.liaison");

        if(null != m_liaison)
        {
          //DOM2Helper liaison =  (DOM2Helper)(Class.forName(liaisonClassName).newInstance());
          m_liaison.parse(SAXSource.sourceToInputSource(iSource));
          if(null != m_diagnosticsPrintWriter)
            displayDuration("Parse of "+xmlIdentifier, xmlIdentifier);
          sourceTree = m_liaison.getDocument();
        }
				else
				{
					try
					{

						// Use an implementation of the JAVA API for XML Parsing 1.0 to
						// create a DOM Document node to contain the result.
						DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();

						dfactory.setNamespaceAware(true);
						dfactory.setValidating(false);

						DocumentBuilder docBuilder = dfactory.newDocumentBuilder();
						sourceTree = docBuilder.parse(xmlIdentifier);

					}
					catch (ParserConfigurationException pce)
					{
						error(XSLTErrorResources.ER_COULDNT_PARSE_DOC, new Object[] {xmlIdentifier}, pce); 
					}
				}							
      }
      catch(Exception e)
      {
        // Unwrap exception
        if((e instanceof SAXException) && (null != ((SAXException)e).getException()))
        {
          // ((SAXException)e).getException().printStackTrace();
          e = ((SAXException)e).getException();
        }
        sourceTree = null; // shutup compiler
        error(XSLTErrorResources.ER_COULDNT_PARSE_DOC, new Object[] {xmlIdentifier}, e); //"Could not parse "+xmlIdentifier+" document!", e);
      }
    }

    return sourceTree;
  }

  /**
   * Get Stylesheet from PI URL
   * 
   * @param xslURLString a valid URI to an XSL stylesheet.
   * @param fragbase Document fragment Node.
   * @param xmlBaseIdent Base URI to resolve stylesheet URL 
   * @param isRoot Flag indicating if root node
   */
  Stylesheet getStylesheetFromPIURL(String xslURLString, Node fragBase,
                                    String xmlBaseIdent, boolean isRoot)
    throws SAXException,
    MalformedURLException,
    FileNotFoundException,
    IOException
  {
    Stylesheet stylesheet = null;
    String[] stringHolder =
    {
      null};
    xslURLString = xslURLString.trim();
    int fragIndex = xslURLString.indexOf('#');
    String fragID = null;
    Document stylesheetDoc;
    if(fragIndex == 0)
    {
      diag("Locating stylesheet from fragment identifier...");
      fragID = xslURLString.substring(1);
      
      // Try a bunch of really ugly stuff to find the fragment.
      // What's the right way to do this?

      // Create a XPath parser.
      XPathParser parser = new XPathParser((ErrorListener)m_problemListener.getErrorHandler(), null);
      XPathContext xpathContext = new XPathContext();
      PrefixResolverDefault nsNode = new PrefixResolverDefault(fragBase); //xpathContext.getNamespaceContext();

      NodeIterator nl = null;
      // Create the XPath object.
      try{
      XPath xpath = new XPath(fragID, null, nsNode, XPath.MATCH);
      Compiler compiler = new Compiler();
      // Parse the xpath
      parser.initXPath(compiler, "id("+fragID+")", nsNode);
      org.apache.xpath.objects.XObject xobj = xpath.execute(xpathContext, fragBase, nsNode);

      nl = new org.apache.xml.dtm.ref.DTMNodeIterator(xobj.iter());
      if(nl.nextNode() == null)
      {
        // xobj = Stylesheet.evalXPathStr(getExecContext(), "//*[@id='"+fragID+"']", fragBase, nsNode);
        // Create the XPath object.
        xpath = new XPath(fragID, null, nsNode, XPath.MATCH);

        // Parse the xpath
        parser.initXPath(compiler, "//*[@id='"+fragID+"']", nsNode);
        xobj = xpath.execute(xpathContext, fragBase, nsNode);

        nl = new org.apache.xml.dtm.ref.DTMNodeIterator(xobj.iter());
        if(nl.nextNode() == null)
        {
          // xobj = Stylesheet.evalXPathStr(getExecContext(), "//*[@name='"+fragID+"']", fragBase, nsNode);
          // Create the XPath object.
          xpath = new XPath(fragID, null, nsNode, XPath.MATCH);

          // Parse the xpath
          parser.initXPath(compiler, "//*[@name='"+fragID+"']", nsNode);
          xobj = xpath.execute(xpathContext, fragBase, nsNode);
          nl = new org.apache.xml.dtm.ref.DTMNodeIterator(xobj.iter());
          if(nl.nextNode() == null)
          {
            // Well, hell, maybe it's an XPath...
            // xobj = Stylesheet.evalXPathStr(getExecContext(), fragID, fragBase, nsNode);
            // Create the XPath object.
            //((StylesheetHandler)( m_processor.getTemplatesBuilder())).getLocator()
            xpath = new XPath(fragID, null, nsNode, XPath.MATCH);

            // Parse the xpath
            parser.initXPath(compiler, fragID, nsNode);
            xobj = xpath.execute(xpathContext, fragBase, nsNode);
            nl = new org.apache.xml.dtm.ref.DTMNodeIterator(xobj.iter());
          }
        }
      }
      }
      catch (TransformerException te)
      {
        throw new SAXException(te);
      }
      if(nl.nextNode() == null)
      {
        error(XSLTErrorResources.ER_COULDNT_FIND_FRAGMENT, new Object[] {fragID}); //"Could not find fragment: "+fragID);
      }
      // Use previous because the previous call moved the pointer.
      // or should we use getRoot??
      Node frag = nl.previousNode(); //.item(0);

      if(Node.ELEMENT_NODE == frag.getNodeType())
      {
        pushTime(frag);
        if(isRoot)
        {
          m_stylesheetRoot = createStylesheetRoot(stringHolder[0]);
          stylesheet = m_stylesheetRoot.getObject();
        }
        else
        {
          //stylesheet = new Stylesheet(m_stylesheetRoot);
          // stylesheet = ((StylesheetHandler)(m_processor.getTemplatesBuilder())).getStylesheetRoot();
          try{
            Source source = new XSLTInputSource(fragID).getSourceObject(); 
            Templates templates = m_tfactory.newTemplates(source);
            stylesheet = (org.apache.xalan.templates.StylesheetRoot)templates;
          }
          catch (TransformerConfigurationException tce)
          {
            throw new SAXException(tce);
          }
        }
        addTraceListenersToStylesheet();

        try{
        StylesheetHandler stylesheetProcessor
          = new StylesheetHandler((TransformerFactoryImpl)m_tfactory);
           
        stylesheetProcessor.pushStylesheet(stylesheet);
        TreeWalker tw = new TreeWalker(stylesheetProcessor, new org.apache.xpath.DOM2Helper());
        tw.traverse(frag);

        displayDuration("Setup of "+xslURLString, frag);
        }
        catch (TransformerConfigurationException tce)
        {
          throw new SAXException(tce);
        }
      }
      else
      {
        stylesheetDoc = null;
        error(XSLTErrorResources.ER_NODE_NOT_ELEMENT, new Object[] {fragID}); //"Node pointed to by fragment identifier was not an element: "+fragID);
      }
    }
    else
    {
      // TODO: Use Reader here??
      // hmmm.. for now I'll rely on the XML parser to handle
      // fragment URLs.
      diag(XSLMessages.createMessage(XSLTErrorResources.WG_PARSING_AND_PREPARING, new Object[] {xslURLString})); //"========= Parsing and preparing "+xslURLString+" ==========");
      pushTime(xslURLString);

			URL xslURL = getURLFromString(xslURLString, xmlBaseIdent);

      XSLTInputSource inputSource = new XSLTInputSource(xslURL.toString());
      
			if(isRoot)
			{				
				if(null != m_liaison)
				{
					try{
						m_liaison.parse(SAXSource.sourceToInputSource(inputSource.getSourceObject()));
						DOMSource source = new DOMSource(m_liaison.getDocument());
						Templates templates = m_tfactory.newTemplates(source);
						m_stylesheetRoot = new StylesheetRoot(
																		(org.apache.xalan.templates.StylesheetRoot)templates);
					}					
					catch (TransformerException tce)
					{
						throw new SAXException(tce);
					}
				}
				else
				{
					m_stylesheetRoot = createStylesheetRoot(xslURLString);
				}
				stylesheet = m_stylesheetRoot.getObject();
			}
      else
      {
        stylesheet = new Stylesheet(m_stylesheetRoot.getObject());
      }
      addTraceListenersToStylesheet();

      try{
      org.apache.xalan.processor.StylesheetHandler stylesheetProcessor
        = new StylesheetHandler((TransformerFactoryImpl)m_tfactory); //this, stylesheet); 
      stylesheetProcessor.pushStylesheet(stylesheet);
         // new StylesheetHandler(this, stylesheet);
      }
      catch (TransformerConfigurationException tce)
      {
        throw new SAXException(tce);
      }
      
       
      //m_parserLiaison.setDocumentHandler(stylesheetProcessor);
      //m_parserLiaison.parse(inputSource);

      displayDuration("Parsing and init of "+xslURLString, xslURLString);
    }
    return stylesheet;
  }

        
  /**
   * Take a user string and try and parse XML, and also return 
   * the url.
   * @exception XSLProcessorException thrown if the active ProblemListener and XPathContext decide 
   * the error condition is severe enough to halt processing.
   */
  public static URL getURLFromString(String urlString, String base)
    throws SAXException 
  {
    String origURLString = urlString;
    String origBase = base;
    
    // System.out.println("getURLFromString - urlString: "+urlString+", base: "+base);
    Object doc;
    URL url = null;
    int fileStartType = 0;
    try
    {
      
      if(null != base)
      {
        if(base.toLowerCase().startsWith("file:/"))
        {
          fileStartType = 1;
        }
        else if(base.toLowerCase().startsWith("file:"))
        {
          fileStartType = 2;
        }
      }
      
      boolean isAbsoluteURL;
      
      // From http://www.ics.uci.edu/pub/ietf/uri/rfc1630.txt
      // A partial form can be distinguished from an absolute form in that the
      // latter must have a colon and that colon must occur before any slash
      // characters. Systems not requiring partial forms should not use any
      // unencoded slashes in their naming schemes.  If they do, absolute URIs
      // will still work, but confusion may result.
      int indexOfColon = urlString.indexOf(':');
      int indexOfSlash = urlString.indexOf('/');
      if((indexOfColon != -1) && (indexOfSlash != -1) && (indexOfColon < indexOfSlash))
      {
        // The url (or filename, for that matter) is absolute.
        isAbsoluteURL = true;
      }
      else
      {
        isAbsoluteURL = false;
      }
      
      if(isAbsoluteURL || (null == base) || (base.length() == 0))
      {
        try 
        {
          url = new URL(urlString);
        }
        catch (MalformedURLException e) {}
      }
      // The Java URL handling doesn't seem to handle relative file names.
      else if(!((urlString.charAt(0) == '.') || (fileStartType > 0)))
      {
        try 
        {
          URL baseUrl = new URL(base);
          url = new URL(baseUrl, urlString);
        }
        catch (MalformedURLException e) 
        {
        }
      }
      
      if(null == url)
      {
        // Then we're going to try and make a file URL below, so strip 
        // off the protocol header.
        if(urlString.toLowerCase().startsWith("file:/"))
        {
          urlString = urlString.substring(6);
        }
        else if(urlString.toLowerCase().startsWith("file:"))
        {
          urlString = urlString.substring(5);
        }
      }
      
      if((null == url) && ((null == base) || (fileStartType > 0)))
      {
        if(1 == fileStartType)
        {
          if(null != base)
            base = base.substring(6);
          fileStartType = 1;
        }
        else if(2 == fileStartType)
        {
          if(null != base)
            base = base.substring(5);
          fileStartType = 2;
        }
        
        File f = new File(urlString);
        
        if(!f.isAbsolute() && (null != base))
        {
          // String dir = f.isDirectory() ? f.getAbsolutePath() : f.getParent();
          // System.out.println("prebuiltUrlString (1): "+base);
          StringTokenizer tokenizer = new StringTokenizer(base, "\\/");
          String fixedBase = null;
          while(tokenizer.hasMoreTokens())
          {
            String token = tokenizer.nextToken();
            if (null == fixedBase) 
            {
              // Thanks to Rick Maddy for the bug fix for UNIX here.
              if (base.charAt(0) == '\\' || base.charAt(0) == '/') 
              {
                fixedBase = File.separator + token;
              }
              else 
              {
                fixedBase = token;
              }
            }
            else 
            {
              fixedBase+= File.separator + token;
            }
          }
          // System.out.println("rebuiltUrlString (1): "+fixedBase);
          f = new File(fixedBase);
          String dir = f.isDirectory() ? f.getAbsolutePath() : f.getParent();
          // System.out.println("dir: "+dir);
          // System.out.println("urlString: "+urlString);
          // f = new File(dir, urlString);
          // System.out.println("f (1): "+f.toString());
          // urlString = f.getAbsolutePath();
          f = new File(urlString); 
          boolean isAbsolute =  f.isAbsolute() 
                                || (urlString.charAt( 0 ) == '\\')
                                || (urlString.charAt( 0 ) == '/');
          if(!isAbsolute)
          {
            // Getting more and more ugly...
            if(dir.charAt( dir.length()-1 ) != File.separator.charAt(0) && 
               urlString.charAt( 0 ) != File.separator.charAt(0))
            {
              urlString = dir + File.separator + urlString;
            }
            else
            {
              urlString = dir + urlString;
            }

            // System.out.println("prebuiltUrlString (2): "+urlString);
            tokenizer = new StringTokenizer(urlString, "\\/");
            String rebuiltUrlString = null;
            while(tokenizer.hasMoreTokens())
            {
              String token = tokenizer.nextToken();
              if (null == rebuiltUrlString) 
              {
                // Thanks to Rick Maddy for the bug fix for UNIX here.
                if (urlString.charAt(0) == '\\' || urlString.charAt(0) == '/') 
                {
                  rebuiltUrlString = File.separator + token;
                }
                else 
                {
                  rebuiltUrlString = token;
                }
              }
              else 
              {
                rebuiltUrlString+= File.separator + token;
              }
            }
            // System.out.println("rebuiltUrlString (2): "+rebuiltUrlString);
            if(null != rebuiltUrlString)
              urlString = rebuiltUrlString;
          }
          // System.out.println("fileStartType: "+fileStartType);
          if(1 == fileStartType)
          {
            if (urlString.charAt(0) == '/') 
            {
              urlString = "file://"+urlString;
            }
            else
            {
              urlString = "file:/"+urlString;
            }
          }
          else if(2 == fileStartType)
          {
            urlString = "file:"+urlString;
          }
          try 
          {
            // System.out.println("Final before try: "+urlString);
            url = new URL(urlString);
          }
          catch (MalformedURLException e) 
          {
            // System.out.println("Error trying to make URL from "+urlString);
          }
        }
      }
      if(null == url)
      {
        // The sun java VM doesn't do this correctly, but I'll 
        // try it here as a second-to-last resort.
        if((null != origBase) && (origBase.length() > 0))
        {
          try 
          {
            URL baseURL = new URL(origBase);
            // System.out.println("Trying to make URL from "+origBase+" and "+origURLString);
            url = new URL(baseURL, origURLString);
            // System.out.println("Success! New URL is: "+url.toString());
          }
          catch (MalformedURLException e) 
          {
            // System.out.println("Error trying to make URL from "+origBase+" and "+origURLString);
          }
        }
        
        if(null == url)
        {
          try 
          {
            String lastPart;
            if(null != origBase)
            {
              File baseFile = new File(origBase);
              if(baseFile.isDirectory())
              {
                lastPart = new File(baseFile, urlString).getAbsolutePath ();
              }
              else
              {
                String parentDir = baseFile.getParent();
                lastPart = new File(parentDir, urlString).getAbsolutePath ();
              }
            }
            else
            {
              lastPart = new File (urlString).getAbsolutePath ();
            }
            // Hack
            // if((lastPart.charAt(0) == '/') && (lastPart.charAt(2) == ':'))
            //   lastPart = lastPart.substring(1, lastPart.length() - 1);
            
            String fullpath;
            if (lastPart.charAt(0) == '\\' || lastPart.charAt(0) == '/') 
            {
              fullpath = "file://" + lastPart;
            }
            else
            {
              fullpath = "file:" + lastPart;
            }
            url = new URL(fullpath);
          }
          catch (MalformedURLException e2)
          {
            throw new SAXException("Cannot create url for: " + urlString, e2 ); 
              //XSLMessages.createXPATHMessage(XPATHErrorResources.ER_CANNOT_CREATE_URL, new Object[]{urlString}),e2); //"Cannot create url for: " + urlString, e2 );
          }
        }
      }
    }
    catch(SecurityException se)
    {
      try
      {
        url = new URL("http://xml.apache.org/xslt/"+java.lang.Math.random()); // dummy
      }
      catch (MalformedURLException e2)
      {
        // I give up
      }
    }
    // System.out.println("url: "+url.toString());
    return url;
  }

   /**
   * Add a trace listener for the purposes of debugging and diagnosis.
   * @param tl Trace listener to be added.
   */
  void addTraceListenersToStylesheet()
    throws SAXException
  {
    /*try
    {
      TraceManager tm = m_transformerImpl.getTraceManager();
      if(tm.hasTraceListeners)
      {
        int nListeners = tm.size();
        for(int i = 0; i < nListeners; i++)
        {
          TraceListener tl = (TraceListener)m_traceListeners.elementAt(i);
          if(null != m_stylesheetRoot)
            m_stylesheetRoot.addTraceListener(tl);
        }
      }
    }
    catch(TooManyListenersException tmle)
    {
      throw new SAXException(XSLMessages.createMessage(XSLTErrorResources.ER_TOO_MANY_LISTENERS, null),tmle ); //"addTraceListenersToStylesheet - TooManyListenersException", tmle);
    }*/
  }
  
  /**
   * Warn the user of an problem.
   * This is public for access by extensions.
   * @exception XSLProcessorException thrown if the active ProblemListener and XMLParserLiaison decide
   * the error condition is severe enough to halt processing.
   */
  public void message(String msg)
    throws SAXException
  {
    message(null, null, msg);
  }



  /**
   * Warn the user of an problem.
   * This is public for access by extensions.
   * @exception XSLProcessorException thrown if the active ProblemListener and XMLParserLiaison decide
   * the error condition is severe enough to halt processing.
   */
  public void message(Node styleNode, Node sourceNode, String msg)
    throws SAXException
  {
    m_problemListener.message(msg);
  }

  /**
   * <meta name="usage" content="internal"/>
   * Warn the user of an problem.
   * @exception XSLProcessorException thrown if the active ProblemListener and XMLParserLiaison decide
   * the error condition is severe enough to halt processing.
   */
  public void warn(int msg)
    throws SAXException
  {
    warn(null, null, msg, null);
  }

  /**
   * <meta name="usage" content="internal"/>
   * Warn the user of an problem.
   * @exception XSLProcessorException thrown if the active ProblemListener and XMLParserLiaison decide
   * the error condition is severe enough to halt processing.
   */
  public void warn(int msg, Object[] args)
    throws SAXException
  {
    warn(null, null, msg, args);
  }

  /**
   * <meta name="usage" content="internal"/>
   * Warn the user of an problem.
   * @exception XSLProcessorException thrown if the active ProblemListener and XMLParserLiaison decide
   * the error condition is severe enough to halt processing.
   */
  public void warn(Node styleNode, Node sourceNode, int msg)
    throws SAXException
  {
    warn(styleNode, sourceNode, msg, null);
  }

  /**
   * <meta name="usage" content="internal"/>
   * Warn the user of an problem.
   * @exception XSLProcessorException thrown if the active ProblemListener and XMLParserLiaison decide
   * the error condition is severe enough to halt processing.
   */
  public void warn(Node styleNode, Node sourceNode, int msg, Object args[])
    throws SAXException
  {
    Exception e = null;
    String fmsg = m_XSLMessages.createWarning(msg, args);
    SourceLocator locator = null;
    try{
     locator = ((StylesheetHandler)(((TransformerFactoryImpl)this.m_tfactory).newTemplatesHandler())).getLocator();
    }
    catch (TransformerConfigurationException tce)
    {
    }
    
    DefaultErrorHandler handler;
    if (m_problemListener == null)
      handler = (DefaultErrorHandler)m_tfactory.getErrorListener();
    else
      handler = (DefaultErrorHandler)m_problemListener.getErrorHandler();
    TransformerException te = (null == e) ? new TransformerException(fmsg, locator) :
                                          new TransformerException(fmsg, locator, e);
    if(null != handler)
    {
      try{
      handler.warning(te);
      }
      catch (TransformerException te2)
      {
        throw new SAXException(te2);
      } 
    }
    else
      throw new SAXException(te);
  }

  /**
   * <meta name="usage" content="internal"/>
   * Tell the user of an error, and probably throw an
   * exception.
   * @exception XSLProcessorException thrown if the active ProblemListener and XMLParserLiaison decide
   * the error condition is severe enough to halt processing.
   */
  public void error(String msg)
    throws SAXException
  {
    Exception e = null;
    SourceLocator locator = null;
    try{
    locator = ((StylesheetHandler)(((TransformerFactoryImpl)this.m_tfactory).newTemplatesHandler())).getLocator();
    }
    catch (TransformerConfigurationException tce)
    {
    }
    DefaultErrorHandler handler;
    if (m_problemListener == null)
      handler = (DefaultErrorHandler)m_tfactory.getErrorListener();
    else
      handler = (DefaultErrorHandler)m_problemListener.getErrorHandler();
    TransformerException te = (null == e) ? new TransformerException(msg, locator) :
                                          new TransformerException(msg, locator, e);
    if(null != handler)
    {  
      try{
      handler.fatalError(te);
    }
      catch (TransformerException te2)
      {
        throw new SAXException(te2);
      } 
    }
    else
      throw new SAXException(te);
  }

  /**
   * <meta name="usage" content="internal"/>
   * Tell the user of an error, and probably throw an
   * exception.
   * @exception XSLProcessorException thrown if the active ProblemListener and XMLParserLiaison decide
   * the error condition is severe enough to halt processing.
   */
  public void error(int msg)
    throws SAXException
  {
    error(null, null, msg, null);
  }

  /**
   * <meta name="usage" content="internal"/>
   * Tell the user of an error, and probably throw an
   * exception.
   * @exception XSLProcessorException thrown if the active ProblemListener and XMLParserLiaison decide
   * the error condition is severe enough to halt processing.
   */
  public void error(int msg, Object[] args)
    throws SAXException
  {
    error(null, null, msg, args);
  }

  /**
   * <meta name="usage" content="internal"/>
   * Tell the user of an error, and probably throw an
   * exception.
   * @exception XSLProcessorException thrown if the active ProblemListener and XMLParserLiaison decide
   * the error condition is severe enough to halt processing.
   */
  public void error(int msg, Exception e)
    throws SAXException
  {
    error(msg, null, e);
  }

  /**
   * <meta name="usage" content="internal"/>
   * Tell the user of an error, and probably throw an
   * exception.
   * @exception XSLProcessorException thrown if the active ProblemListener and XMLParserLiaison decide
   * the error condition is severe enough to halt processing.
   */
  public void error(int msg, Object args[], Exception e)
    throws SAXException
  {
    String fmsg = m_XSLMessages.createMessage(msg, args);
    SourceLocator locator = null;
    try{
      locator = ((StylesheetHandler)(((TransformerFactoryImpl)this.m_tfactory).newTemplatesHandler())).getLocator();
    }
    catch (TransformerConfigurationException tce)
    {
    }
    DefaultErrorHandler handler;
    if (m_problemListener == null)
      handler = (DefaultErrorHandler)m_tfactory.getErrorListener();
    else
      handler = (DefaultErrorHandler)m_problemListener.getErrorHandler();
    TransformerException te = (null == e) ? new TransformerException(fmsg, locator) :
                                          new TransformerException(fmsg, locator, e);
    if(null != handler)
    {
      try{
      handler.fatalError(te);
    }
      catch (TransformerException te2)
      {
        throw new SAXException(te2);
      } 
    }
    else
      throw new SAXException(te);
  }

  /**
    * <meta name="usage" content="internal"/>
  * Tell the user of an error, and probably throw an
   * exception.
   * @exception XSLProcessorException thrown if the active ProblemListener and XMLParserLiaison decide
   * the error condition is severe enough to halt processing.
   */
  public void error(Node styleNode, Node sourceNode, int msg)
    throws SAXException
  {
    error(styleNode, sourceNode, msg, null);
  }

  /**
   * <meta name="usage" content="internal"/>
   * Tell the user of an error, and probably throw an
   * exception.
   * @exception XSLProcessorException thrown if the active ProblemListener and XMLParserLiaison decide
   * the error condition is severe enough to halt processing.
   */
  public void error(Node styleNode, Node sourceNode, int msg, Object args[])
    throws SAXException
  {
    Exception e = null;
    String fmsg = m_XSLMessages.createMessage(msg, args);
    SourceLocator locator = null;
    try{
     locator = ((StylesheetHandler)(((TransformerFactoryImpl)this.m_tfactory).newTemplatesHandler())).getLocator();
    }
    catch (TransformerConfigurationException tce)
    {
    }
    DefaultErrorHandler handler;
    if (m_problemListener == null)
      handler = (DefaultErrorHandler)m_tfactory.getErrorListener();
    else
      handler = (DefaultErrorHandler)m_problemListener.getErrorHandler();
    TransformerException te = (null == e) ? new TransformerException(fmsg, locator) :
                                          new TransformerException(fmsg, locator, e);
    if(null != handler)
    {  
      try{
       handler.fatalError(te);
      }
      catch (TransformerException te2)
      {
        throw new SAXException(te2);
      }
    }
    else
      throw new SAXException(te);
    
   
  }
  
  /**
   * Mark the time, so that displayDuration can later
   * display the elapse.
   */
  void pushTime(Object key)
  {
    if(null != key)
    {
      m_durationsTable.put(key, new Long(System.currentTimeMillis()));
    }
  }

  /**
   * Returns the duration since pushTime was called,
   * in milliseconds.
   */
  long popDuration(Object key)
  {
    long millisecondsDuration = 0;
    if(null != key)
    {
      long start = ((Long)m_durationsTable.get(key)).longValue();
      long stop = System.currentTimeMillis();
      millisecondsDuration = stop - start;
      m_durationsTable.remove(key);
    }
    return millisecondsDuration;
  }
  
   /**
   * Display the duration since pushTime was called.
   */
  protected void displayDuration(String info, Object key)
  {
    long millisecondsDuration = 0;
    if(null != key)
    {
      long start = ((Long)m_durationsTable.get(key)).longValue();
      long stop = System.currentTimeMillis();
      millisecondsDuration = stop - start;
      if(null != m_diagnosticsPrintWriter)
      {
        m_diagnosticsPrintWriter.println(info + " took " + millisecondsDuration + " milliseconds");
      }
      m_durationsTable.remove(key);
    }
  }
  
  /**
   * If this is set, diagnostics will be
   * written to the m_diagnosticsPrintWriter stream. If
   * the value is null, then diagnostics will be turned
   * off.
   */
  public void setDiagnosticsOutput(java.io.OutputStream out)
  {
    setDiagnosticsOutput(new PrintWriter(out));
  }

  /**
   * If this is set, diagnostics will be
   * written to the m_diagnosticsPrintWriter stream. If
   * the value is null, then diagnostics will be turned
   * off.
   */
  public void setDiagnosticsOutput(java.io.PrintWriter pw)
  {
    m_diagnosticsPrintWriter = pw;
   /* if(getProblemListener() instanceof ProblemListenerDefault)
    {
      ((ProblemListenerDefault)getProblemListener()).setDiagnosticsOutput(pw);
    }*/
  }

  /**
   * Bottleneck output of diagnostics.
   */
  protected void diag(String s)
  {
    if(null != m_diagnosticsPrintWriter)
    {
      m_diagnosticsPrintWriter.println(s);
    }
  }

  /**
   * If this is set to true, simple traces of
   * template calls are made.
   */
  public void setTraceTemplates(boolean b)
  {
    m_traceTemplates = b;
  }

  /**
   * If this is set to true, simple traces of
   * template calls are made.
   */
  public void setTraceSelect(boolean b)
  {
    m_traceSelects = b;
  }

  /**
   * If this is set to true, debug diagnostics about
   * template children as they are being constructed
   * will be written to the m_diagnosticsPrintWriter
   * stream.  diagnoseTemplateChildren is false by
   * default.
   */
  public void setTraceTemplateChildren(boolean b)
  {
    m_traceTemplateChildren = b;
  }
  
  //
  // Lexical handler interface
  //

    /**
     * Report the start of DTD declarations, if any.
     *
     * <p>Any declarations are assumed to be in the internal subset
     * unless otherwise indicated by a {@link #startEntity startEntity}
     * event.</p>
     *
     * <p>Note that the start/endDTD events will appear within
     * the start/endDocument events from ContentHandler and
     * before the first startElement event.</p>
     *
     * @param name The document type name.
     * @param publicId The declared public identifier for the
     *        external DTD subset, or null if none was declared.
     * @param systemId The declared system identifier for the
     *        external DTD subset, or null if none was declared.
     * @exception SAXException The application may raise an
     *            exception.
     * @see #endDTD
     * @see #startEntity
     */
    public void startDTD (String name, String publicId, String systemId)
	throws SAXException
    {
      if (m_transformerImpl != null && m_transformerImpl.getInputLexicalHandler() != null)
        m_transformerImpl.getInputLexicalHandler().startDTD(name, publicId, systemId);
    }


    /**
     * Report the end of DTD declarations.
     *
     * @exception SAXException The application may raise an exception.
     * @see #startDTD
     */
    public void endDTD ()
	throws SAXException
    {
      if (m_transformerImpl != null && m_transformerImpl.getInputLexicalHandler() != null)
        m_transformerImpl.getInputLexicalHandler().endDTD();
    }
    
    public void startEntity (String name)
	throws SAXException
    {
      if (m_transformerImpl != null && m_transformerImpl.getInputLexicalHandler() != null)
        m_transformerImpl.getInputLexicalHandler().startEntity (name);
    }


    /**
     * Report the end of an entity.
     *
     * @param name The name of the entity that is ending.
     * @exception SAXException The application may raise an exception.
     * @see #startEntity
     */
    public void endEntity (String name)
	throws SAXException
      {
      if (m_transformerImpl != null && m_transformerImpl.getInputLexicalHandler() != null)
        m_transformerImpl.getInputLexicalHandler().endEntity (name);
    }


    /**
     * Report the start of a CDATA section.
     *
     * <p>The contents of the CDATA section will be reported through
     * the regular {@link org.xml.sax.ContentHandler#characters
     * characters} event.</p>
     *
     * @exception SAXException The application may raise an exception.
     * @see #endCDATA
     */
    public  void startCDATA ()
	throws SAXException
    { 
      if (m_transformerImpl != null && m_transformerImpl.getInputLexicalHandler() != null)
        m_transformerImpl.getInputLexicalHandler().startCDATA();
    }


    /**
     * Report the end of a CDATA section.
     *
     * @exception SAXException The application may raise an exception.
     * @see #startCDATA
     */
    public void endCDATA ()
	throws SAXException
    {
      if (m_transformerImpl != null && m_transformerImpl.getInputLexicalHandler() != null)
        m_transformerImpl.getInputLexicalHandler().endCDATA();
    }


    /**
     * Report an XML comment anywhere in the document.
     *
     * <p>This callback will be used for comments inside or outside the
     * document element, including comments in the external DTD
     * subset (if read).</p>
     *
     * @param ch An array holding the characters in the comment.
     * @param start The starting position in the array.
     * @param length The number of characters to use from the array.
     * @exception SAXException The application may raise an exception.
     */
    public  void comment (char ch[], int start, int length)
	throws SAXException
      {
      if (m_transformerImpl != null && m_transformerImpl.getInputLexicalHandler() != null)
        m_transformerImpl.getInputLexicalHandler().comment (ch, start, length);
    }
    
    // DocumentHandler interface
    
        /**
     * Receive an object for locating the origin of SAX document events.
     *
     * <p>SAX parsers are strongly encouraged (though not absolutely
     * required) to supply a locator: if it does so, it must supply
     * the locator to the application by invoking this method before
     * invoking any of the other methods in the DocumentHandler
     * interface.</p>
     *
     * <p>The locator allows the application to determine the end
     * position of any document-related event, even if the parser is
     * not reporting an error.  Typically, the application will
     * use this information for reporting its own errors (such as
     * character content that does not match an application's
     * business rules).  The information returned by the locator
     * is probably not sufficient for use with a search engine.</p>
     *
     * <p>Note that the locator will return correct information only
     * during the invocation of the events in this interface.  The
     * application should not attempt to use it at any other time.</p>
     *
     * @param locator An object that can return the location of
     *                any SAX document event.
     * @see org.xml.sax.Locator
     */
    public void setDocumentLocator (Locator locator)
    {}
    
    
    /**
     * Receive notification of the beginning of a document.
     *
     * <p>The SAX parser will invoke this method only once, before any
     * other methods in this interface or in DTDHandler (except for
     * setDocumentLocator).</p>
     *
     * @exception org.xml.sax.SAXException Any SAX exception, possibly
     *            wrapping another exception.
     */
    public void startDocument ()
	throws SAXException
    {
      if (m_documentHandler != null)
        m_documentHandler.startDocument();
      else if (m_transformerImpl != null)
        m_transformerImpl.getInputContentHandler().startDocument();
      //m_transformerImpl.getResultTreeHandler().startDocument();
      
    }
    
    
    /**
     * Receive notification of the end of a document.
     *
     * <p>The SAX parser will invoke this method only once, and it will
     * be the last method invoked during the parse.  The parser shall
     * not invoke this method until it has either abandoned parsing
     * (because of an unrecoverable error) or reached the end of
     * input.</p>
     *
     * @exception org.xml.sax.SAXException Any SAX exception, possibly
     *            wrapping another exception.
     */
    public void endDocument ()
	throws SAXException
    {
      if (m_documentHandler != null)
        m_documentHandler.endDocument();
      else if (m_transformerImpl != null)
        m_transformerImpl.getInputContentHandler().endDocument();
      //m_transformerImpl.getResultTreeHandler().endDocument();
    }
    
    
    /**
     * Receive notification of the beginning of an element.
     *
     * <p>The Parser will invoke this method at the beginning of every
     * element in the XML document; there will be a corresponding
     * endElement() event for every startElement() event (even when the
     * element is empty). All of the element's content will be
     * reported, in order, before the corresponding endElement()
     * event.</p>
     *
     * <p>If the element name has a namespace prefix, the prefix will
     * still be attached.  Note that the attribute list provided will
     * contain only attributes with explicit values (specified or
     * defaulted): #IMPLIED attributes will be omitted.</p>
     *
     * @param name The element type name.
     * @param atts The attributes attached to the element, if any.
     * @exception org.xml.sax.SAXException Any SAX exception, possibly
     *            wrapping another exception.
     * @see #endElement
     * @see org.xml.sax.AttributeList 
     */
    public void startElement (String name, AttributeList atts)
	throws SAXException
    {
      if (m_documentHandler == null)       
      { 
        m_documentHandler = new ParserAdapter(new org.apache.xerces.parsers.SAXParser());
        if (m_transformerImpl != null)
        {  
          ((ParserAdapter)m_documentHandler).setContentHandler(m_transformerImpl.getInputContentHandler());
        }
        /* else if (m_transformerImpl != null)
        {
        int index = name.indexOf(":");
        if (index < 0 )
        m_transformerImpl.getInputContentHandler().startElement(null, name, name, (Attributes)atts);
        //getResultTreeHandler().startElement(null, name, name, (Attributes)atts);
        else
        m_transformerImpl.getInputContentHandler().startElement(name.substring(0,index), name.substring(index+1), name, (Attributes)atts);        
        //m_transformerImpl.getResultTreeHandler().startElement(name.substring(0,index), name.substring(index+1), name, (Attributes)atts);
        */ 
      } 
      m_documentHandler.startElement(name, atts);
    }   
    
    /**
     * Receive notification of the end of an element.
     *
     * <p>The SAX parser will invoke this method at the end of every
     * element in the XML document; there will be a corresponding
     * startElement() event for every endElement() event (even when the
     * element is empty).</p>
     *
     * <p>If the element name has a namespace prefix, the prefix will
     * still be attached to the name.</p>
     *
     * @param name The element type name
     * @exception org.xml.sax.SAXException Any SAX exception, possibly
     *            wrapping another exception.
     */
    public void endElement (String name)
	throws SAXException
    {
      if (m_documentHandler == null)        
      {         
        m_documentHandler = new ParserAdapter(new org.apache.xerces.parsers.SAXParser());
        if (m_transformerImpl != null)
        {
          ((ParserAdapter)m_documentHandler).setContentHandler(m_transformerImpl.getInputContentHandler());
        }
     /* else if (m_transformerImpl != null)
      {
        int index = name.indexOf(":");
        if (index < 0 )
          m_transformerImpl.getInputContentHandler().endElement(null, name, name);
          //m_transformerImpl.getResultTreeHandler().endElement(null, name, name);        
        else
          m_transformerImpl.getInputContentHandler().endElement(name.substring(0,index), name.substring(index+1), name);
          //m_transformerImpl.getResultTreeHandler().endElement(name.substring(0,index), name.substring(index+1), name);
        */
      } 
      m_documentHandler.endElement(name);      
    }     
    
    
    /**
     * Receive notification of character data.
     *
     * <p>The Parser will call this method to report each chunk of
     * character data.  SAX parsers may return all contiguous character
     * data in a single chunk, or they may split it into several
     * chunks; however, all of the characters in any single event
     * must come from the same external entity, so that the Locator
     * provides useful information.</p>
     *
     * <p>The application must not attempt to read from the array
     * outside of the specified range.</p>
     *
     * <p>Note that some parsers will report whitespace using the
     * ignorableWhitespace() method rather than this one (validating
     * parsers must do so).</p>
     *
     * @param ch The characters from the XML document.
     * @param start The start position in the array.
     * @param length The number of characters to read from the array.
     * @exception org.xml.sax.SAXException Any SAX exception, possibly
     *            wrapping another exception.
     * @see #ignorableWhitespace 
     * @see org.xml.sax.Locator
     */
    public void characters (char ch[], int start, int length)
	throws SAXException
    {
      if (m_documentHandler != null)
        m_documentHandler.characters(ch, start, length);
      else if (m_transformerImpl != null)
      m_transformerImpl.getInputContentHandler().characters(ch, start, length);
      //m_transformerImpl.getResultTreeHandler().characters(ch, start, length);
    }  
    
    
    /**
     * Receive notification of ignorable whitespace in element content.
     *
     * <p>Validating Parsers must use this method to report each chunk
     * of ignorable whitespace (see the W3C XML 1.0 recommendation,
     * section 2.10): non-validating parsers may also use this method
     * if they are capable of parsing and using content models.</p>
     *
     * <p>SAX parsers may return all contiguous whitespace in a single
     * chunk, or they may split it into several chunks; however, all of
     * the characters in any single event must come from the same
     * external entity, so that the Locator provides useful
     * information.</p>
     *
     * <p>The application must not attempt to read from the array
     * outside of the specified range.</p>
     *
     * @param ch The characters from the XML document.
     * @param start The start position in the array.
     * @param length The number of characters to read from the array.
     * @exception org.xml.sax.SAXException Any SAX exception, possibly
     *            wrapping another exception.
     * @see #characters
     */
    public void ignorableWhitespace (char ch[], int start, int length)
	throws SAXException
    {
      if (m_documentHandler != null)
        m_documentHandler.ignorableWhitespace(ch, start, length);
      else if (m_transformerImpl != null)
      m_transformerImpl.getInputContentHandler().ignorableWhitespace(ch, start, length);
      //m_transformerImpl.getResultTreeHandler().ignorableWhitespace(ch, start, length);
    }
    
    
    /**
     * Receive notification of a processing instruction.
     *
     * <p>The Parser will invoke this method once for each processing
     * instruction found: note that processing instructions may occur
     * before or after the main document element.</p>
     *
     * <p>A SAX parser should never report an XML declaration (XML 1.0,
     * section 2.8) or a text declaration (XML 1.0, section 4.3.1)
     * using this method.</p>
     *
     * @param target The processing instruction target.
     * @param data The processing instruction data, or null if
     *        none was supplied.
     * @exception org.xml.sax.SAXException Any SAX exception, possibly
     *            wrapping another exception.
     */
    public  void processingInstruction (String target, String data)
	throws SAXException
    {
      if (m_documentHandler != null)
        m_documentHandler.processingInstruction(target, data);
      else if (m_transformerImpl != null)
      m_transformerImpl.getInputContentHandler().processingInstruction(target, data);
      //m_transformerImpl.getResultTreeHandler().processingInstruction(target, data);
    }
    
    // Implement XSLTProcessor
    
    /**
   * Set the output stream. Required when the XSLTProcessor is being used
   * as a SAX DocumentHandler.
   */
  public void setOutputStream(java.io.OutputStream os)
  {
    TransformerHandlerImpl handler = new TransformerHandlerImpl(m_transformerImpl, false, null);
    handler.setResult(new StreamResult(os));   
  }
  
  /**
   * Convenience function to create an XString.
   * @param s A valid string.
   * @return An XString object.
   */
  public XString createXString(String s)
  {
    return new XString(s);
  }

  /**
   * Convenience function to create an XObject.
   * @param o Any java object.
   * @return An XObject object.
   */
  public XObject createXObject(Object o)
  {
    return new XObject(o);
  }

  /**
   * Convenience function to create an XNumber.
   * @param d Any double number.
   * @return An XNumber object.
   */
  public XNumber createXNumber(double d)
  {
    return new XNumber(d);
  }

  /**
   * Convenience function to create an XBoolean.
   * @param b boolean value.
   * @return An XBoolean object.
   */
  public XBoolean createXBoolean(boolean b)
  {
    return new XBoolean(b);
  }

  /**
   * Convenience function to create an XNodeSet.
   * @param nl A NodeList object.
   * @return An XNodeSet object.
   */
  public XNodeSet createXNodeSet(NodeList nl)
  {
    return new XNodeSet(nl);
  }

  /**
   * Convenience function to create an XNodeSet from a node.
   * @param n A DOM node.
   * @return An XNodeSet object.
   */
  public XNodeSet createXNodeSet(Node n)
  {
    return new XNodeSet(n);
  }

  /**
   * Convenience function to create an XNull.
   * @return An XNull object.
   */
  public XNull createXNull()
  {
    return new XNull();
  }
  
  /**
   * Get the XMLParserLiaison that this processor uses.
   */
  public XMLParserLiaison getXMLProcessorLiaison()
  {
    return (XMLParserLiaison)m_liaison;
  }

  /**
   * Get the preferred stylesheet for the XSLTInputSource XML document,
   * as identified by the xml-stylesheet PI, and matching the media and
   * charset criteria. See <a href="http://www.w3.org/TR/xml-stylesheet/">
   * Associating Style Sheets with XML documents</a>.
   * Does not yet handle the LINK REL="stylesheet" syntax.
   *
   * @param media The media attribute to be matched.  May be null, in which
   *              case the prefered stylesheet will be used (i.e., alternate = no).
   * @param title The value of the title attribute to match.  May be null.
   * @param charset The value of the charset attribute to match.  May be null.
   * @returns StylesheetSpec extends XSLTInputSource extedns SAX InputSource; the return value
   * can be passed to the processStylesheet method.
   */
  public StylesheetSpec getAssociatedStylesheet(XSLTInputSource source,
                                                      String media,
                                                      String charset)
    throws SAXException
  {
    /*InputSource[]in = m_processor.getAssociatedStylesheets(source, media, null, charset);
    if (in.length >0)
      return (StylesheetSpec)in[0];
    else 
      return null; */
    try{
    Source s = m_tfactory.getAssociatedStylesheet(source.getSourceObject(), media, null, charset);
    return (StylesheetSpec)s; 
    }
    catch (TransformerConfigurationException tce) 
    {
      throw new SAXException(tce); 
    }
  }

  /**
   * Get a list of stylesheet specifications for the XSLTInputSource XML document,
   * as identified by the xml-stylesheet PI, and matching the media and
   * charset criteria. See <a href="http://www.w3.org/TR/xml-stylesheet/">
   * Associating Style Sheets with XML documents</a>.
   * Does not yet handle the LINK REL="stylesheet" syntax.
   *
   * @param media The media attribute to be matched.  May be null, in which
   *              case the prefered stylesheet will be used (i.e., alternate = no).
   * @param title The value of the title attribute to match.  May be null.
   * @param charset The value of the charset attribute to match.  May be null.
   * @returns list of StylesheetSpecs (extend XSLTInputSources extend SAX InputSources; a
   * list member may be passsed to the processStylesheet method.
   */
  public Vector getAssociatedStylesheets(XSLTInputSource source,
                                                      String media,
                                                      String charset)
    throws SAXException
  {
    try{
      Source s = m_tfactory.getAssociatedStylesheet(source.getSourceObject(), media, null, charset);
      Vector v = new Vector();
      //for (int i = 0; i< in.length; i++)
      v.addElement((StylesheetSpec)s);
      return v;
    }
    catch (TransformerConfigurationException tce)
    {
      throw new SAXException(tce);
    }                                              
  }
  
  /**
   * Submit a top-level stylesheet parameter.  This value can
   * be evaluated in the stylesheet via xsl:param-variable.
   * @param key The name of the param.
   * @param value An XObject that will be used.
   */
  public void setStylesheetParam(String key, XObject value)
  {
    setParameter(key, value.object());
  }

  /**
   * Set a top-level stylesheet parameter.  This value can
   * be evaluated via xsl:param-variable.  Note that the value
   * passed is an expression, and not a string.  This means that
   * setStylesheetParam("foo", "hello"); will look for the
   * element "hello".  If you want to pass a string, you'll
   * need to put quotes around it:
   * setStylesheetParam("foo", "'hello'"); will look for the
   * @param key The name of the param.
   * @param expression An expression that will be evaluated.
   */
  public void setStylesheetParam(String key, String expression)
  {    
    if (m_evalList == null)
      m_evalList = new Vector();
    if (!m_evalList.contains(key))
      m_evalList.addElement(key);
    setParameter(key, expression);
    m_needToEval = true;
  }
  
  /**
   * Set a top-level stylesheet parameter.  This value can
   * be evaluated via xsl:param-variable.  Note that the value
   * passed is an expression, and not a string.  This means that
   * setStylesheetParam("foo", "hello"); will look for the
   * element "hello".  If you want to pass a string, you'll
   * need to put quotes around it:
   * setStylesheetParam("foo", "'hello'"); will look for the
   * @param key The name of the param.
   * @param expression An expression that will be evaluated.
   */
  public void setParameter(String key,  Object value)
  {
    if (m_stylesheetParams == null)
      m_stylesheetParams = new Hashtable();
    m_stylesheetParams.put(key, value); 
  }

  /**
   * Get the current FormatterListener (SAX DocumentHandler), or null if none has been set.
   */
  public DocumentHandler getFormatterListener()
  {
    return m_documentHandler;
    
  }

  /**
   * Set the FormatterListener (the SAX DocumentHandler).
   */
  public void setFormatterListener(DocumentHandler flistener)
  {
    m_documentHandler = flistener;
  }

  /**
   * Get the current SAX DocumentHandler (the same object as the FormatterListener), or null if none has been set.
   */
  public DocumentHandler getDocumentHandler()
  {
    return m_documentHandler;
  }

  /**
   * Set the current SAX DocumentHandler (the same
   * object as the FormatterListener).
   */
  public void setDocumentHandler(DocumentHandler listener)
  {
    if (listener instanceof ParserAdapter)      
      m_transformerImpl.setContentHandler(((ParserAdapter)listener).getContentHandler());
    else if (listener instanceof XSLTEngineImpl)
      m_transformerImpl.setContentHandler(((XSLTEngineImpl)listener).getTransformer().getContentHandler());
    else if (listener instanceof XMLSerializer)
    {
      try{
        m_transformerImpl.setContentHandler(((XMLSerializer)listener).asContentHandler());
      }
      catch (IOException ioe)
      {}
    }
    m_documentHandler = listener;
  }

  /**
   * Add a trace listener for the purposes of debugging and diagnosis.
   * @param tl Trace listener to be added.
   */
  public void addTraceListener(TraceListener tl)
    throws TooManyListenersException
  {
    if (m_transformerImpl != null)
      m_transformerImpl.getTraceManager().addTraceListener(tl);
  }
  
  /**
   * If set to true (the default), pattern conflict warnings are not printed to the diagnostics stream.
   * @param b true if conflict warnings should be suppressed.
   */
  public void setQuietConflictWarnings(boolean b)
  {
    if (m_transformerImpl != null)
      m_transformerImpl.setQuietConflictWarnings(b);
  }

  /**
   * Remove a trace listener.
   * @param tl Trace listener to be removed.
   */
  public void removeTraceListener(TraceListener tl)
  {
    if (m_transformerImpl != null)
      m_transformerImpl.getTraceManager().removeTraceListener(tl);
  }
  
  /**
   * Set the problem listener property.
   * The XSL class can have a single listener to be informed
   * of errors and warnings. The problem listener normally controls whether an exception
   * is thrown or not (or the problem listeners can throw its own RuntimeException).
   * @param l A ProblemListener interface.
   */
  public void setProblemListener(ProblemListener l)
  {
    if (l instanceof ProblemListenerDefault)
      m_problemListener = (ProblemListenerDefault)l;
    else
      m_problemListener = new ProblemListenerDefault(l); 
    if (m_transformerImpl != null)
      m_transformerImpl.setErrorListener(m_problemListener);
    m_tfactory.setErrorListener(m_problemListener);
  }

  /**
   * Get the problem listener property.
   * The XSL class can have a single listener to be informed
   * of errors and warnings. The problem listener normally controls whether an exception
   * is thrown or not (or the problem listener can throw its own RuntimeException).
   * @return A ProblemListener interface.
   */
  public ProblemListener getProblemListener()
  {
    if (m_problemListener.getProblemListener() != null)
      return m_problemListener.getProblemListener();
    else
      return m_problemListener;
  }
  
  public TransformerImpl getTransformer()
  {
    return m_transformerImpl;  
    //return (StylesheetProcessor)m_processor;
  }  

  public TransformerFactoryImpl getTransformerFactory()
  {
    return (TransformerFactoryImpl)m_tfactory;    
  } 

  
} // end XSLTEngineImpl class
