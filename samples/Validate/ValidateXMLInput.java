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
 * 4. The names "XSLT4J" and "Apache Software Foundation" must
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
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;

import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;

import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Validate the XML input by using SAXParserFactory to turn on namespace awareness and 
 * validation, and a SAX XMLReader to parse the input and report problems to an error 
 * handler.
 * 
 * This sample uses birds.xml with an internal DOCTYPE declaration. As shipped, birds.xml
 * contains an element that violates the declared document type.
 */
public class ValidateXMLInput
{
  
  public static void main(String[] args) 
    throws Exception
  {
    ValidateXMLInput v = new ValidateXMLInput();
    v.validate();
  }

  void validate()
    throws Exception
   {
     // Since we're going to use a SAX feature, the transformer must support 
    // input in the form of a SAXSource.
    TransformerFactory tfactory = TransformerFactory.newInstance();
    if(tfactory.getFeature(SAXSource.FEATURE))
    {
      // Standard way of creating an XMLReader in JAXP 1.1.
      SAXParserFactory pfactory= SAXParserFactory.newInstance();
      pfactory.setNamespaceAware(true); // Very important!
      // Turn on validation.
      pfactory.setValidating(true);
      // Get an XMLReader.
      XMLReader reader = pfactory.newSAXParser().getXMLReader();
  
      // Instantiate an error handler (see the Handler inner class below) that will report any
      // errors or warnings that occur as the XMLReader is parsing the XML input.
      Handler handler = new Handler();
      reader.setErrorHandler(handler);
  
      // Standard way of creating a transformer from a URL.
      Transformer t = tfactory.newTransformer(
        new StreamSource("birds.xsl"));
      
      // Specify a SAXSource that takes both an XMLReader and a URL.
      SAXSource source = new SAXSource(reader,
        new InputSource("birds.xml"));
      
      // Transform to a file.
      try
      {
        t.transform(source, new StreamResult("birds.out"));
      }
      catch (TransformerException te)
      {
        // The TransformerException wraps someting other than a SAXParseException
        // warning or error, either of which should be "caught" by the Handler.
        System.out.println("Not a SAXParseException warning or error: " + te.getMessage());
      }
                                  
      System.out.println("=====Done=====");
    }
    else
      System.out.println("tfactory does not support SAX features!");
  }

  // Catch any errors or warnings from the XMLReader.
  class Handler extends DefaultHandler
  {
    public void warning (SAXParseException spe)
	     throws SAXException
    {
      System.out.println("SAXParseException warning: " + spe.getMessage());
    }    

    public void error (SAXParseException spe)
    	throws SAXException
    {
      System.out.println("SAXParseException error: " + spe.getMessage());
    }     
  }
}
