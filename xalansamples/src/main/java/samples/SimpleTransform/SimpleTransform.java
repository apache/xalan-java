/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the  "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * $Id$
 */
package samples.SimpleTransform;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 *  Use the TraX interface to perform a transformation in the simplest manner possible
 *  (3 statements).
 */
public class SimpleTransform
{
  public static void main(String[] args)
    throws TransformerException, TransformerConfigurationException,
       FileNotFoundException, IOException
  {

    // Grab the Name of the Stylesheet from the command line
    String stylesheet="birds.xsl";
    String input = "birds.xml";
    String output = "birds.out";
    // XSLTC as fallback
    String factoryClassName = System.getProperty("javax.xml.transform.TransformerFactory","org.apache.xalan.xsltc.trax.TransformerFactoryImpl");

    if (args.length == 0){
       System.out.println("You may provide the path and name to a stylesheet to process birds.xml into birds.out");
       System.out.println("Defaulting to "+stylesheet);
    }
    if (args.length >= 1){
      stylesheet = args[0];
      System.out.println("Stylesheet to use: "+stylesheet);
    }
    if (args.length >= 2){
      input = args[1];
      System.out.println("Stylesheet to use: "+stylesheet+" input: "+input);
    }
    if (args.length >= 3){
      output = args[2];
      System.out.println("Stylesheet to use: "+stylesheet+" input: "+input+" output: "+output);
    }
    if(args.length == 4){
      // Known options:
      // - org.apache.xalan.processor.TransformerFactoryImpl - The Xalan interpreted processor
      // - org.apache.xalan.xsltc.trax.SmartTransformerFactoryImpl - The XSLTC processor
      // - com.ibm.xtq.xslt.jaxp.compiler.TransformerFactoryImpl - The IBM XL-TXE processor in IBM Java 8
      factoryClassName = args[3];
      System.out.println("Stylesheet to use: "+stylesheet+" input: "+input+" output: "+output+" factory: "+factoryClassName);
    }
    if(args.length > 4){
      System.out.println("Only up to four arguments allowed (stylesheet, input, output, transformerFactory)");
      return;
    }

    // In *normal* use, we want to do this:
    // Use the static TransformerFactory.newInstance() method to instantiate
    // a TransformerFactory. The javax.xml.transform.TransformerFactory
    // system property setting determines the actual class to instantiate --
    // for Xalan, org.apache.xalan.transformer.TransformerImpl.
    /* TransformerFactory tFactory = TransformerFactory.newInstance(); */
    // But to allow specifying the factory on the command line to support
    // testing different functionality we're going to use the forName approach
    final TransformerFactory tFactory;
    try{
      Class<?> factoryClass = Class.forName(factoryClassName);
      tFactory = (TransformerFactory) factoryClass.newInstance();
    }
    catch(java.lang.ClassNotFoundException | java.lang.InstantiationException | java.lang.IllegalAccessException e){
      e.printStackTrace();
      return;
    }

    System.out.println("Transforming "+input+" with stylesheet "+ stylesheet);
    System.out.println("  Xalan Version: " + (new org.apache.xalan.Version()).getVersion());

    //  processes the stylesheet into a compiled Templates object.
    Transformer transformer = tFactory.newTransformer(new StreamSource(stylesheet));

    // Use the Transformer to apply the associated Templates object to
    // an XML document and write the output to a file
    transformer.transform(new StreamSource(input),
              new StreamResult(new FileOutputStream(output)));

    System.out.println("************* The result is in "+output+" *************");
  }
}
