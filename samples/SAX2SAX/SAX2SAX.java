
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

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.Templates;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TemplatesHandler;
import javax.xml.transform.sax.TransformerHandler;              

import org.xml.sax.XMLReader;
import org.xml.sax.ContentHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLReaderFactory;

import org.apache.xalan.serialize.SerializerFactory;
import org.apache.xalan.serialize.Serializer;
import org.apache.xalan.templates.OutputProperties;

import java.io.FileOutputStream;
import java.io.IOException;


public class SAX2SAX
{
  public static void main(String[] args)
	throws TransformerException, TransformerConfigurationException, 
         SAXException, IOException	   
	{

    // Instantiate a TransformerFactory.
  	TransformerFactory tFactory = TransformerFactory.newInstance();
    // Determine whether the TransformerFactory supports The use of SAXSource 
    // and SAXResult
    if (tFactory.getFeature(SAXSource.FEATURE) && tFactory.getFeature(SAXResult.FEATURE))
    { 
      // Cast the TransformerFactory.
      SAXTransformerFactory saxTFactory = ((SAXTransformerFactory) tFactory);
      // Create a ContentHandler to handle parsing of the stylesheet.
      TemplatesHandler templatesHandler = saxTFactory.newTemplatesHandler();

      // Create an XMLReader and set its ContentHandler.
      XMLReader reader = XMLReaderFactory.createXMLReader();
      reader.setContentHandler(templatesHandler);
    
      // Parse the stylesheet.                       
      reader.parse("birds.xsl");

      //Get the Templates object from the ContentHandler.
      Templates templates = templatesHandler.getTemplates();
      // Create a ContentHandler to handle parsing of the XML source.  
      TransformerHandler handler 
        = saxTFactory.newTransformerHandler(templates);
      // Reset the XMLReader's ContentHandler.
      reader.setContentHandler(handler);  

      // Set the ContentHandler to also function as a LexicalHandler, which
      // includes "lexical" events (e.g., comments and CDATA). 
      reader.setProperty("http://xml.org/sax/properties/lexical-handler", handler);
      
   	  FileOutputStream fos = new FileOutputStream("birds.out");
      
      Serializer serializer = SerializerFactory.getSerializer
                              (OutputProperties.getDefaultMethodProperties("xml"));
      serializer.setOutputStream(fos);
   
      
      // Set the result handling to be a serialization to the file output stream.
      Result result = new SAXResult(serializer.asContentHandler());
      handler.setResult(result);
      
      // Parse the XML input document.
      reader.parse("birds.xml");
      
    	System.out.println("************* The result is in birds.out *************");	
    }	
    else
      System.out.println("The TransformerFactory does not support SAX input and SAX output");
  }
}