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
// This file uses 4 space indents, no tabs.

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Properties;
import org.apache.xerces.parsers.DOMParser;
import org.apache.xpath.XPathAPI;
import org.apache.xml.utils.TreeWalker;
import org.apache.xml.utils.DOMBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.NodeIterator;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;

// Imported JAVA API for XML Parsing 1.0 classes
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException; 

// Imported Serializer classes
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import javax.xml.transform.dom.*;

/**
 *  Very basic utility for applying an XPath epxression to an xml file and printing information
 /  about the execution of the XPath object and the nodes it finds.
 *  Takes 2 arguments:
 *     (1) an xml filename
 *     (2) an XPath expression to apply to the file
 *  Examples:
 *     java ApplyXPath foo.xml /
 *     java ApplyXPath foo.xml /doc/name[1]/@last
 * @see XPathAPI
 */
public class ApplyXPath
{
  protected String filename = null;
  protected String xpath = null;

  /** Process input args and execute the XPath.  */
  public void doMain(String[] args)
    throws Exception
  {
    filename = args[0];
    xpath = args[1];

    if ((filename != null) && (filename.length() > 0)
        && (xpath != null) && (xpath.length() > 0))
    {
      // Tell that we're loading classes and parsing, so the time it 
      // takes to do this doesn't get confused with the time to do 
      // the actual query and serialization.
      System.out.println("Loading classes, parsing "+filename+", and setting up serializer");
      
      // Set up a DOM tree to query.
      InputSource in = new InputSource(new FileInputStream(filename));
      DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
      dfactory.setNamespaceAware(true);
      Document doc = dfactory.newDocumentBuilder().parse(in);
      
      // Set up an identity transformer to use as serializer.
      Transformer serializer = TransformerFactory.newInstance().newTransformer();
      serializer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

      // Use the simple XPath API to select a nodeIterator.
      System.out.println("Querying DOM using "+xpath);
      NodeIterator nl = XPathAPI.selectNodeIterator(doc, xpath);

      // Serialize the found nodes to System.out.
      System.out.println("<output>");
                  
      Node n;
      while ((n = nl.nextNode())!= null)
      {         
	if (isTextNode(n)) {
	    // DOM may have more than one node corresponding to a 
	    // single XPath text node.  Coalesce all contiguous text nodes
	    // at this level
	    StringBuffer sb = new StringBuffer(n.getNodeValue());
	    for (
	      Node nn = n.getNextSibling(); 
	      isTextNode(nn);
	      nn = nn.getNextSibling()
	    ) {
	      sb.append(nn.getNodeValue());
	    }
	    System.out.print(sb);
	}
	else {
	  serializer.transform(new DOMSource(n), new StreamResult(System.out));
	}
        System.out.println();
      }
      System.out.println("</output>");
    }
    else
    {
      System.out.println("Bad input args: " + filename + ", " + xpath);
    }
  }
  
  /** Decide if the node is text, and so must be handled specially */
  static boolean isTextNode(Node n) {
    if (n == null)
      return false;
    short nodeType = n.getNodeType();
    return nodeType == Node.CDATA_SECTION_NODE || nodeType == Node.TEXT_NODE;
  }

  /** Main method to run from the command line.    */
  public static void main (String[] args)
    throws Exception
  {
    if (args.length != 2)
    {
      System.out.println("java ApplyXPath filename.xml xpath\n"
                         + "Reads filename.xml and applies the xpath; prints the nodelist found.");
      return;
    }
        
    ApplyXPath app = new ApplyXPath();
    app.doMain(args);
  }	
  
} // end of class ApplyXPath

