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
package org.apache.xalan.client;

import java.applet.Applet;

import java.awt.Graphics;

import java.net.URL;
import java.net.MalformedURLException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.StringReader;
import java.io.IOException;
import java.io.InputStream;

// Needed Xalan classes
import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.stree.SourceTreeHandler;
import org.apache.xalan.transformer.TransformerImpl;

// Needed TRaX classes
import trax.Result;
import trax.Processor;
import trax.ProcessorFactoryException;
import trax.Transformer;
import trax.TransformException;
import trax.Templates;
import trax.TemplatesBuilder;

// Needed SAX classes
import org.xml.sax.InputSource;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.ParserAdapter;
import org.xml.sax.helpers.XMLReaderFactory;

// Needed DOM classes
import org.w3c.dom.Node;
import org.w3c.dom.Document;

// Needed Serializer classes
import serialize.OutputFormat;
import serialize.Serializer;
import serialize.SerializerFactory;

/**
 * <meta name="usage" content="general"/>
 * Provides applet host for the XSLT processor. To perform transformations on an HTML client:
 * <ol>
 * <li>Use an &lt;applet&gt; tag to embed this applet in the HTML client.</li>
 * <li>Use the DocumentURL and StyleURL PARAM tags or the {@link #setDocumentURL} and
 * {@link #setStyleURL} methods to specify the XML source document and XSL stylesheet.</li>
 * <li>Call the {@link #transformToHTML} method to perform the transformation and return
 * the result as a String.</li>
 * </ol>
 */
public class XSLTProcessorApplet extends Applet
{

  /**
   * The stylesheet processor
   */
  Processor m_processor = null;
  String m_processorName="org.apache.xalan.processor.StylesheetProcessor";
  
  XMLReader m_reader = null;
  
  TemplatesBuilder m_templatesBuilder = null;

  /**
   * @serial
   */
  private String m_styleURL;

  /**
   * @serial
   */
  private String m_documentURL;

  // Parameter names.  To change a name of a parameter, you need only make
  // a single change.  Simply modify the value of the parameter string below.
  //--------------------------------------------------------------------------
  /**
   * @serial
   */
  private final String PARAM_styleURL = "styleURL";

  /**
   * @serial
   */
  private final String PARAM_documentURL = "documentURL";


  /**
   * @serial
   */
  private final String PARAM_parser = "parser";

  /**
   * @serial
   */
  private String whichParser = null;

  // We'll keep the DOM trees around, so tell which trees
  // are cached.
  /**
   * @serial
   */
  private String m_styleURLOfCached = null;

  /**
   * @serial
   */
  private String m_documentURLOfCached = null;

  /**
   * Save this for use on the worker thread; may not be necessary.
   * @serial
   */
  private URL m_codeBase = null;
  private URL m_documentBase = null;

  private Templates m_styleTree = null;

  /**
   * Thread stuff for the trusted worker thread.
   */
  transient private Thread m_callThread = null;

  /**
   */
  transient private TrustedAgent m_trustedAgent = null;

  /**
   */
  transient private Thread m_trustedWorker = null;

  /**
   * Where the worker thread puts the HTML text.
   */
  transient private String m_htmlText = null;

  /**
   * Stylesheet attribute name and value that the caller can set.
   */
  transient private String m_nameOfIDAttrOfElemToModify = null;

  /**
   */
  transient private String m_elemIdToModify = null;

  /**
   */
  transient private String m_attrNameToSet = null;

  /**
   */
  transient private String m_attrValueToSet = null;

  /**
   * The XSLTProcessorApplet constructor takes no arguments.
   */
  public XSLTProcessorApplet()
  {
  }

  /**
   * Get basic information about the applet
   * @return A String with the applet name and author.
   */
  public String getAppletInfo()
  {
    return "Name: XSLTProcessorApplet\r\n" +
      "Author: Scott Boag";
  }

  /**
   * Get descriptions of the applet parameters.
   * @return A two-dimensional array of Strings with Name, Type, and Description
   * for each parameter.
   */
  public String[][] getParameterInfo()
  {
    String[][] info =
    {
      { PARAM_styleURL, "String", "URL to a XSL style sheet" },
      { PARAM_documentURL, "String", "URL to a XML document" },
      { PARAM_parser, "String", "Which parser to use: Xerces or ANY" },
    };
    return info;
  }

  /**
   * Standard applet initialization.
   */
  public void init()
  {
    // PARAMETER SUPPORT
    //		The following code retrieves the value of each parameter
    // specified with the <PARAM> tag and stores it in a member
    // variable.
    //----------------------------------------------------------------------
    String param;
    param = getParameter(PARAM_parser);
    whichParser = (param != null) ? param : "ANY";

    // styleURL: Parameter description
    //----------------------------------------------------------------------
    param = getParameter(PARAM_styleURL);
    if (param != null)
      setStyleURL(param);
    // documentURL: Parameter description
    //----------------------------------------------------------------------
    param = getParameter(PARAM_documentURL);
    if (param != null)
      setDocumentURL(param);
    m_codeBase = this.getCodeBase();
    m_documentBase = this.getDocumentBase();

    // If you use a ResourceWizard-generated "control creator" class to
    // arrange controls in your applet, you may want to call its
    // CreateControls() method from within this method. Remove the following
    // call to resize() before adding the call to CreateControls();
    // CreateControls() does its own resizing.
    //----------------------------------------------------------------------
    resize(320, 240);

  }

  /**
   * Try to init the XML liaison object: currently not implemented.
   */
   
  protected void initLiaison()
  {
  }
  
 /**
   * Obtain a new instance of a Stysheet Processor object
   * as specified by m_processorName.
   * Workaround for Processor.newInstance() which an
   * applet cannot use because it reads a system property.
   * @return Concrete instance of an Processor object.
   */
   Processor newProcessorInstance()
	  throws ProcessorFactoryException
   {
	 Processor m_processor = null;
	 try
	 {  
	  Class factoryClass = Class.forName(m_processorName);
      m_processor = (Processor)factoryClass.newInstance();
     }
     catch(java.lang.IllegalAccessException iae)
     {
      throw new ProcessorFactoryException("Transformation Processor can not be accessed!", iae);
     }
     catch(java.lang.InstantiationException ie)
     {
      throw new ProcessorFactoryException("Not able to create Transformation Processor!", ie);
     }
     catch(java.lang.ClassNotFoundException cnfe)
     {
      throw new ProcessorFactoryException("Transformation Processor not found!", cnfe);
     }
	 return m_processor;
   }
   
    /**
   * Process the source tree to SAX parse events.
   * @param transformer Concrete Transformer
   * @param xmlSource  The input for the source tree.
   * Workaround for TransformerImpl.transform() which an
   * applet cannot use because it reads a system property.
   */
   
   void transform(TransformerImpl transformer, InputSource xmlSource)
    throws SAXException, TransformException, IOException
  {
      try
      {
        m_reader.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
      }
      catch(SAXException se)
      {
        // What can we do?
        // TODO: User diagnostics.
      }
      try
	  {
        // Get the input content handler, which will handle the 
        // parse events and create the source tree.
        ContentHandler inputHandler = new SourceTreeHandler(transformer);
        m_reader.setContentHandler(inputHandler);
        m_reader.setProperty("http://xml.org/sax/properties/lexical-handler", inputHandler);
        
        // Set the reader for cloning purposes.
        transformer.getXPathContext().setPrimaryReader(m_reader);
          
        // Kick off the parse.  When the ContentHandler gets 
        // the startDocument event, it will call transformNode( node ).
        m_reader.parse( xmlSource );
      }
      catch(SAXException se)
      {
        se.printStackTrace();
        throw new TransformException(se);
      }
      catch(IOException ioe)
      {
      throw new TransformException(ioe);
      }
    }	   

  /**
   * Cleanup; called when applet is terminated and unloaded.
   */
  public void destroy()
  {
    if(null != m_trustedWorker)
    {
      m_trustedWorker.stop();
      // m_trustedWorker.destroy();
      m_trustedWorker = null;
    }
    m_styleURLOfCached = null;
    m_documentURLOfCached = null;
  }

  /**
   * Do not call; this applet contains no UI or visual components.
   */

  public void paint(Graphics g)
  {
  }
 
  /**
   *  Automatically called when the HTML client containing the applet loads.
   *  This method starts execution of the applet thread.
   */
  public void start()
  {
    m_trustedAgent = new TrustedAgent();
    Thread currentThread = Thread.currentThread();
    m_trustedWorker = new Thread(currentThread.getThreadGroup(), m_trustedAgent);
    m_trustedWorker.start();
    try
    {
      this.showStatus("Causing Xalan and Xerces to Load and JIT...");
      // Prime the pump so that subsequent transforms don't look so slow.
      StringReader xmlbuf = new StringReader("<?xml version='1.0'?><foo/>");
      StringReader xslbuf = new StringReader("<?xml version='1.0'?><xsl:stylesheet xmlns:xsl='http://www.w3.org/1999/XSL/Transform' version='1.0'><xsl:template match='foo'><out/></xsl:template></xsl:stylesheet>");
      PrintWriter pw = new PrintWriter(new StringWriter());
	  
      m_processor = newProcessorInstance();
	  m_reader = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
	  m_templatesBuilder = m_processor.getTemplatesBuilder();
	  m_reader.setContentHandler(m_templatesBuilder);

      synchronized(m_processor)
      {
        m_reader.parse(new InputSource(xslbuf));
	    Templates templates = m_templatesBuilder.getTemplates();
	    TransformerImpl transformer = (TransformerImpl)templates.newTransformer();
		// Result result = new Result(pw);
        serialize.Serializer serializer = new org.apache.xml.serialize.transition.HTMLSerializer();
        // serialize.Serializer serializer = serialize.SerializerFactory.getSerializer( "HTML" );
      serializer.setWriter(pw);
      org.xml.sax.ContentHandler handler = serializer.asContentHandler();
       // new org.apache.xml.serialize.HTMLSerializer(pw, new OutputFormat()).asContentHandler();
        transformer.setContentHandler(handler);
	    transformer.setParent(m_reader);
        transform(transformer, new InputSource(xmlbuf));
        this.showStatus("PRIMED the pump!");
      }
      System.out.println("Primed the pump!");
      this.showStatus("Ready to click!"); 
    }
    catch(Exception e)
    {
      this.showStatus("Could not prime the pump!");
      System.out.println("Could not prime the pump!");
      e.printStackTrace();
    }
  }

  /**
   * Automatically called when the HTML page containing the applet is no longer
   * on the screen. Stops execution of the applet thread.
   */
  public void stop()
  {
    if(null != m_trustedWorker)
    {
      m_trustedWorker.stop();
      // m_trustedWorker.destroy();
      m_trustedWorker = null;
    }
    m_styleURLOfCached = null;
    m_documentURLOfCached = null;
  }

  /**
   * Set the URL to the XSL stylesheet that will be used
   * to transform the input XML.  No processing is done yet.
   * @param valid URL string.
   */
  public void setStyleURL(String urlString)
  {
    m_styleURL =urlString;
  }

  /**
   * Set the URL to the XML document that will be transformed
   * with the XSL stylesheet.  No processing is done yet.
   * @param valid URL string.
   */
  public void setDocumentURL(String urlString)
  {
    m_documentURL = urlString;
  }

  /**
   * The processor keeps a cache of the source and
   * style trees, so call this method if they have changed
   * or you want to do garbage collection.
   */
  public void freeCache()
  {
    m_styleURLOfCached = null;
    m_documentURLOfCached = null;
  }

  /**
   * Set an attribute in the stylesheet, which gives the ability
   * to have some dynamic selection control.
   * @param nameOfIDAttrOfElemToModify The name of an attribute to search for a unique id.
   * @param elemId The unique ID to look for.
   * @param attrName Once the element is found, the name of the attribute to set.
   * @param value The value to set the attribute to.
   */
  public void setStyleSheetAttribute(String nameOfIDAttrOfElemToModify,
                                     String elemId,
                                     String attrName,
                                     String value)
  {
    m_nameOfIDAttrOfElemToModify = nameOfIDAttrOfElemToModify;
    m_elemIdToModify = elemId;
    m_attrNameToSet = attrName;
    m_attrValueToSet = value;
  }

  transient String m_key;
  transient String m_expression;

  /**
   * Submit a stylesheet parameter.
   * @param expr The parameter expression to be submitted.
   * @see org.apache.xalan.xslt.Processor#setStylesheetParam(String, String)
   */
  public void setStylesheetParam(String key, String expr)
  {
    m_key = key;
    m_expression = expr;
  }

  /**
   * Given a String containing markup, escape the markup so it
   * can be displayed in the browser.
   */
  public String escapeString(String s)
  {
    StringBuffer sb = new StringBuffer();
    int length = s.length();

    for (int i = 0;  i < length;  i ++)
    {
      char ch = s.charAt(i);
      if ('<' == ch)
      {
        sb.append("&lt;");
      }
      else if ('>' == ch)
      {
        sb.append("&gt;");
      }
      else if ('&' == ch)
      {
        sb.append("&amp;");
      }
      else if (0xd800 <= ch && ch < 0xdc00)
      {
        // UTF-16 surrogate
        int next;
        if (i+1 >= length)
        {
          throw new RuntimeException(XSLMessages.createMessage(XSLTErrorResources.ER_INVALID_UTF16_SURROGATE, new Object[]{Integer.toHexString(ch)}));//"Invalid UTF-16 surrogate detected: "
            //+Integer.toHexString(ch)+ " ?");
        }
        else
        {
          next = s.charAt(++i);
          if (!(0xdc00 <= next && next < 0xe000))
            throw new RuntimeException(XSLMessages.createMessage(XSLTErrorResources.ER_INVALID_UTF16_SURROGATE, new Object[]{Integer.toHexString(ch)+" "+Integer.toHexString(next)}));//"Invalid UTF-16 surrogate detected: "
              //+Integer.toHexString(ch)+" "+Integer.toHexString(next));
          next = ((ch-0xd800)<<10)+next-0xdc00+0x00010000;
        }
        sb.append("&#x");
        sb.append(Integer.toHexString(next));
        sb.append(";");
      }
      else
      {
        sb.append(ch);
      }
    }
    return sb.toString();
  }


  /**
   * Assuming the stylesheet URL and the input XML URL have been set,
   * perform the transformation and return the result as a String.
   */
  public String getHtmlText()
  {
    m_trustedAgent.m_getData = true;
    m_callThread = Thread.currentThread();
    try
    {
      synchronized(m_callThread)
      {
        m_callThread.wait();
      }
    }
    catch(InterruptedException ie)
    {
      System.out.println(ie.getMessage());
    }
    return m_htmlText;
  }

  /**
   * Get a DOM tree as escaped text, suitable for display
   * in the browser.
   */
  public String getTreeAsText(String treeURL)
    throws IOException
  {
    String text = "";
    byte[] buffer = new byte[50000];

    try
		{
      URL docURL = new URL(m_documentBase, treeURL);
      InputStream in = docURL.openStream();

			int nun_chars;
			while ( ( nun_chars = in.read( buffer, 0, buffer.length ) ) != -1 )
			{
				text = text + new String( buffer, 0, nun_chars );
			}
			in.close();
		}
		catch ( Exception any_error )
    {any_error.printStackTrace();}
    return text;
  }

  /**
   * Get the XML source Tree as a text string suitable
   * for display in a browser.  Note that this is for display of the
   * XML itself, not for rendering of HTML by the browser.
   * @exception Exception thrown if tree can not be converted.
   */
  public String getSourceTreeAsText()
    throws Exception
  {
    return getTreeAsText(m_documentURL);
  }

  /**
   * Get the XSL style Tree as a text string suitable
   * for display in a browser.  Note that this is for display of the
   * XML itself, not for rendering of HTML by the browser.
   * @exception Exception thrown if tree can not be converted.
   */
  public String getStyleTreeAsText()
    throws Exception
  {
    return getTreeAsText(m_styleURL);
  }

  /**
   * Get the HTML result Tree as a text string suitable
   * for display in a browser.  Note that this is for display of the
   * XML itself, not for rendering of HTML by the browser.
   * @exception Exception thrown if tree can not be converted.
   */
  public String getResultTreeAsText()
    throws Exception
  {
    return escapeString(getHtmlText());
  }

  /**
   * Process a document and a stylesheet and return
   * the transformation result.  If one of these is null, the
   * existing value (of a previous transformation) is not affected.
   */
  public String transformToHtml(String doc, String style)
  {
    if(null != doc)
    {
      m_documentURL = doc;
    }
    if(null != style)
    {
      m_styleURL = style;
    }
    return getHtmlText();
  }

  /**
   * Process a document and a stylesheet and return
   * the transformation result. Use the xsl:stylesheet PI to find the
   * document, if one exists.
   */
  public String transformToHtml(String doc)
  {
    if(null != doc)
    {
      m_documentURL = doc;
    }
    m_styleURL = null;
    return getHtmlText();
  }

  /**
   * Do the real transformation after the right XML processor
   * liason has been found.
   */
  private String doTransformation(Processor processor)
    throws SAXException
  {
    URL documentURL = null;
    URL styleURL = null;
    StringWriter osw = new StringWriter();
    PrintWriter pw = new PrintWriter(osw, false);

    this.showStatus("Begin Transformation...");
    try
    {
	  m_templatesBuilder = m_processor.getTemplatesBuilder();
	  m_reader.setContentHandler(m_templatesBuilder);
		
      documentURL = new URL(m_codeBase, m_documentURL);
      InputSource xmlSource = new InputSource(documentURL.toString());

      styleURL = new URL(m_codeBase, m_styleURL);
      InputSource xslSource = new InputSource(styleURL.toString());
	  
	  m_reader.parse(xslSource);
	  Templates templates = m_templatesBuilder.getTemplates();
	  TransformerImpl transformer = (TransformerImpl)templates.newTransformer();
      
      if(null != m_key)
        transformer.setParameter(m_key, null, m_expression);
      // Result result = new Result(pw);
      serialize.Serializer serializer = new org.apache.xml.serialize.transition.HTMLSerializer(); this.showStatus("serializer is "+ serializer);
      //serialize.Serializer serializer = serialize.SerializerFactory.getSerializer( "HTML" );
      serializer.setWriter(pw);
      org.xml.sax.ContentHandler handler = serializer.asContentHandler();
	  
      transformer.setContentHandler(handler);
	  transformer.setParent(m_reader);
      transform(transformer, xmlSource );
	}
    catch(MalformedURLException e)
    {
      e.printStackTrace();
      System.exit(-1);
    }
    catch(IOException e)
    {
      e.printStackTrace();
      System.exit(-1);
    }
    this.showStatus("Transformation Done!");

    String htmlData = osw.toString();

    return htmlData;
  }

  /**
   * Process the transformation.
   */
  private String processTransformation()
    throws SAXException
  {
    String htmlData = null;
    try
    {
      if(whichParser.trim().equals("Xerces") || whichParser.trim().equals("ANY"))
      {
        this.showStatus("Waiting for Xalan and Xerces to finish loading and JITing...");
        synchronized(m_processor)
        {
          // TransformerImpl processor = new XSLProcessor(m_liaison);
          htmlData = doTransformation(m_processor);
        }
      }
      else
      {
          System.out.println("Problem with XML parser!");
      }
    }
    catch(NoClassDefFoundError e)
    {
      System.out.println("Can not find "+whichParser+" XML Processor!!");
    }
    return htmlData;
  }

  /**
   * This class maintains a worker thread that that is
   * trusted and can do things like access data.  You need
   * this because the thread that is called by the browser
   * is not trusted and can't access data from the URLs.
   */
  class TrustedAgent implements Runnable
  {
    public boolean m_getData = false;
    public void run()
    {
      while(true)
      {
        m_trustedWorker.yield();
        if(m_getData)
        {
          try
          {
            m_getData = false;
            m_htmlText = null;
            m_htmlText = processTransformation();
          }
          catch(Exception e)
          {
            e.printStackTrace();
          }
          finally
          {
            synchronized(m_callThread)
            {
              m_callThread.notify();
            }

          }
        }
        else
        {
          try
          {
            m_trustedWorker.sleep(50);
          }
          catch (InterruptedException ie)
          {
            ie.printStackTrace();
          }
        }
      }
    }
  }
}

