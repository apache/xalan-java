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

import org.w3c.dom.*;
import java.util.*;
import java.net.*;
import java.io.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.apache.xml.serialize.*;
import org.apache.xalan.serialize.*;
import org.apache.xalan.templates.ElemTemplate;
import org.apache.xalan.templates.OutputProperties;
import javax.xml.transform.OutputKeys;
import org.apache.xalan.xpath.xml.FormatterToXML;
import org.apache.xalan.xpath.xml.FormatterToHTML;
import org.apache.xalan.xpath.xml.FormatterToText;
import org.apache.xalan.xpath.xml.FormatterToDOM;
import org.apache.xalan.res.XSLTErrorResources;

/**
 * <meta name="usage" content="general"/> 
 * Binary representation of a stylesheet -- use the {@link org.apache.xalan.xslt.XSLTProcessor} ProcessStylesheet
 * method to create a StylesheetRoot and improve performance for a stylesheet performing multiple transformations.
 * Also required for XSLTProcessor to function as SAX DocumentHandler.
 * @deprecated This compatibility layer will be removed in later releases. 
 */
public class StylesheetRoot extends Stylesheet
{
  
  private String m_liaisonClassUsedToCreate = null;
  private org.apache.xalan.templates.StylesheetRoot m_sRootObject;
  
  /**
   * Uses an XSL stylesheet document.
   * @param processor  The XSLTProcessor implementation.
   * @param baseIdentifier The file name or URL for the XSL stylesheet.
   * @exception TransformerConfigurationException thrown 
   */
  public StylesheetRoot(XSLTEngineImpl processor,
                        String baseIdentifier)
    throws TransformerConfigurationException
  { 
		super(null);
    m_sRootObject = new org.apache.xalan.templates.StylesheetRoot(processor.getTransformerFactory().getErrorListener()); 
    if (processor.getXMLProcessorLiaison()!= null)
      m_liaisonClassUsedToCreate = ((Object)processor.getXMLProcessorLiaison()).getClass().getName();
    
  }  


  /**
   * Constructor using an org.apache.xalan.templates.StylesheetRoot.
   * 
   * @param s an org.apache.xalan.templates.StylesheetRoot object
   */
  public StylesheetRoot(org.apache.xalan.templates.StylesheetRoot s)    
  { 
		super(null);
    m_sRootObject = s;     
  }
  
  /**
   * Return the org.apache.xalan.templates.StylesheetRoot object
   * associated with this stylesheet root.
   *  
   * @return an org.apache.xalan.templates.StylesheetRoot object
   */
  public org.apache.xalan.templates.StylesheetRoot getObject()
  {
    return m_sRootObject;
  }  
  
  /**
   * Transform the XML source tree and place the output in the result tree target.
   * This method uses a new XSLTProcessor instance to track the running state.
   * @param xmlSource  The XML input source tree.
   * @param outputTarget The output result tree.
   * @exception SAXException
   * @see org.apache.xalan.xslt.XSLTProcessor#process(org.apache.xalan.xslt.XSLTInputSource, org.apache.xalan.xslt.XSLTInputSource, org.apache.xalan.xslt.XSLTResultTarget)
   */
  public void process( XSLTInputSource xmlSource,
                       XSLTResultTarget outputTarget)
    throws SAXException,
           MalformedURLException,
           FileNotFoundException,
           IOException
  {
    XSLTProcessor iprocessor =
                              (null != m_liaisonClassUsedToCreate) ?
                              new XSLTProcessorFactory().getProcessorUsingLiaisonName(m_liaisonClassUsedToCreate)
                              : new XSLTProcessorFactory().getProcessor();
    process(iprocessor, xmlSource, outputTarget);
    
  }

  /**
   * Transform the XML source tree and place the output in the result tree target.
   * Use this version of the StylesheetRoot process() method if you have used an
   * XSLTProcessor object to set a stylesheet parameter. That object is now the iprocessor parameter.
   * @param iprocessor  The XSLTProcessor that will track the running state.
   * @param xmlSource  The XML input source tree.
   * @param outputTarget The output result tree.
   * @exception SAXException
   * @see org.apache.xalan.xslt.XSLTProcessor#process(org.apache.xalan.xslt.XSLTInputSource, org.apache.xalan.xslt.XSLTInputSource, org.apache.xalan.xslt.XSLTResultTarget)
   */
  public void process( XSLTProcessor iprocessor, XSLTInputSource xmlSource,
                       XSLTResultTarget outputTarget)
    throws SAXException,
           MalformedURLException,
           FileNotFoundException,
           IOException
  {
    //process(iprocessor, iprocessor.getSourceTreeFromInput(xmlSource), outputTarget);
		XSLTEngineImpl processor = (XSLTEngineImpl)iprocessor; // TODO: Check for class cast exception
		
    synchronized(processor)
    {      
      processor.setStylesheet(this);

      try
      {
				processor.getTransformer().transform(xmlSource.getSourceObject(),
																						 outputTarget.getResultObject());
			}
      catch (TransformerException te)
      {
        throw new SAXException(te); 
      }             
    }
       
  }

  /**
   * Transform the XML source tree (a DOM Node) and place the output in the result tree target.
   * This is a convenience method. You can also use a DOM Node to instantiate an XSLTInputSource object,
   * and call {@link #process(org.apache.xalan.xslt.XSLTProcessor, org.apache.xalan.xslt.XSLTInputSource, org.apache.xalan.xslt.XSLTResultTarget)} or
   * {@link org.apache.xalan.xslt.XSLTProcessor#process(org.apache.xalan.xslt.XSLTInputSource, org.apache.xalan.xslt.XSLTInputSource, org.apache.xalan.xslt.XSLTResultTarget)}.
   * @param iprocessor  The processor that will track the running state.
   * @param sourceTree  The input source tree in the form of a DOM Node.
   * @param outputTarget The output result tree.
   * @exception SAXException
   */
  public void process( XSLTProcessor iprocessor, Node sourceTree,
                       XSLTResultTarget outputTarget)
    throws SAXException,
           MalformedURLException,
           FileNotFoundException,
           IOException
  {
		
    XSLTEngineImpl processor = (XSLTEngineImpl)iprocessor; // TODO: Check for class cast exception
		//checkInit(processor);
    synchronized(processor)
    {
      //processor.switchLiaisonsIfNeeded(sourceTree, outputTarget.getNode());

      processor.setStylesheet(this);

      OutputStream ostream = null;

      try
      {
				processor.getTransformer().transform(new javax.xml.transform.dom.DOMSource(sourceTree),
																						 outputTarget.getResultObject());
       
      }
      catch (TransformerException te)
      {
        throw new SAXException(te); 
      }
       
      finally
      {
        if (null != ostream)
        {
          ostream.close();
        }
      }
    }

  }
  
  /**
   * Creates a compatible SAX serializer for the specified writer
   * and output format. If the output format is missing, the default
   * is an XML format with UTF8 encoding.
   *
   * @param writer The writer
   * @param format The output format
   * @return A compatible SAX serializer
   */
  public DocumentHandler makeSAXSerializer( Writer writer, OutputFormat format )
    throws IOException, SAXException 
  {
    DocumentHandler handler;
    if ( format == null )
    {
      format = new OutputFormat( "xml", "UTF-8", false );
      handler = null;
    }
    else 
    {
      handler = new ParserAdapter(new org.apache.xerces.parsers.SAXParser());
      OutputProperties props = m_sRootObject.getOutputComposed();
      
      if ( format.getMethod().equalsIgnoreCase( "html" ) )
      {
        FormatterToHTML serializer = new FormatterToHTML(writer);
        serializer.getSerializerObject().setOutputFormat(props.getProperties());
        ((ParserAdapter)handler).setContentHandler(serializer.getSerializerObject());
      }
      else if ( format.getMethod().equalsIgnoreCase( "xml" ) )
      {
        FormatterToXML serializer = new FormatterToXML(writer);
        serializer.getSerializerObject().setOutputFormat(props.getProperties());
        ((ParserAdapter)handler).setContentHandler(serializer.getSerializerObject());
      }
      else if ( format.getMethod().equalsIgnoreCase( "text" ) )
      {
        FormatterToText serializer = new FormatterToText(writer); 
        ((ParserAdapter)handler).setContentHandler(serializer.getSerializerObject());
      }
      else
      {
        handler = null;
      }
    }
    
    if(null == handler)
    {
      String method = format.getMethod();
      org.apache.xml.serialize.SerializerFactory factory = org.apache.xml.serialize.SerializerFactory.getSerializerFactory(method);
      org.apache.xml.serialize.Serializer serializer = factory.makeSerializer(writer, format);
      handler = serializer.asDocumentHandler();
    }

    return handler;
  }
  
  /**
   * Creates a compatible SAX serializer for the specified output stream
   * and output format. If the output format is missing, the default
   * is an XML format with UTF8 encoding.
   *
   * @param ostream The output stream.
   * @param format The output format
   * @return A compatible SAX serializer
   */
  public DocumentHandler makeSAXSerializer( OutputStream ostream, OutputFormat format )
    throws UnsupportedEncodingException, IOException, SAXException
  {
    DocumentHandler handler;
    OutputProperties props;
        
    if ( format == null )
    {
      props = new OutputProperties();
      handler = null;
    }
    else
    { 
      handler = new ParserAdapter(new org.apache.xerces.parsers.SAXParser());
      props = m_sRootObject.getOutputComposed();
      if ( format.getMethod().equalsIgnoreCase( "html" ) )
      {
        FormatterToHTML serializer = new FormatterToHTML(ostream);
        serializer.getSerializerObject().setOutputFormat(props.getProperties());
        ((ParserAdapter)handler).setContentHandler(serializer.getSerializerObject());
      }
      else if ( format.getMethod().equalsIgnoreCase( "xml" ) )
      {
        FormatterToXML serializer = new FormatterToXML(ostream);
        serializer.getSerializerObject().setOutputFormat(props.getProperties());
        ((ParserAdapter)handler).setContentHandler(serializer.getSerializerObject());
      }
      else if ( format.getMethod().equalsIgnoreCase( "text" ) )
      {
        String encoding = format.getEncoding();
        if(null == encoding)
        {
          try
          {
            encoding = System.getProperty("file.encoding");
            encoding = (null != encoding) ?
                       org.apache.xalan.serialize.Encodings.convertJava2MimeEncoding( encoding ) : "ASCII";
            if(null == encoding)
            {
              encoding = "ASCII";
            }
          }
          catch(SecurityException se)
          {
            encoding = "ASCII";
          }
        }

        //this.m_encoding =   encoding;

        String javaEncoding = org.apache.xalan.serialize.Encodings.convertMime2JavaEncoding(encoding);

        Writer w = new OutputStreamWriter( ostream, javaEncoding );
        ((ParserAdapter)handler).setContentHandler(new FormatterToText(w).getSerializerObject());
      }
      else if ( format.getMethod().equalsIgnoreCase( "xhtml" ) )
      {
        handler = new XMLSerializer(ostream, format);
        //handler = Serializer.makeSAXSerializer(ostream, format);
      }
      else
      {
        handler = new XMLSerializer(ostream, format);
        //handler = Serializer.makeSAXSerializer(ostream, format);
      }
    }
    if(null == handler)
    {
      String method = props.getProperty(OutputKeys.METHOD);
      org.apache.xml.serialize.SerializerFactory factory = org.apache.xml.serialize.SerializerFactory.getSerializerFactory(method);
      org.apache.xml.serialize.Serializer serializer = factory.makeSerializer(ostream, format);
      handler = serializer.asDocumentHandler();
    }

    return handler;
  }

  /**
   * Creates a compatible SAX serializer for the specified output stream
   * and output format. If the output format is missing, the default
   * is an XML format with UTF8 encoding.
   *
   * @param ostream The output stream.
   * @return A compatible SAX serializer
   */
  public DocumentHandler getSAXSerializer( OutputStream ostream )
    throws UnsupportedEncodingException, IOException, SAXException
  {
   return makeSAXSerializer(ostream, getOutputFormat());
  }
  
  /**
   * Get a new OutputFormat object according to the xsl:output attributes.
   */
  public OutputFormat getOutputFormat()
  {
    OutputProperties outputFormat = m_sRootObject.getOutputComposed();
    if (outputFormat == null)
      return null;
    OutputFormat formatter = new OutputFormat(outputFormat.getProperty(OutputKeys.METHOD),
                                              outputFormat.getProperty(OutputKeys.ENCODING),
                                              OutputProperties.getBooleanProperty(OutputKeys.INDENT, outputFormat.getProperties()));
    formatter.setDoctype(outputFormat.getProperty(OutputKeys.DOCTYPE_PUBLIC), 
                         outputFormat.getProperty(OutputKeys.DOCTYPE_SYSTEM));
    formatter.setOmitXMLDeclaration(OutputProperties.getBooleanProperty(OutputKeys.OMIT_XML_DECLARATION, outputFormat.getProperties()));
    formatter.setStandalone(OutputProperties.getBooleanProperty(OutputKeys.STANDALONE, outputFormat.getProperties()));
    formatter.setMediaType(outputFormat.getProperty(OutputKeys.MEDIA_TYPE));
    formatter.setVersion(outputFormat.getProperty(OutputKeys.VERSION));
    // This is to get around differences between Xalan and Xerces.
    // Xalan uses -1 as default for no indenting, Xerces uses 0.
    // So we just bump up the indent value here because we will
    // subtract from it at output time (FormatterToXML.init());
   // if (getOutputIndent())
   //   formatter.setIndent(formatter.getIndent()+1);

    
    return formatter;
  }
  
  /** 
   * Get the encoding string that was specified in the stylesheet. 
   */
  public String getOutputEncoding()
  {
    return m_sRootObject.getOutputComposed().getProperty(OutputKeys.ENCODING);
  }

  /** 
   * Get the media-type string that was specified in the stylesheet. 
   */
  public String getOutputMediaType() 
  { 
    return m_sRootObject.getOutputComposed().getProperty(OutputKeys.MEDIA_TYPE); 
  }
  
  /** 
   * Get the output method that was specified in the stylesheet. 
   */
  public String getOutputMethod() 
  { 
    return m_sRootObject.getOutputComposed().getProperty(OutputKeys.METHOD); 
  }
  
  /**
   * Get the base identifier with which this stylesheet is associated.
   */
  public String getBaseIdentifier()
  {
    return m_sRootObject.getBaseIdentifier();
  }

  /**
   * Get the base identifier with which this stylesheet is associated.
   */
  public void setBaseIdentifier(String baseIdent)
  {
    m_sRootObject.setHref(baseIdent);
  }
  
}
