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

// Imported TraX classes
import trax.*;

// Imported java.io class
import java.io.IOException;

// Imported SAX classes
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

// Imported DOM classes
import org.w3c.dom.Document;
import org.w3c.dom.Node;

// Imported Serializer classes
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.Serializer;
import org.apache.xml.serialize.SerializerFactory;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException; 

  /**
   * Show how to transform a DOM tree into another DOM tree.  
   * This uses the javax.xml.parsers to parse an XML file into a 
   * DOM, and create an output DOM.
   */
public class TransformToDom
{
	public static void main(String[] args)
		throws SAXException, IOException, ParserConfigurationException
  {  
    Processor processor = Processor.newInstance("xslt");

    if(processor.getFeature("http://xml.org/trax/features/dom/input"))
    {
      Templates templates = processor.process(new InputSource("foo.xsl"));
      Transformer transformer = templates.newTransformer();

	  // Use an implementation of the JAVA API for XML Parsing 1.0 to
	  // create a DOM Document node to contain the result.
	  DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = dfactory.newDocumentBuilder();
      Document outNode = docBuilder.newDocument();
	  
	  // Perform the transformation, placing the output in the DOM
	  // Document Node.
      /** doesn't yet work -- no error, but no output either!
      transformer.transform(new InputSource("foo.xml"), new Result(outNode));
 
      For time being, input must also be DOM.
      */
      Node doc = docBuilder.parse(new InputSource("foo.xml"));
      transformer.transformNode(doc, new Result(outNode));
	  
	  //Instantiate an XML serializer and use it to serialize the output DOM to System.out
	  // using a default output format.
      SerializerFactory sf = SerializerFactory.getSerializerFactory("xml");
      Serializer serializer = sf.makeSerializer(System.out, new OutputFormat());
      serializer.asDOMSerializer().serialize(outNode);
	}
    else
    {
      throw new org.xml.sax.SAXNotSupportedException("DOM node processing not supported!");
    }
  }
}
