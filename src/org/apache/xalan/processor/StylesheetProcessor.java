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
 *     the documentation and/or other materials provided with the
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
package org.apache.xalan.processor;

import java.io.IOException;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.XMLReader;
import org.xml.sax.SAXException;

import org.w3c.dom.Node;

import trax.Processor;
import trax.ProcessorException;
import trax.Templates;
import trax.TemplatesBuilder;

/**
 * The StylesheetProcessor, which implements the TRaX Processor 
 * interface, processes XSLT Stylesheets into a Templates object.
 */
public class StylesheetProcessor extends Processor
{
  /**
   * Process the source into a templates object.
   * 
   * @param source An object that holds a URL, input stream, etc.
   * @returns A Templates object capable of being used for transformation purposes.
   */
  public Templates process(InputSource source)
    throws ProcessorException, SAXException, IOException
  {
    TemplatesBuilder builder = getTemplatesBuilder();
    builder.setBaseID(source.getSystemId());
    XMLReader reader = this.getXMLReader();
    if(null == reader)
    {
      reader = XMLReaderFactory.createXMLReader();
    }
    reader.setContentHandler(builder);
    reader.parse(source);
    return builder.getTemplates();
  }

  /**
   * Process the stylesheet from a DOM tree, if the 
   * processor supports the "http://xml.org/trax/features/dom/input" 
   * feature.    
   * 
   * @param node A DOM tree which must contain 
   * valid transform instructions that this processor understands.
   * @returns A Templates object capable of being used for transformation purposes.
   */
  public Templates processFromNode(Node node)
    throws ProcessorException
  {
    return null;
  }

  /**
   * Process a series of inputs, treating them in import or cascade 
   * order.  This is mainly for support of the getAssociatedStylesheets
   * method, but may be useful for other purposes.
   * 
   * @param sources An array of SAX InputSource objects.
   * @returns A Templates object capable of being used for transformation purposes.
   */
  public Templates processMultiple(InputSource[] source)
    throws ProcessorException
  {
    return null;
  }

  /**
   * Get InputSource specification(s) that are associated with the 
   * given document specified in the source param,
   * via the xml-stylesheet processing instruction 
   * (see http://www.w3.org/TR/xml-stylesheet/), and that matches 
   * the given criteria.  Note that it is possible to return several stylesheets 
   * that match the criteria, in which case they are applied as if they were 
   * a list of imports or cascades.
   * <p>Note that DOM2 has it's own mechanism for discovering stylesheets. 
   * Therefore, there isn't a DOM version of this method.</p>
   * 
   * <h3>Open issues:</h3>
   * <dl>
   *    <dt><h4>Does the xml-stylesheet recommendation really support multiple stylesheets?</h4></dt>
   *    <dd>Mike Kay wrote:  I don't see any support in the
   *        xml-stylesheet recommendation for this interpretation of what you should do
   *        if there's more than one match. Scott Boag replies: It's in the HTML references.  
   *        But it's a bit subtle.  We talked about this at the last XSL WG F2F, and people 
   *        agreed to the multiple stylesheet stuff.  I'll try and work out the specific 
   *        references.  Probably the xml-stylesheet recommendation needs to have a note 
   *        added to it.</dd>
   * </dl>
   * 
   * @param media The media attribute to be matched.  May be null, in which 
   *              case the prefered templates will be used (i.e. alternate = no).
   * @param title The value of the title attribute to match.  May be null.
   * @param charset The value of the charset attribute to match.  May be null.
   * @returns An array of InputSources that can be passed to processMultiple method.
   */
  public InputSource[] getAssociatedStylesheets(InputSource source,
                                                      String media, 
                                                      String title,
                                                      String charset)
    throws ProcessorException
  {
    return null;
  }
  
  /**
   * Get a TemplatesBuilder object that can process SAX 
   * events into a Templates object, if the processor supports the 
   * "http://xml.org/trax/features/sax/input" feature.
   * 
   * <h3>Open issues:</h3>
   * <dl>
   *    <dt><h4>Should Processor derive from org.xml.sax.ContentHandler?</h4></dt>
   *    <dd>Instead of requesting an object from the Processor class, should 
   *        the Processor class simply derive from org.xml.sax.ContentHandler?</dd>
   * </dl>
   * @return A TemplatesBuilder object, or null if not supported.
   * @exception May throw a ProcessorException if a StylesheetHandler can 
   * not be constructed for some reason.
   */
  public TemplatesBuilder getTemplatesBuilder()
    throws ProcessorException
  {
    return new StylesheetHandler(this);
  }

}
