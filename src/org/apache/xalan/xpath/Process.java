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
package org.apache.xalan.xpath;

import java.util.*;
import org.w3c.dom.*;
import org.w3c.dom.traversal.NodeIterator;
import java.lang.reflect.*;
import org.apache.xalan.xpath.res.XPATHErrorResources;
import org.apache.xalan.res.XSLMessages;

/**
 * Provides command-line interface to the XPath processor
 */
public class Process
{
  /**
   * Print argument options.
   */
  protected static void printArgOptions(XPATHErrorResources resbundle)
  {
    System.out.println(resbundle.getString("xpath_option"));
    System.out.println(resbundle.getString("optionIN"));
    System.out.println(resbundle.getString("optionSelect"));
    System.out.println(resbundle.getString("optionMatch"));
    System.out.println(resbundle.getString("optionAnyExpr"));
  }

  /**
   * Command line interface to the XPath processor.
   * <pre>
   *    xpath
   *    [xml doc]
   * </pre>
   */
  public static void main( String argv[] )
    throws org.xml.sax.SAXException
  {
	XPATHErrorResources resbundle = (XPATHErrorResources)(XSLMessages.loadResourceBundle(XPATHErrorResources.ERROR_RESOURCES));
    if(argv.length > 1)
    {
      XPathContext callbacks = null;
      String parserLiaisonClassName = "org.apache.xalan.xpath.DOM2Helper";
      try
      {
        Class parserLiaisonClass = Class.forName(parserLiaisonClassName);

        Constructor parserLiaisonCtor = parserLiaisonClass.getConstructor(null);
        callbacks
          = (XPathContext)parserLiaisonCtor.newInstance(null);
        // XPathContext callbacks = new org.apache.xalan.xpath.DOM2Helper();
      }
      catch(Exception e)
      {
		  System.err.println(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_COULDNOT_CREATE_XMLPROCESSORLIAISON, new Object [] {parserLiaisonClassName})); //"Could not create XML Processor Liaison: "+parserLiaisonClassName);
        return;
      }
      XPathParser processor = new XPathParser();
      XPath xpath = new XPath();
      String inputURI = null;
      String path = null;
      String match = null;
      for (int i = 0;  i < argv.length;  i ++)
      {
        if("-in".equalsIgnoreCase(argv[i]))
        {
          i++;
          inputURI = argv[i];
        }
        else if("-select".equalsIgnoreCase(argv[i]))
        {
          i++;
          path = argv[i];
        }
        else if("-match".equalsIgnoreCase(argv[i]))
        {
          i++;
          match = argv[i];
        }
      }

      if(null == path)
      {
        System.out.println(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_DIDNOT_FIND_XPATH_SELECT_EXP, null)); //"Error! Did not find xpath select expression (-select).");
        return;
      }

      Document doc;
      if(null != inputURI)
      {
        System.out.println("Parsing XML...");
        java.net.URL url = callbacks.getURLFromString(inputURI, null);
        // doc = callbacks.parseXML(url, null, null);
        doc = null; // TBD:
      }
      else
      {
        doc = callbacks.getDOMHelper().getDOMFactory();
      }
      processor.initXPath(xpath, path, null);

      XPath matchPat = null;
      if(null != match)
      {
        matchPat = new XPath();
        processor.initMatchPattern(matchPat, match, null);
      }

      // TODO: Do something about the prefix resolver.
      XObject result = xpath.execute(callbacks, doc, null);

      if(result.getType() == XObject.CLASS_NODESET)
      {
        System.out.println("<results>");
        NodeIterator nl = result.nodeset();
        Node n;
        while(null != (n = nl.nextNode()))
        {
          System.out.print("  <node name='"+n.getNodeName()+"'");
          if(null != matchPat)
          {
            System.out.print(" match-value='"+matchPat.getMatchScore(callbacks, n)+"'");
          }
          if(Node.ATTRIBUTE_NODE != n.getNodeType())
          {
            Document owner = (Node.DOCUMENT_NODE == n.getNodeType()) ?
                           (Document)n : n.getOwnerDocument();
            String docURI = callbacks.getSourceTreeManager().findURIFromDoc(owner);
            System.out.print(" doc-ref='"+docURI+"'");
          }
          System.out.println("/>");
        }
        System.out.println("</results>");
      }
      else
      {
        System.out.println("XPath Result: \n"+result.str());
      }
    }
    else if(argv.length == 1)
    {
      XPathDumper.diagnoseXPathString(argv[0]);
    }
    else
    {
      printArgOptions(resbundle);
    }
  }
}
