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

import org.apache.xalan.xpath.xml.XMLParserLiaison;
import org.apache.xpath.XPathFactory;
//import javax.xml.transform.Processor;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.Templates;
import org.apache.xml.utils.TreeWalker;

/**
 * <meta name="usage" content="general"/>
 * Manufactures the processor for performing transformations. Use one of the static getProcessor methods
 * to create an XSLTProcessor object.
 * @deprecated This compatibility layer will be removed in later releases. 
 */
public class XSLTProcessorFactory
{
  /**
   * Get a new XSLTProcessor.
   */
  public static XSLTProcessor getProcessor()
    throws org.xml.sax.SAXException
  {
    //Processor processor = Processor.newInstance("xslt"); 
    return new XSLTEngineImpl();
  }

  /**
   * Get a new XSLTProcessor (parserLiaisonClassName no longre used).
   * *
   * @see #getProcessor()
   * @see org.apache.xalan.xpath.xml.XMLParserLiaison
   */
  public static XSLTProcessor getProcessorUsingLiaisonName(String parserLiaisonClassName)
    throws org.xml.sax.SAXException
  {
    //return new XSLTEngineImpl(parserLiaisonClassName);
    //Processor processor = Processor.newInstance("xslt"); 
    return new XSLTEngineImpl();
  }

  /**
   * Get a new XSLTProcessor (XMLParserLiaison no longer used).
   *
   * @param parserLiaison the XMLParserLiaison set up to interact with a given XML parser.
   * @return An implementation of the XSLTProcessor interface with which you can perform transformations.
   *
   * @see #getProcessor()
   * @see org.apache.xalan.xpath.xml.XMLParserLiaison
   */
  public static XSLTProcessor getProcessor(XMLParserLiaison parserLiaison)
  {
    //return new XSLTEngineImpl(parserLiaison);
    try{
      //Processor processor = Processor.newInstance("xslt"); 
      return new XSLTEngineImpl();
    }
   /* catch (TransformerConfigurationException tce)
    {
      return null;
    }*/
    catch (org.xml.sax.SAXException e)
    {
      return null;
    }
    
  }

  /**
   * Get a new XSLTProcessor (XMLParserLiaison and XPathFactory no longer used).
   *
   * @param parserLiaison the XMLParserLiaison set up to interact with a given XML parser.
   * @param xpathFactory A custom XPathFactory.
   * @return An implentation of the XSLTProcessor interface with which you
   * can perform transformations.
   *
   * @see #getProcessor()
   * @see org.apache.xalan.xpath.xml.XMLParserLiaison
   */
  public static XSLTProcessor getProcessor(XMLParserLiaison parserLiaison,
                                                  XPathFactory xpathFactory)
  {
    //return new XSLTEngineImpl(parserLiaison, xpathFactory);
    try{
      //Processor processor = Processor.newInstance("xslt"); 
      return new XSLTEngineImpl();
    }
  /*  catch (TransformerConfigurationException pe)
    {
      return null;
    }*/
    catch (org.xml.sax.SAXException e)
    {
      return null;
    }
  }
}
