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

import java.io.IOException;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.apache.xml.serializer.Serializer;
import org.apache.xml.serializer.SerializerFactory;
import org.apache.xml.serializer.OutputPropertiesFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

  /**
   * This example shows how to chain a series of transformations by
   * piping SAX events from one Transformer to another. Each Transformer
   * operates as a SAX2 XMLFilter/XMLReader.
   */
public class UseXMLFilters
{
  public static void main(String[] args)
	throws TransformerException, TransformerConfigurationException, 
         SAXException, IOException	   
	{
    // Instantiate  a TransformerFactory.
  	TransformerFactory tFactory = TransformerFactory.newInstance();
    // Determine whether the TransformerFactory supports The use uf SAXSource 
    // and SAXResult
    if (tFactory.getFeature(SAXSource.FEATURE) && tFactory.getFeature(SAXResult.FEATURE))
    { 
      // Cast the TransformerFactory to SAXTransformerFactory.
      SAXTransformerFactory saxTFactory = ((SAXTransformerFactory) tFactory);
  	  // Create an XMLFilter for each stylesheet.
      XMLFilter xmlFilter1 = saxTFactory.newXMLFilter(new StreamSource("foo1.xsl"));
      XMLFilter xmlFilter2 = saxTFactory.newXMLFilter(new StreamSource("foo2.xsl"));
      XMLFilter xmlFilter3 = saxTFactory.newXMLFilter(new StreamSource("foo3.xsl"));
    
      // Create an XMLReader.
	    XMLReader reader = XMLReaderFactory.createXMLReader();
    
      // xmlFilter1 uses the XMLReader as its reader.
      xmlFilter1.setParent(reader);
    
      // xmlFilter2 uses xmlFilter1 as its reader.
      xmlFilter2.setParent(xmlFilter1);
    
      // xmlFilter3 uses xmlFilter2 as its reader.
      xmlFilter3.setParent(xmlFilter2);
    
      // xmlFilter3 outputs SAX events to the serializer.
      Serializer serializer = SerializerFactory.getSerializer
                      (OutputPropertiesFactory.getDefaultMethodProperties("xml"));        
      serializer.setOutputStream(System.out);
      xmlFilter3.setContentHandler(serializer.asContentHandler());

  	  // Perform the series of transformations as follows:
	    //   - transformer3 gets its parent (transformer2) as the XMLReader/XMLFilter
	    //     and calls transformer2.parse(new InputSource("foo.xml")).
      //   - transformer2 gets its parent (transformer1) as the XMLReader/XMLFilter
	    //     and calls transformer1.parse(new InputSource("foo.xml")). 
      //   - transformer1 gets its parent (reader, a SAXParser) as the XMLReader 
      //     and calls reader.parse(new InputSource("foo.xml")).
	    //   - reader parses the XML document and sends the SAX parse events to transformer1, 
	    //     which performs transformation 1 and sends the output to transformer2.
  	  //   - transformer2 parses the transformation 1 output, performs transformation 2, and 
	    //     sends the output to transformer3.
	    //   - transformer3 parses the transformation 2 output, performs transformation 3,
  	  //     and sends the output to the serializer.
      xmlFilter3.parse(new InputSource("foo.xml"));
    }
  }
}