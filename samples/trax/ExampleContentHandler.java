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
import org.xml.sax.ContentHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.Locator;

public class ExampleContentHandler implements ContentHandler
{
  public void setDocumentLocator (Locator locator)
  {
    System.out.println("setDocumentLocator");
  }


  public void startDocument ()
    throws SAXException
  {
    System.out.println("startDocument");
  }


  public void endDocument()
    throws SAXException
  {
    System.out.println("endDocument");
  }


  public void startPrefixMapping (String prefix, String uri)
    throws SAXException
  {
    System.out.println("startPrefixMapping: "+prefix+", "+uri);
  }


  public void endPrefixMapping (String prefix)
    throws SAXException
  {
    System.out.println("endPrefixMapping: "+prefix);
  }


  public void startElement (String namespaceURI, String localName,
                            String qName, Attributes atts)
    throws SAXException
  {
    System.out.print("startElement: "+namespaceURI+", "+namespaceURI+
                       ", "+qName);
    int n = atts.getLength();
    for(int i = 0; i < n; i++)
    {
      System.out.print(", "+atts.getQName(i));
    }
    System.out.println("");
  }


  public void endElement (String namespaceURI, String localName,
                          String qName)
    throws SAXException
  {
    System.out.println("endElement: "+namespaceURI+", "+namespaceURI+
                       ", "+qName);
  }


  public void characters (char ch[], int start, int length)
    throws SAXException
  {
    String s = new String(ch, start, (length > 30) ? 30 : length);
    if(length > 30)
      System.out.println("characters: \""+s+"\"...");
    else
      System.out.println("characters: \""+s+"\"");
  }


  public void ignorableWhitespace (char ch[], int start, int length)
    throws SAXException
  {
    System.out.println("ignorableWhitespace");
  }


  public void processingInstruction (String target, String data)
    throws SAXException
  {
    System.out.println("processingInstruction: "+target+", "+target);
  }


  public void skippedEntity (String name)
    throws SAXException
  {
    System.out.println("skippedEntity: "+name);
  }

}
