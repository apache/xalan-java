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
import org.apache.xerces.parsers.DOMParser;
import org.apache.xpath.XPathAPI;
//import org.apache.xpath.xml.FormatterToXML;
import org.apache.xalan.utils.TreeWalker;
import org.apache.xalan.utils.DOMBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.NodeIterator;
//import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;

// Imported JAVA API for XML Parsing 1.0 classes
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException; 

// Imported Serializer classes
import org.apache.serialize.OutputFormat;
import org.apache.serialize.Serializer;
import org.apache.serialize.SerializerFactory;
import org.apache.xml.serialize.transition.XMLSerializer;
import org.apache.xml.serialize.transition.TextSerializer;

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
  {
    filename = args[0];
    xpath = args[1];

    if ((filename != null) && (filename.length() > 0)
        && (xpath != null) && (xpath.length() > 0))
    {
      InputSource in;
      try
      {
        in = new InputSource(new FileInputStream(filename));
      }
      catch (FileNotFoundException fnf)
      {
        System.err.println("FileInputStream of " + filename + " threw: " + fnf.toString());
        fnf.printStackTrace();
        return;
      }
      Document doc = null;
	  Element root = null;
      try
      {
  	    DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder docBuilder = dfactory.newDocumentBuilder();
        doc = docBuilder.parse(in);
		root = doc.getDocumentElement();
      }
	    catch (ParserConfigurationException pce)
      {
	      pce.printStackTrace();
      }
      catch(Exception e1)
      {
        System.err.println("Parsing " + filename + " threw: " + e1.toString());
        e1.printStackTrace();
        return;
      }
      NodeIterator nl = null;
      try
      {
        // Use the simple XPath API to select a nodeIterator.
        nl = XPathAPI.selectNodeIterator(doc, xpath);
	  }
      catch (Exception e2)
      {
        System.err.println("selectNodeIterator threw: " + e2.toString() + " perhaps your xpath didn't select any nodes");
        e2.printStackTrace();
        return;
      }		 
	  Node n = null;
	  try
	  {
		while ((n = nl.nextNode())!= null)
		{
		  // XMLSerializer doesn't fully work!		  
		  XMLSerializer xmlser = new XMLSerializer(System.out, new OutputFormat());
		  if (n.getNodeType() == n.DOCUMENT_NODE)			  
			xmlser.serialize((Document)n);
          else if (n.getNodeType() == n.ELEMENT_NODE)
			xmlser.serialize((Element)n);
	      else
			 System.out.println
				  ("XMLSerializer cannot serialize: " + n.getNodeName()+ " -- " + n.getNodeValue());
//	How about (doesn't yet work, but ??) --	  
/*		  TreeWalker tw = new TreeWalker(new XMLSerializer(System.out, new OutputFormat()));
	          tw.traverse(n); 
*/
		}
      }
	  catch (Exception e3)
      {
        e3.printStackTrace();
        return;
      }
    }
    else
    {
      System.out.println("Bad input args: " + filename + ", " + xpath);
    }
  }
  
  /** Main method to run from the command line.    */
  public static void main (String[] args)
  {
    if (args.length != 2)
    {
      System.out.println("java ApplyXPath filename.xml xpath\n"
                         + "Reads filename.xml and applies the xpath; prints the nodelist found.");
      return;
    }
    ApplyXPath app = new ApplyXPath();
    System.out.println("<output>");
    app.doMain(args);
    System.out.println("</output>");
  }	
  
} // end of class ApplyXPath

