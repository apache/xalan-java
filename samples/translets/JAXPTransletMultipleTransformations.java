/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
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
 * originally based on software copyright (c) 2001, Sun
 * Microsystems., http://www.sun.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 * @author Donald Leslie
 *
 */
import java.util.Properties;
import java.io.FileOutputStream;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Templates;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerConfigurationException;
import java.io.IOException;
import java.io.FileNotFoundException;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;


/**
 * Using the TrAX/JAXP 1.1 interface to compile a translet and use it 
 * to perform multiple transformations. The translet implements 
 * the Templates interface. If you want to use the translet to perform a 
 * single transformation, see JAXPTransletOneTransformation.java.
 * 
 * 
 */
public class JAXPTransletMultipleTransformations 
{
 static void doTransform(Templates translet, String xmlInURI, String htmlOutURI)
        throws TransformerException, FileNotFoundException     
  {
    // For each transformation, instantiate a new Transformer, and perform
    // the transformation from a StreamSource to a StreamResult;
    Transformer transformer = translet.newTransformer();
    transformer.transform( new StreamSource(xmlInURI),
                           new StreamResult(new FileOutputStream(htmlOutURI)));
  }

  public static void main(String argv[])        
  { 
    // Set the TransformerFactory system property to generate and use translets.
    // Note: To make this sample more flexible, load properties from a properties file.
    // The setting for the Xalan Transformer is "org.apache.xalan.processor.TransformerFactoryImpl"
    String key = "javax.xml.transform.TransformerFactory";
    String value = "org.apache.xalan.xsltc.trax.TransformerFactoryImpl";
    Properties props = System.getProperties();
    props.put(key, value);
    
    System.setProperties(props);

    String xslInURI = "todo.xsl";
    
    try
    {
      // Instantiate the TransformerFactory, and use it along with a SteamSource
      // XSL stylesheet to create a translet as a Templates object.
      TransformerFactory tFactory = TransformerFactory.newInstance();
      Templates translet = tFactory.newTemplates(new StreamSource(xslInURI));
    
      // Perform each transformation
      doTransform(translet, "../../xsltc_todo.xml", "todo-xsltc.html");
      System.out.println("Produced todo-xsltc.html");
    
      doTransform(translet, "../../todo.xml", "todo-xalan.html");
      System.out.println("Produced todo-xalan.html");
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }    
  } 
}