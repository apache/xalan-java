/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights 
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

import java.io.IOException;

import javax.xml.transform.TransformerException;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.XMLFilter;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.DTDHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.XMLFilterImpl;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.ErrorListener;

import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.res.XSLMessages;


public class TrAXFilter extends XMLFilterImpl
{
  private Templates m_templates;
  private TransformerImpl m_transformer;
    
  /**
   * Construct an empty XML filter, with no parent.
   *
   * <p>This filter will have no parent: you must assign a parent
   * before you start a parse or do any configuration with
   * setFeature or setProperty.</p>
   *
   * @see org.xml.sax.XMLReader#setFeature
   * @see org.xml.sax.XMLReader#setProperty
   */
  public TrAXFilter (Templates templates)
    throws TransformerConfigurationException
  {
    m_templates = templates;
    m_transformer = (TransformerImpl)templates.newTransformer();
  }
  
  /**
   * Return the Transformer object used for this XML filter.
   */
  public TransformerImpl getTransformer()
  {
    return m_transformer;
  }
  
  /** Set the parent reader.
   *
   * <p>This is the {@link org.xml.sax.XMLReader XMLReader} from which 
   * this filter will obtain its events and to which it will pass its 
   * configuration requests.  The parent may itself be another filter.</p>
   *
   * <p>If there is no parent reader set, any attempt to parse
   * or to set or get a feature or property will fail.</p>
   *
   * @param parent The parent XML reader.
   * @throws java.lang.NullPointerException If the parent is null.
   */
  public void setParent (XMLReader parent)
  { 
    super.setParent(parent);
    
    if(null != parent.getContentHandler())
      this.setContentHandler(parent.getContentHandler());

    // Not really sure if we should do this here, but 
    // it seems safer in case someone calls parse() on 
    // the parent.
    setupParse ();
  }
  
  /**
   * Parse a document.
   *
   * @param input The input source for the document entity.
   * @throws org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @throws java.io.IOException An IO exception from the parser,
   *            possibly from a byte stream or character stream
   *            supplied by the application.
   * @see org.xml.sax.XMLReader#parse(org.xml.sax.InputSource)
   */
  public void parse (InputSource input)
    throws org.xml.sax.SAXException, IOException
  {
    if(null == getParent())
    {
      XMLReader reader=null;

      // Use JAXP1.1 ( if possible )
      try {
          javax.xml.parsers.SAXParserFactory factory=
              javax.xml.parsers.SAXParserFactory.newInstance();
          factory.setNamespaceAware( true );
          javax.xml.parsers.SAXParser jaxpParser=
              factory.newSAXParser();
          reader=jaxpParser.getXMLReader();
          
      } catch( javax.xml.parsers.ParserConfigurationException ex ) {
          throw new org.xml.sax.SAXException( ex );
      } catch( javax.xml.parsers.FactoryConfigurationError ex1 ) {
          throw new org.xml.sax.SAXException( ex1.toString() );
      } catch( NoSuchMethodError ex2 ) {
      }
      catch (AbstractMethodError ame){}

      XMLReader parent;
      if( reader==null )
          parent= XMLReaderFactory.createXMLReader();
      else
          parent=reader;
      try
      {
        parent.setFeature("http://xml.org/sax/features/namespace-prefixes",
                          true);
      }
      catch (org.xml.sax.SAXException se){}
      // setParent calls setupParse...
      setParent(parent);
    }
    else
    {
      // Make sure everything is set up.
      setupParse ();
    }
    if(null == m_transformer.getContentHandler())
    {
      throw new org.xml.sax.SAXException(XSLMessages.createMessage(XSLTErrorResources.ER_CANNOT_CALL_PARSE, null)); //"parse can not be called if the ContentHandler has not been set!");
    }

    getParent().parse(input);
    Exception e = m_transformer.getExceptionThrown();
    if(null != e)
    {
      if(e instanceof org.xml.sax.SAXException)
        throw (org.xml.sax.SAXException)e;
      else
        throw new org.xml.sax.SAXException(e);
    }
  }
  
  /**
   * Parse a document.
   *
   * @param systemId The system identifier as a fully-qualified URI.
   * @throws org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @throws java.io.IOException An IO exception from the parser,
   *            possibly from a byte stream or character stream
   *            supplied by the application.
   * @see org.xml.sax.XMLReader#parse(java.lang.String)
   */
  public void parse (String systemId)
    throws org.xml.sax.SAXException, IOException
  {
    parse(new InputSource(systemId));
  }


  /**
   * Set up before a parse.
   *
   * <p>Before every parse, check whether the parent is
   * non-null, and re-register the filter for all of the 
   * events.</p>
   */
  private void setupParse ()
  {
    XMLReader p = getParent();
    if (p == null) {
      throw new NullPointerException(XSLMessages.createMessage(XSLTErrorResources.ER_NO_PARENT_FOR_FILTER, null)); //"No parent for filter");
    }
    
    ContentHandler ch = m_transformer.getInputContentHandler();
//    if(ch instanceof SourceTreeHandler)
//      ((SourceTreeHandler)ch).setUseMultiThreading(true);
    p.setContentHandler(ch);

    if(ch instanceof EntityResolver)
      p.setEntityResolver((EntityResolver)ch);
    else
      p.setEntityResolver(this);
    
    if(ch instanceof DTDHandler)
      p.setDTDHandler((DTDHandler)ch);
    else
      p.setDTDHandler(this);
    
    ErrorListener elistener = m_transformer.getErrorListener();
    if((null != elistener) && (elistener instanceof org.xml.sax.ErrorHandler))
      p.setErrorHandler((org.xml.sax.ErrorHandler)elistener);
    else
      p.setErrorHandler(this);
  }

  /**
   * Set the content event handler.
   *
   * @param resolver The new content handler.
   * @throws java.lang.NullPointerException If the handler
   *            is null.
   * @see org.xml.sax.XMLReader#setContentHandler
   */
  public void setContentHandler (ContentHandler handler)
  {
    m_transformer.setContentHandler(handler);
    // super.setContentHandler(m_transformer.getResultTreeHandler());
  }
  
  public void setErrorListener (ErrorListener handler)
  {
    m_transformer.setErrorListener(handler);
  }

}
