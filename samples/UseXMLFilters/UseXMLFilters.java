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
import trax.Processor; 
import trax.Templates;
import trax.Transformer; 
import trax.Result;
import trax.ProcessorException; 
import trax.ProcessorFactoryException;
import trax.TransformException; 

// Imported SAX classes
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.Parser;
import org.xml.sax.helpers.ParserAdapter;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.XMLReader;
import org.xml.sax.ContentHandler;

// Imported DOM classes
import org.w3c.dom.Node;

// Imported Serializer classes
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.Serializer;
import org.apache.xml.serialize.SerializerFactory;

// Imported JAVA API for XML Parsing 1.0 classes
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException; 

// Imported java.io classes
import java.io.InputStream;
import java.io.Reader;
import java.io.IOException;

  /**
   * This example shows how to chain a series of transformations by
   * piping SAX events from one Transformer to another. Each Transformer
   * operates as a SAX2 XMLFilter/XMLReader.
   */
public class UseXMLFilters
{
	public static void main(String[] args)
	    throws SAXException, IOException, ParserConfigurationException
  {  
    // Instantiate a stylesheet processor.
	Processor processor = Processor.newInstance("xslt");

	// Create a Templates object and a Transformer for each stylesheet.
    Templates stylesheet1 = processor.process(new InputSource("foo1.xsl"));
    Transformer transformer1 = stylesheet1.newTransformer();

	Templates stylesheet2= processor.process(new InputSource("foo2.xsl"));
    Transformer transformer2 = stylesheet2.newTransformer();

    Templates  stylesheet3 = processor.process(new InputSource("foo3.xsl"));
    Transformer transformer3= stylesheet3.newTransformer();
    
    // Create an XMLReader (implemented by the Xerces SAXParser).
	XMLReader reader = XMLReaderFactory.createXMLReader();
    
    // transformer1 uses the reader (SAXParser) as its reader.
    transformer1.setParent(reader);
    
    // transformer2 uses transformer1 as its reader.
    transformer2.setParent(transformer1);
    
    // transform3 uses transform2 as its reader.
    transformer3.setParent(transformer2);
    
    // transformer3 outputs SAX events to the serializer.
    SerializerFactory sf = SerializerFactory.getSerializerFactory("xml");
    Serializer serializer = sf.makeSerializer(System.out, new OutputFormat());
    transformer3.setContentHandler(serializer.asContentHandler());

	// Perform the series of transformations as follows:
	//   transformer3 gets its parent (transformer2) as the XMLReader/XMLFilter
	//   and calls transformer2.parse(new InputSource("foo.xml")).
    //   transformer2 gets its parent (transformer1) as the XMLReader/XMLFilter
	//   and calls transformer1.parse(new InputSource("foo.xml")). 
    //   transformer1 gets its parent (reader, a SAXParser) as the XMLReader 
    //   and calls reader.parse(new InputSource("foo.xml")).
	//   reader parses the XML document and sends the SAX parse events to transformer1, 
	//   which performs transformation 1 and sends the output to transformer2.
	//   transformer2 parses the transformation 1 output, performs transformation 2, and 
	//   sends the output to transformer3.
	//   transformer3 parses the transformation 2 output, performs transformation 3,
	//   and sends the output to the serializer.
    transformer3.parse(new InputSource("foo.xml"));
  }
}