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
// Transformations for XML (TRaX)
// Copyright ©2000 Lotus Development Corporation, Exoffice Technologies,
// Oracle Corporation, Michael Kay of International Computers Limited, Apache
// Software Foundation.  All rights reserved.
package trax;

import org.xml.sax.InputSource;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;
import org.xml.sax.DocumentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ext.DeclHandler;
import serialize.OutputFormat;
import org.w3c.dom.Node;

/**
 * This object represents a Transformer, which is a SAX2 XMLFilter.  
 * An object of this class can not be used concurrently over multiple threads.
 * 
 * <h3>Open issues:</h3>
 * <dl>
 *    <dt><h4>Separate DOM Interface?</h4></dt>
 *    <dd>Should there be a separate DOMTransformer class, instead of 
 *        having the transformNode method?</dd>
 *    <dt><h4>XMLFilter derivation?</h4></dt>
 *    <dd>There is some question in some people's mind whether or not 
 *        the Transformer interface should extend XMLFilter.</dd>
 *    <dt><h4>XMLReader vs. Parser vs. SAXParser/DocumentBuilder</h4></dt>
 *    <dd>Currently the interfaces support XMLReader.  Should this be 
 *        javax.xml.parsers.SAXParser/javax.xml.parsers.DocumentBuilder?
 *        Or, perhaps just org.xml.sax.Parser?</dd>
 *    <dt><h4>ContentHandler is entitled to expect a well-formed tree</h4></dt>
 *    <dd>Mike Kay: The output of XSLT is a well-balanced tree, or to put it
 *        another way, a well-formed external general parsed entity, but it is not in
 *        general a well-formed XML document. Specifically, it can have multiple
 *        elements and text nodes as children of the root. It's quite possible to feed
 *        such a tree into a SAX ContentHandler, but to do so breaks the implicit
 *        contract that the tree will be well-formed, and I have certainly encountered
 *        SAX ContentHandlers (or DocumentHandlers) that break if you try to do this
 *        (FOP is an example). This is one of those awful cases where it's difficult
 *        to provide the right solution for the 95% of people who want to generate
 *        well-formed output without falling over in the other 5% of cases. In Saxon
 *        I've been moving in the direction of allowing the ContentHandler itself to
 *        declare that it is prepared to accept well-balanced (but ill-formed) input.
 *    </dd>
 * </dl>
 * 
 * @version Alpha
 * @author <a href="mailto:scott_boag@lotus.com">Scott Boag</a>
 */
public interface Transformer extends XMLFilter
{
  /**
   * Process the source tree to SAX parse events.
   * @param xmlSource  The input for the source tree.
   */
  public void transform( InputSource xmlSource)
    throws TransformException;

  /**
   * Process the source tree to the output result.
   * @param xmlSource  The input for the source tree.
   * @param outputTarget The output source target.
   */
  public void transform( InputSource xmlSource, Result outputTarget)
    throws TransformException;

  /**
   * Process the source node to the output result, if the 
   * processor supports the "http://xml.org/trax/features/dom/input" 
   * feature.
   * @param node  The input source node, which can be any valid DOM node.
   * @param outputTarget The output source target.
   */
  public void transformNode( Node node, Result outputTarget)
    throws TransformException;

  /**
   * Process the source node to to SAX parse events, if the 
   * processor supports the "http://xml.org/trax/features/dom/input" 
   * feature.
   * @param node  The input source node, which can be any valid DOM node.
   */
  public void transformNode( Node node )
    throws TransformException;

  /**
   * Get a SAX2 ContentHandler for the input.
   * @return A valid ContentHandler, which should never be null, as 
   * long as getFeature("http://xml.org/trax/features/sax/input") 
   * returns true.
   * <h3>Open issues:</h3>
   * <dl>
   *    <dt><h4>ContentHandler vs. ContentHandler</h4></dt>
   *    <dd>I don't think I would use ContentHandler at all, except that JAXP uses it.  
   *        Maybe we should go back to using ContentHandler?</dd>
   * </dl>
   */
  ContentHandler getInputContentHandler();
  
   /**
   * Get a SAX2 DeclHandler for the input.
   * @return A valid DeclHandler, which should never be null, as 
   * long as getFeature("http://xml.org/trax/features/sax/input") 
   * returns true.
   */
  DeclHandler getInputDeclHandler();
 
   /**
   * Get a SAX2 LexicalHandler for the input.
   * @return A valid LexicalHandler, which should never be null, as 
   * long as getFeature("http://xml.org/trax/features/sax/input") 
   * returns true.
   */
  LexicalHandler getInputLexicalHandler();

  /**
   * Set the output properties for the transformation.  These 
   * properties will override properties set in the templates 
   * with xsl:output.
   * 
   * @see org.xml.serialize.OutputFormat
   */
  void setOutputFormat(OutputFormat oformat);
    
  /**
   * Set a parameter for the templates.
   * @param name The name of the parameter.
   * @param namespace The namespace of the parameter.
   * @value The value object.  This can be any valid Java object 
   * -- it's up to the processor to provide the proper 
   * coersion to the object, or simply pass it on for use 
   * in extensions.
   */
  void setParameter(String name, String namespace, Object value);
  
  /**
   * Reset the parameters to a null list.  
   */
  void resetParameters();
  
  /**
   * Set an object that will be used to resolve URIs used in 
   * document(), etc.
   * @param resolver An object that implements the URIResolver interface, 
   * or null.
   */
  void setURIResolver(URIResolver resolver);
  
}
