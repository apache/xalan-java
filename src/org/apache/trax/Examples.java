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
package org.apache.trax;

// Needed java classes
import java.io.InputStream;
import java.io.Reader;
import java.io.IOException;

// Needed SAX classes
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.Parser;
import org.xml.sax.helpers.ParserAdapter;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.XMLReader;
import org.xml.sax.ContentHandler;

// Needed DOM classes
import org.w3c.dom.Node;

// Needed Serializer classes
import org.apache.serialize.OutputFormat;
import org.apache.serialize.Serializer;
import org.apache.serialize.SerializerFactory;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.BufferedInputStream;  // dml

/**
 * Some examples to show how the Simple API for Transformations
 * could be used.
 *
 * @version Alpha
 * @author <a href="mailto:scott_boag@lotus.com">Scott Boag</a>
 */
public class Examples
{

  /**
   * NEEDSDOC Method main 
   *
   *
   * NEEDSDOC @param argv
   *
   * @throws IOException
   * @throws ParserConfigurationException
   * @throws ProcessorException
   * @throws ProcessorFactoryException
   * @throws SAXException
   * @throws TransformException
   */
  public static void main(String argv[])
          throws ProcessorException, ProcessorFactoryException,
                 TransformException, SAXException, IOException,
                 ParserConfigurationException
  {

    System.out.println("==== exampleSimple ====");
    exampleSimple("foo.xml", "foo.xsl");
    System.out.println("\n==== exampleSAX2SAX ====");
    exampleSAX2SAX("foo.xml", "foo.xsl");
    System.out.println("\n==== exampleXMLFilter ====");
    exampleXMLFilter("foo.xml", "foo.xsl");
    System.out.println("\n==== exampleXMLFilterChain ====");
    exampleXMLFilterChain("foo.xml", "foo.xsl", "foo2.xsl", "foo.xsl");
    System.out.println("\n==== exampleDOM2DOM ====");
    exampleDOM2DOM("foo.xml", "foo.xsl");
    System.out.println("\n==== exampleParam ====");
    exampleParam("foo.xml", "param.xsl");
    System.out.println("\n==== exampleOutputFormat ====");
    exampleOutputFormat("foo.xml", "foo.xsl");
    System.out.println("==== exampleUseAssociated ====");
  }

  /**
   * Show the simplest possible transformation from system id to output stream.
   *
   * NEEDSDOC @param sourceID
   * NEEDSDOC @param xslID
   *
   * @throws IOException
   * @throws ProcessorException
   * @throws ProcessorFactoryException
   * @throws SAXException
   * @throws TransformException
   */
  public static void exampleSimple(String sourceID, String xslID)
          throws ProcessorException, ProcessorFactoryException,
                 TransformException, SAXException, IOException
  {

    Processor processor = Processor.newInstance("xslt");
    Templates templates = processor.process(new InputSource(xslID));
    Transformer transformer = templates.newTransformer();

    transformer.transform(new InputSource(sourceID), new Result(System.out));
  }

  /**
   * Show the Transformer using SAX events in and SAX events out.
   *
   * NEEDSDOC @param sourceID
   * NEEDSDOC @param xslID
   *
   * @throws IOException
   * @throws SAXException
   */
  public static void exampleSAX2SAX(String sourceID, String xslID)
          throws SAXException, IOException  // , ParserConfigurationException
  {

    Processor processor = Processor.newInstance("xslt");
    XMLReader reader = XMLReaderFactory.createXMLReader();

    // Have a Templates builder handle the parse events from the SAXParser's 
    // parse of an xslt file.
    TemplatesBuilder templatesBuilder = processor.getTemplatesBuilder();

    reader.setContentHandler(templatesBuilder);

    if (templatesBuilder instanceof org.xml.sax.ext.LexicalHandler)
      reader.setProperty("http://xml.org/sax/properties/lexical-handler",
                         templatesBuilder);

    reader.parse(xslID);

    Templates templates = templatesBuilder.getTemplates();

    // Get a transformer instance for the templates.
    Transformer transformer = templates.newTransformer();

    // Set the result handling to be a serialization to System.out.
    Serializer serializer = SerializerFactory.getSerializer("xml");

    serializer.setOutputStream(System.out);
    transformer.setContentHandler(serializer.asContentHandler());

    // Cause the transformation to occur by asking the parser to send 
    // the parse events from "foo.xsl" to the transformer.
    ContentHandler chandler = transformer.getInputContentHandler();

    reader.setContentHandler(chandler);

    if (chandler instanceof org.xml.sax.ext.LexicalHandler)
      reader.setProperty("http://xml.org/sax/properties/lexical-handler",
                         chandler);
    else
      reader.setProperty("http://xml.org/sax/properties/lexical-handler",
                         null);

    reader.parse(sourceID);
  }

  /**
   * Show the Transformer as a SAX2 XMLFilter/XMLReader.  In this case
   * the Transformer acts like a parser, and can in fact be polymorphicaly
   * used in places where a SAX parser would be used.
   *
   * NEEDSDOC @param sourceID
   * NEEDSDOC @param xslID
   *
   * @throws IOException
   * @throws SAXException
   */
  public static void exampleXMLFilter(String sourceID, String xslID)
          throws SAXException, IOException  // , ParserConfigurationException
  {

    Processor processor = Processor.newInstance("xslt");
    Templates templates = processor.process(new InputSource(xslID));
    Transformer transformer = templates.newTransformer();

    // Set the result handling to be a serialization to System.out.
    Serializer serializer =
      SerializerFactory.getSerializer(new OutputFormat());

    serializer.setOutputStream(System.out);
    transformer.setContentHandler(serializer.asContentHandler());

    // The transformer will use a SAX parser as it's reader.    
    XMLReader reader = XMLReaderFactory.createXMLReader();

    transformer.setParent(reader);

    // Now, when you call transformer.parse, it will set itself as 
    // the content handler for the parser object (it's "parent"), and 
    // will then call the parse method on the parser.
    transformer.parse(new InputSource(sourceID));
  }

  /**
   * This example shows how to chain events from one Transformer
   * to another transformer, using the Transformer as a
   * SAX2 XMLFilter/XMLReader.
   *
   * NEEDSDOC @param sourceID
   * NEEDSDOC @param xslID_1
   * NEEDSDOC @param xslID_2
   * NEEDSDOC @param xslID_3
   *
   * @throws IOException
   * @throws SAXException
   */
  public static void exampleXMLFilterChain(
          String sourceID, String xslID_1, String xslID_2, String xslID_3)
            throws SAXException, IOException  // , ParserConfigurationException
  {

    Processor processor = Processor.newInstance("xslt");
    Templates stylesheet1 = processor.process(new InputSource(xslID_1));
    Transformer transformer1 = stylesheet1.newTransformer();
    Templates stylesheet2 = processor.process(new InputSource(xslID_2));
    Transformer transformer2 = stylesheet2.newTransformer();
    Templates stylesheet3 = processor.process(new InputSource(xslID_3));
    Transformer transformer3 = stylesheet3.newTransformer();
    XMLReader reader = XMLReaderFactory.createXMLReader();

    // transformer1 will use a SAX parser as it's reader.    
    transformer1.setParent(reader);

    // transformer2 will use transformer1 as it's reader.
    transformer2.setParent(transformer1);

    // transform3 will use transform2 as it's reader.
    transformer3.setParent(transformer2);

    // transform3 will output the events to the serializer.
    Serializer serializer =
      SerializerFactory.getSerializer(new OutputFormat());

    serializer.setOutputStream(System.out);
    transformer3.setContentHandler(serializer.asContentHandler());

    // Now, when you call transformer3 to parse, it will set  
    // itself as the ContentHandler for transform2, and 
    // call transform2.parse, which will set itself as the 
    // content handler for transform1, and call transform1.parse, 
    // which will set itself as the content listener for the 
    // SAX parser, and call parser.parse(new InputSource("foo.xml")).
    transformer3.parse(new InputSource(sourceID));
  }

  /**
   * Show how to transform a DOM tree into another DOM tree.
   * This uses the javax.xml.parsers to parse an XML file into a
   * DOM, and create an output DOM.
   *
   * NEEDSDOC @param sourceID
   * NEEDSDOC @param xslID
   *
   * @throws IOException
   * @throws ParserConfigurationException
   * @throws SAXException
   */
  public static void exampleDOM2DOM(String sourceID, String xslID)
          throws SAXException, IOException, ParserConfigurationException
  {

    Processor processor = Processor.newInstance("xslt");

    if (processor.getFeature("http://xml.org/trax/features/dom/input"))
    {
      Templates templates;

      {
        DocumentBuilderFactory dfactory =
          DocumentBuilderFactory.newInstance();

        dfactory.setNamespaceAware(true);
        dfactory.setValidating(true);

        DocumentBuilder docBuilder = dfactory.newDocumentBuilder();

        // org.w3c.dom.Document outNode = docBuilder.newDocument();
        Node doc = docBuilder.parse(new InputSource(xslID));

        templates = processor.processFromNode(doc);
      }

      Transformer transformer = templates.newTransformer();
      DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = dfactory.newDocumentBuilder();
      org.w3c.dom.Document outNode = docBuilder.newDocument();
      Node doc = docBuilder.parse(new InputSource(sourceID));

      transformer.transformNode(doc, new Result(outNode));

      Serializer serializer =
        SerializerFactory.getSerializer(new OutputFormat());

      serializer.setOutputStream(System.out);
      serializer.asDOMSerializer().serialize(outNode);
    }
    else
    {
      throw new org.xml.sax.SAXNotSupportedException(
        "DOM node processing not supported!");
    }
  }

  /**
   * This shows how to set a parameter for use by the templates.
   *
   * NEEDSDOC @param sourceID
   * NEEDSDOC @param xslID
   *
   * @throws IOException
   * @throws ProcessorException
   * @throws ProcessorFactoryException
   * @throws SAXException
   * @throws TransformException
   */
  public static void exampleParam(String sourceID, String xslID)
          throws ProcessorException, ProcessorFactoryException,
                 TransformException, SAXException, IOException
  {

    Processor processor = Processor.newInstance("xslt");
    Templates templates = processor.process(new InputSource(xslID));
    Transformer transformer = templates.newTransformer();

    transformer.setParameter("my-param", null /* namespace */,
                             "hello to you!");
    transformer.transform(new InputSource(sourceID), new Result(System.out));
  }

  /**
   * Show how to override output properties.
   *
   * NEEDSDOC @param sourceID
   * NEEDSDOC @param xslID
   *
   * @throws IOException
   * @throws ProcessorException
   * @throws ProcessorFactoryException
   * @throws SAXException
   * @throws TransformException
   */
  public static void exampleOutputFormat(String sourceID, String xslID)
          throws ProcessorException, ProcessorFactoryException,
                 TransformException, SAXException, IOException
  {

    Processor processor = Processor.newInstance("xslt");
    Templates templates = processor.process(new InputSource(xslID));
    OutputFormat oprops = templates.getOutputFormat();

    oprops.setIndent(true);

    Transformer transformer = templates.newTransformer();

    transformer.setOutputFormat(oprops);
    transformer.transform(new InputSource(sourceID), new Result(System.out));
  }

  /**
   * Show how to get stylesheets that are associated with a given
   * xml document via the xml-stylesheet PI (see http://www.w3.org/TR/xml-stylesheet/).
   *
   * @throws IOException
   * @throws ProcessorException
   * @throws ProcessorFactoryException
   * @throws SAXException
   * @throws TransformException
   */
  public static void exampleUseAssociated()
          throws ProcessorException, ProcessorFactoryException,
                 TransformException, SAXException, IOException
  {

    Processor processor = Processor.newInstance("xslt");
    InputSource docSouce = new InputSource("foo.xml");
    InputSource[] sources = processor.getAssociatedStylesheets(docSouce,
                              "text/xslt", null, null);

    // Processor may remember it read the given document...
    Templates templates = (null != sources)
                          ? processor.processMultiple(sources)
                          : processor.process(new InputSource("default.xsl"));
    Transformer transformer = templates.newTransformer();

    transformer.transform(docSouce, new Result(System.out));
  }
}
