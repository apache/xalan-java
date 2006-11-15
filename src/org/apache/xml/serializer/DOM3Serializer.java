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
 * $Id:  $
 */

package org.apache.xml.serializer;

import java.io.IOException;

import org.w3c.dom.DOMErrorHandler;
import org.w3c.dom.Node;
import org.w3c.dom.ls.LSSerializerFilter;

/**
 * Interface for a DOM serializer capable of serializing DOMs as specified in 
 * the DOM Level 3 Save Recommendation. This interface is not intended to be used
 * by an end user, but rather by an XML parser that is implementing the DOM 
 * Level 3 Load and Save APIs.
 * <p>
 * 
 * See the DOM Level 3 Load and Save interface at <a href="http://www.w3.org/TR/2004/REC-DOM-Level-3-LS-20040407/load-save.html#LS-LSSerializer">LSSeializer</a>.
 * 
 * For a list of configuration parameters for DOM Level 3 see <a href="http://www.w3.org/TR/2004/REC-DOM-Level-3-Core-20040407/core.html#DOMConfiguration">DOMConfiguration</a>.
 * For additional configuration parameters available with the DOM Level 3 Load and Save API LSSerializer see
 * <a href="http://www.w3.org/TR/2004/REC-DOM-Level-3-LS-20040407/load-save.html#LS-LSSerializer-config">LSerializer config</a>.
 * <p>
 * The following example uses a DOM3Serializer indirectly, through an an XML
 * parser that uses this class as part of its implementation of the DOM Level 3
 * Load and Save APIs, and is the prefered way to serialize with DOM Level 3 APIs.
 * <p>
 * Example:
 * <pre>
 *    public class TestDOM3 {
 *
 *    public static void main(String args[]) throws Exception {
 *        // Get document to serialize
 *        TestDOM3 test = new TestDOM3();
 *        
 *        // Serialize using standard DOM Level 3 Load/Save APIs        
 *        System.out.println(test.testDOM3LS());
 *    }
 *
 *    public org.w3c.dom.Document getDocument() throws Exception {
 *        // Create a simple DOM Document.
 *        javax.xml.parsers.DocumentBuilderFactory factory = 
 *            javax.xml.parsers.DocumentBuilderFactory.newInstance();
 *        javax.xml.parsers.DocumentBuilder builder = factory.newDocumentBuilder();
 *        byte[] bytes = "<parent><child/></parent>".getBytes();
 *        java.io.InputStream is = new java.io.ByteArrayInputStream(bytes);
 *        org.w3c.dom.Document doc = builder.parse(is);
 *        return doc;
 *    }
 *    
 *    //
 *    // This method uses standard DOM Level 3 Load Save APIs:
 *    //   org.w3c.dom.bootstrap.DOMImplementationRegistry
 *    //   org.w3c.dom.ls.DOMImplementationLS
 *    //   org.w3c.dom.ls.DOMImplementationLS
 *    //   org.w3c.dom.ls.LSSerializer
 *    //   org.w3c.dom.DOMConfiguration
 *    //   
 *    // The only thing non-standard in this method is the value set for the
 *    // name of the class implementing the DOM Level 3 Load Save APIs,
 *    // which in this case is:
 *    //   org.apache.xerces.dom.DOMImplementationSourceImpl
 *    //
 *
 *    public String testDOM3LS() throws Exception {
 *        
 *        // Get a simple DOM Document that will be serialized.
 *        org.w3c.dom.Document docToSerialize = getDocument();
 *
 *        // Get a factory (DOMImplementationLS) for creating a Load and Save object.
 *        System.setProperty(org.w3c.dom.bootstrap.DOMImplementationRegistry.PROPERTY,
 *                "org.apache.xerces.dom.DOMImplementationSourceImpl");
 *        org.w3c.dom.ls.DOMImplementationLS impl = 
 *            (org.w3c.dom.ls.DOMImplementationLS) 
 *            org.w3c.dom.bootstrap.DOMImplementationRegistry.newInstance().getDOMImplementation("LS");
 *
 *        // Use the factory to create an object (LSSerializer) used to 
 *        // write out or save the document.
 *        org.w3c.dom.ls.LSSerializer writer = impl.createLSSerializer();
 *        org.w3c.dom.DOMConfiguration config = writer.getDomConfig();
 *        config.setParameter("format-pretty-print", Boolean.TRUE);
 *        
 *        // Use the LSSerializer to write out or serialize the document to a String.
 *        String serializedXML = writer.writeToString(docToSerialize);
 *        return serializedXML;
 *    }
 *    
 *    }  // end of class TestDOM3
 * </pre>
 * <p>
 * The DOM3Serializer is a facet of a serializer and is obtained from the
 * asDOM3Serializer() method of the org.apache.xml.serializer.Serializer interface. 
 * A serializer may or may not support a level 3 DOM serializer, if it does not then the 
 * return value from asDOM3Serializer() is null.
 * <p>
 * Using the DOM3Serializer interface directly is discouraged for use by end users.
 * <p>
 * Example:
 * <pre>
 *   public class TestDOM3 {
 *
 *   public static void main(String args[]) throws Exception {
 *       // Get document to serialize
 *       TestDOM3 test = new TestDOM3();
 *       
 *       // Serialize using implementation specific
 *       // non-standard serialization
 *       // APIs (use standard DOM Level 3 if you can) 
 *       System.out.println(test.testDOM3Xalan());
 *   }
 *   
 *   //
 *   // This method uses non-standard, implementation specific
 *   // classes:
 *   //   org.apache.xml.serializer.OutputPropertiesFactory
 *   //   org.apache.xml.serializer.SerializerFactory
 *   //   org.apache.xml.serializer.Serializer
 *   //   org.apache.xml.serializer.DOM3Serializer
 *   // to serialize a Document.
 *   // 
 *   // These classes are intended to be used by an XML
 *   // parser to implement Load Save support for DOM Level 3.
 *   // They are not intended for use by an end user.
 *   //
 *   public String testDOM3Xalan() throws Exception {
 *       // Get a simple DOM Document that will be serialized.
 *       org.w3c.dom.Document doc = getDocument();
 *    
 *       // Get the default properties for serializing XML
 *       java.util.Properties configProps = org.apache.xml.serializer.OutputPropertiesFactory
 *               .getDefaultMethodProperties("xml");
 *       
 *       // Get a Serializer from the non-standard factory
 *       org.apache.xml.serializer.Serializer ser = org.apache.xml.serializer.SerializerFactory
 *               .getSerializer(configProps);
 *       java.io.StringWriter sw = new java.io.StringWriter();
 *       // Set the writer where the Serializer should write its output to.
 *       ser.setWriter(sw);
 *    
 *       // Configure the Serializer with additional non-standard properties
 *       // for DOM Level 3 serialization:
 *       java.util.Properties props = new java.util.Properties();
 *       props.setProperty(
 *               "{http://www.w3.org/TR/DOM-Level-3-LS}format-pretty-print",
 *               "explicit:yes");
 *       ser.setOutputFormat(props);
 *    
 *       // Cast the Serializer to a DOM3Serializer and use it to serialize the Document.
 *       org.apache.xml.serializer.DOM3Serializer dser = (org.apache.xml.serializer.DOM3Serializer) ser
 *               .asDOM3Serializer();
 *       dser.serializeDOM3(doc);
 *       String serializedXML = sw.toString();
 *       return serializedXML;
 *   }
 *   
 *   }  // end of class TestDOM3
 *   
 * </pre>
 * <p>
 * If a DOM3Serializer is used directly (not through the DOM Level 3 API LSSerializer,
 * then the configuration properties for DOM Level 3 are supported:
 * <ul>
 * <li> "cdata-sections"
 * <li> "comments"
 * <li> "element-content-whitespace"
 * <li> "entities"
 * <li> "infoset"
 * <li> "namespaces"
 * <li> "namespace-declarations"
 * <li> "split-cdata-sections"
 * <li> "well-formed"
 * </ul>
 * <p>
 * Also the following propertes for DOM Level 3 Load and Save are supported:
 * <ul>
 * <li> "{http://www.w3.org/TR/DOM-Level-3-LS}format-pretty-print" 
 * <li> "format-pretty-print"
 * <li> "xml-declaration"
 * </ul>
 * 
 * @see <a href="http://www.w3.org/TR/2004/REC-DOM-Level-3-Core-20040407/core.html#DOMConfiguration">DOMConfiguration</a>
 * @see <a href="http://www.w3.org/TR/2004/REC-DOM-Level-3-LS-20040407/load-save.html#LS-LSSerializer-config">LSSerializer</a>
 * @see org.apache.xml.serializer.Serializer
 * @see org.apache.xml.serializer.DOMSerializer
 * 
 * @xsl.usage general
 *
 */
public interface DOM3Serializer {
    /**
     * Serializes the Level 3 DOM node. Throws an exception only if an I/O
     * exception occured while serializing.
     * 
     * This interface is a public API.
     *
     * @param node the Level 3 DOM node to serialize
     * @throws IOException if an I/O exception occured while serializing
     */
    public void serializeDOM3(Node node) throws IOException;

    /**
     * Sets a DOMErrorHandler on the DOM Level 3 Serializer.
     * 
     * This interface is a public API.
     *
     * @param handler the Level 3 DOMErrorHandler
     */
    public void setErrorHandler(DOMErrorHandler handler);

    /**
     * Returns a DOMErrorHandler set on the DOM Level 3 Serializer.
     * 
     * This interface is a public API.
     *
     * @return A Level 3 DOMErrorHandler
     */
    public DOMErrorHandler getErrorHandler();

    /**
     * Sets a LSSerializerFilter on the DOM Level 3 Serializer to filter nodes
     * during serialization.
     * 
     * This interface is a public API.
     *
     * @param filter the Level 3 LSSerializerFilter
     */
    public void setNodeFilter(LSSerializerFilter filter);

    /**
     * Returns a LSSerializerFilter set on the DOM Level 3 Serializer to filter nodes
     * during serialization.
     * 
     * This interface is a public API.
     *
     * @return The Level 3 LSSerializerFilter
     */
    public LSSerializerFilter getNodeFilter();

    /**
     * Sets the new line character to be used during serialization
     * @param newLine A character array corresponding to the new line character to be used.
     */
    public void setNewLine(char[] newLine);
}
