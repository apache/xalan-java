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
package org.apache.xpath.functions;

import java.util.Vector;

import java.io.StringWriter;
import java.io.PrintWriter;
import java.io.IOException;

import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.dtm.DTMManager;
import org.apache.xml.dtm.XType;

import org.apache.xpath.NodeSetDTM;
import org.apache.xpath.functions.Function;
import org.apache.xpath.functions.Function2Args;
import org.apache.xpath.functions.WrongNumberArgsException;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XSequence;
import org.apache.xpath.objects.XNodeSequenceSingleton;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.SourceTreeManager;
import org.apache.xpath.Expression;
import org.apache.xalan.res.XSLMessages;
import org.apache.xpath.res.XPATHErrorResources;
import org.apache.xalan.transformer.TransformerImpl;

import org.xml.sax.InputSource;
import org.xml.sax.Locator;

import javax.xml.transform.TransformerException;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Source;

import org.apache.xml.utils.SAXSourceLocator;
import org.apache.xml.utils.XMLString;

/**
 * <meta name="usage" content="advanced"/>
 * Execute the Doc() function.
 *
 * When the document function has exactly one argument and the argument
 * is a node-set, then the result is the union, for each node in the
 * argument node-set, of the result of calling the document function with
 * the first argument being the string-value of the node, and the second
 * argument being a node-set with the node as its only member. When the
 * document function has two arguments and the first argument is a node-set,
 * then the result is the union, for each node in the argument node-set,
 * of the result of calling the document function with the first argument
 * being the string-value of the node, and with the second argument being
 * the second argument passed to the document function.
 */
public class FuncRoot extends FunctionDef1Arg
{

  /**
   * Execute the function.  The function must return
   * a valid object.
   * @param xctxt The current execution context.
   * @return A valid XObject.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
  {
    int context = DTM.NULL;
    if (m_arg0 == null)
    { 
      context = xctxt.getCurrentNode();
    }
    else
   {
      XSequence seq = m_arg0.execute(xctxt).xseq();
      XObject item = seq.next();
      if (item != null && item instanceof XNodeSequenceSingleton)
      {
        context = ((XNodeSequenceSingleton)item).getNodeHandle();
      }
      else
      {
         error(xctxt, XPATHErrorResources.ER_CONTEXT_NOT_NODE, null);  //"context does not have an owner document!");
         //context = xctxt.getCurrentNode(); 
      }        
    }
    
    DTM dtm = xctxt.getDTM(context);
    
    int docContext = dtm.getDocumentRoot(context);

    if (DTM.NULL == docContext)
    {
      error(xctxt, XPATHErrorResources.ER_CONTEXT_HAS_NO_OWNERDOC, null);  //"context does not have an owner document!");
    }
    
    return new XNodeSequenceSingleton(docContext, dtm);
  }
  
  

}
