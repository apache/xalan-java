
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

/**
 *  Replicate the SimpleTransform sample, explicitly using the SAX model to handle the
 *  stylesheet, the XML input, and the transformation.
 */

import trax.Processor;
import trax.TemplatesBuilder;
import trax.Templates;
import trax.Transformer;
import trax.Result;
import trax.ProcessorException; 
import trax.ProcessorFactoryException;
import trax.TransformException; 

import org.xml.sax.XMLReader;
import org.xml.sax.ContentHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLReaderFactory;

import org.apache.xml.serialize.SerializerFactory;
import org.apache.xml.serialize.Serializer;
import org.apache.xml.serialize.OutputFormat;

import java.io.FileWriter;
import java.io.IOException;


public class SAX2SAX
{
  public static void main(String[] args)
  throws ProcessorException, ProcessorFactoryException, 
         TransformException, SAXException, IOException
  {  

    // I. Instantiate  a stylesheet processor.
    Processor processor = Processor.newInstance("xslt");

    // II. Process the stylesheet. producing a Templates object.

    // Get the XMLReader.
    XMLReader reader = XMLReaderFactory.createXMLReader();

    // Set the ContentHandler.
    TemplatesBuilder templatesBuilder = processor.getTemplatesBuilder();
    reader.setContentHandler(templatesBuilder);

    // Set the ContentHandler to also function as a LexicalHandler, which
    // includes "lexical" (e.g., comments and CDATA) events. The Xalan
    // TemplatesBuilder -- org.apache.xalan.processor.StylesheetHandler -- is
    // also a LexicalHandler).
    if(templatesBuilder instanceof LexicalHandler)
       reader.setProperty("http://xml.org/sax/properties/lexical-handler", 
                           templatesBuilder);

    // Parse the stylesheet.                       
    reader.parse("foo.xsl");

    //Get the Templates object from the ContentHandler.
    Templates templates = templatesBuilder.getTemplates();

    // III. Use the Templates object to instantiate a Transformer.
    Transformer transformer = templates.newTransformer();

    // IV. Perform the transformation.

    // Set up the ContentHandler for the output.
    Result result = new Result(new FileWriter("foo.out"));
    SerializerFactory sfactory = SerializerFactory.getSerializerFactory("xml");
    Serializer serializer = sfactory.makeSerializer (result.getCharacterStream(), 
	       							                 new OutputFormat());
    transformer.setContentHandler(serializer.asContentHandler());

    // Set up the ContentHandler for the input.
    org.xml.sax.ContentHandler chandler = transformer.getInputContentHandler();
    reader.setContentHandler(chandler);
    if(chandler instanceof LexicalHandler)
       reader.setProperty("http://xml.org/sax/properties/lexical-handler", chandler);
    else
       reader.setProperty("http://xml.org/sax/properties/lexical-handler", null);

    // Parse the XML input document. The input ContentHandler and output ContentHandler
    // work in separate threads to optimize performance.   
    reader.parse("foo.xml");
	
	System.out.println("************* The result is in foo.out *************");	
  }
}