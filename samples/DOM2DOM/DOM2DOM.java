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
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.dom.DOMResult;

// Imported java.io classes
import java.io.IOException;
import java.io.FileNotFoundException;

// Imported SAX classes
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

// Imported DOM classes
import org.w3c.dom.Document;
import org.w3c.dom.Node;

// Imported Serializer classes
import org.apache.xalan.serialize.Serializer;
import org.apache.xalan.serialize.SerializerFactory;

import org.apache.xalan.templates.OutputProperties;

// Imported JAVA API for XML Parsing classes
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException; 

  /**
   * Show how to transform a DOM tree into another DOM tree.  
   * This uses the javax.xml.parsers to parse both an XSL file 
   * and the XML file into a DOM, and create an output DOM.
   */
public class DOM2DOM
{
	public static void main(String[] args)
    throws TransformerException, TransformerConfigurationException, FileNotFoundException,
           ParserConfigurationException, SAXException, IOException
  {    
	  TransformerFactory tFactory = TransformerFactory.newInstance();

    if(tFactory.getFeature(DOMSource.FEATURE) && tFactory.getFeature(DOMResult.FEATURE))
    {
      //Instantiate a DocumentBuilderFactory.
      DocumentBuilderFactory dFactory = DocumentBuilderFactory.newInstance();

      // And setNamespaceAware, which is required when parsing xsl files
      dFactory.setNamespaceAware(true);
      
      //Use the DocumentBuilderFactory to create a DocumentBuilder.
      DocumentBuilder dBuilder = dFactory.newDocumentBuilder();
      
      //Use the DocumentBuilder to parse the XSL stylesheet.
      Document xslDoc = dBuilder.parse("birds.xsl");

      // Use the DOM Document to define a DOMSource object.
      DOMSource xslDomSource = new DOMSource(xslDoc);

      // Set the systemId: note this is actually a URL, not a local filename
      xslDomSource.setSystemId("birds.xsl");

      // Process the stylesheet DOMSource and generate a Transformer.
      Transformer transformer = tFactory.newTransformer(xslDomSource);

      //Use the DocumentBuilder to parse the XML input.
      Document xmlDoc = dBuilder.parse("birds.xml");
      
      // Use the DOM Document to define a DOMSource object.
      DOMSource xmlDomSource = new DOMSource(xmlDoc);
      
      // Set the base URI for the DOMSource so any relative URIs it contains can
      // be resolved.
      xmlDomSource.setSystemId("birds.xml");
      
      // Create an empty DOMResult for the Result.
      DOMResult domResult = new DOMResult();
  
  	  // Perform the transformation, placing the output in the DOMResult.
      transformer.transform(xmlDomSource, domResult);
	  
	    //Instantiate an Xalan XML serializer and use it to serialize the output DOM to System.out
	    // using a default output format.
      Serializer serializer = SerializerFactory.getSerializer
                                   (OutputProperties.getDefaultMethodProperties("xml"));
      serializer.setOutputStream(System.out);
      serializer.asDOMSerializer().serialize(domResult.getNode());
	}
    else
    {
      throw new org.xml.sax.SAXNotSupportedException("DOM node processing not supported!");
    }
  }
}
